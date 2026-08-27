/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypeCodec
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
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

public class PolyClassBelt {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler0 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler0 h$2;
    private static volatile PolyType.TypeCodec c$2_r;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler0 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler1 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler0 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.PropertyHandler p$14;
    private static volatile PolyType.TypedPropertyHandler tp$15;
    private static volatile PolyType.PropertyHandler p$16;
    private static volatile PolyType.TypedPropertyHandler tp$17;
    private static volatile PolyType.PropertyHandler p$18;
    private static volatile PolyType.TypedPropertyHandler tp$19;
    private static volatile PolyType.PropertyHandler p$20;
    private static volatile PolyType.TypedPropertyHandler tp$21;
    private static volatile PolyType.PropertyHandler p$22;
    private static volatile PolyType.PropertyHandler p$23;
    private static volatile PolyType.TypedPropertyHandler tp$24;
    private static volatile PolyType.PropertyHandler p$25;
    private static volatile PolyType.TypedPropertyHandler tp$26;
    private static volatile PolyType.PropertyHandler p$27;
    private static volatile PolyType.TypedPropertyHandler tp$28;
    private static volatile PolyType.PropertyHandler p$29;
    private static volatile PolyType.TypedPropertyHandler tp$30;
    private static volatile PolyType.PropertyHandler p$31;
    private static volatile PolyType.TypedPropertyHandler tp$32;
    private static volatile PolyType.PropertyHandler p$33;
    private static volatile PolyType.TypedPropertyHandler tp$34;
    private static volatile PolyType.PropertyHandler p$35;
    private static volatile PolyType.PropertyHandler p$36;
    private static volatile PolyType.TypedPropertyHandler tp$37;
    private static volatile PolyType.PropertyHandler p$38;
    private static volatile PolyType.TypedPropertyHandler tp$39;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Belt", (String)"take", (String)":R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Belt", (String)"take");
        h$2 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Belt", (String)"get_belt_items", (String)":L");
        c$2_r = PolyClassRuntime.resolveListCodec((String)"Belt", (String)"get_belt_items", (int)-1);
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Belt", (String)"get_belt_items");
        h$4 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Belt", (String)"get_block", (String)":R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Belt", (String)"get_block");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Belt", (String)"get_belt_item", (String)"D:R");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Belt", (String)"get_belt_item");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Belt", (String)"replace", (String)"R:Z");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Belt", (String)"replace");
        h$10 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Belt", (String)"put", (String)"R:R");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Belt", (String)"put");
        h$12 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Belt", (String)"peek", (String)":R");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Belt", (String)"peek");
        p$14 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"item_scale");
        tp$15 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"item_scale", (String)"D");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"rpm");
        tp$17 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"rpm", (String)"D");
        p$18 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"speed");
        tp$19 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"speed", (String)"D");
        p$20 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"is_full");
        tp$21 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"is_full", (String)"Z");
        p$22 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"pos");
        p$23 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"x");
        tp$24 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"x", (String)"D");
        p$25 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"has_item");
        tp$26 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"has_item", (String)"Z");
        p$27 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"y");
        tp$28 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"y", (String)"D");
        p$29 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"exists");
        tp$30 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"exists", (String)"Z");
        p$31 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"progress");
        tp$32 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"progress", (String)"D");
        p$33 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"z");
        tp$34 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"z", (String)"D");
        p$35 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"block");
        p$36 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"slot_count");
        tp$37 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"slot_count", (String)"D");
        p$38 = PolyClassRuntime.resolvePropertyHandler((String)"Belt", (String)"height");
        tp$39 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Belt", (String)"height", (String)"D");
    }

    public ScriptValue tm$0_take() {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Belt", (String)"take", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$1_take(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Belt", (String)"take", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_get_belt_items() {
        if (h$2 != null && c$2_r != null) {
            return c$2_r.encode(h$2.call(this.instance));
        }
        return PolyClassRuntime.genericCall((String)"Belt", (String)"get_belt_items", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$3_get_belt_items(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Belt", (String)"get_belt_items", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_get_block() {
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Belt", (String)"get_block", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$5_get_block(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Belt", (String)"get_block", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_get_belt_item(double d) {
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Belt", (String)"get_belt_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$7_get_belt_item(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Belt", (String)"get_belt_item", (Object)this.instance, (List)list);
    }

    public boolean tm$8_replace(ScriptValue scriptValue) {
        if (h$8 != null) {
            return (Boolean)h$8.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Belt", (String)"replace", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$9_replace(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Belt", (String)"replace", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$10_put(ScriptValue scriptValue) {
        if (h$10 != null) {
            return (ScriptValue)h$10.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Belt", (String)"put", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$11_put(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Belt", (String)"put", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$12_peek() {
        if (h$12 != null) {
            return (ScriptValue)h$12.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Belt", (String)"peek", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$13_peek(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Belt", (String)"peek", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$14_item_scale() {
        if (p$14 != null) {
            return p$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"item_scale", (Object)this.instance);
    }

    public double tg$15_item_scale() {
        if (tp$15 != null) {
            return (Double)tp$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"item_scale", (Object)this.instance).asNum();
    }

    public ScriptValue pg$16_rpm() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"rpm", (Object)this.instance);
    }

    public double tg$17_rpm() {
        if (tp$17 != null) {
            return (Double)tp$17.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"rpm", (Object)this.instance).asNum();
    }

    public ScriptValue pg$18_speed() {
        if (p$18 != null) {
            return p$18.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"speed", (Object)this.instance);
    }

    public double tg$19_speed() {
        if (tp$19 != null) {
            return (Double)tp$19.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"speed", (Object)this.instance).asNum();
    }

    public ScriptValue pg$20_is_full() {
        if (p$20 != null) {
            return p$20.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"is_full", (Object)this.instance);
    }

    public boolean tg$21_is_full() {
        if (tp$21 != null) {
            return (Boolean)tp$21.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"is_full", (Object)this.instance).asBool();
    }

    public ScriptValue pg$22_pos() {
        if (p$22 != null) {
            return p$22.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"pos", (Object)this.instance);
    }

    public ScriptValue pg$23_x() {
        if (p$23 != null) {
            return p$23.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"x", (Object)this.instance);
    }

    public double tg$24_x() {
        if (tp$24 != null) {
            return (Double)tp$24.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$25_has_item() {
        if (p$25 != null) {
            return p$25.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"has_item", (Object)this.instance);
    }

    public boolean tg$26_has_item() {
        if (tp$26 != null) {
            return (Boolean)tp$26.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"has_item", (Object)this.instance).asBool();
    }

    public ScriptValue pg$27_y() {
        if (p$27 != null) {
            return p$27.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"y", (Object)this.instance);
    }

    public double tg$28_y() {
        if (tp$28 != null) {
            return (Double)tp$28.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"y", (Object)this.instance).asNum();
    }

    public ScriptValue pg$29_exists() {
        if (p$29 != null) {
            return p$29.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"exists", (Object)this.instance);
    }

    public boolean tg$30_exists() {
        if (tp$30 != null) {
            return (Boolean)tp$30.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"exists", (Object)this.instance).asBool();
    }

    public ScriptValue pg$31_progress() {
        if (p$31 != null) {
            return p$31.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"progress", (Object)this.instance);
    }

    public double tg$32_progress() {
        if (tp$32 != null) {
            return (Double)tp$32.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"progress", (Object)this.instance).asNum();
    }

    public ScriptValue pg$33_z() {
        if (p$33 != null) {
            return p$33.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"z", (Object)this.instance);
    }

    public double tg$34_z() {
        if (tp$34 != null) {
            return (Double)tp$34.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"z", (Object)this.instance).asNum();
    }

    public ScriptValue pg$35_block() {
        if (p$35 != null) {
            return p$35.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$36_slot_count() {
        if (p$36 != null) {
            return p$36.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"slot_count", (Object)this.instance);
    }

    public double tg$37_slot_count() {
        if (tp$37 != null) {
            return (Double)tp$37.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"slot_count", (Object)this.instance).asNum();
    }

    public ScriptValue pg$38_height() {
        if (p$38 != null) {
            return p$38.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"height", (Object)this.instance);
    }

    public double tg$39_height() {
        if (tp$39 != null) {
            return (Double)tp$39.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Belt", (String)"height", (Object)this.instance).asNum();
    }

    public PolyClassBelt(Object object) {
        this.instance = object;
    }

    public static PolyClassBelt of(Object object) {
        return new PolyClassBelt(object);
    }

    public static PolyClassBelt ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Belt")) {
            return new PolyClassBelt(object);
        }
        return null;
    }
}
