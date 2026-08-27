/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyClassNetwork
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
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
            ScriptValue.Obj obj;
            Object object3;
            String string = "_wr_mode";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("mode", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "_wr_ch";
            String string3 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                object = polyClassMachine_v4.tm$34_get_typed(string, string3);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("ch", scriptValue4);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstone.class, 0.0)))) {
            ScriptValue scriptValue5;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Network");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                v2 = scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Network") ? new PolyClassNetwork(object5).um$13_register(arrayList) : PolyDispatch.bootstrapCall("memberCall", "register", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue7 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$171_redstone() : ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL);
            builder.val("power", scriptValue7);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Network");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                arrayList.add(scriptValue7);
                v3 = scriptValue8 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Network") ? new PolyClassNetwork(object6).um$1_broadcast(arrayList) : PolyDispatch.bootstrapCall("memberCall", "broadcast", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                double d = 0.0;
                if (scriptValue9 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object7);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v42.tm$108_emit_redstone(d));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
        } else {
            Object object8;
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Network");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                object8 = scriptValue10 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Network") ? new PolyClassNetwork(object9).um$9_listen(arrayList) : PolyDispatch.bootstrapCall("memberCall", "listen", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
            } else {
                object8 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = object8;
            builder.val("power", scriptValue11);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                double d = Math.floor(ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstone.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", WirelessRedstone.class, 15.0)), (ScriptContext)scriptContext).asNum());
                if (scriptValue12 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object10);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v4.tm$108_emit_redstone(d));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Network");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                ArrayList arrayList = new ArrayList();
                v7 = scriptValue13 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Network") ? new PolyClassNetwork(object11).um$7_unregister(arrayList) : PolyDispatch.bootstrapCall("memberCall", "unregister", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
