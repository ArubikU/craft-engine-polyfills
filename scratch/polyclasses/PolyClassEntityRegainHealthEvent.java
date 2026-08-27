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

public class PolyClassEntityRegainHealthEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"EntityRegainHealthEvent", (String)"set_amount", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"EntityRegainHealthEvent", (String)"set_amount");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"EntityRegainHealthEvent", (String)"reason");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"EntityRegainHealthEvent", (String)"amount");
    }

    public boolean tm$0_set_amount(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"EntityRegainHealthEvent", (String)"set_amount", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set_amount(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"EntityRegainHealthEvent", (String)"set_amount", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_reason() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityRegainHealthEvent", (String)"reason", (Object)this.instance);
    }

    public ScriptValue pg$3_amount() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityRegainHealthEvent", (String)"amount", (Object)this.instance);
    }

    public PolyClassEntityRegainHealthEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityRegainHealthEvent of(Object object) {
        return new PolyClassEntityRegainHealthEvent(object);
    }
}
