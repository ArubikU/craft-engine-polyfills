/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassServer {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler3 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler2 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler1 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler1 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler1 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.PropertyHandler p$16;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Server", (String)"set_typed", (String)"SSR:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Server", (String)"set_typed");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Server", (String)"get_entity_by_uuid", (String)"S:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Server", (String)"get_entity_by_uuid");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Server", (String)"has_typed", (String)"S:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Server", (String)"has_typed");
        h$6 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Server", (String)"get_typed", (String)"SS:R");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Server", (String)"get_typed");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Server", (String)"get_offline_name", (String)"S:S");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Server", (String)"get_offline_name");
        h$10 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Server", (String)"get_player", (String)"S:R");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Server", (String)"get_player");
        h$12 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Server", (String)"get_player_by_uuid", (String)"S:R");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Server", (String)"get_player_by_uuid");
        h$14 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Server", (String)"exec_command", (String)"S:Z");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"Server", (String)"exec_command");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"Server", (String)"time");
    }

    public boolean tm$0_set_typed(String string, String string2, ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string, (Object)string2, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Server", (String)"set_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2), scriptValue}).asBool();
    }

    public ScriptValue um$1_set_typed(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Server", (String)"set_typed", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_get_entity_by_uuid(String string) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Server", (String)"get_entity_by_uuid", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$3_get_entity_by_uuid(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Server", (String)"get_entity_by_uuid", (Object)this.instance, (List)list);
    }

    public boolean tm$4_has_typed(String string) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Server", (String)"has_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$5_has_typed(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Server", (String)"has_typed", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_get_typed(String string, String string2) {
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"Server", (String)"get_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)});
    }

    public ScriptValue um$7_get_typed(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Server", (String)"get_typed", (Object)this.instance, (List)list);
    }

    public String tm$8_get_offline_name(String string) {
        if (h$8 != null) {
            return (String)h$8.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Server", (String)"get_offline_name", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asStr();
    }

    public ScriptValue um$9_get_offline_name(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Server", (String)"get_offline_name", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$10_get_player(String string) {
        if (h$10 != null) {
            return (ScriptValue)h$10.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Server", (String)"get_player", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$11_get_player(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Server", (String)"get_player", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$12_get_player_by_uuid(String string) {
        if (h$12 != null) {
            return (ScriptValue)h$12.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Server", (String)"get_player_by_uuid", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$13_get_player_by_uuid(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Server", (String)"get_player_by_uuid", (Object)this.instance, (List)list);
    }

    public boolean tm$14_exec_command(String string) {
        if (h$14 != null) {
            return (Boolean)h$14.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Server", (String)"exec_command", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$15_exec_command(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Server", (String)"exec_command", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$16_time() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Server", (String)"time", (Object)this.instance);
    }

    public PolyClassServer(Object object) {
        this.instance = object;
    }

    public static PolyClassServer of(Object object) {
        return new PolyClassServer(object);
    }
}
