/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
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
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine;
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
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$185_pos() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("entities", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("item");
        Object object3 = scriptContext.getClassOrVar("dsq");
        Object object4 = scriptContext.getClassOrVar("direction");
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("entity", scriptValue6);
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "distance_sq", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
                builder.val("dsq", (ScriptValue)callSite);
                object3 = callSite;
                if (object3.asNum() <= 9.0) {
                    CallSite callSite2;
                    ScriptValue.Obj obj;
                    Object object5;
                    ScriptValue scriptValue7;
                    ScriptValue scriptValue8 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("item", scriptValue8);
                    scriptValue5 = scriptValue8;
                    ScriptValue scriptValue9 = scriptContext.getClassOrVar("entity");
                    Object object6 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    ScriptValue scriptValue10 = polyClassMachine3 != null ? polyClassMachine3.pg$120_container() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL);
                    ScriptValue scriptValue11 = scriptValue5;
                    if (scriptValue10 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object5);
                        callSite2 = polyClassContainer.tm$12_push(scriptValue11);
                        continue;
                    }
                    callSite2 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                    continue;
                }
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("machine");
                CallSite callSite3 = PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "normalize", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "sub", (ScriptValue)scriptValue12, (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemMagnet.class, 0.2)), (ScriptContext)scriptContext);
                builder.val("direction", (ScriptValue)callSite3);
                object4 = callSite3;
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("entity");
                Object object7 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue13, (ScriptValue)object4, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
