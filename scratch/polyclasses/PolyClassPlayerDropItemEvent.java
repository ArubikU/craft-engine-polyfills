/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
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

public class PolyClassPlayerDropItemEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerDropItemEvent", (String)"item_drop");
    }

    public ScriptValue pg$0_item_drop() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerDropItemEvent", (String)"item_drop", (Object)this.instance);
    }

    public PolyClassPlayerDropItemEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerDropItemEvent of(Object object) {
        return new PolyClassPlayerDropItemEvent(object);
    }

    public static PolyClassPlayerDropItemEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerDropItemEvent")) {
            return new PolyClassPlayerDropItemEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerDropItemEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerDropItemEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
