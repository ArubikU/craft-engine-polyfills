/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
        ScriptValue scriptValue;
        PolyClassMachine polyClassMachine;
        ScriptValue scriptValue2;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            String string = "scan_range";
            String string2 = "int";
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
            object = polyClassMachine2 != null ? polyClassMachine2.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("scan_range", scriptValue4);
        if (scriptValue4.asNum() <= 0.0) {
            double d = 8.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)8.0);
            builder.val("scan_range", scriptValue5);
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("null");
        builder.val("filter_id", scriptValue6);
        PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue7 = polyClassMachine3 != null ? polyClassMachine3.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 4.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue7);
        ScriptValue scriptValue8 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        builder.val("filter_item", scriptValue8);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block_id", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("filter_id", scriptValue9);
        }
        double d2 = 0.0;
        ScriptValue scriptValue10 = ScriptValue.of((double)0.0);
        builder.val("matched", scriptValue10);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin1((String)"range", (ScriptValue)scriptContext.getClassOrVar("scan_range"), (ScriptContext)scriptContext));
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("b");
        ScriptValue scriptValue12 = ScriptValue.of((double)d2);
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("n");
        if (list != null) {
            for (ScriptValue scriptValue14 : list) {
                Object object2;
                builder.val("i", scriptValue14);
                ScriptValue scriptValue15 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                builder.val("n", scriptValue15);
                scriptValue13 = scriptValue15;
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
                if (scriptValue16 != ScriptValue.NULL) {
                    ScriptValue scriptValue17;
                    ScriptValue scriptValue18;
                    ScriptValue scriptValue19;
                    PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    double d3 = -(polyClassMachine4 != null ? polyClassMachine4.tg$134_facing_dx() : ((scriptValue19 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue19, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue13.asNum();
                    PolyClassMachine polyClassMachine5 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    double d4 = -(polyClassMachine5 != null ? polyClassMachine5.tg$130_facing_dy() : ((scriptValue18 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue18, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue13.asNum();
                    PolyClassMachine polyClassMachine6 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                    double d5 = -(polyClassMachine6 != null ? polyClassMachine6.tg$132_facing_dz() : ((scriptValue17 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue17, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue13.asNum();
                    PolyClassMachine polyClassMachine7 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue16);
                    object2 = polyClassMachine7 != null ? polyClassMachine7.tm$68_block_at(d3, d4, d5) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((double)d3), (ScriptValue)ScriptValue.of((double)d4), (ScriptValue)ScriptValue.of((double)d5), (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue20 = object2;
                builder.val("b", scriptValue20);
                scriptValue11 = scriptValue20;
                if (!((scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("filter_id"), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    if (!ScriptFormula.valuesEqual((ScriptValue)(scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("filter_id"))) continue;
                    ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                    builder.val("matched", scriptValue21);
                    scriptValue12 = scriptValue21;
                    continue;
                }
                ScriptValue scriptValue22 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)));
                builder.val("matched", scriptValue22);
                scriptValue12 = scriptValue22;
            }
        }
        double d6 = 0.0;
        ScriptValue scriptValue23 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue23);
        if (scriptValue12.asNum() > 0.0) {
            double d7 = scriptContext.getNum("scan_range");
            ScriptValue scriptValue24 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)Math.floor((d7 == 0.0 ? 0.0 : scriptValue12.asNum() / d7) * 15.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BlockSensor.class, 15.0)), (ScriptContext)scriptContext);
            builder.val("power", scriptValue24);
        }
        ScriptValue scriptValue25 = (polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine.pg$171_redstone() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        ScriptValue scriptValue26 = scriptContext.getClassOrVar("power");
        PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue25);
        CallSite callSite = polyClassRedstone != null ? ScriptValue.of((boolean)polyClassRedstone.tm$0_set(scriptValue26.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)scriptValue25, (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
        FILE_SCOPE = builder.build();
    }
}
