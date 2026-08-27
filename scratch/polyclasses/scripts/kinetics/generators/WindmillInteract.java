/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
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

import dev.arubik.craftengine.script.PolyClassBlock_v2;
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
        double d = 8.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)8.0);
        builder.val("MIN_SAILS", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            String string = "assembled";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("assembled", scriptValue4);
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (polyClassPlayer != null ? polyClassPlayer.tg$49_is_sneaking() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            if (scriptValue4.asNum() > 0.0) {
                ScriptValue scriptValue5;
                Object object2;
                Object object3;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                if (scriptValue6 != ScriptValue.NULL) {
                    String string = "contraption_uuid";
                    String string3 = "string";
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6);
                    object3 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue7 = object3;
                builder.val("cid", scriptValue7);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("ContraptionManager");
                if (scriptValue8 != ScriptValue.NULL) {
                    ScriptValue scriptValue9 = scriptValue7;
                    PolyClassContraptionManager polyClassContraptionManager = PolyClassContraptionManager.ofGuarded((ScriptValue)scriptValue8);
                    object2 = polyClassContraptionManager != null ? polyClassContraptionManager.tm$6_get(scriptValue9.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = object2;
                builder.val("contraption", scriptValue10);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue10, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    Object object4 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "disassemble", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
                }
                if ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue11 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 0.0);
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5);
                    v4 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string4, scriptValue11)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                } else {
                    v4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                if (scriptValue12 != ScriptValue.NULL) {
                    String string = "contraption_uuid";
                    String string5 = "string";
                    ScriptValue scriptValue13 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "");
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue12);
                    v5 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string5, scriptValue13)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                if (scriptValue14 != ScriptValue.NULL) {
                    double d2 = 0.0;
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue14);
                    v6 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d2)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
                } else {
                    v6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                if (scriptValue15 != ScriptValue.NULL) {
                    double d3 = 0.0;
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue15);
                    v7 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d3)) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                } else {
                    v7 = ScriptValue.NULL;
                }
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
                if (scriptValue16 != ScriptValue.NULL) {
                    String string = "<gray>Windmill disassembled.";
                    PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue16);
                    v8 = polyClassPlayer2 != null ? ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v8 = ScriptValue.NULL;
                }
            }
        } else if (scriptValue4.asNum() > 0.0) {
            Object object5;
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
            if (scriptValue17 != ScriptValue.NULL) {
                String string = "windmill_dir";
                String string6 = "int";
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue17);
                object5 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string6) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
            } else {
                object5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue18 = object5;
            builder.val("dir", scriptValue18);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 0.0)))) {
                double d4 = 1.0;
                ScriptValue scriptValue19 = ScriptValue.of((double)1.0);
                builder.val("dir", scriptValue19);
            }
            double d5 = scriptContext.getNum("dir") > 0.0 ? -1.0 : 1.0;
            ScriptValue scriptValue20 = ScriptValue.of((double)d5);
            builder.val("dir", scriptValue20);
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
            if (scriptValue21 != ScriptValue.NULL) {
                String string = "windmill_dir";
                String string7 = "int";
                ScriptValue scriptValue22 = ScriptValue.of((double)d5);
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue21);
                v10 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string7, scriptValue22)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
            } else {
                v10 = ScriptValue.NULL;
            }
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Player");
            if (scriptValue23 != ScriptValue.NULL) {
                ScriptValue scriptValue24 = d5 > 0.0 ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "<gray>Windmill now turns <white>clockwise<gray>.")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "<gray>Windmill now turns <white>counter-clockwise<gray>."));
                PolyClassPlayer polyClassPlayer3 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue23);
                v11 = polyClassPlayer3 != null ? ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(scriptValue24.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue23, (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
            } else {
                v11 = ScriptValue.NULL;
            }
        } else {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue25 = WindmillInteract._seedBlock(builder2);
            builder.val("seed", scriptValue25);
            ScriptValue scriptValue26 = scriptContext.getClassOrVar("Player");
            if (scriptValue26 != ScriptValue.NULL) {
                Object object6;
                ScriptValue scriptValue27;
                ScriptValue scriptValue28;
                ScriptValue scriptValue29;
                ScriptValue scriptValue30;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue31 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue30 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue30, (ScriptContext)scriptContext) : ScriptValue.NULL);
                String string = "facing";
                PolyClassBlock_v2 polyClassBlock_v2 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue31);
                PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                StringBuilder stringBuilder = new StringBuilder().append("<dark_gray>[debug] facing=").append((polyClassBlock_v2 != null ? polyClassBlock_v2.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue31, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext)).asStr()).append(" seed=(").append((polyClassMachine2 != null ? polyClassMachine2.pg$133_facing_dx() : ((scriptValue29 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue29, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(",").append((polyClassMachine3 != null ? polyClassMachine3.pg$129_facing_dy() : ((scriptValue28 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue28, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(",").append((polyClassMachine4 != null ? polyClassMachine4.pg$131_facing_dz() : ((scriptValue27 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(")").append(" self_glued=");
                ScriptValue scriptValue32 = scriptContext.getClassOrVar("Glue");
                if (scriptValue32 != ScriptValue.NULL) {
                    Object object7;
                    ScriptValue scriptValue33 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue33 != ScriptValue.NULL) {
                        double d6 = 0.0;
                        double d7 = 0.0;
                        double d8 = 0.0;
                        PolyClassMachine polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue33);
                        object7 = polyClassMachine5 != null ? polyClassMachine5.tm$68_block_at(d6, d7, d8) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue33, (ScriptValue)ScriptValue.of((double)d6), (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)ScriptValue.of((double)d8), (ScriptContext)scriptContext);
                    } else {
                        object7 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue34 = object7;
                    PolyClassGlue polyClassGlue = PolyClassGlue.ofGuarded((ScriptValue)scriptValue32);
                    object6 = polyClassGlue != null ? ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue34)) : PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue32, (ScriptValue)scriptValue34, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue35 = ScriptValue.of((String)stringBuilder.append(object6.asStr()).toString());
                PolyClassPlayer polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue26);
                v15 = polyClassPlayer4 != null ? ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(scriptValue35.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue26, (ScriptValue)scriptValue35, (ScriptContext)scriptContext);
            } else {
                v15 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue25, (ScriptValue)scriptContext.getClassOrVar("null")) || (scriptValue25 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue25, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                ScriptValue scriptValue36 = scriptContext.getClassOrVar("Player");
                if (scriptValue36 != ScriptValue.NULL) {
                    String string = "<red>Nothing attached to the bearing's face to assemble.";
                    PolyClassPlayer polyClassPlayer5 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue36);
                    v16 = polyClassPlayer5 != null ? ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue36, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v16 = ScriptValue.NULL;
                }
            } else {
                Object object8;
                ScriptValue scriptValue37 = scriptContext.getClassOrVar("Glue");
                if (scriptValue37 != ScriptValue.NULL) {
                    ScriptValue scriptValue38 = scriptValue25;
                    PolyClassGlue polyClassGlue = PolyClassGlue.ofGuarded((ScriptValue)scriptValue37);
                    object8 = polyClassGlue != null ? ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue38)) : PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue37, (ScriptValue)scriptValue38, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                if (object8.asBool() ^ true) {
                    ScriptValue scriptValue39 = scriptContext.getClassOrVar("Player");
                    if (scriptValue39 != ScriptValue.NULL) {
                        String string = "<red>That structure is not glued together - glue the sails to each other and to the bearing.";
                        PolyClassPlayer polyClassPlayer6 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue39);
                        v18 = polyClassPlayer6 != null ? ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue39, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    } else {
                        v18 = ScriptValue.NULL;
                    }
                } else {
                    Object object9;
                    ScriptValue scriptValue40 = scriptContext.getClassOrVar("Glue");
                    if (scriptValue40 != ScriptValue.NULL) {
                        ScriptValue scriptValue41 = scriptValue25;
                        String string = "sail";
                        PolyClassGlue polyClassGlue = PolyClassGlue.ofGuarded((ScriptValue)scriptValue40);
                        object9 = polyClassGlue != null ? ScriptValue.of((double)polyClassGlue.tm$4_count(scriptValue41, string)) : PolyDispatch.bootstrapCall("memberCall", "count", (ScriptValue)scriptValue40, (ScriptValue)scriptValue41, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    } else {
                        object9 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue42 = object9;
                    builder.val("sails", scriptValue42);
                    if (scriptValue42.asNum() < d) {
                        ScriptValue scriptValue43 = scriptContext.getClassOrVar("Player");
                        if (scriptValue43 != ScriptValue.NULL) {
                            ScriptValue scriptValue44 = ScriptValue.of((String)("<red>Not enough sails: <white>" + scriptValue42.asStr() + "<red> of <white>" + ScriptFormula.numToStr((double)d) + "<red> needed."));
                            PolyClassPlayer polyClassPlayer7 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue43);
                            v20 = polyClassPlayer7 != null ? ScriptValue.of((boolean)polyClassPlayer7.tm$42_send_message(scriptValue44.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue43, (ScriptValue)scriptValue44, (ScriptContext)scriptContext);
                        } else {
                            v20 = ScriptValue.NULL;
                        }
                    } else {
                        Object object10;
                        ScriptValue scriptValue45 = scriptContext.getClassOrVar("ContraptionManager");
                        if (scriptValue45 != ScriptValue.NULL) {
                            Object object11;
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(scriptValue25);
                            ScriptValue scriptValue46 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue46 != ScriptValue.NULL) {
                                double d9 = 0.0;
                                double d10 = 0.0;
                                double d11 = 0.0;
                                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue46);
                                object11 = polyClassMachine != null ? polyClassMachine.tm$68_block_at(d9, d10, d11) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue46, (ScriptValue)ScriptValue.of((double)d9), (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
                            } else {
                                object11 = ScriptValue.NULL;
                            }
                            arrayList.add((ScriptValue)object11);
                            PolyClassContraptionManager polyClassContraptionManager = PolyClassContraptionManager.ofGuarded((ScriptValue)scriptValue45);
                            object10 = polyClassContraptionManager != null ? polyClassContraptionManager.um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue45, arrayList, (ScriptContext)scriptContext);
                        } else {
                            object10 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue47 = object10;
                        builder.val("contraption", scriptValue47);
                        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue47, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                            ScriptValue scriptValue48 = scriptContext.getClassOrVar("Player");
                            if (scriptValue48 != ScriptValue.NULL) {
                                String string = "<red>Could not assemble that structure.";
                                PolyClassPlayer polyClassPlayer8 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue48);
                                v23 = polyClassPlayer8 != null ? ScriptValue.of((boolean)polyClassPlayer8.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue48, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                            } else {
                                v23 = ScriptValue.NULL;
                            }
                        } else {
                            ScriptFormula.callBuiltin1((String)"print", (ScriptValue)ScriptValue.of((String)("[windmill] assembled uuid=" + (scriptValue47 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue47, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " block_count=" + (scriptValue47 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_count", (ScriptValue)scriptValue47, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())), (ScriptContext)scriptContext);
                            ScriptValue scriptValue49 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue49 != ScriptValue.NULL) {
                                String string = "contraption_uuid";
                                String string8 = "string";
                                ScriptValue scriptValue50 = scriptValue47 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue47, (ScriptContext)scriptContext) : ScriptValue.NULL;
                                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue49);
                                v24 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string8, scriptValue50)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue49, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue50, (ScriptContext)scriptContext);
                            } else {
                                v24 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue51 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue51 != ScriptValue.NULL) {
                                String string = "assembled";
                                String string9 = "int";
                                ScriptValue scriptValue52 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 1.0);
                                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue51);
                                v25 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string9, scriptValue52)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue51, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string9), (ScriptValue)scriptValue52, (ScriptContext)scriptContext);
                            } else {
                                v25 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue53 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue53 != ScriptValue.NULL) {
                                String string = "assembled_tick";
                                String string10 = "int";
                                ScriptValue scriptValue54 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)scriptContext);
                                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue53);
                                v26 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string10, scriptValue54)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue53, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string10), (ScriptValue)scriptValue54, (ScriptContext)scriptContext);
                            } else {
                                v26 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue55 = scriptContext.getClassOrVar("Player");
                            if (scriptValue55 != ScriptValue.NULL) {
                                ScriptValue scriptValue56 = ScriptValue.of((String)("<green>Windmill assembled <gray>(" + scriptValue42.asStr() + " sails)."));
                                PolyClassPlayer polyClassPlayer9 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue55);
                                v27 = polyClassPlayer9 != null ? ScriptValue.of((boolean)polyClassPlayer9.tm$42_send_message(scriptValue56.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue55, (ScriptValue)scriptValue56, (ScriptContext)scriptContext);
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
