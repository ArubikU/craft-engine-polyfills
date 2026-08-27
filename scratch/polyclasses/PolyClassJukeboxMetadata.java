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

public class PolyClassJukeboxMetadata
extends PolyClassBlockMetadata {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"JukeboxMetadata", (String)"record");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"JukeboxMetadata", (String)"is_playing");
    }

    public ScriptValue pg$0_record() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"JukeboxMetadata", (String)"record", (Object)this.instance);
    }

    public ScriptValue pg$1_is_playing() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"JukeboxMetadata", (String)"is_playing", (Object)this.instance);
    }

    public PolyClassJukeboxMetadata(Object object) {
        super(object);
    }

    public static PolyClassJukeboxMetadata of(Object object) {
        return new PolyClassJukeboxMetadata(object);
    }
}
