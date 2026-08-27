package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

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
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (AbstractMachineBlockEntity m, String id) -> {
                    try {
                        double[] stat = m.barStat(id);
                        double val = stat[0], max = stat[1];
                        return max > 0 ? val / max : 0.0;
                    } catch (Throwable ignored) { return 0.0; }
                })
            // Bars.value("water") → current raw value
            .methodTyped1("value", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (AbstractMachineBlockEntity m, String id) -> {
                    try { return m.barStat(id)[0]; }
                    catch (Throwable ignored) { return 0.0; }
                })
            // Bars.max("water") → max raw value
            .methodTyped1("max", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (AbstractMachineBlockEntity m, String id) -> {
                    try { return m.barStat(id)[1]; }
                    catch (Throwable ignored) { return 0.0; }
                })
            // Bars.subtype("water") → type string (e.g. "water", "steam")
            // Return codec is RAW (identity) — the original returns ScriptValue.NULL on a null
            // subtype rather than a plain STRING codec's own coercion, so this keeps behavior exact.
            .methodTyped1("subtype", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (AbstractMachineBlockEntity m, String id) -> {
                    try {
                        String sub = m.barSubtype(id);
                        return sub != null ? ScriptValue.of(sub) : ScriptValue.NULL;
                    } catch (Throwable ignored) { return ScriptValue.NULL; }
                })
            // Bars.list() → Array of bar id strings
            .methodTyped0("list", TypeCodecs.RAW,
                (AbstractMachineBlockEntity m) -> {
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
