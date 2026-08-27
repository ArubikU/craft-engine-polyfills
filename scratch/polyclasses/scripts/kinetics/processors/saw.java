/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassBelt
 *  dev.arubik.craftengine.script.PolyClassBlock
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassContraptionWorld
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
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
import dev.arubik.craftengine.script.PolyClassBlock;
import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassContraptionWorld;
import dev.arubik.craftengine.script.PolyClassMachine_v3;
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
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
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        List list = polyClassMachine_v3 != null ? polyClassMachine_v3.tl$147_recipes() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
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
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        List list = polyClassMachine_v3 != null ? polyClassMachine_v3.tl$147_recipes() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
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
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        List list = polyClassMachine_v3 != null ? polyClassMachine_v3.tl$147_recipes() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
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
            PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
            object = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
            if (scriptContext.getStr("filter_id").equals("") ^ true) {
                PolyClassMachine_v3 polyClassMachine_v3;
                List list = scriptValue2 != ScriptValue.NULL ? ((polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine_v3.tl$147_recipes() : ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext))) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("outs");
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("ins");
                if (list != null) {
                    for (ScriptValue scriptValue8 : list) {
                        PolyClassRecipe polyClassRecipe;
                        PolyClassRecipe polyClassRecipe2;
                        builder.val("r", scriptValue8);
                        ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? ((polyClassRecipe2 = PolyClassRecipe.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassRecipe2.pg$7_inputs() : PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                        builder.val("ins", scriptValue9);
                        scriptValue7 = scriptValue9;
                        if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue7, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                        ScriptValue scriptValue10 = scriptValue8 != ScriptValue.NULL ? ((polyClassRecipe = PolyClassRecipe.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassRecipe.pg$4_outputs() : PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                        builder.val("outs", scriptValue10);
                        scriptValue6 = scriptValue10;
                        if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
                        return scriptContext.getClassOrVar("filter_id");
                    }
                }
            }
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "_saw_recipe_index";
                String string3 = "int";
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                object2 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = object2;
            builder.val("idx", scriptValue11);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue11, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                double d2 = 0.0;
                ScriptValue scriptValue12 = ScriptValue.of((double)0.0);
                builder.val("idx", scriptValue12);
            }
            double d3 = (d = scriptValue5.asNum()) == 0.0 ? 0.0 : scriptContext.getNum("idx") % d;
            ScriptValue scriptValue13 = ScriptValue.of((double)d3);
            builder.val("idx", scriptValue13);
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "_saw_recipe_index";
                String string4 = "int";
                double d4 = scriptValue5.asNum();
                ScriptValue scriptValue14 = ScriptValue.of((double)(d4 == 0.0 ? 0.0 : (d3 + 1.0) % d4));
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v2 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue14)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("item_id", scriptContext.getClassOrVar("item_id"));
            builder3.val("target_idx", ScriptValue.of((double)d3));
            ScriptValue scriptValue15 = Saw._sawRecipeAt(builder3);
            builder.val("picked", scriptValue15);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue15, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                ScriptValue scriptValue16 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("outs", scriptValue16);
                if (ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue16, (ScriptContext)scriptContext).asNum() > 0.0) {
                    return PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext);
                }
            }
            return scriptContext.getClassOrVar("null");
        }
        PolyClassRegistry polyClassRegistry = PolyClassRegistry.ofVar((ScriptContext)scriptContext, (String)"Registry");
        ScriptValue scriptValue17 = polyClassRegistry != null ? polyClassRegistry.pg$0_recipes() : ((scriptValue = scriptContext.getClassOrVar("Registry")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        PolyClassRecipeRegistry polyClassRecipeRegistry = PolyClassRecipeRegistry.ofGuarded((ScriptValue)scriptValue17);
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)(polyClassRecipeRegistry != null ? polyClassRecipeRegistry.pg$2_stonecutter() : PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)scriptValue17, (ScriptContext)scriptContext)), (ScriptValue)scriptContext.getClassOrVar("item_id"), (ScriptContext)scriptContext);
        builder.val("stone_candidates", (ScriptValue)callSite);
        if (scriptContext.getStr("filter_id").equals("") ^ true) {
            List list = ScriptProgram.elementsOf((ScriptValue)callSite);
            ScriptValue scriptValue18 = scriptContext.getClassOrVar("outs");
            if (list != null) {
                for (ScriptValue scriptValue19 : list) {
                    builder.val("r", scriptValue19);
                    ScriptValue scriptValue20 = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL;
                    builder.val("outs", scriptValue20);
                    scriptValue18 = scriptValue20;
                    if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue18, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
                    return scriptContext.getClassOrVar("filter_id");
                }
            }
        }
        ScriptValue scriptValue21 = ScriptFormula.callBuiltin1((String)"size", (ScriptValue)callSite, (ScriptContext)scriptContext);
        builder.val("stone_count", scriptValue21);
        if (scriptValue21.asNum() > 0.0) {
            double d;
            Object object3;
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "_saw_recipe_index";
                String string5 = "int";
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                object3 = polyClassMachine_v3 != null ? polyClassMachine_v3.tm$34_get_typed(string, string5) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue22 = object3;
            builder.val("idx", scriptValue22);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue22, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                double d5 = 0.0;
                ScriptValue scriptValue23 = ScriptValue.of((double)0.0);
                builder.val("idx", scriptValue23);
            }
            double d6 = (d = scriptValue21.asNum()) == 0.0 ? 0.0 : scriptContext.getNum("idx") % d;
            ScriptValue scriptValue24 = ScriptValue.of((double)d6);
            builder.val("idx", scriptValue24);
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "_saw_recipe_index";
                String string6 = "int";
                double d7 = scriptValue21.asNum();
                ScriptValue scriptValue25 = ScriptValue.of((double)(d7 == 0.0 ? 0.0 : (d6 + 1.0) % d7));
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue2);
                v4 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string6, scriptValue25)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
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
        PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player");
        ScriptValue scriptValue2 = polyClassPlayer_v2 != null ? polyClassPlayer_v2.pg$48_main_hand() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        builder.val("held", scriptValue2);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                String string = "_saw_filter";
                String string2 = "str";
                ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue3);
                v0 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string2, scriptValue4)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue4, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                String string = "_saw_filter";
                String string3 = "str";
                ScriptValue scriptValue6 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue5);
                v1 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue6)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
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
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "facing";
        PolyClassBlock polyClassBlock = PolyClassBlock.ofGuarded((ScriptValue)scriptValue2);
        ScriptValue scriptValue3 = polyClassBlock != null ? polyClassBlock.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        builder.val("facing", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        if (scriptValue4 != ScriptValue.NULL) {
            String string2 = "_saw_rpm_sign";
            String string3 = "int";
            PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue4);
            object = polyClassMachine_v32 != null ? polyClassMachine_v32.tm$34_get_typed(string2, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
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
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue);
                v0 = polyClassMachine_v3 != null ? ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string2, scriptValue2)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
        PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine_v3 != null ? polyClassMachine_v3.pg$139_block() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "face";
        PolyClassBlock polyClassBlock = PolyClassBlock.ofGuarded((ScriptValue)scriptValue3);
        ScriptValue scriptValue4 = polyClassBlock != null ? polyClassBlock.tm$24_property(string) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        builder.val("face", scriptValue4);
        PolyClassMachine_v3 polyClassMachine_v32 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue5 = polyClassMachine_v32 != null ? polyClassMachine_v32.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string2 = "facing";
        PolyClassBlock polyClassBlock2 = PolyClassBlock.ofGuarded((ScriptValue)scriptValue5);
        ScriptValue scriptValue6 = polyClassBlock2 != null ? polyClassBlock2.tm$24_property(string2) : PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        builder.val("facing", scriptValue6);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"wall")) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                String string3 = "back";
                PolyClassMachine_v3 polyClassMachine_v33 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue7);
                v0 = polyClassMachine_v33 != null ? ScriptValue.of((boolean)polyClassMachine_v33.tm$40_set_rpm_input(string3)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            if (scriptValue7 != ScriptValue.NULL) {
                String string4 = "";
                PolyClassMachine_v3 polyClassMachine_v34 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue7);
                v1 = polyClassMachine_v34 != null ? ScriptValue.of((boolean)polyClassMachine_v34.tm$44_set_rpm_output_same(string4)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string4), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"north") || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"south")) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                String string5 = "north,south";
                PolyClassMachine_v3 polyClassMachine_v35 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue8);
                v2 = polyClassMachine_v35 != null ? ScriptValue.of((boolean)polyClassMachine_v35.tm$40_set_rpm_input(string5)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            if (scriptValue8 != ScriptValue.NULL) {
                String string6 = "north,south";
                PolyClassMachine_v3 polyClassMachine_v36 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue8);
                v3 = polyClassMachine_v36 != null ? ScriptValue.of((boolean)polyClassMachine_v36.tm$44_set_rpm_output_same(string6)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string6), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                String string7 = "east,west";
                PolyClassMachine_v3 polyClassMachine_v37 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue9);
                v4 = polyClassMachine_v37 != null ? ScriptValue.of((boolean)polyClassMachine_v37.tm$40_set_rpm_input(string7)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string7), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
            if (scriptValue9 != ScriptValue.NULL) {
                String string8 = "east,west";
                PolyClassMachine_v3 polyClassMachine_v38 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue9);
                v5 = polyClassMachine_v38 != null ? ScriptValue.of((boolean)polyClassMachine_v38.tm$44_set_rpm_output_same(string8)) : PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string8), (ScriptContext)scriptContext);
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
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofVar((ScriptContext)scriptContext, (String)"Machine");
                if (!(ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine_v3 != null ? polyClassMachine_v3.pg$191_contraption() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) break block4;
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
                PolyClassMachine_v3 polyClassMachine_v3 = PolyClassMachine_v3.ofGuarded((ScriptValue)scriptValue5);
                v0 = polyClassMachine_v3 != null ? polyClassMachine_v3.um$19_drop_item_toward(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
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
                    var14_10 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var16_12 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var18_14 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var20_16 = ScriptValue.of((String)((var14_10 != null ? var14_10.pg$200_x() : ((var15_11 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var15_11, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var16_12 != null ? var16_12.pg$202_y() : ((var17_13 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var17_13, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var18_14 != null ? var18_14.pg$206_z() : ((var19_15 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var19_15, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr()));
                    var0.val("_hold_key", var20_16);
                    var21_17 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var23_19 = var21_17 != null ? var21_17.pg$191_contraption() : ((var22_18 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var22_18, (ScriptContext)var1_1) : ScriptValue.NULL);
                    var0.val("contraption", var23_19);
                    var24_20 = ScriptContext.builder().copyFrom(var1_1);
                    var24_20.val("effective_rpm", (ScriptValue)(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var23_19 != ScriptValue.NULL ? ((var25_21 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var25_21.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm")));
                    Saw._sawUpdateRpmSign(var24_20);
                    var26_22 = 0.0;
                    var28_23 = ScriptValue.of((double)0.0);
                    var0.val("is_now", var28_23);
                    var30_24 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var29_26 = var30_24 != null ? var30_24.pg$139_block() : ((var31_25 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)var31_25, (ScriptContext)var1_1) : ScriptValue.NULL);
                    var32_27 = "face";
                    var33_28 = PolyClassBlock.ofGuarded((ScriptValue)var29_26);
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
                    var49_42 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var51_44 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var53_46 = ScriptFormula.addPolymorphic((ScriptValue)(var49_42 != null ? var49_42.pg$200_x() : ((var50_43 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var50_43, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var51_44 != null ? var51_44.pg$133_facing_dx() : ((var52_45 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var52_45, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("tx", var53_46);
                    var54_47 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var56_49 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var58_51 = ScriptFormula.addPolymorphic((ScriptValue)(var54_47 != null ? var54_47.pg$202_y() : ((var55_48 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var55_48, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var56_49 != null ? var56_49.pg$129_facing_dy() : ((var57_50 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var57_50, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("ty", var58_51);
                    var59_52 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var61_54 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
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
                                var81_74 = PolyClassMachine_v3.ofGuarded((ScriptValue)var78_71);
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
                            var91_84 = PolyClassMachine_v3.ofGuarded((ScriptValue)var87_80);
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
            var99_90 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
            var101_92 = var99_90 != null ? var99_90.pg$137_facing_block() : ((var100_91 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var100_91, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("fb", var101_92);
            if (!(var34_29 ^ true)) ** GOTO lbl-1000
            v13 = var101_92 != ScriptValue.NULL ? ((var102_93 = PolyClassBlock.ofGuarded((ScriptValue)var101_92)) != null ? var102_93.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var101_92, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
            if (v13 ^ true) {
                v14 = true;
            } else lbl-1000:
            // 2 sources

            {
                v14 = false;
            }
            if (!v14) ** GOTO lbl-1000
            var103_94 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
            var103_94.val("id", (ScriptValue)(var101_92 != ScriptValue.NULL ? ((var104_95 = PolyClassBlock.ofGuarded((ScriptValue)var101_92)) != null ? var104_95.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var101_92, (ScriptContext)var1_1)) : ScriptValue.NULL));
            if (TreeUtils._isChoppable((ScriptContext.Builder)var103_94).asBool()) {
                v15 = true;
            } else lbl-1000:
            // 2 sources

            {
                v15 = false;
            }
            if (v15) {
                var105_96 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                var105_96.val("id", (ScriptValue)(var101_92 != ScriptValue.NULL ? ((var106_97 = PolyClassBlock.ofGuarded((ScriptValue)var101_92)) != null ? var106_97.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var101_92, (ScriptContext)var1_1)) : ScriptValue.NULL));
                if (TreeUtils._isLog((ScriptContext.Builder)var105_96).asBool()) {
                    var107_98 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var107_98.val("contraption", var1_1.getClassOrVar("null"));
                    var108_99 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var110_101 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var107_98.val("base_x", ScriptFormula.addPolymorphic((ScriptValue)(var108_99 != null ? var108_99.pg$200_x() : ((var109_100 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var109_100, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var110_101 != null ? var110_101.pg$133_facing_dx() : ((var111_102 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var111_102, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var112_103 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var114_105 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var107_98.val("base_y", ScriptFormula.addPolymorphic((ScriptValue)(var112_103 != null ? var112_103.pg$202_y() : ((var113_104 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var113_104, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var114_105 != null ? var114_105.pg$129_facing_dy() : ((var115_106 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var115_106, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var116_107 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var118_109 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var107_98.val("base_z", ScriptFormula.addPolymorphic((ScriptValue)(var116_107 != null ? var116_107.pg$206_z() : ((var117_108 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var117_108, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var118_109 != null ? var118_109.pg$131_facing_dz() : ((var119_110 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var119_110, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var107_98.val("speed", ScriptValue.of((double)var96_88));
                    TreeUtils._fellTree((ScriptContext.Builder)var107_98);
                } else {
                    var120_111 = var1_1.getClassOrVar("Machine");
                    if (var120_111 != ScriptValue.NULL) {
                        var121_112 = var101_92;
                        var122_113 = var96_88;
                        var124_114 = PolyClassMachine_v3.ofGuarded((ScriptValue)var120_111);
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
                    var133_123 = PolyClassMachine_v3.ofGuarded((ScriptValue)var130_120);
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
            var137_126 = PolyClassMachine_v3.ofVar((ScriptContext)var1_1, (String)"Machine");
            var139_128 = var137_126 != null ? var137_126.pg$158_belt() : ((var138_127 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "belt", (ScriptValue)var138_127, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("belt", var139_128);
            var141_130 = ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var23_19 != ScriptValue.NULL ? ((var140_129 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var140_129.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm");
            var0.val("belt_rpm", var141_130);
            var142_131 = var1_1.getClassOrVar("Machine");
            if (var142_131 != ScriptValue.NULL) {
                var143_132 = "_saw_item_id";
                var144_133 = "str";
                var145_134 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
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
                        if (var142_131 != ScriptValue.NULL) {
                            var155_144 = "_saw_item_id";
                            var156_145 = "str";
                            var157_146 = var154_143 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var154_143, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var158_147 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                            v20 /* !! */  = var158_147 != null ? ScriptValue.of((boolean)var158_147.tm$82_set_typed(var155_144, var156_145, var157_146)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var155_144), (ScriptValue)ScriptValue.of((String)var156_145), (ScriptValue)var157_146, (ScriptContext)var1_1);
                        } else {
                            v20 /* !! */  = ScriptValue.NULL;
                        }
                        if (var142_131 != ScriptValue.NULL) {
                            var159_148 = "_saw_out_id";
                            var160_149 = "str";
                            var161_150 = var152_141;
                            var162_151 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                            v21 /* !! */  = var162_151 != null ? ScriptValue.of((boolean)var162_151.tm$82_set_typed(var159_148, var160_149, var161_150)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var159_148), (ScriptValue)ScriptValue.of((String)var160_149), (ScriptValue)var161_150, (ScriptContext)var1_1);
                        } else {
                            v21 /* !! */  = ScriptValue.NULL;
                        }
                        if (var142_131 != ScriptValue.NULL) {
                            var163_152 = "_saw_count";
                            var164_153 = "int";
                            var165_154 = var154_143 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var154_143, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var166_155 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                            v22 /* !! */  = var166_155 != null ? ScriptValue.of((boolean)var166_155.tm$82_set_typed(var163_152, var164_153, var165_154)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var163_152), (ScriptValue)ScriptValue.of((String)var164_153), (ScriptValue)var165_154, (ScriptContext)var1_1);
                        } else {
                            v22 /* !! */  = ScriptValue.NULL;
                        }
                        if (var142_131 != ScriptValue.NULL) {
                            var167_156 = "_saw_progress";
                            var168_157 = "int";
                            var169_158 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            var170_159 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                            v23 /* !! */  = var170_159 != null ? ScriptValue.of((boolean)var170_159.tm$82_set_typed(var167_156, var168_157, var169_158)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var167_156), (ScriptValue)ScriptValue.of((String)var168_157), (ScriptValue)var169_158, (ScriptContext)var1_1);
                        } else {
                            v23 /* !! */  = ScriptValue.NULL;
                        }
                        if (var142_131 != ScriptValue.NULL) {
                            var171_160 = "_saw_from_ground";
                            var172_161 = "int";
                            var173_162 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            var174_163 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                            v24 /* !! */  = var174_163 != null ? ScriptValue.of((boolean)var174_163.tm$82_set_typed(var171_160, var172_161, var173_162)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var171_160), (ScriptValue)ScriptValue.of((String)var172_161), (ScriptValue)var173_162, (ScriptContext)var1_1);
                        } else {
                            v24 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var175_164 = ScriptContext.builder().copyFrom(var1_1);
                    var176_165 = Saw._sawInputFace(var175_164);
                    var0.val("in_face", var176_165);
                    var177_166 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                    var177_166.val("face", var176_165);
                    var177_166.val("validator", var1_1.getClassOrVar("null"));
                    var177_166.val("amount", var1_1.getClassOrVar("null"));
                    var178_167 = BeltUtils.beltTake((ScriptContext.Builder)var177_166);
                    var0.val("taken", var178_167);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var178_167, (ScriptContext)var1_1).asBool() ^ true) {
                        var179_168 = ScriptContext.builder().copyFrom(var1_1);
                        var179_168.val("item_id", (ScriptValue)(var178_167 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var178_167, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var180_169 = Saw._sawOutputFor(var179_168);
                        var0.val("out_id", var180_169);
                        if (ScriptFormula.valuesEqual((ScriptValue)var180_169, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            if (var142_131 != ScriptValue.NULL) {
                                var181_170 = "_saw_item_id";
                                var182_171 = "str";
                                var183_172 = var178_167 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var178_167, (ScriptContext)var1_1) : ScriptValue.NULL;
                                var184_173 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                                v25 /* !! */  = var184_173 != null ? ScriptValue.of((boolean)var184_173.tm$82_set_typed(var181_170, var182_171, var183_172)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var181_170), (ScriptValue)ScriptValue.of((String)var182_171), (ScriptValue)var183_172, (ScriptContext)var1_1);
                            } else {
                                v25 /* !! */  = ScriptValue.NULL;
                            }
                            if (var142_131 != ScriptValue.NULL) {
                                var185_174 = "_saw_out_id";
                                var186_175 = "str";
                                var187_176 = var180_169;
                                var188_177 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                                v26 /* !! */  = var188_177 != null ? ScriptValue.of((boolean)var188_177.tm$82_set_typed(var185_174, var186_175, var187_176)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var185_174), (ScriptValue)ScriptValue.of((String)var186_175), (ScriptValue)var187_176, (ScriptContext)var1_1);
                            } else {
                                v26 /* !! */  = ScriptValue.NULL;
                            }
                            if (var142_131 != ScriptValue.NULL) {
                                var189_178 = "_saw_count";
                                var190_179 = "int";
                                var191_180 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var178_167, (ScriptContext)var1_1);
                                var192_181 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                                v27 /* !! */  = var192_181 != null ? ScriptValue.of((boolean)var192_181.tm$82_set_typed(var189_178, var190_179, var191_180)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var189_178), (ScriptValue)ScriptValue.of((String)var190_179), (ScriptValue)var191_180, (ScriptContext)var1_1);
                            } else {
                                v27 /* !! */  = ScriptValue.NULL;
                            }
                            if (var142_131 != ScriptValue.NULL) {
                                var193_182 = "_saw_progress";
                                var194_183 = "int";
                                var195_184 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                var196_185 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                                v28 /* !! */  = var196_185 != null ? ScriptValue.of((boolean)var196_185.tm$82_set_typed(var193_182, var194_183, var195_184)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var193_182), (ScriptValue)ScriptValue.of((String)var194_183), (ScriptValue)var195_184, (ScriptContext)var1_1);
                            } else {
                                v28 /* !! */  = ScriptValue.NULL;
                            }
                            if (var142_131 != ScriptValue.NULL) {
                                var197_186 = "_saw_from_ground";
                                var198_187 = "int";
                                var199_188 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0);
                                var200_189 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                                v29 /* !! */  = var200_189 != null ? ScriptValue.of((boolean)var200_189.tm$82_set_typed(var197_186, var198_187, var199_188)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var197_186), (ScriptValue)ScriptValue.of((String)var198_187), (ScriptValue)var199_188, (ScriptContext)var1_1);
                            } else {
                                v29 /* !! */  = ScriptValue.NULL;
                            }
                        } else {
                            var201_190 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                            var201_190.val("face", var176_165);
                            var201_190.val("item", var178_167);
                            BeltUtils.beltGive((ScriptContext.Builder)var201_190);
                        }
                    } else {
                        if (var142_131 != ScriptValue.NULL) {
                            var205_191 = 0.7;
                            var207_192 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                            v30 /* !! */  = var207_192 != null ? var207_192.tm$94_nearby_entities(var205_191) : PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((double)var205_191), (ScriptContext)var1_1);
                        } else {
                            v30 /* !! */  = ScriptValue.NULL;
                        }
                        var202_193 = ScriptProgram.elementsOf((ScriptValue)v30 /* !! */ );
                        var208_194 = var1_1.getClassOrVar("out_id");
                        if (var202_193 != null) {
                            for (ScriptValue var204_196 : var202_193) {
                                var0.val("entity", var204_196);
                                var209_197 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                                var209_197.val("entity", var204_196);
                                if (!Utils.isRestingItem(var209_197).asBool()) continue;
                                var210_198 = ScriptContext.builder().copyFrom(var1_1);
                                var210_198.val("item_id", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var204_196 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var204_196, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                                var211_199 = Saw._sawOutputFor(var210_198);
                                var0.val("out_id", var211_199);
                                var208_194 = var211_199;
                                if (!(ScriptFormula.valuesEqual((ScriptValue)var208_194, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                                if (var142_131 != ScriptValue.NULL) {
                                    var212_200 = "_saw_item_id";
                                    var213_201 = "str";
                                    var214_202 = PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var204_196 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var204_196, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    var215_203 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                                    v31 /* !! */  = var215_203 != null ? ScriptValue.of((boolean)var215_203.tm$82_set_typed(var212_200, var213_201, (ScriptValue)var214_202)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var212_200), (ScriptValue)ScriptValue.of((String)var213_201), (ScriptValue)var214_202, (ScriptContext)var1_1);
                                } else {
                                    v31 /* !! */  = ScriptValue.NULL;
                                }
                                if (var142_131 != ScriptValue.NULL) {
                                    var216_204 = "_saw_out_id";
                                    var217_205 = "str";
                                    var218_206 = var208_194;
                                    var219_207 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                                    v32 /* !! */  = var219_207 != null ? ScriptValue.of((boolean)var219_207.tm$82_set_typed(var216_204, var217_205, var218_206)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var216_204), (ScriptValue)ScriptValue.of((String)var217_205), (ScriptValue)var218_206, (ScriptContext)var1_1);
                                } else {
                                    v32 /* !! */  = ScriptValue.NULL;
                                }
                                if (var142_131 != ScriptValue.NULL) {
                                    var220_208 = "_saw_count";
                                    var221_209 = "int";
                                    var222_210 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)(var204_196 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var204_196, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    var223_211 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                                    v33 /* !! */  = var223_211 != null ? ScriptValue.of((boolean)var223_211.tm$82_set_typed(var220_208, var221_209, var222_210)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var220_208), (ScriptValue)ScriptValue.of((String)var221_209), (ScriptValue)var222_210, (ScriptContext)var1_1);
                                } else {
                                    v33 /* !! */  = ScriptValue.NULL;
                                }
                                if (var142_131 != ScriptValue.NULL) {
                                    var224_212 = "_saw_progress";
                                    var225_213 = "int";
                                    var226_214 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                    var227_215 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                                    v34 /* !! */  = var227_215 != null ? ScriptValue.of((boolean)var227_215.tm$82_set_typed(var224_212, var225_213, var226_214)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var224_212), (ScriptValue)ScriptValue.of((String)var225_213), (ScriptValue)var226_214, (ScriptContext)var1_1);
                                } else {
                                    v34 /* !! */  = ScriptValue.NULL;
                                }
                                if (var142_131 != ScriptValue.NULL) {
                                    var228_216 = "_saw_from_ground";
                                    var229_217 = "int";
                                    var230_218 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0);
                                    var231_219 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                                    v35 /* !! */  = var231_219 != null ? ScriptValue.of((boolean)var231_219.tm$82_set_typed(var228_216, var229_217, var230_218)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var228_216), (ScriptValue)ScriptValue.of((String)var229_217), (ScriptValue)var230_218, (ScriptContext)var1_1);
                                } else {
                                    v35 /* !! */  = ScriptValue.NULL;
                                }
                                v36 /* !! */  = var204_196 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var204_196, (ScriptContext)var1_1) : ScriptValue.NULL;
                                break;
                            }
                        }
                    }
                }
            } else {
                if (var142_131 != ScriptValue.NULL) {
                    var232_220 = "_saw_progress";
                    var233_221 = "int";
                    var234_222 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                    v37 /* !! */  = var234_222 != null ? var234_222.tm$34_get_typed(var232_220, var233_221) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var232_220), (ScriptValue)ScriptValue.of((String)var233_221), (ScriptContext)var1_1);
                } else {
                    v37 /* !! */  = ScriptValue.NULL;
                }
                var235_223 = v37 /* !! */ ;
                var0.val("progress", var235_223);
                if (ScriptFormula.valuesEqual((ScriptValue)var235_223, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var236_224 = 0.0;
                    var238_225 = ScriptValue.of((double)0.0);
                    var0.val("progress", var238_225);
                }
                var239_226 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)Math.abs(var141_130.asNum())));
                var0.val("progress", var239_226);
                if (var142_131 != ScriptValue.NULL) {
                    var241_227 = ScriptContext.builder().copyFrom(var1_1);
                    var241_227.val("current_rpm", var141_130);
                    var240_228 = Saw._sawSuCost(var241_227);
                    var242_229 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                    v38 /* !! */  = var242_229 != null ? ScriptValue.of((boolean)var242_229.tm$56_report_su(var240_228.asNum())) : PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var142_131, (ScriptValue)var240_228, (ScriptContext)var1_1);
                } else {
                    v38 /* !! */  = ScriptValue.NULL;
                }
                if (var239_226.asNum() >= var11_8) {
                    if (var142_131 != ScriptValue.NULL) {
                        var243_230 = "_saw_out_id";
                        var244_231 = "str";
                        var245_232 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                        v39 /* !! */  = var245_232 != null ? var245_232.tm$34_get_typed(var243_230, var244_231) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var243_230), (ScriptValue)ScriptValue.of((String)var244_231), (ScriptContext)var1_1);
                    } else {
                        v39 /* !! */  = ScriptValue.NULL;
                    }
                    var246_233 = v39 /* !! */ ;
                    var0.val("out_id", var246_233);
                    if (var142_131 != ScriptValue.NULL) {
                        var247_234 = "_saw_count";
                        var248_235 = "int";
                        var249_236 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                        v40 /* !! */  = var249_236 != null ? var249_236.tm$34_get_typed(var247_234, var248_235) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var247_234), (ScriptValue)ScriptValue.of((String)var248_235), (ScriptContext)var1_1);
                    } else {
                        v40 /* !! */  = ScriptValue.NULL;
                    }
                    var250_237 = v40 /* !! */ ;
                    var0.val("remaining", var250_237);
                    if (ScriptFormula.valuesEqual((ScriptValue)var250_237, (ScriptValue)var1_1.getClassOrVar("null")) != false || var250_237.asNum() <= 0.0 != false) {
                        var251_238 = 1.0;
                        var253_239 = ScriptValue.of((double)1.0);
                        var0.val("remaining", var253_239);
                    }
                    if (var142_131 != ScriptValue.NULL) {
                        var254_240 = "_saw_from_ground";
                        var255_241 = "int";
                        var256_242 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                        v41 /* !! */  = var256_242 != null ? var256_242.tm$34_get_typed(var254_240, var255_241) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var254_240), (ScriptValue)ScriptValue.of((String)var255_241), (ScriptContext)var1_1);
                    } else {
                        v41 /* !! */  = ScriptValue.NULL;
                    }
                    var257_243 = v41 /* !! */ ;
                    var0.val("from_ground", var257_243);
                    var258_244 = ScriptContext.builder().copyFrom(var1_1);
                    var258_244.val("item_id", var1_1.getClassOrVar("active_id"));
                    var258_244.val("out_id", var246_233);
                    var259_245 = Saw._sawRecipeFor(var258_244);
                    var0.val("recipe", var259_245);
                    var260_246 = ScriptFormula.valuesEqual((ScriptValue)var259_245, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var259_245 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "outputs", (ScriptValue)var259_245, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("null");
                    var0.val("result", var260_246);
                    if (ScriptFormula.valuesEqual((ScriptValue)var260_246, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        v42 /* !! */  = var260_246 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "items", (ScriptValue)var260_246, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var261_247 = new ArrayList<ScriptValue>();
                        var261_247.add(ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)var246_233, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0)), (ScriptContext)var1_1));
                        v42 /* !! */  = new ScriptValue.Array(var261_247);
                    }
                    var262_248 /* !! */  = v42 /* !! */ ;
                    var0.val("items", var262_248 /* !! */ );
                    var263_249 = ScriptProgram.elementsOf((ScriptValue)var262_248 /* !! */ );
                    var266_250 = var1_1.getClassOrVar("leftover");
                    if (var263_249 != null) {
                        for (ScriptValue var265_252 : var263_249) {
                            var0.val("out_item", var265_252);
                            if (ScriptFormula.valuesEqual((ScriptValue)var257_243, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0))) != false || ScriptFormula.valuesEqual((ScriptValue)var257_243, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0))) != false) {
                                var267_253 = ScriptContext.builder().copyFrom(var1_1);
                                var267_253.val("item", var265_252);
                                Saw._sawDepositDirectional(var267_253);
                                continue;
                            }
                            if (var139_128 != ScriptValue.NULL) {
                                var268_254 = var265_252;
                                var269_255 = PolyClassBelt.ofGuarded((ScriptValue)var139_128);
                                v43 /* !! */  = var269_255 != null ? var269_255.tm$10_put(var268_254) : PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var139_128, (ScriptValue)var268_254, (ScriptContext)var1_1);
                            } else {
                                v43 /* !! */  = ScriptValue.NULL;
                            }
                            var270_256 = v43 /* !! */ ;
                            var0.val("leftover", var270_256);
                            var266_250 = var270_256;
                            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var266_250, (ScriptContext)var1_1).asBool() ^ true)) continue;
                            var271_257 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var271_257.val("item", var266_250);
                            Utils.1._deposit((ScriptContext.Builder)var271_257);
                        }
                    }
                    var272_258 = var1_1.getNum("remaining") - 1.0;
                    var274_259 = ScriptValue.of((double)var272_258);
                    var0.val("remaining", var274_259);
                    if (var142_131 != ScriptValue.NULL) {
                        var275_260 = "_saw_count";
                        var276_261 = "int";
                        var277_262 = ScriptValue.of((double)var272_258);
                        var278_263 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                        v44 /* !! */  = var278_263 != null ? ScriptValue.of((boolean)var278_263.tm$82_set_typed(var275_260, var276_261, var277_262)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var275_260), (ScriptValue)ScriptValue.of((String)var276_261), (ScriptValue)var277_262, (ScriptContext)var1_1);
                    } else {
                        v44 /* !! */  = ScriptValue.NULL;
                    }
                    if (var142_131 != ScriptValue.NULL) {
                        var279_264 = "_saw_progress";
                        var280_265 = "int";
                        var281_266 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                        var282_267 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                        v45 /* !! */  = var282_267 != null ? ScriptValue.of((boolean)var282_267.tm$82_set_typed(var279_264, var280_265, var281_266)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var279_264), (ScriptValue)ScriptValue.of((String)var280_265), (ScriptValue)var281_266, (ScriptContext)var1_1);
                    } else {
                        v45 /* !! */  = ScriptValue.NULL;
                    }
                    if (var272_258 <= 0.0) {
                        if (var142_131 != ScriptValue.NULL) {
                            var283_268 = "_saw_item_id";
                            var284_269 = "str";
                            var285_270 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                            var286_271 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                            v46 /* !! */  = var286_271 != null ? ScriptValue.of((boolean)var286_271.tm$82_set_typed(var283_268, var284_269, var285_270)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var283_268), (ScriptValue)ScriptValue.of((String)var284_269), (ScriptValue)var285_270, (ScriptContext)var1_1);
                        } else {
                            v46 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else if (var142_131 != ScriptValue.NULL) {
                    var287_272 = "_saw_progress";
                    var288_273 = "int";
                    var289_274 = var239_226;
                    var290_275 = PolyClassMachine_v3.ofGuarded((ScriptValue)var142_131);
                    v47 /* !! */  = var290_275 != null ? ScriptValue.of((boolean)var290_275.tm$82_set_typed(var287_272, var288_273, var289_274)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var142_131, (ScriptValue)ScriptValue.of((String)var287_272), (ScriptValue)ScriptValue.of((String)var288_273), (ScriptValue)var289_274, (ScriptContext)var1_1);
                } else {
                    v47 /* !! */  = ScriptValue.NULL;
                }
            }
        }
        var291_276 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var291_276.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "_saw_act"));
        var291_276.val("is_now", var1_1.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)var291_276);
        Saw.FILE_SCOPE = var0.build();
    }
}
