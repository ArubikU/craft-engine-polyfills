/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public final class RainSensor {
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
        var2_2 = var1_1.getClassOrVar("World");
        if (var2_2 != ScriptValue.NULL ? ((var3_3 = PolyClassWorld.ofGuarded((ScriptValue)var2_2)) != null ? var3_3.tg$39_is_raining() : PolyDispatch.bootstrapGet("memberGet", "is_raining", (ScriptValue)var2_2, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool()) ** GOTO lbl-1000
        var4_4 = var1_1.getClassOrVar("World");
        if (!(var4_4 != ScriptValue.NULL ? ((var5_5 = PolyClassWorld.ofGuarded((ScriptValue)var4_4)) != null ? var5_5.tg$41_is_thundering() : PolyDispatch.bootstrapGet("memberGet", "is_thundering", (ScriptValue)var4_4, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool())) {
            v0 = false;
        } else lbl-1000:
        // 2 sources

        {
            v0 = true;
        }
        if (v0) {
            var6_6 = var1_1.getClassOrVar("Machine");
            if (var6_6 != ScriptValue.NULL) {
                var7_7 = 15.0;
                if (var6_6 instanceof ScriptValue.Obj && (var10_9 = (var9_8 = (ScriptValue.Obj)var6_6).instance()) != null && !(var10_9 instanceof PolyClass) && var9_8.typeName().equals("Machine")) {
                    var11_10 = new PolyClassMachine_v2(var10_9);
                    v1 /* !! */  = ScriptValue.of((boolean)var11_10.tm$108_emit_redstone(var7_7));
                } else {
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)var6_6, (ScriptValue)ScriptValue.of((double)var7_7), (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
        } else {
            var12_11 = var1_1.getClassOrVar("Machine");
            if (var12_11 != ScriptValue.NULL) {
                var13_12 = 0.0;
                if (var12_11 instanceof ScriptValue.Obj && (var16_14 = (var15_13 = (ScriptValue.Obj)var12_11).instance()) != null && !(var16_14 instanceof PolyClass) && var15_13.typeName().equals("Machine")) {
                    var17_15 = new PolyClassMachine_v2(var16_14);
                    v2 /* !! */  = ScriptValue.of((boolean)var17_15.tm$108_emit_redstone(var13_12));
                } else {
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)var12_11, (ScriptValue)ScriptValue.of((double)var13_12), (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
        }
        RainSensor.FILE_SCOPE = var0.build();
    }
}
