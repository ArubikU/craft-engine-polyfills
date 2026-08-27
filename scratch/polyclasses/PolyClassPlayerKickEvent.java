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

public class PolyClassPlayerKickEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerKickEvent", (String)"set_reason", (String)"S:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerKickEvent", (String)"set_reason");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerKickEvent", (String)"reason");
    }

    public boolean tm$0_set_reason(String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"PlayerKickEvent", (String)"set_reason", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_set_reason(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerKickEvent", (String)"set_reason", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_reason() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerKickEvent", (String)"reason", (Object)this.instance);
    }

    public PolyClassPlayerKickEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerKickEvent of(Object object) {
        return new PolyClassPlayerKickEvent(object);
    }
}
