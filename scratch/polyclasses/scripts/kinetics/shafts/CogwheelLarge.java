/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine
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

import dev.arubik.craftengine.script.PolyClassMachine;
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
public final class CogwheelLarge {
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
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            double d = 0.0;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_update_activated");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"kinetics/utils.pf", null, arrayList);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("rpm");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
            v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(scriptValue4.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue5 = polyClassMachine != null ? polyClassMachine.pg$127_axis() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("axis", scriptValue5);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue6 = polyClassMachine2 != null ? polyClassMachine2.pg$183_rpm_network() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm_network", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("net", scriptValue6);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"y")) {
            Object object;
            Object object2;
            Object object3;
            Object object4;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                double d = 1.0;
                double d2 = 0.0;
                double d3 = 0.0;
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                object4 = polyClassMachine3 != null ? polyClassMachine3.tm$68_block_at(d, d2, d3) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object4);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                double d = -1.0;
                double d4 = 0.0;
                double d5 = 0.0;
                PolyClassMachine polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue8);
                object3 = polyClassMachine4 != null ? polyClassMachine4.tm$68_block_at(d, d4, d5) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object3);
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                double d = 0.0;
                double d6 = 0.0;
                double d7 = 1.0;
                PolyClassMachine polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9);
                object2 = polyClassMachine5 != null ? polyClassMachine5.tm$68_block_at(d, d6, d7) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d6), (ScriptValue)ScriptValue.of((double)d7), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object2);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                double d = 0.0;
                double d8 = 0.0;
                double d9 = -1.0;
                PolyClassMachine polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue10);
                object = polyClassMachine6 != null ? polyClassMachine6.tm$68_block_at(d, d8, d9) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d8), (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object);
            ScriptValue.Array array = new ScriptValue.Array(arrayList2);
            builder.val("perp", (ScriptValue)array);
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"x")) {
            Object object;
            Object object5;
            Object object6;
            Object object7;
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                double d = 0.0;
                double d10 = 1.0;
                double d11 = 0.0;
                PolyClassMachine polyClassMachine7 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11);
                object7 = polyClassMachine7 != null ? polyClassMachine7.tm$68_block_at(d, d10, d11) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
            } else {
                object7 = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object7);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                double d = 0.0;
                double d12 = -1.0;
                double d13 = 0.0;
                PolyClassMachine polyClassMachine8 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue12);
                object6 = polyClassMachine8 != null ? polyClassMachine8.tm$68_block_at(d, d12, d13) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d12), (ScriptValue)ScriptValue.of((double)d13), (ScriptContext)scriptContext);
            } else {
                object6 = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object6);
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
            if (scriptValue13 != ScriptValue.NULL) {
                double d = 0.0;
                double d14 = 0.0;
                double d15 = 1.0;
                PolyClassMachine polyClassMachine9 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue13);
                object5 = polyClassMachine9 != null ? polyClassMachine9.tm$68_block_at(d, d14, d15) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d14), (ScriptValue)ScriptValue.of((double)d15), (ScriptContext)scriptContext);
            } else {
                object5 = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object5);
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
            if (scriptValue14 != ScriptValue.NULL) {
                double d = 0.0;
                double d16 = 0.0;
                double d17 = -1.0;
                PolyClassMachine polyClassMachine10 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue14);
                object = polyClassMachine10 != null ? polyClassMachine10.tm$68_block_at(d, d16, d17) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d16), (ScriptValue)ScriptValue.of((double)d17), (ScriptContext)scriptContext);
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
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                double d = 1.0;
                double d18 = 0.0;
                double d19 = 0.0;
                PolyClassMachine polyClassMachine11 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue15);
                object10 = polyClassMachine11 != null ? polyClassMachine11.tm$68_block_at(d, d18, d19) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d18), (ScriptValue)ScriptValue.of((double)d19), (ScriptContext)scriptContext);
            } else {
                object10 = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object10);
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
            if (scriptValue16 != ScriptValue.NULL) {
                double d = -1.0;
                double d20 = 0.0;
                double d21 = 0.0;
                PolyClassMachine polyClassMachine12 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue16);
                object9 = polyClassMachine12 != null ? polyClassMachine12.tm$68_block_at(d, d20, d21) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d20), (ScriptValue)ScriptValue.of((double)d21), (ScriptContext)scriptContext);
            } else {
                object9 = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object9);
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
            if (scriptValue17 != ScriptValue.NULL) {
                double d = 0.0;
                double d22 = 1.0;
                double d23 = 0.0;
                PolyClassMachine polyClassMachine13 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue17);
                object8 = polyClassMachine13 != null ? polyClassMachine13.tm$68_block_at(d, d22, d23) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d22), (ScriptValue)ScriptValue.of((double)d23), (ScriptContext)scriptContext);
            } else {
                object8 = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object8);
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
            if (scriptValue18 != ScriptValue.NULL) {
                double d = 0.0;
                double d24 = -1.0;
                double d25 = 0.0;
                PolyClassMachine polyClassMachine14 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue18);
                object = polyClassMachine14 != null ? polyClassMachine14.tm$68_block_at(d, d24, d25) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d24), (ScriptValue)ScriptValue.of((double)d25), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object);
            ScriptValue.Array array = new ScriptValue.Array(arrayList4);
            builder.val("perp", (ScriptValue)array);
        }
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("perp"));
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("nid");
        if (list != null) {
            for (ScriptValue scriptValue20 : list) {
                Object object;
                builder.val("neighbor", scriptValue20);
                if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelLarge.class, "axis")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue5) ^ true) continue;
                ScriptValue scriptValue21 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("nid", scriptValue21);
                scriptValue19 = scriptValue21;
                if (!(ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelLarge.class, "cogwheel_small")), (ScriptContext)scriptContext).asBool() && ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelLarge.class, "large")), (ScriptContext)scriptContext).asBool() ^ true)) continue;
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
                if (scriptValue22 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue20);
                    arrayList5.add(ScriptValue.of((double)(-scriptContext.getNum("rpm") * 2.0)));
                    arrayList5.add(scriptValue6);
                    PolyClassMachine polyClassMachine15 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue22);
                    if (polyClassMachine15 != null) {
                        object = polyClassMachine15.um$29_relay_to(arrayList5);
                        continue;
                    }
                    object = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue22, arrayList5, (ScriptContext)scriptContext);
                    continue;
                }
                object = ScriptValue.NULL;
            }
        }
        double d = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", CogwheelLarge.class, 0.0))) ^ true ? 1.0 : 0.0;
        ScriptValue scriptValue23 = ScriptValue.of((double)d);
        builder.val("is_now", scriptValue23);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder2.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelLarge.class, "_cog_act"));
        builder2.val("is_now", ScriptValue.of((double)d));
        Utils.1._updateActivated((ScriptContext.Builder)builder2);
        FILE_SCOPE = builder.build();
    }
}
