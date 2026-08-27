/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            double d = 0.0;
            double d2 = 1.0;
            double d3 = 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$68_block_at(d, d2, d3) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("b", scriptValue2);
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "combined_light", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("light", scriptValue3);
        if (scriptValue3.asNum() > 15.0) {
            double d = 15.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)15.0);
            builder.val("light", scriptValue4);
        }
        if (scriptContext.getNum("light") < 0.0) {
            double d = 0.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)0.0);
            builder.val("light", scriptValue5);
        }
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("light");
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$108_emit_redstone(scriptValue6.asNum())) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
