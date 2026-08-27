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

public class PolyClassEntityKnockbackEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"EntityKnockbackEvent", (String)"set_knockback", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"EntityKnockbackEvent", (String)"set_knockback");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"EntityKnockbackEvent", (String)"cause");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"EntityKnockbackEvent", (String)"knockback");
    }

    public boolean tm$0_set_knockback(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"EntityKnockbackEvent", (String)"set_knockback", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_knockback(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"EntityKnockbackEvent", (String)"set_knockback", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_cause() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityKnockbackEvent", (String)"cause", (Object)this.instance);
    }

    public ScriptValue pg$3_knockback() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityKnockbackEvent", (String)"knockback", (Object)this.instance);
    }

    public PolyClassEntityKnockbackEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityKnockbackEvent of(Object object) {
        return new PolyClassEntityKnockbackEvent(object);
    }
}
