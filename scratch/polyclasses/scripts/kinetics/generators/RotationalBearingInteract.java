/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassGlue
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
            ScriptValue.Obj obj;
            Object object4;
            double d = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                object3 = polyClassMachine.tm$68_block_at(d, d2, d3);
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
            ScriptValue scriptValue7;
            ScriptValue scriptValue8;
            ScriptValue scriptValue9;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue10 = polyClassMachine != null ? polyClassMachine.pg$133_facing_dx() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL);
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue11 = polyClassMachine2 != null ? polyClassMachine2.pg$129_facing_dy() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
            PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            Object object7 = polyClassMachine3 != null ? polyClassMachine3.pg$131_facing_dz() : (scriptValue7 = (scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL);
            if (scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine4 = new PolyClassMachine(object6);
                object = polyClassMachine4.tm$68_block_at(scriptValue10.asNum(), scriptValue11.asNum(), scriptValue7.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue5, (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "assembled";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("assembled", scriptValue3);
        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (polyClassPlayer_v2 != null ? polyClassPlayer_v2.tg$49_is_sneaking() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            if (scriptValue3.asNum() > 0.0) {
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
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object5);
                        object4 = polyClassMachine.tm$34_get_typed(string, string3);
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
                    Object object7 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "disassemble", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
                }
                if ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object8;
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue10 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearingInteract.class, 0.0);
                    if (scriptValue4 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object8);
                        v4 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string4, scriptValue10));
                    } else {
                        v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                    }
                } else {
                    v4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                if (scriptValue11 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object9;
                    String string = "contraption_uuid";
                    String string5 = "string";
                    ScriptValue scriptValue12 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RotationalBearingInteract.class, "");
                    if (scriptValue11 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object9);
                        v5 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string5, scriptValue12));
                    } else {
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                if (scriptValue13 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object10;
                    double d = 0.0;
                    if (scriptValue13 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object10);
                        v6 = ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d));
                    } else {
                        v6 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                    }
                } else {
                    v6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
                if (scriptValue14 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object11;
                    String string = "<gray>Rotational bearing disassembled.";
                    if (scriptValue14 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer_v2 polyClassPlayer_v22 = new PolyClassPlayer_v2(object11);
                        v7 = ScriptValue.of((boolean)polyClassPlayer_v22.tm$42_send_message(string));
                    } else {
                        v7 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v7 = ScriptValue.NULL;
                }
            }
        } else if (scriptValue3.asNum() > 0.0) {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                String string = "<gray>Already assembled - feed RPM from below to turn it.";
                if (scriptValue15 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer_v2 polyClassPlayer_v23 = new PolyClassPlayer_v2(object12);
                    v8 = ScriptValue.of((boolean)polyClassPlayer_v23.tm$42_send_message(string));
                } else {
                    v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
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
                    ScriptValue.Obj obj;
                    Object object13;
                    String string = "<red>Nothing attached to the bearing's face to assemble.";
                    if (scriptValue17 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer_v2 polyClassPlayer_v24 = new PolyClassPlayer_v2(object13);
                        v9 = ScriptValue.of((boolean)polyClassPlayer_v24.tm$42_send_message(string));
                    } else {
                        v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v9 = ScriptValue.NULL;
                }
            } else {
                Object object14;
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Glue");
                if (scriptValue18 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object15;
                    ScriptValue scriptValue19 = scriptValue16;
                    if (scriptValue18 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                        PolyClassGlue polyClassGlue = new PolyClassGlue(object15);
                        object14 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue19));
                    } else {
                        object14 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
                    }
                } else {
                    object14 = ScriptValue.NULL;
                }
                if (object14.asBool() ^ true) {
                    ScriptValue scriptValue20 = scriptContext.getClassOrVar("Player");
                    if (scriptValue20 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object16;
                        String string = "<red>That structure is not glued together - glue it to itself and to the bearing.";
                        if (scriptValue20 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer_v2 polyClassPlayer_v25 = new PolyClassPlayer_v2(object16);
                            v11 = ScriptValue.of((boolean)polyClassPlayer_v25.tm$42_send_message(string));
                        } else {
                            v11 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        }
                    } else {
                        v11 = ScriptValue.NULL;
                    }
                } else {
                    Object object17;
                    ScriptValue scriptValue21 = scriptContext.getClassOrVar("ContraptionManager");
                    if (scriptValue21 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object18;
                        Object object19;
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue16);
                        ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue22 != ScriptValue.NULL) {
                            ScriptValue.Obj obj2;
                            Object object20;
                            double d = 0.0;
                            double d2 = 0.0;
                            double d3 = 0.0;
                            if (scriptValue22 instanceof ScriptValue.Obj && (object20 = (obj2 = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object20 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                                PolyClassMachine polyClassMachine = new PolyClassMachine(object20);
                                object19 = polyClassMachine.tm$68_block_at(d, d2, d3);
                            } else {
                                object19 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                            }
                        } else {
                            object19 = ScriptValue.NULL;
                        }
                        arrayList.add((ScriptValue)object19);
                        object17 = scriptValue21 instanceof ScriptValue.Obj && (object18 = (obj = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object18 instanceof PolyClass) && obj.typeName().equals("ContraptionManager") ? new PolyClassContraptionManager(object18).um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue21, arrayList, (ScriptContext)scriptContext);
                    } else {
                        object17 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue23 = object17;
                    builder.val("contraption", scriptValue23);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue23, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                        ScriptValue scriptValue24 = scriptContext.getClassOrVar("Player");
                        if (scriptValue24 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object21;
                            String string = "<red>Could not assemble that structure.";
                            if (scriptValue24 instanceof ScriptValue.Obj && (object21 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object21 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer_v2 polyClassPlayer_v26 = new PolyClassPlayer_v2(object21);
                                v14 = ScriptValue.of((boolean)polyClassPlayer_v26.tm$42_send_message(string));
                            } else {
                                v14 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                            }
                        } else {
                            v14 = ScriptValue.NULL;
                        }
                    } else {
                        ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue25 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object22;
                            ScriptValue scriptValue26;
                            String string = "contraption_uuid";
                            String string6 = "string";
                            Object object23 = scriptValue26 = scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL;
                            if (scriptValue25 instanceof ScriptValue.Obj && (object22 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object22 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                PolyClassMachine polyClassMachine = new PolyClassMachine(object22);
                                v16 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string6, scriptValue26));
                            } else {
                                v16 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue25, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
                            }
                        } else {
                            v16 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue27 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object24;
                            String string = "assembled";
                            String string7 = "int";
                            ScriptValue scriptValue28 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RotationalBearingInteract.class, 1.0);
                            if (scriptValue27 instanceof ScriptValue.Obj && (object24 = (obj = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object24 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                PolyClassMachine polyClassMachine = new PolyClassMachine(object24);
                                v17 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string7, scriptValue28));
                            } else {
                                v17 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue27, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue28, (ScriptContext)scriptContext);
                            }
                        } else {
                            v17 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue29 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue29 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object25;
                            String string = "assembled_tick";
                            String string8 = "int";
                            ScriptValue scriptValue30 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)scriptContext);
                            if (scriptValue29 instanceof ScriptValue.Obj && (object25 = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object25 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                PolyClassMachine polyClassMachine = new PolyClassMachine(object25);
                                v18 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string8, scriptValue30));
                            } else {
                                v18 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue29, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue30, (ScriptContext)scriptContext);
                            }
                        } else {
                            v18 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue31 = scriptContext.getClassOrVar("Player");
                        if (scriptValue31 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object26;
                            ScriptValue scriptValue32 = ScriptValue.of((String)("<green>Rotational bearing assembled <gray>(" + (scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_count", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " blocks, weight " + ScriptFormula.numToStr((double)Math.floor((scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "weight", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum())) + ")."));
                            if (scriptValue31 instanceof ScriptValue.Obj && (object26 = (obj = (ScriptValue.Obj)scriptValue31).instance()) != null && !(object26 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer_v2 polyClassPlayer_v27 = new PolyClassPlayer_v2(object26);
                                v19 = ScriptValue.of((boolean)polyClassPlayer_v27.tm$42_send_message(scriptValue32.asStr()));
                            } else {
                                v19 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue31, (ScriptValue)scriptValue32, (ScriptContext)scriptContext);
                            }
                        } else {
                            v19 = ScriptValue.NULL;
                        }
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
