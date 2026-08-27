/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
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
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
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
                var19_18 = var1_1.getClassOrVar("Machine");
                var18_20 = var19_18 != ScriptValue.NULL ? ((var20_19 = PolyClassMachine_v4.ofGuarded((ScriptValue)var19_18)) != null ? var20_19.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var19_18, (ScriptContext)var1_1)) : ScriptValue.NULL;
                var21_21 = 0.0;
                if (var18_20 instanceof ScriptValue.Obj && (var24_23 = (var23_22 = (ScriptValue.Obj)var18_20).instance()) != null && !(var24_23 instanceof PolyClass) && var23_22.typeName().equals("Container")) {
                    var25_24 = new PolyClassContainer(var24_23);
                    v2 = var25_24.tm$0_get_item(var21_21);
                } else {
                    var26_25 = new ArrayList<ScriptValue>();
                    var26_25.add(ScriptValue.of((double)var21_21));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)var18_20, var26_25, (ScriptContext)var1_1);
                }
                var27_26 = v2;
                var0.val("weapon", (ScriptValue)var27_26);
                var28_27 = new ArrayList<CallSite>();
                var28_27.add(var27_26);
                var30_29 = ScriptFormula.callBuiltin((String)"is_empty", var28_27, (ScriptContext)var1_1).asBool() != false ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 2.0)) : ((var29_28 = var1_1.getClassOrVar("weapon")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "attack_damage", (ScriptValue)var29_28, (ScriptContext)var1_1) : ScriptValue.NULL);
                var0.val("base_dmg", var30_29);
                var31_30 = var1_1.getClassOrVar("Machine");
                var33_32 = var31_30 != ScriptValue.NULL ? ((var32_31 = PolyClassMachine_v4.ofGuarded((ScriptValue)var31_30)) != null ? var32_31.pg$209_owner_uuid() : PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)var31_30, (ScriptContext)var1_1)) : ScriptValue.NULL;
                var0.val("owner_id", var33_32);
                var34_33 = var1_1.getClassOrVar("Machine");
                if (var34_33 != ScriptValue.NULL) {
                    var35_34 = 0.8;
                    if (var34_33 instanceof ScriptValue.Obj && (var38_36 = (var37_35 = (ScriptValue.Obj)var34_33).instance()) != null && !(var38_36 instanceof PolyClass) && var37_35.typeName().equals("Machine")) {
                        var39_37 = new PolyClassMachine_v4(var38_36);
                        v3 /* !! */  = var39_37.tm$94_nearby_entities(var35_34);
                    } else {
                        var40_38 = new ArrayList<ScriptValue>();
                        var40_38.add(ScriptValue.of((double)var35_34));
                        v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var34_33, var40_38, (ScriptContext)var1_1);
                    }
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                var41_39 = v3 /* !! */ ;
                var0.val("entities", var41_39);
                var42_40 = 0.0;
                var44_41 = ScriptValue.of((double)0.0);
                var0.val("hit_count", var44_41);
                var45_42 = ScriptProgram.elementsOf((ScriptValue)var41_39);
                if (var45_42 == null) break block26;
                for (ScriptValue var47_44 : var45_42) {
                    var0.val("entity", var47_44);
                    var48_45 = var1_1.getClassOrVar("entity");
                    if (!(var48_45 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var48_45, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
                    var49_46 = new ArrayList<ScriptValue>();
                    var49_46.add(var1_1.getClassOrVar("entity"));
                    var49_46.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Spike.class, "LivingEntity"));
                    if (ScriptFormula.callBuiltin((String)"instanceof", var49_46, (ScriptContext)var1_1).asBool()) {
                        v4 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v4 = false;
                    }
                    if (!v4) continue;
                    var51_48 = (ScriptFormula.valuesEqual((ScriptValue)var33_32, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)((var50_47 = var1_1.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var50_47, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var33_32) != false;
                    var52_49 = ScriptValue.of((boolean)var51_48);
                    var0.val("is_owner", var52_49);
                    if (!(var51_48 ^ true)) continue;
                    var53_50 = var1_1.getClassOrVar("entity");
                    if (var53_50 != ScriptValue.NULL) {
                        var54_51 = new ArrayList<ScriptValue>();
                        var54_51.add(var30_29);
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)var53_50, var54_51, (ScriptContext)var1_1);
                    } else {
                        v5 /* !! */  = ScriptValue.NULL;
                    }
                    var55_52 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("hit_count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 1.0)));
                    var0.val("hit_count", var55_52);
                }
            }
            if (var1_1.getNum("hit_count") > 0.0) {
                var56_53 = var1_1.getClassOrVar("Machine");
                if (var56_53 != ScriptValue.NULL) {
                    var57_54 = "atk_cd";
                    var58_55 = "int";
                    var59_56 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Spike.class, 10.0);
                    if (var56_53 instanceof ScriptValue.Obj && (var61_58 = (var60_57 = (ScriptValue.Obj)var56_53).instance()) != null && !(var61_58 instanceof PolyClass) && var60_57.typeName().equals("Machine")) {
                        var62_59 = new PolyClassMachine_v4(var61_58);
                        v6 /* !! */  = ScriptValue.of((boolean)var62_59.tm$82_set_typed(var57_54, var58_55, var59_56));
                    } else {
                        var63_60 = new ArrayList<ScriptValue>();
                        var63_60.add(ScriptValue.of((String)var57_54));
                        var63_60.add(ScriptValue.of((String)var58_55));
                        var63_60.add(var59_56);
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var56_53, var63_60, (ScriptContext)var1_1);
                    }
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
            }
        }
        Spike.FILE_SCOPE = var0.build();
    }
}
