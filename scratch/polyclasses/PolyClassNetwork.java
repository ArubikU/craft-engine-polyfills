/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassNetwork {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler3 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler2 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler2 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler2 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler2 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler1 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler2 h$12;
    private static volatile PolyType.MethodHandler m$13;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Network", (String)"broadcast", (String)"DRS:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Network", (String)"broadcast");
        h$2 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Network", (String)"has_packet", (String)"DS:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Network", (String)"has_packet");
        h$4 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Network", (String)"query", (String)"DS:R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Network", (String)"query");
        h$6 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Network", (String)"unregister", (String)"DS:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Network", (String)"unregister");
        h$8 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Network", (String)"listen", (String)"DS:R");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Network", (String)"listen");
        h$10 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Network", (String)"packet_type", (String)"D:S");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Network", (String)"packet_type");
        h$12 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Network", (String)"register", (String)"DS:Z");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Network", (String)"register");
    }

    public boolean tm$0_broadcast(double d, ScriptValue scriptValue, String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Network", (String)"broadcast", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), scriptValue, ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_broadcast(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Network", (String)"broadcast", (Object)this.instance, (List)list);
    }

    public boolean tm$2_has_packet(double d, String string) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)d, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Network", (String)"has_packet", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$3_has_packet(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Network", (String)"has_packet", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_query(double d, String string) {
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance, (Object)d, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Network", (String)"query", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((String)string)});
    }

    public ScriptValue um$5_query(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Network", (String)"query", (Object)this.instance, (List)list);
    }

    public boolean tm$6_unregister(double d, String string) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)d, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Network", (String)"unregister", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$7_unregister(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Network", (String)"unregister", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$8_listen(double d, String string) {
        if (h$8 != null) {
            return (ScriptValue)h$8.call(this.instance, (Object)d, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Network", (String)"listen", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((String)string)});
    }

    public ScriptValue um$9_listen(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Network", (String)"listen", (Object)this.instance, (List)list);
    }

    public String tm$10_packet_type(double d) {
        if (h$10 != null) {
            return (String)h$10.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Network", (String)"packet_type", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asStr();
    }

    public ScriptValue um$11_packet_type(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Network", (String)"packet_type", (Object)this.instance, (List)list);
    }

    public boolean tm$12_register(double d, String string) {
        if (h$12 != null) {
            return (Boolean)h$12.call(this.instance, (Object)d, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Network", (String)"register", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$13_register(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Network", (String)"register", (Object)this.instance, (List)list);
    }

    public PolyClassNetwork(Object object) {
        this.instance = object;
    }

    public static PolyClassNetwork of(Object object) {
        return new PolyClassNetwork(object);
    }

    public static PolyClassNetwork ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Network")) {
            return new PolyClassNetwork(object);
        }
        return null;
    }
}
