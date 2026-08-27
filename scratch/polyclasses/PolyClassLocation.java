/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
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

public class PolyClassLocation {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler3 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler3 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler3 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.PropertyHandler p$7;
    private static volatile PolyType.PropertyHandler p$8;
    private static volatile PolyType.PropertyHandler p$9;
    private static volatile PolyType.PropertyHandler p$10;
    private static volatile PolyType.PropertyHandler p$11;
    private static volatile PolyType.PropertyHandler p$12;
    private static volatile PolyType.PropertyHandler p$13;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Location", (String)"distance", (String)"DDD:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Location", (String)"distance");
        h$2 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Location", (String)"distance_sq", (String)"DDD:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Location", (String)"distance_sq");
        h$4 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Location", (String)"block", (String)"DDD:R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Location", (String)"block");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"block_z");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"world");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"biome");
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"x");
        p$10 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"y");
        p$11 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"z");
        p$12 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"block_x");
        p$13 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"block_y");
    }

    public ScriptValue tm$0_distance(double d, double d2, double d3) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Location", (String)"distance", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$1_distance(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Location", (String)"distance", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_distance_sq(double d, double d2, double d3) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Location", (String)"distance_sq", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$3_distance_sq(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Location", (String)"distance_sq", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_block(double d, double d2, double d3) {
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Location", (String)"block", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$5_block(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Location", (String)"block", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$6_block_z() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"block_z", (Object)this.instance);
    }

    public ScriptValue pg$7_world() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$8_biome() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"biome", (Object)this.instance);
    }

    public ScriptValue pg$9_x() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"x", (Object)this.instance);
    }

    public ScriptValue pg$10_y() {
        if (p$10 != null) {
            return p$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"y", (Object)this.instance);
    }

    public ScriptValue pg$11_z() {
        if (p$11 != null) {
            return p$11.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"z", (Object)this.instance);
    }

    public ScriptValue pg$12_block_x() {
        if (p$12 != null) {
            return p$12.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"block_x", (Object)this.instance);
    }

    public ScriptValue pg$13_block_y() {
        if (p$13 != null) {
            return p$13.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"block_y", (Object)this.instance);
    }

    public PolyClassLocation(Object object) {
        this.instance = object;
    }

    public static PolyClassLocation of(Object object) {
        return new PolyClassLocation(object);
    }

    public static PolyClassLocation ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Location")) {
            return new PolyClassLocation(object);
        }
        return null;
    }
}
