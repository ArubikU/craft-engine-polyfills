/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class PulseTrigger {
    public static void run(ScriptContext.Builder builder) {
        Object object;
        ScriptValue scriptValue;
        ScriptValue.Obj obj;
        Object object2;
        Object object3;
        Object object4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object5;
            String string = "edge_mode";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object5 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object5 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object5);
                object4 = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object4;
        builder.val("mode", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object6;
            String string = "prev_power";
            String string3 = "int";
            if (scriptValue4 instanceof ScriptValue.Obj && (object6 = (obj3 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object6 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object6);
                object3 = polyClassMachine_v2.tm$34_get_typed(string, string3);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object3;
        builder.val("prev", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        Object object7 = scriptValue6 != ScriptValue.NULL ? (scriptValue6 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object2).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = object7.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue7 = ScriptValue.of((double)d);
        builder.val("cur", scriptValue7);
        double d2 = 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)0.0);
        builder.val("fire", scriptValue8);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)0.0)) && d == 1.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)0.0))) {
            double d3 = 1.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)1.0);
            builder.val("fire", scriptValue9);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)1.0)) && d == 0.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)1.0))) {
            double d4 = 1.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)1.0);
            builder.val("fire", scriptValue10);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)2.0)) && ScriptFormula.valuesEqual((ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue5) ^ true) {
            double d5 = 1.0;
            ScriptValue scriptValue11 = ScriptValue.of((double)1.0);
            builder.val("fire", scriptValue11);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("fire"), (ScriptValue)ScriptValue.of((double)1.0))) {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj4;
                Object object8;
                String string = "pulse_t";
                String string4 = "int";
                ScriptValue scriptValue13 = ScriptValue.of((double)2.0);
                if (scriptValue12 instanceof ScriptValue.Obj && (object8 = (obj4 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object8 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object8);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string4, scriptValue13));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string4));
                    arrayList.add(scriptValue13);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj5;
            Object object9;
            String string = "pulse_t";
            String string5 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object9 = (obj5 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object9 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object9);
                object = polyClassMachine_v2.tm$34_get_typed(string, string5);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string5));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = object;
        builder.val("t", scriptValue14);
        if (scriptValue14.asNum() > 0.0) {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj6;
                Object object10;
                double d6 = 15.0;
                if (scriptValue15 instanceof ScriptValue.Obj && (object10 = (obj6 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object10 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object10);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v2.tm$108_emit_redstone(d6));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((double)d6));
                    v5 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue.Obj obj7;
                Object object11;
                String string = "pulse_t";
                String string6 = "int";
                ScriptValue scriptValue17 = ScriptValue.of((double)(scriptValue14.asNum() - 1.0));
                if (scriptValue16 instanceof ScriptValue.Obj && (object11 = (obj7 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object11 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object11);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string6, scriptValue17));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string6));
                    arrayList.add(scriptValue17);
                    v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
            if (scriptValue18 != ScriptValue.NULL) {
                ScriptValue.Obj obj8;
                Object object12;
                double d7 = 0.0;
                if (scriptValue18 instanceof ScriptValue.Obj && (object12 = (obj8 = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object12 instanceof PolyClass) && obj8.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object12);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v2.tm$108_emit_redstone(d7));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((double)d7));
                    v7 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue18, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
        if (scriptValue19 != ScriptValue.NULL) {
            ScriptValue.Obj obj9;
            Object object13;
            String string = "prev_power";
            String string7 = "int";
            ScriptValue scriptValue20 = ScriptValue.of((double)d);
            if (scriptValue19 instanceof ScriptValue.Obj && (object13 = (obj9 = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object13 instanceof PolyClass) && obj9.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object13);
                v8 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string7, scriptValue20));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string7));
                arrayList.add(scriptValue20);
                v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v8 = ScriptValue.NULL;
        }
    }
}
