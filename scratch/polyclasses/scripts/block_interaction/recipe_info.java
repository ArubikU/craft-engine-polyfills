/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class RecipeInfo {
    public static ScriptValue _recipe(ScriptContext.Builder builder) {
        ScriptContext scriptContext;
        block0: {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue.Obj obj2;
            Object object2;
            scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v2(object2).pg$143_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("recipe", scriptValue2);
            if (!ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null"))) break block0;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).pg$157_matching_recipe() : PolyDispatch.bootstrapGet("memberGet", "matching_recipe", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
            ScriptValue scriptValue4 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("bar"), (ScriptValue)(scriptContext.getNum("i") < d4 ? ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("full_color"), (ScriptValue)ScriptValue.of((String)"\u2588")) : ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("empty_color"), (ScriptValue)ScriptValue.of((String)"\u2591"))));
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
        ScriptValue.Obj obj;
        Object object;
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
        ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gold><b>"), (ScriptValue)((scriptValue = scriptContext.getClassOrVar("first_item")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL));
        builder.val("label", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        Object object2 = scriptValue7 != ScriptValue.NULL ? (scriptValue7 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).pg$143_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ScriptValue.Obj obj2;
            Object object3;
            ScriptValue scriptValue8;
            return ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)" <yellow>")), (ScriptValue)ScriptValue.of((double)Math.floor(((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? (scriptValue8 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v2(object3).pg$135_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum()))), (ScriptValue)ScriptValue.of((String)"%"));
        }
        return ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)" <dark_gray>(idle)"));
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
            var17_14 = ScriptFormula.valuesEqual((ScriptValue)(var14_11 != ScriptValue.NULL ? (var14_11 instanceof ScriptValue.Obj && (var16_13 = (var15_12 = (ScriptValue.Obj)var14_11).instance()) != null && !(var16_13 instanceof PolyClass) && var15_12.typeName().equals("Machine") ? new PolyClassMachine_v2(var16_13).pg$143_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)var14_11, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)var1_1.getClassOrVar("null")) ^ true;
            var18_15 = ScriptValue.of((boolean)var17_14);
            var0.val("working", var18_15);
            var19_16 = new ArrayList<E>();
            var20_17 = new ScriptValue.Array(var19_16);
            var0.val("lines", (ScriptValue)var20_17);
            if (var17_14) {
                var21_18 = new ArrayList<ScriptValue>();
                var21_18.add(var1_1.getClassOrVar("lines"));
                var22_19 = ScriptContext.builder().copyFrom(var1_1);
                var23_20 = var1_1.getClassOrVar("Machine");
                var22_19.val("percent", (ScriptValue)(var23_20 != ScriptValue.NULL ? (var23_20 instanceof ScriptValue.Obj && (var25_22 = (var24_21 = (ScriptValue.Obj)var23_20).instance()) != null && !(var25_22 instanceof PolyClass) && var24_21.typeName().equals("Machine") ? new PolyClassMachine_v2(var25_22).pg$135_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)var23_20, (ScriptContext)var1_1)) : ScriptValue.NULL));
                var22_19.val("width", ScriptValue.of((double)14.0));
                var22_19.val("full_color", ScriptValue.of((String)"<green>"));
                var22_19.val("empty_color", ScriptValue.of((String)"<dark_gray>"));
                var26_23 = var1_1.getClassOrVar("Machine");
                var21_18.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)RecipeInfo._bar(var22_19), (ScriptValue)ScriptValue.of((String)" <white>")), (ScriptValue)ScriptValue.of((double)Math.floor((var26_23 != ScriptValue.NULL ? (var26_23 instanceof ScriptValue.Obj && (var28_25 = (var27_24 = (ScriptValue.Obj)var26_23).instance()) != null && !(var28_25 instanceof PolyClass) && var27_24.typeName().equals("Machine") ? new PolyClassMachine_v2(var28_25).pg$135_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)var26_23, (ScriptContext)var1_1)) : ScriptValue.NULL).asNum()))), (ScriptValue)ScriptValue.of((String)"%")));
                var29_26 = ScriptFormula.callBuiltin((String)"push", var21_18, (ScriptContext)var1_1);
                var0.val("lines", var29_26);
            } else {
                var30_27 = new ArrayList<ScriptValue>();
                var30_27.add(var1_1.getClassOrVar("lines"));
                var30_27.add(ScriptValue.of((String)"<gray><i>Waiting to start\u2026"));
                var31_28 = ScriptFormula.callBuiltin((String)"push", var30_27, (ScriptContext)var1_1);
                var0.val("lines", var31_28);
            }
            var32_29 = new ArrayList<ScriptValue>();
            var32_29.add(var1_1.getClassOrVar("lines"));
            var33_30 = ScriptContext.builder().copyFrom(var1_1);
            var32_29.add(RecipeInfo._div(var33_30));
            var34_31 = ScriptFormula.callBuiltin((String)"push", var32_29, (ScriptContext)var1_1);
            var0.val("lines", var34_31);
            var35_32 = new ArrayList<ScriptValue>();
            var35_32.add(var1_1.getClassOrVar("lines"));
            var35_32.add(ScriptValue.of((String)"<white><b>OUTPUT"));
            var36_33 = ScriptFormula.callBuiltin((String)"push", var35_32, (ScriptContext)var1_1);
            var0.val("lines", var36_33);
            var37_34 = 0.0;
            var39_35 = ScriptValue.of((double)0.0);
            var0.val("out_i", var39_35);
            var40_36 = ScriptProgram.resolveForRows((String)"recipe.outputs", (ScriptContext)var1_1, (int)1);
            if (var40_36 != null) {
                for (ScriptValue[] var42_38 : var40_36) {
                    var0.val("out_item", var42_38.length > 0 ? var42_38[0] : ScriptValue.NULL);
                    var43_39 = new ArrayList<ScriptValue>();
                    var43_39.add(var1_1.getClassOrVar("out_item"));
                    if (ScriptFormula.callBuiltin((String)"is_empty", var43_39, (ScriptContext)var1_1).asBool() ^ true) {
                        var44_40 = new ArrayList<ScriptValue>();
                        var44_40.add(var1_1.getClassOrVar("out_i"));
                        var45_41 = var1_1.getClassOrVar("recipe");
                        var46_42 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)(var45_41 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "output_chances", (ScriptValue)var45_41, (ScriptContext)var1_1) : ScriptValue.NULL), var44_40, (ScriptContext)var1_1);
                        var0.val("chance", (ScriptValue)var46_42);
                        var47_43 = 10.0;
                        if (10.0 == 0.0) {
                            v0 = 0.0;
                        } else {
                            var49_44 = new ArrayList<ScriptValue>();
                            var50_45 = var1_1.getClassOrVar("out_item");
                            var49_44.add(ScriptValue.of((double)(var11_9 * (var50_45 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var50_45, (ScriptContext)var1_1) : ScriptValue.NULL).asNum() * var46_42.asNum() * 10.0)));
                            v0 = ScriptFormula.callBuiltin((String)"round", var49_44, (ScriptContext)var1_1).asNum() / var47_43;
                        }
                        var51_46 = v0;
                        var53_47 = ScriptValue.of((double)v0);
                        var0.val("per_min", var53_47);
                        var54_48 = var1_1.getClassOrVar("out_item");
                        var55_49 = var1_1.getClassOrVar("out_item");
                        var56_50 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<green>\u27a4 <white>"), (ScriptValue)(var54_48 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var54_48, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" <gray>x")), (ScriptValue)(var55_49 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var55_49, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var0.val("line", var56_50);
                        if (var46_42.asNum() < 1.0) {
                            var57_51 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("line"), (ScriptValue)ScriptValue.of((String)" <gold>(")), (ScriptValue)ScriptValue.of((double)Math.floor(var46_42.asNum() * 100.0))), (ScriptValue)ScriptValue.of((String)"%)"));
                            var0.val("line", var57_51);
                        }
                        var58_52 = new ArrayList<ScriptValue>();
                        var58_52.add(var1_1.getClassOrVar("lines"));
                        var58_52.add(var1_1.getClassOrVar("line"));
                        var59_53 = ScriptFormula.callBuiltin((String)"push", var58_52, (ScriptContext)var1_1);
                        var0.val("lines", var59_53);
                        var60_54 = new ArrayList<ScriptValue>();
                        var60_54.add(var1_1.getClassOrVar("lines"));
                        var60_54.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<dark_gray>   "), (ScriptValue)ScriptValue.of((double)var51_46)), (ScriptValue)ScriptValue.of((String)"/min")));
                        var61_55 = ScriptFormula.callBuiltin((String)"push", var60_54, (ScriptContext)var1_1);
                        var0.val("lines", var61_55);
                    }
                    var62_56 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("out_i"), (ScriptValue)ScriptValue.of((double)1.0));
                    var0.val("out_i", var62_56);
                }
            }
            v1 /* !! */  = (var63_57 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var63_57, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)v1 /* !! */ , (ScriptContext)var1_1).asNum() > 0.0) {
                var64_58 = new ArrayList<ScriptValue>();
                var64_58.add(var1_1.getClassOrVar("lines"));
                var65_59 = ScriptContext.builder().copyFrom(var1_1);
                var64_58.add(RecipeInfo._div(var65_59));
                var66_60 = ScriptFormula.callBuiltin((String)"push", var64_58, (ScriptContext)var1_1);
                var0.val("lines", var66_60);
                var67_61 = new ArrayList<ScriptValue>();
                var67_61.add(var1_1.getClassOrVar("lines"));
                var67_61.add(ScriptValue.of((String)"<white><b>INGREDIENTS"));
                var68_62 = ScriptFormula.callBuiltin((String)"push", var67_61, (ScriptContext)var1_1);
                var0.val("lines", var68_62);
                var69_63 = ScriptProgram.resolveForRows((String)"recipe.inputs", (ScriptContext)var1_1, (int)1);
                if (var69_63 != null) {
                    for (ScriptValue[] var71_65 : var69_63) {
                        var0.val("in_item", var71_65.length > 0 ? var71_65[0] : ScriptValue.NULL);
                        var72_66 = new ArrayList<ScriptValue>();
                        var72_66.add(var1_1.getClassOrVar("in_item"));
                        if (!(ScriptFormula.callBuiltin((String)"is_empty", var72_66, (ScriptContext)var1_1).asBool() ^ true)) continue;
                        var73_67 = new ArrayList<ScriptValue>();
                        var73_67.add(var1_1.getClassOrVar("lines"));
                        var74_68 = var1_1.getClassOrVar("in_item");
                        var75_69 = var1_1.getClassOrVar("in_item");
                        var73_67.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<yellow>\u2726 <white>"), (ScriptValue)(var74_68 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var74_68, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" <gray>x")), (ScriptValue)(var75_69 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var75_69, (ScriptContext)var1_1) : ScriptValue.NULL)));
                        var76_70 = ScriptFormula.callBuiltin((String)"push", var73_67, (ScriptContext)var1_1);
                        var0.val("lines", var76_70);
                    }
                }
            }
            v2 /* !! */  = (var77_71 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var77_71, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v2 /* !! */ .asNum() > 0.0) ** GOTO lbl-1000
            var78_72 = var1_1.getClassOrVar("recipe");
            v3 /* !! */  = var78_72 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var78_72, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v3 /* !! */ .asNum() > 0.0)) {
                v4 = false;
            } else lbl-1000:
            // 2 sources

            {
                v4 = true;
            }
            if (v4 != false || ((var79_73 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fuel_required", (ScriptValue)var79_73, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
            var80_74 = var1_1.getClassOrVar("recipe");
            v5 /* !! */  = var80_74 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var80_74, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v5 /* !! */ .asNum() > 0.0)) {
                v6 = false;
            } else lbl-1000:
            // 2 sources

            {
                v6 = true;
            }
            var81_75 = v6;
            var82_76 = ScriptValue.of((boolean)v6);
            var0.val("has_power_line", var82_76);
            if (var81_75) {
                var83_77 = new ArrayList<ScriptValue>();
                var83_77.add(var1_1.getClassOrVar("lines"));
                var84_78 = ScriptContext.builder().copyFrom(var1_1);
                var83_77.add(RecipeInfo._div(var84_78));
                var85_79 = ScriptFormula.callBuiltin((String)"push", var83_77, (ScriptContext)var1_1);
                var0.val("lines", var85_79);
                var86_80 = new ArrayList<ScriptValue>();
                var86_80.add(var1_1.getClassOrVar("lines"));
                var86_80.add(ScriptValue.of((String)"<white><b>POWER"));
                var87_81 = ScriptFormula.callBuiltin((String)"push", var86_80, (ScriptContext)var1_1);
                var0.val("lines", var87_81);
            }
            var88_82 = new ArrayList<ScriptValue>();
            var88_82.add(var1_1.getClassOrVar("lines"));
            v7 = ScriptValue.of((String)"<gray>\u23f1 Time: <white>");
            var89_83 = 10.0;
            if (10.0 == 0.0) {
                v8 = 0.0;
            } else {
                var91_84 = new ArrayList<ScriptValue>();
                var92_85 = 20.0;
                var91_84.add(ScriptValue.of((double)((20.0 == 0.0 ? 0.0 : var6_6 / var92_85) * 10.0)));
                v8 = ScriptFormula.callBuiltin((String)"round", var91_84, (ScriptContext)var1_1).asNum() / var89_83;
            }
            var88_82.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)v7, (ScriptValue)ScriptValue.of((double)v8)), (ScriptValue)ScriptValue.of((String)"s")));
            var94_86 = ScriptFormula.callBuiltin((String)"push", var88_82, (ScriptContext)var1_1);
            var0.val("lines", var94_86);
            var95_87 = var1_1.getClassOrVar("recipe");
            v9 /* !! */  = var95_87 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var95_87, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v9 /* !! */ .asNum() > 0.0) {
                var96_88 = new ArrayList<ScriptValue>();
                var96_88.add(var1_1.getClassOrVar("lines"));
                var97_89 = var1_1.getClassOrVar("recipe");
                var96_88.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>\u2699 RPM: <white>"), (ScriptValue)(var97_89 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var97_89, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" <dark_gray>min")));
                var98_90 = ScriptFormula.callBuiltin((String)"push", var96_88, (ScriptContext)var1_1);
                var0.val("lines", var98_90);
            }
            v10 /* !! */  = (var99_91 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var99_91, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v10 /* !! */ .asNum() > 0.0) {
                var100_92 = new ArrayList<ScriptValue>();
                var100_92.add(var1_1.getClassOrVar("lines"));
                var101_93 = var1_1.getClassOrVar("recipe");
                var100_92.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>\u26ed SU: <aqua>"), (ScriptValue)(var101_93 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var101_93, (ScriptContext)var1_1) : ScriptValue.NULL)));
                var102_94 = ScriptFormula.callBuiltin((String)"push", var100_92, (ScriptContext)var1_1);
                var0.val("lines", var102_94);
            }
            if (((var103_95 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fuel_required", (ScriptValue)var103_95, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
                var104_96 = var1_1.getClassOrVar("Machine");
                v11 /* !! */  = var104_96 != ScriptValue.NULL ? (var104_96 instanceof ScriptValue.Obj && (var106_98 = (var105_97 = (ScriptValue.Obj)var104_96).instance()) != null && !(var106_98 instanceof PolyClass) && var105_97.typeName().equals("Machine") ? new PolyClassMachine_v2(var106_98).pg$164_burn_time() : PolyDispatch.bootstrapGet("memberGet", "burn_time", (ScriptValue)var104_96, (ScriptContext)var1_1)) : ScriptValue.NULL;
                if (v11 /* !! */ .asNum() > 0.0) {
                    var109_99 = var1_1.getClassOrVar("Machine");
                    var107_102 = Math.max(1.0, (var109_99 != ScriptValue.NULL ? (var109_99 instanceof ScriptValue.Obj && (var111_101 = (var110_100 = (ScriptValue.Obj)var109_99).instance()) != null && !(var111_101 instanceof PolyClass) && var110_100.typeName().equals("Machine") ? new PolyClassMachine_v2(var111_101).pg$163_max_burn_time() : PolyDispatch.bootstrapGet("memberGet", "max_burn_time", (ScriptValue)var109_99, (ScriptContext)var1_1)) : ScriptValue.NULL).asNum());
                    v12 = var107_102 == 0.0 ? 0.0 : ((var112_103 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? (var112_103 instanceof ScriptValue.Obj && (var114_105 = (var113_104 = (ScriptValue.Obj)var112_103).instance()) != null && !(var114_105 instanceof PolyClass) && var113_104.typeName().equals("Machine") ? new PolyClassMachine_v2(var114_105).pg$164_burn_time() : PolyDispatch.bootstrapGet("memberGet", "burn_time", (ScriptValue)var112_103, (ScriptContext)var1_1)) : ScriptValue.NULL).asNum() / var107_102;
                    var115_106 = Math.floor(v12 * 100.0);
                    var117_107 = ScriptValue.of((double)var115_106);
                    var0.val("fuel_pct", var117_107);
                    var118_108 = new ArrayList<ScriptValue>();
                    var118_108.add(var1_1.getClassOrVar("lines"));
                    var118_108.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>\ud83d\udd25 Fuel: <gold>"), (ScriptValue)ScriptValue.of((double)var115_106)), (ScriptValue)ScriptValue.of((String)"% <dark_gray>burning")));
                    var119_109 = ScriptFormula.callBuiltin((String)"push", var118_108, (ScriptContext)var1_1);
                    var0.val("lines", var119_109);
                } else {
                    var120_110 = new ArrayList<ScriptValue>();
                    var120_110.add(var1_1.getClassOrVar("lines"));
                    var120_110.add(ScriptValue.of((String)"<gray>\ud83d\udd25 Fuel: <red>needed"));
                    var121_111 = ScriptFormula.callBuiltin((String)"push", var120_110, (ScriptContext)var1_1);
                    var0.val("lines", var121_111);
                }
            }
            v13 /* !! */  = (var122_112 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var122_112, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v13 /* !! */ .asNum() > 0.0)) break block24;
            var123_113 = new ArrayList<ScriptValue>();
            var123_113.add(var1_1.getClassOrVar("lines"));
            var124_114 = var1_1.getClassOrVar("Machine");
            var127_117 = var1_1.getClassOrVar("Machine");
            var130_120 = var1_1.getClassOrVar("recipe");
            var123_113.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>\u26a1 Energy: <white>"), (ScriptValue)(var124_114 != ScriptValue.NULL ? (var124_114 instanceof ScriptValue.Obj && (var126_116 = (var125_115 = (ScriptValue.Obj)var124_114).instance()) != null && !(var126_116 instanceof PolyClass) && var125_115.typeName().equals("Machine") ? new PolyClassMachine_v2(var126_116).pg$122_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)var124_114, (ScriptContext)var1_1)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)"<dark_gray>/<white>")), (ScriptValue)(var127_117 != ScriptValue.NULL ? (var127_117 instanceof ScriptValue.Obj && (var129_119 = (var128_118 = (ScriptValue.Obj)var127_117).instance()) != null && !(var129_119 instanceof PolyClass) && var128_118.typeName().equals("Machine") ? new PolyClassMachine_v2(var129_119).pg$144_energy_capacity() : PolyDispatch.bootstrapGet("memberGet", "energy_capacity", (ScriptValue)var127_117, (ScriptContext)var1_1)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" <gray>(-")), (ScriptValue)(var130_120 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var130_120, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)"/t)")));
            var131_121 = ScriptFormula.callBuiltin((String)"push", var123_113, (ScriptContext)var1_1);
            var0.val("lines", var131_121);
        }
        return var1_1.getClassOrVar("lines");
    }
}
