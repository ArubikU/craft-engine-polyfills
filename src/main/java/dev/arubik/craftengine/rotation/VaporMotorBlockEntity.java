package dev.arubik.craftengine.rotation;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import dev.arubik.craftengine.util.TypedKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import org.bukkit.persistence.PersistentDataType;

/**
 * A vapor-powered motor. Holds a vapor (gas) tank and, each tick, consumes a
 * configurable amount of vapor to produce RPM. RPM is delivered to the block
 * directly in front of the motor (its facing direction) if that block's
 * controller implements {@link RpmConsumer}.
 *
 * <p>Vapor is modelled with the existing gas subsystem; the vapor medium is
 * {@link GasType#STEAM}.</p>
 */
public class VaporMotorBlockEntity extends AbstractMachineBlockEntity implements RpmProvider {

    /** The gas type used as the "vapor" medium. */
    public static final GasType VAPOR = GasType.STEAM;

    public static final int DEFAULT_VAPOR_CAPACITY = 10000;
    public static final int DEFAULT_VAPOR_PER_TICK = 20;
    public static final float DEFAULT_MAX_RPM = 256f;

    private final int vaporPerTick;
    private final float maxRpm;

    private float currentRpm = 0f;

    private static final TypedKey<Float> KEY_RPM = TypedKey.of(
            "craftengine", "vapor_motor_rpm", PersistentDataType.FLOAT);

    private final MachineLayout layout = new MachineLayout(
            org.bukkit.event.inventory.InventoryType.HOPPER, 5, "Vapor Motor");

    public VaporMotorBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, DEFAULT_VAPOR_CAPACITY, DEFAULT_VAPOR_PER_TICK, DEFAULT_MAX_RPM);
    }

    public VaporMotorBlockEntity(BlockEntity blockEntity, int vaporCapacity, int vaporPerTick, float maxRpm) {
        // No item slots are needed; the motor runs purely on vapor.
        super(blockEntity, 0);
        this.vaporPerTick = Math.max(1, vaporPerTick);
        this.maxRpm = Math.max(0f, maxRpm);

        // Single vapor tank, filterable only to the vapor medium.
        addGasTank(new GasTank("vapor", vaporCapacity, VAPOR));

        // Accept vapor from any side, output none.
        IOConfiguration.Simple config = new IOConfiguration.Simple();
        for (Direction d : Direction.values()) {
            config.addInput(IOType.GAS, d);
        }
        config.setSlots(IOType.GAS, IOConfiguration.IORole.INPUT, 0);
        setIOConfiguration(config);

        this.layout.setDynamicProvider(2, (machine, tick) -> {
            VaporMotorBlockEntity self = (VaporMotorBlockEntity) machine;
            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                    org.bukkit.Material.WHITE_STAINED_GLASS);
            org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
            GasStack vapor = self.gasTanks.get(0).getGas(self.getNMSLevel(), self.getMachinePos());
            meta.setDisplayName("§fVapor: " + vapor.getAmount() + " mB  §7| RPM: "
                    + String.format("%.0f", self.currentRpm));
            stack.setItemMeta(meta);
            return stack;
        });
    }

    // The motor is not a processing machine; vapor->RPM is handled in tick().
    @Override
    protected boolean requiresFuel() {
        return false;
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        return null;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        return false;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        // no-op
    }

    @Override
    protected String getMachineId() {
        return "vapor_motor";
    }

    @Override
    public MachineLayout getLayout() {
        return layout;
    }

    // --- RpmProvider ---
    @Override
    public float getRpm() {
        return currentRpm;
    }

    // --- Tick: consume vapor -> produce RPM -> push to head ---
    @Override
    public void tick(Level level, BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        super.tick(level, pos, state);
        if (level.isClientSide())
            return;

        float producedRpm = 0f;
        if (!gasTanks.isEmpty()) {
            GasTank tank = gasTanks.get(0);
            GasStack vapor = tank.getGas(level, getMachinePos());
            int available = vapor.getAmount();
            if (available > 0) {
                int toConsume = Math.min(vaporPerTick, available);
                tank.extract(level, getMachinePos(), toConsume, null);
                // RPM scales with how much of the demanded vapor was satisfied.
                float ratio = (float) toConsume / (float) vaporPerTick;
                producedRpm = maxRpm * ratio;
            }
        }

        if (producedRpm != currentRpm) {
            currentRpm = producedRpm;
            setChanged();
        }

        transferToHead(level);
    }

    /**
     * Delivers RPM to the directly-connected block in the motor's facing
     * direction. No network graph: a single block, the "head".
     */
    private void transferToHead(Level level) {
        Direction facing = getFacing(level);
        if (facing == null)
            return;
        BlockPos headPos = getMachinePos().relative(facing);
        BlockEntity head = BukkitBlockEntityTypes.getIfLoaded(level, headPos);
        if (head == null)
            return;
        BlockEntityController controller = head.controller;
        if (controller instanceof RpmConsumer consumer) {
            consumer.setInputRpm(currentRpm);
        }
    }

    // --- Persistence ---
    @Override
    public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        set(KEY_RPM, currentRpm);
        // Vapor amount itself is persisted by the gas tank via CustomBlockData;
        // RPM is the only extra motor state.
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        this.currentRpm = getOrDefault(KEY_RPM, 0f);
    }
}
