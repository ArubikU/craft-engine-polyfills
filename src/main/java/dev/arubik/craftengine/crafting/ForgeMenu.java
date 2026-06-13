package dev.arubik.craftengine.crafting;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A minimal EXTENSIBILITY SAMPLE proving the framework supports custom slots: a
 * 2x2 crafting grid plus one CUSTOM "fuel" slot. A craft is only allowed when
 * the fuel slot holds at least {@code crafts} pieces of coal, and the fuel is
 * consumed in {@link #onCraft}. The 2x2 recipes live in their own registry
 * bucket (2x2), entirely reusing the base matching/UI.
 *
 * <p>This sketch shows the three subclass hooks a developer overrides for
 * arbitrary extra-slot logic: {@link #isCustomCraftAllowed}, {@link #onCraft},
 * and (implicitly) {@link #findMatch} for the recipe source.
 */
public final class ForgeMenu extends AbstractCraftingMenu {

    /** Chest slot index of the fuel slot in this layout. */
    private static final int FUEL_SLOT = 8;

    private final CraftingRecipeRegistry registry;

    public ForgeMenu(String title, CraftingRecipeRegistry registry) {
        super(buildLayout(), title);
        this.registry = registry != null ? registry : CraftingRecipeRegistry.global();
        recompute();
    }

    private static SlotLayout buildLayout() {
        // One chest row of 9 is enough: 2x2 grid at (0,0)-(1,1) over rows 0-1,
        // a fuel slot, and an output. Use a 3-row chest for breathing room.
        SlotLayout.Builder b = SlotLayout.builder(27).grid(2, 2);
        b.input(0, 0, 0).input(1, 1, 0);
        b.input(9, 0, 1).input(10, 1, 1);
        b.custom(FUEL_SLOT); // fuel
        b.output(6);
        return b.build();
    }

    @Override
    protected Optional<CraftingRecipeLike> findMatch(CraftingGrid grid) {
        return registry.match(grid);
    }

    @Override
    protected boolean isCustomCraftAllowed(CraftingGrid grid, CraftingRecipeLike recipe, int crafts) {
        ItemStack fuel = getInventory().getItem(FUEL_SLOT);
        return fuel != null && fuel.getType() == Material.COAL && fuel.getAmount() >= crafts;
    }

    @Override
    protected void onCraft(CraftingRecipeLike recipe, int crafts) {
        ItemStack fuel = getInventory().getItem(FUEL_SLOT);
        if (fuel != null && fuel.getType() == Material.COAL) {
            int amt = fuel.getAmount() - crafts;
            if (amt <= 0) {
                getInventory().setItem(FUEL_SLOT, null);
            } else {
                fuel.setAmount(amt);
            }
        }
    }
}
