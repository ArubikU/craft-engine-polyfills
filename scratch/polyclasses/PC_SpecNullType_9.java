/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public final class PC_SpecNullType_9 {
    private final Object instance;
    private static volatile PolyType.MethodHandler m$0;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"SpecNullType", (String)"greet");
    }

    public ScriptValue um$0_greet(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"SpecNullType", (String)"greet", (Object)this.instance, (List)list);
    }

    public PC_SpecNullType_9(Object object) {
        this.instance = object;
    }

    public static PC_SpecNullType_9 of(Object object) {
        return new PC_SpecNullType_9(object);
    }
}
