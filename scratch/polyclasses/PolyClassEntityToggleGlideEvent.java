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

public class PolyClassEntityToggleGlideEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"EntityToggleGlideEvent", (String)"is_gliding");
    }

    public ScriptValue pg$0_is_gliding() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityToggleGlideEvent", (String)"is_gliding", (Object)this.instance);
    }

    public PolyClassEntityToggleGlideEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityToggleGlideEvent of(Object object) {
        return new PolyClassEntityToggleGlideEvent(object);
    }

    public static PolyClassEntityToggleGlideEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("EntityToggleGlideEvent")) {
            return new PolyClassEntityToggleGlideEvent(object);
        }
        return null;
    }
}
