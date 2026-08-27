/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
public final class SpeedSensor {
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
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "scan_range";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object6 = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object6;
        builder.val("scan_range", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 3.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)3.0);
            builder.val("scan_range", scriptValue3);
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "full_speed";
            String string3 = "int";
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object5 = polyClassMachine_v33 != null ? polyClassMachine_v33.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object5;
        builder.val("full_speed", scriptValue4);
        if (scriptValue4.asNum() <= 0.0) {
            double d = 50.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)50.0);
            builder.val("full_speed", scriptValue5);
        }
        double d = 100.0;
        double d2 = 100.0 == 0.0 ? 0.0 : scriptContext.getNum("full_speed") / d;
        ScriptValue scriptValue6 = ScriptValue.of((double)d2);
        builder.val("full", scriptValue6);
        if (scriptValue != ScriptValue.NULL) {
            String string = "deadzone";
            String string4 = "int";
            PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object4 = polyClassMachine_v34 != null ? polyClassMachine_v34.tm$34_get_typed(string, string4) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object4;
        builder.val("deadzone", scriptValue7);
        if (scriptValue7.asNum() <= 0.0) {
            double d3 = 10.0;
            ScriptValue scriptValue8 = ScriptValue.of((double)10.0);
            builder.val("deadzone", scriptValue8);
        }
        double d4 = 100.0;
        double d5 = 100.0 == 0.0 ? 0.0 : scriptContext.getNum("deadzone") / d4;
        ScriptValue scriptValue9 = ScriptValue.of((double)d5);
        builder.val("dead", scriptValue9);
        if (scriptValue != ScriptValue.NULL) {
            String string = "living_only";
            String string5 = "int";
            PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object3 = polyClassMachine_v35 != null ? polyClassMachine_v35.tm$34_get_typed(string, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = object3;
        builder.val("living_only", scriptValue10);
        if (scriptValue != ScriptValue.NULL) {
            String string = "count_y";
            String string6 = "int";
            PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v36 != null ? polyClassMachine_v36.tm$34_get_typed(string, string6) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = object2;
        builder.val("count_y", scriptValue11);
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("scan_range");
            PolyClassMachine_v3 polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v37 != null ? polyClassMachine_v37.tm$94_nearby_entities(scriptValue12.asNum()) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = object;
        builder.val("entities", scriptValue13);
        double d6 = 0.0;
        ScriptValue scriptValue14 = ScriptValue.of((double)0.0);
        builder.val("max_speed", scriptValue14);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue13);
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("vx");
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("vy");
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("vz");
        ScriptValue scriptValue18 = ScriptValue.of((double)d6);
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("speed");
        if (list != null) {
            for (ScriptValue scriptValue20 : list) {
                builder.val("entity", scriptValue20);
                if (scriptValue10.asNum() > 0.0 && (scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_living", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) continue;
                ScriptValue scriptValue21 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_x", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("vx", scriptValue21);
                scriptValue15 = scriptValue21;
                ScriptValue scriptValue22 = scriptValue11.asNum() > 0.0 ? (scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_y", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpeedSensor.class, 0.0));
                builder.val("vy", scriptValue22);
                scriptValue16 = scriptValue22;
                ScriptValue scriptValue23 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_z", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("vz", scriptValue23);
                scriptValue17 = scriptValue23;
                double d7 = Math.sqrt(scriptValue15.asNum() * scriptValue15.asNum() + scriptValue16.asNum() * scriptValue16.asNum() + scriptValue17.asNum() * scriptValue17.asNum());
                ScriptValue scriptValue24 = ScriptValue.of((double)d7);
                builder.val("speed", scriptValue24);
                scriptValue19 = scriptValue24;
                if (!(scriptValue19.asNum() > scriptValue18.asNum())) continue;
                ScriptValue scriptValue25 = scriptValue19;
                builder.val("max_speed", scriptValue25);
                scriptValue18 = scriptValue25;
            }
        }
        double d8 = 0.0;
        ScriptValue scriptValue26 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue26);
        if (scriptValue18.asNum() > d5) {
            double d9 = Math.max(d2 - d5, 0.01);
            ScriptValue scriptValue27 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptFormula.callBuiltin1((String)"round", (ScriptValue)ScriptValue.of((double)((d9 == 0.0 ? 0.0 : (scriptValue18.asNum() - d5) / d9) * 15.0)), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpeedSensor.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpeedSensor.class, 15.0)), (ScriptContext)scriptContext);
            builder.val("power", scriptValue27);
        }
        ScriptValue scriptValue28 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$171_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("power");
        PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue28);
        CallSite callSite = polyClassRedstone != null ? ScriptValue.of((boolean)polyClassRedstone.tm$0_set(scriptValue29.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)scriptValue28, (ScriptValue)scriptValue29, (ScriptContext)scriptContext);
        FILE_SCOPE = builder.build();
    }
}
