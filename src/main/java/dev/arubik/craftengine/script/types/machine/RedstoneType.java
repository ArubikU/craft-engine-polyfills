package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.machine.MachineRedstone;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

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
            .property("input", obj -> ScriptValue.of(ref(obj).input()))
            .property("output", obj -> ScriptValue.of(ref(obj).output()))
            /** True when anything is powering this block. */
            .property("powered", obj -> ScriptValue.of(ref(obj).input() > 0))
            /** True when this block is emitting. */
            .property("emitting", obj -> ScriptValue.of(ref(obj).output() > 0))
            .property("max", obj -> ScriptValue.of(MachineRedstone.MAX_POWER))

            // --- Methods ---
            .method("set", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                return ScriptValue.of(ref(obj).set((int) args.get(0).asNum()));
            })
            .method("on", (obj, args) -> {
                int power = args.isEmpty() ? MachineRedstone.MAX_POWER : (int) args.get(0).asNum();
                return ScriptValue.of(ref(obj).set(power));
            })
            .method("off", (obj, args) -> ScriptValue.of(ref(obj).set(0)))
            .method("clear", (obj, args) -> ScriptValue.of(ref(obj).set(0)))
            /** Emit full power when {@code cond} is truthy, nothing otherwise. */
            .method("toggle", (obj, args) -> {
                boolean on = !args.isEmpty() ? args.get(0).asBool() : ref(obj).output() == 0;
                return ScriptValue.of(ref(obj).set(on ? MachineRedstone.MAX_POWER : 0));
            })
            /** Mirror the incoming signal — a repeater in one call. */
            .method("relay", (obj, args) -> {
                RedstoneRef r = ref(obj);
                return ScriptValue.of(r.set(r.input()));
            });
    }

    public static ScriptValue wrap(MachineType.MachineRef machine) {
        if (machine == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Redstone", new RedstoneRef(machine));
    }

    private static RedstoneRef ref(Object obj) {
        return (RedstoneRef) obj;
    }
}
