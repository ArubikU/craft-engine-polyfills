package dev.arubik.craftengine.contraption;

/**
 * The 3 bearing kinds (CONTRAPTIONS.md §1 "Assembly/disassembly", Phase 6): LINEAR and
 * ROTATIONAL are block-anchored (the bearing block itself stays fixed in the real world
 * forever), MINECART is entity-anchored (a real vanilla minecart carries the contraption
 * along a rail).
 */
public enum BearingType {
    LINEAR,
    ROTATIONAL,
    MINECART
}
