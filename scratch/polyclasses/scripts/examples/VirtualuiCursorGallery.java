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
import java.util.ArrayList;

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
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "Cursor gallery"));
            object2 = PolyDispatch.bootstrapCall("memberCall", "screen", (ScriptValue)scriptValue3, arrayList, (ScriptContext)scriptContext);
        } else {
            object2 = ScriptValue.NULL;
        }
        ScriptValue scriptValue4 = object2;
        builder.val("ui", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("ui");
        if (scriptValue5 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 6.5));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 4.0));
            v3 = PolyDispatch.bootstrapCall("memberCall", "bounds", (ScriptValue)scriptValue5, arrayList, (ScriptContext)scriptContext);
        } else {
            v3 = ScriptValue.NULL;
        }
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("ui");
        if (scriptValue6 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "examples/virtualui_cursor_gallery.pf:on_close"));
            v4 = PolyDispatch.bootstrapCall("memberCall", "on_close", (ScriptValue)scriptValue6, arrayList, (ScriptContext)scriptContext);
        } else {
            v4 = ScriptValue.NULL;
        }
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("ui");
        if (scriptValue7 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "title"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<gold><bold>CURSOR GALLERY</bold>\n<gray>Hover each button to preview its cursor state"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 2.4));
            v5 = PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue7, arrayList, (ScriptContext)scriptContext);
        } else {
            v5 = ScriptValue.NULL;
        }
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("ui");
        if (scriptValue8 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_grab"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>grab</white>"));
            arrayList.add(ScriptValue.of((double)(-2.6)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.1));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            v6 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue8, arrayList, (ScriptContext)scriptContext);
        } else {
            v6 = ScriptValue.NULL;
        }
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("ui");
        if (scriptValue9 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "grab"));
            v7 = PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue9, arrayList, (ScriptContext)scriptContext);
        } else {
            v7 = ScriptValue.NULL;
        }
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("ui");
        if (scriptValue10 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_not_allowed"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>not_allowed</white>"));
            arrayList.add(ScriptValue.of((double)(-1.2)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.6));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            v8 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue10, arrayList, (ScriptContext)scriptContext);
        } else {
            v8 = ScriptValue.NULL;
        }
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("ui");
        if (scriptValue11 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "not_allowed"));
            v9 = PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue11, arrayList, (ScriptContext)scriptContext);
        } else {
            v9 = ScriptValue.NULL;
        }
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("ui");
        if (scriptValue12 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_help"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>help</white>"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.4));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            v10 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue12, arrayList, (ScriptContext)scriptContext);
        } else {
            v10 = ScriptValue.NULL;
        }
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("ui");
        if (scriptValue13 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "help"));
            v11 = PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue13, arrayList, (ScriptContext)scriptContext);
        } else {
            v11 = ScriptValue.NULL;
        }
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("ui");
        if (scriptValue14 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_alert"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>alert</white>"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.7));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.1));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            v12 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue14, arrayList, (ScriptContext)scriptContext);
        } else {
            v12 = ScriptValue.NULL;
        }
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("ui");
        if (scriptValue15 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "alert"));
            v13 = PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue15, arrayList, (ScriptContext)scriptContext);
        } else {
            v13 = ScriptValue.NULL;
        }
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("ui");
        if (scriptValue16 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_text"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>text</white>"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 3.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            v14 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue16, arrayList, (ScriptContext)scriptContext);
        } else {
            v14 = ScriptValue.NULL;
        }
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("ui");
        if (scriptValue17 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "text"));
            v15 = PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue17, arrayList, (ScriptContext)scriptContext);
        } else {
            v15 = ScriptValue.NULL;
        }
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("ui");
        if (scriptValue18 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_crosshair"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>crosshair</white>"));
            arrayList.add(ScriptValue.of((double)(-2.6)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.2));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.5));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            v16 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue18, arrayList, (ScriptContext)scriptContext);
        } else {
            v16 = ScriptValue.NULL;
        }
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("ui");
        if (scriptValue19 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "crosshair"));
            v17 = PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue19, arrayList, (ScriptContext)scriptContext);
        } else {
            v17 = ScriptValue.NULL;
        }
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("ui");
        if (scriptValue20 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_zoom_in"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>zoom_in</white>"));
            arrayList.add(ScriptValue.of((double)(-0.9)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.2));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.3));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            v18 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue20, arrayList, (ScriptContext)scriptContext);
        } else {
            v18 = ScriptValue.NULL;
        }
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("ui");
        if (scriptValue21 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "zoom_in"));
            v19 = PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue21, arrayList, (ScriptContext)scriptContext);
        } else {
            v19 = ScriptValue.NULL;
        }
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("ui");
        if (scriptValue22 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_zoom_out"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>zoom_out</white>"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.7));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.2));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.4));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            v20 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue22, arrayList, (ScriptContext)scriptContext);
        } else {
            v20 = ScriptValue.NULL;
        }
        ScriptValue scriptValue23 = scriptContext.getClassOrVar("ui");
        if (scriptValue23 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "zoom_out"));
            v21 = PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue23, arrayList, (ScriptContext)scriptContext);
        } else {
            v21 = ScriptValue.NULL;
        }
        ScriptValue scriptValue24 = scriptContext.getClassOrVar("ui");
        if (scriptValue24 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_move"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>move</white>"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 2.4));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.2));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            v22 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue24, arrayList, (ScriptContext)scriptContext);
        } else {
            v22 = ScriptValue.NULL;
        }
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("ui");
        if (scriptValue25 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "move"));
            v23 = PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue25, arrayList, (ScriptContext)scriptContext);
        } else {
            v23 = ScriptValue.NULL;
        }
        ScriptValue scriptValue26 = scriptContext.getClassOrVar("ui");
        if (scriptValue26 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "btn_processing"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<white>processing</white>"));
            arrayList.add(ScriptValue.of((double)(-1.0)));
            arrayList.add(ScriptValue.of((double)(-0.6)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 1.6));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            v24 = PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue26, arrayList, (ScriptContext)scriptContext);
        } else {
            v24 = ScriptValue.NULL;
        }
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("ui");
        if (scriptValue27 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "processing"));
            v25 = PolyDispatch.bootstrapCall("memberCall", "hover_state", (ScriptValue)scriptValue27, arrayList, (ScriptContext)scriptContext);
        } else {
            v25 = ScriptValue.NULL;
        }
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("ui");
        if (scriptValue28 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "demo_scrollbar"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", VirtualuiCursorGallery.class, 0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.0));
            arrayList.add(ScriptValue.of((double)(-1.6)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 2.4));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.4));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.5));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, ""));
            v26 = PolyDispatch.bootstrapCall("memberCall", "scrollbar", (ScriptValue)scriptValue28, arrayList, (ScriptContext)scriptContext);
        } else {
            v26 = ScriptValue.NULL;
        }
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("ui");
        if (scriptValue29 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "examples/virtualui_cursor_gallery.pf:on_scrollbar_armed"));
            v27 = PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue29, arrayList, (ScriptContext)scriptContext);
        } else {
            v27 = ScriptValue.NULL;
        }
        ScriptValue scriptValue30 = scriptContext.getClassOrVar("ui");
        if (scriptValue30 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "examples/virtualui_cursor_gallery.pf:on_scrollbar_released"));
            v28 = PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue30, arrayList, (ScriptContext)scriptContext);
        } else {
            v28 = ScriptValue.NULL;
        }
        ScriptValue scriptValue31 = scriptContext.getClassOrVar("ui");
        if (scriptValue31 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "status"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCursorGallery.class, "<gray>Hover a button to preview its cursor - no click needed"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCursorGallery.class, 0.0));
            arrayList.add(ScriptValue.of((double)(-2.6)));
            v29 = PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue31, arrayList, (ScriptContext)scriptContext);
        } else {
            v29 = ScriptValue.NULL;
        }
        ScriptValue scriptValue32 = scriptContext.getClassOrVar("ui");
        if (scriptValue32 != ScriptValue.NULL) {
            ArrayList arrayList = new ArrayList();
            object = PolyDispatch.bootstrapCall("memberCall", "build", (ScriptValue)scriptValue32, arrayList, (ScriptContext)scriptContext);
        } else {
            object = ScriptValue.NULL;
        }
        ScriptValue scriptValue33 = object;
        builder.val("built", scriptValue33);
        ScriptValue scriptValue34 = scriptContext.getClassOrVar("built");
        if (scriptValue34 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add(scriptContext.getClassOrVar("Player"));
            v31 = PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)scriptValue34, arrayList, (ScriptContext)scriptContext);
        } else {
            v31 = ScriptValue.NULL;
        }
        ScriptValue scriptValue35 = scriptContext.getClassOrVar("Player");
        if (scriptValue35 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object5;
            String string = "<aqua>Cursor gallery opened. <white>/virtualui close</white> to exit.";
            if (scriptValue35 instanceof ScriptValue.Obj && (object5 = (obj = (ScriptValue.Obj)scriptValue35).instance()) != null && !(object5 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer polyClassPlayer = new PolyClassPlayer(object5);
                v32 = ScriptValue.of((boolean)polyClassPlayer.tm$42_send_message(string));
            } else {
                ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
                arrayList.add(ScriptValue.of((String)string));
                v32 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue35, arrayList, (ScriptContext)scriptContext);
            }
        } else {
            v32 = ScriptValue.NULL;
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
            String string = "<yellow>Scrollbar armed - drag arms the 'hold' cursor automatically.";
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
            String string = "<gray>Scrollbar released.";
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
}
