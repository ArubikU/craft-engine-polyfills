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

public final class PC_SpecTypedMismatchType_5 {
    private final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"SpecTypedMismatchType", (String)"greet", (String)"S:S");
    }

    public String tm$0_greet(String string) {
        if (h$0 != null) {
            return (String)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"SpecTypedMismatchType", (String)"greet", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asStr();
    }

    public PC_SpecTypedMismatchType_5(Object object) {
        this.instance = object;
    }

    public static PC_SpecTypedMismatchType_5 of(Object object) {
        return new PC_SpecTypedMismatchType_5(object);
    }
}
