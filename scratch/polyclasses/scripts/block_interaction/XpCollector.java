/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public final class XpCollector {
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
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            double d = 6.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$94_nearby_entities(d) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("entities", scriptValue2);
        double d = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("total_xp", scriptValue3);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue2);
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("entity", scriptValue5);
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:experience_orb")) continue;
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "xp_value", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder.val("total_xp", scriptValue6);
                scriptValue4 = scriptValue6;
                Object object2 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        if (scriptValue4.asNum() > 0.0) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "experience";
                ScriptValue scriptValue7 = scriptValue4;
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v2 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$98_fill_fluid(string, scriptValue7.asNum())) : PolyDispatch.bootstrapCall("memberCall", "fill_fluid", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
