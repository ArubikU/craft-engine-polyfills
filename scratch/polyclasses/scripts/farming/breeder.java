/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class Breeder {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block16: {
            var1_1 = var0.peek();
            var2_2 = new ArrayList<ScriptValue>();
            var2_2.add(ScriptValue.of((String)"minecraft:cow"));
            var2_2.add(ScriptValue.of((String)"minecraft:wheat"));
            var2_2.add(ScriptValue.of((String)"minecraft:sheep"));
            var2_2.add(ScriptValue.of((String)"minecraft:wheat"));
            var2_2.add(ScriptValue.of((String)"minecraft:goat"));
            var2_2.add(ScriptValue.of((String)"minecraft:wheat"));
            var2_2.add(ScriptValue.of((String)"minecraft:horse"));
            var2_2.add(ScriptValue.of((String)"minecraft:golden_apple"));
            var2_2.add(ScriptValue.of((String)"minecraft:donkey"));
            var2_2.add(ScriptValue.of((String)"minecraft:golden_apple"));
            var2_2.add(ScriptValue.of((String)"minecraft:pig"));
            var2_2.add(ScriptValue.of((String)"minecraft:carrot"));
            var2_2.add(ScriptValue.of((String)"minecraft:rabbit"));
            var2_2.add(ScriptValue.of((String)"minecraft:dandelion"));
            var2_2.add(ScriptValue.of((String)"minecraft:chicken"));
            var2_2.add(ScriptValue.of((String)"minecraft:wheat_seeds"));
            var2_2.add(ScriptValue.of((String)"minecraft:turtle"));
            var2_2.add(ScriptValue.of((String)"minecraft:seagrass"));
            var2_2.add(ScriptValue.of((String)"minecraft:panda"));
            var2_2.add(ScriptValue.of((String)"minecraft:bamboo"));
            var2_2.add(ScriptValue.of((String)"minecraft:fox"));
            var2_2.add(ScriptValue.of((String)"minecraft:sweet_berries"));
            var2_2.add(ScriptValue.of((String)"minecraft:bee"));
            var2_2.add(ScriptValue.of((String)"minecraft:poppy"));
            var2_2.add(ScriptValue.of((String)"minecraft:strider"));
            var2_2.add(ScriptValue.of((String)"minecraft:warped_fungus"));
            var2_2.add(ScriptValue.of((String)"minecraft:hoglin"));
            var2_2.add(ScriptValue.of((String)"minecraft:crimson_fungus"));
            var2_2.add(ScriptValue.of((String)"minecraft:axolotl"));
            var2_2.add(ScriptValue.of((String)"minecraft:tropical_fish_bucket"));
            var2_2.add(ScriptValue.of((String)"minecraft:camel"));
            var2_2.add(ScriptValue.of((String)"minecraft:cactus"));
            var2_2.add(ScriptValue.of((String)"minecraft:sniffer"));
            var2_2.add(ScriptValue.of((String)"minecraft:torchflower_seeds"));
            var3_3 = ScriptFormula.callBuiltin((String)"make_map", var2_2, (ScriptContext)var1_1);
            var0.val("BREEDING_FOODS", var3_3);
            var4_4 = var1_1.getClassOrVar("Machine");
            if (var4_4 != ScriptValue.NULL) {
                var5_5 = 5.0;
                if (var4_4 instanceof ScriptValue.Obj && (var8_7 = (var7_6 = (ScriptValue.Obj)var4_4).instance()) != null && !(var8_7 instanceof PolyClass) && var7_6.typeName().equals("Machine")) {
                    var9_8 = new PolyClassMachine_v4(var8_7);
                    v0 /* !! */  = var9_8.tm$94_nearby_entities(var5_5);
                } else {
                    var10_9 = new ArrayList<ScriptValue>();
                    var10_9.add(ScriptValue.of((double)var5_5));
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var4_4, var10_9, (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var11_10 = v0 /* !! */ ;
            var0.val("animals", var11_10);
            var12_11 = ScriptProgram.elementsOf((ScriptValue)var11_10);
            if (var12_11 == null) break block16;
            for (ScriptValue var14_13 : var12_11) {
                var0.val("animal", var14_13);
                var15_14 = var1_1.getClassOrVar("animal");
                if (!((var15_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var15_14, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false && ((var16_15 = var1_1.getClassOrVar("animal")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_animal", (ScriptValue)var16_15, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false)) ** GOTO lbl-1000
                var17_16 = var1_1.getClassOrVar("animal");
                v1 /* !! */  = var17_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "age", (ScriptValue)var17_16, (ScriptContext)var1_1) : ScriptValue.NULL;
                if (v1 /* !! */ .asNum() >= 0.0) {
                    v2 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v2 = false;
                }
                if (!(v2 != false && ScriptFormula.valuesEqual((ScriptValue)((var18_17 = var1_1.getClassOrVar("animal")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "in_love_time", (ScriptValue)var18_17, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.0)) != false)) continue;
                var19_18 = var1_1.getClassOrVar("BREEDING_FOODS");
                if (var19_18 != ScriptValue.NULL) {
                    var20_19 = new ArrayList<ScriptValue>();
                    var21_20 = var1_1.getClassOrVar("animal");
                    var20_19.add((ScriptValue)(var21_20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)var21_20, (ScriptContext)var1_1) : ScriptValue.NULL));
                    var20_19.add(var1_1.getClassOrVar("null"));
                    v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var19_18, var20_19, (ScriptContext)var1_1);
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                var22_21 = v3 /* !! */ ;
                var0.val("food_id", var22_21);
                if (!(ScriptFormula.valuesEqual((ScriptValue)var22_21, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                var23_22 = false;
                var24_23 = ScriptValue.of((boolean)false);
                var0.val("fed", var24_23);
                var28_27 = new ArrayList<ScriptValue>();
                var28_27.add(ScriptValue.of((double)9.0));
                var25_24 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", var28_27, (ScriptContext)var1_1));
                if (var25_24 == null) continue;
                for (ScriptValue var27_26 : var25_24) {
                    var0.val("i", var27_26);
                    if (!(var1_1.getBool("fed") ^ true)) continue;
                    var30_29 = var1_1.getClassOrVar("Machine");
                    var29_28 = var30_29 != ScriptValue.NULL ? ((var31_30 = PolyClassMachine_v4.ofGuarded((ScriptValue)var30_29)) != null ? var31_30.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var30_29, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var32_31 = var1_1.getClassOrVar("i");
                    if (var29_28 instanceof ScriptValue.Obj && (var34_33 = (var33_32 = (ScriptValue.Obj)var29_28).instance()) != null && !(var34_33 instanceof PolyClass) && var33_32.typeName().equals("Container")) {
                        var35_34 = new PolyClassContainer(var34_33);
                        v4 = var35_34.tm$0_get_item(var32_31.asNum());
                    } else {
                        var36_35 = new ArrayList<ScriptValue>();
                        var36_35.add(var32_31);
                        v4 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)var29_28, var36_35, (ScriptContext)var1_1);
                    }
                    var37_36 = v4;
                    var0.val("item", (ScriptValue)var37_36);
                    var38_37 = new ArrayList<CallSite>();
                    var38_37.add(var37_36);
                    if (!((ScriptFormula.callBuiltin((String)"is_empty", var38_37, (ScriptContext)var1_1).asBool() ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)((var39_38 = var1_1.getClassOrVar("item")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var39_38, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var22_21) != false)) continue;
                    var40_39 = var1_1.getClassOrVar("Machine");
                    if (var40_39 != ScriptValue.NULL) {
                        var41_40 = var1_1.getClassOrVar("animal");
                        var42_41 = var1_1.getClassOrVar("i");
                        if (var40_39 instanceof ScriptValue.Obj && (var44_43 = (var43_42 = (ScriptValue.Obj)var40_39).instance()) != null && !(var44_43 instanceof PolyClass) && var43_42.typeName().equals("Machine")) {
                            var45_44 = new PolyClassMachine_v4(var44_43);
                            v5 /* !! */  = ScriptValue.of((boolean)var45_44.tm$42_use_item_on_entity(var41_40, var42_41.asNum()));
                        } else {
                            var46_45 = new ArrayList<ScriptValue>();
                            var46_45.add(var41_40);
                            var46_45.add(var42_41);
                            v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "use_item_on_entity", (ScriptValue)var40_39, var46_45, (ScriptContext)var1_1);
                        }
                    } else {
                        v5 /* !! */  = ScriptValue.NULL;
                    }
                    var47_46 = true;
                    var48_47 = ScriptValue.of((boolean)true);
                    var0.val("fed", var48_47);
                }
            }
        }
        Breeder.FILE_SCOPE = var0.build();
    }
}
