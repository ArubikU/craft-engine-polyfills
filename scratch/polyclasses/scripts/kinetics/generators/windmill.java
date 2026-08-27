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
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

public final class Windmill {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _sailCount(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("n", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("contraption");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList arrayList = new ArrayList();
            object = PolyDispatch.bootstrapCall("memberCall", "blocks", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.rowsOf((ScriptValue)object, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("block", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("block");
                arrayList.add((ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL));
                arrayList.add(ScriptValue.of((String)"sail"));
                if (!ScriptFormula.callBuiltin((String)"contains", arrayList, (ScriptContext)scriptContext).asBool()) continue;
                ScriptValue scriptValue4 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("n"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("n", scriptValue4);
            }
        }
        return scriptContext.getClassOrVar("n");
    }

    public static ScriptValue _advanceHeadAngle(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "head_last_tick";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("last", scriptValue2);
        ArrayList arrayList = new ArrayList();
        ScriptValue scriptValue3 = ScriptFormula.callBuiltin((String)"tick", arrayList, (ScriptContext)scriptContext);
        builder.val("now", scriptValue3);
        if (scriptValue2.asNum() <= 0.0) {
            ScriptValue scriptValue4 = scriptValue3;
            builder.val("last", scriptValue4);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(ScriptValue.of((double)(scriptValue3.asNum() - scriptContext.getNum("last"))));
        arrayList2.add(ScriptValue.of((double)0.0));
        arrayList2.add(ScriptValue.of((double)100.0));
        ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"clamp", arrayList2, (ScriptContext)scriptContext);
        builder.val("elapsed", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            Object object4;
            String string = "head_angle";
            String string3 = "int";
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object5;
                String string4 = "head_angle";
                String string5 = "int";
                if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj2 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object5);
                    object4 = polyClassMachine.tm$34_get_typed(string4, string5);
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((String)string4));
                    arrayList3.add(ScriptValue.of((String)string5));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, arrayList3, (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = ScriptFormula.addPolymorphic((ScriptValue)object4, (ScriptValue)ScriptValue.of((double)(scriptValue5.asNum() * scriptContext.getNum("rpm_value") * 0.3)));
            if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue8));
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)string));
                arrayList4.add(ScriptValue.of((String)string3));
                arrayList4.add(scriptValue8);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            String string = "head_last_tick";
            String string6 = "int";
            ScriptValue scriptValue10 = scriptValue3;
            if (scriptValue9 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object6);
                v3 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string6, scriptValue10));
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptValue.of((String)string));
                arrayList5.add(ScriptValue.of((String)string6));
                arrayList5.add(scriptValue10);
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _stop(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("rpm_value", ScriptValue.of((double)0.0));
        Windmill._advanceHeadAngle(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue _spinAxis(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        PolyClassMachine polyClassMachine2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        Object object = scriptValue != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine2.pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object, (ScriptValue)ScriptValue.of((double)0.0)) ^ true) {
            return ScriptValue.of((String)"y");
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        Object object2 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine.pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object2, (ScriptValue)ScriptValue.of((double)0.0)) ^ true) {
            return ScriptValue.of((String)"x");
        }
        return ScriptValue.of((String)"z");
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)ScriptValue.of((double)0.0)) ^ true ? ScriptValue.of((String)"true") : ScriptValue.of((String)"false");
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        Object object;
        PolyClassMachine polyClassMachine;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        double d = 8.0;
        ScriptValue scriptValue = ScriptValue.of((double)8.0);
        builder.val("SAILS_PER_RPM", scriptValue);
        double d2 = 16.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)16.0);
        builder.val("MAX_RPM", scriptValue2);
        double d3 = 8.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)8.0);
        builder.val("MIN_SAILS", scriptValue3);
        double d4 = 8.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)8.0);
        builder.val("CAPACITY_PER_RPM", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "assembled";
            String string2 = "int";
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object3);
                object2 = polyClassMachine2.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        if (object2.asNum() <= 0.0) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            Windmill._stop(builder2);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine.pg$160_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("contraption", scriptValue7);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "assembled";
                String string3 = "int";
                ScriptValue scriptValue9 = ScriptValue.of((double)0.0);
                if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine3 = new PolyClassMachine(object4);
                    v1 = ScriptValue.of((boolean)polyClassMachine3.tm$82_set_typed(string, string3, scriptValue9));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    arrayList.add(scriptValue9);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                String string = "contraption_uuid";
                String string4 = "string";
                ScriptValue scriptValue11 = ScriptValue.of((String)"");
                if (scriptValue10 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine4 = new PolyClassMachine(object5);
                    v2 = ScriptValue.of((boolean)polyClassMachine4.tm$82_set_typed(string, string4, scriptValue11));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string4));
                    arrayList.add(scriptValue11);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            Windmill._stop(builder3);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("contraption", scriptValue7);
        ScriptValue scriptValue12 = Windmill._sailCount(builder4);
        builder.val("sails", scriptValue12);
        if (scriptValue12.asNum() < d3) {
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("contraption");
            if (scriptValue13 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                arrayList.add(Windmill._spinAxis(builder5));
                arrayList.add(ScriptValue.of((double)0.0));
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            Windmill._stop(builder6);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
        if (scriptValue14 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            String string = "windmill_dir";
            String string5 = "int";
            if (scriptValue14 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine5 = new PolyClassMachine(object6);
                object = polyClassMachine5.tm$34_get_typed(string, string5);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string5));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = object;
        builder.val("dir", scriptValue15);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)0.0))) {
            double d5 = 1.0;
            ScriptValue scriptValue16 = ScriptValue.of((double)1.0);
            builder.val("dir", scriptValue16);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        double d6 = d;
        arrayList.add(ScriptValue.of((double)Math.floor(d6 == 0.0 ? 0.0 : scriptValue12.asNum() / d6)));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((double)d2));
        ScriptValue scriptValue17 = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext);
        builder.val("speed", scriptValue17);
        double d7 = scriptValue17.asNum() * scriptContext.getNum("dir");
        ScriptValue scriptValue18 = ScriptValue.of((double)d7);
        builder.val("rpm_out", scriptValue18);
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("contraption");
        if (scriptValue19 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            arrayList2.add(Windmill._spinAxis(builder7));
            arrayList2.add(ScriptValue.of((double)d7));
            v5 = PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue19, arrayList2, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
        if (scriptValue20 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            double d8 = d7;
            if (scriptValue20 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine6 = new PolyClassMachine(object7);
                v6 = ScriptValue.of((boolean)polyClassMachine6.tm$106_set_rpm_output(d8));
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((double)d8));
                v6 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue20, arrayList3, (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
        builder8.val("rpm_value", ScriptValue.of((double)d7));
        Windmill._advanceHeadAngle(builder8);
        double d9 = scriptValue17.asNum() * d4;
        ScriptValue scriptValue21 = ScriptValue.of((double)d9);
        builder.val("su", scriptValue21);
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
        if (scriptValue22 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            double d10 = -d9;
            if (scriptValue22 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine7 = new PolyClassMachine(object8);
                v7 = ScriptValue.of((boolean)polyClassMachine7.tm$56_report_su(d10));
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((double)d10));
                v7 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue22, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            v7 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
