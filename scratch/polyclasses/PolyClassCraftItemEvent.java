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
import dev.arubik.craftengine.script.PolyClassInventoryClickEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassCraftItemEvent
extends PolyClassInventoryClickEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"CraftItemEvent", (String)"recipe_result");
    }

    public ScriptValue pg$0_recipe_result() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"CraftItemEvent", (String)"recipe_result", (Object)this.instance);
    }

    public PolyClassCraftItemEvent(Object object) {
        super(object);
    }

    public static PolyClassCraftItemEvent of(Object object) {
        return new PolyClassCraftItemEvent(object);
    }

    public static PolyClassCraftItemEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("CraftItemEvent")) {
            return new PolyClassCraftItemEvent(object);
        }
        return null;
    }
}
