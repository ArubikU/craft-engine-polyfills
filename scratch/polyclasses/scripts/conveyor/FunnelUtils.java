/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v4
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyClassRedstone
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
import dev.arubik.craftengine.script.PolyClassBlock_v4;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyClassRedstone;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
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
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "mode";
        if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v4 polyClassBlock_v4 = new PolyClassBlock_v4(object);
            callSite = polyClassBlock_v4.tm$24_property(string);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("m", (ScriptValue)callSite2);
        return ScriptValue.of((ScriptFormula.valuesEqual((ScriptValue)callSite2, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqualStr((ScriptValue)callSite2, (String)"in") ^ true ? 1 : 0) != 0);
    }

    public static ScriptValue _funnelFacingVec(ScriptContext.Builder builder) {
        Object object;
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object2;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "facing";
        if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v4 polyClassBlock_v4 = new PolyClassBlock_v4(object2);
            callSite = polyClassBlock_v4.tm$24_property(string);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("f", (ScriptValue)callSite2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("DIR_VEC");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0));
            arrayList.add(ScriptValue.of((double)(-1.0)));
            object = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue3, (ScriptValue)callSite2, (ScriptValue)new ScriptValue.Array(arrayList), (ScriptContext)scriptContext);
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
                var11_10 = new PolyClassMachine(var10_9);
                v0 /* !! */  = var11_10.tm$62_belt_at(var6_5.asNum(), var7_6.asNum(), var8_7.asNum());
            } else {
                v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)var5_4, (ScriptValue)var6_5, (ScriptValue)var7_6, (ScriptValue)var8_7, (ScriptContext)var1_1);
            }
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var12_11 = v0 /* !! */ ;
        var0.val("bf", var12_11);
        if (ScriptFormula.valuesEqual((ScriptValue)var12_11, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
            var13_12 = var12_11 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)var12_11, (ScriptContext)var1_1) : ScriptValue.NULL;
            var0.val("best", var13_12);
        }
        if ((var14_13 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
            var15_14 = -var1_1.getNum("fx");
            var17_15 = -var1_1.getNum("fy");
            var19_16 = -var1_1.getNum("fz");
            if (var14_13 instanceof ScriptValue.Obj && (var22_18 = (var21_17 = (ScriptValue.Obj)var14_13).instance()) != null && !(var22_18 instanceof PolyClass) && var21_17.typeName().equals("Machine")) {
                var23_19 = new PolyClassMachine(var22_18);
                v1 /* !! */  = var23_19.tm$62_belt_at(var15_14, var17_15, var19_16);
            } else {
                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)var14_13, (ScriptValue)ScriptValue.of((double)var15_14), (ScriptValue)ScriptValue.of((double)var17_15), (ScriptValue)ScriptValue.of((double)var19_16), (ScriptContext)var1_1);
            }
        } else {
            v1 /* !! */  = ScriptValue.NULL;
        }
        var24_20 = v1 /* !! */ ;
        var0.val("bo", var24_20);
        if (!(ScriptFormula.valuesEqual((ScriptValue)var24_20, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) ** GOTO lbl-1000
        v2 /* !! */  = var24_20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)var24_20, (ScriptContext)var1_1) : ScriptValue.NULL;
        if (v2 /* !! */ .asNum() > var1_1.getNum("best")) {
            v3 = true;
        } else lbl-1000:
        // 2 sources

        {
            v3 = false;
        }
        if (v3) {
            var25_21 = var24_20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "speed", (ScriptValue)var24_20, (ScriptContext)var1_1) : ScriptValue.NULL;
            var0.val("best", var25_21);
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
            ScriptValue scriptValue2 = ScriptFormula.subscriptGet((ScriptValue)scriptContext.getClassOrVar("behind_vec"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0)));
            ScriptValue scriptValue3 = ScriptFormula.subscriptGet((ScriptValue)scriptContext.getClassOrVar("behind_vec"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 1.0)));
            ScriptValue scriptValue4 = ScriptFormula.subscriptGet((ScriptValue)scriptContext.getClassOrVar("behind_vec"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 2.0)));
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$17_container_at(scriptValue2.asNum(), scriptValue3.asNum(), scriptValue4.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
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
            ScriptValue scriptValue8 = scriptValue7 = ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "true")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "false"));
            if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine.tm$16_set_property(string, scriptValue7.asStr()));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
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
            ScriptValue.Obj obj;
            Object object3;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("fx");
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("fy");
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("fz");
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object3);
                object2 = polyClassMachine.tm$62_belt_at(scriptValue2.asNum(), scriptValue3.asNum(), scriptValue4.asNum());
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "belt_at", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            }
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
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("fx");
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("fy");
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("fz");
            if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                object = polyClassMachine.tm$17_container_at(scriptValue7.asNum(), scriptValue8.asNum(), scriptValue9.asNum());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = object;
        builder.val("target_c", scriptValue10);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue10, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            return scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue10, (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
        ScriptValue scriptValue11 = ScriptValue.of((double)d3);
        builder.val("force", scriptValue11);
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("item"));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fx") * d3)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fy") * d3)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fz") * d3)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fx") * 0.5)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fy") * 0.5)));
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("fz") * 0.5)));
            v3 = scriptValue12 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine(object5).um$19_drop_item_toward(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        return scriptContext.getClassOrVar("null");
    }

    public static ScriptValue _funnelTick(ScriptContext.Builder builder) {
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
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
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue10 = polyClassMachine != null ? polyClassMachine.pg$171_redstone() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "redstone", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        PolyClassRedstone polyClassRedstone = PolyClassRedstone.ofGuarded((ScriptValue)scriptValue10);
        if (polyClassRedstone != null ? polyClassRedstone.tg$19_powered() : PolyDispatch.bootstrapGet("memberGet", "powered", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object2;
                String string = "activated";
                String string2 = "false";
                if (scriptValue11 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine2 = new PolyClassMachine(object2);
                    v0 = ScriptValue.of((boolean)polyClassMachine2.tm$16_set_property(string, string2));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue12 = FunnelUtils._funnelModeOut(builder3);
        builder.val("out", scriptValue12);
        PolyClassMachine polyClassMachine3 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue13 = polyClassMachine3 != null ? polyClassMachine3.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        double d4 = 0.0;
        if (scriptValue13 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            PolyClassContainer polyClassContainer = new PolyClassContainer(object);
            callSite = polyClassContainer.tm$0_get_item(d4);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d4), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("held", (ScriptValue)callSite2);
        if (scriptValue12.asBool()) {
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool()) {
                Object object3;
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                if (scriptValue14 != ScriptValue.NULL) {
                    ScriptValue.Obj obj3;
                    Object object4;
                    double d5 = d;
                    double d6 = d2;
                    double d7 = d3;
                    if (scriptValue14 instanceof ScriptValue.Obj && (object4 = (obj3 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object4 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine4 = new PolyClassMachine(object4);
                        object3 = polyClassMachine4.tm$17_container_at(d5, d6, d7);
                    } else {
                        object3 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((double)d5), (ScriptValue)ScriptValue.of((double)d6), (ScriptValue)ScriptValue.of((double)d7), (ScriptContext)scriptContext);
                    }
                } else {
                    object3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue15 = object3;
                builder.val("c", scriptValue15);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue15, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue16 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("taken", scriptValue16);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue16, (ScriptContext)scriptContext).asBool() ^ true) {
                        ScriptValue.Obj obj4;
                        Object object5;
                        ScriptValue scriptValue17;
                        PolyClassMachine polyClassMachine5 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                        ScriptValue scriptValue18 = polyClassMachine5 != null ? polyClassMachine5.pg$120_container() : ((scriptValue17 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL);
                        double d8 = 0.0;
                        ScriptValue scriptValue19 = scriptValue16;
                        if (scriptValue18 instanceof ScriptValue.Obj && (object5 = (obj4 = (ScriptValue.Obj)scriptValue18).instance()) != null && !(object5 instanceof PolyClass) && obj4.typeName().equals("Container")) {
                            PolyClassContainer polyClassContainer = new PolyClassContainer(object5);
                            v3 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d8, scriptValue19));
                        } else {
                            v3 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((double)d8), (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
                        }
                        ScriptValue scriptValue20 = scriptValue16;
                        builder.val("held", scriptValue20);
                    }
                }
            }
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("held"), (ScriptContext)scriptContext).asBool() ^ true) {
                Object object6;
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("Machine");
                if (scriptValue21 != ScriptValue.NULL) {
                    ScriptValue.Obj obj5;
                    Object object7;
                    String string = "_funnel_progress";
                    String string3 = "float";
                    if (scriptValue21 instanceof ScriptValue.Obj && (object7 = (obj5 = (ScriptValue.Obj)scriptValue21).instance()) != null && !(object7 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine6 = new PolyClassMachine(object7);
                        object6 = polyClassMachine6.tm$34_get_typed(string, string3);
                    } else {
                        object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                    }
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue22 = object6;
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
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("Machine");
                    if (scriptValue26 != ScriptValue.NULL) {
                        ScriptValue.Obj obj6;
                        Object object8;
                        String string = "_funnel_progress";
                        String string4 = "float";
                        ScriptValue scriptValue27 = scriptValue25;
                        if (scriptValue26 instanceof ScriptValue.Obj && (object8 = (obj6 = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object8 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                            PolyClassMachine polyClassMachine7 = new PolyClassMachine(object8);
                            v6 = ScriptValue.of((boolean)polyClassMachine7.tm$82_set_typed(string, string4, scriptValue27));
                        } else {
                            v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue27, (ScriptContext)scriptContext);
                        }
                    } else {
                        v6 = ScriptValue.NULL;
                    }
                } else {
                    ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                    builder5.val("item", scriptContext.getClassOrVar("held"));
                    builder5.val("fx", scriptValue4);
                    builder5.val("fy", scriptValue5);
                    builder5.val("fz", scriptValue6);
                    ScriptValue scriptValue28 = FunnelUtils._funnelGiveForward(builder5);
                    builder.val("leftover", scriptValue28);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue28, (ScriptContext)scriptContext).asBool()) {
                        ScriptValue.Obj obj7;
                        Object object9;
                        ScriptValue scriptValue29;
                        PolyClassMachine polyClassMachine8 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                        ScriptValue scriptValue30 = polyClassMachine8 != null ? polyClassMachine8.pg$120_container() : ((scriptValue29 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue29, (ScriptContext)scriptContext) : ScriptValue.NULL);
                        double d10 = 0.0;
                        ScriptValue scriptValue31 = scriptContext.getClassOrVar("null");
                        if (scriptValue30 instanceof ScriptValue.Obj && (object9 = (obj7 = (ScriptValue.Obj)scriptValue30).instance()) != null && !(object9 instanceof PolyClass) && obj7.typeName().equals("Container")) {
                            PolyClassContainer polyClassContainer = new PolyClassContainer(object9);
                            v7 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d10, scriptValue31));
                        } else {
                            v7 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue30, (ScriptValue)ScriptValue.of((double)d10), (ScriptValue)scriptValue31, (ScriptContext)scriptContext);
                        }
                        ScriptValue scriptValue32 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue32 != ScriptValue.NULL) {
                            ScriptValue.Obj obj8;
                            Object object10;
                            String string = "_funnel_progress";
                            String string5 = "float";
                            ScriptValue scriptValue33 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0);
                            if (scriptValue32 instanceof ScriptValue.Obj && (object10 = (obj8 = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object10 instanceof PolyClass) && obj8.typeName().equals("Machine")) {
                                PolyClassMachine polyClassMachine9 = new PolyClassMachine(object10);
                                v8 = ScriptValue.of((boolean)polyClassMachine9.tm$82_set_typed(string, string5, scriptValue33));
                            } else {
                                v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue32, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptValue)scriptValue33, (ScriptContext)scriptContext);
                            }
                        } else {
                            v8 = ScriptValue.NULL;
                        }
                    }
                }
            }
        } else if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool() ^ true) {
            Object object11;
            ScriptValue scriptValue34 = scriptContext.getClassOrVar("Machine");
            if (scriptValue34 != ScriptValue.NULL) {
                ScriptValue.Obj obj9;
                Object object12;
                String string = "_funnel_progress";
                String string6 = "float";
                if (scriptValue34 instanceof ScriptValue.Obj && (object12 = (obj9 = (ScriptValue.Obj)scriptValue34).instance()) != null && !(object12 instanceof PolyClass) && obj9.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine10 = new PolyClassMachine(object12);
                    object11 = polyClassMachine10.tm$34_get_typed(string, string6);
                } else {
                    object11 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue34, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
                }
            } else {
                object11 = ScriptValue.NULL;
            }
            ScriptValue scriptValue35 = object11;
            builder.val("progress", scriptValue35);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue35, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                double d11 = 0.0;
                ScriptValue scriptValue36 = ScriptValue.of((double)0.0);
                builder.val("progress", scriptValue36);
            }
            ScriptValue scriptValue37 = scriptContext.getClassOrVar("progress");
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("fx", scriptValue4);
            builder6.val("fy", scriptValue5);
            builder6.val("fz", scriptValue6);
            ScriptValue scriptValue38 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue37, (ScriptValue)FunnelUtils._funnelInc(builder6));
            builder.val("progress", scriptValue38);
            if (scriptValue38.asNum() < 1.0) {
                ScriptValue scriptValue39 = scriptContext.getClassOrVar("Machine");
                if (scriptValue39 != ScriptValue.NULL) {
                    ScriptValue.Obj obj10;
                    Object object13;
                    String string = "_funnel_progress";
                    String string7 = "float";
                    ScriptValue scriptValue40 = scriptValue38;
                    if (scriptValue39 instanceof ScriptValue.Obj && (object13 = (obj10 = (ScriptValue.Obj)scriptValue39).instance()) != null && !(object13 instanceof PolyClass) && obj10.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine11 = new PolyClassMachine(object13);
                        v11 = ScriptValue.of((boolean)polyClassMachine11.tm$82_set_typed(string, string7, scriptValue40));
                    } else {
                        v11 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue39, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string7), (ScriptValue)scriptValue40, (ScriptContext)scriptContext);
                    }
                } else {
                    v11 = ScriptValue.NULL;
                }
            } else {
                Object object14;
                ScriptValue scriptValue41 = scriptContext.getClassOrVar("Machine");
                if (scriptValue41 != ScriptValue.NULL) {
                    ScriptValue.Obj obj11;
                    Object object15;
                    double d12 = d;
                    double d13 = d2;
                    double d14 = d3;
                    if (scriptValue41 instanceof ScriptValue.Obj && (object15 = (obj11 = (ScriptValue.Obj)scriptValue41).instance()) != null && !(object15 instanceof PolyClass) && obj11.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine12 = new PolyClassMachine(object15);
                        object14 = polyClassMachine12.tm$17_container_at(d12, d13, d14);
                    } else {
                        object14 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue41, (ScriptValue)ScriptValue.of((double)d12), (ScriptValue)ScriptValue.of((double)d13), (ScriptValue)ScriptValue.of((double)d14), (ScriptContext)scriptContext);
                    }
                } else {
                    object14 = ScriptValue.NULL;
                }
                ScriptValue scriptValue42 = object14;
                builder.val("c", scriptValue42);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue42, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    ScriptValue scriptValue43 = scriptValue42 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue42, (ScriptValue)callSite2, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("leftover", scriptValue43);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue43, (ScriptContext)scriptContext).asBool()) {
                        ScriptValue.Obj obj12;
                        Object object16;
                        ScriptValue scriptValue44;
                        PolyClassMachine polyClassMachine13 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                        ScriptValue scriptValue45 = polyClassMachine13 != null ? polyClassMachine13.pg$120_container() : ((scriptValue44 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue44, (ScriptContext)scriptContext) : ScriptValue.NULL);
                        double d15 = 0.0;
                        ScriptValue scriptValue46 = scriptContext.getClassOrVar("null");
                        if (scriptValue45 instanceof ScriptValue.Obj && (object16 = (obj12 = (ScriptValue.Obj)scriptValue45).instance()) != null && !(object16 instanceof PolyClass) && obj12.typeName().equals("Container")) {
                            PolyClassContainer polyClassContainer = new PolyClassContainer(object16);
                            v13 = ScriptValue.of((boolean)polyClassContainer.tm$10_set_item(d15, scriptValue46));
                        } else {
                            v13 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue45, (ScriptValue)ScriptValue.of((double)d15), (ScriptValue)scriptValue46, (ScriptContext)scriptContext);
                        }
                        ScriptValue scriptValue47 = scriptContext.getClassOrVar("Machine");
                        if (scriptValue47 != ScriptValue.NULL) {
                            ScriptValue.Obj obj13;
                            Object object17;
                            String string = "_funnel_progress";
                            String string8 = "float";
                            ScriptValue scriptValue48 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0);
                            if (scriptValue47 instanceof ScriptValue.Obj && (object17 = (obj13 = (ScriptValue.Obj)scriptValue47).instance()) != null && !(object17 instanceof PolyClass) && obj13.typeName().equals("Machine")) {
                                PolyClassMachine polyClassMachine14 = new PolyClassMachine(object17);
                                v14 = ScriptValue.of((boolean)polyClassMachine14.tm$82_set_typed(string, string8, scriptValue48));
                            } else {
                                v14 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue47, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string8), (ScriptValue)scriptValue48, (ScriptContext)scriptContext);
                            }
                        } else {
                            v14 = ScriptValue.NULL;
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
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "mode";
        if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v4 polyClassBlock_v4 = new PolyClassBlock_v4(object);
            callSite = polyClassBlock_v4.tm$24_property(string);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("cur", (ScriptValue)callSite2);
        ScriptValue scriptValue3 = ScriptFormula.valuesEqualStr((ScriptValue)callSite2, (String)"in") ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "out")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", FunnelUtils.class, "in"));
        builder.val("next", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object2;
            String string2 = "mode";
            ScriptValue scriptValue5 = scriptValue3;
            if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object2);
                v1 = ScriptValue.of((boolean)polyClassMachine2.tm$16_set_property(string2, scriptValue5.asStr()));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_property", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj3;
            Object object3;
            String string3 = "_funnel_progress";
            String string4 = "float";
            ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", FunnelUtils.class, 0.0);
            if (scriptValue6 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine3 = new PolyClassMachine(object3);
                v2 = ScriptValue.of((boolean)polyClassMachine3.tm$82_set_typed(string3, string4, scriptValue7));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj4;
            Object object4;
            ScriptValue scriptValue9 = ScriptValue.of((String)("<yellow>Funnel mode: <white>" + ScriptFormula.callBuiltin1((String)"upper", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asStr()));
            if (scriptValue8 instanceof ScriptValue.Obj && (object4 = (obj4 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object4 instanceof PolyClass) && obj4.typeName().equals("Player")) {
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object4);
                v3 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(scriptValue9.asStr()));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _funnelDropHeld(ScriptContext.Builder builder) {
        block4: {
            CallSite callSite;
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue;
            ScriptContext scriptContext = builder.peek();
            PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
            ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$120_container() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
            double d = 0.0;
            if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
                PolyClassContainer polyClassContainer = new PolyClassContainer(object);
                callSite = polyClassContainer.tm$0_get_item(d);
            } else {
                callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
            CallSite callSite2 = callSite;
            builder.val("held", (ScriptValue)callSite2);
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)callSite2, (ScriptContext)scriptContext).asBool() ^ true)) break block4;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object2;
                ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
                arrayList.add(callSite2);
                v1 = scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine(object2).um$4_drop_item(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
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
