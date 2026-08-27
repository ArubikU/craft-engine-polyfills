/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassMachine_v4;
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
        var5_4 = var1_1.getClassOrVar("Player");
        var7_6 = var5_4 != ScriptValue.NULL ? ((var6_5 = PolyClassPlayer.ofGuarded((ScriptValue)var5_4)) != null ? var6_5.pg$48_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)var5_4, (ScriptContext)var1_1)) : ScriptValue.NULL;
        var0.val("held", var7_6);
        var8_7 = var1_1.getClassOrVar("Player");
        if (var8_7 != ScriptValue.NULL ? ((var9_8 = PolyClassPlayer.ofGuarded((ScriptValue)var8_7)) != null ? var9_8.tg$49_is_sneaking() : PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)var8_7, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool()) ** GOTO lbl-1000
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
                    var19_17 = var1_1.getClassOrVar("Machine");
                    var18_16 = var19_17 != ScriptValue.NULL ? ((var20_18 = PolyClassMachine_v4.ofGuarded((ScriptValue)var19_17)) != null ? var20_18.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var19_17, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var21_19 = var15_14;
                    if (var18_16 instanceof ScriptValue.Obj && (var24_21 = (var23_20 = (ScriptValue.Obj)var18_16).instance()) != null && !(var24_21 instanceof PolyClass) && var23_20.typeName().equals("Container")) {
                        var25_22 = new PolyClassContainer(var24_21);
                        v1 = var25_22.tm$0_get_item(var21_19);
                    } else {
                        var26_23 = new ArrayList<ScriptValue>();
                        var26_23.add(ScriptValue.of((double)var21_19));
                        v1 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)var18_16, var26_23, (ScriptContext)var1_1);
                    }
                    var27_24 = v1;
                    var0.val("slot_item", (ScriptValue)var27_24);
                    var28_25 = new ArrayList<CallSite>();
                    var28_25.add(var27_24);
                    if (!(ScriptFormula.callBuiltin((String)"is_empty", var28_25, (ScriptContext)var1_1).asBool() ^ true)) continue;
                    var29_26 = var1_1.getClassOrVar("Player");
                    if (var29_26 != ScriptValue.NULL) {
                        var30_27 = var27_24;
                        if (var29_26 instanceof ScriptValue.Obj && (var32_29 = (var31_28 = (ScriptValue.Obj)var29_26).instance()) != null && !(var32_29 instanceof PolyClass) && var31_28.typeName().equals("Player")) {
                            var33_30 = new PolyClassPlayer(var32_29);
                            v2 /* !! */  = ScriptValue.of((boolean)var33_30.tm$14_give_item((ScriptValue)var30_27));
                        } else {
                            var34_31 = new ArrayList<CallSite>();
                            var34_31.add(var30_27);
                            v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)var29_26, var34_31, (ScriptContext)var1_1);
                        }
                    } else {
                        v2 /* !! */  = ScriptValue.NULL;
                    }
                    var36_32 = var1_1.getClassOrVar("Machine");
                    var35_34 = var36_32 != ScriptValue.NULL ? ((var37_33 = PolyClassMachine_v4.ofGuarded((ScriptValue)var36_32)) != null ? var37_33.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var36_32, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var38_35 = var15_14;
                    var41_36 = new ArrayList<CallSite>();
                    var41_36.add(var27_24);
                    var40_37 = ScriptFormula.callBuiltin((String)"item_count", var41_36, (ScriptContext)var1_1);
                    if (var35_34 instanceof ScriptValue.Obj && (var43_39 = (var42_38 = (ScriptValue.Obj)var35_34).instance()) != null && !(var43_39 instanceof PolyClass) && var42_38.typeName().equals("Container")) {
                        var44_40 = new PolyClassContainer(var43_39);
                        v3 = var44_40.tm$6_remove_item(var38_35, var40_37.asNum());
                    } else {
                        var45_41 = new ArrayList<ScriptValue>();
                        var45_41.add(ScriptValue.of((double)var38_35));
                        var45_41.add(var40_37);
                        v3 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)var35_34, var45_41, (ScriptContext)var1_1);
                    }
                    break;
                }
            }
        } else {
            var46_42 = new ArrayList<ScriptValue>();
            var46_42.add(var7_6);
            var47_43 = ScriptFormula.callBuiltin((String)"item_count", var46_42, (ScriptContext)var1_1);
            var0.val("held_count", var47_43);
            var49_44 = var1_1.getClassOrVar("Machine");
            var48_46 = var49_44 != ScriptValue.NULL ? ((var50_45 = PolyClassMachine_v4.ofGuarded((ScriptValue)var49_44)) != null ? var50_45.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var49_44, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var51_47 = var7_6;
            if (var48_46 instanceof ScriptValue.Obj && (var53_49 = (var52_48 = (ScriptValue.Obj)var48_46).instance()) != null && !(var53_49 instanceof PolyClass) && var52_48.typeName().equals("Container")) {
                var54_50 = new PolyClassContainer(var53_49);
                v4 = var54_50.tm$12_push(var51_47);
            } else {
                var55_51 = new ArrayList<ScriptValue>();
                var55_51.add(var51_47);
                v4 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)var48_46, var55_51, (ScriptContext)var1_1);
            }
            var56_52 = v4;
            var0.val("leftover", (ScriptValue)var56_52);
            v5 = var47_43.asNum();
            var57_53 = new ArrayList<CallSite>();
            var57_53.add(var56_52);
            if (ScriptFormula.callBuiltin((String)"is_empty", var57_53, (ScriptContext)var1_1).asBool()) {
                v6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Depot.class, 0.0);
            } else {
                var58_54 = new ArrayList<CallSite>();
                var58_54.add(var56_52);
                v6 = ScriptFormula.callBuiltin((String)"item_count", var58_54, (ScriptContext)var1_1);
            }
            var59_55 = v5 - v6.asNum();
            var61_56 = ScriptValue.of((double)var59_55);
            var0.val("placed", var61_56);
            if (var59_55 > 0.0) {
                var62_57 = var1_1.getClassOrVar("Player");
                if (var62_57 != ScriptValue.NULL) {
                    var63_58 = "main_hand";
                    var64_59 = var59_55;
                    if (var62_57 instanceof ScriptValue.Obj && (var67_61 = (var66_60 = (ScriptValue.Obj)var62_57).instance()) != null && !(var67_61 instanceof PolyClass) && var66_60.typeName().equals("Player")) {
                        var68_62 = new PolyClassPlayer(var67_61);
                        v7 /* !! */  = ScriptValue.of((boolean)var68_62.tm$34_remove_item(var63_58, var64_59));
                    } else {
                        var69_63 = new ArrayList<ScriptValue>();
                        var69_63.add(ScriptValue.of((String)var63_58));
                        var69_63.add(ScriptValue.of((double)var64_59));
                        v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)var62_57, var69_63, (ScriptContext)var1_1);
                    }
                } else {
                    v7 /* !! */  = ScriptValue.NULL;
                }
            }
        }
        Depot.FILE_SCOPE = var0.build();
    }
}
