/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics.generators;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class EnergyWindmill {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassWorld polyClassWorld;
        ScriptContext scriptContext = builder.peek();
        double d = 80.0;
        ScriptValue scriptValue = ScriptValue.of((double)80.0);
        builder.val("BASE", scriptValue);
        double d2 = 120.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)120.0);
        builder.val("STORM_BONUS", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("World");
        Object object = scriptValue3 != ScriptValue.NULL ? ((polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassWorld.pg$33_is_thundering() : PolyDispatch.bootstrapGet("memberGet", "is_thundering", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (object.asBool()) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                double d3 = d + d2;
                if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                    v1 = ScriptValue.of((boolean)polyClassMachine.tm$48_set_energy_per_tick(d3));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((double)d3));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_energy_per_tick", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else {
            PolyClassWorld polyClassWorld2;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("World");
            Object object3 = scriptValue5 != ScriptValue.NULL ? ((polyClassWorld2 = PolyClassWorld.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassWorld2.pg$32_is_raining() : PolyDispatch.bootstrapGet("memberGet", "is_raining", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            if (object3.asBool()) {
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                if (scriptValue6 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object4;
                    double d4 = 2.0;
                    double d5 = d + (2.0 == 0.0 ? 0.0 : d2 / d4);
                    if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                        v3 = ScriptValue.of((boolean)polyClassMachine.tm$48_set_energy_per_tick(d5));
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
                    ScriptValue.Obj obj;
                    Object object5;
                    double d6 = d;
                    if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine = new PolyClassMachine(object5);
                        v4 = ScriptValue.of((boolean)polyClassMachine.tm$48_set_energy_per_tick(d6));
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
        FILE_SCOPE = builder.build();
    }
}
