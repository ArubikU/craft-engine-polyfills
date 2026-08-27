/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class BucketRefiller {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)3.0));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("input", (ScriptValue)callSite);
        ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
        arrayList2.add(callSite);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object).pg$128_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("front", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("front");
        ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("front_id", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("input");
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:bucket")) {
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"minecraft:water")) {
                ScriptValue.Obj obj3;
                Object object3;
                Object object4;
                ScriptValue.Obj obj4;
                Object object5;
                ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("front");
                arrayList3.add(PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("front");
                arrayList3.add(PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("front");
                arrayList3.add(PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                arrayList3.add((CallSite)ScriptValue.of((String)"minecraft:air"));
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("front");
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), arrayList3, (ScriptContext)scriptContext);
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((double)3.0));
                arrayList4.add(ScriptValue.of((double)1.0));
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(scriptValue11 != ScriptValue.NULL ? (scriptValue11 instanceof ScriptValue.Obj && (object5 = (obj4 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object5 instanceof PolyClass) && obj4.typeName().equals("Machine") ? new PolyClassMachine_v3(object5).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList4, (ScriptContext)scriptContext);
                ArrayList<Object> arrayList5 = new ArrayList<Object>();
                arrayList5.add(ScriptValue.of((double)5.0));
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(ScriptValue.of((double)1.0));
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Item");
                if (scriptValue12 != ScriptValue.NULL) {
                    ScriptValue.Obj obj5;
                    Object object6;
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add(ScriptValue.of((String)"minecraft:water_bucket"));
                    object4 = scriptValue12 instanceof ScriptValue.Obj && (object6 = (obj5 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object6 instanceof PolyClass) && obj5.typeName().equals("Item") ? new PolyClassItem(object6).um$21_create(arrayList7) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue12, arrayList7, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                arrayList5.add(PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object4, arrayList6, (ScriptContext)scriptContext));
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue13 != ScriptValue.NULL ? (scriptValue13 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v3(object3).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue13, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList5, (ScriptContext)scriptContext);
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"minecraft:lava")) {
                ScriptValue.Obj obj6;
                Object object7;
                Object object8;
                ScriptValue.Obj obj7;
                Object object9;
                ArrayList<CallSite> arrayList8 = new ArrayList<CallSite>();
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("front");
                arrayList8.add(PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("front");
                arrayList8.add(PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("front");
                arrayList8.add(PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                arrayList8.add((CallSite)ScriptValue.of((String)"minecraft:air"));
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("front");
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL), arrayList8, (ScriptContext)scriptContext);
                ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                arrayList9.add(ScriptValue.of((double)3.0));
                arrayList9.add(ScriptValue.of((double)1.0));
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(scriptValue18 != ScriptValue.NULL ? (scriptValue18 instanceof ScriptValue.Obj && (object9 = (obj7 = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object9 instanceof PolyClass) && obj7.typeName().equals("Machine") ? new PolyClassMachine_v3(object9).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList9, (ScriptContext)scriptContext);
                ArrayList<Object> arrayList10 = new ArrayList<Object>();
                arrayList10.add(ScriptValue.of((double)5.0));
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                arrayList11.add(ScriptValue.of((double)1.0));
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("Item");
                if (scriptValue19 != ScriptValue.NULL) {
                    ScriptValue.Obj obj8;
                    Object object10;
                    ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                    arrayList12.add(ScriptValue.of((String)"minecraft:lava_bucket"));
                    object8 = scriptValue19 instanceof ScriptValue.Obj && (object10 = (obj8 = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object10 instanceof PolyClass) && obj8.typeName().equals("Item") ? new PolyClassItem(object10).um$21_create(arrayList12) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue19, arrayList12, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                arrayList10.add(PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object8, arrayList11, (ScriptContext)scriptContext));
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue20 != ScriptValue.NULL ? (scriptValue20 instanceof ScriptValue.Obj && (object7 = (obj6 = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object7 instanceof PolyClass) && obj6.typeName().equals("Machine") ? new PolyClassMachine_v3(object7).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue20, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList10, (ScriptContext)scriptContext);
            }
        }
        FILE_SCOPE = builder.build();
    }
}
