/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassMachine_v2;
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$94_nearby_entities(d);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.elementsOf((ScriptValue)object);
        if (list != null) {
            for (ScriptValue scriptValue2 : list) {
                CallSite callSite;
                ScriptValue.Obj obj;
                Object object3;
                ScriptValue scriptValue3;
                PolyClassMachine_v2 polyClassMachine_v2;
                builder.val("entity", scriptValue2);
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(scriptContext);
                builder2.val("entity", scriptContext.getClassOrVar("entity"));
                if (!Utils.isRestingItem(builder2).asBool()) continue;
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v2.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("entity");
                Object object4 = scriptValue3 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
                    callSite = polyClassContainer.tm$12_push(scriptValue3);
                } else {
                    callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue5, (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
                }
                CallSite callSite2 = callSite;
                builder.val("leftover", (ScriptValue)callSite2);
                ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
                arrayList2.add(callSite2);
                if (!ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) continue;
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("entity");
                Object object5 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
