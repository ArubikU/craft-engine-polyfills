/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
        ScriptValue scriptValue;
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine_v2 != null ? polyClassMachine_v2.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 3.0;
        if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("input", (ScriptValue)callSite2);
        ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
        arrayList.add(callSite2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        PolyClassMachine_v2 polyClassMachine_v22 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine_v22 != null ? polyClassMachine_v22.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
                ScriptValue scriptValue8;
                ScriptValue.Obj obj3;
                Object object4;
                ScriptValue scriptValue9;
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("front");
                Object object5 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("front");
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("front");
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("front");
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)object5, (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:air")), (ScriptContext)scriptContext);
                PolyClassMachine_v2 polyClassMachine_v23 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue14 = polyClassMachine_v23 != null ? polyClassMachine_v23.pg$120_container() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d2 = 3.0;
                double d3 = 1.0;
                if (scriptValue14 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                    v2 = polyClassContainer.tm$6_remove_item(d2, d3);
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                }
                PolyClassMachine_v2 polyClassMachine_v24 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue15 = polyClassMachine_v24 != null ? polyClassMachine_v24.pg$120_container() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d4 = 5.0;
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("Item");
                if (scriptValue16 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:water_bucket"));
                    object3 = scriptValue16 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Item") ? new PolyClassItem(object6).um$21_create(arrayList2) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue16, arrayList2, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                CallSite callSite3 = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BucketRefiller.class, 1.0)), (ScriptContext)scriptContext);
                if (scriptValue15 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                    v4 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d4, (ScriptValue)callSite3));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)callSite3, (ScriptContext)scriptContext);
                }
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"minecraft:lava")) {
                ScriptValue.Obj obj5;
                Object object7;
                Object object8;
                ScriptValue scriptValue17;
                ScriptValue.Obj obj6;
                Object object9;
                ScriptValue scriptValue18;
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("front");
                Object object10 = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("front");
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("front");
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("front");
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)object10, (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue21, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue22, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:air")), (ScriptContext)scriptContext);
                PolyClassMachine_v2 polyClassMachine_v25 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue23 = polyClassMachine_v25 != null ? polyClassMachine_v25.pg$120_container() : ((scriptValue18 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d5 = 3.0;
                double d6 = 1.0;
                if (scriptValue23 instanceof ScriptValue.Obj && (object9 = (obj6 = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object9 instanceof PolyClass) && obj6.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object9);
                    v6 = polyClassContainer.tm$6_remove_item(d5, d6);
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
                }
                PolyClassMachine_v2 polyClassMachine_v26 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue24 = polyClassMachine_v26 != null ? polyClassMachine_v26.pg$120_container() : ((scriptValue17 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d7 = 5.0;
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("Item");
                if (scriptValue25 != ScriptValue.NULL) {
                    ScriptValue.Obj obj7;
                    Object object11;
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:lava_bucket"));
                    object8 = scriptValue25 instanceof ScriptValue.Obj && (object11 = (obj7 = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object11 instanceof PolyClass) && obj7.typeName().equals("Item") ? new PolyClassItem(object11).um$21_create(arrayList3) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue25, arrayList3, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                CallSite callSite4 = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BucketRefiller.class, 1.0)), (ScriptContext)scriptContext);
                if (scriptValue24 instanceof ScriptValue.Obj && (object7 = (obj5 = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object7 instanceof PolyClass) && obj5.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object7);
                    v8 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d7, (ScriptValue)callSite4));
                } else {
                    v8 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)callSite4, (ScriptContext)scriptContext);
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
