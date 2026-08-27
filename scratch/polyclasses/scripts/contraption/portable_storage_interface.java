/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
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
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
                var13_12 = var1_1.getClassOrVar("Machine");
                var10_9 = var1_1.getClassOrVar("Machine");
                var9_8.add(ScriptFormula.addPolymorphic((ScriptValue)(var10_9 != ScriptValue.NULL ? (var10_9 instanceof ScriptValue.Obj && (var12_11 = (var11_10 = (ScriptValue.Obj)var10_9).instance()) != null && !(var12_11 instanceof PolyClass) && var11_10.typeName().equals("Machine") ? new PolyClassMachine_v3(var12_11).pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var10_9, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)((var13_12 != ScriptValue.NULL ? (var13_12 instanceof ScriptValue.Obj && (var15_14 = (var14_13 = (ScriptValue.Obj)var13_12).instance()) != null && !(var15_14 instanceof PolyClass) && var14_13.typeName().equals("Machine") ? new PolyClassMachine_v3(var15_14).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var13_12, (ScriptContext)var1_1)) : ScriptValue.NULL).asNum() * var1_1.getNum("i")))));
                var16_15 = var1_1.getClassOrVar("Machine");
                var19_18 = var1_1.getClassOrVar("Machine");
                var9_8.add(ScriptFormula.addPolymorphic((ScriptValue)(var16_15 != ScriptValue.NULL ? (var16_15 instanceof ScriptValue.Obj && (var18_17 = (var17_16 = (ScriptValue.Obj)var16_15).instance()) != null && !(var18_17 instanceof PolyClass) && var17_16.typeName().equals("Machine") ? new PolyClassMachine_v3(var18_17).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var16_15, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)((var19_18 != ScriptValue.NULL ? (var19_18 instanceof ScriptValue.Obj && (var21_20 = (var20_19 = (ScriptValue.Obj)var19_18).instance()) != null && !(var21_20 instanceof PolyClass) && var20_19.typeName().equals("Machine") ? new PolyClassMachine_v3(var21_20).pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var19_18, (ScriptContext)var1_1)) : ScriptValue.NULL).asNum() * var1_1.getNum("i")))));
                var22_21 = var1_1.getClassOrVar("Machine");
                var25_24 = var1_1.getClassOrVar("Machine");
                var9_8.add(ScriptFormula.addPolymorphic((ScriptValue)(var22_21 != ScriptValue.NULL ? (var22_21 instanceof ScriptValue.Obj && (var24_23 = (var23_22 = (ScriptValue.Obj)var22_21).instance()) != null && !(var24_23 instanceof PolyClass) && var23_22.typeName().equals("Machine") ? new PolyClassMachine_v3(var24_23).pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var22_21, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)((var25_24 != ScriptValue.NULL ? (var25_24 instanceof ScriptValue.Obj && (var27_26 = (var26_25 = (ScriptValue.Obj)var25_24).instance()) != null && !(var27_26 instanceof PolyClass) && var26_25.typeName().equals("Machine") ? new PolyClassMachine_v3(var27_26).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var25_24, (ScriptContext)var1_1)) : ScriptValue.NULL).asNum() * var1_1.getNum("i")))));
                var28_27 = var1_1.getClassOrVar("contraption");
                var29_28 = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(var28_27 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)var28_27, (ScriptContext)var1_1) : ScriptValue.NULL), var9_8, (ScriptContext)var1_1);
                var0.val("check", (ScriptValue)var29_28);
                if (!(((ScriptFormula.valuesEqual((ScriptValue)var29_28, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (((var30_29 = var1_1.getClassOrVar("check")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var30_29, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false) != false && ScriptFormula.valuesEqualStr((ScriptValue)((var31_30 = var1_1.getClassOrVar("check")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var31_30, (ScriptContext)var1_1) : ScriptValue.NULL), (String)"cml:portable_storage_interface") != false)) break block7;
                var32_31 = var1_1.getClassOrVar("check");
                var33_32 = var32_31 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "machine", (ScriptValue)var32_31, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("target", var33_32);
                if (!((ScriptFormula.valuesEqual((ScriptValue)var33_32, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && (((var34_33 = var1_1.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)var34_33, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) ** GOTO lbl-1000
                var35_34 = new ArrayList<ScriptValue>();
                var35_34.add(ScriptValue.of((String)"facing"));
                var36_35 = var1_1.getClassOrVar("target");
                if (ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(var36_35 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)var36_35, (ScriptContext)var1_1) : ScriptValue.NULL), var35_34, (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("required_facing"))) {
                    v1 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v1 = false;
                }
                if (v1) {
                    var37_37 = new ArrayList<ScriptValue>();
                    var37_37.add(ScriptValue.of((String)"target"));
                    var37_37.add(var33_32);
                    var37_37.add(ScriptValue.of((String)"i"));
                    var37_37.add(var1_1.getClassOrVar("i"));
                    return ScriptFormula.callBuiltin((String)"make_map", var37_37, (ScriptContext)var1_1);
                }
            }
            var38_36 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("i"), (ScriptValue)ScriptValue.of((double)1.0));
            var0.val("i", var38_36);
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
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                object2 = polyClassMachine_v3.tm$34_get_typed(string, string2);
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
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
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
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
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
                        v0 /* !! */  = var21_18 != ScriptValue.NULL ? (var21_18 instanceof ScriptValue.Obj && (var23_20 = (var22_19 = (ScriptValue.Obj)var21_18).instance()) != null && !(var23_20 instanceof PolyClass) && var22_19.typeName().equals("Machine") ? new PolyClassMachine_v3(var23_20).pg$160_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var21_18, (ScriptContext)var1_1)) : ScriptValue.NULL;
                        if (!(ScriptFormula.valuesEqual((ScriptValue)v0 /* !! */ , (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block63;
                        var24_21 = var1_1.getClassOrVar("Machine");
                        var27_24 = var24_21 != ScriptValue.NULL ? (var24_21 instanceof ScriptValue.Obj && (var26_23 = (var25_22 = (ScriptValue.Obj)var24_21).instance()) != null && !(var26_23 instanceof PolyClass) && var25_22.typeName().equals("Machine") ? new PolyClassMachine_v3(var26_23).pg$160_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var24_21, (ScriptContext)var1_1)) : ScriptValue.NULL;
                        var0.val("contraption", var27_24);
                        var28_25 = var1_1.getClassOrVar("Machine");
                        var31_28 = var1_1.getClassOrVar("Machine");
                        var34_31 = var1_1.getClassOrVar("Machine");
                        var37_34 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(var28_25 != ScriptValue.NULL ? (var28_25 instanceof ScriptValue.Obj && (var30_27 = (var29_26 = (ScriptValue.Obj)var28_25).instance()) != null && !(var30_27 instanceof PolyClass) && var29_26.typeName().equals("Machine") ? new PolyClassMachine_v3(var30_27).pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var28_25, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(var31_28 != ScriptValue.NULL ? (var31_28 instanceof ScriptValue.Obj && (var33_30 = (var32_29 = (ScriptValue.Obj)var31_28).instance()) != null && !(var33_30 instanceof PolyClass) && var32_29.typeName().equals("Machine") ? new PolyClassMachine_v3(var33_30).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var31_28, (ScriptContext)var1_1)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(var34_31 != ScriptValue.NULL ? (var34_31 instanceof ScriptValue.Obj && (var36_33 = (var35_32 = (ScriptValue.Obj)var34_31).instance()) != null && !(var36_33 instanceof PolyClass) && var35_32.typeName().equals("Machine") ? new PolyClassMachine_v3(var36_33).pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var34_31, (ScriptContext)var1_1)) : ScriptValue.NULL));
                        var0.val("hold_key", var37_34);
                        var38_35 = var1_1.getClassOrVar("contraption");
                        if (var38_35 != ScriptValue.NULL) {
                            var39_36 = new ArrayList<ScriptValue>();
                            var40_37 = var1_1.getClassOrVar("Machine");
                            var39_36.add((ScriptValue)(var40_37 != ScriptValue.NULL ? (var40_37 instanceof ScriptValue.Obj && (var42_39 = (var41_38 = (ScriptValue.Obj)var40_37).instance()) != null && !(var42_39 instanceof PolyClass) && var41_38.typeName().equals("Machine") ? new PolyClassMachine_v3(var42_39).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var40_37, (ScriptContext)var1_1)) : ScriptValue.NULL));
                            var43_40 = var1_1.getClassOrVar("Machine");
                            var39_36.add((ScriptValue)(var43_40 != ScriptValue.NULL ? (var43_40 instanceof ScriptValue.Obj && (var45_42 = (var44_41 = (ScriptValue.Obj)var43_40).instance()) != null && !(var45_42 instanceof PolyClass) && var44_41.typeName().equals("Machine") ? new PolyClassMachine_v3(var45_42).pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var43_40, (ScriptContext)var1_1)) : ScriptValue.NULL));
                            var46_43 = var1_1.getClassOrVar("Machine");
                            var39_36.add((ScriptValue)(var46_43 != ScriptValue.NULL ? (var46_43 instanceof ScriptValue.Obj && (var48_45 = (var47_44 = (ScriptValue.Obj)var46_43).instance()) != null && !(var48_45 instanceof PolyClass) && var47_44.typeName().equals("Machine") ? new PolyClassMachine_v3(var48_45).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var46_43, (ScriptContext)var1_1)) : ScriptValue.NULL));
                            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "real_direction", (ScriptValue)var38_35, var39_36, (ScriptContext)var1_1);
                        } else {
                            v1 /* !! */  = ScriptValue.NULL;
                        }
                        var49_46 = v1 /* !! */ ;
                        var0.val("real_facing", var49_46);
                        var50_47 = var1_1.getClassOrVar("OPPOSITE");
                        if (var50_47 != ScriptValue.NULL) {
                            var51_48 = new ArrayList<ScriptValue>();
                            var51_48.add(var49_46);
                            var51_48.add(var1_1.getClassOrVar("null"));
                            v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var50_47, var51_48, (ScriptContext)var1_1);
                        } else {
                            v2 /* !! */  = ScriptValue.NULL;
                        }
                        var52_49 = v2 /* !! */ ;
                        var0.val("required_facing", var52_49);
                        var53_50 = ScriptContext.builder().copyFrom(var1_1);
                        var53_50.val("contraption", var27_24);
                        var53_50.val("real_facing", var49_46);
                        var53_50.val("required_facing", var52_49);
                        var54_51 = PortableStorageInterface._findPartner(var53_50);
                        var0.val("result", var54_51);
                        if (ScriptFormula.valuesEqual((ScriptValue)var54_51, (ScriptValue)var1_1.getClassOrVar("null"))) {
                            v3 /* !! */  = var1_1.getClassOrVar("null");
                        } else {
                            var55_52 = var1_1.getClassOrVar("result");
                            if (var55_52 != ScriptValue.NULL) {
                                var56_53 = new ArrayList<ScriptValue>();
                                var56_53.add(ScriptValue.of((String)"target"));
                                v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var55_52, var56_53, (ScriptContext)var1_1);
                            } else {
                                v3 /* !! */  = ScriptValue.NULL;
                            }
                        }
                        var57_54 = v3 /* !! */ ;
                        var0.val("found", var57_54);
                        if (ScriptFormula.valuesEqual((ScriptValue)var54_51, (ScriptValue)var1_1.getClassOrVar("null"))) {
                            v4 /* !! */  = ScriptValue.of((double)(-1.0));
                        } else {
                            var58_55 = var1_1.getClassOrVar("result");
                            if (var58_55 != ScriptValue.NULL) {
                                var59_56 = new ArrayList<ScriptValue>();
                                var59_56.add(ScriptValue.of((String)"i"));
                                v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var58_55, var59_56, (ScriptContext)var1_1);
                            } else {
                                v4 /* !! */  = ScriptValue.NULL;
                            }
                        }
                        var60_57 = v4 /* !! */ ;
                        var0.val("found_i", var60_57);
                        if (ScriptFormula.valuesEqual((ScriptValue)var57_54, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var61_58 = var1_1.getClassOrVar("found");
                            if (var61_58 != ScriptValue.NULL) {
                                var62_59 = new ArrayList<ScriptValue>();
                                var62_59.add(ScriptValue.of((String)"_psi_uuid"));
                                var62_59.add(ScriptValue.of((String)"str"));
                                var63_60 = var1_1.getClassOrVar("contraption");
                                var62_59.add((ScriptValue)(var63_60 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var63_60, (ScriptContext)var1_1) : ScriptValue.NULL));
                                v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var61_58, var62_59, (ScriptContext)var1_1);
                            } else {
                                v5 /* !! */  = ScriptValue.NULL;
                            }
                            var64_61 = var1_1.getClassOrVar("found");
                            if (var64_61 != ScriptValue.NULL) {
                                var65_62 = new ArrayList<ScriptValue>();
                                var65_62.add(ScriptValue.of((String)"_psi_keepalive"));
                                var65_62.add(ScriptValue.of((String)"int"));
                                var65_62.add(ScriptValue.of((double)var15_14));
                                v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var64_61, var65_62, (ScriptContext)var1_1);
                            } else {
                                v6 /* !! */  = ScriptValue.NULL;
                            }
                            var66_63 = var1_1.getClassOrVar("found");
                            var67_64 = var66_63 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var66_63, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var0.val("last", var67_64);
                            if (var67_64.asNum() < 0.0) {
                                v7 = 0.0;
                            } else {
                                var68_65 = new ArrayList<E>();
                                v7 = ScriptFormula.callBuiltin((String)"tick", var68_65, (ScriptContext)var1_1).asNum() - var67_64.asNum();
                            }
                            var69_66 = v7;
                            var71_67 = ScriptValue.of((double)v7);
                            var0.val("idle_ticks", var71_67);
                            if (var69_66 <= var18_16) {
                                var72_68 = var1_1.getClassOrVar("contraption");
                                if (var72_68 != ScriptValue.NULL) {
                                    var73_69 = new ArrayList<ScriptValue>();
                                    var73_69.add(var37_34);
                                    v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var72_68, var73_69, (ScriptContext)var1_1);
                                } else {
                                    v8 /* !! */  = ScriptValue.NULL;
                                }
                            } else {
                                var74_70 = var1_1.getClassOrVar("contraption");
                                if (var74_70 != ScriptValue.NULL) {
                                    var75_71 = new ArrayList<ScriptValue>();
                                    var75_71.add(var37_34);
                                    v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var74_70, var75_71, (ScriptContext)var1_1);
                                } else {
                                    v9 /* !! */  = ScriptValue.NULL;
                                }
                            }
                        } else {
                            var76_72 = var1_1.getClassOrVar("contraption");
                            if (var76_72 != ScriptValue.NULL) {
                                var77_73 = new ArrayList<ScriptValue>();
                                var77_73.add(var37_34);
                                v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var76_72, var77_73, (ScriptContext)var1_1);
                            } else {
                                v10 /* !! */  = ScriptValue.NULL;
                            }
                        }
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var57_54, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && var60_57.asNum() >= 3.0 != false)) break block64;
                        var78_74 = var1_1.getClassOrVar("found");
                        v11 /* !! */  = var78_74 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var78_74, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (!(v11 /* !! */ .asNum() >= 0.0)) ** GOTO lbl-1000
                        var79_75 = new ArrayList<E>();
                        v12 = ScriptFormula.callBuiltin((String)"tick", var79_75, (ScriptContext)var1_1).asNum();
                        var80_76 = var1_1.getClassOrVar("found");
                        v13 /* !! */  = var80_76 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var80_76, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (v12 - v13 /* !! */ .asNum() < 10.0) {
                            v14 = true;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v14 = false;
                        }
                        var81_77 = v14;
                        var82_78 = ScriptValue.of((boolean)v14);
                        var0.val("recent", var82_78);
                        var83_79 = var1_1.getClassOrVar("Machine");
                        if (var83_79 != ScriptValue.NULL) {
                            var84_80 = "_psi_extend";
                            var85_81 = "int";
                            var86_82 = ScriptValue.of((double)(var81_77 != false ? 2.0 : 1.0));
                            if (var83_79 instanceof ScriptValue.Obj && (var88_84 = (var87_83 = (ScriptValue.Obj)var83_79).instance()) != null && !(var88_84 instanceof PolyClass) && var87_83.typeName().equals("Machine")) {
                                var89_85 = new PolyClassMachine_v3(var88_84);
                                v15 /* !! */  = ScriptValue.of((boolean)var89_85.tm$82_set_typed(var84_80, var85_81, var86_82));
                            } else {
                                var90_86 = new ArrayList<ScriptValue>();
                                var90_86.add(ScriptValue.of((String)var84_80));
                                var90_86.add(ScriptValue.of((String)var85_81));
                                var90_86.add(var86_82);
                                v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var83_79, var90_86, (ScriptContext)var1_1);
                            }
                        } else {
                            v15 /* !! */  = ScriptValue.NULL;
                        }
                        break block65;
                    }
                    var91_87 = var1_1.getClassOrVar("Machine");
                    if (var91_87 != ScriptValue.NULL) {
                        var92_88 = "_psi_extend";
                        var93_89 = "int";
                        var94_90 = ScriptValue.of((double)0.0);
                        if (var91_87 instanceof ScriptValue.Obj && (var96_92 = (var95_91 = (ScriptValue.Obj)var91_87).instance()) != null && !(var96_92 instanceof PolyClass) && var95_91.typeName().equals("Machine")) {
                            var97_93 = new PolyClassMachine_v3(var96_92);
                            v16 /* !! */  = ScriptValue.of((boolean)var97_93.tm$82_set_typed(var92_88, var93_89, var94_90));
                        } else {
                            var98_94 = new ArrayList<ScriptValue>();
                            var98_94.add(ScriptValue.of((String)var92_88));
                            var98_94.add(ScriptValue.of((String)var93_89));
                            var98_94.add(var94_90);
                            v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var91_87, var98_94, (ScriptContext)var1_1);
                        }
                    } else {
                        v16 /* !! */  = ScriptValue.NULL;
                    }
                    break block65;
                }
                var99_95 = var1_1.getClassOrVar("Machine");
                if (var99_95 != ScriptValue.NULL) {
                    var100_96 = "_psi_keepalive";
                    var101_97 = "int";
                    if (var99_95 instanceof ScriptValue.Obj && (var103_99 = (var102_98 = (ScriptValue.Obj)var99_95).instance()) != null && !(var103_99 instanceof PolyClass) && var102_98.typeName().equals("Machine")) {
                        var104_100 = new PolyClassMachine_v3(var103_99);
                        v17 /* !! */  = var104_100.tm$34_get_typed(var100_96, var101_97);
                    } else {
                        var105_101 = new ArrayList<ScriptValue>();
                        var105_101.add(ScriptValue.of((String)var100_96));
                        var105_101.add(ScriptValue.of((String)var101_97));
                        v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var99_95, var105_101, (ScriptContext)var1_1);
                    }
                } else {
                    v17 /* !! */  = ScriptValue.NULL;
                }
                var106_102 = v17 /* !! */ ;
                var0.val("keepalive", var106_102);
                if (ScriptFormula.valuesEqual((ScriptValue)var106_102, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var107_103 = 0.0;
                    var109_104 = ScriptValue.of((double)0.0);
                    var0.val("keepalive", var109_104);
                }
                if (var1_1.getNum("keepalive") > 0.0) {
                    var110_105 = var1_1.getClassOrVar("Machine");
                    if (var110_105 != ScriptValue.NULL) {
                        var111_106 = "_psi_keepalive";
                        var112_107 = "int";
                        var113_108 = ScriptValue.of((double)(var1_1.getNum("keepalive") - 1.0));
                        if (var110_105 instanceof ScriptValue.Obj && (var115_110 = (var114_109 = (ScriptValue.Obj)var110_105).instance()) != null && !(var115_110 instanceof PolyClass) && var114_109.typeName().equals("Machine")) {
                            var116_111 = new PolyClassMachine_v3(var115_110);
                            v18 /* !! */  = ScriptValue.of((boolean)var116_111.tm$82_set_typed(var111_106, var112_107, var113_108));
                        } else {
                            var117_112 = new ArrayList<ScriptValue>();
                            var117_112.add(ScriptValue.of((String)var111_106));
                            var117_112.add(ScriptValue.of((String)var112_107));
                            var117_112.add(var113_108);
                            v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var110_105, var117_112, (ScriptContext)var1_1);
                        }
                    } else {
                        v18 /* !! */  = ScriptValue.NULL;
                    }
                } else {
                    var118_113 = var1_1.getClassOrVar("Machine");
                    if (var118_113 != ScriptValue.NULL) {
                        var119_114 = "_psi_uuid";
                        var120_115 = "str";
                        var121_116 = ScriptValue.of((String)"");
                        if (var118_113 instanceof ScriptValue.Obj && (var123_118 = (var122_117 = (ScriptValue.Obj)var118_113).instance()) != null && !(var123_118 instanceof PolyClass) && var122_117.typeName().equals("Machine")) {
                            var124_119 = new PolyClassMachine_v3(var123_118);
                            v19 /* !! */  = ScriptValue.of((boolean)var124_119.tm$82_set_typed(var119_114, var120_115, var121_116));
                        } else {
                            var125_120 = new ArrayList<ScriptValue>();
                            var125_120.add(ScriptValue.of((String)var119_114));
                            var125_120.add(ScriptValue.of((String)var120_115));
                            var125_120.add(var121_116);
                            v19 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var118_113, var125_120, (ScriptContext)var1_1);
                        }
                    } else {
                        v19 /* !! */  = ScriptValue.NULL;
                    }
                }
                if (!(var1_1.getNum("keepalive") > 0.0)) break block66;
                var126_121 = var1_1.getClassOrVar("Machine");
                v20 /* !! */  = var126_121 != ScriptValue.NULL ? (var126_121 instanceof ScriptValue.Obj && (var128_123 = (var127_122 = (ScriptValue.Obj)var126_121).instance()) != null && !(var128_123 instanceof PolyClass) && var127_122.typeName().equals("Machine") ? new PolyClassMachine_v3(var128_123).pg$127_last_transfer_tick() : PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var126_121, (ScriptContext)var1_1)) : ScriptValue.NULL;
                if (!(v20 /* !! */ .asNum() >= 0.0)) ** GOTO lbl-1000
                var129_124 = new ArrayList<E>();
                v21 = ScriptFormula.callBuiltin((String)"tick", var129_124, (ScriptContext)var1_1).asNum();
                var130_125 = var1_1.getClassOrVar("Machine");
                v22 /* !! */  = var130_125 != ScriptValue.NULL ? (var130_125 instanceof ScriptValue.Obj && (var132_127 = (var131_126 = (ScriptValue.Obj)var130_125).instance()) != null && !(var132_127 instanceof PolyClass) && var131_126.typeName().equals("Machine") ? new PolyClassMachine_v3(var132_127).pg$127_last_transfer_tick() : PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var130_125, (ScriptContext)var1_1)) : ScriptValue.NULL;
                if (v21 - v22 /* !! */ .asNum() < 10.0) {
                    v23 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v23 = false;
                }
                var133_128 = v23;
                var134_129 = ScriptValue.of((boolean)v23);
                var0.val("recent", var134_129);
                var135_130 = var1_1.getClassOrVar("Machine");
                if (var135_130 != ScriptValue.NULL) {
                    var136_131 = "_psi_extend";
                    var137_132 = "int";
                    var138_133 = ScriptValue.of((double)(var133_128 != false ? 2.0 : 1.0));
                    if (var135_130 instanceof ScriptValue.Obj && (var140_135 = (var139_134 = (ScriptValue.Obj)var135_130).instance()) != null && !(var140_135 instanceof PolyClass) && var139_134.typeName().equals("Machine")) {
                        var141_136 = new PolyClassMachine_v3(var140_135);
                        v24 /* !! */  = ScriptValue.of((boolean)var141_136.tm$82_set_typed(var136_131, var137_132, var138_133));
                    } else {
                        var142_137 = new ArrayList<ScriptValue>();
                        var142_137.add(ScriptValue.of((String)var136_131));
                        var142_137.add(ScriptValue.of((String)var137_132));
                        var142_137.add(var138_133);
                        v24 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var135_130, var142_137, (ScriptContext)var1_1);
                    }
                } else {
                    v24 /* !! */  = ScriptValue.NULL;
                }
                break block65;
            }
            var143_138 = var1_1.getClassOrVar("Machine");
            if (var143_138 != ScriptValue.NULL) {
                var144_139 = "_psi_extend";
                var145_140 = "int";
                var146_141 = ScriptValue.of((double)0.0);
                if (var143_138 instanceof ScriptValue.Obj && (var148_143 = (var147_142 = (ScriptValue.Obj)var143_138).instance()) != null && !(var148_143 instanceof PolyClass) && var147_142.typeName().equals("Machine")) {
                    var149_144 = new PolyClassMachine_v3(var148_143);
                    v25 /* !! */  = ScriptValue.of((boolean)var149_144.tm$82_set_typed(var144_139, var145_140, var146_141));
                } else {
                    var150_145 = new ArrayList<ScriptValue>();
                    var150_145.add(ScriptValue.of((String)var144_139));
                    var150_145.add(ScriptValue.of((String)var145_140));
                    var150_145.add(var146_141);
                    v25 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var143_138, var150_145, (ScriptContext)var1_1);
                }
            } else {
                v25 /* !! */  = ScriptValue.NULL;
            }
        }
        PortableStorageInterface.FILE_SCOPE = var0.build();
    }
}
