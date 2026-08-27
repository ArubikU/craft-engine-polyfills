/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassWorkbench {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.PropertyHandler p$7;
    private static volatile PolyType.PropertyHandler p$8;
    private static volatile PolyType.PropertyHandler p$9;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Workbench", (String)"output", (String)"D:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Workbench", (String)"output");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Workbench", (String)"input", (String)"D:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Workbench", (String)"input");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Workbench", (String)"tool", (String)"D:R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Workbench", (String)"tool");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Workbench", (String)"width");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"Workbench", (String)"total_inputs");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"Workbench", (String)"total_outputs");
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"Workbench", (String)"height");
    }

    public ScriptValue tm$0_output(double d) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Workbench", (String)"output", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$1_output(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Workbench", (String)"output", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_input(double d) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Workbench", (String)"input", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$3_input(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Workbench", (String)"input", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_tool(double d) {
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Workbench", (String)"tool", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$5_tool(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Workbench", (String)"tool", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$6_width() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Workbench", (String)"width", (Object)this.instance);
    }

    public ScriptValue pg$7_total_inputs() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Workbench", (String)"total_inputs", (Object)this.instance);
    }

    public ScriptValue pg$8_total_outputs() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Workbench", (String)"total_outputs", (Object)this.instance);
    }

    public ScriptValue pg$9_height() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Workbench", (String)"height", (Object)this.instance);
    }

    public PolyClassWorkbench(Object object) {
        this.instance = object;
    }

    public static PolyClassWorkbench of(Object object) {
        return new PolyClassWorkbench(object);
    }
}
