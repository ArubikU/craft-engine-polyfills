/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.examples;

import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

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
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("event");
        if (scriptValue != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ScriptValue scriptValue2;
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>[<gold>+</gold>] <yellow>"), (ScriptValue)((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer.pg$48_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)"<gray> stepped onto the server.")));
            v0 = PolyDispatch.bootstrapCall("memberCall", "set_join_message", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
