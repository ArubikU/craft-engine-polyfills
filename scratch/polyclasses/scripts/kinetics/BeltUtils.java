/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.kinetics;

import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", BeltUtils.class, 0);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", BeltUtils.class, 1);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin1((String)"type_of", (ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptContext)scriptContext), (String)"str")) {
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
            var8_8 = PolyClassMachine_v3.ofGuarded((ScriptValue)var4_4);
            v0 /* !! */  = var8_8 != null ? var8_8.tm$62_belt_at(var5_5.asNum(), var6_6.asNum(), var7_7.asNum()) : PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)var4_4, (ScriptValue)var5_5, (ScriptValue)var6_6, (ScriptValue)var7_7, (ScriptContext)var1_1);
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var9_9 = v0 /* !! */ ;
        var0.val("b", var9_9);
        if ((var9_9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var9_9, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) ** GOTO lbl-1000
        v1 /* !! */  = var9_9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "progress", (ScriptValue)var9_9, (ScriptContext)var1_1) : ScriptValue.NULL;
        if (!(v1 /* !! */ .asNum() < var1_1.getNum("BELT_TAKE_STALL_THRESHOLD"))) {
            v2 = false;
        } else lbl-1000:
        // 2 sources

        {
            v2 = true;
        }
        if (v2) {
            var10_10 = var1_1.getClassOrVar("Item");
            if (var10_10 != ScriptValue.NULL) {
                var11_11 = new ArrayList<ScriptValue>();
                var11_11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BeltUtils.class, "minecraft:air"));
                var12_12 = PolyClassItem.ofGuarded((ScriptValue)var10_10);
                v3 /* !! */  = var12_12 != null ? var12_12.um$21_create(var11_11) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var10_10, var11_11, (ScriptContext)var1_1);
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            return v3 /* !! */ ;
        }
        var13_13 = var9_9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var9_9, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("carried", var13_13);
        var14_14 = ScriptContext.builder().copyFrom(var1_1);
        var14_14.val("item", var13_13);
        var14_14.val("validator", var1_1.getClassOrVar("validator"));
        if (BeltUtils._itemMatches(var14_14).asBool() ^ true) {
            var15_15 = var1_1.getClassOrVar("Item");
            if (var15_15 != ScriptValue.NULL) {
                var16_16 = new ArrayList<ScriptValue>();
                var16_16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BeltUtils.class, "minecraft:air"));
                var17_17 = PolyClassItem.ofGuarded((ScriptValue)var15_15);
                v4 /* !! */  = var17_17 != null ? var17_17.um$21_create(var16_16) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var15_15, var16_16, (ScriptContext)var1_1);
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            return v4 /* !! */ ;
        }
        var18_18 = var9_9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var9_9, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("taken", var18_18);
        var19_19 = ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("amount"), (ScriptValue)var1_1.getClassOrVar("null")) != false ? ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var18_18, (ScriptContext)var1_1) : var1_1.getClassOrVar("amount");
        var0.val("want", var19_19);
        if (var19_19.asNum() < ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var18_18, (ScriptContext)var1_1).asNum()) {
            var20_20 = ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)(var18_18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var18_18, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)(ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var18_18, (ScriptContext)var1_1).asNum() - var19_19.asNum())), (ScriptContext)var1_1);
            var0.val("remainder", var20_20);
            v5 /* !! */  = var9_9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var9_9, (ScriptValue)var20_20, (ScriptContext)var1_1) : ScriptValue.NULL;
            return ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)(var18_18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var18_18, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var19_19, (ScriptContext)var1_1);
        }
        return var18_18;
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
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 2.0)));
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$62_belt_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum()) : PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("b", scriptValue6);
        return scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 2.0)));
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum()) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("c", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            Object object2;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Item");
            if (scriptValue7 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BeltUtils.class, "minecraft:air"));
                PolyClassItem polyClassItem = PolyClassItem.ofGuarded((ScriptValue)scriptValue7);
                object2 = polyClassItem != null ? polyClassItem.um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            return object2;
        }
        ScriptValue scriptValue8 = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("amount"), (ScriptValue)scriptContext.getClassOrVar("null")) ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 64.0)) : scriptContext.getClassOrVar("amount");
        builder.val("amt", scriptValue8);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue6, (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        return scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull_item", (ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("validator"), (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
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
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 2.0)));
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum()) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
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
        ScriptValue scriptValue9 = ScriptValue.of((double)d);
        ScriptValue scriptValue10 = ScriptValue.of((double)d2);
        for (int i = 0; i < 1000; ++i) {
            double d3 = scriptValue10.asNum();
            Object object2 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "size", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
            if (!(d3 < object2.asNum())) break;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("item", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue6, (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL));
            builder3.val("validator", scriptContext.getClassOrVar("validator"));
            ScriptValue scriptValue11 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue9, (ScriptValue)(BeltUtils._itemMatches(builder3).asBool() ? ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue6, (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0))));
            builder.val("total", scriptValue11);
            scriptValue9 = scriptValue11;
            ScriptValue scriptValue12 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            builder.val("i", scriptValue12);
            scriptValue10 = scriptValue12;
        }
        return scriptValue9;
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
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 2.0)));
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum()) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("c", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return scriptContext.getClassOrVar("item");
        }
        return scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 0.99;
        ScriptValue scriptValue = ScriptValue.of((double)0.99);
        builder.val("BELT_TAKE_STALL_THRESHOLD", scriptValue);
        FILE_SCOPE = builder.build();
    }
}
