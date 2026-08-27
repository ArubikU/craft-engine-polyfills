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
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.block_interaction;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
        block21: {
            block22: {
                block20: {
                    var1_1 = var0.peek();
                    var2_2 = var1_1.getClassOrVar("Machine");
                    if (var2_2 != ScriptValue.NULL) {
                        var3_3 = "atk_cd";
                        var4_4 = "int";
                        if (var2_2 instanceof ScriptValue.Obj && (var6_6 = (var5_5 = (ScriptValue.Obj)var2_2).instance()) != null && !(var6_6 instanceof PolyClass) && var5_5.typeName().equals("Machine")) {
                            var7_7 = new PolyClassMachine_v3(var6_6);
                            v0 /* !! */  = var7_7.tm$34_get_typed(var3_3, var4_4);
                        } else {
                            v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var2_2, (ScriptValue)ScriptValue.of((String)var3_3), (ScriptValue)ScriptValue.of((String)var4_4), (ScriptContext)var1_1);
                        }
                    } else {
                        v0 /* !! */  = ScriptValue.NULL;
                    }
                    var8_8 = v0 /* !! */ ;
                    var0.val("cooldown", var8_8);
                    if (!(var8_8.asNum() > 0.0)) break block20;
                    var9_9 = var1_1.getClassOrVar("Machine");
                    if (var9_9 != ScriptValue.NULL) {
                        var10_10 = "atk_cd";
                        var11_11 = "int";
                        var12_12 = ScriptValue.of((double)(var8_8.asNum() - 1.0));
                        if (var9_9 instanceof ScriptValue.Obj && (var14_14 = (var13_13 = (ScriptValue.Obj)var9_9).instance()) != null && !(var14_14 instanceof PolyClass) && var13_13.typeName().equals("Machine")) {
                            var15_15 = new PolyClassMachine_v3(var14_14);
                            v1 /* !! */  = ScriptValue.of((boolean)var15_15.tm$82_set_typed(var10_10, var11_11, var12_12));
                        } else {
                            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var9_9, (ScriptValue)ScriptValue.of((String)var10_10), (ScriptValue)ScriptValue.of((String)var11_11), (ScriptValue)var12_12, (ScriptContext)var1_1);
                        }
                    } else {
                        v1 /* !! */  = ScriptValue.NULL;
                    }
                    break block21;
                }
                var16_16 = var1_1.getClassOrVar("Machine");
                var18_18 = var16_16 != ScriptValue.NULL ? ((var17_17 = PolyClassMachine_v3.ofGuarded((ScriptValue)var16_16)) != null ? var17_17.pg$209_owner_uuid() : PolyDispatch.bootstrapGet("memberGet", "owner_uuid", (ScriptValue)var16_16, (ScriptContext)var1_1)) : ScriptValue.NULL;
                var0.val("owner_id", var18_18);
                var19_19 = var1_1.getClassOrVar("Machine");
                if (var19_19 != ScriptValue.NULL) {
                    var20_20 = 0.8;
                    if (var19_19 instanceof ScriptValue.Obj && (var23_22 = (var22_21 = (ScriptValue.Obj)var19_19).instance()) != null && !(var23_22 instanceof PolyClass) && var22_21.typeName().equals("Machine")) {
                        var24_23 = new PolyClassMachine_v3(var23_22);
                        v2 /* !! */  = var24_23.tm$94_nearby_entities(var20_20);
                    } else {
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var19_19, (ScriptValue)ScriptValue.of((double)var20_20), (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var25_24 = v2 /* !! */ ;
                var0.val("entities", var25_24);
                var26_25 = 0.0;
                var28_26 = ScriptValue.of((double)0.0);
                var0.val("hit_count", var28_26);
                var29_27 = ScriptProgram.elementsOf((ScriptValue)var25_24);
                if (var29_27 == null) break block22;
                for (ScriptValue var31_29 : var29_27) {
                    var0.val("entity", var31_29);
                    var32_30 = var1_1.getClassOrVar("entity");
                    if (!(var32_30 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_alive", (ScriptValue)var32_30, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) ** GOTO lbl-1000
                    var33_31 = new ArrayList<ScriptValue>();
                    var33_31.add(var1_1.getClassOrVar("entity"));
                    var33_31.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", LavaSpike.class, "LivingEntity"));
                    if (ScriptFormula.callBuiltin((String)"instanceof", var33_31, (ScriptContext)var1_1).asBool()) {
                        v3 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v3 = false;
                    }
                    if (!v3) continue;
                    var35_33 = (ScriptFormula.valuesEqual((ScriptValue)var18_18, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && ScriptFormula.valuesEqual((ScriptValue)((var34_32 = var1_1.getClassOrVar("entity")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var34_32, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var18_18) != false;
                    var36_34 = ScriptValue.of((boolean)var35_33);
                    var0.val("is_owner", var36_34);
                    if (!(var35_33 ^ true)) continue;
                    var37_35 = var1_1.getClassOrVar("entity");
                    v4 /* !! */  = var37_35 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "fire", (ScriptValue)var37_35, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 80.0)), (ScriptContext)var1_1) : ScriptValue.NULL;
                    var38_36 = var1_1.getClassOrVar("entity");
                    v5 /* !! */  = var38_36 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "damage", (ScriptValue)var38_36, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 2.0)), (ScriptContext)var1_1) : ScriptValue.NULL;
                    var39_37 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("hit_count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 1.0)));
                    var0.val("hit_count", var39_37);
                }
            }
            if (var1_1.getNum("hit_count") > 0.0) {
                var40_38 = var1_1.getClassOrVar("Machine");
                if (var40_38 != ScriptValue.NULL) {
                    var41_39 = "atk_cd";
                    var42_40 = "int";
                    var43_41 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", LavaSpike.class, 15.0);
                    if (var40_38 instanceof ScriptValue.Obj && (var45_43 = (var44_42 = (ScriptValue.Obj)var40_38).instance()) != null && !(var45_43 instanceof PolyClass) && var44_42.typeName().equals("Machine")) {
                        var46_44 = new PolyClassMachine_v3(var45_43);
                        v6 /* !! */  = ScriptValue.of((boolean)var46_44.tm$82_set_typed(var41_39, var42_40, var43_41));
                    } else {
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var40_38, (ScriptValue)ScriptValue.of((String)var41_39), (ScriptValue)ScriptValue.of((String)var42_40), (ScriptValue)var43_41, (ScriptContext)var1_1);
                    }
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
            }
        }
        LavaSpike.FILE_SCOPE = var0.build();
    }
}
