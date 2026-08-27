/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraptionManager
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassMachine_v4;
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
            PolyClassMachine_v4 polyClassMachine_v4;
            ++n;
            if (!(scriptValue4.asNum() < scriptContext.getNum("MAX_REACH"))) break;
            PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v4 polyClassMachine_v43 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v4 polyClassMachine_v44 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v4 polyClassMachine_v45 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine_v4 polyClassMachine_v46 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("contraption");
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)((polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine_v4.pg$200_x() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine_v42 != null ? polyClassMachine_v42.tg$134_facing_dx() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v43 != null ? polyClassMachine_v43.pg$202_y() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine_v44 != null ? polyClassMachine_v44.tg$130_facing_dy() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine_v45 != null ? polyClassMachine_v45.pg$206_z() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine_v46 != null ? polyClassMachine_v46.tg$132_facing_dz() : ((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v4.tm$34_get_typed(string, string2);
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
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
        block43: {
            block44: {
                block41: {
                    block42: {
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
                        var21_18 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                        if (!(ScriptFormula.valuesEqual((ScriptValue)(var21_18 != null ? var21_18.pg$191_contraption() : ((var22_19 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var22_19, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block41;
                        var23_20 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var25_22 = var23_20 != null ? var23_20.pg$191_contraption() : ((var24_21 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var24_21, (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("contraption", var25_22);
                        var26_23 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var28_25 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var30_27 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var32_29 = ScriptValue.of((String)((var26_23 != null ? var26_23.pg$200_x() : ((var27_24 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var27_24, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var28_25 != null ? var28_25.pg$202_y() : ((var29_26 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var29_26, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var30_27 != null ? var30_27.pg$206_z() : ((var31_28 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var31_28, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr()));
                        var0.val("hold_key", var32_29);
                        var33_30 = var1_1.getClassOrVar("contraption");
                        if (var33_30 != ScriptValue.NULL) {
                            var34_31 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                            v0 /* !! */  = var34_31 != null ? var34_31.pg$133_facing_dx() : ((var35_32 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var35_32, (ScriptContext)var1_1) : ScriptValue.NULL);
                            var36_33 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                            var38_35 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "real_direction", (ScriptValue)var33_30, (ScriptValue)v0 /* !! */ , (ScriptValue)(var36_33 != null ? var36_33.pg$129_facing_dy() : ((var37_34 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var37_34, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var38_35 != null ? var38_35.pg$131_facing_dz() : ((var39_36 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var39_36, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptContext)var1_1);
                        } else {
                            v1 /* !! */  = ScriptValue.NULL;
                        }
                        var40_37 = v1 /* !! */ ;
                        var0.val("real_facing", var40_37);
                        var41_38 = var1_1.getClassOrVar("OPPOSITE");
                        var42_39 = var41_38 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var41_38, (ScriptValue)var40_37, (ScriptValue)var1_1.getClassOrVar("null"), (ScriptContext)var1_1) : ScriptValue.NULL;
                        var0.val("required_facing", var42_39);
                        var43_40 = ScriptContext.builder().copyFrom(var1_1);
                        var43_40.val("contraption", var25_22);
                        var43_40.val("real_facing", var40_37);
                        var43_40.val("required_facing", var42_39);
                        var44_41 = PortableStorageInterface._findPartner(var43_40);
                        var0.val("result", var44_41);
                        var46_43 = ScriptFormula.valuesEqual((ScriptValue)var44_41, (ScriptValue)var1_1.getClassOrVar("null")) != false ? var1_1.getClassOrVar("null") : ((var45_42 = var1_1.getClassOrVar("result")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var45_42, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "target")), (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("found", var46_43);
                        var48_45 = ScriptFormula.valuesEqual((ScriptValue)var44_41, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ScriptValue.of((double)(-1.0)) : ((var47_44 = var1_1.getClassOrVar("result")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var47_44, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "i")), (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("found_i", var48_45);
                        if (ScriptFormula.valuesEqual((ScriptValue)var46_43, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var49_46 = var1_1.getClassOrVar("found");
                            v2 /* !! */  = var49_46 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var49_46, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "_psi_uuid")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "str")), (ScriptValue)(var25_22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var25_22, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1) : ScriptValue.NULL;
                            var50_47 = var1_1.getClassOrVar("found");
                            v3 /* !! */  = var50_47 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var50_47, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "_psi_keepalive")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "int")), (ScriptValue)ScriptValue.of((double)var15_14), (ScriptContext)var1_1) : ScriptValue.NULL;
                            var51_48 = var46_43 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var46_43, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var0.val("last", var51_48);
                            var52_49 = var51_48.asNum() < 0.0 != false ? 0.0 : ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum() - var51_48.asNum();
                            var54_50 = ScriptValue.of((double)var52_49);
                            var0.val("idle_ticks", var54_50);
                            if (var52_49 <= var18_16) {
                                var55_51 = var1_1.getClassOrVar("contraption");
                                v4 /* !! */  = var55_51 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var55_51, (ScriptValue)var32_29, (ScriptContext)var1_1) : ScriptValue.NULL;
                            } else {
                                var56_52 = var1_1.getClassOrVar("contraption");
                                v5 /* !! */  = var56_52 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var56_52, (ScriptValue)var32_29, (ScriptContext)var1_1) : ScriptValue.NULL;
                            }
                        } else {
                            var57_53 = var1_1.getClassOrVar("contraption");
                            v6 /* !! */  = var57_53 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var57_53, (ScriptValue)var32_29, (ScriptContext)var1_1) : ScriptValue.NULL;
                        }
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var46_43, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && var48_45.asNum() >= 3.0 != false)) break block42;
                        v7 /* !! */  = var46_43 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var46_43, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (!(v7 /* !! */ .asNum() >= 0.0)) ** GOTO lbl-1000
                        v8 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum();
                        v9 /* !! */  = var46_43 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var46_43, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (v8 - v9 /* !! */ .asNum() < 10.0) {
                            v10 = true;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v10 = false;
                        }
                        var58_54 = v10;
                        var59_55 = ScriptValue.of((boolean)v10);
                        var0.val("recent", var59_55);
                        var60_56 = var1_1.getClassOrVar("Machine");
                        if (var60_56 != ScriptValue.NULL) {
                            var61_57 = "_psi_extend";
                            var62_58 = "int";
                            var63_59 = ScriptValue.of((double)(var58_54 != false ? 2.0 : 1.0));
                            if (var60_56 instanceof ScriptValue.Obj && (var65_61 = (var64_60 = (ScriptValue.Obj)var60_56).instance()) != null && !(var65_61 instanceof PolyClass) && var64_60.typeName().equals("Machine")) {
                                var66_62 = new PolyClassMachine_v4(var65_61);
                                v11 /* !! */  = ScriptValue.of((boolean)var66_62.tm$82_set_typed(var61_57, var62_58, var63_59));
                            } else {
                                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var60_56, (ScriptValue)ScriptValue.of((String)var61_57), (ScriptValue)ScriptValue.of((String)var62_58), (ScriptValue)var63_59, (ScriptContext)var1_1);
                            }
                        } else {
                            v11 /* !! */  = ScriptValue.NULL;
                        }
                        break block43;
                    }
                    var67_63 = var1_1.getClassOrVar("Machine");
                    if (var67_63 != ScriptValue.NULL) {
                        var68_64 = "_psi_extend";
                        var69_65 = "int";
                        var70_66 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0);
                        if (var67_63 instanceof ScriptValue.Obj && (var72_68 = (var71_67 = (ScriptValue.Obj)var67_63).instance()) != null && !(var72_68 instanceof PolyClass) && var71_67.typeName().equals("Machine")) {
                            var73_69 = new PolyClassMachine_v4(var72_68);
                            v12 /* !! */  = ScriptValue.of((boolean)var73_69.tm$82_set_typed(var68_64, var69_65, var70_66));
                        } else {
                            v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var67_63, (ScriptValue)ScriptValue.of((String)var68_64), (ScriptValue)ScriptValue.of((String)var69_65), (ScriptValue)var70_66, (ScriptContext)var1_1);
                        }
                    } else {
                        v12 /* !! */  = ScriptValue.NULL;
                    }
                    break block43;
                }
                var74_70 = var1_1.getClassOrVar("Machine");
                if (var74_70 != ScriptValue.NULL) {
                    var75_71 = "_psi_keepalive";
                    var76_72 = "int";
                    if (var74_70 instanceof ScriptValue.Obj && (var78_74 = (var77_73 = (ScriptValue.Obj)var74_70).instance()) != null && !(var78_74 instanceof PolyClass) && var77_73.typeName().equals("Machine")) {
                        var79_75 = new PolyClassMachine_v4(var78_74);
                        v13 /* !! */  = var79_75.tm$34_get_typed(var75_71, var76_72);
                    } else {
                        v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var74_70, (ScriptValue)ScriptValue.of((String)var75_71), (ScriptValue)ScriptValue.of((String)var76_72), (ScriptContext)var1_1);
                    }
                } else {
                    v13 /* !! */  = ScriptValue.NULL;
                }
                var80_76 = v13 /* !! */ ;
                var0.val("keepalive", var80_76);
                if (ScriptFormula.valuesEqual((ScriptValue)var80_76, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var81_77 = 0.0;
                    var83_78 = ScriptValue.of((double)0.0);
                    var0.val("keepalive", var83_78);
                }
                if (var1_1.getNum("keepalive") > 0.0) {
                    var84_79 = var1_1.getClassOrVar("Machine");
                    if (var84_79 != ScriptValue.NULL) {
                        var85_80 = "_psi_keepalive";
                        var86_81 = "int";
                        var87_82 = ScriptValue.of((double)(var1_1.getNum("keepalive") - 1.0));
                        if (var84_79 instanceof ScriptValue.Obj && (var89_84 = (var88_83 = (ScriptValue.Obj)var84_79).instance()) != null && !(var89_84 instanceof PolyClass) && var88_83.typeName().equals("Machine")) {
                            var90_85 = new PolyClassMachine_v4(var89_84);
                            v14 /* !! */  = ScriptValue.of((boolean)var90_85.tm$82_set_typed(var85_80, var86_81, var87_82));
                        } else {
                            v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var84_79, (ScriptValue)ScriptValue.of((String)var85_80), (ScriptValue)ScriptValue.of((String)var86_81), (ScriptValue)var87_82, (ScriptContext)var1_1);
                        }
                    } else {
                        v14 /* !! */  = ScriptValue.NULL;
                    }
                } else {
                    var91_86 = var1_1.getClassOrVar("Machine");
                    if (var91_86 != ScriptValue.NULL) {
                        var92_87 = "_psi_uuid";
                        var93_88 = "str";
                        var94_89 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "");
                        if (var91_86 instanceof ScriptValue.Obj && (var96_91 = (var95_90 = (ScriptValue.Obj)var91_86).instance()) != null && !(var96_91 instanceof PolyClass) && var95_90.typeName().equals("Machine")) {
                            var97_92 = new PolyClassMachine_v4(var96_91);
                            v15 /* !! */  = ScriptValue.of((boolean)var97_92.tm$82_set_typed(var92_87, var93_88, var94_89));
                        } else {
                            v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var91_86, (ScriptValue)ScriptValue.of((String)var92_87), (ScriptValue)ScriptValue.of((String)var93_88), (ScriptValue)var94_89, (ScriptContext)var1_1);
                        }
                    } else {
                        v15 /* !! */  = ScriptValue.NULL;
                    }
                }
                if (!(var1_1.getNum("keepalive") > 0.0)) break block44;
                var98_93 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                v16 = var98_93 != null ? var98_93.tg$136_last_transfer_tick() : ((var99_94 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var99_94, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum());
                if (!(v16 >= 0.0)) ** GOTO lbl-1000
                v17 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum();
                var100_95 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                v18 = var100_95 != null ? var100_95.tg$136_last_transfer_tick() : ((var101_96 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var101_96, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum());
                if (v17 - v18 < 10.0) {
                    v19 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v19 = false;
                }
                var102_97 = v19;
                var103_98 = ScriptValue.of((boolean)v19);
                var0.val("recent", var103_98);
                var104_99 = var1_1.getClassOrVar("Machine");
                if (var104_99 != ScriptValue.NULL) {
                    var105_100 = "_psi_extend";
                    var106_101 = "int";
                    var107_102 = ScriptValue.of((double)(var102_97 != false ? 2.0 : 1.0));
                    if (var104_99 instanceof ScriptValue.Obj && (var109_104 = (var108_103 = (ScriptValue.Obj)var104_99).instance()) != null && !(var109_104 instanceof PolyClass) && var108_103.typeName().equals("Machine")) {
                        var110_105 = new PolyClassMachine_v4(var109_104);
                        v20 /* !! */  = ScriptValue.of((boolean)var110_105.tm$82_set_typed(var105_100, var106_101, var107_102));
                    } else {
                        v20 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var104_99, (ScriptValue)ScriptValue.of((String)var105_100), (ScriptValue)ScriptValue.of((String)var106_101), (ScriptValue)var107_102, (ScriptContext)var1_1);
                    }
                } else {
                    v20 /* !! */  = ScriptValue.NULL;
                }
                break block43;
            }
            var111_106 = var1_1.getClassOrVar("Machine");
            if (var111_106 != ScriptValue.NULL) {
                var112_107 = "_psi_extend";
                var113_108 = "int";
                var114_109 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0);
                if (var111_106 instanceof ScriptValue.Obj && (var116_111 = (var115_110 = (ScriptValue.Obj)var111_106).instance()) != null && !(var116_111 instanceof PolyClass) && var115_110.typeName().equals("Machine")) {
                    var117_112 = new PolyClassMachine_v4(var116_111);
                    v21 /* !! */  = ScriptValue.of((boolean)var117_112.tm$82_set_typed(var112_107, var113_108, var114_109));
                } else {
                    v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var111_106, (ScriptValue)ScriptValue.of((String)var112_107), (ScriptValue)ScriptValue.of((String)var113_108), (ScriptValue)var114_109, (ScriptContext)var1_1);
                }
            } else {
                v21 /* !! */  = ScriptValue.NULL;
            }
        }
        PortableStorageInterface.FILE_SCOPE = var0.build();
    }
}
