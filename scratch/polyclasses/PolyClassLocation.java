/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
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

public class PolyClassLocation {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler3 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler3 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler3 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.TypedPropertyHandler tp$7;
    private static volatile PolyType.PropertyHandler p$8;
    private static volatile PolyType.PropertyHandler p$9;
    private static volatile PolyType.TypedPropertyHandler tp$10;
    private static volatile PolyType.PropertyHandler p$11;
    private static volatile PolyType.TypedPropertyHandler tp$12;
    private static volatile PolyType.PropertyHandler p$13;
    private static volatile PolyType.TypedPropertyHandler tp$14;
    private static volatile PolyType.PropertyHandler p$15;
    private static volatile PolyType.TypedPropertyHandler tp$16;
    private static volatile PolyType.PropertyHandler p$17;
    private static volatile PolyType.TypedPropertyHandler tp$18;
    private static volatile PolyType.PropertyHandler p$19;
    private static volatile PolyType.TypedPropertyHandler tp$20;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Location", (String)"distance", (String)"DDD:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Location", (String)"distance");
        h$2 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Location", (String)"distance_sq", (String)"DDD:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Location", (String)"distance_sq");
        h$4 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Location", (String)"block", (String)"DDD:R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Location", (String)"block");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"block_z");
        tp$7 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Location", (String)"block_z", (String)"D");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"world");
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"biome");
        tp$10 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Location", (String)"biome", (String)"S");
        p$11 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"x");
        tp$12 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Location", (String)"x", (String)"D");
        p$13 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"y");
        tp$14 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Location", (String)"y", (String)"D");
        p$15 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"z");
        tp$16 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Location", (String)"z", (String)"D");
        p$17 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"block_x");
        tp$18 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Location", (String)"block_x", (String)"D");
        p$19 = PolyClassRuntime.resolvePropertyHandler((String)"Location", (String)"block_y");
        tp$20 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Location", (String)"block_y", (String)"D");
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

    public double tg$7_block_z() {
        if (tp$7 != null) {
            return (Double)tp$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"block_z", (Object)this.instance).asNum();
    }

    public ScriptValue pg$8_world() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$9_biome() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"biome", (Object)this.instance);
    }

    public String tg$10_biome() {
        if (tp$10 != null) {
            return (String)tp$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"biome", (Object)this.instance).asStr();
    }

    public ScriptValue pg$11_x() {
        if (p$11 != null) {
            return p$11.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"x", (Object)this.instance);
    }

    public double tg$12_x() {
        if (tp$12 != null) {
            return (Double)tp$12.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$13_y() {
        if (p$13 != null) {
            return p$13.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"y", (Object)this.instance);
    }

    public double tg$14_y() {
        if (tp$14 != null) {
            return (Double)tp$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"y", (Object)this.instance).asNum();
    }

    public ScriptValue pg$15_z() {
        if (p$15 != null) {
            return p$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"z", (Object)this.instance);
    }

    public double tg$16_z() {
        if (tp$16 != null) {
            return (Double)tp$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"z", (Object)this.instance).asNum();
    }

    public ScriptValue pg$17_block_x() {
        if (p$17 != null) {
            return p$17.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"block_x", (Object)this.instance);
    }

    public double tg$18_block_x() {
        if (tp$18 != null) {
            return (Double)tp$18.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"block_x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$19_block_y() {
        if (p$19 != null) {
            return p$19.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"block_y", (Object)this.instance);
    }

    public double tg$20_block_y() {
        if (tp$20 != null) {
            return (Double)tp$20.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Location", (String)"block_y", (Object)this.instance).asNum();
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

    public static PolyClassLocation ofVar(ScriptContext scriptContext, String string) {
        return PolyClassLocation.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
