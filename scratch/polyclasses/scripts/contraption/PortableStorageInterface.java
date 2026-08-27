/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.contraption;

import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassContraptionManager;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class PortableStorageInterface {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _findPartner(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("required_facing"), (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return scriptContext.getClassOrVar("null");
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("DIR_VEC");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("real_facing"), (ScriptValue)scriptContext.getClassOrVar("null"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("vec", scriptValue2);
        double d = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("i", scriptValue3);
        int n = 0;
        ScriptValue scriptValue4 = ScriptValue.of((double)d);
        Object object = scriptContext.getClassOrVar("check");
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("target");
        while (n < 1000) {
            ScriptValue scriptValue6;
            ScriptValue scriptValue7;
            ScriptValue scriptValue8;
            ScriptValue scriptValue9;
            ScriptValue scriptValue10;
            ScriptValue scriptValue11;
            PolyClassMachine_v3 polyClassMachine_v3;
            ++n;
            if (!(scriptValue4.asNum() < scriptContext.getNum("MAX_REACH"))) break;
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("contraption");
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)((polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine_v3.pg$200_x() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine_v32 != null ? polyClassMachine_v32.tg$134_facing_dx() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v33 != null ? polyClassMachine_v33.pg$202_y() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine_v34 != null ? polyClassMachine_v34.tg$130_facing_dy() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v35 != null ? polyClassMachine_v35.pg$206_z() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine_v36 != null ? polyClassMachine_v36.tg$132_facing_dz() : ((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptContext)scriptContext);
            builder.val("check", (ScriptValue)callSite);
            object = callSite;
            if (ScriptFormula.valuesEqual((ScriptValue)object, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && (object != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)object, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true && ScriptFormula.valuesEqualStr((ScriptValue)(object != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)object, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"cml:portable_storage_interface")) {
                ScriptValue scriptValue13 = object != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "machine", (ScriptValue)object, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("target", scriptValue13);
                scriptValue5 = scriptValue13;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && (scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "facing")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("required_facing"))) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "target"));
                    arrayList.add(scriptValue5);
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "i"));
                    arrayList.add(scriptValue4);
                    return ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
                }
            }
            ScriptValue scriptValue14 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 1.0)));
            builder.val("i", scriptValue14);
            scriptValue4 = scriptValue14;
        }
        return scriptContext.getClassOrVar("null");
    }

    public static ScriptValue onGetContainer(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "_psi_uuid";
            String string2 = "str";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
            ScriptValue scriptValue4 = scriptValue2;
            PolyClassContraptionManager polyClassContraptionManager = PolyClassContraptionManager.ofGuarded((ScriptValue)scriptValue3);
            object = polyClassContraptionManager != null ? polyClassContraptionManager.tm$6_get(scriptValue4.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("linked", scriptValue5);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return scriptContext.getClassOrVar("null");
        }
        return scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static ScriptValue statusItem(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "_psi_uuid";
            String string2 = "str";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("uuid", scriptValue2);
        return ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") ^ true ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "minecraft:lightning_rod")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "minecraft:iron_bars"));
    }

    public static ScriptValue statusName(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "_psi_uuid";
            String string2 = "str";
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("uuid", scriptValue2);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") ^ true) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "<green><b>Linked");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "<gray><b>Searching\u2026");
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block35: {
            block36: {
                block33: {
                    block34: {
                        var1_1 = var0.peek();
                        var2_2 = new ArrayList<ScriptValue>();
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "north"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "south"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "south"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "north"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "east"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "west"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "west"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "east"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "up"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "down"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "down"));
                        var2_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "up"));
                        var3_3 = ScriptFormula.callBuiltin((String)"make_map", var2_2, (ScriptContext)var1_1);
                        var0.val("OPPOSITE", var3_3);
                        var4_4 = new ArrayList<Object>();
                        var4_4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "north"));
                        var5_5 = new ArrayList<ScriptValue>();
                        var5_5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var5_5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var5_5.add(ScriptValue.of((double)(-1.0)));
                        var4_4.add(new ScriptValue.Array(var5_5));
                        var4_4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "south"));
                        var6_6 = new ArrayList<ScriptValue>();
                        var6_6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var6_6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var6_6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 1.0));
                        var4_4.add(new ScriptValue.Array(var6_6));
                        var4_4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "east"));
                        var7_7 = new ArrayList<ScriptValue>();
                        var7_7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 1.0));
                        var7_7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var7_7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var4_4.add(new ScriptValue.Array(var7_7));
                        var4_4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "west"));
                        var8_8 = new ArrayList<ScriptValue>();
                        var8_8.add(ScriptValue.of((double)(-1.0)));
                        var8_8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var8_8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var4_4.add(new ScriptValue.Array(var8_8));
                        var4_4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "up"));
                        var9_9 = new ArrayList<ScriptValue>();
                        var9_9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var9_9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 1.0));
                        var9_9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var4_4.add(new ScriptValue.Array(var9_9));
                        var4_4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "down"));
                        var10_10 = new ArrayList<ScriptValue>();
                        var10_10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
                        var10_10.add(ScriptValue.of((double)(-1.0)));
                        var10_10.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0));
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
                        var21_18 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                        if (!(ScriptFormula.valuesEqual((ScriptValue)(var21_18 != null ? var21_18.pg$191_contraption() : ((var22_19 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var22_19, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block33;
                        var23_20 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var25_22 = var23_20 != null ? var23_20.pg$191_contraption() : ((var24_21 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var24_21, (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("contraption", var25_22);
                        var26_23 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var28_25 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var30_27 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var32_29 = ScriptValue.of((String)((var26_23 != null ? var26_23.pg$200_x() : ((var27_24 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var27_24, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var28_25 != null ? var28_25.pg$202_y() : ((var29_26 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var29_26, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var30_27 != null ? var30_27.pg$206_z() : ((var31_28 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var31_28, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr()));
                        var0.val("hold_key", var32_29);
                        if (var25_22 != ScriptValue.NULL) {
                            var34_30 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                            var33_32 = var34_30 != null ? var34_30.pg$133_facing_dx() : ((var35_31 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var35_31, (ScriptContext)var1_1) : ScriptValue.NULL);
                            var37_33 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                            var36_35 = var37_33 != null ? var37_33.pg$129_facing_dy() : ((var38_34 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var38_34, (ScriptContext)var1_1) : ScriptValue.NULL);
                            var40_36 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                            var39_38 = var40_36 != null ? var40_36.pg$131_facing_dz() : ((var41_37 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var41_37, (ScriptContext)var1_1) : ScriptValue.NULL);
                            var42_39 = PolyClassContraption.ofGuarded((ScriptValue)var25_22);
                            v0 /* !! */  = var42_39 != null ? var42_39.tm$50_real_direction(var33_32.asNum(), var36_35.asNum(), var39_38.asNum()) : PolyDispatch.bootstrapCall("memberCall", "real_direction", (ScriptValue)var25_22, (ScriptValue)var33_32, (ScriptValue)var36_35, (ScriptValue)var39_38, (ScriptContext)var1_1);
                        } else {
                            v0 /* !! */  = ScriptValue.NULL;
                        }
                        var43_40 = v0 /* !! */ ;
                        var0.val("real_facing", var43_40);
                        var44_41 = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var3_3, (ScriptValue)var43_40, (ScriptValue)var1_1.getClassOrVar("null"), (ScriptContext)var1_1) : ScriptValue.NULL;
                        var0.val("required_facing", var44_41);
                        var45_42 = ScriptContext.builder().copyFrom(var1_1);
                        var45_42.val("contraption", var25_22);
                        var45_42.val("real_facing", var43_40);
                        var45_42.val("required_facing", var44_41);
                        var46_43 = PortableStorageInterface._findPartner(var45_42);
                        var0.val("result", var46_43);
                        var47_44 = ScriptFormula.valuesEqual((ScriptValue)var46_43, (ScriptValue)var1_1.getClassOrVar("null")) != false ? var1_1.getClassOrVar("null") : (var46_43 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var46_43, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "target")), (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("found", var47_44);
                        var48_45 = ScriptFormula.valuesEqual((ScriptValue)var46_43, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ScriptValue.of((double)(-1.0)) : (var46_43 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var46_43, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "i")), (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("found_i", var48_45);
                        if (ScriptFormula.valuesEqual((ScriptValue)var47_44, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            v1 /* !! */  = var47_44 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var47_44, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "_psi_uuid")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "str")), (ScriptValue)(var25_22 != ScriptValue.NULL ? ((var49_46 = PolyClassContraption.ofGuarded((ScriptValue)var25_22)) != null ? var49_46.pg$64_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var25_22, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptContext)var1_1) : ScriptValue.NULL;
                            v2 /* !! */  = var47_44 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var47_44, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "_psi_keepalive")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "int")), (ScriptValue)ScriptValue.of((double)var15_14), (ScriptContext)var1_1) : ScriptValue.NULL;
                            var50_47 = var47_44 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var47_44, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var0.val("last", var50_47);
                            var51_48 = var50_47.asNum() < 0.0 != false ? 0.0 : ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum() - var50_47.asNum();
                            var53_49 = ScriptValue.of((double)var51_48);
                            var0.val("idle_ticks", var53_49);
                            if (var51_48 <= var18_16) {
                                if (var25_22 != ScriptValue.NULL) {
                                    var54_50 = var32_29;
                                    var55_51 = PolyClassContraption.ofGuarded((ScriptValue)var25_22);
                                    v3 /* !! */  = var55_51 != null ? ScriptValue.of((boolean)var55_51.tm$12_hold(var54_50.asStr())) : PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var25_22, (ScriptValue)var54_50, (ScriptContext)var1_1);
                                } else {
                                    v3 /* !! */  = ScriptValue.NULL;
                                }
                            } else if (var25_22 != ScriptValue.NULL) {
                                var56_52 = var32_29;
                                var57_53 = PolyClassContraption.ofGuarded((ScriptValue)var25_22);
                                v4 /* !! */  = var57_53 != null ? ScriptValue.of((boolean)var57_53.tm$2_release(var56_52.asStr())) : PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var25_22, (ScriptValue)var56_52, (ScriptContext)var1_1);
                            } else {
                                v4 /* !! */  = ScriptValue.NULL;
                            }
                        } else if (var25_22 != ScriptValue.NULL) {
                            var58_54 = var32_29;
                            var59_55 = PolyClassContraption.ofGuarded((ScriptValue)var25_22);
                            v5 /* !! */  = var59_55 != null ? ScriptValue.of((boolean)var59_55.tm$2_release(var58_54.asStr())) : PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var25_22, (ScriptValue)var58_54, (ScriptContext)var1_1);
                        } else {
                            v5 /* !! */  = ScriptValue.NULL;
                        }
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var47_44, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && var48_45.asNum() >= 3.0 != false)) break block34;
                        v6 /* !! */  = var47_44 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var47_44, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (!(v6 /* !! */ .asNum() >= 0.0)) ** GOTO lbl-1000
                        v7 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum();
                        v8 /* !! */  = var47_44 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var47_44, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (v7 - v8 /* !! */ .asNum() < 10.0) {
                            v9 = true;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v9 = false;
                        }
                        var60_56 = v9;
                        var61_57 = ScriptValue.of((boolean)v9);
                        var0.val("recent", var61_57);
                        var62_58 = var1_1.getClassOrVar("Machine");
                        if (var62_58 != ScriptValue.NULL) {
                            var63_59 = "_psi_extend";
                            var64_60 = "int";
                            var65_61 = ScriptValue.of((double)(var60_56 != false ? 2.0 : 1.0));
                            var66_62 = PolyClassMachine_v3.ofGuarded((ScriptValue)var62_58);
                            v10 /* !! */  = var66_62 != null ? ScriptValue.of((boolean)var66_62.tm$82_set_typed(var63_59, var64_60, var65_61)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var62_58, (ScriptValue)ScriptValue.of((String)var63_59), (ScriptValue)ScriptValue.of((String)var64_60), (ScriptValue)var65_61, (ScriptContext)var1_1);
                        } else {
                            v10 /* !! */  = ScriptValue.NULL;
                        }
                        break block35;
                    }
                    var67_63 = var1_1.getClassOrVar("Machine");
                    if (var67_63 != ScriptValue.NULL) {
                        var68_64 = "_psi_extend";
                        var69_65 = "int";
                        var70_66 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0);
                        var71_67 = PolyClassMachine_v3.ofGuarded((ScriptValue)var67_63);
                        v11 /* !! */  = var71_67 != null ? ScriptValue.of((boolean)var71_67.tm$82_set_typed(var68_64, var69_65, var70_66)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var67_63, (ScriptValue)ScriptValue.of((String)var68_64), (ScriptValue)ScriptValue.of((String)var69_65), (ScriptValue)var70_66, (ScriptContext)var1_1);
                    } else {
                        v11 /* !! */  = ScriptValue.NULL;
                    }
                    break block35;
                }
                var72_68 = var1_1.getClassOrVar("Machine");
                if (var72_68 != ScriptValue.NULL) {
                    var73_69 = "_psi_keepalive";
                    var74_70 = "int";
                    var75_71 = PolyClassMachine_v3.ofGuarded((ScriptValue)var72_68);
                    v12 /* !! */  = var75_71 != null ? var75_71.tm$34_get_typed(var73_69, var74_70) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var72_68, (ScriptValue)ScriptValue.of((String)var73_69), (ScriptValue)ScriptValue.of((String)var74_70), (ScriptContext)var1_1);
                } else {
                    v12 /* !! */  = ScriptValue.NULL;
                }
                var76_72 = v12 /* !! */ ;
                var0.val("keepalive", var76_72);
                if (ScriptFormula.valuesEqual((ScriptValue)var76_72, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var77_73 = 0.0;
                    var79_74 = ScriptValue.of((double)0.0);
                    var0.val("keepalive", var79_74);
                }
                if (var1_1.getNum("keepalive") > 0.0) {
                    if (var72_68 != ScriptValue.NULL) {
                        var80_75 = "_psi_keepalive";
                        var81_76 = "int";
                        var82_77 = ScriptValue.of((double)(var1_1.getNum("keepalive") - 1.0));
                        var83_78 = PolyClassMachine_v3.ofGuarded((ScriptValue)var72_68);
                        v13 /* !! */  = var83_78 != null ? ScriptValue.of((boolean)var83_78.tm$82_set_typed(var80_75, var81_76, var82_77)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var72_68, (ScriptValue)ScriptValue.of((String)var80_75), (ScriptValue)ScriptValue.of((String)var81_76), (ScriptValue)var82_77, (ScriptContext)var1_1);
                    } else {
                        v13 /* !! */  = ScriptValue.NULL;
                    }
                } else if (var72_68 != ScriptValue.NULL) {
                    var84_79 = "_psi_uuid";
                    var85_80 = "str";
                    var86_81 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "");
                    var87_82 = PolyClassMachine_v3.ofGuarded((ScriptValue)var72_68);
                    v14 /* !! */  = var87_82 != null ? ScriptValue.of((boolean)var87_82.tm$82_set_typed(var84_79, var85_80, var86_81)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var72_68, (ScriptValue)ScriptValue.of((String)var84_79), (ScriptValue)ScriptValue.of((String)var85_80), (ScriptValue)var86_81, (ScriptContext)var1_1);
                } else {
                    v14 /* !! */  = ScriptValue.NULL;
                }
                if (!(var1_1.getNum("keepalive") > 0.0)) break block36;
                v15 = var72_68 != ScriptValue.NULL ? ((var88_83 = PolyClassMachine_v3.ofGuarded((ScriptValue)var72_68)) != null ? var88_83.tg$136_last_transfer_tick() : PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var72_68, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (!(v15 >= 0.0)) ** GOTO lbl-1000
                v16 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum();
                v17 = var72_68 != ScriptValue.NULL ? ((var89_84 = PolyClassMachine_v3.ofGuarded((ScriptValue)var72_68)) != null ? var89_84.tg$136_last_transfer_tick() : PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var72_68, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (v16 - v17 < 10.0) {
                    v18 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v18 = false;
                }
                var90_85 = v18;
                var91_86 = ScriptValue.of((boolean)v18);
                var0.val("recent", var91_86);
                if (var72_68 != ScriptValue.NULL) {
                    var92_87 = "_psi_extend";
                    var93_88 = "int";
                    var94_89 = ScriptValue.of((double)(var90_85 != false ? 2.0 : 1.0));
                    var95_90 = PolyClassMachine_v3.ofGuarded((ScriptValue)var72_68);
                    v19 /* !! */  = var95_90 != null ? ScriptValue.of((boolean)var95_90.tm$82_set_typed(var92_87, var93_88, var94_89)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var72_68, (ScriptValue)ScriptValue.of((String)var92_87), (ScriptValue)ScriptValue.of((String)var93_88), (ScriptValue)var94_89, (ScriptContext)var1_1);
                } else {
                    v19 /* !! */  = ScriptValue.NULL;
                }
                break block35;
            }
            if (var72_68 != ScriptValue.NULL) {
                var96_91 = "_psi_extend";
                var97_92 = "int";
                var98_93 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0);
                var99_94 = PolyClassMachine_v3.ofGuarded((ScriptValue)var72_68);
                v20 /* !! */  = var99_94 != null ? ScriptValue.of((boolean)var99_94.tm$82_set_typed(var96_91, var97_92, var98_93)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var72_68, (ScriptValue)ScriptValue.of((String)var96_91), (ScriptValue)ScriptValue.of((String)var97_92), (ScriptValue)var98_93, (ScriptContext)var1_1);
            } else {
                v20 /* !! */  = ScriptValue.NULL;
            }
        }
        PortableStorageInterface.FILE_SCOPE = var0.build();
    }
}
