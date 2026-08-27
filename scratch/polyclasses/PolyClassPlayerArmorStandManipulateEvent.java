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

public class PolyClassPlayerArmorStandManipulateEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerArmorStandManipulateEvent", (String)"player_item");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerArmorStandManipulateEvent", (String)"armor_stand");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerArmorStandManipulateEvent", (String)"armor_stand_item");
    }

    public ScriptValue pg$0_player_item() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerArmorStandManipulateEvent", (String)"player_item", (Object)this.instance);
    }

    public ScriptValue pg$1_armor_stand() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerArmorStandManipulateEvent", (String)"armor_stand", (Object)this.instance);
    }

    public ScriptValue pg$2_armor_stand_item() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerArmorStandManipulateEvent", (String)"armor_stand_item", (Object)this.instance);
    }

    public PolyClassPlayerArmorStandManipulateEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerArmorStandManipulateEvent of(Object object) {
        return new PolyClassPlayerArmorStandManipulateEvent(object);
    }

    public static PolyClassPlayerArmorStandManipulateEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerArmorStandManipulateEvent")) {
            return new PolyClassPlayerArmorStandManipulateEvent(object);
        }
        return null;
    }
}
