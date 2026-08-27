/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
                            var7_7 = new PolyClassMachine_v2(var6_6);
                            v0 /* !! */  = var7_7.tm$34_get_typed(var3_3, var4_4);
                        } else {
                            v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var2_2, (ScriptValue)ScriptValue.of((String)var3_3), (ScriptValue)ScriptValue.of((String)var4_4), (ScriptContext)var1_1);
                        }
                    } else {
                        v0 /* !! */  = ScriptValue.NULL;
                    }
                    var8_8 = v0 /* !! */ ;
                    var0.val("cooldown", var8_8);
                    if (!(var8_8.asNum() > 0.0)) break block22;
                    var9_9 = var1_1.getClassOrVar("Machine");
                    if (var9_9 != ScriptValue.NULL) {
                        var10_10 = "atk_cd";
                        var11_11 = "int";
                        var12_12 = ScriptValue.of((double)(var8_8.asNum() - 1.0));
                        if (var9_9 instanceof ScriptValue.Obj && (var14_14 = (var13_13 = (ScriptValue.Obj)var9_9).instance()) != null && !(var14_14 instanceof PolyClass) && var13_13.typeName().equals("Machine")) {
                            var15_15 = new PolyClassMachine_v2(var14_14);
                            v1 /* !! */  = ScriptValue.of((boolean)var15_15.tm$82_set_typed(var10_10, var11_11, var12_12));
                        } else {
                            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var9_9, (ScriptValue)ScriptValue.of((String)var10_10), (ScriptValue)ScriptValue.of((String)var11_11), (ScriptValue)var12_12, (ScriptContext)var1_1);
                        }
                    } else {
                        v1 /* !! */  = ScriptValue.NULL;
                    }
                    break block23;
                }
                var17_16 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                var16_18 = var17_16 != null ? var17_16.pg$120_container() : ((var18_17 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var18_17, (ScriptContext)var1_1) : ScriptValue.NULL);
                var19_19 = 0.0;
                if (var16_18 instanceof ScriptValue.Obj && (var22_21 = (var21_20 = (ScriptValue.Obj)var16_18).instance()) != null && !(var22_21 instanceof PolyClass) && var21_20.typeName().equals("Container")) {
                    var23_22 = new PolyClassContainer(var22_21);
                    v2 = var23_22.tm$0_get_item(var19_19);
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)var16_18, (ScriptValue)ScriptValue.of((double)var19_19), (ScriptContext)var1_1);
                }
                var24_23 = v2;
                var0.val("weapon", (ScriptValue)var24_23);
                var25_24 = new ArrayList<CallSite>();
                var25_24.add(var24_23);
                var27_26 = ScriptFormula.callBuiltin((String)"is_empty", var25_24, (ScriptContext)var1_1).asBool() != false ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 2.0)) : ((var26_25 = var1_1.getClassOrVar("weapon")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "attack_damage", (ScriptValue)var26_25, (ScriptContext)var1_1) : ScriptValue.NULL);
                var0.val("base_dmg", var27_26);
                var28_27 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                var30_29 = var28_27 != null ? var28_27.pg$210_owner_uuid() : ((var29_28 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)var29_28, (ScriptContext)var1_1) : ScriptValue.NULL);
                var0.val("owner_id", var30_29);
                var31_30 = var1_1.getClassOrVar("Machine");
                if (var31_30 != ScriptValue.NULL) {
                    var32_31 = 0.8;
                    if (var31_30 instanceof ScriptValue.Obj && (var35_33 = (var34_32 = (ScriptValue.Obj)var31_30).instance()) != null && !(var35_33 instanceof PolyClass) && var34_32.typeName().equals("Machine")) {
                        var36_34 = new PolyClassMachine_v2(var35_33);
                        v3 /* !! */  = var36_34.tm$94_nearby_entities(var32_31);
                    } else {
                        v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var31_30, (ScriptValue)ScriptValue.of((double)var32_31), (ScriptContext)var1_1);
                    }
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                var37_35 = v3 /* !! */ ;
                var0.val("entities", var37_35);
                var38_36 = 0.0;
                var40_37 = ScriptValue.of((double)0.0);
                var0.val("hit_count", var40_37);
                var41_38 = ScriptProgram.elementsOf((ScriptValue)var37_35);
                if (var41_38 == null) break block24;
                for (ScriptValue var43_40 : var41_38) {
                    var0.val("entity", var43_40);
                    var44_41 = var1_1.getClassOrVar("entity");
                    if (!(var44_41 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var44_41, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
                    var45_42 = new ArrayList<ScriptValue>();
                    var45_42.add(var1_1.getClassOrVar("entity"));
                    var45_42.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Spike.class, "LivingEntity"));
                    if (ScriptFormula.callBuiltin((String)"instanceof", var45_42, (ScriptContext)var1_1).asBool()) {
                        v4 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v4 = false;
                    }
                    if (!v4) continue;
                    var47_44 = (ScriptFormula.valuesEqual((ScriptValue)var30_29, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)((var46_43 = var1_1.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var46_43, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var30_29) != false;
                    var48_45 = ScriptValue.of((boolean)var47_44);
                    var0.val("is_owner", var48_45);
                    if (!(var47_44 ^ true)) continue;
                    var49_46 = var1_1.getClassOrVar("entity");
                    v5 /* !! */  = var49_46 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)var49_46, (ScriptValue)var27_26, (ScriptContext)var1_1) : ScriptValue.NULL;
                    var50_47 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("hit_count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 1.0)));
                    var0.val("hit_count", var50_47);
                }
            }
            if (var1_1.getNum("hit_count") > 0.0) {
                var51_48 = var1_1.getClassOrVar("Machine");
                if (var51_48 != ScriptValue.NULL) {
                    var52_49 = "atk_cd";
                    var53_50 = "int";
                    var54_51 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 10.0);
                    if (var51_48 instanceof ScriptValue.Obj && (var56_53 = (var55_52 = (ScriptValue.Obj)var51_48).instance()) != null && !(var56_53 instanceof PolyClass) && var55_52.typeName().equals("Machine")) {
                        var57_54 = new PolyClassMachine_v2(var56_53);
                        v6 /* !! */  = ScriptValue.of((boolean)var57_54.tm$82_set_typed(var52_49, var53_50, var54_51));
                    } else {
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var51_48, (ScriptValue)ScriptValue.of((String)var52_49), (ScriptValue)ScriptValue.of((String)var53_50), (ScriptValue)var54_51, (ScriptContext)var1_1);
                    }
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
            }
        }
        Spike.FILE_SCOPE = var0.build();
    }
}
