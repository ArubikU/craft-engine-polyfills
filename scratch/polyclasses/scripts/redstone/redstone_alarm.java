/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.redstone;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class RedstoneAlarm {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        Object object = scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine.pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (object.asNum() > 0.0) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "minecraft:block.bell.use";
                double d = 2.0;
                double d2 = 1.0;
                if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine2 = new PolyClassMachine(object2);
                    v1 = ScriptValue.of((boolean)polyClassMachine2.tm$14_play_sound(string, d, d2));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((double)d));
                    arrayList.add(ScriptValue.of((double)d2));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "play_sound", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
