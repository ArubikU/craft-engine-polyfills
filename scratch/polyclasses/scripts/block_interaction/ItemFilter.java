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
package dev.arubik.craftengine.script.gen.block_interaction;

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
public final class ItemFilter {
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
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object2;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 0.0;
        if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("filter", (ScriptValue)callSite2);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool()) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue3 = callSite2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)callSite2, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("filter_id", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object3;
            double d2 = 1.2;
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object3);
                object = polyClassMachine2.tm$94_nearby_entities(d2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.elementsOf((ScriptValue)object);
        Object object4 = scriptContext.getClassOrVar("ey");
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("by");
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                Object object5;
                ScriptValue scriptValue7;
                builder.val("entity", scriptValue6);
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                CallSite callSite3 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("ey", (ScriptValue)callSite3);
                object4 = callSite3;
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue8 = polyClassMachine3 != null ? polyClassMachine3.pg$202_y() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("by", scriptValue8);
                scriptValue5 = scriptValue8;
                if (!(object4.asNum() >= scriptValue5.asNum() && object4.asNum() <= ScriptFormula.addPolymorphic((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemFilter.class, 1.5))).asNum())) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)scriptValue3)) {
                    Object object6;
                    ScriptValue scriptValue9 = scriptContext.getClassOrVar("entity");
                    if (scriptValue9 != ScriptValue.NULL) {
                        ScriptValue scriptValue10;
                        ScriptValue scriptValue11;
                        PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                        PolyClassMachine polyClassMachine5 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                        object6 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue9, (ScriptValue)ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)ScriptValue.of((double)((polyClassMachine5 != null ? polyClassMachine5.tg$134_facing_dx() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * 0.3)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemFilter.class, 0.05)), (ScriptValue)ScriptValue.of((double)((polyClassMachine4 != null ? polyClassMachine4.tg$132_facing_dz() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * 0.3)), (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                        continue;
                    }
                    object6 = ScriptValue.NULL;
                    continue;
                }
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("entity");
                if (scriptValue12 != ScriptValue.NULL) {
                    ScriptValue scriptValue13;
                    ScriptValue scriptValue14;
                    PolyClassMachine polyClassMachine6 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    PolyClassMachine polyClassMachine7 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    object5 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue12, (ScriptValue)ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)ScriptValue.of((double)((polyClassMachine7 != null ? polyClassMachine7.tg$134_facing_dx() : ((scriptValue14 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * -0.3)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemFilter.class, 0.05)), (ScriptValue)ScriptValue.of((double)((polyClassMachine6 != null ? polyClassMachine6.tg$132_facing_dz() : ((scriptValue13 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue13, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * -0.3)), (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                    continue;
                }
                object5 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
