/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBlock
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassBlock;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
        var4_4 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
        var6_6 = var4_4 != null ? var4_4.pg$137_facing_block() : ((var5_5 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var5_5, (ScriptContext)var1_1) : ScriptValue.NULL);
        var0.val("block", var6_6);
        v0 = var6_6 != ScriptValue.NULL ? ((var7_7 = PolyClassBlock.ofGuarded((ScriptValue)var6_6)) != null ? var7_7.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var6_6, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
        if (!(v0 ^ true)) ** GOTO lbl-1000
        v1 /* !! */  = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has", (ScriptValue)var3_3, (ScriptValue)(var6_6 != ScriptValue.NULL ? ((var8_8 = PolyClassBlock.ofGuarded((ScriptValue)var6_6)) != null ? var8_8.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var6_6, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptContext)var1_1) : ScriptValue.NULL;
        if (v1 /* !! */ .asBool() ^ true) {
            v2 = true;
        } else lbl-1000:
        // 2 sources

        {
            v2 = false;
        }
        if (v2) {
            var9_9 = var1_1.getClassOrVar("Machine");
            v3 /* !! */  = var9_9 != ScriptValue.NULL ? ((var10_10 = PolyClassMachine_v3.ofGuarded((ScriptValue)var9_9)) != null ? ScriptValue.of((boolean)var10_10.tm$102_hold_contraption()) : PolyDispatch.bootstrapCall("memberCall", "hold_contraption", (ScriptValue)var9_9, (ScriptContext)var1_1)) : ScriptValue.NULL;
            if (var9_9 != ScriptValue.NULL) {
                var11_11 = var6_6;
                var12_12 = 10.0;
                var14_13 = PolyClassMachine_v3.ofGuarded((ScriptValue)var9_9);
                v4 /* !! */  = var14_13 != null ? var14_13.tm$2_tick_break(var11_11, var12_12) : PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var9_9, (ScriptValue)var11_11, (ScriptValue)ScriptValue.of((double)var12_12), (ScriptContext)var1_1);
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            var15_14 = v4 /* !! */ ;
            var0.val("drops", var15_14);
            if (ScriptFormula.valuesEqual((ScriptValue)var15_14, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                var16_15 = ScriptProgram.elementsOf((ScriptValue)var15_14);
                if (var16_15 != null) {
                    for (ScriptValue var18_17 : var16_15) {
                        var0.val("drop", var18_17);
                        var19_18 = var9_9 != ScriptValue.NULL ? ((var20_19 = PolyClassMachine_v3.ofGuarded((ScriptValue)var9_9)) != null ? var20_19.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var9_9, (ScriptContext)var1_1)) : ScriptValue.NULL;
                        var21_20 = var18_17;
                        var22_21 = PolyClassContainer.ofGuarded((ScriptValue)var19_18);
                        v5 /* !! */  = var22_21 != null ? var22_21.tm$12_push(var21_20) : PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)var19_18, (ScriptValue)var21_20, (ScriptContext)var1_1);
                    }
                }
                v6 /* !! */  = var9_9 != ScriptValue.NULL ? ((var23_22 = PolyClassMachine_v3.ofGuarded((ScriptValue)var9_9)) != null ? ScriptValue.of((boolean)var23_22.tm$32_release_contraption()) : PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var9_9, (ScriptContext)var1_1)) : ScriptValue.NULL;
            }
        } else {
            var24_23 = var1_1.getClassOrVar("Machine");
            v7 /* !! */  = var24_23 != ScriptValue.NULL ? ((var25_24 = PolyClassMachine_v3.ofGuarded((ScriptValue)var24_23)) != null ? ScriptValue.of((boolean)var25_24.tm$32_release_contraption()) : PolyDispatch.bootstrapCall("memberCall", "release_contraption", (ScriptValue)var24_23, (ScriptContext)var1_1)) : ScriptValue.NULL;
        }
        BlockBreaker.FILE_SCOPE = var0.build();
    }
}
