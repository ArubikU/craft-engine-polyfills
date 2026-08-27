/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.examples;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

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
            ScriptValue.Obj obj;
            Object object;
            PolyClassPlayer polyClassPlayer;
            ScriptValue scriptValue2;
            ScriptValue scriptValue3 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<green>Welcome, "), (ScriptValue)((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer.pg$48_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)"!"));
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(scriptValue3.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
