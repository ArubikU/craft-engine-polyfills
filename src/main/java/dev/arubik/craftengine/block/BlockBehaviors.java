package dev.arubik.craftengine.block;

import dev.arubik.craftengine.block.behavior.*;
import dev.arubik.craftengine.util.RegistryUtils;
import net.momirealms.craftengine.core.util.Key;

public class BlockBehaviors {
    public static final Key POLYFILL_BUBBLE_BLOCK = Key.of("polyfills:bubble_block");
    public static final Key POLYFILL_TEARING_CROP_BLOCK = Key.of("polyfills:tearing_crop_block");
    public static final Key POLYFILL_TEARING_BLOCK_SPAWN = Key.of("polyfills:tearing_block_spawn");
    public static final Key POLYFILL_VERTICAL_CROP_BLOCK = Key.of("polyfills:vertical_crop_block");
    public static final Key POLYFILL_BUSH_BLOCK = Key.of("polyfills:bush_block");
    public static final Key POLYFILL_CHANGE_OVER_TIME_BLOCK = Key.of("polyfills:change_over_time_block");
    public static final Key POLYFILL_FAN_BLOCK = Key.of("polyfills:fan_block");
    public static final Key POLYFILL_STORAGE_BLOCK = Key.of("polyfills:storage_block");
    public static void register() {
        RegistryUtils.registerBlockBehavior(POLYFILL_BUBBLE_BLOCK, BubbleBlockBehavior.FACTORY);
        RegistryUtils.registerBlockBehavior(POLYFILL_TEARING_CROP_BLOCK, TearingCropBlockBehavior.FACTORY);
        RegistryUtils.registerBlockBehavior(POLYFILL_TEARING_BLOCK_SPAWN, TearingBlockSpawnBehavior.FACTORY);
        RegistryUtils.registerBlockBehavior(POLYFILL_VERTICAL_CROP_BLOCK, VerticalCropBlockBehavior.FACTORY);
        RegistryUtils.registerBlockBehavior(POLYFILL_CHANGE_OVER_TIME_BLOCK, ChangeOverTimeBlockBehavior.FACTORY);
        RegistryUtils.registerBlockBehavior(POLYFILL_BUSH_BLOCK, BushBlockBehavior.FACTORY);
        RegistryUtils.registerBlockBehavior(POLYFILL_FAN_BLOCK, FanBlockBehavior.FACTORY);
        RegistryUtils.registerBlockBehavior(POLYFILL_STORAGE_BLOCK, StorageBlockBehavior.FACTORY);
    }
}
