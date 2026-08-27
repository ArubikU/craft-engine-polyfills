/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.kinetics;

import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class TreeUtils {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _isLog(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((scriptContext.getStr("id").equals("minecraft:oak_log") || scriptContext.getStr("id").equals("minecraft:spruce_log") || scriptContext.getStr("id").equals("minecraft:birch_log") || scriptContext.getStr("id").equals("minecraft:jungle_log") || scriptContext.getStr("id").equals("minecraft:acacia_log") || scriptContext.getStr("id").equals("minecraft:dark_oak_log") || scriptContext.getStr("id").equals("minecraft:cherry_log") || scriptContext.getStr("id").equals("minecraft:mangrove_log") || scriptContext.getStr("id").equals("minecraft:crimson_stem") || scriptContext.getStr("id").equals("minecraft:warped_stem") || scriptContext.getStr("id").equals("minecraft:stripped_oak_log") || scriptContext.getStr("id").equals("minecraft:stripped_spruce_log") || scriptContext.getStr("id").equals("minecraft:stripped_birch_log") || scriptContext.getStr("id").equals("minecraft:stripped_jungle_log") || scriptContext.getStr("id").equals("minecraft:stripped_acacia_log") || scriptContext.getStr("id").equals("minecraft:stripped_dark_oak_log") || scriptContext.getStr("id").equals("minecraft:mushroom_stem") ? 1 : 0) != 0);
    }

    public static ScriptValue _isLeafId(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((scriptContext.getStr("id").equals("minecraft:oak_leaves") || scriptContext.getStr("id").equals("minecraft:spruce_leaves") || scriptContext.getStr("id").equals("minecraft:birch_leaves") || scriptContext.getStr("id").equals("minecraft:jungle_leaves") || scriptContext.getStr("id").equals("minecraft:acacia_leaves") || scriptContext.getStr("id").equals("minecraft:dark_oak_leaves") || scriptContext.getStr("id").equals("minecraft:cherry_leaves") || scriptContext.getStr("id").equals("minecraft:azalea_leaves") || scriptContext.getStr("id").equals("minecraft:flowering_azalea_leaves") || scriptContext.getStr("id").equals("minecraft:mangrove_leaves") ? 1 : 0) != 0);
    }

    /*
     * Enabled aggressive block sorting
     */
    public static ScriptValue _isChoppable(ScriptContext.Builder builder) {
        boolean bl;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("id", scriptContext.getClassOrVar("id"));
        if (!TreeUtils._isLog(builder2).asBool()) {
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("id", scriptContext.getClassOrVar("id"));
            if (!TreeUtils._isLeafId(builder3).asBool()) {
                bl = false;
                return ScriptValue.of((boolean)bl);
            }
        }
        bl = true;
        return ScriptValue.of((boolean)bl);
    }

    public static ScriptValue _getTreeBlock(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("contraption"), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ScriptValue scriptValue = scriptContext.getClassOrVar("contraption");
            return PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)scriptContext.getClassOrVar("x"), (ScriptValue)scriptContext.getClassOrVar("y"), (ScriptValue)scriptContext.getClassOrVar("z"), (ScriptContext)scriptContext);
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("World");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("x"));
            arrayList.add(scriptContext.getClassOrVar("y"));
            arrayList.add(scriptContext.getClassOrVar("z"));
            PolyClassWorld polyClassWorld = PolyClassWorld.ofGuarded((ScriptValue)scriptValue);
            object = polyClassWorld != null ? polyClassWorld.um$1_get_block(arrayList) : PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    /*
     * Unable to fully structure code
     */
    public static ScriptValue _findTree(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = 300.0;
        var4_3 = ScriptValue.of((double)300.0);
        var0.val("MAX_LOGS", var4_3);
        var5_4 = 400.0;
        var7_5 = ScriptValue.of((double)400.0);
        var0.val("MAX_LEAVES", var7_5);
        var8_6 = 80.0;
        var10_7 = ScriptValue.of((double)80.0);
        var0.val("MAX_ROUNDS", var10_7);
        var11_8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "0,0,0");
        var0.val("found", var11_8);
        var12_9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "0,0,0");
        var0.val("frontier", var12_9);
        var13_10 = 1.0;
        var15_11 = ScriptValue.of((double)1.0);
        var0.val("log_count", var15_11);
        var16_12 = 0.0;
        var18_13 = ScriptValue.of((double)0.0);
        var0.val("round_i", var18_13);
        var19_14 = 0;
        var20_15 = var1_1.getClassOrVar("blk");
        var21_16 = var12_9;
        var22_17 = var1_1.getClassOrVar("nx");
        var23_18 = var1_1.getClassOrVar("ny");
        var24_19 = var1_1.getClassOrVar("cell");
        var25_20 = var1_1.getClassOrVar("nz");
        var26_21 = ScriptValue.of((double)var16_12);
        var27_22 = var1_1.getClassOrVar("p");
        var28_23 = ScriptValue.of((double)var13_10);
        var29_24 = var11_8;
        var30_25 = var1_1.getClassOrVar("dx");
        var31_26 = var1_1.getClassOrVar("cx");
        var32_27 = var1_1.getClassOrVar("dy");
        var33_28 = var1_1.getClassOrVar("next_frontier");
        var34_29 = var1_1.getClassOrVar("cy");
        var35_30 = var1_1.getClassOrVar("dz");
        var36_31 = var1_1.getClassOrVar("cz");
        var37_32 = var1_1.getClassOrVar("is_self");
        var38_33 = var1_1.getClassOrVar("key");
        while (var19_14 < 1000) {
            block12: {
                ++var19_14;
                if (!(((ScriptFormula.valuesEqualStr((ScriptValue)var21_16, (String)"") ^ true) != false && var26_21.asNum() < var8_6 != false) != false && var28_23.asNum() < var2_2 != false)) break;
                var39_34 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "");
                var0.val("next_frontier", var39_34);
                var33_28 = var39_34;
                var40_35 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)var21_16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";")), (ScriptContext)var1_1));
                if (var40_35 == null) break block12;
                block1: for (ScriptValue var42_37 : var40_35) {
                    var0.val("cell", var42_37);
                    var43_38 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)var42_37, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ",")), (ScriptContext)var1_1);
                    var0.val("p", var43_38);
                    var27_22 = var43_38;
                    var44_39 = ScriptFormula.callBuiltin1((String)"num", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var27_22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))), (ScriptContext)var1_1);
                    var0.val("cx", var44_39);
                    var31_26 = var44_39;
                    var45_40 = ScriptFormula.callBuiltin1((String)"num", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var27_22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0))), (ScriptContext)var1_1);
                    var0.val("cy", var45_40);
                    var34_29 = var45_40;
                    var46_41 = ScriptFormula.callBuiltin1((String)"num", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var27_22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 2.0))), (ScriptContext)var1_1);
                    var0.val("cz", var46_41);
                    var36_31 = var46_41;
                    var47_42 = -1.0;
                    var49_43 = ScriptValue.of((double)var47_42);
                    var0.val("dx", var49_43);
                    var30_25 = var49_43;
                    var50_44 = 0;
                    while (var50_44 < 1000) {
                        ++var50_44;
                        if (!(var1_1.getNum("dx") <= 1.0)) continue block1;
                        var51_45 = 0.0;
                        var53_46 = ScriptValue.of((double)0.0);
                        var0.val("dy", var53_46);
                        var32_27 = var53_46;
                        var54_47 = 0;
                        while (var54_47 < 1000) {
                            ++var54_47;
                            if (!(var1_1.getNum("dy") <= 1.0)) break;
                            var55_48 = -1.0;
                            var57_49 = ScriptValue.of((double)var55_48);
                            var0.val("dz", var57_49);
                            var35_30 = var57_49;
                            var58_50 = 0;
                            while (var58_50 < 1000) {
                                block13: {
                                    ++var58_50;
                                    if (!(var1_1.getNum("dz") <= 1.0)) break;
                                    var59_51 = (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))) != false && ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dy"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))) != false) != false && ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dz"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))) != false;
                                    var60_52 = ScriptValue.of((boolean)var59_51);
                                    var0.val("is_self", var60_52);
                                    var37_32 = var60_52;
                                    if (!((var37_32.asBool() ^ true) != false && var1_1.getNum("log_count") < var2_2 != false)) break block13;
                                    var61_53 = ScriptFormula.addPolymorphic((ScriptValue)var31_26, (ScriptValue)var1_1.getClassOrVar("dx"));
                                    var0.val("nx", var61_53);
                                    var22_17 = var61_53;
                                    var62_54 = ScriptFormula.addPolymorphic((ScriptValue)var34_29, (ScriptValue)var1_1.getClassOrVar("dy"));
                                    var0.val("ny", var62_54);
                                    var23_18 = var62_54;
                                    var63_55 = ScriptFormula.addPolymorphic((ScriptValue)var36_31, (ScriptValue)var1_1.getClassOrVar("dz"));
                                    var0.val("nz", var63_55);
                                    var25_20 = var63_55;
                                    var64_56 = ScriptValue.of((String)(var22_17.asStr() + "," + var23_18.asStr() + "," + var25_20.asStr()));
                                    var0.val("key", var64_56);
                                    var38_33 = var64_56;
                                    if (!(ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)var1_1.getClassOrVar("found"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";")), (ScriptContext)var1_1), (ScriptValue)var38_33, (ScriptContext)var1_1).asBool() ^ true)) break block13;
                                    var65_57 = ScriptContext.builder().copyFrom(var1_1);
                                    var65_57.val("contraption", var1_1.getClassOrVar("contraption"));
                                    var65_57.val("x", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_x"), (ScriptValue)var22_17));
                                    var65_57.val("y", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_y"), (ScriptValue)var23_18));
                                    var65_57.val("z", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_z"), (ScriptValue)var25_20));
                                    var66_58 = TreeUtils._getTreeBlock(var65_57);
                                    var0.val("blk", var66_58);
                                    var20_15 = var66_58;
                                    if (!((ScriptFormula.valuesEqual((ScriptValue)var20_15, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && ((var20_15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var20_15, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) ** GOTO lbl-1000
                                    var67_59 = ScriptContext.builder().copyFrom(var1_1);
                                    var67_59.val("id", (ScriptValue)(var20_15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var20_15, (ScriptContext)var1_1) : ScriptValue.NULL));
                                    if (TreeUtils._isLog(var67_59).asBool()) {
                                        v0 = true;
                                    } else lbl-1000:
                                    // 2 sources

                                    {
                                        v0 = false;
                                    }
                                    if (v0) {
                                        var68_60 = ScriptValue.of((String)(var1_1.getStr("found") + ";" + var38_33.asStr()));
                                        var0.val("found", var68_60);
                                        var29_24 = var68_60;
                                        var69_61 = var1_1.getStr("next_frontier").equals("") != false ? var38_33 : ScriptValue.of((String)(var1_1.getStr("next_frontier") + ";" + var38_33.asStr()));
                                        var0.val("next_frontier", var69_61);
                                        var33_28 = var69_61;
                                        var70_62 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("log_count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
                                        var0.val("log_count", var70_62);
                                        var28_23 = var70_62;
                                    }
                                }
                                var71_63 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dz"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
                                var0.val("dz", var71_63);
                                var35_30 = var71_63;
                            }
                            var72_64 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dy"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
                            var0.val("dy", var72_64);
                            var32_27 = var72_64;
                        }
                        var73_65 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
                        var0.val("dx", var73_65);
                        var30_25 = var73_65;
                    }
                }
            }
            var74_66 = var1_1.getClassOrVar("next_frontier");
            var0.val("frontier", var74_66);
            var21_16 = var74_66;
            var75_67 = ScriptFormula.addPolymorphic((ScriptValue)var26_21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
            var0.val("round_i", var75_67);
            var26_21 = var75_67;
        }
        var76_68 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "");
        var0.val("leaves", var76_68);
        var77_69 = ScriptFormula.callBuiltin0((String)"make_map", (ScriptContext)var1_1);
        var0.val("leaf_dist", var77_69);
        var78_70 = var29_24;
        var0.val("leaf_frontier", var78_70);
        var79_71 = 0.0;
        var81_72 = ScriptValue.of((double)0.0);
        var0.val("leaf_count", var81_72);
        var82_73 = 0.0;
        var84_74 = ScriptValue.of((double)0.0);
        var0.val("lround_i", var84_74);
        var85_75 = 0;
        var86_76 = var20_15;
        var87_77 = ScriptValue.of((double)var79_71);
        var88_78 = var1_1.getClassOrVar("prev_dist");
        var89_79 = var1_1.getClassOrVar("next_leaf_frontier");
        var90_80 = var1_1.getClassOrVar("already");
        var91_81 = var1_1.getClassOrVar("dist_str");
        var92_82 = var1_1.getClassOrVar("dist");
        var93_83 = var22_17;
        var94_84 = var23_18;
        var95_85 = var24_19;
        var96_86 = var25_20;
        var97_87 = var77_69;
        var98_88 = var1_1.getClassOrVar("off");
        var99_89 = var27_22;
        var100_90 = var76_68;
        var101_91 = var31_26;
        var102_92 = var34_29;
        var103_93 = var36_31;
        var104_94 = var78_70;
        var105_95 = var38_33;
        var106_96 = ScriptValue.of((double)var82_73);
        while (var85_75 < 1000) {
            ++var85_75;
            if (!(((ScriptFormula.valuesEqualStr((ScriptValue)var104_94, (String)"") ^ true) != false && var106_96.asNum() < var8_6 != false) != false && var87_77.asNum() < var5_4 != false)) break;
            var107_97 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "");
            var0.val("next_leaf_frontier", var107_97);
            var89_79 = var107_97;
            var108_98 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)var104_94, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";")), (ScriptContext)var1_1));
            if (var108_98 != null) {
                for (ScriptValue var110_100 : var108_98) {
                    var0.val("cell", var110_100);
                    var111_101 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)var110_100, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ",")), (ScriptContext)var1_1);
                    var0.val("p", var111_101);
                    var99_89 = var111_101;
                    var112_102 = ScriptFormula.callBuiltin1((String)"num", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var99_89, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))), (ScriptContext)var1_1);
                    var0.val("cx", var112_102);
                    var101_91 = var112_102;
                    var113_103 = ScriptFormula.callBuiltin1((String)"num", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var99_89, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0))), (ScriptContext)var1_1);
                    var0.val("cy", var113_103);
                    var102_92 = var113_103;
                    var114_104 = ScriptFormula.callBuiltin1((String)"num", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var99_89, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 2.0))), (ScriptContext)var1_1);
                    var0.val("cz", var114_104);
                    var103_93 = var114_104;
                    var115_105 = var1_1.getClassOrVar("leaf_dist");
                    var116_106 = ScriptFormula.callBuiltin1((String)"num", (ScriptValue)(var115_105 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var115_105, (ScriptValue)var110_100, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "0")), (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                    var0.val("prev_dist", var116_106);
                    var88_78 = var116_106;
                    var117_107 = ScriptProgram.elementsOf((ScriptValue)var1_1.getClassOrVar("OFFSETS6"));
                    if (var117_107 == null) continue;
                    for (ScriptValue var119_109 : var117_107) {
                        var0.val("off", var119_109);
                        if (!(var1_1.getNum("leaf_count") < var5_4)) continue;
                        var120_110 = ScriptFormula.addPolymorphic((ScriptValue)var101_91, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var119_109, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))));
                        var0.val("nx", var120_110);
                        var93_83 = var120_110;
                        var121_111 = ScriptFormula.addPolymorphic((ScriptValue)var102_92, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var119_109, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0))));
                        var0.val("ny", var121_111);
                        var94_84 = var121_111;
                        var122_112 = ScriptFormula.addPolymorphic((ScriptValue)var103_93, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var119_109, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 2.0))));
                        var0.val("nz", var122_112);
                        var96_86 = var122_112;
                        var123_113 = ScriptValue.of((String)(var93_83.asStr() + "," + var94_84.asStr() + "," + var96_86.asStr()));
                        var0.val("key", var123_113);
                        var105_95 = var123_113;
                        var124_114 = ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)var29_24, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";")), (ScriptContext)var1_1), (ScriptValue)var105_95, (ScriptContext)var1_1).asBool() != false || ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)var1_1.getClassOrVar("leaves"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";")), (ScriptContext)var1_1), (ScriptValue)var105_95, (ScriptContext)var1_1).asBool() != false;
                        var125_115 = ScriptValue.of((boolean)var124_114);
                        var0.val("already", var125_115);
                        var90_80 = var125_115;
                        if (!(var90_80.asBool() ^ true)) continue;
                        var126_116 = ScriptContext.builder().copyFrom(var1_1);
                        var126_116.val("contraption", var1_1.getClassOrVar("contraption"));
                        var126_116.val("x", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_x"), (ScriptValue)var93_83));
                        var126_116.val("y", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_y"), (ScriptValue)var94_84));
                        var126_116.val("z", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_z"), (ScriptValue)var96_86));
                        var127_117 = TreeUtils._getTreeBlock(var126_116);
                        var0.val("blk", var127_117);
                        var86_76 = var127_117;
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var86_76, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && ((var86_76 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var86_76, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) continue;
                        var128_118 = var86_76 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var86_76, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "distance")), (ScriptContext)var1_1) : ScriptValue.NULL;
                        var0.val("dist_str", var128_118);
                        var91_81 = var128_118;
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var91_81, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (ScriptFormula.valuesEqualStr((ScriptValue)var91_81, (String)"") ^ true) != false)) continue;
                        var129_119 = ScriptFormula.callBuiltin1((String)"num", (ScriptValue)var91_81, (ScriptContext)var1_1);
                        var0.val("dist", var129_119);
                        var92_82 = var129_119;
                        if (!(var92_82.asNum() > var88_78.asNum())) continue;
                        var130_120 = var1_1.getStr("leaves").equals("") != false ? var105_95 : ScriptValue.of((String)(var1_1.getStr("leaves") + ";" + var105_95.asStr()));
                        var0.val("leaves", var130_120);
                        var100_90 = var130_120;
                        var131_121 = var1_1.getClassOrVar("leaf_dist");
                        var132_122 = var131_121 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "with", (ScriptValue)var131_121, (ScriptValue)var105_95, (ScriptValue)ScriptFormula.callBuiltin1((String)"str", (ScriptValue)var92_82, (ScriptContext)var1_1), (ScriptContext)var1_1) : ScriptValue.NULL;
                        var0.val("leaf_dist", var132_122);
                        var97_87 = var132_122;
                        var133_123 = var1_1.getStr("next_leaf_frontier").equals("") != false ? var105_95 : ScriptValue.of((String)(var1_1.getStr("next_leaf_frontier") + ";" + var105_95.asStr()));
                        var0.val("next_leaf_frontier", var133_123);
                        var89_79 = var133_123;
                        var134_124 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("leaf_count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
                        var0.val("leaf_count", var134_124);
                        var87_77 = var134_124;
                    }
                }
            }
            var135_125 = var1_1.getClassOrVar("next_leaf_frontier");
            var0.val("leaf_frontier", var135_125);
            var104_94 = var135_125;
            var136_126 = ScriptFormula.addPolymorphic((ScriptValue)var106_96, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
            var0.val("lround_i", var136_126);
            var106_96 = var136_126;
        }
        return ScriptFormula.valuesEqualStr((ScriptValue)var100_90, (String)"") != false ? var29_24 : ScriptValue.of((String)(var29_24.asStr() + ";" + var100_90.asStr()));
    }

    public static ScriptValue _fellTree(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("contraption", scriptContext.getClassOrVar("contraption"));
        builder2.val("start_x", scriptContext.getClassOrVar("base_x"));
        builder2.val("start_y", scriptContext.getClassOrVar("base_y"));
        builder2.val("start_z", scriptContext.getClassOrVar("base_z"));
        ScriptValue scriptValue = TreeUtils._findTree(builder2);
        builder.val("tree", scriptValue);
        boolean bl = false;
        ScriptValue scriptValue2 = ScriptValue.of((boolean)false);
        builder.val("any_broken", scriptValue2);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";")), (ScriptContext)scriptContext));
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("p");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("blk");
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("result");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("item");
        if (list != null) {
            for (ScriptValue scriptValue7 : list) {
                Object object;
                builder.val("cell", scriptValue7);
                ScriptValue scriptValue8 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ",")), (ScriptContext)scriptContext);
                builder.val("p", scriptValue8);
                scriptValue3 = scriptValue8;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("contraption", scriptContext.getClassOrVar("contraption"));
                builder3.val("x", ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("base_x"), (ScriptValue)ScriptFormula.callBuiltin1((String)"num", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))), (ScriptContext)scriptContext)));
                builder3.val("y", ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("base_y"), (ScriptValue)ScriptFormula.callBuiltin1((String)"num", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0))), (ScriptContext)scriptContext)));
                builder3.val("z", ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("base_z"), (ScriptValue)ScriptFormula.callBuiltin1((String)"num", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 2.0))), (ScriptContext)scriptContext)));
                ScriptValue scriptValue9 = TreeUtils._getTreeBlock(builder3);
                builder.val("blk", scriptValue9);
                scriptValue4 = scriptValue9;
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && (scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) continue;
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
                if (scriptValue10 != ScriptValue.NULL) {
                    ScriptValue scriptValue11 = scriptValue4;
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("speed");
                    PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue10);
                    object = polyClassMachine != null ? polyClassMachine.tm$2_tick_break(scriptValue11, scriptValue12.asNum()) : PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue13 = object;
                builder.val("result", scriptValue13);
                scriptValue5 = scriptValue13;
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) continue;
                List list2 = ScriptProgram.elementsOf((ScriptValue)scriptValue5);
                if (list2 != null) {
                    for (ScriptValue scriptValue14 : list2) {
                        builder.val("item", scriptValue14);
                        ScriptFormula.callBuiltin1((String)"_deposit", (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
                    }
                }
                boolean bl2 = true;
                ScriptValue scriptValue15 = ScriptValue.of((boolean)true);
                builder.val("any_broken", scriptValue15);
            }
        }
        return scriptContext.getClassOrVar("any_broken");
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue.Array> arrayList = new ArrayList<ScriptValue.Array>();
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList.add(new ScriptValue.Array(arrayList2));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(ScriptValue.of((double)(-1.0)));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList.add(new ScriptValue.Array(arrayList3));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList.add(new ScriptValue.Array(arrayList4));
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList5.add(ScriptValue.of((double)(-1.0)));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList.add(new ScriptValue.Array(arrayList5));
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0));
        arrayList.add(new ScriptValue.Array(arrayList6));
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0));
        arrayList7.add(ScriptValue.of((double)(-1.0)));
        arrayList.add(new ScriptValue.Array(arrayList7));
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("OFFSETS6", (ScriptValue)array);
        FILE_SCOPE = builder.build();
    }
}
