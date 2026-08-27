/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public final class PC_SpecArityType_16 {
    private final Object instance;
    private static volatile PolyType.TypedMethodHandler2 h$0;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"SpecArityType", (String)"needs_two", (String)"DD:D");
    }

    public double tm$0_needs_two(double d, double d2) {
        if (h$0 != null) {
            return (Double)h$0.call(this.instance, (Object)d, (Object)d2);
        }
        return PolyClassRuntime.genericCall((String)"SpecArityType", (String)"needs_two", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2)}).asNum();
    }

    public PC_SpecArityType_16(Object object) {
        this.instance = object;
    }

    public static PC_SpecArityType_16 of(Object object) {
        return new PC_SpecArityType_16(object);
    }
}
