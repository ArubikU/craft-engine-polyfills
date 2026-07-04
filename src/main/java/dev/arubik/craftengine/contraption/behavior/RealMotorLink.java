package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.rotation.RpmProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * Shared "find/read a REAL adjacent motor" helper (Task 4, CONTRAPTIONS.md 2026-07-01
 * session) — same pull-scan idiom {@code MinerBlockBehavior.MinerController.tick} and
 * {@code CrusherBlockEntity}/{@code SmelteryBlockEntity} already use, extracted so both
 * {@link RotationalBearingBehavior} and {@link LinearActuatorBehavior} can share it instead
 * of duplicating the 6-neighbor scan.
 *
 * <p>Bearings no longer produce their own fixed RPM/speed (the old
 * {@code ContraptionAssembler.DEFAULT_ROTATIONAL_RPM}/{@code DEFAULT_LINEAR_SPEED}
 * constants, and {@code BearingBlockBehavior}'s {@code rpm:} config field, are now only a
 * fallback for when no real motor is present — see those classes' javadocs). Instead, at
 * assembly time the bearing's REAL-WORLD position (still real — only the captured structure
 * moves into the {@code ContraptionLevel}, the bearing's origin block position stays a valid
 * real-world coordinate forever after) is scanned once for the strongest adjacent
 * {@link RpmProvider}; the resulting neighbor {@link BlockPos} is stored on the behavior and
 * re-resolved fresh every tick (a real motor can be placed, removed, or replaced after
 * assembly — re-resolving means the bearing keeps tracking whatever's there NOW, not a stale
 * snapshot).
 */
public final class RealMotorLink {

    private RealMotorLink() {
    }

    /**
     * Scans the 6 neighbors of {@code bearingPos} (a REAL, still-valid world position) for the
     * strongest real {@link RpmProvider} block entity, exactly like
     * {@code MinerController.tick}'s own neighbor scan. Returns the neighbor's position, or
     * {@code null} if none of the 6 neighbors host one right now.
     */
    public static BlockPos findAdjacentMotor(Level level, BlockPos bearingPos) {
        BlockPos best = null;
        float bestPotential = 0f;
        for (Direction d : Direction.values()) {
            BlockPos neighborPos = bearingPos.relative(d);
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, neighborPos);
            if (be != null && be.controller instanceof RpmProvider provider && provider.isRpmSource()
                    && provider.potentialRpm() > bestPotential) {
                bestPotential = provider.potentialRpm();
                best = neighborPos.immutable();
            }
        }
        return best;
    }

    /**
     * Re-resolves {@code motorPos} fresh (the motor may have been placed/broken/replaced since
     * assembly) and returns its current live RPM, or {@code 0} if there's no
     * {@link RpmProvider} there anymore.
     */
    public static float currentRpm(Level level, BlockPos motorPos) {
        if (level == null || motorPos == null) {
            return 0f;
        }
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, motorPos);
        if (be != null && be.controller instanceof RpmProvider provider) {
            return provider.getRpm();
        }
        return 0f;
    }

    /**
     * Reports {@code su} (stress units) of load to the real motor at {@code motorPos}, via
     * {@link RpmProvider#reportStressLoad} — same "tell the source how much load the driven
     * network imposes" contract the conveyor feature already uses (see that interface's own
     * javadoc). Called by {@link RotationalBearingBehavior}/{@link LinearActuatorBehavior}
     * every tick, scaled by how many blocks the contraption has captured (bigger structure =
     * more load = the motor may stall/reduce {@link RpmProvider#getRpm()} on its own if
     * overstressed — this class doesn't decide that, it only reports the demand). No-op if
     * there's no real {@link RpmProvider} at {@code motorPos} right now.
     */
    public static void reportStressLoad(Level level, BlockPos motorPos, float su) {
        if (level == null || motorPos == null) {
            return;
        }
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, motorPos);
        if (be != null && be.controller instanceof RpmProvider provider) {
            provider.reportStressLoad(su);
        }
    }
}
