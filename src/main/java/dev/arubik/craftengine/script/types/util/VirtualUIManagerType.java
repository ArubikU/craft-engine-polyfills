package dev.arubik.craftengine.script.types.util;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.virtualui.DynamicFieldResolver;
import dev.arubik.craftengine.virtualui.VirtualUICameraSystem;
import dev.arubik.craftengine.virtualui.model.CameraSession;
import dev.arubik.craftengine.virtualui.model.HologramLineConfig;
import dev.arubik.craftengine.virtualui.model.ItemSource;
import dev.arubik.craftengine.virtualui.model.VirtualUIScreen;
import dev.arubik.craftengine.virtualui.model.Widget;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Port of  into the .pf script engine as a Forge/Fabric-flavoured screen builder — a
 * script lays out buttons, images, icons, items, interactive slots, and a player-bust render, and
 * {@code VirtualUI} handles the camera-lock + packet-display machinery underneath. Same
 * "manager singleton + builder + built value" shape as {@code DialogManagerType}, registering:
 *
 * <ul>
 *   <li>{@code "VirtualUI"} — the entry point: {@code VirtualUI.screen(title)} starts a builder;
 *       {@code VirtualUI.hide(player)}/{@code .is_open(player)}/{@code .change_hologram(...)}
 *       operate on an already-open session.
 *   <li>{@code "VirtualUIBuilder"} — mutable, chainable: one method per widget kind, each taking
 *       a plain {@code "file.pf:func[:args]"} script ref (or nothing) for its click/hover/unhover
 *       behavior — there is no built-in action vocabulary; a script that wants to open a URL,
 *       run a command, switch server, or change a hologram calls {@code Player}/{@code VirtualUI}
 *       itself from inside that ref.
 *   <li>{@code "BuiltVirtualUI"} — the finalized, showable screen: {@code .show(player)}.
 * </ul>
 */
public final class VirtualUIManagerType {

    /** Stateless singleton bound as the global "VirtualUI" — same convention as Dialog/Server. */
    public static final Object INSTANCE = new Object();

    private VirtualUIManagerType() {}

    /** Mutable builder state, re-wrapped as the same {@link Builder} instance on every chained
     *  call (see {@code DialogManagerType.Builder}'s identical rationale). */
    public static final class Builder {
        String title = "";
        final List<Widget> widgets = new ArrayList<>();
        double cameraDistance = 0; // 0 = use VirtualUIConfig's default
        boolean playerInvisible = true;
        String onCloseRef;
        int autoPriority = 0;
        double maxOffsetX = 0;   // 0 = use VirtualUIConfig's global default — "screen area" bounds
        double maxOffsetY = 0;
        HologramLineConfig cursorIcon;
        String cursorItemId;
        final Map<String, String> hoverCursorStates = new java.util.LinkedHashMap<>();
    }

    public static void register() {
        PolyTypeRegistry.define("VirtualUI")
                .method("screen", (obj, args) -> {
                    Builder b = new Builder();
                    if (!args.isEmpty()) b.title = args.get(0).asStr();
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                .method("hide", (obj, args) -> {
                    Player p = playerOf(args.isEmpty() ? null : args.get(0));
                    if (p == null) return ScriptValue.of(false);
                    VirtualUICameraSystem.hide(p);
                    return ScriptValue.of(true);
                })
                .method("is_open", (obj, args) -> {
                    Player p = playerOf(args.isEmpty() ? null : args.get(0));
                    return ScriptValue.of(p != null && VirtualUICameraSystem.isOpen(p));
                })
                // change_hologram(player, widget_id, text_or_style_map) — restyles/retexts an
                // already-shown ButtonWidget/LabelWidget in place. Accepts either a plain string
                // (keeps the widget's current styling, swaps only the text) or a "Hologram" value
                // built via .hologram(text)....build() below (full restyle).
                .method("change_hologram", (obj, args) -> {
                    if (args.size() < 3) return ScriptValue.of(false);
                    Player p = playerOf(args.get(0));
                    if (p == null) return ScriptValue.of(false);
                    CameraSession session = VirtualUICameraSystem.session(p);
                    if (session == null) return ScriptValue.of(false);
                    String widgetId = args.get(1).asStr();
                    HologramLineConfig content = resolveContent(args.get(2), session, widgetId);
                    if (content == null) return ScriptValue.of(false);
                    session.liveHolograms().put(widgetId, content);
                    return ScriptValue.of(true);
                })
                // show_tooltip(player, text, offset_x?, offset_y?) — a cursor-following floating
                // label (port of Create's TooltipArea), independent of any widget; typically called
                // from a widget's on_hover/on_unhover. offset_y defaults to 0.4 (just above the
                // cursor glyph). Plain MiniMessage text, not dynamic (no ${...}/".pf:" resolution —
                // call again with new text to change it).
                .method("show_tooltip", (obj, args) -> {
                    if (args.size() < 2) return ScriptValue.of(false);
                    Player p = playerOf(args.get(0));
                    if (p == null) return ScriptValue.of(false);
                    CameraSession session = VirtualUICameraSystem.session(p);
                    if (session == null) return ScriptValue.of(false);
                    double ox = args.size() > 2 ? args.get(2).asNum() : 0.0;
                    double oy = args.size() > 3 ? args.get(3).asNum() : 0.4;
                    session.showTooltip(args.get(1).asStr(), ox, oy);
                    return ScriptValue.of(true);
                })
                // set_progress(player, widget_id, value) — pushes a new 0.0-1.0 value onto an
                // already-open ProgressWidget (see Widget.ProgressWidget's doc) — the only way that
                // widget kind's fill level ever changes, since it has no click/drag gesture of its
                // own. Call it whenever the real state it represents changes (a machine tick, a
                // network update, ...); value is clamped into [0,1].
                .method("set_progress", (obj, args) -> {
                    if (args.size() < 3) return ScriptValue.of(false);
                    Player p = playerOf(args.get(0));
                    if (p == null) return ScriptValue.of(false);
                    CameraSession session = VirtualUICameraSystem.session(p);
                    if (session == null) return ScriptValue.of(false);
                    String widgetId = args.get(1).asStr();
                    double value = Math.max(0.0, Math.min(1.0, args.get(2).asNum()));
                    session.progressValues().put(widgetId, value);
                    return ScriptValue.of(true);
                })
                // hide_tooltip(player) — hides whatever show_tooltip(...) last showed for them.
                .method("hide_tooltip", (obj, args) -> {
                    if (args.isEmpty()) return ScriptValue.of(false);
                    Player p = playerOf(args.get(0));
                    if (p == null) return ScriptValue.of(false);
                    CameraSession session = VirtualUICameraSystem.session(p);
                    if (session == null) return ScriptValue.of(false);
                    session.hideTooltip();
                    return ScriptValue.of(true);
                })
                // hologram(text) — starts a standalone styled-text builder (offset/scale/rotation/
                // alignment/billboard/background/etc.), independent of any specific widget; its
                // .build() result can be fed straight into .button(id, hologram, ...) / .label(id,
                // hologram) below, or into change_hologram(...) above.
                .method("hologram", (obj, args) -> {
                    String text = args.isEmpty() ? "" : args.get(0).asStr();
                    return ScriptValue.ofObj("HologramBuilder", HologramLineConfig.text(text, 0, 0, 0, 0.8f));
                })
                // set_cursor_state(player, state) — forces the cursor-state provider (virtualui.yml's
                // cursor.states.<name>, e.g. "processing") regardless of hover/drag context, until
                // cleared. Lets a script show a busy/loading cursor around an async operation.
                .method("set_cursor_state", (obj, args) -> {
                    if (args.size() < 2) return ScriptValue.of(false);
                    Player p = playerOf(args.get(0));
                    if (p == null) return ScriptValue.of(false);
                    CameraSession session = VirtualUICameraSystem.session(p);
                    if (session == null) return ScriptValue.of(false);
                    String stateName = nullIfBlank(args.get(1).asStr());
                    session.setCursorStateOverride(stateName);
                    dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger().info(
                            "[VirtualUI][debug] set_cursor_state(" + p.getName() + ", \"" + stateName + "\")");
                    return ScriptValue.of(true);
                })
                // clear_cursor_state(player) — releases a set_cursor_state override, letting the
                // engine go back to picking the state from hover/drag context.
                .method("clear_cursor_state", (obj, args) -> {
                    Player p = playerOf(args.isEmpty() ? null : args.get(0));
                    if (p == null) return ScriptValue.of(false);
                    CameraSession session = VirtualUICameraSystem.session(p);
                    if (session == null) return ScriptValue.of(false);
                    session.setCursorStateOverride(null);
                    dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger().info(
                            "[VirtualUI][debug] clear_cursor_state(" + p.getName() + ")");
                    return ScriptValue.of(true);
                });

        registerHologramBuilder();

        PolyTypeRegistry.define("VirtualUIBuilder")
                .method("camera_distance", (obj, args) -> {
                    Builder b = builder(obj);
                    if (!args.isEmpty()) b.cameraDistance = args.get(0).asNum();
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                .method("player_invisible", (obj, args) -> {
                    Builder b = builder(obj);
                    b.playerInvisible = args.isEmpty() || args.get(0).asBool();
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                .method("on_close", (obj, args) -> {
                    Builder b = builder(obj);
                    if (!args.isEmpty()) b.onCloseRef = args.get(0).asStr();
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // bounds(max_x, max_y) — this screen's own "screen area": how far the cursor can
                // travel from center before clamping, overriding VirtualUIConfig's server-wide
                // default just for this UI (0/omitted = keep the global default).
                .method("bounds", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() >= 2) { b.maxOffsetX = args.get(0).asNum(); b.maxOffsetY = args.get(1).asNum(); }
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // cursor_icon(text_or_hologram) — overrides the global cursor glyph for this screen.
                .method("cursor_icon", (obj, args) -> {
                    Builder b = builder(obj);
                    if (!args.isEmpty()) b.cursorIcon = hologramOf(args.get(0), 0, 0);
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // cursor_item(item_id) — renders the cursor as a small item icon instead of text.
                .method("cursor_item", (obj, args) -> {
                    Builder b = builder(obj);
                    if (!args.isEmpty()) b.cursorItemId = nullIfBlank(args.get(0).asStr());
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // button(id, text_or_hologram, x, y, on_click?, width?, height?)
                .method("button", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 3) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    String id = args.get(0).asStr();
                    HologramLineConfig hologram = hologramOf(args.get(1), args.get(2).asNum(),
                            args.size() > 3 ? args.get(3).asNum() : 0.0);
                    String onClick = args.size() > 4 ? nullIfBlank(args.get(4).asStr()) : null;
                    double width = args.size() > 5 ? args.get(5).asNum() : 1.2;
                    double height = args.size() > 6 ? args.get(6).asNum() : 0.5;
                    b.widgets.add(new Widget.ButtonWidget(id, hologram, width, height, onClick, null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                .method("on_hover", (obj, args) -> replaceLastWidgetCallback(obj, args, 1));
        PolyTypeRegistry.extend("VirtualUIBuilder", t -> t
                .method("on_unhover", (obj, args) -> replaceLastWidgetCallback(obj, args, 2))
                // icon_of(item, offset_x?, offset_y?, scale?) — attaches an item icon to the
                // last-added .button(...), rendered as its own packet entity next to the text (see
                // Widget.ButtonWidget's doc). "item" is anything itemSourceOf(...) accepts: a bare
                // id, an already-built ScriptValue.Item, or a dynamic ".pf:"/"${...}" ref. Chain
                // immediately after .button(...), same as .on_hover(ref)/.on_unhover(ref).
                .method("icon_of", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.isEmpty() || b.widgets.isEmpty()) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    ItemSource icon = itemSourceOf(args.get(0));
                    double ox = args.size() > 1 ? args.get(1).asNum() : 0.0;
                    double oy = args.size() > 2 ? args.get(2).asNum() : 0.0;
                    float scale = args.size() > 3 ? (float) args.get(3).asNum() : 0.5f;
                    Widget last = b.widgets.get(b.widgets.size() - 1);
                    if (last instanceof Widget.ButtonWidget btn) {
                        b.widgets.set(b.widgets.size() - 1, new Widget.ButtonWidget(btn.id(), btn.hologram(), btn.width(), btn.height(),
                                icon, ox, oy, scale, btn.onClick(), btn.onHover(), btn.onUnhover(), btn.priority()));
                    } else if (last instanceof Widget.ToggleWidget tg) {
                        // Sets the ON-state icon only (the common "no icon when off" toggle look) —
                        // for an off-state icon too, reconstruct via .toggle(...) again isn't
                        // possible mid-chain, so scripts wanting both icons build the ToggleWidget
                        // fields directly isn't exposed; this covers the common case.
                        b.widgets.set(b.widgets.size() - 1, new Widget.ToggleWidget(tg.id(), tg.value(), tg.onLabel(), tg.offLabel(),
                                tg.width(), tg.height(), icon, tg.offIcon(), ox, oy, scale,
                                tg.onChange(), tg.onHover(), tg.onUnhover(), tg.priority()));
                    }
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // label(id, text_or_hologram, x, y) — non-interactive.
                .method("label", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 3) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    String id = args.get(0).asStr();
                    HologramLineConfig hologram = hologramOf(args.get(1), args.get(2).asNum(),
                            args.size() > 3 ? args.get(3).asNum() : 0.0);
                    b.widgets.add(new Widget.LabelWidget(id, hologram));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // image(id, item, x, y, width, height, on_click?, z?) — "item" per itemSourceOf(...).
                // Trailing "z" (default 0.0) offsets this image's DEPTH like .icon(...)'s own z param
                // — a large background/panel image (e.g. a ported Create GUI backdrop) typically wants
                // a negative z so every other widget renders visually in front of it.
                .method("image", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 6) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    b.widgets.add(new Widget.ImageWidget(args.get(0).asStr(), itemSourceOf(args.get(1)),
                            args.get(2).asNum(), args.get(3).asNum(),
                            args.size() > 7 ? args.get(7).asNum() : 0.0,
                            args.get(4).asNum(), args.get(5).asNum(), 0f,
                            args.size() > 6 ? nullIfBlank(args.get(6).asStr()) : null, null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // icon(id, item, x, y, scale?, on_click?) — "item" per itemSourceOf(...): a bare
                // id, a directly-built ScriptValue.Item (same convention Menu.set_item/Dialog.
                // body_item use), or a dynamic ".pf:"/"${...}" ref.
                // icon(id, item, x, y, scale?, on_click?, z?) — "z" (default 0.0) offsets this
                // icon's DEPTH relative to every other widget's own plane (more negative = further
                // away/behind) — the same axis screenPlaneZ already applies uniformly, just
                // per-widget now, so e.g. a plain background/frame icon can sit visually BEHIND
                // another widget placed at the same x/y with z=0, no new rendering concept needed.
                .method("icon", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 4) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    b.widgets.add(new Widget.IconWidget(args.get(0).asStr(), itemSourceOf(args.get(1)),
                            args.get(2).asNum(), args.get(3).asNum(), args.size() > 6 ? args.get(6).asNum() : 0.0,
                            args.size() > 4 ? (float) args.get(4).asNum() : 0.5f,
                            args.size() > 5 ? nullIfBlank(args.get(5).asStr()) : null, null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // item(id, item, x, y, scale?, on_click?) — same rendering as icon(), distinct
                // script-facing name (see Widget.ItemWidget's doc).
                .method("item", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 4) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    b.widgets.add(new Widget.ItemWidget(args.get(0).asStr(), itemSourceOf(args.get(1)),
                            args.get(2).asNum(), args.get(3).asNum(), 0.0,
                            args.size() > 4 ? (float) args.get(4).asNum() : 0.6f,
                            args.size() > 5 ? nullIfBlank(args.get(5).asStr()) : null, null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // slot(id, slot_index, item, x, y, width?, height?, on_click?) — an interactive
                // inventory-like slot; on_click's script receives the slot index as a trailing arg.
                .method("slot", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 5) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    b.widgets.add(new Widget.SlotWidget(args.get(0).asStr(), (int) args.get(1).asNum(), itemSourceOf(args.get(2)),
                            args.get(3).asNum(), args.get(4).asNum(), 0.0,
                            args.size() > 5 ? args.get(5).asNum() : 0.7, args.size() > 6 ? args.get(6).asNum() : 0.7,
                            args.size() > 7 ? nullIfBlank(args.get(7).asStr()) : null, null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // player_render(id, x, y, scale?, target_player?) — target_player omitted/NULL
                // renders the VIEWER's own face (resolved per-session, see CameraSession).
                .method("player_render", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 3) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    UUID target = args.size() > 4 ? uuidOf(args.get(4)) : null;
                    b.widgets.add(new Widget.PlayerRenderWidget(args.get(0).asStr(), target,
                            args.get(1).asNum(), args.get(2).asNum(), 0.0,
                            args.size() > 3 ? (float) args.get(3).asNum() : 1.5f, null, null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // block(id, block_id, x, y, scale?, on_click?) — a 3D block icon (see Widget.BlockWidget).
                .method("block", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 4) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    b.widgets.add(new Widget.BlockWidget(args.get(0).asStr(), args.get(1).asStr(),
                            args.get(2).asNum(), args.get(3).asNum(), 0.0,
                            args.size() > 4 ? (float) args.get(4).asNum() : 0.6f,
                            args.size() > 5 ? nullIfBlank(args.get(5).asStr()) : null, null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // scrollbar(id, vertical, x, y, width, height, initial_value?, on_change?,
                // tracker_item?, thumb_text?) — a draggable 0.0-1.0 control (see
                // Widget.ScrollbarWidget's doc for the click-to-arm/move/click-to-release gesture,
                // and for the tracker-item/font-image/generated-bar thumb rendering preference).
                // "tracker_item" is anything itemSourceOf(...) accepts. on_change(widget_id, value)
                // fires as the value moves while armed.
                .method("scrollbar", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 6) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    b.widgets.add(new Widget.ScrollbarWidget(args.get(0).asStr(), args.get(1).asBool(),
                            args.size() > 6 ? Math.max(0.0, Math.min(1.0, args.get(6).asNum())) : 0.0,
                            args.get(2).asNum(), args.get(3).asNum(), 0.0, args.get(4).asNum(), args.get(5).asNum(),
                            args.size() > 8 ? itemSourceOf(args.get(8)) : ItemSource.EMPTY,
                            args.size() > 9 ? nullIfBlank(args.get(9).asStr()) : null,
                            args.size() > 7 ? nullIfBlank(args.get(7).asStr()) : null, null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // toggle(id, on_text_or_hologram, off_text_or_hologram, x, y, initial_on?,
                // on_change?, width?, height?) — a stateful on/off control (see
                // Widget.ToggleWidget's doc): click flips state internally (no on_click), firing
                // on_change(widget_id, is_on). Pair with .icon_of(...) right after, same as .button.
                .method("toggle", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 5) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    String id = args.get(0).asStr();
                    double x = args.get(3).asNum(), y = args.get(4).asNum();
                    HologramLineConfig onLabel = hologramOf(args.get(1), x, y);
                    HologramLineConfig offLabel = hologramOf(args.get(2), x, y);
                    boolean initialOn = args.size() > 5 && args.get(5).asBool();
                    String onChange = args.size() > 6 ? nullIfBlank(args.get(6).asStr()) : null;
                    double width = args.size() > 7 ? args.get(7).asNum() : 1.2;
                    double height = args.size() > 8 ? args.get(8).asNum() : 0.5;
                    b.widgets.add(new Widget.ToggleWidget(id, initialOn, onLabel, offLabel, width, height,
                            ItemSource.EMPTY, ItemSource.EMPTY, 0, 0, 0.5f, onChange, null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // select(id, options_array, text_template, x, y, initial_index?, on_change?,
                // width?, height?) — a discrete option cycler (see Widget.SelectWidget's doc):
                // click OR scroll-wheel (hover, no click needed) advances through "options" (an
                // array of strings), wrapping at both ends. on_change(widget_id, option, index)
                // fires on every advance. "text_template" is the SAME dynamic ${expr}/".pf:" text
                // any other widget takes, evaluated against this widget's own VUISelect context —
                // nothing about the visual format is baked in, e.g.
                // "<white>◀ <yellow>${VUISelect.value()}</yellow> ▶".
                .method("select", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 5 || !(args.get(1) instanceof ScriptValue.Array arr)) {
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    }
                    String id = args.get(0).asStr();
                    java.util.List<String> options = new java.util.ArrayList<>();
                    for (ScriptValue v : arr.elements()) options.add(v.asStr());
                    double x = args.get(3).asNum(), y = args.get(4).asNum();
                    HologramLineConfig template = hologramOf(args.get(2), x, y);
                    int initialIndex = args.size() > 5 ? (int) args.get(5).asNum() : 0;
                    String onChange = args.size() > 6 ? nullIfBlank(args.get(6).asStr()) : null;
                    double width = args.size() > 7 ? args.get(7).asNum() : 1.4;
                    double height = args.size() > 8 ? args.get(8).asNum() : 0.5;
                    b.widgets.add(new Widget.SelectWidget(id, options, initialIndex, template,
                            width, height, onChange, null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // progress(id, vertical, x, y, width, height, initial_value?, tracker_item?) — a
                // non-interactive fill gauge (see Widget.ProgressWidget's doc): no click/drag at
                // all, "value" (0.0-1.0) is pushed later from a script via
                // VirtualUI.set_progress(player, widget_id, value) whenever the real state changes
                // (a machine's processing progress, a speed reading, ...). "tracker_item" is
                // anything itemSourceOf(...) accepts, rendered centered as a static needle/indicator
                // instead of the generated fill bar — same convention as scrollbar's own tracker.
                .method("progress", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() < 6) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    b.widgets.add(new Widget.ProgressWidget(args.get(0).asStr(),
                            args.size() > 6 ? Math.max(0.0, Math.min(1.0, args.get(6).asNum())) : 0.0,
                            args.get(2).asNum(), args.get(3).asNum(), 0.0, args.get(4).asNum(), args.get(5).asNum(),
                            args.get(1).asBool(),
                            args.size() > 7 ? itemSourceOf(args.get(7)) : ItemSource.EMPTY,
                            null, null, b.autoPriority++));
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                .method("priority", (obj, args) -> {
                    Builder b = builder(obj);
                    if (args.size() >= 2 && !b.widgets.isEmpty()) {
                        String id = args.get(0).asStr();
                        int newPriority = (int) args.get(1).asNum();
                        for (int i = 0; i < b.widgets.size(); i++) {
                            if (b.widgets.get(i).id().equals(id)) {
                                b.widgets.set(i, withPriority(b.widgets.get(i), newPriority));
                                break;
                            }
                        }
                    }
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                // hover_state(name) — chained right after a widget builder call (same convention as
                // .on_hover(ref)): forces THIS widget's hover to show cursor.states.<name> instead
                // of the generic "hover" glyph, e.g. ".button(...).hover_state('not_allowed')" for a
                // disabled-looking button, or ".select(...).hover_state('scroll_horizontal')" to hint
                // it also takes scroll-wheel input. No script/on_hover call needed for this alone.
                .method("hover_state", (obj, args) -> {
                    Builder b = builder(obj);
                    if (!args.isEmpty() && !b.widgets.isEmpty()) {
                        String id = b.widgets.get(b.widgets.size() - 1).id();
                        b.hoverCursorStates.put(id, args.get(0).asStr());
                    }
                    return ScriptValue.ofObj("VirtualUIBuilder", b);
                })
                .method("build", (obj, args) -> {
                    Builder b = builder(obj);
                    return ScriptValue.ofObj("BuiltVirtualUI",
                            VirtualUIScreen.of(b.title, b.widgets, b.cameraDistance, b.playerInvisible, b.onCloseRef,
                                    b.maxOffsetX, b.maxOffsetY, b.cursorIcon, b.cursorItemId, b.hoverCursorStates));
                }));

        PolyTypeRegistry.define("BuiltVirtualUI")
                .method("show", (obj, args) -> {
                    if (args.isEmpty()) return ScriptValue.of(false);
                    Player p = playerOf(args.get(0));
                    if (p == null || !(screenOf(obj) instanceof VirtualUIScreen screen)) return ScriptValue.of(false);
                    return ScriptValue.of(VirtualUICameraSystem.show(p, screen));
                });

        registerPlayerExtensions();
    }

    /** Generic {@code Player} capabilities a VirtualUI click script needs that don't belong to
     *  VirtualUI itself — extends the existing {@code "Player"} type (registered by
     *  {@code PlayerType}, which MUST run first — see {@code ScriptBootstrap}'s ordering) rather
     *  than inventing a VirtualUI-specific action vocabulary, so any script (not just a VirtualUI
     *  click callback) can open a link or switch server. */
    private static void registerPlayerExtensions() {
        PolyTypeRegistry.extend("Player", t -> t
                // open_url(url) — vanilla has no server-forced "open the browser" packet (that's a
                // deliberate client-security boundary); the standard workaround is a clickable chat
                // component, same as every other Minecraft plugin that offers this.
                .method("open_url", (obj, args) -> {
                    if (args.isEmpty()) return ScriptValue.of(false);
                    try {
                        String url = args.get(0).asStr();
                        org.bukkit.entity.Player bp = bukkitPlayerOf(obj);
                        if (bp == null) return ScriptValue.of(false);
                        net.kyori.adventure.text.Component msg = net.kyori.adventure.text.Component
                                .text(url)
                                .color(net.kyori.adventure.text.format.NamedTextColor.AQUA)
                                .decorate(net.kyori.adventure.text.format.TextDecoration.UNDERLINED)
                                .clickEvent(net.kyori.adventure.text.event.ClickEvent.openUrl(url))
                                .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                                        net.kyori.adventure.text.Component.text("Click to open")));
                        bp.sendMessage(msg);
                        return ScriptValue.of(true);
                    } catch (Throwable ex) { return ScriptValue.of(false); }
                })
                // switch_server(name) — BungeeCord/Velocity "Connect" plugin-message, standard
                // proxy-switch mechanism; a no-op (returns false) on a non-proxied standalone server.
                .method("switch_server", (obj, args) -> {
                    if (args.isEmpty()) return ScriptValue.of(false);
                    try {
                        org.bukkit.entity.Player bp = bukkitPlayerOf(obj);
                        if (bp == null) return ScriptValue.of(false);
                        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                        java.io.DataOutputStream data = new java.io.DataOutputStream(out);
                        data.writeUTF("Connect");
                        data.writeUTF(args.get(0).asStr());
                        bp.sendPluginMessage(dev.arubik.craftengine.CraftEnginePolyfills.instance(), "BungeeCord", out.toByteArray());
                        return ScriptValue.of(true);
                    } catch (Throwable ex) { return ScriptValue.of(false); }
                }));
    }

    private static org.bukkit.entity.Player bukkitPlayerOf(Object obj) {
        if (obj instanceof net.minecraft.world.entity.player.Player nms) {
            try { return (org.bukkit.entity.Player) nms.getBukkitEntity(); } catch (Throwable ignored) {}
        }
        return null;
    }

    private static void registerHologramBuilder() {
        PolyTypeRegistry.define("HologramBuilder")
                .method("at", (obj, args) -> {
                    HologramLineConfig c = config(obj);
                    if (args.size() >= 3) c = c.withOffset(args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum());
                    return ScriptValue.ofObj("HologramBuilder", c);
                })
                .method("scale", (obj, args) -> {
                    HologramLineConfig c = config(obj);
                    if (!args.isEmpty()) {
                        float s = (float) args.get(0).asNum();
                        c = new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), s,
                                c.rotationX(), c.rotationY(), c.rotationZ(), c.visible(), c.alignment(), c.lineWidth(),
                                c.opacity(), c.billboard(), c.seeThrough(), c.shadow(), c.backgroundR(), c.backgroundG(),
                                c.backgroundB(), c.backgroundA(), c.brightnessBlock(), c.brightnessSky());
                    }
                    return ScriptValue.ofObj("HologramBuilder", c);
                })
                .method("rotation", (obj, args) -> {
                    HologramLineConfig c = config(obj);
                    if (args.size() >= 3) {
                        c = new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), c.scale(),
                                (float) args.get(0).asNum(), (float) args.get(1).asNum(), (float) args.get(2).asNum(),
                                c.visible(), c.alignment(), c.lineWidth(), c.opacity(), c.billboard(), c.seeThrough(),
                                c.shadow(), c.backgroundR(), c.backgroundG(), c.backgroundB(), c.backgroundA(),
                                c.brightnessBlock(), c.brightnessSky());
                    }
                    return ScriptValue.ofObj("HologramBuilder", c);
                })
                .method("alignment", (obj, args) -> ScriptValue.ofObj("HologramBuilder",
                        args.isEmpty() ? config(obj) : withAlignment(config(obj), args.get(0).asStr())))
                .method("billboard", (obj, args) -> ScriptValue.ofObj("HologramBuilder",
                        args.isEmpty() ? config(obj) : withBillboard(config(obj), args.get(0).asStr())))
                .method("line_width", (obj, args) -> ScriptValue.ofObj("HologramBuilder",
                        args.isEmpty() ? config(obj) : withLineWidth(config(obj), (int) args.get(0).asNum())))
                .method("opacity", (obj, args) -> ScriptValue.ofObj("HologramBuilder",
                        args.isEmpty() ? config(obj) : withOpacity(config(obj), Math.min(254, (int) args.get(0).asNum()))))
                .method("see_through", (obj, args) -> ScriptValue.ofObj("HologramBuilder",
                        withSeeThrough(config(obj), args.isEmpty() || args.get(0).asBool())))
                .method("shadow", (obj, args) -> ScriptValue.ofObj("HologramBuilder",
                        withShadow(config(obj), args.isEmpty() || args.get(0).asBool())))
                .method("background", (obj, args) -> {
                    HologramLineConfig c = config(obj);
                    if (args.size() >= 4) {
                        c = new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), c.scale(),
                                c.rotationX(), c.rotationY(), c.rotationZ(), c.visible(), c.alignment(), c.lineWidth(),
                                c.opacity(), c.billboard(), c.seeThrough(), c.shadow(),
                                (int) args.get(0).asNum(), (int) args.get(1).asNum(), (int) args.get(2).asNum(), (int) args.get(3).asNum(),
                                c.brightnessBlock(), c.brightnessSky());
                    }
                    return ScriptValue.ofObj("HologramBuilder", c);
                })
                .method("build", (obj, args) -> ScriptValue.ofObj("Hologram", config(obj)));
    }

    // --- helpers ---

    private static ScriptValue replaceLastWidgetCallback(Object obj, List<ScriptValue> args, int which) {
        Builder b = builder(obj);
        if (!args.isEmpty() && !b.widgets.isEmpty()) {
            int i = b.widgets.size() - 1;
            b.widgets.set(i, withCallback(b.widgets.get(i), which, args.get(0).asStr()));
        }
        return ScriptValue.ofObj("VirtualUIBuilder", b);
    }

    /** Rebuilds the last-added widget with one of its three callbacks replaced (0=click,
     *  1=hover, 2=unhover) — used by {@code .on_hover(ref)}/{@code .on_unhover(ref)}, which apply
     *  to whichever widget method was called immediately before them in the chain. */
    private static Widget withCallback(Widget w, int which, String ref) {
        return switch (w) {
            case Widget.ButtonWidget x -> new Widget.ButtonWidget(x.id(), x.hologram(), x.width(), x.height(),
                    x.icon(), x.iconOffsetX(), x.iconOffsetY(), x.iconScale(),
                    which == 0 ? ref : x.onClick(), which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            case Widget.ImageWidget x -> new Widget.ImageWidget(x.id(), x.item(), x.offsetX(), x.offsetY(), x.offsetZ(),
                    x.width(), x.height(), x.rotationZ(),
                    which == 0 ? ref : x.onClick(), which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            case Widget.IconWidget x -> new Widget.IconWidget(x.id(), x.item(), x.offsetX(), x.offsetY(), x.offsetZ(),
                    x.scale(), which == 0 ? ref : x.onClick(), which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            case Widget.ItemWidget x -> new Widget.ItemWidget(x.id(), x.item(), x.offsetX(), x.offsetY(), x.offsetZ(),
                    x.scale(), which == 0 ? ref : x.onClick(), which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            case Widget.SlotWidget x -> new Widget.SlotWidget(x.id(), x.slotIndex(), x.item(), x.offsetX(), x.offsetY(), x.offsetZ(),
                    x.width(), x.height(), which == 0 ? ref : x.onClick(), which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            case Widget.PlayerRenderWidget x -> new Widget.PlayerRenderWidget(x.id(), x.targetPlayer(), x.offsetX(), x.offsetY(), x.offsetZ(),
                    x.scale(), which == 0 ? ref : x.onClick(), which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            case Widget.BlockWidget x -> new Widget.BlockWidget(x.id(), x.blockId(), x.offsetX(), x.offsetY(), x.offsetZ(),
                    x.scale(), which == 0 ? ref : x.onClick(), which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            // Scrollbar has no onClick (click toggles drag internally, not scriptable) — "which==1"
            // (on_hover) still applies to its armed-callback, "which==2" to its released-callback.
            case Widget.ScrollbarWidget x -> new Widget.ScrollbarWidget(x.id(), x.vertical(), x.value(), x.offsetX(), x.offsetY(), x.offsetZ(),
                    x.width(), x.height(), x.tracker(), x.thumbText(),
                    x.onChange(), which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            // Toggle has no onClick either (click flips state internally) — same shape as Scrollbar.
            case Widget.ToggleWidget x -> new Widget.ToggleWidget(x.id(), x.value(), x.onLabel(), x.offLabel(),
                    x.width(), x.height(), x.onIcon(), x.offIcon(), x.iconOffsetX(), x.iconOffsetY(), x.iconScale(),
                    x.onChange(), which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            // Select has no onClick either (click advances the option internally) — same shape.
            case Widget.SelectWidget x -> new Widget.SelectWidget(x.id(), x.options(), x.index(), x.template(),
                    x.width(), x.height(),
                    x.onChange(), which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            // Progress has no onClick/onChange at all (script-pushed only) — same shape as Scrollbar.
            case Widget.ProgressWidget x -> new Widget.ProgressWidget(x.id(), x.value(), x.offsetX(), x.offsetY(), x.offsetZ(),
                    x.width(), x.height(), x.vertical(), x.tracker(),
                    which == 1 ? ref : x.onHover(), which == 2 ? ref : x.onUnhover(), x.priority());
            case Widget.LabelWidget x -> x; // non-interactive, ignore
        };
    }

    private static Widget withPriority(Widget w, int p) {
        return switch (w) {
            case Widget.ButtonWidget x -> new Widget.ButtonWidget(x.id(), x.hologram(), x.width(), x.height(),
                    x.icon(), x.iconOffsetX(), x.iconOffsetY(), x.iconScale(), x.onClick(), x.onHover(), x.onUnhover(), p);
            case Widget.ImageWidget x -> new Widget.ImageWidget(x.id(), x.item(), x.offsetX(), x.offsetY(), x.offsetZ(), x.width(), x.height(), x.rotationZ(), x.onClick(), x.onHover(), x.onUnhover(), p);
            case Widget.IconWidget x -> new Widget.IconWidget(x.id(), x.item(), x.offsetX(), x.offsetY(), x.offsetZ(), x.scale(), x.onClick(), x.onHover(), x.onUnhover(), p);
            case Widget.ItemWidget x -> new Widget.ItemWidget(x.id(), x.item(), x.offsetX(), x.offsetY(), x.offsetZ(), x.scale(), x.onClick(), x.onHover(), x.onUnhover(), p);
            case Widget.SlotWidget x -> new Widget.SlotWidget(x.id(), x.slotIndex(), x.item(), x.offsetX(), x.offsetY(), x.offsetZ(), x.width(), x.height(), x.onClick(), x.onHover(), x.onUnhover(), p);
            case Widget.PlayerRenderWidget x -> new Widget.PlayerRenderWidget(x.id(), x.targetPlayer(), x.offsetX(), x.offsetY(), x.offsetZ(), x.scale(), x.onClick(), x.onHover(), x.onUnhover(), p);
            case Widget.BlockWidget x -> new Widget.BlockWidget(x.id(), x.blockId(), x.offsetX(), x.offsetY(), x.offsetZ(), x.scale(), x.onClick(), x.onHover(), x.onUnhover(), p);
            case Widget.ScrollbarWidget x -> new Widget.ScrollbarWidget(x.id(), x.vertical(), x.value(), x.offsetX(), x.offsetY(), x.offsetZ(), x.width(), x.height(), x.tracker(), x.thumbText(), x.onChange(), x.onHover(), x.onUnhover(), p);
            case Widget.ToggleWidget x -> new Widget.ToggleWidget(x.id(), x.value(), x.onLabel(), x.offLabel(), x.width(), x.height(), x.onIcon(), x.offIcon(), x.iconOffsetX(), x.iconOffsetY(), x.iconScale(), x.onChange(), x.onHover(), x.onUnhover(), p);
            case Widget.SelectWidget x -> new Widget.SelectWidget(x.id(), x.options(), x.index(), x.template(), x.width(), x.height(), x.onChange(), x.onHover(), x.onUnhover(), p);
            case Widget.ProgressWidget x -> new Widget.ProgressWidget(x.id(), x.value(), x.offsetX(), x.offsetY(), x.offsetZ(), x.width(), x.height(), x.vertical(), x.tracker(), x.onHover(), x.onUnhover(), p);
            case Widget.LabelWidget x -> x;
        };
    }

    /** Builds an {@link ItemSource} from any script value a widget's item field accepts: an
     *  already-built item (same convention {@code Menu.set_item}/{@code Dialog.body_item} use), a
     *  dynamic {@code ".pf:"}/{@code "${...}"} ref (see {@link DynamicFieldResolver#isDynamic}),
     *  or a plain literal id. */
    private static ItemSource itemSourceOf(ScriptValue value) {
        if (value instanceof ScriptValue.Item it) return ItemSource.ofStack(it.stack());
        if (value instanceof ScriptValue.Str s) {
            return DynamicFieldResolver.isDynamic(s.value()) ? ItemSource.ofScript(s.value()) : ItemSource.ofId(s.value());
        }
        return ItemSource.EMPTY;
    }

    private static HologramLineConfig hologramOf(ScriptValue value, double x, double y) {
        HologramLineConfig base = value instanceof ScriptValue.Obj o && o.instance() instanceof HologramLineConfig hc
                ? hc : HologramLineConfig.text(value.asStr(), 0, 0, 0, 0.8f);
        return base.withOffset(x, y, base.offsetZ());
    }

    private static HologramLineConfig resolveContent(ScriptValue value, CameraSession session, String widgetId) {
        if (value instanceof ScriptValue.Obj o && o.instance() instanceof HologramLineConfig hc) return hc;
        if (value instanceof ScriptValue.Str) {
            HologramLineConfig current = session.liveHolograms().get(widgetId);
            if (current == null) current = widgetHologramOf(session, widgetId);
            if (current == null) current = HologramLineConfig.text("", 0, 0, 0, 0.8f);
            return current.withText(value.asStr());
        }
        return null;
    }

    private static HologramLineConfig widgetHologramOf(CameraSession session, String id) {
        Widget w = session.liveWidgets().get(id);
        return w instanceof Widget.ButtonWidget b ? b.hologram() : w instanceof Widget.LabelWidget l ? l.hologram() : null;
    }

    private static HologramLineConfig withAlignment(HologramLineConfig c, String v) {
        return new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), c.scale(), c.rotationX(), c.rotationY(),
                c.rotationZ(), c.visible(), v, c.lineWidth(), c.opacity(), c.billboard(), c.seeThrough(), c.shadow(),
                c.backgroundR(), c.backgroundG(), c.backgroundB(), c.backgroundA(), c.brightnessBlock(), c.brightnessSky());
    }

    private static HologramLineConfig withBillboard(HologramLineConfig c, String v) {
        return new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), c.scale(), c.rotationX(), c.rotationY(),
                c.rotationZ(), c.visible(), c.alignment(), c.lineWidth(), c.opacity(), v, c.seeThrough(), c.shadow(),
                c.backgroundR(), c.backgroundG(), c.backgroundB(), c.backgroundA(), c.brightnessBlock(), c.brightnessSky());
    }

    private static HologramLineConfig withLineWidth(HologramLineConfig c, int v) {
        return new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), c.scale(), c.rotationX(), c.rotationY(),
                c.rotationZ(), c.visible(), c.alignment(), v, c.opacity(), c.billboard(), c.seeThrough(), c.shadow(),
                c.backgroundR(), c.backgroundG(), c.backgroundB(), c.backgroundA(), c.brightnessBlock(), c.brightnessSky());
    }

    private static HologramLineConfig withOpacity(HologramLineConfig c, int v) {
        return new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), c.scale(), c.rotationX(), c.rotationY(),
                c.rotationZ(), c.visible(), c.alignment(), c.lineWidth(), v, c.billboard(), c.seeThrough(), c.shadow(),
                c.backgroundR(), c.backgroundG(), c.backgroundB(), c.backgroundA(), c.brightnessBlock(), c.brightnessSky());
    }

    private static HologramLineConfig withSeeThrough(HologramLineConfig c, boolean v) {
        return new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), c.scale(), c.rotationX(), c.rotationY(),
                c.rotationZ(), c.visible(), c.alignment(), c.lineWidth(), c.opacity(), c.billboard(), v, c.shadow(),
                c.backgroundR(), c.backgroundG(), c.backgroundB(), c.backgroundA(), c.brightnessBlock(), c.brightnessSky());
    }

    private static HologramLineConfig withShadow(HologramLineConfig c, boolean v) {
        return new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), c.scale(), c.rotationX(), c.rotationY(),
                c.rotationZ(), c.visible(), c.alignment(), c.lineWidth(), c.opacity(), c.billboard(), c.seeThrough(), v,
                c.backgroundR(), c.backgroundG(), c.backgroundB(), c.backgroundA(), c.brightnessBlock(), c.brightnessSky());
    }

    private static String nullIfBlank(String s) { return s == null || s.isBlank() ? null : s; }

    private static Builder builder(Object obj) { return (Builder) obj; }
    private static HologramLineConfig config(Object obj) { return (HologramLineConfig) obj; }
    private static Object screenOf(Object obj) { return obj; }

    private static Player playerOf(ScriptValue v) {
        if (v == null) return null;
        if (v instanceof ScriptValue.Obj o && o.instance() instanceof net.minecraft.world.entity.player.Player nms) {
            try { return (Player) nms.getBukkitEntity(); } catch (Throwable ignored) {}
        }
        return null;
    }

    private static UUID uuidOf(ScriptValue v) {
        Player p = playerOf(v);
        if (p != null) return p.getUniqueId();
        if (v instanceof ScriptValue.Str s) { try { return UUID.fromString(s.value()); } catch (Throwable ignored) {} }
        return null;
    }
}
