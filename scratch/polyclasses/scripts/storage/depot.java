/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
        var7_6 = var5_4 != ScriptValue.NULL ? ((var6_5 = PolyClassPlayer.ofGuarded((ScriptValue)var5_4)) != null ? var6_5.pg$47_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)var5_4, (ScriptContext)var1_1)) : ScriptValue.NULL;
        var0.val("held", var7_6);
        var8_7 = var1_1.getClassOrVar("Player");
        if ((var8_7 != ScriptValue.NULL ? ((var9_8 = PolyClassPlayer.ofGuarded((ScriptValue)var8_7)) != null ? var9_8.pg$38_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)var8_7, (ScriptContext)var1_1)) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
        var10_9 = new ArrayList<ScriptValue>();
        var10_9.add(var7_6);
        if (!ScriptFormula.callBuiltin((String)"is_empty", var10_9, (ScriptContext)var1_1).asBool()) {
            v0 = false;
        } else lbl-1000:
        // 2 sources

        {
            v0 = true;
        }
        if (v0) {
            var14_10 = new ArrayList<ScriptValue>();
            var14_10.add(ScriptValue.of((double)var2_2));
            var11_11 = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", var14_10, (ScriptContext)var1_1), (int)1);
            if (var11_11 != null) {
                for (ScriptValue[] var13_13 : var11_11) {
                    var0.val("i", var13_13.length > 0 ? var13_13[0] : ScriptValue.NULL);
                    var15_14 = var2_2 - 1.0 - var1_1.getNum("i");
                    var17_15 = ScriptValue.of((double)var15_14);
                    var0.val("ri", var17_15);
                    var18_16 = new ArrayList<ScriptValue>();
                    var18_16.add(ScriptValue.of((double)var15_14));
                    var19_17 = var1_1.getClassOrVar("Machine");
                    var21_19 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(var19_17 != ScriptValue.NULL ? ((var20_18 = PolyClassMachine.ofGuarded((ScriptValue)var19_17)) != null ? var20_18.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var19_17, (ScriptContext)var1_1)) : ScriptValue.NULL), var18_16, (ScriptContext)var1_1);
                    var0.val("slot_item", (ScriptValue)var21_19);
                    var22_20 = new ArrayList<CallSite>();
                    var22_20.add(var21_19);
                    if (!(ScriptFormula.callBuiltin((String)"is_empty", var22_20, (ScriptContext)var1_1).asBool() ^ true)) continue;
                    var23_21 = var1_1.getClassOrVar("Player");
                    if (var23_21 != ScriptValue.NULL) {
                        var24_22 = var21_19;
                        if (var23_21 instanceof ScriptValue.Obj && (var26_24 = (var25_23 = (ScriptValue.Obj)var23_21).instance()) != null && !(var26_24 instanceof PolyClass) && var25_23.typeName().equals("Player")) {
                            var27_25 = new PolyClassPlayer(var26_24);
                            v1 /* !! */  = ScriptValue.of((boolean)var27_25.tm$14_give_item((ScriptValue)var24_22));
                        } else {
                            var28_26 = new ArrayList<CallSite>();
                            var28_26.add(var24_22);
                            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)var23_21, var28_26, (ScriptContext)var1_1);
                        }
                    } else {
                        v1 /* !! */  = ScriptValue.NULL;
                    }
                    var29_27 = new ArrayList<ScriptValue>();
                    var29_27.add(ScriptValue.of((double)var15_14));
                    var30_28 = new ArrayList<CallSite>();
                    var30_28.add(var21_19);
                    var29_27.add(ScriptFormula.callBuiltin((String)"item_count", var30_28, (ScriptContext)var1_1));
                    var31_29 = var1_1.getClassOrVar("Machine");
                    PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(var31_29 != ScriptValue.NULL ? ((var32_30 = PolyClassMachine.ofGuarded((ScriptValue)var31_29)) != null ? var32_30.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var31_29, (ScriptContext)var1_1)) : ScriptValue.NULL), var29_27, (ScriptContext)var1_1);
                    break;
                }
            }
        } else {
            var33_31 = new ArrayList<ScriptValue>();
            var33_31.add(var7_6);
            var34_32 = ScriptFormula.callBuiltin((String)"item_count", var33_31, (ScriptContext)var1_1);
            var0.val("held_count", var34_32);
            var35_33 = new ArrayList<ScriptValue>();
            var35_33.add(var7_6);
            var36_34 = var1_1.getClassOrVar("Machine");
            var38_36 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)(var36_34 != ScriptValue.NULL ? ((var37_35 = PolyClassMachine.ofGuarded((ScriptValue)var36_34)) != null ? var37_35.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var36_34, (ScriptContext)var1_1)) : ScriptValue.NULL), var35_33, (ScriptContext)var1_1);
            var0.val("leftover", (ScriptValue)var38_36);
            v2 = var34_32.asNum();
            var39_37 = new ArrayList<CallSite>();
            var39_37.add(var38_36);
            if (ScriptFormula.callBuiltin((String)"is_empty", var39_37, (ScriptContext)var1_1).asBool()) {
                v3 = ScriptValue.of((double)0.0);
            } else {
                var40_38 = new ArrayList<CallSite>();
                var40_38.add(var38_36);
                v3 = ScriptFormula.callBuiltin((String)"item_count", var40_38, (ScriptContext)var1_1);
            }
            var41_39 = v2 - v3.asNum();
            var43_40 = ScriptValue.of((double)var41_39);
            var0.val("placed", var43_40);
            if (var41_39 > 0.0) {
                var44_41 = var1_1.getClassOrVar("Player");
                if (var44_41 != ScriptValue.NULL) {
                    var45_42 = "main_hand";
                    var46_43 = var41_39;
                    if (var44_41 instanceof ScriptValue.Obj && (var49_45 = (var48_44 = (ScriptValue.Obj)var44_41).instance()) != null && !(var49_45 instanceof PolyClass) && var48_44.typeName().equals("Player")) {
                        var50_46 = new PolyClassPlayer(var49_45);
                        v4 /* !! */  = ScriptValue.of((boolean)var50_46.tm$34_remove_item(var45_42, var46_43));
                    } else {
                        var51_47 = new ArrayList<ScriptValue>();
                        var51_47.add(ScriptValue.of((String)var45_42));
                        var51_47.add(ScriptValue.of((double)var46_43));
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)var44_41, var51_47, (ScriptContext)var1_1);
                    }
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
            }
        }
        Depot.FILE_SCOPE = var0.build();
    }
}
