package dev.arubik.craftengine.contraption.behavior;

/**
 * Pure mining-damage accumulation math for {@link MinerBehavior} (CONTRAPTIONS.md §5
 * Phase 5 "stalling state machine") — no NMS dependency, kept separate so the actual
 * accumulate-vs-hardness arithmetic is unit-testable without a live block/world.
 */
public final class MiningMath {

    private MiningMath() {
    }

    /**
     * Damage dealt to a block THIS tick, given the driver's RPM and the block's hardness.
     * Unbreakable blocks (hardness &lt; 0, e.g. bedrock) never take damage. A hardness of 0
     * (e.g. tall grass) is treated as instant-break (returns {@link Double#MAX_VALUE}) so a
     * single tick always finishes it, matching vanilla instant-mine blocks.
     */
    public static double damagePerTick(double rpm, double hardness) {
        if (hardness < 0) {
            return 0.0;
        }
        if (hardness == 0.0) {
            return Double.MAX_VALUE;
        }
        return rpm / hardness;
    }

    /** True once accumulated damage has reached (or passed) the block's hardness. */
    public static boolean isBroken(double accumulatedDamage, double hardness) {
        return hardness >= 0 && accumulatedDamage >= hardness;
    }

    /**
     * Maps accumulated-damage fraction (0..1 of hardness) to a vanilla crack-stage index
     * (0-9), for {@code ClientboundBlockDestructionPacket}. Clamped both ends.
     */
    public static int crackStage(double accumulatedDamage, double hardness) {
        if (hardness <= 0) {
            return 0;
        }
        double fraction = accumulatedDamage / hardness;
        int stage = (int) Math.floor(fraction * 10.0);
        return Math.max(0, Math.min(9, stage));
    }
}
