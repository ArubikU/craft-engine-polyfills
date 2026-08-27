/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler6
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

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
    private static volatile PolyType.PropertyHandler p$17;
    private static volatile PolyType.PropertyHandler p$18;
    private static volatile PolyType.PropertyHandler p$19;
    private static volatile PolyType.PropertyHandler p$20;
    private static volatile PolyType.PropertyHandler p$21;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"get_block", (String)"DDD:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionWorld", (String)"get_block");
        h$2 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"real_pos", (String)"DDD:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionWorld", (String)"real_pos");
        h$4 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"entities", (String)":R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionWorld", (String)"entities");
        h$6 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"ContraptionWorld", (String)"blocks", (String)":R");
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
        p$17 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"roll");
        p$18 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"scale");
        p$19 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"block_count");
        p$20 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"pitch");
        p$21 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionWorld", (String)"yaw");
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
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance);
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

    public ScriptValue pg$17_roll() {
        if (p$17 != null) {
            return p$17.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"roll", (Object)this.instance);
    }

    public ScriptValue pg$18_scale() {
        if (p$18 != null) {
            return p$18.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"scale", (Object)this.instance);
    }

    public ScriptValue pg$19_block_count() {
        if (p$19 != null) {
            return p$19.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"block_count", (Object)this.instance);
    }

    public ScriptValue pg$20_pitch() {
        if (p$20 != null) {
            return p$20.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"pitch", (Object)this.instance);
    }

    public ScriptValue pg$21_yaw() {
        if (p$21 != null) {
            return p$21.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionWorld", (String)"yaw", (Object)this.instance);
    }

    public PolyClassContraptionWorld(Object object) {
        super(object);
    }

    public static PolyClassContraptionWorld of(Object object) {
        return new PolyClassContraptionWorld(object);
    }
}
