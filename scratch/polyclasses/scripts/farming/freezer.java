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
        block16: {
            var1_1 = var0.peek();
            var2_2 = new ArrayList<ScriptValue>();
            var2_2.add(ScriptValue.of((String)"minecraft:water"));
            var2_2.add(ScriptValue.of((String)"minecraft:ice"));
            var3_3 = ScriptFormula.callBuiltin((String)"make_map", var2_2, (ScriptContext)var1_1);
            var0.val("WATER_TO_ICE", var3_3);
            var4_4 = var1_1.getClassOrVar("Machine");
            if (var4_4 != ScriptValue.NULL) {
                var5_5 = 4.0;
                if (var4_4 instanceof ScriptValue.Obj && (var8_7 = (var7_6 = (ScriptValue.Obj)var4_4).instance()) != null && !(var8_7 instanceof PolyClass) && var7_6.typeName().equals("Machine")) {
                    var9_8 = new PolyClassMachine(var8_7);
                    v0 /* !! */  = var9_8.tm$86_blocks_in_range(var5_5);
                } else {
                    var10_9 = new ArrayList<ScriptValue>();
                    var10_9.add(ScriptValue.of((double)var5_5));
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "blocks_in_range", (ScriptValue)var4_4, var10_9, (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var11_10 = v0 /* !! */ ;
            var0.val("blocks", var11_10);
            var12_11 = ScriptProgram.rowsOf((ScriptValue)var11_10, (int)1);
            if (var12_11 != null) {
                for (ScriptValue[] var14_13 : var12_11) {
                    var0.val("block", var14_13.length > 0 ? var14_13[0] : ScriptValue.NULL);
                    var15_14 = var1_1.getClassOrVar("WATER_TO_ICE");
                    if (var15_14 != ScriptValue.NULL) {
                        var16_15 = new ArrayList<ScriptValue>();
                        var17_16 = var1_1.getClassOrVar("block");
                        var16_15.add((ScriptValue)(var17_16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var17_16, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var16_15.add(var1_1.getClassOrVar("null"));
                        v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var15_14, var16_15, (ScriptContext)var1_1);
                    } else {
                        v1 /* !! */  = ScriptValue.NULL;
                    }
                    var18_17 = v1 /* !! */ ;
                    var0.val("ice_id", var18_17);
                    if (!(ScriptFormula.valuesEqual((ScriptValue)var18_17, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                    var19_18 = new ArrayList<CallSite>();
                    var20_19 = var1_1.getClassOrVar("block");
                    var19_18.add(PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(var20_19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var20_19, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                    var21_20 = var1_1.getClassOrVar("block");
                    var19_18.add(PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var21_20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var21_20, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                    var22_21 = var1_1.getClassOrVar("block");
                    var19_18.add(PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(var22_21 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var22_21, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                    var19_18.add((CallSite)var18_17);
                    var23_22 = var1_1.getClassOrVar("block");
                    PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)(var23_22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)var23_22, (ScriptContext)var1_1) : ScriptValue.NULL), var19_18, (ScriptContext)var1_1);
                }
            }
            if ((var24_23 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                var25_24 = 4.0;
                if (var24_23 instanceof ScriptValue.Obj && (var28_26 = (var27_25 = (ScriptValue.Obj)var24_23).instance()) != null && !(var28_26 instanceof PolyClass) && var27_25.typeName().equals("Machine")) {
                    var29_27 = new PolyClassMachine(var28_26);
                    v2 /* !! */  = var29_27.tm$94_nearby_entities(var25_24);
                } else {
                    var30_28 = new ArrayList<ScriptValue>();
                    var30_28.add(ScriptValue.of((double)var25_24));
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var24_23, var30_28, (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
            var31_29 = v2 /* !! */ ;
            var0.val("entities", var31_29);
            var32_30 = ScriptProgram.rowsOf((ScriptValue)var31_29, (int)1);
            if (var32_30 == null) break block16;
            for (ScriptValue[] var34_32 : var32_30) {
                var0.val("entity", var34_32.length > 0 ? var34_32[0] : ScriptValue.NULL);
                var35_33 = var1_1.getClassOrVar("entity");
                if (!(var35_33 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var35_33, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
                var36_34 = new ArrayList<ScriptValue>();
                var36_34.add(var1_1.getClassOrVar("entity"));
                var36_34.add(ScriptValue.of((String)"LivingEntity"));
                if (ScriptFormula.callBuiltin((String)"instanceof", var36_34, (ScriptContext)var1_1).asBool()) {
                    v3 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v3 = false;
                }
                if (!v3) continue;
                var37_35 = var1_1.getClassOrVar("entity");
                if (var37_35 != ScriptValue.NULL) {
                    var38_36 = new ArrayList<ScriptValue>();
                    var38_36.add(ScriptValue.of((double)120.0));
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "freeze", (ScriptValue)var37_35, var38_36, (ScriptContext)var1_1);
                    continue;
                }
                v4 /* !! */  = ScriptValue.NULL;
            }
        }
        Freezer.FILE_SCOPE = var0.build();
    }
}
