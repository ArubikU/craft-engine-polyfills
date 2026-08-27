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

public class PolyClassPlayerShearEntityEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerShearEntityEvent", (String)"item");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerShearEntityEvent", (String)"entity");
    }

    public ScriptValue pg$0_item() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerShearEntityEvent", (String)"item", (Object)this.instance);
    }

    public ScriptValue pg$1_entity() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerShearEntityEvent", (String)"entity", (Object)this.instance);
    }

    public PolyClassPlayerShearEntityEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerShearEntityEvent of(Object object) {
        return new PolyClassPlayerShearEntityEvent(object);
    }

    public static PolyClassPlayerShearEntityEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerShearEntityEvent")) {
            return new PolyClassPlayerShearEntityEvent(object);
        }
        return null;
    }
}
