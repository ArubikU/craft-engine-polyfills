/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassBlock_v2;
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
public final class BlockDispenser {
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
        if (bl) {
            boolean bl2 = false;
            ScriptValue scriptValue3 = ScriptValue.of((boolean)false);
            builder.val("placed", scriptValue3);
            List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockDispenser.class, 27.0)), (ScriptContext)scriptContext));
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("item");
            ScriptValue scriptValue5 = ScriptValue.of((boolean)bl2);
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("ok");
            if (list != null) {
                for (ScriptValue scriptValue7 : list) {
                    ScriptValue scriptValue8;
                    Object object;
                    ScriptValue scriptValue9;
                    builder.val("i", scriptValue7);
                    if (!(scriptValue5.asBool() ^ true)) continue;
                    PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue10 = polyClassMachine2 != null ? polyClassMachine2.pg$120_container() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue11 = scriptValue7;
                    PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue10);
                    Object object2 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(scriptValue11.asNum()) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                    builder.val("item", object2);
                    scriptValue4 = object2;
                    if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    if (scriptValue2 != ScriptValue.NULL) {
                        ScriptValue scriptValue12 = scriptValue4;
                        PolyClassBlock_v2 polyClassBlock_v22 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2);
                        object = polyClassBlock_v22 != null ? ScriptValue.of((boolean)polyClassBlock_v22.tm$30_place_from_item(scriptValue12)) : PolyDispatch.bootstrapCall("memberCall", "place_from_item", (ScriptValue)scriptValue2, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                    } else {
                        object = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue13 = object;
                    builder.val("ok", scriptValue13);
                    scriptValue6 = scriptValue13;
                    if (!scriptValue6.asBool()) continue;
                    PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue14 = polyClassMachine3 != null ? polyClassMachine3.pg$120_container() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue15 = scriptValue7;
                    double d = 1.0;
                    PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue14);
                    Object object3 = polyClassContainer2 != null ? polyClassContainer2.tm$6_remove_item(scriptValue15.asNum(), d) : PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                    boolean bl3 = true;
                    ScriptValue scriptValue16 = ScriptValue.of((boolean)true);
                    builder.val("placed", scriptValue16);
                    scriptValue5 = scriptValue16;
                }
            }
        } else {
            PolyClassBlock_v2 polyClassBlock_v23;
            ScriptValue scriptValue17 = scriptValue2 != ScriptValue.NULL ? ((polyClassBlock_v23 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassBlock_v23.tm$20_break_and_drop() : PolyDispatch.bootstrapCall("memberCall", "break_and_drop", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("drops", scriptValue17);
            List list = ScriptProgram.elementsOf((ScriptValue)scriptValue17);
            if (list != null) {
                for (ScriptValue scriptValue18 : list) {
                    ScriptValue scriptValue19;
                    builder.val("drop", scriptValue18);
                    PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue20 = polyClassMachine4 != null ? polyClassMachine4.pg$120_container() : ((scriptValue19 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue21 = scriptValue18;
                    PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue20);
                    Object object = polyClassContainer != null ? polyClassContainer.tm$12_push(scriptValue21) : PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue20, (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
