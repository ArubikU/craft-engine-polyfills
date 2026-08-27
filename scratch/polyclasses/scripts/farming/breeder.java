/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
        block8: {
            var1_1 = var0.peek();
            var2_2 = new ArrayList<ScriptValue>();
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:cow"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:wheat"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:sheep"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:wheat"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:goat"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:wheat"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:horse"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:golden_apple"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:donkey"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:golden_apple"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:pig"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:carrot"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:rabbit"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:dandelion"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:chicken"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:wheat_seeds"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:turtle"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:seagrass"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:panda"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:bamboo"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:fox"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:sweet_berries"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:bee"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:poppy"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:strider"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:warped_fungus"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:hoglin"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:crimson_fungus"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:axolotl"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:tropical_fish_bucket"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:camel"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:cactus"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:sniffer"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Breeder.class, "minecraft:torchflower_seeds"));
            var3_3 = ScriptFormula.callBuiltin((String)"make_map", var2_2, (ScriptContext)var1_1);
            var0.val("BREEDING_FOODS", var3_3);
            var4_4 = var1_1.getClassOrVar("Machine");
            if (var4_4 != ScriptValue.NULL) {
                var5_5 = 5.0;
                var7_6 = PolyClassMachine_v3.ofGuarded((ScriptValue)var4_4);
                v0 /* !! */  = var7_6 != null ? var7_6.tm$94_nearby_entities(var5_5) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var4_4, (ScriptValue)ScriptValue.of((double)var5_5), (ScriptContext)var1_1);
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var8_7 = v0 /* !! */ ;
            var0.val("animals", var8_7);
            var9_8 = ScriptProgram.elementsOf((ScriptValue)var8_7);
            var12_9 = var1_1.getClassOrVar("item");
            var13_10 = var1_1.getClassOrVar("fed");
            var14_11 = var1_1.getClassOrVar("i");
            var15_12 = var1_1.getClassOrVar("food_id");
            if (var9_8 == null) break block8;
            for (ScriptValue var11_14 : var9_8) {
                var0.val("animal", var11_14);
                if (!((var11_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var11_14, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false && (var11_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_animal", (ScriptValue)var11_14, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false)) ** GOTO lbl-1000
                v1 /* !! */  = var11_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "age", (ScriptValue)var11_14, (ScriptContext)var1_1) : ScriptValue.NULL;
                if (v1 /* !! */ .asNum() >= 0.0) {
                    v2 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v2 = false;
                }
                if (!(v2 != false && ScriptFormula.valuesEqual((ScriptValue)(var11_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "in_love_time", (ScriptValue)var11_14, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Breeder.class, 0.0))) != false)) continue;
                var16_15 = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var3_3, (ScriptValue)(var11_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)var11_14, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var1_1.getClassOrVar("null"), (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("food_id", var16_15);
                var15_12 = var16_15;
                if (!(ScriptFormula.valuesEqual((ScriptValue)var15_12, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                var17_16 = false;
                var18_17 = ScriptValue.of((boolean)false);
                var0.val("fed", var18_17);
                var13_10 = var18_17;
                var19_18 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Breeder.class, 9.0)), (ScriptContext)var1_1));
                if (var19_18 == null) continue;
                for (ScriptValue var21_20 : var19_18) {
                    var0.val("i", var21_20);
                    if (!(var1_1.getBool("fed") ^ true)) continue;
                    var22_21 = var4_4 != ScriptValue.NULL ? ((var23_22 = PolyClassMachine_v3.ofGuarded((ScriptValue)var4_4)) != null ? var23_22.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var4_4, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var24_23 = var21_20;
                    var25_24 = PolyClassContainer.ofGuarded((ScriptValue)var22_21);
                    var26_25 /* !! */  = var25_24 != null ? var25_24.tm$0_get_item(var24_23.asNum()) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)var22_21, (ScriptValue)var24_23, (ScriptContext)var1_1);
                    var0.val("item", var26_25 /* !! */ );
                    var12_9 = var26_25 /* !! */ ;
                    if (!((ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var12_9, (ScriptContext)var1_1).asBool() ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)(var12_9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var12_9, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var15_12) != false)) continue;
                    if (var4_4 != ScriptValue.NULL) {
                        var27_26 = var11_14;
                        var28_27 = var21_20;
                        var29_28 = PolyClassMachine_v3.ofGuarded((ScriptValue)var4_4);
                        v3 /* !! */  = var29_28 != null ? ScriptValue.of((boolean)var29_28.tm$42_use_item_on_entity(var27_26, var28_27.asNum())) : PolyDispatch.bootstrapCall("memberCall", "use_item_on_entity", (ScriptValue)var4_4, (ScriptValue)var27_26, (ScriptValue)var28_27, (ScriptContext)var1_1);
                    } else {
                        v3 /* !! */  = ScriptValue.NULL;
                    }
                    var30_29 = true;
                    var31_30 = ScriptValue.of((boolean)true);
                    var0.val("fed", var31_30);
                    var13_10 = var31_30;
                }
            }
        }
        Breeder.FILE_SCOPE = var0.build();
    }
}
