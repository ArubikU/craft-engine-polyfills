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

public class PolyClassPlayerRiptideEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerRiptideEvent", (String)"item");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerRiptideEvent", (String)"velocity");
    }

    public ScriptValue pg$0_item() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerRiptideEvent", (String)"item", (Object)this.instance);
    }

    public ScriptValue pg$1_velocity() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerRiptideEvent", (String)"velocity", (Object)this.instance);
    }

    public PolyClassPlayerRiptideEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerRiptideEvent of(Object object) {
        return new PolyClassPlayerRiptideEvent(object);
    }
}
