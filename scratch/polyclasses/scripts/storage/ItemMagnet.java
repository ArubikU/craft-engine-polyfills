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
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class ItemMagnet {
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
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine.pg$184_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("machine", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            double d = 8.0;
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object2);
                object = polyClassMachine2.tm$94_nearby_entities(d);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("entities", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue4);
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                Object object3;
                Object object4;
                builder.val("entity", scriptValue5);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("entity");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("entity");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "distance_sq", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
                builder.val("dsq", (ScriptValue)callSite);
                if (callSite.asNum() <= 9.0) {
                    PolyClassMachine polyClassMachine3;
                    ScriptValue scriptValue8 = scriptContext.getClassOrVar("entity");
                    ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("item", scriptValue9);
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("entity");
                    if (scriptValue10 != ScriptValue.NULL) {
                        ArrayList arrayList2 = new ArrayList();
                        v1 = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue10, arrayList2, (ScriptContext)scriptContext);
                    } else {
                        v1 = ScriptValue.NULL;
                    }
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue9);
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                    PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)(scriptValue11 != ScriptValue.NULL ? ((polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassMachine3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList3, (ScriptContext)scriptContext);
                    continue;
                }
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((double)0.2));
                ArrayList arrayList5 = new ArrayList();
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("machine");
                if (scriptValue12 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue13 = scriptContext.getClassOrVar("entity");
                    arrayList6.add((ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "sub", (ScriptValue)scriptValue12, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "normalize", (ScriptValue)object4, arrayList5, (ScriptContext)scriptContext), arrayList4, (ScriptContext)scriptContext);
                builder.val("direction", (ScriptValue)callSite2);
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("entity");
                if (scriptValue14 != ScriptValue.NULL) {
                    ArrayList<CallSite> arrayList7 = new ArrayList<CallSite>();
                    arrayList7.add(callSite2);
                    object3 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue14, arrayList7, (ScriptContext)scriptContext);
                    continue;
                }
                object3 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
