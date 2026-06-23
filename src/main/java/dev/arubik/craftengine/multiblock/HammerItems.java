package dev.arubik.craftengine.multiblock;

import java.util.List;

import net.momirealms.craftengine.core.util.Key;

/** The engineer's-hammer item ids (low→high tier) used to assemble multiblocks. */
public final class HammerItems {

    private HammerItems() {
    }

    public static final Key WOODEN = Key.of("cml", "wooden_hammer");
    public static final Key STONE = Key.of("cml", "stone_hammer");
    public static final Key COPPER = Key.of("cml", "copper_hammer");
    public static final Key IRON = Key.of("cml", "iron_hammer");
    public static final Key GOLDEN = Key.of("cml", "golden_hammer");
    public static final Key DIAMOND = Key.of("cml", "diamond_hammer");
    public static final Key NETHERITE = Key.of("cml", "netherite_hammer");
    public static final Key STEEL = Key.of("cml", "steel_hammer");
    public static final Key ALUMINUM = Key.of("cml", "aluminum_hammer");

    /** All hammers in ascending tier order. */
    public static final List<Key> ALL = List.of(WOODEN, STONE, COPPER, ALUMINUM, IRON, STEEL, GOLDEN, DIAMOND,
            NETHERITE);

    public static boolean isHammer(Key id) {
        return id != null && ALL.contains(id);
    }
}
