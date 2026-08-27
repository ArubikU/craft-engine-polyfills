/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.Utils;
import java.util.ArrayList;

public final class CogwheelSmall {
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine.tm$106_set_rpm_output(d));
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$56_report_su(d));
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
        block67: {
            var1_1 = var0.peek();
            v0 = new ArrayList<String>();
            v0.add("_update_activated");
            ScriptProgram.applyImport((ScriptContext.Builder)var0, (String)"kinetics/utils.pf", null, v0);
            var2_2 = var1_1.getClassOrVar("Machine");
            if (var2_2 != ScriptValue.NULL) {
                var3_3 = var1_1.getClassOrVar("rpm");
                if (var2_2 instanceof ScriptValue.Obj && (var5_5 = (var4_4 = (ScriptValue.Obj)var2_2).instance()) != null && !(var5_5 instanceof PolyClass) && var4_4.typeName().equals("Machine")) {
                    var6_6 = new PolyClassMachine(var5_5);
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
            var10_10 = var8_8 != ScriptValue.NULL ? ((var9_9 = PolyClassMachine.ofGuarded((ScriptValue)var8_8)) != null ? var9_9.pg$123_axis() : PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)var8_8, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("axis", var10_10);
            if (ScriptFormula.valuesEqual((ScriptValue)var10_10, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var0.val("__return__", ScriptValue.NULL);
                return;
            }
            var11_11 = var1_1.getClassOrVar("Machine");
            var13_13 = var11_11 != ScriptValue.NULL ? ((var12_12 = PolyClassMachine.ofGuarded((ScriptValue)var11_11)) != null ? var12_12.pg$155_rpm_network() : PolyDispatch.bootstrapGet("memberGet", "rpm_network", (ScriptValue)var11_11, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("net", var13_13);
            if (ScriptFormula.valuesEqualStr((ScriptValue)var10_10, (String)"y")) {
                var14_14 = new ArrayList<ScriptValue>();
                var15_15 = var1_1.getClassOrVar("Machine");
                if (var15_15 != ScriptValue.NULL) {
                    var16_16 = 1.0;
                    var18_17 = 0.0;
                    var20_18 = 0.0;
                    if (var15_15 instanceof ScriptValue.Obj && (var23_20 = (var22_19 = (ScriptValue.Obj)var15_15).instance()) != null && !(var23_20 instanceof PolyClass) && var22_19.typeName().equals("Machine")) {
                        var24_21 = new PolyClassMachine(var23_20);
                        v2 /* !! */  = var24_21.tm$68_block_at(var16_16, var18_17, var20_18);
                    } else {
                        var25_22 = new ArrayList<ScriptValue>();
                        var25_22.add(ScriptValue.of((double)var16_16));
                        var25_22.add(ScriptValue.of((double)var18_17));
                        var25_22.add(ScriptValue.of((double)var20_18));
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var15_15, var25_22, (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var14_14.add(v2 /* !! */ );
                var26_23 = var1_1.getClassOrVar("Machine");
                if (var26_23 != ScriptValue.NULL) {
                    var27_24 = -1.0;
                    var29_25 = 0.0;
                    var31_26 = 0.0;
                    if (var26_23 instanceof ScriptValue.Obj && (var34_28 = (var33_27 = (ScriptValue.Obj)var26_23).instance()) != null && !(var34_28 instanceof PolyClass) && var33_27.typeName().equals("Machine")) {
                        var35_29 = new PolyClassMachine(var34_28);
                        v3 /* !! */  = var35_29.tm$68_block_at(var27_24, var29_25, var31_26);
                    } else {
                        var36_30 = new ArrayList<ScriptValue>();
                        var36_30.add(ScriptValue.of((double)var27_24));
                        var36_30.add(ScriptValue.of((double)var29_25));
                        var36_30.add(ScriptValue.of((double)var31_26));
                        v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var26_23, var36_30, (ScriptContext)var1_1);
                    }
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                var14_14.add(v3 /* !! */ );
                var37_31 = var1_1.getClassOrVar("Machine");
                if (var37_31 != ScriptValue.NULL) {
                    var38_32 = 0.0;
                    var40_33 = 0.0;
                    var42_34 = 1.0;
                    if (var37_31 instanceof ScriptValue.Obj && (var45_36 = (var44_35 = (ScriptValue.Obj)var37_31).instance()) != null && !(var45_36 instanceof PolyClass) && var44_35.typeName().equals("Machine")) {
                        var46_37 = new PolyClassMachine(var45_36);
                        v4 /* !! */  = var46_37.tm$68_block_at(var38_32, var40_33, var42_34);
                    } else {
                        var47_38 = new ArrayList<ScriptValue>();
                        var47_38.add(ScriptValue.of((double)var38_32));
                        var47_38.add(ScriptValue.of((double)var40_33));
                        var47_38.add(ScriptValue.of((double)var42_34));
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var37_31, var47_38, (ScriptContext)var1_1);
                    }
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
                var14_14.add(v4 /* !! */ );
                var48_39 = var1_1.getClassOrVar("Machine");
                if (var48_39 != ScriptValue.NULL) {
                    var49_40 = 0.0;
                    var51_41 = 0.0;
                    var53_42 = -1.0;
                    if (var48_39 instanceof ScriptValue.Obj && (var56_44 = (var55_43 = (ScriptValue.Obj)var48_39).instance()) != null && !(var56_44 instanceof PolyClass) && var55_43.typeName().equals("Machine")) {
                        var57_45 = new PolyClassMachine(var56_44);
                        v5 /* !! */  = var57_45.tm$68_block_at(var49_40, var51_41, var53_42);
                    } else {
                        var58_46 = new ArrayList<ScriptValue>();
                        var58_46.add(ScriptValue.of((double)var49_40));
                        var58_46.add(ScriptValue.of((double)var51_41));
                        var58_46.add(ScriptValue.of((double)var53_42));
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var48_39, var58_46, (ScriptContext)var1_1);
                    }
                } else {
                    v5 /* !! */  = ScriptValue.NULL;
                }
                var14_14.add(v5 /* !! */ );
                var59_47 = new ScriptValue.Array(var14_14);
                var0.val("perp", (ScriptValue)var59_47);
            } else if (ScriptFormula.valuesEqualStr((ScriptValue)var10_10, (String)"x")) {
                var60_48 = new ArrayList<ScriptValue>();
                var61_49 = var1_1.getClassOrVar("Machine");
                if (var61_49 != ScriptValue.NULL) {
                    var62_50 = 0.0;
                    var64_51 = 1.0;
                    var66_52 = 0.0;
                    if (var61_49 instanceof ScriptValue.Obj && (var69_54 = (var68_53 = (ScriptValue.Obj)var61_49).instance()) != null && !(var69_54 instanceof PolyClass) && var68_53.typeName().equals("Machine")) {
                        var70_55 = new PolyClassMachine(var69_54);
                        v6 /* !! */  = var70_55.tm$68_block_at(var62_50, var64_51, var66_52);
                    } else {
                        var71_56 = new ArrayList<ScriptValue>();
                        var71_56.add(ScriptValue.of((double)var62_50));
                        var71_56.add(ScriptValue.of((double)var64_51));
                        var71_56.add(ScriptValue.of((double)var66_52));
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var61_49, var71_56, (ScriptContext)var1_1);
                    }
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
                var60_48.add(v6 /* !! */ );
                var72_57 = var1_1.getClassOrVar("Machine");
                if (var72_57 != ScriptValue.NULL) {
                    var73_58 = 0.0;
                    var75_59 = -1.0;
                    var77_60 = 0.0;
                    if (var72_57 instanceof ScriptValue.Obj && (var80_62 = (var79_61 = (ScriptValue.Obj)var72_57).instance()) != null && !(var80_62 instanceof PolyClass) && var79_61.typeName().equals("Machine")) {
                        var81_63 = new PolyClassMachine(var80_62);
                        v7 /* !! */  = var81_63.tm$68_block_at(var73_58, var75_59, var77_60);
                    } else {
                        var82_64 = new ArrayList<ScriptValue>();
                        var82_64.add(ScriptValue.of((double)var73_58));
                        var82_64.add(ScriptValue.of((double)var75_59));
                        var82_64.add(ScriptValue.of((double)var77_60));
                        v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var72_57, var82_64, (ScriptContext)var1_1);
                    }
                } else {
                    v7 /* !! */  = ScriptValue.NULL;
                }
                var60_48.add(v7 /* !! */ );
                var83_65 = var1_1.getClassOrVar("Machine");
                if (var83_65 != ScriptValue.NULL) {
                    var84_66 = 0.0;
                    var86_67 = 0.0;
                    var88_68 = 1.0;
                    if (var83_65 instanceof ScriptValue.Obj && (var91_70 = (var90_69 = (ScriptValue.Obj)var83_65).instance()) != null && !(var91_70 instanceof PolyClass) && var90_69.typeName().equals("Machine")) {
                        var92_71 = new PolyClassMachine(var91_70);
                        v8 /* !! */  = var92_71.tm$68_block_at(var84_66, var86_67, var88_68);
                    } else {
                        var93_72 = new ArrayList<ScriptValue>();
                        var93_72.add(ScriptValue.of((double)var84_66));
                        var93_72.add(ScriptValue.of((double)var86_67));
                        var93_72.add(ScriptValue.of((double)var88_68));
                        v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var83_65, var93_72, (ScriptContext)var1_1);
                    }
                } else {
                    v8 /* !! */  = ScriptValue.NULL;
                }
                var60_48.add(v8 /* !! */ );
                var94_73 = var1_1.getClassOrVar("Machine");
                if (var94_73 != ScriptValue.NULL) {
                    var95_74 = 0.0;
                    var97_75 = 0.0;
                    var99_76 = -1.0;
                    if (var94_73 instanceof ScriptValue.Obj && (var102_78 = (var101_77 = (ScriptValue.Obj)var94_73).instance()) != null && !(var102_78 instanceof PolyClass) && var101_77.typeName().equals("Machine")) {
                        var103_79 = new PolyClassMachine(var102_78);
                        v9 /* !! */  = var103_79.tm$68_block_at(var95_74, var97_75, var99_76);
                    } else {
                        var104_80 = new ArrayList<ScriptValue>();
                        var104_80.add(ScriptValue.of((double)var95_74));
                        var104_80.add(ScriptValue.of((double)var97_75));
                        var104_80.add(ScriptValue.of((double)var99_76));
                        v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var94_73, var104_80, (ScriptContext)var1_1);
                    }
                } else {
                    v9 /* !! */  = ScriptValue.NULL;
                }
                var60_48.add(v9 /* !! */ );
                var105_81 = new ScriptValue.Array(var60_48);
                var0.val("perp", (ScriptValue)var105_81);
            } else {
                var106_82 = new ArrayList<ScriptValue>();
                var107_83 = var1_1.getClassOrVar("Machine");
                if (var107_83 != ScriptValue.NULL) {
                    var108_84 = 1.0;
                    var110_85 = 0.0;
                    var112_86 = 0.0;
                    if (var107_83 instanceof ScriptValue.Obj && (var115_88 = (var114_87 = (ScriptValue.Obj)var107_83).instance()) != null && !(var115_88 instanceof PolyClass) && var114_87.typeName().equals("Machine")) {
                        var116_89 = new PolyClassMachine(var115_88);
                        v10 /* !! */  = var116_89.tm$68_block_at(var108_84, var110_85, var112_86);
                    } else {
                        var117_90 = new ArrayList<ScriptValue>();
                        var117_90.add(ScriptValue.of((double)var108_84));
                        var117_90.add(ScriptValue.of((double)var110_85));
                        var117_90.add(ScriptValue.of((double)var112_86));
                        v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var107_83, var117_90, (ScriptContext)var1_1);
                    }
                } else {
                    v10 /* !! */  = ScriptValue.NULL;
                }
                var106_82.add(v10 /* !! */ );
                var118_91 = var1_1.getClassOrVar("Machine");
                if (var118_91 != ScriptValue.NULL) {
                    var119_92 = -1.0;
                    var121_93 = 0.0;
                    var123_94 = 0.0;
                    if (var118_91 instanceof ScriptValue.Obj && (var126_96 = (var125_95 = (ScriptValue.Obj)var118_91).instance()) != null && !(var126_96 instanceof PolyClass) && var125_95.typeName().equals("Machine")) {
                        var127_97 = new PolyClassMachine(var126_96);
                        v11 /* !! */  = var127_97.tm$68_block_at(var119_92, var121_93, var123_94);
                    } else {
                        var128_98 = new ArrayList<ScriptValue>();
                        var128_98.add(ScriptValue.of((double)var119_92));
                        var128_98.add(ScriptValue.of((double)var121_93));
                        var128_98.add(ScriptValue.of((double)var123_94));
                        v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var118_91, var128_98, (ScriptContext)var1_1);
                    }
                } else {
                    v11 /* !! */  = ScriptValue.NULL;
                }
                var106_82.add(v11 /* !! */ );
                var129_99 = var1_1.getClassOrVar("Machine");
                if (var129_99 != ScriptValue.NULL) {
                    var130_100 = 0.0;
                    var132_101 = 1.0;
                    var134_102 = 0.0;
                    if (var129_99 instanceof ScriptValue.Obj && (var137_104 = (var136_103 = (ScriptValue.Obj)var129_99).instance()) != null && !(var137_104 instanceof PolyClass) && var136_103.typeName().equals("Machine")) {
                        var138_105 = new PolyClassMachine(var137_104);
                        v12 /* !! */  = var138_105.tm$68_block_at(var130_100, var132_101, var134_102);
                    } else {
                        var139_106 = new ArrayList<ScriptValue>();
                        var139_106.add(ScriptValue.of((double)var130_100));
                        var139_106.add(ScriptValue.of((double)var132_101));
                        var139_106.add(ScriptValue.of((double)var134_102));
                        v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var129_99, var139_106, (ScriptContext)var1_1);
                    }
                } else {
                    v12 /* !! */  = ScriptValue.NULL;
                }
                var106_82.add(v12 /* !! */ );
                var140_107 = var1_1.getClassOrVar("Machine");
                if (var140_107 != ScriptValue.NULL) {
                    var141_108 = 0.0;
                    var143_109 = -1.0;
                    var145_110 = 0.0;
                    if (var140_107 instanceof ScriptValue.Obj && (var148_112 = (var147_111 = (ScriptValue.Obj)var140_107).instance()) != null && !(var148_112 instanceof PolyClass) && var147_111.typeName().equals("Machine")) {
                        var149_113 = new PolyClassMachine(var148_112);
                        v13 /* !! */  = var149_113.tm$68_block_at(var141_108, var143_109, var145_110);
                    } else {
                        var150_114 = new ArrayList<ScriptValue>();
                        var150_114.add(ScriptValue.of((double)var141_108));
                        var150_114.add(ScriptValue.of((double)var143_109));
                        var150_114.add(ScriptValue.of((double)var145_110));
                        v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var140_107, var150_114, (ScriptContext)var1_1);
                    }
                } else {
                    v13 /* !! */  = ScriptValue.NULL;
                }
                var106_82.add(v13 /* !! */ );
                var151_115 = new ScriptValue.Array(var106_82);
                var0.val("perp", (ScriptValue)var151_115);
            }
            var152_116 = ScriptProgram.rowsOf((ScriptValue)var1_1.getClassOrVar("perp"), (int)1);
            if (var152_116 == null) break block67;
            for (ScriptValue[] var154_118 : var152_116) {
                var0.val("neighbor", var154_118.length > 0 ? var154_118[0] : ScriptValue.NULL);
                var155_119 = var1_1.getClassOrVar("neighbor");
                if (var155_119 != ScriptValue.NULL) {
                    var156_120 = new ArrayList<ScriptValue>();
                    var156_120.add(ScriptValue.of((String)"axis"));
                    v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var155_119, var156_120, (ScriptContext)var1_1);
                } else {
                    v14 /* !! */  = ScriptValue.NULL;
                }
                if (ScriptFormula.valuesEqual((ScriptValue)v14 /* !! */ , (ScriptValue)var10_10) ^ true) continue;
                var157_121 = var1_1.getClassOrVar("neighbor");
                var158_122 = var157_121 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var157_121, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("nid", var158_122);
                var159_123 = new ArrayList<ScriptValue>();
                var159_123.add(var158_122);
                var159_123.add(ScriptValue.of((String)"cogwheel_small"));
                if (!ScriptFormula.callBuiltin((String)"contains", var159_123, (ScriptContext)var1_1).asBool()) ** GOTO lbl-1000
                var160_124 = new ArrayList<ScriptValue>();
                var160_124.add(var158_122);
                var160_124.add(ScriptValue.of((String)"large"));
                if (ScriptFormula.callBuiltin((String)"contains", var160_124, (ScriptContext)var1_1).asBool() ^ true) {
                    v15 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v15 = false;
                }
                if (v15) {
                    var161_125 = var1_1.getClassOrVar("Machine");
                    if (var161_125 != ScriptValue.NULL) {
                        var162_126 = new ArrayList<ScriptValue>();
                        var162_126.add(var1_1.getClassOrVar("neighbor"));
                        var162_126.add(ScriptValue.of((double)(-var1_1.getNum("rpm"))));
                        var162_126.add(var13_13);
                        v16 /* !! */  = var161_125 instanceof ScriptValue.Obj && (var164_128 = (var163_127 = (ScriptValue.Obj)var161_125).instance()) != null && !(var164_128 instanceof PolyClass) && var163_127.typeName().equals("Machine") ? new PolyClassMachine(var164_128).um$29_relay_to(var162_126) : PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)var161_125, var162_126, (ScriptContext)var1_1);
                    } else {
                        v16 /* !! */  = ScriptValue.NULL;
                    }
                }
                var165_129 = new ArrayList<ScriptValue>();
                var165_129.add(var158_122);
                var165_129.add(ScriptValue.of((String)"cogwheel_large"));
                if (!ScriptFormula.callBuiltin((String)"contains", var165_129, (ScriptContext)var1_1).asBool()) continue;
                var166_130 = var1_1.getClassOrVar("Machine");
                if (var166_130 != ScriptValue.NULL) {
                    var167_131 = new ArrayList<ScriptValue>();
                    var167_131.add(var1_1.getClassOrVar("neighbor"));
                    var167_131.add(ScriptValue.of((double)(-var1_1.getNum("rpm") * 0.5)));
                    var167_131.add(var13_13);
                    if (var166_130 instanceof ScriptValue.Obj && (var169_133 = (var168_132 = (ScriptValue.Obj)var166_130).instance()) != null && !(var169_133 instanceof PolyClass) && var168_132.typeName().equals("Machine")) {
                        v17 /* !! */  = new PolyClassMachine(var169_133).um$29_relay_to(var167_131);
                        continue;
                    }
                    v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)var166_130, var167_131, (ScriptContext)var1_1);
                    continue;
                }
                v17 /* !! */  = ScriptValue.NULL;
            }
        }
        var170_134 = (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)ScriptValue.of((double)0.0)) ^ true) != false ? 1.0 : 0.0;
        var172_135 = ScriptValue.of((double)var170_134);
        var0.val("is_now", var172_135);
        var173_136 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var173_136.val("act_key", ScriptValue.of((String)"_cog_act"));
        var173_136.val("is_now", ScriptValue.of((double)var170_134));
        Utils.1._updateActivated((ScriptContext.Builder)var173_136);
        CogwheelSmall.FILE_SCOPE = var0.build();
    }
}
