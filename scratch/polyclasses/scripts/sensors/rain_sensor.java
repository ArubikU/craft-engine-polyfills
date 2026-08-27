/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

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
        if ((var2_2 != ScriptValue.NULL ? ((var3_3 = PolyClassWorld.ofGuarded((ScriptValue)var2_2)) != null ? var3_3.pg$32_is_raining() : PolyDispatch.bootstrapGet("memberGet", "is_raining", (ScriptValue)var2_2, (ScriptContext)var1_1)) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
        var4_4 = var1_1.getClassOrVar("World");
        if (!(var4_4 != ScriptValue.NULL ? ((var5_5 = PolyClassWorld.ofGuarded((ScriptValue)var4_4)) != null ? var5_5.pg$33_is_thundering() : PolyDispatch.bootstrapGet("memberGet", "is_thundering", (ScriptValue)var4_4, (ScriptContext)var1_1)) : ScriptValue.NULL).asBool()) {
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
                    var11_10 = new PolyClassMachine(var10_9);
                    v1 /* !! */  = ScriptValue.of((boolean)var11_10.tm$108_emit_redstone(var7_7));
                } else {
                    var12_11 = new ArrayList<ScriptValue>();
                    var12_11.add(ScriptValue.of((double)var7_7));
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)var6_6, var12_11, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
        } else {
            var13_12 = var1_1.getClassOrVar("Machine");
            if (var13_12 != ScriptValue.NULL) {
                var14_13 = 0.0;
                if (var13_12 instanceof ScriptValue.Obj && (var17_15 = (var16_14 = (ScriptValue.Obj)var13_12).instance()) != null && !(var17_15 instanceof PolyClass) && var16_14.typeName().equals("Machine")) {
                    var18_16 = new PolyClassMachine(var17_15);
                    v2 /* !! */  = ScriptValue.of((boolean)var18_16.tm$108_emit_redstone(var14_13));
                } else {
                    var19_17 = new ArrayList<ScriptValue>();
                    var19_17.add(ScriptValue.of((double)var14_13));
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)var13_12, var19_17, (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
        }
        RainSensor.FILE_SCOPE = var0.build();
    }
}
