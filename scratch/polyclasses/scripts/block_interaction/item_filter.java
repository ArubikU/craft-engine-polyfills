/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
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
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

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
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)0.0));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("filter", (ScriptValue)callSite);
        ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
        arrayList2.add(callSite);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("filter");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("filter_id", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            double d = 1.2;
            if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object2);
                object = polyClassMachine2.tm$94_nearby_entities(d);
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((double)d));
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue4, arrayList3, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        List list = ScriptProgram.rowsOf((ScriptValue)object, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object3;
                PolyClassMachine polyClassMachine3;
                builder.val("entity", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("entity");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("entity");
                CallSite callSite2 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("ey", (ScriptValue)callSite2);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? ((polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassMachine3.pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("by", scriptValue8);
                if (!(callSite2.asNum() >= scriptValue8.asNum() && callSite2.asNum() <= ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((double)1.5)).asNum())) continue;
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("entity");
                if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)scriptValue3)) {
                    Object object4;
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("entity");
                    if (scriptValue10 != ScriptValue.NULL) {
                        PolyClassMachine polyClassMachine4;
                        PolyClassMachine polyClassMachine5;
                        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                        arrayList5.add(ScriptValue.of((double)((scriptValue11 != ScriptValue.NULL ? ((polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassMachine5.pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * 0.3)));
                        arrayList5.add(ScriptValue.of((double)0.05));
                        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                        arrayList5.add(ScriptValue.of((double)((scriptValue12 != ScriptValue.NULL ? ((polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue12)) != null ? polyClassMachine4.pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * 0.3)));
                        arrayList4.add(ScriptFormula.callBuiltin((String)"vec", arrayList5, (ScriptContext)scriptContext));
                        object4 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue10, arrayList4, (ScriptContext)scriptContext);
                        continue;
                    }
                    object4 = ScriptValue.NULL;
                    continue;
                }
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("entity");
                if (scriptValue13 != ScriptValue.NULL) {
                    PolyClassMachine polyClassMachine6;
                    PolyClassMachine polyClassMachine7;
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                    arrayList7.add(ScriptValue.of((double)((scriptValue14 != ScriptValue.NULL ? ((polyClassMachine7 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine7.pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * -0.3)));
                    arrayList7.add(ScriptValue.of((double)0.05));
                    ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                    arrayList7.add(ScriptValue.of((double)((scriptValue15 != ScriptValue.NULL ? ((polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue15)) != null ? polyClassMachine6.pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue15, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * -0.3)));
                    arrayList6.add(ScriptFormula.callBuiltin((String)"vec", arrayList7, (ScriptContext)scriptContext));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue13, arrayList6, (ScriptContext)scriptContext);
                    continue;
                }
                object3 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
