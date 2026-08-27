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
        block9: {
            var1_1 = var0.peek();
            var5_2 = var1_1.getClassOrVar("Machine");
            if (var5_2 != ScriptValue.NULL) {
                var6_3 = 0.7;
                if (var5_2 instanceof ScriptValue.Obj && (var9_5 = (var8_4 = (ScriptValue.Obj)var5_2).instance()) != null && !(var9_5 instanceof PolyClass) && var8_4.typeName().equals("Machine")) {
                    var10_6 = new PolyClassMachine_v4(var9_5);
                    v0 /* !! */  = var10_6.tm$94_nearby_entities(var6_3);
                } else {
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var5_2, (ScriptValue)ScriptValue.of((double)var6_3), (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var2_7 = ScriptProgram.elementsOf((ScriptValue)v0 /* !! */ );
            var11_8 = var1_1.getClassOrVar("item");
            if (var2_7 == null) break block9;
            for (ScriptValue var4_10 : var2_7) {
                var0.val("entity", var4_10);
                var12_11 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                var12_11.val("entity", var4_10);
                if (!Utils.isRestingItem(var12_11).asBool()) ** GOTO lbl-1000
                v1 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var4_10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var4_10, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1).asNum();
                var13_12 = PolyClassMachine_v4.ofVar((ScriptContext)var1_1, (String)"Machine");
                v2 = var13_12 != null ? var13_12.tg$203_y() : ((var14_13 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var14_13, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum());
                if (v1 >= v2) {
                    v3 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v3 = false;
                }
                if (!v3) continue;
                var15_14 = var4_10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var4_10, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("item", var15_14);
                var11_8 = var15_14;
                var16_15 = var1_1.getClassOrVar("entity");
                v4 /* !! */  = var16_15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var16_15, (ScriptContext)var1_1) : ScriptValue.NULL;
                return var11_8;
            }
        }
        if ((var17_16 = var1_1.getClassOrVar("Item")) != ScriptValue.NULL) {
            var18_17 = new ArrayList<ScriptValue>();
            var18_17.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FloorFunnelUtils.class, "minecraft:air"));
            v5 /* !! */  = var17_16 instanceof ScriptValue.Obj && (var20_19 = (var19_18 = (ScriptValue.Obj)var17_16).instance()) != null && !(var20_19 instanceof PolyClass) && var19_18.typeName().equals("Item") ? new PolyClassItem(var20_19).um$21_create(var18_17) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var17_16, var18_17, (ScriptContext)var1_1);
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        return v5 /* !! */ ;
    }

    public static ScriptValue _floorFunnelTick(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptValue scriptValue;
        Object object2;
        CallSite callSite;
        ScriptValue.Obj obj2;
        Object object3;
        ScriptValue scriptValue2;
        PolyClassMachine_v4 polyClassMachine_v4;
        CallSite callSite2;
        ScriptValue.Obj obj3;
        Object object4;
        ScriptValue scriptValue3;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine_v42 != null ? polyClassMachine_v42.pg$120_container() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 0.0;
        if (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object4);
            callSite2 = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite2 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite3 = callSite2;
        builder.val("held", (ScriptValue)callSite3);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite3, (ScriptContext)scriptContext).asBool()) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue5 = FloorFunnelUtils._floorFunnelVacuum(builder2);
            builder.val("held", scriptValue5);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool() && scriptContext.getBool("pull_above")) {
                Object object5;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                if (scriptValue6 != ScriptValue.NULL) {
                    ScriptValue.Obj obj4;
                    Object object6;
                    double d2 = 0.0;
                    double d3 = 1.0;
                    double d4 = 0.0;
                    if (scriptValue6 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                        PolyClassMachine_v4 polyClassMachine_v43 = new PolyClassMachine_v4(object6);
                        object5 = polyClassMachine_v43.tm$17_container_at(d2, d3, d4);
                    } else {
                        object5 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue7 = object5;
                builder.val("above", scriptValue7);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue8 = scriptContext.getClassOrVar("above");
                    ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FloorFunnelUtils.class, 64.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("held", scriptValue9);
                }
            }
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("held"), (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptValue.Obj obj5;
                Object object7;
                ScriptValue scriptValue10;
                PolyClassMachine_v4 polyClassMachine_v44 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue11 = polyClassMachine_v44 != null ? polyClassMachine_v44.pg$120_container() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d5 = 0.0;
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("held");
                if (scriptValue11 instanceof ScriptValue.Obj && (object7 = (obj5 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object7 instanceof PolyClass) && obj5.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object7);
                    v2 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d5, scriptValue12));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                }
            }
        }
        ScriptValue scriptValue13 = (polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine_v4.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d6 = 0.0;
        if (scriptValue13 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
            callSite = polyClassContainer.tm$0_get_item(d6);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
        }
        CallSite callSite4 = callSite;
        builder.val("held", (ScriptValue)callSite4);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite4, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        if (scriptContext.getBool("pull_above")) {
            ScriptValue.Obj obj6;
            Object object8;
            ScriptValue scriptValue14;
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj7;
                Object object9;
                CallSite callSite5 = callSite4;
                double d7 = 0.0;
                double d8 = -1.0;
                double d9 = 0.0;
                if (scriptValue15 instanceof ScriptValue.Obj && (object9 = (obj7 = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object9 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v45 = new PolyClassMachine_v4(object9);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v45.tm$50_drop_item_at((ScriptValue)callSite5, d7, d8, d9));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue15, (ScriptValue)callSite5, (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)ScriptValue.of((double)d8), (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            PolyClassMachine_v4 polyClassMachine_v46 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue16 = polyClassMachine_v46 != null ? polyClassMachine_v46.pg$120_container() : ((scriptValue14 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d10 = 0.0;
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("null");
            if (scriptValue16 instanceof ScriptValue.Obj && (object8 = (obj6 = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object8 instanceof PolyClass) && obj6.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object8);
                v5 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d10, scriptValue17));
            } else {
                v5 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
        if (scriptValue18 != ScriptValue.NULL) {
            ScriptValue.Obj obj8;
            Object object10;
            double d11 = 0.0;
            double d12 = -1.0;
            double d13 = 0.0;
            if (scriptValue18 instanceof ScriptValue.Obj && (object10 = (obj8 = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object10 instanceof PolyClass) && obj8.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v47 = new PolyClassMachine_v4(object10);
                object2 = polyClassMachine_v47.tm$17_container_at(d11, d12, d13);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((double)d11), (ScriptValue)ScriptValue.of((double)d12), (ScriptValue)ScriptValue.of((double)d13), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = object2;
        builder.val("below", scriptValue19);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue19, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("below");
            ScriptValue scriptValue21 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue20, (ScriptValue)callSite4, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("leftover", scriptValue21);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue21, (ScriptContext)scriptContext).asBool()) {
                ScriptValue.Obj obj9;
                Object object11;
                ScriptValue scriptValue22;
                PolyClassMachine_v4 polyClassMachine_v48 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue23 = polyClassMachine_v48 != null ? polyClassMachine_v48.pg$120_container() : ((scriptValue22 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue22, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d14 = 0.0;
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("null");
                if (scriptValue23 instanceof ScriptValue.Obj && (object11 = (obj9 = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object11 instanceof PolyClass) && obj9.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object11);
                    v7 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d14, scriptValue24));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((double)d14), (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
                }
            } else if (ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue21, (ScriptContext)scriptContext).asNum() < ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)callSite4, (ScriptContext)scriptContext).asNum()) {
                ScriptValue.Obj obj10;
                Object object12;
                ScriptValue scriptValue25;
                PolyClassMachine_v4 polyClassMachine_v49 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue26 = polyClassMachine_v49 != null ? polyClassMachine_v49.pg$120_container() : ((scriptValue25 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue25, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d15 = 0.0;
                ScriptValue scriptValue27 = scriptValue21;
                if (scriptValue26 instanceof ScriptValue.Obj && (object12 = (obj10 = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object12 instanceof PolyClass) && obj10.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object12);
                    v8 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d15, scriptValue27));
                } else {
                    v8 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((double)d15), (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
                }
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("Machine");
        if (scriptValue28 != ScriptValue.NULL) {
            ScriptValue.Obj obj11;
            Object object13;
            CallSite callSite6 = callSite4;
            double d16 = 0.0;
            double d17 = -1.0;
            double d18 = 0.0;
            if (scriptValue28 instanceof ScriptValue.Obj && (object13 = (obj11 = (ScriptValue.Obj)scriptValue28).instance()) != null && !(object13 instanceof PolyClass) && obj11.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v410 = new PolyClassMachine_v4(object13);
                v9 = ScriptValue.of((boolean)polyClassMachine_v410.tm$50_drop_item_at((ScriptValue)callSite6, d16, d17, d18));
            } else {
                v9 = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue28, (ScriptValue)callSite6, (ScriptValue)ScriptValue.of((double)d16), (ScriptValue)ScriptValue.of((double)d17), (ScriptValue)ScriptValue.of((double)d18), (ScriptContext)scriptContext);
            }
        } else {
            v9 = ScriptValue.NULL;
        }
        PolyClassMachine_v4 polyClassMachine_v411 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue29 = polyClassMachine_v411 != null ? polyClassMachine_v411.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d19 = 0.0;
        ScriptValue scriptValue30 = scriptContext.getClassOrVar("null");
        if (scriptValue29 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            v10 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d19, scriptValue30));
        } else {
            v10 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue29, (ScriptValue)ScriptValue.of((double)d19), (ScriptValue)scriptValue30, (ScriptContext)scriptContext);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelDropHeld(ScriptContext.Builder builder) {
        block6: {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue;
            CallSite callSite;
            ScriptValue.Obj obj2;
            Object object2;
            ScriptValue scriptValue2;
            ScriptContext scriptContext = builder.peek();
            PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue3 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d = 0.0;
            if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
                callSite = polyClassContainer.tm$0_get_item(d);
            } else {
                callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
            CallSite callSite2 = callSite;
            builder.val("held", (ScriptValue)callSite2);
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool() ^ true)) break block6;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj3;
                Object object3;
                ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
                arrayList.add(callSite2);
                v1 = scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v4(object3).um$4_drop_item(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue5 = polyClassMachine_v42 != null ? polyClassMachine_v42.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
        ScriptValue scriptValue;
        CallSite callSite;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        ScriptContext scriptContext = builder.peek();
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$48_main_hand() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        PolyClassMachine_v4 polyClassMachine_v4 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine_v4 != null ? polyClassMachine_v4.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 0.0;
        if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object2);
            callSite = polyClassContainer.tm$0_get_item(d);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("held", (ScriptValue)callSite2);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object3;
            CallSite callSite3 = callSite2;
            if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object3);
                v1 = ScriptValue.of((boolean)polyClassPlayer2.tm$14_give_item((ScriptValue)callSite3));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)scriptValue5, (ScriptValue)callSite3, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        PolyClassMachine_v4 polyClassMachine_v42 = PolyClassMachine_v4.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue6 = polyClassMachine_v42 != null ? polyClassMachine_v42.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
