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
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassRedstone {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler0 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler0 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler0 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler1 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.PropertyHandler p$12;
    private static volatile PolyType.PropertyHandler p$13;
    private static volatile PolyType.PropertyHandler p$14;
    private static volatile PolyType.PropertyHandler p$15;
    private static volatile PolyType.PropertyHandler p$16;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Redstone", (String)"set", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Redstone", (String)"set");
        h$2 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Redstone", (String)"relay", (String)":Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Redstone", (String)"relay");
        h$4 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Redstone", (String)"clear", (String)":Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Redstone", (String)"clear");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Redstone", (String)"toggle", (String)"Z:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Redstone", (String)"toggle");
        h$8 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Redstone", (String)"off", (String)":Z");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Redstone", (String)"off");
        h$10 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Redstone", (String)"on", (String)"D:Z");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Redstone", (String)"on");
        p$12 = PolyClassRuntime.resolvePropertyHandler((String)"Redstone", (String)"output");
        p$13 = PolyClassRuntime.resolvePropertyHandler((String)"Redstone", (String)"input");
        p$14 = PolyClassRuntime.resolvePropertyHandler((String)"Redstone", (String)"emitting");
        p$15 = PolyClassRuntime.resolvePropertyHandler((String)"Redstone", (String)"powered");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"Redstone", (String)"max");
    }

    public boolean tm$0_set(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Redstone", (String)"set", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Redstone", (String)"set", (Object)this.instance, (List)list);
    }

    public boolean tm$2_relay() {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Redstone", (String)"relay", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$3_relay(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Redstone", (String)"relay", (Object)this.instance, (List)list);
    }

    public boolean tm$4_clear() {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Redstone", (String)"clear", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$5_clear(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Redstone", (String)"clear", (Object)this.instance, (List)list);
    }

    public boolean tm$6_toggle(boolean bl) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"Redstone", (String)"toggle", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$7_toggle(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Redstone", (String)"toggle", (Object)this.instance, (List)list);
    }

    public boolean tm$8_off() {
        if (h$8 != null) {
            return (Boolean)h$8.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Redstone", (String)"off", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$9_off(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Redstone", (String)"off", (Object)this.instance, (List)list);
    }

    public boolean tm$10_on(double d) {
        if (h$10 != null) {
            return (Boolean)h$10.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Redstone", (String)"on", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$11_on(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Redstone", (String)"on", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$12_output() {
        if (p$12 != null) {
            return p$12.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Redstone", (String)"output", (Object)this.instance);
    }

    public ScriptValue pg$13_input() {
        if (p$13 != null) {
            return p$13.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Redstone", (String)"input", (Object)this.instance);
    }

    public ScriptValue pg$14_emitting() {
        if (p$14 != null) {
            return p$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Redstone", (String)"emitting", (Object)this.instance);
    }

    public ScriptValue pg$15_powered() {
        if (p$15 != null) {
            return p$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Redstone", (String)"powered", (Object)this.instance);
    }

    public ScriptValue pg$16_max() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Redstone", (String)"max", (Object)this.instance);
    }

    public PolyClassRedstone(Object object) {
        this.instance = object;
    }

    public static PolyClassRedstone of(Object object) {
        return new PolyClassRedstone(object);
    }

    public static PolyClassRedstone ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Redstone")) {
            return new PolyClassRedstone(object);
        }
        return null;
    }
}
