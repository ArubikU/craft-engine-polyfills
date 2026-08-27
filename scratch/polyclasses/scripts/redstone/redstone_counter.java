/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class RedstoneCounter {
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
        ScriptValue.Obj obj;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        Object object3 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = object3.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)d);
        builder.val("cur_power", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object4;
            String string = "prev_power";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object4);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("prev_power", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object5;
            String string = "prev_power";
            String string3 = "int";
            ScriptValue scriptValue6 = ScriptValue.of((double)d);
            if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj3 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object5);
                v2 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue6));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue6);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        if (d > 0.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)0.0))) {
            Object object6;
            ScriptValue scriptValue7;
            Object object7;
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj4;
                Object object8;
                String string = "max_count";
                String string4 = "int";
                if (scriptValue8 instanceof ScriptValue.Obj && (object8 = (obj4 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object8 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object8);
                    object7 = polyClassMachine_v3.tm$34_get_typed(string, string4);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string4));
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object7 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = object7;
            builder.val("max_count", scriptValue9);
            if (scriptValue9.asNum() <= 0.0) {
                double d2 = 10.0;
                ScriptValue scriptValue10 = ScriptValue.of((double)10.0);
                builder.val("max_count", scriptValue10);
            }
            if ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj5;
                Object object9;
                String string = "count";
                String string5 = "int";
                if (scriptValue7 instanceof ScriptValue.Obj && (object9 = (obj5 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object9 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object9);
                    object6 = polyClassMachine_v3.tm$34_get_typed(string, string5);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string5));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object6 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = ScriptFormula.addPolymorphic((ScriptValue)object6, (ScriptValue)ScriptValue.of((double)1.0));
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
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object10);
                        v5 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string6, scriptValue13));
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
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object11);
                        v6 = ScriptValue.of((boolean)polyClassMachine_v3.tm$108_emit_redstone(d3));
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
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object12);
                        v7 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string7, scriptValue16));
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
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object13);
                        v8 = ScriptValue.of((boolean)polyClassMachine_v3.tm$108_emit_redstone(d4));
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
        FILE_SCOPE = builder.build();
    }
}
