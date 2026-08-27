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
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Backpack {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onPlaceBlock(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            String string = "<green>Backpack unpacked into a storage block.";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue canStore(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("item");
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "enchantments", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (PolyDispatch.bootstrapGet("memberGet", "size", (ScriptValue)object, (ScriptContext)scriptContext).asNum() > 0.0) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "<red>That won't fit - enchanted items stay out of the backpack.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
                v1 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Backpack.class, 0);
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Backpack.class, 1);
    }
}
