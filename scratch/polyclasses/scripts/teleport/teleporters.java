/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyClassServer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.teleport;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyClassServer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;

public final class Teleporters {
    public static ScriptValue freqKey(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"teleporter_freq_"), (ScriptValue)scriptContext.getClassOrVar("freq"));
    }

    public static ScriptValue _signAliasAt(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("world_name"));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"world", arrayList, (ScriptContext)scriptContext);
        builder.val("tw", scriptValue);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptValue);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.of((String)"");
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("tw");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptContext.getClassOrVar("x"));
            arrayList3.add(ScriptFormula.callBuiltin((String)"int", arrayList4, (ScriptContext)scriptContext));
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptContext.getClassOrVar("y"));
            arrayList3.add(ScriptFormula.callBuiltin((String)"int", arrayList5, (ScriptContext)scriptContext));
            ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
            arrayList6.add(scriptContext.getClassOrVar("z"));
            arrayList3.add(ScriptFormula.callBuiltin((String)"int", arrayList6, (ScriptContext)scriptContext));
            arrayList3.add(ScriptValue.of((boolean)true));
            object = PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue2, arrayList3, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("center", scriptValue3);
        List list = ScriptProgram.resolveForRows((String)"[\"north\", \"south\", \"east\", \"west\", \"up\", \"down\"]", (ScriptContext)scriptContext, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object2;
                builder.val("dir", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("center");
                if (scriptValue4 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add(scriptContext.getClassOrVar("dir"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue4, arrayList7, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue5 = object2;
                builder.val("b", scriptValue5);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("b");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "metadata_type", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"SignMetadata")) continue;
                List list2 = ScriptProgram.resolveForRows((String)"b.get_metadata.front.lines", (ScriptContext)scriptContext, (int)1);
                if (list2 != null) {
                    for (ScriptValue[] scriptValueArray2 : list2) {
                        builder.val("line", scriptValueArray2.length > 0 ? scriptValueArray2[0] : ScriptValue.NULL);
                        if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("line"), (String)"") ^ true)) continue;
                        return scriptContext.getClassOrVar("line");
                    }
                }
                return ScriptValue.of((String)"");
            }
        }
        return ScriptValue.of((String)"");
    }

    public static ScriptValue openFrequencyDialog(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Machine"));
        arrayList2.add(ScriptValue.of((String)"Browse"));
        arrayList2.add(ScriptValue.of((String)"teleporters.pf:on_frequency_submit"));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(ScriptValue.of((String)"value"));
        arrayList3.add(ScriptValue.of((String)"Frequency"));
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "teleporters_freq";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object2 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)string));
                arrayList4.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList4, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        arrayList3.add((ScriptValue)object2);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Dialog");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(ScriptValue.of((String)"Teleporter Frequency"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue2, arrayList5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object, arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue onFrequencySubmit(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("freq"), (String)"")) {
            ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>\u2718 <white>You must enter a frequency.";
                if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "teleporters_freq";
            String string2 = "string";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("freq");
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string2, scriptValue2));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                arrayList.add(scriptValue2);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        Teleporters.openDestinationMenu(builder2);
        return ScriptValue.NULL;
    }

    public static ScriptValue openDestinationMenu(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            String string = "teleporters_freq";
            String string2 = "string";
            if (scriptValue2 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                object3 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object3;
        builder.val("freq", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Menu");
        if (scriptValue4 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)36.0));
            arrayList.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gold>Teleporters: <yellow>"), (ScriptValue)scriptValue3));
            object2 = PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object2;
        builder.val("m", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq", scriptValue3);
            ScriptValue scriptValue7 = Teleporters.freqKey(builder2);
            String string = "string";
            if (scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object5);
                object = polyClassServer.tm$6_get_typed(scriptValue7.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue7);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object;
        builder.val("existing", scriptValue8);
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("entries", (ScriptValue)array);
        List list = ScriptProgram.resolveForRows((String)"split(existing, \";\")", (ScriptContext)scriptContext, (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("entry", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptContext.getClassOrVar("entry"), (String)"") ^ true)) continue;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptContext.getClassOrVar("entries"));
                arrayList2.add(scriptContext.getClassOrVar("entry"));
                ScriptValue scriptValue9 = ScriptFormula.callBuiltin((String)"push", arrayList2, (ScriptContext)scriptContext);
                builder.val("entries", scriptValue9);
            }
        }
        double d = 0.0;
        ScriptValue scriptValue10 = ScriptValue.of((double)0.0);
        builder.val("i", scriptValue10);
        List list2 = ScriptProgram.resolveForRows((String)"entries", (ScriptContext)scriptContext, (int)1);
        if (list2 != null) {
            for (ScriptValue[] scriptValueArray : list2) {
                Object object6;
                Object object7;
                Object object8;
                Object object9;
                Object object10;
                Object object11;
                Object object12;
                Object object13;
                Object object14;
                builder.val("entry", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                double d2 = scriptContext.getNum("i");
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptContext.getClassOrVar("DEST_SLOTS"));
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList3, (ScriptContext)scriptContext).asNum()) break;
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptContext.getClassOrVar("entry"));
                arrayList4.add(ScriptValue.of((String)","));
                ScriptValue scriptValue11 = ScriptFormula.callBuiltin((String)"split", arrayList4, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue11);
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("parts");
                if (scriptValue12 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(ScriptValue.of((double)0.0));
                    object14 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, arrayList5, (ScriptContext)scriptContext);
                } else {
                    object14 = ScriptValue.NULL;
                }
                builder3.val("world_name", object14);
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("parts");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(ScriptValue.of((double)1.0));
                    object13 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object13 = ScriptValue.NULL;
                }
                builder3.val("x", object13);
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("parts");
                if (scriptValue14 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add(ScriptValue.of((double)2.0));
                    object12 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, arrayList7, (ScriptContext)scriptContext);
                } else {
                    object12 = ScriptValue.NULL;
                }
                builder3.val("y", object12);
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("parts");
                if (scriptValue15 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(ScriptValue.of((double)3.0));
                    object11 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object11 = ScriptValue.NULL;
                }
                builder3.val("z", object11);
                ScriptValue scriptValue16 = Teleporters._signAliasAt(builder3);
                builder.val("alias", scriptValue16);
                if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue16, (String)"") ^ true) {
                    object10 = scriptValue16;
                } else {
                    ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                    if (scriptValue17 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                        arrayList9.add(ScriptValue.of((double)0.0));
                        object10 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, arrayList9, (ScriptContext)scriptContext);
                    } else {
                        object10 = ScriptValue.NULL;
                    }
                }
                ScriptValue scriptValue18 = object10;
                builder.val("display_name", scriptValue18);
                ArrayList<ScriptValue.Array> arrayList10 = new ArrayList<ScriptValue.Array>();
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue19 = ScriptValue.of((String)"<gray>");
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("parts");
                if (scriptValue20 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                    arrayList12.add(ScriptValue.of((double)1.0));
                    object9 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, arrayList12, (ScriptContext)scriptContext);
                } else {
                    object9 = ScriptValue.NULL;
                }
                ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue19, (ScriptValue)object9), (ScriptValue)ScriptValue.of((String)", "));
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("parts");
                if (scriptValue22 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                    arrayList13.add(ScriptValue.of((double)2.0));
                    object8 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, arrayList13, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                ScriptValue scriptValue23 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue21, (ScriptValue)object8), (ScriptValue)ScriptValue.of((String)", "));
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("parts");
                if (scriptValue24 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    arrayList14.add(ScriptValue.of((double)3.0));
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue24, arrayList14, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                arrayList11.add(ScriptFormula.addPolymorphic((ScriptValue)scriptValue23, (ScriptValue)object7));
                arrayList11.add(ScriptValue.of((String)"<dark_gray>Costs 1 Nether Star"));
                arrayList11.add(ScriptValue.of((String)""));
                arrayList11.add(ScriptValue.of((String)"<green>Click to teleport"));
                arrayList10.add(new ScriptValue.Array(arrayList11));
                ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                arrayList15.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<yellow>"), (ScriptValue)scriptValue18));
                ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                arrayList16.add(ScriptValue.of((String)"minecraft:ender_pearl"));
                arrayList16.add(ScriptValue.of((double)1.0));
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_lore", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin((String)"create_item", arrayList16, (ScriptContext)scriptContext), arrayList15, (ScriptContext)scriptContext), arrayList10, (ScriptContext)scriptContext);
                builder.val("icon", (ScriptValue)callSite);
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("m");
                if (scriptValue25 != ScriptValue.NULL) {
                    Object object15;
                    ArrayList<Object> arrayList17 = new ArrayList<Object>();
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("DEST_SLOTS");
                    if (scriptValue26 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList18 = new ArrayList<ScriptValue>();
                        arrayList18.add(scriptContext.getClassOrVar("i"));
                        object15 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue26, arrayList18, (ScriptContext)scriptContext);
                    } else {
                        object15 = ScriptValue.NULL;
                    }
                    arrayList17.add(object15);
                    arrayList17.add(callSite);
                    arrayList17.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"teleporters.pf:on_teleport_slot:"), (ScriptValue)scriptContext.getClassOrVar("i")));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue25, arrayList17, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue27 = object6;
                builder.val("m", scriptValue27);
                ScriptValue scriptValue28 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)ScriptValue.of((double)1.0));
                builder.val("i", scriptValue28);
            }
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)ScriptValue.of((double)0.0))) {
            Object object16;
            ArrayList<ScriptValue> arrayList19 = new ArrayList<ScriptValue>();
            arrayList19.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<red>No teleporters found on '"), (ScriptValue)scriptValue3), (ScriptValue)ScriptValue.of((String)"'")));
            ArrayList<ScriptValue> arrayList20 = new ArrayList<ScriptValue>();
            arrayList20.add(ScriptValue.of((String)"minecraft:barrier"));
            arrayList20.add(ScriptValue.of((double)1.0));
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin((String)"create_item", arrayList20, (ScriptContext)scriptContext), arrayList19, (ScriptContext)scriptContext);
            builder.val("filler", (ScriptValue)callSite);
            ScriptValue scriptValue29 = scriptContext.getClassOrVar("m");
            if (scriptValue29 != ScriptValue.NULL) {
                ArrayList<Object> arrayList21 = new ArrayList<Object>();
                arrayList21.add(ScriptValue.of((double)13.0));
                arrayList21.add(callSite);
                object16 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue29, arrayList21, (ScriptContext)scriptContext);
            } else {
                object16 = ScriptValue.NULL;
            }
            ScriptValue scriptValue30 = object16;
            builder.val("m", scriptValue30);
        }
        if ((scriptValue = scriptContext.getClassOrVar("m")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList22 = new ArrayList<ScriptValue>();
            arrayList22.add(scriptContext.getClassOrVar("Player"));
            v18 = PolyDispatch.bootstrapCall("memberCall", "open", (ScriptValue)scriptValue, arrayList22, (ScriptContext)scriptContext);
        } else {
            v18 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue onTeleportSlot(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = var1_1.getClassOrVar("Player");
        if (var2_2 != ScriptValue.NULL) {
            var3_3 = "teleporters_freq";
            var4_4 = "string";
            if (var2_2 instanceof ScriptValue.Obj && (var6_6 = (var5_5 = (ScriptValue.Obj)var2_2).instance()) != null && !(var6_6 instanceof PolyClass) && var5_5.typeName().equals("Player")) {
                var7_7 = new PolyClassPlayer(var6_6);
                v0 /* !! */  = var7_7.tm$12_get_typed(var3_3, var4_4);
            } else {
                var8_8 = new ArrayList<ScriptValue>();
                var8_8.add(ScriptValue.of((String)var3_3));
                var8_8.add(ScriptValue.of((String)var4_4));
                v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var2_2, var8_8, (ScriptContext)var1_1);
            }
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var9_9 = v0 /* !! */ ;
        var0.val("freq", var9_9);
        var10_10 = var1_1.getClassOrVar("Server");
        if (var10_10 != ScriptValue.NULL) {
            var12_11 = ScriptContext.builder().copyFrom(var1_1);
            var12_11.val("freq", var9_9);
            var11_12 = Teleporters.freqKey(var12_11);
            var13_13 = "string";
            if (var10_10 instanceof ScriptValue.Obj && (var15_15 = (var14_14 = (ScriptValue.Obj)var10_10).instance()) != null && !(var15_15 instanceof PolyClass) && var14_14.typeName().equals("Server")) {
                var16_16 = new PolyClassServer(var15_15);
                v1 /* !! */  = var16_16.tm$6_get_typed(var11_12.asStr(), var13_13);
            } else {
                var17_17 = new ArrayList<ScriptValue>();
                var17_17.add(var11_12);
                var17_17.add(ScriptValue.of((String)var13_13));
                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var10_10, var17_17, (ScriptContext)var1_1);
            }
        } else {
            v1 /* !! */  = ScriptValue.NULL;
        }
        var18_18 = v1 /* !! */ ;
        var0.val("existing", var18_18);
        var19_19 = new ArrayList<E>();
        var20_20 = new ScriptValue.Array(var19_19);
        var0.val("entries", (ScriptValue)var20_20);
        var21_21 = ScriptProgram.resolveForRows((String)"split(existing, \";\")", (ScriptContext)var1_1, (int)1);
        if (var21_21 != null) {
            for (ScriptValue[] var23_23 : var21_21) {
                var0.val("entry", var23_23.length > 0 ? var23_23[0] : ScriptValue.NULL);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)var1_1.getClassOrVar("entry"), (String)"") ^ true)) continue;
                var24_24 = new ArrayList<ScriptValue>();
                var24_24.add(var1_1.getClassOrVar("entries"));
                var24_24.add(var1_1.getClassOrVar("entry"));
                var25_25 = ScriptFormula.callBuiltin((String)"push", var24_24, (ScriptContext)var1_1);
                var0.val("entries", var25_25);
            }
        }
        var26_26 = new ArrayList<ScriptValue>();
        var26_26.add(var1_1.getClassOrVar("idx"));
        var27_27 = ScriptFormula.callBuiltin((String)"int", var26_26, (ScriptContext)var1_1);
        var0.val("i", var27_27);
        if (var27_27.asNum() < 0.0) ** GOTO lbl-1000
        v2 = var27_27.asNum();
        var28_28 = new ArrayList<ScriptValue>();
        var28_28.add(var1_1.getClassOrVar("entries"));
        if (!(v2 >= ScriptFormula.callBuiltin((String)"len", var28_28, (ScriptContext)var1_1).asNum())) {
            v3 = false;
        } else lbl-1000:
        // 2 sources

        {
            v3 = true;
        }
        if (v3) {
            var29_29 = var1_1.getClassOrVar("Player");
            if (var29_29 != ScriptValue.NULL) {
                var30_30 = "<red>\u2718 <white>That destination is no longer available.";
                if (var29_29 instanceof ScriptValue.Obj && (var32_32 = (var31_31 = (ScriptValue.Obj)var29_29).instance()) != null && !(var32_32 instanceof PolyClass) && var31_31.typeName().equals("Player")) {
                    var33_33 = new PolyClassPlayer(var32_32);
                    v4 /* !! */  = ScriptValue.of((boolean)var33_33.tm$42_send_message(var30_30));
                } else {
                    var34_34 = new ArrayList<ScriptValue>();
                    var34_34.add(ScriptValue.of((String)var30_30));
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var29_29, var34_34, (ScriptContext)var1_1);
                }
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var35_35 = var1_1.getClassOrVar("Player");
        if (var35_35 != ScriptValue.NULL) {
            var36_36 = var1_1.getClassOrVar("TELEPORT_ITEM");
            var37_37 = 1.0;
            if (var35_35 instanceof ScriptValue.Obj && (var40_39 = (var39_38 = (ScriptValue.Obj)var35_35).instance()) != null && !(var40_39 instanceof PolyClass) && var39_38.typeName().equals("Player")) {
                var41_40 = new PolyClassPlayer(var40_39);
                v5 /* !! */  = ScriptValue.of((boolean)var41_40.tm$40_has_item(var36_36.asStr(), var37_37));
            } else {
                var42_41 = new ArrayList<ScriptValue>();
                var42_41.add(var36_36);
                var42_41.add(ScriptValue.of((double)var37_37));
                v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has_item", (ScriptValue)var35_35, var42_41, (ScriptContext)var1_1);
            }
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        if (v5 /* !! */ .asBool() ^ true) {
            var43_42 = var1_1.getClassOrVar("Player");
            if (var43_42 != ScriptValue.NULL) {
                var44_43 = "<red>\u2718 <white>You need a Nether Star to use a teleporter this way.";
                if (var43_42 instanceof ScriptValue.Obj && (var46_45 = (var45_44 = (ScriptValue.Obj)var43_42).instance()) != null && !(var46_45 instanceof PolyClass) && var45_44.typeName().equals("Player")) {
                    var47_46 = new PolyClassPlayer(var46_45);
                    v6 /* !! */  = ScriptValue.of((boolean)var47_46.tm$42_send_message(var44_43));
                } else {
                    var48_47 = new ArrayList<ScriptValue>();
                    var48_47.add(ScriptValue.of((String)var44_43));
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var43_42, var48_47, (ScriptContext)var1_1);
                }
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var49_48 = new ArrayList<ScriptValue>();
        var50_49 = var1_1.getClassOrVar("entries");
        if (var50_49 != ScriptValue.NULL) {
            var51_50 = new ArrayList<ScriptValue>();
            var51_50.add(var27_27);
            v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var50_49, var51_50, (ScriptContext)var1_1);
        } else {
            v7 /* !! */  = ScriptValue.NULL;
        }
        var49_48.add(v7 /* !! */ );
        var49_48.add(ScriptValue.of((String)","));
        var52_51 = ScriptFormula.callBuiltin((String)"split", var49_48, (ScriptContext)var1_1);
        var0.val("parts", var52_51);
        var53_52 = new ArrayList<ScriptValue>();
        var54_53 = var1_1.getClassOrVar("parts");
        if (var54_53 != ScriptValue.NULL) {
            var55_54 = new ArrayList<ScriptValue>();
            var55_54.add(ScriptValue.of((double)0.0));
            v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var54_53, var55_54, (ScriptContext)var1_1);
        } else {
            v8 /* !! */  = ScriptValue.NULL;
        }
        var53_52.add(v8 /* !! */ );
        var56_55 = ScriptFormula.callBuiltin((String)"world", var53_52, (ScriptContext)var1_1);
        var0.val("target_world", var56_55);
        var57_56 = new ArrayList<ScriptValue>();
        var57_56.add(var56_55);
        if (ScriptFormula.callBuiltin((String)"is_empty", var57_56, (ScriptContext)var1_1).asBool()) {
            var58_57 = var1_1.getClassOrVar("Player");
            if (var58_57 != ScriptValue.NULL) {
                var59_58 = "<red>\u2718 <white>That destination's world is not currently loaded.";
                if (var58_57 instanceof ScriptValue.Obj && (var61_60 = (var60_59 = (ScriptValue.Obj)var58_57).instance()) != null && !(var61_60 instanceof PolyClass) && var60_59.typeName().equals("Player")) {
                    var62_61 = new PolyClassPlayer(var61_60);
                    v9 /* !! */  = ScriptValue.of((boolean)var62_61.tm$42_send_message(var59_58));
                } else {
                    var63_62 = new ArrayList<ScriptValue>();
                    var63_62.add(ScriptValue.of((String)var59_58));
                    v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var58_57, var63_62, (ScriptContext)var1_1);
                }
            } else {
                v9 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var64_63 = var1_1.getClassOrVar("target_world");
        if (var64_63 != ScriptValue.NULL) {
            var65_64 = new ArrayList<ScriptValue>();
            var66_65 = new ArrayList<ScriptValue>();
            var67_66 = var1_1.getClassOrVar("parts");
            if (var67_66 != ScriptValue.NULL) {
                var68_67 = new ArrayList<ScriptValue>();
                var68_67.add(ScriptValue.of((double)1.0));
                v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var67_66, var68_67, (ScriptContext)var1_1);
            } else {
                v10 /* !! */  = ScriptValue.NULL;
            }
            var66_65.add(v10 /* !! */ );
            var65_64.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var66_65, (ScriptContext)var1_1), (ScriptValue)ScriptValue.of((double)0.5)));
            var69_68 = new ArrayList<ScriptValue>();
            var70_69 = var1_1.getClassOrVar("parts");
            if (var70_69 != ScriptValue.NULL) {
                var71_70 = new ArrayList<ScriptValue>();
                var71_70.add(ScriptValue.of((double)2.0));
                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var70_69, var71_70, (ScriptContext)var1_1);
            } else {
                v11 /* !! */  = ScriptValue.NULL;
            }
            var69_68.add(v11 /* !! */ );
            var65_64.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var69_68, (ScriptContext)var1_1), (ScriptValue)ScriptValue.of((double)1.5)));
            var72_71 = new ArrayList<ScriptValue>();
            var73_72 = var1_1.getClassOrVar("parts");
            if (var73_72 != ScriptValue.NULL) {
                var74_73 = new ArrayList<ScriptValue>();
                var74_73.add(ScriptValue.of((double)3.0));
                v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var73_72, var74_73, (ScriptContext)var1_1);
            } else {
                v12 /* !! */  = ScriptValue.NULL;
            }
            var72_71.add(v12 /* !! */ );
            var65_64.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var72_71, (ScriptContext)var1_1), (ScriptValue)ScriptValue.of((double)0.5)));
            v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)var64_63, var65_64, (ScriptContext)var1_1);
        } else {
            v13 /* !! */  = ScriptValue.NULL;
        }
        var75_74 = v13 /* !! */ ;
        var0.val("loc", var75_74);
        var76_75 = var1_1.getClassOrVar("Player");
        if (var76_75 != ScriptValue.NULL) {
            var77_76 = var1_1.getClassOrVar("TELEPORT_ITEM");
            var78_77 = 1.0;
            if (var76_75 instanceof ScriptValue.Obj && (var81_79 = (var80_78 = (ScriptValue.Obj)var76_75).instance()) != null && !(var81_79 instanceof PolyClass) && var80_78.typeName().equals("Player")) {
                var82_80 = new PolyClassPlayer(var81_79);
                v14 /* !! */  = ScriptValue.of((boolean)var82_80.tm$16_consume_item(var77_76.asStr(), var78_77));
            } else {
                var83_81 = new ArrayList<ScriptValue>();
                var83_81.add(var77_76);
                var83_81.add(ScriptValue.of((double)var78_77));
                v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "consume_item", (ScriptValue)var76_75, var83_81, (ScriptContext)var1_1);
            }
        } else {
            v14 /* !! */  = ScriptValue.NULL;
        }
        if (v14 /* !! */ .asBool() ^ true) {
            var84_82 = var1_1.getClassOrVar("Player");
            if (var84_82 != ScriptValue.NULL) {
                var85_83 = "<red>\u2718 <white>You need a Nether Star to use a teleporter this way.";
                if (var84_82 instanceof ScriptValue.Obj && (var87_85 = (var86_84 = (ScriptValue.Obj)var84_82).instance()) != null && !(var87_85 instanceof PolyClass) && var86_84.typeName().equals("Player")) {
                    var88_86 = new PolyClassPlayer(var87_85);
                    v15 /* !! */  = ScriptValue.of((boolean)var88_86.tm$42_send_message(var85_83));
                } else {
                    var89_87 = new ArrayList<ScriptValue>();
                    var89_87.add(ScriptValue.of((String)var85_83));
                    v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var84_82, var89_87, (ScriptContext)var1_1);
                }
            } else {
                v15 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var90_88 = var1_1.getClassOrVar("Player");
        if (var90_88 != ScriptValue.NULL) {
            var91_89 = var75_74;
            if (var90_88 instanceof ScriptValue.Obj && (var93_91 = (var92_90 = (ScriptValue.Obj)var90_88).instance()) != null && !(var93_91 instanceof PolyClass) && var92_90.typeName().equals("Player")) {
                var94_92 = new PolyClassPlayer(var93_91);
                v16 /* !! */  = ScriptValue.of((boolean)var94_92.tm$24_teleport_to(var91_89));
            } else {
                var95_93 = new ArrayList<ScriptValue>();
                var95_93.add(var91_89);
                v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)var90_88, var95_93, (ScriptContext)var1_1);
            }
        } else {
            v16 /* !! */  = ScriptValue.NULL;
        }
        var96_94 = var1_1.getClassOrVar("Player");
        if (var96_94 != ScriptValue.NULL) {
            var97_95 = "<green>\u2714 <white>Teleported using a Nether Star.";
            if (var96_94 instanceof ScriptValue.Obj && (var99_97 = (var98_96 = (ScriptValue.Obj)var96_94).instance()) != null && !(var99_97 instanceof PolyClass) && var98_96.typeName().equals("Player")) {
                var100_98 = new PolyClassPlayer(var99_97);
                v17 /* !! */  = ScriptValue.of((boolean)var100_98.tm$42_send_message(var97_95));
            } else {
                var101_99 = new ArrayList<ScriptValue>();
                var101_99.add(ScriptValue.of((String)var97_95));
                v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var96_94, var101_99, (ScriptContext)var1_1);
            }
        } else {
            v17 /* !! */  = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = ScriptValue.of((String)"minecraft:nether_star");
        builder.val("TELEPORT_ITEM", scriptValue);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(ScriptValue.of((double)10.0));
        arrayList.add(ScriptValue.of((double)11.0));
        arrayList.add(ScriptValue.of((double)12.0));
        arrayList.add(ScriptValue.of((double)13.0));
        arrayList.add(ScriptValue.of((double)14.0));
        arrayList.add(ScriptValue.of((double)15.0));
        arrayList.add(ScriptValue.of((double)16.0));
        arrayList.add(ScriptValue.of((double)19.0));
        arrayList.add(ScriptValue.of((double)20.0));
        arrayList.add(ScriptValue.of((double)21.0));
        arrayList.add(ScriptValue.of((double)22.0));
        arrayList.add(ScriptValue.of((double)23.0));
        arrayList.add(ScriptValue.of((double)24.0));
        arrayList.add(ScriptValue.of((double)25.0));
        arrayList.add(ScriptValue.of((double)28.0));
        arrayList.add(ScriptValue.of((double)29.0));
        arrayList.add(ScriptValue.of((double)30.0));
        arrayList.add(ScriptValue.of((double)31.0));
        arrayList.add(ScriptValue.of((double)32.0));
        arrayList.add(ScriptValue.of((double)33.0));
        arrayList.add(ScriptValue.of((double)34.0));
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("DEST_SLOTS", (ScriptValue)array);
    }
}
