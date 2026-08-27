/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassGasTanks {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler2 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler1 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler1 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.PropertyHandler p$14;
    private static volatile PolyType.PropertyHandler p$15;
    private static volatile PolyType.PropertyHandler p$16;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"GasTanks", (String)"is_full", (String)"S:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"GasTanks", (String)"is_full");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"GasTanks", (String)"level", (String)"S:D");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"GasTanks", (String)"level");
        h$4 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"GasTanks", (String)"get", (String)"SS:R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"GasTanks", (String)"get");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"GasTanks", (String)"percent", (String)"S:D");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"GasTanks", (String)"percent");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"GasTanks", (String)"is_empty", (String)"S:Z");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"GasTanks", (String)"is_empty");
        h$10 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"GasTanks", (String)"capacity", (String)"S:D");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"GasTanks", (String)"capacity");
        h$12 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"GasTanks", (String)"fraction", (String)"S:D");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"GasTanks", (String)"fraction");
        p$14 = PolyClassRuntime.resolvePropertyHandler((String)"GasTanks", (String)"total_level");
        p$15 = PolyClassRuntime.resolvePropertyHandler((String)"GasTanks", (String)"count");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"GasTanks", (String)"total_capacity");
    }

    public boolean tm$0_is_full(String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"GasTanks", (String)"is_full", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_is_full(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"GasTanks", (String)"is_full", (Object)this.instance, (List)list);
    }

    public double tm$2_level(String string) {
        if (h$2 != null) {
            return (Double)h$2.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"GasTanks", (String)"level", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$3_level(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"GasTanks", (String)"level", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_get(String string, String string2) {
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"GasTanks", (String)"get", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)});
    }

    public ScriptValue um$5_get(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"GasTanks", (String)"get", (Object)this.instance, (List)list);
    }

    public double tm$6_percent(String string) {
        if (h$6 != null) {
            return (Double)h$6.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"GasTanks", (String)"percent", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$7_percent(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"GasTanks", (String)"percent", (Object)this.instance, (List)list);
    }

    public boolean tm$8_is_empty(String string) {
        if (h$8 != null) {
            return (Boolean)h$8.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"GasTanks", (String)"is_empty", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$9_is_empty(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"GasTanks", (String)"is_empty", (Object)this.instance, (List)list);
    }

    public double tm$10_capacity(String string) {
        if (h$10 != null) {
            return (Double)h$10.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"GasTanks", (String)"capacity", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$11_capacity(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"GasTanks", (String)"capacity", (Object)this.instance, (List)list);
    }

    public double tm$12_fraction(String string) {
        if (h$12 != null) {
            return (Double)h$12.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"GasTanks", (String)"fraction", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$13_fraction(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"GasTanks", (String)"fraction", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$14_total_level() {
        if (p$14 != null) {
            return p$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GasTanks", (String)"total_level", (Object)this.instance);
    }

    public ScriptValue pg$15_count() {
        if (p$15 != null) {
            return p$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GasTanks", (String)"count", (Object)this.instance);
    }

    public ScriptValue pg$16_total_capacity() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GasTanks", (String)"total_capacity", (Object)this.instance);
    }

    public PolyClassGasTanks(Object object) {
        this.instance = object;
    }

    public static PolyClassGasTanks of(Object object) {
        return new PolyClassGasTanks(object);
    }
}
