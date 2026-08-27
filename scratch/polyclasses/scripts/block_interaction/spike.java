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
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class Spike {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block23: {
            block24: {
                block22: {
                    var1_1 = var0.peek();
                    var2_2 = var1_1.getClassOrVar("Machine");
                    if (var2_2 != ScriptValue.NULL) {
                        var3_3 = "atk_cd";
                        var4_4 = "int";
                        if (var2_2 instanceof ScriptValue.Obj && (var6_6 = (var5_5 = (ScriptValue.Obj)var2_2).instance()) != null && !(var6_6 instanceof PolyClass) && var5_5.typeName().equals("Machine")) {
                            var7_7 = new PolyClassMachine(var6_6);
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
                    if (!(var9_9.asNum() > 0.0)) break block22;
                    var10_10 = var1_1.getClassOrVar("Machine");
                    if (var10_10 != ScriptValue.NULL) {
                        var11_11 = "atk_cd";
                        var12_12 = "int";
                        var13_13 = ScriptValue.of((double)(var9_9.asNum() - 1.0));
                        if (var10_10 instanceof ScriptValue.Obj && (var15_15 = (var14_14 = (ScriptValue.Obj)var10_10).instance()) != null && !(var15_15 instanceof PolyClass) && var14_14.typeName().equals("Machine")) {
                            var16_16 = new PolyClassMachine(var15_15);
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
                    break block23;
                }
                var18_18 = new ArrayList<ScriptValue>();
                var18_18.add(ScriptValue.of((double)0.0));
                var19_19 = var1_1.getClassOrVar("Machine");
                var21_21 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(var19_19 != ScriptValue.NULL ? ((var20_20 = PolyClassMachine.ofGuarded((ScriptValue)var19_19)) != null ? var20_20.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var19_19, (ScriptContext)var1_1)) : ScriptValue.NULL), var18_18, (ScriptContext)var1_1);
                var0.val("weapon", (ScriptValue)var21_21);
                var22_22 = new ArrayList<CallSite>();
                var22_22.add(var21_21);
                var24_24 = ScriptFormula.callBuiltin((String)"is_empty", var22_22, (ScriptContext)var1_1).asBool() != false ? ScriptValue.of((double)2.0) : ((var23_23 = var1_1.getClassOrVar("weapon")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "attack_damage", (ScriptValue)var23_23, (ScriptContext)var1_1) : ScriptValue.NULL);
                var0.val("base_dmg", var24_24);
                var25_25 = var1_1.getClassOrVar("Machine");
                var27_27 = var25_25 != ScriptValue.NULL ? ((var26_26 = PolyClassMachine.ofGuarded((ScriptValue)var25_25)) != null ? var26_26.pg$209_owner_uuid() : PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)var25_25, (ScriptContext)var1_1)) : ScriptValue.NULL;
                var0.val("owner_id", var27_27);
                var28_28 = var1_1.getClassOrVar("Machine");
                if (var28_28 != ScriptValue.NULL) {
                    var29_29 = 0.8;
                    if (var28_28 instanceof ScriptValue.Obj && (var32_31 = (var31_30 = (ScriptValue.Obj)var28_28).instance()) != null && !(var32_31 instanceof PolyClass) && var31_30.typeName().equals("Machine")) {
                        var33_32 = new PolyClassMachine(var32_31);
                        v2 /* !! */  = var33_32.tm$94_nearby_entities(var29_29);
                    } else {
                        var34_33 = new ArrayList<ScriptValue>();
                        var34_33.add(ScriptValue.of((double)var29_29));
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var28_28, var34_33, (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var35_34 = v2 /* !! */ ;
                var0.val("entities", var35_34);
                var36_35 = 0.0;
                var38_36 = ScriptValue.of((double)0.0);
                var0.val("hit_count", var38_36);
                var39_37 = ScriptProgram.elementsOf((ScriptValue)var35_34);
                if (var39_37 == null) break block24;
                for (ScriptValue var41_39 : var39_37) {
                    var0.val("entity", var41_39);
                    var42_40 = var1_1.getClassOrVar("entity");
                    if (!(var42_40 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var42_40, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
                    var43_41 = new ArrayList<ScriptValue>();
                    var43_41.add(var1_1.getClassOrVar("entity"));
                    var43_41.add(ScriptValue.of((String)"LivingEntity"));
                    if (ScriptFormula.callBuiltin((String)"instanceof", var43_41, (ScriptContext)var1_1).asBool()) {
                        v3 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v3 = false;
                    }
                    if (!v3) continue;
                    var45_43 = (ScriptFormula.valuesEqual((ScriptValue)var27_27, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)((var44_42 = var1_1.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var44_42, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var27_27) != false;
                    var46_44 = ScriptValue.of((boolean)var45_43);
                    var0.val("is_owner", var46_44);
                    if (!(var45_43 ^ true)) continue;
                    var47_45 = var1_1.getClassOrVar("entity");
                    if (var47_45 != ScriptValue.NULL) {
                        var48_46 = new ArrayList<ScriptValue>();
                        var48_46.add(var24_24);
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)var47_45, var48_46, (ScriptContext)var1_1);
                    } else {
                        v4 /* !! */  = ScriptValue.NULL;
                    }
                    var49_47 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("hit_count"), (ScriptValue)ScriptValue.of((double)1.0));
                    var0.val("hit_count", var49_47);
                }
            }
            if (var1_1.getNum("hit_count") > 0.0) {
                var50_48 = var1_1.getClassOrVar("Machine");
                if (var50_48 != ScriptValue.NULL) {
                    var51_49 = "atk_cd";
                    var52_50 = "int";
                    var53_51 = ScriptValue.of((double)10.0);
                    if (var50_48 instanceof ScriptValue.Obj && (var55_53 = (var54_52 = (ScriptValue.Obj)var50_48).instance()) != null && !(var55_53 instanceof PolyClass) && var54_52.typeName().equals("Machine")) {
                        var56_54 = new PolyClassMachine(var55_53);
                        v5 /* !! */  = ScriptValue.of((boolean)var56_54.tm$82_set_typed(var51_49, var52_50, var53_51));
                    } else {
                        var57_55 = new ArrayList<ScriptValue>();
                        var57_55.add(ScriptValue.of((String)var51_49));
                        var57_55.add(ScriptValue.of((String)var52_50));
                        var57_55.add(var53_51);
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var50_48, var57_55, (ScriptContext)var1_1);
                    }
                } else {
                    v5 /* !! */  = ScriptValue.NULL;
                }
            }
        }
        Spike.FILE_SCOPE = var0.build();
    }
}
