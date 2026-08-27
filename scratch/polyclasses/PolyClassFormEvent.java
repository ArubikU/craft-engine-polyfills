/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassFormEvent
extends PolyClassEvent {
    public static void refresh() {
    }

    public PolyClassFormEvent(Object object) {
        super(object);
    }

    public static PolyClassFormEvent of(Object object) {
        return new PolyClassFormEvent(object);
    }

    public static PolyClassFormEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("FormEvent")) {
            return new PolyClassFormEvent(object);
        }
        return null;
    }

    public static PolyClassFormEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassFormEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
