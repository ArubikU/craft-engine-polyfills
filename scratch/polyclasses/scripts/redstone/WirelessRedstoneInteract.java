/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class WirelessRedstoneInteract {
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
        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (polyClassPlayer_v2 != null ? polyClassPlayer_v2.tg$49_is_sneaking() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_sneaking", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) {
            Object object;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "_wr_mode";
                String string2 = "int";
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = object;
            builder.val("mode", scriptValue3);
            double d = ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstoneInteract.class, 0.0))) ? 1.0 : 0.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)d);
            builder.val("new_mode", scriptValue4);
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "_wr_mode";
                String string3 = "int";
                ScriptValue scriptValue5 = ScriptValue.of((double)d);
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue5)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            if (d == 0.0) {
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
                if (scriptValue6 != ScriptValue.NULL) {
                    String string = "<green>Wireless Redstone: TRANSMITTER";
                    PolyClassPlayer_v2 polyClassPlayer_v22 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue6);
                    v2 = polyClassPlayer_v22 != null ? ScriptValue.of((boolean)polyClassPlayer_v22.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v2 = ScriptValue.NULL;
                }
            } else {
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
                if (scriptValue7 != ScriptValue.NULL) {
                    String string = "<aqua>Wireless Redstone: RECEIVER";
                    PolyClassPlayer_v2 polyClassPlayer_v23 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue7);
                    v3 = polyClassPlayer_v23 != null ? ScriptValue.of((boolean)polyClassPlayer_v23.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                } else {
                    v3 = ScriptValue.NULL;
                }
            }
        } else {
            Object object;
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                String string = "_wr_ch";
                String string4 = "int";
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue8);
                object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string4) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = object;
            builder.val("ch", scriptValue9);
            double d = 16.0;
            double d2 = 16.0 == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstoneInteract.class, 1.0))).asNum() % d;
            ScriptValue scriptValue10 = ScriptValue.of((double)d2);
            builder.val("new_ch", scriptValue10);
            if (scriptValue8 != ScriptValue.NULL) {
                String string = "_wr_ch";
                String string5 = "int";
                ScriptValue scriptValue11 = ScriptValue.of((double)d2);
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue8);
                v5 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string5, scriptValue11)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue scriptValue13 = ScriptValue.of((String)("<yellow>Channel: " + ScriptFormula.numToStr((double)d2)));
                PolyClassPlayer_v2 polyClassPlayer_v24 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue12);
                v6 = polyClassPlayer_v24 != null ? ScriptValue.of((boolean)polyClassPlayer_v24.tm$42_send_message(scriptValue13.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
