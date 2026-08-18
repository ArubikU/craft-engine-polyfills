/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.decoration.ItemFrame
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.AbstractBannerBlock
 *  net.minecraft.world.level.block.AbstractSkullBlock
 *  net.minecraft.world.level.block.BedBlock
 *  net.minecraft.world.level.block.CampfireBlock
 *  net.minecraft.world.level.block.CeilingHangingSignBlock
 *  net.minecraft.world.level.block.JukeboxBlock
 *  net.minecraft.world.level.block.SignBlock
 *  net.minecraft.world.level.block.WallHangingSignBlock
 *  net.minecraft.world.level.block.WallSignBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BedPart
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.plugin.CraftEngine
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.chunk.CEChunk
 *  org.bukkit.World
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.contraption.config.ContraptionConfig;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionBlockElement;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ContraptionEntityRendererElement;
import dev.arubik.craftengine.contraption.element.ContraptionFurnitureElement;
import dev.arubik.craftengine.contraption.element.ContraptionHitboxElement;
import dev.arubik.craftengine.contraption.element.ContraptionInteractionOverlayElement;
import dev.arubik.craftengine.contraption.element.ContraptionLiveEntityMirrorElement;
import dev.arubik.craftengine.contraption.element.ContraptionMachineRendererElement;
import dev.arubik.craftengine.contraption.element.ContraptionPistonShaftElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionBetterModelElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionCampfireElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionHangingSignElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionItemFrameElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionJukeboxElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionModelEngineElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionSignElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionSkullElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionStandingSignElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionVanillaFluidElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionWallSignElement;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurniture;
import dev.arubik.craftengine.machine.render.BetterModelDriven;
import dev.arubik.craftengine.machine.render.BetterModelMachineRenderer;
import dev.arubik.craftengine.machine.render.ModelEngineDriven;
import dev.arubik.craftengine.machine.render.ModelEngineMachineRenderer;
import dev.arubik.craftengine.machine.render.ModelRendersDriven;
import dev.arubik.craftengine.machine.render.RendererManager;
import dev.arubik.craftengine.machine.render.RendererSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.chunk.CEChunk;
import org.bukkit.World;

public final class ElementBuilder {
    private ElementBuilder() {
    }

    public static void rebuild(ContraptionState state) {
        ElementBuilder.rebuild(state, List.of());
    }

    public static void rebuild(ContraptionState state, List<Player> viewers) {
        BlockState bs;
        ContraptionLevel level = state.level();
        if (level == null) {
            for (ContraptionElement e : state.elements()) {
                if (!ElementBuilder.isRebuildable(e)) continue;
                e.despawn(viewers);
            }
            state.setElements(List.of());
            return;
        }
        HashMap<BlockPos, ContraptionBlockElement> existingByPos = new HashMap<BlockPos, ContraptionBlockElement>();
        HashMap<BlockPos, ContraptionElement> existingSpecialByPos = new HashMap<BlockPos, ContraptionElement>();
        HashMap<BlockPos, ContraptionBetterModelElement> existingBMByPos = new HashMap<BlockPos, ContraptionBetterModelElement>();
        HashMap<BlockPos, ContraptionModelEngineElement> existingMEByPos = new HashMap<BlockPos, ContraptionModelEngineElement>();
        HashMap<BlockPos, ContraptionEntityRendererElement> existingEntityRendererByPos = new HashMap<BlockPos, ContraptionEntityRendererElement>();
        for (ContraptionElement e : state.elements()) {
            if (e instanceof ContraptionBlockElement) {
                ContraptionBlockElement be = (ContraptionBlockElement)e;
                existingByPos.put(be.localPos(), be);
                continue;
            }
            if (e instanceof ContraptionEntityRendererElement er && er.localPos() != null) {
                existingEntityRendererByPos.put(er.localPos(), er);
                continue;
            }
            if (e instanceof ContraptionSkullElement) {
                ContraptionSkullElement contraptionSkullElement = (ContraptionSkullElement)e;
                existingSpecialByPos.put(contraptionSkullElement.localPos(), contraptionSkullElement);
                continue;
            }
            if (e instanceof ContraptionSignElement) {
                ContraptionSignElement sg = (ContraptionSignElement)e;
                existingSpecialByPos.put(sg.localPos(), sg);
                continue;
            }
            if (e instanceof ContraptionBetterModelElement) {
                ContraptionBetterModelElement bm = (ContraptionBetterModelElement)e;
                existingBMByPos.put(bm.localPos(), bm);
                continue;
            }
            if (!(e instanceof ContraptionModelEngineElement)) continue;
            ContraptionModelEngineElement me = (ContraptionModelEngineElement)e;
            existingMEByPos.put(me.localPos(), me);
        }
        Set<BlockPos> livePositions = level.localPositions();
        ArrayList<ContraptionElement> elements = new ArrayList<ContraptionElement>();
        HashSet<BlockPos> usedPositions = new HashSet<BlockPos>();
        for (BlockPos blockPos : livePositions) {
            FluidState fs;
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState == null || blockState.isAir()) continue;
            ContraptionBlockElement existing = (ContraptionBlockElement)existingByPos.get(blockPos);
            if (existing != null && existing.blockState() != null && existing.blockState().getBlock() == blockState.getBlock()) {
                elements.add(existing);
                usedPositions.add(blockPos);
                if (!existing.hasEntityRenderer) continue;
                ContraptionEntityRendererElement er = (ContraptionEntityRendererElement)existingEntityRendererByPos.get(blockPos);
                elements.add(er != null ? er : new ContraptionEntityRendererElement(blockPos));
                continue;
            }
            CompoundTag beTag = level.saveBlockEntity(blockPos);
            if (blockState.getBlock() instanceof AbstractSkullBlock) {
                ContraptionSkullElement sk;
                ContraptionElement existingSkull = (ContraptionElement)existingSpecialByPos.get(blockPos);
                if (existingSkull instanceof ContraptionSkullElement && (sk = (ContraptionSkullElement)existingSkull).blockState().getBlock() == blockState.getBlock()) {
                    elements.add(sk);
                } else {
                    if (existingSkull != null) {
                        existingSkull.despawn(viewers);
                    }
                    elements.add(new ContraptionSkullElement(blockPos, blockState, beTag));
                }
                usedPositions.add(blockPos);
                continue;
            }
            if (blockState.getBlock() instanceof JukeboxBlock) {
                elements.add(new ContraptionJukeboxElement(blockPos, blockState, beTag));
                usedPositions.add(blockPos);
                continue;
            }
            if (blockState.getBlock() instanceof CampfireBlock) {
                elements.add(new ContraptionCampfireElement(blockPos, blockState));
                usedPositions.add(blockPos);
                continue;
            }
            if (blockState.getBlock() instanceof SignBlock || blockState.getBlock() instanceof WallSignBlock || blockState.getBlock() instanceof CeilingHangingSignBlock || blockState.getBlock() instanceof WallHangingSignBlock || blockState.getBlock() instanceof AbstractBannerBlock) {
                ContraptionSignElement sg;
                ContraptionElement existingSign = (ContraptionElement)existingSpecialByPos.get(blockPos);
                if (existingSign instanceof ContraptionSignElement && (sg = (ContraptionSignElement)existingSign).blockState().getBlock() == blockState.getBlock()) {
                    elements.add(sg);
                } else {
                    if (existingSign != null) {
                        existingSign.despawn(viewers);
                    }
                    elements.add(ElementBuilder.buildSignElement(blockPos, blockState, beTag));
                }
                usedPositions.add(blockPos);
                continue;
            }
            if (blockState.getBlock() instanceof BedBlock && blockState.getValue((Property)BedBlock.PART) == BedPart.FOOT) {
                usedPositions.add(blockPos);
                continue;
            }
            boolean hasEntityRenderer = ElementBuilder.hasConstantEntityRenderer(level, blockPos);
            float modelYawOffset = 0.0f;
            if (!hasEntityRenderer && blockState.getBlock() instanceof BedBlock) {
                boolean paired;
                modelYawOffset = ((Direction)blockState.getValue((Property)BedBlock.FACING)).toYRot();
                BlockPos partner = blockPos.relative(BedBlock.getConnectedDirection((BlockState)blockState));
                if (!livePositions.contains(partner)) {
                    usedPositions.add(blockPos);
                    continue;
                }
                BlockState partnerState = level.getBlockState(partner);
                boolean bl = paired = partnerState.getBlock() == blockState.getBlock() && partnerState.getValue((Property)BedBlock.PART) == BedPart.FOOT && partnerState.getValue((Property)BedBlock.FACING) == blockState.getValue((Property)BedBlock.FACING);
                if (!paired) {
                    usedPositions.add(blockPos);
                    continue;
                }
            }
            elements.add(new ContraptionBlockElement(blockPos, blockState, beTag, hasEntityRenderer, modelYawOffset));
            usedPositions.add(blockPos);
            if (hasEntityRenderer) {
                elements.add(new ContraptionEntityRendererElement(blockPos));
            }
            if ((fs = blockState.getFluidState()).isEmpty() || !ContraptionConfig.get().fluidRenderEnabled()) continue;
            boolean lava = fs.getType() == Fluids.LAVA || fs.getType() == Fluids.FLOWING_LAVA;
            boolean waterlogged = !lava && blockState.hasProperty((Property)BlockStateProperties.WATERLOGGED) && (Boolean)blockState.getValue((Property)BlockStateProperties.WATERLOGGED) != false;
            boolean enabled = lava ? ContraptionConfig.get().renderLava() : ContraptionConfig.get().renderWater();
            if (!enabled) continue;
            elements.add(new ContraptionVanillaFluidElement(blockPos, lava, waterlogged, fs));
        }
        if (BetterModelMachineRenderer.available()) {
            for (BlockPos blockPos : livePositions) {
                bs = level.getBlockState(blockPos);
                if (bs == null || bs.isAir()) continue;
                ContraptionBetterModelElement existingBM = (ContraptionBetterModelElement)existingBMByPos.get(blockPos);
                if (existingBM != null) {
                    elements.add(existingBM);
                    continue;
                }
                try {
                    BlockEntityController modelYawOffset;
                    BlockEntity ceBlockEntity2 = BukkitBlockEntityTypes.getIfLoaded((Level)level.serverLevel(), blockPos);
                    if (ceBlockEntity2 == null || !((modelYawOffset = ceBlockEntity2.controller) instanceof BetterModelDriven)) continue;
                    BetterModelDriven driven = (BetterModelDriven)modelYawOffset;
                    elements.add(new ContraptionBetterModelElement(blockPos, driven.betterModelRenderer()));
                }
                catch (Throwable ceBlockEntity2) {}
            }
            for (Map.Entry entry : existingBMByPos.entrySet()) {
                if (livePositions.contains(entry.getKey())) continue;
                ((ContraptionBetterModelElement)entry.getValue()).despawn(viewers);
            }
        }
        if (ModelEngineMachineRenderer.available()) {
            for (BlockPos blockPos : livePositions) {
                bs = level.getBlockState(blockPos);
                if (bs == null || bs.isAir()) continue;
                ContraptionModelEngineElement existingME = (ContraptionModelEngineElement)existingMEByPos.get(blockPos);
                if (existingME != null) {
                    elements.add(existingME);
                    continue;
                }
                try {
                    BlockEntityController modelYawOffset;
                    BlockEntity ceBlockEntity3 = BukkitBlockEntityTypes.getIfLoaded((Level)level.serverLevel(), blockPos);
                    if (ceBlockEntity3 == null || !((modelYawOffset = ceBlockEntity3.controller) instanceof ModelEngineDriven)) continue;
                    ModelEngineDriven driven = (ModelEngineDriven)modelYawOffset;
                    elements.add(new ContraptionModelEngineElement(blockPos, driven.modelEngineRenderer()));
                }
                catch (Throwable ceBlockEntity3) {}
            }
            for (Map.Entry entry : existingMEByPos.entrySet()) {
                if (livePositions.contains(entry.getKey())) continue;
                ((ContraptionModelEngineElement)entry.getValue()).despawn(viewers);
            }
        }
        HashMap<BlockPos, ContraptionMachineRendererElement> existingMachineRenderers = new HashMap<BlockPos, ContraptionMachineRendererElement>();
        for (ContraptionElement e : state.elements()) {
            if (!(e instanceof ContraptionMachineRendererElement)) continue;
            ContraptionMachineRendererElement mre = (ContraptionMachineRendererElement)e;
            existingMachineRenderers.put(mre.localPos(), mre);
        }
        for (BlockPos local : livePositions) {
            BlockState bs2 = level.getBlockState(local);
            if (bs2 == null || bs2.isAir()) continue;
            try {
                boolean hasNonBmSpec;
                BlockEntityController modelYawOffset;
                BlockEntity ceBlockEntity4 = BukkitBlockEntityTypes.getIfLoaded((Level)level.serverLevel(), local);
                if (ceBlockEntity4 == null || !((modelYawOffset = ceBlockEntity4.controller) instanceof ModelRendersDriven)) continue;
                ModelRendersDriven mrd = (ModelRendersDriven)modelYawOffset;
                RendererManager mgr = mrd.rendererManager();
                if (mgr == null) continue;
                if (BetterModelMachineRenderer.available()) {
                    List<BetterModelMachineRenderer> bmList = mgr.betterModelRenderers();
                    for (BetterModelMachineRenderer bmr : bmList) {
                        if (bmr == null || existingBMByPos.containsKey(local)) continue;
                        elements.add(new ContraptionBetterModelElement(local, bmr));
                    }
                }
                if (!(hasNonBmSpec = mgr.specs().stream().anyMatch(s -> s instanceof RendererSpec.ItemDisplaySpec || s instanceof RendererSpec.TextDisplaySpec || s instanceof RendererSpec.FluidTankSpec || s instanceof RendererSpec.ParticleSpec))) continue;
                ContraptionMachineRendererElement existing = (ContraptionMachineRendererElement)existingMachineRenderers.get(local);
                elements.add(existing != null ? existing : new ContraptionMachineRendererElement(local));
            }
            catch (Throwable ceBlockEntity4) {}
        }
        for (Map.Entry entry : existingMachineRenderers.entrySet()) {
            if (livePositions.contains(entry.getKey())) continue;
            ((ContraptionMachineRendererElement)entry.getValue()).despawn(viewers);
        }
        for (Map.Entry entry : existingByPos.entrySet()) {
            if (usedPositions.contains(entry.getKey())) continue;
            ((ContraptionBlockElement)entry.getValue()).despawn(viewers);
        }
        for (Map.Entry entry : existingSpecialByPos.entrySet()) {
            if (usedPositions.contains(entry.getKey())) continue;
            ((ContraptionElement)entry.getValue()).despawn(viewers);
        }
        for (Map.Entry entry : existingEntityRendererByPos.entrySet()) {
            if (livePositions.contains(entry.getKey())) continue;
            ((ContraptionEntityRendererElement)entry.getValue()).despawn(viewers);
        }
        ContraptionHitboxElement hitbox = null;
        ContraptionInteractionOverlayElement overlay = null;
        ContraptionPistonShaftElement shaft = null;
        ContraptionLiveEntityMirrorElement mirror = null;
        for (ContraptionElement e : state.elements()) {
            if (e instanceof ContraptionHitboxElement) {
                ContraptionHitboxElement h = (ContraptionHitboxElement)e;
                if (hitbox == null) {
                    hitbox = h;
                    continue;
                }
            }
            if (e instanceof ContraptionInteractionOverlayElement) {
                ContraptionInteractionOverlayElement o = (ContraptionInteractionOverlayElement)e;
                if (overlay == null) {
                    overlay = o;
                    continue;
                }
            }
            if (e instanceof ContraptionPistonShaftElement) {
                ContraptionPistonShaftElement s2 = (ContraptionPistonShaftElement)e;
                if (shaft == null) {
                    shaft = s2;
                    continue;
                }
            }
            if (!(e instanceof ContraptionLiveEntityMirrorElement)) continue;
            ContraptionLiveEntityMirrorElement m = (ContraptionLiveEntityMirrorElement)e;
            if (mirror != null) continue;
            mirror = m;
        }
        HashMap<String, ContraptionFurnitureElement> existingFurniture = new HashMap<String, ContraptionFurnitureElement>();
        for (ContraptionElement e : state.elements()) {
            if (!(e instanceof ContraptionFurnitureElement)) continue;
            ContraptionFurnitureElement fe = (ContraptionFurnitureElement)e;
            existingFurniture.put(fe.definitionId() + "/" + fe.variantName(), fe);
        }
        for (ContraptionFurniture cf : state.furniture()) {
            String key = cf.definitionId() + "/" + cf.variantName();
            ContraptionFurnitureElement fe = existingFurniture.get(key);
            if (fe != null) {
                elements.add(fe);
                continue;
            }
            elements.add(new ContraptionFurnitureElement(cf.localOffset(), cf.yawOffsetDegrees(), cf.definitionId(), cf.variantName(), cf.liveFurniture()));
        }
        HashMap<UUID, ContraptionItemFrameElement> existingFrames = new HashMap<UUID, ContraptionItemFrameElement>();
        for (ContraptionElement e : state.elements()) {
            if (!(e instanceof ContraptionItemFrameElement)) continue;
            ContraptionItemFrameElement ife = (ContraptionItemFrameElement)e;
            existingFrames.put(ife.sourceEntityId(), ife);
        }
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof ItemFrame)) continue;
            ItemFrame frame = (ItemFrame)entity;
            ContraptionItemFrameElement existing2 = (ContraptionItemFrameElement)existingFrames.get(frame.getUUID());
            if (existing2 != null) {
                elements.add(existing2);
                continue;
            }
            elements.add(new ContraptionItemFrameElement(frame.getUUID(), frame.position(), frame.getDirection(), frame.getItem(), frame.getRotation()));
        }
        if (mirror == null) {
            mirror = new ContraptionLiveEntityMirrorElement();
        }
        mirror.setFurniture(state.furniture());
        elements.add(mirror);
        //elements.add((ContraptionElement)(var12_32 != null ? var12_32 : new ContraptionHitboxElement()));
        elements.add(hitbox != null ? hitbox : new ContraptionHitboxElement());
        elements.add(overlay != null ? overlay : new ContraptionInteractionOverlayElement());
        elements.add(shaft != null ? shaft : new ContraptionPistonShaftElement());
        state.setElements(elements);
    }

    private static boolean isRebuildable(ContraptionElement e) {
        return e instanceof ContraptionBlockElement || e instanceof ContraptionFurnitureElement || e instanceof ContraptionItemFrameElement;
    }

    private static ContraptionSignElement buildSignElement(BlockPos local, BlockState bs, CompoundTag beTag) {
        if (bs.getBlock() instanceof WallSignBlock) {
            return new ContraptionWallSignElement(local, bs, beTag);
        }
        if (bs.getBlock() instanceof WallHangingSignBlock) {
            return new ContraptionHangingSignElement(local, bs, beTag, false);
        }
        if (bs.getBlock() instanceof CeilingHangingSignBlock) {
            return new ContraptionHangingSignElement(local, bs, beTag, true);
        }
        return new ContraptionStandingSignElement(local, bs, beTag);
    }

    static boolean hasConstantEntityRenderer(ContraptionLevel level, BlockPos local) {
        try {
            World w = level.getWorld();
            CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(w.getUID());
            if (ceWorld == null) {
                return false;
            }
            net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(local.getX(), local.getY(), local.getZ());
            CEChunk chunk = ceWorld.getChunkAtIfLoaded(cePos);
            return chunk != null && chunk.getConstantBlockEntityRenderer(cePos) != null;
        }
        catch (Throwable ignored) {
            return false;
        }
    }
}

