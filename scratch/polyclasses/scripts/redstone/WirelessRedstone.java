/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassNetwork
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("mode", scriptValue2);
        if (scriptValue != ScriptValue.NULL) {
            String string = "_wr_ch";
            String string3 = "int";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("ch", scriptValue3);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstone.class, 0.0)))) {
            PolyClassMachine_v3 polyClassMachine_v3;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Network");
            if (scriptValue4 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                PolyClassNetwork polyClassNetwork = PolyClassNetwork.ofGuarded((ScriptValue)scriptValue4);
                v2 = polyClassNetwork != null ? polyClassNetwork.um$13_register(arrayList) : PolyDispatch.bootstrapCall("memberCall", "register", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$171_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("power", scriptValue5);
            if (scriptValue4 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                arrayList.add(scriptValue5);
                PolyClassNetwork polyClassNetwork = PolyClassNetwork.ofGuarded((ScriptValue)scriptValue4);
                v3 = polyClassNetwork != null ? polyClassNetwork.um$1_broadcast(arrayList) : PolyDispatch.bootstrapCall("memberCall", "broadcast", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            if (scriptValue != ScriptValue.NULL) {
                double d = 0.0;
                PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v4 = polyClassMachine_v32 != null ? ScriptValue.of((boolean)polyClassMachine_v32.tm$108_emit_redstone(d)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
        } else {
            Object object3;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Network");
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                PolyClassNetwork polyClassNetwork = PolyClassNetwork.ofGuarded((ScriptValue)scriptValue6);
                object3 = polyClassNetwork != null ? polyClassNetwork.um$9_listen(arrayList) : PolyDispatch.bootstrapCall("memberCall", "listen", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = object3;
            builder.val("power", scriptValue7);
            if (scriptValue != ScriptValue.NULL) {
                double d = Math.floor(ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstone.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstone.class, 15.0)), (ScriptContext)scriptContext).asNum());
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v6 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$108_emit_redstone(d)) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
            if (scriptValue6 != ScriptValue.NULL) {
                ArrayList arrayList = new ArrayList();
                PolyClassNetwork polyClassNetwork = PolyClassNetwork.ofGuarded((ScriptValue)scriptValue6);
                v7 = polyClassNetwork != null ? polyClassNetwork.um$7_unregister(arrayList) : PolyDispatch.bootstrapCall("memberCall", "unregister", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
