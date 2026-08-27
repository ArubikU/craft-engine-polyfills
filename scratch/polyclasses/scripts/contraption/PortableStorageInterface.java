/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContraption
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
import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassContraptionManager;
import dev.arubik.craftengine.script.PolyClassMachine;
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
            PolyClassMachine polyClassMachine;
            ++n;
            if (!(scriptValue4.asNum() < scriptContext.getNum("MAX_REACH"))) break;
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine polyClassMachine5 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            PolyClassMachine polyClassMachine6 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("contraption");
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)((polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine.pg$200_x() : ((scriptValue11 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine2 != null ? polyClassMachine2.tg$134_facing_dx() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine3 != null ? polyClassMachine3.pg$202_y() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine4 != null ? polyClassMachine4.tg$130_facing_dy() : ((scriptValue8 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(polyClassMachine5 != null ? polyClassMachine5.pg$206_z() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((double)((polyClassMachine6 != null ? polyClassMachine6.tg$132_facing_dz() : ((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asNum() : ScriptValue.NULL.asNum())) * scriptValue4.asNum()))), (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                object2 = polyClassMachine.tm$34_get_typed(string, string2);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
                        var21_18 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                        if (!(ScriptFormula.valuesEqual((ScriptValue)(var21_18 != null ? var21_18.pg$191_contraption() : ((var22_19 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var22_19, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block55;
                        var23_20 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var25_22 = var23_20 != null ? var23_20.pg$191_contraption() : ((var24_21 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var24_21, (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("contraption", var25_22);
                        var26_23 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var28_25 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var30_27 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                        var32_29 = ScriptValue.of((String)((var26_23 != null ? var26_23.pg$200_x() : ((var27_24 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var27_24, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var28_25 != null ? var28_25.pg$202_y() : ((var29_26 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var29_26, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var30_27 != null ? var30_27.pg$206_z() : ((var31_28 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var31_28, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr()));
                        var0.val("hold_key", var32_29);
                        if (var25_22 != ScriptValue.NULL) {
                            var34_30 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                            var33_32 = var34_30 != null ? var34_30.pg$133_facing_dx() : ((var35_31 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var35_31, (ScriptContext)var1_1) : ScriptValue.NULL);
                            var37_33 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                            var36_35 = var37_33 != null ? var37_33.pg$129_facing_dy() : ((var38_34 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var38_34, (ScriptContext)var1_1) : ScriptValue.NULL);
                            var40_36 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                            v0 /* !! */  = var40_36 != null ? var40_36.pg$131_facing_dz() : (var39_38 = (var41_37 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var41_37, (ScriptContext)var1_1) : ScriptValue.NULL);
                            if (var25_22 instanceof ScriptValue.Obj && (var43_40 = (var42_39 = (ScriptValue.Obj)var25_22).instance()) != null && !(var43_40 instanceof PolyClass) && var42_39.typeName().equals("Contraption")) {
                                var44_41 = new PolyClassContraption(var43_40);
                                v1 /* !! */  = var44_41.tm$50_real_direction(var33_32.asNum(), var36_35.asNum(), var39_38.asNum());
                            } else {
                                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "real_direction", (ScriptValue)var25_22, (ScriptValue)var33_32, (ScriptValue)var36_35, (ScriptValue)var39_38, (ScriptContext)var1_1);
                            }
                        } else {
                            v1 /* !! */  = ScriptValue.NULL;
                        }
                        var45_42 = v1 /* !! */ ;
                        var0.val("real_facing", var45_42);
                        var46_43 = var3_3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)var3_3, (ScriptValue)var45_42, (ScriptValue)var1_1.getClassOrVar("null"), (ScriptContext)var1_1) : ScriptValue.NULL;
                        var0.val("required_facing", var46_43);
                        var47_44 = ScriptContext.builder().copyFrom(var1_1);
                        var47_44.val("contraption", var25_22);
                        var47_44.val("real_facing", var45_42);
                        var47_44.val("required_facing", var46_43);
                        var48_45 = PortableStorageInterface._findPartner(var47_44);
                        var0.val("result", var48_45);
                        var49_46 = ScriptFormula.valuesEqual((ScriptValue)var48_45, (ScriptValue)var1_1.getClassOrVar("null")) != false ? var1_1.getClassOrVar("null") : (var48_45 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var48_45, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "target")), (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("found", var49_46);
                        var50_47 = ScriptFormula.valuesEqual((ScriptValue)var48_45, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ScriptValue.of((double)(-1.0)) : (var48_45 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var48_45, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "i")), (ScriptContext)var1_1) : ScriptValue.NULL);
                        var0.val("found_i", var50_47);
                        if (ScriptFormula.valuesEqual((ScriptValue)var49_46, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            v2 /* !! */  = var49_46 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var49_46, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "_psi_uuid")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "str")), (ScriptValue)(var25_22 != ScriptValue.NULL ? ((var51_48 = PolyClassContraption.ofGuarded((ScriptValue)var25_22)) != null ? var51_48.pg$64_uuid() : PolyDispatch.bootstrapGet("memberGet", "uuid", (ScriptValue)var25_22, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptContext)var1_1) : ScriptValue.NULL;
                            v3 /* !! */  = var49_46 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var49_46, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "_psi_keepalive")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "int")), (ScriptValue)ScriptValue.of((double)var15_14), (ScriptContext)var1_1) : ScriptValue.NULL;
                            var52_49 = var49_46 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var49_46, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var0.val("last", var52_49);
                            var53_50 = var52_49.asNum() < 0.0 != false ? 0.0 : ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum() - var52_49.asNum();
                            var55_51 = ScriptValue.of((double)var53_50);
                            var0.val("idle_ticks", var55_51);
                            if (var53_50 <= var18_16) {
                                if (var25_22 != ScriptValue.NULL) {
                                    var56_52 = var32_29;
                                    if (var25_22 instanceof ScriptValue.Obj && (var58_54 = (var57_53 = (ScriptValue.Obj)var25_22).instance()) != null && !(var58_54 instanceof PolyClass) && var57_53.typeName().equals("Contraption")) {
                                        var59_55 = new PolyClassContraption(var58_54);
                                        v4 /* !! */  = ScriptValue.of((boolean)var59_55.tm$12_hold(var56_52.asStr()));
                                    } else {
                                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var25_22, (ScriptValue)var56_52, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v4 /* !! */  = ScriptValue.NULL;
                                }
                            } else if (var25_22 != ScriptValue.NULL) {
                                var60_56 = var32_29;
                                if (var25_22 instanceof ScriptValue.Obj && (var62_58 = (var61_57 = (ScriptValue.Obj)var25_22).instance()) != null && !(var62_58 instanceof PolyClass) && var61_57.typeName().equals("Contraption")) {
                                    var63_59 = new PolyClassContraption(var62_58);
                                    v5 /* !! */  = ScriptValue.of((boolean)var63_59.tm$2_release(var60_56.asStr()));
                                } else {
                                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var25_22, (ScriptValue)var60_56, (ScriptContext)var1_1);
                                }
                            } else {
                                v5 /* !! */  = ScriptValue.NULL;
                            }
                        } else if (var25_22 != ScriptValue.NULL) {
                            var64_60 = var32_29;
                            if (var25_22 instanceof ScriptValue.Obj && (var66_62 = (var65_61 = (ScriptValue.Obj)var25_22).instance()) != null && !(var66_62 instanceof PolyClass) && var65_61.typeName().equals("Contraption")) {
                                var67_63 = new PolyClassContraption(var66_62);
                                v6 /* !! */  = ScriptValue.of((boolean)var67_63.tm$2_release(var64_60.asStr()));
                            } else {
                                v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var25_22, (ScriptValue)var64_60, (ScriptContext)var1_1);
                            }
                        } else {
                            v6 /* !! */  = ScriptValue.NULL;
                        }
                        if (!((ScriptFormula.valuesEqual((ScriptValue)var49_46, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) != false && var50_47.asNum() >= 3.0 != false)) break block56;
                        v7 /* !! */  = var49_46 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var49_46, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (!(v7 /* !! */ .asNum() >= 0.0)) ** GOTO lbl-1000
                        v8 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum();
                        v9 /* !! */  = var49_46 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var49_46, (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (v8 - v9 /* !! */ .asNum() < 10.0) {
                            v10 = true;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v10 = false;
                        }
                        var68_64 = v10;
                        var69_65 = ScriptValue.of((boolean)v10);
                        var0.val("recent", var69_65);
                        var70_66 = var1_1.getClassOrVar("Machine");
                        if (var70_66 != ScriptValue.NULL) {
                            var71_67 = "_psi_extend";
                            var72_68 = "int";
                            var73_69 = ScriptValue.of((double)(var68_64 != false ? 2.0 : 1.0));
                            if (var70_66 instanceof ScriptValue.Obj && (var75_71 = (var74_70 = (ScriptValue.Obj)var70_66).instance()) != null && !(var75_71 instanceof PolyClass) && var74_70.typeName().equals("Machine")) {
                                var76_72 = new PolyClassMachine(var75_71);
                                v11 /* !! */  = ScriptValue.of((boolean)var76_72.tm$82_set_typed(var71_67, var72_68, var73_69));
                            } else {
                                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var70_66, (ScriptValue)ScriptValue.of((String)var71_67), (ScriptValue)ScriptValue.of((String)var72_68), (ScriptValue)var73_69, (ScriptContext)var1_1);
                            }
                        } else {
                            v11 /* !! */  = ScriptValue.NULL;
                        }
                        break block57;
                    }
                    var77_73 = var1_1.getClassOrVar("Machine");
                    if (var77_73 != ScriptValue.NULL) {
                        var78_74 = "_psi_extend";
                        var79_75 = "int";
                        var80_76 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0);
                        if (var77_73 instanceof ScriptValue.Obj && (var82_78 = (var81_77 = (ScriptValue.Obj)var77_73).instance()) != null && !(var82_78 instanceof PolyClass) && var81_77.typeName().equals("Machine")) {
                            var83_79 = new PolyClassMachine(var82_78);
                            v12 /* !! */  = ScriptValue.of((boolean)var83_79.tm$82_set_typed(var78_74, var79_75, var80_76));
                        } else {
                            v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var77_73, (ScriptValue)ScriptValue.of((String)var78_74), (ScriptValue)ScriptValue.of((String)var79_75), (ScriptValue)var80_76, (ScriptContext)var1_1);
                        }
                    } else {
                        v12 /* !! */  = ScriptValue.NULL;
                    }
                    break block57;
                }
                var84_80 = var1_1.getClassOrVar("Machine");
                if (var84_80 != ScriptValue.NULL) {
                    var85_81 = "_psi_keepalive";
                    var86_82 = "int";
                    if (var84_80 instanceof ScriptValue.Obj && (var88_84 = (var87_83 = (ScriptValue.Obj)var84_80).instance()) != null && !(var88_84 instanceof PolyClass) && var87_83.typeName().equals("Machine")) {
                        var89_85 = new PolyClassMachine(var88_84);
                        v13 /* !! */  = var89_85.tm$34_get_typed(var85_81, var86_82);
                    } else {
                        v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var84_80, (ScriptValue)ScriptValue.of((String)var85_81), (ScriptValue)ScriptValue.of((String)var86_82), (ScriptContext)var1_1);
                    }
                } else {
                    v13 /* !! */  = ScriptValue.NULL;
                }
                var90_86 = v13 /* !! */ ;
                var0.val("keepalive", var90_86);
                if (ScriptFormula.valuesEqual((ScriptValue)var90_86, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var91_87 = 0.0;
                    var93_88 = ScriptValue.of((double)0.0);
                    var0.val("keepalive", var93_88);
                }
                if (var1_1.getNum("keepalive") > 0.0) {
                    var94_89 = var1_1.getClassOrVar("Machine");
                    if (var94_89 != ScriptValue.NULL) {
                        var95_90 = "_psi_keepalive";
                        var96_91 = "int";
                        var97_92 = ScriptValue.of((double)(var1_1.getNum("keepalive") - 1.0));
                        if (var94_89 instanceof ScriptValue.Obj && (var99_94 = (var98_93 = (ScriptValue.Obj)var94_89).instance()) != null && !(var99_94 instanceof PolyClass) && var98_93.typeName().equals("Machine")) {
                            var100_95 = new PolyClassMachine(var99_94);
                            v14 /* !! */  = ScriptValue.of((boolean)var100_95.tm$82_set_typed(var95_90, var96_91, var97_92));
                        } else {
                            v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var94_89, (ScriptValue)ScriptValue.of((String)var95_90), (ScriptValue)ScriptValue.of((String)var96_91), (ScriptValue)var97_92, (ScriptContext)var1_1);
                        }
                    } else {
                        v14 /* !! */  = ScriptValue.NULL;
                    }
                } else {
                    var101_96 = var1_1.getClassOrVar("Machine");
                    if (var101_96 != ScriptValue.NULL) {
                        var102_97 = "_psi_uuid";
                        var103_98 = "str";
                        var104_99 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", PortableStorageInterface.class, "");
                        if (var101_96 instanceof ScriptValue.Obj && (var106_101 = (var105_100 = (ScriptValue.Obj)var101_96).instance()) != null && !(var106_101 instanceof PolyClass) && var105_100.typeName().equals("Machine")) {
                            var107_102 = new PolyClassMachine(var106_101);
                            v15 /* !! */  = ScriptValue.of((boolean)var107_102.tm$82_set_typed(var102_97, var103_98, var104_99));
                        } else {
                            v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var101_96, (ScriptValue)ScriptValue.of((String)var102_97), (ScriptValue)ScriptValue.of((String)var103_98), (ScriptValue)var104_99, (ScriptContext)var1_1);
                        }
                    } else {
                        v15 /* !! */  = ScriptValue.NULL;
                    }
                }
                if (!(var1_1.getNum("keepalive") > 0.0)) break block58;
                var108_103 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                v16 = var108_103 != null ? var108_103.tg$136_last_transfer_tick() : ((var109_104 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var109_104, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum());
                if (!(v16 >= 0.0)) ** GOTO lbl-1000
                v17 = ScriptFormula.callBuiltin0((String)"tick", (ScriptContext)var1_1).asNum();
                var110_105 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                v18 = var110_105 != null ? var110_105.tg$136_last_transfer_tick() : ((var111_106 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "last_transfer_tick", (ScriptValue)var111_106, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum());
                if (v17 - v18 < 10.0) {
                    v19 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v19 = false;
                }
                var112_107 = v19;
                var113_108 = ScriptValue.of((boolean)v19);
                var0.val("recent", var113_108);
                var114_109 = var1_1.getClassOrVar("Machine");
                if (var114_109 != ScriptValue.NULL) {
                    var115_110 = "_psi_extend";
                    var116_111 = "int";
                    var117_112 = ScriptValue.of((double)(var112_107 != false ? 2.0 : 1.0));
                    if (var114_109 instanceof ScriptValue.Obj && (var119_114 = (var118_113 = (ScriptValue.Obj)var114_109).instance()) != null && !(var119_114 instanceof PolyClass) && var118_113.typeName().equals("Machine")) {
                        var120_115 = new PolyClassMachine(var119_114);
                        v20 /* !! */  = ScriptValue.of((boolean)var120_115.tm$82_set_typed(var115_110, var116_111, var117_112));
                    } else {
                        v20 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var114_109, (ScriptValue)ScriptValue.of((String)var115_110), (ScriptValue)ScriptValue.of((String)var116_111), (ScriptValue)var117_112, (ScriptContext)var1_1);
                    }
                } else {
                    v20 /* !! */  = ScriptValue.NULL;
                }
                break block57;
            }
            var121_116 = var1_1.getClassOrVar("Machine");
            if (var121_116 != ScriptValue.NULL) {
                var122_117 = "_psi_extend";
                var123_118 = "int";
                var124_119 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", PortableStorageInterface.class, 0.0);
                if (var121_116 instanceof ScriptValue.Obj && (var126_121 = (var125_120 = (ScriptValue.Obj)var121_116).instance()) != null && !(var126_121 instanceof PolyClass) && var125_120.typeName().equals("Machine")) {
                    var127_122 = new PolyClassMachine(var126_121);
                    v21 /* !! */  = ScriptValue.of((boolean)var127_122.tm$82_set_typed(var122_117, var123_118, var124_119));
                } else {
                    v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var121_116, (ScriptValue)ScriptValue.of((String)var122_117), (ScriptValue)ScriptValue.of((String)var123_118), (ScriptValue)var124_119, (ScriptContext)var1_1);
                }
            } else {
                v21 /* !! */  = ScriptValue.NULL;
            }
        }
        PortableStorageInterface.FILE_SCOPE = var0.build();
    }
}
