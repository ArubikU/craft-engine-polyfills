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
import dev.arubik.craftengine.script.PolyClassBlockGrowEvent;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassBlockFormEvent
extends PolyClassBlockGrowEvent {
    public static void refresh() {
    }

    public PolyClassBlockFormEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockFormEvent of(Object object) {
        return new PolyClassBlockFormEvent(object);
    }

    public static PolyClassBlockFormEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BlockFormEvent")) {
            return new PolyClassBlockFormEvent(object);
        }
        return null;
    }

    public static PolyClassBlockFormEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassBlockFormEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
