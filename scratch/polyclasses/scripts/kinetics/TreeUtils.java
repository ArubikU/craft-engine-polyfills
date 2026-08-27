/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
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
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
        while (var19_14 < 1000) {
            block15: {
                ++var19_14;
                if (!(((var1_1.getStr("frontier").equals("") ^ true) != false && var1_1.getNum("round_i") < var8_6 != false) != false && var1_1.getNum("log_count") < var2_2 != false)) break;
                var20_15 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "");
                var0.val("next_frontier", var20_15);
                var24_19 = new ArrayList<ScriptValue>();
                var24_19.add(var1_1.getClassOrVar("frontier"));
                var24_19.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";"));
                var21_16 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", var24_19, (ScriptContext)var1_1));
                if (var21_16 == null) break block15;
                block1: for (ScriptValue var23_18 : var21_16) {
                    var0.val("cell", var23_18);
                    var25_20 = new ArrayList<ScriptValue>();
                    var25_20.add(var1_1.getClassOrVar("cell"));
                    var25_20.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ","));
                    var26_21 = ScriptFormula.callBuiltin((String)"split", var25_20, (ScriptContext)var1_1);
                    var0.val("p", var26_21);
                    var27_22 = new ArrayList<ScriptValue>();
                    var27_22.add(ScriptFormula.subscriptGet((ScriptValue)var26_21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))));
                    var28_23 = ScriptFormula.callBuiltin((String)"num", var27_22, (ScriptContext)var1_1);
                    var0.val("cx", var28_23);
                    var29_24 = new ArrayList<ScriptValue>();
                    var29_24.add(ScriptFormula.subscriptGet((ScriptValue)var26_21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0))));
                    var30_25 = ScriptFormula.callBuiltin((String)"num", var29_24, (ScriptContext)var1_1);
                    var0.val("cy", var30_25);
                    var31_26 = new ArrayList<ScriptValue>();
                    var31_26.add(ScriptFormula.subscriptGet((ScriptValue)var26_21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 2.0))));
                    var32_27 = ScriptFormula.callBuiltin((String)"num", var31_26, (ScriptContext)var1_1);
                    var0.val("cz", var32_27);
                    var33_28 = -1.0;
                    var35_29 = ScriptValue.of((double)var33_28);
                    var0.val("dx", var35_29);
                    var36_30 = 0;
                    while (var36_30 < 1000) {
                        ++var36_30;
                        if (!(var1_1.getNum("dx") <= 1.0)) continue block1;
                        var37_31 = 0.0;
                        var39_32 = ScriptValue.of((double)0.0);
                        var0.val("dy", var39_32);
                        var40_33 = 0;
                        while (var40_33 < 1000) {
                            ++var40_33;
                            if (!(var1_1.getNum("dy") <= 1.0)) break;
                            var41_34 = -1.0;
                            var43_35 = ScriptValue.of((double)var41_34);
                            var0.val("dz", var43_35);
                            var44_36 = 0;
                            while (var44_36 < 1000) {
                                block16: {
                                    ++var44_36;
                                    if (!(var1_1.getNum("dz") <= 1.0)) break;
                                    var45_37 = (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))) != false && ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dy"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))) != false) != false && ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dz"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))) != false;
                                    var46_38 = ScriptValue.of((boolean)var45_37);
                                    var0.val("is_self", var46_38);
                                    if (!((var45_37 ^ true) != false && var1_1.getNum("log_count") < var2_2 != false)) break block16;
                                    var47_39 = ScriptFormula.addPolymorphic((ScriptValue)var28_23, (ScriptValue)var1_1.getClassOrVar("dx"));
                                    var0.val("nx", var47_39);
                                    var48_40 = ScriptFormula.addPolymorphic((ScriptValue)var30_25, (ScriptValue)var1_1.getClassOrVar("dy"));
                                    var0.val("ny", var48_40);
                                    var49_41 = ScriptFormula.addPolymorphic((ScriptValue)var32_27, (ScriptValue)var1_1.getClassOrVar("dz"));
                                    var0.val("nz", var49_41);
                                    var50_42 = ScriptValue.of((String)(var47_39.asStr() + "," + var48_40.asStr() + "," + var49_41.asStr()));
                                    var0.val("key", var50_42);
                                    var51_43 = new ArrayList<ScriptValue>();
                                    var52_44 = new ArrayList<ScriptValue>();
                                    var52_44.add(var1_1.getClassOrVar("found"));
                                    var52_44.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";"));
                                    var51_43.add(ScriptFormula.callBuiltin((String)"split", var52_44, (ScriptContext)var1_1));
                                    var51_43.add(var50_42);
                                    if (!(ScriptFormula.callBuiltin((String)"contains", var51_43, (ScriptContext)var1_1).asBool() ^ true)) break block16;
                                    var53_45 = ScriptContext.builder().copyFrom(var1_1);
                                    var53_45.val("contraption", var1_1.getClassOrVar("contraption"));
                                    var53_45.val("x", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_x"), (ScriptValue)var47_39));
                                    var53_45.val("y", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_y"), (ScriptValue)var48_40));
                                    var53_45.val("z", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_z"), (ScriptValue)var49_41));
                                    var54_46 = TreeUtils._getTreeBlock(var53_45);
                                    var0.val("blk", var54_46);
                                    if (!((ScriptFormula.valuesEqual((ScriptValue)var54_46, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (((var55_47 = var1_1.getClassOrVar("blk")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var55_47, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) ** GOTO lbl-1000
                                    var56_48 = ScriptContext.builder().copyFrom(var1_1);
                                    var57_49 = var1_1.getClassOrVar("blk");
                                    var56_48.val("id", (ScriptValue)(var57_49 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var57_49, (ScriptContext)var1_1) : ScriptValue.NULL));
                                    if (TreeUtils._isLog(var56_48).asBool()) {
                                        v0 = true;
                                    } else lbl-1000:
                                    // 2 sources

                                    {
                                        v0 = false;
                                    }
                                    if (v0) {
                                        var58_50 = ScriptValue.of((String)(var1_1.getStr("found") + ";" + var50_42.asStr()));
                                        var0.val("found", var58_50);
                                        var59_51 = var1_1.getStr("next_frontier").equals("") != false ? var50_42 : ScriptValue.of((String)(var1_1.getStr("next_frontier") + ";" + var50_42.asStr()));
                                        var0.val("next_frontier", var59_51);
                                        var60_52 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("log_count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
                                        var0.val("log_count", var60_52);
                                    }
                                }
                                var61_53 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dz"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
                                var0.val("dz", var61_53);
                            }
                            var62_54 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dy"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
                            var0.val("dy", var62_54);
                        }
                        var63_55 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
                        var0.val("dx", var63_55);
                    }
                }
            }
            var64_56 = var1_1.getClassOrVar("next_frontier");
            var0.val("frontier", var64_56);
            var65_57 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("round_i"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
            var0.val("round_i", var65_57);
        }
        var66_58 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "");
        var0.val("leaves", var66_58);
        var67_59 = new ArrayList<E>();
        var68_60 = ScriptFormula.callBuiltin((String)"make_map", var67_59, (ScriptContext)var1_1);
        var0.val("leaf_dist", var68_60);
        var69_61 = var1_1.getClassOrVar("found");
        var0.val("leaf_frontier", var69_61);
        var70_62 = 0.0;
        var72_63 = ScriptValue.of((double)0.0);
        var0.val("leaf_count", var72_63);
        var73_64 = 0.0;
        var75_65 = ScriptValue.of((double)0.0);
        var0.val("lround_i", var75_65);
        var76_66 = 0;
        while (var76_66 < 1000) {
            block17: {
                ++var76_66;
                if (!(((var1_1.getStr("leaf_frontier").equals("") ^ true) != false && var1_1.getNum("lround_i") < var8_6 != false) != false && var1_1.getNum("leaf_count") < var5_4 != false)) break;
                var77_67 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "");
                var0.val("next_leaf_frontier", var77_67);
                var81_71 = new ArrayList<ScriptValue>();
                var81_71.add(var1_1.getClassOrVar("leaf_frontier"));
                var81_71.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";"));
                var78_68 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", var81_71, (ScriptContext)var1_1));
                if (var78_68 == null) break block17;
                for (ScriptValue var80_70 : var78_68) {
                    var0.val("cell", var80_70);
                    var82_72 = new ArrayList<ScriptValue>();
                    var82_72.add(var1_1.getClassOrVar("cell"));
                    var82_72.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ","));
                    var83_73 = ScriptFormula.callBuiltin((String)"split", var82_72, (ScriptContext)var1_1);
                    var0.val("p", var83_73);
                    var84_74 = new ArrayList<ScriptValue>();
                    var84_74.add(ScriptFormula.subscriptGet((ScriptValue)var83_73, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))));
                    var85_75 = ScriptFormula.callBuiltin((String)"num", var84_74, (ScriptContext)var1_1);
                    var0.val("cx", var85_75);
                    var86_76 = new ArrayList<ScriptValue>();
                    var86_76.add(ScriptFormula.subscriptGet((ScriptValue)var83_73, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0))));
                    var87_77 = ScriptFormula.callBuiltin((String)"num", var86_76, (ScriptContext)var1_1);
                    var0.val("cy", var87_77);
                    var88_78 = new ArrayList<ScriptValue>();
                    var88_78.add(ScriptFormula.subscriptGet((ScriptValue)var83_73, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 2.0))));
                    var89_79 = ScriptFormula.callBuiltin((String)"num", var88_78, (ScriptContext)var1_1);
                    var0.val("cz", var89_79);
                    var90_80 = new ArrayList<ScriptValue>();
                    var91_81 = var1_1.getClassOrVar("leaf_dist");
                    var90_80.add((ScriptValue)(var91_81 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var91_81, (ScriptValue)var1_1.getClassOrVar("cell"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "0")), (ScriptContext)var1_1) : ScriptValue.NULL));
                    var92_82 = ScriptFormula.callBuiltin((String)"num", var90_80, (ScriptContext)var1_1);
                    var0.val("prev_dist", var92_82);
                    var93_83 = ScriptProgram.elementsOf((ScriptValue)var1_1.getClassOrVar("OFFSETS6"));
                    if (var93_83 == null) continue;
                    for (ScriptValue var95_85 : var93_83) {
                        var0.val("off", var95_85);
                        if (!(var1_1.getNum("leaf_count") < var5_4)) continue;
                        var96_86 = ScriptFormula.addPolymorphic((ScriptValue)var85_75, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var1_1.getClassOrVar("off"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))));
                        var0.val("nx", var96_86);
                        var97_87 = ScriptFormula.addPolymorphic((ScriptValue)var87_77, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var1_1.getClassOrVar("off"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0))));
                        var0.val("ny", var97_87);
                        var98_88 = ScriptFormula.addPolymorphic((ScriptValue)var89_79, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var1_1.getClassOrVar("off"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 2.0))));
                        var0.val("nz", var98_88);
                        var99_89 = ScriptValue.of((String)(var96_86.asStr() + "," + var97_87.asStr() + "," + var98_88.asStr()));
                        var0.val("key", var99_89);
                        var100_90 = new ArrayList<ScriptValue>();
                        var101_91 = new ArrayList<ScriptValue>();
                        var101_91.add(var1_1.getClassOrVar("found"));
                        var101_91.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";"));
                        var100_90.add(ScriptFormula.callBuiltin((String)"split", var101_91, (ScriptContext)var1_1));
                        var100_90.add(var99_89);
                        if (ScriptFormula.callBuiltin((String)"contains", var100_90, (ScriptContext)var1_1).asBool()) ** GOTO lbl-1000
                        var102_92 = new ArrayList<ScriptValue>();
                        var103_93 = new ArrayList<ScriptValue>();
                        var103_93.add(var1_1.getClassOrVar("leaves"));
                        var103_93.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";"));
                        var102_92.add(ScriptFormula.callBuiltin((String)"split", var103_93, (ScriptContext)var1_1));
                        var102_92.add(var99_89);
                        if (!ScriptFormula.callBuiltin((String)"contains", var102_92, (ScriptContext)var1_1).asBool()) {
                            v1 = false;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v1 = true;
                        }
                        var104_94 = v1;
                        var105_95 = ScriptValue.of((boolean)v1);
                        var0.val("already", var105_95);
                        if (!(var104_94 ^ true)) continue;
                        var106_96 = ScriptContext.builder().copyFrom(var1_1);
                        var106_96.val("contraption", var1_1.getClassOrVar("contraption"));
                        var106_96.val("x", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_x"), (ScriptValue)var96_86));
                        var106_96.val("y", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_y"), (ScriptValue)var97_87));
                        var106_96.val("z", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_z"), (ScriptValue)var98_88));
                        var107_97 = TreeUtils._getTreeBlock(var106_96);
                        var0.val("blk", var107_97);
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var107_97, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (((var108_98 = var1_1.getClassOrVar("blk")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var108_98, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) continue;
                        var109_99 = var1_1.getClassOrVar("blk");
                        var110_100 = var109_99 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var109_99, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, "distance")), (ScriptContext)var1_1) : ScriptValue.NULL;
                        var0.val("dist_str", var110_100);
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var110_100, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (ScriptFormula.valuesEqualStr((ScriptValue)var110_100, (String)"") ^ true) != false)) continue;
                        var111_101 = new ArrayList<ScriptValue>();
                        var111_101.add(var110_100);
                        var112_102 = ScriptFormula.callBuiltin((String)"num", var111_101, (ScriptContext)var1_1);
                        var0.val("dist", var112_102);
                        if (!(var112_102.asNum() > var92_82.asNum())) continue;
                        var113_103 = var1_1.getStr("leaves").equals("") != false ? var99_89 : ScriptValue.of((String)(var1_1.getStr("leaves") + ";" + var99_89.asStr()));
                        var0.val("leaves", var113_103);
                        var114_104 = var1_1.getClassOrVar("leaf_dist");
                        if (var114_104 != ScriptValue.NULL) {
                            var115_105 = new ArrayList<ScriptValue>();
                            var115_105.add(var112_102);
                            v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with", (ScriptValue)var114_104, (ScriptValue)var99_89, (ScriptValue)ScriptFormula.callBuiltin((String)"str", var115_105, (ScriptContext)var1_1), (ScriptContext)var1_1);
                        } else {
                            v2 /* !! */  = ScriptValue.NULL;
                        }
                        var116_106 = v2 /* !! */ ;
                        var0.val("leaf_dist", var116_106);
                        var117_107 = var1_1.getStr("next_leaf_frontier").equals("") != false ? var99_89 : ScriptValue.of((String)(var1_1.getStr("next_leaf_frontier") + ";" + var99_89.asStr()));
                        var0.val("next_leaf_frontier", var117_107);
                        var118_108 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("leaf_count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
                        var0.val("leaf_count", var118_108);
                    }
                }
            }
            var119_109 = var1_1.getClassOrVar("next_leaf_frontier");
            var0.val("leaf_frontier", var119_109);
            var120_110 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("lround_i"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0)));
            var0.val("lround_i", var120_110);
        }
        return var1_1.getStr("leaves").equals("") != false ? var1_1.getClassOrVar("found") : ScriptValue.of((String)(var1_1.getStr("found") + ";" + var1_1.getStr("leaves")));
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
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue);
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ";"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                Object object;
                ScriptValue scriptValue4;
                builder.val("cell", scriptValue3);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptContext.getClassOrVar("cell"));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", TreeUtils.class, ","));
                ScriptValue scriptValue5 = ScriptFormula.callBuiltin((String)"split", arrayList2, (ScriptContext)scriptContext);
                builder.val("p", scriptValue5);
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("contraption", scriptContext.getClassOrVar("contraption"));
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("base_x");
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 0.0))));
                builder3.val("x", ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList3, (ScriptContext)scriptContext)));
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("base_y");
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 1.0))));
                builder3.val("y", ScriptFormula.addPolymorphic((ScriptValue)scriptValue7, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList4, (ScriptContext)scriptContext)));
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("base_z");
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", TreeUtils.class, 2.0))));
                builder3.val("z", ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList5, (ScriptContext)scriptContext)));
                ScriptValue scriptValue9 = TreeUtils._getTreeBlock(builder3);
                builder.val("blk", scriptValue9);
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue9, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ((scriptValue4 = scriptContext.getClassOrVar("blk")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true)) continue;
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
                if (scriptValue10 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object2;
                    ScriptValue scriptValue11 = scriptValue9;
                    ScriptValue scriptValue12 = scriptContext.getClassOrVar("speed");
                    if (scriptValue10 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                        object = polyClassMachine_v3.tm$2_tick_break(scriptValue11, scriptValue12.asNum());
                    } else {
                        object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue13 = object;
                builder.val("result", scriptValue13);
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue13, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) continue;
                List list2 = ScriptProgram.elementsOf((ScriptValue)scriptValue13);
                if (list2 != null) {
                    for (ScriptValue scriptValue14 : list2) {
                        builder.val("item", scriptValue14);
                        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                        arrayList6.add(scriptContext.getClassOrVar("item"));
                        ScriptFormula.callBuiltin((String)"_deposit", arrayList6, (ScriptContext)scriptContext);
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
