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
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class ItemTrash {
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
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemTrash.class, 27.0));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue2 : list) {
                CallSite callSite;
                ScriptValue.Obj obj;
                Object object2;
                PolyClassMachine_v4 polyClassMachine_v4;
                CallSite callSite2;
                ScriptValue.Obj obj2;
                Object object3;
                PolyClassMachine_v4 polyClassMachine_v42;
                builder.val("i", scriptValue2);
                ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v42.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("i");
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
                    callSite2 = polyClassContainer.tm$0_get_item(scriptValue5.asNum());
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue5);
                    callSite2 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue4, arrayList3, (ScriptContext)scriptContext);
                }
                arrayList2.add(callSite2);
                if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine_v4.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("i");
                double d = 64.0;
                if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                    callSite = polyClassContainer.tm$6_remove_item(scriptValue8.asNum(), d);
                    continue;
                }
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptValue8);
                arrayList4.add(ScriptValue.of((double)d));
                callSite = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue7, arrayList4, (ScriptContext)scriptContext);
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            double d = 1.0;
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                object = polyClassMachine_v4.tm$94_nearby_entities(d);
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptValue.of((double)d));
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("entities", scriptValue9);
        List list2 = ScriptProgram.elementsOf((ScriptValue)scriptValue9);
        if (list2 != null) {
            for (ScriptValue scriptValue10 : list2) {
                Object object5;
                builder.val("entity", scriptValue10);
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("entity");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("entity");
                if (scriptValue12 != ScriptValue.NULL) {
                    ArrayList arrayList6 = new ArrayList();
                    object5 = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue12, arrayList6, (ScriptContext)scriptContext);
                    continue;
                }
                object5 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
