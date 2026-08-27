/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.block_interaction;

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
public final class RecipeInfo {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _recipe(ScriptContext.Builder builder) {
        ScriptContext scriptContext;
        block0: {
            PolyClassMachine_v3 polyClassMachine_v3;
            PolyClassMachine_v3 polyClassMachine_v32;
            scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v32.pg$161_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("recipe", scriptValue2);
            if (!ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null"))) break block0;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v3.pg$185_matching_recipe() : PolyDispatch.bootstrapGet("memberGet", "matching_recipe", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("recipe", scriptValue4);
        }
        return scriptContext.getClassOrVar("recipe");
    }

    public static ScriptValue _bar(ScriptContext.Builder builder) {
        double d;
        ScriptContext scriptContext = builder.peek();
        double d2 = 100.0;
        if (100.0 == 0.0) {
            d = 0.0;
        } else {
            double d3 = scriptContext.getNum("width");
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("percent"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 100.0));
            d = d3 * ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext).asNum() / d2;
        }
        double d4 = Math.floor(d);
        ScriptValue scriptValue = ScriptValue.of((double)d4);
        builder.val("filled", scriptValue);
        ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "");
        builder.val("bar", scriptValue2);
        double d5 = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("i", scriptValue3);
        for (int i = 0; i < 1000; ++i) {
            if (!(scriptContext.getNum("i") < scriptContext.getNum("width"))) break;
            ScriptValue scriptValue4 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("bar"), (ScriptValue)(scriptContext.getNum("i") < d4 ? ScriptValue.of((String)(scriptContext.getStr("full_color") + "\u2588")) : ScriptValue.of((String)(scriptContext.getStr("empty_color") + "\u2591"))));
            builder.val("bar", scriptValue4);
            ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 1.0)));
            builder.val("i", scriptValue5);
        }
        return scriptContext.getClassOrVar("bar");
    }

    public static ScriptValue _div(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<dark_gray><st>                              ");
    }

    public static ScriptValue item(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = RecipeInfo._recipe(builder2);
        builder.val("recipe", scriptValue);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "minecraft:barrier");
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("recipe");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)object, (ScriptContext)scriptContext).asNum() > 0.0) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("recipe");
            arrayList.add((ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL));
            ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"first", arrayList, (ScriptContext)scriptContext);
            builder.val("first_item", scriptValue4);
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptValue4);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool() ^ true) {
                return scriptValue4;
            }
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "minecraft:book");
    }

    public static ScriptValue name(ScriptContext.Builder builder) {
        PolyClassMachine_v3 polyClassMachine_v3;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = RecipeInfo._recipe(builder2);
        builder.val("recipe", scriptValue2);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<red><b>No Recipe");
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("recipe");
        if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 0.0)))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<gold><b>\u2699 Recipe");
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("recipe");
        arrayList.add((ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"first", arrayList, (ScriptContext)scriptContext);
        builder.val("first_item", scriptValue5);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptValue5);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<gold><b>\u2699 Recipe");
        }
        ScriptValue scriptValue6 = ScriptValue.of((String)("<gold><b>" + ((scriptValue = scriptContext.getClassOrVar("first_item")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()));
        builder.val("label", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        Object object = scriptValue7 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassMachine_v3.pg$161_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            PolyClassMachine_v3 polyClassMachine_v32;
            ScriptValue scriptValue8;
            return ScriptValue.of((String)(scriptValue6.asStr() + " <yellow>" + ScriptFormula.numToStr((double)Math.floor((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassMachine_v32.tg$148_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum())) + "%"));
        }
        return ScriptValue.of((String)(scriptValue6.asStr() + " <dark_gray>(idle)"));
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue lore(ScriptContext.Builder var0) {
        block24: {
            var1_1 = var0.peek();
            var2_2 = ScriptContext.builder().copyFrom(var1_1);
            var3_3 = RecipeInfo._recipe(var2_2);
            var0.val("recipe", var3_3);
            if (ScriptFormula.valuesEqual((ScriptValue)var3_3, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var4_4 = new ArrayList<ScriptValue>();
                var4_4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<gray>No matching recipe for what's inside"));
                return new ScriptValue.Array(var4_4);
            }
            var5_5 = var1_1.getClassOrVar("recipe");
            var6_6 = Math.max(1.0, (var5_5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "processing_time", (ScriptValue)var5_5, (ScriptContext)var1_1) : ScriptValue.NULL).asNum());
            var8_7 = ScriptValue.of((double)var6_6);
            var0.val("eff_ticks", var8_7);
            var9_8 = var6_6;
            var11_9 = var9_8 == 0.0 ? 0.0 : 1200.0 / var9_8;
            var13_10 = ScriptValue.of((double)var11_9);
            var0.val("crafts_per_min", var13_10);
            var14_11 = var1_1.getClassOrVar("Machine");
            var16_13 = ScriptFormula.valuesEqual((ScriptValue)(var14_11 != ScriptValue.NULL ? ((var15_12 = PolyClassMachine_v3.ofGuarded((ScriptValue)var14_11)) != null ? var15_12.pg$161_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)var14_11, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)var1_1.getClassOrVar("null")) ^ true;
            var17_14 = ScriptValue.of((boolean)var16_13);
            var0.val("working", var17_14);
            var18_15 = new ArrayList<E>();
            var19_16 = new ScriptValue.Array(var18_15);
            var0.val("lines", (ScriptValue)var19_16);
            if (var16_13) {
                var20_17 = new ArrayList<ScriptValue>();
                var20_17.add(var1_1.getClassOrVar("lines"));
                v0 = new StringBuilder();
                var21_18 = ScriptContext.builder().copyFrom(var1_1);
                var22_19 = var1_1.getClassOrVar("Machine");
                var21_18.val("percent", (ScriptValue)(var22_19 != ScriptValue.NULL ? ((var23_20 = PolyClassMachine_v3.ofGuarded((ScriptValue)var22_19)) != null ? var23_20.pg$147_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)var22_19, (ScriptContext)var1_1)) : ScriptValue.NULL));
                var21_18.val("width",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 14.0));
                var21_18.val("full_color",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<green>"));
                var21_18.val("empty_color",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<dark_gray>"));
                var24_21 = var1_1.getClassOrVar("Machine");
                var20_17.add(ScriptValue.of((String)v0.append(RecipeInfo._bar(var21_18).asStr()).append(" <white>").append(ScriptFormula.numToStr((double)Math.floor(var24_21 != ScriptValue.NULL ? ((var25_22 = PolyClassMachine_v3.ofGuarded((ScriptValue)var24_21)) != null ? var25_22.tg$148_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)var24_21, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum()))).append("%").toString()));
                var26_23 = ScriptFormula.callBuiltin((String)"push", var20_17, (ScriptContext)var1_1);
                var0.val("lines", var26_23);
            } else {
                var27_24 = new ArrayList<ScriptValue>();
                var27_24.add(var1_1.getClassOrVar("lines"));
                var27_24.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<gray><i>Waiting to start\u2026"));
                var28_25 = ScriptFormula.callBuiltin((String)"push", var27_24, (ScriptContext)var1_1);
                var0.val("lines", var28_25);
            }
            var29_26 = new ArrayList<ScriptValue>();
            var29_26.add(var1_1.getClassOrVar("lines"));
            var30_27 = ScriptContext.builder().copyFrom(var1_1);
            var29_26.add(RecipeInfo._div(var30_27));
            var31_28 = ScriptFormula.callBuiltin((String)"push", var29_26, (ScriptContext)var1_1);
            var0.val("lines", var31_28);
            var32_29 = new ArrayList<ScriptValue>();
            var32_29.add(var1_1.getClassOrVar("lines"));
            var32_29.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<white><b>OUTPUT"));
            var33_30 = ScriptFormula.callBuiltin((String)"push", var32_29, (ScriptContext)var1_1);
            var0.val("lines", var33_30);
            var34_31 = 0.0;
            var36_32 = ScriptValue.of((double)0.0);
            var0.val("out_i", var36_32);
            var40_33 = var1_1.getClassOrVar("recipe");
            var37_34 = ScriptProgram.elementsOf((ScriptValue)(var40_33 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)var40_33, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (var37_34 != null) {
                for (ScriptValue var39_36 : var37_34) {
                    var0.val("out_item", var39_36);
                    var41_37 = new ArrayList<ScriptValue>();
                    var41_37.add(var1_1.getClassOrVar("out_item"));
                    if (ScriptFormula.callBuiltin((String)"is_empty", var41_37, (ScriptContext)var1_1).asBool() ^ true) {
                        var42_38 = var1_1.getClassOrVar("recipe");
                        var43_39 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)(var42_38 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "output_chances", (ScriptValue)var42_38, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var1_1.getClassOrVar("out_i"), (ScriptContext)var1_1);
                        var0.val("chance", (ScriptValue)var43_39);
                        var44_40 = 10.0;
                        if (10.0 == 0.0) {
                            v1 = 0.0;
                        } else {
                            var46_41 = new ArrayList<ScriptValue>();
                            var47_42 = var1_1.getClassOrVar("out_item");
                            var46_41.add(ScriptValue.of((double)(var11_9 * (var47_42 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var47_42, (ScriptContext)var1_1) : ScriptValue.NULL).asNum() * var43_39.asNum() * 10.0)));
                            v1 = ScriptFormula.callBuiltin((String)"round", var46_41, (ScriptContext)var1_1).asNum() / var44_40;
                        }
                        var48_43 = v1;
                        var50_44 = ScriptValue.of((double)v1);
                        var0.val("per_min", var50_44);
                        var51_45 = var1_1.getClassOrVar("out_item");
                        var52_46 = var1_1.getClassOrVar("out_item");
                        var53_47 = ScriptValue.of((String)("<green>\u27a4 <white>" + (var51_45 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var51_45, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " <gray>x" + (var52_46 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var52_46, (ScriptContext)var1_1) : ScriptValue.NULL).asStr()));
                        var0.val("line", var53_47);
                        if (var43_39.asNum() < 1.0) {
                            var54_48 = ScriptValue.of((String)(var1_1.getStr("line") + " <gold>(" + ScriptFormula.numToStr((double)Math.floor(var43_39.asNum() * 100.0)) + "%)"));
                            var0.val("line", var54_48);
                        }
                        var55_49 = new ArrayList<ScriptValue>();
                        var55_49.add(var1_1.getClassOrVar("lines"));
                        var55_49.add(var1_1.getClassOrVar("line"));
                        var56_50 = ScriptFormula.callBuiltin((String)"push", var55_49, (ScriptContext)var1_1);
                        var0.val("lines", var56_50);
                        var57_51 = new ArrayList<ScriptValue>();
                        var57_51.add(var1_1.getClassOrVar("lines"));
                        var57_51.add(ScriptValue.of((String)("<dark_gray>   " + ScriptFormula.numToStr((double)var48_43) + "/min")));
                        var58_52 = ScriptFormula.callBuiltin((String)"push", var57_51, (ScriptContext)var1_1);
                        var0.val("lines", var58_52);
                    }
                    var59_53 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("out_i"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 1.0)));
                    var0.val("out_i", var59_53);
                }
            }
            v2 /* !! */  = (var60_54 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var60_54, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)v2 /* !! */ , (ScriptContext)var1_1).asNum() > 0.0) {
                var61_55 = new ArrayList<ScriptValue>();
                var61_55.add(var1_1.getClassOrVar("lines"));
                var62_56 = ScriptContext.builder().copyFrom(var1_1);
                var61_55.add(RecipeInfo._div(var62_56));
                var63_57 = ScriptFormula.callBuiltin((String)"push", var61_55, (ScriptContext)var1_1);
                var0.val("lines", var63_57);
                var64_58 = new ArrayList<ScriptValue>();
                var64_58.add(var1_1.getClassOrVar("lines"));
                var64_58.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<white><b>INGREDIENTS"));
                var65_59 = ScriptFormula.callBuiltin((String)"push", var64_58, (ScriptContext)var1_1);
                var0.val("lines", var65_59);
                var69_60 = var1_1.getClassOrVar("recipe");
                var66_61 = ScriptProgram.elementsOf((ScriptValue)(var69_60 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var69_60, (ScriptContext)var1_1) : ScriptValue.NULL));
                if (var66_61 != null) {
                    for (ScriptValue var68_63 : var66_61) {
                        var0.val("in_item", var68_63);
                        var70_64 = new ArrayList<ScriptValue>();
                        var70_64.add(var1_1.getClassOrVar("in_item"));
                        if (!(ScriptFormula.callBuiltin((String)"is_empty", var70_64, (ScriptContext)var1_1).asBool() ^ true)) continue;
                        var71_65 = new ArrayList<ScriptValue>();
                        var71_65.add(var1_1.getClassOrVar("lines"));
                        var72_66 = var1_1.getClassOrVar("in_item");
                        var73_67 = var1_1.getClassOrVar("in_item");
                        var71_65.add(ScriptValue.of((String)("<yellow>\u2726 <white>" + (var72_66 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var72_66, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " <gray>x" + (var73_67 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var73_67, (ScriptContext)var1_1) : ScriptValue.NULL).asStr())));
                        var74_68 = ScriptFormula.callBuiltin((String)"push", var71_65, (ScriptContext)var1_1);
                        var0.val("lines", var74_68);
                    }
                }
            }
            v3 /* !! */  = (var75_69 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var75_69, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v3 /* !! */ .asNum() > 0.0) ** GOTO lbl-1000
            var76_70 = var1_1.getClassOrVar("recipe");
            v4 /* !! */  = var76_70 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var76_70, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v4 /* !! */ .asNum() > 0.0)) {
                v5 = false;
            } else lbl-1000:
            // 2 sources

            {
                v5 = true;
            }
            if (v5 != false || ((var77_71 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fuel_required", (ScriptValue)var77_71, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
            var78_72 = var1_1.getClassOrVar("recipe");
            v6 /* !! */  = var78_72 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var78_72, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v6 /* !! */ .asNum() > 0.0)) {
                v7 = false;
            } else lbl-1000:
            // 2 sources

            {
                v7 = true;
            }
            var79_73 = v7;
            var80_74 = ScriptValue.of((boolean)v7);
            var0.val("has_power_line", var80_74);
            if (var79_73) {
                var81_75 = new ArrayList<ScriptValue>();
                var81_75.add(var1_1.getClassOrVar("lines"));
                var82_76 = ScriptContext.builder().copyFrom(var1_1);
                var81_75.add(RecipeInfo._div(var82_76));
                var83_77 = ScriptFormula.callBuiltin((String)"push", var81_75, (ScriptContext)var1_1);
                var0.val("lines", var83_77);
                var84_78 = new ArrayList<ScriptValue>();
                var84_78.add(var1_1.getClassOrVar("lines"));
                var84_78.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<white><b>POWER"));
                var85_79 = ScriptFormula.callBuiltin((String)"push", var84_78, (ScriptContext)var1_1);
                var0.val("lines", var85_79);
            }
            var86_80 = new ArrayList<ScriptValue>();
            var86_80.add(var1_1.getClassOrVar("lines"));
            v8 = new StringBuilder().append("<gray>\u23f1 Time: <white>");
            var87_81 = 10.0;
            if (10.0 == 0.0) {
                v9 = 0.0;
            } else {
                var89_82 = new ArrayList<ScriptValue>();
                var90_83 = 20.0;
                var89_82.add(ScriptValue.of((double)((20.0 == 0.0 ? 0.0 : var6_6 / var90_83) * 10.0)));
                v9 = ScriptFormula.callBuiltin((String)"round", var89_82, (ScriptContext)var1_1).asNum() / var87_81;
            }
            var86_80.add(ScriptValue.of((String)v8.append(ScriptFormula.numToStr((double)v9)).append("s").toString()));
            var92_84 = ScriptFormula.callBuiltin((String)"push", var86_80, (ScriptContext)var1_1);
            var0.val("lines", var92_84);
            var93_85 = var1_1.getClassOrVar("recipe");
            v10 /* !! */  = var93_85 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var93_85, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v10 /* !! */ .asNum() > 0.0) {
                var94_86 = new ArrayList<ScriptValue>();
                var94_86.add(var1_1.getClassOrVar("lines"));
                var95_87 = var1_1.getClassOrVar("recipe");
                var94_86.add(ScriptValue.of((String)("<gray>\u2699 RPM: <white>" + (var95_87 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var95_87, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " <dark_gray>min")));
                var96_88 = ScriptFormula.callBuiltin((String)"push", var94_86, (ScriptContext)var1_1);
                var0.val("lines", var96_88);
            }
            v11 /* !! */  = (var97_89 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var97_89, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v11 /* !! */ .asNum() > 0.0) {
                var98_90 = new ArrayList<ScriptValue>();
                var98_90.add(var1_1.getClassOrVar("lines"));
                var99_91 = var1_1.getClassOrVar("recipe");
                var98_90.add(ScriptValue.of((String)("<gray>\u26ed SU: <aqua>" + (var99_91 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var99_91, (ScriptContext)var1_1) : ScriptValue.NULL).asStr())));
                var100_92 = ScriptFormula.callBuiltin((String)"push", var98_90, (ScriptContext)var1_1);
                var0.val("lines", var100_92);
            }
            if (((var101_93 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fuel_required", (ScriptValue)var101_93, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
                var102_94 = var1_1.getClassOrVar("Machine");
                v12 = var102_94 != ScriptValue.NULL ? ((var103_95 = PolyClassMachine_v3.ofGuarded((ScriptValue)var102_94)) != null ? var103_95.tg$196_burn_time() : PolyDispatch.bootstrapGet("memberGet", "burn_time", (ScriptValue)var102_94, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (v12 > 0.0) {
                    var106_96 = var1_1.getClassOrVar("Machine");
                    var104_98 = Math.max(1.0, var106_96 != ScriptValue.NULL ? ((var107_97 = PolyClassMachine_v3.ofGuarded((ScriptValue)var106_96)) != null ? var107_97.tg$194_max_burn_time() : PolyDispatch.bootstrapGet("memberGet", "max_burn_time", (ScriptValue)var106_96, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum());
                    v13 = var104_98 == 0.0 ? 0.0 : ((var108_99 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? ((var109_100 = PolyClassMachine_v3.ofGuarded((ScriptValue)var108_99)) != null ? var109_100.tg$196_burn_time() : PolyDispatch.bootstrapGet("memberGet", "burn_time", (ScriptValue)var108_99, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum()) / var104_98;
                    var110_101 = Math.floor(v13 * 100.0);
                    var112_102 = ScriptValue.of((double)var110_101);
                    var0.val("fuel_pct", var112_102);
                    var113_103 = new ArrayList<ScriptValue>();
                    var113_103.add(var1_1.getClassOrVar("lines"));
                    var113_103.add(ScriptValue.of((String)("<gray>\ud83d\udd25 Fuel: <gold>" + ScriptFormula.numToStr((double)var110_101) + "% <dark_gray>burning")));
                    var114_104 = ScriptFormula.callBuiltin((String)"push", var113_103, (ScriptContext)var1_1);
                    var0.val("lines", var114_104);
                } else {
                    var115_105 = new ArrayList<ScriptValue>();
                    var115_105.add(var1_1.getClassOrVar("lines"));
                    var115_105.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<gray>\ud83d\udd25 Fuel: <red>needed"));
                    var116_106 = ScriptFormula.callBuiltin((String)"push", var115_105, (ScriptContext)var1_1);
                    var0.val("lines", var116_106);
                }
            }
            v14 /* !! */  = (var117_107 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var117_107, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v14 /* !! */ .asNum() > 0.0)) break block24;
            var118_108 = new ArrayList<ScriptValue>();
            var118_108.add(var1_1.getClassOrVar("lines"));
            var119_109 = var1_1.getClassOrVar("Machine");
            var121_111 = var1_1.getClassOrVar("Machine");
            var123_113 = var1_1.getClassOrVar("recipe");
            var118_108.add(ScriptValue.of((String)("<gray>\u26a1 Energy: <white>" + (var119_109 != ScriptValue.NULL ? ((var120_110 = PolyClassMachine_v3.ofGuarded((ScriptValue)var119_109)) != null ? var120_110.pg$125_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)var119_109, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "<dark_gray>/<white>" + (var121_111 != ScriptValue.NULL ? ((var122_112 = PolyClassMachine_v3.ofGuarded((ScriptValue)var121_111)) != null ? var122_112.pg$162_energy_capacity() : PolyDispatch.bootstrapGet("memberGet", "energy_capacity", (ScriptValue)var121_111, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + " <gray>(-" + (var123_113 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var123_113, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + "/t)")));
            var124_114 = ScriptFormula.callBuiltin((String)"push", var118_108, (ScriptContext)var1_1);
            var0.val("lines", var124_114);
        }
        return var1_1.getClassOrVar("lines");
    }
}
