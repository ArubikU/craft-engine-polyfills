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
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler4
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassContraption {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler3 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler4 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler0 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler1 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler0 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.TypedMethodHandler2 h$16;
    private static volatile PolyType.MethodHandler m$17;
    private static volatile PolyType.TypedMethodHandler3 h$18;
    private static volatile PolyType.MethodHandler m$19;
    private static volatile PolyType.TypedMethodHandler0 h$20;
    private static volatile PolyType.MethodHandler m$21;
    private static volatile PolyType.TypedMethodHandler1 h$22;
    private static volatile PolyType.MethodHandler m$23;
    private static volatile PolyType.TypedMethodHandler0 h$24;
    private static volatile PolyType.MethodHandler m$25;
    private static volatile PolyType.TypedMethodHandler1 h$26;
    private static volatile PolyType.MethodHandler m$27;
    private static volatile PolyType.TypedMethodHandler0 h$28;
    private static volatile PolyType.MethodHandler m$29;
    private static volatile PolyType.TypedMethodHandler3 h$30;
    private static volatile PolyType.MethodHandler m$31;
    private static volatile PolyType.TypedMethodHandler1 h$32;
    private static volatile PolyType.MethodHandler m$33;
    private static volatile PolyType.TypedMethodHandler3 h$34;
    private static volatile PolyType.MethodHandler m$35;
    private static volatile PolyType.TypedMethodHandler0 h$36;
    private static volatile PolyType.MethodHandler m$37;
    private static volatile PolyType.TypedMethodHandler3 h$38;
    private static volatile PolyType.MethodHandler m$39;
    private static volatile PolyType.TypedMethodHandler3 h$40;
    private static volatile PolyType.MethodHandler m$41;
    private static volatile PolyType.TypedMethodHandler1 h$42;
    private static volatile PolyType.MethodHandler m$43;
    private static volatile PolyType.TypedMethodHandler3 h$44;
    private static volatile PolyType.MethodHandler m$45;
    private static volatile PolyType.TypedMethodHandler0 h$46;
    private static volatile PolyType.MethodHandler m$47;
    private static volatile PolyType.TypedMethodHandler0 h$48;
    private static volatile PolyType.MethodHandler m$49;
    private static volatile PolyType.TypedMethodHandler3 h$50;
    private static volatile PolyType.MethodHandler m$51;
    private static volatile PolyType.PropertyHandler p$52;
    private static volatile PolyType.PropertyHandler p$53;
    private static volatile PolyType.PropertyHandler p$54;
    private static volatile PolyType.PropertyHandler p$55;
    private static volatile PolyType.PropertyHandler p$56;
    private static volatile PolyType.PropertyHandler p$57;
    private static volatile PolyType.PropertyHandler p$58;
    private static volatile PolyType.PropertyHandler p$59;
    private static volatile PolyType.PropertyHandler p$60;
    private static volatile PolyType.PropertyHandler p$61;
    private static volatile PolyType.PropertyHandler p$62;
    private static volatile PolyType.PropertyHandler p$63;
    private static volatile PolyType.PropertyHandler p$64;
    private static volatile PolyType.PropertyHandler p$65;
    private static volatile PolyType.PropertyHandler p$66;
    private static volatile PolyType.PropertyHandler p$67;
    private static volatile PolyType.PropertyHandler p$68;
    private static volatile PolyType.PropertyHandler p$69;
    private static volatile PolyType.PropertyHandler p$70;
    private static volatile PolyType.PropertyHandler p$71;
    private static volatile PolyType.PropertyHandler p$72;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"get_block", (String)"DDD:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"get_block");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"release", (String)"S:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"release");
        h$4 = (PolyType.TypedMethodHandler4)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"teleport", (String)"DDDD:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"teleport");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"set_yaw", (String)"D:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"set_yaw");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"set_rpm", (String)"D:Z");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"set_rpm");
        h$10 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"get_rpm", (String)":D");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"get_rpm");
        h$12 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"hold", (String)"S:Z");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"hold");
        h$14 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"disassemble", (String)":Z");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"disassemble");
        h$16 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"set_spin", (String)"SD:Z");
        m$17 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"set_spin");
        h$18 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"move", (String)"DDD:Z");
        m$19 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"move");
        h$20 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"has_riders", (String)":Z");
        m$21 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"has_riders");
        h$22 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"set_roll", (String)"D:Z");
        m$23 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"set_roll");
        h$24 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"blocks", (String)":R");
        m$25 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"blocks");
        h$26 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"set_scale", (String)"D:Z");
        m$27 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"set_scale");
        h$28 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"kill", (String)":Z");
        m$29 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"kill");
        h$30 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"set_velocity", (String)"DDD:Z");
        m$31 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"set_velocity");
        h$32 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"set_yaw_rate", (String)"D:Z");
        m$33 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"set_yaw_rate");
        h$34 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"set_angular_velocity", (String)"DDD:Z");
        m$35 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"set_angular_velocity");
        h$36 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"entities", (String)":R");
        m$37 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"entities");
        h$38 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"set_rotation", (String)"DDD:Z");
        m$39 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"set_rotation");
        h$40 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"apply_impulse", (String)"DDD:Z");
        m$41 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"apply_impulse");
        h$42 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"set_pitch", (String)"D:Z");
        m$43 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"set_pitch");
        h$44 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"rotate_by", (String)"DDD:Z");
        m$45 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"rotate_by");
        h$46 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"report_su", (String)":Z");
        m$47 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"report_su");
        h$48 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"is_moving", (String)":Z");
        m$49 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"is_moving");
        h$50 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Contraption", (String)"real_direction", (String)"DDD:R");
        m$51 = PolyClassRuntime.resolveMethodHandler((String)"Contraption", (String)"real_direction");
        p$52 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"container");
        p$53 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"contraption_world");
        p$54 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"roll");
        p$55 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"is_contraption");
        p$56 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"scale");
        p$57 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"weight");
        p$58 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"velocity");
        p$59 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"anchor_entity");
        p$60 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"uuid");
        p$61 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"yaw");
        p$62 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"speed");
        p$63 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"rpm");
        p$64 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"has_anchor_entity");
        p$65 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"real_world");
        p$66 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"x");
        p$67 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"rider_count");
        p$68 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"y");
        p$69 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"block_count");
        p$70 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"z");
        p$71 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"pitch");
        p$72 = PolyClassRuntime.resolvePropertyHandler((String)"Contraption", (String)"is_held");
    }

    public ScriptValue tm$0_get_block(double d, double d2, double d3) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"get_block", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$1_get_block(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"get_block", (Object)this.instance, (List)list);
    }

    public boolean tm$2_release(String string) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"release", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$3_release(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"release", (Object)this.instance, (List)list);
    }

    public boolean tm$4_teleport(double d, double d2, double d3, double d4) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)d, (Object)d2, (Object)d3, (Object)d4);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"teleport", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3), ScriptValue.of((double)d4)}).asBool();
    }

    public ScriptValue um$5_teleport(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"teleport", (Object)this.instance, (List)list);
    }

    public boolean tm$6_set_yaw(double d) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"set_yaw", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$7_set_yaw(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"set_yaw", (Object)this.instance, (List)list);
    }

    public boolean tm$8_set_rpm(double d) {
        if (h$8 != null) {
            return (Boolean)h$8.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"set_rpm", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$9_set_rpm(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"set_rpm", (Object)this.instance, (List)list);
    }

    public double tm$10_get_rpm() {
        if (h$10 != null) {
            return (Double)h$10.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"get_rpm", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asNum();
    }

    public ScriptValue um$11_get_rpm(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"get_rpm", (Object)this.instance, (List)list);
    }

    public boolean tm$12_hold(String string) {
        if (h$12 != null) {
            return (Boolean)h$12.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"hold", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$13_hold(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"hold", (Object)this.instance, (List)list);
    }

    public boolean tm$14_disassemble() {
        if (h$14 != null) {
            return (Boolean)h$14.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"disassemble", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$15_disassemble(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"disassemble", (Object)this.instance, (List)list);
    }

    public boolean tm$16_set_spin(String string, double d) {
        if (h$16 != null) {
            return (Boolean)h$16.call(this.instance, (Object)string, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"set_spin", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$17_set_spin(List list) {
        if (m$17 != null) {
            return m$17.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"set_spin", (Object)this.instance, (List)list);
    }

    public boolean tm$18_move(double d, double d2, double d3) {
        if (h$18 != null) {
            return (Boolean)h$18.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"move", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$19_move(List list) {
        if (m$19 != null) {
            return m$19.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"move", (Object)this.instance, (List)list);
    }

    public boolean tm$20_has_riders() {
        if (h$20 != null) {
            return (Boolean)h$20.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"has_riders", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$21_has_riders(List list) {
        if (m$21 != null) {
            return m$21.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"has_riders", (Object)this.instance, (List)list);
    }

    public boolean tm$22_set_roll(double d) {
        if (h$22 != null) {
            return (Boolean)h$22.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"set_roll", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$23_set_roll(List list) {
        if (m$23 != null) {
            return m$23.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"set_roll", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$24_blocks() {
        if (h$24 != null) {
            return (ScriptValue)h$24.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"blocks", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$25_blocks(List list) {
        if (m$25 != null) {
            return m$25.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"blocks", (Object)this.instance, (List)list);
    }

    public boolean tm$26_set_scale(double d) {
        if (h$26 != null) {
            return (Boolean)h$26.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"set_scale", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$27_set_scale(List list) {
        if (m$27 != null) {
            return m$27.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"set_scale", (Object)this.instance, (List)list);
    }

    public boolean tm$28_kill() {
        if (h$28 != null) {
            return (Boolean)h$28.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"kill", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$29_kill(List list) {
        if (m$29 != null) {
            return m$29.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"kill", (Object)this.instance, (List)list);
    }

    public boolean tm$30_set_velocity(double d, double d2, double d3) {
        if (h$30 != null) {
            return (Boolean)h$30.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"set_velocity", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$31_set_velocity(List list) {
        if (m$31 != null) {
            return m$31.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"set_velocity", (Object)this.instance, (List)list);
    }

    public boolean tm$32_set_yaw_rate(double d) {
        if (h$32 != null) {
            return (Boolean)h$32.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"set_yaw_rate", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$33_set_yaw_rate(List list) {
        if (m$33 != null) {
            return m$33.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"set_yaw_rate", (Object)this.instance, (List)list);
    }

    public boolean tm$34_set_angular_velocity(double d, double d2, double d3) {
        if (h$34 != null) {
            return (Boolean)h$34.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"set_angular_velocity", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$35_set_angular_velocity(List list) {
        if (m$35 != null) {
            return m$35.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"set_angular_velocity", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$36_entities() {
        if (h$36 != null) {
            return (ScriptValue)h$36.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"entities", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$37_entities(List list) {
        if (m$37 != null) {
            return m$37.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"entities", (Object)this.instance, (List)list);
    }

    public boolean tm$38_set_rotation(double d, double d2, double d3) {
        if (h$38 != null) {
            return (Boolean)h$38.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"set_rotation", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$39_set_rotation(List list) {
        if (m$39 != null) {
            return m$39.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"set_rotation", (Object)this.instance, (List)list);
    }

    public boolean tm$40_apply_impulse(double d, double d2, double d3) {
        if (h$40 != null) {
            return (Boolean)h$40.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"apply_impulse", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$41_apply_impulse(List list) {
        if (m$41 != null) {
            return m$41.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"apply_impulse", (Object)this.instance, (List)list);
    }

    public boolean tm$42_set_pitch(double d) {
        if (h$42 != null) {
            return (Boolean)h$42.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"set_pitch", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$43_set_pitch(List list) {
        if (m$43 != null) {
            return m$43.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"set_pitch", (Object)this.instance, (List)list);
    }

    public boolean tm$44_rotate_by(double d, double d2, double d3) {
        if (h$44 != null) {
            return (Boolean)h$44.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"rotate_by", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$45_rotate_by(List list) {
        if (m$45 != null) {
            return m$45.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"rotate_by", (Object)this.instance, (List)list);
    }

    public boolean tm$46_report_su() {
        if (h$46 != null) {
            return (Boolean)h$46.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"report_su", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$47_report_su(List list) {
        if (m$47 != null) {
            return m$47.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"report_su", (Object)this.instance, (List)list);
    }

    public boolean tm$48_is_moving() {
        if (h$48 != null) {
            return (Boolean)h$48.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"is_moving", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$49_is_moving(List list) {
        if (m$49 != null) {
            return m$49.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"is_moving", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$50_real_direction(double d, double d2, double d3) {
        if (h$50 != null) {
            return (ScriptValue)h$50.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Contraption", (String)"real_direction", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$51_real_direction(List list) {
        if (m$51 != null) {
            return m$51.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Contraption", (String)"real_direction", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$52_container() {
        if (p$52 != null) {
            return p$52.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"container", (Object)this.instance);
    }

    public ScriptValue pg$53_contraption_world() {
        if (p$53 != null) {
            return p$53.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"contraption_world", (Object)this.instance);
    }

    public ScriptValue pg$54_roll() {
        if (p$54 != null) {
            return p$54.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"roll", (Object)this.instance);
    }

    public ScriptValue pg$55_is_contraption() {
        if (p$55 != null) {
            return p$55.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"is_contraption", (Object)this.instance);
    }

    public ScriptValue pg$56_scale() {
        if (p$56 != null) {
            return p$56.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"scale", (Object)this.instance);
    }

    public ScriptValue pg$57_weight() {
        if (p$57 != null) {
            return p$57.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"weight", (Object)this.instance);
    }

    public ScriptValue pg$58_velocity() {
        if (p$58 != null) {
            return p$58.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"velocity", (Object)this.instance);
    }

    public ScriptValue pg$59_anchor_entity() {
        if (p$59 != null) {
            return p$59.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"anchor_entity", (Object)this.instance);
    }

    public ScriptValue pg$60_uuid() {
        if (p$60 != null) {
            return p$60.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"uuid", (Object)this.instance);
    }

    public ScriptValue pg$61_yaw() {
        if (p$61 != null) {
            return p$61.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"yaw", (Object)this.instance);
    }

    public ScriptValue pg$62_speed() {
        if (p$62 != null) {
            return p$62.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"speed", (Object)this.instance);
    }

    public ScriptValue pg$63_rpm() {
        if (p$63 != null) {
            return p$63.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"rpm", (Object)this.instance);
    }

    public ScriptValue pg$64_has_anchor_entity() {
        if (p$64 != null) {
            return p$64.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"has_anchor_entity", (Object)this.instance);
    }

    public ScriptValue pg$65_real_world() {
        if (p$65 != null) {
            return p$65.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"real_world", (Object)this.instance);
    }

    public ScriptValue pg$66_x() {
        if (p$66 != null) {
            return p$66.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"x", (Object)this.instance);
    }

    public ScriptValue pg$67_rider_count() {
        if (p$67 != null) {
            return p$67.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"rider_count", (Object)this.instance);
    }

    public ScriptValue pg$68_y() {
        if (p$68 != null) {
            return p$68.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"y", (Object)this.instance);
    }

    public ScriptValue pg$69_block_count() {
        if (p$69 != null) {
            return p$69.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"block_count", (Object)this.instance);
    }

    public ScriptValue pg$70_z() {
        if (p$70 != null) {
            return p$70.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"z", (Object)this.instance);
    }

    public ScriptValue pg$71_pitch() {
        if (p$71 != null) {
            return p$71.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"pitch", (Object)this.instance);
    }

    public ScriptValue pg$72_is_held() {
        if (p$72 != null) {
            return p$72.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Contraption", (String)"is_held", (Object)this.instance);
    }

    public PolyClassContraption(Object object) {
        this.instance = object;
    }

    public static PolyClassContraption of(Object object) {
        return new PolyClassContraption(object);
    }
}
