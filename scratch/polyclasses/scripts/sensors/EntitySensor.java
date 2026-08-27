/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassMachine_v2;
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object4);
                object3 = polyClassMachine_v2.tm$34_get_typed(string, string2);
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object5);
                object2 = polyClassMachine_v2.tm$34_get_typed(string, string3);
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object6);
                object = polyClassMachine_v2.tm$94_nearby_entities(scriptValue8.asNum());
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
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue12 = polyClassMachine_v2 != null ? polyClassMachine_v2.pg$185_pos() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("machine", scriptValue12);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue9);
        if (list != null) {
            for (ScriptValue scriptValue13 : list) {
                ScriptValue scriptValue14;
                builder.val("entity", scriptValue13);
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("entity");
                if (!((scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_living", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && ((scriptValue14 = scriptContext.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool())) continue;
                ScriptValue scriptValue16 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 1.0)));
                builder.val("count", scriptValue16);
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("entity");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "distance_sq", (ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                builder.val("dsq", (ScriptValue)callSite);
                if (!(callSite.asNum() < scriptContext.getNum("min_dist_sq"))) continue;
                CallSite callSite2 = callSite;
                builder.val("min_dist_sq", (ScriptValue)callSite2);
            }
        }
        double d3 = 0.0;
        ScriptValue scriptValue18 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue18);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 0.0)))) {
            double d4 = scriptContext.getNum("count") > 0.0 ? 15.0 : 0.0;
            ScriptValue scriptValue19 = ScriptValue.of((double)d4);
            builder.val("power", scriptValue19);
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 1.0)))) {
            Object object7;
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
            if (scriptValue20 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                String string = "threshold";
                String string4 = "int";
                if (scriptValue20 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object8);
                    object7 = polyClassMachine_v22.tm$34_get_typed(string, string4);
                } else {
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
                }
            } else {
                object7 = ScriptValue.NULL;
            }
            ScriptValue scriptValue21 = object7;
            builder.val("threshold", scriptValue21);
            if (scriptValue21.asNum() <= 0.0) {
                double d5 = 5.0;
                ScriptValue scriptValue22 = ScriptValue.of((double)5.0);
                builder.val("threshold", scriptValue22);
            }
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            double d6 = scriptContext.getNum("threshold");
            arrayList.add(ScriptValue.of((double)Math.floor((d6 == 0.0 ? 0.0 : scriptContext.getNum("count") / d6) * 15.0)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 15.0));
            ScriptValue scriptValue23 = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext);
            builder.val("power", scriptValue23);
        } else if (scriptContext.getNum("count") > 0.0) {
            double d7 = Math.sqrt(scriptContext.getNum("min_dist_sq"));
            ScriptValue scriptValue24 = ScriptValue.of((double)d7);
            builder.val("min_dist", scriptValue24);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            double d8 = scriptContext.getNum("scan_range");
            arrayList.add(ScriptValue.of((double)Math.floor((1.0 - (d8 == 0.0 ? 0.0 : d7 / d8)) * 15.0)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", EntitySensor.class, 15.0));
            ScriptValue scriptValue25 = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext);
            builder.val("power", scriptValue25);
        }
        ScriptValue scriptValue26 = scriptContext.getClassOrVar("Machine");
        if (scriptValue26 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object9;
            ScriptValue scriptValue27 = scriptContext.getClassOrVar("power");
            if (scriptValue26 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v23 = new PolyClassMachine_v2(object9);
                v4 = ScriptValue.of((boolean)polyClassMachine_v23.tm$108_emit_redstone(scriptValue27.asNum()));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue26, (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        FILE_SCOPE = builder.build();
    }
}
