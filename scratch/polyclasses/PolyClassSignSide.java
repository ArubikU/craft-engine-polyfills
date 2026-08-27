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

public class PolyClassSignSide {
    protected final Object instance;
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"SignSide", (String)"color");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"SignSide", (String)"glowing");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"SignSide", (String)"lines");
    }

    public ScriptValue pg$0_color() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignSide", (String)"color", (Object)this.instance);
    }

    public ScriptValue pg$1_glowing() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignSide", (String)"glowing", (Object)this.instance);
    }

    public ScriptValue pg$2_lines() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignSide", (String)"lines", (Object)this.instance);
    }

    public PolyClassSignSide(Object object) {
        this.instance = object;
    }

    public static PolyClassSignSide of(Object object) {
        return new PolyClassSignSide(object);
    }
}
