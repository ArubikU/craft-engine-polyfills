package dev.arubik.craftengine.contraption.api;

/**
 * A contraption behavior that consumes power from a {@link PowerSource}.
 *
 * <p>
 * Miner drills, movers, and any behavior that needs energy implements this interface.
 * The contraption system automatically connects consumers to adjacent sources, and the
 * behavior reads the delivered power to scale its effect (faster mining, stronger thrust).
 *
 * <p>
 * <b>Implementation note</b>: The rotation system's {@code RpmConsumer} extends this, so
 * existing machines work without changes.
 */
public interface PowerConsumer {

    /**
     * Sets the power currently delivered to this consumer.
     *
     * <p>
     * Called every tick by the contraption system after finding the strongest adjacent
     * {@link PowerSource}. The consumer stores this value and uses it to scale its
     * behavior (a miner with 64 power drills twice as fast as one with 32 power).
     *
     * @param power the delivered power (e.g., RPM, watts, bar), or 0 if no source found
     */
    void setPower(float power);

    /**
     * Gets the power currently delivered to this consumer.
     *
     * @return the last value passed to {@link #setPower(float)}
     */
    float getPower();

    /**
     * Optional: the power this consumer demands for full-speed operation.
     *
     * <p>
     * A miner might declare it needs 32 power to run at base speed. The contraption
     * system can use this to warn when a motor is undersized, or to compute overstress.
     * If not implemented, the consumer works at any power level (scaling down gracefully).
     *
     * @return the minimum recommended power, or 0 if not applicable
     */
    default float getRequiredPower() {
        return 0f;
    }
}
