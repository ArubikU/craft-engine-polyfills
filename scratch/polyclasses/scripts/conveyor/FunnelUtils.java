/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBlock
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyClassRedstone
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.conveyor;

import dev.arubik.craftengine.script.PolyClassBlock;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyClassRedstone;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class FunnelUtils {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _funnelModeOut(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "mode";
        PolyClassBlock polyClassBlock = PolyClassBlock.ofGuarded((ScriptValue)scriptValue2);
        ScriptValue scriptValue3 = polyClassBlock != null ? polyClassBlock.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        builder.val("m", scriptValue3);
        return ScriptValue.of((ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"in") ^ true ? 1 : 0) != 0);
    }

    public static ScriptValue _funnelFacingVec(ScriptContext.Builder builder) {
        Object object;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "facing";
        PolyClassBlock polyClassBlock = PolyClassBlock.ofGuarded((ScriptValue)scriptValue2);
        ScriptValue scriptValue3 = polyClassBlock != null ? polyClassBlock.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        builder.val("f", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("DIR_VEC");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
            arrayList.add(ScriptValue.of((double)(-1.0)));
            object = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue4, (ScriptValue)scriptValue3, (ScriptValue)new ScriptValue.Array(arrayList), (ScriptContext)scriptContext);
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
            var9_8 = PolyClassMachine_v3.ofGuarded((ScriptValue)var5_4);
            v0 /* !! */  = var9_8 != null ? var9_8.tm$62_belt_at(var6_5.asNum(), var7_6.asNum(), var8_7.asNum()) : PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)var5_4, (ScriptValue)var6_5, (ScriptValue)var7_6, (ScriptValue)var8_7, (ScriptContext)var1_1);
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var10_9 = v0 /* !! */ ;
        var0.val("bf", var10_9);
        if (ScriptFormula.valuesEqual((ScriptValue)var10_9, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
            var11_10 = var10_9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)var10_9, (ScriptContext)var1_1) : ScriptValue.NULL;
            var0.val("best", var11_10);
        }
        if (var5_4 != ScriptValue.NULL) {
            var12_11 = -var1_1.getNum("fx");
            var14_12 = -var1_1.getNum("fy");
            var16_13 = -var1_1.getNum("fz");
            var18_14 = PolyClassMachine_v3.ofGuarded((ScriptValue)var5_4);
            v1 /* !! */  = var18_14 != null ? var18_14.tm$62_belt_at(var12_11, var14_12, var16_13) : PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)var5_4, (ScriptValue)ScriptValue.of((double)var12_11), (ScriptValue)ScriptValue.of((double)var14_12), (ScriptValue)ScriptValue.of((double)var16_13), (ScriptContext)var1_1);
        } else {
            v1 /* !! */  = ScriptValue.NULL;
        }
        var19_15 = v1 /* !! */ ;
        var0.val("bo", var19_15);
        if (!(ScriptFormula.valuesEqual((ScriptValue)var19_15, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) ** GOTO lbl-1000
        v2 /* !! */  = var19_15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)var19_15, (ScriptContext)var1_1) : ScriptValue.NULL;
        if (v2 /* !! */ .asNum() > var1_1.getNum("best")) {
            v3 = true;
        } else lbl-1000:
        // 2 sources

        {
            v3 = false;
        }
        if (v3) {
            var20_16 = var19_15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)var19_15, (ScriptContext)var1_1) : ScriptValue.NULL;
            var0.val("best", var20_16);
        }
        return var1_1.getNum("best") > 0.0 != false ? var1_1.getClassOrVar("best") : var1_1.getClassOrVar("DEFAULT_INC");
    }

    public static ScriptValue _funnelSetActivated(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue2 = ScriptFormula.subscriptGet((ScriptValue)scriptContext.getClassOrVar("behind_vec"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0)));
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptContext.getClassOrVar("behind_vec"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 1.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptContext.getClassOrVar("behind_vec"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 2.0)));
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$17_container_at(scriptValue2.asNum(), scriptValue3.asNum(), scriptValue4.asNum()) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("c", scriptValue5);
        if (scriptValue != ScriptValue.NULL) {
            String string = "activated";
            ScriptValue scriptValue6 = ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "false"));
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$16_set_property(string, scriptValue6.asStr())) : PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _funnelGiveForward(ScriptContext.Builder builder) {
        double d;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("fx");
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("fy");
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("fz");
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$62_belt_at(scriptValue2.asNum(), scriptValue3.asNum(), scriptValue4.asNum()) : PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("belt", scriptValue5);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true && (scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "exists", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            if ((scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_full", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
                return scriptContext.getClassOrVar("item");
            }
            return scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("fx");
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("fy");
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("fz");
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$17_container_at(scriptValue6.asNum(), scriptValue7.asNum(), scriptValue8.asNum()) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue, (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("target_c", scriptValue9);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue9, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            return scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue9, (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
        ScriptValue scriptValue10 = ScriptValue.of((double)d3);
        builder.val("force", scriptValue10);
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("item"));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fx") * d3)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fy") * d3)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fz") * d3)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fx") * 0.5)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fy") * 0.5)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fz") * 0.5)));
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
            v3 = polyClassMachine_v3 != null ? polyClassMachine_v3.um$19_drop_item_toward(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        return scriptContext.getClassOrVar("null");
    }

    public static ScriptValue _funnelTick(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue3 = FunnelUtils._funnelFacingVec(builder2);
        builder.val("v", scriptValue3);
        ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0)));
        builder.val("fx", scriptValue4);
        ScriptValue scriptValue5 = ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 1.0)));
        builder.val("fy", scriptValue5);
        ScriptValue scriptValue6 = ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 2.0)));
        builder.val("fz", scriptValue6);
        double d = -scriptValue4.asNum();
        ScriptValue scriptValue7 = ScriptValue.of((double)d);
        builder.val("bx", scriptValue7);
        double d2 = -scriptValue5.asNum();
        ScriptValue scriptValue8 = ScriptValue.of((double)d2);
        builder.val("by", scriptValue8);
        double d3 = -scriptValue6.asNum();
        ScriptValue scriptValue9 = ScriptValue.of((double)d3);
        builder.val("bz", scriptValue9);
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue10 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$171_redstone() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue10);
        if (polyClassRedstone != null ? polyClassRedstone.tg$19_powered() : PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                String string = "activated";
                String string2 = "false";
                PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue11);
                v0 = polyClassMachine_v32 != null ? ScriptValue.of((boolean)polyClassMachine_v32.tm$16_set_property(string, string2)) : PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue12 = FunnelUtils._funnelModeOut(builder3);
        builder.val("out", scriptValue12);
        PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue13 = polyClassMachine_v33 != null ? polyClassMachine_v33.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d4 = 0.0;
        PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue13);
        ScriptValue scriptValue14 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d4) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
        builder.val("held", scriptValue14);
        if (scriptValue12.asBool()) {
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asBool()) {
                Object object;
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                if (scriptValue15 != ScriptValue.NULL) {
                    double d5 = d;
                    double d6 = d2;
                    double d7 = d3;
                    PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue15);
                    object = polyClassMachine_v34 != null ? polyClassMachine_v34.tm$17_container_at(d5, d6, d7) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)ScriptValue.of((double)d6), (ScriptValue)ScriptValue.of((double)d7), (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue16 = object;
                builder.val("c", scriptValue16);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue16, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue17 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("taken", scriptValue17);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue17, (ScriptContext)scriptContext).asBool() ^ true) {
                        PolyClassMachine_v3 polyClassMachine_v35;
                        ScriptValue scriptValue18 = scriptValue15 != ScriptValue.NULL ? ((polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue15)) != null ? polyClassMachine_v35.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue15, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                        double d8 = 0.0;
                        ScriptValue scriptValue19 = scriptValue17;
                        PolyClassContainer polyClassContainer2 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue18);
                        Object object2 = polyClassContainer2 != null ? ScriptValue.of((boolean)polyClassContainer2.tm$10_set_item(d8, scriptValue19)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((double)d8), (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
                        ScriptValue scriptValue20 = scriptValue17;
                        builder.val("held", scriptValue20);
                    }
                }
            }
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("held"), (ScriptContext)scriptContext).asBool() ^ true) {
                Object object;
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                if (scriptValue21 != ScriptValue.NULL) {
                    String string = "_funnel_progress";
                    String string3 = "float";
                    PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue21);
                    object = polyClassMachine_v36 != null ? polyClassMachine_v36.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue22 = object;
                builder.val("progress", scriptValue22);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue22, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                    double d9 = 0.0;
                    ScriptValue scriptValue23 = ScriptValue.of((double)0.0);
                    builder.val("progress", scriptValue23);
                }
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("progress");
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("fx", scriptValue4);
                builder4.val("fy", scriptValue5);
                builder4.val("fz", scriptValue6);
                ScriptValue scriptValue25 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue24, (ScriptValue)FunnelUtils._funnelInc(builder4));
                builder.val("progress", scriptValue25);
                if (scriptValue25.asNum() < 1.0) {
                    if (scriptValue21 != ScriptValue.NULL) {
                        String string = "_funnel_progress";
                        String string4 = "float";
                        ScriptValue scriptValue26 = scriptValue25;
                        PolyClassMachine_v3 polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue21);
                        v5 = polyClassMachine_v37 != null ? ScriptValue.of((boolean)polyClassMachine_v37.tm$82_set_typed(string, string4, scriptValue26)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue26, (ScriptContext)scriptContext);
                    } else {
                        v5 = ScriptValue.NULL;
                    }
                } else {
                    ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                    builder5.val("item", scriptContext.getClassOrVar("held"));
                    builder5.val("fx", scriptValue4);
                    builder5.val("fy", scriptValue5);
                    builder5.val("fz", scriptValue6);
                    ScriptValue scriptValue27 = FunnelUtils._funnelGiveForward(builder5);
                    builder.val("leftover", scriptValue27);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue27, (ScriptContext)scriptContext).asBool()) {
                        PolyClassMachine_v3 polyClassMachine_v38;
                        ScriptValue scriptValue28 = scriptValue21 != ScriptValue.NULL ? ((polyClassMachine_v38 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue21)) != null ? polyClassMachine_v38.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                        double d10 = 0.0;
                        ScriptValue scriptValue29 = scriptContext.getClassOrVar("null");
                        PolyClassContainer polyClassContainer3 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue28);
                        Object object3 = polyClassContainer3 != null ? ScriptValue.of((boolean)polyClassContainer3.tm$10_set_item(d10, scriptValue29)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue28, (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)scriptValue29, (ScriptContext)scriptContext);
                        if (scriptValue21 != ScriptValue.NULL) {
                            String string = "_funnel_progress";
                            String string5 = "float";
                            ScriptValue scriptValue30 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0);
                            PolyClassMachine_v3 polyClassMachine_v39 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue21);
                            v7 = polyClassMachine_v39 != null ? ScriptValue.of((boolean)polyClassMachine_v39.tm$82_set_typed(string, string5, scriptValue30)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue30, (ScriptContext)scriptContext);
                        } else {
                            v7 = ScriptValue.NULL;
                        }
                    }
                }
            }
        } else if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue14, (ScriptContext)scriptContext).asBool() ^ true) {
            Object object;
            ScriptValue scriptValue31 = scriptContext.getClassOrVar("Machine");
            if (scriptValue31 != ScriptValue.NULL) {
                String string = "_funnel_progress";
                String string6 = "float";
                PolyClassMachine_v3 polyClassMachine_v310 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue31);
                object = polyClassMachine_v310 != null ? polyClassMachine_v310.tm$34_get_typed(string, string6) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue31, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue32 = object;
            builder.val("progress", scriptValue32);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue32, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                double d11 = 0.0;
                ScriptValue scriptValue33 = ScriptValue.of((double)0.0);
                builder.val("progress", scriptValue33);
            }
            ScriptValue scriptValue34 = scriptContext.getClassOrVar("progress");
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("fx", scriptValue4);
            builder6.val("fy", scriptValue5);
            builder6.val("fz", scriptValue6);
            ScriptValue scriptValue35 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue34, (ScriptValue)FunnelUtils._funnelInc(builder6));
            builder.val("progress", scriptValue35);
            if (scriptValue35.asNum() < 1.0) {
                if (scriptValue31 != ScriptValue.NULL) {
                    String string = "_funnel_progress";
                    String string7 = "float";
                    ScriptValue scriptValue36 = scriptValue35;
                    PolyClassMachine_v3 polyClassMachine_v311 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue31);
                    v10 = polyClassMachine_v311 != null ? ScriptValue.of((boolean)polyClassMachine_v311.tm$82_set_typed(string, string7, scriptValue36)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue31, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue36, (ScriptContext)scriptContext);
                } else {
                    v10 = ScriptValue.NULL;
                }
            } else {
                Object object4;
                if (scriptValue31 != ScriptValue.NULL) {
                    double d12 = d;
                    double d13 = d2;
                    double d14 = d3;
                    PolyClassMachine_v3 polyClassMachine_v312 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue31);
                    object4 = polyClassMachine_v312 != null ? polyClassMachine_v312.tm$17_container_at(d12, d13, d14) : PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue31, (ScriptValue)ScriptValue.of((double)d12), (ScriptValue)ScriptValue.of((double)d13), (ScriptValue)ScriptValue.of((double)d14), (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue37 = object4;
                builder.val("c", scriptValue37);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue37, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue38 = scriptValue37 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue37, (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("leftover", scriptValue38);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue38, (ScriptContext)scriptContext).asBool()) {
                        PolyClassMachine_v3 polyClassMachine_v313;
                        ScriptValue scriptValue39 = scriptValue31 != ScriptValue.NULL ? ((polyClassMachine_v313 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue31)) != null ? polyClassMachine_v313.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue31, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                        double d15 = 0.0;
                        ScriptValue scriptValue40 = scriptContext.getClassOrVar("null");
                        PolyClassContainer polyClassContainer4 = PolyClassContainer.ofGuarded((ScriptValue)scriptValue39);
                        Object object5 = polyClassContainer4 != null ? ScriptValue.of((boolean)polyClassContainer4.tm$10_set_item(d15, scriptValue40)) : PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue39, (ScriptValue)ScriptValue.of((double)d15), (ScriptValue)scriptValue40, (ScriptContext)scriptContext);
                        if (scriptValue31 != ScriptValue.NULL) {
                            String string = "_funnel_progress";
                            String string8 = "float";
                            ScriptValue scriptValue41 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0);
                            PolyClassMachine_v3 polyClassMachine_v314 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue31);
                            v13 = polyClassMachine_v314 != null ? ScriptValue.of((boolean)polyClassMachine_v314.tm$82_set_typed(string, string8, scriptValue41)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue31, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue41, (ScriptContext)scriptContext);
                        } else {
                            v13 = ScriptValue.NULL;
                        }
                    }
                }
            }
        }
        ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)d));
        arrayList.add(ScriptValue.of((double)d2));
        arrayList.add(ScriptValue.of((double)d3));
        builder7.val("behind_vec", (ScriptValue)new ScriptValue.Array(arrayList));
        FunnelUtils._funnelSetActivated(builder7);
        return ScriptValue.NULL;
    }

    public static ScriptValue _funnelToggleMode(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "mode";
        PolyClassBlock polyClassBlock = PolyClassBlock.ofGuarded((ScriptValue)scriptValue2);
        ScriptValue scriptValue3 = polyClassBlock != null ? polyClassBlock.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        builder.val("cur", scriptValue3);
        ScriptValue scriptValue4 = ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"in") ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "out")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "in"));
        builder.val("next", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
        if (scriptValue5 != ScriptValue.NULL) {
            String string2 = "mode";
            ScriptValue scriptValue6 = scriptValue4;
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue5);
            v0 = polyClassMachine_v32 != null ? ScriptValue.of((boolean)polyClassMachine_v32.tm$16_set_property(string2, scriptValue6.asStr())) : PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        if (scriptValue5 != ScriptValue.NULL) {
            String string3 = "_funnel_progress";
            String string4 = "float";
            ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0);
            PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue5);
            v1 = polyClassMachine_v33 != null ? ScriptValue.of((boolean)polyClassMachine_v33.tm$82_set_typed(string3, string4, scriptValue7)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue scriptValue9 = ScriptValue.of((String)("<yellow>Funnel mode: <white>" + ScriptFormula.callBuiltin1((String)"upper", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asStr()));
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue8);
            v2 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue9.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _funnelDropHeld(ScriptContext.Builder builder) {
        block2: {
            ScriptValue scriptValue;
            ScriptContext scriptContext = builder.peek();
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d = 0.0;
            PolyClassContainer polyClassContainer = PolyClassContainer.ofGuarded((ScriptValue)scriptValue2);
            ScriptValue scriptValue3 = polyClassContainer != null ? polyClassContainer.tm$0_get_item(d) : PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            builder.val("held", scriptValue3);
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool() ^ true)) break block2;
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4);
                v0 = polyClassMachine_v32 != null ? polyClassMachine_v32.um$4_drop_item(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<Object> arrayList = new ArrayList<Object>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "north"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
        arrayList2.add(ScriptValue.of((double)(-1.0)));
        arrayList.add(new ScriptValue.Array(arrayList2));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "south"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 1.0));
        arrayList.add(new ScriptValue.Array(arrayList3));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "east"));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 1.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
        arrayList.add(new ScriptValue.Array(arrayList4));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "west"));
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add(ScriptValue.of((double)(-1.0)));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
        arrayList.add(new ScriptValue.Array(arrayList5));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
        builder.val("DIR_VEC", scriptValue);
        double d = 0.0625;
        ScriptValue scriptValue2 = ScriptValue.of((double)0.0625);
        builder.val("DEFAULT_INC", scriptValue2);
        FILE_SCOPE = builder.build();
    }
}
