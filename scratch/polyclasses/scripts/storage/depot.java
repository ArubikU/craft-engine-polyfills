/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class Depot {
    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block13: {
            block14: {
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
                if (!v0) break block14;
                var13_12 = ScriptProgram.resolveForRows((String)"range(SIZE)", (ScriptContext)var1_1, (int)1);
                if (var13_12 == null) break block13;
                for (ScriptValue[] var15_14 : var13_12) {
                    var0.val("i", var15_14.length > 0 ? var15_14[0] : ScriptValue.NULL);
                    var16_15 = var2_2 - 1.0 - var1_1.getNum("i");
                    var18_16 = ScriptValue.of((double)var16_15);
                    var0.val("ri", var18_16);
                    var19_17 = new ArrayList<ScriptValue>();
                    var19_17.add(ScriptValue.of((double)var16_15));
                    var20_18 = var1_1.getClassOrVar("Machine");
                    var23_21 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(var20_18 != ScriptValue.NULL ? (var20_18 instanceof ScriptValue.Obj && (var22_20 = (var21_19 = (ScriptValue.Obj)var20_18).instance()) != null && !(var22_20 instanceof PolyClass) && var21_19.typeName().equals("Machine") ? new PolyClassMachine_v2(var22_20).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var20_18, (ScriptContext)var1_1)) : ScriptValue.NULL), var19_17, (ScriptContext)var1_1);
                    var0.val("slot_item", (ScriptValue)var23_21);
                    var24_22 = new ArrayList<CallSite>();
                    var24_22.add(var23_21);
                    if (!(ScriptFormula.callBuiltin((String)"is_empty", var24_22, (ScriptContext)var1_1).asBool() ^ true)) continue;
                    var25_23 = var1_1.getClassOrVar("Player");
                    if (var25_23 != ScriptValue.NULL) {
                        var26_24 = var23_21;
                        if (var25_23 instanceof ScriptValue.Obj && (var28_26 = (var27_25 = (ScriptValue.Obj)var25_23).instance()) != null && !(var28_26 instanceof PolyClass) && var27_25.typeName().equals("Player")) {
                            var29_27 = new PolyClassPlayer(var28_26);
                            v1 /* !! */  = ScriptValue.of((boolean)var29_27.tm$14_give_item((ScriptValue)var26_24));
                        } else {
                            var30_28 = new ArrayList<CallSite>();
                            var30_28.add(var26_24);
                            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)var25_23, var30_28, (ScriptContext)var1_1);
                        }
                    } else {
                        v1 /* !! */  = ScriptValue.NULL;
                    }
                    var31_29 = new ArrayList<ScriptValue>();
                    var31_29.add(ScriptValue.of((double)var16_15));
                    var32_30 = new ArrayList<CallSite>();
                    var32_30.add(var23_21);
                    var31_29.add(ScriptFormula.callBuiltin((String)"item_count", var32_30, (ScriptContext)var1_1));
                    var33_31 = var1_1.getClassOrVar("Machine");
                    PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(var33_31 != ScriptValue.NULL ? (var33_31 instanceof ScriptValue.Obj && (var35_33 = (var34_32 = (ScriptValue.Obj)var33_31).instance()) != null && !(var35_33 instanceof PolyClass) && var34_32.typeName().equals("Machine") ? new PolyClassMachine_v2(var35_33).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var33_31, (ScriptContext)var1_1)) : ScriptValue.NULL), var31_29, (ScriptContext)var1_1);
                    break block13;
                }
                break block13;
            }
            var36_34 = new ArrayList<ScriptValue>();
            var36_34.add(var8_7);
            var37_35 = ScriptFormula.callBuiltin((String)"item_count", var36_34, (ScriptContext)var1_1);
            var0.val("held_count", var37_35);
            var38_36 = new ArrayList<ScriptValue>();
            var38_36.add(var8_7);
            var39_37 = var1_1.getClassOrVar("Machine");
            var42_40 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)(var39_37 != ScriptValue.NULL ? (var39_37 instanceof ScriptValue.Obj && (var41_39 = (var40_38 = (ScriptValue.Obj)var39_37).instance()) != null && !(var41_39 instanceof PolyClass) && var40_38.typeName().equals("Machine") ? new PolyClassMachine_v2(var41_39).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var39_37, (ScriptContext)var1_1)) : ScriptValue.NULL), var38_36, (ScriptContext)var1_1);
            var0.val("leftover", (ScriptValue)var42_40);
            v2 = var37_35.asNum();
            var43_41 = new ArrayList<CallSite>();
            var43_41.add(var42_40);
            if (ScriptFormula.callBuiltin((String)"is_empty", var43_41, (ScriptContext)var1_1).asBool()) {
                v3 = ScriptValue.of((double)0.0);
            } else {
                var44_42 = new ArrayList<CallSite>();
                var44_42.add(var42_40);
                v3 = ScriptFormula.callBuiltin((String)"item_count", var44_42, (ScriptContext)var1_1);
            }
            var45_43 = v2 - v3.asNum();
            var47_44 = ScriptValue.of((double)var45_43);
            var0.val("placed", var47_44);
            if (!(var45_43 > 0.0)) break block13;
            var48_45 = var1_1.getClassOrVar("Player");
            if (var48_45 != ScriptValue.NULL) {
                var49_46 = "main_hand";
                var50_47 = var45_43;
                if (var48_45 instanceof ScriptValue.Obj && (var53_49 = (var52_48 = (ScriptValue.Obj)var48_45).instance()) != null && !(var53_49 instanceof PolyClass) && var52_48.typeName().equals("Player")) {
                    var54_50 = new PolyClassPlayer(var53_49);
                    v4 /* !! */  = ScriptValue.of((boolean)var54_50.tm$34_remove_item(var49_46, var50_47));
                } else {
                    var55_51 = new ArrayList<ScriptValue>();
                    var55_51.add(ScriptValue.of((String)var49_46));
                    var55_51.add(ScriptValue.of((double)var50_47));
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)var48_45, var55_51, (ScriptContext)var1_1);
                }
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
        }
    }
}
