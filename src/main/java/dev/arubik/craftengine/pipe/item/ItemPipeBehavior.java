package dev.arubik.craftengine.pipe.item;

import dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Item pipe segment. Physical connectivity/mask stays a plain {@link ConnectedBlockBehavior}
 * concern (same as the fluid/gas pipes) — what makes an item pipe different is that its block
 * entity controller is a full {@link DataMachineBlockEntity}, built from a normal
 * {@code machines/*.json} definition (this pipe type's {@link PipeType#panel()}, e.g.
 * {@code polyfills:item_pipe_panel}), exactly like {@code machines/item_filter.json} is a
 * non-recipe, UI-only "machine". That reuses the ENTIRE existing pages/buttons/scripts/menu
 * engine unchanged: the panel's buttons read/write this segment's own live
 * {@code IOConfiguration} via the generic {@code Machine.io_get}/{@code io_set} script primitives
 * (see {@code MachineType}), which is the SAME object {@link ItemEngine} consults for that face
 * and (via {@code ConnectedBlockBehavior#shouldConnect}) the SAME object driving the visual mask.
 *
 * <p>A plain fluid/gas pipe (whose controller is a bare {@code PersistentBlockEntity}, no
 * {@code AbstractMachineBlockEntity}) is completely unaffected by any of this.
 */
public class ItemPipeBehavior extends ConnectedBlockBehavior implements EntityBlock {

    public static final Factory FACTORY = new Factory();

    /** Used when a {@code pipe_types/*.json} entry for {@code resource: item} declares no
     * explicit {@code "panel"} — a generic 6-face I/O + filter control panel. */
    public static final Key DEFAULT_PANEL = Key.of("polyfills", "item_pipe_panel");

    protected final BlockDefinition block;
    protected final PipeType pipeType;

    public ItemPipeBehavior(BlockDefinition block, PipeType pipeType) {
        super(block, new java.util.ArrayList<>(), new java.util.HashSet<>(),
                new java.util.HashSet<>(pipeType.connectsTo()), true);
        this.block = block;
        this.pipeType = pipeType;
        this.connectableFaces = java.util.Arrays.asList(Direction.values());
    }

    /** Stacks moved per tick through this segment (a tier knob, like {@code transfer_per_tick} for
     * fluid/gas pipes — there it's mB, here it's item count). */
    public int transferPerTick() {
        return pipeType.transferPerTick();
    }

    @Override
    public boolean shouldConnect(Direction direction, BlockPos pos, Level level) {
        if (super.shouldConnect(direction, pos, level))
            return true;
        BlockPos neighborPos = pos.relative(direction);
        if (!ItemTransferHelper.getContainer(level, neighborPos, pos.immutable(), direction.getOpposite()).isPresent())
            return false;
        // A container neighbour only counts as visually/functionally connected if THIS face's own
        // item-transfer mode actually allows something to cross it — without this, a face the
        // player set to Disabled toward a chest always showed (and behaved) connected anyway,
        // since "there's simply a container over there" ignored the panel's mode entirely.
        dev.arubik.craftengine.multiblock.IOConfiguration cfg = getIOConfiguration(level, pos);
        if (cfg == null)
            return false;
        Direction local = toLocalDirection(direction, level.getBlockState(pos));
        return cfg.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, local)
                || cfg.providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, local);
    }

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        Key panelId = pipeType.panel() != null ? pipeType.panel() : DEFAULT_PANEL;
        MachineDefinition def = MachineDefinition.REGISTRY.get(panelId);
        if (def == null)
            throw new IllegalStateException("item pipe panel machine '" + panelId
                    + "' is not defined (add a machines/*.json for it, or set pipe_types/*.json's \"panel\")");
        return new DataMachineBlockEntity(blockEntity, def);
    }

    /** Right-click, empty hand: open the panel (or run its {@code on_right_click} script, same
     * dispatch rule any other data-driven machine follows) — mirrors
     * {@code MachineBlockBehavior#useWithoutItem}. */
    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            ServerLevel level = (ServerLevel) context.getLevel().minecraftWorld();
            BlockPos pos = (BlockPos) LocationUtils.toBlockPos(context.getClickedPos());
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be != null && be.controller instanceof AbstractMachineBlockEntity machine) {
                net.momirealms.craftengine.core.entity.player.Player cePlayerRaw = context.getPlayer();
                if (cePlayerRaw instanceof BukkitServerPlayer bukkitPlayer
                        && bukkitPlayer.platformPlayer() instanceof org.bukkit.entity.Player bukkit) {
                    if (machine instanceof DataMachineBlockEntity dm && dm.definition() != null) {
                        if (dm.definition().interactScript() != null) {
                            net.minecraft.server.level.ServerPlayer nmsPlayer =
                                    ((org.bukkit.craftbukkit.entity.CraftPlayer) bukkit).getHandle();
                            dm.runInteractScript(dm.definition().interactScript(), nmsPlayer);
                            return InteractionResult.SUCCESS_AND_CANCEL;
                        }
                        if (dm.definition().openUi()) {
                            machine.getMenu().open(bukkit);
                            return InteractionResult.SUCCESS_AND_CANCEL;
                        }
                    } else {
                        machine.getMenu().open(bukkit);
                        return InteractionResult.SUCCESS_AND_CANCEL;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return InteractionResult.PASS;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection args) {
            PipeType type = null;
            Object configured = args == null ? null : args.get("pipe_type");
            if (configured != null)
                type = PipeType.byName(String.valueOf(configured));
            if (type == null)
                type = PipeType.byBlockId(block.id());
            if (type == null)
                throw new IllegalStateException(
                        "item_pipe_block '" + block.id() + "' has no matching pipe_types/*.json entry"
                                + " (set behavior.pipe_type or bind a pipe_types block id to it)");
            return new ItemPipeBehavior(block, type);
        }
    }
}
