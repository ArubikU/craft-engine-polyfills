package dev.arubik.craftengine.chainery;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import dev.arubik.craftengine.util.CeWorlds;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
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

    /**
     * The BLOCK state a link renders as — resolved from the chain material's configured link id, so ANY id
     * works and shows its real BLOCK model:
     * <ul>
     *   <li>a CraftEngine block id → its mapped vanilla state (which the pack renders as the custom model);</li>
     *   <li>else a vanilla item/block id → that block's default state (e.g. minecraft:iron_chain).</li>
     * </ul>
     * Falls back to a vanilla chain. The base state is resolved once per render; each link is then oriented
     * along its segment by the display's LeftRotation ({@link #orient}), giving full yaw+pitch.
     */
    private static BlockState resolveBaseState(String linkId) {
        // 1) CraftEngine block (item id == block id is the common case; the id may also be a block id directly).
        try {
            var def = CraftEngineBlocks.byId(Key.of(linkId));
            if (def != null) {
                return (BlockState) def.defaultState().customBlockState().minecraftState();
            }
        } catch (Throwable ignored) {
        }
        // 2) Vanilla block by material key.
        try {
            org.bukkit.NamespacedKey mk = org.bukkit.NamespacedKey.fromString(linkId);
            org.bukkit.Material m = mk == null ? null : org.bukkit.Registry.MATERIAL.get(mk);
            if (m != null && m.isBlock()) {
                return ((org.bukkit.craftbukkit.block.data.CraftBlockData) m.createBlockData()).getState();
            }
        } catch (Throwable ignored) {
        }
        // 3) Fallback: vanilla chain.
        org.bukkit.Material chain = org.bukkit.Material.matchMaterial("CHAIN");
        if (chain != null) {
            return ((org.bukkit.craftbukkit.block.data.CraftBlockData) chain.createBlockData()).getState();
        }
        return null;
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
        BlockState base = resolveBaseState(chain.material.linkItem());
        while (chain.links.size() < segs) {
            chain.links.add(new ChainBlockDisplay());
        }
        while (chain.links.size() > segs) {
            ChainBlockDisplay link = chain.links.remove(chain.links.size() - 1);
            for (Player p : viewers) {
                link.despawn(p);
            }
            link.clearShown();
        }
        for (int i = 0; i < segs; i++) {
            org.joml.Vector3d p0 = rope.particle(i);
            org.joml.Vector3d p1 = rope.particle(i + 1);
            ChainBlockDisplay link = chain.links.get(i);
            link.setBlockState(base);
            link.setRotation(orient(p0, p1)); // full yaw+pitch: point the chain model along the segment
            link.render(viewers, (p0.x + p1.x) / 2.0, (p0.y + p1.y) / 2.0, (p0.z + p1.z) / 2.0);
        }
    }

    /** Quaternion mapping the chain model's vertical (up/Y) axis onto the segment direction — full 3D orient. */
    private static org.joml.Quaternionf orient(org.joml.Vector3d a, org.joml.Vector3d b) {
        org.joml.Vector3f dir = new org.joml.Vector3f((float) (b.x - a.x), (float) (b.y - a.y), (float) (b.z - a.z));
        if (dir.lengthSquared() < 1.0e-6f) {
            dir.set(0, 1, 0);
        }
        dir.normalize();
        return new org.joml.Quaternionf().rotationTo(new org.joml.Vector3f(0, 1, 0), dir);
    }

    /** Despawns every packet link of this chain for whoever can currently see it, and clears the handles. */
    public static void despawn(Chain chain, World world) {
        List<Player> viewers = world == null ? List.of() : viewersFor(world, chain.rope);
        for (ChainBlockDisplay link : chain.links) {
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

    /** No-op kept for the enable-time call: packet entities never persist as real orphans to sweep. */
    public static void sweepOrphans() {
        // Packet-only links vanish on disconnect/reload; nothing to clean.
    }
}
