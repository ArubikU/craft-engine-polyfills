/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.examples;

import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public final class ExampleFancyJoinMessage {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onJoin(ScriptContext.Builder builder) {
        PolyClassPlayer_v2 polyClassPlayer_v2;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("event");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_join_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)("<gray>[<gold>+</gold>] <yellow>" + ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer_v2.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()) + "<gray> stepped onto the server.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }
}
