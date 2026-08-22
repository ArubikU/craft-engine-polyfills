package dev.arubik.craftengine.machine.block.behavior;

import java.util.List;

import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.multiblock.IOConfigurationProvider;
import dev.arubik.craftengine.multiblock.MultiCellGeometry;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * {@link DataMachineBehavior}, plus one capability: placing this block auto-places a whole
 * auto-placing multi-cell structure (a 4-tall energy windmill, a 2-wide press, etc.) around it.
 *
 * <p>Kept as its OWN class rather than folded into {@link DataMachineBehavior} — every one of the
 * hundred-plus ordinary single-block machines in this codebase goes through {@code
 * DataMachineBehavior}, so bolting multi-cell placement/breakage/forwarding onto that shared class
 * would put every existing machine one bug away from a regression it has nothing to do with. This
 * subclass only exists at all for a block whose config declares {@code cells:}; every override here
 * calls {@code super} first and only adds behaviour on top.
 *
 * <p>The cell SHAPE lives on THIS block's own YAML config (see {@link MultiCellGeometry#parseCells}),
 * never on the {@link MachineDefinition} JSON — that is what lets a hypothetical non-machine block
 * behaviour opt into the exact same auto-placement independently, unrelated to whether it happens
 * to also be a machine.
 *
 * <pre>{@code
 * behavior:
 *   type: polyfills:celled_data_machine
 *   machine: polyfills:energy_windmill
 *   cells:
 *     - { right: 0, up: 1, forward: 0 }
 *     - { right: 0, up: 2, forward: 0 }
 *     - { right: 0, up: 3, forward: 0 }
 * }</pre>
 *
 * <h2>Role</h2>
 * The player-placed block is always the CORE (hosts the real {@link DataMachineBlockEntity} — menu,
 * recipes, energy buffer, everything); every auto-placed cell is a PART, whose controller is a plain
 * {@link dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity} that redirects container/IO
 * access and right-clicks back to the core — the SAME forwarding mechanism the existing
 * player-assembled multiblock system already uses, reused here rather than reinvented. Role is a
 * block-state property (default name {@code role}, overridable via {@code role_property}, values
 * {@code core}/{@code part} per {@link dev.arubik.craftengine.multiblock.MultiBlockRole}); a part's
 * offset BACK to its core is not derived from the property (an arbitrary cell count does not fit a
 * tiny enum) — it is plain NBT set at placement time via {@code MultiBlockPartBlockEntity#setCorePos}.
 *
 * <h2>Per-cell I/O</h2>
 * If the resolved {@code machine:} id also has a {@code cell_io} block in its JSON (see {@code
 * MachineDefinitionLoader}), a matching {@link dev.arubik.craftengine.machine.CelledMachineDefinition}
 * is registered under the same id and its {@link IOConfigurationProvider} grants each cell its own
 * per-face rule instead of a blanket redirect-everything-to-core. Absent {@code cell_io} — the
 * default — every cell just redirects, unchanged from a plain assembled-multiblock part.
 */
public class CelledDataMachineBehavior extends DataMachineBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:celled_data_machine");
    public static final Factory FACTORY = new Factory();

    // Gated entirely on "cells" being non-empty — a block config with no "cells" arg behaves
    // exactly like plain DataMachineBehavior (every override below opens with hasStructure()).
    private final Property<?> roleProperty;
    private final List<MultiCellGeometry.Offset> cells;
    /** Per-cell I/O rules from a {@code CelledMachineDefinition}, or null (plain core redirect). */
    private final IOConfigurationProvider cellIo;

    public CelledDataMachineBehavior(BlockDefinition block, MachineDefinition definition,
            MachineMenuConfig menuConfig, List<MachineBar> bars,
            List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontal,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> vertical,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig,
            java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs,
            String roleProperty, List<MultiCellGeometry.Offset> cells, IOConfigurationProvider cellIo) {
        super(block, definition, menuConfig, bars, connectableFaces, horizontal, vertical, ioConfig, upgradeDefs);
        this.cells = cells != null ? List.copyOf(cells) : List.of();
        Property<?> rp = null;
        if (!this.cells.isEmpty()) {
            try {
                rp = block.getProperty(roleProperty);
            } catch (Throwable ignored) {
            }
        }
        this.roleProperty = rp;
        this.cellIo = cellIo;
    }

    private boolean hasStructure() {
        return !cells.isEmpty() && roleProperty != null;
    }

    /** Secondary-cell count (0 for an ordinary single block) — exposed to scripts via
     * {@code Machine.is_multi_cell}/{@code Machine.cell_count}. */
    public int cellCount() {
        return cells.size();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String roleOf(ImmutableBlockState state) {
        if (roleProperty == null || state == null) return "core";
        Object v = state.get((Property) roleProperty);
        if (v == null) return "core";
        try {
            return Property.formatValue((Property) roleProperty, (Comparable<?>) v).toLowerCase();
        } catch (Throwable t) {
            return String.valueOf(v).toLowerCase();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ImmutableBlockState withRole(ImmutableBlockState state, String roleName) {
        if (roleProperty == null || state == null) return state;
        try {
            Object value = ((Property) roleProperty).valueByName(roleName);
            if (value == null) return state;
            return ImmutableBlockState.with(state, (Property) roleProperty, value);
        } catch (Throwable t) {
            return state;
        }
    }

    private net.momirealms.craftengine.core.util.Direction facingOf(ImmutableBlockState state) {
        if (horizontalDirectionProperty == null || state == null)
            return net.momirealms.craftengine.core.util.Direction.NORTH;
        net.momirealms.craftengine.core.util.Direction d = state.getNullable(horizontalDirectionProperty);
        return d != null ? d : net.momirealms.craftengine.core.util.Direction.NORTH;
    }

    @Override
    public ImmutableBlockState updateStateForPlacement(
            net.momirealms.craftengine.core.world.context.BlockPlaceContext context, ImmutableBlockState state) {
        ImmutableBlockState result = super.updateStateForPlacement(context, state);
        if (!hasStructure()) return result;
        // The player-placed block is always the structure's CORE; onPlace below places every PART.
        return withRole(result, "core");
    }

    @Override
    public void onPlace(Object thisBlock, Object[] args) {
        super.onPlace(thisBlock, args);
        if (!hasStructure()) return;
        try {
            Level level = (Level) args[1];
            net.minecraft.core.BlockPos nmsPos = (net.minecraft.core.BlockPos) args[2];
            org.bukkit.World world = level.getWorld();
            if (world == null) return;
            org.bukkit.block.Block placed = world.getBlockAt(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());
            ImmutableBlockState state = net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.getCustomBlockState(placed);
            if (state == null || !"core".equals(roleOf(state))) return; // PARTs never recurse-place

            net.momirealms.craftengine.core.util.Direction facing = facingOf(state);
            net.momirealms.craftengine.core.world.BlockPos masterPos =
                    new net.momirealms.craftengine.core.world.BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());

            // Shared place/idempotency/rollback ALGORITHM with MultiCellBlockBehavior (the workbench's
            // standalone flavor) — see dev.arubik.craftengine.multiblock.MultiCellPlacement's class
            // javadoc for why the role STORAGE itself (core/part here vs. a numeric cell_index there)
            // stays separate rather than being forced to match.
            dev.arubik.craftengine.multiblock.MultiCellPlacement.autoPlaceParts(world, placed, masterPos, facing,
                    cells, state,
                    (s, index) -> "part".equals(roleOf(s)),
                    (coreState, index) -> withRole(coreState, "part"),
                    (block, index) -> {
                        // Stamp this part's controller with its core position + per-cell IO now that
                        // placement fired createBlockEntityController for it.
                        BlockEntity be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level,
                                new net.minecraft.core.BlockPos(block.getX(), block.getY(), block.getZ()));
                        if (be == null || !(be.controller instanceof dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity part))
                            return;
                        part.setRole(dev.arubik.craftengine.multiblock.MultiBlockRole.PART);
                        part.setCorePos(new net.minecraft.core.BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ()));
                        if (cellIo != null) {
                            // Schema-relative (unrotated) offset — configurePartIO's own convention,
                            // the same "schema space" the assembled multiblock system already passes it.
                            MultiCellGeometry.Offset offset = cells.get(index - 1);
                            net.minecraft.core.BlockPos relative =
                                    new net.minecraft.core.BlockPos(offset.x(), offset.y(), offset.z());
                            dev.arubik.craftengine.multiblock.IOConfiguration partIo = cellIo.configurePartIO(relative);
                            if (partIo != null)
                                part.setIOConfiguration(partIo);
                        }
                    });
        } catch (Throwable t) {
            net.momirealms.craftengine.core.plugin.CraftEngine.instance().logger()
                    .warn("CelledDataMachineBehavior onPlace failed", t);
        }
    }

    private boolean removingStructure = false;

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args) {
        super.affectNeighborsAfterRemoval(thisBlock, args);
        if (!hasStructure() || removingStructure) return;
        try {
            net.minecraft.world.level.block.state.BlockState nmsOldState =
                    (net.minecraft.world.level.block.state.BlockState) args[0];
            Level level = (Level) args[1];
            net.minecraft.core.BlockPos nmsPos = (net.minecraft.core.BlockPos) args[2];
            org.bukkit.World world = level.getWorld();
            if (world == null) return;

            org.bukkit.block.data.BlockData data =
                    net.momirealms.craftengine.bukkit.util.BlockStateUtils.fromBlockData(nmsOldState);
            ImmutableBlockState state = data != null
                    ? net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.getCustomBlockState(data) : null;
            if (state == null) return;

            net.momirealms.craftengine.core.world.BlockPos thisPos =
                    new net.momirealms.craftengine.core.world.BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());
            net.momirealms.craftengine.core.world.BlockPos corePos;
            if ("core".equals(roleOf(state))) {
                corePos = thisPos;
            } else {
                // A PART's own facing property may already be gone from the reconstructed old
                // state; the world-level part entity's stored core position (NBT, set at
                // placement) is the reliable source instead.
                BlockEntity be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, nmsPos);
                net.minecraft.core.BlockPos stored = be != null
                        && be.controller instanceof dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity part
                                ? part.getCorePos() : null;
                if (stored == null) return; // never linked — nothing to clean up
                corePos = new net.momirealms.craftengine.core.world.BlockPos(stored.getX(), stored.getY(), stored.getZ());
            }

            net.momirealms.craftengine.core.util.Direction facing = facingOf(state);
            removingStructure = true;
            try {
                dev.arubik.craftengine.multiblock.MultiCellPlacement.breakStructure(world, corePos, facing, cells,
                        thisPos);
            } finally {
                removingStructure = false;
            }
        } catch (Throwable t) {
            net.momirealms.craftengine.core.plugin.CraftEngine.instance().logger()
                    .warn("CelledDataMachineBehavior break failed", t);
        }
    }

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        if (!hasStructure() || "core".equals(roleOf(state)))
            return super.useWithoutItem(context, state);
        try {
            Level level = (Level) context.getLevel().minecraftWorld();
            net.minecraft.core.BlockPos pos = (net.minecraft.core.BlockPos)
                    net.momirealms.craftengine.bukkit.util.LocationUtils.toBlockPos(context.getClickedPos());
            BlockEntity be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be == null || !(be.controller instanceof dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity part))
                return InteractionResult.PASS;
            net.minecraft.core.BlockPos corePos = part.getCorePos();
            if (corePos == null) return InteractionResult.PASS;
            BlockEntity coreBe = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, corePos);
            if (coreBe == null || !(coreBe.controller instanceof DataMachineBlockEntity dm))
                return InteractionResult.PASS;
            net.momirealms.craftengine.core.entity.player.Player cePlayerRaw = context.getPlayer();
            if (!(cePlayerRaw instanceof net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer cePlayer))
                return InteractionResult.PASS;
            if (!(cePlayer.platformPlayer() instanceof org.bukkit.entity.Player bukkit))
                return InteractionResult.PASS;
            if (dm.definition() != null && dm.definition().interactScript() != null) {
                net.minecraft.server.level.ServerPlayer nmsPlayer =
                        ((org.bukkit.craftbukkit.entity.CraftPlayer) bukkit).getHandle();
                dm.runInteractScript(dm.definition().interactScript(), nmsPlayer);
                return InteractionResult.SUCCESS_AND_CANCEL;
            }
            if (dm.definition() == null || dm.definition().openUi()) {
                dm.getMenu().open(bukkit);
                return InteractionResult.SUCCESS_AND_CANCEL;
            }
        } catch (Throwable ignored) {
        }
        return InteractionResult.PASS;
    }

    private static boolean isCellReplaceable(org.bukkit.block.Block block) {
        if (block == null) return false;
        org.bukkit.Material m = block.getType();
        return m.isAir() || m == org.bukkit.Material.WATER || m == org.bukkit.Material.LAVA
                || m == org.bukkit.Material.SHORT_GRASS || m == org.bukkit.Material.TALL_GRASS
                || m == org.bukkit.Material.SNOW;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        if (hasStructure() && "part".equals(roleOf(blockEntity.blockState()))) {
            dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity part =
                    new dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity(blockEntity);
            // By the time affectNeighborsAfterRemoval fires below, this controller (and the NBT
            // corePos it carries) is already torn down — that's why breaking any PART used to
            // silently no-op while breaking the CORE still worked (the core needs no lookup, its
            // own position IS the core position). setPreCleanup fires while the controller is
            // still alive, exactly like MultiBlockBehavior#registerDisassemblyHook uses it for the
            // assembled-multiblock system's own disassembly.
            part.setPreCleanup(p -> {
                handlePartPreCleanup(part);
                return null;
            });
            return part;
        }
        BlockEntityController base = super.createBlockEntityController(blockEntity);
        if (base instanceof DataMachineBlockEntity dm) {
            dm.setCellCount(cells.size());
            // The CORE is relative offset (0,0,0) in cell_io's own schema-space convention (the
            // same space PART offsets — cells.get(index-1) — are already resolved in, see the
            // onPlace lambda above). Without this, cell_io only ever configured the 3 PART cells
            // and the core fell back to whatever machines/*.json's own "io" block granted (or
            // nothing at all) — meaning a rule like `{"y": 0, ...}` could never fire, since every
            // PART here sits at y>=1. Applying cellIo here makes cell_io the single source of
            // truth for the whole structure's I/O, core included.
            if (cellIo != null) {
                dev.arubik.craftengine.multiblock.IOConfiguration coreIo =
                        cellIo.configurePartIO(new net.minecraft.core.BlockPos(0, 0, 0));
                if (coreIo != null) {
                    dm.setIOConfiguration(coreIo);
                }
            }
        }
        return base;
    }

    private void handlePartPreCleanup(dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity part) {
        if (!hasStructure() || removingStructure) return;
        try {
            net.minecraft.core.BlockPos storedCore = part.getCorePos();
            if (storedCore == null) return; // never linked — nothing to clean up
            org.bukkit.World world = (org.bukkit.World) part.world().world.platformWorld();
            if (world == null) return;
            net.momirealms.craftengine.core.world.BlockPos corePos = new net.momirealms.craftengine.core.world.BlockPos(
                    storedCore.getX(), storedCore.getY(), storedCore.getZ());
            net.momirealms.craftengine.core.util.Direction facing = facingOf(part.blockState());
            removingStructure = true;
            try {
                dev.arubik.craftengine.multiblock.MultiCellPlacement.breakStructure(world, corePos, facing, cells,
                        part.pos());
            } finally {
                removingStructure = false;
            }
        } catch (Throwable t) {
            net.momirealms.craftengine.core.plugin.CraftEngine.instance().logger()
                    .warn("CelledDataMachineBehavior part pre-cleanup failed", t);
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior) MachineBlockBehavior.FACTORY.create(block, arguments);

            Object configured = arguments.get("machine");
            MachineDefinition definition = MachineDefinition.byName(String.valueOf(configured));
            if (definition == null)
                throw new IllegalArgumentException("Block " + block.id()
                        + " uses polyfills:celled_data_machine but 'machine: " + configured
                        + "' matches no entry in machines/*.json. Known: "
                        + MachineDefinition.REGISTRY.keys());

            var upgradeDefs = DataMachineBehavior.parseUpgrades(arguments.get("upgrades"));

            var cells = MultiCellGeometry.parseCells(arguments.get("cells"));
            if (cells.isEmpty())
                throw new IllegalArgumentException("Block " + block.id()
                        + " uses polyfills:celled_data_machine but declares no 'cells' — use "
                        + "polyfills:data_machine for an ordinary single-block machine instead.");
            String roleProperty = arguments.getOrDefault("role_property", "role").toString();

            // Optional: if "machine:" also resolves to a CelledMachineDefinition (its JSON declared
            // a "cell_io" block — see MachineDefinitionLoader#apply), pick up its per-cell I/O rules.
            // Absent that, every part just redirects every face to the core.
            var celled = dev.arubik.craftengine.machine.CelledMachineDefinition.byName(String.valueOf(configured));
            IOConfigurationProvider cellIo = celled != null ? celled.io() : null;

            return new CelledDataMachineBehavior(block, definition,
                    MachineMenuConfig.parse(arguments::get),
                    MachineBars.parse(arguments.get("bars")),
                    base.getConnectableFaces(), base.horizontalDirectionProperty,
                    base.verticalDirectionProperty, base.defaultIOConfig, upgradeDefs,
                    roleProperty, cells, cellIo);
        }
    }
}
