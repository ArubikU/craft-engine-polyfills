/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

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
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 0.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue2);
        ScriptValue scriptValue3 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        builder.val("filter", scriptValue3);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool()) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("filter_id", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            double d2 = 1.2;
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5);
            object = polyClassMachine2 != null ? polyClassMachine2.tm$94_nearby_entities(d2) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.elementsOf((ScriptValue)object);
        Object object2 = scriptContext.getClassOrVar("ey");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("by");
        if (list != null) {
            for (ScriptValue scriptValue7 : list) {
                Object object3;
                ScriptValue scriptValue8;
                builder.val("entity", scriptValue7);
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                CallSite callSite = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("ey", (ScriptValue)callSite);
                object2 = callSite;
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue9 = polyClassMachine3 != null ? polyClassMachine3.pg$202_y() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("by", scriptValue9);
                scriptValue6 = scriptValue9;
                if (!(object2.asNum() >= scriptValue6.asNum() && object2.asNum() <= ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemFilter.class, 1.5))).asNum())) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)scriptValue4)) {
                    Object object4;
                    if (scriptValue7 != ScriptValue.NULL) {
                        ScriptValue scriptValue10;
                        ScriptValue scriptValue11;
                        PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                        PolyClassMachine polyClassMachine5 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                        object4 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue7, (ScriptValue)ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)ScriptValue.of((double)((polyClassMachine5 != null ? polyClassMachine5.tg$134_facing_dx() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * 0.3)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemFilter.class, 0.05)), (ScriptValue)ScriptValue.of((double)((polyClassMachine4 != null ? polyClassMachine4.tg$132_facing_dz() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * 0.3)), (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                        continue;
                    }
                    object4 = ScriptValue.NULL;
                    continue;
                }
                if (scriptValue7 != ScriptValue.NULL) {
                    ScriptValue scriptValue12;
                    ScriptValue scriptValue13;
                    PolyClassMachine polyClassMachine6 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    PolyClassMachine polyClassMachine7 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    object3 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue7, (ScriptValue)ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)ScriptValue.of((double)((polyClassMachine7 != null ? polyClassMachine7.tg$134_facing_dx() : ((scriptValue13 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue13, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * -0.3)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemFilter.class, 0.05)), (ScriptValue)ScriptValue.of((double)((polyClassMachine6 != null ? polyClassMachine6.tg$132_facing_dz() : ((scriptValue12 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue12, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * -0.3)), (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                    continue;
                }
                object3 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
