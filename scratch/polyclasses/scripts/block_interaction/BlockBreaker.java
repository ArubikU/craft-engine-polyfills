/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
 *  dev.arubik.craftengine.script.PolyClassContainer
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
import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
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
        var4_4 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
        var6_6 = var4_4 != null ? var4_4.pg$137_facing_block() : ((var5_5 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var5_5, (ScriptContext)var1_1) : ScriptValue.NULL);
        var0.val("block", var6_6);
        v0 = var6_6 != ScriptValue.NULL ? ((var7_7 = PolyClassBlock_v2.ofGuarded((ScriptValue)var6_6)) != null ? var7_7.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var6_6, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
        if (!(v0 ^ true)) ** GOTO lbl-1000
        var8_8 = var1_1.getClassOrVar("UNBREAKABLE");
        v1 /* !! */  = var8_8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has", (ScriptValue)var8_8, (ScriptValue)(var6_6 != ScriptValue.NULL ? ((var9_9 = PolyClassBlock_v2.ofGuarded((ScriptValue)var6_6)) != null ? var9_9.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var6_6, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptContext)var1_1) : ScriptValue.NULL;
        if (v1 /* !! */ .asBool() ^ true) {
            v2 = true;
        } else lbl-1000:
        // 2 sources

        {
            v2 = false;
        }
        if (v2) {
            var10_10 = var1_1.getClassOrVar("Machine");
            if (var10_10 != ScriptValue.NULL) {
                if (var10_10 instanceof ScriptValue.Obj && (var12_12 = (var11_11 = (ScriptValue.Obj)var10_10).instance()) != null && !(var12_12 instanceof PolyClass) && var11_11.typeName().equals("Machine")) {
                    var13_13 = new PolyClassMachine_v2(var12_12);
                    v3 /* !! */  = ScriptValue.of((boolean)var13_13.tm$102_hold_contraption());
                } else {
                    v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold_contraption", (ScriptValue)var10_10, (ScriptContext)var1_1);
                }
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            var14_14 = var1_1.getClassOrVar("Machine");
            if (var14_14 != ScriptValue.NULL) {
                var15_15 = var6_6;
                var16_16 = 10.0;
                if (var14_14 instanceof ScriptValue.Obj && (var19_18 = (var18_17 = (ScriptValue.Obj)var14_14).instance()) != null && !(var19_18 instanceof PolyClass) && var18_17.typeName().equals("Machine")) {
                    var20_19 = new PolyClassMachine_v2(var19_18);
                    v4 /* !! */  = var20_19.tm$2_tick_break(var15_15, var16_16);
                } else {
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var14_14, (ScriptValue)var15_15, (ScriptValue)ScriptValue.of((double)var16_16), (ScriptContext)var1_1);
                }
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            var21_20 = v4 /* !! */ ;
            var0.val("drops", var21_20);
            if (ScriptFormula.valuesEqual((ScriptValue)var21_20, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                var22_21 = ScriptProgram.elementsOf((ScriptValue)var21_20);
                if (var22_21 != null) {
                    for (ScriptValue var24_23 : var22_21) {
                        var0.val("drop", var24_23);
                        var26_25 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var25_24 = var26_25 != null ? var26_25.pg$120_container() : ((var27_26 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var27_26, (ScriptContext)var1_1) : ScriptValue.NULL);
                        var28_27 = var24_23;
                        if (var25_24 instanceof ScriptValue.Obj && (var30_29 = (var29_28 = (ScriptValue.Obj)var25_24).instance()) != null && !(var30_29 instanceof PolyClass) && var29_28.typeName().equals("Container")) {
                            var31_30 = new PolyClassContainer(var30_29);
                            v5 = var31_30.tm$12_push(var28_27);
                            continue;
                        }
                        v5 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)var25_24, (ScriptValue)var28_27, (ScriptContext)var1_1);
                    }
                }
                if ((var32_31 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                    if (var32_31 instanceof ScriptValue.Obj && (var34_33 = (var33_32 = (ScriptValue.Obj)var32_31).instance()) != null && !(var34_33 instanceof PolyClass) && var33_32.typeName().equals("Machine")) {
                        var35_34 = new PolyClassMachine_v2(var34_33);
                        v6 /* !! */  = ScriptValue.of((boolean)var35_34.tm$32_release_contraption());
                    } else {
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var32_31, (ScriptContext)var1_1);
                    }
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
            }
        } else {
            var36_35 = var1_1.getClassOrVar("Machine");
            if (var36_35 != ScriptValue.NULL) {
                if (var36_35 instanceof ScriptValue.Obj && (var38_37 = (var37_36 = (ScriptValue.Obj)var36_35).instance()) != null && !(var38_37 instanceof PolyClass) && var37_36.typeName().equals("Machine")) {
                    var39_38 = new PolyClassMachine_v2(var38_37);
                    v7 /* !! */  = ScriptValue.of((boolean)var39_38.tm$32_release_contraption());
                } else {
                    v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var36_35, (ScriptContext)var1_1);
                }
            } else {
                v7 /* !! */  = ScriptValue.NULL;
            }
        }
        BlockBreaker.FILE_SCOPE = var0.build();
    }
}
