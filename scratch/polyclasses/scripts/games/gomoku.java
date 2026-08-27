/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyClassServer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptProgram
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.games;

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
public final class Gomoku {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue makeGameId(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)scriptContext.getClassOrVar("name2"), (ScriptContext)scriptContext), (String)"bot")) {
            return ScriptValue.of((String)("gomoku_" + scriptContext.getStr("name1") + "_bot"));
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("name1"));
        arrayList.add(scriptContext.getClassOrVar("name2"));
        ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"sort_strs", (ScriptValue)new ScriptValue.Array(arrayList), (ScriptContext)scriptContext);
        builder.val("pair", scriptValue);
        return ScriptValue.of((String)("gomoku_" + (scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "_" + (scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()));
    }

    public static ScriptValue emptyBoard(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "");
        builder.val("row", scriptValue);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("BOARD_SIZE"), (ScriptContext)scriptContext));
        ScriptValue scriptValue2 = scriptValue;
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                builder.val("i", scriptValue3);
                ScriptValue scriptValue4 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY"));
                builder.val("row", scriptValue4);
                scriptValue2 = scriptValue4;
            }
        }
        ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "");
        builder.val("board", scriptValue5);
        List list2 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("BOARD_SIZE"), (ScriptContext)scriptContext));
        ScriptValue scriptValue6 = scriptValue5;
        if (list2 != null) {
            for (ScriptValue scriptValue7 : list2) {
                builder.val("i", scriptValue7);
                ScriptValue scriptValue8 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)scriptValue2);
                builder.val("board", scriptValue8);
                scriptValue6 = scriptValue8;
            }
        }
        return scriptValue6;
    }

    public static ScriptValue cellAt(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptFormula.callBuiltin2((String)"char_at", (ScriptValue)scriptContext.getClassOrVar("board"), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)(scriptContext.getNum("r") * scriptContext.getNum("BOARD_SIZE"))), (ScriptValue)scriptContext.getClassOrVar("c")), (ScriptContext)scriptContext);
    }

    public static ScriptValue boardSet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)(scriptContext.getNum("r") * scriptContext.getNum("BOARD_SIZE"))), (ScriptValue)scriptContext.getClassOrVar("c"));
        builder.val("idx", scriptValue);
        return ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin3((String)"substring", (ScriptValue)scriptContext.getClassOrVar("board"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)scriptValue, (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("val")), (ScriptValue)ScriptFormula.callBuiltin3((String)"substring", (ScriptValue)scriptContext.getClassOrVar("board"), (ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))), (ScriptValue)ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptContext.getClassOrVar("board"), (ScriptContext)scriptContext), (ScriptContext)scriptContext));
    }

    public static ScriptValue isFull(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)ScriptValue.of((double)(scriptContext.getNum("BOARD_SIZE") * scriptContext.getNum("BOARD_SIZE"))), (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue : list) {
                builder.val("i", scriptValue);
                if (!ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin2((String)"char_at", (ScriptValue)scriptContext.getClassOrVar("board"), (ScriptValue)scriptValue, (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY"))) continue;
                return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 0);
            }
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 1);
    }

    /*
     * Unable to fully structure code
     */
    public static ScriptValue countDir(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("row"), (ScriptValue)var1_1.getClassOrVar("dr"));
        var0.val("r", var2_2);
        var3_3 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("col"), (ScriptValue)var1_1.getClassOrVar("dc"));
        var0.val("c", var3_3);
        var4_4 = 0.0;
        var6_5 = ScriptValue.of((double)0.0);
        var0.val("n", var6_5);
        var7_6 = 0;
        var8_7 = var2_2;
        var9_8 = var3_3;
        var10_9 = ScriptValue.of((double)var4_4);
        while (var7_6 < 1000) {
            ++var7_6;
            if (!(((var8_7.asNum() >= 0.0 != false && var8_7.asNum() < var1_1.getNum("BOARD_SIZE") != false) != false && var9_8.asNum() >= 0.0 != false) != false && var9_8.asNum() < var1_1.getNum("BOARD_SIZE") != false)) ** GOTO lbl-1000
            var11_10 = ScriptContext.builder().copyFrom(var1_1);
            var11_10.val("board", var1_1.getClassOrVar("board"));
            var11_10.val("r", var8_7);
            var11_10.val("c", var9_8);
            if (ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(var11_10), (ScriptValue)var1_1.getClassOrVar("color"))) {
                v0 = true;
            } else lbl-1000:
            // 2 sources

            {
                v0 = false;
            }
            if (!v0) break;
            var12_11 = ScriptFormula.addPolymorphic((ScriptValue)var10_9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)));
            var0.val("n", var12_11);
            var10_9 = var12_11;
            var13_12 = ScriptFormula.addPolymorphic((ScriptValue)var8_7, (ScriptValue)var1_1.getClassOrVar("dr"));
            var0.val("r", var13_12);
            var8_7 = var13_12;
            var14_13 = ScriptFormula.addPolymorphic((ScriptValue)var9_8, (ScriptValue)var1_1.getClassOrVar("dc"));
            var0.val("c", var14_13);
            var9_8 = var14_13;
        }
        return var10_9;
    }

    public static ScriptValue hasFive(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue.Array> arrayList = new ArrayList<ScriptValue.Array>();
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        arrayList.add(new ScriptValue.Array(arrayList2));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList.add(new ScriptValue.Array(arrayList3));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        arrayList.add(new ScriptValue.Array(arrayList4));
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        arrayList5.add(ScriptValue.of((double)(-1.0)));
        arrayList.add(new ScriptValue.Array(arrayList5));
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("dirs", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)array);
        ScriptValue scriptValue = scriptContext.getClassOrVar("total");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("dr");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("dc");
        if (list != null) {
            for (ScriptValue scriptValue4 : list) {
                builder.val("d", scriptValue4);
                ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dr", scriptValue5);
                scriptValue2 = scriptValue5;
                ScriptValue scriptValue6 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dc", scriptValue6);
                scriptValue3 = scriptValue6;
                ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0);
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("board", scriptContext.getClassOrVar("board"));
                builder2.val("row", scriptContext.getClassOrVar("row"));
                builder2.val("col", scriptContext.getClassOrVar("col"));
                builder2.val("dr", scriptValue2);
                builder2.val("dc", scriptValue3);
                builder2.val("color", scriptContext.getClassOrVar("color"));
                ScriptValue scriptValue8 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue7, (ScriptValue)Gomoku.countDir(builder2));
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("board", scriptContext.getClassOrVar("board"));
                builder3.val("row", scriptContext.getClassOrVar("row"));
                builder3.val("col", scriptContext.getClassOrVar("col"));
                builder3.val("dr", ScriptValue.of((double)(-scriptValue2.asNum())));
                builder3.val("dc", ScriptValue.of((double)(-scriptValue3.asNum())));
                builder3.val("color", scriptContext.getClassOrVar("color"));
                ScriptValue scriptValue9 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)Gomoku.countDir(builder3));
                builder.val("total", scriptValue9);
                scriptValue = scriptValue9;
                if (!(scriptValue.asNum() >= scriptContext.getNum("WIN_LEN"))) continue;
                return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 1);
            }
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 0);
    }

    public static ScriptValue boardKey(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)("gmk_board_" + scriptContext.getStr("gid")));
    }

    public static ScriptValue turnKey(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)("gmk_turn_" + scriptContext.getStr("gid")));
    }

    public static ScriptValue blackKey(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)("gmk_black_" + scriptContext.getStr("gid")));
    }

    public static ScriptValue whiteKey(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)("gmk_white_" + scriptContext.getStr("gid")));
    }

    public static ScriptValue botKey(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)("gmk_bot_" + scriptContext.getStr("gid")));
    }

    public static ScriptValue difficultyKey(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)("gmk_diff_" + scriptContext.getStr("gid")));
    }

    public static ScriptValue statusKey(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.of((String)("gmk_status_" + scriptContext.getStr("gid")));
    }

    public static ScriptValue isBotGame(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.botKey(builder2);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return ScriptValue.of((boolean)ScriptFormula.valuesEqualStr((ScriptValue)object, (String)"1"));
    }

    public static ScriptValue botDifficulty(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.difficultyKey(builder2);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("d", scriptValue3);
        return ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"") ? scriptContext.getClassOrVar("DIFF_DEFAULT") : scriptValue3;
    }

    public static ScriptValue openGomoku(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        PolyClassPlayer polyClassPlayer;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "opponent")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("opp", scriptValue5);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue5, (ScriptContext)scriptContext).asBool() || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"")) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>Usage: /games gomoku <player|bot>";
                PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue6);
                v0 = polyClassPlayer2 != null ? ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin1((String)"lower", (ScriptValue)scriptValue5, (ScriptContext)scriptContext), (String)"bot")) {
            Object object2;
            ScriptValue scriptValue7;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            PolyClassPlayer polyClassPlayer3 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            builder2.val("name1", (ScriptValue)(polyClassPlayer3 != null ? polyClassPlayer3.pg$67_name() : ((scriptValue7 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue7, (ScriptContext)scriptContext) : ScriptValue.NULL)));
            builder2.val("name2", scriptContext.getClassOrVar("BOT_NAME"));
            ScriptValue scriptValue8 = Gomoku.makeGameId(builder2);
            builder.val("gid", scriptValue8);
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Server");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("gid", scriptValue8);
                ScriptValue scriptValue10 = Gomoku.statusKey(builder3);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue9);
                object2 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue10.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)object2, (String)"active")) {
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("gid", scriptValue8);
                builder4.val("viewer", scriptContext.getClassOrVar("Player"));
                Gomoku.showBoard(builder4);
                return ScriptValue.NULL;
            }
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            Gomoku.showDifficultyPicker(builder5);
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Server");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue scriptValue12 = scriptValue5;
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue11);
            object = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue12.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = object;
        builder.val("target", scriptValue13);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue13, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
            if (scriptValue14 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>Player not found or offline.";
                PolyClassPlayer polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue14);
                v3 = polyClassPlayer4 != null ? ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v3 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$67_name() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)))) {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
            if (scriptValue15 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>You can't play yourself - try '/games gomoku bot'.";
                PolyClassPlayer polyClassPlayer5 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue15);
                v4 = polyClassPlayer5 != null ? ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
        PolyClassPlayer polyClassPlayer6 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        builder6.val("name1", (ScriptValue)(polyClassPlayer6 != null ? polyClassPlayer6.pg$67_name() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        builder6.val("name2", (ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue scriptValue16 = Gomoku.makeGameId(builder6);
        builder.val("gid", scriptValue16);
        ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
        builder7.val("gid", scriptValue16);
        PolyClassPlayer polyClassPlayer7 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        builder7.val("starter", (ScriptValue)(polyClassPlayer7 != null ? polyClassPlayer7.pg$67_name() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        builder7.val("opponent", (ScriptValue)(scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue13, (ScriptContext)scriptContext) : ScriptValue.NULL));
        builder7.val("is_bot",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 0));
        builder7.val("difficulty", scriptContext.getClassOrVar("DIFF_DEFAULT"));
        Gomoku.ensureGame(builder7);
        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
        builder8.val("gid", scriptValue16);
        builder8.val("viewer", scriptContext.getClassOrVar("Player"));
        Gomoku.showBoard(builder8);
        return ScriptValue.NULL;
    }

    public static ScriptValue showDifficultyPicker(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = ScriptFormula.compile((String)"[        make_map(\"label\", \"<green>Easy\", \"tooltip\", \"<gray>Blocks your wins, otherwise plays casually.\", \"action\", \"games/gomoku.pf:start_bot_game:\" + DIFF_EASY),        make_map(\"label\", \"<yellow>Medium\", \"tooltip\", \"<gray>Weighs threats on the whole board.\", \"action\", \"games/gomoku.pf:start_bot_game:\" + DIFF_MEDIUM),        make_map(\"label\", \"<red>Hard\", \"tooltip\", \"<gray>Also thinks one move ahead.\", \"action\", \"games/gomoku.pf:start_bot_game:\" + DIFF_HARD),    ]").evaluate(scriptContext);
        builder.val("buttons", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Dialog");
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_multi_action", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "can_close_with_escape", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "body", (ScriptValue)(scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<gold>Gomoku - Bot Difficulty")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<gray>Choose how strong the bot should play.")), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 1)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Machine"), (ScriptValue)scriptContext.getClassOrVar("buttons"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue startBotGame(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        builder2.val("name1", (ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$67_name() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        builder2.val("name2", scriptContext.getClassOrVar("BOT_NAME"));
        ScriptValue scriptValue3 = Gomoku.makeGameId(builder2);
        builder.val("gid", scriptValue3);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("gid", scriptValue3);
        PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        builder3.val("starter", (ScriptValue)(polyClassPlayer2 != null ? polyClassPlayer2.pg$67_name() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)));
        builder3.val("opponent", scriptContext.getClassOrVar("BOT_NAME"));
        builder3.val("is_bot",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 1));
        builder3.val("difficulty", scriptContext.getClassOrVar("difficulty"));
        Gomoku.ensureGame(builder3);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("gid", scriptValue3);
        builder4.val("viewer", scriptContext.getClassOrVar("Player"));
        Gomoku.showBoard(builder4);
        return ScriptValue.NULL;
    }

    public static ScriptValue ensureGame(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.statusKey(builder2);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)object, (String)"active")) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue4 = Gomoku.boardKey(builder3);
            String string = "string";
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue5 = Gomoku.emptyBoard(builder4);
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue3);
            v1 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue4.asStr(), string, scriptValue5)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue7 = Gomoku.turnKey(builder5);
            String string = "string";
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("CELL_BLACK");
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue6);
            v2 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue7.asStr(), string, scriptValue8)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Server");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue10 = Gomoku.blackKey(builder6);
            String string = "string";
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("starter");
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue9);
            v3 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue10.asStr(), string, scriptValue11)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Server");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue13 = Gomoku.whiteKey(builder7);
            String string = "string";
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("opponent");
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue12);
            v4 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue13.asStr(), string, scriptValue14)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Server");
        if (scriptValue15 != ScriptValue.NULL) {
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue16 = Gomoku.botKey(builder8);
            String string = "string";
            ScriptValue scriptValue17 = scriptContext.getBool("is_bot") ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "1")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "0"));
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue15);
            v5 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue16.asStr(), string, scriptValue17)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue15, (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue17, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("Server");
        if (scriptValue18 != ScriptValue.NULL) {
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            builder9.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue19 = Gomoku.difficultyKey(builder9);
            String string = "string";
            ScriptValue scriptValue20 = scriptContext.getClassOrVar("difficulty");
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue18);
            v6 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue19.asStr(), string, scriptValue20)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("Server");
        if (scriptValue21 != ScriptValue.NULL) {
            ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
            builder10.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue22 = Gomoku.statusKey(builder10);
            String string = "string";
            ScriptValue scriptValue23 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "active");
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue21);
            v7 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue22.asStr(), string, scriptValue23)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue21, (ScriptValue)scriptValue22, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue23, (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue statusLine(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getStr("status").equals("black_win")) {
            return ScriptValue.of((String)("<gold>" + scriptContext.getStr("black") + " <white>wins!"));
        }
        if (scriptContext.getStr("status").equals("white_win")) {
            return ScriptValue.of((String)("<gold>" + scriptContext.getStr("white") + " <white>wins!"));
        }
        if (scriptContext.getStr("status").equals("draw")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<yellow>Draw - the board is full.");
        }
        ScriptValue scriptValue = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("turn"), (ScriptValue)scriptContext.getClassOrVar("CELL_BLACK")) ? scriptContext.getClassOrVar("black") : scriptContext.getClassOrVar("white");
        builder.val("turn_name", scriptValue);
        return ScriptFormula.compile((String)"[        \"<gray>Turn: <yellow>\" + turn_name + \" <white>(\",        turn == CELL_BLACK ? Images.from(\"gomoku:black\") : Images.from(\"gomoku:white\"),        \"<white>)\",    ]").evaluate(scriptContext);
    }

    public static ScriptValue cellGlyph(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("ch"), (ScriptValue)scriptContext.getClassOrVar("CELL_BLACK"))) {
            ScriptValue scriptValue = scriptContext.getClassOrVar("Images");
            return scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "from", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "gomoku:black")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("ch"), (ScriptValue)scriptContext.getClassOrVar("CELL_WHITE"))) {
            ScriptValue scriptValue = scriptContext.getClassOrVar("Images");
            return scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "from", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "gomoku:white")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("Images");
        return scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "from", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "gomoku:empty")), (ScriptContext)scriptContext) : ScriptValue.NULL;
    }

    public static ScriptValue showBoard(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.boardKey(builder2);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
            object5 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object5;
        builder.val("board", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Server");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue5 = Gomoku.turnKey(builder3);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue4);
            object4 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue5.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object4;
        builder.val("turn", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue8 = Gomoku.blackKey(builder4);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue7);
            object3 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue8.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object3;
        builder.val("black", scriptValue9);
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Server");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue11 = Gomoku.whiteKey(builder5);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue10);
            object2 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue11.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = object2;
        builder.val("white", scriptValue12);
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Server");
        if (scriptValue13 != ScriptValue.NULL) {
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue14 = Gomoku.statusKey(builder6);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue13);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue14.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = object;
        builder.val("status", scriptValue15);
        boolean bl = ScriptFormula.valuesEqualStr((ScriptValue)scriptValue15, (String)"active") ^ true;
        ScriptValue scriptValue16 = ScriptValue.of((boolean)bl);
        builder.val("is_over", scriptValue16);
        ArrayList arrayList = new ArrayList();
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("buttons", (ScriptValue)array);
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("BOARD_SIZE"), (ScriptContext)scriptContext));
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("c");
        ScriptValue.Array array2 = array;
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("cell_action");
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("ch");
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("btn");
        if (list != null) {
            for (ScriptValue scriptValue21 : list) {
                builder.val("r", scriptValue21);
                List list2 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("BOARD_SIZE"), (ScriptContext)scriptContext));
                if (list2 == null) continue;
                for (ScriptValue scriptValue22 : list2) {
                    builder.val("c", scriptValue22);
                    ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                    builder7.val("board", scriptValue3);
                    builder7.val("r", scriptValue21);
                    builder7.val("c", scriptValue22);
                    ScriptValue scriptValue23 = Gomoku.cellAt(builder7);
                    builder.val("ch", scriptValue23);
                    scriptValue19 = scriptValue23;
                    ScriptValue scriptValue24 = bl ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "games/gomoku.pf:on_noop")) : ScriptValue.of((String)("games/gomoku.pf:on_cell_click:" + scriptContext.getStr("gid") + ":" + scriptValue21.asStr() + ":" + scriptValue22.asStr()));
                    builder.val("cell_action", scriptValue24);
                    scriptValue18 = scriptValue24;
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "label"));
                    ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                    builder8.val("ch", scriptValue19);
                    arrayList2.add(Gomoku.cellGlyph(builder8));
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "action"));
                    arrayList2.add(scriptValue18);
                    arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "width"));
                    arrayList2.add(scriptContext.getClassOrVar("CELL_WIDTH"));
                    ScriptValue scriptValue25 = ScriptFormula.callBuiltin((String)"make_map", arrayList2, (ScriptContext)scriptContext);
                    builder.val("btn", scriptValue25);
                    scriptValue20 = scriptValue25;
                    ScriptValue scriptValue26 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptContext.getClassOrVar("buttons"), (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
                    builder.val("buttons", scriptValue26);
                    array2 = scriptValue26;
                }
            }
        }
        ScriptValue scriptValue27 = bl ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<gray>Leave")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<red>Resign"));
        builder.val("footer_label", scriptValue27);
        ScriptValue scriptValue28 = bl ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "games/gomoku.pf:on_noop")) : ScriptValue.of((String)("games/gomoku.pf:on_resign:" + scriptContext.getStr("gid")));
        builder.val("footer_action", scriptValue28);
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("buttons");
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "label"));
        arrayList3.add(scriptValue27);
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "action"));
        arrayList3.add(scriptValue28);
        ScriptValue scriptValue30 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)scriptValue29, (ScriptValue)ScriptFormula.callBuiltin((String)"make_map", arrayList3, (ScriptContext)scriptContext), (ScriptContext)scriptContext);
        builder.val("buttons", scriptValue30);
        ScriptValue scriptValue31 = ScriptValue.of((String)("<gold>Gomoku <gray>- <yellow>" + scriptValue9.asStr() + " <gray>vs <yellow>" + scriptValue12.asStr()));
        builder.val("title", scriptValue31);
        ScriptValue scriptValue32 = scriptContext.getClassOrVar("Dialog");
        ScriptValue scriptValue33 = scriptContext.getClassOrVar("Images");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "body", (ScriptValue)(scriptValue32 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue32, (ScriptValue)scriptValue31, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)(scriptValue33 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "from", (ScriptValue)scriptValue33, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "gomoku:ui")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
        builder9.val("status", scriptValue15);
        builder9.val("turn", scriptValue6);
        builder9.val("black", scriptValue9);
        builder9.val("white", scriptValue12);
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_multi_action", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "can_close_with_escape", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "body", (ScriptValue)callSite, (ScriptValue)Gomoku.statusLine(builder9), (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((boolean)(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue15, (String)"active") ^ true)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Machine"), (ScriptValue)scriptValue30, (ScriptValue)scriptContext.getClassOrVar("BOARD_SIZE"), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("viewer"), (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue notifyOpponent(ScriptContext.Builder builder) {
        block7: {
            Object object;
            ScriptValue scriptValue;
            Object object2;
            Object object3;
            ScriptContext scriptContext = builder.peek();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            if (Gomoku.isBotGame(builder2).asBool()) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Server");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue3 = Gomoku.blackKey(builder3);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue2);
                object3 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue3.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue4 = object3;
            builder.val("black", scriptValue4);
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Server");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue6 = Gomoku.whiteKey(builder4);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue5);
                object2 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue6.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = object2;
            builder.val("white", scriptValue7);
            PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
            ScriptValue scriptValue8 = ScriptFormula.valuesEqual((ScriptValue)(polyClassPlayer != null ? polyClassPlayer.pg$67_name() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptValue4) ? scriptValue7 : scriptValue4;
            builder.val("other_name", scriptValue8);
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Server");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue scriptValue10 = scriptValue8;
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue9);
                object = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue10.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = object;
            builder.val("other", scriptValue11);
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asBool() ^ true)) break block7;
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            builder5.val("viewer", scriptValue11);
            Gomoku.showBoard(builder5);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue notifyBothChat(ScriptContext.Builder builder) {
        block10: {
            Object object;
            Object object2;
            Object object3;
            Object object4;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
            if (scriptValue != ScriptValue.NULL) {
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue2 = Gomoku.blackKey(builder2);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
                object4 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = object4;
            builder.val("black", scriptValue3);
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Server");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue5 = Gomoku.whiteKey(builder3);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue4);
                object3 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue5.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = object3;
            builder.val("white", scriptValue6);
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue scriptValue8 = scriptValue3;
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue7);
                object2 = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue8.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = object2;
            builder.val("b", scriptValue9);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue9, (ScriptContext)scriptContext).asBool() ^ true) {
                Object object5 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, (ScriptValue)scriptContext.getClassOrVar("message"), (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            if (!(Gomoku.isBotGame(builder4).asBool() ^ true)) break block10;
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Server");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptValue scriptValue11 = scriptValue6;
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue10);
                object = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue11.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = object;
            builder.val("w", scriptValue12);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue12, (ScriptContext)scriptContext).asBool() ^ true) {
                Object object6 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue12, (ScriptValue)scriptContext.getClassOrVar("message"), (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue applyMove(ScriptContext.Builder builder) {
        block16: {
            Object object;
            Object object2;
            Object object3;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
            if (scriptValue != ScriptValue.NULL) {
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue2 = Gomoku.boardKey(builder2);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
                object3 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = object3;
            builder.val("board", scriptValue3);
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("board", scriptContext.getClassOrVar("board"));
            builder3.val("r", scriptContext.getClassOrVar("row"));
            builder3.val("c", scriptContext.getClassOrVar("col"));
            builder3.val("val", scriptContext.getClassOrVar("color"));
            ScriptValue scriptValue4 = Gomoku.boardSet(builder3);
            builder.val("board", scriptValue4);
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Server");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue6 = Gomoku.boardKey(builder4);
                String string = "string";
                ScriptValue scriptValue7 = scriptValue4;
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue5);
                v1 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue6.asStr(), string, scriptValue7)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Server");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue9 = Gomoku.blackKey(builder5);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue8);
                object2 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue9.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = object2;
            builder.val("black", scriptValue10);
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Server");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue12 = Gomoku.whiteKey(builder6);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue11);
                object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue12.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = object;
            builder.val("white", scriptValue13);
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("board", scriptValue4);
            builder7.val("row", scriptContext.getClassOrVar("row"));
            builder7.val("col", scriptContext.getClassOrVar("col"));
            builder7.val("color", scriptContext.getClassOrVar("color"));
            if (Gomoku.hasFive(builder7).asBool()) {
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("Server");
                if (scriptValue14 != ScriptValue.NULL) {
                    ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                    builder8.val("gid", scriptContext.getClassOrVar("gid"));
                    ScriptValue scriptValue15 = Gomoku.statusKey(builder8);
                    String string = "string";
                    ScriptValue scriptValue16 = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("color"), (ScriptValue)scriptContext.getClassOrVar("CELL_BLACK")) ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "black_win")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "white_win"));
                    PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue14);
                    v4 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue15.asStr(), string, scriptValue16)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue16, (ScriptContext)scriptContext);
                } else {
                    v4 = ScriptValue.NULL;
                }
                ScriptValue scriptValue17 = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("color"), (ScriptValue)scriptContext.getClassOrVar("CELL_BLACK")) ? scriptValue10 : scriptValue13;
                builder.val("winner", scriptValue17);
                ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
                builder9.val("gid", scriptContext.getClassOrVar("gid"));
                builder9.val("message", ScriptValue.of((String)("<gold>\u2726 <white>" + scriptValue17.asStr() + " wins the Gomoku match!")));
                Gomoku.notifyBothChat(builder9);
                return ScriptValue.NULL;
            }
            ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
            builder10.val("board", scriptValue4);
            if (Gomoku.isFull(builder10).asBool()) {
                ScriptValue scriptValue18 = scriptContext.getClassOrVar("Server");
                if (scriptValue18 != ScriptValue.NULL) {
                    ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
                    builder11.val("gid", scriptContext.getClassOrVar("gid"));
                    ScriptValue scriptValue19 = Gomoku.statusKey(builder11);
                    String string = "string";
                    ScriptValue scriptValue20 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "draw");
                    PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue18);
                    v5 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue19.asStr(), string, scriptValue20)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue20, (ScriptContext)scriptContext);
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
                builder12.val("gid", scriptContext.getClassOrVar("gid"));
                builder12.val("message",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<yellow>Gomoku match ended in a draw."));
                Gomoku.notifyBothChat(builder12);
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue21 = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("color"), (ScriptValue)scriptContext.getClassOrVar("CELL_BLACK")) ? scriptContext.getClassOrVar("CELL_WHITE") : scriptContext.getClassOrVar("CELL_BLACK");
            builder.val("next_turn", scriptValue21);
            ScriptValue scriptValue22 = scriptContext.getClassOrVar("Server");
            if (scriptValue22 != ScriptValue.NULL) {
                ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
                builder13.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue23 = Gomoku.turnKey(builder13);
                String string = "string";
                ScriptValue scriptValue24 = scriptValue21;
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue22);
                v6 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue23.asStr(), string, scriptValue24)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue22, (ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder14 = ScriptContext.builder().copyFrom(scriptContext);
            builder14.val("gid", scriptContext.getClassOrVar("gid"));
            if (!(Gomoku.isBotGame(builder14).asBool() && ScriptFormula.valuesEqual((ScriptValue)scriptValue21, (ScriptValue)scriptContext.getClassOrVar("CELL_WHITE")))) break block16;
            ScriptValue scriptValue25 = scriptContext.getClassOrVar("TaskManager");
            Object object4 = scriptValue25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue25, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:bot_move:" + scriptContext.getStr("gid"))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 15.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue refreshBoard(ScriptContext.Builder builder) {
        block2: {
            Object object;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue scriptValue2 = scriptContext.getClassOrVar("player_name");
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
                object = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue2.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = object;
            builder.val("p", scriptValue3);
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool() ^ true)) break block2;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            builder2.val("viewer", scriptValue3);
            Gomoku.showBoard(builder2);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onCellClick(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        Object object;
        ScriptValue scriptValue2;
        PolyClassPlayer polyClassPlayer2;
        ScriptValue scriptValue3;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue4 = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("row"), (ScriptContext)scriptContext);
        builder.val("row", scriptValue4);
        ScriptValue scriptValue5 = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("col"), (ScriptContext)scriptContext);
        builder.val("col", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue7 = Gomoku.statusKey(builder2);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue6);
            object5 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue7.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object5;
        builder.val("status", scriptValue8);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue8, (String)"active") ^ true) {
            ScriptValue scriptValue9;
            PolyClassPlayer polyClassPlayer3;
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
            if (scriptValue10 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>This game has already ended - run the command again to start a new one.";
                PolyClassPlayer polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue10);
                v1 = polyClassPlayer4 != null ? ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("TaskManager");
            Object object6 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((polyClassPlayer3 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer3.tg$68_name() : ((scriptValue9 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue9, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Server");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue13 = Gomoku.blackKey(builder3);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue12);
            object4 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue13.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = object4;
        builder.val("black", scriptValue14);
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Server");
        if (scriptValue15 != ScriptValue.NULL) {
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue16 = Gomoku.whiteKey(builder4);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue15);
            object3 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue16.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue15, (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue17 = object3;
        builder.val("white", scriptValue17);
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("Server");
        if (scriptValue18 != ScriptValue.NULL) {
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue19 = Gomoku.turnKey(builder5);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue18);
            object2 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue19.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue20 = object2;
        builder.val("turn", scriptValue20);
        ScriptValue scriptValue21 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "");
        builder.val("my_color", scriptValue21);
        PolyClassPlayer polyClassPlayer5 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (ScriptFormula.valuesEqual((ScriptValue)(polyClassPlayer5 != null ? polyClassPlayer5.pg$67_name() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptValue14)) {
            ScriptValue scriptValue22 = scriptContext.getClassOrVar("CELL_BLACK");
            builder.val("my_color", scriptValue22);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)((polyClassPlayer2 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer2.pg$67_name() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptValue17)) {
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("CELL_WHITE");
            builder.val("my_color", scriptValue23);
        }
        if (scriptContext.getStr("my_color").equals("")) {
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("Player");
            if (scriptValue24 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>You're not a player in this game.";
                PolyClassPlayer polyClassPlayer6 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue24);
                v6 = polyClassPlayer6 != null ? ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("my_color"), (ScriptValue)scriptValue20) ^ true) {
            ScriptValue scriptValue25;
            PolyClassPlayer polyClassPlayer7;
            ScriptValue scriptValue26 = scriptContext.getClassOrVar("Player");
            if (scriptValue26 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>It's not your turn.";
                PolyClassPlayer polyClassPlayer8 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue26);
                v7 = polyClassPlayer8 != null ? ScriptValue.of((boolean)polyClassPlayer8.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
            ScriptValue scriptValue27 = scriptContext.getClassOrVar("TaskManager");
            Object object7 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue27, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((polyClassPlayer7 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer7.tg$68_name() : ((scriptValue25 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue25, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("Server");
        if (scriptValue28 != ScriptValue.NULL) {
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue29 = Gomoku.boardKey(builder6);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue28);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue29.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue28, (ScriptValue)scriptValue29, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue30 = object;
        builder.val("board", scriptValue30);
        ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
        builder7.val("board", scriptValue30);
        builder7.val("r", scriptValue4);
        builder7.val("c", scriptValue5);
        if (ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(builder7), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY")) ^ true) {
            ScriptValue scriptValue31;
            PolyClassPlayer polyClassPlayer9;
            ScriptValue scriptValue32 = scriptContext.getClassOrVar("Player");
            if (scriptValue32 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>That cell is already taken.";
                PolyClassPlayer polyClassPlayer10 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue32);
                v10 = polyClassPlayer10 != null ? ScriptValue.of((boolean)polyClassPlayer10.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue32, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v10 = ScriptValue.NULL;
            }
            ScriptValue scriptValue33 = scriptContext.getClassOrVar("TaskManager");
            Object object8 = scriptValue33 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue33, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((polyClassPlayer9 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer9.tg$68_name() : ((scriptValue31 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue31, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
        builder8.val("gid", scriptContext.getClassOrVar("gid"));
        builder8.val("row", scriptValue4);
        builder8.val("col", scriptValue5);
        builder8.val("color", scriptContext.getClassOrVar("my_color"));
        Gomoku.applyMove(builder8);
        ScriptValue scriptValue34 = scriptContext.getClassOrVar("TaskManager");
        Object object9 = scriptValue34 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue34, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.tg$68_name() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
        builder9.val("gid", scriptContext.getClassOrVar("gid"));
        Gomoku.notifyOpponent(builder9);
        return ScriptValue.NULL;
    }

    public static ScriptValue onResign(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        ScriptValue scriptValue2;
        ScriptValue scriptValue3;
        PolyClassPlayer polyClassPlayer;
        ScriptValue scriptValue4;
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Server");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue6 = Gomoku.statusKey(builder2);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue5);
            object3 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue6.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object3;
        builder.val("status", scriptValue7);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue7, (String)"active") ^ true) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
            if (scriptValue8 != ScriptValue.NULL) {
                String string = "<yellow>This game has already ended.";
                PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue8);
                v1 = polyClassPlayer2 != null ? ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Server");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue10 = Gomoku.blackKey(builder3);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue9);
            object2 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue10.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = object2;
        builder.val("black", scriptValue11);
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Server");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue13 = Gomoku.whiteKey(builder4);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue12);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue13.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = object;
        builder.val("white", scriptValue14);
        PolyClassPlayer polyClassPlayer3 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        if (ScriptFormula.valuesEqual((ScriptValue)(polyClassPlayer3 != null ? polyClassPlayer3.pg$67_name() : ((scriptValue4 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptValue11) ^ true && ScriptFormula.valuesEqual((ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$67_name() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptValue14) ^ true) {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
            if (scriptValue15 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>You're not a player in this game.";
                PolyClassPlayer polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue15);
                v4 = polyClassPlayer4 != null ? ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        PolyClassPlayer polyClassPlayer5 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        boolean bl = ScriptFormula.valuesEqual((ScriptValue)(polyClassPlayer5 != null ? polyClassPlayer5.pg$67_name() : ((scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptValue)scriptValue11);
        ScriptValue scriptValue16 = ScriptValue.of((boolean)bl);
        builder.val("resigner_is_black", scriptValue16);
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("Server");
        if (scriptValue17 != ScriptValue.NULL) {
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue18 = Gomoku.statusKey(builder5);
            String string = "string";
            ScriptValue scriptValue19 = bl ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "white_win")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "black_win"));
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue17);
            v5 = polyClassServer != null ? ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue18.asStr(), string, scriptValue19)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue17, (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue20 = bl ? scriptValue14 : scriptValue11;
        builder.val("winner", scriptValue20);
        ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
        builder6.val("gid", scriptContext.getClassOrVar("gid"));
        PolyClassPlayer polyClassPlayer6 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player");
        builder6.val("message", ScriptValue.of((String)("<yellow>" + (polyClassPlayer6 != null ? polyClassPlayer6.tg$68_name() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())) + " resigned. <gold>" + scriptValue20.asStr() + " wins!")));
        Gomoku.notifyBothChat(builder6);
        ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
        builder7.val("gid", scriptContext.getClassOrVar("gid"));
        Gomoku.notifyOpponent(builder7);
        return ScriptValue.NULL;
    }

    public static ScriptValue onNoop(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.NULL;
    }

    public static ScriptValue findWinningMove(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("BOARD_SIZE"), (ScriptContext)scriptContext));
        ScriptValue scriptValue = scriptContext.getClassOrVar("c");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("trial");
        if (list != null) {
            for (ScriptValue scriptValue3 : list) {
                builder.val("r", scriptValue3);
                List list2 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("BOARD_SIZE"), (ScriptContext)scriptContext));
                if (list2 == null) continue;
                for (ScriptValue scriptValue4 : list2) {
                    builder.val("c", scriptValue4);
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                    builder2.val("board", scriptContext.getClassOrVar("board"));
                    builder2.val("r", scriptValue3);
                    builder2.val("c", scriptValue4);
                    if (!ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(builder2), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY"))) continue;
                    ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                    builder3.val("board", scriptContext.getClassOrVar("board"));
                    builder3.val("r", scriptValue3);
                    builder3.val("c", scriptValue4);
                    builder3.val("val", scriptContext.getClassOrVar("color"));
                    ScriptValue scriptValue5 = Gomoku.boardSet(builder3);
                    builder.val("trial", scriptValue5);
                    scriptValue2 = scriptValue5;
                    ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                    builder4.val("board", scriptValue2);
                    builder4.val("row", scriptValue3);
                    builder4.val("col", scriptValue4);
                    builder4.val("color", scriptContext.getClassOrVar("color"));
                    if (!Gomoku.hasFive(builder4).asBool()) continue;
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue3);
                    arrayList.add(scriptValue4);
                    return new ScriptValue.Array(arrayList);
                }
            }
        }
        ArrayList arrayList = new ArrayList();
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue findAnyMove(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("BOARD_SIZE"), (ScriptContext)scriptContext));
        ScriptValue scriptValue = scriptContext.getClassOrVar("c");
        if (list != null) {
            for (ScriptValue scriptValue2 : list) {
                builder.val("r", scriptValue2);
                List list2 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)scriptContext.getClassOrVar("BOARD_SIZE"), (ScriptContext)scriptContext));
                if (list2 == null) continue;
                for (ScriptValue scriptValue3 : list2) {
                    builder.val("c", scriptValue3);
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                    builder2.val("board", scriptContext.getClassOrVar("board"));
                    builder2.val("r", scriptValue2);
                    builder2.val("c", scriptValue3);
                    if (!ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(builder2), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY"))) continue;
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue2);
                    arrayList.add(scriptValue3);
                    return new ScriptValue.Array(arrayList);
                }
            }
        }
        ArrayList arrayList = new ArrayList();
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue lineScore(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getNum("count") >= scriptContext.getNum("WIN_LEN")) {
            return  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 100000.0);
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 4.0)))) {
            return ScriptValue.of((double)(ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("open_ends"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 2.0))) ? 10000.0 : (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("open_ends"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))) ? 1000.0 : 0.0)));
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 3.0)))) {
            return ScriptValue.of((double)(ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("open_ends"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 2.0))) ? 500.0 : (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("open_ends"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))) ? 100.0 : 0.0)));
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("count"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 2.0)))) {
            return ScriptValue.of((double)(ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("open_ends"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 2.0))) ? 50.0 : (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("open_ends"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))) ? 10.0 : 0.0)));
        }
        return ScriptValue.of((double)(scriptContext.getNum("open_ends") > 0.0 ? 1.0 : 0.0));
    }

    /*
     * Enabled aggressive block sorting
     */
    public static ScriptValue isOpenCell(ScriptContext.Builder builder) {
        boolean bl;
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getNum("r") >= 0.0 && scriptContext.getNum("r") < scriptContext.getNum("BOARD_SIZE") && scriptContext.getNum("c") >= 0.0 && scriptContext.getNum("c") < scriptContext.getNum("BOARD_SIZE")) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("board", scriptContext.getClassOrVar("board"));
            builder2.val("r", scriptContext.getClassOrVar("r"));
            builder2.val("c", scriptContext.getClassOrVar("c"));
            if (ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(builder2), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY"))) {
                bl = true;
                return ScriptValue.of((boolean)bl);
            }
        }
        bl = false;
        return ScriptValue.of((boolean)bl);
    }

    public static ScriptValue evaluateCell(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue.Array> arrayList = new ArrayList<ScriptValue.Array>();
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        arrayList.add(new ScriptValue.Array(arrayList2));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList.add(new ScriptValue.Array(arrayList3));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        arrayList.add(new ScriptValue.Array(arrayList4));
        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
        arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        arrayList5.add(ScriptValue.of((double)(-1.0)));
        arrayList.add(new ScriptValue.Array(arrayList5));
        ScriptValue.Array array = new ScriptValue.Array(arrayList);
        builder.val("dirs", (ScriptValue)array);
        double d = 0.0;
        ScriptValue scriptValue = ScriptValue.of((double)0.0);
        builder.val("total", scriptValue);
        List list = ScriptProgram.elementsOf((ScriptValue)array);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("fwd");
        ScriptValue scriptValue3 = ScriptValue.of((double)d);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("end2_open");
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("count");
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("open_ends");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("dr");
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("bwd");
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("dc");
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("end1_open");
        if (list != null) {
            for (ScriptValue scriptValue11 : list) {
                builder.val("d", scriptValue11);
                ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dr", scriptValue12);
                scriptValue7 = scriptValue12;
                ScriptValue scriptValue13 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dc", scriptValue13);
                scriptValue9 = scriptValue13;
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("board", scriptContext.getClassOrVar("board"));
                builder2.val("row", scriptContext.getClassOrVar("row"));
                builder2.val("col", scriptContext.getClassOrVar("col"));
                builder2.val("dr", scriptValue7);
                builder2.val("dc", scriptValue9);
                builder2.val("color", scriptContext.getClassOrVar("color"));
                ScriptValue scriptValue14 = Gomoku.countDir(builder2);
                builder.val("fwd", scriptValue14);
                scriptValue2 = scriptValue14;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("board", scriptContext.getClassOrVar("board"));
                builder3.val("row", scriptContext.getClassOrVar("row"));
                builder3.val("col", scriptContext.getClassOrVar("col"));
                builder3.val("dr", ScriptValue.of((double)(-scriptValue7.asNum())));
                builder3.val("dc", ScriptValue.of((double)(-scriptValue9.asNum())));
                builder3.val("color", scriptContext.getClassOrVar("color"));
                ScriptValue scriptValue15 = Gomoku.countDir(builder3);
                builder.val("bwd", scriptValue15);
                scriptValue8 = scriptValue15;
                ScriptValue scriptValue16 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptValue)scriptValue2), (ScriptValue)scriptValue8);
                builder.val("count", scriptValue16);
                scriptValue5 = scriptValue16;
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("board", scriptContext.getClassOrVar("board"));
                builder4.val("r", ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("row"), (ScriptValue)ScriptValue.of((double)(scriptValue7.asNum() * ScriptFormula.addPolymorphic((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))).asNum()))));
                builder4.val("c", ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("col"), (ScriptValue)ScriptValue.of((double)(scriptValue9.asNum() * ScriptFormula.addPolymorphic((ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))).asNum()))));
                ScriptValue scriptValue17 = Gomoku.isOpenCell(builder4);
                builder.val("end1_open", scriptValue17);
                scriptValue10 = scriptValue17;
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("board", scriptContext.getClassOrVar("board"));
                builder5.val("r", ScriptValue.of((double)(scriptContext.getNum("row") - scriptValue7.asNum() * ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))).asNum())));
                builder5.val("c", ScriptValue.of((double)(scriptContext.getNum("col") - scriptValue9.asNum() * ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))).asNum())));
                ScriptValue scriptValue18 = Gomoku.isOpenCell(builder5);
                builder.val("end2_open", scriptValue18);
                scriptValue4 = scriptValue18;
                double d2 = (scriptValue10.asBool() ? 1.0 : 0.0) + (scriptValue4.asBool() ? 1.0 : 0.0);
                ScriptValue scriptValue19 = ScriptValue.of((double)d2);
                builder.val("open_ends", scriptValue19);
                scriptValue6 = scriptValue19;
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("count", scriptValue5);
                builder6.val("open_ends", scriptValue6);
                ScriptValue scriptValue20 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue3, (ScriptValue)Gomoku.lineScore(builder6));
                builder.val("total", scriptValue20);
                scriptValue3 = scriptValue20;
            }
        }
        return scriptValue3;
    }

    /*
     * Unable to fully structure code
     */
    public static ScriptValue hasNeighbor(ScriptContext.Builder var0) {
        block4: {
            var1_1 = var0.peek();
            var5_2 = new ArrayList<ScriptValue>();
            var5_2.add(ScriptValue.of((double)(-2.0)));
            var5_2.add(ScriptValue.of((double)(-1.0)));
            var5_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
            var5_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
            var5_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 2.0));
            var2_3 = ScriptProgram.elementsOf((ScriptValue)new ScriptValue.Array(var5_2));
            var6_4 = var1_1.getClassOrVar("nr");
            var7_5 = var1_1.getClassOrVar("nc");
            var8_6 = var1_1.getClassOrVar("dc");
            if (var2_3 == null) break block4;
            for (ScriptValue var4_8 : var2_3) {
                var0.val("dr", var4_8);
                var12_12 = new ArrayList<ScriptValue>();
                var12_12.add(ScriptValue.of((double)(-2.0)));
                var12_12.add(ScriptValue.of((double)(-1.0)));
                var12_12.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                var12_12.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
                var12_12.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 2.0));
                var9_9 = ScriptProgram.elementsOf((ScriptValue)new ScriptValue.Array(var12_12));
                if (var9_9 == null) continue;
                for (ScriptValue var11_11 : var9_9) {
                    var0.val("dc", var11_11);
                    var13_13 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("r"), (ScriptValue)var4_8);
                    var0.val("nr", var13_13);
                    var6_4 = var13_13;
                    var14_14 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("c"), (ScriptValue)var11_11);
                    var0.val("nc", var14_14);
                    var7_5 = var14_14;
                    if (!(((var6_4.asNum() >= 0.0 != false && var6_4.asNum() < var1_1.getNum("BOARD_SIZE") != false) != false && var7_5.asNum() >= 0.0 != false) != false && var7_5.asNum() < var1_1.getNum("BOARD_SIZE") != false)) ** GOTO lbl-1000
                    var15_15 = ScriptContext.builder().copyFrom(var1_1);
                    var15_15.val("board", var1_1.getClassOrVar("board"));
                    var15_15.val("r", var6_4);
                    var15_15.val("c", var7_5);
                    if (ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(var15_15), (ScriptValue)var1_1.getClassOrVar("CELL_EMPTY")) ^ true) {
                        v0 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v0 = false;
                    }
                    if (!v0) continue;
                    return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 1);
                }
            }
        }
        return  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 0);
    }

    /*
     * Unable to fully structure code
     */
    public static ScriptValue findCandidates(ScriptContext.Builder var0) {
        block7: {
            var1_1 = var0.peek();
            var2_2 = false;
            var3_3 = ScriptValue.of((boolean)false);
            var0.val("any_stone", var3_3);
            var4_4 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)ScriptValue.of((double)(var1_1.getNum("BOARD_SIZE") * var1_1.getNum("BOARD_SIZE"))), (ScriptContext)var1_1));
            if (var4_4 != null) {
                for (ScriptValue var6_6 : var4_4) {
                    var0.val("i", var6_6);
                    if (!(ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin2((String)"char_at", (ScriptValue)var1_1.getClassOrVar("board"), (ScriptValue)var6_6, (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("CELL_EMPTY")) ^ true)) continue;
                    var7_7 = true;
                    var8_8 = ScriptValue.of((boolean)true);
                    var0.val("any_stone", var8_8);
                }
            }
            if (var1_1.getBool("any_stone") ^ true) {
                var9_9 = 2.0;
                var11_10 = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)ScriptValue.of((double)(2.0 == 0.0 ? 0.0 : var1_1.getNum("BOARD_SIZE") / var9_9)), (ScriptContext)var1_1);
                var0.val("center", var11_10);
                var12_11 = new ArrayList<ScriptValue.Array>();
                var13_12 = new ArrayList<ScriptValue>();
                var13_12.add(var11_10);
                var13_12.add(var11_10);
                var12_11.add(new ScriptValue.Array(var13_12));
                return new ScriptValue.Array(var12_11);
            }
            var14_13 = new ArrayList<E>();
            var15_14 = new ScriptValue.Array(var14_13);
            var0.val("result", (ScriptValue)var15_14);
            var16_15 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)var1_1.getClassOrVar("BOARD_SIZE"), (ScriptContext)var1_1));
            var19_16 = var15_14;
            var20_17 = var1_1.getClassOrVar("c");
            if (var16_15 == null) break block7;
            for (ScriptValue var18_19 : var16_15) {
                var0.val("r", var18_19);
                var21_20 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)var1_1.getClassOrVar("BOARD_SIZE"), (ScriptContext)var1_1));
                if (var21_20 == null) continue;
                for (ScriptValue var23_22 : var21_20) {
                    var0.val("c", var23_22);
                    var24_23 = ScriptContext.builder().copyFrom(var1_1);
                    var24_23.val("board", var1_1.getClassOrVar("board"));
                    var24_23.val("r", var18_19);
                    var24_23.val("c", var23_22);
                    if (!ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(var24_23), (ScriptValue)var1_1.getClassOrVar("CELL_EMPTY"))) ** GOTO lbl-1000
                    var25_24 = ScriptContext.builder().copyFrom(var1_1);
                    var25_24.val("board", var1_1.getClassOrVar("board"));
                    var25_24.val("r", var18_19);
                    var25_24.val("c", var23_22);
                    if (Gomoku.hasNeighbor(var25_24).asBool()) {
                        v0 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v0 = false;
                    }
                    if (!v0) continue;
                    v1 = var1_1.getClassOrVar("result");
                    var26_25 = new ArrayList<ScriptValue>();
                    var26_25.add(var18_19);
                    var26_25.add(var23_22);
                    var27_26 = ScriptFormula.callBuiltin2((String)"push", (ScriptValue)v1, (ScriptValue)new ScriptValue.Array(var26_25), (ScriptContext)var1_1);
                    var0.val("result", var27_26);
                    var19_16 = var27_26;
                }
            }
        }
        return var19_16;
    }

    public static ScriptValue combinedScore(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getNum("offense") >= scriptContext.getNum("defense")) {
            return ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("offense"), (ScriptValue)ScriptValue.of((double)(scriptContext.getNum("defense") * 0.2)));
        }
        return ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("defense"), (ScriptValue)ScriptValue.of((double)(scriptContext.getNum("offense") * 0.2)));
    }

    /*
     * Unable to fully structure code
     */
    public static ScriptValue findAdjacentMove(ScriptContext.Builder var0) {
        block4: {
            var1_1 = var0.peek();
            var2_2 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)var1_1.getClassOrVar("BOARD_SIZE"), (ScriptContext)var1_1));
            var5_3 = var1_1.getClassOrVar("c");
            if (var2_2 == null) break block4;
            for (ScriptValue var4_5 : var2_2) {
                var0.val("r", var4_5);
                var6_6 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin2((String)"range", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptValue)var1_1.getClassOrVar("BOARD_SIZE"), (ScriptContext)var1_1));
                if (var6_6 == null) continue;
                for (ScriptValue var8_8 : var6_6) {
                    var0.val("c", var8_8);
                    var9_9 = ScriptContext.builder().copyFrom(var1_1);
                    var9_9.val("board", var1_1.getClassOrVar("board"));
                    var9_9.val("r", var4_5);
                    var9_9.val("c", var8_8);
                    if (!ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(var9_9), (ScriptValue)var1_1.getClassOrVar("CELL_EMPTY"))) ** GOTO lbl-1000
                    var10_10 = ScriptContext.builder().copyFrom(var1_1);
                    var10_10.val("board", var1_1.getClassOrVar("board"));
                    var10_10.val("r", var4_5);
                    var10_10.val("c", var8_8);
                    if (Gomoku.hasNeighbor(var10_10).asBool()) {
                        v0 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v0 = false;
                    }
                    if (!v0) continue;
                    var11_11 = new ArrayList<ScriptValue>();
                    var11_11.add(var4_5);
                    var11_11.add(var8_8);
                    return new ScriptValue.Array(var11_11);
                }
            }
        }
        var12_12 = new ArrayList<E>();
        return new ScriptValue.Array(var12_12);
    }

    public static ScriptValue botMoveEasy(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("board", scriptContext.getClassOrVar("board"));
        return Gomoku.findAdjacentMove(builder2);
    }

    public static ScriptValue botMoveMedium(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("board", scriptContext.getClassOrVar("board"));
        ScriptValue scriptValue = Gomoku.findCandidates(builder2);
        builder.val("candidates", scriptValue);
        double d = -1.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)d);
        builder.val("best_r", scriptValue2);
        double d2 = -1.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)d2);
        builder.val("best_c", scriptValue3);
        double d3 = -1.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)d3);
        builder.val("best_score", scriptValue4);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("score");
        ScriptValue scriptValue6 = ScriptValue.of((double)d3);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("r");
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("c");
        if (list != null) {
            for (ScriptValue scriptValue9 : list) {
                builder.val("cell", scriptValue9);
                ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("r", scriptValue10);
                scriptValue7 = scriptValue10;
                ScriptValue scriptValue11 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("c", scriptValue11);
                scriptValue8 = scriptValue11;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("board", scriptContext.getClassOrVar("board"));
                builder4.val("row", scriptValue7);
                builder4.val("col", scriptValue8);
                builder4.val("color", scriptContext.getClassOrVar("color"));
                builder3.val("offense", Gomoku.evaluateCell(builder4));
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("board", scriptContext.getClassOrVar("board"));
                builder5.val("row", scriptValue7);
                builder5.val("col", scriptValue8);
                builder5.val("color", scriptContext.getClassOrVar("opponent"));
                builder3.val("defense", Gomoku.evaluateCell(builder5));
                ScriptValue scriptValue12 = Gomoku.combinedScore(builder3);
                builder.val("score", scriptValue12);
                scriptValue5 = scriptValue12;
                if (!(scriptValue5.asNum() > scriptValue6.asNum())) continue;
                ScriptValue scriptValue13 = scriptValue5;
                builder.val("best_score", scriptValue13);
                scriptValue6 = scriptValue13;
                ScriptValue scriptValue14 = scriptValue7;
                builder.val("best_r", scriptValue14);
                ScriptValue scriptValue15 = scriptValue8;
                builder.val("best_c", scriptValue15);
            }
        }
        if (scriptContext.getNum("best_r") < 0.0) {
            ArrayList arrayList = new ArrayList();
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("best_r"));
        arrayList.add(scriptContext.getClassOrVar("best_c"));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue botMoveHard(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("board", scriptContext.getClassOrVar("board"));
        ScriptValue scriptValue = Gomoku.findCandidates(builder2);
        builder.val("candidates", scriptValue);
        double d = -999999.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)d);
        builder.val("best_net", scriptValue2);
        double d2 = -1.0;
        ScriptValue scriptValue3 = ScriptValue.of((double)d2);
        builder.val("best_r", scriptValue3);
        double d3 = -1.0;
        ScriptValue scriptValue4 = ScriptValue.of((double)d3);
        builder.val("best_c", scriptValue4);
        double d4 = 0.0;
        ScriptValue scriptValue5 = ScriptValue.of((double)0.0);
        builder.val("considered", scriptValue5);
        List list = ScriptProgram.elementsOf((ScriptValue)scriptValue);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("ocol");
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("c");
        ScriptValue scriptValue8 = ScriptValue.of((double)d4);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("my_score");
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("trial");
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("r");
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("opp_candidates");
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("opp_best");
        ScriptValue scriptValue14 = ScriptValue.of((double)d);
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("opp_considered");
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("ocell");
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("reply");
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("net");
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("orow");
        if (list != null) {
            for (ScriptValue scriptValue20 : list) {
                builder.val("cell", scriptValue20);
                ScriptValue scriptValue21 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("r", scriptValue21);
                scriptValue11 = scriptValue21;
                ScriptValue scriptValue22 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("c", scriptValue22);
                scriptValue7 = scriptValue22;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("board", scriptContext.getClassOrVar("board"));
                builder4.val("row", scriptValue11);
                builder4.val("col", scriptValue7);
                builder4.val("color", scriptContext.getClassOrVar("color"));
                builder3.val("offense", Gomoku.evaluateCell(builder4));
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("board", scriptContext.getClassOrVar("board"));
                builder5.val("row", scriptValue11);
                builder5.val("col", scriptValue7);
                builder5.val("color", scriptContext.getClassOrVar("opponent"));
                builder3.val("defense", Gomoku.evaluateCell(builder5));
                ScriptValue scriptValue23 = Gomoku.combinedScore(builder3);
                builder.val("my_score", scriptValue23);
                scriptValue9 = scriptValue23;
                if (!(scriptValue9.asNum() >= 40.0 && scriptValue8.asNum() < 8.0)) continue;
                ScriptValue scriptValue24 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)));
                builder.val("considered", scriptValue24);
                scriptValue8 = scriptValue24;
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("board", scriptContext.getClassOrVar("board"));
                builder6.val("r", scriptValue11);
                builder6.val("c", scriptValue7);
                builder6.val("val", scriptContext.getClassOrVar("color"));
                ScriptValue scriptValue25 = Gomoku.boardSet(builder6);
                builder.val("trial", scriptValue25);
                scriptValue10 = scriptValue25;
                ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                builder7.val("board", scriptValue10);
                ScriptValue scriptValue26 = Gomoku.findCandidates(builder7);
                builder.val("opp_candidates", scriptValue26);
                scriptValue12 = scriptValue26;
                double d5 = 0.0;
                ScriptValue scriptValue27 = ScriptValue.of((double)0.0);
                builder.val("opp_best", scriptValue27);
                scriptValue13 = scriptValue27;
                double d6 = 0.0;
                ScriptValue scriptValue28 = ScriptValue.of((double)0.0);
                builder.val("opp_considered", scriptValue28);
                scriptValue15 = scriptValue28;
                List list2 = ScriptProgram.elementsOf((ScriptValue)scriptValue12);
                if (list2 != null) {
                    for (ScriptValue scriptValue29 : list2) {
                        builder.val("ocell", scriptValue29);
                        ScriptValue scriptValue30 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("opp_considered"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)));
                        builder.val("opp_considered", scriptValue30);
                        scriptValue15 = scriptValue30;
                        if (!(scriptValue15.asNum() <= 30.0)) continue;
                        ScriptValue scriptValue31 = scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("orow", scriptValue31);
                        scriptValue19 = scriptValue31;
                        ScriptValue scriptValue32 = scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("ocol", scriptValue32);
                        scriptValue6 = scriptValue32;
                        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                        ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
                        builder9.val("board", scriptValue10);
                        builder9.val("row", scriptValue19);
                        builder9.val("col", scriptValue6);
                        builder9.val("color", scriptContext.getClassOrVar("opponent"));
                        builder8.val("offense", Gomoku.evaluateCell(builder9));
                        ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
                        builder10.val("board", scriptValue10);
                        builder10.val("row", scriptValue19);
                        builder10.val("col", scriptValue6);
                        builder10.val("color", scriptContext.getClassOrVar("color"));
                        builder8.val("defense", Gomoku.evaluateCell(builder10));
                        ScriptValue scriptValue33 = Gomoku.combinedScore(builder8);
                        builder.val("reply", scriptValue33);
                        scriptValue17 = scriptValue33;
                        if (!(scriptValue17.asNum() > scriptContext.getNum("opp_best"))) continue;
                        ScriptValue scriptValue34 = scriptValue17;
                        builder.val("opp_best", scriptValue34);
                        scriptValue13 = scriptValue34;
                    }
                }
                double d7 = scriptValue9.asNum() - scriptContext.getNum("opp_best") * 0.9;
                ScriptValue scriptValue35 = ScriptValue.of((double)d7);
                builder.val("net", scriptValue35);
                scriptValue18 = scriptValue35;
                if (!(scriptValue18.asNum() > scriptValue14.asNum())) continue;
                ScriptValue scriptValue36 = scriptValue18;
                builder.val("best_net", scriptValue36);
                scriptValue14 = scriptValue36;
                ScriptValue scriptValue37 = scriptValue11;
                builder.val("best_r", scriptValue37);
                ScriptValue scriptValue38 = scriptValue7;
                builder.val("best_c", scriptValue38);
            }
        }
        if (scriptContext.getNum("best_r") < 0.0) {
            ArrayList arrayList = new ArrayList();
            return new ScriptValue.Array(arrayList);
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("best_r"));
        arrayList.add(scriptContext.getClassOrVar("best_c"));
        return new ScriptValue.Array(arrayList);
    }

    public static ScriptValue botPickMove(ScriptContext.Builder builder) {
        ScriptContext scriptContext;
        block5: {
            scriptContext = builder.peek();
            ScriptValue scriptValue = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("color"), (ScriptValue)scriptContext.getClassOrVar("CELL_WHITE")) ? scriptContext.getClassOrVar("CELL_BLACK") : scriptContext.getClassOrVar("CELL_WHITE");
            builder.val("opponent", scriptValue);
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("board", scriptContext.getClassOrVar("board"));
            builder2.val("color", scriptContext.getClassOrVar("color"));
            ScriptValue scriptValue2 = Gomoku.findWinningMove(builder2);
            builder.val("win", scriptValue2);
            if (ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue2, (ScriptContext)scriptContext).asNum() > 0.0) {
                return scriptValue2;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("board", scriptContext.getClassOrVar("board"));
            builder3.val("color", scriptValue);
            ScriptValue scriptValue3 = Gomoku.findWinningMove(builder3);
            builder.val("block", scriptValue3);
            if (ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asNum() > 0.0) {
                return scriptValue3;
            }
            ArrayList arrayList = new ArrayList();
            ScriptValue.Array array = new ScriptValue.Array(arrayList);
            builder.val("move", (ScriptValue)array);
            if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("difficulty"), (ScriptValue)scriptContext.getClassOrVar("DIFF_HARD"))) {
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("board", scriptContext.getClassOrVar("board"));
                builder4.val("color", scriptContext.getClassOrVar("color"));
                builder4.val("opponent", scriptValue);
                ScriptValue scriptValue4 = Gomoku.botMoveHard(builder4);
                builder.val("move", scriptValue4);
            }
            if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("difficulty"), (ScriptValue)scriptContext.getClassOrVar("DIFF_MEDIUM"))) {
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("board", scriptContext.getClassOrVar("board"));
                builder5.val("color", scriptContext.getClassOrVar("color"));
                builder5.val("opponent", scriptValue);
                ScriptValue scriptValue5 = Gomoku.botMoveMedium(builder5);
                builder.val("move", scriptValue5);
            }
            if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("difficulty"), (ScriptValue)scriptContext.getClassOrVar("DIFF_EASY"))) {
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("board", scriptContext.getClassOrVar("board"));
                builder6.val("color", scriptContext.getClassOrVar("color"));
                builder6.val("opponent", scriptValue);
                ScriptValue scriptValue6 = Gomoku.botMoveEasy(builder6);
                builder.val("move", scriptValue6);
            }
            if (!ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptContext.getClassOrVar("move"), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)))) break block5;
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("board", scriptContext.getClassOrVar("board"));
            ScriptValue scriptValue7 = Gomoku.findAnyMove(builder7);
            builder.val("move", scriptValue7);
        }
        return scriptContext.getClassOrVar("move");
    }

    public static ScriptValue botMove(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.statusKey(builder2);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
            object3 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)object3, (String)"active") ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue4 = Gomoku.turnKey(builder3);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue3);
            object2 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue4.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)object2, (ScriptValue)scriptContext.getClassOrVar("CELL_WHITE")) ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Server");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue6 = Gomoku.boardKey(builder4);
            String string = "string";
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue5);
            object = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue6.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object;
        builder.val("board", scriptValue7);
        ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
        builder5.val("gid", scriptContext.getClassOrVar("gid"));
        ScriptValue scriptValue8 = Gomoku.botDifficulty(builder5);
        builder.val("difficulty", scriptValue8);
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("TaskManager");
        Object object4 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule_async", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:bot_compute_move:" + scriptContext.getStr("gid") + ":" + scriptValue7.asStr() + ":" + scriptValue8.asStr())), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue botComputeMove(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        builder2.val("board", scriptContext.getClassOrVar("board"));
        builder2.val("color", scriptContext.getClassOrVar("CELL_WHITE"));
        builder2.val("difficulty", scriptContext.getClassOrVar("difficulty"));
        ScriptValue scriptValue = Gomoku.botPickMove(builder2);
        builder.val("move", scriptValue);
        if (ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin1((String)"len", (ScriptValue)scriptValue, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("TaskManager");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:bot_apply_move:" + scriptContext.getStr("gid") + ":" + (scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + ":" + (scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr())), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue botApplyMove(ScriptContext.Builder builder) {
        block13: {
            Object object;
            Object object2;
            Object object3;
            Object object4;
            Object object5;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("row"), (ScriptContext)scriptContext);
            builder.val("row", scriptValue);
            ScriptValue scriptValue2 = ScriptFormula.callBuiltin1((String)"int", (ScriptValue)scriptContext.getClassOrVar("col"), (ScriptContext)scriptContext);
            builder.val("col", scriptValue2);
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue4 = Gomoku.statusKey(builder2);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue3);
                object5 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue4.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object5 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)object5, (String)"active") ^ true) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Server");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue6 = Gomoku.turnKey(builder3);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue5);
                object4 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue6.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object4 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqual((ScriptValue)object4, (ScriptValue)scriptContext.getClassOrVar("CELL_WHITE")) ^ true) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue8 = Gomoku.boardKey(builder4);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue7);
                object3 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue8.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = object3;
            builder.val("board", scriptValue9);
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("board", scriptValue9);
            builder5.val("r", scriptValue);
            builder5.val("c", scriptValue2);
            if (ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(builder5), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY")) ^ true) {
                return ScriptValue.NULL;
            }
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("gid", scriptContext.getClassOrVar("gid"));
            builder6.val("row", scriptValue);
            builder6.val("col", scriptValue2);
            builder6.val("color", scriptContext.getClassOrVar("CELL_WHITE"));
            Gomoku.applyMove(builder6);
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Server");
            if (scriptValue10 != ScriptValue.NULL) {
                ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                builder7.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue11 = Gomoku.blackKey(builder7);
                String string = "string";
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue10);
                object2 = polyClassServer != null ? polyClassServer.tm$6_get_typed(scriptValue11.asStr(), string) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = object2;
            builder.val("black", scriptValue12);
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Server");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue scriptValue14 = scriptValue12;
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue13);
                object = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue14.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue15 = object;
            builder.val("human", scriptValue15);
            if (!(ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue15, (ScriptContext)scriptContext).asBool() ^ true)) break block13;
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("gid", scriptContext.getClassOrVar("gid"));
            builder8.val("viewer", scriptValue15);
            Gomoku.showBoard(builder8);
        }
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 14.0;
        ScriptValue scriptValue = ScriptValue.of((double)14.0);
        builder.val("BOARD_SIZE", scriptValue);
        double d2 = 5.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)5.0);
        builder.val("WIN_LEN", scriptValue2);
        ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "E");
        builder.val("CELL_EMPTY", scriptValue3);
        ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "B");
        builder.val("CELL_BLACK", scriptValue4);
        ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "W");
        builder.val("CELL_WHITE", scriptValue5);
        ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "Bot");
        builder.val("BOT_NAME", scriptValue6);
        ScriptValue scriptValue7 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "easy");
        builder.val("DIFF_EASY", scriptValue7);
        ScriptValue scriptValue8 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "medium");
        builder.val("DIFF_MEDIUM", scriptValue8);
        ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "hard");
        builder.val("DIFF_HARD", scriptValue9);
        ScriptValue scriptValue10 = scriptValue8;
        builder.val("DIFF_DEFAULT", scriptValue10);
        double d3 = 20.0;
        ScriptValue scriptValue11 = ScriptValue.of((double)20.0);
        builder.val("CELL_WIDTH", scriptValue11);
        FILE_SCOPE = builder.build();
    }
}
