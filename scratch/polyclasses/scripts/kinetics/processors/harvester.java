/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Harvester {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _harvestColumnAbove(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("block");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("world", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("block");
        CallSite callSite = PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("x", (ScriptValue)callSite);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("block");
        CallSite callSite2 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("y", (ScriptValue)callSite2);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("block");
        CallSite callSite3 = PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("z", (ScriptValue)callSite3);
        ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)callSite2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Harvester.class, 1.0)));
        builder.val("cy", scriptValue6);
        int n = 0;
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("drop");
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("drops");
        ScriptValue scriptValue9 = scriptValue6;
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("above");
        while (n < 1000) {
            ScriptValue scriptValue11;
            ScriptValue scriptValue12;
            ++n;
            if (!true) break;
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("world");
            ScriptValue scriptValue14 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue13, (ScriptValue)callSite, (ScriptValue)scriptValue9, (ScriptValue)callSite3, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("above", scriptValue14);
            scriptValue10 = scriptValue14;
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue10, (ScriptValue)scriptContext.getClassOrVar("null")) || ((scriptValue12 = scriptContext.getClassOrVar("COLUMN_PLANTS")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue12, (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Harvester.class, 0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) break;
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("above");
            ScriptValue scriptValue16 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "break_and_drop", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("drops", scriptValue16);
            scriptValue8 = scriptValue16;
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue8);
            if (list != null) {
                for (ScriptValue scriptValue17 : list) {
                    builder.val("drop", scriptValue17);
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                    builder2.val("item", scriptValue17);
                    Utils.1._deposit((ScriptContext.Builder)builder2);
                }
            }
            Object object = (scriptValue11 = scriptContext.getClassOrVar("world")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)scriptValue11, (ScriptValue)callSite, (ScriptValue)scriptValue9, (ScriptValue)callSite3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:air")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue18 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Harvester.class, 1.0)));
            builder.val("cy", scriptValue18);
            scriptValue9 = scriptValue18;
        }
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue _tryHarvest(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("block"), (ScriptValue)var1_1.getClassOrVar("null"))) {
            return ScriptValue.NULL;
        }
        var2_2 = var1_1.getClassOrVar("COLUMN_PLANTS");
        if ((var2_2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var2_2, (ScriptValue)((var3_3 = var1_1.getClassOrVar("block")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Harvester.class, 0)), (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
            var4_4 = ScriptContext.builder().copyFrom(var1_1);
            var4_4.val("block", var1_1.getClassOrVar("block"));
            Harvester._harvestColumnAbove(var4_4);
            return ScriptValue.NULL;
        }
        var5_5 = var1_1.getClassOrVar("block");
        if ((var5_5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)var5_5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "age")), (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        var6_6 = var1_1.getClassOrVar("CROP_MAX_AGE");
        var8_8 = var6_6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var6_6, (ScriptValue)((var7_7 = var1_1.getClassOrVar("block")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var7_7, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)(-1.0)), (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("max_age", var8_8);
        if (var8_8.asNum() < 0.0) ** GOTO lbl-1000
        var9_9 = var1_1.getClassOrVar("block");
        v0 /* !! */  = var9_9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var9_9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "age")), (ScriptContext)var1_1) : ScriptValue.NULL;
        if (!(v0 /* !! */ .asNum() < var8_8.asNum())) {
            v1 = false;
        } else lbl-1000:
        // 2 sources

        {
            v1 = true;
        }
        if (v1) {
            return ScriptValue.NULL;
        }
        var10_10 = var1_1.getClassOrVar("block");
        var11_11 = var10_10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var10_10, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("crop_id", var11_11);
        var12_12 = var1_1.getClassOrVar("block");
        var13_13 = PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(var12_12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var12_12, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        var0.val("x", (ScriptValue)var13_13);
        var14_14 = var1_1.getClassOrVar("block");
        var15_15 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var14_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var14_14, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        var0.val("y", (ScriptValue)var15_15);
        var16_16 = var1_1.getClassOrVar("block");
        var17_17 = PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(var16_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var16_16, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        var0.val("z", (ScriptValue)var17_17);
        var18_18 = var1_1.getClassOrVar("block");
        var19_19 = var18_18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)var18_18, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("world", var19_19);
        var20_20 = var1_1.getClassOrVar("block");
        var21_21 = var20_20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "break_and_drop", (ScriptValue)var20_20, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("drops", var21_21);
        var22_22 = ScriptProgram.elementsOf((ScriptValue)var21_21);
        if (var22_22 != null) {
            for (ScriptValue var24_24 : var22_22) {
                var0.val("drop", var24_24);
                var25_25 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                var25_25.val("item", var24_24);
                Utils.1._deposit((ScriptContext.Builder)var25_25);
            }
        }
        v2 /* !! */  = (var26_26 = var1_1.getClassOrVar("world")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)var26_26, (ScriptValue)var13_13, (ScriptValue)var15_15, (ScriptValue)var17_17, (ScriptValue)var11_11, (ScriptContext)var1_1) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_deposit");
        arrayList.add("_update_activated");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"kinetics/utils.pf", null, arrayList);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:wheat"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Harvester.class, 7.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:carrots"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Harvester.class, 7.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:potatoes"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Harvester.class, 7.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:beetroots"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Harvester.class, 3.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:nether_wart"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Harvester.class, 3.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:cocoa"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Harvester.class, 2.0));
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext);
        builder.val("CROP_MAX_AGE", scriptValue2);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:sugar_cane"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Harvester.class, 1));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:bamboo"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Harvester.class, 1));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:kelp"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Harvester.class, 1));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "minecraft:kelp_plant"));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Harvester.class, 1));
        ScriptValue scriptValue3 = ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext);
        builder.val("COLUMN_PLANTS", scriptValue3);
        PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$191_contraption() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("contraption", scriptValue4);
        double d = 0.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)0.0);
        builder.val("is_now", scriptValue5);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            boolean bl = ScriptFormula.valuesEqual((ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Harvester.class, 0.0))) ^ true;
            ScriptValue scriptValue6 = ScriptValue.of((boolean)bl);
            builder.val("is_rotational", scriptValue6);
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("contraption");
            ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("is_linear", scriptValue8);
            double d2 = bl || scriptValue8.asBool() ? 1.0 : 0.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)d2);
            builder.val("is_now", scriptValue9);
            if (d2 > 0.0) {
                ScriptValue scriptValue10;
                ScriptValue scriptValue11;
                ScriptValue scriptValue12;
                ScriptValue scriptValue13;
                ScriptValue scriptValue14;
                ScriptValue scriptValue15;
                PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                Object object = polyClassMachine_v42 != null ? polyClassMachine_v42.pg$200_x() : ((scriptValue15 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL);
                PolyClassMachine_v4 polyClassMachine_v43 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v4 polyClassMachine_v44 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v4 polyClassMachine_v45 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v4 polyClassMachine_v46 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                PolyClassMachine_v4 polyClassMachine_v47 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)object, (ScriptValue)(polyClassMachine_v43 != null ? polyClassMachine_v43.pg$133_facing_dx() : ((scriptValue14 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v44 != null ? polyClassMachine_v44.pg$202_y() : ((scriptValue13 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v45 != null ? polyClassMachine_v45.pg$129_facing_dy() : ((scriptValue12 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v46 != null ? polyClassMachine_v46.pg$206_z() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)(polyClassMachine_v47 != null ? polyClassMachine_v47.pg$131_facing_dz() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL))), (ScriptContext)scriptContext);
                builder.val("target", (ScriptValue)callSite);
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("block", (ScriptValue)callSite);
                Harvester._tryHarvest(builder2);
            }
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Harvester.class, 0.0))) ^ true) {
            ScriptValue scriptValue16;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            PolyClassMachine_v4 polyClassMachine_v48 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            builder3.val("block", (ScriptValue)(polyClassMachine_v48 != null ? polyClassMachine_v48.pg$137_facing_block() : ((scriptValue16 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL)));
            Harvester._tryHarvest(builder3);
            double d3 = 1.0;
            ScriptValue scriptValue17 = ScriptValue.of((double)1.0);
            builder.val("is_now", scriptValue17);
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder4.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Harvester.class, "_harvester_act"));
        builder4.val("is_now", scriptContext.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)builder4);
        FILE_SCOPE = builder.build();
    }
}
