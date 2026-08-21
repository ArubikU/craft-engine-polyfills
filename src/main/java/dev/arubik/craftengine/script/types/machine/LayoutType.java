package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

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

            .method("lock", (obj, a) -> setLock(obj, a, true))
            .method("unlock", (obj, a) -> setLock(obj, a, false))

            .method("is_locked", (obj, a) -> {
                MachineLayout l = ref(obj).layout();
                if (l == null || a.isEmpty()) return ScriptValue.of(false);
                return ScriptValue.of(l.isLocked((int) a.get(0).asNum()));
            })

            /** Locks every slot of a kind: "input", "output", "fuel", "upgrade", ... */
            .method("lock_type", (obj, a) -> setLockByType(obj, a, true))
            .method("unlock_type", (obj, a) -> setLockByType(obj, a, false))

            .method("unlock_all", (obj, a) -> {
                MachineLayout l = ref(obj).layout();
                if (l == null) return ScriptValue.of(false);
                for (int s : l.getLockedSlots()) l.setLocked(s, false);
                return ScriptValue.of(true);
            })

            /** The kind of a slot, as a lowercase name; empty when nothing is open. */
            .method("slot_type", (obj, a) -> {
                MachineLayout l = ref(obj).layout();
                if (l == null || a.isEmpty()) return ScriptValue.of("");
                MenuSlotType t = l.getSlotType((int) a.get(0).asNum());
                return ScriptValue.of(t == null ? "" : t.name().toLowerCase(Locale.ROOT));
            })

            /** Every slot of a kind. */
            .method("slots_of_type", (obj, a) -> {
                MachineLayout l = ref(obj).layout();
                List<ScriptValue> out = new ArrayList<>();
                if (l == null || a.isEmpty()) return new ScriptValue.Array(out);
                MenuSlotType t = slotType(a.get(0).asStr());
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

    private static ScriptValue setLockByType(Object obj, List<ScriptValue> a, boolean lock) {
        MachineLayout l = ref(obj).layout();
        if (l == null || a.isEmpty()) return ScriptValue.of(false);
        MenuSlotType t = slotType(a.get(0).asStr());
        if (t == null) return ScriptValue.of(false);
        for (int s : l.getSlotsOfType(t)) l.setLocked(s, lock);
        return ScriptValue.of(true);
    }

    public static ScriptValue wrap(MachineType.MachineRef machine) {
        return machine == null ? ScriptValue.NULL : ScriptValue.ofObj("Layout", new LayoutRef(machine));
    }

    private static LayoutRef ref(Object obj) {
        return (LayoutRef) obj;
    }
}
