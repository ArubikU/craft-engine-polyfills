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

public class PolyClassEntityToggleGlideEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"EntityToggleGlideEvent", (String)"is_gliding");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"EntityToggleGlideEvent", (String)"is_gliding", (String)"Z");
    }

    public ScriptValue pg$0_is_gliding() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityToggleGlideEvent", (String)"is_gliding", (Object)this.instance);
    }

    public boolean tg$1_is_gliding() {
        if (tp$1 != null) {
            return (Boolean)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityToggleGlideEvent", (String)"is_gliding", (Object)this.instance).asBool();
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

    public static PolyClassEntityToggleGlideEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassEntityToggleGlideEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
