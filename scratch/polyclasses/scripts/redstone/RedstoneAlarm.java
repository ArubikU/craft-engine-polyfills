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
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public final class RedstoneAlarm {
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
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        Object object = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$171_redstone() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        if (object.asNum() > 0.0) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "minecraft:block.bell.use";
                double d = 2.0;
                double d2 = 1.0;
                PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v1 = polyClassMachine_v32 != null ? ScriptValue.of((boolean)polyClassMachine_v32.tm$14_play_sound(string, d, d2)) : PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d), (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
