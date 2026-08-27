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

public class PolyClassBlockExplodeEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"BlockExplodeEvent", (String)"set_yield", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"BlockExplodeEvent", (String)"set_yield");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BlockExplodeEvent", (String)"yield");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"BlockExplodeEvent", (String)"block");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"BlockExplodeEvent", (String)"block_list");
    }

    public boolean tm$0_set_yield(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"BlockExplodeEvent", (String)"set_yield", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set_yield(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BlockExplodeEvent", (String)"set_yield", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_yield() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockExplodeEvent", (String)"yield", (Object)this.instance);
    }

    public ScriptValue pg$3_block() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockExplodeEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$4_block_list() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockExplodeEvent", (String)"block_list", (Object)this.instance);
    }

    public PolyClassBlockExplodeEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockExplodeEvent of(Object object) {
        return new PolyClassBlockExplodeEvent(object);
    }
}
