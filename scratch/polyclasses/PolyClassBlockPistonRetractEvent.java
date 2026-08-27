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

public class PolyClassBlockPistonRetractEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPistonRetractEvent", (String)"block");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPistonRetractEvent", (String)"direction");
    }

    public ScriptValue pg$0_block() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPistonRetractEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$1_direction() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPistonRetractEvent", (String)"direction", (Object)this.instance);
    }

    public PolyClassBlockPistonRetractEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockPistonRetractEvent of(Object object) {
        return new PolyClassBlockPistonRetractEvent(object);
    }
}
