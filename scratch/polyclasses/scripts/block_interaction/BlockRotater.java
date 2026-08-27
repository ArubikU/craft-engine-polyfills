/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("block", scriptValue2);
        if ((scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("block");
            if ((scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BlockRotater.class, "facing")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("block");
                Object object = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BlockRotater.class, "facing")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            } else {
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("block");
                if ((scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BlockRotater.class, "horizontal_facing")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                    ScriptValue scriptValue6 = scriptContext.getClassOrVar("block");
                    Object object = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "cycle_prop", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BlockRotater.class, "horizontal_facing")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
