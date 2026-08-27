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
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler6
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassContraptionWorld
extends PolyClassWorld {
    private static volatile PolyType.TypedMethodHandler3 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler3 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler0 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler0 h$6;
    private static volatile PolyType.TypeCodec c$6_r;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler3 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler3 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler6 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.PropertyHandler p$14;
    private static volatile PolyType.PropertyHandler p$15;
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
    private static volatile PolyType.TypedPropertyHandler tp$27;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"get_block", (String)"DDD:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionWorld", (String)"get_block");
        h$2 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"real_pos", (String)"DDD:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionWorld", (String)"real_pos");
        h$4 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"entities", (String)":R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionWorld", (String)"entities");
        h$6 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"blocks", (String)":L");
        c$6_r = PolyClassRuntime.resolveListCodec((String)"ContraptionWorld", (String)"blocks", (int)-1);
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionWorld", (String)"blocks");
        h$8 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"real_block", (String)"DDD:R");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionWorld", (String)"real_block");
        h$10 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"local_block", (String)"DDD:R");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionWorld", (String)"local_block");
        h$12 = (PolyType.TypedMethodHandler6)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"play_sound", (String)"DDDSDD:Z");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionWorld", (String)"play_sound");
        p$14 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"container");
        p$15 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"real_world");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"is_contraption");
        tp$17 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ContraptionWorld", (String)"is_contraption", (String)"Z");
        p$18 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"roll");
        tp$19 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ContraptionWorld", (String)"roll", (String)"D");
        p$20 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"scale");
        tp$21 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ContraptionWorld", (String)"scale", (String)"D");
        p$22 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"block_count");
        tp$23 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ContraptionWorld", (String)"block_count", (String)"D");
        p$24 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"pitch");
        tp$25 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ContraptionWorld", (String)"pitch", (String)"D");
        p$26 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"yaw");
        tp$27 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ContraptionWorld", (String)"yaw", (String)"D");
    }

    public ScriptValue tm$0_get_block(double d, double d2, double d3) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionWorld", (String)"get_block", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    @Override
    public ScriptValue um$1_get_block(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionWorld", (String)"get_block", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_real_pos(double d, double d2, double d3) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionWorld", (String)"real_pos", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$3_real_pos(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionWorld", (String)"real_pos", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_entities() {
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionWorld", (String)"entities", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$5_entities(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionWorld", (String)"entities", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_blocks() {
        if (h$6 != null && c$6_r != null) {
            return c$6_r.encode(h$6.call(this.instance));
        }
        return PolyClassRuntime.genericCall((String)"ContraptionWorld", (String)"blocks", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$7_blocks(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionWorld", (String)"blocks", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$8_real_block(double d, double d2, double d3) {
        if (h$8 != null) {
            return (ScriptValue)h$8.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionWorld", (String)"real_block", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$9_real_block(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionWorld", (String)"real_block", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$10_local_block(double d, double d2, double d3) {
        if (h$10 != null) {
            return (ScriptValue)h$10.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionWorld", (String)"local_block", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$11_local_block(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionWorld", (String)"local_block", (Object)this.instance, (List)list);
    }

    public boolean tm$12_play_sound(double d, double d2, double d3, String string, double d4, double d5) {
        if (h$12 != null) {
            return (Boolean)h$12.call(this.instance, (Object)d, (Object)d2, (Object)d3, (Object)string, (Object)d4, (Object)d5);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionWorld", (String)"play_sound", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3), ScriptValue.of((String)string), ScriptValue.of((double)d4), ScriptValue.of((double)d5)}).asBool();
    }

    public ScriptValue um$13_play_sound(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionWorld", (String)"play_sound", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$14_container() {
        if (p$14 != null) {
            return p$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"container", (Object)this.instance);
    }

    public ScriptValue pg$15_real_world() {
        if (p$15 != null) {
            return p$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"real_world", (Object)this.instance);
    }

    public ScriptValue pg$16_is_contraption() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"is_contraption", (Object)this.instance);
    }

    public boolean tg$17_is_contraption() {
        if (tp$17 != null) {
            return (Boolean)tp$17.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"is_contraption", (Object)this.instance).asBool();
    }

    public ScriptValue pg$18_roll() {
        if (p$18 != null) {
            return p$18.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"roll", (Object)this.instance);
    }

    public double tg$19_roll() {
        if (tp$19 != null) {
            return (Double)tp$19.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"roll", (Object)this.instance).asNum();
    }

    public ScriptValue pg$20_scale() {
        if (p$20 != null) {
            return p$20.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"scale", (Object)this.instance);
    }

    public double tg$21_scale() {
        if (tp$21 != null) {
            return (Double)tp$21.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"scale", (Object)this.instance).asNum();
    }

    public ScriptValue pg$22_block_count() {
        if (p$22 != null) {
            return p$22.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"block_count", (Object)this.instance);
    }

    public double tg$23_block_count() {
        if (tp$23 != null) {
            return (Double)tp$23.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"block_count", (Object)this.instance).asNum();
    }

    public ScriptValue pg$24_pitch() {
        if (p$24 != null) {
            return p$24.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"pitch", (Object)this.instance);
    }

    public double tg$25_pitch() {
        if (tp$25 != null) {
            return (Double)tp$25.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"pitch", (Object)this.instance).asNum();
    }

    public ScriptValue pg$26_yaw() {
        if (p$26 != null) {
            return p$26.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"yaw", (Object)this.instance);
    }

    public double tg$27_yaw() {
        if (tp$27 != null) {
            return (Double)tp$27.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"yaw", (Object)this.instance).asNum();
    }

    public PolyClassContraptionWorld(Object object) {
        super(object);
    }

    public static PolyClassContraptionWorld of(Object object) {
        return new PolyClassContraptionWorld(object);
    }

    public static PolyClassContraptionWorld ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ContraptionWorld")) {
            return new PolyClassContraptionWorld(object);
        }
        return null;
    }
}
