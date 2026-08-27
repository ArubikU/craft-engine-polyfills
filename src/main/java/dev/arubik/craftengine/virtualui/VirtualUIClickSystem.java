package dev.arubik.craftengine.virtualui;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.virtualui.model.CameraSession;
import dev.arubik.craftengine.virtualui.model.Widget;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Hover/click resolution for an open VirtualUI session — port of 's area-hover-tracking
 * ({@code CursorSystem#updateHoveredAreaState}). Whichever widget containing the current cursor
 * position has the HIGHEST {@code priority} wins a tie. There is no built-in action vocabulary
 * here: a hit's {@code onClick}/{@code onHover}/{@code onUnhover} is either {@code null} (nothing
 * happens) or a {@code "file.pf:func[:args]"} script ref, resolved and run exactly like a Menu
 * button's action — everything else (opening a URL, running a command, switching server, changing
 * a hologram) is a script calling {@code Player}/{@code VirtualUI} API methods itself.
 */
public final class VirtualUIClickSystem {

    private VirtualUIClickSystem() {}

    /** Recomputes which widget the cursor is over and fires {@code onHover}/{@code onUnhover}
     *  transitions — called once per tick from {@code VirtualUICameraSystem.tick()}. A
     *  {@link Widget.ScrollbarWidget}'s {@code onHover}/{@code onUnhover} are repurposed for its
     *  arm/release drag gesture (see {@link #toggleScrollbarDrag}) — plain mouse-over must NOT
     *  also fire them, or a script would see the same callback for two unrelated triggers. */
    public static void updateHover(CameraSession session, Player player) {
        Widget hit = resolveHit(session);
        String newId = hit != null ? hit.id() : null;
        String oldId = session.hoveredWidgetId();
        if (java.util.Objects.equals(newId, oldId)) return;

        if (oldId != null) {
            Widget prev = session.liveWidgets().get(oldId);
            if (prev != null && !(prev instanceof Widget.ScrollbarWidget)
                    && prev.onUnhover() != null && !prev.onUnhover().isBlank()) {
                runScriptRef(prev.onUnhover(), player, session, oldId);
            }
        }
        session.setHoveredWidgetId(newId);
        if (hit != null) {
            dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger().info(
                    "[VirtualUI][debug] hover -> " + hit.id() + " onHover=\"" + hit.onHover()
                            + "\" isScrollbar=" + (hit instanceof Widget.ScrollbarWidget));
        }
        if (hit != null && !(hit instanceof Widget.ScrollbarWidget)
                && hit.onHover() != null && !hit.onHover().isBlank()) {
            runScriptRef(hit.onHover(), player, session, newId);
        }
    }

    /** Resolves the current hit (if any) and runs its {@code onClick} — called from
     *  {@code VirtualUIListener} on a real player interact/attack input. A {@link Widget.ScrollbarWidget}
     *  hit is handled specially: click ARMS/RELEASES the drag gesture (port of 's
     *  hotbar-scrollbar "click to grab, move, click to release") instead of running a script. */
    public static void handleClick(CameraSession session, Player player) {
        Widget hit = resolveHit(session);
        if (hit == null) return;
        if (hit instanceof Widget.ScrollbarWidget sb) {
            toggleScrollbarDrag(session, player, sb);
            return;
        }
        if (hit instanceof Widget.ToggleWidget tg) {
            flipToggle(session, player, tg);
            return;
        }
        if (hit instanceof Widget.SelectWidget sel) {
            advanceSelect(session, player, sel, 1);
            return;
        }
        if (hit.onClick() == null || hit.onClick().isBlank()) return;
        runScriptRef(hit.onClick(), player, session, hit.id());
    }

    private static void flipToggle(CameraSession session, Player player, Widget.ToggleWidget tg) {
        boolean next = !session.toggleValues().getOrDefault(tg.id(), tg.value());
        session.toggleValues().put(tg.id(), next);
        if (tg.onChange() != null && !tg.onChange().isBlank()) {
            runScriptRefWithValue(tg.onChange(), player, session, tg.id(), next);
        }
    }

    /** Advances a {@link Widget.SelectWidget} by {@code direction} options, wrapping at both ends
     *  — shared by click (always +1, see {@link #handleClick}) and scroll-wheel (either direction,
     *  see {@link #nudgeHoveredScrollbar}'s sibling call in {@code VirtualUIScrollPacketListener}).
     *  Fires {@code onChange(widget_id, option_string, index)}. No-op on an empty options list. */
    static void advanceSelect(CameraSession session, Player player, Widget.SelectWidget sel, int direction) {
        if (sel.options().isEmpty()) return;
        int previous = session.selectIndices().getOrDefault(sel.id(), sel.index());
        int next = Math.floorMod(previous + direction, sel.options().size());
        if (next == previous) return;
        session.selectIndices().put(sel.id(), next);
        if (sel.onChange() != null && !sel.onChange().isBlank()) {
            runScriptRefWithSelection(sel.onChange(), player, session, sel.id(), sel.options().get(next), next);
        }
    }

    private static void toggleScrollbarDrag(CameraSession session, Player player, Widget.ScrollbarWidget sb) {
        boolean wasDragging = sb.id().equals(session.draggingScrollbarId());
        session.setDraggingScrollbarId(wasDragging ? null : sb.id());
        String callback = wasDragging ? sb.onUnhover() : sb.onHover();
        if (callback != null && !callback.isBlank()) runScriptRef(callback, player, session, sb.id());
    }

    /** Drives the armed scrollbar's live value from the current cursor position within its track
     *  — called every tick from {@code VirtualUICameraSystem.tick()}, a no-op unless
     *  {@link CameraSession#draggingScrollbarId()} is set. Fires {@code onChange} (with the new
     *  0.0-1.0 value as a trailing script argument, after the widget id) only when the value
     *  actually moved, not every tick. */
    public static void updateDraggingScrollbar(CameraSession session, Player player) {
        String id = session.draggingScrollbarId();
        if (id == null) return;
        if (!(session.liveWidgets().get(id) instanceof Widget.ScrollbarWidget sb)) {
            session.setDraggingScrollbarId(null);
            return;
        }
        // Same rendered-position rule as resolveHit — drag math tracks whatever cursor coordinate
        // is actually drawn (predicted, when enabled), so the thumb follows the glyph the player
        // sees rather than a coordinate that's lagging a frame or two behind it.
        boolean predicted = VirtualUIConfig.get().predictionEnabled();
        double drawnX = predicted ? session.predictedCursorX() : session.cursorX();
        double drawnY = predicted ? session.predictedCursorY() : session.cursorY();

        double span = sb.vertical() ? sb.height() : sb.width();
        double center = sb.vertical() ? sb.offsetY() : sb.offsetX();
        double cursor = sb.vertical() ? drawnY : drawnX;

        // Cross-axis bound: a vertical scrollbar's "span" (used for the value below) is its
        // height, but dragging never checked how far the cursor strayed sideways along its WIDTH
        // — the thumb kept tracking the value no matter how far off to the side the cursor
        // wandered. Auto-release, same as a click-to-release, once the cursor leaves the
        // interaction area's cross-axis extent (with 50% slack so a small overshoot doesn't
        // instantly drop the drag).
        double crossSpan = sb.vertical() ? sb.width() : sb.height();
        double crossCenter = sb.vertical() ? sb.offsetX() : sb.offsetY();
        double crossCursor = sb.vertical() ? drawnX : drawnY;
        if (crossSpan > 1.0e-6 && Math.abs(crossCursor - crossCenter) > crossSpan) {
            session.setDraggingScrollbarId(null);
            if (sb.onUnhover() != null && !sb.onUnhover().isBlank()) {
                runScriptRef(sb.onUnhover(), player, session, sb.id());
            }
            return;
        }

        double raw = span > 1.0e-6 ? (cursor - (center - span / 2.0)) / span : 0.0;
        double value = Math.max(0.0, Math.min(1.0, sb.vertical() ? 1.0 - raw : raw));

        double previous = session.scrollbarValues().getOrDefault(id, sb.value());
        if (Math.abs(value - previous) < 1.0e-3) return;
        session.scrollbarValues().put(id, value);
        if (sb.onChange() != null && !sb.onChange().isBlank()) {
            runScriptRefWithValue(sb.onChange(), player, session, id, value);
        }
    }

    /** Default per-tick of {@link #nudgeHoveredScrollbar} — matches Create's {@code ScrollInput}'s
     *  own default step size for "one wheel notch". */
    private static final double SCROLL_STEP = 0.05;

    /** Scroll-wheel adjustment (port of Create's {@code ScrollInput} mouse-wheel gesture) — an
     *  alternative to the click-drag gesture, not a replacement: hovering a
     *  {@link Widget.ScrollbarWidget} (no need to have armed/clicked it at all) and scrolling the
     *  mouse wheel nudges its value by {@link #SCROLL_STEP} per notch, clamped 0.0-1.0, firing
     *  {@code onChange} exactly like a drag does. No-op if the hovered widget isn't a scrollbar.
     *  Called from {@code VirtualUIScrollPacketListener} off the raw {@code HELD_ITEM_CHANGE}
     *  packet (the vanilla hotbar-scroll signal), which is cancelled so a VirtualUI session never
     *  actually changes the player's real held slot. Dispatches by whichever kind is currently
     *  hovered — a {@link Widget.ScrollbarWidget} gets nudged by {@link #SCROLL_STEP}, a
     *  {@link Widget.SelectWidget} advances one option per notch (via {@link #advanceSelect}); any
     *  other hovered kind (or nothing hovered) is a no-op. */
    public static void nudgeHoveredWidget(CameraSession session, Player player, int direction) {
        String id = session.hoveredWidgetId();
        if (id == null) return;
        Widget hovered = session.liveWidgets().get(id);
        if (hovered instanceof Widget.ScrollbarWidget sb) {
            double previous = session.scrollbarValues().getOrDefault(id, sb.value());
            double value = Math.max(0.0, Math.min(1.0, previous + direction * SCROLL_STEP));
            if (Math.abs(value - previous) < 1.0e-6) return;
            session.scrollbarValues().put(id, value);
            if (sb.onChange() != null && !sb.onChange().isBlank()) {
                runScriptRefWithValue(sb.onChange(), player, session, id, value);
            }
        } else if (hovered instanceof Widget.SelectWidget sel) {
            advanceSelect(session, player, sel, direction);
        }
    }

    /** Hit-tests against whatever cursor position is actually ON SCREEN right now — the predicted
     *  position when prediction is enabled, same as {@code VirtualUICameraSystem.renderTick} draws
     *  the glyph at, falling back to the raw position otherwise. This used to always read the RAW
     *  (unpredicted) position regardless of what was rendered — a deliberate choice at the time, but
     *  it meant a click could visibly land ON a widget (per the predicted glyph position) and still
     *  miss, because the hit-test itself was still looking at a lagging raw coordinate. Overlap
     *  between the cursor's OWN drawn position and a widget's bounds is what should decide a hit,
     *  not a stale/undrawn coordinate. */
    private static Widget resolveHit(CameraSession session) {
        double cx = VirtualUIConfig.get().predictionEnabled() ? session.predictedCursorX() : session.cursorX();
        double cy = VirtualUIConfig.get().predictionEnabled() ? session.predictedCursorY() : session.cursorY();
        Widget best = null;
        for (Widget w : session.liveWidgets().values()) {
            if (w.width() <= 0 && w.height() <= 0) continue;
            double halfW = w.width() / 2.0, halfH = w.height() / 2.0;
            if (cx < w.offsetX() - halfW || cx > w.offsetX() + halfW) continue;
            if (cy < w.offsetY() - halfH || cy > w.offsetY() + halfH) continue;
            if (best == null || w.priority() > best.priority()) best = w;
        }
        return best;
    }

    /** Runs a {@code "file.pf:func[:args]"} action ref against a fresh script context bound to
     *  the clicking player, with the widget id (and, for a {@code SlotWidget}, its slot index) as
     *  trailing arguments — same resolution {@code DialogManagerType} uses for its action buttons. */
    static void runScriptRef(String ref, Player player, CameraSession session, String widgetId) {
        if (ref == null || ref.isBlank()) return;
        try {
            ScriptCall call = ScriptCall.parse(ref);
            if (call == null) {
                CraftEnginePolyfills.instance().getLogger().warning(
                        "[VirtualUI][debug] ScriptCall.parse returned null for ref \"" + ref + "\"");
                return;
            }
            net.minecraft.server.level.ServerPlayer sp = ((CraftPlayer) player).getHandle();
            ScriptContext ctx = ScriptContext.builder().copyFrom(genericContext()).player(sp).build();
            List<ScriptValue> extra = new ArrayList<>();
            if (widgetId != null) extra.add(ScriptValue.of(widgetId));
            Widget w = session != null ? session.liveWidgets().get(widgetId) : null;
            if (w instanceof Widget.SlotWidget sw) extra.add(ScriptValue.of(sw.slotIndex()));
            call.executeWithExtraArgs(ctx, extra);
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .log(java.util.logging.Level.WARNING, "[VirtualUI] script action '" + ref + "' failed", t);
        }
    }

    /** Like {@link #runScriptRef}, plus a trailing numeric value — used by a scrollbar's
     *  {@code onChange(widget_id, value)}. */
    private static void runScriptRefWithValue(String ref, Player player, CameraSession session, String widgetId, double value) {
        if (ref == null || ref.isBlank()) return;
        try {
            ScriptCall call = ScriptCall.parse(ref);
            if (call == null) {
                CraftEnginePolyfills.instance().getLogger().warning(
                        "[VirtualUI][debug] ScriptCall.parse returned null for ref \"" + ref + "\"");
                return;
            }
            net.minecraft.server.level.ServerPlayer sp = ((CraftPlayer) player).getHandle();
            ScriptContext ctx = ScriptContext.builder().copyFrom(genericContext()).player(sp).build();
            call.executeWithExtraArgs(ctx, List.of(ScriptValue.of(widgetId), ScriptValue.of(value)));
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .log(java.util.logging.Level.WARNING, "[VirtualUI] scrollbar onChange '" + ref + "' failed", t);
        }
    }

    /** Like {@link #runScriptRef}, plus a trailing boolean value — used by a toggle's
     *  {@code onChange(widget_id, is_on)}. */
    private static void runScriptRefWithValue(String ref, Player player, CameraSession session, String widgetId, boolean value) {
        if (ref == null || ref.isBlank()) return;
        try {
            ScriptCall call = ScriptCall.parse(ref);
            if (call == null) {
                CraftEnginePolyfills.instance().getLogger().warning(
                        "[VirtualUI][debug] ScriptCall.parse returned null for ref \"" + ref + "\"");
                return;
            }
            net.minecraft.server.level.ServerPlayer sp = ((CraftPlayer) player).getHandle();
            ScriptContext ctx = ScriptContext.builder().copyFrom(genericContext()).player(sp).build();
            call.executeWithExtraArgs(ctx, List.of(ScriptValue.of(widgetId), ScriptValue.of(value)));
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .log(java.util.logging.Level.WARNING, "[VirtualUI] toggle onChange '" + ref + "' failed", t);
        }
    }

    /** Like {@link #runScriptRef}, plus the newly-selected option string AND its index — used by a
     *  select widget's {@code onChange(widget_id, option, index)}. */
    private static void runScriptRefWithSelection(String ref, Player player, CameraSession session, String widgetId, String option, int index) {
        if (ref == null || ref.isBlank()) return;
        try {
            ScriptCall call = ScriptCall.parse(ref);
            if (call == null) {
                CraftEnginePolyfills.instance().getLogger().warning(
                        "[VirtualUI][debug] ScriptCall.parse returned null for ref \"" + ref + "\"");
                return;
            }
            net.minecraft.server.level.ServerPlayer sp = ((CraftPlayer) player).getHandle();
            ScriptContext ctx = ScriptContext.builder().copyFrom(genericContext()).player(sp).build();
            call.executeWithExtraArgs(ctx, List.of(ScriptValue.of(widgetId), ScriptValue.of(option), ScriptValue.of((double) index)));
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .log(java.util.logging.Level.WARNING, "[VirtualUI] select onChange '" + ref + "' failed", t);
        }
    }

    private static ScriptContext genericContext() {
        ScriptContext.Builder b = ScriptContext.builder();
        b.typed("Server", dev.arubik.craftengine.script.types.world.ServerType.INSTANCE);
        b.typed("Item", dev.arubik.craftengine.script.types.primitive.ItemType.NAMESPACE);
        b.typed("Menu", dev.arubik.craftengine.script.types.menu.MenuType.INSTANCE);
        b.typed("EventManager", dev.arubik.craftengine.script.types.event.EventManagerType.INSTANCE);
        b.typed("TaskManager", dev.arubik.craftengine.script.types.util.TaskManagerType.INSTANCE);
        b.typed("VirtualUI", dev.arubik.craftengine.script.types.util.VirtualUIManagerType.INSTANCE);
        return b.build();
    }
}
