/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassPlayer
 *  dev.arubik.craftengine.script.PolyDispatch
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptContext$Builder
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script.gen.examples;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassPlayer;
import dev.arubik.craftengine.script.PolyDispatch;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.lang.invoke.MethodHandles;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public final class VirtualuiCursorGallery {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue openCursorGallery(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("VirtualUI");
        if ((scriptValue != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "is_open", (ScriptValue)scriptValue, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL).asBool()) {
            ScriptValue scriptValue2 = scriptContext.getClassOrVar("Player");
            if (scriptValue2 != ScriptValue.NULL) {
                ScriptValue.Obj obj;
                Object object;
                String string = "<red>You already have a VirtualUI open - <white>/virtualui close</white> first.";
                if (scriptValue2 instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue2).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                    PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                    v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
                } else {
                    v0 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue2, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
                }
            } else {
                v0 = ScriptValue.NULL;
            }
            return ScriptValue.NULL;
        }
        ScriptValue scriptValue3 = scriptContext.getClassOrVar("VirtualUI");
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "screen", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "Cursor gallery")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("ui", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("ui");
        Object object = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "bounds", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 6.5)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 4.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("ui");
        Object object2 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_close", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "examples/virtualui_cursor_gallery.pf:on_close")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("ui");
        Object object3 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "title")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<gold><bold>CURSOR GALLERY</bold>\n<gray>Hover each button to preview its cursor state")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 2.4)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("ui");
        Object object4 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_grab")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>grab</white>")), (ScriptValue)ScriptValue.of((double)(-2.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.1)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("ui");
        Object object5 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "grab")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("ui");
        Object object6 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_not_allowed")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>not_allowed</white>")), (ScriptValue)ScriptValue.of((double)(-1.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("ui");
        Object object7 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "not_allowed")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("ui");
        Object object8 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_help")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>help</white>")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.4)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("ui");
        Object object9 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "help")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("ui");
        Object object10 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_alert")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>alert</white>")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.7)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.1)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("ui");
        Object object11 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "alert")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("ui");
        Object object12 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_text")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>text</white>")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 3.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("ui");
        Object object13 = scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "text")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("ui");
        Object object14 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_crosshair")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>crosshair</white>")), (ScriptValue)ScriptValue.of((double)(-2.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.5)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("ui");
        Object object15 = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "crosshair")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("ui");
        Object object16 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_zoom_in")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>zoom_in</white>")), (ScriptValue)ScriptValue.of((double)(-0.9)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.3)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("ui");
        Object object17 = scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "zoom_in")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("ui");
        Object object18 = scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_zoom_out")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>zoom_out</white>")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.7)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.4)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue23 = scriptContext.getClassOrVar("ui");
        Object object19 = scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue23, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "zoom_out")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue24 = scriptContext.getClassOrVar("ui");
        Object object20 = scriptValue24 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue24, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_move")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>move</white>")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 2.4)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("ui");
        Object object21 = scriptValue25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue25, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "move")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue26 = scriptContext.getClassOrVar("ui");
        Object object22 = scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue26, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_processing")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>processing</white>")), (ScriptValue)ScriptValue.of((double)(-1.0)), (ScriptValue)ScriptValue.of((double)(-0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("ui");
        Object object23 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue27, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "processing")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("ui");
        Object object24 = scriptValue28 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "scrollbar", (ScriptValue)scriptValue28, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "demo_scrollbar")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", VirtualuiCursorGallery.class, 0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.0)), (ScriptValue)ScriptValue.of((double)(-1.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 2.4)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.4)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("ui");
        Object object25 = scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "examples/virtualui_cursor_gallery.pf:on_scrollbar_armed")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue30 = scriptContext.getClassOrVar("ui");
        Object object26 = scriptValue30 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue30, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "examples/virtualui_cursor_gallery.pf:on_scrollbar_released")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue31 = scriptContext.getClassOrVar("ui");
        Object object27 = scriptValue31 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue31, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "status")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<gray>Hover a button to preview its cursor - no click needed")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.0)), (ScriptValue)ScriptValue.of((double)(-2.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue32 = scriptContext.getClassOrVar("ui");
        ScriptValue scriptValue33 = scriptValue32 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "build", (ScriptValue)scriptValue32, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("built", scriptValue33);
        ScriptValue scriptValue34 = scriptContext.getClassOrVar("built");
        Object object28 = scriptValue34 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)scriptValue34, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue35 = scriptContext.getClassOrVar("Player");
        if (scriptValue35 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object29;
            String string = "<aqua>Cursor gallery opened. <white>/virtualui close</white> to exit.";
            if (scriptValue35 instanceof ScriptValue.Obj && (object29 = (obj = (ScriptValue.Obj)scriptValue35).instance()) != null && !(object29 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object29);
                v29 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                v29 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue35, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v29 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }

    public static ScriptValue onClose(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<gray>Cursor gallery closed.";
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
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
            String string = "<yellow>Scrollbar armed - drag arms the 'hold' cursor automatically.";
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$36_send_actionbar(string));
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
            String string = "<gray>Scrollbar released.";
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer.tm$36_send_actionbar(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        return ScriptValue.NULL;
    }
}
