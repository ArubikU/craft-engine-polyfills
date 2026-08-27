/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v4
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassVector
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassBlock_v4;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassVector;
import dev.arubik.craftengine.script.PolyClassWorld;
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
        PolyClassBlock_v4 polyClassBlock_v4;
        ScriptValue scriptValue;
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine2 != null ? polyClassMachine2.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("front", scriptValue4);
        ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? ((polyClassBlock_v4 = PolyClassBlock_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassBlock_v4.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
                ScriptValue.Obj obj4;
                Object object5;
                PolyClassBlock_v4 polyClassBlock_v42;
                Object object6;
                PolyClassBlock_v4 polyClassBlock_v43;
                Object object7;
                PolyClassBlock_v4 polyClassBlock_v44;
                ScriptValue scriptValue8;
                PolyClassBlock_v4 polyClassBlock_v45;
                Object object8 = scriptValue4 != ScriptValue.NULL ? ((polyClassBlock_v45 = PolyClassBlock_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassBlock_v45.pg$46_world() : PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : (scriptValue8 = ScriptValue.NULL);
                ScriptValue scriptValue9 = scriptValue4 != ScriptValue.NULL ? ((polyClassBlock_v44 = PolyClassBlock_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassBlock_v44.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector = PolyClassVector.ofGuarded((ScriptValue)scriptValue9);
                Object object9 = object7 = polyClassVector != null ? polyClassVector.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                ScriptValue scriptValue10 = scriptValue4 != ScriptValue.NULL ? ((polyClassBlock_v43 = PolyClassBlock_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassBlock_v43.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector2 = PolyClassVector.ofGuarded((ScriptValue)scriptValue10);
                Object object10 = object6 = polyClassVector2 != null ? polyClassVector2.pg$14_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                ScriptValue scriptValue11 = scriptValue4 != ScriptValue.NULL ? ((polyClassBlock_v42 = PolyClassBlock_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassBlock_v42.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector3 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11);
                Object object11 = polyClassVector3 != null ? polyClassVector3.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                String string = "minecraft:air";
                if (scriptValue8 instanceof ScriptValue.Obj && (object5 = (obj4 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object5 instanceof PolyClass) && obj4.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object5);
                    v4 = ScriptValue.of((boolean)polyClassWorld.tm$14_set_block(object7.asNum(), object6.asNum(), object11.asNum(), string));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)scriptValue8, (ScriptValue)object7, (ScriptValue)object6, (ScriptValue)object11, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue12 = polyClassMachine3 != null ? polyClassMachine3.pg$120_container() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d2 = 3.0;
                double d3 = 1.0;
                if (scriptValue12 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
                    v5 = polyClassContainer.tm$6_remove_item(d2, d3);
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                }
                PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue13 = polyClassMachine4 != null ? polyClassMachine4.pg$120_container() : ((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d4 = 5.0;
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Item");
                if (scriptValue14 != ScriptValue.NULL) {
                    ScriptValue.Obj obj5;
                    Object object12;
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:water_bucket"));
                    object3 = scriptValue14 instanceof ScriptValue.Obj && (object12 = (obj5 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object12 instanceof PolyClass) && obj5.typeName().equals("Item") ? new PolyClassItem(object12).um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                CallSite callSite3 = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BucketRefiller.class, 1.0)), (ScriptContext)scriptContext);
                if (scriptValue13 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                    v7 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d4, (ScriptValue)callSite3));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)callSite3, (ScriptContext)scriptContext);
                }
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"minecraft:lava")) {
                ScriptValue.Obj obj6;
                Object object13;
                Object object14;
                ScriptValue scriptValue15;
                ScriptValue.Obj obj7;
                Object object15;
                ScriptValue scriptValue16;
                ScriptValue.Obj obj8;
                Object object16;
                PolyClassBlock_v4 polyClassBlock_v46;
                Object object17;
                PolyClassBlock_v4 polyClassBlock_v47;
                Object object18;
                PolyClassBlock_v4 polyClassBlock_v48;
                ScriptValue scriptValue17;
                PolyClassBlock_v4 polyClassBlock_v49;
                Object object19 = scriptValue4 != ScriptValue.NULL ? ((polyClassBlock_v49 = PolyClassBlock_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassBlock_v49.pg$46_world() : PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : (scriptValue17 = ScriptValue.NULL);
                ScriptValue scriptValue18 = scriptValue4 != ScriptValue.NULL ? ((polyClassBlock_v48 = PolyClassBlock_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassBlock_v48.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector = PolyClassVector.ofGuarded((ScriptValue)scriptValue18);
                Object object20 = object18 = polyClassVector != null ? polyClassVector.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue18, (ScriptContext)scriptContext);
                ScriptValue scriptValue19 = scriptValue4 != ScriptValue.NULL ? ((polyClassBlock_v47 = PolyClassBlock_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassBlock_v47.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector4 = PolyClassVector.ofGuarded((ScriptValue)scriptValue19);
                Object object21 = object17 = polyClassVector4 != null ? polyClassVector4.pg$14_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
                ScriptValue scriptValue20 = scriptValue4 != ScriptValue.NULL ? ((polyClassBlock_v46 = PolyClassBlock_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassBlock_v46.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector5 = PolyClassVector.ofGuarded((ScriptValue)scriptValue20);
                Object object22 = polyClassVector5 != null ? polyClassVector5.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
                String string = "minecraft:air";
                if (scriptValue17 instanceof ScriptValue.Obj && (object16 = (obj8 = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object16 instanceof PolyClass) && obj8.typeName().equals("World")) {
                    PolyClassWorld polyClassWorld = new PolyClassWorld(object16);
                    v11 = ScriptValue.of((boolean)polyClassWorld.tm$14_set_block(object18.asNum(), object17.asNum(), object22.asNum(), string));
                } else {
                    v11 = PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)scriptValue17, (ScriptValue)object18, (ScriptValue)object17, (ScriptValue)object22, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
                PolyClassMachine polyClassMachine5 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue21 = polyClassMachine5 != null ? polyClassMachine5.pg$120_container() : ((scriptValue16 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d5 = 3.0;
                double d6 = 1.0;
                if (scriptValue21 instanceof ScriptValue.Obj && (object15 = (obj7 = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object15 instanceof PolyClass) && obj7.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object15);
                    v12 = polyClassContainer.tm$6_remove_item(d5, d6);
                } else {
                    v12 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
                }
                PolyClassMachine polyClassMachine6 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue22 = polyClassMachine6 != null ? polyClassMachine6.pg$120_container() : ((scriptValue15 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d7 = 5.0;
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("Item");
                if (scriptValue23 != ScriptValue.NULL) {
                    ScriptValue.Obj obj9;
                    Object object23;
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:lava_bucket"));
                    object14 = scriptValue23 instanceof ScriptValue.Obj && (object23 = (obj9 = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object23 instanceof PolyClass) && obj9.typeName().equals("Item") ? new PolyClassItem(object23).um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue23, arrayList, (ScriptContext)scriptContext);
                } else {
                    object14 = ScriptValue.NULL;
                }
                CallSite callSite4 = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BucketRefiller.class, 1.0)), (ScriptContext)scriptContext);
                if (scriptValue22 instanceof ScriptValue.Obj && (object13 = (obj6 = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object13 instanceof PolyClass) && obj6.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object13);
                    v14 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d7, (ScriptValue)callSite4));
                } else {
                    v14 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)callSite4, (ScriptContext)scriptContext);
                }
            }
        }
        FILE_SCOPE = builder.build();
    }
}
