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
}
