package dev.arubik.craftengine.multiblock.examples;

import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import dev.arubik.craftengine.util.CustomBlockData;
import dev.arubik.craftengine.util.TypedKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Multi-page chest machine entity with 3 pages of 45 slots each (135 total
 * slots)
 */
public class MultiPageChestMachineBlockEntity extends MultiBlockMachineBlockEntity {

    private static final int TOTAL_PAGES = 3;
    private static final int SLOTS_PER_PAGE = 45; // 5 rows of 9
    private static final int TOTAL_SLOTS = TOTAL_PAGES * SLOTS_PER_PAGE; // 135

    // Typed keys for persistence
    private static final TypedKey<List<ItemStackWithSlot>> KEY_PAGE_0 = TypedKey.of("craftengine", "multipage_page0",
            dev.arubik.craftengine.util.CustomDataType.ITEM_STACK_WITH_SLOT_LIST_TYPE);
    private static final TypedKey<List<ItemStackWithSlot>> KEY_PAGE_1 = TypedKey.of("craftengine", "multipage_page1",
            dev.arubik.craftengine.util.CustomDataType.ITEM_STACK_WITH_SLOT_LIST_TYPE);
    private static final TypedKey<List<ItemStackWithSlot>> KEY_PAGE_2 = TypedKey.of("craftengine", "multipage_page2",
            dev.arubik.craftengine.util.CustomDataType.ITEM_STACK_WITH_SLOT_LIST_TYPE);
    private static final TypedKey<Integer> KEY_CURRENT_PAGE = TypedKey.of("craftengine", "multipage_current_page",
            PersistentDataType.INTEGER);

    private final ItemStack[][] pages; // [page][slot]
    private int currentPage = 0;
    private final MachineLayout layout;

    public MultiPageChestMachineBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state, MultiBlockSchema schema) {
        super(SLOTS_PER_PAGE, pos, state, schema); // Container holds current page only

        // Initialize pages
        pages = new ItemStack[TOTAL_PAGES][SLOTS_PER_PAGE];
        for (int p = 0; p < TOTAL_PAGES; p++) {
            for (int i = 0; i < SLOTS_PER_PAGE; i++) {
                pages[p][i] = ItemStack.EMPTY;
            }
        }

        // Create layout with navigation buttons
        this.layout = new MachineLayout(InventoryType.CHEST, 54, "Multi-Page Chest");
        setupLayout();

        // Load from persistence
        loadFromCustomBlockData();
        syncCurrentPageToInventory();
    }

    private void setupLayout() {
        // All 45 slots are input/output (5 rows of 9)
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            layout.addSlot(i, MenuSlotType.INPUT);
        }

        // Row 5: Navigation buttons
        // Previous page button (slot 45 - bottom left)
        layout.addButton(45, (m, p) -> {
            org.bukkit.inventory.ItemStack arrow = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
            arrow.editMeta(meta -> {
                meta.setDisplayName("§aPrevious Page");
            });
            return arrow;
        }, (machine, player) -> {
            if (currentPage > 0) {
                previousPage();
                reopenMenu(((CraftPlayer) player).getHandle());
                player.playSound(
                        player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
        });

        // Page indicator (slot 49 - center)
        layout.setDynamicProvider(49, (machine, i) -> {
            org.bukkit.inventory.ItemStack paper = new org.bukkit.inventory.ItemStack(org.bukkit.Material.PAPER);
            paper.editMeta(meta -> {
                meta.setDisplayName("§ePage " + (currentPage + 1) + "/" + TOTAL_PAGES);
            });
            return paper;
        });

        // Next page button (slot 53 - bottom right)
        layout.addButton(53, (m, p) -> {
            org.bukkit.inventory.ItemStack arrow = new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW);
            arrow.editMeta(meta -> {
                meta.setDisplayName("§aNext Page");
            });
            return arrow;
        }, (machine, player) -> {
            if (currentPage < TOTAL_PAGES - 1) {
                nextPage();
                reopenMenu(((CraftPlayer) player).getHandle());
                player.playSound(
                        player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
        });
    }

    @Override
    public MachineLayout getLayout() {
        return layout;
    }

    // ========== Page Navigation ==========

    public void nextPage() {
        if (currentPage < TOTAL_PAGES - 1) {
            saveCurrentPageFromInventory();
            currentPage++;
            syncCurrentPageToInventory();
            saveToCustomBlockData();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            saveCurrentPageFromInventory();
            currentPage--;
            syncCurrentPageToInventory();
            saveToCustomBlockData();
        }
    }

    private void syncCurrentPageToInventory() {
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            setItem(i, pages[currentPage][i]);
        }
    }

    private void saveCurrentPageFromInventory() {
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            pages[currentPage][i] = getItem(i);
        }
    }

    private void reopenMenu(Player player) {
        // Close and reopen menu to refresh
        player.closeContainer();
        openMenu(player);
    }

    // ========== Persistence ==========

    private void saveToCustomBlockData() {
        saveCurrentPageFromInventory();

        BlockPos nmsPos = getMachinePos();
        Level level = getNMSLevel();
        if (level == null)
            return;

        CustomBlockData data = CustomBlockData.from(level, nmsPos);

        // Save each page
        data.set(KEY_PAGE_0.getKey(), KEY_PAGE_0.getType(),
                dev.arubik.craftengine.util.ArrayItemStackWithSlot.from(pages[0]));
        data.set(KEY_PAGE_1.getKey(), KEY_PAGE_1.getType(),
                dev.arubik.craftengine.util.ArrayItemStackWithSlot.from(pages[1]));
        data.set(KEY_PAGE_2.getKey(), KEY_PAGE_2.getType(),
                dev.arubik.craftengine.util.ArrayItemStackWithSlot.from(pages[2]));
        data.set(KEY_CURRENT_PAGE.getKey(), KEY_CURRENT_PAGE.getType(), currentPage);
    }

    private void loadFromCustomBlockData() {
        BlockPos nmsPos = getMachinePos();
        Level level = getNMSLevel();
        if (level == null)
            return;

        CustomBlockData data = CustomBlockData.from(level, nmsPos);

        // Load each page
        loadPageFromData(data, KEY_PAGE_0, 0);
        loadPageFromData(data, KEY_PAGE_1, 1);
        loadPageFromData(data, KEY_PAGE_2, 2);

        // Load current page
        data.getOptional(KEY_CURRENT_PAGE).ifPresent(page -> {
            if (page >= 0 && page < TOTAL_PAGES) {
                currentPage = page;
            }
        });
    }

    private void loadPageFromData(CustomBlockData data, TypedKey<List<ItemStackWithSlot>> key, int pageIndex) {
        // Initialize page with empty stacks
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            pages[pageIndex][i] = ItemStack.EMPTY;
        }

        // Load items from data
        data.getOptional(key).ifPresent(contents -> {
            for (ItemStackWithSlot item : contents) {
                if (item.slot() >= 0 && item.slot() < SLOTS_PER_PAGE) {
                    pages[pageIndex][item.slot()] = item.stack();
                }
            }
        });
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        saveToCustomBlockData();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = super.removeItem(slot, amount);
        saveToCustomBlockData();
        return result;
    }

    // ========== No Processing (Passive Container) ==========

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        return null; // No processing
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        return false; // No processing
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        // No processing
    }

    @Override
    protected String getMachineId() {
        return "multipage_chest";
    }
}
