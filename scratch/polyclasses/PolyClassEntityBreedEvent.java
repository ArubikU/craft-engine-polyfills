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

public class PolyClassEntityBreedEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"EntityBreedEvent", (String)"mother");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"EntityBreedEvent", (String)"father");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"EntityBreedEvent", (String)"breeder");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"EntityBreedEvent", (String)"child");
    }

    public ScriptValue pg$0_mother() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityBreedEvent", (String)"mother", (Object)this.instance);
    }

    public ScriptValue pg$1_father() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityBreedEvent", (String)"father", (Object)this.instance);
    }

    public ScriptValue pg$2_breeder() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityBreedEvent", (String)"breeder", (Object)this.instance);
    }

    public ScriptValue pg$3_child() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityBreedEvent", (String)"child", (Object)this.instance);
    }

    public PolyClassEntityBreedEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityBreedEvent of(Object object) {
        return new PolyClassEntityBreedEvent(object);
    }
}
