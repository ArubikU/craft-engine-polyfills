/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Fertilizer {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassMachine_v3 polyClassMachine_v3;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "level";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("level_flag", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 1.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)1.0);
            builder.val("level_flag", scriptValue3);
        }
        ScriptValue scriptValue4 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("level_flag"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Fertilizer.class, 2.0)));
        builder.val("radius", scriptValue4);
        ScriptValue scriptValue5 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 0.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue5);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)(polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext)), (ScriptContext)scriptContext).asBool() ^ true) {
            Object object2;
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue scriptValue6 = scriptValue4;
                PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object2 = polyClassMachine_v33 != null ? polyClassMachine_v33.tm$86_blocks_in_range(scriptValue6.asNum()) : PolyDispatch.bootstrapCall("memberCall", "blocks_in_range", (ScriptValue)scriptValue, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = object2;
            builder.val("blocks", scriptValue7);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue7);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("ok");
            if (list != null) {
                for (ScriptValue scriptValue9 : list) {
                    PolyClassMachine_v3 polyClassMachine_v34;
                    builder.val("block", scriptValue9);
                    if (!((scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Fertilizer.class, "age")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() || (scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Fertilizer.class, "growth")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool())) continue;
                    ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "apply_bone_meal", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("ok", scriptValue10);
                    scriptValue8 = scriptValue10;
                    if (!scriptValue8.asBool()) continue;
                    ScriptValue scriptValue11 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v34.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    double d2 = 0.0;
                    double d3 = 1.0;
                    PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue11);
                    Object object3 = polyClassContainer2 != null ? polyClassContainer2.tm$6_remove_item(d2, d3) : PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                    break;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
