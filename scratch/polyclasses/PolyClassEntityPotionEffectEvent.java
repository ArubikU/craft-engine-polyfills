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
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassEntityPotionEffectEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"EntityPotionEffectEvent", (String)"cause");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"EntityPotionEffectEvent", (String)"cause", (String)"S");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"EntityPotionEffectEvent", (String)"action");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"EntityPotionEffectEvent", (String)"action", (String)"S");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"EntityPotionEffectEvent", (String)"entity");
    }

    public ScriptValue pg$0_cause() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityPotionEffectEvent", (String)"cause", (Object)this.instance);
    }

    public String tg$1_cause() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityPotionEffectEvent", (String)"cause", (Object)this.instance).asStr();
    }

    public ScriptValue pg$2_action() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityPotionEffectEvent", (String)"action", (Object)this.instance);
    }

    public String tg$3_action() {
        if (tp$3 != null) {
            return (String)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityPotionEffectEvent", (String)"action", (Object)this.instance).asStr();
    }

    public ScriptValue pg$4_entity() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityPotionEffectEvent", (String)"entity", (Object)this.instance);
    }

    public PolyClassEntityPotionEffectEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityPotionEffectEvent of(Object object) {
        return new PolyClassEntityPotionEffectEvent(object);
    }

    public static PolyClassEntityPotionEffectEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("EntityPotionEffectEvent")) {
            return new PolyClassEntityPotionEffectEvent(object);
        }
        return null;
    }
}
