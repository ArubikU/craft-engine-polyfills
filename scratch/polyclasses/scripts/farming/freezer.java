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
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
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
public final class Freezer {
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
        block13: {
            var1_1 = var0.peek();
            var2_2 = new ArrayList<ScriptValue>();
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Freezer.class, "minecraft:water"));
            var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Freezer.class, "minecraft:ice"));
            var3_3 = ScriptFormula.callBuiltin((String)"make_map", var2_2, (ScriptContext)var1_1);
            var0.val("WATER_TO_ICE", var3_3);
            var4_4 = var1_1.getClassOrVar("Machine");
            if (var4_4 != ScriptValue.NULL) {
                var5_5 = 4.0;
                if (var4_4 instanceof ScriptValue.Obj && (var8_7 = (var7_6 = (ScriptValue.Obj)var4_4).instance()) != null && !(var8_7 instanceof PolyClass) && var7_6.typeName().equals("Machine")) {
                    var9_8 = new PolyClassMachine_v2(var8_7);
                    v0 /* !! */  = var9_8.tm$86_blocks_in_range(var5_5);
                } else {
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "blocks_in_range", (ScriptValue)var4_4, (ScriptValue)ScriptValue.of((double)var5_5), (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var10_9 = v0 /* !! */ ;
            var0.val("blocks", var10_9);
            var11_10 = ScriptProgram.elementsOf((ScriptValue)var10_9);
            if (var11_10 != null) {
                for (ScriptValue var13_12 : var11_10) {
                    var0.val("block", var13_12);
                    var14_13 = var1_1.getClassOrVar("WATER_TO_ICE");
                    var16_15 = var14_13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var14_13, (ScriptValue)((var15_14 = var1_1.getClassOrVar("block")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var15_14, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var1_1.getClassOrVar("null"), (ScriptContext)var1_1) : ScriptValue.NULL;
                    var0.val("ice_id", var16_15);
                    if (!(ScriptFormula.valuesEqual((ScriptValue)var16_15, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                    var18_17 = var1_1.getClassOrVar("block");
                    var19_18 = var1_1.getClassOrVar("block");
                    var20_19 = var1_1.getClassOrVar("block");
                    var17_16 = var1_1.getClassOrVar("block");
                    PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)(var17_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)var17_16, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(var18_17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var18_17, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var19_18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var19_18, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(var20_19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var20_19, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1), (ScriptValue)var16_15, (ScriptContext)var1_1);
                }
            }
            if ((var21_20 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                var22_21 = 4.0;
                if (var21_20 instanceof ScriptValue.Obj && (var25_23 = (var24_22 = (ScriptValue.Obj)var21_20).instance()) != null && !(var25_23 instanceof PolyClass) && var24_22.typeName().equals("Machine")) {
                    var26_24 = new PolyClassMachine_v2(var25_23);
                    v1 /* !! */  = var26_24.tm$94_nearby_entities(var22_21);
                } else {
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var21_20, (ScriptValue)ScriptValue.of((double)var22_21), (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            var27_25 = v1 /* !! */ ;
            var0.val("entities", var27_25);
            var28_26 = ScriptProgram.elementsOf((ScriptValue)var27_25);
            if (var28_26 == null) break block13;
            for (ScriptValue var30_28 : var28_26) {
                var0.val("entity", var30_28);
                var31_29 = var1_1.getClassOrVar("entity");
                if (!(var31_29 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var31_29, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
                var32_30 = new ArrayList<ScriptValue>();
                var32_30.add(var1_1.getClassOrVar("entity"));
                var32_30.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Freezer.class, "LivingEntity"));
                if (ScriptFormula.callBuiltin((String)"instanceof", var32_30, (ScriptContext)var1_1).asBool()) {
                    v2 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v2 = false;
                }
                if (!v2) continue;
                var33_31 = var1_1.getClassOrVar("entity");
                v3 /* !! */  = var33_31 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "freeze", (ScriptValue)var33_31, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Freezer.class, 120.0)), (ScriptContext)var1_1) : ScriptValue.NULL;
            }
        }
        Freezer.FILE_SCOPE = var0.build();
    }
}
