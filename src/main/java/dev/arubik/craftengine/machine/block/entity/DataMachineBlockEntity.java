/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.ChunkPos
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.inventory.CraftInventory
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.joml.Quaternionf
 */
package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.conveyor.ConveyorItemDisplay;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.GuiTitles;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.OverclockMenu;
import dev.arubik.craftengine.machine.menu.RecipeInfoIcon;
import dev.arubik.craftengine.machine.menu.UpgradeMenu;
import dev.arubik.craftengine.machine.menu.bar.BarDefinition;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
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
import dev.arubik.craftengine.machine.render.ModelRendersDriven;
import dev.arubik.craftengine.machine.render.RendererManager;
import dev.arubik.craftengine.machine.render.RendererSpec;
import dev.arubik.craftengine.machine.render.formula.InventoryClass;
import dev.arubik.craftengine.machine.render.formula.PolyContext;
import dev.arubik.craftengine.machine.render.formula.PolyScript;
import dev.arubik.craftengine.machine.render.formula.PolyScriptRegistry;
import dev.arubik.craftengine.machine.render.variable.MachineRenderContext;
import dev.arubik.craftengine.machine.upgrade.UpgradeModifiers;
import dev.arubik.craftengine.network.NetworkClass;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.joml.Quaternionf;

public class DataMachineBlockEntity
extends AbstractMachineBlockEntity
implements RpmConsumer,
RpmProvider,
ModelRendersDriven {
    private final MachineDefinition definition;
    private RendererManager rendererManager;
    private ConveyorItemDisplay[] specDisplays;
    private int[] specDisplayHashes;
    private ServerLevel lastKnownLevel;
    public static volatile boolean SPEC_DISPLAY_DEBUG = false;
    public static volatile boolean SCRIPT_DEBUG = false;
    private static final Set<DataMachineBlockEntity> INSTANCES = Collections.newSetFromMap(new WeakHashMap());
    private float inputRpm = 0.0f;
    private final List<RpmProvider> activeMotors = new ArrayList<RpmProvider>();
    private RpmProvider activeMotor;
    private int lastSuLoad = 0;
    private int stressGrace = 0;
    private int sourceDistance = Integer.MAX_VALUE;
    private float rpmSourceOutput = 0.0f;
    private boolean rpmSourceActive = false;
    private MachineMenuConfig menuConfig = MachineMenuConfig.parse(key -> null);
    private List<MachineBar> bars = List.of();
    private boolean invalidating = false;
    private int actionTickCounter = 0;
    private int page = 0;
    private float overclock = 0.0f;
    private MachineMenu active;
    private int curUnlocked = -1;
    private double curOverclockLimit = 0.0;
    private double curFuelEff = 0.0;
    private double curGeneration = 0.0;
    private Map<Key, List<MachineAttributes.Mod>> upgradeDefs = Map.of();
    private double genBuffer = 0.0;

    public static int reloadAll() {
        int n = 0;
        for (DataMachineBlockEntity be : new ArrayList<DataMachineBlockEntity>(INSTANCES)) {
            if (be.rendererManager != null) {
                try {
                    be.rendererManager.close();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            be.rendererManager = null;
            be.despawnSpecDisplays();
            ++n;
        }
        return n;
    }

    public int sourceDistance() {
        return this.sourceDistance;
    }

    public List<RpmProvider> getActiveMotors() {
        return Collections.unmodifiableList(this.activeMotors);
    }

    public void setRpmSourceOutput(float rpm) {
        this.rpmSourceOutput = rpm;
        boolean bl = this.rpmSourceActive = rpm != 0.0f;
        if (this.rpmSourceActive) {
            this.sourceDistance = 0;
            this.inputRpm = rpm;
        }
    }

    @Override
    public float getRpm() {
        return this.rpmSourceActive ? this.rpmSourceOutput : this.inputRpm;
    }

    @Override
    public boolean isRpmSource() {
        return this.rpmSourceActive;
    }

    @Override
    public float potentialRpm() {
        if (this.rpmSourceActive) {
            return Math.abs(this.rpmSourceOutput);
        }
        return this.sourceDistance < Integer.MAX_VALUE ? Math.abs(this.inputRpm) : 0.0f;
    }

    public boolean isValidOutputFace(Direction face, Level level) {
        if (this.definition == null || !this.definition.noProcessing()) {
            return true;
        }
        Set<String> same = this.definition.rpmOutputFacesRaw();
        Set<String> inv = this.definition.rpmOutputInvertedRaw();
        if (same.isEmpty() && inv.isEmpty() && !this.definition.rpmOutputDeclared()) {
            return true;
        }
        Direction facing = this.getFacing(level);
        return this.rpmFacesContainWithAxis(same, face, facing, level) || this.rpmFacesContainWithAxis(inv, face, facing, level);
    }

    public static boolean rpmFacesContainWithAxisStatic(Set<String> raw, Direction d, Direction facing, ServerLevel level, BlockPos pos) {
        boolean hasAxisNames;
        boolean bl = hasAxisNames = raw.contains("axis_pos") || raw.contains("axis_neg") || raw.contains("axis_perp");
        if (!hasAxisNames) {
            return DataMachineBlockEntity.rpmFacesContain(raw, d, facing);
        }
        try {
            Property axisProp;
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
            if (cs != null && (axisProp = ((BlockDefinition)cs.owner().value()).getProperty("axis")) != null) {
                String axis;
                Direction posDir = switch (axis = String.valueOf(cs.get(axisProp)).toLowerCase()) {
                    case "x" -> Direction.EAST;
                    case "y" -> Direction.UP;
                    default -> Direction.SOUTH;
                };
                Direction.Axis cogAxis = posDir.getAxis();
                Iterator<String> iterator = raw.iterator();
                while (iterator.hasNext()) {
                    boolean match;
                    String s;
                    if (!(match = (switch (s = iterator.next()) {
                        case "axis_pos" -> {
                            if (d == posDir) {
                                yield true;
                            }
                            yield false;
                        }
                        case "axis_neg" -> {
                            if (d == posDir.getOpposite()) {
                                yield true;
                            }
                            yield false;
                        }
                        case "axis_perp" -> {
                            if (d.getAxis() != cogAxis) {
                                yield true;
                            }
                            yield false;
                        }
                        default -> false;
                    }))) continue;
                    return true;
                }
                HashSet<String> nonAxis = new HashSet<String>(raw);
                nonAxis.remove("axis_pos");
                nonAxis.remove("axis_neg");
                nonAxis.remove("axis_perp");
                if (!nonAxis.isEmpty()) {
                    return DataMachineBlockEntity.rpmFacesContain(nonAxis, d, facing);
                }
                return false;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return DataMachineBlockEntity.rpmFacesContain(raw, d, facing);
    }

    public Direction getFacingPublic(Level level) {
        return this.getFacing(level);
    }

    boolean rpmFacesContainWithAxis(Set<String> raw, Direction d, Direction facing, Level level) {
        boolean hasAxisNames;
        boolean bl = hasAxisNames = raw.contains("axis_pos") || raw.contains("axis_neg") || raw.contains("axis_perp");
        if (!hasAxisNames) {
            return DataMachineBlockEntity.rpmFacesContain(raw, d, facing);
        }
        try {
            Property axisProp;
            BlockState bs = level.getBlockState(this.getMachinePos());
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(bs).orElse(null);
            if (cs != null && (axisProp = ((BlockDefinition)cs.owner().value()).getProperty("axis")) != null) {
                String axis;
                Direction posDir = switch (axis = String.valueOf(cs.get(axisProp)).toLowerCase()) {
                    case "x" -> Direction.EAST;
                    case "y" -> Direction.UP;
                    default -> Direction.SOUTH;
                };
                Direction.Axis cogAxis = posDir.getAxis();
                Iterator<String> iterator = raw.iterator();
                while (iterator.hasNext()) {
                    boolean match;
                    String s;
                    if (!(match = (switch (s = iterator.next()) {
                        case "axis_pos" -> {
                            if (d == posDir) {
                                yield true;
                            }
                            yield false;
                        }
                        case "axis_neg" -> {
                            if (d == posDir.getOpposite()) {
                                yield true;
                            }
                            yield false;
                        }
                        case "axis_perp" -> {
                            if (d.getAxis() != cogAxis) {
                                yield true;
                            }
                            yield false;
                        }
                        default -> false;
                    }))) continue;
                    return true;
                }
                HashSet<String> nonAxis = new HashSet<String>(raw);
                nonAxis.remove("axis_pos");
                nonAxis.remove("axis_neg");
                nonAxis.remove("axis_perp");
                if (!nonAxis.isEmpty()) {
                    return DataMachineBlockEntity.rpmFacesContain(nonAxis, d, facing);
                }
                return false;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return DataMachineBlockEntity.rpmFacesContain(raw, d, facing);
    }

    @Override
    public float getPower() {
        return this.inputRpm;
    }

    @Override
    public void reportStressLoad(float su) {
        for (RpmProvider m : this.activeMotors) {
            m.reportStressLoad(su);
        }
        if (this.activeMotors.isEmpty() && this.activeMotor != null) {
            this.activeMotor.reportStressLoad(su);
        }
    }

    public DataMachineBlockEntity(BlockEntity blockEntity, MachineDefinition definition) {
        super(blockEntity, definition.menuSize());
        FluidType filter;
        this.definition = definition;
        for (MachineDefinition.TankSpec spec : definition.fluidTanks()) {
            filter = spec.filter() == null ? null : FluidType.REGISTRY.get(spec.filter());
            this.addFluidTank(filter == null ? new FluidTank(spec.name(), spec.capacity()) : new FluidTank(spec.name(), spec.capacity(), filter));
        }
        for (MachineDefinition.TankSpec spec : definition.gasTanks()) {
            GasType gasFilter = spec.filter() == null ? null : GasType.REGISTRY.get(spec.filter());
            this.addGasTank(gasFilter == null ? new GasTank(spec.name(), spec.capacity()) : new GasTank(spec.name(), spec.capacity(), gasFilter));
        }
        if (definition.io() != null) {
            this.setIOConfiguration(definition.io());
        }
        if (!definition.renderers().isEmpty()) {
            this.rendererManager = new RendererManager(definition.renderers(), definition.variables());
            int n = definition.renderers().size();
            this.specDisplays = new ConveyorItemDisplay[n];
            this.specDisplayHashes = new int[n];
        }
        INSTANCES.add(this);
    }

    private void ensureRenderer() {
        if (this.rendererManager == null) {
            MachineDefinition eff;
            MachineDefinition fresh = MachineDefinition.REGISTRY.get(this.definition.id());
            MachineDefinition machineDefinition = eff = fresh != null ? fresh : this.definition;
            if (!eff.renderers().isEmpty()) {
                this.rendererManager = new RendererManager(eff.renderers(), eff.variables());
                int n = eff.renderers().size();
                this.specDisplays = new ConveyorItemDisplay[n];
                this.specDisplayHashes = new int[n];
            }
        }
    }

    @Override
    public RendererManager rendererManager() {
        return this.rendererManager;
    }

    public MachineDefinition definition() {
        return this.definition;
    }

    @Override
    protected String getMachineId() {
        return this.definition.recipeType();
    }

    @Override
    public int[] getInputSlots() {
        return this.definition.inputSlots();
    }

    @Override
    public int[] getOutputSlots() {
        return this.definition.outputSlots();
    }

    @Override
    public int[] getFuelSlots() {
        return this.definition.fuelSlots();
    }

    @Override
    public int[] getUpgradeSlots() {
        return this.definition.upgrades().slots();
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        for (AbstractProcessingRecipe recipe : RecipeManager.getRecipes(this.getMachineId())) {
            if (this.matchingInputSlot(recipe) < 0) continue;
            return recipe;
        }
        return null;
    }

    protected int matchingInputSlot(AbstractProcessingRecipe recipe) {
        boolean wantsItem = false;
        for (RecipeInput input : recipe.getInputs()) {
            if (!DataMachineBlockEntity.isItemInput(input)) continue;
            wantsItem = true;
        }
        if (!wantsItem) {
            return this.definition.inputSlots().length > 0 ? this.definition.inputSlots()[0] : 0;
        }
        for (Object slot : this.definition.inputSlots()) {
            net.minecraft.world.item.ItemStack stack = this.getItem((int)slot);
            if (stack == null || stack.isEmpty()) continue;
            boolean all = true;
            for (RecipeInput input : recipe.getInputs()) {
                if (!DataMachineBlockEntity.isItemInput(input) || input.matches(stack) && stack.getCount() >= input.getAmount()) continue;
                all = false;
                break;
            }
            if (!all) continue;
            return (int)slot;
        }
        return -1;
    }

    private static boolean isItemInput(RecipeInput input) {
        return input instanceof ItemInput || input instanceof CraftEngineItemInput || input instanceof TagInput;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        if (!(output instanceof ItemOutput)) {
            return true;
        }
        ItemOutput itemOutput = (ItemOutput)output;
        net.minecraft.world.item.ItemStack produced = (net.minecraft.world.item.ItemStack)itemOutput.getOutput();
        for (int slot : this.definition.outputSlots()) {
            net.minecraft.world.item.ItemStack current = this.getItem(slot);
            if (current == null || current.isEmpty()) {
                return true;
            }
            if (!net.minecraft.world.item.ItemStack.isSameItem((net.minecraft.world.item.ItemStack)current, (net.minecraft.world.item.ItemStack)produced) || current.getCount() + produced.getCount() > current.getMaxStackSize()) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        int slot = this.matchingInputSlot(recipe);
        if (slot < 0) {
            return;
        }
        for (RecipeInput input : recipe.getInputs()) {
            if (!DataMachineBlockEntity.isItemInput(input)) continue;
            this.removeItem(slot, input.getAmount());
        }
    }

    public void setMenuConfig(MachineMenuConfig config) {
        this.menuConfig = config != null ? config : MachineMenuConfig.parse(key -> null);
    }

    public void setBars(List<MachineBar> bars) {
        this.bars = bars != null ? bars : List.of();
    }

    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return this.inputRpm;
    }

    private boolean hasPower() {
        if (!this.definition.power().consumesStress()) {
            return true;
        }
        AbstractProcessingRecipe recipe = this.getMatchingRecipe(this.getNMSLevel());
        return recipe == null ? this.inputRpm > 0.0f : this.canProcess(this.getNMSLevel(), recipe);
    }

    @Override
    protected boolean canProcess(Level level, AbstractProcessingRecipe recipe) {
        if (!super.canProcess(level, recipe)) {
            return false;
        }
        if (!this.definition.power().consumesStress()) {
            return true;
        }
        return recipe.getMinRpm() <= 0 || this.inputRpm >= (float)this.effectiveRpm(recipe);
    }

    @Override
    public void tick(Level level, BlockPos pos, ImmutableBlockState state) {
        boolean needsRpm;
        this.ensureRenderer();
        if (!level.isClientSide() && level instanceof ServerLevel) {
            ServerLevel sl;
            this.lastKnownLevel = sl = (ServerLevel)level;
        }
        boolean bl = needsRpm = this.definition.power().consumesStress() || this.definition.noProcessing();
        if (needsRpm && !level.isClientSide()) {
            this.pullRotationalPower(level);
        }
        if (!this.definition.noProcessing()) {
            super.tick(level, pos, state);
            if (this.definition.power().consumesStress() && !level.isClientSide()) {
                this.reportStressLoad();
            }
            if (!level.isClientSide()) {
                this.maybeUpdateActivated(level, pos, state, this.isProcessing());
            }
        } else if (!level.isClientSide()) {
            this.setChanged();
        }
        if (this.rendererManager != null) {
            try {
                float f;
                int n = 0;
                LinkedHashMap<String, double[]> fluidTankData = new LinkedHashMap<String, double[]>();
                for (MachineDefinition.TankSpec tankSpec : this.definition.fluidTanks()) {
                    FluidTank fluidTank = this.fluidTank(tankSpec.name());
                    if (fluidTank == null) continue;
                    FluidStack stored = fluidTank.getFluid(this.getNMSLevel(), this.getMachinePos());
                    fluidTankData.put(tankSpec.name(), new double[]{stored.getAmount(), fluidTank.getCapacity()});
                }
                LinkedHashMap<String, double[]> gasTankData = new LinkedHashMap<String, double[]>();
                for (MachineDefinition.TankSpec tankSpec : this.definition.gasTanks()) {
                    GasTank tank = this.gasTank(tankSpec.name());
                    if (tank == null) continue;
                    GasStack stored = tank.getGas(this.getNMSLevel(), this.getMachinePos());
                    gasTankData.put(tankSpec.name(), new double[]{stored.getAmount(), tank.getCapacity()});
                }
                LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<String, Integer>();
                if (!this.upgradeDefs.isEmpty()) {
                    for (int upSlot : this.definition.upgrades().slots()) {
                        net.minecraft.world.item.ItemStack nmsItem = this.getItem(upSlot);
                        Key uid = this.upgradeItemId(nmsItem);
                        if (uid == null) continue;
                        linkedHashMap.merge(uid.namespace() + ":" + uid.value(), 1, Integer::sum);
                    }
                }
                boolean bl2 = false;
                try {
                    n = level.getBestNeighborSignal(pos);
                }
                catch (Throwable tank) {
                    // empty catch block
                }
                MachineRenderContext ctx = new MachineRenderContext(this.inputRpm, this.overclock, this.curFuelEff, this.progress, this.maxProgress, this.curGeneration, this.isProcessing(), this.inputRpm > 0.0f, this.isOverclocked(), this.burnTime > 0, null, linkedHashMap, fluidTankData, gasTankData, n);
                Direction facing = this.getFacing(level);
                if (facing == null) {
                    f = 0.0f;
                } else {
                    switch (facing) {
                        case SOUTH: {
                            f = 0.0f;
                            break;
                        }
                        case WEST: {
                            f = 90.0f;
                            break;
                        }
                        case NORTH: {
                            f = 180.0f;
                            break;
                        }
                        case EAST: {
                            f = 270.0f;
                            break;
                        }
                        default: {
                            f = 0.0f;
                        }
                    }
                }
                float yaw = f;
                PolyContext machinePolyCtx = this.buildEvalContext();
                if (machinePolyCtx != null) {
                    ctx = ctx.augmented(machinePolyCtx);
                }
                this.rendererManager.tick(ctx, (ServerLevel)level, pos.getX(), pos.getY(), pos.getZ(), yaw);
                this.tickSpecDisplays((ServerLevel)level, pos);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (this.definition != null && this.definition.actionScript() != null && ++this.actionTickCounter >= this.definition.actionInterval()) {
            this.actionTickCounter = 0;
            this.runActionScript(this.definition.actionScript());
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        this.despawnSpecDisplays();
        if (this.rendererManager != null) {
            this.rendererManager.close();
            this.rendererManager = null;
        }
    }

    @Override
    public void unregister() {
        if (this.inputRpm != 0.0f || this.sourceDistance < Integer.MAX_VALUE) {
            this.inputRpm = 0.0f;
            this.sourceDistance = Integer.MAX_VALUE;
            this.activeMotor = null;
            this.activeMotors.clear();
            try {
                Level level = this.getNMSLevel();
                BlockPos pos = this.getMachinePos();
                if (level != null && pos != null) {
                    for (Direction d : Direction.values()) {
                        try {
                            BlockEntityController blockEntityController;
                            BlockEntity adjBe = BukkitBlockEntityTypes.getIfLoaded(level, pos.relative(d));
                            if (adjBe == null || !((blockEntityController = adjBe.controller) instanceof DataMachineBlockEntity)) continue;
                            DataMachineBlockEntity adj = (DataMachineBlockEntity)blockEntityController;
                            adj.invalidateRpm(level);
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        super.unregister();
        this.despawnSpecDisplays();
        if (this.rendererManager != null) {
            this.rendererManager.close();
            this.rendererManager = null;
        }
    }

    public void despawnSpecDisplays(ServerLevel level) {
        if (this.specDisplays == null) {
            return;
        }
        try {
            for (ConveyorItemDisplay d : this.specDisplays) {
                if (d == null) continue;
                d.despawnAll(level);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.specDisplays = null;
        this.specDisplayHashes = null;
    }

    private void despawnSpecDisplays() {
        ServerLevel nmsLevel = this.lastKnownLevel;
        if (nmsLevel == null) {
            try {
                nmsLevel = (ServerLevel)this.getNMSLevel();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (nmsLevel != null) {
            this.despawnSpecDisplays(nmsLevel);
        } else {
            this.specDisplays = null;
            this.specDisplayHashes = null;
        }
    }

    private void tickSpecDisplays(ServerLevel nmsLevel, BlockPos pos) {
        block11: {
            if (this.rendererManager == null || this.specDisplays == null) {
                return;
            }
            try {
                CEWorld ceWorld = this.blockEntity().world();
                if (ceWorld == null) {
                    if (SPEC_DISPLAY_DEBUG) {
                        System.out.println("[CEP specDisplay] ceWorld null at " + String.valueOf(pos));
                    }
                    return;
                }
                net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(pos.getX(), pos.getY(), pos.getZ());
                List<Player> viewers = ceWorld.world().getTrackedBy(new ChunkPos(cePos));
                List<RendererSpec> specs = this.rendererManager.specs();
                net.minecraft.world.item.ItemStack[] items = this.rendererManager.currentItems();
                if (SPEC_DISPLAY_DEBUG) {
                    System.out.println("[CEP specDisplay] pos=" + String.valueOf(pos) + " viewers=" + viewers.size() + " specs=" + specs.size());
                }
                for (int i = 0; i < specs.size(); ++i) {
                    RendererSpec rendererSpec = specs.get(i);
                    if (!(rendererSpec instanceof RendererSpec.ItemDisplaySpec)) continue;
                    RendererSpec.ItemDisplaySpec idSpec = (RendererSpec.ItemDisplaySpec)rendererSpec;
                    RendererManager.EvalResult er = this.rendererManager.evalResult(i);
                    net.minecraft.world.item.ItemStack item = items[i];
                    if (SPEC_DISPLAY_DEBUG) {
                        System.out.println("[CEP specDisplay]  spec[" + i + "] active=" + String.valueOf(er != null ? Boolean.valueOf(er.active) : "null") + " item=" + String.valueOf(item != null ? item.getItem() : "null") + " itemDisplay=" + String.valueOf(er != null ? er.itemDisplay : "null"));
                    }
                    if (er != null && er.active && item != null && !item.isEmpty() && er.itemDisplay != null) {
                        if (this.specDisplays[i] == null) {
                            this.specDisplays[i] = new ConveyorItemDisplay();
                        }
                        RendererSpec.EvaluatedItemDisplay eid = er.itemDisplay;
                        double wx = (double)pos.getX() + 0.5 + eid.offsetX();
                        double wy = (double)pos.getY() + eid.offsetY();
                        double wz = (double)pos.getZ() + 0.5 + eid.offsetZ();
                        Quaternionf q = new Quaternionf().rotateY((float)Math.toRadians(eid.rotY())).rotateX((float)Math.toRadians(eid.rotX())).rotateZ((float)Math.toRadians(eid.rotZ()));
                        boolean lightChanged = this.specDisplays[i].setLightFromLevel(nmsLevel, wx, wy, wz);
                        this.specDisplays[i].setScale(eid.scale());
                        this.specDisplays[i].setRotation(q);
                        boolean rotDirty = this.specDisplays[i].consumeRotationDirty();
                        int h = item.hashCode();
                        this.specDisplays[i].setNmsItem(item);
                        List<Player> effectiveViewers = viewers;
                        if (er.qualifyingPlayers != null) {
                            effectiveViewers = viewers.stream().filter(v -> {
                                org.bukkit.entity.Player bukkit;
                                Object pp = v.platformPlayer();
                                return pp instanceof org.bukkit.entity.Player && er.qualifyingPlayers.contains((bukkit = (org.bukkit.entity.Player)pp).getUniqueId());
                            }).collect(Collectors.toList());
                        }
                        this.specDisplays[i].render(effectiveViewers, wx, wy, wz, lightChanged || h != this.specDisplayHashes[i] || rotDirty);
                        this.specDisplayHashes[i] = h;
                        continue;
                    }
                    if (this.specDisplays[i] == null) continue;
                    this.specDisplays[i].despawnAll(nmsLevel);
                    this.specDisplays[i] = null;
                    this.specDisplayHashes[i] = 0;
                }
            }
            catch (Throwable e) {
                if (!SPEC_DISPLAY_DEBUG) break block11;
                System.out.println("[CEP specDisplay] EXCEPTION: " + String.valueOf(e));
                e.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invalidateRpm(Level level) {
        if (this.invalidating) {
            return;
        }
        if (this.definition == null) {
            return;
        }
        if (!this.definition.power().consumesStress() && !this.definition.noProcessing()) {
            return;
        }
        this.invalidating = true;
        try {
            BlockPos pos;
            float before = this.inputRpm;
            this.pullRotationalPower(level);
            if (before != this.inputRpm && (pos = this.getMachinePos()) != null) {
                for (Direction d : Direction.values()) {
                    try {
                        BlockEntityController blockEntityController;
                        BlockEntity adjBe = BukkitBlockEntityTypes.getIfLoaded(level, pos.relative(d));
                        if (adjBe == null || !((blockEntityController = adjBe.controller) instanceof DataMachineBlockEntity)) continue;
                        DataMachineBlockEntity adj = (DataMachineBlockEntity)blockEntityController;
                        adj.invalidateRpm(level);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
                BlockState bs = level.getBlockState(pos);
                level.updateNeighborsAt(pos, bs.getBlock());
            }
        }
        finally {
            this.invalidating = false;
        }
    }

    private void pullRotationalPower(Level level) {
        Object p2;
        boolean hasAxisPerp;
        boolean hasRpmInput;
        this.activeMotor = null;
        this.activeMotors.clear();
        float bestPotential = 0.0f;
        float delivered = 0.0f;
        net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(this.getMachinePos().getX(), this.getMachinePos().getY(), this.getMachinePos().getZ());
        EnumSet<Direction> autoFaces = null;
        try {
            BlockState bs = level.getBlockState(this.getMachinePos());
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(bs).orElse(null);
            if (cs != null) {
                Property shaftProp = ((BlockDefinition)cs.owner().value()).getProperty("axis");
                Property gearProp = ((BlockDefinition)cs.owner().value()).getProperty("gear_axis");
                if (shaftProp != null) {
                    String v;
                    autoFaces = switch (v = String.valueOf(cs.get(shaftProp)).toLowerCase()) {
                        case "x" -> EnumSet.of(Direction.EAST, Direction.WEST);
                        case "y" -> EnumSet.of(Direction.UP, Direction.DOWN);
                        default -> EnumSet.of(Direction.NORTH, Direction.SOUTH);
                    };
                }
            }
        }
        catch (Throwable bs) {
            // empty catch block
        }
        EnumSet<Direction> autoFacesFinal = autoFaces;
        boolean bl = hasRpmInput = autoFacesFinal != null || this.definition != null && !this.definition.rpmInputFacesRaw().isEmpty();
        if (!hasRpmInput) {
            this.inputRpm = 0.0f;
            this.sourceDistance = Integer.MAX_VALUE;
            return;
        }
        int bestSourceDist = Integer.MAX_VALUE;
        for (Direction d : Direction.values()) {
            float pot;
            int providerDist;
            BlockEntityController blockEntityController;
            if (this.definition == null || this.definition.rpmInputFacesRaw().isEmpty() ? autoFacesFinal != null && !autoFacesFinal.contains(d) : !this.rpmFacesContainWithAxis(this.definition.rpmInputFacesRaw(), d, this.getFacing(level), level)) continue;
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, this.getMachinePos().relative(d));
            if (be == null || !((blockEntityController = be.controller) instanceof RpmProvider)) continue;
            RpmProvider p = (RpmProvider)blockEntityController;
            if (p instanceof DataMachineBlockEntity) {
                Set<String> perpFilter;
                DataMachineBlockEntity dm = (DataMachineBlockEntity)p;
                providerDist = dm.sourceDistance;
                if (providerDist >= this.sourceDistance || !dm.isValidOutputFace(d.getOpposite(), level)) continue;
                if (!dm.definition.rpmOutputBlockFilter().isEmpty() && (perpFilter = dm.definition.rpmOutputBlockFilter().get("axis_perp")) != null) {
                    boolean weAreAllowed;
                    boolean bl2 = weAreAllowed = this.definition != null && this.definition.rpmInputFacesRaw().contains("axis_perp");
                    if (!weAreAllowed) {
                        continue;
                    }
                }
            } else {
                providerDist = 0;
            }
            if (this.definition != null && !this.definition.rpmInputFacesRaw().isEmpty()) {
                Direction myFacing = this.getFacing(level);
                if (!this.rpmFacesContainWithAxis(this.definition.rpmInputFacesRaw(), d, myFacing, level)) {
                    continue;
                }
            } else if (p instanceof DataMachineBlockEntity) {
                DataMachineBlockEntity dm2 = (DataMachineBlockEntity)p;
                if (dm2.definition != null) {
                    boolean providerOutputsAxisPerp;
                    boolean bl3 = providerOutputsAxisPerp = dm2.definition.rpmOutputInvertedRaw().contains("axis_perp") || dm2.definition.rpmOutputFacesRaw().contains("axis_perp");
                    if (providerOutputsAxisPerp) {
                        boolean weAcceptAxisPerp;
                        boolean bl4 = weAcceptAxisPerp = this.definition != null && this.definition.rpmInputFacesRaw().contains("axis_perp");
                        if (!weAcceptAxisPerp) continue;
                    }
                }
            }
            if ((pot = p.potentialRpm()) <= 0.0f || !p.rpmReaches(cePos)) continue;
            float raw = p.getRpm();
            if (p instanceof DataMachineBlockEntity) {
                Set<String> inv2;
                DataMachineBlockEntity dm2 = (DataMachineBlockEntity)p;
                Set<String> set = inv2 = dm2.definition != null ? dm2.definition.rpmOutputInvertedRaw() : Set.of();
                if (!inv2.isEmpty() && DataMachineBlockEntity.rpmFacesContain(inv2, d.getOpposite(), dm2.getFacing(level))) {
                    raw = -raw;
                }
            }
            if (delivered != 0.0f && raw != 0.0f && Math.signum(delivered) != Math.signum(raw) && !p.isRpmSource() && this.activeMotor != null && !this.activeMotor.isRpmSource()) {
                try {
                    if (bestPotential < pot) {
                        level.destroyBlock(this.getMachinePos(), true);
                        return;
                    }
                    level.destroyBlock(this.getMachinePos().relative(d), true);
                    continue;
                }
                catch (Throwable dm2) {
                    // empty catch block
                }
            }
            if (providerDist < bestSourceDist || providerDist == bestSourceDist && pot > bestPotential) {
                bestSourceDist = providerDist;
                bestPotential = pot;
                delivered = raw;
                this.activeMotor = p;
                this.activeMotors.clear();
                this.activeMotors.add(p);
                continue;
            }
            if (providerDist != bestSourceDist || pot != bestPotential || raw == 0.0f || Math.signum(raw) != Math.signum(delivered)) continue;
            this.activeMotors.add(p);
        }
        boolean bl5 = hasAxisPerp = this.definition != null && this.definition.rpmInputFacesRaw().contains("axis_perp");
        if (hasAxisPerp && autoFacesFinal != null) {
            try {
                Property myAxisProp;
                BlockState myBs = level.getBlockState(this.getMachinePos());
                ImmutableBlockState myCe = BlockStateUtils.getOptionalCustomBlockState(myBs).orElse(null);
                if (myCe != null && (myAxisProp = ((BlockDefinition)myCe.owner().value()).getProperty("axis")) != null) {
                    String myAxis;
                    Direction.Axis cogAxis = switch (myAxis = String.valueOf(myCe.get(myAxisProp)).toLowerCase()) {
                        case "x" -> Direction.Axis.X;
                        case "y" -> Direction.Axis.Y;
                        default -> Direction.Axis.Z;
                    };
                    Direction.Axis[] axes = Direction.Axis.values();
                    ArrayList<Direction> perpDirs = new ArrayList<Direction>();
                    for (Direction pd : Direction.values()) {
                        if (pd.getAxis() == cogAxis) continue;
                        perpDirs.add(pd);
                    }
                    for (int i = 0; i < perpDirs.size(); ++i) {
                        for (int j = i + 1; j < perpDirs.size(); ++j) {
                            Direction da = (Direction)perpDirs.get(i);
                            Direction db = (Direction)perpDirs.get(j);
                            if (da.getAxis() == db.getAxis()) continue;
                            int[] nArray = new int[]{1, -1};
                            int n = nArray.length;
                            for (int k = 0; k < n; ++k) {
                                int sa = nArray[k];
                                int[] nArray2 = new int[]{1, -1};
                                int n2 = nArray2.length;
                                for (int i2 = 0; i2 < n2; ++i2) {
                                    float pot2;
                                    Property theirAxisProp;
                                    BlockState theirBs;
                                    ImmutableBlockState theirCe;
                                    BlockEntityController blockEntityController;
                                    RpmProvider diagRpm;
                                    int sb = nArray2[i2];
                                    BlockPos diag = this.getMachinePos().relative(sa > 0 ? da : da.getOpposite()).relative(sb > 0 ? db : db.getOpposite());
                                    BlockEntity diagBe = BukkitBlockEntityTypes.getIfLoaded(level, diag);
                                    if (diagBe == null || !((blockEntityController = diagBe.controller) instanceof RpmProvider) || !((diagRpm = (RpmProvider)blockEntityController) instanceof DataMachineBlockEntity)) continue;
                                    DataMachineBlockEntity dm3 = (DataMachineBlockEntity)diagRpm;
                                    if (dm3.definition == null || (theirCe = (ImmutableBlockState)BlockStateUtils.getOptionalCustomBlockState((theirBs = level.getBlockState(diag))).orElse(null)) == null || (theirAxisProp = ((BlockDefinition)theirCe.owner().value()).getProperty("axis")) == null) continue;
                                    String theirAxis = String.valueOf(theirCe.get(theirAxisProp)).toLowerCase();
                                    int pd = dm3.sourceDistance;
                                    if (pd >= this.sourceDistance || (pot2 = diagRpm.potentialRpm()) <= 0.0f || dm3.definition.rpmLargeCog() || !theirAxis.equals(myAxis)) continue;
                                    float providerRatio = dm3.definition.rpmRatio();
                                    float raw2 = -diagRpm.getRpm() * providerRatio;
                                    if (pd >= bestSourceDist && (pd != bestSourceDist || !(pot2 > bestPotential))) continue;
                                    bestSourceDist = pd;
                                    bestPotential = pot2;
                                    delivered = raw2;
                                    this.activeMotor = diagRpm;
                                    this.activeMotors.clear();
                                    this.activeMotors.add(diagRpm);
                                }
                            }
                        }
                    }
                }
            }
            catch (Throwable myBs) {
                // empty catch block
            }
        }
        if (this.definition != null && this.definition.rpmLargeCog() && autoFacesFinal != null) {
            try {
                Property myAP2;
                BlockState myBs2 = level.getBlockState(this.getMachinePos());
                ImmutableBlockState myCe2 = BlockStateUtils.getOptionalCustomBlockState(myBs2).orElse(null);
                if (myCe2 != null && (myAP2 = ((BlockDefinition)myCe2.owner().value()).getProperty("axis")) != null) {
                    String myAx2 = String.valueOf(myCe2.get(myAP2)).toLowerCase();
                    Direction.Axis myCA2 = myAx2.equals("x") ? Direction.Axis.X : (myAx2.equals("y") ? Direction.Axis.Y : Direction.Axis.Z);
                    Direction myAxisPos = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)myCA2);
                    for (Direction perpD : Direction.values()) {
                        if (perpD.getAxis() == myCA2) continue;
                        for (int sa : new int[]{1, -1}) {
                            for (int sp : new int[]{1, -1}) {
                                float lraw;
                                float lPot;
                                int ld;
                                String tAx;
                                Property tAP;
                                BlockState tBs;
                                ImmutableBlockState tCe;
                                RpmProvider lp2;
                                BlockPos lp = this.getMachinePos().relative(sa > 0 ? myAxisPos : myAxisPos.getOpposite()).relative(sp > 0 ? perpD : perpD.getOpposite());
                                BlockEntity lbe = BukkitBlockEntityTypes.getIfLoaded(level, lp);
                                if (lbe == null || !((p2 = lbe.controller) instanceof RpmProvider) || !((lp2 = (RpmProvider)p2) instanceof DataMachineBlockEntity)) continue;
                                DataMachineBlockEntity ldm = (DataMachineBlockEntity)lp2;
                                if (ldm.definition == null || !ldm.definition.rpmLargeCog() || (tCe = (ImmutableBlockState)BlockStateUtils.getOptionalCustomBlockState((tBs = level.getBlockState(lp))).orElse(null)) == null || (tAP = ((BlockDefinition)tCe.owner().value()).getProperty("axis")) == null || (tAx = String.valueOf(tCe.get(tAP)).toLowerCase()).equals(myAx2)) continue;
                                Direction.Axis theirCA = tAx.equals("x") ? Direction.Axis.X : (tAx.equals("y") ? Direction.Axis.Y : Direction.Axis.Z);
                                if (theirCA != perpD.getAxis() || (ld = ldm.sourceDistance) >= this.sourceDistance || (lPot = lp2.potentialRpm()) <= 0.0f) continue;
                                BlockPos ldiff = lp.subtract((Vec3i)this.getMachinePos());
                                int fromAxisDiff = myCA2 == Direction.Axis.X ? ldiff.getX() : (myCA2 == Direction.Axis.Y ? ldiff.getY() : ldiff.getZ());
                                int toAxisDiff = theirCA == Direction.Axis.X ? ldiff.getX() : (theirCA == Direction.Axis.Y ? ldiff.getY() : ldiff.getZ());
                                float f = lraw = fromAxisDiff > 0 ^ toAxisDiff > 0 ? -lp2.getRpm() : lp2.getRpm();
                                if (ld < bestSourceDist || ld == bestSourceDist && lPot > bestPotential) {
                                    bestSourceDist = ld;
                                    bestPotential = lPot;
                                    delivered = lraw;
                                    this.activeMotor = lp2;
                                    this.activeMotors.clear();
                                    this.activeMotors.add(lp2);
                                    continue;
                                }
                                if (ld != bestSourceDist || lPot != bestPotential) continue;
                                this.activeMotors.add(lp2);
                            }
                        }
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        this.inputRpm = delivered;
        this.sourceDistance = bestSourceDist < Integer.MAX_VALUE ? bestSourceDist + 1 : Integer.MAX_VALUE;
    }

    private void reportStressLoad() {
        int demand;
        AbstractProcessingRecipe recipe = this.getMatchingRecipe(this.getNMSLevel());
        int n = demand = recipe != null ? this.effectiveSu(recipe) : 0;
        if (demand > 0) {
            this.lastSuLoad = demand;
            this.stressGrace = this.definition.power().stressGraceTicks();
        }
        if (this.stressGrace > 0 && this.lastSuLoad > 0) {
            for (RpmProvider m : this.activeMotors) {
                m.reportStressLoad(this.lastSuLoad);
            }
            if (this.activeMotors.isEmpty() && this.activeMotor != null) {
                this.activeMotor.reportStressLoad(this.lastSuLoad);
            }
            --this.stressGrace;
        }
    }

    private ItemStack infoIcon() {
        MachineDefinition.InfoSpec spec = this.definition.info();
        if (!spec.isTank()) {
            return RecipeInfoIcon.build(this, this.getMachineId(), this.getUpgradeModifiers().speedMultiplier(), this.curGeneration);
        }
        String source = spec.source();
        boolean gas = source.startsWith("gas");
        String tankName = source.contains(":") ? source.substring(source.indexOf(58) + 1) : "";
        long amount = 0L;
        long capacity = 0L;
        Component contents = MenuText.textOrTranslatable(gas ? "polyfill.gas.empty" : "polyfill.liquid.empty", NamedTextColor.WHITE);
        Material material = Material.BUCKET;
        Object tank;
        Object stored;
        if (gas) {
            tank = this.gasTank(tankName);
            if (tank != null) {
                stored = ((GasTank)tank).getGas(this.getNMSLevel(), this.getMachinePos());
                amount = ((GasStack)stored).getAmount();
                capacity = ((GasTank)tank).getCapacity();
                if (!((GasStack)stored).isEmpty()) {
                    contents = MenuText.textOrTranslatable(((GasStack)stored).getType().translationKey(), NamedTextColor.WHITE);
                }
            }
        } else {
            tank = this.fluidTank(tankName);
            if (tank != null) {
                stored = ((FluidTank)tank).getFluid(this.getNMSLevel(), this.getMachinePos());
                amount = ((FluidStack)stored).getAmount();
                capacity = ((FluidTank)tank).getCapacity();
                if (!((FluidStack)stored).isEmpty()) {
                    contents = MenuText.textOrTranslatable(((FluidStack)stored).getType().translationKey(), NamedTextColor.WHITE);
                    if (((FluidStack)stored).getType() == FluidType.LAVA) {
                        material = Material.LAVA_BUCKET;
                    } else if (((FluidStack)stored).getType() == FluidType.WATER) {
                        material = Material.WATER_BUCKET;
                    }
                }
            }
        }
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            NamedTextColor gray = NamedTextColor.GRAY;
            NamedTextColor aqua = NamedTextColor.AQUA;
            meta.displayName(MenuText.noI(MenuText.tr(gas ? "polyfill.ui.gas" : "polyfill.ui.fluid", aqua).append((Component)Component.text((String)": ", (TextColor)gray)).append(contents)));
            meta.lore(List.of(MenuText.noI((Component)Component.text((String)(amount + " / " + capacity + " mB"), (TextColor)gray))));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private String barSource(String barId) {
        for (MachineDefinition.BarRef ref : this.definition.bars()) {
            if (!ref.bar().value().equals(barId)) continue;
            return ref.source();
        }
        return barId;
    }

    private FluidTank fluidTank(String name) {
        for (FluidTank t : this.fluidTanks) {
            if (!t.getName().equalsIgnoreCase(name)) continue;
            return t;
        }
        return this.fluidTanks.isEmpty() ? null : (FluidTank)this.fluidTanks.get(0);
    }

    private GasTank gasTank(String name) {
        for (GasTank t : this.gasTanks) {
            if (!t.getName().equalsIgnoreCase(name)) continue;
            return t;
        }
        return this.gasTanks.isEmpty() ? null : (GasTank)this.gasTanks.get(0);
    }

    @Override
    public double[] barStat(String id) {
        String source = this.barSource(id);
        if (source.startsWith("fluid:") || source.equals("fluid")) {
            FluidTank tank = this.fluidTank(source.startsWith("fluid:") ? source.substring(6) : "");
            if (tank == null) {
                return new double[]{0.0, 0.0};
            }
            return new double[]{tank.getFluid(this.getNMSLevel(), this.getMachinePos()).getAmount(), tank.getCapacity()};
        }
        if (source.startsWith("gas:") || source.equals("gas")) {
            GasTank tank = this.gasTank(source.startsWith("gas:") ? source.substring(4) : "");
            if (tank == null) {
                return new double[]{0.0, 0.0};
            }
            return new double[]{tank.getGas(this.getNMSLevel(), this.getMachinePos()).getAmount(), tank.getCapacity()};
        }
        if (source.equals("fuel")) {
            return new double[]{this.burnTime, Math.max(1, this.maxBurnTime)};
        }
        if (source.equals("progress")) {
            return new double[]{this.getProgress(), Math.max(1, this.getMaxProgress())};
        }
        if (source.equals("rpm") || source.equals("power")) {
            return new double[]{this.isProcessing() || this.hasPower() ? 100.0 : 0.0, 100.0};
        }
        return super.barStat(id);
    }

    @Override
    public Map<String, String> barPlaceholders(String id) {
        String source = this.barSource(id);
        if (source.equals("rpm") || source.equals("power")) {
            AbstractProcessingRecipe recipe = this.getMatchingRecipe(this.getNMSLevel());
            HashMap<String, String> out = new HashMap<String, String>();
            out.put("rpm", String.valueOf((int)this.getInputRpm()));
            out.put("req", String.valueOf(recipe != null ? this.effectiveRpm(recipe) : 0));
            out.put("su", String.valueOf(recipe != null ? this.effectiveSu(recipe) : 0));
            return out;
        }
        return super.barPlaceholders(id);
    }

    @Override
    public String barSubtype(String id) {
        String source = this.barSource(id);
        if (source.startsWith("fluid:") || source.equals("fluid")) {
            FluidTank tank = this.fluidTank(source.startsWith("fluid:") ? source.substring(6) : "");
            if (tank == null) {
                return "";
            }
            FluidStack stored = tank.getFluid(this.getNMSLevel(), this.getMachinePos());
            return stored.isEmpty() ? "" : stored.getType().name().toLowerCase(Locale.ROOT);
        }
        if (source.startsWith("gas:") || source.equals("gas")) {
            GasTank tank = this.gasTank(source.startsWith("gas:") ? source.substring(4) : "");
            if (tank == null) {
                return "";
            }
            GasStack stored = tank.getGas(this.getNMSLevel(), this.getMachinePos());
            return stored.isEmpty() ? "" : stored.getType().name().toLowerCase(Locale.ROOT);
        }
        return super.barSubtype(id);
    }

    public void setUpgradeDefs(Map<Key, List<MachineAttributes.Mod>> defs) {
        this.upgradeDefs = defs == null ? Map.of() : defs;
    }

    private List<MachineAttributes.Mod> modsOf(int slot) {
        Key id = this.upgradeItemId(this.getItem(slot));
        return id == null ? null : this.upgradeDefs.get(id);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    protected void recomputeUpgrades() {
        if (this.upgradeDefs.isEmpty()) {
            super.recomputeUpgrades();
            return;
        }
        int count = this.definition.upgrades().size();
        ArrayList<MachineAttributes.Mod> all = new ArrayList<MachineAttributes.Mod>();
        int[] slots = this.definition.upgrades().slots();
        for (int i = 0; i < count; ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(slots[i]);
            if (m == null) continue;
            all.addAll(m);
        }
        int extra = (int)Math.round(MachineAttributes.compute(all).getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        this.curUnlocked = Math.max(this.definition.upgrades().baseUnlocked(), Math.min(count, this.definition.upgrades().baseUnlocked() + extra));
        ArrayList<MachineAttributes.Mod> active = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < this.curUnlocked; ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(slots[i]);
            if (m == null) continue;
            active.addAll(m);
        }
        Map<Key, Double> attrs = MachineAttributes.compute(active);
        this.curGeneration = DataMachineBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        this.curOverclockLimit = DataMachineBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curFuelEff = DataMachineBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.FUEL_EFFICIENCY, 0.0), -32.0, 0.95);
        this.overclock = (float)DataMachineBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        this.upgradeModifiers = new UpgradeModifiers(1.0 + (double)this.overclock, 1.0, 0.0);
    }

    private int unlockedSlots() {
        if (this.curUnlocked < 0) {
            this.curUnlocked = this.definition.upgrades().baseUnlocked();
        }
        return Math.min(this.definition.upgrades().size(), Math.max(0, this.curUnlocked));
    }

    @Override
    public MachineMenu getMenu() {
        if (this.active == null) {
            this.active = new MachineMenu(this, this.getLayout());
            this.active.syncFromMachine();
            this.menu = this.active;
        }
        return this.active;
    }

    public void openPage(org.bukkit.entity.Player player, int newPage) {
        this.page = newPage;
        this.active = new MachineMenu(this, this.getLayout());
        this.active.syncFromMachine();
        this.menu = this.active;
        this.active.open(player);
    }

    @Override
    public void openMenu(net.minecraft.world.entity.player.Player player) {
        this.openPage((org.bukkit.entity.Player)player.getBukkitEntity(), 0);
    }

    @Override
    public MachineLayout getLayout() {
        if (this.definition.upgrades().isInline()) {
            return this.buildMainLayout();
        }
        return switch (this.page) {
            case 1 -> this.buildUpgradeLayout();
            case 2 -> this.buildOverclockLayout();
            default -> this.buildMainLayout();
        };
    }

    private MachineLayout buildUpgradeLayout() {
        return UpgradeMenu.build(this.getMachineId(), this.definition.upgrades().size(), this::unlockedSlots, (m, p) -> ((DataMachineBlockEntity)m).openPage((org.bukkit.entity.Player)p, 0), null);
    }

    private MachineLayout buildOverclockLayout() {
        return OverclockMenu.build(this.getMachineId(), NamedTextColor.RED, () -> this.overclock, () -> (float)this.curOverclockLimit, this::bumpOverclock, p -> this.openPage((org.bukkit.entity.Player)p, 0));
    }

    @Override
    protected void process(Level level, AbstractProcessingRecipe recipe) {
        this.consumeInputs(level, recipe);
        int extra = 0;
        if (this.curGeneration > 0.0) {
            this.genBuffer += this.curGeneration;
            while (this.genBuffer >= 1.0) {
                this.genBuffer -= 1.0;
                ++extra;
            }
        }
        for (int set = 0; set < 1 + extra; ++set) {
            for (RecipeOutput output : recipe.getOutputs()) {
                output.dispense(level, this);
            }
        }
        if (extra > 0) {
            this.setChanged();
        }
    }

    @Override
    public int effectiveRpm(AbstractProcessingRecipe recipe) {
        if (recipe == null) {
            return 0;
        }
        return Math.round((float)((double)recipe.getMinRpm() * (1.0 + (double)this.overclock) * (1.0 - this.curFuelEff)));
    }

    @Override
    public int effectiveSu(AbstractProcessingRecipe recipe) {
        if (recipe == null) {
            return 0;
        }
        double factor = Math.pow(Math.max(0.0, 1.0 + (double)this.overclock), this.definition.power().suExponent());
        return (int)Math.round((double)recipe.getSuCost() * factor);
    }

    @Override
    public boolean isOverclocked() {
        return super.isOverclocked() || this.overclock > 0.0f;
    }

    public void bumpOverclock(boolean up, ClickType click) {
        float delta = OverclockMenu.step(click);
        this.overclock += up ? delta : -delta;
        this.overclock = (float)DataMachineBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        this.setChanged();
    }

    @Override
    public PolyContext buildEvalContext() {
        try {
            float f;
            int n = 0;
            Level level = this.getNMSLevel();
            BlockPos pos = this.getMachinePos();
            if (level == null || pos == null) {
                return null;
            }
            LinkedHashMap<String, double[]> fluidTankData = new LinkedHashMap<String, double[]>();
            for (MachineDefinition.TankSpec tankSpec : this.definition.fluidTanks()) {
                FluidTank fluidTank = this.fluidTank(tankSpec.name());
                if (fluidTank == null) continue;
                FluidStack stored = fluidTank.getFluid(level, pos);
                fluidTankData.put(tankSpec.name(), new double[]{stored.getAmount(), fluidTank.getCapacity()});
            }
            LinkedHashMap<String, double[]> gasTankData = new LinkedHashMap<String, double[]>();
            for (MachineDefinition.TankSpec tankSpec : this.definition.gasTanks()) {
                GasTank tank = this.gasTank(tankSpec.name());
                if (tank == null) continue;
                GasStack stored = tank.getGas(level, pos);
                gasTankData.put(tankSpec.name(), new double[]{stored.getAmount(), tank.getCapacity()});
            }
            LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<String, Integer>();
            if (!this.upgradeDefs.isEmpty()) {
                for (int upSlot : this.definition.upgrades().slots()) {
                    net.minecraft.world.item.ItemStack nmsItem = this.getItem(upSlot);
                    Key uid = this.upgradeItemId(nmsItem);
                    if (uid == null) continue;
                    linkedHashMap.merge(uid.namespace() + ":" + uid.value(), 1, Integer::sum);
                }
            }
            boolean bl = false;
            try {
                n = level.getBestNeighborSignal(pos);
            }
            catch (Throwable tank) {
                // empty catch block
            }
            MachineRenderContext mrc = new MachineRenderContext(this.inputRpm, this.overclock, this.curFuelEff, this.progress, this.maxProgress, this.curGeneration, this.isProcessing(), this.inputRpm > 0.0f, this.isOverclocked(), this.burnTime > 0, null, linkedHashMap, fluidTankData, gasTankData, n);
            Direction facing = this.getFacing(level);
            if (facing == null) {
                f = 0.0f;
            } else {
                switch (facing) {
                    case SOUTH: {
                        f = 0.0f;
                        break;
                    }
                    case WEST: {
                        f = 90.0f;
                        break;
                    }
                    case NORTH: {
                        f = 180.0f;
                        break;
                    }
                    case EAST: {
                        f = 270.0f;
                        break;
                    }
                    default: {
                        f = 0.0f;
                    }
                }
            }
            float yaw = f;
            String facingName = facing != null ? facing.getName().toLowerCase() : "north";
            return PolyContext.builder().copyFrom(mrc.toPolyContext()).machinePos((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, facingName, yaw, (World)level.getWorld(), this).redstone(n).contraption(level).num("burn_time", this.burnTime).num("max_burn_time", this.maxBurnTime).num("overclock_limit", this.curOverclockLimit).num("generation", this.curGeneration).cls("Inventory", new InventoryClass((Inventory)new CraftInventory((Container)this))).cls("Network", new NetworkClass((ServerLevel)level, pos.getX(), pos.getY(), pos.getZ())).build();
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private void runActionScript(String scriptName) {
        block9: {
            PolyScript script = PolyScriptRegistry.get(scriptName);
            if (script == null) {
                if (SCRIPT_DEBUG) {
                    System.out.println("[CEP script] " + scriptName + " NOT FOUND in registry");
                }
                return;
            }
            try {
                PolyContext ctx = this.buildEvalContext();
                if (ctx == null) {
                    if (SCRIPT_DEBUG) {
                        System.out.println("[CEP script] " + scriptName + " ctx=null");
                    }
                    return;
                }
                if (SCRIPT_DEBUG) {
                    System.out.println("[CEP script] RUN " + scriptName);
                }
                script.evaluate(ctx);
                if (SCRIPT_DEBUG) {
                    System.out.println("[CEP script] OK " + scriptName);
                }
            }
            catch (Throwable t) {
                if (SCRIPT_DEBUG) {
                    System.out.println("[CEP script] " + scriptName + " EXCEPTION: " + t.getMessage());
                }
                if (!SCRIPT_DEBUG) break block9;
                t.printStackTrace();
            }
        }
    }

    public void runInteractScript(String scriptName, ServerPlayer player) {
        PolyScript script = PolyScriptRegistry.get(scriptName);
        if (script == null) {
            return;
        }
        try {
            PolyContext base = this.buildEvalContext();
            if (base == null) {
                return;
            }
            PolyContext ctx = PolyContext.builder().copyFrom(base).player(player).build();
            script.evaluate(ctx);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private MachineLayout buildMainLayout() {
        int infoSlot;
        Object object;
        MachineLayout layout = new MachineLayout(InventoryType.CHEST, this.definition.menuSize(), this.definition.title());
        Component title = GuiTitles.title(this.getMachineId(), "main");
        if (title != null) {
            layout.setTitleComponent(title);
        }
        for (Object slot : this.definition.inputSlots()) {
            layout.addSlot((int)slot, MenuSlotType.INPUT);
        }
        for (Object slot : this.definition.outputSlots()) {
            layout.addSlot((int)slot, MenuSlotType.OUTPUT);
        }
        for (Object slot : this.definition.fuelSlots()) {
            layout.addSlot((int)slot, MenuSlotType.FUEL);
        }
        if (this.definition.upgrades().isInline()) {
            int[] upgradeSlots = this.definition.upgrades().slots();
            for (int i = 0; i < upgradeSlots.length; ++i) {
                layout.addSlot(upgradeSlots[i], MenuSlotType.UPGRADE);
            }
        }
        if (!this.definition.buttons().isEmpty()) {
            for (MachineDefinition.ButtonSpec spec : this.definition.buttons()) {
                this.installButton(layout, DataMachineBlockEntity.toButton(spec));
            }
        } else {
            for (MachineMenuConfig.Button button : this.menuConfig.buttons) {
                this.installButton(layout, button);
            }
        }
        List<MachineBar> effectiveBars = this.bars;
        if (!this.definition.bars().isEmpty()) {
            ArrayList<MachineBar> resolved = new ArrayList<MachineBar>();
            for (MachineDefinition.BarRef ref : this.definition.bars()) {
                BarDefinition def = BarDefinition.REGISTRY.get(ref.bar());
                if (def == null) continue;
                resolved.add(def.toBar(ref.slots()));
            }
            if (!resolved.isEmpty()) {
                effectiveBars = resolved;
            }
        }
        MachineBars.install(layout, effectiveBars);
        int n = infoSlot = this.definition.infoSlot() >= 0 ? this.definition.infoSlot() : this.menuConfig.infoSlot;
        if (infoSlot >= 0) {
            layout.setDynamicProvider(infoSlot, (machine, tick) -> this.infoIcon());
        }
        return layout;
    }

    static MachineMenuConfig.Button toButton(MachineDefinition.ButtonSpec spec) {
        return new MachineMenuConfig.Button(spec.slot(), spec.icon(), MachineMenuConfig.Action.parse(spec.action()), spec.name(), spec.lore(), spec.lockedIcon(), MachineMenuConfig.LockedWhen.parse(spec.lockedWhen()));
    }

    private void installButton(MachineLayout layout, MachineMenuConfig.Button button) {
        layout.addButton(button.slot, (machine, tick) -> {
            boolean locked = DataMachineBlockEntity.isLocked(machine, button.lockedWhen);
            Key icon = locked && button.lockedIcon != null ? DataMachineBlockEntity.parseKey(button.lockedIcon) : DataMachineBlockEntity.parseKey(button.icon);
            return MenuText.iconItem(icon, Material.PAPER, (Component)Component.text((String)(button.name == null ? "" : button.name)), new Component[0]);
        }, (machine, player) -> {
            if (DataMachineBlockEntity.isLocked(machine, button.lockedWhen)) {
                return;
            }
            switch (button.action.kind) {
                case DEPLETE_FLUID: {
                    for (FluidTank t : machine.fluidTanks) {
                        if (!button.action.targets(t.getName())) continue;
                        t.extract(machine.getNMSLevel(), machine.getMachinePos(), t.getCapacity(), null);
                    }
                    break;
                }
                case DEPLETE_GAS: {
                    for (GasTank t : machine.gasTanks) {
                        if (!button.action.targets(t.getName())) continue;
                        t.extract(machine.getNMSLevel(), machine.getMachinePos(), t.getCapacity(), null);
                    }
                    break;
                }
                case OPEN_PAGE: {
                    if (!(machine instanceof DataMachineBlockEntity)) break;
                    DataMachineBlockEntity self = (DataMachineBlockEntity)machine;
                    self.openPage((org.bukkit.entity.Player)player, button.action.page);
                    break;
                }
                case SCRIPT: {
                    PolyContext ctx;
                    PolyScript script = PolyScriptRegistry.get(button.action.target);
                    if (script == null || (ctx = machine.buildEvalContext()) == null) break;
                    script.evaluate(ctx);
                    break;
                }
            }
        });
    }

    private static boolean isLocked(AbstractMachineBlockEntity machine, MachineMenuConfig.LockedWhen lw) {
        PolyContext polyContext;
        double ocLimit;
        if (lw == null) {
            return false;
        }
        if (machine instanceof DataMachineBlockEntity) {
            DataMachineBlockEntity dm = (DataMachineBlockEntity)machine;
            ocLimit = dm.curOverclockLimit;
        } else {
            ocLimit = 0.0;
        }
        if (machine instanceof DataMachineBlockEntity) {
            DataMachineBlockEntity dm2 = (DataMachineBlockEntity)machine;
            polyContext = dm2.buildEvalContext();
        } else {
            polyContext = null;
        }
        PolyContext ctx = polyContext;
        return lw.isLocked(ctx, ocLimit);
    }

    public static Direction rpmFacesContainDir(String s, Direction facing) {
        if (facing == null) {
            facing = Direction.NORTH;
        }
        return switch (s.toLowerCase()) {
            case "front" -> facing;
            case "back" -> facing.getOpposite();
            case "right" -> facing.getClockWise();
            case "left" -> facing.getCounterClockWise();
            case "up" -> Direction.UP;
            case "down" -> Direction.DOWN;
            default -> Direction.byName((String)s.toLowerCase());
        };
    }

    private static boolean rpmFacesContain(Set<String> raw, Direction d, Direction facing) {
        if (facing == null) {
            facing = Direction.NORTH;
        }
        Iterator<String> iterator = raw.iterator();
        while (iterator.hasNext()) {
            boolean match;
            String s;
            if (!(match = (switch (s = iterator.next()) {
                case "front" -> {
                    if (d == facing) {
                        yield true;
                    }
                    yield false;
                }
                case "back" -> {
                    if (d == facing.getOpposite()) {
                        yield true;
                    }
                    yield false;
                }
                case "right" -> {
                    if (d == facing.getClockWise()) {
                        yield true;
                    }
                    yield false;
                }
                case "left" -> {
                    if (d == facing.getCounterClockWise()) {
                        yield true;
                    }
                    yield false;
                }
                case "up" -> {
                    if (d == Direction.UP) {
                        yield true;
                    }
                    yield false;
                }
                case "down" -> {
                    if (d == Direction.DOWN) {
                        yield true;
                    }
                    yield false;
                }
                case "horizontal" -> {
                    if (d.getAxis() != Direction.Axis.Y) {
                        yield true;
                    }
                    yield false;
                }
                case "vertical" -> {
                    if (d.getAxis() == Direction.Axis.Y) {
                        yield true;
                    }
                    yield false;
                }
                case "axis_perp" -> true;
                case "all" -> true;
                default -> d.getName().equals(s);
            }))) continue;
            return true;
        }
        return false;
    }

    private static Key parseKey(String spec) {
        if (spec == null) {
            return Key.of((String)"cml", (String)"gui_empty");
        }
        int i = spec.indexOf(58);
        return i < 0 ? Key.of((String)"cml", (String)spec) : Key.of((String)spec.substring(0, i), (String)spec.substring(i + 1));
    }
}

