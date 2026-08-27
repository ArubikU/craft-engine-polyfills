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
import java.util.ArrayList;
import java.util.List;

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
                var24_19 = new ArrayList<ScriptValue>();
                var24_19.add(var1_1.getClassOrVar("frontier"));
                var24_19.add(ScriptValue.of((String)";"));
                var21_16 = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", var24_19, (ScriptContext)var1_1), (int)1);
                if (var21_16 == null) break block19;
                block1: for (ScriptValue[] var23_18 : var21_16) {
                    var0.val("cell", var23_18.length > 0 ? var23_18[0] : ScriptValue.NULL);
                    var25_20 = new ArrayList<ScriptValue>();
                    var25_20.add(var1_1.getClassOrVar("cell"));
                    var25_20.add(ScriptValue.of((String)","));
                    var26_21 = ScriptFormula.callBuiltin((String)"split", var25_20, (ScriptContext)var1_1);
                    var0.val("p", var26_21);
                    var27_22 = new ArrayList<ScriptValue>();
                    var27_22.add(ScriptFormula.subscriptGet((ScriptValue)var26_21, (ScriptValue)ScriptValue.of((double)0.0)));
                    var28_23 = ScriptFormula.callBuiltin((String)"num", var27_22, (ScriptContext)var1_1);
                    var0.val("cx", var28_23);
                    var29_24 = new ArrayList<ScriptValue>();
                    var29_24.add(ScriptFormula.subscriptGet((ScriptValue)var26_21, (ScriptValue)ScriptValue.of((double)1.0)));
                    var30_25 = ScriptFormula.callBuiltin((String)"num", var29_24, (ScriptContext)var1_1);
                    var0.val("cy", var30_25);
                    var31_26 = new ArrayList<ScriptValue>();
                    var31_26.add(ScriptFormula.subscriptGet((ScriptValue)var26_21, (ScriptValue)ScriptValue.of((double)2.0)));
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
                                block20: {
                                    ++var44_36;
                                    if (!(var1_1.getNum("dz") <= 1.0)) break;
                                    var45_37 = (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dx"), (ScriptValue)ScriptValue.of((double)0.0)) != false && ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dy"), (ScriptValue)ScriptValue.of((double)0.0)) != false) != false && ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("dz"), (ScriptValue)ScriptValue.of((double)0.0)) != false;
                                    var46_38 = ScriptValue.of((boolean)var45_37);
                                    var0.val("is_self", var46_38);
                                    if (!((var45_37 ^ true) != false && var1_1.getNum("log_count") < var2_2 != false)) break block20;
                                    var47_39 = ScriptFormula.addPolymorphic((ScriptValue)var28_23, (ScriptValue)var1_1.getClassOrVar("dx"));
                                    var0.val("nx", var47_39);
                                    var48_40 = ScriptFormula.addPolymorphic((ScriptValue)var30_25, (ScriptValue)var1_1.getClassOrVar("dy"));
                                    var0.val("ny", var48_40);
                                    var49_41 = ScriptFormula.addPolymorphic((ScriptValue)var32_27, (ScriptValue)var1_1.getClassOrVar("dz"));
                                    var0.val("nz", var49_41);
                                    var50_42 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var47_39, (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)var48_40), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)var49_41);
                                    var0.val("key", var50_42);
                                    var51_43 = new ArrayList<ScriptValue>();
                                    var52_44 = new ArrayList<ScriptValue>();
                                    var52_44.add(var1_1.getClassOrVar("found"));
                                    var52_44.add(ScriptValue.of((String)";"));
                                    var51_43.add(ScriptFormula.callBuiltin((String)"split", var52_44, (ScriptContext)var1_1));
                                    var51_43.add(var50_42);
                                    if (!(ScriptFormula.callBuiltin((String)"contains", var51_43, (ScriptContext)var1_1).asBool() ^ true)) break block20;
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
                                        var58_50 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("found"), (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)var50_42);
                                        var0.val("found", var58_50);
                                        var59_51 = ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("next_frontier"), (String)"") != false ? var50_42 : ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("next_frontier"), (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)var50_42);
                                        var0.val("next_frontier", var59_51);
                                        var60_52 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("log_count"), (ScriptValue)ScriptValue.of((double)1.0));
                                        var0.val("log_count", var60_52);
                                    }
                                }
                                var61_53 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dz"), (ScriptValue)ScriptValue.of((double)1.0));
                                var0.val("dz", var61_53);
                            }
                            var62_54 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dy"), (ScriptValue)ScriptValue.of((double)1.0));
                            var0.val("dy", var62_54);
                        }
                        var63_55 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("dx"), (ScriptValue)ScriptValue.of((double)1.0));
                        var0.val("dx", var63_55);
                    }
                }
            }
            var64_56 = var1_1.getClassOrVar("next_frontier");
            var0.val("frontier", var64_56);
            var65_57 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("round_i"), (ScriptValue)ScriptValue.of((double)1.0));
            var0.val("round_i", var65_57);
        }
        var66_58 = ScriptValue.of((String)"");
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
            block21: {
                ++var76_66;
                if (!(((ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("leaf_frontier"), (String)"") ^ true) != false && var1_1.getNum("lround_i") < var8_6 != false) != false && var1_1.getNum("leaf_count") < var5_4 != false)) break;
                var77_67 = ScriptValue.of((String)"");
                var0.val("next_leaf_frontier", var77_67);
                var81_71 = new ArrayList<ScriptValue>();
                var81_71.add(var1_1.getClassOrVar("leaf_frontier"));
                var81_71.add(ScriptValue.of((String)";"));
                var78_68 = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", var81_71, (ScriptContext)var1_1), (int)1);
                if (var78_68 == null) break block21;
                for (ScriptValue[] var80_70 : var78_68) {
                    var0.val("cell", var80_70.length > 0 ? var80_70[0] : ScriptValue.NULL);
                    var82_72 = new ArrayList<ScriptValue>();
                    var82_72.add(var1_1.getClassOrVar("cell"));
                    var82_72.add(ScriptValue.of((String)","));
                    var83_73 = ScriptFormula.callBuiltin((String)"split", var82_72, (ScriptContext)var1_1);
                    var0.val("p", var83_73);
                    var84_74 = new ArrayList<ScriptValue>();
                    var84_74.add(ScriptFormula.subscriptGet((ScriptValue)var83_73, (ScriptValue)ScriptValue.of((double)0.0)));
                    var85_75 = ScriptFormula.callBuiltin((String)"num", var84_74, (ScriptContext)var1_1);
                    var0.val("cx", var85_75);
                    var86_76 = new ArrayList<ScriptValue>();
                    var86_76.add(ScriptFormula.subscriptGet((ScriptValue)var83_73, (ScriptValue)ScriptValue.of((double)1.0)));
                    var87_77 = ScriptFormula.callBuiltin((String)"num", var86_76, (ScriptContext)var1_1);
                    var0.val("cy", var87_77);
                    var88_78 = new ArrayList<ScriptValue>();
                    var88_78.add(ScriptFormula.subscriptGet((ScriptValue)var83_73, (ScriptValue)ScriptValue.of((double)2.0)));
                    var89_79 = ScriptFormula.callBuiltin((String)"num", var88_78, (ScriptContext)var1_1);
                    var0.val("cz", var89_79);
                    var90_80 = new ArrayList<ScriptValue>();
                    var91_81 = var1_1.getClassOrVar("leaf_dist");
                    if (var91_81 != ScriptValue.NULL) {
                        var92_82 = new ArrayList<ScriptValue>();
                        var92_82.add(var1_1.getClassOrVar("cell"));
                        var92_82.add(ScriptValue.of((String)"0"));
                        v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var91_81, var92_82, (ScriptContext)var1_1);
                    } else {
                        v1 /* !! */  = ScriptValue.NULL;
                    }
                    var90_80.add(v1 /* !! */ );
                    var93_83 = ScriptFormula.callBuiltin((String)"num", var90_80, (ScriptContext)var1_1);
                    var0.val("prev_dist", var93_83);
                    var94_84 = ScriptProgram.rowsOf((ScriptValue)var1_1.getClassOrVar("OFFSETS6"), (int)1);
                    if (var94_84 == null) continue;
                    for (ScriptValue[] var96_86 : var94_84) {
                        var0.val("off", var96_86.length > 0 ? var96_86[0] : ScriptValue.NULL);
                        if (!(var1_1.getNum("leaf_count") < var5_4)) continue;
                        var97_87 = ScriptFormula.addPolymorphic((ScriptValue)var85_75, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var1_1.getClassOrVar("off"), (ScriptValue)ScriptValue.of((double)0.0)));
                        var0.val("nx", var97_87);
                        var98_88 = ScriptFormula.addPolymorphic((ScriptValue)var87_77, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var1_1.getClassOrVar("off"), (ScriptValue)ScriptValue.of((double)1.0)));
                        var0.val("ny", var98_88);
                        var99_89 = ScriptFormula.addPolymorphic((ScriptValue)var89_79, (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var1_1.getClassOrVar("off"), (ScriptValue)ScriptValue.of((double)2.0)));
                        var0.val("nz", var99_89);
                        var100_90 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var97_87, (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)var98_88), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)var99_89);
                        var0.val("key", var100_90);
                        var101_91 = new ArrayList<ScriptValue>();
                        var102_92 = new ArrayList<ScriptValue>();
                        var102_92.add(var1_1.getClassOrVar("found"));
                        var102_92.add(ScriptValue.of((String)";"));
                        var101_91.add(ScriptFormula.callBuiltin((String)"split", var102_92, (ScriptContext)var1_1));
                        var101_91.add(var100_90);
                        if (ScriptFormula.callBuiltin((String)"contains", var101_91, (ScriptContext)var1_1).asBool()) ** GOTO lbl-1000
                        var103_93 = new ArrayList<ScriptValue>();
                        var104_94 = new ArrayList<ScriptValue>();
                        var104_94.add(var1_1.getClassOrVar("leaves"));
                        var104_94.add(ScriptValue.of((String)";"));
                        var103_93.add(ScriptFormula.callBuiltin((String)"split", var104_94, (ScriptContext)var1_1));
                        var103_93.add(var100_90);
                        if (!ScriptFormula.callBuiltin((String)"contains", var103_93, (ScriptContext)var1_1).asBool()) {
                            v2 = false;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v2 = true;
                        }
                        var105_95 = v2;
                        var106_96 = ScriptValue.of((boolean)v2);
                        var0.val("already", var106_96);
                        if (!(var105_95 ^ true)) continue;
                        var107_97 = ScriptContext.builder().copyFrom(var1_1);
                        var107_97.val("contraption", var1_1.getClassOrVar("contraption"));
                        var107_97.val("x", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_x"), (ScriptValue)var97_87));
                        var107_97.val("y", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_y"), (ScriptValue)var98_88));
                        var107_97.val("z", ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("start_z"), (ScriptValue)var99_89));
                        var108_98 = TreeUtils._getTreeBlock(var107_97);
                        var0.val("blk", var108_98);
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var108_98, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (((var109_99 = var1_1.getClassOrVar("blk")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var109_99, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) continue;
                        var110_100 = var1_1.getClassOrVar("blk");
                        if (var110_100 != ScriptValue.NULL) {
                            var111_101 = new ArrayList<ScriptValue>();
                            var111_101.add(ScriptValue.of((String)"distance"));
                            v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var110_100, var111_101, (ScriptContext)var1_1);
                        } else {
                            v3 /* !! */  = ScriptValue.NULL;
                        }
                        var112_102 = v3 /* !! */ ;
                        var0.val("dist_str", var112_102);
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var112_102, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (ScriptFormula.valuesEqualStr((ScriptValue)var112_102, (String)"") ^ true) != false)) continue;
                        var113_103 = new ArrayList<ScriptValue>();
                        var113_103.add(var112_102);
                        var114_104 = ScriptFormula.callBuiltin((String)"num", var113_103, (ScriptContext)var1_1);
                        var0.val("dist", var114_104);
                        if (!(var114_104.asNum() > var93_83.asNum())) continue;
                        var115_105 = ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("leaves"), (String)"") != false ? var100_90 : ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("leaves"), (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)var100_90);
                        var0.val("leaves", var115_105);
                        var116_106 = var1_1.getClassOrVar("leaf_dist");
                        if (var116_106 != ScriptValue.NULL) {
                            var117_107 = new ArrayList<ScriptValue>();
                            var117_107.add(var100_90);
                            var118_108 = new ArrayList<ScriptValue>();
                            var118_108.add(var114_104);
                            var117_107.add(ScriptFormula.callBuiltin((String)"str", var118_108, (ScriptContext)var1_1));
                            v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with", (ScriptValue)var116_106, var117_107, (ScriptContext)var1_1);
                        } else {
                            v4 /* !! */  = ScriptValue.NULL;
                        }
                        var119_109 = v4 /* !! */ ;
                        var0.val("leaf_dist", var119_109);
                        var120_110 = ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("next_leaf_frontier"), (String)"") != false ? var100_90 : ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("next_leaf_frontier"), (ScriptValue)ScriptValue.of((String)";")), (ScriptValue)var100_90);
                        var0.val("next_leaf_frontier", var120_110);
                        var121_111 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("leaf_count"), (ScriptValue)ScriptValue.of((double)1.0));
                        var0.val("leaf_count", var121_111);
                    }
                }
            }
            var122_112 = var1_1.getClassOrVar("next_leaf_frontier");
            var0.val("leaf_frontier", var122_112);
            var123_113 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("lround_i"), (ScriptValue)ScriptValue.of((double)1.0));
            var0.val("lround_i", var123_113);
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
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue);
        arrayList.add(ScriptValue.of((String)";"));
        List list = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList, (ScriptContext)scriptContext), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object;
                ScriptValue scriptValue3;
                builder.val("cell", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptContext.getClassOrVar("cell"));
                arrayList2.add(ScriptValue.of((String)","));
                ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"split", arrayList2, (ScriptContext)scriptContext);
                builder.val("p", scriptValue4);
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("contraption", scriptContext.getClassOrVar("contraption"));
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("base_x");
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)0.0)));
                builder3.val("x", ScriptFormula.addPolymorphic((ScriptValue)scriptValue5, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList3, (ScriptContext)scriptContext)));
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("base_y");
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)1.0)));
                builder3.val("y", ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList4, (ScriptContext)scriptContext)));
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("base_z");
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)2.0)));
                builder3.val("z", ScriptFormula.addPolymorphic((ScriptValue)scriptValue7, (ScriptValue)ScriptFormula.callBuiltin((String)"num", arrayList5, (ScriptContext)scriptContext)));
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
                        PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                        object = polyClassMachine_v3.tm$2_tick_break(scriptValue10, scriptValue11.asNum());
                    } else {
                        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                        arrayList6.add(scriptValue10);
                        arrayList6.add(scriptValue11);
                        object = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)scriptValue9, arrayList6, (ScriptContext)scriptContext);
                    }
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = object;
                builder.val("result", scriptValue12);
                if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue12, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) continue;
                List list2 = ScriptProgram.rowsOf((ScriptValue)scriptValue12, (int)1);
                if (list2 != null) {
                    for (ScriptValue[] scriptValueArray2 : list2) {
                        builder.val("item", scriptValueArray2.length > 0 ? scriptValueArray2[0] : ScriptValue.NULL);
                        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                        arrayList7.add(scriptContext.getClassOrVar("item"));
                        ScriptFormula.callBuiltin((String)"_deposit", arrayList7, (ScriptContext)scriptContext);
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
        FILE_SCOPE = builder.build();
    }
}
