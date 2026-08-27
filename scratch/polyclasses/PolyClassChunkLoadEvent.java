/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassChunkLoadEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkLoadEvent", (String)"world");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkLoadEvent", (String)"chunk_x");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkLoadEvent", (String)"is_new");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkLoadEvent", (String)"chunk_z");
    }

    public ScriptValue pg$0_world() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkLoadEvent", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$1_chunk_x() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkLoadEvent", (String)"chunk_x", (Object)this.instance);
    }

    public ScriptValue pg$2_is_new() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkLoadEvent", (String)"is_new", (Object)this.instance);
    }

    public ScriptValue pg$3_chunk_z() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkLoadEvent", (String)"chunk_z", (Object)this.instance);
    }

    public PolyClassChunkLoadEvent(Object object) {
        super(object);
    }

    public static PolyClassChunkLoadEvent of(Object object) {
        return new PolyClassChunkLoadEvent(object);
    }
}
