package dev.arubik.craftengine.crafting;

import java.util.Optional;

/**
 * The default W x H crafting-table menu, now a thin subclass of
 * {@link AbstractCraftingMenu} that proves the reusable base: it only declares
 * its slot layout (a contiguous WxH input block + one OUTPUT slot) and points
 * the base at a {@link CraftingRecipeRegistry}. All recompute / take / shift
 * math lives in the base.
 *
 * <p>Layout (chest, 9 columns): input grid occupies columns {@code 0..W-1} of
 * rows {@code 0..H-1}; the OUTPUT slot sits at column 6 of the middle input row.
 */
public final class CraftingTableMenu extends AbstractCraftingMenu {

    private final CraftingRecipeRegistry registry;

    public CraftingTableMenu(int gridWidth, int gridHeight, String title, CraftingRecipeRegistry registry) {
        super(buildLayout(gridWidth, gridHeight), title);
        this.registry = registry != null ? registry : CraftingRecipeRegistry.global();
        recompute();
    }

    private static SlotLayout buildLayout(int gridWidth, int gridHeight) {
        int rows = Math.max(gridHeight, 3);
        if (rows > 6) {
            rows = 6;
        }
        SlotLayout.Builder b = SlotLayout.builder(rows * 9).grid(gridWidth, gridHeight);
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                b.input(y * 9 + x, x, y);
            }
        }
        int resultSlot = (Math.min(gridHeight, rows) / 2) * 9 + 6;
        b.output(resultSlot);
        // Everything else stays BACKGROUND (the default).
        return b.build();
    }

    @Override
    protected Optional<CraftingRecipeLike> findMatch(CraftingGrid grid) {
        return registry.match(grid);
    }
}
