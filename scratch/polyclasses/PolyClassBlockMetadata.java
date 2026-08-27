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

public class PolyClassBlockMetadata {
    protected final Object instance;
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BlockMetadata", (String)"custom_name");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"BlockMetadata", (String)"pos");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BlockMetadata", (String)"type");
    }

    public ScriptValue pg$0_custom_name() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockMetadata", (String)"custom_name", (Object)this.instance);
    }

    public ScriptValue pg$1_pos() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockMetadata", (String)"pos", (Object)this.instance);
    }

    public ScriptValue pg$2_type() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockMetadata", (String)"type", (Object)this.instance);
    }

    public PolyClassBlockMetadata(Object object) {
        this.instance = object;
    }

    public static PolyClassBlockMetadata of(Object object) {
        return new PolyClassBlockMetadata(object);
    }
}
