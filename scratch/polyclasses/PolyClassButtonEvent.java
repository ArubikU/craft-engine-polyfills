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

public class PolyClassButtonEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"ButtonEvent", (String)"click_type");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"ButtonEvent", (String)"slot");
    }

    public ScriptValue pg$0_click_type() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ButtonEvent", (String)"click_type", (Object)this.instance);
    }

    public ScriptValue pg$1_slot() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ButtonEvent", (String)"slot", (Object)this.instance);
    }

    public PolyClassButtonEvent(Object object) {
        super(object);
    }

    public static PolyClassButtonEvent of(Object object) {
        return new PolyClassButtonEvent(object);
    }

    public static PolyClassButtonEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ButtonEvent")) {
            return new PolyClassButtonEvent(object);
        }
        return null;
    }
}
