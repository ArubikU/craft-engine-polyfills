package dev.arubik.craftengine.machine;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Single owner of a machine's redstone output state.
 *
 * <p>Before this class existed the emitted power was written by the script layer under
 * {@code polyfills:redstone_output} and read back by {@link
 * dev.arubik.craftengine.machine.block.MachineBlockBehavior} under {@code polyfills:_redstone_power}.
 * The two string literals lived in different files, never matched, and every script-driven
 * redstone emitter silently produced no signal. Routing both sides through {@link #POWER} makes
 * that class of desync impossible to express.
 *
 * <p>All access is static because the state lives in the block entity's persistent NBT, not in a
 * Java object with a lifetime — a machine's redstone output has to survive chunk unload.
 */
public final class MachineRedstone {

    /** Minimum emittable power. */
    public static final int MIN_POWER = 0;
    /** Maximum emittable power (vanilla redstone range). */
    public static final int MAX_POWER = 15;

    /**
     * The one and only storage key for a machine's emitted redstone power.
     * Note {@link TypedKey#nbtKey()} stores the path only, so this is {@code "_redstone_power"}
     * in NBT — matching the historical {@code NamespacedKey.getKey()} layout of saved worlds.
     */
    public static final TypedKey<Integer> POWER =
            TypedKey.of("polyfills", "_redstone_power", NbtType.INTEGER);

    private MachineRedstone() {}

    /** Clamps {@code power} into the emittable range. */
    public static int clamp(int power) {
        return Math.max(MIN_POWER, Math.min(MAX_POWER, power));
    }

    /**
     * The power this machine is currently emitting, or 0 when it has never emitted / has no
     * block entity. Never throws.
     */
    public static int output(PersistentBlockEntity be) {
        if (be == null) return 0;
        try {
            Integer v = be.get(POWER);
            return v != null ? clamp(v) : 0;
        } catch (Throwable ignored) {
            return 0;
        }
    }

    /**
     * Sets the emitted power and notifies neighbours so vanilla redstone recomputes.
     *
     * <p>Returns {@code false} when there is nothing to write to. A write whose value equals the
     * stored one is skipped entirely: action scripts run every tick, and re-notifying neighbours
     * on an unchanged value is what turns a handful of sensors into a redstone update storm.
     */
    public static boolean setOutput(Level level, BlockPos pos, PersistentBlockEntity be, int power) {
        if (be == null || !allowed(be)) return false;
        int clamped = clamp(power);
        if (output(be) == clamped && has(be)) return true;
        be.set(POWER, clamped);
        if (level != null && pos != null) {
            try {
                level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
            } catch (Throwable ignored) {
                // Neighbour notification is best-effort; the stored value is what getSignal reads.
            }
        }
        return true;
    }

    /**
     * Whether this machine is allowed to emit at all — the {@code flags.redstone} switch.
     * A machine that is not a sensor should not be able to power its neighbours by accident.
     * Non-machine block entities are unrestricted.
     */
    private static boolean allowed(PersistentBlockEntity be) {
        try {
            if (be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm) {
                MachineDefinition def = dm.definition();
                return def == null || def.redstone();
            }
        } catch (Throwable ignored) {
            // Fall through to permissive: never let a lookup failure mute a sensor.
        }
        return true;
    }

    /** Whether an output value has ever been stored (distinguishes "0" from "never set"). */
    public static boolean has(PersistentBlockEntity be) {
        if (be == null) return false;
        try {
            return be.has(POWER);
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * The strongest redstone signal reaching this position from its neighbours — the machine's
     * redstone <em>input</em>, as opposed to {@link #output}.
     */
    public static int input(Level level, BlockPos pos) {
        if (level == null || pos == null) return 0;
        try {
            return clamp(level.getBestNeighborSignal(pos));
        } catch (Throwable ignored) {
            return 0;
        }
    }
}
