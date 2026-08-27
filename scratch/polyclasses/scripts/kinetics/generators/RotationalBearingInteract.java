/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassGlue
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClassContraptionManager;
import dev.arubik.craftengine.script.PolyClassGlue;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class RotationalBearingInteract {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _seedBlock(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object3 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$68_block_at(d, d2, d3) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object3;
        builder.val("self", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Glue");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue scriptValue4 = scriptValue2;
            PolyClassGlue polyClassGlue = PolyClassGlue.ofGuarded((ScriptValue)scriptValue3);
            object2 = polyClassGlue != null ? ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue4)) : PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        if (object2.asBool()) {
            return scriptValue2;
        }
        if (scriptValue != ScriptValue.NULL) {
            PolyClassMachine_v3 polyClassMachine_v3;
            ScriptValue scriptValue5;
            PolyClassMachine_v3 polyClassMachine_v32;
            ScriptValue scriptValue6;
            PolyClassMachine_v3 polyClassMachine_v33;
            Object object4 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v33.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : (scriptValue6 = ScriptValue.NULL);
            Object object5 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v32.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : (scriptValue5 = ScriptValue.NULL);
            ScriptValue scriptValue7 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v34 != null ? polyClassMachine_v34.tm$68_block_at(scriptValue6.asNum(), scriptValue5.asNum(), scriptValue7.asNum()) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)scriptValue6, (ScriptValue)scriptValue5, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "assembled";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("assembled", scriptValue3);
        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (polyClassPlayer_v2 != null ? polyClassPlayer_v2.tg$49_is_sneaking() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            if (scriptValue3.asNum() > 0.0) {
                Object object2;
                Object object3;
                if (scriptValue2 != ScriptValue.NULL) {
                    String string = "contraption_uuid";
                    String string3 = "string";
                    PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                    object3 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue4 = object3;
                builder.val("cid", scriptValue4);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("ContraptionManager");
                if (scriptValue5 != ScriptValue.NULL) {
                    ScriptValue scriptValue6 = scriptValue4;
                    PolyClassContraptionManager polyClassContraptionManager = PolyClassContraptionManager.ofGuarded((ScriptValue)scriptValue5);
                    object2 = polyClassContraptionManager != null ? polyClassContraptionManager.tm$6_get(scriptValue6.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue7 = object2;
                builder.val("contraption", scriptValue7);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    Object object4 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "disassemble", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
                }
                if (scriptValue2 != ScriptValue.NULL) {
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearingInteract.class, 0.0);
                    PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                    v4 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue8)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                } else {
                    v4 = ScriptValue.NULL;
                }
                if (scriptValue2 != ScriptValue.NULL) {
                    String string = "contraption_uuid";
                    String string5 = "string";
                    ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearingInteract.class, "");
                    PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                    v5 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string5, scriptValue9)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                } else {
                    v5 = ScriptValue.NULL;
                }
                if (scriptValue2 != ScriptValue.NULL) {
                    double d = 0.0;
                    PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                    v6 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                } else {
                    v6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
                if (scriptValue10 != ScriptValue.NULL) {
                    String string = "<gray>Rotational bearing disassembled.";
                    PolyClassPlayer_v2 polyClassPlayer_v22 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue10);
                    v7 = polyClassPlayer_v22 != null ? ScriptValue.of((boolean)polyClassPlayer_v22.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v7 = ScriptValue.NULL;
                }
            }
        } else if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
            if (scriptValue11 != ScriptValue.NULL) {
                String string = "<gray>Already assembled - feed RPM from below to turn it.";
                PolyClassPlayer_v2 polyClassPlayer_v23 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue11);
                v8 = polyClassPlayer_v23 != null ? ScriptValue.of((boolean)polyClassPlayer_v23.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v8 = ScriptValue.NULL;
            }
        } else {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue12 = RotationalBearingInteract._seedBlock(builder2);
            builder.val("seed", scriptValue12);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue12, (ScriptValue)scriptContext.getClassOrVar("null")) || (scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Player");
                if (scriptValue13 != ScriptValue.NULL) {
                    String string = "<red>Nothing attached to the bearing's face to assemble.";
                    PolyClassPlayer_v2 polyClassPlayer_v24 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue13);
                    v9 = polyClassPlayer_v24 != null ? ScriptValue.of((boolean)polyClassPlayer_v24.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v9 = ScriptValue.NULL;
                }
            } else {
                Object object5;
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Glue");
                if (scriptValue14 != ScriptValue.NULL) {
                    ScriptValue scriptValue15 = scriptValue12;
                    PolyClassGlue polyClassGlue = PolyClassGlue.ofGuarded((ScriptValue)scriptValue14);
                    object5 = polyClassGlue != null ? ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue15)) : PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                if (object5.asBool() ^ true) {
                    ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
                    if (scriptValue16 != ScriptValue.NULL) {
                        String string = "<red>That structure is not glued together - glue it to itself and to the bearing.";
                        PolyClassPlayer_v2 polyClassPlayer_v25 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue16);
                        v11 = polyClassPlayer_v25 != null ? ScriptValue.of((boolean)polyClassPlayer_v25.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    } else {
                        v11 = ScriptValue.NULL;
                    }
                } else {
                    Object object6;
                    ScriptValue scriptValue17 = scriptContext.getClassOrVar("ContraptionManager");
                    if (scriptValue17 != ScriptValue.NULL) {
                        Object object7;
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue12);
                        if (scriptValue2 != ScriptValue.NULL) {
                            double d = 0.0;
                            double d2 = 0.0;
                            double d3 = 0.0;
                            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                            object7 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$68_block_at(d, d2, d3) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                        } else {
                            object7 = ScriptValue.NULL;
                        }
                        arrayList.add((ScriptValue)object7);
                        PolyClassContraptionManager polyClassContraptionManager = PolyClassContraptionManager.ofGuarded((ScriptValue)scriptValue17);
                        object6 = polyClassContraptionManager != null ? polyClassContraptionManager.um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue17, arrayList, (ScriptContext)scriptContext);
                    } else {
                        object6 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue18 = object6;
                    builder.val("contraption", scriptValue18);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue18, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                        ScriptValue scriptValue19 = scriptContext.getClassOrVar("Player");
                        if (scriptValue19 != ScriptValue.NULL) {
                            String string = "<red>Could not assemble that structure.";
                            PolyClassPlayer_v2 polyClassPlayer_v26 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue19);
                            v14 = polyClassPlayer_v26 != null ? ScriptValue.of((boolean)polyClassPlayer_v26.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        } else {
                            v14 = ScriptValue.NULL;
                        }
                    } else {
                        if (scriptValue2 != ScriptValue.NULL) {
                            String string = "contraption_uuid";
                            String string6 = "string";
                            ScriptValue scriptValue20 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL;
                            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                            v15 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string6, scriptValue20)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
                        } else {
                            v15 = ScriptValue.NULL;
                        }
                        if (scriptValue2 != ScriptValue.NULL) {
                            String string = "assembled";
                            String string7 = "int";
                            ScriptValue scriptValue21 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearingInteract.class, 1.0);
                            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                            v16 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string7, scriptValue21)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
                        } else {
                            v16 = ScriptValue.NULL;
                        }
                        if (scriptValue2 != ScriptValue.NULL) {
                            String string = "assembled_tick";
                            String string8 = "int";
                            ScriptValue scriptValue22 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)scriptContext);
                            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                            v17 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string8, scriptValue22)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
                        } else {
                            v17 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue23 = scriptContext.getClassOrVar("Player");
                        if (scriptValue23 != ScriptValue.NULL) {
                            ScriptValue scriptValue24 = ScriptValue.of((String)("<green>Rotational bearing assembled <gray>(" + (scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_count", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " blocks, weight " + ScriptFormula.numToStr((double)Math.floor((scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "weight", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum())) + ")."));
                            PolyClassPlayer_v2 polyClassPlayer_v27 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue23);
                            v18 = polyClassPlayer_v27 != null ? ScriptValue.of((boolean)polyClassPlayer_v27.tm$42_send_message(scriptValue24.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue23, (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
                        } else {
                            v18 = ScriptValue.NULL;
                        }
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
