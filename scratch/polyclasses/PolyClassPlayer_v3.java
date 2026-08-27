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
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler4
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler5
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassLivingEntity;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassPlayer_v3
extends PolyClassLivingEntity {
    private static volatile PolyType.TypedMethodHandler4 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler2 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler0 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler0 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler2 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler1 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.TypedMethodHandler2 h$16;
    private static volatile PolyType.MethodHandler m$17;
    private static volatile PolyType.TypedMethodHandler1 h$18;
    private static volatile PolyType.MethodHandler m$19;
    private static volatile PolyType.TypedMethodHandler1 h$20;
    private static volatile PolyType.MethodHandler m$21;
    private static volatile PolyType.TypedMethodHandler1 h$22;
    private static volatile PolyType.MethodHandler m$23;
    private static volatile PolyType.TypedMethodHandler1 h$24;
    private static volatile PolyType.MethodHandler m$25;
    private static volatile PolyType.TypedMethodHandler1 h$26;
    private static volatile PolyType.MethodHandler m$27;
    private static volatile PolyType.TypedMethodHandler1 h$28;
    private static volatile PolyType.MethodHandler m$29;
    private static volatile PolyType.TypedMethodHandler3 h$30;
    private static volatile PolyType.MethodHandler m$31;
    private static volatile PolyType.TypedMethodHandler1 h$32;
    private static volatile PolyType.MethodHandler m$33;
    private static volatile PolyType.TypedMethodHandler2 h$34;
    private static volatile PolyType.MethodHandler m$35;
    private static volatile PolyType.TypedMethodHandler1 h$36;
    private static volatile PolyType.MethodHandler m$37;
    private static volatile PolyType.TypedMethodHandler1 h$38;
    private static volatile PolyType.MethodHandler m$39;
    private static volatile PolyType.TypedMethodHandler2 h$40;
    private static volatile PolyType.MethodHandler m$41;
    private static volatile PolyType.TypedMethodHandler1 h$42;
    private static volatile PolyType.MethodHandler m$43;
    private static volatile PolyType.TypedMethodHandler5 h$44;
    private static volatile PolyType.MethodHandler m$45;
    private static volatile PolyType.PropertyHandler p$46;
    private static volatile PolyType.TypedPropertyHandler tp$47;
    private static volatile PolyType.PropertyHandler p$48;
    private static volatile PolyType.PropertyHandler p$49;
    private static volatile PolyType.TypedPropertyHandler tp$50;
    private static volatile PolyType.PropertyHandler p$51;
    private static volatile PolyType.TypedPropertyHandler tp$52;
    private static volatile PolyType.PropertyHandler p$53;
    private static volatile PolyType.TypedPropertyHandler tp$54;
    private static volatile PolyType.PropertyHandler p$55;
    private static volatile PolyType.PropertyHandler p$56;
    private static volatile PolyType.TypedPropertyHandler tp$57;
    private static volatile PolyType.PropertyHandler p$58;
    private static volatile PolyType.TypedPropertyHandler tp$59;
    private static volatile PolyType.PropertyHandler p$60;
    private static volatile PolyType.TypedPropertyHandler tp$61;
    private static volatile PolyType.PropertyHandler p$62;
    private static volatile PolyType.TypedPropertyHandler tp$63;
    private static volatile PolyType.PropertyHandler p$64;
    private static volatile PolyType.TypedPropertyHandler tp$65;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler4)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"show_bossbar", (String)"SDSS:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"show_bossbar");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"set_flying", (String)"Z:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"set_flying");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"has_permission", (String)"S:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"has_permission");
        h$6 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"has_typed", (String)"SS:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"has_typed");
        h$8 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"close_inventory", (String)":Z");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"close_inventory");
        h$10 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"hide_bossbar", (String)":Z");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"hide_bossbar");
        h$12 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"get_typed", (String)"SS:R");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"get_typed");
        h$14 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"give_item", (String)"R:Z");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"give_item");
        h$16 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"consume_item", (String)"SD:Z");
        m$17 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"consume_item");
        h$18 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"count_item", (String)"S:D");
        m$19 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"count_item");
        h$20 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"get_inventory_slot", (String)"D:R");
        m$21 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"get_inventory_slot");
        h$22 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"parse", (String)"S:S");
        m$23 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"parse");
        h$24 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"exec_command", (String)"S:Z");
        m$25 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"exec_command");
        h$26 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"give_exp", (String)"D:Z");
        m$27 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"give_exp");
        h$28 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"take_exp", (String)"D:Z");
        m$29 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"take_exp");
        h$30 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"set_typed", (String)"SSR:Z");
        m$31 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"set_typed");
        h$32 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"set_allow_flight", (String)"Z:Z");
        m$33 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"set_allow_flight");
        h$34 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"remove_item", (String)"SD:Z");
        m$35 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"remove_item");
        h$36 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"send_actionbar", (String)"S:Z");
        m$37 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"send_actionbar");
        h$38 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"has_exp", (String)"D:Z");
        m$39 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"has_exp");
        h$40 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"has_item", (String)"SD:Z");
        m$41 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"has_item");
        h$42 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"send_message", (String)"S:Z");
        m$43 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"send_message");
        h$44 = (PolyType.TypedMethodHandler5)PolyClassRuntime.resolveTypedHandler((String)"Player", (String)"send_title", (String)"SSDDD:Z");
        m$45 = PolyClassRuntime.resolveMethodHandler((String)"Player", (String)"send_title");
        p$46 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"saturation");
        tp$47 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Player", (String)"saturation", (String)"D");
        p$48 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"main_hand");
        p$49 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"is_flying");
        tp$50 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Player", (String)"is_flying", (String)"Z");
        p$51 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"is_creative");
        tp$52 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Player", (String)"is_creative", (String)"Z");
        p$53 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"allow_flight");
        tp$54 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Player", (String)"allow_flight", (String)"Z");
        p$55 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"off_hand");
        p$56 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"xp_level");
        tp$57 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Player", (String)"xp_level", (String)"D");
        p$58 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"xp_progress");
        tp$59 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Player", (String)"xp_progress", (String)"D");
        p$60 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"food_level");
        tp$61 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Player", (String)"food_level", (String)"D");
        p$62 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"gamemode");
        tp$63 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Player", (String)"gamemode", (String)"S");
        p$64 = PolyClassRuntime.resolvePropertyHandler((String)"Player", (String)"total_exp");
        tp$65 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Player", (String)"total_exp", (String)"D");
    }

    public boolean tm$0_show_bossbar(String string, double d, String string2, String string3) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string, (Object)d, (Object)string2, (Object)string3);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"show_bossbar", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d), ScriptValue.of((String)string2), ScriptValue.of((String)string3)}).asBool();
    }

    public ScriptValue um$1_show_bossbar(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"show_bossbar", (Object)this.instance, (List)list);
    }

    public boolean tm$2_set_flying(boolean bl) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"set_flying", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$3_set_flying(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"set_flying", (Object)this.instance, (List)list);
    }

    public boolean tm$4_has_permission(String string) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"has_permission", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$5_has_permission(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"has_permission", (Object)this.instance, (List)list);
    }

    public boolean tm$6_has_typed(String string, String string2) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"has_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)}).asBool();
    }

    public ScriptValue um$7_has_typed(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"has_typed", (Object)this.instance, (List)list);
    }

    public boolean tm$8_close_inventory() {
        if (h$8 != null) {
            return (Boolean)h$8.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"close_inventory", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$9_close_inventory(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"close_inventory", (Object)this.instance, (List)list);
    }

    public boolean tm$10_hide_bossbar() {
        if (h$10 != null) {
            return (Boolean)h$10.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"hide_bossbar", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$11_hide_bossbar(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"hide_bossbar", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$12_get_typed(String string, String string2) {
        if (h$12 != null) {
            return (ScriptValue)h$12.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"get_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)});
    }

    public ScriptValue um$13_get_typed(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"get_typed", (Object)this.instance, (List)list);
    }

    public boolean tm$14_give_item(ScriptValue scriptValue) {
        if (h$14 != null) {
            return (Boolean)h$14.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"give_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$15_give_item(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"give_item", (Object)this.instance, (List)list);
    }

    public boolean tm$16_consume_item(String string, double d) {
        if (h$16 != null) {
            return (Boolean)h$16.call(this.instance, (Object)string, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"consume_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$17_consume_item(List list) {
        if (m$17 != null) {
            return m$17.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"consume_item", (Object)this.instance, (List)list);
    }

    public double tm$18_count_item(String string) {
        if (h$18 != null) {
            return (Double)h$18.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"count_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$19_count_item(List list) {
        if (m$19 != null) {
            return m$19.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"count_item", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$20_get_inventory_slot(double d) {
        if (h$20 != null) {
            return (ScriptValue)h$20.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"get_inventory_slot", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$21_get_inventory_slot(List list) {
        if (m$21 != null) {
            return m$21.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"get_inventory_slot", (Object)this.instance, (List)list);
    }

    public String tm$22_parse(String string) {
        if (h$22 != null) {
            return (String)h$22.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"parse", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asStr();
    }

    public ScriptValue um$23_parse(List list) {
        if (m$23 != null) {
            return m$23.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"parse", (Object)this.instance, (List)list);
    }

    public boolean tm$24_exec_command(String string) {
        if (h$24 != null) {
            return (Boolean)h$24.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"exec_command", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$25_exec_command(List list) {
        if (m$25 != null) {
            return m$25.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"exec_command", (Object)this.instance, (List)list);
    }

    public boolean tm$26_give_exp(double d) {
        if (h$26 != null) {
            return (Boolean)h$26.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"give_exp", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$27_give_exp(List list) {
        if (m$27 != null) {
            return m$27.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"give_exp", (Object)this.instance, (List)list);
    }

    public boolean tm$28_take_exp(double d) {
        if (h$28 != null) {
            return (Boolean)h$28.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"take_exp", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$29_take_exp(List list) {
        if (m$29 != null) {
            return m$29.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"take_exp", (Object)this.instance, (List)list);
    }

    public boolean tm$30_set_typed(String string, String string2, ScriptValue scriptValue) {
        if (h$30 != null) {
            return (Boolean)h$30.call(this.instance, (Object)string, (Object)string2, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"set_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2), scriptValue}).asBool();
    }

    public ScriptValue um$31_set_typed(List list) {
        if (m$31 != null) {
            return m$31.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"set_typed", (Object)this.instance, (List)list);
    }

    public boolean tm$32_set_allow_flight(boolean bl) {
        if (h$32 != null) {
            return (Boolean)h$32.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"set_allow_flight", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$33_set_allow_flight(List list) {
        if (m$33 != null) {
            return m$33.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"set_allow_flight", (Object)this.instance, (List)list);
    }

    public boolean tm$34_remove_item(String string, double d) {
        if (h$34 != null) {
            return (Boolean)h$34.call(this.instance, (Object)string, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"remove_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$35_remove_item(List list) {
        if (m$35 != null) {
            return m$35.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"remove_item", (Object)this.instance, (List)list);
    }

    public boolean tm$36_send_actionbar(String string) {
        if (h$36 != null) {
            return (Boolean)h$36.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"send_actionbar", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$37_send_actionbar(List list) {
        if (m$37 != null) {
            return m$37.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"send_actionbar", (Object)this.instance, (List)list);
    }

    public boolean tm$38_has_exp(double d) {
        if (h$38 != null) {
            return (Boolean)h$38.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"has_exp", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$39_has_exp(List list) {
        if (m$39 != null) {
            return m$39.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"has_exp", (Object)this.instance, (List)list);
    }

    public boolean tm$40_has_item(String string, double d) {
        if (h$40 != null) {
            return (Boolean)h$40.call(this.instance, (Object)string, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"has_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$41_has_item(List list) {
        if (m$41 != null) {
            return m$41.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"has_item", (Object)this.instance, (List)list);
    }

    public boolean tm$42_send_message(String string) {
        if (h$42 != null) {
            return (Boolean)h$42.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"send_message", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$43_send_message(List list) {
        if (m$43 != null) {
            return m$43.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"send_message", (Object)this.instance, (List)list);
    }

    public boolean tm$44_send_title(String string, String string2, double d, double d2, double d3) {
        if (h$44 != null) {
            return (Boolean)h$44.call(this.instance, (Object)string, (Object)string2, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Player", (String)"send_title", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2), ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$45_send_title(List list) {
        if (m$45 != null) {
            return m$45.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Player", (String)"send_title", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$46_saturation() {
        if (p$46 != null) {
            return p$46.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"saturation", (Object)this.instance);
    }

    public double tg$47_saturation() {
        if (tp$47 != null) {
            return (Double)tp$47.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"saturation", (Object)this.instance).asNum();
    }

    public ScriptValue pg$48_main_hand() {
        if (p$48 != null) {
            return p$48.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"main_hand", (Object)this.instance);
    }

    public ScriptValue pg$49_is_flying() {
        if (p$49 != null) {
            return p$49.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"is_flying", (Object)this.instance);
    }

    public boolean tg$50_is_flying() {
        if (tp$50 != null) {
            return (Boolean)tp$50.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"is_flying", (Object)this.instance).asBool();
    }

    public ScriptValue pg$51_is_creative() {
        if (p$51 != null) {
            return p$51.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"is_creative", (Object)this.instance);
    }

    public boolean tg$52_is_creative() {
        if (tp$52 != null) {
            return (Boolean)tp$52.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"is_creative", (Object)this.instance).asBool();
    }

    public ScriptValue pg$53_allow_flight() {
        if (p$53 != null) {
            return p$53.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"allow_flight", (Object)this.instance);
    }

    public boolean tg$54_allow_flight() {
        if (tp$54 != null) {
            return (Boolean)tp$54.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"allow_flight", (Object)this.instance).asBool();
    }

    public ScriptValue pg$55_off_hand() {
        if (p$55 != null) {
            return p$55.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"off_hand", (Object)this.instance);
    }

    public ScriptValue pg$56_xp_level() {
        if (p$56 != null) {
            return p$56.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"xp_level", (Object)this.instance);
    }

    public double tg$57_xp_level() {
        if (tp$57 != null) {
            return (Double)tp$57.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"xp_level", (Object)this.instance).asNum();
    }

    public ScriptValue pg$58_xp_progress() {
        if (p$58 != null) {
            return p$58.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"xp_progress", (Object)this.instance);
    }

    public double tg$59_xp_progress() {
        if (tp$59 != null) {
            return (Double)tp$59.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"xp_progress", (Object)this.instance).asNum();
    }

    public ScriptValue pg$60_food_level() {
        if (p$60 != null) {
            return p$60.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"food_level", (Object)this.instance);
    }

    public double tg$61_food_level() {
        if (tp$61 != null) {
            return (Double)tp$61.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"food_level", (Object)this.instance).asNum();
    }

    public ScriptValue pg$62_gamemode() {
        if (p$62 != null) {
            return p$62.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"gamemode", (Object)this.instance);
    }

    public String tg$63_gamemode() {
        if (tp$63 != null) {
            return (String)tp$63.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"gamemode", (Object)this.instance).asStr();
    }

    public ScriptValue pg$64_total_exp() {
        if (p$64 != null) {
            return p$64.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"total_exp", (Object)this.instance);
    }

    public double tg$65_total_exp() {
        if (tp$65 != null) {
            return (Double)tp$65.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Player", (String)"total_exp", (Object)this.instance).asNum();
    }

    public PolyClassPlayer_v3(Object object) {
        super(object);
    }

    public static PolyClassPlayer_v3 of(Object object) {
        return new PolyClassPlayer_v3(object);
    }

    public static PolyClassPlayer_v3 ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
            return new PolyClassPlayer_v3(object);
        }
        return null;
    }

    public static PolyClassPlayer_v3 ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayer_v3.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
