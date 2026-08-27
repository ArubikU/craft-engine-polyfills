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

public class PolyClassStructureGrowEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"StructureGrowEvent", (String)"blocks");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"StructureGrowEvent", (String)"location");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"StructureGrowEvent", (String)"player");
    }

    public ScriptValue pg$0_blocks() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"StructureGrowEvent", (String)"blocks", (Object)this.instance);
    }

    public ScriptValue pg$1_location() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"StructureGrowEvent", (String)"location", (Object)this.instance);
    }

    public ScriptValue pg$2_player() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"StructureGrowEvent", (String)"player", (Object)this.instance);
    }

    public PolyClassStructureGrowEvent(Object object) {
        super(object);
    }

    public static PolyClassStructureGrowEvent of(Object object) {
        return new PolyClassStructureGrowEvent(object);
    }
}
