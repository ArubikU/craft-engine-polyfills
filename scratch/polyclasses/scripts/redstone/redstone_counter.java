/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class RedstoneCounter {
    public static void run(ScriptContext.Builder builder) {
        block35: {
            Object object;
            ScriptValue scriptValue;
            Object object2;
            Object object3;
            ScriptValue.Obj obj;
            Object object4;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
            Object object5 = scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object4).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d = object5.asNum() > 0.0 ? 1.0 : 0.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)d);
            builder.val("cur_power", scriptValue3);
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object6;
                String string = "prev_power";
                String string2 = "int";
                if (scriptValue4 instanceof ScriptValue.Obj && (object6 = (obj2 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object6 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object6);
                    object3 = polyClassMachine_v4.tm$34_get_typed(string, string2);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string2));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = object3;
            builder.val("prev_power", scriptValue5);
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj3;
                Object object7;
                String string = "prev_power";
                String string3 = "int";
                ScriptValue scriptValue7 = ScriptValue.of((double)d);
                if (scriptValue6 instanceof ScriptValue.Obj && (object7 = (obj3 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object7 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object7);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue7));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue7);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            if (!(d > 0.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)0.0)))) break block35;
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj4;
                Object object8;
                String string = "max_count";
                String string4 = "int";
                if (scriptValue8 instanceof ScriptValue.Obj && (object8 = (obj4 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object8 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object8);
                    object2 = polyClassMachine_v4.tm$34_get_typed(string, string4);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string4));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = object2;
            builder.val("max_count", scriptValue9);
            if (scriptValue9.asNum() <= 0.0) {
                double d2 = 10.0;
                ScriptValue scriptValue10 = ScriptValue.of((double)10.0);
                builder.val("max_count", scriptValue10);
            }
            if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj5;
                Object object9;
                String string = "count";
                String string5 = "int";
                if (scriptValue instanceof ScriptValue.Obj && (object9 = (obj5 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object9 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object9);
                    object = polyClassMachine_v4.tm$34_get_typed(string, string5);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string5));
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = ScriptFormula.addPolymorphic((ScriptValue)object, (ScriptValue)ScriptValue.of((double)1.0));
            builder.val("count", scriptValue11);
            if (scriptValue11.asNum() >= scriptContext.getNum("max_count")) {
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                if (scriptValue12 != ScriptValue.NULL) {
                    ScriptValue.Obj obj6;
                    Object object10;
                    String string = "count";
                    String string6 = "int";
                    ScriptValue scriptValue13 = ScriptValue.of((double)0.0);
                    if (scriptValue12 instanceof ScriptValue.Obj && (object10 = (obj6 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object10 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object10);
                        v5 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string6, scriptValue13));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string6));
                        arrayList.add(scriptValue13);
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                if (scriptValue14 != ScriptValue.NULL) {
                    ScriptValue.Obj obj7;
                    Object object11;
                    double d3 = 15.0;
                    if (scriptValue14 instanceof ScriptValue.Obj && (object11 = (obj7 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object11 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object11);
                        v6 = ScriptValue.of((boolean)polyClassMachine_v4.tm$108_emit_redstone(d3));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((double)d3));
                        v6 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v6 = ScriptValue.NULL;
                }
            } else {
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                if (scriptValue15 != ScriptValue.NULL) {
                    ScriptValue.Obj obj8;
                    Object object12;
                    String string = "count";
                    String string7 = "int";
                    ScriptValue scriptValue16 = scriptValue11;
                    if (scriptValue15 instanceof ScriptValue.Obj && (object12 = (obj8 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object12 instanceof PolyClass) && obj8.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object12);
                        v7 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string7, scriptValue16));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string7));
                        arrayList.add(scriptValue16);
                        v7 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v7 = ScriptValue.NULL;
                }
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
                if (scriptValue17 != ScriptValue.NULL) {
                    ScriptValue.Obj obj9;
                    Object object13;
                    double d4 = 0.0;
                    if (scriptValue17 instanceof ScriptValue.Obj && (object13 = (obj9 = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object13 instanceof PolyClass) && obj9.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object13);
                        v8 = ScriptValue.of((boolean)polyClassMachine_v4.tm$108_emit_redstone(d4));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((double)d4));
                        v8 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue17, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v8 = ScriptValue.NULL;
                }
            }
        }
    }
}
