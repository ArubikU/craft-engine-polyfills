/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassEntityTeleportEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"EntityTeleportEvent", (String)"set_to", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"EntityTeleportEvent", (String)"set_to");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"EntityTeleportEvent", (String)"from");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"EntityTeleportEvent", (String)"to");
    }

    public boolean tm$0_set_to(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"EntityTeleportEvent", (String)"set_to", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_to(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"EntityTeleportEvent", (String)"set_to", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_from() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityTeleportEvent", (String)"from", (Object)this.instance);
    }

    public ScriptValue pg$3_to() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityTeleportEvent", (String)"to", (Object)this.instance);
    }

    public PolyClassEntityTeleportEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityTeleportEvent of(Object object) {
        return new PolyClassEntityTeleportEvent(object);
    }
}
