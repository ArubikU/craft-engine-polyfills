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

public class PolyClassPlayerFishEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerFishEvent", (String)"set_exp", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerFishEvent", (String)"set_exp");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerFishEvent", (String)"caught");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerFishEvent", (String)"hook");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerFishEvent", (String)"state");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerFishEvent", (String)"exp");
    }

    public boolean tm$0_set_exp(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"PlayerFishEvent", (String)"set_exp", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set_exp(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerFishEvent", (String)"set_exp", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_caught() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerFishEvent", (String)"caught", (Object)this.instance);
    }

    public ScriptValue pg$3_hook() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerFishEvent", (String)"hook", (Object)this.instance);
    }

    public ScriptValue pg$4_state() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerFishEvent", (String)"state", (Object)this.instance);
    }

    public ScriptValue pg$5_exp() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerFishEvent", (String)"exp", (Object)this.instance);
    }

    public PolyClassPlayerFishEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerFishEvent of(Object object) {
        return new PolyClassPlayerFishEvent(object);
    }
}
