/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassSpecShapeType {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"SpecShapeType", (String)"thing", (String)"S:S");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"SpecShapeType", (String)"thing");
    }

    public String tm$0_thing(String string) {
        if (h$0 != null) {
            return (String)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"SpecShapeType", (String)"thing", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asStr();
    }

    public ScriptValue um$1_thing(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"SpecShapeType", (String)"thing", (Object)this.instance, (List)list);
    }

    public PolyClassSpecShapeType(Object object) {
        this.instance = object;
    }

    public static PolyClassSpecShapeType of(Object object) {
        return new PolyClassSpecShapeType(object);
    }
}
