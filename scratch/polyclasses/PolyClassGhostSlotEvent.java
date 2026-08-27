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

public class PolyClassGhostSlotEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"GhostSlotEvent", (String)"click_type");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"GhostSlotEvent", (String)"slot");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"GhostSlotEvent", (String)"clicked_id");
    }

    public ScriptValue pg$0_click_type() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GhostSlotEvent", (String)"click_type", (Object)this.instance);
    }

    public ScriptValue pg$1_slot() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GhostSlotEvent", (String)"slot", (Object)this.instance);
    }

    public ScriptValue pg$2_clicked_id() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GhostSlotEvent", (String)"clicked_id", (Object)this.instance);
    }

    public PolyClassGhostSlotEvent(Object object) {
        super(object);
    }

    public static PolyClassGhostSlotEvent of(Object object) {
        return new PolyClassGhostSlotEvent(object);
    }
}
