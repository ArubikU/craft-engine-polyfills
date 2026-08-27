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

public class PolyClassDispatchPropType {
    protected final Object instance;
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"DispatchPropType", (String)"v");
    }

    public ScriptValue pg$0_v() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"DispatchPropType", (String)"v", (Object)this.instance);
    }

    public PolyClassDispatchPropType(Object object) {
        this.instance = object;
    }

    public static PolyClassDispatchPropType of(Object object) {
        return new PolyClassDispatchPropType(object);
    }
}
