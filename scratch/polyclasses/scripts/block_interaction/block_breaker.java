/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class BlockBreaker {
    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block23: {
            block22: {
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
                var7_7 = var4_4 != ScriptValue.NULL ? (var4_4 instanceof ScriptValue.Obj && (var6_6 = (var5_5 = (ScriptValue.Obj)var4_4).instance()) != null && !(var6_6 instanceof PolyClass) && var5_5.typeName().equals("Machine") ? new PolyClassMachine_v2(var6_6).pg$128_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var4_4, (ScriptContext)var1_1)) : ScriptValue.NULL;
                var0.val("block", var7_7);
                var8_8 = var1_1.getClassOrVar("block");
                if (!((var8_8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var8_8, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true)) ** GOTO lbl-1000
                var9_9 = var1_1.getClassOrVar("UNBREAKABLE");
                if (var9_9 != ScriptValue.NULL) {
                    var10_10 = new ArrayList<ScriptValue>();
                    var11_11 = var1_1.getClassOrVar("block");
                    var10_10.add((ScriptValue)(var11_11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var11_11, (ScriptContext)var1_1) : ScriptValue.NULL));
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has", (ScriptValue)var9_9, var10_10, (ScriptContext)var1_1);
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
                if (!v1) break block22;
                var12_12 = var1_1.getClassOrVar("Machine");
                if (var12_12 != ScriptValue.NULL) {
                    if (var12_12 instanceof ScriptValue.Obj && (var14_14 = (var13_13 = (ScriptValue.Obj)var12_12).instance()) != null && !(var14_14 instanceof PolyClass) && var13_13.typeName().equals("Machine")) {
                        var15_15 = new PolyClassMachine_v2(var14_14);
                        v2 /* !! */  = ScriptValue.of((boolean)var15_15.tm$102_hold_contraption());
                    } else {
                        var16_16 = new ArrayList<E>();
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold_contraption", (ScriptValue)var12_12, var16_16, (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var17_17 = var1_1.getClassOrVar("Machine");
                if (var17_17 != ScriptValue.NULL) {
                    var18_18 = var7_7;
                    var19_19 = 10.0;
                    if (var17_17 instanceof ScriptValue.Obj && (var22_21 = (var21_20 = (ScriptValue.Obj)var17_17).instance()) != null && !(var22_21 instanceof PolyClass) && var21_20.typeName().equals("Machine")) {
                        var23_22 = new PolyClassMachine_v2(var22_21);
                        v3 /* !! */  = var23_22.tm$2_tick_break(var18_18, var19_19);
                    } else {
                        var24_23 = new ArrayList<ScriptValue>();
                        var24_23.add(var18_18);
                        var24_23.add(ScriptValue.of((double)var19_19));
                        v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var17_17, var24_23, (ScriptContext)var1_1);
                    }
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                var25_24 = v3 /* !! */ ;
                var0.val("drops", var25_24);
                if (!(ScriptFormula.valuesEqual((ScriptValue)var25_24, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block23;
                var26_25 = ScriptProgram.resolveForRows((String)"drops", (ScriptContext)var1_1, (int)1);
                if (var26_25 != null) {
                    for (ScriptValue[] var28_27 : var26_25) {
                        var0.val("drop", var28_27.length > 0 ? var28_27[0] : ScriptValue.NULL);
                        var29_28 = new ArrayList<ScriptValue>();
                        var29_28.add(var1_1.getClassOrVar("drop"));
                        var30_29 = var1_1.getClassOrVar("Machine");
                        PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)(var30_29 != ScriptValue.NULL ? (var30_29 instanceof ScriptValue.Obj && (var32_31 = (var31_30 = (ScriptValue.Obj)var30_29).instance()) != null && !(var32_31 instanceof PolyClass) && var31_30.typeName().equals("Machine") ? new PolyClassMachine_v2(var32_31).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var30_29, (ScriptContext)var1_1)) : ScriptValue.NULL), var29_28, (ScriptContext)var1_1);
                    }
                }
                if ((var33_32 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    if (var33_32 instanceof ScriptValue.Obj && (var35_34 = (var34_33 = (ScriptValue.Obj)var33_32).instance()) != null && !(var35_34 instanceof PolyClass) && var34_33.typeName().equals("Machine")) {
                        var36_35 = new PolyClassMachine_v2(var35_34);
                        v4 /* !! */  = ScriptValue.of((boolean)var36_35.tm$32_release_contraption());
                    } else {
                        var37_36 = new ArrayList<E>();
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var33_32, var37_36, (ScriptContext)var1_1);
                    }
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
                break block23;
            }
            var38_37 = var1_1.getClassOrVar("Machine");
            if (var38_37 != ScriptValue.NULL) {
                if (var38_37 instanceof ScriptValue.Obj && (var40_39 = (var39_38 = (ScriptValue.Obj)var38_37).instance()) != null && !(var40_39 instanceof PolyClass) && var39_38.typeName().equals("Machine")) {
                    var41_40 = new PolyClassMachine_v2(var40_39);
                    v5 /* !! */  = ScriptValue.of((boolean)var41_40.tm$32_release_contraption());
                } else {
                    var42_41 = new ArrayList<E>();
                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var38_37, var42_41, (ScriptContext)var1_1);
                }
            } else {
                v5 /* !! */  = ScriptValue.NULL;
            }
        }
    }
}
