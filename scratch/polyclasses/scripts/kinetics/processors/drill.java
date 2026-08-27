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
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClass;
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
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("contraption");
            boolean bl = ScriptFormula.valuesEqual((ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 0.0))) ^ true;
            ScriptValue scriptValue8 = ScriptValue.of((boolean)bl);
            builder.val("is_rotational", scriptValue8);
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("contraption");
            ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("is_linear", scriptValue10);
            double d = bl || scriptValue10.asBool() ? 1.0 : 0.0;
            ScriptValue scriptValue11 = ScriptValue.of((double)d);
            builder.val("is_now", scriptValue11);
            if (d > 0.0) {
                ScriptValue scriptValue12;
                ScriptValue scriptValue13;
                ScriptValue scriptValue14;
                ScriptValue scriptValue15;
                ScriptValue scriptValue16;
                ScriptValue scriptValue17;
                ScriptValue scriptValue18;
                ScriptValue scriptValue19;
                if (bl) {
                    ScriptValue scriptValue20;
                    double d2 = 10.0;
                    scriptValue19 = ScriptValue.of((double)Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(((scriptValue20 = scriptContext.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum()) / d2));
                } else {
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder2.val("contraption", scriptValue6);
                    scriptValue19 = Utils.1._linearBreakSpeed((ScriptContext.Builder)builder2);
                }
                ScriptValue scriptValue21 = scriptValue19;
                builder.val("speed", scriptValue21);
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("contraption");
                PolyClassMachine_v2 polyClassMachine_v25 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v2 polyClassMachine_v26 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v2 polyClassMachine_v27 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v2 polyClassMachine_v28 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v2 polyClassMachine_v29 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v2 polyClassMachine_v210 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue22, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v25 != null ? polyClassMachine_v25.pg$200_x() : ((scriptValue18 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v26 != null ? polyClassMachine_v26.pg$133_facing_dx() : ((scriptValue17 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v27 != null ? polyClassMachine_v27.pg$202_y() : ((scriptValue16 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v28 != null ? polyClassMachine_v28.pg$129_facing_dy() : ((scriptValue15 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v29 != null ? polyClassMachine_v29.pg$206_z() : ((scriptValue14 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v210 != null ? polyClassMachine_v210.pg$131_facing_dz() : ((scriptValue13 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL))), (ScriptContext)scriptContext);
                builder.val("target", (ScriptValue)callSite);
                if (ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null")) || ((scriptValue12 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("contraption");
                    Object object = scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue23, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
                } else {
                    ScriptValue scriptValue24;
                    Object object;
                    ScriptValue scriptValue25 = scriptContext.getClassOrVar("contraption");
                    Object object2 = scriptValue25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)scriptValue25, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue26 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object3;
                        CallSite callSite2 = callSite;
                        ScriptValue scriptValue27 = scriptValue21;
                        if (scriptValue26 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            PolyClassMachine_v2 polyClassMachine_v211 = new PolyClassMachine_v2(object3);
                            object = polyClassMachine_v211.tm$2_tick_break((ScriptValue)callSite2, scriptValue27.asNum());
                        } else {
                            object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue26, (ScriptValue)callSite2, (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
                        }
                    } else {
                        object = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue28 = object;
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
                        Object object4 = (scriptValue29 = scriptContext.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue29, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    }
                    if ((scriptValue24 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object5;
                        ScriptValue scriptValue31 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 8.0)), (ScriptValue)scriptValue21);
                        if (scriptValue24 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            PolyClassMachine_v2 polyClassMachine_v212 = new PolyClassMachine_v2(object5);
                            v6 = ScriptValue.of((boolean)polyClassMachine_v212.tm$56_report_su(scriptValue31.asNum()));
                        } else {
                            v6 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue24, (ScriptValue)scriptValue31, (ScriptContext)scriptContext);
                        }
                    } else {
                        v6 = ScriptValue.NULL;
                    }
                }
            } else {
                ScriptValue scriptValue32 = scriptContext.getClassOrVar("contraption");
                Object object = scriptValue32 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue32, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 0.0)))) {
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
                Object object6;
                ScriptValue scriptValue37;
                PolyClassMachine_v2 polyClassMachine_v213 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue38 = polyClassMachine_v213 != null ? polyClassMachine_v213.pg$137_facing_block() : ((scriptValue37 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue37, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d4 = d3;
                if (scriptValue36 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue36).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v214 = new PolyClassMachine_v2(object6);
                    object = polyClassMachine_v214.tm$2_tick_break(scriptValue38, d4);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue36, (ScriptValue)scriptValue38, (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
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
                Object object7;
                double d5 = 8.0 + d3;
                if (scriptValue34 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue34).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v215 = new PolyClassMachine_v2(object7);
                    v9 = ScriptValue.of((boolean)polyClassMachine_v215.tm$56_report_su(d5));
                } else {
                    v9 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue34, (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
            double d6 = 1.0;
            ScriptValue scriptValue41 = ScriptValue.of((double)1.0);
            builder.val("is_now", scriptValue41);
        }
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder5.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Drill.class, "_drill_act"));
        builder5.val("is_now", scriptContext.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)builder5);
        FILE_SCOPE = builder.build();
    }
}
