/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
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

public class PolyClassMap {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler2 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler2 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.PropertyHandler p$10;
    private static volatile PolyType.TypedPropertyHandler tp$11;
    private static volatile PolyType.PropertyHandler p$12;
    private static volatile PolyType.PropertyHandler p$13;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Map", (String)"with", (String)"SR:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Map", (String)"with");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Map", (String)"get", (String)"S:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Map", (String)"get");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Map", (String)"has", (String)"S:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Map", (String)"has");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Map", (String)"without", (String)"S:R");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Map", (String)"without");
        h$8 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Map", (String)"switch", (String)"SR:R");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Map", (String)"switch");
        p$10 = PolyClassRuntime.resolvePropertyHandler((String)"Map", (String)"size");
        tp$11 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Map", (String)"size", (String)"D");
        p$12 = PolyClassRuntime.resolvePropertyHandler((String)"Map", (String)"keys");
        p$13 = PolyClassRuntime.resolvePropertyHandler((String)"Map", (String)"values");
    }

    public ScriptValue tm$0_with(String string, ScriptValue scriptValue) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)string, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Map", (String)"with", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), scriptValue});
    }

    public ScriptValue um$1_with(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Map", (String)"with", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_get(String string) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Map", (String)"get", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$3_get(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Map", (String)"get", (Object)this.instance, (List)list);
    }

    public boolean tm$4_has(String string) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Map", (String)"has", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$5_has(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Map", (String)"has", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_without(String string) {
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Map", (String)"without", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$7_without(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Map", (String)"without", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$8_switch(String string, ScriptValue scriptValue) {
        if (h$8 != null) {
            return (ScriptValue)h$8.call(this.instance, (Object)string, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Map", (String)"switch", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), scriptValue});
    }

    public ScriptValue um$9_switch(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Map", (String)"switch", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$10_size() {
        if (p$10 != null) {
            return p$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Map", (String)"size", (Object)this.instance);
    }

    public double tg$11_size() {
        if (tp$11 != null) {
            return (Double)tp$11.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Map", (String)"size", (Object)this.instance).asNum();
    }

    public ScriptValue pg$12_keys() {
        if (p$12 != null) {
            return p$12.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Map", (String)"keys", (Object)this.instance);
    }

    public ScriptValue pg$13_values() {
        if (p$13 != null) {
            return p$13.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Map", (String)"values", (Object)this.instance);
    }

    public PolyClassMap(Object object) {
        this.instance = object;
    }

    public static PolyClassMap of(Object object) {
        return new PolyClassMap(object);
    }

    public static PolyClassMap ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Map")) {
            return new PolyClassMap(object);
        }
        return null;
    }
}
