/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassRedstone
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassRedstone;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public final class EyeSensor {
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
            double d = 16.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$30_is_player_looking_at(d)) : PolyDispatch.bootstrapCall("memberCall", "is_player_looking_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool()) {
            ScriptValue scriptValue2;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue3 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$171_redstone() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d = 15.0;
            PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue3);
            Object object2 = polyClassRedstone != null ? ScriptValue.of((boolean)polyClassRedstone.tm$0_set(d)) : PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            ScriptValue scriptValue4;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue5 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$171_redstone() : ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL);
            PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue5);
            Object object3 = polyClassRedstone != null ? ScriptValue.of((boolean)polyClassRedstone.tm$8_off()) : PolyDispatch.bootstrapCall("memberCall", "off", (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        }
        FILE_SCOPE = builder.build();
    }
}
