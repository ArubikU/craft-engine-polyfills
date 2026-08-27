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
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler6
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassBlock {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler3 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler0 h$4;
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

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"cycle_prop", (String)"S:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"cycle_prop");
        h$2 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"offset", (String)"DDD:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Block", (String)"offset");
        h$4 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Block", (String)"glue_structure", (String)":R");
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
        p$39 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"metadata_types");
        p$40 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"fluid_flow");
        p$41 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"combined_light");
        p$42 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_source_fluid");
        p$43 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"world");
        p$44 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"pos");
        p$45 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"sky_light");
        p$46 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"facing_block");
        p$47 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_custom");
        p$48 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"id");
        p$49 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_air");
        p$50 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"has_fluid");
        p$51 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_water");
        p$52 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"metadata_type");
        p$53 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"block_light");
        p$54 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"hardness");
        p$55 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"block_state");
        p$56 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"get_metadata");
        p$57 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"light_level");
        p$58 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"biome");
        p$59 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"machine");
        p$60 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_container");
        p$61 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"is_glued");
        p$62 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"face_blocks");
        p$63 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"location");
        p$64 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"vanilla_id");
        p$65 = PolyClassRuntime.resolvePropertyHandler((String)"Block", (String)"redstone");
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
        if (h$4 != null) {
            return (ScriptValue)h$4.call(this.instance);
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

    public ScriptValue pg$39_metadata_types() {
        if (p$39 != null) {
            return p$39.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"metadata_types", (Object)this.instance);
    }

    public ScriptValue pg$40_fluid_flow() {
        if (p$40 != null) {
            return p$40.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"fluid_flow", (Object)this.instance);
    }

    public ScriptValue pg$41_combined_light() {
        if (p$41 != null) {
            return p$41.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"combined_light", (Object)this.instance);
    }

    public ScriptValue pg$42_is_source_fluid() {
        if (p$42 != null) {
            return p$42.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_source_fluid", (Object)this.instance);
    }

    public ScriptValue pg$43_world() {
        if (p$43 != null) {
            return p$43.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$44_pos() {
        if (p$44 != null) {
            return p$44.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"pos", (Object)this.instance);
    }

    public ScriptValue pg$45_sky_light() {
        if (p$45 != null) {
            return p$45.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"sky_light", (Object)this.instance);
    }

    public ScriptValue pg$46_facing_block() {
        if (p$46 != null) {
            return p$46.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"facing_block", (Object)this.instance);
    }

    public ScriptValue pg$47_is_custom() {
        if (p$47 != null) {
            return p$47.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_custom", (Object)this.instance);
    }

    public ScriptValue pg$48_id() {
        if (p$48 != null) {
            return p$48.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"id", (Object)this.instance);
    }

    public ScriptValue pg$49_is_air() {
        if (p$49 != null) {
            return p$49.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_air", (Object)this.instance);
    }

    public ScriptValue pg$50_has_fluid() {
        if (p$50 != null) {
            return p$50.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"has_fluid", (Object)this.instance);
    }

    public ScriptValue pg$51_is_water() {
        if (p$51 != null) {
            return p$51.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_water", (Object)this.instance);
    }

    public ScriptValue pg$52_metadata_type() {
        if (p$52 != null) {
            return p$52.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"metadata_type", (Object)this.instance);
    }

    public ScriptValue pg$53_block_light() {
        if (p$53 != null) {
            return p$53.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"block_light", (Object)this.instance);
    }

    public ScriptValue pg$54_hardness() {
        if (p$54 != null) {
            return p$54.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"hardness", (Object)this.instance);
    }

    public ScriptValue pg$55_block_state() {
        if (p$55 != null) {
            return p$55.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"block_state", (Object)this.instance);
    }

    public ScriptValue pg$56_get_metadata() {
        if (p$56 != null) {
            return p$56.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"get_metadata", (Object)this.instance);
    }

    public ScriptValue pg$57_light_level() {
        if (p$57 != null) {
            return p$57.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"light_level", (Object)this.instance);
    }

    public ScriptValue pg$58_biome() {
        if (p$58 != null) {
            return p$58.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"biome", (Object)this.instance);
    }

    public ScriptValue pg$59_machine() {
        if (p$59 != null) {
            return p$59.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"machine", (Object)this.instance);
    }

    public ScriptValue pg$60_is_container() {
        if (p$60 != null) {
            return p$60.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_container", (Object)this.instance);
    }

    public ScriptValue pg$61_is_glued() {
        if (p$61 != null) {
            return p$61.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"is_glued", (Object)this.instance);
    }

    public ScriptValue pg$62_face_blocks() {
        if (p$62 != null) {
            return p$62.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"face_blocks", (Object)this.instance);
    }

    public ScriptValue pg$63_location() {
        if (p$63 != null) {
            return p$63.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"location", (Object)this.instance);
    }

    public ScriptValue pg$64_vanilla_id() {
        if (p$64 != null) {
            return p$64.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"vanilla_id", (Object)this.instance);
    }

    public ScriptValue pg$65_redstone() {
        if (p$65 != null) {
            return p$65.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Block", (String)"redstone", (Object)this.instance);
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
}
