package dev.arubik.craftengine.crafting;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Immutable bundle of the whole UI state handed to a recipe's optional
 * {@link RecipeCondition} and {@link RecipeExecutor} hooks. It carries enough
 * context for a recipe to make a gating decision (condition) or run a custom
 * side-effect (executor) without the recipe having to reach into the menu's
 * internals.
 *
 * <p>Created by {@link WorkbenchMenu} right before a craft is committed. The
 * {@code tool} stack is the live item in the CUSTOM tool slot (may be null if
 * the recipe required no tool); {@code crafts} is the number of crafts about to
 * run (1 for a normal take, &gt;1 for a shift-click bulk craft).
 */
public final class CraftContext {

    private final CraftingGrid grid;
    private final ItemStack tool;
    private final int crafts;
    private final Player player;
    private final AbstractCraftingMenu menu;

    public CraftContext(CraftingGrid grid, ItemStack tool, int crafts, Player player, AbstractCraftingMenu menu) {
        this.grid = grid;
        this.tool = tool;
        this.crafts = crafts;
        this.player = player;
        this.menu = menu;
    }

    public CraftingGrid grid() {
        return grid;
    }

    /** Live item in the tool slot, or null if absent / the recipe needs none. */
    public ItemStack tool() {
        return tool;
    }

    public int crafts() {
        return crafts;
    }

    public Player player() {
        return player;
    }

    public AbstractCraftingMenu menu() {
        return menu;
    }
}
