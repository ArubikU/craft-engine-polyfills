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
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.BeltUtils
 */
package dev.arubik.craftengine.script.gen.storage;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.BeltUtils;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class ChuteUtils {
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
            return ScriptValue.of((double)64.0);
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
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ChuteUtils._chuteAmountMax(builder2);
        builder.val("max", scriptValue);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue2 = ChuteUtils._chutePullAmount(builder3);
        builder.val("current", scriptValue2);
        ScriptValue scriptValue3 = scriptValue2.asNum() > scriptValue.asNum() ? scriptValue : scriptValue2;
        builder.val("initial", scriptValue3);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Machine"));
        arrayList2.add(ScriptValue.of((String)"Set"));
        arrayList2.add(ScriptValue.of((String)"storage/chute_utils.pf:_chute_on_amount_dialog_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(ScriptValue.of((String)"storage/chute_utils.pf:_chute_on_amount_dialog_closed"));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add(ScriptValue.of((String)"amount"));
        arrayList4.add(ScriptValue.of((String)"Amount (-1 = any)"));
        arrayList4.add(ScriptValue.of((double)(-1.0)));
        arrayList4.add(scriptValue);
        arrayList4.add(ScriptValue.of((double)1.0));
        arrayList4.add(scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Dialog");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(ScriptValue.of((String)"Pull Amount"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue4, arrayList5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "on_close", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_number", (ScriptValue)object, arrayList4, (ScriptContext)scriptContext), arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
        ScriptValue.Obj obj;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"facing"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object2).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("facing", (ScriptValue)callSite);
        if (ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)callSite, (String)"down")) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(ScriptValue.of((double)0.0));
            arrayList2.add(ScriptValue.of((double)(-1.0)));
            arrayList2.add(ScriptValue.of((double)0.0));
            return new ScriptValue.Array(arrayList2);
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("DIR_VEC");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
            arrayList3.add(callSite);
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(ScriptValue.of((double)0.0));
            arrayList4.add(ScriptValue.of((double)0.0));
            arrayList4.add(ScriptValue.of((double)0.0));
            arrayList3.add((CallSite)new ScriptValue.Array(arrayList4));
            object = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue2, arrayList3, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("v", scriptValue3);
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)0.0)));
        arrayList5.add(ScriptValue.of((double)(-1.0)));
        arrayList5.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)2.0)));
        return new ScriptValue.Array(arrayList5);
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
            ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)0.0));
            ScriptValue scriptValue6 = ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)1.0));
            ScriptValue scriptValue7 = ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)2.0));
            if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$68_block_at(scriptValue5.asNum(), scriptValue6.asNum(), scriptValue7.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue5);
                arrayList.add(scriptValue6);
                arrayList.add(scriptValue7);
                object = PolyDispatch.bootstrapCall("memberCall", "block_at", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
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
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = ChuteUtils._chuteOutOffset(builder2);
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

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue _chutePull(ScriptContext.Builder var0) {
        block34: {
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
            var16_15 = ScriptContext.builder().copyFrom(var1_1);
            var16_15.val("face", ScriptValue.of((String)"up"));
            var16_15.val("validator", var3_3);
            if (!(BeltUtils.depotCount((ScriptContext.Builder)var16_15).asNum() >= var1_1.getNum("cap"))) {
                v1 = false;
            } else lbl-1000:
            // 2 sources

            {
                v1 = true;
            }
            if (v1) {
                var17_16 = ScriptContext.builder().copyFrom(var1_1);
                var17_16.val("face", ScriptValue.of((String)"up"));
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
                    var28_23 = new PolyClassMachine_v4(var27_22);
                    v2 /* !! */  = var28_23.tm$94_nearby_entities(var24_20);
                } else {
                    var29_24 = new ArrayList<ScriptValue>();
                    var29_24.add(ScriptValue.of((double)var24_20));
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var23_19, var29_24, (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
            var20_25 = ScriptProgram.rowsOf((ScriptValue)v2 /* !! */ , (int)1);
            if (var20_25 == null) break block34;
            for (ScriptValue[] var22_27 : var20_25) {
                var0.val("entity", var22_27.length > 0 ? var22_27[0] : ScriptValue.NULL);
                var30_28 = ScriptContext.builder().copyFrom(var1_1);
                var30_28.val("entity", var1_1.getClassOrVar("entity"));
                if (!Utils.isRestingItem(var30_28).asBool()) ** GOTO lbl-1000
                var31_29 = var1_1.getClassOrVar("entity");
                v3 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var31_29 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var31_29, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1).asNum();
                var32_30 = var1_1.getClassOrVar("Machine");
                v4 /* !! */  = var32_30 != ScriptValue.NULL ? (var32_30 instanceof ScriptValue.Obj && (var34_32 = (var33_31 = (ScriptValue.Obj)var32_30).instance()) != null && !(var34_32 instanceof PolyClass) && var33_31.typeName().equals("Machine") ? new PolyClassMachine_v4(var34_32).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var32_30, (ScriptContext)var1_1)) : ScriptValue.NULL;
                if (v3 >= v4 /* !! */ .asNum()) {
                    v5 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v5 = false;
                }
                if (!v5) continue;
                var35_33 = var1_1.getClassOrVar("entity");
                var36_34 = var35_33 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var35_33, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("item", var36_34);
                if (ScriptFormula.valuesEqual((ScriptValue)var3_3, (ScriptValue)var1_1.getClassOrVar("null"))) ** GOTO lbl-1000
                var37_35 = var1_1.getClassOrVar("item");
                if (var37_35 != ScriptValue.NULL) {
                    var38_36 = new ArrayList<ScriptValue>();
                    var38_36.add(var3_3);
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "matches", (ScriptValue)var37_35, var38_36, (ScriptContext)var1_1);
                } else {
                    v6 /* !! */  = ScriptValue.NULL;
                }
                if (!v6 /* !! */ .asBool()) {
                    v7 = false;
                } else lbl-1000:
                // 2 sources

                {
                    v7 = true;
                }
                if (!v7) continue;
                var39_37 = new ArrayList<ScriptValue>();
                var39_37.add(var36_34);
                var40_38 = ScriptFormula.callBuiltin((String)"item_count", var39_37, (ScriptContext)var1_1);
                var0.val("full", var40_38);
                if (var1_1.getBool("exact") != false && var40_38.asNum() < var1_1.getNum("cap") != false) continue;
                if ((var1_1.getBool("smart") ^ true) != false && var40_38.asNum() > var1_1.getNum("PLAIN_TRANSFER_CAP") != false) {
                    var41_39 = var1_1.getClassOrVar("entity");
                    if (var41_39 != ScriptValue.NULL) {
                        var42_40 = new ArrayList<ScriptValue>();
                        var43_41 = new ArrayList<ScriptValue>();
                        var44_42 = var1_1.getClassOrVar("item");
                        var43_41.add((ScriptValue)(var44_42 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var44_42, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var43_41.add(ScriptValue.of((double)(var40_38.asNum() - var1_1.getNum("PLAIN_TRANSFER_CAP"))));
                        var42_40.add(ScriptFormula.callBuiltin((String)"MinecraftItem", var43_41, (ScriptContext)var1_1));
                        v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)var41_39, var42_40, (ScriptContext)var1_1);
                    } else {
                        v8 /* !! */  = ScriptValue.NULL;
                    }
                    var45_43 = var1_1.getClassOrVar("item");
                    if (var45_43 != ScriptValue.NULL) {
                        var46_44 = new ArrayList<ScriptValue>();
                        var46_44.add(var1_1.getClassOrVar("PLAIN_TRANSFER_CAP"));
                        v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var45_43, var46_44, (ScriptContext)var1_1);
                    } else {
                        v9 /* !! */  = ScriptValue.NULL;
                    }
                    return v9 /* !! */ ;
                }
                if (var1_1.getBool("exact") != false && var40_38.asNum() > var1_1.getNum("cap") != false) {
                    var47_45 = var1_1.getClassOrVar("entity");
                    if (var47_45 != ScriptValue.NULL) {
                        var48_46 = new ArrayList<ScriptValue>();
                        var49_47 = new ArrayList<ScriptValue>();
                        var50_48 = var1_1.getClassOrVar("item");
                        var49_47.add((ScriptValue)(var50_48 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var50_48, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var49_47.add(ScriptValue.of((double)(var40_38.asNum() - var1_1.getNum("cap"))));
                        var48_46.add(ScriptFormula.callBuiltin((String)"MinecraftItem", var49_47, (ScriptContext)var1_1));
                        v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)var47_45, var48_46, (ScriptContext)var1_1);
                    } else {
                        v10 /* !! */  = ScriptValue.NULL;
                    }
                    var51_49 = var1_1.getClassOrVar("item");
                    if (var51_49 != ScriptValue.NULL) {
                        var52_50 = new ArrayList<ScriptValue>();
                        var52_50.add(var1_1.getClassOrVar("cap"));
                        v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var51_49, var52_50, (ScriptContext)var1_1);
                    } else {
                        v11 /* !! */  = ScriptValue.NULL;
                    }
                    return v11 /* !! */ ;
                }
                var53_51 = var1_1.getClassOrVar("entity");
                if (var53_51 != ScriptValue.NULL) {
                    var54_52 = new ArrayList<E>();
                    v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var53_51, var54_52, (ScriptContext)var1_1);
                } else {
                    v12 /* !! */  = ScriptValue.NULL;
                }
                return var36_34;
            }
        }
        if ((var55_53 = var1_1.getClassOrVar("Item")) != ScriptValue.NULL) {
            var56_54 = new ArrayList<ScriptValue>();
            var56_54.add(ScriptValue.of((String)"minecraft:air"));
            v13 /* !! */  = var55_53 instanceof ScriptValue.Obj && (var58_56 = (var57_55 = (ScriptValue.Obj)var55_53).instance()) != null && !(var58_56 instanceof PolyClass) && var57_55.typeName().equals("Item") ? new PolyClassItem(var58_56).um$21_create(var56_54) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var55_53, var56_54, (ScriptContext)var1_1);
        } else {
            v13 /* !! */  = ScriptValue.NULL;
        }
        return v13 /* !! */ ;
    }

    public static ScriptValue _chuteSyncPowered(ScriptContext.Builder builder) {
        block5: {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue.Obj obj2;
            Object object2;
            ScriptContext scriptContext = builder.peek();
            if (scriptContext.getBool("smart") ^ true) {
                return ScriptValue.NULL;
            }
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"powered"));
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v4(object2).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
            builder.val("cur", (ScriptValue)callSite);
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue3 = PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptContext)scriptContext).asBool() ? ScriptValue.of((String)"true") : ScriptValue.of((String)"false");
            builder.val("want", scriptValue3);
            if (!(ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptValue3) ^ true)) break block5;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj3;
                Object object3;
                String string = "powered";
                ScriptValue scriptValue5 = scriptValue3;
                if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$16_set_property(string, scriptValue5.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(ScriptValue.of((String)string));
                    arrayList2.add(scriptValue5);
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue4, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
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
        var3_3 = var1_1.getClassOrVar("Machine");
        v0 /* !! */  = var3_3 != ScriptValue.NULL ? (var3_3 instanceof ScriptValue.Obj && (var5_5 = (var4_4 = (ScriptValue.Obj)var3_3).instance()) != null && !(var5_5 instanceof PolyClass) && var4_4.typeName().equals("Machine") ? new PolyClassMachine_v4(var5_5).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)var3_3, (ScriptContext)var1_1)) : ScriptValue.NULL;
        if (PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)v0 /* !! */ , (ScriptContext)var1_1).asBool()) {
            v1 = true;
        } else lbl-1000:
        // 2 sources

        {
            v1 = false;
        }
        if (v1) {
            return ScriptValue.NULL;
        }
        var6_6 = var1_1.getClassOrVar("Machine");
        if (var6_6 != ScriptValue.NULL) {
            var7_7 = "_chute_item";
            var8_8 = "item";
            if (var6_6 instanceof ScriptValue.Obj && (var10_10 = (var9_9 = (ScriptValue.Obj)var6_6).instance()) != null && !(var10_10 instanceof PolyClass) && var9_9.typeName().equals("Machine")) {
                var11_11 = new PolyClassMachine_v4(var10_10);
                v2 /* !! */  = var11_11.tm$34_get_typed(var7_7, var8_8);
            } else {
                var12_12 = new ArrayList<ScriptValue>();
                var12_12.add(ScriptValue.of((String)var7_7));
                var12_12.add(ScriptValue.of((String)var8_8));
                v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var6_6, var12_12, (ScriptContext)var1_1);
            }
        } else {
            v2 /* !! */  = ScriptValue.NULL;
        }
        var13_13 = v2 /* !! */ ;
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
                        var24_24 = new PolyClassMachine_v4(var23_23);
                        v3 /* !! */  = ScriptValue.of((boolean)var24_24.tm$82_set_typed(var19_19, var20_20, var21_21));
                    } else {
                        var25_25 = new ArrayList<ScriptValue>();
                        var25_25.add(ScriptValue.of((String)var19_19));
                        var25_25.add(ScriptValue.of((String)var20_20));
                        var25_25.add(var21_21);
                        v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var18_18, var25_25, (ScriptContext)var1_1);
                    }
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                var26_26 = var1_1.getClassOrVar("Machine");
                if (var26_26 != ScriptValue.NULL) {
                    var27_27 = "_chute_progress";
                    var28_28 = "int";
                    var29_29 = ScriptValue.of((double)0.0);
                    if (var26_26 instanceof ScriptValue.Obj && (var31_31 = (var30_30 = (ScriptValue.Obj)var26_26).instance()) != null && !(var31_31 instanceof PolyClass) && var30_30.typeName().equals("Machine")) {
                        var32_32 = new PolyClassMachine_v4(var31_31);
                        v4 /* !! */  = ScriptValue.of((boolean)var32_32.tm$82_set_typed(var27_27, var28_28, var29_29));
                    } else {
                        var33_33 = new ArrayList<ScriptValue>();
                        var33_33.add(ScriptValue.of((String)var27_27));
                        var33_33.add(ScriptValue.of((String)var28_28));
                        var33_33.add(var29_29);
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var26_26, var33_33, (ScriptContext)var1_1);
                    }
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        var34_34 = var1_1.getClassOrVar("Machine");
        if (var34_34 != ScriptValue.NULL) {
            var35_35 = "_chute_progress";
            var36_36 = "int";
            if (var34_34 instanceof ScriptValue.Obj && (var38_38 = (var37_37 = (ScriptValue.Obj)var34_34).instance()) != null && !(var38_38 instanceof PolyClass) && var37_37.typeName().equals("Machine")) {
                var39_39 = new PolyClassMachine_v4(var38_38);
                v5 /* !! */  = var39_39.tm$34_get_typed(var35_35, var36_36);
            } else {
                var40_40 = new ArrayList<ScriptValue>();
                var40_40.add(ScriptValue.of((String)var35_35));
                var40_40.add(ScriptValue.of((String)var36_36));
                v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var34_34, var40_40, (ScriptContext)var1_1);
            }
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        var41_41 = v5 /* !! */ ;
        var0.val("progress", var41_41);
        if (ScriptFormula.valuesEqual((ScriptValue)var41_41, (ScriptValue)var1_1.getClassOrVar("null"))) {
            var42_42 = 0.0;
            var44_43 = ScriptValue.of((double)0.0);
            var0.val("progress", var44_43);
        }
        var45_44 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)1.0));
        var0.val("progress", var45_44);
        if (var45_44.asNum() < var1_1.getNum("CHUTE_STEP")) {
            var46_45 = var1_1.getClassOrVar("Machine");
            if (var46_45 != ScriptValue.NULL) {
                var47_46 = "_chute_progress";
                var48_47 = "int";
                var49_48 = var45_44;
                if (var46_45 instanceof ScriptValue.Obj && (var51_50 = (var50_49 = (ScriptValue.Obj)var46_45).instance()) != null && !(var51_50 instanceof PolyClass) && var50_49.typeName().equals("Machine")) {
                    var52_51 = new PolyClassMachine_v4(var51_50);
                    v6 /* !! */  = ScriptValue.of((boolean)var52_51.tm$82_set_typed(var47_46, var48_47, var49_48));
                } else {
                    var53_52 = new ArrayList<ScriptValue>();
                    var53_52.add(ScriptValue.of((String)var47_46));
                    var53_52.add(ScriptValue.of((String)var48_47));
                    var53_52.add(var49_48);
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var46_45, var53_52, (ScriptContext)var1_1);
                }
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var54_53 = ScriptContext.builder().copyFrom(var1_1);
        var55_54 = ChuteUtils._chuteOutChute(var54_53);
        var0.val("below_info", var55_54);
        if (ScriptFormula.valuesEqual((ScriptValue)var55_54, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
            var56_55 = ScriptFormula.subscriptGet((ScriptValue)var55_54, (ScriptValue)ScriptValue.of((double)0.0));
            var0.val("below", var56_55);
            var57_56 = ScriptFormula.subscriptGet((ScriptValue)var55_54, (ScriptValue)ScriptValue.of((double)1.0));
            var0.val("below_is_smart", var57_56);
            var58_57 = var1_1.getClassOrVar("below");
            if (var58_57 != ScriptValue.NULL) {
                var59_58 = new ArrayList<ScriptValue>();
                var59_58.add(ScriptValue.of((String)"_chute_item"));
                var59_58.add(ScriptValue.of((String)"item"));
                v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var58_57, var59_58, (ScriptContext)var1_1);
            } else {
                v7 /* !! */  = ScriptValue.NULL;
            }
            var60_59 = v7 /* !! */ ;
            var0.val("below_holding", var60_59);
            var61_60 = new ArrayList<ScriptValue>();
            var61_60.add(var60_59);
            if (ScriptFormula.callBuiltin((String)"is_empty", var61_60, (ScriptContext)var1_1).asBool()) {
                var62_61 = new ArrayList<ScriptValue>();
                var62_61.add(var13_13);
                var63_62 = ScriptFormula.callBuiltin((String)"item_count", var62_61, (ScriptContext)var1_1);
                var0.val("full", var63_62);
                if ((var57_56.asBool() ^ true) != false && var63_62.asNum() > var1_1.getNum("PLAIN_TRANSFER_CAP") != false) {
                    var64_63 = var1_1.getClassOrVar("below");
                    if (var64_63 != ScriptValue.NULL) {
                        var65_64 = new ArrayList<ScriptValue>();
                        var65_64.add(ScriptValue.of((String)"_chute_item"));
                        var65_64.add(ScriptValue.of((String)"item"));
                        var66_65 = var1_1.getClassOrVar("holding");
                        if (var66_65 != ScriptValue.NULL) {
                            var67_66 = new ArrayList<ScriptValue>();
                            var67_66.add(var1_1.getClassOrVar("PLAIN_TRANSFER_CAP"));
                            v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var66_65, var67_66, (ScriptContext)var1_1);
                        } else {
                            v8 /* !! */  = ScriptValue.NULL;
                        }
                        var65_64.add(v8 /* !! */ );
                        v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var64_63, var65_64, (ScriptContext)var1_1);
                    } else {
                        v9 /* !! */  = ScriptValue.NULL;
                    }
                    var68_67 = var1_1.getClassOrVar("Machine");
                    if (var68_67 != ScriptValue.NULL) {
                        var69_68 = "_chute_item";
                        var70_69 = "item";
                        var72_70 = var1_1.getClassOrVar("holding");
                        if (var72_70 != ScriptValue.NULL) {
                            var73_71 = new ArrayList<ScriptValue>();
                            var73_71.add(ScriptValue.of((double)(var63_62.asNum() - var1_1.getNum("PLAIN_TRANSFER_CAP"))));
                            v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var72_70, var73_71, (ScriptContext)var1_1);
                        } else {
                            v10 /* !! */  = var71_72 = ScriptValue.NULL;
                        }
                        if (var68_67 instanceof ScriptValue.Obj && (var75_74 = (var74_73 = (ScriptValue.Obj)var68_67).instance()) != null && !(var75_74 instanceof PolyClass) && var74_73.typeName().equals("Machine")) {
                            var76_75 = new PolyClassMachine_v4(var75_74);
                            v11 /* !! */  = ScriptValue.of((boolean)var76_75.tm$82_set_typed(var69_68, var70_69, var71_72));
                        } else {
                            var77_76 = new ArrayList<ScriptValue>();
                            var77_76.add(ScriptValue.of((String)var69_68));
                            var77_76.add(ScriptValue.of((String)var70_69));
                            var77_76.add(var71_72);
                            v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var68_67, var77_76, (ScriptContext)var1_1);
                        }
                    } else {
                        v11 /* !! */  = ScriptValue.NULL;
                    }
                } else {
                    var78_77 = var1_1.getClassOrVar("below");
                    if (var78_77 != ScriptValue.NULL) {
                        var79_78 = new ArrayList<ScriptValue>();
                        var79_78.add(ScriptValue.of((String)"_chute_item"));
                        var79_78.add(ScriptValue.of((String)"item"));
                        var79_78.add(var13_13);
                        v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var78_77, var79_78, (ScriptContext)var1_1);
                    } else {
                        v12 /* !! */  = ScriptValue.NULL;
                    }
                    var80_79 = var1_1.getClassOrVar("Machine");
                    if (var80_79 != ScriptValue.NULL) {
                        var81_80 = "_chute_item";
                        var82_81 = "item";
                        var83_82 = var1_1.getClassOrVar("null");
                        if (var80_79 instanceof ScriptValue.Obj && (var85_84 = (var84_83 = (ScriptValue.Obj)var80_79).instance()) != null && !(var85_84 instanceof PolyClass) && var84_83.typeName().equals("Machine")) {
                            var86_85 = new PolyClassMachine_v4(var85_84);
                            v13 /* !! */  = ScriptValue.of((boolean)var86_85.tm$82_set_typed(var81_80, var82_81, var83_82));
                        } else {
                            var87_86 = new ArrayList<ScriptValue>();
                            var87_86.add(ScriptValue.of((String)var81_80));
                            var87_86.add(ScriptValue.of((String)var82_81));
                            var87_86.add(var83_82);
                            v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var80_79, var87_86, (ScriptContext)var1_1);
                        }
                    } else {
                        v13 /* !! */  = ScriptValue.NULL;
                    }
                }
                var88_87 = var1_1.getClassOrVar("below");
                if (var88_87 != ScriptValue.NULL) {
                    var89_88 = new ArrayList<ScriptValue>();
                    var89_88.add(ScriptValue.of((String)"_chute_progress"));
                    var89_88.add(ScriptValue.of((String)"int"));
                    var89_88.add(ScriptValue.of((double)0.0));
                    v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var88_87, var89_88, (ScriptContext)var1_1);
                } else {
                    v14 /* !! */  = ScriptValue.NULL;
                }
                var90_89 = var1_1.getClassOrVar("Machine");
                if (var90_89 != ScriptValue.NULL) {
                    var91_90 = "_chute_progress";
                    var92_91 = "int";
                    var93_92 = ScriptValue.of((double)0.0);
                    if (var90_89 instanceof ScriptValue.Obj && (var95_94 = (var94_93 = (ScriptValue.Obj)var90_89).instance()) != null && !(var95_94 instanceof PolyClass) && var94_93.typeName().equals("Machine")) {
                        var96_95 = new PolyClassMachine_v4(var95_94);
                        v15 /* !! */  = ScriptValue.of((boolean)var96_95.tm$82_set_typed(var91_90, var92_91, var93_92));
                    } else {
                        var97_96 = new ArrayList<ScriptValue>();
                        var97_96.add(ScriptValue.of((String)var91_90));
                        var97_96.add(ScriptValue.of((String)var92_91));
                        var97_96.add(var93_92);
                        v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var90_89, var97_96, (ScriptContext)var1_1);
                    }
                } else {
                    v15 /* !! */  = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        var98_97 = ScriptContext.builder().copyFrom(var1_1);
        var98_97.val("item", var13_13);
        var99_98 = ChuteUtils._chuteGiveOut(var98_97);
        var0.val("leftover", var99_98);
        var100_99 = new ArrayList<ScriptValue>();
        var100_99.add(var99_98);
        if (ScriptFormula.callBuiltin((String)"is_empty", var100_99, (ScriptContext)var1_1).asBool() ^ true) {
            var101_100 = ScriptContext.builder().copyFrom(var1_1);
            var102_101 = ChuteUtils._chuteOutOffset(var101_100);
            var0.val("off", var102_101);
            var103_102 = var1_1.getClassOrVar("Machine");
            if (var103_102 != ScriptValue.NULL) {
                var104_103 = var99_98;
                var105_104 = ScriptFormula.subscriptGet((ScriptValue)var102_101, (ScriptValue)ScriptValue.of((double)0.0));
                var106_105 = ScriptFormula.subscriptGet((ScriptValue)var102_101, (ScriptValue)ScriptValue.of((double)1.0));
                var107_106 = ScriptFormula.subscriptGet((ScriptValue)var102_101, (ScriptValue)ScriptValue.of((double)2.0));
                if (var103_102 instanceof ScriptValue.Obj && (var109_108 = (var108_107 = (ScriptValue.Obj)var103_102).instance()) != null && !(var109_108 instanceof PolyClass) && var108_107.typeName().equals("Machine")) {
                    var110_109 = new PolyClassMachine_v4(var109_108);
                    v16 /* !! */  = ScriptValue.of((boolean)var110_109.tm$50_drop_item_at(var104_103, var105_104.asNum(), var106_105.asNum(), var107_106.asNum()));
                } else {
                    var111_110 = new ArrayList<ScriptValue>();
                    var111_110.add(var104_103);
                    var111_110.add(var105_104);
                    var111_110.add(var106_105);
                    var111_110.add(var107_106);
                    v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)var103_102, var111_110, (ScriptContext)var1_1);
                }
            } else {
                v16 /* !! */  = ScriptValue.NULL;
            }
        }
        if ((var112_111 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
            var113_112 = "_chute_item";
            var114_113 = "item";
            var115_114 = var1_1.getClassOrVar("null");
            if (var112_111 instanceof ScriptValue.Obj && (var117_116 = (var116_115 = (ScriptValue.Obj)var112_111).instance()) != null && !(var117_116 instanceof PolyClass) && var116_115.typeName().equals("Machine")) {
                var118_117 = new PolyClassMachine_v4(var117_116);
                v17 /* !! */  = ScriptValue.of((boolean)var118_117.tm$82_set_typed(var113_112, var114_113, var115_114));
            } else {
                var119_118 = new ArrayList<ScriptValue>();
                var119_118.add(ScriptValue.of((String)var113_112));
                var119_118.add(ScriptValue.of((String)var114_113));
                var119_118.add(var115_114);
                v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var112_111, var119_118, (ScriptContext)var1_1);
            }
        } else {
            v17 /* !! */  = ScriptValue.NULL;
        }
        var120_119 = var1_1.getClassOrVar("Machine");
        if (var120_119 != ScriptValue.NULL) {
            var121_120 = "_chute_progress";
            var122_121 = "int";
            var123_122 = ScriptValue.of((double)0.0);
            if (var120_119 instanceof ScriptValue.Obj && (var125_124 = (var124_123 = (ScriptValue.Obj)var120_119).instance()) != null && !(var125_124 instanceof PolyClass) && var124_123.typeName().equals("Machine")) {
                var126_125 = new PolyClassMachine_v4(var125_124);
                v18 /* !! */  = ScriptValue.of((boolean)var126_125.tm$82_set_typed(var121_120, var122_121, var123_122));
            } else {
                var127_126 = new ArrayList<ScriptValue>();
                var127_126.add(ScriptValue.of((String)var121_120));
                var127_126.add(ScriptValue.of((String)var122_121));
                var127_126.add(var123_122);
                v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var120_119, var127_126, (ScriptContext)var1_1);
            }
        } else {
            v18 /* !! */  = ScriptValue.NULL;
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("win", scriptValue2);
        double d = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("win"), (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("win"), (ScriptValue)ScriptValue.of((double)0.0)) ? 1.0 : 0.0;
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue5));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue5);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
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
            ScriptValue scriptValue8 = scriptValue7 = d == 1.0 ? ScriptValue.of((String)"true") : ScriptValue.of((String)"false");
            if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                v3 = ScriptValue.of((boolean)polyClassMachine_v4.tm$16_set_property(string, scriptValue7.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(scriptValue7);
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _chuteCycleFacing(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"facing"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("cur", (ScriptValue)callSite);
        if (ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue2 = ScriptValue.of((String)"down");
            builder.val("cur", scriptValue2);
        }
        double d = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue3);
        List list = ScriptProgram.rowsOf((ScriptValue)scriptContext.getClassOrVar("FACING_CYCLE"), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("f", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("f"), (ScriptValue)scriptContext.getClassOrVar("cur"))) break;
                ScriptValue scriptValue4 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("idx", scriptValue4);
            }
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("FACING_CYCLE");
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("FACING_CYCLE"));
        double d2 = ScriptFormula.callBuiltin((String)"size", arrayList2, (ScriptContext)scriptContext).asNum();
        ScriptValue scriptValue6 = ScriptFormula.subscriptGet((ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)(d2 == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)1.0)).asNum() % d2)));
        builder.val("next", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object2;
            String string = "facing";
            ScriptValue scriptValue8 = scriptValue6;
            if (scriptValue7 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$16_set_property(string, scriptValue8.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((String)string));
                arrayList3.add(scriptValue8);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue7, arrayList3, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
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
            arrayList.add(ScriptValue.of((String)"glass"));
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
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                    object = polyClassMachine_v4.tm$34_get_typed(string, string2);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string2));
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                v1 = scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object3).um$4_drop_item(arrayList2) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
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
        arrayList3.add(ScriptValue.of((String)"north"));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add(ScriptValue.of((double)0.0));
        arrayList4.add(ScriptValue.of((double)0.0));
        arrayList4.add(ScriptValue.of((double)(-1.0)));
        arrayList3.add(new ScriptValue.Array(arrayList4));
        arrayList3.add(ScriptValue.of((String)"south"));
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add(ScriptValue.of((double)0.0));
        arrayList5.add(ScriptValue.of((double)0.0));
        arrayList5.add(ScriptValue.of((double)1.0));
        arrayList3.add(new ScriptValue.Array(arrayList5));
        arrayList3.add(ScriptValue.of((String)"east"));
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add(ScriptValue.of((double)1.0));
        arrayList6.add(ScriptValue.of((double)0.0));
        arrayList6.add(ScriptValue.of((double)0.0));
        arrayList3.add(new ScriptValue.Array(arrayList6));
        arrayList3.add(ScriptValue.of((String)"west"));
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add(ScriptValue.of((double)(-1.0)));
        arrayList7.add(ScriptValue.of((double)0.0));
        arrayList7.add(ScriptValue.of((double)0.0));
        arrayList3.add(new ScriptValue.Array(arrayList7));
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext);
        builder.val("DIR_VEC", scriptValue2);
        ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
        arrayList8.add(ScriptValue.of((String)"down"));
        arrayList8.add(ScriptValue.of((String)"north"));
        arrayList8.add(ScriptValue.of((String)"east"));
        arrayList8.add(ScriptValue.of((String)"south"));
        arrayList8.add(ScriptValue.of((String)"west"));
        ScriptValue.Array array = new ScriptValue.Array(arrayList8);
        builder.val("FACING_CYCLE", (ScriptValue)array);
        double d2 = 16.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)16.0);
        builder.val("PLAIN_TRANSFER_CAP", scriptValue3);
    }
}
