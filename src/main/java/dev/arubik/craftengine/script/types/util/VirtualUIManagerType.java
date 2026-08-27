package dev.arubik.craftengine.script.types.util;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
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
                // screen(title?) — migrated with methodTypedOpt1, NOT methodTyped1: the handler must
                // still RUN with args empty (a fresh mutable Builder per call — a fixed
                // onMissingArgs value would hand every "no title" caller the SAME Builder instance
                // and leak state between them). Opt1 always runs the body, so each call constructs
                // its own Builder; default "" is exactly the field's initial value, so
                // "args empty -> title stays \"\"" is reproduced verbatim.
                .methodTypedOpt1("screen", TypeCodecs.STRING, "", TypeCodecs.RAW,
                    (Object obj, String title) -> {
                        Builder b = new Builder();
                        b.title = title;
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    })
                // hide/is_open: migrated to the typed-registration API. The single arg is a
                // ScriptValue passthrough (TypeCodecs.RAW) — playerOf(...) needs the raw
                // ScriptValue to unwrap an NMS Player, there's no dedicated TypeCodec for that.
                // onMissingArgs=false is exact: playerOf(null) is always null regardless of
                // instance, so the original "args empty -> p==null -> false" path is
                // instance-independent and safely fixed at registration time.
                .methodTyped1("hide", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                    (Object obj, ScriptValue arg0) -> {
                        Player p = playerOf(arg0);
                        if (p == null) return false;
                        VirtualUICameraSystem.hide(p);
                        return true;
                    })
                .methodTyped1("is_open", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                    (Object obj, ScriptValue arg0) -> {
                        Player p = playerOf(arg0);
                        return p != null && VirtualUICameraSystem.isOpen(p);
                    })
                // change_hologram(player, widget_id, text_or_style_map) — restyles/retexts an
                // already-shown ButtonWidget/LabelWidget in place. Accepts either a plain string
                // (keeps the widget's current styling, swaps only the text) or a "Hologram" value
                // built via .hologram(text)....build() below (full restyle).
                // Migrated to the typed-registration API: player and content are dynamic
                // (TypeCodecs.RAW — playerOf/resolveContent need the raw ScriptValue), widget_id
                // is a real string. onMissingArgs=false matches the original's "args.size()<3"
                // short-circuit, which is instance-independent (VirtualUI's obj is the stateless
                // singleton, unused).
                .methodTyped3("change_hologram", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.RAW,
                    TypeCodecs.BOOL, false,
                    (Object obj, ScriptValue playerArg, String widgetId, ScriptValue contentArg) -> {
                        Player p = playerOf(playerArg);
                        if (p == null) return false;
                        CameraSession session = VirtualUICameraSystem.session(p);
                        if (session == null) return false;
                        HologramLineConfig content = resolveContent(contentArg, session, widgetId);
                        if (content == null) return false;
                        session.liveHolograms().put(widgetId, content);
                        return true;
                    })
                // show_tooltip(player, text, offset_x?, offset_y?) — a cursor-following floating
                // label (port of Create's TooltipArea), independent of any widget; typically called
                // from a widget's on_hover/on_unhover. offset_y defaults to 0.4 (just above the
                // cursor glyph). Plain MiniMessage text, not dynamic (no ${...}/".pf:" resolution —
                // call again with new text to change it).
                // Migrated with methodTypedOpt4 — every slot optional, each with its own default,
                // which is exactly what the original's per-arg "args.size()>N ? ... : default"
                // reads did (offset_x 0.0 / offset_y 0.4). The two REQUIRED slots are expressed as
                // null-sentinel defaults: TypeCodecs.STRING/RAW never decode a present argument to
                // Java null (ScriptValue.asStr() always returns a string, and an args-list element
                // is never null), so "text == null" is precisely the original "args.size() < 2"
                // short-circuit — and a 1-arg call can't reach playerOf/showTooltip, matching the
                // original's check order (both paths return false with no side effect anyway).
                .methodTypedOpt4("show_tooltip", TypeCodecs.RAW, (ScriptValue) null,
                    TypeCodecs.STRING, (String) null, TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, 0.4,
                    TypeCodecs.BOOL,
                    (Object obj, ScriptValue playerArg, String text, Double ox, Double oy) -> {
                        if (text == null) return false;
                        Player p = playerOf(playerArg);
                        if (p == null) return false;
                        CameraSession session = VirtualUICameraSystem.session(p);
                        if (session == null) return false;
                        session.showTooltip(text, ox, oy);
                        return true;
                    })
                // set_progress(player, widget_id, value) — pushes a new 0.0-1.0 value onto an
                // already-open ProgressWidget (see Widget.ProgressWidget's doc) — the only way that
                // widget kind's fill level ever changes, since it has no click/drag gesture of its
                // own. Call it whenever the real state it represents changes (a machine tick, a
                // network update, ...); value is clamped into [0,1].
                // Migrated to the typed-registration API: player is TypeCodecs.RAW (playerOf
                // needs the raw ScriptValue), widget_id is a real string, value a real double
                // (clamp kept inside the body unchanged). onMissingArgs=false matches the
                // original's "args.size()<3" short-circuit (instance-independent — VirtualUI's
                // obj is the stateless singleton).
                .methodTyped3("set_progress", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.DOUBLE,
                    TypeCodecs.BOOL, false,
                    (Object obj, ScriptValue playerArg, String widgetId, Double valueArg) -> {
                        Player p = playerOf(playerArg);
                        if (p == null) return false;
                        CameraSession session = VirtualUICameraSystem.session(p);
                        if (session == null) return false;
                        double value = Math.max(0.0, Math.min(1.0, valueArg));
                        session.progressValues().put(widgetId, value);
                        return true;
                    })
                // hide_tooltip(player) — hides whatever show_tooltip(...) last showed for them.
                // Migrated: same RAW-player / onMissingArgs=false rationale as hide/is_open above.
                .methodTyped1("hide_tooltip", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                    (Object obj, ScriptValue playerArg) -> {
                        Player p = playerOf(playerArg);
                        if (p == null) return false;
                        CameraSession session = VirtualUICameraSystem.session(p);
                        if (session == null) return false;
                        session.hideTooltip();
                        return true;
                    })
                // hologram(text) — starts a standalone styled-text builder (offset/scale/rotation/
                // alignment/billboard/background/etc.), independent of any specific widget; its
                // .build() result can be fed straight into .button(id, hologram, ...) / .label(id,
                // hologram) below, or into change_hologram(...) above.
                // Migrated to the typed-registration API. onMissingArgs is a single fixed
                // ScriptValue computed once at registration — safe here only because
                // HologramLineConfig is an immutable record, so every "args empty" call sharing
                // that one instance is behaviorally identical to constructing
                // HologramLineConfig.text("", 0, 0, 0, 0.8f) fresh each time (unlike e.g. "screen"
                // below, whose Builder is mutable and must stay untyped for this exact reason).
                .methodTyped1("hologram", TypeCodecs.STRING, TypeCodecs.RAW,
                    ScriptValue.ofObj("HologramBuilder", HologramLineConfig.text("", 0, 0, 0, 0.8f)),
                    (Object obj, String text) -> ScriptValue.ofObj("HologramBuilder",
                            HologramLineConfig.text(text, 0, 0, 0, 0.8f)))
                // set_cursor_state(player, state) — forces the cursor-state provider (virtualui.yml's
                // cursor.states.<name>, e.g. "processing") regardless of hover/drag context, until
                // cleared. Lets a script show a busy/loading cursor around an async operation.
                // Migrated: player RAW, state_name a real string. onMissingArgs=false matches
                // "args.size()<2" (instance-independent — stateless VirtualUI singleton).
                .methodTyped2("set_cursor_state", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                    (Object obj, ScriptValue playerArg, String stateNameArg) -> {
                        Player p = playerOf(playerArg);
                        if (p == null) return false;
                        CameraSession session = VirtualUICameraSystem.session(p);
                        if (session == null) return false;
                        String stateName = nullIfBlank(stateNameArg);
                        session.setCursorStateOverride(stateName);
                        dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger().info(
                                "[VirtualUI][debug] set_cursor_state(" + p.getName() + ", \"" + stateName + "\")");
                        return true;
                    })
                // clear_cursor_state(player) — releases a set_cursor_state override, letting the
                // engine go back to picking the state from hover/drag context.
                // Migrated: same RAW-player / onMissingArgs=false rationale as hide/is_open above.
                .methodTyped1("clear_cursor_state", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                    (Object obj, ScriptValue playerArg) -> {
                        Player p = playerOf(playerArg);
                        if (p == null) return false;
                        CameraSession session = VirtualUICameraSystem.session(p);
                        if (session == null) return false;
                        session.setCursorStateOverride(null);
                        dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger().info(
                                "[VirtualUI][debug] clear_cursor_state(" + p.getName() + ")");
                        return true;
                    });

        registerHologramBuilder();

        // NOTE on the VirtualUIBuilder / HologramBuilder chains below. Every one of these is a
        // "chainable setter": with an arg missing/insufficient the body still returns
        // ScriptValue.ofObj(..., THIS CALL's own instance) unchanged. methodTypedN's onMissingArgs
        // (one fixed value computed once at registration, with no access to `instance`) structurally
        // cannot express that — but methodTypedOptN CAN: it always runs the handler, so the body
        // still receives its own instance and still returns it wrapped. Two conventions used
        // throughout below:
        //
        //  * A slot whose absence must mean "don't touch the field" (as opposed to "use a default
        //    value") is declared with a NULL default. That is a sound sentinel because none of the
        //    TypeCodecs ever decodes a PRESENT argument to Java null — asStr()/asNum()/asBool()
        //    always produce a value (asStr() yields "null" for a NULL ScriptValue, never null), and
        //    an args-list element is itself never null. So "param == null" is exactly the original
        //    "this arg wasn't supplied", and the JIT's native fast path (primitive double/boolean,
        //    Double.valueOf/Boolean.valueOf) can never manufacture one either.
        //  * A required-prefix + optional-tail shape (label/player_render/icon_of) declares the
        //    required slots with null defaults and null-checks the LAST required one — positional
        //    args mean "arg k present" implies every earlier one is too, so that single check is
        //    equivalent to the original's args.size() < k test.
        //
        // Still left untyped below: button/image/icon/item/slot/block/scrollbar/toggle/select/
        // progress — every one has MORE than 5 optional slots, and methodTypedOpt only goes up to
        // methodTypedOpt5 (methodTyped4..7 don't help: they'd force ALL args required and reject
        // the legal short call shapes).
        PolyTypeRegistry.define("VirtualUIBuilder")
                .methodTypedOpt1("camera_distance", TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW,
                    (Builder b, Double distance) -> {
                        if (distance != null) b.cameraDistance = distance;
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    })
                // Default TRUE reproduces the original's "args.isEmpty() || args.get(0).asBool()"
                // exactly — here the missing-arg case really does mean a value, not "don't touch".
                .methodTypedOpt1("player_invisible", TypeCodecs.BOOL, true, TypeCodecs.RAW,
                    (Builder b, Boolean invisible) -> {
                        b.playerInvisible = invisible;
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    })
                .methodTypedOpt1("on_close", TypeCodecs.STRING, (String) null, TypeCodecs.RAW,
                    (Builder b, String ref) -> {
                        if (ref != null) b.onCloseRef = ref;
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    })
                // bounds(max_x, max_y) — this screen's own "screen area": how far the cursor can
                // travel from center before clamping, overriding VirtualUIConfig's server-wide
                // default just for this UI (0/omitted = keep the global default).
                // maxY != null is exactly the original's args.size() >= 2.
                .methodTypedOpt2("bounds", TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null,
                    TypeCodecs.RAW,
                    (Builder b, Double maxX, Double maxY) -> {
                        if (maxY != null) { b.maxOffsetX = maxX; b.maxOffsetY = maxY; }
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    })
                // cursor_icon(text_or_hologram) — overrides the global cursor glyph for this screen.
                // RAW: hologramOf(...) needs the raw ScriptValue (it accepts either a built
                // Hologram obj or a plain string).
                .methodTypedOpt1("cursor_icon", TypeCodecs.RAW, (ScriptValue) null, TypeCodecs.RAW,
                    (Builder b, ScriptValue icon) -> {
                        if (icon != null) b.cursorIcon = hologramOf(icon, 0, 0);
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    })
                // cursor_item(item_id) — renders the cursor as a small item icon instead of text.
                .methodTypedOpt1("cursor_item", TypeCodecs.STRING, (String) null, TypeCodecs.RAW,
                    (Builder b, String itemId) -> {
                        if (itemId != null) b.cursorItemId = nullIfBlank(itemId);
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    })
                // button(id, text_or_hologram, x, y, on_click?, width?, height?)
                // Left untyped: 7 optional slots, past methodTypedOpt5's maximum arity.
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
                .methodTypedOpt1("on_hover", TypeCodecs.STRING, (String) null, TypeCodecs.RAW,
                    (Builder b, String ref) -> replaceLastWidgetCallback(b, ref, 1));
        PolyTypeRegistry.extend("VirtualUIBuilder", t -> t
                .methodTypedOpt1("on_unhover", TypeCodecs.STRING, (String) null, TypeCodecs.RAW,
                    (Builder b, String ref) -> replaceLastWidgetCallback(b, ref, 2))
                // icon_of(item, offset_x?, offset_y?, scale?) — attaches an item icon to the
                // last-added .button(...), rendered as its own packet entity next to the text (see
                // Widget.ButtonWidget's doc). "item" is anything itemSourceOf(...) accepts: a bare
                // id, an already-built ScriptValue.Item, or a dynamic ".pf:"/"${...}" ref. Chain
                // immediately after .button(...), same as .on_hover(ref)/.on_unhover(ref).
                // Migrated: 4 optional slots. The item slot stays RAW (itemSourceOf needs the raw
                // ScriptValue — id string / built Item / dynamic ref) with a null default standing
                // in for the original's args.isEmpty(); ox/oy/scale keep their own defaults.
                .methodTypedOpt4("icon_of", TypeCodecs.RAW, (ScriptValue) null,
                    TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, 0.0, TypeCodecs.DOUBLE, 0.5,
                    TypeCodecs.RAW,
                    (Builder b, ScriptValue itemArg, Double ox, Double oy, Double scaleArg) -> {
                        if (itemArg == null || b.widgets.isEmpty()) return ScriptValue.ofObj("VirtualUIBuilder", b);
                    ItemSource icon = itemSourceOf(itemArg);
                    float scale = (float) scaleArg.doubleValue();
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
                // Migrated: id/text/x are the required prefix (null defaults; "x == null" is the
                // original's args.size() < 3), y the optional tail with its own 0.0 default.
                .methodTypedOpt4("label", TypeCodecs.STRING, (String) null, TypeCodecs.RAW, (ScriptValue) null,
                    TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, 0.0, TypeCodecs.RAW,
                    (Builder b, String id, ScriptValue text, Double x, Double y) -> {
                        if (x == null) return ScriptValue.ofObj("VirtualUIBuilder", b);
                        HologramLineConfig hologram = hologramOf(text, x, y);
                        b.widgets.add(new Widget.LabelWidget(id, hologram));
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    })
                // image(id, item, x, y, width, height, on_click?, z?) — "item" per itemSourceOf(...).
                // Trailing "z" (default 0.0) offsets this image's DEPTH like .icon(...)'s own z param
                // — a large background/panel image (e.g. a ported Create GUI backdrop) typically wants
                // a negative z so every other widget renders visually in front of it.
                // Left untyped: 8 optional slots, past methodTypedOpt5's maximum arity.
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
                // Left untyped: 7 optional slots, past methodTypedOpt5's maximum arity.
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
                // Left untyped: 6 optional slots, past methodTypedOpt5's maximum arity.
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
                // Left untyped: 8 optional slots, past methodTypedOpt5's maximum arity.
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
                // Migrated: exactly 5 slots — id/x/y required (null defaults; "y == null" is the
                // original's args.size() < 3), scale optional with its own 1.5f default, and
                // target_player RAW (uuidOf accepts a Player obj or a uuid string, and already
                // returns null for a null/absent value, so the original's "args.size() > 4 ?
                // uuidOf(...) : null" collapses to a plain uuidOf call).
                .methodTypedOpt5("player_render", TypeCodecs.STRING, (String) null,
                    TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null,
                    TypeCodecs.DOUBLE, 1.5, TypeCodecs.RAW, (ScriptValue) null, TypeCodecs.RAW,
                    (Builder b, String id, Double x, Double y, Double scale, ScriptValue targetArg) -> {
                        if (y == null) return ScriptValue.ofObj("VirtualUIBuilder", b);
                        UUID target = uuidOf(targetArg);
                        b.widgets.add(new Widget.PlayerRenderWidget(id, target, x, y, 0.0,
                                (float) scale.doubleValue(), null, null, null, b.autoPriority++));
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    })
                // block(id, block_id, x, y, scale?, on_click?) — a 3D block icon (see Widget.BlockWidget).
                // Left untyped: 6 optional slots, past methodTypedOpt5's maximum arity.
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
                // Left untyped: 10 optional slots, past methodTypedOpt5's maximum arity.
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
                // Left untyped: 9 optional slots, past methodTypedOpt5's maximum arity.
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
                // Left untyped: 9 optional slots (past methodTypedOpt5's maximum arity), and arg 1
                // must additionally be shape-checked as a ScriptValue.Array.
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
                // Left untyped: 8 optional slots, past methodTypedOpt5's maximum arity.
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
                // Migrated: "newPriority != null" is exactly the original's args.size() >= 2.
                .methodTypedOpt2("priority", TypeCodecs.STRING, (String) null,
                    TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW,
                    (Builder b, String id, Double newPriority) -> {
                        if (newPriority != null && !b.widgets.isEmpty()) {
                            int p = (int) newPriority.doubleValue();
                            for (int i = 0; i < b.widgets.size(); i++) {
                                if (b.widgets.get(i).id().equals(id)) {
                                    b.widgets.set(i, withPriority(b.widgets.get(i), p));
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
                .methodTypedOpt1("hover_state", TypeCodecs.STRING, (String) null, TypeCodecs.RAW,
                    (Builder b, String state) -> {
                        if (state != null && !b.widgets.isEmpty()) {
                            String id = b.widgets.get(b.widgets.size() - 1).id();
                            b.hoverCursorStates.put(id, state);
                        }
                        return ScriptValue.ofObj("VirtualUIBuilder", b);
                    })
                // build() takes no args at all — methodTyped0 has no "missing args" branch to
                // worry about (that's the whole issue blocking its siblings above), so this one
                // migrates cleanly.
                .methodTyped0("build", TypeCodecs.RAW, (Builder b) -> ScriptValue.ofObj("BuiltVirtualUI",
                        VirtualUIScreen.of(b.title, b.widgets, b.cameraDistance, b.playerInvisible, b.onCloseRef,
                                b.maxOffsetX, b.maxOffsetY, b.cursorIcon, b.cursorItemId, b.hoverCursorStates))));

        // Migrated: "show"'s missing-arg fallback (args.isEmpty() -> false) is
        // instance-independent — unlike the VirtualUIBuilder chain above, it does NOT touch obj —
        // so onMissingArgs=false is exact. The player arg stays TypeCodecs.RAW (playerOf needs the
        // raw ScriptValue); the instanceof check on screenOf(obj) stays inside the body unchanged.
        PolyTypeRegistry.define("BuiltVirtualUI")
                .methodTyped1("show", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                    (Object obj, ScriptValue playerArg) -> {
                        Player p = playerOf(playerArg);
                        if (p == null || !(screenOf(obj) instanceof VirtualUIScreen screen)) return false;
                        return VirtualUICameraSystem.show(p, screen);
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
                // Migrated: missing-arg fallback (args.isEmpty() -> false) is instance-independent
                // (doesn't touch obj), so onMissingArgs=false is exact; the try/catch around the
                // real logic is kept unchanged inside the typed body.
                .methodTyped1("open_url", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                    (Object obj, String url) -> {
                        try {
                            org.bukkit.entity.Player bp = bukkitPlayerOf(obj);
                            if (bp == null) return false;
                            net.kyori.adventure.text.Component msg = net.kyori.adventure.text.Component
                                    .text(url)
                                    .color(net.kyori.adventure.text.format.NamedTextColor.AQUA)
                                    .decorate(net.kyori.adventure.text.format.TextDecoration.UNDERLINED)
                                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.openUrl(url))
                                    .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                                            net.kyori.adventure.text.Component.text("Click to open")));
                            bp.sendMessage(msg);
                            return true;
                        } catch (Throwable ex) { return false; }
                    })
                // switch_server(name) — BungeeCord/Velocity "Connect" plugin-message, standard
                // proxy-switch mechanism; a no-op (returns false) on a non-proxied standalone server.
                // Migrated: same instance-independent onMissingArgs=false rationale as open_url.
                .methodTyped1("switch_server", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                    (Object obj, String name) -> {
                        try {
                            org.bukkit.entity.Player bp = bukkitPlayerOf(obj);
                            if (bp == null) return false;
                            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                            java.io.DataOutputStream data = new java.io.DataOutputStream(out);
                            data.writeUTF("Connect");
                            data.writeUTF(name);
                            bp.sendPluginMessage(dev.arubik.craftengine.CraftEnginePolyfills.instance(), "BungeeCord", out.toByteArray());
                            return true;
                        } catch (Throwable ex) { return false; }
                    }));
    }

    private static org.bukkit.entity.Player bukkitPlayerOf(Object obj) {
        if (obj instanceof net.minecraft.world.entity.player.Player nms) {
            try { return (org.bukkit.entity.Player) nms.getBukkitEntity(); } catch (Throwable ignored) {}
        }
        return null;
    }

    private static void registerHologramBuilder() {
        // Every setter below has the "return wrap(this call's own config) unchanged when args are
        // missing/insufficient" shape, which needs the instance on the missing-arg path — so they
        // all use methodTypedOptN (whose handler ALWAYS runs and so always has the instance), never
        // methodTypedN/onMissingArgs. See the null-sentinel note above the VirtualUIBuilder chain
        // in register() for why "param == null" is exactly the original "arg wasn't supplied".
        // A HologramBuilder's instance is always a plain HologramLineConfig (the old config(obj)
        // helper was a bare cast and nothing else), so the instance parameter is typed
        // HologramLineConfig directly, matching build()'s existing typed registration below.
        PolyTypeRegistry.define("HologramBuilder")
                .methodTypedOpt3("at", TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null,
                    TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW,
                    (HologramLineConfig c, Double x, Double y, Double z) -> ScriptValue.ofObj("HologramBuilder",
                            z == null ? c : c.withOffset(x, y, z)))
                .methodTypedOpt1("scale", TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW,
                    (HologramLineConfig c, Double scale) -> {
                        if (scale != null) {
                            float s = (float) scale.doubleValue();
                            c = new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), s,
                                    c.rotationX(), c.rotationY(), c.rotationZ(), c.visible(), c.alignment(), c.lineWidth(),
                                    c.opacity(), c.billboard(), c.seeThrough(), c.shadow(), c.backgroundR(), c.backgroundG(),
                                    c.backgroundB(), c.backgroundA(), c.brightnessBlock(), c.brightnessSky());
                        }
                        return ScriptValue.ofObj("HologramBuilder", c);
                    })
                .methodTypedOpt3("rotation", TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null,
                    TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW,
                    (HologramLineConfig c, Double rx, Double ry, Double rz) -> {
                        if (rz != null) {
                            c = new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), c.scale(),
                                    (float) rx.doubleValue(), (float) ry.doubleValue(), (float) rz.doubleValue(),
                                    c.visible(), c.alignment(), c.lineWidth(), c.opacity(), c.billboard(), c.seeThrough(),
                                    c.shadow(), c.backgroundR(), c.backgroundG(), c.backgroundB(), c.backgroundA(),
                                    c.brightnessBlock(), c.brightnessSky());
                        }
                        return ScriptValue.ofObj("HologramBuilder", c);
                    })
                .methodTypedOpt1("alignment", TypeCodecs.STRING, (String) null, TypeCodecs.RAW,
                    (HologramLineConfig c, String v) -> ScriptValue.ofObj("HologramBuilder",
                            v == null ? c : withAlignment(c, v)))
                .methodTypedOpt1("billboard", TypeCodecs.STRING, (String) null, TypeCodecs.RAW,
                    (HologramLineConfig c, String v) -> ScriptValue.ofObj("HologramBuilder",
                            v == null ? c : withBillboard(c, v)))
                .methodTypedOpt1("line_width", TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW,
                    (HologramLineConfig c, Double v) -> ScriptValue.ofObj("HologramBuilder",
                            v == null ? c : withLineWidth(c, (int) v.doubleValue())))
                .methodTypedOpt1("opacity", TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW,
                    (HologramLineConfig c, Double v) -> ScriptValue.ofObj("HologramBuilder",
                            v == null ? c : withOpacity(c, Math.min(254, (int) v.doubleValue()))))
                // Default TRUE reproduces "args.isEmpty() || args.get(0).asBool()" exactly — the
                // missing-arg case here genuinely means a value, not "don't touch".
                .methodTypedOpt1("see_through", TypeCodecs.BOOL, true, TypeCodecs.RAW,
                    (HologramLineConfig c, Boolean v) -> ScriptValue.ofObj("HologramBuilder", withSeeThrough(c, v)))
                .methodTypedOpt1("shadow", TypeCodecs.BOOL, true, TypeCodecs.RAW,
                    (HologramLineConfig c, Boolean v) -> ScriptValue.ofObj("HologramBuilder", withShadow(c, v)))
                .methodTypedOpt4("background", TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null,
                    TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null, TypeCodecs.RAW,
                    (HologramLineConfig c, Double r, Double g, Double bl, Double a) -> {
                        if (a != null) {
                            c = new HologramLineConfig(c.text(), c.offsetX(), c.offsetY(), c.offsetZ(), c.scale(),
                                    c.rotationX(), c.rotationY(), c.rotationZ(), c.visible(), c.alignment(), c.lineWidth(),
                                    c.opacity(), c.billboard(), c.seeThrough(), c.shadow(),
                                    (int) r.doubleValue(), (int) g.doubleValue(), (int) bl.doubleValue(), (int) a.doubleValue(),
                                    c.brightnessBlock(), c.brightnessSky());
                        }
                        return ScriptValue.ofObj("HologramBuilder", c);
                    })
                // 0-arg — no missing-args branch to worry about, same rationale as
                // VirtualUIBuilder.build() above.
                .methodTyped0("build", TypeCodecs.RAW,
                    (HologramLineConfig c) -> ScriptValue.ofObj("Hologram", c));
    }

    // --- helpers ---

    /** {@code ref == null} is the typed-registration spelling of the original untyped body's
     *  "args.isEmpty()" — see the null-sentinel note above the VirtualUIBuilder chain. */
    private static ScriptValue replaceLastWidgetCallback(Builder b, String ref, int which) {
        if (ref != null && !b.widgets.isEmpty()) {
            int i = b.widgets.size() - 1;
            b.widgets.set(i, withCallback(b.widgets.get(i), which, ref));
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
