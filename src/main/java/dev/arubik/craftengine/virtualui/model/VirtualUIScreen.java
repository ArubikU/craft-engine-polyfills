package dev.arubik.craftengine.virtualui.model;

import java.util.List;
import java.util.Map;

/**
 * A finalized, showable UI definition — what {@code VirtualUIBuilder.build()} produces (the
 * "BuiltDialog" equivalent). Immutable; {@code VirtualUICameraSystem.show(player, screen)} is what
 * actually spawns the camera lock + widget displays for a viewer.
 *
 * <p>{@code maxOffsetX}/{@code maxOffsetY} (0 = use {@code VirtualUIConfig}'s global default) are
 * this screen's own "screen area" — the cursor is clamped inside this rectangle exactly like a
 * real screen's edges, letting a wide/tall layout (or a deliberately cramped one) declare its own
 * bounds instead of always inheriting the server-wide default. {@code cursorIcon} (nullable)
 * overrides the global cursor glyph's text/style for just this screen; {@code cursorItemId}
 * (nullable) renders the cursor as an item icon instead of text entirely — at most one of the two
 * should be set (item wins if both are). {@code hoverCursorStates} (widget id -> {@code
 * cursor.states.<name>} key, see virtualui.yml) is a per-widget override of the generic "hover"
 * cursor-state — set via {@code .hover_state(name)} chained right after a widget builder call (same
 * chaining convention as {@code .on_hover(ref)}), so e.g. a disabled-looking button can show
 * "not_allowed" instead of the default hover glyph without a script having to call
 * {@code VirtualUI.set_cursor_state} from its own {@code on_hover}/{@code on_unhover} pair.
 */
public record VirtualUIScreen(
        String title,
        List<Widget> widgets,
        double cameraDistance,
        boolean playerInvisibleOnStart,
        String onCloseRef,
        double maxOffsetX,
        double maxOffsetY,
        HologramLineConfig cursorIcon,
        String cursorItemId,
        Map<String, String> hoverCursorStates
) {
    public static VirtualUIScreen of(String title, List<Widget> widgets, double cameraDistance,
                                      boolean playerInvisibleOnStart, String onCloseRef) {
        return new VirtualUIScreen(title, List.copyOf(widgets), cameraDistance, playerInvisibleOnStart, onCloseRef,
                0, 0, null, null, Map.of());
    }

    public static VirtualUIScreen of(String title, List<Widget> widgets, double cameraDistance,
                                      boolean playerInvisibleOnStart, String onCloseRef,
                                      double maxOffsetX, double maxOffsetY,
                                      HologramLineConfig cursorIcon, String cursorItemId,
                                      Map<String, String> hoverCursorStates) {
        return new VirtualUIScreen(title, List.copyOf(widgets), cameraDistance, playerInvisibleOnStart, onCloseRef,
                maxOffsetX, maxOffsetY, cursorIcon, cursorItemId, Map.copyOf(hoverCursorStates));
    }
}
