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
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class LavaSpike {
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
        block25: {
            block26: {
                block24: {
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
                    if (!(var9_9.asNum() > 0.0)) break block24;
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
                    break block25;
                }
                var18_18 = var1_1.getClassOrVar("Machine");
                var20_20 = var18_18 != ScriptValue.NULL ? ((var19_19 = PolyClassMachine_v4.ofGuarded((ScriptValue)var18_18)) != null ? var19_19.pg$209_owner_uuid() : PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)var18_18, (ScriptContext)var1_1)) : ScriptValue.NULL;
                var0.val("owner_id", var20_20);
                var21_21 = var1_1.getClassOrVar("Machine");
                if (var21_21 != ScriptValue.NULL) {
                    var22_22 = 0.8;
                    if (var21_21 instanceof ScriptValue.Obj && (var25_24 = (var24_23 = (ScriptValue.Obj)var21_21).instance()) != null && !(var25_24 instanceof PolyClass) && var24_23.typeName().equals("Machine")) {
                        var26_25 = new PolyClassMachine_v4(var25_24);
                        v2 /* !! */  = var26_25.tm$94_nearby_entities(var22_22);
                    } else {
                        var27_26 = new ArrayList<ScriptValue>();
                        var27_26.add(ScriptValue.of((double)var22_22));
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var21_21, var27_26, (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var28_27 = v2 /* !! */ ;
                var0.val("entities", var28_27);
                var29_28 = 0.0;
                var31_29 = ScriptValue.of((double)0.0);
                var0.val("hit_count", var31_29);
                var32_30 = ScriptProgram.elementsOf((ScriptValue)var28_27);
                if (var32_30 == null) break block26;
                for (ScriptValue var34_32 : var32_30) {
                    var0.val("entity", var34_32);
                    var35_33 = var1_1.getClassOrVar("entity");
                    if (!(var35_33 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var35_33, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
                    var36_34 = new ArrayList<ScriptValue>();
                    var36_34.add(var1_1.getClassOrVar("entity"));
                    var36_34.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", LavaSpike.class, "LivingEntity"));
                    if (ScriptFormula.callBuiltin((String)"instanceof", var36_34, (ScriptContext)var1_1).asBool()) {
                        v3 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v3 = false;
                    }
                    if (!v3) continue;
                    var38_36 = (ScriptFormula.valuesEqual((ScriptValue)var20_20, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)((var37_35 = var1_1.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var37_35, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var20_20) != false;
                    var39_37 = ScriptValue.of((boolean)var38_36);
                    var0.val("is_owner", var39_37);
                    if (!(var38_36 ^ true)) continue;
                    var40_38 = var1_1.getClassOrVar("entity");
                    if (var40_38 != ScriptValue.NULL) {
                        var41_39 = new ArrayList<ScriptValue>();
                        var41_39.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 80.0));
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "fire", (ScriptValue)var40_38, var41_39, (ScriptContext)var1_1);
                    } else {
                        v4 /* !! */  = ScriptValue.NULL;
                    }
                    var42_40 = var1_1.getClassOrVar("entity");
                    if (var42_40 != ScriptValue.NULL) {
                        var43_41 = new ArrayList<ScriptValue>();
                        var43_41.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 2.0));
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)var42_40, var43_41, (ScriptContext)var1_1);
                    } else {
                        v5 /* !! */  = ScriptValue.NULL;
                    }
                    var44_42 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("hit_count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 1.0)));
                    var0.val("hit_count", var44_42);
                }
            }
            if (var1_1.getNum("hit_count") > 0.0) {
                var45_43 = var1_1.getClassOrVar("Machine");
                if (var45_43 != ScriptValue.NULL) {
                    var46_44 = "atk_cd";
                    var47_45 = "int";
                    var48_46 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 15.0);
                    if (var45_43 instanceof ScriptValue.Obj && (var50_48 = (var49_47 = (ScriptValue.Obj)var45_43).instance()) != null && !(var50_48 instanceof PolyClass) && var49_47.typeName().equals("Machine")) {
                        var51_49 = new PolyClassMachine_v4(var50_48);
                        v6 /* !! */  = ScriptValue.of((boolean)var51_49.tm$82_set_typed(var46_44, var47_45, var48_46));
                    } else {
                        var52_50 = new ArrayList<ScriptValue>();
                        var52_50.add(ScriptValue.of((String)var46_44));
                        var52_50.add(ScriptValue.of((String)var47_45));
                        var52_50.add(var48_46);
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var45_43, var52_50, (ScriptContext)var1_1);
                    }
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
            }
        }
        LavaSpike.FILE_SCOPE = var0.build();
    }
}
