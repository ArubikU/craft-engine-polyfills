package dev.arubik.craftengine.crafting;

import java.util.Objects;

import net.momirealms.craftengine.core.util.Key;

/**
 * A recipe ingredient: a required item {@link Key} and a minimum count a single
 * grid cell must supply. An "empty" ingredient matches only empty cells and is
 * used to express the gaps inside a shaped pattern.
 *
 * <p>Pure value type, unit-testable without Bukkit. Matching is purely
 * {@code id} equality plus a {@code count >= required} check, so the same
 * ingredient works for a 2D or a future 3D grid.
 */
public final class CraftIngredient {

    /** Matches only an empty cell. */
    public static final CraftIngredient EMPTY = new CraftIngredient(null, 0);

    private final Key id;
    private final int count;

    private CraftIngredient(Key id, int count) {
        this.id = id;
        this.count = count;
    }

    public static CraftIngredient of(Key id, int count) {
        if (id == null || count <= 0) {
            return EMPTY;
        }
        return new CraftIngredient(id, count);
    }

    public static CraftIngredient of(Key id) {
        return of(id, 1);
    }

    public boolean isEmpty() {
        return id == null;
    }

    public Key id() {
        return id;
    }

    public int count() {
        return count;
    }

    /** True if {@code cell} satisfies this ingredient (id match + enough count). */
    public boolean matches(CraftCell cell) {
        if (isEmpty()) {
            return cell.isEmpty();
        }
        if (cell.isEmpty()) {
            return false;
        }
        return id.equals(cell.id()) && cell.count() >= count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CraftIngredient other)) {
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
        return isEmpty() ? "CraftIngredient.EMPTY" : "CraftIngredient[" + id.asString() + " x" + count + "]";
    }
}
