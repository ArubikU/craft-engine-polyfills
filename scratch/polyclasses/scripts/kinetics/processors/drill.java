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
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class Drill {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptValue.Obj obj3;
        Object object3;
        ScriptValue.Obj obj4;
        Object object4;
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_deposit");
        arrayList.add("_linear_break_speed");
        arrayList.add("_update_activated");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"kinetics/utils.pf", null, arrayList);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object4 = (obj4 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj4.typeName().equals("Machine") ? new PolyClassMachine_v3(object4).pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v3(object3).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(scriptValue3 != ScriptValue.NULL ? (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        builder.val("_hold_key", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object).pg$160_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("contraption", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            Object object5;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("contraption");
            boolean bl = ScriptFormula.valuesEqual((ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.0)) ^ true;
            ScriptValue scriptValue8 = ScriptValue.of((boolean)bl);
            builder.val("is_rotational", scriptValue8);
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("contraption");
            if (scriptValue9 != ScriptValue.NULL) {
                ArrayList arrayList2 = new ArrayList();
                object5 = PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)scriptValue9, arrayList2, (ScriptContext)scriptContext);
            } else {
                object5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = object5;
            builder.val("is_linear", scriptValue10);
            double d = bl || scriptValue10.asBool() ? 1.0 : 0.0;
            ScriptValue scriptValue11 = ScriptValue.of((double)d);
            builder.val("is_now", scriptValue11);
            if (d > 0.0) {
                ScriptValue scriptValue12;
                ScriptValue.Obj obj5;
                Object object6;
                ScriptValue.Obj obj6;
                Object object7;
                ScriptValue.Obj obj7;
                Object object8;
                ScriptValue.Obj obj8;
                Object object9;
                ScriptValue.Obj obj9;
                Object object10;
                ScriptValue.Obj obj10;
                Object object11;
                ScriptValue scriptValue13;
                if (bl) {
                    ScriptValue scriptValue14;
                    double d2 = 10.0;
                    scriptValue13 = ScriptValue.of((double)Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(((scriptValue14 = scriptContext.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum()) / d2));
                } else {
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder2.val("contraption", scriptValue6);
                    scriptValue13 = Utils.1._linearBreakSpeed((ScriptContext.Builder)builder2);
                }
                ScriptValue scriptValue15 = scriptValue13;
                builder.val("speed", scriptValue15);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
                arrayList3.add(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue16 != ScriptValue.NULL ? (scriptValue16 instanceof ScriptValue.Obj && (object11 = (obj10 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object11 instanceof PolyClass) && obj10.typeName().equals("Machine") ? new PolyClassMachine_v3(object11).pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue16, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue17 != ScriptValue.NULL ? (scriptValue17 instanceof ScriptValue.Obj && (object10 = (obj9 = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object10 instanceof PolyClass) && obj9.typeName().equals("Machine") ? new PolyClassMachine_v3(object10).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue17, (ScriptContext)scriptContext)) : ScriptValue.NULL)));
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
                arrayList3.add(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue18 != ScriptValue.NULL ? (scriptValue18 instanceof ScriptValue.Obj && (object9 = (obj8 = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object9 instanceof PolyClass) && obj8.typeName().equals("Machine") ? new PolyClassMachine_v3(object9).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue19 != ScriptValue.NULL ? (scriptValue19 instanceof ScriptValue.Obj && (object8 = (obj7 = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object8 instanceof PolyClass) && obj7.typeName().equals("Machine") ? new PolyClassMachine_v3(object8).pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue19, (ScriptContext)scriptContext)) : ScriptValue.NULL)));
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                arrayList3.add(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue20 != ScriptValue.NULL ? (scriptValue20 instanceof ScriptValue.Obj && (object7 = (obj6 = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object7 instanceof PolyClass) && obj6.typeName().equals("Machine") ? new PolyClassMachine_v3(object7).pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue20, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue21 != ScriptValue.NULL ? (scriptValue21 instanceof ScriptValue.Obj && (object6 = (obj5 = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object6 instanceof PolyClass) && obj5.typeName().equals("Machine") ? new PolyClassMachine_v3(object6).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL)));
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("contraption");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue22, (ScriptContext)scriptContext) : ScriptValue.NULL), arrayList3, (ScriptContext)scriptContext);
                builder.val("target", (ScriptValue)callSite);
                if (ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null")) || ((scriptValue12 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("contraption");
                    if (scriptValue23 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                        arrayList4.add(scriptValue4);
                        v3 = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue23, arrayList4, (ScriptContext)scriptContext);
                    } else {
                        v3 = ScriptValue.NULL;
                    }
                } else {
                    ScriptValue scriptValue24;
                    Object object12;
                    ScriptValue scriptValue25 = scriptContext.getClassOrVar("contraption");
                    if (scriptValue25 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                        arrayList5.add(scriptValue4);
                        v4 = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)scriptValue25, arrayList5, (ScriptContext)scriptContext);
                    } else {
                        v4 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue26 != ScriptValue.NULL) {
                        ScriptValue.Obj obj11;
                        Object object13;
                        CallSite callSite2 = callSite;
                        ScriptValue scriptValue27 = scriptValue15;
                        if (scriptValue26 instanceof ScriptValue.Obj && (object13 = (obj11 = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object13 instanceof PolyClass) && obj11.typeName().equals("Machine")) {
                            PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object13);
                            object12 = polyClassMachine_v3.tm$2_tick_break((ScriptValue)callSite2, scriptValue27.asNum());
                        } else {
                            ArrayList<CallSite> arrayList6 = new ArrayList<CallSite>();
                            arrayList6.add(callSite2);
                            arrayList6.add((CallSite)scriptValue27);
                            object12 = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue26, arrayList6, (ScriptContext)scriptContext);
                        }
                    } else {
                        object12 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue28 = object12;
                    builder.val("result", scriptValue28);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue28, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                        ScriptValue scriptValue29;
                        List list = ScriptProgram.rowsOf((ScriptValue)scriptValue28, (int)1);
                        if (list != null) {
                            for (ScriptValue[] scriptValueArray : list) {
                                builder.val("item", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                                builder3.val("item", scriptContext.getClassOrVar("item"));
                                Utils.1._deposit((ScriptContext.Builder)builder3);
                            }
                        }
                        if ((scriptValue29 = scriptContext.getClassOrVar("contraption")) != ScriptValue.NULL) {
                            ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                            arrayList7.add(scriptValue4);
                            v6 = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue29, arrayList7, (ScriptContext)scriptContext);
                        } else {
                            v6 = ScriptValue.NULL;
                        }
                    }
                    if ((scriptValue24 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        ScriptValue.Obj obj12;
                        Object object14;
                        ScriptValue scriptValue30 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)8.0), (ScriptValue)scriptValue15);
                        if (scriptValue24 instanceof ScriptValue.Obj && (object14 = (obj12 = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object14 instanceof PolyClass) && obj12.typeName().equals("Machine")) {
                            PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object14);
                            v7 = ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(scriptValue30.asNum()));
                        } else {
                            ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                            arrayList8.add(scriptValue30);
                            v7 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue24, arrayList8, (ScriptContext)scriptContext);
                        }
                    } else {
                        v7 = ScriptValue.NULL;
                    }
                }
            } else {
                ScriptValue scriptValue31 = scriptContext.getClassOrVar("contraption");
                if (scriptValue31 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(scriptValue4);
                    v8 = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue31, arrayList9, (ScriptContext)scriptContext);
                } else {
                    v8 = ScriptValue.NULL;
                }
            }
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)ScriptValue.of((double)0.0))) {
            double d = 0.0;
            ScriptValue scriptValue32 = ScriptValue.of((double)0.0);
            builder.val("is_now", scriptValue32);
        } else {
            ScriptValue scriptValue33;
            List list;
            Object object15;
            double d = 10.0;
            double d3 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(scriptContext.getNum("rpm")) / d);
            ScriptValue scriptValue34 = ScriptValue.of((double)d3);
            builder.val("speed", scriptValue34);
            ScriptValue scriptValue35 = scriptContext.getClassOrVar("Machine");
            if (scriptValue35 != ScriptValue.NULL) {
                ScriptValue.Obj obj13;
                Object object16;
                ScriptValue.Obj obj14;
                Object object17;
                ScriptValue scriptValue36 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue37 = scriptValue36 != ScriptValue.NULL ? (scriptValue36 instanceof ScriptValue.Obj && (object17 = (obj14 = (ScriptValue.Obj)scriptValue36).instance()) != null && !(object17 instanceof PolyClass) && obj14.typeName().equals("Machine") ? new PolyClassMachine_v3(object17).pg$128_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue36, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d4 = d3;
                if (scriptValue35 instanceof ScriptValue.Obj && (object16 = (obj13 = (ScriptValue.Obj)scriptValue35).instance()) != null && !(object16 instanceof PolyClass) && obj13.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object16);
                    object15 = polyClassMachine_v3.tm$2_tick_break(scriptValue37, d4);
                } else {
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add(scriptValue37);
                    arrayList10.add(ScriptValue.of((double)d4));
                    object15 = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue35, arrayList10, (ScriptContext)scriptContext);
                }
            } else {
                object15 = ScriptValue.NULL;
            }
            ScriptValue scriptValue38 = object15;
            builder.val("result", scriptValue38);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue38, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && (list = ScriptProgram.rowsOf((ScriptValue)scriptValue38, (int)1)) != null) {
                for (ScriptValue[] scriptValueArray : list) {
                    builder.val("item", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                    ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder4.val("item", scriptContext.getClassOrVar("item"));
                    Utils.1._deposit((ScriptContext.Builder)builder4);
                }
            }
            if ((scriptValue33 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj15;
                Object object18;
                double d5 = 8.0 + d3;
                if (scriptValue33 instanceof ScriptValue.Obj && (object18 = (obj15 = (ScriptValue.Obj)scriptValue33).instance()) != null && !(object18 instanceof PolyClass) && obj15.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object18);
                    v10 = ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d5));
                } else {
                    ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                    arrayList11.add(ScriptValue.of((double)d5));
                    v10 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue33, arrayList11, (ScriptContext)scriptContext);
                }
            } else {
                v10 = ScriptValue.NULL;
            }
            double d6 = 1.0;
            ScriptValue scriptValue39 = ScriptValue.of((double)1.0);
            builder.val("is_now", scriptValue39);
        }
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder5.val("act_key", ScriptValue.of((String)"_drill_act"));
        builder5.val("is_now", scriptContext.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)builder5);
        FILE_SCOPE = builder.build();
    }
}
