package dev.arubik.craftengine.script.types.resource;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

import java.util.List;
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
            // NOT migrated to methodTypedN — every method below takes an OPTIONAL tank-name arg via
            // resolve(obj, args): when the arg is missing, resolve() doesn't short-circuit to a
            // fixed fallback value, it still runs real logic (falls back to the map's first entry,
            // `m.values().iterator().next()`). A typed handler's onMissingArgs can only supply a
            // fixed R value for "not enough args" — it never invokes the handler body at all in
            // that case — so this "default-if-missing" shape (see task instructions) can't be
            // expressed without changing behavior. Left untyped, matching every method here.
            .method("level",    (obj, args) -> ScriptValue.of(resolve(obj, args)[0]))
            .method("capacity", (obj, args) -> ScriptValue.of(resolve(obj, args)[1]))
            .method("fraction", (obj, args) -> { double[] d = resolve(obj, args); return ScriptValue.of(d[1] > 0 ? d[0] / d[1] : 0); })
            .method("percent",  (obj, args) -> { double[] d = resolve(obj, args); return ScriptValue.of(d[1] > 0 ? (d[0] / d[1]) * 100 : 0); })
            .method("is_empty", (obj, args) -> ScriptValue.of(resolve(obj, args)[0] <= 0))
            .method("is_full",  (obj, args) -> { double[] d = resolve(obj, args); return ScriptValue.of(d[1] > 0 && d[0] >= d[1]); })
            .method("get",      (obj, args) -> {
                String prop = args.size() >= 2 ? args.get(1).asStr() : "fraction";
                double[] d = resolve(obj, args);
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

    private static double[] resolve(Object obj, List<ScriptValue> args) {
        Map<String, double[]> m = tanks(obj);
        String name = args.isEmpty() ? null : args.get(0).asStr();
        double[] d = name != null ? m.get(name) : null;
        if (d == null && !m.isEmpty()) d = m.values().iterator().next();
        return d != null ? d : new double[]{0, 0};
    }
}
