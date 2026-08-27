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
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassRedstone;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public final class EyeSensor {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            double d = 16.0;
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = ScriptValue.of((boolean)polyClassMachine_v4.tm$30_is_player_looking_at(d));
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "is_player_looking_at", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool()) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue2;
            PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue3 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$171_redstone() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d = 15.0;
            if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Redstone")) {
                PolyClassRedstone polyClassRedstone = new PolyClassRedstone(object3);
                v1 = ScriptValue.of((boolean)polyClassRedstone.tm$0_set(d));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue4;
            ScriptValue scriptValue5;
            PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            Object object5 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$171_redstone() : (scriptValue5 = (scriptValue4 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL);
            if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Redstone")) {
                PolyClassRedstone polyClassRedstone = new PolyClassRedstone(object4);
                v3 = ScriptValue.of((boolean)polyClassRedstone.tm$8_off());
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "off", (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        }
        FILE_SCOPE = builder.build();
    }
}
