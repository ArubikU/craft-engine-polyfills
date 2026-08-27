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
        Object object;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("world_name"));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"world", arrayList, (ScriptContext)scriptContext);
        builder.val("tw", scriptValue);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptValue);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool()) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "");
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("tw");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add(scriptContext.getClassOrVar("x"));
            ScriptValue scriptValue3 = ScriptFormula.callBuiltin((String)"int", arrayList3, (ScriptContext)scriptContext);
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptContext.getClassOrVar("y"));
            ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"int", arrayList4, (ScriptContext)scriptContext);
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(scriptContext.getClassOrVar("z"));
            object = PolyDispatch.bootstrapCall("memberCall", "get_block", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptFormula.callBuiltin((String)"int", arrayList5, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Teleporters.class, 1)), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("center", scriptValue5);
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "north"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "south"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "east"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "west"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "up"));
        arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "down"));
        List list = ScriptProgram.elementsOf((ScriptValue)new ScriptValue.Array(arrayList6));
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("dir", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("center");
                ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "relative", (ScriptValue)scriptValue7, (ScriptValue)scriptContext.getClassOrVar("dir"), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("b", scriptValue8);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("b");
                if (!ScriptFormula.valuesEqualStr((ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "metadata_type", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (String)"SignMetadata")) continue;
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("b");
                List list2 = ScriptProgram.elementsOf((ScriptValue)PolyDispatch.bootstrapGet("memberGet", "lines", (ScriptValue)PolyDispatch.bootstrapGet("memberGet", "front", (ScriptValue)(scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "get_metadata", (ScriptValue)scriptValue10, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
                if (list2 != null) {
                    for (ScriptValue scriptValue11 : list2) {
                        builder.val("line", scriptValue11);
                        if (!(scriptContext.getStr("line").equals("") ^ true)) continue;
                        return scriptContext.getClassOrVar("line");
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
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptValue8);
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ";"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", arrayList2, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue9 : list) {
                builder.val("entry", scriptValue9);
                if (!(scriptContext.getStr("entry").equals("") ^ true)) continue;
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptContext.getClassOrVar("entries"));
                arrayList3.add(scriptContext.getClassOrVar("entry"));
                ScriptValue scriptValue10 = ScriptFormula.callBuiltin((String)"push", arrayList3, (ScriptContext)scriptContext);
                builder.val("entries", scriptValue10);
            }
        }
        double d = 0.0;
        ScriptValue scriptValue11 = ScriptValue.of((double)0.0);
        builder.val("i", scriptValue11);
        List list2 = ScriptProgram.elementsOf((ScriptValue)scriptContext.getClassOrVar("entries"));
        if (list2 != null) {
            for (ScriptValue scriptValue12 : list2) {
                ScriptValue scriptValue13;
                ScriptValue scriptValue14;
                builder.val("entry", scriptValue12);
                double d2 = scriptContext.getNum("i");
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(scriptContext.getClassOrVar("DEST_SLOTS"));
                if (d2 >= ScriptFormula.callBuiltin((String)"len", arrayList4, (ScriptContext)scriptContext).asNum()) break;
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(scriptContext.getClassOrVar("entry"));
                arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ","));
                ScriptValue scriptValue15 = ScriptFormula.callBuiltin((String)"split", arrayList5, (ScriptContext)scriptContext);
                builder.val("parts", scriptValue15);
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptValue scriptValue16 = scriptContext.getClassOrVar("parts");
                builder3.val("world_name", (ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue17 = scriptContext.getClassOrVar("parts");
                builder3.val("x", (ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("parts");
                builder3.val("y", (ScriptValue)(scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("parts");
                builder3.val("z", (ScriptValue)(scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL));
                ScriptValue scriptValue20 = Teleporters._signAliasAt(builder3);
                builder.val("alias", scriptValue20);
                ScriptValue scriptValue21 = ScriptFormula.valuesEqualStr((ScriptValue)scriptValue20, (String)"") ^ true ? scriptValue20 : ((scriptValue14 = scriptContext.getClassOrVar("parts")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL);
                builder.val("display_name", scriptValue21);
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "minecraft:ender_pearl"));
                arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0));
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin((String)"create_item", arrayList6, (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((String)("<yellow>" + scriptValue21.asStr())), (ScriptContext)scriptContext);
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                ScriptValue scriptValue22 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue23 = scriptContext.getClassOrVar("parts");
                ScriptValue scriptValue24 = scriptContext.getClassOrVar("parts");
                arrayList7.add(ScriptValue.of((String)("<gray>" + (scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ", " + (scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue23, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 2.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ", " + (scriptValue24 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue24, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 3.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())));
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "<dark_gray>Costs 1 Nether Star"));
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ""));
                arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "<green>Click to teleport"));
                CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "with_lore", (ScriptValue)callSite, (ScriptValue)new ScriptValue.Array(arrayList7), (ScriptContext)scriptContext);
                builder.val("icon", (ScriptValue)callSite2);
                ScriptValue scriptValue25 = scriptContext.getClassOrVar("m");
                ScriptValue scriptValue26 = scriptValue25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue25, (ScriptValue)((scriptValue13 = scriptContext.getClassOrVar("DEST_SLOTS")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue13, (ScriptValue)scriptContext.getClassOrVar("i"), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)callSite2, (ScriptValue)ScriptValue.of((String)("teleporters.pf:on_teleport_slot:" + scriptContext.getStr("i"))), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("m", scriptValue26);
                ScriptValue scriptValue27 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)));
                builder.val("i", scriptValue27);
            }
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("i"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)))) {
            ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
            arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, "minecraft:barrier"));
            arrayList8.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0));
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin((String)"create_item", arrayList8, (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((String)("<red>No teleporters found on '" + scriptValue3.asStr() + "'")), (ScriptContext)scriptContext);
            builder.val("filler", (ScriptValue)callSite);
            ScriptValue scriptValue28 = scriptContext.getClassOrVar("m");
            ScriptValue scriptValue29 = scriptValue28 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue28, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 13.0)), (ScriptValue)callSite, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("m", scriptValue29);
        }
        Object object5 = (scriptValue = scriptContext.getClassOrVar("m")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var2_2, (ScriptValue)ScriptValue.of((String)var3_3), (ScriptValue)ScriptValue.of((String)var4_4), (ScriptContext)var1_1);
            }
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var8_8 = v0 /* !! */ ;
        var0.val("freq", var8_8);
        var9_9 = var1_1.getClassOrVar("Server");
        if (var9_9 != ScriptValue.NULL) {
            var11_10 = ScriptContext.builder().copyFrom(var1_1);
            var11_10.val("freq", var8_8);
            var10_11 = Teleporters.freqKey(var11_10);
            var12_12 = "string";
            if (var9_9 instanceof ScriptValue.Obj && (var14_14 = (var13_13 = (ScriptValue.Obj)var9_9).instance()) != null && !(var14_14 instanceof PolyClass) && var13_13.typeName().equals("Server")) {
                var15_15 = new PolyClassServer(var14_14);
                v1 /* !! */  = var15_15.tm$6_get_typed(var10_11.asStr(), var12_12);
            } else {
                v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var9_9, (ScriptValue)var10_11, (ScriptValue)ScriptValue.of((String)var12_12), (ScriptContext)var1_1);
            }
        } else {
            v1 /* !! */  = ScriptValue.NULL;
        }
        var16_16 = v1 /* !! */ ;
        var0.val("existing", var16_16);
        var17_17 = new ArrayList<E>();
        var18_18 = new ScriptValue.Array(var17_17);
        var0.val("entries", (ScriptValue)var18_18);
        var22_19 = new ArrayList<ScriptValue>();
        var22_19.add(var16_16);
        var22_19.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ";"));
        var19_20 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"split", var22_19, (ScriptContext)var1_1));
        if (var19_20 != null) {
            for (ScriptValue var21_22 : var19_20) {
                var0.val("entry", var21_22);
                if (!(var1_1.getStr("entry").equals("") ^ true)) continue;
                var23_23 = new ArrayList<ScriptValue>();
                var23_23.add(var1_1.getClassOrVar("entries"));
                var23_23.add(var1_1.getClassOrVar("entry"));
                var24_24 = ScriptFormula.callBuiltin((String)"push", var23_23, (ScriptContext)var1_1);
                var0.val("entries", var24_24);
            }
        }
        var25_25 = new ArrayList<ScriptValue>();
        var25_25.add(var1_1.getClassOrVar("idx"));
        var26_26 = ScriptFormula.callBuiltin((String)"int", var25_25, (ScriptContext)var1_1);
        var0.val("i", var26_26);
        if (var26_26.asNum() < 0.0) ** GOTO lbl-1000
        v2 = var26_26.asNum();
        var27_27 = new ArrayList<ScriptValue>();
        var27_27.add(var1_1.getClassOrVar("entries"));
        if (!(v2 >= ScriptFormula.callBuiltin((String)"len", var27_27, (ScriptContext)var1_1).asNum())) {
            v3 = false;
        } else lbl-1000:
        // 2 sources

        {
            v3 = true;
        }
        if (v3) {
            var28_28 = var1_1.getClassOrVar("Player");
            if (var28_28 != ScriptValue.NULL) {
                var29_29 = "<red>\u2718 <white>That destination is no longer available.";
                if (var28_28 instanceof ScriptValue.Obj && (var31_31 = (var30_30 = (ScriptValue.Obj)var28_28).instance()) != null && !(var31_31 instanceof PolyClass) && var30_30.typeName().equals("Player")) {
                    var32_32 = new PolyClassPlayer(var31_31);
                    v4 /* !! */  = ScriptValue.of((boolean)var32_32.tm$42_send_message(var29_29));
                } else {
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var28_28, (ScriptValue)ScriptValue.of((String)var29_29), (ScriptContext)var1_1);
                }
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var33_33 = var1_1.getClassOrVar("Player");
        if (var33_33 != ScriptValue.NULL) {
            var34_34 = var1_1.getClassOrVar("TELEPORT_ITEM");
            var35_35 = 1.0;
            if (var33_33 instanceof ScriptValue.Obj && (var38_37 = (var37_36 = (ScriptValue.Obj)var33_33).instance()) != null && !(var38_37 instanceof PolyClass) && var37_36.typeName().equals("Player")) {
                var39_38 = new PolyClassPlayer(var38_37);
                v5 /* !! */  = ScriptValue.of((boolean)var39_38.tm$40_has_item(var34_34.asStr(), var35_35));
            } else {
                v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "has_item", (ScriptValue)var33_33, (ScriptValue)var34_34, (ScriptValue)ScriptValue.of((double)var35_35), (ScriptContext)var1_1);
            }
        } else {
            v5 /* !! */  = ScriptValue.NULL;
        }
        if (v5 /* !! */ .asBool() ^ true) {
            var40_39 = var1_1.getClassOrVar("Player");
            if (var40_39 != ScriptValue.NULL) {
                var41_40 = "<red>\u2718 <white>You need a Nether Star to use a teleporter this way.";
                if (var40_39 instanceof ScriptValue.Obj && (var43_42 = (var42_41 = (ScriptValue.Obj)var40_39).instance()) != null && !(var43_42 instanceof PolyClass) && var42_41.typeName().equals("Player")) {
                    var44_43 = new PolyClassPlayer(var43_42);
                    v6 /* !! */  = ScriptValue.of((boolean)var44_43.tm$42_send_message(var41_40));
                } else {
                    v6 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var40_39, (ScriptValue)ScriptValue.of((String)var41_40), (ScriptContext)var1_1);
                }
            } else {
                v6 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var45_44 = new ArrayList<ScriptValue>();
        var46_45 = var1_1.getClassOrVar("entries");
        var45_44.add((ScriptValue)(var46_45 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var46_45, (ScriptValue)var26_26, (ScriptContext)var1_1) : ScriptValue.NULL));
        var45_44.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Teleporters.class, ","));
        var47_46 = ScriptFormula.callBuiltin((String)"split", var45_44, (ScriptContext)var1_1);
        var0.val("parts", var47_46);
        var48_47 = new ArrayList<ScriptValue>();
        var49_48 = var1_1.getClassOrVar("parts");
        var48_47.add((ScriptValue)(var49_48 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var49_48, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.0)), (ScriptContext)var1_1) : ScriptValue.NULL));
        var50_49 = ScriptFormula.callBuiltin((String)"world", var48_47, (ScriptContext)var1_1);
        var0.val("target_world", var50_49);
        var51_50 = new ArrayList<ScriptValue>();
        var51_50.add(var50_49);
        if (ScriptFormula.callBuiltin((String)"is_empty", var51_50, (ScriptContext)var1_1).asBool()) {
            var52_51 = var1_1.getClassOrVar("Player");
            if (var52_51 != ScriptValue.NULL) {
                var53_52 = "<red>\u2718 <white>That destination's world is not currently loaded.";
                if (var52_51 instanceof ScriptValue.Obj && (var55_54 = (var54_53 = (ScriptValue.Obj)var52_51).instance()) != null && !(var55_54 instanceof PolyClass) && var54_53.typeName().equals("Player")) {
                    var56_55 = new PolyClassPlayer(var55_54);
                    v7 /* !! */  = ScriptValue.of((boolean)var56_55.tm$42_send_message(var53_52));
                } else {
                    v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var52_51, (ScriptValue)ScriptValue.of((String)var53_52), (ScriptContext)var1_1);
                }
            } else {
                v7 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var57_56 = var1_1.getClassOrVar("target_world");
        if (var57_56 != ScriptValue.NULL) {
            var58_57 = new ArrayList<ScriptValue>();
            var59_58 = var1_1.getClassOrVar("parts");
            var58_57.add((ScriptValue)(var59_58 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var59_58, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.0)), (ScriptContext)var1_1) : ScriptValue.NULL));
            v8 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var58_57, (ScriptContext)var1_1), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.5)));
            var60_59 = new ArrayList<ScriptValue>();
            var61_60 = var1_1.getClassOrVar("parts");
            var60_59.add((ScriptValue)(var61_60 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var61_60, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 2.0)), (ScriptContext)var1_1) : ScriptValue.NULL));
            v9 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var60_59, (ScriptContext)var1_1), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 1.5)));
            var62_61 = new ArrayList<ScriptValue>();
            var63_62 = var1_1.getClassOrVar("parts");
            var62_61.add((ScriptValue)(var63_62 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)var63_62, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 3.0)), (ScriptContext)var1_1) : ScriptValue.NULL));
            v10 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "location", (ScriptValue)var57_56, (ScriptValue)v8, (ScriptValue)v9, (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"int", var62_61, (ScriptContext)var1_1), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Teleporters.class, 0.5))), (ScriptContext)var1_1);
        } else {
            v10 /* !! */  = ScriptValue.NULL;
        }
        var64_63 = v10 /* !! */ ;
        var0.val("loc", var64_63);
        var65_64 = var1_1.getClassOrVar("Player");
        if (var65_64 != ScriptValue.NULL) {
            var66_65 = var1_1.getClassOrVar("TELEPORT_ITEM");
            var67_66 = 1.0;
            if (var65_64 instanceof ScriptValue.Obj && (var70_68 = (var69_67 = (ScriptValue.Obj)var65_64).instance()) != null && !(var70_68 instanceof PolyClass) && var69_67.typeName().equals("Player")) {
                var71_69 = new PolyClassPlayer(var70_68);
                v11 /* !! */  = ScriptValue.of((boolean)var71_69.tm$16_consume_item(var66_65.asStr(), var67_66));
            } else {
                v11 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "consume_item", (ScriptValue)var65_64, (ScriptValue)var66_65, (ScriptValue)ScriptValue.of((double)var67_66), (ScriptContext)var1_1);
            }
        } else {
            v11 /* !! */  = ScriptValue.NULL;
        }
        if (v11 /* !! */ .asBool() ^ true) {
            var72_70 = var1_1.getClassOrVar("Player");
            if (var72_70 != ScriptValue.NULL) {
                var73_71 = "<red>\u2718 <white>You need a Nether Star to use a teleporter this way.";
                if (var72_70 instanceof ScriptValue.Obj && (var75_73 = (var74_72 = (ScriptValue.Obj)var72_70).instance()) != null && !(var75_73 instanceof PolyClass) && var74_72.typeName().equals("Player")) {
                    var76_74 = new PolyClassPlayer(var75_73);
                    v12 /* !! */  = ScriptValue.of((boolean)var76_74.tm$42_send_message(var73_71));
                } else {
                    v12 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var72_70, (ScriptValue)ScriptValue.of((String)var73_71), (ScriptContext)var1_1);
                }
            } else {
                v12 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var77_75 = var1_1.getClassOrVar("Player");
        if (var77_75 != ScriptValue.NULL) {
            var78_76 = var64_63;
            if (var77_75 instanceof ScriptValue.Obj && (var80_78 = (var79_77 = (ScriptValue.Obj)var77_75).instance()) != null && !(var80_78 instanceof PolyClass) && var79_77.typeName().equals("Player")) {
                var81_79 = new PolyClassPlayer(var80_78);
                v13 /* !! */  = ScriptValue.of((boolean)var81_79.tm$24_teleport_to(var78_76));
            } else {
                v13 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)var77_75, (ScriptValue)var78_76, (ScriptContext)var1_1);
            }
        } else {
            v13 /* !! */  = ScriptValue.NULL;
        }
        var82_80 = var1_1.getClassOrVar("Player");
        if (var82_80 != ScriptValue.NULL) {
            var83_81 = "<green>\u2714 <white>Teleported using a Nether Star.";
            if (var82_80 instanceof ScriptValue.Obj && (var85_83 = (var84_82 = (ScriptValue.Obj)var82_80).instance()) != null && !(var85_83 instanceof PolyClass) && var84_82.typeName().equals("Player")) {
                var86_84 = new PolyClassPlayer(var85_83);
                v14 /* !! */  = ScriptValue.of((boolean)var86_84.tm$42_send_message(var83_81));
            } else {
                v14 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var82_80, (ScriptValue)ScriptValue.of((String)var83_81), (ScriptContext)var1_1);
            }
        } else {
            v14 /* !! */  = ScriptValue.NULL;
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
