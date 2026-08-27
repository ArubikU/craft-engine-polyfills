/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class Fertilizer {
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
        block21: {
            var1_1 = var0.peek();
            var2_2 = var1_1.getClassOrVar("Machine");
            if (var2_2 != ScriptValue.NULL) {
                var3_3 = "level";
                var4_4 = "int";
                if (var2_2 instanceof ScriptValue.Obj && (var6_6 = (var5_5 = (ScriptValue.Obj)var2_2).instance()) != null && !(var6_6 instanceof PolyClass) && var5_5.typeName().equals("Machine")) {
                    var7_7 = new PolyClassMachine_v4(var6_6);
                    v0 /* !! */  = var7_7.tm$34_get_typed(var3_3, var4_4);
                } else {
                    var8_8 = new ArrayList<ScriptValue>();
                    var8_8.add(ScriptValue.of((String)var3_3));
                    var8_8.add(ScriptValue.of((String)var4_4));
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var2_2, var8_8, (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var9_9 = v0 /* !! */ ;
            var0.val("level_flag", var9_9);
            if (var9_9.asNum() <= 0.0) {
                var10_10 = 1.0;
                var12_11 = ScriptValue.of((double)1.0);
                var0.val("level_flag", var12_11);
            }
            var13_12 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("level_flag"), (ScriptValue)ScriptValue.of((double)2.0));
            var0.val("radius", var13_12);
            var14_13 = new ArrayList<CallSite>();
            var16_14 = var1_1.getClassOrVar("Machine");
            var15_16 = var16_14 != ScriptValue.NULL ? ((var17_15 = PolyClassMachine_v4.ofGuarded((ScriptValue)var16_14)) != null ? var17_15.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var16_14, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var18_17 = 0.0;
            if (var15_16 instanceof ScriptValue.Obj && (var21_19 = (var20_18 = (ScriptValue.Obj)var15_16).instance()) != null && !(var21_19 instanceof PolyClass) && var20_18.typeName().equals("Container")) {
                var22_20 = new PolyClassContainer(var21_19);
                v1 = var22_20.tm$0_get_item(var18_17);
            } else {
                var23_21 = new ArrayList<ScriptValue>();
                var23_21.add(ScriptValue.of((double)var18_17));
                v1 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)var15_16, var23_21, (ScriptContext)var1_1);
            }
            var14_13.add(v1);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", var14_13, (ScriptContext)var1_1).asBool() ^ true)) break block21;
            var24_22 = var1_1.getClassOrVar("Machine");
            if (var24_22 != ScriptValue.NULL) {
                var25_23 = var13_12;
                if (var24_22 instanceof ScriptValue.Obj && (var27_25 = (var26_24 = (ScriptValue.Obj)var24_22).instance()) != null && !(var27_25 instanceof PolyClass) && var26_24.typeName().equals("Machine")) {
                    var28_26 = new PolyClassMachine_v4(var27_25);
                    v2 /* !! */  = var28_26.tm$86_blocks_in_range(var25_23.asNum());
                } else {
                    var29_27 = new ArrayList<ScriptValue>();
                    var29_27.add(var25_23);
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "blocks_in_range", (ScriptValue)var24_22, var29_27, (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
            var30_28 = v2 /* !! */ ;
            var0.val("blocks", var30_28);
            var31_29 = ScriptProgram.elementsOf((ScriptValue)var30_28);
            if (var31_29 == null) break block21;
            for (ScriptValue var33_31 : var31_29) {
                var0.val("block", var33_31);
                var34_32 = var1_1.getClassOrVar("block");
                if (var34_32 != ScriptValue.NULL) {
                    var35_33 = new ArrayList<ScriptValue>();
                    var35_33.add(ScriptValue.of((String)"age"));
                    v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)var34_32, var35_33, (ScriptContext)var1_1);
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                if (v3 /* !! */ .asBool()) ** GOTO lbl-1000
                var36_34 = var1_1.getClassOrVar("block");
                if (var36_34 != ScriptValue.NULL) {
                    var37_35 = new ArrayList<ScriptValue>();
                    var37_35.add(ScriptValue.of((String)"growth"));
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)var36_34, var37_35, (ScriptContext)var1_1);
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
                if (!v4 /* !! */ .asBool()) {
                    v5 = false;
                } else lbl-1000:
                // 2 sources

                {
                    v5 = true;
                }
                if (!v5) continue;
                var38_36 = var1_1.getClassOrVar("block");
                if (var38_36 != ScriptValue.NULL) {
                    var39_37 = new ArrayList<E>();
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "apply_bone_meal", (ScriptValue)var38_36, var39_37, (ScriptContext)var1_1);
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
                var40_38 = v6 /* !! */ ;
                var0.val("ok", var40_38);
                if (!var40_38.asBool()) continue;
                var42_39 = var1_1.getClassOrVar("Machine");
                var41_41 = var42_39 != ScriptValue.NULL ? ((var43_40 = PolyClassMachine_v4.ofGuarded((ScriptValue)var42_39)) != null ? var43_40.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var42_39, (ScriptContext)var1_1)) : ScriptValue.NULL;
                var44_42 = 0.0;
                var46_43 = 1.0;
                if (var41_41 instanceof ScriptValue.Obj && (var49_45 = (var48_44 = (ScriptValue.Obj)var41_41).instance()) != null && !(var49_45 instanceof PolyClass) && var48_44.typeName().equals("Container")) {
                    var50_46 = new PolyClassContainer(var49_45);
                    v7 = var50_46.tm$6_remove_item(var44_42, var46_43);
                    break;
                }
                var51_47 = new ArrayList<ScriptValue>();
                var51_47.add(ScriptValue.of((double)var44_42));
                var51_47.add(ScriptValue.of((double)var46_43));
                v7 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)var41_41, var51_47, (ScriptContext)var1_1);
                break;
            }
        }
        Fertilizer.FILE_SCOPE = var0.build();
    }
}
