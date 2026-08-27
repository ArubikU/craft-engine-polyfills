/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassRedstone
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.gen.BeltUtils
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassRedstone;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.BeltUtils;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class ChuteUtils {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _chuteFilterId(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "_chute_filter";
            String string2 = "str";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("f", scriptValue2);
        return ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"") ? scriptContext.getClassOrVar("null") : scriptValue2;
    }

    public static ScriptValue _chutePullAmount(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "_chute_pull_amount";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("a", scriptValue2);
        return ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null")) ? ScriptValue.of((double)(-1.0)) : scriptValue2;
    }

    public static ScriptValue _chuteAmountMax(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ChuteUtils._chuteFilterId(builder2);
        builder.val("filter_id", scriptValue);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 64.0);
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Item");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue);
            PolyClassItem polyClassItem = PolyClassItem.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassItem != null ? polyClassItem.um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return PolyDispatch.bootstrapGet("memberGet", "max_count", (ScriptValue)object, (ScriptContext)scriptContext);
    }

    public static ScriptValue _chuteOpenAmountDialog(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ChuteUtils._chuteAmountMax(builder2);
        builder.val("max", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = ChuteUtils._chutePullAmount(builder3);
        builder.val("current", scriptValue2);
        ScriptValue scriptValue3 = scriptValue2.asNum() > scriptValue.asNum() ? scriptValue : scriptValue2;
        builder.val("initial", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Dialog");
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "on_close", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_number", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "Pull Amount")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "amount")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "Amount (-1 = any)")), (ScriptValue)ScriptValue.of((double)(-1.0)), (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)), (ScriptValue)scriptValue3, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "storage/chute_utils.pf:_chute_on_amount_dialog_closed")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Machine"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "Set")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "storage/chute_utils.pf:_chute_on_amount_dialog_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue _chuteOnAmountDialogSubmit(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "_chute_pull_amount";
            String string2 = "int";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("amount");
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue2)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _chuteOnAmountDialogClosed(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return scriptContext.getClassOrVar("null");
    }

    public static ScriptValue _chuteOutOffset(ScriptContext.Builder builder) {
        Object object;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "facing";
        PolyClassBlock_v2 polyClassBlock_v2 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2);
        ScriptValue scriptValue3 = polyClassBlock_v2 != null ? polyClassBlock_v2.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        builder.val("facing", scriptValue3);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"down")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
            arrayList.add(ScriptValue.of((double)(-1.0)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
            return new ScriptValue.Array(arrayList);
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("DIR_VEC");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
            object = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue4, (ScriptValue)scriptValue3, (ScriptValue)new ScriptValue.Array(arrayList), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("v", scriptValue5);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0))));
        arrayList.add(ScriptValue.of((double)(-1.0)));
        arrayList.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 2.0))));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _chuteOutChute(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ChuteUtils._chuteOutOffset(builder2);
        builder.val("off", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 2.0)));
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine != null ? polyClassMachine.tm$68_block_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum()) : PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("below", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"cml:chute") ^ true && ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"cml:smart_chute") ^ true) {
            return scriptContext.getClassOrVar("null");
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "machine", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL));
        arrayList.add(ScriptValue.of((boolean)ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"cml:smart_chute")));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _chuteGiveOut(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ChuteUtils._chuteOutOffset(builder2);
        builder.val("off", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 2.0)));
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine != null ? polyClassMachine.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum()) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
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

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue _chutePull(ScriptContext.Builder var0) {
        block18: {
            var1_1 = var0.peek();
            if (var1_1.getBool("smart")) {
                var2_2 = ScriptContext.builder().copyFrom(var1_1);
                v0 = ChuteUtils._chuteFilterId(var2_2);
            } else {
                v0 = var1_1.getClassOrVar("null");
            }
            var3_3 = v0;
            var0.val("validator", var3_3);
            var4_4 = var1_1.getClassOrVar("PLAIN_TRANSFER_CAP");
            var0.val("cap", var4_4);
            var5_5 = false;
            var6_6 = ScriptValue.of((boolean)false);
            var0.val("exact", var6_6);
            if (var1_1.getBool("smart")) {
                var7_7 = ScriptContext.builder().copyFrom(var1_1);
                var8_8 = ChuteUtils._chutePullAmount(var7_7);
                var0.val("set_amount", var8_8);
                if (var8_8.asNum() < 0.0) {
                    var9_9 = var1_1.getClassOrVar("null");
                    var0.val("cap", var9_9);
                } else {
                    var10_10 = ScriptContext.builder().copyFrom(var1_1);
                    var11_11 = Math.min(var8_8.asNum(), ChuteUtils._chuteAmountMax(var10_10).asNum());
                    var13_12 = ScriptValue.of((double)var11_11);
                    var0.val("cap", var13_12);
                    var14_13 = true;
                    var15_14 = ScriptValue.of((boolean)true);
                    var0.val("exact", var15_14);
                }
            }
            if (var1_1.getBool("exact") ^ true) ** GOTO lbl-1000
            var16_15 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
            var16_15.val("face",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "up"));
            var16_15.val("validator", var3_3);
            if (!(BeltUtils.depotCount((ScriptContext.Builder)var16_15).asNum() >= var1_1.getNum("cap"))) {
                v1 = false;
            } else lbl-1000:
            // 2 sources

            {
                v1 = true;
            }
            if (v1) {
                var17_16 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                var17_16.val("face",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "up"));
                var17_16.val("validator", var3_3);
                var17_16.val("amount", var1_1.getClassOrVar("cap"));
                var18_17 = BeltUtils.depotTake((ScriptContext.Builder)var17_16);
                var0.val("taken", var18_17);
                if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var18_17, (ScriptContext)var1_1).asBool() ^ true) {
                    return var18_17;
                }
            }
            if ((var22_18 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                var23_19 = 0.6;
                var25_20 = PolyClassMachine.ofGuarded((ScriptValue)var22_18);
                v2 /* !! */  = var25_20 != null ? var25_20.tm$94_nearby_entities(var23_19) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var22_18, (ScriptValue)ScriptValue.of((double)var23_19), (ScriptContext)var1_1);
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
            var19_21 = ScriptProgram.elementsOf((ScriptValue)v2 /* !! */ );
            var26_22 = var1_1.getClassOrVar("item");
            var27_23 = var1_1.getClassOrVar("full");
            if (var19_21 == null) break block18;
            for (ScriptValue var21_25 : var19_21) {
                var0.val("entity", var21_25);
                var28_26 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                var28_26.val("entity", var21_25);
                if (!Utils.isRestingItem(var28_26).asBool()) ** GOTO lbl-1000
                v3 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var21_25 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var21_25, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1).asNum();
                var29_27 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                v4 = var29_27 != null ? var29_27.tg$203_y() : ((var30_28 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var30_28, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum());
                if (v3 >= v4) {
                    v5 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v5 = false;
                }
                if (!v5) continue;
                var31_29 = var21_25 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var21_25, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("item", var31_29);
                var26_22 = var31_29;
                if (!(ScriptFormula.valuesEqual((ScriptValue)var3_3, (ScriptValue)var1_1.getClassOrVar("null")) != false || (var26_22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "matches", (ScriptValue)var26_22, (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false)) continue;
                var32_30 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var26_22, (ScriptContext)var1_1);
                var0.val("full", var32_30);
                var27_23 = var32_30;
                if (var1_1.getBool("exact") != false && var27_23.asNum() < var1_1.getNum("cap") != false) continue;
                if ((var1_1.getBool("smart") ^ true) != false && var27_23.asNum() > var1_1.getNum("PLAIN_TRANSFER_CAP") != false) {
                    v6 /* !! */  = var21_25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)var21_25, (ScriptValue)ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)(var26_22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var26_22, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)(var27_23.asNum() - var1_1.getNum("PLAIN_TRANSFER_CAP"))), (ScriptContext)var1_1), (ScriptContext)var1_1) : ScriptValue.NULL;
                    return var26_22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var26_22, (ScriptValue)var1_1.getClassOrVar("PLAIN_TRANSFER_CAP"), (ScriptContext)var1_1) : ScriptValue.NULL;
                }
                if (var1_1.getBool("exact") != false && var27_23.asNum() > var1_1.getNum("cap") != false) {
                    v7 /* !! */  = var21_25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)var21_25, (ScriptValue)ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)(var26_22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var26_22, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)(var27_23.asNum() - var1_1.getNum("cap"))), (ScriptContext)var1_1), (ScriptContext)var1_1) : ScriptValue.NULL;
                    return var26_22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var26_22, (ScriptValue)var1_1.getClassOrVar("cap"), (ScriptContext)var1_1) : ScriptValue.NULL;
                }
                v8 /* !! */  = var21_25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var21_25, (ScriptContext)var1_1) : ScriptValue.NULL;
                return var26_22;
            }
        }
        if ((var33_31 = var1_1.getClassOrVar("Item")) != ScriptValue.NULL) {
            var34_32 = new ArrayList<ScriptValue>();
            var34_32.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "minecraft:air"));
            var35_33 = PolyClassItem.ofGuarded((ScriptValue)var33_31);
            v9 /* !! */  = var35_33 != null ? var35_33.um$21_create(var34_32) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var33_31, var34_32, (ScriptContext)var1_1);
        } else {
            v9 /* !! */  = ScriptValue.NULL;
        }
        return v9 /* !! */ ;
    }

    public static ScriptValue _chuteSyncPowered(ScriptContext.Builder builder) {
        block3: {
            ScriptValue scriptValue;
            ScriptValue scriptValue2;
            ScriptContext scriptContext = builder.peek();
            if (scriptContext.getBool("smart") ^ true) {
                return ScriptValue.NULL;
            }
            PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue3 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
            String string = "powered";
            PolyClassBlock_v2 polyClassBlock_v2 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue3);
            ScriptValue scriptValue4 = polyClassBlock_v2 != null ? polyClassBlock_v2.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            builder.val("cur", scriptValue4);
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue5 = polyClassMachine2 != null ? polyClassMachine2.pg$171_redstone() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
            PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue5);
            ScriptValue scriptValue6 = (polyClassRedstone != null ? polyClassRedstone.tg$19_powered() : PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool()) ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "false"));
            builder.val("want", scriptValue6);
            if (!(ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptValue6) ^ true)) break block3;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                String string2 = "powered";
                ScriptValue scriptValue8 = scriptValue6;
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                v0 = polyClassMachine3 != null ? ScriptValue.of((boolean)polyClassMachine3.tm$16_set_property(string2, scriptValue8.asStr())) : PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _chuteTick(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        Object object2;
        ScriptValue scriptValue2;
        PolyClassMachine polyClassMachine;
        ScriptValue scriptValue3;
        PolyClassRedstone polyClassRedstone;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("smart", scriptContext.getClassOrVar("smart"));
        ChuteUtils._chuteSyncPowered(builder2);
        if (scriptContext.getBool("smart") && ((polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)(scriptValue3 = (polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine.pg$171_redstone() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)))) != null ? polyClassRedstone.tg$19_powered() : PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool())) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            String string = "_chute_item";
            String string2 = "item";
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
            object2 = polyClassMachine2 != null ? polyClassMachine2.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("holding", scriptValue5);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool()) {
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("smart", scriptContext.getClassOrVar("smart"));
            ScriptValue scriptValue6 = ChuteUtils._chutePull(builder3);
            builder.val("taken", scriptValue6);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
                if (scriptValue7 != ScriptValue.NULL) {
                    String string = "_chute_item";
                    String string3 = "item";
                    ScriptValue scriptValue8 = scriptValue6;
                    PolyClassMachine polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                    v1 = polyClassMachine3 != null ? ScriptValue.of((boolean)polyClassMachine3.tm$82_set_typed(string, string3, scriptValue8)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                } else {
                    v1 = ScriptValue.NULL;
                }
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
                if (scriptValue9 != ScriptValue.NULL) {
                    String string = "_chute_progress";
                    String string4 = "int";
                    ScriptValue scriptValue10 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0);
                    PolyClassMachine polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9);
                    v2 = polyClassMachine4 != null ? ScriptValue.of((boolean)polyClassMachine4.tm$82_set_typed(string, string4, scriptValue10)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                } else {
                    v2 = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
        if (scriptValue11 != ScriptValue.NULL) {
            String string = "_chute_progress";
            String string5 = "int";
            PolyClassMachine polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11);
            object = polyClassMachine5 != null ? polyClassMachine5.tm$34_get_typed(string, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = object;
        builder.val("progress", scriptValue12);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue12, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            double d = 0.0;
            ScriptValue scriptValue13 = ScriptValue.of((double)0.0);
            builder.val("progress", scriptValue13);
        }
        ScriptValue scriptValue14 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("progress"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
        builder.val("progress", scriptValue14);
        if (scriptValue14.asNum() < scriptContext.getNum("CHUTE_STEP")) {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                String string = "_chute_progress";
                String string6 = "int";
                ScriptValue scriptValue16 = scriptValue14;
                PolyClassMachine polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue15);
                v4 = polyClassMachine6 != null ? ScriptValue.of((boolean)polyClassMachine6.tm$82_set_typed(string, string6, scriptValue16)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue16, (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue17 = ChuteUtils._chuteOutChute(builder4);
        builder.val("below_info", scriptValue17);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue17, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ScriptValue scriptValue18 = ScriptFormula.subscriptGet((ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0)));
            builder.val("below", scriptValue18);
            ScriptValue scriptValue19 = ScriptFormula.subscriptGet((ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
            builder.val("below_is_smart", scriptValue19);
            ScriptValue scriptValue20 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "_chute_item")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "item")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("below_holding", scriptValue20);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue20, (ScriptContext)scriptContext).asBool()) {
                ScriptValue scriptValue21 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
                builder.val("full", scriptValue21);
                if (scriptValue19.asBool() ^ true && scriptValue21.asNum() > scriptContext.getNum("PLAIN_TRANSFER_CAP")) {
                    Object object3 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "_chute_item")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "item")), (ScriptValue)(scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("PLAIN_TRANSFER_CAP"), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue22 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue22 != ScriptValue.NULL) {
                        String string = "_chute_item";
                        String string7 = "item";
                        ScriptValue scriptValue23 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)(scriptValue21.asNum() - scriptContext.getNum("PLAIN_TRANSFER_CAP"))), (ScriptContext)scriptContext) : ScriptValue.NULL;
                        PolyClassMachine polyClassMachine7 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue22);
                        v6 = polyClassMachine7 != null ? ScriptValue.of((boolean)polyClassMachine7.tm$82_set_typed(string, string7, scriptValue23)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
                    } else {
                        v6 = ScriptValue.NULL;
                    }
                } else {
                    Object object4 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "_chute_item")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "item")), (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue24 != ScriptValue.NULL) {
                        String string = "_chute_item";
                        String string8 = "item";
                        ScriptValue scriptValue25 = scriptContext.getClassOrVar("null");
                        PolyClassMachine polyClassMachine8 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue24);
                        v8 = polyClassMachine8 != null ? ScriptValue.of((boolean)polyClassMachine8.tm$82_set_typed(string, string8, scriptValue25)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
                    } else {
                        v8 = ScriptValue.NULL;
                    }
                }
                Object object5 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "_chute_progress")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "int")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                ScriptValue scriptValue26 = scriptContext.getClassOrVar("Machine");
                if (scriptValue26 != ScriptValue.NULL) {
                    String string = "_chute_progress";
                    String string9 = "int";
                    ScriptValue scriptValue27 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0);
                    PolyClassMachine polyClassMachine9 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue26);
                    v10 = polyClassMachine9 != null ? ScriptValue.of((boolean)polyClassMachine9.tm$82_set_typed(string, string9, scriptValue27)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string9), (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
                } else {
                    v10 = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
        builder5.val("item", scriptValue5);
        ScriptValue scriptValue28 = ChuteUtils._chuteGiveOut(builder5);
        builder.val("leftover", scriptValue28);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue28, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue29 = ChuteUtils._chuteOutOffset(builder6);
            builder.val("off", scriptValue29);
            ScriptValue scriptValue30 = scriptContext.getClassOrVar("Machine");
            if (scriptValue30 != ScriptValue.NULL) {
                ScriptValue scriptValue31 = scriptValue28;
                ScriptValue scriptValue32 = ScriptFormula.subscriptGet((ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0)));
                ScriptValue scriptValue33 = ScriptFormula.subscriptGet((ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
                ScriptValue scriptValue34 = ScriptFormula.subscriptGet((ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 2.0)));
                PolyClassMachine polyClassMachine10 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue30);
                v11 = polyClassMachine10 != null ? ScriptValue.of((boolean)polyClassMachine10.tm$50_drop_item_at(scriptValue31, scriptValue32.asNum(), scriptValue33.asNum(), scriptValue34.asNum())) : PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue30, (ScriptValue)scriptValue31, (ScriptValue)scriptValue32, (ScriptValue)scriptValue33, (ScriptValue)scriptValue34, (ScriptContext)scriptContext);
            } else {
                v11 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
            String string = "_chute_item";
            String string10 = "item";
            ScriptValue scriptValue35 = scriptContext.getClassOrVar("null");
            PolyClassMachine polyClassMachine11 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            v12 = polyClassMachine11 != null ? ScriptValue.of((boolean)polyClassMachine11.tm$82_set_typed(string, string10, scriptValue35)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string10), (ScriptValue)scriptValue35, (ScriptContext)scriptContext);
        } else {
            v12 = ScriptValue.NULL;
        }
        ScriptValue scriptValue36 = scriptContext.getClassOrVar("Machine");
        if (scriptValue36 != ScriptValue.NULL) {
            String string = "_chute_progress";
            String string11 = "int";
            ScriptValue scriptValue37 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0);
            PolyClassMachine polyClassMachine12 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue36);
            v13 = polyClassMachine12 != null ? ScriptValue.of((boolean)polyClassMachine12.tm$82_set_typed(string, string11, scriptValue37)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue36, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string11), (ScriptValue)scriptValue37, (ScriptContext)scriptContext);
        } else {
            v13 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _chuteToggleWindow(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            String string = "_chute_window";
            String string2 = "int";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("win", scriptValue2);
        double d = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("win"), (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("win"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0))) ? 1.0 : 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)d);
        builder.val("win", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            String string = "_chute_window";
            String string3 = "int";
            ScriptValue scriptValue5 = ScriptValue.of((double)d);
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
            v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue5)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            String string = "window";
            ScriptValue scriptValue7 = d == 1.0 ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "false"));
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6);
            v2 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$16_set_property(string, scriptValue7.asStr())) : PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _chuteCycleFacing(ScriptContext.Builder builder) {
        double d;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "facing";
        PolyClassBlock_v2 polyClassBlock_v2 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2);
        ScriptValue scriptValue3 = polyClassBlock_v2 != null ? polyClassBlock_v2.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        builder.val("cur", scriptValue3);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "down");
            builder.val("cur", scriptValue4);
        }
        double d2 = 0.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue5);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("FACING_CYCLE"));
        ScriptValue scriptValue6 = ScriptValue.of((double)d2);
        if (list != null) {
            for (ScriptValue scriptValue7 : list) {
                builder.val("f", scriptValue7);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("cur"))) break;
                ScriptValue scriptValue8 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
                builder.val("idx", scriptValue8);
                scriptValue6 = scriptValue8;
            }
        }
        ScriptValue scriptValue9 = ScriptFormula.subscriptGet((ScriptValue)scriptContext.getClassOrVar("FACING_CYCLE"), (ScriptValue)ScriptValue.of((double)((d = ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptContext.getClassOrVar("FACING_CYCLE"), (ScriptContext)scriptContext).asNum()) == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0))).asNum() % d)));
        builder.val("next", scriptValue9);
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
        if (scriptValue10 != ScriptValue.NULL) {
            String string2 = "facing";
            ScriptValue scriptValue11 = scriptValue9;
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue10);
            v0 = polyClassMachine2 != null ? ScriptValue.of((boolean)polyClassMachine2.tm$16_set_property(string2, scriptValue11.asStr())) : PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _isGlassItem(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("id"), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.callBuiltin2((String)"contains", (ScriptValue)scriptContext.getClassOrVar("id"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "glass")), (ScriptContext)scriptContext).asBool() ? 1 : 0) != 0);
    }

    public static ScriptValue _chuteDropHeld(ScriptContext.Builder builder) {
        block4: {
            Object object;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                String string = "_chute_item";
                String string2 = "item";
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
                object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue2 = object;
            builder.val("holding", scriptValue2);
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool() ^ true)) break block4;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
                v1 = polyClassMachine != null ? polyClassMachine.um$4_drop_item(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("is_resting_item");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"utils.pf", null, arrayList);
        ArrayList<String> arrayList2 = new ArrayList<String>();
        arrayList2.add("depot_take");
        arrayList2.add("depot_give");
        arrayList2.add("depot_count");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"kinetics/belt_utils.pf", null, arrayList2);
        double d = 20.0;
        ScriptValue scriptValue = ScriptValue.of((double)20.0);
        builder.val("CHUTE_STEP", scriptValue);
        ArrayList<Object> arrayList3 = new ArrayList<Object>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "north"));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
        arrayList4.add(ScriptValue.of((double)(-1.0)));
        arrayList3.add(new ScriptValue.Array(arrayList4));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "south"));
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0));
        arrayList3.add(new ScriptValue.Array(arrayList5));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "east"));
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
        arrayList3.add(new ScriptValue.Array(arrayList6));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "west"));
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add(ScriptValue.of((double)(-1.0)));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
        arrayList3.add(new ScriptValue.Array(arrayList7));
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext);
        builder.val("DIR_VEC", scriptValue2);
        ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "down"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "north"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "east"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "south"));
        arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "west"));
        ScriptValue.Array array = new ScriptValue.Array(arrayList8);
        builder.val("FACING_CYCLE", (ScriptValue)array);
        double d2 = 16.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)16.0);
        builder.val("PLAIN_TRANSFER_CAP", scriptValue3);
        FILE_SCOPE = builder.build();
    }
}
