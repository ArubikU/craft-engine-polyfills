package dev.arubik.craftengine.crafting;

/**
 * The role a chest slot plays in a crafting menu. Drives the framework's
 * interaction policy:
 * <ul>
 *   <li>{@link #INPUT} — editable; part of the recipe grid; changes trigger
 *       recompute; consumed on craft.</li>
 *   <li>{@link #OUTPUT} — read-only preview; clicking it crafts (take/shift).</li>
 *   <li>{@link #BACKGROUND} — static filler; never interactive.</li>
 *   <li>{@link #CUSTOM} — subclass-owned (fuel, fluid, buttons, ...). The base
 *       defers all behavior for these to subclass hooks.</li>
 * </ul>
 */
public enum SlotRole {
    INPUT,
    OUTPUT,
    BACKGROUND,
    CUSTOM
}
