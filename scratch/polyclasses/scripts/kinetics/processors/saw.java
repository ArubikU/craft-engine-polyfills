/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer
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
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer;
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
import java.util.ArrayList;
import java.util.List;

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
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("count", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        List list = ScriptProgram.elementsOf((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine.pg$146_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                builder.val("r", scriptValue3);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("r");
                ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("ins", scriptValue5);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue5);
                if (!(ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("count"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("count", scriptValue6);
            }
        }
        return scriptContext.getClassOrVar("count");
    }

    public static ScriptValue _sawRecipeAt(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        List list = ScriptProgram.elementsOf((ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine.pg$146_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                builder.val("r", scriptValue3);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("r");
                ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("ins", scriptValue5);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue5);
                if (!(ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)scriptContext.getClassOrVar("target_idx"))) {
                    return scriptContext.getClassOrVar("r");
                }
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)1.0));
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
            var2_4 = ScriptProgram.elementsOf((ScriptValue)(var5_2 != ScriptValue.NULL ? ((var6_3 = PolyClassMachine.ofGuarded((ScriptValue)var5_2)) != null ? var6_3.pg$146_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)var5_2, (ScriptContext)var1_1)) : ScriptValue.NULL));
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
                if (!(ScriptFormula.callBuiltin((String)"size", var11_11, (ScriptContext)var1_1).asNum() > 0.0 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var8_8, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("item_id")) != false)) ** GOTO lbl-1000
                var12_12 = new ArrayList<ScriptValue>();
                var12_12.add(var10_10);
                if (ScriptFormula.callBuiltin((String)"size", var12_12, (ScriptContext)var1_1).asNum() > 0.0) {
                    v0 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v0 = false;
                }
                if (!(v0 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var10_10, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("out_id")) != false)) continue;
                return var1_1.getClassOrVar("r");
            }
        }
        var16_13 = new ArrayList<ScriptValue>();
        var16_13.add(var1_1.getClassOrVar("item_id"));
        var17_14 = var1_1.getClassOrVar("Registry");
        var13_16 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)(var17_14 != ScriptValue.NULL ? ((var18_15 = PolyClassRegistry.ofGuarded((ScriptValue)var17_14)) != null ? var18_15.pg$0_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)var17_14, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptContext)var1_1), var16_13, (ScriptContext)var1_1));
        if (var13_16 != null) {
            for (ScriptValue var15_18 : var13_16) {
                var0.val("r", var15_18);
                var19_19 = var1_1.getClassOrVar("r");
                var20_20 = var19_19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)var19_19, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("outs", var20_20);
                var21_21 = new ArrayList<ScriptValue>();
                var21_21.add(var20_20);
                if (!(ScriptFormula.callBuiltin((String)"size", var21_21, (ScriptContext)var1_1).asNum() > 0.0 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var20_20, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("out_id")) != false)) continue;
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
            ScriptValue scriptValue3 = ScriptValue.of((String)"");
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
                PolyClassMachine polyClassMachine;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                List list2 = ScriptProgram.elementsOf((ScriptValue)(scriptValue6 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine.pg$146_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL));
                if (list2 != null) {
                    for (ScriptValue scriptValue7 : list2) {
                        builder.val("r", scriptValue7);
                        ScriptValue scriptValue8 = scriptContext.getClassOrVar("r");
                        ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("ins", scriptValue9);
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue9);
                        if (!(ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                        ScriptValue scriptValue10 = scriptContext.getClassOrVar("r");
                        ScriptValue scriptValue11 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("outs", scriptValue11);
                        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                        arrayList2.add(scriptValue11);
                        if (!(ScriptFormula.callBuiltin((String)"size", arrayList2, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                    object3 = polyClassMachine.tm$34_get_typed(string, string3);
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object5);
                    v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string4, scriptValue16));
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
                    return PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext);
                }
            }
            return scriptContext.getClassOrVar("null");
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("item_id"));
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("Registry");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)(scriptValue20 != ScriptValue.NULL ? ((polyClassRegistry = PolyClassRegistry.ofGuarded((ScriptValue)scriptValue20)) != null ? polyClassRegistry.pg$0_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue20, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        builder.val("stone_candidates", (ScriptValue)callSite);
        if (scriptContext.getStr("filter_id").equals("") ^ true && (list = ScriptProgram.elementsOf((ScriptValue)callSite)) != null) {
            for (ScriptValue scriptValue21 : list) {
                builder.val("r", scriptValue21);
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("r");
                ScriptValue scriptValue23 = scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue22, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("outs", scriptValue23);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptValue23);
                if (!(ScriptFormula.callBuiltin((String)"size", arrayList3, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
                return scriptContext.getClassOrVar("filter_id");
            }
        }
        ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
        arrayList4.add(callSite);
        ScriptValue scriptValue24 = ScriptFormula.callBuiltin((String)"size", arrayList4, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(ScriptValue.of((String)string));
                    arrayList5.add(ScriptValue.of((String)string5));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue25, arrayList5, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(ScriptValue.of((String)string));
                    arrayList6.add(ScriptValue.of((String)string6));
                    arrayList6.add(scriptValue30);
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue29, arrayList6, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            CallSite callSite2 = PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)callSite, (ScriptValue)ScriptValue.of((double)d6)), (ScriptContext)scriptContext);
            builder.val("outs", (ScriptValue)callSite2);
            ArrayList<CallSite> arrayList7 = new ArrayList<CallSite>();
            arrayList7.add(callSite2);
            if (ScriptFormula.callBuiltin((String)"size", arrayList7, (ScriptContext)scriptContext).asNum() > 0.0) {
                return PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)callSite2, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext);
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
                ScriptValue scriptValue4 = ScriptValue.of((String)"");
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string2, scriptValue4));
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
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object);
                    v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue6));
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
        PolyClassMachine polyClassMachine;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"facing"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("facing", (ScriptValue)callSite);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "_saw_rpm_sign";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine2 = new PolyClassMachine(object2);
                object = polyClassMachine2.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)string));
                arrayList2.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("sign", scriptValue3);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("null")) || ScriptFormula.valuesEqual((ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((double)0.0))) {
            double d = 1.0;
            ScriptValue scriptValue4 = ScriptValue.of((double)1.0);
            builder.val("sign", scriptValue4);
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)callSite, (String)"north") || ScriptFormula.valuesEqualStr((ScriptValue)callSite, (String)"south")) {
            return scriptContext.getNum("sign") > 0.0 ? ScriptValue.of((String)"east") : ScriptValue.of((String)"west");
        }
        return scriptContext.getNum("sign") > 0.0 ? ScriptValue.of((String)"south") : ScriptValue.of((String)"north");
    }

    public static ScriptValue _sawInputFace(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = Saw._sawOutputFace(builder2);
        builder.val("face", scriptValue);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"east")) {
            return ScriptValue.of((String)"west");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"west")) {
            return ScriptValue.of((String)"east");
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue, (String)"north")) {
            return ScriptValue.of((String)"south");
        }
        return ScriptValue.of((String)"north");
    }

    public static ScriptValue _sawUpdateRpmSign(ScriptContext.Builder builder) {
        block4: {
            ScriptContext scriptContext = builder.peek();
            if (!(ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("effective_rpm"), (ScriptValue)ScriptValue.of((double)0.0)) ^ true)) break block4;
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
        PolyClassMachine polyClassMachine;
        PolyClassMachine polyClassMachine2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"face"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine2.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("face", (ScriptValue)callSite);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(ScriptValue.of((String)"facing"));
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList2, (ScriptContext)scriptContext);
        builder.val("facing", (ScriptValue)callSite2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)callSite, (String)"wall")) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "back";
                if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine3 = new PolyClassMachine(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine3.tm$40_set_rpm_input(string));
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((String)string));
                    v0 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue3, arrayList3, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "";
                if (scriptValue4 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine4 = new PolyClassMachine(object);
                    v1 = ScriptValue.of((boolean)polyClassMachine4.tm$44_set_rpm_output_same(string));
                } else {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue4, arrayList4, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        } else if (ScriptFormula.valuesEqualStr((ScriptValue)callSite2, (String)"north") || ScriptFormula.valuesEqualStr((ScriptValue)callSite2, (String)"south")) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "north,south";
                if (scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine5 = new PolyClassMachine(object);
                    v2 = ScriptValue.of((boolean)polyClassMachine5.tm$40_set_rpm_input(string));
                } else {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(ScriptValue.of((String)string));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue5, arrayList5, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "north,south";
                if (scriptValue6 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine6 = new PolyClassMachine(object);
                    v3 = ScriptValue.of((boolean)polyClassMachine6.tm$44_set_rpm_output_same(string));
                } else {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(ScriptValue.of((String)string));
                    v3 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue6, arrayList6, (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "east,west";
                if (scriptValue7 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine7 = new PolyClassMachine(object);
                    v4 = ScriptValue.of((boolean)polyClassMachine7.tm$40_set_rpm_input(string));
                } else {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add(ScriptValue.of((String)string));
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_input", (ScriptValue)scriptValue7, arrayList7, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "east,west";
                if (scriptValue8 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine8 = new PolyClassMachine(object);
                    v5 = ScriptValue.of((boolean)polyClassMachine8.tm$44_set_rpm_output_same(string));
                } else {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(ScriptValue.of((String)string));
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_rpm_output_same", (ScriptValue)scriptValue8, arrayList8, (ScriptContext)scriptContext);
                }
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
                PolyClassMachine polyClassMachine;
                scriptContext = builder.peek();
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("item"));
                if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
                Object object = scriptValue != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine.pg$190_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
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
                arrayList3.add(ScriptValue.of((double)(ScriptFormula.subscriptGet((ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)0.0)).asNum() * 0.08)));
                arrayList3.add(ScriptValue.of((double)0.05));
                arrayList3.add(ScriptValue.of((double)(ScriptFormula.subscriptGet((ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((double)2.0)).asNum() * 0.08)));
                v1 = scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine(object).um$19_drop_item_toward(arrayList3) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
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
        block185: {
            block183: {
                block184: {
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
                    var20_16 = ScriptValue.of((String)((var14_10 != ScriptValue.NULL ? ((var15_11 = PolyClassMachine.ofGuarded((ScriptValue)var14_10)) != null ? var15_11.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var14_10, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "," + (var16_12 != ScriptValue.NULL ? ((var17_13 = PolyClassMachine.ofGuarded((ScriptValue)var16_12)) != null ? var17_13.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var16_12, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr() + "," + (var18_14 != ScriptValue.NULL ? ((var19_15 = PolyClassMachine.ofGuarded((ScriptValue)var18_14)) != null ? var19_15.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var18_14, (ScriptContext)var1_1)) : ScriptValue.NULL).asStr()));
                    var0.val("_hold_key", var20_16);
                    var21_17 = var1_1.getClassOrVar("Machine");
                    var23_19 = var21_17 != ScriptValue.NULL ? ((var22_18 = PolyClassMachine.ofGuarded((ScriptValue)var21_17)) != null ? var22_18.pg$190_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var21_17, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var0.val("contraption", var23_19);
                    var24_20 = ScriptContext.builder().copyFrom(var1_1);
                    var24_20.val("effective_rpm", (ScriptValue)(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var25_21 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var25_21, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm")));
                    Saw._sawUpdateRpmSign(var24_20);
                    var26_22 = 0.0;
                    var28_23 = ScriptValue.of((double)0.0);
                    var0.val("is_now", var28_23);
                    var29_24 = new ArrayList<ScriptValue>();
                    var29_24.add(ScriptValue.of((String)"face"));
                    var30_25 = var1_1.getClassOrVar("Machine");
                    var32_27 = ScriptFormula.valuesEqualStr((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(var30_25 != ScriptValue.NULL ? ((var31_26 = PolyClassMachine.ofGuarded((ScriptValue)var30_25)) != null ? var31_26.pg$139_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)var30_25, (ScriptContext)var1_1)) : ScriptValue.NULL), var29_24, (ScriptContext)var1_1), (String)"floor");
                    var33_28 = ScriptValue.of((boolean)var32_27);
                    var0.val("is_belt_facing", var33_28);
                    if (!(ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block183;
                    var34_29 = var1_1.getClassOrVar("contraption");
                    var35_30 = ScriptFormula.valuesEqual((ScriptValue)(var34_29 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var34_29, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.0)) ^ true;
                    var36_31 = ScriptValue.of((boolean)var35_30);
                    var0.val("is_rotational", var36_31);
                    var37_32 = var1_1.getClassOrVar("contraption");
                    if (var37_32 != ScriptValue.NULL) {
                        var38_33 = new ArrayList<E>();
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)var37_32, var38_33, (ScriptContext)var1_1);
                    } else {
                        v4 /* !! */  = ScriptValue.NULL;
                    }
                    var39_34 = v4 /* !! */ ;
                    var0.val("is_linear", var39_34);
                    var40_35 = (var35_30 != false || var39_34.asBool() != false) != false ? 1.0 : 0.0;
                    var42_36 = ScriptValue.of((double)var40_35);
                    var0.val("is_now", var42_36);
                    if (!(var40_35 > 0.0)) break block184;
                    if (var35_30) {
                        var43_37 = 10.0;
                        v5 = ScriptValue.of((double)Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(((var45_38 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var45_38, (ScriptContext)var1_1) : ScriptValue.NULL).asNum()) / var43_37));
                    } else {
                        var46_39 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                        var46_39.val("contraption", var23_19);
                        v5 = Utils.1._linearBreakSpeed((ScriptContext.Builder)var46_39);
                    }
                    var47_40 = v5;
                    var0.val("speed", var47_40);
                    var48_41 = var1_1.getClassOrVar("Machine");
                    var50_43 = var1_1.getClassOrVar("Machine");
                    var52_45 = ScriptFormula.addPolymorphic((ScriptValue)(var48_41 != ScriptValue.NULL ? ((var49_42 = PolyClassMachine.ofGuarded((ScriptValue)var48_41)) != null ? var49_42.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var48_41, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var50_43 != ScriptValue.NULL ? ((var51_44 = PolyClassMachine.ofGuarded((ScriptValue)var50_43)) != null ? var51_44.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var50_43, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("tx", var52_45);
                    var53_46 = var1_1.getClassOrVar("Machine");
                    var55_48 = var1_1.getClassOrVar("Machine");
                    var57_50 = ScriptFormula.addPolymorphic((ScriptValue)(var53_46 != ScriptValue.NULL ? ((var54_47 = PolyClassMachine.ofGuarded((ScriptValue)var53_46)) != null ? var54_47.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var53_46, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var55_48 != ScriptValue.NULL ? ((var56_49 = PolyClassMachine.ofGuarded((ScriptValue)var55_48)) != null ? var56_49.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var55_48, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("ty", var57_50);
                    var58_51 = var1_1.getClassOrVar("Machine");
                    var60_53 = var1_1.getClassOrVar("Machine");
                    var62_55 = ScriptFormula.addPolymorphic((ScriptValue)(var58_51 != ScriptValue.NULL ? ((var59_52 = PolyClassMachine.ofGuarded((ScriptValue)var58_51)) != null ? var59_52.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var58_51, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var60_53 != ScriptValue.NULL ? ((var61_54 = PolyClassMachine.ofGuarded((ScriptValue)var60_53)) != null ? var61_54.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var60_53, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("tz", var62_55);
                    var63_56 = new ArrayList<ScriptValue>();
                    var63_56.add(var52_45);
                    var63_56.add(var57_50);
                    var63_56.add(var62_55);
                    var64_57 = var1_1.getClassOrVar("contraption");
                    var65_58 = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(var64_57 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)var64_57, (ScriptContext)var1_1) : ScriptValue.NULL), var63_56, (ScriptContext)var1_1);
                    var0.val("target", (ScriptValue)var65_58);
                    if ((var32_27 != false || ScriptFormula.valuesEqual((ScriptValue)var65_58, (ScriptValue)var1_1.getClassOrVar("null")) != false) != false || ((var66_59 = var1_1.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var66_59, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
                    var67_60 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var68_61 = var1_1.getClassOrVar("target");
                    var67_60.val("id", (ScriptValue)(var68_61 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var68_61, (ScriptContext)var1_1) : ScriptValue.NULL));
                    if (!(TreeUtils._isChoppable((ScriptContext.Builder)var67_60).asBool() ^ true)) {
                        v6 = false;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v6 = true;
                    }
                    if (v6) {
                        var69_62 = var1_1.getClassOrVar("contraption");
                        if (var69_62 != ScriptValue.NULL) {
                            var70_63 = new ArrayList<ScriptValue>();
                            var70_63.add(var20_16);
                            v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var69_62, var70_63, (ScriptContext)var1_1);
                        } else {
                            v7 /* !! */  = ScriptValue.NULL;
                        }
                    } else {
                        var71_64 = var1_1.getClassOrVar("contraption");
                        if (var71_64 != ScriptValue.NULL) {
                            var72_65 = new ArrayList<ScriptValue>();
                            var72_65.add(var20_16);
                            v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var71_64, var72_65, (ScriptContext)var1_1);
                        } else {
                            v8 /* !! */  = ScriptValue.NULL;
                        }
                        var73_66 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                        var74_67 = var1_1.getClassOrVar("target");
                        var73_66.val("id", (ScriptValue)(var74_67 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var74_67, (ScriptContext)var1_1) : ScriptValue.NULL));
                        if (TreeUtils._isLog((ScriptContext.Builder)var73_66).asBool()) {
                            var75_68 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                            var75_68.val("contraption", var23_19);
                            var75_68.val("base_x", var52_45);
                            var75_68.val("base_y", var57_50);
                            var75_68.val("base_z", var62_55);
                            var75_68.val("speed", var47_40);
                            TreeUtils._fellTree((ScriptContext.Builder)var75_68);
                        } else {
                            var76_69 = var1_1.getClassOrVar("Machine");
                            if (var76_69 != ScriptValue.NULL) {
                                var77_70 = var65_58;
                                var78_71 = var47_40;
                                if (var76_69 instanceof ScriptValue.Obj && (var80_73 = (var79_72 = (ScriptValue.Obj)var76_69).instance()) != null && !(var80_73 instanceof PolyClass) && var79_72.typeName().equals("Machine")) {
                                    var81_74 = new PolyClassMachine(var80_73);
                                    v9 /* !! */  = var81_74.tm$2_tick_break((ScriptValue)var77_70, var78_71.asNum());
                                } else {
                                    var82_75 = new ArrayList<CallSite>();
                                    var82_75.add(var77_70);
                                    var82_75.add((CallSite)var78_71);
                                    v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var76_69, var82_75, (ScriptContext)var1_1);
                                }
                            } else {
                                v9 /* !! */  = ScriptValue.NULL;
                            }
                            var83_76 = v9 /* !! */ ;
                            var0.val("result", var83_76);
                            if (ScriptFormula.valuesEqual((ScriptValue)var83_76, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var84_77 = ScriptProgram.elementsOf((ScriptValue)var83_76)) != null) {
                                for (ScriptValue var86_79 : var84_77) {
                                    var0.val("item", var86_79);
                                    var87_80 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                                    var87_80.val("item", var1_1.getClassOrVar("item"));
                                    Utils.1._deposit((ScriptContext.Builder)var87_80);
                                }
                            }
                        }
                        var88_81 = var1_1.getClassOrVar("Machine");
                        if (var88_81 != ScriptValue.NULL) {
                            if (var35_30) {
                                var90_82 = ScriptContext.builder().copyFrom(var1_1);
                                var91_83 = var1_1.getClassOrVar("contraption");
                                var90_82.val("current_rpm", (ScriptValue)(var91_83 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var91_83, (ScriptContext)var1_1) : ScriptValue.NULL));
                                v10 = Saw._sawSuCost(var90_82);
                            } else {
                                v10 = var89_84 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)6.0), (ScriptValue)var47_40);
                            }
                            if (var88_81 instanceof ScriptValue.Obj && (var93_86 = (var92_85 = (ScriptValue.Obj)var88_81).instance()) != null && !(var93_86 instanceof PolyClass) && var92_85.typeName().equals("Machine")) {
                                var94_87 = new PolyClassMachine(var93_86);
                                v11 /* !! */  = ScriptValue.of((boolean)var94_87.tm$56_report_su(var89_84.asNum()));
                            } else {
                                var95_88 = new ArrayList<ScriptValue>();
                                var95_88.add(var89_84);
                                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var88_81, var95_88, (ScriptContext)var1_1);
                            }
                        } else {
                            v11 /* !! */  = ScriptValue.NULL;
                        }
                    }
                    break block185;
                }
                var96_89 = var1_1.getClassOrVar("contraption");
                if (var96_89 != ScriptValue.NULL) {
                    var97_90 = new ArrayList<ScriptValue>();
                    var97_90.add(var20_16);
                    v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var96_89, var97_90, (ScriptContext)var1_1);
                } else {
                    v12 /* !! */  = ScriptValue.NULL;
                }
                break block185;
            }
            if (!(ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)ScriptValue.of((double)0.0)) ^ true)) break block185;
            var98_91 = 10.0;
            var100_92 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(var1_1.getNum("rpm")) / var98_91);
            var102_93 = ScriptValue.of((double)var100_92);
            var0.val("speed", var102_93);
            var103_94 = var1_1.getClassOrVar("Machine");
            var105_96 = var103_94 != ScriptValue.NULL ? ((var104_95 = PolyClassMachine.ofGuarded((ScriptValue)var103_94)) != null ? var104_95.pg$137_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var103_94, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("fb", var105_96);
            if (!((var32_27 ^ true) != false && (((var106_97 = var1_1.getClassOrVar("fb")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var106_97, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) ** GOTO lbl-1000
            var107_98 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
            var108_99 = var1_1.getClassOrVar("fb");
            var107_98.val("id", (ScriptValue)(var108_99 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var108_99, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (TreeUtils._isChoppable((ScriptContext.Builder)var107_98).asBool()) {
                v13 = true;
            } else lbl-1000:
            // 2 sources

            {
                v13 = false;
            }
            if (v13) {
                var109_100 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                var110_101 = var1_1.getClassOrVar("fb");
                var109_100.val("id", (ScriptValue)(var110_101 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var110_101, (ScriptContext)var1_1) : ScriptValue.NULL));
                if (TreeUtils._isLog((ScriptContext.Builder)var109_100).asBool()) {
                    var111_102 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var111_102.val("contraption", var1_1.getClassOrVar("null"));
                    var112_103 = var1_1.getClassOrVar("Machine");
                    var114_105 = var1_1.getClassOrVar("Machine");
                    var111_102.val("base_x", ScriptFormula.addPolymorphic((ScriptValue)(var112_103 != ScriptValue.NULL ? ((var113_104 = PolyClassMachine.ofGuarded((ScriptValue)var112_103)) != null ? var113_104.pg$199_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var112_103, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var114_105 != ScriptValue.NULL ? ((var115_106 = PolyClassMachine.ofGuarded((ScriptValue)var114_105)) != null ? var115_106.pg$133_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var114_105, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var116_107 = var1_1.getClassOrVar("Machine");
                    var118_109 = var1_1.getClassOrVar("Machine");
                    var111_102.val("base_y", ScriptFormula.addPolymorphic((ScriptValue)(var116_107 != ScriptValue.NULL ? ((var117_108 = PolyClassMachine.ofGuarded((ScriptValue)var116_107)) != null ? var117_108.pg$201_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var116_107, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var118_109 != ScriptValue.NULL ? ((var119_110 = PolyClassMachine.ofGuarded((ScriptValue)var118_109)) != null ? var119_110.pg$129_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var118_109, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var120_111 = var1_1.getClassOrVar("Machine");
                    var122_113 = var1_1.getClassOrVar("Machine");
                    var111_102.val("base_z", ScriptFormula.addPolymorphic((ScriptValue)(var120_111 != ScriptValue.NULL ? ((var121_112 = PolyClassMachine.ofGuarded((ScriptValue)var120_111)) != null ? var121_112.pg$205_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var120_111, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var122_113 != ScriptValue.NULL ? ((var123_114 = PolyClassMachine.ofGuarded((ScriptValue)var122_113)) != null ? var123_114.pg$131_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var122_113, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var111_102.val("speed", ScriptValue.of((double)var100_92));
                    TreeUtils._fellTree((ScriptContext.Builder)var111_102);
                } else {
                    var124_115 = var1_1.getClassOrVar("Machine");
                    if (var124_115 != ScriptValue.NULL) {
                        var125_116 = var105_96;
                        var126_117 = var100_92;
                        if (var124_115 instanceof ScriptValue.Obj && (var129_119 = (var128_118 = (ScriptValue.Obj)var124_115).instance()) != null && !(var129_119 instanceof PolyClass) && var128_118.typeName().equals("Machine")) {
                            var130_120 = new PolyClassMachine(var129_119);
                            v14 /* !! */  = var130_120.tm$2_tick_break(var125_116, var126_117);
                        } else {
                            var131_121 = new ArrayList<ScriptValue>();
                            var131_121.add(var125_116);
                            var131_121.add(ScriptValue.of((double)var126_117));
                            v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var124_115, var131_121, (ScriptContext)var1_1);
                        }
                    } else {
                        v14 /* !! */  = ScriptValue.NULL;
                    }
                    var132_122 = v14 /* !! */ ;
                    var0.val("result", var132_122);
                    if (ScriptFormula.valuesEqual((ScriptValue)var132_122, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var133_123 = ScriptProgram.elementsOf((ScriptValue)var132_122)) != null) {
                        for (ScriptValue var135_125 : var133_123) {
                            var0.val("item", var135_125);
                            var136_126 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var136_126.val("item", var1_1.getClassOrVar("item"));
                            Utils.1._deposit((ScriptContext.Builder)var136_126);
                        }
                    }
                }
                var137_127 = var1_1.getClassOrVar("Machine");
                if (var137_127 != ScriptValue.NULL) {
                    var139_128 = ScriptContext.builder().copyFrom(var1_1);
                    var139_128.val("current_rpm", var1_1.getClassOrVar("rpm"));
                    var138_129 = Saw._sawSuCost(var139_128);
                    if (var137_127 instanceof ScriptValue.Obj && (var141_131 = (var140_130 = (ScriptValue.Obj)var137_127).instance()) != null && !(var141_131 instanceof PolyClass) && var140_130.typeName().equals("Machine")) {
                        var142_132 = new PolyClassMachine(var141_131);
                        v15 /* !! */  = ScriptValue.of((boolean)var142_132.tm$56_report_su(var138_129.asNum()));
                    } else {
                        var143_133 = new ArrayList<ScriptValue>();
                        var143_133.add(var138_129);
                        v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var137_127, var143_133, (ScriptContext)var1_1);
                    }
                } else {
                    v15 /* !! */  = ScriptValue.NULL;
                }
            }
            var144_134 = 1.0;
            var146_135 = ScriptValue.of((double)1.0);
            var0.val("is_now", var146_135);
        }
        if (var1_1.getNum("is_now") > 0.0 != false && var32_27 != false) {
            var147_136 = var1_1.getClassOrVar("Machine");
            var149_138 = var147_136 != ScriptValue.NULL ? ((var148_137 = PolyClassMachine.ofGuarded((ScriptValue)var147_136)) != null ? var148_137.pg$157_belt() : PolyDispatch.bootstrapGet("memberGet", "belt", (ScriptValue)var147_136, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("belt", var149_138);
            var151_140 = ScriptFormula.valuesEqual((ScriptValue)var23_19, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var150_139 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var150_139, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm");
            var0.val("belt_rpm", var151_140);
            var152_141 = var1_1.getClassOrVar("Machine");
            if (var152_141 != ScriptValue.NULL) {
                var153_142 = "_saw_item_id";
                var154_143 = "str";
                if (var152_141 instanceof ScriptValue.Obj && (var156_145 = (var155_144 = (ScriptValue.Obj)var152_141).instance()) != null && !(var156_145 instanceof PolyClass) && var155_144.typeName().equals("Machine")) {
                    var157_146 = new PolyClassMachine(var156_145);
                    v16 /* !! */  = var157_146.tm$34_get_typed(var153_142, var154_143);
                } else {
                    var158_147 = new ArrayList<ScriptValue>();
                    var158_147.add(ScriptValue.of((String)var153_142));
                    var158_147.add(ScriptValue.of((String)var154_143));
                    v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var152_141, var158_147, (ScriptContext)var1_1);
                }
            } else {
                v16 /* !! */  = ScriptValue.NULL;
            }
            var159_148 = v16 /* !! */ ;
            var0.val("active_id", var159_148);
            if (ScriptFormula.valuesEqual((ScriptValue)var159_148, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var160_149 = ScriptValue.of((String)"");
                var0.val("active_id", var160_149);
            }
            if (var1_1.getStr("active_id").equals("")) {
                var161_150 = var1_1.getClassOrVar("belt");
                if ((var161_150 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var161_150, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
                    var162_151 = var1_1.getClassOrVar("belt");
                    if (var162_151 != ScriptValue.NULL) {
                        var163_152 = new ArrayList<E>();
                        v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var162_151, var163_152, (ScriptContext)var1_1);
                    } else {
                        v17 /* !! */  = ScriptValue.NULL;
                    }
                    var164_153 = v17 /* !! */ ;
                    var0.val("carried", var164_153);
                    var165_154 = ScriptContext.builder().copyFrom(var1_1);
                    var166_155 = var1_1.getClassOrVar("carried");
                    var165_154.val("item_id", (ScriptValue)(var166_155 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var166_155, (ScriptContext)var1_1) : ScriptValue.NULL));
                    var167_156 = Saw._sawOutputFor(var165_154);
                    var0.val("out_id", var167_156);
                    var168_157 = new ArrayList<ScriptValue>();
                    var169_158 = var1_1.getClassOrVar("carried");
                    var168_157.add(ScriptValue.of((String)("[saw-debug] own-belt has_item id=" + (var169_158 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var169_158, (ScriptContext)var1_1) : ScriptValue.NULL).asStr() + " out_id=" + (ScriptFormula.valuesEqual((ScriptValue)var167_156, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ScriptValue.of((String)"null") : var167_156).asStr())));
                    ScriptFormula.callBuiltin((String)"print", var168_157, (ScriptContext)var1_1);
                    if (ScriptFormula.valuesEqual((ScriptValue)var167_156, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var170_159 = var1_1.getClassOrVar("belt");
                        if (var170_159 != ScriptValue.NULL) {
                            var171_160 = new ArrayList<E>();
                            v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var170_159, var171_160, (ScriptContext)var1_1);
                        } else {
                            v18 /* !! */  = ScriptValue.NULL;
                        }
                        var172_161 = v18 /* !! */ ;
                        var0.val("taken", var172_161);
                        var173_162 = var1_1.getClassOrVar("Machine");
                        if (var173_162 != ScriptValue.NULL) {
                            var174_163 = "_saw_item_id";
                            var175_164 = "str";
                            var177_165 = var1_1.getClassOrVar("taken");
                            v19 /* !! */  = var176_166 = var177_165 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var177_165, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var173_162 instanceof ScriptValue.Obj && (var179_168 = (var178_167 = (ScriptValue.Obj)var173_162).instance()) != null && !(var179_168 instanceof PolyClass) && var178_167.typeName().equals("Machine")) {
                                var180_169 = new PolyClassMachine(var179_168);
                                v20 /* !! */  = ScriptValue.of((boolean)var180_169.tm$82_set_typed(var174_163, var175_164, var176_166));
                            } else {
                                var181_170 = new ArrayList<ScriptValue>();
                                var181_170.add(ScriptValue.of((String)var174_163));
                                var181_170.add(ScriptValue.of((String)var175_164));
                                var181_170.add(var176_166);
                                v20 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var173_162, var181_170, (ScriptContext)var1_1);
                            }
                        } else {
                            v20 /* !! */  = ScriptValue.NULL;
                        }
                        var182_171 = var1_1.getClassOrVar("Machine");
                        if (var182_171 != ScriptValue.NULL) {
                            var183_172 = "_saw_out_id";
                            var184_173 = "str";
                            var185_174 = var167_156;
                            if (var182_171 instanceof ScriptValue.Obj && (var187_176 = (var186_175 = (ScriptValue.Obj)var182_171).instance()) != null && !(var187_176 instanceof PolyClass) && var186_175.typeName().equals("Machine")) {
                                var188_177 = new PolyClassMachine(var187_176);
                                v21 /* !! */  = ScriptValue.of((boolean)var188_177.tm$82_set_typed(var183_172, var184_173, var185_174));
                            } else {
                                var189_178 = new ArrayList<ScriptValue>();
                                var189_178.add(ScriptValue.of((String)var183_172));
                                var189_178.add(ScriptValue.of((String)var184_173));
                                var189_178.add(var185_174);
                                v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var182_171, var189_178, (ScriptContext)var1_1);
                            }
                        } else {
                            v21 /* !! */  = ScriptValue.NULL;
                        }
                        var190_179 = var1_1.getClassOrVar("Machine");
                        if (var190_179 != ScriptValue.NULL) {
                            var191_180 = "_saw_count";
                            var192_181 = "int";
                            var194_182 = var1_1.getClassOrVar("taken");
                            v22 /* !! */  = var193_183 = var194_182 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var194_182, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var190_179 instanceof ScriptValue.Obj && (var196_185 = (var195_184 = (ScriptValue.Obj)var190_179).instance()) != null && !(var196_185 instanceof PolyClass) && var195_184.typeName().equals("Machine")) {
                                var197_186 = new PolyClassMachine(var196_185);
                                v23 /* !! */  = ScriptValue.of((boolean)var197_186.tm$82_set_typed(var191_180, var192_181, var193_183));
                            } else {
                                var198_187 = new ArrayList<ScriptValue>();
                                var198_187.add(ScriptValue.of((String)var191_180));
                                var198_187.add(ScriptValue.of((String)var192_181));
                                var198_187.add(var193_183);
                                v23 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var190_179, var198_187, (ScriptContext)var1_1);
                            }
                        } else {
                            v23 /* !! */  = ScriptValue.NULL;
                        }
                        var199_188 = var1_1.getClassOrVar("Machine");
                        if (var199_188 != ScriptValue.NULL) {
                            var200_189 = "_saw_progress";
                            var201_190 = "int";
                            var202_191 = ScriptValue.of((double)0.0);
                            if (var199_188 instanceof ScriptValue.Obj && (var204_193 = (var203_192 = (ScriptValue.Obj)var199_188).instance()) != null && !(var204_193 instanceof PolyClass) && var203_192.typeName().equals("Machine")) {
                                var205_194 = new PolyClassMachine(var204_193);
                                v24 /* !! */  = ScriptValue.of((boolean)var205_194.tm$82_set_typed(var200_189, var201_190, var202_191));
                            } else {
                                var206_195 = new ArrayList<ScriptValue>();
                                var206_195.add(ScriptValue.of((String)var200_189));
                                var206_195.add(ScriptValue.of((String)var201_190));
                                var206_195.add(var202_191);
                                v24 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var199_188, var206_195, (ScriptContext)var1_1);
                            }
                        } else {
                            v24 /* !! */  = ScriptValue.NULL;
                        }
                        var207_196 = var1_1.getClassOrVar("Machine");
                        if (var207_196 != ScriptValue.NULL) {
                            var208_197 = "_saw_from_ground";
                            var209_198 = "int";
                            var210_199 = ScriptValue.of((double)0.0);
                            if (var207_196 instanceof ScriptValue.Obj && (var212_201 = (var211_200 = (ScriptValue.Obj)var207_196).instance()) != null && !(var212_201 instanceof PolyClass) && var211_200.typeName().equals("Machine")) {
                                var213_202 = new PolyClassMachine(var212_201);
                                v25 /* !! */  = ScriptValue.of((boolean)var213_202.tm$82_set_typed(var208_197, var209_198, var210_199));
                            } else {
                                var214_203 = new ArrayList<ScriptValue>();
                                var214_203.add(ScriptValue.of((String)var208_197));
                                var214_203.add(ScriptValue.of((String)var209_198));
                                var214_203.add(var210_199);
                                v25 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var207_196, var214_203, (ScriptContext)var1_1);
                            }
                        } else {
                            v25 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var215_204 = ScriptContext.builder().copyFrom(var1_1);
                    var216_205 = Saw._sawInputFace(var215_204);
                    var0.val("in_face", var216_205);
                    var217_206 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                    var217_206.val("face", var216_205);
                    var217_206.val("validator", var1_1.getClassOrVar("null"));
                    var217_206.val("amount", var1_1.getClassOrVar("null"));
                    var218_207 = BeltUtils.beltTake((ScriptContext.Builder)var217_206);
                    var0.val("taken", var218_207);
                    var219_208 = new ArrayList<ScriptValue>();
                    var219_208.add(var218_207);
                    if (ScriptFormula.callBuiltin((String)"is_empty", var219_208, (ScriptContext)var1_1).asBool() ^ true) {
                        var220_209 = ScriptContext.builder().copyFrom(var1_1);
                        var221_210 = var1_1.getClassOrVar("taken");
                        var220_209.val("item_id", (ScriptValue)(var221_210 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var221_210, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var222_211 = Saw._sawOutputFor(var220_209);
                        var0.val("out_id", var222_211);
                        if (ScriptFormula.valuesEqual((ScriptValue)var222_211, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var223_212 = var1_1.getClassOrVar("Machine");
                            if (var223_212 != ScriptValue.NULL) {
                                var224_213 = "_saw_item_id";
                                var225_214 = "str";
                                var227_215 = var1_1.getClassOrVar("taken");
                                v26 /* !! */  = var226_216 = var227_215 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var227_215, (ScriptContext)var1_1) : ScriptValue.NULL;
                                if (var223_212 instanceof ScriptValue.Obj && (var229_218 = (var228_217 = (ScriptValue.Obj)var223_212).instance()) != null && !(var229_218 instanceof PolyClass) && var228_217.typeName().equals("Machine")) {
                                    var230_219 = new PolyClassMachine(var229_218);
                                    v27 /* !! */  = ScriptValue.of((boolean)var230_219.tm$82_set_typed(var224_213, var225_214, var226_216));
                                } else {
                                    var231_220 = new ArrayList<ScriptValue>();
                                    var231_220.add(ScriptValue.of((String)var224_213));
                                    var231_220.add(ScriptValue.of((String)var225_214));
                                    var231_220.add(var226_216);
                                    v27 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var223_212, var231_220, (ScriptContext)var1_1);
                                }
                            } else {
                                v27 /* !! */  = ScriptValue.NULL;
                            }
                            var232_221 = var1_1.getClassOrVar("Machine");
                            if (var232_221 != ScriptValue.NULL) {
                                var233_222 = "_saw_out_id";
                                var234_223 = "str";
                                var235_224 = var222_211;
                                if (var232_221 instanceof ScriptValue.Obj && (var237_226 = (var236_225 = (ScriptValue.Obj)var232_221).instance()) != null && !(var237_226 instanceof PolyClass) && var236_225.typeName().equals("Machine")) {
                                    var238_227 = new PolyClassMachine(var237_226);
                                    v28 /* !! */  = ScriptValue.of((boolean)var238_227.tm$82_set_typed(var233_222, var234_223, var235_224));
                                } else {
                                    var239_228 = new ArrayList<ScriptValue>();
                                    var239_228.add(ScriptValue.of((String)var233_222));
                                    var239_228.add(ScriptValue.of((String)var234_223));
                                    var239_228.add(var235_224);
                                    v28 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var232_221, var239_228, (ScriptContext)var1_1);
                                }
                            } else {
                                v28 /* !! */  = ScriptValue.NULL;
                            }
                            var240_229 = var1_1.getClassOrVar("Machine");
                            if (var240_229 != ScriptValue.NULL) {
                                var241_230 = "_saw_count";
                                var242_231 = "int";
                                var244_232 = new ArrayList<ScriptValue>();
                                var244_232.add(var218_207);
                                var243_233 = ScriptFormula.callBuiltin((String)"item_count", var244_232, (ScriptContext)var1_1);
                                if (var240_229 instanceof ScriptValue.Obj && (var246_235 = (var245_234 = (ScriptValue.Obj)var240_229).instance()) != null && !(var246_235 instanceof PolyClass) && var245_234.typeName().equals("Machine")) {
                                    var247_236 = new PolyClassMachine(var246_235);
                                    v29 /* !! */  = ScriptValue.of((boolean)var247_236.tm$82_set_typed(var241_230, var242_231, var243_233));
                                } else {
                                    var248_237 = new ArrayList<ScriptValue>();
                                    var248_237.add(ScriptValue.of((String)var241_230));
                                    var248_237.add(ScriptValue.of((String)var242_231));
                                    var248_237.add(var243_233);
                                    v29 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var240_229, var248_237, (ScriptContext)var1_1);
                                }
                            } else {
                                v29 /* !! */  = ScriptValue.NULL;
                            }
                            var249_238 = var1_1.getClassOrVar("Machine");
                            if (var249_238 != ScriptValue.NULL) {
                                var250_239 = "_saw_progress";
                                var251_240 = "int";
                                var252_241 = ScriptValue.of((double)0.0);
                                if (var249_238 instanceof ScriptValue.Obj && (var254_243 = (var253_242 = (ScriptValue.Obj)var249_238).instance()) != null && !(var254_243 instanceof PolyClass) && var253_242.typeName().equals("Machine")) {
                                    var255_244 = new PolyClassMachine(var254_243);
                                    v30 /* !! */  = ScriptValue.of((boolean)var255_244.tm$82_set_typed(var250_239, var251_240, var252_241));
                                } else {
                                    var256_245 = new ArrayList<ScriptValue>();
                                    var256_245.add(ScriptValue.of((String)var250_239));
                                    var256_245.add(ScriptValue.of((String)var251_240));
                                    var256_245.add(var252_241);
                                    v30 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var249_238, var256_245, (ScriptContext)var1_1);
                                }
                            } else {
                                v30 /* !! */  = ScriptValue.NULL;
                            }
                            var257_246 = var1_1.getClassOrVar("Machine");
                            if (var257_246 != ScriptValue.NULL) {
                                var258_247 = "_saw_from_ground";
                                var259_248 = "int";
                                var260_249 = ScriptValue.of((double)2.0);
                                if (var257_246 instanceof ScriptValue.Obj && (var262_251 = (var261_250 = (ScriptValue.Obj)var257_246).instance()) != null && !(var262_251 instanceof PolyClass) && var261_250.typeName().equals("Machine")) {
                                    var263_252 = new PolyClassMachine(var262_251);
                                    v31 /* !! */  = ScriptValue.of((boolean)var263_252.tm$82_set_typed(var258_247, var259_248, var260_249));
                                } else {
                                    var264_253 = new ArrayList<ScriptValue>();
                                    var264_253.add(ScriptValue.of((String)var258_247));
                                    var264_253.add(ScriptValue.of((String)var259_248));
                                    var264_253.add(var260_249);
                                    v31 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var257_246, var264_253, (ScriptContext)var1_1);
                                }
                            } else {
                                v31 /* !! */  = ScriptValue.NULL;
                            }
                        } else {
                            var265_254 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                            var265_254.val("face", var216_205);
                            var265_254.val("item", var218_207);
                            BeltUtils.beltGive((ScriptContext.Builder)var265_254);
                        }
                    } else {
                        var269_255 = var1_1.getClassOrVar("Machine");
                        if (var269_255 != ScriptValue.NULL) {
                            var270_256 = 0.7;
                            if (var269_255 instanceof ScriptValue.Obj && (var273_258 = (var272_257 = (ScriptValue.Obj)var269_255).instance()) != null && !(var273_258 instanceof PolyClass) && var272_257.typeName().equals("Machine")) {
                                var274_259 = new PolyClassMachine(var273_258);
                                v32 /* !! */  = var274_259.tm$94_nearby_entities(var270_256);
                            } else {
                                var275_260 = new ArrayList<ScriptValue>();
                                var275_260.add(ScriptValue.of((double)var270_256));
                                v32 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var269_255, var275_260, (ScriptContext)var1_1);
                            }
                        } else {
                            v32 /* !! */  = ScriptValue.NULL;
                        }
                        var266_261 = ScriptProgram.elementsOf((ScriptValue)v32 /* !! */ );
                        if (var266_261 != null) {
                            for (ScriptValue var268_263 : var266_261) {
                                var0.val("entity", var268_263);
                                var276_264 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                                var276_264.val("entity", var1_1.getClassOrVar("entity"));
                                if (!Utils.isRestingItem(var276_264).asBool()) continue;
                                var277_265 = ScriptContext.builder().copyFrom(var1_1);
                                var278_266 = var1_1.getClassOrVar("entity");
                                var277_265.val("item_id", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var278_266 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var278_266, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                                var279_267 = Saw._sawOutputFor(var277_265);
                                var0.val("out_id", var279_267);
                                if (!(ScriptFormula.valuesEqual((ScriptValue)var279_267, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                                var280_268 = var1_1.getClassOrVar("Machine");
                                if (var280_268 != ScriptValue.NULL) {
                                    var281_269 = "_saw_item_id";
                                    var282_270 = "str";
                                    var284_271 = var1_1.getClassOrVar("entity");
                                    var283_272 = PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var284_271 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var284_271, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    if (var280_268 instanceof ScriptValue.Obj && (var286_274 = (var285_273 = (ScriptValue.Obj)var280_268).instance()) != null && !(var286_274 instanceof PolyClass) && var285_273.typeName().equals("Machine")) {
                                        var287_275 = new PolyClassMachine(var286_274);
                                        v33 /* !! */  = ScriptValue.of((boolean)var287_275.tm$82_set_typed(var281_269, var282_270, (ScriptValue)var283_272));
                                    } else {
                                        var288_276 = new ArrayList<Object>();
                                        var288_276.add(ScriptValue.of((String)var281_269));
                                        var288_276.add(ScriptValue.of((String)var282_270));
                                        var288_276.add(var283_272);
                                        v33 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var280_268, var288_276, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v33 /* !! */  = ScriptValue.NULL;
                                }
                                var289_277 = var1_1.getClassOrVar("Machine");
                                if (var289_277 != ScriptValue.NULL) {
                                    var290_278 = "_saw_out_id";
                                    var291_279 = "str";
                                    var292_280 = var279_267;
                                    if (var289_277 instanceof ScriptValue.Obj && (var294_282 = (var293_281 = (ScriptValue.Obj)var289_277).instance()) != null && !(var294_282 instanceof PolyClass) && var293_281.typeName().equals("Machine")) {
                                        var295_283 = new PolyClassMachine(var294_282);
                                        v34 /* !! */  = ScriptValue.of((boolean)var295_283.tm$82_set_typed(var290_278, var291_279, var292_280));
                                    } else {
                                        var296_284 = new ArrayList<ScriptValue>();
                                        var296_284.add(ScriptValue.of((String)var290_278));
                                        var296_284.add(ScriptValue.of((String)var291_279));
                                        var296_284.add(var292_280);
                                        v34 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var289_277, var296_284, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v34 /* !! */  = ScriptValue.NULL;
                                }
                                var297_285 = var1_1.getClassOrVar("Machine");
                                if (var297_285 != ScriptValue.NULL) {
                                    var298_286 = "_saw_count";
                                    var299_287 = "int";
                                    var301_288 = new ArrayList<ScriptValue>();
                                    var302_289 = var1_1.getClassOrVar("entity");
                                    var301_288.add((ScriptValue)(var302_289 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var302_289, (ScriptContext)var1_1) : ScriptValue.NULL));
                                    var300_290 = ScriptFormula.callBuiltin((String)"item_count", var301_288, (ScriptContext)var1_1);
                                    if (var297_285 instanceof ScriptValue.Obj && (var304_292 = (var303_291 = (ScriptValue.Obj)var297_285).instance()) != null && !(var304_292 instanceof PolyClass) && var303_291.typeName().equals("Machine")) {
                                        var305_293 = new PolyClassMachine(var304_292);
                                        v35 /* !! */  = ScriptValue.of((boolean)var305_293.tm$82_set_typed(var298_286, var299_287, var300_290));
                                    } else {
                                        var306_294 = new ArrayList<ScriptValue>();
                                        var306_294.add(ScriptValue.of((String)var298_286));
                                        var306_294.add(ScriptValue.of((String)var299_287));
                                        var306_294.add(var300_290);
                                        v35 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var297_285, var306_294, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v35 /* !! */  = ScriptValue.NULL;
                                }
                                var307_295 = var1_1.getClassOrVar("Machine");
                                if (var307_295 != ScriptValue.NULL) {
                                    var308_296 = "_saw_progress";
                                    var309_297 = "int";
                                    var310_298 = ScriptValue.of((double)0.0);
                                    if (var307_295 instanceof ScriptValue.Obj && (var312_300 = (var311_299 = (ScriptValue.Obj)var307_295).instance()) != null && !(var312_300 instanceof PolyClass) && var311_299.typeName().equals("Machine")) {
                                        var313_301 = new PolyClassMachine(var312_300);
                                        v36 /* !! */  = ScriptValue.of((boolean)var313_301.tm$82_set_typed(var308_296, var309_297, var310_298));
                                    } else {
                                        var314_302 = new ArrayList<ScriptValue>();
                                        var314_302.add(ScriptValue.of((String)var308_296));
                                        var314_302.add(ScriptValue.of((String)var309_297));
                                        var314_302.add(var310_298);
                                        v36 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var307_295, var314_302, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v36 /* !! */  = ScriptValue.NULL;
                                }
                                var315_303 = var1_1.getClassOrVar("Machine");
                                if (var315_303 != ScriptValue.NULL) {
                                    var316_304 = "_saw_from_ground";
                                    var317_305 = "int";
                                    var318_306 = ScriptValue.of((double)1.0);
                                    if (var315_303 instanceof ScriptValue.Obj && (var320_308 = (var319_307 = (ScriptValue.Obj)var315_303).instance()) != null && !(var320_308 instanceof PolyClass) && var319_307.typeName().equals("Machine")) {
                                        var321_309 = new PolyClassMachine(var320_308);
                                        v37 /* !! */  = ScriptValue.of((boolean)var321_309.tm$82_set_typed(var316_304, var317_305, var318_306));
                                    } else {
                                        var322_310 = new ArrayList<ScriptValue>();
                                        var322_310.add(ScriptValue.of((String)var316_304));
                                        var322_310.add(ScriptValue.of((String)var317_305));
                                        var322_310.add(var318_306);
                                        v37 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var315_303, var322_310, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v37 /* !! */  = ScriptValue.NULL;
                                }
                                var323_311 = var1_1.getClassOrVar("entity");
                                if (var323_311 != ScriptValue.NULL) {
                                    var324_312 = new ArrayList<E>();
                                    v38 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var323_311, var324_312, (ScriptContext)var1_1);
                                } else {
                                    v38 /* !! */  = ScriptValue.NULL;
                                }
                                break;
                            }
                        }
                    }
                }
            } else {
                var325_313 = var1_1.getClassOrVar("Machine");
                if (var325_313 != ScriptValue.NULL) {
                    var326_314 = "_saw_progress";
                    var327_315 = "int";
                    if (var325_313 instanceof ScriptValue.Obj && (var329_317 = (var328_316 = (ScriptValue.Obj)var325_313).instance()) != null && !(var329_317 instanceof PolyClass) && var328_316.typeName().equals("Machine")) {
                        var330_318 = new PolyClassMachine(var329_317);
                        v39 /* !! */  = var330_318.tm$34_get_typed(var326_314, var327_315);
                    } else {
                        var331_319 = new ArrayList<ScriptValue>();
                        var331_319.add(ScriptValue.of((String)var326_314));
                        var331_319.add(ScriptValue.of((String)var327_315));
                        v39 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var325_313, var331_319, (ScriptContext)var1_1);
                    }
                } else {
                    v39 /* !! */  = ScriptValue.NULL;
                }
                var332_320 = v39 /* !! */ ;
                var0.val("progress", var332_320);
                if (ScriptFormula.valuesEqual((ScriptValue)var332_320, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var333_321 = 0.0;
                    var335_322 = ScriptValue.of((double)0.0);
                    var0.val("progress", var335_322);
                }
                var336_323 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)Math.abs(var151_140.asNum())));
                var0.val("progress", var336_323);
                var337_324 = var1_1.getClassOrVar("Machine");
                if (var337_324 != ScriptValue.NULL) {
                    var339_325 = ScriptContext.builder().copyFrom(var1_1);
                    var339_325.val("current_rpm", var151_140);
                    var338_326 = Saw._sawSuCost(var339_325);
                    if (var337_324 instanceof ScriptValue.Obj && (var341_328 = (var340_327 = (ScriptValue.Obj)var337_324).instance()) != null && !(var341_328 instanceof PolyClass) && var340_327.typeName().equals("Machine")) {
                        var342_329 = new PolyClassMachine(var341_328);
                        v40 /* !! */  = ScriptValue.of((boolean)var342_329.tm$56_report_su(var338_326.asNum()));
                    } else {
                        var343_330 = new ArrayList<ScriptValue>();
                        var343_330.add(var338_326);
                        v40 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var337_324, var343_330, (ScriptContext)var1_1);
                    }
                } else {
                    v40 /* !! */  = ScriptValue.NULL;
                }
                if (var336_323.asNum() >= var11_8) {
                    var344_331 = var1_1.getClassOrVar("Machine");
                    if (var344_331 != ScriptValue.NULL) {
                        var345_332 = "_saw_out_id";
                        var346_333 = "str";
                        if (var344_331 instanceof ScriptValue.Obj && (var348_335 = (var347_334 = (ScriptValue.Obj)var344_331).instance()) != null && !(var348_335 instanceof PolyClass) && var347_334.typeName().equals("Machine")) {
                            var349_336 = new PolyClassMachine(var348_335);
                            v41 /* !! */  = var349_336.tm$34_get_typed(var345_332, var346_333);
                        } else {
                            var350_337 = new ArrayList<ScriptValue>();
                            var350_337.add(ScriptValue.of((String)var345_332));
                            var350_337.add(ScriptValue.of((String)var346_333));
                            v41 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var344_331, var350_337, (ScriptContext)var1_1);
                        }
                    } else {
                        v41 /* !! */  = ScriptValue.NULL;
                    }
                    var351_338 = v41 /* !! */ ;
                    var0.val("out_id", var351_338);
                    var352_339 = var1_1.getClassOrVar("Machine");
                    if (var352_339 != ScriptValue.NULL) {
                        var353_340 = "_saw_count";
                        var354_341 = "int";
                        if (var352_339 instanceof ScriptValue.Obj && (var356_343 = (var355_342 = (ScriptValue.Obj)var352_339).instance()) != null && !(var356_343 instanceof PolyClass) && var355_342.typeName().equals("Machine")) {
                            var357_344 = new PolyClassMachine(var356_343);
                            v42 /* !! */  = var357_344.tm$34_get_typed(var353_340, var354_341);
                        } else {
                            var358_345 = new ArrayList<ScriptValue>();
                            var358_345.add(ScriptValue.of((String)var353_340));
                            var358_345.add(ScriptValue.of((String)var354_341));
                            v42 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var352_339, var358_345, (ScriptContext)var1_1);
                        }
                    } else {
                        v42 /* !! */  = ScriptValue.NULL;
                    }
                    var359_346 = v42 /* !! */ ;
                    var0.val("remaining", var359_346);
                    if (ScriptFormula.valuesEqual((ScriptValue)var359_346, (ScriptValue)var1_1.getClassOrVar("null")) != false || var359_346.asNum() <= 0.0 != false) {
                        var360_347 = 1.0;
                        var362_348 = ScriptValue.of((double)1.0);
                        var0.val("remaining", var362_348);
                    }
                    if ((var363_349 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        var364_350 = "_saw_from_ground";
                        var365_351 = "int";
                        if (var363_349 instanceof ScriptValue.Obj && (var367_353 = (var366_352 = (ScriptValue.Obj)var363_349).instance()) != null && !(var367_353 instanceof PolyClass) && var366_352.typeName().equals("Machine")) {
                            var368_354 = new PolyClassMachine(var367_353);
                            v43 /* !! */  = var368_354.tm$34_get_typed(var364_350, var365_351);
                        } else {
                            var369_355 = new ArrayList<ScriptValue>();
                            var369_355.add(ScriptValue.of((String)var364_350));
                            var369_355.add(ScriptValue.of((String)var365_351));
                            v43 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var363_349, var369_355, (ScriptContext)var1_1);
                        }
                    } else {
                        v43 /* !! */  = ScriptValue.NULL;
                    }
                    var370_356 = v43 /* !! */ ;
                    var0.val("from_ground", var370_356);
                    var371_357 = ScriptContext.builder().copyFrom(var1_1);
                    var371_357.val("item_id", var1_1.getClassOrVar("active_id"));
                    var371_357.val("out_id", var351_338);
                    var372_358 = Saw._sawRecipeFor(var371_357);
                    var0.val("recipe", var372_358);
                    if (ScriptFormula.valuesEqual((ScriptValue)var372_358, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var373_359 = var1_1.getClassOrVar("recipe");
                        if (var373_359 != ScriptValue.NULL) {
                            var374_360 = new ArrayList<E>();
                            v44 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "outputs", (ScriptValue)var373_359, var374_360, (ScriptContext)var1_1);
                        } else {
                            v44 /* !! */  = ScriptValue.NULL;
                        }
                    } else {
                        v44 /* !! */  = var1_1.getClassOrVar("null");
                    }
                    var375_361 = v44 /* !! */ ;
                    var0.val("result", var375_361);
                    if (ScriptFormula.valuesEqual((ScriptValue)var375_361, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var376_362 = var1_1.getClassOrVar("result");
                        v45 /* !! */  = var376_362 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "items", (ScriptValue)var376_362, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var377_363 = new ArrayList<ScriptValue>();
                        var378_364 = new ArrayList<ScriptValue>();
                        var378_364.add(var351_338);
                        var378_364.add(ScriptValue.of((double)1.0));
                        var377_363.add(ScriptFormula.callBuiltin((String)"MinecraftItem", var378_364, (ScriptContext)var1_1));
                        v45 /* !! */  = new ScriptValue.Array(var377_363);
                    }
                    var379_365 /* !! */  = v45 /* !! */ ;
                    var0.val("items", var379_365 /* !! */ );
                    var380_366 = ScriptProgram.elementsOf((ScriptValue)var379_365 /* !! */ );
                    if (var380_366 != null) {
                        for (ScriptValue var382_368 : var380_366) {
                            var0.val("out_item", var382_368);
                            if (ScriptFormula.valuesEqual((ScriptValue)var370_356, (ScriptValue)ScriptValue.of((double)1.0)) != false || ScriptFormula.valuesEqual((ScriptValue)var370_356, (ScriptValue)ScriptValue.of((double)2.0)) != false) {
                                var383_369 = ScriptContext.builder().copyFrom(var1_1);
                                var383_369.val("item", var1_1.getClassOrVar("out_item"));
                                Saw._sawDepositDirectional(var383_369);
                                continue;
                            }
                            var384_370 = var1_1.getClassOrVar("belt");
                            if (var384_370 != ScriptValue.NULL) {
                                var385_371 = new ArrayList<ScriptValue>();
                                var385_371.add(var1_1.getClassOrVar("out_item"));
                                v46 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var384_370, var385_371, (ScriptContext)var1_1);
                            } else {
                                v46 /* !! */  = ScriptValue.NULL;
                            }
                            var386_372 = v46 /* !! */ ;
                            var0.val("leftover", var386_372);
                            var387_373 = new ArrayList<ScriptValue>();
                            var387_373.add(var386_372);
                            if (!(ScriptFormula.callBuiltin((String)"is_empty", var387_373, (ScriptContext)var1_1).asBool() ^ true)) continue;
                            var388_374 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var388_374.val("item", var386_372);
                            Utils.1._deposit((ScriptContext.Builder)var388_374);
                        }
                    }
                    var389_375 = var1_1.getNum("remaining") - 1.0;
                    var391_376 = ScriptValue.of((double)var389_375);
                    var0.val("remaining", var391_376);
                    var392_377 = var1_1.getClassOrVar("Machine");
                    if (var392_377 != ScriptValue.NULL) {
                        var393_378 = "_saw_count";
                        var394_379 = "int";
                        var395_380 = ScriptValue.of((double)var389_375);
                        if (var392_377 instanceof ScriptValue.Obj && (var397_382 = (var396_381 = (ScriptValue.Obj)var392_377).instance()) != null && !(var397_382 instanceof PolyClass) && var396_381.typeName().equals("Machine")) {
                            var398_383 = new PolyClassMachine(var397_382);
                            v47 /* !! */  = ScriptValue.of((boolean)var398_383.tm$82_set_typed(var393_378, var394_379, var395_380));
                        } else {
                            var399_384 = new ArrayList<ScriptValue>();
                            var399_384.add(ScriptValue.of((String)var393_378));
                            var399_384.add(ScriptValue.of((String)var394_379));
                            var399_384.add(var395_380);
                            v47 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var392_377, var399_384, (ScriptContext)var1_1);
                        }
                    } else {
                        v47 /* !! */  = ScriptValue.NULL;
                    }
                    var400_385 = var1_1.getClassOrVar("Machine");
                    if (var400_385 != ScriptValue.NULL) {
                        var401_386 = "_saw_progress";
                        var402_387 = "int";
                        var403_388 = ScriptValue.of((double)0.0);
                        if (var400_385 instanceof ScriptValue.Obj && (var405_390 = (var404_389 = (ScriptValue.Obj)var400_385).instance()) != null && !(var405_390 instanceof PolyClass) && var404_389.typeName().equals("Machine")) {
                            var406_391 = new PolyClassMachine(var405_390);
                            v48 /* !! */  = ScriptValue.of((boolean)var406_391.tm$82_set_typed(var401_386, var402_387, var403_388));
                        } else {
                            var407_392 = new ArrayList<ScriptValue>();
                            var407_392.add(ScriptValue.of((String)var401_386));
                            var407_392.add(ScriptValue.of((String)var402_387));
                            var407_392.add(var403_388);
                            v48 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var400_385, var407_392, (ScriptContext)var1_1);
                        }
                    } else {
                        v48 /* !! */  = ScriptValue.NULL;
                    }
                    if (var389_375 <= 0.0) {
                        var408_393 = var1_1.getClassOrVar("Machine");
                        if (var408_393 != ScriptValue.NULL) {
                            var409_394 = "_saw_item_id";
                            var410_395 = "str";
                            var411_396 = ScriptValue.of((String)"");
                            if (var408_393 instanceof ScriptValue.Obj && (var413_398 = (var412_397 = (ScriptValue.Obj)var408_393).instance()) != null && !(var413_398 instanceof PolyClass) && var412_397.typeName().equals("Machine")) {
                                var414_399 = new PolyClassMachine(var413_398);
                                v49 /* !! */  = ScriptValue.of((boolean)var414_399.tm$82_set_typed(var409_394, var410_395, var411_396));
                            } else {
                                var415_400 = new ArrayList<ScriptValue>();
                                var415_400.add(ScriptValue.of((String)var409_394));
                                var415_400.add(ScriptValue.of((String)var410_395));
                                var415_400.add(var411_396);
                                v49 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var408_393, var415_400, (ScriptContext)var1_1);
                            }
                        } else {
                            v49 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var416_401 = var1_1.getClassOrVar("Machine");
                    if (var416_401 != ScriptValue.NULL) {
                        var417_402 = "_saw_progress";
                        var418_403 = "int";
                        var419_404 = var336_323;
                        if (var416_401 instanceof ScriptValue.Obj && (var421_406 = (var420_405 = (ScriptValue.Obj)var416_401).instance()) != null && !(var421_406 instanceof PolyClass) && var420_405.typeName().equals("Machine")) {
                            var422_407 = new PolyClassMachine(var421_406);
                            v50 /* !! */  = ScriptValue.of((boolean)var422_407.tm$82_set_typed(var417_402, var418_403, var419_404));
                        } else {
                            var423_408 = new ArrayList<ScriptValue>();
                            var423_408.add(ScriptValue.of((String)var417_402));
                            var423_408.add(ScriptValue.of((String)var418_403));
                            var423_408.add(var419_404);
                            v50 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var416_401, var423_408, (ScriptContext)var1_1);
                        }
                    } else {
                        v50 /* !! */  = ScriptValue.NULL;
                    }
                }
            }
        }
        var424_409 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var424_409.val("act_key", ScriptValue.of((String)"_saw_act"));
        var424_409.val("is_now", var1_1.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)var424_409);
        Saw.FILE_SCOPE = var0.build();
    }
}
