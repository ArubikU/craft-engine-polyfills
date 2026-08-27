/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.examples;

import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public final class ExampleDeepMiningGuard {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("event");
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)object, (ScriptContext)scriptContext), (ScriptContext)scriptContext).asNum() <= -1000.0) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("event");
            Object object2 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "cancel", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                String string = "<red>Mining this deep is off-limits here.";
                PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue3);
                v2 = polyClassPlayer != null ? ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue4;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("event");
            Object object3 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_exp_to_drop", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)(((scriptValue4 = scriptContext.getClassOrVar("event")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "exp_to_drop", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() * 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
