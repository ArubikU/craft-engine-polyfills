/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
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

public class PolyClassVector {
    protected final Object instance;
    private static volatile PolyType.MethodHandler m$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.MethodHandler m$2;
    private static volatile PolyType.TypedMethodHandler0 h$3;
    private static volatile PolyType.MethodHandler m$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.MethodHandler m$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.PropertyHandler p$10;
    private static volatile PolyType.TypedPropertyHandler tp$11;
    private static volatile PolyType.PropertyHandler p$12;
    private static volatile PolyType.TypedPropertyHandler tp$13;
    private static volatile PolyType.PropertyHandler p$14;
    private static volatile PolyType.TypedPropertyHandler tp$15;
    private static volatile PolyType.PropertyHandler p$16;
    private static volatile PolyType.TypedPropertyHandler tp$17;
    private static volatile PolyType.PropertyHandler p$18;
    private static volatile PolyType.TypedPropertyHandler tp$19;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"Vector", (String)"add");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Vector", (String)"sub");
        m$2 = PolyClassRuntime.resolveMethodHandler((String)"Vector", (String)"distance");
        h$3 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Vector", (String)"normalize", (String)":R");
        m$4 = PolyClassRuntime.resolveMethodHandler((String)"Vector", (String)"normalize");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Vector", (String)"distance_sq");
        m$6 = PolyClassRuntime.resolveMethodHandler((String)"Vector", (String)"dot");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Vector", (String)"cross");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Vector", (String)"scale", (String)"D:R");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Vector", (String)"scale");
        p$10 = PolyClassRuntime.resolvePropertyHandler((String)"Vector", (String)"x");
        tp$11 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Vector", (String)"x", (String)"D");
        p$12 = PolyClassRuntime.resolvePropertyHandler((String)"Vector", (String)"length");
        tp$13 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Vector", (String)"length", (String)"D");
        p$14 = PolyClassRuntime.resolvePropertyHandler((String)"Vector", (String)"y");
        tp$15 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Vector", (String)"y", (String)"D");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"Vector", (String)"z");
        tp$17 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Vector", (String)"z", (String)"D");
        p$18 = PolyClassRuntime.resolvePropertyHandler((String)"Vector", (String)"length_sq");
        tp$19 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Vector", (String)"length_sq", (String)"D");
    }

    public ScriptValue um$0_add(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Vector", (String)"add", (Object)this.instance, (List)list);
    }

    public ScriptValue um$1_sub(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Vector", (String)"sub", (Object)this.instance, (List)list);
    }

    public ScriptValue um$2_distance(List list) {
        if (m$2 != null) {
            return m$2.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Vector", (String)"distance", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$3_normalize() {
        if (h$3 != null) {
            return (ScriptValue)h$3.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Vector", (String)"normalize", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$4_normalize(List list) {
        if (m$4 != null) {
            return m$4.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Vector", (String)"normalize", (Object)this.instance, (List)list);
    }

    public ScriptValue um$5_distance_sq(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Vector", (String)"distance_sq", (Object)this.instance, (List)list);
    }

    public ScriptValue um$6_dot(List list) {
        if (m$6 != null) {
            return m$6.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Vector", (String)"dot", (Object)this.instance, (List)list);
    }

    public ScriptValue um$7_cross(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Vector", (String)"cross", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$8_scale(double d) {
        if (h$8 != null) {
            return (ScriptValue)h$8.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Vector", (String)"scale", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$9_scale(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Vector", (String)"scale", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$10_x() {
        if (p$10 != null) {
            return p$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Vector", (String)"x", (Object)this.instance);
    }

    public double tg$11_x() {
        if (tp$11 != null) {
            return (Double)tp$11.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Vector", (String)"x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$12_length() {
        if (p$12 != null) {
            return p$12.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Vector", (String)"length", (Object)this.instance);
    }

    public double tg$13_length() {
        if (tp$13 != null) {
            return (Double)tp$13.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Vector", (String)"length", (Object)this.instance).asNum();
    }

    public ScriptValue pg$14_y() {
        if (p$14 != null) {
            return p$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Vector", (String)"y", (Object)this.instance);
    }

    public double tg$15_y() {
        if (tp$15 != null) {
            return (Double)tp$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Vector", (String)"y", (Object)this.instance).asNum();
    }

    public ScriptValue pg$16_z() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Vector", (String)"z", (Object)this.instance);
    }

    public double tg$17_z() {
        if (tp$17 != null) {
            return (Double)tp$17.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Vector", (String)"z", (Object)this.instance).asNum();
    }

    public ScriptValue pg$18_length_sq() {
        if (p$18 != null) {
            return p$18.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Vector", (String)"length_sq", (Object)this.instance);
    }

    public double tg$19_length_sq() {
        if (tp$19 != null) {
            return (Double)tp$19.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Vector", (String)"length_sq", (Object)this.instance).asNum();
    }

    public PolyClassVector(Object object) {
        this.instance = object;
    }

    public static PolyClassVector of(Object object) {
        return new PolyClassVector(object);
    }

    public static PolyClassVector ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Vector")) {
            return new PolyClassVector(object);
        }
        return null;
    }

    public static PolyClassVector ofVar(ScriptContext scriptContext, String string) {
        return PolyClassVector.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
