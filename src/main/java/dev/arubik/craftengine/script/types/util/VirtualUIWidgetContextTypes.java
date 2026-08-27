package dev.arubik.craftengine.script.types.util;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.virtualui.model.WidgetRenderContext;

/**
 * Per-widget-kind script context types, bound (by the SAME name as the widget kind it describes)
 * while resolving one of that widget's dynamic (script-ref) fields — see
 * {@code dev.arubik.craftengine.virtualui.DynamicFieldResolver}. A button's icon script sees
 * {@code VUIButton}; a scrollbar's tracker-item script sees {@code VUIScrollbar} (with its own
 * extra {@code value()}/{@code is_dragging()}); every kind shares {@code VUIWidget}'s base
 * properties via normal single-parent {@code PolyTypeRegistry} inheritance:
 * <pre>
 *   def icon():
 *       if VUIButton.is_hover():
 *           return "cml:hover_button"
 *       end
 *       return "cml:button"
 *   end
 * </pre>
 * Only meaningful inside such a dynamic-field evaluation — reading e.g. {@code VUIScrollbar.*}
 * outside one returns sensible empty defaults rather than erroring (see {@link #ctx}).
 */
public final class VirtualUIWidgetContextTypes {

    private VirtualUIWidgetContextTypes() {}

    public static void register() {
        // ctx(obj) is more than a plain cast (it falls back to a default WidgetRenderContext for a
        // non-WidgetRenderContext instance) — per the migration rules, the typed handler's first
        // parameter stays Object and ctx(obj) is still called explicitly inside each body, rather
        // than declaring WidgetRenderContext as the handler's instance type directly.
        PolyTypeRegistry.define("VUIWidget")
                .methodTyped0("id", TypeCodecs.STRING, (Object obj) -> ctx(obj).widgetId())
                .methodTyped0("is_hover", TypeCodecs.BOOL, (Object obj) -> ctx(obj).hovered())
                // Server ticks since the screen opened — a time axis every widget kind inherits for
                // script-driven animation, e.g. a pulsing scale:
                // "<yellow>" + round((sin(VUIWidget.ticks_open() * 0.1) + 1) * 50) + "%"
                .methodTyped0("ticks_open", TypeCodecs.DOUBLE, (Object obj) -> (double) ctx(obj).ticksOpen());

        PolyTypeRegistry.define("VUIButton", "VUIWidget");
        PolyTypeRegistry.define("VUIText", "VUIWidget");
        PolyTypeRegistry.define("VUIIcon", "VUIWidget");
        PolyTypeRegistry.define("VUIItem", "VUIWidget");
        PolyTypeRegistry.define("VUIImage", "VUIWidget");
        PolyTypeRegistry.define("VUIBlock", "VUIWidget");
        PolyTypeRegistry.define("VUIPlayerRender", "VUIWidget");

        PolyTypeRegistry.define("VUISlot", "VUIWidget")
                .methodTyped0("slot_index", TypeCodecs.DOUBLE, (Object obj) -> (double) ctx(obj).slotIndex());

        PolyTypeRegistry.define("VUIScrollbar", "VUIWidget")
                .methodTyped0("value", TypeCodecs.DOUBLE, (Object obj) -> ctx(obj).value())
                .methodTyped0("is_dragging", TypeCodecs.BOOL, (Object obj) -> ctx(obj).dragging());

        PolyTypeRegistry.define("VUIToggle", "VUIWidget")
                .methodTyped0("is_on", TypeCodecs.BOOL, (Object obj) -> ctx(obj).value() > 0.5);

        PolyTypeRegistry.define("VUISelect", "VUIWidget")
                .methodTyped0("index", TypeCodecs.DOUBLE, (Object obj) -> ctx(obj).value())
                .methodTyped0("value", TypeCodecs.STRING, (Object obj) -> ctx(obj).stringValue());

        PolyTypeRegistry.define("VUIProgress", "VUIWidget")
                .methodTyped0("value", TypeCodecs.DOUBLE, (Object obj) -> ctx(obj).value());
    }

    /** Maps a {@link dev.arubik.craftengine.virtualui.model.Widget} class to the script type name
     *  its dynamic fields are evaluated under — the ONE place that mapping lives, so a new widget
     *  kind adds one line here instead of scattering the choice across every call site. */
    public static String typeNameFor(dev.arubik.craftengine.virtualui.model.Widget widget) {
        return switch (widget) {
            case dev.arubik.craftengine.virtualui.model.Widget.ButtonWidget ignored -> "VUIButton";
            case dev.arubik.craftengine.virtualui.model.Widget.LabelWidget ignored -> "VUIText";
            case dev.arubik.craftengine.virtualui.model.Widget.IconWidget ignored -> "VUIIcon";
            case dev.arubik.craftengine.virtualui.model.Widget.ItemWidget ignored -> "VUIItem";
            case dev.arubik.craftengine.virtualui.model.Widget.ImageWidget ignored -> "VUIImage";
            case dev.arubik.craftengine.virtualui.model.Widget.BlockWidget ignored -> "VUIBlock";
            case dev.arubik.craftengine.virtualui.model.Widget.PlayerRenderWidget ignored -> "VUIPlayerRender";
            case dev.arubik.craftengine.virtualui.model.Widget.SlotWidget ignored -> "VUISlot";
            case dev.arubik.craftengine.virtualui.model.Widget.ScrollbarWidget ignored -> "VUIScrollbar";
            case dev.arubik.craftengine.virtualui.model.Widget.ToggleWidget ignored -> "VUIToggle";
            case dev.arubik.craftengine.virtualui.model.Widget.SelectWidget ignored -> "VUISelect";
            case dev.arubik.craftengine.virtualui.model.Widget.ProgressWidget ignored -> "VUIProgress";
        };
    }

    private static WidgetRenderContext ctx(Object obj) {
        return obj instanceof WidgetRenderContext c ? c : new WidgetRenderContext("", false, false, 0, -1, 0, "");
    }
}
