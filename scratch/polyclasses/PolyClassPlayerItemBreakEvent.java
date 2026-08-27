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

public class PolyClassPlayerItemBreakEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerItemBreakEvent", (String)"broken_item");
    }

    public ScriptValue pg$0_broken_item() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerItemBreakEvent", (String)"broken_item", (Object)this.instance);
    }

    public PolyClassPlayerItemBreakEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerItemBreakEvent of(Object object) {
        return new PolyClassPlayerItemBreakEvent(object);
    }
}
