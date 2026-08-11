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

import java.util.*;

public final class ElementBuilder {

    private ElementBuilder() {}

    public static void rebuild(ContraptionState state) {
        rebuild(state, List.of());
    }

    /**
     * Diff-based rebuild: reuses existing block elements whose localPos is unchanged,
     * despawns elements for positions no longer present, creates elements for new positions.
     * Called every tick — must not create new entity instances for unchanged cells.
     */
    public static void rebuild(ContraptionState state, java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        ContraptionLevel level = state.level();
        if (level == null) {
            // Despawn all existing block elements
            for (ContraptionElement e : state.elements()) {
                if (isRebuildable(e)) e.despawn(viewers);
            }
            state.setElements(List.of());
            return;
        }

        // Index all existing position-keyed elements (block + special subclasses)
        Map<BlockPos, ContraptionBlockElement> existingByPos = new HashMap<>();
        Map<BlockPos, ContraptionElement> existingSpecialByPos = new HashMap<>();
        for (ContraptionElement e : state.elements()) {
            if (e instanceof ContraptionBlockElement be) {
                existingByPos.put(be.localPos(), be);
            } else if (e instanceof dev.arubik.craftengine.contraption.element.special.ContraptionSkullElement sk) {
                existingSpecialByPos.put(sk.localPos(), sk);
            } else if (e instanceof dev.arubik.craftengine.contraption.element.special.ContraptionSignElement sg) {
                existingSpecialByPos.put(sg.localPos(), sg);
            }
        }

        Set<BlockPos> livePositions = level.localPositions();
        List<ContraptionElement> elements = new ArrayList<>();
        Set<BlockPos> usedPositions = new HashSet<>();

        for (BlockPos local : livePositions) {
            BlockState blockState = level.getBlockState(local);
            if (blockState == null || blockState.isAir()) continue;

            // Reuse existing element if the block type hasn't changed
            ContraptionBlockElement existing = existingByPos.get(local);
            if (existing != null && existing.blockState() != null
                    && existing.blockState().getBlock() == blockState.getBlock()) {
                elements.add(existing);
                usedPositions.add(local);
                // If entity renderer is needed, also carry over or add it
                if (existing.hasEntityRenderer) {
                    // check if entity renderer element already exists for this pos
                    boolean hasRenderer = false;
                    for (ContraptionElement e : state.elements()) {
                        if (e instanceof ContraptionEntityRendererElement er
                                && er.localPos() != null && er.localPos().equals(local)) {
                            elements.add(er);
                            hasRenderer = true;
                            break;
                        }
                    }
                    if (!hasRenderer) elements.add(new ContraptionEntityRendererElement(local));
                }
                continue;
            }

            // New or changed block — create fresh element
            CompoundTag beTag = level.saveBlockEntity(local);

            if (blockState.getBlock() instanceof net.minecraft.world.level.block.AbstractSkullBlock) {
                var existingSkull = existingSpecialByPos.get(local);
                if (existingSkull instanceof dev.arubik.craftengine.contraption.element.special.ContraptionSkullElement sk
                        && sk.blockState().getBlock() == blockState.getBlock()) {
                    elements.add(sk);
                } else {
                    if (existingSkull != null) existingSkull.despawn(viewers);
                    elements.add(new dev.arubik.craftengine.contraption.element.special.ContraptionSkullElement(local, blockState, beTag));
                }
                usedPositions.add(local); continue;
            }
            if (blockState.getBlock() instanceof net.minecraft.world.level.block.JukeboxBlock) {
                elements.add(new dev.arubik.craftengine.contraption.element.special.ContraptionJukeboxElement(local, blockState, beTag));
                usedPositions.add(local); continue;
            }
            if (blockState.getBlock() instanceof net.minecraft.world.level.block.CampfireBlock) {
                elements.add(new dev.arubik.craftengine.contraption.element.special.ContraptionCampfireElement(local, blockState));
                usedPositions.add(local); continue;
            }
            if (blockState.getBlock() instanceof net.minecraft.world.level.block.AbstractBannerBlock
                    || blockState.getBlock() instanceof net.minecraft.world.level.block.SignBlock
                    || blockState.getBlock() instanceof net.minecraft.world.level.block.WallSignBlock
                    || blockState.getBlock() instanceof net.minecraft.world.level.block.CeilingHangingSignBlock
                    || blockState.getBlock() instanceof net.minecraft.world.level.block.WallHangingSignBlock) {
                var existingSign = existingSpecialByPos.get(local);
                if (existingSign instanceof dev.arubik.craftengine.contraption.element.special.ContraptionSignElement sg
                        && sg.blockState().getBlock() == blockState.getBlock()) {
                    elements.add(sg);
                } else {
                    if (existingSign != null) existingSign.despawn(viewers);
                    elements.add(new dev.arubik.craftengine.contraption.element.special.ContraptionSignElement(local, blockState, beTag));
                }
                usedPositions.add(local); continue;
            }

            if (blockState.getBlock() instanceof BedBlock
                    && blockState.getValue(BedBlock.PART) == BedPart.FOOT) {
                usedPositions.add(local); continue;
            }

            boolean hasEntityRenderer = hasConstantEntityRenderer(level, local);
            float modelYawOffset = 0f;

            if (!hasEntityRenderer && blockState.getBlock() instanceof BedBlock) {
                modelYawOffset = blockState.getValue(BedBlock.FACING).toYRot();
                BlockPos partner = local.relative(BedBlock.getConnectedDirection(blockState));
                if (!livePositions.contains(partner)) { usedPositions.add(local); continue; }
                BlockState partnerState = level.getBlockState(partner);
                boolean paired = partnerState.getBlock() == blockState.getBlock()
                        && partnerState.getValue(BedBlock.PART) == BedPart.FOOT
                        && partnerState.getValue(BedBlock.FACING) == blockState.getValue(BedBlock.FACING);
                if (!paired) { usedPositions.add(local); continue; }
            }

            elements.add(new ContraptionBlockElement(local, blockState, beTag, hasEntityRenderer, modelYawOffset));
            usedPositions.add(local);
            if (hasEntityRenderer) elements.add(new ContraptionEntityRendererElement(local));
        }

        // Despawn elements for removed positions
        for (Map.Entry<BlockPos, ContraptionBlockElement> entry : existingByPos.entrySet()) {
            if (!usedPositions.contains(entry.getKey())) {
                entry.getValue().despawn(viewers);
            }
        }
        for (Map.Entry<BlockPos, ContraptionElement> entry : existingSpecialByPos.entrySet()) {
            if (!usedPositions.contains(entry.getKey())) {
                entry.getValue().despawn(viewers);
            }
        }

        // Preserve singleton elements (hitbox, overlay, piston shaft, entity mirror, furniture)
        // reuse existing instances so they keep their internal state
        ContraptionHitboxElement hitbox = null;
        ContraptionInteractionOverlayElement overlay = null;
        ContraptionPistonShaftElement shaft = null;
        ContraptionLiveEntityMirrorElement mirror = null;
        for (ContraptionElement e : state.elements()) {
            if (e instanceof ContraptionHitboxElement h && hitbox == null) hitbox = h;
            else if (e instanceof ContraptionInteractionOverlayElement o && overlay == null) overlay = o;
            else if (e instanceof ContraptionPistonShaftElement s && shaft == null) shaft = s;
            else if (e instanceof ContraptionLiveEntityMirrorElement m && mirror == null) mirror = m;
        }

        // Furniture: reuse by definitionId+variantName key
        Map<String, ContraptionFurnitureElement> existingFurniture = new HashMap<>();
        for (ContraptionElement e : state.elements()) {
            if (e instanceof ContraptionFurnitureElement fe) {
                existingFurniture.put(fe.definitionId() + "/" + fe.variantName(), fe);
            }
        }
        for (ContraptionFurniture cf : state.furniture()) {
            String key = cf.definitionId() + "/" + cf.variantName();
            ContraptionFurnitureElement fe = existingFurniture.get(key);
            if (fe != null) {
                elements.add(fe);
            } else {
                elements.add(new ContraptionFurnitureElement(
                        cf.localOffset(), cf.yawOffsetDegrees(),
                        cf.definitionId(), cf.variantName(), cf.liveFurniture()));
            }
        }

        // Item frames: reuse by sourceEntityId
        Map<java.util.UUID, dev.arubik.craftengine.contraption.element.special.ContraptionItemFrameElement> existingFrames = new HashMap<>();
        for (ContraptionElement e : state.elements()) {
            if (e instanceof dev.arubik.craftengine.contraption.element.special.ContraptionItemFrameElement ife) {
                existingFrames.put(ife.sourceEntityId(), ife);
            }
        }
        for (net.minecraft.world.entity.Entity entity : level.getAllEntities()) {
            if (entity instanceof net.minecraft.world.entity.decoration.ItemFrame frame) {
                var existing2 = existingFrames.get(frame.getUUID());
                if (existing2 != null) {
                    elements.add(existing2);
                } else {
                    elements.add(new dev.arubik.craftengine.contraption.element.special.ContraptionItemFrameElement(
                            frame.getUUID(), frame.position(), frame.getDirection(),
                            frame.getItem(), frame.getRotation()));
                }
            }
        }

        // Singletons — reuse existing or create
        if (mirror == null) { mirror = new ContraptionLiveEntityMirrorElement(); }
        mirror.setFurniture(state.furniture());
        elements.add(mirror);

        elements.add(hitbox != null ? hitbox : new ContraptionHitboxElement());
        elements.add(overlay != null ? overlay : new ContraptionInteractionOverlayElement());
        elements.add(shaft != null ? shaft : new ContraptionPistonShaftElement());

        state.setElements(elements);
    }

    /** Elements that should be despawned when removed from the list. */
    private static boolean isRebuildable(ContraptionElement e) {
        return e instanceof ContraptionBlockElement
                || e instanceof ContraptionFurnitureElement
                || e instanceof dev.arubik.craftengine.contraption.element.special.ContraptionItemFrameElement;
    }

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
