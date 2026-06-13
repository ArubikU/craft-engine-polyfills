package dev.arubik.craftengine.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.momirealms.craftengine.core.util.VersionHelper;

public final class MBlocks {
    private MBlocks() {}

    public static final Object AIR;
    public static final Object AIR$defaultState;
    public static final Object STONE;
    public static final Object STONE$defaultState;
    public static final Object FIRE;
    public static final Object SOUL_FIRE;
    public static final Object ICE;
    public static final Object SHORT_GRASS;
    public static final Object SHORT_GRASS$defaultState;
    public static final Object SHULKER_BOX;
    public static final Object COMPOSTER;
    public static final Object BUBBLE_COLUMN;

    private static Object getById(String id) {
        return BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(id));
    }

    static {
        BUBBLE_COLUMN = getById("bubble_column");
        AIR = getById("air");
        AIR$defaultState = ((Block) AIR).defaultBlockState();
        FIRE = getById("fire");
        SOUL_FIRE = getById("soul_fire");
        STONE = getById("stone");
        STONE$defaultState = ((Block) STONE).defaultBlockState();
        ICE = getById("ice");
        SHORT_GRASS = getById(VersionHelper.isOrAbove1_20_3() ? "short_grass" : "grass");
        SHORT_GRASS$defaultState = ((Block) SHORT_GRASS).defaultBlockState();
        SHULKER_BOX = getById("shulker_box");
        COMPOSTER = getById("composter");
    }
}
