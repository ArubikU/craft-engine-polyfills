/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class Fertilizer {
    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block18: {
            var1_1 = var0.peek();
            var2_2 = var1_1.getClassOrVar("Machine");
            if (var2_2 != ScriptValue.NULL) {
                var3_3 = "level";
                var4_4 = "int";
                if (var2_2 instanceof ScriptValue.Obj && (var6_6 = (var5_5 = (ScriptValue.Obj)var2_2).instance()) != null && !(var6_6 instanceof PolyClass) && var5_5.typeName().equals("Machine")) {
                    var7_7 = new PolyClassMachine_v2(var6_6);
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
            var0.val("level_flag", var9_9);
            if (var9_9.asNum() <= 0.0) {
                var10_10 = 1.0;
                var12_11 = ScriptValue.of((double)1.0);
                var0.val("level_flag", var12_11);
            }
            var13_12 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("level_flag"), (ScriptValue)ScriptValue.of((double)2.0));
            var0.val("radius", var13_12);
            var14_13 = new ArrayList<CallSite>();
            var15_14 = new ArrayList<ScriptValue>();
            var15_14.add(ScriptValue.of((double)0.0));
            var16_15 = var1_1.getClassOrVar("Machine");
            var14_13.add(PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(var16_15 != ScriptValue.NULL ? (var16_15 instanceof ScriptValue.Obj && (var18_17 = (var17_16 = (ScriptValue.Obj)var16_15).instance()) != null && !(var18_17 instanceof PolyClass) && var17_16.typeName().equals("Machine") ? new PolyClassMachine_v2(var18_17).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var16_15, (ScriptContext)var1_1)) : ScriptValue.NULL), var15_14, (ScriptContext)var1_1));
            if (!(ScriptFormula.callBuiltin((String)"is_empty", var14_13, (ScriptContext)var1_1).asBool() ^ true)) break block18;
            var19_18 = var1_1.getClassOrVar("Machine");
            if (var19_18 != ScriptValue.NULL) {
                var20_19 = var13_12;
                if (var19_18 instanceof ScriptValue.Obj && (var22_21 = (var21_20 = (ScriptValue.Obj)var19_18).instance()) != null && !(var22_21 instanceof PolyClass) && var21_20.typeName().equals("Machine")) {
                    var23_22 = new PolyClassMachine_v2(var22_21);
                    v1 /* !! */  = var23_22.tm$86_blocks_in_range(var20_19.asNum());
                } else {
                    var24_23 = new ArrayList<ScriptValue>();
                    var24_23.add(var20_19);
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "blocks_in_range", (ScriptValue)var19_18, var24_23, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            var25_24 = v1 /* !! */ ;
            var0.val("blocks", var25_24);
            var26_25 = ScriptProgram.resolveForRows((String)"blocks", (ScriptContext)var1_1, (int)1);
            if (var26_25 == null) break block18;
            for (ScriptValue[] var28_27 : var26_25) {
                var0.val("block", var28_27.length > 0 ? var28_27[0] : ScriptValue.NULL);
                var29_28 = var1_1.getClassOrVar("block");
                if (var29_28 != ScriptValue.NULL) {
                    var30_29 = new ArrayList<ScriptValue>();
                    var30_29.add(ScriptValue.of((String)"age"));
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)var29_28, var30_29, (ScriptContext)var1_1);
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                if (v2 /* !! */ .asBool()) ** GOTO lbl-1000
                var31_30 = var1_1.getClassOrVar("block");
                if (var31_30 != ScriptValue.NULL) {
                    var32_31 = new ArrayList<ScriptValue>();
                    var32_31.add(ScriptValue.of((String)"growth"));
                    v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has_property", (ScriptValue)var31_30, var32_31, (ScriptContext)var1_1);
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                if (!v3 /* !! */ .asBool()) {
                    v4 = false;
                } else lbl-1000:
                // 2 sources

                {
                    v4 = true;
                }
                if (!v4) continue;
                var33_32 = var1_1.getClassOrVar("block");
                if (var33_32 != ScriptValue.NULL) {
                    var34_33 = new ArrayList<E>();
                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "apply_bone_meal", (ScriptValue)var33_32, var34_33, (ScriptContext)var1_1);
                } else {
                    v5 /* !! */  = ScriptValue.NULL;
                }
                var35_34 = v5 /* !! */ ;
                var0.val("ok", var35_34);
                if (!var35_34.asBool()) continue;
                var36_35 = new ArrayList<ScriptValue>();
                var36_35.add(ScriptValue.of((double)0.0));
                var36_35.add(ScriptValue.of((double)1.0));
                var37_36 = var1_1.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(var37_36 != ScriptValue.NULL ? (var37_36 instanceof ScriptValue.Obj && (var39_38 = (var38_37 = (ScriptValue.Obj)var37_36).instance()) != null && !(var39_38 instanceof PolyClass) && var38_37.typeName().equals("Machine") ? new PolyClassMachine_v2(var39_38).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)var37_36, (ScriptContext)var1_1)) : ScriptValue.NULL), var36_35, (ScriptContext)var1_1);
                break;
            }
        }
    }
}
