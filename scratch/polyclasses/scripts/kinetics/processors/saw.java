/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBelt
 *  dev.arubik.craftengine.script.PolyClassBlock_v4
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassContraptionWorld
 *  dev.arubik.craftengine.script.PolyClassMachine
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
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.BeltUtils
 *  dev.arubik.craftengine.script.gen.TreeUtils
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassBelt;
import dev.arubik.craftengine.script.PolyClassBlock_v4;
import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassContraptionWorld;
import dev.arubik.craftengine.script.PolyClassMachine;
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
            ScriptValue.Obj obj;
            Object object2;
            String string = "_saw_filter";
            String string2 = "str";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
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
            Object object3;
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
                ScriptValue.Obj obj;
                Object object4;
                String string = "_saw_recipe_index";
                String string3 = "int";
                if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                    object3 = polyClassMachine.tm$34_get_typed(string, string3);
                } else {
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
                }
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = object3;
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
                ScriptValue.Obj obj;
                Object object5;
                String string = "_saw_recipe_index";
                String string4 = "int";
                double d4 = scriptValue5.asNum();
                ScriptValue scriptValue17 = ScriptValue.of((double)(d4 == 0.0 ? 0.0 : (d3 + 1.0) % d4));
                if (scriptValue16 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object5);
                    v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string4, scriptValue17));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
                }
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
            Object object6;
            ScriptValue scriptValue25 = scriptContext.getClassOrVar("Machine");
            if (scriptValue25 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "_saw_recipe_index";
                String string5 = "int";
                if (scriptValue25 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue25).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object7);
                    object6 = polyClassMachine.tm$34_get_typed(string, string5);
                } else {
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue25, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string5), (ScriptContext)scriptContext);
                }
            } else {
                object6 = ScriptValue.NULL;
            }
            ScriptValue scriptValue26 = object6;
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
                ScriptValue.Obj obj;
                Object object8;
                String string = "_saw_recipe_index";
                String string6 = "int";
                double d7 = scriptValue24.asNum();
                ScriptValue scriptValue30 = ScriptValue.of((double)(d7 == 0.0 ? 0.0 : (d6 + 1.0) % d7));
                if (scriptValue29 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object8);
                    v4 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string6, scriptValue30));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue29, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string6), (ScriptValue)scriptValue30, (ScriptContext)scriptContext);
                }
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
                ScriptValue.Obj obj;
                Object object;
                String string = "_saw_filter";
                String string2 = "str";
                ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue4));
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
                Object object2 = scriptValue6 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue6));
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
        builder.val("facing", (ScriptValue)callSite2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object3;
            String string2 = "_saw_rpm_sign";
            String string3 = "int";
            if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object3);
                object = polyClassMachine2.tm$34_get_typed(string2, string3);
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue2));
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
        ScriptValue scriptValue;
        CallSite callSite2;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine polyClassMachine = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine != null ? polyClassMachine.pg$139_block() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "face";
        if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Block")) {
            PolyClassBlock_v4 polyClassBlock_v4 = new PolyClassBlock_v4(object2);
            callSite2 = polyClassBlock_v4.tm$24_property(string);
        } else {
            callSite2 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        }
        CallSite callSite3 = callSite2;
        builder.val("face", (ScriptValue)callSite3);
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine2 != null ? polyClassMachine2.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string2 = "facing";
        if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v4 polyClassBlock_v4 = new PolyClassBlock_v4(object);
            callSite = polyClassBlock_v4.tm$24_property(string2);
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
                    PolyClassMachine polyClassMachine3 = new PolyClassMachine(object3);
                    v2 = ScriptValue.of((boolean)polyClassMachine3.tm$40_set_rpm_input(string3));
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
                    PolyClassMachine polyClassMachine4 = new PolyClassMachine(object4);
                    v3 = ScriptValue.of((boolean)polyClassMachine4.tm$44_set_rpm_output_same(string4));
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
                    PolyClassMachine polyClassMachine5 = new PolyClassMachine(object5);
                    v4 = ScriptValue.of((boolean)polyClassMachine5.tm$40_set_rpm_input(string5));
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
                    PolyClassMachine polyClassMachine6 = new PolyClassMachine(object6);
                    v5 = ScriptValue.of((boolean)polyClassMachine6.tm$44_set_rpm_output_same(string6));
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
                    PolyClassMachine polyClassMachine7 = new PolyClassMachine(object7);
                    v6 = ScriptValue.of((boolean)polyClassMachine7.tm$40_set_rpm_input(string7));
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
                    PolyClassMachine polyClassMachine8 = new PolyClassMachine(object8);
                    v7 = ScriptValue.of((boolean)polyClassMachine8.tm$44_set_rpm_output_same(string8));
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
                ScriptValue.Obj obj;
                Object object;
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("remaining"));
                arrayList.add(ScriptValue.of((double)(ScriptFormula.subscriptGet((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))).asNum() * 0.08)));
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.05));
                arrayList.add(ScriptValue.of((double)(ScriptFormula.subscriptGet((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0))).asNum() * 0.08)));
                v0 = scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine(object).um$19_drop_item_toward(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
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
        block199: {
            block197: {
                block198: {
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
                    if (var29_26 instanceof ScriptValue.Obj && (var34_29 = (var33_28 = (ScriptValue.Obj)var29_26).instance()) != null && !(var34_29 instanceof PolyClass) && var33_28.typeName().equals("Block")) {
                        var35_30 = new PolyClassBlock_v4(var34_29);
                        v4 = var35_30.tm$24_property(var32_27);
                    } else {
                        v4 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var29_26, (ScriptValue)ScriptValue.of((String)var32_27), (ScriptContext)var1_1);
                    }
                    var36_31 = ScriptFormula.valuesEqualStr((ScriptValue)v4, (String)"floor");
                    var37_32 = ScriptValue.of((boolean)var36_31);
                    var0.val("is_belt_facing", var37_32);
                    if (!(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block197;
                    var39_34 = ScriptFormula.valuesEqual((ScriptValue)(var23_19 != ScriptValue.NULL ? ((var38_33 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var38_33.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true;
                    var40_35 = ScriptValue.of((boolean)var39_34);
                    var0.val("is_rotational", var40_35);
                    if (var23_19 != ScriptValue.NULL) {
                        if (var23_19 instanceof ScriptValue.Obj && (var42_37 = (var41_36 = (ScriptValue.Obj)var23_19).instance()) != null && !(var42_37 instanceof PolyClass) && var41_36.typeName().equals("Contraption")) {
                            var43_38 = new PolyClassContraption(var42_37);
                            v5 /* !! */  = ScriptValue.of((boolean)var43_38.tm$48_is_moving());
                        } else {
                            v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)var23_19, (ScriptContext)var1_1);
                        }
                    } else {
                        v5 /* !! */  = ScriptValue.NULL;
                    }
                    var44_39 = v5 /* !! */ ;
                    var0.val("is_linear", var44_39);
                    var45_40 = (var39_34 != false || var44_39.asBool() != false) != false ? 1.0 : 0.0;
                    var47_41 = ScriptValue.of((double)var45_40);
                    var0.val("is_now", var47_41);
                    if (!(var45_40 > 0.0)) break block198;
                    if (var39_34) {
                        var48_42 = 10.0;
                        v6 = 10.0 == 0.0 ? 0.0 : Math.abs(var23_19 != ScriptValue.NULL ? ((var50_43 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var50_43.tg$71_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum()) / var48_42;
                        v7 = ScriptValue.of((double)Math.max(1.0, v6));
                    } else {
                        var51_44 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                        var51_44.val("contraption", var23_19);
                        v7 = Utils.1._linearBreakSpeed((ScriptContext.Builder)var51_44);
                    }
                    var52_45 = v7;
                    var0.val("speed", var52_45);
                    var53_46 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var55_48 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var57_50 = ScriptFormula.addPolymorphic((ScriptValue)(var53_46 != null ? var53_46.pg$200_x() : ((var54_47 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var54_47, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var55_48 != null ? var55_48.pg$133_facing_dx() : ((var56_49 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var56_49, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("tx", var57_50);
                    var58_51 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var60_53 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var62_55 = ScriptFormula.addPolymorphic((ScriptValue)(var58_51 != null ? var58_51.pg$202_y() : ((var59_52 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var59_52, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var60_53 != null ? var60_53.pg$129_facing_dy() : ((var61_54 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var61_54, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("ty", var62_55);
                    var63_56 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var65_58 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var67_60 = ScriptFormula.addPolymorphic((ScriptValue)(var63_56 != null ? var63_56.pg$206_z() : ((var64_57 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var64_57, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var65_58 != null ? var65_58.pg$131_facing_dz() : ((var66_59 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var66_59, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("tz", var67_60);
                    var68_62 = var23_19 != ScriptValue.NULL ? ((var69_61 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var69_61.pg$53_contraption_world() : PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var70_63 = var57_50;
                    var71_64 = var62_55;
                    var72_65 = var67_60;
                    if (var68_62 instanceof ScriptValue.Obj && (var74_67 = (var73_66 = (ScriptValue.Obj)var68_62).instance()) != null && !(var74_67 instanceof PolyClass) && var73_66.typeName().equals("ContraptionWorld")) {
                        var75_68 = new PolyClassContraptionWorld(var74_67);
                        v8 = var75_68.tm$8_real_block(var70_63.asNum(), var71_64.asNum(), var72_65.asNum());
                    } else {
                        v8 = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)var68_62, (ScriptValue)var70_63, (ScriptValue)var71_64, (ScriptValue)var72_65, (ScriptContext)var1_1);
                    }
                    var76_69 = v8;
                    var0.val("target", (ScriptValue)var76_69);
                    if ((var36_31 != false || ScriptFormula.valuesEqual((ScriptValue)var76_69, (ScriptValue)var1_1.getClassOrVar("null")) != false) != false || (var76_69 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var76_69, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
                    var77_70 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var77_70.val("id", (ScriptValue)(var76_69 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var76_69, (ScriptContext)var1_1) : ScriptValue.NULL));
                    if (!(TreeUtils._isChoppable((ScriptContext.Builder)var77_70).asBool() ^ true)) {
                        v9 = false;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v9 = true;
                    }
                    if (v9) {
                        if (var23_19 != ScriptValue.NULL) {
                            var78_71 = var20_16;
                            if (var23_19 instanceof ScriptValue.Obj && (var80_73 = (var79_72 = (ScriptValue.Obj)var23_19).instance()) != null && !(var80_73 instanceof PolyClass) && var79_72.typeName().equals("Contraption")) {
                                var81_74 = new PolyClassContraption(var80_73);
                                v10 /* !! */  = ScriptValue.of((boolean)var81_74.tm$2_release(var78_71.asStr()));
                            } else {
                                v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var23_19, (ScriptValue)var78_71, (ScriptContext)var1_1);
                            }
                        } else {
                            v10 /* !! */  = ScriptValue.NULL;
                        }
                    } else {
                        if (var23_19 != ScriptValue.NULL) {
                            var82_75 = var20_16;
                            if (var23_19 instanceof ScriptValue.Obj && (var84_77 = (var83_76 = (ScriptValue.Obj)var23_19).instance()) != null && !(var84_77 instanceof PolyClass) && var83_76.typeName().equals("Contraption")) {
                                var85_78 = new PolyClassContraption(var84_77);
                                v11 /* !! */  = ScriptValue.of((boolean)var85_78.tm$12_hold(var82_75.asStr()));
                            } else {
                                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var23_19, (ScriptValue)var82_75, (ScriptContext)var1_1);
                            }
                        } else {
                            v11 /* !! */  = ScriptValue.NULL;
                        }
                        var86_79 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                        var86_79.val("id", (ScriptValue)(var76_69 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var76_69, (ScriptContext)var1_1) : ScriptValue.NULL));
                        if (TreeUtils._isLog((ScriptContext.Builder)var86_79).asBool()) {
                            var87_80 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                            var87_80.val("contraption", var23_19);
                            var87_80.val("base_x", var57_50);
                            var87_80.val("base_y", var62_55);
                            var87_80.val("base_z", var67_60);
                            var87_80.val("speed", var52_45);
                            TreeUtils._fellTree((ScriptContext.Builder)var87_80);
                        } else {
                            var88_81 = var1_1.getClassOrVar("Machine");
                            if (var88_81 != ScriptValue.NULL) {
                                var89_82 = var76_69;
                                var90_83 = var52_45;
                                if (var88_81 instanceof ScriptValue.Obj && (var92_85 = (var91_84 = (ScriptValue.Obj)var88_81).instance()) != null && !(var92_85 instanceof PolyClass) && var91_84.typeName().equals("Machine")) {
                                    var93_86 = new PolyClassMachine(var92_85);
                                    v12 /* !! */  = var93_86.tm$2_tick_break((ScriptValue)var89_82, var90_83.asNum());
                                } else {
                                    v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var88_81, (ScriptValue)var89_82, (ScriptValue)var90_83, (ScriptContext)var1_1);
                                }
                            } else {
                                v12 /* !! */  = ScriptValue.NULL;
                            }
                            var94_87 = v12 /* !! */ ;
                            var0.val("result", var94_87);
                            if (ScriptFormula.valuesEqual((ScriptValue)var94_87, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var95_88 = ScriptProgram.elementsOf((ScriptValue)var94_87)) != null) {
                                for (ScriptValue var97_90 : var95_88) {
                                    var0.val("item", var97_90);
                                    var98_91 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                                    var98_91.val("item", var97_90);
                                    Utils.1._deposit((ScriptContext.Builder)var98_91);
                                }
                            }
                        }
                        var99_92 = var1_1.getClassOrVar("Machine");
                        if (var99_92 != ScriptValue.NULL) {
                            if (var39_34) {
                                var101_93 = ScriptContext.builder().copyFrom(var1_1);
                                var101_93.val("current_rpm", (ScriptValue)(var23_19 != ScriptValue.NULL ? ((var102_94 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var102_94.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL));
                                v13 = Saw._sawSuCost(var101_93);
                            } else {
                                v13 = var100_95 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 6.0)), (ScriptValue)var52_45);
                            }
                            if (var99_92 instanceof ScriptValue.Obj && (var104_97 = (var103_96 = (ScriptValue.Obj)var99_92).instance()) != null && !(var104_97 instanceof PolyClass) && var103_96.typeName().equals("Machine")) {
                                var105_98 = new PolyClassMachine(var104_97);
                                v14 /* !! */  = ScriptValue.of((boolean)var105_98.tm$56_report_su(var100_95.asNum()));
                            } else {
                                v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var99_92, (ScriptValue)var100_95, (ScriptContext)var1_1);
                            }
                        } else {
                            v14 /* !! */  = ScriptValue.NULL;
                        }
                    }
                    break block199;
                }
                if (var23_19 != ScriptValue.NULL) {
                    var106_99 = var20_16;
                    if (var23_19 instanceof ScriptValue.Obj && (var108_101 = (var107_100 = (ScriptValue.Obj)var23_19).instance()) != null && !(var108_101 instanceof PolyClass) && var107_100.typeName().equals("Contraption")) {
                        var109_102 = new PolyClassContraption(var108_101);
                        v15 /* !! */  = ScriptValue.of((boolean)var109_102.tm$2_release(var106_99.asStr()));
                    } else {
                        v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var23_19, (ScriptValue)var106_99, (ScriptContext)var1_1);
                    }
                } else {
                    v15 /* !! */  = ScriptValue.NULL;
                }
                break block199;
            }
            if (!(ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true)) break block199;
            var110_103 = 10.0;
            var112_104 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(var1_1.getNum("rpm")) / var110_103);
            var114_105 = ScriptValue.of((double)var112_104);
            var0.val("speed", var114_105);
            var115_106 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
            var117_108 = var115_106 != null ? var115_106.pg$137_facing_block() : ((var116_107 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var116_107, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("fb", var117_108);
            if (!(var36_31 ^ true)) ** GOTO lbl-1000
            v16 = var117_108 != ScriptValue.NULL ? ((var118_109 = PolyClassBlock_v4.ofGuarded((ScriptValue)var117_108)) != null ? var118_109.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var117_108, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
            if (v16 ^ true) {
                v17 = true;
            } else lbl-1000:
            // 2 sources

            {
                v17 = false;
            }
            if (!v17) ** GOTO lbl-1000
            var119_110 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
            var119_110.val("id", (ScriptValue)(var117_108 != ScriptValue.NULL ? ((var120_111 = PolyClassBlock_v4.ofGuarded((ScriptValue)var117_108)) != null ? var120_111.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var117_108, (ScriptContext)var1_1)) : ScriptValue.NULL));
            if (TreeUtils._isChoppable((ScriptContext.Builder)var119_110).asBool()) {
                v18 = true;
            } else lbl-1000:
            // 2 sources

            {
                v18 = false;
            }
            if (v18) {
                var121_112 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                var121_112.val("id", (ScriptValue)(var117_108 != ScriptValue.NULL ? ((var122_113 = PolyClassBlock_v4.ofGuarded((ScriptValue)var117_108)) != null ? var122_113.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var117_108, (ScriptContext)var1_1)) : ScriptValue.NULL));
                if (TreeUtils._isLog((ScriptContext.Builder)var121_112).asBool()) {
                    var123_114 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var123_114.val("contraption", var1_1.getClassOrVar("null"));
                    var124_115 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var126_117 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var123_114.val("base_x", ScriptFormula.addPolymorphic((ScriptValue)(var124_115 != null ? var124_115.pg$200_x() : ((var125_116 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var125_116, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var126_117 != null ? var126_117.pg$133_facing_dx() : ((var127_118 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var127_118, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var128_119 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var130_121 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var123_114.val("base_y", ScriptFormula.addPolymorphic((ScriptValue)(var128_119 != null ? var128_119.pg$202_y() : ((var129_120 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var129_120, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var130_121 != null ? var130_121.pg$129_facing_dy() : ((var131_122 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var131_122, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var132_123 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var134_125 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var123_114.val("base_z", ScriptFormula.addPolymorphic((ScriptValue)(var132_123 != null ? var132_123.pg$206_z() : ((var133_124 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var133_124, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var134_125 != null ? var134_125.pg$131_facing_dz() : ((var135_126 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var135_126, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var123_114.val("speed", ScriptValue.of((double)var112_104));
                    TreeUtils._fellTree((ScriptContext.Builder)var123_114);
                } else {
                    var136_127 = var1_1.getClassOrVar("Machine");
                    if (var136_127 != ScriptValue.NULL) {
                        var137_128 = var117_108;
                        var138_129 = var112_104;
                        if (var136_127 instanceof ScriptValue.Obj && (var141_131 = (var140_130 = (ScriptValue.Obj)var136_127).instance()) != null && !(var141_131 instanceof PolyClass) && var140_130.typeName().equals("Machine")) {
                            var142_132 = new PolyClassMachine(var141_131);
                            v19 /* !! */  = var142_132.tm$2_tick_break(var137_128, var138_129);
                        } else {
                            v19 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var136_127, (ScriptValue)var137_128, (ScriptValue)ScriptValue.of((double)var138_129), (ScriptContext)var1_1);
                        }
                    } else {
                        v19 /* !! */  = ScriptValue.NULL;
                    }
                    var143_133 = v19 /* !! */ ;
                    var0.val("result", var143_133);
                    if (ScriptFormula.valuesEqual((ScriptValue)var143_133, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var144_134 = ScriptProgram.elementsOf((ScriptValue)var143_133)) != null) {
                        for (ScriptValue var146_136 : var144_134) {
                            var0.val("item", var146_136);
                            var147_137 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var147_137.val("item", var146_136);
                            Utils.1._deposit((ScriptContext.Builder)var147_137);
                        }
                    }
                }
                var148_138 = var1_1.getClassOrVar("Machine");
                if (var148_138 != ScriptValue.NULL) {
                    var150_139 = ScriptContext.builder().copyFrom(var1_1);
                    var150_139.val("current_rpm", var1_1.getClassOrVar("rpm"));
                    var149_140 = Saw._sawSuCost(var150_139);
                    if (var148_138 instanceof ScriptValue.Obj && (var152_142 = (var151_141 = (ScriptValue.Obj)var148_138).instance()) != null && !(var152_142 instanceof PolyClass) && var151_141.typeName().equals("Machine")) {
                        var153_143 = new PolyClassMachine(var152_142);
                        v20 /* !! */  = ScriptValue.of((boolean)var153_143.tm$56_report_su(var149_140.asNum()));
                    } else {
                        v20 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var148_138, (ScriptValue)var149_140, (ScriptContext)var1_1);
                    }
                } else {
                    v20 /* !! */  = ScriptValue.NULL;
                }
            }
            var154_144 = 1.0;
            var156_145 = ScriptValue.of((double)1.0);
            var0.val("is_now", var156_145);
        }
        if (var1_1.getNum("is_now") > 0.0 != false && var36_31 != false) {
            var157_146 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
            var159_148 = var157_146 != null ? var157_146.pg$158_belt() : ((var158_147 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "belt", (ScriptValue)var158_147, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("belt", var159_148);
            var161_150 = ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var23_19 != ScriptValue.NULL ? ((var160_149 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var160_149.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm");
            var0.val("belt_rpm", var161_150);
            var162_151 = var1_1.getClassOrVar("Machine");
            if (var162_151 != ScriptValue.NULL) {
                var163_152 = "_saw_item_id";
                var164_153 = "str";
                if (var162_151 instanceof ScriptValue.Obj && (var166_155 = (var165_154 = (ScriptValue.Obj)var162_151).instance()) != null && !(var166_155 instanceof PolyClass) && var165_154.typeName().equals("Machine")) {
                    var167_156 = new PolyClassMachine(var166_155);
                    v21 /* !! */  = var167_156.tm$34_get_typed(var163_152, var164_153);
                } else {
                    v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var162_151, (ScriptValue)ScriptValue.of((String)var163_152), (ScriptValue)ScriptValue.of((String)var164_153), (ScriptContext)var1_1);
                }
            } else {
                v21 /* !! */  = ScriptValue.NULL;
            }
            var168_157 = v21 /* !! */ ;
            var0.val("active_id", var168_157);
            if (ScriptFormula.valuesEqual((ScriptValue)var168_157, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var169_158 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                var0.val("active_id", var169_158);
            }
            if (var1_1.getStr("active_id").equals("")) {
                v22 = var159_148 != ScriptValue.NULL ? ((var170_159 = PolyClassBelt.ofGuarded((ScriptValue)var159_148)) != null ? var170_159.tg$26_has_item() : PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var159_148, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
                if (v22) {
                    if (var159_148 != ScriptValue.NULL) {
                        if (var159_148 instanceof ScriptValue.Obj && (var172_161 = (var171_160 = (ScriptValue.Obj)var159_148).instance()) != null && !(var172_161 instanceof PolyClass) && var171_160.typeName().equals("Belt")) {
                            var173_162 = new PolyClassBelt(var172_161);
                            v23 /* !! */  = var173_162.tm$12_peek();
                        } else {
                            v23 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var159_148, (ScriptContext)var1_1);
                        }
                    } else {
                        v23 /* !! */  = ScriptValue.NULL;
                    }
                    var174_163 = v23 /* !! */ ;
                    var0.val("carried", var174_163);
                    var175_164 = ScriptContext.builder().copyFrom(var1_1);
                    var175_164.val("item_id", (ScriptValue)(var174_163 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var174_163, (ScriptContext)var1_1) : ScriptValue.NULL));
                    var176_165 = Saw._sawOutputFor(var175_164);
                    var0.val("out_id", var176_165);
                    ScriptFormula.callBuiltin1((String)"print", (ScriptValue)ScriptValue.of((String)("[saw-debug] own-belt has_item id=" + (var174_163 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var174_163, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " out_id=" + (ScriptFormula.valuesEqual((ScriptValue)var176_165, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "null")) : var176_165).asStr())), (ScriptContext)var1_1);
                    if (ScriptFormula.valuesEqual((ScriptValue)var176_165, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        if (var159_148 != ScriptValue.NULL) {
                            if (var159_148 instanceof ScriptValue.Obj && (var178_167 = (var177_166 = (ScriptValue.Obj)var159_148).instance()) != null && !(var178_167 instanceof PolyClass) && var177_166.typeName().equals("Belt")) {
                                var179_168 = new PolyClassBelt(var178_167);
                                v24 /* !! */  = var179_168.tm$0_take();
                            } else {
                                v24 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var159_148, (ScriptContext)var1_1);
                            }
                        } else {
                            v24 /* !! */  = ScriptValue.NULL;
                        }
                        var180_169 = v24 /* !! */ ;
                        var0.val("taken", var180_169);
                        var181_170 = var1_1.getClassOrVar("Machine");
                        if (var181_170 != ScriptValue.NULL) {
                            var182_171 = "_saw_item_id";
                            var183_172 = "str";
                            v25 /* !! */  = var184_173 = var180_169 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var180_169, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var181_170 instanceof ScriptValue.Obj && (var186_175 = (var185_174 = (ScriptValue.Obj)var181_170).instance()) != null && !(var186_175 instanceof PolyClass) && var185_174.typeName().equals("Machine")) {
                                var187_176 = new PolyClassMachine(var186_175);
                                v26 /* !! */  = ScriptValue.of((boolean)var187_176.tm$82_set_typed(var182_171, var183_172, var184_173));
                            } else {
                                v26 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var181_170, (ScriptValue)ScriptValue.of((String)var182_171), (ScriptValue)ScriptValue.of((String)var183_172), (ScriptValue)var184_173, (ScriptContext)var1_1);
                            }
                        } else {
                            v26 /* !! */  = ScriptValue.NULL;
                        }
                        var188_177 = var1_1.getClassOrVar("Machine");
                        if (var188_177 != ScriptValue.NULL) {
                            var189_178 = "_saw_out_id";
                            var190_179 = "str";
                            var191_180 = var176_165;
                            if (var188_177 instanceof ScriptValue.Obj && (var193_182 = (var192_181 = (ScriptValue.Obj)var188_177).instance()) != null && !(var193_182 instanceof PolyClass) && var192_181.typeName().equals("Machine")) {
                                var194_183 = new PolyClassMachine(var193_182);
                                v27 /* !! */  = ScriptValue.of((boolean)var194_183.tm$82_set_typed(var189_178, var190_179, var191_180));
                            } else {
                                v27 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var188_177, (ScriptValue)ScriptValue.of((String)var189_178), (ScriptValue)ScriptValue.of((String)var190_179), (ScriptValue)var191_180, (ScriptContext)var1_1);
                            }
                        } else {
                            v27 /* !! */  = ScriptValue.NULL;
                        }
                        var195_184 = var1_1.getClassOrVar("Machine");
                        if (var195_184 != ScriptValue.NULL) {
                            var196_185 = "_saw_count";
                            var197_186 = "int";
                            v28 /* !! */  = var198_187 = var180_169 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var180_169, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var195_184 instanceof ScriptValue.Obj && (var200_189 = (var199_188 = (ScriptValue.Obj)var195_184).instance()) != null && !(var200_189 instanceof PolyClass) && var199_188.typeName().equals("Machine")) {
                                var201_190 = new PolyClassMachine(var200_189);
                                v29 /* !! */  = ScriptValue.of((boolean)var201_190.tm$82_set_typed(var196_185, var197_186, var198_187));
                            } else {
                                v29 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var195_184, (ScriptValue)ScriptValue.of((String)var196_185), (ScriptValue)ScriptValue.of((String)var197_186), (ScriptValue)var198_187, (ScriptContext)var1_1);
                            }
                        } else {
                            v29 /* !! */  = ScriptValue.NULL;
                        }
                        var202_191 = var1_1.getClassOrVar("Machine");
                        if (var202_191 != ScriptValue.NULL) {
                            var203_192 = "_saw_progress";
                            var204_193 = "int";
                            var205_194 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            if (var202_191 instanceof ScriptValue.Obj && (var207_196 = (var206_195 = (ScriptValue.Obj)var202_191).instance()) != null && !(var207_196 instanceof PolyClass) && var206_195.typeName().equals("Machine")) {
                                var208_197 = new PolyClassMachine(var207_196);
                                v30 /* !! */  = ScriptValue.of((boolean)var208_197.tm$82_set_typed(var203_192, var204_193, var205_194));
                            } else {
                                v30 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var202_191, (ScriptValue)ScriptValue.of((String)var203_192), (ScriptValue)ScriptValue.of((String)var204_193), (ScriptValue)var205_194, (ScriptContext)var1_1);
                            }
                        } else {
                            v30 /* !! */  = ScriptValue.NULL;
                        }
                        var209_198 = var1_1.getClassOrVar("Machine");
                        if (var209_198 != ScriptValue.NULL) {
                            var210_199 = "_saw_from_ground";
                            var211_200 = "int";
                            var212_201 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            if (var209_198 instanceof ScriptValue.Obj && (var214_203 = (var213_202 = (ScriptValue.Obj)var209_198).instance()) != null && !(var214_203 instanceof PolyClass) && var213_202.typeName().equals("Machine")) {
                                var215_204 = new PolyClassMachine(var214_203);
                                v31 /* !! */  = ScriptValue.of((boolean)var215_204.tm$82_set_typed(var210_199, var211_200, var212_201));
                            } else {
                                v31 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var209_198, (ScriptValue)ScriptValue.of((String)var210_199), (ScriptValue)ScriptValue.of((String)var211_200), (ScriptValue)var212_201, (ScriptContext)var1_1);
                            }
                        } else {
                            v31 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var216_205 = ScriptContext.builder().copyFrom(var1_1);
                    var217_206 = Saw._sawInputFace(var216_205);
                    var0.val("in_face", var217_206);
                    var218_207 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                    var218_207.val("face", var217_206);
                    var218_207.val("validator", var1_1.getClassOrVar("null"));
                    var218_207.val("amount", var1_1.getClassOrVar("null"));
                    var219_208 = BeltUtils.beltTake((ScriptContext.Builder)var218_207);
                    var0.val("taken", var219_208);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var219_208, (ScriptContext)var1_1).asBool() ^ true) {
                        var220_209 = ScriptContext.builder().copyFrom(var1_1);
                        var220_209.val("item_id", (ScriptValue)(var219_208 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var219_208, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var221_210 = Saw._sawOutputFor(var220_209);
                        var0.val("out_id", var221_210);
                        if (ScriptFormula.valuesEqual((ScriptValue)var221_210, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var222_211 = var1_1.getClassOrVar("Machine");
                            if (var222_211 != ScriptValue.NULL) {
                                var223_212 = "_saw_item_id";
                                var224_213 = "str";
                                v32 /* !! */  = var225_214 = var219_208 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var219_208, (ScriptContext)var1_1) : ScriptValue.NULL;
                                if (var222_211 instanceof ScriptValue.Obj && (var227_216 = (var226_215 = (ScriptValue.Obj)var222_211).instance()) != null && !(var227_216 instanceof PolyClass) && var226_215.typeName().equals("Machine")) {
                                    var228_217 = new PolyClassMachine(var227_216);
                                    v33 /* !! */  = ScriptValue.of((boolean)var228_217.tm$82_set_typed(var223_212, var224_213, var225_214));
                                } else {
                                    v33 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var222_211, (ScriptValue)ScriptValue.of((String)var223_212), (ScriptValue)ScriptValue.of((String)var224_213), (ScriptValue)var225_214, (ScriptContext)var1_1);
                                }
                            } else {
                                v33 /* !! */  = ScriptValue.NULL;
                            }
                            var229_218 = var1_1.getClassOrVar("Machine");
                            if (var229_218 != ScriptValue.NULL) {
                                var230_219 = "_saw_out_id";
                                var231_220 = "str";
                                var232_221 = var221_210;
                                if (var229_218 instanceof ScriptValue.Obj && (var234_223 = (var233_222 = (ScriptValue.Obj)var229_218).instance()) != null && !(var234_223 instanceof PolyClass) && var233_222.typeName().equals("Machine")) {
                                    var235_224 = new PolyClassMachine(var234_223);
                                    v34 /* !! */  = ScriptValue.of((boolean)var235_224.tm$82_set_typed(var230_219, var231_220, var232_221));
                                } else {
                                    v34 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var229_218, (ScriptValue)ScriptValue.of((String)var230_219), (ScriptValue)ScriptValue.of((String)var231_220), (ScriptValue)var232_221, (ScriptContext)var1_1);
                                }
                            } else {
                                v34 /* !! */  = ScriptValue.NULL;
                            }
                            var236_225 = var1_1.getClassOrVar("Machine");
                            if (var236_225 != ScriptValue.NULL) {
                                var237_226 = "_saw_count";
                                var238_227 = "int";
                                var239_228 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var219_208, (ScriptContext)var1_1);
                                if (var236_225 instanceof ScriptValue.Obj && (var241_230 = (var240_229 = (ScriptValue.Obj)var236_225).instance()) != null && !(var241_230 instanceof PolyClass) && var240_229.typeName().equals("Machine")) {
                                    var242_231 = new PolyClassMachine(var241_230);
                                    v35 /* !! */  = ScriptValue.of((boolean)var242_231.tm$82_set_typed(var237_226, var238_227, var239_228));
                                } else {
                                    v35 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var236_225, (ScriptValue)ScriptValue.of((String)var237_226), (ScriptValue)ScriptValue.of((String)var238_227), (ScriptValue)var239_228, (ScriptContext)var1_1);
                                }
                            } else {
                                v35 /* !! */  = ScriptValue.NULL;
                            }
                            var243_232 = var1_1.getClassOrVar("Machine");
                            if (var243_232 != ScriptValue.NULL) {
                                var244_233 = "_saw_progress";
                                var245_234 = "int";
                                var246_235 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                if (var243_232 instanceof ScriptValue.Obj && (var248_237 = (var247_236 = (ScriptValue.Obj)var243_232).instance()) != null && !(var248_237 instanceof PolyClass) && var247_236.typeName().equals("Machine")) {
                                    var249_238 = new PolyClassMachine(var248_237);
                                    v36 /* !! */  = ScriptValue.of((boolean)var249_238.tm$82_set_typed(var244_233, var245_234, var246_235));
                                } else {
                                    v36 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var243_232, (ScriptValue)ScriptValue.of((String)var244_233), (ScriptValue)ScriptValue.of((String)var245_234), (ScriptValue)var246_235, (ScriptContext)var1_1);
                                }
                            } else {
                                v36 /* !! */  = ScriptValue.NULL;
                            }
                            var250_239 = var1_1.getClassOrVar("Machine");
                            if (var250_239 != ScriptValue.NULL) {
                                var251_240 = "_saw_from_ground";
                                var252_241 = "int";
                                var253_242 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0);
                                if (var250_239 instanceof ScriptValue.Obj && (var255_244 = (var254_243 = (ScriptValue.Obj)var250_239).instance()) != null && !(var255_244 instanceof PolyClass) && var254_243.typeName().equals("Machine")) {
                                    var256_245 = new PolyClassMachine(var255_244);
                                    v37 /* !! */  = ScriptValue.of((boolean)var256_245.tm$82_set_typed(var251_240, var252_241, var253_242));
                                } else {
                                    v37 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var250_239, (ScriptValue)ScriptValue.of((String)var251_240), (ScriptValue)ScriptValue.of((String)var252_241), (ScriptValue)var253_242, (ScriptContext)var1_1);
                                }
                            } else {
                                v37 /* !! */  = ScriptValue.NULL;
                            }
                        } else {
                            var257_246 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                            var257_246.val("face", var217_206);
                            var257_246.val("item", var219_208);
                            BeltUtils.beltGive((ScriptContext.Builder)var257_246);
                        }
                    } else {
                        var261_247 = var1_1.getClassOrVar("Machine");
                        if (var261_247 != ScriptValue.NULL) {
                            var262_248 = 0.7;
                            if (var261_247 instanceof ScriptValue.Obj && (var265_250 = (var264_249 = (ScriptValue.Obj)var261_247).instance()) != null && !(var265_250 instanceof PolyClass) && var264_249.typeName().equals("Machine")) {
                                var266_251 = new PolyClassMachine(var265_250);
                                v38 /* !! */  = var266_251.tm$94_nearby_entities(var262_248);
                            } else {
                                v38 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var261_247, (ScriptValue)ScriptValue.of((double)var262_248), (ScriptContext)var1_1);
                            }
                        } else {
                            v38 /* !! */  = ScriptValue.NULL;
                        }
                        var258_252 = ScriptProgram.elementsOf((ScriptValue)v38 /* !! */ );
                        var267_253 = var1_1.getClassOrVar("out_id");
                        if (var258_252 != null) {
                            for (ScriptValue var260_255 : var258_252) {
                                var0.val("entity", var260_255);
                                var268_256 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                                var268_256.val("entity", var260_255);
                                if (!Utils.isRestingItem(var268_256).asBool()) continue;
                                var269_257 = ScriptContext.builder().copyFrom(var1_1);
                                var269_257.val("item_id", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var260_255 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var260_255, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                                var270_258 = Saw._sawOutputFor(var269_257);
                                var0.val("out_id", var270_258);
                                var267_253 = var270_258;
                                if (!(ScriptFormula.valuesEqual((ScriptValue)var267_253, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                                var271_259 = var1_1.getClassOrVar("Machine");
                                if (var271_259 != ScriptValue.NULL) {
                                    var272_260 = "_saw_item_id";
                                    var273_261 = "str";
                                    var274_262 = PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var260_255 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var260_255, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    if (var271_259 instanceof ScriptValue.Obj && (var276_264 = (var275_263 = (ScriptValue.Obj)var271_259).instance()) != null && !(var276_264 instanceof PolyClass) && var275_263.typeName().equals("Machine")) {
                                        var277_265 = new PolyClassMachine(var276_264);
                                        v39 /* !! */  = ScriptValue.of((boolean)var277_265.tm$82_set_typed(var272_260, var273_261, (ScriptValue)var274_262));
                                    } else {
                                        v39 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var271_259, (ScriptValue)ScriptValue.of((String)var272_260), (ScriptValue)ScriptValue.of((String)var273_261), (ScriptValue)var274_262, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v39 /* !! */  = ScriptValue.NULL;
                                }
                                var278_266 = var1_1.getClassOrVar("Machine");
                                if (var278_266 != ScriptValue.NULL) {
                                    var279_267 = "_saw_out_id";
                                    var280_268 = "str";
                                    var281_269 = var267_253;
                                    if (var278_266 instanceof ScriptValue.Obj && (var283_271 = (var282_270 = (ScriptValue.Obj)var278_266).instance()) != null && !(var283_271 instanceof PolyClass) && var282_270.typeName().equals("Machine")) {
                                        var284_272 = new PolyClassMachine(var283_271);
                                        v40 /* !! */  = ScriptValue.of((boolean)var284_272.tm$82_set_typed(var279_267, var280_268, var281_269));
                                    } else {
                                        v40 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var278_266, (ScriptValue)ScriptValue.of((String)var279_267), (ScriptValue)ScriptValue.of((String)var280_268), (ScriptValue)var281_269, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v40 /* !! */  = ScriptValue.NULL;
                                }
                                var285_273 = var1_1.getClassOrVar("Machine");
                                if (var285_273 != ScriptValue.NULL) {
                                    var286_274 = "_saw_count";
                                    var287_275 = "int";
                                    var288_276 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)(var260_255 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var260_255, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    if (var285_273 instanceof ScriptValue.Obj && (var290_278 = (var289_277 = (ScriptValue.Obj)var285_273).instance()) != null && !(var290_278 instanceof PolyClass) && var289_277.typeName().equals("Machine")) {
                                        var291_279 = new PolyClassMachine(var290_278);
                                        v41 /* !! */  = ScriptValue.of((boolean)var291_279.tm$82_set_typed(var286_274, var287_275, var288_276));
                                    } else {
                                        v41 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var285_273, (ScriptValue)ScriptValue.of((String)var286_274), (ScriptValue)ScriptValue.of((String)var287_275), (ScriptValue)var288_276, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v41 /* !! */  = ScriptValue.NULL;
                                }
                                var292_280 = var1_1.getClassOrVar("Machine");
                                if (var292_280 != ScriptValue.NULL) {
                                    var293_281 = "_saw_progress";
                                    var294_282 = "int";
                                    var295_283 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                    if (var292_280 instanceof ScriptValue.Obj && (var297_285 = (var296_284 = (ScriptValue.Obj)var292_280).instance()) != null && !(var297_285 instanceof PolyClass) && var296_284.typeName().equals("Machine")) {
                                        var298_286 = new PolyClassMachine(var297_285);
                                        v42 /* !! */  = ScriptValue.of((boolean)var298_286.tm$82_set_typed(var293_281, var294_282, var295_283));
                                    } else {
                                        v42 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var292_280, (ScriptValue)ScriptValue.of((String)var293_281), (ScriptValue)ScriptValue.of((String)var294_282), (ScriptValue)var295_283, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v42 /* !! */  = ScriptValue.NULL;
                                }
                                var299_287 = var1_1.getClassOrVar("Machine");
                                if (var299_287 != ScriptValue.NULL) {
                                    var300_288 = "_saw_from_ground";
                                    var301_289 = "int";
                                    var302_290 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0);
                                    if (var299_287 instanceof ScriptValue.Obj && (var304_292 = (var303_291 = (ScriptValue.Obj)var299_287).instance()) != null && !(var304_292 instanceof PolyClass) && var303_291.typeName().equals("Machine")) {
                                        var305_293 = new PolyClassMachine(var304_292);
                                        v43 /* !! */  = ScriptValue.of((boolean)var305_293.tm$82_set_typed(var300_288, var301_289, var302_290));
                                    } else {
                                        v43 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var299_287, (ScriptValue)ScriptValue.of((String)var300_288), (ScriptValue)ScriptValue.of((String)var301_289), (ScriptValue)var302_290, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v43 /* !! */  = ScriptValue.NULL;
                                }
                                v44 /* !! */  = var260_255 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var260_255, (ScriptContext)var1_1) : ScriptValue.NULL;
                                break;
                            }
                        }
                    }
                }
            } else {
                var306_294 = var1_1.getClassOrVar("Machine");
                if (var306_294 != ScriptValue.NULL) {
                    var307_295 = "_saw_progress";
                    var308_296 = "int";
                    if (var306_294 instanceof ScriptValue.Obj && (var310_298 = (var309_297 = (ScriptValue.Obj)var306_294).instance()) != null && !(var310_298 instanceof PolyClass) && var309_297.typeName().equals("Machine")) {
                        var311_299 = new PolyClassMachine(var310_298);
                        v45 /* !! */  = var311_299.tm$34_get_typed(var307_295, var308_296);
                    } else {
                        v45 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var306_294, (ScriptValue)ScriptValue.of((String)var307_295), (ScriptValue)ScriptValue.of((String)var308_296), (ScriptContext)var1_1);
                    }
                } else {
                    v45 /* !! */  = ScriptValue.NULL;
                }
                var312_300 = v45 /* !! */ ;
                var0.val("progress", var312_300);
                if (ScriptFormula.valuesEqual((ScriptValue)var312_300, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var313_301 = 0.0;
                    var315_302 = ScriptValue.of((double)0.0);
                    var0.val("progress", var315_302);
                }
                var316_303 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)Math.abs(var161_150.asNum())));
                var0.val("progress", var316_303);
                var317_304 = var1_1.getClassOrVar("Machine");
                if (var317_304 != ScriptValue.NULL) {
                    var319_305 = ScriptContext.builder().copyFrom(var1_1);
                    var319_305.val("current_rpm", var161_150);
                    var318_306 = Saw._sawSuCost(var319_305);
                    if (var317_304 instanceof ScriptValue.Obj && (var321_308 = (var320_307 = (ScriptValue.Obj)var317_304).instance()) != null && !(var321_308 instanceof PolyClass) && var320_307.typeName().equals("Machine")) {
                        var322_309 = new PolyClassMachine(var321_308);
                        v46 /* !! */  = ScriptValue.of((boolean)var322_309.tm$56_report_su(var318_306.asNum()));
                    } else {
                        v46 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var317_304, (ScriptValue)var318_306, (ScriptContext)var1_1);
                    }
                } else {
                    v46 /* !! */  = ScriptValue.NULL;
                }
                if (var316_303.asNum() >= var11_8) {
                    var323_310 = var1_1.getClassOrVar("Machine");
                    if (var323_310 != ScriptValue.NULL) {
                        var324_311 = "_saw_out_id";
                        var325_312 = "str";
                        if (var323_310 instanceof ScriptValue.Obj && (var327_314 = (var326_313 = (ScriptValue.Obj)var323_310).instance()) != null && !(var327_314 instanceof PolyClass) && var326_313.typeName().equals("Machine")) {
                            var328_315 = new PolyClassMachine(var327_314);
                            v47 /* !! */  = var328_315.tm$34_get_typed(var324_311, var325_312);
                        } else {
                            v47 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var323_310, (ScriptValue)ScriptValue.of((String)var324_311), (ScriptValue)ScriptValue.of((String)var325_312), (ScriptContext)var1_1);
                        }
                    } else {
                        v47 /* !! */  = ScriptValue.NULL;
                    }
                    var329_316 = v47 /* !! */ ;
                    var0.val("out_id", var329_316);
                    var330_317 = var1_1.getClassOrVar("Machine");
                    if (var330_317 != ScriptValue.NULL) {
                        var331_318 = "_saw_count";
                        var332_319 = "int";
                        if (var330_317 instanceof ScriptValue.Obj && (var334_321 = (var333_320 = (ScriptValue.Obj)var330_317).instance()) != null && !(var334_321 instanceof PolyClass) && var333_320.typeName().equals("Machine")) {
                            var335_322 = new PolyClassMachine(var334_321);
                            v48 /* !! */  = var335_322.tm$34_get_typed(var331_318, var332_319);
                        } else {
                            v48 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var330_317, (ScriptValue)ScriptValue.of((String)var331_318), (ScriptValue)ScriptValue.of((String)var332_319), (ScriptContext)var1_1);
                        }
                    } else {
                        v48 /* !! */  = ScriptValue.NULL;
                    }
                    var336_323 = v48 /* !! */ ;
                    var0.val("remaining", var336_323);
                    if (ScriptFormula.valuesEqual((ScriptValue)var336_323, (ScriptValue)var1_1.getClassOrVar("null")) != false || var336_323.asNum() <= 0.0 != false) {
                        var337_324 = 1.0;
                        var339_325 = ScriptValue.of((double)1.0);
                        var0.val("remaining", var339_325);
                    }
                    if ((var340_326 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        var341_327 = "_saw_from_ground";
                        var342_328 = "int";
                        if (var340_326 instanceof ScriptValue.Obj && (var344_330 = (var343_329 = (ScriptValue.Obj)var340_326).instance()) != null && !(var344_330 instanceof PolyClass) && var343_329.typeName().equals("Machine")) {
                            var345_331 = new PolyClassMachine(var344_330);
                            v49 /* !! */  = var345_331.tm$34_get_typed(var341_327, var342_328);
                        } else {
                            v49 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var340_326, (ScriptValue)ScriptValue.of((String)var341_327), (ScriptValue)ScriptValue.of((String)var342_328), (ScriptContext)var1_1);
                        }
                    } else {
                        v49 /* !! */  = ScriptValue.NULL;
                    }
                    var346_332 = v49 /* !! */ ;
                    var0.val("from_ground", var346_332);
                    var347_333 = ScriptContext.builder().copyFrom(var1_1);
                    var347_333.val("item_id", var1_1.getClassOrVar("active_id"));
                    var347_333.val("out_id", var329_316);
                    var348_334 = Saw._sawRecipeFor(var347_333);
                    var0.val("recipe", var348_334);
                    var349_335 = ScriptFormula.valuesEqual((ScriptValue)var348_334, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var348_334 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "outputs", (ScriptValue)var348_334, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("null");
                    var0.val("result", var349_335);
                    if (ScriptFormula.valuesEqual((ScriptValue)var349_335, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        v50 /* !! */  = var349_335 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "items", (ScriptValue)var349_335, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var350_336 = new ArrayList<ScriptValue>();
                        var350_336.add(ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)var329_316, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0)), (ScriptContext)var1_1));
                        v50 /* !! */  = new ScriptValue.Array(var350_336);
                    }
                    var351_337 /* !! */  = v50 /* !! */ ;
                    var0.val("items", var351_337 /* !! */ );
                    var352_338 = ScriptProgram.elementsOf((ScriptValue)var351_337 /* !! */ );
                    var355_339 = var1_1.getClassOrVar("leftover");
                    if (var352_338 != null) {
                        for (ScriptValue var354_341 : var352_338) {
                            var0.val("out_item", var354_341);
                            if (ScriptFormula.valuesEqual((ScriptValue)var346_332, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0))) != false || ScriptFormula.valuesEqual((ScriptValue)var346_332, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0))) != false) {
                                var356_342 = ScriptContext.builder().copyFrom(var1_1);
                                var356_342.val("item", var354_341);
                                Saw._sawDepositDirectional(var356_342);
                                continue;
                            }
                            if (var159_148 != ScriptValue.NULL) {
                                var357_343 = var354_341;
                                if (var159_148 instanceof ScriptValue.Obj && (var359_345 = (var358_344 = (ScriptValue.Obj)var159_148).instance()) != null && !(var359_345 instanceof PolyClass) && var358_344.typeName().equals("Belt")) {
                                    var360_346 = new PolyClassBelt(var359_345);
                                    v51 /* !! */  = var360_346.tm$10_put(var357_343);
                                } else {
                                    v51 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var159_148, (ScriptValue)var357_343, (ScriptContext)var1_1);
                                }
                            } else {
                                v51 /* !! */  = ScriptValue.NULL;
                            }
                            var361_347 = v51 /* !! */ ;
                            var0.val("leftover", var361_347);
                            var355_339 = var361_347;
                            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var355_339, (ScriptContext)var1_1).asBool() ^ true)) continue;
                            var362_348 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var362_348.val("item", var355_339);
                            Utils.1._deposit((ScriptContext.Builder)var362_348);
                        }
                    }
                    var363_349 = var1_1.getNum("remaining") - 1.0;
                    var365_350 = ScriptValue.of((double)var363_349);
                    var0.val("remaining", var365_350);
                    var366_351 = var1_1.getClassOrVar("Machine");
                    if (var366_351 != ScriptValue.NULL) {
                        var367_352 = "_saw_count";
                        var368_353 = "int";
                        var369_354 = ScriptValue.of((double)var363_349);
                        if (var366_351 instanceof ScriptValue.Obj && (var371_356 = (var370_355 = (ScriptValue.Obj)var366_351).instance()) != null && !(var371_356 instanceof PolyClass) && var370_355.typeName().equals("Machine")) {
                            var372_357 = new PolyClassMachine(var371_356);
                            v52 /* !! */  = ScriptValue.of((boolean)var372_357.tm$82_set_typed(var367_352, var368_353, var369_354));
                        } else {
                            v52 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var366_351, (ScriptValue)ScriptValue.of((String)var367_352), (ScriptValue)ScriptValue.of((String)var368_353), (ScriptValue)var369_354, (ScriptContext)var1_1);
                        }
                    } else {
                        v52 /* !! */  = ScriptValue.NULL;
                    }
                    var373_358 = var1_1.getClassOrVar("Machine");
                    if (var373_358 != ScriptValue.NULL) {
                        var374_359 = "_saw_progress";
                        var375_360 = "int";
                        var376_361 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                        if (var373_358 instanceof ScriptValue.Obj && (var378_363 = (var377_362 = (ScriptValue.Obj)var373_358).instance()) != null && !(var378_363 instanceof PolyClass) && var377_362.typeName().equals("Machine")) {
                            var379_364 = new PolyClassMachine(var378_363);
                            v53 /* !! */  = ScriptValue.of((boolean)var379_364.tm$82_set_typed(var374_359, var375_360, var376_361));
                        } else {
                            v53 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var373_358, (ScriptValue)ScriptValue.of((String)var374_359), (ScriptValue)ScriptValue.of((String)var375_360), (ScriptValue)var376_361, (ScriptContext)var1_1);
                        }
                    } else {
                        v53 /* !! */  = ScriptValue.NULL;
                    }
                    if (var363_349 <= 0.0) {
                        var380_365 = var1_1.getClassOrVar("Machine");
                        if (var380_365 != ScriptValue.NULL) {
                            var381_366 = "_saw_item_id";
                            var382_367 = "str";
                            var383_368 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                            if (var380_365 instanceof ScriptValue.Obj && (var385_370 = (var384_369 = (ScriptValue.Obj)var380_365).instance()) != null && !(var385_370 instanceof PolyClass) && var384_369.typeName().equals("Machine")) {
                                var386_371 = new PolyClassMachine(var385_370);
                                v54 /* !! */  = ScriptValue.of((boolean)var386_371.tm$82_set_typed(var381_366, var382_367, var383_368));
                            } else {
                                v54 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var380_365, (ScriptValue)ScriptValue.of((String)var381_366), (ScriptValue)ScriptValue.of((String)var382_367), (ScriptValue)var383_368, (ScriptContext)var1_1);
                            }
                        } else {
                            v54 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var387_372 = var1_1.getClassOrVar("Machine");
                    if (var387_372 != ScriptValue.NULL) {
                        var388_373 = "_saw_progress";
                        var389_374 = "int";
                        var390_375 = var316_303;
                        if (var387_372 instanceof ScriptValue.Obj && (var392_377 = (var391_376 = (ScriptValue.Obj)var387_372).instance()) != null && !(var392_377 instanceof PolyClass) && var391_376.typeName().equals("Machine")) {
                            var393_378 = new PolyClassMachine(var392_377);
                            v55 /* !! */  = ScriptValue.of((boolean)var393_378.tm$82_set_typed(var388_373, var389_374, var390_375));
                        } else {
                            v55 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var387_372, (ScriptValue)ScriptValue.of((String)var388_373), (ScriptValue)ScriptValue.of((String)var389_374), (ScriptValue)var390_375, (ScriptContext)var1_1);
                        }
                    } else {
                        v55 /* !! */  = ScriptValue.NULL;
                    }
                }
            }
        }
        var394_379 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var394_379.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "_saw_act"));
        var394_379.val("is_now", var1_1.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)var394_379);
        Saw.FILE_SCOPE = var0.build();
    }
}
