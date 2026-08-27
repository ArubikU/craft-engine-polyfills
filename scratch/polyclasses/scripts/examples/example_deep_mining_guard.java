/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.examples;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

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
            if (scriptValue2 != ScriptValue.NULL) {
                ArrayList arrayList = new ArrayList();
                v1 = PolyDispatch.bootstrapCall("memberCall", "cancel", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "<red>Mining this deep is off-limits here.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("event");
            if (scriptValue4 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("event");
                arrayList.add(ScriptValue.of((double)((scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "exp_to_drop", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() * 2.0)));
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_exp_to_drop", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }
}
