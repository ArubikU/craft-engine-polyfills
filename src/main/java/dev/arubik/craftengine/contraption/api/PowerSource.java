package dev.arubik.craftengine.contraption.api;

import net.momirealms.craftengine.core.world.BlockPos;

/**
 * A block that produces power for contraption behaviors (motors, engines, manual cranks).
 *
 * <p>
 * This is the contraption API's abstraction over rotational power (RPM), electric power,
 * hydraulic pressure, or any other energy system. Contraption behaviors (miner, mover)
 * depend on this interface rather than concrete motor implementations, so external plugins
 * can add custom power sources without modifying contraption code.
 *
 * <p>
 * <b>Implementation note</b>: The rotation system's {@code RpmProvider} extends this
 * interface, so existing motors work without changes. New power systems implement this
 * directly.
 */
public interface PowerSource {

    /**
     * Current delivered power, accounting for load and overstress.
     *
     * <p>
     * For rotational power this is RPM (0 if overstressed). For electric power this might
     * be watts. For hydraulic pressure this might be bar. The contraption behavior
     * interprets the number according to its own unit (a miner's drill speed scales with
     * "power" regardless of what that power is).
     *
     * @return current power output, or 0 if stalled/unpowered
     */
    float getPower();

    /**
     * Potential power ignoring current load, for network sizing.
     *
     * <p>
     * A motor sizing a belt network needs to know what power it <em>would</em> deliver if
     * unstressed, not what it currently delivers (which may be zero because of overstress).
     * This value stays stable while the motor is running, even if {@link #getPower()}
     * drops to zero.
     *
     * @return power capacity, independent of current load
     */
    float getPotentialPower();

    /**
     * Whether this block is currently a power source.
     *
     * <p>
     * A block may implement {@code PowerSource} but not always be active (e.g., a motor
     * that's out of fuel). This returns {@code true} only when the source is genuinely
     * supplying power.
     *
     * @return true if this source is active right now
     */
    boolean isPowerSource();

    /**
     * Whether power from this source can reach the given consumer position.
     *
     * <p>
     * Rotational power travels along shafts and through gearboxes; a motor 10 blocks away
     * connected by belts reaches the consumer, but a motor 1 block away with no belt does
     * not. Electric power might check wire connections. This lets consumers find the
     * correct source when multiple motors are nearby.
     *
     * @param consumerPos the position of the block trying to receive power
     * @return true if power from this source reaches that position
     */
    default boolean powerReaches(BlockPos consumerPos) {
        return true; // default: direct adjacency only (override for network propagation)
    }
}
