/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
        block18: {
            var1_1 = var0.peek();
            var2_2 = var1_1.getClassOrVar("Machine");
            if (var2_2 != ScriptValue.NULL) {
                var3_3 = "level";
                var4_4 = "int";
                if (var2_2 instanceof ScriptValue.Obj && (var6_6 = (var5_5 = (ScriptValue.Obj)var2_2).instance()) != null && !(var6_6 instanceof PolyClass) && var5_5.typeName().equals("Machine")) {
                    var7_7 = new PolyClassMachine(var6_6);
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
            var15_14 = new ArrayList<ScriptValue>();
            var15_14.add(ScriptValue.of((double)0.0));
            var16_15 = var1_1.getClassOrVar("Machine");
            var14_13.add(PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(var16_15 != ScriptValue.NULL ? ((var17_16 = PolyClassMachine.ofGuarded((ScriptValue)var16_15)) != null ? var17_16.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var16_15, (ScriptContext)var1_1)) : ScriptValue.NULL), var15_14, (ScriptContext)var1_1));
            if (!(ScriptFormula.callBuiltin((String)"is_empty", var14_13, (ScriptContext)var1_1).asBool() ^ true)) break block18;
            var18_17 = var1_1.getClassOrVar("Machine");
            if (var18_17 != ScriptValue.NULL) {
                var19_18 = var13_12;
                if (var18_17 instanceof ScriptValue.Obj && (var21_20 = (var20_19 = (ScriptValue.Obj)var18_17).instance()) != null && !(var21_20 instanceof PolyClass) && var20_19.typeName().equals("Machine")) {
                    var22_21 = new PolyClassMachine(var21_20);
                    v1 /* !! */  = var22_21.tm$86_blocks_in_range(var19_18.asNum());
                } else {
                    var23_22 = new ArrayList<ScriptValue>();
                    var23_22.add(var19_18);
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "blocks_in_range", (ScriptValue)var18_17, var23_22, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            var24_23 = v1 /* !! */ ;
            var0.val("blocks", var24_23);
            var25_24 = ScriptProgram.rowsOf((ScriptValue)var24_23, (int)1);
            if (var25_24 == null) break block18;
            for (ScriptValue[] var27_26 : var25_24) {
                var0.val("block", var27_26.length > 0 ? var27_26[0] : ScriptValue.NULL);
                var28_27 = var1_1.getClassOrVar("block");
                if (var28_27 != ScriptValue.NULL) {
                    var29_28 = new ArrayList<ScriptValue>();
                    var29_28.add(ScriptValue.of((String)"age"));
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)var28_27, var29_28, (ScriptContext)var1_1);
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                if (v2 /* !! */ .asBool()) ** GOTO lbl-1000
                var30_29 = var1_1.getClassOrVar("block");
                if (var30_29 != ScriptValue.NULL) {
                    var31_30 = new ArrayList<ScriptValue>();
                    var31_30.add(ScriptValue.of((String)"growth"));
                    v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)var30_29, var31_30, (ScriptContext)var1_1);
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                if (!v3 /* !! */ .asBool()) {
                    v4 = false;
                } else lbl-1000:
                // 2 sources

                {
                    v4 = true;
                }
                if (!v4) continue;
                var32_31 = var1_1.getClassOrVar("block");
                if (var32_31 != ScriptValue.NULL) {
                    var33_32 = new ArrayList<E>();
                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "apply_bone_meal", (ScriptValue)var32_31, var33_32, (ScriptContext)var1_1);
                } else {
                    v5 /* !! */  = ScriptValue.NULL;
                }
                var34_33 = v5 /* !! */ ;
                var0.val("ok", var34_33);
                if (!var34_33.asBool()) continue;
                var35_34 = new ArrayList<ScriptValue>();
                var35_34.add(ScriptValue.of((double)0.0));
                var35_34.add(ScriptValue.of((double)1.0));
                var36_35 = var1_1.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(var36_35 != ScriptValue.NULL ? ((var37_36 = PolyClassMachine.ofGuarded((ScriptValue)var36_35)) != null ? var37_36.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var36_35, (ScriptContext)var1_1)) : ScriptValue.NULL), var35_34, (ScriptContext)var1_1);
                break;
            }
        }
        Fertilizer.FILE_SCOPE = var0.build();
    }
}
