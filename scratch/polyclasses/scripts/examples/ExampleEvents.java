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

public final class ExampleEvents {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onJoin(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue2;
            PolyClassPlayer_v2 polyClassPlayer_v2;
            ScriptValue scriptValue3 = ScriptValue.of((String)("<green>Welcome, " + ((polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer_v2.tg$68_name() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())) + "!"));
            PolyClassPlayer_v2 polyClassPlayer_v22 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassPlayer_v22 != null ? ScriptValue.of((boolean)polyClassPlayer_v22.tm$42_send_message(scriptValue3.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
