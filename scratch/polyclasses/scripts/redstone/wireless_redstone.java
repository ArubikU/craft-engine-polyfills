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
import java.util.ArrayList;

public final class WirelessRedstone {
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("ch", scriptValue4);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)0.0))) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Network");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object6;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                v2 = scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj2 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj2.typeName().equals("Network") ? new PolyClassNetwork(object6).um$13_register(arrayList) : PolyDispatch.bootstrapCall("memberCall", "register", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? (scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object5).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            builder.val("power", scriptValue7);
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Network");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj3;
                Object object7;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                arrayList.add(scriptValue7);
                v3 = scriptValue8 instanceof ScriptValue.Obj && (object7 = (obj3 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object7 instanceof PolyClass) && obj3.typeName().equals("Network") ? new PolyClassNetwork(object7).um$1_broadcast(arrayList) : PolyDispatch.bootstrapCall("memberCall", "broadcast", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj4;
                Object object8;
                double d = 0.0;
                if (scriptValue9 instanceof ScriptValue.Obj && (object8 = (obj4 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object8 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object8);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v4.tm$108_emit_redstone(d));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((double)d));
                    v4 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
        } else {
            Object object9;
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Network");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                object9 = scriptValue10 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Network") ? new PolyClassNetwork(object10).um$9_listen(arrayList) : PolyDispatch.bootstrapCall("memberCall", "listen", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
            } else {
                object9 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = object9;
            builder.val("power", scriptValue11);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue11);
                arrayList.add(ScriptValue.of((double)0.0));
                arrayList.add(ScriptValue.of((double)15.0));
                double d = Math.floor(ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext).asNum());
                if (scriptValue12 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object11);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v4.tm$108_emit_redstone(d));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(ScriptValue.of((double)d));
                    v6 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue12, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Network");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object12;
                ArrayList arrayList = new ArrayList();
                v7 = scriptValue13 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Network") ? new PolyClassNetwork(object12).um$7_unregister(arrayList) : PolyDispatch.bootstrapCall("memberCall", "unregister", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
        }
    }
}
