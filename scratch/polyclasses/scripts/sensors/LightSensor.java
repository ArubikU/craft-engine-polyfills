/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public final class LightSensor {
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
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            double d = 0.0;
            double d2 = 1.0;
            double d3 = 0.0;
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine != null ? polyClassMachine.tm$68_block_at(d, d2, d3) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("b", scriptValue3);
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "combined_light", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("light", scriptValue4);
        if (scriptValue4.asNum() > 15.0) {
            double d = 15.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)15.0);
            builder.val("light", scriptValue5);
        }
        if (scriptContext.getNum("light") < 0.0) {
            double d = 0.0;
            ScriptValue scriptValue6 = ScriptValue.of((double)0.0);
            builder.val("light", scriptValue6);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("light");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$108_emit_redstone(scriptValue7.asNum())) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
