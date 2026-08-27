/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassEntity
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyClassVector
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassEntity;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyClassVector;
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
            String string = "gas";
            PolyClassItem polyClassItem = PolyClassItem.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassItem != null ? ScriptValue.of((double)polyClassItem.tm$24_tank(string)) : PolyDispatch.bootstrapCall("memberCall", "tank", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("fuel", scriptValue2);
        if (scriptValue != ScriptValue.NULL) {
            String string = "gas";
            PolyClassItem polyClassItem = PolyClassItem.ofGuarded((ScriptValue)scriptValue);
            object = polyClassItem != null ? ScriptValue.of((double)polyClassItem.tm$16_tank_capacity(string)) : PolyDispatch.bootstrapCall("memberCall", "tank_capacity", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("cap", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue scriptValue5 = ScriptValue.of((String)("<aqua>Jetpack equipped <gray>(" + scriptValue2.asStr() + "/" + scriptValue3.asStr() + " gas) - jump to rise, sneak to descend, let go to hover."));
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue4);
            v2 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue5.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onUnequip(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            String string = "<gray>Jetpack unequipped.";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onEquippedTick(ScriptContext.Builder builder) {
        block36: {
            ScriptValue scriptValue;
            PolyClassEntity polyClassEntity;
            ScriptValue scriptValue2;
            Object object;
            ScriptValue scriptValue3;
            ScriptValue scriptValue4;
            ScriptContext scriptContext = builder.peek();
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
            boolean bl = (polyClassPlayer_v2 != null ? polyClassPlayer_v2.tg$53_is_on_ground() : ((scriptValue4 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_on_ground", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) ^ true;
            ScriptValue scriptValue5 = ScriptValue.of((boolean)bl);
            builder.val("airborne", scriptValue5);
            PolyClassPlayer_v2 polyClassPlayer_v22 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
            ScriptValue scriptValue6 = polyClassPlayer_v22 != null ? polyClassPlayer_v22.pg$48_is_sneaking() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
                String string = "gas";
                PolyClassItem polyClassItem = PolyClassItem.ofGuarded((ScriptValue)scriptValue9);
                object = polyClassItem != null ? ScriptValue.of((double)polyClassItem.tm$24_tank(string)) : PolyDispatch.bootstrapCall("memberCall", "tank", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = object;
            builder.val("fuel", scriptValue10);
            if (scriptValue10.asNum() <= 0.0) {
                return ScriptValue.NULL;
            }
            PolyClassPlayer_v2 polyClassPlayer_v23 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
            ScriptValue scriptValue11 = polyClassPlayer_v23 != null ? polyClassPlayer_v23.pg$56_velocity() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("v", scriptValue11);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptValue6)) {
                PolyClassVector polyClassVector;
                double d = scriptValue11 != ScriptValue.NULL ? ((polyClassVector = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector.tg$15_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum();
                if (d > 0.0) {
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
                    if (scriptValue12 != ScriptValue.NULL) {
                        PolyClassVector polyClassVector2;
                        PolyClassVector polyClassVector3;
                        ScriptValue scriptValue13;
                        PolyClassVector polyClassVector4;
                        Object object2 = scriptValue11 != ScriptValue.NULL ? ((polyClassVector4 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector4.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : (scriptValue13 = ScriptValue.NULL);
                        double d2 = Math.max((scriptValue11 != ScriptValue.NULL ? ((polyClassVector3 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector3.tg$15_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) - scriptContext.getNum("THRUST"), 0.0);
                        ScriptValue scriptValue14 = scriptValue11 != ScriptValue.NULL ? ((polyClassVector2 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector2.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                        PolyClassPlayer_v2 polyClassPlayer_v24 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue12);
                        v3 = polyClassPlayer_v24 != null ? ScriptValue.of((boolean)polyClassPlayer_v24.tm$16_set_velocity(scriptValue13.asNum(), d2, scriptValue14.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
                    } else {
                        v3 = ScriptValue.NULL;
                    }
                } else {
                    PolyClassVector polyClassVector5;
                    double d3 = scriptValue11 != ScriptValue.NULL ? ((polyClassVector5 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector5.tg$15_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum();
                    if (d3 < 0.0 && bl) {
                        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
                        if (scriptValue15 != ScriptValue.NULL) {
                            PolyClassVector polyClassVector6;
                            PolyClassVector polyClassVector7;
                            ScriptValue scriptValue16;
                            PolyClassVector polyClassVector8;
                            Object object3 = scriptValue11 != ScriptValue.NULL ? ((polyClassVector8 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector8.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : (scriptValue16 = ScriptValue.NULL);
                            double d4 = Math.min(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue11 != ScriptValue.NULL ? ((polyClassVector7 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector7.pg$14_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("THRUST")).asNum(), 0.0);
                            ScriptValue scriptValue17 = scriptValue11 != ScriptValue.NULL ? ((polyClassVector6 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector6.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                            PolyClassPlayer_v2 polyClassPlayer_v25 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue15);
                            v6 = polyClassPlayer_v25 != null ? ScriptValue.of((boolean)polyClassPlayer_v25.tm$16_set_velocity(scriptValue16.asNum(), d4, scriptValue17.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue15, (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
                        } else {
                            v6 = ScriptValue.NULL;
                        }
                    }
                }
            } else if (scriptValue7.asBool()) {
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Player");
                if (scriptValue18 != ScriptValue.NULL) {
                    PolyClassVector polyClassVector;
                    PolyClassVector polyClassVector9;
                    ScriptValue scriptValue19;
                    PolyClassVector polyClassVector10;
                    Object object4 = scriptValue11 != ScriptValue.NULL ? ((polyClassVector10 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector10.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : (scriptValue19 = ScriptValue.NULL);
                    double d = Math.min(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue11 != ScriptValue.NULL ? ((polyClassVector9 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector9.pg$14_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("THRUST")).asNum(), 2.0 * scriptContext.getNum("THRUST"));
                    ScriptValue scriptValue20 = scriptValue11 != ScriptValue.NULL ? ((polyClassVector = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    PolyClassPlayer_v2 polyClassPlayer_v26 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue18);
                    v8 = polyClassPlayer_v26 != null ? ScriptValue.of((boolean)polyClassPlayer_v26.tm$16_set_velocity(scriptValue19.asNum(), d, scriptValue20.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
                } else {
                    v8 = ScriptValue.NULL;
                }
            } else if (bl) {
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Player");
                if (scriptValue21 != ScriptValue.NULL) {
                    PolyClassVector polyClassVector;
                    PolyClassVector polyClassVector11;
                    ScriptValue scriptValue22;
                    PolyClassVector polyClassVector12;
                    Object object5 = scriptValue11 != ScriptValue.NULL ? ((polyClassVector12 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector12.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : (scriptValue22 = ScriptValue.NULL);
                    double d = Math.max((scriptValue11 != ScriptValue.NULL ? ((polyClassVector11 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector11.tg$15_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) - scriptContext.getNum("THRUST"), -2.0 * scriptContext.getNum("THRUST"));
                    ScriptValue scriptValue23 = scriptValue11 != ScriptValue.NULL ? ((polyClassVector = PolyClassVector.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassVector.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    PolyClassPlayer_v2 polyClassPlayer_v27 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue21);
                    v10 = polyClassPlayer_v27 != null ? ScriptValue.of((boolean)polyClassPlayer_v27.tm$16_set_velocity(scriptValue22.asNum(), d, scriptValue23.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set_velocity", (ScriptValue)scriptValue21, (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
                } else {
                    v10 = ScriptValue.NULL;
                }
            }
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("Entity");
            Object object6 = scriptValue24 != ScriptValue.NULL ? ((polyClassEntity = PolyClassEntity.ofGuarded((ScriptValue)scriptValue24)) != null ? ScriptValue.of((boolean)polyClassEntity.tm$22_reset_fall_distance()) : PolyDispatch.bootstrapCall("memberCall", "reset_fall_distance", (ScriptValue)scriptValue24, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d = Math.max(scriptValue10.asNum() - scriptContext.getNum("CONSUMPTION_PER_CALL"), 0.0);
            ScriptValue scriptValue25 = ScriptValue.of((double)d);
            builder.val("new_fuel", scriptValue25);
            if (scriptValue9 != ScriptValue.NULL) {
                String string = "gas";
                double d5 = d;
                PolyClassItem polyClassItem = PolyClassItem.ofGuarded((ScriptValue)scriptValue9);
                v12 = polyClassItem != null ? polyClassItem.tm$45_set_tank(string, d5) : PolyDispatch.bootstrapCall("memberCall", "set_tank", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
            } else {
                v12 = ScriptValue.NULL;
            }
            PolyClassPlayer_v2 polyClassPlayer_v28 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
            ScriptValue scriptValue26 = polyClassPlayer_v28 != null ? polyClassPlayer_v28.pg$37_pos() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("pos", scriptValue26);
            ScriptValue scriptValue27 = scriptContext.getClassOrVar("World");
            if (scriptValue27 != ScriptValue.NULL) {
                PolyClassVector polyClassVector;
                PolyClassVector polyClassVector13;
                ScriptValue scriptValue28;
                PolyClassVector polyClassVector14;
                String string = "cloud";
                Object object7 = scriptValue26 != ScriptValue.NULL ? ((polyClassVector14 = PolyClassVector.ofGuarded((ScriptValue)scriptValue26)) != null ? polyClassVector14.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue26, (ScriptContext)scriptContext)) : (scriptValue28 = ScriptValue.NULL);
                double d6 = (scriptValue26 != ScriptValue.NULL ? ((polyClassVector13 = PolyClassVector.ofGuarded((ScriptValue)scriptValue26)) != null ? polyClassVector13.tg$15_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue26, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) - 0.3;
                ScriptValue scriptValue29 = scriptValue26 != ScriptValue.NULL ? ((polyClassVector = PolyClassVector.ofGuarded((ScriptValue)scriptValue26)) != null ? polyClassVector.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue26, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d7 = 2.0;
                double d8 = 0.1;
                double d9 = 0.02;
                double d10 = 0.1;
                double d11 = 0.01;
                PolyClassWorld polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue27);
                if (polyClassWorld != null) {
                    v14 = polyClassWorld.tm$12_spawn_particle(string, scriptValue28.asNum(), d6, scriptValue29.asNum(), d7, d8, d9, d10, d11);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue28);
                    arrayList.add(ScriptValue.of((double)d6));
                    arrayList.add(scriptValue29);
                    arrayList.add(ScriptValue.of((double)d7));
                    arrayList.add(ScriptValue.of((double)d8));
                    arrayList.add(ScriptValue.of((double)d9));
                    arrayList.add(ScriptValue.of((double)d10));
                    arrayList.add(ScriptValue.of((double)d11));
                    v14 = PolyDispatch.bootstrapCall("memberCall", "spawn_particle", (ScriptValue)scriptValue27, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v14 = ScriptValue.NULL;
            }
            if (scriptValue27 != ScriptValue.NULL) {
                PolyClassVector polyClassVector;
                PolyClassVector polyClassVector15;
                ScriptValue scriptValue30;
                PolyClassVector polyClassVector16;
                String string = "flame";
                Object object8 = scriptValue26 != ScriptValue.NULL ? ((polyClassVector16 = PolyClassVector.ofGuarded((ScriptValue)scriptValue26)) != null ? polyClassVector16.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue26, (ScriptContext)scriptContext)) : (scriptValue30 = ScriptValue.NULL);
                double d12 = (scriptValue26 != ScriptValue.NULL ? ((polyClassVector15 = PolyClassVector.ofGuarded((ScriptValue)scriptValue26)) != null ? polyClassVector15.tg$15_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue26, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) - 0.3;
                ScriptValue scriptValue31 = scriptValue26 != ScriptValue.NULL ? ((polyClassVector = PolyClassVector.ofGuarded((ScriptValue)scriptValue26)) != null ? polyClassVector.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue26, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d13 = 1.0;
                double d14 = 0.08;
                double d15 = 0.02;
                double d16 = 0.08;
                double d17 = 0.005;
                PolyClassWorld polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue27);
                if (polyClassWorld != null) {
                    v16 = polyClassWorld.tm$12_spawn_particle(string, scriptValue30.asNum(), d12, scriptValue31.asNum(), d13, d14, d15, d16, d17);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue30);
                    arrayList.add(ScriptValue.of((double)d12));
                    arrayList.add(scriptValue31);
                    arrayList.add(ScriptValue.of((double)d13));
                    arrayList.add(ScriptValue.of((double)d14));
                    arrayList.add(ScriptValue.of((double)d15));
                    arrayList.add(ScriptValue.of((double)d16));
                    arrayList.add(ScriptValue.of((double)d17));
                    v16 = PolyDispatch.bootstrapCall("memberCall", "spawn_particle", (ScriptValue)scriptValue27, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v16 = ScriptValue.NULL;
            }
            if (scriptValue27 != ScriptValue.NULL) {
                PolyClassVector polyClassVector;
                ScriptValue scriptValue32;
                PolyClassVector polyClassVector17;
                ScriptValue scriptValue33;
                PolyClassVector polyClassVector18;
                Object object9 = scriptValue26 != ScriptValue.NULL ? ((polyClassVector18 = PolyClassVector.ofGuarded((ScriptValue)scriptValue26)) != null ? polyClassVector18.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue26, (ScriptContext)scriptContext)) : (scriptValue33 = ScriptValue.NULL);
                Object object10 = scriptValue26 != ScriptValue.NULL ? ((polyClassVector17 = PolyClassVector.ofGuarded((ScriptValue)scriptValue26)) != null ? polyClassVector17.pg$14_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue26, (ScriptContext)scriptContext)) : (scriptValue32 = ScriptValue.NULL);
                ScriptValue scriptValue34 = scriptValue26 != ScriptValue.NULL ? ((polyClassVector = PolyClassVector.ofGuarded((ScriptValue)scriptValue26)) != null ? polyClassVector.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue26, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                String string = "minecraft:entity.firework_rocket.launch";
                double d18 = 0.1;
                double d19 = 1.8;
                PolyClassWorld polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue27);
                v19 = polyClassWorld != null ? polyClassWorld.tm$16_play_sound(scriptValue33.asNum(), scriptValue32.asNum(), scriptValue34.asNum(), string, d18, d19) : PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue27, (ScriptValue)scriptValue33, (ScriptValue)scriptValue32, (ScriptValue)scriptValue34, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d18), (ScriptValue)ScriptValue.of((double)d19), (ScriptContext)scriptContext);
            } else {
                v19 = ScriptValue.NULL;
            }
            if (!(d <= 0.0)) break block36;
            if (scriptValue24 != ScriptValue.NULL) {
                String string = "slow_falling";
                double d20 = 60.0;
                double d21 = 0.0;
                PolyClassEntity polyClassEntity2 = PolyClassEntity.ofGuarded((ScriptValue)scriptValue24);
                v20 = polyClassEntity2 != null ? ScriptValue.of((boolean)polyClassEntity2.tm$2_add_potion_effect(string, d20, d21)) : PolyDispatch.bootstrapCall("memberCall", "add_potion_effect", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d20), (ScriptValue)ScriptValue.of((double)d21), (ScriptContext)scriptContext);
            } else {
                v20 = ScriptValue.NULL;
            }
            ScriptValue scriptValue35 = scriptContext.getClassOrVar("Player");
            if (scriptValue35 != ScriptValue.NULL) {
                String string = "<red>Jetpack out of gas.";
                PolyClassPlayer_v2 polyClassPlayer_v29 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue35);
                v21 = polyClassPlayer_v29 != null ? ScriptValue.of((boolean)polyClassPlayer_v29.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue35, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v21 = ScriptValue.NULL;
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
