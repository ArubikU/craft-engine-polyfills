package dev.arubik.craftengine.machine.menu.layout;

public enum MenuSlotType {
    INPUT,
    OUTPUT,
    FUEL, // Fuel slot
    UPGRADE, // Upgrade module slot
    DYNAMIC, // Visual only, updates frequently
    BUTTON, // Interactive Button
    BACKGROUND, // Static Decoration
    /** A clickable "identity marker" slot: renders a representative item (from a script `get`
     * hook) but NEVER actually holds a real, transferable ItemStack in the underlying container —
     * clicking it only reads the cursor item's id and calls a script `set` hook, the held item
     * itself is never touched/consumed/duplicated. Built for item-filter-style UIs (see
     * item_pipe_panel.pf) where the old approach — real INPUT slots re-populated from saved flags
     * — let a player walk off with a "filter marker" item that was only ever supposed to represent
     * a type, then have it re-conjured again next visit: a repeatable item duplication exploit. */
    GHOST,
    /** A plain, unrestricted slot — place/take/stack freely, like a normal chest slot. No
     * machine ever declares this (INPUT/OUTPUT/FUEL/UPGRADE cover every machine case), but an
     * item-behavior page (a backpack's storage) has no recipe-shaped slots to begin with, just
     * free storage — see {@code dev.arubik.craftengine.item.menu.ItemMenu}. */
    STORAGE
}
