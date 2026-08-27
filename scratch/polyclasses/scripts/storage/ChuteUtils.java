/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                v0 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue2));
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
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"facing"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$68_block_at(scriptValue5.asNum(), scriptValue6.asNum(), scriptValue7.asNum());
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                object2 = polyClassMachine.tm$17_container_at(scriptValue3.asNum(), scriptValue4.asNum(), scriptValue5.asNum());
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
            var16_15 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
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
                var17_16 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
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
                    var28_23 = new PolyClassMachine(var27_22);
                    v2 /* !! */  = var28_23.tm$94_nearby_entities(var24_20);
                } else {
                    var29_24 = new ArrayList<ScriptValue>();
                    var29_24.add(ScriptValue.of((double)var24_20));
                    v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var23_19, var29_24, (ScriptContext)var1_1);
                }
            } else {
                v2 /* !! */  = ScriptValue.NULL;
            }
            var20_25 = ScriptProgram.elementsOf((ScriptValue)v2 /* !! */ );
            if (var20_25 == null) break block34;
            for (ScriptValue var22_27 : var20_25) {
                var0.val("entity", var22_27);
                var30_28 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                var30_28.val("entity", var1_1.getClassOrVar("entity"));
                if (!Utils.isRestingItem(var30_28).asBool()) ** GOTO lbl-1000
                var31_29 = var1_1.getClassOrVar("entity");
                v3 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var31_29 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var31_29, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1).asNum();
                var32_30 = var1_1.getClassOrVar("Machine");
                v4 = var32_30 != ScriptValue.NULL ? ((var33_31 = PolyClassMachine.ofGuarded((ScriptValue)var32_30)) != null ? var33_31.tg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var32_30, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (v3 >= v4) {
                    v5 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v5 = false;
                }
                if (!v5) continue;
                var34_32 = var1_1.getClassOrVar("entity");
                var35_33 = var34_32 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var34_32, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("item", var35_33);
                if (ScriptFormula.valuesEqual((ScriptValue)var3_3, (ScriptValue)var1_1.getClassOrVar("null"))) ** GOTO lbl-1000
                var36_34 = var1_1.getClassOrVar("item");
                if (var36_34 != ScriptValue.NULL) {
                    var37_35 = new ArrayList<ScriptValue>();
                    var37_35.add(var3_3);
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "matches", (ScriptValue)var36_34, var37_35, (ScriptContext)var1_1);
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
                var38_36 = new ArrayList<ScriptValue>();
                var38_36.add(var35_33);
                var39_37 = ScriptFormula.callBuiltin((String)"item_count", var38_36, (ScriptContext)var1_1);
                var0.val("full", var39_37);
                if (var1_1.getBool("exact") != false && var39_37.asNum() < var1_1.getNum("cap") != false) continue;
                if ((var1_1.getBool("smart") ^ true) != false && var39_37.asNum() > var1_1.getNum("PLAIN_TRANSFER_CAP") != false) {
                    var40_38 = var1_1.getClassOrVar("entity");
                    if (var40_38 != ScriptValue.NULL) {
                        var41_39 = new ArrayList<ScriptValue>();
                        var42_40 = new ArrayList<ScriptValue>();
                        var43_41 = var1_1.getClassOrVar("item");
                        var42_40.add((ScriptValue)(var43_41 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var43_41, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var42_40.add(ScriptValue.of((double)(var39_37.asNum() - var1_1.getNum("PLAIN_TRANSFER_CAP"))));
                        var41_39.add(ScriptFormula.callBuiltin((String)"MinecraftItem", var42_40, (ScriptContext)var1_1));
                        v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)var40_38, var41_39, (ScriptContext)var1_1);
                    } else {
                        v8 /* !! */  = ScriptValue.NULL;
                    }
                    var44_42 = var1_1.getClassOrVar("item");
                    if (var44_42 != ScriptValue.NULL) {
                        var45_43 = new ArrayList<ScriptValue>();
                        var45_43.add(var1_1.getClassOrVar("PLAIN_TRANSFER_CAP"));
                        v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var44_42, var45_43, (ScriptContext)var1_1);
                    } else {
                        v9 /* !! */  = ScriptValue.NULL;
                    }
                    return v9 /* !! */ ;
                }
                if (var1_1.getBool("exact") != false && var39_37.asNum() > var1_1.getNum("cap") != false) {
                    var46_44 = var1_1.getClassOrVar("entity");
                    if (var46_44 != ScriptValue.NULL) {
                        var47_45 = new ArrayList<ScriptValue>();
                        var48_46 = new ArrayList<ScriptValue>();
                        var49_47 = var1_1.getClassOrVar("item");
                        var48_46.add((ScriptValue)(var49_47 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var49_47, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var48_46.add(ScriptValue.of((double)(var39_37.asNum() - var1_1.getNum("cap"))));
                        var47_45.add(ScriptFormula.callBuiltin((String)"MinecraftItem", var48_46, (ScriptContext)var1_1));
                        v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)var46_44, var47_45, (ScriptContext)var1_1);
                    } else {
                        v10 /* !! */  = ScriptValue.NULL;
                    }
                    var50_48 = var1_1.getClassOrVar("item");
                    if (var50_48 != ScriptValue.NULL) {
                        var51_49 = new ArrayList<ScriptValue>();
                        var51_49.add(var1_1.getClassOrVar("cap"));
                        v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var50_48, var51_49, (ScriptContext)var1_1);
                    } else {
                        v11 /* !! */  = ScriptValue.NULL;
                    }
                    return v11 /* !! */ ;
                }
                var52_50 = var1_1.getClassOrVar("entity");
                if (var52_50 != ScriptValue.NULL) {
                    var53_51 = new ArrayList<E>();
                    v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var52_50, var53_51, (ScriptContext)var1_1);
                } else {
                    v12 /* !! */  = ScriptValue.NULL;
                }
                return var35_33;
            }
        }
        if ((var54_52 = var1_1.getClassOrVar("Item")) != ScriptValue.NULL) {
            var55_53 = new ArrayList<ScriptValue>();
            var55_53.add(ScriptValue.of((String)"minecraft:air"));
            v13 /* !! */  = var54_52 instanceof ScriptValue.Obj && (var57_55 = (var56_54 = (ScriptValue.Obj)var54_52).instance()) != null && !(var57_55 instanceof PolyClass) && var56_54.typeName().equals("Item") ? new PolyClassItem(var57_55).um$21_create(var55_53) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var54_52, var55_53, (ScriptContext)var1_1);
        } else {
            v13 /* !! */  = ScriptValue.NULL;
        }
        return v13 /* !! */ ;
    }

    public static ScriptValue _chuteSyncPowered(ScriptContext.Builder builder) {
        block5: {
            PolyClassMachine polyClassMachine;
            PolyClassMachine polyClassMachine2;
            ScriptContext scriptContext = builder.peek();
            if (scriptContext.getBool("smart") ^ true) {
                return ScriptValue.NULL;
            }
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"powered"));
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine2.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
            builder.val("cur", (ScriptValue)callSite);
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue3 = PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptContext)scriptContext).asBool() ? ScriptValue.of((String)"true") : ScriptValue.of((String)"false");
            builder.val("want", scriptValue3);
            if (!(ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptValue3) ^ true)) break block5;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "powered";
                ScriptValue scriptValue5 = scriptValue3;
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine3 = new PolyClassMachine(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine3.tm$16_set_property(string, scriptValue5.asStr()));
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
        v0 /* !! */  = var3_3 != ScriptValue.NULL ? ((var4_4 = PolyClassMachine.ofGuarded((ScriptValue)var3_3)) != null ? var4_4.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)var3_3, (ScriptContext)var1_1)) : ScriptValue.NULL;
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
        var5_5 = var1_1.getClassOrVar("Machine");
        if (var5_5 != ScriptValue.NULL) {
            var6_6 = "_chute_item";
            var7_7 = "item";
            if (var5_5 instanceof ScriptValue.Obj && (var9_9 = (var8_8 = (ScriptValue.Obj)var5_5).instance()) != null && !(var9_9 instanceof PolyClass) && var8_8.typeName().equals("Machine")) {
                var10_10 = new PolyClassMachine(var9_9);
                v2 /* !! */  = var10_10.tm$34_get_typed(var6_6, var7_7);
            } else {
                var11_11 = new ArrayList<ScriptValue>();
                var11_11.add(ScriptValue.of((String)var6_6));
                var11_11.add(ScriptValue.of((String)var7_7));
                v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var5_5, var11_11, (ScriptContext)var1_1);
            }
        } else {
            v2 /* !! */  = ScriptValue.NULL;
        }
        var12_12 = v2 /* !! */ ;
        var0.val("holding", var12_12);
        var13_13 = new ArrayList<ScriptValue>();
        var13_13.add(var12_12);
        if (ScriptFormula.callBuiltin((String)"is_empty", var13_13, (ScriptContext)var1_1).asBool()) {
            var14_14 = ScriptContext.builder().copyFrom(var1_1);
            var14_14.val("smart", var1_1.getClassOrVar("smart"));
            var15_15 = ChuteUtils._chutePull(var14_14);
            var0.val("taken", var15_15);
            var16_16 = new ArrayList<ScriptValue>();
            var16_16.add(var15_15);
            if (ScriptFormula.callBuiltin((String)"is_empty", var16_16, (ScriptContext)var1_1).asBool() ^ true) {
                var17_17 = var1_1.getClassOrVar("Machine");
                if (var17_17 != ScriptValue.NULL) {
                    var18_18 = "_chute_item";
                    var19_19 = "item";
                    var20_20 = var15_15;
                    if (var17_17 instanceof ScriptValue.Obj && (var22_22 = (var21_21 = (ScriptValue.Obj)var17_17).instance()) != null && !(var22_22 instanceof PolyClass) && var21_21.typeName().equals("Machine")) {
                        var23_23 = new PolyClassMachine(var22_22);
                        v3 /* !! */  = ScriptValue.of((boolean)var23_23.tm$82_set_typed(var18_18, var19_19, var20_20));
                    } else {
                        var24_24 = new ArrayList<ScriptValue>();
                        var24_24.add(ScriptValue.of((String)var18_18));
                        var24_24.add(ScriptValue.of((String)var19_19));
                        var24_24.add(var20_20);
                        v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var17_17, var24_24, (ScriptContext)var1_1);
                    }
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
                var25_25 = var1_1.getClassOrVar("Machine");
                if (var25_25 != ScriptValue.NULL) {
                    var26_26 = "_chute_progress";
                    var27_27 = "int";
                    var28_28 = ScriptValue.of((double)0.0);
                    if (var25_25 instanceof ScriptValue.Obj && (var30_30 = (var29_29 = (ScriptValue.Obj)var25_25).instance()) != null && !(var30_30 instanceof PolyClass) && var29_29.typeName().equals("Machine")) {
                        var31_31 = new PolyClassMachine(var30_30);
                        v4 /* !! */  = ScriptValue.of((boolean)var31_31.tm$82_set_typed(var26_26, var27_27, var28_28));
                    } else {
                        var32_32 = new ArrayList<ScriptValue>();
                        var32_32.add(ScriptValue.of((String)var26_26));
                        var32_32.add(ScriptValue.of((String)var27_27));
                        var32_32.add(var28_28);
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var25_25, var32_32, (ScriptContext)var1_1);
                    }
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        var33_33 = var1_1.getClassOrVar("Machine");
        if (var33_33 != ScriptValue.NULL) {
            var34_34 = "_chute_progress";
            var35_35 = "int";
            if (var33_33 instanceof ScriptValue.Obj && (var37_37 = (var36_36 = (ScriptValue.Obj)var33_33).instance()) != null && !(var37_37 instanceof PolyClass) && var36_36.typeName().equals("Machine")) {
                var38_38 = new PolyClassMachine(var37_37);
                v5 /* !! */  = var38_38.tm$34_get_typed(var34_34, var35_35);
            } else {
                var39_39 = new ArrayList<ScriptValue>();
                var39_39.add(ScriptValue.of((String)var34_34));
                var39_39.add(ScriptValue.of((String)var35_35));
                v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var33_33, var39_39, (ScriptContext)var1_1);
            }
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        var40_40 = v5 /* !! */ ;
        var0.val("progress", var40_40);
        if (ScriptFormula.valuesEqual((ScriptValue)var40_40, (ScriptValue)var1_1.getClassOrVar("null"))) {
            var41_41 = 0.0;
            var43_42 = ScriptValue.of((double)0.0);
            var0.val("progress", var43_42);
        }
        var44_43 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)1.0));
        var0.val("progress", var44_43);
        if (var44_43.asNum() < var1_1.getNum("CHUTE_STEP")) {
            var45_44 = var1_1.getClassOrVar("Machine");
            if (var45_44 != ScriptValue.NULL) {
                var46_45 = "_chute_progress";
                var47_46 = "int";
                var48_47 = var44_43;
                if (var45_44 instanceof ScriptValue.Obj && (var50_49 = (var49_48 = (ScriptValue.Obj)var45_44).instance()) != null && !(var50_49 instanceof PolyClass) && var49_48.typeName().equals("Machine")) {
                    var51_50 = new PolyClassMachine(var50_49);
                    v6 /* !! */  = ScriptValue.of((boolean)var51_50.tm$82_set_typed(var46_45, var47_46, var48_47));
                } else {
                    var52_51 = new ArrayList<ScriptValue>();
                    var52_51.add(ScriptValue.of((String)var46_45));
                    var52_51.add(ScriptValue.of((String)var47_46));
                    var52_51.add(var48_47);
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var45_44, var52_51, (ScriptContext)var1_1);
                }
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var53_52 = ScriptContext.builder().copyFrom(var1_1);
        var54_53 = ChuteUtils._chuteOutChute(var53_52);
        var0.val("below_info", var54_53);
        if (ScriptFormula.valuesEqual((ScriptValue)var54_53, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
            var55_54 = ScriptFormula.subscriptGet((ScriptValue)var54_53, (ScriptValue)ScriptValue.of((double)0.0));
            var0.val("below", var55_54);
            var56_55 = ScriptFormula.subscriptGet((ScriptValue)var54_53, (ScriptValue)ScriptValue.of((double)1.0));
            var0.val("below_is_smart", var56_55);
            var57_56 = var1_1.getClassOrVar("below");
            if (var57_56 != ScriptValue.NULL) {
                var58_57 = new ArrayList<ScriptValue>();
                var58_57.add(ScriptValue.of((String)"_chute_item"));
                var58_57.add(ScriptValue.of((String)"item"));
                v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var57_56, var58_57, (ScriptContext)var1_1);
            } else {
                v7 /* !! */  = ScriptValue.NULL;
            }
            var59_58 = v7 /* !! */ ;
            var0.val("below_holding", var59_58);
            var60_59 = new ArrayList<ScriptValue>();
            var60_59.add(var59_58);
            if (ScriptFormula.callBuiltin((String)"is_empty", var60_59, (ScriptContext)var1_1).asBool()) {
                var61_60 = new ArrayList<ScriptValue>();
                var61_60.add(var12_12);
                var62_61 = ScriptFormula.callBuiltin((String)"item_count", var61_60, (ScriptContext)var1_1);
                var0.val("full", var62_61);
                if ((var56_55.asBool() ^ true) != false && var62_61.asNum() > var1_1.getNum("PLAIN_TRANSFER_CAP") != false) {
                    var63_62 = var1_1.getClassOrVar("below");
                    if (var63_62 != ScriptValue.NULL) {
                        var64_63 = new ArrayList<ScriptValue>();
                        var64_63.add(ScriptValue.of((String)"_chute_item"));
                        var64_63.add(ScriptValue.of((String)"item"));
                        var65_64 = var1_1.getClassOrVar("holding");
                        if (var65_64 != ScriptValue.NULL) {
                            var66_65 = new ArrayList<ScriptValue>();
                            var66_65.add(var1_1.getClassOrVar("PLAIN_TRANSFER_CAP"));
                            v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var65_64, var66_65, (ScriptContext)var1_1);
                        } else {
                            v8 /* !! */  = ScriptValue.NULL;
                        }
                        var64_63.add(v8 /* !! */ );
                        v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var63_62, var64_63, (ScriptContext)var1_1);
                    } else {
                        v9 /* !! */  = ScriptValue.NULL;
                    }
                    var67_66 = var1_1.getClassOrVar("Machine");
                    if (var67_66 != ScriptValue.NULL) {
                        var68_67 = "_chute_item";
                        var69_68 = "item";
                        var71_69 = var1_1.getClassOrVar("holding");
                        if (var71_69 != ScriptValue.NULL) {
                            var72_70 = new ArrayList<ScriptValue>();
                            var72_70.add(ScriptValue.of((double)(var62_61.asNum() - var1_1.getNum("PLAIN_TRANSFER_CAP"))));
                            v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var71_69, var72_70, (ScriptContext)var1_1);
                        } else {
                            v10 /* !! */  = var70_71 = ScriptValue.NULL;
                        }
                        if (var67_66 instanceof ScriptValue.Obj && (var74_73 = (var73_72 = (ScriptValue.Obj)var67_66).instance()) != null && !(var74_73 instanceof PolyClass) && var73_72.typeName().equals("Machine")) {
                            var75_74 = new PolyClassMachine(var74_73);
                            v11 /* !! */  = ScriptValue.of((boolean)var75_74.tm$82_set_typed(var68_67, var69_68, var70_71));
                        } else {
                            var76_75 = new ArrayList<ScriptValue>();
                            var76_75.add(ScriptValue.of((String)var68_67));
                            var76_75.add(ScriptValue.of((String)var69_68));
                            var76_75.add(var70_71);
                            v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var67_66, var76_75, (ScriptContext)var1_1);
                        }
                    } else {
                        v11 /* !! */  = ScriptValue.NULL;
                    }
                } else {
                    var77_76 = var1_1.getClassOrVar("below");
                    if (var77_76 != ScriptValue.NULL) {
                        var78_77 = new ArrayList<ScriptValue>();
                        var78_77.add(ScriptValue.of((String)"_chute_item"));
                        var78_77.add(ScriptValue.of((String)"item"));
                        var78_77.add(var12_12);
                        v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var77_76, var78_77, (ScriptContext)var1_1);
                    } else {
                        v12 /* !! */  = ScriptValue.NULL;
                    }
                    var79_78 = var1_1.getClassOrVar("Machine");
                    if (var79_78 != ScriptValue.NULL) {
                        var80_79 = "_chute_item";
                        var81_80 = "item";
                        var82_81 = var1_1.getClassOrVar("null");
                        if (var79_78 instanceof ScriptValue.Obj && (var84_83 = (var83_82 = (ScriptValue.Obj)var79_78).instance()) != null && !(var84_83 instanceof PolyClass) && var83_82.typeName().equals("Machine")) {
                            var85_84 = new PolyClassMachine(var84_83);
                            v13 /* !! */  = ScriptValue.of((boolean)var85_84.tm$82_set_typed(var80_79, var81_80, var82_81));
                        } else {
                            var86_85 = new ArrayList<ScriptValue>();
                            var86_85.add(ScriptValue.of((String)var80_79));
                            var86_85.add(ScriptValue.of((String)var81_80));
                            var86_85.add(var82_81);
                            v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var79_78, var86_85, (ScriptContext)var1_1);
                        }
                    } else {
                        v13 /* !! */  = ScriptValue.NULL;
                    }
                }
                var87_86 = var1_1.getClassOrVar("below");
                if (var87_86 != ScriptValue.NULL) {
                    var88_87 = new ArrayList<ScriptValue>();
                    var88_87.add(ScriptValue.of((String)"_chute_progress"));
                    var88_87.add(ScriptValue.of((String)"int"));
                    var88_87.add(ScriptValue.of((double)0.0));
                    v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var87_86, var88_87, (ScriptContext)var1_1);
                } else {
                    v14 /* !! */  = ScriptValue.NULL;
                }
                var89_88 = var1_1.getClassOrVar("Machine");
                if (var89_88 != ScriptValue.NULL) {
                    var90_89 = "_chute_progress";
                    var91_90 = "int";
                    var92_91 = ScriptValue.of((double)0.0);
                    if (var89_88 instanceof ScriptValue.Obj && (var94_93 = (var93_92 = (ScriptValue.Obj)var89_88).instance()) != null && !(var94_93 instanceof PolyClass) && var93_92.typeName().equals("Machine")) {
                        var95_94 = new PolyClassMachine(var94_93);
                        v15 /* !! */  = ScriptValue.of((boolean)var95_94.tm$82_set_typed(var90_89, var91_90, var92_91));
                    } else {
                        var96_95 = new ArrayList<ScriptValue>();
                        var96_95.add(ScriptValue.of((String)var90_89));
                        var96_95.add(ScriptValue.of((String)var91_90));
                        var96_95.add(var92_91);
                        v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var89_88, var96_95, (ScriptContext)var1_1);
                    }
                } else {
                    v15 /* !! */  = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        var97_96 = ScriptContext.builder().copyFrom(var1_1);
        var97_96.val("item", var12_12);
        var98_97 = ChuteUtils._chuteGiveOut(var97_96);
        var0.val("leftover", var98_97);
        var99_98 = new ArrayList<ScriptValue>();
        var99_98.add(var98_97);
        if (ScriptFormula.callBuiltin((String)"is_empty", var99_98, (ScriptContext)var1_1).asBool() ^ true) {
            var100_99 = ScriptContext.builder().copyFrom(var1_1);
            var101_100 = ChuteUtils._chuteOutOffset(var100_99);
            var0.val("off", var101_100);
            var102_101 = var1_1.getClassOrVar("Machine");
            if (var102_101 != ScriptValue.NULL) {
                var103_102 = var98_97;
                var104_103 = ScriptFormula.subscriptGet((ScriptValue)var101_100, (ScriptValue)ScriptValue.of((double)0.0));
                var105_104 = ScriptFormula.subscriptGet((ScriptValue)var101_100, (ScriptValue)ScriptValue.of((double)1.0));
                var106_105 = ScriptFormula.subscriptGet((ScriptValue)var101_100, (ScriptValue)ScriptValue.of((double)2.0));
                if (var102_101 instanceof ScriptValue.Obj && (var108_107 = (var107_106 = (ScriptValue.Obj)var102_101).instance()) != null && !(var108_107 instanceof PolyClass) && var107_106.typeName().equals("Machine")) {
                    var109_108 = new PolyClassMachine(var108_107);
                    v16 /* !! */  = ScriptValue.of((boolean)var109_108.tm$50_drop_item_at(var103_102, var104_103.asNum(), var105_104.asNum(), var106_105.asNum()));
                } else {
                    var110_109 = new ArrayList<ScriptValue>();
                    var110_109.add(var103_102);
                    var110_109.add(var104_103);
                    var110_109.add(var105_104);
                    var110_109.add(var106_105);
                    v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)var102_101, var110_109, (ScriptContext)var1_1);
                }
            } else {
                v16 /* !! */  = ScriptValue.NULL;
            }
        }
        if ((var111_110 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
            var112_111 = "_chute_item";
            var113_112 = "item";
            var114_113 = var1_1.getClassOrVar("null");
            if (var111_110 instanceof ScriptValue.Obj && (var116_115 = (var115_114 = (ScriptValue.Obj)var111_110).instance()) != null && !(var116_115 instanceof PolyClass) && var115_114.typeName().equals("Machine")) {
                var117_116 = new PolyClassMachine(var116_115);
                v17 /* !! */  = ScriptValue.of((boolean)var117_116.tm$82_set_typed(var112_111, var113_112, var114_113));
            } else {
                var118_117 = new ArrayList<ScriptValue>();
                var118_117.add(ScriptValue.of((String)var112_111));
                var118_117.add(ScriptValue.of((String)var113_112));
                var118_117.add(var114_113);
                v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var111_110, var118_117, (ScriptContext)var1_1);
            }
        } else {
            v17 /* !! */  = ScriptValue.NULL;
        }
        var119_118 = var1_1.getClassOrVar("Machine");
        if (var119_118 != ScriptValue.NULL) {
            var120_119 = "_chute_progress";
            var121_120 = "int";
            var122_121 = ScriptValue.of((double)0.0);
            if (var119_118 instanceof ScriptValue.Obj && (var124_123 = (var123_122 = (ScriptValue.Obj)var119_118).instance()) != null && !(var124_123 instanceof PolyClass) && var123_122.typeName().equals("Machine")) {
                var125_124 = new PolyClassMachine(var124_123);
                v18 /* !! */  = ScriptValue.of((boolean)var125_124.tm$82_set_typed(var120_119, var121_120, var122_121));
            } else {
                var126_125 = new ArrayList<ScriptValue>();
                var126_125.add(ScriptValue.of((String)var120_119));
                var126_125.add(ScriptValue.of((String)var121_120));
                var126_125.add(var122_121);
                v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var119_118, var126_125, (ScriptContext)var1_1);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue5));
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                v3 = ScriptValue.of((boolean)polyClassMachine.tm$16_set_property(string, scriptValue7.asStr()));
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
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"facing"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("cur", (ScriptValue)callSite);
        if (ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue2 = ScriptValue.of((String)"down");
            builder.val("cur", scriptValue2);
        }
        double d = 0.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue3);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("FACING_CYCLE"));
        if (list != null) {
            for (ScriptValue scriptValue4 : list) {
                builder.val("f", scriptValue4);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("f"), (ScriptValue)scriptContext.getClassOrVar("cur"))) break;
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("idx", scriptValue5);
            }
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("FACING_CYCLE");
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("FACING_CYCLE"));
        double d2 = ScriptFormula.callBuiltin((String)"size", arrayList2, (ScriptContext)scriptContext).asNum();
        ScriptValue scriptValue7 = ScriptFormula.subscriptGet((ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)(d2 == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)1.0)).asNum() % d2)));
        builder.val("next", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "facing";
            ScriptValue scriptValue9 = scriptValue7;
            if (scriptValue8 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object);
                v1 = ScriptValue.of((boolean)polyClassMachine2.tm$16_set_property(string, scriptValue9.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((String)string));
                arrayList3.add(scriptValue9);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue8, arrayList3, (ScriptContext)scriptContext);
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                    object = polyClassMachine.tm$34_get_typed(string, string2);
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
                v1 = scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine(object3).um$4_drop_item(arrayList2) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
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
        FILE_SCOPE = builder.build();
    }
}
