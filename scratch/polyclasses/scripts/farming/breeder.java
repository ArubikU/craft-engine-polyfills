/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
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
                    var9_8 = new PolyClassMachine_v2(var8_7);
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
            if (var11_10 == null) break block14;
            for (ScriptValue var13_12 : var11_10) {
                var0.val("animal", var13_12);
                var14_13 = var1_1.getClassOrVar("animal");
                if (!((var14_13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var14_13, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false && ((var15_14 = var1_1.getClassOrVar("animal")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_animal", (ScriptValue)var15_14, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false)) ** GOTO lbl-1000
                var16_15 = var1_1.getClassOrVar("animal");
                v1 /* !! */  = var16_15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "age", (ScriptValue)var16_15, (ScriptContext)var1_1) : ScriptValue.NULL;
                if (v1 /* !! */ .asNum() >= 0.0) {
                    v2 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v2 = false;
                }
                if (!(v2 != false && ScriptFormula.valuesEqual((ScriptValue)((var17_16 = var1_1.getClassOrVar("animal")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "in_love_time", (ScriptValue)var17_16, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Breeder.class, 0.0))) != false)) continue;
                var18_17 = var1_1.getClassOrVar("BREEDING_FOODS");
                var20_19 = var18_17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var18_17, (ScriptValue)((var19_18 = var1_1.getClassOrVar("animal")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)var19_18, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var1_1.getClassOrVar("null"), (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("food_id", var20_19);
                if (!(ScriptFormula.valuesEqual((ScriptValue)var20_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                var21_20 = false;
                var22_21 = ScriptValue.of((boolean)false);
                var0.val("fed", var22_21);
                var26_25 = new ArrayList<ScriptValue>();
                var26_25.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Breeder.class, 9.0));
                var23_22 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", var26_25, (ScriptContext)var1_1));
                if (var23_22 == null) continue;
                for (ScriptValue var25_24 : var23_22) {
                    var0.val("i", var25_24);
                    if (!(var1_1.getBool("fed") ^ true)) continue;
                    var28_27 = var1_1.getClassOrVar("Machine");
                    var27_26 = var28_27 != ScriptValue.NULL ? ((var29_28 = PolyClassMachine_v2.ofGuarded((ScriptValue)var28_27)) != null ? var29_28.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var28_27, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var30_29 = var1_1.getClassOrVar("i");
                    if (var27_26 instanceof ScriptValue.Obj && (var32_31 = (var31_30 = (ScriptValue.Obj)var27_26).instance()) != null && !(var32_31 instanceof PolyClass) && var31_30.typeName().equals("Container")) {
                        var33_32 = new PolyClassContainer(var32_31);
                        v3 = var33_32.tm$0_get_item(var30_29.asNum());
                    } else {
                        v3 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)var27_26, (ScriptValue)var30_29, (ScriptContext)var1_1);
                    }
                    var34_33 = v3;
                    var0.val("item", (ScriptValue)var34_33);
                    var35_34 = new ArrayList<CallSite>();
                    var35_34.add(var34_33);
                    if (!((ScriptFormula.callBuiltin((String)"is_empty", var35_34, (ScriptContext)var1_1).asBool() ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)((var36_35 = var1_1.getClassOrVar("item")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var36_35, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var20_19) != false)) continue;
                    var37_36 = var1_1.getClassOrVar("Machine");
                    if (var37_36 != ScriptValue.NULL) {
                        var38_37 = var1_1.getClassOrVar("animal");
                        var39_38 = var1_1.getClassOrVar("i");
                        if (var37_36 instanceof ScriptValue.Obj && (var41_40 = (var40_39 = (ScriptValue.Obj)var37_36).instance()) != null && !(var41_40 instanceof PolyClass) && var40_39.typeName().equals("Machine")) {
                            var42_41 = new PolyClassMachine_v2(var41_40);
                            v4 /* !! */  = ScriptValue.of((boolean)var42_41.tm$42_use_item_on_entity(var38_37, var39_38.asNum()));
                        } else {
                            v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "use_item_on_entity", (ScriptValue)var37_36, (ScriptValue)var38_37, (ScriptValue)var39_38, (ScriptContext)var1_1);
                        }
                    } else {
                        v4 /* !! */  = ScriptValue.NULL;
                    }
                    var43_42 = true;
                    var44_43 = ScriptValue.of((boolean)true);
                    var0.val("fed", var44_43);
                }
            }
        }
        Breeder.FILE_SCOPE = var0.build();
    }
}
