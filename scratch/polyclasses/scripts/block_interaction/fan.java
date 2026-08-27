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
import java.util.List;

public final class Fan {
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
        Object object2;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$171_redstone() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("power", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            builder.val("__return__", ScriptValue.NULL);
            return;
        }
        double d = 15.0;
        double d2 = 15.0 == 0.0 ? 0.0 : scriptValue2.asNum() / d;
        ScriptValue scriptValue3 = ScriptValue.of((double)d2);
        builder.val("strength", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            String string = "inversed";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4);
            object2 = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("inversed", scriptValue5);
        if (scriptValue4 != ScriptValue.NULL) {
            double d3 = 5.0;
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4);
            object = polyClassMachine_v33 != null ? polyClassMachine_v33.tm$94_nearby_entities(d3) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)d3), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("entities", scriptValue6);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("dist_sq");
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("dx");
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("dy");
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("dz");
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("dist");
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("force");
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("impulse");
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("dir");
        if (list != null) {
            for (ScriptValue scriptValue15 : list) {
                ScriptValue scriptValue16;
                PolyClassMachine_v3 polyClassMachine_v34;
                PolyClassMachine_v3 polyClassMachine_v35;
                PolyClassMachine_v3 polyClassMachine_v36;
                PolyClassMachine_v3 polyClassMachine_v37;
                PolyClassMachine_v3 polyClassMachine_v38;
                PolyClassMachine_v3 polyClassMachine_v39;
                builder.val("entity", scriptValue15);
                if (!(scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) continue;
                double d4 = (scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - (scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v39 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v39.tg$201_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum());
                ScriptValue scriptValue17 = ScriptValue.of((double)d4);
                builder.val("dx", scriptValue17);
                scriptValue8 = scriptValue17;
                double d5 = (scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - (scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v38 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v38.tg$203_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum());
                ScriptValue scriptValue18 = ScriptValue.of((double)d5);
                builder.val("dy", scriptValue18);
                scriptValue9 = scriptValue18;
                double d6 = (scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asNum() - (scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v37.tg$207_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum());
                ScriptValue scriptValue19 = ScriptValue.of((double)d6);
                builder.val("dz", scriptValue19);
                scriptValue10 = scriptValue19;
                double d7 = scriptValue8.asNum() * scriptValue8.asNum() + scriptValue9.asNum() * scriptValue9.asNum() + scriptValue10.asNum() * scriptValue10.asNum();
                ScriptValue scriptValue20 = ScriptValue.of((double)d7);
                builder.val("dist_sq", scriptValue20);
                scriptValue7 = scriptValue20;
                if (!(scriptValue7.asNum() > 0.1)) continue;
                double d8 = Math.sqrt(scriptValue7.asNum());
                ScriptValue scriptValue21 = ScriptValue.of((double)d8);
                builder.val("dist", scriptValue21);
                scriptValue11 = scriptValue21;
                double d9 = scriptValue11.asNum();
                double d10 = d9 == 0.0 ? 0.0 : d2 / d9;
                ScriptValue scriptValue22 = ScriptValue.of((double)d10);
                builder.val("force", scriptValue22);
                scriptValue12 = scriptValue22;
                ScriptValue scriptValue23 = ScriptFormula.callBuiltin3((String)"vec", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v36.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v35.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v34.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptContext)scriptContext);
                builder.val("dir", scriptValue23);
                scriptValue14 = scriptValue23;
                if (scriptValue5.asNum() > 0.0) {
                    ScriptValue scriptValue24 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)(-1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("dir", scriptValue24);
                    scriptValue14 = scriptValue24;
                }
                ScriptValue scriptValue25 = (scriptValue16 = scriptContext.getClassOrVar("dir")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "scale", (ScriptValue)scriptValue16, (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("impulse", scriptValue25);
                scriptValue13 = scriptValue25;
                Object object3 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue15, (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
