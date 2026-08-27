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
import java.util.ArrayList;

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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                arrayList.add(ScriptValue.of((double)d2));
                arrayList.add(ScriptValue.of((double)d3));
                object3 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                object2 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
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
            ScriptValue.Obj obj2;
            Object object7;
            ScriptValue.Obj obj3;
            Object object8;
            ScriptValue.Obj obj4;
            Object object9;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? (scriptValue7 instanceof ScriptValue.Obj && (object9 = (obj4 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object9 instanceof PolyClass) && obj4.typeName().equals("Machine") ? new PolyClassMachine_v3(object9).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? (scriptValue9 instanceof ScriptValue.Obj && (object8 = (obj3 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object8 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v3(object8).pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            Object object10 = scriptValue11 != ScriptValue.NULL ? (scriptValue11 instanceof ScriptValue.Obj && (object7 = (obj2 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object7 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v3(object7).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : (scriptValue6 = ScriptValue.NULL);
            if (scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object6);
                object = polyClassMachine_v3.tm$68_block_at(scriptValue8.asNum(), scriptValue10.asNum(), scriptValue6.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue8);
                arrayList.add(scriptValue10);
                arrayList.add(scriptValue6);
                object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object3;
            String string = "assembled";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                object2 = polyClassMachine_v3.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("assembled", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        Object object4 = scriptValue3 != ScriptValue.NULL ? (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player") ? new PolyClassPlayer(object).pg$38_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (object4.asBool()) {
            if (scriptValue2.asNum() > 0.0) {
                ScriptValue scriptValue4;
                Object object5;
                Object object6;
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
                if (scriptValue5 != ScriptValue.NULL) {
                    ScriptValue.Obj obj3;
                    Object object7;
                    String string = "contraption_uuid";
                    String string3 = "string";
                    if (scriptValue5 instanceof ScriptValue.Obj && (object7 = (obj3 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object7 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object7);
                        object6 = polyClassMachine_v3.tm$34_get_typed(string, string3);
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string3));
                        object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue6 = object6;
                builder.val("cid", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("ContraptionManager");
                if (scriptValue7 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object8;
                    ScriptValue scriptValue8 = scriptValue6;
                    if (scriptValue7 instanceof ScriptValue.Obj && (object8 = (obj4 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object8 instanceof PolyClass) && obj4.typeName().equals("ContraptionManager")) {
                        PolyClassContraptionManager polyClassContraptionManager = new PolyClassContraptionManager(object8);
                        object5 = polyClassContraptionManager.tm$6_get(scriptValue8.asStr());
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue8);
                        object5 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue9 = object5;
                builder.val("contraption", scriptValue9);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue9, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("contraption");
                    if (scriptValue10 != ScriptValue.NULL) {
                        ArrayList arrayList = new ArrayList();
                        v4 = PolyDispatch.bootstrapCall("memberCall", "disassemble", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
                    } else {
                        v4 = ScriptValue.NULL;
                    }
                }
                if ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    ScriptValue.Obj obj5;
                    Object object9;
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue11 = ScriptValue.of((double)0.0);
                    if (scriptValue4 instanceof ScriptValue.Obj && (object9 = (obj5 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object9 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object9);
                        v5 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue11));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string4));
                        arrayList.add(scriptValue11);
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                if (scriptValue12 != ScriptValue.NULL) {
                    ScriptValue.Obj obj6;
                    Object object10;
                    String string = "contraption_uuid";
                    String string5 = "string";
                    ScriptValue scriptValue13 = ScriptValue.of((String)"");
                    if (scriptValue12 instanceof ScriptValue.Obj && (object10 = (obj6 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object10 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object10);
                        v6 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string5, scriptValue13));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string5));
                        arrayList.add(scriptValue13);
                        v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                if (scriptValue14 != ScriptValue.NULL) {
                    ScriptValue.Obj obj7;
                    Object object11;
                    double d = 0.0;
                    if (scriptValue14 instanceof ScriptValue.Obj && (object11 = (obj7 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object11 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object11);
                        v7 = ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((double)d));
                        v7 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v7 = ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
                if (scriptValue15 != ScriptValue.NULL) {
                    ScriptValue.Obj obj8;
                    Object object12;
                    String string = "<gray>Rotational bearing disassembled.";
                    if (scriptValue15 instanceof ScriptValue.Obj && (object12 = (obj8 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object12 instanceof PolyClass) && obj8.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object12);
                        v8 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v8 = ScriptValue.NULL;
                }
            }
        } else if (scriptValue2.asNum() > 0.0) {
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue.Obj obj9;
                Object object13;
                String string = "<gray>Already assembled - feed RPM from below to turn it.";
                if (scriptValue16 instanceof ScriptValue.Obj && (object13 = (obj9 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object13 instanceof PolyClass) && obj9.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object13);
                    v9 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
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
                    ScriptValue.Obj obj10;
                    Object object14;
                    String string = "<red>Nothing attached to the bearing's face to assemble.";
                    if (scriptValue19 instanceof ScriptValue.Obj && (object14 = (obj10 = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object14 instanceof PolyClass) && obj10.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object14);
                        v10 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        v10 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v10 = ScriptValue.NULL;
                }
            } else {
                Object object15;
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("Glue");
                if (scriptValue20 != ScriptValue.NULL) {
                    ScriptValue.Obj obj11;
                    Object object16;
                    ScriptValue scriptValue21 = scriptValue18;
                    if (scriptValue20 instanceof ScriptValue.Obj && (object16 = (obj11 = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object16 instanceof PolyClass) && obj11.typeName().equals("Glue")) {
                        PolyClassGlue polyClassGlue = new PolyClassGlue(object16);
                        object15 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue21));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue21);
                        object15 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue20, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object15 = ScriptValue.NULL;
                }
                if (object15.asBool() ^ true) {
                    ScriptValue scriptValue22 = scriptContext.getClassOrVar("Player");
                    if (scriptValue22 != ScriptValue.NULL) {
                        ScriptValue.Obj obj12;
                        Object object17;
                        String string = "<red>That structure is not glued together - glue it to itself and to the bearing.";
                        if (scriptValue22 instanceof ScriptValue.Obj && (object17 = (obj12 = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object17 instanceof PolyClass) && obj12.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object17);
                            v12 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                        } else {
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(ScriptValue.of((String)string));
                            v12 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue22, arrayList, (ScriptContext)scriptContext);
                        }
                    } else {
                        v12 = ScriptValue.NULL;
                    }
                } else {
                    Object object18;
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("ContraptionManager");
                    if (scriptValue23 != ScriptValue.NULL) {
                        ScriptValue.Obj obj13;
                        Object object19;
                        Object object20;
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue18);
                        ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue24 != ScriptValue.NULL) {
                            ScriptValue.Obj obj14;
                            Object object21;
                            double d = 0.0;
                            double d2 = 0.0;
                            double d3 = 0.0;
                            if (scriptValue24 instanceof ScriptValue.Obj && (object21 = (obj14 = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object21 instanceof PolyClass) && obj14.typeName().equals("Machine")) {
                                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object21);
                                object20 = polyClassMachine_v3.tm$68_block_at(d, d2, d3);
                            } else {
                                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                                arrayList2.add(ScriptValue.of((double)d));
                                arrayList2.add(ScriptValue.of((double)d2));
                                arrayList2.add(ScriptValue.of((double)d3));
                                object20 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue24, arrayList2, (ScriptContext)scriptContext);
                            }
                        } else {
                            object20 = ScriptValue.NULL;
                        }
                        arrayList.add((ScriptValue)object20);
                        object18 = scriptValue23 instanceof ScriptValue.Obj && (object19 = (obj13 = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object19 instanceof PolyClass) && obj13.typeName().equals("ContraptionManager") ? new PolyClassContraptionManager(object19).um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue23, arrayList, (ScriptContext)scriptContext);
                    } else {
                        object18 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue25 = object18;
                    builder.val("contraption", scriptValue25);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue25, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                        ScriptValue scriptValue26 = scriptContext.getClassOrVar("Player");
                        if (scriptValue26 != ScriptValue.NULL) {
                            ScriptValue.Obj obj15;
                            Object object22;
                            String string = "<red>Could not assemble that structure.";
                            if (scriptValue26 instanceof ScriptValue.Obj && (object22 = (obj15 = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object22 instanceof PolyClass) && obj15.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object22);
                                v15 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                            } else {
                                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                arrayList.add(ScriptValue.of((String)string));
                                v15 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue26, arrayList, (ScriptContext)scriptContext);
                            }
                        } else {
                            v15 = ScriptValue.NULL;
                        }
                    } else {
                        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue27 != ScriptValue.NULL) {
                            ScriptValue.Obj obj16;
                            Object object23;
                            ScriptValue scriptValue28;
                            String string = "contraption_uuid";
                            String string6 = "string";
                            ScriptValue scriptValue29 = scriptContext.getClassOrVar("contraption");
                            Object object24 = scriptValue28 = scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue29, (ScriptContext)scriptContext) : ScriptValue.NULL;
                            if (scriptValue27 instanceof ScriptValue.Obj && (object23 = (obj16 = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object23 instanceof PolyClass) && obj16.typeName().equals("Machine")) {
                                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object23);
                                v17 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string6, scriptValue28));
                            } else {
                                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                arrayList.add(ScriptValue.of((String)string));
                                arrayList.add(ScriptValue.of((String)string6));
                                arrayList.add(scriptValue28);
                                v17 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue27, arrayList, (ScriptContext)scriptContext);
                            }
                        } else {
                            v17 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue30 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue30 != ScriptValue.NULL) {
                            ScriptValue.Obj obj17;
                            Object object25;
                            String string = "assembled";
                            String string7 = "int";
                            ScriptValue scriptValue31 = ScriptValue.of((double)1.0);
                            if (scriptValue30 instanceof ScriptValue.Obj && (object25 = (obj17 = (ScriptValue.Obj)scriptValue30).instance()) != null && !(object25 instanceof PolyClass) && obj17.typeName().equals("Machine")) {
                                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object25);
                                v18 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string7, scriptValue31));
                            } else {
                                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                arrayList.add(ScriptValue.of((String)string));
                                arrayList.add(ScriptValue.of((String)string7));
                                arrayList.add(scriptValue31);
                                v18 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue30, arrayList, (ScriptContext)scriptContext);
                            }
                        } else {
                            v18 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue32 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue32 != ScriptValue.NULL) {
                            ScriptValue.Obj obj18;
                            Object object26;
                            String string = "assembled_tick";
                            String string8 = "int";
                            ArrayList arrayList = new ArrayList();
                            ScriptValue scriptValue33 = ScriptFormula.callBuiltin((String)"tick", arrayList, (ScriptContext)scriptContext);
                            if (scriptValue32 instanceof ScriptValue.Obj && (object26 = (obj18 = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object26 instanceof PolyClass) && obj18.typeName().equals("Machine")) {
                                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object26);
                                v19 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string8, scriptValue33));
                            } else {
                                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                                arrayList3.add(ScriptValue.of((String)string));
                                arrayList3.add(ScriptValue.of((String)string8));
                                arrayList3.add(scriptValue33);
                                v19 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue32, arrayList3, (ScriptContext)scriptContext);
                            }
                        } else {
                            v19 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue34 = scriptContext.getClassOrVar("Player");
                        if (scriptValue34 != ScriptValue.NULL) {
                            ScriptValue.Obj obj19;
                            Object object27;
                            ScriptValue scriptValue35;
                            ScriptValue scriptValue36 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<green>Rotational bearing assembled <gray>("), (ScriptValue)((scriptValue35 = scriptContext.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_count", (ScriptValue)scriptValue35, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" blocks, weight "));
                            ScriptValue scriptValue37 = scriptContext.getClassOrVar("contraption");
                            ScriptValue scriptValue38 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue36, (ScriptValue)ScriptValue.of((double)Math.floor((scriptValue37 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "weight", (ScriptValue)scriptValue37, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum()))), (ScriptValue)ScriptValue.of((String)")."));
                            if (scriptValue34 instanceof ScriptValue.Obj && (object27 = (obj19 = (ScriptValue.Obj)scriptValue34).instance()) != null && !(object27 instanceof PolyClass) && obj19.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object27);
                                v21 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue38.asStr()));
                            } else {
                                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                arrayList.add(scriptValue38);
                                v21 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue34, arrayList, (ScriptContext)scriptContext);
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
