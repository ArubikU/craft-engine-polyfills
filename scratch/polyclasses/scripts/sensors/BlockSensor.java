/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyClassRedstone
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyClassRedstone;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class BlockSensor {
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
        ScriptValue scriptValue;
        PolyClassMachine_v2 polyClassMachine_v2;
        CallSite callSite;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptValue scriptValue2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object4;
            String string = "scan_range";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object4);
                object3 = polyClassMachine_v22.tm$34_get_typed(string, string2);
            } else {
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object3;
        builder.val("scan_range", scriptValue4);
        if (scriptValue4.asNum() <= 0.0) {
            double d = 8.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)8.0);
            builder.val("scan_range", scriptValue5);
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("null");
        builder.val("filter_id", scriptValue6);
        PolyClassMachine_v2 polyClassMachine_v23 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue7 = polyClassMachine_v23 != null ? polyClassMachine_v23.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 4.0;
        if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("filter_item", (ScriptValue)callSite2);
        ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
        arrayList.add(callSite2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("filter_item");
            ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_id", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("filter_id", scriptValue9);
        }
        double d2 = 0.0;
        ScriptValue scriptValue10 = ScriptValue.of((double)0.0);
        builder.val("matched", scriptValue10);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("scan_range"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList2, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue11 : list) {
                Object object5;
                builder.val("i", scriptValue11);
                ScriptValue scriptValue12 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                builder.val("n", scriptValue12);
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
                if (scriptValue13 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    ScriptValue scriptValue14;
                    ScriptValue scriptValue15;
                    ScriptValue scriptValue16;
                    PolyClassMachine_v2 polyClassMachine_v24 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    double d3 = -(polyClassMachine_v24 != null ? polyClassMachine_v24.tg$134_facing_dx() : ((scriptValue16 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue16, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue12.asNum();
                    PolyClassMachine_v2 polyClassMachine_v25 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    double d4 = -(polyClassMachine_v25 != null ? polyClassMachine_v25.tg$130_facing_dy() : ((scriptValue15 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue15, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue12.asNum();
                    PolyClassMachine_v2 polyClassMachine_v26 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    double d5 = -(polyClassMachine_v26 != null ? polyClassMachine_v26.tg$132_facing_dz() : ((scriptValue14 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue12.asNum();
                    if (scriptValue13 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v27 = new PolyClassMachine_v2(object6);
                        object5 = polyClassMachine_v27.tm$68_block_at(d3, d4, d5);
                    } else {
                        object5 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d3), (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue17 = object5;
                builder.val("b", scriptValue17);
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("b");
                if (!((scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("filter_id"), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue19 = scriptContext.getClassOrVar("b");
                    if (!ScriptFormula.valuesEqual((ScriptValue)(scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("filter_id"))) continue;
                    ScriptValue scriptValue20 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("matched"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                    builder.val("matched", scriptValue20);
                    continue;
                }
                ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("matched"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                builder.val("matched", scriptValue21);
            }
        }
        double d6 = 0.0;
        ScriptValue scriptValue22 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue22);
        if (scriptContext.getNum("matched") > 0.0) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            double d7 = scriptContext.getNum("scan_range");
            arrayList3.add(ScriptValue.of((double)Math.floor((d7 == 0.0 ? 0.0 : scriptContext.getNum("matched") / d7) * 15.0)));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0));
            arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 15.0));
            ScriptValue scriptValue23 = ScriptFormula.callBuiltin((String)"clamp", arrayList3, (ScriptContext)scriptContext);
            builder.val("power", scriptValue23);
        }
        ScriptValue scriptValue24 = (polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine_v2.pg$171_redstone() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("power");
        if (scriptValue24 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Redstone")) {
            PolyClassRedstone polyClassRedstone = new PolyClassRedstone(object);
            v3 = ScriptValue.of((boolean)polyClassRedstone.tm$0_set(scriptValue25.asNum()));
        } else {
            v3 = PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)scriptValue24, (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
        }
        FILE_SCOPE = builder.build();
    }
}
