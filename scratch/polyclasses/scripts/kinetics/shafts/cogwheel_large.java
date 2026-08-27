/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.shafts;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.Utils;
import java.util.ArrayList;

public final class CogwheelLarge {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onBreak(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v3.tm$106_set_rpm_output(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            double d = 0.0;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                v1 = ScriptValue.of((boolean)polyClassMachine_v3.tm$56_report_su(d));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block64: {
            var1_1 = var0.peek();
            v0 = new ArrayList<String>();
            v0.add("_update_activated");
            ScriptProgram.applyImport((ScriptContext.Builder)var0, (String)"kinetics/utils.pf", null, v0);
            var2_2 = var1_1.getClassOrVar("Machine");
            if (var2_2 != ScriptValue.NULL) {
                var3_3 = var1_1.getClassOrVar("rpm");
                if (var2_2 instanceof ScriptValue.Obj && (var5_5 = (var4_4 = (ScriptValue.Obj)var2_2).instance()) != null && !(var5_5 instanceof PolyClass) && var4_4.typeName().equals("Machine")) {
                    var6_6 = new PolyClassMachine_v3(var5_5);
                    v1 /* !! */  = ScriptValue.of((boolean)var6_6.tm$106_set_rpm_output(var3_3.asNum()));
                } else {
                    var7_7 = new ArrayList<ScriptValue>();
                    var7_7.add(var3_3);
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)var2_2, var7_7, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            var8_8 = var1_1.getClassOrVar("Machine");
            var11_11 = var8_8 != ScriptValue.NULL ? (var8_8 instanceof ScriptValue.Obj && (var10_10 = (var9_9 = (ScriptValue.Obj)var8_8).instance()) != null && !(var10_10 instanceof PolyClass) && var9_9.typeName().equals("Machine") ? new PolyClassMachine_v3(var10_10).pg$123_axis() : PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)var8_8, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("axis", var11_11);
            if (ScriptFormula.valuesEqual((ScriptValue)var11_11, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var0.val("__return__", ScriptValue.NULL);
                return;
            }
            var12_12 = var1_1.getClassOrVar("Machine");
            var15_15 = var12_12 != ScriptValue.NULL ? (var12_12 instanceof ScriptValue.Obj && (var14_14 = (var13_13 = (ScriptValue.Obj)var12_12).instance()) != null && !(var14_14 instanceof PolyClass) && var13_13.typeName().equals("Machine") ? new PolyClassMachine_v3(var14_14).pg$155_rpm_network() : PolyDispatch.bootstrapGet("memberGet", "rpm_network", (ScriptValue)var12_12, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("net", var15_15);
            if (ScriptFormula.valuesEqualStr((ScriptValue)var11_11, (String)"y")) {
                var16_16 = new ArrayList<ScriptValue>();
                var17_17 = var1_1.getClassOrVar("Machine");
                if (var17_17 != ScriptValue.NULL) {
                    var18_18 = 1.0;
                    var20_19 = 0.0;
                    var22_20 = 0.0;
                    if (var17_17 instanceof ScriptValue.Obj && (var25_22 = (var24_21 = (ScriptValue.Obj)var17_17).instance()) != null && !(var25_22 instanceof PolyClass) && var24_21.typeName().equals("Machine")) {
                        var26_23 = new PolyClassMachine_v3(var25_22);
                        v2 /* !! */  = var26_23.tm$68_block_at(var18_18, var20_19, var22_20);
                    } else {
                        var27_24 = new ArrayList<ScriptValue>();
                        var27_24.add(ScriptValue.of((double)var18_18));
                        var27_24.add(ScriptValue.of((double)var20_19));
                        var27_24.add(ScriptValue.of((double)var22_20));
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var17_17, var27_24, (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var16_16.add(v2 /* !! */ );
                var28_25 = var1_1.getClassOrVar("Machine");
                if (var28_25 != ScriptValue.NULL) {
                    var29_26 = -1.0;
                    var31_27 = 0.0;
                    var33_28 = 0.0;
                    if (var28_25 instanceof ScriptValue.Obj && (var36_30 = (var35_29 = (ScriptValue.Obj)var28_25).instance()) != null && !(var36_30 instanceof PolyClass) && var35_29.typeName().equals("Machine")) {
                        var37_31 = new PolyClassMachine_v3(var36_30);
                        v3 /* !! */  = var37_31.tm$68_block_at(var29_26, var31_27, var33_28);
                    } else {
                        var38_32 = new ArrayList<ScriptValue>();
                        var38_32.add(ScriptValue.of((double)var29_26));
                        var38_32.add(ScriptValue.of((double)var31_27));
                        var38_32.add(ScriptValue.of((double)var33_28));
                        v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var28_25, var38_32, (ScriptContext)var1_1);
                    }
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                var16_16.add(v3 /* !! */ );
                var39_33 = var1_1.getClassOrVar("Machine");
                if (var39_33 != ScriptValue.NULL) {
                    var40_34 = 0.0;
                    var42_35 = 0.0;
                    var44_36 = 1.0;
                    if (var39_33 instanceof ScriptValue.Obj && (var47_38 = (var46_37 = (ScriptValue.Obj)var39_33).instance()) != null && !(var47_38 instanceof PolyClass) && var46_37.typeName().equals("Machine")) {
                        var48_39 = new PolyClassMachine_v3(var47_38);
                        v4 /* !! */  = var48_39.tm$68_block_at(var40_34, var42_35, var44_36);
                    } else {
                        var49_40 = new ArrayList<ScriptValue>();
                        var49_40.add(ScriptValue.of((double)var40_34));
                        var49_40.add(ScriptValue.of((double)var42_35));
                        var49_40.add(ScriptValue.of((double)var44_36));
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var39_33, var49_40, (ScriptContext)var1_1);
                    }
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
                var16_16.add(v4 /* !! */ );
                var50_41 = var1_1.getClassOrVar("Machine");
                if (var50_41 != ScriptValue.NULL) {
                    var51_42 = 0.0;
                    var53_43 = 0.0;
                    var55_44 = -1.0;
                    if (var50_41 instanceof ScriptValue.Obj && (var58_46 = (var57_45 = (ScriptValue.Obj)var50_41).instance()) != null && !(var58_46 instanceof PolyClass) && var57_45.typeName().equals("Machine")) {
                        var59_47 = new PolyClassMachine_v3(var58_46);
                        v5 /* !! */  = var59_47.tm$68_block_at(var51_42, var53_43, var55_44);
                    } else {
                        var60_48 = new ArrayList<ScriptValue>();
                        var60_48.add(ScriptValue.of((double)var51_42));
                        var60_48.add(ScriptValue.of((double)var53_43));
                        var60_48.add(ScriptValue.of((double)var55_44));
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var50_41, var60_48, (ScriptContext)var1_1);
                    }
                } else {
                    v5 /* !! */  = ScriptValue.NULL;
                }
                var16_16.add(v5 /* !! */ );
                var61_49 = new ScriptValue.Array(var16_16);
                var0.val("perp", (ScriptValue)var61_49);
            } else if (ScriptFormula.valuesEqualStr((ScriptValue)var11_11, (String)"x")) {
                var62_50 = new ArrayList<ScriptValue>();
                var63_51 = var1_1.getClassOrVar("Machine");
                if (var63_51 != ScriptValue.NULL) {
                    var64_52 = 0.0;
                    var66_53 = 1.0;
                    var68_54 = 0.0;
                    if (var63_51 instanceof ScriptValue.Obj && (var71_56 = (var70_55 = (ScriptValue.Obj)var63_51).instance()) != null && !(var71_56 instanceof PolyClass) && var70_55.typeName().equals("Machine")) {
                        var72_57 = new PolyClassMachine_v3(var71_56);
                        v6 /* !! */  = var72_57.tm$68_block_at(var64_52, var66_53, var68_54);
                    } else {
                        var73_58 = new ArrayList<ScriptValue>();
                        var73_58.add(ScriptValue.of((double)var64_52));
                        var73_58.add(ScriptValue.of((double)var66_53));
                        var73_58.add(ScriptValue.of((double)var68_54));
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var63_51, var73_58, (ScriptContext)var1_1);
                    }
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
                var62_50.add(v6 /* !! */ );
                var74_59 = var1_1.getClassOrVar("Machine");
                if (var74_59 != ScriptValue.NULL) {
                    var75_60 = 0.0;
                    var77_61 = -1.0;
                    var79_62 = 0.0;
                    if (var74_59 instanceof ScriptValue.Obj && (var82_64 = (var81_63 = (ScriptValue.Obj)var74_59).instance()) != null && !(var82_64 instanceof PolyClass) && var81_63.typeName().equals("Machine")) {
                        var83_65 = new PolyClassMachine_v3(var82_64);
                        v7 /* !! */  = var83_65.tm$68_block_at(var75_60, var77_61, var79_62);
                    } else {
                        var84_66 = new ArrayList<ScriptValue>();
                        var84_66.add(ScriptValue.of((double)var75_60));
                        var84_66.add(ScriptValue.of((double)var77_61));
                        var84_66.add(ScriptValue.of((double)var79_62));
                        v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var74_59, var84_66, (ScriptContext)var1_1);
                    }
                } else {
                    v7 /* !! */  = ScriptValue.NULL;
                }
                var62_50.add(v7 /* !! */ );
                var85_67 = var1_1.getClassOrVar("Machine");
                if (var85_67 != ScriptValue.NULL) {
                    var86_68 = 0.0;
                    var88_69 = 0.0;
                    var90_70 = 1.0;
                    if (var85_67 instanceof ScriptValue.Obj && (var93_72 = (var92_71 = (ScriptValue.Obj)var85_67).instance()) != null && !(var93_72 instanceof PolyClass) && var92_71.typeName().equals("Machine")) {
                        var94_73 = new PolyClassMachine_v3(var93_72);
                        v8 /* !! */  = var94_73.tm$68_block_at(var86_68, var88_69, var90_70);
                    } else {
                        var95_74 = new ArrayList<ScriptValue>();
                        var95_74.add(ScriptValue.of((double)var86_68));
                        var95_74.add(ScriptValue.of((double)var88_69));
                        var95_74.add(ScriptValue.of((double)var90_70));
                        v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var85_67, var95_74, (ScriptContext)var1_1);
                    }
                } else {
                    v8 /* !! */  = ScriptValue.NULL;
                }
                var62_50.add(v8 /* !! */ );
                var96_75 = var1_1.getClassOrVar("Machine");
                if (var96_75 != ScriptValue.NULL) {
                    var97_76 = 0.0;
                    var99_77 = 0.0;
                    var101_78 = -1.0;
                    if (var96_75 instanceof ScriptValue.Obj && (var104_80 = (var103_79 = (ScriptValue.Obj)var96_75).instance()) != null && !(var104_80 instanceof PolyClass) && var103_79.typeName().equals("Machine")) {
                        var105_81 = new PolyClassMachine_v3(var104_80);
                        v9 /* !! */  = var105_81.tm$68_block_at(var97_76, var99_77, var101_78);
                    } else {
                        var106_82 = new ArrayList<ScriptValue>();
                        var106_82.add(ScriptValue.of((double)var97_76));
                        var106_82.add(ScriptValue.of((double)var99_77));
                        var106_82.add(ScriptValue.of((double)var101_78));
                        v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var96_75, var106_82, (ScriptContext)var1_1);
                    }
                } else {
                    v9 /* !! */  = ScriptValue.NULL;
                }
                var62_50.add(v9 /* !! */ );
                var107_83 = new ScriptValue.Array(var62_50);
                var0.val("perp", (ScriptValue)var107_83);
            } else {
                var108_84 = new ArrayList<ScriptValue>();
                var109_85 = var1_1.getClassOrVar("Machine");
                if (var109_85 != ScriptValue.NULL) {
                    var110_86 = 1.0;
                    var112_87 = 0.0;
                    var114_88 = 0.0;
                    if (var109_85 instanceof ScriptValue.Obj && (var117_90 = (var116_89 = (ScriptValue.Obj)var109_85).instance()) != null && !(var117_90 instanceof PolyClass) && var116_89.typeName().equals("Machine")) {
                        var118_91 = new PolyClassMachine_v3(var117_90);
                        v10 /* !! */  = var118_91.tm$68_block_at(var110_86, var112_87, var114_88);
                    } else {
                        var119_92 = new ArrayList<ScriptValue>();
                        var119_92.add(ScriptValue.of((double)var110_86));
                        var119_92.add(ScriptValue.of((double)var112_87));
                        var119_92.add(ScriptValue.of((double)var114_88));
                        v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var109_85, var119_92, (ScriptContext)var1_1);
                    }
                } else {
                    v10 /* !! */  = ScriptValue.NULL;
                }
                var108_84.add(v10 /* !! */ );
                var120_93 = var1_1.getClassOrVar("Machine");
                if (var120_93 != ScriptValue.NULL) {
                    var121_94 = -1.0;
                    var123_95 = 0.0;
                    var125_96 = 0.0;
                    if (var120_93 instanceof ScriptValue.Obj && (var128_98 = (var127_97 = (ScriptValue.Obj)var120_93).instance()) != null && !(var128_98 instanceof PolyClass) && var127_97.typeName().equals("Machine")) {
                        var129_99 = new PolyClassMachine_v3(var128_98);
                        v11 /* !! */  = var129_99.tm$68_block_at(var121_94, var123_95, var125_96);
                    } else {
                        var130_100 = new ArrayList<ScriptValue>();
                        var130_100.add(ScriptValue.of((double)var121_94));
                        var130_100.add(ScriptValue.of((double)var123_95));
                        var130_100.add(ScriptValue.of((double)var125_96));
                        v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var120_93, var130_100, (ScriptContext)var1_1);
                    }
                } else {
                    v11 /* !! */  = ScriptValue.NULL;
                }
                var108_84.add(v11 /* !! */ );
                var131_101 = var1_1.getClassOrVar("Machine");
                if (var131_101 != ScriptValue.NULL) {
                    var132_102 = 0.0;
                    var134_103 = 1.0;
                    var136_104 = 0.0;
                    if (var131_101 instanceof ScriptValue.Obj && (var139_106 = (var138_105 = (ScriptValue.Obj)var131_101).instance()) != null && !(var139_106 instanceof PolyClass) && var138_105.typeName().equals("Machine")) {
                        var140_107 = new PolyClassMachine_v3(var139_106);
                        v12 /* !! */  = var140_107.tm$68_block_at(var132_102, var134_103, var136_104);
                    } else {
                        var141_108 = new ArrayList<ScriptValue>();
                        var141_108.add(ScriptValue.of((double)var132_102));
                        var141_108.add(ScriptValue.of((double)var134_103));
                        var141_108.add(ScriptValue.of((double)var136_104));
                        v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var131_101, var141_108, (ScriptContext)var1_1);
                    }
                } else {
                    v12 /* !! */  = ScriptValue.NULL;
                }
                var108_84.add(v12 /* !! */ );
                var142_109 = var1_1.getClassOrVar("Machine");
                if (var142_109 != ScriptValue.NULL) {
                    var143_110 = 0.0;
                    var145_111 = -1.0;
                    var147_112 = 0.0;
                    if (var142_109 instanceof ScriptValue.Obj && (var150_114 = (var149_113 = (ScriptValue.Obj)var142_109).instance()) != null && !(var150_114 instanceof PolyClass) && var149_113.typeName().equals("Machine")) {
                        var151_115 = new PolyClassMachine_v3(var150_114);
                        v13 /* !! */  = var151_115.tm$68_block_at(var143_110, var145_111, var147_112);
                    } else {
                        var152_116 = new ArrayList<ScriptValue>();
                        var152_116.add(ScriptValue.of((double)var143_110));
                        var152_116.add(ScriptValue.of((double)var145_111));
                        var152_116.add(ScriptValue.of((double)var147_112));
                        v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var142_109, var152_116, (ScriptContext)var1_1);
                    }
                } else {
                    v13 /* !! */  = ScriptValue.NULL;
                }
                var108_84.add(v13 /* !! */ );
                var153_117 = new ScriptValue.Array(var108_84);
                var0.val("perp", (ScriptValue)var153_117);
            }
            var154_118 = ScriptProgram.rowsOf((ScriptValue)var1_1.getClassOrVar("perp"), (int)1);
            if (var154_118 == null) break block64;
            for (ScriptValue[] var156_120 : var154_118) {
                var0.val("neighbor", var156_120.length > 0 ? var156_120[0] : ScriptValue.NULL);
                var157_121 = var1_1.getClassOrVar("neighbor");
                if (var157_121 != ScriptValue.NULL) {
                    var158_122 = new ArrayList<ScriptValue>();
                    var158_122.add(ScriptValue.of((String)"axis"));
                    v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var157_121, var158_122, (ScriptContext)var1_1);
                } else {
                    v14 /* !! */  = ScriptValue.NULL;
                }
                if (ScriptFormula.valuesEqual((ScriptValue)v14 /* !! */ , (ScriptValue)var11_11) ^ true) continue;
                var159_123 = var1_1.getClassOrVar("neighbor");
                var160_124 = var159_123 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var159_123, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("nid", var160_124);
                var161_125 = new ArrayList<ScriptValue>();
                var161_125.add(var160_124);
                var161_125.add(ScriptValue.of((String)"cogwheel_small"));
                if (!ScriptFormula.callBuiltin((String)"contains", var161_125, (ScriptContext)var1_1).asBool()) ** GOTO lbl-1000
                var162_126 = new ArrayList<ScriptValue>();
                var162_126.add(var160_124);
                var162_126.add(ScriptValue.of((String)"large"));
                if (ScriptFormula.callBuiltin((String)"contains", var162_126, (ScriptContext)var1_1).asBool() ^ true) {
                    v15 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v15 = false;
                }
                if (!v15) continue;
                var163_127 = var1_1.getClassOrVar("Machine");
                if (var163_127 != ScriptValue.NULL) {
                    var164_128 = new ArrayList<ScriptValue>();
                    var164_128.add(var1_1.getClassOrVar("neighbor"));
                    var164_128.add(ScriptValue.of((double)(-var1_1.getNum("rpm") * 2.0)));
                    var164_128.add(var15_15);
                    if (var163_127 instanceof ScriptValue.Obj && (var166_130 = (var165_129 = (ScriptValue.Obj)var163_127).instance()) != null && !(var166_130 instanceof PolyClass) && var165_129.typeName().equals("Machine")) {
                        v16 /* !! */  = new PolyClassMachine_v3(var166_130).um$29_relay_to(var164_128);
                        continue;
                    }
                    v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)var163_127, var164_128, (ScriptContext)var1_1);
                    continue;
                }
                v16 /* !! */  = ScriptValue.NULL;
            }
        }
        var167_131 = (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)ScriptValue.of((double)0.0)) ^ true) != false ? 1.0 : 0.0;
        var169_132 = ScriptValue.of((double)var167_131);
        var0.val("is_now", var169_132);
        var170_133 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var170_133.val("act_key", ScriptValue.of((String)"_cog_act"));
        var170_133.val("is_now", ScriptValue.of((double)var167_131));
        Utils.1._updateActivated((ScriptContext.Builder)var170_133);
        CogwheelLarge.FILE_SCOPE = var0.build();
    }
}
