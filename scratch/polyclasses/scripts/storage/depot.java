/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Depot {
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
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        double d = 9.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)9.0);
        builder.val("SIZE", scriptValue3);
        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        ScriptValue scriptValue4 = polyClassPlayer_v2 != null ? polyClassPlayer_v2.pg$48_main_hand() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("held", scriptValue4);
        PolyClassPlayer_v2 polyClassPlayer_v22 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        if ((polyClassPlayer_v22 != null ? polyClassPlayer_v22.tg$49_is_sneaking() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) || ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool()) {
            List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext));
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("ri");
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("slot_item");
            if (list != null) {
                for (ScriptValue scriptValue7 : list) {
                    ScriptValue scriptValue8;
                    ScriptValue scriptValue9;
                    builder.val("i", scriptValue7);
                    double d2 = d - 1.0 - scriptValue7.asNum();
                    ScriptValue scriptValue10 = ScriptValue.of((double)d2);
                    builder.val("ri", scriptValue10);
                    scriptValue5 = scriptValue10;
                    PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue11 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$120_container() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue12 = scriptValue5;
                    PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue11);
                    Object object = polyClassContainer != null ? polyClassContainer.tm$0_get_item(scriptValue12.asNum()) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                    builder.val("slot_item", object);
                    scriptValue6 = object;
                    if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    ScriptValue scriptValue13 = scriptContext.getClassOrVar("Player");
                    if (scriptValue13 != ScriptValue.NULL) {
                        ScriptValue scriptValue14 = scriptValue6;
                        PolyClassPlayer_v2 polyClassPlayer_v23 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue13);
                        v0 = polyClassPlayer_v23 != null ? ScriptValue.of((boolean)polyClassPlayer_v23.tm$14_give_item(scriptValue14)) : PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
                    } else {
                        v0 = ScriptValue.NULL;
                    }
                    PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue15 = polyClassMachine_v32 != null ? polyClassMachine_v32.pg$120_container() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue16 = scriptValue5;
                    ScriptValue scriptValue17 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                    PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue15);
                    CallSite callSite = polyClassContainer2 != null ? polyClassContainer2.tm$6_remove_item(scriptValue16.asNum(), scriptValue17.asNum()) : PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue15, (ScriptValue)scriptValue16, (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
                    break;
                }
            }
        } else {
            ScriptValue scriptValue18;
            ScriptValue scriptValue19 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            builder.val("held_count", scriptValue19);
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue20 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$120_container() : ((scriptValue18 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL);
            ScriptValue scriptValue21 = scriptValue4;
            PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue20);
            Object object = polyClassContainer != null ? polyClassContainer.tm$12_push(scriptValue21) : PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue20, (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
            builder.val("leftover", object);
            double d3 = scriptValue19.asNum() - (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)object, (ScriptContext)scriptContext).asBool() ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Depot.class, 0.0)) : ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)object, (ScriptContext)scriptContext)).asNum();
            ScriptValue scriptValue22 = ScriptValue.of((double)d3);
            builder.val("placed", scriptValue22);
            if (d3 > 0.0) {
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("Player");
                if (scriptValue23 != ScriptValue.NULL) {
                    String string = "main_hand";
                    double d4 = d3;
                    PolyClassPlayer_v2 polyClassPlayer_v24 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue23);
                    v2 = polyClassPlayer_v24 != null ? ScriptValue.of((boolean)polyClassPlayer_v24.tm$34_remove_item(string, d4)) : PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
                } else {
                    v2 = ScriptValue.NULL;
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
