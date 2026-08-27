/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$TypeCodec
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassGlue {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler2 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.TypeCodec c$6_r;
    private static volatile PolyType.MethodHandler m$7;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Glue", (String)"size", (String)"R:D");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Glue", (String)"size");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Glue", (String)"is_glued", (String)"R:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Glue", (String)"is_glued");
        h$4 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Glue", (String)"count", (String)"RS:D");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Glue", (String)"count");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Glue", (String)"structure", (String)"R:L");
        c$6_r = PolyClassRuntime.resolveListCodec((String)"Glue", (String)"structure", (int)-1);
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Glue", (String)"structure");
    }

    public double tm$0_size(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Double)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Glue", (String)"size", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asNum();
    }

    public ScriptValue um$1_size(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Glue", (String)"size", (Object)this.instance, (List)list);
    }

    public boolean tm$2_is_glued(ScriptValue scriptValue) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Glue", (String)"is_glued", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$3_is_glued(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Glue", (String)"is_glued", (Object)this.instance, (List)list);
    }

    public double tm$4_count(ScriptValue scriptValue, String string) {
        if (h$4 != null) {
            return (Double)h$4.call(this.instance, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Glue", (String)"count", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$5_count(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Glue", (String)"count", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_structure(ScriptValue scriptValue) {
        if (h$6 != null && c$6_r != null) {
            return c$6_r.encode(h$6.call(this.instance, (Object)scriptValue));
        }
        return PolyClassRuntime.genericCall((String)"Glue", (String)"structure", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$7_structure(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Glue", (String)"structure", (Object)this.instance, (List)list);
    }

    public PolyClassGlue(Object object) {
        this.instance = object;
    }

    public static PolyClassGlue of(Object object) {
        return new PolyClassGlue(object);
    }

    public static PolyClassGlue ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Glue")) {
            return new PolyClassGlue(object);
        }
        return null;
    }
}
