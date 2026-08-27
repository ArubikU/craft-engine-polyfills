/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class Spike {
    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block22: {
            block23: {
                block21: {
                    var1_1 = var0.peek();
                    var2_2 = var1_1.getClassOrVar("Machine");
                    if (var2_2 != ScriptValue.NULL) {
                        var3_3 = "atk_cd";
                        var4_4 = "int";
                        if (var2_2 instanceof ScriptValue.Obj && (var6_6 = (var5_5 = (ScriptValue.Obj)var2_2).instance()) != null && !(var6_6 instanceof PolyClass) && var5_5.typeName().equals("Machine")) {
                            var7_7 = new PolyClassMachine_v4(var6_6);
                            v0 /* !! */  = var7_7.tm$34_get_typed(var3_3, var4_4);
                        } else {
                            var8_8 = new ArrayList<ScriptValue>();
                            var8_8.add(ScriptValue.of((String)var3_3));
                            var8_8.add(ScriptValue.of((String)var4_4));
                            v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var2_2, var8_8, (ScriptContext)var1_1);
                        }
                    } else {
                        v0 /* !! */  = ScriptValue.NULL;
                    }
                    var9_9 = v0 /* !! */ ;
                    var0.val("cooldown", var9_9);
                    if (!(var9_9.asNum() > 0.0)) break block21;
                    var10_10 = var1_1.getClassOrVar("Machine");
                    if (var10_10 != ScriptValue.NULL) {
                        var11_11 = "atk_cd";
                        var12_12 = "int";
                        var13_13 = ScriptValue.of((double)(var9_9.asNum() - 1.0));
                        if (var10_10 instanceof ScriptValue.Obj && (var15_15 = (var14_14 = (ScriptValue.Obj)var10_10).instance()) != null && !(var15_15 instanceof PolyClass) && var14_14.typeName().equals("Machine")) {
                            var16_16 = new PolyClassMachine_v4(var15_15);
                            v1 /* !! */  = ScriptValue.of((boolean)var16_16.tm$82_set_typed(var11_11, var12_12, var13_13));
                        } else {
                            var17_17 = new ArrayList<ScriptValue>();
                            var17_17.add(ScriptValue.of((String)var11_11));
                            var17_17.add(ScriptValue.of((String)var12_12));
                            var17_17.add(var13_13);
                            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var10_10, var17_17, (ScriptContext)var1_1);
                        }
                    } else {
                        v1 /* !! */  = ScriptValue.NULL;
                    }
                    break block22;
                }
                var18_18 = new ArrayList<ScriptValue>();
                var18_18.add(ScriptValue.of((double)0.0));
                var19_19 = var1_1.getClassOrVar("Machine");
                var22_22 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(var19_19 != ScriptValue.NULL ? (var19_19 instanceof ScriptValue.Obj && (var21_21 = (var20_20 = (ScriptValue.Obj)var19_19).instance()) != null && !(var21_21 instanceof PolyClass) && var20_20.typeName().equals("Machine") ? new PolyClassMachine_v4(var21_21).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var19_19, (ScriptContext)var1_1)) : ScriptValue.NULL), var18_18, (ScriptContext)var1_1);
                var0.val("weapon", (ScriptValue)var22_22);
                var23_23 = new ArrayList<CallSite>();
                var23_23.add(var22_22);
                var25_25 = ScriptFormula.callBuiltin((String)"is_empty", var23_23, (ScriptContext)var1_1).asBool() != false ? ScriptValue.of((double)2.0) : ((var24_24 = var1_1.getClassOrVar("weapon")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "attack_damage", (ScriptValue)var24_24, (ScriptContext)var1_1) : ScriptValue.NULL);
                var0.val("base_dmg", var25_25);
                var26_26 = var1_1.getClassOrVar("Machine");
                var29_29 = var26_26 != ScriptValue.NULL ? (var26_26 instanceof ScriptValue.Obj && (var28_28 = (var27_27 = (ScriptValue.Obj)var26_26).instance()) != null && !(var28_28 instanceof PolyClass) && var27_27.typeName().equals("Machine") ? new PolyClassMachine_v4(var28_28).pg$171_owner_uuid() : PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)var26_26, (ScriptContext)var1_1)) : ScriptValue.NULL;
                var0.val("owner_id", var29_29);
                var30_30 = var1_1.getClassOrVar("Machine");
                if (var30_30 != ScriptValue.NULL) {
                    var31_31 = 0.8;
                    if (var30_30 instanceof ScriptValue.Obj && (var34_33 = (var33_32 = (ScriptValue.Obj)var30_30).instance()) != null && !(var34_33 instanceof PolyClass) && var33_32.typeName().equals("Machine")) {
                        var35_34 = new PolyClassMachine_v4(var34_33);
                        v2 /* !! */  = var35_34.tm$94_nearby_entities(var31_31);
                    } else {
                        var36_35 = new ArrayList<ScriptValue>();
                        var36_35.add(ScriptValue.of((double)var31_31));
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var30_30, var36_35, (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var37_36 = v2 /* !! */ ;
                var0.val("entities", var37_36);
                var38_37 = 0.0;
                var40_38 = ScriptValue.of((double)0.0);
                var0.val("hit_count", var40_38);
                var41_39 = ScriptProgram.rowsOf((ScriptValue)var37_36, (int)1);
                if (var41_39 == null) break block23;
                for (ScriptValue[] var43_41 : var41_39) {
                    var0.val("entity", var43_41.length > 0 ? var43_41[0] : ScriptValue.NULL);
                    var44_42 = var1_1.getClassOrVar("entity");
                    if (!(var44_42 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var44_42, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
                    var45_43 = new ArrayList<ScriptValue>();
                    var45_43.add(var1_1.getClassOrVar("entity"));
                    var45_43.add(ScriptValue.of((String)"LivingEntity"));
                    if (ScriptFormula.callBuiltin((String)"instanceof", var45_43, (ScriptContext)var1_1).asBool()) {
                        v3 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v3 = false;
                    }
                    if (!v3) continue;
                    var47_45 = (ScriptFormula.valuesEqual((ScriptValue)var29_29, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)((var46_44 = var1_1.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var46_44, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var29_29) != false;
                    var48_46 = ScriptValue.of((boolean)var47_45);
                    var0.val("is_owner", var48_46);
                    if (!(var47_45 ^ true)) continue;
                    var49_47 = var1_1.getClassOrVar("entity");
                    if (var49_47 != ScriptValue.NULL) {
                        var50_48 = new ArrayList<ScriptValue>();
                        var50_48.add(var25_25);
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)var49_47, var50_48, (ScriptContext)var1_1);
                    } else {
                        v4 /* !! */  = ScriptValue.NULL;
                    }
                    var51_49 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("hit_count"), (ScriptValue)ScriptValue.of((double)1.0));
                    var0.val("hit_count", var51_49);
                }
            }
            if (!(var1_1.getNum("hit_count") > 0.0)) break block22;
            var52_50 = var1_1.getClassOrVar("Machine");
            if (var52_50 != ScriptValue.NULL) {
                var53_51 = "atk_cd";
                var54_52 = "int";
                var55_53 = ScriptValue.of((double)10.0);
                if (var52_50 instanceof ScriptValue.Obj && (var57_55 = (var56_54 = (ScriptValue.Obj)var52_50).instance()) != null && !(var57_55 instanceof PolyClass) && var56_54.typeName().equals("Machine")) {
                    var58_56 = new PolyClassMachine_v4(var57_55);
                    v5 /* !! */  = ScriptValue.of((boolean)var58_56.tm$82_set_typed(var53_51, var54_52, var55_53));
                } else {
                    var59_57 = new ArrayList<ScriptValue>();
                    var59_57.add(ScriptValue.of((String)var53_51));
                    var59_57.add(ScriptValue.of((String)var54_52));
                    var59_57.add(var55_53);
                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var52_50, var59_57, (ScriptContext)var1_1);
                }
            } else {
                v5 /* !! */  = ScriptValue.NULL;
            }
        }
    }
}
