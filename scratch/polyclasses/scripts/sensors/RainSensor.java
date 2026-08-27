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
package dev.arubik.craftengine.script.gen.sensors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public final class RainSensor {
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
        PolyClassWorld polyClassWorld;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassWorld polyClassWorld2 = PolyClassWorld.ofVar((ScriptContext)scriptContext, (String)"World");
        if ((polyClassWorld2 != null ? polyClassWorld2.tg$39_is_raining() : ((scriptValue2 = scriptContext.getClassOrVar("World")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_raining", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool())) || ((polyClassWorld = PolyClassWorld.ofVar((ScriptContext)scriptContext, (String)"World")) != null ? polyClassWorld.tg$41_is_thundering() : ((scriptValue = scriptContext.getClassOrVar("World")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_thundering", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool() : ScriptValue.NULL.asBool()))) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                double d = 15.0;
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine.tm$108_emit_redstone(d));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                double d = 0.0;
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v1 = ScriptValue.of((boolean)polyClassMachine.tm$108_emit_redstone(d));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "emit_redstone", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
