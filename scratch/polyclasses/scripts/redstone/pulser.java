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
public final class Pulser {
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
        PolyClassMachine_v3 polyClassMachine_v3;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "duration";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object3 = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object3;
        builder.val("duration", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 10.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)10.0);
            builder.val("duration", scriptValue3);
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "prev_power";
            String string3 = "int";
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v33 != null ? polyClassMachine_v33.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object2;
        builder.val("prev", scriptValue4);
        Object object4 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$171_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = object4.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)d);
        builder.val("cur", scriptValue5);
        if (d == 1.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Pulser.class, 0.0)))) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "countdown";
                String string4 = "int";
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("duration");
                PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v3 = polyClassMachine_v34 != null ? ScriptValue.of((boolean)polyClassMachine_v34.tm$82_set_typed(string, string4, scriptValue6)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "countdown";
            String string5 = "int";
            PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v35 != null ? polyClassMachine_v35.tm$34_get_typed(string, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("t", scriptValue7);
        if (scriptValue7.asNum() > 0.0) {
            if (scriptValue != ScriptValue.NULL) {
                double d2 = 15.0;
                PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v5 = polyClassMachine_v36 != null ? ScriptValue.of((boolean)polyClassMachine_v36.tm$108_emit_redstone(d2)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            if (scriptValue != ScriptValue.NULL) {
                String string = "countdown";
                String string6 = "int";
                ScriptValue scriptValue8 = ScriptValue.of((double)(scriptValue7.asNum() - 1.0));
                PolyClassMachine_v3 polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v6 = polyClassMachine_v37 != null ? ScriptValue.of((boolean)polyClassMachine_v37.tm$82_set_typed(string, string6, scriptValue8)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
        } else if (scriptValue != ScriptValue.NULL) {
            double d3 = 0.0;
            PolyClassMachine_v3 polyClassMachine_v38 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v7 = polyClassMachine_v38 != null ? ScriptValue.of((boolean)polyClassMachine_v38.tm$108_emit_redstone(d3)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "prev_power";
            String string7 = "int";
            ScriptValue scriptValue9 = ScriptValue.of((double)d);
            PolyClassMachine_v3 polyClassMachine_v39 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v8 = polyClassMachine_v39 != null ? ScriptValue.of((boolean)polyClassMachine_v39.tm$82_set_typed(string, string7, scriptValue9)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
        } else {
            v8 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
