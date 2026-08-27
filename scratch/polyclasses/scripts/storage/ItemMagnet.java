/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
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
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$184_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("machine", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            double d = 8.0;
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v42.tm$94_nearby_entities(d);
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
                    CallSite callSite2;
                    ScriptValue.Obj obj;
                    Object object5;
                    PolyClassMachine_v4 polyClassMachine_v43;
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
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? ((polyClassMachine_v43 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassMachine_v43.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    ScriptValue scriptValue13 = scriptValue9;
                    if (scriptValue12 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object5);
                        callSite2 = polyClassContainer.tm$12_push(scriptValue13);
                        continue;
                    }
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue13);
                    callSite2 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue12, arrayList3, (ScriptContext)scriptContext);
                    continue;
                }
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((double)0.2));
                ArrayList arrayList5 = new ArrayList();
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("machine");
                if (scriptValue14 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue15 = scriptContext.getClassOrVar("entity");
                    arrayList6.add((ScriptValue)(scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "sub", (ScriptValue)scriptValue14, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                CallSite callSite3 = PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "normalize", (ScriptValue)object4, arrayList5, (ScriptContext)scriptContext), arrayList4, (ScriptContext)scriptContext);
                builder.val("direction", (ScriptValue)callSite3);
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("entity");
                if (scriptValue16 != ScriptValue.NULL) {
                    ArrayList<CallSite> arrayList7 = new ArrayList<CallSite>();
                    arrayList7.add(callSite3);
                    object3 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue16, arrayList7, (ScriptContext)scriptContext);
                    continue;
                }
                object3 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
