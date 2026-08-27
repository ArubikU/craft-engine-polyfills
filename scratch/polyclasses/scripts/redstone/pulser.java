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
        ScriptValue.Obj obj;
        Object object2;
        Object object3;
        ScriptValue scriptValue2;
        Object object4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object5;
            String string = "duration";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object5 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object5 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object5);
                object4 = polyClassMachine_v3.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object4;
        builder.val("duration", scriptValue4);
        if (scriptValue4.asNum() <= 0.0) {
            double d = 10.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)10.0);
            builder.val("duration", scriptValue5);
        }
        if ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object6;
            String string = "prev_power";
            String string3 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object6 = (obj3 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object6 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object6);
                object3 = polyClassMachine_v3.tm$34_get_typed(string, string3);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object3;
        builder.val("prev", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        Object object7 = scriptValue7 != ScriptValue.NULL ? (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = object7.asNum() > 0.0 ? 1.0 : 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)d);
        builder.val("cur", scriptValue8);
        if (d == 1.0 && ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)0.0))) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj4;
                Object object8;
                String string = "countdown";
                String string4 = "int";
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("duration");
                if (scriptValue9 instanceof ScriptValue.Obj && (object8 = (obj4 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object8 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object8);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue10));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string4));
                    arrayList.add(scriptValue10);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj5;
            Object object9;
            String string = "countdown";
            String string5 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object9 = (obj5 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object9 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object9);
                object = polyClassMachine_v3.tm$34_get_typed(string, string5);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string5));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = object;
        builder.val("t", scriptValue11);
        if (scriptValue11.asNum() > 0.0) {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj6;
                Object object10;
                double d2 = 15.0;
                if (scriptValue12 instanceof ScriptValue.Obj && (object10 = (obj6 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object10 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object10);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v3.tm$108_emit_redstone(d2));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((double)d2));
                    v5 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj7;
                Object object11;
                String string = "countdown";
                String string6 = "int";
                ScriptValue scriptValue14 = ScriptValue.of((double)(scriptValue11.asNum() - 1.0));
                if (scriptValue13 instanceof ScriptValue.Obj && (object11 = (obj7 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object11 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object11);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string6, scriptValue14));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string6));
                    arrayList.add(scriptValue14);
                    v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj8;
                Object object12;
                double d3 = 0.0;
                if (scriptValue15 instanceof ScriptValue.Obj && (object12 = (obj8 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object12 instanceof PolyClass) && obj8.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object12);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v3.tm$108_emit_redstone(d3));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((double)d3));
                    v7 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
        if (scriptValue16 != ScriptValue.NULL) {
            ScriptValue.Obj obj9;
            Object object13;
            String string = "prev_power";
            String string7 = "int";
            ScriptValue scriptValue17 = ScriptValue.of((double)d);
            if (scriptValue16 instanceof ScriptValue.Obj && (object13 = (obj9 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object13 instanceof PolyClass) && obj9.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object13);
                v8 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string7, scriptValue17));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string7));
                arrayList.add(scriptValue17);
                v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v8 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
