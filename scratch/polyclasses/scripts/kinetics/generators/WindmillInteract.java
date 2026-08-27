/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v4
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
import dev.arubik.craftengine.script.PolyClassBlock_v4;
import dev.arubik.craftengine.script.PolyClassContraptionManager;
import dev.arubik.craftengine.script.PolyClassGlue;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
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
        double d = 8.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)8.0);
        builder.val("MIN_SAILS", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "assembled";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("assembled", scriptValue4);
        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (polyClassPlayer_v2 != null ? polyClassPlayer_v2.tg$49_is_sneaking() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            if (scriptValue4.asNum() > 0.0) {
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
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object5);
                        object4 = polyClassMachine.tm$34_get_typed(string, string3);
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
                    Object object7 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "disassemble", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
                }
                if ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object8;
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue11 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 0.0);
                    if (scriptValue5 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object8);
                        v4 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string4, scriptValue11));
                    } else {
                        v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                    }
                } else {
                    v4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                if (scriptValue12 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object9;
                    String string = "contraption_uuid";
                    String string5 = "string";
                    ScriptValue scriptValue13 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "");
                    if (scriptValue12 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object9);
                        v5 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string5, scriptValue13));
                    } else {
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                if (scriptValue14 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object10;
                    double d2 = 0.0;
                    if (scriptValue14 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object10);
                        v6 = ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d2));
                    } else {
                        v6 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
                    }
                } else {
                    v6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                if (scriptValue15 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object11;
                    double d3 = 0.0;
                    if (scriptValue15 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object11);
                        v7 = ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d3));
                    } else {
                        v7 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                    }
                } else {
                    v7 = ScriptValue.NULL;
                }
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
                if (scriptValue16 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object12;
                    String string = "<gray>Windmill disassembled.";
                    if (scriptValue16 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer_v2 polyClassPlayer_v22 = new PolyClassPlayer_v2(object12);
                        v8 = ScriptValue.of((boolean)polyClassPlayer_v22.tm$42_send_message(string));
                    } else {
                        v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v8 = ScriptValue.NULL;
                }
            }
        } else if (scriptValue4.asNum() > 0.0) {
            Object object13;
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
            if (scriptValue17 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                String string = "windmill_dir";
                String string6 = "int";
                if (scriptValue17 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object14);
                    object13 = polyClassMachine.tm$34_get_typed(string, string6);
                } else {
                    object13 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
                }
            } else {
                object13 = ScriptValue.NULL;
            }
            ScriptValue scriptValue18 = object13;
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
                ScriptValue.Obj obj;
                Object object15;
                String string = "windmill_dir";
                String string7 = "int";
                ScriptValue scriptValue22 = ScriptValue.of((double)d5);
                if (scriptValue21 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object15);
                    v10 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string7, scriptValue22));
                } else {
                    v10 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
                }
            } else {
                v10 = ScriptValue.NULL;
            }
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Player");
            if (scriptValue23 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                ScriptValue scriptValue24;
                ScriptValue scriptValue25 = scriptValue24 = d5 > 0.0 ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "<gray>Windmill now turns <white>clockwise<gray>.")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", WindmillInteract.class, "<gray>Windmill now turns <white>counter-clockwise<gray>."));
                if (scriptValue23 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer_v2 polyClassPlayer_v23 = new PolyClassPlayer_v2(object16);
                    v12 = ScriptValue.of((boolean)polyClassPlayer_v23.tm$42_send_message(scriptValue24.asStr()));
                } else {
                    v12 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue23, (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
                }
            } else {
                v12 = ScriptValue.NULL;
            }
        } else {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue26 = WindmillInteract._seedBlock(builder2);
            builder.val("seed", scriptValue26);
            ScriptValue scriptValue27 = scriptContext.getClassOrVar("Player");
            if (scriptValue27 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object17;
                Object object18;
                ScriptValue scriptValue28;
                ScriptValue scriptValue29;
                ScriptValue scriptValue30;
                CallSite callSite;
                ScriptValue.Obj obj2;
                Object object19;
                ScriptValue scriptValue31;
                StringBuilder stringBuilder = new StringBuilder().append("<dark_gray>[debug] facing=");
                PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue32 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue31 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue31, (ScriptContext)scriptContext) : ScriptValue.NULL);
                String string = "facing";
                if (scriptValue32 instanceof ScriptValue.Obj && (object19 = (obj2 = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object19 instanceof PolyClass) && obj2.typeName().equals("Block")) {
                    PolyClassBlock_v4 polyClassBlock_v4 = new PolyClassBlock_v4(object19);
                    callSite = polyClassBlock_v4.tm$24_property(string);
                } else {
                    callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue32, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
                PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                StringBuilder stringBuilder2 = stringBuilder.append(callSite.asStr()).append(" seed=(").append((polyClassMachine2 != null ? polyClassMachine2.pg$133_facing_dx() : ((scriptValue30 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue30, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(",").append((polyClassMachine3 != null ? polyClassMachine3.pg$129_facing_dy() : ((scriptValue29 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue29, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(",").append((polyClassMachine4 != null ? polyClassMachine4.pg$131_facing_dz() : ((scriptValue28 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue28, (ScriptContext)scriptContext) : ScriptValue.NULL)).asStr()).append(")").append(" self_glued=");
                ScriptValue scriptValue33 = scriptContext.getClassOrVar("Glue");
                if (scriptValue33 != ScriptValue.NULL) {
                    ScriptValue.Obj obj3;
                    Object object20;
                    ScriptValue scriptValue34;
                    ScriptValue scriptValue35 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue35 != ScriptValue.NULL) {
                        ScriptValue.Obj obj4;
                        Object object21;
                        double d6 = 0.0;
                        double d7 = 0.0;
                        double d8 = 0.0;
                        if (scriptValue35 instanceof ScriptValue.Obj && (object21 = (obj4 = (ScriptValue.Obj)scriptValue35).instance()) != null && !(object21 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                            PolyClassMachine polyClassMachine5 = new PolyClassMachine(object21);
                            v16 = polyClassMachine5.tm$68_block_at(d6, d7, d8);
                        } else {
                            v16 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue35, (ScriptValue)ScriptValue.of((double)d6), (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)ScriptValue.of((double)d8), (ScriptContext)scriptContext);
                        }
                    } else {
                        v16 = scriptValue34 = ScriptValue.NULL;
                    }
                    if (scriptValue33 instanceof ScriptValue.Obj && (object20 = (obj3 = (ScriptValue.Obj)scriptValue33).instance()) != null && !(object20 instanceof PolyClass) && obj3.typeName().equals("Glue")) {
                        PolyClassGlue polyClassGlue = new PolyClassGlue(object20);
                        object18 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue34));
                    } else {
                        object18 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue33, (ScriptValue)scriptValue34, (ScriptContext)scriptContext);
                    }
                } else {
                    object18 = ScriptValue.NULL;
                }
                ScriptValue scriptValue36 = ScriptValue.of((String)stringBuilder2.append(object18.asStr()).toString());
                if (scriptValue27 instanceof ScriptValue.Obj && (object17 = (obj = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object17 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer_v2 polyClassPlayer_v24 = new PolyClassPlayer_v2(object17);
                    v18 = ScriptValue.of((boolean)polyClassPlayer_v24.tm$42_send_message(scriptValue36.asStr()));
                } else {
                    v18 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue27, (ScriptValue)scriptValue36, (ScriptContext)scriptContext);
                }
            } else {
                v18 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue26, (ScriptValue)scriptContext.getClassOrVar("null")) || (scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue26, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                ScriptValue scriptValue37 = scriptContext.getClassOrVar("Player");
                if (scriptValue37 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object22;
                    String string = "<red>Nothing attached to the bearing's face to assemble.";
                    if (scriptValue37 instanceof ScriptValue.Obj && (object22 = (obj = (ScriptValue.Obj)scriptValue37).instance()) != null && !(object22 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer_v2 polyClassPlayer_v25 = new PolyClassPlayer_v2(object22);
                        v19 = ScriptValue.of((boolean)polyClassPlayer_v25.tm$42_send_message(string));
                    } else {
                        v19 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue37, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    }
                } else {
                    v19 = ScriptValue.NULL;
                }
            } else {
                Object object23;
                ScriptValue scriptValue38 = scriptContext.getClassOrVar("Glue");
                if (scriptValue38 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object24;
                    ScriptValue scriptValue39 = scriptValue26;
                    if (scriptValue38 instanceof ScriptValue.Obj && (object24 = (obj = (ScriptValue.Obj)scriptValue38).instance()) != null && !(object24 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                        PolyClassGlue polyClassGlue = new PolyClassGlue(object24);
                        object23 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue39));
                    } else {
                        object23 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue38, (ScriptValue)scriptValue39, (ScriptContext)scriptContext);
                    }
                } else {
                    object23 = ScriptValue.NULL;
                }
                if (object23.asBool() ^ true) {
                    ScriptValue scriptValue40 = scriptContext.getClassOrVar("Player");
                    if (scriptValue40 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object25;
                        String string = "<red>That structure is not glued together - glue the sails to each other and to the bearing.";
                        if (scriptValue40 instanceof ScriptValue.Obj && (object25 = (obj = (ScriptValue.Obj)scriptValue40).instance()) != null && !(object25 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer_v2 polyClassPlayer_v26 = new PolyClassPlayer_v2(object25);
                            v21 = ScriptValue.of((boolean)polyClassPlayer_v26.tm$42_send_message(string));
                        } else {
                            v21 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue40, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        }
                    } else {
                        v21 = ScriptValue.NULL;
                    }
                } else {
                    Object object26;
                    ScriptValue scriptValue41 = scriptContext.getClassOrVar("Glue");
                    if (scriptValue41 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object27;
                        ScriptValue scriptValue42 = scriptValue26;
                        String string = "sail";
                        if (scriptValue41 instanceof ScriptValue.Obj && (object27 = (obj = (ScriptValue.Obj)scriptValue41).instance()) != null && !(object27 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                            PolyClassGlue polyClassGlue = new PolyClassGlue(object27);
                            object26 = ScriptValue.of((double)polyClassGlue.tm$4_count(scriptValue42, string));
                        } else {
                            object26 = PolyDispatch.bootstrapCall("memberCall", "count", (ScriptValue)scriptValue41, (ScriptValue)scriptValue42, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                        }
                    } else {
                        object26 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue43 = object26;
                    builder.val("sails", scriptValue43);
                    if (scriptValue43.asNum() < d) {
                        ScriptValue scriptValue44 = scriptContext.getClassOrVar("Player");
                        if (scriptValue44 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object28;
                            ScriptValue scriptValue45 = ScriptValue.of((String)("<red>Not enough sails: <white>" + scriptValue43.asStr() + "<red> of <white>" + ScriptFormula.numToStr((double)d) + "<red> needed."));
                            if (scriptValue44 instanceof ScriptValue.Obj && (object28 = (obj = (ScriptValue.Obj)scriptValue44).instance()) != null && !(object28 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer_v2 polyClassPlayer_v27 = new PolyClassPlayer_v2(object28);
                                v23 = ScriptValue.of((boolean)polyClassPlayer_v27.tm$42_send_message(scriptValue45.asStr()));
                            } else {
                                v23 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue44, (ScriptValue)scriptValue45, (ScriptContext)scriptContext);
                            }
                        } else {
                            v23 = ScriptValue.NULL;
                        }
                    } else {
                        Object object29;
                        ScriptValue scriptValue46 = scriptContext.getClassOrVar("ContraptionManager");
                        if (scriptValue46 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object30;
                            Object object31;
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(scriptValue26);
                            ScriptValue scriptValue47 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue47 != ScriptValue.NULL) {
                                ScriptValue.Obj obj5;
                                Object object32;
                                double d9 = 0.0;
                                double d10 = 0.0;
                                double d11 = 0.0;
                                if (scriptValue47 instanceof ScriptValue.Obj && (object32 = (obj5 = (ScriptValue.Obj)scriptValue47).instance()) != null && !(object32 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                                    PolyClassMachine polyClassMachine = new PolyClassMachine(object32);
                                    object31 = polyClassMachine.tm$68_block_at(d9, d10, d11);
                                } else {
                                    object31 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue47, (ScriptValue)ScriptValue.of((double)d9), (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)ScriptValue.of((double)d11), (ScriptContext)scriptContext);
                                }
                            } else {
                                object31 = ScriptValue.NULL;
                            }
                            arrayList.add((ScriptValue)object31);
                            object29 = scriptValue46 instanceof ScriptValue.Obj && (object30 = (obj = (ScriptValue.Obj)scriptValue46).instance()) != null && !(object30 instanceof PolyClass) && obj.typeName().equals("ContraptionManager") ? new PolyClassContraptionManager(object30).um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue46, arrayList, (ScriptContext)scriptContext);
                        } else {
                            object29 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue48 = object29;
                        builder.val("contraption", scriptValue48);
                        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue48, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                            ScriptValue scriptValue49 = scriptContext.getClassOrVar("Player");
                            if (scriptValue49 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object33;
                                String string = "<red>Could not assemble that structure.";
                                if (scriptValue49 instanceof ScriptValue.Obj && (object33 = (obj = (ScriptValue.Obj)scriptValue49).instance()) != null && !(object33 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                    PolyClassPlayer_v2 polyClassPlayer_v28 = new PolyClassPlayer_v2(object33);
                                    v26 = ScriptValue.of((boolean)polyClassPlayer_v28.tm$42_send_message(string));
                                } else {
                                    v26 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue49, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                                }
                            } else {
                                v26 = ScriptValue.NULL;
                            }
                        } else {
                            ScriptFormula.callBuiltin1((String)"print", (ScriptValue)ScriptValue.of((String)("[windmill] assembled uuid=" + (scriptValue48 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue48, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + " block_count=" + (scriptValue48 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_count", (ScriptValue)scriptValue48, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())), (ScriptContext)scriptContext);
                            ScriptValue scriptValue50 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue50 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object34;
                                ScriptValue scriptValue51;
                                String string = "contraption_uuid";
                                String string8 = "string";
                                Object object35 = scriptValue51 = scriptValue48 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue48, (ScriptContext)scriptContext) : ScriptValue.NULL;
                                if (scriptValue50 instanceof ScriptValue.Obj && (object34 = (obj = (ScriptValue.Obj)scriptValue50).instance()) != null && !(object34 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                    PolyClassMachine polyClassMachine = new PolyClassMachine(object34);
                                    v28 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string8, scriptValue51));
                                } else {
                                    v28 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue50, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue51, (ScriptContext)scriptContext);
                                }
                            } else {
                                v28 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue52 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue52 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object36;
                                String string = "assembled";
                                String string9 = "int";
                                ScriptValue scriptValue53 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WindmillInteract.class, 1.0);
                                if (scriptValue52 instanceof ScriptValue.Obj && (object36 = (obj = (ScriptValue.Obj)scriptValue52).instance()) != null && !(object36 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                    PolyClassMachine polyClassMachine = new PolyClassMachine(object36);
                                    v29 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string9, scriptValue53));
                                } else {
                                    v29 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue52, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string9), (ScriptValue)scriptValue53, (ScriptContext)scriptContext);
                                }
                            } else {
                                v29 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue54 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue54 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object37;
                                String string = "assembled_tick";
                                String string10 = "int";
                                ScriptValue scriptValue55 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)scriptContext);
                                if (scriptValue54 instanceof ScriptValue.Obj && (object37 = (obj = (ScriptValue.Obj)scriptValue54).instance()) != null && !(object37 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                    PolyClassMachine polyClassMachine = new PolyClassMachine(object37);
                                    v30 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string10, scriptValue55));
                                } else {
                                    v30 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue54, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string10), (ScriptValue)scriptValue55, (ScriptContext)scriptContext);
                                }
                            } else {
                                v30 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue56 = scriptContext.getClassOrVar("Player");
                            if (scriptValue56 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object38;
                                ScriptValue scriptValue57 = ScriptValue.of((String)("<green>Windmill assembled <gray>(" + scriptValue43.asStr() + " sails)."));
                                if (scriptValue56 instanceof ScriptValue.Obj && (object38 = (obj = (ScriptValue.Obj)scriptValue56).instance()) != null && !(object38 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                    PolyClassPlayer_v2 polyClassPlayer_v29 = new PolyClassPlayer_v2(object38);
                                    v31 = ScriptValue.of((boolean)polyClassPlayer_v29.tm$42_send_message(scriptValue57.asStr()));
                                } else {
                                    v31 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue56, (ScriptValue)scriptValue57, (ScriptContext)scriptContext);
                                }
                            } else {
                                v31 = ScriptValue.NULL;
                            }
                        }
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
