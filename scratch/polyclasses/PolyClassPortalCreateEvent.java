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

public class PolyClassPortalCreateEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PortalCreateEvent", (String)"reason");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PortalCreateEvent", (String)"reason", (String)"S");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PortalCreateEvent", (String)"world");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"PortalCreateEvent", (String)"blocks");
    }

    public ScriptValue pg$0_reason() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PortalCreateEvent", (String)"reason", (Object)this.instance);
    }

    public String tg$1_reason() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PortalCreateEvent", (String)"reason", (Object)this.instance).asStr();
    }

    public ScriptValue pg$2_world() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PortalCreateEvent", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$3_blocks() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PortalCreateEvent", (String)"blocks", (Object)this.instance);
    }

    public PolyClassPortalCreateEvent(Object object) {
        super(object);
    }

    public static PolyClassPortalCreateEvent of(Object object) {
        return new PolyClassPortalCreateEvent(object);
    }

    public static PolyClassPortalCreateEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PortalCreateEvent")) {
            return new PolyClassPortalCreateEvent(object);
        }
        return null;
    }

    public static PolyClassPortalCreateEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPortalCreateEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
