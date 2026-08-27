/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassGlue
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContraptionManager;
import dev.arubik.craftengine.script.PolyClassGlue;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
            ScriptValue.Obj obj;
            Object object4;
            double d = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object4);
                object3 = polyClassMachine_v3.tm$68_block_at(d, d2, d3);
            } else {
                object3 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object3;
        builder.val("self", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Glue");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue4 = scriptValue2;
            if (scriptValue3 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                PolyClassGlue polyClassGlue = new PolyClassGlue(object5);
                object2 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue4));
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        if (object2.asBool()) {
            return scriptValue2;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue6;
            PolyClassMachine_v3 polyClassMachine_v3;
            PolyClassMachine_v3 polyClassMachine_v32;
            PolyClassMachine_v3 polyClassMachine_v33;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? ((polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassMachine_v33.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassMachine_v32.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            Object object7 = scriptValue11 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassMachine_v3.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : (scriptValue6 = ScriptValue.NULL);
            if (scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v34 = new PolyClassMachine_v3(object6);
                object = polyClassMachine_v34.tm$68_block_at(scriptValue8.asNum(), scriptValue10.asNum(), scriptValue6.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue5, (ScriptValue)scriptValue8, (ScriptValue)scriptValue10, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassPlayer polyClassPlayer;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "assembled";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("assembled", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        boolean bl = scriptValue3 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassPlayer.tg$49_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl) {
            if (scriptValue2.asNum() > 0.0) {
                ScriptValue scriptValue4;
                Object object3;
                Object object4;
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
                if (scriptValue5 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    String string = "contraption_uuid";
                    String string3 = "string";
                    if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object5);
                        object4 = polyClassMachine_v3.tm$34_get_typed(string, string3);
                    } else {
                        object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                    }
                } else {
                    object4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue6 = object4;
                builder.val("cid", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("ContraptionManager");
                if (scriptValue7 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object6;
                    ScriptValue scriptValue8 = scriptValue6;
                    if (scriptValue7 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("ContraptionManager")) {
                        PolyClassContraptionManager polyClassContraptionManager = new PolyClassContraptionManager(object6);
                        object3 = polyClassContraptionManager.tm$6_get(scriptValue8.asStr());
                    } else {
                        object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                    }
                } else {
                    object3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue9 = object3;
                builder.val("contraption", scriptValue9);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue9, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("contraption");
                    Object object7 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "disassemble", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
                }
                if ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object8;
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue11 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearingInteract.class, 0.0);
                    if (scriptValue4 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object8);
                        v5 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue11));
                    } else {
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                if (scriptValue12 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object9;
                    String string = "contraption_uuid";
                    String string5 = "string";
                    ScriptValue scriptValue13 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearingInteract.class, "");
                    if (scriptValue12 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object9);
                        v6 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string5, scriptValue13));
                    } else {
                        v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
                    }
                } else {
                    v6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                if (scriptValue14 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object10;
                    double d = 0.0;
                    if (scriptValue14 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object10);
                        v7 = ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d));
                    } else {
                        v7 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                    }
                } else {
                    v7 = ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
                if (scriptValue15 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object11;
                    String string = "<gray>Rotational bearing disassembled.";
                    if (scriptValue15 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object11);
                        v8 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string));
                    } else {
                        v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v8 = ScriptValue.NULL;
                }
            }
        } else if (scriptValue2.asNum() > 0.0) {
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                String string = "<gray>Already assembled - feed RPM from below to turn it.";
                if (scriptValue16 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object12);
                    v9 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string));
                } else {
                    v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue17;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue18 = RotationalBearingInteract._seedBlock(builder2);
            builder.val("seed", scriptValue18);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue18, (ScriptValue)scriptContext.getClassOrVar("null")) || ((scriptValue17 = scriptContext.getClassOrVar("seed")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("Player");
                if (scriptValue19 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object13;
                    String string = "<red>Nothing attached to the bearing's face to assemble.";
                    if (scriptValue19 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer4 = new PolyClassPlayer(object13);
                        v10 = ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(string));
                    } else {
                        v10 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v10 = ScriptValue.NULL;
                }
            } else {
                Object object14;
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("Glue");
                if (scriptValue20 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object15;
                    ScriptValue scriptValue21 = scriptValue18;
                    if (scriptValue20 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                        PolyClassGlue polyClassGlue = new PolyClassGlue(object15);
                        object14 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue21));
                    } else {
                        object14 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue20, (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
                    }
                } else {
                    object14 = ScriptValue.NULL;
                }
                if (object14.asBool() ^ true) {
                    ScriptValue scriptValue22 = scriptContext.getClassOrVar("Player");
                    if (scriptValue22 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object16;
                        String string = "<red>That structure is not glued together - glue it to itself and to the bearing.";
                        if (scriptValue22 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer5 = new PolyClassPlayer(object16);
                            v12 = ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(string));
                        } else {
                            v12 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        }
                    } else {
                        v12 = ScriptValue.NULL;
                    }
                } else {
                    Object object17;
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("ContraptionManager");
                    if (scriptValue23 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object18;
                        Object object19;
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue18);
                        ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue24 != ScriptValue.NULL) {
                            ScriptValue.Obj obj2;
                            Object object20;
                            double d = 0.0;
                            double d2 = 0.0;
                            double d3 = 0.0;
                            if (scriptValue24 instanceof ScriptValue.Obj && (object20 = (obj2 = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object20 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object20);
                                object19 = polyClassMachine_v3.tm$68_block_at(d, d2, d3);
                            } else {
                                object19 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                            }
                        } else {
                            object19 = ScriptValue.NULL;
                        }
                        arrayList.add((ScriptValue)object19);
                        object17 = scriptValue23 instanceof ScriptValue.Obj && (object18 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object18 instanceof PolyClass) && obj.typeName().equals("ContraptionManager") ? new PolyClassContraptionManager(object18).um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue23, arrayList, (ScriptContext)scriptContext);
                    } else {
                        object17 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue25 = object17;
                    builder.val("contraption", scriptValue25);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue25, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                        ScriptValue scriptValue26 = scriptContext.getClassOrVar("Player");
                        if (scriptValue26 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object21;
                            String string = "<red>Could not assemble that structure.";
                            if (scriptValue26 instanceof ScriptValue.Obj && (object21 = (obj = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object21 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object21);
                                v15 = ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string));
                            } else {
                                v15 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                            }
                        } else {
                            v15 = ScriptValue.NULL;
                        }
                    } else {
                        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue27 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object22;
                            ScriptValue scriptValue28;
                            String string = "contraption_uuid";
                            String string6 = "string";
                            ScriptValue scriptValue29 = scriptContext.getClassOrVar("contraption");
                            Object object23 = scriptValue28 = scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue29, (ScriptContext)scriptContext) : ScriptValue.NULL;
                            if (scriptValue27 instanceof ScriptValue.Obj && (object22 = (obj = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object22 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object22);
                                v17 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string6, scriptValue28));
                            } else {
                                v17 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue27, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue28, (ScriptContext)scriptContext);
                            }
                        } else {
                            v17 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue30 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue30 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object24;
                            String string = "assembled";
                            String string7 = "int";
                            ScriptValue scriptValue31 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearingInteract.class, 1.0);
                            if (scriptValue30 instanceof ScriptValue.Obj && (object24 = (obj = (ScriptValue.Obj)scriptValue30).instance()) != null && !(object24 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object24);
                                v18 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string7, scriptValue31));
                            } else {
                                v18 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue30, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue31, (ScriptContext)scriptContext);
                            }
                        } else {
                            v18 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue32 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue32 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object25;
                            String string = "assembled_tick";
                            String string8 = "int";
                            ArrayList arrayList = new ArrayList();
                            ScriptValue scriptValue33 = ScriptFormula.callBuiltin((String)"tick", arrayList, (ScriptContext)scriptContext);
                            if (scriptValue32 instanceof ScriptValue.Obj && (object25 = (obj = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object25 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object25);
                                v19 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string8, scriptValue33));
                            } else {
                                v19 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue32, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue33, (ScriptContext)scriptContext);
                            }
                        } else {
                            v19 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue34 = scriptContext.getClassOrVar("Player");
                        if (scriptValue34 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object26;
                            ScriptValue scriptValue35;
                            StringBuilder stringBuilder = new StringBuilder().append("<green>Rotational bearing assembled <gray>(").append(((scriptValue35 = scriptContext.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_count", (ScriptValue)scriptValue35, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()).append(" blocks, weight ");
                            ScriptValue scriptValue36 = scriptContext.getClassOrVar("contraption");
                            ScriptValue scriptValue37 = ScriptValue.of((String)stringBuilder.append(ScriptFormula.numToStr((double)Math.floor((scriptValue36 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "weight", (ScriptValue)scriptValue36, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum()))).append(").").toString());
                            if (scriptValue34 instanceof ScriptValue.Obj && (object26 = (obj = (ScriptValue.Obj)scriptValue34).instance()) != null && !(object26 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer7 = new PolyClassPlayer(object26);
                                v21 = ScriptValue.of((boolean)polyClassPlayer7.tm$42_send_message(scriptValue37.asStr()));
                            } else {
                                v21 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue34, (ScriptValue)scriptValue37, (ScriptContext)scriptContext);
                            }
                        } else {
                            v21 = ScriptValue.NULL;
                        }
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
