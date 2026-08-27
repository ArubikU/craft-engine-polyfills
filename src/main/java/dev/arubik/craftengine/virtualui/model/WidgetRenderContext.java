package dev.arubik.craftengine.virtualui.model;

/**
 * Live per-render-tick state for ONE widget, bound as the script-visible {@code "Button"} object
 * while resolving a dynamic (script-ref) text/icon field on that widget — see
 * {@code VirtualUICameraSystem#resolveDynamicFields}. Lets a script write
 * {@code "examples/foo.pf:icon"} for a button's icon and have that function read
 * {@code Button.is_hover()} to switch which item shows, exactly the same "a string containing
 * '.pf:' is evaluated dynamically" convention a machine's declarative page already uses for its
 * button/slot names and lore (see {@code CmdRegistry#resolveText}).
 */
public record WidgetRenderContext(String widgetId, boolean hovered, boolean dragging, double value, int slotIndex,
                                   int ticksOpen, String stringValue) {
}
