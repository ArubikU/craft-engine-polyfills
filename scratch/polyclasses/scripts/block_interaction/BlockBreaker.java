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
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BlockBreaker.class, "minecraft:bedrock"));
        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", BlockBreaker.class, 1));
        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BlockBreaker.class, "minecraft:end_portal"));
        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", BlockBreaker.class, 1));
        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BlockBreaker.class, "minecraft:barrier"));
        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", BlockBreaker.class, 1));
        var3_3 = ScriptFormula.callBuiltin((String)"make_map", var2_2, (ScriptContext)var1_1);
        var0.val("UNBREAKABLE", var3_3);
        var4_4 = var1_1.getClassOrVar("Machine");
        var6_6 = var4_4 != ScriptValue.NULL ? ((var5_5 = PolyClassMachine_v4.ofGuarded((ScriptValue)var4_4)) != null ? var5_5.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var4_4, (ScriptContext)var1_1)) : ScriptValue.NULL;
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
                    var14_14 = new PolyClassMachine_v4(var13_13);
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
                    var22_21 = new PolyClassMachine_v4(var21_20);
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
                var25_24 = ScriptProgram.elementsOf((ScriptValue)var24_23);
                if (var25_24 != null) {
                    for (ScriptValue var27_26 : var25_24) {
                        var0.val("drop", var27_26);
                        var29_28 = var1_1.getClassOrVar("Machine");
                        var28_27 = var29_28 != ScriptValue.NULL ? ((var30_29 = PolyClassMachine_v4.ofGuarded((ScriptValue)var29_28)) != null ? var30_29.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var29_28, (ScriptContext)var1_1)) : ScriptValue.NULL;
                        var31_30 = var1_1.getClassOrVar("drop");
                        if (var28_27 instanceof ScriptValue.Obj && (var33_32 = (var32_31 = (ScriptValue.Obj)var28_27).instance()) != null && !(var33_32 instanceof PolyClass) && var32_31.typeName().equals("Container")) {
                            var34_33 = new PolyClassContainer(var33_32);
                            v4 = var34_33.tm$12_push(var31_30);
                            continue;
                        }
                        var35_34 = new ArrayList<ScriptValue>();
                        var35_34.add(var31_30);
                        v4 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)var28_27, var35_34, (ScriptContext)var1_1);
                    }
                }
                if ((var36_35 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    if (var36_35 instanceof ScriptValue.Obj && (var38_37 = (var37_36 = (ScriptValue.Obj)var36_35).instance()) != null && !(var38_37 instanceof PolyClass) && var37_36.typeName().equals("Machine")) {
                        var39_38 = new PolyClassMachine_v4(var38_37);
                        v5 /* !! */  = ScriptValue.of((boolean)var39_38.tm$32_release_contraption());
                    } else {
                        var40_39 = new ArrayList<E>();
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var36_35, var40_39, (ScriptContext)var1_1);
                    }
                } else {
                    v5 /* !! */  = ScriptValue.NULL;
                }
            }
        } else {
            var41_40 = var1_1.getClassOrVar("Machine");
            if (var41_40 != ScriptValue.NULL) {
                if (var41_40 instanceof ScriptValue.Obj && (var43_42 = (var42_41 = (ScriptValue.Obj)var41_40).instance()) != null && !(var43_42 instanceof PolyClass) && var42_41.typeName().equals("Machine")) {
                    var44_43 = new PolyClassMachine_v4(var43_42);
                    v6 /* !! */  = ScriptValue.of((boolean)var44_43.tm$32_release_contraption());
                } else {
                    var45_44 = new ArrayList<E>();
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var41_40, var45_44, (ScriptContext)var1_1);
                }
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
        }
        BlockBreaker.FILE_SCOPE = var0.build();
    }
}
