/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.conveyor;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class FloorFunnelUtils {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue _floorFunnelVacuum(ScriptContext.Builder var0) {
        block9: {
            var1_1 = var0.peek();
            var5_2 = var1_1.getClassOrVar("Machine");
            if (var5_2 != ScriptValue.NULL) {
                var6_3 = 0.7;
                if (var5_2 instanceof ScriptValue.Obj && (var9_5 = (var8_4 = (ScriptValue.Obj)var5_2).instance()) != null && !(var9_5 instanceof PolyClass) && var8_4.typeName().equals("Machine")) {
                    var10_6 = new PolyClassMachine_v3(var9_5);
                    v0 /* !! */  = var10_6.tm$94_nearby_entities(var6_3);
                } else {
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var5_2, (ScriptValue)ScriptValue.of((double)var6_3), (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var2_7 = ScriptProgram.elementsOf((ScriptValue)v0 /* !! */ );
            if (var2_7 == null) break block9;
            for (ScriptValue var4_9 : var2_7) {
                var0.val("entity", var4_9);
                var11_10 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                var11_10.val("entity", var1_1.getClassOrVar("entity"));
                if (!Utils.isRestingItem(var11_10).asBool()) ** GOTO lbl-1000
                var12_11 = var1_1.getClassOrVar("entity");
                v1 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var12_11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var12_11, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1).asNum();
                var13_12 = var1_1.getClassOrVar("Machine");
                v2 = var13_12 != ScriptValue.NULL ? ((var14_13 = PolyClassMachine_v3.ofGuarded((ScriptValue)var13_12)) != null ? var14_13.tg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var13_12, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (v1 >= v2) {
                    v3 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v3 = false;
                }
                if (!v3) continue;
                var15_14 = var1_1.getClassOrVar("entity");
                var16_15 = var15_14 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var15_14, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("item", var16_15);
                var17_16 = var1_1.getClassOrVar("entity");
                v4 /* !! */  = var17_16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var17_16, (ScriptContext)var1_1) : ScriptValue.NULL;
                return var16_15;
            }
        }
        if ((var18_17 = var1_1.getClassOrVar("Item")) != ScriptValue.NULL) {
            var19_18 = new ArrayList<ScriptValue>();
            var19_18.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FloorFunnelUtils.class, "minecraft:air"));
            v5 /* !! */  = var18_17 instanceof ScriptValue.Obj && (var21_20 = (var20_19 = (ScriptValue.Obj)var18_17).instance()) != null && !(var21_20 instanceof PolyClass) && var20_19.typeName().equals("Item") ? new PolyClassItem(var21_20).um$21_create(var19_18) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var18_17, var19_18, (ScriptContext)var1_1);
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        return v5 /* !! */ ;
    }

    public static ScriptValue _floorFunnelTick(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v3 polyClassMachine_v3;
        Object object2;
        CallSite callSite;
        ScriptValue.Obj obj2;
        Object object3;
        PolyClassMachine_v3 polyClassMachine_v32;
        ScriptValue scriptValue;
        CallSite callSite2;
        ScriptValue.Obj obj3;
        Object object4;
        PolyClassMachine_v3 polyClassMachine_v33;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v33.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 0.0;
        if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
            callSite2 = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite2 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite3 = callSite2;
        builder.val("held", (ScriptValue)callSite3);
        ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
        arrayList.add(callSite3);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue4 = FloorFunnelUtils._floorFunnelVacuum(builder2);
            builder.val("held", scriptValue4);
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptValue4);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool() && scriptContext.getBool("pull_above")) {
                Object object5;
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
                if (scriptValue5 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    double d2 = 0.0;
                    double d3 = 1.0;
                    double d4 = 0.0;
                    if (scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        PolyClassMachine_v3 polyClassMachine_v34 = new PolyClassMachine_v3(object6);
                        object5 = polyClassMachine_v34.tm$17_container_at(d2, d3, d4);
                    } else {
                        object5 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue6 = object5;
                builder.val("above", scriptValue6);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue7 = scriptContext.getClassOrVar("above");
                    ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FloorFunnelUtils.class, 64.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("held", scriptValue8);
                }
            }
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add(scriptContext.getClassOrVar("held"));
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptValue.Obj obj5;
                Object object7;
                PolyClassMachine_v3 polyClassMachine_v35;
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? ((polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassMachine_v35.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d5 = 0.0;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("held");
                if (scriptValue10 instanceof ScriptValue.Obj && (object7 = (obj5 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object7 instanceof PolyClass) && obj5.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object7);
                    v2 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d5, scriptValue11));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
                }
            }
        }
        ScriptValue scriptValue12 = (scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v32.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d6 = 0.0;
        if (scriptValue12 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
            callSite = polyClassContainer.tm$0_get_item(d6);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
        }
        CallSite callSite4 = callSite;
        builder.val("held", (ScriptValue)callSite4);
        ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
        arrayList4.add(callSite4);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList4, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        if (scriptContext.getBool("pull_above")) {
            ScriptValue.Obj obj6;
            Object object8;
            PolyClassMachine_v3 polyClassMachine_v36;
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj7;
                Object object9;
                CallSite callSite5 = callSite4;
                double d7 = 0.0;
                double d8 = -1.0;
                double d9 = 0.0;
                if (scriptValue13 instanceof ScriptValue.Obj && (object9 = (obj7 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object9 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v37 = new PolyClassMachine_v3(object9);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v37.tm$50_drop_item_at((ScriptValue)callSite5, d7, d8, d9));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue13, (ScriptValue)callSite5, (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)ScriptValue.of((double)d8), (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue15 = scriptValue14 != ScriptValue.NULL ? ((polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine_v36.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d10 = 0.0;
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("null");
            if (scriptValue15 instanceof ScriptValue.Obj && (object8 = (obj6 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object8 instanceof PolyClass) && obj6.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object8);
                v5 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d10, scriptValue16));
            } else {
                v5 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)scriptValue16, (ScriptContext)scriptContext);
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
        if (scriptValue17 != ScriptValue.NULL) {
            ScriptValue.Obj obj8;
            Object object10;
            double d11 = 0.0;
            double d12 = -1.0;
            double d13 = 0.0;
            if (scriptValue17 instanceof ScriptValue.Obj && (object10 = (obj8 = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object10 instanceof PolyClass) && obj8.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v38 = new PolyClassMachine_v3(object10);
                object2 = polyClassMachine_v38.tm$17_container_at(d11, d12, d13);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((double)d11), (ScriptValue)ScriptValue.of((double)d12), (ScriptValue)ScriptValue.of((double)d13), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = object2;
        builder.val("below", scriptValue18);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue18, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("below");
            ScriptValue scriptValue20 = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue19, (ScriptValue)callSite4, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("leftover", scriptValue20);
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptValue20);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList5, (ScriptContext)scriptContext).asBool()) {
                ScriptValue.Obj obj9;
                Object object11;
                PolyClassMachine_v3 polyClassMachine_v39;
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue22 = scriptValue21 != ScriptValue.NULL ? ((polyClassMachine_v39 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue21)) != null ? polyClassMachine_v39.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d14 = 0.0;
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("null");
                if (scriptValue22 instanceof ScriptValue.Obj && (object11 = (obj9 = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object11 instanceof PolyClass) && obj9.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object11);
                    v7 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d14, scriptValue23));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((double)d14), (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
                }
            } else {
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(scriptValue20);
                double d15 = ScriptFormula.callBuiltin((String)"item_count", arrayList6, (ScriptContext)scriptContext).asNum();
                ArrayList<CallSite> arrayList7 = new ArrayList<CallSite>();
                arrayList7.add(callSite4);
                if (d15 < ScriptFormula.callBuiltin((String)"item_count", arrayList7, (ScriptContext)scriptContext).asNum()) {
                    ScriptValue.Obj obj10;
                    Object object12;
                    PolyClassMachine_v3 polyClassMachine_v310;
                    ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue25 = scriptValue24 != ScriptValue.NULL ? ((polyClassMachine_v310 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue24)) != null ? polyClassMachine_v310.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue24, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    double d16 = 0.0;
                    ScriptValue scriptValue26 = scriptValue20;
                    if (scriptValue25 instanceof ScriptValue.Obj && (object12 = (obj10 = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object12 instanceof PolyClass) && obj10.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object12);
                        v9 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d16, scriptValue26));
                    } else {
                        v9 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue25, (ScriptValue)ScriptValue.of((double)d16), (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
                    }
                }
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
        if (scriptValue27 != ScriptValue.NULL) {
            ScriptValue.Obj obj11;
            Object object13;
            CallSite callSite6 = callSite4;
            double d17 = 0.0;
            double d18 = -1.0;
            double d19 = 0.0;
            if (scriptValue27 instanceof ScriptValue.Obj && (object13 = (obj11 = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object13 instanceof PolyClass) && obj11.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v311 = new PolyClassMachine_v3(object13);
                v10 = ScriptValue.of((boolean)polyClassMachine_v311.tm$50_drop_item_at((ScriptValue)callSite6, d17, d18, d19));
            } else {
                v10 = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue27, (ScriptValue)callSite6, (ScriptValue)ScriptValue.of((double)d17), (ScriptValue)ScriptValue.of((double)d18), (ScriptValue)ScriptValue.of((double)d19), (ScriptContext)scriptContext);
            }
        } else {
            v10 = ScriptValue.NULL;
        }
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue29 = scriptValue28 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue28)) != null ? polyClassMachine_v3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue28, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d20 = 0.0;
        ScriptValue scriptValue30 = scriptContext.getClassOrVar("null");
        if (scriptValue29 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            v11 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d20, scriptValue30));
        } else {
            v11 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue29, (ScriptValue)ScriptValue.of((double)d20), (ScriptValue)scriptValue30, (ScriptContext)scriptContext);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelDropHeld(ScriptContext.Builder builder) {
        block6: {
            ScriptValue.Obj obj;
            Object object;
            PolyClassMachine_v3 polyClassMachine_v3;
            CallSite callSite;
            ScriptValue.Obj obj2;
            Object object2;
            PolyClassMachine_v3 polyClassMachine_v32;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v32.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d = 0.0;
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                callSite = polyClassContainer.tm$0_get_item(d);
            } else {
                callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
            CallSite callSite2 = callSite;
            builder.val("held", (ScriptValue)callSite2);
            ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
            arrayList.add(callSite2);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true)) break block6;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj3;
                Object object3;
                ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
                arrayList2.add(callSite2);
                v1 = scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v3(object3).um$4_drop_item(arrayList2) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d2 = 0.0;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("null");
            if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object);
                v2 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d2, scriptValue6));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelOnRightClick(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v3 polyClassMachine_v3;
        CallSite callSite;
        ScriptValue.Obj obj2;
        Object object2;
        PolyClassMachine_v3 polyClassMachine_v32;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        arrayList.add((ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer.pg$48_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v32.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 0.0;
        if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("held", (ScriptValue)callSite2);
        ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
        arrayList2.add(callSite2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object3;
            CallSite callSite3 = callSite2;
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object3);
                v1 = ScriptValue.of((boolean)polyClassPlayer2.tm$14_give_item((ScriptValue)callSite3));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)scriptValue4, (ScriptValue)callSite3, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassMachine_v3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d2 = 0.0;
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("null");
        if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            v2 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d2, scriptValue7));
        } else {
            v2 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("is_resting_item");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"utils.pf", null, arrayList);
        FILE_SCOPE = builder.build();
    }
}
