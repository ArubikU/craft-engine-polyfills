/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
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

public class PolyClassEntityDeathEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.MethodHandler m$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"EntityDeathEvent", (String)"set_dropped_exp", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"EntityDeathEvent", (String)"set_dropped_exp");
        m$2 = PolyClassRuntime.resolveMethodHandler((String)"EntityDeathEvent", (String)"set_drops");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"EntityDeathEvent", (String)"drops");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"EntityDeathEvent", (String)"dropped_exp");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"EntityDeathEvent", (String)"entity");
    }

    public boolean tm$0_set_dropped_exp(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"EntityDeathEvent", (String)"set_dropped_exp", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set_dropped_exp(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"EntityDeathEvent", (String)"set_dropped_exp", (Object)this.instance, (List)list);
    }

    public ScriptValue um$2_set_drops(List list) {
        if (m$2 != null) {
            return m$2.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"EntityDeathEvent", (String)"set_drops", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$3_drops() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityDeathEvent", (String)"drops", (Object)this.instance);
    }

    public ScriptValue pg$4_dropped_exp() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityDeathEvent", (String)"dropped_exp", (Object)this.instance);
    }

    public ScriptValue pg$5_entity() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityDeathEvent", (String)"entity", (Object)this.instance);
    }

    public PolyClassEntityDeathEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityDeathEvent of(Object object) {
        return new PolyClassEntityDeathEvent(object);
    }

    public static PolyClassEntityDeathEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("EntityDeathEvent")) {
            return new PolyClassEntityDeathEvent(object);
        }
        return null;
    }
}
