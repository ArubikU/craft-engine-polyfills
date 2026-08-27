/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class Utils {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _deposit(ScriptContext.Builder builder) {
        block6: {
            ScriptContext scriptContext;
            block5: {
                PolyClassMachine polyClassMachine;
                scriptContext = builder.peek();
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("item"));
                if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine.pg$160_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("contraption", scriptValue2);
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) break block5;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptContext.getClassOrVar("item"));
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("contraption");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL), arrayList2, (ScriptContext)scriptContext);
                builder.val("leftover", (ScriptValue)callSite);
                ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
                arrayList3.add(callSite);
                if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool() ^ true)) break block6;
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
                if (scriptValue4 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object;
                    ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
                    arrayList4.add(callSite);
                    v0 = scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine(object).um$4_drop_item(arrayList4) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue4, arrayList4, (ScriptContext)scriptContext);
                } else {
                    v0 = ScriptValue.NULL;
                }
                break block6;
            }
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("item"));
                v1 = scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine(object).um$4_drop_item(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _linearBreakSpeed(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 20.0;
        ScriptValue scriptValue = ScriptValue.of((double)20.0);
        builder.val("LINEAR_SPEED_SCALE", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("contraption");
        return ScriptValue.of((double)Math.max(1.0, Math.abs((scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum()) * d));
    }

    public static ScriptValue _updateActivated(ScriptContext.Builder builder) {
        block12: {
            Object object;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue2 = scriptContext.getClassOrVar("act_key");
                String string = "int";
                if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                    object = polyClassMachine.tm$34_get_typed(scriptValue2.asStr(), string);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue2);
                    arrayList.add(ScriptValue.of((String)string));
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = object;
            builder.val("was", scriptValue3);
            if (!(ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("is_now"), (ScriptValue)scriptValue3) ^ true)) break block12;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue5;
                String string = "activated";
                ScriptValue scriptValue6 = scriptValue5 = scriptContext.getNum("is_now") > 0.0 ? ScriptValue.of((String)"true") : ScriptValue.of((String)"false");
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                    v2 = ScriptValue.of((boolean)polyClassMachine.tm$16_set_property(string, scriptValue5.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue5);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("act_key");
                String string = "int";
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("is_now");
                if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                    v3 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(scriptValue8.asStr(), string, scriptValue9));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue8);
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue9);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }
}
