/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassMachine
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.conveyor;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassMachine;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.gen.Utils;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

public final class FloorFunnelUtils {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue _floorFunnelVacuum(ScriptContext.Builder var0) {
        block11: {
            var1_1 = var0.peek();
            var5_2 = var1_1.getClassOrVar("Machine");
            if (var5_2 != ScriptValue.NULL) {
                var6_3 = 0.7;
                if (var5_2 instanceof ScriptValue.Obj && (var9_5 = (var8_4 = (ScriptValue.Obj)var5_2).instance()) != null && !(var9_5 instanceof PolyClass) && var8_4.typeName().equals("Machine")) {
                    var10_6 = new PolyClassMachine(var9_5);
                    v0 /* !! */  = var10_6.tm$94_nearby_entities(var6_3);
                } else {
                    var11_7 = new ArrayList<ScriptValue>();
                    var11_7.add(ScriptValue.of((double)var6_3));
                    v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "nearby_entities", (ScriptValue)var5_2, var11_7, (ScriptContext)var1_1);
                }
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            var2_8 = ScriptProgram.elementsOf((ScriptValue)v0 /* !! */ );
            if (var2_8 == null) break block11;
            for (ScriptValue var4_10 : var2_8) {
                var0.val("entity", var4_10);
                var12_11 = ScriptContext.builder().copyFrom(Utils.fileScope()).copyFrom(var1_1);
                var12_11.val("entity", var1_1.getClassOrVar("entity"));
                if (!Utils.isRestingItem(var12_11).asBool()) ** GOTO lbl-1000
                var13_12 = var1_1.getClassOrVar("entity");
                v1 = PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)(var13_12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "pos", (ScriptValue)var13_12, (ScriptContext)var1_1) : ScriptValue.NULL), (ScriptContext)var1_1).asNum();
                var14_13 = var1_1.getClassOrVar("Machine");
                v2 = var14_13 != ScriptValue.NULL ? ((var15_14 = PolyClassMachine.ofGuarded((ScriptValue)var14_13)) != null ? var15_14.tg$202_y() : PolyDispatch.bootstrapGet("memberGet", "y", (ScriptValue)var14_13, (ScriptContext)var1_1).asNum()) : ScriptValue.NULL.asNum();
                if (v1 >= v2) {
                    v3 = true;
                } else lbl-1000:
                // 2 sources

                {
                    v3 = false;
                }
                if (!v3) continue;
                var16_15 = var1_1.getClassOrVar("entity");
                var17_16 = var16_15 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "item", (ScriptValue)var16_15, (ScriptContext)var1_1) : ScriptValue.NULL;
                var0.val("item", var17_16);
                var18_17 = var1_1.getClassOrVar("entity");
                if (var18_17 != ScriptValue.NULL) {
                    var19_18 = new ArrayList<E>();
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "remove", (ScriptValue)var18_17, var19_18, (ScriptContext)var1_1);
                } else {
                    v4 /* !! */  = ScriptValue.NULL;
                }
                return var17_16;
            }
        }
        if ((var20_19 = var1_1.getClassOrVar("Item")) != ScriptValue.NULL) {
            var21_20 = new ArrayList<ScriptValue>();
            var21_20.add(ScriptValue.of((String)"minecraft:air"));
            v5 /* !! */  = var20_19 instanceof ScriptValue.Obj && (var23_22 = (var22_21 = (ScriptValue.Obj)var20_19).instance()) != null && !(var23_22 instanceof PolyClass) && var22_21.typeName().equals("Item") ? new PolyClassItem(var23_22).um$21_create(var21_20) : PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)var20_19, var21_20, (ScriptContext)var1_1);
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        return v5 /* !! */ ;
    }

    public static ScriptValue _floorFunnelTick(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        Object object;
        PolyClassMachine polyClassMachine2;
        PolyClassMachine polyClassMachine3;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)0.0));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine3 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine3.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
        builder.val("held", (ScriptValue)callSite);
        ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
        arrayList2.add(callSite);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue2 = FloorFunnelUtils._floorFunnelVacuum(builder2);
            builder.val("held", scriptValue2);
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add(scriptValue2);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool() && scriptContext.getBool("pull_above")) {
                Object object2;
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
                if (scriptValue3 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object3;
                    double d = 0.0;
                    double d2 = 1.0;
                    double d3 = 0.0;
                    if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                        PolyClassMachine polyClassMachine4 = new PolyClassMachine(object3);
                        object2 = polyClassMachine4.tm$17_container_at(d, d2, d3);
                    } else {
                        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                        arrayList4.add(ScriptValue.of((double)d));
                        arrayList4.add(ScriptValue.of((double)d2));
                        arrayList4.add(ScriptValue.of((double)d3));
                        object2 = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue3, arrayList4, (ScriptContext)scriptContext);
                    }
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue4 = object2;
                builder.val("above", scriptValue4);
                if (ScriptFormula.valuesEqual((ScriptValue)scriptValue4, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
                    Object object4;
                    ScriptValue scriptValue5 = scriptContext.getClassOrVar("above");
                    if (scriptValue5 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                        arrayList5.add(ScriptValue.of((double)64.0));
                        object4 = PolyDispatch.bootstrapCall("memberCall", "pull", (ScriptValue)scriptValue5, arrayList5, (ScriptContext)scriptContext);
                    } else {
                        object4 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue6 = object4;
                    builder.val("held", scriptValue6);
                }
            }
            ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
            arrayList6.add(scriptContext.getClassOrVar("held"));
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList6, (ScriptContext)scriptContext).asBool() ^ true) {
                PolyClassMachine polyClassMachine5;
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(ScriptValue.of((double)0.0));
                arrayList7.add(scriptContext.getClassOrVar("held"));
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? ((polyClassMachine5 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassMachine5.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList7, (ScriptContext)scriptContext);
            }
        }
        ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
        arrayList8.add(ScriptValue.of((double)0.0));
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Machine");
        CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue8 != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassMachine2.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList8, (ScriptContext)scriptContext);
        builder.val("held", (ScriptValue)callSite2);
        ArrayList<CallSite> arrayList9 = new ArrayList<CallSite>();
        arrayList9.add(callSite2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList9, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        if (scriptContext.getBool("pull_above")) {
            PolyClassMachine polyClassMachine6;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                CallSite callSite3 = callSite2;
                double d = 0.0;
                double d4 = -1.0;
                double d5 = 0.0;
                if (scriptValue9 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine7 = new PolyClassMachine(object5);
                    v2 = ScriptValue.of((boolean)polyClassMachine7.tm$50_drop_item_at((ScriptValue)callSite3, d, d4, d5));
                } else {
                    ArrayList<CallSite> arrayList10 = new ArrayList<CallSite>();
                    arrayList10.add(callSite3);
                    arrayList10.add((CallSite)ScriptValue.of((double)d));
                    arrayList10.add((CallSite)ScriptValue.of((double)d4));
                    arrayList10.add((CallSite)ScriptValue.of((double)d5));
                    v2 = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue9, arrayList10, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
            arrayList11.add(ScriptValue.of((double)0.0));
            arrayList11.add(scriptContext.getClassOrVar("null"));
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
            PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? ((polyClassMachine6 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue10)) != null ? polyClassMachine6.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue10, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList11, (ScriptContext)scriptContext);
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Machine");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            double d = 0.0;
            double d6 = -1.0;
            double d7 = 0.0;
            if (scriptValue11 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine8 = new PolyClassMachine(object6);
                object = polyClassMachine8.tm$17_container_at(d, d6, d7);
            } else {
                ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                arrayList12.add(ScriptValue.of((double)d));
                arrayList12.add(ScriptValue.of((double)d6));
                arrayList12.add(ScriptValue.of((double)d7));
                object = PolyDispatch.bootstrapCall("memberCall", "container_at", (ScriptValue)scriptValue11, arrayList12, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = object;
        builder.val("below", scriptValue12);
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue12, (ScriptValue)scriptContext.getClassOrVar("null")) ^ true) {
            Object object7;
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("below");
            if (scriptValue13 != ScriptValue.NULL) {
                ArrayList<CallSite> arrayList13 = new ArrayList<CallSite>();
                arrayList13.add(callSite2);
                object7 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue13, arrayList13, (ScriptContext)scriptContext);
            } else {
                object7 = ScriptValue.NULL;
            }
            ScriptValue scriptValue14 = object7;
            builder.val("leftover", scriptValue14);
            ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
            arrayList14.add(scriptValue14);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList14, (ScriptContext)scriptContext).asBool()) {
                PolyClassMachine polyClassMachine9;
                ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                arrayList15.add(ScriptValue.of((double)0.0));
                arrayList15.add(scriptContext.getClassOrVar("null"));
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue15 != ScriptValue.NULL ? ((polyClassMachine9 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue15)) != null ? polyClassMachine9.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue15, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList15, (ScriptContext)scriptContext);
            } else {
                ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                arrayList16.add(scriptValue14);
                double d = ScriptFormula.callBuiltin((String)"item_count", arrayList16, (ScriptContext)scriptContext).asNum();
                ArrayList<CallSite> arrayList17 = new ArrayList<CallSite>();
                arrayList17.add(callSite2);
                if (d < ScriptFormula.callBuiltin((String)"item_count", arrayList17, (ScriptContext)scriptContext).asNum()) {
                    PolyClassMachine polyClassMachine10;
                    ArrayList<ScriptValue> arrayList18 = new ArrayList<ScriptValue>();
                    arrayList18.add(ScriptValue.of((double)0.0));
                    arrayList18.add(scriptValue14);
                    ScriptValue scriptValue16 = scriptContext.getClassOrVar("Machine");
                    PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue16 != ScriptValue.NULL ? ((polyClassMachine10 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue16)) != null ? polyClassMachine10.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue16, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList18, (ScriptContext)scriptContext);
                }
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("Machine");
        if (scriptValue17 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            CallSite callSite4 = callSite2;
            double d = 0.0;
            double d8 = -1.0;
            double d9 = 0.0;
            if (scriptValue17 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine polyClassMachine11 = new PolyClassMachine(object8);
                v6 = ScriptValue.of((boolean)polyClassMachine11.tm$50_drop_item_at((ScriptValue)callSite4, d, d8, d9));
            } else {
                ArrayList<CallSite> arrayList19 = new ArrayList<CallSite>();
                arrayList19.add(callSite4);
                arrayList19.add((CallSite)ScriptValue.of((double)d));
                arrayList19.add((CallSite)ScriptValue.of((double)d8));
                arrayList19.add((CallSite)ScriptValue.of((double)d9));
                v6 = PolyDispatch.bootstrapCall("memberCall", "drop_item_at", (ScriptValue)scriptValue17, arrayList19, (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList20 = new ArrayList<ScriptValue>();
        arrayList20.add(ScriptValue.of((double)0.0));
        arrayList20.add(scriptContext.getClassOrVar("null"));
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("Machine");
        PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue18 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassMachine.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList20, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelDropHeld(ScriptContext.Builder builder) {
        block2: {
            PolyClassMachine polyClassMachine;
            PolyClassMachine polyClassMachine2;
            ScriptContext scriptContext = builder.peek();
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)0.0));
            ScriptValue scriptValue = scriptContext.getClassOrVar("Machine");
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassMachine2.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList, (ScriptContext)scriptContext);
            builder.val("held", (ScriptValue)callSite);
            ArrayList<CallSite> arrayList2 = new ArrayList<CallSite>();
            arrayList2.add(callSite);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool() ^ true)) break block2;
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
                arrayList3.add(callSite);
                v0 = scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine(object).um$4_drop_item(arrayList3) : PolyDispatch.bootstrapCall("memberCall", "drop_item", (ScriptValue)scriptValue2, arrayList3, (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(ScriptValue.of((double)0.0));
            arrayList4.add(scriptContext.getClassOrVar("null"));
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassMachine.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList4, (ScriptContext)scriptContext);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue _floorFunnelOnRightClick(ScriptContext.Builder builder) {
        PolyClassMachine polyClassMachine;
        PolyClassMachine polyClassMachine2;
        PolyClassPlayer polyClassPlayer;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        arrayList.add((ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer.pg$48_main_hand() : PolyDispatch.bootstrapGet("memberGet", "main_hand", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
            return ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(ScriptValue.of((double)0.0));
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassMachine2.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList2, (ScriptContext)scriptContext);
        builder.val("held", (ScriptValue)callSite);
        ArrayList<CallSite> arrayList3 = new ArrayList<CallSite>();
        arrayList3.add(callSite);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            CallSite callSite2 = callSite;
            if (scriptValue3 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer2.tm$14_give_item((ScriptValue)callSite2));
            } else {
                ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
                arrayList4.add(callSite2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "give_item", (ScriptValue)scriptValue3, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add(ScriptValue.of((double)0.0));
        arrayList5.add(scriptContext.getClassOrVar("null"));
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Machine");
        PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassMachine.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList5, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("is_resting_item");
        ScriptProgram.applyImport((ScriptContext.Builder)builder, (String)"utils.pf", null, arrayList);
        FILE_SCOPE = builder.build();
    }
}
