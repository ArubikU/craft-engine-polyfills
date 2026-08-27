/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

public final class SpeedSensor {
    public static void run(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        ScriptValue scriptValue;
        Object object7;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object8;
            String string = "scan_range";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object8 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object8 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object8);
                object7 = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object7 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object7;
        builder.val("scan_range", scriptValue3);
        if (scriptValue3.asNum() <= 0.0) {
            double d = 3.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)3.0);
            builder.val("scan_range", scriptValue4);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object9;
            String string = "full_speed";
            String string3 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object9 = (obj3 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object9 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object9);
                object6 = polyClassMachine_v4.tm$34_get_typed(string, string3);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object6;
        builder.val("full_speed", scriptValue5);
        if (scriptValue5.asNum() <= 0.0) {
            double d = 50.0;
            ScriptValue scriptValue6 = ScriptValue.of((double)50.0);
            builder.val("full_speed", scriptValue6);
        }
        double d = 100.0;
        double d2 = 100.0 == 0.0 ? 0.0 : scriptContext.getNum("full_speed") / d;
        ScriptValue scriptValue7 = ScriptValue.of((double)d2);
        builder.val("full", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj4;
            Object object10;
            String string = "deadzone";
            String string4 = "int";
            if (scriptValue8 instanceof ScriptValue.Obj && (object10 = (obj4 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object10 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object10);
                object5 = polyClassMachine_v4.tm$34_get_typed(string, string4);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string4));
                object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object5;
        builder.val("deadzone", scriptValue9);
        if (scriptValue9.asNum() <= 0.0) {
            double d3 = 10.0;
            ScriptValue scriptValue10 = ScriptValue.of((double)10.0);
            builder.val("deadzone", scriptValue10);
        }
        double d4 = 100.0;
        double d5 = 100.0 == 0.0 ? 0.0 : scriptContext.getNum("deadzone") / d4;
        ScriptValue scriptValue11 = ScriptValue.of((double)d5);
        builder.val("dead", scriptValue11);
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptValue.Obj obj5;
            Object object11;
            String string = "living_only";
            String string5 = "int";
            if (scriptValue12 instanceof ScriptValue.Obj && (object11 = (obj5 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object11 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object11);
                object4 = polyClassMachine_v4.tm$34_get_typed(string, string5);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string5));
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = object4;
        builder.val("living_only", scriptValue13);
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
        if (scriptValue14 != ScriptValue.NULL) {
            ScriptValue.Obj obj6;
            Object object12;
            String string = "count_y";
            String string6 = "int";
            if (scriptValue14 instanceof ScriptValue.Obj && (object12 = (obj6 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object12 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object12);
                object3 = polyClassMachine_v4.tm$34_get_typed(string, string6);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string6));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = object3;
        builder.val("count_y", scriptValue15);
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
        if (scriptValue16 != ScriptValue.NULL) {
            ScriptValue.Obj obj7;
            Object object13;
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("scan_range");
            if (scriptValue16 instanceof ScriptValue.Obj && (object13 = (obj7 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object13 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object13);
                object2 = polyClassMachine_v4.tm$94_nearby_entities(scriptValue17.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue17);
                object2 = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = object2;
        builder.val("entities", scriptValue18);
        double d6 = 0.0;
        ScriptValue scriptValue19 = ScriptValue.of((double)0.0);
        builder.val("max_speed", scriptValue19);
        List list = ScriptProgram.rowsOf((ScriptValue)scriptValue18, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                ScriptValue scriptValue20;
                ScriptValue scriptValue21;
                builder.val("entity", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                if (scriptValue13.asNum() > 0.0 && ((scriptValue21 = scriptContext.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_living", (ScriptValue)scriptValue21, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) continue;
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("entity");
                ScriptValue scriptValue23 = scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_x", (ScriptValue)scriptValue22, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("vx", scriptValue23);
                ScriptValue scriptValue24 = scriptValue15.asNum() > 0.0 ? ((scriptValue20 = scriptContext.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_y", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL) : ScriptValue.of((double)0.0);
                builder.val("vy", scriptValue24);
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("entity");
                ScriptValue scriptValue26 = scriptValue25 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "velocity_z", (ScriptValue)scriptValue25, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("vz", scriptValue26);
                double d7 = Math.sqrt(scriptValue23.asNum() * scriptValue23.asNum() + scriptValue24.asNum() * scriptValue24.asNum() + scriptValue26.asNum() * scriptValue26.asNum());
                ScriptValue scriptValue27 = ScriptValue.of((double)d7);
                builder.val("speed", scriptValue27);
                if (!(d7 > scriptContext.getNum("max_speed"))) continue;
                double d8 = d7;
                ScriptValue scriptValue28 = ScriptValue.of((double)d8);
                builder.val("max_speed", scriptValue28);
            }
        }
        double d9 = 0.0;
        ScriptValue scriptValue29 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue29);
        if (scriptContext.getNum("max_speed") > d5) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            double d10 = Math.max(d2 - d5, 0.01);
            arrayList2.add(ScriptValue.of((double)((d10 == 0.0 ? 0.0 : (scriptContext.getNum("max_speed") - d5) / d10) * 15.0)));
            arrayList.add(ScriptFormula.callBuiltin((String)"round", arrayList2, (ScriptContext)scriptContext));
            arrayList.add(ScriptValue.of((double)1.0));
            arrayList.add(ScriptValue.of((double)15.0));
            ScriptValue scriptValue30 = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext);
            builder.val("power", scriptValue30);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("power"));
        ScriptValue scriptValue31 = scriptContext.getClassOrVar("Machine");
        PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)(scriptValue31 != ScriptValue.NULL ? (scriptValue31 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue31).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue31, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
    }
}
