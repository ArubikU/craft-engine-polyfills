/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassPlayerToggleFlightEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerToggleFlightEvent", (String)"is_flying");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerToggleFlightEvent", (String)"is_flying", (String)"Z");
    }

    public ScriptValue pg$0_is_flying() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerToggleFlightEvent", (String)"is_flying", (Object)this.instance);
    }

    public boolean tg$1_is_flying() {
        if (tp$1 != null) {
            return (Boolean)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerToggleFlightEvent", (String)"is_flying", (Object)this.instance).asBool();
    }

    public PolyClassPlayerToggleFlightEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerToggleFlightEvent of(Object object) {
        return new PolyClassPlayerToggleFlightEvent(object);
    }

    public static PolyClassPlayerToggleFlightEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerToggleFlightEvent")) {
            return new PolyClassPlayerToggleFlightEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerToggleFlightEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerToggleFlightEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
