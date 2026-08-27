/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v3
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyClassPlayer
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
import dev.arubik.craftengine.script.PolyClassBlock_v3;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyClassPlayer;
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
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("count", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        List list = ScriptProgram.elementsOf((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v4.pg$146_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
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
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        List list = ScriptProgram.elementsOf((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v4.pg$146_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
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
            var2_4 = ScriptProgram.elementsOf((ScriptValue)(var5_2 != ScriptValue.NULL ? ((var6_3 = PolyClassMachine_v4.ofGuarded((ScriptValue)var5_2)) != null ? var6_3.pg$146_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)var5_2, (ScriptContext)var1_1)) : ScriptValue.NULL));
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
                    v0 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v0 = false;
                }
                if (!(v0 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var10_10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("out_id")) != false)) continue;
                return var1_1.getClassOrVar("r");
            }
        }
        var16_13 = new ArrayList<ScriptValue>();
        var16_13.add(var1_1.getClassOrVar("item_id"));
        var18_14 = var1_1.getClassOrVar("Registry");
        var17_16 = var18_14 != ScriptValue.NULL ? ((var19_15 = PolyClassRegistry.ofGuarded((ScriptValue)var18_14)) != null ? var19_15.pg$0_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)var18_14, (ScriptContext)var1_1)) : ScriptValue.NULL;
        var20_17 = PolyClassRecipeRegistry.ofGuarded((ScriptValue)var17_16);
        var13_18 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)(var20_17 != null ? var20_17.pg$2_stonecutter() : PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)var17_16, (ScriptContext)var1_1)), var16_13, (ScriptContext)var1_1));
        if (var13_18 != null) {
            for (ScriptValue var15_20 : var13_18) {
                var0.val("r", var15_20);
                var21_21 = var1_1.getClassOrVar("r");
                var22_22 = var21_21 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)var21_21, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("outs", var22_22);
                var23_23 = new ArrayList<ScriptValue>();
                var23_23.add(var22_22);
                if (!(ScriptFormula.callBuiltin((String)"size", var23_23, (ScriptContext)var1_1).asNum() > 0.0 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var22_22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("out_id")) != false)) continue;
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
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v4;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                List list2 = ScriptProgram.elementsOf((ScriptValue)(scriptValue6 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine_v4.pg$146_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL));
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
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object4);
                    object3 = polyClassMachine_v4.tm$34_get_typed(string, string3);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object5);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string4, scriptValue16));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string4));
                    arrayList.add(scriptValue16);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
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
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("item_id"));
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("Registry");
        ScriptValue scriptValue21 = scriptValue20 != ScriptValue.NULL ? ((polyClassRegistry = PolyClassRegistry.ofGuarded((ScriptValue)scriptValue20)) != null ? polyClassRegistry.pg$0_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue20, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        PolyClassRecipeRegistry polyClassRecipeRegistry = PolyClassRecipeRegistry.ofGuarded((ScriptValue)scriptValue21);
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)(polyClassRecipeRegistry != null ? polyClassRecipeRegistry.pg$2_stonecutter() : PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)), arrayList, (ScriptContext)scriptContext);
        builder.val("stone_candidates", (ScriptValue)callSite);
        if (scriptContext.getStr("filter_id").equals("") ^ true && (list = ScriptProgram.elementsOf((ScriptValue)callSite)) != null) {
            for (ScriptValue scriptValue22 : list) {
                builder.val("r", scriptValue22);
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("r");
                ScriptValue scriptValue24 = scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue23, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("outs", scriptValue24);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptValue24);
                if (!(ScriptFormula.callBuiltin((String)"size", arrayList3, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue24, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
                return scriptContext.getClassOrVar("filter_id");
            }
        }
        ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
        arrayList4.add(callSite);
        ScriptValue scriptValue25 = ScriptFormula.callBuiltin((String)"size", arrayList4, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object7);
                    object6 = polyClassMachine_v4.tm$34_get_typed(string, string5);
                } else {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(ScriptValue.of((String)string));
                    arrayList5.add(ScriptValue.of((String)string5));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue26, arrayList5, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object8);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string6, scriptValue31));
                } else {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(ScriptValue.of((String)string));
                    arrayList6.add(ScriptValue.of((String)string6));
                    arrayList6.add(scriptValue31);
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue30, arrayList6, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            CallSite callSite2 = PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)callSite, (ScriptValue)ScriptValue.of((double)d6)), (ScriptContext)scriptContext);
            builder.val("outs", (ScriptValue)callSite2);
            ArrayList<CallSite> arrayList7 = new ArrayList<CallSite>();
            arrayList7.add(callSite2);
            if (ScriptFormula.callBuiltin((String)"size", arrayList7, (ScriptContext)scriptContext).asNum() > 0.0) {
                return PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)callSite2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext);
            }
        }
        return scriptContext.getClassOrVar("null");
    }

    public static ScriptValue onRightClick(ScriptContext.Builder builder) {
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer.pg$48_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string2, scriptValue4));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(ScriptValue.of((String)string));
                    arrayList2.add(ScriptValue.of((String)string2));
                    arrayList2.add(scriptValue4);
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue6));
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((String)string));
                    arrayList3.add(ScriptValue.of((String)string3));
                    arrayList3.add(scriptValue6);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
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
        PolyClassMachine_v4 polyClassMachine_v4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        String string = "facing";
        if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v3 polyClassBlock_v3 = new PolyClassBlock_v3(object2);
            callSite = polyClassBlock_v3.tm$24_property(string);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)string));
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v42 = new PolyClassMachine_v4(object3);
                object = polyClassMachine_v42.tm$34_get_typed(string2, string3);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(ScriptValue.of((String)string3));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string2, scriptValue2));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string2));
                    arrayList.add(scriptValue2);
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
        PolyClassMachine_v4 polyClassMachine_v4;
        CallSite callSite2;
        ScriptValue.Obj obj2;
        Object object2;
        PolyClassMachine_v4 polyClassMachine_v42;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v42.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        String string = "face";
        if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Block")) {
            PolyClassBlock_v3 polyClassBlock_v3 = new PolyClassBlock_v3(object2);
            callSite2 = polyClassBlock_v3.tm$24_property(string);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)string));
            callSite2 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        }
        CallSite callSite3 = callSite2;
        builder.val("face", (ScriptValue)callSite3);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine_v4.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        String string2 = "facing";
        if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v3 polyClassBlock_v3 = new PolyClassBlock_v3(object);
            callSite = polyClassBlock_v3.tm$24_property(string2);
        } else {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)string2));
            callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v43 = new PolyClassMachine_v4(object3);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v43.tm$40_set_rpm_input(string3));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string3));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v44 = new PolyClassMachine_v4(object4);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v44.tm$44_set_rpm_output_same(string4));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string4));
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v45 = new PolyClassMachine_v4(object5);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v45.tm$40_set_rpm_input(string5));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string5));
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v46 = new PolyClassMachine_v4(object6);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v46.tm$44_set_rpm_output_same(string6));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string6));
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v47 = new PolyClassMachine_v4(object7);
                    v6 = ScriptValue.of((boolean)polyClassMachine_v47.tm$40_set_rpm_input(string7));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string7));
                    v6 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
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
                    PolyClassMachine_v4 polyClassMachine_v48 = new PolyClassMachine_v4(object8);
                    v7 = ScriptValue.of((boolean)polyClassMachine_v48.tm$44_set_rpm_output_same(string8));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string8));
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
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
                PolyClassMachine_v4 polyClassMachine_v4;
                scriptContext = builder.peek();
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("item"));
                if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
                Object object = scriptValue != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine_v4.pg$190_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
                v1 = scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v4(object).um$19_drop_item_toward(arrayList3) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
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
        block187: {
            block185: {
                block186: {
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
                    var20_16 = ScriptValue.of((String)((var14_10 != ScriptValue.NULL ? ((var15_11 = PolyClassMachine_v4.ofGuarded((ScriptValue)var14_10)) != null ? var15_11.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var14_10, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "," + (var16_12 != ScriptValue.NULL ? ((var17_13 = PolyClassMachine_v4.ofGuarded((ScriptValue)var16_12)) != null ? var17_13.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var16_12, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "," + (var18_14 != ScriptValue.NULL ? ((var19_15 = PolyClassMachine_v4.ofGuarded((ScriptValue)var18_14)) != null ? var19_15.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var18_14, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr()));
                    var0.val("_hold_key", var20_16);
                    var21_17 = var1_1.getClassOrVar("Machine");
                    var23_19 = var21_17 != ScriptValue.NULL ? ((var22_18 = PolyClassMachine_v4.ofGuarded((ScriptValue)var21_17)) != null ? var22_18.pg$190_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var21_17, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var0.val("contraption", var23_19);
                    var24_20 = ScriptContext.builder().copyFrom(var1_1);
                    var24_20.val("effective_rpm", (ScriptValue)(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var25_21 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var25_21, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm")));
                    Saw._sawUpdateRpmSign(var24_20);
                    var26_22 = 0.0;
                    var28_23 = ScriptValue.of((double)0.0);
                    var0.val("is_now", var28_23);
                    var30_24 = var1_1.getClassOrVar("Machine");
                    var29_26 = var30_24 != ScriptValue.NULL ? ((var31_25 = PolyClassMachine_v4.ofGuarded((ScriptValue)var30_24)) != null ? var31_25.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)var30_24, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var32_27 = "face";
                    if (var29_26 instanceof ScriptValue.Obj && (var34_29 = (var33_28 = (ScriptValue.Obj)var29_26).instance()) != null && !(var34_29 instanceof PolyClass) && var33_28.typeName().equals("Block")) {
                        var35_30 = new PolyClassBlock_v3(var34_29);
                        v4 = var35_30.tm$24_property(var32_27);
                    } else {
                        var36_31 = new ArrayList<ScriptValue>();
                        var36_31.add(ScriptValue.of((String)var32_27));
                        v4 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var29_26, var36_31, (ScriptContext)var1_1);
                    }
                    var37_32 = ScriptFormula.valuesEqualStr((ScriptValue)v4, (String)"floor");
                    var38_33 = ScriptValue.of((boolean)var37_32);
                    var0.val("is_belt_facing", var38_33);
                    if (!(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block185;
                    var39_34 = var1_1.getClassOrVar("contraption");
                    var40_35 = ScriptFormula.valuesEqual((ScriptValue)(var39_34 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var39_34, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true;
                    var41_36 = ScriptValue.of((boolean)var40_35);
                    var0.val("is_rotational", var41_36);
                    var42_37 = var1_1.getClassOrVar("contraption");
                    if (var42_37 != ScriptValue.NULL) {
                        var43_38 = new ArrayList<E>();
                        v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)var42_37, var43_38, (ScriptContext)var1_1);
                    } else {
                        v5 /* !! */  = ScriptValue.NULL;
                    }
                    var44_39 = v5 /* !! */ ;
                    var0.val("is_linear", var44_39);
                    var45_40 = (var40_35 != false || var44_39.asBool() != false) != false ? 1.0 : 0.0;
                    var47_41 = ScriptValue.of((double)var45_40);
                    var0.val("is_now", var47_41);
                    if (!(var45_40 > 0.0)) break block186;
                    if (var40_35) {
                        var48_42 = 10.0;
                        v6 = ScriptValue.of((double)Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(((var50_43 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var50_43, (ScriptContext)var1_1) : ScriptValue.NULL).asNum()) / var48_42));
                    } else {
                        var51_44 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                        var51_44.val("contraption", var23_19);
                        v6 = Utils.1._linearBreakSpeed((ScriptContext.Builder)var51_44);
                    }
                    var52_45 = v6;
                    var0.val("speed", var52_45);
                    var53_46 = var1_1.getClassOrVar("Machine");
                    var55_48 = var1_1.getClassOrVar("Machine");
                    var57_50 = ScriptFormula.addPolymorphic((ScriptValue)(var53_46 != ScriptValue.NULL ? ((var54_47 = PolyClassMachine_v4.ofGuarded((ScriptValue)var53_46)) != null ? var54_47.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var53_46, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var55_48 != ScriptValue.NULL ? ((var56_49 = PolyClassMachine_v4.ofGuarded((ScriptValue)var55_48)) != null ? var56_49.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var55_48, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("tx", var57_50);
                    var58_51 = var1_1.getClassOrVar("Machine");
                    var60_53 = var1_1.getClassOrVar("Machine");
                    var62_55 = ScriptFormula.addPolymorphic((ScriptValue)(var58_51 != ScriptValue.NULL ? ((var59_52 = PolyClassMachine_v4.ofGuarded((ScriptValue)var58_51)) != null ? var59_52.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var58_51, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var60_53 != ScriptValue.NULL ? ((var61_54 = PolyClassMachine_v4.ofGuarded((ScriptValue)var60_53)) != null ? var61_54.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var60_53, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("ty", var62_55);
                    var63_56 = var1_1.getClassOrVar("Machine");
                    var65_58 = var1_1.getClassOrVar("Machine");
                    var67_60 = ScriptFormula.addPolymorphic((ScriptValue)(var63_56 != ScriptValue.NULL ? ((var64_57 = PolyClassMachine_v4.ofGuarded((ScriptValue)var63_56)) != null ? var64_57.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var63_56, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var65_58 != ScriptValue.NULL ? ((var66_59 = PolyClassMachine_v4.ofGuarded((ScriptValue)var65_58)) != null ? var66_59.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var65_58, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("tz", var67_60);
                    var68_61 = new ArrayList<ScriptValue>();
                    var68_61.add(var57_50);
                    var68_61.add(var62_55);
                    var68_61.add(var67_60);
                    var69_62 = var1_1.getClassOrVar("contraption");
                    var70_63 = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(var69_62 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)var69_62, (ScriptContext)var1_1) : ScriptValue.NULL), var68_61, (ScriptContext)var1_1);
                    var0.val("target", (ScriptValue)var70_63);
                    if ((var37_32 != false || ScriptFormula.valuesEqual((ScriptValue)var70_63, (ScriptValue)var1_1.getClassOrVar("null")) != false) != false || ((var71_64 = var1_1.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var71_64, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
                    var72_65 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var73_66 = var1_1.getClassOrVar("target");
                    var72_65.val("id", (ScriptValue)(var73_66 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var73_66, (ScriptContext)var1_1) : ScriptValue.NULL));
                    if (!(TreeUtils._isChoppable((ScriptContext.Builder)var72_65).asBool() ^ true)) {
                        v7 = false;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v7 = true;
                    }
                    if (v7) {
                        var74_67 = var1_1.getClassOrVar("contraption");
                        if (var74_67 != ScriptValue.NULL) {
                            var75_68 = new ArrayList<ScriptValue>();
                            var75_68.add(var20_16);
                            v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var74_67, var75_68, (ScriptContext)var1_1);
                        } else {
                            v8 /* !! */  = ScriptValue.NULL;
                        }
                    } else {
                        var76_69 = var1_1.getClassOrVar("contraption");
                        if (var76_69 != ScriptValue.NULL) {
                            var77_70 = new ArrayList<ScriptValue>();
                            var77_70.add(var20_16);
                            v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var76_69, var77_70, (ScriptContext)var1_1);
                        } else {
                            v9 /* !! */  = ScriptValue.NULL;
                        }
                        var78_71 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                        var79_72 = var1_1.getClassOrVar("target");
                        var78_71.val("id", (ScriptValue)(var79_72 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var79_72, (ScriptContext)var1_1) : ScriptValue.NULL));
                        if (TreeUtils._isLog((ScriptContext.Builder)var78_71).asBool()) {
                            var80_73 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                            var80_73.val("contraption", var23_19);
                            var80_73.val("base_x", var57_50);
                            var80_73.val("base_y", var62_55);
                            var80_73.val("base_z", var67_60);
                            var80_73.val("speed", var52_45);
                            TreeUtils._fellTree((ScriptContext.Builder)var80_73);
                        } else {
                            var81_74 = var1_1.getClassOrVar("Machine");
                            if (var81_74 != ScriptValue.NULL) {
                                var82_75 = var70_63;
                                var83_76 = var52_45;
                                if (var81_74 instanceof ScriptValue.Obj && (var85_78 = (var84_77 = (ScriptValue.Obj)var81_74).instance()) != null && !(var85_78 instanceof PolyClass) && var84_77.typeName().equals("Machine")) {
                                    var86_79 = new PolyClassMachine_v4(var85_78);
                                    v10 /* !! */  = var86_79.tm$2_tick_break((ScriptValue)var82_75, var83_76.asNum());
                                } else {
                                    var87_80 = new ArrayList<CallSite>();
                                    var87_80.add(var82_75);
                                    var87_80.add((CallSite)var83_76);
                                    v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var81_74, var87_80, (ScriptContext)var1_1);
                                }
                            } else {
                                v10 /* !! */  = ScriptValue.NULL;
                            }
                            var88_81 = v10 /* !! */ ;
                            var0.val("result", var88_81);
                            if (ScriptFormula.valuesEqual((ScriptValue)var88_81, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var89_82 = ScriptProgram.elementsOf((ScriptValue)var88_81)) != null) {
                                for (ScriptValue var91_84 : var89_82) {
                                    var0.val("item", var91_84);
                                    var92_85 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                                    var92_85.val("item", var1_1.getClassOrVar("item"));
                                    Utils.1._deposit((ScriptContext.Builder)var92_85);
                                }
                            }
                        }
                        var93_86 = var1_1.getClassOrVar("Machine");
                        if (var93_86 != ScriptValue.NULL) {
                            if (var40_35) {
                                var95_87 = ScriptContext.builder().copyFrom(var1_1);
                                var96_88 = var1_1.getClassOrVar("contraption");
                                var95_87.val("current_rpm", (ScriptValue)(var96_88 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var96_88, (ScriptContext)var1_1) : ScriptValue.NULL));
                                v11 = Saw._sawSuCost(var95_87);
                            } else {
                                v11 = var94_89 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 6.0)), (ScriptValue)var52_45);
                            }
                            if (var93_86 instanceof ScriptValue.Obj && (var98_91 = (var97_90 = (ScriptValue.Obj)var93_86).instance()) != null && !(var98_91 instanceof PolyClass) && var97_90.typeName().equals("Machine")) {
                                var99_92 = new PolyClassMachine_v4(var98_91);
                                v12 /* !! */  = ScriptValue.of((boolean)var99_92.tm$56_report_su(var94_89.asNum()));
                            } else {
                                var100_93 = new ArrayList<ScriptValue>();
                                var100_93.add(var94_89);
                                v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var93_86, var100_93, (ScriptContext)var1_1);
                            }
                        } else {
                            v12 /* !! */  = ScriptValue.NULL;
                        }
                    }
                    break block187;
                }
                var101_94 = var1_1.getClassOrVar("contraption");
                if (var101_94 != ScriptValue.NULL) {
                    var102_95 = new ArrayList<ScriptValue>();
                    var102_95.add(var20_16);
                    v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var101_94, var102_95, (ScriptContext)var1_1);
                } else {
                    v13 /* !! */  = ScriptValue.NULL;
                }
                break block187;
            }
            if (!(ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true)) break block187;
            var103_96 = 10.0;
            var105_97 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(var1_1.getNum("rpm")) / var103_96);
            var107_98 = ScriptValue.of((double)var105_97);
            var0.val("speed", var107_98);
            var108_99 = var1_1.getClassOrVar("Machine");
            var110_101 = var108_99 != ScriptValue.NULL ? ((var109_100 = PolyClassMachine_v4.ofGuarded((ScriptValue)var108_99)) != null ? var109_100.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var108_99, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("fb", var110_101);
            if (!((var37_32 ^ true) != false && (((var111_102 = var1_1.getClassOrVar("fb")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var111_102, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) ** GOTO lbl-1000
            var112_103 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
            var113_104 = var1_1.getClassOrVar("fb");
            var112_103.val("id", (ScriptValue)(var113_104 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var113_104, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (TreeUtils._isChoppable((ScriptContext.Builder)var112_103).asBool()) {
                v14 = true;
            } else lbl-1000:
            // 2 sources

            {
                v14 = false;
            }
            if (v14) {
                var114_105 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                var115_106 = var1_1.getClassOrVar("fb");
                var114_105.val("id", (ScriptValue)(var115_106 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var115_106, (ScriptContext)var1_1) : ScriptValue.NULL));
                if (TreeUtils._isLog((ScriptContext.Builder)var114_105).asBool()) {
                    var116_107 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var116_107.val("contraption", var1_1.getClassOrVar("null"));
                    var117_108 = var1_1.getClassOrVar("Machine");
                    var119_110 = var1_1.getClassOrVar("Machine");
                    var116_107.val("base_x", ScriptFormula.addPolymorphic((ScriptValue)(var117_108 != ScriptValue.NULL ? ((var118_109 = PolyClassMachine_v4.ofGuarded((ScriptValue)var117_108)) != null ? var118_109.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var117_108, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var119_110 != ScriptValue.NULL ? ((var120_111 = PolyClassMachine_v4.ofGuarded((ScriptValue)var119_110)) != null ? var120_111.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var119_110, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var121_112 = var1_1.getClassOrVar("Machine");
                    var123_114 = var1_1.getClassOrVar("Machine");
                    var116_107.val("base_y", ScriptFormula.addPolymorphic((ScriptValue)(var121_112 != ScriptValue.NULL ? ((var122_113 = PolyClassMachine_v4.ofGuarded((ScriptValue)var121_112)) != null ? var122_113.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var121_112, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var123_114 != ScriptValue.NULL ? ((var124_115 = PolyClassMachine_v4.ofGuarded((ScriptValue)var123_114)) != null ? var124_115.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var123_114, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var125_116 = var1_1.getClassOrVar("Machine");
                    var127_118 = var1_1.getClassOrVar("Machine");
                    var116_107.val("base_z", ScriptFormula.addPolymorphic((ScriptValue)(var125_116 != ScriptValue.NULL ? ((var126_117 = PolyClassMachine_v4.ofGuarded((ScriptValue)var125_116)) != null ? var126_117.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var125_116, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var127_118 != ScriptValue.NULL ? ((var128_119 = PolyClassMachine_v4.ofGuarded((ScriptValue)var127_118)) != null ? var128_119.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var127_118, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var116_107.val("speed", ScriptValue.of((double)var105_97));
                    TreeUtils._fellTree((ScriptContext.Builder)var116_107);
                } else {
                    var129_120 = var1_1.getClassOrVar("Machine");
                    if (var129_120 != ScriptValue.NULL) {
                        var130_121 = var110_101;
                        var131_122 = var105_97;
                        if (var129_120 instanceof ScriptValue.Obj && (var134_124 = (var133_123 = (ScriptValue.Obj)var129_120).instance()) != null && !(var134_124 instanceof PolyClass) && var133_123.typeName().equals("Machine")) {
                            var135_125 = new PolyClassMachine_v4(var134_124);
                            v15 /* !! */  = var135_125.tm$2_tick_break(var130_121, var131_122);
                        } else {
                            var136_126 = new ArrayList<ScriptValue>();
                            var136_126.add(var130_121);
                            var136_126.add(ScriptValue.of((double)var131_122));
                            v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var129_120, var136_126, (ScriptContext)var1_1);
                        }
                    } else {
                        v15 /* !! */  = ScriptValue.NULL;
                    }
                    var137_127 = v15 /* !! */ ;
                    var0.val("result", var137_127);
                    if (ScriptFormula.valuesEqual((ScriptValue)var137_127, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var138_128 = ScriptProgram.elementsOf((ScriptValue)var137_127)) != null) {
                        for (ScriptValue var140_130 : var138_128) {
                            var0.val("item", var140_130);
                            var141_131 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var141_131.val("item", var1_1.getClassOrVar("item"));
                            Utils.1._deposit((ScriptContext.Builder)var141_131);
                        }
                    }
                }
                var142_132 = var1_1.getClassOrVar("Machine");
                if (var142_132 != ScriptValue.NULL) {
                    var144_133 = ScriptContext.builder().copyFrom(var1_1);
                    var144_133.val("current_rpm", var1_1.getClassOrVar("rpm"));
                    var143_134 = Saw._sawSuCost(var144_133);
                    if (var142_132 instanceof ScriptValue.Obj && (var146_136 = (var145_135 = (ScriptValue.Obj)var142_132).instance()) != null && !(var146_136 instanceof PolyClass) && var145_135.typeName().equals("Machine")) {
                        var147_137 = new PolyClassMachine_v4(var146_136);
                        v16 /* !! */  = ScriptValue.of((boolean)var147_137.tm$56_report_su(var143_134.asNum()));
                    } else {
                        var148_138 = new ArrayList<ScriptValue>();
                        var148_138.add(var143_134);
                        v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var142_132, var148_138, (ScriptContext)var1_1);
                    }
                } else {
                    v16 /* !! */  = ScriptValue.NULL;
                }
            }
            var149_139 = 1.0;
            var151_140 = ScriptValue.of((double)1.0);
            var0.val("is_now", var151_140);
        }
        if (var1_1.getNum("is_now") > 0.0 != false && var37_32 != false) {
            var152_141 = var1_1.getClassOrVar("Machine");
            var154_143 = var152_141 != ScriptValue.NULL ? ((var153_142 = PolyClassMachine_v4.ofGuarded((ScriptValue)var152_141)) != null ? var153_142.pg$157_belt() : PolyDispatch.bootstrapGet("memberGet", "belt", (ScriptValue)var152_141, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("belt", var154_143);
            var156_145 = ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var155_144 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var155_144, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm");
            var0.val("belt_rpm", var156_145);
            var157_146 = var1_1.getClassOrVar("Machine");
            if (var157_146 != ScriptValue.NULL) {
                var158_147 = "_saw_item_id";
                var159_148 = "str";
                if (var157_146 instanceof ScriptValue.Obj && (var161_150 = (var160_149 = (ScriptValue.Obj)var157_146).instance()) != null && !(var161_150 instanceof PolyClass) && var160_149.typeName().equals("Machine")) {
                    var162_151 = new PolyClassMachine_v4(var161_150);
                    v17 /* !! */  = var162_151.tm$34_get_typed(var158_147, var159_148);
                } else {
                    var163_152 = new ArrayList<ScriptValue>();
                    var163_152.add(ScriptValue.of((String)var158_147));
                    var163_152.add(ScriptValue.of((String)var159_148));
                    v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var157_146, var163_152, (ScriptContext)var1_1);
                }
            } else {
                v17 /* !! */  = ScriptValue.NULL;
            }
            var164_153 = v17 /* !! */ ;
            var0.val("active_id", var164_153);
            if (ScriptFormula.valuesEqual((ScriptValue)var164_153, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var165_154 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                var0.val("active_id", var165_154);
            }
            if (var1_1.getStr("active_id").equals("")) {
                var166_155 = var1_1.getClassOrVar("belt");
                if ((var166_155 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var166_155, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
                    var167_156 = var1_1.getClassOrVar("belt");
                    if (var167_156 != ScriptValue.NULL) {
                        var168_157 = new ArrayList<E>();
                        v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var167_156, var168_157, (ScriptContext)var1_1);
                    } else {
                        v18 /* !! */  = ScriptValue.NULL;
                    }
                    var169_158 = v18 /* !! */ ;
                    var0.val("carried", var169_158);
                    var170_159 = ScriptContext.builder().copyFrom(var1_1);
                    var171_160 = var1_1.getClassOrVar("carried");
                    var170_159.val("item_id", (ScriptValue)(var171_160 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var171_160, (ScriptContext)var1_1) : ScriptValue.NULL));
                    var172_161 = Saw._sawOutputFor(var170_159);
                    var0.val("out_id", var172_161);
                    var173_162 = new ArrayList<ScriptValue>();
                    var174_163 = var1_1.getClassOrVar("carried");
                    var173_162.add(ScriptValue.of((String)("[saw-debug] own-belt has_item id=" + (var174_163 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var174_163, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " out_id=" + (ScriptFormula.valuesEqual((ScriptValue)var172_161, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "null")) : var172_161).asStr())));
                    ScriptFormula.callBuiltin((String)"print", var173_162, (ScriptContext)var1_1);
                    if (ScriptFormula.valuesEqual((ScriptValue)var172_161, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var175_164 = var1_1.getClassOrVar("belt");
                        if (var175_164 != ScriptValue.NULL) {
                            var176_165 = new ArrayList<E>();
                            v19 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var175_164, var176_165, (ScriptContext)var1_1);
                        } else {
                            v19 /* !! */  = ScriptValue.NULL;
                        }
                        var177_166 = v19 /* !! */ ;
                        var0.val("taken", var177_166);
                        var178_167 = var1_1.getClassOrVar("Machine");
                        if (var178_167 != ScriptValue.NULL) {
                            var179_168 = "_saw_item_id";
                            var180_169 = "str";
                            var182_170 = var1_1.getClassOrVar("taken");
                            v20 /* !! */  = var181_171 = var182_170 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var182_170, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var178_167 instanceof ScriptValue.Obj && (var184_173 = (var183_172 = (ScriptValue.Obj)var178_167).instance()) != null && !(var184_173 instanceof PolyClass) && var183_172.typeName().equals("Machine")) {
                                var185_174 = new PolyClassMachine_v4(var184_173);
                                v21 /* !! */  = ScriptValue.of((boolean)var185_174.tm$82_set_typed(var179_168, var180_169, var181_171));
                            } else {
                                var186_175 = new ArrayList<ScriptValue>();
                                var186_175.add(ScriptValue.of((String)var179_168));
                                var186_175.add(ScriptValue.of((String)var180_169));
                                var186_175.add(var181_171);
                                v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var178_167, var186_175, (ScriptContext)var1_1);
                            }
                        } else {
                            v21 /* !! */  = ScriptValue.NULL;
                        }
                        var187_176 = var1_1.getClassOrVar("Machine");
                        if (var187_176 != ScriptValue.NULL) {
                            var188_177 = "_saw_out_id";
                            var189_178 = "str";
                            var190_179 = var172_161;
                            if (var187_176 instanceof ScriptValue.Obj && (var192_181 = (var191_180 = (ScriptValue.Obj)var187_176).instance()) != null && !(var192_181 instanceof PolyClass) && var191_180.typeName().equals("Machine")) {
                                var193_182 = new PolyClassMachine_v4(var192_181);
                                v22 /* !! */  = ScriptValue.of((boolean)var193_182.tm$82_set_typed(var188_177, var189_178, var190_179));
                            } else {
                                var194_183 = new ArrayList<ScriptValue>();
                                var194_183.add(ScriptValue.of((String)var188_177));
                                var194_183.add(ScriptValue.of((String)var189_178));
                                var194_183.add(var190_179);
                                v22 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var187_176, var194_183, (ScriptContext)var1_1);
                            }
                        } else {
                            v22 /* !! */  = ScriptValue.NULL;
                        }
                        var195_184 = var1_1.getClassOrVar("Machine");
                        if (var195_184 != ScriptValue.NULL) {
                            var196_185 = "_saw_count";
                            var197_186 = "int";
                            var199_187 = var1_1.getClassOrVar("taken");
                            v23 /* !! */  = var198_188 = var199_187 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var199_187, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var195_184 instanceof ScriptValue.Obj && (var201_190 = (var200_189 = (ScriptValue.Obj)var195_184).instance()) != null && !(var201_190 instanceof PolyClass) && var200_189.typeName().equals("Machine")) {
                                var202_191 = new PolyClassMachine_v4(var201_190);
                                v24 /* !! */  = ScriptValue.of((boolean)var202_191.tm$82_set_typed(var196_185, var197_186, var198_188));
                            } else {
                                var203_192 = new ArrayList<ScriptValue>();
                                var203_192.add(ScriptValue.of((String)var196_185));
                                var203_192.add(ScriptValue.of((String)var197_186));
                                var203_192.add(var198_188);
                                v24 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var195_184, var203_192, (ScriptContext)var1_1);
                            }
                        } else {
                            v24 /* !! */  = ScriptValue.NULL;
                        }
                        var204_193 = var1_1.getClassOrVar("Machine");
                        if (var204_193 != ScriptValue.NULL) {
                            var205_194 = "_saw_progress";
                            var206_195 = "int";
                            var207_196 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            if (var204_193 instanceof ScriptValue.Obj && (var209_198 = (var208_197 = (ScriptValue.Obj)var204_193).instance()) != null && !(var209_198 instanceof PolyClass) && var208_197.typeName().equals("Machine")) {
                                var210_199 = new PolyClassMachine_v4(var209_198);
                                v25 /* !! */  = ScriptValue.of((boolean)var210_199.tm$82_set_typed(var205_194, var206_195, var207_196));
                            } else {
                                var211_200 = new ArrayList<ScriptValue>();
                                var211_200.add(ScriptValue.of((String)var205_194));
                                var211_200.add(ScriptValue.of((String)var206_195));
                                var211_200.add(var207_196);
                                v25 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var204_193, var211_200, (ScriptContext)var1_1);
                            }
                        } else {
                            v25 /* !! */  = ScriptValue.NULL;
                        }
                        var212_201 = var1_1.getClassOrVar("Machine");
                        if (var212_201 != ScriptValue.NULL) {
                            var213_202 = "_saw_from_ground";
                            var214_203 = "int";
                            var215_204 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            if (var212_201 instanceof ScriptValue.Obj && (var217_206 = (var216_205 = (ScriptValue.Obj)var212_201).instance()) != null && !(var217_206 instanceof PolyClass) && var216_205.typeName().equals("Machine")) {
                                var218_207 = new PolyClassMachine_v4(var217_206);
                                v26 /* !! */  = ScriptValue.of((boolean)var218_207.tm$82_set_typed(var213_202, var214_203, var215_204));
                            } else {
                                var219_208 = new ArrayList<ScriptValue>();
                                var219_208.add(ScriptValue.of((String)var213_202));
                                var219_208.add(ScriptValue.of((String)var214_203));
                                var219_208.add(var215_204);
                                v26 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var212_201, var219_208, (ScriptContext)var1_1);
                            }
                        } else {
                            v26 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var220_209 = ScriptContext.builder().copyFrom(var1_1);
                    var221_210 = Saw._sawInputFace(var220_209);
                    var0.val("in_face", var221_210);
                    var222_211 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                    var222_211.val("face", var221_210);
                    var222_211.val("validator", var1_1.getClassOrVar("null"));
                    var222_211.val("amount", var1_1.getClassOrVar("null"));
                    var223_212 = BeltUtils.beltTake((ScriptContext.Builder)var222_211);
                    var0.val("taken", var223_212);
                    var224_213 = new ArrayList<ScriptValue>();
                    var224_213.add(var223_212);
                    if (ScriptFormula.callBuiltin((String)"is_empty", var224_213, (ScriptContext)var1_1).asBool() ^ true) {
                        var225_214 = ScriptContext.builder().copyFrom(var1_1);
                        var226_215 = var1_1.getClassOrVar("taken");
                        var225_214.val("item_id", (ScriptValue)(var226_215 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var226_215, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var227_216 = Saw._sawOutputFor(var225_214);
                        var0.val("out_id", var227_216);
                        if (ScriptFormula.valuesEqual((ScriptValue)var227_216, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var228_217 = var1_1.getClassOrVar("Machine");
                            if (var228_217 != ScriptValue.NULL) {
                                var229_218 = "_saw_item_id";
                                var230_219 = "str";
                                var232_220 = var1_1.getClassOrVar("taken");
                                v27 /* !! */  = var231_221 = var232_220 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var232_220, (ScriptContext)var1_1) : ScriptValue.NULL;
                                if (var228_217 instanceof ScriptValue.Obj && (var234_223 = (var233_222 = (ScriptValue.Obj)var228_217).instance()) != null && !(var234_223 instanceof PolyClass) && var233_222.typeName().equals("Machine")) {
                                    var235_224 = new PolyClassMachine_v4(var234_223);
                                    v28 /* !! */  = ScriptValue.of((boolean)var235_224.tm$82_set_typed(var229_218, var230_219, var231_221));
                                } else {
                                    var236_225 = new ArrayList<ScriptValue>();
                                    var236_225.add(ScriptValue.of((String)var229_218));
                                    var236_225.add(ScriptValue.of((String)var230_219));
                                    var236_225.add(var231_221);
                                    v28 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var228_217, var236_225, (ScriptContext)var1_1);
                                }
                            } else {
                                v28 /* !! */  = ScriptValue.NULL;
                            }
                            var237_226 = var1_1.getClassOrVar("Machine");
                            if (var237_226 != ScriptValue.NULL) {
                                var238_227 = "_saw_out_id";
                                var239_228 = "str";
                                var240_229 = var227_216;
                                if (var237_226 instanceof ScriptValue.Obj && (var242_231 = (var241_230 = (ScriptValue.Obj)var237_226).instance()) != null && !(var242_231 instanceof PolyClass) && var241_230.typeName().equals("Machine")) {
                                    var243_232 = new PolyClassMachine_v4(var242_231);
                                    v29 /* !! */  = ScriptValue.of((boolean)var243_232.tm$82_set_typed(var238_227, var239_228, var240_229));
                                } else {
                                    var244_233 = new ArrayList<ScriptValue>();
                                    var244_233.add(ScriptValue.of((String)var238_227));
                                    var244_233.add(ScriptValue.of((String)var239_228));
                                    var244_233.add(var240_229);
                                    v29 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var237_226, var244_233, (ScriptContext)var1_1);
                                }
                            } else {
                                v29 /* !! */  = ScriptValue.NULL;
                            }
                            var245_234 = var1_1.getClassOrVar("Machine");
                            if (var245_234 != ScriptValue.NULL) {
                                var246_235 = "_saw_count";
                                var247_236 = "int";
                                var249_237 = new ArrayList<ScriptValue>();
                                var249_237.add(var223_212);
                                var248_238 = ScriptFormula.callBuiltin((String)"item_count", var249_237, (ScriptContext)var1_1);
                                if (var245_234 instanceof ScriptValue.Obj && (var251_240 = (var250_239 = (ScriptValue.Obj)var245_234).instance()) != null && !(var251_240 instanceof PolyClass) && var250_239.typeName().equals("Machine")) {
                                    var252_241 = new PolyClassMachine_v4(var251_240);
                                    v30 /* !! */  = ScriptValue.of((boolean)var252_241.tm$82_set_typed(var246_235, var247_236, var248_238));
                                } else {
                                    var253_242 = new ArrayList<ScriptValue>();
                                    var253_242.add(ScriptValue.of((String)var246_235));
                                    var253_242.add(ScriptValue.of((String)var247_236));
                                    var253_242.add(var248_238);
                                    v30 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var245_234, var253_242, (ScriptContext)var1_1);
                                }
                            } else {
                                v30 /* !! */  = ScriptValue.NULL;
                            }
                            var254_243 = var1_1.getClassOrVar("Machine");
                            if (var254_243 != ScriptValue.NULL) {
                                var255_244 = "_saw_progress";
                                var256_245 = "int";
                                var257_246 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                if (var254_243 instanceof ScriptValue.Obj && (var259_248 = (var258_247 = (ScriptValue.Obj)var254_243).instance()) != null && !(var259_248 instanceof PolyClass) && var258_247.typeName().equals("Machine")) {
                                    var260_249 = new PolyClassMachine_v4(var259_248);
                                    v31 /* !! */  = ScriptValue.of((boolean)var260_249.tm$82_set_typed(var255_244, var256_245, var257_246));
                                } else {
                                    var261_250 = new ArrayList<ScriptValue>();
                                    var261_250.add(ScriptValue.of((String)var255_244));
                                    var261_250.add(ScriptValue.of((String)var256_245));
                                    var261_250.add(var257_246);
                                    v31 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var254_243, var261_250, (ScriptContext)var1_1);
                                }
                            } else {
                                v31 /* !! */  = ScriptValue.NULL;
                            }
                            var262_251 = var1_1.getClassOrVar("Machine");
                            if (var262_251 != ScriptValue.NULL) {
                                var263_252 = "_saw_from_ground";
                                var264_253 = "int";
                                var265_254 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0);
                                if (var262_251 instanceof ScriptValue.Obj && (var267_256 = (var266_255 = (ScriptValue.Obj)var262_251).instance()) != null && !(var267_256 instanceof PolyClass) && var266_255.typeName().equals("Machine")) {
                                    var268_257 = new PolyClassMachine_v4(var267_256);
                                    v32 /* !! */  = ScriptValue.of((boolean)var268_257.tm$82_set_typed(var263_252, var264_253, var265_254));
                                } else {
                                    var269_258 = new ArrayList<ScriptValue>();
                                    var269_258.add(ScriptValue.of((String)var263_252));
                                    var269_258.add(ScriptValue.of((String)var264_253));
                                    var269_258.add(var265_254);
                                    v32 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var262_251, var269_258, (ScriptContext)var1_1);
                                }
                            } else {
                                v32 /* !! */  = ScriptValue.NULL;
                            }
                        } else {
                            var270_259 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                            var270_259.val("face", var221_210);
                            var270_259.val("item", var223_212);
                            BeltUtils.beltGive((ScriptContext.Builder)var270_259);
                        }
                    } else {
                        var274_260 = var1_1.getClassOrVar("Machine");
                        if (var274_260 != ScriptValue.NULL) {
                            var275_261 = 0.7;
                            if (var274_260 instanceof ScriptValue.Obj && (var278_263 = (var277_262 = (ScriptValue.Obj)var274_260).instance()) != null && !(var278_263 instanceof PolyClass) && var277_262.typeName().equals("Machine")) {
                                var279_264 = new PolyClassMachine_v4(var278_263);
                                v33 /* !! */  = var279_264.tm$94_nearby_entities(var275_261);
                            } else {
                                var280_265 = new ArrayList<ScriptValue>();
                                var280_265.add(ScriptValue.of((double)var275_261));
                                v33 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var274_260, var280_265, (ScriptContext)var1_1);
                            }
                        } else {
                            v33 /* !! */  = ScriptValue.NULL;
                        }
                        var271_266 = ScriptProgram.elementsOf((ScriptValue)v33 /* !! */ );
                        if (var271_266 != null) {
                            for (ScriptValue var273_268 : var271_266) {
                                var0.val("entity", var273_268);
                                var281_269 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                                var281_269.val("entity", var1_1.getClassOrVar("entity"));
                                if (!Utils.isRestingItem(var281_269).asBool()) continue;
                                var282_270 = ScriptContext.builder().copyFrom(var1_1);
                                var283_271 = var1_1.getClassOrVar("entity");
                                var282_270.val("item_id", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var283_271 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var283_271, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                                var284_272 = Saw._sawOutputFor(var282_270);
                                var0.val("out_id", var284_272);
                                if (!(ScriptFormula.valuesEqual((ScriptValue)var284_272, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                                var285_273 = var1_1.getClassOrVar("Machine");
                                if (var285_273 != ScriptValue.NULL) {
                                    var286_274 = "_saw_item_id";
                                    var287_275 = "str";
                                    var289_276 = var1_1.getClassOrVar("entity");
                                    var288_277 = PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var289_276 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var289_276, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    if (var285_273 instanceof ScriptValue.Obj && (var291_279 = (var290_278 = (ScriptValue.Obj)var285_273).instance()) != null && !(var291_279 instanceof PolyClass) && var290_278.typeName().equals("Machine")) {
                                        var292_280 = new PolyClassMachine_v4(var291_279);
                                        v34 /* !! */  = ScriptValue.of((boolean)var292_280.tm$82_set_typed(var286_274, var287_275, (ScriptValue)var288_277));
                                    } else {
                                        var293_281 = new ArrayList<Object>();
                                        var293_281.add(ScriptValue.of((String)var286_274));
                                        var293_281.add(ScriptValue.of((String)var287_275));
                                        var293_281.add(var288_277);
                                        v34 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var285_273, var293_281, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v34 /* !! */  = ScriptValue.NULL;
                                }
                                var294_282 = var1_1.getClassOrVar("Machine");
                                if (var294_282 != ScriptValue.NULL) {
                                    var295_283 = "_saw_out_id";
                                    var296_284 = "str";
                                    var297_285 = var284_272;
                                    if (var294_282 instanceof ScriptValue.Obj && (var299_287 = (var298_286 = (ScriptValue.Obj)var294_282).instance()) != null && !(var299_287 instanceof PolyClass) && var298_286.typeName().equals("Machine")) {
                                        var300_288 = new PolyClassMachine_v4(var299_287);
                                        v35 /* !! */  = ScriptValue.of((boolean)var300_288.tm$82_set_typed(var295_283, var296_284, var297_285));
                                    } else {
                                        var301_289 = new ArrayList<ScriptValue>();
                                        var301_289.add(ScriptValue.of((String)var295_283));
                                        var301_289.add(ScriptValue.of((String)var296_284));
                                        var301_289.add(var297_285);
                                        v35 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var294_282, var301_289, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v35 /* !! */  = ScriptValue.NULL;
                                }
                                var302_290 = var1_1.getClassOrVar("Machine");
                                if (var302_290 != ScriptValue.NULL) {
                                    var303_291 = "_saw_count";
                                    var304_292 = "int";
                                    var306_293 = new ArrayList<ScriptValue>();
                                    var307_294 = var1_1.getClassOrVar("entity");
                                    var306_293.add((ScriptValue)(var307_294 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var307_294, (ScriptContext)var1_1) : ScriptValue.NULL));
                                    var305_295 = ScriptFormula.callBuiltin((String)"item_count", var306_293, (ScriptContext)var1_1);
                                    if (var302_290 instanceof ScriptValue.Obj && (var309_297 = (var308_296 = (ScriptValue.Obj)var302_290).instance()) != null && !(var309_297 instanceof PolyClass) && var308_296.typeName().equals("Machine")) {
                                        var310_298 = new PolyClassMachine_v4(var309_297);
                                        v36 /* !! */  = ScriptValue.of((boolean)var310_298.tm$82_set_typed(var303_291, var304_292, var305_295));
                                    } else {
                                        var311_299 = new ArrayList<ScriptValue>();
                                        var311_299.add(ScriptValue.of((String)var303_291));
                                        var311_299.add(ScriptValue.of((String)var304_292));
                                        var311_299.add(var305_295);
                                        v36 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var302_290, var311_299, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v36 /* !! */  = ScriptValue.NULL;
                                }
                                var312_300 = var1_1.getClassOrVar("Machine");
                                if (var312_300 != ScriptValue.NULL) {
                                    var313_301 = "_saw_progress";
                                    var314_302 = "int";
                                    var315_303 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                    if (var312_300 instanceof ScriptValue.Obj && (var317_305 = (var316_304 = (ScriptValue.Obj)var312_300).instance()) != null && !(var317_305 instanceof PolyClass) && var316_304.typeName().equals("Machine")) {
                                        var318_306 = new PolyClassMachine_v4(var317_305);
                                        v37 /* !! */  = ScriptValue.of((boolean)var318_306.tm$82_set_typed(var313_301, var314_302, var315_303));
                                    } else {
                                        var319_307 = new ArrayList<ScriptValue>();
                                        var319_307.add(ScriptValue.of((String)var313_301));
                                        var319_307.add(ScriptValue.of((String)var314_302));
                                        var319_307.add(var315_303);
                                        v37 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var312_300, var319_307, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v37 /* !! */  = ScriptValue.NULL;
                                }
                                var320_308 = var1_1.getClassOrVar("Machine");
                                if (var320_308 != ScriptValue.NULL) {
                                    var321_309 = "_saw_from_ground";
                                    var322_310 = "int";
                                    var323_311 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0);
                                    if (var320_308 instanceof ScriptValue.Obj && (var325_313 = (var324_312 = (ScriptValue.Obj)var320_308).instance()) != null && !(var325_313 instanceof PolyClass) && var324_312.typeName().equals("Machine")) {
                                        var326_314 = new PolyClassMachine_v4(var325_313);
                                        v38 /* !! */  = ScriptValue.of((boolean)var326_314.tm$82_set_typed(var321_309, var322_310, var323_311));
                                    } else {
                                        var327_315 = new ArrayList<ScriptValue>();
                                        var327_315.add(ScriptValue.of((String)var321_309));
                                        var327_315.add(ScriptValue.of((String)var322_310));
                                        var327_315.add(var323_311);
                                        v38 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var320_308, var327_315, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v38 /* !! */  = ScriptValue.NULL;
                                }
                                var328_316 = var1_1.getClassOrVar("entity");
                                if (var328_316 != ScriptValue.NULL) {
                                    var329_317 = new ArrayList<E>();
                                    v39 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var328_316, var329_317, (ScriptContext)var1_1);
                                } else {
                                    v39 /* !! */  = ScriptValue.NULL;
                                }
                                break;
                            }
                        }
                    }
                }
            } else {
                var330_318 = var1_1.getClassOrVar("Machine");
                if (var330_318 != ScriptValue.NULL) {
                    var331_319 = "_saw_progress";
                    var332_320 = "int";
                    if (var330_318 instanceof ScriptValue.Obj && (var334_322 = (var333_321 = (ScriptValue.Obj)var330_318).instance()) != null && !(var334_322 instanceof PolyClass) && var333_321.typeName().equals("Machine")) {
                        var335_323 = new PolyClassMachine_v4(var334_322);
                        v40 /* !! */  = var335_323.tm$34_get_typed(var331_319, var332_320);
                    } else {
                        var336_324 = new ArrayList<ScriptValue>();
                        var336_324.add(ScriptValue.of((String)var331_319));
                        var336_324.add(ScriptValue.of((String)var332_320));
                        v40 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var330_318, var336_324, (ScriptContext)var1_1);
                    }
                } else {
                    v40 /* !! */  = ScriptValue.NULL;
                }
                var337_325 = v40 /* !! */ ;
                var0.val("progress", var337_325);
                if (ScriptFormula.valuesEqual((ScriptValue)var337_325, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var338_326 = 0.0;
                    var340_327 = ScriptValue.of((double)0.0);
                    var0.val("progress", var340_327);
                }
                var341_328 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)Math.abs(var156_145.asNum())));
                var0.val("progress", var341_328);
                var342_329 = var1_1.getClassOrVar("Machine");
                if (var342_329 != ScriptValue.NULL) {
                    var344_330 = ScriptContext.builder().copyFrom(var1_1);
                    var344_330.val("current_rpm", var156_145);
                    var343_331 = Saw._sawSuCost(var344_330);
                    if (var342_329 instanceof ScriptValue.Obj && (var346_333 = (var345_332 = (ScriptValue.Obj)var342_329).instance()) != null && !(var346_333 instanceof PolyClass) && var345_332.typeName().equals("Machine")) {
                        var347_334 = new PolyClassMachine_v4(var346_333);
                        v41 /* !! */  = ScriptValue.of((boolean)var347_334.tm$56_report_su(var343_331.asNum()));
                    } else {
                        var348_335 = new ArrayList<ScriptValue>();
                        var348_335.add(var343_331);
                        v41 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var342_329, var348_335, (ScriptContext)var1_1);
                    }
                } else {
                    v41 /* !! */  = ScriptValue.NULL;
                }
                if (var341_328.asNum() >= var11_8) {
                    var349_336 = var1_1.getClassOrVar("Machine");
                    if (var349_336 != ScriptValue.NULL) {
                        var350_337 = "_saw_out_id";
                        var351_338 = "str";
                        if (var349_336 instanceof ScriptValue.Obj && (var353_340 = (var352_339 = (ScriptValue.Obj)var349_336).instance()) != null && !(var353_340 instanceof PolyClass) && var352_339.typeName().equals("Machine")) {
                            var354_341 = new PolyClassMachine_v4(var353_340);
                            v42 /* !! */  = var354_341.tm$34_get_typed(var350_337, var351_338);
                        } else {
                            var355_342 = new ArrayList<ScriptValue>();
                            var355_342.add(ScriptValue.of((String)var350_337));
                            var355_342.add(ScriptValue.of((String)var351_338));
                            v42 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var349_336, var355_342, (ScriptContext)var1_1);
                        }
                    } else {
                        v42 /* !! */  = ScriptValue.NULL;
                    }
                    var356_343 = v42 /* !! */ ;
                    var0.val("out_id", var356_343);
                    var357_344 = var1_1.getClassOrVar("Machine");
                    if (var357_344 != ScriptValue.NULL) {
                        var358_345 = "_saw_count";
                        var359_346 = "int";
                        if (var357_344 instanceof ScriptValue.Obj && (var361_348 = (var360_347 = (ScriptValue.Obj)var357_344).instance()) != null && !(var361_348 instanceof PolyClass) && var360_347.typeName().equals("Machine")) {
                            var362_349 = new PolyClassMachine_v4(var361_348);
                            v43 /* !! */  = var362_349.tm$34_get_typed(var358_345, var359_346);
                        } else {
                            var363_350 = new ArrayList<ScriptValue>();
                            var363_350.add(ScriptValue.of((String)var358_345));
                            var363_350.add(ScriptValue.of((String)var359_346));
                            v43 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var357_344, var363_350, (ScriptContext)var1_1);
                        }
                    } else {
                        v43 /* !! */  = ScriptValue.NULL;
                    }
                    var364_351 = v43 /* !! */ ;
                    var0.val("remaining", var364_351);
                    if (ScriptFormula.valuesEqual((ScriptValue)var364_351, (ScriptValue)var1_1.getClassOrVar("null")) != false || var364_351.asNum() <= 0.0 != false) {
                        var365_352 = 1.0;
                        var367_353 = ScriptValue.of((double)1.0);
                        var0.val("remaining", var367_353);
                    }
                    if ((var368_354 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        var369_355 = "_saw_from_ground";
                        var370_356 = "int";
                        if (var368_354 instanceof ScriptValue.Obj && (var372_358 = (var371_357 = (ScriptValue.Obj)var368_354).instance()) != null && !(var372_358 instanceof PolyClass) && var371_357.typeName().equals("Machine")) {
                            var373_359 = new PolyClassMachine_v4(var372_358);
                            v44 /* !! */  = var373_359.tm$34_get_typed(var369_355, var370_356);
                        } else {
                            var374_360 = new ArrayList<ScriptValue>();
                            var374_360.add(ScriptValue.of((String)var369_355));
                            var374_360.add(ScriptValue.of((String)var370_356));
                            v44 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var368_354, var374_360, (ScriptContext)var1_1);
                        }
                    } else {
                        v44 /* !! */  = ScriptValue.NULL;
                    }
                    var375_361 = v44 /* !! */ ;
                    var0.val("from_ground", var375_361);
                    var376_362 = ScriptContext.builder().copyFrom(var1_1);
                    var376_362.val("item_id", var1_1.getClassOrVar("active_id"));
                    var376_362.val("out_id", var356_343);
                    var377_363 = Saw._sawRecipeFor(var376_362);
                    var0.val("recipe", var377_363);
                    if (ScriptFormula.valuesEqual((ScriptValue)var377_363, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var378_364 = var1_1.getClassOrVar("recipe");
                        if (var378_364 != ScriptValue.NULL) {
                            var379_365 = new ArrayList<E>();
                            v45 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "outputs", (ScriptValue)var378_364, var379_365, (ScriptContext)var1_1);
                        } else {
                            v45 /* !! */  = ScriptValue.NULL;
                        }
                    } else {
                        v45 /* !! */  = var1_1.getClassOrVar("null");
                    }
                    var380_366 = v45 /* !! */ ;
                    var0.val("result", var380_366);
                    if (ScriptFormula.valuesEqual((ScriptValue)var380_366, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var381_367 = var1_1.getClassOrVar("result");
                        v46 /* !! */  = var381_367 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "items", (ScriptValue)var381_367, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var382_368 = new ArrayList<ScriptValue>();
                        var383_369 = new ArrayList<ScriptValue>();
                        var383_369.add(var356_343);
                        var383_369.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0));
                        var382_368.add(ScriptFormula.callBuiltin((String)"MinecraftItem", var383_369, (ScriptContext)var1_1));
                        v46 /* !! */  = new ScriptValue.Array(var382_368);
                    }
                    var384_370 /* !! */  = v46 /* !! */ ;
                    var0.val("items", var384_370 /* !! */ );
                    var385_371 = ScriptProgram.elementsOf((ScriptValue)var384_370 /* !! */ );
                    if (var385_371 != null) {
                        for (ScriptValue var387_373 : var385_371) {
                            var0.val("out_item", var387_373);
                            if (ScriptFormula.valuesEqual((ScriptValue)var375_361, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0))) != false || ScriptFormula.valuesEqual((ScriptValue)var375_361, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0))) != false) {
                                var388_374 = ScriptContext.builder().copyFrom(var1_1);
                                var388_374.val("item", var1_1.getClassOrVar("out_item"));
                                Saw._sawDepositDirectional(var388_374);
                                continue;
                            }
                            var389_375 = var1_1.getClassOrVar("belt");
                            if (var389_375 != ScriptValue.NULL) {
                                var390_376 = new ArrayList<ScriptValue>();
                                var390_376.add(var1_1.getClassOrVar("out_item"));
                                v47 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var389_375, var390_376, (ScriptContext)var1_1);
                            } else {
                                v47 /* !! */  = ScriptValue.NULL;
                            }
                            var391_377 = v47 /* !! */ ;
                            var0.val("leftover", var391_377);
                            var392_378 = new ArrayList<ScriptValue>();
                            var392_378.add(var391_377);
                            if (!(ScriptFormula.callBuiltin((String)"is_empty", var392_378, (ScriptContext)var1_1).asBool() ^ true)) continue;
                            var393_379 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var393_379.val("item", var391_377);
                            Utils.1._deposit((ScriptContext.Builder)var393_379);
                        }
                    }
                    var394_380 = var1_1.getNum("remaining") - 1.0;
                    var396_381 = ScriptValue.of((double)var394_380);
                    var0.val("remaining", var396_381);
                    var397_382 = var1_1.getClassOrVar("Machine");
                    if (var397_382 != ScriptValue.NULL) {
                        var398_383 = "_saw_count";
                        var399_384 = "int";
                        var400_385 = ScriptValue.of((double)var394_380);
                        if (var397_382 instanceof ScriptValue.Obj && (var402_387 = (var401_386 = (ScriptValue.Obj)var397_382).instance()) != null && !(var402_387 instanceof PolyClass) && var401_386.typeName().equals("Machine")) {
                            var403_388 = new PolyClassMachine_v4(var402_387);
                            v48 /* !! */  = ScriptValue.of((boolean)var403_388.tm$82_set_typed(var398_383, var399_384, var400_385));
                        } else {
                            var404_389 = new ArrayList<ScriptValue>();
                            var404_389.add(ScriptValue.of((String)var398_383));
                            var404_389.add(ScriptValue.of((String)var399_384));
                            var404_389.add(var400_385);
                            v48 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var397_382, var404_389, (ScriptContext)var1_1);
                        }
                    } else {
                        v48 /* !! */  = ScriptValue.NULL;
                    }
                    var405_390 = var1_1.getClassOrVar("Machine");
                    if (var405_390 != ScriptValue.NULL) {
                        var406_391 = "_saw_progress";
                        var407_392 = "int";
                        var408_393 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                        if (var405_390 instanceof ScriptValue.Obj && (var410_395 = (var409_394 = (ScriptValue.Obj)var405_390).instance()) != null && !(var410_395 instanceof PolyClass) && var409_394.typeName().equals("Machine")) {
                            var411_396 = new PolyClassMachine_v4(var410_395);
                            v49 /* !! */  = ScriptValue.of((boolean)var411_396.tm$82_set_typed(var406_391, var407_392, var408_393));
                        } else {
                            var412_397 = new ArrayList<ScriptValue>();
                            var412_397.add(ScriptValue.of((String)var406_391));
                            var412_397.add(ScriptValue.of((String)var407_392));
                            var412_397.add(var408_393);
                            v49 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var405_390, var412_397, (ScriptContext)var1_1);
                        }
                    } else {
                        v49 /* !! */  = ScriptValue.NULL;
                    }
                    if (var394_380 <= 0.0) {
                        var413_398 = var1_1.getClassOrVar("Machine");
                        if (var413_398 != ScriptValue.NULL) {
                            var414_399 = "_saw_item_id";
                            var415_400 = "str";
                            var416_401 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                            if (var413_398 instanceof ScriptValue.Obj && (var418_403 = (var417_402 = (ScriptValue.Obj)var413_398).instance()) != null && !(var418_403 instanceof PolyClass) && var417_402.typeName().equals("Machine")) {
                                var419_404 = new PolyClassMachine_v4(var418_403);
                                v50 /* !! */  = ScriptValue.of((boolean)var419_404.tm$82_set_typed(var414_399, var415_400, var416_401));
                            } else {
                                var420_405 = new ArrayList<ScriptValue>();
                                var420_405.add(ScriptValue.of((String)var414_399));
                                var420_405.add(ScriptValue.of((String)var415_400));
                                var420_405.add(var416_401);
                                v50 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var413_398, var420_405, (ScriptContext)var1_1);
                            }
                        } else {
                            v50 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var421_406 = var1_1.getClassOrVar("Machine");
                    if (var421_406 != ScriptValue.NULL) {
                        var422_407 = "_saw_progress";
                        var423_408 = "int";
                        var424_409 = var341_328;
                        if (var421_406 instanceof ScriptValue.Obj && (var426_411 = (var425_410 = (ScriptValue.Obj)var421_406).instance()) != null && !(var426_411 instanceof PolyClass) && var425_410.typeName().equals("Machine")) {
                            var427_412 = new PolyClassMachine_v4(var426_411);
                            v51 /* !! */  = ScriptValue.of((boolean)var427_412.tm$82_set_typed(var422_407, var423_408, var424_409));
                        } else {
                            var428_413 = new ArrayList<ScriptValue>();
                            var428_413.add(ScriptValue.of((String)var422_407));
                            var428_413.add(ScriptValue.of((String)var423_408));
                            var428_413.add(var424_409);
                            v51 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var421_406, var428_413, (ScriptContext)var1_1);
                        }
                    } else {
                        v51 /* !! */  = ScriptValue.NULL;
                    }
                }
            }
        }
        var429_414 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var429_414.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "_saw_act"));
        var429_414.val("is_now", var1_1.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)var429_414);
        Saw.FILE_SCOPE = var0.build();
    }
}
