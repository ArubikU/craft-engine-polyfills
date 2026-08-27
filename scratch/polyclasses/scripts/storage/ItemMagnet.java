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
        PolyClassMachine_v2 polyClassMachine_v2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v2.pg$185_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("machine", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            double d = 8.0;
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v22.tm$94_nearby_entities(d);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("entities", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue4);
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                ScriptValue scriptValue6;
                builder.val("entity", scriptValue5);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("entity");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("entity");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "distance_sq", (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
                builder.val("dsq", (ScriptValue)callSite);
                if (callSite.asNum() <= 9.0) {
                    CallSite callSite2;
                    ScriptValue.Obj obj;
                    Object object3;
                    PolyClassMachine_v2 polyClassMachine_v23;
                    ScriptValue scriptValue9 = scriptContext.getClassOrVar("entity");
                    ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("item", scriptValue10);
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("entity");
                    Object object4 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue13 = scriptValue12 != ScriptValue.NULL ? ((polyClassMachine_v23 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue12)) != null ? polyClassMachine_v23.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    ScriptValue scriptValue14 = scriptValue10;
                    if (scriptValue13 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
                        callSite2 = polyClassContainer.tm$12_push(scriptValue14);
                        continue;
                    }
                    callSite2 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
                    continue;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("machine");
                CallSite callSite3 = PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "normalize", (ScriptValue)(scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "sub", (ScriptValue)scriptValue15, (ScriptValue)((scriptValue6 = scriptContext.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemMagnet.class, 0.2)), (ScriptContext)scriptContext);
                builder.val("direction", (ScriptValue)callSite3);
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("entity");
                Object object5 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue16, (ScriptValue)callSite3, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
