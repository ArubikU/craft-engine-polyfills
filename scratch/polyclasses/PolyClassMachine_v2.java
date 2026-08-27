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

public class PolyClassMachine_v2 {
    protected final Object instance;
    private static volatile PolyType.MethodHandler m$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.MethodHandler m$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.MethodHandler m$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.PropertyHandler p$7;
    private static volatile PolyType.PropertyHandler p$8;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_typed");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"get_typed");
        m$2 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_rpm_output");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"consume_gas");
        m$4 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"report_su");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"overclock");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"efficiency");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"gas_tanks");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"is_overstressed");
    }

    public ScriptValue um$0_set_typed(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_typed", (Object)this.instance, (List)list);
    }

    public ScriptValue um$1_get_typed(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"get_typed", (Object)this.instance, (List)list);
    }

    public ScriptValue um$2_set_rpm_output(List list) {
        if (m$2 != null) {
            return m$2.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_rpm_output", (Object)this.instance, (List)list);
    }

    public ScriptValue um$3_consume_gas(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"consume_gas", (Object)this.instance, (List)list);
    }

    public ScriptValue um$4_report_su(List list) {
        if (m$4 != null) {
            return m$4.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"report_su", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$5_overclock() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"overclock", (Object)this.instance);
    }

    public ScriptValue pg$6_efficiency() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"efficiency", (Object)this.instance);
    }

    public ScriptValue pg$7_gas_tanks() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"gas_tanks", (Object)this.instance);
    }

    public ScriptValue pg$8_is_overstressed() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"is_overstressed", (Object)this.instance);
    }

    public PolyClassMachine_v2(Object object) {
        this.instance = object;
    }

    public static PolyClassMachine_v2 of(Object object) {
        return new PolyClassMachine_v2(object);
    }
}
