/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassMachine_v4;
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
            ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v4(object2).pg$143_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("recipe", scriptValue2);
            if (!ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null"))) break block0;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$157_matching_recipe() : PolyDispatch.bootstrapGet("memberGet", "matching_recipe", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
        Object object2 = scriptValue7 != ScriptValue.NULL ? (scriptValue7 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$143_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ScriptValue.Obj obj2;
            Object object3;
            ScriptValue scriptValue8;
            return ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)" <yellow>")), (ScriptValue)ScriptValue.of((double)Math.floor(((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? (scriptValue8 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v4(object3).pg$135_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum()))), (ScriptValue)ScriptValue.of((String)"%"));
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
            var17_14 = ScriptFormula.valuesEqual((ScriptValue)(var14_11 != ScriptValue.NULL ? (var14_11 instanceof ScriptValue.Obj && (var16_13 = (var15_12 = (ScriptValue.Obj)var14_11).instance()) != null && !(var16_13 instanceof PolyClass) && var15_12.typeName().equals("Machine") ? new PolyClassMachine_v4(var16_13).pg$143_working_recipe() : PolyDispatch.bootstrapGet("memberGet", "working_recipe", (ScriptValue)var14_11, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)var1_1.getClassOrVar("null")) ^ true;
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
                var22_19.val("percent", (ScriptValue)(var23_20 != ScriptValue.NULL ? (var23_20 instanceof ScriptValue.Obj && (var25_22 = (var24_21 = (ScriptValue.Obj)var23_20).instance()) != null && !(var25_22 instanceof PolyClass) && var24_21.typeName().equals("Machine") ? new PolyClassMachine_v4(var25_22).pg$135_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)var23_20, (ScriptContext)var1_1)) : ScriptValue.NULL));
                var22_19.val("width", ScriptValue.of((double)14.0));
                var22_19.val("full_color", ScriptValue.of((String)"<green>"));
                var22_19.val("empty_color", ScriptValue.of((String)"<dark_gray>"));
                var26_23 = var1_1.getClassOrVar("Machine");
                var21_18.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)RecipeInfo._bar(var22_19), (ScriptValue)ScriptValue.of((String)" <white>")), (ScriptValue)ScriptValue.of((double)Math.floor((var26_23 != ScriptValue.NULL ? (var26_23 instanceof ScriptValue.Obj && (var28_25 = (var27_24 = (ScriptValue.Obj)var26_23).instance()) != null && !(var28_25 instanceof PolyClass) && var27_24.typeName().equals("Machine") ? new PolyClassMachine_v4(var28_25).pg$135_progress_percent() : PolyDispatch.bootstrapGet("memberGet", "progress_percent", (ScriptValue)var26_23, (ScriptContext)var1_1)) : ScriptValue.NULL).asNum()))), (ScriptValue)ScriptValue.of((String)"%")));
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
            var43_36 = var1_1.getClassOrVar("recipe");
            var40_37 = ScriptProgram.rowsOf((ScriptValue)(var43_36 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)var43_36, (ScriptContext)var1_1) : ScriptValue.NULL), (int)1);
            if (var40_37 != null) {
                for (ScriptValue[] var42_39 : var40_37) {
                    var0.val("out_item", var42_39.length > 0 ? var42_39[0] : ScriptValue.NULL);
                    var44_40 = new ArrayList<ScriptValue>();
                    var44_40.add(var1_1.getClassOrVar("out_item"));
                    if (ScriptFormula.callBuiltin((String)"is_empty", var44_40, (ScriptContext)var1_1).asBool() ^ true) {
                        var45_41 = new ArrayList<ScriptValue>();
                        var45_41.add(var1_1.getClassOrVar("out_i"));
                        var46_42 = var1_1.getClassOrVar("recipe");
                        var47_43 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)(var46_42 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "output_chances", (ScriptValue)var46_42, (ScriptContext)var1_1) : ScriptValue.NULL), var45_41, (ScriptContext)var1_1);
                        var0.val("chance", (ScriptValue)var47_43);
                        var48_44 = 10.0;
                        if (10.0 == 0.0) {
                            v0 = 0.0;
                        } else {
                            var50_45 = new ArrayList<ScriptValue>();
                            var51_46 = var1_1.getClassOrVar("out_item");
                            var50_45.add(ScriptValue.of((double)(var11_9 * (var51_46 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var51_46, (ScriptContext)var1_1) : ScriptValue.NULL).asNum() * var47_43.asNum() * 10.0)));
                            v0 = ScriptFormula.callBuiltin((String)"round", var50_45, (ScriptContext)var1_1).asNum() / var48_44;
                        }
                        var52_47 = v0;
                        var54_48 = ScriptValue.of((double)v0);
                        var0.val("per_min", var54_48);
                        var55_49 = var1_1.getClassOrVar("out_item");
                        var56_50 = var1_1.getClassOrVar("out_item");
                        var57_51 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<green>\u27a4 <white>"), (ScriptValue)(var55_49 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var55_49, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" <gray>x")), (ScriptValue)(var56_50 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var56_50, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var0.val("line", var57_51);
                        if (var47_43.asNum() < 1.0) {
                            var58_52 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("line"), (ScriptValue)ScriptValue.of((String)" <gold>(")), (ScriptValue)ScriptValue.of((double)Math.floor(var47_43.asNum() * 100.0))), (ScriptValue)ScriptValue.of((String)"%)"));
                            var0.val("line", var58_52);
                        }
                        var59_53 = new ArrayList<ScriptValue>();
                        var59_53.add(var1_1.getClassOrVar("lines"));
                        var59_53.add(var1_1.getClassOrVar("line"));
                        var60_54 = ScriptFormula.callBuiltin((String)"push", var59_53, (ScriptContext)var1_1);
                        var0.val("lines", var60_54);
                        var61_55 = new ArrayList<ScriptValue>();
                        var61_55.add(var1_1.getClassOrVar("lines"));
                        var61_55.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<dark_gray>   "), (ScriptValue)ScriptValue.of((double)var52_47)), (ScriptValue)ScriptValue.of((String)"/min")));
                        var62_56 = ScriptFormula.callBuiltin((String)"push", var61_55, (ScriptContext)var1_1);
                        var0.val("lines", var62_56);
                    }
                    var63_57 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("out_i"), (ScriptValue)ScriptValue.of((double)1.0));
                    var0.val("out_i", var63_57);
                }
            }
            v1 /* !! */  = (var64_58 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var64_58, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (PolyDispatch.bootstrapGet("memberGet", "length", (ScriptValue)v1 /* !! */ , (ScriptContext)var1_1).asNum() > 0.0) {
                var65_59 = new ArrayList<ScriptValue>();
                var65_59.add(var1_1.getClassOrVar("lines"));
                var66_60 = ScriptContext.builder().copyFrom(var1_1);
                var65_59.add(RecipeInfo._div(var66_60));
                var67_61 = ScriptFormula.callBuiltin((String)"push", var65_59, (ScriptContext)var1_1);
                var0.val("lines", var67_61);
                var68_62 = new ArrayList<ScriptValue>();
                var68_62.add(var1_1.getClassOrVar("lines"));
                var68_62.add(ScriptValue.of((String)"<white><b>INGREDIENTS"));
                var69_63 = ScriptFormula.callBuiltin((String)"push", var68_62, (ScriptContext)var1_1);
                var0.val("lines", var69_63);
                var73_64 = var1_1.getClassOrVar("recipe");
                var70_65 = ScriptProgram.rowsOf((ScriptValue)(var73_64 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var73_64, (ScriptContext)var1_1) : ScriptValue.NULL), (int)1);
                if (var70_65 != null) {
                    for (ScriptValue[] var72_67 : var70_65) {
                        var0.val("in_item", var72_67.length > 0 ? var72_67[0] : ScriptValue.NULL);
                        var74_68 = new ArrayList<ScriptValue>();
                        var74_68.add(var1_1.getClassOrVar("in_item"));
                        if (!(ScriptFormula.callBuiltin((String)"is_empty", var74_68, (ScriptContext)var1_1).asBool() ^ true)) continue;
                        var75_69 = new ArrayList<ScriptValue>();
                        var75_69.add(var1_1.getClassOrVar("lines"));
                        var76_70 = var1_1.getClassOrVar("in_item");
                        var77_71 = var1_1.getClassOrVar("in_item");
                        var75_69.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<yellow>\u2726 <white>"), (ScriptValue)(var76_70 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var76_70, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" <gray>x")), (ScriptValue)(var77_71 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var77_71, (ScriptContext)var1_1) : ScriptValue.NULL)));
                        var78_72 = ScriptFormula.callBuiltin((String)"push", var75_69, (ScriptContext)var1_1);
                        var0.val("lines", var78_72);
                    }
                }
            }
            v2 /* !! */  = (var79_73 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var79_73, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v2 /* !! */ .asNum() > 0.0) ** GOTO lbl-1000
            var80_74 = var1_1.getClassOrVar("recipe");
            v3 /* !! */  = var80_74 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var80_74, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v3 /* !! */ .asNum() > 0.0)) {
                v4 = false;
            } else lbl-1000:
            // 2 sources

            {
                v4 = true;
            }
            if (v4 != false || ((var81_75 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fuel_required", (ScriptValue)var81_75, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
            var82_76 = var1_1.getClassOrVar("recipe");
            v5 /* !! */  = var82_76 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var82_76, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v5 /* !! */ .asNum() > 0.0)) {
                v6 = false;
            } else lbl-1000:
            // 2 sources

            {
                v6 = true;
            }
            var83_77 = v6;
            var84_78 = ScriptValue.of((boolean)v6);
            var0.val("has_power_line", var84_78);
            if (var83_77) {
                var85_79 = new ArrayList<ScriptValue>();
                var85_79.add(var1_1.getClassOrVar("lines"));
                var86_80 = ScriptContext.builder().copyFrom(var1_1);
                var85_79.add(RecipeInfo._div(var86_80));
                var87_81 = ScriptFormula.callBuiltin((String)"push", var85_79, (ScriptContext)var1_1);
                var0.val("lines", var87_81);
                var88_82 = new ArrayList<ScriptValue>();
                var88_82.add(var1_1.getClassOrVar("lines"));
                var88_82.add(ScriptValue.of((String)"<white><b>POWER"));
                var89_83 = ScriptFormula.callBuiltin((String)"push", var88_82, (ScriptContext)var1_1);
                var0.val("lines", var89_83);
            }
            var90_84 = new ArrayList<ScriptValue>();
            var90_84.add(var1_1.getClassOrVar("lines"));
            v7 = ScriptValue.of((String)"<gray>\u23f1 Time: <white>");
            var91_85 = 10.0;
            if (10.0 == 0.0) {
                v8 = 0.0;
            } else {
                var93_86 = new ArrayList<ScriptValue>();
                var94_87 = 20.0;
                var93_86.add(ScriptValue.of((double)((20.0 == 0.0 ? 0.0 : var6_6 / var94_87) * 10.0)));
                v8 = ScriptFormula.callBuiltin((String)"round", var93_86, (ScriptContext)var1_1).asNum() / var91_85;
            }
            var90_84.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)v7, (ScriptValue)ScriptValue.of((double)v8)), (ScriptValue)ScriptValue.of((String)"s")));
            var96_88 = ScriptFormula.callBuiltin((String)"push", var90_84, (ScriptContext)var1_1);
            var0.val("lines", var96_88);
            var97_89 = var1_1.getClassOrVar("recipe");
            v9 /* !! */  = var97_89 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var97_89, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v9 /* !! */ .asNum() > 0.0) {
                var98_90 = new ArrayList<ScriptValue>();
                var98_90.add(var1_1.getClassOrVar("lines"));
                var99_91 = var1_1.getClassOrVar("recipe");
                var98_90.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>\u2699 RPM: <white>"), (ScriptValue)(var99_91 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "min_rpm", (ScriptValue)var99_91, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" <dark_gray>min")));
                var100_92 = ScriptFormula.callBuiltin((String)"push", var98_90, (ScriptContext)var1_1);
                var0.val("lines", var100_92);
            }
            v10 /* !! */  = (var101_93 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var101_93, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (v10 /* !! */ .asNum() > 0.0) {
                var102_94 = new ArrayList<ScriptValue>();
                var102_94.add(var1_1.getClassOrVar("lines"));
                var103_95 = var1_1.getClassOrVar("recipe");
                var102_94.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>\u26ed SU: <aqua>"), (ScriptValue)(var103_95 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "su_cost", (ScriptValue)var103_95, (ScriptContext)var1_1) : ScriptValue.NULL)));
                var104_96 = ScriptFormula.callBuiltin((String)"push", var102_94, (ScriptContext)var1_1);
                var0.val("lines", var104_96);
            }
            if (((var105_97 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "fuel_required", (ScriptValue)var105_97, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
                var106_98 = var1_1.getClassOrVar("Machine");
                v11 /* !! */  = var106_98 != ScriptValue.NULL ? (var106_98 instanceof ScriptValue.Obj && (var108_100 = (var107_99 = (ScriptValue.Obj)var106_98).instance()) != null && !(var108_100 instanceof PolyClass) && var107_99.typeName().equals("Machine") ? new PolyClassMachine_v4(var108_100).pg$164_burn_time() : PolyDispatch.bootstrapGet("memberGet", "burn_time", (ScriptValue)var106_98, (ScriptContext)var1_1)) : ScriptValue.NULL;
                if (v11 /* !! */ .asNum() > 0.0) {
                    var111_101 = var1_1.getClassOrVar("Machine");
                    var109_104 = Math.max(1.0, (var111_101 != ScriptValue.NULL ? (var111_101 instanceof ScriptValue.Obj && (var113_103 = (var112_102 = (ScriptValue.Obj)var111_101).instance()) != null && !(var113_103 instanceof PolyClass) && var112_102.typeName().equals("Machine") ? new PolyClassMachine_v4(var113_103).pg$163_max_burn_time() : PolyDispatch.bootstrapGet("memberGet", "max_burn_time", (ScriptValue)var111_101, (ScriptContext)var1_1)) : ScriptValue.NULL).asNum());
                    v12 = var109_104 == 0.0 ? 0.0 : ((var114_105 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? (var114_105 instanceof ScriptValue.Obj && (var116_107 = (var115_106 = (ScriptValue.Obj)var114_105).instance()) != null && !(var116_107 instanceof PolyClass) && var115_106.typeName().equals("Machine") ? new PolyClassMachine_v4(var116_107).pg$164_burn_time() : PolyDispatch.bootstrapGet("memberGet", "burn_time", (ScriptValue)var114_105, (ScriptContext)var1_1)) : ScriptValue.NULL).asNum() / var109_104;
                    var117_108 = Math.floor(v12 * 100.0);
                    var119_109 = ScriptValue.of((double)var117_108);
                    var0.val("fuel_pct", var119_109);
                    var120_110 = new ArrayList<ScriptValue>();
                    var120_110.add(var1_1.getClassOrVar("lines"));
                    var120_110.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>\ud83d\udd25 Fuel: <gold>"), (ScriptValue)ScriptValue.of((double)var117_108)), (ScriptValue)ScriptValue.of((String)"% <dark_gray>burning")));
                    var121_111 = ScriptFormula.callBuiltin((String)"push", var120_110, (ScriptContext)var1_1);
                    var0.val("lines", var121_111);
                } else {
                    var122_112 = new ArrayList<ScriptValue>();
                    var122_112.add(var1_1.getClassOrVar("lines"));
                    var122_112.add(ScriptValue.of((String)"<gray>\ud83d\udd25 Fuel: <red>needed"));
                    var123_113 = ScriptFormula.callBuiltin((String)"push", var122_112, (ScriptContext)var1_1);
                    var0.val("lines", var123_113);
                }
            }
            v13 /* !! */  = (var124_114 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var124_114, (ScriptContext)var1_1) : ScriptValue.NULL;
            if (!(v13 /* !! */ .asNum() > 0.0)) break block24;
            var125_115 = new ArrayList<ScriptValue>();
            var125_115.add(var1_1.getClassOrVar("lines"));
            var126_116 = var1_1.getClassOrVar("Machine");
            var129_119 = var1_1.getClassOrVar("Machine");
            var132_122 = var1_1.getClassOrVar("recipe");
            var125_115.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>\u26a1 Energy: <white>"), (ScriptValue)(var126_116 != ScriptValue.NULL ? (var126_116 instanceof ScriptValue.Obj && (var128_118 = (var127_117 = (ScriptValue.Obj)var126_116).instance()) != null && !(var128_118 instanceof PolyClass) && var127_117.typeName().equals("Machine") ? new PolyClassMachine_v4(var128_118).pg$122_energy_stored() : PolyDispatch.bootstrapGet("memberGet", "energy_stored", (ScriptValue)var126_116, (ScriptContext)var1_1)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)"<dark_gray>/<white>")), (ScriptValue)(var129_119 != ScriptValue.NULL ? (var129_119 instanceof ScriptValue.Obj && (var131_121 = (var130_120 = (ScriptValue.Obj)var129_119).instance()) != null && !(var131_121 instanceof PolyClass) && var130_120.typeName().equals("Machine") ? new PolyClassMachine_v4(var131_121).pg$144_energy_capacity() : PolyDispatch.bootstrapGet("memberGet", "energy_capacity", (ScriptValue)var129_119, (ScriptContext)var1_1)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" <gray>(-")), (ScriptValue)(var132_122 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "energy_cost", (ScriptValue)var132_122, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)"/t)")));
            var133_123 = ScriptFormula.callBuiltin((String)"push", var125_115, (ScriptContext)var1_1);
            var0.val("lines", var133_123);
        }
        return var1_1.getClassOrVar("lines");
    }
}
