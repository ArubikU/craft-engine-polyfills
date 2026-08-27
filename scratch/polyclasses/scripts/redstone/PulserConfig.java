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
public final class PulserConfig {
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
            String string = "dur_idx";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("idx", scriptValue2);
        double d = 6.0;
        double d2 = 6.0 == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PulserConfig.class, 1.0))).asNum() % d;
        ScriptValue scriptValue3 = ScriptValue.of((double)d2);
        builder.val("next_idx", scriptValue3);
        if (scriptValue != ScriptValue.NULL) {
            String string = "dur_idx";
            String string3 = "int";
            ScriptValue scriptValue4 = ScriptValue.of((double)d2);
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue4)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        double d3 = 5.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)5.0);
        builder.val("dur", scriptValue5);
        if (d2 == 1.0) {
            double d4 = 10.0;
            ScriptValue scriptValue6 = ScriptValue.of((double)10.0);
            builder.val("dur", scriptValue6);
        }
        if (d2 == 2.0) {
            double d5 = 20.0;
            ScriptValue scriptValue7 = ScriptValue.of((double)20.0);
            builder.val("dur", scriptValue7);
        }
        if (d2 == 3.0) {
            double d6 = 40.0;
            ScriptValue scriptValue8 = ScriptValue.of((double)40.0);
            builder.val("dur", scriptValue8);
        }
        if (d2 == 4.0) {
            double d7 = 60.0;
            ScriptValue scriptValue9 = ScriptValue.of((double)60.0);
            builder.val("dur", scriptValue9);
        }
        if (d2 == 5.0) {
            double d8 = 100.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)100.0);
            builder.val("dur", scriptValue10);
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "duration";
            String string4 = "int";
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("dur");
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v2 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue11)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "minecraft:block.stone_button.click_on";
            double d9 = 0.5;
            double d10 = 1.0;
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v3 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$14_play_sound(string, d9, d10)) : PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d9), (ScriptValue)ScriptValue.of((double)d10), (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
