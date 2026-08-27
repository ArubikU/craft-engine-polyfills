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
            if (var4_4 instanceof ScriptValue.Obj && (var9_9 = (var8_8 = (ScriptValue.Obj)var4_4).instance()) != null && !(var9_9 instanceof PolyClass) && var8_8.typeName().equals("Machine")) {
                var10_10 = new PolyClassMachine_v4(var9_9);
                v0 /* !! */  = var10_10.tm$62_belt_at(var5_5.asNum(), var6_6.asNum(), var7_7.asNum());
            } else {
                v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)var4_4, (ScriptValue)var5_5, (ScriptValue)var6_6, (ScriptValue)var7_7, (ScriptContext)var1_1);
            }
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var11_11 = v0 /* !! */ ;
        var0.val("b", var11_11);
        if ((var11_11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var11_11, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) ** GOTO lbl-1000
        v1 /* !! */  = var11_11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "progress", (ScriptValue)var11_11, (ScriptContext)var1_1) : ScriptValue.NULL;
        if (!(v1 /* !! */ .asNum() < var1_1.getNum("BELT_TAKE_STALL_THRESHOLD"))) {
            v2 = false;
        } else lbl-1000:
        // 2 sources

        {
            v2 = true;
        }
        if (v2) {
            var12_12 = var1_1.getClassOrVar("Item");
            if (var12_12 != ScriptValue.NULL) {
                var13_13 = new ArrayList<ScriptValue>();
                var13_13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BeltUtils.class, "minecraft:air"));
                v3 /* !! */  = var12_12 instanceof ScriptValue.Obj && (var15_15 = (var14_14 = (ScriptValue.Obj)var12_12).instance()) != null && !(var15_15 instanceof PolyClass) && var14_14.typeName().equals("Item") ? new PolyClassItem(var15_15).um$21_create(var13_13) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var12_12, var13_13, (ScriptContext)var1_1);
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            return v3 /* !! */ ;
        }
        var16_16 = var1_1.getClassOrVar("b");
        var17_17 = var16_16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var16_16, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("carried", var17_17);
        var18_18 = ScriptContext.builder().copyFrom(var1_1);
        var18_18.val("item", var17_17);
        var18_18.val("validator", var1_1.getClassOrVar("validator"));
        if (BeltUtils._itemMatches(var18_18).asBool() ^ true) {
            var19_19 = var1_1.getClassOrVar("Item");
            if (var19_19 != ScriptValue.NULL) {
                var20_20 = new ArrayList<ScriptValue>();
                var20_20.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", BeltUtils.class, "minecraft:air"));
                v4 /* !! */  = var19_19 instanceof ScriptValue.Obj && (var22_22 = (var21_21 = (ScriptValue.Obj)var19_19).instance()) != null && !(var22_22 instanceof PolyClass) && var21_21.typeName().equals("Item") ? new PolyClassItem(var22_22).um$21_create(var20_20) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var19_19, var20_20, (ScriptContext)var1_1);
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            return v4 /* !! */ ;
        }
        var23_23 = var1_1.getClassOrVar("b");
        var24_24 = var23_23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var23_23, (ScriptContext)var1_1) : ScriptValue.NULL;
        var0.val("taken", var24_24);
        var25_25 = ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("amount"), (ScriptValue)var1_1.getClassOrVar("null")) != false ? ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var24_24, (ScriptContext)var1_1) : var1_1.getClassOrVar("amount");
        var0.val("want", var25_25);
        if (var25_25.asNum() < ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var24_24, (ScriptContext)var1_1).asNum()) {
            var26_26 = ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)(var24_24 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var24_24, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)(ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var24_24, (ScriptContext)var1_1).asNum() - var25_25.asNum())), (ScriptContext)var1_1);
            var0.val("remainder", var26_26);
            var27_27 = var1_1.getClassOrVar("b");
            v5 /* !! */  = var27_27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var27_27, (ScriptValue)var26_26, (ScriptContext)var1_1) : ScriptValue.NULL;
            return ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)(var24_24 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var24_24, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var25_25, (ScriptContext)var1_1);
        }
        return var24_24;
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$62_belt_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
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
        ScriptValue scriptValue9 = ScriptValue.of((double)d);
        ScriptValue scriptValue10 = ScriptValue.of((double)d2);
        for (int i = 0; i < 1000; ++i) {
            ScriptValue scriptValue11;
            double d3 = scriptValue10.asNum();
            Object object3 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "size", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
            if (!(d3 < object3.asNum())) break;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("c");
            builder3.val("item", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue12, (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL));
            builder3.val("validator", scriptContext.getClassOrVar("validator"));
            ScriptValue scriptValue13 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue9, (ScriptValue)(BeltUtils._itemMatches(builder3).asBool() ? ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)((scriptValue11 = scriptContext.getClassOrVar("c")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue11, (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0))));
            builder.val("total", scriptValue13);
            scriptValue9 = scriptValue13;
            ScriptValue scriptValue14 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            builder.val("i", scriptValue14);
            scriptValue10 = scriptValue14;
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
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", BeltUtils.class, 2.0)));
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
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
