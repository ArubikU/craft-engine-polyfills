/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
        ScriptValue scriptValue;
        PolyClassMachine polyClassMachine;
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        ScriptValue scriptValue2;
        Object object6;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            String string = "scan_range";
            String string2 = "int";
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
            object6 = polyClassMachine2 != null ? polyClassMachine2.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object6;
        builder.val("scan_range", scriptValue4);
        if (scriptValue4.asNum() <= 0.0) {
            double d = 3.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)3.0);
            builder.val("scan_range", scriptValue5);
        }
        if ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            String string = "full_speed";
            String string3 = "int";
            PolyClassMachine polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            object5 = polyClassMachine3 != null ? polyClassMachine3.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object5;
        builder.val("full_speed", scriptValue6);
        if (scriptValue6.asNum() <= 0.0) {
            double d = 50.0;
            ScriptValue scriptValue7 = ScriptValue.of((double)50.0);
            builder.val("full_speed", scriptValue7);
        }
        double d = 100.0;
        double d2 = 100.0 == 0.0 ? 0.0 : scriptContext.getNum("full_speed") / d;
        ScriptValue scriptValue8 = ScriptValue.of((double)d2);
        builder.val("full", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
        if (scriptValue9 != ScriptValue.NULL) {
            String string = "deadzone";
            String string4 = "int";
            PolyClassMachine polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9);
            object4 = polyClassMachine4 != null ? polyClassMachine4.tm$34_get_typed(string, string4) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = object4;
        builder.val("deadzone", scriptValue10);
        if (scriptValue10.asNum() <= 0.0) {
            double d3 = 10.0;
            ScriptValue scriptValue11 = ScriptValue.of((double)10.0);
            builder.val("deadzone", scriptValue11);
        }
        double d4 = 100.0;
        double d5 = 100.0 == 0.0 ? 0.0 : scriptContext.getNum("deadzone") / d4;
        ScriptValue scriptValue12 = ScriptValue.of((double)d5);
        builder.val("dead", scriptValue12);
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
        if (scriptValue13 != ScriptValue.NULL) {
            String string = "living_only";
            String string5 = "int";
            PolyClassMachine polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue13);
            object3 = polyClassMachine5 != null ? polyClassMachine5.tm$34_get_typed(string, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = object3;
        builder.val("living_only", scriptValue14);
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
        if (scriptValue15 != ScriptValue.NULL) {
            String string = "count_y";
            String string6 = "int";
            PolyClassMachine polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue15);
            object2 = polyClassMachine6 != null ? polyClassMachine6.tm$34_get_typed(string, string6) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue16 = object2;
        builder.val("count_y", scriptValue16);
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
        if (scriptValue17 != ScriptValue.NULL) {
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("scan_range");
            PolyClassMachine polyClassMachine7 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue17);
            object = polyClassMachine7 != null ? polyClassMachine7.tm$94_nearby_entities(scriptValue18.asNum()) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue17, (ScriptValue)scriptValue18, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = object;
        builder.val("entities", scriptValue19);
        double d6 = 0.0;
        ScriptValue scriptValue20 = ScriptValue.of((double)0.0);
        builder.val("max_speed", scriptValue20);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue19);
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("vx");
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("vy");
        ScriptValue scriptValue23 = scriptContext.getClassOrVar("vz");
        ScriptValue scriptValue24 = ScriptValue.of((double)d6);
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("speed");
        if (list != null) {
            for (ScriptValue scriptValue26 : list) {
                builder.val("entity", scriptValue26);
                if (scriptValue14.asNum() > 0.0 && (scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_living", (ScriptValue)scriptValue26, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) continue;
                ScriptValue scriptValue27 = scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_x", (ScriptValue)scriptValue26, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("vx", scriptValue27);
                scriptValue21 = scriptValue27;
                ScriptValue scriptValue28 = scriptValue16.asNum() > 0.0 ? (scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_y", (ScriptValue)scriptValue26, (ScriptContext)scriptContext) : ScriptValue.NULL) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpeedSensor.class, 0.0));
                builder.val("vy", scriptValue28);
                scriptValue22 = scriptValue28;
                ScriptValue scriptValue29 = scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_z", (ScriptValue)scriptValue26, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("vz", scriptValue29);
                scriptValue23 = scriptValue29;
                double d7 = Math.sqrt(scriptValue21.asNum() * scriptValue21.asNum() + scriptValue22.asNum() * scriptValue22.asNum() + scriptValue23.asNum() * scriptValue23.asNum());
                ScriptValue scriptValue30 = ScriptValue.of((double)d7);
                builder.val("speed", scriptValue30);
                scriptValue25 = scriptValue30;
                if (!(scriptValue25.asNum() > scriptValue24.asNum())) continue;
                ScriptValue scriptValue31 = scriptValue25;
                builder.val("max_speed", scriptValue31);
                scriptValue24 = scriptValue31;
            }
        }
        double d8 = 0.0;
        ScriptValue scriptValue32 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue32);
        if (scriptValue24.asNum() > d5) {
            double d9 = Math.max(d2 - d5, 0.01);
            ScriptValue scriptValue33 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptFormula.callBuiltin1((String)"round", (ScriptValue)ScriptValue.of((double)((d9 == 0.0 ? 0.0 : (scriptValue24.asNum() - d5) / d9) * 15.0)), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpeedSensor.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", SpeedSensor.class, 15.0)), (ScriptContext)scriptContext);
            builder.val("power", scriptValue33);
        }
        ScriptValue scriptValue34 = (polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine.pg$171_redstone() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        ScriptValue scriptValue35 = scriptContext.getClassOrVar("power");
        PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue34);
        CallSite callSite = polyClassRedstone != null ? ScriptValue.of((boolean)polyClassRedstone.tm$0_set(scriptValue35.asNum())) : PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)scriptValue34, (ScriptValue)scriptValue35, (ScriptContext)scriptContext);
        FILE_SCOPE = builder.build();
    }
}
