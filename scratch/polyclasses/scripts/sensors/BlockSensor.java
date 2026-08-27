/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassRedstone
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
        PolyClassMachine_v3 polyClassMachine_v3;
        PolyClassMachine_v3 polyClassMachine_v32;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "scan_range";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v33 != null ? polyClassMachine_v33.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("scan_range", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 8.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)8.0);
            builder.val("scan_range", scriptValue3);
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("null");
        builder.val("filter_id", scriptValue4);
        ScriptValue scriptValue5 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v32.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 4.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue5);
        ScriptValue scriptValue6 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        builder.val("filter_item", scriptValue6);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("filter_id", scriptValue7);
        }
        double d2 = 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)0.0);
        builder.val("matched", scriptValue8);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)scriptContext.getClassOrVar("scan_range"), (ScriptContext)scriptContext));
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("b");
        ScriptValue scriptValue10 = ScriptValue.of((double)d2);
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("n");
        if (list != null) {
            for (ScriptValue scriptValue12 : list) {
                Object object2;
                builder.val("i", scriptValue12);
                ScriptValue scriptValue13 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                builder.val("n", scriptValue13);
                scriptValue11 = scriptValue13;
                if (scriptValue != ScriptValue.NULL) {
                    PolyClassMachine_v3 polyClassMachine_v34;
                    PolyClassMachine_v3 polyClassMachine_v35;
                    PolyClassMachine_v3 polyClassMachine_v36;
                    double d3 = -(scriptValue != ScriptValue.NULL ? ((polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v36.tg$134_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptValue11.asNum();
                    double d4 = -(scriptValue != ScriptValue.NULL ? ((polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v35.tg$130_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptValue11.asNum();
                    double d5 = -(scriptValue != ScriptValue.NULL ? ((polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v34.tg$132_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptValue11.asNum();
                    PolyClassMachine_v3 polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                    object2 = polyClassMachine_v37 != null ? polyClassMachine_v37.tm$68_block_at(d3, d4, d5) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d3), (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue14 = object2;
                builder.val("b", scriptValue14);
                scriptValue9 = scriptValue14;
                if (!((scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("filter_id"), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    if (!ScriptFormula.valuesEqual((ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("filter_id"))) continue;
                    ScriptValue scriptValue15 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                    builder.val("matched", scriptValue15);
                    scriptValue10 = scriptValue15;
                    continue;
                }
                ScriptValue scriptValue16 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                builder.val("matched", scriptValue16);
                scriptValue10 = scriptValue16;
            }
        }
        double d6 = 0.0;
        ScriptValue scriptValue17 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue17);
        if (scriptValue10.asNum() > 0.0) {
            double d7 = scriptContext.getNum("scan_range");
            ScriptValue scriptValue18 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)Math.floor((d7 == 0.0 ? 0.0 : scriptValue10.asNum() / d7) * 15.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 15.0)), (ScriptContext)scriptContext);
            builder.val("power", scriptValue18);
        }
        ScriptValue scriptValue19 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$171_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("power");
        PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue19);
        CallSite callSite = polyClassRedstone != null ? ScriptValue.of((boolean)polyClassRedstone.tm$0_set(scriptValue20.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)scriptValue19, (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
        FILE_SCOPE = builder.build();
    }
}
