/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassRedstone;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
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
        PolyClassMachine_v4 polyClassMachine_v4;
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
                PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object4);
                object3 = polyClassMachine_v42.tm$34_get_typed(string, string2);
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
        PolyClassMachine_v4 polyClassMachine_v43 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue7 = polyClassMachine_v43 != null ? polyClassMachine_v43.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 4.0;
        if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("filter_item", (ScriptValue)callSite2);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue8 = callSite2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_id", (ScriptValue)callSite2, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("filter_id", scriptValue8);
        }
        double d2 = 0.0;
        ScriptValue scriptValue9 = ScriptValue.of((double)0.0);
        builder.val("matched", scriptValue9);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)scriptContext.getClassOrVar("scan_range"), (ScriptContext)scriptContext));
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("b");
        ScriptValue scriptValue11 = ScriptValue.of((double)d2);
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("n");
        if (list != null) {
            for (ScriptValue scriptValue13 : list) {
                Object object5;
                builder.val("i", scriptValue13);
                ScriptValue scriptValue14 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                builder.val("n", scriptValue14);
                scriptValue12 = scriptValue14;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                if (scriptValue15 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    ScriptValue scriptValue16;
                    ScriptValue scriptValue17;
                    ScriptValue scriptValue18;
                    PolyClassMachine_v4 polyClassMachine_v44 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    double d3 = -(polyClassMachine_v44 != null ? polyClassMachine_v44.tg$134_facing_dx() : ((scriptValue18 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue18, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue12.asNum();
                    PolyClassMachine_v4 polyClassMachine_v45 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    double d4 = -(polyClassMachine_v45 != null ? polyClassMachine_v45.tg$130_facing_dy() : ((scriptValue17 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue17, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue12.asNum();
                    PolyClassMachine_v4 polyClassMachine_v46 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    double d5 = -(polyClassMachine_v46 != null ? polyClassMachine_v46.tg$132_facing_dz() : ((scriptValue16 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue16, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue12.asNum();
                    if (scriptValue15 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v47 = new PolyClassMachine_v4(object6);
                        object5 = polyClassMachine_v47.tm$68_block_at(d3, d4, d5);
                    } else {
                        object5 = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d3), (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue19 = object5;
                builder.val("b", scriptValue19);
                scriptValue10 = scriptValue19;
                if (!((scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("filter_id"), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    if (!ScriptFormula.valuesEqual((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("filter_id"))) continue;
                    ScriptValue scriptValue20 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                    builder.val("matched", scriptValue20);
                    scriptValue11 = scriptValue20;
                    continue;
                }
                ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                builder.val("matched", scriptValue21);
                scriptValue11 = scriptValue21;
            }
        }
        double d6 = 0.0;
        ScriptValue scriptValue22 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue22);
        if (scriptValue11.asNum() > 0.0) {
            double d7 = scriptContext.getNum("scan_range");
            ScriptValue scriptValue23 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)Math.floor((d7 == 0.0 ? 0.0 : scriptValue11.asNum() / d7) * 15.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 15.0)), (ScriptContext)scriptContext);
            builder.val("power", scriptValue23);
        }
        ScriptValue scriptValue24 = (polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine_v4.pg$171_redstone() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
