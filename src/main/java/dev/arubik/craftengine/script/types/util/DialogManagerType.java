package dev.arubik.craftengine.script.types.util;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
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
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

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

        Builder(String title) { this.title = Component.text(title); }
    }

    public static void register() {
        PolyTypeRegistry.define("Dialog")
            .method("base", (obj, args) -> {
                if (!DialogSupport.isAvailable() || args.isEmpty()) return ScriptValue.NULL;
                return ScriptValue.ofObj("DialogBuilder", new Builder(args.get(0).asStr()));
            });

        PolyTypeRegistry.define("DialogBuilder")
            // can_close_with_escape(bool)
            .method("can_close_with_escape", (obj, args) -> {
                Builder b = builder(obj);
                b.canCloseWithEscape = args.isEmpty() || args.get(0).asBool();
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // body(text) — a plain message line. Call multiple times for multiple paragraphs.
            .method("body", (obj, args) -> {
                Builder b = builder(obj);
                if (!args.isEmpty()) b.body.add(DialogBody.plainMessage(mm(args.get(0).asStr())));
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // body_item(item) — shows an ItemStack in the dialog body.
            .method("body_item", (obj, args) -> {
                Builder b = builder(obj);
                if (!args.isEmpty() && args.get(0) instanceof ScriptValue.Item it) {
                    try {
                        org.bukkit.inventory.ItemStack bukkit =
                                org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(it.stack());
                        b.body.add(DialogBody.item(bukkit).build());
                    } catch (Throwable ignored) {}
                }
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // input_text(key, label, initial?, width?)
            .method("input_text", (obj, args) -> {
                Builder b = builder(obj);
                if (args.size() >= 2) {
                    String key = args.get(0).asStr();
                    var tb = DialogInput.text(key, mm(args.get(1).asStr()));
                    if (args.size() >= 3) tb.initial(args.get(2).asStr());
                    if (args.size() >= 4) tb.width((int) args.get(3).asNum());
                    b.inputs.add(tb.build());
                    b.specs.add(new InputSpec(key, Kind.TEXT));
                }
                return ScriptValue.ofObj("DialogBuilder", b);
            })
            // input_bool(key, label, initial?)
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
            // as_notice(machine, button_label, action_ref) — single-button dialog. Accept calls
            // action_ref with every declared input's value appended, in declaration order.
            .method("as_notice", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                Builder b = builder(obj);
                DialogAction action = buildAction(machineOf(args.get(0)), args.get(2).asStr(), b.specs);
                DialogBase base = base(b);
                DialogType type = DialogType.notice(ActionButton.builder(mm(args.get(1).asStr())).action(action).build());
                return wrapDialog(base, type);
            })
            // as_confirmation(machine, accept_label, cancel_label, accept_action_ref, cancel_action_ref?)
            .method("as_confirmation", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.NULL;
                Builder b = builder(obj);
                MachineType.MachineRef m = machineOf(args.get(0));
                DialogAction yes = buildAction(m, args.get(3).asStr(), b.specs);
                DialogAction no = args.size() >= 5 && !args.get(4).asStr().isBlank()
                        ? buildAction(m, args.get(4).asStr(), b.specs)
                        : DialogAction.customClick((view, audience) -> { if (audience instanceof Player p) p.closeDialog(); },
                            ClickCallback.Options.builder().uses(1).build());
                DialogBase base = base(b);
                DialogType type = DialogType.confirmation(
                        ActionButton.builder(mm(args.get(1).asStr())).action(yes).build(),
                        ActionButton.builder(mm(args.get(2).asStr())).action(no).build());
                return wrapDialog(base, type);
            })
            // as_multi_action(machine, buttons) — buttons is an Array of maps built with
            // make_map("label", "...", "action", "file.pf:func"). Every button gets whatever
            // inputs were declared appended as arguments, same as as_notice.
            .method("as_multi_action", (obj, args) -> {
                if (args.size() < 2 || !(args.get(1) instanceof ScriptValue.Array arr)) return ScriptValue.NULL;
                Builder b = builder(obj);
                MachineType.MachineRef m = machineOf(args.get(0));
                List<ActionButton> buttons = new ArrayList<>();
                for (ScriptValue v : arr.elements()) {
                    if (!(v instanceof ScriptValue.Obj mo) || !(mo.instance() instanceof java.util.Map<?, ?> map)) continue;
                    Object labelV = map.get("label");
                    Object actionV = map.get("action");
                    String label = labelV instanceof ScriptValue sv ? sv.asStr() : String.valueOf(labelV);
                    String actionRef = actionV instanceof ScriptValue sv ? sv.asStr() : String.valueOf(actionV);
                    buttons.add(ActionButton.builder(mm(label)).action(buildAction(m, actionRef, b.specs)).build());
                }
                DialogBase base = base(b);
                DialogType type = DialogType.multiAction(buttons).build();
                return wrapDialog(base, type);
            });

        PolyTypeRegistry.define("BuiltDialog")
            // show(player) — display to that player.
            .method("show", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                Player p = playerOf(args.get(0));
                if (p == null || !(dialogOf(obj) instanceof Dialog d)) return ScriptValue.of(false);
                try { p.showDialog(d); return ScriptValue.of(true); } catch (Throwable ignored) { return ScriptValue.of(false); }
            });
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
     *  fresh script context bound to {@code machine} + that REAL player (never a guess), and calls
     *  actionRef with every declared input's submitted value appended in declaration order, PLUS a
     *  trailing Map argument (key -> value, for every declared input) for named access — a callback
     *  that only cares about one of several inputs, or that's shared across dialogs with different
     *  input sets, can read {@code results.get("key")} instead of relying on positional order. */
    private static DialogAction buildAction(MachineType.MachineRef machine, String actionRef, List<InputSpec> specs) {
        List<InputSpec> keys = List.copyOf(specs);
        return DialogAction.customClick(
                (view, audience) -> {
                    if (!(audience instanceof Player bukkitPlayer)) return;
                    if (machine == null || !(machine.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) {
                        bukkitPlayer.closeDialog();
                        return;
                    }
                    try {
                        ServerPlayer sp = ((CraftPlayer) bukkitPlayer).getHandle();
                        ScriptContext base = dm.buildScriptContext();
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
                        // A dialog opened FROM a machine's own menu (e.g. a button) should return
                        // TO that menu on accept/cancel, not just close to nothing — reopening the
                        // same page both closes the dialog (the client can only show one such view
                        // at a time) and restores context, matching Machine.update()'s "stay on the
                        // same open menu" convention elsewhere in this script API.
                        try { dm.openPage(bukkitPlayer, dm.currentPage()); }
                        catch (Throwable ignored) { bukkitPlayer.closeDialog(); }
                    }
                },
                ClickCallback.Options.builder().uses(1).build()
        );
    }

}
