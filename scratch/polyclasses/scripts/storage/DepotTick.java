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
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class DepotTick {
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
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("is_resting_item");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"utils.pf", null, arrayList);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            double d = 0.7;
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$94_nearby_entities(d);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((double)d));
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.elementsOf((ScriptValue)object);
        if (list != null) {
            for (ScriptValue scriptValue2 : list) {
                Object object3;
                CallSite callSite;
                ScriptValue.Obj obj;
                Object object4;
                ScriptValue scriptValue3;
                PolyClassMachine_v4 polyClassMachine_v4;
                builder.val("entity", scriptValue2);
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(scriptContext);
                builder2.val("entity", scriptContext.getClassOrVar("entity"));
                if (!Utils.isRestingItem(builder2).asBool()) continue;
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v4.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("entity");
                Object object5 = scriptValue3 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
                if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                    callSite = polyClassContainer.tm$12_push(scriptValue3);
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue3);
                    callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
                }
                CallSite callSite2 = callSite;
                builder.val("leftover", (ScriptValue)callSite2);
                ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
                arrayList4.add(callSite2);
                if (!ScriptFormula.callBuiltin((String)"is_empty", arrayList4, (ScriptContext)scriptContext).asBool()) continue;
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("entity");
                if (scriptValue7 != ScriptValue.NULL) {
                    ArrayList arrayList5 = new ArrayList();
                    object3 = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue7, arrayList5, (ScriptContext)scriptContext);
                    continue;
                }
                object3 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
