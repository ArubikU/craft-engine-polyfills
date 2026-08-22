package dev.arubik.craftengine.script.types.event;

import java.util.ArrayList;
import java.util.List;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.event.BreakEvent;
import dev.arubik.craftengine.script.event.ButtonEvent;
import dev.arubik.craftengine.script.event.FormEvent;
import dev.arubik.craftengine.script.event.GhostSlotEvent;
import dev.arubik.craftengine.script.event.InteractEvent;
import dev.arubik.craftengine.script.event.ItemActionEvent;
import dev.arubik.craftengine.script.event.RenderEvent;
import dev.arubik.craftengine.script.event.ScriptEvent;
import dev.arubik.craftengine.script.event.TransferEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

/**
 * Registers the {@code Event} base PolyType and every concrete subtype (each declared as
 * {@code PolyTypeRegistry.define("XEvent", "Event")}, mirroring how {@code Player} extends
 * {@code LivingEntity}). Every {@code on_*} script hook — machines, multiblocks (which reuse the
 * exact same machine hooks, see {@code CelledDataMachineBehavior}), and items alike — and every
 * button/ghost-slot menu interaction binds one of these as the lowercase {@code event} variable,
 * giving every hook the SAME base shape ({@code event.type}, {@code event.cancelled},
 * {@code event.cancel()}) instead of each one inventing its own ad-hoc convention (the old
 * {@code Machine.set_flag("_transfer_cancel", 1)} veto, or — for most item hooks — no veto
 * mechanism at all). See each {@code ScriptEvent} subclass's javadoc for what it adds and, where
 * relevant, what older convention it deprecates (kept working alongside the new one, not removed).
 */
public final class EventType {
    private EventType() {}

    public static void register() {
        PolyTypeRegistry.define("Event")
            .property("type", obj -> ScriptValue.of(event(obj).type()))
            .property("cancelled", obj -> ScriptValue.of(event(obj).isCancelled()))
            .method("cancel", (obj, args) -> { event(obj).setCancelled(true); return ScriptValue.of(true); })
            .method("set_cancelled", (obj, args) -> {
                event(obj).setCancelled(!args.isEmpty() && args.get(0).asBool());
                return ScriptValue.of(true);
            });

        PolyTypeRegistry.define("BreakEvent", "Event")
            .property("drops", obj -> {
                List<ScriptValue> out = new ArrayList<>();
                for (ItemStack s : ((BreakEvent) obj).drops()) out.add(ScriptValue.ofItem(s));
                return new ScriptValue.Array(out);
            })
            // set_drops(item, item, ...) — replaces the DEFAULT drops (this event's own container
            // contents) with exactly the given items. Every arg is treated as one drop, so both
            // event.set_drops(one_item) and event.set_drops(a, b, c) work with no array-literal
            // syntax required.
            .method("set_drops", (obj, args) -> {
                List<ItemStack> drops = new ArrayList<>();
                for (ScriptValue v : args) {
                    if (v instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
                        drops.add(i.stack());
                    } else if (v instanceof ScriptValue.Array arr) {
                        for (ScriptValue elem : arr.elements()) {
                            if (elem instanceof ScriptValue.Item ei && ei.stack() != null && !ei.stack().isEmpty()) {
                                drops.add(ei.stack());
                            }
                        }
                    }
                }
                ((BreakEvent) obj).setDrops(drops);
                return ScriptValue.of(true);
            });

        // Generic item-hook event (on_right_click, on_use, on_drop, on_pickup, ...) — see
        // ItemActionEvent's javadoc. event.type carries the hook name so a script that shares one
        // function across several hooks can branch on it; other_entity/amount are null/0 when the
        // firing hook doesn't have one.
        PolyTypeRegistry.define("ItemActionEvent", "Event")
            .property("other_entity", obj -> {
                var e = ((ItemActionEvent) obj).otherEntity();
                return e == null ? ScriptValue.NULL
                        : dev.arubik.craftengine.script.types.entity.EntityType.wrap(e);
            })
            .property("amount", obj -> {
                Double a = ((ItemActionEvent) obj).amount();
                return ScriptValue.of(a == null ? 0.0 : a);
            });

        // Right/left-click interactions — machines' on_right_click/on_left_click and items' click
        // hooks alike.
        PolyTypeRegistry.define("InteractEvent", "Event")
            .property("hand", obj -> {
                String h = ((InteractEvent) obj).hand();
                return h == null ? ScriptValue.NULL : ScriptValue.of(h);
            });

        // on_pipe_transfer — read-only mirror of the type/payload/direction/mode already bound as
        // separate classes (kept for back-compat), plus a real cancel().
        PolyTypeRegistry.define("TransferEvent", "Event")
            .property("transfer_type", obj -> ScriptValue.of(((TransferEvent) obj).transferType()))
            .property("payload", obj -> ((TransferEvent) obj).payload())
            .property("direction", obj -> ScriptValue.of(((TransferEvent) obj).direction()))
            .property("mode", obj -> ScriptValue.of(((TransferEvent) obj).mode()));

        // A menu button's "file.pf:function" script action.
        PolyTypeRegistry.define("ButtonEvent", "Event")
            .property("slot", obj -> ScriptValue.of(((ButtonEvent) obj).slot()))
            .property("click_type", obj -> ScriptValue.of(((ButtonEvent) obj).clickType()));

        // A GHOST slot's set-script — cancel() rejects the click (see MenuSlotType#GHOST).
        PolyTypeRegistry.define("GhostSlotEvent", "Event")
            .property("slot", obj -> ScriptValue.of(((GhostSlotEvent) obj).slot()))
            .property("clicked_id", obj -> ScriptValue.of(((GhostSlotEvent) obj).clickedId()))
            .property("click_type", obj -> ScriptValue.of(((GhostSlotEvent) obj).clickType()));

        // A multiblock's on_form/on_disassemble — event.type distinguishes the two.
        PolyTypeRegistry.define("FormEvent", "Event");

        // on_render — see RenderEvent's javadoc for why cancel() is a no-op here.
        PolyTypeRegistry.define("RenderEvent", "Event")
            .property("holder", obj -> {
                Entity e = ((RenderEvent) obj).holder();
                return e == null ? ScriptValue.NULL : dev.arubik.craftengine.script.types.entity.EntityType.wrap(e);
            })
            .property("slot", obj -> ScriptValue.of(((RenderEvent) obj).slot()));
    }

    public static ScriptValue wrap(ScriptEvent e) {
        if (e == null) return ScriptValue.NULL;
        String typeName = switch (e) {
            case BreakEvent ignored -> "BreakEvent";
            case ItemActionEvent ignored -> "ItemActionEvent";
            case InteractEvent ignored -> "InteractEvent";
            case TransferEvent ignored -> "TransferEvent";
            case ButtonEvent ignored -> "ButtonEvent";
            case GhostSlotEvent ignored -> "GhostSlotEvent";
            case FormEvent ignored -> "FormEvent";
            case RenderEvent ignored -> "RenderEvent";
            default -> "Event";
        };
        return ScriptValue.ofObj(typeName, e);
    }

    private static ScriptEvent event(Object obj) { return (ScriptEvent) obj; }
}
