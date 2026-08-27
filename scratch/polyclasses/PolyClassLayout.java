/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassLayout {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler1 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler1 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler0 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.PropertyHandler p$16;
    private static volatile PolyType.TypedPropertyHandler tp$17;
    private static volatile PolyType.PropertyHandler p$18;
    private static volatile PolyType.PropertyHandler p$19;
    private static volatile PolyType.TypedPropertyHandler tp$20;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Layout", (String)"is_locked", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Layout", (String)"is_locked");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Layout", (String)"unlock", (String)"R:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Layout", (String)"unlock");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Layout", (String)"unlock_type", (String)"S:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Layout", (String)"unlock_type");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Layout", (String)"slots_of_type", (String)"S:R");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Layout", (String)"slots_of_type");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Layout", (String)"slot_type", (String)"D:S");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Layout", (String)"slot_type");
        h$10 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Layout", (String)"lock", (String)"R:Z");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Layout", (String)"lock");
        h$12 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Layout", (String)"lock_type", (String)"S:Z");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Layout", (String)"lock_type");
        h$14 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Layout", (String)"unlock_all", (String)":Z");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"Layout", (String)"unlock_all");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"Layout", (String)"size");
        tp$17 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Layout", (String)"size", (String)"D");
        p$18 = PolyClassRuntime.resolvePropertyHandler((String)"Layout", (String)"locked_slots");
        p$19 = PolyClassRuntime.resolvePropertyHandler((String)"Layout", (String)"open");
        tp$20 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Layout", (String)"open", (String)"Z");
    }

    public boolean tm$0_is_locked(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Layout", (String)"is_locked", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_is_locked(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Layout", (String)"is_locked", (Object)this.instance, (List)list);
    }

    public boolean tm$2_unlock(ScriptValue scriptValue) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Layout", (String)"unlock", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$3_unlock(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Layout", (String)"unlock", (Object)this.instance, (List)list);
    }

    public boolean tm$4_unlock_type(String string) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Layout", (String)"unlock_type", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$5_unlock_type(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Layout", (String)"unlock_type", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_slots_of_type(String string) {
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Layout", (String)"slots_of_type", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$7_slots_of_type(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Layout", (String)"slots_of_type", (Object)this.instance, (List)list);
    }

    public String tm$8_slot_type(double d) {
        if (h$8 != null) {
            return (String)h$8.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Layout", (String)"slot_type", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asStr();
    }

    public ScriptValue um$9_slot_type(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Layout", (String)"slot_type", (Object)this.instance, (List)list);
    }

    public boolean tm$10_lock(ScriptValue scriptValue) {
        if (h$10 != null) {
            return (Boolean)h$10.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Layout", (String)"lock", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$11_lock(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Layout", (String)"lock", (Object)this.instance, (List)list);
    }

    public boolean tm$12_lock_type(String string) {
        if (h$12 != null) {
            return (Boolean)h$12.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Layout", (String)"lock_type", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$13_lock_type(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Layout", (String)"lock_type", (Object)this.instance, (List)list);
    }

    public boolean tm$14_unlock_all() {
        if (h$14 != null) {
            return (Boolean)h$14.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Layout", (String)"unlock_all", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$15_unlock_all(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Layout", (String)"unlock_all", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$16_size() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Layout", (String)"size", (Object)this.instance);
    }

    public double tg$17_size() {
        if (tp$17 != null) {
            return (Double)tp$17.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Layout", (String)"size", (Object)this.instance).asNum();
    }

    public ScriptValue pg$18_locked_slots() {
        if (p$18 != null) {
            return p$18.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Layout", (String)"locked_slots", (Object)this.instance);
    }

    public ScriptValue pg$19_open() {
        if (p$19 != null) {
            return p$19.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Layout", (String)"open", (Object)this.instance);
    }

    public boolean tg$20_open() {
        if (tp$20 != null) {
            return (Boolean)tp$20.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Layout", (String)"open", (Object)this.instance).asBool();
    }

    public PolyClassLayout(Object object) {
        this.instance = object;
    }

    public static PolyClassLayout of(Object object) {
        return new PolyClassLayout(object);
    }

    public static PolyClassLayout ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Layout")) {
            return new PolyClassLayout(object);
        }
        return null;
    }
}
