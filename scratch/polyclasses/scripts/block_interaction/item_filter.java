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
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class ItemFilter {
    public static void run(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)0.0));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
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
        List list = ScriptProgram.resolveForRows((String)"Machine.nearby_entities(1.2)", (ScriptContext)scriptContext, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object2;
                ScriptValue.Obj obj2;
                Object object3;
                builder.val("entity", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("entity");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("entity");
                CallSite callSite2 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("ey", (ScriptValue)callSite2);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v2(object3).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("by", scriptValue7);
                if (!(callSite2.asNum() >= scriptValue7.asNum() && callSite2.asNum() <= ScriptFormula.addPolymorphic((ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)1.5)).asNum())) continue;
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("entity");
                if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)scriptValue3)) {
                    Object object4;
                    ScriptValue scriptValue9 = scriptContext.getClassOrVar("entity");
                    if (scriptValue9 != ScriptValue.NULL) {
                        ScriptValue.Obj obj3;
                        Object object5;
                        ScriptValue.Obj obj4;
                        Object object6;
                        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
                        arrayList4.add(ScriptValue.of((double)((scriptValue10 != ScriptValue.NULL ? (scriptValue10 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine") ? new PolyClassMachine_v2(object6).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue10, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * 0.3)));
                        arrayList4.add(ScriptValue.of((double)0.05));
                        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                        arrayList4.add(ScriptValue.of((double)((scriptValue11 != ScriptValue.NULL ? (scriptValue11 instanceof ScriptValue.Obj && (object5 = (obj3 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object5 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v2(object5).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * 0.3)));
                        arrayList3.add(ScriptFormula.callBuiltin((String)"vec", arrayList4, (ScriptContext)scriptContext));
                        object4 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue9, arrayList3, (ScriptContext)scriptContext);
                        continue;
                    }
                    object4 = ScriptValue.NULL;
                    continue;
                }
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("entity");
                if (scriptValue12 != ScriptValue.NULL) {
                    ScriptValue.Obj obj5;
                    Object object7;
                    ScriptValue.Obj obj6;
                    Object object8;
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                    arrayList6.add(ScriptValue.of((double)((scriptValue13 != ScriptValue.NULL ? (scriptValue13 instanceof ScriptValue.Obj && (object8 = (obj6 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object8 instanceof PolyClass) && obj6.typeName().equals("Machine") ? new PolyClassMachine_v2(object8).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue13, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * -0.3)));
                    arrayList6.add(ScriptValue.of((double)0.05));
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                    arrayList6.add(ScriptValue.of((double)((scriptValue14 != ScriptValue.NULL ? (scriptValue14 instanceof ScriptValue.Obj && (object7 = (obj5 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object7 instanceof PolyClass) && obj5.typeName().equals("Machine") ? new PolyClassMachine_v2(object7).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * -0.3)));
                    arrayList5.add(ScriptFormula.callBuiltin((String)"vec", arrayList6, (ScriptContext)scriptContext));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue12, arrayList5, (ScriptContext)scriptContext);
                    continue;
                }
                object2 = ScriptValue.NULL;
            }
        }
    }
}
