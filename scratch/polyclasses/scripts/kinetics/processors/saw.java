/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBelt
 *  dev.arubik.craftengine.script.PolyClassBlock_v2
 *  dev.arubik.craftengine.script.PolyClassContraption
 *  dev.arubik.craftengine.script.PolyClassContraptionWorld
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 *  dev.arubik.craftengine.script.gen.BeltUtils
 *  dev.arubik.craftengine.script.gen.TreeUtils
 *  dev.arubik.craftengine.script.gen.Utils$1
 */
package dev.arubik.craftengine.script.gen.kinetics.processors;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassBelt;
import dev.arubik.craftengine.script.PolyClassBlock_v2;
import dev.arubik.craftengine.script.PolyClassContraption;
import dev.arubik.craftengine.script.PolyClassContraptionWorld;
import dev.arubik.craftengine.script.PolyClassMachine_v2;
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
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        List list = polyClassMachine_v2 != null ? polyClassMachine_v2.tl$147_recipes() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
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
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        List list = polyClassMachine_v2 != null ? polyClassMachine_v2.tl$147_recipes() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
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
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        List list = polyClassMachine_v2 != null ? polyClassMachine_v2.tl$147_recipes() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
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
                PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                object = polyClassMachine_v2.tm$34_get_typed(string, string2);
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
                PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                List list = polyClassMachine_v2 != null ? polyClassMachine_v2.tl$147_recipes() : ((scriptValue7 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptProgram.elementsOf((ScriptValue)ScriptValue.NULL));
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
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object4);
                    object3 = polyClassMachine_v2.tm$34_get_typed(string, string3);
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
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object5);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string4, scriptValue17));
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
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object7);
                    object6 = polyClassMachine_v2.tm$34_get_typed(string, string5);
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
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object8);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string6, scriptValue30));
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
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        ScriptValue scriptValue2 = polyClassPlayer != null ? polyClassPlayer.pg$48_main_hand() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
                Object object2 = scriptValue6 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
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
        ScriptValue scriptValue;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue2 = polyClassMachine_v2 != null ? polyClassMachine_v2.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
        ScriptValue scriptValue;
        CallSite callSite2;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue3 = polyClassMachine_v2 != null ? polyClassMachine_v2.pg$139_block() : ((scriptValue2 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string = "face";
        if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Block")) {
            PolyClassBlock_v2 polyClassBlock_v2 = new PolyClassBlock_v2(object2);
            callSite2 = polyClassBlock_v2.tm$24_property(string);
        } else {
            callSite2 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        }
        CallSite callSite3 = callSite2;
        builder.val("face", (ScriptValue)callSite3);
        PolyClassMachine_v2 polyClassMachine_v22 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine_v22 != null ? polyClassMachine_v22.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
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
                ScriptValue scriptValue;
                scriptContext = builder.peek();
                if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptContext.getClassOrVar("item"), (ScriptContext)scriptContext).asBool()) {
                    return ScriptValue.NULL;
                }
                PolyClassMachine_v2 polyClassMachine_v2 = PolyClassMachine_v2.ofVar((ScriptContext)scriptContext, (String)"Machine");
                if (!(ScriptFormula.valuesEqual((ScriptValue)(polyClassMachine_v2 != null ? polyClassMachine_v2.pg$191_contraption() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) break block4;
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
                v0 = scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object).um$19_drop_item_toward(arrayList) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
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
                    var14_10 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var16_12 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var18_14 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var20_16 = ScriptValue.of((String)((var14_10 != null ? var14_10.pg$200_x() : ((var15_11 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var15_11, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var16_12 != null ? var16_12.pg$202_y() : ((var17_13 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var17_13, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var18_14 != null ? var18_14.pg$206_z() : ((var19_15 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var19_15, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr()));
                    var0.val("_hold_key", var20_16);
                    var21_17 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var23_19 = var21_17 != null ? var21_17.pg$191_contraption() : ((var22_18 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var22_18, (ScriptContext)var1_1) : ScriptValue.NULL);
                    var0.val("contraption", var23_19);
                    var24_20 = ScriptContext.builder().copyFrom(var1_1);
                    var24_20.val("effective_rpm", (ScriptValue)(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var23_19 != ScriptValue.NULL ? ((var25_21 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var25_21.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm")));
                    Saw._sawUpdateRpmSign(var24_20);
                    var26_22 = 0.0;
                    var28_23 = ScriptValue.of((double)0.0);
                    var0.val("is_now", var28_23);
                    var30_24 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var29_26 = var30_24 != null ? var30_24.pg$139_block() : ((var31_25 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)var31_25, (ScriptContext)var1_1) : ScriptValue.NULL);
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
                    if (!(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block197;
                    var39_34 = ScriptFormula.valuesEqual((ScriptValue)(var23_19 != ScriptValue.NULL ? ((var38_33 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var38_33.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true;
                    var40_35 = ScriptValue.of((boolean)var39_34);
                    var0.val("is_rotational", var40_35);
                    var41_36 = var1_1.getClassOrVar("contraption");
                    if (var41_36 != ScriptValue.NULL) {
                        if (var41_36 instanceof ScriptValue.Obj && (var43_38 = (var42_37 = (ScriptValue.Obj)var41_36).instance()) != null && !(var43_38 instanceof PolyClass) && var42_37.typeName().equals("Contraption")) {
                            var44_39 = new PolyClassContraption(var43_38);
                            v5 /* !! */  = ScriptValue.of((boolean)var44_39.tm$48_is_moving());
                        } else {
                            v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)var41_36, (ScriptContext)var1_1);
                        }
                    } else {
                        v5 /* !! */  = ScriptValue.NULL;
                    }
                    var45_40 = v5 /* !! */ ;
                    var0.val("is_linear", var45_40);
                    var46_41 = (var39_34 != false || var45_40.asBool() != false) != false ? 1.0 : 0.0;
                    var48_42 = ScriptValue.of((double)var46_41);
                    var0.val("is_now", var48_42);
                    if (!(var46_41 > 0.0)) break block198;
                    if (var39_34) {
                        var49_43 = 10.0;
                        v6 = 10.0 == 0.0 ? 0.0 : Math.abs(var23_19 != ScriptValue.NULL ? ((var51_44 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var51_44.tg$71_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum()) / var49_43;
                        v7 = ScriptValue.of((double)Math.max(1.0, v6));
                    } else {
                        var52_45 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                        var52_45.val("contraption", var23_19);
                        v7 = Utils.1._linearBreakSpeed((ScriptContext.Builder)var52_45);
                    }
                    var53_46 = v7;
                    var0.val("speed", var53_46);
                    var54_47 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var56_49 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var58_51 = ScriptFormula.addPolymorphic((ScriptValue)(var54_47 != null ? var54_47.pg$200_x() : ((var55_48 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var55_48, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var56_49 != null ? var56_49.pg$133_facing_dx() : ((var57_50 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var57_50, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("tx", var58_51);
                    var59_52 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var61_54 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var63_56 = ScriptFormula.addPolymorphic((ScriptValue)(var59_52 != null ? var59_52.pg$202_y() : ((var60_53 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var60_53, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var61_54 != null ? var61_54.pg$129_facing_dy() : ((var62_55 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var62_55, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("ty", var63_56);
                    var64_57 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var66_59 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var68_61 = ScriptFormula.addPolymorphic((ScriptValue)(var64_57 != null ? var64_57.pg$206_z() : ((var65_58 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var65_58, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var66_59 != null ? var66_59.pg$131_facing_dz() : ((var67_60 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var67_60, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("tz", var68_61);
                    var69_63 = var23_19 != ScriptValue.NULL ? ((var70_62 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var70_62.pg$53_contraption_world() : PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var71_64 = var58_51;
                    var72_65 = var63_56;
                    var73_66 = var68_61;
                    if (var69_63 instanceof ScriptValue.Obj && (var75_68 = (var74_67 = (ScriptValue.Obj)var69_63).instance()) != null && !(var75_68 instanceof PolyClass) && var74_67.typeName().equals("ContraptionWorld")) {
                        var76_69 = new PolyClassContraptionWorld(var75_68);
                        v8 = var76_69.tm$8_real_block(var71_64.asNum(), var72_65.asNum(), var73_66.asNum());
                    } else {
                        v8 = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)var69_63, (ScriptValue)var71_64, (ScriptValue)var72_65, (ScriptValue)var73_66, (ScriptContext)var1_1);
                    }
                    var77_70 = v8;
                    var0.val("target", (ScriptValue)var77_70);
                    if ((var36_31 != false || ScriptFormula.valuesEqual((ScriptValue)var77_70, (ScriptValue)var1_1.getClassOrVar("null")) != false) != false || (var77_70 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var77_70, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
                    var78_71 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var78_71.val("id", (ScriptValue)(var77_70 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var77_70, (ScriptContext)var1_1) : ScriptValue.NULL));
                    if (!(TreeUtils._isChoppable((ScriptContext.Builder)var78_71).asBool() ^ true)) {
                        v9 = false;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v9 = true;
                    }
                    if (v9) {
                        var79_72 = var1_1.getClassOrVar("contraption");
                        if (var79_72 != ScriptValue.NULL) {
                            var80_73 = var20_16;
                            if (var79_72 instanceof ScriptValue.Obj && (var82_75 = (var81_74 = (ScriptValue.Obj)var79_72).instance()) != null && !(var82_75 instanceof PolyClass) && var81_74.typeName().equals("Contraption")) {
                                var83_76 = new PolyClassContraption(var82_75);
                                v10 /* !! */  = ScriptValue.of((boolean)var83_76.tm$2_release(var80_73.asStr()));
                            } else {
                                v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var79_72, (ScriptValue)var80_73, (ScriptContext)var1_1);
                            }
                        } else {
                            v10 /* !! */  = ScriptValue.NULL;
                        }
                    } else {
                        var84_77 = var1_1.getClassOrVar("contraption");
                        if (var84_77 != ScriptValue.NULL) {
                            var85_78 = var20_16;
                            if (var84_77 instanceof ScriptValue.Obj && (var87_80 = (var86_79 = (ScriptValue.Obj)var84_77).instance()) != null && !(var87_80 instanceof PolyClass) && var86_79.typeName().equals("Contraption")) {
                                var88_81 = new PolyClassContraption(var87_80);
                                v11 /* !! */  = ScriptValue.of((boolean)var88_81.tm$12_hold(var85_78.asStr()));
                            } else {
                                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var84_77, (ScriptValue)var85_78, (ScriptContext)var1_1);
                            }
                        } else {
                            v11 /* !! */  = ScriptValue.NULL;
                        }
                        var89_82 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                        var89_82.val("id", (ScriptValue)(var77_70 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var77_70, (ScriptContext)var1_1) : ScriptValue.NULL));
                        if (TreeUtils._isLog((ScriptContext.Builder)var89_82).asBool()) {
                            var90_83 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                            var90_83.val("contraption", var23_19);
                            var90_83.val("base_x", var58_51);
                            var90_83.val("base_y", var63_56);
                            var90_83.val("base_z", var68_61);
                            var90_83.val("speed", var53_46);
                            TreeUtils._fellTree((ScriptContext.Builder)var90_83);
                        } else {
                            var91_84 = var1_1.getClassOrVar("Machine");
                            if (var91_84 != ScriptValue.NULL) {
                                var92_85 = var77_70;
                                var93_86 = var53_46;
                                if (var91_84 instanceof ScriptValue.Obj && (var95_88 = (var94_87 = (ScriptValue.Obj)var91_84).instance()) != null && !(var95_88 instanceof PolyClass) && var94_87.typeName().equals("Machine")) {
                                    var96_89 = new PolyClassMachine_v2(var95_88);
                                    v12 /* !! */  = var96_89.tm$2_tick_break((ScriptValue)var92_85, var93_86.asNum());
                                } else {
                                    v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var91_84, (ScriptValue)var92_85, (ScriptValue)var93_86, (ScriptContext)var1_1);
                                }
                            } else {
                                v12 /* !! */  = ScriptValue.NULL;
                            }
                            var97_90 = v12 /* !! */ ;
                            var0.val("result", var97_90);
                            if (ScriptFormula.valuesEqual((ScriptValue)var97_90, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var98_91 = ScriptProgram.elementsOf((ScriptValue)var97_90)) != null) {
                                for (ScriptValue var100_93 : var98_91) {
                                    var0.val("item", var100_93);
                                    var101_94 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                                    var101_94.val("item", var100_93);
                                    Utils.1._deposit((ScriptContext.Builder)var101_94);
                                }
                            }
                        }
                        var102_95 = var1_1.getClassOrVar("Machine");
                        if (var102_95 != ScriptValue.NULL) {
                            if (var39_34) {
                                var104_96 = ScriptContext.builder().copyFrom(var1_1);
                                var104_96.val("current_rpm", (ScriptValue)(var23_19 != ScriptValue.NULL ? ((var105_97 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var105_97.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL));
                                v13 = Saw._sawSuCost(var104_96);
                            } else {
                                v13 = var103_98 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 6.0)), (ScriptValue)var53_46);
                            }
                            if (var102_95 instanceof ScriptValue.Obj && (var107_100 = (var106_99 = (ScriptValue.Obj)var102_95).instance()) != null && !(var107_100 instanceof PolyClass) && var106_99.typeName().equals("Machine")) {
                                var108_101 = new PolyClassMachine_v2(var107_100);
                                v14 /* !! */  = ScriptValue.of((boolean)var108_101.tm$56_report_su(var103_98.asNum()));
                            } else {
                                v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var102_95, (ScriptValue)var103_98, (ScriptContext)var1_1);
                            }
                        } else {
                            v14 /* !! */  = ScriptValue.NULL;
                        }
                    }
                    break block199;
                }
                var109_102 = var1_1.getClassOrVar("contraption");
                if (var109_102 != ScriptValue.NULL) {
                    var110_103 = var20_16;
                    if (var109_102 instanceof ScriptValue.Obj && (var112_105 = (var111_104 = (ScriptValue.Obj)var109_102).instance()) != null && !(var112_105 instanceof PolyClass) && var111_104.typeName().equals("Contraption")) {
                        var113_106 = new PolyClassContraption(var112_105);
                        v15 /* !! */  = ScriptValue.of((boolean)var113_106.tm$2_release(var110_103.asStr()));
                    } else {
                        v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var109_102, (ScriptValue)var110_103, (ScriptContext)var1_1);
                    }
                } else {
                    v15 /* !! */  = ScriptValue.NULL;
                }
                break block199;
            }
            if (!(ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true)) break block199;
            var114_107 = 10.0;
            var116_108 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(var1_1.getNum("rpm")) / var114_107);
            var118_109 = ScriptValue.of((double)var116_108);
            var0.val("speed", var118_109);
            var119_110 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
            var121_112 = var119_110 != null ? var119_110.pg$137_facing_block() : ((var120_111 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var120_111, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("fb", var121_112);
            if (!(var36_31 ^ true)) ** GOTO lbl-1000
            v16 = var121_112 != ScriptValue.NULL ? ((var122_113 = PolyClassBlock_v2.ofGuarded((ScriptValue)var121_112)) != null ? var122_113.tg$56_is_air() : PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var121_112, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
            if (v16 ^ true) {
                v17 = true;
            } else lbl-1000:
            // 2 sources

            {
                v17 = false;
            }
            if (!v17) ** GOTO lbl-1000
            var123_114 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
            var123_114.val("id", (ScriptValue)(var121_112 != ScriptValue.NULL ? ((var124_115 = PolyClassBlock_v2.ofGuarded((ScriptValue)var121_112)) != null ? var124_115.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var121_112, (ScriptContext)var1_1)) : ScriptValue.NULL));
            if (TreeUtils._isChoppable((ScriptContext.Builder)var123_114).asBool()) {
                v18 = true;
            } else lbl-1000:
            // 2 sources

            {
                v18 = false;
            }
            if (v18) {
                var125_116 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                var125_116.val("id", (ScriptValue)(var121_112 != ScriptValue.NULL ? ((var126_117 = PolyClassBlock_v2.ofGuarded((ScriptValue)var121_112)) != null ? var126_117.pg$53_id() : PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var121_112, (ScriptContext)var1_1)) : ScriptValue.NULL));
                if (TreeUtils._isLog((ScriptContext.Builder)var125_116).asBool()) {
                    var127_118 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var127_118.val("contraption", var1_1.getClassOrVar("null"));
                    var128_119 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var130_121 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var127_118.val("base_x", ScriptFormula.addPolymorphic((ScriptValue)(var128_119 != null ? var128_119.pg$200_x() : ((var129_120 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var129_120, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var130_121 != null ? var130_121.pg$133_facing_dx() : ((var131_122 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var131_122, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var132_123 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var134_125 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var127_118.val("base_y", ScriptFormula.addPolymorphic((ScriptValue)(var132_123 != null ? var132_123.pg$202_y() : ((var133_124 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var133_124, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var134_125 != null ? var134_125.pg$129_facing_dy() : ((var135_126 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var135_126, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var136_127 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var138_129 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var127_118.val("base_z", ScriptFormula.addPolymorphic((ScriptValue)(var136_127 != null ? var136_127.pg$206_z() : ((var137_128 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var137_128, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var138_129 != null ? var138_129.pg$131_facing_dz() : ((var139_130 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var139_130, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var127_118.val("speed", ScriptValue.of((double)var116_108));
                    TreeUtils._fellTree((ScriptContext.Builder)var127_118);
                } else {
                    var140_131 = var1_1.getClassOrVar("Machine");
                    if (var140_131 != ScriptValue.NULL) {
                        var141_132 = var121_112;
                        var142_133 = var116_108;
                        if (var140_131 instanceof ScriptValue.Obj && (var145_135 = (var144_134 = (ScriptValue.Obj)var140_131).instance()) != null && !(var145_135 instanceof PolyClass) && var144_134.typeName().equals("Machine")) {
                            var146_136 = new PolyClassMachine_v2(var145_135);
                            v19 /* !! */  = var146_136.tm$2_tick_break(var141_132, var142_133);
                        } else {
                            v19 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var140_131, (ScriptValue)var141_132, (ScriptValue)ScriptValue.of((double)var142_133), (ScriptContext)var1_1);
                        }
                    } else {
                        v19 /* !! */  = ScriptValue.NULL;
                    }
                    var147_137 = v19 /* !! */ ;
                    var0.val("result", var147_137);
                    if (ScriptFormula.valuesEqual((ScriptValue)var147_137, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var148_138 = ScriptProgram.elementsOf((ScriptValue)var147_137)) != null) {
                        for (ScriptValue var150_140 : var148_138) {
                            var0.val("item", var150_140);
                            var151_141 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var151_141.val("item", var150_140);
                            Utils.1._deposit((ScriptContext.Builder)var151_141);
                        }
                    }
                }
                var152_142 = var1_1.getClassOrVar("Machine");
                if (var152_142 != ScriptValue.NULL) {
                    var154_143 = ScriptContext.builder().copyFrom(var1_1);
                    var154_143.val("current_rpm", var1_1.getClassOrVar("rpm"));
                    var153_144 = Saw._sawSuCost(var154_143);
                    if (var152_142 instanceof ScriptValue.Obj && (var156_146 = (var155_145 = (ScriptValue.Obj)var152_142).instance()) != null && !(var156_146 instanceof PolyClass) && var155_145.typeName().equals("Machine")) {
                        var157_147 = new PolyClassMachine_v2(var156_146);
                        v20 /* !! */  = ScriptValue.of((boolean)var157_147.tm$56_report_su(var153_144.asNum()));
                    } else {
                        v20 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var152_142, (ScriptValue)var153_144, (ScriptContext)var1_1);
                    }
                } else {
                    v20 /* !! */  = ScriptValue.NULL;
                }
            }
            var158_148 = 1.0;
            var160_149 = ScriptValue.of((double)1.0);
            var0.val("is_now", var160_149);
        }
        if (var1_1.getNum("is_now") > 0.0 != false && var36_31 != false) {
            var161_150 = PolyClassMachine_v2.ofVar((ScriptContext)var1_1, (String)"Machine");
            var163_152 = var161_150 != null ? var161_150.pg$158_belt() : ((var162_151 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "belt", (ScriptValue)var162_151, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("belt", var163_152);
            var165_154 = ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var23_19 != ScriptValue.NULL ? ((var164_153 = PolyClassContraption.ofGuarded((ScriptValue)var23_19)) != null ? var164_153.pg$70_rpm() : PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1)) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm");
            var0.val("belt_rpm", var165_154);
            var166_155 = var1_1.getClassOrVar("Machine");
            if (var166_155 != ScriptValue.NULL) {
                var167_156 = "_saw_item_id";
                var168_157 = "str";
                if (var166_155 instanceof ScriptValue.Obj && (var170_159 = (var169_158 = (ScriptValue.Obj)var166_155).instance()) != null && !(var170_159 instanceof PolyClass) && var169_158.typeName().equals("Machine")) {
                    var171_160 = new PolyClassMachine_v2(var170_159);
                    v21 /* !! */  = var171_160.tm$34_get_typed(var167_156, var168_157);
                } else {
                    v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var166_155, (ScriptValue)ScriptValue.of((String)var167_156), (ScriptValue)ScriptValue.of((String)var168_157), (ScriptContext)var1_1);
                }
            } else {
                v21 /* !! */  = ScriptValue.NULL;
            }
            var172_161 = v21 /* !! */ ;
            var0.val("active_id", var172_161);
            if (ScriptFormula.valuesEqual((ScriptValue)var172_161, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var173_162 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                var0.val("active_id", var173_162);
            }
            if (var1_1.getStr("active_id").equals("")) {
                v22 = var163_152 != ScriptValue.NULL ? ((var174_163 = PolyClassBelt.ofGuarded((ScriptValue)var163_152)) != null ? var174_163.tg$26_has_item() : PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var163_152, (ScriptContext)var1_1).asBool()) : ScriptValue.NULL.asBool();
                if (v22) {
                    var175_164 = var1_1.getClassOrVar("belt");
                    if (var175_164 != ScriptValue.NULL) {
                        if (var175_164 instanceof ScriptValue.Obj && (var177_166 = (var176_165 = (ScriptValue.Obj)var175_164).instance()) != null && !(var177_166 instanceof PolyClass) && var176_165.typeName().equals("Belt")) {
                            var178_167 = new PolyClassBelt(var177_166);
                            v23 /* !! */  = var178_167.tm$12_peek();
                        } else {
                            v23 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var175_164, (ScriptContext)var1_1);
                        }
                    } else {
                        v23 /* !! */  = ScriptValue.NULL;
                    }
                    var179_168 = v23 /* !! */ ;
                    var0.val("carried", var179_168);
                    var180_169 = ScriptContext.builder().copyFrom(var1_1);
                    var180_169.val("item_id", (ScriptValue)(var179_168 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var179_168, (ScriptContext)var1_1) : ScriptValue.NULL));
                    var181_170 = Saw._sawOutputFor(var180_169);
                    var0.val("out_id", var181_170);
                    ScriptFormula.callBuiltin1((String)"print", (ScriptValue)ScriptValue.of((String)("[saw-debug] own-belt has_item id=" + (var179_168 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var179_168, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " out_id=" + (ScriptFormula.valuesEqual((ScriptValue)var181_170, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "null")) : var181_170).asStr())), (ScriptContext)var1_1);
                    if (ScriptFormula.valuesEqual((ScriptValue)var181_170, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var182_171 = var1_1.getClassOrVar("belt");
                        if (var182_171 != ScriptValue.NULL) {
                            if (var182_171 instanceof ScriptValue.Obj && (var184_173 = (var183_172 = (ScriptValue.Obj)var182_171).instance()) != null && !(var184_173 instanceof PolyClass) && var183_172.typeName().equals("Belt")) {
                                var185_174 = new PolyClassBelt(var184_173);
                                v24 /* !! */  = var185_174.tm$0_take();
                            } else {
                                v24 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var182_171, (ScriptContext)var1_1);
                            }
                        } else {
                            v24 /* !! */  = ScriptValue.NULL;
                        }
                        var186_175 = v24 /* !! */ ;
                        var0.val("taken", var186_175);
                        var187_176 = var1_1.getClassOrVar("Machine");
                        if (var187_176 != ScriptValue.NULL) {
                            var188_177 = "_saw_item_id";
                            var189_178 = "str";
                            v25 /* !! */  = var190_179 = var186_175 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var186_175, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var187_176 instanceof ScriptValue.Obj && (var192_181 = (var191_180 = (ScriptValue.Obj)var187_176).instance()) != null && !(var192_181 instanceof PolyClass) && var191_180.typeName().equals("Machine")) {
                                var193_182 = new PolyClassMachine_v2(var192_181);
                                v26 /* !! */  = ScriptValue.of((boolean)var193_182.tm$82_set_typed(var188_177, var189_178, var190_179));
                            } else {
                                v26 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var187_176, (ScriptValue)ScriptValue.of((String)var188_177), (ScriptValue)ScriptValue.of((String)var189_178), (ScriptValue)var190_179, (ScriptContext)var1_1);
                            }
                        } else {
                            v26 /* !! */  = ScriptValue.NULL;
                        }
                        var194_183 = var1_1.getClassOrVar("Machine");
                        if (var194_183 != ScriptValue.NULL) {
                            var195_184 = "_saw_out_id";
                            var196_185 = "str";
                            var197_186 = var181_170;
                            if (var194_183 instanceof ScriptValue.Obj && (var199_188 = (var198_187 = (ScriptValue.Obj)var194_183).instance()) != null && !(var199_188 instanceof PolyClass) && var198_187.typeName().equals("Machine")) {
                                var200_189 = new PolyClassMachine_v2(var199_188);
                                v27 /* !! */  = ScriptValue.of((boolean)var200_189.tm$82_set_typed(var195_184, var196_185, var197_186));
                            } else {
                                v27 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var194_183, (ScriptValue)ScriptValue.of((String)var195_184), (ScriptValue)ScriptValue.of((String)var196_185), (ScriptValue)var197_186, (ScriptContext)var1_1);
                            }
                        } else {
                            v27 /* !! */  = ScriptValue.NULL;
                        }
                        var201_190 = var1_1.getClassOrVar("Machine");
                        if (var201_190 != ScriptValue.NULL) {
                            var202_191 = "_saw_count";
                            var203_192 = "int";
                            v28 /* !! */  = var204_193 = var186_175 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var186_175, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var201_190 instanceof ScriptValue.Obj && (var206_195 = (var205_194 = (ScriptValue.Obj)var201_190).instance()) != null && !(var206_195 instanceof PolyClass) && var205_194.typeName().equals("Machine")) {
                                var207_196 = new PolyClassMachine_v2(var206_195);
                                v29 /* !! */  = ScriptValue.of((boolean)var207_196.tm$82_set_typed(var202_191, var203_192, var204_193));
                            } else {
                                v29 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var201_190, (ScriptValue)ScriptValue.of((String)var202_191), (ScriptValue)ScriptValue.of((String)var203_192), (ScriptValue)var204_193, (ScriptContext)var1_1);
                            }
                        } else {
                            v29 /* !! */  = ScriptValue.NULL;
                        }
                        var208_197 = var1_1.getClassOrVar("Machine");
                        if (var208_197 != ScriptValue.NULL) {
                            var209_198 = "_saw_progress";
                            var210_199 = "int";
                            var211_200 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            if (var208_197 instanceof ScriptValue.Obj && (var213_202 = (var212_201 = (ScriptValue.Obj)var208_197).instance()) != null && !(var213_202 instanceof PolyClass) && var212_201.typeName().equals("Machine")) {
                                var214_203 = new PolyClassMachine_v2(var213_202);
                                v30 /* !! */  = ScriptValue.of((boolean)var214_203.tm$82_set_typed(var209_198, var210_199, var211_200));
                            } else {
                                v30 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var208_197, (ScriptValue)ScriptValue.of((String)var209_198), (ScriptValue)ScriptValue.of((String)var210_199), (ScriptValue)var211_200, (ScriptContext)var1_1);
                            }
                        } else {
                            v30 /* !! */  = ScriptValue.NULL;
                        }
                        var215_204 = var1_1.getClassOrVar("Machine");
                        if (var215_204 != ScriptValue.NULL) {
                            var216_205 = "_saw_from_ground";
                            var217_206 = "int";
                            var218_207 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            if (var215_204 instanceof ScriptValue.Obj && (var220_209 = (var219_208 = (ScriptValue.Obj)var215_204).instance()) != null && !(var220_209 instanceof PolyClass) && var219_208.typeName().equals("Machine")) {
                                var221_210 = new PolyClassMachine_v2(var220_209);
                                v31 /* !! */  = ScriptValue.of((boolean)var221_210.tm$82_set_typed(var216_205, var217_206, var218_207));
                            } else {
                                v31 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var215_204, (ScriptValue)ScriptValue.of((String)var216_205), (ScriptValue)ScriptValue.of((String)var217_206), (ScriptValue)var218_207, (ScriptContext)var1_1);
                            }
                        } else {
                            v31 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var222_211 = ScriptContext.builder().copyFrom(var1_1);
                    var223_212 = Saw._sawInputFace(var222_211);
                    var0.val("in_face", var223_212);
                    var224_213 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                    var224_213.val("face", var223_212);
                    var224_213.val("validator", var1_1.getClassOrVar("null"));
                    var224_213.val("amount", var1_1.getClassOrVar("null"));
                    var225_214 = BeltUtils.beltTake((ScriptContext.Builder)var224_213);
                    var0.val("taken", var225_214);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var225_214, (ScriptContext)var1_1).asBool() ^ true) {
                        var226_215 = ScriptContext.builder().copyFrom(var1_1);
                        var226_215.val("item_id", (ScriptValue)(var225_214 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var225_214, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var227_216 = Saw._sawOutputFor(var226_215);
                        var0.val("out_id", var227_216);
                        if (ScriptFormula.valuesEqual((ScriptValue)var227_216, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var228_217 = var1_1.getClassOrVar("Machine");
                            if (var228_217 != ScriptValue.NULL) {
                                var229_218 = "_saw_item_id";
                                var230_219 = "str";
                                v32 /* !! */  = var231_220 = var225_214 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var225_214, (ScriptContext)var1_1) : ScriptValue.NULL;
                                if (var228_217 instanceof ScriptValue.Obj && (var233_222 = (var232_221 = (ScriptValue.Obj)var228_217).instance()) != null && !(var233_222 instanceof PolyClass) && var232_221.typeName().equals("Machine")) {
                                    var234_223 = new PolyClassMachine_v2(var233_222);
                                    v33 /* !! */  = ScriptValue.of((boolean)var234_223.tm$82_set_typed(var229_218, var230_219, var231_220));
                                } else {
                                    v33 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var228_217, (ScriptValue)ScriptValue.of((String)var229_218), (ScriptValue)ScriptValue.of((String)var230_219), (ScriptValue)var231_220, (ScriptContext)var1_1);
                                }
                            } else {
                                v33 /* !! */  = ScriptValue.NULL;
                            }
                            var235_224 = var1_1.getClassOrVar("Machine");
                            if (var235_224 != ScriptValue.NULL) {
                                var236_225 = "_saw_out_id";
                                var237_226 = "str";
                                var238_227 = var227_216;
                                if (var235_224 instanceof ScriptValue.Obj && (var240_229 = (var239_228 = (ScriptValue.Obj)var235_224).instance()) != null && !(var240_229 instanceof PolyClass) && var239_228.typeName().equals("Machine")) {
                                    var241_230 = new PolyClassMachine_v2(var240_229);
                                    v34 /* !! */  = ScriptValue.of((boolean)var241_230.tm$82_set_typed(var236_225, var237_226, var238_227));
                                } else {
                                    v34 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var235_224, (ScriptValue)ScriptValue.of((String)var236_225), (ScriptValue)ScriptValue.of((String)var237_226), (ScriptValue)var238_227, (ScriptContext)var1_1);
                                }
                            } else {
                                v34 /* !! */  = ScriptValue.NULL;
                            }
                            var242_231 = var1_1.getClassOrVar("Machine");
                            if (var242_231 != ScriptValue.NULL) {
                                var243_232 = "_saw_count";
                                var244_233 = "int";
                                var245_234 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var225_214, (ScriptContext)var1_1);
                                if (var242_231 instanceof ScriptValue.Obj && (var247_236 = (var246_235 = (ScriptValue.Obj)var242_231).instance()) != null && !(var247_236 instanceof PolyClass) && var246_235.typeName().equals("Machine")) {
                                    var248_237 = new PolyClassMachine_v2(var247_236);
                                    v35 /* !! */  = ScriptValue.of((boolean)var248_237.tm$82_set_typed(var243_232, var244_233, var245_234));
                                } else {
                                    v35 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var242_231, (ScriptValue)ScriptValue.of((String)var243_232), (ScriptValue)ScriptValue.of((String)var244_233), (ScriptValue)var245_234, (ScriptContext)var1_1);
                                }
                            } else {
                                v35 /* !! */  = ScriptValue.NULL;
                            }
                            var249_238 = var1_1.getClassOrVar("Machine");
                            if (var249_238 != ScriptValue.NULL) {
                                var250_239 = "_saw_progress";
                                var251_240 = "int";
                                var252_241 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                if (var249_238 instanceof ScriptValue.Obj && (var254_243 = (var253_242 = (ScriptValue.Obj)var249_238).instance()) != null && !(var254_243 instanceof PolyClass) && var253_242.typeName().equals("Machine")) {
                                    var255_244 = new PolyClassMachine_v2(var254_243);
                                    v36 /* !! */  = ScriptValue.of((boolean)var255_244.tm$82_set_typed(var250_239, var251_240, var252_241));
                                } else {
                                    v36 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var249_238, (ScriptValue)ScriptValue.of((String)var250_239), (ScriptValue)ScriptValue.of((String)var251_240), (ScriptValue)var252_241, (ScriptContext)var1_1);
                                }
                            } else {
                                v36 /* !! */  = ScriptValue.NULL;
                            }
                            var256_245 = var1_1.getClassOrVar("Machine");
                            if (var256_245 != ScriptValue.NULL) {
                                var257_246 = "_saw_from_ground";
                                var258_247 = "int";
                                var259_248 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0);
                                if (var256_245 instanceof ScriptValue.Obj && (var261_250 = (var260_249 = (ScriptValue.Obj)var256_245).instance()) != null && !(var261_250 instanceof PolyClass) && var260_249.typeName().equals("Machine")) {
                                    var262_251 = new PolyClassMachine_v2(var261_250);
                                    v37 /* !! */  = ScriptValue.of((boolean)var262_251.tm$82_set_typed(var257_246, var258_247, var259_248));
                                } else {
                                    v37 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var256_245, (ScriptValue)ScriptValue.of((String)var257_246), (ScriptValue)ScriptValue.of((String)var258_247), (ScriptValue)var259_248, (ScriptContext)var1_1);
                                }
                            } else {
                                v37 /* !! */  = ScriptValue.NULL;
                            }
                        } else {
                            var263_252 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                            var263_252.val("face", var223_212);
                            var263_252.val("item", var225_214);
                            BeltUtils.beltGive((ScriptContext.Builder)var263_252);
                        }
                    } else {
                        var267_253 = var1_1.getClassOrVar("Machine");
                        if (var267_253 != ScriptValue.NULL) {
                            var268_254 = 0.7;
                            if (var267_253 instanceof ScriptValue.Obj && (var271_256 = (var270_255 = (ScriptValue.Obj)var267_253).instance()) != null && !(var271_256 instanceof PolyClass) && var270_255.typeName().equals("Machine")) {
                                var272_257 = new PolyClassMachine_v2(var271_256);
                                v38 /* !! */  = var272_257.tm$94_nearby_entities(var268_254);
                            } else {
                                v38 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var267_253, (ScriptValue)ScriptValue.of((double)var268_254), (ScriptContext)var1_1);
                            }
                        } else {
                            v38 /* !! */  = ScriptValue.NULL;
                        }
                        var264_258 = ScriptProgram.elementsOf((ScriptValue)v38 /* !! */ );
                        var273_259 = var1_1.getClassOrVar("out_id");
                        if (var264_258 != null) {
                            for (ScriptValue var266_261 : var264_258) {
                                var0.val("entity", var266_261);
                                var274_262 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                                var274_262.val("entity", var266_261);
                                if (!Utils.isRestingItem(var274_262).asBool()) continue;
                                var275_263 = ScriptContext.builder().copyFrom(var1_1);
                                var275_263.val("item_id", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var266_261 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var266_261, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                                var276_264 = Saw._sawOutputFor(var275_263);
                                var0.val("out_id", var276_264);
                                var273_259 = var276_264;
                                if (!(ScriptFormula.valuesEqual((ScriptValue)var273_259, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                                var277_265 = var1_1.getClassOrVar("Machine");
                                if (var277_265 != ScriptValue.NULL) {
                                    var278_266 = "_saw_item_id";
                                    var279_267 = "str";
                                    var280_268 = PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var266_261 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var266_261, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    if (var277_265 instanceof ScriptValue.Obj && (var282_270 = (var281_269 = (ScriptValue.Obj)var277_265).instance()) != null && !(var282_270 instanceof PolyClass) && var281_269.typeName().equals("Machine")) {
                                        var283_271 = new PolyClassMachine_v2(var282_270);
                                        v39 /* !! */  = ScriptValue.of((boolean)var283_271.tm$82_set_typed(var278_266, var279_267, (ScriptValue)var280_268));
                                    } else {
                                        v39 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var277_265, (ScriptValue)ScriptValue.of((String)var278_266), (ScriptValue)ScriptValue.of((String)var279_267), (ScriptValue)var280_268, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v39 /* !! */  = ScriptValue.NULL;
                                }
                                var284_272 = var1_1.getClassOrVar("Machine");
                                if (var284_272 != ScriptValue.NULL) {
                                    var285_273 = "_saw_out_id";
                                    var286_274 = "str";
                                    var287_275 = var273_259;
                                    if (var284_272 instanceof ScriptValue.Obj && (var289_277 = (var288_276 = (ScriptValue.Obj)var284_272).instance()) != null && !(var289_277 instanceof PolyClass) && var288_276.typeName().equals("Machine")) {
                                        var290_278 = new PolyClassMachine_v2(var289_277);
                                        v40 /* !! */  = ScriptValue.of((boolean)var290_278.tm$82_set_typed(var285_273, var286_274, var287_275));
                                    } else {
                                        v40 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var284_272, (ScriptValue)ScriptValue.of((String)var285_273), (ScriptValue)ScriptValue.of((String)var286_274), (ScriptValue)var287_275, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v40 /* !! */  = ScriptValue.NULL;
                                }
                                var291_279 = var1_1.getClassOrVar("Machine");
                                if (var291_279 != ScriptValue.NULL) {
                                    var292_280 = "_saw_count";
                                    var293_281 = "int";
                                    var294_282 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)(var266_261 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var266_261, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    if (var291_279 instanceof ScriptValue.Obj && (var296_284 = (var295_283 = (ScriptValue.Obj)var291_279).instance()) != null && !(var296_284 instanceof PolyClass) && var295_283.typeName().equals("Machine")) {
                                        var297_285 = new PolyClassMachine_v2(var296_284);
                                        v41 /* !! */  = ScriptValue.of((boolean)var297_285.tm$82_set_typed(var292_280, var293_281, var294_282));
                                    } else {
                                        v41 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var291_279, (ScriptValue)ScriptValue.of((String)var292_280), (ScriptValue)ScriptValue.of((String)var293_281), (ScriptValue)var294_282, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v41 /* !! */  = ScriptValue.NULL;
                                }
                                var298_286 = var1_1.getClassOrVar("Machine");
                                if (var298_286 != ScriptValue.NULL) {
                                    var299_287 = "_saw_progress";
                                    var300_288 = "int";
                                    var301_289 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                    if (var298_286 instanceof ScriptValue.Obj && (var303_291 = (var302_290 = (ScriptValue.Obj)var298_286).instance()) != null && !(var303_291 instanceof PolyClass) && var302_290.typeName().equals("Machine")) {
                                        var304_292 = new PolyClassMachine_v2(var303_291);
                                        v42 /* !! */  = ScriptValue.of((boolean)var304_292.tm$82_set_typed(var299_287, var300_288, var301_289));
                                    } else {
                                        v42 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var298_286, (ScriptValue)ScriptValue.of((String)var299_287), (ScriptValue)ScriptValue.of((String)var300_288), (ScriptValue)var301_289, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v42 /* !! */  = ScriptValue.NULL;
                                }
                                var305_293 = var1_1.getClassOrVar("Machine");
                                if (var305_293 != ScriptValue.NULL) {
                                    var306_294 = "_saw_from_ground";
                                    var307_295 = "int";
                                    var308_296 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0);
                                    if (var305_293 instanceof ScriptValue.Obj && (var310_298 = (var309_297 = (ScriptValue.Obj)var305_293).instance()) != null && !(var310_298 instanceof PolyClass) && var309_297.typeName().equals("Machine")) {
                                        var311_299 = new PolyClassMachine_v2(var310_298);
                                        v43 /* !! */  = ScriptValue.of((boolean)var311_299.tm$82_set_typed(var306_294, var307_295, var308_296));
                                    } else {
                                        v43 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var305_293, (ScriptValue)ScriptValue.of((String)var306_294), (ScriptValue)ScriptValue.of((String)var307_295), (ScriptValue)var308_296, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v43 /* !! */  = ScriptValue.NULL;
                                }
                                var312_300 = var1_1.getClassOrVar("entity");
                                v44 /* !! */  = var312_300 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var312_300, (ScriptContext)var1_1) : ScriptValue.NULL;
                                break;
                            }
                        }
                    }
                }
            } else {
                var313_301 = var1_1.getClassOrVar("Machine");
                if (var313_301 != ScriptValue.NULL) {
                    var314_302 = "_saw_progress";
                    var315_303 = "int";
                    if (var313_301 instanceof ScriptValue.Obj && (var317_305 = (var316_304 = (ScriptValue.Obj)var313_301).instance()) != null && !(var317_305 instanceof PolyClass) && var316_304.typeName().equals("Machine")) {
                        var318_306 = new PolyClassMachine_v2(var317_305);
                        v45 /* !! */  = var318_306.tm$34_get_typed(var314_302, var315_303);
                    } else {
                        v45 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var313_301, (ScriptValue)ScriptValue.of((String)var314_302), (ScriptValue)ScriptValue.of((String)var315_303), (ScriptContext)var1_1);
                    }
                } else {
                    v45 /* !! */  = ScriptValue.NULL;
                }
                var319_307 = v45 /* !! */ ;
                var0.val("progress", var319_307);
                if (ScriptFormula.valuesEqual((ScriptValue)var319_307, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var320_308 = 0.0;
                    var322_309 = ScriptValue.of((double)0.0);
                    var0.val("progress", var322_309);
                }
                var323_310 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)Math.abs(var165_154.asNum())));
                var0.val("progress", var323_310);
                var324_311 = var1_1.getClassOrVar("Machine");
                if (var324_311 != ScriptValue.NULL) {
                    var326_312 = ScriptContext.builder().copyFrom(var1_1);
                    var326_312.val("current_rpm", var165_154);
                    var325_313 = Saw._sawSuCost(var326_312);
                    if (var324_311 instanceof ScriptValue.Obj && (var328_315 = (var327_314 = (ScriptValue.Obj)var324_311).instance()) != null && !(var328_315 instanceof PolyClass) && var327_314.typeName().equals("Machine")) {
                        var329_316 = new PolyClassMachine_v2(var328_315);
                        v46 /* !! */  = ScriptValue.of((boolean)var329_316.tm$56_report_su(var325_313.asNum()));
                    } else {
                        v46 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var324_311, (ScriptValue)var325_313, (ScriptContext)var1_1);
                    }
                } else {
                    v46 /* !! */  = ScriptValue.NULL;
                }
                if (var323_310.asNum() >= var11_8) {
                    var330_317 = var1_1.getClassOrVar("Machine");
                    if (var330_317 != ScriptValue.NULL) {
                        var331_318 = "_saw_out_id";
                        var332_319 = "str";
                        if (var330_317 instanceof ScriptValue.Obj && (var334_321 = (var333_320 = (ScriptValue.Obj)var330_317).instance()) != null && !(var334_321 instanceof PolyClass) && var333_320.typeName().equals("Machine")) {
                            var335_322 = new PolyClassMachine_v2(var334_321);
                            v47 /* !! */  = var335_322.tm$34_get_typed(var331_318, var332_319);
                        } else {
                            v47 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var330_317, (ScriptValue)ScriptValue.of((String)var331_318), (ScriptValue)ScriptValue.of((String)var332_319), (ScriptContext)var1_1);
                        }
                    } else {
                        v47 /* !! */  = ScriptValue.NULL;
                    }
                    var336_323 = v47 /* !! */ ;
                    var0.val("out_id", var336_323);
                    var337_324 = var1_1.getClassOrVar("Machine");
                    if (var337_324 != ScriptValue.NULL) {
                        var338_325 = "_saw_count";
                        var339_326 = "int";
                        if (var337_324 instanceof ScriptValue.Obj && (var341_328 = (var340_327 = (ScriptValue.Obj)var337_324).instance()) != null && !(var341_328 instanceof PolyClass) && var340_327.typeName().equals("Machine")) {
                            var342_329 = new PolyClassMachine_v2(var341_328);
                            v48 /* !! */  = var342_329.tm$34_get_typed(var338_325, var339_326);
                        } else {
                            v48 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var337_324, (ScriptValue)ScriptValue.of((String)var338_325), (ScriptValue)ScriptValue.of((String)var339_326), (ScriptContext)var1_1);
                        }
                    } else {
                        v48 /* !! */  = ScriptValue.NULL;
                    }
                    var343_330 = v48 /* !! */ ;
                    var0.val("remaining", var343_330);
                    if (ScriptFormula.valuesEqual((ScriptValue)var343_330, (ScriptValue)var1_1.getClassOrVar("null")) != false || var343_330.asNum() <= 0.0 != false) {
                        var344_331 = 1.0;
                        var346_332 = ScriptValue.of((double)1.0);
                        var0.val("remaining", var346_332);
                    }
                    if ((var347_333 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        var348_334 = "_saw_from_ground";
                        var349_335 = "int";
                        if (var347_333 instanceof ScriptValue.Obj && (var351_337 = (var350_336 = (ScriptValue.Obj)var347_333).instance()) != null && !(var351_337 instanceof PolyClass) && var350_336.typeName().equals("Machine")) {
                            var352_338 = new PolyClassMachine_v2(var351_337);
                            v49 /* !! */  = var352_338.tm$34_get_typed(var348_334, var349_335);
                        } else {
                            v49 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var347_333, (ScriptValue)ScriptValue.of((String)var348_334), (ScriptValue)ScriptValue.of((String)var349_335), (ScriptContext)var1_1);
                        }
                    } else {
                        v49 /* !! */  = ScriptValue.NULL;
                    }
                    var353_339 = v49 /* !! */ ;
                    var0.val("from_ground", var353_339);
                    var354_340 = ScriptContext.builder().copyFrom(var1_1);
                    var354_340.val("item_id", var1_1.getClassOrVar("active_id"));
                    var354_340.val("out_id", var336_323);
                    var355_341 = Saw._sawRecipeFor(var354_340);
                    var0.val("recipe", var355_341);
                    var357_343 = ScriptFormula.valuesEqual((ScriptValue)var355_341, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var356_342 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "outputs", (ScriptValue)var356_342, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("null");
                    var0.val("result", var357_343);
                    if (ScriptFormula.valuesEqual((ScriptValue)var357_343, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        v50 /* !! */  = var357_343 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "items", (ScriptValue)var357_343, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var358_344 = new ArrayList<ScriptValue>();
                        var358_344.add(ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)var336_323, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0)), (ScriptContext)var1_1));
                        v50 /* !! */  = new ScriptValue.Array(var358_344);
                    }
                    var359_345 /* !! */  = v50 /* !! */ ;
                    var0.val("items", var359_345 /* !! */ );
                    var360_346 = ScriptProgram.elementsOf((ScriptValue)var359_345 /* !! */ );
                    var363_347 = var1_1.getClassOrVar("leftover");
                    if (var360_346 != null) {
                        for (ScriptValue var362_349 : var360_346) {
                            var0.val("out_item", var362_349);
                            if (ScriptFormula.valuesEqual((ScriptValue)var353_339, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0))) != false || ScriptFormula.valuesEqual((ScriptValue)var353_339, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0))) != false) {
                                var364_350 = ScriptContext.builder().copyFrom(var1_1);
                                var364_350.val("item", var362_349);
                                Saw._sawDepositDirectional(var364_350);
                                continue;
                            }
                            var365_351 = var1_1.getClassOrVar("belt");
                            if (var365_351 != ScriptValue.NULL) {
                                var366_352 = var362_349;
                                if (var365_351 instanceof ScriptValue.Obj && (var368_354 = (var367_353 = (ScriptValue.Obj)var365_351).instance()) != null && !(var368_354 instanceof PolyClass) && var367_353.typeName().equals("Belt")) {
                                    var369_355 = new PolyClassBelt(var368_354);
                                    v51 /* !! */  = var369_355.tm$10_put(var366_352);
                                } else {
                                    v51 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var365_351, (ScriptValue)var366_352, (ScriptContext)var1_1);
                                }
                            } else {
                                v51 /* !! */  = ScriptValue.NULL;
                            }
                            var370_356 = v51 /* !! */ ;
                            var0.val("leftover", var370_356);
                            var363_347 = var370_356;
                            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var363_347, (ScriptContext)var1_1).asBool() ^ true)) continue;
                            var371_357 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var371_357.val("item", var363_347);
                            Utils.1._deposit((ScriptContext.Builder)var371_357);
                        }
                    }
                    var372_358 = var1_1.getNum("remaining") - 1.0;
                    var374_359 = ScriptValue.of((double)var372_358);
                    var0.val("remaining", var374_359);
                    var375_360 = var1_1.getClassOrVar("Machine");
                    if (var375_360 != ScriptValue.NULL) {
                        var376_361 = "_saw_count";
                        var377_362 = "int";
                        var378_363 = ScriptValue.of((double)var372_358);
                        if (var375_360 instanceof ScriptValue.Obj && (var380_365 = (var379_364 = (ScriptValue.Obj)var375_360).instance()) != null && !(var380_365 instanceof PolyClass) && var379_364.typeName().equals("Machine")) {
                            var381_366 = new PolyClassMachine_v2(var380_365);
                            v52 /* !! */  = ScriptValue.of((boolean)var381_366.tm$82_set_typed(var376_361, var377_362, var378_363));
                        } else {
                            v52 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var375_360, (ScriptValue)ScriptValue.of((String)var376_361), (ScriptValue)ScriptValue.of((String)var377_362), (ScriptValue)var378_363, (ScriptContext)var1_1);
                        }
                    } else {
                        v52 /* !! */  = ScriptValue.NULL;
                    }
                    var382_367 = var1_1.getClassOrVar("Machine");
                    if (var382_367 != ScriptValue.NULL) {
                        var383_368 = "_saw_progress";
                        var384_369 = "int";
                        var385_370 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                        if (var382_367 instanceof ScriptValue.Obj && (var387_372 = (var386_371 = (ScriptValue.Obj)var382_367).instance()) != null && !(var387_372 instanceof PolyClass) && var386_371.typeName().equals("Machine")) {
                            var388_373 = new PolyClassMachine_v2(var387_372);
                            v53 /* !! */  = ScriptValue.of((boolean)var388_373.tm$82_set_typed(var383_368, var384_369, var385_370));
                        } else {
                            v53 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var382_367, (ScriptValue)ScriptValue.of((String)var383_368), (ScriptValue)ScriptValue.of((String)var384_369), (ScriptValue)var385_370, (ScriptContext)var1_1);
                        }
                    } else {
                        v53 /* !! */  = ScriptValue.NULL;
                    }
                    if (var372_358 <= 0.0) {
                        var389_374 = var1_1.getClassOrVar("Machine");
                        if (var389_374 != ScriptValue.NULL) {
                            var390_375 = "_saw_item_id";
                            var391_376 = "str";
                            var392_377 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                            if (var389_374 instanceof ScriptValue.Obj && (var394_379 = (var393_378 = (ScriptValue.Obj)var389_374).instance()) != null && !(var394_379 instanceof PolyClass) && var393_378.typeName().equals("Machine")) {
                                var395_380 = new PolyClassMachine_v2(var394_379);
                                v54 /* !! */  = ScriptValue.of((boolean)var395_380.tm$82_set_typed(var390_375, var391_376, var392_377));
                            } else {
                                v54 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var389_374, (ScriptValue)ScriptValue.of((String)var390_375), (ScriptValue)ScriptValue.of((String)var391_376), (ScriptValue)var392_377, (ScriptContext)var1_1);
                            }
                        } else {
                            v54 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var396_381 = var1_1.getClassOrVar("Machine");
                    if (var396_381 != ScriptValue.NULL) {
                        var397_382 = "_saw_progress";
                        var398_383 = "int";
                        var399_384 = var323_310;
                        if (var396_381 instanceof ScriptValue.Obj && (var401_386 = (var400_385 = (ScriptValue.Obj)var396_381).instance()) != null && !(var401_386 instanceof PolyClass) && var400_385.typeName().equals("Machine")) {
                            var402_387 = new PolyClassMachine_v2(var401_386);
                            v55 /* !! */  = ScriptValue.of((boolean)var402_387.tm$82_set_typed(var397_382, var398_383, var399_384));
                        } else {
                            v55 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var396_381, (ScriptValue)ScriptValue.of((String)var397_382), (ScriptValue)ScriptValue.of((String)var398_383), (ScriptValue)var399_384, (ScriptContext)var1_1);
                        }
                    } else {
                        v55 /* !! */  = ScriptValue.NULL;
                    }
                }
            }
        }
        var403_388 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var403_388.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "_saw_act"));
        var403_388.val("is_now", var1_1.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)var403_388);
        Saw.FILE_SCOPE = var0.build();
    }
}
