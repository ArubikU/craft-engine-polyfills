package dev.arubik.craftengine.script.types.util;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.script.types.machine.MachineType;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Port of Paper's Dialog system (io.papermc.paper.dialog.*, docs.papermc.io/paper/dev/dialogs)
 * into the .pf script engine, as its own type hierarchy — NOT hung off Machine, which already
 * mixes enough unrelated concerns. Registers three script types:
 *
 * <ul>
 *   <li>{@code "Dialog"} — the global entry point (a singleton, same convention as
 *       {@code Server}/{@code ContraptionManager}): {@code Dialog.base(title)} starts a builder.
 *   <li>{@code "DialogBuilder"} — mutable, chainable (every method returns the same wrapped
 *       {@link Builder} instance): add body text/items and inputs, then finalize into a shown-once
 *       shape (notice/confirmation/multi-action) with {@code .as_*(...)}.
 *   <li>{@code "BuiltDialog"} — the finalized, showable {@link Dialog}: {@code .show(player)}.
 * </ul>
 *
 * All gated behind {@link DialogSupport#isAvailable()} so a jar built against this API doesn't
 * crash with {@code NoClassDefFoundError} on an older Paper without Dialogs.
 *
 * <p>Action buttons resolve their {@code "file.pf:func[:args]"} ref the same way a menu button's
 * {@code "action"} field does (see {@link ScriptCall}), with whatever the player typed/picked
 * appended as extra arguments. The calling script passes its own {@code Machine} object into the
 * {@code .as_*(...)} finalizer explicitly — Java method handlers only see {@code (obj, args)}, not
 * the ambient script context, so there is no implicit "nearest player"/"current machine" guessing
 * anywhere in this file: the machine is whatever the script says it is, and the player is always
 * whoever actually clicked the button (read from the live click callback), never inferred.
 */
public final class DialogManagerType {

    /** Stateless singleton bound as the global "Dialog" — same convention as Server/ContraptionManager. */
    public static final Object INSTANCE = new Object();

    private DialogManagerType() {}

    /** Which typed accessor a submitted input's value must be read back with. Reading it back
     *  with any OTHER accessor risked silently corrupting the value (see {@link #readValue}) —
     *  the previous version tried text-then-bool-then-float on every key regardless of what kind
     *  of input actually declared it, and kept whichever guess came back non-null last, so a
     *  type-mismatched accessor that coerced instead of returning null would silently clobber a
     *  correctly-read earlier guess. Tracking the REAL declared kind at build time removes the
     *  guessing entirely. */
    private enum Kind { TEXT, BOOL, NUMBER, OPTION }

    private record InputSpec(String key, Kind kind) {}

    /** Mutable builder state — a plain Java object, wrapped/re-wrapped as the same ScriptValue.Obj
     *  instance on every chained call so "b = Dialog.base(t).input_text(...); b.as_notice(...)"
     *  and one-line chaining both work identically. */
    public static final class Builder {
        Component title;
        final List<DialogBody> body = new ArrayList<>();
        final List<DialogInput> inputs = new ArrayList<>();
        final List<InputSpec> specs = new ArrayList<>();
        boolean canCloseWithEscape = true;
        String onCloseRef;

        Builder(ScriptValue title) { this.title = partsToComponent(title); }
    }

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger("CraftEnginePolyfills");

    /** Set for the duration of one dialog action click (see {@link #buildAction}) to the CLICKING
     *  player's UUID plus a mutable flag — {@code BuiltDialog.show}/{@code show_many} flip the flag
     *  true when they successfully redisplay a dialog to that SAME player mid-click (never for some
     *  other player also shown something in the same click, e.g. an opponent's board refresh), so
     *  the click's own closeDialog() at the end doesn't immediately dismiss what the script just
     *  showed. Unset outside a click (plain "open a dialog from a command" calls), where it's simply
     *  not observed by anything. */
    private record ReshowTracker(java.util.UUID clickingPlayer, boolean[] flag) {}
    private static final ThreadLocal<ReshowTracker> RESHOWN = new ThreadLocal<>();

    public static void register() {
        PolyTypeRegistry.define("Dialog")
            // base(title) — NOT migrated to methodTyped1: the DialogSupport.isAvailable() warning log
            // must fire even when called with zero args (it's checked BEFORE the args.isEmpty() check,
            // independent of arg count), but methodTypedN's onMissingArgs shortcut returns immediately
            // for a too-short args list WITHOUT ever entering the handler body — so that log line would
            // silently stop firing for a 0-arg call on an unavailable server. Left untyped.
            .method("base", (obj, args) -> {
                if (!DialogSupport.isAvailable()) {
                    LOG.warning("[Dialog] Dialog.base() called but DialogSupport.isAvailable() is false — "
                        + "io.papermc.paper.dialog.Dialog / DialogAction couldn't be loaded on this server's "
                        + "Paper implementation. Every Dialog.* call from a script silently no-ops as a result.");
                    return ScriptValue.NULL;
                }
                if (args.isEmpty()) return ScriptValue.NULL;
                return ScriptValue.ofObj("DialogBuilder", new Builder(args.get(0)));
            });

        PolyTypeRegistry.define("DialogBuilder")
            // can_close_with_escape(bool) — NOT migrated to methodTyped1: a missing arg still runs
            // the handler with a default of true ("default-if-missing" shape) rather than short-
            // circuiting, which methodTypedN's onMissingArgs can't express. Left untyped.
            .method("can_close_with_escape", (obj, args) -> {
                Builder b = builder(obj);
                b.canCloseWithEscape = args.isEmpty() || args.get(0).asBool();
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // on_close(action_ref) — called after ANY button action finishes (or, once Escape can be
            // told apart from a real button click, on Escape too), in place of the default "reopen
            // the machine's own menu" behavior. Call before .as_notice/.as_confirmation/
            // .as_multi_action, since those are what finalize the buttons that read this. A dialog
            // opened directly from a block right-click (no menu ever involved, e.g. Smart Chute's
            // ui:false pull-amount slider) should set this instead of relying on flags.ui to suppress
            // the reopen — flags.ui only happens to be false for every dialog-outside-a-menu case so
            // far, not because it's actually what determines this.
            // NOT migrated to methodTyped1: a missing arg still runs the handler and just skips the
            // assignment ("default-if-missing" shape) rather than short-circuiting. Left untyped.
            .method("on_close", (obj, args) -> {
                Builder b = builder(obj);
                if (!args.isEmpty()) b.onCloseRef = args.get(0).asStr();
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // body(text) — a plain message line, OR an Array mixing plain strings with
            // Images.from(id)/Images.shift(n) parts (same convention a machine's title uses — see
            // DataMachineBlockEntity#scriptValueToTitleComponent) for a line that embeds a
            // CraftEngine font glyph. Call multiple times for multiple paragraphs.
            // NOT migrated to methodTyped1: a missing arg still runs the handler and just skips the
            // append ("default-if-missing" shape) rather than short-circuiting. Left untyped.
            .method("body", (obj, args) -> {
                Builder b = builder(obj);
                if (!args.isEmpty()) b.body.add(DialogBody.plainMessage(partsToComponent(args.get(0))));
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // body_item(item, show_tooltip?) — shows an ItemStack in the dialog body.
            // NOT migrated to methodTyped: "default-if-missing" shape (missing/wrong-shape args just
            // skip the append instead of short-circuiting) plus a dynamic instanceof-typed arg. Left
            // untyped.
            .method("body_item", (obj, args) -> {
                Builder b = builder(obj);
                if (!args.isEmpty() && args.get(0) instanceof ScriptValue.Item it) {
                    try {
                        ItemStack bukkit = org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(it.stack());
                        addItemBody(b, bukkit, args.size() >= 2 ? args.get(1) : null);
                    } catch (Throwable ignored) {}
                }
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // body_head(owner_name_or_uuid, show_tooltip?) — shows a player-head ItemStack, owned by
            // that name/UUID, in the dialog body (Bukkit#getOfflinePlayer, no texture support).
            // NOT migrated to methodTyped: "default-if-missing" shape (a missing arg just skips the
            // append instead of short-circuiting), plus an optional trailing show_tooltip arg. Left
            // untyped.
            .method("body_head", (obj, args) -> {
                Builder b = builder(obj);
                if (!args.isEmpty()) {
                    try {
                        String ownerStr = args.get(0).asStr();
                        OfflinePlayer owner;
                        try {
                            owner = org.bukkit.Bukkit.getOfflinePlayer(java.util.UUID.fromString(ownerStr));
                        } catch (IllegalArgumentException notUuid) {
                            owner = org.bukkit.Bukkit.getOfflinePlayer(ownerStr);
                        }
                        ItemStack head = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
                        if (head.getItemMeta() instanceof SkullMeta meta) {
                            meta.setOwningPlayer(owner);
                            head.setItemMeta(meta);
                        }
                        addItemBody(b, head, args.size() >= 2 ? args.get(1) : null);
                    } catch (Throwable ignored) {}
                }
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // input_text(key, label, initial?, width?, multiline?, max_lines?)
            // NOT migrated to methodTyped: "default-if-missing" shape (missing args just skip the
            // append instead of short-circuiting) with several optional trailing args (initial, width,
            // multiline, max_lines). Left untyped.
            .method("input_text", (obj, args) -> {
                Builder b = builder(obj);
                if (args.size() >= 2) {
                    String key = args.get(0).asStr();
                    var tb = DialogInput.text(key, mm(args.get(1).asStr()));
                    if (args.size() >= 3) tb.initial(args.get(2).asStr());
                    if (args.size() >= 4) tb.width((int) args.get(3).asNum());
                    if (args.size() >= 5 && args.get(4).asBool()) {
                        Integer maxLines = args.size() >= 6 ? (int) args.get(5).asNum() : null;
                        tb.multiline(io.papermc.paper.registry.data.dialog.input.TextDialogInput.MultilineOptions.create(maxLines, null));
                    }
                    b.inputs.add(tb.build());
                    b.specs.add(new InputSpec(key, Kind.TEXT));
                }
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // input_bool(key, label, initial?)
            // NOT migrated to methodTyped: "default-if-missing" shape (missing args just skip the
            // append instead of short-circuiting) with an optional trailing initial arg. Left untyped.
            .method("input_bool", (obj, args) -> {
                Builder b = builder(obj);
                if (args.size() >= 2) {
                    String key = args.get(0).asStr();
                    var bb = DialogInput.bool(key, mm(args.get(1).asStr()));
                    if (args.size() >= 3) bb.initial(args.get(2).asBool());
                    b.inputs.add(bb.build());
                    b.specs.add(new InputSpec(key, Kind.BOOL));
                }
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // input_number(key, label, min, max, step?, initial?)
            // NOT migrated to methodTyped: "default-if-missing" shape (missing args just skip the
            // append instead of short-circuiting) with optional trailing step/initial args. Left
            // untyped.
            .method("input_number", (obj, args) -> {
                Builder b = builder(obj);
                if (args.size() >= 4) {
                    String key = args.get(0).asStr();
                    var nb = DialogInput.numberRange(key, mm(args.get(1).asStr()),
                            (float) args.get(2).asNum(), (float) args.get(3).asNum());
                    if (args.size() >= 5) nb.step((float) args.get(4).asNum());
                    if (args.size() >= 6) nb.initial((float) args.get(5).asNum());
                    b.inputs.add(nb.build());
                    b.specs.add(new InputSpec(key, Kind.NUMBER));
                }
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // input_single_option(key, label, options) — options is an Array of maps built with
            // make_map("id", "...", "label", "...", "initial", true/false) (initial optional).
            // NOT migrated to methodTyped: "default-if-missing" shape (missing/wrong-shape args just
            // skip the append instead of short-circuiting), plus a dynamic Array-of-maps arg. Left
            // untyped.
            .method("input_single_option", (obj, args) -> {
                Builder b = builder(obj);
                if (args.size() >= 3 && args.get(2) instanceof ScriptValue.Array arr) {
                    String key = args.get(0).asStr();
                    List<SingleOptionDialogInput.OptionEntry> entries = new ArrayList<>();
                    for (ScriptValue v : arr.elements()) {
                        if (!(v instanceof ScriptValue.Obj mo) || !(mo.instance() instanceof java.util.Map<?, ?> map)) continue;
                        Object idV = map.get("id");
                        Object labelV = map.get("label");
                        Object initV = map.get("initial");
                        String id = idV instanceof ScriptValue sv ? sv.asStr() : String.valueOf(idV);
                        String label = labelV instanceof ScriptValue sv ? sv.asStr() : String.valueOf(labelV);
                        boolean initial = initV instanceof ScriptValue sv && sv.asBool();
                        entries.add(SingleOptionDialogInput.OptionEntry.create(id, mm(label), initial));
                    }
                    b.inputs.add(DialogInput.singleOption(key, mm(args.get(1).asStr()), entries).build());
                    b.specs.add(new InputSpec(key, Kind.OPTION));
                }
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // as_notice(machine, button_label, action_ref, tooltip?, width?) — single-button dialog.
            // Accept calls action_ref with every declared input's value appended, in declaration order.
            // NOT migrated to methodTyped3: has genuinely optional 4th/5th args (tooltip, width) past
            // the required 3, same reasoning as ContraptionType's teleport()/play_sound() — a typed
            // handler only receives its fixed-arity decoded arguments, not the original args
            // list/size, so that conditional read can't be expressed. Left untyped.
            .method("as_notice", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                Builder b = builder(obj);
                DialogAction action = buildAction(args.get(0), args.get(2).asStr(), b);
                var buttonB = ActionButton.builder(mm(args.get(1).asStr())).action(action);
                if (args.size() >= 4 && !args.get(3).asStr().isBlank()) buttonB.tooltip(mm(args.get(3).asStr()));
                if (args.size() >= 5) buttonB.width((int) args.get(4).asNum());
                DialogBase base = base(b);
                DialogType type = DialogType.notice(buttonB.build());
                return wrapDialog(base, type);
            })
            // as_confirmation(owner, accept_label, cancel_label, accept_action_ref, cancel_action_ref?,
            // accept_tooltip?, cancel_tooltip?, accept_width?, cancel_width?)
            // NOT migrated to methodTyped: has multiple genuinely optional args past the required 4
            // (cancel_action_ref, tooltips, widths), same reasoning as as_notice() above. Also 9 total
            // args, past methodTypedN's arity-7 ceiling. Left untyped.
            .method("as_confirmation", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.NULL;
                Builder b = builder(obj);
                ScriptValue owner = args.get(0);
                DialogAction yes = buildAction(owner, args.get(3).asStr(), b);
                DialogAction no = args.size() >= 5 && !args.get(4).asStr().isBlank()
                        ? buildAction(owner, args.get(4).asStr(), b)
                        : DialogAction.customClick((view, audience) -> { if (audience instanceof Player p) p.closeDialog(); },
                            ClickCallback.Options.builder().uses(1).build());
                var acceptB = ActionButton.builder(mm(args.get(1).asStr())).action(yes);
                var cancelB = ActionButton.builder(mm(args.get(2).asStr())).action(no);
                if (args.size() >= 6 && !args.get(5).asStr().isBlank()) acceptB.tooltip(mm(args.get(5).asStr()));
                if (args.size() >= 7 && !args.get(6).asStr().isBlank()) cancelB.tooltip(mm(args.get(6).asStr()));
                if (args.size() >= 8) acceptB.width((int) args.get(7).asNum());
                if (args.size() >= 9) cancelB.width((int) args.get(8).asNum());
                DialogBase base = base(b);
                DialogType type = DialogType.confirmation(acceptB.build(), cancelB.build());
                return wrapDialog(base, type);
            })
            // as_multi_action(owner, buttons, columns?) — buttons is an Array of maps built with
            // make_map("label", "...", "action", "file.pf:func", "tooltip", "...", "width", 100).
            // "tooltip"/"width" are optional. Every button gets whatever inputs were declared
            // appended as arguments, same as as_notice. Optional trailing "columns" (a grid-width
            // integer, Paper's own MultiActionType#columns()) wraps buttons into rows of that width
            // instead of Paper's default 2 — the ONLY way to lay out a fixed grid (e.g. a game
            // board) that isn't just "however many buttons fit at their declared per-button width".
            // NOT migrated to methodTyped2: has a genuinely optional 3rd arg (columns), same reasoning
            // as as_notice() above. Left untyped.
            .method("as_multi_action", (obj, args) -> {
                if (args.size() < 2 || !(args.get(1) instanceof ScriptValue.Array arr)) return ScriptValue.NULL;
                Builder b = builder(obj);
                ScriptValue owner = args.get(0);
                List<ActionButton> buttons = new ArrayList<>();
                for (ScriptValue v : arr.elements()) {
                    if (!(v instanceof ScriptValue.Obj mo) || !(mo.instance() instanceof java.util.Map<?, ?> map)) continue;
                    Object labelV = map.get("label");
                    Object actionV = map.get("action");
                    Component labelComponent = labelV instanceof ScriptValue sv
                            ? partsToComponent(sv) : mm(String.valueOf(labelV));
                    String actionRef = actionV instanceof ScriptValue sv ? sv.asStr() : String.valueOf(actionV);
                    var btnB = ActionButton.builder(labelComponent).action(buildAction(owner, actionRef, b));
                    Object tooltipV = map.get("tooltip");
                    if (tooltipV instanceof ScriptValue sv && !sv.asStr().isBlank()) btnB.tooltip(mm(sv.asStr()));
                    Object widthV = map.get("width");
                    if (widthV instanceof ScriptValue sv) btnB.width((int) sv.asNum());
                    buttons.add(btnB.build());
                }
                DialogBase base = base(b);
                DialogType type = args.size() >= 3 && args.get(2).asNum() > 0
                        ? DialogType.multiAction(buttons, null, (int) args.get(2).asNum())
                        : DialogType.multiAction(buttons).build();
                return wrapDialog(base, type);
            })
            // as_link(owner, button_label, url) — single-button dialog whose click opens a URL.
            // Skips the .pf script engine entirely (no action_ref). owner (arg0) is decoded but
            // intentionally unused, exactly like the original — kept as a reserved positional arg.
            .methodTyped3("as_link", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Builder b, ScriptValue owner, String buttonLabel, String url) -> {
                    DialogAction action = DialogAction.staticAction(ClickEvent.openUrl(url));
                    DialogBase base = base(b);
                    DialogType type = DialogType.notice(ActionButton.builder(mm(buttonLabel)).action(action).build());
                    return wrapDialog(base, type);
                })
            // as_run_command(owner, button_label, command) — single-button dialog whose click runs a
            // vanilla command. Skips the .pf script engine entirely (no action_ref). owner (arg0) is
            // decoded but intentionally unused, exactly like the original.
            .methodTyped3("as_run_command", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Builder b, ScriptValue owner, String buttonLabel, String command) -> {
                    DialogAction action = DialogAction.staticAction(ClickEvent.runCommand(command));
                    DialogBase base = base(b);
                    DialogType type = DialogType.notice(ActionButton.builder(mm(buttonLabel)).action(action).build());
                    return wrapDialog(base, type);
                });

        PolyTypeRegistry.define("BuiltDialog")
            // show(player) — display to that player.
            // NOT migrated to methodTyped1: the missing-arg branch logs a warning before returning
            // false, but methodTypedN's onMissingArgs shortcut returns immediately without ever
            // entering the handler body, so that log line would silently stop firing. Left untyped.
            .method("show", (obj, args) -> {
                if (args.isEmpty()) {
                    LOG.warning("[Dialog] BuiltDialog.show() called with no player argument.");
                    return ScriptValue.of(false);
                }
                Player p = playerOf(args.get(0));
                if (p == null) {
                    LOG.warning("[Dialog] BuiltDialog.show(" + args.get(0) + ") — playerOf() couldn't resolve a "
                        + "Bukkit Player from that argument (was it really a Player value, e.g. from the "
                        + "bare \"Player\" binding?).");
                    return ScriptValue.of(false);
                }
                if (!(dialogOf(obj) instanceof Dialog d)) {
                    LOG.warning("[Dialog] BuiltDialog.show() — the receiver wasn't a built Dialog (did the "
                        + ".as_notice/.as_confirmation/.as_multi_action call before it return NULL?).");
                    return ScriptValue.of(false);
                }
                try {
                    p.showDialog(d);
                    markReshownIfClicker(p.getUniqueId());
                    return ScriptValue.of(true);
                } catch (Throwable t) {
                    LOG.log(java.util.logging.Level.WARNING, "[Dialog] Player#showDialog threw", t);
                    return ScriptValue.of(false);
                }
            })
            // show_many(players) — display to every resolvable Player in that Array, skipping nulls.
            // Returns the count of players actually shown.
            // NOT migrated to methodTyped1: the missing/wrong-type-arg branch logs a warning before
            // returning 0, same log-loss issue as show() above. Left untyped.
            .method("show_many", (obj, args) -> {
                if (args.isEmpty() || !(args.get(0) instanceof ScriptValue.Array arr)) {
                    LOG.warning("[Dialog] BuiltDialog.show_many() called with no players array argument.");
                    return ScriptValue.of(0.0);
                }
                if (!(dialogOf(obj) instanceof Dialog d)) {
                    LOG.warning("[Dialog] BuiltDialog.show_many() — the receiver wasn't a built Dialog (did the "
                        + ".as_notice/.as_confirmation/.as_multi_action call before it return NULL?).");
                    return ScriptValue.of(0.0);
                }
                int shown = 0;
                for (ScriptValue v : arr.elements()) {
                    Player p = playerOf(v);
                    if (p == null) continue;
                    try {
                        p.showDialog(d);
                        markReshownIfClicker(p.getUniqueId());
                        shown++;
                    } catch (Throwable t) {
                        LOG.log(java.util.logging.Level.WARNING, "[Dialog] Player#showDialog threw", t);
                    }
                }
                return ScriptValue.of((double) shown);
            });
    }

    /** Flags {@link #RESHOWN} when {@code shownTo} is the player whose OWN click is currently
     *  executing (see {@link #buildAction}) — a no-op outside a click, or when a dialog action shows
     *  a dialog to some OTHER player (e.g. pushing a refreshed board to an opponent). */
    private static void markReshownIfClicker(java.util.UUID shownTo) {
        ReshowTracker t = RESHOWN.get();
        if (t != null && t.clickingPlayer().equals(shownTo)) t.flag()[0] = true;
    }

    /** MiniMessage-parse a dialog string, matching how every other display text in this codebase
     *  (button names/lore, item names — see ItemType#toDisplayComponent) accepts "<yellow>..."
     *  tags; falls back to literal text on bad syntax instead of showing nothing. Italics are NOT
     *  force-disabled here (unlike item names) since dialog text isn't rendered on an item tooltip
     *  where vanilla's default-italic behavior would otherwise leak in. */
    private static Component mm(String text) {
        try {
            return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text == null ? "" : text);
        } catch (Throwable ignored) {
            return Component.text(text == null ? "" : text);
        }
    }

    /** Builds one {@link Component} from either a plain MiniMessage string or an {@code Array} of
     *  parts mixing plain strings with {@code Images.from(id, shift?)}/{@code Images.shift(n)}
     *  results — the SAME {@code _TitleImage}/{@code _TitleShift} script values a machine's title
     *  already accepts (see {@code DataMachineBlockEntity#scriptValueToTitleComponent}), reusing
     *  the exact same {@code MenuText.imageTitle}/{@code MenuText.shiftOnly} glyph-resolution the
     *  rest of the codebase uses — a Dialog title/body/button label is no different a piece of text
     *  than a menu's, so it should be built the same way instead of a second parallel mechanism. */
    private static Component partsToComponent(ScriptValue value) {
        if (value instanceof ScriptValue.Array arr) {
            Component out = Component.empty();
            for (ScriptValue part : arr.elements()) out = out.append(partToComponent(part));
            return out;
        }
        return partToComponent(value);
    }

    private static Component partToComponent(ScriptValue part) {
        if (part instanceof ScriptValue.Obj o && "_TitleImage".equals(o.typeName())
                && o.instance() instanceof String[] data && data.length >= 2) {
            try {
                Component img = dev.arubik.craftengine.machine.menu.MenuText.imageTitle(
                        data[0], Integer.parseInt(data[1]));
                if (img != null) return img;
            } catch (Throwable ignored) {}
            return Component.empty();
        }
        if (part instanceof ScriptValue.Obj o && "_TitleShift".equals(o.typeName())
                && o.instance() instanceof Integer shift) {
            return dev.arubik.craftengine.machine.menu.MenuText.shiftOnly(shift);
        }
        return mm(part.asStr());
    }

    private static void addItemBody(Builder b, ItemStack bukkit, ScriptValue showTooltip) {
        var ib = DialogBody.item(bukkit);
        if (showTooltip != null) ib.showTooltip(showTooltip.asBool());
        b.body.add(ib.build());
    }

    private static Builder builder(Object obj) { return (Builder) obj; }

    private static DialogBase base(Builder b) {
        var bb = DialogBase.builder(b.title).canCloseWithEscape(b.canCloseWithEscape);
        if (!b.body.isEmpty()) bb.body(b.body);
        if (!b.inputs.isEmpty()) bb.inputs(b.inputs);
        return bb.build();
    }

    private static ScriptValue wrapDialog(DialogBase base, DialogType type) {
        Dialog d = Dialog.create(factory -> factory.empty().base(base).type(type));
        return ScriptValue.ofObj("BuiltDialog", d);
    }

    private static Object dialogOf(Object obj) { return obj; }

    private static MachineType.MachineRef machineOf(ScriptValue v) {
        if (v instanceof ScriptValue.Obj o && o.instance() instanceof MachineType.MachineRef m) return m;
        return null;
    }

    private static Player playerOf(ScriptValue v) {
        if (v instanceof ScriptValue.Obj o && o.instance() instanceof net.minecraft.world.entity.player.Player nms) {
            try { return (Player) nms.getBukkitEntity(); } catch (Throwable ignored) {}
        }
        return null;
    }

    /** Reads one submitted input back using ONLY the accessor matching its declared kind — never
     *  guessing across text/bool/float, so a type mismatch fails safe (a sensible default for that
     *  kind) instead of risking a wrong-but-non-null coerced value silently overwriting a correct
     *  earlier read (the bug this replaces: trying every accessor and keeping whichever came back
     *  non-null last).
     *
     *  {@code view}'s real class ({@code DialogResponseView}'s exact package couldn't be pinned
     *  down without the {@code find} command, permission-denied on this machine) is reached via
     *  reflection instead of a compile-time import — mirrors {@code BlockMetadataType}'s
     *  {@code reflectOptionalUuid}/{@code gameProfileId} pattern for the same reason: call the ONE
     *  correctly-named method for this input's declared kind by name, not by a guessed type. */
    private static ScriptValue readValue(Object view, InputSpec spec) {
        String methodName = switch (spec.kind()) {
            case TEXT, OPTION -> "getText";
            case BOOL -> "getBoolean";
            case NUMBER -> "getFloat";
        };
        try {
            Object result = view.getClass().getMethod(methodName, String.class).invoke(view, spec.key());
            return switch (spec.kind()) {
                case TEXT, OPTION -> ScriptValue.of(result instanceof String s ? s : "");
                case BOOL -> ScriptValue.of(result instanceof Boolean b && b);
                case NUMBER -> ScriptValue.of(result instanceof Number n ? n.doubleValue() : 0.0);
            };
        } catch (Throwable ignored) {
            return switch (spec.kind()) {
                case TEXT, OPTION -> ScriptValue.of("");
                case BOOL -> ScriptValue.of(false);
                case NUMBER -> ScriptValue.of(0.0);
            };
        }
    }

    /** Builds the DialogAction for one button: on click, resolves the clicking player, rebuilds a
     *  fresh script context bound to {@code owner} + that REAL player (never a guess), and calls
     *  actionRef with every declared input's submitted value appended in declaration order, PLUS a
     *  trailing Map argument (key -> value, for every declared input) for named access — a callback
     *  that only cares about one of several inputs, or that's shared across dialogs with different
     *  input sets, can read {@code results.get("key")} instead of relying on positional order.
     *
     *  <p>{@code owner} is whatever the script passed as {@code as_notice}/{@code
     *  as_confirmation}/{@code as_multi_action}'s first argument. When it resolves to a real
     *  {@code Machine} backed by a {@code DataMachineBlockEntity}, behavior is UNCHANGED from
     *  before this was generalized: the fired script gets that machine's own full context, and the
     *  dialog closes back into the machine's menu (not just to nothing) afterward. Any other value
     *  — {@code NULL} included, e.g. a {@code /cmds} command with no machine at all — falls back to
     *  a plain baseline context (the same {@code Server}/{@code Item}/{@code Menu}/{@code
     *  EventManager}/{@code TaskManager} namespaces every other script entry point this session
     *  binds) plus the clicking player, and the dialog just closes when done. This is what lets a
     *  standalone command like {@code /teleporters} use the exact same Dialog API a machine button
     *  does, with no machine involved at all. */
    private static DialogAction buildAction(ScriptValue owner, String actionRef, Builder b) {
        List<InputSpec> keys = List.copyOf(b.specs);
        String onCloseRef = b.onCloseRef;
        MachineType.MachineRef machine = machineOf(owner);
        dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm =
                machine != null && machine.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity d
                        ? d : null;
        return DialogAction.customClick(
                (view, audience) -> {
                    if (!(audience instanceof Player bukkitPlayer)) return;
                    ServerPlayer sp = ((CraftPlayer) bukkitPlayer).getHandle();
                    // Tracks whether THIS click's own action script already re-displayed a (fresh)
                    // dialog to this same player via BuiltDialog.show/show_many (see RESHOWN below) —
                    // e.g. a turn-based game redrawing its own board after a move. Without this, the
                    // unconditional bukkitPlayer.closeDialog() at the bottom of this finally block
                    // would immediately dismiss whatever the script just showed, since Dialog has no
                    // OTHER way (unlike a machine's inventory Menu) to stay open across one click's
                    // own action — every non-machine dialog action used to hard-close every time.
                    boolean[] reshown = {false};
                    RESHOWN.set(new ReshowTracker(bukkitPlayer.getUniqueId(), reshown));
                    try {
                        ScriptContext base = dm != null ? dm.buildScriptContext() : genericBaseContext();
                        if (base != null) {
                            ScriptContext ctx = ScriptContext.builder().copyFrom(base).player(sp).build();
                            List<ScriptValue> extra = new ArrayList<>(keys.size() + 1);
                            java.util.LinkedHashMap<String, ScriptValue> results = new java.util.LinkedHashMap<>();
                            for (InputSpec spec : keys) {
                                ScriptValue value = readValue(view, spec);
                                extra.add(value);
                                results.put(spec.key(), value);
                            }
                            extra.add(dev.arubik.craftengine.script.types.primitive.MapType.wrap(results));
                            ScriptCall call = ScriptCall.parse(actionRef);
                            if (call != null) call.executeWithExtraArgs(ctx, extra);
                        }
                    } catch (Throwable ignored) {
                    } finally {
                        boolean alreadyReshown = reshown[0];
                        RESHOWN.remove();
                        if (onCloseRef != null && !onCloseRef.isBlank()) {
                            // Script opted out of the default "reopen the menu" behavior — run its own
                            // fallback instead (or nothing, if it just wants the dialog to close).
                            try {
                                ScriptCall closeCall = ScriptCall.parse(onCloseRef);
                                if (closeCall != null) {
                                    ScriptContext closeBase = dm != null ? dm.buildScriptContext() : genericBaseContext();
                                    if (closeBase != null) {
                                        ScriptContext closeCtx = ScriptContext.builder().copyFrom(closeBase).player(sp).build();
                                        closeCall.execute(closeCtx);
                                    }
                                }
                            } catch (Throwable ignored) {}
                            if (!alreadyReshown) bukkitPlayer.closeDialog();
                        } else if (dm != null && dm.definition() != null && dm.definition().openUi()) {
                            // Default when the script didn't ask for anything else: a dialog opened
                            // FROM a machine's own menu (e.g. a button) returns TO that menu, matching
                            // Machine.update()'s "stay on the same open menu" convention elsewhere.
                            try { dm.openPage(bukkitPlayer, dm.currentPage()); }
                            catch (Throwable ignored) { bukkitPlayer.closeDialog(); }
                        } else if (!alreadyReshown) {
                            bukkitPlayer.closeDialog();
                        }
                    }
                },
                ClickCallback.Options.builder().uses(1).build()
        );
    }

    /** Baseline context for a machine-less dialog action — the same namespace singletons every
     *  OTHER non-machine script entry point this session binds (Cmd execution, TaskManager, the
     *  generic events bridge, script menus), so a {@code /cmds}-opened dialog's Accept button can
     *  do anything those can (schedule a task, register a countdown, open a menu, build an item). */
    private static ScriptContext genericBaseContext() {
        ScriptContext.Builder b = ScriptContext.builder();
        b.typedAll(dev.arubik.craftengine.script.ScriptBootstrap.globalSingletons());
        return b.build();
    }

}
