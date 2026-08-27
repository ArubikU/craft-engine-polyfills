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
import dev.arubik.craftengine.script.PolyClassLivingEntity_v2;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassMob
extends PolyClassLivingEntity_v2 {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"Mob", (String)"is_aggressive");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"Mob", (String)"can_pickup_loot");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"Mob", (String)"target_uuid");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"Mob", (String)"has_target");
    }

    public ScriptValue pg$0_is_aggressive() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"is_aggressive", (Object)this.instance);
    }

    public ScriptValue pg$1_can_pickup_loot() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"can_pickup_loot", (Object)this.instance);
    }

    public ScriptValue pg$2_target_uuid() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"target_uuid", (Object)this.instance);
    }

    public ScriptValue pg$3_has_target() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"has_target", (Object)this.instance);
    }

    public PolyClassMob(Object object) {
        super(object);
    }

    public static PolyClassMob of(Object object) {
        return new PolyClassMob(object);
    }

    public static PolyClassMob ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Mob")) {
            return new PolyClassMob(object);
        }
        return null;
    }
}
