/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class Depot {
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
        var1_1 = var0.peek();
        var2_2 = 9.0;
        var4_3 = ScriptValue.of((double)9.0);
        var0.val("SIZE", var4_3);
        var5_4 = var1_1.getClassOrVar("Player");
        var8_7 = var5_4 != ScriptValue.NULL ? (var5_4 instanceof ScriptValue.Obj && (var7_6 = (var6_5 = (ScriptValue.Obj)var5_4).instance()) != null && !(var7_6 instanceof PolyClass) && var6_5.typeName().equals("Player") ? new PolyClassPlayer(var7_6).pg$47_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)var5_4, (ScriptContext)var1_1)) : ScriptValue.NULL;
        var0.val("held", var8_7);
        var9_8 = var1_1.getClassOrVar("Player");
        if ((var9_8 != ScriptValue.NULL ? (var9_8 instanceof ScriptValue.Obj && (var11_10 = (var10_9 = (ScriptValue.Obj)var9_8).instance()) != null && !(var11_10 instanceof PolyClass) && var10_9.typeName().equals("Player") ? new PolyClassPlayer(var11_10).pg$38_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)var9_8, (ScriptContext)var1_1)) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
        var12_11 = new ArrayList<ScriptValue>();
        var12_11.add(var8_7);
        if (!ScriptFormula.callBuiltin((String)"is_empty", var12_11, (ScriptContext)var1_1).asBool()) {
            v0 = false;
        } else lbl-1000:
        // 2 sources

        {
            v0 = true;
        }
        if (v0) {
            var16_12 = new ArrayList<ScriptValue>();
            var16_12.add(ScriptValue.of((double)var2_2));
            var13_13 = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", var16_12, (ScriptContext)var1_1), (int)1);
            if (var13_13 != null) {
                for (ScriptValue[] var15_15 : var13_13) {
                    var0.val("i", var15_15.length > 0 ? var15_15[0] : ScriptValue.NULL);
                    var17_16 = var2_2 - 1.0 - var1_1.getNum("i");
                    var19_17 = ScriptValue.of((double)var17_16);
                    var0.val("ri", var19_17);
                    var20_18 = new ArrayList<ScriptValue>();
                    var20_18.add(ScriptValue.of((double)var17_16));
                    var21_19 = var1_1.getClassOrVar("Machine");
                    var24_22 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(var21_19 != ScriptValue.NULL ? (var21_19 instanceof ScriptValue.Obj && (var23_21 = (var22_20 = (ScriptValue.Obj)var21_19).instance()) != null && !(var23_21 instanceof PolyClass) && var22_20.typeName().equals("Machine") ? new PolyClassMachine_v3(var23_21).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var21_19, (ScriptContext)var1_1)) : ScriptValue.NULL), var20_18, (ScriptContext)var1_1);
                    var0.val("slot_item", (ScriptValue)var24_22);
                    var25_23 = new ArrayList<CallSite>();
                    var25_23.add(var24_22);
                    if (!(ScriptFormula.callBuiltin((String)"is_empty", var25_23, (ScriptContext)var1_1).asBool() ^ true)) continue;
                    var26_24 = var1_1.getClassOrVar("Player");
                    if (var26_24 != ScriptValue.NULL) {
                        var27_25 = var24_22;
                        if (var26_24 instanceof ScriptValue.Obj && (var29_27 = (var28_26 = (ScriptValue.Obj)var26_24).instance()) != null && !(var29_27 instanceof PolyClass) && var28_26.typeName().equals("Player")) {
                            var30_28 = new PolyClassPlayer(var29_27);
                            v1 /* !! */  = ScriptValue.of((boolean)var30_28.tm$14_give_item((ScriptValue)var27_25));
                        } else {
                            var31_29 = new ArrayList<CallSite>();
                            var31_29.add(var27_25);
                            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)var26_24, var31_29, (ScriptContext)var1_1);
                        }
                    } else {
                        v1 /* !! */  = ScriptValue.NULL;
                    }
                    var32_30 = new ArrayList<ScriptValue>();
                    var32_30.add(ScriptValue.of((double)var17_16));
                    var33_31 = new ArrayList<CallSite>();
                    var33_31.add(var24_22);
                    var32_30.add(ScriptFormula.callBuiltin((String)"item_count", var33_31, (ScriptContext)var1_1));
                    var34_32 = var1_1.getClassOrVar("Machine");
                    PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(var34_32 != ScriptValue.NULL ? (var34_32 instanceof ScriptValue.Obj && (var36_34 = (var35_33 = (ScriptValue.Obj)var34_32).instance()) != null && !(var36_34 instanceof PolyClass) && var35_33.typeName().equals("Machine") ? new PolyClassMachine_v3(var36_34).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var34_32, (ScriptContext)var1_1)) : ScriptValue.NULL), var32_30, (ScriptContext)var1_1);
                    break;
                }
            }
        } else {
            var37_35 = new ArrayList<ScriptValue>();
            var37_35.add(var8_7);
            var38_36 = ScriptFormula.callBuiltin((String)"item_count", var37_35, (ScriptContext)var1_1);
            var0.val("held_count", var38_36);
            var39_37 = new ArrayList<ScriptValue>();
            var39_37.add(var8_7);
            var40_38 = var1_1.getClassOrVar("Machine");
            var43_41 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)(var40_38 != ScriptValue.NULL ? (var40_38 instanceof ScriptValue.Obj && (var42_40 = (var41_39 = (ScriptValue.Obj)var40_38).instance()) != null && !(var42_40 instanceof PolyClass) && var41_39.typeName().equals("Machine") ? new PolyClassMachine_v3(var42_40).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var40_38, (ScriptContext)var1_1)) : ScriptValue.NULL), var39_37, (ScriptContext)var1_1);
            var0.val("leftover", (ScriptValue)var43_41);
            v2 = var38_36.asNum();
            var44_42 = new ArrayList<CallSite>();
            var44_42.add(var43_41);
            if (ScriptFormula.callBuiltin((String)"is_empty", var44_42, (ScriptContext)var1_1).asBool()) {
                v3 = ScriptValue.of((double)0.0);
            } else {
                var45_43 = new ArrayList<CallSite>();
                var45_43.add(var43_41);
                v3 = ScriptFormula.callBuiltin((String)"item_count", var45_43, (ScriptContext)var1_1);
            }
            var46_44 = v2 - v3.asNum();
            var48_45 = ScriptValue.of((double)var46_44);
            var0.val("placed", var48_45);
            if (var46_44 > 0.0) {
                var49_46 = var1_1.getClassOrVar("Player");
                if (var49_46 != ScriptValue.NULL) {
                    var50_47 = "main_hand";
                    var51_48 = var46_44;
                    if (var49_46 instanceof ScriptValue.Obj && (var54_50 = (var53_49 = (ScriptValue.Obj)var49_46).instance()) != null && !(var54_50 instanceof PolyClass) && var53_49.typeName().equals("Player")) {
                        var55_51 = new PolyClassPlayer(var54_50);
                        v4 /* !! */  = ScriptValue.of((boolean)var55_51.tm$34_remove_item(var50_47, var51_48));
                    } else {
                        var56_52 = new ArrayList<ScriptValue>();
                        var56_52.add(ScriptValue.of((String)var50_47));
                        var56_52.add(ScriptValue.of((double)var51_48));
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)var49_46, var56_52, (ScriptContext)var1_1);
                    }
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
            }
        }
        Depot.FILE_SCOPE = var0.build();
    }
}
