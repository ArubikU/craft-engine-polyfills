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
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

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
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add(ScriptValue.of((String)"north"));
        arrayList7.add(ScriptValue.of((String)"south"));
        arrayList7.add(ScriptValue.of((String)"east"));
        arrayList7.add(ScriptValue.of((String)"west"));
        arrayList7.add(ScriptValue.of((String)"up"));
        arrayList7.add(ScriptValue.of((String)"down"));
        List list = ScriptProgram.rowsOf((ScriptValue)new ScriptValue.Array(arrayList7), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                Object object2;
                builder.val("dir", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("center");
                if (scriptValue4 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(scriptContext.getClassOrVar("dir"));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue4, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue5 = object2;
                builder.val("b", scriptValue5);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("b");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "metadata_type", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"SignMetadata")) continue;
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("b");
                List list2 = ScriptProgram.rowsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "lines", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "front", (ScriptValue)(scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "get_metadata", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptContext)scriptContext), (int)1);
                if (list2 != null) {
                    for (ScriptValue[] scriptValueArray2 : list2) {
                        builder.val("line", scriptValueArray2.length > 0 ? scriptValueArray2[0] : ScriptValue.NULL);
                        if (!(scriptContext.getStr("line").equals("") ^ true)) continue;
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
        if (scriptContext.getStr("freq").equals("")) {
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
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptValue8);
        arrayList2.add(ScriptValue.of((String)";"));
        List list = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList2, (ScriptContext)scriptContext), (int)1);
        if (list != null) {
            for (ScriptValue[] scriptValueArray : list) {
                builder.val("entry", scriptValueArray.length > 0 ? scriptValueArray[0] : ScriptValue.NULL);
                if (!(scriptContext.getStr("entry").equals("") ^ true)) continue;
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptContext.getClassOrVar("entries"));
                arrayList3.add(scriptContext.getClassOrVar("entry"));
                ScriptValue scriptValue9 = ScriptFormula.callBuiltin((String)"push", arrayList3, (ScriptContext)scriptContext);
                builder.val("entries", scriptValue9);
            }
        }
        double d = 0.0;
        ScriptValue scriptValue10 = ScriptValue.of((double)0.0);
        builder.val("i", scriptValue10);
        List list2 = ScriptProgram.rowsOf((ScriptValue)scriptContext.getClassOrVar("entries"), (int)1);
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
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptContext.getClassOrVar("DEST_SLOTS"));
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList4, (ScriptContext)scriptContext).asNum()) break;
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(scriptContext.getClassOrVar("entry"));
                arrayList5.add(ScriptValue.of((String)","));
                ScriptValue scriptValue11 = ScriptFormula.callBuiltin((String)"split", arrayList5, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue11);
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue12 = scriptContext.getClassOrVar("parts");
                if (scriptValue12 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(ScriptValue.of((double)0.0));
                    object14 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object14 = ScriptValue.NULL;
                }
                builder3.val("world_name", object14);
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("parts");
                if (scriptValue13 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add(ScriptValue.of((double)1.0));
                    object13 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, arrayList7, (ScriptContext)scriptContext);
                } else {
                    object13 = ScriptValue.NULL;
                }
                builder3.val("x", object13);
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("parts");
                if (scriptValue14 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(ScriptValue.of((double)2.0));
                    object12 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, arrayList8, (ScriptContext)scriptContext);
                } else {
                    object12 = ScriptValue.NULL;
                }
                builder3.val("y", object12);
                ScriptValue scriptValue15 = scriptContext.getClassOrVar("parts");
                if (scriptValue15 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(ScriptValue.of((double)3.0));
                    object11 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, arrayList9, (ScriptContext)scriptContext);
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
                        ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                        arrayList10.add(ScriptValue.of((double)0.0));
                        object10 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, arrayList10, (ScriptContext)scriptContext);
                    } else {
                        object10 = ScriptValue.NULL;
                    }
                }
                ScriptValue scriptValue18 = object10;
                builder.val("display_name", scriptValue18);
                ArrayList<ScriptValue.Array> arrayList11 = new ArrayList<ScriptValue.Array>();
                ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue19 = ScriptValue.of((String)"<gray>");
                ScriptValue scriptValue20 = scriptContext.getClassOrVar("parts");
                if (scriptValue20 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                    arrayList13.add(ScriptValue.of((double)1.0));
                    object9 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, arrayList13, (ScriptContext)scriptContext);
                } else {
                    object9 = ScriptValue.NULL;
                }
                ScriptValue scriptValue21 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue19, (ScriptValue)object9), (ScriptValue)ScriptValue.of((String)", "));
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("parts");
                if (scriptValue22 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                    arrayList14.add(ScriptValue.of((double)2.0));
                    object8 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, arrayList14, (ScriptContext)scriptContext);
                } else {
                    object8 = ScriptValue.NULL;
                }
                ScriptValue scriptValue23 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue21, (ScriptValue)object8), (ScriptValue)ScriptValue.of((String)", "));
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("parts");
                if (scriptValue24 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
                    arrayList15.add(ScriptValue.of((double)3.0));
                    object7 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue24, arrayList15, (ScriptContext)scriptContext);
                } else {
                    object7 = ScriptValue.NULL;
                }
                arrayList12.add(ScriptFormula.addPolymorphic((ScriptValue)scriptValue23, (ScriptValue)object7));
                arrayList12.add(ScriptValue.of((String)"<dark_gray>Costs 1 Nether Star"));
                arrayList12.add(ScriptValue.of((String)""));
                arrayList12.add(ScriptValue.of((String)"<green>Click to teleport"));
                arrayList11.add(new ScriptValue.Array(arrayList12));
                ArrayList<ScriptValue> arrayList16 = new ArrayList<ScriptValue>();
                arrayList16.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<yellow>"), (ScriptValue)scriptValue18));
                ArrayList<ScriptValue> arrayList17 = new ArrayList<ScriptValue>();
                arrayList17.add(ScriptValue.of((String)"minecraft:ender_pearl"));
                arrayList17.add(ScriptValue.of((double)1.0));
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_lore", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin((String)"create_item", arrayList17, (ScriptContext)scriptContext), arrayList16, (ScriptContext)scriptContext), arrayList11, (ScriptContext)scriptContext);
                builder.val("icon", (ScriptValue)callSite);
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("m");
                if (scriptValue25 != ScriptValue.NULL) {
                    Object object15;
                    ArrayList<Object> arrayList18 = new ArrayList<Object>();
                    ScriptValue scriptValue26 = scriptContext.getClassOrVar("DEST_SLOTS");
                    if (scriptValue26 != ScriptValue.NULL) {
                        ArrayList<ScriptValue> arrayList19 = new ArrayList<ScriptValue>();
                        arrayList19.add(scriptContext.getClassOrVar("i"));
                        object15 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue26, arrayList19, (ScriptContext)scriptContext);
                    } else {
                        object15 = ScriptValue.NULL;
                    }
                    arrayList18.add(object15);
                    arrayList18.add(callSite);
                    arrayList18.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"teleporters.pf:on_teleport_slot:"), (ScriptValue)scriptContext.getClassOrVar("i")));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue25, arrayList18, (ScriptContext)scriptContext);
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
            ArrayList<ScriptValue> arrayList20 = new ArrayList<ScriptValue>();
            arrayList20.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<red>No teleporters found on '"), (ScriptValue)scriptValue3), (ScriptValue)ScriptValue.of((String)"'")));
            ArrayList<ScriptValue> arrayList21 = new ArrayList<ScriptValue>();
            arrayList21.add(ScriptValue.of((String)"minecraft:barrier"));
            arrayList21.add(ScriptValue.of((double)1.0));
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin((String)"create_item", arrayList21, (ScriptContext)scriptContext), arrayList20, (ScriptContext)scriptContext);
            builder.val("filler", (ScriptValue)callSite);
            ScriptValue scriptValue29 = scriptContext.getClassOrVar("m");
            if (scriptValue29 != ScriptValue.NULL) {
                ArrayList<Object> arrayList22 = new ArrayList<Object>();
                arrayList22.add(ScriptValue.of((double)13.0));
                arrayList22.add(callSite);
                object16 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue29, arrayList22, (ScriptContext)scriptContext);
            } else {
                object16 = ScriptValue.NULL;
            }
            ScriptValue scriptValue30 = object16;
            builder.val("m", scriptValue30);
        }
        if ((scriptValue = scriptContext.getClassOrVar("m")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList23 = new ArrayList<ScriptValue>();
            arrayList23.add(scriptContext.getClassOrVar("Player"));
            v18 = PolyDispatch.bootstrapCall("memberCall", "open", (ScriptValue)scriptValue, arrayList23, (ScriptContext)scriptContext);
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
        var24_21 = new ArrayList<ScriptValue>();
        var24_21.add(var18_18);
        var24_21.add(ScriptValue.of((String)";"));
        var21_22 = ScriptProgram.rowsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", var24_21, (ScriptContext)var1_1), (int)1);
        if (var21_22 != null) {
            for (ScriptValue[] var23_24 : var21_22) {
                var0.val("entry", var23_24.length > 0 ? var23_24[0] : ScriptValue.NULL);
                if (!(var1_1.getStr("entry").equals("") ^ true)) continue;
                var25_25 = new ArrayList<ScriptValue>();
                var25_25.add(var1_1.getClassOrVar("entries"));
                var25_25.add(var1_1.getClassOrVar("entry"));
                var26_26 = ScriptFormula.callBuiltin((String)"push", var25_25, (ScriptContext)var1_1);
                var0.val("entries", var26_26);
            }
        }
        var27_27 = new ArrayList<ScriptValue>();
        var27_27.add(var1_1.getClassOrVar("idx"));
        var28_28 = ScriptFormula.callBuiltin((String)"int", var27_27, (ScriptContext)var1_1);
        var0.val("i", var28_28);
        if (var28_28.asNum() < 0.0) ** GOTO lbl-1000
        v2 = var28_28.asNum();
        var29_29 = new ArrayList<ScriptValue>();
        var29_29.add(var1_1.getClassOrVar("entries"));
        if (!(v2 >= ScriptFormula.callBuiltin((String)"len", var29_29, (ScriptContext)var1_1).asNum())) {
            v3 = false;
        } else lbl-1000:
        // 2 sources

        {
            v3 = true;
        }
        if (v3) {
            var30_30 = var1_1.getClassOrVar("Player");
            if (var30_30 != ScriptValue.NULL) {
                var31_31 = "<red>\u2718 <white>That destination is no longer available.";
                if (var30_30 instanceof ScriptValue.Obj && (var33_33 = (var32_32 = (ScriptValue.Obj)var30_30).instance()) != null && !(var33_33 instanceof PolyClass) && var32_32.typeName().equals("Player")) {
                    var34_34 = new PolyClassPlayer(var33_33);
                    v4 /* !! */  = ScriptValue.of((boolean)var34_34.tm$42_send_message(var31_31));
                } else {
                    var35_35 = new ArrayList<ScriptValue>();
                    var35_35.add(ScriptValue.of((String)var31_31));
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var30_30, var35_35, (ScriptContext)var1_1);
                }
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var36_36 = var1_1.getClassOrVar("Player");
        if (var36_36 != ScriptValue.NULL) {
            var37_37 = var1_1.getClassOrVar("TELEPORT_ITEM");
            var38_38 = 1.0;
            if (var36_36 instanceof ScriptValue.Obj && (var41_40 = (var40_39 = (ScriptValue.Obj)var36_36).instance()) != null && !(var41_40 instanceof PolyClass) && var40_39.typeName().equals("Player")) {
                var42_41 = new PolyClassPlayer(var41_40);
                v5 /* !! */  = ScriptValue.of((boolean)var42_41.tm$40_has_item(var37_37.asStr(), var38_38));
            } else {
                var43_42 = new ArrayList<ScriptValue>();
                var43_42.add(var37_37);
                var43_42.add(ScriptValue.of((double)var38_38));
                v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has_item", (ScriptValue)var36_36, var43_42, (ScriptContext)var1_1);
            }
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        if (v5 /* !! */ .asBool() ^ true) {
            var44_43 = var1_1.getClassOrVar("Player");
            if (var44_43 != ScriptValue.NULL) {
                var45_44 = "<red>\u2718 <white>You need a Nether Star to use a teleporter this way.";
                if (var44_43 instanceof ScriptValue.Obj && (var47_46 = (var46_45 = (ScriptValue.Obj)var44_43).instance()) != null && !(var47_46 instanceof PolyClass) && var46_45.typeName().equals("Player")) {
                    var48_47 = new PolyClassPlayer(var47_46);
                    v6 /* !! */  = ScriptValue.of((boolean)var48_47.tm$42_send_message(var45_44));
                } else {
                    var49_48 = new ArrayList<ScriptValue>();
                    var49_48.add(ScriptValue.of((String)var45_44));
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var44_43, var49_48, (ScriptContext)var1_1);
                }
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var50_49 = new ArrayList<ScriptValue>();
        var51_50 = var1_1.getClassOrVar("entries");
        if (var51_50 != ScriptValue.NULL) {
            var52_51 = new ArrayList<ScriptValue>();
            var52_51.add(var28_28);
            v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var51_50, var52_51, (ScriptContext)var1_1);
        } else {
            v7 /* !! */  = ScriptValue.NULL;
        }
        var50_49.add(v7 /* !! */ );
        var50_49.add(ScriptValue.of((String)","));
        var53_52 = ScriptFormula.callBuiltin((String)"split", var50_49, (ScriptContext)var1_1);
        var0.val("parts", var53_52);
        var54_53 = new ArrayList<ScriptValue>();
        var55_54 = var1_1.getClassOrVar("parts");
        if (var55_54 != ScriptValue.NULL) {
            var56_55 = new ArrayList<ScriptValue>();
            var56_55.add(ScriptValue.of((double)0.0));
            v8 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var55_54, var56_55, (ScriptContext)var1_1);
        } else {
            v8 /* !! */  = ScriptValue.NULL;
        }
        var54_53.add(v8 /* !! */ );
        var57_56 = ScriptFormula.callBuiltin((String)"world", var54_53, (ScriptContext)var1_1);
        var0.val("target_world", var57_56);
        var58_57 = new ArrayList<ScriptValue>();
        var58_57.add(var57_56);
        if (ScriptFormula.callBuiltin((String)"is_empty", var58_57, (ScriptContext)var1_1).asBool()) {
            var59_58 = var1_1.getClassOrVar("Player");
            if (var59_58 != ScriptValue.NULL) {
                var60_59 = "<red>\u2718 <white>That destination's world is not currently loaded.";
                if (var59_58 instanceof ScriptValue.Obj && (var62_61 = (var61_60 = (ScriptValue.Obj)var59_58).instance()) != null && !(var62_61 instanceof PolyClass) && var61_60.typeName().equals("Player")) {
                    var63_62 = new PolyClassPlayer(var62_61);
                    v9 /* !! */  = ScriptValue.of((boolean)var63_62.tm$42_send_message(var60_59));
                } else {
                    var64_63 = new ArrayList<ScriptValue>();
                    var64_63.add(ScriptValue.of((String)var60_59));
                    v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var59_58, var64_63, (ScriptContext)var1_1);
                }
            } else {
                v9 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var65_64 = var1_1.getClassOrVar("target_world");
        if (var65_64 != ScriptValue.NULL) {
            var66_65 = new ArrayList<ScriptValue>();
            var67_66 = new ArrayList<ScriptValue>();
            var68_67 = var1_1.getClassOrVar("parts");
            if (var68_67 != ScriptValue.NULL) {
                var69_68 = new ArrayList<ScriptValue>();
                var69_68.add(ScriptValue.of((double)1.0));
                v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var68_67, var69_68, (ScriptContext)var1_1);
            } else {
                v10 /* !! */  = ScriptValue.NULL;
            }
            var67_66.add(v10 /* !! */ );
            var66_65.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var67_66, (ScriptContext)var1_1), (ScriptValue)ScriptValue.of((double)0.5)));
            var70_69 = new ArrayList<ScriptValue>();
            var71_70 = var1_1.getClassOrVar("parts");
            if (var71_70 != ScriptValue.NULL) {
                var72_71 = new ArrayList<ScriptValue>();
                var72_71.add(ScriptValue.of((double)2.0));
                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var71_70, var72_71, (ScriptContext)var1_1);
            } else {
                v11 /* !! */  = ScriptValue.NULL;
            }
            var70_69.add(v11 /* !! */ );
            var66_65.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var70_69, (ScriptContext)var1_1), (ScriptValue)ScriptValue.of((double)1.5)));
            var73_72 = new ArrayList<ScriptValue>();
            var74_73 = var1_1.getClassOrVar("parts");
            if (var74_73 != ScriptValue.NULL) {
                var75_74 = new ArrayList<ScriptValue>();
                var75_74.add(ScriptValue.of((double)3.0));
                v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var74_73, var75_74, (ScriptContext)var1_1);
            } else {
                v12 /* !! */  = ScriptValue.NULL;
            }
            var73_72.add(v12 /* !! */ );
            var66_65.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var73_72, (ScriptContext)var1_1), (ScriptValue)ScriptValue.of((double)0.5)));
            v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)var65_64, var66_65, (ScriptContext)var1_1);
        } else {
            v13 /* !! */  = ScriptValue.NULL;
        }
        var76_75 = v13 /* !! */ ;
        var0.val("loc", var76_75);
        var77_76 = var1_1.getClassOrVar("Player");
        if (var77_76 != ScriptValue.NULL) {
            var78_77 = var1_1.getClassOrVar("TELEPORT_ITEM");
            var79_78 = 1.0;
            if (var77_76 instanceof ScriptValue.Obj && (var82_80 = (var81_79 = (ScriptValue.Obj)var77_76).instance()) != null && !(var82_80 instanceof PolyClass) && var81_79.typeName().equals("Player")) {
                var83_81 = new PolyClassPlayer(var82_80);
                v14 /* !! */  = ScriptValue.of((boolean)var83_81.tm$16_consume_item(var78_77.asStr(), var79_78));
            } else {
                var84_82 = new ArrayList<ScriptValue>();
                var84_82.add(var78_77);
                var84_82.add(ScriptValue.of((double)var79_78));
                v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "consume_item", (ScriptValue)var77_76, var84_82, (ScriptContext)var1_1);
            }
        } else {
            v14 /* !! */  = ScriptValue.NULL;
        }
        if (v14 /* !! */ .asBool() ^ true) {
            var85_83 = var1_1.getClassOrVar("Player");
            if (var85_83 != ScriptValue.NULL) {
                var86_84 = "<red>\u2718 <white>You need a Nether Star to use a teleporter this way.";
                if (var85_83 instanceof ScriptValue.Obj && (var88_86 = (var87_85 = (ScriptValue.Obj)var85_83).instance()) != null && !(var88_86 instanceof PolyClass) && var87_85.typeName().equals("Player")) {
                    var89_87 = new PolyClassPlayer(var88_86);
                    v15 /* !! */  = ScriptValue.of((boolean)var89_87.tm$42_send_message(var86_84));
                } else {
                    var90_88 = new ArrayList<ScriptValue>();
                    var90_88.add(ScriptValue.of((String)var86_84));
                    v15 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var85_83, var90_88, (ScriptContext)var1_1);
                }
            } else {
                v15 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var91_89 = var1_1.getClassOrVar("Player");
        if (var91_89 != ScriptValue.NULL) {
            var92_90 = var76_75;
            if (var91_89 instanceof ScriptValue.Obj && (var94_92 = (var93_91 = (ScriptValue.Obj)var91_89).instance()) != null && !(var94_92 instanceof PolyClass) && var93_91.typeName().equals("Player")) {
                var95_93 = new PolyClassPlayer(var94_92);
                v16 /* !! */  = ScriptValue.of((boolean)var95_93.tm$24_teleport_to(var92_90));
            } else {
                var96_94 = new ArrayList<ScriptValue>();
                var96_94.add(var92_90);
                v16 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)var91_89, var96_94, (ScriptContext)var1_1);
            }
        } else {
            v16 /* !! */  = ScriptValue.NULL;
        }
        var97_95 = var1_1.getClassOrVar("Player");
        if (var97_95 != ScriptValue.NULL) {
            var98_96 = "<green>\u2714 <white>Teleported using a Nether Star.";
            if (var97_95 instanceof ScriptValue.Obj && (var100_98 = (var99_97 = (ScriptValue.Obj)var97_95).instance()) != null && !(var100_98 instanceof PolyClass) && var99_97.typeName().equals("Player")) {
                var101_99 = new PolyClassPlayer(var100_98);
                v17 /* !! */  = ScriptValue.of((boolean)var101_99.tm$42_send_message(var98_96));
            } else {
                var102_100 = new ArrayList<ScriptValue>();
                var102_100.add(ScriptValue.of((String)var98_96));
                v17 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var97_95, var102_100, (ScriptContext)var1_1);
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
        FILE_SCOPE = builder.build();
    }
}
