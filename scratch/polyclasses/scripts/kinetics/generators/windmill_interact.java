/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassGlue
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

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
            PolyClassMachine polyClassMachine;
            PolyClassMachine polyClassMachine2;
            PolyClassMachine polyClassMachine3;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? ((polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassMachine3.pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassMachine2.pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            Object object7 = scriptValue11 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassMachine.pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : (scriptValue6 = ScriptValue.NULL);
            if (scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine4 = new PolyClassMachine(object6);
                object = polyClassMachine4.tm$68_block_at(scriptValue8.asNum(), scriptValue10.asNum(), scriptValue6.asNum());
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("assembled", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        Object object3 = scriptValue4 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassPlayer.pg$38_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (object3.asBool()) {
            if (scriptValue3.asNum() > 0.0) {
                ScriptValue scriptValue5;
                Object object4;
                Object object5;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                if (scriptValue6 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object6;
                    String string = "contraption_uuid";
                    String string3 = "string";
                    if (scriptValue6 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object6);
                        object5 = polyClassMachine.tm$34_get_typed(string, string3);
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string3));
                        object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue7 = object5;
                builder.val("cid", scriptValue7);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("ContraptionManager");
                if (scriptValue8 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object7;
                    ScriptValue scriptValue9 = scriptValue7;
                    if (scriptValue8 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("ContraptionManager")) {
                        PolyClassContraptionManager polyClassContraptionManager = new PolyClassContraptionManager(object7);
                        object4 = polyClassContraptionManager.tm$6_get(scriptValue9.asStr());
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue9);
                        object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = object4;
                builder.val("contraption", scriptValue10);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue10, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("contraption");
                    if (scriptValue11 != ScriptValue.NULL) {
                        ArrayList arrayList = new ArrayList();
                        v4 = PolyDispatch.bootstrapCall("memberCall", "disassemble", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
                    } else {
                        v4 = ScriptValue.NULL;
                    }
                }
                if ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object8;
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue12 = ScriptValue.of((double)0.0);
                    if (scriptValue5 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object8);
                        v5 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string4, scriptValue12));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string4));
                        arrayList.add(scriptValue12);
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
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
                    ScriptValue scriptValue14 = ScriptValue.of((String)"");
                    if (scriptValue13 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object9);
                        v6 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string5, scriptValue14));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string5));
                        arrayList.add(scriptValue14);
                        v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
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
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object10);
                        v7 = ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d2));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((double)d2));
                        v7 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
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
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object11);
                        v8 = ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d3));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((double)d3));
                        v8 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
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
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue17, arrayList, (ScriptContext)scriptContext);
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object14);
                    object13 = polyClassMachine.tm$34_get_typed(string, string6);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string6));
                    object13 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue18, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object13 = ScriptValue.NULL;
            }
            ScriptValue scriptValue19 = object13;
            builder.val("dir", scriptValue19);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((double)0.0))) {
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object15);
                    v11 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string7, scriptValue23));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string7));
                    arrayList.add(scriptValue23);
                    v11 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue22, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v11 = ScriptValue.NULL;
            }
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("Player");
            if (scriptValue24 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                ScriptValue scriptValue25;
                ScriptValue scriptValue26 = scriptValue25 = d5 > 0.0 ? ScriptValue.of((String)"<gray>Windmill now turns <white>clockwise<gray>.") : ScriptValue.of((String)"<gray>Windmill now turns <white>counter-clockwise<gray>.");
                if (scriptValue24 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object16);
                    v13 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(scriptValue25.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue25);
                    v13 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue24, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine;
                PolyClassMachine polyClassMachine2;
                PolyClassMachine polyClassMachine3;
                PolyClassMachine polyClassMachine4;
                ScriptValue scriptValue30 = ScriptValue.of((String)"<dark_gray>[debug] facing=");
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"facing"));
                ScriptValue scriptValue31 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue32 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue33 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue34 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue35 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue30, (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue31 != ScriptValue.NULL ? ((polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue31)) != null ? polyClassMachine4.pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue31, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext)), (ScriptValue)ScriptValue.of((String)" seed=(")), (ScriptValue)(scriptValue32 != ScriptValue.NULL ? ((polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue32)) != null ? polyClassMachine3.pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue32, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(scriptValue33 != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue33)) != null ? polyClassMachine2.pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue33, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(scriptValue34 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue34)) != null ? polyClassMachine.pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue34, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)")")), (ScriptValue)ScriptValue.of((String)" self_glued="));
                ScriptValue scriptValue36 = scriptContext.getClassOrVar("Glue");
                if (scriptValue36 != ScriptValue.NULL) {
                    ScriptValue.Obj obj2;
                    Object object19;
                    ScriptValue scriptValue37;
                    ScriptValue scriptValue38 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue38 != ScriptValue.NULL) {
                        ScriptValue.Obj obj3;
                        Object object20;
                        double d6 = 0.0;
                        double d7 = 0.0;
                        double d8 = 0.0;
                        if (scriptValue38 instanceof ScriptValue.Obj && (object20 = (obj3 = (ScriptValue.Obj)scriptValue38).instance()) != null && !(object20 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                            PolyClassMachine polyClassMachine5 = new PolyClassMachine(object20);
                            v16 = polyClassMachine5.tm$68_block_at(d6, d7, d8);
                        } else {
                            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                            arrayList2.add(ScriptValue.of((double)d6));
                            arrayList2.add(ScriptValue.of((double)d7));
                            arrayList2.add(ScriptValue.of((double)d8));
                            v16 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue38, arrayList2, (ScriptContext)scriptContext);
                        }
                    } else {
                        v16 = scriptValue37 = ScriptValue.NULL;
                    }
                    if (scriptValue36 instanceof ScriptValue.Obj && (object19 = (obj2 = (ScriptValue.Obj)scriptValue36).instance()) != null && !(object19 instanceof PolyClass) && obj2.typeName().equals("Glue")) {
                        PolyClassGlue polyClassGlue = new PolyClassGlue(object19);
                        object18 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue37));
                    } else {
                        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                        arrayList3.add(scriptValue37);
                        object18 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue36, arrayList3, (ScriptContext)scriptContext);
                    }
                } else {
                    object18 = ScriptValue.NULL;
                }
                ScriptValue scriptValue39 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue35, (ScriptValue)object18);
                if (scriptValue29 instanceof ScriptValue.Obj && (object17 = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object17 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer4 = new PolyClassPlayer(object17);
                    v18 = ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(scriptValue39.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(scriptValue39);
                    v18 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue29, arrayList4, (ScriptContext)scriptContext);
                }
            } else {
                v18 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue28, (ScriptValue)scriptContext.getClassOrVar("null")) || ((scriptValue27 = scriptContext.getClassOrVar("seed")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                ScriptValue scriptValue40 = scriptContext.getClassOrVar("Player");
                if (scriptValue40 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object21;
                    String string = "<red>Nothing attached to the bearing's face to assemble.";
                    if (scriptValue40 instanceof ScriptValue.Obj && (object21 = (obj = (ScriptValue.Obj)scriptValue40).instance()) != null && !(object21 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer5 = new PolyClassPlayer(object21);
                        v19 = ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(string));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        v19 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue40, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v19 = ScriptValue.NULL;
                }
            } else {
                Object object22;
                ScriptValue scriptValue41 = scriptContext.getClassOrVar("Glue");
                if (scriptValue41 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object23;
                    ScriptValue scriptValue42 = scriptValue28;
                    if (scriptValue41 instanceof ScriptValue.Obj && (object23 = (obj = (ScriptValue.Obj)scriptValue41).instance()) != null && !(object23 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                        PolyClassGlue polyClassGlue = new PolyClassGlue(object23);
                        object22 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue42));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue42);
                        object22 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue41, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object22 = ScriptValue.NULL;
                }
                if (object22.asBool() ^ true) {
                    ScriptValue scriptValue43 = scriptContext.getClassOrVar("Player");
                    if (scriptValue43 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object24;
                        String string = "<red>That structure is not glued together - glue the sails to each other and to the bearing.";
                        if (scriptValue43 instanceof ScriptValue.Obj && (object24 = (obj = (ScriptValue.Obj)scriptValue43).instance()) != null && !(object24 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object24);
                            v21 = ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string));
                        } else {
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(ScriptValue.of((String)string));
                            v21 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue43, arrayList, (ScriptContext)scriptContext);
                        }
                    } else {
                        v21 = ScriptValue.NULL;
                    }
                } else {
                    Object object25;
                    ScriptValue scriptValue44 = scriptContext.getClassOrVar("Glue");
                    if (scriptValue44 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object26;
                        ScriptValue scriptValue45 = scriptValue28;
                        String string = "sail";
                        if (scriptValue44 instanceof ScriptValue.Obj && (object26 = (obj = (ScriptValue.Obj)scriptValue44).instance()) != null && !(object26 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                            PolyClassGlue polyClassGlue = new PolyClassGlue(object26);
                            object25 = ScriptValue.of((double)polyClassGlue.tm$4_count(scriptValue45, string));
                        } else {
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(scriptValue45);
                            arrayList.add(ScriptValue.of((String)string));
                            object25 = PolyDispatch.bootstrapCall("memberCall", "count", (ScriptValue)scriptValue44, arrayList, (ScriptContext)scriptContext);
                        }
                    } else {
                        object25 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue46 = object25;
                    builder.val("sails", scriptValue46);
                    if (scriptValue46.asNum() < d) {
                        ScriptValue scriptValue47 = scriptContext.getClassOrVar("Player");
                        if (scriptValue47 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object27;
                            ScriptValue scriptValue48 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<red>Not enough sails: <white>"), (ScriptValue)scriptValue46), (ScriptValue)ScriptValue.of((String)"<red> of <white>")), (ScriptValue)ScriptValue.of((double)d)), (ScriptValue)ScriptValue.of((String)"<red> needed."));
                            if (scriptValue47 instanceof ScriptValue.Obj && (object27 = (obj = (ScriptValue.Obj)scriptValue47).instance()) != null && !(object27 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer7 = new PolyClassPlayer(object27);
                                v23 = ScriptValue.of((boolean)polyClassPlayer7.tm$42_send_message(scriptValue48.asStr()));
                            } else {
                                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                arrayList.add(scriptValue48);
                                v23 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue47, arrayList, (ScriptContext)scriptContext);
                            }
                        } else {
                            v23 = ScriptValue.NULL;
                        }
                    } else {
                        Object object28;
                        ScriptValue scriptValue49 = scriptContext.getClassOrVar("ContraptionManager");
                        if (scriptValue49 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object29;
                            Object object30;
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(scriptValue28);
                            ScriptValue scriptValue50 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue50 != ScriptValue.NULL) {
                                ScriptValue.Obj obj4;
                                Object object31;
                                double d9 = 0.0;
                                double d10 = 0.0;
                                double d11 = 0.0;
                                if (scriptValue50 instanceof ScriptValue.Obj && (object31 = (obj4 = (ScriptValue.Obj)scriptValue50).instance()) != null && !(object31 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                                    PolyClassMachine polyClassMachine = new PolyClassMachine(object31);
                                    object30 = polyClassMachine.tm$68_block_at(d9, d10, d11);
                                } else {
                                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                                    arrayList5.add(ScriptValue.of((double)d9));
                                    arrayList5.add(ScriptValue.of((double)d10));
                                    arrayList5.add(ScriptValue.of((double)d11));
                                    object30 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue50, arrayList5, (ScriptContext)scriptContext);
                                }
                            } else {
                                object30 = ScriptValue.NULL;
                            }
                            arrayList.add((ScriptValue)object30);
                            object28 = scriptValue49 instanceof ScriptValue.Obj && (object29 = (obj = (ScriptValue.Obj)scriptValue49).instance()) != null && !(object29 instanceof PolyClass) && obj.typeName().equals("ContraptionManager") ? new PolyClassContraptionManager(object29).um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue49, arrayList, (ScriptContext)scriptContext);
                        } else {
                            object28 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue51 = object28;
                        builder.val("contraption", scriptValue51);
                        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue51, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                            ScriptValue scriptValue52 = scriptContext.getClassOrVar("Player");
                            if (scriptValue52 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object32;
                                String string = "<red>Could not assemble that structure.";
                                if (scriptValue52 instanceof ScriptValue.Obj && (object32 = (obj = (ScriptValue.Obj)scriptValue52).instance()) != null && !(object32 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                    PolyClassPlayer polyClassPlayer8 = new PolyClassPlayer(object32);
                                    v26 = ScriptValue.of((boolean)polyClassPlayer8.tm$42_send_message(string));
                                } else {
                                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                    arrayList.add(ScriptValue.of((String)string));
                                    v26 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue52, arrayList, (ScriptContext)scriptContext);
                                }
                            } else {
                                v26 = ScriptValue.NULL;
                            }
                        } else {
                            ScriptValue scriptValue53;
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            ScriptValue scriptValue54 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"[windmill] assembled uuid="), (ScriptValue)((scriptValue53 = scriptContext.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue53, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" block_count="));
                            ScriptValue scriptValue55 = scriptContext.getClassOrVar("contraption");
                            arrayList.add(ScriptFormula.addPolymorphic((ScriptValue)scriptValue54, (ScriptValue)(scriptValue55 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_count", (ScriptValue)scriptValue55, (ScriptContext)scriptContext) : ScriptValue.NULL)));
                            ScriptFormula.callBuiltin((String)"print", arrayList, (ScriptContext)scriptContext);
                            ScriptValue scriptValue56 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue56 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object33;
                                ScriptValue scriptValue57;
                                String string = "contraption_uuid";
                                String string8 = "string";
                                ScriptValue scriptValue58 = scriptContext.getClassOrVar("contraption");
                                Object object34 = scriptValue57 = scriptValue58 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue58, (ScriptContext)scriptContext) : ScriptValue.NULL;
                                if (scriptValue56 instanceof ScriptValue.Obj && (object33 = (obj = (ScriptValue.Obj)scriptValue56).instance()) != null && !(object33 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                    PolyClassMachine polyClassMachine = new PolyClassMachine(object33);
                                    v29 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string8, scriptValue57));
                                } else {
                                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                                    arrayList6.add(ScriptValue.of((String)string));
                                    arrayList6.add(ScriptValue.of((String)string8));
                                    arrayList6.add(scriptValue57);
                                    v29 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue56, arrayList6, (ScriptContext)scriptContext);
                                }
                            } else {
                                v29 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue59 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue59 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object35;
                                String string = "assembled";
                                String string9 = "int";
                                ScriptValue scriptValue60 = ScriptValue.of((double)1.0);
                                if (scriptValue59 instanceof ScriptValue.Obj && (object35 = (obj = (ScriptValue.Obj)scriptValue59).instance()) != null && !(object35 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                    PolyClassMachine polyClassMachine = new PolyClassMachine(object35);
                                    v30 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string9, scriptValue60));
                                } else {
                                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                                    arrayList7.add(ScriptValue.of((String)string));
                                    arrayList7.add(ScriptValue.of((String)string9));
                                    arrayList7.add(scriptValue60);
                                    v30 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue59, arrayList7, (ScriptContext)scriptContext);
                                }
                            } else {
                                v30 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue61 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue61 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object36;
                                String string = "assembled_tick";
                                String string10 = "int";
                                ArrayList arrayList8 = new ArrayList();
                                ScriptValue scriptValue62 = ScriptFormula.callBuiltin((String)"tick", arrayList8, (ScriptContext)scriptContext);
                                if (scriptValue61 instanceof ScriptValue.Obj && (object36 = (obj = (ScriptValue.Obj)scriptValue61).instance()) != null && !(object36 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                    PolyClassMachine polyClassMachine = new PolyClassMachine(object36);
                                    v31 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string10, scriptValue62));
                                } else {
                                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                                    arrayList9.add(ScriptValue.of((String)string));
                                    arrayList9.add(ScriptValue.of((String)string10));
                                    arrayList9.add(scriptValue62);
                                    v31 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue61, arrayList9, (ScriptContext)scriptContext);
                                }
                            } else {
                                v31 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue63 = scriptContext.getClassOrVar("Player");
                            if (scriptValue63 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object37;
                                ScriptValue scriptValue64 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<green>Windmill assembled <gray>("), (ScriptValue)scriptValue46), (ScriptValue)ScriptValue.of((String)" sails)."));
                                if (scriptValue63 instanceof ScriptValue.Obj && (object37 = (obj = (ScriptValue.Obj)scriptValue63).instance()) != null && !(object37 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                    PolyClassPlayer polyClassPlayer9 = new PolyClassPlayer(object37);
                                    v32 = ScriptValue.of((boolean)polyClassPlayer9.tm$42_send_message(scriptValue64.asStr()));
                                } else {
                                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                                    arrayList10.add(scriptValue64);
                                    v32 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue63, arrayList10, (ScriptContext)scriptContext);
                                }
                            } else {
                                v32 = ScriptValue.NULL;
                            }
                        }
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
