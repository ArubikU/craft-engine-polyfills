/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
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
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
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
        PolyClassPlayer_v2 polyClassPlayer_v2;
        ScriptValue scriptValue2;
        PolyClassServer polyClassServer;
        ScriptValue scriptValue3;
        PolyClassPlayer_v2 polyClassPlayer_v22;
        ScriptValue scriptValue4;
        PolyClassPlayer_v2 polyClassPlayer_v23;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Cmd");
        ScriptValue scriptValue6 = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "target")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target", scriptValue6);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
            if (scriptValue7 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>Player not found or offline.";
                PolyClassPlayer_v2 polyClassPlayer_v24 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue7);
                v0 = polyClassPlayer_v24 != null ? ScriptValue.of((boolean)polyClassPlayer_v24.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)((polyClassPlayer_v23 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer_v23.pg$67_name() : ((scriptValue4 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL)))) {
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
            if (scriptValue8 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>You can't teleport to yourself.";
                PolyClassPlayer_v2 polyClassPlayer_v25 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue8);
                v1 = polyClassPlayer_v25 != null ? ScriptValue.of((boolean)polyClassPlayer_v25.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        Object object = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_from")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptValue)((polyClassPlayer_v22 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer_v22.pg$67_name() : ((scriptValue3 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue3, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object2 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_requested_at")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "int")), (ScriptValue)ScriptFormula.callBuiltin1((String)"int", (ScriptValue)((polyClassServer = PolyClassServer.ofVar((ScriptContext)scriptContext, (String)"Server")) != null ? polyClassServer.pg$16_time() : ((scriptValue2 = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)scriptValue2, (ScriptContext)scriptContext) : ScriptValue.NULL)), (ScriptContext)scriptContext), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object3 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)("<gold>\u2691 <yellow>" + ((polyClassPlayer_v2 = PolyClassPlayer_v2.ofVar((ScriptContext)scriptContext, (String)"Player")) != null ? polyClassPlayer_v2.tg$68_name() : ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asStr() : ScriptValue.NULL.asStr())) + " <white>wants to teleport to you.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object4 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "<gray> \u00bb <green><click:run_command:'/tpa accept'>[Accept]</click> <gray>or <red><click:run_command:'/tpa deny'>[Deny]</click>")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
        if (scriptValue9 != ScriptValue.NULL) {
            ScriptValue scriptValue10 = ScriptValue.of((String)("<green>\u2714 <white>Teleport request sent to <yellow>" + (scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue6, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "<white>."));
            PolyClassPlayer_v2 polyClassPlayer_v26 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue9);
            v6 = polyClassPlayer_v26 != null ? ScriptValue.of((boolean)polyClassPlayer_v26.tm$42_send_message(scriptValue10.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, (ScriptValue)scriptValue10, (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onAccept(ScriptContext.Builder builder) {
        PolyClassPlayer_v2 polyClassPlayer_v2;
        Object object;
        ScriptValue scriptValue;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "tpa_from";
            String string2 = "string";
            PolyClassPlayer_v2 polyClassPlayer_v22 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
            object3 = polyClassPlayer_v22 != null ? polyClassPlayer_v22.tm$12_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object3;
        builder.val("requester_name", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"")) {
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>No pending teleport request.";
                PolyClassPlayer_v2 polyClassPlayer_v23 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
                v1 = polyClassPlayer_v23 != null ? ScriptValue.of((boolean)polyClassPlayer_v23.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "tpa_requested_at";
            String string3 = "int";
            PolyClassPlayer_v2 polyClassPlayer_v24 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
            object2 = polyClassPlayer_v24 != null ? polyClassPlayer_v24.tm$12_get_typed(string, string3) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object2;
        builder.val("requested_at", scriptValue4);
        if (scriptValue2 != ScriptValue.NULL) {
            String string = "tpa_from";
            String string4 = "string";
            ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "");
            PolyClassPlayer_v2 polyClassPlayer_v25 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
            v3 = polyClassPlayer_v25 != null ? ScriptValue.of((boolean)polyClassPlayer_v25.tm$30_set_typed(string, string4, scriptValue5)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string4), (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        PolyClassServer polyClassServer = PolyClassServer.ofVar((ScriptContext)scriptContext, (String)"Server");
        Object object4 = polyClassServer != null ? polyClassServer.pg$16_time() : ((scriptValue = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL);
        if (ScriptFormula.callBuiltin1((String)"int", (ScriptValue)object4, (ScriptContext)scriptContext).asNum() - scriptValue4.asNum() > scriptContext.getNum("TPA_EXPIRE_SECONDS")) {
            if (scriptValue2 != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>That request expired.";
                PolyClassPlayer_v2 polyClassPlayer_v26 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
                v5 = polyClassPlayer_v26 != null ? ScriptValue.of((boolean)polyClassPlayer_v26.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue scriptValue7 = scriptValue3;
            PolyClassServer polyClassServer2 = PolyClassServer.ofGuarded((ScriptValue)scriptValue6);
            object = polyClassServer2 != null ? polyClassServer2.tm$10_get_player(scriptValue7.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue6, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object;
        builder.val("requester", scriptValue8);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asBool()) {
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue scriptValue9 = ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue3.asStr() + " is no longer online."));
                PolyClassPlayer_v2 polyClassPlayer_v27 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
                v7 = polyClassPlayer_v27 != null ? ScriptValue.of((boolean)polyClassPlayer_v27.tm$42_send_message(scriptValue9.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)scriptValue9, (ScriptContext)scriptContext);
            } else {
                v7 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        Object object5 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_target")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptValue)(scriptValue2 != ScriptValue.NULL ? ((polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2)) != null ? polyClassPlayer_v2.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue2, (ScriptContext)scriptContext)) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object6 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_moved")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "int")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Tpa.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("EventManager");
        ScriptValue scriptValue11 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "register", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "PlayerMoveEvent")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa.pf:on_move_cancel")), (ScriptValue)scriptContext.getClassOrVar("TPA_WARMUP_TICKS"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("handle_id", scriptValue11);
        Object object7 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_move_handle")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptValue)scriptValue11, (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object8 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "<green>\u2714 <white>Request accepted! Teleporting in <yellow>3s<white> - don't move.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue scriptValue12 = ScriptValue.of((String)("<green>\u2714 <white>Teleporting <yellow>" + scriptValue3.asStr() + "<white> to you in 3 seconds."));
            PolyClassPlayer_v2 polyClassPlayer_v28 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
            v12 = polyClassPlayer_v28 != null ? ScriptValue.of((boolean)polyClassPlayer_v28.tm$42_send_message(scriptValue12.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)scriptValue12, (ScriptContext)scriptContext);
        } else {
            v12 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("TaskManager");
        Object object9 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue13, (ScriptValue)ScriptValue.of((String)("tpa.pf:on_warmup_done:" + scriptValue3.asStr())), (ScriptValue)scriptContext.getClassOrVar("TPA_WARMUP_TICKS"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onDeny(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            String string = "tpa_from";
            String string2 = "string";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassPlayer_v2 != null ? polyClassPlayer_v2.tm$12_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object2;
        builder.val("requester_name", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            if (scriptValue != ScriptValue.NULL) {
                String string = "<red>\u2718 <white>No pending teleport request.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
                v1 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "tpa_from";
            String string3 = "string";
            ScriptValue scriptValue3 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "");
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v2 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$30_set_typed(string, string3, scriptValue3)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string3), (ScriptValue)scriptValue3, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Server");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue scriptValue5 = scriptValue2;
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue4);
            object = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue5.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = object;
        builder.val("requester", scriptValue6);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue6, (ScriptContext)scriptContext).asBool() ^ true) {
            PolyClassPlayer_v2 polyClassPlayer_v2;
            Object object3 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, (ScriptValue)ScriptValue.of((String)("<red>\u2718 <white>" + (scriptValue != ScriptValue.NULL ? ((polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue)) != null ? polyClassPlayer_v2.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()) + " denied your teleport request.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            String string = "<yellow>Teleport request denied.";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v5 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                var5_5 = PolyClassPlayer_v2.ofGuarded((ScriptValue)var2_2);
                v0 /* !! */  = var5_5 != null ? var5_5.tm$12_get_typed(var3_3, var4_4) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var2_2, (ScriptValue)ScriptValue.of((String)var3_3), (ScriptValue)ScriptValue.of((String)var4_4), (ScriptContext)var1_1);
            } else {
                v0 /* !! */  = ScriptValue.NULL;
            }
            if (!(ScriptFormula.valuesEqualStr((ScriptValue)v0 /* !! */ , (String)"") ^ true)) ** GOTO lbl-1000
            var6_6 = var1_1.getClassOrVar("Player");
            if (var6_6 != ScriptValue.NULL) {
                var7_7 = "tpa_moved";
                var8_8 = "int";
                var9_9 = PolyClassPlayer_v2.ofGuarded((ScriptValue)var6_6);
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
                var14_14 = PolyClassPlayer_v2.ofGuarded((ScriptValue)var10_10);
                v3 /* !! */  = var14_14 != null ? ScriptValue.of((boolean)var14_14.tm$30_set_typed(var11_11, var12_12, var13_13)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var10_10, (ScriptValue)ScriptValue.of((String)var11_11), (ScriptValue)ScriptValue.of((String)var12_12), (ScriptValue)var13_13, (ScriptContext)var1_1);
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            if (var10_10 != ScriptValue.NULL) {
                var15_15 = "tpa_target";
                var16_16 = "string";
                var17_17 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "");
                var18_18 = PolyClassPlayer_v2.ofGuarded((ScriptValue)var10_10);
                v4 /* !! */  = var18_18 != null ? ScriptValue.of((boolean)var18_18.tm$30_set_typed(var15_15, var16_16, var17_17)) : PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var10_10, (ScriptValue)ScriptValue.of((String)var15_15), (ScriptValue)ScriptValue.of((String)var16_16), (ScriptValue)var17_17, (ScriptContext)var1_1);
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            if (var10_10 != ScriptValue.NULL) {
                var19_19 = "<red>\u2718 <white>Teleport cancelled - you moved.";
                var20_20 = PolyClassPlayer_v2.ofGuarded((ScriptValue)var10_10);
                v5 /* !! */  = var20_20 != null ? ScriptValue.of((boolean)var20_20.tm$42_send_message(var19_19)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var10_10, (ScriptValue)ScriptValue.of((String)var19_19), (ScriptContext)var1_1);
            } else {
                v5 /* !! */  = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onWarmupDone(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Server");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("name");
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
            object2 = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue2.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("p", scriptValue3);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue3, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)(scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_moved")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "int")), (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", Tpa.class, 1.0)))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_target")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("target_name", scriptValue4);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue4, (String)"")) {
            return ScriptValue.NULL;
        }
        Object object3 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_target")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue5 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_move_handle")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("handle_id", scriptValue5);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"") ^ true) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("EventManager");
            Object object4 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "unregister", (ScriptValue)scriptValue6, (ScriptValue)scriptValue5, (ScriptContext)scriptContext) : ScriptValue.NULL;
            Object object5 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "tpa_move_handle")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "string")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", Tpa.class, "")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        }
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue7 = scriptValue4;
            PolyClassServer polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue);
            object = polyClassServer != null ? polyClassServer.tm$10_get_player(scriptValue7.asStr()) : PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue, (ScriptValue)scriptValue7, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = object;
        builder.val("target", scriptValue8);
        if (ScriptFormula.callBuiltin1((String)"is_empty", (ScriptValue)scriptValue8, (ScriptContext)scriptContext).asBool()) {
            Object object6 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue4.asStr() + " is no longer online.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
            return ScriptValue.NULL;
        }
        Object object7 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue3, (ScriptValue)(scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "location", (ScriptValue)scriptValue8, (ScriptContext)scriptContext) : ScriptValue.NULL), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object8 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, (ScriptValue)ScriptValue.of((String)("<green>\u2714 <white>Teleported to <yellow>" + scriptValue4.asStr() + "<white>!")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object9 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue8, (ScriptValue)ScriptValue.of((String)("<green>\u2714 <white>" + scriptContext.getStr("name") + " has teleported to you.")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue4);
            object = polyClassPlayer_v2 != null ? polyClassPlayer_v2.tm$12_get_typed(string, string2) : PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptValue)ScriptValue.of((String)string2), (ScriptContext)scriptContext);
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
        Object object4 = (scriptValue = scriptContext.getClassOrVar("m")) != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open", (ScriptValue)scriptValue, (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
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
