/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class EnergyWindmill {
    public static void run(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        double d = 80.0;
        ScriptValue scriptValue = ScriptValue.of((double)80.0);
        builder.val("BASE", scriptValue);
        double d2 = 120.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)120.0);
        builder.val("STORM_BONUS", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("World");
        Object object2 = scriptValue3 != ScriptValue.NULL ? (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("World") ? new PolyClassWorld(object).pg$33_is_thundering() : PolyDispatch.bootstrapGet("memberGet", "is_thundering", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (object2.asBool()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object3;
                double d3 = d + d2;
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v2.tm$48_set_energy_per_tick(d3));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((double)d3));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_energy_per_tick", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            ScriptValue.Obj obj3;
            Object object4;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("World");
            Object object5 = scriptValue5 != ScriptValue.NULL ? (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("World") ? new PolyClassWorld(object4).pg$32_is_raining() : PolyDispatch.bootstrapGet("memberGet", "is_raining", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            if (object5.asBool()) {
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                if (scriptValue6 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    double d4 = 2.0;
                    double d5 = d + (2.0 == 0.0 ? 0.0 : d2 / d4);
                    if (scriptValue6 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object6);
                        v3 = ScriptValue.of((boolean)polyClassMachine_v2.tm$48_set_energy_per_tick(d5));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((double)d5));
                        v3 = PolyDispatch.bootstrapCall("memberCall", "set_energy_per_tick", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v3 = ScriptValue.NULL;
                }
            } else {
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
                if (scriptValue7 != ScriptValue.NULL) {
                    ScriptValue.Obj obj5;
                    Object object7;
                    double d6 = d;
                    if (scriptValue7 instanceof ScriptValue.Obj && (object7 = (obj5 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object7 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object7);
                        v4 = ScriptValue.of((boolean)polyClassMachine_v2.tm$48_set_energy_per_tick(d6));
                    } else {
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(ScriptValue.of((double)d6));
                        v4 = PolyDispatch.bootstrapCall("memberCall", "set_energy_per_tick", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
                    }
                } else {
                    v4 = ScriptValue.NULL;
                }
            }
        }
    }
}
