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
        PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 3.0;
        if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("input", (ScriptValue)callSite2);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool()) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine_v42 != null ? polyClassMachine_v42.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("front", scriptValue4);
        ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("front_id", scriptValue5);
        if (ScriptFormula.valuesEqualStr((ScriptValue)(callSite2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)callSite2, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:bucket")) {
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"minecraft:water")) {
                ScriptValue.Obj obj2;
                Object object2;
                Object object3;
                ScriptValue scriptValue6;
                ScriptValue.Obj obj3;
                Object object4;
                ScriptValue scriptValue7;
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:air")), (ScriptContext)scriptContext);
                PolyClassMachine_v4 polyClassMachine_v43 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue8 = polyClassMachine_v43 != null ? polyClassMachine_v43.pg$120_container() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d2 = 3.0;
                double d3 = 1.0;
                if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                    v1 = polyClassContainer.tm$6_remove_item(d2, d3);
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                }
                PolyClassMachine_v4 polyClassMachine_v44 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue9 = polyClassMachine_v44 != null ? polyClassMachine_v44.pg$120_container() : ((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d4 = 5.0;
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("Item");
                if (scriptValue10 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object5;
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:water_bucket"));
                    object3 = scriptValue10 instanceof ScriptValue.Obj && (object5 = (obj4 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object5 instanceof PolyClass) && obj4.typeName().equals("Item") ? new PolyClassItem(object5).um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                CallSite callSite3 = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BucketRefiller.class, 1.0)), (ScriptContext)scriptContext);
                if (scriptValue9 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                    v3 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d4, (ScriptValue)callSite3));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)callSite3, (ScriptContext)scriptContext);
                }
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"minecraft:lava")) {
                ScriptValue.Obj obj5;
                Object object6;
                Object object7;
                ScriptValue scriptValue11;
                ScriptValue.Obj obj6;
                Object object8;
                ScriptValue scriptValue12;
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:air")), (ScriptContext)scriptContext);
                PolyClassMachine_v4 polyClassMachine_v45 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue13 = polyClassMachine_v45 != null ? polyClassMachine_v45.pg$120_container() : ((scriptValue12 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d5 = 3.0;
                double d6 = 1.0;
                if (scriptValue13 instanceof ScriptValue.Obj && (object8 = (obj6 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object8 instanceof PolyClass) && obj6.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object8);
                    v4 = polyClassContainer.tm$6_remove_item(d5, d6);
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
                }
                PolyClassMachine_v4 polyClassMachine_v46 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue14 = polyClassMachine_v46 != null ? polyClassMachine_v46.pg$120_container() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d7 = 5.0;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Item");
                if (scriptValue15 != ScriptValue.NULL) {
                    ScriptValue.Obj obj7;
                    Object object9;
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:lava_bucket"));
                    object7 = scriptValue15 instanceof ScriptValue.Obj && (object9 = (obj7 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object9 instanceof PolyClass) && obj7.typeName().equals("Item") ? new PolyClassItem(object9).um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                CallSite callSite4 = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BucketRefiller.class, 1.0)), (ScriptContext)scriptContext);
                if (scriptValue14 instanceof ScriptValue.Obj && (object6 = (obj5 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object6 instanceof PolyClass) && obj5.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object6);
                    v6 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d7, (ScriptValue)callSite4));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)callSite4, (ScriptContext)scriptContext);
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
