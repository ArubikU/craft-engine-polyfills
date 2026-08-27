/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassBlock;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassRedstone;
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
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object2;
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        String string = "facing";
        if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock polyClassBlock = new PolyClassBlock(object2);
            callSite = polyClassBlock.tm$24_property(string);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)string));
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("facing", (ScriptValue)callSite2);
        if (ScriptFormula.valuesEqual((ScriptValue)callSite2, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)callSite2, (String)"down")) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)(-1.0)));
            arrayList.add(ScriptValue.of((double)0.0));
            return new ScriptValue.Array(arrayList);
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("DIR_VEC");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
            arrayList.add(callSite2);
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(ScriptValue.of((double)0.0));
            arrayList2.add(ScriptValue.of((double)0.0));
            arrayList2.add(ScriptValue.of((double)0.0));
            arrayList.add((CallSite)new ScriptValue.Array(arrayList2));
            object = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("v", scriptValue4);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)0.0)));
        arrayList.add(ScriptValue.of((double)(-1.0)));
        arrayList.add(ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)2.0)));
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
                v4 = var32_30 != ScriptValue.NULL ? ((var33_31 = PolyClassMachine_v4.ofGuarded((ScriptValue)var32_30)) != null ? var33_31.tg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var32_30, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
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
        block7: {
            PolyClassMachine_v4 polyClassMachine_v4;
            CallSite callSite;
            ScriptValue.Obj obj;
            Object object;
            PolyClassMachine_v4 polyClassMachine_v42;
            ScriptContext scriptContext = builder.peek();
            if (scriptContext.getBool("smart") ^ true) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v42.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            String string = "powered";
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
                PolyClassBlock polyClassBlock = new PolyClassBlock(object);
                callSite = polyClassBlock.tm$24_property(string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
            CallSite callSite2 = callSite;
            builder.val("cur", (ScriptValue)callSite2);
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v4.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue4);
            ScriptValue scriptValue5 = (polyClassRedstone != null ? polyClassRedstone.tg$19_powered() : PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool()) ? ScriptValue.of((String)"true") : ScriptValue.of((String)"false");
            builder.val("want", scriptValue5);
            if (!(ScriptFormula.valuesEqual((ScriptValue)callSite2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ScriptFormula.valuesEqual((ScriptValue)callSite2, (ScriptValue)scriptValue5) ^ true)) break block7;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object2;
                String string2 = "powered";
                ScriptValue scriptValue7 = scriptValue5;
                if (scriptValue6 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v43 = new PolyClassMachine_v4(object2);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v43.tm$16_set_property(string2, scriptValue7.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string2));
                    arrayList.add(scriptValue7);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
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
        var3_5 = var4_3 != ScriptValue.NULL ? ((var5_4 = PolyClassMachine_v4.ofGuarded((ScriptValue)var4_3)) != null ? var5_4.pg$170_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)var4_3, (ScriptContext)var1_1)) : ScriptValue.NULL;
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
                var12_12 = new PolyClassMachine_v4(var11_11);
                v1 /* !! */  = var12_12.tm$34_get_typed(var8_8, var9_9);
            } else {
                var13_13 = new ArrayList<ScriptValue>();
                var13_13.add(ScriptValue.of((String)var8_8));
                var13_13.add(ScriptValue.of((String)var9_9));
                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var7_7, var13_13, (ScriptContext)var1_1);
            }
        } else {
            v1 /* !! */  = ScriptValue.NULL;
        }
        var14_14 = v1 /* !! */ ;
        var0.val("holding", var14_14);
        var15_15 = new ArrayList<ScriptValue>();
        var15_15.add(var14_14);
        if (ScriptFormula.callBuiltin((String)"is_empty", var15_15, (ScriptContext)var1_1).asBool()) {
            var16_16 = ScriptContext.builder().copyFrom(var1_1);
            var16_16.val("smart", var1_1.getClassOrVar("smart"));
            var17_17 = ChuteUtils._chutePull(var16_16);
            var0.val("taken", var17_17);
            var18_18 = new ArrayList<ScriptValue>();
            var18_18.add(var17_17);
            if (ScriptFormula.callBuiltin((String)"is_empty", var18_18, (ScriptContext)var1_1).asBool() ^ true) {
                var19_19 = var1_1.getClassOrVar("Machine");
                if (var19_19 != ScriptValue.NULL) {
                    var20_20 = "_chute_item";
                    var21_21 = "item";
                    var22_22 = var17_17;
                    if (var19_19 instanceof ScriptValue.Obj && (var24_24 = (var23_23 = (ScriptValue.Obj)var19_19).instance()) != null && !(var24_24 instanceof PolyClass) && var23_23.typeName().equals("Machine")) {
                        var25_25 = new PolyClassMachine_v4(var24_24);
                        v2 /* !! */  = ScriptValue.of((boolean)var25_25.tm$82_set_typed(var20_20, var21_21, var22_22));
                    } else {
                        var26_26 = new ArrayList<ScriptValue>();
                        var26_26.add(ScriptValue.of((String)var20_20));
                        var26_26.add(ScriptValue.of((String)var21_21));
                        var26_26.add(var22_22);
                        v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var19_19, var26_26, (ScriptContext)var1_1);
                    }
                } else {
                    v2 /* !! */  = ScriptValue.NULL;
                }
                var27_27 = var1_1.getClassOrVar("Machine");
                if (var27_27 != ScriptValue.NULL) {
                    var28_28 = "_chute_progress";
                    var29_29 = "int";
                    var30_30 = ScriptValue.of((double)0.0);
                    if (var27_27 instanceof ScriptValue.Obj && (var32_32 = (var31_31 = (ScriptValue.Obj)var27_27).instance()) != null && !(var32_32 instanceof PolyClass) && var31_31.typeName().equals("Machine")) {
                        var33_33 = new PolyClassMachine_v4(var32_32);
                        v3 /* !! */  = ScriptValue.of((boolean)var33_33.tm$82_set_typed(var28_28, var29_29, var30_30));
                    } else {
                        var34_34 = new ArrayList<ScriptValue>();
                        var34_34.add(ScriptValue.of((String)var28_28));
                        var34_34.add(ScriptValue.of((String)var29_29));
                        var34_34.add(var30_30);
                        v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var27_27, var34_34, (ScriptContext)var1_1);
                    }
                } else {
                    v3 /* !! */  = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        var35_35 = var1_1.getClassOrVar("Machine");
        if (var35_35 != ScriptValue.NULL) {
            var36_36 = "_chute_progress";
            var37_37 = "int";
            if (var35_35 instanceof ScriptValue.Obj && (var39_39 = (var38_38 = (ScriptValue.Obj)var35_35).instance()) != null && !(var39_39 instanceof PolyClass) && var38_38.typeName().equals("Machine")) {
                var40_40 = new PolyClassMachine_v4(var39_39);
                v4 /* !! */  = var40_40.tm$34_get_typed(var36_36, var37_37);
            } else {
                var41_41 = new ArrayList<ScriptValue>();
                var41_41.add(ScriptValue.of((String)var36_36));
                var41_41.add(ScriptValue.of((String)var37_37));
                v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var35_35, var41_41, (ScriptContext)var1_1);
            }
        } else {
            v4 /* !! */  = ScriptValue.NULL;
        }
        var42_42 = v4 /* !! */ ;
        var0.val("progress", var42_42);
        if (ScriptFormula.valuesEqual((ScriptValue)var42_42, (ScriptValue)var1_1.getClassOrVar("null"))) {
            var43_43 = 0.0;
            var45_44 = ScriptValue.of((double)0.0);
            var0.val("progress", var45_44);
        }
        var46_45 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)1.0));
        var0.val("progress", var46_45);
        if (var46_45.asNum() < var1_1.getNum("CHUTE_STEP")) {
            var47_46 = var1_1.getClassOrVar("Machine");
            if (var47_46 != ScriptValue.NULL) {
                var48_47 = "_chute_progress";
                var49_48 = "int";
                var50_49 = var46_45;
                if (var47_46 instanceof ScriptValue.Obj && (var52_51 = (var51_50 = (ScriptValue.Obj)var47_46).instance()) != null && !(var52_51 instanceof PolyClass) && var51_50.typeName().equals("Machine")) {
                    var53_52 = new PolyClassMachine_v4(var52_51);
                    v5 /* !! */  = ScriptValue.of((boolean)var53_52.tm$82_set_typed(var48_47, var49_48, var50_49));
                } else {
                    var54_53 = new ArrayList<ScriptValue>();
                    var54_53.add(ScriptValue.of((String)var48_47));
                    var54_53.add(ScriptValue.of((String)var49_48));
                    var54_53.add(var50_49);
                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var47_46, var54_53, (ScriptContext)var1_1);
                }
            } else {
                v5 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var55_54 = ScriptContext.builder().copyFrom(var1_1);
        var56_55 = ChuteUtils._chuteOutChute(var55_54);
        var0.val("below_info", var56_55);
        if (ScriptFormula.valuesEqual((ScriptValue)var56_55, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
            var57_56 = ScriptFormula.subscriptGet((ScriptValue)var56_55, (ScriptValue)ScriptValue.of((double)0.0));
            var0.val("below", var57_56);
            var58_57 = ScriptFormula.subscriptGet((ScriptValue)var56_55, (ScriptValue)ScriptValue.of((double)1.0));
            var0.val("below_is_smart", var58_57);
            var59_58 = var1_1.getClassOrVar("below");
            if (var59_58 != ScriptValue.NULL) {
                var60_59 = new ArrayList<ScriptValue>();
                var60_59.add(ScriptValue.of((String)"_chute_item"));
                var60_59.add(ScriptValue.of((String)"item"));
                v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var59_58, var60_59, (ScriptContext)var1_1);
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            var61_60 = v6 /* !! */ ;
            var0.val("below_holding", var61_60);
            var62_61 = new ArrayList<ScriptValue>();
            var62_61.add(var61_60);
            if (ScriptFormula.callBuiltin((String)"is_empty", var62_61, (ScriptContext)var1_1).asBool()) {
                var63_62 = new ArrayList<ScriptValue>();
                var63_62.add(var14_14);
                var64_63 = ScriptFormula.callBuiltin((String)"item_count", var63_62, (ScriptContext)var1_1);
                var0.val("full", var64_63);
                if ((var58_57.asBool() ^ true) != false && var64_63.asNum() > var1_1.getNum("PLAIN_TRANSFER_CAP") != false) {
                    var65_64 = var1_1.getClassOrVar("below");
                    if (var65_64 != ScriptValue.NULL) {
                        var66_65 = new ArrayList<ScriptValue>();
                        var66_65.add(ScriptValue.of((String)"_chute_item"));
                        var66_65.add(ScriptValue.of((String)"item"));
                        var67_66 = var1_1.getClassOrVar("holding");
                        if (var67_66 != ScriptValue.NULL) {
                            var68_67 = new ArrayList<ScriptValue>();
                            var68_67.add(var1_1.getClassOrVar("PLAIN_TRANSFER_CAP"));
                            v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var67_66, var68_67, (ScriptContext)var1_1);
                        } else {
                            v7 /* !! */  = ScriptValue.NULL;
                        }
                        var66_65.add(v7 /* !! */ );
                        v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var65_64, var66_65, (ScriptContext)var1_1);
                    } else {
                        v8 /* !! */  = ScriptValue.NULL;
                    }
                    var69_68 = var1_1.getClassOrVar("Machine");
                    if (var69_68 != ScriptValue.NULL) {
                        var70_69 = "_chute_item";
                        var71_70 = "item";
                        var73_71 = var1_1.getClassOrVar("holding");
                        if (var73_71 != ScriptValue.NULL) {
                            var74_72 = new ArrayList<ScriptValue>();
                            var74_72.add(ScriptValue.of((double)(var64_63.asNum() - var1_1.getNum("PLAIN_TRANSFER_CAP"))));
                            v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "with_count", (ScriptValue)var73_71, var74_72, (ScriptContext)var1_1);
                        } else {
                            v9 /* !! */  = var72_73 = ScriptValue.NULL;
                        }
                        if (var69_68 instanceof ScriptValue.Obj && (var76_75 = (var75_74 = (ScriptValue.Obj)var69_68).instance()) != null && !(var76_75 instanceof PolyClass) && var75_74.typeName().equals("Machine")) {
                            var77_76 = new PolyClassMachine_v4(var76_75);
                            v10 /* !! */  = ScriptValue.of((boolean)var77_76.tm$82_set_typed(var70_69, var71_70, var72_73));
                        } else {
                            var78_77 = new ArrayList<ScriptValue>();
                            var78_77.add(ScriptValue.of((String)var70_69));
                            var78_77.add(ScriptValue.of((String)var71_70));
                            var78_77.add(var72_73);
                            v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var69_68, var78_77, (ScriptContext)var1_1);
                        }
                    } else {
                        v10 /* !! */  = ScriptValue.NULL;
                    }
                } else {
                    var79_78 = var1_1.getClassOrVar("below");
                    if (var79_78 != ScriptValue.NULL) {
                        var80_79 = new ArrayList<ScriptValue>();
                        var80_79.add(ScriptValue.of((String)"_chute_item"));
                        var80_79.add(ScriptValue.of((String)"item"));
                        var80_79.add(var14_14);
                        v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var79_78, var80_79, (ScriptContext)var1_1);
                    } else {
                        v11 /* !! */  = ScriptValue.NULL;
                    }
                    var81_80 = var1_1.getClassOrVar("Machine");
                    if (var81_80 != ScriptValue.NULL) {
                        var82_81 = "_chute_item";
                        var83_82 = "item";
                        var84_83 = var1_1.getClassOrVar("null");
                        if (var81_80 instanceof ScriptValue.Obj && (var86_85 = (var85_84 = (ScriptValue.Obj)var81_80).instance()) != null && !(var86_85 instanceof PolyClass) && var85_84.typeName().equals("Machine")) {
                            var87_86 = new PolyClassMachine_v4(var86_85);
                            v12 /* !! */  = ScriptValue.of((boolean)var87_86.tm$82_set_typed(var82_81, var83_82, var84_83));
                        } else {
                            var88_87 = new ArrayList<ScriptValue>();
                            var88_87.add(ScriptValue.of((String)var82_81));
                            var88_87.add(ScriptValue.of((String)var83_82));
                            var88_87.add(var84_83);
                            v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var81_80, var88_87, (ScriptContext)var1_1);
                        }
                    } else {
                        v12 /* !! */  = ScriptValue.NULL;
                    }
                }
                var89_88 = var1_1.getClassOrVar("below");
                if (var89_88 != ScriptValue.NULL) {
                    var90_89 = new ArrayList<ScriptValue>();
                    var90_89.add(ScriptValue.of((String)"_chute_progress"));
                    var90_89.add(ScriptValue.of((String)"int"));
                    var90_89.add(ScriptValue.of((double)0.0));
                    v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var89_88, var90_89, (ScriptContext)var1_1);
                } else {
                    v13 /* !! */  = ScriptValue.NULL;
                }
                var91_90 = var1_1.getClassOrVar("Machine");
                if (var91_90 != ScriptValue.NULL) {
                    var92_91 = "_chute_progress";
                    var93_92 = "int";
                    var94_93 = ScriptValue.of((double)0.0);
                    if (var91_90 instanceof ScriptValue.Obj && (var96_95 = (var95_94 = (ScriptValue.Obj)var91_90).instance()) != null && !(var96_95 instanceof PolyClass) && var95_94.typeName().equals("Machine")) {
                        var97_96 = new PolyClassMachine_v4(var96_95);
                        v14 /* !! */  = ScriptValue.of((boolean)var97_96.tm$82_set_typed(var92_91, var93_92, var94_93));
                    } else {
                        var98_97 = new ArrayList<ScriptValue>();
                        var98_97.add(ScriptValue.of((String)var92_91));
                        var98_97.add(ScriptValue.of((String)var93_92));
                        var98_97.add(var94_93);
                        v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var91_90, var98_97, (ScriptContext)var1_1);
                    }
                } else {
                    v14 /* !! */  = ScriptValue.NULL;
                }
            }
            return ScriptValue.NULL;
        }
        var99_98 = ScriptContext.builder().copyFrom(var1_1);
        var99_98.val("item", var14_14);
        var100_99 = ChuteUtils._chuteGiveOut(var99_98);
        var0.val("leftover", var100_99);
        var101_100 = new ArrayList<ScriptValue>();
        var101_100.add(var100_99);
        if (ScriptFormula.callBuiltin((String)"is_empty", var101_100, (ScriptContext)var1_1).asBool() ^ true) {
            var102_101 = ScriptContext.builder().copyFrom(var1_1);
            var103_102 = ChuteUtils._chuteOutOffset(var102_101);
            var0.val("off", var103_102);
            var104_103 = var1_1.getClassOrVar("Machine");
            if (var104_103 != ScriptValue.NULL) {
                var105_104 = var100_99;
                var106_105 = ScriptFormula.subscriptGet((ScriptValue)var103_102, (ScriptValue)ScriptValue.of((double)0.0));
                var107_106 = ScriptFormula.subscriptGet((ScriptValue)var103_102, (ScriptValue)ScriptValue.of((double)1.0));
                var108_107 = ScriptFormula.subscriptGet((ScriptValue)var103_102, (ScriptValue)ScriptValue.of((double)2.0));
                if (var104_103 instanceof ScriptValue.Obj && (var110_109 = (var109_108 = (ScriptValue.Obj)var104_103).instance()) != null && !(var110_109 instanceof PolyClass) && var109_108.typeName().equals("Machine")) {
                    var111_110 = new PolyClassMachine_v4(var110_109);
                    v15 /* !! */  = ScriptValue.of((boolean)var111_110.tm$50_drop_item_at(var105_104, var106_105.asNum(), var107_106.asNum(), var108_107.asNum()));
                } else {
                    var112_111 = new ArrayList<ScriptValue>();
                    var112_111.add(var105_104);
                    var112_111.add(var106_105);
                    var112_111.add(var107_106);
                    var112_111.add(var108_107);
                    v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)var104_103, var112_111, (ScriptContext)var1_1);
                }
            } else {
                v15 /* !! */  = ScriptValue.NULL;
            }
        }
        if ((var113_112 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
            var114_113 = "_chute_item";
            var115_114 = "item";
            var116_115 = var1_1.getClassOrVar("null");
            if (var113_112 instanceof ScriptValue.Obj && (var118_117 = (var117_116 = (ScriptValue.Obj)var113_112).instance()) != null && !(var118_117 instanceof PolyClass) && var117_116.typeName().equals("Machine")) {
                var119_118 = new PolyClassMachine_v4(var118_117);
                v16 /* !! */  = ScriptValue.of((boolean)var119_118.tm$82_set_typed(var114_113, var115_114, var116_115));
            } else {
                var120_119 = new ArrayList<ScriptValue>();
                var120_119.add(ScriptValue.of((String)var114_113));
                var120_119.add(ScriptValue.of((String)var115_114));
                var120_119.add(var116_115);
                v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var113_112, var120_119, (ScriptContext)var1_1);
            }
        } else {
            v16 /* !! */  = ScriptValue.NULL;
        }
        var121_120 = var1_1.getClassOrVar("Machine");
        if (var121_120 != ScriptValue.NULL) {
            var122_121 = "_chute_progress";
            var123_122 = "int";
            var124_123 = ScriptValue.of((double)0.0);
            if (var121_120 instanceof ScriptValue.Obj && (var126_125 = (var125_124 = (ScriptValue.Obj)var121_120).instance()) != null && !(var126_125 instanceof PolyClass) && var125_124.typeName().equals("Machine")) {
                var127_126 = new PolyClassMachine_v4(var126_125);
                v17 /* !! */  = ScriptValue.of((boolean)var127_126.tm$82_set_typed(var122_121, var123_122, var124_123));
            } else {
                var128_127 = new ArrayList<ScriptValue>();
                var128_127.add(ScriptValue.of((String)var122_121));
                var128_127.add(ScriptValue.of((String)var123_122));
                var128_127.add(var124_123);
                v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var121_120, var128_127, (ScriptContext)var1_1);
            }
        } else {
            v17 /* !! */  = ScriptValue.NULL;
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
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        String string = "facing";
        if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock polyClassBlock = new PolyClassBlock(object);
            callSite = polyClassBlock.tm$24_property(string);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)string));
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("cur", (ScriptValue)callSite2);
        if (ScriptFormula.valuesEqual((ScriptValue)callSite2, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue3 = ScriptValue.of((String)"down");
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
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("idx", scriptValue6);
            }
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("FACING_CYCLE");
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("FACING_CYCLE"));
        double d2 = ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum();
        ScriptValue scriptValue8 = ScriptFormula.subscriptGet((ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)(d2 == 0.0 ? 0.0 : ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)1.0)).asNum() % d2)));
        builder.val("next", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object2;
            String string2 = "facing";
            ScriptValue scriptValue10 = scriptValue8;
            if (scriptValue9 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object2);
                v2 = ScriptValue.of((boolean)polyClassMachine_v42.tm$16_set_property(string2, scriptValue10.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string2));
                arrayList2.add(scriptValue10);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue9, arrayList2, (ScriptContext)scriptContext);
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
        FILE_SCOPE = builder.build();
    }
}
