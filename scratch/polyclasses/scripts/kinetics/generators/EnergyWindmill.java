/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public final class EnergyWindmill {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        double d = 80.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)80.0);
        builder.val("BASE", scriptValue2);
        double d2 = 120.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)120.0);
        builder.val("STORM_BONUS", scriptValue3);
        PolyClassWorld polyClassWorld = PolyClassWorld.ofVar((ScriptContext)scriptContext, (String)"World");
        if (polyClassWorld != null ? polyClassWorld.tg$41_is_thundering() : ((scriptValue = scriptContext.getClassOrVar("World")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_thundering", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                double d3 = d + d2;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
                v0 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$48_set_energy_per_tick(d3)) : PolyDispatch.bootstrapCall("memberCall", "set_energy_per_tick", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue5;
            PolyClassWorld polyClassWorld2 = PolyClassWorld.ofVar((ScriptContext)scriptContext, (String)"World");
            if (polyClassWorld2 != null ? polyClassWorld2.tg$39_is_raining() : ((scriptValue5 = scriptContext.getClassOrVar("World")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_raining", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                if (scriptValue6 != ScriptValue.NULL) {
                    double d4 = 2.0;
                    double d5 = d + (2.0 == 0.0 ? 0.0 : d2 / d4);
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6);
                    v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$48_set_energy_per_tick(d5)) : PolyDispatch.bootstrapCall("memberCall", "set_energy_per_tick", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
                } else {
                    v1 = ScriptValue.NULL;
                }
            } else {
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
                if (scriptValue7 != ScriptValue.NULL) {
                    double d6 = d;
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                    v2 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$48_set_energy_per_tick(d6)) : PolyDispatch.bootstrapCall("memberCall", "set_energy_per_tick", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
                } else {
                    v2 = ScriptValue.NULL;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
