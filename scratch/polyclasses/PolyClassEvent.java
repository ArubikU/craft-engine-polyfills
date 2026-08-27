/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassEvent {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler0 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Event", (String)"cancel", (String)":Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Event", (String)"cancel");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Event", (String)"set_cancelled", (String)"Z:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Event", (String)"set_cancelled");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"Event", (String)"cancelled");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"Event", (String)"type");
    }

    public boolean tm$0_cancel() {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Event", (String)"cancel", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$1_cancel(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Event", (String)"cancel", (Object)this.instance, (List)list);
    }

    public boolean tm$2_set_cancelled(boolean bl) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"Event", (String)"set_cancelled", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$3_set_cancelled(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Event", (String)"set_cancelled", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$4_cancelled() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Event", (String)"cancelled", (Object)this.instance);
    }

    public ScriptValue pg$5_type() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Event", (String)"type", (Object)this.instance);
    }

    public PolyClassEvent(Object object) {
        this.instance = object;
    }

    public static PolyClassEvent of(Object object) {
        return new PolyClassEvent(object);
    }
}
