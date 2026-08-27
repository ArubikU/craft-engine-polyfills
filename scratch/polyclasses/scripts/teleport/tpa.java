/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassItem
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyClassServer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.teleport;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassItem;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyClassServer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.CallSite;
import java.util.ArrayList;

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
        PolyClassPlayer polyClassPlayer;
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Cmd");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"target"));
            object = PolyDispatch.bootstrapCall("memberCall", "arg", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object;
        builder.val("target", scriptValue2);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue2);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "<red>\u2718 <white>Player not found or offline.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer2 = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer2.tm$42_send_message(string));
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
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("target");
        Object object3 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        Object object4 = scriptValue5 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue5)) != null ? polyClassPlayer.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue5, (ScriptContext)scriptContext)) : ScriptValue.NULL;
        if (ScriptFormula.valuesEqual((ScriptValue)object3, (ScriptValue)object4)) {
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                String string = "<red>\u2718 <white>You can't teleport to yourself.";
                if (scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer3 = new PolyClassPlayer(object5);
                    v4 = ScriptValue.of((boolean)polyClassPlayer3.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                    arrayList3.add(ScriptValue.of((String)string));
                    v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue6, arrayList3, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("target");
        if (scriptValue7 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer4;
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(ScriptValue.of((String)"tpa_from"));
            arrayList4.add(ScriptValue.of((String)"string"));
            ScriptValue scriptValue8 = scriptContext.getClassOrVar("Player");
            arrayList4.add((ScriptValue)(scriptValue8 != ScriptValue.NULL ? ((polyClassPlayer4 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassPlayer4.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            v5 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue7, arrayList4, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("target");
        if (scriptValue9 != ScriptValue.NULL) {
            PolyClassServer polyClassServer;
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(ScriptValue.of((String)"tpa_requested_at"));
            arrayList5.add(ScriptValue.of((String)"int"));
            ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("Server");
            arrayList6.add((ScriptValue)(scriptValue10 != ScriptValue.NULL ? ((polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue10)) != null ? polyClassServer.pg$16_time() : PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)scriptValue10, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            arrayList5.add(ScriptFormula.callBuiltin((String)"int", arrayList6, (ScriptContext)scriptContext));
            v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue9, arrayList5, (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("target");
        if (scriptValue11 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer5;
            ScriptValue scriptValue12;
            ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
            arrayList7.add(ScriptValue.of((String)("<gold>\u2691 <yellow>" + ((scriptValue12 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer5 = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue12)) != null ? polyClassPlayer5.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue12, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()) + " <white>wants to teleport to you.")));
            v7 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue11, arrayList7, (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("target");
        if (scriptValue13 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
            arrayList8.add(ScriptValue.of((String)"<gray> \u00bb <green><click:run_command:'/tpa accept'>[Accept]</click> <gray>or <red><click:run_command:'/tpa deny'>[Deny]</click>"));
            v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue13, arrayList8, (ScriptContext)scriptContext);
        } else {
            v8 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("Player");
        if (scriptValue14 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue15;
            ScriptValue scriptValue16 = ScriptValue.of((String)("<green>\u2714 <white>Teleport request sent to <yellow>" + ((scriptValue15 = scriptContext.getClassOrVar("target")) != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue15, (ScriptContext)scriptContext) : ScriptValue.NULL).asStr() + "<white>."));
            if (scriptValue14 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue14).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer6 = new PolyClassPlayer(object6);
                v9 = ScriptValue.of((boolean)polyClassPlayer6.tm$42_send_message(scriptValue16.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
                arrayList9.add(scriptValue16);
                v9 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue14, arrayList9, (ScriptContext)scriptContext);
            }
        } else {
            v9 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onAccept(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        PolyClassServer polyClassServer;
        Object object3;
        Object object4;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "tpa_from";
            String string2 = "string";
            if (scriptValue instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                object4 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = object4;
        builder.val("requester_name", scriptValue2);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue2, (String)"")) {
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("Player");
            if (scriptValue3 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object6;
                String string = "<red>\u2718 <white>No pending teleport request.";
                if (scriptValue3 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue3).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object6);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            String string = "tpa_requested_at";
            String string3 = "int";
            if (scriptValue4 instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object7);
                object3 = polyClassPlayer.tm$12_get_typed(string, string3);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object3;
        builder.val("requested_at", scriptValue5);
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("Player");
        if (scriptValue6 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object8;
            String string = "tpa_from";
            String string4 = "string";
            ScriptValue scriptValue7 = ScriptValue.of((String)"");
            if (scriptValue6 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object8);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string4, scriptValue7));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string4));
                arrayList.add(scriptValue7);
                v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("Server");
        arrayList.add((ScriptValue)(scriptValue8 != ScriptValue.NULL ? ((polyClassServer = PolyClassServer.ofGuarded((ScriptValue)scriptValue8)) != null ? polyClassServer.pg$16_time() : PolyDispatch.bootstrapGet("memberGet", "time", (ScriptValue)scriptValue8, (ScriptContext)scriptContext)) : ScriptValue.NULL));
        if (ScriptFormula.callBuiltin((String)"int", arrayList, (ScriptContext)scriptContext).asNum() - scriptValue5.asNum() > scriptContext.getNum("TPA_EXPIRE_SECONDS")) {
            ScriptValue scriptValue9 = scriptContext.getClassOrVar("Player");
            if (scriptValue9 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object9;
                String string = "<red>\u2718 <white>That request expired.";
                if (scriptValue9 instanceof ScriptValue.Obj && (object9 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object9 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object9);
                    v4 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                    arrayList2.add(ScriptValue.of((String)string));
                    v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue9, arrayList2, (ScriptContext)scriptContext);
                }
            } else {
                v4 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("Server");
        if (scriptValue10 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object10;
            ScriptValue scriptValue11 = scriptValue2;
            if (scriptValue10 instanceof ScriptValue.Obj && (object10 = (obj = (ScriptValue.Obj)scriptValue10).instance()) != null && !(object10 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer2 = new PolyClassServer(object10);
                object2 = polyClassServer2.tm$10_get_player(scriptValue11.asStr());
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(scriptValue11);
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue10, arrayList3, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = object2;
        builder.val("requester", scriptValue12);
        ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
        arrayList4.add(scriptValue12);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList4, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("Player");
            if (scriptValue13 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object11;
                ScriptValue scriptValue14 = ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue2.asStr() + " is no longer online."));
                if (scriptValue13 instanceof ScriptValue.Obj && (object11 = (obj = (ScriptValue.Obj)scriptValue13).instance()) != null && !(object11 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object11);
                    v6 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue14.asStr()));
                } else {
                    ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                    arrayList5.add(scriptValue14);
                    v6 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue13, arrayList5, (ScriptContext)scriptContext);
                }
            } else {
                v6 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("requester");
        if (scriptValue15 != ScriptValue.NULL) {
            PolyClassPlayer polyClassPlayer;
            ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
            arrayList6.add(ScriptValue.of((String)"tpa_target"));
            arrayList6.add(ScriptValue.of((String)"string"));
            ScriptValue scriptValue16 = scriptContext.getClassOrVar("Player");
            arrayList6.add((ScriptValue)(scriptValue16 != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue16)) != null ? polyClassPlayer.pg$67_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue16, (ScriptContext)scriptContext)) : ScriptValue.NULL));
            v7 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue15, arrayList6, (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("requester");
        if (scriptValue17 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
            arrayList7.add(ScriptValue.of((String)"tpa_moved"));
            arrayList7.add(ScriptValue.of((String)"int"));
            arrayList7.add(ScriptValue.of((double)0.0));
            v8 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue17, arrayList7, (ScriptContext)scriptContext);
        } else {
            v8 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("EventManager");
        if (scriptValue18 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
            arrayList8.add(ScriptValue.of((String)"PlayerMoveEvent"));
            arrayList8.add(ScriptValue.of((String)"tpa.pf:on_move_cancel"));
            arrayList8.add(scriptContext.getClassOrVar("TPA_WARMUP_TICKS"));
            object = PolyDispatch.bootstrapCall("memberCall", "register", (ScriptValue)scriptValue18, arrayList8, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = object;
        builder.val("handle_id", scriptValue19);
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("requester");
        if (scriptValue20 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
            arrayList9.add(ScriptValue.of((String)"tpa_move_handle"));
            arrayList9.add(ScriptValue.of((String)"string"));
            arrayList9.add(scriptValue19);
            v10 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue20, arrayList9, (ScriptContext)scriptContext);
        } else {
            v10 = ScriptValue.NULL;
        }
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("requester");
        if (scriptValue21 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
            arrayList10.add(ScriptValue.of((String)"<green>\u2714 <white>Request accepted! Teleporting in <yellow>3s<white> - don't move."));
            v11 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue21, arrayList10, (ScriptContext)scriptContext);
        } else {
            v11 = ScriptValue.NULL;
        }
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("Player");
        if (scriptValue22 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object12;
            ScriptValue scriptValue23 = ScriptValue.of((String)("<green>\u2714 <white>Teleporting <yellow>" + scriptValue2.asStr() + "<white> to you in 3 seconds."));
            if (scriptValue22 instanceof ScriptValue.Obj && (object12 = (obj = (ScriptValue.Obj)scriptValue22).instance()) != null && !(object12 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object12);
                v12 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(scriptValue23.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
                arrayList11.add(scriptValue23);
                v12 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue22, arrayList11, (ScriptContext)scriptContext);
            }
        } else {
            v12 = ScriptValue.NULL;
        }
        ScriptValue scriptValue24 = scriptContext.getClassOrVar("TaskManager");
        if (scriptValue24 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
            arrayList12.add(ScriptValue.of((String)("tpa.pf:on_warmup_done:" + scriptValue2.asStr())));
            arrayList12.add(scriptContext.getClassOrVar("TPA_WARMUP_TICKS"));
            v13 = PolyDispatch.bootstrapCall("memberCall", "schedule", (ScriptValue)scriptValue24, arrayList12, (ScriptContext)scriptContext);
        } else {
            v13 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onDeny(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "tpa_from";
            String string2 = "string";
            if (scriptValue2 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object2 = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("requester_name", scriptValue3);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue3, (String)"")) {
            ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
            if (scriptValue4 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "<red>\u2718 <white>No pending teleport request.";
                if (scriptValue4 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("Player");
        if (scriptValue5 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "tpa_from";
            String string3 = "string";
            ScriptValue scriptValue6 = ScriptValue.of((String)"");
            if (scriptValue5 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue5).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v2 = ScriptValue.of((boolean)polyClassPlayer.tm$30_set_typed(string, string3, scriptValue6));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string3));
                arrayList.add(scriptValue6);
                v2 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Server");
        if (scriptValue7 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue8 = scriptValue3;
            if (scriptValue7 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue7).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object6);
                object = polyClassServer.tm$10_get_player(scriptValue8.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue8);
                object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = object;
        builder.val("requester", scriptValue9);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue9);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
            ScriptValue scriptValue10 = scriptContext.getClassOrVar("requester");
            if (scriptValue10 != ScriptValue.NULL) {
                PolyClassPlayer polyClassPlayer;
                ScriptValue scriptValue11;
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(ScriptValue.of((String)("<red>\u2718 <white>" + ((scriptValue11 = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL ? ((polyClassPlayer = PolyClassPlayer.ofGuarded((ScriptValue)scriptValue11)) != null ? polyClassPlayer.tg$68_name() : PolyDispatch.bootstrapGet("memberGet", "name", (ScriptValue)scriptValue11, (ScriptContext)scriptContext).asStr()) : ScriptValue.NULL.asStr()) + " denied your teleport request.")));
                v4 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue10, arrayList2, (ScriptContext)scriptContext);
            } else {
                v4 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Player")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            String string = "<yellow>Teleport request denied.";
            if (scriptValue instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object7);
                v5 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((String)string));
                v5 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, arrayList3, (ScriptContext)scriptContext);
            }
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
        block22: {
            var1_1 = var0.peek();
            var2_2 = var1_1.getClassOrVar("Player");
            if (var2_2 != ScriptValue.NULL) {
                var3_3 = "tpa_target";
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
            if (!(ScriptFormula.valuesEqualStr((ScriptValue)v0 /* !! */ , (String)"") ^ true)) ** GOTO lbl-1000
            var9_9 = var1_1.getClassOrVar("Player");
            if (var9_9 != ScriptValue.NULL) {
                var10_10 = "tpa_moved";
                var11_11 = "int";
                if (var9_9 instanceof ScriptValue.Obj && (var13_13 = (var12_12 = (ScriptValue.Obj)var9_9).instance()) != null && !(var13_13 instanceof PolyClass) && var12_12.typeName().equals("Player")) {
                    var14_14 = new PolyClassPlayer(var13_13);
                    v1 /* !! */  = var14_14.tm$12_get_typed(var10_10, var11_11);
                } else {
                    var15_15 = new ArrayList<ScriptValue>();
                    var15_15.add(ScriptValue.of((String)var10_10));
                    var15_15.add(ScriptValue.of((String)var11_11));
                    v1 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)var9_9, var15_15, (ScriptContext)var1_1);
                }
            } else {
                v1 /* !! */  = ScriptValue.NULL;
            }
            if (ScriptFormula.valuesEqual((ScriptValue)v1 /* !! */ , (ScriptValue)ScriptValue.of((double)0.0))) {
                v2 = true;
            } else lbl-1000:
            // 2 sources

            {
                v2 = false;
            }
            if (!v2) break block22;
            var16_16 = var1_1.getClassOrVar("Player");
            if (var16_16 != ScriptValue.NULL) {
                var17_17 = "tpa_moved";
                var18_18 = "int";
                var19_19 = ScriptValue.of((double)1.0);
                if (var16_16 instanceof ScriptValue.Obj && (var21_21 = (var20_20 = (ScriptValue.Obj)var16_16).instance()) != null && !(var21_21 instanceof PolyClass) && var20_20.typeName().equals("Player")) {
                    var22_22 = new PolyClassPlayer(var21_21);
                    v3 /* !! */  = ScriptValue.of((boolean)var22_22.tm$30_set_typed(var17_17, var18_18, var19_19));
                } else {
                    var23_23 = new ArrayList<ScriptValue>();
                    var23_23.add(ScriptValue.of((String)var17_17));
                    var23_23.add(ScriptValue.of((String)var18_18));
                    var23_23.add(var19_19);
                    v3 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var16_16, var23_23, (ScriptContext)var1_1);
                }
            } else {
                v3 /* !! */  = ScriptValue.NULL;
            }
            var24_24 = var1_1.getClassOrVar("Player");
            if (var24_24 != ScriptValue.NULL) {
                var25_25 = "tpa_target";
                var26_26 = "string";
                var27_27 = ScriptValue.of((String)"");
                if (var24_24 instanceof ScriptValue.Obj && (var29_29 = (var28_28 = (ScriptValue.Obj)var24_24).instance()) != null && !(var29_29 instanceof PolyClass) && var28_28.typeName().equals("Player")) {
                    var30_30 = new PolyClassPlayer(var29_29);
                    v4 /* !! */  = ScriptValue.of((boolean)var30_30.tm$30_set_typed(var25_25, var26_26, var27_27));
                } else {
                    var31_31 = new ArrayList<ScriptValue>();
                    var31_31.add(ScriptValue.of((String)var25_25));
                    var31_31.add(ScriptValue.of((String)var26_26));
                    var31_31.add(var27_27);
                    v4 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)var24_24, var31_31, (ScriptContext)var1_1);
                }
            } else {
                v4 /* !! */  = ScriptValue.NULL;
            }
            var32_32 = var1_1.getClassOrVar("Player");
            if (var32_32 != ScriptValue.NULL) {
                var33_33 = "<red>\u2718 <white>Teleport cancelled - you moved.";
                if (var32_32 instanceof ScriptValue.Obj && (var35_35 = (var34_34 = (ScriptValue.Obj)var32_32).instance()) != null && !(var35_35 instanceof PolyClass) && var34_34.typeName().equals("Player")) {
                    var36_36 = new PolyClassPlayer(var35_35);
                    v5 /* !! */  = ScriptValue.of((boolean)var36_36.tm$42_send_message(var33_33));
                } else {
                    var37_37 = new ArrayList<ScriptValue>();
                    var37_37.add(ScriptValue.of((String)var33_33));
                    v5 /* !! */  = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)var32_32, var37_37, (ScriptContext)var1_1);
                }
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
        Object object3;
        Object object4;
        Object object5;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Server");
        if (scriptValue2 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object6;
            ScriptValue scriptValue3 = scriptContext.getClassOrVar("name");
            if (scriptValue2 instanceof ScriptValue.Obj && (object6 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object6 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object6);
                object5 = polyClassServer.tm$10_get_player(scriptValue3.asStr());
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue3);
                object5 = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object5;
        builder.val("p", scriptValue4);
        ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
        arrayList.add(scriptValue4);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool()) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("p");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(ScriptValue.of((String)"tpa_moved"));
            arrayList2.add(ScriptValue.of((String)"int"));
            object4 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue5, arrayList2, (ScriptContext)scriptContext);
        } else {
            object4 = ScriptValue.NULL;
        }
        if (ScriptFormula.valuesEqual((ScriptValue)object4, (ScriptValue)ScriptValue.of((double)1.0))) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("p");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
            arrayList3.add(ScriptValue.of((String)"tpa_target"));
            arrayList3.add(ScriptValue.of((String)"string"));
            object3 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue6, arrayList3, (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = object3;
        builder.val("target_name", scriptValue7);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue7, (String)"")) {
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("p");
        if (scriptValue8 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
            arrayList4.add(ScriptValue.of((String)"tpa_target"));
            arrayList4.add(ScriptValue.of((String)"string"));
            arrayList4.add(ScriptValue.of((String)""));
            v3 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue8, arrayList4, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("p");
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
            arrayList5.add(ScriptValue.of((String)"tpa_move_handle"));
            arrayList5.add(ScriptValue.of((String)"string"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue9, arrayList5, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = object2;
        builder.val("handle_id", scriptValue10);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue10, (String)"") ^ true) {
            ScriptValue scriptValue11 = scriptContext.getClassOrVar("EventManager");
            if (scriptValue11 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList6 = new ArrayList<ScriptValue>();
                arrayList6.add(scriptValue10);
                v5 = PolyDispatch.bootstrapCall("memberCall", "unregister", (ScriptValue)scriptValue11, arrayList6, (ScriptContext)scriptContext);
            } else {
                v5 = ScriptValue.NULL;
            }
            ScriptValue scriptValue12 = scriptContext.getClassOrVar("p");
            if (scriptValue12 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
                arrayList7.add(ScriptValue.of((String)"tpa_move_handle"));
                arrayList7.add(ScriptValue.of((String)"string"));
                arrayList7.add(ScriptValue.of((String)""));
                v6 = PolyDispatch.bootstrapCall("memberCall", "set_typed", (ScriptValue)scriptValue12, arrayList7, (ScriptContext)scriptContext);
            } else {
                v6 = ScriptValue.NULL;
            }
        }
        if ((scriptValue = scriptContext.getClassOrVar("Server")) != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object7;
            ScriptValue scriptValue13 = scriptValue7;
            if (scriptValue instanceof ScriptValue.Obj && (object7 = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object7 instanceof PolyClass) && obj.typeName().equals("Server")) {
                PolyClassServer polyClassServer = new PolyClassServer(object7);
                object = polyClassServer.tm$10_get_player(scriptValue13.asStr());
            } else {
                ArrayList<ScriptValue> arrayList8 = new ArrayList<ScriptValue>();
                arrayList8.add(scriptValue13);
                object = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue, arrayList8, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = object;
        builder.val("target", scriptValue14);
        ArrayList<ScriptValue> arrayList9 = new ArrayList<ScriptValue>();
        arrayList9.add(scriptValue14);
        if (ScriptFormula.callBuiltin((String)"is_empty", arrayList9, (ScriptContext)scriptContext).asBool()) {
            ScriptValue scriptValue15 = scriptContext.getClassOrVar("p");
            if (scriptValue15 != ScriptValue.NULL) {
                ArrayList<ScriptValue> arrayList10 = new ArrayList<ScriptValue>();
                arrayList10.add(ScriptValue.of((String)("<red>\u2718 <white>" + scriptValue7.asStr() + " is no longer online.")));
                v8 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue15, arrayList10, (ScriptContext)scriptContext);
            } else {
                v8 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("p");
        if (scriptValue16 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList11 = new ArrayList<ScriptValue>();
            ScriptValue scriptValue17 = scriptContext.getClassOrVar("target");
            arrayList11.add((ScriptValue)(scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapGet("memberGet", "location", (ScriptValue)scriptValue17, (ScriptContext)scriptContext) : ScriptValue.NULL));
            v9 = PolyDispatch.bootstrapCall("memberCall", "teleport_to", (ScriptValue)scriptValue16, arrayList11, (ScriptContext)scriptContext);
        } else {
            v9 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("p");
        if (scriptValue18 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList12 = new ArrayList<ScriptValue>();
            arrayList12.add(ScriptValue.of((String)("<green>\u2714 <white>Teleported to <yellow>" + scriptValue7.asStr() + "<white>!")));
            v10 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue18, arrayList12, (ScriptContext)scriptContext);
        } else {
            v10 = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("target");
        if (scriptValue19 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList13 = new ArrayList<ScriptValue>();
            arrayList13.add(ScriptValue.of((String)("<green>\u2714 <white>" + scriptContext.getStr("name") + " has teleported to you.")));
            v11 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue19, arrayList13, (ScriptContext)scriptContext);
        } else {
            v11 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue openRequestsMenu(ScriptContext.Builder builder) {
        ScriptValue scriptValue;
        Object object;
        Object object2;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Menu");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)27.0));
            arrayList.add(ScriptValue.of((String)"<gold>Teleport Requests"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "create", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = object2;
        builder.val("m", scriptValue3);
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "tpa_from";
            String string2 = "string";
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                object = polyClassPlayer.tm$12_get_typed(string, string2);
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                arrayList.add(ScriptValue.of((String)string2));
                object = PolyDispatch.bootstrapCall("memberCall", "get_typed", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue5 = object;
        builder.val("from_name", scriptValue5);
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"") ^ true) {
            Object object4;
            ScriptValue scriptValue6 = scriptContext.getClassOrVar("Server");
            if (scriptValue6 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object5;
                ScriptValue scriptValue7 = scriptValue5;
                if (scriptValue6 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue6).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Server")) {
                    PolyClassServer polyClassServer = new PolyClassServer(object5);
                    object4 = polyClassServer.tm$10_get_player(scriptValue7.asStr());
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(scriptValue7);
                    object4 = PolyDispatch.bootstrapCall("memberCall", "get_player", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                object4 = ScriptValue.NULL;
            }
            ScriptValue scriptValue8 = object4;
            builder.val("requester", scriptValue8);
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptValue8);
            if (ScriptFormula.callBuiltin((String)"is_empty", arrayList, (ScriptContext)scriptContext).asBool() ^ true) {
                Object object6;
                Object object7;
                ArrayList<ScriptValue.Array> arrayList2 = new ArrayList<ScriptValue.Array>();
                ArrayList<ScriptValue> arrayList3 = new ArrayList<ScriptValue>();
                arrayList3.add(ScriptValue.of((String)"<gray>Click to accept"));
                arrayList2.add(new ScriptValue.Array(arrayList3));
                ArrayList<ScriptValue> arrayList4 = new ArrayList<ScriptValue>();
                arrayList4.add(ScriptValue.of((String)("<yellow>" + scriptValue5.asStr())));
                ScriptValue scriptValue9 = scriptContext.getClassOrVar("Item");
                if (scriptValue9 != ScriptValue.NULL) {
                    ScriptValue.Obj obj;
                    Object object8;
                    ScriptValue scriptValue10 = scriptValue8;
                    if (scriptValue9 instanceof ScriptValue.Obj && (object8 = (obj = (ScriptValue.Obj)scriptValue9).instance()) != null && !(object8 instanceof PolyClass) && obj.typeName().equals("Item")) {
                        PolyClassItem polyClassItem = new PolyClassItem(object8);
                        object7 = polyClassItem.tm$22_skull(scriptValue10);
                    } else {
                        ArrayList<ScriptValue> arrayList5 = new ArrayList<ScriptValue>();
                        arrayList5.add(scriptValue10);
                        object7 = PolyDispatch.bootstrapCall("memberCall", "skull", (ScriptValue)scriptValue9, arrayList5, (ScriptContext)scriptContext);
                    }
                } else {
                    object7 = ScriptValue.NULL;
                }
                CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_lore", (ScriptValue)PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)object7, arrayList4, (ScriptContext)scriptContext), arrayList2, (ScriptContext)scriptContext);
                builder.val("head", (ScriptValue)callSite);
                ScriptValue scriptValue11 = scriptContext.getClassOrVar("m");
                if (scriptValue11 != ScriptValue.NULL) {
                    ArrayList<Object> arrayList6 = new ArrayList<Object>();
                    arrayList6.add(ScriptValue.of((double)13.0));
                    arrayList6.add(callSite);
                    arrayList6.add(ScriptValue.of((String)"tpa.pf:on_accept"));
                    object6 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue11, arrayList6, (ScriptContext)scriptContext);
                } else {
                    object6 = ScriptValue.NULL;
                }
                ScriptValue scriptValue12 = object6;
                builder.val("m", scriptValue12);
            }
        }
        if (ScriptFormula.valuesEqualStr((ScriptValue)scriptValue5, (String)"")) {
            Object object9;
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"<gray>No pending requests"));
            ArrayList<ScriptValue> arrayList7 = new ArrayList<ScriptValue>();
            arrayList7.add(ScriptValue.of((String)"minecraft:gray_stained_glass_pane"));
            arrayList7.add(ScriptValue.of((double)1.0));
            CallSite callSite = PolyDispatch.bootstrapCall("memberCall", "with_name", (ScriptValue)ScriptFormula.callBuiltin((String)"create_item", arrayList7, (ScriptContext)scriptContext), arrayList, (ScriptContext)scriptContext);
            builder.val("filler", (ScriptValue)callSite);
            ScriptValue scriptValue13 = scriptContext.getClassOrVar("m");
            if (scriptValue13 != ScriptValue.NULL) {
                ArrayList<Object> arrayList8 = new ArrayList<Object>();
                arrayList8.add(ScriptValue.of((double)13.0));
                arrayList8.add(callSite);
                object9 = PolyDispatch.bootstrapCall("memberCall", "set_item", (ScriptValue)scriptValue13, arrayList8, (ScriptContext)scriptContext);
            } else {
                object9 = ScriptValue.NULL;
            }
            ScriptValue scriptValue14 = object9;
            builder.val("m", scriptValue14);
        }
        if ((scriptValue = scriptContext.getClassOrVar("m")) != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            v6 = PolyDispatch.bootstrapCall("memberCall", "open", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
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
