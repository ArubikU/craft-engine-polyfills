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
    private static volatile PolyType.PropertyHandler p$9;
    private static volatile PolyType.PropertyHandler p$10;
    private static volatile PolyType.PropertyHandler p$11;
    private static volatile PolyType.PropertyHandler p$12;
    private static volatile PolyType.PropertyHandler p$13;
    private static volatile PolyType.PropertyHandler p$14;
    private static volatile PolyType.PropertyHandler p$15;
    private static volatile PolyType.PropertyHandler p$16;
    private static volatile PolyType.PropertyHandler p$17;

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
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"is_master");
        p$10 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"is_part");
        p$11 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"rel_x");
        p$12 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"rel_y");
        p$13 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"part_id");
        p$14 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"is_core_part");
        p$15 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"formed");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"part_count");
        p$17 = PolyClassRuntime.resolvePropertyHandler((String)"MultiBlock", (String)"core_block");
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

    public ScriptValue pg$9_is_master() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"is_master", (Object)this.instance);
    }

    public ScriptValue pg$10_is_part() {
        if (p$10 != null) {
            return p$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"is_part", (Object)this.instance);
    }

    public ScriptValue pg$11_rel_x() {
        if (p$11 != null) {
            return p$11.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"rel_x", (Object)this.instance);
    }

    public ScriptValue pg$12_rel_y() {
        if (p$12 != null) {
            return p$12.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"rel_y", (Object)this.instance);
    }

    public ScriptValue pg$13_part_id() {
        if (p$13 != null) {
            return p$13.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"part_id", (Object)this.instance);
    }

    public ScriptValue pg$14_is_core_part() {
        if (p$14 != null) {
            return p$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"is_core_part", (Object)this.instance);
    }

    public ScriptValue pg$15_formed() {
        if (p$15 != null) {
            return p$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"formed", (Object)this.instance);
    }

    public ScriptValue pg$16_part_count() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"MultiBlock", (String)"part_count", (Object)this.instance);
    }

    public ScriptValue pg$17_core_block() {
        if (p$17 != null) {
            return p$17.get(this.instance);
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
