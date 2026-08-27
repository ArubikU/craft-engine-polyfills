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
package dev.arubik.craftengine.script.gen.games;

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
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("name2"));
        if (ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin((String)"lower", arrayList, (ScriptContext)scriptContext), (String)"bot")) {
            return ScriptValue.of((String)("gomoku_" + scriptContext.getStr("name1") + "_bot"));
        }
        ArrayList<ScriptValue.Array> arrayList2 = new ArrayList<ScriptValue.Array>();
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(scriptContext.getClassOrVar("name1"));
        arrayList3.add(scriptContext.getClassOrVar("name2"));
        arrayList2.add(new ScriptValue.Array(arrayList3));
        ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"sort_strs", arrayList2, (ScriptContext)scriptContext);
        builder.val("pair", scriptValue);
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("pair");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("pair");
        return ScriptValue.of((String)("gomoku_" + (scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "_" + (scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()));
    }

    public static ScriptValue emptyBoard(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "");
        builder.val("row", scriptValue);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList.add(scriptContext.getClassOrVar("BOARD_SIZE"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue2 : list) {
                builder.val("i", scriptValue2);
                ScriptValue scriptValue3 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("row"), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY"));
                builder.val("row", scriptValue3);
            }
        }
        ScriptValue scriptValue4 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "");
        builder.val("board", scriptValue4);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList2.add(scriptContext.getClassOrVar("BOARD_SIZE"));
        List list2 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList2, (ScriptContext)scriptContext));
        if (list2 != null) {
            for (ScriptValue scriptValue5 : list2) {
                builder.val("i", scriptValue5);
                ScriptValue scriptValue6 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("board"), (ScriptValue)scriptContext.getClassOrVar("row"));
                builder.val("board", scriptValue6);
            }
        }
        return scriptContext.getClassOrVar("board");
    }

    public static ScriptValue cellAt(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("board"));
        arrayList.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)(scriptContext.getNum("r") * scriptContext.getNum("BOARD_SIZE"))), (ScriptValue)scriptContext.getClassOrVar("c")));
        return ScriptFormula.callBuiltin((String)"char_at", arrayList, (ScriptContext)scriptContext);
    }

    public static ScriptValue boardSet(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((double)(scriptContext.getNum("r") * scriptContext.getNum("BOARD_SIZE"))), (ScriptValue)scriptContext.getClassOrVar("c"));
        builder.val("idx", scriptValue);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("board"));
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList.add(scriptValue);
        ScriptValue scriptValue2 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.callBuiltin((String)"substring", arrayList, (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("val"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("board"));
        arrayList2.add(ScriptFormula.addPolymorphic((ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(scriptContext.getClassOrVar("board"));
        arrayList2.add(ScriptFormula.callBuiltin((String)"len", arrayList3, (ScriptContext)scriptContext));
        return ScriptFormula.addPolymorphic((ScriptValue)scriptValue2, (ScriptValue)ScriptFormula.callBuiltin((String)"substring", arrayList2, (ScriptContext)scriptContext));
    }

    public static ScriptValue isFull(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList.add(ScriptValue.of((double)(scriptContext.getNum("BOARD_SIZE") * scriptContext.getNum("BOARD_SIZE"))));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue : list) {
                builder.val("i", scriptValue);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptContext.getClassOrVar("board"));
                arrayList2.add(scriptContext.getClassOrVar("i"));
                if (!ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin((String)"char_at", arrayList2, (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY"))) continue;
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
        while (var7_6 < 1000) {
            ++var7_6;
            if (!(((var1_1.getNum("r") >= 0.0 != false && var1_1.getNum("r") < var1_1.getNum("BOARD_SIZE") != false) != false && var1_1.getNum("c") >= 0.0 != false) != false && var1_1.getNum("c") < var1_1.getNum("BOARD_SIZE") != false)) ** GOTO lbl-1000
            var8_7 = ScriptContext.builder().copyFrom(var1_1);
            var8_7.val("board", var1_1.getClassOrVar("board"));
            var8_7.val("r", var1_1.getClassOrVar("r"));
            var8_7.val("c", var1_1.getClassOrVar("c"));
            if (ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(var8_7), (ScriptValue)var1_1.getClassOrVar("color"))) {
                v0 = true;
            } else lbl-1000:
            // 2 sources

            {
                v0 = false;
            }
            if (!v0) break;
            var9_8 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("n"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)));
            var0.val("n", var9_8);
            var10_9 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("r"), (ScriptValue)var1_1.getClassOrVar("dr"));
            var0.val("r", var10_9);
            var11_10 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("c"), (ScriptValue)var1_1.getClassOrVar("dc"));
            var0.val("c", var11_10);
        }
        return var1_1.getClassOrVar("n");
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
        if (list != null) {
            for (ScriptValue scriptValue : list) {
                builder.val("d", scriptValue);
                ScriptValue scriptValue2 = scriptContext.getClassOrVar("d");
                ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dr", scriptValue3);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("d");
                ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dc", scriptValue5);
                ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0);
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("board", scriptContext.getClassOrVar("board"));
                builder2.val("row", scriptContext.getClassOrVar("row"));
                builder2.val("col", scriptContext.getClassOrVar("col"));
                builder2.val("dr", scriptValue3);
                builder2.val("dc", scriptValue5);
                builder2.val("color", scriptContext.getClassOrVar("color"));
                ScriptValue scriptValue7 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue6, (ScriptValue)Gomoku.countDir(builder2));
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("board", scriptContext.getClassOrVar("board"));
                builder3.val("row", scriptContext.getClassOrVar("row"));
                builder3.val("col", scriptContext.getClassOrVar("col"));
                builder3.val("dr", ScriptValue.of((double)(-scriptValue3.asNum())));
                builder3.val("dc", ScriptValue.of((double)(-scriptValue5.asNum())));
                builder3.val("color", scriptContext.getClassOrVar("color"));
                ScriptValue scriptValue8 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue7, (ScriptValue)Gomoku.countDir(builder3));
                builder.val("total", scriptValue8);
                if (!(scriptValue8.asNum() >= scriptContext.getNum("WIN_LEN"))) continue;
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
            ScriptValue.Obj obj;
            Object object2;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.botKey(builder2);
            String string = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object2);
                object = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
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
            ScriptValue.Obj obj;
            Object object2;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.difficultyKey(builder2);
            String string = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object2);
                object = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object;
        builder.val("d", scriptValue3);
        return ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"") ? scriptContext.getClassOrVar("DIFF_DEFAULT") : scriptValue3;
    }

    public static ScriptValue openGomoku(ScriptContext.Builder builder) {
        PolyClassPlayer polyClassPlayer;
        PolyClassPlayer polyClassPlayer2;
        PolyClassPlayer polyClassPlayer3;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "opponent")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("opp", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "<red>\u2718 <white>Usage: /games gomoku <player|bot>";
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer4 = new PolyClassPlayer(object2);
                    v0 = ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(string));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin((String)"lower", arrayList2, (ScriptContext)scriptContext), (String)"bot")) {
            Object object3;
            PolyClassPlayer polyClassPlayer5;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            builder2.val("name1", (ScriptValue)(scriptValue4 != ScriptValue.NULL ? ((polyClassPlayer5 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue4)) != null ? polyClassPlayer5.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            builder2.val("name2", scriptContext.getClassOrVar("BOT_NAME"));
            ScriptValue scriptValue5 = Gomoku.makeGameId(builder2);
            builder.val("gid", scriptValue5);
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("gid", scriptValue5);
                ScriptValue scriptValue7 = Gomoku.statusKey(builder3);
                String string = "string";
                if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object4);
                    object3 = polyClassServer.tm$6_get_typed(scriptValue7.asStr(), string);
                } else {
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object3 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)object3, (String)"active")) {
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("gid", scriptValue5);
                builder4.val("viewer", scriptContext.getClassOrVar("Player"));
                Gomoku.showBoard(builder4);
                return ScriptValue.NULL;
            }
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            Gomoku.showDifficultyPicker(builder5);
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Server");
        if (scriptValue8 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptValue scriptValue9 = scriptValue2;
            if (scriptValue8 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object5);
                object = polyClassServer.tm$10_get_player(scriptValue9.asStr());
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = object;
        builder.val("target", scriptValue10);
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(scriptValue10);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                String string = "<red>\u2718 <white>Player not found or offline.";
                if (scriptValue11 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object6);
                    v3 = ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string));
                } else {
                    v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v3 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("target");
        Object object7 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Player");
        Object object8 = scriptValue13 != ScriptValue.NULL ? ((polyClassPlayer3 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue13)) != null ? polyClassPlayer3.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue13, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object7, (ScriptValue)object8)) {
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                String string = "<red>\u2718 <white>You can't play yourself - try '/games gomoku bot'.";
                if (scriptValue14 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer7 = new PolyClassPlayer(object9);
                    v6 = ScriptValue.of((boolean)polyClassPlayer7.tm$42_send_message(string));
                } else {
                    v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Player");
        builder6.val("name1", (ScriptValue)(scriptValue15 != ScriptValue.NULL ? ((polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue15)) != null ? polyClassPlayer2.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue15, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("target");
        builder6.val("name2", (ScriptValue)(scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue16, (ScriptContext)scriptContext) : ScriptValue.NULL));
        ScriptValue scriptValue17 = Gomoku.makeGameId(builder6);
        builder.val("gid", scriptValue17);
        ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
        builder7.val("gid", scriptValue17);
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("Player");
        builder7.val("starter", (ScriptValue)(scriptValue18 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue18)) != null ? polyClassPlayer.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue18, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("target");
        builder7.val("opponent", (ScriptValue)(scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue19, (ScriptContext)scriptContext) : ScriptValue.NULL));
        builder7.val("is_bot",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 0));
        builder7.val("difficulty", scriptContext.getClassOrVar("DIFF_DEFAULT"));
        Gomoku.ensureGame(builder7);
        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
        builder8.val("gid", scriptValue17);
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
        PolyClassPlayer polyClassPlayer;
        PolyClassPlayer polyClassPlayer2;
        ScriptContext scriptContext = builder.peek();
        ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        builder2.val("name1", (ScriptValue)(scriptValue != ScriptValue.NULL ? ((polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer2.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        builder2.val("name2", scriptContext.getClassOrVar("BOT_NAME"));
        ScriptValue scriptValue2 = Gomoku.makeGameId(builder2);
        builder.val("gid", scriptValue2);
        ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
        builder3.val("gid", scriptValue2);
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        builder3.val("starter", (ScriptValue)(scriptValue3 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue3)) != null ? polyClassPlayer.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue3, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        builder3.val("opponent", scriptContext.getClassOrVar("BOT_NAME"));
        builder3.val("is_bot",  /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 1));
        builder3.val("difficulty", scriptContext.getClassOrVar("difficulty"));
        Gomoku.ensureGame(builder3);
        ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
        builder4.val("gid", scriptValue2);
        builder4.val("viewer", scriptContext.getClassOrVar("Player"));
        Gomoku.showBoard(builder4);
        return ScriptValue.NULL;
    }

    public static ScriptValue ensureGame(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.statusKey(builder2);
            String string = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object2);
                object = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)object, (String)"active")) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue4 = Gomoku.boardKey(builder3);
            String string = "string";
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            ScriptValue scriptValue5 = Gomoku.emptyBoard(builder4);
            if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object3);
                v1 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue4.asStr(), string, scriptValue5));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object4;
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue7 = Gomoku.turnKey(builder5);
            String string = "string";
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("CELL_BLACK");
            if (scriptValue6 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object4);
                v2 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue7.asStr(), string, scriptValue8));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Server");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue10 = Gomoku.blackKey(builder6);
            String string = "string";
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("starter");
            if (scriptValue9 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object5);
                v3 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue10.asStr(), string, scriptValue11));
            } else {
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue11, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("Server");
        if (scriptValue12 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
            builder7.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue13 = Gomoku.whiteKey(builder7);
            String string = "string";
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("opponent");
            if (scriptValue12 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue12).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object6);
                v4 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue13.asStr(), string, scriptValue14));
            } else {
                v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
            }
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("Server");
        if (scriptValue15 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            ScriptValue scriptValue16;
            ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
            builder8.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue17 = Gomoku.botKey(builder8);
            String string = "string";
            ScriptValue scriptValue18 = scriptValue16 = scriptContext.getBool("is_bot") ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "1")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "0"));
            if (scriptValue15 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue15).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object7);
                v6 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue17.asStr(), string, scriptValue16));
            } else {
                v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue15, (ScriptValue)scriptValue17, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue16, (ScriptContext)scriptContext);
            }
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("Server");
        if (scriptValue19 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
            builder9.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue20 = Gomoku.difficultyKey(builder9);
            String string = "string";
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("difficulty");
            if (scriptValue19 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object8);
                v7 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue20.asStr(), string, scriptValue21));
            } else {
                v7 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue19, (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
            }
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("Server");
        if (scriptValue22 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object9;
            ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
            builder10.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue23 = Gomoku.statusKey(builder10);
            String string = "string";
            ScriptValue scriptValue24 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "active");
            if (scriptValue22 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object9);
                v8 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue23.asStr(), string, scriptValue24));
            } else {
                v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue22, (ScriptValue)scriptValue23, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue24, (ScriptContext)scriptContext);
            }
        } else {
            v8 = ScriptValue.NULL;
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
            ScriptValue.Obj obj;
            Object object6;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.boardKey(builder2);
            String string = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object6);
                object5 = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
            } else {
                object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object5;
        builder.val("board", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Server");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue5 = Gomoku.turnKey(builder3);
            String string = "string";
            if (scriptValue4 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object7);
                object4 = polyClassServer.tm$6_get_typed(scriptValue5.asStr(), string);
            } else {
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object4;
        builder.val("turn", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue8 = Gomoku.blackKey(builder4);
            String string = "string";
            if (scriptValue7 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object8);
                object3 = polyClassServer.tm$6_get_typed(scriptValue8.asStr(), string);
            } else {
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object3;
        builder.val("black", scriptValue9);
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Server");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object9;
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue11 = Gomoku.whiteKey(builder5);
            String string = "string";
            if (scriptValue10 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object9);
                object2 = polyClassServer.tm$6_get_typed(scriptValue11.asStr(), string);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = object2;
        builder.val("white", scriptValue12);
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Server");
        if (scriptValue13 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object10;
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue14 = Gomoku.statusKey(builder6);
            String string = "string";
            if (scriptValue13 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object10);
                object = polyClassServer.tm$6_get_typed(scriptValue14.asStr(), string);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
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
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList2.add(scriptContext.getClassOrVar("BOARD_SIZE"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList2, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue17 : list) {
                builder.val("r", scriptValue17);
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                arrayList3.add(scriptContext.getClassOrVar("BOARD_SIZE"));
                List list2 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList3, (ScriptContext)scriptContext));
                if (list2 == null) continue;
                for (ScriptValue scriptValue18 : list2) {
                    builder.val("c", scriptValue18);
                    ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                    builder7.val("board", scriptValue3);
                    builder7.val("r", scriptContext.getClassOrVar("r"));
                    builder7.val("c", scriptContext.getClassOrVar("c"));
                    ScriptValue scriptValue19 = Gomoku.cellAt(builder7);
                    builder.val("ch", scriptValue19);
                    ScriptValue scriptValue20 = bl ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "games/gomoku.pf:on_noop")) : ScriptValue.of((String)("games/gomoku.pf:on_cell_click:" + scriptContext.getStr("gid") + ":" + scriptContext.getStr("r") + ":" + scriptContext.getStr("c")));
                    builder.val("cell_action", scriptValue20);
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "label"));
                    ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                    builder8.val("ch", scriptValue19);
                    arrayList4.add(Gomoku.cellGlyph(builder8));
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "action"));
                    arrayList4.add(scriptValue20);
                    arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "width"));
                    arrayList4.add(scriptContext.getClassOrVar("CELL_WIDTH"));
                    ScriptValue scriptValue21 = ScriptFormula.callBuiltin((String)"make_map", arrayList4, (ScriptContext)scriptContext);
                    builder.val("btn", scriptValue21);
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptContext.getClassOrVar("buttons"));
                    arrayList5.add(scriptValue21);
                    ScriptValue scriptValue22 = ScriptFormula.callBuiltin((String)"push", arrayList5, (ScriptContext)scriptContext);
                    builder.val("buttons", scriptValue22);
                }
            }
        }
        ScriptValue scriptValue23 = bl ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<gray>Leave")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<red>Resign"));
        builder.val("footer_label", scriptValue23);
        ScriptValue scriptValue24 = bl ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "games/gomoku.pf:on_noop")) : ScriptValue.of((String)("games/gomoku.pf:on_resign:" + scriptContext.getStr("gid")));
        builder.val("footer_action", scriptValue24);
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add(scriptContext.getClassOrVar("buttons"));
        ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "label"));
        arrayList7.add(scriptValue23);
        arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "action"));
        arrayList7.add(scriptValue24);
        arrayList6.add(ScriptFormula.callBuiltin((String)"make_map", arrayList7, (ScriptContext)scriptContext));
        ScriptValue scriptValue25 = ScriptFormula.callBuiltin((String)"push", arrayList6, (ScriptContext)scriptContext);
        builder.val("buttons", scriptValue25);
        ScriptValue scriptValue26 = ScriptValue.of((String)("<gold>Gomoku <gray>- <yellow>" + scriptValue9.asStr() + " <gray>vs <yellow>" + scriptValue12.asStr()));
        builder.val("title", scriptValue26);
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Dialog");
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("Images");
        CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "body", (ScriptValue)(scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue27, (ScriptValue)scriptValue26, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)(scriptValue28 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "from", (ScriptValue)scriptValue28, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "gomoku:ui")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext);
        ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
        builder9.val("status", scriptValue15);
        builder9.val("turn", scriptValue6);
        builder9.val("black", scriptValue9);
        builder9.val("white", scriptValue12);
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_multi_action", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "can_close_with_escape", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "body", (ScriptValue)callSite, (ScriptValue)Gomoku.statusLine(builder9), (ScriptContext)scriptContext), (ScriptValue)ScriptValue.of((boolean)(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue15, (String)"active") ^ true)), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("Machine"), (ScriptValue)scriptValue25, (ScriptValue)scriptContext.getClassOrVar("BOARD_SIZE"), (ScriptContext)scriptContext), (ScriptValue)scriptContext.getClassOrVar("viewer"), (ScriptContext)scriptContext);
        return ScriptValue.NULL;
    }

    public static ScriptValue notifyOpponent(ScriptContext.Builder builder) {
        block13: {
            Object object;
            PolyClassPlayer polyClassPlayer;
            Object object2;
            Object object3;
            ScriptContext scriptContext = builder.peek();
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            if (Gomoku.isBotGame(builder2).asBool()) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue2 = Gomoku.blackKey(builder3);
                String string = "string";
                if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object4);
                    object3 = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
                } else {
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = object3;
            builder.val("black", scriptValue3);
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Server");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue5 = Gomoku.whiteKey(builder4);
                String string = "string";
                if (scriptValue4 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object5);
                    object2 = polyClassServer.tm$6_get_typed(scriptValue5.asStr(), string);
                } else {
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = object2;
            builder.val("white", scriptValue6);
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
            ScriptValue scriptValue8 = ScriptFormula.valuesEqual((ScriptValue)(scriptValue7 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue7)) != null ? polyClassPlayer.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue7, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptValue)scriptValue3) ? scriptValue6 : scriptValue3;
            builder.val("other_name", scriptValue8);
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Server");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                ScriptValue scriptValue10 = scriptValue8;
                if (scriptValue9 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object6);
                    object = polyClassServer.tm$10_get_player(scriptValue10.asStr());
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue11 = object;
            builder.val("other", scriptValue11);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue11);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true)) break block13;
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            builder5.val("viewer", scriptValue11);
            Gomoku.showBoard(builder5);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue notifyBothChat(ScriptContext.Builder builder) {
        block18: {
            Object object;
            Object object2;
            Object object3;
            Object object4;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue2 = Gomoku.blackKey(builder2);
                String string = "string";
                if (scriptValue instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object5);
                    object4 = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
                } else {
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = object4;
            builder.val("black", scriptValue3);
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Server");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue5 = Gomoku.whiteKey(builder3);
                String string = "string";
                if (scriptValue4 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object6);
                    object3 = polyClassServer.tm$6_get_typed(scriptValue5.asStr(), string);
                } else {
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object3 = ScriptValue.NULL;
            }
            ScriptValue scriptValue6 = object3;
            builder.val("white", scriptValue6);
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                ScriptValue scriptValue8 = scriptValue3;
                if (scriptValue7 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object7);
                    object2 = polyClassServer.tm$10_get_player(scriptValue8.asStr());
                } else {
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
                }
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue9 = object2;
            builder.val("b", scriptValue9);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue9);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptValue scriptValue10 = scriptContext.getClassOrVar("b");
                Object object8 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)scriptContext.getClassOrVar("message"), (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            if (!(Gomoku.isBotGame(builder4).asBool() ^ true)) break block18;
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Server");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                ScriptValue scriptValue12 = scriptValue6;
                if (scriptValue11 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object9);
                    object = polyClassServer.tm$10_get_player(scriptValue12.asStr());
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = object;
            builder.val("w", scriptValue13);
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptValue13);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList2, (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("w");
                Object object10 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, (ScriptValue)scriptContext.getClassOrVar("message"), (ScriptContext)scriptContext) : ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue applyMove(ScriptContext.Builder builder) {
        block30: {
            Object object;
            Object object2;
            Object object3;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue2 = Gomoku.boardKey(builder2);
                String string = "string";
                if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object4);
                    object3 = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
                } else {
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
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
                ScriptValue.Obj obj;
                Object object5;
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue6 = Gomoku.boardKey(builder4);
                String string = "string";
                ScriptValue scriptValue7 = scriptValue4;
                if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object5);
                    v1 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue6.asStr(), string, scriptValue7));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Server");
            if (scriptValue8 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue9 = Gomoku.blackKey(builder5);
                String string = "string";
                if (scriptValue8 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object6);
                    object2 = polyClassServer.tm$6_get_typed(scriptValue9.asStr(), string);
                } else {
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = object2;
            builder.val("black", scriptValue10);
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Server");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue12 = Gomoku.whiteKey(builder6);
                String string = "string";
                if (scriptValue11 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object7);
                    object = polyClassServer.tm$6_get_typed(scriptValue12.asStr(), string);
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
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
                    ScriptValue.Obj obj;
                    Object object8;
                    ScriptValue scriptValue15;
                    ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                    builder8.val("gid", scriptContext.getClassOrVar("gid"));
                    ScriptValue scriptValue16 = Gomoku.statusKey(builder8);
                    String string = "string";
                    ScriptValue scriptValue17 = scriptValue15 = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("color"), (ScriptValue)scriptContext.getClassOrVar("CELL_BLACK")) ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "black_win")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "white_win"));
                    if (scriptValue14 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Server")) {
                        PolyClassServer polyClassServer = new PolyClassServer(object8);
                        v5 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue16.asStr(), string, scriptValue15));
                    } else {
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue14, (ScriptValue)scriptValue16, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
                    }
                } else {
                    v5 = ScriptValue.NULL;
                }
                ScriptValue scriptValue18 = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("color"), (ScriptValue)scriptContext.getClassOrVar("CELL_BLACK")) ? scriptValue10 : scriptValue13;
                builder.val("winner", scriptValue18);
                ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
                builder9.val("gid", scriptContext.getClassOrVar("gid"));
                builder9.val("message", ScriptValue.of((String)("<gold>\u2726 <white>" + scriptValue18.asStr() + " wins the Gomoku match!")));
                Gomoku.notifyBothChat(builder9);
                return ScriptValue.NULL;
            }
            ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
            builder10.val("board", scriptValue4);
            if (Gomoku.isFull(builder10).asBool()) {
                ScriptValue scriptValue19 = scriptContext.getClassOrVar("Server");
                if (scriptValue19 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object9;
                    ScriptContext.Builder builder11 = ScriptContext.builder().copyFrom(scriptContext);
                    builder11.val("gid", scriptContext.getClassOrVar("gid"));
                    ScriptValue scriptValue20 = Gomoku.statusKey(builder11);
                    String string = "string";
                    ScriptValue scriptValue21 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "draw");
                    if (scriptValue19 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue19).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Server")) {
                        PolyClassServer polyClassServer = new PolyClassServer(object9);
                        v6 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue20.asStr(), string, scriptValue21));
                    } else {
                        v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue19, (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue21, (ScriptContext)scriptContext);
                    }
                } else {
                    v6 = ScriptValue.NULL;
                }
                ScriptContext.Builder builder12 = ScriptContext.builder().copyFrom(scriptContext);
                builder12.val("gid", scriptContext.getClassOrVar("gid"));
                builder12.val("message",  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<yellow>Gomoku match ended in a draw."));
                Gomoku.notifyBothChat(builder12);
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue22 = ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("color"), (ScriptValue)scriptContext.getClassOrVar("CELL_BLACK")) ? scriptContext.getClassOrVar("CELL_WHITE") : scriptContext.getClassOrVar("CELL_BLACK");
            builder.val("next_turn", scriptValue22);
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Server");
            if (scriptValue23 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                ScriptContext.Builder builder13 = ScriptContext.builder().copyFrom(scriptContext);
                builder13.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue24 = Gomoku.turnKey(builder13);
                String string = "string";
                ScriptValue scriptValue25 = scriptValue22;
                if (scriptValue23 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object10);
                    v7 = ScriptValue.of((boolean)polyClassServer.tm$0_set_typed(scriptValue24.asStr(), string, scriptValue25));
                } else {
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue23, (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)scriptValue25, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder14 = ScriptContext.builder().copyFrom(scriptContext);
            builder14.val("gid", scriptContext.getClassOrVar("gid"));
            if (!(Gomoku.isBotGame(builder14).asBool() && ScriptFormula.valuesEqual((ScriptValue)scriptValue22, (ScriptValue)scriptContext.getClassOrVar("CELL_WHITE")))) break block30;
            ScriptValue scriptValue26 = scriptContext.getClassOrVar("TaskManager");
            Object object11 = scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:bot_move:" + scriptContext.getStr("gid"))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 15.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue refreshBoard(ScriptContext.Builder builder) {
        block4: {
            Object object;
            ScriptContext scriptContext = builder.peek();
            ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                ScriptValue scriptValue2 = scriptContext.getClassOrVar("player_name");
                if (scriptValue instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object2);
                    object = polyClassServer.tm$10_get_player(scriptValue2.asStr());
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue3 = object;
            builder.val("p", scriptValue3);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue3);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true)) break block4;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            builder2.val("viewer", scriptValue3);
            Gomoku.showBoard(builder2);
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onCellClick(ScriptContext.Builder builder) {
        PolyClassPlayer polyClassPlayer;
        ScriptValue scriptValue;
        Object object;
        PolyClassPlayer polyClassPlayer2;
        ScriptValue scriptValue2;
        PolyClassPlayer polyClassPlayer3;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("row"));
        ScriptValue scriptValue3 = ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext);
        builder.val("row", scriptValue3);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("col"));
        ScriptValue scriptValue4 = ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext);
        builder.val("col", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Server");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue6 = Gomoku.statusKey(builder2);
            String string = "string";
            if (scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object6);
                object5 = polyClassServer.tm$6_get_typed(scriptValue6.asStr(), string);
            } else {
                object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object5;
        builder.val("status", scriptValue7);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue7, (String)"active") ^ true) {
            PolyClassPlayer polyClassPlayer4;
            ScriptValue scriptValue8;
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "<red>\u2718 <white>This game has already ended - run the command again to start a new one.";
                if (scriptValue9 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer5 = new PolyClassPlayer(object7);
                    v1 = ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("TaskManager");
            Object object8 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((scriptValue8 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassPlayer4.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Server");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object9;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue12 = Gomoku.blackKey(builder3);
            String string = "string";
            if (scriptValue11 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object9);
                object4 = polyClassServer.tm$6_get_typed(scriptValue12.asStr(), string);
            } else {
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = object4;
        builder.val("black", scriptValue13);
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Server");
        if (scriptValue14 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object10;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue15 = Gomoku.whiteKey(builder4);
            String string = "string";
            if (scriptValue14 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object10);
                object3 = polyClassServer.tm$6_get_typed(scriptValue15.asStr(), string);
            } else {
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue16 = object3;
        builder.val("white", scriptValue16);
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("Server");
        if (scriptValue17 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object11;
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue18 = Gomoku.turnKey(builder5);
            String string = "string";
            if (scriptValue17 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue17).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object11);
                object2 = polyClassServer.tm$6_get_typed(scriptValue18.asStr(), string);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue17, (ScriptValue)scriptValue18, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = object2;
        builder.val("turn", scriptValue19);
        ScriptValue scriptValue20 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "");
        builder.val("my_color", scriptValue20);
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("Player");
        Object object12 = scriptValue21 != ScriptValue.NULL ? ((polyClassPlayer3 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue21)) != null ? polyClassPlayer3.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue21, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object12, (ScriptValue)scriptValue13)) {
            ScriptValue scriptValue22 = scriptContext.getClassOrVar("CELL_BLACK");
            builder.val("my_color", scriptValue22);
        }
        Object object13 = (scriptValue2 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer2.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object13, (ScriptValue)scriptValue16)) {
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("CELL_WHITE");
            builder.val("my_color", scriptValue23);
        }
        if (scriptContext.getStr("my_color").equals("")) {
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("Player");
            if (scriptValue24 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                String string = "<red>\u2718 <white>You're not a player in this game.";
                if (scriptValue24 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object14);
                    v8 = ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string));
                } else {
                    v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue24, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v8 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("my_color"), (ScriptValue)scriptValue19) ^ true) {
            PolyClassPlayer polyClassPlayer7;
            ScriptValue scriptValue25;
            ScriptValue scriptValue26 = scriptContext.getClassOrVar("Player");
            if (scriptValue26 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object15;
                String string = "<red>\u2718 <white>It's not your turn.";
                if (scriptValue26 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue26).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer8 = new PolyClassPlayer(object15);
                    v9 = ScriptValue.of((boolean)polyClassPlayer8.tm$42_send_message(string));
                } else {
                    v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue26, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
            ScriptValue scriptValue27 = scriptContext.getClassOrVar("TaskManager");
            Object object16 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue27, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((scriptValue25 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer7 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue25)) != null ? polyClassPlayer7.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue25, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("Server");
        if (scriptValue28 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object17;
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue29 = Gomoku.boardKey(builder6);
            String string = "string";
            if (scriptValue28 instanceof ScriptValue.Obj && (object17 = (obj = (ScriptValue.Obj)scriptValue28).instance()) != null && !(object17 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object17);
                object = polyClassServer.tm$6_get_typed(scriptValue29.asStr(), string);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue28, (ScriptValue)scriptValue29, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue30 = object;
        builder.val("board", scriptValue30);
        ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
        builder7.val("board", scriptValue30);
        builder7.val("r", scriptValue3);
        builder7.val("c", scriptValue4);
        if (ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(builder7), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY")) ^ true) {
            PolyClassPlayer polyClassPlayer9;
            ScriptValue scriptValue31;
            ScriptValue scriptValue32 = scriptContext.getClassOrVar("Player");
            if (scriptValue32 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object18;
                String string = "<red>\u2718 <white>That cell is already taken.";
                if (scriptValue32 instanceof ScriptValue.Obj && (object18 = (obj = (ScriptValue.Obj)scriptValue32).instance()) != null && !(object18 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer10 = new PolyClassPlayer(object18);
                    v12 = ScriptValue.of((boolean)polyClassPlayer10.tm$42_send_message(string));
                } else {
                    v12 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue32, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v12 = ScriptValue.NULL;
            }
            ScriptValue scriptValue33 = scriptContext.getClassOrVar("TaskManager");
            Object object19 = scriptValue33 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue33, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((scriptValue31 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer9 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue31)) != null ? polyClassPlayer9.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue31, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
        builder8.val("gid", scriptContext.getClassOrVar("gid"));
        builder8.val("row", scriptValue3);
        builder8.val("col", scriptValue4);
        builder8.val("color", scriptContext.getClassOrVar("my_color"));
        Gomoku.applyMove(builder8);
        ScriptValue scriptValue34 = scriptContext.getClassOrVar("TaskManager");
        Object object20 = scriptValue34 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue34, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()))), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
        builder9.val("gid", scriptContext.getClassOrVar("gid"));
        Gomoku.notifyOpponent(builder9);
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue onResign(ScriptContext.Builder var0) {
        var1_1 = var0.peek();
        var2_2 = var1_1.getClassOrVar("Server");
        if (var2_2 != ScriptValue.NULL) {
            var4_3 = ScriptContext.builder().copyFrom(var1_1);
            var4_3.val("gid", var1_1.getClassOrVar("gid"));
            var3_4 = Gomoku.statusKey(var4_3);
            var5_5 = "string";
            if (var2_2 instanceof ScriptValue.Obj && (var7_7 = (var6_6 = (ScriptValue.Obj)var2_2).instance()) != null && !(var7_7 instanceof PolyClass) && var6_6.typeName().equals("Server")) {
                var8_8 = new PolyClassServer(var7_7);
                v0 /* !! */  = var8_8.tm$6_get_typed(var3_4.asStr(), var5_5);
            } else {
                v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var2_2, (ScriptValue)var3_4, (ScriptValue)ScriptValue.of((String)var5_5), (ScriptContext)var1_1);
            }
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var9_9 = v0 /* !! */ ;
        var0.val("status", var9_9);
        if (ScriptFormula.valuesEqualStr((ScriptValue)var9_9, (String)"active") ^ true) {
            var10_10 = var1_1.getClassOrVar("Player");
            if (var10_10 != ScriptValue.NULL) {
                var11_11 = "<yellow>This game has already ended.";
                if (var10_10 instanceof ScriptValue.Obj && (var13_13 = (var12_12 = (ScriptValue.Obj)var10_10).instance()) != null && !(var13_13 instanceof PolyClass) && var12_12.typeName().equals("Player")) {
                    var14_14 = new PolyClassPlayer(var13_13);
                    v1 /* !! */  = ScriptValue.of((boolean)var14_14.tm$42_send_message(var11_11));
                } else {
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var10_10, (ScriptValue)ScriptValue.of((String)var11_11), (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var15_15 = var1_1.getClassOrVar("Server");
        if (var15_15 != ScriptValue.NULL) {
            var17_16 = ScriptContext.builder().copyFrom(var1_1);
            var17_16.val("gid", var1_1.getClassOrVar("gid"));
            var16_17 = Gomoku.blackKey(var17_16);
            var18_18 = "string";
            if (var15_15 instanceof ScriptValue.Obj && (var20_20 = (var19_19 = (ScriptValue.Obj)var15_15).instance()) != null && !(var20_20 instanceof PolyClass) && var19_19.typeName().equals("Server")) {
                var21_21 = new PolyClassServer(var20_20);
                v2 /* !! */  = var21_21.tm$6_get_typed(var16_17.asStr(), var18_18);
            } else {
                v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var15_15, (ScriptValue)var16_17, (ScriptValue)ScriptValue.of((String)var18_18), (ScriptContext)var1_1);
            }
        } else {
            v2 /* !! */  = ScriptValue.NULL;
        }
        var22_22 = v2 /* !! */ ;
        var0.val("black", var22_22);
        var23_23 = var1_1.getClassOrVar("Server");
        if (var23_23 != ScriptValue.NULL) {
            var25_24 = ScriptContext.builder().copyFrom(var1_1);
            var25_24.val("gid", var1_1.getClassOrVar("gid"));
            var24_25 = Gomoku.whiteKey(var25_24);
            var26_26 = "string";
            if (var23_23 instanceof ScriptValue.Obj && (var28_28 = (var27_27 = (ScriptValue.Obj)var23_23).instance()) != null && !(var28_28 instanceof PolyClass) && var27_27.typeName().equals("Server")) {
                var29_29 = new PolyClassServer(var28_28);
                v3 /* !! */  = var29_29.tm$6_get_typed(var24_25.asStr(), var26_26);
            } else {
                v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var23_23, (ScriptValue)var24_25, (ScriptValue)ScriptValue.of((String)var26_26), (ScriptContext)var1_1);
            }
        } else {
            v3 /* !! */  = ScriptValue.NULL;
        }
        var30_30 = v3 /* !! */ ;
        var0.val("white", var30_30);
        var31_31 = var1_1.getClassOrVar("Player");
        v4 /* !! */  = var31_31 != ScriptValue.NULL ? ((var32_32 = PolyClassPlayer.ofGuarded((ScriptValue)var31_31)) != null ? var32_32.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var31_31, (ScriptContext)var1_1)) : ScriptValue.NULL;
        if (!(ScriptFormula.valuesEqual((ScriptValue)v4 /* !! */ , (ScriptValue)var22_22) ^ true)) ** GOTO lbl-1000
        var33_33 = var1_1.getClassOrVar("Player");
        v5 /* !! */  = var33_33 != ScriptValue.NULL ? ((var34_34 = PolyClassPlayer.ofGuarded((ScriptValue)var33_33)) != null ? var34_34.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var33_33, (ScriptContext)var1_1)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)v5 /* !! */ , (ScriptValue)var30_30) ^ true) {
            v6 = true;
        } else lbl-1000:
        // 2 sources

        {
            v6 = false;
        }
        if (v6) {
            var35_35 = var1_1.getClassOrVar("Player");
            if (var35_35 != ScriptValue.NULL) {
                var36_36 = "<red>\u2718 <white>You're not a player in this game.";
                if (var35_35 instanceof ScriptValue.Obj && (var38_38 = (var37_37 = (ScriptValue.Obj)var35_35).instance()) != null && !(var38_38 instanceof PolyClass) && var37_37.typeName().equals("Player")) {
                    var39_39 = new PolyClassPlayer(var38_38);
                    v7 /* !! */  = ScriptValue.of((boolean)var39_39.tm$42_send_message(var36_36));
                } else {
                    v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var35_35, (ScriptValue)ScriptValue.of((String)var36_36), (ScriptContext)var1_1);
                }
            } else {
                v7 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var40_40 = var1_1.getClassOrVar("Player");
        var42_42 = ScriptFormula.valuesEqual((ScriptValue)(var40_40 != ScriptValue.NULL ? ((var41_41 = PolyClassPlayer.ofGuarded((ScriptValue)var40_40)) != null ? var41_41.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var40_40, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)var22_22);
        var43_43 = ScriptValue.of((boolean)var42_42);
        var0.val("resigner_is_black", var43_43);
        var44_44 = var1_1.getClassOrVar("Server");
        if (var44_44 != ScriptValue.NULL) {
            var46_45 = ScriptContext.builder().copyFrom(var1_1);
            var46_45.val("gid", var1_1.getClassOrVar("gid"));
            var45_46 = Gomoku.statusKey(var46_45);
            var47_47 = "string";
            v8 = var48_48 = var42_42 != false ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "white_win")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "black_win"));
            if (var44_44 instanceof ScriptValue.Obj && (var50_50 = (var49_49 = (ScriptValue.Obj)var44_44).instance()) != null && !(var50_50 instanceof PolyClass) && var49_49.typeName().equals("Server")) {
                var51_51 = new PolyClassServer(var50_50);
                v9 /* !! */  = ScriptValue.of((boolean)var51_51.tm$0_set_typed(var45_46.asStr(), var47_47, var48_48));
            } else {
                v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var44_44, (ScriptValue)var45_46, (ScriptValue)ScriptValue.of((String)var47_47), (ScriptValue)var48_48, (ScriptContext)var1_1);
            }
        } else {
            v9 /* !! */  = ScriptValue.NULL;
        }
        var52_52 = var42_42 != false ? var30_30 : var22_22;
        var0.val("winner", var52_52);
        var53_53 = ScriptContext.builder().copyFrom(var1_1);
        var53_53.val("gid", var1_1.getClassOrVar("gid"));
        var54_54 = var1_1.getClassOrVar("Player");
        var53_53.val("message", ScriptValue.of((String)("<yellow>" + (var54_54 != ScriptValue.NULL ? ((var55_55 = PolyClassPlayer.ofGuarded((ScriptValue)var54_54)) != null ? var55_55.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var54_54, (ScriptContext)var1_1).asStr()) : ScriptValue.NULL.asStr()) + " resigned. <gold>" + var52_52.asStr() + " wins!")));
        Gomoku.notifyBothChat(var53_53);
        var56_56 = ScriptContext.builder().copyFrom(var1_1);
        var56_56.val("gid", var1_1.getClassOrVar("gid"));
        Gomoku.notifyOpponent(var56_56);
        return ScriptValue.NULL;
    }

    public static ScriptValue onNoop(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.NULL;
    }

    public static ScriptValue findWinningMove(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList.add(scriptContext.getClassOrVar("BOARD_SIZE"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue : list) {
                builder.val("r", scriptValue);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                arrayList2.add(scriptContext.getClassOrVar("BOARD_SIZE"));
                List list2 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList2, (ScriptContext)scriptContext));
                if (list2 == null) continue;
                for (ScriptValue scriptValue2 : list2) {
                    builder.val("c", scriptValue2);
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                    builder2.val("board", scriptContext.getClassOrVar("board"));
                    builder2.val("r", scriptContext.getClassOrVar("r"));
                    builder2.val("c", scriptContext.getClassOrVar("c"));
                    if (!ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(builder2), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY"))) continue;
                    ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                    builder3.val("board", scriptContext.getClassOrVar("board"));
                    builder3.val("r", scriptContext.getClassOrVar("r"));
                    builder3.val("c", scriptContext.getClassOrVar("c"));
                    builder3.val("val", scriptContext.getClassOrVar("color"));
                    ScriptValue scriptValue3 = Gomoku.boardSet(builder3);
                    builder.val("trial", scriptValue3);
                    ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                    builder4.val("board", scriptValue3);
                    builder4.val("row", scriptContext.getClassOrVar("r"));
                    builder4.val("col", scriptContext.getClassOrVar("c"));
                    builder4.val("color", scriptContext.getClassOrVar("color"));
                    if (!Gomoku.hasFive(builder4).asBool()) continue;
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptContext.getClassOrVar("r"));
                    arrayList3.add(scriptContext.getClassOrVar("c"));
                    return new ScriptValue.Array(arrayList3);
                }
            }
        }
        ArrayList arrayList4 = new ArrayList();
        return new ScriptValue.Array(arrayList4);
    }

    public static ScriptValue findAnyMove(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
        arrayList.add(scriptContext.getClassOrVar("BOARD_SIZE"));
        List list = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList, (ScriptContext)scriptContext));
        if (list != null) {
            for (ScriptValue scriptValue : list) {
                builder.val("r", scriptValue);
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                arrayList2.add(scriptContext.getClassOrVar("BOARD_SIZE"));
                List list2 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", arrayList2, (ScriptContext)scriptContext));
                if (list2 == null) continue;
                for (ScriptValue scriptValue2 : list2) {
                    builder.val("c", scriptValue2);
                    ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                    builder2.val("board", scriptContext.getClassOrVar("board"));
                    builder2.val("r", scriptContext.getClassOrVar("r"));
                    builder2.val("c", scriptContext.getClassOrVar("c"));
                    if (!ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(builder2), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY"))) continue;
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptContext.getClassOrVar("r"));
                    arrayList3.add(scriptContext.getClassOrVar("c"));
                    return new ScriptValue.Array(arrayList3);
                }
            }
        }
        ArrayList arrayList4 = new ArrayList();
        return new ScriptValue.Array(arrayList4);
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
        if (list != null) {
            for (ScriptValue scriptValue2 : list) {
                builder.val("d", scriptValue2);
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("d");
                ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dr", scriptValue4);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("d");
                ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("dc", scriptValue6);
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("board", scriptContext.getClassOrVar("board"));
                builder2.val("row", scriptContext.getClassOrVar("row"));
                builder2.val("col", scriptContext.getClassOrVar("col"));
                builder2.val("dr", scriptValue4);
                builder2.val("dc", scriptValue6);
                builder2.val("color", scriptContext.getClassOrVar("color"));
                ScriptValue scriptValue7 = Gomoku.countDir(builder2);
                builder.val("fwd", scriptValue7);
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("board", scriptContext.getClassOrVar("board"));
                builder3.val("row", scriptContext.getClassOrVar("row"));
                builder3.val("col", scriptContext.getClassOrVar("col"));
                builder3.val("dr", ScriptValue.of((double)(-scriptValue4.asNum())));
                builder3.val("dc", ScriptValue.of((double)(-scriptValue6.asNum())));
                builder3.val("color", scriptContext.getClassOrVar("color"));
                ScriptValue scriptValue8 = Gomoku.countDir(builder3);
                builder.val("bwd", scriptValue8);
                ScriptValue scriptValue9 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptValue)scriptValue7), (ScriptValue)scriptValue8);
                builder.val("count", scriptValue9);
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("board", scriptContext.getClassOrVar("board"));
                builder4.val("r", ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("row"), (ScriptValue)ScriptValue.of((double)(scriptValue4.asNum() * ScriptFormula.addPolymorphic((ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))).asNum()))));
                builder4.val("c", ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("col"), (ScriptValue)ScriptValue.of((double)(scriptValue6.asNum() * ScriptFormula.addPolymorphic((ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))).asNum()))));
                ScriptValue scriptValue10 = Gomoku.isOpenCell(builder4);
                builder.val("end1_open", scriptValue10);
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("board", scriptContext.getClassOrVar("board"));
                builder5.val("r", ScriptValue.of((double)(scriptContext.getNum("row") - scriptValue4.asNum() * ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))).asNum())));
                builder5.val("c", ScriptValue.of((double)(scriptContext.getNum("col") - scriptValue6.asNum() * ScriptFormula.addPolymorphic((ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0))).asNum())));
                ScriptValue scriptValue11 = Gomoku.isOpenCell(builder5);
                builder.val("end2_open", scriptValue11);
                double d2 = (scriptValue10.asBool() ? 1.0 : 0.0) + (scriptValue11.asBool() ? 1.0 : 0.0);
                ScriptValue scriptValue12 = ScriptValue.of((double)d2);
                builder.val("open_ends", scriptValue12);
                ScriptValue scriptValue13 = scriptContext.getClassOrVar("total");
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("count", scriptValue9);
                builder6.val("open_ends", ScriptValue.of((double)d2));
                ScriptValue scriptValue14 = ScriptFormula.addPolymorphic((ScriptValue)scriptValue13, (ScriptValue)Gomoku.lineScore(builder6));
                builder.val("total", scriptValue14);
            }
        }
        return scriptContext.getClassOrVar("total");
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
            if (var2_3 == null) break block4;
            for (ScriptValue var4_5 : var2_3) {
                var0.val("dr", var4_5);
                var9_9 = new ArrayList<ScriptValue>();
                var9_9.add(ScriptValue.of((double)(-2.0)));
                var9_9.add(ScriptValue.of((double)(-1.0)));
                var9_9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                var9_9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
                var9_9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 2.0));
                var6_6 = ScriptProgram.elementsOf((ScriptValue)new ScriptValue.Array(var9_9));
                if (var6_6 == null) continue;
                for (ScriptValue var8_8 : var6_6) {
                    var0.val("dc", var8_8);
                    var10_10 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("r"), (ScriptValue)var1_1.getClassOrVar("dr"));
                    var0.val("nr", var10_10);
                    var11_11 = ScriptFormula.addPolymorphic((ScriptValue)var1_1.getClassOrVar("c"), (ScriptValue)var1_1.getClassOrVar("dc"));
                    var0.val("nc", var11_11);
                    if (!(((var10_10.asNum() >= 0.0 != false && var10_10.asNum() < var1_1.getNum("BOARD_SIZE") != false) != false && var11_11.asNum() >= 0.0 != false) != false && var11_11.asNum() < var1_1.getNum("BOARD_SIZE") != false)) ** GOTO lbl-1000
                    var12_12 = ScriptContext.builder().copyFrom(var1_1);
                    var12_12.val("board", var1_1.getClassOrVar("board"));
                    var12_12.val("r", var10_10);
                    var12_12.val("c", var11_11);
                    if (ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(var12_12), (ScriptValue)var1_1.getClassOrVar("CELL_EMPTY")) ^ true) {
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
            var7_4 = new ArrayList<ScriptValue>();
            var7_4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
            var7_4.add(ScriptValue.of((double)(var1_1.getNum("BOARD_SIZE") * var1_1.getNum("BOARD_SIZE"))));
            var4_5 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", var7_4, (ScriptContext)var1_1));
            if (var4_5 != null) {
                for (ScriptValue var6_7 : var4_5) {
                    var0.val("i", var6_7);
                    var8_8 = new ArrayList<ScriptValue>();
                    var8_8.add(var1_1.getClassOrVar("board"));
                    var8_8.add(var1_1.getClassOrVar("i"));
                    if (!(ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin((String)"char_at", var8_8, (ScriptContext)var1_1), (ScriptValue)var1_1.getClassOrVar("CELL_EMPTY")) ^ true)) continue;
                    var9_9 = true;
                    var10_10 = ScriptValue.of((boolean)true);
                    var0.val("any_stone", var10_10);
                }
            }
            if (var1_1.getBool("any_stone") ^ true) {
                var11_11 = new ArrayList<ScriptValue>();
                var12_12 = 2.0;
                var11_11.add(ScriptValue.of((double)(2.0 == 0.0 ? 0.0 : var1_1.getNum("BOARD_SIZE") / var12_12)));
                var14_13 = ScriptFormula.callBuiltin((String)"int", var11_11, (ScriptContext)var1_1);
                var0.val("center", var14_13);
                var15_14 = new ArrayList<ScriptValue.Array>();
                var16_15 = new ArrayList<ScriptValue>();
                var16_15.add(var14_13);
                var16_15.add(var14_13);
                var15_14.add(new ScriptValue.Array(var16_15));
                return new ScriptValue.Array(var15_14);
            }
            var17_16 = new ArrayList<E>();
            var18_17 = new ScriptValue.Array(var17_16);
            var0.val("result", (ScriptValue)var18_17);
            var22_18 = new ArrayList<ScriptValue>();
            var22_18.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
            var22_18.add(var1_1.getClassOrVar("BOARD_SIZE"));
            var19_19 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", var22_18, (ScriptContext)var1_1));
            if (var19_19 == null) break block7;
            for (ScriptValue var21_21 : var19_19) {
                var0.val("r", var21_21);
                var26_25 = new ArrayList<ScriptValue>();
                var26_25.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                var26_25.add(var1_1.getClassOrVar("BOARD_SIZE"));
                var23_22 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", var26_25, (ScriptContext)var1_1));
                if (var23_22 == null) continue;
                for (ScriptValue var25_24 : var23_22) {
                    var0.val("c", var25_24);
                    var27_26 = ScriptContext.builder().copyFrom(var1_1);
                    var27_26.val("board", var1_1.getClassOrVar("board"));
                    var27_26.val("r", var1_1.getClassOrVar("r"));
                    var27_26.val("c", var1_1.getClassOrVar("c"));
                    if (!ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(var27_26), (ScriptValue)var1_1.getClassOrVar("CELL_EMPTY"))) ** GOTO lbl-1000
                    var28_27 = ScriptContext.builder().copyFrom(var1_1);
                    var28_27.val("board", var1_1.getClassOrVar("board"));
                    var28_27.val("r", var1_1.getClassOrVar("r"));
                    var28_27.val("c", var1_1.getClassOrVar("c"));
                    if (Gomoku.hasNeighbor(var28_27).asBool()) {
                        v0 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v0 = false;
                    }
                    if (!v0) continue;
                    var29_28 = new ArrayList<Object>();
                    var29_28.add(var1_1.getClassOrVar("result"));
                    var30_29 = new ArrayList<ScriptValue>();
                    var30_29.add(var1_1.getClassOrVar("r"));
                    var30_29.add(var1_1.getClassOrVar("c"));
                    var29_28.add(new ScriptValue.Array(var30_29));
                    var31_30 = ScriptFormula.callBuiltin((String)"push", var29_28, (ScriptContext)var1_1);
                    var0.val("result", var31_30);
                }
            }
        }
        return var1_1.getClassOrVar("result");
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
            var5_2 = new ArrayList<ScriptValue>();
            var5_2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
            var5_2.add(var1_1.getClassOrVar("BOARD_SIZE"));
            var2_3 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", var5_2, (ScriptContext)var1_1));
            if (var2_3 == null) break block4;
            for (ScriptValue var4_5 : var2_3) {
                var0.val("r", var4_5);
                var9_9 = new ArrayList<ScriptValue>();
                var9_9.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                var9_9.add(var1_1.getClassOrVar("BOARD_SIZE"));
                var6_6 = ScriptProgram.elementsOf((ScriptValue)ScriptFormula.callBuiltin((String)"range", var9_9, (ScriptContext)var1_1));
                if (var6_6 == null) continue;
                for (ScriptValue var8_8 : var6_6) {
                    var0.val("c", var8_8);
                    var10_10 = ScriptContext.builder().copyFrom(var1_1);
                    var10_10.val("board", var1_1.getClassOrVar("board"));
                    var10_10.val("r", var1_1.getClassOrVar("r"));
                    var10_10.val("c", var1_1.getClassOrVar("c"));
                    if (!ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(var10_10), (ScriptValue)var1_1.getClassOrVar("CELL_EMPTY"))) ** GOTO lbl-1000
                    var11_11 = ScriptContext.builder().copyFrom(var1_1);
                    var11_11.val("board", var1_1.getClassOrVar("board"));
                    var11_11.val("r", var1_1.getClassOrVar("r"));
                    var11_11.val("c", var1_1.getClassOrVar("c"));
                    if (Gomoku.hasNeighbor(var11_11).asBool()) {
                        v0 = true;
                    } else lbl-1000:
                    // 2 sources

                    {
                        v0 = false;
                    }
                    if (!v0) continue;
                    var12_12 = new ArrayList<ScriptValue>();
                    var12_12.add(var1_1.getClassOrVar("r"));
                    var12_12.add(var1_1.getClassOrVar("c"));
                    return new ScriptValue.Array(var12_12);
                }
            }
        }
        var13_13 = new ArrayList<E>();
        return new ScriptValue.Array(var13_13);
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
        if (list != null) {
            for (ScriptValue scriptValue5 : list) {
                builder.val("cell", scriptValue5);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("cell");
                ScriptValue scriptValue7 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("r", scriptValue7);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("cell");
                ScriptValue scriptValue9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("c", scriptValue9);
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("board", scriptContext.getClassOrVar("board"));
                builder4.val("row", scriptValue7);
                builder4.val("col", scriptValue9);
                builder4.val("color", scriptContext.getClassOrVar("color"));
                builder3.val("offense", Gomoku.evaluateCell(builder4));
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("board", scriptContext.getClassOrVar("board"));
                builder5.val("row", scriptValue7);
                builder5.val("col", scriptValue9);
                builder5.val("color", scriptContext.getClassOrVar("opponent"));
                builder3.val("defense", Gomoku.evaluateCell(builder5));
                ScriptValue scriptValue10 = Gomoku.combinedScore(builder3);
                builder.val("score", scriptValue10);
                if (!(scriptValue10.asNum() > scriptContext.getNum("best_score"))) continue;
                ScriptValue scriptValue11 = scriptValue10;
                builder.val("best_score", scriptValue11);
                ScriptValue scriptValue12 = scriptValue7;
                builder.val("best_r", scriptValue12);
                ScriptValue scriptValue13 = scriptValue9;
                builder.val("best_c", scriptValue13);
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
        if (list != null) {
            for (ScriptValue scriptValue6 : list) {
                builder.val("cell", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("cell");
                ScriptValue scriptValue8 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("r", scriptValue8);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("cell");
                ScriptValue scriptValue10 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("c", scriptValue10);
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("board", scriptContext.getClassOrVar("board"));
                builder4.val("row", scriptValue8);
                builder4.val("col", scriptValue10);
                builder4.val("color", scriptContext.getClassOrVar("color"));
                builder3.val("offense", Gomoku.evaluateCell(builder4));
                ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
                builder5.val("board", scriptContext.getClassOrVar("board"));
                builder5.val("row", scriptValue8);
                builder5.val("col", scriptValue10);
                builder5.val("color", scriptContext.getClassOrVar("opponent"));
                builder3.val("defense", Gomoku.evaluateCell(builder5));
                ScriptValue scriptValue11 = Gomoku.combinedScore(builder3);
                builder.val("my_score", scriptValue11);
                if (!(scriptValue11.asNum() >= 40.0 && scriptContext.getNum("considered") < 8.0)) continue;
                ScriptValue scriptValue12 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("considered"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)));
                builder.val("considered", scriptValue12);
                ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
                builder6.val("board", scriptContext.getClassOrVar("board"));
                builder6.val("r", scriptValue8);
                builder6.val("c", scriptValue10);
                builder6.val("val", scriptContext.getClassOrVar("color"));
                ScriptValue scriptValue13 = Gomoku.boardSet(builder6);
                builder.val("trial", scriptValue13);
                ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                builder7.val("board", scriptValue13);
                ScriptValue scriptValue14 = Gomoku.findCandidates(builder7);
                builder.val("opp_candidates", scriptValue14);
                double d5 = 0.0;
                ScriptValue scriptValue15 = ScriptValue.of((double)0.0);
                builder.val("opp_best", scriptValue15);
                double d6 = 0.0;
                ScriptValue scriptValue16 = ScriptValue.of((double)0.0);
                builder.val("opp_considered", scriptValue16);
                List list2 = ScriptProgram.elementsOf((ScriptValue)scriptValue14);
                if (list2 != null) {
                    for (ScriptValue scriptValue17 : list2) {
                        builder.val("ocell", scriptValue17);
                        ScriptValue scriptValue18 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("opp_considered"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)));
                        builder.val("opp_considered", scriptValue18);
                        if (!(scriptValue18.asNum() <= 30.0)) continue;
                        ScriptValue scriptValue19 = scriptContext.getClassOrVar("ocell");
                        ScriptValue scriptValue20 = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("orow", scriptValue20);
                        ScriptValue scriptValue21 = scriptContext.getClassOrVar("ocell");
                        ScriptValue scriptValue22 = scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
                        builder.val("ocol", scriptValue22);
                        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
                        ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
                        builder9.val("board", scriptValue13);
                        builder9.val("row", scriptValue20);
                        builder9.val("col", scriptValue22);
                        builder9.val("color", scriptContext.getClassOrVar("opponent"));
                        builder8.val("offense", Gomoku.evaluateCell(builder9));
                        ScriptContext.Builder builder10 = ScriptContext.builder().copyFrom(scriptContext);
                        builder10.val("board", scriptValue13);
                        builder10.val("row", scriptValue20);
                        builder10.val("col", scriptValue22);
                        builder10.val("color", scriptContext.getClassOrVar("color"));
                        builder8.val("defense", Gomoku.evaluateCell(builder10));
                        ScriptValue scriptValue23 = Gomoku.combinedScore(builder8);
                        builder.val("reply", scriptValue23);
                        if (!(scriptValue23.asNum() > scriptContext.getNum("opp_best"))) continue;
                        ScriptValue scriptValue24 = scriptValue23;
                        builder.val("opp_best", scriptValue24);
                    }
                }
                double d7 = scriptValue11.asNum() - scriptContext.getNum("opp_best") * 0.9;
                ScriptValue scriptValue25 = ScriptValue.of((double)d7);
                builder.val("net", scriptValue25);
                if (!(d7 > scriptContext.getNum("best_net"))) continue;
                double d8 = d7;
                ScriptValue scriptValue26 = ScriptValue.of((double)d8);
                builder.val("best_net", scriptValue26);
                ScriptValue scriptValue27 = scriptValue8;
                builder.val("best_r", scriptValue27);
                ScriptValue scriptValue28 = scriptValue10;
                builder.val("best_c", scriptValue28);
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
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue2);
            if (ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext).asNum() > 0.0) {
                return scriptValue2;
            }
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("board", scriptContext.getClassOrVar("board"));
            builder3.val("color", scriptValue);
            ScriptValue scriptValue3 = Gomoku.findWinningMove(builder3);
            builder.val("block", scriptValue3);
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptValue3);
            if (ScriptFormula.callBuiltin((String)"len", arrayList2, (ScriptContext)scriptContext).asNum() > 0.0) {
                return scriptValue3;
            }
            ArrayList arrayList3 = new ArrayList();
            ScriptValue.Array array = new ScriptValue.Array(arrayList3);
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
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptContext.getClassOrVar("move"));
            if (!ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin((String)"len", arrayList4, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)))) break block5;
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
            ScriptValue.Obj obj;
            Object object4;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.statusKey(builder2);
            String string = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object4);
                object3 = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
            } else {
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)object3, (String)"active") ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
        if (scriptValue3 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue4 = Gomoku.turnKey(builder3);
            String string = "string";
            if (scriptValue3 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object5);
                object2 = polyClassServer.tm$6_get_typed(scriptValue4.asStr(), string);
            } else {
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)object2, (ScriptValue)scriptContext.getClassOrVar("CELL_WHITE")) ^ true) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Server");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue6 = Gomoku.boardKey(builder4);
            String string = "string";
            if (scriptValue5 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object6);
                object = polyClassServer.tm$6_get_typed(scriptValue6.asStr(), string);
            } else {
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
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
        Object object7 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule_async", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)("games/gomoku.pf:bot_compute_move:" + scriptContext.getStr("gid") + ":" + scriptValue7.asStr() + ":" + scriptValue8.asStr())), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue);
        if (ScriptFormula.valuesEqual((ScriptValue)ScriptFormula.callBuiltin((String)"len", arrayList, (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("TaskManager");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue scriptValue3;
            StringBuilder stringBuilder = new StringBuilder().append("games/gomoku.pf:bot_apply_move:").append(scriptContext.getStr("gid")).append(":").append(((scriptValue3 = scriptContext.getClassOrVar("move")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()).append(":");
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("move");
            v1 = PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)stringBuilder.append((scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL).asStr()).toString()), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0)), (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue botApplyMove(ScriptContext.Builder builder) {
        block23: {
            Object object;
            Object object2;
            Object object3;
            Object object4;
            Object object5;
            ScriptContext scriptContext = builder.peek();
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("row"));
            ScriptValue scriptValue = ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext);
            builder.val("row", scriptValue);
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(scriptContext.getClassOrVar("col"));
            ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext);
            builder.val("col", scriptValue2);
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Server");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
                builder2.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue4 = Gomoku.statusKey(builder2);
                String string = "string";
                if (scriptValue3 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object6);
                    object5 = polyClassServer.tm$6_get_typed(scriptValue4.asStr(), string);
                } else {
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object5 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)object5, (String)"active") ^ true) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Server");
            if (scriptValue5 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue6 = Gomoku.turnKey(builder3);
                String string = "string";
                if (scriptValue5 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object7);
                    object4 = polyClassServer.tm$6_get_typed(scriptValue6.asStr(), string);
                } else {
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqual((ScriptValue)object4, (ScriptValue)scriptContext.getClassOrVar("CELL_WHITE")) ^ true) {
                return ScriptValue.NULL;
            }
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
                builder4.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue8 = Gomoku.boardKey(builder4);
                String string = "string";
                if (scriptValue7 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object8);
                    object3 = polyClassServer.tm$6_get_typed(scriptValue8.asStr(), string);
                } else {
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
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
                ScriptValue.Obj obj;
                Object object9;
                ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
                builder7.val("gid", scriptContext.getClassOrVar("gid"));
                ScriptValue scriptValue11 = Gomoku.blackKey(builder7);
                String string = "string";
                if (scriptValue10 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object9);
                    object2 = polyClassServer.tm$6_get_typed(scriptValue11.asStr(), string);
                } else {
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, (ScriptValue)scriptValue11, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = object2;
            builder.val("black", scriptValue12);
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Server");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                ScriptValue scriptValue14 = scriptValue12;
                if (scriptValue13 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object10);
                    object = polyClassServer.tm$10_get_player(scriptValue14.asStr());
                } else {
                    object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue13, (ScriptValue)scriptValue14, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue15 = object;
            builder.val("human", scriptValue15);
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add(scriptValue15);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList3, (ScriptContext)scriptContext).asBool() ^ true)) break block23;
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
