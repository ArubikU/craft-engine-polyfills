/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.shafts;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class CogwheelSmall {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$106_set_rpm_output(d)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassMachine_v3 polyClassMachine_v3;
        PolyClassMachine_v3 polyClassMachine_v32;
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_update_activated");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"kinetics/utils.pf", null, arrayList);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("rpm");
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v33 != null ? ScriptValue.of((boolean)polyClassMachine_v33.tm$106_set_rpm_output(scriptValue2.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v32.pg$127_axis() : PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("axis", scriptValue3);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue4 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$183_rpm_network() : PolyDispatch.bootstrapGet("memberGet", "rpm_network", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("net", scriptValue4);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"y")) {
            Object object;
            Object object2;
            Object object3;
            Object object4;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            if (scriptValue != ScriptValue.NULL) {
                double d = 1.0;
                double d2 = 0.0;
                double d3 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object4 = polyClassMachine_v34 != null ? polyClassMachine_v34.tm$68_block_at(d, d2, d3) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object4);
            if (scriptValue != ScriptValue.NULL) {
                double d = -1.0;
                double d4 = 0.0;
                double d5 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object3 = polyClassMachine_v35 != null ? polyClassMachine_v35.tm$68_block_at(d, d4, d5) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object3);
            if (scriptValue != ScriptValue.NULL) {
                double d = 0.0;
                double d6 = 0.0;
                double d7 = 1.0;
                PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object2 = polyClassMachine_v36 != null ? polyClassMachine_v36.tm$68_block_at(d, d6, d7) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d6), (ScriptValue)ScriptValue.of((double)d7), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object2);
            if (scriptValue != ScriptValue.NULL) {
                double d = 0.0;
                double d8 = 0.0;
                double d9 = -1.0;
                PolyClassMachine_v3 polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object = polyClassMachine_v37 != null ? polyClassMachine_v37.tm$68_block_at(d, d8, d9) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d8), (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object);
            ScriptValue.Array array = new ScriptValue.Array(arrayList2);
            builder.val("perp", (ScriptValue)array);
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"x")) {
            Object object;
            Object object5;
            Object object6;
            Object object7;
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            if (scriptValue != ScriptValue.NULL) {
                double d = 0.0;
                double d10 = 1.0;
                double d11 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v38 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object7 = polyClassMachine_v38 != null ? polyClassMachine_v38.tm$68_block_at(d, d10, d11) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
            } else {
                object7 = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object7);
            if (scriptValue != ScriptValue.NULL) {
                double d = 0.0;
                double d12 = -1.0;
                double d13 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v39 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object6 = polyClassMachine_v39 != null ? polyClassMachine_v39.tm$68_block_at(d, d12, d13) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d12), (ScriptValue)ScriptValue.of((double)d13), (ScriptContext)scriptContext);
            } else {
                object6 = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object6);
            if (scriptValue != ScriptValue.NULL) {
                double d = 0.0;
                double d14 = 0.0;
                double d15 = 1.0;
                PolyClassMachine_v3 polyClassMachine_v310 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object5 = polyClassMachine_v310 != null ? polyClassMachine_v310.tm$68_block_at(d, d14, d15) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d14), (ScriptValue)ScriptValue.of((double)d15), (ScriptContext)scriptContext);
            } else {
                object5 = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object5);
            if (scriptValue != ScriptValue.NULL) {
                double d = 0.0;
                double d16 = 0.0;
                double d17 = -1.0;
                PolyClassMachine_v3 polyClassMachine_v311 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object = polyClassMachine_v311 != null ? polyClassMachine_v311.tm$68_block_at(d, d16, d17) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d16), (ScriptValue)ScriptValue.of((double)d17), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object);
            ScriptValue.Array array = new ScriptValue.Array(arrayList3);
            builder.val("perp", (ScriptValue)array);
        } else {
            Object object;
            Object object8;
            Object object9;
            Object object10;
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            if (scriptValue != ScriptValue.NULL) {
                double d = 1.0;
                double d18 = 0.0;
                double d19 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v312 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object10 = polyClassMachine_v312 != null ? polyClassMachine_v312.tm$68_block_at(d, d18, d19) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d18), (ScriptValue)ScriptValue.of((double)d19), (ScriptContext)scriptContext);
            } else {
                object10 = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object10);
            if (scriptValue != ScriptValue.NULL) {
                double d = -1.0;
                double d20 = 0.0;
                double d21 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v313 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object9 = polyClassMachine_v313 != null ? polyClassMachine_v313.tm$68_block_at(d, d20, d21) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d20), (ScriptValue)ScriptValue.of((double)d21), (ScriptContext)scriptContext);
            } else {
                object9 = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object9);
            if (scriptValue != ScriptValue.NULL) {
                double d = 0.0;
                double d22 = 1.0;
                double d23 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v314 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object8 = polyClassMachine_v314 != null ? polyClassMachine_v314.tm$68_block_at(d, d22, d23) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d22), (ScriptValue)ScriptValue.of((double)d23), (ScriptContext)scriptContext);
            } else {
                object8 = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object8);
            if (scriptValue != ScriptValue.NULL) {
                double d = 0.0;
                double d24 = -1.0;
                double d25 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v315 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object = polyClassMachine_v315 != null ? polyClassMachine_v315.tm$68_block_at(d, d24, d25) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d24), (ScriptValue)ScriptValue.of((double)d25), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object);
            ScriptValue.Array array = new ScriptValue.Array(arrayList4);
            builder.val("perp", (ScriptValue)array);
        }
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("perp"));
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("nid");
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                Object object;
                builder.val("neighbor", scriptValue6);
                if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelSmall.class, "axis")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue3) ^ true) continue;
                ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("nid", scriptValue7);
                scriptValue5 = scriptValue7;
                if (ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelSmall.class, "cogwheel_small")), (ScriptContext)scriptContext).asBool() && ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelSmall.class, "large")), (ScriptContext)scriptContext).asBool() ^ true) {
                    if (scriptValue != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                        arrayList5.add(scriptValue6);
                        arrayList5.add(ScriptValue.of((double)(-scriptContext.getNum("rpm"))));
                        arrayList5.add(scriptValue4);
                        PolyClassMachine_v3 polyClassMachine_v316 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                        v14 = polyClassMachine_v316 != null ? polyClassMachine_v316.um$29_relay_to(arrayList5) : PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue, arrayList5, (ScriptContext)scriptContext);
                    } else {
                        v14 = ScriptValue.NULL;
                    }
                }
                if (!ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelSmall.class, "cogwheel_large")), (ScriptContext)scriptContext).asBool()) continue;
                if (scriptValue != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(scriptValue6);
                    arrayList6.add(ScriptValue.of((double)(-scriptContext.getNum("rpm") * 0.5)));
                    arrayList6.add(scriptValue4);
                    PolyClassMachine_v3 polyClassMachine_v317 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                    if (polyClassMachine_v317 != null) {
                        object = polyClassMachine_v317.um$29_relay_to(arrayList6);
                        continue;
                    }
                    object = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue, arrayList6, (ScriptContext)scriptContext);
                    continue;
                }
                object = ScriptValue.NULL;
            }
        }
        double d = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", CogwheelSmall.class, 0.0))) ^ true ? 1.0 : 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)d);
        builder.val("is_now", scriptValue8);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder2.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelSmall.class, "_cog_act"));
        builder2.val("is_now", ScriptValue.of((double)d));
        Utils.1._updateActivated((ScriptContext.Builder)builder2);
        FILE_SCOPE = builder.build();
    }
}
