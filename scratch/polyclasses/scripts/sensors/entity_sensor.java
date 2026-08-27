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
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class EntitySensor {
    public static void run(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        Object object2;
        Object object3;
        ScriptValue scriptValue;
        Object object4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object5;
            String string = "scan_range";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object5 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object5 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object5);
                object4 = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object4;
        builder.val("scan_range", scriptValue3);
        if (scriptValue3.asNum() <= 0.0) {
            double d = 5.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)5.0);
            builder.val("scan_range", scriptValue4);
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object6;
            String string = "mode";
            String string3 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object6 = (obj3 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object6 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object6);
                object3 = polyClassMachine_v4.tm$34_get_typed(string, string3);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object3;
        builder.val("mode", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj4;
            Object object7;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("scan_range");
            if (scriptValue6 instanceof ScriptValue.Obj && (object7 = (obj4 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object7 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object7);
                object2 = polyClassMachine_v4.tm$94_nearby_entities(scriptValue7.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue7);
                object2 = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object2;
        builder.val("entities", scriptValue8);
        double d = 0.0;
        ScriptValue scriptValue9 = ScriptValue.of((double)0.0);
        builder.val("count", scriptValue9);
        double d2 = 9999.0;
        ScriptValue scriptValue10 = ScriptValue.of((double)9999.0);
        builder.val("min_dist_sq", scriptValue10);
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? (scriptValue11 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$156_pos() : PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("machine", scriptValue12);
        List list = ScriptProgram.rowsOf((ScriptValue)scriptValue8, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                ScriptValue scriptValue13;
                builder.val("entity", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("entity");
                if (!((scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_living", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() && ((scriptValue13 = scriptContext.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool())) continue;
                ScriptValue scriptValue15 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("count"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("count", scriptValue15);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue12);
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("entity");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "distance_sq", (ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
                builder.val("dsq", (ScriptValue)callSite);
                if (!(callSite.asNum() < scriptContext.getNum("min_dist_sq"))) continue;
                CallSite callSite2 = callSite;
                builder.val("min_dist_sq", (ScriptValue)callSite2);
            }
        }
        double d3 = 0.0;
        ScriptValue scriptValue17 = ScriptValue.of((double)0.0);
        builder.val("power", scriptValue17);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)0.0))) {
            double d4 = scriptContext.getNum("count") > 0.0 ? 15.0 : 0.0;
            ScriptValue scriptValue18 = ScriptValue.of((double)d4);
            builder.val("power", scriptValue18);
        } else if (ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)1.0))) {
            Object object8;
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
            if (scriptValue19 != ScriptValue.NULL) {
                ScriptValue.Obj obj5;
                Object object9;
                String string = "threshold";
                String string4 = "int";
                if (scriptValue19 instanceof ScriptValue.Obj && (object9 = (obj5 = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object9 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object9);
                    object8 = polyClassMachine_v4.tm$34_get_typed(string, string4);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string4));
                    object8 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object8 = ScriptValue.NULL;
            }
            ScriptValue scriptValue20 = object8;
            builder.val("threshold", scriptValue20);
            if (scriptValue20.asNum() <= 0.0) {
                double d5 = 5.0;
                ScriptValue scriptValue21 = ScriptValue.of((double)5.0);
                builder.val("threshold", scriptValue21);
            }
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            double d6 = scriptContext.getNum("threshold");
            arrayList.add(ScriptValue.of((double)Math.floor((d6 == 0.0 ? 0.0 : scriptContext.getNum("count") / d6) * 15.0)));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)15.0));
            ScriptValue scriptValue22 = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext);
            builder.val("power", scriptValue22);
        } else if (scriptContext.getNum("count") > 0.0) {
            double d7 = Math.sqrt(scriptContext.getNum("min_dist_sq"));
            ScriptValue scriptValue23 = ScriptValue.of((double)d7);
            builder.val("min_dist", scriptValue23);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            double d8 = scriptContext.getNum("scan_range");
            arrayList.add(ScriptValue.of((double)Math.floor((1.0 - (d8 == 0.0 ? 0.0 : d7 / d8)) * 15.0)));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)15.0));
            ScriptValue scriptValue24 = ScriptFormula.callBuiltin((String)"clamp", arrayList, (ScriptContext)scriptContext);
            builder.val("power", scriptValue24);
        }
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
        if (scriptValue25 != ScriptValue.NULL) {
            ScriptValue.Obj obj6;
            Object object10;
            ScriptValue scriptValue26 = scriptContext.getClassOrVar("power");
            if (scriptValue25 instanceof ScriptValue.Obj && (object10 = (obj6 = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object10 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object10);
                v4 = ScriptValue.of((boolean)polyClassMachine_v4.tm$108_emit_redstone(scriptValue26.asNum()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue26);
                v4 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue25, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
    }
}
