/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassPortalCreateEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PortalCreateEvent", (String)"reason");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"PortalCreateEvent", (String)"world");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PortalCreateEvent", (String)"blocks");
    }

    public ScriptValue pg$0_reason() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PortalCreateEvent", (String)"reason", (Object)this.instance);
    }

    public ScriptValue pg$1_world() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PortalCreateEvent", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$2_blocks() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PortalCreateEvent", (String)"blocks", (Object)this.instance);
    }

    public PolyClassPortalCreateEvent(Object object) {
        super(object);
    }

    public static PolyClassPortalCreateEvent of(Object object) {
        return new PolyClassPortalCreateEvent(object);
    }
}
