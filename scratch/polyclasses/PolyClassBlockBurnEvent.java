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

public class PolyClassBlockBurnEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BlockBurnEvent", (String)"block");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"BlockBurnEvent", (String)"ignition_source");
    }

    public ScriptValue pg$0_block() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockBurnEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$1_ignition_source() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockBurnEvent", (String)"ignition_source", (Object)this.instance);
    }

    public PolyClassBlockBurnEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockBurnEvent of(Object object) {
        return new PolyClassBlockBurnEvent(object);
    }
}
