/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassEntity
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
import dev.arubik.craftengine.script.PolyClassEntity;
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
                object2 = PolyDispatch.bootstrapCall("memberCall", "tank", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "tank_capacity", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onEquippedTick(ScriptContext.Builder builder) {
        block58: {
            ScriptValue scriptValue;
            ScriptValue scriptValue2;
            Object object;
            ScriptValue scriptValue3;
            ScriptValue scriptValue4;
            ScriptContext scriptContext = builder.peek();
            PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            boolean bl = (polyClassPlayer != null ? polyClassPlayer.tg$53_is_on_ground() : ((scriptValue4 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_on_ground", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) ^ true;
            ScriptValue scriptValue5 = ScriptValue.of((boolean)bl);
            builder.val("airborne", scriptValue5);
            PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            ScriptValue scriptValue6 = polyClassPlayer2 != null ? polyClassPlayer2.pg$48_is_sneaking() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("descending", scriptValue6);
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("jumped");
            builder.val("ascending", scriptValue7);
            boolean bl2 = bl || scriptValue7.asBool() && scriptValue6.asBool() ^ true;
            ScriptValue scriptValue8 = ScriptValue.of((boolean)bl2);
            builder.val("active", scriptValue8);
            if (bl2 ^ true) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Item");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "gas";
                if (scriptValue9 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Item")) {
                    PolyClassItem polyClassItem = new PolyClassItem(object2);
                    object = ScriptValue.of((double)polyClassItem.tm$24_tank(string));
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "tank", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = object;
            builder.val("fuel", scriptValue10);
            if (scriptValue10.asNum() <= 0.0) {
                return ScriptValue.NULL;
            }
            PolyClassPlayer polyClassPlayer3 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            ScriptValue scriptValue11 = polyClassPlayer3 != null ? polyClassPlayer3.pg$56_velocity() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("v", scriptValue11);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptValue6)) {
                Object object3 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                if (object3.asNum() > 0.0) {
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
                    if (scriptValue12 != ScriptValue.NULL) {
                        ScriptValue.Obj obj;
                        Object object4;
                        ScriptValue scriptValue13;
                        ScriptValue scriptValue14 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        double d = Math.max((scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - scriptContext.getNum("THRUST"), 0.0);
                        Object object5 = scriptValue13 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        if (scriptValue12 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                            PolyClassPlayer polyClassPlayer4 = new PolyClassPlayer(object4);
                            v3 = ScriptValue.of((boolean)polyClassPlayer4.tm$16_set_velocity(scriptValue14.asNum(), d, scriptValue13.asNum()));
                        } else {
                            v3 = PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue12, (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
                        }
                    } else {
                        v3 = ScriptValue.NULL;
                    }
                } else {
                    Object object6 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    if (object6.asNum() < 0.0 && bl) {
                        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
                        if (scriptValue15 != ScriptValue.NULL) {
                            ScriptValue.Obj obj;
                            Object object7;
                            ScriptValue scriptValue16;
                            ScriptValue scriptValue17 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                            double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("THRUST")).asNum(), 0.0);
                            Object object8 = scriptValue16 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                            if (scriptValue15 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                                PolyClassPlayer polyClassPlayer5 = new PolyClassPlayer(object7);
                                v6 = ScriptValue.of((boolean)polyClassPlayer5.tm$16_set_velocity(scriptValue17.asNum(), d, scriptValue16.asNum()));
                            } else {
                                v6 = PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue15, (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue16, (ScriptContext)scriptContext);
                            }
                        } else {
                            v6 = ScriptValue.NULL;
                        }
                    }
                }
            } else if (scriptValue7.asBool()) {
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Player");
                if (scriptValue18 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object9;
                    ScriptValue scriptValue19;
                    ScriptValue scriptValue20 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("THRUST")).asNum(), 2.0 * scriptContext.getNum("THRUST"));
                    Object object10 = scriptValue19 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    if (scriptValue18 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object9);
                        v8 = ScriptValue.of((boolean)polyClassPlayer6.tm$16_set_velocity(scriptValue20.asNum(), d, scriptValue19.asNum()));
                    } else {
                        v8 = PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue18, (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
                    }
                } else {
                    v8 = ScriptValue.NULL;
                }
            } else if (bl) {
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Player");
                if (scriptValue21 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object11;
                    ScriptValue scriptValue22;
                    ScriptValue scriptValue23 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    double d = Math.max((scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - scriptContext.getNum("THRUST"), -2.0 * scriptContext.getNum("THRUST"));
                    Object object12 = scriptValue22 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    if (scriptValue21 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Player")) {
                        PolyClassPlayer polyClassPlayer7 = new PolyClassPlayer(object11);
                        v10 = ScriptValue.of((boolean)polyClassPlayer7.tm$16_set_velocity(scriptValue23.asNum(), d, scriptValue22.asNum()));
                    } else {
                        v10 = PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue21, (ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
                    }
                } else {
                    v10 = ScriptValue.NULL;
                }
            }
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("Entity");
            if (scriptValue24 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object13;
                if (scriptValue24 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Entity")) {
                    PolyClassEntity polyClassEntity = new PolyClassEntity(object13);
                    v11 = ScriptValue.of((boolean)polyClassEntity.tm$22_reset_fall_distance());
                } else {
                    v11 = PolyDispatch.bootstrapCall("memberCall", "reset_fall_distance", (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
                }
            } else {
                v11 = ScriptValue.NULL;
            }
            double d = Math.max(scriptValue10.asNum() - scriptContext.getNum("CONSUMPTION_PER_CALL"), 0.0);
            ScriptValue scriptValue25 = ScriptValue.of((double)d);
            builder.val("new_fuel", scriptValue25);
            ScriptValue scriptValue26 = scriptContext.getClassOrVar("Item");
            if (scriptValue26 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                String string = "gas";
                double d2 = d;
                if (scriptValue26 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Item")) {
                    PolyClassItem polyClassItem = new PolyClassItem(object14);
                    v12 = polyClassItem.tm$45_set_tank(string, d2);
                } else {
                    v12 = PolyDispatch.bootstrapCall("memberCall", "set_tank", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
                }
            } else {
                v12 = ScriptValue.NULL;
            }
            PolyClassPlayer polyClassPlayer8 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            ScriptValue scriptValue27 = polyClassPlayer8 != null ? polyClassPlayer8.pg$37_pos() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("pos", scriptValue27);
            ScriptValue scriptValue28 = scriptContext.getClassOrVar("World");
            if (scriptValue28 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object15;
                String string = "cloud";
                ScriptValue scriptValue29 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL;
                double d3 = (scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - 0.3;
                ScriptValue scriptValue30 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL;
                double d4 = 2.0;
                double d5 = 0.1;
                double d6 = 0.02;
                double d7 = 0.1;
                double d8 = 0.01;
                if (scriptValue28 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue28).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object15);
                    v13 = polyClassWorld.tm$12_spawn_particle(string, scriptValue29.asNum(), d3, scriptValue30.asNum(), d4, d5, d6, d7, d8);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue29);
                    arrayList.add(ScriptValue.of((double)d3));
                    arrayList.add(scriptValue30);
                    arrayList.add(ScriptValue.of((double)d4));
                    arrayList.add(ScriptValue.of((double)d5));
                    arrayList.add(ScriptValue.of((double)d6));
                    arrayList.add(ScriptValue.of((double)d7));
                    arrayList.add(ScriptValue.of((double)d8));
                    v13 = PolyDispatch.bootstrapCall("memberCall", "spawn_particle", (ScriptValue)scriptValue28, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v13 = ScriptValue.NULL;
            }
            ScriptValue scriptValue31 = scriptContext.getClassOrVar("World");
            if (scriptValue31 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                String string = "flame";
                ScriptValue scriptValue32 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL;
                double d9 = (scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - 0.3;
                ScriptValue scriptValue33 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL;
                double d10 = 1.0;
                double d11 = 0.08;
                double d12 = 0.02;
                double d13 = 0.08;
                double d14 = 0.005;
                if (scriptValue31 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue31).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object16);
                    v14 = polyClassWorld.tm$12_spawn_particle(string, scriptValue32.asNum(), d9, scriptValue33.asNum(), d10, d11, d12, d13, d14);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue32);
                    arrayList.add(ScriptValue.of((double)d9));
                    arrayList.add(scriptValue33);
                    arrayList.add(ScriptValue.of((double)d10));
                    arrayList.add(ScriptValue.of((double)d11));
                    arrayList.add(ScriptValue.of((double)d12));
                    arrayList.add(ScriptValue.of((double)d13));
                    arrayList.add(ScriptValue.of((double)d14));
                    v14 = PolyDispatch.bootstrapCall("memberCall", "spawn_particle", (ScriptValue)scriptValue31, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v14 = ScriptValue.NULL;
            }
            ScriptValue scriptValue34 = scriptContext.getClassOrVar("World");
            if (scriptValue34 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object17;
                ScriptValue scriptValue35 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue36 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue37 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL;
                String string = "minecraft:entity.firework_rocket.launch";
                double d15 = 0.1;
                double d16 = 1.8;
                if (scriptValue34 instanceof ScriptValue.Obj && (object17 = (obj = (ScriptValue.Obj)scriptValue34).instance()) != null && !(object17 instanceof PolyClass) && obj.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object17);
                    v15 = polyClassWorld.tm$16_play_sound(scriptValue35.asNum(), scriptValue36.asNum(), scriptValue37.asNum(), string, d15, d16);
                } else {
                    v15 = PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue34, (ScriptValue)scriptValue35, (ScriptValue)scriptValue36, (ScriptValue)scriptValue37, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d15), (ScriptValue)ScriptValue.of((double)d16), (ScriptContext)scriptContext);
                }
            } else {
                v15 = ScriptValue.NULL;
            }
            if (!(d <= 0.0)) break block58;
            ScriptValue scriptValue38 = scriptContext.getClassOrVar("Entity");
            if (scriptValue38 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object18;
                String string = "slow_falling";
                double d17 = 60.0;
                double d18 = 0.0;
                if (scriptValue38 instanceof ScriptValue.Obj && (object18 = (obj = (ScriptValue.Obj)scriptValue38).instance()) != null && !(object18 instanceof PolyClass) && obj.typeName().equals("Entity")) {
                    PolyClassEntity polyClassEntity = new PolyClassEntity(object18);
                    v16 = ScriptValue.of((boolean)polyClassEntity.tm$2_add_potion_effect(string, d17, d18));
                } else {
                    v16 = PolyDispatch.bootstrapCall("memberCall", "add_potion_effect", (ScriptValue)scriptValue38, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d17), (ScriptValue)ScriptValue.of((double)d18), (ScriptContext)scriptContext);
                }
            } else {
                v16 = ScriptValue.NULL;
            }
            ScriptValue scriptValue39 = scriptContext.getClassOrVar("Player");
            if (scriptValue39 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object19;
                String string = "<red>Jetpack out of gas.";
                if (scriptValue39 instanceof ScriptValue.Obj && (object19 = (obj = (ScriptValue.Obj)scriptValue39).instance()) != null && !(object19 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer9 = new PolyClassPlayer(object19);
                    v17 = ScriptValue.of((boolean)polyClassPlayer9.tm$42_send_message(string));
                } else {
                    v17 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue39, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
