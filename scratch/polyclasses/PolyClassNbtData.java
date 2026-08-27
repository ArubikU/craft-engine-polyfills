/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassNbtData {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"NbtData", (String)"get_string", (String)"S:S");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"NbtData", (String)"get_string");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"NbtData", (String)"get_double", (String)"S:D");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"NbtData", (String)"get_double");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"NbtData", (String)"get", (String)"S:R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"NbtData", (String)"get");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"NbtData", (String)"has", (String)"S:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"NbtData", (String)"has");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"NbtData", (String)"get_int", (String)"S:D");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"NbtData", (String)"get_int");
    }

    public String tm$0_get_string(String string) {
        if (h$0 != null) {
            return (String)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"NbtData", (String)"get_string", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asStr();
    }

    public ScriptValue um$1_get_string(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"NbtData", (String)"get_string", (Object)this.instance, (List)list);
    }

    public double tm$2_get_double(String string) {
        if (h$2 != null) {
            return (Double)h$2.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"NbtData", (String)"get_double", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$3_get_double(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"NbtData", (String)"get_double", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_get(String string) {
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"NbtData", (String)"get", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$5_get(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"NbtData", (String)"get", (Object)this.instance, (List)list);
    }

    public boolean tm$6_has(String string) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"NbtData", (String)"has", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$7_has(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"NbtData", (String)"has", (Object)this.instance, (List)list);
    }

    public double tm$8_get_int(String string) {
        if (h$8 != null) {
            return (Double)h$8.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"NbtData", (String)"get_int", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$9_get_int(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"NbtData", (String)"get_int", (Object)this.instance, (List)list);
    }

    public PolyClassNbtData(Object object) {
        this.instance = object;
    }

    public static PolyClassNbtData of(Object object) {
        return new PolyClassNbtData(object);
    }

    public static PolyClassNbtData ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("NbtData")) {
            return new PolyClassNbtData(object);
        }
        return null;
    }

    public static PolyClassNbtData ofVar(ScriptContext scriptContext, String string) {
        return PolyClassNbtData.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
