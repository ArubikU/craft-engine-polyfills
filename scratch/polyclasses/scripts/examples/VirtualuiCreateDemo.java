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
public final class VirtualuiCreateDemo {
    private static volatile ScriptContext FILE_SCOPE;

    public static ScriptContext fileScope() {
        ScriptContext scriptContext = FILE_SCOPE;
        if (scriptContext == null) {
            scriptContext = ScriptContext.builder().build();
        }
        return scriptContext;
    }

    public static ScriptValue openCreateDemo(ScriptContext.Builder builder) {
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
        ScriptValue scriptValue4 = scriptValue3 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "screen", (ScriptValue)scriptValue3, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "Create-ported widget demo")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("ui", scriptValue4);
        ScriptValue scriptValue5 = scriptContext.getClassOrVar("ui");
        Object object = scriptValue5 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "bounds", (ScriptValue)scriptValue5, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 6.5)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 4.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue6 = scriptContext.getClassOrVar("ui");
        Object object2 = scriptValue6 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_close", (ScriptValue)scriptValue6, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_close")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue7 = scriptContext.getClassOrVar("ui");
        Object object3 = scriptValue7 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue7, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "title")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "<gold><bold>CREATE-STYLE DEMO</bold>\n<gray>Every widget, real Create icons")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 2.4)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue8 = scriptContext.getClassOrVar("ui");
        Object object4 = scriptValue8 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue8, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "btn_add")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "")), (ScriptValue)ScriptValue.of((double)(-3.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_add")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue9 = scriptContext.getClassOrVar("ui");
        Object object5 = scriptValue9 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue9, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_plus")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue10 = scriptContext.getClassOrVar("ui");
        Object object6 = scriptValue10 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue10, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_hover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue11 = scriptContext.getClassOrVar("ui");
        Object object7 = scriptValue11 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue11, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_unhover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue12 = scriptContext.getClassOrVar("ui");
        Object object8 = scriptValue12 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue12, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "btn_trash")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "")), (ScriptValue)ScriptValue.of((double)(-2.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_trash")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue13 = scriptContext.getClassOrVar("ui");
        Object object9 = scriptValue13 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue13, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_trash")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue14 = scriptContext.getClassOrVar("ui");
        Object object10 = scriptValue14 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue14, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_hover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue15 = scriptContext.getClassOrVar("ui");
        Object object11 = scriptValue15 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue15, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_unhover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue16 = scriptContext.getClassOrVar("ui");
        Object object12 = scriptValue16 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue16, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "btn_confirm")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "")), (ScriptValue)ScriptValue.of((double)(-1.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_confirm")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue17 = scriptContext.getClassOrVar("ui");
        Object object13 = scriptValue17 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue17, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_check")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue18 = scriptContext.getClassOrVar("ui");
        Object object14 = scriptValue18 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue18, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_hover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue19 = scriptContext.getClassOrVar("ui");
        Object object15 = scriptValue19 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue19, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_unhover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue20 = scriptContext.getClassOrVar("ui");
        Object object16 = scriptValue20 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue20, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "btn_cancel")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "")), (ScriptValue)ScriptValue.of((double)(-0.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_cancel_btn")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue21 = scriptContext.getClassOrVar("ui");
        Object object17 = scriptValue21 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue21, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_cancel")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue22 = scriptContext.getClassOrVar("ui");
        Object object18 = scriptValue22 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue22, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_hover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue23 = scriptContext.getClassOrVar("ui");
        Object object19 = scriptValue23 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue23, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_unhover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue24 = scriptContext.getClassOrVar("ui");
        Object object20 = scriptValue24 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue24, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "btn_edit")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.8)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_edit")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue25 = scriptContext.getClassOrVar("ui");
        Object object21 = scriptValue25 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue25, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_edit")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue26 = scriptContext.getClassOrVar("ui");
        Object object22 = scriptValue26 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue26, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_hover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue27 = scriptContext.getClassOrVar("ui");
        Object object23 = scriptValue27 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue27, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_unhover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue28 = scriptContext.getClassOrVar("ui");
        Object object24 = scriptValue28 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "button", (ScriptValue)scriptValue28, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "btn_refresh")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.8)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_refresh")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue29 = scriptContext.getClassOrVar("ui");
        Object object25 = scriptValue29 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue29, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_refresh")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue30 = scriptContext.getClassOrVar("ui");
        Object object26 = scriptValue30 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue30, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_hover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue31 = scriptContext.getClassOrVar("ui");
        Object object27 = scriptValue31 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue31, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_toolbar_unhover")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue32 = scriptContext.getClassOrVar("ui");
        if (scriptValue32 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "play_toggle"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, ""));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, ""));
            arrayList.add(ScriptValue.of((double)(-3.2)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", VirtualuiCreateDemo.class, 0));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_play_toggle"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6));
            v28 = PolyDispatch.bootstrapCall("memberCall", "toggle", (ScriptValue)scriptValue32, arrayList, (ScriptContext)scriptContext);
        } else {
            v28 = ScriptValue.NULL;
        }
        ScriptValue scriptValue33 = scriptContext.getClassOrVar("ui");
        Object object28 = scriptValue33 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon_of", (ScriptValue)scriptValue33, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_pause")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue34 = scriptContext.getClassOrVar("ui");
        Object object29 = scriptValue34 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon", (ScriptValue)scriptValue34, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "grid_icon")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_grid")), (ScriptValue)ScriptValue.of((double)(-1.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue35 = scriptContext.getClassOrVar("ui");
        Object object30 = scriptValue35 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon", (ScriptValue)scriptValue35, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "held_item_frame")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_slot_frame")), (ScriptValue)ScriptValue.of((double)(-0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.85)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "")), (ScriptValue)ScriptValue.of((double)(-0.3)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue36 = scriptContext.getClassOrVar("ui");
        Object object31 = scriptValue36 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "item", (ScriptValue)scriptValue36, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "held_item")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "minecraft:golden_axe")), (ScriptValue)ScriptValue.of((double)(-0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue37 = scriptContext.getClassOrVar("ui");
        Object object32 = scriptValue37 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "icon", (ScriptValue)scriptValue37, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "gear_slot_frame")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_slot_frame")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.4)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.85)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "")), (ScriptValue)ScriptValue.of((double)(-0.3)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue38 = scriptContext.getClassOrVar("ui");
        Object object33 = scriptValue38 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "slot", (ScriptValue)scriptValue38, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "gear_slot")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "minecraft:iron_ingot")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.4)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_slot_click")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue39 = scriptContext.getClassOrVar("ui");
        Object object34 = scriptValue39 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "block", (ScriptValue)scriptValue39, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "brass_block")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "minecraft:gold_block")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.6)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.7)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue40 = scriptContext.getClassOrVar("ui");
        Object object35 = scriptValue40 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "player_render", (ScriptValue)scriptValue40, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "self_render")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 2.8)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.2)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue41 = scriptContext.getClassOrVar("ui");
        Object object36 = scriptValue41 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "image", (ScriptValue)scriptValue41, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "speed_track_bg")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_scroll_track")), (ScriptValue)ScriptValue.of((double)(-2.0)), (ScriptValue)ScriptValue.of((double)(-1.4)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 2.4)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.3)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "")), (ScriptValue)ScriptValue.of((double)(-0.3)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue42 = scriptContext.getClassOrVar("ui");
        if (scriptValue42 != ScriptValue.NULL) {
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "speed_scrollbar"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", VirtualuiCreateDemo.class, 0));
            arrayList.add(ScriptValue.of((double)(-2.0)));
            arrayList.add(ScriptValue.of((double)(-1.4)));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 2.4));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.4));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.5));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_speed_change"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "cml:create_scroll_handle"));
            v38 = PolyDispatch.bootstrapCall("memberCall", "scrollbar", (ScriptValue)scriptValue42, arrayList, (ScriptContext)scriptContext);
        } else {
            v38 = ScriptValue.NULL;
        }
        ScriptValue scriptValue43 = scriptContext.getClassOrVar("ui");
        Object object37 = scriptValue43 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_hover", (ScriptValue)scriptValue43, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_scrollbar_armed")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue44 = scriptContext.getClassOrVar("ui");
        Object object38 = scriptValue44 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "on_unhover", (ScriptValue)scriptValue44, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_scrollbar_released")), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue45 = scriptContext.getClassOrVar("ui");
        if (scriptValue45 != ScriptValue.NULL) {
            ScriptValue scriptValue46 =  /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "mode_select");
            ArrayList<ScriptValue> arrayList = new ArrayList<ScriptValue>();
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "Auto"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "Manual"));
            arrayList.add( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "Off"));
            v42 = PolyDispatch.bootstrapCall("memberCall", "select", (ScriptValue)scriptValue45, (ScriptValue)scriptValue46, (ScriptValue)new ScriptValue.Array(arrayList), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "<white>\u25c0 <gold>${VUISelect.value()}</gold> \u25b6")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.8)), (ScriptValue)ScriptValue.of((double)(-1.4)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "examples/virtualui_create_demo.pf:on_mode_change")), (ScriptContext)scriptContext);
        } else {
            v42 = ScriptValue.NULL;
        }
        ScriptValue scriptValue47 = scriptContext.getClassOrVar("ui");
        Object object39 = scriptValue47 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "progress", (ScriptValue)scriptValue47, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "queue_progress")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constBool("b", MethodHandles.lookup(), "constBool", VirtualuiCreateDemo.class, 0)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)ScriptValue.of((double)(-1.9)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 3.2)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.35)), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.35)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue48 = scriptContext.getClassOrVar("ui");
        Object object40 = scriptValue48 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "label", (ScriptValue)scriptValue48, (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "status")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "<gray>Hover a button to see its id in the actionbar")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptValue)ScriptValue.of((double)(-2.4)), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue49 = scriptContext.getClassOrVar("ui");
        ScriptValue scriptValue50 = scriptValue49 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "build", (ScriptValue)scriptValue49, (ScriptContext)scriptContext) : ScriptValue.NULL;
        builder.val("built", scriptValue50);
        ScriptValue scriptValue51 = scriptContext.getClassOrVar("built");
        Object object41 = scriptValue51 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "show", (ScriptValue)scriptValue51, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptContext)scriptContext) : ScriptValue.NULL;
        ScriptValue scriptValue52 = scriptContext.getClassOrVar("Player");
        if (scriptValue52 != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object42;
            String string = "<aqua>Create-style demo opened. <white>/virtualui close</white> to exit.";
            if (scriptValue52 instanceof ScriptValue.Obj && (object42 = (obj = (ScriptValue.Obj)scriptValue52).instance()) != null && !(object42 instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object42);
                v46 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$42_send_message(string));
            } else {
                v46 = PolyDispatch.bootstrapCall("memberCall", "send_message", (ScriptValue)scriptValue52, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v46 = ScriptValue.NULL;
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

    public static ScriptValue onToolbarHover(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue2 = ScriptValue.of((String)("<gray>Hovering: <white>" + scriptContext.getStr("widget_id")));
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

    public static ScriptValue onToolbarUnhover(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "";
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

    public static ScriptValue onAdd(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<green>+ Add pressed";
            if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Player")) {
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_progress", (ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "queue_progress")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 1.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_progress", (ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "queue_progress")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.0)), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_progress", (ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "queue_progress")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.75)), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_progress", (ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "queue_progress")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.5)), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                PolyClassPlayer_v2 polyClassPlayer_v2 = new PolyClassPlayer_v2(object);
                v0 = ScriptValue.of((boolean)polyClassPlayer_v2.tm$36_send_actionbar(string));
            } else {
                v0 = PolyDispatch.bootstrapCall("memberCall", "send_actionbar", (ScriptValue)scriptValue, (ScriptValue)ScriptValue.of((String)string), (ScriptContext)scriptContext);
            }
        } else {
            v0 = ScriptValue.NULL;
        }
        ScriptValue scriptValue2 = scriptContext.getClassOrVar("VirtualUI");
        Object object = scriptValue2 != ScriptValue.NULL ? PolyDispatch.bootstrapCall("memberCall", "set_progress", (ScriptValue)scriptValue2, (ScriptValue)scriptContext.getClassOrVar("Player"), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constStr("s", MethodHandles.lookup(), "constStr", VirtualuiCreateDemo.class, "queue_progress")), (ScriptValue)( /* dynamic constant */ (ScriptValue)ScriptValue.constNum("n", MethodHandles.lookup(), "constNum", VirtualuiCreateDemo.class, 0.25)), (ScriptContext)scriptContext) : ScriptValue.NULL;
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
                String string = "<gray>\u23f8 Paused";
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

    public static ScriptValue onSlotClick(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue2 = ScriptValue.of((String)("<white>Slot " + scriptContext.getStr("slot_index") + " clicked"));
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

    public static ScriptValue onScrollbarArmed(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            String string = "<yellow>Speed scrollbar armed.";
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
            String string = "<gray>Speed scrollbar released.";
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

    public static ScriptValue onSpeedChange(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            StringBuilder stringBuilder = new StringBuilder().append("<aqua>Speed: <white>");
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

    public static ScriptValue onModeChange(ScriptContext.Builder builder) {
        ScriptContext scriptContext = builder.peek();
        ScriptValue scriptValue = scriptContext.getClassOrVar("Player");
        if (scriptValue != ScriptValue.NULL) {
            ScriptValue.Obj obj;
            Object object;
            ScriptValue scriptValue2 = ScriptValue.of((String)("<aqua>Mode: <white>" + scriptContext.getStr("option")));
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
