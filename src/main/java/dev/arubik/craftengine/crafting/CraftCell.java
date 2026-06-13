package dev.arubik.craftengine.crafting;

import java.util.Objects;

import net.momirealms.craftengine.core.util.Key;

/**
 * A single, immutable cell of a {@link CraftingGrid}: either empty, or an item
 * identified by a craft-engine {@link Key} plus a count.
 *
 * <p>This is a pure value type with no Bukkit/NMS dependency so the matching
 * logic built on top of it is fully unit-testable on the plain JVM. Adapters
 * (e.g. from {@code org.bukkit.inventory.ItemStack}) live in the Bukkit-facing
 * classes and convert into {@code CraftCell} before matching.
 */
public final class CraftCell {

    /** The canonical empty cell. */
    public static final CraftCell EMPTY = new CraftCell(null, 0);

    private final Key id;
    private final int count;

    private CraftCell(Key id, int count) {
        this.id = id;
        this.count = count;
    }

    /** Creates a non-empty cell. A null id or count &lt;= 0 collapses to {@link #EMPTY}. */
    public static CraftCell of(Key id, int count) {
        if (id == null || count <= 0) {
            return EMPTY;
        }
        return new CraftCell(id, count);
    }

    /** Convenience for a single item. */
    public static CraftCell of(Key id) {
        return of(id, 1);
    }

    public boolean isEmpty() {
        return id == null;
    }

    /** The item id, or null if {@link #isEmpty()}. */
    public Key id() {
        return id;
    }

    public int count() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CraftCell other)) {
            return false;
        }
        return count == other.count && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, count);
    }

    @Override
    public String toString() {
        return isEmpty() ? "CraftCell.EMPTY" : "CraftCell[" + id.asString() + " x" + count + "]";
    }
}
