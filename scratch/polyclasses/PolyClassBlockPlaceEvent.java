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

public class PolyClassBlockPlaceEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPlaceEvent", (String)"can_build");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPlaceEvent", (String)"block_placed_against");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPlaceEvent", (String)"block");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPlaceEvent", (String)"player");
    }

    public ScriptValue pg$0_can_build() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPlaceEvent", (String)"can_build", (Object)this.instance);
    }

    public ScriptValue pg$1_block_placed_against() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPlaceEvent", (String)"block_placed_against", (Object)this.instance);
    }

    public ScriptValue pg$2_block() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPlaceEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$3_player() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPlaceEvent", (String)"player", (Object)this.instance);
    }

    public PolyClassBlockPlaceEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockPlaceEvent of(Object object) {
        return new PolyClassBlockPlaceEvent(object);
    }
}
