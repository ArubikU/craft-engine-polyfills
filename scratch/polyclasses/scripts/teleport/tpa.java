/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyClassServer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.teleport;

import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyClassServer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class Tpa {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue onRequest(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        ScriptValue scriptValue2;
        PolyClassServer polyClassServer;
        ScriptValue scriptValue3;
        PolyClassPlayer polyClassPlayer2;
        ScriptValue scriptValue4;
        PolyClassPlayer polyClassPlayer3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "target")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target", scriptValue6);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
            if (scriptValue7 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>Player not found or offline.";
                PolyClassPlayer polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue7);
                v0 = polyClassPlayer4 != null ? ScriptValue.of((boolean)polyClassPlayer4.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)((polyClassPlayer3 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer3.pg$67_name() : ((scriptValue4 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL)))) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
            if (scriptValue8 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>You can't teleport to yourself.";
                PolyClassPlayer polyClassPlayer5 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue8);
                v1 = polyClassPlayer5 != null ? ScriptValue.of((boolean)polyClassPlayer5.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        Object object = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_from")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptValue)((polyClassPlayer2 = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer2.pg$67_name() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object2 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_requested_at")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "int")), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)((polyClassServer = PolyClassServer.ofVar((ScriptContext)scriptContext, (String)"Server")) != null ? polyClassServer.pg$16_time() : ((scriptValue2 = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object3 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)("<gold>\u2691 <yellow>" + ((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.tg$68_name() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())) + " <white>wants to teleport to you.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object4 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "<gray> \u00bb <green><click:run_command:'/tpa accept'>[Accept]</click> <gray>or <red><click:run_command:'/tpa deny'>[Deny]</click>")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue scriptValue10 = ScriptValue.of((String)("<green>\u2714 <white>Teleport request sent to <yellow>" + (scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "<white>."));
            PolyClassPlayer polyClassPlayer6 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue9);
            v6 = polyClassPlayer6 != null ? ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(scriptValue10.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onAccept(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        PolyClassPlayer polyClassPlayer;
        Object object;
        ScriptValue scriptValue2;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
        if (scriptValue3 != ScriptValue.NULL) {
            String string = "tpa_from";
            String string2 = "string";
            PolyClassPlayer polyClassPlayer2 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue3);
            object3 = polyClassPlayer2 != null ? polyClassPlayer2.tm$12_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object3;
        builder.val("requester_name", scriptValue4);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"")) {
            ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
            if (scriptValue5 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>No pending teleport request.";
                PolyClassPlayer polyClassPlayer3 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue5);
                v1 = polyClassPlayer3 != null ? ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
        if (scriptValue6 != ScriptValue.NULL) {
            String string = "tpa_requested_at";
            String string3 = "int";
            PolyClassPlayer polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue6);
            object2 = polyClassPlayer4 != null ? polyClassPlayer4.tm$12_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object2;
        builder.val("requested_at", scriptValue7);
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
        if (scriptValue8 != ScriptValue.NULL) {
            String string = "tpa_from";
            String string4 = "string";
            ScriptValue scriptValue9 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "");
            PolyClassPlayer polyClassPlayer5 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue8);
            v3 = polyClassPlayer5 != null ? ScriptValue.of((boolean)polyClassPlayer5.tm$30_set_typed(string, string4, scriptValue9)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        PolyClassServer polyClassServer = PolyClassServer.ofVar((ScriptContext)scriptContext, (String)"Server");
        Object object4 = polyClassServer != null ? polyClassServer.pg$16_time() : ((scriptValue2 = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL);
        if (ScriptFormula.callBuiltin1((String)"int", (ScriptValue)object4, (ScriptContext)scriptContext).asNum() - scriptValue7.asNum() > scriptContext.getNum("TPA_EXPIRE_SECONDS")) {
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Player");
            if (scriptValue10 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>That request expired.";
                PolyClassPlayer polyClassPlayer6 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue10);
                v5 = polyClassPlayer6 != null ? ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("Server");
        if (scriptValue11 != ScriptValue.NULL) {
            ScriptValue scriptValue12 = scriptValue4;
            PolyClassServer polyClassServer2 = PolyClassServer.ofGuarded((ScriptValue)scriptValue11);
            object = polyClassServer2 != null ? polyClassServer2.tm$10_get_player(scriptValue12.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue11, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = object;
        builder.val("requester", scriptValue13);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue13, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
            if (scriptValue14 != ScriptValue.NULL) {
                ScriptValue scriptValue15 = ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue4.asStr() + " is no longer online."));
                PolyClassPlayer polyClassPlayer7 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue14);
                v7 = polyClassPlayer7 != null ? ScriptValue.of((boolean)polyClassPlayer7.tm$42_send_message(scriptValue15.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, (ScriptValue)scriptValue15, (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        Object object5 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_target")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptValue)((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.pg$67_name() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object6 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_moved")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "int")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Tpa.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("EventManager");
        ScriptValue scriptValue17 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "register", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "PlayerMoveEvent")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa.pf:on_move_cancel")), (ScriptValue)scriptContext.getClassOrVar("TPA_WARMUP_TICKS"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("handle_id", scriptValue17);
        Object object7 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_move_handle")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object8 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "<green>\u2714 <white>Request accepted! Teleporting in <yellow>3s<white> - don't move.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("Player");
        if (scriptValue18 != ScriptValue.NULL) {
            ScriptValue scriptValue19 = ScriptValue.of((String)("<green>\u2714 <white>Teleporting <yellow>" + scriptValue4.asStr() + "<white> to you in 3 seconds."));
            PolyClassPlayer polyClassPlayer8 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue18);
            v12 = polyClassPlayer8 != null ? ScriptValue.of((boolean)polyClassPlayer8.tm$42_send_message(scriptValue19.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue18, (ScriptValue)scriptValue19, (ScriptContext)scriptContext);
        } else {
            v12 = ScriptValue.NULL;
        }
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("TaskManager");
        Object object9 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue20, (ScriptValue)ScriptValue.of((String)("tpa.pf:on_warmup_done:" + scriptValue4.asStr())), (ScriptValue)scriptContext.getClassOrVar("TPA_WARMUP_TICKS"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onDeny(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "tpa_from";
            String string2 = "string";
            PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue2);
            object2 = polyClassPlayer != null ? polyClassPlayer.tm$12_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("requester_name", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"")) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>No pending teleport request.";
                PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue4);
                v1 = polyClassPlayer != null ? ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            String string = "tpa_from";
            String string3 = "string";
            ScriptValue scriptValue6 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "");
            PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue5);
            v2 = polyClassPlayer != null ? ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue6)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue6, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue scriptValue8 = scriptValue3;
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue7);
            object = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue8.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue7, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("requester", scriptValue9);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue9, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue10;
            PolyClassPlayer polyClassPlayer;
            Object object3 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)("<red>\u2718 <white>" + ((polyClassPlayer = PolyClassPlayer.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer.tg$68_name() : ((scriptValue10 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue10, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())) + " denied your teleport request.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        if ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL) {
            String string = "<yellow>Teleport request denied.";
            PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue);
            v5 = polyClassPlayer != null ? ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static ScriptValue onMoveCancel(ScriptContext.Builder var0) {
        block12: {
            var1_1 = var0.peek();
            var2_2 = var1_1.getClassOrVar("Player");
            if (var2_2 != ScriptValue.NULL) {
                var3_3 = "tpa_target";
                var4_4 = "string";
                var5_5 = PolyClassPlayer.ofGuarded((ScriptValue)var2_2);
                v0 /* !! */  = var5_5 != null ? var5_5.tm$12_get_typed(var3_3, var4_4) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var2_2, (ScriptValue)ScriptValue.of((String)var3_3), (ScriptValue)ScriptValue.of((String)var4_4), (ScriptContext)var1_1);
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            if (!(ScriptFormula.valuesEqualStr((ScriptValue)v0 /* !! */ , (String)"") ^ true)) ** GOTO lbl-1000
            var6_6 = var1_1.getClassOrVar("Player");
            if (var6_6 != ScriptValue.NULL) {
                var7_7 = "tpa_moved";
                var8_8 = "int";
                var9_9 = PolyClassPlayer.ofGuarded((ScriptValue)var6_6);
                v1 /* !! */  = var9_9 != null ? var9_9.tm$12_get_typed(var7_7, var8_8) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var6_6, (ScriptValue)ScriptValue.of((String)var7_7), (ScriptValue)ScriptValue.of((String)var8_8), (ScriptContext)var1_1);
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqual((ScriptValue)v1 /* !! */ , (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Tpa.class, 0.0)))) {
                v2 = true;
            } else lbl-1000:
            // 2 sources

            {
                v2 = false;
            }
            if (!v2) break block12;
            var10_10 = var1_1.getClassOrVar("Player");
            if (var10_10 != ScriptValue.NULL) {
                var11_11 = "tpa_moved";
                var12_12 = "int";
                var13_13 =  /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Tpa.class, 1.0);
                var14_14 = PolyClassPlayer.ofGuarded((ScriptValue)var10_10);
                v3 /* !! */  = var14_14 != null ? ScriptValue.of((boolean)var14_14.tm$30_set_typed(var11_11, var12_12, var13_13)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var10_10, (ScriptValue)ScriptValue.of((String)var11_11), (ScriptValue)ScriptValue.of((String)var12_12), (ScriptValue)var13_13, (ScriptContext)var1_1);
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            var15_15 = var1_1.getClassOrVar("Player");
            if (var15_15 != ScriptValue.NULL) {
                var16_16 = "tpa_target";
                var17_17 = "string";
                var18_18 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "");
                var19_19 = PolyClassPlayer.ofGuarded((ScriptValue)var15_15);
                v4 /* !! */  = var19_19 != null ? ScriptValue.of((boolean)var19_19.tm$30_set_typed(var16_16, var17_17, var18_18)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var15_15, (ScriptValue)ScriptValue.of((String)var16_16), (ScriptValue)ScriptValue.of((String)var17_17), (ScriptValue)var18_18, (ScriptContext)var1_1);
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            var20_20 = var1_1.getClassOrVar("Player");
            if (var20_20 != ScriptValue.NULL) {
                var21_21 = "<red>\u2718 <white>Teleport cancelled - you moved.";
                var22_22 = PolyClassPlayer.ofGuarded((ScriptValue)var20_20);
                v5 /* !! */  = var22_22 != null ? ScriptValue.of((boolean)var22_22.tm$42_send_message(var21_21)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var20_20, (ScriptValue)ScriptValue.of((String)var21_21), (ScriptContext)var1_1);
            } else {
                v5 /* !! */  = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onWarmupDone(ScriptContext.Builder builder) {
        Object object;
        ScriptValue scriptValue;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Server");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("name");
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue2);
            object2 = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue3.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue2, (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object2;
        builder.val("p", scriptValue4);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue4, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_moved")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "int")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Tpa.class, 1.0)))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_target")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target_name", scriptValue5);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"")) {
            return ScriptValue.NULL;
        }
        Object object3 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_target")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue6 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_move_handle")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("handle_id", scriptValue6);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue6, (String)"") ^ true) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("EventManager");
            Object object4 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "unregister", (ScriptValue)scriptValue7, (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL;
            Object object5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_move_handle")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        if ((scriptValue = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL) {
            ScriptValue scriptValue8 = scriptValue5;
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
            object = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue8.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue, (ScriptValue)scriptValue8, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("target", scriptValue9);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue9, (ScriptContext)scriptContext).asBool()) {
            Object object6 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue5.asStr() + " is no longer online.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            return ScriptValue.NULL;
        }
        Object object7 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue4, (ScriptValue)(scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "location", (ScriptValue)scriptValue9, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object8 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)("<green>\u2714 <white>Teleported to <yellow>" + scriptValue5.asStr() + "<white>!")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object9 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, (ScriptValue)ScriptValue.of((String)("<green>\u2714 <white>" + scriptContext.getStr("name") + " has teleported to you.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue openRequestsMenu(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Menu");
        ScriptValue scriptValue3 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Tpa.class, 27.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "<gold>Teleport Requests")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("m", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            String string = "tpa_from";
            String string2 = "string";
            PolyClassPlayer polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue4);
            object = polyClassPlayer != null ? polyClassPlayer.tm$12_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("from_name", scriptValue5);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"") ^ true) {
            Object object2;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue scriptValue7 = scriptValue5;
                PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue6);
                object2 = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue7.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
            } else {
                object2 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = object2;
            builder.val("requester", scriptValue8);
            if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asBool() ^ true) {
                Object object3;
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Item");
                if (scriptValue9 != ScriptValue.NULL) {
                    ScriptValue scriptValue10 = scriptValue8;
                    PolyClassItem polyClassItem = PolyClassItem.ofGuarded((ScriptValue)scriptValue9);
                    object3 = polyClassItem != null ? polyClassItem.tm$22_skull(scriptValue10) : PolyDispatch.bootstrapCall("memberCall", "skull", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
                } else {
                    object3 = ScriptValue.NULL;
                }
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)object3, (ScriptValue)ScriptValue.of((String)("<yellow>" + scriptValue5.asStr())), (ScriptContext)scriptContext);
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "<gray>Click to accept"));
                CallSite callSite2 = PolyDispatch.bootstrapCall("memberCall", "with_lore", (ScriptValue)callSite, (ScriptValue)new ScriptValue.Array(arrayList), (ScriptContext)scriptContext);
                builder.val("head", (ScriptValue)callSite2);
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("m");
                ScriptValue scriptValue12 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Tpa.class, 13.0)), (ScriptValue)callSite2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa.pf:on_accept")), (ScriptContext)scriptContext) : ScriptValue.NULL;
                builder.val("m", scriptValue12);
            }
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"")) {
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin2((String)"create_item", (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "minecraft:gray_stained_glass_pane")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Tpa.class, 1.0)), (ScriptContext)scriptContext), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "<gray>No pending requests")), (ScriptContext)scriptContext);
            builder.val("filler", (ScriptValue)callSite);
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("m");
            ScriptValue scriptValue14 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Tpa.class, 13.0)), (ScriptValue)callSite, (ScriptContext)scriptContext) : ScriptValue.NULL;
            builder.val("m", scriptValue14);
        }
        Object object4 = (scriptValue = scriptContext.getClassOrVar("m")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static void run(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        double d = 60.0;
        ScriptValue scriptValue = ScriptValue.of((double)60.0);
        builder.val("TPA_WARMUP_TICKS", scriptValue);
        double d2 = 300.0;
        ScriptValue scriptValue2 = ScriptValue.of((double)300.0);
        builder.val("TPA_EXPIRE_SECONDS", scriptValue2);
        FILE_SCOPE = builder.build();
    }
}
