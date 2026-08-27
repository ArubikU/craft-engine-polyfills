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
import java.util.ArrayList;

public final class LavaSpike {
    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block24: {
            block25: {
                block23: {
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
                    if (!(var9_9.asNum() > 0.0)) break block23;
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
                    break block24;
                }
                var18_18 = var1_1.getClassOrVar("Machine");
                var21_21 = var18_18 != ScriptValue.NULL ? (var18_18 instanceof ScriptValue.Obj && (var20_20 = (var19_19 = (ScriptValue.Obj)var18_18).instance()) != null && !(var20_20 instanceof PolyClass) && var19_19.typeName().equals("Machine") ? new PolyClassMachine_v4(var20_20).pg$171_owner_uuid() : PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)var18_18, (ScriptContext)var1_1)) : ScriptValue.NULL;
                var0.val("owner_id", var21_21);
                var22_22 = var1_1.getClassOrVar("Machine");
                if (var22_22 != ScriptValue.NULL) {
                    var23_23 = 0.8;
                    if (var22_22 instanceof ScriptValue.Obj && (var26_25 = (var25_24 = (ScriptValue.Obj)var22_22).instance()) != null && !(var26_25 instanceof PolyClass) && var25_24.typeName().equals("Machine")) {
                        var27_26 = new PolyClassMachine_v4(var26_25);
                        v2 /* !! */  = var27_26.tm$94_nearby_entities(var23_23);
                    } else {
                        var28_27 = new ArrayList<ScriptValue>();
                        var28_27.add(ScriptValue.of((double)var23_23));
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var22_22, var28_27, (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var29_28 = v2 /* !! */ ;
                var0.val("entities", var29_28);
                var30_29 = 0.0;
                var32_30 = ScriptValue.of((double)0.0);
                var0.val("hit_count", var32_30);
                var33_31 = ScriptProgram.rowsOf((ScriptValue)var29_28, (int)1);
                if (var33_31 == null) break block25;
                for (ScriptValue[] var35_33 : var33_31) {
                    var0.val("entity", var35_33.length > 0 ? var35_33[0] : ScriptValue.NULL);
                    var36_34 = var1_1.getClassOrVar("entity");
                    if (!(var36_34 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var36_34, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
                    var37_35 = new ArrayList<ScriptValue>();
                    var37_35.add(var1_1.getClassOrVar("entity"));
                    var37_35.add(ScriptValue.of((String)"LivingEntity"));
                    if (ScriptFormula.callBuiltin((String)"instanceof", var37_35, (ScriptContext)var1_1).asBool()) {
                        v3 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v3 = false;
                    }
                    if (!v3) continue;
                    var39_37 = (ScriptFormula.valuesEqual((ScriptValue)var21_21, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)((var38_36 = var1_1.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var38_36, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var21_21) != false;
                    var40_38 = ScriptValue.of((boolean)var39_37);
                    var0.val("is_owner", var40_38);
                    if (!(var39_37 ^ true)) continue;
                    var41_39 = var1_1.getClassOrVar("entity");
                    if (var41_39 != ScriptValue.NULL) {
                        var42_40 = new ArrayList<ScriptValue>();
                        var42_40.add(ScriptValue.of((double)80.0));
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "fire", (ScriptValue)var41_39, var42_40, (ScriptContext)var1_1);
                    } else {
                        v4 /* !! */  = ScriptValue.NULL;
                    }
                    var43_41 = var1_1.getClassOrVar("entity");
                    if (var43_41 != ScriptValue.NULL) {
                        var44_42 = new ArrayList<ScriptValue>();
                        var44_42.add(ScriptValue.of((double)2.0));
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)var43_41, var44_42, (ScriptContext)var1_1);
                    } else {
                        v5 /* !! */  = ScriptValue.NULL;
                    }
                    var45_43 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("hit_count"), (ScriptValue)ScriptValue.of((double)1.0));
                    var0.val("hit_count", var45_43);
                }
            }
            if (!(var1_1.getNum("hit_count") > 0.0)) break block24;
            var46_44 = var1_1.getClassOrVar("Machine");
            if (var46_44 != ScriptValue.NULL) {
                var47_45 = "atk_cd";
                var48_46 = "int";
                var49_47 = ScriptValue.of((double)15.0);
                if (var46_44 instanceof ScriptValue.Obj && (var51_49 = (var50_48 = (ScriptValue.Obj)var46_44).instance()) != null && !(var51_49 instanceof PolyClass) && var50_48.typeName().equals("Machine")) {
                    var52_50 = new PolyClassMachine_v4(var51_49);
                    v6 /* !! */  = ScriptValue.of((boolean)var52_50.tm$82_set_typed(var47_45, var48_46, var49_47));
                } else {
                    var53_51 = new ArrayList<ScriptValue>();
                    var53_51.add(ScriptValue.of((String)var47_45));
                    var53_51.add(ScriptValue.of((String)var48_46));
                    var53_51.add(var49_47);
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var46_44, var53_51, (ScriptContext)var1_1);
                }
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
        }
    }
}
