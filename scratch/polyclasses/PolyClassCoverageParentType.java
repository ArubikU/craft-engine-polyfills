/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassCoverageParentType {
    protected final Object instance;
    private static volatile PolyType.MethodHandler m$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"CoverageParentType", (String)"inherited_method");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"CoverageParentType", (String)"inherited_prop");
    }

    public ScriptValue um$0_inherited_method(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"CoverageParentType", (String)"inherited_method", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$1_inherited_prop() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"CoverageParentType", (String)"inherited_prop", (Object)this.instance);
    }

    public PolyClassCoverageParentType(Object object) {
        this.instance = object;
    }

    public static PolyClassCoverageParentType of(Object object) {
        return new PolyClassCoverageParentType(object);
    }
}
