/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v4
 *  dev.arubik.craftengine.script.PolyClassContainer
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
import dev.arubik.craftengine.script.PolyClassBlock_v4;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine;
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
        var4_4 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
        var6_6 = var4_4 != null ? var4_4.pg$137_facing_block() : ((var5_5 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var5_5, (ScriptContext)var1_1) : ScriptValue.NULL);
        var0.val("block", var6_6);
        v0 = var6_6 != ScriptValue.NULL ? ((var7_7 = PolyClassBlock_v4.ofGuarded((ScriptValue)var6_6)) != null ? var7_7.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var6_6, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
        if (!(v0 ^ true)) ** GOTO lbl-1000
        v1 /* !! */  = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has", (ScriptValue)var3_3, (ScriptValue)(var6_6 != ScriptValue.NULL ? ((var8_8 = PolyClassBlock_v4.ofGuarded((ScriptValue)var6_6)) != null ? var8_8.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var6_6, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptContext)var1_1) : ScriptValue.NULL;
        if (v1 /* !! */ .asBool() ^ true) {
            v2 = true;
        } else lbl-1000:
        // 2 sources

        {
            v2 = false;
        }
        if (v2) {
            var9_9 = var1_1.getClassOrVar("Machine");
            if (var9_9 != ScriptValue.NULL) {
                if (var9_9 instanceof ScriptValue.Obj && (var11_11 = (var10_10 = (ScriptValue.Obj)var9_9).instance()) != null && !(var11_11 instanceof PolyClass) && var10_10.typeName().equals("Machine")) {
                    var12_12 = new PolyClassMachine(var11_11);
                    v3 /* !! */  = ScriptValue.of((boolean)var12_12.tm$102_hold_contraption());
                } else {
                    v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold_contraption", (ScriptValue)var9_9, (ScriptContext)var1_1);
                }
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            var13_13 = var1_1.getClassOrVar("Machine");
            if (var13_13 != ScriptValue.NULL) {
                var14_14 = var6_6;
                var15_15 = 10.0;
                if (var13_13 instanceof ScriptValue.Obj && (var18_17 = (var17_16 = (ScriptValue.Obj)var13_13).instance()) != null && !(var18_17 instanceof PolyClass) && var17_16.typeName().equals("Machine")) {
                    var19_18 = new PolyClassMachine(var18_17);
                    v4 /* !! */  = var19_18.tm$2_tick_break(var14_14, var15_15);
                } else {
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var13_13, (ScriptValue)var14_14, (ScriptValue)ScriptValue.of((double)var15_15), (ScriptContext)var1_1);
                }
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            var20_19 = v4 /* !! */ ;
            var0.val("drops", var20_19);
            if (ScriptFormula.valuesEqual((ScriptValue)var20_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                var21_20 = ScriptProgram.elementsOf((ScriptValue)var20_19);
                if (var21_20 != null) {
                    for (ScriptValue var23_22 : var21_20) {
                        var0.val("drop", var23_22);
                        var25_24 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var24_23 = var25_24 != null ? var25_24.pg$120_container() : ((var26_25 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var26_25, (ScriptContext)var1_1) : ScriptValue.NULL);
                        var27_26 = var23_22;
                        if (var24_23 instanceof ScriptValue.Obj && (var29_28 = (var28_27 = (ScriptValue.Obj)var24_23).instance()) != null && !(var29_28 instanceof PolyClass) && var28_27.typeName().equals("Container")) {
                            var30_29 = new PolyClassContainer(var29_28);
                            v5 = var30_29.tm$12_push(var27_26);
                            continue;
                        }
                        v5 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)var24_23, (ScriptValue)var27_26, (ScriptContext)var1_1);
                    }
                }
                if ((var31_30 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    if (var31_30 instanceof ScriptValue.Obj && (var33_32 = (var32_31 = (ScriptValue.Obj)var31_30).instance()) != null && !(var33_32 instanceof PolyClass) && var32_31.typeName().equals("Machine")) {
                        var34_33 = new PolyClassMachine(var33_32);
                        v6 /* !! */  = ScriptValue.of((boolean)var34_33.tm$32_release_contraption());
                    } else {
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var31_30, (ScriptContext)var1_1);
                    }
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
            }
        } else {
            var35_34 = var1_1.getClassOrVar("Machine");
            if (var35_34 != ScriptValue.NULL) {
                if (var35_34 instanceof ScriptValue.Obj && (var37_36 = (var36_35 = (ScriptValue.Obj)var35_34).instance()) != null && !(var37_36 instanceof PolyClass) && var36_35.typeName().equals("Machine")) {
                    var38_37 = new PolyClassMachine(var37_36);
                    v7 /* !! */  = ScriptValue.of((boolean)var38_37.tm$32_release_contraption());
                } else {
                    v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var35_34, (ScriptContext)var1_1);
                }
            } else {
                v7 /* !! */  = ScriptValue.NULL;
            }
        }
        BlockBreaker.FILE_SCOPE = var0.build();
    }
}
