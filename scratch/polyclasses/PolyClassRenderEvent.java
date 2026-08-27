/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassRenderEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"RenderEvent", (String)"holder");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"RenderEvent", (String)"slot");
    }

    public ScriptValue pg$0_holder() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"RenderEvent", (String)"holder", (Object)this.instance);
    }

    public ScriptValue pg$1_slot() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"RenderEvent", (String)"slot", (Object)this.instance);
    }

    public PolyClassRenderEvent(Object object) {
        super(object);
    }

    public static PolyClassRenderEvent of(Object object) {
        return new PolyClassRenderEvent(object);
    }

    public static PolyClassRenderEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("RenderEvent")) {
            return new PolyClassRenderEvent(object);
        }
        return null;
    }
}
