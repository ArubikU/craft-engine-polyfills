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

public final class VirtualuiCreateDemo {
    public static ScriptValue openCreateDemo(ScriptContext.Builder builder) {
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
            arrayList.add(ScriptValue.of((String)"Create-ported widget demo"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "screen", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object2;
        builder.val("ui", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("ui");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)6.5));
            arrayList.add(ScriptValue.of((double)4.0));
            v3 = PolyDispatch.bootstrapCall("memberCall", "bounds", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("ui");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_close"));
            v4 = PolyDispatch.bootstrapCall("memberCall", "on_close", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("ui");
        if (scriptValue7 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"title"));
            arrayList.add(ScriptValue.of((String)"<gold><bold>CREATE-STYLE DEMO</bold>\n<gray>Every widget, real Create icons"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)2.4));
            v5 = PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("ui");
        if (scriptValue8 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"btn_add"));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((double)(-3.2)));
            arrayList.add(ScriptValue.of((double)1.2));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_add"));
            arrayList.add(ScriptValue.of((double)0.6));
            arrayList.add(ScriptValue.of((double)0.6));
            v6 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("ui");
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"cml:create_plus"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.6));
            v7 = PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("ui");
        if (scriptValue10 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_hover"));
            v8 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
        } else {
            v8 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("ui");
        if (scriptValue11 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_unhover"));
            v9 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
        } else {
            v9 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("ui");
        if (scriptValue12 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"btn_trash"));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((double)(-2.2)));
            arrayList.add(ScriptValue.of((double)1.2));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_trash"));
            arrayList.add(ScriptValue.of((double)0.6));
            arrayList.add(ScriptValue.of((double)0.6));
            v10 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
        } else {
            v10 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("ui");
        if (scriptValue13 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"cml:create_trash"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.6));
            v11 = PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
        } else {
            v11 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("ui");
        if (scriptValue14 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_hover"));
            v12 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
        } else {
            v12 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("ui");
        if (scriptValue15 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_unhover"));
            v13 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
        } else {
            v13 = ScriptValue.NULL;
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("ui");
        if (scriptValue16 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"btn_confirm"));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((double)(-1.2)));
            arrayList.add(ScriptValue.of((double)1.2));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_confirm"));
            arrayList.add(ScriptValue.of((double)0.6));
            arrayList.add(ScriptValue.of((double)0.6));
            v14 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
        } else {
            v14 = ScriptValue.NULL;
        }
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("ui");
        if (scriptValue17 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"cml:create_check"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.6));
            v15 = PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue17, arrayList, (ScriptContext)scriptContext);
        } else {
            v15 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("ui");
        if (scriptValue18 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_hover"));
            v16 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue18, arrayList, (ScriptContext)scriptContext);
        } else {
            v16 = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("ui");
        if (scriptValue19 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_unhover"));
            v17 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
        } else {
            v17 = ScriptValue.NULL;
        }
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("ui");
        if (scriptValue20 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"btn_cancel"));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((double)(-0.2)));
            arrayList.add(ScriptValue.of((double)1.2));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_cancel_btn"));
            arrayList.add(ScriptValue.of((double)0.6));
            arrayList.add(ScriptValue.of((double)0.6));
            v18 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue20, arrayList, (ScriptContext)scriptContext);
        } else {
            v18 = ScriptValue.NULL;
        }
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("ui");
        if (scriptValue21 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"cml:create_cancel"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.6));
            v19 = PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue21, arrayList, (ScriptContext)scriptContext);
        } else {
            v19 = ScriptValue.NULL;
        }
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("ui");
        if (scriptValue22 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_hover"));
            v20 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue22, arrayList, (ScriptContext)scriptContext);
        } else {
            v20 = ScriptValue.NULL;
        }
        ScriptValue scriptValue23 = scriptContext.getClassOrVar("ui");
        if (scriptValue23 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_unhover"));
            v21 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue23, arrayList, (ScriptContext)scriptContext);
        } else {
            v21 = ScriptValue.NULL;
        }
        ScriptValue scriptValue24 = scriptContext.getClassOrVar("ui");
        if (scriptValue24 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"btn_edit"));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((double)0.8));
            arrayList.add(ScriptValue.of((double)1.2));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_edit"));
            arrayList.add(ScriptValue.of((double)0.6));
            arrayList.add(ScriptValue.of((double)0.6));
            v22 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue24, arrayList, (ScriptContext)scriptContext);
        } else {
            v22 = ScriptValue.NULL;
        }
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("ui");
        if (scriptValue25 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"cml:create_edit"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.6));
            v23 = PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue25, arrayList, (ScriptContext)scriptContext);
        } else {
            v23 = ScriptValue.NULL;
        }
        ScriptValue scriptValue26 = scriptContext.getClassOrVar("ui");
        if (scriptValue26 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_hover"));
            v24 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue26, arrayList, (ScriptContext)scriptContext);
        } else {
            v24 = ScriptValue.NULL;
        }
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("ui");
        if (scriptValue27 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_unhover"));
            v25 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue27, arrayList, (ScriptContext)scriptContext);
        } else {
            v25 = ScriptValue.NULL;
        }
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("ui");
        if (scriptValue28 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"btn_refresh"));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((double)1.8));
            arrayList.add(ScriptValue.of((double)1.2));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_refresh"));
            arrayList.add(ScriptValue.of((double)0.6));
            arrayList.add(ScriptValue.of((double)0.6));
            v26 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue28, arrayList, (ScriptContext)scriptContext);
        } else {
            v26 = ScriptValue.NULL;
        }
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("ui");
        if (scriptValue29 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"cml:create_refresh"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.6));
            v27 = PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue29, arrayList, (ScriptContext)scriptContext);
        } else {
            v27 = ScriptValue.NULL;
        }
        ScriptValue scriptValue30 = scriptContext.getClassOrVar("ui");
        if (scriptValue30 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_hover"));
            v28 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue30, arrayList, (ScriptContext)scriptContext);
        } else {
            v28 = ScriptValue.NULL;
        }
        ScriptValue scriptValue31 = scriptContext.getClassOrVar("ui");
        if (scriptValue31 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_toolbar_unhover"));
            v29 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue31, arrayList, (ScriptContext)scriptContext);
        } else {
            v29 = ScriptValue.NULL;
        }
        ScriptValue scriptValue32 = scriptContext.getClassOrVar("ui");
        if (scriptValue32 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"play_toggle"));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((double)(-3.2)));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((boolean)false));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_play_toggle"));
            arrayList.add(ScriptValue.of((double)0.6));
            arrayList.add(ScriptValue.of((double)0.6));
            v30 = PolyDispatch.bootstrapCall("memberCall", "toggle", (ScriptValue)scriptValue32, arrayList, (ScriptContext)scriptContext);
        } else {
            v30 = ScriptValue.NULL;
        }
        ScriptValue scriptValue33 = scriptContext.getClassOrVar("ui");
        if (scriptValue33 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"cml:create_pause"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.6));
            v31 = PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue33, arrayList, (ScriptContext)scriptContext);
        } else {
            v31 = ScriptValue.NULL;
        }
        ScriptValue scriptValue34 = scriptContext.getClassOrVar("ui");
        if (scriptValue34 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"grid_icon"));
            arrayList.add(ScriptValue.of((String)"cml:create_grid"));
            arrayList.add(ScriptValue.of((double)(-1.6)));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.6));
            v32 = PolyDispatch.bootstrapCall("memberCall", "icon", (ScriptValue)scriptValue34, arrayList, (ScriptContext)scriptContext);
        } else {
            v32 = ScriptValue.NULL;
        }
        ScriptValue scriptValue35 = scriptContext.getClassOrVar("ui");
        if (scriptValue35 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"held_item_frame"));
            arrayList.add(ScriptValue.of((String)"cml:create_slot_frame"));
            arrayList.add(ScriptValue.of((double)(-0.6)));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.85));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((double)(-0.3)));
            v33 = PolyDispatch.bootstrapCall("memberCall", "icon", (ScriptValue)scriptValue35, arrayList, (ScriptContext)scriptContext);
        } else {
            v33 = ScriptValue.NULL;
        }
        ScriptValue scriptValue36 = scriptContext.getClassOrVar("ui");
        if (scriptValue36 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"held_item"));
            arrayList.add(ScriptValue.of((String)"minecraft:golden_axe"));
            arrayList.add(ScriptValue.of((double)(-0.6)));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.6));
            v34 = PolyDispatch.bootstrapCall("memberCall", "item", (ScriptValue)scriptValue36, arrayList, (ScriptContext)scriptContext);
        } else {
            v34 = ScriptValue.NULL;
        }
        ScriptValue scriptValue37 = scriptContext.getClassOrVar("ui");
        if (scriptValue37 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"gear_slot_frame"));
            arrayList.add(ScriptValue.of((String)"cml:create_slot_frame"));
            arrayList.add(ScriptValue.of((double)0.4));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.85));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((double)(-0.3)));
            v35 = PolyDispatch.bootstrapCall("memberCall", "icon", (ScriptValue)scriptValue37, arrayList, (ScriptContext)scriptContext);
        } else {
            v35 = ScriptValue.NULL;
        }
        ScriptValue scriptValue38 = scriptContext.getClassOrVar("ui");
        if (scriptValue38 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"gear_slot"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((String)"minecraft:iron_ingot"));
            arrayList.add(ScriptValue.of((double)0.4));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.6));
            arrayList.add(ScriptValue.of((double)0.6));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_slot_click"));
            v36 = PolyDispatch.bootstrapCall("memberCall", "slot", (ScriptValue)scriptValue38, arrayList, (ScriptContext)scriptContext);
        } else {
            v36 = ScriptValue.NULL;
        }
        ScriptValue scriptValue39 = scriptContext.getClassOrVar("ui");
        if (scriptValue39 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"brass_block"));
            arrayList.add(ScriptValue.of((String)"minecraft:gold_block"));
            arrayList.add(ScriptValue.of((double)1.6));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)0.7));
            v37 = PolyDispatch.bootstrapCall("memberCall", "block", (ScriptValue)scriptValue39, arrayList, (ScriptContext)scriptContext);
        } else {
            v37 = ScriptValue.NULL;
        }
        ScriptValue scriptValue40 = scriptContext.getClassOrVar("ui");
        if (scriptValue40 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"self_render"));
            arrayList.add(ScriptValue.of((double)2.8));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)1.2));
            v38 = PolyDispatch.bootstrapCall("memberCall", "player_render", (ScriptValue)scriptValue40, arrayList, (ScriptContext)scriptContext);
        } else {
            v38 = ScriptValue.NULL;
        }
        ScriptValue scriptValue41 = scriptContext.getClassOrVar("ui");
        if (scriptValue41 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"speed_track_bg"));
            arrayList.add(ScriptValue.of((String)"cml:create_scroll_track"));
            arrayList.add(ScriptValue.of((double)(-2.0)));
            arrayList.add(ScriptValue.of((double)(-1.4)));
            arrayList.add(ScriptValue.of((double)2.4));
            arrayList.add(ScriptValue.of((double)0.3));
            arrayList.add(ScriptValue.of((String)""));
            arrayList.add(ScriptValue.of((double)(-0.3)));
            v39 = PolyDispatch.bootstrapCall("memberCall", "image", (ScriptValue)scriptValue41, arrayList, (ScriptContext)scriptContext);
        } else {
            v39 = ScriptValue.NULL;
        }
        ScriptValue scriptValue42 = scriptContext.getClassOrVar("ui");
        if (scriptValue42 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"speed_scrollbar"));
            arrayList.add(ScriptValue.of((boolean)false));
            arrayList.add(ScriptValue.of((double)(-2.0)));
            arrayList.add(ScriptValue.of((double)(-1.4)));
            arrayList.add(ScriptValue.of((double)2.4));
            arrayList.add(ScriptValue.of((double)0.4));
            arrayList.add(ScriptValue.of((double)0.5));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_speed_change"));
            arrayList.add(ScriptValue.of((String)"cml:create_scroll_handle"));
            v40 = PolyDispatch.bootstrapCall("memberCall", "scrollbar", (ScriptValue)scriptValue42, arrayList, (ScriptContext)scriptContext);
        } else {
            v40 = ScriptValue.NULL;
        }
        ScriptValue scriptValue43 = scriptContext.getClassOrVar("ui");
        if (scriptValue43 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_scrollbar_armed"));
            v41 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue43, arrayList, (ScriptContext)scriptContext);
        } else {
            v41 = ScriptValue.NULL;
        }
        ScriptValue scriptValue44 = scriptContext.getClassOrVar("ui");
        if (scriptValue44 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_scrollbar_released"));
            v42 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue44, arrayList, (ScriptContext)scriptContext);
        } else {
            v42 = ScriptValue.NULL;
        }
        ScriptValue scriptValue45 = scriptContext.getClassOrVar("ui");
        if (scriptValue45 != ScriptValue.NULL) {
            ArrayList<Object> arrayList = new ArrayList<Object>();
            arrayList.add(ScriptValue.of((String)"mode_select"));
            ArrayList<ScriptValue> arrayList2 = new ArrayList<ScriptValue>();
            arrayList2.add(ScriptValue.of((String)"Auto"));
            arrayList2.add(ScriptValue.of((String)"Manual"));
            arrayList2.add(ScriptValue.of((String)"Off"));
            arrayList.add(new ScriptValue.Array(arrayList2));
            arrayList.add(ScriptValue.of((String)"<white>\u25c0 <gold>${VUISelect.value()}</gold> \u25b6"));
            arrayList.add(ScriptValue.of((double)1.8));
            arrayList.add(ScriptValue.of((double)(-1.4)));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((String)"examples/virtualui_create_demo.pf:on_mode_change"));
            v43 = PolyDispatch.bootstrapCall("memberCall", "select", (ScriptValue)scriptValue45, arrayList, (ScriptContext)scriptContext);
        } else {
            v43 = ScriptValue.NULL;
        }
        ScriptValue scriptValue46 = scriptContext.getClassOrVar("ui");
        if (scriptValue46 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"queue_progress"));
            arrayList.add(ScriptValue.of((boolean)false));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)(-1.9)));
            arrayList.add(ScriptValue.of((double)3.2));
            arrayList.add(ScriptValue.of((double)0.35));
            arrayList.add(ScriptValue.of((double)0.35));
            v44 = PolyDispatch.bootstrapCall("memberCall", "progress", (ScriptValue)scriptValue46, arrayList, (ScriptContext)scriptContext);
        } else {
            v44 = ScriptValue.NULL;
        }
        ScriptValue scriptValue47 = scriptContext.getClassOrVar("ui");
        if (scriptValue47 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((String)"status"));
            arrayList.add(ScriptValue.of((String)"<gray>Hover a button to see its id in the actionbar"));
            arrayList.add(ScriptValue.of((double)0.0));
            arrayList.add(ScriptValue.of((double)(-2.4)));
            v45 = PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue47, arrayList, (ScriptContext)scriptContext);
        } else {
            v45 = ScriptValue.NULL;
        }
        ScriptValue scriptValue48 = scriptContext.getClassOrVar("ui");
        if (scriptValue48 != ScriptValue.NULL) {
            ArrayList arrayList = new ArrayList();
            object = PolyDispatch.bootstrapCall("memberCall", "build", (ScriptValue)scriptValue48, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue49 = object;
        builder.val("built", scriptValue49);
        ScriptValue scriptValue50 = scriptContext.getClassOrVar("built");
        if (scriptValue50 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            v47 = PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)scriptValue50, arrayList, (ScriptContext)scriptContext);
        } else {
            v47 = ScriptValue.NULL;
        }
        ScriptValue scriptValue51 = scriptContext.getClassOrVar("Player");
        if (scriptValue51 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "<aqua>Create-style demo opened. <white>/virtualui close</white> to exit.";
            if (scriptValue51 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue51).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v48 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v48 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue51, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v48 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onClose(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<gray>Create-style demo closed.";
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

    public static ScriptValue onToolbarHover(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue2 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<gray>Hovering: <white>"), (ScriptValue)scriptContext.getClassOrVar("widget_id"));
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

    public static ScriptValue onToolbarUnhover(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "";
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

    public static ScriptValue onAdd(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>+ Add pressed";
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
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            arrayList.add(ScriptValue.of((String)"queue_progress"));
            arrayList.add(ScriptValue.of((double)1.0));
            v1 = PolyDispatch.bootstrapCall("memberCall", "set_progress", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onTrash(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<red>Trash pressed";
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
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            arrayList.add(ScriptValue.of((String)"queue_progress"));
            arrayList.add(ScriptValue.of((double)0.0));
            v1 = PolyDispatch.bootstrapCall("memberCall", "set_progress", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onConfirm(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>Confirmed";
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
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            arrayList.add(ScriptValue.of((String)"queue_progress"));
            arrayList.add(ScriptValue.of((double)0.75));
            v1 = PolyDispatch.bootstrapCall("memberCall", "set_progress", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onCancelBtn(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        return ScriptValue.NULL;
    }

    public static ScriptValue onEdit(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<yellow>Edit pressed";
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
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            arrayList.add(ScriptValue.of((String)"queue_progress"));
            arrayList.add(ScriptValue.of((double)0.5));
            v1 = PolyDispatch.bootstrapCall("memberCall", "set_progress", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onRefresh(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<aqua>Refreshed";
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
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        if (scriptValue2 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            arrayList.add(ScriptValue.of((String)"queue_progress"));
            arrayList.add(ScriptValue.of((double)0.25));
            v1 = PolyDispatch.bootstrapCall("memberCall", "set_progress", (ScriptValue)scriptValue2, arrayList, (ScriptContext)scriptContext);
        } else {
            v1 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onPlayToggle(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getBool("is_on")) {
            ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
            if (scriptValue != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<green>\u25b6 Playing";
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
                String string = "<gray>\u23f8 Paused";
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

    public static ScriptValue onSlotClick(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue2 = ScriptFormula.addPolymorphic((ScriptValue)ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<white>Slot "), (ScriptValue)scriptContext.getClassOrVar("slot_index")), (ScriptValue)ScriptValue.of((String)" clicked"));
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

    public static ScriptValue onScrollbarArmed(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<yellow>Speed scrollbar armed.";
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
            String string = "<gray>Speed scrollbar released.";
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

    public static ScriptValue onSpeedChange(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue2 = ScriptValue.of((String)"<aqua>Speed: <white>");
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

    public static ScriptValue onModeChange(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue2 = ScriptFormula.addPolymorphic((ScriptValue)ScriptValue.of((String)"<aqua>Mode: <white>"), (ScriptValue)scriptContext.getClassOrVar("option"));
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
