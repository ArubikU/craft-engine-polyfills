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
        Object object;
        Object object2;
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
        StringBuilder stringBuilder = new StringBuilder().append("gomoku_");
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("pair");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
            object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue2, arrayList4, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        StringBuilder stringBuilder2 = stringBuilder.append(object2.asStr()).append("_");
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("pair");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
            object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return ScriptValue.of((String)stringBuilder2.append(object.asStr()).toString());
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
                Object object;
                Object object2;
                builder.val("d", scriptValue);
                ScriptValue scriptValue2 = scriptContext.getClassOrVar("d");
                if (scriptValue2 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue2, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue3 = object2;
                builder.val("dr", scriptValue3);
                ScriptValue scriptValue4 = scriptContext.getClassOrVar("d");
                if (scriptValue4 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
                    object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList7, (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue5 = object;
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "opponent"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("opp", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() || ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object3;
                String string = "<red>\u2718 <white>Usage: /games gomoku <player|bot>";
                if (scriptValue3 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer4 = new PolyClassPlayer(object3);
                    v1 = ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add(scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)ScriptFormula.callBuiltin((String)"lower", arrayList3, (ScriptContext)scriptContext), (String)"bot")) {
            Object object4;
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
                Object object5;
                ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
                builder3.val("gid", scriptValue5);
                ScriptValue scriptValue7 = Gomoku.statusKey(builder3);
                String string = "string";
                if (scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object5);
                    object4 = polyClassServer.tm$6_get_typed(scriptValue7.asStr(), string);
                } else {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(scriptValue7);
                    arrayList4.add(ScriptValue.of((String)string));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, arrayList4, (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqualStr((ScriptValue)object4, (String)"active")) {
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
            Object object6;
            ScriptValue scriptValue9 = scriptValue2;
            if (scriptValue8 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue8).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object6);
                object = polyClassServer.tm$10_get_player(scriptValue9.asStr());
            } else {
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(scriptValue9);
                object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue8, arrayList5, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = object;
        builder.val("target", scriptValue10);
        ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
        arrayList6.add(scriptValue10);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList6, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Player");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "<red>\u2718 <white>Player not found or offline.";
                if (scriptValue11 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object7);
                    v4 = ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add(ScriptValue.of((String)string));
                    v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, arrayList7, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("target");
        Object object8 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue12, (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Player");
        Object object9 = scriptValue13 != ScriptValue.NULL ? ((polyClassPlayer3 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue13)) != null ? polyClassPlayer3.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue13, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object8, (ScriptValue)object9)) {
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object10;
                String string = "<red>\u2718 <white>You can't play yourself - try '/games gomoku bot'.";
                if (scriptValue14 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer7 = new PolyClassPlayer(object10);
                    v7 = ScriptValue.of((boolean)polyClassPlayer7.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                    arrayList8.add(ScriptValue.of((String)string));
                    v7 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, arrayList8, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
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
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = ScriptFormula.compile((String)"[        make_map(\"label\", \"<green>Easy\", \"tooltip\", \"<gray>Blocks your wins, otherwise plays casually.\", \"action\", \"games/gomoku.pf:start_bot_game:\" + DIFF_EASY),        make_map(\"label\", \"<yellow>Medium\", \"tooltip\", \"<gray>Weighs threats on the whole board.\", \"action\", \"games/gomoku.pf:start_bot_game:\" + DIFF_MEDIUM),        make_map(\"label\", \"<red>Hard\", \"tooltip\", \"<gray>Also thinks one move ahead.\", \"action\", \"games/gomoku.pf:start_bot_game:\" + DIFF_HARD),    ]").evaluate(scriptContext);
        builder.val("buttons", scriptValue);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("Player"));
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("Machine"));
        arrayList2.add(scriptContext.getClassOrVar("buttons"));
        arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
        ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
        arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", Gomoku.class, 1));
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<gray>Choose how strong the bot should play."));
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Dialog");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "<gold>Gomoku - Bot Difficulty"));
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue2, arrayList5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_multi_action", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "can_close_with_escape", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "body", (ScriptValue)object, arrayList4, (ScriptContext)scriptContext), arrayList3, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(scriptValue5);
                v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue7);
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(scriptValue8);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue10);
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(scriptValue11);
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue13);
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(scriptValue14);
                v4 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue17);
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(scriptValue16);
                v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue20);
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(scriptValue21);
                v7 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue23);
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(scriptValue24);
                v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue22, arrayList, (ScriptContext)scriptContext);
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
        Object object;
        ScriptContext scriptContext = builder.peek();
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("ch"), (ScriptValue)scriptContext.getClassOrVar("CELL_BLACK"))) {
            Object object2;
            ScriptValue scriptValue = scriptContext.getClassOrVar("Images");
            if (scriptValue != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "gomoku:black"));
                object2 = PolyDispatch.bootstrapCall("memberCall", "from", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            return object2;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("ch"), (ScriptValue)scriptContext.getClassOrVar("CELL_WHITE"))) {
            Object object3;
            ScriptValue scriptValue = scriptContext.getClassOrVar("Images");
            if (scriptValue != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "gomoku:white"));
                object3 = PolyDispatch.bootstrapCall("memberCall", "from", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            } else {
                object3 = ScriptValue.NULL;
            }
            return object3;
        }
        ScriptValue scriptValue = scriptContext.getClassOrVar("Images");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "gomoku:empty"));
            object = PolyDispatch.bootstrapCall("memberCall", "from", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        return object;
    }

    public static ScriptValue showBoard(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        Object object7;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue2 = Gomoku.boardKey(builder2);
            String string = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object8);
                object7 = polyClassServer.tm$6_get_typed(scriptValue2.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                arrayList.add(ScriptValue.of((String)string));
                object7 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object7;
        builder.val("board", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Server");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object9;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue5 = Gomoku.turnKey(builder3);
            String string = "string";
            if (scriptValue4 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object9);
                object6 = polyClassServer.tm$6_get_typed(scriptValue5.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue5);
                arrayList.add(ScriptValue.of((String)string));
                object6 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object6;
        builder.val("turn", scriptValue6);
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object10;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue8 = Gomoku.blackKey(builder4);
            String string = "string";
            if (scriptValue7 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object10);
                object5 = polyClassServer.tm$6_get_typed(scriptValue8.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue8);
                arrayList.add(ScriptValue.of((String)string));
                object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object5;
        builder.val("black", scriptValue9);
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Server");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object11;
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue11 = Gomoku.whiteKey(builder5);
            String string = "string";
            if (scriptValue10 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object11);
                object4 = polyClassServer.tm$6_get_typed(scriptValue11.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue11);
                arrayList.add(ScriptValue.of((String)string));
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = object4;
        builder.val("white", scriptValue12);
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Server");
        if (scriptValue13 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object12;
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue14 = Gomoku.statusKey(builder6);
            String string = "string";
            if (scriptValue13 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object12);
                object3 = polyClassServer.tm$6_get_typed(scriptValue14.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue14);
                arrayList.add(ScriptValue.of((String)string));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = object3;
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
        ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
        arrayList8.add(scriptContext.getClassOrVar("viewer"));
        ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
        arrayList9.add(scriptContext.getClassOrVar("Machine"));
        arrayList9.add(scriptValue25);
        arrayList9.add(scriptContext.getClassOrVar("BOARD_SIZE"));
        ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
        arrayList10.add(ScriptValue.of((boolean)(ScriptFormula.valuesEqualStr((ScriptValue)scriptValue15, (String)"active") ^ true)));
        ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
        ScriptContext.Builder builder9 = ScriptContext.builder().copyFrom(scriptContext);
        builder9.val("status", scriptValue15);
        builder9.val("turn", scriptValue6);
        builder9.val("black", scriptValue9);
        builder9.val("white", scriptValue12);
        arrayList11.add(Gomoku.statusLine(builder9));
        ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Images");
        if (scriptValue27 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
            arrayList13.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "gomoku:ui"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "from", (ScriptValue)scriptValue27, arrayList13, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        arrayList12.add((ScriptValue)object2);
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("Dialog");
        if (scriptValue28 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
            arrayList14.add(scriptValue26);
            object = PolyDispatch.bootstrapCall("memberCall", "base", (ScriptValue)scriptValue28, arrayList14, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "as_multi_action", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "can_close_with_escape", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "body", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "body", (ScriptValue)object, arrayList12, (ScriptContext)scriptContext), arrayList11, (ScriptContext)scriptContext), arrayList10, (ScriptContext)scriptContext), arrayList9, (ScriptContext)scriptContext), arrayList8, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue2);
                    arrayList.add(ScriptValue.of((String)string));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue5);
                    arrayList.add(ScriptValue.of((String)string));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue10);
                    object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
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
        block22: {
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue2);
                    arrayList.add(ScriptValue.of((String)string));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue5);
                    arrayList.add(ScriptValue.of((String)string));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue8);
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
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
                if (scriptValue10 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(scriptContext.getClassOrVar("message"));
                    v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, arrayList2, (ScriptContext)scriptContext);
                } else {
                    v3 = ScriptValue.NULL;
                }
            }
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            if (!(Gomoku.isBotGame(builder4).asBool() ^ true)) break block22;
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("Server");
            if (scriptValue11 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object8;
                ScriptValue scriptValue12 = scriptValue6;
                if (scriptValue11 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue11).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object8);
                    object = polyClassServer.tm$10_get_player(scriptValue12.asStr());
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue12);
                    object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue11, arrayList3, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue13 = object;
            builder.val("w", scriptValue13);
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(scriptValue13);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList4, (ScriptContext)scriptContext).asBool() ^ true) {
                ScriptValue scriptValue14 = scriptContext.getClassOrVar("w");
                if (scriptValue14 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptContext.getClassOrVar("message"));
                    v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, arrayList5, (ScriptContext)scriptContext);
                } else {
                    v5 = ScriptValue.NULL;
                }
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue applyMove(ScriptContext.Builder builder) {
        block32: {
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue2);
                    arrayList.add(ScriptValue.of((String)string));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue6);
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue7);
                    v1 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue9);
                    arrayList.add(ScriptValue.of((String)string));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue12);
                    arrayList.add(ScriptValue.of((String)string));
                    object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
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
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue16);
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(scriptValue15);
                        v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
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
                        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                        arrayList.add(scriptValue20);
                        arrayList.add(ScriptValue.of((String)string));
                        arrayList.add(scriptValue21);
                        v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue24);
                    arrayList.add(ScriptValue.of((String)string));
                    arrayList.add(scriptValue25);
                    v7 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue23, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v7 = ScriptValue.NULL;
            }
            ScriptContext.Builder builder14 = ScriptContext.builder().copyFrom(scriptContext);
            builder14.val("gid", scriptContext.getClassOrVar("gid"));
            if (!(Gomoku.isBotGame(builder14).asBool() && ScriptFormula.valuesEqual((ScriptValue)scriptValue22, (ScriptValue)scriptContext.getClassOrVar("CELL_WHITE")))) break block32;
            ScriptValue scriptValue26 = scriptContext.getClassOrVar("TaskManager");
            if (scriptValue26 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)("games/gomoku.pf:bot_move:" + scriptContext.getStr("gid"))));
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 15.0));
                v8 = PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue26, arrayList, (ScriptContext)scriptContext);
            } else {
                v8 = ScriptValue.NULL;
            }
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
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue2);
                    object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
        Object object;
        PolyClassPlayer polyClassPlayer;
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer2;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        ScriptContext scriptContext = builder.peek();
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptContext.getClassOrVar("row"));
        ScriptValue scriptValue2 = ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext);
        builder.val("row", scriptValue2);
        ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
        arrayList2.add(scriptContext.getClassOrVar("col"));
        ScriptValue scriptValue3 = ScriptFormula.callBuiltin((String)"int", arrayList2, (ScriptContext)scriptContext);
        builder.val("col", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Server");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptContext.Builder builder2 = ScriptContext.builder().copyFrom(scriptContext);
            builder2.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue5 = Gomoku.statusKey(builder2);
            String string = "string";
            if (scriptValue4 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object6);
                object5 = polyClassServer.tm$6_get_typed(scriptValue5.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptValue5);
                arrayList3.add(ScriptValue.of((String)string));
                object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList3, (ScriptContext)scriptContext);
            }
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object5;
        builder.val("status", scriptValue6);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"active") ^ true) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
            if (scriptValue7 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object7;
                String string = "<red>\u2718 <white>This game has already ended - run the command again to start a new one.";
                if (scriptValue7 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object7);
                    v1 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, arrayList4, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("TaskManager");
            if (scriptValue8 != ScriptValue.NULL) {
                PolyClassPlayer polyClassPlayer4;
                ScriptValue scriptValue9;
                ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                arrayList5.add(ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((scriptValue9 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue9)) != null ? polyClassPlayer4.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue9, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()))));
                arrayList5.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                v2 = PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue8, arrayList5, (ScriptContext)scriptContext);
            } else {
                v2 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Server");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            ScriptContext.Builder builder3 = ScriptContext.builder().copyFrom(scriptContext);
            builder3.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue11 = Gomoku.blackKey(builder3);
            String string = "string";
            if (scriptValue10 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object8);
                object4 = polyClassServer.tm$6_get_typed(scriptValue11.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(scriptValue11);
                arrayList6.add(ScriptValue.of((String)string));
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, arrayList6, (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = object4;
        builder.val("black", scriptValue12);
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("Server");
        if (scriptValue13 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object9;
            ScriptContext.Builder builder4 = ScriptContext.builder().copyFrom(scriptContext);
            builder4.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue14 = Gomoku.whiteKey(builder4);
            String string = "string";
            if (scriptValue13 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object9);
                object3 = polyClassServer.tm$6_get_typed(scriptValue14.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(scriptValue14);
                arrayList7.add(ScriptValue.of((String)string));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue13, arrayList7, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = object3;
        builder.val("white", scriptValue15);
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("Server");
        if (scriptValue16 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object10;
            ScriptContext.Builder builder5 = ScriptContext.builder().copyFrom(scriptContext);
            builder5.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue17 = Gomoku.turnKey(builder5);
            String string = "string";
            if (scriptValue16 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue16).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object10);
                object2 = polyClassServer.tm$6_get_typed(scriptValue17.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                arrayList8.add(scriptValue17);
                arrayList8.add(ScriptValue.of((String)string));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue16, arrayList8, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = object2;
        builder.val("turn", scriptValue18);
        ScriptValue scriptValue19 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "");
        builder.val("my_color", scriptValue19);
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("Player");
        Object object11 = scriptValue20 != ScriptValue.NULL ? ((polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue20)) != null ? polyClassPlayer2.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue20, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object11, (ScriptValue)scriptValue12)) {
            ScriptValue scriptValue21 = scriptContext.getClassOrVar("CELL_BLACK");
            builder.val("my_color", scriptValue21);
        }
        Object object12 = (scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object12, (ScriptValue)scriptValue15)) {
            ScriptValue scriptValue22 = scriptContext.getClassOrVar("CELL_WHITE");
            builder.val("my_color", scriptValue22);
        }
        if (scriptContext.getStr("my_color").equals("")) {
            ScriptValue scriptValue23 = scriptContext.getClassOrVar("Player");
            if (scriptValue23 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object13;
                String string = "<red>\u2718 <white>You're not a player in this game.";
                if (scriptValue23 instanceof ScriptValue.Obj && (object13 = (obj = (ScriptValue.Obj)scriptValue23).instance()) != null && !(object13 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer5 = new PolyClassPlayer(object13);
                    v8 = ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                    arrayList9.add(ScriptValue.of((String)string));
                    v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue23, arrayList9, (ScriptContext)scriptContext);
                }
            } else {
                v8 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)scriptContext.getClassOrVar("my_color"), (ScriptValue)scriptValue18) ^ true) {
            ScriptValue scriptValue24 = scriptContext.getClassOrVar("Player");
            if (scriptValue24 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object14;
                String string = "<red>\u2718 <white>It's not your turn.";
                if (scriptValue24 instanceof ScriptValue.Obj && (object14 = (obj = (ScriptValue.Obj)scriptValue24).instance()) != null && !(object14 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object14);
                    v9 = ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                    arrayList10.add(ScriptValue.of((String)string));
                    v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue24, arrayList10, (ScriptContext)scriptContext);
                }
            } else {
                v9 = ScriptValue.NULL;
            }
            ScriptValue scriptValue25 = scriptContext.getClassOrVar("TaskManager");
            if (scriptValue25 != ScriptValue.NULL) {
                PolyClassPlayer polyClassPlayer7;
                ScriptValue scriptValue26;
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                arrayList11.add(ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((scriptValue26 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer7 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue26)) != null ? polyClassPlayer7.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue26, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()))));
                arrayList11.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                v10 = PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue25, arrayList11, (ScriptContext)scriptContext);
            } else {
                v10 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("Server");
        if (scriptValue27 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object15;
            ScriptContext.Builder builder6 = ScriptContext.builder().copyFrom(scriptContext);
            builder6.val("gid", scriptContext.getClassOrVar("gid"));
            ScriptValue scriptValue28 = Gomoku.boardKey(builder6);
            String string = "string";
            if (scriptValue27 instanceof ScriptValue.Obj && (object15 = (obj = (ScriptValue.Obj)scriptValue27).instance()) != null && !(object15 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object15);
                object = polyClassServer.tm$6_get_typed(scriptValue28.asStr(), string);
            } else {
                ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
                arrayList12.add(scriptValue28);
                arrayList12.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue27, arrayList12, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue29 = object;
        builder.val("board", scriptValue29);
        ScriptContext.Builder builder7 = ScriptContext.builder().copyFrom(scriptContext);
        builder7.val("board", scriptValue29);
        builder7.val("r", scriptValue2);
        builder7.val("c", scriptValue3);
        if (ScriptFormula.valuesEqual((ScriptValue)Gomoku.cellAt(builder7), (ScriptValue)scriptContext.getClassOrVar("CELL_EMPTY")) ^ true) {
            ScriptValue scriptValue30 = scriptContext.getClassOrVar("Player");
            if (scriptValue30 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object16;
                String string = "<red>\u2718 <white>That cell is already taken.";
                if (scriptValue30 instanceof ScriptValue.Obj && (object16 = (obj = (ScriptValue.Obj)scriptValue30).instance()) != null && !(object16 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer8 = new PolyClassPlayer(object16);
                    v12 = ScriptValue.of((boolean)polyClassPlayer8.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
                    arrayList13.add(ScriptValue.of((String)string));
                    v12 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue30, arrayList13, (ScriptContext)scriptContext);
                }
            } else {
                v12 = ScriptValue.NULL;
            }
            ScriptValue scriptValue31 = scriptContext.getClassOrVar("TaskManager");
            if (scriptValue31 != ScriptValue.NULL) {
                PolyClassPlayer polyClassPlayer9;
                ScriptValue scriptValue32;
                ArrayList<ScriptValue> arrayList14 = new ArrayList<ScriptValue>();
                arrayList14.add(ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((scriptValue32 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer9 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue32)) != null ? polyClassPlayer9.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue32, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()))));
                arrayList14.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                v13 = PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue31, arrayList14, (ScriptContext)scriptContext);
            } else {
                v13 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptContext.Builder builder8 = ScriptContext.builder().copyFrom(scriptContext);
        builder8.val("gid", scriptContext.getClassOrVar("gid"));
        builder8.val("row", scriptValue2);
        builder8.val("col", scriptValue3);
        builder8.val("color", scriptContext.getClassOrVar("my_color"));
        Gomoku.applyMove(builder8);
        ScriptValue scriptValue33 = scriptContext.getClassOrVar("TaskManager");
        if (scriptValue33 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer10;
            ScriptValue scriptValue34;
            ArrayList<ScriptValue> arrayList15 = new ArrayList<ScriptValue>();
            arrayList15.add(ScriptValue.of((String)("games/gomoku.pf:refresh_board:" + scriptContext.getStr("gid") + ":" + ((scriptValue34 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer10 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue34)) != null ? polyClassPlayer10.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue34, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()))));
            arrayList15.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
            v14 = PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue33, arrayList15, (ScriptContext)scriptContext);
        } else {
            v14 = ScriptValue.NULL;
        }
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
                var9_9 = new ArrayList<ScriptValue>();
                var9_9.add(var3_4);
                var9_9.add(ScriptValue.of((String)var5_5));
                v0 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var2_2, var9_9, (ScriptContext)var1_1);
            }
        } else {
            v0 /* !! */  = ScriptValue.NULL;
        }
        var10_10 = v0 /* !! */ ;
        var0.val("status", var10_10);
        if (ScriptFormula.valuesEqualStr((ScriptValue)var10_10, (String)"active") ^ true) {
            var11_11 = var1_1.getClassOrVar("Player");
            if (var11_11 != ScriptValue.NULL) {
                var12_12 = "<yellow>This game has already ended.";
                if (var11_11 instanceof ScriptValue.Obj && (var14_14 = (var13_13 = (ScriptValue.Obj)var11_11).instance()) != null && !(var14_14 instanceof PolyClass) && var13_13.typeName().equals("Player")) {
                    var15_15 = new PolyClassPlayer(var14_14);
                    v1 /* !! */  = ScriptValue.of((boolean)var15_15.tm$42_send_message(var12_12));
                } else {
                    var16_16 = new ArrayList<ScriptValue>();
                    var16_16.add(ScriptValue.of((String)var12_12));
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var11_11, var16_16, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var17_17 = var1_1.getClassOrVar("Server");
        if (var17_17 != ScriptValue.NULL) {
            var19_18 = ScriptContext.builder().copyFrom(var1_1);
            var19_18.val("gid", var1_1.getClassOrVar("gid"));
            var18_19 = Gomoku.blackKey(var19_18);
            var20_20 = "string";
            if (var17_17 instanceof ScriptValue.Obj && (var22_22 = (var21_21 = (ScriptValue.Obj)var17_17).instance()) != null && !(var22_22 instanceof PolyClass) && var21_21.typeName().equals("Server")) {
                var23_23 = new PolyClassServer(var22_22);
                v2 /* !! */  = var23_23.tm$6_get_typed(var18_19.asStr(), var20_20);
            } else {
                var24_24 = new ArrayList<ScriptValue>();
                var24_24.add(var18_19);
                var24_24.add(ScriptValue.of((String)var20_20));
                v2 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var17_17, var24_24, (ScriptContext)var1_1);
            }
        } else {
            v2 /* !! */  = ScriptValue.NULL;
        }
        var25_25 = v2 /* !! */ ;
        var0.val("black", var25_25);
        var26_26 = var1_1.getClassOrVar("Server");
        if (var26_26 != ScriptValue.NULL) {
            var28_27 = ScriptContext.builder().copyFrom(var1_1);
            var28_27.val("gid", var1_1.getClassOrVar("gid"));
            var27_28 = Gomoku.whiteKey(var28_27);
            var29_29 = "string";
            if (var26_26 instanceof ScriptValue.Obj && (var31_31 = (var30_30 = (ScriptValue.Obj)var26_26).instance()) != null && !(var31_31 instanceof PolyClass) && var30_30.typeName().equals("Server")) {
                var32_32 = new PolyClassServer(var31_31);
                v3 /* !! */  = var32_32.tm$6_get_typed(var27_28.asStr(), var29_29);
            } else {
                var33_33 = new ArrayList<ScriptValue>();
                var33_33.add(var27_28);
                var33_33.add(ScriptValue.of((String)var29_29));
                v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var26_26, var33_33, (ScriptContext)var1_1);
            }
        } else {
            v3 /* !! */  = ScriptValue.NULL;
        }
        var34_34 = v3 /* !! */ ;
        var0.val("white", var34_34);
        var35_35 = var1_1.getClassOrVar("Player");
        v4 /* !! */  = var35_35 != ScriptValue.NULL ? ((var36_36 = PolyClassPlayer.ofGuarded((ScriptValue)var35_35)) != null ? var36_36.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var35_35, (ScriptContext)var1_1)) : ScriptValue.NULL;
        if (!(ScriptFormula.valuesEqual((ScriptValue)v4 /* !! */ , (ScriptValue)var25_25) ^ true)) ** GOTO lbl-1000
        var37_37 = var1_1.getClassOrVar("Player");
        v5 /* !! */  = var37_37 != ScriptValue.NULL ? ((var38_38 = PolyClassPlayer.ofGuarded((ScriptValue)var37_37)) != null ? var38_38.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var37_37, (ScriptContext)var1_1)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)v5 /* !! */ , (ScriptValue)var34_34) ^ true) {
            v6 = true;
        } else lbl-1000:
        // 2 sources

        {
            v6 = false;
        }
        if (v6) {
            var39_39 = var1_1.getClassOrVar("Player");
            if (var39_39 != ScriptValue.NULL) {
                var40_40 = "<red>\u2718 <white>You're not a player in this game.";
                if (var39_39 instanceof ScriptValue.Obj && (var42_42 = (var41_41 = (ScriptValue.Obj)var39_39).instance()) != null && !(var42_42 instanceof PolyClass) && var41_41.typeName().equals("Player")) {
                    var43_43 = new PolyClassPlayer(var42_42);
                    v7 /* !! */  = ScriptValue.of((boolean)var43_43.tm$42_send_message(var40_40));
                } else {
                    var44_44 = new ArrayList<ScriptValue>();
                    var44_44.add(ScriptValue.of((String)var40_40));
                    v7 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var39_39, var44_44, (ScriptContext)var1_1);
                }
            } else {
                v7 /* !! */  = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        var45_45 = var1_1.getClassOrVar("Player");
        var47_47 = ScriptFormula.valuesEqual((ScriptValue)(var45_45 != ScriptValue.NULL ? ((var46_46 = PolyClassPlayer.ofGuarded((ScriptValue)var45_45)) != null ? var46_46.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var45_45, (ScriptContext)var1_1)) : ScriptValue.NULL), (ScriptValue)var25_25);
        var48_48 = ScriptValue.of((boolean)var47_47);
        var0.val("resigner_is_black", var48_48);
        var49_49 = var1_1.getClassOrVar("Server");
        if (var49_49 != ScriptValue.NULL) {
            var51_50 = ScriptContext.builder().copyFrom(var1_1);
            var51_50.val("gid", var1_1.getClassOrVar("gid"));
            var50_51 = Gomoku.statusKey(var51_50);
            var52_52 = "string";
            v8 = var53_53 = var47_47 != false ? ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "white_win")) : ( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Gomoku.class, "black_win"));
            if (var49_49 instanceof ScriptValue.Obj && (var55_55 = (var54_54 = (ScriptValue.Obj)var49_49).instance()) != null && !(var55_55 instanceof PolyClass) && var54_54.typeName().equals("Server")) {
                var56_56 = new PolyClassServer(var55_55);
                v9 /* !! */  = ScriptValue.of((boolean)var56_56.tm$0_set_typed(var50_51.asStr(), var52_52, var53_53));
            } else {
                var57_57 = new ArrayList<ScriptValue>();
                var57_57.add(var50_51);
                var57_57.add(ScriptValue.of((String)var52_52));
                var57_57.add(var53_53);
                v9 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var49_49, var57_57, (ScriptContext)var1_1);
            }
        } else {
            v9 /* !! */  = ScriptValue.NULL;
        }
        var58_58 = var47_47 != false ? var34_34 : var25_25;
        var0.val("winner", var58_58);
        var59_59 = ScriptContext.builder().copyFrom(var1_1);
        var59_59.val("gid", var1_1.getClassOrVar("gid"));
        var60_60 = var1_1.getClassOrVar("Player");
        var59_59.val("message", ScriptValue.of((String)("<yellow>" + (var60_60 != ScriptValue.NULL ? ((var61_61 = PolyClassPlayer.ofGuarded((ScriptValue)var60_60)) != null ? var61_61.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)var60_60, (ScriptContext)var1_1).asStr()) : ScriptValue.NULL.asStr()) + " resigned. <gold>" + var58_58.asStr() + " wins!")));
        Gomoku.notifyBothChat(var59_59);
        var62_62 = ScriptContext.builder().copyFrom(var1_1);
        var62_62.val("gid", var1_1.getClassOrVar("gid"));
        Gomoku.notifyOpponent(var62_62);
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
                Object object;
                Object object2;
                builder.val("d", scriptValue2);
                ScriptValue scriptValue3 = scriptContext.getClassOrVar("d");
                if (scriptValue3 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue4 = object2;
                builder.val("dr", scriptValue4);
                ScriptValue scriptValue5 = scriptContext.getClassOrVar("d");
                if (scriptValue5 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
                    object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue5, arrayList7, (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue6 = object;
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
                Object object;
                Object object2;
                builder.val("cell", scriptValue5);
                ScriptValue scriptValue6 = scriptContext.getClassOrVar("cell");
                if (scriptValue6 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue7 = object2;
                builder.val("r", scriptValue7);
                ScriptValue scriptValue8 = scriptContext.getClassOrVar("cell");
                if (scriptValue8 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
                    object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue9 = object;
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
                Object object;
                Object object2;
                builder.val("cell", scriptValue6);
                ScriptValue scriptValue7 = scriptContext.getClassOrVar("cell");
                if (scriptValue7 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
                } else {
                    object2 = ScriptValue.NULL;
                }
                ScriptValue scriptValue8 = object2;
                builder.val("r", scriptValue8);
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("cell");
                if (scriptValue9 != ScriptValue.NULL) {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
                    object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
                } else {
                    object = ScriptValue.NULL;
                }
                ScriptValue scriptValue10 = object;
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
                        Object object3;
                        Object object4;
                        builder.val("ocell", scriptValue17);
                        ScriptValue scriptValue18 = ScriptFormula.addPolymorphic((ScriptValue)scriptContext.getClassOrVar("opp_considered"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0)));
                        builder.val("opp_considered", scriptValue18);
                        if (!(scriptValue18.asNum() <= 30.0)) continue;
                        ScriptValue scriptValue19 = scriptContext.getClassOrVar("ocell");
                        if (scriptValue19 != ScriptValue.NULL) {
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                            object4 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
                        } else {
                            object4 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue20 = object4;
                        builder.val("orow", scriptValue20);
                        ScriptValue scriptValue21 = scriptContext.getClassOrVar("ocell");
                        if (scriptValue21 != ScriptValue.NULL) {
                            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
                            object3 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue21, arrayList, (ScriptContext)scriptContext);
                        } else {
                            object3 = ScriptValue.NULL;
                        }
                        ScriptValue scriptValue22 = object3;
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                arrayList.add(ScriptValue.of((String)string));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue4);
                arrayList.add(ScriptValue.of((String)string));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
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
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue6);
                arrayList.add(ScriptValue.of((String)string));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
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
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)("games/gomoku.pf:bot_compute_move:" + scriptContext.getStr("gid") + ":" + scriptValue7.asStr() + ":" + scriptValue8.asStr())));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
            v3 = PolyDispatch.bootstrapCall("memberCall", "schedule_async", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
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
            Object object;
            Object object2;
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            StringBuilder stringBuilder = new StringBuilder().append("games/gomoku.pf:bot_apply_move:").append(scriptContext.getStr("gid")).append(":");
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("move");
            if (scriptValue3 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue3, arrayList3, (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            StringBuilder stringBuilder2 = stringBuilder.append(object2.asStr()).append(":");
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("move");
            if (scriptValue4 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 1.0));
                object = PolyDispatch.bootstrapCall("memberCall", "get", (ScriptValue)scriptValue4, arrayList4, (ScriptContext)scriptContext);
            } else {
                object = ScriptValue.NULL;
            }
            arrayList2.add(ScriptValue.of((String)stringBuilder2.append(object.asStr()).toString()));
            arrayList2.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Gomoku.class, 0.0));
            v4 = PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue2, arrayList2, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
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
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(scriptValue4);
                    arrayList3.add(ScriptValue.of((String)string));
                    object5 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, arrayList3, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                    arrayList4.add(scriptValue6);
                    arrayList4.add(ScriptValue.of((String)string));
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, arrayList4, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue8);
                    arrayList5.add(ScriptValue.of((String)string));
                    object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue7, arrayList5, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                    arrayList6.add(scriptValue11);
                    arrayList6.add(ScriptValue.of((String)string));
                    object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue10, arrayList6, (ScriptContext)scriptContext);
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
                    ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                    arrayList7.add(scriptValue14);
                    object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue13, arrayList7, (ScriptContext)scriptContext);
                }
            } else {
                object = ScriptValue.NULL;
            }
            ScriptValue scriptValue15 = object;
            builder.val("human", scriptValue15);
            ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
            arrayList8.add(scriptValue15);
            if (!(ScriptFormula.callBuiltin((String)"is_empty", arrayList8, (ScriptContext)scriptContext).asBool() ^ true)) break block23;
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
