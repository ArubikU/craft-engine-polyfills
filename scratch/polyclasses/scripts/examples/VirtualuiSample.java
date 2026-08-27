/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
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
import dev.arubik.craftengine.script.PolyClassPlayer_v2;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class VirtualuiSample {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue openSample(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        if ((scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "is_open", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>You already have a VirtualUI open - <white>/virtualui close</white> first.";
                if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("VirtualUI");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "screen", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, " sample (ported)")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("ui", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("ui");
        Object object = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "bounds", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 6.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 4.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("ui");
        Object object2 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_close", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_close")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("ui");
        Object object3 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "left")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<white><bold>LEFT HOLOGRAM</bold>\n<gray>Static label, like 's own sample")), (ScriptValue)ScriptValue.of((double)(-2.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("ui");
        Object object4 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "right")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<white><bold>RIGHT HOLOGRAM</bold>\n<gray>Static label")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 2.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("ui");
        Object object5 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "info")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<green><bold>Cursor Information</bold>\n<gray>Move the cursor over a button")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 1.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("ui");
        Object object6 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "pulse")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "${if(sin(VUIText.ticks_open() * 0.1) > 0, '<yellow>', '<gold>')}<bold>* PULSING *</bold>")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 2.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("ui");
        Object object7 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "link_button")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<aqua><bold>Open a link</bold>")), (ScriptValue)ScriptValue.of((double)(-0.9)), (ScriptValue)ScriptValue.of((double)(-0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_click_link")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 1.3)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("ui");
        Object object8 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_hover_info")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("ui");
        Object object9 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_unhover_info")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("ui");
        Object object10 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "close_button")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<red><bold>Close</bold>")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.9)), (ScriptValue)ScriptValue.of((double)(-0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_click_close")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("ui");
        Object object11 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_hover_info")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("ui");
        Object object12 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_unhover_info")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("ui");
        Object object13 = scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "icon_demo")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "minecraft:compass")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0)), (ScriptValue)ScriptValue.of((double)(-1.4)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("ui");
        Object object14 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "player_render", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "self_render")), (ScriptValue)ScriptValue.of((double)(-2.5)), (ScriptValue)ScriptValue.of((double)(-1.4)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("ui");
        Object object15 = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "block", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "block_demo")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "minecraft:diamond_block")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 2.5)), (ScriptValue)ScriptValue.of((double)(-1.4)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("ui");
        if (scriptValue20 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "volume_scrollbar"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", VirtualuiSample.class, 0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0));
            arrayList.add(ScriptValue.of((double)(-2.2)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 2.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.4));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.5));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_scrollbar_change"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "minecraft:lever"));
            v16 = PolyDispatch.bootstrapCall("memberCall", "scrollbar", (ScriptValue)scriptValue20, arrayList, (ScriptContext)scriptContext);
        } else {
            v16 = ScriptValue.NULL;
        }
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("ui");
        Object object16 = scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_scrollbar_armed")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("ui");
        Object object17 = scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_scrollbar_released")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue23 = scriptContext.getClassOrVar("ui");
        if (scriptValue23 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "mute_toggle"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<green><bold>ON</bold>"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<red><bold>OFF</bold>"));
            arrayList.add(ScriptValue.of((double)(-3.2)));
            arrayList.add(ScriptValue.of((double)(-2.2)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", VirtualuiSample.class, 0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_toggle_mute"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.5));
            v19 = PolyDispatch.bootstrapCall("memberCall", "toggle", (ScriptValue)scriptValue23, arrayList, (ScriptContext)scriptContext);
        } else {
            v19 = ScriptValue.NULL;
        }
        ScriptValue scriptValue24 = scriptContext.getClassOrVar("ui");
        if (scriptValue24 != ScriptValue.NULL) {
            ScriptValue scriptValue25 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "difficulty_select");
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "Easy"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "Normal"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "Hard"));
            v21 = PolyDispatch.bootstrapCall("memberCall", "select", (ScriptValue)scriptValue24, (ScriptValue)scriptValue25, (ScriptValue)new ScriptValue.Array(arrayList), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<white>\u25c0 <yellow>${VUISelect.value()}</yellow> \u25b6")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 3.2)), (ScriptValue)ScriptValue.of((double)(-2.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_difficulty_change")), (ScriptContext)scriptContext);
        } else {
            v21 = ScriptValue.NULL;
        }
        ScriptValue scriptValue26 = scriptContext.getClassOrVar("ui");
        ScriptValue scriptValue27 = scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "build", (ScriptValue)scriptValue26, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("built", scriptValue27);
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("built");
        Object object18 = scriptValue28 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)scriptValue28, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("Player");
        if (scriptValue29 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object19;
            String string = "<aqua>VirtualUI sample opened. Left/right-click a button, sneak, or <white>/virtualui close</white> to exit.";
            if (scriptValue29 instanceof ScriptValue.Obj && (object19 = (obj = (ScriptValue.Obj)scriptValue29).instance()) != null && !(object19 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object19);
                v23 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string));
            } else {
                v23 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue29, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v23 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue closeSample(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        if ((scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "is_open", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool() ^ true) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<gray>You don't have a VirtualUI open.";
                if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hide", (ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object2;
            String string = "<aqua>VirtualUI closed.";
            if (scriptValue4 instanceof ScriptValue.Obj && (object2 = (obj = (ScriptValue.Obj)scriptValue4).instance()) != null && !(object2 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object2);
                v2 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string));
            } else {
                v2 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onHoverInfo(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "change_hologram", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "info")), (ScriptValue)ScriptValue.of((String)("<green><bold>Cursor Information</bold></green>\n<gray>Hovering: " + scriptContext.getStr("widget_id"))), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        Object object2 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "show_tooltip", (ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)ScriptValue.of((String)("<dark_gray>Widget: <white>" + scriptContext.getStr("widget_id"))), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onUnhoverInfo(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "change_hologram", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "info")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<green><bold>Cursor Information</bold></green>\n<gray>Move the cursor over a button")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        Object object2 = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hide_tooltip", (ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_url", (ScriptValue)scriptValue2, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "https://github.com/")), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hide", (ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
            StringBuilder stringBuilder = new StringBuilder().append("<aqua>Volume: <white>");
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(ScriptValue.of((double)(scriptContext.getNum("value") * 100.0)));
            ScriptValue scriptValue2 = ScriptValue.of((String)stringBuilder.append(ScriptFormula.callBuiltin((String)"round", arrayList, (ScriptContext)scriptContext).asStr()).append("%").toString());
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v1 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(scriptValue2.asStr()));
            } else {
                v1 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
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
                    PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                    PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                    v1 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string));
                } else {
                    v1 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
            ScriptValue scriptValue2 = ScriptValue.of((String)("<aqua>Difficulty: <white>" + scriptContext.getStr("option") + " <gray>(" + scriptContext.getStr("index") + ")"));
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(scriptValue2.asStr()));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
