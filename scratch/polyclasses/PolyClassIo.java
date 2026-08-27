/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassIo {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler2 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler2 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler2 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler2 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler2 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler2 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler2 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler0 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.TypedMethodHandler1 h$16;
    private static volatile PolyType.MethodHandler m$17;
    private static volatile PolyType.TypedMethodHandler2 h$18;
    private static volatile PolyType.MethodHandler m$19;
    private static volatile PolyType.PropertyHandler p$20;
    private static volatile PolyType.PropertyHandler p$21;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Io", (String)"allow", (String)"RS:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Io", (String)"allow");
        h$2 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Io", (String)"deny_output", (String)"RS:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Io", (String)"deny_output");
        h$4 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Io", (String)"deny", (String)"RS:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Io", (String)"deny");
        h$6 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Io", (String)"accepts_input", (String)"RS:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Io", (String)"accepts_input");
        h$8 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Io", (String)"allow_input", (String)"RS:Z");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Io", (String)"allow_input");
        h$10 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Io", (String)"provides_output", (String)"RS:Z");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Io", (String)"provides_output");
        h$12 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Io", (String)"allow_output", (String)"RS:Z");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Io", (String)"allow_output");
        h$14 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Io", (String)"clear", (String)":Z");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"Io", (String)"clear");
        h$16 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Io", (String)"describe", (String)"R:R");
        m$17 = PolyClassRuntime.resolveMethodHandler((String)"Io", (String)"describe");
        h$18 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Io", (String)"deny_input", (String)"RS:Z");
        m$19 = PolyClassRuntime.resolveMethodHandler((String)"Io", (String)"deny_input");
        p$20 = PolyClassRuntime.resolvePropertyHandler((String)"Io", (String)"configured");
        p$21 = PolyClassRuntime.resolvePropertyHandler((String)"Io", (String)"types");
    }

    public boolean tm$0_allow(ScriptValue scriptValue, String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Io", (String)"allow", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_allow(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Io", (String)"allow", (Object)this.instance, (List)list);
    }

    public boolean tm$2_deny_output(ScriptValue scriptValue, String string) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Io", (String)"deny_output", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$3_deny_output(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Io", (String)"deny_output", (Object)this.instance, (List)list);
    }

    public boolean tm$4_deny(ScriptValue scriptValue, String string) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Io", (String)"deny", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$5_deny(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Io", (String)"deny", (Object)this.instance, (List)list);
    }

    public boolean tm$6_accepts_input(ScriptValue scriptValue, String string) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Io", (String)"accepts_input", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$7_accepts_input(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Io", (String)"accepts_input", (Object)this.instance, (List)list);
    }

    public boolean tm$8_allow_input(ScriptValue scriptValue, String string) {
        if (h$8 != null) {
            return (Boolean)h$8.call(this.instance, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Io", (String)"allow_input", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$9_allow_input(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Io", (String)"allow_input", (Object)this.instance, (List)list);
    }

    public boolean tm$10_provides_output(ScriptValue scriptValue, String string) {
        if (h$10 != null) {
            return (Boolean)h$10.call(this.instance, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Io", (String)"provides_output", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$11_provides_output(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Io", (String)"provides_output", (Object)this.instance, (List)list);
    }

    public boolean tm$12_allow_output(ScriptValue scriptValue, String string) {
        if (h$12 != null) {
            return (Boolean)h$12.call(this.instance, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Io", (String)"allow_output", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$13_allow_output(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Io", (String)"allow_output", (Object)this.instance, (List)list);
    }

    public boolean tm$14_clear() {
        if (h$14 != null) {
            return (Boolean)h$14.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Io", (String)"clear", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$15_clear(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Io", (String)"clear", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$16_describe(ScriptValue scriptValue) {
        if (h$16 != null) {
            return (ScriptValue)h$16.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Io", (String)"describe", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$17_describe(List list) {
        if (m$17 != null) {
            return m$17.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Io", (String)"describe", (Object)this.instance, (List)list);
    }

    public boolean tm$18_deny_input(ScriptValue scriptValue, String string) {
        if (h$18 != null) {
            return (Boolean)h$18.call(this.instance, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Io", (String)"deny_input", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$19_deny_input(List list) {
        if (m$19 != null) {
            return m$19.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Io", (String)"deny_input", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$20_configured() {
        if (p$20 != null) {
            return p$20.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Io", (String)"configured", (Object)this.instance);
    }

    public ScriptValue pg$21_types() {
        if (p$21 != null) {
            return p$21.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Io", (String)"types", (Object)this.instance);
    }

    public PolyClassIo(Object object) {
        this.instance = object;
    }

    public static PolyClassIo of(Object object) {
        return new PolyClassIo(object);
    }
}
