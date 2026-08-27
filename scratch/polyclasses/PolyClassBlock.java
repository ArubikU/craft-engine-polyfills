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
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler6
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

public class PolyClassBlock {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler3 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler0 h$4;
    private static volatile PolyType.TypeCodec c$4_r;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler1 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler1 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler1 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler3 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.TypedMethodHandler2 h$16;
    private static volatile PolyType.MethodHandler m$17;
    private static volatile PolyType.TypedMethodHandler2 h$18;
    private static volatile PolyType.MethodHandler m$19;
    private static volatile PolyType.TypedMethodHandler0 h$20;
    private static volatile PolyType.MethodHandler m$21;
    private static volatile PolyType.TypedMethodHandler1 h$22;
    private static volatile PolyType.MethodHandler m$23;
    private static volatile PolyType.TypedMethodHandler1 h$24;
    private static volatile PolyType.MethodHandler m$25;
    private static volatile PolyType.TypedMethodHandler1 h$26;
    private static volatile PolyType.MethodHandler m$27;
    private static volatile PolyType.TypedMethodHandler1 h$28;
    private static volatile PolyType.MethodHandler m$29;
    private static volatile PolyType.TypedMethodHandler1 h$30;
    private static volatile PolyType.MethodHandler m$31;
    private static volatile PolyType.TypedMethodHandler0 h$32;
    private static volatile PolyType.MethodHandler m$33;
    private static volatile PolyType.TypedMethodHandler6 h$34;
    private static volatile PolyType.MethodHandler m$35;
    private static volatile PolyType.TypedMethodHandler1 h$36;
    private static volatile PolyType.MethodHandler m$37;
    private static volatile PolyType.PropertyHandler p$38;
    private static volatile PolyType.TypedPropertyHandler tp$39;
    private static volatile PolyType.PropertyHandler p$40;
    private static volatile PolyType.PropertyHandler p$41;
    private static volatile PolyType.PropertyHandler p$42;
    private static volatile PolyType.TypedPropertyHandler tp$43;
    private static volatile PolyType.PropertyHandler p$44;
    private static volatile PolyType.TypedPropertyHandler tp$45;
    private static volatile PolyType.PropertyHandler p$46;
    private static volatile PolyType.PropertyHandler p$47;
    private static volatile PolyType.PropertyHandler p$48;
    private static volatile PolyType.TypedPropertyHandler tp$49;
    private static volatile PolyType.PropertyHandler p$50;
    private static volatile PolyType.PropertyHandler p$51;
    private static volatile PolyType.TypedPropertyHandler tp$52;
    private static volatile PolyType.PropertyHandler p$53;
    private static volatile PolyType.TypedPropertyHandler tp$54;
    private static volatile PolyType.PropertyHandler p$55;
    private static volatile PolyType.TypedPropertyHandler tp$56;
    private static volatile PolyType.PropertyHandler p$57;
    private static volatile PolyType.TypedPropertyHandler tp$58;
    private static volatile PolyType.PropertyHandler p$59;
    private static volatile PolyType.TypedPropertyHandler tp$60;
    private static volatile PolyType.PropertyHandler p$61;
    private static volatile PolyType.PropertyHandler p$62;
    private static volatile PolyType.TypedPropertyHandler tp$63;
    private static volatile PolyType.PropertyHandler p$64;
    private static volatile PolyType.TypedPropertyHandler tp$65;
    private static volatile PolyType.PropertyHandler p$66;
    private static volatile PolyType.PropertyHandler p$67;
    private static volatile PolyType.PropertyHandler p$68;
    private static volatile PolyType.TypedPropertyHandler tp$69;
    private static volatile PolyType.PropertyHandler p$70;
    private static volatile PolyType.TypedPropertyHandler tp$71;
    private static volatile PolyType.PropertyHandler p$72;
    private static volatile PolyType.PropertyHandler p$73;
    private static volatile PolyType.TypedPropertyHandler tp$74;
    private static volatile PolyType.PropertyHandler p$75;
    private static volatile PolyType.TypedPropertyHandler tp$76;
    private static volatile PolyType.PropertyHandler p$77;
    private static volatile PolyType.PropertyHandler p$78;
    private static volatile PolyType.PropertyHandler p$79;
    private static volatile PolyType.TypedPropertyHandler tp$80;
    private static volatile PolyType.PropertyHandler p$81;
    private static volatile PolyType.TypedPropertyHandler tp$82;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"cycle_prop", (String)"S:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"cycle_prop");
        h$2 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"offset", (String)"DDD:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"offset");
        h$4 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"glue_structure", (String)":L");
        c$4_r = PolyClassRuntime.resolveListCodec((String)"Block", (String)"glue_structure", (int)-1);
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"glue_structure");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"is_player_looking", (String)"R:Z");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"is_player_looking");
        h$8 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"replace", (String)"S:Z");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"replace");
        h$10 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"facing", (String)"R:R");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"facing");
        h$12 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"hit_face", (String)"R:R");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"hit_face");
        h$14 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"play_sound", (String)"SDD:Z");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"play_sound");
        h$16 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"set_property", (String)"SS:Z");
        m$17 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"set_property");
        h$18 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"place_custom", (String)"SR:Z");
        m$19 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"place_custom");
        h$20 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"break_and_drop", (String)":R");
        m$21 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"break_and_drop");
        h$22 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"entities", (String)"D:R");
        m$23 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"entities");
        h$24 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"property", (String)"S:R");
        m$25 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"property");
        h$26 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"hit_uv", (String)"R:R");
        m$27 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"hit_uv");
        h$28 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"has_property", (String)"S:Z");
        m$29 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"has_property");
        h$30 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"place_from_item", (String)"R:Z");
        m$31 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"place_from_item");
        h$32 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"apply_bone_meal", (String)":Z");
        m$33 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"apply_bone_meal");
        h$34 = (PolyType.TypedMethodHandler6)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"hit_in_area", (String)"RSDDDD:Z");
        m$35 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"hit_in_area");
        h$36 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"relative", (String)"S:R");
        m$37 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"relative");
        p$38 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"powered");
        tp$39 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"powered", (String)"Z");
        p$40 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"metadata_types");
        p$41 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"fluid_flow");
        p$42 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"combined_light");
        tp$43 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"combined_light", (String)"D");
        p$44 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_source_fluid");
        tp$45 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"is_source_fluid", (String)"Z");
        p$46 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"world");
        p$47 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"pos");
        p$48 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"sky_light");
        tp$49 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"sky_light", (String)"D");
        p$50 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"facing_block");
        p$51 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_custom");
        tp$52 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"is_custom", (String)"Z");
        p$53 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"id");
        tp$54 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"id", (String)"S");
        p$55 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_air");
        tp$56 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"is_air", (String)"Z");
        p$57 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"has_fluid");
        tp$58 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"has_fluid", (String)"Z");
        p$59 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_water");
        tp$60 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"is_water", (String)"Z");
        p$61 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"metadata_type");
        p$62 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"block_light");
        tp$63 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"block_light", (String)"D");
        p$64 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"hardness");
        tp$65 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"hardness", (String)"D");
        p$66 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"block_state");
        p$67 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"get_metadata");
        p$68 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"light_level");
        tp$69 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"light_level", (String)"D");
        p$70 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"biome");
        tp$71 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"biome", (String)"S");
        p$72 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"machine");
        p$73 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_container");
        tp$74 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"is_container", (String)"Z");
        p$75 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_glued");
        tp$76 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"is_glued", (String)"Z");
        p$77 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"face_blocks");
        p$78 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"location");
        p$79 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"vanilla_id");
        tp$80 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"vanilla_id", (String)"S");
        p$81 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"redstone");
        tp$82 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Block", (String)"redstone", (String)"D");
    }

    public boolean tm$0_cycle_prop(String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"cycle_prop", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_cycle_prop(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"cycle_prop", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_offset(double d, double d2, double d3) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"offset", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$3_offset(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"offset", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$4_glue_structure() {
        if (h$4 != null && c$4_r != null) {
            return c$4_r.encode(h$4.call(this.instance));
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"glue_structure", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$5_glue_structure(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"glue_structure", (Object)this.instance, (List)list);
    }

    public boolean tm$6_is_player_looking(ScriptValue scriptValue) {
        if (h$6 != null) {
            return (Boolean)h$6.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"is_player_looking", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$7_is_player_looking(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"is_player_looking", (Object)this.instance, (List)list);
    }

    public boolean tm$8_replace(String string) {
        if (h$8 != null) {
            return (Boolean)h$8.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"replace", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$9_replace(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"replace", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$10_facing(ScriptValue scriptValue) {
        if (h$10 != null) {
            return (ScriptValue)h$10.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"facing", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$11_facing(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"facing", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$12_hit_face(ScriptValue scriptValue) {
        if (h$12 != null) {
            return (ScriptValue)h$12.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"hit_face", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$13_hit_face(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"hit_face", (Object)this.instance, (List)list);
    }

    public boolean tm$14_play_sound(String string, double d, double d2) {
        if (h$14 != null) {
            return (Boolean)h$14.call(this.instance, (Object)string, (Object)d, (Object)d2);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"play_sound", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d), ScriptValue.of((double)d2)}).asBool();
    }

    public ScriptValue um$15_play_sound(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"play_sound", (Object)this.instance, (List)list);
    }

    public boolean tm$16_set_property(String string, String string2) {
        if (h$16 != null) {
            return (Boolean)h$16.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"set_property", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)}).asBool();
    }

    public ScriptValue um$17_set_property(List list) {
        if (m$17 != null) {
            return m$17.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"set_property", (Object)this.instance, (List)list);
    }

    public boolean tm$18_place_custom(String string, ScriptValue scriptValue) {
        if (h$18 != null) {
            return (Boolean)h$18.call(this.instance, (Object)string, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"place_custom", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), scriptValue}).asBool();
    }

    public ScriptValue um$19_place_custom(List list) {
        if (m$19 != null) {
            return m$19.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"place_custom", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$20_break_and_drop() {
        if (h$20 != null) {
            return (ScriptValue)h$20.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"break_and_drop", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$21_break_and_drop(List list) {
        if (m$21 != null) {
            return m$21.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"break_and_drop", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$22_entities(double d) {
        if (h$22 != null) {
            return (ScriptValue)h$22.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"entities", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$23_entities(List list) {
        if (m$23 != null) {
            return m$23.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"entities", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$24_property(String string) {
        if (h$24 != null) {
            return (ScriptValue)h$24.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"property", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$25_property(List list) {
        if (m$25 != null) {
            return m$25.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"property", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$26_hit_uv(ScriptValue scriptValue) {
        if (h$26 != null) {
            return (ScriptValue)h$26.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"hit_uv", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$27_hit_uv(List list) {
        if (m$27 != null) {
            return m$27.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"hit_uv", (Object)this.instance, (List)list);
    }

    public boolean tm$28_has_property(String string) {
        if (h$28 != null) {
            return (Boolean)h$28.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"has_property", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$29_has_property(List list) {
        if (m$29 != null) {
            return m$29.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"has_property", (Object)this.instance, (List)list);
    }

    public boolean tm$30_place_from_item(ScriptValue scriptValue) {
        if (h$30 != null) {
            return (Boolean)h$30.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"place_from_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$31_place_from_item(List list) {
        if (m$31 != null) {
            return m$31.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"place_from_item", (Object)this.instance, (List)list);
    }

    public boolean tm$32_apply_bone_meal() {
        if (h$32 != null) {
            return (Boolean)h$32.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"apply_bone_meal", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$33_apply_bone_meal(List list) {
        if (m$33 != null) {
            return m$33.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"apply_bone_meal", (Object)this.instance, (List)list);
    }

    public boolean tm$34_hit_in_area(ScriptValue scriptValue, String string, double d, double d2, double d3, double d4) {
        if (h$34 != null) {
            return (Boolean)h$34.call(this.instance, (Object)scriptValue, (Object)string, (Object)d, (Object)d2, (Object)d3, (Object)d4);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"hit_in_area", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string), ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3), ScriptValue.of((double)d4)}).asBool();
    }

    public ScriptValue um$35_hit_in_area(List list) {
        if (m$35 != null) {
            return m$35.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"hit_in_area", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$36_relative(String string) {
        if (h$36 != null) {
            return (ScriptValue)h$36.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Block", (String)"relative", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$37_relative(List list) {
        if (m$37 != null) {
            return m$37.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Block", (String)"relative", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$38_powered() {
        if (p$38 != null) {
            return p$38.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"powered", (Object)this.instance);
    }

    public boolean tg$39_powered() {
        if (tp$39 != null) {
            return (Boolean)tp$39.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"powered", (Object)this.instance).asBool();
    }

    public ScriptValue pg$40_metadata_types() {
        if (p$40 != null) {
            return p$40.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"metadata_types", (Object)this.instance);
    }

    public ScriptValue pg$41_fluid_flow() {
        if (p$41 != null) {
            return p$41.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"fluid_flow", (Object)this.instance);
    }

    public ScriptValue pg$42_combined_light() {
        if (p$42 != null) {
            return p$42.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"combined_light", (Object)this.instance);
    }

    public double tg$43_combined_light() {
        if (tp$43 != null) {
            return (Double)tp$43.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"combined_light", (Object)this.instance).asNum();
    }

    public ScriptValue pg$44_is_source_fluid() {
        if (p$44 != null) {
            return p$44.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_source_fluid", (Object)this.instance);
    }

    public boolean tg$45_is_source_fluid() {
        if (tp$45 != null) {
            return (Boolean)tp$45.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_source_fluid", (Object)this.instance).asBool();
    }

    public ScriptValue pg$46_world() {
        if (p$46 != null) {
            return p$46.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$47_pos() {
        if (p$47 != null) {
            return p$47.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"pos", (Object)this.instance);
    }

    public ScriptValue pg$48_sky_light() {
        if (p$48 != null) {
            return p$48.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"sky_light", (Object)this.instance);
    }

    public double tg$49_sky_light() {
        if (tp$49 != null) {
            return (Double)tp$49.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"sky_light", (Object)this.instance).asNum();
    }

    public ScriptValue pg$50_facing_block() {
        if (p$50 != null) {
            return p$50.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"facing_block", (Object)this.instance);
    }

    public ScriptValue pg$51_is_custom() {
        if (p$51 != null) {
            return p$51.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_custom", (Object)this.instance);
    }

    public boolean tg$52_is_custom() {
        if (tp$52 != null) {
            return (Boolean)tp$52.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_custom", (Object)this.instance).asBool();
    }

    public ScriptValue pg$53_id() {
        if (p$53 != null) {
            return p$53.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"id", (Object)this.instance);
    }

    public String tg$54_id() {
        if (tp$54 != null) {
            return (String)tp$54.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"id", (Object)this.instance).asStr();
    }

    public ScriptValue pg$55_is_air() {
        if (p$55 != null) {
            return p$55.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_air", (Object)this.instance);
    }

    public boolean tg$56_is_air() {
        if (tp$56 != null) {
            return (Boolean)tp$56.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_air", (Object)this.instance).asBool();
    }

    public ScriptValue pg$57_has_fluid() {
        if (p$57 != null) {
            return p$57.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"has_fluid", (Object)this.instance);
    }

    public boolean tg$58_has_fluid() {
        if (tp$58 != null) {
            return (Boolean)tp$58.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"has_fluid", (Object)this.instance).asBool();
    }

    public ScriptValue pg$59_is_water() {
        if (p$59 != null) {
            return p$59.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_water", (Object)this.instance);
    }

    public boolean tg$60_is_water() {
        if (tp$60 != null) {
            return (Boolean)tp$60.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_water", (Object)this.instance).asBool();
    }

    public ScriptValue pg$61_metadata_type() {
        if (p$61 != null) {
            return p$61.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"metadata_type", (Object)this.instance);
    }

    public ScriptValue pg$62_block_light() {
        if (p$62 != null) {
            return p$62.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"block_light", (Object)this.instance);
    }

    public double tg$63_block_light() {
        if (tp$63 != null) {
            return (Double)tp$63.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"block_light", (Object)this.instance).asNum();
    }

    public ScriptValue pg$64_hardness() {
        if (p$64 != null) {
            return p$64.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"hardness", (Object)this.instance);
    }

    public double tg$65_hardness() {
        if (tp$65 != null) {
            return (Double)tp$65.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"hardness", (Object)this.instance).asNum();
    }

    public ScriptValue pg$66_block_state() {
        if (p$66 != null) {
            return p$66.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"block_state", (Object)this.instance);
    }

    public ScriptValue pg$67_get_metadata() {
        if (p$67 != null) {
            return p$67.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"get_metadata", (Object)this.instance);
    }

    public ScriptValue pg$68_light_level() {
        if (p$68 != null) {
            return p$68.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"light_level", (Object)this.instance);
    }

    public double tg$69_light_level() {
        if (tp$69 != null) {
            return (Double)tp$69.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"light_level", (Object)this.instance).asNum();
    }

    public ScriptValue pg$70_biome() {
        if (p$70 != null) {
            return p$70.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"biome", (Object)this.instance);
    }

    public String tg$71_biome() {
        if (tp$71 != null) {
            return (String)tp$71.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"biome", (Object)this.instance).asStr();
    }

    public ScriptValue pg$72_machine() {
        if (p$72 != null) {
            return p$72.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"machine", (Object)this.instance);
    }

    public ScriptValue pg$73_is_container() {
        if (p$73 != null) {
            return p$73.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_container", (Object)this.instance);
    }

    public boolean tg$74_is_container() {
        if (tp$74 != null) {
            return (Boolean)tp$74.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_container", (Object)this.instance).asBool();
    }

    public ScriptValue pg$75_is_glued() {
        if (p$75 != null) {
            return p$75.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_glued", (Object)this.instance);
    }

    public boolean tg$76_is_glued() {
        if (tp$76 != null) {
            return (Boolean)tp$76.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_glued", (Object)this.instance).asBool();
    }

    public ScriptValue pg$77_face_blocks() {
        if (p$77 != null) {
            return p$77.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"face_blocks", (Object)this.instance);
    }

    public ScriptValue pg$78_location() {
        if (p$78 != null) {
            return p$78.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"location", (Object)this.instance);
    }

    public ScriptValue pg$79_vanilla_id() {
        if (p$79 != null) {
            return p$79.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"vanilla_id", (Object)this.instance);
    }

    public String tg$80_vanilla_id() {
        if (tp$80 != null) {
            return (String)tp$80.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"vanilla_id", (Object)this.instance).asStr();
    }

    public ScriptValue pg$81_redstone() {
        if (p$81 != null) {
            return p$81.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"redstone", (Object)this.instance);
    }

    public double tg$82_redstone() {
        if (tp$82 != null) {
            return (Double)tp$82.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"redstone", (Object)this.instance).asNum();
    }

    public PolyClassBlock(Object object) {
        this.instance = object;
    }

    public static PolyClassBlock of(Object object) {
        return new PolyClassBlock(object);
    }

    public static PolyClassBlock ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
            return new PolyClassBlock(object);
        }
        return null;
    }

    public static PolyClassBlock ofVar(ScriptContext scriptContext, String string) {
        return PolyClassBlock.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
