/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.kinetics;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class BeltUtils {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _faceOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("face").equals("north")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            arrayList.add(ScriptValue.of((double)(-1.0)));
            return new ScriptValue.Array(arrayList);
        }
        if (scriptContext.getStr("face").equals("south")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0));
            return new ScriptValue.Array(arrayList);
        }
        if (scriptContext.getStr("face").equals("east")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            return new ScriptValue.Array(arrayList);
        }
        if (scriptContext.getStr("face").equals("west")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)(-1.0)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            return new ScriptValue.Array(arrayList);
        }
        if (scriptContext.getStr("face").equals("up")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            return new ScriptValue.Array(arrayList);
        }
        if (scriptContext.getStr("face").equals("down")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            arrayList.add(ScriptValue.of((double)(-1.0)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _itemMatches(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("item"));
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", BeltUtils.class, 0);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", BeltUtils.class, 1);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("validator"));
        if (ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin((String)"type_of", arrayList2, (ScriptContext)scriptContext), (String)"str")) {
            ScriptValue scriptValue = scriptContext.getClassOrVar("item");
            return scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "matches", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("item");
        return scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "same_as", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue beltTake(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = ScriptContext.builder().copyFrom(var1_1);
        var2_2.val("face", var1_1.getClassOrVar("face"));
        var3_3 = BeltUtils._faceOffset(var2_2);
        var0.val("off", var3_3);
        var4_4 = var1_1.getClassOrVar("Machine");
        if (var4_4 != ScriptValue.NULL) {
            var5_5 = ScriptFormula.subscriptGet((ScriptValue)var3_3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0)));
            var6_6 = ScriptFormula.subscriptGet((ScriptValue)var3_3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            var7_7 = ScriptFormula.subscriptGet((ScriptValue)var3_3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 2.0)));
            if (var4_4 instanceof ScriptValue.Obj && (var9_9 = (var8_8 = (ScriptValue.Obj)var4_4).instance()) != null && !(var9_9 instanceof PolyClass) && var8_8.typeName().equals("Machine")) {
                var10_10 = new PolyClassMachine_v2(var9_9);
                v0 /* !! */  = var10_10.tm$62_belt_at(var5_5.asNum(), var6_6.asNum(), var7_7.asNum());
            } else {
                v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)var4_4, (ScriptValue)var5_5, (ScriptValue)var6_6, (ScriptValue)var7_7, (ScriptContext)var1_1);
            }
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var11_11 = v0 /* !! */ ;
        var0.val("b", var11_11);
        var12_12 = var1_1.getClassOrVar("b");
        if ((var12_12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var12_12, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) ** GOTO lbl-1000
        var13_13 = var1_1.getClassOrVar("b");
        v1 /* !! */  = var13_13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "progress", (ScriptValue)var13_13, (ScriptContext)var1_1) : ScriptValue.NULL;
        if (!(v1 /* !! */ .asNum() < var1_1.getNum("BELT_TAKE_STALL_THRESHOLD"))) {
            v2 = false;
        } else lbl-1000:
        // 2 sources

        {
            v2 = true;
        }
        if (v2) {
            var14_14 = var1_1.getClassOrVar("Item");
            if (var14_14 != ScriptValue.NULL) {
                var15_15 = new ArrayList<ScriptValue>();
                var15_15.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BeltUtils.class, "minecraft:air"));
                v3 /* !! */  = var14_14 instanceof ScriptValue.Obj && (var17_17 = (var16_16 = (ScriptValue.Obj)var14_14).instance()) != null && !(var17_17 instanceof PolyClass) && var16_16.typeName().equals("Item") ? new PolyClassItem(var17_17).um$21_create(var15_15) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var14_14, var15_15, (ScriptContext)var1_1);
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            return v3 /* !! */ ;
        }
        var18_18 = var1_1.getClassOrVar("b");
        var19_19 = var18_18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var18_18, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("carried", var19_19);
        var20_20 = ScriptContext.builder().copyFrom(var1_1);
        var20_20.val("item", var19_19);
        var20_20.val("validator", var1_1.getClassOrVar("validator"));
        if (BeltUtils._itemMatches(var20_20).asBool() ^ true) {
            var21_21 = var1_1.getClassOrVar("Item");
            if (var21_21 != ScriptValue.NULL) {
                var22_22 = new ArrayList<ScriptValue>();
                var22_22.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BeltUtils.class, "minecraft:air"));
                v4 /* !! */  = var21_21 instanceof ScriptValue.Obj && (var24_24 = (var23_23 = (ScriptValue.Obj)var21_21).instance()) != null && !(var24_24 instanceof PolyClass) && var23_23.typeName().equals("Item") ? new PolyClassItem(var24_24).um$21_create(var22_22) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var21_21, var22_22, (ScriptContext)var1_1);
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            return v4 /* !! */ ;
        }
        var25_25 = var1_1.getClassOrVar("b");
        var26_26 = var25_25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var25_25, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("taken", var26_26);
        if (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("amount"), (ScriptValue)var1_1.getClassOrVar("null"))) {
            var27_27 = new ArrayList<ScriptValue>();
            var27_27.add(var26_26);
            v5 = ScriptFormula.callBuiltin((String)"item_count", var27_27, (ScriptContext)var1_1);
        } else {
            v5 = var1_1.getClassOrVar("amount");
        }
        var28_28 = v5;
        var0.val("want", var28_28);
        v6 = var28_28.asNum();
        var29_29 = new ArrayList<ScriptValue>();
        var29_29.add(var26_26);
        if (v6 < ScriptFormula.callBuiltin((String)"item_count", var29_29, (ScriptContext)var1_1).asNum()) {
            var30_30 = new ArrayList<ScriptValue>();
            var31_31 = var1_1.getClassOrVar("taken");
            var30_30.add((ScriptValue)(var31_31 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var31_31, (ScriptContext)var1_1) : ScriptValue.NULL));
            var32_32 = new ArrayList<ScriptValue>();
            var32_32.add(var26_26);
            var30_30.add(ScriptValue.of((double)(ScriptFormula.callBuiltin((String)"item_count", var32_32, (ScriptContext)var1_1).asNum() - var28_28.asNum())));
            var33_33 = ScriptFormula.callBuiltin((String)"MinecraftItem", var30_30, (ScriptContext)var1_1);
            var0.val("remainder", var33_33);
            var34_34 = var1_1.getClassOrVar("b");
            v7 /* !! */  = var34_34 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var34_34, (ScriptValue)var33_33, (ScriptContext)var1_1) : ScriptValue.NULL;
            var35_35 = new ArrayList<ScriptValue>();
            var36_36 = var1_1.getClassOrVar("taken");
            var35_35.add((ScriptValue)(var36_36 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var36_36, (ScriptContext)var1_1) : ScriptValue.NULL));
            var35_35.add(var28_28);
            return ScriptFormula.callBuiltin((String)"MinecraftItem", var35_35, (ScriptContext)var1_1);
        }
        return var26_26;
    }

    public static ScriptValue beltGive(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("face", scriptContext.getClassOrVar("face"));
        ScriptValue scriptValue = BeltUtils._faceOffset(builder2);
        builder.val("off", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 2.0)));
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$62_belt_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("b", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("b");
        return scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static ScriptValue depotTake(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("face", scriptContext.getClassOrVar("face"));
        ScriptValue scriptValue = BeltUtils._faceOffset(builder2);
        builder.val("off", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 2.0)));
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("c", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            Object object3;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Item");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BeltUtils.class, "minecraft:air"));
                object3 = scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Item") ? new PolyClassItem(object4).um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            return object3;
        }
        ScriptValue scriptValue8 = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptValue)scriptContext.getClassOrVar("null")) ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 64.0)) : scriptContext.getClassOrVar("amount");
        builder.val("amt", scriptValue8);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("c");
            return scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue9, (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("c");
        return scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull_item", (ScriptValue)scriptValue10, (ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static ScriptValue depotCount(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("face", scriptContext.getClassOrVar("face"));
        ScriptValue scriptValue = BeltUtils._faceOffset(builder2);
        builder.val("off", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 2.0)));
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("c", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0);
        }
        double d = 0.0;
        ScriptValue scriptValue7 = ScriptValue.of((double)0.0);
        builder.val("total", scriptValue7);
        double d2 = 0.0;
        ScriptValue scriptValue8 = ScriptValue.of((double)0.0);
        builder.val("i", scriptValue8);
        int n = 0;
        while (n < 1000) {
            ScriptValue scriptValue9;
            ++n;
            double d3 = scriptContext.getNum("i");
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("c");
            Object object3 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "size", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
            if (!(d3 < object3.asNum())) break;
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("total");
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("c");
            builder3.val("item", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue12, (ScriptValue)scriptContext.getClassOrVar("i"), (ScriptContext)scriptContext) : ScriptValue.NULL));
            builder3.val("validator", scriptContext.getClassOrVar("validator"));
            if (BeltUtils._itemMatches(builder3).asBool()) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("c");
                arrayList.add((ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue13, (ScriptValue)scriptContext.getClassOrVar("i"), (ScriptContext)scriptContext) : ScriptValue.NULL));
                scriptValue9 = ScriptFormula.callBuiltin((String)"item_count", arrayList, (ScriptContext)scriptContext);
            } else {
                scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0);
            }
            ScriptValue scriptValue14 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue11, (ScriptValue)scriptValue9);
            builder.val("total", scriptValue14);
            ScriptValue scriptValue15 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            builder.val("i", scriptValue15);
        }
        return scriptContext.getClassOrVar("total");
    }

    public static ScriptValue depotGive(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("face", scriptContext.getClassOrVar("face"));
        ScriptValue scriptValue = BeltUtils._faceOffset(builder2);
        builder.val("off", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 2.0)));
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("c", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return scriptContext.getClassOrVar("item");
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("c");
        return scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 0.99;
        ScriptValue scriptValue = ScriptValue.of((double)0.99);
        builder.val("BELT_TAKE_STALL_THRESHOLD", scriptValue);
        FILE_SCOPE = builder.build();
    }
}
