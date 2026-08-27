/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
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
import java.util.List;

public class PolyClassPlayerTeleportEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerTeleportEvent", (String)"set_to", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerTeleportEvent", (String)"set_to");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerTeleportEvent", (String)"cause");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerTeleportEvent", (String)"cause", (String)"S");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerTeleportEvent", (String)"from");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerTeleportEvent", (String)"to");
    }

    public boolean tm$0_set_to(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"PlayerTeleportEvent", (String)"set_to", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_to(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerTeleportEvent", (String)"set_to", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_cause() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerTeleportEvent", (String)"cause", (Object)this.instance);
    }

    public String tg$3_cause() {
        if (tp$3 != null) {
            return (String)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerTeleportEvent", (String)"cause", (Object)this.instance).asStr();
    }

    public ScriptValue pg$4_from() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerTeleportEvent", (String)"from", (Object)this.instance);
    }

    public ScriptValue pg$5_to() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerTeleportEvent", (String)"to", (Object)this.instance);
    }

    public PolyClassPlayerTeleportEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerTeleportEvent of(Object object) {
        return new PolyClassPlayerTeleportEvent(object);
    }

    public static PolyClassPlayerTeleportEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerTeleportEvent")) {
            return new PolyClassPlayerTeleportEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerTeleportEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerTeleportEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
