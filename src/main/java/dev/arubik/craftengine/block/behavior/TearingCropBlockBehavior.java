package dev.arubik.craftengine.block.behavior;

import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.MBlocks;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.BlockTags;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.bukkit.world.BukkitBlockInWorld;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.IntegerProperty;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.MiscUtils;
import net.momirealms.craftengine.core.util.RandomUtils;
import net.momirealms.craftengine.core.util.ResourceConfigUtils;
import net.momirealms.craftengine.core.util.Tuple;
import net.momirealms.craftengine.core.world.BlockPos;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PointedDripstone;

import dev.arubik.craftengine.CraftEnginePolyfills;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@SuppressWarnings("DuplicatedCode")
public class TearingCropBlockBehavior extends BukkitBlockBehavior {
    public static final Factory FACTORY = new Factory();

    private final boolean water; // true == water , false == lava
    private final IntegerProperty ageProperty;
    private final float growSpeed;
    private final int heightLimit;
    protected final List<Object> tagsCanSurviveOn;
    protected final Set<Object> blockStatesCanSurviveOn;
    protected final Set<String> customBlocksCansSurviveOn;
    protected final boolean blacklistMode;

    public TearingCropBlockBehavior(CustomBlock block, Property<Integer> ageProperty, float growSpeed, boolean water, int heightLimit, 
            boolean blacklist, List<Object> tagsCanSurviveOn, Set<Object> blockStatesCanSurviveOn, Set<String> customBlocksCansSurviveOn) {
        super(block);
        this.ageProperty = (IntegerProperty) ageProperty;
        this.growSpeed = growSpeed;
        this.water = water;
        this.heightLimit = heightLimit;
        this.blacklistMode = blacklist;
        this.tagsCanSurviveOn = tagsCanSurviveOn;
        this.blockStatesCanSurviveOn = blockStatesCanSurviveOn;
        this.customBlocksCansSurviveOn = customBlocksCansSurviveOn;
    }

    public final int getAge(ImmutableBlockState state) {
        return state.get(ageProperty);
    }

    public boolean isMaxAge(ImmutableBlockState state) {
        return state.get(ageProperty) == ageProperty.max;
    }

    public float growSpeed() {
        return growSpeed;
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
            if (this.customBlocksCansSurviveOn.contains(belowCustomState.owner().value().id().toString())) {
                return !this.blacklistMode;
            }
            if (this.customBlocksCansSurviveOn.contains(belowCustomState.toString())) {
                return !this.blacklistMode;
            }
        }
        return this.blacklistMode;
    }


    public BlockPos getTearingDripstone(Object level, BlockPos pos) {
        BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
        int heightLimit = Math.min(this.heightLimit + pos.y(), 320);
        for (int y = pos.y(); y < heightLimit; y++) {
            BlockPos currentPos = new BlockPos(pos.x(), y, pos.z());
            BukkitBlockInWorld blockInWorld = (BukkitBlockInWorld) world.getBlockAt(currentPos);
            if (blockInWorld == null || blockInWorld.block().getType() == Material.AIR) continue;
            if(blockInWorld.block().getBlockData() instanceof PointedDripstone pDrip && pDrip.getVerticalDirection() == BlockFace.DOWN){
                return currentPos;
            }else{
                continue;
            }
        }
        return null;
    }

    public boolean canTear(BukkitWorld world, BlockPos pos) {
        BukkitBlockInWorld blockInWorld = (BukkitBlockInWorld) world.getBlockAt(pos);
        BlockData blockData = blockInWorld.block().getBlockData();

        if (!(blockData instanceof PointedDripstone pDripstone)) return false;
        if (pDripstone.getVerticalDirection() != BlockFace.DOWN) return false;

        BlockPos highest = findHighestConnectedDripstone(pos, world);
        
        if (highest == null) return false;

        BlockPos fluidPos = highest.above().above();
        
        // Usar BukkitBlockInWorld para verificar el estado del fluido
        BukkitBlockInWorld fluidBlock = (BukkitBlockInWorld) world.getBlockAt(fluidPos);
        BlockData fluidBlockData = fluidBlock.block().getBlockData();
        
        // Verificar si es agua directa o waterlogged
        boolean hasWater = fluidBlockData.getMaterial() == Material.WATER || 
                          (fluidBlockData instanceof org.bukkit.block.data.Waterlogged waterlogged && waterlogged.isWaterlogged());
        
        // Verificar si es lava directa
        boolean hasLava = fluidBlockData.getMaterial() == Material.LAVA;
        
        return (water && hasWater) || (!water && hasLava);
    }

    public BlockPos findHighestConnectedDripstone(BlockPos start, BukkitWorld world) {
        BlockPos pos = start;
        while (true) {
            BlockPos above = pos.above();
            BukkitBlockInWorld blockInWorld = (BukkitBlockInWorld) world.getBlockAt(above);
            BlockData blockData = blockInWorld.block().getBlockData();
            if (!(blockData instanceof PointedDripstone pDripstone) || pDripstone.getVerticalDirection() != BlockFace.DOWN) break;
            pos = above;
        }
        return pos;
    }

    @Override
    public void randomTick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        if(RandomUtils.generateRandomFloat(0, 1) >= this.growSpeed) return;
        Object level = args[1];
        BlockPos pos = LocationUtils.fromBlockPos(args[2]);
        
        // Verificar si puede crecer en el bloque de abajo
        int y = pos.y();
        int x = pos.x();
        int z = pos.z();
        Object belowPos = FastNMS.INSTANCE.constructor$BlockPos(x, y - 1, z);
        Object belowState = FastNMS.INSTANCE.method$BlockGetter$getBlockState(level, belowPos);
        
        if (!mayPlaceOn(belowState, level, belowPos)) {
            return; // No puede crecer si no está en un bloque válido
        }
        BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
        BlockPos targetPos = pos.above();
        BlockPos tearing = getTearingDripstone(level, targetPos);
        if (tearing == null){
            return;
        }
        if (!canTear(world, tearing)){
            return;
        }
        
        BukkitBlockInWorld block = (BukkitBlockInWorld) world.getBlockAt(pos);
        
        int age = this.getAge(block.customBlockState());
        
        if (age < this.ageProperty.max ) {
            BukkitBlockInWorld blockInWorld = (BukkitBlockInWorld) world.getBlockAt(pos);
            blockInWorld.block().setBlockData(BlockStateUtils.fromBlockData(block.customBlock().defaultState().with(this.ageProperty, age + 1).customBlockState().handle()));
        }
    }


    public static class Factory implements BlockBehaviorFactory {

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

        @SuppressWarnings("unchecked")
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            Property<Integer> ageProperty = (Property<Integer>) ResourceConfigUtils.requireNonNullOrThrow(block.getProperty("age"), "warning.config.block.behavior.crop.missing_age");
            float growSpeed = ResourceConfigUtils.getAsFloat(arguments.getOrDefault("grow-speed", 0.125f), "grow-speed");
            Boolean water = arguments.getOrDefault("liquid", "water").toString().equalsIgnoreCase("water");
            int heightLimit = Integer.parseInt(arguments.getOrDefault("heightLimit", 12).toString());
            
            Tuple<List<Object>, Set<Object>, Set<String>> tuple = readTagsAndState(arguments, false);
            boolean blacklistMode = ResourceConfigUtils.getAsBoolean(arguments.getOrDefault("blacklist", false), "blacklist");
            
            return new TearingCropBlockBehavior(block, ageProperty, growSpeed, water, heightLimit, blacklistMode, tuple.left(), tuple.mid(), tuple.right());
        }
    }
}
