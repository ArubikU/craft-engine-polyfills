/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyClassRecipeRegistry
 *  dev.arubik.craftengine.script.PolyClassRegistry
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.BeltUtils
 *  dev.arubik.craftengine.script.gen.TreeUtils
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyClassRecipeRegistry;
import dev.arubik.craftengine.script.PolyClassRegistry;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.BeltUtils;
import dev.arubik.craftengine.script.gen.TreeUtils;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Saw {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue _sawSuCost(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = scriptContext.getNum("BASE_RPM");
        return ScriptValue.of((double)(d == 0.0 ? 0.0 : scriptContext.getNum("BASE_SU") * Math.abs(scriptContext.getNum("current_rpm")) / d));
    }

    public static ScriptValue _sawRecipeCount(ScriptContext.Builder builder) {
        List list;
        PolyClassMachine_v2 polyClassMachine_v2;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("count", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        List list2 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v2.tl$147_recipes() : ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext))) : (list = ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                builder.val("r", scriptValue3);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("r");
                ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("ins", scriptValue5);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue5);
                if (!(ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0)));
                builder.val("count", scriptValue6);
            }
        }
        return scriptContext.getClassOrVar("count");
    }

    public static ScriptValue _sawRecipeAt(ScriptContext.Builder builder) {
        List list;
        PolyClassMachine_v2 polyClassMachine_v2;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        List list2 = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v2.tl$147_recipes() : ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext))) : (list = ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                builder.val("r", scriptValue3);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("r");
                ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("ins", scriptValue5);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue5);
                if (!(ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)scriptContext.getClassOrVar("target_idx"))) {
                    return scriptContext.getClassOrVar("r");
                }
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0)));
                builder.val("idx", scriptValue6);
            }
        }
        return scriptContext.getClassOrVar("null");
    }

    /*
     * Unable to fully structure code
     */
    public static ScriptValue _sawRecipeFor(ScriptContext.Builder var0) {
        block5: {
            var1_1 = var0.peek();
            var5_2 = var1_1.getClassOrVar("Machine");
            v0 = var5_2 != ScriptValue.NULL ? ((var6_3 = PolyClassMachine_v2.ofGuarded((ScriptValue)var5_2)) != null ? var6_3.tl$147_recipes() : ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)var5_2, (ScriptContext)var1_1))) : (var2_4 = ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
            if (var2_4 == null) break block5;
            for (ScriptValue var4_6 : var2_4) {
                var0.val("r", var4_6);
                var7_7 = var1_1.getClassOrVar("r");
                var8_8 = var7_7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var7_7, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("ins", var8_8);
                var9_9 = var1_1.getClassOrVar("r");
                var10_10 = var9_9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)var9_9, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("outs", var10_10);
                var11_11 = new ArrayList<ScriptValue>();
                var11_11.add(var8_8);
                if (!(ScriptFormula.callBuiltin((String)"size", var11_11, (ScriptContext)var1_1).asNum() > 0.0 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var8_8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("item_id")) != false)) ** GOTO lbl-1000
                var12_12 = new ArrayList<ScriptValue>();
                var12_12.add(var10_10);
                if (ScriptFormula.callBuiltin((String)"size", var12_12, (ScriptContext)var1_1).asNum() > 0.0) {
                    v1 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v1 = false;
                }
                if (!(v1 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var10_10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("out_id")) != false)) continue;
                return var1_1.getClassOrVar("r");
            }
        }
        var16_15 = (var17_13 = var1_1.getClassOrVar("Registry")) != ScriptValue.NULL ? ((var18_14 = PolyClassRegistry.ofGuarded((ScriptValue)var17_13)) != null ? var18_14.pg$0_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)var17_13, (ScriptContext)var1_1)) : ScriptValue.NULL;
        var19_16 = PolyClassRecipeRegistry.ofGuarded((ScriptValue)var16_15);
        var13_17 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)(var19_16 != null ? var19_16.pg$2_stonecutter() : PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)var16_15, (ScriptContext)var1_1)), (ScriptValue)var1_1.getClassOrVar("item_id"), (ScriptContext)var1_1));
        if (var13_17 != null) {
            for (ScriptValue var15_19 : var13_17) {
                var0.val("r", var15_19);
                var20_20 = var1_1.getClassOrVar("r");
                var21_21 = var20_20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)var20_20, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("outs", var21_21);
                var22_22 = new ArrayList<ScriptValue>();
                var22_22.add(var21_21);
                if (!(ScriptFormula.callBuiltin((String)"size", var22_22, (ScriptContext)var1_1).asNum() > 0.0 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var21_21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("out_id")) != false)) continue;
                return var1_1.getClassOrVar("r");
            }
        }
        return var1_1.getClassOrVar("null");
    }

    public static ScriptValue _sawOutputFor(ScriptContext.Builder builder) {
        List list;
        PolyClassRegistry polyClassRegistry;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "_saw_filter";
            String string2 = "str";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("filter_id", scriptValue2);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
            builder.val("filter_id", scriptValue3);
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("item_id", scriptContext.getClassOrVar("item_id"));
        ScriptValue scriptValue4 = Saw._sawRecipeCount(builder2);
        builder.val("count", scriptValue4);
        if (scriptValue4.asNum() > 0.0) {
            double d;
            Object object3;
            ScriptValue scriptValue5;
            if (scriptContext.getStr("filter_id").equals("") ^ true) {
                List list2;
                PolyClassMachine_v2 polyClassMachine_v2;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                List list3 = scriptValue6 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine_v2.tl$147_recipes() : ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue6, (ScriptContext)scriptContext))) : (list2 = ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
                if (list2 != null) {
                    for (ScriptValue scriptValue7 : list2) {
                        builder.val("r", scriptValue7);
                        ScriptValue scriptValue8 = scriptContext.getClassOrVar("r");
                        ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("ins", scriptValue9);
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue9);
                        if (!(ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                        ScriptValue scriptValue10 = scriptContext.getClassOrVar("r");
                        ScriptValue scriptValue11 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("outs", scriptValue11);
                        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                        arrayList2.add(scriptValue11);
                        if (!(ScriptFormula.callBuiltin((String)"size", arrayList2, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
                        return scriptContext.getClassOrVar("filter_id");
                    }
                }
            }
            if ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "_saw_recipe_index";
                String string3 = "int";
                if (scriptValue5 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object4);
                    object3 = polyClassMachine_v2.tm$34_get_typed(string, string3);
                } else {
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                }
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = object3;
            builder.val("idx", scriptValue12);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue12, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                double d2 = 0.0;
                ScriptValue scriptValue13 = ScriptValue.of((double)0.0);
                builder.val("idx", scriptValue13);
            }
            double d3 = (d = scriptValue4.asNum()) == 0.0 ? 0.0 : scriptContext.getNum("idx") % d;
            ScriptValue scriptValue14 = ScriptValue.of((double)d3);
            builder.val("idx", scriptValue14);
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
            if (scriptValue15 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                String string = "_saw_recipe_index";
                String string4 = "int";
                double d4 = scriptValue4.asNum();
                ScriptValue scriptValue16 = ScriptValue.of((double)(d4 == 0.0 ? 0.0 : (d3 + 1.0) % d4));
                if (scriptValue15 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object5);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string4, scriptValue16));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue16, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("item_id", scriptContext.getClassOrVar("item_id"));
            builder3.val("target_idx", ScriptValue.of((double)d3));
            ScriptValue scriptValue17 = Saw._sawRecipeAt(builder3);
            builder.val("picked", scriptValue17);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue17, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("picked");
                ScriptValue scriptValue19 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("outs", scriptValue19);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue19);
                if (ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0) {
                    return PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext);
                }
            }
            return scriptContext.getClassOrVar("null");
        }
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("Registry");
        ScriptValue scriptValue21 = scriptValue20 != ScriptValue.NULL ? ((polyClassRegistry = PolyClassRegistry.ofGuarded((ScriptValue)scriptValue20)) != null ? polyClassRegistry.pg$0_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue20, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        PolyClassRecipeRegistry polyClassRecipeRegistry = PolyClassRecipeRegistry.ofGuarded((ScriptValue)scriptValue21);
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)(polyClassRecipeRegistry != null ? polyClassRecipeRegistry.pg$2_stonecutter() : PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)), (ScriptValue)scriptContext.getClassOrVar("item_id"), (ScriptContext)scriptContext);
        builder.val("stone_candidates", (ScriptValue)callSite);
        if (scriptContext.getStr("filter_id").equals("") ^ true && (list = ScriptProgram.elementsOf((ScriptValue)callSite)) != null) {
            for (ScriptValue scriptValue22 : list) {
                builder.val("r", scriptValue22);
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("r");
                ScriptValue scriptValue24 = scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("outs", scriptValue24);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue24);
                if (!(ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue24, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
                return scriptContext.getClassOrVar("filter_id");
            }
        }
        ArrayList<CallSite> arrayList = new ArrayList<CallSite>();
        arrayList.add(callSite);
        ScriptValue scriptValue25 = ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext);
        builder.val("stone_count", scriptValue25);
        if (scriptValue25.asNum() > 0.0) {
            double d;
            Object object6;
            ScriptValue scriptValue26 = scriptContext.getClassOrVar("Machine");
            if (scriptValue26 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "_saw_recipe_index";
                String string5 = "int";
                if (scriptValue26 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object7);
                    object6 = polyClassMachine_v2.tm$34_get_typed(string, string5);
                } else {
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
                }
            } else {
                object6 = ScriptValue.NULL;
            }
            ScriptValue scriptValue27 = object6;
            builder.val("idx", scriptValue27);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue27, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                double d5 = 0.0;
                ScriptValue scriptValue28 = ScriptValue.of((double)0.0);
                builder.val("idx", scriptValue28);
            }
            double d6 = (d = scriptValue25.asNum()) == 0.0 ? 0.0 : scriptContext.getNum("idx") % d;
            ScriptValue scriptValue29 = ScriptValue.of((double)d6);
            builder.val("idx", scriptValue29);
            ScriptValue scriptValue30 = scriptContext.getClassOrVar("Machine");
            if (scriptValue30 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                String string = "_saw_recipe_index";
                String string6 = "int";
                double d7 = scriptValue25.asNum();
                ScriptValue scriptValue31 = ScriptValue.of((double)(d7 == 0.0 ? 0.0 : (d6 + 1.0) % d7));
                if (scriptValue30 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue30).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object8);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string6, scriptValue31));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue30, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue31, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            CallSite callSite2 = PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)callSite, (ScriptValue)ScriptValue.of((double)d6)), (ScriptContext)scriptContext);
            builder.val("outs", (ScriptValue)callSite2);
            ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
            arrayList3.add(callSite2);
            if (ScriptFormula.callBuiltin((String)"size", arrayList3, (ScriptContext)scriptContext).asNum() > 0.0) {
                return PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)callSite2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext);
            }
        }
        return scriptContext.getClassOrVar("null");
    }

    public static ScriptValue onRightClick(ScriptContext.Builder builder) {
        PolyClassPlayer_v2 polyClassPlayer_v2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer_v2.pg$48_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("held", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "_saw_filter";
                String string2 = "str";
                ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string2, scriptValue4));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ScriptValue scriptValue6;
                String string = "_saw_filter";
                String string3 = "str";
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("held");
                Object object2 = scriptValue6 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string3, scriptValue6));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _sawOutputFace(ScriptContext.Builder builder) {
        Object object;
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object2;
        PolyClassMachine_v2 polyClassMachine_v2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v2.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        String string = "facing";
        if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v2 polyClassBlock_v2 = new PolyClassBlock_v2(object2);
            callSite = polyClassBlock_v2.tm$24_property(string);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        }
        CallSite callSite2 = callSite;
        builder.val("facing", (ScriptValue)callSite2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object3;
            String string2 = "_saw_rpm_sign";
            String string3 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v2 polyClassMachine_v22 = new PolyClassMachine_v2(object3);
                object = polyClassMachine_v22.tm$34_get_typed(string2, string3);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object;
        builder.val("sign", scriptValue4);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0)))) {
            double d = 1.0;
            ScriptValue scriptValue5 = ScriptValue.of((double)1.0);
            builder.val("sign", scriptValue5);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)callSite2, (String)"north") || ScriptFormula.valuesEqualStr((ScriptValue)callSite2, (String)"south")) {
            return scriptContext.getNum("sign") > 0.0 ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "east")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "west"));
        }
        return scriptContext.getNum("sign") > 0.0 ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "south")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "north"));
    }

    public static ScriptValue _sawInputFace(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Saw._sawOutputFace(builder2);
        builder.val("face", scriptValue);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"east")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "west");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"west")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "east");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"north")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "south");
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "north");
    }

    public static ScriptValue _sawUpdateRpmSign(ScriptContext.Builder builder) {
        block4: {
            ScriptContext scriptContext = builder.peek();
            if (!(ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("effective_rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true)) break block4;
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "_saw_rpm_sign";
                String string2 = "int";
                ScriptValue scriptValue2 = ScriptValue.of((double)(scriptContext.getNum("effective_rpm") < 0.0 ? -1.0 : 1.0));
                if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string2, scriptValue2));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _sawSetupRpm(ScriptContext.Builder builder) {
        CallSite callSite;
        ScriptValue.Obj obj;
        Object object;
        PolyClassMachine_v2 polyClassMachine_v2;
        CallSite callSite2;
        ScriptValue.Obj obj2;
        Object object2;
        PolyClassMachine_v2 polyClassMachine_v22;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v22 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v22.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        String string = "face";
        if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Block")) {
            PolyClassBlock_v2 polyClassBlock_v2 = new PolyClassBlock_v2(object2);
            callSite2 = polyClassBlock_v2.tm$24_property(string);
        } else {
            callSite2 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        }
        CallSite callSite3 = callSite2;
        builder.val("face", (ScriptValue)callSite3);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v2.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        String string2 = "facing";
        if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v2 polyClassBlock_v2 = new PolyClassBlock_v2(object);
            callSite = polyClassBlock_v2.tm$24_property(string2);
        } else {
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        }
        CallSite callSite4 = callSite;
        builder.val("facing", (ScriptValue)callSite4);
        if (ScriptFormula.valuesEqualStr((ScriptValue)callSite3, (String)"wall")) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj3;
                Object object3;
                String string3 = "back";
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v23 = new PolyClassMachine_v2(object3);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v23.tm$40_set_rpm_input(string3));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj4;
                Object object4;
                String string4 = "";
                if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj4 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v24 = new PolyClassMachine_v2(object4);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v24.tm$44_set_rpm_output_same(string4));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)callSite4, (String)"north") || ScriptFormula.valuesEqualStr((ScriptValue)callSite4, (String)"south")) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj5;
                Object object5;
                String string5 = "north,south";
                if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj5 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v25 = new PolyClassMachine_v2(object5);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v25.tm$40_set_rpm_input(string5));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj6;
                Object object6;
                String string6 = "north,south";
                if (scriptValue8 instanceof ScriptValue.Obj && (object6 = (obj6 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object6 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v26 = new PolyClassMachine_v2(object6);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v26.tm$44_set_rpm_output_same(string6));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj7;
                Object object7;
                String string7 = "east,west";
                if (scriptValue9 instanceof ScriptValue.Obj && (object7 = (obj7 = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object7 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v27 = new PolyClassMachine_v2(object7);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v27.tm$40_set_rpm_input(string7));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string7), (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue.Obj obj8;
                Object object8;
                String string8 = "east,west";
                if (scriptValue10 instanceof ScriptValue.Obj && (object8 = (obj8 = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object8 instanceof PolyClass) && obj8.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v28 = new PolyClassMachine_v2(object8);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v28.tm$44_set_rpm_output_same(string8));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string8), (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _sawDepositDirectional(ScriptContext.Builder builder) {
        block5: {
            ScriptContext scriptContext;
            block4: {
                PolyClassMachine_v2 polyClassMachine_v2;
                scriptContext = builder.peek();
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("item"));
                if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
                Object object = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v2 = PolyClassMachine_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v2.pg$191_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                if (!(ScriptFormula.valuesEqual((ScriptValue)object, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) break block4;
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(scriptContext);
                builder2.val("item", scriptContext.getClassOrVar("item"));
                Utils.1._deposit((ScriptContext.Builder)builder2);
                break block5;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue = Saw._sawOutputFace(builder3);
            builder.val("face", scriptValue);
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(scriptContext);
            builder4.val("face", scriptValue);
            ScriptValue scriptValue2 = BeltUtils._faceOffset((ScriptContext.Builder)builder4);
            builder.val("off", scriptValue2);
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(scriptContext);
            builder5.val("face", scriptValue);
            builder5.val("item", scriptContext.getClassOrVar("item"));
            ScriptValue scriptValue3 = BeltUtils.beltGive((ScriptContext.Builder)builder5);
            builder.val("remaining", scriptValue3);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue3);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(scriptContext);
                builder6.val("face", scriptValue);
                builder6.val("item", scriptContext.getClassOrVar("remaining"));
                ScriptValue scriptValue4 = BeltUtils.depotGive((ScriptContext.Builder)builder6);
                builder.val("remaining", scriptValue4);
            }
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptContext.getClassOrVar("remaining"));
            if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool() ^ true)) break block5;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptContext.getClassOrVar("remaining"));
                arrayList3.add(ScriptValue.of((double)(ScriptFormula.subscriptGet((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))).asNum() * 0.08)));
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.05));
                arrayList3.add(ScriptValue.of((double)(ScriptFormula.subscriptGet((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0))).asNum() * 0.08)));
                v1 = scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).um$19_drop_item_toward(arrayList3) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
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
    public static void run(ScriptContext.Builder var0) {
        block167: {
            block165: {
                block166: {
                    var1_1 = var0.peek();
                    v0 = new ArrayList<String>();
                    v0.add("is_resting_item");
                    ScriptProgram.applyImport((ScriptContext.Builder)var0, (String)"utils.pf", null, v0);
                    v1 = new ArrayList<String>();
                    v1.add("_deposit");
                    v1.add("_linear_break_speed");
                    v1.add("_update_activated");
                    ScriptProgram.applyImport((ScriptContext.Builder)var0, (String)"kinetics/utils.pf", null, v1);
                    v2 = new ArrayList<String>();
                    v2.add("_is_log");
                    v2.add("_is_leaf_id");
                    v2.add("_is_choppable");
                    v2.add("_get_tree_block");
                    v2.add("_find_tree");
                    v2.add("_fell_tree");
                    ScriptProgram.applyImport((ScriptContext.Builder)var0, (String)"kinetics/tree_utils.pf", null, v2);
                    v3 = new ArrayList<String>();
                    v3.add("_face_offset");
                    v3.add("belt_take");
                    v3.add("belt_give");
                    v3.add("depot_give");
                    v3.add("_item_matches");
                    ScriptProgram.applyImport((ScriptContext.Builder)var0, (String)"kinetics/belt_utils.pf", null, v3);
                    var2_2 = 32.0;
                    var4_3 = ScriptValue.of((double)32.0);
                    var0.val("BASE_RPM", var4_3);
                    var5_4 = 8.0;
                    var7_5 = ScriptValue.of((double)8.0);
                    var0.val("BASE_SU", var7_5);
                    var8_6 = 10.0;
                    var10_7 = ScriptValue.of((double)10.0);
                    var0.val("PROCESS_TICKS", var10_7);
                    var11_8 = var8_6 * var2_2;
                    var13_9 = ScriptValue.of((double)var11_8);
                    var0.val("BASE_PROCESS_WORK", var13_9);
                    var14_10 = var1_1.getClassOrVar("Machine");
                    var16_12 = var1_1.getClassOrVar("Machine");
                    var18_14 = var1_1.getClassOrVar("Machine");
                    var20_16 = ScriptValue.of((String)((var14_10 != ScriptValue.NULL ? ((var15_11 = PolyClassMachine_v2.ofGuarded((ScriptValue)var14_10)) != null ? var15_11.pg$200_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var14_10, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "," + (var16_12 != ScriptValue.NULL ? ((var17_13 = PolyClassMachine_v2.ofGuarded((ScriptValue)var16_12)) != null ? var17_13.pg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var16_12, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "," + (var18_14 != ScriptValue.NULL ? ((var19_15 = PolyClassMachine_v2.ofGuarded((ScriptValue)var18_14)) != null ? var19_15.pg$206_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var18_14, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr()));
                    var0.val("_hold_key", var20_16);
                    var21_17 = var1_1.getClassOrVar("Machine");
                    var23_19 = var21_17 != ScriptValue.NULL ? ((var22_18 = PolyClassMachine_v2.ofGuarded((ScriptValue)var21_17)) != null ? var22_18.pg$191_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var21_17, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var0.val("contraption", var23_19);
                    var24_20 = ScriptContext.builder().copyFrom(var1_1);
                    var24_20.val("effective_rpm", (ScriptValue)(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var25_21 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var25_21, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm")));
                    Saw._sawUpdateRpmSign(var24_20);
                    var26_22 = 0.0;
                    var28_23 = ScriptValue.of((double)0.0);
                    var0.val("is_now", var28_23);
                    var30_24 = var1_1.getClassOrVar("Machine");
                    var29_26 = var30_24 != ScriptValue.NULL ? ((var31_25 = PolyClassMachine_v2.ofGuarded((ScriptValue)var30_24)) != null ? var31_25.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)var30_24, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var32_27 = "face";
                    if (var29_26 instanceof ScriptValue.Obj && (var34_29 = (var33_28 = (ScriptValue.Obj)var29_26).instance()) != null && !(var34_29 instanceof PolyClass) && var33_28.typeName().equals("Block")) {
                        var35_30 = new PolyClassBlock_v2(var34_29);
                        v4 = var35_30.tm$24_property(var32_27);
                    } else {
                        v4 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var29_26, (ScriptValue)ScriptValue.of((String)var32_27), (ScriptContext)var1_1);
                    }
                    var36_31 = ScriptFormula.valuesEqualStr((ScriptValue)v4, (String)"floor");
                    var37_32 = ScriptValue.of((boolean)var36_31);
                    var0.val("is_belt_facing", var37_32);
                    if (!(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block165;
                    var38_33 = var1_1.getClassOrVar("contraption");
                    var39_34 = ScriptFormula.valuesEqual((ScriptValue)(var38_33 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var38_33, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true;
                    var40_35 = ScriptValue.of((boolean)var39_34);
                    var0.val("is_rotational", var40_35);
                    var41_36 = var1_1.getClassOrVar("contraption");
                    var42_37 = var41_36 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)var41_36, (ScriptContext)var1_1) : ScriptValue.NULL;
                    var0.val("is_linear", var42_37);
                    var43_38 = (var39_34 != false || var42_37.asBool() != false) != false ? 1.0 : 0.0;
                    var45_39 = ScriptValue.of((double)var43_38);
                    var0.val("is_now", var45_39);
                    if (!(var43_38 > 0.0)) break block166;
                    if (var39_34) {
                        var46_40 = 10.0;
                        v5 = ScriptValue.of((double)Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(((var48_41 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var48_41, (ScriptContext)var1_1) : ScriptValue.NULL).asNum()) / var46_40));
                    } else {
                        var49_42 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                        var49_42.val("contraption", var23_19);
                        v5 = Utils.1._linearBreakSpeed((ScriptContext.Builder)var49_42);
                    }
                    var50_43 = v5;
                    var0.val("speed", var50_43);
                    var51_44 = var1_1.getClassOrVar("Machine");
                    var53_46 = var1_1.getClassOrVar("Machine");
                    var55_48 = ScriptFormula.addPolymorphic((ScriptValue)(var51_44 != ScriptValue.NULL ? ((var52_45 = PolyClassMachine_v2.ofGuarded((ScriptValue)var51_44)) != null ? var52_45.pg$200_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var51_44, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var53_46 != ScriptValue.NULL ? ((var54_47 = PolyClassMachine_v2.ofGuarded((ScriptValue)var53_46)) != null ? var54_47.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var53_46, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("tx", var55_48);
                    var56_49 = var1_1.getClassOrVar("Machine");
                    var58_51 = var1_1.getClassOrVar("Machine");
                    var60_53 = ScriptFormula.addPolymorphic((ScriptValue)(var56_49 != ScriptValue.NULL ? ((var57_50 = PolyClassMachine_v2.ofGuarded((ScriptValue)var56_49)) != null ? var57_50.pg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var56_49, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var58_51 != ScriptValue.NULL ? ((var59_52 = PolyClassMachine_v2.ofGuarded((ScriptValue)var58_51)) != null ? var59_52.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var58_51, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("ty", var60_53);
                    var61_54 = var1_1.getClassOrVar("Machine");
                    var63_56 = var1_1.getClassOrVar("Machine");
                    var65_58 = ScriptFormula.addPolymorphic((ScriptValue)(var61_54 != ScriptValue.NULL ? ((var62_55 = PolyClassMachine_v2.ofGuarded((ScriptValue)var61_54)) != null ? var62_55.pg$206_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var61_54, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var63_56 != ScriptValue.NULL ? ((var64_57 = PolyClassMachine_v2.ofGuarded((ScriptValue)var63_56)) != null ? var64_57.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var63_56, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("tz", var65_58);
                    var66_59 = var1_1.getClassOrVar("contraption");
                    var67_60 = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(var66_59 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)var66_59, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var55_48, (ScriptValue)var60_53, (ScriptValue)var65_58, (ScriptContext)var1_1);
                    var0.val("target", (ScriptValue)var67_60);
                    if ((var36_31 != false || ScriptFormula.valuesEqual((ScriptValue)var67_60, (ScriptValue)var1_1.getClassOrVar("null")) != false) != false || ((var68_61 = var1_1.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var68_61, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
                    var69_62 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var70_63 = var1_1.getClassOrVar("target");
                    var69_62.val("id", (ScriptValue)(var70_63 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var70_63, (ScriptContext)var1_1) : ScriptValue.NULL));
                    if (!(TreeUtils._isChoppable((ScriptContext.Builder)var69_62).asBool() ^ true)) {
                        v6 = false;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v6 = true;
                    }
                    if (v6) {
                        var71_64 = var1_1.getClassOrVar("contraption");
                        v7 /* !! */  = var71_64 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var71_64, (ScriptValue)var20_16, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var72_65 = var1_1.getClassOrVar("contraption");
                        v8 /* !! */  = var72_65 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var72_65, (ScriptValue)var20_16, (ScriptContext)var1_1) : ScriptValue.NULL;
                        var73_66 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                        var74_67 = var1_1.getClassOrVar("target");
                        var73_66.val("id", (ScriptValue)(var74_67 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var74_67, (ScriptContext)var1_1) : ScriptValue.NULL));
                        if (TreeUtils._isLog((ScriptContext.Builder)var73_66).asBool()) {
                            var75_68 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                            var75_68.val("contraption", var23_19);
                            var75_68.val("base_x", var55_48);
                            var75_68.val("base_y", var60_53);
                            var75_68.val("base_z", var65_58);
                            var75_68.val("speed", var50_43);
                            TreeUtils._fellTree((ScriptContext.Builder)var75_68);
                        } else {
                            var76_69 = var1_1.getClassOrVar("Machine");
                            if (var76_69 != ScriptValue.NULL) {
                                var77_70 = var67_60;
                                var78_71 = var50_43;
                                if (var76_69 instanceof ScriptValue.Obj && (var80_73 = (var79_72 = (ScriptValue.Obj)var76_69).instance()) != null && !(var80_73 instanceof PolyClass) && var79_72.typeName().equals("Machine")) {
                                    var81_74 = new PolyClassMachine_v2(var80_73);
                                    v9 /* !! */  = var81_74.tm$2_tick_break((ScriptValue)var77_70, var78_71.asNum());
                                } else {
                                    v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var76_69, (ScriptValue)var77_70, (ScriptValue)var78_71, (ScriptContext)var1_1);
                                }
                            } else {
                                v9 /* !! */  = ScriptValue.NULL;
                            }
                            var82_75 = v9 /* !! */ ;
                            var0.val("result", var82_75);
                            if (ScriptFormula.valuesEqual((ScriptValue)var82_75, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var83_76 = ScriptProgram.elementsOf((ScriptValue)var82_75)) != null) {
                                for (ScriptValue var85_78 : var83_76) {
                                    var0.val("item", var85_78);
                                    var86_79 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                                    var86_79.val("item", var1_1.getClassOrVar("item"));
                                    Utils.1._deposit((ScriptContext.Builder)var86_79);
                                }
                            }
                        }
                        var87_80 = var1_1.getClassOrVar("Machine");
                        if (var87_80 != ScriptValue.NULL) {
                            if (var39_34) {
                                var89_81 = ScriptContext.builder().copyFrom(var1_1);
                                var90_82 = var1_1.getClassOrVar("contraption");
                                var89_81.val("current_rpm", (ScriptValue)(var90_82 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var90_82, (ScriptContext)var1_1) : ScriptValue.NULL));
                                v10 = Saw._sawSuCost(var89_81);
                            } else {
                                v10 = var88_83 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 6.0)), (ScriptValue)var50_43);
                            }
                            if (var87_80 instanceof ScriptValue.Obj && (var92_85 = (var91_84 = (ScriptValue.Obj)var87_80).instance()) != null && !(var92_85 instanceof PolyClass) && var91_84.typeName().equals("Machine")) {
                                var93_86 = new PolyClassMachine_v2(var92_85);
                                v11 /* !! */  = ScriptValue.of((boolean)var93_86.tm$56_report_su(var88_83.asNum()));
                            } else {
                                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var87_80, (ScriptValue)var88_83, (ScriptContext)var1_1);
                            }
                        } else {
                            v11 /* !! */  = ScriptValue.NULL;
                        }
                    }
                    break block167;
                }
                var94_87 = var1_1.getClassOrVar("contraption");
                v12 /* !! */  = var94_87 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var94_87, (ScriptValue)var20_16, (ScriptContext)var1_1) : ScriptValue.NULL;
                break block167;
            }
            if (!(ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true)) break block167;
            var95_88 = 10.0;
            var97_89 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(var1_1.getNum("rpm")) / var95_88);
            var99_90 = ScriptValue.of((double)var97_89);
            var0.val("speed", var99_90);
            var100_91 = var1_1.getClassOrVar("Machine");
            var102_93 = var100_91 != ScriptValue.NULL ? ((var101_92 = PolyClassMachine_v2.ofGuarded((ScriptValue)var100_91)) != null ? var101_92.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var100_91, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("fb", var102_93);
            if (!((var36_31 ^ true) != false && (((var103_94 = var1_1.getClassOrVar("fb")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var103_94, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) ** GOTO lbl-1000
            var104_95 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
            var105_96 = var1_1.getClassOrVar("fb");
            var104_95.val("id", (ScriptValue)(var105_96 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var105_96, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (TreeUtils._isChoppable((ScriptContext.Builder)var104_95).asBool()) {
                v13 = true;
            } else lbl-1000:
            // 2 sources

            {
                v13 = false;
            }
            if (v13) {
                var106_97 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                var107_98 = var1_1.getClassOrVar("fb");
                var106_97.val("id", (ScriptValue)(var107_98 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var107_98, (ScriptContext)var1_1) : ScriptValue.NULL));
                if (TreeUtils._isLog((ScriptContext.Builder)var106_97).asBool()) {
                    var108_99 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var108_99.val("contraption", var1_1.getClassOrVar("null"));
                    var109_100 = var1_1.getClassOrVar("Machine");
                    var111_102 = var1_1.getClassOrVar("Machine");
                    var108_99.val("base_x", ScriptFormula.addPolymorphic((ScriptValue)(var109_100 != ScriptValue.NULL ? ((var110_101 = PolyClassMachine_v2.ofGuarded((ScriptValue)var109_100)) != null ? var110_101.pg$200_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var109_100, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var111_102 != ScriptValue.NULL ? ((var112_103 = PolyClassMachine_v2.ofGuarded((ScriptValue)var111_102)) != null ? var112_103.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var111_102, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var113_104 = var1_1.getClassOrVar("Machine");
                    var115_106 = var1_1.getClassOrVar("Machine");
                    var108_99.val("base_y", ScriptFormula.addPolymorphic((ScriptValue)(var113_104 != ScriptValue.NULL ? ((var114_105 = PolyClassMachine_v2.ofGuarded((ScriptValue)var113_104)) != null ? var114_105.pg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var113_104, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var115_106 != ScriptValue.NULL ? ((var116_107 = PolyClassMachine_v2.ofGuarded((ScriptValue)var115_106)) != null ? var116_107.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var115_106, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var117_108 = var1_1.getClassOrVar("Machine");
                    var119_110 = var1_1.getClassOrVar("Machine");
                    var108_99.val("base_z", ScriptFormula.addPolymorphic((ScriptValue)(var117_108 != ScriptValue.NULL ? ((var118_109 = PolyClassMachine_v2.ofGuarded((ScriptValue)var117_108)) != null ? var118_109.pg$206_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var117_108, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var119_110 != ScriptValue.NULL ? ((var120_111 = PolyClassMachine_v2.ofGuarded((ScriptValue)var119_110)) != null ? var120_111.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var119_110, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var108_99.val("speed", ScriptValue.of((double)var97_89));
                    TreeUtils._fellTree((ScriptContext.Builder)var108_99);
                } else {
                    var121_112 = var1_1.getClassOrVar("Machine");
                    if (var121_112 != ScriptValue.NULL) {
                        var122_113 = var102_93;
                        var123_114 = var97_89;
                        if (var121_112 instanceof ScriptValue.Obj && (var126_116 = (var125_115 = (ScriptValue.Obj)var121_112).instance()) != null && !(var126_116 instanceof PolyClass) && var125_115.typeName().equals("Machine")) {
                            var127_117 = new PolyClassMachine_v2(var126_116);
                            v14 /* !! */  = var127_117.tm$2_tick_break(var122_113, var123_114);
                        } else {
                            v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var121_112, (ScriptValue)var122_113, (ScriptValue)ScriptValue.of((double)var123_114), (ScriptContext)var1_1);
                        }
                    } else {
                        v14 /* !! */  = ScriptValue.NULL;
                    }
                    var128_118 = v14 /* !! */ ;
                    var0.val("result", var128_118);
                    if (ScriptFormula.valuesEqual((ScriptValue)var128_118, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var129_119 = ScriptProgram.elementsOf((ScriptValue)var128_118)) != null) {
                        for (ScriptValue var131_121 : var129_119) {
                            var0.val("item", var131_121);
                            var132_122 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var132_122.val("item", var1_1.getClassOrVar("item"));
                            Utils.1._deposit((ScriptContext.Builder)var132_122);
                        }
                    }
                }
                var133_123 = var1_1.getClassOrVar("Machine");
                if (var133_123 != ScriptValue.NULL) {
                    var135_124 = ScriptContext.builder().copyFrom(var1_1);
                    var135_124.val("current_rpm", var1_1.getClassOrVar("rpm"));
                    var134_125 = Saw._sawSuCost(var135_124);
                    if (var133_123 instanceof ScriptValue.Obj && (var137_127 = (var136_126 = (ScriptValue.Obj)var133_123).instance()) != null && !(var137_127 instanceof PolyClass) && var136_126.typeName().equals("Machine")) {
                        var138_128 = new PolyClassMachine_v2(var137_127);
                        v15 /* !! */  = ScriptValue.of((boolean)var138_128.tm$56_report_su(var134_125.asNum()));
                    } else {
                        v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var133_123, (ScriptValue)var134_125, (ScriptContext)var1_1);
                    }
                } else {
                    v15 /* !! */  = ScriptValue.NULL;
                }
            }
            var139_129 = 1.0;
            var141_130 = ScriptValue.of((double)1.0);
            var0.val("is_now", var141_130);
        }
        if (var1_1.getNum("is_now") > 0.0 != false && var36_31 != false) {
            var142_131 = var1_1.getClassOrVar("Machine");
            var144_133 = var142_131 != ScriptValue.NULL ? ((var143_132 = PolyClassMachine_v2.ofGuarded((ScriptValue)var142_131)) != null ? var143_132.pg$158_belt() : PolyDispatch.bootstrapGet("memberGet", "belt", (ScriptValue)var142_131, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("belt", var144_133);
            var146_135 = ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var145_134 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var145_134, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm");
            var0.val("belt_rpm", var146_135);
            var147_136 = var1_1.getClassOrVar("Machine");
            if (var147_136 != ScriptValue.NULL) {
                var148_137 = "_saw_item_id";
                var149_138 = "str";
                if (var147_136 instanceof ScriptValue.Obj && (var151_140 = (var150_139 = (ScriptValue.Obj)var147_136).instance()) != null && !(var151_140 instanceof PolyClass) && var150_139.typeName().equals("Machine")) {
                    var152_141 = new PolyClassMachine_v2(var151_140);
                    v16 /* !! */  = var152_141.tm$34_get_typed(var148_137, var149_138);
                } else {
                    v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var147_136, (ScriptValue)ScriptValue.of((String)var148_137), (ScriptValue)ScriptValue.of((String)var149_138), (ScriptContext)var1_1);
                }
            } else {
                v16 /* !! */  = ScriptValue.NULL;
            }
            var153_142 = v16 /* !! */ ;
            var0.val("active_id", var153_142);
            if (ScriptFormula.valuesEqual((ScriptValue)var153_142, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var154_143 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                var0.val("active_id", var154_143);
            }
            if (var1_1.getStr("active_id").equals("")) {
                var155_144 = var1_1.getClassOrVar("belt");
                if ((var155_144 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var155_144, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
                    var156_145 = var1_1.getClassOrVar("belt");
                    var157_146 = var156_145 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var156_145, (ScriptContext)var1_1) : ScriptValue.NULL;
                    var0.val("carried", var157_146);
                    var158_147 = ScriptContext.builder().copyFrom(var1_1);
                    var159_148 = var1_1.getClassOrVar("carried");
                    var158_147.val("item_id", (ScriptValue)(var159_148 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var159_148, (ScriptContext)var1_1) : ScriptValue.NULL));
                    var160_149 = Saw._sawOutputFor(var158_147);
                    var0.val("out_id", var160_149);
                    var161_150 = new ArrayList<ScriptValue>();
                    var162_151 = var1_1.getClassOrVar("carried");
                    var161_150.add(ScriptValue.of((String)("[saw-debug] own-belt has_item id=" + (var162_151 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var162_151, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " out_id=" + (ScriptFormula.valuesEqual((ScriptValue)var160_149, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "null")) : var160_149).asStr())));
                    ScriptFormula.callBuiltin((String)"print", var161_150, (ScriptContext)var1_1);
                    if (ScriptFormula.valuesEqual((ScriptValue)var160_149, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var163_152 = var1_1.getClassOrVar("belt");
                        var164_153 = var163_152 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var163_152, (ScriptContext)var1_1) : ScriptValue.NULL;
                        var0.val("taken", var164_153);
                        var165_154 = var1_1.getClassOrVar("Machine");
                        if (var165_154 != ScriptValue.NULL) {
                            var166_155 = "_saw_item_id";
                            var167_156 = "str";
                            var169_157 = var1_1.getClassOrVar("taken");
                            v17 /* !! */  = var168_158 = var169_157 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var169_157, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var165_154 instanceof ScriptValue.Obj && (var171_160 = (var170_159 = (ScriptValue.Obj)var165_154).instance()) != null && !(var171_160 instanceof PolyClass) && var170_159.typeName().equals("Machine")) {
                                var172_161 = new PolyClassMachine_v2(var171_160);
                                v18 /* !! */  = ScriptValue.of((boolean)var172_161.tm$82_set_typed(var166_155, var167_156, var168_158));
                            } else {
                                v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var165_154, (ScriptValue)ScriptValue.of((String)var166_155), (ScriptValue)ScriptValue.of((String)var167_156), (ScriptValue)var168_158, (ScriptContext)var1_1);
                            }
                        } else {
                            v18 /* !! */  = ScriptValue.NULL;
                        }
                        var173_162 = var1_1.getClassOrVar("Machine");
                        if (var173_162 != ScriptValue.NULL) {
                            var174_163 = "_saw_out_id";
                            var175_164 = "str";
                            var176_165 = var160_149;
                            if (var173_162 instanceof ScriptValue.Obj && (var178_167 = (var177_166 = (ScriptValue.Obj)var173_162).instance()) != null && !(var178_167 instanceof PolyClass) && var177_166.typeName().equals("Machine")) {
                                var179_168 = new PolyClassMachine_v2(var178_167);
                                v19 /* !! */  = ScriptValue.of((boolean)var179_168.tm$82_set_typed(var174_163, var175_164, var176_165));
                            } else {
                                v19 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var173_162, (ScriptValue)ScriptValue.of((String)var174_163), (ScriptValue)ScriptValue.of((String)var175_164), (ScriptValue)var176_165, (ScriptContext)var1_1);
                            }
                        } else {
                            v19 /* !! */  = ScriptValue.NULL;
                        }
                        var180_169 = var1_1.getClassOrVar("Machine");
                        if (var180_169 != ScriptValue.NULL) {
                            var181_170 = "_saw_count";
                            var182_171 = "int";
                            var184_172 = var1_1.getClassOrVar("taken");
                            v20 /* !! */  = var183_173 = var184_172 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var184_172, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var180_169 instanceof ScriptValue.Obj && (var186_175 = (var185_174 = (ScriptValue.Obj)var180_169).instance()) != null && !(var186_175 instanceof PolyClass) && var185_174.typeName().equals("Machine")) {
                                var187_176 = new PolyClassMachine_v2(var186_175);
                                v21 /* !! */  = ScriptValue.of((boolean)var187_176.tm$82_set_typed(var181_170, var182_171, var183_173));
                            } else {
                                v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var180_169, (ScriptValue)ScriptValue.of((String)var181_170), (ScriptValue)ScriptValue.of((String)var182_171), (ScriptValue)var183_173, (ScriptContext)var1_1);
                            }
                        } else {
                            v21 /* !! */  = ScriptValue.NULL;
                        }
                        var188_177 = var1_1.getClassOrVar("Machine");
                        if (var188_177 != ScriptValue.NULL) {
                            var189_178 = "_saw_progress";
                            var190_179 = "int";
                            var191_180 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            if (var188_177 instanceof ScriptValue.Obj && (var193_182 = (var192_181 = (ScriptValue.Obj)var188_177).instance()) != null && !(var193_182 instanceof PolyClass) && var192_181.typeName().equals("Machine")) {
                                var194_183 = new PolyClassMachine_v2(var193_182);
                                v22 /* !! */  = ScriptValue.of((boolean)var194_183.tm$82_set_typed(var189_178, var190_179, var191_180));
                            } else {
                                v22 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var188_177, (ScriptValue)ScriptValue.of((String)var189_178), (ScriptValue)ScriptValue.of((String)var190_179), (ScriptValue)var191_180, (ScriptContext)var1_1);
                            }
                        } else {
                            v22 /* !! */  = ScriptValue.NULL;
                        }
                        var195_184 = var1_1.getClassOrVar("Machine");
                        if (var195_184 != ScriptValue.NULL) {
                            var196_185 = "_saw_from_ground";
                            var197_186 = "int";
                            var198_187 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            if (var195_184 instanceof ScriptValue.Obj && (var200_189 = (var199_188 = (ScriptValue.Obj)var195_184).instance()) != null && !(var200_189 instanceof PolyClass) && var199_188.typeName().equals("Machine")) {
                                var201_190 = new PolyClassMachine_v2(var200_189);
                                v23 /* !! */  = ScriptValue.of((boolean)var201_190.tm$82_set_typed(var196_185, var197_186, var198_187));
                            } else {
                                v23 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var195_184, (ScriptValue)ScriptValue.of((String)var196_185), (ScriptValue)ScriptValue.of((String)var197_186), (ScriptValue)var198_187, (ScriptContext)var1_1);
                            }
                        } else {
                            v23 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var202_191 = ScriptContext.builder().copyFrom(var1_1);
                    var203_192 = Saw._sawInputFace(var202_191);
                    var0.val("in_face", var203_192);
                    var204_193 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                    var204_193.val("face", var203_192);
                    var204_193.val("validator", var1_1.getClassOrVar("null"));
                    var204_193.val("amount", var1_1.getClassOrVar("null"));
                    var205_194 = BeltUtils.beltTake((ScriptContext.Builder)var204_193);
                    var0.val("taken", var205_194);
                    var206_195 = new ArrayList<ScriptValue>();
                    var206_195.add(var205_194);
                    if (ScriptFormula.callBuiltin((String)"is_empty", var206_195, (ScriptContext)var1_1).asBool() ^ true) {
                        var207_196 = ScriptContext.builder().copyFrom(var1_1);
                        var208_197 = var1_1.getClassOrVar("taken");
                        var207_196.val("item_id", (ScriptValue)(var208_197 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var208_197, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var209_198 = Saw._sawOutputFor(var207_196);
                        var0.val("out_id", var209_198);
                        if (ScriptFormula.valuesEqual((ScriptValue)var209_198, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var210_199 = var1_1.getClassOrVar("Machine");
                            if (var210_199 != ScriptValue.NULL) {
                                var211_200 = "_saw_item_id";
                                var212_201 = "str";
                                var214_202 = var1_1.getClassOrVar("taken");
                                v24 /* !! */  = var213_203 = var214_202 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var214_202, (ScriptContext)var1_1) : ScriptValue.NULL;
                                if (var210_199 instanceof ScriptValue.Obj && (var216_205 = (var215_204 = (ScriptValue.Obj)var210_199).instance()) != null && !(var216_205 instanceof PolyClass) && var215_204.typeName().equals("Machine")) {
                                    var217_206 = new PolyClassMachine_v2(var216_205);
                                    v25 /* !! */  = ScriptValue.of((boolean)var217_206.tm$82_set_typed(var211_200, var212_201, var213_203));
                                } else {
                                    v25 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var210_199, (ScriptValue)ScriptValue.of((String)var211_200), (ScriptValue)ScriptValue.of((String)var212_201), (ScriptValue)var213_203, (ScriptContext)var1_1);
                                }
                            } else {
                                v25 /* !! */  = ScriptValue.NULL;
                            }
                            var218_207 = var1_1.getClassOrVar("Machine");
                            if (var218_207 != ScriptValue.NULL) {
                                var219_208 = "_saw_out_id";
                                var220_209 = "str";
                                var221_210 = var209_198;
                                if (var218_207 instanceof ScriptValue.Obj && (var223_212 = (var222_211 = (ScriptValue.Obj)var218_207).instance()) != null && !(var223_212 instanceof PolyClass) && var222_211.typeName().equals("Machine")) {
                                    var224_213 = new PolyClassMachine_v2(var223_212);
                                    v26 /* !! */  = ScriptValue.of((boolean)var224_213.tm$82_set_typed(var219_208, var220_209, var221_210));
                                } else {
                                    v26 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var218_207, (ScriptValue)ScriptValue.of((String)var219_208), (ScriptValue)ScriptValue.of((String)var220_209), (ScriptValue)var221_210, (ScriptContext)var1_1);
                                }
                            } else {
                                v26 /* !! */  = ScriptValue.NULL;
                            }
                            var225_214 = var1_1.getClassOrVar("Machine");
                            if (var225_214 != ScriptValue.NULL) {
                                var226_215 = "_saw_count";
                                var227_216 = "int";
                                var229_217 = new ArrayList<ScriptValue>();
                                var229_217.add(var205_194);
                                var228_218 = ScriptFormula.callBuiltin((String)"item_count", var229_217, (ScriptContext)var1_1);
                                if (var225_214 instanceof ScriptValue.Obj && (var231_220 = (var230_219 = (ScriptValue.Obj)var225_214).instance()) != null && !(var231_220 instanceof PolyClass) && var230_219.typeName().equals("Machine")) {
                                    var232_221 = new PolyClassMachine_v2(var231_220);
                                    v27 /* !! */  = ScriptValue.of((boolean)var232_221.tm$82_set_typed(var226_215, var227_216, var228_218));
                                } else {
                                    v27 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var225_214, (ScriptValue)ScriptValue.of((String)var226_215), (ScriptValue)ScriptValue.of((String)var227_216), (ScriptValue)var228_218, (ScriptContext)var1_1);
                                }
                            } else {
                                v27 /* !! */  = ScriptValue.NULL;
                            }
                            var233_222 = var1_1.getClassOrVar("Machine");
                            if (var233_222 != ScriptValue.NULL) {
                                var234_223 = "_saw_progress";
                                var235_224 = "int";
                                var236_225 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                if (var233_222 instanceof ScriptValue.Obj && (var238_227 = (var237_226 = (ScriptValue.Obj)var233_222).instance()) != null && !(var238_227 instanceof PolyClass) && var237_226.typeName().equals("Machine")) {
                                    var239_228 = new PolyClassMachine_v2(var238_227);
                                    v28 /* !! */  = ScriptValue.of((boolean)var239_228.tm$82_set_typed(var234_223, var235_224, var236_225));
                                } else {
                                    v28 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var233_222, (ScriptValue)ScriptValue.of((String)var234_223), (ScriptValue)ScriptValue.of((String)var235_224), (ScriptValue)var236_225, (ScriptContext)var1_1);
                                }
                            } else {
                                v28 /* !! */  = ScriptValue.NULL;
                            }
                            var240_229 = var1_1.getClassOrVar("Machine");
                            if (var240_229 != ScriptValue.NULL) {
                                var241_230 = "_saw_from_ground";
                                var242_231 = "int";
                                var243_232 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0);
                                if (var240_229 instanceof ScriptValue.Obj && (var245_234 = (var244_233 = (ScriptValue.Obj)var240_229).instance()) != null && !(var245_234 instanceof PolyClass) && var244_233.typeName().equals("Machine")) {
                                    var246_235 = new PolyClassMachine_v2(var245_234);
                                    v29 /* !! */  = ScriptValue.of((boolean)var246_235.tm$82_set_typed(var241_230, var242_231, var243_232));
                                } else {
                                    v29 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var240_229, (ScriptValue)ScriptValue.of((String)var241_230), (ScriptValue)ScriptValue.of((String)var242_231), (ScriptValue)var243_232, (ScriptContext)var1_1);
                                }
                            } else {
                                v29 /* !! */  = ScriptValue.NULL;
                            }
                        } else {
                            var247_236 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                            var247_236.val("face", var203_192);
                            var247_236.val("item", var205_194);
                            BeltUtils.beltGive((ScriptContext.Builder)var247_236);
                        }
                    } else {
                        var251_237 = var1_1.getClassOrVar("Machine");
                        if (var251_237 != ScriptValue.NULL) {
                            var252_238 = 0.7;
                            if (var251_237 instanceof ScriptValue.Obj && (var255_240 = (var254_239 = (ScriptValue.Obj)var251_237).instance()) != null && !(var255_240 instanceof PolyClass) && var254_239.typeName().equals("Machine")) {
                                var256_241 = new PolyClassMachine_v2(var255_240);
                                v30 /* !! */  = var256_241.tm$94_nearby_entities(var252_238);
                            } else {
                                v30 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var251_237, (ScriptValue)ScriptValue.of((double)var252_238), (ScriptContext)var1_1);
                            }
                        } else {
                            v30 /* !! */  = ScriptValue.NULL;
                        }
                        var248_242 = ScriptProgram.elementsOf((ScriptValue)v30 /* !! */ );
                        if (var248_242 != null) {
                            for (ScriptValue var250_248 : var248_242) {
                                var0.val("entity", var250_248);
                                var257_244 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                                var257_244.val("entity", var1_1.getClassOrVar("entity"));
                                if (!Utils.isRestingItem(var257_244).asBool()) continue;
                                var258_245 = ScriptContext.builder().copyFrom(var1_1);
                                var259_246 = var1_1.getClassOrVar("entity");
                                var258_245.val("item_id", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var259_246 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var259_246, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                                var260_247 = Saw._sawOutputFor(var258_245);
                                var0.val("out_id", var260_247);
                                if (!(ScriptFormula.valuesEqual((ScriptValue)var260_247, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                                var261_249 = var1_1.getClassOrVar("Machine");
                                if (var261_249 != ScriptValue.NULL) {
                                    var262_250 = "_saw_item_id";
                                    var263_251 = "str";
                                    var265_252 = var1_1.getClassOrVar("entity");
                                    var264_253 = PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var265_252 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var265_252, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    if (var261_249 instanceof ScriptValue.Obj && (var267_255 = (var266_254 = (ScriptValue.Obj)var261_249).instance()) != null && !(var267_255 instanceof PolyClass) && var266_254.typeName().equals("Machine")) {
                                        var268_256 = new PolyClassMachine_v2(var267_255);
                                        v31 /* !! */  = ScriptValue.of((boolean)var268_256.tm$82_set_typed(var262_250, var263_251, (ScriptValue)var264_253));
                                    } else {
                                        v31 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var261_249, (ScriptValue)ScriptValue.of((String)var262_250), (ScriptValue)ScriptValue.of((String)var263_251), (ScriptValue)var264_253, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v31 /* !! */  = ScriptValue.NULL;
                                }
                                var269_257 = var1_1.getClassOrVar("Machine");
                                if (var269_257 != ScriptValue.NULL) {
                                    var270_258 = "_saw_out_id";
                                    var271_259 = "str";
                                    var272_260 = var260_247;
                                    if (var269_257 instanceof ScriptValue.Obj && (var274_262 = (var273_261 = (ScriptValue.Obj)var269_257).instance()) != null && !(var274_262 instanceof PolyClass) && var273_261.typeName().equals("Machine")) {
                                        var275_263 = new PolyClassMachine_v2(var274_262);
                                        v32 /* !! */  = ScriptValue.of((boolean)var275_263.tm$82_set_typed(var270_258, var271_259, var272_260));
                                    } else {
                                        v32 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var269_257, (ScriptValue)ScriptValue.of((String)var270_258), (ScriptValue)ScriptValue.of((String)var271_259), (ScriptValue)var272_260, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v32 /* !! */  = ScriptValue.NULL;
                                }
                                var276_264 = var1_1.getClassOrVar("Machine");
                                if (var276_264 != ScriptValue.NULL) {
                                    var277_265 = "_saw_count";
                                    var278_266 = "int";
                                    var280_267 = new ArrayList<ScriptValue>();
                                    var281_268 = var1_1.getClassOrVar("entity");
                                    var280_267.add((ScriptValue)(var281_268 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var281_268, (ScriptContext)var1_1) : ScriptValue.NULL));
                                    var279_269 = ScriptFormula.callBuiltin((String)"item_count", var280_267, (ScriptContext)var1_1);
                                    if (var276_264 instanceof ScriptValue.Obj && (var283_271 = (var282_270 = (ScriptValue.Obj)var276_264).instance()) != null && !(var283_271 instanceof PolyClass) && var282_270.typeName().equals("Machine")) {
                                        var284_272 = new PolyClassMachine_v2(var283_271);
                                        v33 /* !! */  = ScriptValue.of((boolean)var284_272.tm$82_set_typed(var277_265, var278_266, var279_269));
                                    } else {
                                        v33 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var276_264, (ScriptValue)ScriptValue.of((String)var277_265), (ScriptValue)ScriptValue.of((String)var278_266), (ScriptValue)var279_269, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v33 /* !! */  = ScriptValue.NULL;
                                }
                                var285_273 = var1_1.getClassOrVar("Machine");
                                if (var285_273 != ScriptValue.NULL) {
                                    var286_274 = "_saw_progress";
                                    var287_275 = "int";
                                    var288_276 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                    if (var285_273 instanceof ScriptValue.Obj && (var290_278 = (var289_277 = (ScriptValue.Obj)var285_273).instance()) != null && !(var290_278 instanceof PolyClass) && var289_277.typeName().equals("Machine")) {
                                        var291_279 = new PolyClassMachine_v2(var290_278);
                                        v34 /* !! */  = ScriptValue.of((boolean)var291_279.tm$82_set_typed(var286_274, var287_275, var288_276));
                                    } else {
                                        v34 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var285_273, (ScriptValue)ScriptValue.of((String)var286_274), (ScriptValue)ScriptValue.of((String)var287_275), (ScriptValue)var288_276, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v34 /* !! */  = ScriptValue.NULL;
                                }
                                var292_280 = var1_1.getClassOrVar("Machine");
                                if (var292_280 != ScriptValue.NULL) {
                                    var293_281 = "_saw_from_ground";
                                    var294_282 = "int";
                                    var295_283 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0);
                                    if (var292_280 instanceof ScriptValue.Obj && (var297_285 = (var296_284 = (ScriptValue.Obj)var292_280).instance()) != null && !(var297_285 instanceof PolyClass) && var296_284.typeName().equals("Machine")) {
                                        var298_286 = new PolyClassMachine_v2(var297_285);
                                        v35 /* !! */  = ScriptValue.of((boolean)var298_286.tm$82_set_typed(var293_281, var294_282, var295_283));
                                    } else {
                                        v35 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var292_280, (ScriptValue)ScriptValue.of((String)var293_281), (ScriptValue)ScriptValue.of((String)var294_282), (ScriptValue)var295_283, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v35 /* !! */  = ScriptValue.NULL;
                                }
                                var299_287 = var1_1.getClassOrVar("entity");
                                v36 /* !! */  = var299_287 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var299_287, (ScriptContext)var1_1) : ScriptValue.NULL;
                                break;
                            }
                        }
                    }
                }
            } else {
                var300_288 = var1_1.getClassOrVar("Machine");
                if (var300_288 != ScriptValue.NULL) {
                    var301_289 = "_saw_progress";
                    var302_290 = "int";
                    if (var300_288 instanceof ScriptValue.Obj && (var304_292 = (var303_291 = (ScriptValue.Obj)var300_288).instance()) != null && !(var304_292 instanceof PolyClass) && var303_291.typeName().equals("Machine")) {
                        var305_293 = new PolyClassMachine_v2(var304_292);
                        v37 /* !! */  = var305_293.tm$34_get_typed(var301_289, var302_290);
                    } else {
                        v37 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var300_288, (ScriptValue)ScriptValue.of((String)var301_289), (ScriptValue)ScriptValue.of((String)var302_290), (ScriptContext)var1_1);
                    }
                } else {
                    v37 /* !! */  = ScriptValue.NULL;
                }
                var306_294 = v37 /* !! */ ;
                var0.val("progress", var306_294);
                if (ScriptFormula.valuesEqual((ScriptValue)var306_294, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var307_295 = 0.0;
                    var309_296 = ScriptValue.of((double)0.0);
                    var0.val("progress", var309_296);
                }
                var310_297 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)Math.abs(var146_135.asNum())));
                var0.val("progress", var310_297);
                var311_298 = var1_1.getClassOrVar("Machine");
                if (var311_298 != ScriptValue.NULL) {
                    var313_299 = ScriptContext.builder().copyFrom(var1_1);
                    var313_299.val("current_rpm", var146_135);
                    var312_300 = Saw._sawSuCost(var313_299);
                    if (var311_298 instanceof ScriptValue.Obj && (var315_302 = (var314_301 = (ScriptValue.Obj)var311_298).instance()) != null && !(var315_302 instanceof PolyClass) && var314_301.typeName().equals("Machine")) {
                        var316_303 = new PolyClassMachine_v2(var315_302);
                        v38 /* !! */  = ScriptValue.of((boolean)var316_303.tm$56_report_su(var312_300.asNum()));
                    } else {
                        v38 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var311_298, (ScriptValue)var312_300, (ScriptContext)var1_1);
                    }
                } else {
                    v38 /* !! */  = ScriptValue.NULL;
                }
                if (var310_297.asNum() >= var11_8) {
                    var317_304 = var1_1.getClassOrVar("Machine");
                    if (var317_304 != ScriptValue.NULL) {
                        var318_305 = "_saw_out_id";
                        var319_306 = "str";
                        if (var317_304 instanceof ScriptValue.Obj && (var321_308 = (var320_307 = (ScriptValue.Obj)var317_304).instance()) != null && !(var321_308 instanceof PolyClass) && var320_307.typeName().equals("Machine")) {
                            var322_309 = new PolyClassMachine_v2(var321_308);
                            v39 /* !! */  = var322_309.tm$34_get_typed(var318_305, var319_306);
                        } else {
                            v39 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var317_304, (ScriptValue)ScriptValue.of((String)var318_305), (ScriptValue)ScriptValue.of((String)var319_306), (ScriptContext)var1_1);
                        }
                    } else {
                        v39 /* !! */  = ScriptValue.NULL;
                    }
                    var323_310 = v39 /* !! */ ;
                    var0.val("out_id", var323_310);
                    var324_311 = var1_1.getClassOrVar("Machine");
                    if (var324_311 != ScriptValue.NULL) {
                        var325_312 = "_saw_count";
                        var326_313 = "int";
                        if (var324_311 instanceof ScriptValue.Obj && (var328_315 = (var327_314 = (ScriptValue.Obj)var324_311).instance()) != null && !(var328_315 instanceof PolyClass) && var327_314.typeName().equals("Machine")) {
                            var329_316 = new PolyClassMachine_v2(var328_315);
                            v40 /* !! */  = var329_316.tm$34_get_typed(var325_312, var326_313);
                        } else {
                            v40 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var324_311, (ScriptValue)ScriptValue.of((String)var325_312), (ScriptValue)ScriptValue.of((String)var326_313), (ScriptContext)var1_1);
                        }
                    } else {
                        v40 /* !! */  = ScriptValue.NULL;
                    }
                    var330_317 = v40 /* !! */ ;
                    var0.val("remaining", var330_317);
                    if (ScriptFormula.valuesEqual((ScriptValue)var330_317, (ScriptValue)var1_1.getClassOrVar("null")) != false || var330_317.asNum() <= 0.0 != false) {
                        var331_318 = 1.0;
                        var333_319 = ScriptValue.of((double)1.0);
                        var0.val("remaining", var333_319);
                    }
                    if ((var334_320 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        var335_321 = "_saw_from_ground";
                        var336_322 = "int";
                        if (var334_320 instanceof ScriptValue.Obj && (var338_324 = (var337_323 = (ScriptValue.Obj)var334_320).instance()) != null && !(var338_324 instanceof PolyClass) && var337_323.typeName().equals("Machine")) {
                            var339_325 = new PolyClassMachine_v2(var338_324);
                            v41 /* !! */  = var339_325.tm$34_get_typed(var335_321, var336_322);
                        } else {
                            v41 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var334_320, (ScriptValue)ScriptValue.of((String)var335_321), (ScriptValue)ScriptValue.of((String)var336_322), (ScriptContext)var1_1);
                        }
                    } else {
                        v41 /* !! */  = ScriptValue.NULL;
                    }
                    var340_326 = v41 /* !! */ ;
                    var0.val("from_ground", var340_326);
                    var341_327 = ScriptContext.builder().copyFrom(var1_1);
                    var341_327.val("item_id", var1_1.getClassOrVar("active_id"));
                    var341_327.val("out_id", var323_310);
                    var342_328 = Saw._sawRecipeFor(var341_327);
                    var0.val("recipe", var342_328);
                    var344_330 = ScriptFormula.valuesEqual((ScriptValue)var342_328, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var343_329 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "outputs", (ScriptValue)var343_329, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("null");
                    var0.val("result", var344_330);
                    if (ScriptFormula.valuesEqual((ScriptValue)var344_330, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var345_331 = var1_1.getClassOrVar("result");
                        v42 /* !! */  = var345_331 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "items", (ScriptValue)var345_331, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var346_332 = new ArrayList<ScriptValue>();
                        var347_333 = new ArrayList<ScriptValue>();
                        var347_333.add(var323_310);
                        var347_333.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0));
                        var346_332.add(ScriptFormula.callBuiltin((String)"MinecraftItem", var347_333, (ScriptContext)var1_1));
                        v42 /* !! */  = new ScriptValue.Array(var346_332);
                    }
                    var348_334 /* !! */  = v42 /* !! */ ;
                    var0.val("items", var348_334 /* !! */ );
                    var349_335 = ScriptProgram.elementsOf((ScriptValue)var348_334 /* !! */ );
                    if (var349_335 != null) {
                        for (ScriptValue var351_337 : var349_335) {
                            var0.val("out_item", var351_337);
                            if (ScriptFormula.valuesEqual((ScriptValue)var340_326, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0))) != false || ScriptFormula.valuesEqual((ScriptValue)var340_326, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0))) != false) {
                                var352_338 = ScriptContext.builder().copyFrom(var1_1);
                                var352_338.val("item", var1_1.getClassOrVar("out_item"));
                                Saw._sawDepositDirectional(var352_338);
                                continue;
                            }
                            var353_339 = var1_1.getClassOrVar("belt");
                            var354_340 = var353_339 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var353_339, (ScriptValue)var1_1.getClassOrVar("out_item"), (ScriptContext)var1_1) : ScriptValue.NULL;
                            var0.val("leftover", var354_340);
                            var355_341 = new ArrayList<ScriptValue>();
                            var355_341.add(var354_340);
                            if (!(ScriptFormula.callBuiltin((String)"is_empty", var355_341, (ScriptContext)var1_1).asBool() ^ true)) continue;
                            var356_342 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var356_342.val("item", var354_340);
                            Utils.1._deposit((ScriptContext.Builder)var356_342);
                        }
                    }
                    var357_343 = var1_1.getNum("remaining") - 1.0;
                    var359_344 = ScriptValue.of((double)var357_343);
                    var0.val("remaining", var359_344);
                    var360_345 = var1_1.getClassOrVar("Machine");
                    if (var360_345 != ScriptValue.NULL) {
                        var361_346 = "_saw_count";
                        var362_347 = "int";
                        var363_348 = ScriptValue.of((double)var357_343);
                        if (var360_345 instanceof ScriptValue.Obj && (var365_350 = (var364_349 = (ScriptValue.Obj)var360_345).instance()) != null && !(var365_350 instanceof PolyClass) && var364_349.typeName().equals("Machine")) {
                            var366_351 = new PolyClassMachine_v2(var365_350);
                            v43 /* !! */  = ScriptValue.of((boolean)var366_351.tm$82_set_typed(var361_346, var362_347, var363_348));
                        } else {
                            v43 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var360_345, (ScriptValue)ScriptValue.of((String)var361_346), (ScriptValue)ScriptValue.of((String)var362_347), (ScriptValue)var363_348, (ScriptContext)var1_1);
                        }
                    } else {
                        v43 /* !! */  = ScriptValue.NULL;
                    }
                    var367_352 = var1_1.getClassOrVar("Machine");
                    if (var367_352 != ScriptValue.NULL) {
                        var368_353 = "_saw_progress";
                        var369_354 = "int";
                        var370_355 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                        if (var367_352 instanceof ScriptValue.Obj && (var372_357 = (var371_356 = (ScriptValue.Obj)var367_352).instance()) != null && !(var372_357 instanceof PolyClass) && var371_356.typeName().equals("Machine")) {
                            var373_358 = new PolyClassMachine_v2(var372_357);
                            v44 /* !! */  = ScriptValue.of((boolean)var373_358.tm$82_set_typed(var368_353, var369_354, var370_355));
                        } else {
                            v44 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var367_352, (ScriptValue)ScriptValue.of((String)var368_353), (ScriptValue)ScriptValue.of((String)var369_354), (ScriptValue)var370_355, (ScriptContext)var1_1);
                        }
                    } else {
                        v44 /* !! */  = ScriptValue.NULL;
                    }
                    if (var357_343 <= 0.0) {
                        var374_359 = var1_1.getClassOrVar("Machine");
                        if (var374_359 != ScriptValue.NULL) {
                            var375_360 = "_saw_item_id";
                            var376_361 = "str";
                            var377_362 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                            if (var374_359 instanceof ScriptValue.Obj && (var379_364 = (var378_363 = (ScriptValue.Obj)var374_359).instance()) != null && !(var379_364 instanceof PolyClass) && var378_363.typeName().equals("Machine")) {
                                var380_365 = new PolyClassMachine_v2(var379_364);
                                v45 /* !! */  = ScriptValue.of((boolean)var380_365.tm$82_set_typed(var375_360, var376_361, var377_362));
                            } else {
                                v45 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var374_359, (ScriptValue)ScriptValue.of((String)var375_360), (ScriptValue)ScriptValue.of((String)var376_361), (ScriptValue)var377_362, (ScriptContext)var1_1);
                            }
                        } else {
                            v45 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var381_366 = var1_1.getClassOrVar("Machine");
                    if (var381_366 != ScriptValue.NULL) {
                        var382_367 = "_saw_progress";
                        var383_368 = "int";
                        var384_369 = var310_297;
                        if (var381_366 instanceof ScriptValue.Obj && (var386_371 = (var385_370 = (ScriptValue.Obj)var381_366).instance()) != null && !(var386_371 instanceof PolyClass) && var385_370.typeName().equals("Machine")) {
                            var387_372 = new PolyClassMachine_v2(var386_371);
                            v46 /* !! */  = ScriptValue.of((boolean)var387_372.tm$82_set_typed(var382_367, var383_368, var384_369));
                        } else {
                            v46 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var381_366, (ScriptValue)ScriptValue.of((String)var382_367), (ScriptValue)ScriptValue.of((String)var383_368), (ScriptValue)var384_369, (ScriptContext)var1_1);
                        }
                    } else {
                        v46 /* !! */  = ScriptValue.NULL;
                    }
                }
            }
        }
        var388_373 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var388_373.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "_saw_act"));
        var388_373.val("is_now", var1_1.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)var388_373);
        Saw.FILE_SCOPE = var0.build();
    }
}
