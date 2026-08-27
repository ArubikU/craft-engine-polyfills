/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassBlockPlaceEvent;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassBlockMultiPlaceEvent
extends PolyClassBlockPlaceEvent {
    public static void refresh() {
    }

    public PolyClassBlockMultiPlaceEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockMultiPlaceEvent of(Object object) {
        return new PolyClassBlockMultiPlaceEvent(object);
    }

    public static PolyClassBlockMultiPlaceEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BlockMultiPlaceEvent")) {
            return new PolyClassBlockMultiPlaceEvent(object);
        }
        return null;
    }
}
