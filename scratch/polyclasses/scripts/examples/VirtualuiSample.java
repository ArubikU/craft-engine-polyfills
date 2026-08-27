/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassPlayer_v2
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptFormula
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Array
 */
package dev.arubik.craftengine.script.gen.examples;

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
                String string = "<red>You already have a VirtualUI open - <white>/virtualui close</white> first.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
                v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("VirtualUI");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "screen", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, " sample (ported)")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("ui", scriptValue4);
        Object object = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "bounds", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 6.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 4.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object2 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_close", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_close")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object3 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "left")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<white><bold>LEFT HOLOGRAM</bold>\n<gray>Static label, like 's own sample")), (ScriptValue)ScriptValue.of((double)(-2.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object4 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "right")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<white><bold>RIGHT HOLOGRAM</bold>\n<gray>Static label")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 2.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object5 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "info")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<green><bold>Cursor Information</bold>\n<gray>Move the cursor over a button")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 1.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object6 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "pulse")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "${if(sin(VUIText.ticks_open() * 0.1) > 0, '<yellow>', '<gold>')}<bold>* PULSING *</bold>")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 2.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object7 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "link_button")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<aqua><bold>Open a link</bold>")), (ScriptValue)ScriptValue.of((double)(-0.9)), (ScriptValue)ScriptValue.of((double)(-0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_click_link")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 1.3)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object8 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_hover_info")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object9 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_unhover_info")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object10 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "close_button")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<red><bold>Close</bold>")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.9)), (ScriptValue)ScriptValue.of((double)(-0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_click_close")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object11 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_hover_info")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object12 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_unhover_info")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object13 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "icon_demo")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "minecraft:compass")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 0.0)), (ScriptValue)ScriptValue.of((double)(-1.4)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object14 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "player_render", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "self_render")), (ScriptValue)ScriptValue.of((double)(-2.5)), (ScriptValue)ScriptValue.of((double)(-1.4)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object15 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "block", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "block_demo")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "minecraft:diamond_block")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 2.5)), (ScriptValue)ScriptValue.of((double)(-1.4)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (scriptValue4 != ScriptValue.NULL) {
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
            v16 = PolyDispatch.bootstrapCall("memberCall", "scrollbar", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            v16 = ScriptValue.NULL;
        }
        Object object16 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_scrollbar_armed")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object17 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue4, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_scrollbar_released")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        if (scriptValue4 != ScriptValue.NULL) {
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
            v19 = PolyDispatch.bootstrapCall("memberCall", "toggle", (ScriptValue)scriptValue4, arrayList, (ScriptContext)scriptContext);
        } else {
            v19 = ScriptValue.NULL;
        }
        if (scriptValue4 != ScriptValue.NULL) {
            ScriptValue scriptValue5 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "difficulty_select");
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "Easy"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "Normal"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "Hard"));
            v21 = PolyDispatch.bootstrapCall("memberCall", "select", (ScriptValue)scriptValue4, (ScriptValue)scriptValue5, (ScriptValue)new ScriptValue.Array(arrayList), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<white>\u25c0 <yellow>${VUISelect.value()}</yellow> \u25b6")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 3.2)), (ScriptValue)ScriptValue.of((double)(-2.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiSample.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "examples/virtualui_sample.pf:on_difficulty_change")), (ScriptContext)scriptContext);
        } else {
            v21 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptValue4 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "build", (ScriptValue)scriptValue4, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("built", scriptValue6);
        Object object18 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)scriptValue6, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("Player");
        if (scriptValue7 != ScriptValue.NULL) {
            String string = "<aqua>VirtualUI sample opened. Left/right-click a button, sneak, or <white>/virtualui close</white> to exit.";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue7);
            v23 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue7, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
                String string = "<gray>You don't have a VirtualUI open.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue2);
                v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hide", (ScriptValue)scriptValue3, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue4 = scriptContext.getClassOrVar("Player");
        if (scriptValue4 != ScriptValue.NULL) {
            String string = "<aqua>VirtualUI closed.";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue4);
            v2 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue4, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            v2 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onHoverInfo(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "change_hologram", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "info")), (ScriptValue)ScriptValue.of((String)("<green><bold>Cursor Information</bold></green>\n<gray>Hovering: " + scriptContext.getStr("widget_id"))), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "show_tooltip", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)ScriptValue.of((String)("<dark_gray>Widget: <white>" + scriptContext.getStr("widget_id"))), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onUnhoverInfo(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "change_hologram", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "info")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "<green><bold>Cursor Information</bold></green>\n<gray>Move the cursor over a button")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        Object object2 = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hide_tooltip", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onClickLink(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            String string = "<yellow>Opening the CraftEngine Polyfills repo link...";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        Object object = scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "open_url", (ScriptValue)scriptValue, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiSample.class, "https://github.com/")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onClickClose(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            String string = "<gray>Closing VirtualUI sample.";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hide", (ScriptValue)scriptValue2, (ScriptValue)scriptValue, (ScriptContext)scriptContext) : ScriptValue.NULL;
        return ScriptValue.NULL;
    }

    public static ScriptValue onClose(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            String string = "<gray>VirtualUI sample closed.";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string)) : PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onScrollbarArmed(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            String string = "<yellow>Volume scrollbar armed - move the cursor, click again to release.";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string)) : PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onScrollbarReleased(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            String string = "<gray>Volume scrollbar released.";
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string)) : PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onScrollbarChange(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue scriptValue2 = ScriptValue.of((String)("<aqua>Volume: <white>" + ScriptFormula.callBuiltin1((String)"round", (ScriptValue)ScriptValue.of((double)(scriptContext.getNum("value") * 100.0)), (ScriptContext)scriptContext).asStr() + "%"));
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(scriptValue2.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onToggleMute(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        if (scriptContext.getBool("is_on")) {
            ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
            if (scriptValue != ScriptValue.NULL) {
                String string = "<red>Muted.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
                v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string)) : PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            } else {
                v0 = ScriptValue.NULL;
            }
        } else {
            ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
            if (scriptValue != ScriptValue.NULL) {
                String string = "<green>Unmuted.";
                PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
                v1 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string)) : PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
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
            ScriptValue scriptValue2 = ScriptValue.of((String)("<aqua>Difficulty: <white>" + scriptContext.getStr("option") + " <gray>(" + scriptContext.getStr("index") + ")"));
            PolyClassPlayer_v2 polyClassPlayer_v2 = PolyClassPlayer_v2.ofGuarded((ScriptValue)scriptValue);
            v0 = polyClassPlayer_v2 != null ? ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(scriptValue2.asStr())) : PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)scriptValue2, (ScriptContext)scriptContext);
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
