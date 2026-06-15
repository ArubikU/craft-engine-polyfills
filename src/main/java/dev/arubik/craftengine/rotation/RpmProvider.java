package dev.arubik.craftengine.rotation;

/**
 * A source of rotational power (RPM), e.g. a motor.
 *
 * <p>SHARED ROTATION CONTRACT — do not change this signature; the conveyor
 * feature depends on it.</p>
 */
public interface RpmProvider {

    /** Current rotational output, in revolutions-per-minute. 0 when not producing. */
    float getRpm();

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
}
