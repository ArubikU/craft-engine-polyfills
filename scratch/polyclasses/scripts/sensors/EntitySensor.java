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
package dev.arubik.craftengine.script.gen.sensors;

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
public final class EntitySensor {
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
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "scan_range";
            String string2 = "int";
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object3 = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object3;
        builder.val("scan_range", scriptValue2);
        if (scriptValue2.asNum() <= 0.0) {
            double d = 5.0;
            ScriptValue scriptValue3 = ScriptValue.of((double)5.0);
            builder.val("scan_range", scriptValue3);
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "mode";
            String string3 = "int";
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v33 != null ? polyClassMachine_v33.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object2;
        builder.val("mode", scriptValue4);
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("scan_range");
            PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v34 != null ? polyClassMachine_v34.tm$94_nearby_entities(scriptValue5.asNum()) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("entities", scriptValue6);
        double d = 0.0;
        ScriptValue scriptValue7 = ScriptValue.of((double)0.0);
        builder.val("count", scriptValue7);
        double d2 = 9999.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)9999.0);
        builder.val("min_dist_sq", scriptValue8);
        ScriptValue scriptValue9 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$185_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("machine", scriptValue9);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue6);
        Object object4 = scriptContext.getClassOrVar("dsq");
        ScriptValue scriptValue10 = ScriptValue.of((double)d2);
        ScriptValue scriptValue11 = ScriptValue.of((double)d);
        if (list != null) {
            for (ScriptValue scriptValue12 : list) {
                builder.val("entity", scriptValue12);
                if (!((scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_living", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && (scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool())) continue;
                ScriptValue scriptValue13 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 1.0)));
                builder.val("count", scriptValue13);
                scriptValue11 = scriptValue13;
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "distance_sq", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                builder.val("dsq", (ScriptValue)callSite);
                object4 = callSite;
                if (!(object4.asNum() < scriptValue10.asNum())) continue;
                Object object5 = object4;
                builder.val("min_dist_sq", object5);
                scriptValue10 = object5;
            }
        }
        double d3 = 0.0;
        ScriptValue scriptValue14 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue14);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 0.0)))) {
            double d4 = scriptValue11.asNum() > 0.0 ? 15.0 : 0.0;
            ScriptValue scriptValue15 = ScriptValue.of((double)d4);
            builder.val("power", scriptValue15);
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 1.0)))) {
            double d5;
            Object object6;
            if (scriptValue != ScriptValue.NULL) {
                String string = "threshold";
                String string4 = "int";
                PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                object6 = polyClassMachine_v35 != null ? polyClassMachine_v35.tm$34_get_typed(string, string4) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
            } else {
                object6 = ScriptValue.NULL;
            }
            ScriptValue scriptValue16 = object6;
            builder.val("threshold", scriptValue16);
            if (scriptValue16.asNum() <= 0.0) {
                double d6 = 5.0;
                ScriptValue scriptValue17 = ScriptValue.of((double)5.0);
                builder.val("threshold", scriptValue17);
            }
            ScriptValue scriptValue18 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)Math.floor(((d5 = scriptContext.getNum("threshold")) == 0.0 ? 0.0 : scriptValue11.asNum() / d5) * 15.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 15.0)), (ScriptContext)scriptContext);
            builder.val("power", scriptValue18);
        } else if (scriptValue11.asNum() > 0.0) {
            double d7 = Math.sqrt(scriptValue10.asNum());
            ScriptValue scriptValue19 = ScriptValue.of((double)d7);
            builder.val("min_dist", scriptValue19);
            double d8 = scriptContext.getNum("scan_range");
            ScriptValue scriptValue20 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)Math.floor((1.0 - (d8 == 0.0 ? 0.0 : d7 / d8)) * 15.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 15.0)), (ScriptContext)scriptContext);
            builder.val("power", scriptValue20);
        }
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("power");
            PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v4 = polyClassMachine_v36 != null ? ScriptValue.of((boolean)polyClassMachine_v36.tm$108_emit_redstone(scriptValue21.asNum())) : PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue, (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
