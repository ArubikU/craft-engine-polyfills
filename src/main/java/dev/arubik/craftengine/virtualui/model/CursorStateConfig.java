package dev.arubik.craftengine.virtualui.model;

/**
 * One named cursor appearance from {@code virtualui.yml}'s {@code cursor.states} section (port of
 * 's cursor-provider concept) — either a text glyph ({@link #text}, styled like any other
 * hologram line) or an item icon ({@link #itemId}), never both. {@code VirtualUICameraSystem}
 * picks the active state name per tick (a script override via
 * {@code VirtualUI.set_cursor_state}, else scrollbar-drag orientation, else hover, else
 * {@code "normal"}) and resolves it through {@link VirtualUIConfig#cursorState(String)} to one of
 * these, falling back to {@code "normal"} then the engine's built-in default glyph if a state
 * isn't configured.
 */
public record CursorStateConfig(HologramLineConfig text, String itemId) {
    public boolean isItem() { return itemId != null && !itemId.isBlank(); }
}
