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
public final class ArithmeticGate {
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
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "mode";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object3 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object3;
        builder.val("mode", scriptValue2);
        if (scriptValue != ScriptValue.NULL) {
            String string = "left_power";
            String string3 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("left", scriptValue3);
        if (scriptValue != ScriptValue.NULL) {
            String string = "right_power";
            String string4 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string4) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("right", scriptValue4);
        double d = 0.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)0.0);
        builder.val("result", scriptValue5);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ArithmeticGate.class, 0.0)))) {
            ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)scriptValue4);
            builder.val("result", scriptValue6);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ArithmeticGate.class, 1.0)))) {
            double d2 = scriptValue3.asNum() - scriptValue4.asNum();
            ScriptValue scriptValue7 = ScriptValue.of((double)d2);
            builder.val("result", scriptValue7);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ArithmeticGate.class, 2.0)))) {
            double d3 = scriptValue3.asNum() * scriptValue4.asNum();
            ScriptValue scriptValue8 = ScriptValue.of((double)d3);
            builder.val("result", scriptValue8);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ArithmeticGate.class, 3.0)))) {
            double d4;
            double d5 = ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ArithmeticGate.class, 0.0))) ^ true ? Math.floor((d4 = scriptValue4.asNum()) == 0.0 ? 0.0 : scriptValue3.asNum() / d4) : 0.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)d5);
            builder.val("result", scriptValue9);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ArithmeticGate.class, 4.0)))) {
            ScriptValue scriptValue10 = scriptValue3.asNum() < scriptValue4.asNum() ? scriptValue3 : scriptValue4;
            builder.val("result", scriptValue10);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ArithmeticGate.class, 5.0)))) {
            ScriptValue scriptValue11 = scriptValue3.asNum() > scriptValue4.asNum() ? scriptValue3 : scriptValue4;
            builder.val("result", scriptValue11);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ArithmeticGate.class, 6.0)))) {
            double d6;
            double d7 = ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ArithmeticGate.class, 0.0))) ^ true ? ((d6 = scriptValue4.asNum()) == 0.0 ? 0.0 : scriptValue3.asNum() % d6) : 0.0;
            ScriptValue scriptValue12 = ScriptValue.of((double)d7);
            builder.val("result", scriptValue12);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ArithmeticGate.class, 7.0)))) {
            double d8 = Math.floor(Math.pow(scriptValue3.asNum(), scriptValue4.asNum()));
            ScriptValue scriptValue13 = ScriptValue.of((double)d8);
            builder.val("result", scriptValue13);
        }
        if (scriptContext.getNum("result") < 0.0) {
            double d9 = 0.0;
            ScriptValue scriptValue14 = ScriptValue.of((double)0.0);
            builder.val("result", scriptValue14);
        }
        if (scriptContext.getNum("result") > 15.0) {
            double d10 = 15.0;
            ScriptValue scriptValue15 = ScriptValue.of((double)15.0);
            builder.val("result", scriptValue15);
        }
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("result");
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v3 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$108_emit_redstone(scriptValue16.asNum())) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, (ScriptValue)scriptValue16, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
