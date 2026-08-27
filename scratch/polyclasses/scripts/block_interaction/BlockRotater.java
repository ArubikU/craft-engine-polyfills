/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassMachine;
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
        PolyClassBlock_v2 polyClassBlock_v2;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("block", scriptValue2);
        boolean bl = scriptValue2 != ScriptValue.NULL ? ((polyClassBlock_v2 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassBlock_v2.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool()) : ScriptValue.NULL.asBool();
        if (bl ^ true) {
            Object object;
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "facing";
                PolyClassBlock_v2 polyClassBlock_v22 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2);
                object = polyClassBlock_v22 != null ? ScriptValue.of((boolean)polyClassBlock_v22.tm$28_has_property(string)) : PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            if (object.asBool()) {
                if (scriptValue2 != ScriptValue.NULL) {
                    String string = "facing";
                    PolyClassBlock_v2 polyClassBlock_v23 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2);
                    v2 = polyClassBlock_v23 != null ? ScriptValue.of((boolean)polyClassBlock_v23.tm$0_cycle_prop(string)) : PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v2 = ScriptValue.NULL;
                }
            } else {
                Object object2;
                if (scriptValue2 != ScriptValue.NULL) {
                    String string = "horizontal_facing";
                    PolyClassBlock_v2 polyClassBlock_v24 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2);
                    object2 = polyClassBlock_v24 != null ? ScriptValue.of((boolean)polyClassBlock_v24.tm$28_has_property(string)) : PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                if (object2.asBool()) {
                    if (scriptValue2 != ScriptValue.NULL) {
                        String string = "horizontal_facing";
                        PolyClassBlock_v2 polyClassBlock_v25 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2);
                        v4 = polyClassBlock_v25 != null ? ScriptValue.of((boolean)polyClassBlock_v25.tm$0_cycle_prop(string)) : PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                    } else {
                        v4 = ScriptValue.NULL;
                    }
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
