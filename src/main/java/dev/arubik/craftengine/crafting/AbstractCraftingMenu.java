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
 * Reusable base for a live "just-in-time" crafting chest UI. It owns all the
 * plumbing shared by every crafting block:
 * <ul>
 *   <li>a declarative {@link SlotLayout} (slot -&gt; {@link SlotRole});</li>
 *   <li>live recipe recomputation whenever an INPUT changes;</li>
 *   <li>an output preview in the OUTPUT slot(s);</li>
 *   <li>correct take/consume on a normal click and bulk craft on shift-click
 *       (delegating the count math to the pure {@link CraftingTransaction}).</li>
 * </ul>
 *
 * <p>A subclass only declares its layout + recipe source and may override the
 * CUSTOM-slot hooks ({@link #isCustomCraftAllowed}, {@link #onCraft},
 * {@link #onCustomSlotChanged}) to add fuel/fluid/arbitrary logic without
 * touching matching or UI code.
 *
 * <p>Recipe matching is delegated to {@link CraftingRecipeLike} via the
 * subclass-supplied {@link #findMatch}; Bukkit conversion to/from the pure
 * {@link CraftCell} world happens only here, via {@link CraftItemAdapter}.
 */
public abstract class AbstractCraftingMenu implements InventoryHolder {

    /** Shared inert filler for BACKGROUND slots. */
    protected static final ItemStack FILLER;

    static {
        FILLER = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = FILLER.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            FILLER.setItemMeta(meta);
        }
    }

    private final SlotLayout layout;
    private final Inventory inventory;
    private CraftingRecipeLike lastMatch;

    protected AbstractCraftingMenu(SlotLayout layout, String title) {
        this.layout = layout;
        int rows = Math.max(1, Math.min(6, (layout.size() + 8) / 9));
        this.inventory = Bukkit.createInventory(this, rows * 9, title != null ? title : "Crafting");
        drawBackground();
    }

    // ---- subclass contract ----

    /** Resolve the recipe matched by the current input grid (e.g. a registry). */
    protected abstract Optional<CraftingRecipeLike> findMatch(CraftingGrid grid);

    /**
     * CUSTOM-slot gate for whether a craft may proceed (e.g. enough fuel). The
     * default allows it. Subclasses override to inspect CUSTOM slots.
     */
    protected boolean isCustomCraftAllowed(CraftingGrid grid, CraftingRecipeLike recipe, int crafts) {
        return true;
    }

    /**
     * Hook invoked AFTER a successful craft of {@code crafts} times, before the
     * recompute. Use to consume fuel / fluid in CUSTOM slots. Default no-op.
     */
    protected void onCraft(CraftingRecipeLike recipe, int crafts) {
    }

    /** Hook invoked when a CUSTOM slot changes (default: just recompute). */
    protected void onCustomSlotChanged(int slot) {
        recompute();
    }

    /** Public bridge for the listener to notify a CUSTOM-slot change. */
    public final void onCustomSlotChangedExternal(int slot) {
        onCustomSlotChanged(slot);
    }

    // ---- accessors ----

    public final SlotLayout layout() {
        return layout;
    }

    @Override
    public final Inventory getInventory() {
        return inventory;
    }

    public final void open(Player player) {
        player.openInventory(inventory);
    }

    public final boolean isInputSlot(int raw) {
        return raw >= 0 && raw < inventory.getSize() && layout.isInput(raw);
    }

    public final boolean isOutputSlot(int raw) {
        return raw >= 0 && raw < inventory.getSize() && layout.isOutput(raw);
    }

    public final boolean isBackgroundSlot(int raw) {
        return raw >= 0 && raw < inventory.getSize() && layout.isBackground(raw);
    }

    public final boolean isCustomSlot(int raw) {
        return raw >= 0 && raw < inventory.getSize() && layout.isCustom(raw);
    }

    // ---- grid <-> bukkit ----

    private void drawBackground() {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (layout.isBackground(i)) {
                inventory.setItem(i, FILLER.clone());
            }
        }
    }

    /** Snapshot the INPUT slots into a pure {@link CraftingGrid}. */
    public final CraftingGrid snapshotGrid() {
        int[] inputs = layout.inputSlots();
        List<CraftCell> cells = new ArrayList<>(inputs.length);
        for (int s : inputs) {
            cells.add(CraftItemAdapter.toCell(inventory.getItem(s)));
        }
        return CraftingGrid.of(layout.gridWidth(), layout.gridHeight(), layout.gridDepth(), cells);
    }

    /** Recompute the matched recipe and refresh the output preview slot(s). */
    public final void recompute() {
        CraftingGrid grid = snapshotGrid();
        lastMatch = findMatch(grid).orElse(null);
        List<Integer> outSlots = layout.outputSlots();
        if (lastMatch == null) {
            for (int s : outSlots) {
                inventory.setItem(s, null);
            }
            return;
        }
        List<CraftCell> outputs = lastMatch.outputs(grid);
        for (int i = 0; i < outSlots.size(); i++) {
            CraftCell cell = i < outputs.size() ? outputs.get(i) : CraftCell.EMPTY;
            inventory.setItem(outSlots.get(i), CraftItemAdapter.toBukkit(cell));
        }
    }

    // ---- crafting ----

    /**
     * Normal take: craft exactly once. Primary output goes to the player normal
     * flow (their inventory; overflow dropped), every secondary output to the
     * inventory (overflow dropped), one of each occupied input consumed.
     */
    public final void takeResult(Player player) {
        craft(player, 1, false);
    }

    /**
     * Shift-click take: craft as many times as inputs AND player inventory room
     * for ALL outputs allow, never overflowing.
     */
    public final void shiftTakeResult(Player player) {
        craft(player, Integer.MAX_VALUE, true);
    }

    private void craft(Player player, int requested, boolean shift) {
        if (lastMatch == null || requested <= 0) {
            return;
        }
        CraftingGrid grid = snapshotGrid();
        if (!lastMatch.matches(grid)) {
            recompute();
            return;
        }
        List<CraftCell> outputs = lastMatch.outputs(grid);

        int[] inputsAvailable = occupiedInputCounts(grid);
        int crafts;
        if (shift) {
            int[] free = freeSpaceForOutputs(player, outputs);
            crafts = CraftingTransaction.maxCrafts(inputsAvailable, outputs, free);
        } else {
            // Normal take is one craft, but still bounded by what fits so nothing voids.
            int[] free = freeSpaceForOutputs(player, outputs);
            crafts = Math.min(1, CraftingTransaction.maxCrafts(inputsAvailable, outputs, free));
        }
        crafts = Math.min(crafts, requested);
        if (crafts <= 0) {
            return;
        }
        if (!isCustomCraftAllowed(grid, lastMatch, crafts)) {
            return;
        }

        consumeInputs(crafts);
        applyRemainders(lastMatch.remainders(grid), grid, crafts, player);
        for (CraftCell out : outputs) {
            giveOutput(player, out, crafts);
        }
        onCraft(lastMatch, crafts);
        recompute();
    }

    private int[] occupiedInputCounts(CraftingGrid grid) {
        List<Integer> counts = new ArrayList<>();
        for (CraftCell c : grid.cells()) {
            if (!c.isEmpty()) {
                counts.add(c.count());
            }
        }
        int[] arr = new int[counts.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = counts.get(i);
        }
        return arr;
    }

    private int[] freeSpaceForOutputs(Player player, List<CraftCell> outputs) {
        int[] free = new int[outputs.size()];
        for (int i = 0; i < outputs.size(); i++) {
            free[i] = freeSpaceFor(player, outputs.get(i));
        }
        return free;
    }

    /**
     * How many more of {@code cell}'s item the player inventory can accept,
     * accounting for partial stacks and empty slots. Pure-ish (reads inventory).
     */
    private int freeSpaceFor(Player player, CraftCell cell) {
        ItemStack proto = CraftItemAdapter.toBukkit(cell);
        if (proto == null) {
            return 0;
        }
        int max = proto.getMaxStackSize();
        int free = 0;
        ItemStack[] contents = player.getInventory().getStorageContents();
        for (ItemStack stack : contents) {
            if (stack == null || stack.getType() == Material.AIR) {
                free += max;
            } else if (stack.isSimilar(proto)) {
                free += Math.max(0, max - stack.getAmount());
            }
        }
        return free;
    }

    private void consumeInputs(int crafts) {
        for (int s : layout.inputSlots()) {
            ItemStack stack = inventory.getItem(s);
            if (stack != null && stack.getType() != Material.AIR && stack.getAmount() > 0) {
                int amt = stack.getAmount() - crafts;
                if (amt <= 0) {
                    inventory.setItem(s, null);
                } else {
                    stack.setAmount(amt);
                }
            }
        }
    }

    /**
     * Places remainder items back into the corresponding occupied INPUT slots
     * after consumption (vanilla container-item semantics). Overflow goes to the
     * player. {@code remainders} is indexed by occupied-input order.
     */
    private void applyRemainders(List<CraftCell> remainders, CraftingGrid grid, int crafts, Player player) {
        if (remainders == null || remainders.isEmpty()) {
            return;
        }
        int[] inputs = layout.inputSlots();
        int occ = 0;
        for (int gi = 0; gi < inputs.length; gi++) {
            if (grid.cells().get(gi).isEmpty()) {
                continue;
            }
            if (occ < remainders.size()) {
                CraftCell rem = remainders.get(occ);
                if (rem != null && !rem.isEmpty()) {
                    ItemStack item = CraftItemAdapter.toBukkit(CraftCell.of(rem.id(), rem.count() * crafts));
                    if (item != null) {
                        // Only place into the slot if it is now empty; else give to player.
                        ItemStack existing = inventory.getItem(inputs[gi]);
                        if (existing == null || existing.getType() == Material.AIR) {
                            inventory.setItem(inputs[gi], item);
                        } else {
                            giveStack(player, item);
                        }
                    }
                }
            }
            occ++;
        }
    }

    private void giveOutput(Player player, CraftCell out, int crafts) {
        if (out.isEmpty()) {
            return;
        }
        ItemStack item = CraftItemAdapter.toBukkit(CraftCell.of(out.id(), out.count() * crafts));
        giveStack(player, item);
    }

    private void giveStack(Player player, ItemStack item) {
        if (item == null) {
            return;
        }
        // Split into max-stack chunks so addItem behaves with big counts.
        int max = item.getMaxStackSize();
        int remaining = item.getAmount();
        while (remaining > 0) {
            int chunk = Math.min(max, remaining);
            ItemStack part = item.clone();
            part.setAmount(chunk);
            var leftover = player.getInventory().addItem(part);
            for (ItemStack drop : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), drop);
            }
            remaining -= chunk;
        }
    }

    /** Return INPUT items to the player (called on close) so nothing is lost. */
    public final void returnInputs(Player player) {
        for (int s : layout.inputSlots()) {
            ItemStack stack = inventory.getItem(s);
            if (stack != null && stack.getType() != Material.AIR && stack.getAmount() > 0) {
                giveStack(player, stack);
                inventory.setItem(s, null);
            }
        }
    }
}
