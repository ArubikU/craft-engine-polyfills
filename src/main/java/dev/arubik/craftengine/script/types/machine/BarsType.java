package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

import java.util.ArrayList;
import java.util.List;

/**
 * "Bars" script type — exposes the machine's bar stats (fluid, progress, etc.) for read/query.
 * Instance object: the owning AbstractMachineBlockEntity (bars are read via barStat/barSubtype).
 */
public final class BarsType {

    private BarsType() {}

    public static void register() {
        PolyTypeRegistry.define("Bars")
            // Bars.get("water") → fraction 0.0-1.0 for that bar
            .method("get", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0.0);
                AbstractMachineBlockEntity m = be(obj);
                String id = args.get(0).asStr();
                try {
                    double[] stat = m.barStat(id);
                    double val = stat[0], max = stat[1];
                    return ScriptValue.of(max > 0 ? val / max : 0.0);
                } catch (Throwable ignored) { return ScriptValue.of(0.0); }
            })
            // Bars.value("water") → current raw value
            .method("value", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0.0);
                AbstractMachineBlockEntity m = be(obj);
                try { return ScriptValue.of(m.barStat(args.get(0).asStr())[0]); }
                catch (Throwable ignored) { return ScriptValue.of(0.0); }
            })
            // Bars.max("water") → max raw value
            .method("max", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0.0);
                AbstractMachineBlockEntity m = be(obj);
                try { return ScriptValue.of(m.barStat(args.get(0).asStr())[1]); }
                catch (Throwable ignored) { return ScriptValue.of(0.0); }
            })
            // Bars.subtype("water") → type string (e.g. "water", "steam")
            .method("subtype", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                AbstractMachineBlockEntity m = be(obj);
                try {
                    String sub = m.barSubtype(args.get(0).asStr());
                    return sub != null ? ScriptValue.of(sub) : ScriptValue.NULL;
                } catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            // Bars.list() → Array of bar id strings
            .method("list", (obj, args) -> {
                AbstractMachineBlockEntity m = be(obj);
                try {
                    List<ScriptValue> ids = new ArrayList<>();
                    // DataMachineBlockEntity exposes bars via getBars() — use reflection-safe cast
                    if (m instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm) {
                        for (MachineBar bar : dm.getBars()) ids.add(ScriptValue.of(bar.id));
                    }
                    return new ScriptValue.Array(ids);
                } catch (Throwable ignored) { return new ScriptValue.Array(List.of()); }
            });
    }

    public static ScriptValue wrap(AbstractMachineBlockEntity machine) {
        return machine == null ? ScriptValue.NULL : ScriptValue.ofObj("Bars", machine);
    }

    private static AbstractMachineBlockEntity be(Object obj) {
        return (AbstractMachineBlockEntity) obj;
    }
}
