/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class RainSensor {
    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = var1_1.getClassOrVar("World");
        if ((var2_2 != ScriptValue.NULL ? (var2_2 instanceof ScriptValue.Obj && (var4_4 = (var3_3 = (ScriptValue.Obj)var2_2).instance()) != null && !(var4_4 instanceof PolyClass) && var3_3.typeName().equals("World") ? new PolyClassWorld(var4_4).pg$32_is_raining() : PolyDispatch.bootstrapGet("memberGet", "is_raining", (ScriptValue)var2_2, (ScriptContext)var1_1)) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
        var5_5 = var1_1.getClassOrVar("World");
        if (!(var5_5 != ScriptValue.NULL ? (var5_5 instanceof ScriptValue.Obj && (var7_7 = (var6_6 = (ScriptValue.Obj)var5_5).instance()) != null && !(var7_7 instanceof PolyClass) && var6_6.typeName().equals("World") ? new PolyClassWorld(var7_7).pg$33_is_thundering() : PolyDispatch.bootstrapGet("memberGet", "is_thundering", (ScriptValue)var5_5, (ScriptContext)var1_1)) : ScriptValue.NULL).asBool()) {
            v0 = false;
        } else lbl-1000:
        // 2 sources

        {
            v0 = true;
        }
        if (v0) {
            var8_8 = var1_1.getClassOrVar("Machine");
            if (var8_8 != ScriptValue.NULL) {
                var9_9 = 15.0;
                if (var8_8 instanceof ScriptValue.Obj && (var12_11 = (var11_10 = (ScriptValue.Obj)var8_8).instance()) != null && !(var12_11 instanceof PolyClass) && var11_10.typeName().equals("Machine")) {
                    var13_12 = new PolyClassMachine_v4(var12_11);
                    v1 /* !! */  = ScriptValue.of((boolean)var13_12.tm$108_emit_redstone(var9_9));
                } else {
                    var14_13 = new ArrayList<ScriptValue>();
                    var14_13.add(ScriptValue.of((double)var9_9));
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)var8_8, var14_13, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
        } else {
            var15_14 = var1_1.getClassOrVar("Machine");
            if (var15_14 != ScriptValue.NULL) {
                var16_15 = 0.0;
                if (var15_14 instanceof ScriptValue.Obj && (var19_17 = (var18_16 = (ScriptValue.Obj)var15_14).instance()) != null && !(var19_17 instanceof PolyClass) && var18_16.typeName().equals("Machine")) {
                    var20_18 = new PolyClassMachine_v4(var19_17);
                    v2 /* !! */  = ScriptValue.of((boolean)var20_18.tm$108_emit_redstone(var16_15));
                } else {
                    var21_19 = new ArrayList<ScriptValue>();
                    var21_19.add(ScriptValue.of((double)var16_15));
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)var15_14, var21_19, (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
        }
    }
}
