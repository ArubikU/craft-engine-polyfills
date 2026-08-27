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
package dev.arubik.craftengine.script.gen.dump;

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
    public static ScriptValue _sailCount(ScriptContext.Builder builder) {
        ScriptContext scriptContext;
        ScriptValue scriptValue;
        ScriptContext scriptContext2 = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)0.0);
        builder.val("n", scriptValue2);
        List list = ScriptProgram.resolveForRows((String)"contraption.blocks()", (ScriptContext)builder.peek(), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("block", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptContext scriptContext3 = builder.peek();
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptValue scriptValue3 = scriptContext3.getClassInstance("block");
                if (scriptValue3 == ScriptValue.NULL) {
                    scriptValue3 = scriptContext3.getVar("block");
                }
                arrayList.add((ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue3, (ScriptContext)scriptContext3) : ScriptValue.NULL));
                arrayList.add(ScriptValue.of((String)"sail"));
                if (!ScriptFormula.callBuiltin((String)"contains", arrayList, (ScriptContext)scriptContext3).asBool()) continue;
                ScriptContext scriptContext4 = builder.peek();
                ScriptValue scriptValue4 = scriptContext4.getClassInstance("n");
                if (scriptValue4 == ScriptValue.NULL) {
                    scriptValue4 = scriptContext4.getVar("n");
                }
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("n", scriptValue5);
            }
        }
        if ((scriptValue = (scriptContext = builder.peek()).getClassInstance("n")) == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("n");
        }
        return scriptValue;
    }

    public static ScriptValue _advanceHeadAngle(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassInstance("Machine");
        if (scriptValue == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("Machine");
        }
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue2 = ScriptValue.of((String)"head_last_tick");
            ScriptValue scriptValue3 = ScriptValue.of((String)"int");
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                object = new PolyClassMachine(object2).tm$34_get_typed(scriptValue2.asStr(), scriptValue3.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                arrayList.add(scriptValue3);
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("last", scriptValue4);
        ScriptContext scriptContext2 = builder.peek();
        ArrayList arrayList = new ArrayList();
        ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"tick", arrayList, (ScriptContext)scriptContext2);
        builder.val("now", scriptValue5);
        ScriptContext scriptContext3 = builder.peek();
        if (scriptValue4.asNum() <= 0.0) {
            ScriptContext scriptContext4 = builder.peek();
            ScriptValue scriptValue6 = scriptContext4.getClassInstance("now");
            if (scriptValue6 == ScriptValue.NULL) {
                scriptValue6 = scriptContext4.getVar("now");
            }
            ScriptValue scriptValue7 = scriptValue6;
            builder.val("last", scriptValue7);
        }
        ScriptContext scriptContext5 = builder.peek();
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        ScriptValue scriptValue8 = scriptContext5.getClassInstance("now");
        if (scriptValue8 == ScriptValue.NULL) {
            scriptValue8 = scriptContext5.getVar("now");
        }
        double d = scriptValue8.asNum();
        ScriptValue scriptValue9 = scriptContext5.getClassInstance("last");
        if (scriptValue9 == ScriptValue.NULL) {
            scriptValue9 = scriptContext5.getVar("last");
        }
        arrayList2.add(ScriptValue.of((double)(d - scriptValue9.asNum())));
        arrayList2.add(ScriptValue.of((double)0.0));
        arrayList2.add(ScriptValue.of((double)100.0));
        ScriptValue scriptValue10 = ScriptFormula.callBuiltin((String)"clamp", arrayList2, (ScriptContext)scriptContext5);
        builder.val("elapsed", scriptValue10);
        ScriptContext scriptContext6 = builder.peek();
        ScriptValue scriptValue11 = scriptContext6.getClassInstance("Machine");
        if (scriptValue11 == ScriptValue.NULL) {
            scriptValue11 = scriptContext6.getVar("Machine");
        }
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            Object object4;
            ScriptValue scriptValue12 = ScriptValue.of((String)"head_angle");
            ScriptValue scriptValue13 = ScriptValue.of((String)"int");
            ScriptValue scriptValue14 = scriptContext6.getClassInstance("Machine");
            if (scriptValue14 == ScriptValue.NULL) {
                scriptValue14 = scriptContext6.getVar("Machine");
            }
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object5;
                ScriptValue scriptValue15 = ScriptValue.of((String)"head_angle");
                ScriptValue scriptValue16 = ScriptValue.of((String)"int");
                if (scriptValue14 instanceof ScriptValue.Obj && (object5 = (obj2 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object5 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                    object4 = new PolyClassMachine(object5).tm$34_get_typed(scriptValue15.asStr(), scriptValue16.asStr());
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue15);
                    arrayList3.add(scriptValue16);
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue14, arrayList3, (ScriptContext)scriptContext6);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            double d2 = scriptValue10.asNum();
            ScriptValue scriptValue17 = scriptContext6.getClassInstance("rpm_value");
            if (scriptValue17 == ScriptValue.NULL) {
                scriptValue17 = scriptContext6.getVar("rpm_value");
            }
            ScriptValue scriptValue18 = ScriptFormula.addPolymorphic((ScriptValue)object4, (ScriptValue)ScriptValue.of((double)(d2 * scriptValue17.asNum() * 0.3)));
            if (scriptValue11 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                v4 = ScriptValue.of((boolean)new PolyClassMachine(object3).tm$82_set_typed(scriptValue12.asStr(), scriptValue13.asStr(), scriptValue18));
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptValue12);
                arrayList4.add(scriptValue13);
                arrayList4.add(scriptValue18);
                v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue11, arrayList4, (ScriptContext)scriptContext6);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptContext scriptContext7 = builder.peek();
        ScriptValue scriptValue19 = scriptContext7.getClassInstance("Machine");
        if (scriptValue19 == ScriptValue.NULL) {
            scriptValue19 = scriptContext7.getVar("Machine");
        }
        if (scriptValue19 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue20 = ScriptValue.of((String)"head_last_tick");
            ScriptValue scriptValue21 = ScriptValue.of((String)"int");
            ScriptValue scriptValue22 = scriptContext7.getClassInstance("now");
            if (scriptValue22 == ScriptValue.NULL) {
                scriptValue22 = scriptContext7.getVar("now");
            }
            ScriptValue scriptValue23 = scriptValue22;
            if (scriptValue19 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                v5 = ScriptValue.of((boolean)new PolyClassMachine(object6).tm$82_set_typed(scriptValue20.asStr(), scriptValue21.asStr(), scriptValue23));
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(scriptValue20);
                arrayList5.add(scriptValue21);
                arrayList5.add(scriptValue23);
                v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue19, arrayList5, (ScriptContext)scriptContext7);
            }
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _stop(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassInstance("Machine");
        if (scriptValue == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("Machine");
        }
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                v0 = ScriptValue.of((boolean)new PolyClassMachine(object).tm$106_set_rpm_output(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptContext scriptContext2 = builder.peek();
        ScriptValue scriptValue2 = scriptContext2.getClassInstance("Machine");
        if (scriptValue2 == ScriptValue.NULL) {
            scriptValue2 = scriptContext2.getVar("Machine");
        }
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                v1 = ScriptValue.of((boolean)new PolyClassMachine(object).tm$56_report_su(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext2);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptContext scriptContext3 = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext3);
        builder2.val("rpm_value", ScriptValue.of((double)0.0));
        Windmill._advanceHeadAngle(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue _spinAxis(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassInstance("Machine");
        if (scriptValue == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("Machine");
        }
        Object object3 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine(object2).pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object3, (ScriptValue)ScriptValue.of((double)0.0)) ^ true) {
            ScriptContext scriptContext2 = builder.peek();
            return ScriptValue.of((String)"y");
        }
        ScriptContext scriptContext3 = builder.peek();
        ScriptValue scriptValue2 = scriptContext3.getClassInstance("Machine");
        if (scriptValue2 == ScriptValue.NULL) {
            scriptValue2 = scriptContext3.getVar("Machine");
        }
        Object object4 = scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine(object).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue2, (ScriptContext)scriptContext3)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object4, (ScriptValue)ScriptValue.of((double)0.0)) ^ true) {
            ScriptContext scriptContext4 = builder.peek();
            return ScriptValue.of((String)"x");
        }
        ScriptContext scriptContext5 = builder.peek();
        return ScriptValue.of((String)"z");
    }

    public static ScriptValue status(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassInstance("rpm");
        if (scriptValue == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("rpm");
        }
        return ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)0.0)) ^ true ? ScriptValue.of((String)"true") : ScriptValue.of((String)"false");
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassInstance("Machine");
        if (scriptValue == ScriptValue.NULL) {
            scriptValue = scriptContext.getVar("Machine");
        }
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                v0 = ScriptValue.of((boolean)new PolyClassMachine(object).tm$106_set_rpm_output(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptContext scriptContext2 = builder.peek();
        ScriptValue scriptValue2 = scriptContext2.getClassInstance("Machine");
        if (scriptValue2 == ScriptValue.NULL) {
            scriptValue2 = scriptContext2.getVar("Machine");
        }
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                v1 = ScriptValue.of((boolean)new PolyClassMachine(object).tm$56_report_su(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext2);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        double d;
        double d2;
        Object object;
        ScriptValue.Obj obj;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        double d3 = 8.0;
        ScriptValue scriptValue = ScriptValue.of((double)8.0);
        builder.val("SAILS_PER_RPM", scriptValue);
        ScriptContext scriptContext2 = builder.peek();
        double d4 = 16.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)16.0);
        builder.val("MAX_RPM", scriptValue2);
        ScriptContext scriptContext3 = builder.peek();
        double d5 = 8.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)8.0);
        builder.val("MIN_SAILS", scriptValue3);
        ScriptContext scriptContext4 = builder.peek();
        double d6 = 8.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)8.0);
        builder.val("CAPACITY_PER_RPM", scriptValue4);
        ScriptContext scriptContext5 = builder.peek();
        ScriptValue scriptValue5 = scriptContext5.getClassInstance("Machine");
        if (scriptValue5 == ScriptValue.NULL) {
            scriptValue5 = scriptContext5.getVar("Machine");
        }
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object4;
            ScriptValue scriptValue6 = ScriptValue.of((String)"assembled");
            ScriptValue scriptValue7 = ScriptValue.of((String)"int");
            if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj2 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                object3 = new PolyClassMachine(object4).tm$34_get_typed(scriptValue6.asStr(), scriptValue7.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue6);
                arrayList.add(scriptValue7);
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext5);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        if (object3.asNum() <= 0.0) {
            ScriptContext scriptContext6 = builder.peek();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext6);
            Windmill._stop(builder2);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptContext scriptContext7 = builder.peek();
        ScriptValue scriptValue8 = scriptContext7.getClassInstance("Machine");
        if (scriptValue8 == ScriptValue.NULL) {
            scriptValue8 = scriptContext7.getVar("Machine");
        }
        ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? (scriptValue8 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine(object2).pg$160_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue8, (ScriptContext)scriptContext7)) : ScriptValue.NULL;
        builder.val("contraption", scriptValue9);
        ScriptContext scriptContext8 = builder.peek();
        ScriptValue scriptValue10 = scriptContext8.getClassInstance("null");
        if (scriptValue10 == ScriptValue.NULL) {
            scriptValue10 = scriptContext8.getVar("null");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue9, (ScriptValue)scriptValue10)) {
            ScriptContext scriptContext9 = builder.peek();
            ScriptValue scriptValue11 = scriptContext9.getClassInstance("Machine");
            if (scriptValue11 == ScriptValue.NULL) {
                scriptValue11 = scriptContext9.getVar("Machine");
            }
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj3;
                Object object5;
                ScriptValue scriptValue12 = ScriptValue.of((String)"assembled");
                ScriptValue scriptValue13 = ScriptValue.of((String)"int");
                ScriptValue scriptValue14 = ScriptValue.of((double)0.0);
                if (scriptValue11 instanceof ScriptValue.Obj && (object5 = (obj3 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object5 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                    v1 = ScriptValue.of((boolean)new PolyClassMachine(object5).tm$82_set_typed(scriptValue12.asStr(), scriptValue13.asStr(), scriptValue14));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue12);
                    arrayList.add(scriptValue13);
                    arrayList.add(scriptValue14);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext9);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptContext scriptContext10 = builder.peek();
            ScriptValue scriptValue15 = scriptContext10.getClassInstance("Machine");
            if (scriptValue15 == ScriptValue.NULL) {
                scriptValue15 = scriptContext10.getVar("Machine");
            }
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj4;
                Object object6;
                ScriptValue scriptValue16 = ScriptValue.of((String)"contraption_uuid");
                ScriptValue scriptValue17 = ScriptValue.of((String)"string");
                ScriptValue scriptValue18 = ScriptValue.of((String)"");
                if (scriptValue15 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                    v2 = ScriptValue.of((boolean)new PolyClassMachine(object6).tm$82_set_typed(scriptValue16.asStr(), scriptValue17.asStr(), scriptValue18));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue16);
                    arrayList.add(scriptValue17);
                    arrayList.add(scriptValue18);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext10);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptContext scriptContext11 = builder.peek();
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext11);
            Windmill._stop(builder3);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptContext scriptContext12 = builder.peek();
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext12);
        ScriptValue scriptValue19 = scriptContext12.getClassInstance("contraption");
        if (scriptValue19 == ScriptValue.NULL) {
            scriptValue19 = scriptContext12.getVar("contraption");
        }
        builder4.val("contraption", scriptValue19);
        ScriptValue scriptValue20 = Windmill._sailCount(builder4);
        builder.val("sails", scriptValue20);
        ScriptContext scriptContext13 = builder.peek();
        double d7 = scriptValue20.asNum();
        ScriptValue scriptValue21 = scriptContext13.getClassInstance("MIN_SAILS");
        if (scriptValue21 == ScriptValue.NULL) {
            scriptValue21 = scriptContext13.getVar("MIN_SAILS");
        }
        if (d7 < scriptValue21.asNum()) {
            ScriptContext scriptContext14 = builder.peek();
            ScriptValue scriptValue22 = scriptContext14.getClassInstance("contraption");
            if (scriptValue22 == ScriptValue.NULL) {
                scriptValue22 = scriptContext14.getVar("contraption");
            }
            if (scriptValue22 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext14);
                arrayList.add(Windmill._spinAxis(builder5));
                arrayList.add(ScriptValue.of((double)0.0));
                v4 = PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue22, arrayList, (ScriptContext)scriptContext14);
            } else {
                v4 = ScriptValue.NULL;
            }
            ScriptContext scriptContext15 = builder.peek();
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext15);
            Windmill._stop(builder6);
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptContext scriptContext16 = builder.peek();
        ScriptValue scriptValue23 = scriptContext16.getClassInstance("Machine");
        if (scriptValue23 == ScriptValue.NULL) {
            scriptValue23 = scriptContext16.getVar("Machine");
        }
        if (scriptValue23 != ScriptValue.NULL) {
            ScriptValue.Obj obj5;
            Object object7;
            ScriptValue scriptValue24 = ScriptValue.of((String)"windmill_dir");
            ScriptValue scriptValue25 = ScriptValue.of((String)"int");
            if (scriptValue23 instanceof ScriptValue.Obj && (object7 = (obj5 = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object7 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                object = new PolyClassMachine(object7).tm$34_get_typed(scriptValue24.asStr(), scriptValue25.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue24);
                arrayList.add(scriptValue25);
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue23, arrayList, (ScriptContext)scriptContext16);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue26 = object;
        builder.val("dir", scriptValue26);
        ScriptContext scriptContext17 = builder.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((double)0.0))) {
            ScriptContext scriptContext18 = builder.peek();
            double d8 = 1.0;
            ScriptValue scriptValue27 = ScriptValue.of((double)1.0);
            builder.val("dir", scriptValue27);
        }
        ScriptContext scriptContext19 = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue28 = scriptContext19.getClassInstance("SAILS_PER_RPM");
        if (scriptValue28 == ScriptValue.NULL) {
            scriptValue28 = scriptContext19.getVar("SAILS_PER_RPM");
        }
        if ((d2 = scriptValue28.asNum()) == 0.0) {
            d = 0.0;
        } else {
            ScriptValue scriptValue29 = scriptContext19.getClassInstance("sails");
            if (scriptValue29 == ScriptValue.NULL) {
                scriptValue29 = scriptContext19.getVar("sails");
            }
            d = scriptValue29.asNum() / d2;
        }
        arrayList.add(ScriptValue.of((double)Math.floor(d)));
        arrayList.add(ScriptValue.of((double)1.0));
        ScriptValue scriptValue30 = scriptContext19.getClassInstance("MAX_RPM");
        if (scriptValue30 == ScriptValue.NULL) {
            scriptValue30 = scriptContext19.getVar("MAX_RPM");
        }
        arrayList.add(scriptValue30);
        ScriptValue scriptValue31 = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext19);
        builder.val("speed", scriptValue31);
        ScriptContext scriptContext20 = builder.peek();
        double d9 = scriptValue31.asNum();
        ScriptValue scriptValue32 = scriptContext20.getClassInstance("dir");
        if (scriptValue32 == ScriptValue.NULL) {
            scriptValue32 = scriptContext20.getVar("dir");
        }
        double d10 = d9 * scriptValue32.asNum();
        ScriptValue scriptValue33 = ScriptValue.of((double)d10);
        builder.val("rpm_out", scriptValue33);
        ScriptContext scriptContext21 = builder.peek();
        ScriptValue scriptValue34 = scriptContext21.getClassInstance("contraption");
        if (scriptValue34 == ScriptValue.NULL) {
            scriptValue34 = scriptContext21.getVar("contraption");
        }
        if (scriptValue34 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext21);
            arrayList2.add(Windmill._spinAxis(builder7));
            arrayList2.add(ScriptValue.of((double)d10));
            v8 = PolyDispatch.bootstrapCall("memberCall", "set_spin", (ScriptValue)scriptValue34, arrayList2, (ScriptContext)scriptContext21);
        } else {
            v8 = ScriptValue.NULL;
        }
        ScriptContext scriptContext22 = builder.peek();
        ScriptValue scriptValue35 = scriptContext22.getClassInstance("Machine");
        if (scriptValue35 == ScriptValue.NULL) {
            scriptValue35 = scriptContext22.getVar("Machine");
        }
        if (scriptValue35 != ScriptValue.NULL) {
            ScriptValue.Obj obj6;
            Object object8;
            double d11 = d10;
            if (scriptValue35 instanceof ScriptValue.Obj && (object8 = (obj6 = (ScriptValue.Obj)scriptValue35).instance()) != null && !(object8 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                v9 = ScriptValue.of((boolean)new PolyClassMachine(object8).tm$106_set_rpm_output(d11));
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((double)d11));
                v9 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue35, arrayList3, (ScriptContext)scriptContext22);
            }
        } else {
            v9 = ScriptValue.NULL;
        }
        ScriptContext scriptContext23 = builder.peek();
        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext23);
        builder8.val("rpm_value", ScriptValue.of((double)d10));
        Windmill._advanceHeadAngle(builder8);
        ScriptContext scriptContext24 = builder.peek();
        double d12 = scriptValue31.asNum();
        ScriptValue scriptValue36 = scriptContext24.getClassInstance("CAPACITY_PER_RPM");
        if (scriptValue36 == ScriptValue.NULL) {
            scriptValue36 = scriptContext24.getVar("CAPACITY_PER_RPM");
        }
        double d13 = d12 * scriptValue36.asNum();
        ScriptValue scriptValue37 = ScriptValue.of((double)d13);
        builder.val("su", scriptValue37);
        ScriptContext scriptContext25 = builder.peek();
        ScriptValue scriptValue38 = scriptContext25.getClassInstance("Machine");
        if (scriptValue38 == ScriptValue.NULL) {
            scriptValue38 = scriptContext25.getVar("Machine");
        }
        if (scriptValue38 != ScriptValue.NULL) {
            ScriptValue.Obj obj7;
            Object object9;
            double d14 = -d13;
            if (scriptValue38 instanceof ScriptValue.Obj && (object9 = (obj7 = (ScriptValue.Obj)scriptValue38).instance()) != null && !(object9 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                v11 = ScriptValue.of((boolean)new PolyClassMachine(object9).tm$56_report_su(d14));
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((double)d14));
                v11 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue38, arrayList4, (ScriptContext)scriptContext25);
            }
        } else {
            v11 = ScriptValue.NULL;
        }
    }
}
