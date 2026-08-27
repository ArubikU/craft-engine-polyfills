/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassRedstone;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
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
        ScriptValue.Obj obj;
        Object object;
        ScriptValue scriptValue;
        PolyClassMachine polyClassMachine;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        ScriptValue scriptValue2;
        Object object7;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object8;
            String string = "scan_range";
            String string2 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object8 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object8 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object8);
                object7 = polyClassMachine2.tm$34_get_typed(string, string2);
            } else {
                object7 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object7;
        builder.val("scan_range", scriptValue4);
        if (scriptValue4.asNum() <= 0.0) {
            double d = 3.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)3.0);
            builder.val("scan_range", scriptValue5);
        }
        if ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object9;
            String string = "full_speed";
            String string3 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object9 = (obj3 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object9 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine3 = new PolyClassMachine(object9);
                object6 = polyClassMachine3.tm$34_get_typed(string, string3);
            } else {
                object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object6;
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
            ScriptValue.Obj obj4;
            Object object10;
            String string = "deadzone";
            String string4 = "int";
            if (scriptValue9 instanceof ScriptValue.Obj && (object10 = (obj4 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object10 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine4 = new PolyClassMachine(object10);
                object5 = polyClassMachine4.tm$34_get_typed(string, string4);
            } else {
                object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
            }
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = object5;
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
            ScriptValue.Obj obj5;
            Object object11;
            String string = "living_only";
            String string5 = "int";
            if (scriptValue13 instanceof ScriptValue.Obj && (object11 = (obj5 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object11 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine5 = new PolyClassMachine(object11);
                object4 = polyClassMachine5.tm$34_get_typed(string, string5);
            } else {
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = object4;
        builder.val("living_only", scriptValue14);
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
        if (scriptValue15 != ScriptValue.NULL) {
            ScriptValue.Obj obj6;
            Object object12;
            String string = "count_y";
            String string6 = "int";
            if (scriptValue15 instanceof ScriptValue.Obj && (object12 = (obj6 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object12 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine6 = new PolyClassMachine(object12);
                object3 = polyClassMachine6.tm$34_get_typed(string, string6);
            } else {
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue16 = object3;
        builder.val("count_y", scriptValue16);
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
        if (scriptValue17 != ScriptValue.NULL) {
            ScriptValue.Obj obj7;
            Object object13;
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("scan_range");
            if (scriptValue17 instanceof ScriptValue.Obj && (object13 = (obj7 = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object13 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine7 = new PolyClassMachine(object13);
                object2 = polyClassMachine7.tm$94_nearby_entities(scriptValue18.asNum());
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue17, (ScriptValue)scriptValue18, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = object2;
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
        if (scriptValue34 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue34).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Redstone")) {
            PolyClassRedstone polyClassRedstone = new PolyClassRedstone(object);
            v6 = ScriptValue.of((boolean)polyClassRedstone.tm$0_set(scriptValue35.asNum()));
        } else {
            v6 = PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)scriptValue34, (ScriptValue)scriptValue35, (ScriptContext)scriptContext);
        }
        FILE_SCOPE = builder.build();
    }
}
