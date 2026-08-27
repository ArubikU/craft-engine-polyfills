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
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassUpgrades {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.PropertyHandler p$8;
    private static volatile PolyType.PropertyHandler p$9;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Upgrades", (String)"get", (String)"S:D");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Upgrades", (String)"get");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Upgrades", (String)"count", (String)"S:D");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Upgrades", (String)"count");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Upgrades", (String)"slot", (String)"D:R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Upgrades", (String)"slot");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Upgrades", (String)"has", (String)"S:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Upgrades", (String)"has");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"Upgrades", (String)"total");
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"Upgrades", (String)"inventory");
    }

    public double tm$0_get(String string) {
        if (h$0 != null) {
            return (Double)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Upgrades", (String)"get", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$1_get(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Upgrades", (String)"get", (Object)this.instance, (List)list);
    }

    public double tm$2_count(String string) {
        if (h$2 != null) {
            return (Double)h$2.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Upgrades", (String)"count", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$3_count(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Upgrades", (String)"count", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_slot(double d) {
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Upgrades", (String)"slot", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$5_slot(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Upgrades", (String)"slot", (Object)this.instance, (List)list);
    }

    public boolean tm$6_has(String string) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Upgrades", (String)"has", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$7_has(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Upgrades", (String)"has", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$8_total() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Upgrades", (String)"total", (Object)this.instance);
    }

    public ScriptValue pg$9_inventory() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Upgrades", (String)"inventory", (Object)this.instance);
    }

    public PolyClassUpgrades(Object object) {
        this.instance = object;
    }

    public static PolyClassUpgrades of(Object object) {
        return new PolyClassUpgrades(object);
    }

    public static PolyClassUpgrades ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Upgrades")) {
            return new PolyClassUpgrades(object);
        }
        return null;
    }
}
