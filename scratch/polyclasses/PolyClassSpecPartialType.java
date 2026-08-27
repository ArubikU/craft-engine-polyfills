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

public class PolyClassSpecPartialType {
    protected final Object instance;
    private static volatile PolyType.MethodHandler m$0;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"SpecPartialType", (String)"known_method");
    }

    public ScriptValue um$0_known_method(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"SpecPartialType", (String)"known_method", (Object)this.instance, (List)list);
    }

    public PolyClassSpecPartialType(Object object) {
        this.instance = object;
    }

    public static PolyClassSpecPartialType of(Object object) {
        return new PolyClassSpecPartialType(object);
    }
}
