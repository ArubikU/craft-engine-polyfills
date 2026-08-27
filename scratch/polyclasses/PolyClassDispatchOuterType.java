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

public class PolyClassDispatchOuterType {
    protected final Object instance;
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"DispatchOuterType", (String)"inner");
    }

    public ScriptValue pg$0_inner() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"DispatchOuterType", (String)"inner", (Object)this.instance);
    }

    public PolyClassDispatchOuterType(Object object) {
        this.instance = object;
    }

    public static PolyClassDispatchOuterType of(Object object) {
        return new PolyClassDispatchOuterType(object);
    }
}
