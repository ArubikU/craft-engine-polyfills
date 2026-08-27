/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassContraptionWorld
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassContraptionWorld;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue5 = ScriptValue.of((String)((polyClassMachine_v3 != null ? polyClassMachine_v3.pg$200_x() : ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr() + "," + (polyClassMachine_v32 != null ? polyClassMachine_v32.pg$202_y() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr() + "," + (polyClassMachine_v33 != null ? polyClassMachine_v33.pg$206_z() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()));
        builder.val("_hold_key", scriptValue5);
        PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue6 = polyClassMachine_v34 != null ? polyClassMachine_v34.pg$191_contraption() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("contraption", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            PolyClassContraption polyClassContraption;
            PolyClassContraption polyClassContraption2;
            boolean bl = ScriptFormula.valuesEqual((ScriptValue)(scriptValue6 != ScriptValue.NULL ? ((polyClassContraption2 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassContraption2.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 0.0))) ^ true;
            ScriptValue scriptValue7 = ScriptValue.of((boolean)bl);
            builder.val("is_rotational", scriptValue7);
            ScriptValue scriptValue8 = scriptValue6 != ScriptValue.NULL ? ((polyClassContraption = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6)) != null ? ScriptValue.of((boolean)polyClassContraption.tm$48_is_moving()) : PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("is_linear", scriptValue8);
            double d = bl || scriptValue8.asBool() ? 1.0 : 0.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)d);
            builder.val("is_now", scriptValue9);
            if (d > 0.0) {
                ScriptValue scriptValue10;
                ScriptValue scriptValue11;
                ScriptValue scriptValue12;
                ScriptValue scriptValue13;
                ScriptValue scriptValue14;
                ScriptValue scriptValue15;
                PolyClassContraption polyClassContraption3;
                ScriptValue scriptValue16;
                if (bl) {
                    PolyClassContraption polyClassContraption4;
                    double d2 = 10.0;
                    double d3 = 10.0 == 0.0 ? 0.0 : Math.abs(scriptValue6 != ScriptValue.NULL ? ((polyClassContraption4 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassContraption4.tg$71_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) / d2;
                    scriptValue16 = ScriptValue.of((double)Math.max(1.0, d3));
                } else {
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder2.val("contraption", scriptValue6);
                    scriptValue16 = Utils.1._linearBreakSpeed((ScriptContext.Builder)builder2);
                }
                ScriptValue scriptValue17 = scriptValue16;
                builder.val("speed", scriptValue17);
                ScriptValue scriptValue18 = scriptValue6 != ScriptValue.NULL ? ((polyClassContraption3 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassContraption3.pg$53_contraption_world() : PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue19 = ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v35 != null ? polyClassMachine_v35.pg$200_x() : ((scriptValue15 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v36 != null ? polyClassMachine_v36.pg$133_facing_dx() : ((scriptValue14 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL)));
                PolyClassMachine_v3 polyClassMachine_v37 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v3 polyClassMachine_v38 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue20 = ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v37 != null ? polyClassMachine_v37.pg$202_y() : ((scriptValue13 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v38 != null ? polyClassMachine_v38.pg$129_facing_dy() : ((scriptValue12 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL)));
                PolyClassMachine_v3 polyClassMachine_v39 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v3 polyClassMachine_v310 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v39 != null ? polyClassMachine_v39.pg$206_z() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v310 != null ? polyClassMachine_v310.pg$131_facing_dz() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL)));
                PolyClassContraptionWorld polyClassContraptionWorld = PolyClassContraptionWorld.ofGuarded((ScriptValue)scriptValue18);
                CallSite callSite = polyClassContraptionWorld != null ? polyClassContraptionWorld.tm$8_real_block(scriptValue19.asNum(), scriptValue20.asNum(), scriptValue21.asNum()) : PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptValue)scriptValue20, (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
                builder.val("target", (ScriptValue)callSite);
                if (ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null")) || (callSite != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)callSite, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                    if (scriptValue6 != ScriptValue.NULL) {
                        ScriptValue scriptValue22 = scriptValue5;
                        PolyClassContraption polyClassContraption5 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6);
                        v3 = polyClassContraption5 != null ? ScriptValue.of((boolean)polyClassContraption5.tm$2_release(scriptValue22.asStr())) : PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue6, (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
                    } else {
                        v3 = ScriptValue.NULL;
                    }
                } else {
                    Object object;
                    if (scriptValue6 != ScriptValue.NULL) {
                        ScriptValue scriptValue23 = scriptValue5;
                        PolyClassContraption polyClassContraption6 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6);
                        v4 = polyClassContraption6 != null ? ScriptValue.of((boolean)polyClassContraption6.tm$12_hold(scriptValue23.asStr())) : PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)scriptValue6, (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
                    } else {
                        v4 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue24 != ScriptValue.NULL) {
                        CallSite callSite2 = callSite;
                        ScriptValue scriptValue25 = scriptValue17;
                        PolyClassMachine_v3 polyClassMachine_v311 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue24);
                        object = polyClassMachine_v311 != null ? polyClassMachine_v311.tm$2_tick_break((ScriptValue)callSite2, scriptValue25.asNum()) : PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue24, (ScriptValue)callSite2, (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
                    } else {
                        object = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue26 = object;
                    builder.val("result", scriptValue26);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue26, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue26);
                        if (list != null) {
                            for (ScriptValue scriptValue27 : list) {
                                builder.val("item", scriptValue27);
                                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                                builder3.val("item", scriptValue27);
                                Utils.1._deposit((ScriptContext.Builder)builder3);
                            }
                        }
                        if (scriptValue6 != ScriptValue.NULL) {
                            ScriptValue scriptValue28 = scriptValue5;
                            PolyClassContraption polyClassContraption7 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6);
                            v6 = polyClassContraption7 != null ? ScriptValue.of((boolean)polyClassContraption7.tm$2_release(scriptValue28.asStr())) : PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue6, (ScriptValue)scriptValue28, (ScriptContext)scriptContext);
                        } else {
                            v6 = ScriptValue.NULL;
                        }
                    }
                    if (scriptValue24 != ScriptValue.NULL) {
                        ScriptValue scriptValue29 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 8.0)), (ScriptValue)scriptValue17);
                        PolyClassMachine_v3 polyClassMachine_v312 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue24);
                        v7 = polyClassMachine_v312 != null ? ScriptValue.of((boolean)polyClassMachine_v312.tm$56_report_su(scriptValue29.asNum())) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue24, (ScriptValue)scriptValue29, (ScriptContext)scriptContext);
                    } else {
                        v7 = ScriptValue.NULL;
                    }
                }
            } else if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue scriptValue30 = scriptValue5;
                PolyClassContraption polyClassContraption8 = PolyClassContraption.ofGuarded((ScriptValue)scriptValue6);
                v8 = polyClassContraption8 != null ? ScriptValue.of((boolean)polyClassContraption8.tm$2_release(scriptValue30.asStr())) : PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)scriptValue6, (ScriptValue)scriptValue30, (ScriptContext)scriptContext);
            } else {
                v8 = ScriptValue.NULL;
            }
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Drill.class, 0.0)))) {
            double d = 0.0;
            ScriptValue scriptValue31 = ScriptValue.of((double)0.0);
            builder.val("is_now", scriptValue31);
        } else {
            List list;
            Object object;
            double d = 10.0;
            double d4 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(scriptContext.getNum("rpm")) / d);
            ScriptValue scriptValue32 = ScriptValue.of((double)d4);
            builder.val("speed", scriptValue32);
            ScriptValue scriptValue33 = scriptContext.getClassOrVar("Machine");
            if (scriptValue33 != ScriptValue.NULL) {
                ScriptValue scriptValue34;
                PolyClassMachine_v3 polyClassMachine_v313 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue35 = polyClassMachine_v313 != null ? polyClassMachine_v313.pg$137_facing_block() : ((scriptValue34 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue34, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d5 = d4;
                PolyClassMachine_v3 polyClassMachine_v314 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue33);
                object = polyClassMachine_v314 != null ? polyClassMachine_v314.tm$2_tick_break(scriptValue35, d5) : PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue33, (ScriptValue)scriptValue35, (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue36 = object;
            builder.val("result", scriptValue36);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue36, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && (list = ScriptProgram.elementsOf((ScriptValue)scriptValue36)) != null) {
                for (ScriptValue scriptValue37 : list) {
                    builder.val("item", scriptValue37);
                    ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder4.val("item", scriptValue37);
                    Utils.1._deposit((ScriptContext.Builder)builder4);
                }
            }
            if (scriptValue33 != ScriptValue.NULL) {
                double d6 = 8.0 + d4;
                PolyClassMachine_v3 polyClassMachine_v315 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue33);
                v10 = polyClassMachine_v315 != null ? ScriptValue.of((boolean)polyClassMachine_v315.tm$56_report_su(d6)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue33, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
            } else {
                v10 = ScriptValue.NULL;
            }
            double d7 = 1.0;
            ScriptValue scriptValue38 = ScriptValue.of((double)1.0);
            builder.val("is_now", scriptValue38);
        }
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder5.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Drill.class, "_drill_act"));
        builder5.val("is_now", scriptContext.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)builder5);
        FILE_SCOPE = builder.build();
    }
}
