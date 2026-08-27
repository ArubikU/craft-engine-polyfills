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
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class BlockBreaker {
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
        var2_2 = new ArrayList<ScriptValue>();
        var2_2.add(ScriptValue.of((String)"minecraft:bedrock"));
        var2_2.add(ScriptValue.of((boolean)true));
        var2_2.add(ScriptValue.of((String)"minecraft:end_portal"));
        var2_2.add(ScriptValue.of((boolean)true));
        var2_2.add(ScriptValue.of((String)"minecraft:barrier"));
        var2_2.add(ScriptValue.of((boolean)true));
        var3_3 = ScriptFormula.callBuiltin((String)"make_map", var2_2, (ScriptContext)var1_1);
        var0.val("UNBREAKABLE", var3_3);
        var4_4 = var1_1.getClassOrVar("Machine");
        var6_6 = var4_4 != ScriptValue.NULL ? ((var5_5 = PolyClassMachine.ofGuarded((ScriptValue)var4_4)) != null ? var5_5.pg$128_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var4_4, (ScriptContext)var1_1)) : ScriptValue.NULL;
        var0.val("block", var6_6);
        var7_7 = var1_1.getClassOrVar("block");
        if (!((var7_7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var7_7, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true)) ** GOTO lbl-1000
        var8_8 = var1_1.getClassOrVar("UNBREAKABLE");
        if (var8_8 != ScriptValue.NULL) {
            var9_9 = new ArrayList<ScriptValue>();
            var10_10 = var1_1.getClassOrVar("block");
            var9_9.add((ScriptValue)(var10_10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var10_10, (ScriptContext)var1_1) : ScriptValue.NULL));
            v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has", (ScriptValue)var8_8, var9_9, (ScriptContext)var1_1);
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        if (v0 /* !! */ .asBool() ^ true) {
            v1 = true;
        } else lbl-1000:
        // 2 sources

        {
            v1 = false;
        }
        if (v1) {
            var11_11 = var1_1.getClassOrVar("Machine");
            if (var11_11 != ScriptValue.NULL) {
                if (var11_11 instanceof ScriptValue.Obj && (var13_13 = (var12_12 = (ScriptValue.Obj)var11_11).instance()) != null && !(var13_13 instanceof PolyClass) && var12_12.typeName().equals("Machine")) {
                    var14_14 = new PolyClassMachine(var13_13);
                    v2 /* !! */  = ScriptValue.of((boolean)var14_14.tm$102_hold_contraption());
                } else {
                    var15_15 = new ArrayList<E>();
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold_contraption", (ScriptValue)var11_11, var15_15, (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
            var16_16 = var1_1.getClassOrVar("Machine");
            if (var16_16 != ScriptValue.NULL) {
                var17_17 = var6_6;
                var18_18 = 10.0;
                if (var16_16 instanceof ScriptValue.Obj && (var21_20 = (var20_19 = (ScriptValue.Obj)var16_16).instance()) != null && !(var21_20 instanceof PolyClass) && var20_19.typeName().equals("Machine")) {
                    var22_21 = new PolyClassMachine(var21_20);
                    v3 /* !! */  = var22_21.tm$2_tick_break(var17_17, var18_18);
                } else {
                    var23_22 = new ArrayList<ScriptValue>();
                    var23_22.add(var17_17);
                    var23_22.add(ScriptValue.of((double)var18_18));
                    v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var16_16, var23_22, (ScriptContext)var1_1);
                }
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            var24_23 = v3 /* !! */ ;
            var0.val("drops", var24_23);
            if (ScriptFormula.valuesEqual((ScriptValue)var24_23, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                var25_24 = ScriptProgram.rowsOf((ScriptValue)var24_23, (int)1);
                if (var25_24 != null) {
                    for (ScriptValue[] var27_26 : var25_24) {
                        var0.val("drop", var27_26.length > 0 ? var27_26[0] : ScriptValue.NULL);
                        var28_27 = new ArrayList<ScriptValue>();
                        var28_27.add(var1_1.getClassOrVar("drop"));
                        var29_28 = var1_1.getClassOrVar("Machine");
                        PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)(var29_28 != ScriptValue.NULL ? ((var30_29 = PolyClassMachine.ofGuarded((ScriptValue)var29_28)) != null ? var30_29.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var29_28, (ScriptContext)var1_1)) : ScriptValue.NULL), var28_27, (ScriptContext)var1_1);
                    }
                }
                if ((var31_30 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    if (var31_30 instanceof ScriptValue.Obj && (var33_32 = (var32_31 = (ScriptValue.Obj)var31_30).instance()) != null && !(var33_32 instanceof PolyClass) && var32_31.typeName().equals("Machine")) {
                        var34_33 = new PolyClassMachine(var33_32);
                        v4 /* !! */  = ScriptValue.of((boolean)var34_33.tm$32_release_contraption());
                    } else {
                        var35_34 = new ArrayList<E>();
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var31_30, var35_34, (ScriptContext)var1_1);
                    }
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
            }
        } else {
            var36_35 = var1_1.getClassOrVar("Machine");
            if (var36_35 != ScriptValue.NULL) {
                if (var36_35 instanceof ScriptValue.Obj && (var38_37 = (var37_36 = (ScriptValue.Obj)var36_35).instance()) != null && !(var38_37 instanceof PolyClass) && var37_36.typeName().equals("Machine")) {
                    var39_38 = new PolyClassMachine(var38_37);
                    v5 /* !! */  = ScriptValue.of((boolean)var39_38.tm$32_release_contraption());
                } else {
                    var40_39 = new ArrayList<E>();
                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var36_35, var40_39, (ScriptContext)var1_1);
                }
            } else {
                v5 /* !! */  = ScriptValue.NULL;
            }
        }
        BlockBreaker.FILE_SCOPE = var0.build();
    }
}
