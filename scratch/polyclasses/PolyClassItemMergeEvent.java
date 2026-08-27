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

public class PolyClassItemMergeEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"ItemMergeEvent", (String)"entity");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"ItemMergeEvent", (String)"target");
    }

    public ScriptValue pg$0_entity() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemMergeEvent", (String)"entity", (Object)this.instance);
    }

    public ScriptValue pg$1_target() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemMergeEvent", (String)"target", (Object)this.instance);
    }

    public PolyClassItemMergeEvent(Object object) {
        super(object);
    }

    public static PolyClassItemMergeEvent of(Object object) {
        return new PolyClassItemMergeEvent(object);
    }

    public static PolyClassItemMergeEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ItemMergeEvent")) {
            return new PolyClassItemMergeEvent(object);
        }
        return null;
    }
}
