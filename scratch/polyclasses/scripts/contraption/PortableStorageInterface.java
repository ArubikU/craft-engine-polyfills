/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
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
        while (n < 1000) {
            ScriptValue scriptValue4;
            ScriptValue scriptValue5;
            PolyClassMachine_v2 polyClassMachine_v2;
            PolyClassMachine_v2 polyClassMachine_v22;
            PolyClassMachine_v2 polyClassMachine_v23;
            PolyClassMachine_v2 polyClassMachine_v24;
            PolyClassMachine_v2 polyClassMachine_v25;
            PolyClassMachine_v2 polyClassMachine_v26;
            ScriptValue scriptValue6;
            ++n;
            if (!(scriptContext.getNum("i") < scriptContext.getNum("MAX_REACH"))) break;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("contraption");
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine_v26 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine_v26.pg$200_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)((scriptValue7 != ScriptValue.NULL ? ((polyClassMachine_v25 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassMachine_v25.tg$134_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue7, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptContext.getNum("i")))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(scriptValue8 != ScriptValue.NULL ? ((polyClassMachine_v24 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassMachine_v24.pg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)((scriptValue9 != ScriptValue.NULL ? ((polyClassMachine_v23 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassMachine_v23.tg$130_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue9, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptContext.getNum("i")))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(scriptValue10 != ScriptValue.NULL ? ((polyClassMachine_v22 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue10)) != null ? polyClassMachine_v22.pg$206_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue10, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)((scriptValue11 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassMachine_v2.tg$132_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asNum()) : ScriptValue.NULL.asNum()) * scriptContext.getNum("i")))), (ScriptContext)scriptContext);
            builder.val("check", (ScriptValue)callSite);
            if (ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ((scriptValue5 = scriptContext.getClassOrVar("check")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true && ScriptFormula.valuesEqualStr((ScriptValue)((scriptValue4 = scriptContext.getClassOrVar("check")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"cml:portable_storage_interface")) {
                ScriptValue scriptValue13;
                ScriptValue scriptValue14;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("check");
                ScriptValue scriptValue16 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "machine", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("target", scriptValue16);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue16, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ((scriptValue14 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)((scriptValue13 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "facing")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("required_facing"))) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "target"));
                    arrayList.add(scriptValue16);
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "i"));
                    arrayList.add(scriptContext.getClassOrVar("i"));
                    return ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
                }
            }
            ScriptValue scriptValue17 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 1.0)));
            builder.val("i", scriptValue17);
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
        block45: {
            block46: {
                block43: {
                    block44: {
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
                        var21_18 = var1_1.getClassOrVar("Machine");
                        v0 /* !! */  = var21_18 != ScriptValue.NULL ? ((var22_19 = PolyClassMachine_v2.ofGuarded((ScriptValue)var21_18)) != null ? var22_19.pg$191_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var21_18, (ScriptContext)var1_1)) : ScriptValue.NULL;
                        if (!(ScriptFormula.valuesEqual((ScriptValue)v0 /* !! */ , (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block43;
                        var23_20 = var1_1.getClassOrVar("Machine");
                        var25_22 = var23_20 != ScriptValue.NULL ? ((var24_21 = PolyClassMachine_v2.ofGuarded((ScriptValue)var23_20)) != null ? var24_21.pg$191_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var23_20, (ScriptContext)var1_1)) : ScriptValue.NULL;
                        var0.val("contraption", var25_22);
                        var26_23 = var1_1.getClassOrVar("Machine");
                        var28_25 = var1_1.getClassOrVar("Machine");
                        var30_27 = var1_1.getClassOrVar("Machine");
                        var32_29 = ScriptValue.of((String)((var26_23 != ScriptValue.NULL ? ((var27_24 = PolyClassMachine_v2.ofGuarded((ScriptValue)var26_23)) != null ? var27_24.pg$200_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var26_23, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "," + (var28_25 != ScriptValue.NULL ? ((var29_26 = PolyClassMachine_v2.ofGuarded((ScriptValue)var28_25)) != null ? var29_26.pg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var28_25, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "," + (var30_27 != ScriptValue.NULL ? ((var31_28 = PolyClassMachine_v2.ofGuarded((ScriptValue)var30_27)) != null ? var31_28.pg$206_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var30_27, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr()));
                        var0.val("hold_key", var32_29);
                        var33_30 = var1_1.getClassOrVar("contraption");
                        if (var33_30 != ScriptValue.NULL) {
                            var36_33 = var1_1.getClassOrVar("Machine");
                            var38_35 = var1_1.getClassOrVar("Machine");
                            var34_31 = var1_1.getClassOrVar("Machine");
                            v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "real_direction", (ScriptValue)var33_30, (ScriptValue)(var34_31 != ScriptValue.NULL ? ((var35_32 = PolyClassMachine_v2.ofGuarded((ScriptValue)var34_31)) != null ? var35_32.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var34_31, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var36_33 != ScriptValue.NULL ? ((var37_34 = PolyClassMachine_v2.ofGuarded((ScriptValue)var36_33)) != null ? var37_34.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var36_33, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var38_35 != ScriptValue.NULL ? ((var39_36 = PolyClassMachine_v2.ofGuarded((ScriptValue)var38_35)) != null ? var39_36.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var38_35, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptContext)var1_1);
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
                            v2 /* !! */  = var49_46 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var49_46, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "_psi_uuid")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "str")), (ScriptValue)((var50_47 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var50_47, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1) : ScriptValue.NULL;
                            var51_48 = var1_1.getClassOrVar("found");
                            v3 /* !! */  = var51_48 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var51_48, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "_psi_keepalive")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "int")), (ScriptValue)ScriptValue.of((double)var15_14), (ScriptContext)var1_1) : ScriptValue.NULL;
                            var52_49 = var1_1.getClassOrVar("found");
                            var53_50 = var52_49 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var52_49, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var0.val("last", var53_50);
                            if (var53_50.asNum() < 0.0) {
                                v4 = 0.0;
                            } else {
                                var54_51 = new ArrayList<E>();
                                v4 = ScriptFormula.callBuiltin((String)"tick", var54_51, (ScriptContext)var1_1).asNum() - var53_50.asNum();
                            }
                            var55_52 = v4;
                            var57_53 = ScriptValue.of((double)v4);
                            var0.val("idle_ticks", var57_53);
                            if (var55_52 <= var18_16) {
                                var58_54 = var1_1.getClassOrVar("contraption");
                                v5 /* !! */  = var58_54 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var58_54, (ScriptValue)var32_29, (ScriptContext)var1_1) : ScriptValue.NULL;
                            } else {
                                var59_55 = var1_1.getClassOrVar("contraption");
                                v6 /* !! */  = var59_55 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var59_55, (ScriptValue)var32_29, (ScriptContext)var1_1) : ScriptValue.NULL;
                            }
                        } else {
                            var60_56 = var1_1.getClassOrVar("contraption");
                            v7 /* !! */  = var60_56 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var60_56, (ScriptValue)var32_29, (ScriptContext)var1_1) : ScriptValue.NULL;
                        }
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var46_43, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && var48_45.asNum() >= 3.0 != false)) break block44;
                        var61_57 = var1_1.getClassOrVar("found");
                        v8 /* !! */  = var61_57 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var61_57, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (!(v8 /* !! */ .asNum() >= 0.0)) ** GOTO lbl-1000
                        var62_58 = new ArrayList<E>();
                        v9 = ScriptFormula.callBuiltin((String)"tick", var62_58, (ScriptContext)var1_1).asNum();
                        var63_59 = var1_1.getClassOrVar("found");
                        v10 /* !! */  = var63_59 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var63_59, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (v9 - v10 /* !! */ .asNum() < 10.0) {
                            v11 = true;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v11 = false;
                        }
                        var64_60 = v11;
                        var65_61 = ScriptValue.of((boolean)v11);
                        var0.val("recent", var65_61);
                        var66_62 = var1_1.getClassOrVar("Machine");
                        if (var66_62 != ScriptValue.NULL) {
                            var67_63 = "_psi_extend";
                            var68_64 = "int";
                            var69_65 = ScriptValue.of((double)(var64_60 != false ? 2.0 : 1.0));
                            if (var66_62 instanceof ScriptValue.Obj && (var71_67 = (var70_66 = (ScriptValue.Obj)var66_62).instance()) != null && !(var71_67 instanceof PolyClass) && var70_66.typeName().equals("Machine")) {
                                var72_68 = new PolyClassMachine_v2(var71_67);
                                v12 /* !! */  = ScriptValue.of((boolean)var72_68.tm$82_set_typed(var67_63, var68_64, var69_65));
                            } else {
                                v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var66_62, (ScriptValue)ScriptValue.of((String)var67_63), (ScriptValue)ScriptValue.of((String)var68_64), (ScriptValue)var69_65, (ScriptContext)var1_1);
                            }
                        } else {
                            v12 /* !! */  = ScriptValue.NULL;
                        }
                        break block45;
                    }
                    var73_69 = var1_1.getClassOrVar("Machine");
                    if (var73_69 != ScriptValue.NULL) {
                        var74_70 = "_psi_extend";
                        var75_71 = "int";
                        var76_72 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0);
                        if (var73_69 instanceof ScriptValue.Obj && (var78_74 = (var77_73 = (ScriptValue.Obj)var73_69).instance()) != null && !(var78_74 instanceof PolyClass) && var77_73.typeName().equals("Machine")) {
                            var79_75 = new PolyClassMachine_v2(var78_74);
                            v13 /* !! */  = ScriptValue.of((boolean)var79_75.tm$82_set_typed(var74_70, var75_71, var76_72));
                        } else {
                            v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var73_69, (ScriptValue)ScriptValue.of((String)var74_70), (ScriptValue)ScriptValue.of((String)var75_71), (ScriptValue)var76_72, (ScriptContext)var1_1);
                        }
                    } else {
                        v13 /* !! */  = ScriptValue.NULL;
                    }
                    break block45;
                }
                var80_76 = var1_1.getClassOrVar("Machine");
                if (var80_76 != ScriptValue.NULL) {
                    var81_77 = "_psi_keepalive";
                    var82_78 = "int";
                    if (var80_76 instanceof ScriptValue.Obj && (var84_80 = (var83_79 = (ScriptValue.Obj)var80_76).instance()) != null && !(var84_80 instanceof PolyClass) && var83_79.typeName().equals("Machine")) {
                        var85_81 = new PolyClassMachine_v2(var84_80);
                        v14 /* !! */  = var85_81.tm$34_get_typed(var81_77, var82_78);
                    } else {
                        v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var80_76, (ScriptValue)ScriptValue.of((String)var81_77), (ScriptValue)ScriptValue.of((String)var82_78), (ScriptContext)var1_1);
                    }
                } else {
                    v14 /* !! */  = ScriptValue.NULL;
                }
                var86_82 = v14 /* !! */ ;
                var0.val("keepalive", var86_82);
                if (ScriptFormula.valuesEqual((ScriptValue)var86_82, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var87_83 = 0.0;
                    var89_84 = ScriptValue.of((double)0.0);
                    var0.val("keepalive", var89_84);
                }
                if (var1_1.getNum("keepalive") > 0.0) {
                    var90_85 = var1_1.getClassOrVar("Machine");
                    if (var90_85 != ScriptValue.NULL) {
                        var91_86 = "_psi_keepalive";
                        var92_87 = "int";
                        var93_88 = ScriptValue.of((double)(var1_1.getNum("keepalive") - 1.0));
                        if (var90_85 instanceof ScriptValue.Obj && (var95_90 = (var94_89 = (ScriptValue.Obj)var90_85).instance()) != null && !(var95_90 instanceof PolyClass) && var94_89.typeName().equals("Machine")) {
                            var96_91 = new PolyClassMachine_v2(var95_90);
                            v15 /* !! */  = ScriptValue.of((boolean)var96_91.tm$82_set_typed(var91_86, var92_87, var93_88));
                        } else {
                            v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var90_85, (ScriptValue)ScriptValue.of((String)var91_86), (ScriptValue)ScriptValue.of((String)var92_87), (ScriptValue)var93_88, (ScriptContext)var1_1);
                        }
                    } else {
                        v15 /* !! */  = ScriptValue.NULL;
                    }
                } else {
                    var97_92 = var1_1.getClassOrVar("Machine");
                    if (var97_92 != ScriptValue.NULL) {
                        var98_93 = "_psi_uuid";
                        var99_94 = "str";
                        var100_95 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "");
                        if (var97_92 instanceof ScriptValue.Obj && (var102_97 = (var101_96 = (ScriptValue.Obj)var97_92).instance()) != null && !(var102_97 instanceof PolyClass) && var101_96.typeName().equals("Machine")) {
                            var103_98 = new PolyClassMachine_v2(var102_97);
                            v16 /* !! */  = ScriptValue.of((boolean)var103_98.tm$82_set_typed(var98_93, var99_94, var100_95));
                        } else {
                            v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var97_92, (ScriptValue)ScriptValue.of((String)var98_93), (ScriptValue)ScriptValue.of((String)var99_94), (ScriptValue)var100_95, (ScriptContext)var1_1);
                        }
                    } else {
                        v16 /* !! */  = ScriptValue.NULL;
                    }
                }
                if (!(var1_1.getNum("keepalive") > 0.0)) break block46;
                var104_99 = var1_1.getClassOrVar("Machine");
                v17 = var104_99 != ScriptValue.NULL ? ((var105_100 = PolyClassMachine_v2.ofGuarded((ScriptValue)var104_99)) != null ? var105_100.tg$136_last_transfer_tick() : PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var104_99, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (!(v17 >= 0.0)) ** GOTO lbl-1000
                var106_101 = new ArrayList<E>();
                v18 = ScriptFormula.callBuiltin((String)"tick", var106_101, (ScriptContext)var1_1).asNum();
                var107_102 = var1_1.getClassOrVar("Machine");
                v19 = var107_102 != ScriptValue.NULL ? ((var108_103 = PolyClassMachine_v2.ofGuarded((ScriptValue)var107_102)) != null ? var108_103.tg$136_last_transfer_tick() : PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var107_102, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (v18 - v19 < 10.0) {
                    v20 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v20 = false;
                }
                var109_104 = v20;
                var110_105 = ScriptValue.of((boolean)v20);
                var0.val("recent", var110_105);
                var111_106 = var1_1.getClassOrVar("Machine");
                if (var111_106 != ScriptValue.NULL) {
                    var112_107 = "_psi_extend";
                    var113_108 = "int";
                    var114_109 = ScriptValue.of((double)(var109_104 != false ? 2.0 : 1.0));
                    if (var111_106 instanceof ScriptValue.Obj && (var116_111 = (var115_110 = (ScriptValue.Obj)var111_106).instance()) != null && !(var116_111 instanceof PolyClass) && var115_110.typeName().equals("Machine")) {
                        var117_112 = new PolyClassMachine_v2(var116_111);
                        v21 /* !! */  = ScriptValue.of((boolean)var117_112.tm$82_set_typed(var112_107, var113_108, var114_109));
                    } else {
                        v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var111_106, (ScriptValue)ScriptValue.of((String)var112_107), (ScriptValue)ScriptValue.of((String)var113_108), (ScriptValue)var114_109, (ScriptContext)var1_1);
                    }
                } else {
                    v21 /* !! */  = ScriptValue.NULL;
                }
                break block45;
            }
            var118_113 = var1_1.getClassOrVar("Machine");
            if (var118_113 != ScriptValue.NULL) {
                var119_114 = "_psi_extend";
                var120_115 = "int";
                var121_116 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0);
                if (var118_113 instanceof ScriptValue.Obj && (var123_118 = (var122_117 = (ScriptValue.Obj)var118_113).instance()) != null && !(var123_118 instanceof PolyClass) && var122_117.typeName().equals("Machine")) {
                    var124_119 = new PolyClassMachine_v2(var123_118);
                    v22 /* !! */  = ScriptValue.of((boolean)var124_119.tm$82_set_typed(var119_114, var120_115, var121_116));
                } else {
                    v22 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var118_113, (ScriptValue)ScriptValue.of((String)var119_114), (ScriptValue)ScriptValue.of((String)var120_115), (ScriptValue)var121_116, (ScriptContext)var1_1);
                }
            } else {
                v22 /* !! */  = ScriptValue.NULL;
            }
        }
        PortableStorageInterface.FILE_SCOPE = var0.build();
    }
}
