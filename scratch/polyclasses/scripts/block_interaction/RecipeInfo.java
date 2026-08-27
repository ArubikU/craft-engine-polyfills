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
            ScriptValue scriptValue;
            ScriptValue scriptValue2;
            scriptContext = builder.peek();
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue3 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$162_working_recipe() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("recipe", scriptValue3);
            if (!ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("null"))) break block0;
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue4 = polyClassMachine_v32 != null ? polyClassMachine_v32.pg$186_matching_recipe() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "matching_recipe", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("recipe", scriptValue4);
        }
        return scriptContext.getClassOrVar("recipe");
    }

    public static ScriptValue _bar(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 100.0;
        double d2 = Math.floor(100.0 == 0.0 ? 0.0 : scriptContext.getNum("width") * ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)scriptContext.getClassOrVar("percent"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 100.0)), (ScriptContext)scriptContext).asNum() / d);
        ScriptValue scriptValue = ScriptValue.of((double)d2);
        builder.val("filled", scriptValue);
        ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "");
        builder.val("bar", scriptValue2);
        double d3 = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("i", scriptValue3);
        ScriptValue scriptValue4 = scriptValue2;
        ScriptValue scriptValue5 = ScriptValue.of((double)d3);
        for (int i = 0; i < 1000; ++i) {
            if (!(scriptValue5.asNum() < scriptContext.getNum("width"))) break;
            ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)(scriptValue5.asNum() < d2 ? ScriptValue.of((String)(scriptContext.getStr("full_color") + "\u2588")) : ScriptValue.of((String)(scriptContext.getStr("empty_color") + "\u2591"))));
            builder.val("bar", scriptValue6);
            scriptValue4 = scriptValue6;
            ScriptValue scriptValue7 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 1.0)));
            builder.val("i", scriptValue7);
            scriptValue5 = scriptValue7;
        }
        return scriptValue4;
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
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)object, (ScriptContext)scriptContext).asNum() > 0.0) {
            ScriptValue scriptValue2 = ScriptFormula.callBuiltin1((String)"first", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
            builder.val("first_item", scriptValue2);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool() ^ true) {
                return scriptValue2;
            }
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "minecraft:book");
    }

    public static ScriptValue name(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = RecipeInfo._recipe(builder2);
        builder.val("recipe", scriptValue2);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<red><b>No Recipe");
        }
        if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 0.0)))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<gold><b>\u2699 Recipe");
        }
        ScriptValue scriptValue3 = ScriptFormula.callBuiltin1((String)"first", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("first_item", scriptValue3);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<gold><b>\u2699 Recipe");
        }
        ScriptValue scriptValue4 = ScriptValue.of((String)("<gold><b>" + (scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()));
        builder.val("label", scriptValue4);
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        if (ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine_v3 != null ? polyClassMachine_v3.pg$162_working_recipe() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ScriptValue scriptValue5;
            PolyClassMachine_v3 polyClassMachine_v32;
            return ScriptValue.of((String)(scriptValue4.asStr() + " <yellow>" + ScriptFormula.numToStr((double)Math.floor((polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine_v32.tg$149_progress_percent() : ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum()))) + "%"));
        }
        return ScriptValue.of((String)(scriptValue4.asStr() + " <dark_gray>(idle)"));
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue lore(ScriptContext.Builder var0) {
        block22: {
            var1_1 = var0.peek();
            var2_2 = ScriptContext.builder().copyFrom(var1_1);
            var3_3 = RecipeInfo._recipe(var2_2);
            var0.val("recipe", var3_3);
            if (ScriptFormula.valuesEqual((ScriptValue)var3_3, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var4_4 = new ArrayList<ScriptValue>();
                var4_4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<gray>No matching recipe for what's inside"));
                return new ScriptValue.Array(var4_4);
            }
            var5_5 = Math.max(1.0, (var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "processing_time", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL).asNum());
            var7_6 = ScriptValue.of((double)var5_5);
            var0.val("eff_ticks", var7_6);
            var8_7 = var5_5;
            var10_8 = var8_7 == 0.0 ? 0.0 : 1200.0 / var8_7;
            var12_9 = ScriptValue.of((double)var10_8);
            var0.val("crafts_per_min", var12_9);
            var13_10 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
            var15_12 = ScriptFormula.valuesEqual((ScriptValue)(var13_10 != null ? var13_10.pg$162_working_recipe() : ((var14_11 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)var14_11, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)var1_1.getClassOrVar("null")) ^ true;
            var16_13 = ScriptValue.of((boolean)var15_12);
            var0.val("working", var16_13);
            var17_14 = new ArrayList<E>();
            var18_15 = new ScriptValue.Array(var17_14);
            var0.val("lines", (ScriptValue)var18_15);
            if (var15_12) {
                v0 = var1_1.getClassOrVar("lines");
                v1 = new StringBuilder();
                var19_16 = ScriptContext.builder().copyFrom(var1_1);
                var20_17 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                var19_16.val("percent", (ScriptValue)(var20_17 != null ? var20_17.pg$148_progress_percent() : ((var21_18 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)var21_18, (ScriptContext)var1_1) : ScriptValue.NULL)));
                var19_16.val("width",  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 14.0));
                var19_16.val("full_color",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<green>"));
                var19_16.val("empty_color",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<dark_gray>"));
                var22_19 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                var24_21 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)v0, (ScriptValue)ScriptValue.of((String)v1.append(RecipeInfo._bar(var19_16).asStr()).append(" <white>").append(ScriptFormula.numToStr((double)Math.floor(var22_19 != null ? var22_19.tg$149_progress_percent() : ((var23_20 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)var23_20, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum())))).append("%").toString()), (ScriptContext)var1_1);
                var0.val("lines", var24_21);
            } else {
                var25_22 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<gray><i>Waiting to start\u2026")), (ScriptContext)var1_1);
                var0.val("lines", var25_22);
            }
            var26_23 = ScriptContext.builder().copyFrom(var1_1);
            var27_24 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)RecipeInfo._div(var26_23), (ScriptContext)var1_1);
            var0.val("lines", var27_24);
            var28_25 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<white><b>OUTPUT")), (ScriptContext)var1_1);
            var0.val("lines", var28_25);
            var29_26 = 0.0;
            var31_27 = ScriptValue.of((double)0.0);
            var0.val("out_i", var31_27);
            var32_28 = ScriptProgram.elementsOf((ScriptValue)(var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL));
            var35_29 /* !! */  = var1_1.getClassOrVar("chance");
            var36_30 = var1_1.getClassOrVar("per_min");
            var37_31 = var1_1.getClassOrVar("line");
            var38_32 = ScriptValue.of((double)var29_26);
            var39_33 = var28_25;
            if (var32_28 != null) {
                for (ScriptValue var34_35 : var32_28) {
                    var0.val("out_item", var34_35);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var34_35, (ScriptContext)var1_1).asBool() ^ true) {
                        var40_36 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)(var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "output_chances", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var38_32, (ScriptContext)var1_1);
                        var0.val("chance", (ScriptValue)var40_36);
                        var35_29 /* !! */  = var40_36;
                        var41_37 = 10.0;
                        var43_38 = 10.0 == 0.0 ? 0.0 : ScriptFormula.callBuiltin1((String)"round", (ScriptValue)ScriptValue.of((double)(var10_8 * (var34_35 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var34_35, (ScriptContext)var1_1) : ScriptValue.NULL).asNum() * var35_29 /* !! */ .asNum() * 10.0)), (ScriptContext)var1_1).asNum() / var41_37;
                        var45_39 = ScriptValue.of((double)var43_38);
                        var0.val("per_min", var45_39);
                        var36_30 = var45_39;
                        var46_40 = ScriptValue.of((String)("<green>\u27a4 <white>" + (var34_35 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var34_35, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " <gray>x" + (var34_35 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var34_35, (ScriptContext)var1_1) : ScriptValue.NULL).asStr()));
                        var0.val("line", var46_40);
                        var37_31 = var46_40;
                        if (var35_29 /* !! */ .asNum() < 1.0) {
                            var47_41 = ScriptValue.of((String)(var37_31.asStr() + " <gold>(" + ScriptFormula.numToStr((double)Math.floor(var35_29 /* !! */ .asNum() * 100.0)) + "%)"));
                            var0.val("line", var47_41);
                            var37_31 = var47_41;
                        }
                        var48_42 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var39_33, (ScriptValue)var1_1.getClassOrVar("line"), (ScriptContext)var1_1);
                        var0.val("lines", var48_42);
                        var39_33 = var48_42;
                        var49_43 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var39_33, (ScriptValue)ScriptValue.of((String)("<dark_gray>   " + var36_30.asStr() + "/min")), (ScriptContext)var1_1);
                        var0.val("lines", var49_43);
                        var39_33 = var49_43;
                    }
                    var50_44 = ScriptFormula.addPolymorphic((ScriptValue)var38_32, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RecipeInfo.class, 1.0)));
                    var0.val("out_i", var50_44);
                    var38_32 = var50_44;
                }
            }
            v2 /* !! */  = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)v2 /* !! */ , (ScriptContext)var1_1).asNum() > 0.0) {
                var51_45 = ScriptContext.builder().copyFrom(var1_1);
                var52_46 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)RecipeInfo._div(var51_45), (ScriptContext)var1_1);
                var0.val("lines", var52_46);
                var53_47 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<white><b>INGREDIENTS")), (ScriptContext)var1_1);
                var0.val("lines", var53_47);
                var54_48 = ScriptProgram.elementsOf((ScriptValue)(var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL));
                var57_49 = var53_47;
                if (var54_48 != null) {
                    for (ScriptValue var56_51 : var54_48) {
                        var0.val("in_item", var56_51);
                        if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var56_51, (ScriptContext)var1_1).asBool() ^ true)) continue;
                        var58_52 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var57_49, (ScriptValue)ScriptValue.of((String)("<yellow>\u2726 <white>" + (var56_51 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var56_51, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " <gray>x" + (var56_51 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var56_51, (ScriptContext)var1_1) : ScriptValue.NULL).asStr())), (ScriptContext)var1_1);
                        var0.val("lines", var58_52);
                        var57_49 = var58_52;
                    }
                }
            }
            v3 /* !! */  = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v3 /* !! */ .asNum() > 0.0) ** GOTO lbl-1000
            v4 /* !! */  = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v4 /* !! */ .asNum() > 0.0)) {
                v5 = false;
            } else lbl-1000:
            // 2 sources

            {
                v5 = true;
            }
            if (v5 != false || (var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fuel_required", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
            v6 /* !! */  = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v6 /* !! */ .asNum() > 0.0)) {
                v7 = false;
            } else lbl-1000:
            // 2 sources

            {
                v7 = true;
            }
            var59_53 = v7;
            var60_54 = ScriptValue.of((boolean)v7);
            var0.val("has_power_line", var60_54);
            if (var59_53) {
                var61_55 = ScriptContext.builder().copyFrom(var1_1);
                var62_56 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)RecipeInfo._div(var61_55), (ScriptContext)var1_1);
                var0.val("lines", var62_56);
                var63_57 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<white><b>POWER")), (ScriptContext)var1_1);
                var0.val("lines", var63_57);
            }
            v8 = var1_1.getClassOrVar("lines");
            v9 = new StringBuilder().append("<gray>\u23f1 Time: <white>");
            var64_58 = 10.0;
            if (10.0 == 0.0) {
                v10 = 0.0;
            } else {
                var66_59 = 20.0;
                v10 = ScriptFormula.callBuiltin1((String)"round", (ScriptValue)ScriptValue.of((double)((20.0 == 0.0 ? 0.0 : var5_5 / var66_59) * 10.0)), (ScriptContext)var1_1).asNum() / var64_58;
            }
            var68_60 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)v8, (ScriptValue)ScriptValue.of((String)v9.append(ScriptFormula.numToStr((double)v10)).append("s").toString()), (ScriptContext)var1_1);
            var0.val("lines", var68_60);
            v11 /* !! */  = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v11 /* !! */ .asNum() > 0.0) {
                var69_61 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)ScriptValue.of((String)("<gray>\u2699 RPM: <white>" + (var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " <dark_gray>min")), (ScriptContext)var1_1);
                var0.val("lines", var69_61);
            }
            v12 /* !! */  = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v12 /* !! */ .asNum() > 0.0) {
                var70_62 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)ScriptValue.of((String)("<gray>\u26ed SU: <aqua>" + (var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL).asStr())), (ScriptContext)var1_1);
                var0.val("lines", var70_62);
            }
            if ((var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fuel_required", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
                var71_63 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                v13 = var71_63 != null ? var71_63.tg$197_burn_time() : ((var72_64 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "burn_time", (ScriptValue)var72_64, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum());
                if (v13 > 0.0) {
                    var75_65 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var73_67 = Math.max(1.0, var75_65 != null ? var75_65.tg$195_max_burn_time() : ((var76_66 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "max_burn_time", (ScriptValue)var76_66, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum()));
                    var79_70 = Math.floor((var73_67 == 0.0 ? 0.0 : ((var77_68 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine")) != null ? var77_68.tg$197_burn_time() : ((var78_69 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "burn_time", (ScriptValue)var78_69, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum())) / var73_67) * 100.0);
                    var81_71 = ScriptValue.of((double)var79_70);
                    var0.val("fuel_pct", var81_71);
                    var82_72 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)ScriptValue.of((String)("<gray>\ud83d\udd25 Fuel: <gold>" + ScriptFormula.numToStr((double)var79_70) + "% <dark_gray>burning")), (ScriptContext)var1_1);
                    var0.val("lines", var82_72);
                } else {
                    var83_73 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", RecipeInfo.class, "<gray>\ud83d\udd25 Fuel: <red>needed")), (ScriptContext)var1_1);
                    var0.val("lines", var83_73);
                }
            }
            v14 /* !! */  = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v14 /* !! */ .asNum() > 0.0)) break block22;
            v15 = new StringBuilder().append("<gray>\u26a1 Energy: <white>").append(((var84_74 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine")) != null ? var84_74.pg$125_energy_stored() : ((var85_75 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)var85_75, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr()).append("<dark_gray>/<white>");
            var86_76 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
            var88_78 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)var1_1.getClassOrVar("lines"), (ScriptValue)ScriptValue.of((String)v15.append((var86_76 != null ? var86_76.pg$163_energy_capacity() : ((var87_77 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_capacity", (ScriptValue)var87_77, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr()).append(" <gray>(-").append((var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL).asStr()).append("/t)").toString()), (ScriptContext)var1_1);
            var0.val("lines", var88_78);
        }
        return var1_1.getClassOrVar("lines");
    }
}
