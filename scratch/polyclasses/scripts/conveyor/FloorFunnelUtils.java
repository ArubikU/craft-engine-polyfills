/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script.gen.conveyor;

import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.Utils;
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
        block7: {
            var1_1 = var0.peek();
            var5_2 = var1_1.getClassOrVar("Machine");
            if (var5_2 != ScriptValue.NULL) {
                var6_3 = 0.7;
                var8_4 = PolyClassMachine_v3.ofGuarded((ScriptValue)var5_2);
                v0 /* !! */  = var8_4 != null ? var8_4.tm$94_nearby_entities(var6_3) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var5_2, (ScriptValue)ScriptValue.of((double)var6_3), (ScriptContext)var1_1);
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var2_5 = ScriptProgram.elementsOf((ScriptValue)v0 /* !! */ );
            var9_6 = var1_1.getClassOrVar("item");
            if (var2_5 == null) break block7;
            for (ScriptValue var4_8 : var2_5) {
                var0.val("entity", var4_8);
                var10_9 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                var10_9.val("entity", var4_8);
                if (!Utils.isRestingItem(var10_9).asBool()) ** GOTO lbl-1000
                v1 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var4_8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var4_8, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1).asNum();
                var11_10 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                v2 = var11_10 != null ? var11_10.tg$203_y() : ((var12_11 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var12_11, (ScriptContext)var1_1).asNum() : ScriptValue.NULL.asNum());
                if (v1 >= v2) {
                    v3 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v3 = false;
                }
                if (!v3) continue;
                var13_12 = var4_8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var4_8, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("item", var13_12);
                var9_6 = var13_12;
                v4 /* !! */  = var4_8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var4_8, (ScriptContext)var1_1) : ScriptValue.NULL;
                return var9_6;
            }
        }
        if ((var14_13 = var1_1.getClassOrVar("Item")) != ScriptValue.NULL) {
            var15_14 = new ArrayList<ScriptValue>();
            var15_14.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FloorFunnelUtils.class, "minecraft:air"));
            var16_15 = PolyClassItem.ofGuarded((ScriptValue)var14_13);
            v5 /* !! */  = var16_15 != null ? var16_15.um$21_create(var15_14) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var14_13, var15_14, (ScriptContext)var1_1);
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        return v5 /* !! */ ;
    }

    public static ScriptValue _floorFunnelTick(ScriptContext.Builder builder) {
        PolyClassMachine_v3 polyClassMachine_v3;
        Object object;
        ScriptValue scriptValue;
        PolyClassMachine_v3 polyClassMachine_v32;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine_v33 != null ? polyClassMachine_v33.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 0.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue3);
        ScriptValue scriptValue4 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        builder.val("held", scriptValue4);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool()) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue5 = FloorFunnelUtils._floorFunnelVacuum(builder2);
            builder.val("held", scriptValue5);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool() && scriptContext.getBool("pull_above")) {
                Object object2;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                if (scriptValue6 != ScriptValue.NULL) {
                    double d2 = 0.0;
                    double d3 = 1.0;
                    double d4 = 0.0;
                    PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue6);
                    object2 = polyClassMachine_v34 != null ? polyClassMachine_v34.tm$17_container_at(d2, d3, d4) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue7 = object2;
                builder.val("above", scriptValue7);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FloorFunnelUtils.class, 64.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("held", scriptValue8);
                }
            }
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("held"), (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptValue scriptValue9;
                PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue10 = polyClassMachine_v35 != null ? polyClassMachine_v35.pg$120_container() : ((scriptValue9 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d5 = 0.0;
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("held");
                PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue10);
                Object object3 = polyClassContainer2 != null ? ScriptValue.of((boolean)polyClassContainer2.tm$10_set_item(d5, scriptValue11)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
            }
        }
        ScriptValue scriptValue12 = (polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine_v32.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d6 = 0.0;
        PolyClassContainer polyClassContainer3 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue12);
        ScriptValue scriptValue13 = polyClassContainer3 != null ? polyClassContainer3.tm$0_get_item(d6) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
        builder.val("held", scriptValue13);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue13, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        if (scriptContext.getBool("pull_above")) {
            PolyClassMachine_v3 polyClassMachine_v36;
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue scriptValue15 = scriptValue13;
                double d7 = 0.0;
                double d8 = -1.0;
                double d9 = 0.0;
                PolyClassMachine_v3 polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue14);
                v2 = polyClassMachine_v37 != null ? ScriptValue.of((boolean)polyClassMachine_v37.tm$50_drop_item_at(scriptValue15, d7, d8, d9)) : PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)ScriptValue.of((double)d8), (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue16 = scriptValue14 != ScriptValue.NULL ? ((polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine_v36.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d10 = 0.0;
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("null");
            PolyClassContainer polyClassContainer4 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue16);
            Object object4 = polyClassContainer4 != null ? ScriptValue.of((boolean)polyClassContainer4.tm$10_set_item(d10, scriptValue17)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
        if (scriptValue18 != ScriptValue.NULL) {
            double d11 = 0.0;
            double d12 = -1.0;
            double d13 = 0.0;
            PolyClassMachine_v3 polyClassMachine_v38 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue18);
            object = polyClassMachine_v38 != null ? polyClassMachine_v38.tm$17_container_at(d11, d12, d13) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((double)d11), (ScriptValue)ScriptValue.of((double)d12), (ScriptValue)ScriptValue.of((double)d13), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = object;
        builder.val("below", scriptValue19);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue19, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ScriptValue scriptValue20 = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue19, (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("leftover", scriptValue20);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue20, (ScriptContext)scriptContext).asBool()) {
                PolyClassMachine_v3 polyClassMachine_v39;
                ScriptValue scriptValue21 = scriptValue18 != ScriptValue.NULL ? ((polyClassMachine_v39 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassMachine_v39.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d14 = 0.0;
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("null");
                PolyClassContainer polyClassContainer5 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue21);
                Object object5 = polyClassContainer5 != null ? ScriptValue.of((boolean)polyClassContainer5.tm$10_set_item(d14, scriptValue22)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((double)d14), (ScriptValue)scriptValue22, (ScriptContext)scriptContext);
            } else if (ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue20, (ScriptContext)scriptContext).asNum() < ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue13, (ScriptContext)scriptContext).asNum()) {
                PolyClassMachine_v3 polyClassMachine_v310;
                ScriptValue scriptValue23 = scriptValue18 != ScriptValue.NULL ? ((polyClassMachine_v310 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassMachine_v310.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                double d15 = 0.0;
                ScriptValue scriptValue24 = scriptValue20;
                PolyClassContainer polyClassContainer6 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue23);
                Object object6 = polyClassContainer6 != null ? ScriptValue.of((boolean)polyClassContainer6.tm$10_set_item(d15, scriptValue24)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((double)d15), (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
            }
            return ScriptValue.NULL;
        }
        if (scriptValue18 != ScriptValue.NULL) {
            ScriptValue scriptValue25 = scriptValue13;
            double d16 = 0.0;
            double d17 = -1.0;
            double d18 = 0.0;
            PolyClassMachine_v3 polyClassMachine_v311 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue18);
            v7 = polyClassMachine_v311 != null ? ScriptValue.of((boolean)polyClassMachine_v311.tm$50_drop_item_at(scriptValue25, d16, d17, d18)) : PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue18, (ScriptValue)scriptValue25, (ScriptValue)ScriptValue.of((double)d16), (ScriptValue)ScriptValue.of((double)d17), (ScriptValue)ScriptValue.of((double)d18), (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue26 = scriptValue18 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassMachine_v3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        double d19 = 0.0;
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("null");
        PolyClassContainer polyClassContainer7 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue26);
        Object object7 = polyClassContainer7 != null ? ScriptValue.of((boolean)polyClassContainer7.tm$10_set_item(d19, scriptValue27)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((double)d19), (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelDropHeld(ScriptContext.Builder builder) {
        block2: {
            PolyClassMachine_v3 polyClassMachine_v3;
            ScriptValue scriptValue;
            ScriptContext scriptContext = builder.peek();
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue2 = polyClassMachine_v32 != null ? polyClassMachine_v32.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d = 0.0;
            PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue2);
            ScriptValue scriptValue3 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            builder.val("held", scriptValue3);
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool() ^ true)) break block2;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4);
                v0 = polyClassMachine_v33 != null ? polyClassMachine_v33.um$4_drop_item(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine_v3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL;
            double d2 = 0.0;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("null");
            PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue5);
            Object object = polyClassContainer2 != null ? ScriptValue.of((boolean)polyClassContainer2.tm$10_set_item(d2, scriptValue6)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelOnRightClick(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        ScriptContext scriptContext = builder.peek();
        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)(polyClassPlayer_v2 != null ? polyClassPlayer_v2.pg$48_main_hand() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 0.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue4);
        ScriptValue scriptValue5 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        builder.val("held", scriptValue5);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue scriptValue7 = scriptValue5;
            PolyClassPlayer_v2 polyClassPlayer_v22 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue6);
            v0 = polyClassPlayer_v22 != null ? ScriptValue.of((boolean)polyClassPlayer_v22.tm$14_give_item(scriptValue7)) : PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue8 = polyClassMachine_v32 != null ? polyClassMachine_v32.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d2 = 0.0;
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("null");
        PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue8);
        Object object = polyClassContainer2 != null ? ScriptValue.of((boolean)polyClassContainer2.tm$10_set_item(d2, scriptValue9)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
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
