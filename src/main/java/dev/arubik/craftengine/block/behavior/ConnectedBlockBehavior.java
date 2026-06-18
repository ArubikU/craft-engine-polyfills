package dev.arubik.craftengine.block.behavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import dev.arubik.craftengine.property.ConnectedFace;
import dev.arubik.craftengine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.BlockTags;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.property.EnumProperty;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.minecraft.core.Direction;
import net.momirealms.craftengine.core.util.Key;

public class ConnectedBlockBehavior extends ConnectableBlockBehavior {
    public final EnumProperty<ConnectedFace> NORTH;
    public final EnumProperty<ConnectedFace> EAST;
    public final EnumProperty<ConnectedFace> SOUTH;
    public final EnumProperty<ConnectedFace> WEST;
    public final EnumProperty<ConnectedFace> UP;
    public final EnumProperty<ConnectedFace> DOWN;

    private final List<Object> tags;
    private final Set<Object> blockStates;
    private final Set<String> customBlocks;
    private final boolean includeSelfByDefault;

    @SuppressWarnings("unchecked")
    public ConnectedBlockBehavior(BlockDefinition customBlock,
            List<Object> tags,
            Set<Object> blockStates,
            Set<String> customBlocks,
            boolean includeSelfByDefault) {
        super(customBlock, new ArrayList<>(), null, null, new dev.arubik.craftengine.multiblock.IOConfiguration.Open()); // Default
                                                                                                                         // open
                                                                                                                         // IO
                                                                                                                         // for
                                                                                                                         // connected
                                                                                                                         // blocks
                                                                                                                         // unless
                                                                                                                         // specified
                                                                                                                         // otherwise
        this.NORTH = (EnumProperty<ConnectedFace>) customBlock.getProperty("north");
        this.EAST = (EnumProperty<ConnectedFace>) customBlock.getProperty("east");
        this.SOUTH = (EnumProperty<ConnectedFace>) customBlock.getProperty("south");
        this.WEST = (EnumProperty<ConnectedFace>) customBlock.getProperty("west");
        this.UP = (EnumProperty<ConnectedFace>) customBlock.getProperty("up");
        this.DOWN = (EnumProperty<ConnectedFace>) customBlock.getProperty("down");

        this.tags = tags;
        this.blockStates = blockStates;
        this.customBlocks = customBlocks;
        this.includeSelfByDefault = includeSelfByDefault;
    }

    /**
     * Verifica si un estado de bloque vecino es conectable usando tags, keys y
     * custom blocks.
     */
    public boolean canConnectToBlock(Object state) {
        if (state == null)
            return false;

        // 1. Tags
        for (Object tag : tags) {
            if (dev.arubik.craftengine.util.MNms.INSTANCE.method$BlockStateBase$is(state, tag)) {
                return true;
            }
        }

        // 2. Vanilla blockstates
        if (blockStates.contains(state))
            return true;

        // 3. Custom blocks
        Optional<ImmutableBlockState> customOpt = BlockStateUtils.getOptionalCustomBlockState(state);
        if (customOpt.isPresent()) {
            String id = customOpt.get().owner().value().id().toString();
            if (customBlocks.contains(id))
                return true;
            if (includeSelfByDefault && id.equals(this.block().id().toString()))
                return true;
        }

        return false;
    }

    /** True if {@code beh} (possibly a Composite/Dual wrapper) exposes capability {@code cc}. */
    @SuppressWarnings("unchecked")
    private static boolean hasCapability(Object beh, Class<?> cc) {
        if (beh == null)
            return false;
        if (cc.isInstance(beh))
            return true;
        // CE BlockBehavior exposes getFirst(Class) and it resolves interfaces (see FluidTransferHelper).
        if (beh instanceof net.momirealms.craftengine.core.block.behavior.BlockBehavior bb) {
            try {
                return bb.getFirst((Class<Object>) cc) != null;
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    /**
     * The carrier capability this connected block links to (e.g. {@code FluidCarrier.class} for a
     * fluid pipe). Returns {@code null} to disable capability-based connection. Override in pipes.
     */
    protected Class<?> carrierClass() {
        return null;
    }

    /** The IO type this carrier transfers (FLUID / GAS), used to filter connection per-face. */
    protected dev.arubik.craftengine.multiblock.IOConfiguration.IOType carrierIOType() {
        return null;
    }

    /**
     * Capability- AND face-aware connection: link to a neighbour that exposes this pipe's carrier
     * AND whose IO config accepts/provides this pipe's type on the touched LOCAL face — so a fluid
     * pipe only connects to fluid faces, not item faces of the same multiblock.
     */
    protected boolean carrierConnectsHere(Direction direction, BlockState neighborState, Level level,
            BlockPos neighborPos) {
        Class<?> cc = carrierClass();
        dev.arubik.craftengine.multiblock.IOConfiguration.IOType type = carrierIOType();
        if (cc == null || type == null)
            return false;
        Optional<ImmutableBlockState> opt = BlockStateUtils.getOptionalCustomBlockState(neighborState);
        if (opt.isEmpty())
            return false;
        Object beh = opt.get().behavior();
        boolean cap = hasCapability(beh, cc);
        if (dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity.DEBUG_IO)
            System.out.println("[PipeConnect] dir=" + direction + " neigh=" + neighborPos.toShortString()
                    + " beh=" + (beh == null ? "null" : beh.getClass().getSimpleName()) + " cap=" + cap);
        if (!cap)
            return false;
        ConnectableBlockBehavior conn = beh instanceof ConnectableBlockBehavior c ? c
                : (beh instanceof net.momirealms.craftengine.core.block.behavior.BlockBehavior bb
                        ? bb.getFirst(ConnectableBlockBehavior.class)
                        : null);
        if (conn == null)
            return false;
        Direction opp = Utils.oppositeDirection(direction);
        dev.arubik.craftengine.multiblock.IOConfiguration cfg = conn.getIOConfiguration(level, neighborPos);
        // A carrier without a directional IO config (e.g. the creative gas tank) just connects.
        if (cfg == null)
            return true;
        Direction local = conn.toLocalDirection(opp, neighborState);
        boolean ok = cfg.acceptsInput(type, local) || cfg.providesOutput(type, local);
        if (dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity.DEBUG_IO)
            System.out.println("[PipeConnect] dir=" + direction + " neigh=" + neighborPos.toShortString()
                    + " type=" + type + " local=" + local + " -> " + ok);
        return ok;
    }

    /**
     * Same face-connection check as {@link #isConnectedTo} but against an ALREADY-resolved custom
     * state — no world read. Lets a tick loop resolve the block's own state once and test all 6 faces
     * cheaply instead of re-reading getBlockState per face.
     */
    public boolean isFaceConnected(ImmutableBlockState relativeState, Direction direction) {
        if (relativeState == null)
            return false;
        try {
            switch (direction) {
                case NORTH:
                    return relativeState.get(NORTH) == ConnectedFace.CONNECTED;
                case EAST:
                    return relativeState.get(EAST) == ConnectedFace.CONNECTED;
                case SOUTH:
                    return relativeState.get(SOUTH) == ConnectedFace.CONNECTED;
                case WEST:
                    return relativeState.get(WEST) == ConnectedFace.CONNECTED;
                case UP:
                    return relativeState.get(UP) == ConnectedFace.CONNECTED;
                case DOWN:
                    return relativeState.get(DOWN) == ConnectedFace.CONNECTED;
                default:
                    return false;
            }
        } catch (IllegalArgumentException stale) {
            return false;
        }
    }

    public boolean isConnectedTo(Direction direction, BlockPos pos, Level level) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState relativeState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (relativeState == null)
            return false; // No es un custom block, no
        // Guard: a stale block whose saved real-state doesn't match the current config can lack the
        // connected_face property — treat as not connected instead of throwing every tick.
        try {
            switch (direction) {
                case NORTH:
                    return relativeState.get(NORTH) == ConnectedFace.CONNECTED;
                case EAST:
                    return relativeState.get(EAST) == ConnectedFace.CONNECTED;
                case SOUTH:
                    return relativeState.get(SOUTH) == ConnectedFace.CONNECTED;
                case WEST:
                    return relativeState.get(WEST) == ConnectedFace.CONNECTED;
                case UP:
                    return relativeState.get(UP) == ConnectedFace.CONNECTED;
                case DOWN:
                    return relativeState.get(DOWN) == ConnectedFace.CONNECTED;
                default:
                    return false;
            }
        } catch (IllegalArgumentException stale) {
            return false;
        }
    }

    /**
     * Verifica si el bloque vecino tiene ConnectableBlockBehavior y si permite
     * conectar desde la dirección opuesta.
     */
    public boolean neighborCanConnectBack(Direction direction, BlockState neighborState, Level level,
            BlockPos neighborPos) {
        Optional<ImmutableBlockState> customOpt = BlockStateUtils.getOptionalCustomBlockState(neighborState);
        if (customOpt.isEmpty())
            return true; // Si no es custom block, asumimos que puede conectar

        if (customOpt.get().behavior() instanceof ConnectableBlockBehavior connectableBehavior) {
            Direction oppositeDirection = Utils.oppositeDirection(direction);
            // Respetar la orientación del bloque vecino al decidir si puede conectar de
            // vuelta
            return connectableBehavior.canConnectTo(level, neighborPos, oppositeDirection);
        }
        if (customOpt.get().behavior() instanceof CompositeBlockBehavior composite) {
            ConnectableBlockBehavior connectableBehavior = composite.getFirst(ConnectableBlockBehavior.class);
            if (connectableBehavior != null) {
                Direction oppositeDirection = Utils.oppositeDirection(direction);
                return connectableBehavior.canConnectTo(level, neighborPos, oppositeDirection);
            }
        }
        return true;
    }

    public boolean shouldConnect(Direction direction, BlockPos pos, Level level) {
        // get block relative to the direction
        BlockPos relativePos = Utils.getRelativeBlockPos(direction, pos, level);
        BlockState relativeState = level.getBlockState(relativePos);
        if (canConnectToBlock(relativeState)) {
            if (neighborCanConnectBack(direction, relativeState, level, relativePos)) {
                return true;
            }
        }
        // Capability + type + face: connect to carriers (tanks, machines, multiblocks) only on a
        // face that actually handles this pipe's resource type.
        if (carrierConnectsHere(direction, relativeState, level, relativePos)) {
            return true;
        }
        return false;
    }

    public Object vanillaMakeState(BlockPos pos, Level level) {
        ImmutableBlockState state = this.block().defaultState();
        if (this.NORTH != null && state.get(this.NORTH) != ConnectedFace.CONNECTED) {
            state = state.with(this.NORTH,
                    shouldConnect(Direction.NORTH, pos, level) ? ConnectedFace.CONNECTED : ConnectedFace.NONE);

        }
        if (this.EAST != null && state.get(this.EAST) != ConnectedFace.CONNECTED) {
            state = state.with(this.EAST,
                    shouldConnect(Direction.EAST, pos, level) ? ConnectedFace.CONNECTED : ConnectedFace.NONE);

        }
        if (this.SOUTH != null && state.get(this.SOUTH) != ConnectedFace.CONNECTED) {
            state = state.with(this.SOUTH,
                    shouldConnect(Direction.SOUTH, pos, level) ? ConnectedFace.CONNECTED : ConnectedFace.NONE);

        }
        if (this.WEST != null && state.get(this.WEST) != ConnectedFace.CONNECTED) {
            state = state.with(this.WEST,
                    shouldConnect(Direction.WEST, pos, level) ? ConnectedFace.CONNECTED : ConnectedFace.NONE);

        }
        if (this.UP != null && state.get(this.UP) != ConnectedFace.CONNECTED) {
            state = state.with(this.UP,
                    shouldConnect(Direction.UP, pos, level) ? ConnectedFace.CONNECTED : ConnectedFace.NONE);

        }
        if (this.DOWN != null && state.get(this.DOWN) != ConnectedFace.CONNECTED) {
            state = state.with(this.DOWN,
                    shouldConnect(Direction.DOWN, pos, level) ? ConnectedFace.CONNECTED : ConnectedFace.NONE);

        }
        return state;
    }

    @Override
    public void onPlace(Object thisBlock, Object[] args) {
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).get();
        ImmutableBlockState newState = (ImmutableBlockState) vanillaMakeState(pos, level);
        newState.getNbtToSave();
        customState.getNbtToSave();
        if (customState != null && !newState.equals(customState)) {
            dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$setBlock(level, pos, newState.customBlockState().minecraftState(),
                    UpdateFlags.UPDATE_ALL_IMMEDIATE);
        }
    }

    // BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess,
    // BlockPos pos, Direction direction, BlockPos neighborPos, BlockState
    // neighborState, RandomSource random
    @Override
    public Object updateShape(Object thisBlock, Object[] args) {
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[BukkitBlockBehavior.updateShape$level];
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (customState == null)
            return state;
        ImmutableBlockState newState = (ImmutableBlockState) vanillaMakeState(
                (BlockPos) args[BukkitBlockBehavior.updateShape$blockPos], level);
        newState.getNbtToSave();
        customState.getNbtToSave();
        if (!newState.equals(customState)) {
            return newState.customBlockState().minecraftState();
        } else {
            return state;
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        public static final Factory FACTORY = new Factory();

        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            List<Object> tags = new ArrayList<>();
            Set<Object> blockStates = new HashSet<>();
            Set<String> customBlocks = new HashSet<>();

            // Procesar configuración de conexiones (similar a SpreadingBlockBehavior)
            Object raw = arguments.getOrDefault("connects", List.of());
            List<String> entries = new ArrayList<>();
            if (raw instanceof List<?> list) {
                for (Object o : list)
                    entries.add(o.toString());
            } else if (raw instanceof String s) {
                // Permitir coma separada
                for (String part : s.split(","))
                    entries.add(part.trim());
            }

            for (String key : entries) {
                if (key.isEmpty())
                    continue;
                if (key.startsWith("#")) {
                    tags.add(BlockTags.getOrCreate(Key.of(key)));
                    continue;
                }
                int idx = key.indexOf('[');
                Key blockType = idx != -1 ? Key.from(key.substring(0, idx)) : Key.from(key);
                Material material = Registry.MATERIAL.get(new NamespacedKey(blockType.namespace(), blockType.value()));
                if (material != null) {
                    if (idx == -1) {
                        // Todas las variantes del bloque vanilla
                        blockStates.addAll(BlockStateUtils.getPossibleBlockStates(blockType));
                    } else {
                        // Estado específico
                        blockStates.add(BlockStateUtils.blockDataToBlockState(Bukkit.createBlockData(key)));
                    }
                } else {
                    // Custom block id
                    customBlocks.add(key);
                }
            }

            // Si no se especifica nada, incluir self por defecto.
            boolean includeSelf = entries.isEmpty() || customBlocks.contains(block.id().toString());

            return new ConnectedBlockBehavior(block, tags, blockStates, customBlocks, includeSelf);
        }
    }
}
