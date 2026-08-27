/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassContraptionManager;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
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
            PolyClassMachine_v2 polyClassMachine_v2;
            ++n;
            if (!(scriptValue4.asNum() < scriptContext.getNum("MAX_REACH"))) break;
            PolyClassMachine_v2 polyClassMachine_v22 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v2 polyClassMachine_v23 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v2 polyClassMachine_v24 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v2 polyClassMachine_v25 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v2 polyClassMachine_v26 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("contraption");
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)((polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine_v2.pg$200_x() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine_v22 != null ? polyClassMachine_v22.tg$134_facing_dx() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v23 != null ? polyClassMachine_v23.pg$202_y() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine_v24 != null ? polyClassMachine_v24.tg$130_facing_dy() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v25 != null ? polyClassMachine_v25.pg$206_z() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine_v26 != null ? polyClassMachine_v26.tg$132_facing_dz() : ((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptContext)scriptContext);
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
            ScriptValue.Obj obj;
            Object object3;
            String string = "_psi_uuid";
            String string2 = "str";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object3);
                object2 = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "_psi_uuid";
            String string2 = "str";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "_psi_uuid";
            String string2 = "str";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
        block57: {
            block58: {
                block55: {
                    block56: {
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
                        var21_18 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                        if (!(ScriptFormula.valuesEqual((ScriptValue)(var21_18 != null ? var21_18.pg$191_contraption() : ((var22_19 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var22_19, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block55;
                        var23_20 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var25_22 = var23_20 != null ? var23_20.pg$191_contraption() : ((var24_21 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var24_21, (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("contraption", var25_22);
                        var26_23 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var28_25 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var30_27 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var32_29 = ScriptValue.of((String)((var26_23 != null ? var26_23.pg$200_x() : ((var27_24 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var27_24, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var28_25 != null ? var28_25.pg$202_y() : ((var29_26 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var29_26, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var30_27 != null ? var30_27.pg$206_z() : ((var31_28 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var31_28, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr()));
                        var0.val("hold_key", var32_29);
                        var33_30 = var1_1.getClassOrVar("contraption");
                        if (var33_30 != ScriptValue.NULL) {
                            var35_31 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                            var34_33 = var35_31 != null ? var35_31.pg$133_facing_dx() : ((var36_32 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var36_32, (ScriptContext)var1_1) : ScriptValue.NULL);
                            var38_34 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                            var37_36 = var38_34 != null ? var38_34.pg$129_facing_dy() : ((var39_35 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var39_35, (ScriptContext)var1_1) : ScriptValue.NULL);
                            var41_37 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                            v0 /* !! */  = var41_37 != null ? var41_37.pg$131_facing_dz() : (var40_39 = (var42_38 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var42_38, (ScriptContext)var1_1) : ScriptValue.NULL);
                            if (var33_30 instanceof ScriptValue.Obj && (var44_41 = (var43_40 = (ScriptValue.Obj)var33_30).instance()) != null && !(var44_41 instanceof PolyClass) && var43_40.typeName().equals("Contraption")) {
                                var45_42 = new PolyClassContraption(var44_41);
                                v1 /* !! */  = var45_42.tm$50_real_direction(var34_33.asNum(), var37_36.asNum(), var40_39.asNum());
                            } else {
                                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "real_direction", (ScriptValue)var33_30, (ScriptValue)var34_33, (ScriptValue)var37_36, (ScriptValue)var40_39, (ScriptContext)var1_1);
                            }
                        } else {
                            v1 /* !! */  = ScriptValue.NULL;
                        }
                        var46_43 = v1 /* !! */ ;
                        var0.val("real_facing", var46_43);
                        var47_44 = var1_1.getClassOrVar("OPPOSITE");
                        var48_45 = var47_44 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var47_44, (ScriptValue)var46_43, (ScriptValue)var1_1.getClassOrVar("null"), (ScriptContext)var1_1) : ScriptValue.NULL;
                        var0.val("required_facing", var48_45);
                        var49_46 = ScriptContext.builder().copyFrom(var1_1);
                        var49_46.val("contraption", var25_22);
                        var49_46.val("real_facing", var46_43);
                        var49_46.val("required_facing", var48_45);
                        var50_47 = PortableStorageInterface._findPartner(var49_46);
                        var0.val("result", var50_47);
                        var52_49 = ScriptFormula.valuesEqual((ScriptValue)var50_47, (ScriptValue)var1_1.getClassOrVar("null")) != false ? var1_1.getClassOrVar("null") : ((var51_48 = var1_1.getClassOrVar("result")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var51_48, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "target")), (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("found", var52_49);
                        var54_51 = ScriptFormula.valuesEqual((ScriptValue)var50_47, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ScriptValue.of((double)(-1.0)) : ((var53_50 = var1_1.getClassOrVar("result")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var53_50, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "i")), (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("found_i", var54_51);
                        if (ScriptFormula.valuesEqual((ScriptValue)var52_49, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var55_52 = var1_1.getClassOrVar("found");
                            v2 /* !! */  = var55_52 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var55_52, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "_psi_uuid")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "str")), (ScriptValue)(var25_22 != ScriptValue.NULL ? ((var56_53 = PolyClassContraption.ofGuarded((ScriptValue)var25_22)) != null ? var56_53.pg$64_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var25_22, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptContext)var1_1) : ScriptValue.NULL;
                            var57_54 = var1_1.getClassOrVar("found");
                            v3 /* !! */  = var57_54 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var57_54, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "_psi_keepalive")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "int")), (ScriptValue)ScriptValue.of((double)var15_14), (ScriptContext)var1_1) : ScriptValue.NULL;
                            var58_55 = var52_49 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var52_49, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var0.val("last", var58_55);
                            var59_56 = var58_55.asNum() < 0.0 != false ? 0.0 : ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum() - var58_55.asNum();
                            var61_57 = ScriptValue.of((double)var59_56);
                            var0.val("idle_ticks", var61_57);
                            if (var59_56 <= var18_16) {
                                var62_58 = var1_1.getClassOrVar("contraption");
                                if (var62_58 != ScriptValue.NULL) {
                                    var63_59 = var32_29;
                                    if (var62_58 instanceof ScriptValue.Obj && (var65_61 = (var64_60 = (ScriptValue.Obj)var62_58).instance()) != null && !(var65_61 instanceof PolyClass) && var64_60.typeName().equals("Contraption")) {
                                        var66_62 = new PolyClassContraption(var65_61);
                                        v4 /* !! */  = ScriptValue.of((boolean)var66_62.tm$12_hold(var63_59.asStr()));
                                    } else {
                                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var62_58, (ScriptValue)var63_59, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v4 /* !! */  = ScriptValue.NULL;
                                }
                            } else {
                                var67_63 = var1_1.getClassOrVar("contraption");
                                if (var67_63 != ScriptValue.NULL) {
                                    var68_64 = var32_29;
                                    if (var67_63 instanceof ScriptValue.Obj && (var70_66 = (var69_65 = (ScriptValue.Obj)var67_63).instance()) != null && !(var70_66 instanceof PolyClass) && var69_65.typeName().equals("Contraption")) {
                                        var71_67 = new PolyClassContraption(var70_66);
                                        v5 /* !! */  = ScriptValue.of((boolean)var71_67.tm$2_release(var68_64.asStr()));
                                    } else {
                                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var67_63, (ScriptValue)var68_64, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v5 /* !! */  = ScriptValue.NULL;
                                }
                            }
                        } else {
                            var72_68 = var1_1.getClassOrVar("contraption");
                            if (var72_68 != ScriptValue.NULL) {
                                var73_69 = var32_29;
                                if (var72_68 instanceof ScriptValue.Obj && (var75_71 = (var74_70 = (ScriptValue.Obj)var72_68).instance()) != null && !(var75_71 instanceof PolyClass) && var74_70.typeName().equals("Contraption")) {
                                    var76_72 = new PolyClassContraption(var75_71);
                                    v6 /* !! */  = ScriptValue.of((boolean)var76_72.tm$2_release(var73_69.asStr()));
                                } else {
                                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var72_68, (ScriptValue)var73_69, (ScriptContext)var1_1);
                                }
                            } else {
                                v6 /* !! */  = ScriptValue.NULL;
                            }
                        }
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var52_49, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && var54_51.asNum() >= 3.0 != false)) break block56;
                        v7 /* !! */  = var52_49 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var52_49, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (!(v7 /* !! */ .asNum() >= 0.0)) ** GOTO lbl-1000
                        v8 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum();
                        v9 /* !! */  = var52_49 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var52_49, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (v8 - v9 /* !! */ .asNum() < 10.0) {
                            v10 = true;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v10 = false;
                        }
                        var77_73 = v10;
                        var78_74 = ScriptValue.of((boolean)v10);
                        var0.val("recent", var78_74);
                        var79_75 = var1_1.getClassOrVar("Machine");
                        if (var79_75 != ScriptValue.NULL) {
                            var80_76 = "_psi_extend";
                            var81_77 = "int";
                            var82_78 = ScriptValue.of((double)(var77_73 != false ? 2.0 : 1.0));
                            if (var79_75 instanceof ScriptValue.Obj && (var84_80 = (var83_79 = (ScriptValue.Obj)var79_75).instance()) != null && !(var84_80 instanceof PolyClass) && var83_79.typeName().equals("Machine")) {
                                var85_81 = new PolyClassMachine_v2(var84_80);
                                v11 /* !! */  = ScriptValue.of((boolean)var85_81.tm$82_set_typed(var80_76, var81_77, var82_78));
                            } else {
                                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var79_75, (ScriptValue)ScriptValue.of((String)var80_76), (ScriptValue)ScriptValue.of((String)var81_77), (ScriptValue)var82_78, (ScriptContext)var1_1);
                            }
                        } else {
                            v11 /* !! */  = ScriptValue.NULL;
                        }
                        break block57;
                    }
                    var86_82 = var1_1.getClassOrVar("Machine");
                    if (var86_82 != ScriptValue.NULL) {
                        var87_83 = "_psi_extend";
                        var88_84 = "int";
                        var89_85 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0);
                        if (var86_82 instanceof ScriptValue.Obj && (var91_87 = (var90_86 = (ScriptValue.Obj)var86_82).instance()) != null && !(var91_87 instanceof PolyClass) && var90_86.typeName().equals("Machine")) {
                            var92_88 = new PolyClassMachine_v2(var91_87);
                            v12 /* !! */  = ScriptValue.of((boolean)var92_88.tm$82_set_typed(var87_83, var88_84, var89_85));
                        } else {
                            v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var86_82, (ScriptValue)ScriptValue.of((String)var87_83), (ScriptValue)ScriptValue.of((String)var88_84), (ScriptValue)var89_85, (ScriptContext)var1_1);
                        }
                    } else {
                        v12 /* !! */  = ScriptValue.NULL;
                    }
                    break block57;
                }
                var93_89 = var1_1.getClassOrVar("Machine");
                if (var93_89 != ScriptValue.NULL) {
                    var94_90 = "_psi_keepalive";
                    var95_91 = "int";
                    if (var93_89 instanceof ScriptValue.Obj && (var97_93 = (var96_92 = (ScriptValue.Obj)var93_89).instance()) != null && !(var97_93 instanceof PolyClass) && var96_92.typeName().equals("Machine")) {
                        var98_94 = new PolyClassMachine_v2(var97_93);
                        v13 /* !! */  = var98_94.tm$34_get_typed(var94_90, var95_91);
                    } else {
                        v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var93_89, (ScriptValue)ScriptValue.of((String)var94_90), (ScriptValue)ScriptValue.of((String)var95_91), (ScriptContext)var1_1);
                    }
                } else {
                    v13 /* !! */  = ScriptValue.NULL;
                }
                var99_95 = v13 /* !! */ ;
                var0.val("keepalive", var99_95);
                if (ScriptFormula.valuesEqual((ScriptValue)var99_95, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var100_96 = 0.0;
                    var102_97 = ScriptValue.of((double)0.0);
                    var0.val("keepalive", var102_97);
                }
                if (var1_1.getNum("keepalive") > 0.0) {
                    var103_98 = var1_1.getClassOrVar("Machine");
                    if (var103_98 != ScriptValue.NULL) {
                        var104_99 = "_psi_keepalive";
                        var105_100 = "int";
                        var106_101 = ScriptValue.of((double)(var1_1.getNum("keepalive") - 1.0));
                        if (var103_98 instanceof ScriptValue.Obj && (var108_103 = (var107_102 = (ScriptValue.Obj)var103_98).instance()) != null && !(var108_103 instanceof PolyClass) && var107_102.typeName().equals("Machine")) {
                            var109_104 = new PolyClassMachine_v2(var108_103);
                            v14 /* !! */  = ScriptValue.of((boolean)var109_104.tm$82_set_typed(var104_99, var105_100, var106_101));
                        } else {
                            v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var103_98, (ScriptValue)ScriptValue.of((String)var104_99), (ScriptValue)ScriptValue.of((String)var105_100), (ScriptValue)var106_101, (ScriptContext)var1_1);
                        }
                    } else {
                        v14 /* !! */  = ScriptValue.NULL;
                    }
                } else {
                    var110_105 = var1_1.getClassOrVar("Machine");
                    if (var110_105 != ScriptValue.NULL) {
                        var111_106 = "_psi_uuid";
                        var112_107 = "str";
                        var113_108 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "");
                        if (var110_105 instanceof ScriptValue.Obj && (var115_110 = (var114_109 = (ScriptValue.Obj)var110_105).instance()) != null && !(var115_110 instanceof PolyClass) && var114_109.typeName().equals("Machine")) {
                            var116_111 = new PolyClassMachine_v2(var115_110);
                            v15 /* !! */  = ScriptValue.of((boolean)var116_111.tm$82_set_typed(var111_106, var112_107, var113_108));
                        } else {
                            v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var110_105, (ScriptValue)ScriptValue.of((String)var111_106), (ScriptValue)ScriptValue.of((String)var112_107), (ScriptValue)var113_108, (ScriptContext)var1_1);
                        }
                    } else {
                        v15 /* !! */  = ScriptValue.NULL;
                    }
                }
                if (!(var1_1.getNum("keepalive") > 0.0)) break block58;
                var117_112 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                v16 = var117_112 != null ? var117_112.tg$136_last_transfer_tick() : ((var118_113 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var118_113, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum());
                if (!(v16 >= 0.0)) ** GOTO lbl-1000
                v17 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum();
                var119_114 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                v18 = var119_114 != null ? var119_114.tg$136_last_transfer_tick() : ((var120_115 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var120_115, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum());
                if (v17 - v18 < 10.0) {
                    v19 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v19 = false;
                }
                var121_116 = v19;
                var122_117 = ScriptValue.of((boolean)v19);
                var0.val("recent", var122_117);
                var123_118 = var1_1.getClassOrVar("Machine");
                if (var123_118 != ScriptValue.NULL) {
                    var124_119 = "_psi_extend";
                    var125_120 = "int";
                    var126_121 = ScriptValue.of((double)(var121_116 != false ? 2.0 : 1.0));
                    if (var123_118 instanceof ScriptValue.Obj && (var128_123 = (var127_122 = (ScriptValue.Obj)var123_118).instance()) != null && !(var128_123 instanceof PolyClass) && var127_122.typeName().equals("Machine")) {
                        var129_124 = new PolyClassMachine_v2(var128_123);
                        v20 /* !! */  = ScriptValue.of((boolean)var129_124.tm$82_set_typed(var124_119, var125_120, var126_121));
                    } else {
                        v20 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var123_118, (ScriptValue)ScriptValue.of((String)var124_119), (ScriptValue)ScriptValue.of((String)var125_120), (ScriptValue)var126_121, (ScriptContext)var1_1);
                    }
                } else {
                    v20 /* !! */  = ScriptValue.NULL;
                }
                break block57;
            }
            var130_125 = var1_1.getClassOrVar("Machine");
            if (var130_125 != ScriptValue.NULL) {
                var131_126 = "_psi_extend";
                var132_127 = "int";
                var133_128 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0);
                if (var130_125 instanceof ScriptValue.Obj && (var135_130 = (var134_129 = (ScriptValue.Obj)var130_125).instance()) != null && !(var135_130 instanceof PolyClass) && var134_129.typeName().equals("Machine")) {
                    var136_131 = new PolyClassMachine_v2(var135_130);
                    v21 /* !! */  = ScriptValue.of((boolean)var136_131.tm$82_set_typed(var131_126, var132_127, var133_128));
                } else {
                    v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var130_125, (ScriptValue)ScriptValue.of((String)var131_126), (ScriptValue)ScriptValue.of((String)var132_127), (ScriptValue)var133_128, (ScriptContext)var1_1);
                }
            } else {
                v21 /* !! */  = ScriptValue.NULL;
            }
        }
        PortableStorageInterface.FILE_SCOPE = var0.build();
    }
}
