package dev.arubik.craftengine.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A live "just-in-time" crafting-table chest UI for an arbitrary W x H input
 * grid plus a single result slot.
 *
 * <p>Behaves like a vanilla crafting table: the matched output is recomputed
 * every time an input slot changes, and taking the output consumes one of each
 * input. The grid contents live in the open inventory itself (per-session,
 * vanilla-style); nothing is persisted to the block.
 *
 * <p>Layout (chest, 9 columns): input grid occupies columns {@code 0..W-1} of
 * rows {@code 0..H-1}; the result slot sits at column 6 of the middle input
 * row. All matching is delegated to {@link CraftingRecipeRegistry}; this class
 * only adapts to/from Bukkit via {@link CraftItemAdapter}.
 */
public final class CraftingTableMenu implements InventoryHolder {

    private static final ItemStack FILLER;

    static {
        FILLER = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = FILLER.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            FILLER.setItemMeta(meta);
        }
    }

    private final int gridWidth;
    private final int gridHeight;
    private final CraftingRecipeRegistry registry;
    private final Inventory inventory;

    private final int[] inputSlots; // length gridWidth*gridHeight, row-major
    private final int resultSlot;

    private CraftingRecipe lastMatch;

    public CraftingTableMenu(int gridWidth, int gridHeight, String title, CraftingRecipeRegistry registry) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.registry = registry;

        int rows = Math.max(gridHeight, 3);
        if (rows > 6) {
            rows = 6;
        }
        this.inventory = Bukkit.createInventory(this, rows * 9, title);

        this.inputSlots = new int[gridWidth * gridHeight];
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                inputSlots[y * gridWidth + x] = y * 9 + x;
            }
        }
        this.resultSlot = (Math.min(gridHeight, rows) / 2) * 9 + 6;

        drawFiller();
        recompute();
    }

    private void drawFiller() {
        boolean[] reserved = new boolean[inventory.getSize()];
        for (int s : inputSlots) {
            reserved[s] = true;
        }
        reserved[resultSlot] = true;
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!reserved[i]) {
                inventory.setItem(i, FILLER.clone());
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int gridWidth() {
        return gridWidth;
    }

    public int gridHeight() {
        return gridHeight;
    }

    public int resultSlot() {
        return resultSlot;
    }

    /** True if {@code rawSlot} is one of the editable grid input slots. */
    public boolean isInputSlot(int rawSlot) {
        for (int s : inputSlots) {
            if (s == rawSlot) {
                return true;
            }
        }
        return false;
    }

    public boolean isResultSlot(int rawSlot) {
        return rawSlot == resultSlot;
    }

    /** True if the slot is a static filler pane (never interactive). */
    public boolean isFillerSlot(int rawSlot) {
        return rawSlot >= 0 && rawSlot < inventory.getSize() && !isInputSlot(rawSlot) && !isResultSlot(rawSlot);
    }

    /** Builds the current input grid as a pure {@link CraftingGrid}. */
    public CraftingGrid snapshotGrid() {
        List<CraftCell> cells = new ArrayList<>(inputSlots.length);
        for (int s : inputSlots) {
            cells.add(CraftItemAdapter.toCell(inventory.getItem(s)));
        }
        return CraftingGrid.of(gridWidth, gridHeight, cells);
    }

    /** Recomputes the matched recipe and refreshes the result slot. */
    public void recompute() {
        Optional<CraftingRecipe> match = registry.match(snapshotGrid());
        lastMatch = match.orElse(null);
        if (lastMatch == null) {
            inventory.setItem(resultSlot, null);
            return;
        }
        // Display the first output as the result preview.
        ItemStack preview = CraftItemAdapter.toBukkit(lastMatch.outputs().get(0));
        inventory.setItem(resultSlot, preview);
    }

    /**
     * Handles a player taking the current result: consumes one of every
     * non-empty input cell, gives all outputs to the player (dropping overflow),
     * then recomputes. No-op if there is no current match.
     */
    public void takeResult(Player player) {
        if (lastMatch == null) {
            return;
        }
        // Re-validate against the live grid in case it changed.
        CraftingGrid grid = snapshotGrid();
        if (!lastMatch.matches(grid)) {
            recompute();
            return;
        }
        // Consume one item from each occupied input slot.
        for (int s : inputSlots) {
            ItemStack stack = inventory.getItem(s);
            if (stack != null && stack.getType() != Material.AIR && stack.getAmount() > 0) {
                int amt = stack.getAmount() - 1;
                if (amt <= 0) {
                    inventory.setItem(s, null);
                } else {
                    stack.setAmount(amt);
                }
            }
        }
        // Give every output.
        for (CraftCell out : lastMatch.outputs()) {
            ItemStack item = CraftItemAdapter.toBukkit(out);
            if (item == null) {
                continue;
            }
            var leftover = player.getInventory().addItem(item);
            for (ItemStack drop : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), drop);
            }
        }
        recompute();
    }

    /** Returns input items to the player (called on close) so nothing is lost. */
    public void returnInputs(Player player) {
        for (int s : inputSlots) {
            ItemStack stack = inventory.getItem(s);
            if (stack != null && stack.getType() != Material.AIR && stack.getAmount() > 0) {
                var leftover = player.getInventory().addItem(stack);
                for (ItemStack drop : leftover.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), drop);
                }
                inventory.setItem(s, null);
            }
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }
}
