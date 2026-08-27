/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
        ScriptValue scriptValue;
        Object object;
        Object object2;
        ScriptValue scriptValue2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "scan_range";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                object3 = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object3;
        builder.val("scan_range", scriptValue4);
        if (scriptValue4.asNum() <= 0.0) {
            double d = 5.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)5.0);
            builder.val("scan_range", scriptValue5);
        }
        if ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "mode";
            String string3 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object5);
                object2 = polyClassMachine.tm$34_get_typed(string, string3);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object2;
        builder.val("mode", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("scan_range");
            if (scriptValue7 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object6);
                object = polyClassMachine.tm$94_nearby_entities(scriptValue8.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("entities", scriptValue9);
        double d = 0.0;
        ScriptValue scriptValue10 = ScriptValue.of((double)0.0);
        builder.val("count", scriptValue10);
        double d2 = 9999.0;
        ScriptValue scriptValue11 = ScriptValue.of((double)9999.0);
        builder.val("min_dist_sq", scriptValue11);
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue12 = polyClassMachine != null ? polyClassMachine.pg$185_pos() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("machine", scriptValue12);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue9);
        Object object7 = scriptContext.getClassOrVar("dsq");
        ScriptValue scriptValue13 = ScriptValue.of((double)d2);
        ScriptValue scriptValue14 = ScriptValue.of((double)d);
        if (list != null) {
            for (ScriptValue scriptValue15 : list) {
                builder.val("entity", scriptValue15);
                if (!((scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_living", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && (scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool())) continue;
                ScriptValue scriptValue16 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 1.0)));
                builder.val("count", scriptValue16);
                scriptValue14 = scriptValue16;
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "distance_sq", (ScriptValue)(scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                builder.val("dsq", (ScriptValue)callSite);
                object7 = callSite;
                if (!(object7.asNum() < scriptValue13.asNum())) continue;
                Object object8 = object7;
                builder.val("min_dist_sq", object8);
                scriptValue13 = object8;
            }
        }
        double d3 = 0.0;
        ScriptValue scriptValue17 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue17);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 0.0)))) {
            double d4 = scriptValue14.asNum() > 0.0 ? 15.0 : 0.0;
            ScriptValue scriptValue18 = ScriptValue.of((double)d4);
            builder.val("power", scriptValue18);
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 1.0)))) {
            double d5;
            Object object9;
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
            if (scriptValue19 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                String string = "threshold";
                String string4 = "int";
                if (scriptValue19 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine2 = new PolyClassMachine(object10);
                    object9 = polyClassMachine2.tm$34_get_typed(string, string4);
                } else {
                    object9 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
                }
            } else {
                object9 = ScriptValue.NULL;
            }
            ScriptValue scriptValue20 = object9;
            builder.val("threshold", scriptValue20);
            if (scriptValue20.asNum() <= 0.0) {
                double d6 = 5.0;
                ScriptValue scriptValue21 = ScriptValue.of((double)5.0);
                builder.val("threshold", scriptValue21);
            }
            ScriptValue scriptValue22 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)Math.floor(((d5 = scriptContext.getNum("threshold")) == 0.0 ? 0.0 : scriptValue14.asNum() / d5) * 15.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 15.0)), (ScriptContext)scriptContext);
            builder.val("power", scriptValue22);
        } else if (scriptValue14.asNum() > 0.0) {
            double d7 = Math.sqrt(scriptValue13.asNum());
            ScriptValue scriptValue23 = ScriptValue.of((double)d7);
            builder.val("min_dist", scriptValue23);
            double d8 = scriptContext.getNum("scan_range");
            ScriptValue scriptValue24 = ScriptFormula.callBuiltin3((String)"clamp", (ScriptValue)ScriptValue.of((double)Math.floor((1.0 - (d8 == 0.0 ? 0.0 : d7 / d8)) * 15.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 15.0)), (ScriptContext)scriptContext);
            builder.val("power", scriptValue24);
        }
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
        if (scriptValue25 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object11;
            ScriptValue scriptValue26 = scriptContext.getClassOrVar("power");
            if (scriptValue25 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine3 = new PolyClassMachine(object11);
                v4 = ScriptValue.of((boolean)polyClassMachine3.tm$108_emit_redstone(scriptValue26.asNum()));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue25, (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
