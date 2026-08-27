/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
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
import dev.arubik.craftengine.script.PolyClassMachine_v4;
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
        block11: {
            var1_1 = var0.peek();
            var5_2 = var1_1.getClassOrVar("Machine");
            if (var5_2 != ScriptValue.NULL) {
                var6_3 = 0.7;
                if (var5_2 instanceof ScriptValue.Obj && (var9_5 = (var8_4 = (ScriptValue.Obj)var5_2).instance()) != null && !(var9_5 instanceof PolyClass) && var8_4.typeName().equals("Machine")) {
                    var10_6 = new PolyClassMachine_v4(var9_5);
                    v0 /* !! */  = var10_6.tm$94_nearby_entities(var6_3);
                } else {
                    var11_7 = new ArrayList<ScriptValue>();
                    var11_7.add(ScriptValue.of((double)var6_3));
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var5_2, var11_7, (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var2_8 = ScriptProgram.elementsOf((ScriptValue)v0 /* !! */ );
            if (var2_8 == null) break block11;
            for (ScriptValue var4_10 : var2_8) {
                var0.val("entity", var4_10);
                var12_11 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                var12_11.val("entity", var1_1.getClassOrVar("entity"));
                if (!Utils.isRestingItem(var12_11).asBool()) ** GOTO lbl-1000
                var13_12 = var1_1.getClassOrVar("entity");
                v1 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var13_12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var13_12, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1).asNum();
                var14_13 = var1_1.getClassOrVar("Machine");
                v2 = var14_13 != ScriptValue.NULL ? ((var15_14 = PolyClassMachine_v4.ofGuarded((ScriptValue)var14_13)) != null ? var15_14.tg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var14_13, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (v1 >= v2) {
                    v3 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v3 = false;
                }
                if (!v3) continue;
                var16_15 = var1_1.getClassOrVar("entity");
                var17_16 = var16_15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var16_15, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("item", var17_16);
                var18_17 = var1_1.getClassOrVar("entity");
                if (var18_17 != ScriptValue.NULL) {
                    var19_18 = new ArrayList<E>();
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var18_17, var19_18, (ScriptContext)var1_1);
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
                return var17_16;
            }
        }
        if ((var20_19 = var1_1.getClassOrVar("Item")) != ScriptValue.NULL) {
            var21_20 = new ArrayList<ScriptValue>();
            var21_20.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FloorFunnelUtils.class, "minecraft:air"));
            v5 /* !! */  = var20_19 instanceof ScriptValue.Obj && (var23_22 = (var22_21 = (ScriptValue.Obj)var20_19).instance()) != null && !(var23_22 instanceof PolyClass) && var22_21.typeName().equals("Item") ? new PolyClassItem(var23_22).um$21_create(var21_20) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var20_19, var21_20, (ScriptContext)var1_1);
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        return v5 /* !! */ ;
    }

    public static ScriptValue _floorFunnelTick(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v4 polyClassMachine_v4;
        Object object2;
        CallSite callSite;
        ScriptValue.Obj obj2;
        Object object3;
        PolyClassMachine_v4 polyClassMachine_v42;
        ScriptValue scriptValue;
        CallSite callSite2;
        ScriptValue.Obj obj3;
        Object object4;
        PolyClassMachine_v4 polyClassMachine_v43;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v43 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v43.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 0.0;
        if (scriptValue3 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
            callSite2 = polyClassContainer.tm$0_get_item(d);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)d));
            callSite2 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
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
                        PolyClassMachine_v4 polyClassMachine_v44 = new PolyClassMachine_v4(object6);
                        object5 = polyClassMachine_v44.tm$17_container_at(d2, d3, d4);
                    } else {
                        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                        arrayList3.add(ScriptValue.of((double)d2));
                        arrayList3.add(ScriptValue.of((double)d3));
                        arrayList3.add(ScriptValue.of((double)d4));
                        object5 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue6 = object5;
                builder.val("above", scriptValue6);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    Object object7;
                    ScriptValue scriptValue7 = scriptContext.getClassOrVar("above");
                    if (scriptValue7 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FloorFunnelUtils.class, 64.0));
                        object7 = PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue7, arrayList4, (ScriptContext)scriptContext);
                    } else {
                        object7 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue8 = object7;
                    builder.val("held", scriptValue8);
                }
            }
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptContext.getClassOrVar("held"));
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList5, (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptValue.Obj obj5;
                Object object8;
                PolyClassMachine_v4 polyClassMachine_v45;
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? ((polyClassMachine_v45 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassMachine_v45.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d5 = 0.0;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("held");
                if (scriptValue10 instanceof ScriptValue.Obj && (object8 = (obj5 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object8 instanceof PolyClass) && obj5.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object8);
                    v3 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d5, scriptValue11));
                } else {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(ScriptValue.of((double)d5));
                    arrayList6.add(scriptValue11);
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue10, arrayList6, (ScriptContext)scriptContext);
                }
            }
        }
        ScriptValue scriptValue12 = (scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v42.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d6 = 0.0;
        if (scriptValue12 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
            callSite = polyClassContainer.tm$0_get_item(d6);
        } else {
            ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
            arrayList7.add(ScriptValue.of((double)d6));
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue12, arrayList7, (ScriptContext)scriptContext);
        }
        CallSite callSite4 = callSite;
        builder.val("held", (ScriptValue)callSite4);
        ArrayList<CallSite> arrayList8 = new ArrayList<CallSite>();
        arrayList8.add(callSite4);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList8, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        if (scriptContext.getBool("pull_above")) {
            ScriptValue.Obj obj6;
            Object object9;
            PolyClassMachine_v4 polyClassMachine_v46;
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Machine");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj7;
                Object object10;
                CallSite callSite5 = callSite4;
                double d7 = 0.0;
                double d8 = -1.0;
                double d9 = 0.0;
                if (scriptValue13 instanceof ScriptValue.Obj && (object10 = (obj7 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object10 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v47 = new PolyClassMachine_v4(object10);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v47.tm$50_drop_item_at((ScriptValue)callSite5, d7, d8, d9));
                } else {
                    ArrayList<CallSite> arrayList9 = new ArrayList<CallSite>();
                    arrayList9.add(callSite5);
                    arrayList9.add((CallSite)ScriptValue.of((double)d7));
                    arrayList9.add((CallSite)ScriptValue.of((double)d8));
                    arrayList9.add((CallSite)ScriptValue.of((double)d9));
                    v5 = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue13, arrayList9, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue15 = scriptValue14 != ScriptValue.NULL ? ((polyClassMachine_v46 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine_v46.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d10 = 0.0;
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("null");
            if (scriptValue15 instanceof ScriptValue.Obj && (object9 = (obj6 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object9 instanceof PolyClass) && obj6.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object9);
                v6 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d10, scriptValue16));
            } else {
                ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                arrayList10.add(ScriptValue.of((double)d10));
                arrayList10.add(scriptValue16);
                v6 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue15, arrayList10, (ScriptContext)scriptContext);
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
        if (scriptValue17 != ScriptValue.NULL) {
            ScriptValue.Obj obj8;
            Object object11;
            double d11 = 0.0;
            double d12 = -1.0;
            double d13 = 0.0;
            if (scriptValue17 instanceof ScriptValue.Obj && (object11 = (obj8 = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object11 instanceof PolyClass) && obj8.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v48 = new PolyClassMachine_v4(object11);
                object2 = polyClassMachine_v48.tm$17_container_at(d11, d12, d13);
            } else {
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                arrayList11.add(ScriptValue.of((double)d11));
                arrayList11.add(ScriptValue.of((double)d12));
                arrayList11.add(ScriptValue.of((double)d13));
                object2 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue17, arrayList11, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = object2;
        builder.val("below", scriptValue18);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue18, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            Object object12;
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("below");
            if (scriptValue19 != ScriptValue.NULL) {
                ArrayList<CallSite> arrayList12 = new ArrayList<CallSite>();
                arrayList12.add(callSite4);
                object12 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue19, arrayList12, (ScriptContext)scriptContext);
            } else {
                object12 = ScriptValue.NULL;
            }
            ScriptValue scriptValue20 = object12;
            builder.val("leftover", scriptValue20);
            ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
            arrayList13.add(scriptValue20);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList13, (ScriptContext)scriptContext).asBool()) {
                ScriptValue.Obj obj9;
                Object object13;
                PolyClassMachine_v4 polyClassMachine_v49;
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue22 = scriptValue21 != ScriptValue.NULL ? ((polyClassMachine_v49 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue21)) != null ? polyClassMachine_v49.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d14 = 0.0;
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("null");
                if (scriptValue22 instanceof ScriptValue.Obj && (object13 = (obj9 = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object13 instanceof PolyClass) && obj9.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object13);
                    v9 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d14, scriptValue23));
                } else {
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    arrayList14.add(ScriptValue.of((double)d14));
                    arrayList14.add(scriptValue23);
                    v9 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue22, arrayList14, (ScriptContext)scriptContext);
                }
            } else {
                ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                arrayList15.add(scriptValue20);
                double d15 = ScriptFormula.callBuiltin((String)"item_count", arrayList15, (ScriptContext)scriptContext).asNum();
                ArrayList<CallSite> arrayList16 = new ArrayList<CallSite>();
                arrayList16.add(callSite4);
                if (d15 < ScriptFormula.callBuiltin((String)"item_count", arrayList16, (ScriptContext)scriptContext).asNum()) {
                    ScriptValue.Obj obj10;
                    Object object14;
                    PolyClassMachine_v4 polyClassMachine_v410;
                    ScriptValue scriptValue24 = scriptContext.getClassOrVar("Machine");
                    ScriptValue scriptValue25 = scriptValue24 != ScriptValue.NULL ? ((polyClassMachine_v410 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue24)) != null ? polyClassMachine_v410.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue24, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                    double d16 = 0.0;
                    ScriptValue scriptValue26 = scriptValue20;
                    if (scriptValue25 instanceof ScriptValue.Obj && (object14 = (obj10 = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object14 instanceof PolyClass) && obj10.typeName().equals("Container")) {
                        PolyClassContainer polyClassContainer = new PolyClassContainer(object14);
                        v11 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d16, scriptValue26));
                    } else {
                        ArrayList<ScriptValue> arrayList17 = new ArrayList<ScriptValue>();
                        arrayList17.add(ScriptValue.of((double)d16));
                        arrayList17.add(scriptValue26);
                        v11 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue25, arrayList17, (ScriptContext)scriptContext);
                    }
                }
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
        if (scriptValue27 != ScriptValue.NULL) {
            ScriptValue.Obj obj11;
            Object object15;
            CallSite callSite6 = callSite4;
            double d17 = 0.0;
            double d18 = -1.0;
            double d19 = 0.0;
            if (scriptValue27 instanceof ScriptValue.Obj && (object15 = (obj11 = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object15 instanceof PolyClass) && obj11.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v411 = new PolyClassMachine_v4(object15);
                v12 = ScriptValue.of((boolean)polyClassMachine_v411.tm$50_drop_item_at((ScriptValue)callSite6, d17, d18, d19));
            } else {
                ArrayList<CallSite> arrayList18 = new ArrayList<CallSite>();
                arrayList18.add(callSite6);
                arrayList18.add((CallSite)ScriptValue.of((double)d17));
                arrayList18.add((CallSite)ScriptValue.of((double)d18));
                arrayList18.add((CallSite)ScriptValue.of((double)d19));
                v12 = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue27, arrayList18, (ScriptContext)scriptContext);
            }
        } else {
            v12 = ScriptValue.NULL;
        }
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue29 = scriptValue28 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue28)) != null ? polyClassMachine_v4.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue28, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d20 = 0.0;
        ScriptValue scriptValue30 = scriptContext.getClassOrVar("null");
        if (scriptValue29 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            v13 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d20, scriptValue30));
        } else {
            ArrayList<ScriptValue> arrayList19 = new ArrayList<ScriptValue>();
            arrayList19.add(ScriptValue.of((double)d20));
            arrayList19.add(scriptValue30);
            v13 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue29, arrayList19, (ScriptContext)scriptContext);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelDropHeld(ScriptContext.Builder builder) {
        block6: {
            ScriptValue.Obj obj;
            Object object;
            PolyClassMachine_v4 polyClassMachine_v4;
            CallSite callSite;
            ScriptValue.Obj obj2;
            Object object2;
            PolyClassMachine_v4 polyClassMachine_v42;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v42.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d = 0.0;
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                callSite = polyClassContainer.tm$0_get_item(d);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((double)d));
                callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
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
                v1 = scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v4(object3).um$4_drop_item(arrayList2) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v4.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d2 = 0.0;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("null");
            if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object);
                v2 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d2, scriptValue6));
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((double)d2));
                arrayList3.add(scriptValue6);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelOnRightClick(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v4 polyClassMachine_v4;
        CallSite callSite;
        ScriptValue.Obj obj2;
        Object object2;
        PolyClassMachine_v4 polyClassMachine_v42;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        arrayList.add((ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer.pg$48_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v42.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d = 0.0;
        if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(ScriptValue.of((double)d));
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("held", (ScriptValue)callSite2);
        ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
        arrayList3.add(callSite2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool()) {
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
                ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
                arrayList4.add(callSite3);
                v1 = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)scriptValue4, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassMachine_v4.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d2 = 0.0;
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("null");
        if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            v2 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d2, scriptValue7));
        } else {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(ScriptValue.of((double)d2));
            arrayList5.add(scriptValue7);
            v2 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue6, arrayList5, (ScriptContext)scriptContext);
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
