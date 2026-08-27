/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
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

public class PolyClassBlockState {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.PropertyHandler p$7;
    private static volatile PolyType.TypedPropertyHandler tp$8;
    private static volatile PolyType.PropertyHandler p$9;
    private static volatile PolyType.TypedPropertyHandler tp$10;
    private static volatile PolyType.PropertyHandler p$11;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"BlockState", (String)"get", (String)"S:S");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"BlockState", (String)"get");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"BlockState", (String)"equals", (String)"R:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"BlockState", (String)"equals");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"BlockState", (String)"has", (String)"S:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"BlockState", (String)"has");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"BlockState", (String)"property_names");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"BlockState", (String)"id");
        tp$8 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BlockState", (String)"id", (String)"S");
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"BlockState", (String)"is_air");
        tp$10 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BlockState", (String)"is_air", (String)"Z");
        p$11 = PolyClassRuntime.resolvePropertyHandler((String)"BlockState", (String)"properties");
    }

    public String tm$0_get(String string) {
        if (h$0 != null) {
            return (String)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"BlockState", (String)"get", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asStr();
    }

    public ScriptValue um$1_get(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BlockState", (String)"get", (Object)this.instance, (List)list);
    }

    public boolean tm$2_equals(ScriptValue scriptValue) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"BlockState", (String)"equals", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$3_equals(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BlockState", (String)"equals", (Object)this.instance, (List)list);
    }

    public boolean tm$4_has(String string) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"BlockState", (String)"has", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$5_has(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BlockState", (String)"has", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$6_property_names() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockState", (String)"property_names", (Object)this.instance);
    }

    public ScriptValue pg$7_id() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockState", (String)"id", (Object)this.instance);
    }

    public String tg$8_id() {
        if (tp$8 != null) {
            return (String)tp$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockState", (String)"id", (Object)this.instance).asStr();
    }

    public ScriptValue pg$9_is_air() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockState", (String)"is_air", (Object)this.instance);
    }

    public boolean tg$10_is_air() {
        if (tp$10 != null) {
            return (Boolean)tp$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockState", (String)"is_air", (Object)this.instance).asBool();
    }

    public ScriptValue pg$11_properties() {
        if (p$11 != null) {
            return p$11.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockState", (String)"properties", (Object)this.instance);
    }

    public PolyClassBlockState(Object object) {
        this.instance = object;
    }

    public static PolyClassBlockState of(Object object) {
        return new PolyClassBlockState(object);
    }

    public static PolyClassBlockState ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BlockState")) {
            return new PolyClassBlockState(object);
        }
        return null;
    }

    public static PolyClassBlockState ofVar(ScriptContext scriptContext, String string) {
        return PolyClassBlockState.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
