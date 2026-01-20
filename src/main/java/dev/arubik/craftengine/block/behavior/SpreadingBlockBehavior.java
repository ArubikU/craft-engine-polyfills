package dev.arubik.craftengine.block.behavior;

import java.util.ArrayList;
import java.util.Collections;
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

import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.BlockTags;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.random.RandomUtils;
import net.momirealms.craftengine.core.world.BlockPos;

@SuppressWarnings("DuplicatedCode")
public class SpreadingBlockBehavior extends BukkitBlockBehavior {

    public static final Factory FACTORY = new Factory();

    private final double spreadChance;
    private final int spreadRadius;
    private final int maxPerTick;
    private final List<ReplaceRule> rules;

    public SpreadingBlockBehavior(CustomBlock block,
            double spreadChance,
            int spreadRadius,
            int maxPerTick,
            List<ReplaceRule> rules) {
        super(block);
        this.spreadChance = spreadChance;
        this.spreadRadius = spreadRadius;
        this.maxPerTick = maxPerTick;
        this.rules = rules;
    }

    @Override
    public void randomTick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        if (RandomUtils.generateRandomFloat(0, 1) >= this.spreadChance)
            return;

        Object level = args[1];
        BlockPos pos = LocationUtils.fromBlockPos(args[2]);
        int replaced = 0;
        List<BlockPos> nearby = getNearby(pos, spreadRadius);
        Collections.shuffle(nearby);

        for (BlockPos targetPos : nearby) {
            if (replaced >= maxPerTick)
                break;

            Object targetState = FastNMS.INSTANCE.method$BlockGetter$getBlockState(level,
                    LocationUtils.toBlockPos(targetPos));

            ReplaceRule matched = getRuleFor(targetState);
            if (matched == null)
                continue;

            Object newState = matched.resolveReplacement();
            if (newState == null)
                continue;

            FastNMS.INSTANCE.method$LevelWriter$setBlock(level, LocationUtils.toBlockPos(targetPos), newState, 3);
            replaced++;
        }
    }

    private ReplaceRule getRuleFor(Object state) {
        for (ReplaceRule rule : rules) {
            if (rule.matches(state)) {
                return rule;
            }
        }
        return null;
    }

    private List<BlockPos> getNearby(BlockPos origin, int radius) {
        List<BlockPos> list = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && y == 0 && z == 0)
                        continue;
                    list.add(new BlockPos(origin.x() + x, origin.y() + y, origin.z() + z));
                }
            }
        }
        return list;
    }

    // --- ReplaceRule ---
    static class ReplaceRule {
        final List<Object> tags;
        final Set<Object> blockStates;
        final Set<String> customBlocks;
        final String replacement;

        ReplaceRule(List<Object> tags, Set<Object> blockStates, Set<String> customBlocks, String replacement) {
            this.tags = tags;
            this.blockStates = blockStates;
            this.customBlocks = customBlocks;
            this.replacement = replacement;
        }

        boolean matches(Object state) {
            // Match por tags
            for (Object tag : tags) {
                if (FastNMS.INSTANCE.method$BlockStateBase$is(state, tag))
                    return true;
            }
            // Match por vanilla blockstates
            if (blockStates.contains(state))
                return true;

            // Match por custom
            Optional<ImmutableBlockState> customOpt = BlockStateUtils.getOptionalCustomBlockState(state);
            if (customOpt.isPresent()) {
                String id = customOpt.get().owner().value().id().toString();
                return customBlocks.contains(id) || customBlocks.contains(customOpt.get().toString());
            }
            return false;
        }

        Object resolveReplacement() {
            // Vanilla
            Material material = Registry.MATERIAL.get(NamespacedKey.fromString(replacement));
            if (material != null) {
                return BlockStateUtils.blockDataToBlockState(Bukkit.createBlockData(replacement));
            }
            // Custom
            Optional<ImmutableBlockState> custom = BlockStateUtils.getOptionalCustomBlockState(
                    BlockStateUtils.blockDataToBlockState(Bukkit.createBlockData(replacement)));
            if (custom.isPresent()) {
                return custom.get().customBlockState().literalObject();
            }
            return null;
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @SuppressWarnings("unchecked")
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            double spreadChance = Double.parseDouble(arguments.getOrDefault("spread-chance", 0.2).toString());
            int spreadRadius = Integer.parseInt(arguments.getOrDefault("spread-radius", 3).toString());
            int maxPerTick = Integer.parseInt(arguments.getOrDefault("max-per-tick", 1).toString());

            List<ReplaceRule> rules = new ArrayList<>();
            Map<String, Object> replaces = (Map<String, Object>) arguments.getOrDefault("replaces", Map.of());

            for (Map.Entry<String, Object> entry : replaces.entrySet()) {
                String key = entry.getKey();
                String replacement = entry.getValue().toString();

                List<Object> tags = new ArrayList<>();
                Set<Object> blockStates = new HashSet<>();
                Set<String> customBlocks = new HashSet<>();

                if (key.startsWith("#")) {
                    tags.add(BlockTags.getOrCreate(Key.of(key)));
                } else {
                    int idx = key.indexOf('[');
                    Key blockType = idx != -1 ? Key.from(key.substring(0, idx)) : Key.from(key);
                    Material material = Registry.MATERIAL
                            .get(new NamespacedKey(blockType.namespace(), blockType.value()));
                    if (material != null) {
                        if (idx == -1) {
                            // todas las variantes
                            blockStates.addAll(BlockStateUtils.getPossibleBlockStates(blockType));
                        } else {
                            blockStates.add(BlockStateUtils.blockDataToBlockState(Bukkit.createBlockData(key)));
                        }
                    } else {
                        // custom block
                        customBlocks.add(key);
                    }
                }
                rules.add(new ReplaceRule(tags, blockStates, customBlocks, replacement));
            }

            return new SpreadingBlockBehavior(block, spreadChance, spreadRadius, maxPerTick, rules);
        }
    }
}
