/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.conveyor;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class FunnelUtils {
    public static ScriptValue _funnelModeOut(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"mode"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("m", (ScriptValue)callSite);
        return ScriptValue.of((ScriptFormula.valuesEqual((ScriptValue)callSite, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)callSite, (String)"in") ^ true ? 1 : 0) != 0);
    }

    public static ScriptValue _funnelFacingVec(ScriptContext.Builder builder) {
        Object object;
        ScriptValue.Obj obj;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"facing"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object2).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("f", (ScriptValue)callSite);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("DIR_VEC");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
            arrayList2.add(callSite);
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add(ScriptValue.of((double)0.0));
            arrayList3.add(ScriptValue.of((double)0.0));
            arrayList3.add(ScriptValue.of((double)(-1.0)));
            arrayList2.add((CallSite)new ScriptValue.Array(arrayList3));
            object = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue2, arrayList2, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue _funnelInc(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = 0.0;
        var4_3 = ScriptValue.of((double)0.0);
        var0.val("best", var4_3);
        var5_4 = var1_1.getClassOrVar("Machine");
        if (var5_4 != ScriptValue.NULL) {
            var6_5 = var1_1.getClassOrVar("fx");
            var7_6 = var1_1.getClassOrVar("fy");
            var8_7 = var1_1.getClassOrVar("fz");
            if (var5_4 instanceof ScriptValue.Obj && (var10_9 = (var9_8 = (ScriptValue.Obj)var5_4).instance()) != null && !(var10_9 instanceof PolyClass) && var9_8.typeName().equals("Machine")) {
                var11_10 = new PolyClassMachine_v4(var10_9);
                v0 /* !! */  = var11_10.tm$62_belt_at(var6_5.asNum(), var7_6.asNum(), var8_7.asNum());
            } else {
                var12_11 = new ArrayList<ScriptValue>();
                var12_11.add(var6_5);
                var12_11.add(var7_6);
                var12_11.add(var8_7);
                v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)var5_4, var12_11, (ScriptContext)var1_1);
            }
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var13_12 = v0 /* !! */ ;
        var0.val("bf", var13_12);
        if (ScriptFormula.valuesEqual((ScriptValue)var13_12, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
            var14_13 = var1_1.getClassOrVar("bf");
            var15_14 = var14_13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)var14_13, (ScriptContext)var1_1) : ScriptValue.NULL;
            var0.val("best", var15_14);
        }
        if ((var16_15 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
            var17_16 = -var1_1.getNum("fx");
            var19_17 = -var1_1.getNum("fy");
            var21_18 = -var1_1.getNum("fz");
            if (var16_15 instanceof ScriptValue.Obj && (var24_20 = (var23_19 = (ScriptValue.Obj)var16_15).instance()) != null && !(var24_20 instanceof PolyClass) && var23_19.typeName().equals("Machine")) {
                var25_21 = new PolyClassMachine_v4(var24_20);
                v1 /* !! */  = var25_21.tm$62_belt_at(var17_16, var19_17, var21_18);
            } else {
                var26_22 = new ArrayList<ScriptValue>();
                var26_22.add(ScriptValue.of((double)var17_16));
                var26_22.add(ScriptValue.of((double)var19_17));
                var26_22.add(ScriptValue.of((double)var21_18));
                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)var16_15, var26_22, (ScriptContext)var1_1);
            }
        } else {
            v1 /* !! */  = ScriptValue.NULL;
        }
        var27_23 = v1 /* !! */ ;
        var0.val("bo", var27_23);
        if (!(ScriptFormula.valuesEqual((ScriptValue)var27_23, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) ** GOTO lbl-1000
        var28_24 = var1_1.getClassOrVar("bo");
        v2 /* !! */  = var28_24 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)var28_24, (ScriptContext)var1_1) : ScriptValue.NULL;
        if (v2 /* !! */ .asNum() > var1_1.getNum("best")) {
            v3 = true;
        } else lbl-1000:
        // 2 sources

        {
            v3 = false;
        }
        if (v3) {
            var29_25 = var1_1.getClassOrVar("bo");
            var30_26 = var29_25 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)var29_25, (ScriptContext)var1_1) : ScriptValue.NULL;
            var0.val("best", var30_26);
        }
        return var1_1.getNum("best") > 0.0 != false ? var1_1.getClassOrVar("best") : var1_1.getClassOrVar("DEFAULT_INC");
    }

    public static ScriptValue _funnelSetActivated(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptValue scriptValue2 = ScriptFormula.subscriptGet((ScriptValue)scriptContext.getClassOrVar("behind_vec"), (ScriptValue)ScriptValue.of((double)0.0));
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptContext.getClassOrVar("behind_vec"), (ScriptValue)ScriptValue.of((double)1.0));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptContext.getClassOrVar("behind_vec"), (ScriptValue)ScriptValue.of((double)2.0));
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$17_container_at(scriptValue2.asNum(), scriptValue3.asNum(), scriptValue4.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                arrayList.add(scriptValue3);
                arrayList.add(scriptValue4);
                object = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("c", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue7;
            String string = "activated";
            ScriptValue scriptValue8 = scriptValue7 = ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true ? ScriptValue.of((String)"true") : ScriptValue.of((String)"false");
            if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$16_set_property(string, scriptValue7.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(scriptValue7);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _funnelGiveForward(ScriptContext.Builder builder) {
        double d;
        Object object;
        ScriptValue scriptValue;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("fx");
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("fy");
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("fz");
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
        builder.val("belt", scriptValue6);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && ((scriptValue = scriptContext.getClassOrVar("belt")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "exists", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            Object object4;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("belt");
            if ((scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_full", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                return scriptContext.getClassOrVar("item");
            }
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("belt");
            if (scriptValue8 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("item"));
                object4 = PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            return object4;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("fx");
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("fy");
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("fz");
            if (scriptValue9 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object5);
                object = polyClassMachine_v4.tm$17_container_at(scriptValue10.asNum(), scriptValue11.asNum(), scriptValue12.asNum());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue10);
                arrayList.add(scriptValue11);
                arrayList.add(scriptValue12);
                object = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = object;
        builder.val("target_c", scriptValue13);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue13, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            Object object6;
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("target_c");
            if (scriptValue14 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("item"));
                object6 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
            } else {
                object6 = ScriptValue.NULL;
            }
            return object6;
        }
        double d2 = scriptContext.getNum("DEFAULT_INC");
        if (d2 == 0.0) {
            d = 0.0;
        } else {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("fx", scriptContext.getClassOrVar("fx"));
            builder2.val("fy", scriptContext.getClassOrVar("fy"));
            builder2.val("fz", scriptContext.getClassOrVar("fz"));
            d = FunnelUtils._funnelInc(builder2).asNum() / d2;
        }
        double d3 = d * 0.1;
        ScriptValue scriptValue15 = ScriptValue.of((double)d3);
        builder.val("force", scriptValue15);
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
        if (scriptValue16 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("item"));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fx") * d3)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fy") * d3)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fz") * d3)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fx") * 0.5)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fy") * 0.5)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fz") * 0.5)));
            v5 = scriptValue16 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object7).um$19_drop_item_toward(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        return scriptContext.getClassOrVar("null");
    }

    public static ScriptValue _funnelTick(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = FunnelUtils._funnelFacingVec(builder2);
        builder.val("v", scriptValue);
        ScriptValue scriptValue2 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)0.0));
        builder.val("fx", scriptValue2);
        ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)1.0));
        builder.val("fy", scriptValue3);
        ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((double)2.0));
        builder.val("fz", scriptValue4);
        double d = -scriptValue2.asNum();
        ScriptValue scriptValue5 = ScriptValue.of((double)d);
        builder.val("bx", scriptValue5);
        double d2 = -scriptValue3.asNum();
        ScriptValue scriptValue6 = ScriptValue.of((double)d2);
        builder.val("by", scriptValue6);
        double d3 = -scriptValue4.asNum();
        ScriptValue scriptValue7 = ScriptValue.of((double)d3);
        builder.val("bz", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
        Object object3 = scriptValue8 != ScriptValue.NULL ? (scriptValue8 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v4(object2).pg$148_redstone() : PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)object3, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj3;
                Object object4;
                String string = "activated";
                String string2 = "false";
                if (scriptValue9 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$16_set_property(string, string2));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string2));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue10 = FunnelUtils._funnelModeOut(builder3);
        builder.val("out", scriptValue10);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)0.0));
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue11 != ScriptValue.NULL ? (scriptValue11 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue11, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("held", (ScriptValue)callSite);
        if (scriptValue10.asBool()) {
            ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
            arrayList2.add(callSite);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) {
                Object object5;
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                if (scriptValue12 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    double d4 = d;
                    double d5 = d2;
                    double d6 = d3;
                    if (scriptValue12 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object6);
                        object5 = polyClassMachine_v4.tm$17_container_at(d4, d5, d6);
                    } else {
                        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                        arrayList3.add(ScriptValue.of((double)d4));
                        arrayList3.add(ScriptValue.of((double)d5));
                        arrayList3.add(ScriptValue.of((double)d6));
                        object5 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue12, arrayList3, (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue13 = object5;
                builder.val("c", scriptValue13);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue13, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    Object object7;
                    ScriptValue scriptValue14 = scriptContext.getClassOrVar("c");
                    if (scriptValue14 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                        arrayList4.add(ScriptValue.of((double)1.0));
                        object7 = PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue14, arrayList4, (ScriptContext)scriptContext);
                    } else {
                        object7 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue15 = object7;
                    builder.val("taken", scriptValue15);
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue15);
                    if (ScriptFormula.callBuiltin((String)"is_empty", arrayList5, (ScriptContext)scriptContext).asBool() ^ true) {
                        ScriptValue.Obj obj5;
                        Object object8;
                        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                        arrayList6.add(ScriptValue.of((double)0.0));
                        arrayList6.add(scriptValue15);
                        ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
                        PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue16 != ScriptValue.NULL ? (scriptValue16 instanceof ScriptValue.Obj && (object8 = (obj5 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object8 instanceof PolyClass) && obj5.typeName().equals("Machine") ? new PolyClassMachine_v4(object8).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue16, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList6, (ScriptContext)scriptContext);
                        ScriptValue scriptValue17 = scriptValue15;
                        builder.val("held", scriptValue17);
                    }
                }
            }
            ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
            arrayList7.add(scriptContext.getClassOrVar("held"));
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList7, (ScriptContext)scriptContext).asBool() ^ true) {
                Object object9;
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
                if (scriptValue18 != ScriptValue.NULL) {
                    ScriptValue.Obj obj6;
                    Object object10;
                    String string = "_funnel_progress";
                    String string3 = "float";
                    if (scriptValue18 instanceof ScriptValue.Obj && (object10 = (obj6 = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object10 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object10);
                        object9 = polyClassMachine_v4.tm$34_get_typed(string, string3);
                    } else {
                        ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                        arrayList8.add(ScriptValue.of((String)string));
                        arrayList8.add(ScriptValue.of((String)string3));
                        object9 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue18, arrayList8, (ScriptContext)scriptContext);
                    }
                } else {
                    object9 = ScriptValue.NULL;
                }
                ScriptValue scriptValue19 = object9;
                builder.val("progress", scriptValue19);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue19, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                    double d7 = 0.0;
                    ScriptValue scriptValue20 = ScriptValue.of((double)0.0);
                    builder.val("progress", scriptValue20);
                }
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("progress");
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("fx", scriptValue2);
                builder4.val("fy", scriptValue3);
                builder4.val("fz", scriptValue4);
                ScriptValue scriptValue22 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue21, (ScriptValue)FunnelUtils._funnelInc(builder4));
                builder.val("progress", scriptValue22);
                if (scriptValue22.asNum() < 1.0) {
                    ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue23 != ScriptValue.NULL) {
                        ScriptValue.Obj obj7;
                        Object object11;
                        String string = "_funnel_progress";
                        String string4 = "float";
                        ScriptValue scriptValue24 = scriptValue22;
                        if (scriptValue23 instanceof ScriptValue.Obj && (object11 = (obj7 = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object11 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                            PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object11);
                            v6 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string4, scriptValue24));
                        } else {
                            ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                            arrayList9.add(ScriptValue.of((String)string));
                            arrayList9.add(ScriptValue.of((String)string4));
                            arrayList9.add(scriptValue24);
                            v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue23, arrayList9, (ScriptContext)scriptContext);
                        }
                    } else {
                        v6 = ScriptValue.NULL;
                    }
                } else {
                    ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                    builder5.val("item", scriptContext.getClassOrVar("held"));
                    builder5.val("fx", scriptValue2);
                    builder5.val("fy", scriptValue3);
                    builder5.val("fz", scriptValue4);
                    ScriptValue scriptValue25 = FunnelUtils._funnelGiveForward(builder5);
                    builder.val("leftover", scriptValue25);
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add(scriptValue25);
                    if (ScriptFormula.callBuiltin((String)"is_empty", arrayList10, (ScriptContext)scriptContext).asBool()) {
                        ScriptValue.Obj obj8;
                        Object object12;
                        ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                        arrayList11.add(ScriptValue.of((double)0.0));
                        arrayList11.add(scriptContext.getClassOrVar("null"));
                        ScriptValue scriptValue26 = scriptContext.getClassOrVar("Machine");
                        PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue26 != ScriptValue.NULL ? (scriptValue26 instanceof ScriptValue.Obj && (object12 = (obj8 = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object12 instanceof PolyClass) && obj8.typeName().equals("Machine") ? new PolyClassMachine_v4(object12).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue26, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList11, (ScriptContext)scriptContext);
                        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue27 != ScriptValue.NULL) {
                            ScriptValue.Obj obj9;
                            Object object13;
                            String string = "_funnel_progress";
                            String string5 = "float";
                            ScriptValue scriptValue28 = ScriptValue.of((double)0.0);
                            if (scriptValue27 instanceof ScriptValue.Obj && (object13 = (obj9 = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object13 instanceof PolyClass) && obj9.typeName().equals("Machine")) {
                                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object13);
                                v7 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string5, scriptValue28));
                            } else {
                                ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                                arrayList12.add(ScriptValue.of((String)string));
                                arrayList12.add(ScriptValue.of((String)string5));
                                arrayList12.add(scriptValue28);
                                v7 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue27, arrayList12, (ScriptContext)scriptContext);
                            }
                        } else {
                            v7 = ScriptValue.NULL;
                        }
                    }
                }
            }
        } else {
            ArrayList<CallSite> arrayList13 = new ArrayList<CallSite>();
            arrayList13.add(callSite);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList13, (ScriptContext)scriptContext).asBool() ^ true) {
                Object object14;
                ScriptValue scriptValue29 = scriptContext.getClassOrVar("Machine");
                if (scriptValue29 != ScriptValue.NULL) {
                    ScriptValue.Obj obj10;
                    Object object15;
                    String string = "_funnel_progress";
                    String string6 = "float";
                    if (scriptValue29 instanceof ScriptValue.Obj && (object15 = (obj10 = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object15 instanceof PolyClass) && obj10.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object15);
                        object14 = polyClassMachine_v4.tm$34_get_typed(string, string6);
                    } else {
                        ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                        arrayList14.add(ScriptValue.of((String)string));
                        arrayList14.add(ScriptValue.of((String)string6));
                        object14 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue29, arrayList14, (ScriptContext)scriptContext);
                    }
                } else {
                    object14 = ScriptValue.NULL;
                }
                ScriptValue scriptValue30 = object14;
                builder.val("progress", scriptValue30);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue30, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                    double d8 = 0.0;
                    ScriptValue scriptValue31 = ScriptValue.of((double)0.0);
                    builder.val("progress", scriptValue31);
                }
                ScriptValue scriptValue32 = scriptContext.getClassOrVar("progress");
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("fx", scriptValue2);
                builder6.val("fy", scriptValue3);
                builder6.val("fz", scriptValue4);
                ScriptValue scriptValue33 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue32, (ScriptValue)FunnelUtils._funnelInc(builder6));
                builder.val("progress", scriptValue33);
                if (scriptValue33.asNum() < 1.0) {
                    ScriptValue scriptValue34 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue34 != ScriptValue.NULL) {
                        ScriptValue.Obj obj11;
                        Object object16;
                        String string = "_funnel_progress";
                        String string7 = "float";
                        ScriptValue scriptValue35 = scriptValue33;
                        if (scriptValue34 instanceof ScriptValue.Obj && (object16 = (obj11 = (ScriptValue.Obj)scriptValue34).instance()) != null && !(object16 instanceof PolyClass) && obj11.typeName().equals("Machine")) {
                            PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object16);
                            v10 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string7, scriptValue35));
                        } else {
                            ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                            arrayList15.add(ScriptValue.of((String)string));
                            arrayList15.add(ScriptValue.of((String)string7));
                            arrayList15.add(scriptValue35);
                            v10 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue34, arrayList15, (ScriptContext)scriptContext);
                        }
                    } else {
                        v10 = ScriptValue.NULL;
                    }
                } else {
                    Object object17;
                    ScriptValue scriptValue36 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue36 != ScriptValue.NULL) {
                        ScriptValue.Obj obj12;
                        Object object18;
                        double d9 = d;
                        double d10 = d2;
                        double d11 = d3;
                        if (scriptValue36 instanceof ScriptValue.Obj && (object18 = (obj12 = (ScriptValue.Obj)scriptValue36).instance()) != null && !(object18 instanceof PolyClass) && obj12.typeName().equals("Machine")) {
                            PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object18);
                            object17 = polyClassMachine_v4.tm$17_container_at(d9, d10, d11);
                        } else {
                            ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                            arrayList16.add(ScriptValue.of((double)d9));
                            arrayList16.add(ScriptValue.of((double)d10));
                            arrayList16.add(ScriptValue.of((double)d11));
                            object17 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue36, arrayList16, (ScriptContext)scriptContext);
                        }
                    } else {
                        object17 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue37 = object17;
                    builder.val("c", scriptValue37);
                    if (ScriptFormula.valuesEqual((ScriptValue)scriptValue37, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                        Object object19;
                        ScriptValue scriptValue38 = scriptContext.getClassOrVar("c");
                        if (scriptValue38 != ScriptValue.NULL) {
                            ArrayList<CallSite> arrayList17 = new ArrayList<CallSite>();
                            arrayList17.add(callSite);
                            object19 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue38, arrayList17, (ScriptContext)scriptContext);
                        } else {
                            object19 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue39 = object19;
                        builder.val("leftover", scriptValue39);
                        ArrayList<ScriptValue> arrayList18 = new ArrayList<ScriptValue>();
                        arrayList18.add(scriptValue39);
                        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList18, (ScriptContext)scriptContext).asBool()) {
                            ScriptValue.Obj obj13;
                            Object object20;
                            ArrayList<ScriptValue> arrayList19 = new ArrayList<ScriptValue>();
                            arrayList19.add(ScriptValue.of((double)0.0));
                            arrayList19.add(scriptContext.getClassOrVar("null"));
                            ScriptValue scriptValue40 = scriptContext.getClassOrVar("Machine");
                            PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue40 != ScriptValue.NULL ? (scriptValue40 instanceof ScriptValue.Obj && (object20 = (obj13 = (ScriptValue.Obj)scriptValue40).instance()) != null && !(object20 instanceof PolyClass) && obj13.typeName().equals("Machine") ? new PolyClassMachine_v4(object20).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue40, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList19, (ScriptContext)scriptContext);
                            ScriptValue scriptValue41 = scriptContext.getClassOrVar("Machine");
                            if (scriptValue41 != ScriptValue.NULL) {
                                ScriptValue.Obj obj14;
                                Object object21;
                                String string = "_funnel_progress";
                                String string8 = "float";
                                ScriptValue scriptValue42 = ScriptValue.of((double)0.0);
                                if (scriptValue41 instanceof ScriptValue.Obj && (object21 = (obj14 = (ScriptValue.Obj)scriptValue41).instance()) != null && !(object21 instanceof PolyClass) && obj14.typeName().equals("Machine")) {
                                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object21);
                                    v13 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string8, scriptValue42));
                                } else {
                                    ArrayList<ScriptValue> arrayList20 = new ArrayList<ScriptValue>();
                                    arrayList20.add(ScriptValue.of((String)string));
                                    arrayList20.add(ScriptValue.of((String)string8));
                                    arrayList20.add(scriptValue42);
                                    v13 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue41, arrayList20, (ScriptContext)scriptContext);
                                }
                            } else {
                                v13 = ScriptValue.NULL;
                            }
                        }
                    }
                }
            }
        }
        ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
        ArrayList<ScriptValue> arrayList21 = new ArrayList<ScriptValue>();
        arrayList21.add(ScriptValue.of((double)d));
        arrayList21.add(ScriptValue.of((double)d2));
        arrayList21.add(ScriptValue.of((double)d3));
        builder7.val("behind_vec", (ScriptValue)new ScriptValue.Array(arrayList21));
        FunnelUtils._funnelSetActivated(builder7);
        return ScriptValue.NULL;
    }

    public static ScriptValue _funnelToggleMode(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"mode"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("cur", (ScriptValue)callSite);
        ScriptValue scriptValue2 = ScriptFormula.valuesEqualStr((ScriptValue)callSite, (String)"in") ? ScriptValue.of((String)"out") : ScriptValue.of((String)"in");
        builder.val("next", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object2;
            String string = "mode";
            ScriptValue scriptValue4 = scriptValue2;
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$16_set_property(string, scriptValue4.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(scriptValue4);
                v0 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object3;
            String string = "_funnel_progress";
            String string2 = "float";
            ScriptValue scriptValue6 = ScriptValue.of((double)0.0);
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object3);
                v1 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string2, scriptValue6));
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((String)string));
                arrayList3.add(ScriptValue.of((String)string2));
                arrayList3.add(scriptValue6);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj4;
            Object object4;
            ScriptValue scriptValue8 = ScriptValue.of((String)"<yellow>Funnel mode: <white>");
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptValue2);
            ScriptValue scriptValue9 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)ScriptFormula.callBuiltin((String)"upper", arrayList4, (ScriptContext)scriptContext));
            if (scriptValue7 instanceof ScriptValue.Obj && (object4 = (obj4 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object4 instanceof PolyClass) && obj4.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue9.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(scriptValue9);
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _funnelDropHeld(ScriptContext.Builder builder) {
        block2: {
            ScriptValue.Obj obj;
            Object object;
            ScriptContext scriptContext = builder.peek();
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
            builder.val("held", (ScriptValue)callSite);
            ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
            arrayList2.add(callSite);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool() ^ true)) break block2;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object2;
                ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
                arrayList3.add(callSite);
                v0 = scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v4(object2).um$4_drop_item(arrayList3) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue2, arrayList3, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<Object> arrayList = new ArrayList<Object>();
        arrayList.add(ScriptValue.of((String)"north"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(ScriptValue.of((double)0.0));
        arrayList2.add(ScriptValue.of((double)0.0));
        arrayList2.add(ScriptValue.of((double)(-1.0)));
        arrayList.add(new ScriptValue.Array(arrayList2));
        arrayList.add(ScriptValue.of((String)"south"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(ScriptValue.of((double)0.0));
        arrayList3.add(ScriptValue.of((double)0.0));
        arrayList3.add(ScriptValue.of((double)1.0));
        arrayList.add(new ScriptValue.Array(arrayList3));
        arrayList.add(ScriptValue.of((String)"east"));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add(ScriptValue.of((double)1.0));
        arrayList4.add(ScriptValue.of((double)0.0));
        arrayList4.add(ScriptValue.of((double)0.0));
        arrayList.add(new ScriptValue.Array(arrayList4));
        arrayList.add(ScriptValue.of((String)"west"));
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add(ScriptValue.of((double)(-1.0)));
        arrayList5.add(ScriptValue.of((double)0.0));
        arrayList5.add(ScriptValue.of((double)0.0));
        arrayList.add(new ScriptValue.Array(arrayList5));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
        builder.val("DIR_VEC", scriptValue);
        double d = 0.0625;
        ScriptValue scriptValue2 = ScriptValue.of((double)0.0625);
        builder.val("DEFAULT_INC", scriptValue2);
    }
}
