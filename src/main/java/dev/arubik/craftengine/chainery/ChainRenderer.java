package dev.arubik.craftengine.chainery;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import dev.arubik.craftengine.conveyor.ConveyorItemDisplay;
import dev.arubik.craftengine.util.CeWorlds;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.world.ChunkPos;

/**
 * PACKET-ONLY render of a {@link Chain} span (CHAINERY — user: "usa packets, no uses item displays reales,
 * por algo te dije que uses el phys system"). One fake {@code item_display} per rope segment (reusing
 * {@link ConveyorItemDisplay}, the same lightweight per-viewer packet entity the conveyor and contraption
 * swarms use — no real Bukkit entities), positioned at the segment midpoint and oriented along it, so the
 * links follow the verlet {@link ChainRope}'s real sag/ground-rest every tick.
 */
public final class ChainRenderer {

    private ChainRenderer() {
    }

    private static final float LINK_SCALE = 0.9f;

    /**
     * The NMS item a link renders as — built from the chain material's configured link id, so ANY CraftEngine
     * item (copper_chain, gold_chain, …) or vanilla item works, showing that item's own model. Falls back to a
     * vanilla chain if the id resolves to nothing.
     */
    private static Object linkNmsItem(String linkId) {
        org.bukkit.inventory.ItemStack bukkit = ChainEngine.linkItemStack(linkId);
        if (bukkit == null) {
            org.bukkit.Material m = org.bukkit.Material.matchMaterial("CHAIN");
            bukkit = new org.bukkit.inventory.ItemStack(m != null ? m : org.bukkit.Material.IRON_INGOT);
        }
        return org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
    }

    /**
     * Draws {@code chain} from its verlet {@link ChainRope}: one packet link per segment at the segment
     * midpoint, oriented along it. Spawns/despawns links to match the segment count and syncs their position
     * to whoever is tracking the chain's chunks.
     */
    public static void syncRope(Chain chain, World world, ChainRope rope) {
        int segs = Math.max(0, rope.particleCount() - 1);
        List<Player> viewers = viewersFor(world, rope);
        if (segs == 0) {
            return;
        }
        while (chain.links.size() < segs) {
            ConveyorItemDisplay link = new ConveyorItemDisplay();
            link.setNmsItem(linkNmsItem(chain.material.linkItem()));
            link.setScale(LINK_SCALE);
            chain.links.add(link);
        }
        while (chain.links.size() > segs) {
            ConveyorItemDisplay link = chain.links.remove(chain.links.size() - 1);
            for (Player p : viewers) {
                link.despawn(p);
            }
            link.clearShown();
        }
        for (int i = 0; i < segs; i++) {
            org.joml.Vector3d p0 = rope.particle(i);
            org.joml.Vector3d p1 = rope.particle(i + 1);
            ConveyorItemDisplay link = chain.links.get(i);
            link.setRotation(orient(p0, p1));
            link.render(viewers, (p0.x + p1.x) / 2.0, (p0.y + p1.y) / 2.0, (p0.z + p1.z) / 2.0,
                    link.consumeRotationDirty());
        }
    }

    /** Despawns every packet link of this chain for whoever can currently see it, and clears the handles. */
    public static void despawn(Chain chain, World world) {
        List<Player> viewers = world == null ? List.of() : viewersFor(world, chain.rope);
        for (ConveyorItemDisplay link : chain.links) {
            for (Player p : viewers) {
                link.despawn(p);
            }
            link.clearShown();
        }
        chain.links.clear();
    }

    /** CE players tracking either endpoint's chunk (union) — the viewers a chain's links are sent to. */
    private static List<Player> viewersFor(World world, ChainRope rope) {
        List<Player> out = new ArrayList<>();
        try {
            int n = rope.particleCount();
            if (n == 0) {
                return out;
            }
            org.joml.Vector3d a = rope.particle(0);
            org.joml.Vector3d b = rope.particle(n - 1);
            var ce = CeWorlds.of(world);
            addTracked(out, ce.getTrackedBy(new ChunkPos((int) Math.floor(a.x) >> 4, (int) Math.floor(a.z) >> 4)));
            int bcx = (int) Math.floor(b.x) >> 4, bcz = (int) Math.floor(b.z) >> 4;
            if (bcx != ((int) Math.floor(a.x) >> 4) || bcz != ((int) Math.floor(a.z) >> 4)) {
                addTracked(out, ce.getTrackedBy(new ChunkPos(bcx, bcz)));
            }
        } catch (Throwable ignored) {
            // no viewers this tick rather than an exception
        }
        return out;
    }

    private static void addTracked(List<Player> out, List<Player> tracked) {
        if (tracked == null) {
            return;
        }
        for (Player p : tracked) {
            if (!out.contains(p)) {
                out.add(p);
            }
        }
    }

    /** A rotation mapping the item model's up axis onto the segment direction. */
    private static Quaternionf orient(org.joml.Vector3d a, org.joml.Vector3d b) {
        Vector3f dir = new Vector3f((float) (b.x - a.x), (float) (b.y - a.y), (float) (b.z - a.z));
        if (dir.lengthSquared() < 1.0e-6f) {
            dir.set(0, 1, 0);
        }
        dir.normalize();
        return new Quaternionf().rotationTo(new Vector3f(0, 1, 0), dir);
    }

    /** No-op kept for the enable-time call: packet entities never persist as real orphans to sweep. */
    public static void sweepOrphans() {
        // Packet-only links vanish on disconnect/reload; nothing to clean.
    }
}
