package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Derives the full set of ContraptionElements from a ContraptionLevel + furniture list.
 * Called when a contraption spawns its rendering entity — elements are ephemeral render objects,
 * not persisted. The level (blocks, BEs, CE controller data) and furniture records are the
 * authoritative source; elements are rebuilt from them on every spawn/load.
 */
public final class ElementBuilder {

    private ElementBuilder() {}

    public static void rebuild(ContraptionState state) {
        ContraptionLevel level = state.level();
        if (level == null) {
            state.setElements(List.of());
            return;
        }

        List<ContraptionElement> elements = new ArrayList<>();

        for (BlockPos local : level.localPositions()) {
            BlockState blockState = level.getBlockState(local);
            if (blockState == null || blockState.isAir()) continue;
            CompoundTag beTag = level.saveBlockEntity(local);
            elements.add(new ContraptionBlockElement(local, blockState, beTag));
        }

        for (ContraptionFurniture cf : state.furniture()) {
            elements.add(new ContraptionFurnitureElement(
                    cf.localOffset(), cf.yawOffsetDegrees(),
                    cf.definitionId(), cf.variantName(), cf.liveFurniture()));
        }

        ContraptionLiveEntityMirrorElement entityMirror = new ContraptionLiveEntityMirrorElement();
        entityMirror.setFurniture(state.furniture());
        elements.add(entityMirror);

        elements.add(new ContraptionHitboxElement());

        state.setElements(elements);
    }
}
