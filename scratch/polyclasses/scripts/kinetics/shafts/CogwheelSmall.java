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
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.shafts;

import dev.arubik.craftengine.script.PolyClass;
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
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
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
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("rpm");
            if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(scriptValue4.asNum()));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            }
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
                ScriptValue.Obj obj;
                Object object5;
                double d = 1.0;
                double d2 = 0.0;
                double d3 = 0.0;
                if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine3 = new PolyClassMachine(object5);
                    object4 = polyClassMachine3.tm$68_block_at(d, d2, d3);
                } else {
                    object4 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object4);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                double d = -1.0;
                double d4 = 0.0;
                double d5 = 0.0;
                if (scriptValue8 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine4 = new PolyClassMachine(object6);
                    object3 = polyClassMachine4.tm$68_block_at(d, d4, d5);
                } else {
                    object3 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
                }
            } else {
                object3 = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object3);
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                double d = 0.0;
                double d6 = 0.0;
                double d7 = 1.0;
                if (scriptValue9 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine5 = new PolyClassMachine(object7);
                    object2 = polyClassMachine5.tm$68_block_at(d, d6, d7);
                } else {
                    object2 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d6), (ScriptValue)ScriptValue.of((double)d7), (ScriptContext)scriptContext);
                }
            } else {
                object2 = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object2);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                double d = 0.0;
                double d8 = 0.0;
                double d9 = -1.0;
                if (scriptValue10 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine6 = new PolyClassMachine(object8);
                    object = polyClassMachine6.tm$68_block_at(d, d8, d9);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d8), (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            arrayList2.add((ScriptValue)object);
            ScriptValue.Array array = new ScriptValue.Array(arrayList2);
            builder.val("perp", (ScriptValue)array);
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"x")) {
            Object object;
            Object object9;
            Object object10;
            Object object11;
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                double d = 0.0;
                double d10 = 1.0;
                double d11 = 0.0;
                if (scriptValue11 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine7 = new PolyClassMachine(object12);
                    object11 = polyClassMachine7.tm$68_block_at(d, d10, d11);
                } else {
                    object11 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
                }
            } else {
                object11 = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object11);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object13;
                double d = 0.0;
                double d12 = -1.0;
                double d13 = 0.0;
                if (scriptValue12 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine8 = new PolyClassMachine(object13);
                    object10 = polyClassMachine8.tm$68_block_at(d, d12, d13);
                } else {
                    object10 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d12), (ScriptValue)ScriptValue.of((double)d13), (ScriptContext)scriptContext);
                }
            } else {
                object10 = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object10);
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                double d = 0.0;
                double d14 = 0.0;
                double d15 = 1.0;
                if (scriptValue13 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine9 = new PolyClassMachine(object14);
                    object9 = polyClassMachine9.tm$68_block_at(d, d14, d15);
                } else {
                    object9 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d14), (ScriptValue)ScriptValue.of((double)d15), (ScriptContext)scriptContext);
                }
            } else {
                object9 = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object9);
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object15;
                double d = 0.0;
                double d16 = 0.0;
                double d17 = -1.0;
                if (scriptValue14 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine10 = new PolyClassMachine(object15);
                    object = polyClassMachine10.tm$68_block_at(d, d16, d17);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d16), (ScriptValue)ScriptValue.of((double)d17), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            arrayList3.add((ScriptValue)object);
            ScriptValue.Array array = new ScriptValue.Array(arrayList3);
            builder.val("perp", (ScriptValue)array);
        } else {
            Object object;
            Object object16;
            Object object17;
            Object object18;
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object19;
                double d = 1.0;
                double d18 = 0.0;
                double d19 = 0.0;
                if (scriptValue15 instanceof ScriptValue.Obj && (object19 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object19 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine11 = new PolyClassMachine(object19);
                    object18 = polyClassMachine11.tm$68_block_at(d, d18, d19);
                } else {
                    object18 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d18), (ScriptValue)ScriptValue.of((double)d19), (ScriptContext)scriptContext);
                }
            } else {
                object18 = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object18);
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object20;
                double d = -1.0;
                double d20 = 0.0;
                double d21 = 0.0;
                if (scriptValue16 instanceof ScriptValue.Obj && (object20 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object20 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine12 = new PolyClassMachine(object20);
                    object17 = polyClassMachine12.tm$68_block_at(d, d20, d21);
                } else {
                    object17 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d20), (ScriptValue)ScriptValue.of((double)d21), (ScriptContext)scriptContext);
                }
            } else {
                object17 = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object17);
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
            if (scriptValue17 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object21;
                double d = 0.0;
                double d22 = 1.0;
                double d23 = 0.0;
                if (scriptValue17 instanceof ScriptValue.Obj && (object21 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object21 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine13 = new PolyClassMachine(object21);
                    object16 = polyClassMachine13.tm$68_block_at(d, d22, d23);
                } else {
                    object16 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d22), (ScriptValue)ScriptValue.of((double)d23), (ScriptContext)scriptContext);
                }
            } else {
                object16 = ScriptValue.NULL;
            }
            arrayList4.add((ScriptValue)object16);
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
            if (scriptValue18 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object22;
                double d = 0.0;
                double d24 = -1.0;
                double d25 = 0.0;
                if (scriptValue18 instanceof ScriptValue.Obj && (object22 = (obj = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object22 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine14 = new PolyClassMachine(object22);
                    object = polyClassMachine14.tm$68_block_at(d, d24, d25);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d24), (ScriptValue)ScriptValue.of((double)d25), (ScriptContext)scriptContext);
                }
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
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("neighbor");
                if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelSmall.class, "axis")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue5) ^ true) continue;
                ScriptValue scriptValue22 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("nid", scriptValue22);
                scriptValue19 = scriptValue22;
                if (ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelSmall.class, "cogwheel_small")), (ScriptContext)scriptContext).asBool() && ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelSmall.class, "large")), (ScriptContext)scriptContext).asBool() ^ true) {
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue23 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object23;
                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                        arrayList5.add(scriptValue20);
                        arrayList5.add(ScriptValue.of((double)(-scriptContext.getNum("rpm"))));
                        arrayList5.add(scriptValue6);
                        v14 = scriptValue23 instanceof ScriptValue.Obj && (object23 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object23 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine(object23).um$29_relay_to(arrayList5) : PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue23, arrayList5, (ScriptContext)scriptContext);
                    } else {
                        v14 = ScriptValue.NULL;
                    }
                }
                if (!ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelSmall.class, "cogwheel_large")), (ScriptContext)scriptContext).asBool()) continue;
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                if (scriptValue24 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object24;
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(scriptValue20);
                    arrayList6.add(ScriptValue.of((double)(-scriptContext.getNum("rpm") * 0.5)));
                    arrayList6.add(scriptValue6);
                    if (scriptValue24 instanceof ScriptValue.Obj && (object24 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object24 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        object = new PolyClassMachine(object24).um$29_relay_to(arrayList6);
                        continue;
                    }
                    object = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)scriptValue24, arrayList6, (ScriptContext)scriptContext);
                    continue;
                }
                object = ScriptValue.NULL;
            }
        }
        double d = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", CogwheelSmall.class, 0.0))) ^ true ? 1.0 : 0.0;
        ScriptValue scriptValue25 = ScriptValue.of((double)d);
        builder.val("is_now", scriptValue25);
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder2.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelSmall.class, "_cog_act"));
        builder2.val("is_now", ScriptValue.of((double)d));
        Utils.1._updateActivated((ScriptContext.Builder)builder2);
        FILE_SCOPE = builder.build();
    }
}
