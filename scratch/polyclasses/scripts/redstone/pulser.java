/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClassMachine;
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
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        Object object2;
        ScriptValue scriptValue3;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            String string = "duration";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
            object3 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object3;
        builder.val("duration", scriptValue5);
        if (scriptValue5.asNum() <= 0.0) {
            double d = 10.0;
            ScriptValue scriptValue6 = ScriptValue.of((double)10.0);
            builder.val("duration", scriptValue6);
        }
        if ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            String string = "prev_power";
            String string3 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
            object2 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object2;
        builder.val("prev", scriptValue7);
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        Object object4 = polyClassMachine != null ? polyClassMachine.pg$171_redstone() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = object4.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)d);
        builder.val("cur", scriptValue8);
        if (d == 1.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Pulser.class, 0.0)))) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                String string = "countdown";
                String string4 = "int";
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("duration");
                PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9);
                v3 = polyClassMachine2 != null ? ScriptValue.of((boolean)polyClassMachine2.tm$82_set_typed(string, string4, scriptValue10)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            String string = "countdown";
            String string5 = "int";
            PolyClassMachine polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine3 != null ? polyClassMachine3.tm$34_get_typed(string, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = object;
        builder.val("t", scriptValue11);
        if (scriptValue11.asNum() > 0.0) {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                double d2 = 15.0;
                PolyClassMachine polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue12);
                v5 = polyClassMachine4 != null ? ScriptValue.of((boolean)polyClassMachine4.tm$108_emit_redstone(d2)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
            if (scriptValue13 != ScriptValue.NULL) {
                String string = "countdown";
                String string6 = "int";
                ScriptValue scriptValue14 = ScriptValue.of((double)(scriptValue11.asNum() - 1.0));
                PolyClassMachine polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue13);
                v6 = polyClassMachine5 != null ? ScriptValue.of((boolean)polyClassMachine5.tm$82_set_typed(string, string6, scriptValue14)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                double d3 = 0.0;
                PolyClassMachine polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue15);
                v7 = polyClassMachine6 != null ? ScriptValue.of((boolean)polyClassMachine6.tm$108_emit_redstone(d3)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
        if (scriptValue16 != ScriptValue.NULL) {
            String string = "prev_power";
            String string7 = "int";
            ScriptValue scriptValue17 = ScriptValue.of((double)d);
            PolyClassMachine polyClassMachine7 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue16);
            v8 = polyClassMachine7 != null ? ScriptValue.of((boolean)polyClassMachine7.tm$82_set_typed(string, string7, scriptValue17)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
        } else {
            v8 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
