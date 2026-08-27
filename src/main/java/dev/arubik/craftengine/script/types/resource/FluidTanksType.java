package dev.arubik.craftengine.script.types.resource;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

import java.util.Map;

/** FluidTanks and GasTanks types. Instance = Map<String, double[]> {level, capacity}. */
public final class FluidTanksType {

    private FluidTanksType() {}

    public static void register() {
        registerFor("FluidTanks");
        registerFor("GasTanks");
    }

    private static void registerFor(String typeName) {
        PolyTypeRegistry.define(typeName)
            .property("total_level", obj -> ScriptValue.of(tanks(obj).values().stream().mapToDouble(t -> t[0]).sum()))
            .property("total_capacity", obj -> ScriptValue.of(tanks(obj).values().stream().mapToDouble(t -> t[1]).sum()))
            .property("count", obj -> ScriptValue.of(tanks(obj).size()))
            // Every method here takes an OPTIONAL tank-name argument: when it is absent, resolve()
            // doesn't short-circuit, it still runs (falling back to the map's first entry). That is
            // exactly the methodTypedOptN shape — a null default for the name reproduces the
            // original `args.isEmpty() ? null : args.get(0).asStr()` precisely, and extra trailing
            // args were already ignored by the originals too.
            .methodTypedOpt1("level", TypeCodecs.STRING, null, TypeCodecs.DOUBLE,
                (Map<String, double[]> m, String name) -> resolve(m, name)[0])
            .methodTypedOpt1("capacity", TypeCodecs.STRING, null, TypeCodecs.DOUBLE,
                (Map<String, double[]> m, String name) -> resolve(m, name)[1])
            .methodTypedOpt1("fraction", TypeCodecs.STRING, null, TypeCodecs.DOUBLE,
                (Map<String, double[]> m, String name) -> { double[] d = resolve(m, name); return d[1] > 0 ? d[0] / d[1] : 0; })
            .methodTypedOpt1("percent", TypeCodecs.STRING, null, TypeCodecs.DOUBLE,
                (Map<String, double[]> m, String name) -> { double[] d = resolve(m, name); return d[1] > 0 ? (d[0] / d[1]) * 100 : 0; })
            .methodTypedOpt1("is_empty", TypeCodecs.STRING, null, TypeCodecs.BOOL,
                (Map<String, double[]> m, String name) -> resolve(m, name)[0] <= 0)
            .methodTypedOpt1("is_full", TypeCodecs.STRING, null, TypeCodecs.BOOL,
                (Map<String, double[]> m, String name) -> { double[] d = resolve(m, name); return d[1] > 0 && d[0] >= d[1]; })
            // get(name?, prop?) — the return type is genuinely dynamic (double for level/capacity/
            // fraction/percent, boolean for is_empty/is_full), so the return slot stays RAW.
            .methodTypedOpt2("get", TypeCodecs.STRING, null, TypeCodecs.STRING, "fraction", TypeCodecs.RAW,
                (Map<String, double[]> m, String name, String prop) -> {
                    double[] d = resolve(m, name);
                    return switch (prop) {
                        case "level"    -> ScriptValue.of(d[0]);
                        case "capacity" -> ScriptValue.of(d[1]);
                        case "fraction" -> ScriptValue.of(d[1] > 0 ? d[0] / d[1] : 0);
                        case "percent"  -> ScriptValue.of(d[1] > 0 ? (d[0] / d[1]) * 100 : 0);
                        case "is_empty" -> ScriptValue.of(d[0] <= 0);
                        case "is_full"  -> ScriptValue.of(d[1] > 0 && d[0] >= d[1]);
                        default -> ScriptValue.of(d[0]);
                    };
                });
    }

    public static ScriptValue wrap(String typeName, Map<String, double[]> tanks) {
        return tanks == null || tanks.isEmpty() ? ScriptValue.NULL : ScriptValue.ofObj(typeName, tanks);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, double[]> tanks(Object obj) { return (Map<String, double[]>) obj; }

    /** {@code name} is null only when the caller omitted the tank-name argument entirely — a
     *  missing name falls back to the map's first entry, never to a fixed zero tank. */
    private static double[] resolve(Map<String, double[]> m, String name) {
        double[] d = name != null ? m.get(name) : null;
        if (d == null && !m.isEmpty()) d = m.values().iterator().next();
        return d != null ? d : new double[]{0, 0};
    }
}
