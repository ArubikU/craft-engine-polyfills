package dev.arubik.craftengine.machine.examples;

import org.bukkit.Material;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.CraftEngineItemInput;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.TagInput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;

/**
 * [TASK-02] Crusher — a single-block, RPM-powered processing machine. Reuses the
 * {@link AbstractMachineBlockEntity} recipe loop but is driven by mechanical power
 * (no fuel): it only advances while the {@code minRpm} of the matched recipe is met,
 * and reports the recipe's {@code su} load back to the driving motor. Recipes are
 * standard MACHINE recipes ({@code machineId = "smeltery"}) with process time + chance
 * outputs + optional rpm/su.
 */
public class SmelteryBlockEntity extends AbstractMachineBlockEntity implements RpmConsumer {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;

    private float inputRpm = 0f;
    private int activeSu = 0;

    private final MachineLayout layout = new MachineLayout(
            org.bukkit.event.inventory.InventoryType.CHEST, 9, "Smeltery")
            .addSlot(SLOT_INPUT, MenuSlotType.INPUT)
            .addSlot(SLOT_OUTPUT, MenuSlotType.OUTPUT);

    public SmelteryBlockEntity(BlockEntity blockEntity) {
        super(blockEntity, 2);
        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        cfg.addInput(IOType.ITEM, Direction.UP);
        cfg.addInput(IOType.ITEM, Direction.NORTH);
        cfg.addInput(IOType.ITEM, Direction.EAST);
        cfg.addInput(IOType.ITEM, Direction.WEST);
        cfg.addOutput(IOType.ITEM, Direction.DOWN);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.INPUT, SLOT_INPUT);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.OUTPUT, SLOT_OUTPUT);
        setIOConfiguration(cfg);

        layout.setTitleComponent(MenuText.noI(MenuText.tr("polyfill.ui.smeltery_title", NamedTextColor.GOLD)));
        layout.setDynamicProvider(4, (m, t) -> {
            SmelteryBlockEntity s = (SmelteryBlockEntity) m;
            int need = currentNeededRpm(s);
            boolean ok = need == 0 || s.inputRpm >= need;
            return MenuText.icon(ok ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                    s.isProcessing() ? MenuText.tr("polyfill.ui.processing", NamedTextColor.GREEN)
                            : MenuText.tr("polyfill.ui.idle", NamedTextColor.RED),
                    MenuText.kv("polyfill.ui.rpm", NamedTextColor.GRAY,
                            String.format("%.0f", s.inputRpm) + (need > 0 ? " / " + need : ""), NamedTextColor.WHITE));
        });
    }

    private static int currentNeededRpm(SmelteryBlockEntity s) {
        ItemStack in = s.getItem(SLOT_INPUT);
        if (in == null || in.isEmpty())
            return 0;
        for (AbstractProcessingRecipe r : RecipeManager.getRecipes(s.getMachineId())) {
            if (s.matchesInputs(r, in))
                return r.getMinRpm();
        }
        return 0;
    }

    @Override
    public MachineLayout getLayout() {
        return layout;
    }

    @Override
    protected boolean requiresFuel() {
        return false; // mechanical power, not fuel
    }

    @Override
    protected String getMachineId() {
        return "smeltery";
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        ItemStack input = getItem(SLOT_INPUT);
        if (input == null || input.isEmpty())
            return null;
        for (AbstractProcessingRecipe r : RecipeManager.getRecipes(getMachineId())) {
            if (matchesInputs(r, input))
                return r;
        }
        return null;
    }

    private boolean matchesInputs(AbstractProcessingRecipe recipe, ItemStack input) {
        boolean any = false;
        for (RecipeInput in : recipe.getInputs()) {
            if (in instanceof ItemInput || in instanceof CraftEngineItemInput || in instanceof TagInput) {
                if (!in.matches(input))
                    return false;
                any = true;
            }
        }
        return any;
    }

    @Override
    protected boolean canProcess(Level level, AbstractProcessingRecipe recipe) {
        if (!super.canProcess(level, recipe))
            return false;
        this.activeSu = recipe.getSuCost();
        // Mechanical gate: only run when the driving motor delivers enough RPM.
        return recipe.getMinRpm() <= 0 || inputRpm >= recipe.getMinRpm();
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        if (output instanceof ItemOutput io) {
            ItemStack out = (ItemStack) io.getOutput();
            ItemStack cur = getItem(SLOT_OUTPUT);
            if (cur == null || cur.isEmpty())
                return true;
            if (ItemStack.isSameItem(cur, out))
                return cur.getCount() + out.getCount() <= cur.getMaxStackSize();
            return false;
        }
        return true;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        for (RecipeInput in : recipe.getInputs()) {
            if (in instanceof ItemInput || in instanceof CraftEngineItemInput || in instanceof TagInput) {
                int amt = in.getAmount();
                removeItem(SLOT_INPUT, amt);
            }
        }
    }

    @Override
    public void tick(Level level, BlockPos pos, net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (!level.isClientSide()) {
            // PULL rpm from the strongest adjacent motor (robust — works regardless of
            // which way the motor faces, unlike relying on the motor's push).
            this.activeMotor = null;
            float best = 0f;
            for (Direction d : Direction.values()) {
                BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, getMachinePos().relative(d));
                if (be != null && be.controller instanceof RpmProvider p && p.getRpm() > best) {
                    best = p.getRpm();
                    this.activeMotor = p;
                }
            }
            this.inputRpm = best;
        }
        super.tick(level, pos, state); // runs the recipe loop (progress/process); reads inputRpm
        if (!level.isClientSide() && isProcessing() && activeSu > 0 && activeMotor != null) {
            // Draw our SU from that motor (it burns more vapor / can overstress).
            activeMotor.reportStressLoad(activeSu);
        }
    }

    private RpmProvider activeMotor;

    // ---- RpmConsumer (the motor pushes its RPM into the block it faces) ----
    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return inputRpm;
    }
}
