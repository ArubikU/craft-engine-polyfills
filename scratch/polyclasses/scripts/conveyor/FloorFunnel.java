/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.gen.FloorFunnelUtils
 */
package dev.arubik.craftengine.script.gen.conveyor;

import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.FloorFunnelUtils;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class FloorFunnel {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onRightClick(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(FloorFunnelUtils.fileScope()).copyFrom(scriptContext);
        FloorFunnelUtils._floorFunnelOnRightClick((ScriptContext.Builder)builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(FloorFunnelUtils.fileScope()).copyFrom(scriptContext);
        FloorFunnelUtils._floorFunnelDropHeld((ScriptContext.Builder)builder2);
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_floor_funnel_tick");
        arrayList.add("_floor_funnel_on_right_click");
        arrayList.add("_floor_funnel_drop_held");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"conveyor/floor_funnel_utils.pf", null, arrayList);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(FloorFunnelUtils.fileScope()).copyFrom(scriptContext);
        builder2.val("pull_above",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", FloorFunnel.class, 0));
        FloorFunnelUtils._floorFunnelTick((ScriptContext.Builder)builder2);
        FILE_SCOPE = builder.build();
    }
}
