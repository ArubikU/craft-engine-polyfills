package dev.arubik.craftengine.machine.examples;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.machine.upgrade.UpgradeRegistry;
import dev.arubik.craftengine.machine.upgrade.UpgradeType;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.util.Key;

/**
 * Sample machine demonstrating the {@code machine/upgrade} slot system.
 *
 * <p>Layout: input (0), output (1), fuel (2), and three upgrade slots (3-5).
 * Modeled on {@link TestMachineBlockEntity}; processes item-to-item smelting
 * recipes registered under the machine id {@code upgradeable_furnace}.</p>
 */
public class UpgradeableFurnaceBlockEntity extends AbstractMachineBlockEntity {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_FUEL = 2;
    public static final int[] UPGRADE_SLOTS = { 3, 4, 5 };


    private final MachineLayout layout = new MachineLayout(
            org.bukkit.event.inventory.InventoryType.CHEST, 9, "Upgradeable Furnace")
            .addSlot(SLOT_INPUT, MenuSlotType.INPUT)
            .addSlot(SLOT_OUTPUT, MenuSlotType.OUTPUT)
            .addSlot(SLOT_FUEL, MenuSlotType.FUEL)
            .addSlot(UPGRADE_SLOTS[0], MenuSlotType.UPGRADE)
            .addSlot(UPGRADE_SLOTS[1], MenuSlotType.UPGRADE)
            .addSlot(UPGRADE_SLOTS[2], MenuSlotType.UPGRADE);

    public UpgradeableFurnaceBlockEntity(net.momirealms.craftengine.core.block.entity.BlockEntity blockEntity) {
        super(blockEntity, 6);

        this.layout.setTitleComponent(dev.arubik.craftengine.machine.menu.MenuText.noI(
                dev.arubik.craftengine.machine.menu.MenuText.tr("polyfill.ui.furnace_title",
                        net.kyori.adventure.text.format.NamedTextColor.GOLD)));

        // Ensure the sample upgrades exist in the global registry.
        registerDefaultUpgrades(UpgradeRegistry.global());

        IOConfiguration.Simple config = new IOConfiguration.Simple();
        config.addInput(IOType.ITEM, Direction.NORTH);
        config.addOutput(IOType.ITEM, Direction.SOUTH);
        config.setSlots(IOType.ITEM, IOConfiguration.IORole.INPUT, SLOT_INPUT);
        config.setSlots(IOType.ITEM, IOConfiguration.IORole.OUTPUT, SLOT_OUTPUT);
        config.setSlots(IOType.ITEM, IOConfiguration.IORole.FUEL, SLOT_FUEL);
        this.setIOConfiguration(config);

        // Status indicator (slot 8).
        this.layout.setDynamicProvider(8, (machine, tick) -> {
            UpgradeableFurnaceBlockEntity self = (UpgradeableFurnaceBlockEntity) machine;
            boolean processing = self.isProcessing();
            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                    processing ? org.bukkit.Material.LIME_STAINED_GLASS_PANE
                            : org.bukkit.Material.RED_STAINED_GLASS_PANE);
            org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
            if (processing) {
                meta.displayName(dev.arubik.craftengine.machine.menu.MenuText.noI(
                        dev.arubik.craftengine.machine.menu.MenuText.tr("polyfill.ui.processing",
                                net.kyori.adventure.text.format.NamedTextColor.GREEN)
                                .append(net.kyori.adventure.text.Component.text(
                                        " (x" + self.getUpgradeModifiers().speedMultiplier() + ")",
                                        net.kyori.adventure.text.format.NamedTextColor.GRAY))));
            } else {
                meta.displayName(dev.arubik.craftengine.machine.menu.MenuText.noI(
                        dev.arubik.craftengine.machine.menu.MenuText.tr("polyfill.ui.idle",
                                net.kyori.adventure.text.format.NamedTextColor.RED)));
            }
            stack.setItemMeta(meta);
            return stack;
        });
    }

    /**
     * Registers the sample upgrades against vanilla item ids. Idempotent.
     *
     * <ul>
     *   <li>{@code minecraft:sugar} &rarr; SPEED +1 (max 3)</li>
     *   <li>{@code minecraft:redstone} &rarr; EFFICIENCY 0.2 (max 3)</li>
     *   <li>{@code minecraft:glowstone_dust} &rarr; YIELD 0.5 (unlimited)</li>
     * </ul>
     */
    public static void registerDefaultUpgrades(UpgradeRegistry registry) {
        // Intentionally empty. The three upgrades this used to register lazily (from a
        // block-entity constructor, behind a static guard that never reset on reload)
        // now live in upgrades/*.json and are loaded by UpgradeLoader on every load
        // pass. Kept as a no-op so a private per-machine registry can still opt out of
        // the global set without this call disappearing from the call sites.
    }

    @Override
    public int[] getUpgradeSlots() {
        return UPGRADE_SLOTS.clone();
    }

    @Override
    public MachineLayout getLayout() {
        return layout;
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        ItemStack input = getItem(SLOT_INPUT);
        if (input.isEmpty()) {
            return null;
        }
        for (AbstractProcessingRecipe recipe : RecipeManager.getRecipes(getMachineId())) {
            if (matchesInputs(recipe, input)) {
                return recipe;
            }
        }
        return null;
    }

    private boolean matchesInputs(AbstractProcessingRecipe recipe, ItemStack input) {
        boolean matchedAny = false;
        for (RecipeInput in : recipe.getInputs()) {
            if (in instanceof ItemInput || in instanceof dev.arubik.craftengine.machine.recipe.CraftEngineItemInput
                    || in instanceof dev.arubik.craftengine.machine.recipe.TagInput) {
                if (!in.matches(input)) {
                    return false;
                }
                matchedAny = true;
            }
        }
        return matchedAny;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        if (output instanceof ItemOutput itemOutput) {
            ItemStack out = (ItemStack) itemOutput.getOutput();
            ItemStack current = getItem(SLOT_OUTPUT);
            if (current.isEmpty()) {
                return true;
            }
            if (ItemStack.isSameItem(current, out)) {
                return current.getCount() + out.getCount() <= current.getMaxStackSize();
            }
            return false;
        }
        // Non-item outputs (xp, etc.) are always acceptable.
        return true;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        int toConsume = 1;
        for (RecipeInput in : recipe.getInputs()) {
            if (in instanceof ItemInput || in instanceof dev.arubik.craftengine.machine.recipe.CraftEngineItemInput
                    || in instanceof dev.arubik.craftengine.machine.recipe.TagInput) {
                toConsume = Math.max(1, in.getAmount());
                break;
            }
        }
        removeItem(SLOT_INPUT, toConsume);
        setChanged();
    }

    @Override
    protected String getMachineId() {
        return "upgradeable_furnace";
    }
}
