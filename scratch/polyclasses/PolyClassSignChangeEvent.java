/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassSignChangeEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler2 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"SignChangeEvent", (String)"get_line", (String)"D:S");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"SignChangeEvent", (String)"get_line");
        h$2 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"SignChangeEvent", (String)"set_line", (String)"DS:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"SignChangeEvent", (String)"set_line");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"SignChangeEvent", (String)"block");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"SignChangeEvent", (String)"player");
    }

    public String tm$0_get_line(double d) {
        if (h$0 != null) {
            return (String)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"SignChangeEvent", (String)"get_line", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asStr();
    }

    public ScriptValue um$1_get_line(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"SignChangeEvent", (String)"get_line", (Object)this.instance, (List)list);
    }

    public boolean tm$2_set_line(double d, String string) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)d, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"SignChangeEvent", (String)"set_line", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$3_set_line(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"SignChangeEvent", (String)"set_line", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$4_block() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignChangeEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$5_player() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignChangeEvent", (String)"player", (Object)this.instance);
    }

    public PolyClassSignChangeEvent(Object object) {
        super(object);
    }

    public static PolyClassSignChangeEvent of(Object object) {
        return new PolyClassSignChangeEvent(object);
    }
}
