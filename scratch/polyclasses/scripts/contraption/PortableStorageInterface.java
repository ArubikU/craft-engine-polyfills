/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.contraption;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContraptionManager;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class PortableStorageInterface {
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
    public static ScriptValue _findPartner(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("required_facing"), (ScriptValue)var1_1.getClassOrVar("null"))) {
            return var1_1.getClassOrVar("null");
        }
        var2_2 = var1_1.getClassOrVar("DIR_VEC");
        if (var2_2 != ScriptValue.NULL) {
            var3_3 = new ArrayList<ScriptValue>();
            var3_3.add(var1_1.getClassOrVar("real_facing"));
            var3_3.add(var1_1.getClassOrVar("null"));
            v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var2_2, var3_3, (ScriptContext)var1_1);
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var4_4 = v0 /* !! */ ;
        var0.val("vec", var4_4);
        var5_5 = 0.0;
        var7_6 = ScriptValue.of((double)0.0);
        var0.val("i", var7_6);
        var8_7 = 0;
        while (var8_7 < 1000) {
            block7: {
                ++var8_7;
                if (!(var1_1.getNum("i") < var1_1.getNum("MAX_REACH"))) break;
                var9_8 = new ArrayList<ScriptValue>();
                var12_11 = var1_1.getClassOrVar("Machine");
                var10_9 = var1_1.getClassOrVar("Machine");
                var9_8.add(ScriptFormula.addPolymorphic((ScriptValue)(var10_9 != ScriptValue.NULL ? ((var11_10 = PolyClassMachine.ofGuarded((ScriptValue)var10_9)) != null ? var11_10.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var10_9, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)((var12_11 != ScriptValue.NULL ? ((var13_12 = PolyClassMachine.ofGuarded((ScriptValue)var12_11)) != null ? var13_12.tg$134_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var12_11, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum()) * var1_1.getNum("i")))));
                var14_13 = var1_1.getClassOrVar("Machine");
                var16_15 = var1_1.getClassOrVar("Machine");
                var9_8.add(ScriptFormula.addPolymorphic((ScriptValue)(var14_13 != ScriptValue.NULL ? ((var15_14 = PolyClassMachine.ofGuarded((ScriptValue)var14_13)) != null ? var15_14.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var14_13, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)((var16_15 != ScriptValue.NULL ? ((var17_16 = PolyClassMachine.ofGuarded((ScriptValue)var16_15)) != null ? var17_16.tg$130_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var16_15, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum()) * var1_1.getNum("i")))));
                var18_17 = var1_1.getClassOrVar("Machine");
                var20_19 = var1_1.getClassOrVar("Machine");
                var9_8.add(ScriptFormula.addPolymorphic((ScriptValue)(var18_17 != ScriptValue.NULL ? ((var19_18 = PolyClassMachine.ofGuarded((ScriptValue)var18_17)) != null ? var19_18.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var18_17, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)((var20_19 != ScriptValue.NULL ? ((var21_20 = PolyClassMachine.ofGuarded((ScriptValue)var20_19)) != null ? var21_20.tg$132_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var20_19, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum()) * var1_1.getNum("i")))));
                var22_21 = var1_1.getClassOrVar("contraption");
                var23_22 = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(var22_21 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)var22_21, (ScriptContext)var1_1) : ScriptValue.NULL), var9_8, (ScriptContext)var1_1);
                var0.val("check", (ScriptValue)var23_22);
                if (!(((ScriptFormula.valuesEqual((ScriptValue)var23_22, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (((var24_23 = var1_1.getClassOrVar("check")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var24_23, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false) != false && ScriptFormula.valuesEqualStr((ScriptValue)((var25_24 = var1_1.getClassOrVar("check")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var25_24, (ScriptContext)var1_1) : ScriptValue.NULL), (String)"cml:portable_storage_interface") != false)) break block7;
                var26_25 = var1_1.getClassOrVar("check");
                var27_26 = var26_25 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "machine", (ScriptValue)var26_25, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("target", var27_26);
                if (!((ScriptFormula.valuesEqual((ScriptValue)var27_26, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (((var28_27 = var1_1.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)var28_27, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) ** GOTO lbl-1000
                var29_28 = new ArrayList<ScriptValue>();
                var29_28.add(ScriptValue.of((String)"facing"));
                var30_29 = var1_1.getClassOrVar("target");
                if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(var30_29 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)var30_29, (ScriptContext)var1_1) : ScriptValue.NULL), var29_28, (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("required_facing"))) {
                    v1 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v1 = false;
                }
                if (v1) {
                    var31_31 = new ArrayList<ScriptValue>();
                    var31_31.add(ScriptValue.of((String)"target"));
                    var31_31.add(var27_26);
                    var31_31.add(ScriptValue.of((String)"i"));
                    var31_31.add(var1_1.getClassOrVar("i"));
                    return ScriptFormula.callBuiltin((String)"make_map", var31_31, (ScriptContext)var1_1);
                }
            }
            var32_30 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("i"), (ScriptValue)ScriptValue.of((double)1.0));
            var0.val("i", var32_30);
        }
        return var1_1.getClassOrVar("null");
    }

    public static ScriptValue onGetContainer(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "_psi_uuid";
            String string2 = "str";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                object2 = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("uuid", scriptValue2);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            return scriptContext.getClassOrVar("null");
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("ContraptionManager");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue4 = scriptValue2;
            if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("ContraptionManager")) {
                PolyClassContraptionManager polyClassContraptionManager = new PolyClassContraptionManager(object4);
                object = polyClassContraptionManager.tm$6_get(scriptValue4.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("linked", scriptValue5);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return scriptContext.getClassOrVar("null");
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("linked");
        return scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static ScriptValue statusItem(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "_psi_uuid";
            String string2 = "str";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("uuid", scriptValue2);
        return ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") ^ true ? ScriptValue.of((String)"minecraft:lightning_rod") : ScriptValue.of((String)"minecraft:iron_bars");
    }

    public static ScriptValue statusName(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "_psi_uuid";
            String string2 = "str";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("uuid", scriptValue2);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") ^ true) {
            return ScriptValue.of((String)"<green><b>Linked");
        }
        return ScriptValue.of((String)"<gray><b>Searching\u2026");
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block65: {
            block66: {
                block63: {
                    block64: {
                        var1_1 = var0.peek();
                        var2_2 = new ArrayList<ScriptValue>();
                        var2_2.add(ScriptValue.of((String)"north"));
                        var2_2.add(ScriptValue.of((String)"south"));
                        var2_2.add(ScriptValue.of((String)"south"));
                        var2_2.add(ScriptValue.of((String)"north"));
                        var2_2.add(ScriptValue.of((String)"east"));
                        var2_2.add(ScriptValue.of((String)"west"));
                        var2_2.add(ScriptValue.of((String)"west"));
                        var2_2.add(ScriptValue.of((String)"east"));
                        var2_2.add(ScriptValue.of((String)"up"));
                        var2_2.add(ScriptValue.of((String)"down"));
                        var2_2.add(ScriptValue.of((String)"down"));
                        var2_2.add(ScriptValue.of((String)"up"));
                        var3_3 = ScriptFormula.callBuiltin((String)"make_map", var2_2, (ScriptContext)var1_1);
                        var0.val("OPPOSITE", var3_3);
                        var4_4 = new ArrayList<Object>();
                        var4_4.add(ScriptValue.of((String)"north"));
                        var5_5 = new ArrayList<ScriptValue>();
                        var5_5.add(ScriptValue.of((double)0.0));
                        var5_5.add(ScriptValue.of((double)0.0));
                        var5_5.add(ScriptValue.of((double)(-1.0)));
                        var4_4.add(new ScriptValue.Array(var5_5));
                        var4_4.add(ScriptValue.of((String)"south"));
                        var6_6 = new ArrayList<ScriptValue>();
                        var6_6.add(ScriptValue.of((double)0.0));
                        var6_6.add(ScriptValue.of((double)0.0));
                        var6_6.add(ScriptValue.of((double)1.0));
                        var4_4.add(new ScriptValue.Array(var6_6));
                        var4_4.add(ScriptValue.of((String)"east"));
                        var7_7 = new ArrayList<ScriptValue>();
                        var7_7.add(ScriptValue.of((double)1.0));
                        var7_7.add(ScriptValue.of((double)0.0));
                        var7_7.add(ScriptValue.of((double)0.0));
                        var4_4.add(new ScriptValue.Array(var7_7));
                        var4_4.add(ScriptValue.of((String)"west"));
                        var8_8 = new ArrayList<ScriptValue>();
                        var8_8.add(ScriptValue.of((double)(-1.0)));
                        var8_8.add(ScriptValue.of((double)0.0));
                        var8_8.add(ScriptValue.of((double)0.0));
                        var4_4.add(new ScriptValue.Array(var8_8));
                        var4_4.add(ScriptValue.of((String)"up"));
                        var9_9 = new ArrayList<ScriptValue>();
                        var9_9.add(ScriptValue.of((double)0.0));
                        var9_9.add(ScriptValue.of((double)1.0));
                        var9_9.add(ScriptValue.of((double)0.0));
                        var4_4.add(new ScriptValue.Array(var9_9));
                        var4_4.add(ScriptValue.of((String)"down"));
                        var10_10 = new ArrayList<ScriptValue>();
                        var10_10.add(ScriptValue.of((double)0.0));
                        var10_10.add(ScriptValue.of((double)(-1.0)));
                        var10_10.add(ScriptValue.of((double)0.0));
                        var4_4.add(new ScriptValue.Array(var10_10));
                        var11_11 = ScriptFormula.callBuiltin((String)"make_map", var4_4, (ScriptContext)var1_1);
                        var0.val("DIR_VEC", var11_11);
                        var12_12 = 4.0;
                        var14_13 = ScriptValue.of((double)4.0);
                        var0.val("MAX_REACH", var14_13);
                        var15_14 = 6.0;
                        var17_15 = ScriptValue.of((double)6.0);
                        var0.val("KEEPALIVE_TICKS", var17_15);
                        var18_16 = 40.0;
                        var20_17 = ScriptValue.of((double)40.0);
                        var0.val("RELEASE_GRACE_TICKS", var20_17);
                        var21_18 = var1_1.getClassOrVar("Machine");
                        v0 /* !! */  = var21_18 != ScriptValue.NULL ? ((var22_19 = PolyClassMachine.ofGuarded((ScriptValue)var21_18)) != null ? var22_19.pg$190_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var21_18, (ScriptContext)var1_1)) : ScriptValue.NULL;
                        if (!(ScriptFormula.valuesEqual((ScriptValue)v0 /* !! */ , (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block63;
                        var23_20 = var1_1.getClassOrVar("Machine");
                        var25_22 = var23_20 != ScriptValue.NULL ? ((var24_21 = PolyClassMachine.ofGuarded((ScriptValue)var23_20)) != null ? var24_21.pg$190_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var23_20, (ScriptContext)var1_1)) : ScriptValue.NULL;
                        var0.val("contraption", var25_22);
                        var26_23 = var1_1.getClassOrVar("Machine");
                        var28_25 = var1_1.getClassOrVar("Machine");
                        var30_27 = var1_1.getClassOrVar("Machine");
                        var32_29 = ScriptValue.of((String)((var26_23 != ScriptValue.NULL ? ((var27_24 = PolyClassMachine.ofGuarded((ScriptValue)var26_23)) != null ? var27_24.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var26_23, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "," + (var28_25 != ScriptValue.NULL ? ((var29_26 = PolyClassMachine.ofGuarded((ScriptValue)var28_25)) != null ? var29_26.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var28_25, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "," + (var30_27 != ScriptValue.NULL ? ((var31_28 = PolyClassMachine.ofGuarded((ScriptValue)var30_27)) != null ? var31_28.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var30_27, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr()));
                        var0.val("hold_key", var32_29);
                        var33_30 = var1_1.getClassOrVar("contraption");
                        if (var33_30 != ScriptValue.NULL) {
                            var34_31 = new ArrayList<ScriptValue>();
                            var35_32 = var1_1.getClassOrVar("Machine");
                            var34_31.add((ScriptValue)(var35_32 != ScriptValue.NULL ? ((var36_33 = PolyClassMachine.ofGuarded((ScriptValue)var35_32)) != null ? var36_33.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var35_32, (ScriptContext)var1_1)) : ScriptValue.NULL));
                            var37_34 = var1_1.getClassOrVar("Machine");
                            var34_31.add((ScriptValue)(var37_34 != ScriptValue.NULL ? ((var38_35 = PolyClassMachine.ofGuarded((ScriptValue)var37_34)) != null ? var38_35.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var37_34, (ScriptContext)var1_1)) : ScriptValue.NULL));
                            var39_36 = var1_1.getClassOrVar("Machine");
                            var34_31.add((ScriptValue)(var39_36 != ScriptValue.NULL ? ((var40_37 = PolyClassMachine.ofGuarded((ScriptValue)var39_36)) != null ? var40_37.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var39_36, (ScriptContext)var1_1)) : ScriptValue.NULL));
                            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "real_direction", (ScriptValue)var33_30, var34_31, (ScriptContext)var1_1);
                        } else {
                            v1 /* !! */  = ScriptValue.NULL;
                        }
                        var41_38 = v1 /* !! */ ;
                        var0.val("real_facing", var41_38);
                        var42_39 = var1_1.getClassOrVar("OPPOSITE");
                        if (var42_39 != ScriptValue.NULL) {
                            var43_40 = new ArrayList<ScriptValue>();
                            var43_40.add(var41_38);
                            var43_40.add(var1_1.getClassOrVar("null"));
                            v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var42_39, var43_40, (ScriptContext)var1_1);
                        } else {
                            v2 /* !! */  = ScriptValue.NULL;
                        }
                        var44_41 = v2 /* !! */ ;
                        var0.val("required_facing", var44_41);
                        var45_42 = ScriptContext.builder().copyFrom(var1_1);
                        var45_42.val("contraption", var25_22);
                        var45_42.val("real_facing", var41_38);
                        var45_42.val("required_facing", var44_41);
                        var46_43 = PortableStorageInterface._findPartner(var45_42);
                        var0.val("result", var46_43);
                        if (ScriptFormula.valuesEqual((ScriptValue)var46_43, (ScriptValue)var1_1.getClassOrVar("null"))) {
                            v3 /* !! */  = var1_1.getClassOrVar("null");
                        } else {
                            var47_44 = var1_1.getClassOrVar("result");
                            if (var47_44 != ScriptValue.NULL) {
                                var48_45 = new ArrayList<ScriptValue>();
                                var48_45.add(ScriptValue.of((String)"target"));
                                v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var47_44, var48_45, (ScriptContext)var1_1);
                            } else {
                                v3 /* !! */  = ScriptValue.NULL;
                            }
                        }
                        var49_46 = v3 /* !! */ ;
                        var0.val("found", var49_46);
                        if (ScriptFormula.valuesEqual((ScriptValue)var46_43, (ScriptValue)var1_1.getClassOrVar("null"))) {
                            v4 /* !! */  = ScriptValue.of((double)(-1.0));
                        } else {
                            var50_47 = var1_1.getClassOrVar("result");
                            if (var50_47 != ScriptValue.NULL) {
                                var51_48 = new ArrayList<ScriptValue>();
                                var51_48.add(ScriptValue.of((String)"i"));
                                v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var50_47, var51_48, (ScriptContext)var1_1);
                            } else {
                                v4 /* !! */  = ScriptValue.NULL;
                            }
                        }
                        var52_49 = v4 /* !! */ ;
                        var0.val("found_i", var52_49);
                        if (ScriptFormula.valuesEqual((ScriptValue)var49_46, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var53_50 = var1_1.getClassOrVar("found");
                            if (var53_50 != ScriptValue.NULL) {
                                var54_51 = new ArrayList<ScriptValue>();
                                var54_51.add(ScriptValue.of((String)"_psi_uuid"));
                                var54_51.add(ScriptValue.of((String)"str"));
                                var55_52 = var1_1.getClassOrVar("contraption");
                                var54_51.add((ScriptValue)(var55_52 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var55_52, (ScriptContext)var1_1) : ScriptValue.NULL));
                                v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var53_50, var54_51, (ScriptContext)var1_1);
                            } else {
                                v5 /* !! */  = ScriptValue.NULL;
                            }
                            var56_53 = var1_1.getClassOrVar("found");
                            if (var56_53 != ScriptValue.NULL) {
                                var57_54 = new ArrayList<ScriptValue>();
                                var57_54.add(ScriptValue.of((String)"_psi_keepalive"));
                                var57_54.add(ScriptValue.of((String)"int"));
                                var57_54.add(ScriptValue.of((double)var15_14));
                                v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var56_53, var57_54, (ScriptContext)var1_1);
                            } else {
                                v6 /* !! */  = ScriptValue.NULL;
                            }
                            var58_55 = var1_1.getClassOrVar("found");
                            var59_56 = var58_55 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var58_55, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var0.val("last", var59_56);
                            if (var59_56.asNum() < 0.0) {
                                v7 = 0.0;
                            } else {
                                var60_57 = new ArrayList<E>();
                                v7 = ScriptFormula.callBuiltin((String)"tick", var60_57, (ScriptContext)var1_1).asNum() - var59_56.asNum();
                            }
                            var61_58 = v7;
                            var63_59 = ScriptValue.of((double)v7);
                            var0.val("idle_ticks", var63_59);
                            if (var61_58 <= var18_16) {
                                var64_60 = var1_1.getClassOrVar("contraption");
                                if (var64_60 != ScriptValue.NULL) {
                                    var65_61 = new ArrayList<ScriptValue>();
                                    var65_61.add(var32_29);
                                    v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var64_60, var65_61, (ScriptContext)var1_1);
                                } else {
                                    v8 /* !! */  = ScriptValue.NULL;
                                }
                            } else {
                                var66_62 = var1_1.getClassOrVar("contraption");
                                if (var66_62 != ScriptValue.NULL) {
                                    var67_63 = new ArrayList<ScriptValue>();
                                    var67_63.add(var32_29);
                                    v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var66_62, var67_63, (ScriptContext)var1_1);
                                } else {
                                    v9 /* !! */  = ScriptValue.NULL;
                                }
                            }
                        } else {
                            var68_64 = var1_1.getClassOrVar("contraption");
                            if (var68_64 != ScriptValue.NULL) {
                                var69_65 = new ArrayList<ScriptValue>();
                                var69_65.add(var32_29);
                                v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var68_64, var69_65, (ScriptContext)var1_1);
                            } else {
                                v10 /* !! */  = ScriptValue.NULL;
                            }
                        }
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var49_46, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && var52_49.asNum() >= 3.0 != false)) break block64;
                        var70_66 = var1_1.getClassOrVar("found");
                        v11 /* !! */  = var70_66 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var70_66, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (!(v11 /* !! */ .asNum() >= 0.0)) ** GOTO lbl-1000
                        var71_67 = new ArrayList<E>();
                        v12 = ScriptFormula.callBuiltin((String)"tick", var71_67, (ScriptContext)var1_1).asNum();
                        var72_68 = var1_1.getClassOrVar("found");
                        v13 /* !! */  = var72_68 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var72_68, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (v12 - v13 /* !! */ .asNum() < 10.0) {
                            v14 = true;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v14 = false;
                        }
                        var73_69 = v14;
                        var74_70 = ScriptValue.of((boolean)v14);
                        var0.val("recent", var74_70);
                        var75_71 = var1_1.getClassOrVar("Machine");
                        if (var75_71 != ScriptValue.NULL) {
                            var76_72 = "_psi_extend";
                            var77_73 = "int";
                            var78_74 = ScriptValue.of((double)(var73_69 != false ? 2.0 : 1.0));
                            if (var75_71 instanceof ScriptValue.Obj && (var80_76 = (var79_75 = (ScriptValue.Obj)var75_71).instance()) != null && !(var80_76 instanceof PolyClass) && var79_75.typeName().equals("Machine")) {
                                var81_77 = new PolyClassMachine(var80_76);
                                v15 /* !! */  = ScriptValue.of((boolean)var81_77.tm$82_set_typed(var76_72, var77_73, var78_74));
                            } else {
                                var82_78 = new ArrayList<ScriptValue>();
                                var82_78.add(ScriptValue.of((String)var76_72));
                                var82_78.add(ScriptValue.of((String)var77_73));
                                var82_78.add(var78_74);
                                v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var75_71, var82_78, (ScriptContext)var1_1);
                            }
                        } else {
                            v15 /* !! */  = ScriptValue.NULL;
                        }
                        break block65;
                    }
                    var83_79 = var1_1.getClassOrVar("Machine");
                    if (var83_79 != ScriptValue.NULL) {
                        var84_80 = "_psi_extend";
                        var85_81 = "int";
                        var86_82 = ScriptValue.of((double)0.0);
                        if (var83_79 instanceof ScriptValue.Obj && (var88_84 = (var87_83 = (ScriptValue.Obj)var83_79).instance()) != null && !(var88_84 instanceof PolyClass) && var87_83.typeName().equals("Machine")) {
                            var89_85 = new PolyClassMachine(var88_84);
                            v16 /* !! */  = ScriptValue.of((boolean)var89_85.tm$82_set_typed(var84_80, var85_81, var86_82));
                        } else {
                            var90_86 = new ArrayList<ScriptValue>();
                            var90_86.add(ScriptValue.of((String)var84_80));
                            var90_86.add(ScriptValue.of((String)var85_81));
                            var90_86.add(var86_82);
                            v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var83_79, var90_86, (ScriptContext)var1_1);
                        }
                    } else {
                        v16 /* !! */  = ScriptValue.NULL;
                    }
                    break block65;
                }
                var91_87 = var1_1.getClassOrVar("Machine");
                if (var91_87 != ScriptValue.NULL) {
                    var92_88 = "_psi_keepalive";
                    var93_89 = "int";
                    if (var91_87 instanceof ScriptValue.Obj && (var95_91 = (var94_90 = (ScriptValue.Obj)var91_87).instance()) != null && !(var95_91 instanceof PolyClass) && var94_90.typeName().equals("Machine")) {
                        var96_92 = new PolyClassMachine(var95_91);
                        v17 /* !! */  = var96_92.tm$34_get_typed(var92_88, var93_89);
                    } else {
                        var97_93 = new ArrayList<ScriptValue>();
                        var97_93.add(ScriptValue.of((String)var92_88));
                        var97_93.add(ScriptValue.of((String)var93_89));
                        v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var91_87, var97_93, (ScriptContext)var1_1);
                    }
                } else {
                    v17 /* !! */  = ScriptValue.NULL;
                }
                var98_94 = v17 /* !! */ ;
                var0.val("keepalive", var98_94);
                if (ScriptFormula.valuesEqual((ScriptValue)var98_94, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var99_95 = 0.0;
                    var101_96 = ScriptValue.of((double)0.0);
                    var0.val("keepalive", var101_96);
                }
                if (var1_1.getNum("keepalive") > 0.0) {
                    var102_97 = var1_1.getClassOrVar("Machine");
                    if (var102_97 != ScriptValue.NULL) {
                        var103_98 = "_psi_keepalive";
                        var104_99 = "int";
                        var105_100 = ScriptValue.of((double)(var1_1.getNum("keepalive") - 1.0));
                        if (var102_97 instanceof ScriptValue.Obj && (var107_102 = (var106_101 = (ScriptValue.Obj)var102_97).instance()) != null && !(var107_102 instanceof PolyClass) && var106_101.typeName().equals("Machine")) {
                            var108_103 = new PolyClassMachine(var107_102);
                            v18 /* !! */  = ScriptValue.of((boolean)var108_103.tm$82_set_typed(var103_98, var104_99, var105_100));
                        } else {
                            var109_104 = new ArrayList<ScriptValue>();
                            var109_104.add(ScriptValue.of((String)var103_98));
                            var109_104.add(ScriptValue.of((String)var104_99));
                            var109_104.add(var105_100);
                            v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var102_97, var109_104, (ScriptContext)var1_1);
                        }
                    } else {
                        v18 /* !! */  = ScriptValue.NULL;
                    }
                } else {
                    var110_105 = var1_1.getClassOrVar("Machine");
                    if (var110_105 != ScriptValue.NULL) {
                        var111_106 = "_psi_uuid";
                        var112_107 = "str";
                        var113_108 = ScriptValue.of((String)"");
                        if (var110_105 instanceof ScriptValue.Obj && (var115_110 = (var114_109 = (ScriptValue.Obj)var110_105).instance()) != null && !(var115_110 instanceof PolyClass) && var114_109.typeName().equals("Machine")) {
                            var116_111 = new PolyClassMachine(var115_110);
                            v19 /* !! */  = ScriptValue.of((boolean)var116_111.tm$82_set_typed(var111_106, var112_107, var113_108));
                        } else {
                            var117_112 = new ArrayList<ScriptValue>();
                            var117_112.add(ScriptValue.of((String)var111_106));
                            var117_112.add(ScriptValue.of((String)var112_107));
                            var117_112.add(var113_108);
                            v19 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var110_105, var117_112, (ScriptContext)var1_1);
                        }
                    } else {
                        v19 /* !! */  = ScriptValue.NULL;
                    }
                }
                if (!(var1_1.getNum("keepalive") > 0.0)) break block66;
                var118_113 = var1_1.getClassOrVar("Machine");
                v20 = var118_113 != ScriptValue.NULL ? ((var119_114 = PolyClassMachine.ofGuarded((ScriptValue)var118_113)) != null ? var119_114.tg$136_last_transfer_tick() : PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var118_113, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (!(v20 >= 0.0)) ** GOTO lbl-1000
                var120_115 = new ArrayList<E>();
                v21 = ScriptFormula.callBuiltin((String)"tick", var120_115, (ScriptContext)var1_1).asNum();
                var121_116 = var1_1.getClassOrVar("Machine");
                v22 = var121_116 != ScriptValue.NULL ? ((var122_117 = PolyClassMachine.ofGuarded((ScriptValue)var121_116)) != null ? var122_117.tg$136_last_transfer_tick() : PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var121_116, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (v21 - v22 < 10.0) {
                    v23 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v23 = false;
                }
                var123_118 = v23;
                var124_119 = ScriptValue.of((boolean)v23);
                var0.val("recent", var124_119);
                var125_120 = var1_1.getClassOrVar("Machine");
                if (var125_120 != ScriptValue.NULL) {
                    var126_121 = "_psi_extend";
                    var127_122 = "int";
                    var128_123 = ScriptValue.of((double)(var123_118 != false ? 2.0 : 1.0));
                    if (var125_120 instanceof ScriptValue.Obj && (var130_125 = (var129_124 = (ScriptValue.Obj)var125_120).instance()) != null && !(var130_125 instanceof PolyClass) && var129_124.typeName().equals("Machine")) {
                        var131_126 = new PolyClassMachine(var130_125);
                        v24 /* !! */  = ScriptValue.of((boolean)var131_126.tm$82_set_typed(var126_121, var127_122, var128_123));
                    } else {
                        var132_127 = new ArrayList<ScriptValue>();
                        var132_127.add(ScriptValue.of((String)var126_121));
                        var132_127.add(ScriptValue.of((String)var127_122));
                        var132_127.add(var128_123);
                        v24 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var125_120, var132_127, (ScriptContext)var1_1);
                    }
                } else {
                    v24 /* !! */  = ScriptValue.NULL;
                }
                break block65;
            }
            var133_128 = var1_1.getClassOrVar("Machine");
            if (var133_128 != ScriptValue.NULL) {
                var134_129 = "_psi_extend";
                var135_130 = "int";
                var136_131 = ScriptValue.of((double)0.0);
                if (var133_128 instanceof ScriptValue.Obj && (var138_133 = (var137_132 = (ScriptValue.Obj)var133_128).instance()) != null && !(var138_133 instanceof PolyClass) && var137_132.typeName().equals("Machine")) {
                    var139_134 = new PolyClassMachine(var138_133);
                    v25 /* !! */  = ScriptValue.of((boolean)var139_134.tm$82_set_typed(var134_129, var135_130, var136_131));
                } else {
                    var140_135 = new ArrayList<ScriptValue>();
                    var140_135.add(ScriptValue.of((String)var134_129));
                    var140_135.add(ScriptValue.of((String)var135_130));
                    var140_135.add(var136_131);
                    v25 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var133_128, var140_135, (ScriptContext)var1_1);
                }
            } else {
                v25 /* !! */  = ScriptValue.NULL;
            }
        }
        PortableStorageInterface.FILE_SCOPE = var0.build();
    }
}
