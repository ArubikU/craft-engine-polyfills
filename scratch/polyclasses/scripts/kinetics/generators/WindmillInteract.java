/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
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
import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassContraptionManager;
import dev.arubik.craftengine.script.PolyClassGlue;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
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
        double d = 8.0;
        ScriptValue scriptValue = ScriptValue.of((double)8.0);
        builder.val("MIN_SAILS", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "assembled";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("assembled", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        boolean bl = scriptValue4 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassPlayer.tg$49_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl) {
            if (scriptValue3.asNum() > 0.0) {
                ScriptValue scriptValue5;
                Object object3;
                Object object4;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                if (scriptValue6 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object5;
                    String string = "contraption_uuid";
                    String string3 = "string";
                    if (scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object5);
                        object4 = polyClassMachine_v3.tm$34_get_typed(string, string3);
                    } else {
                        object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                    }
                } else {
                    object4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue7 = object4;
                builder.val("cid", scriptValue7);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("ContraptionManager");
                if (scriptValue8 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object6;
                    ScriptValue scriptValue9 = scriptValue7;
                    if (scriptValue8 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("ContraptionManager")) {
                        PolyClassContraptionManager polyClassContraptionManager = new PolyClassContraptionManager(object6);
                        object3 = polyClassContraptionManager.tm$6_get(scriptValue9.asStr());
                    } else {
                        object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                    }
                } else {
                    object3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = object3;
                builder.val("contraption", scriptValue10);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue10, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("contraption");
                    Object object7 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "disassemble", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                }
                if ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object8;
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue12 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 0.0);
                    if (scriptValue5 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object8);
                        v5 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue12));
                    } else {
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                if (scriptValue13 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object9;
                    String string = "contraption_uuid";
                    String string5 = "string";
                    ScriptValue scriptValue14 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "");
                    if (scriptValue13 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object9);
                        v6 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string5, scriptValue14));
                    } else {
                        v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
                    }
                } else {
                    v6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                if (scriptValue15 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object10;
                    double d2 = 0.0;
                    if (scriptValue15 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object10);
                        v7 = ScriptValue.of((boolean)polyClassMachine_v3.tm$106_set_rpm_output(d2));
                    } else {
                        v7 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
                    }
                } else {
                    v7 = ScriptValue.NULL;
                }
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
                if (scriptValue16 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object11;
                    double d3 = 0.0;
                    if (scriptValue16 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object11);
                        v8 = ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d3));
                    } else {
                        v8 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                    }
                } else {
                    v8 = ScriptValue.NULL;
                }
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("Player");
                if (scriptValue17 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object12;
                    String string = "<gray>Windmill disassembled.";
                    if (scriptValue17 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object12);
                        v9 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string));
                    } else {
                        v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v9 = ScriptValue.NULL;
                }
            }
        } else if (scriptValue3.asNum() > 0.0) {
            Object object13;
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
            if (scriptValue18 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                String string = "windmill_dir";
                String string6 = "int";
                if (scriptValue18 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object14);
                    object13 = polyClassMachine_v3.tm$34_get_typed(string, string6);
                } else {
                    object13 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
                }
            } else {
                object13 = ScriptValue.NULL;
            }
            ScriptValue scriptValue19 = object13;
            builder.val("dir", scriptValue19);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 0.0)))) {
                double d4 = 1.0;
                ScriptValue scriptValue20 = ScriptValue.of((double)1.0);
                builder.val("dir", scriptValue20);
            }
            double d5 = scriptContext.getNum("dir") > 0.0 ? -1.0 : 1.0;
            ScriptValue scriptValue21 = ScriptValue.of((double)d5);
            builder.val("dir", scriptValue21);
            ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
            if (scriptValue22 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object15;
                String string = "windmill_dir";
                String string7 = "int";
                ScriptValue scriptValue23 = ScriptValue.of((double)d5);
                if (scriptValue22 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object15);
                    v11 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string7, scriptValue23));
                } else {
                    v11 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
                }
            } else {
                v11 = ScriptValue.NULL;
            }
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("Player");
            if (scriptValue24 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                ScriptValue scriptValue25;
                ScriptValue scriptValue26 = scriptValue25 = d5 > 0.0 ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "<gray>Windmill now turns <white>clockwise<gray>.")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "<gray>Windmill now turns <white>counter-clockwise<gray>."));
                if (scriptValue24 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object16);
                    v13 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(scriptValue25.asStr()));
                } else {
                    v13 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue24, (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
                }
            } else {
                v13 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue27;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue28 = WindmillInteract._seedBlock(builder2);
            builder.val("seed", scriptValue28);
            ScriptValue scriptValue29 = scriptContext.getClassOrVar("Player");
            if (scriptValue29 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object17;
                Object object18;
                PolyClassMachine_v3 polyClassMachine_v3;
                PolyClassMachine_v3 polyClassMachine_v32;
                PolyClassMachine_v3 polyClassMachine_v33;
                CallSite callSite;
                ScriptValue.Obj obj2;
                Object object19;
                PolyClassMachine_v3 polyClassMachine_v34;
                StringBuilder stringBuilder = new StringBuilder().append("<dark_gray>[debug] facing=");
                ScriptValue scriptValue30 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue31 = scriptValue30 != ScriptValue.NULL ? ((polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue30)) != null ? polyClassMachine_v34.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue30, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                String string = "facing";
                if (scriptValue31 instanceof ScriptValue.Obj && (object19 = (obj2 = (ScriptValue.Obj)scriptValue31).instance()) != null && !(object19 instanceof PolyClass) && obj2.typeName().equals("Block")) {
                    PolyClassBlock_v2 polyClassBlock_v2 = new PolyClassBlock_v2(object19);
                    callSite = polyClassBlock_v2.tm$24_property(string);
                } else {
                    callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue31, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
                ScriptValue scriptValue32 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue33 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue34 = scriptContext.getClassOrVar("Machine");
                StringBuilder stringBuilder2 = stringBuilder.append(callSite.asStr()).append(" seed=(").append((scriptValue32 != ScriptValue.NULL ? ((polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue32)) != null ? polyClassMachine_v33.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue32, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr()).append(",").append((scriptValue33 != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue33)) != null ? polyClassMachine_v32.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue33, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr()).append(",").append((scriptValue34 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue34)) != null ? polyClassMachine_v3.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue34, (ScriptContext)scriptContext)) : ScriptValue.NULL).asStr()).append(")").append(" self_glued=");
                ScriptValue scriptValue35 = scriptContext.getClassOrVar("Glue");
                if (scriptValue35 != ScriptValue.NULL) {
                    ScriptValue.Obj obj3;
                    Object object20;
                    ScriptValue scriptValue36;
                    ScriptValue scriptValue37 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue37 != ScriptValue.NULL) {
                        ScriptValue.Obj obj4;
                        Object object21;
                        double d6 = 0.0;
                        double d7 = 0.0;
                        double d8 = 0.0;
                        if (scriptValue37 instanceof ScriptValue.Obj && (object21 = (obj4 = (ScriptValue.Obj)scriptValue37).instance()) != null && !(object21 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                            PolyClassMachine_v3 polyClassMachine_v35 = new PolyClassMachine_v3(object21);
                            v17 = polyClassMachine_v35.tm$68_block_at(d6, d7, d8);
                        } else {
                            v17 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue37, (ScriptValue)ScriptValue.of((double)d6), (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)ScriptValue.of((double)d8), (ScriptContext)scriptContext);
                        }
                    } else {
                        v17 = scriptValue36 = ScriptValue.NULL;
                    }
                    if (scriptValue35 instanceof ScriptValue.Obj && (object20 = (obj3 = (ScriptValue.Obj)scriptValue35).instance()) != null && !(object20 instanceof PolyClass) && obj3.typeName().equals("Glue")) {
                        PolyClassGlue polyClassGlue = new PolyClassGlue(object20);
                        object18 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue36));
                    } else {
                        object18 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue35, (ScriptValue)scriptValue36, (ScriptContext)scriptContext);
                    }
                } else {
                    object18 = ScriptValue.NULL;
                }
                ScriptValue scriptValue38 = ScriptValue.of((String)stringBuilder2.append(object18.asStr()).toString());
                if (scriptValue29 instanceof ScriptValue.Obj && (object17 = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object17 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer4 = new PolyClassPlayer(object17);
                    v19 = ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(scriptValue38.asStr()));
                } else {
                    v19 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue29, (ScriptValue)scriptValue38, (ScriptContext)scriptContext);
                }
            } else {
                v19 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue28, (ScriptValue)scriptContext.getClassOrVar("null")) || ((scriptValue27 = scriptContext.getClassOrVar("seed")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                ScriptValue scriptValue39 = scriptContext.getClassOrVar("Player");
                if (scriptValue39 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object22;
                    String string = "<red>Nothing attached to the bearing's face to assemble.";
                    if (scriptValue39 instanceof ScriptValue.Obj && (object22 = (obj = (ScriptValue.Obj)scriptValue39).instance()) != null && !(object22 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer5 = new PolyClassPlayer(object22);
                        v20 = ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(string));
                    } else {
                        v20 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue39, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v20 = ScriptValue.NULL;
                }
            } else {
                Object object23;
                ScriptValue scriptValue40 = scriptContext.getClassOrVar("Glue");
                if (scriptValue40 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object24;
                    ScriptValue scriptValue41 = scriptValue28;
                    if (scriptValue40 instanceof ScriptValue.Obj && (object24 = (obj = (ScriptValue.Obj)scriptValue40).instance()) != null && !(object24 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                        PolyClassGlue polyClassGlue = new PolyClassGlue(object24);
                        object23 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue41));
                    } else {
                        object23 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue40, (ScriptValue)scriptValue41, (ScriptContext)scriptContext);
                    }
                } else {
                    object23 = ScriptValue.NULL;
                }
                if (object23.asBool() ^ true) {
                    ScriptValue scriptValue42 = scriptContext.getClassOrVar("Player");
                    if (scriptValue42 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object25;
                        String string = "<red>That structure is not glued together - glue the sails to each other and to the bearing.";
                        if (scriptValue42 instanceof ScriptValue.Obj && (object25 = (obj = (ScriptValue.Obj)scriptValue42).instance()) != null && !(object25 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object25);
                            v22 = ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string));
                        } else {
                            v22 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue42, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        }
                    } else {
                        v22 = ScriptValue.NULL;
                    }
                } else {
                    Object object26;
                    ScriptValue scriptValue43 = scriptContext.getClassOrVar("Glue");
                    if (scriptValue43 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object27;
                        ScriptValue scriptValue44 = scriptValue28;
                        String string = "sail";
                        if (scriptValue43 instanceof ScriptValue.Obj && (object27 = (obj = (ScriptValue.Obj)scriptValue43).instance()) != null && !(object27 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                            PolyClassGlue polyClassGlue = new PolyClassGlue(object27);
                            object26 = ScriptValue.of((double)polyClassGlue.tm$4_count(scriptValue44, string));
                        } else {
                            object26 = PolyDispatch.bootstrapCall("memberCall", "count", (ScriptValue)scriptValue43, (ScriptValue)scriptValue44, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        }
                    } else {
                        object26 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue45 = object26;
                    builder.val("sails", scriptValue45);
                    if (scriptValue45.asNum() < d) {
                        ScriptValue scriptValue46 = scriptContext.getClassOrVar("Player");
                        if (scriptValue46 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object28;
                            ScriptValue scriptValue47 = ScriptValue.of((String)("<red>Not enough sails: <white>" + scriptValue45.asStr() + "<red> of <white>" + ScriptFormula.numToStr((double)d) + "<red> needed."));
                            if (scriptValue46 instanceof ScriptValue.Obj && (object28 = (obj = (ScriptValue.Obj)scriptValue46).instance()) != null && !(object28 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer7 = new PolyClassPlayer(object28);
                                v24 = ScriptValue.of((boolean)polyClassPlayer7.tm$42_send_message(scriptValue47.asStr()));
                            } else {
                                v24 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue46, (ScriptValue)scriptValue47, (ScriptContext)scriptContext);
                            }
                        } else {
                            v24 = ScriptValue.NULL;
                        }
                    } else {
                        Object object29;
                        ScriptValue scriptValue48 = scriptContext.getClassOrVar("ContraptionManager");
                        if (scriptValue48 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object30;
                            Object object31;
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(scriptValue28);
                            ScriptValue scriptValue49 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue49 != ScriptValue.NULL) {
                                ScriptValue.Obj obj5;
                                Object object32;
                                double d9 = 0.0;
                                double d10 = 0.0;
                                double d11 = 0.0;
                                if (scriptValue49 instanceof ScriptValue.Obj && (object32 = (obj5 = (ScriptValue.Obj)scriptValue49).instance()) != null && !(object32 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object32);
                                    object31 = polyClassMachine_v3.tm$68_block_at(d9, d10, d11);
                                } else {
                                    object31 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue49, (ScriptValue)ScriptValue.of((double)d9), (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
                                }
                            } else {
                                object31 = ScriptValue.NULL;
                            }
                            arrayList.add((ScriptValue)object31);
                            object29 = scriptValue48 instanceof ScriptValue.Obj && (object30 = (obj = (ScriptValue.Obj)scriptValue48).instance()) != null && !(object30 instanceof PolyClass) && obj.typeName().equals("ContraptionManager") ? new PolyClassContraptionManager(object30).um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue48, arrayList, (ScriptContext)scriptContext);
                        } else {
                            object29 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue50 = object29;
                        builder.val("contraption", scriptValue50);
                        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue50, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                            ScriptValue scriptValue51 = scriptContext.getClassOrVar("Player");
                            if (scriptValue51 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object33;
                                String string = "<red>Could not assemble that structure.";
                                if (scriptValue51 instanceof ScriptValue.Obj && (object33 = (obj = (ScriptValue.Obj)scriptValue51).instance()) != null && !(object33 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                    PolyClassPlayer polyClassPlayer8 = new PolyClassPlayer(object33);
                                    v27 = ScriptValue.of((boolean)polyClassPlayer8.tm$42_send_message(string));
                                } else {
                                    v27 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue51, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                                }
                            } else {
                                v27 = ScriptValue.NULL;
                            }
                        } else {
                            ScriptValue scriptValue52;
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            StringBuilder stringBuilder = new StringBuilder().append("[windmill] assembled uuid=").append(((scriptValue52 = scriptContext.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue52, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()).append(" block_count=");
                            ScriptValue scriptValue53 = scriptContext.getClassOrVar("contraption");
                            arrayList.add(ScriptValue.of((String)stringBuilder.append((scriptValue53 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_count", (ScriptValue)scriptValue53, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()).toString()));
                            ScriptFormula.callBuiltin((String)"print", arrayList, (ScriptContext)scriptContext);
                            ScriptValue scriptValue54 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue54 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object34;
                                ScriptValue scriptValue55;
                                String string = "contraption_uuid";
                                String string8 = "string";
                                ScriptValue scriptValue56 = scriptContext.getClassOrVar("contraption");
                                Object object35 = scriptValue55 = scriptValue56 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue56, (ScriptContext)scriptContext) : ScriptValue.NULL;
                                if (scriptValue54 instanceof ScriptValue.Obj && (object34 = (obj = (ScriptValue.Obj)scriptValue54).instance()) != null && !(object34 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object34);
                                    v30 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string8, scriptValue55));
                                } else {
                                    v30 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue54, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue55, (ScriptContext)scriptContext);
                                }
                            } else {
                                v30 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue57 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue57 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object36;
                                String string = "assembled";
                                String string9 = "int";
                                ScriptValue scriptValue58 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 1.0);
                                if (scriptValue57 instanceof ScriptValue.Obj && (object36 = (obj = (ScriptValue.Obj)scriptValue57).instance()) != null && !(object36 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object36);
                                    v31 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string9, scriptValue58));
                                } else {
                                    v31 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue57, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string9), (ScriptValue)scriptValue58, (ScriptContext)scriptContext);
                                }
                            } else {
                                v31 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue59 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue59 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object37;
                                String string = "assembled_tick";
                                String string10 = "int";
                                ArrayList arrayList2 = new ArrayList();
                                ScriptValue scriptValue60 = ScriptFormula.callBuiltin((String)"tick", arrayList2, (ScriptContext)scriptContext);
                                if (scriptValue59 instanceof ScriptValue.Obj && (object37 = (obj = (ScriptValue.Obj)scriptValue59).instance()) != null && !(object37 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object37);
                                    v32 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string10, scriptValue60));
                                } else {
                                    v32 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue59, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string10), (ScriptValue)scriptValue60, (ScriptContext)scriptContext);
                                }
                            } else {
                                v32 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue61 = scriptContext.getClassOrVar("Player");
                            if (scriptValue61 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object38;
                                ScriptValue scriptValue62 = ScriptValue.of((String)("<green>Windmill assembled <gray>(" + scriptValue45.asStr() + " sails)."));
                                if (scriptValue61 instanceof ScriptValue.Obj && (object38 = (obj = (ScriptValue.Obj)scriptValue61).instance()) != null && !(object38 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                    PolyClassPlayer polyClassPlayer9 = new PolyClassPlayer(object38);
                                    v33 = ScriptValue.of((boolean)polyClassPlayer9.tm$42_send_message(scriptValue62.asStr()));
                                } else {
                                    v33 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue61, (ScriptValue)scriptValue62, (ScriptContext)scriptContext);
                                }
                            } else {
                                v33 = ScriptValue.NULL;
                            }
                        }
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
