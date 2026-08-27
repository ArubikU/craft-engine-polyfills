/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class HandCrank {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_update_activated");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"kinetics/utils.pf", null, arrayList);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "crank_energy";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("energy", scriptValue2);
        if (scriptValue2.asNum() > 0.0) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "crank_energy";
                String string3 = "int";
                ScriptValue scriptValue3 = ScriptValue.of((double)(scriptValue2.asNum() - 1.0));
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v2 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue3)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            if (scriptValue != ScriptValue.NULL) {
                double d = 16.0;
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v3 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$106_set_rpm_output(d)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
        } else if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v4 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$106_set_rpm_output(d)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        double d = scriptValue2.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("is_now", scriptValue4);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder2.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", HandCrank.class, "_hc_act"));
        builder2.val("is_now", ScriptValue.of((double)d));
        Utils.1._updateActivated((ScriptContext.Builder)builder2);
        FILE_SCOPE = builder.build();
    }
}
