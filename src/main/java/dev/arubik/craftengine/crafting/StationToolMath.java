package dev.arubik.craftengine.crafting;

/**
 * Pure, Bukkit-free math for the workbench TOOL slot: how many uses a tool has
 * left given vanilla durability, whether a number of crafts is affordable, and
 * the resulting damage after consuming uses. Kept free of Bukkit/NMS so the
 * (easy-to-get-wrong) durability arithmetic is unit-testable on the plain JVM.
 *
 * <p>Vanilla durability model: an item has a {@code maxDurability} and a current
 * {@code damage} in {@code [0, maxDurability]}. Each "use" adds
 * {@code usesPerCraft} damage. The tool breaks when {@code damage >= maxDurability}.
 * Remaining uses is therefore {@code ceil((maxDurability - damage) / usesPerCraft)}
 * — the last partial use still counts and breaks the tool.
 */
public final class StationToolMath {

    private StationToolMath() {
    }

    /**
     * Remaining whole crafts a tool can perform.
     *
     * @param maxDurability item max durability (&gt; 0 for a damageable tool)
     * @param damage        current damage in {@code [0, maxDurability]}
     * @param usesPerCraft  damage applied per craft (&gt;= 1)
     * @return number of crafts this tool still allows (0 if broken/non-damageable input)
     */
    public static int remainingUses(int maxDurability, int damage, int usesPerCraft) {
        if (maxDurability <= 0 || usesPerCraft <= 0) {
            return 0;
        }
        int left = maxDurability - Math.max(0, damage);
        if (left <= 0) {
            return 0;
        }
        return (left + usesPerCraft - 1) / usesPerCraft; // ceil
    }

    /** True if the tool can afford {@code crafts} crafts at {@code usesPerCraft} each. */
    public static boolean canAfford(int maxDurability, int damage, int usesPerCraft, int crafts) {
        return crafts > 0 && remainingUses(maxDurability, damage, usesPerCraft) >= crafts;
    }

    /**
     * The new damage value after consuming {@code crafts} crafts, clamped to
     * {@code maxDurability}. If the result reaches {@code maxDurability} the caller
     * should treat the tool as broken (cleared).
     */
    public static int damageAfter(int maxDurability, int damage, int usesPerCraft, int crafts) {
        if (crafts <= 0 || usesPerCraft <= 0) {
            return Math.max(0, damage);
        }
        long next = (long) Math.max(0, damage) + (long) usesPerCraft * crafts;
        return (int) Math.min(maxDurability, next);
    }

    /** True if {@code damageAfter} would break the tool (reach max durability). */
    public static boolean breaksAfter(int maxDurability, int damage, int usesPerCraft, int crafts) {
        return maxDurability > 0 && damageAfter(maxDurability, damage, usesPerCraft, crafts) >= maxDurability;
    }
}
