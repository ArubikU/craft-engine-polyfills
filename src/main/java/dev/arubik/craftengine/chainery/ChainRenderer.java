package dev.arubik.craftengine.chainery;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

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

    /** Link thickness across the chain (X/Z). Slightly over 1 so adjacent links overlap sideways too. */
    private static final float LINK_WIDTH = 1.05f;

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
        int n = rope.particleCount();
        List<Player> viewers = viewersFor(world, rope);
        if (n < 2) {
            return;
        }
        // The rope is sub-divided SUBDIV× for collision; draw ONE link per block by striding those sub-particles.
        int stride = rope.renderStride();
        int segs = Math.max(1, (n - 1 + stride - 1) / stride);
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
        // Interaction hitboxes: one per link, kept in lock-step so the chain is clickable per segment.
        while (chain.hitboxes.size() < segs) {
            chain.hitboxes.add(new ChainInteraction(chain.id, chain.hitboxes.size()));
        }
        while (chain.hitboxes.size() > segs) {
            chain.hitboxes.remove(chain.hitboxes.size() - 1).remove(viewers);
        }
        for (int r = 0; r < segs; r++) {
            int i0 = Math.min(r * stride, n - 1);
            int i1 = Math.min((r + 1) * stride, n - 1);
            org.joml.Vector3d p0 = rope.particle(i0);
            org.joml.Vector3d p1 = rope.particle(i1);
            double mx = (p0.x + p1.x) / 2.0, my = (p0.y + p1.y) / 2.0, mz = (p0.z + p1.z) / 2.0;
            ChainBlockDisplay link = chain.links.get(r);
            link.setBlockState(base);
            link.setRotation(orient(p0, p1)); // full yaw+pitch: point the chain model along the block segment
            // Stretch the model ALONG the chain to exactly span this segment (tension = longer segments), so the
            // links stay continuous with no gaps whether the rope is taut/stretched or slack/compressed.
            float length = (float) Math.max(0.05, p0.distance(p1));
            link.setSize(LINK_WIDTH, length);
            link.render(viewers, mx, my, mz);
            chain.hitboxes.get(r).render(viewers, mx, my, mz);
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
        for (ChainInteraction hit : chain.hitboxes) {
            hit.remove(viewers);
        }
        chain.hitboxes.clear();
    }

    /** View radius (blocks) past a chain's span within which a player is sent its links. */
    private static final double VIEW_RANGE = 96.0;

    /**
     * CE players near the chain — every Bukkit player in the world within {@link #VIEW_RANGE} of the span,
     * wrapped to a CE player. Uses live nearby players (not chunk-tracking) so a player that just logged in or
     * walked into range reliably gets the links spawned on the next tick (fixes "no se renderiza al entrar").
     */
    private static List<Player> viewersFor(World world, ChainRope rope) {
        List<Player> out = new ArrayList<>();
        int n = rope.particleCount();
        if (n == 0) {
            return out;
        }
        org.joml.Vector3d a = rope.particle(0);
        org.joml.Vector3d b = rope.particle(n - 1);
        double mx = (a.x + b.x) / 2.0, my = (a.y + b.y) / 2.0, mz = (a.z + b.z) / 2.0;
        double reach = 0.5 * a.distance(b) + VIEW_RANGE;
        double reach2 = reach * reach;
        for (org.bukkit.entity.Player bp : world.getPlayers()) {
            org.bukkit.Location l = bp.getLocation();
            double dx = l.getX() - mx, dy = l.getY() - my, dz = l.getZ() - mz;
            if (dx * dx + dy * dy + dz * dz > reach2) {
                continue;
            }
            try {
                out.add(net.momirealms.craftengine.bukkit.api.BukkitAdaptor.adapt(bp));
            } catch (Throwable ignored) {
                // skip a player that can't be wrapped this tick
            }
        }
        return out;
    }

    /** No-op kept for the enable-time call: packet entities never persist as real orphans to sweep. */
    public static void sweepOrphans() {
        // Packet-only links vanish on disconnect/reload; nothing to clean.
    }
}
