/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassEntity_v3 {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler3 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler2 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler3 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler2 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.MethodHandler m$10;
    private static volatile PolyType.TypedMethodHandler0 h$11;
    private static volatile PolyType.MethodHandler m$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler0 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.TypedMethodHandler3 h$16;
    private static volatile PolyType.MethodHandler m$17;
    private static volatile PolyType.TypedMethodHandler3 h$18;
    private static volatile PolyType.MethodHandler m$19;
    private static volatile PolyType.TypedMethodHandler1 h$20;
    private static volatile PolyType.MethodHandler m$21;
    private static volatile PolyType.TypedMethodHandler0 h$22;
    private static volatile PolyType.MethodHandler m$23;
    private static volatile PolyType.TypedMethodHandler1 h$24;
    private static volatile PolyType.MethodHandler m$25;
    private static volatile PolyType.PropertyHandler p$26;
    private static volatile PolyType.PropertyHandler p$27;
    private static volatile PolyType.PropertyHandler p$28;
    private static volatile PolyType.PropertyHandler p$29;
    private static volatile PolyType.PropertyHandler p$30;
    private static volatile PolyType.PropertyHandler p$31;
    private static volatile PolyType.PropertyHandler p$32;
    private static volatile PolyType.PropertyHandler p$33;
    private static volatile PolyType.PropertyHandler p$34;
    private static volatile PolyType.PropertyHandler p$35;
    private static volatile PolyType.PropertyHandler p$36;
    private static volatile PolyType.PropertyHandler p$37;
    private static volatile PolyType.PropertyHandler p$38;
    private static volatile PolyType.PropertyHandler p$39;
    private static volatile PolyType.PropertyHandler p$40;
    private static volatile PolyType.PropertyHandler p$41;
    private static volatile PolyType.PropertyHandler p$42;
    private static volatile PolyType.PropertyHandler p$43;
    private static volatile PolyType.PropertyHandler p$44;
    private static volatile PolyType.PropertyHandler p$45;
    private static volatile PolyType.PropertyHandler p$46;
    private static volatile PolyType.PropertyHandler p$47;
    private static volatile PolyType.PropertyHandler p$48;
    private static volatile PolyType.PropertyHandler p$49;
    private static volatile PolyType.PropertyHandler p$50;
    private static volatile PolyType.PropertyHandler p$51;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"remove_potion_effect", (String)"S:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"remove_potion_effect");
        h$2 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"add_potion_effect", (String)"SDD:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"add_potion_effect");
        h$4 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"has_typed", (String)"SS:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"has_typed");
        h$6 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"teleport", (String)"DDD:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"teleport");
        h$8 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"get_typed", (String)"SS:R");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"get_typed");
        m$10 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"distance_to");
        h$11 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"kill", (String)":Z");
        m$12 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"kill");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"push");
        h$14 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"remove", (String)":Z");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"remove");
        h$16 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"set_velocity", (String)"DDD:Z");
        m$17 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"set_velocity");
        h$18 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"set_typed", (String)"SSR:Z");
        m$19 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"set_typed");
        h$20 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"has_potion_effect", (String)"S:Z");
        m$21 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"has_potion_effect");
        h$22 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"reset_fall_distance", (String)":Z");
        m$23 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"reset_fall_distance");
        h$24 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Entity", (String)"teleport_to", (String)"R:Z");
        m$25 = PolyClassRuntime.resolveMethodHandler((String)"Entity", (String)"teleport_to");
        p$26 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"velocity_z");
        p$27 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"type");
        p$28 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"velocity_x");
        p$29 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"velocity_y");
        p$30 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"uuid");
        p$31 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"world");
        p$32 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"pos");
        p$33 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_alive");
        p$34 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"pitch");
        p$35 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_animal");
        p$36 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_sprinting");
        p$37 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_in_water");
        p$38 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_sneaking");
        p$39 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_invisible");
        p$40 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_on_ground");
        p$41 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_silent");
        p$42 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"velocity");
        p$43 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"tick_age");
        p$44 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"fall_distance");
        p$45 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_living");
        p$46 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"yaw");
        p$47 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"x");
        p$48 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"name");
        p$49 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"y");
        p$50 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"z");
        p$51 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"location");
    }

    public boolean tm$0_remove_potion_effect(String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"remove_potion_effect", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_remove_potion_effect(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"remove_potion_effect", (Object)this.instance, (List)list);
    }

    public boolean tm$2_add_potion_effect(String string, double d, double d2) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)string, (Object)d, (Object)d2);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"add_potion_effect", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d), ScriptValue.of((double)d2)}).asBool();
    }

    public ScriptValue um$3_add_potion_effect(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"add_potion_effect", (Object)this.instance, (List)list);
    }

    public boolean tm$4_has_typed(String string, String string2) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"has_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)}).asBool();
    }

    public ScriptValue um$5_has_typed(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"has_typed", (Object)this.instance, (List)list);
    }

    public boolean tm$6_teleport(double d, double d2, double d3) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"teleport", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$7_teleport(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"teleport", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$8_get_typed(String string, String string2) {
        if (h$8 != null) {
            return (ScriptValue)h$8.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"get_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)});
    }

    public ScriptValue um$9_get_typed(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"get_typed", (Object)this.instance, (List)list);
    }

    public ScriptValue um$10_distance_to(List list) {
        if (m$10 != null) {
            return m$10.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"distance_to", (Object)this.instance, (List)list);
    }

    public boolean tm$11_kill() {
        if (h$11 != null) {
            return (Boolean)h$11.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"kill", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$12_kill(List list) {
        if (m$12 != null) {
            return m$12.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"kill", (Object)this.instance, (List)list);
    }

    public ScriptValue um$13_push(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"push", (Object)this.instance, (List)list);
    }

    public boolean tm$14_remove() {
        if (h$14 != null) {
            return (Boolean)h$14.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"remove", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$15_remove(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"remove", (Object)this.instance, (List)list);
    }

    public boolean tm$16_set_velocity(double d, double d2, double d3) {
        if (h$16 != null) {
            return (Boolean)h$16.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"set_velocity", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$17_set_velocity(List list) {
        if (m$17 != null) {
            return m$17.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"set_velocity", (Object)this.instance, (List)list);
    }

    public boolean tm$18_set_typed(String string, String string2, ScriptValue scriptValue) {
        if (h$18 != null) {
            return (Boolean)h$18.call(this.instance, (Object)string, (Object)string2, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"set_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2), scriptValue}).asBool();
    }

    public ScriptValue um$19_set_typed(List list) {
        if (m$19 != null) {
            return m$19.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"set_typed", (Object)this.instance, (List)list);
    }

    public boolean tm$20_has_potion_effect(String string) {
        if (h$20 != null) {
            return (Boolean)h$20.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"has_potion_effect", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$21_has_potion_effect(List list) {
        if (m$21 != null) {
            return m$21.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"has_potion_effect", (Object)this.instance, (List)list);
    }

    public boolean tm$22_reset_fall_distance() {
        if (h$22 != null) {
            return (Boolean)h$22.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"reset_fall_distance", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$23_reset_fall_distance(List list) {
        if (m$23 != null) {
            return m$23.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"reset_fall_distance", (Object)this.instance, (List)list);
    }

    public boolean tm$24_teleport_to(ScriptValue scriptValue) {
        if (h$24 != null) {
            return (Boolean)h$24.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Entity", (String)"teleport_to", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$25_teleport_to(List list) {
        if (m$25 != null) {
            return m$25.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Entity", (String)"teleport_to", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$26_velocity_z() {
        if (p$26 != null) {
            return p$26.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"velocity_z", (Object)this.instance);
    }

    public ScriptValue pg$27_type() {
        if (p$27 != null) {
            return p$27.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"type", (Object)this.instance);
    }

    public ScriptValue pg$28_velocity_x() {
        if (p$28 != null) {
            return p$28.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"velocity_x", (Object)this.instance);
    }

    public ScriptValue pg$29_velocity_y() {
        if (p$29 != null) {
            return p$29.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"velocity_y", (Object)this.instance);
    }

    public ScriptValue pg$30_uuid() {
        if (p$30 != null) {
            return p$30.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"uuid", (Object)this.instance);
    }

    public ScriptValue pg$31_world() {
        if (p$31 != null) {
            return p$31.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$32_pos() {
        if (p$32 != null) {
            return p$32.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"pos", (Object)this.instance);
    }

    public ScriptValue pg$33_is_alive() {
        if (p$33 != null) {
            return p$33.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_alive", (Object)this.instance);
    }

    public ScriptValue pg$34_pitch() {
        if (p$34 != null) {
            return p$34.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"pitch", (Object)this.instance);
    }

    public ScriptValue pg$35_is_animal() {
        if (p$35 != null) {
            return p$35.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_animal", (Object)this.instance);
    }

    public ScriptValue pg$36_is_sprinting() {
        if (p$36 != null) {
            return p$36.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_sprinting", (Object)this.instance);
    }

    public ScriptValue pg$37_is_in_water() {
        if (p$37 != null) {
            return p$37.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_in_water", (Object)this.instance);
    }

    public ScriptValue pg$38_is_sneaking() {
        if (p$38 != null) {
            return p$38.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_sneaking", (Object)this.instance);
    }

    public ScriptValue pg$39_is_invisible() {
        if (p$39 != null) {
            return p$39.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_invisible", (Object)this.instance);
    }

    public ScriptValue pg$40_is_on_ground() {
        if (p$40 != null) {
            return p$40.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_on_ground", (Object)this.instance);
    }

    public ScriptValue pg$41_is_silent() {
        if (p$41 != null) {
            return p$41.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_silent", (Object)this.instance);
    }

    public ScriptValue pg$42_velocity() {
        if (p$42 != null) {
            return p$42.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"velocity", (Object)this.instance);
    }

    public ScriptValue pg$43_tick_age() {
        if (p$43 != null) {
            return p$43.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"tick_age", (Object)this.instance);
    }

    public ScriptValue pg$44_fall_distance() {
        if (p$44 != null) {
            return p$44.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"fall_distance", (Object)this.instance);
    }

    public ScriptValue pg$45_is_living() {
        if (p$45 != null) {
            return p$45.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_living", (Object)this.instance);
    }

    public ScriptValue pg$46_yaw() {
        if (p$46 != null) {
            return p$46.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"yaw", (Object)this.instance);
    }

    public ScriptValue pg$47_x() {
        if (p$47 != null) {
            return p$47.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"x", (Object)this.instance);
    }

    public ScriptValue pg$48_name() {
        if (p$48 != null) {
            return p$48.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"name", (Object)this.instance);
    }

    public ScriptValue pg$49_y() {
        if (p$49 != null) {
            return p$49.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"y", (Object)this.instance);
    }

    public ScriptValue pg$50_z() {
        if (p$50 != null) {
            return p$50.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"z", (Object)this.instance);
    }

    public ScriptValue pg$51_location() {
        if (p$51 != null) {
            return p$51.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"location", (Object)this.instance);
    }

    public PolyClassEntity_v3(Object object) {
        this.instance = object;
    }

    public static PolyClassEntity_v3 of(Object object) {
        return new PolyClassEntity_v3(object);
    }
}
