package dev.arubik.craftengine.crafting;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * Immersive-Engineering-style "engineer's workbench" menu. Lays out a 6x4 usable
 * region inside a vanilla 6-row chest (size 54) for a custom GUI texture overlay:
 * a 3x2 input grid, 2 output slots, and 1 CUSTOM "tool" slot. Crafting is gated
 * on the {@link StationRecipe}'s required tool (present, correct, with remaining
 * uses) plus the recipe's optional {@link RecipeCondition}; each craft consumes
 * {@code toolUsesPerCraft} durability points and breaks the tool when exhausted.
 *
 * <p>Reuses the entire base ({@link AbstractCraftingMenu}) matching/UI/take
 * pipeline; only the three CUSTOM hooks and {@link #findMatch} are specialised.
 */
public final class WorkbenchMenu extends AbstractCraftingMenu {

    // ---- slot indices (vanilla 6x9 chest; see class doc / summary for the map) ----
    // Row 1 cols 1-3 -> 10,11,12 ; Row 2 cols 1-3 -> 19,20,21 (the 3x2 input grid).
    public static final int IN_00 = 10, IN_10 = 11, IN_20 = 12;
    public static final int IN_01 = 19, IN_11 = 20, IN_21 = 21;
    // Two outputs (row 1-2, col 5): 14 and 23.
    public static final int OUT_0 = 14, OUT_1 = 23;
    // Tool slot (row 1, col 6 region): 16.
    public static final int TOOL_SLOT = 16;

    private final StationRecipeRegistry registry;

    public WorkbenchMenu(String title, StationRecipeRegistry registry) {
        super(buildLayout(), title);
        this.registry = registry != null ? registry : StationRecipeRegistry.global();
        recompute();
    }

    private static SlotLayout buildLayout() {
        SlotLayout.Builder b = SlotLayout.builder(54).grid(3, 2);
        b.input(IN_00, 0, 0).input(IN_10, 1, 0).input(IN_20, 2, 0);
        b.input(IN_01, 0, 1).input(IN_11, 1, 1).input(IN_21, 2, 1);
        b.output(OUT_0).output(OUT_1);
        b.custom(TOOL_SLOT, true); // non-persistent menu: hand the tool back on close
        // All other slots stay BACKGROUND (the base enforces non-placeable).
        return b.build();
    }

    // ---- matching: resolve a StationRecipe AND require its tool present ----

    @Override
    protected Optional<CraftingRecipeLike> findMatch(CraftingGrid grid) {
        Optional<StationRecipe> match = registry.match(grid);
        if (match.isEmpty()) {
            return Optional.empty();
        }
        StationRecipe recipe = match.get();
        // Gate the preview on the tool being present & correct (broken tools blocked
        // here too so no preview shows for an unusable tool).
        if (!toolMatches(recipe) || remainingUses(recipe) <= 0) {
            return Optional.empty();
        }
        return Optional.of(recipe);
    }

    // ---- gate: tool uses >= crafts*usesPerCraft AND recipe condition ----

    @Override
    protected boolean isCustomCraftAllowed(CraftingGrid grid, CraftingRecipeLike recipe, int crafts) {
        if (!(recipe instanceof StationRecipe station)) {
            return true;
        }
        if (!toolMatches(station) || remainingUses(station) < crafts) {
            return false;
        }
        if (recipe instanceof RecipeCondition cond) {
            CraftContext ctx = new CraftContext(grid, getInventory().getItem(TOOL_SLOT), crafts,
                    currentViewer(), this);
            return cond.test(ctx);
        }
        return true;
    }

    // ---- side effects: consume tool durability + run executor ----

    @Override
    protected void onCraft(CraftingRecipeLike recipe, int crafts) {
        if (!(recipe instanceof StationRecipe station)) {
            return;
        }
        consumeTool(station, crafts);
        if (recipe instanceof RecipeExecutor exec) {
            // Player is the inventory viewer; resolve from the open inventory.
            Player player = currentViewer();
            CraftContext ctx = new CraftContext(snapshotGrid(), getInventory().getItem(TOOL_SLOT), crafts, player, this);
            exec.run(ctx);
        }
    }

    private Player currentViewer() {
        var viewers = getInventory().getViewers();
        for (var v : viewers) {
            if (v instanceof Player p) {
                return p;
            }
        }
        return null;
    }

    // ---- tool helpers ----

    /** True if the TOOL slot holds the recipe's required item (custom or vanilla id). */
    private boolean toolMatches(StationRecipe recipe) {
        ItemStack tool = getInventory().getItem(TOOL_SLOT);
        Key id = toolItemId(tool);
        return id != null && id.equals(recipe.requiredTool());
    }

    /** Remaining whole crafts the current tool allows for {@code recipe}. */
    private int remainingUses(StationRecipe recipe) {
        ItemStack tool = getInventory().getItem(TOOL_SLOT);
        if (tool == null || tool.getType() == Material.AIR) {
            return 0;
        }
        int max = tool.getType().getMaxDurability();
        if (max <= 0) {
            // Non-damageable item used as a tool: treat as a single, infinite-ish use.
            return Integer.MAX_VALUE;
        }
        int damage = currentDamage(tool);
        return StationToolMath.remainingUses(max, damage, recipe.toolUsesPerCraft());
    }

    private void consumeTool(StationRecipe recipe, int crafts) {
        ItemStack tool = getInventory().getItem(TOOL_SLOT);
        if (tool == null || tool.getType() == Material.AIR) {
            return;
        }
        int max = tool.getType().getMaxDurability();
        if (max <= 0) {
            return; // non-damageable: nothing to consume
        }
        int damage = currentDamage(tool);
        if (StationToolMath.breaksAfter(max, damage, recipe.toolUsesPerCraft(), crafts)) {
            getInventory().setItem(TOOL_SLOT, null); // tool broke -> slot cleared
            return;
        }
        int next = StationToolMath.damageAfter(max, damage, recipe.toolUsesPerCraft(), crafts);
        ItemMeta meta = tool.getItemMeta();
        if (meta instanceof Damageable dmg) {
            dmg.setDamage(next);
            tool.setItemMeta(meta);
        }
    }

    private static int currentDamage(ItemStack tool) {
        ItemMeta meta = tool.getItemMeta();
        if (meta instanceof Damageable dmg) {
            return dmg.getDamage();
        }
        return 0;
    }

    /**
     * Resolves the tool item's id, mirroring {@code AbstractMachineBlockEntity#upgradeItemId}:
     * craft-engine custom id if present, else the vanilla {@code minecraft:<material>} key.
     * Returns null for an empty slot.
     */
    private static Key toolItemId(ItemStack bukkit) {
        if (bukkit == null || bukkit.getType() == Material.AIR || bukkit.getAmount() <= 0) {
            return null;
        }
        Key custom = CraftEngineItems.getCustomItemId(bukkit);
        if (custom != null) {
            return custom;
        }
        NamespacedKey nk = bukkit.getType().getKey();
        return Key.of(nk.getNamespace(), nk.getKey());
    }
}
