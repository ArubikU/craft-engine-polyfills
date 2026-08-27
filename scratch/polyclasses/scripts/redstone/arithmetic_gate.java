/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

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
        ScriptValue scriptValue;
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "mode";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                object3 = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object3;
        builder.val("mode", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "left_power";
            String string3 = "int";
            if (scriptValue4 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object5);
                object2 = polyClassMachine.tm$34_get_typed(string, string3);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("left", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            String string = "right_power";
            String string4 = "int";
            if (scriptValue6 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object6);
                object = polyClassMachine.tm$34_get_typed(string, string4);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string4));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("right", scriptValue7);
        double d = 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)0.0);
        builder.val("result", scriptValue8);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)0.0))) {
            ScriptValue scriptValue9 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue5, (ScriptValue)scriptValue7);
            builder.val("result", scriptValue9);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)1.0))) {
            double d2 = scriptValue5.asNum() - scriptValue7.asNum();
            ScriptValue scriptValue10 = ScriptValue.of((double)d2);
            builder.val("result", scriptValue10);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)2.0))) {
            double d3 = scriptValue5.asNum() * scriptValue7.asNum();
            ScriptValue scriptValue11 = ScriptValue.of((double)d3);
            builder.val("result", scriptValue11);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)3.0))) {
            double d4;
            double d5 = ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)0.0)) ^ true ? Math.floor((d4 = scriptValue7.asNum()) == 0.0 ? 0.0 : scriptValue5.asNum() / d4) : 0.0;
            ScriptValue scriptValue12 = ScriptValue.of((double)d5);
            builder.val("result", scriptValue12);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)4.0))) {
            ScriptValue scriptValue13 = scriptValue5.asNum() < scriptValue7.asNum() ? scriptValue5 : scriptValue7;
            builder.val("result", scriptValue13);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)5.0))) {
            ScriptValue scriptValue14 = scriptValue5.asNum() > scriptValue7.asNum() ? scriptValue5 : scriptValue7;
            builder.val("result", scriptValue14);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)6.0))) {
            double d6;
            double d7 = ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)0.0)) ^ true ? ((d6 = scriptValue7.asNum()) == 0.0 ? 0.0 : scriptValue5.asNum() % d6) : 0.0;
            ScriptValue scriptValue15 = ScriptValue.of((double)d7);
            builder.val("result", scriptValue15);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)7.0))) {
            double d8 = Math.floor(Math.pow(scriptValue5.asNum(), scriptValue7.asNum()));
            ScriptValue scriptValue16 = ScriptValue.of((double)d8);
            builder.val("result", scriptValue16);
        }
        if (scriptContext.getNum("result") < 0.0) {
            double d9 = 0.0;
            ScriptValue scriptValue17 = ScriptValue.of((double)0.0);
            builder.val("result", scriptValue17);
        }
        if (scriptContext.getNum("result") > 15.0) {
            double d10 = 15.0;
            ScriptValue scriptValue18 = ScriptValue.of((double)15.0);
            builder.val("result", scriptValue18);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("result");
            if (scriptValue instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object7);
                v3 = ScriptValue.of((boolean)polyClassMachine.tm$108_emit_redstone(scriptValue19.asNum()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue19);
                v3 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
