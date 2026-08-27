/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBlock
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassBlock;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public final class BlockRotater {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassBlock polyClassBlock;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("block", scriptValue2);
        boolean bl = scriptValue2 != ScriptValue.NULL ? ((polyClassBlock = PolyClassBlock.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassBlock.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl ^ true) {
            Object object;
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "facing";
                PolyClassBlock polyClassBlock2 = PolyClassBlock.ofGuarded((ScriptValue)scriptValue2);
                object = polyClassBlock2 != null ? ScriptValue.of((boolean)polyClassBlock2.tm$28_has_property(string)) : PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            if (object.asBool()) {
                if (scriptValue2 != ScriptValue.NULL) {
                    String string = "facing";
                    PolyClassBlock polyClassBlock3 = PolyClassBlock.ofGuarded((ScriptValue)scriptValue2);
                    v2 = polyClassBlock3 != null ? ScriptValue.of((boolean)polyClassBlock3.tm$0_cycle_prop(string)) : PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v2 = ScriptValue.NULL;
                }
            } else {
                Object object2;
                if (scriptValue2 != ScriptValue.NULL) {
                    String string = "horizontal_facing";
                    PolyClassBlock polyClassBlock4 = PolyClassBlock.ofGuarded((ScriptValue)scriptValue2);
                    object2 = polyClassBlock4 != null ? ScriptValue.of((boolean)polyClassBlock4.tm$28_has_property(string)) : PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                if (object2.asBool()) {
                    if (scriptValue2 != ScriptValue.NULL) {
                        String string = "horizontal_facing";
                        PolyClassBlock polyClassBlock5 = PolyClassBlock.ofGuarded((ScriptValue)scriptValue2);
                        v4 = polyClassBlock5 != null ? ScriptValue.of((boolean)polyClassBlock5.tm$0_cycle_prop(string)) : PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    } else {
                        v4 = ScriptValue.NULL;
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
