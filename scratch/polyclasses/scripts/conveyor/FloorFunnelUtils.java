/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer
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
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer;
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
                var8_4 = PolyClassMachine.ofGuarded((ScriptValue)var5_2);
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
                var11_10 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
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
        ScriptValue scriptValue;
        Object object;
        ScriptValue scriptValue2;
        PolyClassMachine polyClassMachine;
        ScriptValue scriptValue3;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine2 != null ? polyClassMachine2.pg$120_container() : ((scriptValue3 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d = 0.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue4);
        ScriptValue scriptValue5 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        builder.val("held", scriptValue5);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool()) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue6 = FloorFunnelUtils._floorFunnelVacuum(builder2);
            builder.val("held", scriptValue6);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asBool() && scriptContext.getBool("pull_above")) {
                Object object2;
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
                if (scriptValue7 != ScriptValue.NULL) {
                    double d2 = 0.0;
                    double d3 = 1.0;
                    double d4 = 0.0;
                    PolyClassMachine polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                    object2 = polyClassMachine3 != null ? polyClassMachine3.tm$17_container_at(d2, d3, d4) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)ScriptValue.of((double)d3), (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue8 = object2;
                builder.val("above", scriptValue8);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue8, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FloorFunnelUtils.class, 64.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("held", scriptValue9);
                }
            }
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("held"), (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptValue scriptValue10;
                PolyClassMachine polyClassMachine4 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue11 = polyClassMachine4 != null ? polyClassMachine4.pg$120_container() : ((scriptValue10 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d5 = 0.0;
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("held");
                PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue11);
                Object object3 = polyClassContainer2 != null ? ScriptValue.of((boolean)polyClassContainer2.tm$10_set_item(d5, scriptValue12)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
            }
        }
        ScriptValue scriptValue13 = (polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine")) != null ? polyClassMachine.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d6 = 0.0;
        PolyClassContainer polyClassContainer3 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue13);
        ScriptValue scriptValue14 = polyClassContainer3 != null ? polyClassContainer3.tm$0_get_item(d6) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d6), (ScriptContext)scriptContext);
        builder.val("held", scriptValue14);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        if (scriptContext.getBool("pull_above")) {
            ScriptValue scriptValue15;
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
            if (scriptValue16 != ScriptValue.NULL) {
                ScriptValue scriptValue17 = scriptValue14;
                double d7 = 0.0;
                double d8 = -1.0;
                double d9 = 0.0;
                PolyClassMachine polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue16);
                v2 = polyClassMachine5 != null ? ScriptValue.of((boolean)polyClassMachine5.tm$50_drop_item_at(scriptValue17, d7, d8, d9)) : PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue16, (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((double)d7), (ScriptValue)ScriptValue.of((double)d8), (ScriptValue)ScriptValue.of((double)d9), (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            PolyClassMachine polyClassMachine6 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue18 = polyClassMachine6 != null ? polyClassMachine6.pg$120_container() : ((scriptValue15 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d10 = 0.0;
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("null");
            PolyClassContainer polyClassContainer4 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue18);
            Object object4 = polyClassContainer4 != null ? ScriptValue.of((boolean)polyClassContainer4.tm$10_set_item(d10, scriptValue19)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("Machine");
        if (scriptValue20 != ScriptValue.NULL) {
            double d11 = 0.0;
            double d12 = -1.0;
            double d13 = 0.0;
            PolyClassMachine polyClassMachine7 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue20);
            object = polyClassMachine7 != null ? polyClassMachine7.tm$17_container_at(d11, d12, d13) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((double)d11), (ScriptValue)ScriptValue.of((double)d12), (ScriptValue)ScriptValue.of((double)d13), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue21 = object;
        builder.val("below", scriptValue21);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue21, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            ScriptValue scriptValue22 = scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue21, (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("leftover", scriptValue22);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue22, (ScriptContext)scriptContext).asBool()) {
                ScriptValue scriptValue23;
                PolyClassMachine polyClassMachine8 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue24 = polyClassMachine8 != null ? polyClassMachine8.pg$120_container() : ((scriptValue23 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d14 = 0.0;
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("null");
                PolyClassContainer polyClassContainer5 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue24);
                Object object5 = polyClassContainer5 != null ? ScriptValue.of((boolean)polyClassContainer5.tm$10_set_item(d14, scriptValue25)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((double)d14), (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
            } else if (ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue22, (ScriptContext)scriptContext).asNum() < ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asNum()) {
                ScriptValue scriptValue26;
                PolyClassMachine polyClassMachine9 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                ScriptValue scriptValue27 = polyClassMachine9 != null ? polyClassMachine9.pg$120_container() : ((scriptValue26 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue26, (ScriptContext)scriptContext) : ScriptValue.NULL);
                double d15 = 0.0;
                ScriptValue scriptValue28 = scriptValue22;
                PolyClassContainer polyClassContainer6 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue27);
                Object object6 = polyClassContainer6 != null ? ScriptValue.of((boolean)polyClassContainer6.tm$10_set_item(d15, scriptValue28)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue27, (ScriptValue)ScriptValue.of((double)d15), (ScriptValue)scriptValue28, (ScriptContext)scriptContext);
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("Machine");
        if (scriptValue29 != ScriptValue.NULL) {
            ScriptValue scriptValue30 = scriptValue14;
            double d16 = 0.0;
            double d17 = -1.0;
            double d18 = 0.0;
            PolyClassMachine polyClassMachine10 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue29);
            v7 = polyClassMachine10 != null ? ScriptValue.of((boolean)polyClassMachine10.tm$50_drop_item_at(scriptValue30, d16, d17, d18)) : PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue29, (ScriptValue)scriptValue30, (ScriptValue)ScriptValue.of((double)d16), (ScriptValue)ScriptValue.of((double)d17), (ScriptValue)ScriptValue.of((double)d18), (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        PolyClassMachine polyClassMachine11 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue31 = polyClassMachine11 != null ? polyClassMachine11.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d19 = 0.0;
        ScriptValue scriptValue32 = scriptContext.getClassOrVar("null");
        PolyClassContainer polyClassContainer7 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue31);
        Object object7 = polyClassContainer7 != null ? ScriptValue.of((boolean)polyClassContainer7.tm$10_set_item(d19, scriptValue32)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue31, (ScriptValue)ScriptValue.of((double)d19), (ScriptValue)scriptValue32, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelDropHeld(ScriptContext.Builder builder) {
        block2: {
            ScriptValue scriptValue;
            ScriptValue scriptValue2;
            ScriptContext scriptContext = builder.peek();
            PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue3 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d = 0.0;
            PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue3);
            ScriptValue scriptValue4 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            builder.val("held", scriptValue4);
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool() ^ true)) break block2;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5);
                v0 = polyClassMachine2 != null ? polyClassMachine2.um$4_drop_item(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue6 = polyClassMachine3 != null ? polyClassMachine3.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d2 = 0.0;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("null");
            PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue6);
            Object object = polyClassContainer2 != null ? ScriptValue.of((boolean)polyClassContainer2.tm$10_set_item(d2, scriptValue7)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((double)d2), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelOnRightClick(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        ScriptContext scriptContext = builder.peek();
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$48_main_hand() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
            PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue6);
            v0 = polyClassPlayer2 != null ? ScriptValue.of((boolean)polyClassPlayer2.tm$14_give_item(scriptValue7)) : PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue8 = polyClassMachine2 != null ? polyClassMachine2.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
