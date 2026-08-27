/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassRedstone
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.BeltUtils
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassRedstone;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.BeltUtils;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.CallSite;
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "_chute_filter";
            String string2 = "str";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "_chute_pull_amount";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object2;
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue);
            object = scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Item") ? new PolyClassItem(object2).um$21_create(arrayList) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
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
            ScriptValue.Obj obj;
            Object object;
            String string = "_chute_pull_amount";
            String string2 = "int";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("amount");
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string2, scriptValue2));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
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
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object2;
        PolyClassMachine_v3 polyClassMachine_v3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        String string = "facing";
        if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v2 polyClassBlock_v2 = new PolyClassBlock_v2(object2);
            callSite = polyClassBlock_v2.tm$24_property(string);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("facing", (ScriptValue)callSite2);
        if (ScriptFormula.valuesEqual((ScriptValue)callSite2, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)callSite2, (String)"down")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
            arrayList.add(ScriptValue.of((double)(-1.0)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
            return new ScriptValue.Array(arrayList);
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("DIR_VEC");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0));
            object = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue3, (ScriptValue)callSite2, (ScriptValue)new ScriptValue.Array(arrayList), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("v", scriptValue4);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0))));
        arrayList.add(ScriptValue.of((double)(-1.0)));
        arrayList.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 2.0))));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue _chuteOutChute(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue3 = ChuteUtils._chuteOutOffset(builder2);
        builder.val("off", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0)));
            ScriptValue scriptValue6 = ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
            ScriptValue scriptValue7 = ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 2.0)));
            if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$68_block_at(scriptValue5.asNum(), scriptValue6.asNum(), scriptValue7.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object;
        builder.val("below", scriptValue8);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue8, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)((scriptValue2 = scriptContext.getClassOrVar("below")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"cml:chute") ^ true && ScriptFormula.valuesEqualStr((ScriptValue)((scriptValue = scriptContext.getClassOrVar("below")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"cml:smart_chute") ^ true) {
            return scriptContext.getClassOrVar("null");
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("below");
        arrayList.add((ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "machine", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("below");
        arrayList.add(ScriptValue.of((boolean)ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"cml:smart_chute")));
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
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 2.0)));
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
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

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue _chutePull(ScriptContext.Builder var0) {
        block24: {
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
                var19_18 = new ArrayList<ScriptValue>();
                var19_18.add(var18_17);
                if (ScriptFormula.callBuiltin((String)"is_empty", var19_18, (ScriptContext)var1_1).asBool() ^ true) {
                    return var18_17;
                }
            }
            if ((var23_19 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                var24_20 = 0.6;
                if (var23_19 instanceof ScriptValue.Obj && (var27_22 = (var26_21 = (ScriptValue.Obj)var23_19).instance()) != null && !(var27_22 instanceof PolyClass) && var26_21.typeName().equals("Machine")) {
                    var28_23 = new PolyClassMachine_v3(var27_22);
                    v2 /* !! */  = var28_23.tm$94_nearby_entities(var24_20);
                } else {
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var23_19, (ScriptValue)ScriptValue.of((double)var24_20), (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
            var20_24 = ScriptProgram.elementsOf((ScriptValue)v2 /* !! */ );
            if (var20_24 == null) break block24;
            for (ScriptValue var22_26 : var20_24) {
                var0.val("entity", var22_26);
                var29_27 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                var29_27.val("entity", var1_1.getClassOrVar("entity"));
                if (!Utils.isRestingItem(var29_27).asBool()) ** GOTO lbl-1000
                var30_28 = var1_1.getClassOrVar("entity");
                v3 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var30_28 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var30_28, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1).asNum();
                var31_29 = var1_1.getClassOrVar("Machine");
                v4 = var31_29 != ScriptValue.NULL ? ((var32_30 = PolyClassMachine_v3.ofGuarded((ScriptValue)var31_29)) != null ? var32_30.tg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var31_29, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (v3 >= v4) {
                    v5 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v5 = false;
                }
                if (!v5) continue;
                var33_31 = var1_1.getClassOrVar("entity");
                var34_32 = var33_31 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var33_31, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("item", var34_32);
                if (!(ScriptFormula.valuesEqual((ScriptValue)var3_3, (ScriptValue)var1_1.getClassOrVar("null")) != false || ((var35_33 = var1_1.getClassOrVar("item")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "matches", (ScriptValue)var35_33, (ScriptValue)var3_3, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false)) continue;
                var36_34 = new ArrayList<ScriptValue>();
                var36_34.add(var34_32);
                var37_35 = ScriptFormula.callBuiltin((String)"item_count", var36_34, (ScriptContext)var1_1);
                var0.val("full", var37_35);
                if (var1_1.getBool("exact") != false && var37_35.asNum() < var1_1.getNum("cap") != false) continue;
                if ((var1_1.getBool("smart") ^ true) != false && var37_35.asNum() > var1_1.getNum("PLAIN_TRANSFER_CAP") != false) {
                    var38_36 = var1_1.getClassOrVar("entity");
                    if (var38_36 != ScriptValue.NULL) {
                        var39_37 = new ArrayList<ScriptValue>();
                        var40_38 = var1_1.getClassOrVar("item");
                        var39_37.add((ScriptValue)(var40_38 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var40_38, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var39_37.add(ScriptValue.of((double)(var37_35.asNum() - var1_1.getNum("PLAIN_TRANSFER_CAP"))));
                        v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)var38_36, (ScriptValue)ScriptFormula.callBuiltin((String)"MinecraftItem", var39_37, (ScriptContext)var1_1), (ScriptContext)var1_1);
                    } else {
                        v6 /* !! */  = ScriptValue.NULL;
                    }
                    var41_39 = var1_1.getClassOrVar("item");
                    return var41_39 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var41_39, (ScriptValue)var1_1.getClassOrVar("PLAIN_TRANSFER_CAP"), (ScriptContext)var1_1) : ScriptValue.NULL;
                }
                if (var1_1.getBool("exact") != false && var37_35.asNum() > var1_1.getNum("cap") != false) {
                    var42_40 = var1_1.getClassOrVar("entity");
                    if (var42_40 != ScriptValue.NULL) {
                        var43_41 = new ArrayList<ScriptValue>();
                        var44_42 = var1_1.getClassOrVar("item");
                        var43_41.add((ScriptValue)(var44_42 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var44_42, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var43_41.add(ScriptValue.of((double)(var37_35.asNum() - var1_1.getNum("cap"))));
                        v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)var42_40, (ScriptValue)ScriptFormula.callBuiltin((String)"MinecraftItem", var43_41, (ScriptContext)var1_1), (ScriptContext)var1_1);
                    } else {
                        v7 /* !! */  = ScriptValue.NULL;
                    }
                    var45_43 = var1_1.getClassOrVar("item");
                    return var45_43 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var45_43, (ScriptValue)var1_1.getClassOrVar("cap"), (ScriptContext)var1_1) : ScriptValue.NULL;
                }
                var46_44 = var1_1.getClassOrVar("entity");
                v8 /* !! */  = var46_44 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var46_44, (ScriptContext)var1_1) : ScriptValue.NULL;
                return var34_32;
            }
        }
        if ((var47_45 = var1_1.getClassOrVar("Item")) != ScriptValue.NULL) {
            var48_46 = new ArrayList<ScriptValue>();
            var48_46.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "minecraft:air"));
            v9 /* !! */  = var47_45 instanceof ScriptValue.Obj && (var50_48 = (var49_47 = (ScriptValue.Obj)var47_45).instance()) != null && !(var50_48 instanceof PolyClass) && var49_47.typeName().equals("Item") ? new PolyClassItem(var50_48).um$21_create(var48_46) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var47_45, var48_46, (ScriptContext)var1_1);
        } else {
            v9 /* !! */  = ScriptValue.NULL;
        }
        return v9 /* !! */ ;
    }

    public static ScriptValue _chuteSyncPowered(ScriptContext.Builder builder) {
        block7: {
            PolyClassMachine_v3 polyClassMachine_v3;
            CallSite callSite;
            ScriptValue.Obj obj;
            Object object;
            PolyClassMachine_v3 polyClassMachine_v32;
            ScriptContext scriptContext = builder.peek();
            if (scriptContext.getBool("smart") ^ true) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v32.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            String string = "powered";
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
                PolyClassBlock_v2 polyClassBlock_v2 = new PolyClassBlock_v2(object);
                callSite = polyClassBlock_v2.tm$24_property(string);
            } else {
                callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
            CallSite callSite2 = callSite;
            builder.val("cur", (ScriptValue)callSite2);
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v3.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue4);
            ScriptValue scriptValue5 = (polyClassRedstone != null ? polyClassRedstone.tg$19_powered() : PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool()) ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "false"));
            builder.val("want", scriptValue5);
            if (!(ScriptFormula.valuesEqual((ScriptValue)callSite2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqual((ScriptValue)callSite2, (ScriptValue)scriptValue5) ^ true)) break block7;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object2;
                String string2 = "powered";
                ScriptValue scriptValue7 = scriptValue5;
                if (scriptValue6 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v33 = new PolyClassMachine_v3(object2);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v33.tm$16_set_property(string2, scriptValue7.asStr()));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue _chuteTick(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = ScriptContext.builder().copyFrom(var1_1);
        var2_2.val("smart", var1_1.getClassOrVar("smart"));
        ChuteUtils._chuteSyncPowered(var2_2);
        if (!var1_1.getBool("smart")) ** GOTO lbl-1000
        var4_3 = var1_1.getClassOrVar("Machine");
        var3_5 = var4_3 != ScriptValue.NULL ? ((var5_4 = PolyClassMachine_v3.ofGuarded((ScriptValue)var4_3)) != null ? var5_4.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)var4_3, (ScriptContext)var1_1)) : ScriptValue.NULL;
        var6_6 = PolyClassRedstone.ofGuarded((ScriptValue)var3_5);
        if (var6_6 != null ? var6_6.tg$19_powered() : PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)var3_5, (ScriptContext)var1_1).asBool()) {
            v0 = true;
        } else lbl-1000:
        // 2 sources

        {
            v0 = false;
        }
        if (v0) {
            return ScriptValue.NULL;
        }
        var7_7 = var1_1.getClassOrVar("Machine");
        if (var7_7 != ScriptValue.NULL) {
            var8_8 = "_chute_item";
            var9_9 = "item";
            if (var7_7 instanceof ScriptValue.Obj && (var11_11 = (var10_10 = (ScriptValue.Obj)var7_7).instance()) != null && !(var11_11 instanceof PolyClass) && var10_10.typeName().equals("Machine")) {
                var12_12 = new PolyClassMachine_v3(var11_11);
                v1 /* !! */  = var12_12.tm$34_get_typed(var8_8, var9_9);
            } else {
                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var7_7, (ScriptValue)ScriptValue.of((String)var8_8), (ScriptValue)ScriptValue.of((String)var9_9), (ScriptContext)var1_1);
            }
        } else {
            v1 /* !! */  = ScriptValue.NULL;
        }
        var13_13 = v1 /* !! */ ;
        var0.val("holding", var13_13);
        var14_14 = new ArrayList<ScriptValue>();
        var14_14.add(var13_13);
        if (ScriptFormula.callBuiltin((String)"is_empty", var14_14, (ScriptContext)var1_1).asBool()) {
            var15_15 = ScriptContext.builder().copyFrom(var1_1);
            var15_15.val("smart", var1_1.getClassOrVar("smart"));
            var16_16 = ChuteUtils._chutePull(var15_15);
            var0.val("taken", var16_16);
            var17_17 = new ArrayList<ScriptValue>();
            var17_17.add(var16_16);
            if (ScriptFormula.callBuiltin((String)"is_empty", var17_17, (ScriptContext)var1_1).asBool() ^ true) {
                var18_18 = var1_1.getClassOrVar("Machine");
                if (var18_18 != ScriptValue.NULL) {
                    var19_19 = "_chute_item";
                    var20_20 = "item";
                    var21_21 = var16_16;
                    if (var18_18 instanceof ScriptValue.Obj && (var23_23 = (var22_22 = (ScriptValue.Obj)var18_18).instance()) != null && !(var23_23 instanceof PolyClass) && var22_22.typeName().equals("Machine")) {
                        var24_24 = new PolyClassMachine_v3(var23_23);
                        v2 /* !! */  = ScriptValue.of((boolean)var24_24.tm$82_set_typed(var19_19, var20_20, var21_21));
                    } else {
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var18_18, (ScriptValue)ScriptValue.of((String)var19_19), (ScriptValue)ScriptValue.of((String)var20_20), (ScriptValue)var21_21, (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var25_25 = var1_1.getClassOrVar("Machine");
                if (var25_25 != ScriptValue.NULL) {
                    var26_26 = "_chute_progress";
                    var27_27 = "int";
                    var28_28 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0);
                    if (var25_25 instanceof ScriptValue.Obj && (var30_30 = (var29_29 = (ScriptValue.Obj)var25_25).instance()) != null && !(var30_30 instanceof PolyClass) && var29_29.typeName().equals("Machine")) {
                        var31_31 = new PolyClassMachine_v3(var30_30);
                        v3 /* !! */  = ScriptValue.of((boolean)var31_31.tm$82_set_typed(var26_26, var27_27, var28_28));
                    } else {
                        v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var25_25, (ScriptValue)ScriptValue.of((String)var26_26), (ScriptValue)ScriptValue.of((String)var27_27), (ScriptValue)var28_28, (ScriptContext)var1_1);
                    }
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        var32_32 = var1_1.getClassOrVar("Machine");
        if (var32_32 != ScriptValue.NULL) {
            var33_33 = "_chute_progress";
            var34_34 = "int";
            if (var32_32 instanceof ScriptValue.Obj && (var36_36 = (var35_35 = (ScriptValue.Obj)var32_32).instance()) != null && !(var36_36 instanceof PolyClass) && var35_35.typeName().equals("Machine")) {
                var37_37 = new PolyClassMachine_v3(var36_36);
                v4 /* !! */  = var37_37.tm$34_get_typed(var33_33, var34_34);
            } else {
                v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var32_32, (ScriptValue)ScriptValue.of((String)var33_33), (ScriptValue)ScriptValue.of((String)var34_34), (ScriptContext)var1_1);
            }
        } else {
            v4 /* !! */  = ScriptValue.NULL;
        }
        var38_38 = v4 /* !! */ ;
        var0.val("progress", var38_38);
        if (ScriptFormula.valuesEqual((ScriptValue)var38_38, (ScriptValue)var1_1.getClassOrVar("null"))) {
            var39_39 = 0.0;
            var41_40 = ScriptValue.of((double)0.0);
            var0.val("progress", var41_40);
        }
        var42_41 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
        var0.val("progress", var42_41);
        if (var42_41.asNum() < var1_1.getNum("CHUTE_STEP")) {
            var43_42 = var1_1.getClassOrVar("Machine");
            if (var43_42 != ScriptValue.NULL) {
                var44_43 = "_chute_progress";
                var45_44 = "int";
                var46_45 = var42_41;
                if (var43_42 instanceof ScriptValue.Obj && (var48_47 = (var47_46 = (ScriptValue.Obj)var43_42).instance()) != null && !(var48_47 instanceof PolyClass) && var47_46.typeName().equals("Machine")) {
                    var49_48 = new PolyClassMachine_v3(var48_47);
                    v5 /* !! */  = ScriptValue.of((boolean)var49_48.tm$82_set_typed(var44_43, var45_44, var46_45));
                } else {
                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var43_42, (ScriptValue)ScriptValue.of((String)var44_43), (ScriptValue)ScriptValue.of((String)var45_44), (ScriptValue)var46_45, (ScriptContext)var1_1);
                }
            } else {
                v5 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var50_49 = ScriptContext.builder().copyFrom(var1_1);
        var51_50 = ChuteUtils._chuteOutChute(var50_49);
        var0.val("below_info", var51_50);
        if (ScriptFormula.valuesEqual((ScriptValue)var51_50, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
            var52_51 = ScriptFormula.subscriptGet((ScriptValue)var51_50, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0)));
            var0.val("below", var52_51);
            var53_52 = ScriptFormula.subscriptGet((ScriptValue)var51_50, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
            var0.val("below_is_smart", var53_52);
            var54_53 = var1_1.getClassOrVar("below");
            var55_54 = var54_53 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var54_53, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "_chute_item")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "item")), (ScriptContext)var1_1) : ScriptValue.NULL;
            var0.val("below_holding", var55_54);
            var56_55 = new ArrayList<ScriptValue>();
            var56_55.add(var55_54);
            if (ScriptFormula.callBuiltin((String)"is_empty", var56_55, (ScriptContext)var1_1).asBool()) {
                var57_56 = new ArrayList<ScriptValue>();
                var57_56.add(var13_13);
                var58_57 = ScriptFormula.callBuiltin((String)"item_count", var57_56, (ScriptContext)var1_1);
                var0.val("full", var58_57);
                if ((var53_52.asBool() ^ true) != false && var58_57.asNum() > var1_1.getNum("PLAIN_TRANSFER_CAP") != false) {
                    var59_58 = var1_1.getClassOrVar("below");
                    v6 /* !! */  = var59_58 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var59_58, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "_chute_item")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "item")), (ScriptValue)((var60_59 = var1_1.getClassOrVar("holding")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var60_59, (ScriptValue)var1_1.getClassOrVar("PLAIN_TRANSFER_CAP"), (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1) : ScriptValue.NULL;
                    var61_60 = var1_1.getClassOrVar("Machine");
                    if (var61_60 != ScriptValue.NULL) {
                        var62_61 = "_chute_item";
                        var63_62 = "item";
                        var65_63 = var1_1.getClassOrVar("holding");
                        v7 /* !! */  = var64_64 = var65_63 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var65_63, (ScriptValue)ScriptValue.of((double)(var58_57.asNum() - var1_1.getNum("PLAIN_TRANSFER_CAP"))), (ScriptContext)var1_1) : ScriptValue.NULL;
                        if (var61_60 instanceof ScriptValue.Obj && (var67_66 = (var66_65 = (ScriptValue.Obj)var61_60).instance()) != null && !(var67_66 instanceof PolyClass) && var66_65.typeName().equals("Machine")) {
                            var68_67 = new PolyClassMachine_v3(var67_66);
                            v8 /* !! */  = ScriptValue.of((boolean)var68_67.tm$82_set_typed(var62_61, var63_62, var64_64));
                        } else {
                            v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var61_60, (ScriptValue)ScriptValue.of((String)var62_61), (ScriptValue)ScriptValue.of((String)var63_62), (ScriptValue)var64_64, (ScriptContext)var1_1);
                        }
                    } else {
                        v8 /* !! */  = ScriptValue.NULL;
                    }
                } else {
                    var69_68 = var1_1.getClassOrVar("below");
                    v9 /* !! */  = var69_68 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var69_68, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "_chute_item")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "item")), (ScriptValue)var13_13, (ScriptContext)var1_1) : ScriptValue.NULL;
                    var70_69 = var1_1.getClassOrVar("Machine");
                    if (var70_69 != ScriptValue.NULL) {
                        var71_70 = "_chute_item";
                        var72_71 = "item";
                        var73_72 = var1_1.getClassOrVar("null");
                        if (var70_69 instanceof ScriptValue.Obj && (var75_74 = (var74_73 = (ScriptValue.Obj)var70_69).instance()) != null && !(var75_74 instanceof PolyClass) && var74_73.typeName().equals("Machine")) {
                            var76_75 = new PolyClassMachine_v3(var75_74);
                            v10 /* !! */  = ScriptValue.of((boolean)var76_75.tm$82_set_typed(var71_70, var72_71, var73_72));
                        } else {
                            v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var70_69, (ScriptValue)ScriptValue.of((String)var71_70), (ScriptValue)ScriptValue.of((String)var72_71), (ScriptValue)var73_72, (ScriptContext)var1_1);
                        }
                    } else {
                        v10 /* !! */  = ScriptValue.NULL;
                    }
                }
                var77_76 = var1_1.getClassOrVar("below");
                v11 /* !! */  = var77_76 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var77_76, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "_chute_progress")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "int")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0)), (ScriptContext)var1_1) : ScriptValue.NULL;
                var78_77 = var1_1.getClassOrVar("Machine");
                if (var78_77 != ScriptValue.NULL) {
                    var79_78 = "_chute_progress";
                    var80_79 = "int";
                    var81_80 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0);
                    if (var78_77 instanceof ScriptValue.Obj && (var83_82 = (var82_81 = (ScriptValue.Obj)var78_77).instance()) != null && !(var83_82 instanceof PolyClass) && var82_81.typeName().equals("Machine")) {
                        var84_83 = new PolyClassMachine_v3(var83_82);
                        v12 /* !! */  = ScriptValue.of((boolean)var84_83.tm$82_set_typed(var79_78, var80_79, var81_80));
                    } else {
                        v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var78_77, (ScriptValue)ScriptValue.of((String)var79_78), (ScriptValue)ScriptValue.of((String)var80_79), (ScriptValue)var81_80, (ScriptContext)var1_1);
                    }
                } else {
                    v12 /* !! */  = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        var85_84 = ScriptContext.builder().copyFrom(var1_1);
        var85_84.val("item", var13_13);
        var86_85 = ChuteUtils._chuteGiveOut(var85_84);
        var0.val("leftover", var86_85);
        var87_86 = new ArrayList<ScriptValue>();
        var87_86.add(var86_85);
        if (ScriptFormula.callBuiltin((String)"is_empty", var87_86, (ScriptContext)var1_1).asBool() ^ true) {
            var88_87 = ScriptContext.builder().copyFrom(var1_1);
            var89_88 = ChuteUtils._chuteOutOffset(var88_87);
            var0.val("off", var89_88);
            var90_89 = var1_1.getClassOrVar("Machine");
            if (var90_89 != ScriptValue.NULL) {
                var91_90 = var86_85;
                var92_91 = ScriptFormula.subscriptGet((ScriptValue)var89_88, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0)));
                var93_92 = ScriptFormula.subscriptGet((ScriptValue)var89_88, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
                var94_93 = ScriptFormula.subscriptGet((ScriptValue)var89_88, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 2.0)));
                if (var90_89 instanceof ScriptValue.Obj && (var96_95 = (var95_94 = (ScriptValue.Obj)var90_89).instance()) != null && !(var96_95 instanceof PolyClass) && var95_94.typeName().equals("Machine")) {
                    var97_96 = new PolyClassMachine_v3(var96_95);
                    v13 /* !! */  = ScriptValue.of((boolean)var97_96.tm$50_drop_item_at(var91_90, var92_91.asNum(), var93_92.asNum(), var94_93.asNum()));
                } else {
                    v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)var90_89, (ScriptValue)var91_90, (ScriptValue)var92_91, (ScriptValue)var93_92, (ScriptValue)var94_93, (ScriptContext)var1_1);
                }
            } else {
                v13 /* !! */  = ScriptValue.NULL;
            }
        }
        if ((var98_97 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
            var99_98 = "_chute_item";
            var100_99 = "item";
            var101_100 = var1_1.getClassOrVar("null");
            if (var98_97 instanceof ScriptValue.Obj && (var103_102 = (var102_101 = (ScriptValue.Obj)var98_97).instance()) != null && !(var103_102 instanceof PolyClass) && var102_101.typeName().equals("Machine")) {
                var104_103 = new PolyClassMachine_v3(var103_102);
                v14 /* !! */  = ScriptValue.of((boolean)var104_103.tm$82_set_typed(var99_98, var100_99, var101_100));
            } else {
                v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var98_97, (ScriptValue)ScriptValue.of((String)var99_98), (ScriptValue)ScriptValue.of((String)var100_99), (ScriptValue)var101_100, (ScriptContext)var1_1);
            }
        } else {
            v14 /* !! */  = ScriptValue.NULL;
        }
        var105_104 = var1_1.getClassOrVar("Machine");
        if (var105_104 != ScriptValue.NULL) {
            var106_105 = "_chute_progress";
            var107_106 = "int";
            var108_107 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 0.0);
            if (var105_104 instanceof ScriptValue.Obj && (var110_109 = (var109_108 = (ScriptValue.Obj)var105_104).instance()) != null && !(var110_109 instanceof PolyClass) && var109_108.typeName().equals("Machine")) {
                var111_110 = new PolyClassMachine_v3(var110_109);
                v15 /* !! */  = ScriptValue.of((boolean)var111_110.tm$82_set_typed(var106_105, var107_106, var108_107));
            } else {
                v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var105_104, (ScriptValue)ScriptValue.of((String)var106_105), (ScriptValue)ScriptValue.of((String)var107_106), (ScriptValue)var108_107, (ScriptContext)var1_1);
            }
        } else {
            v15 /* !! */  = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _chuteToggleWindow(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "_chute_window";
            String string2 = "int";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object3;
            String string = "_chute_window";
            String string3 = "int";
            ScriptValue scriptValue5 = ScriptValue.of((double)d);
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue5));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue7;
            String string = "window";
            ScriptValue scriptValue8 = scriptValue7 = d == 1.0 ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "false"));
            if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object4);
                v3 = ScriptValue.of((boolean)polyClassMachine_v3.tm$16_set_property(string, scriptValue7.asStr()));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _chuteCycleFacing(ScriptContext.Builder builder) {
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v3 polyClassMachine_v3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v3.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        String string = "facing";
        if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v2 polyClassBlock_v2 = new PolyClassBlock_v2(object);
            callSite = polyClassBlock_v2.tm$24_property(string);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("cur", (ScriptValue)callSite2);
        if (ScriptFormula.valuesEqual((ScriptValue)callSite2, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "down");
            builder.val("cur", scriptValue3);
        }
        double d = 0.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("FACING_CYCLE"));
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("f", scriptValue5);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("f"), (ScriptValue)scriptContext.getClassOrVar("cur"))) break;
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0)));
                builder.val("idx", scriptValue6);
            }
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("FACING_CYCLE");
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("FACING_CYCLE"));
        double d2 = ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum();
        ScriptValue scriptValue8 = ScriptFormula.subscriptGet((ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)(d2 == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", ChuteUtils.class, 1.0))).asNum() % d2)));
        builder.val("next", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object2;
            String string2 = "facing";
            ScriptValue scriptValue10 = scriptValue8;
            if (scriptValue9 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v32 = new PolyClassMachine_v3(object2);
                v2 = ScriptValue.of((boolean)polyClassMachine_v32.tm$16_set_property(string2, scriptValue10.asStr()));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    /*
     * Enabled aggressive block sorting
     */
    public static ScriptValue _isGlassItem(ScriptContext.Builder builder) {
        boolean bl;
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("id"), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("id"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", ChuteUtils.class, "glass"));
            if (ScriptFormula.callBuiltin((String)"contains", arrayList, (ScriptContext)scriptContext).asBool()) {
                bl = true;
                return ScriptValue.of((boolean)bl);
            }
        }
        bl = false;
        return ScriptValue.of((boolean)bl);
    }

    public static ScriptValue _chuteDropHeld(ScriptContext.Builder builder) {
        block6: {
            Object object;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "_chute_item";
                String string2 = "item";
                if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                    object = polyClassMachine_v3.tm$34_get_typed(string, string2);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue2 = object;
            builder.val("holding", scriptValue2);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue2);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true)) break block6;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptValue2);
                v1 = scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object3).um$4_drop_item(arrayList2) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
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
