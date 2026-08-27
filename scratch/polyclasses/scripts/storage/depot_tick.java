/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
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
    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("is_resting_item");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"utils.pf", null, arrayList);
        List list = ScriptProgram.resolveForRows((String)"Machine.nearby_entities(0.7)", (ScriptContext)scriptContext, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object;
                ScriptValue.Obj obj;
                Object object2;
                builder.val("entity", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("entity", scriptContext.getClassOrVar("entity"));
                if (!Utils.isRestingItem(builder2).asBool()) continue;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue = scriptContext.getClassOrVar("entity");
                arrayList2.add((ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object2).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList2, (ScriptContext)scriptContext);
                builder.val("leftover", (ScriptValue)callSite);
                ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
                arrayList3.add(callSite);
                if (!ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool()) continue;
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("entity");
                if (scriptValue3 != ScriptValue.NULL) {
                    ArrayList arrayList4 = new ArrayList();
                    object = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue3, arrayList4, (ScriptContext)scriptContext);
                    continue;
                }
                object = ScriptValue.NULL;
            }
        }
    }
}
