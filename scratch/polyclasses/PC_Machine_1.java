/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public final class PC_Machine_1 {
    private final Object instance;
    private static volatile PolyType.MethodHandler m$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.MethodHandler m$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.MethodHandler m$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.PropertyHandler p$6;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"block_at");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_rpm_output");
        m$2 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_state");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"report_su");
        m$4 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"relay_to");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"rpm_network");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"axis");
    }

    public ScriptValue um$0_block_at(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"block_at", (Object)this.instance, (List)list);
    }

    public ScriptValue um$1_set_rpm_output(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_rpm_output", (Object)this.instance, (List)list);
    }

    public ScriptValue um$2_set_state(List list) {
        if (m$2 != null) {
            return m$2.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_state", (Object)this.instance, (List)list);
    }

    public ScriptValue um$3_report_su(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"report_su", (Object)this.instance, (List)list);
    }

    public ScriptValue um$4_relay_to(List list) {
        if (m$4 != null) {
            return m$4.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"relay_to", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$5_rpm_network() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"rpm_network", (Object)this.instance);
    }

    public ScriptValue pg$6_axis() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"axis", (Object)this.instance);
    }

    public PC_Machine_1(Object object) {
        this.instance = object;
    }

    public static PC_Machine_1 of(Object object) {
        return new PC_Machine_1(object);
    }
}
