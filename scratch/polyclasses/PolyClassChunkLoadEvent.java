/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassChunkLoadEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.TypedPropertyHandler tp$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.TypedPropertyHandler tp$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.TypedPropertyHandler tp$6;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkLoadEvent", (String)"world");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkLoadEvent", (String)"chunk_x");
        tp$2 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ChunkLoadEvent", (String)"chunk_x", (String)"D");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkLoadEvent", (String)"is_new");
        tp$4 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ChunkLoadEvent", (String)"is_new", (String)"Z");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkLoadEvent", (String)"chunk_z");
        tp$6 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ChunkLoadEvent", (String)"chunk_z", (String)"D");
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

    public double tg$2_chunk_x() {
        if (tp$2 != null) {
            return (Double)tp$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkLoadEvent", (String)"chunk_x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$3_is_new() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkLoadEvent", (String)"is_new", (Object)this.instance);
    }

    public boolean tg$4_is_new() {
        if (tp$4 != null) {
            return (Boolean)tp$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkLoadEvent", (String)"is_new", (Object)this.instance).asBool();
    }

    public ScriptValue pg$5_chunk_z() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkLoadEvent", (String)"chunk_z", (Object)this.instance);
    }

    public double tg$6_chunk_z() {
        if (tp$6 != null) {
            return (Double)tp$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkLoadEvent", (String)"chunk_z", (Object)this.instance).asNum();
    }

    public PolyClassChunkLoadEvent(Object object) {
        super(object);
    }

    public static PolyClassChunkLoadEvent of(Object object) {
        return new PolyClassChunkLoadEvent(object);
    }

    public static PolyClassChunkLoadEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ChunkLoadEvent")) {
            return new PolyClassChunkLoadEvent(object);
        }
        return null;
    }
}
