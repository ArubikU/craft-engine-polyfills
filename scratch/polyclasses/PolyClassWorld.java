/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler4
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler5
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler6
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler9
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

public class PolyClassWorld {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler4 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler2 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler4 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler4 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler9 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler4 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.TypedMethodHandler6 h$16;
    private static volatile PolyType.MethodHandler m$17;
    private static volatile PolyType.TypedMethodHandler5 h$18;
    private static volatile PolyType.MethodHandler m$19;
    private static volatile PolyType.TypedMethodHandler3 h$20;
    private static volatile PolyType.MethodHandler m$21;
    private static volatile PolyType.TypedMethodHandler3 h$22;
    private static volatile PolyType.MethodHandler m$23;
    private static volatile PolyType.TypedMethodHandler3 h$24;
    private static volatile PolyType.MethodHandler m$25;
    private static volatile PolyType.PropertyHandler p$26;
    private static volatile PolyType.TypedPropertyHandler tp$27;
    private static volatile PolyType.PropertyHandler p$28;
    private static volatile PolyType.TypedPropertyHandler tp$29;
    private static volatile PolyType.PropertyHandler p$30;
    private static volatile PolyType.TypedPropertyHandler tp$31;
    private static volatile PolyType.PropertyHandler p$32;
    private static volatile PolyType.TypedPropertyHandler tp$33;
    private static volatile PolyType.PropertyHandler p$34;
    private static volatile PolyType.TypedPropertyHandler tp$35;
    private static volatile PolyType.PropertyHandler p$36;
    private static volatile PolyType.TypedPropertyHandler tp$37;
    private static volatile PolyType.PropertyHandler p$38;
    private static volatile PolyType.TypedPropertyHandler tp$39;
    private static volatile PolyType.PropertyHandler p$40;
    private static volatile PolyType.TypedPropertyHandler tp$41;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler4)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"get_block", (String)"DDDZ:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"get_block");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"broadcast_actionbar", (String)"S:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"broadcast_actionbar");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"has_typed", (String)"S:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"has_typed");
        h$6 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"get_typed", (String)"SS:R");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"get_typed");
        h$8 = (PolyType.TypedMethodHandler4)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"spawn_entity", (String)"SDDD:R");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"spawn_entity");
        h$10 = (PolyType.TypedMethodHandler4)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"entities_in_range", (String)"DDDD:R");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"entities_in_range");
        h$12 = (PolyType.TypedMethodHandler9)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"spawn_particle", (String)"SDDDDDDDD:R");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"spawn_particle");
        h$14 = (PolyType.TypedMethodHandler4)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"set_block", (String)"DDDS:Z");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"set_block");
        h$16 = (PolyType.TypedMethodHandler6)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"play_sound", (String)"DDDSDD:R");
        m$17 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"play_sound");
        h$18 = (PolyType.TypedMethodHandler5)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"broadcast_title", (String)"SSDDD:Z");
        m$19 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"broadcast_title");
        h$20 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"set_typed", (String)"SSR:Z");
        m$21 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"set_typed");
        h$22 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"location", (String)"DDD:R");
        m$23 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"location");
        h$24 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"World", (String)"get_light", (String)"DDD:R");
        m$25 = PolyClassRuntime.resolveMethodHandler((String)"World", (String)"get_light");
        p$26 = PolyClassRuntime.resolvePropertyHandler((String)"World", (String)"is_night");
        tp$27 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"World", (String)"is_night", (String)"Z");
        p$28 = PolyClassRuntime.resolvePropertyHandler((String)"World", (String)"seed");
        tp$29 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"World", (String)"seed", (String)"D");
        p$30 = PolyClassRuntime.resolvePropertyHandler((String)"World", (String)"name");
        tp$31 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"World", (String)"name", (String)"S");
        p$32 = PolyClassRuntime.resolvePropertyHandler((String)"World", (String)"is_day");
        tp$33 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"World", (String)"is_day", (String)"Z");
        p$34 = PolyClassRuntime.resolvePropertyHandler((String)"World", (String)"time");
        tp$35 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"World", (String)"time", (String)"D");
        p$36 = PolyClassRuntime.resolvePropertyHandler((String)"World", (String)"day_time");
        tp$37 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"World", (String)"day_time", (String)"D");
        p$38 = PolyClassRuntime.resolvePropertyHandler((String)"World", (String)"is_raining");
        tp$39 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"World", (String)"is_raining", (String)"Z");
        p$40 = PolyClassRuntime.resolvePropertyHandler((String)"World", (String)"is_thundering");
        tp$41 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"World", (String)"is_thundering", (String)"Z");
    }

    public ScriptValue tm$0_get_block(double d, double d2, double d3, boolean bl) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)d, (Object)d2, (Object)d3, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"get_block", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3), ScriptValue.of((boolean)bl)});
    }

    public ScriptValue um$1_get_block(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"get_block", (Object)this.instance, (List)list);
    }

    public boolean tm$2_broadcast_actionbar(String string) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"broadcast_actionbar", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$3_broadcast_actionbar(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"broadcast_actionbar", (Object)this.instance, (List)list);
    }

    public boolean tm$4_has_typed(String string) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"has_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$5_has_typed(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"has_typed", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_get_typed(String string, String string2) {
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"get_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)});
    }

    public ScriptValue um$7_get_typed(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"get_typed", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$8_spawn_entity(String string, double d, double d2, double d3) {
        if (h$8 != null) {
            return (ScriptValue)h$8.call(this.instance, (Object)string, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"spawn_entity", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$9_spawn_entity(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"spawn_entity", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$10_entities_in_range(double d, double d2, double d3, double d4) {
        if (h$10 != null) {
            return (ScriptValue)h$10.call(this.instance, (Object)d, (Object)d2, (Object)d3, (Object)d4);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"entities_in_range", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3), ScriptValue.of((double)d4)});
    }

    public ScriptValue um$11_entities_in_range(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"entities_in_range", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$12_spawn_particle(String string, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8) {
        if (h$12 != null) {
            return (ScriptValue)h$12.call(this.instance, (Object)string, (Object)d, (Object)d2, (Object)d3, (Object)d4, (Object)d5, (Object)d6, (Object)d7, (Object)d8);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"spawn_particle", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3), ScriptValue.of((double)d4), ScriptValue.of((double)d5), ScriptValue.of((double)d6), ScriptValue.of((double)d7), ScriptValue.of((double)d8)});
    }

    public ScriptValue um$13_spawn_particle(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"spawn_particle", (Object)this.instance, (List)list);
    }

    public boolean tm$14_set_block(double d, double d2, double d3, String string) {
        if (h$14 != null) {
            return (Boolean)h$14.call(this.instance, (Object)d, (Object)d2, (Object)d3, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"set_block", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3), ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$15_set_block(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"set_block", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$16_play_sound(double d, double d2, double d3, String string, double d4, double d5) {
        if (h$16 != null) {
            return (ScriptValue)h$16.call(this.instance, (Object)d, (Object)d2, (Object)d3, (Object)string, (Object)d4, (Object)d5);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"play_sound", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3), ScriptValue.of((String)string), ScriptValue.of((double)d4), ScriptValue.of((double)d5)});
    }

    public ScriptValue um$17_play_sound(List list) {
        if (m$17 != null) {
            return m$17.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"play_sound", (Object)this.instance, (List)list);
    }

    public boolean tm$18_broadcast_title(String string, String string2, double d, double d2, double d3) {
        if (h$18 != null) {
            return (Boolean)h$18.call(this.instance, (Object)string, (Object)string2, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"broadcast_title", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2), ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$19_broadcast_title(List list) {
        if (m$19 != null) {
            return m$19.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"broadcast_title", (Object)this.instance, (List)list);
    }

    public boolean tm$20_set_typed(String string, String string2, ScriptValue scriptValue) {
        if (h$20 != null) {
            return (Boolean)h$20.call(this.instance, (Object)string, (Object)string2, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"set_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2), scriptValue}).asBool();
    }

    public ScriptValue um$21_set_typed(List list) {
        if (m$21 != null) {
            return m$21.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"set_typed", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$22_location(double d, double d2, double d3) {
        if (h$22 != null) {
            return (ScriptValue)h$22.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"location", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$23_location(List list) {
        if (m$23 != null) {
            return m$23.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"location", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$24_get_light(double d, double d2, double d3) {
        if (h$24 != null) {
            return (ScriptValue)h$24.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"World", (String)"get_light", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$25_get_light(List list) {
        if (m$25 != null) {
            return m$25.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"World", (String)"get_light", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$26_is_night() {
        if (p$26 != null) {
            return p$26.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"is_night", (Object)this.instance);
    }

    public boolean tg$27_is_night() {
        if (tp$27 != null) {
            return (Boolean)tp$27.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"is_night", (Object)this.instance).asBool();
    }

    public ScriptValue pg$28_seed() {
        if (p$28 != null) {
            return p$28.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"seed", (Object)this.instance);
    }

    public double tg$29_seed() {
        if (tp$29 != null) {
            return (Double)tp$29.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"seed", (Object)this.instance).asNum();
    }

    public ScriptValue pg$30_name() {
        if (p$30 != null) {
            return p$30.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"name", (Object)this.instance);
    }

    public String tg$31_name() {
        if (tp$31 != null) {
            return (String)tp$31.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"name", (Object)this.instance).asStr();
    }

    public ScriptValue pg$32_is_day() {
        if (p$32 != null) {
            return p$32.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"is_day", (Object)this.instance);
    }

    public boolean tg$33_is_day() {
        if (tp$33 != null) {
            return (Boolean)tp$33.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"is_day", (Object)this.instance).asBool();
    }

    public ScriptValue pg$34_time() {
        if (p$34 != null) {
            return p$34.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"time", (Object)this.instance);
    }

    public double tg$35_time() {
        if (tp$35 != null) {
            return (Double)tp$35.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"time", (Object)this.instance).asNum();
    }

    public ScriptValue pg$36_day_time() {
        if (p$36 != null) {
            return p$36.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"day_time", (Object)this.instance);
    }

    public double tg$37_day_time() {
        if (tp$37 != null) {
            return (Double)tp$37.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"day_time", (Object)this.instance).asNum();
    }

    public ScriptValue pg$38_is_raining() {
        if (p$38 != null) {
            return p$38.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"is_raining", (Object)this.instance);
    }

    public boolean tg$39_is_raining() {
        if (tp$39 != null) {
            return (Boolean)tp$39.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"is_raining", (Object)this.instance).asBool();
    }

    public ScriptValue pg$40_is_thundering() {
        if (p$40 != null) {
            return p$40.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"is_thundering", (Object)this.instance);
    }

    public boolean tg$41_is_thundering() {
        if (tp$41 != null) {
            return (Boolean)tp$41.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"World", (String)"is_thundering", (Object)this.instance).asBool();
    }

    public PolyClassWorld(Object object) {
        this.instance = object;
    }

    public static PolyClassWorld of(Object object) {
        return new PolyClassWorld(object);
    }

    public static PolyClassWorld ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("World")) {
            return new PolyClassWorld(object);
        }
        return null;
    }
}
