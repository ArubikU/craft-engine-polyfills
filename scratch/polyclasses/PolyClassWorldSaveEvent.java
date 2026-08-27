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

public class PolyClassWorldSaveEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"WorldSaveEvent", (String)"world");
    }

    public ScriptValue pg$0_world() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"WorldSaveEvent", (String)"world", (Object)this.instance);
    }

    public PolyClassWorldSaveEvent(Object object) {
        super(object);
    }

    public static PolyClassWorldSaveEvent of(Object object) {
        return new PolyClassWorldSaveEvent(object);
    }

    public static PolyClassWorldSaveEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("WorldSaveEvent")) {
            return new PolyClassWorldSaveEvent(object);
        }
        return null;
    }
}
