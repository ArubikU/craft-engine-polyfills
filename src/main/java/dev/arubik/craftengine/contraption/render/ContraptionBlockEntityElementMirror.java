/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  kr.toxicity.model.api.BetterModel
 *  kr.toxicity.model.api.animation.AnimationIterator$Type
 *  kr.toxicity.model.api.animation.AnimationModifier
 *  kr.toxicity.model.api.bukkit.platform.BukkitAdapter
 *  kr.toxicity.model.api.platform.PlatformPlayer
 *  kr.toxicity.model.api.tracker.DummyTracker
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ArmorStandBlockEntityElement
 *  net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ItemBlockEntityElement
 *  net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ItemDisplayBlockEntityElement
 *  net.momirealms.craftengine.bukkit.block.entity.renderer.constant.TextDisplayBlockEntityElement
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$ItemDisplayData
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.entity.render.BlockEntityRenderer
 *  net.momirealms.craftengine.core.block.entity.render.ConstantBlockEntityRenderer
 *  net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElement
 *  net.momirealms.craftengine.core.block.entity.render.element.ConstantBlockEntityElement
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.plugin.CraftEngine
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.chunk.CEChunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.contraption.render;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.render.ContraptionLightEmitters;
import dev.arubik.craftengine.contraption.render.ContraptionRenderScale;
import dev.arubik.craftengine.machine.render.BetterModelDriven;
import dev.arubik.craftengine.machine.render.BetterModelMachineRenderer;
import dev.arubik.craftengine.machine.render.ModelRendersDriven;
import dev.arubik.craftengine.machine.render.RendererManager;
import dev.arubik.craftengine.machine.render.RendererSpec;
import dev.arubik.craftengine.machine.render.formula.PolyContext;
import dev.arubik.craftengine.machine.render.formula.PolyFormula;
import dev.arubik.craftengine.machine.render.formula.PolyValue;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationIterator;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.platform.PlatformPlayer;
import kr.toxicity.model.api.tracker.DummyTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ArmorStandBlockEntityElement;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ItemBlockEntityElement;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.ItemDisplayBlockEntityElement;
import net.momirealms.craftengine.bukkit.block.entity.renderer.constant.TextDisplayBlockEntityElement;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.render.BlockEntityRenderer;
import net.momirealms.craftengine.core.block.entity.render.ConstantBlockEntityRenderer;
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElement;
import net.momirealms.craftengine.core.block.entity.render.element.ConstantBlockEntityElement;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.chunk.CEChunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public final class ContraptionBlockEntityElementMirror {
    private final Map<Long, List<Cell>> byBlock = new HashMap<Long, List<Cell>>();
    private final Map<Long, BlockState> wrappedFrom = new HashMap<Long, BlockState>();
    private int lastAmbientBlockLight = 15;
    private int lastAmbientSkyLight = 15;

    /*
     * WARNING - void declaration
     */
    public void rebuild(ContraptionLevel level, List<Player> viewers) {
        if (level == null) {
            for (List<Cell> cells : this.byBlock.values()) {
                for (Cell cell : cells) {
                    cell.despawnAll(viewers);
                }
            }
            this.byBlock.clear();
            this.wrappedFrom.clear();
            return;
        }
        Set<BlockPos> wanted = level.localPositions();
        Iterator<Map.Entry<Long, List<Cell>>> it = this.byBlock.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<Cell>> e = it.next();
            if (wanted.contains(BlockPos.of((long)e.getKey()))) continue;
            for (Cell cell : e.getValue()) {
                cell.despawnAll(viewers);
            }
            this.wrappedFrom.remove(e.getKey());
            it.remove();
        }
        CEWorld ceWorld = ContraptionBlockEntityElementMirror.ceWorldOf(level);
        if (ceWorld == null) {
            return;
        }
        for (BlockPos local : wanted) {
            Cell cell;
            BlockState nowState = level.getBlockState(local);
            List<Cell> tracked = this.byBlock.get(local.asLong());
            if (tracked != null) {
                if (nowState.equals(this.wrappedFrom.get(local.asLong()))) continue;
                for (Cell cell2 : tracked) {
                    cell2.despawnAll(viewers);
                }
                this.byBlock.remove(local.asLong());
                this.wrappedFrom.remove(local.asLong());
            }
            net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(local.getX(), local.getY(), local.getZ());
            ArrayList<Cell> cells = new ArrayList<Cell>();
            try {
                ConstantBlockEntityRenderer constant;
                CEChunk chunk = ceWorld.getChunkAtIfLoaded(cePos);
                if (chunk != null && (constant = chunk.getConstantBlockEntityRenderer(cePos)) != null) {
                    for (ConstantBlockEntityElement constantBlockEntityElement : constant.elements()) {
                        cell = ContraptionBlockEntityElementMirror.wrap((BlockEntityElement)constantBlockEntityElement, local);
                        if (cell == null) continue;
                        cells.add(cell);
                    }
                }
            }
            catch (Throwable chunk) {
                // empty catch block
            }
            try {
                BlockEntityRenderer renderer;
                BlockEntity be2 = ceWorld.getBlockEntityAtIfLoaded(cePos, false);
                if (be2 != null && (renderer = be2.renderer()) != null) {
                    for (ConstantBlockEntityElement constantBlockEntityElement : renderer.elements()) {
                        cell = ContraptionBlockEntityElementMirror.wrap((BlockEntityElement)constantBlockEntityElement, local);
                        if (cell == null) continue;
                        cells.add(cell);
                    }
                }
            }
            catch (Throwable be2) {
                // empty catch block
            }
            try {
                BlockEntity be = ceWorld.getBlockEntityAtIfLoaded(cePos, false);
                if (be != null) {
                    ModelRendersDriven mrd;
                    BlockEntityController blockEntityController = be.controller;
                    if (blockEntityController instanceof ModelRendersDriven && (mrd = (ModelRendersDriven)blockEntityController).rendererManager() != null) {
                        RendererManager mgr = mrd.rendererManager();
                        List<BetterModelMachineRenderer> bmList = mgr.betterModelRenderers();
                        boolean bl = false;
                        for (int i = 0; i < bmList.size(); i++) {
                            BetterModelMachineRenderer bmr = bmList.get(i);
                            if (bmr != null) {
                                cells.add(new BetterModelCell(bmr, local));
                            }
                        }
                        List<RendererSpec> list = mgr.specs();
                        for (int i = 0; i < list.size(); ++i) {
                            RendererSpec rendererSpec = list.get(i);
                            if (!(rendererSpec instanceof RendererSpec.ItemDisplaySpec)) continue;
                            RendererSpec.ItemDisplaySpec idSpec = (RendererSpec.ItemDisplaySpec)rendererSpec;
                            cells.add(new MachineItemDisplayCell(idSpec, mgr, i, local));
                        }
                    } else {
                        blockEntityController = be.controller;
                        if (blockEntityController instanceof BetterModelDriven) {
                            BetterModelDriven driven = (BetterModelDriven)blockEntityController;
                            cells.add(new BetterModelCell(driven.betterModelRenderer(), local));
                        }
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (cells.isEmpty()) continue;
            this.byBlock.put(local.asLong(), cells);
            this.wrappedFrom.put(local.asLong(), nowState);
        }
    }

    public void render(List<Player> viewers, ContraptionLevel level, ServerLevel realLevel, double scale) {
        if (level == null) {
            return;
        }
        this.updateAmbientLight(level, realLevel);
        for (List<Cell> cells : this.byBlock.values()) {
            for (Cell cell : cells) {
                int blockLight = ContraptionLightEmitters.withEmitterFalloff(level, cell.local, this.lastAmbientBlockLight);
                cell.render(viewers, level, blockLight, this.lastAmbientSkyLight, scale);
            }
        }
    }

    public void render(List<Player> viewers, ContraptionLevel level, ServerLevel realLevel) {
        this.render(viewers, level, realLevel, 1.0);
    }

    public void render(List<Player> viewers, ContraptionLevel level) {
        this.render(viewers, level, null, 1.0);
    }

    private void updateAmbientLight(ContraptionLevel level, ServerLevel realLevel) {
        if (realLevel == null) {
            this.lastAmbientBlockLight = 15;
            this.lastAmbientSkyLight = 15;
            return;
        }
        try {
            Vec3 bearingWorldPos = level.realWorldPositionOf(Vec3.ZERO);
            BlockPos pos = BlockPos.containing((double)bearingWorldPos.x, (double)bearingWorldPos.y, (double)bearingWorldPos.z);
            this.lastAmbientBlockLight = realLevel.getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(pos);
            this.lastAmbientSkyLight = realLevel.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos);
        }
        catch (Throwable ignored) {
            this.lastAmbientBlockLight = 15;
            this.lastAmbientSkyLight = 15;
        }
    }

    public void despawnAll(List<Player> viewers) {
        for (List<Cell> cells : this.byBlock.values()) {
            for (Cell cell : cells) {
                cell.despawnAll(viewers);
            }
        }
        this.byBlock.clear();
        this.wrappedFrom.clear();
    }

    public int cellCount() {
        int n = 0;
        for (List<Cell> cells : this.byBlock.values()) {
            n += cells.size();
        }
        return n;
    }

    private static CEWorld ceWorldOf(ContraptionLevel level) {
        try {
            World bukkitWorld = level.getWorld();
            return CraftEngine.instance().worldManager().getWorld(bukkitWorld.getUID());
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static Cell wrap(BlockEntityElement element, BlockPos local) {
        if (element instanceof ItemDisplayBlockEntityElement) {
            ItemDisplayBlockEntityElement e = (ItemDisplayBlockEntityElement)element;
            return new ItemDisplayCell(e, local);
        }
        if (element instanceof TextDisplayBlockEntityElement) {
            TextDisplayBlockEntityElement e = (TextDisplayBlockEntityElement)element;
            return new TextDisplayCell(e, local);
        }
        if (element instanceof ArmorStandBlockEntityElement) {
            ArmorStandBlockEntityElement e = (ArmorStandBlockEntityElement)element;
            return new ArmorStandCell(e, local);
        }
        if (element instanceof ItemBlockEntityElement) {
            ItemBlockEntityElement e = (ItemBlockEntityElement)element;
            return new ItemRideCell(e, local);
        }
        return null;
    }

    private static void addInterpolationTuning(List<Object> values) {
        DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
    }

    private static abstract class Cell {
        final BlockPos local;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        private int blockLight = -1;
        private int skyLight = -1;
        private double scale = 1.0;
        private Quaternionf rotation;

        Cell(BlockPos local) {
            this.local = local;
        }

        abstract void spawn(Player var1, Vec3 var2, float var3);

        abstract void updatePosition(Player var1, Vec3 var2, float var3);

        abstract void updateMetadata(Player var1);

        abstract void despawn(Player var1);

        abstract Vector3f offset();

        abstract float baseYaw();

        abstract float basePitch();

        Quaternionf modelRotation(ContraptionLevel level) {
            double pitchRadians = level.realPitchRadians();
            double rollRadians = level.realRollRadians();
            if (pitchRadians == 0.0 && rollRadians == 0.0) {
                return null;
            }
            Quaternionf tilt = new Quaternionf().rotateX((float)pitchRadians).rotateZ((float)rollRadians);
            Quaternionf e0 = new Quaternionf().rotateY((float)(-Math.toRadians(this.baseYaw()))).rotateX((float)Math.toRadians(this.basePitch()));
            return e0.invert(new Quaternionf()).mul((Quaternionfc)tilt).mul((Quaternionfc)e0);
        }

        int blockLight() {
            return this.blockLight;
        }

        int skyLight() {
            return this.skyLight;
        }

        double scale() {
            return this.scale;
        }

        Quaternionf rotation() {
            return this.rotation;
        }

        private boolean rotationChanged(Quaternionf next) {
            if (this.rotation == null || next == null) {
                return this.rotation != next;
            }
            return !next.equals((Quaternionfc)this.rotation, 1.0E-4f);
        }

        void render(List<Player> viewers, ContraptionLevel level, int blockLight, int skyLight, double scale) {
            Vector3f off = this.offset();
            Vec3 localPos = new Vec3((double)((float)this.local.getX() + off.x), (double)((float)this.local.getY() + off.y), (double)((float)this.local.getZ() + off.z));
            Vec3 realPos = level.realWorldPositionOf(localPos);
            float yawDegrees = this.baseYaw() + (float)Math.toDegrees(level.realYawRadians());
            Quaternionf rotation = this.modelRotation(level);
            boolean metaChanged = blockLight != this.blockLight || skyLight != this.skyLight || scale != this.scale || this.rotationChanged(rotation);
            this.blockLight = blockLight;
            this.skyLight = skyLight;
            this.scale = scale;
            this.rotation = rotation;
            HashSet<UUID> current = new HashSet<UUID>();
            for (Player p : viewers) {
                UUID id = Cell.uuidOf(p);
                if (id == null) continue;
                current.add(id);
                if (this.shownTo.add(id)) {
                    this.spawn(p, realPos, yawDegrees);
                    continue;
                }
                this.updatePosition(p, realPos, yawDegrees);
                if (!metaChanged) continue;
                this.updateMetadata(p);
            }
            this.shownTo.retainAll(current);
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                this.despawn(p);
            }
            this.shownTo.clear();
        }

        static UUID uuidOf(Player player) {
            UUID uUID;
            Object pp = player.platformPlayer();
            if (pp instanceof org.bukkit.entity.Player) {
                org.bukkit.entity.Player b = (org.bukkit.entity.Player)pp;
                uUID = b.getUniqueId();
            } else {
                uUID = null;
            }
            return uUID;
        }
    }

    private static final class BetterModelCell
    extends Cell {
        private final BetterModelMachineRenderer source;
        private DummyTracker mirrorTracker;
        private String mirrorAnim;

        BetterModelCell(BetterModelMachineRenderer source, BlockPos local) {
            super(local);
            this.source = source;
        }

        @Override
        Vector3f offset() {
            return new Vector3f(0.5f, 0.0f, 0.5f);
        }

        @Override
        float baseYaw() {
            return 0.0f;
        }

        @Override
        float basePitch() {
            return 0.0f;
        }

        @Override
        Quaternionf modelRotation(ContraptionLevel level) {
            return null;
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
        }

        @Override
        void updateMetadata(Player player) {
        }

        @Override
        void despawn(Player player) {
            Object pp = player.platformPlayer();
            if (pp instanceof org.bukkit.entity.Player) {
                org.bukkit.entity.Player b = (org.bukkit.entity.Player)pp;
                if (this.mirrorTracker != null) {
                    try {
                        this.mirrorTracker.remove(BukkitAdapter.adapt((org.bukkit.entity.Player)b));
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
        }

        @Override
        void render(List<Player> viewers, ContraptionLevel level, int blockLight, int skyLight, double scale) {
            if (!BetterModelMachineRenderer.available()) {
                this.closeTracker();
                return;
            }
            if (!this.source.isShown()) {
                this.closeTracker();
                return;
            }
            World bukkitWorld = level.getWorld();
            Vec3 localCenter = new Vec3((double)this.local.getX() + 0.5, (double)this.local.getY(), (double)this.local.getZ() + 0.5);
            Vec3 realPos = level.realWorldPositionOf(localCenter);
            float yawDegrees = (float)Math.toDegrees(level.realYawRadians());
            Location loc = new Location(bukkitWorld, realPos.x, realPos.y, realPos.z, yawDegrees, 0.0f);
            try {
                if (this.mirrorTracker == null || this.mirrorTracker.isClosed()) {
                    this.mirrorAnim = null;
                    this.mirrorTracker = BetterModel.model((String)this.source.modelId()).map(r -> r.create(BukkitAdapter.adapt((Location)loc))).orElse(null);
                } else {
                    this.mirrorTracker.location(BukkitAdapter.adapt((Location)loc));
                }
            }
            catch (Throwable ignored) {
                this.mirrorTracker = null;
                return;
            }
            if (this.mirrorTracker == null) {
                return;
            }
            String wantAnim = this.source.currentAnimation();
            if (wantAnim != null && !wantAnim.equals(this.mirrorAnim)) {
                try {
                    this.mirrorTracker.animate(wantAnim, new AnimationModifier(0, 0, AnimationIterator.Type.LOOP, () -> 1.0f));
                    this.mirrorAnim = wantAnim;
                }
                catch (Throwable throwable) {}
            } else if (wantAnim == null && this.mirrorAnim != null) {
                try {
                    this.mirrorTracker.stopAnimation(this.mirrorAnim);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                this.mirrorAnim = null;
            }
            HashSet<UUID> current = new HashSet<UUID>();
            for (Player p : viewers) {
                UUID id = BetterModelCell.uuidOf(p);
                if (id == null) continue;
                current.add(id);
                Object pp = p.platformPlayer();
                if (!(pp instanceof org.bukkit.entity.Player)) continue;
                org.bukkit.entity.Player b = (org.bukkit.entity.Player)pp;
                try {
                    PlatformPlayer bp = BukkitAdapter.adapt((org.bukkit.entity.Player)b);
                    if (!this.mirrorTracker.isSpawned(bp)) {
                        this.mirrorTracker.spawn(bp);
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                this.shownTo.add(id);
            }
            this.shownTo.retainAll(current);
        }

        private void closeTracker() {
            if (this.mirrorTracker == null) {
                return;
            }
            try {
                this.mirrorTracker.close();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.mirrorTracker = null;
            this.mirrorAnim = null;
        }

        @Override
        void despawnAll(List<Player> viewers) {
            this.closeTracker();
            this.shownTo.clear();
        }
    }

    private static final class MachineItemDisplayCell
    extends Cell {
        private final RendererSpec.ItemDisplaySpec spec;
        private final RendererManager manager;
        private final int specIndex;
        private final int entityId;
        private final UUID entityUuid;
        private final Object despawnPacket;
        private ItemStack lastSentItem;

        MachineItemDisplayCell(RendererSpec.ItemDisplaySpec spec, RendererManager manager, int specIndex, BlockPos local) {
            super(local);
            this.spec = spec;
            this.manager = manager;
            this.specIndex = specIndex;
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        @Override
        Vector3f offset() {
            String locExpr = this.spec.locationExpr();
            if (locExpr != null && !locExpr.isEmpty()) {
                try {
                    PolyValue.Array a;
                    PolyValue val = PolyFormula.compile(locExpr).evaluate(PolyContext.builder().build());
                    if (val instanceof PolyValue.Array && (a = (PolyValue.Array)val).elements().size() >= 3) {
                        return new Vector3f((float)a.elements().get(0).asNum() + 0.5f, (float)a.elements().get(1).asNum(), (float)a.elements().get(2).asNum() + 0.5f);
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return new Vector3f(0.5f, 0.0f, 0.5f);
        }

        @Override
        float baseYaw() {
            String locExpr = this.spec.locationExpr();
            if (locExpr != null && !locExpr.isEmpty()) {
                try {
                    PolyValue val = PolyFormula.compile(locExpr).evaluate(PolyContext.builder().build());
                    if (val instanceof PolyValue.Array) {
                        PolyValue.Array a = (PolyValue.Array)val;
                        if (a.elements().size() == 4) {
                            return (float)a.elements().get(3).asNum();
                        }
                        if (a.elements().size() >= 5) {
                            return (float)a.elements().get(4).asNum();
                        }
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return MachineItemDisplayCell.ef(this.spec.rotY(), 0.0f);
        }

        @Override
        float basePitch() {
            String locExpr = this.spec.locationExpr();
            if (locExpr != null && !locExpr.isEmpty()) {
                try {
                    PolyValue.Array a;
                    PolyValue val = PolyFormula.compile(locExpr).evaluate(PolyContext.builder().build());
                    if (val instanceof PolyValue.Array && (a = (PolyValue.Array)val).elements().size() >= 5) {
                        return (float)a.elements().get(3).asNum();
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return MachineItemDisplayCell.ef(this.spec.rotX(), 0.0f);
        }

        private static float ef(String expr, float def) {
            if (expr == null || expr.isEmpty()) {
                return def;
            }
            try {
                return Float.parseFloat(expr.trim());
            }
            catch (NumberFormatException numberFormatException) {
                try {
                    return (float)PolyFormula.compile(expr).evaluateNum(PolyContext.builder().build());
                }
                catch (Throwable ignored) {
                    return def;
                }
            }
        }

        @Override
        Quaternionf modelRotation(ContraptionLevel level) {
            return null;
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, realPos.x, realPos.y, realPos.z, MachineItemDisplayCell.ef(this.spec.rotX(), 0.0f), yawDegrees, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMeta());
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, realPos.x, realPos.y, realPos.z, yawDegrees, MachineItemDisplayCell.ef(this.spec.rotX(), 0.0f), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMeta()), false);
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(this.despawnPacket, false);
        }

        @Override
        void render(List<Player> viewers, ContraptionLevel level, int blockLight, int skyLight, double scale) {
            ItemStack currentItem = this.manager.currentItems()[this.specIndex];
            boolean itemChanged = currentItem != this.lastSentItem;
            this.lastSentItem = currentItem;
            super.render(viewers, level, blockLight, skyLight, scale);
            if (itemChanged && !this.shownTo.isEmpty()) {
                List<Object> meta = this.buildMeta();
                for (Player p : viewers) {
                    UUID id = MachineItemDisplayCell.uuidOf(p);
                    if (id == null || !this.shownTo.contains(id)) continue;
                    p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, meta), false);
                }
            }
        }

        private List<Object> buildMeta() {
            byte bb;
            ItemStack item = this.manager.currentItems()[this.specIndex];
            ArrayList<Object> values = new ArrayList<Object>();
            if (item != null && !item.isEmpty()) {
                DisplayData.ItemDisplayData.ItemStack.addEntityData(item, values);
            }
            float s = MachineItemDisplayCell.ef(this.spec.scale(), 1.0f);
            DisplayData.Scale.addEntityData(new Vector3f(s, s, s), values);
            float rx = MachineItemDisplayCell.ef(this.spec.rotX(), 0.0f);
            float ry = MachineItemDisplayCell.ef(this.spec.rotY(), 0.0f);
            float rz = MachineItemDisplayCell.ef(this.spec.rotZ(), 0.0f);
            if (rx != 0.0f || ry != 0.0f || rz != 0.0f) {
                Quaternionf q = new Quaternionf().rotateY((float)Math.toRadians(ry)).rotateX((float)Math.toRadians(rx)).rotateZ((float)Math.toRadians(rz));
                DisplayData.LeftRotation.addEntityData(q, values);
            }
            if ((bb = MachineItemDisplayCell.billboardByte(this.spec.billboard())) != 0) {
                DisplayData.BillboardConstraints.addEntityData(bb, values);
            }
            DisplayData.BrightnessOverride.addEntityData((this.blockLight() << 4 | this.skyLight() << 20), values);
            ContraptionBlockEntityElementMirror.addInterpolationTuning(values);
            return values;
        }

        private static byte billboardByte(String mode) {
            if (mode == null) {
                return 0;
            }
            return switch (mode) {
                case "vertical" -> 1;
                case "horizontal" -> 2;
                case "center" -> 3;
                default -> 0;
            };
        }
    }

    private static final class ItemDisplayCell
    extends Cell {
        private final ItemDisplayBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        ItemDisplayCell(ItemDisplayBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        Vector3f offset() {
            return this.element.config.position();
        }

        @Override
        float baseYaw() {
            return this.element.config.yRot();
        }

        @Override
        float basePitch() {
            return this.element.config.xRot();
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.element.entityId, this.uuid, realPos.x, realPos.y, realPos.z, this.element.config.xRot(), yawDegrees, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.element.entityId, realPos.x, realPos.y, realPos.z, yawDegrees, this.element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.metadata(player)), false);
        }

        private List<Object> metadata(Player player) {
            ArrayList<Object> values = new ArrayList<Object>(this.element.config.metadataValues(player, this.element.tintSource));
            ContraptionRenderScale.applyTo(values, this.scale(), this.rotation());
            DisplayData.BrightnessOverride.addEntityData((this.blockLight() << 4 | this.skyLight() << 20), values);
            ContraptionBlockEntityElementMirror.addInterpolationTuning(values);
            return values;
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(this.element.cachedDespawnPacket, false);
        }
    }

    private static final class TextDisplayCell
    extends Cell {
        private final TextDisplayBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        TextDisplayCell(TextDisplayBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        Vector3f offset() {
            return this.element.config.position();
        }

        @Override
        float baseYaw() {
            return this.element.config.yRot();
        }

        @Override
        float basePitch() {
            return this.element.config.xRot();
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.element.entityId, this.uuid, realPos.x, realPos.y, realPos.z, this.element.config.xRot(), yawDegrees, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.metadata(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.element.entityId, realPos.x, realPos.y, realPos.z, yawDegrees, this.element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.metadata(player)), false);
        }

        private List<Object> metadata(Player player) {
            ArrayList<Object> values = new ArrayList<Object>(this.element.config.metadataValues(player));
            ContraptionRenderScale.applyTo(values, this.scale(), this.rotation());
            DisplayData.BrightnessOverride.addEntityData((this.blockLight() << 4 | this.skyLight() << 20), values);
            ContraptionBlockEntityElementMirror.addInterpolationTuning(values);
            return values;
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(this.element.cachedDespawnPacket, false);
        }
    }

    private static final class ArmorStandCell
    extends Cell {
        private final ArmorStandBlockEntityElement element;
        private final UUID uuid = UUID.randomUUID();

        ArmorStandCell(ArmorStandBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        Vector3f offset() {
            return this.element.config.position();
        }

        @Override
        float baseYaw() {
            return this.element.config.yRot();
        }

        @Override
        float basePitch() {
            return this.element.config.xRot();
        }

        @Override
        Quaternionf modelRotation(ContraptionLevel level) {
            return null;
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.element.entityId, this.uuid, realPos.x, realPos.y, realPos.z, this.element.config.xRot(), yawDegrees, EntityType.ARMOR_STAND, 0, Vec3.ZERO, yawDegrees);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.element.config.metadataValues(player));
            player.sendPackets(List.of(add, data), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.element.entityId, realPos.x, realPos.y, realPos.z, yawDegrees, this.element.config.xRot(), false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId, this.element.config.metadataValues(player)), false);
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(this.element.cachedDespawnPacket, false);
        }
    }

    private static final class ItemRideCell
    extends Cell {
        private final ItemBlockEntityElement element;
        private final UUID uuid1 = UUID.randomUUID();
        private final UUID uuid2 = UUID.randomUUID();

        ItemRideCell(ItemBlockEntityElement element, BlockPos local) {
            super(local);
            this.element = element;
        }

        @Override
        Vector3f offset() {
            return this.element.config.position();
        }

        @Override
        float baseYaw() {
            return 0.0f;
        }

        @Override
        float basePitch() {
            return 0.0f;
        }

        @Override
        Quaternionf modelRotation(ContraptionLevel level) {
            return null;
        }

        @Override
        void spawn(Player player, Vec3 realPos, float yawDegrees) {
            Object add1 = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.element.entityId1, this.uuid1, realPos.x, realPos.y, realPos.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            ArrayList<Object> display1Values = new ArrayList<Object>();
            ContraptionBlockEntityElementMirror.addInterpolationTuning(display1Values);
            Object data1 = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId1, display1Values);
            Object add2 = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.element.entityId2, this.uuid2, realPos.x, realPos.y, realPos.z, 0.0f, 0.0f, EntityType.ITEM, 0, Vec3.ZERO, 0.0);
            Object ride = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId2, this.element.config.metadataValues(player, this.element.tintSource));
            player.sendPackets(List.of(add1, data1, add2, this.element.cachedRidePacket, ride), false);
        }

        @Override
        void updatePosition(Player player, Vec3 realPos, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.element.entityId1, realPos.x, realPos.y, realPos.z, 0.0f, 0.0f, false), false);
        }

        @Override
        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.element.entityId2, this.element.config.metadataValues(player, this.element.tintSource)), false);
        }

        @Override
        void despawn(Player player) {
            player.sendPacket(this.element.cachedDespawnPacket, false);
        }
    }
}

