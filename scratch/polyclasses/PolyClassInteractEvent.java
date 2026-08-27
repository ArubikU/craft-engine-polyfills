/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassInteractEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"InteractEvent", (String)"hand");
    }

    public ScriptValue pg$0_hand() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InteractEvent", (String)"hand", (Object)this.instance);
    }

    public PolyClassInteractEvent(Object object) {
        super(object);
    }

    public static PolyClassInteractEvent of(Object object) {
        return new PolyClassInteractEvent(object);
    }

    public static PolyClassInteractEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("InteractEvent")) {
            return new PolyClassInteractEvent(object);
        }
        return null;
    }
}
