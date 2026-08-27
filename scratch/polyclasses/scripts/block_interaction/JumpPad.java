/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
public final class JumpPad {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        Object object;
        PolyClassMachine_v3 polyClassMachine_v3;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "force";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("force", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 1.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)1.0);
            builder.val("force", scriptValue3);
        }
        double d = 1.0 * scriptContext.getNum("force");
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        builder.val("horiz_scale", scriptValue4);
        double d2 = 0.8 * scriptContext.getNum("force");
        ScriptValue scriptValue5 = ScriptValue.of((double)d2);
        builder.val("vert_power", scriptValue5);
        boolean bl = ScriptFormula.valuesEqual((ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0))) ^ true;
        ScriptValue scriptValue6 = ScriptValue.of((boolean)bl);
        builder.val("is_vertical", scriptValue6);
        if (scriptValue != ScriptValue.NULL) {
            double d3 = 0.7;
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v33 != null ? polyClassMachine_v33.tm$94_nearby_entities(d3) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("entities", scriptValue7);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue7);
        Object object3 = scriptContext.getClassOrVar("launch");
        Object object4 = scriptContext.getClassOrVar("dir");
        if (list != null) {
            for (ScriptValue scriptValue8 : list) {
                PolyClassMachine_v3 polyClassMachine_v34;
                PolyClassMachine_v3 polyClassMachine_v35;
                builder.val("entity", scriptValue8);
                if (!(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) continue;
                if (bl) {
                    Object object5 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue8, (ScriptValue)ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0)), (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0)), (ScriptContext)scriptContext), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    continue;
                }
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "normalize", (ScriptValue)ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v35.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0)), (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v34.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("dir", (ScriptValue)callSite);
                object4 = callSite;
                CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "add", (ScriptValue)(object4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)object4, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0)), (ScriptValue)ScriptValue.of((double)(d2 * 0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", JumpPad.class, 0.0)), (ScriptContext)scriptContext), (ScriptContext)scriptContext);
                builder.val("launch", (ScriptValue)callSite2);
                object3 = callSite2;
                Object object6 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue8, (ScriptValue)object3, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
