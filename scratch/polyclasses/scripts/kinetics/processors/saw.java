/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v3
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
import dev.arubik.craftengine.script.PolyClassMachine_v3;
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
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("count", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        List list = ScriptProgram.rowsOf((ScriptValue)(scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object).pg$134_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("r", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("r");
                ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("ins", scriptValue4);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                if (!(ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("count"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("count", scriptValue5);
            }
        }
        return scriptContext.getClassOrVar("count");
    }

    public static ScriptValue _sawRecipeAt(ScriptContext.Builder builder) {
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("idx", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        List list = ScriptProgram.rowsOf((ScriptValue)(scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object).pg$134_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("r", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("r");
                ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("ins", scriptValue4);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                if (!(ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)scriptContext.getClassOrVar("target_idx"))) {
                    return scriptContext.getClassOrVar("r");
                }
                ScriptValue scriptValue5 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("idx", scriptValue5);
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
            var2_5 = ScriptProgram.rowsOf((ScriptValue)(var5_2 != ScriptValue.NULL ? (var5_2 instanceof ScriptValue.Obj && (var7_4 = (var6_3 = (ScriptValue.Obj)var5_2).instance()) != null && !(var7_4 instanceof PolyClass) && var6_3.typeName().equals("Machine") ? new PolyClassMachine_v3(var7_4).pg$134_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)var5_2, (ScriptContext)var1_1)) : ScriptValue.NULL), (int)1);
            if (var2_5 == null) break block5;
            for (ScriptValue[] var4_7 : var2_5) {
                var0.val("r", var4_7.length > 0 ? var4_7[0] : ScriptValue.NULL);
                var8_8 = var1_1.getClassOrVar("r");
                var9_9 = var8_8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)var8_8, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("ins", var9_9);
                var10_10 = var1_1.getClassOrVar("r");
                var11_11 = var10_10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)var10_10, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("outs", var11_11);
                var12_12 = new ArrayList<ScriptValue>();
                var12_12.add(var9_9);
                if (!(ScriptFormula.callBuiltin((String)"size", var12_12, (ScriptContext)var1_1).asNum() > 0.0 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var9_9, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("item_id")) != false)) ** GOTO lbl-1000
                var13_13 = new ArrayList<ScriptValue>();
                var13_13.add(var11_11);
                if (ScriptFormula.callBuiltin((String)"size", var13_13, (ScriptContext)var1_1).asNum() > 0.0) {
                    v0 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v0 = false;
                }
                if (!(v0 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var11_11, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("out_id")) != false)) continue;
                return var1_1.getClassOrVar("r");
            }
        }
        var17_14 = new ArrayList<ScriptValue>();
        var17_14.add(var1_1.getClassOrVar("item_id"));
        var18_15 = var1_1.getClassOrVar("Registry");
        var14_18 = ScriptProgram.rowsOf((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)(var18_15 != ScriptValue.NULL ? (var18_15 instanceof ScriptValue.Obj && (var20_17 = (var19_16 = (ScriptValue.Obj)var18_15).instance()) != null && !(var20_17 instanceof PolyClass) && var19_16.typeName().equals("Registry") ? new PolyClassRegistry(var20_17).pg$0_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)var18_15, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptContext)var1_1), var17_14, (ScriptContext)var1_1), (int)1);
        if (var14_18 != null) {
            for (ScriptValue[] var16_20 : var14_18) {
                var0.val("r", var16_20.length > 0 ? var16_20[0] : ScriptValue.NULL);
                var21_21 = var1_1.getClassOrVar("r");
                var22_22 = var21_21 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)var21_21, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("outs", var22_22);
                var23_23 = new ArrayList<ScriptValue>();
                var23_23.add(var22_22);
                if (!(ScriptFormula.callBuiltin((String)"size", var23_23, (ScriptContext)var1_1).asNum() > 0.0 != false && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)var22_22, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("out_id")) != false)) continue;
                return var1_1.getClassOrVar("r");
            }
        }
        return var1_1.getClassOrVar("null");
    }

    public static ScriptValue _sawOutputFor(ScriptContext.Builder builder) {
        List list;
        ScriptValue.Obj obj;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object3;
            String string = "_saw_filter";
            String string2 = "str";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                object2 = polyClassMachine_v3.tm$34_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
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
            Object object4;
            ScriptValue scriptValue5;
            if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("filter_id"), (String)"") ^ true) {
                ScriptValue.Obj obj3;
                Object object5;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                List list2 = ScriptProgram.rowsOf((ScriptValue)(scriptValue6 != ScriptValue.NULL ? (scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj3 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj3.typeName().equals("Machine") ? new PolyClassMachine_v3(object5).pg$134_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL), (int)1);
                if (list2 != null) {
                    for (ScriptValue[] scriptValueArray : list2) {
                        builder.val("r", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                        ScriptValue scriptValue7 = scriptContext.getClassOrVar("r");
                        ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "inputs", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("ins", scriptValue8);
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue8);
                        if (!(ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("item_id")))) continue;
                        ScriptValue scriptValue9 = scriptContext.getClassOrVar("r");
                        ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("outs", scriptValue10);
                        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                        arrayList2.add(scriptValue10);
                        if (!(ScriptFormula.callBuiltin((String)"size", arrayList2, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
                        return scriptContext.getClassOrVar("filter_id");
                    }
                }
            }
            if ((scriptValue5 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj4;
                Object object6;
                String string = "_saw_recipe_index";
                String string3 = "int";
                if (scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj4 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object6);
                    object4 = polyClassMachine_v3.tm$34_get_typed(string, string3);
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string3));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = object4;
            builder.val("idx", scriptValue11);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue11, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                double d2 = 0.0;
                ScriptValue scriptValue12 = ScriptValue.of((double)0.0);
                builder.val("idx", scriptValue12);
            }
            double d3 = (d = scriptValue4.asNum()) == 0.0 ? 0.0 : scriptContext.getNum("idx") % d;
            ScriptValue scriptValue13 = ScriptValue.of((double)d3);
            builder.val("idx", scriptValue13);
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj5;
                Object object7;
                String string = "_saw_recipe_index";
                String string4 = "int";
                double d4 = scriptValue4.asNum();
                ScriptValue scriptValue15 = ScriptValue.of((double)(d4 == 0.0 ? 0.0 : (d3 + 1.0) % d4));
                if (scriptValue14 instanceof ScriptValue.Obj && (object7 = (obj5 = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object7 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object7);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string4, scriptValue15));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(ScriptValue.of((String)string4));
                    arrayList.add(scriptValue15);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("item_id", scriptContext.getClassOrVar("item_id"));
            builder3.val("target_idx", ScriptValue.of((double)d3));
            ScriptValue scriptValue16 = Saw._sawRecipeAt(builder3);
            builder.val("picked", scriptValue16);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue16, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("picked");
                ScriptValue scriptValue18 = scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("outs", scriptValue18);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue18);
                if (ScriptFormula.callBuiltin((String)"size", arrayList, (ScriptContext)scriptContext).asNum() > 0.0) {
                    return PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext);
                }
            }
            return scriptContext.getClassOrVar("null");
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("item_id"));
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("Registry");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "for_input", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "stonecutter", (ScriptValue)(scriptValue19 != ScriptValue.NULL ? (scriptValue19 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Registry") ? new PolyClassRegistry(object).pg$0_recipes() : PolyDispatch.bootstrapGet("memberGet", "recipes", (ScriptValue)scriptValue19, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        builder.val("stone_candidates", (ScriptValue)callSite);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("filter_id"), (String)"") ^ true && (list = ScriptProgram.rowsOf((ScriptValue)callSite, (int)1)) != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("r", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("r");
                ScriptValue scriptValue21 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "outputs", (ScriptValue)scriptValue20, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("outs", scriptValue21);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptValue21);
                if (!(ScriptFormula.callBuiltin((String)"size", arrayList3, (ScriptContext)scriptContext).asNum() > 0.0 && ScriptFormula.valuesEqual((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)ScriptFormula.subscriptGet((ScriptValue)scriptValue21, (ScriptValue)ScriptValue.of((double)0.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("filter_id")))) continue;
                return scriptContext.getClassOrVar("filter_id");
            }
        }
        ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
        arrayList4.add(callSite);
        ScriptValue scriptValue22 = ScriptFormula.callBuiltin((String)"size", arrayList4, (ScriptContext)scriptContext);
        builder.val("stone_count", scriptValue22);
        if (scriptValue22.asNum() > 0.0) {
            double d;
            Object object8;
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Machine");
            if (scriptValue23 != ScriptValue.NULL) {
                ScriptValue.Obj obj6;
                Object object9;
                String string = "_saw_recipe_index";
                String string5 = "int";
                if (scriptValue23 instanceof ScriptValue.Obj && (object9 = (obj6 = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object9 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object9);
                    object8 = polyClassMachine_v3.tm$34_get_typed(string, string5);
                } else {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(ScriptValue.of((String)string));
                    arrayList5.add(ScriptValue.of((String)string5));
                    object8 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue23, arrayList5, (ScriptContext)scriptContext);
                }
            } else {
                object8 = ScriptValue.NULL;
            }
            ScriptValue scriptValue24 = object8;
            builder.val("idx", scriptValue24);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptValue24, (ScriptValue)scriptContext.getClassOrVar("null"))) {
                double d5 = 0.0;
                ScriptValue scriptValue25 = ScriptValue.of((double)0.0);
                builder.val("idx", scriptValue25);
            }
            double d6 = (d = scriptValue22.asNum()) == 0.0 ? 0.0 : scriptContext.getNum("idx") % d;
            ScriptValue scriptValue26 = ScriptValue.of((double)d6);
            builder.val("idx", scriptValue26);
            ScriptValue scriptValue27 = scriptContext.getClassOrVar("Machine");
            if (scriptValue27 != ScriptValue.NULL) {
                ScriptValue.Obj obj7;
                Object object10;
                String string = "_saw_recipe_index";
                String string6 = "int";
                double d7 = scriptValue22.asNum();
                ScriptValue scriptValue28 = ScriptValue.of((double)(d7 == 0.0 ? 0.0 : (d6 + 1.0) % d7));
                if (scriptValue27 instanceof ScriptValue.Obj && (object10 = (obj7 = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object10 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object10);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string6, scriptValue28));
                } else {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(ScriptValue.of((String)string));
                    arrayList6.add(ScriptValue.of((String)string6));
                    arrayList6.add(scriptValue28);
                    v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue27, arrayList6, (ScriptContext)scriptContext);
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
        ScriptValue.Obj obj;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player") ? new PolyClassPlayer(object).pg$47_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        builder.val("held", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj2;
                Object object2;
                String string = "_saw_filter";
                String string2 = "str";
                ScriptValue scriptValue4 = ScriptValue.of((String)"");
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object2);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string2, scriptValue4));
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
                ScriptValue.Obj obj3;
                Object object3;
                ScriptValue scriptValue6;
                String string = "_saw_filter";
                String string3 = "str";
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("held");
                Object object4 = scriptValue6 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL;
                if (scriptValue5 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string3, scriptValue6));
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
        ScriptValue.Obj obj;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"facing"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("facing", (ScriptValue)callSite);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj2;
            Object object3;
            String string = "_saw_rpm_sign";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj2 = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj2.typeName().equals("Machine")) {
                PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                object = polyClassMachine_v3.tm$34_get_typed(string, string2);
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
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v3.tm$82_set_typed(string, string2, scriptValue2));
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
        ScriptValue.Obj obj;
        Object object;
        ScriptValue.Obj obj2;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"face"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object2 = (obj2 = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v3(object2).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("face", (ScriptValue)callSite);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(ScriptValue.of((String)"facing"));
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList2, (ScriptContext)scriptContext);
        builder.val("facing", (ScriptValue)callSite2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)callSite, (String)"wall")) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj3;
                Object object3;
                String string = "back";
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj3 = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj3.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object3);
                    v0 = ScriptValue.of((boolean)polyClassMachine_v3.tm$40_set_rpm_input(string));
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
                ScriptValue.Obj obj4;
                Object object4;
                String string = "";
                if (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj4 = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj4.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object4);
                    v1 = ScriptValue.of((boolean)polyClassMachine_v3.tm$44_set_rpm_output_same(string));
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
                ScriptValue.Obj obj5;
                Object object5;
                String string = "north,south";
                if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj5 = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj5.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object5);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v3.tm$40_set_rpm_input(string));
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
                ScriptValue.Obj obj6;
                Object object6;
                String string = "north,south";
                if (scriptValue6 instanceof ScriptValue.Obj && (object6 = (obj6 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object6 instanceof PolyClass) && obj6.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object6);
                    v3 = ScriptValue.of((boolean)polyClassMachine_v3.tm$44_set_rpm_output_same(string));
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
                ScriptValue.Obj obj7;
                Object object7;
                String string = "east,west";
                if (scriptValue7 instanceof ScriptValue.Obj && (object7 = (obj7 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object7 instanceof PolyClass) && obj7.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object7);
                    v4 = ScriptValue.of((boolean)polyClassMachine_v3.tm$40_set_rpm_input(string));
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
                ScriptValue.Obj obj8;
                Object object8;
                String string = "east,west";
                if (scriptValue8 instanceof ScriptValue.Obj && (object8 = (obj8 = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object8 instanceof PolyClass) && obj8.typeName().equals("Machine")) {
                    PolyClassMachine_v3 polyClassMachine_v3 = new PolyClassMachine_v3(object8);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v3.tm$44_set_rpm_output_same(string));
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
                ScriptValue.Obj obj;
                Object object;
                scriptContext = builder.peek();
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptContext.getClassOrVar("item"));
                if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
                    return ScriptValue.NULL;
                }
                ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
                Object object2 = scriptValue != ScriptValue.NULL ? (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object).pg$160_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                if (!(ScriptFormula.valuesEqual((ScriptValue)object2, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true)) break block4;
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
                v1 = scriptValue5 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v3(object).um$19_drop_item_toward(arrayList3) : PolyDispatch.bootstrapCall("memberCall", "drop_item_toward", (ScriptValue)scriptValue5, arrayList3, (ScriptContext)scriptContext);
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
                    var17_13 = var1_1.getClassOrVar("Machine");
                    var20_16 = var1_1.getClassOrVar("Machine");
                    var23_19 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)(var14_10 != ScriptValue.NULL ? (var14_10 instanceof ScriptValue.Obj && (var16_12 = (var15_11 = (ScriptValue.Obj)var14_10).instance()) != null && !(var16_12 instanceof PolyClass) && var15_11.typeName().equals("Machine") ? new PolyClassMachine_v3(var16_12).pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var14_10, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(var17_13 != ScriptValue.NULL ? (var17_13 instanceof ScriptValue.Obj && (var19_15 = (var18_14 = (ScriptValue.Obj)var17_13).instance()) != null && !(var19_15 instanceof PolyClass) && var18_14.typeName().equals("Machine") ? new PolyClassMachine_v3(var19_15).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var17_13, (ScriptContext)var1_1)) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)",")), (ScriptValue)(var20_16 != ScriptValue.NULL ? (var20_16 instanceof ScriptValue.Obj && (var22_18 = (var21_17 = (ScriptValue.Obj)var20_16).instance()) != null && !(var22_18 instanceof PolyClass) && var21_17.typeName().equals("Machine") ? new PolyClassMachine_v3(var22_18).pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var20_16, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("_hold_key", var23_19);
                    var24_20 = var1_1.getClassOrVar("Machine");
                    var27_23 = var24_20 != ScriptValue.NULL ? (var24_20 instanceof ScriptValue.Obj && (var26_22 = (var25_21 = (ScriptValue.Obj)var24_20).instance()) != null && !(var26_22 instanceof PolyClass) && var25_21.typeName().equals("Machine") ? new PolyClassMachine_v3(var26_22).pg$160_contraption() : PolyDispatch.bootstrapGet("memberGet", "contraption", (ScriptValue)var24_20, (ScriptContext)var1_1)) : ScriptValue.NULL;
                    var0.val("contraption", var27_23);
                    var28_24 = ScriptContext.builder().copyFrom(var1_1);
                    var28_24.val("effective_rpm", (ScriptValue)(ScriptFormula.valuesEqual((ScriptValue)var27_23, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var29_25 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var29_25, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm")));
                    Saw._sawUpdateRpmSign(var28_24);
                    var30_26 = 0.0;
                    var32_27 = ScriptValue.of((double)0.0);
                    var0.val("is_now", var32_27);
                    var33_28 = new ArrayList<ScriptValue>();
                    var33_28.add(ScriptValue.of((String)"face"));
                    var34_29 = var1_1.getClassOrVar("Machine");
                    var37_32 = ScriptFormula.valuesEqualStr((ScriptValue)PolyDispatch.bootstrapCall("memberCall", "property", (ScriptValue)(var34_29 != ScriptValue.NULL ? (var34_29 instanceof ScriptValue.Obj && (var36_31 = (var35_30 = (ScriptValue.Obj)var34_29).instance()) != null && !(var36_31 instanceof PolyClass) && var35_30.typeName().equals("Machine") ? new PolyClassMachine_v3(var36_31).pg$130_block() : PolyDispatch.bootstrapGet("memberGet", "block", (ScriptValue)var34_29, (ScriptContext)var1_1)) : ScriptValue.NULL), var33_28, (ScriptContext)var1_1), (String)"floor");
                    var38_33 = ScriptValue.of((boolean)var37_32);
                    var0.val("is_belt_facing", var38_33);
                    if (!(ScriptFormula.valuesEqual((ScriptValue)var27_23, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) break block183;
                    var39_34 = var1_1.getClassOrVar("contraption");
                    var40_35 = ScriptFormula.valuesEqual((ScriptValue)(var39_34 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var39_34, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptValue)ScriptValue.of((double)0.0)) ^ true;
                    var41_36 = ScriptValue.of((boolean)var40_35);
                    var0.val("is_rotational", var41_36);
                    var42_37 = var1_1.getClassOrVar("contraption");
                    if (var42_37 != ScriptValue.NULL) {
                        var43_38 = new ArrayList<E>();
                        v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "is_moving", (ScriptValue)var42_37, var43_38, (ScriptContext)var1_1);
                    } else {
                        v4 /* !! */  = ScriptValue.NULL;
                    }
                    var44_39 = v4 /* !! */ ;
                    var0.val("is_linear", var44_39);
                    var45_40 = (var40_35 != false || var44_39.asBool() != false) != false ? 1.0 : 0.0;
                    var47_41 = ScriptValue.of((double)var45_40);
                    var0.val("is_now", var47_41);
                    if (!(var45_40 > 0.0)) break block184;
                    if (var40_35) {
                        var48_42 = 10.0;
                        v5 = ScriptValue.of((double)Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(((var50_43 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var50_43, (ScriptContext)var1_1) : ScriptValue.NULL).asNum()) / var48_42));
                    } else {
                        var51_44 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                        var51_44.val("contraption", var27_23);
                        v5 = Utils.1._linearBreakSpeed((ScriptContext.Builder)var51_44);
                    }
                    var52_45 = v5;
                    var0.val("speed", var52_45);
                    var53_46 = var1_1.getClassOrVar("Machine");
                    var56_49 = var1_1.getClassOrVar("Machine");
                    var59_52 = ScriptFormula.addPolymorphic((ScriptValue)(var53_46 != ScriptValue.NULL ? (var53_46 instanceof ScriptValue.Obj && (var55_48 = (var54_47 = (ScriptValue.Obj)var53_46).instance()) != null && !(var55_48 instanceof PolyClass) && var54_47.typeName().equals("Machine") ? new PolyClassMachine_v3(var55_48).pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var53_46, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var56_49 != ScriptValue.NULL ? (var56_49 instanceof ScriptValue.Obj && (var58_51 = (var57_50 = (ScriptValue.Obj)var56_49).instance()) != null && !(var58_51 instanceof PolyClass) && var57_50.typeName().equals("Machine") ? new PolyClassMachine_v3(var58_51).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var56_49, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("tx", var59_52);
                    var60_53 = var1_1.getClassOrVar("Machine");
                    var63_56 = var1_1.getClassOrVar("Machine");
                    var66_59 = ScriptFormula.addPolymorphic((ScriptValue)(var60_53 != ScriptValue.NULL ? (var60_53 instanceof ScriptValue.Obj && (var62_55 = (var61_54 = (ScriptValue.Obj)var60_53).instance()) != null && !(var62_55 instanceof PolyClass) && var61_54.typeName().equals("Machine") ? new PolyClassMachine_v3(var62_55).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var60_53, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var63_56 != ScriptValue.NULL ? (var63_56 instanceof ScriptValue.Obj && (var65_58 = (var64_57 = (ScriptValue.Obj)var63_56).instance()) != null && !(var65_58 instanceof PolyClass) && var64_57.typeName().equals("Machine") ? new PolyClassMachine_v3(var65_58).pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var63_56, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("ty", var66_59);
                    var67_60 = var1_1.getClassOrVar("Machine");
                    var70_63 = var1_1.getClassOrVar("Machine");
                    var73_66 = ScriptFormula.addPolymorphic((ScriptValue)(var67_60 != ScriptValue.NULL ? (var67_60 instanceof ScriptValue.Obj && (var69_62 = (var68_61 = (ScriptValue.Obj)var67_60).instance()) != null && !(var69_62 instanceof PolyClass) && var68_61.typeName().equals("Machine") ? new PolyClassMachine_v3(var69_62).pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var67_60, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var70_63 != ScriptValue.NULL ? (var70_63 instanceof ScriptValue.Obj && (var72_65 = (var71_64 = (ScriptValue.Obj)var70_63).instance()) != null && !(var72_65 instanceof PolyClass) && var71_64.typeName().equals("Machine") ? new PolyClassMachine_v3(var72_65).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var70_63, (ScriptContext)var1_1)) : ScriptValue.NULL));
                    var0.val("tz", var73_66);
                    var74_67 = new ArrayList<ScriptValue>();
                    var74_67.add(var59_52);
                    var74_67.add(var66_59);
                    var74_67.add(var73_66);
                    var75_68 = var1_1.getClassOrVar("contraption");
                    var76_69 = PolyDispatch.bootstrapCall("memberCall", "real_block", (ScriptValue)(var75_68 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "contraption_world", (ScriptValue)var75_68, (ScriptContext)var1_1) : ScriptValue.NULL), var74_67, (ScriptContext)var1_1);
                    var0.val("target", (ScriptValue)var76_69);
                    if ((var37_32 != false || ScriptFormula.valuesEqual((ScriptValue)var76_69, (ScriptValue)var1_1.getClassOrVar("null")) != false) != false || ((var77_70 = var1_1.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var77_70, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() != false) ** GOTO lbl-1000
                    var78_71 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var79_72 = var1_1.getClassOrVar("target");
                    var78_71.val("id", (ScriptValue)(var79_72 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var79_72, (ScriptContext)var1_1) : ScriptValue.NULL));
                    if (!(TreeUtils._isChoppable((ScriptContext.Builder)var78_71).asBool() ^ true)) {
                        v6 = false;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v6 = true;
                    }
                    if (v6) {
                        var80_73 = var1_1.getClassOrVar("contraption");
                        if (var80_73 != ScriptValue.NULL) {
                            var81_74 = new ArrayList<ScriptValue>();
                            var81_74.add(var23_19);
                            v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var80_73, var81_74, (ScriptContext)var1_1);
                        } else {
                            v7 /* !! */  = ScriptValue.NULL;
                        }
                    } else {
                        var82_75 = var1_1.getClassOrVar("contraption");
                        if (var82_75 != ScriptValue.NULL) {
                            var83_76 = new ArrayList<ScriptValue>();
                            var83_76.add(var23_19);
                            v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "hold", (ScriptValue)var82_75, var83_76, (ScriptContext)var1_1);
                        } else {
                            v8 /* !! */  = ScriptValue.NULL;
                        }
                        var84_77 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                        var85_78 = var1_1.getClassOrVar("target");
                        var84_77.val("id", (ScriptValue)(var85_78 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var85_78, (ScriptContext)var1_1) : ScriptValue.NULL));
                        if (TreeUtils._isLog((ScriptContext.Builder)var84_77).asBool()) {
                            var86_79 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                            var86_79.val("contraption", var27_23);
                            var86_79.val("base_x", var59_52);
                            var86_79.val("base_y", var66_59);
                            var86_79.val("base_z", var73_66);
                            var86_79.val("speed", var52_45);
                            TreeUtils._fellTree((ScriptContext.Builder)var86_79);
                        } else {
                            var87_80 = var1_1.getClassOrVar("Machine");
                            if (var87_80 != ScriptValue.NULL) {
                                var88_81 = var76_69;
                                var89_82 = var52_45;
                                if (var87_80 instanceof ScriptValue.Obj && (var91_84 = (var90_83 = (ScriptValue.Obj)var87_80).instance()) != null && !(var91_84 instanceof PolyClass) && var90_83.typeName().equals("Machine")) {
                                    var92_85 = new PolyClassMachine_v3(var91_84);
                                    v9 /* !! */  = var92_85.tm$2_tick_break((ScriptValue)var88_81, var89_82.asNum());
                                } else {
                                    var93_86 = new ArrayList<CallSite>();
                                    var93_86.add(var88_81);
                                    var93_86.add((CallSite)var89_82);
                                    v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var87_80, var93_86, (ScriptContext)var1_1);
                                }
                            } else {
                                v9 /* !! */  = ScriptValue.NULL;
                            }
                            var94_87 = v9 /* !! */ ;
                            var0.val("result", var94_87);
                            if (ScriptFormula.valuesEqual((ScriptValue)var94_87, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var95_88 = ScriptProgram.rowsOf((ScriptValue)var94_87, (int)1)) != null) {
                                for (ScriptValue[] var97_90 : var95_88) {
                                    var0.val("item", var97_90.length > 0 ? var97_90[0] : ScriptValue.NULL);
                                    var98_91 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                                    var98_91.val("item", var1_1.getClassOrVar("item"));
                                    Utils.1._deposit((ScriptContext.Builder)var98_91);
                                }
                            }
                        }
                        var99_92 = var1_1.getClassOrVar("Machine");
                        if (var99_92 != ScriptValue.NULL) {
                            if (var40_35) {
                                var101_93 = ScriptContext.builder().copyFrom(var1_1);
                                var102_94 = var1_1.getClassOrVar("contraption");
                                var101_93.val("current_rpm", (ScriptValue)(var102_94 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var102_94, (ScriptContext)var1_1) : ScriptValue.NULL));
                                v10 = Saw._sawSuCost(var101_93);
                            } else {
                                v10 = var100_95 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)6.0), (ScriptValue)var52_45);
                            }
                            if (var99_92 instanceof ScriptValue.Obj && (var104_97 = (var103_96 = (ScriptValue.Obj)var99_92).instance()) != null && !(var104_97 instanceof PolyClass) && var103_96.typeName().equals("Machine")) {
                                var105_98 = new PolyClassMachine_v3(var104_97);
                                v11 /* !! */  = ScriptValue.of((boolean)var105_98.tm$56_report_su(var100_95.asNum()));
                            } else {
                                var106_99 = new ArrayList<ScriptValue>();
                                var106_99.add(var100_95);
                                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var99_92, var106_99, (ScriptContext)var1_1);
                            }
                        } else {
                            v11 /* !! */  = ScriptValue.NULL;
                        }
                    }
                    break block185;
                }
                var107_100 = var1_1.getClassOrVar("contraption");
                if (var107_100 != ScriptValue.NULL) {
                    var108_101 = new ArrayList<ScriptValue>();
                    var108_101.add(var23_19);
                    v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "release", (ScriptValue)var107_100, var108_101, (ScriptContext)var1_1);
                } else {
                    v12 /* !! */  = ScriptValue.NULL;
                }
                break block185;
            }
            if (!(ScriptFormula.valuesEqual((ScriptValue)var1_1.getClassOrVar("rpm"), (ScriptValue)ScriptValue.of((double)0.0)) ^ true)) break block185;
            var109_102 = 10.0;
            var111_103 = Math.max(1.0, 10.0 == 0.0 ? 0.0 : Math.abs(var1_1.getNum("rpm")) / var109_102);
            var113_104 = ScriptValue.of((double)var111_103);
            var0.val("speed", var113_104);
            var114_105 = var1_1.getClassOrVar("Machine");
            var117_108 = var114_105 != ScriptValue.NULL ? (var114_105 instanceof ScriptValue.Obj && (var116_107 = (var115_106 = (ScriptValue.Obj)var114_105).instance()) != null && !(var116_107 instanceof PolyClass) && var115_106.typeName().equals("Machine") ? new PolyClassMachine_v3(var116_107).pg$128_facing_block() : PolyDispatch.bootstrapGet("memberGet", "facing_block", (ScriptValue)var114_105, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("fb", var117_108);
            if (!((var37_32 ^ true) != false && (((var118_109 = var1_1.getClassOrVar("fb")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "is_air", (ScriptValue)var118_109, (ScriptContext)var1_1) : ScriptValue.NULL).asBool() ^ true) != false)) ** GOTO lbl-1000
            var119_110 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
            var120_111 = var1_1.getClassOrVar("fb");
            var119_110.val("id", (ScriptValue)(var120_111 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var120_111, (ScriptContext)var1_1) : ScriptValue.NULL));
            if (TreeUtils._isChoppable((ScriptContext.Builder)var119_110).asBool()) {
                v13 = true;
            } else lbl-1000:
            // 2 sources

            {
                v13 = false;
            }
            if (v13) {
                var121_112 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                var122_113 = var1_1.getClassOrVar("fb");
                var121_112.val("id", (ScriptValue)(var122_113 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var122_113, (ScriptContext)var1_1) : ScriptValue.NULL));
                if (TreeUtils._isLog((ScriptContext.Builder)var121_112).asBool()) {
                    var123_114 = ScriptContext.builder().copyFrom(TreeUtils.fileScope()).copyFrom(var1_1);
                    var123_114.val("contraption", var1_1.getClassOrVar("null"));
                    var124_115 = var1_1.getClassOrVar("Machine");
                    var127_118 = var1_1.getClassOrVar("Machine");
                    var123_114.val("base_x", ScriptFormula.addPolymorphic((ScriptValue)(var124_115 != ScriptValue.NULL ? (var124_115 instanceof ScriptValue.Obj && (var126_117 = (var125_116 = (ScriptValue.Obj)var124_115).instance()) != null && !(var126_117 instanceof PolyClass) && var125_116.typeName().equals("Machine") ? new PolyClassMachine_v3(var126_117).pg$166_x() : PolyDispatch.bootstrapGet("memberGet", "x", (ScriptValue)var124_115, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var127_118 != ScriptValue.NULL ? (var127_118 instanceof ScriptValue.Obj && (var129_120 = (var128_119 = (ScriptValue.Obj)var127_118).instance()) != null && !(var129_120 instanceof PolyClass) && var128_119.typeName().equals("Machine") ? new PolyClassMachine_v3(var129_120).pg$126_facing_dx() : PolyDispatch.bootstrapGet("memberGet", "facing_dx", (ScriptValue)var127_118, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var130_121 = var1_1.getClassOrVar("Machine");
                    var133_124 = var1_1.getClassOrVar("Machine");
                    var123_114.val("base_y", ScriptFormula.addPolymorphic((ScriptValue)(var130_121 != ScriptValue.NULL ? (var130_121 instanceof ScriptValue.Obj && (var132_123 = (var131_122 = (ScriptValue.Obj)var130_121).instance()) != null && !(var132_123 instanceof PolyClass) && var131_122.typeName().equals("Machine") ? new PolyClassMachine_v3(var132_123).pg$167_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var130_121, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var133_124 != ScriptValue.NULL ? (var133_124 instanceof ScriptValue.Obj && (var135_126 = (var134_125 = (ScriptValue.Obj)var133_124).instance()) != null && !(var135_126 instanceof PolyClass) && var134_125.typeName().equals("Machine") ? new PolyClassMachine_v3(var135_126).pg$124_facing_dy() : PolyDispatch.bootstrapGet("memberGet", "facing_dy", (ScriptValue)var133_124, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var136_127 = var1_1.getClassOrVar("Machine");
                    var139_130 = var1_1.getClassOrVar("Machine");
                    var123_114.val("base_z", ScriptFormula.addPolymorphic((ScriptValue)(var136_127 != ScriptValue.NULL ? (var136_127 instanceof ScriptValue.Obj && (var138_129 = (var137_128 = (ScriptValue.Obj)var136_127).instance()) != null && !(var138_129 instanceof PolyClass) && var137_128.typeName().equals("Machine") ? new PolyClassMachine_v3(var138_129).pg$169_z() : PolyDispatch.bootstrapGet("memberGet", "z", (ScriptValue)var136_127, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)(var139_130 != ScriptValue.NULL ? (var139_130 instanceof ScriptValue.Obj && (var141_132 = (var140_131 = (ScriptValue.Obj)var139_130).instance()) != null && !(var141_132 instanceof PolyClass) && var140_131.typeName().equals("Machine") ? new PolyClassMachine_v3(var141_132).pg$125_facing_dz() : PolyDispatch.bootstrapGet("memberGet", "facing_dz", (ScriptValue)var139_130, (ScriptContext)var1_1)) : ScriptValue.NULL)));
                    var123_114.val("speed", ScriptValue.of((double)var111_103));
                    TreeUtils._fellTree((ScriptContext.Builder)var123_114);
                } else {
                    var142_133 = var1_1.getClassOrVar("Machine");
                    if (var142_133 != ScriptValue.NULL) {
                        var143_134 = var117_108;
                        var144_135 = var111_103;
                        if (var142_133 instanceof ScriptValue.Obj && (var147_137 = (var146_136 = (ScriptValue.Obj)var142_133).instance()) != null && !(var147_137 instanceof PolyClass) && var146_136.typeName().equals("Machine")) {
                            var148_138 = new PolyClassMachine_v3(var147_137);
                            v14 /* !! */  = var148_138.tm$2_tick_break(var143_134, var144_135);
                        } else {
                            var149_139 = new ArrayList<ScriptValue>();
                            var149_139.add(var143_134);
                            var149_139.add(ScriptValue.of((double)var144_135));
                            v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "tick_break", (ScriptValue)var142_133, var149_139, (ScriptContext)var1_1);
                        }
                    } else {
                        v14 /* !! */  = ScriptValue.NULL;
                    }
                    var150_140 = v14 /* !! */ ;
                    var0.val("result", var150_140);
                    if (ScriptFormula.valuesEqual((ScriptValue)var150_140, (ScriptValue)var1_1.getClassOrVar("null")) ^ true && (var151_141 = ScriptProgram.rowsOf((ScriptValue)var150_140, (int)1)) != null) {
                        for (ScriptValue[] var153_143 : var151_141) {
                            var0.val("item", var153_143.length > 0 ? var153_143[0] : ScriptValue.NULL);
                            var154_144 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var154_144.val("item", var1_1.getClassOrVar("item"));
                            Utils.1._deposit((ScriptContext.Builder)var154_144);
                        }
                    }
                }
                var155_145 = var1_1.getClassOrVar("Machine");
                if (var155_145 != ScriptValue.NULL) {
                    var157_146 = ScriptContext.builder().copyFrom(var1_1);
                    var157_146.val("current_rpm", var1_1.getClassOrVar("rpm"));
                    var156_147 = Saw._sawSuCost(var157_146);
                    if (var155_145 instanceof ScriptValue.Obj && (var159_149 = (var158_148 = (ScriptValue.Obj)var155_145).instance()) != null && !(var159_149 instanceof PolyClass) && var158_148.typeName().equals("Machine")) {
                        var160_150 = new PolyClassMachine_v3(var159_149);
                        v15 /* !! */  = ScriptValue.of((boolean)var160_150.tm$56_report_su(var156_147.asNum()));
                    } else {
                        var161_151 = new ArrayList<ScriptValue>();
                        var161_151.add(var156_147);
                        v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var155_145, var161_151, (ScriptContext)var1_1);
                    }
                } else {
                    v15 /* !! */  = ScriptValue.NULL;
                }
            }
            var162_152 = 1.0;
            var164_153 = ScriptValue.of((double)1.0);
            var0.val("is_now", var164_153);
        }
        if (var1_1.getNum("is_now") > 0.0 != false && var37_32 != false) {
            var165_154 = var1_1.getClassOrVar("Machine");
            var168_157 = var165_154 != ScriptValue.NULL ? (var165_154 instanceof ScriptValue.Obj && (var167_156 = (var166_155 = (ScriptValue.Obj)var165_154).instance()) != null && !(var167_156 instanceof PolyClass) && var166_155.typeName().equals("Machine") ? new PolyClassMachine_v3(var167_156).pg$140_belt() : PolyDispatch.bootstrapGet("memberGet", "belt", (ScriptValue)var165_154, (ScriptContext)var1_1)) : ScriptValue.NULL;
            var0.val("belt", var168_157);
            var170_159 = ScriptFormula.valuesEqual((ScriptValue)var27_23, (ScriptValue)var1_1.getClassOrVar("null")) ^ true ? ((var169_158 = var1_1.getClassOrVar("contraption")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "rpm", (ScriptValue)var169_158, (ScriptContext)var1_1) : ScriptValue.NULL) : var1_1.getClassOrVar("rpm");
            var0.val("belt_rpm", var170_159);
            var171_160 = var1_1.getClassOrVar("Machine");
            if (var171_160 != ScriptValue.NULL) {
                var172_161 = "_saw_item_id";
                var173_162 = "str";
                if (var171_160 instanceof ScriptValue.Obj && (var175_164 = (var174_163 = (ScriptValue.Obj)var171_160).instance()) != null && !(var175_164 instanceof PolyClass) && var174_163.typeName().equals("Machine")) {
                    var176_165 = new PolyClassMachine_v3(var175_164);
                    v16 /* !! */  = var176_165.tm$34_get_typed(var172_161, var173_162);
                } else {
                    var177_166 = new ArrayList<ScriptValue>();
                    var177_166.add(ScriptValue.of((String)var172_161));
                    var177_166.add(ScriptValue.of((String)var173_162));
                    v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var171_160, var177_166, (ScriptContext)var1_1);
                }
            } else {
                v16 /* !! */  = ScriptValue.NULL;
            }
            var178_167 = v16 /* !! */ ;
            var0.val("active_id", var178_167);
            if (ScriptFormula.valuesEqual((ScriptValue)var178_167, (ScriptValue)var1_1.getClassOrVar("null"))) {
                var179_168 = ScriptValue.of((String)"");
                var0.val("active_id", var179_168);
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("active_id"), (String)"")) {
                var180_169 = var1_1.getClassOrVar("belt");
                if ((var180_169 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "has_item", (ScriptValue)var180_169, (ScriptContext)var1_1) : ScriptValue.NULL).asBool()) {
                    var181_170 = var1_1.getClassOrVar("belt");
                    if (var181_170 != ScriptValue.NULL) {
                        var182_171 = new ArrayList<E>();
                        v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "peek", (ScriptValue)var181_170, var182_171, (ScriptContext)var1_1);
                    } else {
                        v17 /* !! */  = ScriptValue.NULL;
                    }
                    var183_172 = v17 /* !! */ ;
                    var0.val("carried", var183_172);
                    var184_173 = ScriptContext.builder().copyFrom(var1_1);
                    var185_174 = var1_1.getClassOrVar("carried");
                    var184_173.val("item_id", (ScriptValue)(var185_174 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var185_174, (ScriptContext)var1_1) : ScriptValue.NULL));
                    var186_175 = Saw._sawOutputFor(var184_173);
                    var0.val("out_id", var186_175);
                    var187_176 = new ArrayList<ScriptValue>();
                    var188_177 = var1_1.getClassOrVar("carried");
                    var187_176.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"[saw-debug] own-belt has_item id="), (ScriptValue)(var188_177 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var188_177, (ScriptContext)var1_1) : ScriptValue.NULL)), (ScriptValue)ScriptValue.of((String)" out_id=")), (ScriptValue)(ScriptFormula.valuesEqual((ScriptValue)var186_175, (ScriptValue)var1_1.getClassOrVar("null")) != false ? ScriptValue.of((String)"null") : var186_175)));
                    ScriptFormula.callBuiltin((String)"print", var187_176, (ScriptContext)var1_1);
                    if (ScriptFormula.valuesEqual((ScriptValue)var186_175, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var189_178 = var1_1.getClassOrVar("belt");
                        if (var189_178 != ScriptValue.NULL) {
                            var190_179 = new ArrayList<E>();
                            v18 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "take", (ScriptValue)var189_178, var190_179, (ScriptContext)var1_1);
                        } else {
                            v18 /* !! */  = ScriptValue.NULL;
                        }
                        var191_180 = v18 /* !! */ ;
                        var0.val("taken", var191_180);
                        var192_181 = var1_1.getClassOrVar("Machine");
                        if (var192_181 != ScriptValue.NULL) {
                            var193_182 = "_saw_item_id";
                            var194_183 = "str";
                            var196_184 = var1_1.getClassOrVar("taken");
                            v19 /* !! */  = var195_185 = var196_184 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var196_184, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var192_181 instanceof ScriptValue.Obj && (var198_187 = (var197_186 = (ScriptValue.Obj)var192_181).instance()) != null && !(var198_187 instanceof PolyClass) && var197_186.typeName().equals("Machine")) {
                                var199_188 = new PolyClassMachine_v3(var198_187);
                                v20 /* !! */  = ScriptValue.of((boolean)var199_188.tm$82_set_typed(var193_182, var194_183, var195_185));
                            } else {
                                var200_189 = new ArrayList<ScriptValue>();
                                var200_189.add(ScriptValue.of((String)var193_182));
                                var200_189.add(ScriptValue.of((String)var194_183));
                                var200_189.add(var195_185);
                                v20 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var192_181, var200_189, (ScriptContext)var1_1);
                            }
                        } else {
                            v20 /* !! */  = ScriptValue.NULL;
                        }
                        var201_190 = var1_1.getClassOrVar("Machine");
                        if (var201_190 != ScriptValue.NULL) {
                            var202_191 = "_saw_out_id";
                            var203_192 = "str";
                            var204_193 = var186_175;
                            if (var201_190 instanceof ScriptValue.Obj && (var206_195 = (var205_194 = (ScriptValue.Obj)var201_190).instance()) != null && !(var206_195 instanceof PolyClass) && var205_194.typeName().equals("Machine")) {
                                var207_196 = new PolyClassMachine_v3(var206_195);
                                v21 /* !! */  = ScriptValue.of((boolean)var207_196.tm$82_set_typed(var202_191, var203_192, var204_193));
                            } else {
                                var208_197 = new ArrayList<ScriptValue>();
                                var208_197.add(ScriptValue.of((String)var202_191));
                                var208_197.add(ScriptValue.of((String)var203_192));
                                var208_197.add(var204_193);
                                v21 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var201_190, var208_197, (ScriptContext)var1_1);
                            }
                        } else {
                            v21 /* !! */  = ScriptValue.NULL;
                        }
                        var209_198 = var1_1.getClassOrVar("Machine");
                        if (var209_198 != ScriptValue.NULL) {
                            var210_199 = "_saw_count";
                            var211_200 = "int";
                            var213_201 = var1_1.getClassOrVar("taken");
                            v22 /* !! */  = var212_202 = var213_201 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "count", (ScriptValue)var213_201, (ScriptContext)var1_1) : ScriptValue.NULL;
                            if (var209_198 instanceof ScriptValue.Obj && (var215_204 = (var214_203 = (ScriptValue.Obj)var209_198).instance()) != null && !(var215_204 instanceof PolyClass) && var214_203.typeName().equals("Machine")) {
                                var216_205 = new PolyClassMachine_v3(var215_204);
                                v23 /* !! */  = ScriptValue.of((boolean)var216_205.tm$82_set_typed(var210_199, var211_200, var212_202));
                            } else {
                                var217_206 = new ArrayList<ScriptValue>();
                                var217_206.add(ScriptValue.of((String)var210_199));
                                var217_206.add(ScriptValue.of((String)var211_200));
                                var217_206.add(var212_202);
                                v23 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var209_198, var217_206, (ScriptContext)var1_1);
                            }
                        } else {
                            v23 /* !! */  = ScriptValue.NULL;
                        }
                        var218_207 = var1_1.getClassOrVar("Machine");
                        if (var218_207 != ScriptValue.NULL) {
                            var219_208 = "_saw_progress";
                            var220_209 = "int";
                            var221_210 = ScriptValue.of((double)0.0);
                            if (var218_207 instanceof ScriptValue.Obj && (var223_212 = (var222_211 = (ScriptValue.Obj)var218_207).instance()) != null && !(var223_212 instanceof PolyClass) && var222_211.typeName().equals("Machine")) {
                                var224_213 = new PolyClassMachine_v3(var223_212);
                                v24 /* !! */  = ScriptValue.of((boolean)var224_213.tm$82_set_typed(var219_208, var220_209, var221_210));
                            } else {
                                var225_214 = new ArrayList<ScriptValue>();
                                var225_214.add(ScriptValue.of((String)var219_208));
                                var225_214.add(ScriptValue.of((String)var220_209));
                                var225_214.add(var221_210);
                                v24 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var218_207, var225_214, (ScriptContext)var1_1);
                            }
                        } else {
                            v24 /* !! */  = ScriptValue.NULL;
                        }
                        var226_215 = var1_1.getClassOrVar("Machine");
                        if (var226_215 != ScriptValue.NULL) {
                            var227_216 = "_saw_from_ground";
                            var228_217 = "int";
                            var229_218 = ScriptValue.of((double)0.0);
                            if (var226_215 instanceof ScriptValue.Obj && (var231_220 = (var230_219 = (ScriptValue.Obj)var226_215).instance()) != null && !(var231_220 instanceof PolyClass) && var230_219.typeName().equals("Machine")) {
                                var232_221 = new PolyClassMachine_v3(var231_220);
                                v25 /* !! */  = ScriptValue.of((boolean)var232_221.tm$82_set_typed(var227_216, var228_217, var229_218));
                            } else {
                                var233_222 = new ArrayList<ScriptValue>();
                                var233_222.add(ScriptValue.of((String)var227_216));
                                var233_222.add(ScriptValue.of((String)var228_217));
                                var233_222.add(var229_218);
                                v25 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var226_215, var233_222, (ScriptContext)var1_1);
                            }
                        } else {
                            v25 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var234_223 = ScriptContext.builder().copyFrom(var1_1);
                    var235_224 = Saw._sawInputFace(var234_223);
                    var0.val("in_face", var235_224);
                    var236_225 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                    var236_225.val("face", var235_224);
                    var236_225.val("validator", var1_1.getClassOrVar("null"));
                    var236_225.val("amount", var1_1.getClassOrVar("null"));
                    var237_226 = BeltUtils.beltTake((ScriptContext.Builder)var236_225);
                    var0.val("taken", var237_226);
                    var238_227 = new ArrayList<ScriptValue>();
                    var238_227.add(var237_226);
                    if (ScriptFormula.callBuiltin((String)"is_empty", var238_227, (ScriptContext)var1_1).asBool() ^ true) {
                        var239_228 = ScriptContext.builder().copyFrom(var1_1);
                        var240_229 = var1_1.getClassOrVar("taken");
                        var239_228.val("item_id", (ScriptValue)(var240_229 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var240_229, (ScriptContext)var1_1) : ScriptValue.NULL));
                        var241_230 = Saw._sawOutputFor(var239_228);
                        var0.val("out_id", var241_230);
                        if (ScriptFormula.valuesEqual((ScriptValue)var241_230, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                            var242_231 = var1_1.getClassOrVar("Machine");
                            if (var242_231 != ScriptValue.NULL) {
                                var243_232 = "_saw_item_id";
                                var244_233 = "str";
                                var246_234 = var1_1.getClassOrVar("taken");
                                v26 /* !! */  = var245_235 = var246_234 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)var246_234, (ScriptContext)var1_1) : ScriptValue.NULL;
                                if (var242_231 instanceof ScriptValue.Obj && (var248_237 = (var247_236 = (ScriptValue.Obj)var242_231).instance()) != null && !(var248_237 instanceof PolyClass) && var247_236.typeName().equals("Machine")) {
                                    var249_238 = new PolyClassMachine_v3(var248_237);
                                    v27 /* !! */  = ScriptValue.of((boolean)var249_238.tm$82_set_typed(var243_232, var244_233, var245_235));
                                } else {
                                    var250_239 = new ArrayList<ScriptValue>();
                                    var250_239.add(ScriptValue.of((String)var243_232));
                                    var250_239.add(ScriptValue.of((String)var244_233));
                                    var250_239.add(var245_235);
                                    v27 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var242_231, var250_239, (ScriptContext)var1_1);
                                }
                            } else {
                                v27 /* !! */  = ScriptValue.NULL;
                            }
                            var251_240 = var1_1.getClassOrVar("Machine");
                            if (var251_240 != ScriptValue.NULL) {
                                var252_241 = "_saw_out_id";
                                var253_242 = "str";
                                var254_243 = var241_230;
                                if (var251_240 instanceof ScriptValue.Obj && (var256_245 = (var255_244 = (ScriptValue.Obj)var251_240).instance()) != null && !(var256_245 instanceof PolyClass) && var255_244.typeName().equals("Machine")) {
                                    var257_246 = new PolyClassMachine_v3(var256_245);
                                    v28 /* !! */  = ScriptValue.of((boolean)var257_246.tm$82_set_typed(var252_241, var253_242, var254_243));
                                } else {
                                    var258_247 = new ArrayList<ScriptValue>();
                                    var258_247.add(ScriptValue.of((String)var252_241));
                                    var258_247.add(ScriptValue.of((String)var253_242));
                                    var258_247.add(var254_243);
                                    v28 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var251_240, var258_247, (ScriptContext)var1_1);
                                }
                            } else {
                                v28 /* !! */  = ScriptValue.NULL;
                            }
                            var259_248 = var1_1.getClassOrVar("Machine");
                            if (var259_248 != ScriptValue.NULL) {
                                var260_249 = "_saw_count";
                                var261_250 = "int";
                                var263_251 = new ArrayList<ScriptValue>();
                                var263_251.add(var237_226);
                                var262_252 = ScriptFormula.callBuiltin((String)"item_count", var263_251, (ScriptContext)var1_1);
                                if (var259_248 instanceof ScriptValue.Obj && (var265_254 = (var264_253 = (ScriptValue.Obj)var259_248).instance()) != null && !(var265_254 instanceof PolyClass) && var264_253.typeName().equals("Machine")) {
                                    var266_255 = new PolyClassMachine_v3(var265_254);
                                    v29 /* !! */  = ScriptValue.of((boolean)var266_255.tm$82_set_typed(var260_249, var261_250, var262_252));
                                } else {
                                    var267_256 = new ArrayList<ScriptValue>();
                                    var267_256.add(ScriptValue.of((String)var260_249));
                                    var267_256.add(ScriptValue.of((String)var261_250));
                                    var267_256.add(var262_252);
                                    v29 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var259_248, var267_256, (ScriptContext)var1_1);
                                }
                            } else {
                                v29 /* !! */  = ScriptValue.NULL;
                            }
                            var268_257 = var1_1.getClassOrVar("Machine");
                            if (var268_257 != ScriptValue.NULL) {
                                var269_258 = "_saw_progress";
                                var270_259 = "int";
                                var271_260 = ScriptValue.of((double)0.0);
                                if (var268_257 instanceof ScriptValue.Obj && (var273_262 = (var272_261 = (ScriptValue.Obj)var268_257).instance()) != null && !(var273_262 instanceof PolyClass) && var272_261.typeName().equals("Machine")) {
                                    var274_263 = new PolyClassMachine_v3(var273_262);
                                    v30 /* !! */  = ScriptValue.of((boolean)var274_263.tm$82_set_typed(var269_258, var270_259, var271_260));
                                } else {
                                    var275_264 = new ArrayList<ScriptValue>();
                                    var275_264.add(ScriptValue.of((String)var269_258));
                                    var275_264.add(ScriptValue.of((String)var270_259));
                                    var275_264.add(var271_260);
                                    v30 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var268_257, var275_264, (ScriptContext)var1_1);
                                }
                            } else {
                                v30 /* !! */  = ScriptValue.NULL;
                            }
                            var276_265 = var1_1.getClassOrVar("Machine");
                            if (var276_265 != ScriptValue.NULL) {
                                var277_266 = "_saw_from_ground";
                                var278_267 = "int";
                                var279_268 = ScriptValue.of((double)2.0);
                                if (var276_265 instanceof ScriptValue.Obj && (var281_270 = (var280_269 = (ScriptValue.Obj)var276_265).instance()) != null && !(var281_270 instanceof PolyClass) && var280_269.typeName().equals("Machine")) {
                                    var282_271 = new PolyClassMachine_v3(var281_270);
                                    v31 /* !! */  = ScriptValue.of((boolean)var282_271.tm$82_set_typed(var277_266, var278_267, var279_268));
                                } else {
                                    var283_272 = new ArrayList<ScriptValue>();
                                    var283_272.add(ScriptValue.of((String)var277_266));
                                    var283_272.add(ScriptValue.of((String)var278_267));
                                    var283_272.add(var279_268);
                                    v31 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var276_265, var283_272, (ScriptContext)var1_1);
                                }
                            } else {
                                v31 /* !! */  = ScriptValue.NULL;
                            }
                        } else {
                            var284_273 = ScriptContext.builder().copyFrom(BeltUtils.fileScope()).copyFrom(var1_1);
                            var284_273.val("face", var235_224);
                            var284_273.val("item", var237_226);
                            BeltUtils.beltGive((ScriptContext.Builder)var284_273);
                        }
                    } else {
                        var288_274 = var1_1.getClassOrVar("Machine");
                        if (var288_274 != ScriptValue.NULL) {
                            var289_275 = 0.7;
                            if (var288_274 instanceof ScriptValue.Obj && (var292_277 = (var291_276 = (ScriptValue.Obj)var288_274).instance()) != null && !(var292_277 instanceof PolyClass) && var291_276.typeName().equals("Machine")) {
                                var293_278 = new PolyClassMachine_v3(var292_277);
                                v32 /* !! */  = var293_278.tm$94_nearby_entities(var289_275);
                            } else {
                                var294_279 = new ArrayList<ScriptValue>();
                                var294_279.add(ScriptValue.of((double)var289_275));
                                v32 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var288_274, var294_279, (ScriptContext)var1_1);
                            }
                        } else {
                            v32 /* !! */  = ScriptValue.NULL;
                        }
                        var285_280 = ScriptProgram.rowsOf((ScriptValue)v32 /* !! */ , (int)1);
                        if (var285_280 != null) {
                            for (ScriptValue[] var287_282 : var285_280) {
                                var0.val("entity", var287_282.length > 0 ? var287_282[0] : ScriptValue.NULL);
                                var295_283 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                                var295_283.val("entity", var1_1.getClassOrVar("entity"));
                                if (!Utils.isRestingItem(var295_283).asBool()) continue;
                                var296_284 = ScriptContext.builder().copyFrom(var1_1);
                                var297_285 = var1_1.getClassOrVar("entity");
                                var296_284.val("item_id", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var297_285 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var297_285, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1));
                                var298_286 = Saw._sawOutputFor(var296_284);
                                var0.val("out_id", var298_286);
                                if (!(ScriptFormula.valuesEqual((ScriptValue)var298_286, (ScriptValue)var1_1.getClassOrVar("null")) ^ true)) continue;
                                var299_287 = var1_1.getClassOrVar("Machine");
                                if (var299_287 != ScriptValue.NULL) {
                                    var300_288 = "_saw_item_id";
                                    var301_289 = "str";
                                    var303_290 = var1_1.getClassOrVar("entity");
                                    var302_291 = PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)(var303_290 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var303_290, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1);
                                    if (var299_287 instanceof ScriptValue.Obj && (var305_293 = (var304_292 = (ScriptValue.Obj)var299_287).instance()) != null && !(var305_293 instanceof PolyClass) && var304_292.typeName().equals("Machine")) {
                                        var306_294 = new PolyClassMachine_v3(var305_293);
                                        v33 /* !! */  = ScriptValue.of((boolean)var306_294.tm$82_set_typed(var300_288, var301_289, (ScriptValue)var302_291));
                                    } else {
                                        var307_295 = new ArrayList<Object>();
                                        var307_295.add(ScriptValue.of((String)var300_288));
                                        var307_295.add(ScriptValue.of((String)var301_289));
                                        var307_295.add(var302_291);
                                        v33 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var299_287, var307_295, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v33 /* !! */  = ScriptValue.NULL;
                                }
                                var308_296 = var1_1.getClassOrVar("Machine");
                                if (var308_296 != ScriptValue.NULL) {
                                    var309_297 = "_saw_out_id";
                                    var310_298 = "str";
                                    var311_299 = var298_286;
                                    if (var308_296 instanceof ScriptValue.Obj && (var313_301 = (var312_300 = (ScriptValue.Obj)var308_296).instance()) != null && !(var313_301 instanceof PolyClass) && var312_300.typeName().equals("Machine")) {
                                        var314_302 = new PolyClassMachine_v3(var313_301);
                                        v34 /* !! */  = ScriptValue.of((boolean)var314_302.tm$82_set_typed(var309_297, var310_298, var311_299));
                                    } else {
                                        var315_303 = new ArrayList<ScriptValue>();
                                        var315_303.add(ScriptValue.of((String)var309_297));
                                        var315_303.add(ScriptValue.of((String)var310_298));
                                        var315_303.add(var311_299);
                                        v34 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var308_296, var315_303, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v34 /* !! */  = ScriptValue.NULL;
                                }
                                var316_304 = var1_1.getClassOrVar("Machine");
                                if (var316_304 != ScriptValue.NULL) {
                                    var317_305 = "_saw_count";
                                    var318_306 = "int";
                                    var320_307 = new ArrayList<ScriptValue>();
                                    var321_308 = var1_1.getClassOrVar("entity");
                                    var320_307.add((ScriptValue)(var321_308 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var321_308, (ScriptContext)var1_1) : ScriptValue.NULL));
                                    var319_309 = ScriptFormula.callBuiltin((String)"item_count", var320_307, (ScriptContext)var1_1);
                                    if (var316_304 instanceof ScriptValue.Obj && (var323_311 = (var322_310 = (ScriptValue.Obj)var316_304).instance()) != null && !(var323_311 instanceof PolyClass) && var322_310.typeName().equals("Machine")) {
                                        var324_312 = new PolyClassMachine_v3(var323_311);
                                        v35 /* !! */  = ScriptValue.of((boolean)var324_312.tm$82_set_typed(var317_305, var318_306, var319_309));
                                    } else {
                                        var325_313 = new ArrayList<ScriptValue>();
                                        var325_313.add(ScriptValue.of((String)var317_305));
                                        var325_313.add(ScriptValue.of((String)var318_306));
                                        var325_313.add(var319_309);
                                        v35 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var316_304, var325_313, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v35 /* !! */  = ScriptValue.NULL;
                                }
                                var326_314 = var1_1.getClassOrVar("Machine");
                                if (var326_314 != ScriptValue.NULL) {
                                    var327_315 = "_saw_progress";
                                    var328_316 = "int";
                                    var329_317 = ScriptValue.of((double)0.0);
                                    if (var326_314 instanceof ScriptValue.Obj && (var331_319 = (var330_318 = (ScriptValue.Obj)var326_314).instance()) != null && !(var331_319 instanceof PolyClass) && var330_318.typeName().equals("Machine")) {
                                        var332_320 = new PolyClassMachine_v3(var331_319);
                                        v36 /* !! */  = ScriptValue.of((boolean)var332_320.tm$82_set_typed(var327_315, var328_316, var329_317));
                                    } else {
                                        var333_321 = new ArrayList<ScriptValue>();
                                        var333_321.add(ScriptValue.of((String)var327_315));
                                        var333_321.add(ScriptValue.of((String)var328_316));
                                        var333_321.add(var329_317);
                                        v36 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var326_314, var333_321, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v36 /* !! */  = ScriptValue.NULL;
                                }
                                var334_322 = var1_1.getClassOrVar("Machine");
                                if (var334_322 != ScriptValue.NULL) {
                                    var335_323 = "_saw_from_ground";
                                    var336_324 = "int";
                                    var337_325 = ScriptValue.of((double)1.0);
                                    if (var334_322 instanceof ScriptValue.Obj && (var339_327 = (var338_326 = (ScriptValue.Obj)var334_322).instance()) != null && !(var339_327 instanceof PolyClass) && var338_326.typeName().equals("Machine")) {
                                        var340_328 = new PolyClassMachine_v3(var339_327);
                                        v37 /* !! */  = ScriptValue.of((boolean)var340_328.tm$82_set_typed(var335_323, var336_324, var337_325));
                                    } else {
                                        var341_329 = new ArrayList<ScriptValue>();
                                        var341_329.add(ScriptValue.of((String)var335_323));
                                        var341_329.add(ScriptValue.of((String)var336_324));
                                        var341_329.add(var337_325);
                                        v37 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var334_322, var341_329, (ScriptContext)var1_1);
                                    }
                                } else {
                                    v37 /* !! */  = ScriptValue.NULL;
                                }
                                var342_330 = var1_1.getClassOrVar("entity");
                                if (var342_330 != ScriptValue.NULL) {
                                    var343_331 = new ArrayList<E>();
                                    v38 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var342_330, var343_331, (ScriptContext)var1_1);
                                } else {
                                    v38 /* !! */  = ScriptValue.NULL;
                                }
                                break;
                            }
                        }
                    }
                }
            } else {
                var344_332 = var1_1.getClassOrVar("Machine");
                if (var344_332 != ScriptValue.NULL) {
                    var345_333 = "_saw_progress";
                    var346_334 = "int";
                    if (var344_332 instanceof ScriptValue.Obj && (var348_336 = (var347_335 = (ScriptValue.Obj)var344_332).instance()) != null && !(var348_336 instanceof PolyClass) && var347_335.typeName().equals("Machine")) {
                        var349_337 = new PolyClassMachine_v3(var348_336);
                        v39 /* !! */  = var349_337.tm$34_get_typed(var345_333, var346_334);
                    } else {
                        var350_338 = new ArrayList<ScriptValue>();
                        var350_338.add(ScriptValue.of((String)var345_333));
                        var350_338.add(ScriptValue.of((String)var346_334));
                        v39 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var344_332, var350_338, (ScriptContext)var1_1);
                    }
                } else {
                    v39 /* !! */  = ScriptValue.NULL;
                }
                var351_339 = v39 /* !! */ ;
                var0.val("progress", var351_339);
                if (ScriptFormula.valuesEqual((ScriptValue)var351_339, (ScriptValue)var1_1.getClassOrVar("null"))) {
                    var352_340 = 0.0;
                    var354_341 = ScriptValue.of((double)0.0);
                    var0.val("progress", var354_341);
                }
                var355_342 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("progress"), (ScriptValue)ScriptValue.of((double)Math.abs(var170_159.asNum())));
                var0.val("progress", var355_342);
                var356_343 = var1_1.getClassOrVar("Machine");
                if (var356_343 != ScriptValue.NULL) {
                    var358_344 = ScriptContext.builder().copyFrom(var1_1);
                    var358_344.val("current_rpm", var170_159);
                    var357_345 = Saw._sawSuCost(var358_344);
                    if (var356_343 instanceof ScriptValue.Obj && (var360_347 = (var359_346 = (ScriptValue.Obj)var356_343).instance()) != null && !(var360_347 instanceof PolyClass) && var359_346.typeName().equals("Machine")) {
                        var361_348 = new PolyClassMachine_v3(var360_347);
                        v40 /* !! */  = ScriptValue.of((boolean)var361_348.tm$56_report_su(var357_345.asNum()));
                    } else {
                        var362_349 = new ArrayList<ScriptValue>();
                        var362_349.add(var357_345);
                        v40 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "report_su", (ScriptValue)var356_343, var362_349, (ScriptContext)var1_1);
                    }
                } else {
                    v40 /* !! */  = ScriptValue.NULL;
                }
                if (var355_342.asNum() >= var11_8) {
                    var363_350 = var1_1.getClassOrVar("Machine");
                    if (var363_350 != ScriptValue.NULL) {
                        var364_351 = "_saw_out_id";
                        var365_352 = "str";
                        if (var363_350 instanceof ScriptValue.Obj && (var367_354 = (var366_353 = (ScriptValue.Obj)var363_350).instance()) != null && !(var367_354 instanceof PolyClass) && var366_353.typeName().equals("Machine")) {
                            var368_355 = new PolyClassMachine_v3(var367_354);
                            v41 /* !! */  = var368_355.tm$34_get_typed(var364_351, var365_352);
                        } else {
                            var369_356 = new ArrayList<ScriptValue>();
                            var369_356.add(ScriptValue.of((String)var364_351));
                            var369_356.add(ScriptValue.of((String)var365_352));
                            v41 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var363_350, var369_356, (ScriptContext)var1_1);
                        }
                    } else {
                        v41 /* !! */  = ScriptValue.NULL;
                    }
                    var370_357 = v41 /* !! */ ;
                    var0.val("out_id", var370_357);
                    var371_358 = var1_1.getClassOrVar("Machine");
                    if (var371_358 != ScriptValue.NULL) {
                        var372_359 = "_saw_count";
                        var373_360 = "int";
                        if (var371_358 instanceof ScriptValue.Obj && (var375_362 = (var374_361 = (ScriptValue.Obj)var371_358).instance()) != null && !(var375_362 instanceof PolyClass) && var374_361.typeName().equals("Machine")) {
                            var376_363 = new PolyClassMachine_v3(var375_362);
                            v42 /* !! */  = var376_363.tm$34_get_typed(var372_359, var373_360);
                        } else {
                            var377_364 = new ArrayList<ScriptValue>();
                            var377_364.add(ScriptValue.of((String)var372_359));
                            var377_364.add(ScriptValue.of((String)var373_360));
                            v42 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var371_358, var377_364, (ScriptContext)var1_1);
                        }
                    } else {
                        v42 /* !! */  = ScriptValue.NULL;
                    }
                    var378_365 = v42 /* !! */ ;
                    var0.val("remaining", var378_365);
                    if (ScriptFormula.valuesEqual((ScriptValue)var378_365, (ScriptValue)var1_1.getClassOrVar("null")) != false || var378_365.asNum() <= 0.0 != false) {
                        var379_366 = 1.0;
                        var381_367 = ScriptValue.of((double)1.0);
                        var0.val("remaining", var381_367);
                    }
                    if ((var382_368 = var1_1.getClassOrVar("Machine")) != ScriptValue.NULL) {
                        var383_369 = "_saw_from_ground";
                        var384_370 = "int";
                        if (var382_368 instanceof ScriptValue.Obj && (var386_372 = (var385_371 = (ScriptValue.Obj)var382_368).instance()) != null && !(var386_372 instanceof PolyClass) && var385_371.typeName().equals("Machine")) {
                            var387_373 = new PolyClassMachine_v3(var386_372);
                            v43 /* !! */  = var387_373.tm$34_get_typed(var383_369, var384_370);
                        } else {
                            var388_374 = new ArrayList<ScriptValue>();
                            var388_374.add(ScriptValue.of((String)var383_369));
                            var388_374.add(ScriptValue.of((String)var384_370));
                            v43 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var382_368, var388_374, (ScriptContext)var1_1);
                        }
                    } else {
                        v43 /* !! */  = ScriptValue.NULL;
                    }
                    var389_375 = v43 /* !! */ ;
                    var0.val("from_ground", var389_375);
                    var390_376 = ScriptContext.builder().copyFrom(var1_1);
                    var390_376.val("item_id", var1_1.getClassOrVar("active_id"));
                    var390_376.val("out_id", var370_357);
                    var391_377 = Saw._sawRecipeFor(var390_376);
                    var0.val("recipe", var391_377);
                    if (ScriptFormula.valuesEqual((ScriptValue)var391_377, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var392_378 = var1_1.getClassOrVar("recipe");
                        if (var392_378 != ScriptValue.NULL) {
                            var393_379 = new ArrayList<E>();
                            v44 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "outputs", (ScriptValue)var392_378, var393_379, (ScriptContext)var1_1);
                        } else {
                            v44 /* !! */  = ScriptValue.NULL;
                        }
                    } else {
                        v44 /* !! */  = var1_1.getClassOrVar("null");
                    }
                    var394_380 = v44 /* !! */ ;
                    var0.val("result", var394_380);
                    if (ScriptFormula.valuesEqual((ScriptValue)var394_380, (ScriptValue)var1_1.getClassOrVar("null")) ^ true) {
                        var395_381 = var1_1.getClassOrVar("result");
                        v45 /* !! */  = var395_381 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "items", (ScriptValue)var395_381, (ScriptContext)var1_1) : ScriptValue.NULL;
                    } else {
                        var396_382 = new ArrayList<ScriptValue>();
                        var397_383 = new ArrayList<ScriptValue>();
                        var397_383.add(var370_357);
                        var397_383.add(ScriptValue.of((double)1.0));
                        var396_382.add(ScriptFormula.callBuiltin((String)"MinecraftItem", var397_383, (ScriptContext)var1_1));
                        v45 /* !! */  = new ScriptValue.Array(var396_382);
                    }
                    var398_384 /* !! */  = v45 /* !! */ ;
                    var0.val("items", var398_384 /* !! */ );
                    var399_385 = ScriptProgram.rowsOf((ScriptValue)var398_384 /* !! */ , (int)1);
                    if (var399_385 != null) {
                        for (ScriptValue[] var401_387 : var399_385) {
                            var0.val("out_item", var401_387.length > 0 ? var401_387[0] : ScriptValue.NULL);
                            if (ScriptFormula.valuesEqual((ScriptValue)var389_375, (ScriptValue)ScriptValue.of((double)1.0)) != false || ScriptFormula.valuesEqual((ScriptValue)var389_375, (ScriptValue)ScriptValue.of((double)2.0)) != false) {
                                var402_388 = ScriptContext.builder().copyFrom(var1_1);
                                var402_388.val("item", var1_1.getClassOrVar("out_item"));
                                Saw._sawDepositDirectional(var402_388);
                                continue;
                            }
                            var403_389 = var1_1.getClassOrVar("belt");
                            if (var403_389 != ScriptValue.NULL) {
                                var404_390 = new ArrayList<ScriptValue>();
                                var404_390.add(var1_1.getClassOrVar("out_item"));
                                v46 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "put", (ScriptValue)var403_389, var404_390, (ScriptContext)var1_1);
                            } else {
                                v46 /* !! */  = ScriptValue.NULL;
                            }
                            var405_391 = v46 /* !! */ ;
                            var0.val("leftover", var405_391);
                            var406_392 = new ArrayList<ScriptValue>();
                            var406_392.add(var405_391);
                            if (!(ScriptFormula.callBuiltin((String)"is_empty", var406_392, (ScriptContext)var1_1).asBool() ^ true)) continue;
                            var407_393 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
                            var407_393.val("item", var405_391);
                            Utils.1._deposit((ScriptContext.Builder)var407_393);
                        }
                    }
                    var408_394 = var1_1.getNum("remaining") - 1.0;
                    var410_395 = ScriptValue.of((double)var408_394);
                    var0.val("remaining", var410_395);
                    var411_396 = var1_1.getClassOrVar("Machine");
                    if (var411_396 != ScriptValue.NULL) {
                        var412_397 = "_saw_count";
                        var413_398 = "int";
                        var414_399 = ScriptValue.of((double)var408_394);
                        if (var411_396 instanceof ScriptValue.Obj && (var416_401 = (var415_400 = (ScriptValue.Obj)var411_396).instance()) != null && !(var416_401 instanceof PolyClass) && var415_400.typeName().equals("Machine")) {
                            var417_402 = new PolyClassMachine_v3(var416_401);
                            v47 /* !! */  = ScriptValue.of((boolean)var417_402.tm$82_set_typed(var412_397, var413_398, var414_399));
                        } else {
                            var418_403 = new ArrayList<ScriptValue>();
                            var418_403.add(ScriptValue.of((String)var412_397));
                            var418_403.add(ScriptValue.of((String)var413_398));
                            var418_403.add(var414_399);
                            v47 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var411_396, var418_403, (ScriptContext)var1_1);
                        }
                    } else {
                        v47 /* !! */  = ScriptValue.NULL;
                    }
                    var419_404 = var1_1.getClassOrVar("Machine");
                    if (var419_404 != ScriptValue.NULL) {
                        var420_405 = "_saw_progress";
                        var421_406 = "int";
                        var422_407 = ScriptValue.of((double)0.0);
                        if (var419_404 instanceof ScriptValue.Obj && (var424_409 = (var423_408 = (ScriptValue.Obj)var419_404).instance()) != null && !(var424_409 instanceof PolyClass) && var423_408.typeName().equals("Machine")) {
                            var425_410 = new PolyClassMachine_v3(var424_409);
                            v48 /* !! */  = ScriptValue.of((boolean)var425_410.tm$82_set_typed(var420_405, var421_406, var422_407));
                        } else {
                            var426_411 = new ArrayList<ScriptValue>();
                            var426_411.add(ScriptValue.of((String)var420_405));
                            var426_411.add(ScriptValue.of((String)var421_406));
                            var426_411.add(var422_407);
                            v48 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var419_404, var426_411, (ScriptContext)var1_1);
                        }
                    } else {
                        v48 /* !! */  = ScriptValue.NULL;
                    }
                    if (var408_394 <= 0.0) {
                        var427_412 = var1_1.getClassOrVar("Machine");
                        if (var427_412 != ScriptValue.NULL) {
                            var428_413 = "_saw_item_id";
                            var429_414 = "str";
                            var430_415 = ScriptValue.of((String)"");
                            if (var427_412 instanceof ScriptValue.Obj && (var432_417 = (var431_416 = (ScriptValue.Obj)var427_412).instance()) != null && !(var432_417 instanceof PolyClass) && var431_416.typeName().equals("Machine")) {
                                var433_418 = new PolyClassMachine_v3(var432_417);
                                v49 /* !! */  = ScriptValue.of((boolean)var433_418.tm$82_set_typed(var428_413, var429_414, var430_415));
                            } else {
                                var434_419 = new ArrayList<ScriptValue>();
                                var434_419.add(ScriptValue.of((String)var428_413));
                                var434_419.add(ScriptValue.of((String)var429_414));
                                var434_419.add(var430_415);
                                v49 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var427_412, var434_419, (ScriptContext)var1_1);
                            }
                        } else {
                            v49 /* !! */  = ScriptValue.NULL;
                        }
                    }
                } else {
                    var435_420 = var1_1.getClassOrVar("Machine");
                    if (var435_420 != ScriptValue.NULL) {
                        var436_421 = "_saw_progress";
                        var437_422 = "int";
                        var438_423 = var355_342;
                        if (var435_420 instanceof ScriptValue.Obj && (var440_425 = (var439_424 = (ScriptValue.Obj)var435_420).instance()) != null && !(var440_425 instanceof PolyClass) && var439_424.typeName().equals("Machine")) {
                            var441_426 = new PolyClassMachine_v3(var440_425);
                            v50 /* !! */  = ScriptValue.of((boolean)var441_426.tm$82_set_typed(var436_421, var437_422, var438_423));
                        } else {
                            var442_427 = new ArrayList<ScriptValue>();
                            var442_427.add(ScriptValue.of((String)var436_421));
                            var442_427.add(ScriptValue.of((String)var437_422));
                            var442_427.add(var438_423);
                            v50 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var435_420, var442_427, (ScriptContext)var1_1);
                        }
                    } else {
                        v50 /* !! */  = ScriptValue.NULL;
                    }
                }
            }
        }
        var443_428 = ScriptContext.builder().copyFrom(Utils.1.fileScope()).copyFrom(var1_1);
        var443_428.val("act_key", ScriptValue.of((String)"_saw_act"));
        var443_428.val("is_now", var1_1.getClassOrVar("is_now"));
        Utils.1._updateActivated((ScriptContext.Builder)var443_428);
        Saw.FILE_SCOPE = var0.build();
    }
}
