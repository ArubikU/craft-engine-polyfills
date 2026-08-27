/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassLivingEntity;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassMob
extends PolyClassLivingEntity {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.TypedPropertyHandler tp$7;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"Mob", (String)"is_aggressive");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Mob", (String)"is_aggressive", (String)"Z");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"Mob", (String)"can_pickup_loot");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Mob", (String)"can_pickup_loot", (String)"Z");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"Mob", (String)"target_uuid");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Mob", (String)"target_uuid", (String)"S");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Mob", (String)"has_target");
        tp$7 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Mob", (String)"has_target", (String)"Z");
    }

    public ScriptValue pg$0_is_aggressive() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"is_aggressive", (Object)this.instance);
    }

    public boolean tg$1_is_aggressive() {
        if (tp$1 != null) {
            return (Boolean)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"is_aggressive", (Object)this.instance).asBool();
    }

    public ScriptValue pg$2_can_pickup_loot() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"can_pickup_loot", (Object)this.instance);
    }

    public boolean tg$3_can_pickup_loot() {
        if (tp$3 != null) {
            return (Boolean)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"can_pickup_loot", (Object)this.instance).asBool();
    }

    public ScriptValue pg$4_target_uuid() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"target_uuid", (Object)this.instance);
    }

    public String tg$5_target_uuid() {
        if (tp$5 != null) {
            return (String)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"target_uuid", (Object)this.instance).asStr();
    }

    public ScriptValue pg$6_has_target() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"has_target", (Object)this.instance);
    }

    public boolean tg$7_has_target() {
        if (tp$7 != null) {
            return (Boolean)tp$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Mob", (String)"has_target", (Object)this.instance).asBool();
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
