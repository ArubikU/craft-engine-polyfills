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

public class PolyClassEntityPotionEffectEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"EntityPotionEffectEvent", (String)"cause");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"EntityPotionEffectEvent", (String)"action");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"EntityPotionEffectEvent", (String)"entity");
    }

    public ScriptValue pg$0_cause() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityPotionEffectEvent", (String)"cause", (Object)this.instance);
    }

    public ScriptValue pg$1_action() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityPotionEffectEvent", (String)"action", (Object)this.instance);
    }

    public ScriptValue pg$2_entity() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityPotionEffectEvent", (String)"entity", (Object)this.instance);
    }

    public PolyClassEntityPotionEffectEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityPotionEffectEvent of(Object object) {
        return new PolyClassEntityPotionEffectEvent(object);
    }
}
