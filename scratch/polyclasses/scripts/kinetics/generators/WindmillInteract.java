/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBlock
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

import dev.arubik.craftengine.script.PolyClassBlock;
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
public final class WindmillInteract {
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
        double d = 8.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)8.0);
        builder.val("MIN_SAILS", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            String string = "assembled";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("assembled", scriptValue4);
        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (polyClassPlayer_v2 != null ? polyClassPlayer_v2.tg$49_is_sneaking() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            if (scriptValue4.asNum() > 0.0) {
                Object object2;
                Object object3;
                if (scriptValue3 != ScriptValue.NULL) {
                    String string = "contraption_uuid";
                    String string3 = "string";
                    PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                    object3 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue5 = object3;
                builder.val("cid", scriptValue5);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("ContraptionManager");
                if (scriptValue6 != ScriptValue.NULL) {
                    ScriptValue scriptValue7 = scriptValue5;
                    PolyClassContraptionManager polyClassContraptionManager = PolyClassContraptionManager.ofGuarded((ScriptValue)scriptValue6);
                    object2 = polyClassContraptionManager != null ? polyClassContraptionManager.tm$6_get(scriptValue7.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue8 = object2;
                builder.val("contraption", scriptValue8);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue8, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    Object object4 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "disassemble", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
                }
                if (scriptValue3 != ScriptValue.NULL) {
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 0.0);
                    PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                    v4 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue9)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                } else {
                    v4 = ScriptValue.NULL;
                }
                if (scriptValue3 != ScriptValue.NULL) {
                    String string = "contraption_uuid";
                    String string5 = "string";
                    ScriptValue scriptValue10 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "");
                    PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                    v5 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string5, scriptValue10)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                } else {
                    v5 = ScriptValue.NULL;
                }
                if (scriptValue3 != ScriptValue.NULL) {
                    double d2 = 0.0;
                    PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                    v6 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$106_set_rpm_output(d2)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
                } else {
                    v6 = ScriptValue.NULL;
                }
                if (scriptValue3 != ScriptValue.NULL) {
                    double d3 = 0.0;
                    PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                    v7 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d3)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                } else {
                    v7 = ScriptValue.NULL;
                }
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
                if (scriptValue11 != ScriptValue.NULL) {
                    String string = "<gray>Windmill disassembled.";
                    PolyClassPlayer_v2 polyClassPlayer_v22 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue11);
                    v8 = polyClassPlayer_v22 != null ? ScriptValue.of((boolean)polyClassPlayer_v22.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v8 = ScriptValue.NULL;
                }
            }
        } else if (scriptValue4.asNum() > 0.0) {
            Object object5;
            if (scriptValue3 != ScriptValue.NULL) {
                String string = "windmill_dir";
                String string6 = "int";
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                object5 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string6) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
            } else {
                object5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = object5;
            builder.val("dir", scriptValue12);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 0.0)))) {
                double d4 = 1.0;
                ScriptValue scriptValue13 = ScriptValue.of((double)1.0);
                builder.val("dir", scriptValue13);
            }
            double d5 = scriptContext.getNum("dir") > 0.0 ? -1.0 : 1.0;
            ScriptValue scriptValue14 = ScriptValue.of((double)d5);
            builder.val("dir", scriptValue14);
            if (scriptValue3 != ScriptValue.NULL) {
                String string = "windmill_dir";
                String string7 = "int";
                ScriptValue scriptValue15 = ScriptValue.of((double)d5);
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                v10 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string7, scriptValue15)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
            } else {
                v10 = ScriptValue.NULL;
            }
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue scriptValue17 = d5 > 0.0 ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "<gray>Windmill now turns <white>clockwise<gray>.")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "<gray>Windmill now turns <white>counter-clockwise<gray>."));
                PolyClassPlayer_v2 polyClassPlayer_v23 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue16);
                v11 = polyClassPlayer_v23 != null ? ScriptValue.of((boolean)polyClassPlayer_v23.tm$42_send_message(scriptValue17.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
            } else {
                v11 = ScriptValue.NULL;
            }
        } else {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue18 = WindmillInteract._seedBlock(builder2);
            builder.val("seed", scriptValue18);
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("Player");
            if (scriptValue19 != ScriptValue.NULL) {
                Object object6;
                PolyClassMachine_v3 polyClassMachine_v3;
                PolyClassMachine_v3 polyClassMachine_v32;
                PolyClassMachine_v3 polyClassMachine_v33;
                PolyClassMachine_v3 polyClassMachine_v34;
                ScriptValue scriptValue20 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v34.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                String string = "facing";
                PolyClassBlock polyClassBlock = PolyClassBlock.ofGuarded((ScriptValue)scriptValue20);
                StringBuilder stringBuilder = new StringBuilder().append("<dark_gray>[debug] facing=").append((polyClassBlock != null ? polyClassBlock.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext)).asStr()).append(" seed=(").append((scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v33.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr()).append(",").append((scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v32.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr()).append(",").append((scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v3.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr()).append(")").append(" self_glued=");
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Glue");
                if (scriptValue21 != ScriptValue.NULL) {
                    Object object7;
                    if (scriptValue3 != ScriptValue.NULL) {
                        double d6 = 0.0;
                        double d7 = 0.0;
                        double d8 = 0.0;
                        PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                        object7 = polyClassMachine_v35 != null ? polyClassMachine_v35.tm$68_block_at(d6, d7, d8) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d6), (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)ScriptValue.of((double)d8), (ScriptContext)scriptContext);
                    } else {
                        object7 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue22 = object7;
                    PolyClassGlue polyClassGlue = PolyClassGlue.ofGuarded((ScriptValue)scriptValue21);
                    object6 = polyClassGlue != null ? ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue22)) : PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue21, (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue23 = ScriptValue.of((String)stringBuilder.append(object6.asStr()).toString());
                PolyClassPlayer_v2 polyClassPlayer_v24 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue19);
                v15 = polyClassPlayer_v24 != null ? ScriptValue.of((boolean)polyClassPlayer_v24.tm$42_send_message(scriptValue23.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
            } else {
                v15 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue18, (ScriptValue)scriptContext.getClassOrVar("null")) || (scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                if (scriptValue19 != ScriptValue.NULL) {
                    String string = "<red>Nothing attached to the bearing's face to assemble.";
                    PolyClassPlayer_v2 polyClassPlayer_v25 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue19);
                    v16 = polyClassPlayer_v25 != null ? ScriptValue.of((boolean)polyClassPlayer_v25.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v16 = ScriptValue.NULL;
                }
            } else {
                Object object8;
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("Glue");
                if (scriptValue24 != ScriptValue.NULL) {
                    ScriptValue scriptValue25 = scriptValue18;
                    PolyClassGlue polyClassGlue = PolyClassGlue.ofGuarded((ScriptValue)scriptValue24);
                    object8 = polyClassGlue != null ? ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue25)) : PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue24, (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                if (object8.asBool() ^ true) {
                    if (scriptValue19 != ScriptValue.NULL) {
                        String string = "<red>That structure is not glued together - glue the sails to each other and to the bearing.";
                        PolyClassPlayer_v2 polyClassPlayer_v26 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue19);
                        v18 = polyClassPlayer_v26 != null ? ScriptValue.of((boolean)polyClassPlayer_v26.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    } else {
                        v18 = ScriptValue.NULL;
                    }
                } else {
                    Object object9;
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("Glue");
                    if (scriptValue26 != ScriptValue.NULL) {
                        ScriptValue scriptValue27 = scriptValue18;
                        String string = "sail";
                        PolyClassGlue polyClassGlue = PolyClassGlue.ofGuarded((ScriptValue)scriptValue26);
                        object9 = polyClassGlue != null ? ScriptValue.of((double)polyClassGlue.tm$4_count(scriptValue27, string)) : PolyDispatch.bootstrapCall("memberCall", "count", (ScriptValue)scriptValue26, (ScriptValue)scriptValue27, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    } else {
                        object9 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue28 = object9;
                    builder.val("sails", scriptValue28);
                    if (scriptValue28.asNum() < d) {
                        if (scriptValue19 != ScriptValue.NULL) {
                            ScriptValue scriptValue29 = ScriptValue.of((String)("<red>Not enough sails: <white>" + scriptValue28.asStr() + "<red> of <white>" + ScriptFormula.numToStr((double)d) + "<red> needed."));
                            PolyClassPlayer_v2 polyClassPlayer_v27 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue19);
                            v20 = polyClassPlayer_v27 != null ? ScriptValue.of((boolean)polyClassPlayer_v27.tm$42_send_message(scriptValue29.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, (ScriptValue)scriptValue29, (ScriptContext)scriptContext);
                        } else {
                            v20 = ScriptValue.NULL;
                        }
                    } else {
                        Object object10;
                        ScriptValue scriptValue30 = scriptContext.getClassOrVar("ContraptionManager");
                        if (scriptValue30 != ScriptValue.NULL) {
                            Object object11;
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(scriptValue18);
                            if (scriptValue3 != ScriptValue.NULL) {
                                double d9 = 0.0;
                                double d10 = 0.0;
                                double d11 = 0.0;
                                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                                object11 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$68_block_at(d9, d10, d11) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d9), (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
                            } else {
                                object11 = ScriptValue.NULL;
                            }
                            arrayList.add((ScriptValue)object11);
                            PolyClassContraptionManager polyClassContraptionManager = PolyClassContraptionManager.ofGuarded((ScriptValue)scriptValue30);
                            object10 = polyClassContraptionManager != null ? polyClassContraptionManager.um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue30, arrayList, (ScriptContext)scriptContext);
                        } else {
                            object10 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue31 = object10;
                        builder.val("contraption", scriptValue31);
                        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue31, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                            if (scriptValue19 != ScriptValue.NULL) {
                                String string = "<red>Could not assemble that structure.";
                                PolyClassPlayer_v2 polyClassPlayer_v28 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue19);
                                v23 = polyClassPlayer_v28 != null ? ScriptValue.of((boolean)polyClassPlayer_v28.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                            } else {
                                v23 = ScriptValue.NULL;
                            }
                        } else {
                            ScriptFormula.callBuiltin1((String)"print", (ScriptValue)ScriptValue.of((String)("[windmill] assembled uuid=" + (scriptValue31 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue31, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " block_count=" + (scriptValue31 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_count", (ScriptValue)scriptValue31, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())), (ScriptContext)scriptContext);
                            if (scriptValue3 != ScriptValue.NULL) {
                                String string = "contraption_uuid";
                                String string8 = "string";
                                ScriptValue scriptValue32 = scriptValue31 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue31, (ScriptContext)scriptContext) : ScriptValue.NULL;
                                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                                v24 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string8, scriptValue32)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue32, (ScriptContext)scriptContext);
                            } else {
                                v24 = ScriptValue.NULL;
                            }
                            if (scriptValue3 != ScriptValue.NULL) {
                                String string = "assembled";
                                String string9 = "int";
                                ScriptValue scriptValue33 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 1.0);
                                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                                v25 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string9, scriptValue33)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string9), (ScriptValue)scriptValue33, (ScriptContext)scriptContext);
                            } else {
                                v25 = ScriptValue.NULL;
                            }
                            if (scriptValue3 != ScriptValue.NULL) {
                                String string = "assembled_tick";
                                String string10 = "int";
                                ScriptValue scriptValue34 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)scriptContext);
                                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                                v26 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string10, scriptValue34)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string10), (ScriptValue)scriptValue34, (ScriptContext)scriptContext);
                            } else {
                                v26 = ScriptValue.NULL;
                            }
                            if (scriptValue19 != ScriptValue.NULL) {
                                ScriptValue scriptValue35 = ScriptValue.of((String)("<green>Windmill assembled <gray>(" + scriptValue28.asStr() + " sails)."));
                                PolyClassPlayer_v2 polyClassPlayer_v29 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue19);
                                v27 = polyClassPlayer_v29 != null ? ScriptValue.of((boolean)polyClassPlayer_v29.tm$42_send_message(scriptValue35.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, (ScriptValue)scriptValue35, (ScriptContext)scriptContext);
                            } else {
                                v27 = ScriptValue.NULL;
                            }
                        }
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
