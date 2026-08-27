/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public final class PC_SpecTypedNumArgType_6 {
    private final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"SpecTypedNumArgType", (String)"scale", (String)"D:D");
    }

    public double tm$0_scale(double d) {
        if (h$0 != null) {
            return (Double)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"SpecTypedNumArgType", (String)"scale", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asNum();
    }

    public PC_SpecTypedNumArgType_6(Object object) {
        this.instance = object;
    }

    public static PC_SpecTypedNumArgType_6 of(Object object) {
        return new PC_SpecTypedNumArgType_6(object);
    }
}
