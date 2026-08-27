/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class BeltUtils {
    public static ScriptValue _faceOffset(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("face"), (String)"north")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)(-1.0)));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("face"), (String)"south")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)1.0));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("face"), (String)"east")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)1.0));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("face"), (String)"west")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)(-1.0)));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("face"), (String)"up")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)1.0));
            arrayList.add(ScriptValue.of((double)0.0));
            return new ScriptValue.Array(arrayList);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("face"), (String)"down")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)(-1.0)));
            arrayList.add(ScriptValue.of((double)0.0));
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)0.0));
        arrayList.add(ScriptValue.of((double)0.0));
        arrayList.add(ScriptValue.of((double)0.0));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _itemMatches(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("item"));
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.of((boolean)false);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return ScriptValue.of((boolean)true);
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("validator"));
        if (ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin((String)"type_of", arrayList2, (ScriptContext)scriptContext), (String)"str")) {
            Object object2;
            ScriptValue scriptValue = scriptContext.getClassOrVar("item");
            if (scriptValue != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptContext.getClassOrVar("validator"));
                object2 = PolyDispatch.bootstrapCall("memberCall", "matches", (ScriptValue)scriptValue, arrayList3, (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            return object2;
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("item");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptContext.getClassOrVar("validator"));
            object = PolyDispatch.bootstrapCall("memberCall", "same_as", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
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
            var5_5 = ScriptFormula.subscriptGet((ScriptValue)var3_3, (ScriptValue)ScriptValue.of((double)0.0));
            var6_6 = ScriptFormula.subscriptGet((ScriptValue)var3_3, (ScriptValue)ScriptValue.of((double)1.0));
            var7_7 = ScriptFormula.subscriptGet((ScriptValue)var3_3, (ScriptValue)ScriptValue.of((double)2.0));
            if (var4_4 instanceof ScriptValue.Obj && (var9_9 = (var8_8 = (ScriptValue.Obj)var4_4).instance()) != null && !(var9_9 instanceof PolyClass) && var8_8.typeName().equals("Machine")) {
                var10_10 = new PolyClassMachine_v4(var9_9);
                v0 /* !! */  = var10_10.tm$62_belt_at(var5_5.asNum(), var6_6.asNum(), var7_7.asNum());
            } else {
                var11_11 = new ArrayList<ScriptValue>();
                var11_11.add(var5_5);
                var11_11.add(var6_6);
                var11_11.add(var7_7);
                v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)var4_4, var11_11, (ScriptContext)var1_1);
            }
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var12_12 = v0 /* !! */ ;
        var0.val("b", var12_12);
        var13_13 = var1_1.getClassOrVar("b");
        if ((var13_13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var13_13, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) ** GOTO lbl-1000
        var14_14 = var1_1.getClassOrVar("b");
        v1 /* !! */  = var14_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "progress", (ScriptValue)var14_14, (ScriptContext)var1_1) : ScriptValue.NULL;
        if (!(v1 /* !! */ .asNum() < var1_1.getNum("BELT_TAKE_STALL_THRESHOLD"))) {
            v2 = false;
        } else lbl-1000:
        // 2 sources

        {
            v2 = true;
        }
        if (v2) {
            var15_15 = var1_1.getClassOrVar("Item");
            if (var15_15 != ScriptValue.NULL) {
                var16_16 = new ArrayList<ScriptValue>();
                var16_16.add(ScriptValue.of((String)"minecraft:air"));
                v3 /* !! */  = var15_15 instanceof ScriptValue.Obj && (var18_18 = (var17_17 = (ScriptValue.Obj)var15_15).instance()) != null && !(var18_18 instanceof PolyClass) && var17_17.typeName().equals("Item") ? new PolyClassItem(var18_18).um$21_create(var16_16) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var15_15, var16_16, (ScriptContext)var1_1);
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            return v3 /* !! */ ;
        }
        var19_19 = var1_1.getClassOrVar("b");
        if (var19_19 != ScriptValue.NULL) {
            var20_20 = new ArrayList<E>();
            v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var19_19, var20_20, (ScriptContext)var1_1);
        } else {
            v4 /* !! */  = ScriptValue.NULL;
        }
        var21_21 = v4 /* !! */ ;
        var0.val("carried", var21_21);
        var22_22 = ScriptContext.builder().copyFrom(var1_1);
        var22_22.val("item", var21_21);
        var22_22.val("validator", var1_1.getClassOrVar("validator"));
        if (BeltUtils._itemMatches(var22_22).asBool() ^ true) {
            var23_23 = var1_1.getClassOrVar("Item");
            if (var23_23 != ScriptValue.NULL) {
                var24_24 = new ArrayList<ScriptValue>();
                var24_24.add(ScriptValue.of((String)"minecraft:air"));
                v5 /* !! */  = var23_23 instanceof ScriptValue.Obj && (var26_26 = (var25_25 = (ScriptValue.Obj)var23_23).instance()) != null && !(var26_26 instanceof PolyClass) && var25_25.typeName().equals("Item") ? new PolyClassItem(var26_26).um$21_create(var24_24) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var23_23, var24_24, (ScriptContext)var1_1);
            } else {
                v5 /* !! */  = ScriptValue.NULL;
            }
            return v5 /* !! */ ;
        }
        var27_27 = var1_1.getClassOrVar("b");
        if (var27_27 != ScriptValue.NULL) {
            var28_28 = new ArrayList<E>();
            v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var27_27, var28_28, (ScriptContext)var1_1);
        } else {
            v6 /* !! */  = ScriptValue.NULL;
        }
        var29_29 = v6 /* !! */ ;
        var0.val("taken", var29_29);
        if (ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("amount"), (ScriptValue)var1_1.getClassOrVar("null"))) {
            var30_30 = new ArrayList<ScriptValue>();
            var30_30.add(var29_29);
            v7 = ScriptFormula.callBuiltin((String)"item_count", var30_30, (ScriptContext)var1_1);
        } else {
            v7 = var1_1.getClassOrVar("amount");
        }
        var31_31 = v7;
        var0.val("want", var31_31);
        v8 = var31_31.asNum();
        var32_32 = new ArrayList<ScriptValue>();
        var32_32.add(var29_29);
        if (v8 < ScriptFormula.callBuiltin((String)"item_count", var32_32, (ScriptContext)var1_1).asNum()) {
            var33_33 = new ArrayList<ScriptValue>();
            var34_34 = var1_1.getClassOrVar("taken");
            var33_33.add((ScriptValue)(var34_34 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var34_34, (ScriptContext)var1_1) : ScriptValue.NULL));
            var35_35 = new ArrayList<ScriptValue>();
            var35_35.add(var29_29);
            var33_33.add(ScriptValue.of((double)(ScriptFormula.callBuiltin((String)"item_count", var35_35, (ScriptContext)var1_1).asNum() - var31_31.asNum())));
            var36_36 = ScriptFormula.callBuiltin((String)"MinecraftItem", var33_33, (ScriptContext)var1_1);
            var0.val("remainder", var36_36);
            var37_37 = var1_1.getClassOrVar("b");
            if (var37_37 != ScriptValue.NULL) {
                var38_38 = new ArrayList<ScriptValue>();
                var38_38.add(var36_36);
                v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var37_37, var38_38, (ScriptContext)var1_1);
            } else {
                v9 /* !! */  = ScriptValue.NULL;
            }
            var39_39 = new ArrayList<ScriptValue>();
            var40_40 = var1_1.getClassOrVar("taken");
            var39_39.add((ScriptValue)(var40_40 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var40_40, (ScriptContext)var1_1) : ScriptValue.NULL));
            var39_39.add(var31_31);
            return ScriptFormula.callBuiltin((String)"MinecraftItem", var39_39, (ScriptContext)var1_1);
        }
        return var29_29;
    }

    public static ScriptValue beltGive(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("face", scriptContext.getClassOrVar("face"));
        ScriptValue scriptValue = BeltUtils._faceOffset(builder2);
        builder.val("off", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)0.0));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)1.0));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)2.0));
            if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v4.tm$62_belt_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                arrayList.add(scriptValue4);
                arrayList.add(scriptValue5);
                object2 = PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object2;
        builder.val("b", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("b");
        if (scriptValue7 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("item"));
            object = PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue depotTake(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("face", scriptContext.getClassOrVar("face"));
        ScriptValue scriptValue = BeltUtils._faceOffset(builder2);
        builder.val("off", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)0.0));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)1.0));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)2.0));
            if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v4.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                arrayList.add(scriptValue4);
                arrayList.add(scriptValue5);
                object2 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object2;
        builder.val("c", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            Object object4;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Item");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)"minecraft:air"));
                object4 = scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Item") ? new PolyClassItem(object5).um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            return object4;
        }
        ScriptValue scriptValue8 = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptValue)scriptContext.getClassOrVar("null")) ? ScriptValue.of((double)64.0) : scriptContext.getClassOrVar("amount");
        builder.val("amt", scriptValue8);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptValue)scriptContext.getClassOrVar("null"))) {
            Object object6;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("c");
            if (scriptValue9 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue8);
                object6 = PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
            } else {
                object6 = ScriptValue.NULL;
            }
            return object6;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("c");
        if (scriptValue10 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("validator"));
            arrayList.add(scriptValue8);
            object = PolyDispatch.bootstrapCall("memberCall", "pull_item", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
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
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)0.0));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)1.0));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)2.0));
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                arrayList.add(scriptValue4);
                arrayList.add(scriptValue5);
                object = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("c", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return ScriptValue.of((double)0.0);
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
            Object object3;
            ++n;
            double d3 = scriptContext.getNum("i");
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("c");
            Object object4 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "size", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
            if (!(d3 < object4.asNum())) break;
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("total");
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("c");
            if (scriptValue12 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("i"));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            builder3.val("item", object3);
            builder3.val("validator", scriptContext.getClassOrVar("validator"));
            if (BeltUtils._itemMatches(builder3).asBool()) {
                Object object5;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("c");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(scriptContext.getClassOrVar("i"));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue13, arrayList2, (ScriptContext)scriptContext);
                } else {
                    object5 = ScriptValue.NULL;
                }
                arrayList.add((ScriptValue)object5);
                scriptValue9 = ScriptFormula.callBuiltin((String)"item_count", arrayList, (ScriptContext)scriptContext);
            } else {
                scriptValue9 = ScriptValue.of((double)0.0);
            }
            ScriptValue scriptValue14 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue11, (ScriptValue)scriptValue9);
            builder.val("total", scriptValue14);
            ScriptValue scriptValue15 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)ScriptValue.of((double)1.0));
            builder.val("i", scriptValue15);
        }
        return scriptContext.getClassOrVar("total");
    }

    public static ScriptValue depotGive(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("face", scriptContext.getClassOrVar("face"));
        ScriptValue scriptValue = BeltUtils._faceOffset(builder2);
        builder.val("off", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)0.0));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)1.0));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)2.0));
            if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                object2 = polyClassMachine_v4.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                arrayList.add(scriptValue4);
                arrayList.add(scriptValue5);
                object2 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object2;
        builder.val("c", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return scriptContext.getClassOrVar("item");
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("c");
        if (scriptValue7 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("item"));
            object = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 0.99;
        ScriptValue scriptValue = ScriptValue.of((double)0.99);
        builder.val("BELT_TAKE_STALL_THRESHOLD", scriptValue);
    }
}
