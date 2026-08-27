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
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemTrash.class, 27.0)), (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue2 : list) {
                CallSite callSite;
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue3;
                CallSite callSite2;
                ScriptValue.Obj obj2;
                Object object3;
                ScriptValue scriptValue4;
                builder.val("i", scriptValue2);
                PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue5 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$120_container() : ((scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL);
                ScriptValue scriptValue6 = scriptValue2;
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
                    callSite2 = polyClassContainer.tm$0_get_item(scriptValue6.asNum());
                } else {
                    callSite2 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                }
                if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue7 = polyClassMachine_v42 != null ? polyClassMachine_v42.pg$120_container() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL);
                ScriptValue scriptValue8 = scriptValue2;
                double d = 64.0;
                if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                    callSite = polyClassContainer.tm$6_remove_item(scriptValue8.asNum(), d);
                    continue;
                }
                callSite = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("entities", scriptValue9);
        List list2 = ScriptProgram.elementsOf((ScriptValue)scriptValue9);
        if (list2 != null) {
            for (ScriptValue scriptValue10 : list2) {
                builder.val("entity", scriptValue10);
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("entity");
                Object object5 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
