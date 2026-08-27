/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEntityDamageEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassEntityDamageByEntityEvent
extends PolyClassEntityDamageEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"EntityDamageByEntityEvent", (String)"damager");
    }

    public ScriptValue pg$0_damager() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityDamageByEntityEvent", (String)"damager", (Object)this.instance);
    }

    public PolyClassEntityDamageByEntityEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityDamageByEntityEvent of(Object object) {
        return new PolyClassEntityDamageByEntityEvent(object);
    }
}
