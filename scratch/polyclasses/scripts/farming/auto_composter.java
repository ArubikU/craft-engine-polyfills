/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassMachine_v2
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
import dev.arubik.craftengine.script.PolyClassMachine_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class AutoComposter {
    public static void run(ScriptContext.Builder builder) {
        block13: {
            ScriptValue scriptValue;
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
            ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"make_map", arrayList, (ScriptContext)scriptContext);
            builder.val("COMPOST_VALUES", scriptValue2);
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Machine");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "compost_level";
                String string2 = "int";
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object2);
                    object = polyClassMachine_v2.tm$34_get_typed(string, string2);
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(ScriptValue.of((String)string));
                    arrayList2.add(ScriptValue.of((String)string2));
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = object;
            builder.val("level", scriptValue4);
            boolean bl = false;
            ScriptValue scriptValue5 = ScriptValue.of((boolean)false);
            builder.val("found", scriptValue5);
            List list = ScriptProgram.resolveForRows((String)"range(18)", (ScriptContext)scriptContext, (int)1);
            if (list != null) {
                for (ScriptValue[] scriptValueArray : list) {
                    ScriptValue.Obj obj;
                    Object object3;
                    Object object4;
                    ScriptValue.Obj obj2;
                    Object object5;
                    builder.val("i", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                    if (!(scriptContext.getBool("found") ^ true)) continue;
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptContext.getClassOrVar("i"));
                    ScriptValue scriptValue6 = scriptContext.getClassOrVar("Machine");
                    CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "get_item", (ScriptValue)(scriptValue6 != ScriptValue.NULL ? (scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj2 = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj2.typeName().equals("Machine") ? new PolyClassMachine_v2(object5).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue6, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList3, (ScriptContext)scriptContext);
                    builder.val("item", (ScriptValue)callSite);
                    ArrayList<CallSite> arrayList4 = new ArrayList<CallSite>();
                    arrayList4.add(callSite);
                    if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList4, (ScriptContext)scriptContext).asBool() ^ true)) continue;
                    ScriptValue scriptValue7 = scriptContext.getClassOrVar("COMPOST_VALUES");
                    if (scriptValue7 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                        ScriptValue scriptValue8 = scriptContext.getClassOrVar("item");
                        arrayList5.add((ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "id", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL));
                        arrayList5.add(ScriptValue.of((double)0.0));
                        object4 = PolyDispatch.bootstrapCall("memberCall", "switch", (ScriptValue)scriptValue7, arrayList5, (ScriptContext)scriptContext);
                    } else {
                        object4 = ScriptValue.NULL;
                    }
                    ScriptValue scriptValue9 = object4;
                    builder.val("compost_value", scriptValue9);
                    if (!(scriptValue9.asNum() > 0.0)) continue;
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(scriptContext.getClassOrVar("i"));
                    arrayList6.add(ScriptValue.of((double)1.0));
                    ScriptValue scriptValue10 = scriptContext.getClassOrVar("Machine");
                    PolyDispatch.bootstrapCall("memberCall", "remove_item", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? (scriptValue10 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object3).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue10, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList6, (ScriptContext)scriptContext);
                    ScriptValue scriptValue11 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("level"), (ScriptValue)scriptValue9);
                    builder.val("level", scriptValue11);
                    boolean bl2 = true;
                    ScriptValue scriptValue12 = ScriptValue.of((boolean)true);
                    builder.val("found", scriptValue12);
                }
            }
            if (!scriptContext.getBool("found")) break block13;
            if (scriptContext.getNum("level") >= 8.0) {
                ScriptValue.Obj obj;
                Object object6;
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(ScriptValue.of((String)"minecraft:bone_meal"));
                arrayList7.add(ScriptValue.of((double)1.0));
                ScriptValue scriptValue13 = ScriptFormula.callBuiltin((String)"create_item", arrayList7, (ScriptContext)scriptContext);
                builder.val("bonemeal", scriptValue13);
                ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                arrayList8.add(scriptValue13);
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Machine");
                PolyDispatch.bootstrapCall("memberCall", "push", (ScriptValue)(scriptValue14 != ScriptValue.NULL ? (scriptValue14 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Machine") ? new PolyClassMachine_v2(object6).pg$119_container() : PolyDispatch.bootstrapGet("memberGet", "container", (ScriptValue)scriptValue14, (ScriptContext)scriptContext)) : ScriptValue.NULL), arrayList8, (ScriptContext)scriptContext);
                double d = 0.0;
                ScriptValue scriptValue15 = ScriptValue.of((double)0.0);
                builder.val("level", scriptValue15);
            }
            if ((scriptValue = scriptContext.getClassOrVar("Machine")) != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "compost_level";
                String string3 = "int";
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("level");
                if (scriptValue instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Machine")) {
                    PolyClassMachine_v2 polyClassMachine_v2 = new PolyClassMachine_v2(object7);
                    v2 = ScriptValue.of((boolean)polyClassMachine_v2.tm$82_set_typed(string, string3, scriptValue16));
                } else {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(ScriptValue.of((String)string));
                    arrayList9.add(ScriptValue.of((String)string3));
                    arrayList9.add(scriptValue16);
                    v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList9, (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
        }
    }
}
