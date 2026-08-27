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
public final class LogicGate {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "mode";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            object3 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object3;
        builder.val("mode", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            String string = "left_power";
            String string3 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
            object2 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        double d = object2.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)d);
        builder.val("left", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            String string = "right_power";
            String string4 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string4) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        double d2 = object.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue7 = ScriptValue.of((double)d2);
        builder.val("right", scriptValue7);
        double d3 = 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)0.0);
        builder.val("result", scriptValue8);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LogicGate.class, 0.0)))) {
            double d4 = d != 0.0 || d2 != 0.0 ? 15.0 : 0.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)d4);
            builder.val("result", scriptValue9);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LogicGate.class, 1.0)))) {
            double d5 = d != 0.0 && d2 != 0.0 ? 15.0 : 0.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)d5);
            builder.val("result", scriptValue10);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LogicGate.class, 2.0)))) {
            double d6 = d != 0.0 && d2 != 0.0 ? 0.0 : 15.0;
            ScriptValue scriptValue11 = ScriptValue.of((double)d6);
            builder.val("result", scriptValue11);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LogicGate.class, 3.0)))) {
            double d7 = d != 0.0 || d2 != 0.0 ? 0.0 : 15.0;
            ScriptValue scriptValue12 = ScriptValue.of((double)d7);
            builder.val("result", scriptValue12);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LogicGate.class, 4.0)))) {
            double d8 = d != d2 ? 15.0 : 0.0;
            ScriptValue scriptValue13 = ScriptValue.of((double)d8);
            builder.val("result", scriptValue13);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LogicGate.class, 5.0)))) {
            double d9 = d == d2 ? 15.0 : 0.0;
            ScriptValue scriptValue14 = ScriptValue.of((double)d9);
            builder.val("result", scriptValue14);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("result");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            v3 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$108_emit_redstone(scriptValue15.asNum())) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
