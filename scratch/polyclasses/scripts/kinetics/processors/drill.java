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
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
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
        PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine_v4 polyClassMachine_v43 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue5 = ScriptValue.of((String)((polyClassMachine_v4 != null ? polyClassMachine_v4.pg$200_x() : ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr() + "," + (polyClassMachine_v42 != null ? polyClassMachine_v42.pg$202_y() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr() + "," + (polyClassMachine_v43 != null ? polyClassMachine_v43.pg$206_z() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()));
        builder.val("_hold_key", scriptValue5);
        PolyClassMachine_v4 polyClassMachine_v44 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue6 = polyClassMachine_v44 != null ? polyClassMachine_v44.pg$191_contraption() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("contraption", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            boolean bl = ScriptFormula.valuesEqual((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 0.0))) ^ true;
            ScriptValue scriptValue7 = ScriptValue.of((boolean)bl);
            builder.val("is_rotational", scriptValue7);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("contraption");
            ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("is_linear", scriptValue9);
            double d = bl || scriptValue9.asBool() ? 1.0 : 0.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)d);
            builder.val("is_now", scriptValue10);
            if (d > 0.0) {
                ScriptValue scriptValue11;
                ScriptValue scriptValue12;
                ScriptValue scriptValue13;
                ScriptValue scriptValue14;
                ScriptValue scriptValue15;
                ScriptValue scriptValue16;
                ScriptValue scriptValue17;
                if (bl) {
                    double d2 = 10.0;
                    scriptValue17 = ScriptValue.of((double)Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs((scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum()) / d2));
                } else {
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder2.val("contraption", scriptValue6);
                    scriptValue17 = Utils.1._linearBreakSpeed((ScriptContext.Builder)builder2);
                }
                ScriptValue scriptValue18 = scriptValue17;
                builder.val("speed", scriptValue18);
                PolyClassMachine_v4 polyClassMachine_v45 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v4 polyClassMachine_v46 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v4 polyClassMachine_v47 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v4 polyClassMachine_v48 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v4 polyClassMachine_v49 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v4 polyClassMachine_v410 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v45 != null ? polyClassMachine_v45.pg$200_x() : ((scriptValue16 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v46 != null ? polyClassMachine_v46.pg$133_facing_dx() : ((scriptValue15 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v47 != null ? polyClassMachine_v47.pg$202_y() : ((scriptValue14 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v48 != null ? polyClassMachine_v48.pg$129_facing_dy() : ((scriptValue13 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v49 != null ? polyClassMachine_v49.pg$206_z() : ((scriptValue12 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v410 != null ? polyClassMachine_v410.pg$131_facing_dz() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL))), (ScriptContext)scriptContext);
                builder.val("target", (ScriptValue)callSite);
                if (ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null")) || (callSite != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)callSite, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                    ScriptValue scriptValue19 = scriptContext.getClassOrVar("contraption");
                    Object object = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue19, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
                } else {
                    ScriptValue scriptValue20;
                    Object object;
                    ScriptValue scriptValue21 = scriptContext.getClassOrVar("contraption");
                    Object object2 = scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)scriptValue21, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue22 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object3;
                        CallSite callSite2 = callSite;
                        ScriptValue scriptValue23 = scriptValue18;
                        if (scriptValue22 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            PolyClassMachine_v4 polyClassMachine_v411 = new PolyClassMachine_v4(object3);
                            object = polyClassMachine_v411.tm$2_tick_break((ScriptValue)callSite2, scriptValue23.asNum());
                        } else {
                            object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue22, (ScriptValue)callSite2, (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
                        }
                    } else {
                        object = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue24 = object;
                    builder.val("result", scriptValue24);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue24, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                        ScriptValue scriptValue25;
                        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue24);
                        if (list != null) {
                            for (ScriptValue scriptValue26 : list) {
                                builder.val("item", scriptValue26);
                                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                                builder3.val("item", scriptValue26);
                                Utils.1._deposit((ScriptContext.Builder)builder3);
                            }
                        }
                        Object object4 = (scriptValue25 = scriptContext.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue25, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    }
                    if ((scriptValue20 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object5;
                        ScriptValue scriptValue27 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 8.0)), (ScriptValue)scriptValue18);
                        if (scriptValue20 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                            PolyClassMachine_v4 polyClassMachine_v412 = new PolyClassMachine_v4(object5);
                            v6 = ScriptValue.of((boolean)polyClassMachine_v412.tm$56_report_su(scriptValue27.asNum()));
                        } else {
                            v6 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue20, (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
                        }
                    } else {
                        v6 = ScriptValue.NULL;
                    }
                }
            } else {
                ScriptValue scriptValue28 = scriptContext.getClassOrVar("contraption");
                Object object = scriptValue28 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue28, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 0.0)))) {
            double d = 0.0;
            ScriptValue scriptValue29 = ScriptValue.of((double)0.0);
            builder.val("is_now", scriptValue29);
        } else {
            ScriptValue scriptValue30;
            List list;
            Object object;
            double d = 10.0;
            double d3 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(scriptContext.getNum("rpm")) / d);
            ScriptValue scriptValue31 = ScriptValue.of((double)d3);
            builder.val("speed", scriptValue31);
            ScriptValue scriptValue32 = scriptContext.getClassOrVar("Machine");
            if (scriptValue32 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                ScriptValue scriptValue33;
                PolyClassMachine_v4 polyClassMachine_v413 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue34 = polyClassMachine_v413 != null ? polyClassMachine_v413.pg$137_facing_block() : ((scriptValue33 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue33, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d4 = d3;
                if (scriptValue32 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v414 = new PolyClassMachine_v4(object6);
                    object = polyClassMachine_v414.tm$2_tick_break(scriptValue34, d4);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue32, (ScriptValue)scriptValue34, (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue35 = object;
            builder.val("result", scriptValue35);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue35, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && (list = ScriptProgram.elementsOf((ScriptValue)scriptValue35)) != null) {
                for (ScriptValue scriptValue36 : list) {
                    builder.val("item", scriptValue36);
                    ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder4.val("item", scriptValue36);
                    Utils.1._deposit((ScriptContext.Builder)builder4);
                }
            }
            if ((scriptValue30 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                double d5 = 8.0 + d3;
                if (scriptValue30 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue30).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v415 = new PolyClassMachine_v4(object7);
                    v9 = ScriptValue.of((boolean)polyClassMachine_v415.tm$56_report_su(d5));
                } else {
                    v9 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue30, (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
            double d6 = 1.0;
            ScriptValue scriptValue37 = ScriptValue.of((double)1.0);
            builder.val("is_now", scriptValue37);
        }
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder5.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Drill.class, "_drill_act"));
        builder5.val("is_now", scriptContext.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)builder5);
        FILE_SCOPE = builder.build();
    }
}
