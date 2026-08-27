/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
        block14: {
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
                    var9_8 = new PolyClassMachine(var8_7);
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
            if (var12_11 == null) break block14;
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
                    var29_28 = new ArrayList<ScriptValue>();
                    var29_28.add(var1_1.getClassOrVar("i"));
                    var30_29 = var1_1.getClassOrVar("Machine");
                    var32_31 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(var30_29 != ScriptValue.NULL ? ((var31_30 = PolyClassMachine.ofGuarded((ScriptValue)var30_29)) != null ? var31_30.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var30_29, (ScriptContext)var1_1)) : ScriptValue.NULL), var29_28, (ScriptContext)var1_1);
                    var0.val("item", (ScriptValue)var32_31);
                    var33_32 = new ArrayList<CallSite>();
                    var33_32.add(var32_31);
                    if (!((ScriptFormula.callBuiltin((String)"is_empty", var33_32, (ScriptContext)var1_1).asBool() ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)((var34_33 = var1_1.getClassOrVar("item")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var34_33, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var22_21) != false)) continue;
                    var35_34 = var1_1.getClassOrVar("Machine");
                    if (var35_34 != ScriptValue.NULL) {
                        var36_35 = var1_1.getClassOrVar("animal");
                        var37_36 = var1_1.getClassOrVar("i");
                        if (var35_34 instanceof ScriptValue.Obj && (var39_38 = (var38_37 = (ScriptValue.Obj)var35_34).instance()) != null && !(var39_38 instanceof PolyClass) && var38_37.typeName().equals("Machine")) {
                            var40_39 = new PolyClassMachine(var39_38);
                            v4 /* !! */  = ScriptValue.of((boolean)var40_39.tm$42_use_item_on_entity(var36_35, var37_36.asNum()));
                        } else {
                            var41_40 = new ArrayList<ScriptValue>();
                            var41_40.add(var36_35);
                            var41_40.add(var37_36);
                            v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "use_item_on_entity", (ScriptValue)var35_34, var41_40, (ScriptContext)var1_1);
                        }
                    } else {
                        v4 /* !! */  = ScriptValue.NULL;
                    }
                    var42_41 = true;
                    var43_42 = ScriptValue.of((boolean)true);
                    var0.val("fed", var43_42);
                }
            }
        }
        Breeder.FILE_SCOPE = var0.build();
    }
}
