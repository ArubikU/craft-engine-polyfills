/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassNetwork
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassNetwork;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class WirelessRedstone {
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
            String string = "_wr_mode";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("mode", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            String string = "_wr_ch";
            String string3 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("ch", scriptValue4);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstone.class, 0.0)))) {
            ScriptValue scriptValue5;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Network");
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                PolyClassNetwork polyClassNetwork = PolyClassNetwork.ofGuarded((ScriptValue)scriptValue6);
                v2 = polyClassNetwork != null ? polyClassNetwork.um$13_register(arrayList) : PolyDispatch.bootstrapCall("memberCall", "register", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue7 = polyClassMachine != null ? polyClassMachine.pg$171_redstone() : ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("power", scriptValue7);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Network");
            if (scriptValue8 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                arrayList.add(scriptValue7);
                PolyClassNetwork polyClassNetwork = PolyClassNetwork.ofGuarded((ScriptValue)scriptValue8);
                v3 = polyClassNetwork != null ? polyClassNetwork.um$1_broadcast(arrayList) : PolyDispatch.bootstrapCall("memberCall", "broadcast", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                double d = 0.0;
                PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9);
                v4 = polyClassMachine2 != null ? ScriptValue.of((boolean)polyClassMachine2.tm$108_emit_redstone(d)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
        } else {
            Object object3;
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Network");
            if (scriptValue10 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                PolyClassNetwork polyClassNetwork = PolyClassNetwork.ofGuarded((ScriptValue)scriptValue10);
                object3 = polyClassNetwork != null ? polyClassNetwork.um$9_listen(arrayList) : PolyDispatch.bootstrapCall("memberCall", "listen", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = object3;
            builder.val("power", scriptValue11);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                double d = Math.floor(ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstone.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstone.class, 15.0)), (ScriptContext)scriptContext).asNum());
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue12);
                v6 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$108_emit_redstone(d)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Network");
            if (scriptValue13 != ScriptValue.NULL) {
                ArrayList arrayList = new ArrayList();
                PolyClassNetwork polyClassNetwork = PolyClassNetwork.ofGuarded((ScriptValue)scriptValue13);
                v7 = polyClassNetwork != null ? polyClassNetwork.um$7_unregister(arrayList) : PolyDispatch.bootstrapCall("memberCall", "unregister", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
