/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
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
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
        var5_4 = PolyClassPlayer.ofVar((ScriptContext)var1_1, (String)"Player");
        var7_6 = var5_4 != null ? var5_4.pg$48_main_hand() : ((var6_5 = var1_1.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)var6_5, (ScriptContext)var1_1) : ScriptValue.NULL);
        var0.val("held", var7_6);
        var8_7 = PolyClassPlayer.ofVar((ScriptContext)var1_1, (String)"Player");
        if (var8_7 != null ? var8_7.tg$49_is_sneaking() : ((var9_8 = var1_1.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)var9_8, (ScriptContext)var1_1).asBool() : ScriptValue.NULL.asBool())) ** GOTO lbl-1000
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
            var11_11 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", var14_10, (ScriptContext)var1_1));
            if (var11_11 != null) {
                for (ScriptValue var13_13 : var11_11) {
                    var0.val("i", var13_13);
                    var15_14 = var2_2 - 1.0 - var1_1.getNum("i");
                    var17_15 = ScriptValue.of((double)var15_14);
                    var0.val("ri", var17_15);
                    var19_17 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var18_16 = var19_17 != null ? var19_17.pg$120_container() : ((var20_18 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var20_18, (ScriptContext)var1_1) : ScriptValue.NULL);
                    var21_19 = var15_14;
                    if (var18_16 instanceof ScriptValue.Obj && (var24_21 = (var23_20 = (ScriptValue.Obj)var18_16).instance()) != null && !(var24_21 instanceof PolyClass) && var23_20.typeName().equals("Container")) {
                        var25_22 = new PolyClassContainer(var24_21);
                        v1 = var25_22.tm$0_get_item(var21_19);
                    } else {
                        v1 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)var18_16, (ScriptValue)ScriptValue.of((double)var21_19), (ScriptContext)var1_1);
                    }
                    var26_23 = v1;
                    var0.val("slot_item", (ScriptValue)var26_23);
                    var27_24 = new ArrayList<CallSite>();
                    var27_24.add(var26_23);
                    if (!(ScriptFormula.callBuiltin((String)"is_empty", var27_24, (ScriptContext)var1_1).asBool() ^ true)) continue;
                    var28_25 = var1_1.getClassOrVar("Player");
                    if (var28_25 != ScriptValue.NULL) {
                        var29_26 = var26_23;
                        if (var28_25 instanceof ScriptValue.Obj && (var31_28 = (var30_27 = (ScriptValue.Obj)var28_25).instance()) != null && !(var31_28 instanceof PolyClass) && var30_27.typeName().equals("Player")) {
                            var32_29 = new PolyClassPlayer(var31_28);
                            v2 /* !! */  = ScriptValue.of((boolean)var32_29.tm$14_give_item((ScriptValue)var29_26));
                        } else {
                            v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)var28_25, (ScriptValue)var29_26, (ScriptContext)var1_1);
                        }
                    } else {
                        v2 /* !! */  = ScriptValue.NULL;
                    }
                    var34_30 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var33_32 = var34_30 != null ? var34_30.pg$120_container() : ((var35_31 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var35_31, (ScriptContext)var1_1) : ScriptValue.NULL);
                    var36_33 = var15_14;
                    var39_34 = new ArrayList<CallSite>();
                    var39_34.add(var26_23);
                    var38_35 = ScriptFormula.callBuiltin((String)"item_count", var39_34, (ScriptContext)var1_1);
                    if (var33_32 instanceof ScriptValue.Obj && (var41_37 = (var40_36 = (ScriptValue.Obj)var33_32).instance()) != null && !(var41_37 instanceof PolyClass) && var40_36.typeName().equals("Container")) {
                        var42_38 = new PolyClassContainer(var41_37);
                        v3 = var42_38.tm$6_remove_item(var36_33, var38_35.asNum());
                    } else {
                        v3 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)var33_32, (ScriptValue)ScriptValue.of((double)var36_33), (ScriptValue)var38_35, (ScriptContext)var1_1);
                    }
                    break;
                }
            }
        } else {
            var43_39 = new ArrayList<ScriptValue>();
            var43_39.add(var7_6);
            var44_40 = ScriptFormula.callBuiltin((String)"item_count", var43_39, (ScriptContext)var1_1);
            var0.val("held_count", var44_40);
            var46_41 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
            var45_43 = var46_41 != null ? var46_41.pg$120_container() : ((var47_42 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var47_42, (ScriptContext)var1_1) : ScriptValue.NULL);
            var48_44 = var7_6;
            if (var45_43 instanceof ScriptValue.Obj && (var50_46 = (var49_45 = (ScriptValue.Obj)var45_43).instance()) != null && !(var50_46 instanceof PolyClass) && var49_45.typeName().equals("Container")) {
                var51_47 = new PolyClassContainer(var50_46);
                v4 = var51_47.tm$12_push(var48_44);
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)var45_43, (ScriptValue)var48_44, (ScriptContext)var1_1);
            }
            var52_48 = v4;
            var0.val("leftover", (ScriptValue)var52_48);
            v5 = var44_40.asNum();
            var53_49 = new ArrayList<CallSite>();
            var53_49.add(var52_48);
            if (ScriptFormula.callBuiltin((String)"is_empty", var53_49, (ScriptContext)var1_1).asBool()) {
                v6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Depot.class, 0.0);
            } else {
                var54_50 = new ArrayList<CallSite>();
                var54_50.add(var52_48);
                v6 = ScriptFormula.callBuiltin((String)"item_count", var54_50, (ScriptContext)var1_1);
            }
            var55_51 = v5 - v6.asNum();
            var57_52 = ScriptValue.of((double)var55_51);
            var0.val("placed", var57_52);
            if (var55_51 > 0.0) {
                var58_53 = var1_1.getClassOrVar("Player");
                if (var58_53 != ScriptValue.NULL) {
                    var59_54 = "main_hand";
                    var60_55 = var55_51;
                    if (var58_53 instanceof ScriptValue.Obj && (var63_57 = (var62_56 = (ScriptValue.Obj)var58_53).instance()) != null && !(var63_57 instanceof PolyClass) && var62_56.typeName().equals("Player")) {
                        var64_58 = new PolyClassPlayer(var63_57);
                        v7 /* !! */  = ScriptValue.of((boolean)var64_58.tm$34_remove_item(var59_54, var60_55));
                    } else {
                        v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)var58_53, (ScriptValue)ScriptValue.of((String)var59_54), (ScriptValue)ScriptValue.of((double)var60_55), (ScriptContext)var1_1);
                    }
                } else {
                    v7 /* !! */  = ScriptValue.NULL;
                }
            }
        }
        Depot.FILE_SCOPE = var0.build();
    }
}
