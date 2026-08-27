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
import dev.arubik.craftengine.script.PolyClassEntity_v2;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassProjectile
extends PolyClassEntity_v2 {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"Projectile", (String)"owner_uuid");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Projectile", (String)"owner_uuid", (String)"S");
    }

    public ScriptValue pg$0_owner_uuid() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Projectile", (String)"owner_uuid", (Object)this.instance);
    }

    public String tg$1_owner_uuid() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Projectile", (String)"owner_uuid", (Object)this.instance).asStr();
    }

    public PolyClassProjectile(Object object) {
        super(object);
    }

    public static PolyClassProjectile of(Object object) {
        return new PolyClassProjectile(object);
    }

    public static PolyClassProjectile ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Projectile")) {
            return new PolyClassProjectile(object);
        }
        return null;
    }
}
