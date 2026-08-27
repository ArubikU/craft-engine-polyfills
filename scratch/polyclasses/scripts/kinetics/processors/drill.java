/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassContraptionWorld
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassContraptionWorld;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        ScriptValue scriptValue4;
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_deposit");
        arrayList.add("_linear_break_speed");
        arrayList.add("_update_activated");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"kinetics/utils.pf", null, arrayList);
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine_v2 polyClassMachine_v22 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine_v2 polyClassMachine_v23 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue5 = ScriptValue.of((String)((polyClassMachine_v2 != null ? polyClassMachine_v2.pg$200_x() : ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr() + "," + (polyClassMachine_v22 != null ? polyClassMachine_v22.pg$202_y() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr() + "," + (polyClassMachine_v23 != null ? polyClassMachine_v23.pg$206_z() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()));
        builder.val("_hold_key", scriptValue5);
        PolyClassMachine_v2 polyClassMachine_v24 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue6 = polyClassMachine_v24 != null ? polyClassMachine_v24.pg$191_contraption() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("contraption", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            Object object;
            PolyClassContraption polyClassContraption;
            boolean bl = ScriptFormula.valuesEqual((ScriptValue)(scriptValue6 != ScriptValue.NULL ? ((polyClassContraption = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassContraption.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 0.0))) ^ true;
            ScriptValue scriptValue7 = ScriptValue.of((boolean)bl);
            builder.val("is_rotational", scriptValue7);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("contraption");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                if (scriptValue8 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Contraption")) {
                    PolyClassContraption polyClassContraption2 = new PolyClassContraption(object2);
                    object = ScriptValue.of((boolean)polyClassContraption2.tm$48_is_moving());
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = object;
            builder.val("is_linear", scriptValue9);
            double d = bl || scriptValue9.asBool() ? 1.0 : 0.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)d);
            builder.val("is_now", scriptValue10);
            if (d > 0.0) {
                CallSite callSite;
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue11;
                ScriptValue scriptValue12;
                ScriptValue scriptValue13;
                ScriptValue scriptValue14;
                ScriptValue scriptValue15;
                ScriptValue scriptValue16;
                PolyClassContraption polyClassContraption3;
                ScriptValue scriptValue17;
                if (bl) {
                    PolyClassContraption polyClassContraption4;
                    double d2 = 10.0;
                    double d3 = 10.0 == 0.0 ? 0.0 : Math.abs(scriptValue6 != ScriptValue.NULL ? ((polyClassContraption4 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassContraption4.tg$71_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) / d2;
                    scriptValue17 = ScriptValue.of((double)Math.max(1.0, d3));
                } else {
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder2.val("contraption", scriptValue6);
                    scriptValue17 = Utils.1._linearBreakSpeed((ScriptContext.Builder)builder2);
                }
                ScriptValue scriptValue18 = scriptValue17;
                builder.val("speed", scriptValue18);
                ScriptValue scriptValue19 = scriptValue6 != ScriptValue.NULL ? ((polyClassContraption3 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassContraption3.pg$53_contraption_world() : PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassMachine_v2 polyClassMachine_v25 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v2 polyClassMachine_v26 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue20 = ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v25 != null ? polyClassMachine_v25.pg$200_x() : ((scriptValue16 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v26 != null ? polyClassMachine_v26.pg$133_facing_dx() : ((scriptValue15 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL)));
                PolyClassMachine_v2 polyClassMachine_v27 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v2 polyClassMachine_v28 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v27 != null ? polyClassMachine_v27.pg$202_y() : ((scriptValue14 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v28 != null ? polyClassMachine_v28.pg$129_facing_dy() : ((scriptValue13 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL)));
                PolyClassMachine_v2 polyClassMachine_v29 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v2 polyClassMachine_v210 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue22 = ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v29 != null ? polyClassMachine_v29.pg$206_z() : ((scriptValue12 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v210 != null ? polyClassMachine_v210.pg$131_facing_dz() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL)));
                if (scriptValue19 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("ContraptionWorld")) {
                    PolyClassContraptionWorld polyClassContraptionWorld = new PolyClassContraptionWorld(object3);
                    callSite = polyClassContraptionWorld.tm$8_real_block(scriptValue20.asNum(), scriptValue21.asNum(), scriptValue22.asNum());
                } else {
                    callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)scriptValue19, (ScriptValue)scriptValue20, (ScriptValue)scriptValue21, (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
                }
                CallSite callSite2 = callSite;
                builder.val("target", (ScriptValue)callSite2);
                if (ScriptFormula.valuesEqual((ScriptValue)callSite2, (ScriptValue)scriptContext.getClassOrVar("null")) || (callSite2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)callSite2, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("contraption");
                    if (scriptValue23 != ScriptValue.NULL) {
                        ScriptValue.Obj obj2;
                        Object object4;
                        ScriptValue scriptValue24 = scriptValue5;
                        if (scriptValue23 instanceof ScriptValue.Obj && (object4 = (obj2 = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object4 instanceof PolyClass) && obj2.typeName().equals("Contraption")) {
                            PolyClassContraption polyClassContraption5 = new PolyClassContraption(object4);
                            v5 = ScriptValue.of((boolean)polyClassContraption5.tm$2_release(scriptValue24.asStr()));
                        } else {
                            v5 = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue23, (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
                        }
                    } else {
                        v5 = ScriptValue.NULL;
                    }
                } else {
                    ScriptValue scriptValue25;
                    Object object5;
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("contraption");
                    if (scriptValue26 != ScriptValue.NULL) {
                        ScriptValue.Obj obj3;
                        Object object6;
                        ScriptValue scriptValue27 = scriptValue5;
                        if (scriptValue26 instanceof ScriptValue.Obj && (object6 = (obj3 = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object6 instanceof PolyClass) && obj3.typeName().equals("Contraption")) {
                            PolyClassContraption polyClassContraption6 = new PolyClassContraption(object6);
                            v6 = ScriptValue.of((boolean)polyClassContraption6.tm$12_hold(scriptValue27.asStr()));
                        } else {
                            v6 = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)scriptValue26, (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
                        }
                    } else {
                        v6 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue28 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue28 != ScriptValue.NULL) {
                        ScriptValue.Obj obj4;
                        Object object7;
                        CallSite callSite3 = callSite2;
                        ScriptValue scriptValue29 = scriptValue18;
                        if (scriptValue28 instanceof ScriptValue.Obj && (object7 = (obj4 = (ScriptValue.Obj)scriptValue28).instance()) != null && !(object7 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                            PolyClassMachine_v2 polyClassMachine_v211 = new PolyClassMachine_v2(object7);
                            object5 = polyClassMachine_v211.tm$2_tick_break((ScriptValue)callSite3, scriptValue29.asNum());
                        } else {
                            object5 = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue28, (ScriptValue)callSite3, (ScriptValue)scriptValue29, (ScriptContext)scriptContext);
                        }
                    } else {
                        object5 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue30 = object5;
                    builder.val("result", scriptValue30);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue30, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                        ScriptValue scriptValue31;
                        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue30);
                        if (list != null) {
                            for (ScriptValue scriptValue32 : list) {
                                builder.val("item", scriptValue32);
                                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                                builder3.val("item", scriptValue32);
                                Utils.1._deposit((ScriptContext.Builder)builder3);
                            }
                        }
                        if ((scriptValue31 = scriptContext.getClassOrVar("contraption")) != ScriptValue.NULL) {
                            ScriptValue.Obj obj5;
                            Object object8;
                            ScriptValue scriptValue33 = scriptValue5;
                            if (scriptValue31 instanceof ScriptValue.Obj && (object8 = (obj5 = (ScriptValue.Obj)scriptValue31).instance()) != null && !(object8 instanceof PolyClass) && obj5.typeName().equals("Contraption")) {
                                PolyClassContraption polyClassContraption7 = new PolyClassContraption(object8);
                                v8 = ScriptValue.of((boolean)polyClassContraption7.tm$2_release(scriptValue33.asStr()));
                            } else {
                                v8 = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue31, (ScriptValue)scriptValue33, (ScriptContext)scriptContext);
                            }
                        } else {
                            v8 = ScriptValue.NULL;
                        }
                    }
                    if ((scriptValue25 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        ScriptValue.Obj obj6;
                        Object object9;
                        ScriptValue scriptValue34 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 8.0)), (ScriptValue)scriptValue18);
                        if (scriptValue25 instanceof ScriptValue.Obj && (object9 = (obj6 = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object9 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                            PolyClassMachine_v2 polyClassMachine_v212 = new PolyClassMachine_v2(object9);
                            v9 = ScriptValue.of((boolean)polyClassMachine_v212.tm$56_report_su(scriptValue34.asNum()));
                        } else {
                            v9 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue25, (ScriptValue)scriptValue34, (ScriptContext)scriptContext);
                        }
                    } else {
                        v9 = ScriptValue.NULL;
                    }
                }
            } else {
                ScriptValue scriptValue35 = scriptContext.getClassOrVar("contraption");
                if (scriptValue35 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object10;
                    ScriptValue scriptValue36 = scriptValue5;
                    if (scriptValue35 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue35).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Contraption")) {
                        PolyClassContraption polyClassContraption8 = new PolyClassContraption(object10);
                        v10 = ScriptValue.of((boolean)polyClassContraption8.tm$2_release(scriptValue36.asStr()));
                    } else {
                        v10 = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue35, (ScriptValue)scriptValue36, (ScriptContext)scriptContext);
                    }
                } else {
                    v10 = ScriptValue.NULL;
                }
            }
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 0.0)))) {
            double d = 0.0;
            ScriptValue scriptValue37 = ScriptValue.of((double)0.0);
            builder.val("is_now", scriptValue37);
        } else {
            ScriptValue scriptValue38;
            List list;
            Object object;
            double d = 10.0;
            double d4 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(scriptContext.getNum("rpm")) / d);
            ScriptValue scriptValue39 = ScriptValue.of((double)d4);
            builder.val("speed", scriptValue39);
            ScriptValue scriptValue40 = scriptContext.getClassOrVar("Machine");
            if (scriptValue40 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                ScriptValue scriptValue41;
                PolyClassMachine_v2 polyClassMachine_v213 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue42 = polyClassMachine_v213 != null ? polyClassMachine_v213.pg$137_facing_block() : ((scriptValue41 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue41, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d5 = d4;
                if (scriptValue40 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue40).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v214 = new PolyClassMachine_v2(object11);
                    object = polyClassMachine_v214.tm$2_tick_break(scriptValue42, d5);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue40, (ScriptValue)scriptValue42, (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue43 = object;
            builder.val("result", scriptValue43);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue43, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && (list = ScriptProgram.elementsOf((ScriptValue)scriptValue43)) != null) {
                for (ScriptValue scriptValue44 : list) {
                    builder.val("item", scriptValue44);
                    ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder4.val("item", scriptValue44);
                    Utils.1._deposit((ScriptContext.Builder)builder4);
                }
            }
            if ((scriptValue38 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                double d6 = 8.0 + d4;
                if (scriptValue38 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue38).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v215 = new PolyClassMachine_v2(object12);
                    v12 = ScriptValue.of((boolean)polyClassMachine_v215.tm$56_report_su(d6));
                } else {
                    v12 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue38, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
                }
            } else {
                v12 = ScriptValue.NULL;
            }
            double d7 = 1.0;
            ScriptValue scriptValue45 = ScriptValue.of((double)1.0);
            builder.val("is_now", scriptValue45);
        }
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder5.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Drill.class, "_drill_act"));
        builder5.val("is_now", scriptContext.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)builder5);
        FILE_SCOPE = builder.build();
    }
}
