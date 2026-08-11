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
            // Special elements for blocks with custom behavior
            if (blockState.getBlock() instanceof net.minecraft.world.level.block.CampfireBlock) {
                elements.add(new dev.arubik.craftengine.contraption.element.special.ContraptionCampfireElement(local, blockState));
                continue;
            }
            CompoundTag beTag = level.saveBlockEntity(local);
            elements.add(new ContraptionBlockElement(local, blockState, beTag));
        }

        for (ContraptionFurniture cf : state.furniture()) {
            elements.add(new ContraptionFurnitureElement(
                    cf.localOffset(), cf.yawOffsetDegrees(),
                    cf.definitionId(), cf.variantName(), cf.liveFurniture()));
        }

        for (net.minecraft.world.entity.Entity entity : level.getAllEntities()) {
            if (entity instanceof net.minecraft.world.entity.decoration.ItemFrame frame) {
                elements.add(new dev.arubik.craftengine.contraption.element.special.ContraptionItemFrameElement(
                        frame.getUUID(),
                        frame.position(),
                        frame.getDirection(),
                        frame.getItem(),
                        frame.getRotation()));
            }
        }

        ContraptionLiveEntityMirrorElement entityMirror = new ContraptionLiveEntityMirrorElement();
        entityMirror.setFurniture(state.furniture());
        elements.add(entityMirror);

        elements.add(new ContraptionHitboxElement());
        elements.add(new ContraptionInteractionOverlayElement());
        elements.add(new ContraptionPistonShaftElement());

        state.setElements(elements);
    }
}
