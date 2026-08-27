/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
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
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
        PolyClassMachine_v3 polyClassMachine_v3;
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v3 polyClassMachine_v32;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v32.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 3.0;
        if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
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
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v3.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
                PolyClassMachine_v3 polyClassMachine_v33;
                ScriptValue.Obj obj3;
                Object object4;
                PolyClassMachine_v3 polyClassMachine_v34;
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("front");
                Object object5 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("front");
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("front");
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("front");
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)object5, (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:air")), (ScriptContext)scriptContext);
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue13 = scriptValue12 != ScriptValue.NULL ? ((polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue12)) != null ? polyClassMachine_v34.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d2 = 3.0;
                double d3 = 1.0;
                if (scriptValue13 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                    v2 = polyClassContainer.tm$6_remove_item(d2, d3);
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                }
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue15 = scriptValue14 != ScriptValue.NULL ? ((polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine_v33.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
                PolyClassMachine_v3 polyClassMachine_v35;
                ScriptValue.Obj obj6;
                Object object9;
                PolyClassMachine_v3 polyClassMachine_v36;
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("front");
                Object object10 = scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("front");
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("front");
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("front");
                PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)object10, (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)(scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)(scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:air")), (ScriptContext)scriptContext);
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue22 = scriptValue21 != ScriptValue.NULL ? ((polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue21)) != null ? polyClassMachine_v36.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d5 = 3.0;
                double d6 = 1.0;
                if (scriptValue22 instanceof ScriptValue.Obj && (object9 = (obj6 = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object9 instanceof PolyClass) && obj6.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object9);
                    v6 = polyClassContainer.tm$6_remove_item(d5, d6);
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
                }
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue24 = scriptValue23 != ScriptValue.NULL ? ((polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue23)) != null ? polyClassMachine_v35.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue23, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
