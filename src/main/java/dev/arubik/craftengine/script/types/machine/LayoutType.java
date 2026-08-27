package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * "Layout" type — runtime control over the open menu, reachable as {@code Machine.layout}.
 *
 * <p>The menu is described in the machine JSON, but which of its slots a player may use often
 * depends on state the JSON cannot see: a filter bay that only unlocks at a certain tier, an
 * upgrade slot gated behind a recipe, an output the machine wants to hold shut mid-craft.
 *
 * <pre>
 *   Machine.layout.lock(13)                       # pin slot 13: its contents become immovable
 *   Machine.layout.unlock(13)
 *   if Machine.layout.is_locked(13) { ... }
 *   Machine.layout.lock_type("upgrade")           # pin every upgrade slot at once
 *   Machine.layout.unlock_all()
 * </pre>
 *
 * <p>Locking is independent of a slot's {@link MenuSlotType} on purpose: the slot usually IS a
 * real input/output/fuel/upgrade that the machine still tracks, and turning it into decoration
 * would take it out of that bookkeeping. A locked slot simply refuses every player interaction —
 * direct clicks, shift-clicks and drags alike.
 */
public final class LayoutType {

    /** Instance object: the machine whose menu this is. */
    public record LayoutRef(MachineType.MachineRef machine) {

        /** The layout of the currently open menu, or null when nothing is open. */
        MachineLayout layout() {
            if (!(machine.blockEntity() instanceof DataMachineBlockEntity dm)) return null;
            MachineMenu menu = dm.getMenu();
            return menu == null ? null : menu.getLayout();
        }
    }

    private LayoutType() {}

    private static MenuSlotType slotType(String name) {
        try {
            return MenuSlotType.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static void register() {
        PolyTypeRegistry.define("Layout")
            .property("open", obj -> ScriptValue.of(ref(obj).layout() != null))
            .property("size", obj -> {
                MachineLayout l = ref(obj).layout();
                return ScriptValue.of(l == null ? 0 : l.getSize());
            })
            .property("locked_slots", obj -> {
                MachineLayout l = ref(obj).layout();
                List<ScriptValue> out = new ArrayList<>();
                if (l != null) for (int s : l.getLockedSlots()) out.add(ScriptValue.of(s));
                return new ScriptValue.Array(out);
            })

            // NOT migrated: "lock"/"unlock" accept EITHER a single slot number OR an Array of them
            // (see setLock's `a.get(0) instanceof ScriptValue.Array` branch) — a multi-shape dynamic
            // dispatch on the argument's own runtime type, which a typed handler (one fixed TypeCodec
            // per argument) can't express. Left untyped.
            .method("lock", (obj, a) -> setLock(obj, a, true))
            .method("unlock", (obj, a) -> setLock(obj, a, false))

            .methodTyped1("is_locked", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (LayoutRef ref, Double slot) -> {
                    MachineLayout l = ref.layout();
                    if (l == null) return false;
                    return l.isLocked(slot.intValue());
                })

            /** Locks every slot of a kind: "input", "output", "fuel", "upgrade", ... */
            .methodTyped1("lock_type", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (LayoutRef ref, String name) -> {
                    MachineLayout l = ref.layout();
                    if (l == null) return false;
                    MenuSlotType t = slotType(name);
                    if (t == null) return false;
                    for (int s : l.getSlotsOfType(t)) l.setLocked(s, true);
                    return true;
                })
            .methodTyped1("unlock_type", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (LayoutRef ref, String name) -> {
                    MachineLayout l = ref.layout();
                    if (l == null) return false;
                    MenuSlotType t = slotType(name);
                    if (t == null) return false;
                    for (int s : l.getSlotsOfType(t)) l.setLocked(s, false);
                    return true;
                })

            .methodTyped0("unlock_all", TypeCodecs.BOOL,
                (LayoutRef ref) -> {
                    MachineLayout l = ref.layout();
                    if (l == null) return false;
                    for (int s : l.getLockedSlots()) l.setLocked(s, false);
                    return true;
                })

            /** The kind of a slot, as a lowercase name; empty when nothing is open. */
            .methodTyped1("slot_type", TypeCodecs.DOUBLE, TypeCodecs.STRING, "",
                (LayoutRef ref, Double slot) -> {
                    MachineLayout l = ref.layout();
                    if (l == null) return "";
                    MenuSlotType t = l.getSlotType(slot.intValue());
                    return t == null ? "" : t.name().toLowerCase(Locale.ROOT);
                })

            /** Every slot of a kind. */
            .methodTyped1("slots_of_type", TypeCodecs.STRING, TypeCodecs.RAW, new ScriptValue.Array(new ArrayList<>()),
                (LayoutRef ref, String name) -> {
                    MachineLayout l = ref.layout();
                    List<ScriptValue> out = new ArrayList<>();
                    if (l == null) return new ScriptValue.Array(out);
                    MenuSlotType t = slotType(name);
                    if (t == null) return new ScriptValue.Array(out);
                    for (int s : l.getSlotsOfType(t)) out.add(ScriptValue.of(s));
                    return new ScriptValue.Array(out);
                });
    }

    private static ScriptValue setLock(Object obj, List<ScriptValue> a, boolean lock) {
        MachineLayout l = ref(obj).layout();
        if (l == null || a.isEmpty()) return ScriptValue.of(false);
        // Accept a single slot or an array of them, so a script can pin a whole bay in one call.
        if (a.get(0) instanceof ScriptValue.Array arr) {
            for (ScriptValue v : arr.elements()) l.setLocked((int) v.asNum(), lock);
            return ScriptValue.of(true);
        }
        l.setLocked((int) a.get(0).asNum(), lock);
        return ScriptValue.of(true);
    }

    // setLockByType(Object, List<ScriptValue>, boolean) — the old lock_type/unlock_type helper —
    // was removed: both methods are now methodTyped1 with TypeCodecs.STRING, whose decode() already
    // performs the exact same asStr() coercion, and the a.isEmpty() short-circuit is now handled by
    // onMissingArgs, so the helper became dead code (same reasoning as ContraptionType's removed
    // holderKey() helper).

    public static ScriptValue wrap(MachineType.MachineRef machine) {
        return machine == null ? ScriptValue.NULL : ScriptValue.ofObj("Layout", new LayoutRef(machine));
    }

    private static LayoutRef ref(Object obj) {
        return (LayoutRef) obj;
    }
}
