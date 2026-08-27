/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassBlock {
    protected final Object instance;
    private static volatile PolyType.MethodHandler m$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"property");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"id");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_air");
    }

    public ScriptValue um$0_property(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"property", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$1_id() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"id", (Object)this.instance);
    }

    public ScriptValue pg$2_is_air() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_air", (Object)this.instance);
    }

    public PolyClassBlock(Object object) {
        this.instance = object;
    }

    public static PolyClassBlock of(Object object) {
        return new PolyClassBlock(object);
    }
}
