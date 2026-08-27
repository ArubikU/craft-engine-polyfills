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

public class PolyClassPlayerAdvancementDoneEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerAdvancementDoneEvent", (String)"advancement_key");
    }

    public ScriptValue pg$0_advancement_key() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerAdvancementDoneEvent", (String)"advancement_key", (Object)this.instance);
    }

    public PolyClassPlayerAdvancementDoneEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerAdvancementDoneEvent of(Object object) {
        return new PolyClassPlayerAdvancementDoneEvent(object);
    }
}
