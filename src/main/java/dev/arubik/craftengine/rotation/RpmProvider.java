package dev.arubik.craftengine.rotation;

/**
 * A source of rotational power (RPM), e.g. a motor.
 *
 * <p>SHARED ROTATION CONTRACT — do not change this signature; the conveyor
 * feature depends on it.</p>
 *
 * <p>Extends {@link dev.arubik.craftengine.contraption.api.PowerSource} so contraption
 * behaviors can consume rotational power through the generic power API.
 */
public interface RpmProvider extends dev.arubik.craftengine.contraption.api.PowerSource {

    /** Current rotational output, in revolutions-per-minute. 0 when not producing (or stalled). */
    float getRpm();

    @Override
    default float getPower() {
        return getRpm();
    }

    /**
     * The rotational output this source WOULD drive ignoring overstress — i.e. the speed used to
     * compute the network's stress load so it stays stable whether or not the source is currently
     * stalled. Belts size their reported SU from this (not {@link #getRpm()}) to avoid a
     * run/stall/run flicker; the source then decides the stall by comparing the accumulated load to
     * {@link #stressCapacity()}. Default: same as {@link #getRpm()}.
     */
    default float potentialRpm() {
        return getRpm();
    }

    @Override
    default float getPotentialPower() {
        return potentialRpm();
    }

    /**
     * True for a genuine power SOURCE (a motor). A relay (conveyor router that just passes power
     * through) returns false, so machines that must be driven directly by a motor can exclude it.
     */
    default boolean isRpmSource() {
        return true;
    }

    @Override
    default boolean isPowerSource() {
        return isRpmSource();
    }

    /**
     * Max stress (Create-style "stress units", SU) this source can drive. A consumer
     * network whose total stress impact exceeds this is overstressed and stalls.
     * Default: effectively unlimited.
     */
    default float stressCapacity() {
        return Float.MAX_VALUE;
    }

    /**
     * Report the stress load (SU) the driven network imposes this tick. The source
     * uses it to scale fuel use (more load = more vapor). Default ignores it.
     */
    default void reportStressLoad(float su) {
    }

    /**
     * World position this source pushes its RPM toward — its FRONT (emitter) face. A consumer that
     * is NOT at this position should not be driven by this source. Returns {@code null} for an
     * omnidirectional source (drives any adjacent consumer).
     */
    default net.momirealms.craftengine.core.world.BlockPos rpmHeadPos() {
        return null;
    }

    /**
     * Whether this source drives a consumer at {@code consumerPos}. A single-front motor reaches
     * only its {@link #rpmHeadPos()}; a relay (conveyor router) can reach several output sides.
     */
    default boolean rpmReaches(net.momirealms.craftengine.core.world.BlockPos consumerPos) {
        net.momirealms.craftengine.core.world.BlockPos head = rpmHeadPos();
        return head == null || (head.x() == consumerPos.x() && head.y() == consumerPos.y()
                && head.z() == consumerPos.z());
    }

    @Override
    default boolean powerReaches(net.momirealms.craftengine.core.world.BlockPos consumerPos) {
        return rpmReaches(consumerPos);
    }
}
