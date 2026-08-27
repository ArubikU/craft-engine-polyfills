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
import java.util.ArrayList;

public final class Harvester {
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
    public static ScriptValue _harvestColumnAbove(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = var1_1.getClassOrVar("block");
        var3_3 = var2_2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)var2_2, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("world", var3_3);
        var4_4 = var1_1.getClassOrVar("block");
        var5_5 = PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(var4_4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var4_4, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        var0.val("x", (ScriptValue)var5_5);
        var6_6 = var1_1.getClassOrVar("block");
        var7_7 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var6_6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var6_6, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        var0.val("y", (ScriptValue)var7_7);
        var8_8 = var1_1.getClassOrVar("block");
        var9_9 = PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(var8_8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var8_8, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        var0.val("z", (ScriptValue)var9_9);
        var10_10 = ScriptFormula.addPolymorphic((ScriptValue)var7_7, (ScriptValue)ScriptValue.of((double)1.0));
        var0.val("cy", var10_10);
        var11_11 = 0;
        while (var11_11 < 1000) {
            ++var11_11;
            if (!true) break;
            var12_12 = var1_1.getClassOrVar("world");
            if (var12_12 != ScriptValue.NULL) {
                var13_13 = new ArrayList<CallSite>();
                var13_13.add(var5_5);
                var13_13.add((CallSite)var1_1.getClassOrVar("cy"));
                var13_13.add(var9_9);
                v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)var12_12, var13_13, (ScriptContext)var1_1);
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var14_14 = v0 /* !! */ ;
            var0.val("above", var14_14);
            if (ScriptFormula.valuesEqual((ScriptValue)var14_14, (ScriptValue)var1_1.getClassOrVar("null"))) ** GOTO lbl-1000
            var15_15 = var1_1.getClassOrVar("COLUMN_PLANTS");
            if (var15_15 != ScriptValue.NULL) {
                var16_16 = new ArrayList<ScriptValue>();
                var17_17 = var1_1.getClassOrVar("above");
                var16_16.add((ScriptValue)(var17_17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var17_17, (ScriptContext)var1_1) : ScriptValue.NULL));
                var16_16.add(ScriptValue.of((boolean)false));
                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var15_15, var16_16, (ScriptContext)var1_1);
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            if (!(v1 /* !! */ .asBool() ^ true)) {
                v2 = false;
            } else lbl-1000:
            // 2 sources

            {
                v2 = true;
            }
            if (v2) break;
            var18_18 = var1_1.getClassOrVar("above");
            if (var18_18 != ScriptValue.NULL) {
                var19_19 = new ArrayList<E>();
                v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "break_and_drop", (ScriptValue)var18_18, var19_19, (ScriptContext)var1_1);
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            var20_20 = v3 /* !! */ ;
            var0.val("drops", var20_20);
            var21_21 = ScriptProgram.elementsOf((ScriptValue)var20_20);
            if (var21_21 != null) {
                for (ScriptValue var23_23 : var21_21) {
                    var0.val("drop", var23_23);
                    var24_24 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                    var24_24.val("item", var1_1.getClassOrVar("drop"));
                    Utils.1._deposit((ScriptContext.Builder)var24_24);
                }
            }
            if ((var25_25 = var1_1.getClassOrVar("world")) != ScriptValue.NULL) {
                var26_26 = new ArrayList<CallSite>();
                var26_26.add(var5_5);
                var26_26.add((CallSite)var1_1.getClassOrVar("cy"));
                var26_26.add(var9_9);
                var26_26.add((CallSite)ScriptValue.of((String)"minecraft:air"));
                v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)var25_25, var26_26, (ScriptContext)var1_1);
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            var27_27 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("cy"), (ScriptValue)ScriptValue.of((double)1.0));
            var0.val("cy", var27_27);
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
        if (var2_2 != ScriptValue.NULL) {
            var3_3 = new ArrayList<ScriptValue>();
            var4_4 = var1_1.getClassOrVar("block");
            var3_3.add((ScriptValue)(var4_4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var4_4, (ScriptContext)var1_1) : ScriptValue.NULL));
            var3_3.add(ScriptValue.of((boolean)false));
            v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var2_2, var3_3, (ScriptContext)var1_1);
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        if (v0 /* !! */ .asBool()) {
            var5_5 = ScriptContext.builder().copyFrom(var1_1);
            var5_5.val("block", var1_1.getClassOrVar("block"));
            Harvester._harvestColumnAbove(var5_5);
            return ScriptValue.NULL;
        }
        var6_6 = var1_1.getClassOrVar("block");
        if (var6_6 != ScriptValue.NULL) {
            var7_7 = new ArrayList<ScriptValue>();
            var7_7.add(ScriptValue.of((String)"age"));
            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)var6_6, var7_7, (ScriptContext)var1_1);
        } else {
            v1 /* !! */  = ScriptValue.NULL;
        }
        if (v1 /* !! */ .asBool() ^ true) {
            return ScriptValue.NULL;
        }
        var8_8 = var1_1.getClassOrVar("CROP_MAX_AGE");
        if (var8_8 != ScriptValue.NULL) {
            var9_9 = new ArrayList<ScriptValue>();
            var10_10 = var1_1.getClassOrVar("block");
            var9_9.add((ScriptValue)(var10_10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var10_10, (ScriptContext)var1_1) : ScriptValue.NULL));
            var9_9.add(ScriptValue.of((double)(-1.0)));
            v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var8_8, var9_9, (ScriptContext)var1_1);
        } else {
            v2 /* !! */  = ScriptValue.NULL;
        }
        var11_11 = v2 /* !! */ ;
        var0.val("max_age", var11_11);
        if (var11_11.asNum() < 0.0) ** GOTO lbl-1000
        var12_12 = var1_1.getClassOrVar("block");
        if (var12_12 != ScriptValue.NULL) {
            var13_13 = new ArrayList<ScriptValue>();
            var13_13.add(ScriptValue.of((String)"age"));
            v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var12_12, var13_13, (ScriptContext)var1_1);
        } else {
            v3 /* !! */  = ScriptValue.NULL;
        }
        if (!(v3 /* !! */ .asNum() < var11_11.asNum())) {
            v4 = false;
        } else lbl-1000:
        // 2 sources

        {
            v4 = true;
        }
        if (v4) {
            return ScriptValue.NULL;
        }
        var14_14 = var1_1.getClassOrVar("block");
        var15_15 = var14_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var14_14, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("crop_id", var15_15);
        var16_16 = var1_1.getClassOrVar("block");
        var17_17 = PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(var16_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var16_16, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        var0.val("x", (ScriptValue)var17_17);
        var18_18 = var1_1.getClassOrVar("block");
        var19_19 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var18_18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var18_18, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        var0.val("y", (ScriptValue)var19_19);
        var20_20 = var1_1.getClassOrVar("block");
        var21_21 = PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(var20_20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var20_20, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
        var0.val("z", (ScriptValue)var21_21);
        var22_22 = var1_1.getClassOrVar("block");
        var23_23 = var22_22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)var22_22, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("world", var23_23);
        var24_24 = var1_1.getClassOrVar("block");
        if (var24_24 != ScriptValue.NULL) {
            var25_25 = new ArrayList<E>();
            v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "break_and_drop", (ScriptValue)var24_24, var25_25, (ScriptContext)var1_1);
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        var26_26 = v5 /* !! */ ;
        var0.val("drops", var26_26);
        var27_27 = ScriptProgram.elementsOf((ScriptValue)var26_26);
        if (var27_27 != null) {
            for (ScriptValue var29_29 : var27_27) {
                var0.val("drop", var29_29);
                var30_30 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                var30_30.val("item", var1_1.getClassOrVar("drop"));
                Utils.1._deposit((ScriptContext.Builder)var30_30);
            }
        }
        if ((var31_31 = var1_1.getClassOrVar("world")) != ScriptValue.NULL) {
            var32_32 = new ArrayList<CallSite>();
            var32_32.add(var17_17);
            var32_32.add(var19_19);
            var32_32.add(var21_21);
            var32_32.add((CallSite)var15_15);
            v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)var31_31, var32_32, (ScriptContext)var1_1);
        } else {
            v6 /* !! */  = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("_deposit");
        arrayList.add("_update_activated");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"kinetics/utils.pf", null, arrayList);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(ScriptValue.of((String)"minecraft:wheat"));
        arrayList2.add(ScriptValue.of((double)7.0));
        arrayList2.add(ScriptValue.of((String)"minecraft:carrots"));
        arrayList2.add(ScriptValue.of((double)7.0));
        arrayList2.add(ScriptValue.of((String)"minecraft:potatoes"));
        arrayList2.add(ScriptValue.of((double)7.0));
        arrayList2.add(ScriptValue.of((String)"minecraft:beetroots"));
        arrayList2.add(ScriptValue.of((double)3.0));
        arrayList2.add(ScriptValue.of((String)"minecraft:nether_wart"));
        arrayList2.add(ScriptValue.of((double)3.0));
        arrayList2.add(ScriptValue.of((String)"minecraft:cocoa"));
        arrayList2.add(ScriptValue.of((double)2.0));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext);
        builder.val("CROP_MAX_AGE", scriptValue);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(ScriptValue.of((String)"minecraft:sugar_cane"));
        arrayList3.add(ScriptValue.of((boolean)true));
        arrayList3.add(ScriptValue.of((String)"minecraft:bamboo"));
        arrayList3.add(ScriptValue.of((boolean)true));
        arrayList3.add(ScriptValue.of((String)"minecraft:kelp"));
        arrayList3.add(ScriptValue.of((boolean)true));
        arrayList3.add(ScriptValue.of((String)"minecraft:kelp_plant"));
        arrayList3.add(ScriptValue.of((boolean)true));
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext);
        builder.val("COLUMN_PLANTS", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v4.pg$190_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("contraption", scriptValue4);
        double d = 0.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)0.0);
        builder.val("is_now", scriptValue5);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            Object object;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("contraption");
            boolean bl = ScriptFormula.valuesEqual((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.0)) ^ true;
            ScriptValue scriptValue7 = ScriptValue.of((boolean)bl);
            builder.val("is_rotational", scriptValue7);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("contraption");
            if (scriptValue8 != ScriptValue.NULL) {
                ArrayList arrayList4 = new ArrayList();
                object = PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)scriptValue8, arrayList4, (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = object;
            builder.val("is_linear", scriptValue9);
            double d2 = bl || scriptValue9.asBool() ? 1.0 : 0.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)d2);
            builder.val("is_now", scriptValue10);
            if (d2 > 0.0) {
                PolyClassMachine_v4 polyClassMachine_v42;
                PolyClassMachine_v4 polyClassMachine_v43;
                PolyClassMachine_v4 polyClassMachine_v44;
                PolyClassMachine_v4 polyClassMachine_v45;
                PolyClassMachine_v4 polyClassMachine_v46;
                PolyClassMachine_v4 polyClassMachine_v47;
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                arrayList5.add(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue12 != ScriptValue.NULL ? ((polyClassMachine_v47 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue12)) != null ? polyClassMachine_v47.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue11 != ScriptValue.NULL ? ((polyClassMachine_v46 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassMachine_v46.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL)));
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                arrayList5.add(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue13 != ScriptValue.NULL ? ((polyClassMachine_v45 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue13)) != null ? polyClassMachine_v45.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue13, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue14 != ScriptValue.NULL ? ((polyClassMachine_v44 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine_v44.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL)));
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
                arrayList5.add(ScriptFormula.addPolymorphic((ScriptValue)(scriptValue15 != ScriptValue.NULL ? ((polyClassMachine_v43 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue15)) != null ? polyClassMachine_v43.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue15, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue16 != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue16)) != null ? polyClassMachine_v42.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue16, (ScriptContext)scriptContext)) : ScriptValue.NULL)));
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("contraption");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL), arrayList5, (ScriptContext)scriptContext);
                builder.val("target", (ScriptValue)callSite);
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("block", (ScriptValue)callSite);
                Harvester._tryHarvest(builder2);
            }
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("rpm"), (ScriptValue)ScriptValue.of((double)0.0)) ^ true) {
            PolyClassMachine_v4 polyClassMachine_v48;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
            builder3.val("block", (ScriptValue)(scriptValue18 != ScriptValue.NULL ? ((polyClassMachine_v48 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassMachine_v48.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            Harvester._tryHarvest(builder3);
            double d3 = 1.0;
            ScriptValue scriptValue19 = ScriptValue.of((double)1.0);
            builder.val("is_now", scriptValue19);
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
        builder4.val("act_key", ScriptValue.of((String)"_harvester_act"));
        builder4.val("is_now", scriptContext.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)builder4);
        FILE_SCOPE = builder.build();
    }
}
