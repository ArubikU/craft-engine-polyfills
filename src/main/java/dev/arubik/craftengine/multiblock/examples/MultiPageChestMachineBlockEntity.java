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
import net.minecraft.util.datafix.fixes.ChunkPalettedStorageFix.Direction;
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

    private boolean dataLoaded = false;

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

        // Note: loadFromCustomBlockData() is deferred until world is set
    }

    /**
     * Ensures data is loaded from persistence. Called lazily when world is
     * available.
     */
    private void ensureDataLoaded() {
        if (!dataLoaded && world != null) {
            loadFromPersistence();
            syncCurrentPageToInventory();
            dataLoaded = true;
        }
    }

    @Override
    public void setWorld(net.momirealms.craftengine.core.world.CEWorld world) {
        super.setWorld(world);
        ensureDataLoaded();
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
                getMenu().syncFromMachine();
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
                getMenu().syncFromMachine();
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
            getMenu().syncToMachine();
            saveCurrentPageFromInventory();
            currentPage++;
            syncCurrentPageToInventory();
            saveToPersistence();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            getMenu().syncToMachine();
            saveCurrentPageFromInventory();
            currentPage--;
            syncCurrentPageToInventory();
            saveToPersistence();
        }
    }

    private void syncCurrentPageToInventory() {
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            // Use super.setItem to avoid redundant saveToCustomBlockData calls
            super.setItem(i, pages[currentPage][i]);
        }
    }

    private void saveCurrentPageFromInventory() {
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            pages[currentPage][i] = getItem(i);
        }
    }

    // ========== Persistence ==========

    private void saveToPersistence() {
        saveCurrentPageFromInventory();

        // Save each page directly into the BlockEntity PDC
        this.set(KEY_PAGE_0, dev.arubik.craftengine.util.ArrayItemStackWithSlot.from(pages[0]));
        this.set(KEY_PAGE_1, dev.arubik.craftengine.util.ArrayItemStackWithSlot.from(pages[1]));
        this.set(KEY_PAGE_2, dev.arubik.craftengine.util.ArrayItemStackWithSlot.from(pages[2]));
        this.set(KEY_CURRENT_PAGE, currentPage);
        setChanged();
    }

    private void loadFromPersistence() {
        // Load each page
        loadPageFromPersistence(KEY_PAGE_0, 0);
        loadPageFromPersistence(KEY_PAGE_1, 1);
        loadPageFromPersistence(KEY_PAGE_2, 2);

        // Load current page
        Integer page = this.get(KEY_CURRENT_PAGE);
        if (page != null && page >= 0 && page < TOTAL_PAGES) {
            currentPage = page;
        }
    }

    private void loadPageFromPersistence(TypedKey<List<ItemStackWithSlot>> key, int pageIndex) {
        // Initialize page with empty stacks
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            pages[pageIndex][i] = ItemStack.EMPTY;
        }

        // Load items from persistence
        List<ItemStackWithSlot> contents = this.get(key);
        if (contents != null) {
            for (ItemStackWithSlot item : contents) {
                if (item.slot() >= 0 && item.slot() < SLOTS_PER_PAGE) {
                    pages[pageIndex][item.slot()] = item.stack();
                }
            }
        }
    }

    // ========== Automation Support (Virtual Slots) ==========

    private static final int AUTOMATION_OFFSET = 1000;

    @Override
    public boolean isEmpty() {
        for (int p = 0; p < TOTAL_PAGES; p++) {
            for (int i = 0; i < SLOTS_PER_PAGE; i++) {
                ItemStack stack = pages[p][i];
                if (stack != null && !stack.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getContainerSize() {
        // Expose virtual slots for automation in addition to the standard view
        // Automation sees slots 1000 to 1134 (135 slots)
        return AUTOMATION_OFFSET + TOTAL_SLOTS;
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        // Automation always accesses the virtual slots (all pages)
        int[] slots = new int[TOTAL_SLOTS];
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            slots[i] = AUTOMATION_OFFSET + i;
        }
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction dir) {
        return slot >= AUTOMATION_OFFSET; // Only allow automation through virtual slots
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction dir) {
        return slot >= AUTOMATION_OFFSET; // Only allow automation through virtual slots
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot >= AUTOMATION_OFFSET) {
            int absoluteIndex = slot - AUTOMATION_OFFSET;
            if (absoluteIndex >= 0 && absoluteIndex < TOTAL_SLOTS) {
                int page = absoluteIndex / SLOTS_PER_PAGE;
                int index = absoluteIndex % SLOTS_PER_PAGE;
                if (page == currentPage) {
                    return super.getItem(index);
                }
                return pages[page][index];
            }
            return ItemStack.EMPTY;
        }
        return super.getItem(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= AUTOMATION_OFFSET) {
            int absoluteIndex = slot - AUTOMATION_OFFSET;
            if (absoluteIndex >= 0 && absoluteIndex < TOTAL_SLOTS) {
                int page = absoluteIndex / SLOTS_PER_PAGE;
                int index = absoluteIndex % SLOTS_PER_PAGE;

                // Update internal storage
                pages[page][index] = stack;

                // If modifying the current page, update the container view
                if (page == currentPage) {
                    super.setItem(index, stack);
                } else {
                    saveToPersistence();
                }
            }
            return;
        }

        // Handle direct slot access (e.g. from Menu sync)
        if (slot >= 0 && slot < SLOTS_PER_PAGE) {
            if (ItemStack.matches(pages[currentPage][slot], stack))
                return;
            pages[currentPage][slot] = stack;
            super.setItem(slot, stack);
            saveToPersistence();
            return;
        }

        super.setItem(slot, stack);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot >= AUTOMATION_OFFSET) {
            ItemStack stack = getItem(slot);
            if (!stack.isEmpty()) {
                if (stack.getCount() <= amount) {
                    setItem(slot, ItemStack.EMPTY);
                    return stack;
                } else {
                    ItemStack split = stack.split(amount);
                    if (stack.isEmpty()) {
                        setItem(slot, ItemStack.EMPTY);
                    } else {
                        setItem(slot, stack); // updates persistence
                    }
                    return split;
                }
            }
            return ItemStack.EMPTY;
        }

        ItemStack result = super.removeItem(slot, amount);
        if (slot >= 0 && slot < SLOTS_PER_PAGE) {
            pages[currentPage][slot] = getItem(slot);
            saveToPersistence();
        }
        return result;
    }

    // ========== No Processing (Passive Container) ==========

    @Override
    public void clearContent() {
        for (int p = 0; p < TOTAL_PAGES; p++) {
            for (int i = 0; i < SLOTS_PER_PAGE; i++) {
                pages[p][i] = ItemStack.EMPTY;
            }
        }
        super.clearContent(); // Clears current page inventory and calls setChanged()
        saveToPersistence();
    }

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
