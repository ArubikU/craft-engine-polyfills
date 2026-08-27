/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class RedstoneLooper {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "interval";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("interval", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 1.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)1.0);
            builder.val("interval", scriptValue3);
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "_loop_t";
            String string3 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = ScriptFormula.addPolymorphic((ScriptValue)object, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", RedstoneLooper.class, 1.0)));
        builder.val("t", scriptValue4);
        if (scriptValue != ScriptValue.NULL) {
            String string = "_loop_t";
            String string4 = "int";
            ScriptValue scriptValue5 = scriptValue4;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v2 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue5)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        double d = scriptContext.getNum("interval") * 2.0;
        ScriptValue scriptValue6 = ScriptValue.of((double)d);
        builder.val("full_cycle", scriptValue6);
        double d2 = d;
        double d3 = d2 == 0.0 ? 0.0 : scriptValue4.asNum() % d2;
        ScriptValue scriptValue7 = ScriptValue.of((double)d3);
        builder.val("phase", scriptValue7);
        if (scriptValue != ScriptValue.NULL) {
            double d4 = d3 < scriptContext.getNum("interval") ? 15.0 : 0.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v3 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$108_emit_redstone(d4)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
