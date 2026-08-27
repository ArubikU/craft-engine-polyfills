/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassSpecTypedNumArgType {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"SpecTypedNumArgType", (String)"scale", (String)"D:D");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"SpecTypedNumArgType", (String)"scale");
    }

    public double tm$0_scale(double d) {
        if (h$0 != null) {
            return (Double)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"SpecTypedNumArgType", (String)"scale", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asNum();
    }

    public ScriptValue um$1_scale(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"SpecTypedNumArgType", (String)"scale", (Object)this.instance, (List)list);
    }

    public PolyClassSpecTypedNumArgType(Object object) {
        this.instance = object;
    }

    public static PolyClassSpecTypedNumArgType of(Object object) {
        return new PolyClassSpecTypedNumArgType(object);
    }
}
