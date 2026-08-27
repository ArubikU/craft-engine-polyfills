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

public class PolyClassBlockRedstoneEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"BlockRedstoneEvent", (String)"set_new_current", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"BlockRedstoneEvent", (String)"set_new_current");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BlockRedstoneEvent", (String)"new_current");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"BlockRedstoneEvent", (String)"block");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"BlockRedstoneEvent", (String)"old_current");
    }

    public boolean tm$0_set_new_current(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"BlockRedstoneEvent", (String)"set_new_current", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set_new_current(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BlockRedstoneEvent", (String)"set_new_current", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_new_current() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockRedstoneEvent", (String)"new_current", (Object)this.instance);
    }

    public ScriptValue pg$3_block() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockRedstoneEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$4_old_current() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockRedstoneEvent", (String)"old_current", (Object)this.instance);
    }

    public PolyClassBlockRedstoneEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockRedstoneEvent of(Object object) {
        return new PolyClassBlockRedstoneEvent(object);
    }
}
