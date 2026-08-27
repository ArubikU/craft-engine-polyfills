/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
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

public class PolyClassBars {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler0 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Bars", (String)"max", (String)"S:D");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Bars", (String)"max");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Bars", (String)"subtype", (String)"S:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Bars", (String)"subtype");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Bars", (String)"get", (String)"S:D");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Bars", (String)"get");
        h$6 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Bars", (String)"list", (String)":R");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Bars", (String)"list");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Bars", (String)"value", (String)"S:D");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Bars", (String)"value");
    }

    public double tm$0_max(String string) {
        if (h$0 != null) {
            return (Double)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Bars", (String)"max", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$1_max(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Bars", (String)"max", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_subtype(String string) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Bars", (String)"subtype", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$3_subtype(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Bars", (String)"subtype", (Object)this.instance, (List)list);
    }

    public double tm$4_get(String string) {
        if (h$4 != null) {
            return (Double)h$4.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Bars", (String)"get", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$5_get(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Bars", (String)"get", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_list() {
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Bars", (String)"list", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$7_list(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Bars", (String)"list", (Object)this.instance, (List)list);
    }

    public double tm$8_value(String string) {
        if (h$8 != null) {
            return (Double)h$8.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Bars", (String)"value", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$9_value(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Bars", (String)"value", (Object)this.instance, (List)list);
    }

    public PolyClassBars(Object object) {
        this.instance = object;
    }

    public static PolyClassBars of(Object object) {
        return new PolyClassBars(object);
    }

    public static PolyClassBars ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Bars")) {
            return new PolyClassBars(object);
        }
        return null;
    }
}
