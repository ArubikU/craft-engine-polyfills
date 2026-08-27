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
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
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
        PolyClassMachine polyClassMachine;
        PolyClassMachine polyClassMachine2;
        PolyClassMachine polyClassMachine3;
        PolyClassMachine polyClassMachine4;
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_deposit");
        arrayList.add("_linear_break_speed");
        arrayList.add("_update_activated");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"kinetics/utils.pf", null, arrayList);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = ScriptValue.of((String)((scriptValue != ScriptValue.NULL ? ((polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine4.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr() + "," + (scriptValue2 != ScriptValue.NULL ? ((polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine3.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr() + "," + (scriptValue3 != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine2.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr()));
        builder.val("_hold_key", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassMachine.pg$190_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("contraption", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            Object object;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("contraption");
            boolean bl = ScriptFormula.valuesEqual((ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.0)) ^ true;
            ScriptValue scriptValue8 = ScriptValue.of((boolean)bl);
            builder.val("is_rotational", scriptValue8);
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("contraption");
            if (scriptValue9 != ScriptValue.NULL) {
                ArrayList arrayList2 = new ArrayList();
                object = PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)scriptValue9, arrayList2, (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = object;
            builder.val("is_linear", scriptValue10);
            double d = bl || scriptValue10.asBool() ? 1.0 : 0.0;
            ScriptValue scriptValue11 = ScriptValue.of((double)d);
            builder.val("is_now", scriptValue11);
            if (d > 0.0) {
                ScriptValue scriptValue12;
                PolyClassMachine polyClassMachine5;
                PolyClassMachine polyClassMachine6;
                PolyClassMachine polyClassMachine7;
                PolyClassMachine polyClassMachine8;
                PolyClassMachine polyClassMachine9;
                PolyClassMachine polyClassMachine10;
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
                arrayList3.add(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue16 != ScriptValue.NULL ? ((polyClassMachine10 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue16)) != null ? polyClassMachine10.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue16, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue17 != ScriptValue.NULL ? ((polyClassMachine9 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue17)) != null ? polyClassMachine9.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue17, (ScriptContext)scriptContext)) : ScriptValue.NULL)));
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
                arrayList3.add(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue18 != ScriptValue.NULL ? ((polyClassMachine8 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassMachine8.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue19 != ScriptValue.NULL ? ((polyClassMachine7 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue19)) != null ? polyClassMachine7.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue19, (ScriptContext)scriptContext)) : ScriptValue.NULL)));
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                arrayList3.add(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue20 != ScriptValue.NULL ? ((polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue20)) != null ? polyClassMachine6.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue20, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue21 != ScriptValue.NULL ? ((polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue21)) != null ? polyClassMachine5.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL)));
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
                    Object object2;
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
                        ScriptValue.Obj obj;
                        Object object3;
                        CallSite callSite2 = callSite;
                        ScriptValue scriptValue27 = scriptValue15;
                        if (scriptValue26 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            PolyClassMachine polyClassMachine11 = new PolyClassMachine(object3);
                            object2 = polyClassMachine11.tm$2_tick_break((ScriptValue)callSite2, scriptValue27.asNum());
                        } else {
                            ArrayList<CallSite> arrayList6 = new ArrayList<CallSite>();
                            arrayList6.add(callSite2);
                            arrayList6.add((CallSite)scriptValue27);
                            object2 = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue26, arrayList6, (ScriptContext)scriptContext);
                        }
                    } else {
                        object2 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue28 = object2;
                    builder.val("result", scriptValue28);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue28, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                        ScriptValue scriptValue29;
                        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue28);
                        if (list != null) {
                            for (ScriptValue scriptValue30 : list) {
                                builder.val("item", scriptValue30);
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
                        ScriptValue.Obj obj;
                        Object object4;
                        ScriptValue scriptValue31 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)8.0), (ScriptValue)scriptValue15);
                        if (scriptValue24 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            PolyClassMachine polyClassMachine12 = new PolyClassMachine(object4);
                            v7 = ScriptValue.of((boolean)polyClassMachine12.tm$56_report_su(scriptValue31.asNum()));
                        } else {
                            ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                            arrayList8.add(scriptValue31);
                            v7 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue24, arrayList8, (ScriptContext)scriptContext);
                        }
                    } else {
                        v7 = ScriptValue.NULL;
                    }
                }
            } else {
                ScriptValue scriptValue32 = scriptContext.getClassOrVar("contraption");
                if (scriptValue32 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(scriptValue4);
                    v8 = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue32, arrayList9, (ScriptContext)scriptContext);
                } else {
                    v8 = ScriptValue.NULL;
                }
            }
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)ScriptValue.of((double)0.0))) {
            double d = 0.0;
            ScriptValue scriptValue33 = ScriptValue.of((double)0.0);
            builder.val("is_now", scriptValue33);
        } else {
            ScriptValue scriptValue34;
            List list;
            Object object;
            double d = 10.0;
            double d3 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(scriptContext.getNum("rpm")) / d);
            ScriptValue scriptValue35 = ScriptValue.of((double)d3);
            builder.val("speed", scriptValue35);
            ScriptValue scriptValue36 = scriptContext.getClassOrVar("Machine");
            if (scriptValue36 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                PolyClassMachine polyClassMachine13;
                ScriptValue scriptValue37 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue38 = scriptValue37 != ScriptValue.NULL ? ((polyClassMachine13 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue37)) != null ? polyClassMachine13.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue37, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d4 = d3;
                if (scriptValue36 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue36).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine14 = new PolyClassMachine(object5);
                    object = polyClassMachine14.tm$2_tick_break(scriptValue38, d4);
                } else {
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add(scriptValue38);
                    arrayList10.add(ScriptValue.of((double)d4));
                    object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue36, arrayList10, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue39 = object;
            builder.val("result", scriptValue39);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue39, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && (list = ScriptProgram.elementsOf((ScriptValue)scriptValue39)) != null) {
                for (ScriptValue scriptValue40 : list) {
                    builder.val("item", scriptValue40);
                    ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder4.val("item", scriptContext.getClassOrVar("item"));
                    Utils.1._deposit((ScriptContext.Builder)builder4);
                }
            }
            if ((scriptValue34 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                double d5 = 8.0 + d3;
                if (scriptValue34 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue34).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine15 = new PolyClassMachine(object6);
                    v10 = ScriptValue.of((boolean)polyClassMachine15.tm$56_report_su(d5));
                } else {
                    ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                    arrayList11.add(ScriptValue.of((double)d5));
                    v10 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue34, arrayList11, (ScriptContext)scriptContext);
                }
            } else {
                v10 = ScriptValue.NULL;
            }
            double d6 = 1.0;
            ScriptValue scriptValue41 = ScriptValue.of((double)1.0);
            builder.val("is_now", scriptValue41);
        }
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder5.val("act_key", ScriptValue.of((String)"_drill_act"));
        builder5.val("is_now", scriptContext.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)builder5);
        FILE_SCOPE = builder.build();
    }
}
