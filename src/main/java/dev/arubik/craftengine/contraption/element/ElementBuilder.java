package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.world.CEWorld;

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

            // Special elements for blocks with custom behavior
            if (blockState.getBlock() instanceof net.minecraft.world.level.block.AbstractSkullBlock) {
                elements.add(new dev.arubik.craftengine.contraption.element.special.ContraptionSkullElement(local, blockState, beTag));
                continue;
            }
            if (blockState.getBlock() instanceof net.minecraft.world.level.block.JukeboxBlock) {
                elements.add(new dev.arubik.craftengine.contraption.element.special.ContraptionJukeboxElement(local, blockState, beTag));
                continue;
            }
            if (blockState.getBlock() instanceof net.minecraft.world.level.block.CampfireBlock) {
                elements.add(new dev.arubik.craftengine.contraption.element.special.ContraptionCampfireElement(local, blockState));
                continue;
            }

            // Bed foot: skip entirely — the head cell draws the whole bed visual.
            if (blockState.getBlock() instanceof BedBlock
                    && blockState.getValue(BedBlock.PART) == BedPart.FOOT) {
                continue;
            }

            boolean hasEntityRenderer = hasConstantEntityRenderer(level, local);
            float modelYawOffset = 0f;

            if (!hasEntityRenderer && blockState.getBlock() instanceof BedBlock) {
                // HEAD half: BedSpecialRenderer hardcodes SOUTH — correct by adding the captured facing yaw.
                modelYawOffset = blockState.getValue(BedBlock.FACING).toYRot();
                // Only add the element if the foot partner is also captured (half-captured bed → invisible).
                BlockPos partner = local.relative(BedBlock.getConnectedDirection(blockState));
                if (!level.localPositions().contains(partner)) {
                    continue; // half-captured bed — render nothing
                }
                BlockState partnerState = level.getBlockState(partner);
                boolean paired = partnerState.getBlock() == blockState.getBlock()
                        && partnerState.getValue(BedBlock.PART) == BedPart.FOOT
                        && partnerState.getValue(BedBlock.FACING) == blockState.getValue(BedBlock.FACING);
                if (!paired) continue;
            }

            elements.add(new ContraptionBlockElement(local, blockState, beTag, hasEntityRenderer, modelYawOffset));

            if (hasEntityRenderer) {
                elements.add(new ContraptionEntityRendererElement(local));
            }
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

    /** True when the CE chunk at this position declares an entity-renderer config for the current blockstate. */
    static boolean hasConstantEntityRenderer(ContraptionLevel level, BlockPos local) {
        try {
            org.bukkit.World w = level.getWorld();
            CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(w.getUID());
            if (ceWorld == null) return false;
            net.momirealms.craftengine.core.world.BlockPos cePos =
                    new net.momirealms.craftengine.core.world.BlockPos(local.getX(), local.getY(), local.getZ());
            net.momirealms.craftengine.core.world.chunk.CEChunk chunk = ceWorld.getChunkAtIfLoaded(cePos);
            return chunk != null && chunk.getConstantBlockEntityRenderer(cePos) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
