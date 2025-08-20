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

import net.momirealms.craftengine.bukkit.block.behavior.AbstractCanSurviveBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.BlockTags;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitBlockInWorld;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.IntegerProperty;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.MiscUtils;
import net.momirealms.craftengine.core.util.ResourceConfigUtils;
import net.momirealms.craftengine.core.util.Tuple;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.World;

public class BushBlockBehavior extends AbstractCanSurviveBlockBehavior {
    public static final Factory FACTORY = new Factory();
    protected final List<Object> tagsCanSurviveOn;
    protected final Set<Object> blockStatesCanSurviveOn;
    protected final Set<String> customBlocksCansSurviveOn;
    protected final boolean blacklistMode;
    protected final boolean stackable;
    protected final boolean ageDirection; // true if the age increases upwards
    protected final IntegerProperty ageProperty;

    public BushBlockBehavior(CustomBlock block, int delay, boolean blacklist, boolean stackable, boolean ageDirection,
            List<Object> tagsCanSurviveOn, Set<Object> blockStatesCanSurviveOn, Set<String> customBlocksCansSurviveOn,
            Property<Integer> ageProperty) {
        super(block, delay);
        this.blacklistMode = blacklist;
        this.stackable = stackable;
        this.ageDirection = ageDirection;
        this.tagsCanSurviveOn = tagsCanSurviveOn;
        this.blockStatesCanSurviveOn = blockStatesCanSurviveOn;
        this.customBlocksCansSurviveOn = customBlocksCansSurviveOn;
        this.ageProperty = (IntegerProperty) ageProperty;
    }

    public static class Factory implements BlockBehaviorFactory {

        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            Tuple<List<Object>, Set<Object>, Set<String>> tuple = readTagsAndState(arguments, false);
            boolean stackable = ResourceConfigUtils.getAsBoolean(arguments.getOrDefault("stackable", false),
                    "stackable");
            int delay = ResourceConfigUtils.getAsInt(arguments.getOrDefault("delay", 0), "delay");
            boolean blacklistMode = ResourceConfigUtils.getAsBoolean(arguments.getOrDefault("blacklist", false),
                    "blacklist");
            boolean ageDirection = ResourceConfigUtils.getAsBoolean(
                    arguments.getOrDefault("age-direction", "up").toString().equals("up"), "age-direction");

            @SuppressWarnings("unchecked")
            Property<Integer> ageProperty = (Property<Integer>) ResourceConfigUtils
                    .requireNonNullOrThrow(block.getProperty("age"), "warning.config.block.behavior.crop.missing_age");

            return new BushBlockBehavior(block, delay, blacklistMode, stackable, ageDirection, tuple.left(),
                    tuple.mid(), tuple.right(), ageProperty);
        }
    }

    public static Tuple<List<Object>, Set<Object>, Set<String>> readTagsAndState(Map<String, Object> arguments,
            boolean aboveOrBelow) {
        List<Object> mcTags = new ArrayList<>();
        for (String tag : MiscUtils.getAsStringList(
                arguments.getOrDefault((aboveOrBelow ? "above" : "bottom") + "-block-tags", List.of()))) {
            mcTags.add(BlockTags.getOrCreate(Key.of(tag)));
        }
        Set<Object> mcBlocks = new HashSet<>();
        Set<String> customBlocks = new HashSet<>();
        for (String blockStateStr : MiscUtils
                .getAsStringList(arguments.getOrDefault((aboveOrBelow ? "above" : "bottom") + "-blocks", List.of()))) {
            int index = blockStateStr.indexOf('[');
            Key blockType = index != -1 ? Key.from(blockStateStr.substring(0, index)) : Key.from(blockStateStr);
            Material material = Registry.MATERIAL.get(new NamespacedKey(blockType.namespace(), blockType.value()));
            if (material != null) {
                if (index == -1) {
                    // vanilla
                    mcBlocks.addAll(BlockStateUtils.getAllVanillaBlockStates(blockType));
                } else {
                    mcBlocks.add(BlockStateUtils.blockDataToBlockState(Bukkit.createBlockData(blockStateStr)));
                }
            } else {
                // custom maybe
                customBlocks.add(blockStateStr);
            }
        }
        return new Tuple<>(mcTags, mcBlocks, customBlocks);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected boolean canSurvive(Object thisBlock, Object state, Object world, Object blockPos) throws Exception {
        int y = FastNMS.INSTANCE.field$Vec3i$y(blockPos);
        int x = FastNMS.INSTANCE.field$Vec3i$x(blockPos);
        int z = FastNMS.INSTANCE.field$Vec3i$z(blockPos);
        Object belowPos = FastNMS.INSTANCE.constructor$BlockPos(x, y - 1, z);
        Object belowState = FastNMS.INSTANCE.method$BlockGetter$getBlockState(world, belowPos);
        return mayPlaceOn(belowState, world, belowPos);
    }

    protected boolean mayPlaceOn(Object belowState, Object world, Object belowPos) {
        for (Object tag : this.tagsCanSurviveOn) {
            if (FastNMS.INSTANCE.method$BlockStateBase$is(belowState, tag)) {
                return !this.blacklistMode;
            }
        }
        Optional<ImmutableBlockState> optionalCustomState = BlockStateUtils.getOptionalCustomBlockState(belowState);
        if (optionalCustomState.isEmpty()) {
            if (!this.blockStatesCanSurviveOn.isEmpty() && this.blockStatesCanSurviveOn.contains(belowState)) {
                return !this.blacklistMode;
            }
        } else {
            ImmutableBlockState belowCustomState = optionalCustomState.get();
            if (belowCustomState.owner().value() == super.customBlock) {
                return this.stackable;
            }
            if (this.customBlocksCansSurviveOn.contains(belowCustomState.owner().value().id().toString())) {
                return !this.blacklistMode;
            }
            if (this.customBlocksCansSurviveOn.contains(belowCustomState.toString())) {
                return !this.blacklistMode;
            }
        }
        return this.blacklistMode;
    }

    @Override
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        super.onPlace(thisBlock, args, superMethod);
        Object world = args[1];
        Object blockPos = args[2];
        if (ageProperty == null)
            return;
        Optional<ImmutableBlockState> optionalState = BlockStateUtils.getOptionalCustomBlockState(args[0]);

        if (optionalState.isEmpty())
            return;

        BlockPos hPos = LocationUtils.fromBlockPos(blockPos);
        hPos = hPos.offset(0, 1, 0);
        World worldObj = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(world));
        // check if the block up is the same type as the current block
        ImmutableBlockState aboveState = worldObj.getBlockAt(hPos).customBlockState();
        if (aboveState != null && aboveState.owner().value() == super.customBlock) {
            return;
        }

        ImmutableBlockState currentState = optionalState.get();
        BlockPos pos = LocationUtils.fromBlockPos(blockPos);
        // Set the tip block to max or min age depending on direction
        int tipAge = ageDirection ? ageProperty.max : ageProperty.min;
        ImmutableBlockState tipState = currentState.with(ageProperty, tipAge);
        BukkitBlockInWorld blockInWorld = (BukkitBlockInWorld) worldObj.getBlockAt(pos);
        blockInWorld.block().setBlockData(BlockStateUtils.fromBlockData(tipState.customBlockState().handle()));
        updateBlocks(world, blockPos);
    }

    @Override
    public void onRemove(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        Object level = args[1];
        Object pos = args[2];
            updateBlocks(level, pos);
    }

    /**
     * Walks downward from the placed block and updates each segment's age based on
     * ageDirection.
     */
    protected void updateBlocks(Object world, Object topPos) {
        World bworld = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(world));
        BlockPos pos = LocationUtils.fromBlockPos(topPos).offset(0, -1, 0);
        // Initialize first below segment age
        int ageValue = ageDirection ? ageProperty.max - 1 : ageProperty.min + 1;
        while (true) {
            ImmutableBlockState belowState = bworld.getBlockAt(pos).customBlockState();
            if (belowState == null) {
                break;
            }
            if (belowState.isEmpty()) {
                break;
            }
            if (belowState.owner().value() != super.customBlock) {
                break;
            }
            belowState = belowState.with(ageProperty, ageValue);

            BukkitBlockInWorld blockInWorldBelow = (BukkitBlockInWorld) bworld.getBlockAt(pos);
            blockInWorldBelow.block()
                    .setBlockData(BlockStateUtils.fromBlockData(belowState.customBlockState().handle()));
            // Adjust age for next segment
            if (ageDirection) {
                if (ageValue > ageProperty.min)
                    ageValue--;
            } else {
                if (ageValue < ageProperty.max)
                    ageValue++;
            }
            pos = pos.offset(0, -1, 0);
        }
    }
}
