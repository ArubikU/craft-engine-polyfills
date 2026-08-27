/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
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
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClassBlock_v2;
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
        PolyClassBlock_v2 polyClassBlock_v2;
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 3.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue3);
        ScriptValue scriptValue4 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        builder.val("input", scriptValue4);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool()) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue5 = polyClassMachine2 != null ? polyClassMachine2.pg$137_facing_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("front", scriptValue5);
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? ((polyClassBlock_v2 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassBlock_v2.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("front_id", scriptValue6);
        if (ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"minecraft:bucket")) {
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"minecraft:water")) {
                Object object;
                ScriptValue scriptValue7;
                ScriptValue scriptValue8;
                PolyClassBlock_v2 polyClassBlock_v22;
                Object object2;
                PolyClassBlock_v2 polyClassBlock_v23;
                Object object3;
                PolyClassBlock_v2 polyClassBlock_v24;
                ScriptValue scriptValue9;
                PolyClassBlock_v2 polyClassBlock_v25;
                Object object4 = scriptValue5 != ScriptValue.NULL ? ((polyClassBlock_v25 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassBlock_v25.pg$46_world() : PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : (scriptValue9 = ScriptValue.NULL);
                ScriptValue scriptValue10 = scriptValue5 != ScriptValue.NULL ? ((polyClassBlock_v24 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassBlock_v24.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector = PolyClassVector.ofGuarded((ScriptValue)scriptValue10);
                Object object5 = object3 = polyClassVector != null ? polyClassVector.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                ScriptValue scriptValue11 = scriptValue5 != ScriptValue.NULL ? ((polyClassBlock_v23 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassBlock_v23.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector2 = PolyClassVector.ofGuarded((ScriptValue)scriptValue11);
                Object object6 = object2 = polyClassVector2 != null ? polyClassVector2.pg$14_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                ScriptValue scriptValue12 = scriptValue5 != ScriptValue.NULL ? ((polyClassBlock_v22 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassBlock_v22.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector3 = PolyClassVector.ofGuarded((ScriptValue)scriptValue12);
                Object object7 = polyClassVector3 != null ? polyClassVector3.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                String string = "minecraft:air";
                PolyClassWorld polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue9);
                CallSite callSite = polyClassWorld != null ? ScriptValue.of((boolean)polyClassWorld.tm$14_set_block(object3.asNum(), object2.asNum(), object7.asNum(), string)) : PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)scriptValue9, (ScriptValue)object3, (ScriptValue)object2, (ScriptValue)object7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue13 = polyClassMachine3 != null ? polyClassMachine3.pg$120_container() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d2 = 3.0;
                double d3 = 1.0;
                PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue13);
                Object object8 = polyClassContainer2 != null ? polyClassContainer2.tm$6_remove_item(d2, d3) : PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
                PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue14 = polyClassMachine4 != null ? polyClassMachine4.pg$120_container() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d4 = 5.0;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Item");
                if (scriptValue15 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:water_bucket"));
                    PolyClassItem polyClassItem = PolyClassItem.ofGuarded((ScriptValue)scriptValue15);
                    object = polyClassItem != null ? polyClassItem.um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BucketRefiller.class, 1.0)), (ScriptContext)scriptContext);
                PolyClassContainer polyClassContainer3 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue14);
                Object object9 = polyClassContainer3 != null ? ScriptValue.of((boolean)polyClassContainer3.tm$10_set_item(d4, (ScriptValue)callSite2)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)callSite2, (ScriptContext)scriptContext);
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"minecraft:lava")) {
                Object object;
                ScriptValue scriptValue16;
                ScriptValue scriptValue17;
                PolyClassBlock_v2 polyClassBlock_v26;
                Object object10;
                PolyClassBlock_v2 polyClassBlock_v27;
                Object object11;
                PolyClassBlock_v2 polyClassBlock_v28;
                ScriptValue scriptValue18;
                PolyClassBlock_v2 polyClassBlock_v29;
                Object object12 = scriptValue5 != ScriptValue.NULL ? ((polyClassBlock_v29 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassBlock_v29.pg$46_world() : PolyDispatch.bootstrapGet("memberGet", "world", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : (scriptValue18 = ScriptValue.NULL);
                ScriptValue scriptValue19 = scriptValue5 != ScriptValue.NULL ? ((polyClassBlock_v28 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassBlock_v28.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector = PolyClassVector.ofGuarded((ScriptValue)scriptValue19);
                Object object13 = object11 = polyClassVector != null ? polyClassVector.pg$10_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
                ScriptValue scriptValue20 = scriptValue5 != ScriptValue.NULL ? ((polyClassBlock_v27 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassBlock_v27.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector4 = PolyClassVector.ofGuarded((ScriptValue)scriptValue20);
                Object object14 = object10 = polyClassVector4 != null ? polyClassVector4.pg$14_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
                ScriptValue scriptValue21 = scriptValue5 != ScriptValue.NULL ? ((polyClassBlock_v26 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassBlock_v26.pg$47_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                PolyClassVector polyClassVector5 = PolyClassVector.ofGuarded((ScriptValue)scriptValue21);
                Object object15 = polyClassVector5 != null ? polyClassVector5.pg$16_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
                String string = "minecraft:air";
                PolyClassWorld polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue18);
                CallSite callSite = polyClassWorld != null ? ScriptValue.of((boolean)polyClassWorld.tm$14_set_block(object11.asNum(), object10.asNum(), object15.asNum(), string)) : PolyDispatch.bootstrapCall("memberCall", "set_block", (ScriptValue)scriptValue18, (ScriptValue)object11, (ScriptValue)object10, (ScriptValue)object15, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                PolyClassMachine polyClassMachine5 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue22 = polyClassMachine5 != null ? polyClassMachine5.pg$120_container() : ((scriptValue17 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d5 = 3.0;
                double d6 = 1.0;
                PolyClassContainer polyClassContainer4 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue22);
                Object object16 = polyClassContainer4 != null ? polyClassContainer4.tm$6_remove_item(d5, d6) : PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
                PolyClassMachine polyClassMachine6 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue23 = polyClassMachine6 != null ? polyClassMachine6.pg$120_container() : ((scriptValue16 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d7 = 5.0;
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("Item");
                if (scriptValue24 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BucketRefiller.class, "minecraft:lava_bucket"));
                    PolyClassItem polyClassItem = PolyClassItem.ofGuarded((ScriptValue)scriptValue24);
                    object = polyClassItem != null ? polyClassItem.um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue24, arrayList, (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                CallSite callSite3 = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)object, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BucketRefiller.class, 1.0)), (ScriptContext)scriptContext);
                PolyClassContainer polyClassContainer5 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue23);
                Object object17 = polyClassContainer5 != null ? ScriptValue.of((boolean)polyClassContainer5.tm$10_set_item(d7, (ScriptValue)callSite3)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)callSite3, (ScriptContext)scriptContext);
            }
        }
        FILE_SCOPE = builder.build();
    }
}
