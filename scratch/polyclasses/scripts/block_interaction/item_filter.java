/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
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
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
        ScriptValue.Obj obj;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)0.0));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
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
            ScriptValue.Obj obj2;
            Object object3;
            double d = 1.2;
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                object = polyClassMachine_v3.tm$94_nearby_entities(d);
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
                Object object4;
                ScriptValue.Obj obj3;
                Object object5;
                builder.val("entity", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("entity");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "type", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:item")) continue;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("entity");
                CallSite callSite2 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("ey", (ScriptValue)callSite2);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj3 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v3(object5).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("by", scriptValue8);
                if (!(callSite2.asNum() >= scriptValue8.asNum() && callSite2.asNum() <= ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((double)1.5)).asNum())) continue;
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("entity");
                if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)scriptValue3)) {
                    Object object6;
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("entity");
                    if (scriptValue10 != ScriptValue.NULL) {
                        ScriptValue.Obj obj4;
                        Object object7;
                        ScriptValue.Obj obj5;
                        Object object8;
                        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                        arrayList5.add(ScriptValue.of((double)((scriptValue11 != ScriptValue.NULL ? (scriptValue11 instanceof ScriptValue.Obj && (object8 = (obj5 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object8 instanceof PolyClass) && obj5.typeName().equals("Machine") ? new PolyClassMachine_v3(object8).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * 0.3)));
                        arrayList5.add(ScriptValue.of((double)0.05));
                        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                        arrayList5.add(ScriptValue.of((double)((scriptValue12 != ScriptValue.NULL ? (scriptValue12 instanceof ScriptValue.Obj && (object7 = (obj4 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object7 instanceof PolyClass) && obj4.typeName().equals("Machine") ? new PolyClassMachine_v3(object7).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * 0.3)));
                        arrayList4.add(ScriptFormula.callBuiltin((String)"vec", arrayList5, (ScriptContext)scriptContext));
                        object6 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue10, arrayList4, (ScriptContext)scriptContext);
                        continue;
                    }
                    object6 = ScriptValue.NULL;
                    continue;
                }
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("entity");
                if (scriptValue13 != ScriptValue.NULL) {
                    ScriptValue.Obj obj6;
                    Object object9;
                    ScriptValue.Obj obj7;
                    Object object10;
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                    arrayList7.add(ScriptValue.of((double)((scriptValue14 != ScriptValue.NULL ? (scriptValue14 instanceof ScriptValue.Obj && (object10 = (obj7 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object10 instanceof PolyClass) && obj7.typeName().equals("Machine") ? new PolyClassMachine_v3(object10).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * -0.3)));
                    arrayList7.add(ScriptValue.of((double)0.05));
                    ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                    arrayList7.add(ScriptValue.of((double)((scriptValue15 != ScriptValue.NULL ? (scriptValue15 instanceof ScriptValue.Obj && (object9 = (obj6 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object9 instanceof PolyClass) && obj6.typeName().equals("Machine") ? new PolyClassMachine_v3(object9).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue15, (ScriptContext)scriptContext)) : ScriptValue.NULL).asNum() * -0.3)));
                    arrayList6.add(ScriptFormula.callBuiltin((String)"vec", arrayList7, (ScriptContext)scriptContext));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue13, arrayList6, (ScriptContext)scriptContext);
                    continue;
                }
                object4 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
