/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassGlue
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class WindmillInteract {
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object4);
                object3 = polyClassMachine_v2.tm$68_block_at(d, d2, d3);
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
            ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? (scriptValue7 instanceof ScriptValue.Obj && (object9 = (obj4 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object9 instanceof PolyClass) && obj4.typeName().equals("Machine") ? new PolyClassMachine_v2(object9).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? (scriptValue9 instanceof ScriptValue.Obj && (object8 = (obj3 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object8 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v2(object8).pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            Object object10 = scriptValue11 != ScriptValue.NULL ? (scriptValue11 instanceof ScriptValue.Obj && (object7 = (obj2 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object7 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v2(object7).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : (scriptValue6 = ScriptValue.NULL);
            if (scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object6);
                object = polyClassMachine_v2.tm$68_block_at(scriptValue8.asNum(), scriptValue10.asNum(), scriptValue6.asNum());
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
        block117: {
            ScriptValue scriptValue;
            double d;
            ScriptContext scriptContext;
            block116: {
                ScriptValue scriptValue2;
                Object object;
                Object object2;
                ScriptValue.Obj obj;
                Object object3;
                Object object4;
                scriptContext = builder.peek();
                d = 8.0;
                ScriptValue scriptValue3 = ScriptValue.of((double)8.0);
                builder.val("MIN_SAILS", scriptValue3);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
                if (scriptValue4 != ScriptValue.NULL) {
                    ScriptValue.Obj obj2;
                    Object object5;
                    String string = "assembled";
                    String string2 = "int";
                    if (scriptValue4 instanceof ScriptValue.Obj && (object5 = (obj2 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object5 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object5);
                        object4 = polyClassMachine_v2.tm$34_get_typed(string, string2);
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string2));
                        object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object4 = ScriptValue.NULL;
                }
                scriptValue = object4;
                builder.val("assembled", scriptValue);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
                Object object6 = scriptValue5 != ScriptValue.NULL ? (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player") ? new PolyClassPlayer(object3).pg$38_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                if (!object6.asBool()) break block116;
                if (!(scriptValue.asNum() > 0.0)) break block117;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                if (scriptValue6 != ScriptValue.NULL) {
                    ScriptValue.Obj obj3;
                    Object object7;
                    String string = "contraption_uuid";
                    String string3 = "string";
                    if (scriptValue6 instanceof ScriptValue.Obj && (object7 = (obj3 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object7 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object7);
                        object2 = polyClassMachine_v2.tm$34_get_typed(string, string3);
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string3));
                        object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue7 = object2;
                builder.val("cid", scriptValue7);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("ContraptionManager");
                if (scriptValue8 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object8;
                    ScriptValue scriptValue9 = scriptValue7;
                    if (scriptValue8 instanceof ScriptValue.Obj && (object8 = (obj4 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object8 instanceof PolyClass) && obj4.typeName().equals("ContraptionManager")) {
                        PolyClassContraptionManager polyClassContraptionManager = new PolyClassContraptionManager(object8);
                        object = polyClassContraptionManager.tm$6_get(scriptValue9.asStr());
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue9);
                        object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = object;
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
                if ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    ScriptValue.Obj obj5;
                    Object object9;
                    String string = "assembled";
                    String string4 = "int";
                    ScriptValue scriptValue12 = ScriptValue.of((double)0.0);
                    if (scriptValue2 instanceof ScriptValue.Obj && (object9 = (obj5 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object9 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object9);
                        v5 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string4, scriptValue12));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string4));
                        arrayList.add(scriptValue12);
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                if (scriptValue13 != ScriptValue.NULL) {
                    ScriptValue.Obj obj6;
                    Object object10;
                    String string = "contraption_uuid";
                    String string5 = "string";
                    ScriptValue scriptValue14 = ScriptValue.of((String)"");
                    if (scriptValue13 instanceof ScriptValue.Obj && (object10 = (obj6 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object10 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object10);
                        v6 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string5, scriptValue14));
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
                    ScriptValue.Obj obj7;
                    Object object11;
                    double d2 = 0.0;
                    if (scriptValue15 instanceof ScriptValue.Obj && (object11 = (obj7 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object11 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object11);
                        v7 = ScriptValue.of((boolean)polyClassMachine_v2.tm$106_set_rpm_output(d2));
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
                    ScriptValue.Obj obj8;
                    Object object12;
                    double d3 = 0.0;
                    if (scriptValue16 instanceof ScriptValue.Obj && (object12 = (obj8 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object12 instanceof PolyClass) && obj8.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object12);
                        v8 = ScriptValue.of((boolean)polyClassMachine_v2.tm$56_report_su(d3));
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
                    ScriptValue.Obj obj9;
                    Object object13;
                    String string = "<gray>Windmill disassembled.";
                    if (scriptValue17 instanceof ScriptValue.Obj && (object13 = (obj9 = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object13 instanceof PolyClass) && obj9.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object13);
                        v9 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue17, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v9 = ScriptValue.NULL;
                }
                break block117;
            }
            if (scriptValue.asNum() > 0.0) {
                Object object;
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                if (scriptValue18 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object14;
                    String string = "windmill_dir";
                    String string6 = "int";
                    if (scriptValue18 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object14);
                        object = polyClassMachine_v2.tm$34_get_typed(string, string6);
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(ScriptValue.of((String)string6));
                        object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue18, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue19 = object;
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
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object15);
                        v11 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string7, scriptValue23));
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
                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object16);
                        v13 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue25.asStr()));
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
                    Object object;
                    Object object17;
                    ScriptValue.Obj obj10;
                    Object object18;
                    ScriptValue.Obj obj11;
                    Object object19;
                    ScriptValue.Obj obj12;
                    Object object20;
                    ScriptValue.Obj obj13;
                    Object object21;
                    ScriptValue scriptValue30 = ScriptValue.of((String)"<dark_gray>[debug] facing=");
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)"facing"));
                    ScriptValue scriptValue31 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue32 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue33 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue34 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue35 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue30, (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue31 != ScriptValue.NULL ? (scriptValue31 instanceof ScriptValue.Obj && (object21 = (obj13 = (ScriptValue.Obj)scriptValue31).instance()) != null && !(object21 instanceof PolyClass) && obj13.typeName().equals("Machine") ? new PolyClassMachine_v2(object21).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue31, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext)), (ScriptValue)ScriptValue.of((String)" seed=(")), (ScriptValue)(scriptValue32 != ScriptValue.NULL ? (scriptValue32 instanceof ScriptValue.Obj && (object20 = (obj12 = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object20 instanceof PolyClass) && obj12.typeName().equals("Machine") ? new PolyClassMachine_v2(object20).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue32, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(scriptValue33 != ScriptValue.NULL ? (scriptValue33 instanceof ScriptValue.Obj && (object19 = (obj11 = (ScriptValue.Obj)scriptValue33).instance()) != null && !(object19 instanceof PolyClass) && obj11.typeName().equals("Machine") ? new PolyClassMachine_v2(object19).pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue33, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(scriptValue34 != ScriptValue.NULL ? (scriptValue34 instanceof ScriptValue.Obj && (object18 = (obj10 = (ScriptValue.Obj)scriptValue34).instance()) != null && !(object18 instanceof PolyClass) && obj10.typeName().equals("Machine") ? new PolyClassMachine_v2(object18).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue34, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)")")), (ScriptValue)ScriptValue.of((String)" self_glued="));
                    ScriptValue scriptValue36 = scriptContext.getClassOrVar("Glue");
                    if (scriptValue36 != ScriptValue.NULL) {
                        ScriptValue.Obj obj14;
                        Object object22;
                        ScriptValue scriptValue37;
                        ScriptValue scriptValue38 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue38 != ScriptValue.NULL) {
                            ScriptValue.Obj obj15;
                            Object object23;
                            double d6 = 0.0;
                            double d7 = 0.0;
                            double d8 = 0.0;
                            if (scriptValue38 instanceof ScriptValue.Obj && (object23 = (obj15 = (ScriptValue.Obj)scriptValue38).instance()) != null && !(object23 instanceof PolyClass) && obj15.typeName().equals("Machine")) {
                                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object23);
                                v16 = polyClassMachine_v2.tm$68_block_at(d6, d7, d8);
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
                        if (scriptValue36 instanceof ScriptValue.Obj && (object22 = (obj14 = (ScriptValue.Obj)scriptValue36).instance()) != null && !(object22 instanceof PolyClass) && obj14.typeName().equals("Glue")) {
                            PolyClassGlue polyClassGlue = new PolyClassGlue(object22);
                            object17 = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue37));
                        } else {
                            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                            arrayList3.add(scriptValue37);
                            object17 = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue36, arrayList3, (ScriptContext)scriptContext);
                        }
                    } else {
                        object17 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue39 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue35, (ScriptValue)object17);
                    if (scriptValue29 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                        v18 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue39.asStr()));
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
                        Object object;
                        String string = "<red>Nothing attached to the bearing's face to assemble.";
                        if (scriptValue40 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue40).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                            v19 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                        } else {
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(ScriptValue.of((String)string));
                            v19 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue40, arrayList, (ScriptContext)scriptContext);
                        }
                    } else {
                        v19 = ScriptValue.NULL;
                    }
                } else {
                    Object object;
                    ScriptValue scriptValue41 = scriptContext.getClassOrVar("Glue");
                    if (scriptValue41 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object24;
                        ScriptValue scriptValue42 = scriptValue28;
                        if (scriptValue41 instanceof ScriptValue.Obj && (object24 = (obj = (ScriptValue.Obj)scriptValue41).instance()) != null && !(object24 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                            PolyClassGlue polyClassGlue = new PolyClassGlue(object24);
                            object = ScriptValue.of((boolean)polyClassGlue.tm$2_is_glued(scriptValue42));
                        } else {
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(scriptValue42);
                            object = PolyDispatch.bootstrapCall("memberCall", "is_glued", (ScriptValue)scriptValue41, arrayList, (ScriptContext)scriptContext);
                        }
                    } else {
                        object = ScriptValue.NULL;
                    }
                    if (object.asBool() ^ true) {
                        ScriptValue scriptValue43 = scriptContext.getClassOrVar("Player");
                        if (scriptValue43 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object25;
                            String string = "<red>That structure is not glued together - glue the sails to each other and to the bearing.";
                            if (scriptValue43 instanceof ScriptValue.Obj && (object25 = (obj = (ScriptValue.Obj)scriptValue43).instance()) != null && !(object25 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object25);
                                v21 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                            } else {
                                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                arrayList.add(ScriptValue.of((String)string));
                                v21 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue43, arrayList, (ScriptContext)scriptContext);
                            }
                        } else {
                            v21 = ScriptValue.NULL;
                        }
                    } else {
                        Object object26;
                        ScriptValue scriptValue44 = scriptContext.getClassOrVar("Glue");
                        if (scriptValue44 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object27;
                            ScriptValue scriptValue45 = scriptValue28;
                            String string = "sail";
                            if (scriptValue44 instanceof ScriptValue.Obj && (object27 = (obj = (ScriptValue.Obj)scriptValue44).instance()) != null && !(object27 instanceof PolyClass) && obj.typeName().equals("Glue")) {
                                PolyClassGlue polyClassGlue = new PolyClassGlue(object27);
                                object26 = ScriptValue.of((double)polyClassGlue.tm$4_count(scriptValue45, string));
                            } else {
                                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                arrayList.add(scriptValue45);
                                arrayList.add(ScriptValue.of((String)string));
                                object26 = PolyDispatch.bootstrapCall("memberCall", "count", (ScriptValue)scriptValue44, arrayList, (ScriptContext)scriptContext);
                            }
                        } else {
                            object26 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue46 = object26;
                        builder.val("sails", scriptValue46);
                        if (scriptValue46.asNum() < d) {
                            ScriptValue scriptValue47 = scriptContext.getClassOrVar("Player");
                            if (scriptValue47 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object28;
                                ScriptValue scriptValue48 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<red>Not enough sails: <white>"), (ScriptValue)scriptValue46), (ScriptValue)ScriptValue.of((String)"<red> of <white>")), (ScriptValue)ScriptValue.of((double)d)), (ScriptValue)ScriptValue.of((String)"<red> needed."));
                                if (scriptValue47 instanceof ScriptValue.Obj && (object28 = (obj = (ScriptValue.Obj)scriptValue47).instance()) != null && !(object28 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object28);
                                    v23 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue48.asStr()));
                                } else {
                                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                    arrayList.add(scriptValue48);
                                    v23 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue47, arrayList, (ScriptContext)scriptContext);
                                }
                            } else {
                                v23 = ScriptValue.NULL;
                            }
                        } else {
                            Object object29;
                            ScriptValue scriptValue49 = scriptContext.getClassOrVar("ContraptionManager");
                            if (scriptValue49 != ScriptValue.NULL) {
                                ScriptValue.Obj obj;
                                Object object30;
                                Object object31;
                                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                arrayList.add(scriptValue28);
                                ScriptValue scriptValue50 = scriptContext.getClassOrVar("Machine");
                                if (scriptValue50 != ScriptValue.NULL) {
                                    ScriptValue.Obj obj16;
                                    Object object32;
                                    double d9 = 0.0;
                                    double d10 = 0.0;
                                    double d11 = 0.0;
                                    if (scriptValue50 instanceof ScriptValue.Obj && (object32 = (obj16 = (ScriptValue.Obj)scriptValue50).instance()) != null && !(object32 instanceof PolyClass) && obj16.typeName().equals("Machine")) {
                                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object32);
                                        object31 = polyClassMachine_v2.tm$68_block_at(d9, d10, d11);
                                    } else {
                                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                                        arrayList5.add(ScriptValue.of((double)d9));
                                        arrayList5.add(ScriptValue.of((double)d10));
                                        arrayList5.add(ScriptValue.of((double)d11));
                                        object31 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue50, arrayList5, (ScriptContext)scriptContext);
                                    }
                                } else {
                                    object31 = ScriptValue.NULL;
                                }
                                arrayList.add((ScriptValue)object31);
                                object29 = scriptValue49 instanceof ScriptValue.Obj && (object30 = (obj = (ScriptValue.Obj)scriptValue49).instance()) != null && !(object30 instanceof PolyClass) && obj.typeName().equals("ContraptionManager") ? new PolyClassContraptionManager(object30).um$1_create_bearing(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create_bearing", (ScriptValue)scriptValue49, arrayList, (ScriptContext)scriptContext);
                            } else {
                                object29 = ScriptValue.NULL;
                            }
                            ScriptValue scriptValue51 = object29;
                            builder.val("contraption", scriptValue51);
                            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue51, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                                ScriptValue scriptValue52 = scriptContext.getClassOrVar("Player");
                                if (scriptValue52 != ScriptValue.NULL) {
                                    ScriptValue.Obj obj;
                                    Object object33;
                                    String string = "<red>Could not assemble that structure.";
                                    if (scriptValue52 instanceof ScriptValue.Obj && (object33 = (obj = (ScriptValue.Obj)scriptValue52).instance()) != null && !(object33 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object33);
                                        v26 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
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
                                    Object object34;
                                    ScriptValue scriptValue57;
                                    String string = "contraption_uuid";
                                    String string8 = "string";
                                    ScriptValue scriptValue58 = scriptContext.getClassOrVar("contraption");
                                    Object object35 = scriptValue57 = scriptValue58 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)scriptValue58, (ScriptContext)scriptContext) : ScriptValue.NULL;
                                    if (scriptValue56 instanceof ScriptValue.Obj && (object34 = (obj = (ScriptValue.Obj)scriptValue56).instance()) != null && !(object34 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object34);
                                        v29 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string8, scriptValue57));
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
                                    Object object36;
                                    String string = "assembled";
                                    String string9 = "int";
                                    ScriptValue scriptValue60 = ScriptValue.of((double)1.0);
                                    if (scriptValue59 instanceof ScriptValue.Obj && (object36 = (obj = (ScriptValue.Obj)scriptValue59).instance()) != null && !(object36 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object36);
                                        v30 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string9, scriptValue60));
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
                                    Object object37;
                                    String string = "assembled_tick";
                                    String string10 = "int";
                                    ArrayList arrayList8 = new ArrayList();
                                    ScriptValue scriptValue62 = ScriptFormula.callBuiltin((String)"tick", arrayList8, (ScriptContext)scriptContext);
                                    if (scriptValue61 instanceof ScriptValue.Obj && (object37 = (obj = (ScriptValue.Obj)scriptValue61).instance()) != null && !(object37 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object37);
                                        v31 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string10, scriptValue62));
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
                                    Object object38;
                                    ScriptValue scriptValue64 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<green>Windmill assembled <gray>("), (ScriptValue)scriptValue46), (ScriptValue)ScriptValue.of((String)" sails)."));
                                    if (scriptValue63 instanceof ScriptValue.Obj && (object38 = (obj = (ScriptValue.Obj)scriptValue63).instance()) != null && !(object38 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                        PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object38);
                                        v32 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue64.asStr()));
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
        }
    }
}
