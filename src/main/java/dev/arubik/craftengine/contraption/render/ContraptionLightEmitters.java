package dev.arubik.craftengine.contraption.render;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared "internal light emitter falloff" scan against a {@link ContraptionLevel}'s FULL captured
 * block set — extracted so {@link ContraptionFurnitureSwarm} and
 * {@link ContraptionBlockEntityElementMirror} can get the same cross-cell torch-glow behavior
 * {@link ContraptionDisplaySwarm} already has for its own (plain captured block) cells.
 *
 * <p><b>Root cause (2026-07-02 live-test follow-up — "la antorcha no ilumina a los furnitures ni
 * al block entity renderer de craftengine").</b> {@link ContraptionDisplaySwarm}'s own
 * {@code ambientBlockLightWithEmitters} already scans for nearby light-emitting captured blocks
 * and applies a {@code max(ambient, emission - chebyshevDistance)} falloff — but it only scans
 * {@code this.cells}, i.e. that ONE swarm's own bookkeeping. That bookkeeping happens to already
 * cover every captured {@code BlockPos} (built straight from {@code ContraptionLevel#localPositions()}
 * in {@code rebuild}), so it's already "the whole contraption's blocks" as far as swarm #1 itself is
 * concerned. The bug is that swarms #2 (furniture) and #3 (CraftEngine entity-renderer block
 * mirrors) render a SEPARATE, overlapping-in-world-space data source (furniture instances / CEWorld
 * block-entities) and never consulted swarm #1's block set — or any block set — for emitters at
 * all, so a captured torch's light never reached them.
 *
 * <p>Fix: scan the {@link ContraptionLevel} directly — it is the single source of truth for every
 * captured block regardless of which swarm ends up rendering the position ({@code localPositions()}
 * + {@code getBlockState(pos)}), so any swarm can ask it "is there a nearby light emitter" without
 * needing to reach into another swarm's private cell map. Same falloff formula, formula, and
 * self-lighting convention as {@link ContraptionDisplaySwarm#ambientBlockLightWithEmitters} — a
 * light-emitting block's own position counts as its own distance-0 contribution.
 */
final class ContraptionLightEmitters {

    private ContraptionLightEmitters() {
    }

    /**
     * {@code max(ambientBlockLight, max over every light-emitting captured block in {@code level}
     * of (emission - chebyshevDistance to {@code targetLocal}))}, clamped 0-15. {@code level} null
     * (no captured-block data available, e.g. unit-test path) returns {@code ambientBlockLight}
     * unchanged.
     */
    static int withEmitterFalloff(ContraptionLevel level, BlockPos targetLocal, int ambientBlockLight) {
        if (level == null) {
            return ambientBlockLight;
        }
        int best = ambientBlockLight;
        try {
            for (BlockPos pos : level.localPositions()) {
                BlockState state = level.getBlockState(pos);
                int emission = state.getLightEmission();
                if (emission <= 0) {
                    continue;
                }
                int dist = chebyshevDistance(targetLocal, pos);
                int contribution = emission - dist;
                if (contribution > best) {
                    best = contribution;
                }
            }
        } catch (Throwable ignored) {
            // Defensive: never let a scan failure blank out the caller's own ambient reading.
            return ambientBlockLight;
        }
        return Math.min(15, Math.max(0, best));
    }

    /** Nearest local {@code BlockPos} to a fractional local-space position (e.g. a furniture's {@code localOffset()}). */
    static BlockPos nearestBlockPos(net.minecraft.world.phys.Vec3 local) {
        return BlockPos.containing(local.x, local.y, local.z);
    }

    private static int chebyshevDistance(BlockPos a, BlockPos b) {
        return Math.max(Math.abs(a.getX() - b.getX()),
                Math.max(Math.abs(a.getY() - b.getY()), Math.abs(a.getZ() - b.getZ())));
    }
}
