/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public final class PC_SpecTypedHappyType_4 {
    private final Object instance;
    private static volatile PolyType.TypedMethodHandler3 h$0;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"SpecTypedHappyType", (String)"combine", (String)"DSZ:D");
    }

    public double tm$0_combine(double d, String string, boolean bl) {
        if (h$0 != null) {
            return (Double)h$0.call(this.instance, (Object)d, (Object)string, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"SpecTypedHappyType", (String)"combine", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((String)string), ScriptValue.of((boolean)bl)}).asNum();
    }

    public PC_SpecTypedHappyType_4(Object object) {
        this.instance = object;
    }

    public static PC_SpecTypedHappyType_4 of(Object object) {
        return new PC_SpecTypedHappyType_4(object);
    }
}
