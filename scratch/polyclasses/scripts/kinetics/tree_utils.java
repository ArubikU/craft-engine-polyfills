/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyClassWorld
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyClassWorld;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.List;

public final class TreeUtils {
    public static ScriptValue _isLog(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:oak_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:spruce_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:birch_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:jungle_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:acacia_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:dark_oak_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:cherry_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:mangrove_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:crimson_stem") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:warped_stem") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:stripped_oak_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:stripped_spruce_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:stripped_birch_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:stripped_jungle_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:stripped_acacia_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:stripped_dark_oak_log") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:mushroom_stem") ? 1 : 0) != 0);
    }

    public static ScriptValue _isLeafId(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:oak_leaves") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:spruce_leaves") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:birch_leaves") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:jungle_leaves") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:acacia_leaves") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:dark_oak_leaves") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:cherry_leaves") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:azalea_leaves") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:flowering_azalea_leaves") || ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("id"), (String)"minecraft:mangrove_leaves") ? 1 : 0) != 0);
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
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("x"));
            arrayList.add(scriptContext.getClassOrVar("y"));
            arrayList.add(scriptContext.getClassOrVar("z"));
            ScriptValue scriptValue = scriptContext.getClassOrVar("contraption");
            return PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("World");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("x"));
            arrayList.add(scriptContext.getClassOrVar("y"));
            arrayList.add(scriptContext.getClassOrVar("z"));
            object = scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("World") ? new PolyClassWorld(object2).um$1_get_block(arrayList) : PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
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
        var11_8 = ScriptValue.of((String)"0,0,0");
        var0.val("found", var11_8);
        var12_9 = ScriptValue.of((String)"0,0,0");
        var0.val("frontier", var12_9);
        var13_10 = 1.0;
        var15_11 = ScriptValue.of((double)1.0);
        var0.val("log_count", var15_11);
        var16_12 = 0.0;
        var18_13 = ScriptValue.of((double)0.0);
        var0.val("round_i", var18_13);
        var19_14 = 0;
        while (var19_14 < 1000) {
            block19: {
                ++var19_14;
                if (!(((ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("frontier"), (String)"") ^ true) != false && var1_1.getNum("round_i") < var8_6 != false) != false && var1_1.getNum("log_count") < var2_2 != false)) break;
                var20_15 = ScriptValue.of((String)"");
                var0.val("next_frontier", var20_15);
                var21_16 = ScriptProgram.resolveForRows((String)"split(frontier, \";\")", (ScriptContext)var1_1, (int)1);
                if (var21_16 == null) break block19;
                block1: for (ScriptValue[] var23_18 : var21_16) {
                    var0.val("cell", var23_18.length > 0 ? var23_18[0] : ScriptValue.NULL);
                    var24_19 = new ArrayList<ScriptValue>();
                    var24_19.add(var1_1.getClassOrVar("cell"));
                    var24_19.add(ScriptValue.of((String)","));
                    var25_20 = ScriptFormula.callBuiltin((String)"split", var24_19, (ScriptContext)var1_1);
                    var0.val("p", var25_20);
                    var26_21 = new ArrayList<ScriptValue>();
                    var26_21.add(ScriptFormula.subscriptGet((ScriptValue)var25_20, (ScriptValue)ScriptValue.of((double)0.0)));
                    var27_22 = ScriptFormula.callBuiltin((String)"num", var26_21, (ScriptContext)var1_1);
                    var0.val("cx", var27_22);
                    var28_23 = new ArrayList<ScriptValue>();
                    var28_23.add(ScriptFormula.subscriptGet((ScriptValue)var25_20, (ScriptValue)ScriptValue.of((double)1.0)));
                    var29_24 = ScriptFormula.callBuiltin((String)"num", var28_23, (ScriptContext)var1_1);
                    var0.val("cy", var29_24);
                    var30_25 = new ArrayList<ScriptValue>();
                    var30_25.add(ScriptFormula.subscriptGet((ScriptValue)var25_20, (ScriptValue)ScriptValue.of((double)2.0)));
                    var31_26 = ScriptFormula.callBuiltin((String)"num", var30_25, (ScriptContext)var1_1);
                    var0.val("cz", var31_26);
                    var32_27 = -1.0;
                    var34_28 = ScriptValue.of((double)var32_27);
                    var0.val("dx", var34_28);
                    var35_29 = 0;
                    while (var35_29 < 1000) {
                        ++var35_29;
                        if (!(var1_1.getNum("dx") <= 1.0)) continue block1;
                        var36_30 = 0.0;
                        var38_31 = ScriptValue.of((double)0.0);
                        var0.val("dy", var38_31);
                        var39_32 = 0;
                        while (var39_32 < 1000) {
                            ++var39_32;
                            if (!(var1_1.getNum("dy") <= 1.0)) break;
                            var40_33 = -1.0;
                            var42_34 = ScriptValue.of((double)var40_33);
                            var0.val("dz", var42_34);
                            var43_35 = 0;
                            while (var43_35 < 1000) {
                                block20: {
                                    ++var43_35;
                                    if (!(var1_1.getNum("dz") <= 1.0)) break;
                                    var44_36 = (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dx"), (ScriptValue)ScriptValue.of((double)0.0)) != false && ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dy"), (ScriptValue)ScriptValue.of((double)0.0)) != false) != false && ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dz"), (ScriptValue)ScriptValue.of((double)0.0)) != false;
                                    var45_37 = ScriptValue.of((boolean)var44_36);
                                    var0.val("is_self", var45_37);
                                    if (!((var44_36 ^ true) != false && var1_1.getNum("log_count") < var2_2 != false)) break block20;
                                    var46_38 = ScriptFormula.addPolymorphic((ScriptValue)var27_22, (ScriptValue)var1_1.getClassOrVar("dx"));
                                    var0.val("nx", var46_38);
                                    var47_39 = ScriptFormula.addPolymorphic((ScriptValue)var29_24, (ScriptValue)var1_1.getClassOrVar("dy"));
                                    var0.val("ny", var47_39);
                                    var48_40 = ScriptFormula.addPolymorphic((ScriptValue)var31_26, (ScriptValue)var1_1.getClassOrVar("dz"));
                                    var0.val("nz", var48_40);
                                    var49_41 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var46_38, (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)var47_39), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)var48_40);
                                    var0.val("key", var49_41);
                                    var50_42 = new ArrayList<ScriptValue>();
                                    var51_43 = new ArrayList<ScriptValue>();
                                    var51_43.add(var1_1.getClassOrVar("found"));
                                    var51_43.add(ScriptValue.of((String)";"));
                                    var50_42.add(ScriptFormula.callBuiltin((String)"split", var51_43, (ScriptContext)var1_1));
                                    var50_42.add(var49_41);
                                    if (!(ScriptFormula.callBuiltin((String)"contains", var50_42, (ScriptContext)var1_1).asBool() ^ true)) break block20;
                                    var52_44 = ScriptContext.builder().copyFrom(var1_1);
                                    var52_44.val("contraption", var1_1.getClassOrVar("contraption"));
                                    var52_44.val("x", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_x"), (ScriptValue)var46_38));
                                    var52_44.val("y", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_y"), (ScriptValue)var47_39));
                                    var52_44.val("z", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_z"), (ScriptValue)var48_40));
                                    var53_45 = TreeUtils._getTreeBlock(var52_44);
                                    var0.val("blk", var53_45);
                                    if (!((ScriptFormula.valuesEqual((ScriptValue)var53_45, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (((var54_46 = var1_1.getClassOrVar("blk")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var54_46, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) ** GOTO lbl-1000
                                    var55_47 = ScriptContext.builder().copyFrom(var1_1);
                                    var56_48 = var1_1.getClassOrVar("blk");
                                    var55_47.val("id", (ScriptValue)(var56_48 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var56_48, (ScriptContext)var1_1) : ScriptValue.NULL));
                                    if (TreeUtils._isLog(var55_47).asBool()) {
                                        v0 = true;
                                    } else lbl-1000:
                                    // 2 sources

                                    {
                                        v0 = false;
                                    }
                                    if (v0) {
                                        var57_49 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("found"), (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)var49_41);
                                        var0.val("found", var57_49);
                                        var58_50 = ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("next_frontier"), (String)"") != false ? var49_41 : ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("next_frontier"), (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)var49_41);
                                        var0.val("next_frontier", var58_50);
                                        var59_51 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("log_count"), (ScriptValue)ScriptValue.of((double)1.0));
                                        var0.val("log_count", var59_51);
                                    }
                                }
                                var60_52 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dz"), (ScriptValue)ScriptValue.of((double)1.0));
                                var0.val("dz", var60_52);
                            }
                            var61_53 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dy"), (ScriptValue)ScriptValue.of((double)1.0));
                            var0.val("dy", var61_53);
                        }
                        var62_54 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dx"), (ScriptValue)ScriptValue.of((double)1.0));
                        var0.val("dx", var62_54);
                    }
                }
            }
            var63_55 = var1_1.getClassOrVar("next_frontier");
            var0.val("frontier", var63_55);
            var64_56 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("round_i"), (ScriptValue)ScriptValue.of((double)1.0));
            var0.val("round_i", var64_56);
        }
        var65_57 = ScriptValue.of((String)"");
        var0.val("leaves", var65_57);
        var66_58 = new ArrayList<E>();
        var67_59 = ScriptFormula.callBuiltin((String)"make_map", var66_58, (ScriptContext)var1_1);
        var0.val("leaf_dist", var67_59);
        var68_60 = var1_1.getClassOrVar("found");
        var0.val("leaf_frontier", var68_60);
        var69_61 = 0.0;
        var71_62 = ScriptValue.of((double)0.0);
        var0.val("leaf_count", var71_62);
        var72_63 = 0.0;
        var74_64 = ScriptValue.of((double)0.0);
        var0.val("lround_i", var74_64);
        var75_65 = 0;
        while (var75_65 < 1000) {
            block21: {
                ++var75_65;
                if (!(((ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("leaf_frontier"), (String)"") ^ true) != false && var1_1.getNum("lround_i") < var8_6 != false) != false && var1_1.getNum("leaf_count") < var5_4 != false)) break;
                var76_66 = ScriptValue.of((String)"");
                var0.val("next_leaf_frontier", var76_66);
                var77_67 = ScriptProgram.resolveForRows((String)"split(leaf_frontier, \";\")", (ScriptContext)var1_1, (int)1);
                if (var77_67 == null) break block21;
                for (ScriptValue[] var79_69 : var77_67) {
                    var0.val("cell", var79_69.length > 0 ? var79_69[0] : ScriptValue.NULL);
                    var80_70 = new ArrayList<ScriptValue>();
                    var80_70.add(var1_1.getClassOrVar("cell"));
                    var80_70.add(ScriptValue.of((String)","));
                    var81_71 = ScriptFormula.callBuiltin((String)"split", var80_70, (ScriptContext)var1_1);
                    var0.val("p", var81_71);
                    var82_72 = new ArrayList<ScriptValue>();
                    var82_72.add(ScriptFormula.subscriptGet((ScriptValue)var81_71, (ScriptValue)ScriptValue.of((double)0.0)));
                    var83_73 = ScriptFormula.callBuiltin((String)"num", var82_72, (ScriptContext)var1_1);
                    var0.val("cx", var83_73);
                    var84_74 = new ArrayList<ScriptValue>();
                    var84_74.add(ScriptFormula.subscriptGet((ScriptValue)var81_71, (ScriptValue)ScriptValue.of((double)1.0)));
                    var85_75 = ScriptFormula.callBuiltin((String)"num", var84_74, (ScriptContext)var1_1);
                    var0.val("cy", var85_75);
                    var86_76 = new ArrayList<ScriptValue>();
                    var86_76.add(ScriptFormula.subscriptGet((ScriptValue)var81_71, (ScriptValue)ScriptValue.of((double)2.0)));
                    var87_77 = ScriptFormula.callBuiltin((String)"num", var86_76, (ScriptContext)var1_1);
                    var0.val("cz", var87_77);
                    var88_78 = new ArrayList<ScriptValue>();
                    var89_79 = var1_1.getClassOrVar("leaf_dist");
                    if (var89_79 != ScriptValue.NULL) {
                        var90_80 = new ArrayList<ScriptValue>();
                        var90_80.add(var1_1.getClassOrVar("cell"));
                        var90_80.add(ScriptValue.of((String)"0"));
                        v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var89_79, var90_80, (ScriptContext)var1_1);
                    } else {
                        v1 /* !! */  = ScriptValue.NULL;
                    }
                    var88_78.add(v1 /* !! */ );
                    var91_81 = ScriptFormula.callBuiltin((String)"num", var88_78, (ScriptContext)var1_1);
                    var0.val("prev_dist", var91_81);
                    var92_82 = ScriptProgram.resolveForRows((String)"OFFSETS6", (ScriptContext)var1_1, (int)1);
                    if (var92_82 == null) continue;
                    for (ScriptValue[] var94_84 : var92_82) {
                        var0.val("off", var94_84.length > 0 ? var94_84[0] : ScriptValue.NULL);
                        if (!(var1_1.getNum("leaf_count") < var5_4)) continue;
                        var95_85 = ScriptFormula.addPolymorphic((ScriptValue)var83_73, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var1_1.getClassOrVar("off"), (ScriptValue)ScriptValue.of((double)0.0)));
                        var0.val("nx", var95_85);
                        var96_86 = ScriptFormula.addPolymorphic((ScriptValue)var85_75, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var1_1.getClassOrVar("off"), (ScriptValue)ScriptValue.of((double)1.0)));
                        var0.val("ny", var96_86);
                        var97_87 = ScriptFormula.addPolymorphic((ScriptValue)var87_77, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var1_1.getClassOrVar("off"), (ScriptValue)ScriptValue.of((double)2.0)));
                        var0.val("nz", var97_87);
                        var98_88 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var95_85, (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)var96_86), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)var97_87);
                        var0.val("key", var98_88);
                        var99_89 = new ArrayList<ScriptValue>();
                        var100_90 = new ArrayList<ScriptValue>();
                        var100_90.add(var1_1.getClassOrVar("found"));
                        var100_90.add(ScriptValue.of((String)";"));
                        var99_89.add(ScriptFormula.callBuiltin((String)"split", var100_90, (ScriptContext)var1_1));
                        var99_89.add(var98_88);
                        if (ScriptFormula.callBuiltin((String)"contains", var99_89, (ScriptContext)var1_1).asBool()) ** GOTO lbl-1000
                        var101_91 = new ArrayList<ScriptValue>();
                        var102_92 = new ArrayList<ScriptValue>();
                        var102_92.add(var1_1.getClassOrVar("leaves"));
                        var102_92.add(ScriptValue.of((String)";"));
                        var101_91.add(ScriptFormula.callBuiltin((String)"split", var102_92, (ScriptContext)var1_1));
                        var101_91.add(var98_88);
                        if (!ScriptFormula.callBuiltin((String)"contains", var101_91, (ScriptContext)var1_1).asBool()) {
                            v2 = false;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v2 = true;
                        }
                        var103_93 = v2;
                        var104_94 = ScriptValue.of((boolean)v2);
                        var0.val("already", var104_94);
                        if (!(var103_93 ^ true)) continue;
                        var105_95 = ScriptContext.builder().copyFrom(var1_1);
                        var105_95.val("contraption", var1_1.getClassOrVar("contraption"));
                        var105_95.val("x", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_x"), (ScriptValue)var95_85));
                        var105_95.val("y", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_y"), (ScriptValue)var96_86));
                        var105_95.val("z", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_z"), (ScriptValue)var97_87));
                        var106_96 = TreeUtils._getTreeBlock(var105_95);
                        var0.val("blk", var106_96);
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var106_96, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (((var107_97 = var1_1.getClassOrVar("blk")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var107_97, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) continue;
                        var108_98 = var1_1.getClassOrVar("blk");
                        if (var108_98 != ScriptValue.NULL) {
                            var109_99 = new ArrayList<ScriptValue>();
                            var109_99.add(ScriptValue.of((String)"distance"));
                            v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var108_98, var109_99, (ScriptContext)var1_1);
                        } else {
                            v3 /* !! */  = ScriptValue.NULL;
                        }
                        var110_100 = v3 /* !! */ ;
                        var0.val("dist_str", var110_100);
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var110_100, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (ScriptFormula.valuesEqualStr((ScriptValue)var110_100, (String)"") ^ true) != false)) continue;
                        var111_101 = new ArrayList<ScriptValue>();
                        var111_101.add(var110_100);
                        var112_102 = ScriptFormula.callBuiltin((String)"num", var111_101, (ScriptContext)var1_1);
                        var0.val("dist", var112_102);
                        if (!(var112_102.asNum() > var91_81.asNum())) continue;
                        var113_103 = ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("leaves"), (String)"") != false ? var98_88 : ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("leaves"), (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)var98_88);
                        var0.val("leaves", var113_103);
                        var114_104 = var1_1.getClassOrVar("leaf_dist");
                        if (var114_104 != ScriptValue.NULL) {
                            var115_105 = new ArrayList<ScriptValue>();
                            var115_105.add(var98_88);
                            var116_106 = new ArrayList<ScriptValue>();
                            var116_106.add(var112_102);
                            var115_105.add(ScriptFormula.callBuiltin((String)"str", var116_106, (ScriptContext)var1_1));
                            v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with", (ScriptValue)var114_104, var115_105, (ScriptContext)var1_1);
                        } else {
                            v4 /* !! */  = ScriptValue.NULL;
                        }
                        var117_107 = v4 /* !! */ ;
                        var0.val("leaf_dist", var117_107);
                        var118_108 = ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("next_leaf_frontier"), (String)"") != false ? var98_88 : ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("next_leaf_frontier"), (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)var98_88);
                        var0.val("next_leaf_frontier", var118_108);
                        var119_109 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("leaf_count"), (ScriptValue)ScriptValue.of((double)1.0));
                        var0.val("leaf_count", var119_109);
                    }
                }
            }
            var120_110 = var1_1.getClassOrVar("next_leaf_frontier");
            var0.val("leaf_frontier", var120_110);
            var121_111 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("lround_i"), (ScriptValue)ScriptValue.of((double)1.0));
            var0.val("lround_i", var121_111);
        }
        return ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("leaves"), (String)"") != false ? var1_1.getClassOrVar("found") : ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("found"), (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)var1_1.getClassOrVar("leaves"));
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
        List list = ScriptProgram.resolveForRows((String)"split(tree, \";\")", (ScriptContext)scriptContext, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object;
                ScriptValue scriptValue3;
                builder.val("cell", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("cell"));
                arrayList.add(ScriptValue.of((String)","));
                ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext);
                builder.val("p", scriptValue4);
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("contraption", scriptContext.getClassOrVar("contraption"));
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("base_x");
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)0.0)));
                builder3.val("x", ScriptFormula.addPolymorphic((ScriptValue)scriptValue5, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList2, (ScriptContext)scriptContext)));
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("base_y");
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)1.0)));
                builder3.val("y", ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList3, (ScriptContext)scriptContext)));
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("base_z");
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)2.0)));
                builder3.val("z", ScriptFormula.addPolymorphic((ScriptValue)scriptValue7, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList4, (ScriptContext)scriptContext)));
                ScriptValue scriptValue8 = TreeUtils._getTreeBlock(builder3);
                builder.val("blk", scriptValue8);
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue8, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ((scriptValue3 = scriptContext.getClassOrVar("blk")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) continue;
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
                if (scriptValue9 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object2;
                    ScriptValue scriptValue10 = scriptValue8;
                    ScriptValue scriptValue11 = scriptContext.getClassOrVar("speed");
                    if (scriptValue9 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                        object = polyClassMachine_v2.tm$2_tick_break(scriptValue10, scriptValue11.asNum());
                    } else {
                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                        arrayList5.add(scriptValue10);
                        arrayList5.add(scriptValue11);
                        object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue9, arrayList5, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = object;
                builder.val("result", scriptValue12);
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue12, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) continue;
                List list2 = ScriptProgram.resolveForRows((String)"result", (ScriptContext)scriptContext, (int)1);
                if (list2 != null) {
                    for (ScriptValue[] scriptValueArray2 : list2) {
                        builder.val("item", scriptValueArray2.length > 0 ? scriptValueArray2[0] : ScriptValue.NULL);
                        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                        arrayList6.add(scriptContext.getClassOrVar("item"));
                        ScriptFormula.callBuiltin((String)"_deposit", arrayList6, (ScriptContext)scriptContext);
                    }
                }
                boolean bl2 = true;
                ScriptValue scriptValue13 = ScriptValue.of((boolean)true);
                builder.val("any_broken", scriptValue13);
            }
        }
        return scriptContext.getClassOrVar("any_broken");
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue.Array> arrayList = new ArrayList<ScriptValue.Array>();
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(ScriptValue.of((double)1.0));
        arrayList2.add(ScriptValue.of((double)0.0));
        arrayList2.add(ScriptValue.of((double)0.0));
        arrayList.add(new ScriptValue.Array(arrayList2));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(ScriptValue.of((double)(-1.0)));
        arrayList3.add(ScriptValue.of((double)0.0));
        arrayList3.add(ScriptValue.of((double)0.0));
        arrayList.add(new ScriptValue.Array(arrayList3));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add(ScriptValue.of((double)0.0));
        arrayList4.add(ScriptValue.of((double)1.0));
        arrayList4.add(ScriptValue.of((double)0.0));
        arrayList.add(new ScriptValue.Array(arrayList4));
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add(ScriptValue.of((double)0.0));
        arrayList5.add(ScriptValue.of((double)(-1.0)));
        arrayList5.add(ScriptValue.of((double)0.0));
        arrayList.add(new ScriptValue.Array(arrayList5));
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add(ScriptValue.of((double)0.0));
        arrayList6.add(ScriptValue.of((double)0.0));
        arrayList6.add(ScriptValue.of((double)1.0));
        arrayList.add(new ScriptValue.Array(arrayList6));
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add(ScriptValue.of((double)0.0));
        arrayList7.add(ScriptValue.of((double)0.0));
        arrayList7.add(ScriptValue.of((double)(-1.0)));
        arrayList.add(new ScriptValue.Array(arrayList7));
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("OFFSETS6", (ScriptValue)array);
    }
}
