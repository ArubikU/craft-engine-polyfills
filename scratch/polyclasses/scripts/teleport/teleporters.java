/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyClassServer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.teleport;

import dev.arubik.craftengine.script.PolyClassPlayer_v2;
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
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue, (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("x"), (ScriptContext)scriptContext), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("y"), (ScriptContext)scriptContext), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("z"), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Teleporters.class, 1)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("center", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "north"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "south"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "east"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "west"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "up"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "down"));
        List list = ScriptProgram.elementsOf((ScriptValue)new ScriptValue.Array(arrayList));
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("b");
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("line");
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("dir", scriptValue5);
                ScriptValue scriptValue6 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue2, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("b", scriptValue6);
                scriptValue3 = scriptValue6;
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "metadata_type", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"SignMetadata")) continue;
                List list2 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "lines", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "front", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "get_metadata", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
                if (list2 != null) {
                    for (ScriptValue scriptValue7 : list2) {
                        builder.val("line", scriptValue7);
                        if (!(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue7, (String)"") ^ true)) continue;
                        return scriptValue7;
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
            String string = "teleporters_freq";
            String string2 = "string";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue4);
            object = polyClassPlayer_v2 != null ? polyClassPlayer_v2.tm$12_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
                String string = "<red>\u2718 <white>You must enter a frequency.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
                v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            String string = "teleporters_freq";
            String string2 = "string";
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("freq");
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v1 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$30_set_typed(string, string2, scriptValue2)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
            String string = "teleporters_freq";
            String string2 = "string";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
            object2 = polyClassPlayer_v2 != null ? polyClassPlayer_v2.tm$12_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq", scriptValue3);
            ScriptValue scriptValue7 = Teleporters.freqKey(builder2);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue6);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue7.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
        Object object3 = scriptContext.getClassOrVar("icon");
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("alias");
        ScriptValue scriptValue14 = ScriptValue.of((double)d);
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("display_name");
        ScriptValue scriptValue16 = scriptValue5;
        if (list2 != null) {
            for (ScriptValue scriptValue17 : list2) {
                ScriptValue scriptValue18;
                builder.val("entry", scriptValue17);
                if (scriptValue14.asNum() >= ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptContext.getClassOrVar("DEST_SLOTS"), (ScriptContext)scriptContext).asNum()) break;
                ScriptValue scriptValue19 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ",")), (ScriptContext)scriptContext);
                builder.val("parts", scriptValue19);
                scriptValue12 = scriptValue19;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("world_name", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder3.val("x", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder3.val("y", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                builder3.val("z", (ScriptValue)(scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue20 = Teleporters._signAliasAt(builder3);
                builder.val("alias", scriptValue20);
                scriptValue13 = scriptValue20;
                ScriptValue scriptValue21 = ScriptFormula.valuesEqualStr((ScriptValue)scriptValue13, (String)"") ^ true ? scriptValue13 : (scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("display_name", scriptValue21);
                scriptValue15 = scriptValue21;
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin2((String)"create_item", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "minecraft:ender_pearl")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((String)("<yellow>" + scriptValue15.asStr())), (ScriptContext)scriptContext);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)("<gray>" + (scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ", " + (scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ", " + (scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "<dark_gray>Costs 1 Nether Star"));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ""));
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "<green>Click to teleport"));
                CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "with_lore", (ScriptValue)callSite, (ScriptValue)new ScriptValue.Array(arrayList2), (ScriptContext)scriptContext);
                builder.val("icon", (ScriptValue)callSite2);
                object3 = callSite2;
                ScriptValue scriptValue22 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue16, (ScriptValue)((scriptValue18 = scriptContext.getClassOrVar("DEST_SLOTS")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, (ScriptValue)scriptValue14, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)object3, (ScriptValue)ScriptValue.of((String)("teleporters.pf:on_teleport_slot:" + scriptValue14.asStr())), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("m", scriptValue22);
                scriptValue16 = scriptValue22;
                ScriptValue scriptValue23 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)));
                builder.val("i", scriptValue23);
                scriptValue14 = scriptValue23;
            }
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)))) {
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin2((String)"create_item", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "minecraft:barrier")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((String)("<red>No teleporters found on '" + scriptValue3.asStr() + "'")), (ScriptContext)scriptContext);
            builder.val("filler", (ScriptValue)callSite);
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("m");
            ScriptValue scriptValue25 = scriptValue24 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue24, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 13.0)), (ScriptValue)callSite, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("m", scriptValue25);
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("m")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onTeleportSlot(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        Object object4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            String string = "teleporters_freq";
            String string2 = "string";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            object4 = polyClassPlayer_v2 != null ? polyClassPlayer_v2.tm$12_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object4;
        builder.val("freq", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("freq", scriptValue2);
            ScriptValue scriptValue4 = Teleporters.freqKey(builder2);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue3);
            object3 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue4.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object3;
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
            if (scriptValue != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>That destination is no longer available.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
                v2 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("TELEPORT_ITEM");
            double d = 1.0;
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$40_has_item(scriptValue9.asStr(), d)) : PolyDispatch.bootstrapCall("memberCall", "has_item", (ScriptValue)scriptValue, (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        if (object2.asBool() ^ true) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>You need a Nether Star to use a teleporter this way.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
                v4 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = ScriptFormula.callBuiltin2((String)"split", (ScriptValue)(array2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)array2, (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ",")), (ScriptContext)scriptContext);
        builder.val("parts", scriptValue10);
        ScriptValue scriptValue11 = ScriptFormula.callBuiltin1((String)"world", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        builder.val("target_world", scriptValue11);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asBool()) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>That destination's world is not currently loaded.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
                v5 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)scriptValue11, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.5))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.5))), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.5))), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("loc", scriptValue12);
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("TELEPORT_ITEM");
            double d = 1.0;
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            object = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$16_consume_item(scriptValue13.asStr(), d)) : PolyDispatch.bootstrapCall("memberCall", "consume_item", (ScriptValue)scriptValue, (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((double)d), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool() ^ true) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>You need a Nether Star to use a teleporter this way.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
                v7 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue14 = scriptValue12;
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v8 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$24_teleport_to(scriptValue14)) : PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue, (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
        } else {
            v8 = ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "<green>\u2714 <white>Teleported using a Nether Star.";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v9 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            v9 = ScriptValue.NULL;
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
