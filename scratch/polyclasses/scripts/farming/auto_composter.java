/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine
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
import dev.arubik.craftengine.script.PolyClassMachine;
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
                PolyClassMachine polyClassMachine = new PolyClassMachine(object2);
                object = polyClassMachine.tm$34_get_typed(string, string2);
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
        List list = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList3, (ScriptContext)scriptContext), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                PolyClassMachine polyClassMachine;
                Object object3;
                PolyClassMachine polyClassMachine2;
                builder.val("i", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                if (!(scriptContext.getBool("found") ^ true)) continue;
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptContext.getClassOrVar("i"));
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("Machine");
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue5 != ScriptValue.NULL ? ((polyClassMachine2 = PolyClassMachine.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassMachine2.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList4, (ScriptContext)scriptContext);
                builder.val("item", (ScriptValue)callSite);
                ArrayList<CallSite> arrayList5 = new ArrayList<CallSite>();
                arrayList5.add(callSite);
                if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList5, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("COMPOST_VALUES");
                if (scriptValue6 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    ScriptValue scriptValue7 = scriptContext.getClassOrVar("item");
                    arrayList6.add((ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL));
                    arrayList6.add(ScriptValue.of((double)0.0));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue6, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                ScriptValue scriptValue8 = object3;
                builder.val("compost_value", scriptValue8);
                if (!(scriptValue8.asNum() > 0.0)) continue;
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(scriptContext.getClassOrVar("i"));
                arrayList7.add(ScriptValue.of((double)1.0));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(scriptValue9 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassMachine.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue9, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList7, (ScriptContext)scriptContext);
                ScriptValue scriptValue10 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("level"), (ScriptValue)scriptValue8);
                builder.val("level", scriptValue10);
                boolean bl2 = true;
                ScriptValue scriptValue11 = ScriptValue.of((boolean)true);
                builder.val("found", scriptValue11);
            }
        }
        if (scriptContext.getBool("found")) {
            ScriptValue scriptValue12;
            if (scriptContext.getNum("level") >= 8.0) {
                PolyClassMachine polyClassMachine;
                ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                arrayList8.add(ScriptValue.of((String)"minecraft:bone_meal"));
                arrayList8.add(ScriptValue.of((double)1.0));
                ScriptValue scriptValue13 = ScriptFormula.callBuiltin((String)"create_item", arrayList8, (ScriptContext)scriptContext);
                builder.val("bonemeal", scriptValue13);
                ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                arrayList9.add(scriptValue13);
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)(scriptValue14 != ScriptValue.NULL ? ((polyClassMachine = PolyClassMachine.ofGuarded((ScriptValue)scriptValue14)) != null ? polyClassMachine.pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList9, (ScriptContext)scriptContext);
                double d = 0.0;
                ScriptValue scriptValue15 = ScriptValue.of((double)0.0);
                builder.val("level", scriptValue15);
            }
            if ((scriptValue12 = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "compost_level";
                String string3 = "int";
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("level");
                if (scriptValue12 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine polyClassMachine = new PolyClassMachine(object4);
                    v2 = ScriptValue.of((boolean)polyClassMachine.tm$82_set_typed(string, string3, scriptValue16));
                } else {
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add(ScriptValue.of((String)string));
                    arrayList10.add(ScriptValue.of((String)string3));
                    arrayList10.add(scriptValue16);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, arrayList10, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        }
        FILE_SCOPE = builder.build();
    }
}
