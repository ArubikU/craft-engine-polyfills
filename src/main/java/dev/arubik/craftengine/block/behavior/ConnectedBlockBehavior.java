package dev.arubik.craftengine.block.behavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

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
import net.momirealms.craftengine.bukkit.block.behavior.UnsafeCompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.BlockTags;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateOption;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.EnumProperty;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;

public class ConnectedBlockBehavior extends BukkitBlockBehavior {
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
    public ConnectedBlockBehavior(CustomBlock customBlock,
            List<Object> tags,
            Set<Object> blockStates,
            Set<String> customBlocks,
            boolean includeSelfByDefault) {
        super(customBlock);
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
            if (FastNMS.INSTANCE.method$BlockStateBase$is(state, tag)) {
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
            if (includeSelfByDefault && id.equals(this.customBlock.id().toString()))
                return true;
        }

        return false;
    }

    public boolean isConnectedTo(Direction direction, BlockPos pos, Level level) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState relativeState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (relativeState == null)
            return false; // No es un custom block, no
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
    }

    /**
     * Verifica si el bloque vecino tiene ConnectableBlockBehavior y si permite
     * conectar desde la dirección opuesta.
     */
    public boolean neighborCanConnectBack(Direction direction, BlockState neighborState) {
        Optional<ImmutableBlockState> customOpt = BlockStateUtils.getOptionalCustomBlockState(neighborState);
        if (customOpt.isEmpty())
            return true; // Si no es custom block, asumimos que puede conectar

        if (customOpt.get().behavior() instanceof ConnectableBlockBehavior connectableBehavior) {
            Direction oppositeDirection = direction.opposite();
            return connectableBehavior.canConnectTo(oppositeDirection);
        }
        if (customOpt.get().behavior() instanceof UnsafeCompositeBlockBehavior composite) {
            if (composite.getAs(ConnectableBlockBehavior.class).isPresent()) {
                ConnectableBlockBehavior connectableBehavior = composite.getAs(ConnectableBlockBehavior.class).get();
                Direction oppositeDirection = direction.opposite();
                return connectableBehavior.canConnectTo(oppositeDirection);
            }
        }
        return true;
    }

    public boolean shouldConnect(Direction direction, BlockPos pos, Level level) {
        // get block relative to the direction
        BlockPos relativePos = Utils.getRelativeBlockPos(direction, pos, level);
        BlockState relativeState = level.getBlockState(relativePos);
        if (canConnectToBlock(relativeState)) {
            if (neighborCanConnectBack(direction, relativeState)) {
                return true;
            }
        }
        return false;
    }

    public Object vanillaMakeState(BlockPos pos, Level level) {
        ImmutableBlockState state = (ImmutableBlockState) this.customBlock.defaultState();
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
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).get();
        ImmutableBlockState newState = (ImmutableBlockState) vanillaMakeState(pos, level);
        newState.getNbtToSave();
        customState.getNbtToSave();
        if (customState != null && !newState.equals(customState)) {
            FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, newState.customBlockState().literalObject(),
                    UpdateOption.UPDATE_ALL_IMMEDIATE.flags());
        }
    }

    // BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess,
    // BlockPos pos, Direction direction, BlockPos neighborPos, BlockState
    // neighborState, RandomSource random
    @Override
    public Object updateShape(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (customState == null)
            return state;
        ImmutableBlockState newState = (ImmutableBlockState) vanillaMakeState((BlockPos) args[3], level);
        newState.getNbtToSave();
        customState.getNbtToSave();
        if (!newState.equals(customState)) {
            return newState.customBlockState().literalObject();
        } else {
            return state;
        }
    }

    public static class Factory implements BlockBehaviorFactory {
        public static final Factory FACTORY = new Factory();

        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
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
                        blockStates.addAll(BlockStateUtils.getAllVanillaBlockStates(blockType));
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
