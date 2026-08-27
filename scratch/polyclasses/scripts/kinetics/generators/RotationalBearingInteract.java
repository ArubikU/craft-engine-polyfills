/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassGlue
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClassContraptionManager;
import dev.arubik.craftengine.script.PolyClassGlue;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer;
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
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object3 = polyClassMachine != null ? polyClassMachine.tm$68_block_at(d, d2, d3) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
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
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue scriptValue6;
            ScriptValue scriptValue7;
            ScriptValue scriptValue8;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue9 = polyClassMachine != null ? polyClassMachine.pg$133_facing_dx() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue10 = polyClassMachine2 != null ? polyClassMachine2.pg$129_facing_dy() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL);
            PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue11 = polyClassMachine3 != null ? polyClassMachine3.pg$131_facing_dz() : ((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL);
            PolyClassMachine polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5);
            object = polyClassMachine4 != null ? polyClassMachine4.tm$68_block_at(scriptValue9.asNum(), scriptValue10.asNum(), scriptValue11.asNum()) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue5, (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
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
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("assembled", scriptValue3);
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (polyClassPlayer != null ? polyClassPlayer.tg$49_is_sneaking() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            if (scriptValue3.asNum() > 0.0) {
                ScriptValue scriptValue4;
                Object object2;
                Object object3;
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
                if (scriptValue5 != ScriptValue.NULL) {
                    String string = "contraption_uuid";
                    String string3 = "string";
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5);
                    object3 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue6 = object3;
                builder.val("cid", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("ContraptionManager");
                if (scriptValue7 != ScriptValue.NULL) {
                    ScriptValue scriptValue8 = scriptValue6;
                    PolyClassContraptionManager polyClassContraptionManager = PolyClassContraptionManager.ofGuarded((ScriptValue)scriptValue7);
                    object2 = polyClassContraptionManager != null ? polyClassContraptionManager.tm$6_get(scriptValue8.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue9 = object2;
                builder.val("contraption", scriptValue9);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue9, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    Object object4 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "disassemble", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
                }
                if ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue10 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearingInteract.class, 0.0);
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
                    v4 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string4, scriptValue10)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                } else {
                    v4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                if (scriptValue11 != ScriptValue.NULL) {
                    String string = "contraption_uuid";
                    String string5 = "string";
                    ScriptValue scriptValue12 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearingInteract.class, "");
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11);
                    v5 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string5, scriptValue12)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                if (scriptValue13 != ScriptValue.NULL) {
                    double d = 0.0;
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue13);
                    v6 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                } else {
                    v6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
                if (scriptValue14 != ScriptValue.NULL) {
                    String string = "<gray>Rotational bearing disassembled.";
                    PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue14);
                    v7 = polyClassPlayer2 != null ? ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v7 = ScriptValue.NULL;
                }
            }
        } else if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
            if (scriptValue15 != ScriptValue.NULL) {
                String string = "<gray>Already assembled - feed RPM from below to turn it.";
                PolyClassPlayer polyClassPlayer3 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue15);
                v8 = polyClassPlayer3 != null ? ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v8 = ScriptValue.NULL;
            }
        } else {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue16 = RotationalBearingInteract._seedBlock(builder2);
            builder.val("seed", scriptValue16);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue16, (ScriptValue)scriptContext.getClassOrVar("null")) || (scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("Player");
                if (scriptValue17 != ScriptValue.NULL) {
                    String string = "<red>Nothing attached to the bearing's face to assemble.";
                    PolyClassPlayer polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue17);
                    v9 = polyClassPlayer4 != null ? ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v9 = ScriptValue.NULL;
                }
            } else {
                Object object5;
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Glue");
                if (scriptValue18 != ScriptValue.NULL) {
                    ScriptValue scriptValue19 = scriptValue16;
                    PolyClassGlue polyClassGlue = PolyClassGlue.ofGuarded((ScriptValue)scriptValue18);
                    object5 = polyClassGlue != null ? ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue19)) : PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                if (object5.asBool() ^ true) {
                    ScriptValue scriptValue20 = scriptContext.getClassOrVar("Player");
                    if (scriptValue20 != ScriptValue.NULL) {
                        String string = "<red>That structure is not glued together - glue it to itself and to the bearing.";
                        PolyClassPlayer polyClassPlayer5 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue20);
                        v11 = polyClassPlayer5 != null ? ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    } else {
                        v11 = ScriptValue.NULL;
                    }
                } else {
                    Object object6;
                    ScriptValue scriptValue21 = scriptContext.getClassOrVar("ContraptionManager");
                    if (scriptValue21 != ScriptValue.NULL) {
                        Object object7;
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue16);
                        ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue22 != ScriptValue.NULL) {
                            double d = 0.0;
                            double d2 = 0.0;
                            double d3 = 0.0;
                            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue22);
                            object7 = polyClassMachine != null ? polyClassMachine.tm$68_block_at(d, d2, d3) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                        } else {
                            object7 = ScriptValue.NULL;
                        }
                        arrayList.add((ScriptValue)object7);
                        PolyClassContraptionManager polyClassContraptionManager = PolyClassContraptionManager.ofGuarded((ScriptValue)scriptValue21);
                        object6 = polyClassContraptionManager != null ? polyClassContraptionManager.um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue21, arrayList, (ScriptContext)scriptContext);
                    } else {
                        object6 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue23 = object6;
                    builder.val("contraption", scriptValue23);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue23, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                        ScriptValue scriptValue24 = scriptContext.getClassOrVar("Player");
                        if (scriptValue24 != ScriptValue.NULL) {
                            String string = "<red>Could not assemble that structure.";
                            PolyClassPlayer polyClassPlayer6 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue24);
                            v14 = polyClassPlayer6 != null ? ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        } else {
                            v14 = ScriptValue.NULL;
                        }
                    } else {
                        ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue25 != ScriptValue.NULL) {
                            String string = "contraption_uuid";
                            String string6 = "string";
                            ScriptValue scriptValue26 = scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL;
                            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue25);
                            v15 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string6, scriptValue26)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue25, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
                        } else {
                            v15 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue27 != ScriptValue.NULL) {
                            String string = "assembled";
                            String string7 = "int";
                            ScriptValue scriptValue28 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearingInteract.class, 1.0);
                            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue27);
                            v16 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string7, scriptValue28)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue27, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue28, (ScriptContext)scriptContext);
                        } else {
                            v16 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue29 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue29 != ScriptValue.NULL) {
                            String string = "assembled_tick";
                            String string8 = "int";
                            ScriptValue scriptValue30 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)scriptContext);
                            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue29);
                            v17 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string8, scriptValue30)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue29, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue30, (ScriptContext)scriptContext);
                        } else {
                            v17 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue31 = scriptContext.getClassOrVar("Player");
                        if (scriptValue31 != ScriptValue.NULL) {
                            ScriptValue scriptValue32 = ScriptValue.of((String)("<green>Rotational bearing assembled <gray>(" + (scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_count", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " blocks, weight " + ScriptFormula.numToStr((double)Math.floor((scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "weight", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum())) + ")."));
                            PolyClassPlayer polyClassPlayer7 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue31);
                            v18 = polyClassPlayer7 != null ? ScriptValue.of((boolean)polyClassPlayer7.tm$42_send_message(scriptValue32.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue31, (ScriptValue)scriptValue32, (ScriptContext)scriptContext);
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
