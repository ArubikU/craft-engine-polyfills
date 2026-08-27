/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine;
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
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "level";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("level_flag", scriptValue3);
        if (scriptValue3.asNum() <= 0.0) {
            double d = 1.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)1.0);
            builder.val("level_flag", scriptValue4);
        }
        ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("level_flag"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Fertilizer.class, 2.0)));
        builder.val("radius", scriptValue5);
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue6 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 0.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue6);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)(polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext)), (ScriptContext)scriptContext).asBool() ^ true) {
            Object object2;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue scriptValue8 = scriptValue5;
                PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                object2 = polyClassMachine2 != null ? polyClassMachine2.tm$86_blocks_in_range(scriptValue8.asNum()) : PolyDispatch.bootstrapCall("memberCall", "blocks_in_range", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = object2;
            builder.val("blocks", scriptValue9);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue9);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("ok");
            if (list != null) {
                for (ScriptValue scriptValue11 : list) {
                    ScriptValue scriptValue12;
                    builder.val("block", scriptValue11);
                    if (!((scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Fertilizer.class, "age")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() || (scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Fertilizer.class, "growth")), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool())) continue;
                    ScriptValue scriptValue13 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "apply_bone_meal", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("ok", scriptValue13);
                    scriptValue10 = scriptValue13;
                    if (!scriptValue10.asBool()) continue;
                    PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue14 = polyClassMachine3 != null ? polyClassMachine3.pg$120_container() : ((scriptValue12 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    double d2 = 0.0;
                    double d3 = 1.0;
                    PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue14);
                    Object object3 = polyClassContainer2 != null ? polyClassContainer2.tm$6_remove_item(d2, d3) : PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                    break;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
