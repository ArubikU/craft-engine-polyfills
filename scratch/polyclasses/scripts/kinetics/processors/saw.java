/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassBlock_v3
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
                builder.val("r", scriptValue5);
                ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                builder.val("r", scriptValue5);
                ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                builder.val("r", scriptValue5);
                ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("ins", scriptValue6);
                scriptValue4 = scriptValue6;
                ScriptValue scriptValue7 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                        builder.val("r", scriptValue10);
                        ScriptValue scriptValue11 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("ins", scriptValue11);
                        scriptValue9 = scriptValue11;
                        if (!(ScriptFormula.callBuiltin1((String)"size", (ScriptValue)scriptValue9, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                        ScriptValue scriptValue12 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
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
            PolyClassBlock_v3 polyClassBlock_v3 = new PolyClassBlock_v3(object2);
            callSite = polyClassBlock_v3.tm$24_property(string);
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
            PolyClassBlock_v3 polyClassBlock_v3 = new PolyClassBlock_v3(object2);
            callSite2 = polyClassBlock_v3.tm$24_property(string);
        } else {
            callSite2 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        }
        CallSite callSite3 = callSite2;
        builder.val("face", (ScriptValue)callSite3);
        PolyClassMachine polyClassMachine2 = PolyClassMachine.ofVar((ScriptContext)scriptContext, (String)"Machine");
        ScriptValue scriptValue4 = polyClassMachine2 != null ? polyClassMachine2.pg$139_block() : ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        String string2 = "facing";
        if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Block")) {
            PolyClassBlock_v3 polyClassBlock_v3 = new PolyClassBlock_v3(object);
            callSite = polyClassBlock_v3.tm$24_property(string2);
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
                    var14_10 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var16_12 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var18_14 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var20_16 = ScriptValue.of((String)((var14_10 != null ? var14_10.pg$200_x() : ((var15_11 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var15_11, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var16_12 != null ? var16_12.pg$202_y() : ((var17_13 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var17_13, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr() + "," + (var18_14 != null ? var18_14.pg$206_z() : ((var19_15 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var19_15, (ScriptContext)var1_1) : ScriptValue.NULL)).asStr()));
                    var0.val("_hold_key", var20_16);
                    var21_17 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var23_19 = var21_17 != null ? var21_17.pg$191_contraption() : ((var22_18 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var22_18, (ScriptContext)var1_1) : ScriptValue.NULL);
                    var0.val("contraption", var23_19);
                    var24_20 = ScriptContext.builder().copyFrom(var1_1);
                    var24_20.val("effective_rpm", (ScriptValue)(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var23_19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm")));
                    Saw._sawUpdateRpmSign(var24_20);
                    var25_21 = 0.0;
                    var27_22 = ScriptValue.of((double)0.0);
                    var0.val("is_now", var27_22);
                    var29_23 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var28_25 = var29_23 != null ? var29_23.pg$139_block() : ((var30_24 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)var30_24, (ScriptContext)var1_1) : ScriptValue.NULL);
                    var31_26 = "face";
                    if (var28_25 instanceof ScriptValue.Obj && (var33_28 = (var32_27 = (ScriptValue.Obj)var28_25).instance()) != null && !(var33_28 instanceof PolyClass) && var32_27.typeName().equals("Block")) {
                        var34_29 = new PolyClassBlock_v3(var33_28);
                        v4 = var34_29.tm$24_property(var31_26);
                    } else {
                        v4 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)var28_25, (ScriptValue)ScriptValue.of((String)var31_26), (ScriptContext)var1_1);
                    }
                    var35_30 = ScriptFormula.valuesEqualStr((ScriptValue)v4, (String)"floor");
                    var36_31 = ScriptValue.of((boolean)var35_30);
                    var0.val("is_belt_facing", var36_31);
                    if (!(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block165;
                    var37_32 = ScriptFormula.valuesEqual((ScriptValue)(var23_19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true;
                    var38_33 = ScriptValue.of((boolean)var37_32);
                    var0.val("is_rotational", var38_33);
                    var39_34 = var1_1.getClassOrVar("contraption");
                    var40_35 = var39_34 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)var39_34, (ScriptContext)var1_1) : ScriptValue.NULL;
                    var0.val("is_linear", var40_35);
                    var41_36 = (var37_32 != false || var40_35.asBool() != false) != false ? 1.0 : 0.0;
                    var43_37 = ScriptValue.of((double)var41_36);
                    var0.val("is_now", var43_37);
                    if (!(var41_36 > 0.0)) break block166;
                    if (var37_32) {
                        var44_38 = 10.0;
                        v5 = ScriptValue.of((double)Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs((var23_19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1) : ScriptValue.NULL).asNum()) / var44_38));
                    } else {
                        var46_39 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                        var46_39.val("contraption", var23_19);
                        v5 = Utils.1._linearBreakSpeed((ScriptContext.Builder)var46_39);
                    }
                    var47_40 = v5;
                    var0.val("speed", var47_40);
                    var48_41 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var50_43 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var52_45 = ScriptFormula.addPolymorphic((ScriptValue)(var48_41 != null ? var48_41.pg$200_x() : ((var49_42 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var49_42, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var50_43 != null ? var50_43.pg$133_facing_dx() : ((var51_44 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var51_44, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("tx", var52_45);
                    var53_46 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var55_48 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var57_50 = ScriptFormula.addPolymorphic((ScriptValue)(var53_46 != null ? var53_46.pg$202_y() : ((var54_47 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var54_47, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var55_48 != null ? var55_48.pg$129_facing_dy() : ((var56_49 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var56_49, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("ty", var57_50);
                    var58_51 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var60_53 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var62_55 = ScriptFormula.addPolymorphic((ScriptValue)(var58_51 != null ? var58_51.pg$206_z() : ((var59_52 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var59_52, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var60_53 != null ? var60_53.pg$131_facing_dz() : ((var61_54 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var61_54, (ScriptContext)var1_1) : ScriptValue.NULL)));
                    var0.val("tz", var62_55);
                    var63_56 = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(var23_19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)var23_19, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)var52_45, (ScriptValue)var57_50, (ScriptValue)var62_55, (ScriptContext)var1_1);
                    var0.val("target", (ScriptValue)var63_56);
                    if ((var35_30 != false || ScriptFormula.valuesEqual((ScriptValue)var63_56, (ScriptValue)var1_1.getClassOrVar("null")) != false) != false || (var63_56 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var63_56, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
                    var64_57 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var64_57.val("id", (ScriptValue)(var63_56 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var63_56, (ScriptContext)var1_1) : ScriptValue.NULL));
                    if (!(TreeUtils._isChoppable((ScriptContext.Builder)var64_57).asBool() ^ true)) {
                        v6 = false;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v6 = true;
                    }
                    if (v6) {
                        var65_58 = var1_1.getClassOrVar("contraption");
                        v7 /* !! */  = var65_58 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var65_58, (ScriptValue)var20_16, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var66_59 = var1_1.getClassOrVar("contraption");
                        v8 /* !! */  = var66_59 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var66_59, (ScriptValue)var20_16, (ScriptContext)var1_1) : ScriptValue.NULL;
                        var67_60 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                        var67_60.val("id", (ScriptValue)(var63_56 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var63_56, (ScriptContext)var1_1) : ScriptValue.NULL));
                        if (TreeUtils._isLog((ScriptContext.Builder)var67_60).asBool()) {
                            var68_61 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                            var68_61.val("contraption", var23_19);
                            var68_61.val("base_x", var52_45);
                            var68_61.val("base_y", var57_50);
                            var68_61.val("base_z", var62_55);
                            var68_61.val("speed", var47_40);
                            TreeUtils._fellTree((ScriptContext.Builder)var68_61);
                        } else {
                            var69_62 = var1_1.getClassOrVar("Machine");
                            if (var69_62 != ScriptValue.NULL) {
                                var70_63 = var63_56;
                                var71_64 = var47_40;
                                if (var69_62 instanceof ScriptValue.Obj && (var73_66 = (var72_65 = (ScriptValue.Obj)var69_62).instance()) != null && !(var73_66 instanceof PolyClass) && var72_65.typeName().equals("Machine")) {
                                    var74_67 = new PolyClassMachine(var73_66);
                                    v9 /* !! */  = var74_67.tm$2_tick_break((ScriptValue)var70_63, var71_64.asNum());
                                } else {
                                    v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var69_62, (ScriptValue)var70_63, (ScriptValue)var71_64, (ScriptContext)var1_1);
                                }
                            } else {
                                v9 /* !! */  = ScriptValue.NULL;
                            }
                            var75_68 = v9 /* !! */ ;
                            var0.val("result", var75_68);
                            if (ScriptFormula.valuesEqual((ScriptValue)var75_68, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var76_69 = ScriptProgram.elementsOf((ScriptValue)var75_68)) != null) {
                                for (ScriptValue var78_71 : var76_69) {
                                    var0.val("item", var78_71);
                                    var79_72 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                                    var79_72.val("item", var78_71);
                                    Utils.1._deposit((ScriptContext.Builder)var79_72);
                                }
                            }
                        }
                        var80_73 = var1_1.getClassOrVar("Machine");
                        if (var80_73 != ScriptValue.NULL) {
                            if (var37_32) {
                                var82_74 = ScriptContext.builder().copyFrom(var1_1);
                                var82_74.val("current_rpm", (ScriptValue)(var23_19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1) : ScriptValue.NULL));
                                v10 = Saw._sawSuCost(var82_74);
                            } else {
                                v10 = var81_75 = ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 6.0)), (ScriptValue)var47_40);
                            }
                            if (var80_73 instanceof ScriptValue.Obj && (var84_77 = (var83_76 = (ScriptValue.Obj)var80_73).instance()) != null && !(var84_77 instanceof PolyClass) && var83_76.typeName().equals("Machine")) {
                                var85_78 = new PolyClassMachine(var84_77);
                                v11 /* !! */  = ScriptValue.of((boolean)var85_78.tm$56_report_su(var81_75.asNum()));
                            } else {
                                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var80_73, (ScriptValue)var81_75, (ScriptContext)var1_1);
                            }
                        } else {
                            v11 /* !! */  = ScriptValue.NULL;
                        }
                    }
                    break block167;
                }
                var86_79 = var1_1.getClassOrVar("contraption");
                v12 /* !! */  = var86_79 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var86_79, (ScriptValue)var20_16, (ScriptContext)var1_1) : ScriptValue.NULL;
                break block167;
            }
            if (!(ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0))) ^ true)) break block167;
            var87_80 = 10.0;
            var89_81 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(var1_1.getNum("rpm")) / var87_80);
            var91_82 = ScriptValue.of((double)var89_81);
            var0.val("speed", var91_82);
            var92_83 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
            var94_85 = var92_83 != null ? var92_83.pg$137_facing_block() : ((var93_84 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var93_84, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("fb", var94_85);
            if (!((var35_30 ^ true) != false && ((var94_85 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var94_85, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) ** GOTO lbl-1000
            var95_86 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
            var95_86.val("id", (ScriptValue)(var94_85 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var94_85, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (TreeUtils._isChoppable((ScriptContext.Builder)var95_86).asBool()) {
                v13 = true;
            } else lbl-1000:
            // 2 sources

            {
                v13 = false;
            }
            if (v13) {
                var96_87 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                var96_87.val("id", (ScriptValue)(var94_85 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var94_85, (ScriptContext)var1_1) : ScriptValue.NULL));
                if (TreeUtils._isLog((ScriptContext.Builder)var96_87).asBool()) {
                    var97_88 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var97_88.val("contraption", var1_1.getClassOrVar("null"));
                    var98_89 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var100_91 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var97_88.val("base_x", ScriptFormula.addPolymorphic((ScriptValue)(var98_89 != null ? var98_89.pg$200_x() : ((var99_90 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var99_90, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var100_91 != null ? var100_91.pg$133_facing_dx() : ((var101_92 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var101_92, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var102_93 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var104_95 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var97_88.val("base_y", ScriptFormula.addPolymorphic((ScriptValue)(var102_93 != null ? var102_93.pg$202_y() : ((var103_94 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var103_94, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var104_95 != null ? var104_95.pg$129_facing_dy() : ((var105_96 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var105_96, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var106_97 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var108_99 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
                    var97_88.val("base_z", ScriptFormula.addPolymorphic((ScriptValue)(var106_97 != null ? var106_97.pg$206_z() : ((var107_98 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var107_98, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)(var108_99 != null ? var108_99.pg$131_facing_dz() : ((var109_100 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var109_100, (ScriptContext)var1_1) : ScriptValue.NULL))));
                    var97_88.val("speed", ScriptValue.of((double)var89_81));
                    TreeUtils._fellTree((ScriptContext.Builder)var97_88);
                } else {
                    var110_101 = var1_1.getClassOrVar("Machine");
                    if (var110_101 != ScriptValue.NULL) {
                        var111_102 = var94_85;
                        var112_103 = var89_81;
                        if (var110_101 instanceof ScriptValue.Obj && (var115_105 = (var114_104 = (ScriptValue.Obj)var110_101).instance()) != null && !(var115_105 instanceof PolyClass) && var114_104.typeName().equals("Machine")) {
                            var116_106 = new PolyClassMachine(var115_105);
                            v14 /* !! */  = var116_106.tm$2_tick_break(var111_102, var112_103);
                        } else {
                            v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var110_101, (ScriptValue)var111_102, (ScriptValue)ScriptValue.of((double)var112_103), (ScriptContext)var1_1);
                        }
                    } else {
                        v14 /* !! */  = ScriptValue.NULL;
                    }
                    var117_107 = v14 /* !! */ ;
                    var0.val("result", var117_107);
                    if (ScriptFormula.valuesEqual((ScriptValue)var117_107, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var118_108 = ScriptProgram.elementsOf((ScriptValue)var117_107)) != null) {
                        for (ScriptValue var120_110 : var118_108) {
                            var0.val("item", var120_110);
                            var121_111 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var121_111.val("item", var120_110);
                            Utils.1._deposit((ScriptContext.Builder)var121_111);
                        }
                    }
                }
                var122_112 = var1_1.getClassOrVar("Machine");
                if (var122_112 != ScriptValue.NULL) {
                    var124_113 = ScriptContext.builder().copyFrom(var1_1);
                    var124_113.val("current_rpm", var1_1.getClassOrVar("rpm"));
                    var123_114 = Saw._sawSuCost(var124_113);
                    if (var122_112 instanceof ScriptValue.Obj && (var126_116 = (var125_115 = (ScriptValue.Obj)var122_112).instance()) != null && !(var126_116 instanceof PolyClass) && var125_115.typeName().equals("Machine")) {
                        var127_117 = new PolyClassMachine(var126_116);
                        v15 /* !! */  = ScriptValue.of((boolean)var127_117.tm$56_report_su(var123_114.asNum()));
                    } else {
                        v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var122_112, (ScriptValue)var123_114, (ScriptContext)var1_1);
                    }
                } else {
                    v15 /* !! */  = ScriptValue.NULL;
                }
            }
            var128_118 = 1.0;
            var130_119 = ScriptValue.of((double)1.0);
            var0.val("is_now", var130_119);
        }
        if (var1_1.getNum("is_now") > 0.0 != false && var35_30 != false) {
            var131_120 = PolyClassMachine.ofVar((ScriptContext)var1_1, (String)"Machine");
            var133_122 = var131_120 != null ? var131_120.pg$158_belt() : ((var132_121 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "belt", (ScriptValue)var132_121, (ScriptContext)var1_1) : ScriptValue.NULL);
            var0.val("belt", var133_122);
            var134_123 = ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? (var23_19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var23_19, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm");
            var0.val("belt_rpm", var134_123);
            var135_124 = var1_1.getClassOrVar("Machine");
            if (var135_124 != ScriptValue.NULL) {
                var136_125 = "_saw_item_id";
                var137_126 = "str";
                if (var135_124 instanceof ScriptValue.Obj && (var139_128 = (var138_127 = (ScriptValue.Obj)var135_124).instance()) != null && !(var139_128 instanceof PolyClass) && var138_127.typeName().equals("Machine")) {
                    var140_129 = new PolyClassMachine(var139_128);
                    v16 /* !! */  = var140_129.tm$34_get_typed(var136_125, var137_126);
                } else {
                    v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var135_124, (ScriptValue)ScriptValue.of((String)var136_125), (ScriptValue)ScriptValue.of((String)var137_126), (ScriptContext)var1_1);
                }
            } else {
                v16 /* !! */  = ScriptValue.NULL;
            }
            var141_130 = v16 /* !! */ ;
            var0.val("active_id", var141_130);
            if (ScriptFormula.valuesEqual((ScriptValue)var141_130, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var142_131 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                var0.val("active_id", var142_131);
            }
            if (var1_1.getStr("active_id").equals("")) {
                if ((var133_122 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var133_122, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
                    var143_132 = var1_1.getClassOrVar("belt");
                    var144_133 = var143_132 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var143_132, (ScriptContext)var1_1) : ScriptValue.NULL;
                    var0.val("carried", var144_133);
                    var145_134 = ScriptContext.builder().copyFrom(var1_1);
                    var145_134.val("item_id", (ScriptValue)(var144_133 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var144_133, (ScriptContext)var1_1) : ScriptValue.NULL));
                    var146_135 = Saw._sawOutputFor(var145_134);
                    var0.val("out_id", var146_135);
                    ScriptFormula.callBuiltin1((String)"print", (ScriptValue)ScriptValue.of((String)("[saw-debug] own-belt has_item id=" + (var144_133 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var144_133, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " out_id=" + (ScriptFormula.valuesEqual((ScriptValue)var146_135, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "null")) : var146_135).asStr())), (ScriptContext)var1_1);
                    if (ScriptFormula.valuesEqual((ScriptValue)var146_135, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var147_136 = var1_1.getClassOrVar("belt");
                        var148_137 = var147_136 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var147_136, (ScriptContext)var1_1) : ScriptValue.NULL;
                        var0.val("taken", var148_137);
                        var149_138 = var1_1.getClassOrVar("Machine");
                        if (var149_138 != ScriptValue.NULL) {
                            var150_139 = "_saw_item_id";
                            var151_140 = "str";
                            v17 /* !! */  = var152_141 = var148_137 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var148_137, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var149_138 instanceof ScriptValue.Obj && (var154_143 = (var153_142 = (ScriptValue.Obj)var149_138).instance()) != null && !(var154_143 instanceof PolyClass) && var153_142.typeName().equals("Machine")) {
                                var155_144 = new PolyClassMachine(var154_143);
                                v18 /* !! */  = ScriptValue.of((boolean)var155_144.tm$82_set_typed(var150_139, var151_140, var152_141));
                            } else {
                                v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var149_138, (ScriptValue)ScriptValue.of((String)var150_139), (ScriptValue)ScriptValue.of((String)var151_140), (ScriptValue)var152_141, (ScriptContext)var1_1);
                            }
                        } else {
                            v18 /* !! */  = ScriptValue.NULL;
                        }
                        var156_145 = var1_1.getClassOrVar("Machine");
                        if (var156_145 != ScriptValue.NULL) {
                            var157_146 = "_saw_out_id";
                            var158_147 = "str";
                            var159_148 = var146_135;
                            if (var156_145 instanceof ScriptValue.Obj && (var161_150 = (var160_149 = (ScriptValue.Obj)var156_145).instance()) != null && !(var161_150 instanceof PolyClass) && var160_149.typeName().equals("Machine")) {
                                var162_151 = new PolyClassMachine(var161_150);
                                v19 /* !! */  = ScriptValue.of((boolean)var162_151.tm$82_set_typed(var157_146, var158_147, var159_148));
                            } else {
                                v19 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var156_145, (ScriptValue)ScriptValue.of((String)var157_146), (ScriptValue)ScriptValue.of((String)var158_147), (ScriptValue)var159_148, (ScriptContext)var1_1);
                            }
                        } else {
                            v19 /* !! */  = ScriptValue.NULL;
                        }
                        var163_152 = var1_1.getClassOrVar("Machine");
                        if (var163_152 != ScriptValue.NULL) {
                            var164_153 = "_saw_count";
                            var165_154 = "int";
                            v20 /* !! */  = var166_155 = var148_137 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var148_137, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var163_152 instanceof ScriptValue.Obj && (var168_157 = (var167_156 = (ScriptValue.Obj)var163_152).instance()) != null && !(var168_157 instanceof PolyClass) && var167_156.typeName().equals("Machine")) {
                                var169_158 = new PolyClassMachine(var168_157);
                                v21 /* !! */  = ScriptValue.of((boolean)var169_158.tm$82_set_typed(var164_153, var165_154, var166_155));
                            } else {
                                v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var163_152, (ScriptValue)ScriptValue.of((String)var164_153), (ScriptValue)ScriptValue.of((String)var165_154), (ScriptValue)var166_155, (ScriptContext)var1_1);
                            }
                        } else {
                            v21 /* !! */  = ScriptValue.NULL;
                        }
                        var170_159 = var1_1.getClassOrVar("Machine");
                        if (var170_159 != ScriptValue.NULL) {
                            var171_160 = "_saw_progress";
                            var172_161 = "int";
                            var173_162 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            if (var170_159 instanceof ScriptValue.Obj && (var175_164 = (var174_163 = (ScriptValue.Obj)var170_159).instance()) != null && !(var175_164 instanceof PolyClass) && var174_163.typeName().equals("Machine")) {
                                var176_165 = new PolyClassMachine(var175_164);
                                v22 /* !! */  = ScriptValue.of((boolean)var176_165.tm$82_set_typed(var171_160, var172_161, var173_162));
                            } else {
                                v22 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var170_159, (ScriptValue)ScriptValue.of((String)var171_160), (ScriptValue)ScriptValue.of((String)var172_161), (ScriptValue)var173_162, (ScriptContext)var1_1);
                            }
                        } else {
                            v22 /* !! */  = ScriptValue.NULL;
                        }
                        var177_166 = var1_1.getClassOrVar("Machine");
                        if (var177_166 != ScriptValue.NULL) {
                            var178_167 = "_saw_from_ground";
                            var179_168 = "int";
                            var180_169 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                            if (var177_166 instanceof ScriptValue.Obj && (var182_171 = (var181_170 = (ScriptValue.Obj)var177_166).instance()) != null && !(var182_171 instanceof PolyClass) && var181_170.typeName().equals("Machine")) {
                                var183_172 = new PolyClassMachine(var182_171);
                                v23 /* !! */  = ScriptValue.of((boolean)var183_172.tm$82_set_typed(var178_167, var179_168, var180_169));
                            } else {
                                v23 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var177_166, (ScriptValue)ScriptValue.of((String)var178_167), (ScriptValue)ScriptValue.of((String)var179_168), (ScriptValue)var180_169, (ScriptContext)var1_1);
                            }
                        } else {
                            v23 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var184_173 = ScriptContext.builder().copyFrom(var1_1);
                    var185_174 = Saw._sawInputFace(var184_173);
                    var0.val("in_face", var185_174);
                    var186_175 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                    var186_175.val("face", var185_174);
                    var186_175.val("validator", var1_1.getClassOrVar("null"));
                    var186_175.val("amount", var1_1.getClassOrVar("null"));
                    var187_176 = BeltUtils.beltTake((ScriptContext.Builder)var186_175);
                    var0.val("taken", var187_176);
                    if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var187_176, (ScriptContext)var1_1).asBool() ^ true) {
                        var188_177 = ScriptContext.builder().copyFrom(var1_1);
                        var188_177.val("item_id", (ScriptValue)(var187_176 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var187_176, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var189_178 = Saw._sawOutputFor(var188_177);
                        var0.val("out_id", var189_178);
                        if (ScriptFormula.valuesEqual((ScriptValue)var189_178, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var190_179 = var1_1.getClassOrVar("Machine");
                            if (var190_179 != ScriptValue.NULL) {
                                var191_180 = "_saw_item_id";
                                var192_181 = "str";
                                v24 /* !! */  = var193_182 = var187_176 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var187_176, (ScriptContext)var1_1) : ScriptValue.NULL;
                                if (var190_179 instanceof ScriptValue.Obj && (var195_184 = (var194_183 = (ScriptValue.Obj)var190_179).instance()) != null && !(var195_184 instanceof PolyClass) && var194_183.typeName().equals("Machine")) {
                                    var196_185 = new PolyClassMachine(var195_184);
                                    v25 /* !! */  = ScriptValue.of((boolean)var196_185.tm$82_set_typed(var191_180, var192_181, var193_182));
                                } else {
                                    v25 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var190_179, (ScriptValue)ScriptValue.of((String)var191_180), (ScriptValue)ScriptValue.of((String)var192_181), (ScriptValue)var193_182, (ScriptContext)var1_1);
                                }
                            } else {
                                v25 /* !! */  = ScriptValue.NULL;
                            }
                            var197_186 = var1_1.getClassOrVar("Machine");
                            if (var197_186 != ScriptValue.NULL) {
                                var198_187 = "_saw_out_id";
                                var199_188 = "str";
                                var200_189 = var189_178;
                                if (var197_186 instanceof ScriptValue.Obj && (var202_191 = (var201_190 = (ScriptValue.Obj)var197_186).instance()) != null && !(var202_191 instanceof PolyClass) && var201_190.typeName().equals("Machine")) {
                                    var203_192 = new PolyClassMachine(var202_191);
                                    v26 /* !! */  = ScriptValue.of((boolean)var203_192.tm$82_set_typed(var198_187, var199_188, var200_189));
                                } else {
                                    v26 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var197_186, (ScriptValue)ScriptValue.of((String)var198_187), (ScriptValue)ScriptValue.of((String)var199_188), (ScriptValue)var200_189, (ScriptContext)var1_1);
                                }
                            } else {
                                v26 /* !! */  = ScriptValue.NULL;
                            }
                            var204_193 = var1_1.getClassOrVar("Machine");
                            if (var204_193 != ScriptValue.NULL) {
                                var205_194 = "_saw_count";
                                var206_195 = "int";
                                var207_196 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)var187_176, (ScriptContext)var1_1);
                                if (var204_193 instanceof ScriptValue.Obj && (var209_198 = (var208_197 = (ScriptValue.Obj)var204_193).instance()) != null && !(var209_198 instanceof PolyClass) && var208_197.typeName().equals("Machine")) {
                                    var210_199 = new PolyClassMachine(var209_198);
                                    v27 /* !! */  = ScriptValue.of((boolean)var210_199.tm$82_set_typed(var205_194, var206_195, var207_196));
                                } else {
                                    v27 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var204_193, (ScriptValue)ScriptValue.of((String)var205_194), (ScriptValue)ScriptValue.of((String)var206_195), (ScriptValue)var207_196, (ScriptContext)var1_1);
                                }
                            } else {
                                v27 /* !! */  = ScriptValue.NULL;
                            }
                            var211_200 = var1_1.getClassOrVar("Machine");
                            if (var211_200 != ScriptValue.NULL) {
                                var212_201 = "_saw_progress";
                                var213_202 = "int";
                                var214_203 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                if (var211_200 instanceof ScriptValue.Obj && (var216_205 = (var215_204 = (ScriptValue.Obj)var211_200).instance()) != null && !(var216_205 instanceof PolyClass) && var215_204.typeName().equals("Machine")) {
                                    var217_206 = new PolyClassMachine(var216_205);
                                    v28 /* !! */  = ScriptValue.of((boolean)var217_206.tm$82_set_typed(var212_201, var213_202, var214_203));
                                } else {
                                    v28 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var211_200, (ScriptValue)ScriptValue.of((String)var212_201), (ScriptValue)ScriptValue.of((String)var213_202), (ScriptValue)var214_203, (ScriptContext)var1_1);
                                }
                            } else {
                                v28 /* !! */  = ScriptValue.NULL;
                            }
                            var218_207 = var1_1.getClassOrVar("Machine");
                            if (var218_207 != ScriptValue.NULL) {
                                var219_208 = "_saw_from_ground";
                                var220_209 = "int";
                                var221_210 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0);
                                if (var218_207 instanceof ScriptValue.Obj && (var223_212 = (var222_211 = (ScriptValue.Obj)var218_207).instance()) != null && !(var223_212 instanceof PolyClass) && var222_211.typeName().equals("Machine")) {
                                    var224_213 = new PolyClassMachine(var223_212);
                                    v29 /* !! */  = ScriptValue.of((boolean)var224_213.tm$82_set_typed(var219_208, var220_209, var221_210));
                                } else {
                                    v29 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var218_207, (ScriptValue)ScriptValue.of((String)var219_208), (ScriptValue)ScriptValue.of((String)var220_209), (ScriptValue)var221_210, (ScriptContext)var1_1);
                                }
                            } else {
                                v29 /* !! */  = ScriptValue.NULL;
                            }
                        } else {
                            var225_214 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                            var225_214.val("face", var185_174);
                            var225_214.val("item", var187_176);
                            BeltUtils.beltGive((ScriptContext.Builder)var225_214);
                        }
                    } else {
                        var229_215 = var1_1.getClassOrVar("Machine");
                        if (var229_215 != ScriptValue.NULL) {
                            var230_216 = 0.7;
                            if (var229_215 instanceof ScriptValue.Obj && (var233_218 = (var232_217 = (ScriptValue.Obj)var229_215).instance()) != null && !(var233_218 instanceof PolyClass) && var232_217.typeName().equals("Machine")) {
                                var234_219 = new PolyClassMachine(var233_218);
                                v30 /* !! */  = var234_219.tm$94_nearby_entities(var230_216);
                            } else {
                                v30 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var229_215, (ScriptValue)ScriptValue.of((double)var230_216), (ScriptContext)var1_1);
                            }
                        } else {
                            v30 /* !! */  = ScriptValue.NULL;
                        }
                        var226_220 = ScriptProgram.elementsOf((ScriptValue)v30 /* !! */ );
                        var235_221 = var1_1.getClassOrVar("out_id");
                        if (var226_220 != null) {
                            for (ScriptValue var228_223 : var226_220) {
                                var0.val("entity", var228_223);
                                var236_224 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                                var236_224.val("entity", var228_223);
                                if (!Utils.isRestingItem(var236_224).asBool()) continue;
                                var237_225 = ScriptContext.builder().copyFrom(var1_1);
                                var237_225.val("item_id", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var228_223 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var228_223, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                                var238_226 = Saw._sawOutputFor(var237_225);
                                var0.val("out_id", var238_226);
                                var235_221 = var238_226;
                                if (!(ScriptFormula.valuesEqual((ScriptValue)var235_221, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                                var239_227 = var1_1.getClassOrVar("Machine");
                                if (var239_227 != ScriptValue.NULL) {
                                    var240_228 = "_saw_item_id";
                                    var241_229 = "str";
                                    var242_230 = PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var228_223 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var228_223, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    if (var239_227 instanceof ScriptValue.Obj && (var244_232 = (var243_231 = (ScriptValue.Obj)var239_227).instance()) != null && !(var244_232 instanceof PolyClass) && var243_231.typeName().equals("Machine")) {
                                        var245_233 = new PolyClassMachine(var244_232);
                                        v31 /* !! */  = ScriptValue.of((boolean)var245_233.tm$82_set_typed(var240_228, var241_229, (ScriptValue)var242_230));
                                    } else {
                                        v31 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var239_227, (ScriptValue)ScriptValue.of((String)var240_228), (ScriptValue)ScriptValue.of((String)var241_229), (ScriptValue)var242_230, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v31 /* !! */  = ScriptValue.NULL;
                                }
                                var246_234 = var1_1.getClassOrVar("Machine");
                                if (var246_234 != ScriptValue.NULL) {
                                    var247_235 = "_saw_out_id";
                                    var248_236 = "str";
                                    var249_237 = var235_221;
                                    if (var246_234 instanceof ScriptValue.Obj && (var251_239 = (var250_238 = (ScriptValue.Obj)var246_234).instance()) != null && !(var251_239 instanceof PolyClass) && var250_238.typeName().equals("Machine")) {
                                        var252_240 = new PolyClassMachine(var251_239);
                                        v32 /* !! */  = ScriptValue.of((boolean)var252_240.tm$82_set_typed(var247_235, var248_236, var249_237));
                                    } else {
                                        v32 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var246_234, (ScriptValue)ScriptValue.of((String)var247_235), (ScriptValue)ScriptValue.of((String)var248_236), (ScriptValue)var249_237, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v32 /* !! */  = ScriptValue.NULL;
                                }
                                var253_241 = var1_1.getClassOrVar("Machine");
                                if (var253_241 != ScriptValue.NULL) {
                                    var254_242 = "_saw_count";
                                    var255_243 = "int";
                                    var256_244 = ScriptFormula.callBuiltin1((String)"item_count", (ScriptValue)(var228_223 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var228_223, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    if (var253_241 instanceof ScriptValue.Obj && (var258_246 = (var257_245 = (ScriptValue.Obj)var253_241).instance()) != null && !(var258_246 instanceof PolyClass) && var257_245.typeName().equals("Machine")) {
                                        var259_247 = new PolyClassMachine(var258_246);
                                        v33 /* !! */  = ScriptValue.of((boolean)var259_247.tm$82_set_typed(var254_242, var255_243, var256_244));
                                    } else {
                                        v33 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var253_241, (ScriptValue)ScriptValue.of((String)var254_242), (ScriptValue)ScriptValue.of((String)var255_243), (ScriptValue)var256_244, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v33 /* !! */  = ScriptValue.NULL;
                                }
                                var260_248 = var1_1.getClassOrVar("Machine");
                                if (var260_248 != ScriptValue.NULL) {
                                    var261_249 = "_saw_progress";
                                    var262_250 = "int";
                                    var263_251 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                                    if (var260_248 instanceof ScriptValue.Obj && (var265_253 = (var264_252 = (ScriptValue.Obj)var260_248).instance()) != null && !(var265_253 instanceof PolyClass) && var264_252.typeName().equals("Machine")) {
                                        var266_254 = new PolyClassMachine(var265_253);
                                        v34 /* !! */  = ScriptValue.of((boolean)var266_254.tm$82_set_typed(var261_249, var262_250, var263_251));
                                    } else {
                                        v34 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var260_248, (ScriptValue)ScriptValue.of((String)var261_249), (ScriptValue)ScriptValue.of((String)var262_250), (ScriptValue)var263_251, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v34 /* !! */  = ScriptValue.NULL;
                                }
                                var267_255 = var1_1.getClassOrVar("Machine");
                                if (var267_255 != ScriptValue.NULL) {
                                    var268_256 = "_saw_from_ground";
                                    var269_257 = "int";
                                    var270_258 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0);
                                    if (var267_255 instanceof ScriptValue.Obj && (var272_260 = (var271_259 = (ScriptValue.Obj)var267_255).instance()) != null && !(var272_260 instanceof PolyClass) && var271_259.typeName().equals("Machine")) {
                                        var273_261 = new PolyClassMachine(var272_260);
                                        v35 /* !! */  = ScriptValue.of((boolean)var273_261.tm$82_set_typed(var268_256, var269_257, var270_258));
                                    } else {
                                        v35 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var267_255, (ScriptValue)ScriptValue.of((String)var268_256), (ScriptValue)ScriptValue.of((String)var269_257), (ScriptValue)var270_258, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v35 /* !! */  = ScriptValue.NULL;
                                }
                                var274_262 = var1_1.getClassOrVar("entity");
                                v36 /* !! */  = var274_262 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var274_262, (ScriptContext)var1_1) : ScriptValue.NULL;
                                break;
                            }
                        }
                    }
                }
            } else {
                var275_263 = var1_1.getClassOrVar("Machine");
                if (var275_263 != ScriptValue.NULL) {
                    var276_264 = "_saw_progress";
                    var277_265 = "int";
                    if (var275_263 instanceof ScriptValue.Obj && (var279_267 = (var278_266 = (ScriptValue.Obj)var275_263).instance()) != null && !(var279_267 instanceof PolyClass) && var278_266.typeName().equals("Machine")) {
                        var280_268 = new PolyClassMachine(var279_267);
                        v37 /* !! */  = var280_268.tm$34_get_typed(var276_264, var277_265);
                    } else {
                        v37 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var275_263, (ScriptValue)ScriptValue.of((String)var276_264), (ScriptValue)ScriptValue.of((String)var277_265), (ScriptContext)var1_1);
                    }
                } else {
                    v37 /* !! */  = ScriptValue.NULL;
                }
                var281_269 = v37 /* !! */ ;
                var0.val("progress", var281_269);
                if (ScriptFormula.valuesEqual((ScriptValue)var281_269, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var282_270 = 0.0;
                    var284_271 = ScriptValue.of((double)0.0);
                    var0.val("progress", var284_271);
                }
                var285_272 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)Math.abs(var134_123.asNum())));
                var0.val("progress", var285_272);
                var286_273 = var1_1.getClassOrVar("Machine");
                if (var286_273 != ScriptValue.NULL) {
                    var288_274 = ScriptContext.builder().copyFrom(var1_1);
                    var288_274.val("current_rpm", var134_123);
                    var287_275 = Saw._sawSuCost(var288_274);
                    if (var286_273 instanceof ScriptValue.Obj && (var290_277 = (var289_276 = (ScriptValue.Obj)var286_273).instance()) != null && !(var290_277 instanceof PolyClass) && var289_276.typeName().equals("Machine")) {
                        var291_278 = new PolyClassMachine(var290_277);
                        v38 /* !! */  = ScriptValue.of((boolean)var291_278.tm$56_report_su(var287_275.asNum()));
                    } else {
                        v38 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var286_273, (ScriptValue)var287_275, (ScriptContext)var1_1);
                    }
                } else {
                    v38 /* !! */  = ScriptValue.NULL;
                }
                if (var285_272.asNum() >= var11_8) {
                    var292_279 = var1_1.getClassOrVar("Machine");
                    if (var292_279 != ScriptValue.NULL) {
                        var293_280 = "_saw_out_id";
                        var294_281 = "str";
                        if (var292_279 instanceof ScriptValue.Obj && (var296_283 = (var295_282 = (ScriptValue.Obj)var292_279).instance()) != null && !(var296_283 instanceof PolyClass) && var295_282.typeName().equals("Machine")) {
                            var297_284 = new PolyClassMachine(var296_283);
                            v39 /* !! */  = var297_284.tm$34_get_typed(var293_280, var294_281);
                        } else {
                            v39 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var292_279, (ScriptValue)ScriptValue.of((String)var293_280), (ScriptValue)ScriptValue.of((String)var294_281), (ScriptContext)var1_1);
                        }
                    } else {
                        v39 /* !! */  = ScriptValue.NULL;
                    }
                    var298_285 = v39 /* !! */ ;
                    var0.val("out_id", var298_285);
                    var299_286 = var1_1.getClassOrVar("Machine");
                    if (var299_286 != ScriptValue.NULL) {
                        var300_287 = "_saw_count";
                        var301_288 = "int";
                        if (var299_286 instanceof ScriptValue.Obj && (var303_290 = (var302_289 = (ScriptValue.Obj)var299_286).instance()) != null && !(var303_290 instanceof PolyClass) && var302_289.typeName().equals("Machine")) {
                            var304_291 = new PolyClassMachine(var303_290);
                            v40 /* !! */  = var304_291.tm$34_get_typed(var300_287, var301_288);
                        } else {
                            v40 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var299_286, (ScriptValue)ScriptValue.of((String)var300_287), (ScriptValue)ScriptValue.of((String)var301_288), (ScriptContext)var1_1);
                        }
                    } else {
                        v40 /* !! */  = ScriptValue.NULL;
                    }
                    var305_292 = v40 /* !! */ ;
                    var0.val("remaining", var305_292);
                    if (ScriptFormula.valuesEqual((ScriptValue)var305_292, (ScriptValue)var1_1.getClassOrVar("null")) != false || var305_292.asNum() <= 0.0 != false) {
                        var306_293 = 1.0;
                        var308_294 = ScriptValue.of((double)1.0);
                        var0.val("remaining", var308_294);
                    }
                    if ((var309_295 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        var310_296 = "_saw_from_ground";
                        var311_297 = "int";
                        if (var309_295 instanceof ScriptValue.Obj && (var313_299 = (var312_298 = (ScriptValue.Obj)var309_295).instance()) != null && !(var313_299 instanceof PolyClass) && var312_298.typeName().equals("Machine")) {
                            var314_300 = new PolyClassMachine(var313_299);
                            v41 /* !! */  = var314_300.tm$34_get_typed(var310_296, var311_297);
                        } else {
                            v41 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var309_295, (ScriptValue)ScriptValue.of((String)var310_296), (ScriptValue)ScriptValue.of((String)var311_297), (ScriptContext)var1_1);
                        }
                    } else {
                        v41 /* !! */  = ScriptValue.NULL;
                    }
                    var315_301 = v41 /* !! */ ;
                    var0.val("from_ground", var315_301);
                    var316_302 = ScriptContext.builder().copyFrom(var1_1);
                    var316_302.val("item_id", var1_1.getClassOrVar("active_id"));
                    var316_302.val("out_id", var298_285);
                    var317_303 = Saw._sawRecipeFor(var316_302);
                    var0.val("recipe", var317_303);
                    var319_305 = ScriptFormula.valuesEqual((ScriptValue)var317_303, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var318_304 = var1_1.getClassOrVar("recipe")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "outputs", (ScriptValue)var318_304, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("null");
                    var0.val("result", var319_305);
                    if (ScriptFormula.valuesEqual((ScriptValue)var319_305, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        v42 /* !! */  = var319_305 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "items", (ScriptValue)var319_305, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var320_306 = new ArrayList<ScriptValue>();
                        var320_306.add(ScriptFormula.callBuiltin2((String)"MinecraftItem", (ScriptValue)var298_285, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0)), (ScriptContext)var1_1));
                        v42 /* !! */  = new ScriptValue.Array(var320_306);
                    }
                    var321_307 /* !! */  = v42 /* !! */ ;
                    var0.val("items", var321_307 /* !! */ );
                    var322_308 = ScriptProgram.elementsOf((ScriptValue)var321_307 /* !! */ );
                    var325_309 = var1_1.getClassOrVar("leftover");
                    if (var322_308 != null) {
                        for (ScriptValue var324_311 : var322_308) {
                            var0.val("out_item", var324_311);
                            if (ScriptFormula.valuesEqual((ScriptValue)var315_301, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 1.0))) != false || ScriptFormula.valuesEqual((ScriptValue)var315_301, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 2.0))) != false) {
                                var326_312 = ScriptContext.builder().copyFrom(var1_1);
                                var326_312.val("item", var324_311);
                                Saw._sawDepositDirectional(var326_312);
                                continue;
                            }
                            var327_313 = var1_1.getClassOrVar("belt");
                            var328_314 = var327_313 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var327_313, (ScriptValue)var324_311, (ScriptContext)var1_1) : ScriptValue.NULL;
                            var0.val("leftover", var328_314);
                            var325_309 = var328_314;
                            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)var325_309, (ScriptContext)var1_1).asBool() ^ true)) continue;
                            var329_315 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var329_315.val("item", var325_309);
                            Utils.1._deposit((ScriptContext.Builder)var329_315);
                        }
                    }
                    var330_316 = var1_1.getNum("remaining") - 1.0;
                    var332_317 = ScriptValue.of((double)var330_316);
                    var0.val("remaining", var332_317);
                    var333_318 = var1_1.getClassOrVar("Machine");
                    if (var333_318 != ScriptValue.NULL) {
                        var334_319 = "_saw_count";
                        var335_320 = "int";
                        var336_321 = ScriptValue.of((double)var330_316);
                        if (var333_318 instanceof ScriptValue.Obj && (var338_323 = (var337_322 = (ScriptValue.Obj)var333_318).instance()) != null && !(var338_323 instanceof PolyClass) && var337_322.typeName().equals("Machine")) {
                            var339_324 = new PolyClassMachine(var338_323);
                            v43 /* !! */  = ScriptValue.of((boolean)var339_324.tm$82_set_typed(var334_319, var335_320, var336_321));
                        } else {
                            v43 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var333_318, (ScriptValue)ScriptValue.of((String)var334_319), (ScriptValue)ScriptValue.of((String)var335_320), (ScriptValue)var336_321, (ScriptContext)var1_1);
                        }
                    } else {
                        v43 /* !! */  = ScriptValue.NULL;
                    }
                    var340_325 = var1_1.getClassOrVar("Machine");
                    if (var340_325 != ScriptValue.NULL) {
                        var341_326 = "_saw_progress";
                        var342_327 = "int";
                        var343_328 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Saw.class, 0.0);
                        if (var340_325 instanceof ScriptValue.Obj && (var345_330 = (var344_329 = (ScriptValue.Obj)var340_325).instance()) != null && !(var345_330 instanceof PolyClass) && var344_329.typeName().equals("Machine")) {
                            var346_331 = new PolyClassMachine(var345_330);
                            v44 /* !! */  = ScriptValue.of((boolean)var346_331.tm$82_set_typed(var341_326, var342_327, var343_328));
                        } else {
                            v44 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var340_325, (ScriptValue)ScriptValue.of((String)var341_326), (ScriptValue)ScriptValue.of((String)var342_327), (ScriptValue)var343_328, (ScriptContext)var1_1);
                        }
                    } else {
                        v44 /* !! */  = ScriptValue.NULL;
                    }
                    if (var330_316 <= 0.0) {
                        var347_332 = var1_1.getClassOrVar("Machine");
                        if (var347_332 != ScriptValue.NULL) {
                            var348_333 = "_saw_item_id";
                            var349_334 = "str";
                            var350_335 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "");
                            if (var347_332 instanceof ScriptValue.Obj && (var352_337 = (var351_336 = (ScriptValue.Obj)var347_332).instance()) != null && !(var352_337 instanceof PolyClass) && var351_336.typeName().equals("Machine")) {
                                var353_338 = new PolyClassMachine(var352_337);
                                v45 /* !! */  = ScriptValue.of((boolean)var353_338.tm$82_set_typed(var348_333, var349_334, var350_335));
                            } else {
                                v45 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var347_332, (ScriptValue)ScriptValue.of((String)var348_333), (ScriptValue)ScriptValue.of((String)var349_334), (ScriptValue)var350_335, (ScriptContext)var1_1);
                            }
                        } else {
                            v45 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var354_339 = var1_1.getClassOrVar("Machine");
                    if (var354_339 != ScriptValue.NULL) {
                        var355_340 = "_saw_progress";
                        var356_341 = "int";
                        var357_342 = var285_272;
                        if (var354_339 instanceof ScriptValue.Obj && (var359_344 = (var358_343 = (ScriptValue.Obj)var354_339).instance()) != null && !(var359_344 instanceof PolyClass) && var358_343.typeName().equals("Machine")) {
                            var360_345 = new PolyClassMachine(var359_344);
                            v46 /* !! */  = ScriptValue.of((boolean)var360_345.tm$82_set_typed(var355_340, var356_341, var357_342));
                        } else {
                            v46 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var354_339, (ScriptValue)ScriptValue.of((String)var355_340), (ScriptValue)ScriptValue.of((String)var356_341), (ScriptValue)var357_342, (ScriptContext)var1_1);
                        }
                    } else {
                        v46 /* !! */  = ScriptValue.NULL;
                    }
                }
            }
        }
        var361_346 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var361_346.val("act_key",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Saw.class, "_saw_act"));
        var361_346.val("is_now", var1_1.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)var361_346);
        Saw.FILE_SCOPE = var0.build();
    }
}
