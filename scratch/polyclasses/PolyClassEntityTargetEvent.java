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

public class PolyClassEntityTargetEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"EntityTargetEvent", (String)"set_target", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"EntityTargetEvent", (String)"set_target");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"EntityTargetEvent", (String)"reason");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"EntityTargetEvent", (String)"target");
    }

    public boolean tm$0_set_target(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"EntityTargetEvent", (String)"set_target", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_target(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"EntityTargetEvent", (String)"set_target", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_reason() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityTargetEvent", (String)"reason", (Object)this.instance);
    }

    public ScriptValue pg$3_target() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityTargetEvent", (String)"target", (Object)this.instance);
    }

    public PolyClassEntityTargetEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityTargetEvent of(Object object) {
        return new PolyClassEntityTargetEvent(object);
    }
}
