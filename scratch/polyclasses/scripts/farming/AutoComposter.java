/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassContainer
 *  dev.arubik.craftengine.script.PolyClassMachine_v4
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.farming;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassContainer;
import dev.arubik.craftengine.script.PolyClassMachine_v4;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class AutoComposter {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static void run(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((String)"minecraft:wheat"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:wheat_seeds"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:beetroot_seeds"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:melon_seeds"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:pumpkin_seeds"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:carrot"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:potato"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:beetroot"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:apple"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:cactus"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:vine"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:seagrass"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:tall_seagrass"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:kelp"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:dried_kelp"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:lily_pad"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:dandelion"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:grass"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:tall_grass"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:fern"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:large_fern"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:dead_bush"));
        arrayList.add(ScriptValue.of((double)1.0));
        arrayList.add(ScriptValue.of((String)"minecraft:mushroom_stew"));
        arrayList.add(ScriptValue.of((double)3.0));
        arrayList.add(ScriptValue.of((String)"minecraft:red_mushroom"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:brown_mushroom"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:bread"));
        arrayList.add(ScriptValue.of((double)3.0));
        arrayList.add(ScriptValue.of((String)"minecraft:cookie"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:pumpkin"));
        arrayList.add(ScriptValue.of((double)4.0));
        arrayList.add(ScriptValue.of((String)"minecraft:melon"));
        arrayList.add(ScriptValue.of((double)4.0));
        arrayList.add(ScriptValue.of((String)"minecraft:melon_slice"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:hay_block"));
        arrayList.add(ScriptValue.of((double)5.0));
        arrayList.add(ScriptValue.of((String)"minecraft:nether_wart"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:cocoa_beans"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:sweet_berries"));
        arrayList.add(ScriptValue.of((double)2.0));
        arrayList.add(ScriptValue.of((String)"minecraft:glow_berries"));
        arrayList.add(ScriptValue.of((double)2.0));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
        builder.val("COMPOST_VALUES", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Machine");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "compost_level";
            String string2 = "int";
            if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object2);
                object = polyClassMachine_v4.tm$34_get_typed(string, string2);
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
        builder.val("level", scriptValue3);
        boolean bl = false;
        ScriptValue scriptValue4 = ScriptValue.of((boolean)false);
        builder.val("found", scriptValue4);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(ScriptValue.of((double)18.0));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList3, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                ScriptValue.Obj obj;
                Object object3;
                PolyClassMachine_v4 polyClassMachine_v4;
                Object object4;
                CallSite callSite;
                ScriptValue.Obj obj2;
                Object object5;
                PolyClassMachine_v4 polyClassMachine_v42;
                builder.val("i", scriptValue5);
                if (!(scriptContext.getBool("found") ^ true)) continue;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? ((polyClassMachine_v42 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue6)) != null ? polyClassMachine_v42.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("i");
                if (scriptValue7 instanceof ScriptValue.Obj && (object5 = (obj2 = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object5 instanceof PolyClass) && obj2.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object5);
                    callSite = polyClassContainer.tm$0_get_item(scriptValue8.asNum());
                } else {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(scriptValue8);
                    callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)scriptValue7, arrayList4, (ScriptContext)scriptContext);
                }
                CallSite callSite2 = callSite;
                builder.val("item", (ScriptValue)callSite2);
                ArrayList<CallSite> arrayList5 = new ArrayList<CallSite>();
                arrayList5.add(callSite2);
                if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList5, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("COMPOST_VALUES");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("item");
                    arrayList6.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL));
                    arrayList6.add(ScriptValue.of((double)0.0));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue9, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue11 = object4;
                builder.val("compost_value", scriptValue11);
                if (!(scriptValue11.asNum() > 0.0)) continue;
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue13 = scriptValue12 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue12)) != null ? polyClassMachine_v4.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue12, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("i");
                double d = 1.0;
                if (scriptValue13 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object3);
                    v3 = polyClassContainer.tm$6_remove_item(scriptValue14.asNum(), d);
                } else {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add(scriptValue14);
                    arrayList7.add(ScriptValue.of((double)d));
                    v3 = PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)scriptValue13, arrayList7, (ScriptContext)scriptContext);
                }
                ScriptValue scriptValue15 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("level"), (ScriptValue)scriptValue11);
                builder.val("level", scriptValue15);
                boolean bl2 = true;
                ScriptValue scriptValue16 = ScriptValue.of((boolean)true);
                builder.val("found", scriptValue16);
            }
        }
        if (scriptContext.getBool("found")) {
            ScriptValue scriptValue17;
            if (scriptContext.getNum("level") >= 8.0) {
                ScriptValue.Obj obj;
                Object object6;
                PolyClassMachine_v4 polyClassMachine_v4;
                ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                arrayList8.add(ScriptValue.of((String)"minecraft:bone_meal"));
                arrayList8.add(ScriptValue.of((double)1.0));
                ScriptValue scriptValue18 = ScriptFormula.callBuiltin((String)"create_item", arrayList8, (ScriptContext)scriptContext);
                builder.val("bonemeal", scriptValue18);
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("Machine");
                ScriptValue scriptValue20 = scriptValue19 != ScriptValue.NULL ? ((polyClassMachine_v4 = PolyClassMachine_v4.ofGuarded((ScriptValue)scriptValue19)) != null ? polyClassMachine_v4.pg$120_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue19, (ScriptContext)scriptContext)) : ScriptValue.NULL;
                ScriptValue scriptValue21 = scriptValue18;
                if (scriptValue20 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue20).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Container")) {
                    PolyClassContainer polyClassContainer = new PolyClassContainer(object6);
                    v4 = polyClassContainer.tm$12_push(scriptValue21);
                } else {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(scriptValue21);
                    v4 = PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)scriptValue20, arrayList9, (ScriptContext)scriptContext);
                }
                double d = 0.0;
                ScriptValue scriptValue22 = ScriptValue.of((double)0.0);
                builder.val("level", scriptValue22);
            }
            if ((scriptValue17 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "compost_level";
                String string3 = "int";
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("level");
                if (scriptValue17 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v4 polyClassMachine_v4 = new PolyClassMachine_v4(object7);
                    v5 = ScriptValue.of((boolean)polyClassMachine_v4.tm$82_set_typed(string, string3, scriptValue23));
                } else {
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add(ScriptValue.of((String)string));
                    arrayList10.add(ScriptValue.of((String)string3));
                    arrayList10.add(scriptValue23);
                    v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue17, arrayList10, (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
