/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassBlockMetadata;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassBeehiveMetadata
extends PolyClassBlockMetadata {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BeehiveMetadata", (String)"bee_count");
    }

    public ScriptValue pg$0_bee_count() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BeehiveMetadata", (String)"bee_count", (Object)this.instance);
    }

    public PolyClassBeehiveMetadata(Object object) {
        super(object);
    }

    public static PolyClassBeehiveMetadata of(Object object) {
        return new PolyClassBeehiveMetadata(object);
    }
}
