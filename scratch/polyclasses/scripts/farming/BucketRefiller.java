/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
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
        PolyClassMachine_v4 polyClassMachine_v4;
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v4 polyClassMachine_v42;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v42.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 3.0;
        if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)d));
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("input", (ScriptValue)callSite2);
        ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
        arrayList.add(callSite2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v4.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("front", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("front");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("front_id", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("input");
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:bucket")) {
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"minecraft:water")) {
                ScriptValue.Obj obj2;
                Object object2;
                Object object3;
                PolyClassMachine_v4 polyClassMachine_v43;
                ScriptValue.Obj obj3;
                Object object4;
                PolyClassMachine_v4 polyClassMachine_v44;
                ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("front");
                arrayList2.add(PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("front");
                arrayList2.add(PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("front");
                arrayList2.add(PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                arrayList2.add((CallSite)ScriptValue.of((String)"minecraft:air"));
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("front");
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL), arrayList2, (ScriptContext)scriptContext);
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue13 = scriptValue12 != ScriptValue.NULL ? ((polyClassMachine_v44 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue12)) != null ? polyClassMachine_v44.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d2 = 3.0;
                double d3 = 1.0;
                if (scriptValue13 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                    v1 = polyClassContainer.tm$6_remove_item(d2, d3);
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((double)d2));
                    arrayList3.add(ScriptValue.of((double)d3));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue13, arrayList3, (ScriptContext)scriptContext);
                }
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue15 = scriptValue14 != ScriptValue.NULL ? ((polyClassMachine_v43 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine_v43.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d4 = 5.0;
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((double)1.0));
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("Item");
                if (scriptValue16 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object5;
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(ScriptValue.of((String)"minecraft:water_bucket"));
                    object3 = scriptValue16 instanceof ScriptValue.Obj && (object5 = (obj4 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object5 instanceof PolyClass) && obj4.typeName().equals("Item") ? new PolyClassItem(object5).um$21_create(arrayList5) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue16, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                CallSite callSite3 = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object3, arrayList4, (ScriptContext)scriptContext);
                if (scriptValue15 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                    v3 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d4, (ScriptValue)callSite3));
                } else {
                    ArrayList<Object> arrayList6 = new ArrayList<Object>();
                    arrayList6.add(ScriptValue.of((double)d4));
                    arrayList6.add(callSite3);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue15, arrayList6, (ScriptContext)scriptContext);
                }
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"minecraft:lava")) {
                ScriptValue.Obj obj5;
                Object object6;
                Object object7;
                PolyClassMachine_v4 polyClassMachine_v45;
                ScriptValue.Obj obj6;
                Object object8;
                PolyClassMachine_v4 polyClassMachine_v46;
                ArrayList<CallSite> arrayList7 = new ArrayList<CallSite>();
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("front");
                arrayList7.add(PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("front");
                arrayList7.add(PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("front");
                arrayList7.add(PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext));
                arrayList7.add((CallSite)ScriptValue.of((String)"minecraft:air"));
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("front");
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)(scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL), arrayList7, (ScriptContext)scriptContext);
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue22 = scriptValue21 != ScriptValue.NULL ? ((polyClassMachine_v46 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue21)) != null ? polyClassMachine_v46.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d5 = 3.0;
                double d6 = 1.0;
                if (scriptValue22 instanceof ScriptValue.Obj && (object8 = (obj6 = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object8 instanceof PolyClass) && obj6.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object8);
                    v4 = polyClassContainer.tm$6_remove_item(d5, d6);
                } else {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(ScriptValue.of((double)d5));
                    arrayList8.add(ScriptValue.of((double)d6));
                    v4 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue22, arrayList8, (ScriptContext)scriptContext);
                }
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue24 = scriptValue23 != ScriptValue.NULL ? ((polyClassMachine_v45 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue23)) != null ? polyClassMachine_v45.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue23, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d7 = 5.0;
                ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                arrayList9.add(ScriptValue.of((double)1.0));
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("Item");
                if (scriptValue25 != ScriptValue.NULL) {
                    ScriptValue.Obj obj7;
                    Object object9;
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add(ScriptValue.of((String)"minecraft:lava_bucket"));
                    object7 = scriptValue25 instanceof ScriptValue.Obj && (object9 = (obj7 = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object9 instanceof PolyClass) && obj7.typeName().equals("Item") ? new PolyClassItem(object9).um$21_create(arrayList10) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue25, arrayList10, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                CallSite callSite4 = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object7, arrayList9, (ScriptContext)scriptContext);
                if (scriptValue24 instanceof ScriptValue.Obj && (object6 = (obj5 = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object6 instanceof PolyClass) && obj5.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object6);
                    v6 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d7, (ScriptValue)callSite4));
                } else {
                    ArrayList<Object> arrayList11 = new ArrayList<Object>();
                    arrayList11.add(ScriptValue.of((double)d7));
                    arrayList11.add(callSite4);
                    v6 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue24, arrayList11, (ScriptContext)scriptContext);
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
