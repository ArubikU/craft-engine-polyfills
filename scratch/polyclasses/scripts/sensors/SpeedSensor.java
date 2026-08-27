/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassRedstone;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

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
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptValue scriptValue;
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
                PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object8);
                object7 = polyClassMachine_v42.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object7 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v43 = new PolyClassMachine_v4(object9);
                object6 = polyClassMachine_v43.tm$34_get_typed(string, string3);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v44 = new PolyClassMachine_v4(object10);
                object5 = polyClassMachine_v44.tm$34_get_typed(string, string4);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string4));
                object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v45 = new PolyClassMachine_v4(object11);
                object4 = polyClassMachine_v45.tm$34_get_typed(string, string5);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string5));
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v46 = new PolyClassMachine_v4(object12);
                object3 = polyClassMachine_v46.tm$34_get_typed(string, string6);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string6));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v47 = new PolyClassMachine_v4(object13);
                object2 = polyClassMachine_v47.tm$94_nearby_entities(scriptValue18.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue18);
                object2 = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue17, arrayList, (ScriptContext)scriptContext);
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
        if (list != null) {
            for (ScriptValue scriptValue21 : list) {
                ScriptValue scriptValue22;
                ScriptValue scriptValue23;
                builder.val("entity", scriptValue21);
                if (scriptValue14.asNum() > 0.0 && ((scriptValue23 = scriptContext.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_living", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) continue;
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("entity");
                ScriptValue scriptValue25 = scriptValue24 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_x", (ScriptValue)scriptValue24, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("vx", scriptValue25);
                ScriptValue scriptValue26 = scriptValue16.asNum() > 0.0 ? ((scriptValue22 = scriptContext.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_y", (ScriptValue)scriptValue22, (ScriptContext)scriptContext) : ScriptValue.NULL) : ScriptValue.of((double)0.0);
                builder.val("vy", scriptValue26);
                ScriptValue scriptValue27 = scriptContext.getClassOrVar("entity");
                ScriptValue scriptValue28 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_z", (ScriptValue)scriptValue27, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("vz", scriptValue28);
                double d7 = Math.sqrt(scriptValue25.asNum() * scriptValue25.asNum() + scriptValue26.asNum() * scriptValue26.asNum() + scriptValue28.asNum() * scriptValue28.asNum());
                ScriptValue scriptValue29 = ScriptValue.of((double)d7);
                builder.val("speed", scriptValue29);
                if (!(d7 > scriptContext.getNum("max_speed"))) continue;
                double d8 = d7;
                ScriptValue scriptValue30 = ScriptValue.of((double)d8);
                builder.val("max_speed", scriptValue30);
            }
        }
        double d9 = 0.0;
        ScriptValue scriptValue31 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue31);
        if (scriptContext.getNum("max_speed") > d5) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            double d10 = Math.max(d2 - d5, 0.01);
            arrayList2.add(ScriptValue.of((double)((d10 == 0.0 ? 0.0 : (scriptContext.getNum("max_speed") - d5) / d10) * 15.0)));
            arrayList.add(ScriptFormula.callBuiltin((String)"round", arrayList2, (ScriptContext)scriptContext));
            arrayList.add(ScriptValue.of((double)1.0));
            arrayList.add(ScriptValue.of((double)15.0));
            ScriptValue scriptValue32 = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext);
            builder.val("power", scriptValue32);
        }
        ScriptValue scriptValue33 = (scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        ScriptValue scriptValue34 = scriptContext.getClassOrVar("power");
        if (scriptValue33 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue33).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Redstone")) {
            PolyClassRedstone polyClassRedstone = new PolyClassRedstone(object);
            v6 = ScriptValue.of((boolean)polyClassRedstone.tm$0_set(scriptValue34.asNum()));
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue34);
            v6 = PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)scriptValue33, arrayList, (ScriptContext)scriptContext);
        }
        FILE_SCOPE = builder.build();
    }
}
