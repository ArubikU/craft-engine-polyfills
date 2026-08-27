/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

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
            PolyClassMachine polyClassMachine;
            PolyClassMachine polyClassMachine2;
            scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine2.pg$161_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("recipe", scriptValue2);
            if (!ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null"))) break block0;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine.pg$185_matching_recipe() : PolyDispatch.bootstrapGet("memberGet", "matching_recipe", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)100.0));
            d = d3 * ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext).asNum() / d2;
        }
        double d4 = Math.floor(d);
        ScriptValue scriptValue = ScriptValue.of((double)d4);
        builder.val("filled", scriptValue);
        ScriptValue scriptValue2 = ScriptValue.of((String)"");
        builder.val("bar", scriptValue2);
        double d5 = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("i", scriptValue3);
        for (int i = 0; i < 1000; ++i) {
            if (!(scriptContext.getNum("i") < scriptContext.getNum("width"))) break;
            ScriptValue scriptValue4 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("bar"), (ScriptValue)(scriptContext.getNum("i") < d4 ? ScriptValue.of((String)(scriptContext.getStr("full_color") + "\u2588")) : ScriptValue.of((String)(scriptContext.getStr("empty_color") + "\u2591"))));
            builder.val("bar", scriptValue4);
            ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)ScriptValue.of((double)1.0));
            builder.val("i", scriptValue5);
        }
        return scriptContext.getClassOrVar("bar");
    }

    public static ScriptValue _div(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)"<dark_gray><st>                              ");
    }

    public static ScriptValue item(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = RecipeInfo._recipe(builder2);
        builder.val("recipe", scriptValue);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return ScriptValue.of((String)"minecraft:barrier");
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
        return ScriptValue.of((String)"minecraft:book");
    }

    public static ScriptValue name(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = RecipeInfo._recipe(builder2);
        builder.val("recipe", scriptValue2);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return ScriptValue.of((String)"<red><b>No Recipe");
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("recipe");
        if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((double)0.0))) {
            return ScriptValue.of((String)"<gold><b>\u2699 Recipe");
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("recipe");
        arrayList.add((ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"first", arrayList, (ScriptContext)scriptContext);
        builder.val("first_item", scriptValue5);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptValue5);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.of((String)"<gold><b>\u2699 Recipe");
        }
        ScriptValue scriptValue6 = ScriptValue.of((String)("<gold><b>" + ((scriptValue = scriptContext.getClassOrVar("first_item")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()));
        builder.val("label", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        Object object = scriptValue7 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassMachine.pg$161_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            PolyClassMachine polyClassMachine2;
            ScriptValue scriptValue8;
            return ScriptValue.of((String)(scriptValue6.asStr() + " <yellow>" + ScriptFormula.numToStr((double)Math.floor((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassMachine2.tg$148_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum())) + "%"));
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
                var4_4.add(ScriptValue.of((String)"<gray>No matching recipe for what's inside"));
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
            var16_13 = ScriptFormula.valuesEqual((ScriptValue)(var14_11 != ScriptValue.NULL ? ((var15_12 = PolyClassMachine.ofGuarded((ScriptValue)var14_11)) != null ? var15_12.pg$161_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)var14_11, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)var1_1.getClassOrVar("null")) ^ true;
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
                var21_18.val("percent", (ScriptValue)(var22_19 != ScriptValue.NULL ? ((var23_20 = PolyClassMachine.ofGuarded((ScriptValue)var22_19)) != null ? var23_20.pg$147_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)var22_19, (ScriptContext)var1_1)) : ScriptValue.NULL));
                var21_18.val("width", ScriptValue.of((double)14.0));
                var21_18.val("full_color", ScriptValue.of((String)"<green>"));
                var21_18.val("empty_color", ScriptValue.of((String)"<dark_gray>"));
                var24_21 = var1_1.getClassOrVar("Machine");
                var20_17.add(ScriptValue.of((String)v0.append(RecipeInfo._bar(var21_18).asStr()).append(" <white>").append(ScriptFormula.numToStr((double)Math.floor(var24_21 != ScriptValue.NULL ? ((var25_22 = PolyClassMachine.ofGuarded((ScriptValue)var24_21)) != null ? var25_22.tg$148_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)var24_21, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum()))).append("%").toString()));
                var26_23 = ScriptFormula.callBuiltin((String)"push", var20_17, (ScriptContext)var1_1);
                var0.val("lines", var26_23);
            } else {
                var27_24 = new ArrayList<ScriptValue>();
                var27_24.add(var1_1.getClassOrVar("lines"));
                var27_24.add(ScriptValue.of((String)"<gray><i>Waiting to start\u2026"));
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
            var32_29.add(ScriptValue.of((String)"<white><b>OUTPUT"));
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
                        var42_38 = new ArrayList<ScriptValue>();
                        var42_38.add(var1_1.getClassOrVar("out_i"));
                        var43_39 = var1_1.getClassOrVar("recipe");
                        var44_40 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)(var43_39 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "output_chances", (ScriptValue)var43_39, (ScriptContext)var1_1) : ScriptValue.NULL), var42_38, (ScriptContext)var1_1);
                        var0.val("chance", (ScriptValue)var44_40);
                        var45_41 = 10.0;
                        if (10.0 == 0.0) {
                            v1 = 0.0;
                        } else {
                            var47_42 = new ArrayList<ScriptValue>();
                            var48_43 = var1_1.getClassOrVar("out_item");
                            var47_42.add(ScriptValue.of((double)(var11_9 * (var48_43 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var48_43, (ScriptContext)var1_1) : ScriptValue.NULL).asNum() * var44_40.asNum() * 10.0)));
                            v1 = ScriptFormula.callBuiltin((String)"round", var47_42, (ScriptContext)var1_1).asNum() / var45_41;
                        }
                        var49_44 = v1;
                        var51_45 = ScriptValue.of((double)v1);
                        var0.val("per_min", var51_45);
                        var52_46 = var1_1.getClassOrVar("out_item");
                        var53_47 = var1_1.getClassOrVar("out_item");
                        var54_48 = ScriptValue.of((String)("<green>\u27a4 <white>" + (var52_46 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var52_46, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " <gray>x" + (var53_47 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var53_47, (ScriptContext)var1_1) : ScriptValue.NULL).asStr()));
                        var0.val("line", var54_48);
                        if (var44_40.asNum() < 1.0) {
                            var55_49 = ScriptValue.of((String)(var1_1.getStr("line") + " <gold>(" + ScriptFormula.numToStr((double)Math.floor(var44_40.asNum() * 100.0)) + "%)"));
                            var0.val("line", var55_49);
                        }
                        var56_50 = new ArrayList<ScriptValue>();
                        var56_50.add(var1_1.getClassOrVar("lines"));
                        var56_50.add(var1_1.getClassOrVar("line"));
                        var57_51 = ScriptFormula.callBuiltin((String)"push", var56_50, (ScriptContext)var1_1);
                        var0.val("lines", var57_51);
                        var58_52 = new ArrayList<ScriptValue>();
                        var58_52.add(var1_1.getClassOrVar("lines"));
                        var58_52.add(ScriptValue.of((String)("<dark_gray>   " + ScriptFormula.numToStr((double)var49_44) + "/min")));
                        var59_53 = ScriptFormula.callBuiltin((String)"push", var58_52, (ScriptContext)var1_1);
                        var0.val("lines", var59_53);
                    }
                    var60_54 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("out_i"), (ScriptValue)ScriptValue.of((double)1.0));
                    var0.val("out_i", var60_54);
                }
            }
            v2 /* !! */  = (var61_55 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var61_55, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)v2 /* !! */ , (ScriptContext)var1_1).asNum() > 0.0) {
                var62_56 = new ArrayList<ScriptValue>();
                var62_56.add(var1_1.getClassOrVar("lines"));
                var63_57 = ScriptContext.builder().copyFrom(var1_1);
                var62_56.add(RecipeInfo._div(var63_57));
                var64_58 = ScriptFormula.callBuiltin((String)"push", var62_56, (ScriptContext)var1_1);
                var0.val("lines", var64_58);
                var65_59 = new ArrayList<ScriptValue>();
                var65_59.add(var1_1.getClassOrVar("lines"));
                var65_59.add(ScriptValue.of((String)"<white><b>INGREDIENTS"));
                var66_60 = ScriptFormula.callBuiltin((String)"push", var65_59, (ScriptContext)var1_1);
                var0.val("lines", var66_60);
                var70_61 = var1_1.getClassOrVar("recipe");
                var67_62 = ScriptProgram.elementsOf((ScriptValue)(var70_61 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var70_61, (ScriptContext)var1_1) : ScriptValue.NULL));
                if (var67_62 != null) {
                    for (ScriptValue var69_64 : var67_62) {
                        var0.val("in_item", var69_64);
                        var71_65 = new ArrayList<ScriptValue>();
                        var71_65.add(var1_1.getClassOrVar("in_item"));
                        if (!(ScriptFormula.callBuiltin((String)"is_empty", var71_65, (ScriptContext)var1_1).asBool() ^ true)) continue;
                        var72_66 = new ArrayList<ScriptValue>();
                        var72_66.add(var1_1.getClassOrVar("lines"));
                        var73_67 = var1_1.getClassOrVar("in_item");
                        var74_68 = var1_1.getClassOrVar("in_item");
                        var72_66.add(ScriptValue.of((String)("<yellow>\u2726 <white>" + (var73_67 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var73_67, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " <gray>x" + (var74_68 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var74_68, (ScriptContext)var1_1) : ScriptValue.NULL).asStr())));
                        var75_69 = ScriptFormula.callBuiltin((String)"push", var72_66, (ScriptContext)var1_1);
                        var0.val("lines", var75_69);
                    }
                }
            }
            v3 /* !! */  = (var76_70 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var76_70, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v3 /* !! */ .asNum() > 0.0) ** GOTO lbl-1000
            var77_71 = var1_1.getClassOrVar("recipe");
            v4 /* !! */  = var77_71 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var77_71, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v4 /* !! */ .asNum() > 0.0)) {
                v5 = false;
            } else lbl-1000:
            // 2 sources

            {
                v5 = true;
            }
            if (v5 != false || ((var78_72 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fuel_required", (ScriptValue)var78_72, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
            var79_73 = var1_1.getClassOrVar("recipe");
            v6 /* !! */  = var79_73 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var79_73, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v6 /* !! */ .asNum() > 0.0)) {
                v7 = false;
            } else lbl-1000:
            // 2 sources

            {
                v7 = true;
            }
            var80_74 = v7;
            var81_75 = ScriptValue.of((boolean)v7);
            var0.val("has_power_line", var81_75);
            if (var80_74) {
                var82_76 = new ArrayList<ScriptValue>();
                var82_76.add(var1_1.getClassOrVar("lines"));
                var83_77 = ScriptContext.builder().copyFrom(var1_1);
                var82_76.add(RecipeInfo._div(var83_77));
                var84_78 = ScriptFormula.callBuiltin((String)"push", var82_76, (ScriptContext)var1_1);
                var0.val("lines", var84_78);
                var85_79 = new ArrayList<ScriptValue>();
                var85_79.add(var1_1.getClassOrVar("lines"));
                var85_79.add(ScriptValue.of((String)"<white><b>POWER"));
                var86_80 = ScriptFormula.callBuiltin((String)"push", var85_79, (ScriptContext)var1_1);
                var0.val("lines", var86_80);
            }
            var87_81 = new ArrayList<ScriptValue>();
            var87_81.add(var1_1.getClassOrVar("lines"));
            v8 = new StringBuilder().append("<gray>\u23f1 Time: <white>");
            var88_82 = 10.0;
            if (10.0 == 0.0) {
                v9 = 0.0;
            } else {
                var90_83 = new ArrayList<ScriptValue>();
                var91_84 = 20.0;
                var90_83.add(ScriptValue.of((double)((20.0 == 0.0 ? 0.0 : var6_6 / var91_84) * 10.0)));
                v9 = ScriptFormula.callBuiltin((String)"round", var90_83, (ScriptContext)var1_1).asNum() / var88_82;
            }
            var87_81.add(ScriptValue.of((String)v8.append(ScriptFormula.numToStr((double)v9)).append("s").toString()));
            var93_85 = ScriptFormula.callBuiltin((String)"push", var87_81, (ScriptContext)var1_1);
            var0.val("lines", var93_85);
            var94_86 = var1_1.getClassOrVar("recipe");
            v10 /* !! */  = var94_86 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var94_86, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v10 /* !! */ .asNum() > 0.0) {
                var95_87 = new ArrayList<ScriptValue>();
                var95_87.add(var1_1.getClassOrVar("lines"));
                var96_88 = var1_1.getClassOrVar("recipe");
                var95_87.add(ScriptValue.of((String)("<gray>\u2699 RPM: <white>" + (var96_88 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var96_88, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " <dark_gray>min")));
                var97_89 = ScriptFormula.callBuiltin((String)"push", var95_87, (ScriptContext)var1_1);
                var0.val("lines", var97_89);
            }
            v11 /* !! */  = (var98_90 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var98_90, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v11 /* !! */ .asNum() > 0.0) {
                var99_91 = new ArrayList<ScriptValue>();
                var99_91.add(var1_1.getClassOrVar("lines"));
                var100_92 = var1_1.getClassOrVar("recipe");
                var99_91.add(ScriptValue.of((String)("<gray>\u26ed SU: <aqua>" + (var100_92 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var100_92, (ScriptContext)var1_1) : ScriptValue.NULL).asStr())));
                var101_93 = ScriptFormula.callBuiltin((String)"push", var99_91, (ScriptContext)var1_1);
                var0.val("lines", var101_93);
            }
            if (((var102_94 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fuel_required", (ScriptValue)var102_94, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
                var103_95 = var1_1.getClassOrVar("Machine");
                v12 = var103_95 != ScriptValue.NULL ? ((var104_96 = PolyClassMachine.ofGuarded((ScriptValue)var103_95)) != null ? var104_96.tg$196_burn_time() : PolyDispatch.bootstrapGet("memberGet", "burn_time", (ScriptValue)var103_95, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (v12 > 0.0) {
                    var107_97 = var1_1.getClassOrVar("Machine");
                    var105_99 = Math.max(1.0, var107_97 != ScriptValue.NULL ? ((var108_98 = PolyClassMachine.ofGuarded((ScriptValue)var107_97)) != null ? var108_98.tg$194_max_burn_time() : PolyDispatch.bootstrapGet("memberGet", "max_burn_time", (ScriptValue)var107_97, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum());
                    v13 = var105_99 == 0.0 ? 0.0 : ((var109_100 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? ((var110_101 = PolyClassMachine.ofGuarded((ScriptValue)var109_100)) != null ? var110_101.tg$196_burn_time() : PolyDispatch.bootstrapGet("memberGet", "burn_time", (ScriptValue)var109_100, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum()) / var105_99;
                    var111_102 = Math.floor(v13 * 100.0);
                    var113_103 = ScriptValue.of((double)var111_102);
                    var0.val("fuel_pct", var113_103);
                    var114_104 = new ArrayList<ScriptValue>();
                    var114_104.add(var1_1.getClassOrVar("lines"));
                    var114_104.add(ScriptValue.of((String)("<gray>\ud83d\udd25 Fuel: <gold>" + ScriptFormula.numToStr((double)var111_102) + "% <dark_gray>burning")));
                    var115_105 = ScriptFormula.callBuiltin((String)"push", var114_104, (ScriptContext)var1_1);
                    var0.val("lines", var115_105);
                } else {
                    var116_106 = new ArrayList<ScriptValue>();
                    var116_106.add(var1_1.getClassOrVar("lines"));
                    var116_106.add(ScriptValue.of((String)"<gray>\ud83d\udd25 Fuel: <red>needed"));
                    var117_107 = ScriptFormula.callBuiltin((String)"push", var116_106, (ScriptContext)var1_1);
                    var0.val("lines", var117_107);
                }
            }
            v14 /* !! */  = (var118_108 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var118_108, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v14 /* !! */ .asNum() > 0.0)) break block24;
            var119_109 = new ArrayList<ScriptValue>();
            var119_109.add(var1_1.getClassOrVar("lines"));
            var120_110 = var1_1.getClassOrVar("Machine");
            var122_112 = var1_1.getClassOrVar("Machine");
            var124_114 = var1_1.getClassOrVar("recipe");
            var119_109.add(ScriptValue.of((String)("<gray>\u26a1 Energy: <white>" + (var120_110 != ScriptValue.NULL ? ((var121_111 = PolyClassMachine.ofGuarded((ScriptValue)var120_110)) != null ? var121_111.pg$125_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)var120_110, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "<dark_gray>/<white>" + (var122_112 != ScriptValue.NULL ? ((var123_113 = PolyClassMachine.ofGuarded((ScriptValue)var122_112)) != null ? var123_113.pg$162_energy_capacity() : PolyDispatch.bootstrapGet("memberGet", "energy_capacity", (ScriptValue)var122_112, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + " <gray>(-" + (var124_114 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var124_114, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + "/t)")));
            var125_115 = ScriptFormula.callBuiltin((String)"push", var119_109, (ScriptContext)var1_1);
            var0.val("lines", var125_115);
        }
        return var1_1.getClassOrVar("lines");
    }
}
