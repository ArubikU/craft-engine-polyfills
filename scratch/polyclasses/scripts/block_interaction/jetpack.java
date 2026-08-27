/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassEntity_v2
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEntity_v2;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class Jetpack {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onEquip(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Item");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "gas";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Item")) {
                PolyClassItem polyClassItem = new PolyClassItem(object3);
                object2 = ScriptValue.of((double)polyClassItem.tm$24_tank(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                object2 = PolyDispatch.bootstrapCall("memberCall", "tank", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("fuel", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Item");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "gas";
            if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Item")) {
                PolyClassItem polyClassItem = new PolyClassItem(object4);
                object = ScriptValue.of((double)polyClassItem.tm$16_tank_capacity(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "tank_capacity", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("cap", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue6 = ScriptValue.of((String)("<aqua>Jetpack equipped <gray>(" + scriptValue2.asStr() + "/" + scriptValue4.asStr() + " gas) - jump to rise, sneak to descend, let go to hover."));
            if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue6.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue6);
                v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onUnequip(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<gray>Jetpack unequipped.";
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onEquippedTick(ScriptContext.Builder builder) {
        block58: {
            PolyClassPlayer polyClassPlayer;
            PolyClassPlayer polyClassPlayer2;
            Object object;
            PolyClassPlayer polyClassPlayer3;
            PolyClassPlayer polyClassPlayer4;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
            boolean bl = (scriptValue != ScriptValue.NULL ? ((polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer4.tg$53_is_on_ground() : PolyDispatch.bootstrapGet("memberGet", "is_on_ground", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool()) ^ true;
            ScriptValue scriptValue2 = ScriptValue.of((boolean)bl);
            builder.val("airborne", scriptValue2);
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassPlayer3 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassPlayer3.pg$48_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("descending", scriptValue4);
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("jumped");
            builder.val("ascending", scriptValue5);
            boolean bl2 = bl || scriptValue5.asBool() && scriptValue4.asBool() ^ true;
            ScriptValue scriptValue6 = ScriptValue.of((boolean)bl2);
            builder.val("active", scriptValue6);
            if (bl2 ^ true) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Item");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "gas";
                if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Item")) {
                    PolyClassItem polyClassItem = new PolyClassItem(object2);
                    object = ScriptValue.of((double)polyClassItem.tm$24_tank(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    object = PolyDispatch.bootstrapCall("memberCall", "tank", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = object;
            builder.val("fuel", scriptValue8);
            if (scriptValue8.asNum() <= 0.0) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
            ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? ((polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassPlayer2.pg$56_velocity() : PolyDispatch.bootstrapGet("memberGet", "velocity", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("v", scriptValue10);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptValue4)) {
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("v");
                Object object3 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                if (object3.asNum() > 0.0) {
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
                    if (scriptValue12 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object4;
                        ScriptValue scriptValue13;
                        ScriptValue scriptValue14 = scriptContext.getClassOrVar("v");
                        ScriptValue scriptValue15 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        ScriptValue scriptValue16 = scriptContext.getClassOrVar("v");
                        double d = Math.max((scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - scriptContext.getNum("THRUST"), 0.0);
                        ScriptValue scriptValue17 = scriptContext.getClassOrVar("v");
                        Object object5 = scriptValue13 = scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        if (scriptValue12 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer5 = new PolyClassPlayer(object4);
                            v3 = ScriptValue.of((boolean)polyClassPlayer5.tm$16_set_velocity(scriptValue15.asNum(), d, scriptValue13.asNum()));
                        } else {
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add(scriptValue15);
                            arrayList.add(ScriptValue.of((double)d));
                            arrayList.add(scriptValue13);
                            v3 = PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
                        }
                    } else {
                        v3 = ScriptValue.NULL;
                    }
                } else {
                    ScriptValue scriptValue18 = scriptContext.getClassOrVar("v");
                    Object object6 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    if (object6.asNum() < 0.0 && bl) {
                        ScriptValue scriptValue19 = scriptContext.getClassOrVar("Player");
                        if (scriptValue19 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object7;
                            ScriptValue scriptValue20;
                            ScriptValue scriptValue21 = scriptContext.getClassOrVar("v");
                            ScriptValue scriptValue22 = scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue21, (ScriptContext)scriptContext) : ScriptValue.NULL;
                            ScriptValue scriptValue23 = scriptContext.getClassOrVar("v");
                            double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("THRUST")).asNum(), 0.0);
                            ScriptValue scriptValue24 = scriptContext.getClassOrVar("v");
                            Object object8 = scriptValue20 = scriptValue24 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue24, (ScriptContext)scriptContext) : ScriptValue.NULL;
                            if (scriptValue19 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object7);
                                v6 = ScriptValue.of((boolean)polyClassPlayer6.tm$16_set_velocity(scriptValue22.asNum(), d, scriptValue20.asNum()));
                            } else {
                                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                                arrayList.add(scriptValue22);
                                arrayList.add(ScriptValue.of((double)d));
                                arrayList.add(scriptValue20);
                                v6 = PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
                            }
                        } else {
                            v6 = ScriptValue.NULL;
                        }
                    }
                }
            } else if (scriptValue5.asBool()) {
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("Player");
                if (scriptValue25 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object9;
                    ScriptValue scriptValue26;
                    ScriptValue scriptValue27 = scriptContext.getClassOrVar("v");
                    ScriptValue scriptValue28 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue29 = scriptContext.getClassOrVar("v");
                    double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue29, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("THRUST")).asNum(), 2.0 * scriptContext.getNum("THRUST"));
                    ScriptValue scriptValue30 = scriptContext.getClassOrVar("v");
                    Object object10 = scriptValue26 = scriptValue30 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue30, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    if (scriptValue25 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer7 = new PolyClassPlayer(object9);
                        v8 = ScriptValue.of((boolean)polyClassPlayer7.tm$16_set_velocity(scriptValue28.asNum(), d, scriptValue26.asNum()));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue28);
                        arrayList.add(ScriptValue.of((double)d));
                        arrayList.add(scriptValue26);
                        v8 = PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue25, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v8 = ScriptValue.NULL;
                }
            } else if (bl) {
                ScriptValue scriptValue31 = scriptContext.getClassOrVar("Player");
                if (scriptValue31 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object11;
                    ScriptValue scriptValue32;
                    ScriptValue scriptValue33 = scriptContext.getClassOrVar("v");
                    ScriptValue scriptValue34 = scriptValue33 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue33, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue35 = scriptContext.getClassOrVar("v");
                    double d = Math.max((scriptValue35 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue35, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - scriptContext.getNum("THRUST"), -2.0 * scriptContext.getNum("THRUST"));
                    ScriptValue scriptValue36 = scriptContext.getClassOrVar("v");
                    Object object12 = scriptValue32 = scriptValue36 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue36, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    if (scriptValue31 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue31).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer8 = new PolyClassPlayer(object11);
                        v10 = ScriptValue.of((boolean)polyClassPlayer8.tm$16_set_velocity(scriptValue34.asNum(), d, scriptValue32.asNum()));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue34);
                        arrayList.add(ScriptValue.of((double)d));
                        arrayList.add(scriptValue32);
                        v10 = PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue31, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v10 = ScriptValue.NULL;
                }
            }
            ScriptValue scriptValue37 = scriptContext.getClassOrVar("Entity");
            if (scriptValue37 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object13;
                if (scriptValue37 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue37).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Entity")) {
                    PolyClassEntity_v2 polyClassEntity_v2 = new PolyClassEntity_v2(object13);
                    v11 = ScriptValue.of((boolean)polyClassEntity_v2.tm$22_reset_fall_distance());
                } else {
                    ArrayList arrayList = new ArrayList();
                    v11 = PolyDispatch.bootstrapCall("memberCall", "reset_fall_distance", (ScriptValue)scriptValue37, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v11 = ScriptValue.NULL;
            }
            double d = Math.max(scriptValue8.asNum() - scriptContext.getNum("CONSUMPTION_PER_CALL"), 0.0);
            ScriptValue scriptValue38 = ScriptValue.of((double)d);
            builder.val("new_fuel", scriptValue38);
            ScriptValue scriptValue39 = scriptContext.getClassOrVar("Item");
            if (scriptValue39 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                String string = "gas";
                double d2 = d;
                if (scriptValue39 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue39).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Item")) {
                    PolyClassItem polyClassItem = new PolyClassItem(object14);
                    v12 = polyClassItem.tm$45_set_tank(string, d2);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((double)d2));
                    v12 = PolyDispatch.bootstrapCall("memberCall", "set_tank", (ScriptValue)scriptValue39, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v12 = ScriptValue.NULL;
            }
            ScriptValue scriptValue40 = scriptContext.getClassOrVar("Player");
            ScriptValue scriptValue41 = scriptValue40 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue40)) != null ? polyClassPlayer.pg$37_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue40, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("pos", scriptValue41);
            ScriptValue scriptValue42 = scriptContext.getClassOrVar("World");
            if (scriptValue42 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object15;
                String string = "cloud";
                ScriptValue scriptValue43 = scriptContext.getClassOrVar("pos");
                ScriptValue scriptValue44 = scriptValue43 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue43, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue45 = scriptContext.getClassOrVar("pos");
                double d3 = (scriptValue45 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue45, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - 0.3;
                ScriptValue scriptValue46 = scriptContext.getClassOrVar("pos");
                ScriptValue scriptValue47 = scriptValue46 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue46, (ScriptContext)scriptContext) : ScriptValue.NULL;
                double d4 = 2.0;
                double d5 = 0.1;
                double d6 = 0.02;
                double d7 = 0.1;
                double d8 = 0.01;
                if (scriptValue42 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue42).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object15);
                    v13 = polyClassWorld.tm$12_spawn_particle(string, scriptValue44.asNum(), d3, scriptValue47.asNum(), d4, d5, d6, d7, d8);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue44);
                    arrayList.add(ScriptValue.of((double)d3));
                    arrayList.add(scriptValue47);
                    arrayList.add(ScriptValue.of((double)d4));
                    arrayList.add(ScriptValue.of((double)d5));
                    arrayList.add(ScriptValue.of((double)d6));
                    arrayList.add(ScriptValue.of((double)d7));
                    arrayList.add(ScriptValue.of((double)d8));
                    v13 = PolyDispatch.bootstrapCall("memberCall", "spawn_particle", (ScriptValue)scriptValue42, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v13 = ScriptValue.NULL;
            }
            ScriptValue scriptValue48 = scriptContext.getClassOrVar("World");
            if (scriptValue48 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                String string = "flame";
                ScriptValue scriptValue49 = scriptContext.getClassOrVar("pos");
                ScriptValue scriptValue50 = scriptValue49 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue49, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue51 = scriptContext.getClassOrVar("pos");
                double d9 = (scriptValue51 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue51, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - 0.3;
                ScriptValue scriptValue52 = scriptContext.getClassOrVar("pos");
                ScriptValue scriptValue53 = scriptValue52 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue52, (ScriptContext)scriptContext) : ScriptValue.NULL;
                double d10 = 1.0;
                double d11 = 0.08;
                double d12 = 0.02;
                double d13 = 0.08;
                double d14 = 0.005;
                if (scriptValue48 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue48).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object16);
                    v14 = polyClassWorld.tm$12_spawn_particle(string, scriptValue50.asNum(), d9, scriptValue53.asNum(), d10, d11, d12, d13, d14);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue50);
                    arrayList.add(ScriptValue.of((double)d9));
                    arrayList.add(scriptValue53);
                    arrayList.add(ScriptValue.of((double)d10));
                    arrayList.add(ScriptValue.of((double)d11));
                    arrayList.add(ScriptValue.of((double)d12));
                    arrayList.add(ScriptValue.of((double)d13));
                    arrayList.add(ScriptValue.of((double)d14));
                    v14 = PolyDispatch.bootstrapCall("memberCall", "spawn_particle", (ScriptValue)scriptValue48, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v14 = ScriptValue.NULL;
            }
            ScriptValue scriptValue54 = scriptContext.getClassOrVar("World");
            if (scriptValue54 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object17;
                ScriptValue scriptValue55 = scriptContext.getClassOrVar("pos");
                ScriptValue scriptValue56 = scriptValue55 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue55, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue57 = scriptContext.getClassOrVar("pos");
                ScriptValue scriptValue58 = scriptValue57 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue57, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue59 = scriptContext.getClassOrVar("pos");
                ScriptValue scriptValue60 = scriptValue59 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue59, (ScriptContext)scriptContext) : ScriptValue.NULL;
                String string = "minecraft:entity.firework_rocket.launch";
                double d15 = 0.1;
                double d16 = 1.8;
                if (scriptValue54 instanceof ScriptValue.Obj && (object17 = (obj = (ScriptValue.Obj)scriptValue54).instance()) != null && !(object17 instanceof PolyClass) && obj.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object17);
                    v15 = polyClassWorld.tm$16_play_sound(scriptValue56.asNum(), scriptValue58.asNum(), scriptValue60.asNum(), string, d15, d16);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue56);
                    arrayList.add(scriptValue58);
                    arrayList.add(scriptValue60);
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((double)d15));
                    arrayList.add(ScriptValue.of((double)d16));
                    v15 = PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue54, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v15 = ScriptValue.NULL;
            }
            if (!(d <= 0.0)) break block58;
            ScriptValue scriptValue61 = scriptContext.getClassOrVar("Entity");
            if (scriptValue61 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object18;
                String string = "slow_falling";
                double d17 = 60.0;
                double d18 = 0.0;
                if (scriptValue61 instanceof ScriptValue.Obj && (object18 = (obj = (ScriptValue.Obj)scriptValue61).instance()) != null && !(object18 instanceof PolyClass) && obj.typeName().equals("Entity")) {
                    PolyClassEntity_v2 polyClassEntity_v2 = new PolyClassEntity_v2(object18);
                    v16 = ScriptValue.of((boolean)polyClassEntity_v2.tm$2_add_potion_effect(string, d17, d18));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((double)d17));
                    arrayList.add(ScriptValue.of((double)d18));
                    v16 = PolyDispatch.bootstrapCall("memberCall", "add_potion_effect", (ScriptValue)scriptValue61, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v16 = ScriptValue.NULL;
            }
            ScriptValue scriptValue62 = scriptContext.getClassOrVar("Player");
            if (scriptValue62 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object19;
                String string = "<red>Jetpack out of gas.";
                if (scriptValue62 instanceof ScriptValue.Obj && (object19 = (obj = (ScriptValue.Obj)scriptValue62).instance()) != null && !(object19 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer9 = new PolyClassPlayer(object19);
                    v17 = ScriptValue.of((boolean)polyClassPlayer9.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v17 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue62, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v17 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 0.15;
        ScriptValue scriptValue = ScriptValue.of((double)0.15);
        builder.val("THRUST", scriptValue);
        double d2 = 24.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)24.0);
        builder.val("CONSUMPTION_PER_CALL", scriptValue2);
        FILE_SCOPE = builder.build();
    }
}
