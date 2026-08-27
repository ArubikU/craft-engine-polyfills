/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassMultiBlock {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler3 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler3 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.PropertyHandler p$8;
    private static volatile PolyType.TypedPropertyHandler tp$9;
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
    private static volatile PolyType.PropertyHandler p$20;
    private static volatile PolyType.TypedPropertyHandler tp$21;
    private static volatile PolyType.PropertyHandler p$22;
    private static volatile PolyType.TypedPropertyHandler tp$23;
    private static volatile PolyType.PropertyHandler p$24;
    private static volatile PolyType.TypedPropertyHandler tp$25;
    private static volatile PolyType.PropertyHandler p$26;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"MultiBlock", (String)"side", (String)"S:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"MultiBlock", (String)"side");
        h$2 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"MultiBlock", (String)"is_at", (String)"DDD:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"MultiBlock", (String)"is_at");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"MultiBlock", (String)"get_side_block", (String)"S:R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"MultiBlock", (String)"get_side_block");
        h$6 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"MultiBlock", (String)"get_part_block", (String)"DDD:R");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"MultiBlock", (String)"get_part_block");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"rel_z");
        tp$9 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"MultiBlock", (String)"rel_z", (String)"D");
        p$10 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"is_master");
        tp$11 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"MultiBlock", (String)"is_master", (String)"Z");
        p$12 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"is_part");
        tp$13 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"MultiBlock", (String)"is_part", (String)"Z");
        p$14 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"rel_x");
        tp$15 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"MultiBlock", (String)"rel_x", (String)"D");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"rel_y");
        tp$17 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"MultiBlock", (String)"rel_y", (String)"D");
        p$18 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"part_id");
        tp$19 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"MultiBlock", (String)"part_id", (String)"D");
        p$20 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"is_core_part");
        tp$21 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"MultiBlock", (String)"is_core_part", (String)"Z");
        p$22 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"formed");
        tp$23 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"MultiBlock", (String)"formed", (String)"Z");
        p$24 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"part_count");
        tp$25 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"MultiBlock", (String)"part_count", (String)"D");
        p$26 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"core_block");
    }

    public boolean tm$0_side(String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"MultiBlock", (String)"side", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_side(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"MultiBlock", (String)"side", (Object)this.instance, (List)list);
    }

    public boolean tm$2_is_at(double d, double d2, double d3) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"MultiBlock", (String)"is_at", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$3_is_at(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"MultiBlock", (String)"is_at", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_get_side_block(String string) {
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"MultiBlock", (String)"get_side_block", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$5_get_side_block(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"MultiBlock", (String)"get_side_block", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_get_part_block(double d, double d2, double d3) {
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"MultiBlock", (String)"get_part_block", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$7_get_part_block(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"MultiBlock", (String)"get_part_block", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$8_rel_z() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"rel_z", (Object)this.instance);
    }

    public double tg$9_rel_z() {
        if (tp$9 != null) {
            return (Double)tp$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"rel_z", (Object)this.instance).asNum();
    }

    public ScriptValue pg$10_is_master() {
        if (p$10 != null) {
            return p$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"is_master", (Object)this.instance);
    }

    public boolean tg$11_is_master() {
        if (tp$11 != null) {
            return (Boolean)tp$11.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"is_master", (Object)this.instance).asBool();
    }

    public ScriptValue pg$12_is_part() {
        if (p$12 != null) {
            return p$12.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"is_part", (Object)this.instance);
    }

    public boolean tg$13_is_part() {
        if (tp$13 != null) {
            return (Boolean)tp$13.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"is_part", (Object)this.instance).asBool();
    }

    public ScriptValue pg$14_rel_x() {
        if (p$14 != null) {
            return p$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"rel_x", (Object)this.instance);
    }

    public double tg$15_rel_x() {
        if (tp$15 != null) {
            return (Double)tp$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"rel_x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$16_rel_y() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"rel_y", (Object)this.instance);
    }

    public double tg$17_rel_y() {
        if (tp$17 != null) {
            return (Double)tp$17.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"rel_y", (Object)this.instance).asNum();
    }

    public ScriptValue pg$18_part_id() {
        if (p$18 != null) {
            return p$18.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"part_id", (Object)this.instance);
    }

    public double tg$19_part_id() {
        if (tp$19 != null) {
            return (Double)tp$19.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"part_id", (Object)this.instance).asNum();
    }

    public ScriptValue pg$20_is_core_part() {
        if (p$20 != null) {
            return p$20.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"is_core_part", (Object)this.instance);
    }

    public boolean tg$21_is_core_part() {
        if (tp$21 != null) {
            return (Boolean)tp$21.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"is_core_part", (Object)this.instance).asBool();
    }

    public ScriptValue pg$22_formed() {
        if (p$22 != null) {
            return p$22.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"formed", (Object)this.instance);
    }

    public boolean tg$23_formed() {
        if (tp$23 != null) {
            return (Boolean)tp$23.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"formed", (Object)this.instance).asBool();
    }

    public ScriptValue pg$24_part_count() {
        if (p$24 != null) {
            return p$24.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"part_count", (Object)this.instance);
    }

    public double tg$25_part_count() {
        if (tp$25 != null) {
            return (Double)tp$25.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"part_count", (Object)this.instance).asNum();
    }

    public ScriptValue pg$26_core_block() {
        if (p$26 != null) {
            return p$26.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"core_block", (Object)this.instance);
    }

    public PolyClassMultiBlock(Object object) {
        this.instance = object;
    }

    public static PolyClassMultiBlock of(Object object) {
        return new PolyClassMultiBlock(object);
    }

    public static PolyClassMultiBlock ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("MultiBlock")) {
            return new PolyClassMultiBlock(object);
        }
        return null;
    }
}
