/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class RedstoneRandomizer {
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
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        Object object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$171_redstone() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = object2.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)d);
        builder.val("cur_power", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            String string = "prev_power";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
            object = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("prev_power", scriptValue4);
        if (scriptValue3 != ScriptValue.NULL) {
            String string = "prev_power";
            String string3 = "int";
            ScriptValue scriptValue5 = ScriptValue.of((double)d);
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
            v2 = polyClassMachine_v33 != null ? ScriptValue.of((boolean)polyClassMachine_v33.tm$82_set_typed(string, string3, scriptValue5)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        if (d > 0.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RedstoneRandomizer.class, 0.0)))) {
            double d2 = Math.floor(ScriptFormula.callBuiltin1((String)"random", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RedstoneRandomizer.class, 4.0)), (ScriptContext)scriptContext).asNum());
            ScriptValue scriptValue6 = ScriptValue.of((double)d2);
            builder.val("dir", scriptValue6);
            if (scriptValue3 != ScriptValue.NULL) {
                String string = "rand_dir";
                String string4 = "int";
                ScriptValue scriptValue7 = ScriptValue.of((double)d2);
                PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                v3 = polyClassMachine_v34 != null ? ScriptValue.of((boolean)polyClassMachine_v34.tm$82_set_typed(string, string4, scriptValue7)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            if (scriptValue3 != ScriptValue.NULL) {
                double d3 = 15.0;
                PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                v4 = polyClassMachine_v35 != null ? ScriptValue.of((boolean)polyClassMachine_v35.tm$108_emit_redstone(d3)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
        } else if (d == 0.0) {
            if (scriptValue3 != ScriptValue.NULL) {
                double d4 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                v5 = polyClassMachine_v36 != null ? ScriptValue.of((boolean)polyClassMachine_v36.tm$108_emit_redstone(d4)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
