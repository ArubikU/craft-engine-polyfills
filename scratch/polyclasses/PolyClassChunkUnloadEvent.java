/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassChunkUnloadEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.TypedPropertyHandler tp$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.TypedPropertyHandler tp$4;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkUnloadEvent", (String)"world");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkUnloadEvent", (String)"chunk_x");
        tp$2 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ChunkUnloadEvent", (String)"chunk_x", (String)"D");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"ChunkUnloadEvent", (String)"chunk_z");
        tp$4 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ChunkUnloadEvent", (String)"chunk_z", (String)"D");
    }

    public ScriptValue pg$0_world() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkUnloadEvent", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$1_chunk_x() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkUnloadEvent", (String)"chunk_x", (Object)this.instance);
    }

    public double tg$2_chunk_x() {
        if (tp$2 != null) {
            return (Double)tp$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkUnloadEvent", (String)"chunk_x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$3_chunk_z() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkUnloadEvent", (String)"chunk_z", (Object)this.instance);
    }

    public double tg$4_chunk_z() {
        if (tp$4 != null) {
            return (Double)tp$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ChunkUnloadEvent", (String)"chunk_z", (Object)this.instance).asNum();
    }

    public PolyClassChunkUnloadEvent(Object object) {
        super(object);
    }

    public static PolyClassChunkUnloadEvent of(Object object) {
        return new PolyClassChunkUnloadEvent(object);
    }

    public static PolyClassChunkUnloadEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ChunkUnloadEvent")) {
            return new PolyClassChunkUnloadEvent(object);
        }
        return null;
    }

    public static PolyClassChunkUnloadEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassChunkUnloadEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
