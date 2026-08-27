/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.gen.FunnelUtils
 */
package dev.arubik.craftengine.script.gen.conveyor;

import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.FunnelUtils;
import java.util.ArrayList;

public final class Funnel {
    public static ScriptValue onRightClick(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        FunnelUtils._funnelToggleMode((ScriptContext.Builder)builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        FunnelUtils._funnelDropHeld((ScriptContext.Builder)builder2);
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_funnel_tick");
        arrayList.add("_funnel_toggle_mode");
        arrayList.add("_funnel_drop_held");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"conveyor/funnel_utils.pf", null, arrayList);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        FunnelUtils._funnelTick((ScriptContext.Builder)builder2);
    }
}
