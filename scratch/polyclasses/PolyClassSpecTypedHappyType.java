/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassSpecTypedHappyType {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler3 h$0;
    private static volatile PolyType.MethodHandler m$1;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"SpecTypedHappyType", (String)"combine", (String)"DSZ:D");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"SpecTypedHappyType", (String)"combine");
    }

    public double tm$0_combine(double d, String string, boolean bl) {
        if (h$0 != null) {
            return (Double)h$0.call(this.instance, (Object)d, (Object)string, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"SpecTypedHappyType", (String)"combine", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((String)string), ScriptValue.of((boolean)bl)}).asNum();
    }

    public ScriptValue um$1_combine(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"SpecTypedHappyType", (String)"combine", (Object)this.instance, (List)list);
    }

    public PolyClassSpecTypedHappyType(Object object) {
        this.instance = object;
    }

    public static PolyClassSpecTypedHappyType of(Object object) {
        return new PolyClassSpecTypedHappyType(object);
    }
}
