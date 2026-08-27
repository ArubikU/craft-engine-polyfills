/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
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
import java.util.List;

public class PolyClassEntityDamageEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;
    private static volatile PolyType.PropertyHandler p$6;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"EntityDamageEvent", (String)"set_damage", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"EntityDamageEvent", (String)"set_damage");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"EntityDamageEvent", (String)"damage");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"EntityDamageEvent", (String)"damage", (String)"D");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"EntityDamageEvent", (String)"cause");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"EntityDamageEvent", (String)"cause", (String)"S");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"EntityDamageEvent", (String)"entity");
    }

    public boolean tm$0_set_damage(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"EntityDamageEvent", (String)"set_damage", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set_damage(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"EntityDamageEvent", (String)"set_damage", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_damage() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityDamageEvent", (String)"damage", (Object)this.instance);
    }

    public double tg$3_damage() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityDamageEvent", (String)"damage", (Object)this.instance).asNum();
    }

    public ScriptValue pg$4_cause() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityDamageEvent", (String)"cause", (Object)this.instance);
    }

    public String tg$5_cause() {
        if (tp$5 != null) {
            return (String)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityDamageEvent", (String)"cause", (Object)this.instance).asStr();
    }

    public ScriptValue pg$6_entity() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityDamageEvent", (String)"entity", (Object)this.instance);
    }

    public PolyClassEntityDamageEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityDamageEvent of(Object object) {
        return new PolyClassEntityDamageEvent(object);
    }

    public static PolyClassEntityDamageEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("EntityDamageEvent")) {
            return new PolyClassEntityDamageEvent(object);
        }
        return null;
    }
}
