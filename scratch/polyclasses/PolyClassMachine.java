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
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler4
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassMachine
extends PolyClassBlock_v2 {
    private static volatile PolyType.TypedMethodHandler3 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler2 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.MethodHandler m$4;
    private static volatile PolyType.TypedMethodHandler1 h$5;
    private static volatile PolyType.MethodHandler m$6;
    private static volatile PolyType.TypedMethodHandler1 h$7;
    private static volatile PolyType.MethodHandler m$8;
    private static volatile PolyType.TypedMethodHandler1 h$9;
    private static volatile PolyType.MethodHandler m$10;
    private static volatile PolyType.TypedMethodHandler1 h$11;
    private static volatile PolyType.MethodHandler m$12;
    private static volatile PolyType.TypedMethodHandler1 h$13;
    private static volatile PolyType.MethodHandler m$14;
    private static volatile PolyType.TypedMethodHandler1 h$15;
    private static volatile PolyType.MethodHandler m$16;
    private static volatile PolyType.TypedMethodHandler3 h$17;
    private static volatile PolyType.MethodHandler m$18;
    private static volatile PolyType.MethodHandler m$19;
    private static volatile PolyType.TypedMethodHandler1 h$20;
    private static volatile PolyType.MethodHandler m$21;
    private static volatile PolyType.TypedMethodHandler1 h$22;
    private static volatile PolyType.MethodHandler m$23;
    private static volatile PolyType.TypedMethodHandler1 h$24;
    private static volatile PolyType.MethodHandler m$25;
    private static volatile PolyType.TypedMethodHandler0 h$26;
    private static volatile PolyType.MethodHandler m$27;
    private static volatile PolyType.TypedMethodHandler2 h$28;
    private static volatile PolyType.MethodHandler m$29;
    private static volatile PolyType.TypedMethodHandler1 h$30;
    private static volatile PolyType.MethodHandler m$31;
    private static volatile PolyType.TypedMethodHandler0 h$32;
    private static volatile PolyType.MethodHandler m$33;
    private static volatile PolyType.TypedMethodHandler2 h$34;
    private static volatile PolyType.MethodHandler m$35;
    private static volatile PolyType.TypedMethodHandler2 h$36;
    private static volatile PolyType.MethodHandler m$37;
    private static volatile PolyType.TypedMethodHandler1 h$38;
    private static volatile PolyType.MethodHandler m$39;
    private static volatile PolyType.TypedMethodHandler1 h$40;
    private static volatile PolyType.MethodHandler m$41;
    private static volatile PolyType.TypedMethodHandler2 h$42;
    private static volatile PolyType.MethodHandler m$43;
    private static volatile PolyType.TypedMethodHandler1 h$44;
    private static volatile PolyType.MethodHandler m$45;
    private static volatile PolyType.TypedMethodHandler0 h$46;
    private static volatile PolyType.MethodHandler m$47;
    private static volatile PolyType.TypedMethodHandler1 h$48;
    private static volatile PolyType.MethodHandler m$49;
    private static volatile PolyType.TypedMethodHandler4 h$50;
    private static volatile PolyType.MethodHandler m$51;
    private static volatile PolyType.TypedMethodHandler1 h$52;
    private static volatile PolyType.MethodHandler m$53;
    private static volatile PolyType.TypedMethodHandler4 h$54;
    private static volatile PolyType.MethodHandler m$55;
    private static volatile PolyType.TypedMethodHandler1 h$56;
    private static volatile PolyType.MethodHandler m$57;
    private static volatile PolyType.TypedMethodHandler1 h$58;
    private static volatile PolyType.MethodHandler m$59;
    private static volatile PolyType.TypedMethodHandler0 h$60;
    private static volatile PolyType.MethodHandler m$61;
    private static volatile PolyType.TypedMethodHandler3 h$62;
    private static volatile PolyType.MethodHandler m$63;
    private static volatile PolyType.TypedMethodHandler1 h$64;
    private static volatile PolyType.MethodHandler m$65;
    private static volatile PolyType.TypedMethodHandler2 h$66;
    private static volatile PolyType.MethodHandler m$67;
    private static volatile PolyType.TypedMethodHandler3 h$68;
    private static volatile PolyType.MethodHandler m$69;
    private static volatile PolyType.TypedMethodHandler1 h$70;
    private static volatile PolyType.MethodHandler m$71;
    private static volatile PolyType.TypedMethodHandler3 h$72;
    private static volatile PolyType.MethodHandler m$73;
    private static volatile PolyType.TypedMethodHandler0 h$74;
    private static volatile PolyType.MethodHandler m$75;
    private static volatile PolyType.TypedMethodHandler0 h$76;
    private static volatile PolyType.MethodHandler m$77;
    private static volatile PolyType.TypedMethodHandler1 h$78;
    private static volatile PolyType.MethodHandler m$79;
    private static volatile PolyType.TypedMethodHandler0 h$80;
    private static volatile PolyType.MethodHandler m$81;
    private static volatile PolyType.TypedMethodHandler3 h$82;
    private static volatile PolyType.MethodHandler m$83;
    private static volatile PolyType.TypedMethodHandler1 h$84;
    private static volatile PolyType.MethodHandler m$85;
    private static volatile PolyType.TypedMethodHandler1 h$86;
    private static volatile PolyType.TypeCodec c$86_r;
    private static volatile PolyType.MethodHandler m$87;
    private static volatile PolyType.TypedMethodHandler2 h$88;
    private static volatile PolyType.MethodHandler m$89;
    private static volatile PolyType.TypedMethodHandler0 h$90;
    private static volatile PolyType.MethodHandler m$91;
    private static volatile PolyType.TypedMethodHandler0 h$92;
    private static volatile PolyType.MethodHandler m$93;
    private static volatile PolyType.TypedMethodHandler1 h$94;
    private static volatile PolyType.MethodHandler m$95;
    private static volatile PolyType.TypedMethodHandler1 h$96;
    private static volatile PolyType.MethodHandler m$97;
    private static volatile PolyType.TypedMethodHandler2 h$98;
    private static volatile PolyType.MethodHandler m$99;
    private static volatile PolyType.TypedMethodHandler1 h$100;
    private static volatile PolyType.MethodHandler m$101;
    private static volatile PolyType.TypedMethodHandler0 h$102;
    private static volatile PolyType.MethodHandler m$103;
    private static volatile PolyType.TypedMethodHandler2 h$104;
    private static volatile PolyType.MethodHandler m$105;
    private static volatile PolyType.TypedMethodHandler1 h$106;
    private static volatile PolyType.MethodHandler m$107;
    private static volatile PolyType.TypedMethodHandler1 h$108;
    private static volatile PolyType.MethodHandler m$109;
    private static volatile PolyType.TypedMethodHandler0 h$110;
    private static volatile PolyType.MethodHandler m$111;
    private static volatile PolyType.TypedMethodHandler1 h$112;
    private static volatile PolyType.MethodHandler m$113;
    private static volatile PolyType.TypedMethodHandler1 h$114;
    private static volatile PolyType.MethodHandler m$115;
    private static volatile PolyType.TypedMethodHandler3 h$116;
    private static volatile PolyType.MethodHandler m$117;
    private static volatile PolyType.PropertyHandler p$118;
    private static volatile PolyType.TypedPropertyHandler tp$119;
    private static volatile PolyType.PropertyHandler p$120;
    private static volatile PolyType.PropertyHandler p$121;
    private static volatile PolyType.TypedPropertyHandler tp$122;
    private static volatile PolyType.PropertyHandler p$123;
    private static volatile PolyType.TypedPropertyHandler tp$124;
    private static volatile PolyType.PropertyHandler p$125;
    private static volatile PolyType.TypedPropertyHandler tp$126;
    private static volatile PolyType.PropertyHandler p$127;
    private static volatile PolyType.TypedPropertyHandler tp$128;
    private static volatile PolyType.PropertyHandler p$129;
    private static volatile PolyType.TypedPropertyHandler tp$130;
    private static volatile PolyType.PropertyHandler p$131;
    private static volatile PolyType.TypedPropertyHandler tp$132;
    private static volatile PolyType.PropertyHandler p$133;
    private static volatile PolyType.TypedPropertyHandler tp$134;
    private static volatile PolyType.PropertyHandler p$135;
    private static volatile PolyType.TypedPropertyHandler tp$136;
    private static volatile PolyType.PropertyHandler p$137;
    private static volatile PolyType.PropertyHandler p$138;
    private static volatile PolyType.PropertyHandler p$139;
    private static volatile PolyType.PropertyHandler p$140;
    private static volatile PolyType.TypedPropertyHandler tp$141;
    private static volatile PolyType.PropertyHandler p$142;
    private static volatile PolyType.TypedPropertyHandler tp$143;
    private static volatile PolyType.PropertyHandler p$144;
    private static volatile PolyType.TypedPropertyHandler tp$145;
    private static volatile PolyType.PropertyHandler p$146;
    private static volatile PolyType.PropertyHandler p$147;
    private static volatile PolyType.TypedPropertyHandler tp$148;
    private static volatile PolyType.PropertyHandler p$149;
    private static volatile PolyType.TypedPropertyHandler tp$150;
    private static volatile PolyType.PropertyHandler p$151;
    private static volatile PolyType.TypedPropertyHandler tp$152;
    private static volatile PolyType.PropertyHandler p$153;
    private static volatile PolyType.TypedPropertyHandler tp$154;
    private static volatile PolyType.PropertyHandler p$155;
    private static volatile PolyType.TypedPropertyHandler tp$156;
    private static volatile PolyType.PropertyHandler p$157;
    private static volatile PolyType.PropertyHandler p$158;
    private static volatile PolyType.PropertyHandler p$159;
    private static volatile PolyType.TypedPropertyHandler tp$160;
    private static volatile PolyType.PropertyHandler p$161;
    private static volatile PolyType.PropertyHandler p$162;
    private static volatile PolyType.TypedPropertyHandler tp$163;
    private static volatile PolyType.PropertyHandler p$164;
    private static volatile PolyType.TypedPropertyHandler tp$165;
    private static volatile PolyType.PropertyHandler p$166;
    private static volatile PolyType.TypedPropertyHandler tp$167;
    private static volatile PolyType.PropertyHandler p$168;
    private static volatile PolyType.TypedPropertyHandler tp$169;
    private static volatile PolyType.PropertyHandler p$170;
    private static volatile PolyType.PropertyHandler p$171;
    private static volatile PolyType.TypedPropertyHandler tp$172;
    private static volatile PolyType.PropertyHandler p$173;
    private static volatile PolyType.TypedPropertyHandler tp$174;
    private static volatile PolyType.PropertyHandler p$175;
    private static volatile PolyType.TypedPropertyHandler tp$176;
    private static volatile PolyType.PropertyHandler p$177;
    private static volatile PolyType.PropertyHandler p$178;
    private static volatile PolyType.TypedPropertyHandler tp$179;
    private static volatile PolyType.PropertyHandler p$180;
    private static volatile PolyType.TypedPropertyHandler tp$181;
    private static volatile PolyType.PropertyHandler p$182;
    private static volatile PolyType.TypedPropertyHandler tp$183;
    private static volatile PolyType.PropertyHandler p$184;
    private static volatile PolyType.PropertyHandler p$185;
    private static volatile PolyType.PropertyHandler p$186;
    private static volatile PolyType.TypedPropertyHandler tp$187;
    private static volatile PolyType.PropertyHandler p$188;
    private static volatile PolyType.TypedPropertyHandler tp$189;
    private static volatile PolyType.PropertyHandler p$190;
    private static volatile PolyType.PropertyHandler p$191;
    private static volatile PolyType.PropertyHandler p$192;
    private static volatile PolyType.PropertyHandler p$193;
    private static volatile PolyType.TypedPropertyHandler tp$194;
    private static volatile PolyType.PropertyHandler p$195;
    private static volatile PolyType.TypedPropertyHandler tp$196;
    private static volatile PolyType.PropertyHandler p$197;
    private static volatile PolyType.TypedPropertyHandler tp$198;
    private static volatile PolyType.PropertyHandler p$199;
    private static volatile PolyType.TypedPropertyHandler tp$200;
    private static volatile PolyType.PropertyHandler p$201;
    private static volatile PolyType.TypedPropertyHandler tp$202;
    private static volatile PolyType.PropertyHandler p$203;
    private static volatile PolyType.TypedPropertyHandler tp$204;
    private static volatile PolyType.PropertyHandler p$205;
    private static volatile PolyType.TypedPropertyHandler tp$206;
    private static volatile PolyType.PropertyHandler p$207;
    private static volatile PolyType.TypedPropertyHandler tp$208;
    private static volatile PolyType.PropertyHandler p$209;
    private static volatile PolyType.TypedPropertyHandler tp$210;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"spawn_display", (String)"SRD:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"spawn_display");
        h$2 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"tick_break", (String)"RD:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"tick_break");
        m$4 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"drop_item");
        h$5 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_rpm_output_inverted", (String)"S:Z");
        m$6 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_rpm_output_inverted");
        h$7 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"turn_page", (String)"D:Z");
        m$8 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"turn_page");
        h$9 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_energy_max_output", (String)"D:Z");
        m$10 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_energy_max_output");
        h$11 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"consume_energy", (String)"D:Z");
        m$12 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"consume_energy");
        h$13 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"player_facing", (String)"S:Z");
        m$14 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"player_facing");
        h$15 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_rpm_output_new_network", (String)"S:Z");
        m$16 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_rpm_output_new_network");
        h$17 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"container_at", (String)"DDD:R");
        m$18 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"container_at");
        m$19 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"drop_item_toward");
        h$20 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"player_in_range", (String)"D:Z");
        m$21 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"player_in_range");
        h$22 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"play_animation", (String)"R:Z");
        m$23 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"play_animation");
        h$24 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"open_menu", (String)"R:Z");
        m$25 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"open_menu");
        h$26 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"recompute_io", (String)":Z");
        m$27 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"recompute_io");
        h$28 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"relay_to", (String)"RD:Z");
        m$29 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"relay_to");
        h$30 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"is_player_looking_at", (String)"D:Z");
        m$31 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"is_player_looking_at");
        h$32 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"release_contraption", (String)":Z");
        m$33 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"release_contraption");
        h$34 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"get_typed", (String)"SS:R");
        m$35 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"get_typed");
        h$36 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"push_rpm", (String)"RD:Z");
        m$37 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"push_rpm");
        h$38 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"rpm_out", (String)"S:D");
        m$39 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"rpm_out");
        h$40 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_rpm_input", (String)"S:Z");
        m$41 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_rpm_input");
        h$42 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"use_item_on_entity", (String)"RD:Z");
        m$43 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"use_item_on_entity");
        h$44 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_rpm_output_same", (String)"S:Z");
        m$45 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_rpm_output_same");
        h$46 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"rpm_out_faces", (String)":R");
        m$47 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"rpm_out_faces");
        h$48 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_energy_per_tick", (String)"D:Z");
        m$49 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_energy_per_tick");
        h$50 = (PolyType.TypedMethodHandler4)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"drop_item_at", (String)"RDDD:Z");
        m$51 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"drop_item_at");
        h$52 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"page", (String)"D:Z");
        m$53 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"page");
        h$54 = (PolyType.TypedMethodHandler4)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"io_set", (String)"SSSZ:Z");
        m$55 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"io_set");
        h$56 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"report_su", (String)"D:Z");
        m$57 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"report_su");
        h$58 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"get_gas_level", (String)"S:D");
        m$59 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"get_gas_level");
        h$60 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"pipe_register_network", (String)":Z");
        m$61 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"pipe_register_network");
        h$62 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"belt_at", (String)"DDD:R");
        m$63 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"belt_at");
        h$64 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"neighbor_block", (String)"S:R");
        m$65 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"neighbor_block");
        h$66 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"has_typed", (String)"SS:Z");
        m$67 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"has_typed");
        h$68 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"block_at", (String)"DDD:R");
        m$69 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"block_at");
        h$70 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"bump_overclock", (String)"D:Z");
        m$71 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"bump_overclock");
        h$72 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"io_get", (String)"SSS:Z");
        m$73 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"io_get");
        h$74 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"update", (String)":Z");
        m$75 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"update");
        h$76 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"prev_page", (String)":Z");
        m$77 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"prev_page");
        h$78 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"add_items", (String)"R:Z");
        m$79 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"add_items");
        h$80 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"next_page", (String)":Z");
        m$81 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"next_page");
        h$82 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_typed", (String)"SSR:Z");
        m$83 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_typed");
        h$84 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_overclock", (String)"D:Z");
        m$85 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_overclock");
        h$86 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"blocks_in_range", (String)"D:L");
        c$86_r = PolyClassRuntime.resolveListCodec((String)"Machine", (String)"blocks_in_range", (int)-1);
        m$87 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"blocks_in_range");
        h$88 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"consume_gas", (String)"SD:Z");
        m$89 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"consume_gas");
        h$90 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"clear_rpm_override", (String)":Z");
        m$91 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"clear_rpm_override");
        h$92 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"close", (String)":Z");
        m$93 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"close");
        h$94 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"nearby_entities", (String)"D:R");
        m$95 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"nearby_entities");
        h$96 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"get_renderer", (String)"S:R");
        m$97 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"get_renderer");
        h$98 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"fill_fluid", (String)"SD:Z");
        m$99 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"fill_fluid");
        h$100 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_rpm_output_same_inverted", (String)"S:Z");
        m$101 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_rpm_output_same_inverted");
        h$102 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"hold_contraption", (String)":Z");
        m$103 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"hold_contraption");
        h$104 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"update_display", (String)"SR:Z");
        m$105 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"update_display");
        h$106 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_rpm_output", (String)"D:Z");
        m$107 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_rpm_output");
        h$108 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"emit_redstone", (String)"D:Z");
        m$109 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"emit_redstone");
        h$110 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"connect_contraption", (String)":R");
        m$111 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"connect_contraption");
        h$112 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"set_energy_max_input", (String)"D:Z");
        m$113 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"set_energy_max_input");
        h$114 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"kill_display", (String)"S:Z");
        m$115 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"kill_display");
        h$116 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"Machine", (String)"to_item", (String)"SZZ:R");
        m$117 = PolyClassRuntime.resolveMethodHandler((String)"Machine", (String)"to_item");
        p$118 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"is_multi_cell");
        tp$119 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"is_multi_cell", (String)"Z");
        p$120 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"container");
        p$121 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"has_contraption");
        tp$122 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"has_contraption", (String)"Z");
        p$123 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"energy_max_output");
        tp$124 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"energy_max_output", (String)"D");
        p$125 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"energy_stored");
        tp$126 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"energy_stored", (String)"D");
        p$127 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"axis");
        tp$128 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"axis", (String)"S");
        p$129 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"facing_dy");
        tp$130 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"facing_dy", (String)"D");
        p$131 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"facing_dz");
        tp$132 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"facing_dz", (String)"D");
        p$133 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"facing_dx");
        tp$134 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"facing_dx", (String)"D");
        p$135 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"last_transfer_tick");
        tp$136 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"last_transfer_tick", (String)"D");
        p$137 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"facing_block");
        p$138 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"gas_tanks");
        p$139 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"block");
        p$140 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"current_page");
        tp$141 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"current_page", (String)"D");
        p$142 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"page_count");
        tp$143 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"page_count", (String)"D");
        p$144 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"generation");
        tp$145 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"generation", (String)"D");
        p$146 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"recipes");
        p$147 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"progress_percent");
        tp$148 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"progress_percent", (String)"D");
        p$149 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"efficiency");
        tp$150 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"efficiency", (String)"D");
        p$151 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"ticks_alive");
        tp$152 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"ticks_alive", (String)"D");
        p$153 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"source_distance");
        tp$154 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"source_distance", (String)"D");
        p$155 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"overclock_limit");
        tp$156 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"overclock_limit", (String)"D");
        p$157 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"belt");
        p$158 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"io");
        p$159 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"energy_real_output");
        tp$160 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"energy_real_output", (String)"D");
        p$161 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"working_recipe");
        p$162 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"energy_capacity");
        tp$163 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"energy_capacity", (String)"D");
        p$164 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"cell_count");
        tp$165 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"cell_count", (String)"D");
        p$166 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"network_capacity");
        tp$167 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"network_capacity", (String)"D");
        p$168 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"meter_state");
        tp$169 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"meter_state", (String)"D");
        p$170 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"redstone");
        p$171 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"overclock");
        tp$172 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"overclock", (String)"D");
        p$173 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"energy_max_input");
        tp$174 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"energy_max_input", (String)"D");
        p$175 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"energy_per_tick");
        tp$176 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"energy_per_tick", (String)"D");
        p$177 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"fluid_tanks");
        p$178 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"energy_real_input");
        tp$179 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"energy_real_input", (String)"D");
        p$180 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"rename_text");
        tp$181 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"rename_text", (String)"S");
        p$182 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"rpm_network");
        tp$183 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"rpm_network", (String)"D");
        p$184 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"pos");
        p$185 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"matching_recipe");
        p$186 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"max_progress");
        tp$187 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"max_progress", (String)"D");
        p$188 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"network_stress");
        tp$189 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"network_stress", (String)"D");
        p$190 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"contraption");
        p$191 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"bars");
        p$192 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"layout");
        p$193 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"max_burn_time");
        tp$194 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"max_burn_time", (String)"D");
        p$195 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"burn_time");
        tp$196 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"burn_time", (String)"D");
        p$197 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"is_overstressed");
        tp$198 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"is_overstressed", (String)"Z");
        p$199 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"x");
        tp$200 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"x", (String)"D");
        p$201 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"y");
        tp$202 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"y", (String)"D");
        p$203 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"progress");
        tp$204 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"progress", (String)"D");
        p$205 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"z");
        tp$206 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"z", (String)"D");
        p$207 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"activated");
        tp$208 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"activated", (String)"Z");
        p$209 = PolyClassRuntime.resolvePropertyHandler((String)"Machine", (String)"owner_uuid");
        tp$210 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Machine", (String)"owner_uuid", (String)"S");
    }

    public ScriptValue tm$0_spawn_display(String string, ScriptValue scriptValue, double d) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)string, (Object)scriptValue, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"spawn_display", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), scriptValue, ScriptValue.of((double)d)});
    }

    public ScriptValue um$1_spawn_display(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"spawn_display", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_tick_break(ScriptValue scriptValue, double d) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)scriptValue, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"tick_break", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((double)d)});
    }

    public ScriptValue um$3_tick_break(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"tick_break", (Object)this.instance, (List)list);
    }

    public ScriptValue um$4_drop_item(List list) {
        if (m$4 != null) {
            return m$4.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"drop_item", (Object)this.instance, (List)list);
    }

    public boolean tm$5_set_rpm_output_inverted(String string) {
        if (h$5 != null) {
            return (Boolean)h$5.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_rpm_output_inverted", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$6_set_rpm_output_inverted(List list) {
        if (m$6 != null) {
            return m$6.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_rpm_output_inverted", (Object)this.instance, (List)list);
    }

    public boolean tm$7_turn_page(double d) {
        if (h$7 != null) {
            return (Boolean)h$7.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"turn_page", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$8_turn_page(List list) {
        if (m$8 != null) {
            return m$8.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"turn_page", (Object)this.instance, (List)list);
    }

    public boolean tm$9_set_energy_max_output(double d) {
        if (h$9 != null) {
            return (Boolean)h$9.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_energy_max_output", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$10_set_energy_max_output(List list) {
        if (m$10 != null) {
            return m$10.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_energy_max_output", (Object)this.instance, (List)list);
    }

    public boolean tm$11_consume_energy(double d) {
        if (h$11 != null) {
            return (Boolean)h$11.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"consume_energy", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$12_consume_energy(List list) {
        if (m$12 != null) {
            return m$12.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"consume_energy", (Object)this.instance, (List)list);
    }

    public boolean tm$13_player_facing(String string) {
        if (h$13 != null) {
            return (Boolean)h$13.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"player_facing", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$14_player_facing(List list) {
        if (m$14 != null) {
            return m$14.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"player_facing", (Object)this.instance, (List)list);
    }

    public boolean tm$15_set_rpm_output_new_network(String string) {
        if (h$15 != null) {
            return (Boolean)h$15.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_rpm_output_new_network", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$16_set_rpm_output_new_network(List list) {
        if (m$16 != null) {
            return m$16.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_rpm_output_new_network", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$17_container_at(double d, double d2, double d3) {
        if (h$17 != null) {
            return (ScriptValue)h$17.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"container_at", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$18_container_at(List list) {
        if (m$18 != null) {
            return m$18.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"container_at", (Object)this.instance, (List)list);
    }

    public ScriptValue um$19_drop_item_toward(List list) {
        if (m$19 != null) {
            return m$19.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"drop_item_toward", (Object)this.instance, (List)list);
    }

    public boolean tm$20_player_in_range(double d) {
        if (h$20 != null) {
            return (Boolean)h$20.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"player_in_range", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$21_player_in_range(List list) {
        if (m$21 != null) {
            return m$21.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"player_in_range", (Object)this.instance, (List)list);
    }

    public boolean tm$22_play_animation(ScriptValue scriptValue) {
        if (h$22 != null) {
            return (Boolean)h$22.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"play_animation", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$23_play_animation(List list) {
        if (m$23 != null) {
            return m$23.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"play_animation", (Object)this.instance, (List)list);
    }

    public boolean tm$24_open_menu(ScriptValue scriptValue) {
        if (h$24 != null) {
            return (Boolean)h$24.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"open_menu", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$25_open_menu(List list) {
        if (m$25 != null) {
            return m$25.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"open_menu", (Object)this.instance, (List)list);
    }

    public boolean tm$26_recompute_io() {
        if (h$26 != null) {
            return (Boolean)h$26.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"recompute_io", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$27_recompute_io(List list) {
        if (m$27 != null) {
            return m$27.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"recompute_io", (Object)this.instance, (List)list);
    }

    public boolean tm$28_relay_to(ScriptValue scriptValue, double d) {
        if (h$28 != null) {
            return (Boolean)h$28.call(this.instance, (Object)scriptValue, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"relay_to", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$29_relay_to(List list) {
        if (m$29 != null) {
            return m$29.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"relay_to", (Object)this.instance, (List)list);
    }

    public boolean tm$30_is_player_looking_at(double d) {
        if (h$30 != null) {
            return (Boolean)h$30.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"is_player_looking_at", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$31_is_player_looking_at(List list) {
        if (m$31 != null) {
            return m$31.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"is_player_looking_at", (Object)this.instance, (List)list);
    }

    public boolean tm$32_release_contraption() {
        if (h$32 != null) {
            return (Boolean)h$32.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"release_contraption", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$33_release_contraption(List list) {
        if (m$33 != null) {
            return m$33.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"release_contraption", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$34_get_typed(String string, String string2) {
        if (h$34 != null) {
            return (ScriptValue)h$34.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"get_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)});
    }

    public ScriptValue um$35_get_typed(List list) {
        if (m$35 != null) {
            return m$35.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"get_typed", (Object)this.instance, (List)list);
    }

    public boolean tm$36_push_rpm(ScriptValue scriptValue, double d) {
        if (h$36 != null) {
            return (Boolean)h$36.call(this.instance, (Object)scriptValue, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"push_rpm", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$37_push_rpm(List list) {
        if (m$37 != null) {
            return m$37.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"push_rpm", (Object)this.instance, (List)list);
    }

    public double tm$38_rpm_out(String string) {
        if (h$38 != null) {
            return (Double)h$38.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"rpm_out", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$39_rpm_out(List list) {
        if (m$39 != null) {
            return m$39.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"rpm_out", (Object)this.instance, (List)list);
    }

    public boolean tm$40_set_rpm_input(String string) {
        if (h$40 != null) {
            return (Boolean)h$40.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_rpm_input", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$41_set_rpm_input(List list) {
        if (m$41 != null) {
            return m$41.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_rpm_input", (Object)this.instance, (List)list);
    }

    public boolean tm$42_use_item_on_entity(ScriptValue scriptValue, double d) {
        if (h$42 != null) {
            return (Boolean)h$42.call(this.instance, (Object)scriptValue, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"use_item_on_entity", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$43_use_item_on_entity(List list) {
        if (m$43 != null) {
            return m$43.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"use_item_on_entity", (Object)this.instance, (List)list);
    }

    public boolean tm$44_set_rpm_output_same(String string) {
        if (h$44 != null) {
            return (Boolean)h$44.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_rpm_output_same", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$45_set_rpm_output_same(List list) {
        if (m$45 != null) {
            return m$45.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_rpm_output_same", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$46_rpm_out_faces() {
        if (h$46 != null) {
            return (ScriptValue)h$46.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"rpm_out_faces", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$47_rpm_out_faces(List list) {
        if (m$47 != null) {
            return m$47.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"rpm_out_faces", (Object)this.instance, (List)list);
    }

    public boolean tm$48_set_energy_per_tick(double d) {
        if (h$48 != null) {
            return (Boolean)h$48.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_energy_per_tick", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$49_set_energy_per_tick(List list) {
        if (m$49 != null) {
            return m$49.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_energy_per_tick", (Object)this.instance, (List)list);
    }

    public boolean tm$50_drop_item_at(ScriptValue scriptValue, double d, double d2, double d3) {
        if (h$50 != null) {
            return (Boolean)h$50.call(this.instance, (Object)scriptValue, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"drop_item_at", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)}).asBool();
    }

    public ScriptValue um$51_drop_item_at(List list) {
        if (m$51 != null) {
            return m$51.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"drop_item_at", (Object)this.instance, (List)list);
    }

    public boolean tm$52_page(double d) {
        if (h$52 != null) {
            return (Boolean)h$52.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"page", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$53_page(List list) {
        if (m$53 != null) {
            return m$53.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"page", (Object)this.instance, (List)list);
    }

    public boolean tm$54_io_set(String string, String string2, String string3, boolean bl) {
        if (h$54 != null) {
            return (Boolean)h$54.call(this.instance, (Object)string, (Object)string2, (Object)string3, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"io_set", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2), ScriptValue.of((String)string3), ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$55_io_set(List list) {
        if (m$55 != null) {
            return m$55.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"io_set", (Object)this.instance, (List)list);
    }

    public boolean tm$56_report_su(double d) {
        if (h$56 != null) {
            return (Boolean)h$56.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"report_su", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$57_report_su(List list) {
        if (m$57 != null) {
            return m$57.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"report_su", (Object)this.instance, (List)list);
    }

    public double tm$58_get_gas_level(String string) {
        if (h$58 != null) {
            return (Double)h$58.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"get_gas_level", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$59_get_gas_level(List list) {
        if (m$59 != null) {
            return m$59.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"get_gas_level", (Object)this.instance, (List)list);
    }

    public boolean tm$60_pipe_register_network() {
        if (h$60 != null) {
            return (Boolean)h$60.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"pipe_register_network", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$61_pipe_register_network(List list) {
        if (m$61 != null) {
            return m$61.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"pipe_register_network", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$62_belt_at(double d, double d2, double d3) {
        if (h$62 != null) {
            return (ScriptValue)h$62.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"belt_at", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$63_belt_at(List list) {
        if (m$63 != null) {
            return m$63.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"belt_at", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$64_neighbor_block(String string) {
        if (h$64 != null) {
            return (ScriptValue)h$64.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"neighbor_block", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$65_neighbor_block(List list) {
        if (m$65 != null) {
            return m$65.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"neighbor_block", (Object)this.instance, (List)list);
    }

    public boolean tm$66_has_typed(String string, String string2) {
        if (h$66 != null) {
            return (Boolean)h$66.call(this.instance, (Object)string, (Object)string2);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"has_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2)}).asBool();
    }

    public ScriptValue um$67_has_typed(List list) {
        if (m$67 != null) {
            return m$67.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"has_typed", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$68_block_at(double d, double d2, double d3) {
        if (h$68 != null) {
            return (ScriptValue)h$68.call(this.instance, (Object)d, (Object)d2, (Object)d3);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"block_at", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3)});
    }

    public ScriptValue um$69_block_at(List list) {
        if (m$69 != null) {
            return m$69.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"block_at", (Object)this.instance, (List)list);
    }

    public boolean tm$70_bump_overclock(double d) {
        if (h$70 != null) {
            return (Boolean)h$70.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"bump_overclock", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$71_bump_overclock(List list) {
        if (m$71 != null) {
            return m$71.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"bump_overclock", (Object)this.instance, (List)list);
    }

    public boolean tm$72_io_get(String string, String string2, String string3) {
        if (h$72 != null) {
            return (Boolean)h$72.call(this.instance, (Object)string, (Object)string2, (Object)string3);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"io_get", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2), ScriptValue.of((String)string3)}).asBool();
    }

    public ScriptValue um$73_io_get(List list) {
        if (m$73 != null) {
            return m$73.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"io_get", (Object)this.instance, (List)list);
    }

    public boolean tm$74_update() {
        if (h$74 != null) {
            return (Boolean)h$74.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"update", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$75_update(List list) {
        if (m$75 != null) {
            return m$75.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"update", (Object)this.instance, (List)list);
    }

    public boolean tm$76_prev_page() {
        if (h$76 != null) {
            return (Boolean)h$76.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"prev_page", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$77_prev_page(List list) {
        if (m$77 != null) {
            return m$77.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"prev_page", (Object)this.instance, (List)list);
    }

    public boolean tm$78_add_items(ScriptValue scriptValue) {
        if (h$78 != null) {
            return (Boolean)h$78.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"add_items", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$79_add_items(List list) {
        if (m$79 != null) {
            return m$79.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"add_items", (Object)this.instance, (List)list);
    }

    public boolean tm$80_next_page() {
        if (h$80 != null) {
            return (Boolean)h$80.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"next_page", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$81_next_page(List list) {
        if (m$81 != null) {
            return m$81.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"next_page", (Object)this.instance, (List)list);
    }

    public boolean tm$82_set_typed(String string, String string2, ScriptValue scriptValue) {
        if (h$82 != null) {
            return (Boolean)h$82.call(this.instance, (Object)string, (Object)string2, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_typed", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((String)string2), scriptValue}).asBool();
    }

    public ScriptValue um$83_set_typed(List list) {
        if (m$83 != null) {
            return m$83.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_typed", (Object)this.instance, (List)list);
    }

    public boolean tm$84_set_overclock(double d) {
        if (h$84 != null) {
            return (Boolean)h$84.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_overclock", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$85_set_overclock(List list) {
        if (m$85 != null) {
            return m$85.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_overclock", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$86_blocks_in_range(double d) {
        if (h$86 != null && c$86_r != null) {
            return c$86_r.encode(h$86.call(this.instance, (Object)d));
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"blocks_in_range", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$87_blocks_in_range(List list) {
        if (m$87 != null) {
            return m$87.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"blocks_in_range", (Object)this.instance, (List)list);
    }

    public boolean tm$88_consume_gas(String string, double d) {
        if (h$88 != null) {
            return (Boolean)h$88.call(this.instance, (Object)string, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"consume_gas", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$89_consume_gas(List list) {
        if (m$89 != null) {
            return m$89.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"consume_gas", (Object)this.instance, (List)list);
    }

    public boolean tm$90_clear_rpm_override() {
        if (h$90 != null) {
            return (Boolean)h$90.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"clear_rpm_override", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$91_clear_rpm_override(List list) {
        if (m$91 != null) {
            return m$91.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"clear_rpm_override", (Object)this.instance, (List)list);
    }

    public boolean tm$92_close() {
        if (h$92 != null) {
            return (Boolean)h$92.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"close", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$93_close(List list) {
        if (m$93 != null) {
            return m$93.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"close", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$94_nearby_entities(double d) {
        if (h$94 != null) {
            return (ScriptValue)h$94.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"nearby_entities", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$95_nearby_entities(List list) {
        if (m$95 != null) {
            return m$95.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"nearby_entities", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$96_get_renderer(String string) {
        if (h$96 != null) {
            return (ScriptValue)h$96.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"get_renderer", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$97_get_renderer(List list) {
        if (m$97 != null) {
            return m$97.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"get_renderer", (Object)this.instance, (List)list);
    }

    public boolean tm$98_fill_fluid(String string, double d) {
        if (h$98 != null) {
            return (Boolean)h$98.call(this.instance, (Object)string, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"fill_fluid", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$99_fill_fluid(List list) {
        if (m$99 != null) {
            return m$99.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"fill_fluid", (Object)this.instance, (List)list);
    }

    public boolean tm$100_set_rpm_output_same_inverted(String string) {
        if (h$100 != null) {
            return (Boolean)h$100.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_rpm_output_same_inverted", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$101_set_rpm_output_same_inverted(List list) {
        if (m$101 != null) {
            return m$101.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_rpm_output_same_inverted", (Object)this.instance, (List)list);
    }

    public boolean tm$102_hold_contraption() {
        if (h$102 != null) {
            return (Boolean)h$102.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"hold_contraption", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$103_hold_contraption(List list) {
        if (m$103 != null) {
            return m$103.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"hold_contraption", (Object)this.instance, (List)list);
    }

    public boolean tm$104_update_display(String string, ScriptValue scriptValue) {
        if (h$104 != null) {
            return (Boolean)h$104.call(this.instance, (Object)string, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"update_display", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), scriptValue}).asBool();
    }

    public ScriptValue um$105_update_display(List list) {
        if (m$105 != null) {
            return m$105.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"update_display", (Object)this.instance, (List)list);
    }

    public boolean tm$106_set_rpm_output(double d) {
        if (h$106 != null) {
            return (Boolean)h$106.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_rpm_output", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$107_set_rpm_output(List list) {
        if (m$107 != null) {
            return m$107.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_rpm_output", (Object)this.instance, (List)list);
    }

    public boolean tm$108_emit_redstone(double d) {
        if (h$108 != null) {
            return (Boolean)h$108.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"emit_redstone", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$109_emit_redstone(List list) {
        if (m$109 != null) {
            return m$109.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"emit_redstone", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$110_connect_contraption() {
        if (h$110 != null) {
            return (ScriptValue)h$110.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"connect_contraption", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$111_connect_contraption(List list) {
        if (m$111 != null) {
            return m$111.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"connect_contraption", (Object)this.instance, (List)list);
    }

    public boolean tm$112_set_energy_max_input(double d) {
        if (h$112 != null) {
            return (Boolean)h$112.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"set_energy_max_input", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$113_set_energy_max_input(List list) {
        if (m$113 != null) {
            return m$113.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"set_energy_max_input", (Object)this.instance, (List)list);
    }

    public boolean tm$114_kill_display(String string) {
        if (h$114 != null) {
            return (Boolean)h$114.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"kill_display", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$115_kill_display(List list) {
        if (m$115 != null) {
            return m$115.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"kill_display", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$116_to_item(String string, boolean bl, boolean bl2) {
        if (h$116 != null) {
            return (ScriptValue)h$116.call(this.instance, (Object)string, (Object)bl, (Object)bl2);
        }
        return PolyClassRuntime.genericCall((String)"Machine", (String)"to_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), ScriptValue.of((boolean)bl), ScriptValue.of((boolean)bl2)});
    }

    public ScriptValue um$117_to_item(List list) {
        if (m$117 != null) {
            return m$117.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Machine", (String)"to_item", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$118_is_multi_cell() {
        if (p$118 != null) {
            return p$118.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"is_multi_cell", (Object)this.instance);
    }

    public boolean tg$119_is_multi_cell() {
        if (tp$119 != null) {
            return (Boolean)tp$119.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"is_multi_cell", (Object)this.instance).asBool();
    }

    public ScriptValue pg$120_container() {
        if (p$120 != null) {
            return p$120.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"container", (Object)this.instance);
    }

    public ScriptValue pg$121_has_contraption() {
        if (p$121 != null) {
            return p$121.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"has_contraption", (Object)this.instance);
    }

    public boolean tg$122_has_contraption() {
        if (tp$122 != null) {
            return (Boolean)tp$122.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"has_contraption", (Object)this.instance).asBool();
    }

    public ScriptValue pg$123_energy_max_output() {
        if (p$123 != null) {
            return p$123.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_max_output", (Object)this.instance);
    }

    public double tg$124_energy_max_output() {
        if (tp$124 != null) {
            return (Double)tp$124.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_max_output", (Object)this.instance).asNum();
    }

    public ScriptValue pg$125_energy_stored() {
        if (p$125 != null) {
            return p$125.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_stored", (Object)this.instance);
    }

    public double tg$126_energy_stored() {
        if (tp$126 != null) {
            return (Double)tp$126.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_stored", (Object)this.instance).asNum();
    }

    public ScriptValue pg$127_axis() {
        if (p$127 != null) {
            return p$127.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"axis", (Object)this.instance);
    }

    public String tg$128_axis() {
        if (tp$128 != null) {
            return (String)tp$128.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"axis", (Object)this.instance).asStr();
    }

    public ScriptValue pg$129_facing_dy() {
        if (p$129 != null) {
            return p$129.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"facing_dy", (Object)this.instance);
    }

    public double tg$130_facing_dy() {
        if (tp$130 != null) {
            return (Double)tp$130.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"facing_dy", (Object)this.instance).asNum();
    }

    public ScriptValue pg$131_facing_dz() {
        if (p$131 != null) {
            return p$131.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"facing_dz", (Object)this.instance);
    }

    public double tg$132_facing_dz() {
        if (tp$132 != null) {
            return (Double)tp$132.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"facing_dz", (Object)this.instance).asNum();
    }

    public ScriptValue pg$133_facing_dx() {
        if (p$133 != null) {
            return p$133.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"facing_dx", (Object)this.instance);
    }

    public double tg$134_facing_dx() {
        if (tp$134 != null) {
            return (Double)tp$134.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"facing_dx", (Object)this.instance).asNum();
    }

    public ScriptValue pg$135_last_transfer_tick() {
        if (p$135 != null) {
            return p$135.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"last_transfer_tick", (Object)this.instance);
    }

    public double tg$136_last_transfer_tick() {
        if (tp$136 != null) {
            return (Double)tp$136.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"last_transfer_tick", (Object)this.instance).asNum();
    }

    public ScriptValue pg$137_facing_block() {
        if (p$137 != null) {
            return p$137.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"facing_block", (Object)this.instance);
    }

    public ScriptValue pg$138_gas_tanks() {
        if (p$138 != null) {
            return p$138.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"gas_tanks", (Object)this.instance);
    }

    public ScriptValue pg$139_block() {
        if (p$139 != null) {
            return p$139.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$140_current_page() {
        if (p$140 != null) {
            return p$140.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"current_page", (Object)this.instance);
    }

    public double tg$141_current_page() {
        if (tp$141 != null) {
            return (Double)tp$141.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"current_page", (Object)this.instance).asNum();
    }

    public ScriptValue pg$142_page_count() {
        if (p$142 != null) {
            return p$142.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"page_count", (Object)this.instance);
    }

    public double tg$143_page_count() {
        if (tp$143 != null) {
            return (Double)tp$143.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"page_count", (Object)this.instance).asNum();
    }

    public ScriptValue pg$144_generation() {
        if (p$144 != null) {
            return p$144.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"generation", (Object)this.instance);
    }

    public double tg$145_generation() {
        if (tp$145 != null) {
            return (Double)tp$145.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"generation", (Object)this.instance).asNum();
    }

    public ScriptValue pg$146_recipes() {
        if (p$146 != null) {
            return p$146.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"recipes", (Object)this.instance);
    }

    public ScriptValue pg$147_progress_percent() {
        if (p$147 != null) {
            return p$147.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"progress_percent", (Object)this.instance);
    }

    public double tg$148_progress_percent() {
        if (tp$148 != null) {
            return (Double)tp$148.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"progress_percent", (Object)this.instance).asNum();
    }

    public ScriptValue pg$149_efficiency() {
        if (p$149 != null) {
            return p$149.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"efficiency", (Object)this.instance);
    }

    public double tg$150_efficiency() {
        if (tp$150 != null) {
            return (Double)tp$150.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"efficiency", (Object)this.instance).asNum();
    }

    public ScriptValue pg$151_ticks_alive() {
        if (p$151 != null) {
            return p$151.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"ticks_alive", (Object)this.instance);
    }

    public double tg$152_ticks_alive() {
        if (tp$152 != null) {
            return (Double)tp$152.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"ticks_alive", (Object)this.instance).asNum();
    }

    public ScriptValue pg$153_source_distance() {
        if (p$153 != null) {
            return p$153.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"source_distance", (Object)this.instance);
    }

    public double tg$154_source_distance() {
        if (tp$154 != null) {
            return (Double)tp$154.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"source_distance", (Object)this.instance).asNum();
    }

    public ScriptValue pg$155_overclock_limit() {
        if (p$155 != null) {
            return p$155.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"overclock_limit", (Object)this.instance);
    }

    public double tg$156_overclock_limit() {
        if (tp$156 != null) {
            return (Double)tp$156.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"overclock_limit", (Object)this.instance).asNum();
    }

    public ScriptValue pg$157_belt() {
        if (p$157 != null) {
            return p$157.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"belt", (Object)this.instance);
    }

    public ScriptValue pg$158_io() {
        if (p$158 != null) {
            return p$158.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"io", (Object)this.instance);
    }

    public ScriptValue pg$159_energy_real_output() {
        if (p$159 != null) {
            return p$159.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_real_output", (Object)this.instance);
    }

    public double tg$160_energy_real_output() {
        if (tp$160 != null) {
            return (Double)tp$160.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_real_output", (Object)this.instance).asNum();
    }

    public ScriptValue pg$161_working_recipe() {
        if (p$161 != null) {
            return p$161.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"working_recipe", (Object)this.instance);
    }

    public ScriptValue pg$162_energy_capacity() {
        if (p$162 != null) {
            return p$162.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_capacity", (Object)this.instance);
    }

    public double tg$163_energy_capacity() {
        if (tp$163 != null) {
            return (Double)tp$163.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_capacity", (Object)this.instance).asNum();
    }

    public ScriptValue pg$164_cell_count() {
        if (p$164 != null) {
            return p$164.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"cell_count", (Object)this.instance);
    }

    public double tg$165_cell_count() {
        if (tp$165 != null) {
            return (Double)tp$165.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"cell_count", (Object)this.instance).asNum();
    }

    public ScriptValue pg$166_network_capacity() {
        if (p$166 != null) {
            return p$166.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"network_capacity", (Object)this.instance);
    }

    public double tg$167_network_capacity() {
        if (tp$167 != null) {
            return (Double)tp$167.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"network_capacity", (Object)this.instance).asNum();
    }

    public ScriptValue pg$168_meter_state() {
        if (p$168 != null) {
            return p$168.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"meter_state", (Object)this.instance);
    }

    public double tg$169_meter_state() {
        if (tp$169 != null) {
            return (Double)tp$169.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"meter_state", (Object)this.instance).asNum();
    }

    public ScriptValue pg$170_redstone() {
        if (p$170 != null) {
            return p$170.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"redstone", (Object)this.instance);
    }

    public ScriptValue pg$171_overclock() {
        if (p$171 != null) {
            return p$171.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"overclock", (Object)this.instance);
    }

    public double tg$172_overclock() {
        if (tp$172 != null) {
            return (Double)tp$172.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"overclock", (Object)this.instance).asNum();
    }

    public ScriptValue pg$173_energy_max_input() {
        if (p$173 != null) {
            return p$173.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_max_input", (Object)this.instance);
    }

    public double tg$174_energy_max_input() {
        if (tp$174 != null) {
            return (Double)tp$174.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_max_input", (Object)this.instance).asNum();
    }

    public ScriptValue pg$175_energy_per_tick() {
        if (p$175 != null) {
            return p$175.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_per_tick", (Object)this.instance);
    }

    public double tg$176_energy_per_tick() {
        if (tp$176 != null) {
            return (Double)tp$176.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_per_tick", (Object)this.instance).asNum();
    }

    public ScriptValue pg$177_fluid_tanks() {
        if (p$177 != null) {
            return p$177.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"fluid_tanks", (Object)this.instance);
    }

    public ScriptValue pg$178_energy_real_input() {
        if (p$178 != null) {
            return p$178.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_real_input", (Object)this.instance);
    }

    public double tg$179_energy_real_input() {
        if (tp$179 != null) {
            return (Double)tp$179.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"energy_real_input", (Object)this.instance).asNum();
    }

    public ScriptValue pg$180_rename_text() {
        if (p$180 != null) {
            return p$180.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"rename_text", (Object)this.instance);
    }

    public String tg$181_rename_text() {
        if (tp$181 != null) {
            return (String)tp$181.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"rename_text", (Object)this.instance).asStr();
    }

    public ScriptValue pg$182_rpm_network() {
        if (p$182 != null) {
            return p$182.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"rpm_network", (Object)this.instance);
    }

    public double tg$183_rpm_network() {
        if (tp$183 != null) {
            return (Double)tp$183.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"rpm_network", (Object)this.instance).asNum();
    }

    public ScriptValue pg$184_pos() {
        if (p$184 != null) {
            return p$184.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"pos", (Object)this.instance);
    }

    public ScriptValue pg$185_matching_recipe() {
        if (p$185 != null) {
            return p$185.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"matching_recipe", (Object)this.instance);
    }

    public ScriptValue pg$186_max_progress() {
        if (p$186 != null) {
            return p$186.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"max_progress", (Object)this.instance);
    }

    public double tg$187_max_progress() {
        if (tp$187 != null) {
            return (Double)tp$187.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"max_progress", (Object)this.instance).asNum();
    }

    public ScriptValue pg$188_network_stress() {
        if (p$188 != null) {
            return p$188.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"network_stress", (Object)this.instance);
    }

    public double tg$189_network_stress() {
        if (tp$189 != null) {
            return (Double)tp$189.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"network_stress", (Object)this.instance).asNum();
    }

    public ScriptValue pg$190_contraption() {
        if (p$190 != null) {
            return p$190.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"contraption", (Object)this.instance);
    }

    public ScriptValue pg$191_bars() {
        if (p$191 != null) {
            return p$191.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"bars", (Object)this.instance);
    }

    public ScriptValue pg$192_layout() {
        if (p$192 != null) {
            return p$192.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"layout", (Object)this.instance);
    }

    public ScriptValue pg$193_max_burn_time() {
        if (p$193 != null) {
            return p$193.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"max_burn_time", (Object)this.instance);
    }

    public double tg$194_max_burn_time() {
        if (tp$194 != null) {
            return (Double)tp$194.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"max_burn_time", (Object)this.instance).asNum();
    }

    public ScriptValue pg$195_burn_time() {
        if (p$195 != null) {
            return p$195.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"burn_time", (Object)this.instance);
    }

    public double tg$196_burn_time() {
        if (tp$196 != null) {
            return (Double)tp$196.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"burn_time", (Object)this.instance).asNum();
    }

    public ScriptValue pg$197_is_overstressed() {
        if (p$197 != null) {
            return p$197.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"is_overstressed", (Object)this.instance);
    }

    public boolean tg$198_is_overstressed() {
        if (tp$198 != null) {
            return (Boolean)tp$198.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"is_overstressed", (Object)this.instance).asBool();
    }

    public ScriptValue pg$199_x() {
        if (p$199 != null) {
            return p$199.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"x", (Object)this.instance);
    }

    public double tg$200_x() {
        if (tp$200 != null) {
            return (Double)tp$200.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$201_y() {
        if (p$201 != null) {
            return p$201.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"y", (Object)this.instance);
    }

    public double tg$202_y() {
        if (tp$202 != null) {
            return (Double)tp$202.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"y", (Object)this.instance).asNum();
    }

    public ScriptValue pg$203_progress() {
        if (p$203 != null) {
            return p$203.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"progress", (Object)this.instance);
    }

    public double tg$204_progress() {
        if (tp$204 != null) {
            return (Double)tp$204.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"progress", (Object)this.instance).asNum();
    }

    public ScriptValue pg$205_z() {
        if (p$205 != null) {
            return p$205.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"z", (Object)this.instance);
    }

    public double tg$206_z() {
        if (tp$206 != null) {
            return (Double)tp$206.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"z", (Object)this.instance).asNum();
    }

    public ScriptValue pg$207_activated() {
        if (p$207 != null) {
            return p$207.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"activated", (Object)this.instance);
    }

    public boolean tg$208_activated() {
        if (tp$208 != null) {
            return (Boolean)tp$208.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"activated", (Object)this.instance).asBool();
    }

    public ScriptValue pg$209_owner_uuid() {
        if (p$209 != null) {
            return p$209.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"owner_uuid", (Object)this.instance);
    }

    public String tg$210_owner_uuid() {
        if (tp$210 != null) {
            return (String)tp$210.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Machine", (String)"owner_uuid", (Object)this.instance).asStr();
    }

    public PolyClassMachine(Object object) {
        super(object);
    }

    public static PolyClassMachine of(Object object) {
        return new PolyClassMachine(object);
    }

    public static PolyClassMachine ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
            return new PolyClassMachine(object);
        }
        return null;
    }
}
