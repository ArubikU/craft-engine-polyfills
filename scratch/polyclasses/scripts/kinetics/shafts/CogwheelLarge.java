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
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
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
                v1 = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
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
        block62: {
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
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output", (ScriptValue)var2_2, (ScriptValue)var3_3, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            var7_7 = var1_1.getClassOrVar("Machine");
            var9_9 = var7_7 != ScriptValue.NULL ? ((var8_8 = PolyClassMachine_v3.ofGuarded((ScriptValue)var7_7)) != null ? var8_8.pg$127_axis() : PolyDispatch.bootstrapGet("memberGet", "axis", (ScriptValue)var7_7, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("axis", var9_9);
            if (ScriptFormula.valuesEqual((ScriptValue)var9_9, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var0.val("__return__", ScriptValue.NULL);
                return;
            }
            var10_10 = var1_1.getClassOrVar("Machine");
            var12_12 = var10_10 != ScriptValue.NULL ? ((var11_11 = PolyClassMachine_v3.ofGuarded((ScriptValue)var10_10)) != null ? var11_11.pg$182_rpm_network() : PolyDispatch.bootstrapGet("memberGet", "rpm_network", (ScriptValue)var10_10, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("net", var12_12);
            if (ScriptFormula.valuesEqualStr((ScriptValue)var9_9, (String)"y")) {
                var13_13 = new ArrayList<ScriptValue>();
                var14_14 = var1_1.getClassOrVar("Machine");
                if (var14_14 != ScriptValue.NULL) {
                    var15_15 = 1.0;
                    var17_16 = 0.0;
                    var19_17 = 0.0;
                    if (var14_14 instanceof ScriptValue.Obj && (var22_19 = (var21_18 = (ScriptValue.Obj)var14_14).instance()) != null && !(var22_19 instanceof PolyClass) && var21_18.typeName().equals("Machine")) {
                        var23_20 = new PolyClassMachine_v3(var22_19);
                        v2 /* !! */  = var23_20.tm$68_block_at(var15_15, var17_16, var19_17);
                    } else {
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var14_14, (ScriptValue)ScriptValue.of((double)var15_15), (ScriptValue)ScriptValue.of((double)var17_16), (ScriptValue)ScriptValue.of((double)var19_17), (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var13_13.add(v2 /* !! */ );
                var24_21 = var1_1.getClassOrVar("Machine");
                if (var24_21 != ScriptValue.NULL) {
                    var25_22 = -1.0;
                    var27_23 = 0.0;
                    var29_24 = 0.0;
                    if (var24_21 instanceof ScriptValue.Obj && (var32_26 = (var31_25 = (ScriptValue.Obj)var24_21).instance()) != null && !(var32_26 instanceof PolyClass) && var31_25.typeName().equals("Machine")) {
                        var33_27 = new PolyClassMachine_v3(var32_26);
                        v3 /* !! */  = var33_27.tm$68_block_at(var25_22, var27_23, var29_24);
                    } else {
                        v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var24_21, (ScriptValue)ScriptValue.of((double)var25_22), (ScriptValue)ScriptValue.of((double)var27_23), (ScriptValue)ScriptValue.of((double)var29_24), (ScriptContext)var1_1);
                    }
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                var13_13.add(v3 /* !! */ );
                var34_28 = var1_1.getClassOrVar("Machine");
                if (var34_28 != ScriptValue.NULL) {
                    var35_29 = 0.0;
                    var37_30 = 0.0;
                    var39_31 = 1.0;
                    if (var34_28 instanceof ScriptValue.Obj && (var42_33 = (var41_32 = (ScriptValue.Obj)var34_28).instance()) != null && !(var42_33 instanceof PolyClass) && var41_32.typeName().equals("Machine")) {
                        var43_34 = new PolyClassMachine_v3(var42_33);
                        v4 /* !! */  = var43_34.tm$68_block_at(var35_29, var37_30, var39_31);
                    } else {
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var34_28, (ScriptValue)ScriptValue.of((double)var35_29), (ScriptValue)ScriptValue.of((double)var37_30), (ScriptValue)ScriptValue.of((double)var39_31), (ScriptContext)var1_1);
                    }
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
                var13_13.add(v4 /* !! */ );
                var44_35 = var1_1.getClassOrVar("Machine");
                if (var44_35 != ScriptValue.NULL) {
                    var45_36 = 0.0;
                    var47_37 = 0.0;
                    var49_38 = -1.0;
                    if (var44_35 instanceof ScriptValue.Obj && (var52_40 = (var51_39 = (ScriptValue.Obj)var44_35).instance()) != null && !(var52_40 instanceof PolyClass) && var51_39.typeName().equals("Machine")) {
                        var53_41 = new PolyClassMachine_v3(var52_40);
                        v5 /* !! */  = var53_41.tm$68_block_at(var45_36, var47_37, var49_38);
                    } else {
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var44_35, (ScriptValue)ScriptValue.of((double)var45_36), (ScriptValue)ScriptValue.of((double)var47_37), (ScriptValue)ScriptValue.of((double)var49_38), (ScriptContext)var1_1);
                    }
                } else {
                    v5 /* !! */  = ScriptValue.NULL;
                }
                var13_13.add(v5 /* !! */ );
                var54_42 = new ScriptValue.Array(var13_13);
                var0.val("perp", (ScriptValue)var54_42);
            } else if (ScriptFormula.valuesEqualStr((ScriptValue)var9_9, (String)"x")) {
                var55_43 = new ArrayList<ScriptValue>();
                var56_44 = var1_1.getClassOrVar("Machine");
                if (var56_44 != ScriptValue.NULL) {
                    var57_45 = 0.0;
                    var59_46 = 1.0;
                    var61_47 = 0.0;
                    if (var56_44 instanceof ScriptValue.Obj && (var64_49 = (var63_48 = (ScriptValue.Obj)var56_44).instance()) != null && !(var64_49 instanceof PolyClass) && var63_48.typeName().equals("Machine")) {
                        var65_50 = new PolyClassMachine_v3(var64_49);
                        v6 /* !! */  = var65_50.tm$68_block_at(var57_45, var59_46, var61_47);
                    } else {
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var56_44, (ScriptValue)ScriptValue.of((double)var57_45), (ScriptValue)ScriptValue.of((double)var59_46), (ScriptValue)ScriptValue.of((double)var61_47), (ScriptContext)var1_1);
                    }
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
                var55_43.add(v6 /* !! */ );
                var66_51 = var1_1.getClassOrVar("Machine");
                if (var66_51 != ScriptValue.NULL) {
                    var67_52 = 0.0;
                    var69_53 = -1.0;
                    var71_54 = 0.0;
                    if (var66_51 instanceof ScriptValue.Obj && (var74_56 = (var73_55 = (ScriptValue.Obj)var66_51).instance()) != null && !(var74_56 instanceof PolyClass) && var73_55.typeName().equals("Machine")) {
                        var75_57 = new PolyClassMachine_v3(var74_56);
                        v7 /* !! */  = var75_57.tm$68_block_at(var67_52, var69_53, var71_54);
                    } else {
                        v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var66_51, (ScriptValue)ScriptValue.of((double)var67_52), (ScriptValue)ScriptValue.of((double)var69_53), (ScriptValue)ScriptValue.of((double)var71_54), (ScriptContext)var1_1);
                    }
                } else {
                    v7 /* !! */  = ScriptValue.NULL;
                }
                var55_43.add(v7 /* !! */ );
                var76_58 = var1_1.getClassOrVar("Machine");
                if (var76_58 != ScriptValue.NULL) {
                    var77_59 = 0.0;
                    var79_60 = 0.0;
                    var81_61 = 1.0;
                    if (var76_58 instanceof ScriptValue.Obj && (var84_63 = (var83_62 = (ScriptValue.Obj)var76_58).instance()) != null && !(var84_63 instanceof PolyClass) && var83_62.typeName().equals("Machine")) {
                        var85_64 = new PolyClassMachine_v3(var84_63);
                        v8 /* !! */  = var85_64.tm$68_block_at(var77_59, var79_60, var81_61);
                    } else {
                        v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var76_58, (ScriptValue)ScriptValue.of((double)var77_59), (ScriptValue)ScriptValue.of((double)var79_60), (ScriptValue)ScriptValue.of((double)var81_61), (ScriptContext)var1_1);
                    }
                } else {
                    v8 /* !! */  = ScriptValue.NULL;
                }
                var55_43.add(v8 /* !! */ );
                var86_65 = var1_1.getClassOrVar("Machine");
                if (var86_65 != ScriptValue.NULL) {
                    var87_66 = 0.0;
                    var89_67 = 0.0;
                    var91_68 = -1.0;
                    if (var86_65 instanceof ScriptValue.Obj && (var94_70 = (var93_69 = (ScriptValue.Obj)var86_65).instance()) != null && !(var94_70 instanceof PolyClass) && var93_69.typeName().equals("Machine")) {
                        var95_71 = new PolyClassMachine_v3(var94_70);
                        v9 /* !! */  = var95_71.tm$68_block_at(var87_66, var89_67, var91_68);
                    } else {
                        v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var86_65, (ScriptValue)ScriptValue.of((double)var87_66), (ScriptValue)ScriptValue.of((double)var89_67), (ScriptValue)ScriptValue.of((double)var91_68), (ScriptContext)var1_1);
                    }
                } else {
                    v9 /* !! */  = ScriptValue.NULL;
                }
                var55_43.add(v9 /* !! */ );
                var96_72 = new ScriptValue.Array(var55_43);
                var0.val("perp", (ScriptValue)var96_72);
            } else {
                var97_73 = new ArrayList<ScriptValue>();
                var98_74 = var1_1.getClassOrVar("Machine");
                if (var98_74 != ScriptValue.NULL) {
                    var99_75 = 1.0;
                    var101_76 = 0.0;
                    var103_77 = 0.0;
                    if (var98_74 instanceof ScriptValue.Obj && (var106_79 = (var105_78 = (ScriptValue.Obj)var98_74).instance()) != null && !(var106_79 instanceof PolyClass) && var105_78.typeName().equals("Machine")) {
                        var107_80 = new PolyClassMachine_v3(var106_79);
                        v10 /* !! */  = var107_80.tm$68_block_at(var99_75, var101_76, var103_77);
                    } else {
                        v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var98_74, (ScriptValue)ScriptValue.of((double)var99_75), (ScriptValue)ScriptValue.of((double)var101_76), (ScriptValue)ScriptValue.of((double)var103_77), (ScriptContext)var1_1);
                    }
                } else {
                    v10 /* !! */  = ScriptValue.NULL;
                }
                var97_73.add(v10 /* !! */ );
                var108_81 = var1_1.getClassOrVar("Machine");
                if (var108_81 != ScriptValue.NULL) {
                    var109_82 = -1.0;
                    var111_83 = 0.0;
                    var113_84 = 0.0;
                    if (var108_81 instanceof ScriptValue.Obj && (var116_86 = (var115_85 = (ScriptValue.Obj)var108_81).instance()) != null && !(var116_86 instanceof PolyClass) && var115_85.typeName().equals("Machine")) {
                        var117_87 = new PolyClassMachine_v3(var116_86);
                        v11 /* !! */  = var117_87.tm$68_block_at(var109_82, var111_83, var113_84);
                    } else {
                        v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var108_81, (ScriptValue)ScriptValue.of((double)var109_82), (ScriptValue)ScriptValue.of((double)var111_83), (ScriptValue)ScriptValue.of((double)var113_84), (ScriptContext)var1_1);
                    }
                } else {
                    v11 /* !! */  = ScriptValue.NULL;
                }
                var97_73.add(v11 /* !! */ );
                var118_88 = var1_1.getClassOrVar("Machine");
                if (var118_88 != ScriptValue.NULL) {
                    var119_89 = 0.0;
                    var121_90 = 1.0;
                    var123_91 = 0.0;
                    if (var118_88 instanceof ScriptValue.Obj && (var126_93 = (var125_92 = (ScriptValue.Obj)var118_88).instance()) != null && !(var126_93 instanceof PolyClass) && var125_92.typeName().equals("Machine")) {
                        var127_94 = new PolyClassMachine_v3(var126_93);
                        v12 /* !! */  = var127_94.tm$68_block_at(var119_89, var121_90, var123_91);
                    } else {
                        v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var118_88, (ScriptValue)ScriptValue.of((double)var119_89), (ScriptValue)ScriptValue.of((double)var121_90), (ScriptValue)ScriptValue.of((double)var123_91), (ScriptContext)var1_1);
                    }
                } else {
                    v12 /* !! */  = ScriptValue.NULL;
                }
                var97_73.add(v12 /* !! */ );
                var128_95 = var1_1.getClassOrVar("Machine");
                if (var128_95 != ScriptValue.NULL) {
                    var129_96 = 0.0;
                    var131_97 = -1.0;
                    var133_98 = 0.0;
                    if (var128_95 instanceof ScriptValue.Obj && (var136_100 = (var135_99 = (ScriptValue.Obj)var128_95).instance()) != null && !(var136_100 instanceof PolyClass) && var135_99.typeName().equals("Machine")) {
                        var137_101 = new PolyClassMachine_v3(var136_100);
                        v13 /* !! */  = var137_101.tm$68_block_at(var129_96, var131_97, var133_98);
                    } else {
                        v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)var128_95, (ScriptValue)ScriptValue.of((double)var129_96), (ScriptValue)ScriptValue.of((double)var131_97), (ScriptValue)ScriptValue.of((double)var133_98), (ScriptContext)var1_1);
                    }
                } else {
                    v13 /* !! */  = ScriptValue.NULL;
                }
                var97_73.add(v13 /* !! */ );
                var138_102 = new ScriptValue.Array(var97_73);
                var0.val("perp", (ScriptValue)var138_102);
            }
            var139_103 = ScriptProgram.elementsOf((ScriptValue)var1_1.getClassOrVar("perp"));
            if (var139_103 == null) break block62;
            for (ScriptValue var141_105 : var139_103) {
                var0.val("neighbor", var141_105);
                var142_106 = var1_1.getClassOrVar("neighbor");
                if (ScriptFormula.valuesEqual((ScriptValue)(var142_106 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var142_106, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelLarge.class, "axis")), (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var9_9) ^ true) continue;
                var143_107 = var1_1.getClassOrVar("neighbor");
                var144_108 = var143_107 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var143_107, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("nid", var144_108);
                var145_109 = new ArrayList<ScriptValue>();
                var145_109.add(var144_108);
                var145_109.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelLarge.class, "cogwheel_small"));
                if (!ScriptFormula.callBuiltin((String)"contains", var145_109, (ScriptContext)var1_1).asBool()) ** GOTO lbl-1000
                var146_110 = new ArrayList<ScriptValue>();
                var146_110.add(var144_108);
                var146_110.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelLarge.class, "large"));
                if (ScriptFormula.callBuiltin((String)"contains", var146_110, (ScriptContext)var1_1).asBool() ^ true) {
                    v14 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v14 = false;
                }
                if (!v14) continue;
                var147_111 = var1_1.getClassOrVar("Machine");
                if (var147_111 != ScriptValue.NULL) {
                    var148_112 = new ArrayList<ScriptValue>();
                    var148_112.add(var1_1.getClassOrVar("neighbor"));
                    var148_112.add(ScriptValue.of((double)(-var1_1.getNum("rpm") * 2.0)));
                    var148_112.add(var12_12);
                    if (var147_111 instanceof ScriptValue.Obj && (var150_114 = (var149_113 = (ScriptValue.Obj)var147_111).instance()) != null && !(var150_114 instanceof PolyClass) && var149_113.typeName().equals("Machine")) {
                        v15 /* !! */  = new PolyClassMachine_v3(var150_114).um$29_relay_to(var148_112);
                        continue;
                    }
                    v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "relay_to", (ScriptValue)var147_111, var148_112, (ScriptContext)var1_1);
                    continue;
                }
                v15 /* !! */  = ScriptValue.NULL;
            }
        }
        var151_115 = (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", CogwheelLarge.class, 0.0))) ^ true) != false ? 1.0 : 0.0;
        var153_116 = ScriptValue.of((double)var151_115);
        var0.val("is_now", var153_116);
        var154_117 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var154_117.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", CogwheelLarge.class, "_cog_act"));
        var154_117.val("is_now", ScriptValue.of((double)var151_115));
        Utils.1._updateActivated((ScriptContext.Builder)var154_117);
        CogwheelLarge.FILE_SCOPE = var0.build();
    }
}
