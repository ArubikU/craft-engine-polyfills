/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyClassSpecParentType;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassSpecChildType
extends PolyClassSpecParentType {
    private static volatile PolyType.MethodHandler m$0;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"SpecChildType", (String)"from_child");
    }

    public ScriptValue um$0_from_child(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"SpecChildType", (String)"from_child", (Object)this.instance, (List)list);
    }

    public PolyClassSpecChildType(Object object) {
        super(object);
    }

    public static PolyClassSpecChildType of(Object object) {
        return new PolyClassSpecChildType(object);
    }
}
