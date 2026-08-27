/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassSpecPropRefreshType {
    protected final Object instance;
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"SpecPropRefreshType", (String)"value");
    }

    public ScriptValue pg$0_value() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SpecPropRefreshType", (String)"value", (Object)this.instance);
    }

    public PolyClassSpecPropRefreshType(Object object) {
        this.instance = object;
    }

    public static PolyClassSpecPropRefreshType of(Object object) {
        return new PolyClassSpecPropRefreshType(object);
    }
}
