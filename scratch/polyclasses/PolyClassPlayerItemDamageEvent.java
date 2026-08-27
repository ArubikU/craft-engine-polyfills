/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassPlayerItemDamageEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerItemDamageEvent", (String)"set_damage", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerItemDamageEvent", (String)"set_damage");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerItemDamageEvent", (String)"damage");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerItemDamageEvent", (String)"item");
    }

    public boolean tm$0_set_damage(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"PlayerItemDamageEvent", (String)"set_damage", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set_damage(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerItemDamageEvent", (String)"set_damage", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_damage() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerItemDamageEvent", (String)"damage", (Object)this.instance);
    }

    public ScriptValue pg$3_item() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerItemDamageEvent", (String)"item", (Object)this.instance);
    }

    public PolyClassPlayerItemDamageEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerItemDamageEvent of(Object object) {
        return new PolyClassPlayerItemDamageEvent(object);
    }
}
