/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassSpecArityType {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler2 h$0;
    private static volatile PolyType.MethodHandler m$1;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"SpecArityType", (String)"needs_two", (String)"DD:D");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"SpecArityType", (String)"needs_two");
    }

    public double tm$0_needs_two(double d, double d2) {
        if (h$0 != null) {
            return (Double)h$0.call(this.instance, (Object)d, (Object)d2);
        }
        return PolyClassRuntime.genericCall((String)"SpecArityType", (String)"needs_two", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2)}).asNum();
    }

    public ScriptValue um$1_needs_two(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"SpecArityType", (String)"needs_two", (Object)this.instance, (List)list);
    }

    public PolyClassSpecArityType(Object object) {
        this.instance = object;
    }

    public static PolyClassSpecArityType of(Object object) {
        return new PolyClassSpecArityType(object);
    }
}
