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

public class PolyClassPlayerDeathEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.MethodHandler m$4;
    private static volatile PolyType.TypedMethodHandler1 h$5;
    private static volatile PolyType.MethodHandler m$6;
    private static volatile PolyType.PropertyHandler p$7;
    private static volatile PolyType.PropertyHandler p$8;
    private static volatile PolyType.TypedPropertyHandler tp$9;
    private static volatile PolyType.PropertyHandler p$10;
    private static volatile PolyType.TypedPropertyHandler tp$11;
    private static volatile PolyType.PropertyHandler p$12;
    private static volatile PolyType.TypedPropertyHandler tp$13;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerDeathEvent", (String)"set_keep_inventory", (String)"Z:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerDeathEvent", (String)"set_keep_inventory");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerDeathEvent", (String)"set_exp", (String)"D:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"PlayerDeathEvent", (String)"set_exp");
        m$4 = PolyClassRuntime.resolveMethodHandler((String)"PlayerDeathEvent", (String)"set_drops");
        h$5 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerDeathEvent", (String)"set_death_message", (String)"S:Z");
        m$6 = PolyClassRuntime.resolveMethodHandler((String)"PlayerDeathEvent", (String)"set_death_message");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerDeathEvent", (String)"drops");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerDeathEvent", (String)"death_message");
        tp$9 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerDeathEvent", (String)"death_message", (String)"S");
        p$10 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerDeathEvent", (String)"keep_inventory");
        tp$11 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerDeathEvent", (String)"keep_inventory", (String)"Z");
        p$12 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerDeathEvent", (String)"exp");
        tp$13 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerDeathEvent", (String)"exp", (String)"D");
    }

    public boolean tm$0_set_keep_inventory(boolean bl) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"PlayerDeathEvent", (String)"set_keep_inventory", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$1_set_keep_inventory(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerDeathEvent", (String)"set_keep_inventory", (Object)this.instance, (List)list);
    }

    public boolean tm$2_set_exp(double d) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"PlayerDeathEvent", (String)"set_exp", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$3_set_exp(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerDeathEvent", (String)"set_exp", (Object)this.instance, (List)list);
    }

    public ScriptValue um$4_set_drops(List list) {
        if (m$4 != null) {
            return m$4.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerDeathEvent", (String)"set_drops", (Object)this.instance, (List)list);
    }

    public boolean tm$5_set_death_message(String string) {
        if (h$5 != null) {
            return (Boolean)h$5.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"PlayerDeathEvent", (String)"set_death_message", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$6_set_death_message(List list) {
        if (m$6 != null) {
            return m$6.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerDeathEvent", (String)"set_death_message", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$7_drops() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerDeathEvent", (String)"drops", (Object)this.instance);
    }

    public ScriptValue pg$8_death_message() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerDeathEvent", (String)"death_message", (Object)this.instance);
    }

    public String tg$9_death_message() {
        if (tp$9 != null) {
            return (String)tp$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerDeathEvent", (String)"death_message", (Object)this.instance).asStr();
    }

    public ScriptValue pg$10_keep_inventory() {
        if (p$10 != null) {
            return p$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerDeathEvent", (String)"keep_inventory", (Object)this.instance);
    }

    public boolean tg$11_keep_inventory() {
        if (tp$11 != null) {
            return (Boolean)tp$11.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerDeathEvent", (String)"keep_inventory", (Object)this.instance).asBool();
    }

    public ScriptValue pg$12_exp() {
        if (p$12 != null) {
            return p$12.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerDeathEvent", (String)"exp", (Object)this.instance);
    }

    public double tg$13_exp() {
        if (tp$13 != null) {
            return (Double)tp$13.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerDeathEvent", (String)"exp", (Object)this.instance).asNum();
    }

    public PolyClassPlayerDeathEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerDeathEvent of(Object object) {
        return new PolyClassPlayerDeathEvent(object);
    }

    public static PolyClassPlayerDeathEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerDeathEvent")) {
            return new PolyClassPlayerDeathEvent(object);
        }
        return null;
    }
}
