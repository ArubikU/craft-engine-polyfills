/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
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
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine;
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
        block14: {
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
                if (var4_4 instanceof ScriptValue.Obj && (var8_7 = (var7_6 = (ScriptValue.Obj)var4_4).instance()) != null && !(var8_7 instanceof PolyClass) && var7_6.typeName().equals("Machine")) {
                    var9_8 = new PolyClassMachine(var8_7);
                    v0 /* !! */  = var9_8.tm$94_nearby_entities(var5_5);
                } else {
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var4_4, (ScriptValue)ScriptValue.of((double)var5_5), (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var10_9 = v0 /* !! */ ;
            var0.val("animals", var10_9);
            var11_10 = ScriptProgram.elementsOf((ScriptValue)var10_9);
            var14_11 /* !! */  = var1_1.getClassOrVar("item");
            var15_12 = var1_1.getClassOrVar("fed");
            var16_13 = var1_1.getClassOrVar("i");
            var17_14 = var1_1.getClassOrVar("food_id");
            if (var11_10 == null) break block14;
            for (ScriptValue var13_16 : var11_10) {
                var0.val("animal", var13_16);
                if (!((var13_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var13_16, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false && (var13_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_animal", (ScriptValue)var13_16, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false)) ** GOTO lbl-1000
                v1 /* !! */  = var13_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "age", (ScriptValue)var13_16, (ScriptContext)var1_1) : ScriptValue.NULL;
                if (v1 /* !! */ .asNum() >= 0.0) {
                    v2 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v2 = false;
                }
                if (!(v2 != false && ScriptFormula.valuesEqual((ScriptValue)(var13_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "in_love_time", (ScriptValue)var13_16, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Breeder.class, 0.0))) != false)) continue;
                var18_17 = var1_1.getClassOrVar("BREEDING_FOODS");
                var19_18 = var18_17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var18_17, (ScriptValue)(var13_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)var13_16, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var1_1.getClassOrVar("null"), (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("food_id", var19_18);
                var17_14 = var19_18;
                if (!(ScriptFormula.valuesEqual((ScriptValue)var17_14, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                var20_19 = false;
                var21_20 = ScriptValue.of((boolean)false);
                var0.val("fed", var21_20);
                var15_12 = var21_20;
                var22_21 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Breeder.class, 9.0)), (ScriptContext)var1_1));
                if (var22_21 == null) continue;
                for (ScriptValue var24_23 : var22_21) {
                    var0.val("i", var24_23);
                    if (!(var1_1.getBool("fed") ^ true)) continue;
                    var26_25 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var25_24 = var26_25 != null ? var26_25.pg$120_container() : ((var27_26 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var27_26, (ScriptContext)var1_1) : ScriptValue.NULL);
                    var28_27 = var24_23;
                    if (var25_24 instanceof ScriptValue.Obj && (var30_29 = (var29_28 = (ScriptValue.Obj)var25_24).instance()) != null && !(var30_29 instanceof PolyClass) && var29_28.typeName().equals("Container")) {
                        var31_30 = new PolyClassContainer(var30_29);
                        v3 = var31_30.tm$0_get_item(var28_27.asNum());
                    } else {
                        v3 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)var25_24, (ScriptValue)var28_27, (ScriptContext)var1_1);
                    }
                    var32_31 = v3;
                    var0.val("item", (ScriptValue)var32_31);
                    var14_11 /* !! */  = var32_31;
                    if (!((ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var14_11 /* !! */ , (ScriptContext)var1_1).asBool() ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)(var14_11 /* !! */  != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var14_11 /* !! */ , (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var17_14) != false)) continue;
                    var33_32 = var1_1.getClassOrVar("Machine");
                    if (var33_32 != ScriptValue.NULL) {
                        var34_33 = var13_16;
                        var35_34 = var24_23;
                        if (var33_32 instanceof ScriptValue.Obj && (var37_36 = (var36_35 = (ScriptValue.Obj)var33_32).instance()) != null && !(var37_36 instanceof PolyClass) && var36_35.typeName().equals("Machine")) {
                            var38_37 = new PolyClassMachine(var37_36);
                            v4 /* !! */  = ScriptValue.of((boolean)var38_37.tm$42_use_item_on_entity(var34_33, var35_34.asNum()));
                        } else {
                            v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "use_item_on_entity", (ScriptValue)var33_32, (ScriptValue)var34_33, (ScriptValue)var35_34, (ScriptContext)var1_1);
                        }
                    } else {
                        v4 /* !! */  = ScriptValue.NULL;
                    }
                    var39_38 = true;
                    var40_39 = ScriptValue.of((boolean)true);
                    var0.val("fed", var40_39);
                    var15_12 = var40_39;
                }
            }
        }
        Breeder.FILE_SCOPE = var0.build();
    }
}
