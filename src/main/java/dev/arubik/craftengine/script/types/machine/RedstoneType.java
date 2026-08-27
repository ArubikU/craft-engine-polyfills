package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.machine.MachineRedstone;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

/**
 * "Redstone" type — a machine's redstone port, reachable from scripts as {@code Machine.redstone}.
 *
 * <p>Wraps {@link MachineRedstone} so scripts never name a storage key. Reads go through
 * {@code .input} / {@code .output}, writes through {@code .set(n)} and friends.
 *
 * <pre>
 *   if Machine.redstone.powered { ... }         # something is powering us
 *   Machine.redstone.set(15)                    # emit full power
 *   Machine.redstone.off()                      # stop emitting
 *   Machine.redstone.set(Machine.redstone.input)  # repeater
 * </pre>
 *
 * <p>{@link RedstoneRef} implements {@link ScriptValue.Numeric} returning the <em>input</em> level,
 * so the object still behaves as a number wherever {@code Machine.redstone} was previously a plain
 * int — {@code Machine.redstone > 0}, {@code power = Machine.redstone}, {@code power / 15.0} all
 * keep their old meaning.
 */
public final class RedstoneType {

    /** Instance object: the machine whose redstone port this is. */
    public record RedstoneRef(MachineType.MachineRef machine) implements ScriptValue.Numeric {

        /** Strongest signal reaching this block from its neighbours. */
        public int input() {
            return MachineRedstone.input(machine.level(), machine.pos());
        }

        /** Power this block is currently emitting. */
        public int output() {
            return MachineRedstone.output(machine.blockEntity());
        }

        public boolean set(int power) {
            return MachineRedstone.setOutput(machine.level(), machine.pos(), machine.blockEntity(), power);
        }

        /** Numeric coercion is the INPUT level — the pre-existing meaning of {@code Machine.redstone}. */
        @Override
        public double numericValue() {
            return input();
        }
    }

    private RedstoneType() {}

    public static void register() {
        PolyTypeRegistry.define("Redstone")
            // --- Properties ---
            .propertyTyped("input", TypeCodecs.DOUBLE, (RedstoneRef r) -> (double) r.input())
            .propertyTyped("output", TypeCodecs.DOUBLE, (RedstoneRef r) -> (double) r.output())
            /** True when anything is powering this block. */
            .propertyTyped("powered", TypeCodecs.BOOL, (RedstoneRef r) -> r.input() > 0)
            /** True when this block is emitting. */
            .propertyTyped("emitting", TypeCodecs.BOOL, (RedstoneRef r) -> r.output() > 0)
            .propertyTyped("max", TypeCodecs.DOUBLE, (RedstoneRef r) -> (double) MachineRedstone.MAX_POWER)

            // --- Methods ---
            .methodTyped1("set", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (RedstoneRef r, Double power) -> r.set(power.intValue()))
            // "on" defaults its power argument to MAX_POWER when omitted and still runs the body —
            // exactly methodTypedOpt1's optional-argument-with-default shape.
            .methodTypedOpt1("on", TypeCodecs.DOUBLE, (double) MachineRedstone.MAX_POWER, TypeCodecs.BOOL,
                (RedstoneRef r, Double power) -> r.set(power.intValue()))
            .methodTyped0("off", TypeCodecs.BOOL, (RedstoneRef r) -> r.set(0))
            .methodTyped0("clear", TypeCodecs.BOOL, (RedstoneRef r) -> r.set(0))
            // Typed with a null sentinel: BOOL decodes a PRESENT argument via the primitive-backed
            // asBool(), so it can never yield Java null — `onArg == null` is exactly "no argument",
            // which lets the instance-dependent fallback (output() == 0) still be recomputed per
            // call from the live instance inside the body.
            /** Emit full power when {@code cond} is truthy, nothing otherwise. */
            .methodTypedOpt1("toggle", TypeCodecs.BOOL, null, TypeCodecs.BOOL,
                (RedstoneRef r, Boolean onArg) -> {
                    boolean on = onArg != null ? onArg : r.output() == 0;
                    return r.set(on ? MachineRedstone.MAX_POWER : 0);
                })
            /** Mirror the incoming signal — a repeater in one call. */
            .methodTyped0("relay", TypeCodecs.BOOL, (RedstoneRef r) -> r.set(r.input()));
    }

    public static ScriptValue wrap(MachineType.MachineRef machine) {
        if (machine == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Redstone", new RedstoneRef(machine));
    }
}
