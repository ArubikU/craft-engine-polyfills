/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
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
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
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
                    var10_6 = new PolyClassMachine(var9_5);
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
                var13_12 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
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
                v4 /* !! */  = var4_10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var4_10, (ScriptContext)var1_1) : ScriptValue.NULL;
                return var11_8;
            }
        }
        if ((var16_15 = var1_1.getClassOrVar("Item")) != ScriptValue.NULL) {
            var17_16 = new ArrayList<ScriptValue>();
            var17_16.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FloorFunnelUtils.class, "minecraft:air"));
            v5 /* !! */  = var16_15 instanceof ScriptValue.Obj && (var19_18 = (var18_17 = (ScriptValue.Obj)var16_15).instance()) != null && !(var19_18 instanceof PolyClass) && var18_17.typeName().equals("Item") ? new PolyClassItem(var19_18).um$21_create(var17_16) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var16_15, var17_16, (ScriptContext)var1_1);
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
        PolyClassMachine polyClassMachine;
        CallSite callSite2;
        ScriptValue.Obj obj3;
        Object object4;
        ScriptValue scriptValue3;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine2 != null ? polyClassMachine2.pg$120_container() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
                        PolyClassMachine polyClassMachine3 = new PolyClassMachine(object6);
                        object5 = polyClassMachine3.tm$17_container_at(d2, d3, d4);
                    } else {
                        object5 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
                    }
                } else {
                    object5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue7 = object5;
                builder.val("above", scriptValue7);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FloorFunnelUtils.class, 64.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("held", scriptValue8);
                }
            }
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("held"), (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptValue.Obj obj5;
                Object object7;
                ScriptValue scriptValue9;
                PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue10 = polyClassMachine4 != null ? polyClassMachine4.pg$120_container() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
        ScriptValue scriptValue12 = (polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d6 = 0.0;
        if (scriptValue12 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
            callSite = polyClassContainer.tm$0_get_item(d6);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
        }
        CallSite callSite4 = callSite;
        builder.val("held", (ScriptValue)callSite4);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite4, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        if (scriptContext.getBool("pull_above")) {
            ScriptValue.Obj obj6;
            Object object8;
            ScriptValue scriptValue13;
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj7;
                Object object9;
                CallSite callSite5 = callSite4;
                double d7 = 0.0;
                double d8 = -1.0;
                double d9 = 0.0;
                if (scriptValue14 instanceof ScriptValue.Obj && (object9 = (obj7 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object9 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine5 = new PolyClassMachine(object9);
                    v4 = ScriptValue.of((boolean)polyClassMachine5.tm$50_drop_item_at((ScriptValue)callSite5, d7, d8, d9));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue14, (ScriptValue)callSite5, (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)ScriptValue.of((double)d8), (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            PolyClassMachine polyClassMachine6 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue15 = polyClassMachine6 != null ? polyClassMachine6.pg$120_container() : ((scriptValue13 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
                PolyClassMachine polyClassMachine7 = new PolyClassMachine(object10);
                object2 = polyClassMachine7.tm$17_container_at(d11, d12, d13);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((double)d11), (ScriptValue)ScriptValue.of((double)d12), (ScriptValue)ScriptValue.of((double)d13), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = object2;
        builder.val("below", scriptValue18);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue18, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ScriptValue scriptValue19 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue18, (ScriptValue)callSite4, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("leftover", scriptValue19);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue19, (ScriptContext)scriptContext).asBool()) {
                ScriptValue.Obj obj9;
                Object object11;
                ScriptValue scriptValue20;
                PolyClassMachine polyClassMachine8 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue21 = polyClassMachine8 != null ? polyClassMachine8.pg$120_container() : ((scriptValue20 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d14 = 0.0;
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("null");
                if (scriptValue21 instanceof ScriptValue.Obj && (object11 = (obj9 = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object11 instanceof PolyClass) && obj9.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object11);
                    v7 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d14, scriptValue22));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((double)d14), (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
                }
            } else if (ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue19, (ScriptContext)scriptContext).asNum() < ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)callSite4, (ScriptContext)scriptContext).asNum()) {
                ScriptValue.Obj obj10;
                Object object12;
                ScriptValue scriptValue23;
                PolyClassMachine polyClassMachine9 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue24 = polyClassMachine9 != null ? polyClassMachine9.pg$120_container() : ((scriptValue23 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d15 = 0.0;
                ScriptValue scriptValue25 = scriptValue19;
                if (scriptValue24 instanceof ScriptValue.Obj && (object12 = (obj10 = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object12 instanceof PolyClass) && obj10.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object12);
                    v8 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d15, scriptValue25));
                } else {
                    v8 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((double)d15), (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
                }
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue26 = scriptContext.getClassOrVar("Machine");
        if (scriptValue26 != ScriptValue.NULL) {
            ScriptValue.Obj obj11;
            Object object13;
            CallSite callSite6 = callSite4;
            double d16 = 0.0;
            double d17 = -1.0;
            double d18 = 0.0;
            if (scriptValue26 instanceof ScriptValue.Obj && (object13 = (obj11 = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object13 instanceof PolyClass) && obj11.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine10 = new PolyClassMachine(object13);
                v9 = ScriptValue.of((boolean)polyClassMachine10.tm$50_drop_item_at((ScriptValue)callSite6, d16, d17, d18));
            } else {
                v9 = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue26, (ScriptValue)callSite6, (ScriptValue)ScriptValue.of((double)d16), (ScriptValue)ScriptValue.of((double)d17), (ScriptValue)ScriptValue.of((double)d18), (ScriptContext)scriptContext);
            }
        } else {
            v9 = ScriptValue.NULL;
        }
        PolyClassMachine polyClassMachine11 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue27 = polyClassMachine11 != null ? polyClassMachine11.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d19 = 0.0;
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("null");
        if (scriptValue27 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            v10 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d19, scriptValue28));
        } else {
            v10 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue27, (ScriptValue)ScriptValue.of((double)d19), (ScriptValue)scriptValue28, (ScriptContext)scriptContext);
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
            PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue3 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
                v1 = scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine(object3).um$4_drop_item(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue5 = polyClassMachine2 != null ? polyClassMachine2.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)(polyClassPlayer_v2 != null ? polyClassPlayer_v2.pg$48_main_hand() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
                PolyClassPlayer_v2 polyClassPlayer_v22 = new PolyClassPlayer_v2(object3);
                v1 = ScriptValue.of((boolean)polyClassPlayer_v22.tm$14_give_item((ScriptValue)callSite3));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)scriptValue5, (ScriptValue)callSite3, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue6 = polyClassMachine2 != null ? polyClassMachine2.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
