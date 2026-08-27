/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler4
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassAnimation {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler0 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler0 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler1 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler3 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler0 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.TypedMethodHandler1 h$16;
    private static volatile PolyType.MethodHandler m$17;
    private static volatile PolyType.TypedMethodHandler1 h$18;
    private static volatile PolyType.MethodHandler m$19;
    private static volatile PolyType.TypedMethodHandler1 h$20;
    private static volatile PolyType.MethodHandler m$21;
    private static volatile PolyType.TypedMethodHandler0 h$22;
    private static volatile PolyType.MethodHandler m$23;
    private static volatile PolyType.TypedMethodHandler1 h$24;
    private static volatile PolyType.MethodHandler m$25;
    private static volatile PolyType.TypedMethodHandler1 h$26;
    private static volatile PolyType.MethodHandler m$27;
    private static volatile PolyType.TypedMethodHandler0 h$28;
    private static volatile PolyType.MethodHandler m$29;
    private static volatile PolyType.TypedMethodHandler4 h$30;
    private static volatile PolyType.MethodHandler m$31;
    private static volatile PolyType.TypedMethodHandler1 h$32;
    private static volatile PolyType.MethodHandler m$33;
    private static volatile PolyType.PropertyHandler p$34;
    private static volatile PolyType.PropertyHandler p$35;
    private static volatile PolyType.PropertyHandler p$36;
    private static volatile PolyType.PropertyHandler p$37;
    private static volatile PolyType.PropertyHandler p$38;
    private static volatile PolyType.PropertyHandler p$39;
    private static volatile PolyType.PropertyHandler p$40;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"play", (String)":R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"play");
        h$2 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"resume", (String)":R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"resume");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"play_from", (String)"D:R");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"play_from");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"remove_child", (String)"R:R");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"remove_child");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"seek", (String)"D:R");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"seek");
        h$10 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"add_child", (String)"R:R");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"add_child");
        h$12 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"keyframe", (String)"DRS:R");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"keyframe");
        h$14 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"pause", (String)":R");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"pause");
        h$16 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"speed", (String)"D:R");
        m$17 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"speed");
        h$18 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"interpolation", (String)"D:R");
        m$19 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"interpolation");
        h$20 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"on_loop", (String)"S:R");
        m$21 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"on_loop");
        h$22 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"stop", (String)":Z");
        m$23 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"stop");
        h$24 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"loop", (String)"Z:R");
        m$25 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"loop");
        h$26 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"max_loops", (String)"D:R");
        m$27 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"max_loops");
        h$28 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"reset", (String)":R");
        m$29 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"reset");
        h$30 = (PolyType.TypedMethodHandler4)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"sound_at", (String)"DRDD:R");
        m$31 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"sound_at");
        h$32 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Animation", (String)"on_end", (String)"S:R");
        m$33 = PolyClassRuntime.resolveMethodHandler((String)"Animation", (String)"on_end");
        p$34 = PolyClassRuntime.resolvePropertyHandler((String)"Animation", (String)"duration");
        p$35 = PolyClassRuntime.resolvePropertyHandler((String)"Animation", (String)"paused");
        p$36 = PolyClassRuntime.resolvePropertyHandler((String)"Animation", (String)"looping");
        p$37 = PolyClassRuntime.resolvePropertyHandler((String)"Animation", (String)"progress");
        p$38 = PolyClassRuntime.resolvePropertyHandler((String)"Animation", (String)"playing");
        p$39 = PolyClassRuntime.resolvePropertyHandler((String)"Animation", (String)"tick");
        p$40 = PolyClassRuntime.resolvePropertyHandler((String)"Animation", (String)"speed");
    }

    public ScriptValue tm$0_play() {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"play", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$1_play(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"play", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_resume() {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"resume", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$3_resume(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"resume", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_play_from(double d) {
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"play_from", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$5_play_from(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"play_from", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_remove_child(ScriptValue scriptValue) {
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"remove_child", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$7_remove_child(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"remove_child", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$8_seek(double d) {
        if (h$8 != null) {
            return (ScriptValue)h$8.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"seek", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$9_seek(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"seek", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$10_add_child(ScriptValue scriptValue) {
        if (h$10 != null) {
            return (ScriptValue)h$10.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"add_child", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$11_add_child(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"add_child", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$12_keyframe(double d, ScriptValue scriptValue, String string) {
        if (h$12 != null) {
            return (ScriptValue)h$12.call(this.instance, (Object)d, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"keyframe", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), scriptValue, ScriptValue.of((String)string)});
    }

    public ScriptValue um$13_keyframe(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"keyframe", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$14_pause() {
        if (h$14 != null) {
            return (ScriptValue)h$14.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"pause", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$15_pause(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"pause", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$16_speed(double d) {
        if (h$16 != null) {
            return (ScriptValue)h$16.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"speed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$17_speed(List list) {
        if (m$17 != null) {
            return m$17.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"speed", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$18_interpolation(double d) {
        if (h$18 != null) {
            return (ScriptValue)h$18.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"interpolation", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$19_interpolation(List list) {
        if (m$19 != null) {
            return m$19.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"interpolation", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$20_on_loop(String string) {
        if (h$20 != null) {
            return (ScriptValue)h$20.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"on_loop", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$21_on_loop(List list) {
        if (m$21 != null) {
            return m$21.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"on_loop", (Object)this.instance, (List)list);
    }

    public boolean tm$22_stop() {
        if (h$22 != null) {
            return (Boolean)h$22.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"stop", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$23_stop(List list) {
        if (m$23 != null) {
            return m$23.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"stop", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$24_loop(boolean bl) {
        if (h$24 != null) {
            return (ScriptValue)h$24.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"loop", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)});
    }

    public ScriptValue um$25_loop(List list) {
        if (m$25 != null) {
            return m$25.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"loop", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$26_max_loops(double d) {
        if (h$26 != null) {
            return (ScriptValue)h$26.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"max_loops", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$27_max_loops(List list) {
        if (m$27 != null) {
            return m$27.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"max_loops", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$28_reset() {
        if (h$28 != null) {
            return (ScriptValue)h$28.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"reset", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$29_reset(List list) {
        if (m$29 != null) {
            return m$29.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"reset", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$30_sound_at(double d, ScriptValue scriptValue, double d2, double d3) {
        if (h$30 != null) {
            return (ScriptValue)h$30.call(this.instance, (Object)d, (Object)scriptValue, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"sound_at", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), scriptValue, ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$31_sound_at(List list) {
        if (m$31 != null) {
            return m$31.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"sound_at", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$32_on_end(String string) {
        if (h$32 != null) {
            return (ScriptValue)h$32.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Animation", (String)"on_end", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$33_on_end(List list) {
        if (m$33 != null) {
            return m$33.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Animation", (String)"on_end", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$34_duration() {
        if (p$34 != null) {
            return p$34.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animation", (String)"duration", (Object)this.instance);
    }

    public ScriptValue pg$35_paused() {
        if (p$35 != null) {
            return p$35.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animation", (String)"paused", (Object)this.instance);
    }

    public ScriptValue pg$36_looping() {
        if (p$36 != null) {
            return p$36.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animation", (String)"looping", (Object)this.instance);
    }

    public ScriptValue pg$37_progress() {
        if (p$37 != null) {
            return p$37.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animation", (String)"progress", (Object)this.instance);
    }

    public ScriptValue pg$38_playing() {
        if (p$38 != null) {
            return p$38.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animation", (String)"playing", (Object)this.instance);
    }

    public ScriptValue pg$39_tick() {
        if (p$39 != null) {
            return p$39.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animation", (String)"tick", (Object)this.instance);
    }

    public ScriptValue pg$40_speed() {
        if (p$40 != null) {
            return p$40.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animation", (String)"speed", (Object)this.instance);
    }

    public PolyClassAnimation(Object object) {
        this.instance = object;
    }

    public static PolyClassAnimation of(Object object) {
        return new PolyClassAnimation(object);
    }
}
