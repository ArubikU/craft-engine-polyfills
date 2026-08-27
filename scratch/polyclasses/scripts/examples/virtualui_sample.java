/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.examples;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;

public final class VirtualuiSample {
    public static ScriptValue openSample(ScriptContext.Builder builder) {
        Object object;
        Object object2;
        Object object3;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            object3 = PolyDispatch.bootstrapCall("memberCall", "is_open", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object3 = ScriptValue.NULL;
        }
        if (object3.asBool()) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object4;
                String string = "<red>You already have a VirtualUI open - <white>/virtualui close</white> first.";
                if (scriptValue2 instanceof ScriptValue.Obj && (object4 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object4 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object4);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)" sample (ported)"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "screen", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object2;
        builder.val("ui", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("ui");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)6.0));
            arrayList.add(ScriptValue.of((double)4.0));
            v3 = PolyDispatch.bootstrapCall("memberCall", "bounds", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("ui");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_close"));
            v4 = PolyDispatch.bootstrapCall("memberCall", "on_close", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("ui");
        if (scriptValue7 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"left"));
            arrayList.add(ScriptValue.of((String)"<white><bold>LEFT HOLOGRAM</bold>\n<gray>Static label, like 's own sample"));
            arrayList.add(ScriptValue.of((double)(-2.0)));
            arrayList.add(ScriptValue.of((double)0.0));
            v5 = PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("ui");
        if (scriptValue8 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"right"));
            arrayList.add(ScriptValue.of((String)"<white><bold>RIGHT HOLOGRAM</bold>\n<gray>Static label"));
            arrayList.add(ScriptValue.of((double)2.0));
            arrayList.add(ScriptValue.of((double)0.0));
            v6 = PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("ui");
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"info"));
            arrayList.add(ScriptValue.of((String)"<green><bold>Cursor Information</bold>\n<gray>Move the cursor over a button"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)1.6));
            v7 = PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("ui");
        if (scriptValue10 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"pulse"));
            arrayList.add(ScriptValue.of((String)"${if(sin(VUIText.ticks_open() * 0.1) > 0, '<yellow>', '<gold>')}<bold>* PULSING *</bold>"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)2.6));
            v8 = PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
        } else {
            v8 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("ui");
        if (scriptValue11 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"link_button"));
            arrayList.add(ScriptValue.of((String)"<aqua><bold>Open a link</bold>"));
            arrayList.add(ScriptValue.of((double)(-0.9)));
            arrayList.add(ScriptValue.of((double)(-0.6)));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_click_link"));
            arrayList.add(ScriptValue.of((double)1.3));
            arrayList.add(ScriptValue.of((double)0.5));
            v9 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
        } else {
            v9 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("ui");
        if (scriptValue12 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_hover_info"));
            v10 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
        } else {
            v10 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("ui");
        if (scriptValue13 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_unhover_info"));
            v11 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
        } else {
            v11 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("ui");
        if (scriptValue14 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"close_button"));
            arrayList.add(ScriptValue.of((String)"<red><bold>Close</bold>"));
            arrayList.add(ScriptValue.of((double)0.9));
            arrayList.add(ScriptValue.of((double)(-0.6)));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_click_close"));
            arrayList.add(ScriptValue.of((double)1.0));
            arrayList.add(ScriptValue.of((double)0.5));
            v12 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
        } else {
            v12 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("ui");
        if (scriptValue15 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_hover_info"));
            v13 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
        } else {
            v13 = ScriptValue.NULL;
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("ui");
        if (scriptValue16 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_unhover_info"));
            v14 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
        } else {
            v14 = ScriptValue.NULL;
        }
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("ui");
        if (scriptValue17 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"icon_demo"));
            arrayList.add(ScriptValue.of((String)"minecraft:compass"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)(-1.4)));
            v15 = PolyDispatch.bootstrapCall("memberCall", "icon", (ScriptValue)scriptValue17, arrayList, (ScriptContext)scriptContext);
        } else {
            v15 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("ui");
        if (scriptValue18 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"self_render"));
            arrayList.add(ScriptValue.of((double)(-2.5)));
            arrayList.add(ScriptValue.of((double)(-1.4)));
            v16 = PolyDispatch.bootstrapCall("memberCall", "player_render", (ScriptValue)scriptValue18, arrayList, (ScriptContext)scriptContext);
        } else {
            v16 = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("ui");
        if (scriptValue19 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"block_demo"));
            arrayList.add(ScriptValue.of((String)"minecraft:diamond_block"));
            arrayList.add(ScriptValue.of((double)2.5));
            arrayList.add(ScriptValue.of((double)(-1.4)));
            v17 = PolyDispatch.bootstrapCall("memberCall", "block", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
        } else {
            v17 = ScriptValue.NULL;
        }
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("ui");
        if (scriptValue20 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"volume_scrollbar"));
            arrayList.add(ScriptValue.of((boolean)false));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)(-2.2)));
            arrayList.add(ScriptValue.of((double)2.0));
            arrayList.add(ScriptValue.of((double)0.4));
            arrayList.add(ScriptValue.of((double)0.5));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_scrollbar_change"));
            arrayList.add(ScriptValue.of((String)"minecraft:lever"));
            v18 = PolyDispatch.bootstrapCall("memberCall", "scrollbar", (ScriptValue)scriptValue20, arrayList, (ScriptContext)scriptContext);
        } else {
            v18 = ScriptValue.NULL;
        }
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("ui");
        if (scriptValue21 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_scrollbar_armed"));
            v19 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue21, arrayList, (ScriptContext)scriptContext);
        } else {
            v19 = ScriptValue.NULL;
        }
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("ui");
        if (scriptValue22 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_scrollbar_released"));
            v20 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue22, arrayList, (ScriptContext)scriptContext);
        } else {
            v20 = ScriptValue.NULL;
        }
        ScriptValue scriptValue23 = scriptContext.getClassOrVar("ui");
        if (scriptValue23 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"mute_toggle"));
            arrayList.add(ScriptValue.of((String)"<green><bold>ON</bold>"));
            arrayList.add(ScriptValue.of((String)"<red><bold>OFF</bold>"));
            arrayList.add(ScriptValue.of((double)(-3.2)));
            arrayList.add(ScriptValue.of((double)(-2.2)));
            arrayList.add(ScriptValue.of((boolean)false));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_toggle_mute"));
            arrayList.add(ScriptValue.of((double)1.0));
            arrayList.add(ScriptValue.of((double)0.5));
            v21 = PolyDispatch.bootstrapCall("memberCall", "toggle", (ScriptValue)scriptValue23, arrayList, (ScriptContext)scriptContext);
        } else {
            v21 = ScriptValue.NULL;
        }
        ScriptValue scriptValue24 = scriptContext.getClassOrVar("ui");
        if (scriptValue24 != ScriptValue.NULL) {
            ArrayList<Object> arrayList = new ArrayList<Object>();
            arrayList.add(ScriptValue.of((String)"difficulty_select"));
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(ScriptValue.of((String)"Easy"));
            arrayList2.add(ScriptValue.of((String)"Normal"));
            arrayList2.add(ScriptValue.of((String)"Hard"));
            arrayList.add(new ScriptValue.Array(arrayList2));
            arrayList.add(ScriptValue.of((String)"<white>\u25c0 <yellow>${VUISelect.value()}</yellow> \u25b6"));
            arrayList.add(ScriptValue.of((double)3.2));
            arrayList.add(ScriptValue.of((double)(-2.2)));
            arrayList.add(ScriptValue.of((double)1.0));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_sample.pf:on_difficulty_change"));
            v22 = PolyDispatch.bootstrapCall("memberCall", "select", (ScriptValue)scriptValue24, arrayList, (ScriptContext)scriptContext);
        } else {
            v22 = ScriptValue.NULL;
        }
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("ui");
        if (scriptValue25 != ScriptValue.NULL) {
            ArrayList arrayList = new ArrayList();
            object = PolyDispatch.bootstrapCall("memberCall", "build", (ScriptValue)scriptValue25, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue26 = object;
        builder.val("built", scriptValue26);
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("built");
        if (scriptValue27 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            v24 = PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)scriptValue27, arrayList, (ScriptContext)scriptContext);
        } else {
            v24 = ScriptValue.NULL;
        }
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("Player");
        if (scriptValue28 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "<aqua>VirtualUI sample opened. Left/right-click a button, sneak, or <white>/virtualui close</white> to exit.";
            if (scriptValue28 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue28).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v25 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v25 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue28, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v25 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue closeSample(ScriptContext.Builder builder) {
        Object object;
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            object = PolyDispatch.bootstrapCall("memberCall", "is_open", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        if (object.asBool() ^ true) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object2;
                String string = "<gray>You don't have a VirtualUI open.";
                if (scriptValue2 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object2);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue3 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            v2 = PolyDispatch.bootstrapCall("memberCall", "hide", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object3;
            String string = "<aqua>VirtualUI closed.";
            if (scriptValue4 instanceof ScriptValue.Obj && (object3 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object3 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object3);
                v3 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v3 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v3 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onHoverInfo(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            arrayList.add(ScriptValue.of((String)"info"));
            arrayList.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<green><bold>Cursor Information</bold></green>\n<gray>Hovering: "), (ScriptValue)scriptContext.getClassOrVar("widget_id")));
            v0 = PolyDispatch.bootstrapCall("memberCall", "change_hologram", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            arrayList.add(ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<dark_gray>Widget: <white>"), (ScriptValue)scriptContext.getClassOrVar("widget_id")));
            v1 = PolyDispatch.bootstrapCall("memberCall", "show_tooltip", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onUnhoverInfo(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            arrayList.add(ScriptValue.of((String)"info"));
            arrayList.add(ScriptValue.of((String)"<green><bold>Cursor Information</bold></green>\n<gray>Move the cursor over a button"));
            v0 = PolyDispatch.bootstrapCall("memberCall", "change_hologram", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            v1 = PolyDispatch.bootstrapCall("memberCall", "hide_tooltip", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onClickLink(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<yellow>Opening the CraftEngine Polyfills repo link...";
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
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"https://github.com/"));
            v1 = PolyDispatch.bootstrapCall("memberCall", "open_url", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onClickClose(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<gray>Closing VirtualUI sample.";
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
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            v1 = PolyDispatch.bootstrapCall("memberCall", "hide", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onClose(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<gray>VirtualUI sample closed.";
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

    public static ScriptValue onScrollbarArmed(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<yellow>Volume scrollbar armed - move the cursor, click again to release.";
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$36_send_actionbar(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onScrollbarReleased(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<gray>Volume scrollbar released.";
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$36_send_actionbar(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onScrollbarChange(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue2 = ScriptValue.of((String)"<aqua>Volume: <white>");
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("value") * 100.0)));
            ScriptValue scriptValue3 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)scriptValue2, (ScriptValue)ScriptFormula.callBuiltin((String)"round", arrayList, (ScriptContext)scriptContext)), (ScriptValue)ScriptValue.of((String)"%"));
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer.tm$36_send_actionbar(scriptValue3.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
                arrayList2.add(scriptValue3);
                v1 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, arrayList2, (ScriptContext)scriptContext);
            }
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onToggleMute(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getBool("is_on")) {
            ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>Muted.";
                if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$36_send_actionbar(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<green>Unmuted.";
                if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer.tm$36_send_actionbar(string));
                } else {
                    ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                    arrayList.add(ScriptValue.of((String)string));
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
                }
            } else {
                v1 = ScriptValue.NULL;
            }
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onDifficultyChange(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue2 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<aqua>Difficulty: <white>"), (ScriptValue)scriptContext.getClassOrVar("option")), (ScriptValue)ScriptValue.of((String)" <gray>(")), (ScriptValue)scriptContext.getClassOrVar("index")), (ScriptValue)ScriptValue.of((String)")"));
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$36_send_actionbar(scriptValue2.asStr()));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(scriptValue2);
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
