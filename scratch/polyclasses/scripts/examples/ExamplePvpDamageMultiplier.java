/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.examples;

import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;

public final class ExamplePvpDamageMultiplier {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onDamage(ScriptContext.Builder builder) {
        block0: {
            ScriptValue scriptValue;
            ScriptValue scriptValue2;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("event");
            if (!(ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin1((String)"type_of", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "damager", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (String)"Player") && ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin1((String)"type_of", (ScriptValue)((scriptValue2 = scriptContext.getClassOrVar("event")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "entity", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (String)"Player"))) break block0;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("event");
            Object object = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_damage", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)(((scriptValue = scriptContext.getClassOrVar("event")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "damage", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() * 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
