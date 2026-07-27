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

    /** Interaction hitboxes per rendered link — 3× the links, spread along each segment to fill the gaps. */
    private static final int HITBOX_PER_LINK = 3;

    /** Max INTERACTION hitboxes spawned in one 1-block world cell per tick, across ALL chains — the density cap
     *  that stops N stacked chains between two blocks from spawning ~N×hitboxes of overlapping clickboxes. A
     *  click in that cell still resolves to whichever chain kept a box there (they overlap, so it's ambiguous
     *  anyway). Sized to let a lone chain's own {@link #HITBOX_PER_LINK} boxes through, but collapse a pile-up. */
    private static final int MAX_HITBOXES_PER_CELL = 3;

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
    public static BlockState resolveBaseState(String linkId) {
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
    public static void syncRope(Chain chain, World world, ChainRope rope,
            java.util.Map<Long, Integer> interactionBudget) {
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
        // Interaction hitboxes: HITBOX_PER_LINK per link (small boxes filling the spaces between links so the
        // whole chain is clickable). Each carries its link (block) index for the interaction event.
        int wantHits = segs * HITBOX_PER_LINK;
        while (chain.hitboxes.size() < wantHits) {
            chain.hitboxes.add(new ChainInteraction(chain.id, chain.hitboxes.size() / HITBOX_PER_LINK));
        }
        while (chain.hitboxes.size() > wantHits) {
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
            // LOD the interaction entities (user: "aplica LOD a los interaction entity de los chains"): a chain
            // link is only CLICKABLE from close up (interaction reach is a few blocks), so its hitboxes are sent
            // ONLY to viewers within INTERACT_RANGE of THIS segment — not to every one of the up-to-96-block visual
            // viewers, and not for the far segments of a long chain. A 32-link chain thus spawns ~a handful of
            // interaction entities around the player instead of 96×viewers of them.
            List<Player> near = interactViewersFor(viewers, mx, my, mz);
            for (int j = 0; j < HITBOX_PER_LINK; j++) {
                double t = (j + 0.5) / HITBOX_PER_LINK;
                double hx = p0.x + (p1.x - p0.x) * t;
                double hy = p0.y + (p1.y - p0.y) * t;
                double hz = p0.z + (p1.z - p0.z) * t;
                // Density LOD: a world cell only needs a few clickable boxes — once MAX_PER_CELL hitboxes (from
                // this or any other chain this tick) already fill this cell, cull the rest (render to nobody →
                // despawn). Only consumed when there's actually a near viewer to show it to.
                List<Player> show = near;
                if (!near.isEmpty()) {
                    long cell = net.minecraft.core.BlockPos.asLong((int) Math.floor(hx),
                            (int) Math.floor(hy), (int) Math.floor(hz));
                    int count = interactionBudget.getOrDefault(cell, 0);
                    if (count >= MAX_HITBOXES_PER_CELL) {
                        show = List.of();
                    } else {
                        interactionBudget.put(cell, count + 1);
                    }
                }
                chain.hitboxes.get(r * HITBOX_PER_LINK + j).render(show, hx, hy, hz);
            }
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

    /** LOD radius (blocks) within which a player is sent a segment's INTERACTION hitboxes — a bit beyond the
     *  client's ~3-6 block interaction reach so an edge click / a moving zipline rider still registers, while far
     *  viewers and the far segments of a long chain get no interaction entities at all. */
    private static final double INTERACT_RANGE = 24.0;

    /** The subset of {@code viewers} within {@link #INTERACT_RANGE} of segment centre {@code (x,y,z)} — the only
     *  ones a segment's clickable hitboxes are worth spawning for (see the LOD note in {@link #syncRope}). */
    private static List<Player> interactViewersFor(List<Player> viewers, double x, double y, double z) {
        double r2 = INTERACT_RANGE * INTERACT_RANGE;
        List<Player> near = null;
        for (Player p : viewers) {
            Object pp = p.platformPlayer();
            if (!(pp instanceof org.bukkit.entity.Player b)) {
                continue;
            }
            org.bukkit.Location l = b.getLocation();
            double dx = l.getX() - x, dy = l.getY() - y, dz = l.getZ() - z;
            if (dx * dx + dy * dy + dz * dz <= r2) {
                if (near == null) {
                    near = new ArrayList<>(viewers.size());
                }
                near.add(p);
            }
        }
        return near == null ? List.of() : near;
    }

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
