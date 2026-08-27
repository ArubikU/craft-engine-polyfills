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

public final class PC_SpecHappyPropType_14 {
    private final Object instance;
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"SpecHappyPropType", (String)"value");
    }

    public ScriptValue pg$0_value() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SpecHappyPropType", (String)"value", (Object)this.instance);
    }

    public PC_SpecHappyPropType_14(Object object) {
        this.instance = object;
    }

    public static PC_SpecHappyPropType_14 of(Object object) {
        return new PC_SpecHappyPropType_14(object);
    }
}
