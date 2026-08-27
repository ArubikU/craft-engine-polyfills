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
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassItem {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler2 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler0 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler2 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler3 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler1 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.TypedMethodHandler1 h$16;
    private static volatile PolyType.MethodHandler m$17;
    private static volatile PolyType.TypedMethodHandler1 h$18;
    private static volatile PolyType.MethodHandler m$19;
    private static volatile PolyType.TypedMethodHandler2 h$20;
    private static volatile PolyType.MethodHandler m$21;
    private static volatile PolyType.TypedMethodHandler1 h$22;
    private static volatile PolyType.MethodHandler m$23;
    private static volatile PolyType.TypedMethodHandler1 h$24;
    private static volatile PolyType.MethodHandler m$25;
    private static volatile PolyType.TypedMethodHandler2 h$26;
    private static volatile PolyType.MethodHandler m$27;
    private static volatile PolyType.TypedMethodHandler1 h$28;
    private static volatile PolyType.MethodHandler m$29;
    private static volatile PolyType.TypedMethodHandler1 h$30;
    private static volatile PolyType.MethodHandler m$31;
    private static volatile PolyType.TypedMethodHandler2 h$32;
    private static volatile PolyType.MethodHandler m$33;
    private static volatile PolyType.MethodHandler m$34;
    private static volatile PolyType.TypedMethodHandler0 h$35;
    private static volatile PolyType.MethodHandler m$36;
    private static volatile PolyType.TypedMethodHandler1 h$37;
    private static volatile PolyType.MethodHandler m$38;
    private static volatile PolyType.TypedMethodHandler1 h$39;
    private static volatile PolyType.MethodHandler m$40;
    private static volatile PolyType.TypedMethodHandler1 h$41;
    private static volatile PolyType.MethodHandler m$42;
    private static volatile PolyType.TypedMethodHandler1 h$43;
    private static volatile PolyType.MethodHandler m$44;
    private static volatile PolyType.TypedMethodHandler2 h$45;
    private static volatile PolyType.MethodHandler m$46;
    private static volatile PolyType.TypedMethodHandler1 h$47;
    private static volatile PolyType.MethodHandler m$48;
    private static volatile PolyType.TypedMethodHandler1 h$49;
    private static volatile PolyType.MethodHandler m$50;
    private static volatile PolyType.TypedMethodHandler1 h$51;
    private static volatile PolyType.MethodHandler m$52;
    private static volatile PolyType.PropertyHandler p$53;
    private static volatile PolyType.TypedPropertyHandler tp$54;
    private static volatile PolyType.PropertyHandler p$55;
    private static volatile PolyType.TypedPropertyHandler tp$56;
    private static volatile PolyType.PropertyHandler p$57;
    private static volatile PolyType.PropertyHandler p$58;
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
    private static volatile PolyType.TypedPropertyHandler tp$74;
    private static volatile PolyType.PropertyHandler p$75;
    private static volatile PolyType.TypedPropertyHandler tp$76;
    private static volatile PolyType.PropertyHandler p$77;
    private static volatile PolyType.TypedPropertyHandler tp$78;
    private static volatile PolyType.PropertyHandler p$79;
    private static volatile PolyType.TypedPropertyHandler tp$80;
    private static volatile PolyType.PropertyHandler p$81;
    private static volatile PolyType.TypedPropertyHandler tp$82;
    private static volatile PolyType.PropertyHandler p$83;
    private static volatile PolyType.TypedPropertyHandler tp$84;
    private static volatile PolyType.PropertyHandler p$85;
    private static volatile PolyType.TypedPropertyHandler tp$86;
    private static volatile PolyType.PropertyHandler p$87;
    private static volatile PolyType.TypedPropertyHandler tp$88;
    private static volatile PolyType.PropertyHandler p$89;
    private static volatile PolyType.TypedPropertyHandler tp$90;
    private static volatile PolyType.PropertyHandler p$91;
    private static volatile PolyType.TypedPropertyHandler tp$92;
    private static volatile PolyType.PropertyHandler p$93;
    private static volatile PolyType.PropertyHandler p$94;
    private static volatile PolyType.TypedPropertyHandler tp$95;
    private static volatile PolyType.PropertyHandler p$96;
    private static volatile PolyType.TypedPropertyHandler tp$97;
    private static volatile PolyType.PropertyHandler p$98;
    private static volatile PolyType.TypedPropertyHandler tp$99;
    private static volatile PolyType.PropertyHandler p$100;
    private static volatile PolyType.TypedPropertyHandler tp$101;
    private static volatile PolyType.PropertyHandler p$102;
    private static volatile PolyType.TypedPropertyHandler tp$103;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"with_name", (String)"S:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"with_name");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"hide_tooltip", (String)"Z:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"hide_tooltip");
        h$4 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"has_typed", (String)"SS:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"has_typed");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"same_as", (String)"R:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"same_as");
        h$8 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"update", (String)":R");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"update");
        h$10 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"with_component", (String)"RR:R");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"with_component");
        h$12 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"with_typed", (String)"SSR:R");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"with_typed");
        h$14 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"has_component", (String)"S:Z");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"has_component");
        h$16 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"tank_capacity", (String)"S:D");
        m$17 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"tank_capacity");
        h$18 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"has_script", (String)"S:Z");
        m$19 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"has_script");
        h$20 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"create", (String)"SD:R");
        m$21 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"create");
        h$22 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"skull", (String)"R:R");
        m$23 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"skull");
        h$24 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"tank", (String)"S:D");
        m$25 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"tank");
        h$26 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"add_tank", (String)"SD:R");
        m$27 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"add_tank");
        h$28 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"with_count", (String)"D:R");
        m$29 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"with_count");
        h$30 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"with_profile", (String)"R:R");
        m$31 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"with_profile");
        h$32 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"get_typed", (String)"SS:R");
        m$33 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"get_typed");
        m$34 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"with_lore");
        h$35 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"has_definition", (String)":Z");
        m$36 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"has_definition");
        h$37 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"matches", (String)"S:Z");
        m$38 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"matches");
        h$39 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"component", (String)"S:R");
        m$40 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"component");
        h$41 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"remove_component", (String)"S:R");
        m$42 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"remove_component");
        h$43 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"transmutate", (String)"S:R");
        m$44 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"transmutate");
        h$45 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"set_tank", (String)"SD:R");
        m$46 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"set_tank");
        h$47 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"can_break", (String)"S:Z");
        m$48 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"can_break");
        h$49 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"glow", (String)"Z:R");
        m$50 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"glow");
        h$51 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Item", (String)"get_script", (String)"S:S");
        m$52 = PolyClassRuntime.resolveMethodHandler((String)"Item", (String)"get_script");
        p$53 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"armor_value");
        tp$54 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"armor_value", (String)"D");
        p$55 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"damage");
        tp$56 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"damage", (String)"D");
        p$57 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"nbt");
        p$58 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"lore");
        p$59 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"is_food");
        tp$60 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"is_food", (String)"Z");
        p$61 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"attack_damage");
        tp$62 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"attack_damage", (String)"D");
        p$63 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"attack_speed");
        tp$64 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"attack_speed", (String)"D");
        p$65 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"type");
        tp$66 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"type", (String)"S");
        p$67 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"max_count");
        tp$68 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"max_count", (String)"D");
        p$69 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"is_stackable");
        tp$70 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"is_stackable", (String)"Z");
        p$71 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"is_tool");
        tp$72 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"is_tool", (String)"Z");
        p$73 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"has_nbt");
        tp$74 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"has_nbt", (String)"Z");
        p$75 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"is_custom");
        tp$76 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"is_custom", (String)"Z");
        p$77 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"food_value");
        tp$78 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"food_value", (String)"D");
        p$79 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"is_armor");
        tp$80 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"is_armor", (String)"Z");
        p$81 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"id");
        tp$82 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"id", (String)"S");
        p$83 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"is_empty");
        tp$84 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"is_empty", (String)"Z");
        p$85 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"custom_model_data");
        tp$86 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"custom_model_data", (String)"D");
        p$87 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"amount");
        tp$88 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"amount", (String)"D");
        p$89 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"is_weapon");
        tp$90 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"is_weapon", (String)"Z");
        p$91 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"count");
        tp$92 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"count", (String)"D");
        p$93 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"enchantments");
        p$94 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"block_id");
        tp$95 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"block_id", (String)"S");
        p$96 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"max_damage");
        tp$97 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"max_damage", (String)"D");
        p$98 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"name");
        tp$99 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"name", (String)"S");
        p$100 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"vanilla_id");
        tp$101 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"vanilla_id", (String)"S");
        p$102 = PolyClassRuntime.resolvePropertyHandler((String)"Item", (String)"rarity");
        tp$103 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Item", (String)"rarity", (String)"S");
    }

    public ScriptValue tm$0_with_name(String string) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"with_name", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$1_with_name(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"with_name", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_hide_tooltip(boolean bl) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"hide_tooltip", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)});
    }

    public ScriptValue um$3_hide_tooltip(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"hide_tooltip", (Object)this.instance, (List)list);
    }

    public boolean tm$4_has_typed(String string, String string2) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"has_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)}).asBool();
    }

    public ScriptValue um$5_has_typed(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"has_typed", (Object)this.instance, (List)list);
    }

    public boolean tm$6_same_as(ScriptValue scriptValue) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"same_as", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$7_same_as(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"same_as", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$8_update() {
        if (h$8 != null) {
            return (ScriptValue)h$8.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"update", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$9_update(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"update", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$10_with_component(ScriptValue scriptValue, ScriptValue scriptValue2) {
        if (h$10 != null) {
            return (ScriptValue)h$10.call(this.instance, (Object)scriptValue, (Object)scriptValue2);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"with_component", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, scriptValue2});
    }

    public ScriptValue um$11_with_component(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"with_component", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$12_with_typed(String string, String string2, ScriptValue scriptValue) {
        if (h$12 != null) {
            return (ScriptValue)h$12.call(this.instance, (Object)string, (Object)string2, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"with_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2), scriptValue});
    }

    public ScriptValue um$13_with_typed(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"with_typed", (Object)this.instance, (List)list);
    }

    public boolean tm$14_has_component(String string) {
        if (h$14 != null) {
            return (Boolean)h$14.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"has_component", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$15_has_component(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"has_component", (Object)this.instance, (List)list);
    }

    public double tm$16_tank_capacity(String string) {
        if (h$16 != null) {
            return (Double)h$16.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"tank_capacity", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$17_tank_capacity(List list) {
        if (m$17 != null) {
            return m$17.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"tank_capacity", (Object)this.instance, (List)list);
    }

    public boolean tm$18_has_script(String string) {
        if (h$18 != null) {
            return (Boolean)h$18.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"has_script", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$19_has_script(List list) {
        if (m$19 != null) {
            return m$19.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"has_script", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$20_create(String string, double d) {
        if (h$20 != null) {
            return (ScriptValue)h$20.call(this.instance, (Object)string, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"create", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d)});
    }

    public ScriptValue um$21_create(List list) {
        if (m$21 != null) {
            return m$21.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"create", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$22_skull(ScriptValue scriptValue) {
        if (h$22 != null) {
            return (ScriptValue)h$22.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"skull", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$23_skull(List list) {
        if (m$23 != null) {
            return m$23.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"skull", (Object)this.instance, (List)list);
    }

    public double tm$24_tank(String string) {
        if (h$24 != null) {
            return (Double)h$24.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"tank", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$25_tank(List list) {
        if (m$25 != null) {
            return m$25.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"tank", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$26_add_tank(String string, double d) {
        if (h$26 != null) {
            return (ScriptValue)h$26.call(this.instance, (Object)string, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"add_tank", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d)});
    }

    public ScriptValue um$27_add_tank(List list) {
        if (m$27 != null) {
            return m$27.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"add_tank", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$28_with_count(double d) {
        if (h$28 != null) {
            return (ScriptValue)h$28.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"with_count", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$29_with_count(List list) {
        if (m$29 != null) {
            return m$29.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"with_count", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$30_with_profile(ScriptValue scriptValue) {
        if (h$30 != null) {
            return (ScriptValue)h$30.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"with_profile", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$31_with_profile(List list) {
        if (m$31 != null) {
            return m$31.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"with_profile", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$32_get_typed(String string, String string2) {
        if (h$32 != null) {
            return (ScriptValue)h$32.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"get_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)});
    }

    public ScriptValue um$33_get_typed(List list) {
        if (m$33 != null) {
            return m$33.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"get_typed", (Object)this.instance, (List)list);
    }

    public ScriptValue um$34_with_lore(List list) {
        if (m$34 != null) {
            return m$34.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"with_lore", (Object)this.instance, (List)list);
    }

    public boolean tm$35_has_definition() {
        if (h$35 != null) {
            return (Boolean)h$35.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"has_definition", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$36_has_definition(List list) {
        if (m$36 != null) {
            return m$36.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"has_definition", (Object)this.instance, (List)list);
    }

    public boolean tm$37_matches(String string) {
        if (h$37 != null) {
            return (Boolean)h$37.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"matches", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$38_matches(List list) {
        if (m$38 != null) {
            return m$38.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"matches", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$39_component(String string) {
        if (h$39 != null) {
            return (ScriptValue)h$39.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"component", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$40_component(List list) {
        if (m$40 != null) {
            return m$40.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"component", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$41_remove_component(String string) {
        if (h$41 != null) {
            return (ScriptValue)h$41.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"remove_component", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$42_remove_component(List list) {
        if (m$42 != null) {
            return m$42.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"remove_component", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$43_transmutate(String string) {
        if (h$43 != null) {
            return (ScriptValue)h$43.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"transmutate", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$44_transmutate(List list) {
        if (m$44 != null) {
            return m$44.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"transmutate", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$45_set_tank(String string, double d) {
        if (h$45 != null) {
            return (ScriptValue)h$45.call(this.instance, (Object)string, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"set_tank", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d)});
    }

    public ScriptValue um$46_set_tank(List list) {
        if (m$46 != null) {
            return m$46.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"set_tank", (Object)this.instance, (List)list);
    }

    public boolean tm$47_can_break(String string) {
        if (h$47 != null) {
            return (Boolean)h$47.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"can_break", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$48_can_break(List list) {
        if (m$48 != null) {
            return m$48.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"can_break", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$49_glow(boolean bl) {
        if (h$49 != null) {
            return (ScriptValue)h$49.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"glow", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)});
    }

    public ScriptValue um$50_glow(List list) {
        if (m$50 != null) {
            return m$50.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"glow", (Object)this.instance, (List)list);
    }

    public String tm$51_get_script(String string) {
        if (h$51 != null) {
            return (String)h$51.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Item", (String)"get_script", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asStr();
    }

    public ScriptValue um$52_get_script(List list) {
        if (m$52 != null) {
            return m$52.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Item", (String)"get_script", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$53_armor_value() {
        if (p$53 != null) {
            return p$53.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"armor_value", (Object)this.instance);
    }

    public double tg$54_armor_value() {
        if (tp$54 != null) {
            return (Double)tp$54.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"armor_value", (Object)this.instance).asNum();
    }

    public ScriptValue pg$55_damage() {
        if (p$55 != null) {
            return p$55.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"damage", (Object)this.instance);
    }

    public double tg$56_damage() {
        if (tp$56 != null) {
            return (Double)tp$56.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"damage", (Object)this.instance).asNum();
    }

    public ScriptValue pg$57_nbt() {
        if (p$57 != null) {
            return p$57.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"nbt", (Object)this.instance);
    }

    public ScriptValue pg$58_lore() {
        if (p$58 != null) {
            return p$58.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"lore", (Object)this.instance);
    }

    public ScriptValue pg$59_is_food() {
        if (p$59 != null) {
            return p$59.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_food", (Object)this.instance);
    }

    public boolean tg$60_is_food() {
        if (tp$60 != null) {
            return (Boolean)tp$60.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_food", (Object)this.instance).asBool();
    }

    public ScriptValue pg$61_attack_damage() {
        if (p$61 != null) {
            return p$61.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"attack_damage", (Object)this.instance);
    }

    public double tg$62_attack_damage() {
        if (tp$62 != null) {
            return (Double)tp$62.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"attack_damage", (Object)this.instance).asNum();
    }

    public ScriptValue pg$63_attack_speed() {
        if (p$63 != null) {
            return p$63.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"attack_speed", (Object)this.instance);
    }

    public double tg$64_attack_speed() {
        if (tp$64 != null) {
            return (Double)tp$64.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"attack_speed", (Object)this.instance).asNum();
    }

    public ScriptValue pg$65_type() {
        if (p$65 != null) {
            return p$65.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"type", (Object)this.instance);
    }

    public String tg$66_type() {
        if (tp$66 != null) {
            return (String)tp$66.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"type", (Object)this.instance).asStr();
    }

    public ScriptValue pg$67_max_count() {
        if (p$67 != null) {
            return p$67.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"max_count", (Object)this.instance);
    }

    public double tg$68_max_count() {
        if (tp$68 != null) {
            return (Double)tp$68.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"max_count", (Object)this.instance).asNum();
    }

    public ScriptValue pg$69_is_stackable() {
        if (p$69 != null) {
            return p$69.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_stackable", (Object)this.instance);
    }

    public boolean tg$70_is_stackable() {
        if (tp$70 != null) {
            return (Boolean)tp$70.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_stackable", (Object)this.instance).asBool();
    }

    public ScriptValue pg$71_is_tool() {
        if (p$71 != null) {
            return p$71.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_tool", (Object)this.instance);
    }

    public boolean tg$72_is_tool() {
        if (tp$72 != null) {
            return (Boolean)tp$72.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_tool", (Object)this.instance).asBool();
    }

    public ScriptValue pg$73_has_nbt() {
        if (p$73 != null) {
            return p$73.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"has_nbt", (Object)this.instance);
    }

    public boolean tg$74_has_nbt() {
        if (tp$74 != null) {
            return (Boolean)tp$74.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"has_nbt", (Object)this.instance).asBool();
    }

    public ScriptValue pg$75_is_custom() {
        if (p$75 != null) {
            return p$75.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_custom", (Object)this.instance);
    }

    public boolean tg$76_is_custom() {
        if (tp$76 != null) {
            return (Boolean)tp$76.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_custom", (Object)this.instance).asBool();
    }

    public ScriptValue pg$77_food_value() {
        if (p$77 != null) {
            return p$77.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"food_value", (Object)this.instance);
    }

    public double tg$78_food_value() {
        if (tp$78 != null) {
            return (Double)tp$78.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"food_value", (Object)this.instance).asNum();
    }

    public ScriptValue pg$79_is_armor() {
        if (p$79 != null) {
            return p$79.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_armor", (Object)this.instance);
    }

    public boolean tg$80_is_armor() {
        if (tp$80 != null) {
            return (Boolean)tp$80.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_armor", (Object)this.instance).asBool();
    }

    public ScriptValue pg$81_id() {
        if (p$81 != null) {
            return p$81.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"id", (Object)this.instance);
    }

    public String tg$82_id() {
        if (tp$82 != null) {
            return (String)tp$82.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"id", (Object)this.instance).asStr();
    }

    public ScriptValue pg$83_is_empty() {
        if (p$83 != null) {
            return p$83.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_empty", (Object)this.instance);
    }

    public boolean tg$84_is_empty() {
        if (tp$84 != null) {
            return (Boolean)tp$84.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_empty", (Object)this.instance).asBool();
    }

    public ScriptValue pg$85_custom_model_data() {
        if (p$85 != null) {
            return p$85.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"custom_model_data", (Object)this.instance);
    }

    public double tg$86_custom_model_data() {
        if (tp$86 != null) {
            return (Double)tp$86.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"custom_model_data", (Object)this.instance).asNum();
    }

    public ScriptValue pg$87_amount() {
        if (p$87 != null) {
            return p$87.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"amount", (Object)this.instance);
    }

    public double tg$88_amount() {
        if (tp$88 != null) {
            return (Double)tp$88.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"amount", (Object)this.instance).asNum();
    }

    public ScriptValue pg$89_is_weapon() {
        if (p$89 != null) {
            return p$89.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_weapon", (Object)this.instance);
    }

    public boolean tg$90_is_weapon() {
        if (tp$90 != null) {
            return (Boolean)tp$90.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"is_weapon", (Object)this.instance).asBool();
    }

    public ScriptValue pg$91_count() {
        if (p$91 != null) {
            return p$91.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"count", (Object)this.instance);
    }

    public double tg$92_count() {
        if (tp$92 != null) {
            return (Double)tp$92.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"count", (Object)this.instance).asNum();
    }

    public ScriptValue pg$93_enchantments() {
        if (p$93 != null) {
            return p$93.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"enchantments", (Object)this.instance);
    }

    public ScriptValue pg$94_block_id() {
        if (p$94 != null) {
            return p$94.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"block_id", (Object)this.instance);
    }

    public String tg$95_block_id() {
        if (tp$95 != null) {
            return (String)tp$95.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"block_id", (Object)this.instance).asStr();
    }

    public ScriptValue pg$96_max_damage() {
        if (p$96 != null) {
            return p$96.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"max_damage", (Object)this.instance);
    }

    public double tg$97_max_damage() {
        if (tp$97 != null) {
            return (Double)tp$97.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"max_damage", (Object)this.instance).asNum();
    }

    public ScriptValue pg$98_name() {
        if (p$98 != null) {
            return p$98.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"name", (Object)this.instance);
    }

    public String tg$99_name() {
        if (tp$99 != null) {
            return (String)tp$99.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"name", (Object)this.instance).asStr();
    }

    public ScriptValue pg$100_vanilla_id() {
        if (p$100 != null) {
            return p$100.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"vanilla_id", (Object)this.instance);
    }

    public String tg$101_vanilla_id() {
        if (tp$101 != null) {
            return (String)tp$101.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"vanilla_id", (Object)this.instance).asStr();
    }

    public ScriptValue pg$102_rarity() {
        if (p$102 != null) {
            return p$102.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"rarity", (Object)this.instance);
    }

    public String tg$103_rarity() {
        if (tp$103 != null) {
            return (String)tp$103.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Item", (String)"rarity", (Object)this.instance).asStr();
    }

    public PolyClassItem(Object object) {
        this.instance = object;
    }

    public static PolyClassItem of(Object object) {
        return new PolyClassItem(object);
    }

    public static PolyClassItem ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Item")) {
            return new PolyClassItem(object);
        }
        return null;
    }

    public static PolyClassItem ofVar(ScriptContext scriptContext, String string) {
        return PolyClassItem.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
