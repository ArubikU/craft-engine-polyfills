/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
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

public class PolyClassEntity_v2 {
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
    private static volatile PolyType.PropertyHandler p$37;
    private static volatile PolyType.PropertyHandler p$38;
    private static volatile PolyType.TypedPropertyHandler tp$39;
    private static volatile PolyType.PropertyHandler p$40;
    private static volatile PolyType.TypedPropertyHandler tp$41;
    private static volatile PolyType.PropertyHandler p$42;
    private static volatile PolyType.TypedPropertyHandler tp$43;
    private static volatile PolyType.PropertyHandler p$44;
    private static volatile PolyType.TypedPropertyHandler tp$45;
    private static volatile PolyType.PropertyHandler p$46;
    private static volatile PolyType.TypedPropertyHandler tp$47;
    private static volatile PolyType.PropertyHandler p$48;
    private static volatile PolyType.TypedPropertyHandler tp$49;
    private static volatile PolyType.PropertyHandler p$50;
    private static volatile PolyType.TypedPropertyHandler tp$51;
    private static volatile PolyType.PropertyHandler p$52;
    private static volatile PolyType.TypedPropertyHandler tp$53;
    private static volatile PolyType.PropertyHandler p$54;
    private static volatile PolyType.TypedPropertyHandler tp$55;
    private static volatile PolyType.PropertyHandler p$56;
    private static volatile PolyType.PropertyHandler p$57;
    private static volatile PolyType.TypedPropertyHandler tp$58;
    private static volatile PolyType.PropertyHandler p$59;
    private static volatile PolyType.TypedPropertyHandler tp$60;
    private static volatile PolyType.PropertyHandler p$61;
    private static volatile PolyType.TypedPropertyHandler tp$62;
    private static volatile PolyType.PropertyHandler p$63;
    private static volatile PolyType.TypedPropertyHandler tp$64;
    private static volatile PolyType.PropertyHandler p$65;
    private static volatile PolyType.TypedPropertyHandler tp$66;
    private static volatile PolyType.PropertyHandler p$67;
    private static volatile PolyType.TypedPropertyHandler tp$68;
    private static volatile PolyType.PropertyHandler p$69;
    private static volatile PolyType.TypedPropertyHandler tp$70;
    private static volatile PolyType.PropertyHandler p$71;
    private static volatile PolyType.TypedPropertyHandler tp$72;
    private static volatile PolyType.PropertyHandler p$73;

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
        tp$27 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"velocity_z", (String)"D");
        p$28 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"type");
        tp$29 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"type", (String)"S");
        p$30 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"velocity_x");
        tp$31 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"velocity_x", (String)"D");
        p$32 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"velocity_y");
        tp$33 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"velocity_y", (String)"D");
        p$34 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"uuid");
        tp$35 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"uuid", (String)"S");
        p$36 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"world");
        p$37 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"pos");
        p$38 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_alive");
        tp$39 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"is_alive", (String)"Z");
        p$40 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"pitch");
        tp$41 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"pitch", (String)"D");
        p$42 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_animal");
        tp$43 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"is_animal", (String)"Z");
        p$44 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_sprinting");
        tp$45 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"is_sprinting", (String)"Z");
        p$46 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_in_water");
        tp$47 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"is_in_water", (String)"Z");
        p$48 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_sneaking");
        tp$49 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"is_sneaking", (String)"Z");
        p$50 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_invisible");
        tp$51 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"is_invisible", (String)"Z");
        p$52 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_on_ground");
        tp$53 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"is_on_ground", (String)"Z");
        p$54 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_silent");
        tp$55 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"is_silent", (String)"Z");
        p$56 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"velocity");
        p$57 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"tick_age");
        tp$58 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"tick_age", (String)"D");
        p$59 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"fall_distance");
        tp$60 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"fall_distance", (String)"D");
        p$61 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"is_living");
        tp$62 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"is_living", (String)"Z");
        p$63 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"yaw");
        tp$64 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"yaw", (String)"D");
        p$65 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"x");
        tp$66 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"x", (String)"D");
        p$67 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"name");
        tp$68 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"name", (String)"S");
        p$69 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"y");
        tp$70 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"y", (String)"D");
        p$71 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"z");
        tp$72 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Entity", (String)"z", (String)"D");
        p$73 = PolyClassRuntime.resolvePropertyHandler((String)"Entity", (String)"location");
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

    public double tg$27_velocity_z() {
        if (tp$27 != null) {
            return (Double)tp$27.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"velocity_z", (Object)this.instance).asNum();
    }

    public ScriptValue pg$28_type() {
        if (p$28 != null) {
            return p$28.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"type", (Object)this.instance);
    }

    public String tg$29_type() {
        if (tp$29 != null) {
            return (String)tp$29.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"type", (Object)this.instance).asStr();
    }

    public ScriptValue pg$30_velocity_x() {
        if (p$30 != null) {
            return p$30.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"velocity_x", (Object)this.instance);
    }

    public double tg$31_velocity_x() {
        if (tp$31 != null) {
            return (Double)tp$31.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"velocity_x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$32_velocity_y() {
        if (p$32 != null) {
            return p$32.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"velocity_y", (Object)this.instance);
    }

    public double tg$33_velocity_y() {
        if (tp$33 != null) {
            return (Double)tp$33.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"velocity_y", (Object)this.instance).asNum();
    }

    public ScriptValue pg$34_uuid() {
        if (p$34 != null) {
            return p$34.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"uuid", (Object)this.instance);
    }

    public String tg$35_uuid() {
        if (tp$35 != null) {
            return (String)tp$35.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"uuid", (Object)this.instance).asStr();
    }

    public ScriptValue pg$36_world() {
        if (p$36 != null) {
            return p$36.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$37_pos() {
        if (p$37 != null) {
            return p$37.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"pos", (Object)this.instance);
    }

    public ScriptValue pg$38_is_alive() {
        if (p$38 != null) {
            return p$38.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_alive", (Object)this.instance);
    }

    public boolean tg$39_is_alive() {
        if (tp$39 != null) {
            return (Boolean)tp$39.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_alive", (Object)this.instance).asBool();
    }

    public ScriptValue pg$40_pitch() {
        if (p$40 != null) {
            return p$40.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"pitch", (Object)this.instance);
    }

    public double tg$41_pitch() {
        if (tp$41 != null) {
            return (Double)tp$41.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"pitch", (Object)this.instance).asNum();
    }

    public ScriptValue pg$42_is_animal() {
        if (p$42 != null) {
            return p$42.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_animal", (Object)this.instance);
    }

    public boolean tg$43_is_animal() {
        if (tp$43 != null) {
            return (Boolean)tp$43.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_animal", (Object)this.instance).asBool();
    }

    public ScriptValue pg$44_is_sprinting() {
        if (p$44 != null) {
            return p$44.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_sprinting", (Object)this.instance);
    }

    public boolean tg$45_is_sprinting() {
        if (tp$45 != null) {
            return (Boolean)tp$45.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_sprinting", (Object)this.instance).asBool();
    }

    public ScriptValue pg$46_is_in_water() {
        if (p$46 != null) {
            return p$46.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_in_water", (Object)this.instance);
    }

    public boolean tg$47_is_in_water() {
        if (tp$47 != null) {
            return (Boolean)tp$47.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_in_water", (Object)this.instance).asBool();
    }

    public ScriptValue pg$48_is_sneaking() {
        if (p$48 != null) {
            return p$48.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_sneaking", (Object)this.instance);
    }

    public boolean tg$49_is_sneaking() {
        if (tp$49 != null) {
            return (Boolean)tp$49.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_sneaking", (Object)this.instance).asBool();
    }

    public ScriptValue pg$50_is_invisible() {
        if (p$50 != null) {
            return p$50.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_invisible", (Object)this.instance);
    }

    public boolean tg$51_is_invisible() {
        if (tp$51 != null) {
            return (Boolean)tp$51.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_invisible", (Object)this.instance).asBool();
    }

    public ScriptValue pg$52_is_on_ground() {
        if (p$52 != null) {
            return p$52.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_on_ground", (Object)this.instance);
    }

    public boolean tg$53_is_on_ground() {
        if (tp$53 != null) {
            return (Boolean)tp$53.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_on_ground", (Object)this.instance).asBool();
    }

    public ScriptValue pg$54_is_silent() {
        if (p$54 != null) {
            return p$54.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_silent", (Object)this.instance);
    }

    public boolean tg$55_is_silent() {
        if (tp$55 != null) {
            return (Boolean)tp$55.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_silent", (Object)this.instance).asBool();
    }

    public ScriptValue pg$56_velocity() {
        if (p$56 != null) {
            return p$56.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"velocity", (Object)this.instance);
    }

    public ScriptValue pg$57_tick_age() {
        if (p$57 != null) {
            return p$57.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"tick_age", (Object)this.instance);
    }

    public double tg$58_tick_age() {
        if (tp$58 != null) {
            return (Double)tp$58.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"tick_age", (Object)this.instance).asNum();
    }

    public ScriptValue pg$59_fall_distance() {
        if (p$59 != null) {
            return p$59.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"fall_distance", (Object)this.instance);
    }

    public double tg$60_fall_distance() {
        if (tp$60 != null) {
            return (Double)tp$60.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"fall_distance", (Object)this.instance).asNum();
    }

    public ScriptValue pg$61_is_living() {
        if (p$61 != null) {
            return p$61.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_living", (Object)this.instance);
    }

    public boolean tg$62_is_living() {
        if (tp$62 != null) {
            return (Boolean)tp$62.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"is_living", (Object)this.instance).asBool();
    }

    public ScriptValue pg$63_yaw() {
        if (p$63 != null) {
            return p$63.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"yaw", (Object)this.instance);
    }

    public double tg$64_yaw() {
        if (tp$64 != null) {
            return (Double)tp$64.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"yaw", (Object)this.instance).asNum();
    }

    public ScriptValue pg$65_x() {
        if (p$65 != null) {
            return p$65.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"x", (Object)this.instance);
    }

    public double tg$66_x() {
        if (tp$66 != null) {
            return (Double)tp$66.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$67_name() {
        if (p$67 != null) {
            return p$67.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"name", (Object)this.instance);
    }

    public String tg$68_name() {
        if (tp$68 != null) {
            return (String)tp$68.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"name", (Object)this.instance).asStr();
    }

    public ScriptValue pg$69_y() {
        if (p$69 != null) {
            return p$69.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"y", (Object)this.instance);
    }

    public double tg$70_y() {
        if (tp$70 != null) {
            return (Double)tp$70.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"y", (Object)this.instance).asNum();
    }

    public ScriptValue pg$71_z() {
        if (p$71 != null) {
            return p$71.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"z", (Object)this.instance);
    }

    public double tg$72_z() {
        if (tp$72 != null) {
            return (Double)tp$72.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"z", (Object)this.instance).asNum();
    }

    public ScriptValue pg$73_location() {
        if (p$73 != null) {
            return p$73.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Entity", (String)"location", (Object)this.instance);
    }

    public PolyClassEntity_v2(Object object) {
        this.instance = object;
    }

    public static PolyClassEntity_v2 of(Object object) {
        return new PolyClassEntity_v2(object);
    }

    public static PolyClassEntity_v2 ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Entity")) {
            return new PolyClassEntity_v2(object);
        }
        return null;
    }
}
