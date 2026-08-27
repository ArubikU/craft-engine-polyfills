/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBelt
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassContraptionWorld
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyClassRecipe
 *  dev.arubik.craftengine.script.PolyClassRecipeRegistry
 *  dev.arubik.craftengine.script.PolyClassRegistry
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.gen.BeltUtils
 *  dev.arubik.craftengine.script.gen.TreeUtils
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClassBelt;
import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassContraptionWorld;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyClassRecipe;
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
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)0.0);
        builder.val("count", scriptValue2);
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        List list = polyClassMachine != null ? polyClassMachine.tl$147_recipes() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
        ScriptValue scriptValue3 = ScriptValue.of((double)d);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("ins");
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                PolyClassRecipe polyClassRecipe;
                builder.val("r", scriptValue5);
                ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? ((polyClassRecipe = PolyClassRecipe.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassRecipe.pg$7_inputs() : PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("ins", scriptValue6);
                scriptValue4 = scriptValue6;
                if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                ScriptValue scriptValue7 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0)));
                builder.val("count", scriptValue7);
                scriptValue3 = scriptValue7;
            }
        }
        return scriptValue3;
    }

    public static ScriptValue _sawRecipeAt(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue2);
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        List list = polyClassMachine != null ? polyClassMachine.tl$147_recipes() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
        ScriptValue scriptValue3 = ScriptValue.of((double)d);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("ins");
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                PolyClassRecipe polyClassRecipe;
                builder.val("r", scriptValue5);
                ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? ((polyClassRecipe = PolyClassRecipe.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassRecipe.pg$7_inputs() : PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("ins", scriptValue6);
                scriptValue4 = scriptValue6;
                if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("target_idx"))) {
                    return scriptValue5;
                }
                ScriptValue scriptValue7 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0)));
                builder.val("idx", scriptValue7);
                scriptValue3 = scriptValue7;
            }
        }
        return scriptContext.getClassOrVar("null");
    }

    public static ScriptValue _sawRecipeFor(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassRegistry polyClassRegistry;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        List list = polyClassMachine != null ? polyClassMachine.tl$147_recipes() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("outs");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("ins");
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                PolyClassRecipe polyClassRecipe;
                PolyClassRecipe polyClassRecipe2;
                builder.val("r", scriptValue5);
                ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? ((polyClassRecipe2 = PolyClassRecipe.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassRecipe2.pg$7_inputs() : PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("ins", scriptValue6);
                scriptValue4 = scriptValue6;
                ScriptValue scriptValue7 = scriptValue5 != ScriptValue.NULL ? ((polyClassRecipe = PolyClassRecipe.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassRecipe.pg$4_outputs() : PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                builder.val("outs", scriptValue7);
                scriptValue3 = scriptValue7;
                if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")) && ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("out_id")))) continue;
                return scriptValue5;
            }
        }
        ScriptValue scriptValue8 = (polyClassRegistry = PolyClassRegistry.ofVar((ScriptContext)scriptContext, (String)"Registry")) != null ? polyClassRegistry.pg$0_recipes() : ((scriptValue = scriptContext.getClassOrVar("Registry")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        PolyClassRecipeRegistry polyClassRecipeRegistry = PolyClassRecipeRegistry.ofGuarded((ScriptValue)scriptValue8);
        List list2 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)(polyClassRecipeRegistry != null ? polyClassRecipeRegistry.pg$2_stonecutter() : PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)), (ScriptValue)scriptContext.getClassOrVar("item_id"), (ScriptContext)scriptContext));
        ScriptValue scriptValue9 = scriptValue3;
        if (list2 != null) {
            for (ScriptValue scriptValue10 : list2) {
                builder.val("r", scriptValue10);
                ScriptValue scriptValue11 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("outs", scriptValue11);
                scriptValue9 = scriptValue11;
                if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue9, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("out_id")))) continue;
                return scriptValue10;
            }
        }
        return scriptContext.getClassOrVar("null");
    }

    public static ScriptValue _sawOutputFor(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "_saw_filter";
            String string2 = "str";
            PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("filter_id", scriptValue3);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("null"))) {
            ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
            builder.val("filter_id", scriptValue4);
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("item_id", scriptContext.getClassOrVar("item_id"));
        ScriptValue scriptValue5 = Saw._sawRecipeCount(builder2);
        builder.val("count", scriptValue5);
        if (scriptValue5.asNum() > 0.0) {
            double d;
            Object object2;
            ScriptValue scriptValue6;
            if (scriptContext.getStr("filter_id").equals("") ^ true) {
                ScriptValue scriptValue7;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                List list = polyClassMachine != null ? polyClassMachine.tl$147_recipes() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("outs");
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("ins");
                if (list != null) {
                    for (ScriptValue scriptValue10 : list) {
                        PolyClassRecipe polyClassRecipe;
                        PolyClassRecipe polyClassRecipe2;
                        builder.val("r", scriptValue10);
                        ScriptValue scriptValue11 = scriptValue10 != ScriptValue.NULL ? ((polyClassRecipe2 = PolyClassRecipe.ofGuarded((ScriptValue)scriptValue10)) != null ? polyClassRecipe2.pg$7_inputs() : PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue10, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                        builder.val("ins", scriptValue11);
                        scriptValue9 = scriptValue11;
                        if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue9, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                        ScriptValue scriptValue12 = scriptValue10 != ScriptValue.NULL ? ((polyClassRecipe = PolyClassRecipe.ofGuarded((ScriptValue)scriptValue10)) != null ? polyClassRecipe.pg$4_outputs() : PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue10, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                        builder.val("outs", scriptValue12);
                        scriptValue8 = scriptValue12;
                        if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
                        return scriptContext.getClassOrVar("filter_id");
                    }
                }
            }
            if ((scriptValue6 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                String string = "_saw_recipe_index";
                String string3 = "int";
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6);
                object2 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = object2;
            builder.val("idx", scriptValue13);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue13, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                double d2 = 0.0;
                ScriptValue scriptValue14 = ScriptValue.of((double)0.0);
                builder.val("idx", scriptValue14);
            }
            double d3 = (d = scriptValue5.asNum()) == 0.0 ? 0.0 : scriptContext.getNum("idx") % d;
            ScriptValue scriptValue15 = ScriptValue.of((double)d3);
            builder.val("idx", scriptValue15);
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
            if (scriptValue16 != ScriptValue.NULL) {
                String string = "_saw_recipe_index";
                String string4 = "int";
                double d4 = scriptValue5.asNum();
                ScriptValue scriptValue17 = ScriptValue.of((double)(d4 == 0.0 ? 0.0 : (d3 + 1.0) % d4));
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue16);
                v2 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string4, scriptValue17)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("item_id", scriptContext.getClassOrVar("item_id"));
            builder3.val("target_idx", ScriptValue.of((double)d3));
            ScriptValue scriptValue18 = Saw._sawRecipeAt(builder3);
            builder.val("picked", scriptValue18);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue18, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                ScriptValue scriptValue19 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue18, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("outs", scriptValue19);
                if (ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue19, (ScriptContext)scriptContext).asNum() > 0.0) {
                    return PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext);
                }
            }
            return scriptContext.getClassOrVar("null");
        }
        PolyClassRegistry polyClassRegistry = PolyClassRegistry.ofVar((ScriptContext)scriptContext, (String)"Registry");
        ScriptValue scriptValue20 = polyClassRegistry != null ? polyClassRegistry.pg$0_recipes() : ((scriptValue = scriptContext.getClassOrVar("Registry")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        PolyClassRecipeRegistry polyClassRecipeRegistry = PolyClassRecipeRegistry.ofGuarded((ScriptValue)scriptValue20);
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)(polyClassRecipeRegistry != null ? polyClassRecipeRegistry.pg$2_stonecutter() : PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)scriptValue20, (ScriptContext)scriptContext)), (ScriptValue)scriptContext.getClassOrVar("item_id"), (ScriptContext)scriptContext);
        builder.val("stone_candidates", (ScriptValue)callSite);
        if (scriptContext.getStr("filter_id").equals("") ^ true) {
            List list = ScriptProgram.elementsOf((ScriptValue)callSite);
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("outs");
            if (list != null) {
                for (ScriptValue scriptValue22 : list) {
                    builder.val("r", scriptValue22);
                    ScriptValue scriptValue23 = scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue22, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("outs", scriptValue23);
                    scriptValue21 = scriptValue23;
                    if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue21, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
                    return scriptContext.getClassOrVar("filter_id");
                }
            }
        }
        ScriptValue scriptValue24 = ScriptFormula.callBuiltin1((String)"size", (ScriptValue)callSite, (ScriptContext)scriptContext);
        builder.val("stone_count", scriptValue24);
        if (scriptValue24.asNum() > 0.0) {
            double d;
            Object object3;
            ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
            if (scriptValue25 != ScriptValue.NULL) {
                String string = "_saw_recipe_index";
                String string5 = "int";
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue25);
                object3 = polyClassMachine != null ? polyClassMachine.tm$34_get_typed(string, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue25, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue26 = object3;
            builder.val("idx", scriptValue26);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue26, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                double d5 = 0.0;
                ScriptValue scriptValue27 = ScriptValue.of((double)0.0);
                builder.val("idx", scriptValue27);
            }
            double d6 = (d = scriptValue24.asNum()) == 0.0 ? 0.0 : scriptContext.getNum("idx") % d;
            ScriptValue scriptValue28 = ScriptValue.of((double)d6);
            builder.val("idx", scriptValue28);
            ScriptValue scriptValue29 = scriptContext.getClassOrVar("Machine");
            if (scriptValue29 != ScriptValue.NULL) {
                String string = "_saw_recipe_index";
                String string6 = "int";
                double d7 = scriptValue24.asNum();
                ScriptValue scriptValue30 = ScriptValue.of((double)(d7 == 0.0 ? 0.0 : (d6 + 1.0) % d7));
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue29);
                v4 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string6, scriptValue30)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue29, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue30, (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
            CallSite callSite2 = PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)callSite, (ScriptValue)ScriptValue.of((double)d6)), (ScriptContext)scriptContext);
            builder.val("outs", (ScriptValue)callSite2);
            if (ScriptFormula.callBuiltin1((String)"size", (ScriptValue)callSite2, (ScriptContext)scriptContext).asNum() > 0.0) {
                return PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)callSite2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext);
            }
        }
        return scriptContext.getClassOrVar("null");
    }

    public static ScriptValue onRightClick(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        ScriptValue scriptValue2 = polyClassPlayer != null ? polyClassPlayer.pg$48_main_hand() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("held", scriptValue2);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                String string = "_saw_filter";
                String string2 = "str";
                ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3);
                v0 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue4)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                String string = "_saw_filter";
                String string3 = "str";
                ScriptValue scriptValue6 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5);
                v1 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue6)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _sawOutputFace(ScriptContext.Builder builder) {
        Object object;
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "facing";
        PolyClassBlock_v2 polyClassBlock_v2 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue2);
        ScriptValue scriptValue3 = polyClassBlock_v2 != null ? polyClassBlock_v2.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        builder.val("facing", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            String string2 = "_saw_rpm_sign";
            String string3 = "int";
            PolyClassMachine polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4);
            object = polyClassMachine2 != null ? polyClassMachine2.tm$34_get_typed(string2, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("sign", scriptValue5);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqual((ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0)))) {
            double d = 1.0;
            ScriptValue scriptValue6 = ScriptValue.of((double)1.0);
            builder.val("sign", scriptValue6);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"north") || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"south")) {
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
        block2: {
            ScriptContext scriptContext = builder.peek();
            if (!(ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("effective_rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true)) break block2;
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            if (scriptValue != ScriptValue.NULL) {
                String string = "_saw_rpm_sign";
                String string2 = "int";
                ScriptValue scriptValue2 = ScriptValue.of((double)(scriptContext.getNum("effective_rpm") < 0.0 ? -1.0 : 1.0));
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue);
                v0 = polyClassMachine != null ? ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue2)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _sawSetupRpm(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "face";
        PolyClassBlock_v2 polyClassBlock_v2 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue3);
        ScriptValue scriptValue4 = polyClassBlock_v2 != null ? polyClassBlock_v2.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        builder.val("face", scriptValue4);
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue5 = polyClassMachine2 != null ? polyClassMachine2.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string2 = "facing";
        PolyClassBlock_v2 polyClassBlock_v22 = PolyClassBlock_v2.ofGuarded((ScriptValue)scriptValue5);
        ScriptValue scriptValue6 = polyClassBlock_v22 != null ? polyClassBlock_v22.tm$24_property(string2) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        builder.val("facing", scriptValue6);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"wall")) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                String string3 = "back";
                PolyClassMachine polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7);
                v0 = polyClassMachine3 != null ? ScriptValue.of((boolean)polyClassMachine3.tm$40_set_rpm_input(string3)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                String string4 = "";
                PolyClassMachine polyClassMachine4 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue8);
                v1 = polyClassMachine4 != null ? ScriptValue.of((boolean)polyClassMachine4.tm$44_set_rpm_output_same(string4)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"north") || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"south")) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                String string5 = "north,south";
                PolyClassMachine polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9);
                v2 = polyClassMachine5 != null ? ScriptValue.of((boolean)polyClassMachine5.tm$40_set_rpm_input(string5)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            if (scriptValue10 != ScriptValue.NULL) {
                String string6 = "north,south";
                PolyClassMachine polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue10);
                v3 = polyClassMachine6 != null ? ScriptValue.of((boolean)polyClassMachine6.tm$44_set_rpm_output_same(string6)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
            if (scriptValue11 != ScriptValue.NULL) {
                String string7 = "east,west";
                PolyClassMachine polyClassMachine7 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue11);
                v4 = polyClassMachine7 != null ? ScriptValue.of((boolean)polyClassMachine7.tm$40_set_rpm_input(string7)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string7), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
            if (scriptValue12 != ScriptValue.NULL) {
                String string8 = "east,west";
                PolyClassMachine polyClassMachine8 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue12);
                v5 = polyClassMachine8 != null ? ScriptValue.of((boolean)polyClassMachine8.tm$44_set_rpm_output_same(string8)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string8), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _sawDepositDirectional(ScriptContext.Builder builder) {
        block5: {
            ScriptContext scriptContext;
            block4: {
                ScriptValue scriptValue;
                scriptContext = builder.peek();
                if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext).asBool()) {
                    return ScriptValue.NULL;
                }
                PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
                if (!(ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine != null ? polyClassMachine.pg$191_contraption() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) break block4;
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
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(scriptContext);
                builder6.val("face", scriptValue);
                builder6.val("item", scriptContext.getClassOrVar("remaining"));
                ScriptValue scriptValue4 = BeltUtils.depotGive((ScriptContext.Builder)builder6);
                builder.val("remaining", scriptValue4);
            }
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("remaining"), (ScriptContext)scriptContext).asBool() ^ true)) break block5;
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("remaining"));
                arrayList.add(ScriptValue.of((double)(ScriptFormula.subscriptGet((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))).asNum() * 0.08)));
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.05));
                arrayList.add(ScriptValue.of((double)(ScriptFormula.subscriptGet((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0))).asNum() * 0.08)));
                PolyClassMachine polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5);
                v0 = polyClassMachine != null ? polyClassMachine.um$19_drop_item_toward(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static void run(ScriptContext.Builder var0) {
        block115: {
            block113: {
                block114: {
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
                    var14_10 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var16_12 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var18_14 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var20_16 = ScriptValue.of((String)((var14_10 != null ? var14_10.pg$200_x() : ((var15_11 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var15_11, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var16_12 != null ? var16_12.pg$202_y() : ((var17_13 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var17_13, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var18_14 != null ? var18_14.pg$206_z() : ((var19_15 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var19_15, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr()));
                    var0.val("_hold_key", var20_16);
                    var21_17 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var23_19 = var21_17 != null ? var21_17.pg$191_contraption() : ((var22_18 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var22_18, (ScriptContext)var1_1) : ScriptValue.NULL);
                    var0.val("contraption", var23_19);
                    var24_20 = ScriptContext.builder().copyFrom(var1_1);
                    var24_20.val("effective_rpm", (ScriptValue)(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var23_19 != ScriptValue.NULL ? ((var25_21 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var25_21.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm")));
                    Saw._sawUpdateRpmSign(var24_20);
                    var26_22 = 0.0;
                    var28_23 = ScriptValue.of((double)0.0);
                    var0.val("is_now", var28_23);
                    var30_24 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var29_26 = var30_24 != null ? var30_24.pg$139_block() : ((var31_25 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)var31_25, (ScriptContext)var1_1) : ScriptValue.NULL);
                    var32_27 = "face";
                    var33_28 = PolyClassBlock_v2.ofGuarded((ScriptValue)var29_26);
                    var34_29 = ScriptFormula.valuesEqualStr((ScriptValue)(var33_28 != null ? var33_28.tm$24_property(var32_27) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var29_26, (ScriptValue)ScriptValue.of((String)var32_27), (ScriptContext)var1_1)), (String)"floor");
                    var35_30 = ScriptValue.of((boolean)var34_29);
                    var0.val("is_belt_facing", var35_30);
                    if (!(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block113;
                    var37_32 = ScriptFormula.valuesEqual((ScriptValue)(var23_19 != ScriptValue.NULL ? ((var36_31 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var36_31.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true;
                    var38_33 = ScriptValue.of((boolean)var37_32);
                    var0.val("is_rotational", var38_33);
                    var40_35 = var23_19 != ScriptValue.NULL ? ((var39_34 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? ScriptValue.of((boolean)var39_34.tm$48_is_moving()) : PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var0.val("is_linear", var40_35);
                    var41_36 = (var37_32 != false || var40_35.asBool() != false) != false ? 1.0 : 0.0;
                    var43_37 = ScriptValue.of((double)var41_36);
                    var0.val("is_now", var43_37);
                    if (!(var41_36 > 0.0)) break block114;
                    if (var37_32) {
                        var44_38 = 10.0;
                        v4 = 10.0 == 0.0 ? 0.0 : Math.abs(var23_19 != ScriptValue.NULL ? ((var46_39 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var46_39.tg$71_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum()) / var44_38;
                        v5 = ScriptValue.of((double)Math.max(1.0, v4));
                    } else {
                        var47_40 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                        var47_40.val("contraption", var23_19);
                        v5 = Utils.1._linearBreakSpeed((ScriptContext.Builder)var47_40);
                    }
                    var48_41 = v5;
                    var0.val("speed", var48_41);
                    var49_42 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var51_44 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var53_46 = ScriptFormula.addPolymorphic((ScriptValue)(var49_42 != null ? var49_42.pg$200_x() : ((var50_43 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var50_43, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var51_44 != null ? var51_44.pg$133_facing_dx() : ((var52_45 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var52_45, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("tx", var53_46);
                    var54_47 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var56_49 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var58_51 = ScriptFormula.addPolymorphic((ScriptValue)(var54_47 != null ? var54_47.pg$202_y() : ((var55_48 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var55_48, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var56_49 != null ? var56_49.pg$129_facing_dy() : ((var57_50 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var57_50, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("ty", var58_51);
                    var59_52 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var61_54 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var63_56 = ScriptFormula.addPolymorphic((ScriptValue)(var59_52 != null ? var59_52.pg$206_z() : ((var60_53 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var60_53, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var61_54 != null ? var61_54.pg$131_facing_dz() : ((var62_55 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var62_55, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("tz", var63_56);
                    var64_58 = var23_19 != ScriptValue.NULL ? ((var65_57 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var65_57.pg$53_contraption_world() : PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var66_59 = var53_46;
                    var67_60 = var58_51;
                    var68_61 = var63_56;
                    var69_62 = PolyClassContraptionWorld.ofGuarded((ScriptValue)var64_58);
                    var70_63 = var69_62 != null ? var69_62.tm$8_real_block(var66_59.asNum(), var67_60.asNum(), var68_61.asNum()) : PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)var64_58, (ScriptValue)var66_59, (ScriptValue)var67_60, (ScriptValue)var68_61, (ScriptContext)var1_1);
                    var0.val("target", (ScriptValue)var70_63);
                    if ((var34_29 != false || ScriptFormula.valuesEqual((ScriptValue)var70_63, (ScriptValue)var1_1.getClassOrVar("null")) != false) != false || (var70_63 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var70_63, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
                    var71_64 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var71_64.val("id", (ScriptValue)(var70_63 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var70_63, (ScriptContext)var1_1) : ScriptValue.NULL));
                    if (!(TreeUtils._isChoppable((ScriptContext.Builder)var71_64).asBool() ^ true)) {
                        v6 = false;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v6 = true;
                    }
                    if (v6) {
                        if (var23_19 != ScriptValue.NULL) {
                            var72_65 = var20_16;
                            var73_66 = PolyClassContraption.ofGuarded((ScriptValue)var23_19);
                            v7 /* !! */  = var73_66 != null ? ScriptValue.of((boolean)var73_66.tm$2_release(var72_65.asStr())) : PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var23_19, (ScriptValue)var72_65, (ScriptContext)var1_1);
                        } else {
                            v7 /* !! */  = ScriptValue.NULL;
                        }
                    } else {
                        if (var23_19 != ScriptValue.NULL) {
                            var74_67 = var20_16;
                            var75_68 = PolyClassContraption.ofGuarded((ScriptValue)var23_19);
                            v8 /* !! */  = var75_68 != null ? ScriptValue.of((boolean)var75_68.tm$12_hold(var74_67.asStr())) : PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var23_19, (ScriptValue)var74_67, (ScriptContext)var1_1);
                        } else {
                            v8 /* !! */  = ScriptValue.NULL;
                        }
                        var76_69 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                        var76_69.val("id", (ScriptValue)(var70_63 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var70_63, (ScriptContext)var1_1) : ScriptValue.NULL));
                        if (TreeUtils._isLog((ScriptContext.Builder)var76_69).asBool()) {
                            var77_70 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                            var77_70.val("contraption", var23_19);
                            var77_70.val("base_x", var53_46);
                            var77_70.val("base_y", var58_51);
                            var77_70.val("base_z", var63_56);
                            var77_70.val("speed", var48_41);
                            TreeUtils._fellTree((ScriptContext.Builder)var77_70);
                        } else {
                            var78_71 = var1_1.getClassOrVar("Machine");
                            if (var78_71 != ScriptValue.NULL) {
                                var79_72 = var70_63;
                                var80_73 = var48_41;
                                var81_74 = PolyClassMachine.ofGuarded((ScriptValue)var78_71);
                                v9 /* !! */  = var81_74 != null ? var81_74.tm$2_tick_break((ScriptValue)var79_72, var80_73.asNum()) : PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var78_71, (ScriptValue)var79_72, (ScriptValue)var80_73, (ScriptContext)var1_1);
                            } else {
                                v9 /* !! */  = ScriptValue.NULL;
                            }
                            var82_75 = v9 /* !! */ ;
                            var0.val("result", var82_75);
                            if (ScriptFormula.valuesEqual((ScriptValue)var82_75, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var83_76 = ScriptProgram.elementsOf((ScriptValue)var82_75)) != null) {
                                for (ScriptValue var85_78 : var83_76) {
                                    var0.val("item", var85_78);
                                    var86_79 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                                    var86_79.val("item", var85_78);
                                    Utils.1._deposit((ScriptContext.Builder)var86_79);
                                }
                            }
                        }
                        var87_80 = var1_1.getClassOrVar("Machine");
                        if (var87_80 != ScriptValue.NULL) {
                            if (var37_32) {
                                var89_81 = ScriptContext.builder().copyFrom(var1_1);
                                var89_81.val("current_rpm", (ScriptValue)(var23_19 != ScriptValue.NULL ? ((var90_82 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var90_82.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL));
                                v10 = Saw._sawSuCost(var89_81);
                            } else {
                                v10 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 6.0)), (ScriptValue)var48_41);
                            }
                            var88_83 = v10;
                            var91_84 = PolyClassMachine.ofGuarded((ScriptValue)var87_80);
                            v11 /* !! */  = var91_84 != null ? ScriptValue.of((boolean)var91_84.tm$56_report_su(var88_83.asNum())) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var87_80, (ScriptValue)var88_83, (ScriptContext)var1_1);
                        } else {
                            v11 /* !! */  = ScriptValue.NULL;
                        }
                    }
                    break block115;
                }
                if (var23_19 != ScriptValue.NULL) {
                    var92_85 = var20_16;
                    var93_86 = PolyClassContraption.ofGuarded((ScriptValue)var23_19);
                    v12 /* !! */  = var93_86 != null ? ScriptValue.of((boolean)var93_86.tm$2_release(var92_85.asStr())) : PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var23_19, (ScriptValue)var92_85, (ScriptContext)var1_1);
                } else {
                    v12 /* !! */  = ScriptValue.NULL;
                }
                break block115;
            }
            if (!(ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true)) break block115;
            var94_87 = 10.0;
            var96_88 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(var1_1.getNum("rpm")) / var94_87);
            var98_89 = ScriptValue.of((double)var96_88);
            var0.val("speed", var98_89);
            var99_90 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
            var101_92 = var99_90 != null ? var99_90.pg$137_facing_block() : ((var100_91 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var100_91, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("fb", var101_92);
            if (!(var34_29 ^ true)) ** GOTO lbl-1000
            v13 = var101_92 != ScriptValue.NULL ? ((var102_93 = PolyClassBlock_v2.ofGuarded((ScriptValue)var101_92)) != null ? var102_93.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var101_92, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
            if (v13 ^ true) {
                v14 = true;
            } else lbl-1000:
            // 2 sources

            {
                v14 = false;
            }
            if (!v14) ** GOTO lbl-1000
            var103_94 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
            var103_94.val("id", (ScriptValue)(var101_92 != ScriptValue.NULL ? ((var104_95 = PolyClassBlock_v2.ofGuarded((ScriptValue)var101_92)) != null ? var104_95.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var101_92, (ScriptContext)var1_1)) : ScriptValue.NULL));
            if (TreeUtils._isChoppable((ScriptContext.Builder)var103_94).asBool()) {
                v15 = true;
            } else lbl-1000:
            // 2 sources

            {
                v15 = false;
            }
            if (v15) {
                var105_96 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                var105_96.val("id", (ScriptValue)(var101_92 != ScriptValue.NULL ? ((var106_97 = PolyClassBlock_v2.ofGuarded((ScriptValue)var101_92)) != null ? var106_97.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var101_92, (ScriptContext)var1_1)) : ScriptValue.NULL));
                if (TreeUtils._isLog((ScriptContext.Builder)var105_96).asBool()) {
                    var107_98 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var107_98.val("contraption", var1_1.getClassOrVar("null"));
                    var108_99 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var110_101 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var107_98.val("base_x", ScriptFormula.addPolymorphic((ScriptValue)(var108_99 != null ? var108_99.pg$200_x() : ((var109_100 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var109_100, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var110_101 != null ? var110_101.pg$133_facing_dx() : ((var111_102 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var111_102, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var112_103 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var114_105 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var107_98.val("base_y", ScriptFormula.addPolymorphic((ScriptValue)(var112_103 != null ? var112_103.pg$202_y() : ((var113_104 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var113_104, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var114_105 != null ? var114_105.pg$129_facing_dy() : ((var115_106 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var115_106, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var116_107 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var118_109 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var107_98.val("base_z", ScriptFormula.addPolymorphic((ScriptValue)(var116_107 != null ? var116_107.pg$206_z() : ((var117_108 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var117_108, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var118_109 != null ? var118_109.pg$131_facing_dz() : ((var119_110 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var119_110, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var107_98.val("speed", ScriptValue.of((double)var96_88));
                    TreeUtils._fellTree((ScriptContext.Builder)var107_98);
                } else {
                    var120_111 = var1_1.getClassOrVar("Machine");
                    if (var120_111 != ScriptValue.NULL) {
                        var121_112 = var101_92;
                        var122_113 = var96_88;
                        var124_114 = PolyClassMachine.ofGuarded((ScriptValue)var120_111);
                        v16 /* !! */  = var124_114 != null ? var124_114.tm$2_tick_break(var121_112, var122_113) : PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var120_111, (ScriptValue)var121_112, (ScriptValue)ScriptValue.of((double)var122_113), (ScriptContext)var1_1);
                    } else {
                        v16 /* !! */  = ScriptValue.NULL;
                    }
                    var125_115 = v16 /* !! */ ;
                    var0.val("result", var125_115);
                    if (ScriptFormula.valuesEqual((ScriptValue)var125_115, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var126_116 = ScriptProgram.elementsOf((ScriptValue)var125_115)) != null) {
                        for (ScriptValue var128_118 : var126_116) {
                            var0.val("item", var128_118);
                            var129_119 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var129_119.val("item", var128_118);
                            Utils.1._deposit((ScriptContext.Builder)var129_119);
                        }
                    }
                }
                var130_120 = var1_1.getClassOrVar("Machine");
                if (var130_120 != ScriptValue.NULL) {
                    var132_121 = ScriptContext.builder().copyFrom(var1_1);
                    var132_121.val("current_rpm", var1_1.getClassOrVar("rpm"));
                    var131_122 = Saw._sawSuCost(var132_121);
                    var133_123 = PolyClassMachine.ofGuarded((ScriptValue)var130_120);
                    v17 /* !! */  = var133_123 != null ? ScriptValue.of((boolean)var133_123.tm$56_report_su(var131_122.asNum())) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var130_120, (ScriptValue)var131_122, (ScriptContext)var1_1);
                } else {
                    v17 /* !! */  = ScriptValue.NULL;
                }
            }
            var134_124 = 1.0;
            var136_125 = ScriptValue.of((double)1.0);
            var0.val("is_now", var136_125);
        }
        if (var1_1.getNum("is_now") > 0.0 != false && var34_29 != false) {
            var137_126 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
            var139_128 = var137_126 != null ? var137_126.pg$158_belt() : ((var138_127 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "belt", (ScriptValue)var138_127, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("belt", var139_128);
            var141_130 = ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var23_19 != ScriptValue.NULL ? ((var140_129 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var140_129.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm");
            var0.val("belt_rpm", var141_130);
            var142_131 = var1_1.getClassOrVar("Machine");
            if (var142_131 != ScriptValue.NULL) {
                var143_132 = "_saw_item_id";
                var144_133 = "str";
                var145_134 = PolyClassMachine.ofGuarded((ScriptValue)var142_131);
                v18 /* !! */  = var145_134 != null ? var145_134.tm$34_get_typed(var143_132, var144_133) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var143_132), (ScriptValue)ScriptValue.of((String)var144_133), (ScriptContext)var1_1);
            } else {
                v18 /* !! */  = ScriptValue.NULL;
            }
            var146_135 = v18 /* !! */ ;
            var0.val("active_id", var146_135);
            if (ScriptFormula.valuesEqual((ScriptValue)var146_135, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var147_136 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                var0.val("active_id", var147_136);
            }
            if (var1_1.getStr("active_id").equals("")) {
                v19 = var139_128 != ScriptValue.NULL ? ((var148_137 = PolyClassBelt.ofGuarded((ScriptValue)var139_128)) != null ? var148_137.tg$26_has_item() : PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var139_128, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
                if (v19) {
                    var150_139 = var139_128 != ScriptValue.NULL ? ((var149_138 = PolyClassBelt.ofGuarded((ScriptValue)var139_128)) != null ? var149_138.tm$12_peek() : PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var139_128, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var0.val("carried", var150_139);
                    var151_140 = ScriptContext.builder().copyFrom(var1_1);
                    var151_140.val("item_id", (ScriptValue)(var150_139 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var150_139, (ScriptContext)var1_1) : ScriptValue.NULL));
                    var152_141 = Saw._sawOutputFor(var151_140);
                    var0.val("out_id", var152_141);
                    ScriptFormula.callBuiltin1((String)"print", (ScriptValue)ScriptValue.of((String)("[saw-debug] own-belt has_item id=" + (var150_139 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var150_139, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " out_id=" + (ScriptFormula.valuesEqual((ScriptValue)var152_141, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "null")) : var152_141).asStr())), (ScriptContext)var1_1);
                    if (ScriptFormula.valuesEqual((ScriptValue)var152_141, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var154_143 = var139_128 != ScriptValue.NULL ? ((var153_142 = PolyClassBelt.ofGuarded((ScriptValue)var139_128)) != null ? var153_142.tm$0_take() : PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var139_128, (ScriptContext)var1_1)) : ScriptValue.NULL;
                        var0.val("taken", var154_143);
                        var155_144 = var1_1.getClassOrVar("Machine");
                        if (var155_144 != ScriptValue.NULL) {
                            var156_145 = "_saw_item_id";
                            var157_146 = "str";
                            var158_147 = var154_143 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var154_143, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var159_148 = PolyClassMachine.ofGuarded((ScriptValue)var155_144);
                            v20 /* !! */  = var159_148 != null ? ScriptValue.of((boolean)var159_148.tm$82_set_typed(var156_145, var157_146, var158_147)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var155_144, (ScriptValue)ScriptValue.of((String)var156_145), (ScriptValue)ScriptValue.of((String)var157_146), (ScriptValue)var158_147, (ScriptContext)var1_1);
                        } else {
                            v20 /* !! */  = ScriptValue.NULL;
                        }
                        var160_149 = var1_1.getClassOrVar("Machine");
                        if (var160_149 != ScriptValue.NULL) {
                            var161_150 = "_saw_out_id";
                            var162_151 = "str";
                            var163_152 = var152_141;
                            var164_153 = PolyClassMachine.ofGuarded((ScriptValue)var160_149);
                            v21 /* !! */  = var164_153 != null ? ScriptValue.of((boolean)var164_153.tm$82_set_typed(var161_150, var162_151, var163_152)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var160_149, (ScriptValue)ScriptValue.of((String)var161_150), (ScriptValue)ScriptValue.of((String)var162_151), (ScriptValue)var163_152, (ScriptContext)var1_1);
                        } else {
                            v21 /* !! */  = ScriptValue.NULL;
                        }
                        var165_154 = var1_1.getClassOrVar("Machine");
                        if (var165_154 != ScriptValue.NULL) {
                            var166_155 = "_saw_count";
                            var167_156 = "int";
                            var168_157 = var154_143 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var154_143, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var169_158 = PolyClassMachine.ofGuarded((ScriptValue)var165_154);
                            v22 /* !! */  = var169_158 != null ? ScriptValue.of((boolean)var169_158.tm$82_set_typed(var166_155, var167_156, var168_157)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var165_154, (ScriptValue)ScriptValue.of((String)var166_155), (ScriptValue)ScriptValue.of((String)var167_156), (ScriptValue)var168_157, (ScriptContext)var1_1);
                        } else {
                            v22 /* !! */  = ScriptValue.NULL;
                        }
                        var170_159 = var1_1.getClassOrVar("Machine");
                        if (var170_159 != ScriptValue.NULL) {
                            var171_160 = "_saw_progress";
                            var172_161 = "int";
                            var173_162 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            var174_163 = PolyClassMachine.ofGuarded((ScriptValue)var170_159);
                            v23 /* !! */  = var174_163 != null ? ScriptValue.of((boolean)var174_163.tm$82_set_typed(var171_160, var172_161, var173_162)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var170_159, (ScriptValue)ScriptValue.of((String)var171_160), (ScriptValue)ScriptValue.of((String)var172_161), (ScriptValue)var173_162, (ScriptContext)var1_1);
                        } else {
                            v23 /* !! */  = ScriptValue.NULL;
                        }
                        var175_164 = var1_1.getClassOrVar("Machine");
                        if (var175_164 != ScriptValue.NULL) {
                            var176_165 = "_saw_from_ground";
                            var177_166 = "int";
                            var178_167 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            var179_168 = PolyClassMachine.ofGuarded((ScriptValue)var175_164);
                            v24 /* !! */  = var179_168 != null ? ScriptValue.of((boolean)var179_168.tm$82_set_typed(var176_165, var177_166, var178_167)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var175_164, (ScriptValue)ScriptValue.of((String)var176_165), (ScriptValue)ScriptValue.of((String)var177_166), (ScriptValue)var178_167, (ScriptContext)var1_1);
                        } else {
                            v24 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var180_169 = ScriptContext.builder().copyFrom(var1_1);
                    var181_170 = Saw._sawInputFace(var180_169);
                    var0.val("in_face", var181_170);
                    var182_171 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                    var182_171.val("face", var181_170);
                    var182_171.val("validator", var1_1.getClassOrVar("null"));
                    var182_171.val("amount", var1_1.getClassOrVar("null"));
                    var183_172 = BeltUtils.beltTake((ScriptContext.Builder)var182_171);
                    var0.val("taken", var183_172);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var183_172, (ScriptContext)var1_1).asBool() ^ true) {
                        var184_173 = ScriptContext.builder().copyFrom(var1_1);
                        var184_173.val("item_id", (ScriptValue)(var183_172 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var183_172, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var185_174 = Saw._sawOutputFor(var184_173);
                        var0.val("out_id", var185_174);
                        if (ScriptFormula.valuesEqual((ScriptValue)var185_174, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var186_175 = var1_1.getClassOrVar("Machine");
                            if (var186_175 != ScriptValue.NULL) {
                                var187_176 = "_saw_item_id";
                                var188_177 = "str";
                                var189_178 = var183_172 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var183_172, (ScriptContext)var1_1) : ScriptValue.NULL;
                                var190_179 = PolyClassMachine.ofGuarded((ScriptValue)var186_175);
                                v25 /* !! */  = var190_179 != null ? ScriptValue.of((boolean)var190_179.tm$82_set_typed(var187_176, var188_177, var189_178)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var186_175, (ScriptValue)ScriptValue.of((String)var187_176), (ScriptValue)ScriptValue.of((String)var188_177), (ScriptValue)var189_178, (ScriptContext)var1_1);
                            } else {
                                v25 /* !! */  = ScriptValue.NULL;
                            }
                            var191_180 = var1_1.getClassOrVar("Machine");
                            if (var191_180 != ScriptValue.NULL) {
                                var192_181 = "_saw_out_id";
                                var193_182 = "str";
                                var194_183 = var185_174;
                                var195_184 = PolyClassMachine.ofGuarded((ScriptValue)var191_180);
                                v26 /* !! */  = var195_184 != null ? ScriptValue.of((boolean)var195_184.tm$82_set_typed(var192_181, var193_182, var194_183)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var191_180, (ScriptValue)ScriptValue.of((String)var192_181), (ScriptValue)ScriptValue.of((String)var193_182), (ScriptValue)var194_183, (ScriptContext)var1_1);
                            } else {
                                v26 /* !! */  = ScriptValue.NULL;
                            }
                            var196_185 = var1_1.getClassOrVar("Machine");
                            if (var196_185 != ScriptValue.NULL) {
                                var197_186 = "_saw_count";
                                var198_187 = "int";
                                var199_188 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var183_172, (ScriptContext)var1_1);
                                var200_189 = PolyClassMachine.ofGuarded((ScriptValue)var196_185);
                                v27 /* !! */  = var200_189 != null ? ScriptValue.of((boolean)var200_189.tm$82_set_typed(var197_186, var198_187, var199_188)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var196_185, (ScriptValue)ScriptValue.of((String)var197_186), (ScriptValue)ScriptValue.of((String)var198_187), (ScriptValue)var199_188, (ScriptContext)var1_1);
                            } else {
                                v27 /* !! */  = ScriptValue.NULL;
                            }
                            var201_190 = var1_1.getClassOrVar("Machine");
                            if (var201_190 != ScriptValue.NULL) {
                                var202_191 = "_saw_progress";
                                var203_192 = "int";
                                var204_193 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                var205_194 = PolyClassMachine.ofGuarded((ScriptValue)var201_190);
                                v28 /* !! */  = var205_194 != null ? ScriptValue.of((boolean)var205_194.tm$82_set_typed(var202_191, var203_192, var204_193)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var201_190, (ScriptValue)ScriptValue.of((String)var202_191), (ScriptValue)ScriptValue.of((String)var203_192), (ScriptValue)var204_193, (ScriptContext)var1_1);
                            } else {
                                v28 /* !! */  = ScriptValue.NULL;
                            }
                            var206_195 = var1_1.getClassOrVar("Machine");
                            if (var206_195 != ScriptValue.NULL) {
                                var207_196 = "_saw_from_ground";
                                var208_197 = "int";
                                var209_198 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0);
                                var210_199 = PolyClassMachine.ofGuarded((ScriptValue)var206_195);
                                v29 /* !! */  = var210_199 != null ? ScriptValue.of((boolean)var210_199.tm$82_set_typed(var207_196, var208_197, var209_198)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var206_195, (ScriptValue)ScriptValue.of((String)var207_196), (ScriptValue)ScriptValue.of((String)var208_197), (ScriptValue)var209_198, (ScriptContext)var1_1);
                            } else {
                                v29 /* !! */  = ScriptValue.NULL;
                            }
                        } else {
                            var211_200 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                            var211_200.val("face", var181_170);
                            var211_200.val("item", var183_172);
                            BeltUtils.beltGive((ScriptContext.Builder)var211_200);
                        }
                    } else {
                        var215_201 = var1_1.getClassOrVar("Machine");
                        if (var215_201 != ScriptValue.NULL) {
                            var216_202 = 0.7;
                            var218_203 = PolyClassMachine.ofGuarded((ScriptValue)var215_201);
                            v30 /* !! */  = var218_203 != null ? var218_203.tm$94_nearby_entities(var216_202) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var215_201, (ScriptValue)ScriptValue.of((double)var216_202), (ScriptContext)var1_1);
                        } else {
                            v30 /* !! */  = ScriptValue.NULL;
                        }
                        var212_204 = ScriptProgram.elementsOf((ScriptValue)v30 /* !! */ );
                        var219_205 = var1_1.getClassOrVar("out_id");
                        if (var212_204 != null) {
                            for (ScriptValue var214_207 : var212_204) {
                                var0.val("entity", var214_207);
                                var220_208 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                                var220_208.val("entity", var214_207);
                                if (!Utils.isRestingItem(var220_208).asBool()) continue;
                                var221_209 = ScriptContext.builder().copyFrom(var1_1);
                                var221_209.val("item_id", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var214_207 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var214_207, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                                var222_210 = Saw._sawOutputFor(var221_209);
                                var0.val("out_id", var222_210);
                                var219_205 = var222_210;
                                if (!(ScriptFormula.valuesEqual((ScriptValue)var219_205, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                                var223_211 = var1_1.getClassOrVar("Machine");
                                if (var223_211 != ScriptValue.NULL) {
                                    var224_212 = "_saw_item_id";
                                    var225_213 = "str";
                                    var226_214 = PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var214_207 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var214_207, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    var227_215 = PolyClassMachine.ofGuarded((ScriptValue)var223_211);
                                    v31 /* !! */  = var227_215 != null ? ScriptValue.of((boolean)var227_215.tm$82_set_typed(var224_212, var225_213, (ScriptValue)var226_214)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var223_211, (ScriptValue)ScriptValue.of((String)var224_212), (ScriptValue)ScriptValue.of((String)var225_213), (ScriptValue)var226_214, (ScriptContext)var1_1);
                                } else {
                                    v31 /* !! */  = ScriptValue.NULL;
                                }
                                var228_216 = var1_1.getClassOrVar("Machine");
                                if (var228_216 != ScriptValue.NULL) {
                                    var229_217 = "_saw_out_id";
                                    var230_218 = "str";
                                    var231_219 = var219_205;
                                    var232_220 = PolyClassMachine.ofGuarded((ScriptValue)var228_216);
                                    v32 /* !! */  = var232_220 != null ? ScriptValue.of((boolean)var232_220.tm$82_set_typed(var229_217, var230_218, var231_219)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var228_216, (ScriptValue)ScriptValue.of((String)var229_217), (ScriptValue)ScriptValue.of((String)var230_218), (ScriptValue)var231_219, (ScriptContext)var1_1);
                                } else {
                                    v32 /* !! */  = ScriptValue.NULL;
                                }
                                var233_221 = var1_1.getClassOrVar("Machine");
                                if (var233_221 != ScriptValue.NULL) {
                                    var234_222 = "_saw_count";
                                    var235_223 = "int";
                                    var236_224 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)(var214_207 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var214_207, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    var237_225 = PolyClassMachine.ofGuarded((ScriptValue)var233_221);
                                    v33 /* !! */  = var237_225 != null ? ScriptValue.of((boolean)var237_225.tm$82_set_typed(var234_222, var235_223, var236_224)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var233_221, (ScriptValue)ScriptValue.of((String)var234_222), (ScriptValue)ScriptValue.of((String)var235_223), (ScriptValue)var236_224, (ScriptContext)var1_1);
                                } else {
                                    v33 /* !! */  = ScriptValue.NULL;
                                }
                                var238_226 = var1_1.getClassOrVar("Machine");
                                if (var238_226 != ScriptValue.NULL) {
                                    var239_227 = "_saw_progress";
                                    var240_228 = "int";
                                    var241_229 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                    var242_230 = PolyClassMachine.ofGuarded((ScriptValue)var238_226);
                                    v34 /* !! */  = var242_230 != null ? ScriptValue.of((boolean)var242_230.tm$82_set_typed(var239_227, var240_228, var241_229)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var238_226, (ScriptValue)ScriptValue.of((String)var239_227), (ScriptValue)ScriptValue.of((String)var240_228), (ScriptValue)var241_229, (ScriptContext)var1_1);
                                } else {
                                    v34 /* !! */  = ScriptValue.NULL;
                                }
                                var243_231 = var1_1.getClassOrVar("Machine");
                                if (var243_231 != ScriptValue.NULL) {
                                    var244_232 = "_saw_from_ground";
                                    var245_233 = "int";
                                    var246_234 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0);
                                    var247_235 = PolyClassMachine.ofGuarded((ScriptValue)var243_231);
                                    v35 /* !! */  = var247_235 != null ? ScriptValue.of((boolean)var247_235.tm$82_set_typed(var244_232, var245_233, var246_234)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var243_231, (ScriptValue)ScriptValue.of((String)var244_232), (ScriptValue)ScriptValue.of((String)var245_233), (ScriptValue)var246_234, (ScriptContext)var1_1);
                                } else {
                                    v35 /* !! */  = ScriptValue.NULL;
                                }
                                v36 /* !! */  = var214_207 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var214_207, (ScriptContext)var1_1) : ScriptValue.NULL;
                                break;
                            }
                        }
                    }
                }
            } else {
                var248_236 = var1_1.getClassOrVar("Machine");
                if (var248_236 != ScriptValue.NULL) {
                    var249_237 = "_saw_progress";
                    var250_238 = "int";
                    var251_239 = PolyClassMachine.ofGuarded((ScriptValue)var248_236);
                    v37 /* !! */  = var251_239 != null ? var251_239.tm$34_get_typed(var249_237, var250_238) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var248_236, (ScriptValue)ScriptValue.of((String)var249_237), (ScriptValue)ScriptValue.of((String)var250_238), (ScriptContext)var1_1);
                } else {
                    v37 /* !! */  = ScriptValue.NULL;
                }
                var252_240 = v37 /* !! */ ;
                var0.val("progress", var252_240);
                if (ScriptFormula.valuesEqual((ScriptValue)var252_240, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var253_241 = 0.0;
                    var255_242 = ScriptValue.of((double)0.0);
                    var0.val("progress", var255_242);
                }
                var256_243 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)Math.abs(var141_130.asNum())));
                var0.val("progress", var256_243);
                var257_244 = var1_1.getClassOrVar("Machine");
                if (var257_244 != ScriptValue.NULL) {
                    var259_245 = ScriptContext.builder().copyFrom(var1_1);
                    var259_245.val("current_rpm", var141_130);
                    var258_246 = Saw._sawSuCost(var259_245);
                    var260_247 = PolyClassMachine.ofGuarded((ScriptValue)var257_244);
                    v38 /* !! */  = var260_247 != null ? ScriptValue.of((boolean)var260_247.tm$56_report_su(var258_246.asNum())) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var257_244, (ScriptValue)var258_246, (ScriptContext)var1_1);
                } else {
                    v38 /* !! */  = ScriptValue.NULL;
                }
                if (var256_243.asNum() >= var11_8) {
                    var261_248 = var1_1.getClassOrVar("Machine");
                    if (var261_248 != ScriptValue.NULL) {
                        var262_249 = "_saw_out_id";
                        var263_250 = "str";
                        var264_251 = PolyClassMachine.ofGuarded((ScriptValue)var261_248);
                        v39 /* !! */  = var264_251 != null ? var264_251.tm$34_get_typed(var262_249, var263_250) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var261_248, (ScriptValue)ScriptValue.of((String)var262_249), (ScriptValue)ScriptValue.of((String)var263_250), (ScriptContext)var1_1);
                    } else {
                        v39 /* !! */  = ScriptValue.NULL;
                    }
                    var265_252 = v39 /* !! */ ;
                    var0.val("out_id", var265_252);
                    var266_253 = var1_1.getClassOrVar("Machine");
                    if (var266_253 != ScriptValue.NULL) {
                        var267_254 = "_saw_count";
                        var268_255 = "int";
                        var269_256 = PolyClassMachine.ofGuarded((ScriptValue)var266_253);
                        v40 /* !! */  = var269_256 != null ? var269_256.tm$34_get_typed(var267_254, var268_255) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var266_253, (ScriptValue)ScriptValue.of((String)var267_254), (ScriptValue)ScriptValue.of((String)var268_255), (ScriptContext)var1_1);
                    } else {
                        v40 /* !! */  = ScriptValue.NULL;
                    }
                    var270_257 = v40 /* !! */ ;
                    var0.val("remaining", var270_257);
                    if (ScriptFormula.valuesEqual((ScriptValue)var270_257, (ScriptValue)var1_1.getClassOrVar("null")) != false || var270_257.asNum() <= 0.0 != false) {
                        var271_258 = 1.0;
                        var273_259 = ScriptValue.of((double)1.0);
                        var0.val("remaining", var273_259);
                    }
                    if ((var274_260 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        var275_261 = "_saw_from_ground";
                        var276_262 = "int";
                        var277_263 = PolyClassMachine.ofGuarded((ScriptValue)var274_260);
                        v41 /* !! */  = var277_263 != null ? var277_263.tm$34_get_typed(var275_261, var276_262) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var274_260, (ScriptValue)ScriptValue.of((String)var275_261), (ScriptValue)ScriptValue.of((String)var276_262), (ScriptContext)var1_1);
                    } else {
                        v41 /* !! */  = ScriptValue.NULL;
                    }
                    var278_264 = v41 /* !! */ ;
                    var0.val("from_ground", var278_264);
                    var279_265 = ScriptContext.builder().copyFrom(var1_1);
                    var279_265.val("item_id", var1_1.getClassOrVar("active_id"));
                    var279_265.val("out_id", var265_252);
                    var280_266 = Saw._sawRecipeFor(var279_265);
                    var0.val("recipe", var280_266);
                    var281_267 = ScriptFormula.valuesEqual((ScriptValue)var280_266, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var280_266 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "outputs", (ScriptValue)var280_266, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("null");
                    var0.val("result", var281_267);
                    if (ScriptFormula.valuesEqual((ScriptValue)var281_267, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        v42 /* !! */  = var281_267 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "items", (ScriptValue)var281_267, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var282_268 = new ArrayList<ScriptValue>();
                        var282_268.add(ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)var265_252, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0)), (ScriptContext)var1_1));
                        v42 /* !! */  = new ScriptValue.Array(var282_268);
                    }
                    var283_269 /* !! */  = v42 /* !! */ ;
                    var0.val("items", var283_269 /* !! */ );
                    var284_270 = ScriptProgram.elementsOf((ScriptValue)var283_269 /* !! */ );
                    var287_271 = var1_1.getClassOrVar("leftover");
                    if (var284_270 != null) {
                        for (ScriptValue var286_273 : var284_270) {
                            var0.val("out_item", var286_273);
                            if (ScriptFormula.valuesEqual((ScriptValue)var278_264, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0))) != false || ScriptFormula.valuesEqual((ScriptValue)var278_264, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0))) != false) {
                                var288_274 = ScriptContext.builder().copyFrom(var1_1);
                                var288_274.val("item", var286_273);
                                Saw._sawDepositDirectional(var288_274);
                                continue;
                            }
                            if (var139_128 != ScriptValue.NULL) {
                                var289_275 = var286_273;
                                var290_276 = PolyClassBelt.ofGuarded((ScriptValue)var139_128);
                                v43 /* !! */  = var290_276 != null ? var290_276.tm$10_put(var289_275) : PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var139_128, (ScriptValue)var289_275, (ScriptContext)var1_1);
                            } else {
                                v43 /* !! */  = ScriptValue.NULL;
                            }
                            var291_277 = v43 /* !! */ ;
                            var0.val("leftover", var291_277);
                            var287_271 = var291_277;
                            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var287_271, (ScriptContext)var1_1).asBool() ^ true)) continue;
                            var292_278 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var292_278.val("item", var287_271);
                            Utils.1._deposit((ScriptContext.Builder)var292_278);
                        }
                    }
                    var293_279 = var1_1.getNum("remaining") - 1.0;
                    var295_280 = ScriptValue.of((double)var293_279);
                    var0.val("remaining", var295_280);
                    var296_281 = var1_1.getClassOrVar("Machine");
                    if (var296_281 != ScriptValue.NULL) {
                        var297_282 = "_saw_count";
                        var298_283 = "int";
                        var299_284 = ScriptValue.of((double)var293_279);
                        var300_285 = PolyClassMachine.ofGuarded((ScriptValue)var296_281);
                        v44 /* !! */  = var300_285 != null ? ScriptValue.of((boolean)var300_285.tm$82_set_typed(var297_282, var298_283, var299_284)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var296_281, (ScriptValue)ScriptValue.of((String)var297_282), (ScriptValue)ScriptValue.of((String)var298_283), (ScriptValue)var299_284, (ScriptContext)var1_1);
                    } else {
                        v44 /* !! */  = ScriptValue.NULL;
                    }
                    var301_286 = var1_1.getClassOrVar("Machine");
                    if (var301_286 != ScriptValue.NULL) {
                        var302_287 = "_saw_progress";
                        var303_288 = "int";
                        var304_289 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                        var305_290 = PolyClassMachine.ofGuarded((ScriptValue)var301_286);
                        v45 /* !! */  = var305_290 != null ? ScriptValue.of((boolean)var305_290.tm$82_set_typed(var302_287, var303_288, var304_289)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var301_286, (ScriptValue)ScriptValue.of((String)var302_287), (ScriptValue)ScriptValue.of((String)var303_288), (ScriptValue)var304_289, (ScriptContext)var1_1);
                    } else {
                        v45 /* !! */  = ScriptValue.NULL;
                    }
                    if (var293_279 <= 0.0) {
                        var306_291 = var1_1.getClassOrVar("Machine");
                        if (var306_291 != ScriptValue.NULL) {
                            var307_292 = "_saw_item_id";
                            var308_293 = "str";
                            var309_294 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                            var310_295 = PolyClassMachine.ofGuarded((ScriptValue)var306_291);
                            v46 /* !! */  = var310_295 != null ? ScriptValue.of((boolean)var310_295.tm$82_set_typed(var307_292, var308_293, var309_294)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var306_291, (ScriptValue)ScriptValue.of((String)var307_292), (ScriptValue)ScriptValue.of((String)var308_293), (ScriptValue)var309_294, (ScriptContext)var1_1);
                        } else {
                            v46 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var311_296 = var1_1.getClassOrVar("Machine");
                    if (var311_296 != ScriptValue.NULL) {
                        var312_297 = "_saw_progress";
                        var313_298 = "int";
                        var314_299 = var256_243;
                        var315_300 = PolyClassMachine.ofGuarded((ScriptValue)var311_296);
                        v47 /* !! */  = var315_300 != null ? ScriptValue.of((boolean)var315_300.tm$82_set_typed(var312_297, var313_298, var314_299)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var311_296, (ScriptValue)ScriptValue.of((String)var312_297), (ScriptValue)ScriptValue.of((String)var313_298), (ScriptValue)var314_299, (ScriptContext)var1_1);
                    } else {
                        v47 /* !! */  = ScriptValue.NULL;
                    }
                }
            }
        }
        var316_301 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var316_301.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "_saw_act"));
        var316_301.val("is_now", var1_1.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)var316_301);
        Saw.FILE_SCOPE = var0.build();
    }
}
