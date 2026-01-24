package dev.arubik.craftengine.machine.block.entity;

import java.util.function.Consumer;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidTransferHelper;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.RelativeDirection;
import dev.arubik.craftengine.multiblock.DirectionalIOHelper;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import dev.arubik.craftengine.util.TransferAccessMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import dev.arubik.craftengine.util.DirectionType;

public class MachinePumpBlockEntity extends AbstractMachineBlockEntity {

    protected static final int CAPACITY = 4000;
    protected static final int TRANSFER_PER_TICK = 1000;
    protected static final int PRESSURE_BOOST = 8;

    private final MachineLayout layout = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 9,
            "Machine Pump")
            .addSlot(4, MenuSlotType.OUTPUT); // Slot 4 for status

    public MachinePumpBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state) {
        super(0, pos, state); // 0 item slots

        // Initialize Tank
        addFluidTank(new FluidTank("internal", CAPACITY));

        // Configure Relative IO
        IOConfiguration.RelativeIO config = new IOConfiguration.RelativeIO();
        config.addInput(IOType.FLUID, RelativeDirection.DOWN);
        config.addOutput(IOType.FLUID, RelativeDirection.UP);
        this.setIOConfiguration(config);

        // UI Fluid Readout
        this.layout.setDynamicProvider(4, (machine, tick) -> {
            MachinePumpBlockEntity pump = (MachinePumpBlockEntity) machine;
            FluidTank tank = pump.fluidTanks.get(0);
            FluidStack stored = pump.get(tank.getKey());

            org.bukkit.Material material = org.bukkit.Material.BUCKET;
            if (!stored.isEmpty()) {
                if (stored.getType() == FluidType.LAVA)
                    material = org.bukkit.Material.LAVA_BUCKET;
                else if (stored.getType() == FluidType.WATER)
                    material = org.bukkit.Material.WATER_BUCKET;
            }

            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(material);
            org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();

            String fluidName = stored.isEmpty() ? "Empty" : stored.getType().toString();
            meta.setDisplayName("§bFluid: " + fluidName);

            java.util.List<String> lore = new java.util.ArrayList<>();
            lore.add("§7Amount: §f" + stored.getAmount() + " / " + CAPACITY + " mB");
            lore.add("§7Pressure: §f" + stored.getPressure());
            meta.setLore(lore);

            stack.setItemMeta(meta);
            return stack;
        });
    }

    @Override
    public MachineLayout getLayout() {
        return layout;
    }

    @Override
    protected void processTick(Level level) {
        if (level.isClientSide())
            return;

        // Redstone check
        if (requiresRedstone && !isRedstoneEnabled(level))
            return;

        BlockPos pos = getMachinePos();
        FluidTank tank = fluidTanks.get(0);
        FluidStack stored = get(tank.getKey());

        // Update pressure if not empty
        if (!stored.isEmpty()) {
            set(tank.getKey(), new FluidStack(stored.getType(), stored.getAmount(), PRESSURE_BOOST));
        }

        // Determine world directions based on facing
        Direction facing = getFacing(level);

        DirectionType type = getBlockBehavior(ConnectableBlockBehavior.class).getDirectionType();
        Direction worldDown = type == DirectionType.FULL
                ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.DOWN, facing)
                : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.DOWN,
                        DirectionalIOHelper.toHorizontalDirection(facing));

        Direction worldUp = type == DirectionType.FULL
                ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.UP, facing)
                : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.UP,
                        DirectionalIOHelper.toHorizontalDirection(facing));
        // Cooldowns
        int blockCd = getOrDefault(FluidKeys.FLUID_BLOCK_COOLDOWN, 0);
        int ioCd = getOrDefault(FluidKeys.FLUID_IO_COOLDOWN, 0);

        if (blockCd > 0)
            set(FluidKeys.FLUID_BLOCK_COOLDOWN, blockCd - 1);
        if (ioCd > 0)
            set(FluidKeys.FLUID_IO_COOLDOWN, ioCd - 1);

        // PUMP from worldDown
        if (blockCd <= 0 && stored.getAmount() < CAPACITY) {
            BlockPos target = pos.relative(worldDown);
            FluidType base = FluidType.getFluidTypeAt(target, level);
            FluidStack collected = base == FluidType.LAVA
                    ? FluidType.collectArea(target, level, 32, TRANSFER_PER_TICK, stored.getType())
                    : FluidType.collectAt(target, level, TRANSFER_PER_TICK, stored.getType());

            if (!collected.isEmpty()) {
                int accepted = insertFluid(level, collected, worldDown);
                if (accepted > 0) {
                    int delay = FluidType.blockCollectDelay(collected.getType());
                    if (delay > 1)
                        set(FluidKeys.FLUID_BLOCK_COOLDOWN, delay);
                }
            } else {
                // Try from carrier below (worldDown)
                FluidTransferHelper.pull(level, pos, worldDown, TRANSFER_PER_TICK, 0);
            }
        }

        // PUSH to worldUp
        if (ioCd <= 0 && !stored.isEmpty()) {
            if (FluidTransferHelper.push(level, pos, worldUp, TRANSFER_PER_TICK, 1)) {
                int delay = FluidType.carrierIODelay(stored.getType());
                if (delay > 1)
                    set(FluidKeys.FLUID_IO_COOLDOWN, delay);
            }
        }
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        return null; // Manual processing
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        return false;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
    }

    @Override
    protected String getMachineId() {
        return "machine_pump";
    }
}
