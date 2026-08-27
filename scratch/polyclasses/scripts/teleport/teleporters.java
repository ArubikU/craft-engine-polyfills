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
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
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
        return ScriptValue.of((String)("teleporter_freq_" + scriptContext.getStr("freq")));
    }

    public static ScriptValue _signAliasAt(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)scriptContext.getClassOrVar("world_name"), (ScriptContext)scriptContext);
        builder.val("tw", scriptValue);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "");
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("tw");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue2, (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("x"), (ScriptContext)scriptContext), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("y"), (ScriptContext)scriptContext), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("z"), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Teleporters.class, 1)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("center", scriptValue3);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "north"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "south"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "east"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "west"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "up"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "down"));
        List list = ScriptProgram.elementsOf((ScriptValue)new ScriptValue.Array(arrayList));
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("b");
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("line");
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("dir", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("center");
                ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue7, (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("b", scriptValue8);
                scriptValue4 = scriptValue8;
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "metadata_type", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"SignMetadata")) continue;
                List list2 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "lines", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "front", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "get_metadata", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
                if (list2 != null) {
                    for (ScriptValue scriptValue9 : list2) {
                        builder.val("line", scriptValue9);
                        if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue9, (String)"") ^ true)) continue;
                        return scriptValue9;
                    }
                }
                return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "");
            }
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "");
    }

    public static ScriptValue openFrequencyDialog(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Dialog");
        Object object2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "Teleporter Frequency")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue2 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "value");
        ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "Frequency");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "teleporters_freq";
            String string2 = "string";
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_notice", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "input_text", (ScriptValue)object2, (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)object, (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Machine"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "Browse")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "teleporters.pf:on_frequency_submit")), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
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
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "teleporters_freq";
            String string2 = "string";
            if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object2 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("freq", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Menu");
        ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 36.0)), (ScriptValue)ScriptValue.of((String)("<gold>Teleporters: <yellow>" + scriptValue3.asStr())), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("m", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq", scriptValue3);
            ScriptValue scriptValue7 = Teleporters.freqKey(builder2);
            String string = "string";
            if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object4);
                object = polyClassServer.tm$6_get_typed(scriptValue7.asStr(), string);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object;
        builder.val("existing", scriptValue8);
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("entries", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ";")), (ScriptContext)scriptContext));
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue9 : list) {
                builder.val("entry", scriptValue9);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue9, (String)"") ^ true)) continue;
                ScriptValue scriptValue10 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
                builder.val("entries", scriptValue10);
                array2 = scriptValue10;
            }
        }
        double d = 0.0;
        ScriptValue scriptValue11 = ScriptValue.of((double)0.0);
        builder.val("i", scriptValue11);
        List list2 = ScriptProgram.elementsOf((ScriptValue)array2);
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("parts");
        Object object5 = scriptContext.getClassOrVar("icon");
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("alias");
        ScriptValue scriptValue14 = ScriptValue.of((double)d);
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("display_name");
        ScriptValue scriptValue16 = scriptValue5;
        if (list2 != null) {
            for (ScriptValue scriptValue17 : list2) {
                ScriptValue scriptValue18;
                ScriptValue scriptValue19;
                builder.val("entry", scriptValue17);
                if (scriptValue14.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptContext.getClassOrVar("DEST_SLOTS"), (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue20 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ",")), (ScriptContext)scriptContext);
                builder.val("parts", scriptValue20);
                scriptValue12 = scriptValue20;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue21 = scriptContext.getClassOrVar("parts");
                builder3.val("world_name", (ScriptValue)(scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("parts");
                builder3.val("x", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("parts");
                builder3.val("y", (ScriptValue)(scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue23, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("parts");
                builder3.val("z", (ScriptValue)(scriptValue24 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue24, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue25 = Teleporters._signAliasAt(builder3);
                builder.val("alias", scriptValue25);
                scriptValue13 = scriptValue25;
                ScriptValue scriptValue26 = ScriptFormula.valuesEqualStr((ScriptValue)scriptValue13, (String)"") ^ true ? scriptValue13 : ((scriptValue19 = scriptContext.getClassOrVar("parts")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("display_name", scriptValue26);
                scriptValue15 = scriptValue26;
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin2((String)"create_item", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "minecraft:ender_pearl")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((String)("<yellow>" + scriptValue15.asStr())), (ScriptContext)scriptContext);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue27 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue28 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue29 = scriptContext.getClassOrVar("parts");
                arrayList2.add(ScriptValue.of((String)("<gray>" + (scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue27, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ", " + (scriptValue28 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue28, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ", " + (scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "<dark_gray>Costs 1 Nether Star"));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ""));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "<green>Click to teleport"));
                CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "with_lore", (ScriptValue)callSite, (ScriptValue)new ScriptValue.Array(arrayList2), (ScriptContext)scriptContext);
                builder.val("icon", (ScriptValue)callSite2);
                object5 = callSite2;
                ScriptValue scriptValue30 = scriptContext.getClassOrVar("m");
                ScriptValue scriptValue31 = scriptValue30 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue30, (ScriptValue)((scriptValue18 = scriptContext.getClassOrVar("DEST_SLOTS")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)object5, (ScriptValue)ScriptValue.of((String)("teleporters.pf:on_teleport_slot:" + scriptValue14.asStr())), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("m", scriptValue31);
                scriptValue16 = scriptValue31;
                ScriptValue scriptValue32 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)));
                builder.val("i", scriptValue32);
                scriptValue14 = scriptValue32;
            }
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)))) {
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin2((String)"create_item", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "minecraft:barrier")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((String)("<red>No teleporters found on '" + scriptValue3.asStr() + "'")), (ScriptContext)scriptContext);
            builder.val("filler", (ScriptValue)callSite);
            ScriptValue scriptValue33 = scriptContext.getClassOrVar("m");
            ScriptValue scriptValue34 = scriptValue33 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue33, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 13.0)), (ScriptValue)callSite, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("m", scriptValue34);
        }
        Object object6 = (scriptValue = scriptContext.getClassOrVar("m")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onTeleportSlot(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            String string = "teleporters_freq";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                object5 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
            }
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object5;
        builder.val("freq", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq", scriptValue2);
            ScriptValue scriptValue4 = Teleporters.freqKey(builder2);
            String string = "string";
            if (scriptValue3 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object7);
                object4 = polyClassServer.tm$6_get_typed(scriptValue4.asStr(), string);
            } else {
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object4;
        builder.val("existing", scriptValue5);
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("entries", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ";")), (ScriptContext)scriptContext));
        ScriptValue.Array array2 = array;
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("entry", scriptValue6);
                if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"") ^ true)) continue;
                ScriptValue scriptValue7 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)array2, (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
                builder.val("entries", scriptValue7);
                array2 = scriptValue7;
            }
        }
        ScriptValue scriptValue8 = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("idx"), (ScriptContext)scriptContext);
        builder.val("i", scriptValue8);
        if (scriptValue8.asNum() < 0.0 || scriptValue8.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)array2, (ScriptContext)scriptContext).asNum()) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                String string = "<red>\u2718 <white>That destination is no longer available.";
                if (scriptValue9 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object8);
                    v2 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object9;
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("TELEPORT_ITEM");
            double d = 1.0;
            if (scriptValue10 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object9);
                object3 = ScriptValue.of((boolean)polyClassPlayer.tm$40_has_item(scriptValue11.asStr(), d));
            } else {
                object3 = PolyDispatch.bootstrapCall("memberCall", "has_item", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        if (object3.asBool() ^ true) {
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("Player");
            if (scriptValue12 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                String string = "<red>\u2718 <white>You need a Nether Star to use a teleporter this way.";
                if (scriptValue12 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object10);
                    v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("entries");
        ScriptValue scriptValue14 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ",")), (ScriptContext)scriptContext);
        builder.val("parts", scriptValue14);
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("parts");
        ScriptValue scriptValue16 = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)(scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("target_world", scriptValue16);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue16, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("Player");
            if (scriptValue17 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                String string = "<red>\u2718 <white>That destination's world is not currently loaded.";
                if (scriptValue17 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object11);
                    v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v5 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("target_world");
        if (scriptValue18 != ScriptValue.NULL) {
            ScriptValue scriptValue19 = scriptContext.getClassOrVar("parts");
            ScriptValue scriptValue20 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.5)));
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("parts");
            ScriptValue scriptValue22 = scriptContext.getClassOrVar("parts");
            object2 = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue18, (ScriptValue)scriptValue20, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.5))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.5))), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue23 = object2;
        builder.val("loc", scriptValue23);
        ScriptValue scriptValue24 = scriptContext.getClassOrVar("Player");
        if (scriptValue24 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object12;
            ScriptValue scriptValue25 = scriptContext.getClassOrVar("TELEPORT_ITEM");
            double d = 1.0;
            if (scriptValue24 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object12);
                object = ScriptValue.of((boolean)polyClassPlayer.tm$16_consume_item(scriptValue25.asStr(), d));
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "consume_item", (ScriptValue)scriptValue24, (ScriptValue)scriptValue25, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool() ^ true) {
            ScriptValue scriptValue26 = scriptContext.getClassOrVar("Player");
            if (scriptValue26 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object13;
                String string = "<red>\u2718 <white>You need a Nether Star to use a teleporter this way.";
                if (scriptValue26 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object13);
                    v9 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Player");
        if (scriptValue27 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object14;
            ScriptValue scriptValue28 = scriptValue23;
            if (scriptValue27 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object14);
                v10 = ScriptValue.of((boolean)polyClassPlayer.tm$24_teleport_to(scriptValue28));
            } else {
                v10 = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue27, (ScriptValue)scriptValue28, (ScriptContext)scriptContext);
            }
        } else {
            v10 = ScriptValue.NULL;
        }
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("Player");
        if (scriptValue29 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object15;
            String string = "<green>\u2714 <white>Teleported using a Nether Star.";
            if (scriptValue29 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object15);
                v11 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                v11 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue29, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v11 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "minecraft:nether_star");
        builder.val("TELEPORT_ITEM", scriptValue);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 10.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 11.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 12.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 13.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 14.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 15.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 16.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 19.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 20.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 21.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 22.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 23.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 24.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 25.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 28.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 29.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 30.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 31.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 32.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 33.0));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 34.0));
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("DEST_SLOTS", (ScriptValue)array);
        FILE_SCOPE = builder.build();
    }
}
