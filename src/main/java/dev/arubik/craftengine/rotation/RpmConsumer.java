package dev.arubik.craftengine.rotation;

/**
 * A sink that receives rotational power (RPM) from a directly-connected
 * {@link RpmProvider} (e.g. a belt head driven by a motor).
 *
 * <p>SHARED ROTATION CONTRACT — do not change these signatures; the conveyor
 * feature depends on them.</p>
 */
public interface RpmConsumer {

    /** Called by the upstream provider each tick with the RPM being delivered. */
    void setInputRpm(float rpm);

    /** The RPM currently being delivered to this consumer (0 if disconnected). */
    float getInputRpm();
}
