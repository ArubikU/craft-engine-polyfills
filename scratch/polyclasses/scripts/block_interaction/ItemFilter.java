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
package dev.arubik.craftengine.script.gen.block_interaction;

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
import java.util.ArrayList;
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
        PolyClassMachine_v2 polyClassMachine_v2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v2.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 0.0;
        if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("filter", (ScriptValue)callSite2);
        ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
        arrayList.add(callSite2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("filter");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("filter_id", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object3;
            double d2 = 1.2;
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object3);
                object = polyClassMachine_v22.tm$94_nearby_entities(d2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)d2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.elementsOf((ScriptValue)object);
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                Object object4;
                PolyClassMachine_v2 polyClassMachine_v23;
                builder.val("entity", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("entity");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("entity");
                CallSite callSite3 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("ey", (ScriptValue)callSite3);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? ((polyClassMachine_v23 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassMachine_v23.pg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("by", scriptValue10);
                if (!(callSite3.asNum() >= scriptValue10.asNum() && callSite3.asNum() <= ScriptFormula.addPolymorphic((ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemFilter.class, 1.5))).asNum())) continue;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("entity");
                if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)scriptValue4)) {
                    Object object5;
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("entity");
                    if (scriptValue12 != ScriptValue.NULL) {
                        PolyClassMachine_v2 polyClassMachine_v24;
                        PolyClassMachine_v2 polyClassMachine_v25;
                        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                        arrayList2.add(ScriptValue.of((double)((scriptValue13 != ScriptValue.NULL ? ((polyClassMachine_v25 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue13)) != null ? polyClassMachine_v25.tg$134_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue13, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * 0.3)));
                        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemFilter.class, 0.05));
                        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                        arrayList2.add(ScriptValue.of((double)((scriptValue14 != ScriptValue.NULL ? ((polyClassMachine_v24 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine_v24.tg$132_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * 0.3)));
                        object5 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue12, (ScriptValue)ScriptFormula.callBuiltin((String)"vec", arrayList2, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                        continue;
                    }
                    object5 = ScriptValue.NULL;
                    continue;
                }
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("entity");
                if (scriptValue15 != ScriptValue.NULL) {
                    PolyClassMachine_v2 polyClassMachine_v26;
                    PolyClassMachine_v2 polyClassMachine_v27;
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
                    arrayList3.add(ScriptValue.of((double)((scriptValue16 != ScriptValue.NULL ? ((polyClassMachine_v27 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue16)) != null ? polyClassMachine_v27.tg$134_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue16, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * -0.3)));
                    arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ItemFilter.class, 0.05));
                    ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
                    arrayList3.add(ScriptValue.of((double)((scriptValue17 != ScriptValue.NULL ? ((polyClassMachine_v26 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue17)) != null ? polyClassMachine_v26.tg$132_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue17, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * -0.3)));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue15, (ScriptValue)ScriptFormula.callBuiltin((String)"vec", arrayList3, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                    continue;
                }
                object4 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
