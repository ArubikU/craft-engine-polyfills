/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.SynchedEntityData$DataValue
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Display$TextDisplay
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$ItemDisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$TextDisplayData
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.plugin.CraftEngine
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  org.bukkit.Particle
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.machine.render.ModelRendersDriven;
import dev.arubik.craftengine.machine.render.ParticleUtils;
import dev.arubik.craftengine.machine.render.RendererManager;
import dev.arubik.craftengine.machine.render.RendererSpec;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ContraptionMachineRendererElement
implements ContraptionElement {
    private final BlockPos localPos;
    private RendererManager lastManager;
    private final Map<Integer, ItemCell> itemCells = new LinkedHashMap<Integer, ItemCell>();
    private final Map<Integer, TextCell> textCells = new LinkedHashMap<Integer, TextCell>();
    private final Map<Integer, FluidCell> fluidCells = new LinkedHashMap<Integer, FluidCell>();

    public ContraptionMachineRendererElement(BlockPos localPos) {
        this.localPos = localPos;
    }

    public BlockPos localPos() {
        return this.localPos;
    }

    @Override
    public Key type() {
        return ElementTypes.MACHINE_RENDERER;
    }

    @Override
    public Vec3 localOffset() {
        return new Vec3((double)this.localPos.getX() + 0.5, (double)this.localPos.getY() + 0.5, (double)this.localPos.getZ() + 0.5);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int[] entityIds() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (ItemCell itemCell : this.itemCells.values()) {
            ids.add(itemCell.entityId);
        }
        for (TextCell textCell : this.textCells.values()) {
            ids.add(textCell.entityId);
        }
        for (FluidCell fluidCell : this.fluidCells.values()) {
            ids.add(fluidCell.entityId);
        }
        return ids.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public void tick(RenderContext ctx) {
        RendererManager mgr = ContraptionMachineRendererElement.resolveManager(ctx.level(), this.localPos);
        if (mgr == this.lastManager) {
            return;
        }
        this.despawnAllCells(ctx.viewers());
        this.itemCells.clear();
        this.textCells.clear();
        this.fluidCells.clear();
        this.lastManager = mgr;
        if (mgr == null) {
            return;
        }
        List<RendererSpec> specs = mgr.specs();
        for (int i = 0; i < specs.size(); ++i) {
            RendererSpec spec = specs.get(i);
            if (spec instanceof RendererSpec.ItemDisplaySpec) {
                this.itemCells.put(i, new ItemCell(i));
                continue;
            }
            if (spec instanceof RendererSpec.TextDisplaySpec) {
                this.textCells.put(i, new TextCell(i));
                continue;
            }
            if (!(spec instanceof RendererSpec.FluidTankSpec)) continue;
            this.fluidCells.put(i, new FluidCell(i));
        }
    }

    @Override
    public void render(RenderContext ctx) {
        if (this.lastManager == null) {
            return;
        }
        List<RendererSpec> specs = this.lastManager.specs();
        int blockLight = 15;
        int skyLight = 15;
        if (ctx.realLevel() != null) {
            try {
                Vec3 worldCenter = ContraptionMath.renderPosition(new Vec3((double)this.localPos.getX() + 0.5, (double)this.localPos.getY() + 0.5, (double)this.localPos.getZ() + 0.5), ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
                BlockPos at = BlockPos.containing((double)worldCenter.x, (double)worldCenter.y, (double)worldCenter.z);
                blockLight = ctx.realLevel().getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(at);
                skyLight = ctx.realLevel().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(at);
            }
            catch (Throwable worldCenter) {
                // empty catch block
            }
        }
        if (ctx.lightMap() != null) {
            blockLight = ctx.lightMap().combinedBlockLight(this.localPos, blockLight);
        }
        for (int i = 0; i < specs.size(); ++i) {
            Object cell;
            RendererSpec spec = specs.get(i);
            RendererManager.EvalResult er = this.lastManager.evalResult(i);
            if (er == null) continue;
            if (spec instanceof RendererSpec.ItemDisplaySpec) {
                RendererSpec.ItemDisplaySpec idSpec = (RendererSpec.ItemDisplaySpec)spec;
                cell = this.itemCells.get(i);
                if (cell == null) continue;
                ((ItemCell)cell).render(ctx, er, idSpec, blockLight, skyLight);
                continue;
            }
            if (spec instanceof RendererSpec.TextDisplaySpec) {
                RendererSpec.TextDisplaySpec tdSpec = (RendererSpec.TextDisplaySpec)spec;
                cell = this.textCells.get(i);
                if (cell == null) continue;
                ((TextCell)cell).render(ctx, er, tdSpec, blockLight, skyLight);
                continue;
            }
            if (spec instanceof RendererSpec.FluidTankSpec) {
                RendererSpec.FluidTankSpec ftSpec = (RendererSpec.FluidTankSpec)spec;
                cell = this.fluidCells.get(i);
                if (cell == null) continue;
                ((FluidCell)cell).render(ctx, er, ftSpec, blockLight, skyLight);
                continue;
            }
            if (!(spec instanceof RendererSpec.ParticleSpec)) continue;
            RendererSpec.ParticleSpec psSpec = (RendererSpec.ParticleSpec)spec;
            if (!er.active || !er.emittedThisTick || ctx.realLevel() == null) continue;
            this.emitParticles(ctx, er, psSpec);
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        this.despawnAllCells(viewers);
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
    }

    private void despawnAllCells(List<Player> viewers) {
        for (ItemCell itemCell : this.itemCells.values()) {
            itemCell.despawnAll(viewers);
        }
        for (TextCell textCell : this.textCells.values()) {
            textCell.despawnAll(viewers);
        }
        for (FluidCell fluidCell : this.fluidCells.values()) {
            fluidCell.despawnAll(viewers);
        }
    }

    private static RendererManager resolveManager(ContraptionLevel level, BlockPos local) {
        if (level == null) {
            return null;
        }
        try {
            BlockEntityController blockEntityController;
            CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(level.getWorld().getUID());
            if (ceWorld == null) {
                return null;
            }
            net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(local.getX(), local.getY(), local.getZ());
            BlockEntity be = ceWorld.getBlockEntityAtIfLoaded(cePos, false);
            if (be != null && (blockEntityController = be.controller) instanceof ModelRendersDriven) {
                ModelRendersDriven mrd = (ModelRendersDriven)blockEntityController;
                return mrd.rendererManager();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private void emitParticles(RenderContext ctx, RendererManager.EvalResult er, RendererSpec.ParticleSpec spec) {
        try {
            Vec3 localWithOffset = new Vec3((double)this.localPos.getX() + 0.5 + er.specRelX, (double)this.localPos.getY() + er.specRelY, (double)this.localPos.getZ() + 0.5 + er.specRelZ);
            Vec3 worldPos = ContraptionMath.renderPosition(localWithOffset, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            CraftWorld bworld = ctx.realLevel().getWorld();
            Particle particle = Particle.valueOf((String)er.pType.toUpperCase(Locale.ROOT));
            ParticleUtils.emit((World)bworld, particle, worldPos.x, worldPos.y, worldPos.z, er.pSpreadX, er.pSpreadY, er.pSpreadZ, er.pShape, er.pDir, er.particleCount, er.pSpeed, er.pDvx, er.pDvy, er.pDvz);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void addInterpolation(List<Object> values) {
        DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
    }

    private static byte itemBillboardByte(String mode) {
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

    private final class ItemCell {
        final int specIndex;
        final int entityId;
        final UUID entityUuid;
        final Object despawnPacket;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        net.minecraft.world.item.ItemStack lastItem;
        int lastBlockLight = -1;
        int lastSkyLight = -1;
        double lastScale = 1.0;

        ItemCell(int specIndex) {
            this.specIndex = specIndex;
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        void render(RenderContext ctx, RendererManager.EvalResult er, RendererSpec.ItemDisplaySpec spec, int blockLight, int skyLight) {
            if (!er.active || er.itemDisplay == null) {
                this.despawnAll(ctx.viewers());
                this.shownTo.clear();
                return;
            }
            RendererSpec.EvaluatedItemDisplay ev = er.itemDisplay;
            Vec3 localWithOffset = new Vec3((double)ContraptionMachineRendererElement.this.localPos.getX() + 0.5 + ev.offsetX(), (double)ContraptionMachineRendererElement.this.localPos.getY() + ev.offsetY(), (double)ContraptionMachineRendererElement.this.localPos.getZ() + 0.5 + ev.offsetZ());
            Vec3 worldPos = ContraptionMath.renderPosition(localWithOffset, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            float yawDeg = ev.rotY() + (float)ctx.yawDegrees();
            boolean itemChanged = ev.item() != this.lastItem;
            boolean metaChanged = itemChanged || blockLight != this.lastBlockLight || skyLight != this.lastSkyLight || ctx.scale() != this.lastScale;
            this.lastItem = ev.item();
            this.lastBlockLight = blockLight;
            this.lastSkyLight = skyLight;
            this.lastScale = ctx.scale();
            HashSet<UUID> current = new HashSet<UUID>();
            for (Player p : ctx.viewers()) {
                UUID id = p.uuid();
                if (id == null) continue;
                current.add(id);
                if (this.shownTo.add(id)) {
                    this.spawnFor(p, worldPos, yawDeg, ev, blockLight, skyLight, spec);
                    continue;
                }
                if (ctx.moved()) {
                    this.updatePositionFor(p, worldPos, yawDeg, ev);
                    if (!metaChanged) continue;
                    this.updateMetaFor(p, ev, blockLight, skyLight, spec);
                    continue;
                }
                if (!metaChanged) continue;
                this.updateMetaFor(p, ev, blockLight, skyLight, spec);
            }
            this.shownTo.retainAll(current);
        }

        private void spawnFor(Player p, Vec3 pos, float yawDeg, RendererSpec.EvaluatedItemDisplay ev, int bl, int sl, RendererSpec.ItemDisplaySpec spec) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, pos.x, pos.y, pos.z, ev.rotX(), yawDeg, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMeta(ev, bl, sl, spec));
            p.sendPackets(List.of(add, data), false);
        }

        private void updatePositionFor(Player p, Vec3 pos, float yawDeg, RendererSpec.EvaluatedItemDisplay ev) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, yawDeg, ev.rotX(), false), false);
        }

        private void updateMetaFor(Player p, RendererSpec.EvaluatedItemDisplay ev, int bl, int sl, RendererSpec.ItemDisplaySpec spec) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMeta(ev, bl, sl, spec)), false);
        }

        private List<Object> buildMeta(RendererSpec.EvaluatedItemDisplay ev, int bl, int sl, RendererSpec.ItemDisplaySpec spec) {
            byte bb;
            ArrayList<Object> values = new ArrayList<Object>();
            if (ev.item() != null && !ev.item().isEmpty()) {
                DisplayData.ItemDisplayData.ItemStack.addEntityData(ev.item(), values);
            }
            float s = ev.scale();
            DisplayData.Scale.addEntityData(new Vector3f(s, s, s), values);
            float rx = ev.rotX();
            float ry = ev.rotY();
            float rz = ev.rotZ();
            if (rx != 0.0f || ry != 0.0f || rz != 0.0f) {
                Quaternionf q = new Quaternionf().rotateY((float)Math.toRadians(ry)).rotateX((float)Math.toRadians(rx)).rotateZ((float)Math.toRadians(rz));
                DisplayData.LeftRotation.addEntityData(q, values);
            }
            if ((bb = ContraptionMachineRendererElement.itemBillboardByte(spec.billboard())) != 0) {
                DisplayData.BillboardConstraints.addEntityData(bb, values);
            }
            DisplayData.BrightnessOverride.addEntityData((bl << 4 | sl << 20), values);
            ContraptionMachineRendererElement.addInterpolation(values);
            return values;
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                if (!this.shownTo.remove(p.uuid())) continue;
                p.sendPacket(this.despawnPacket, false);
            }
        }
    }

    private final class TextCell {
        final int specIndex;
        final int entityId;
        final UUID entityUuid;
        final Object despawnPacket;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        String lastText;
        float lastScale = -1.0f;
        int lastBlockLight = -1;
        int lastSkyLight = -1;
        double lastScale3d = 1.0;

        TextCell(int specIndex) {
            this.specIndex = specIndex;
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        void render(RenderContext ctx, RendererManager.EvalResult er, RendererSpec.TextDisplaySpec spec, int blockLight, int skyLight) {
            if (!er.active) {
                this.despawnAll(ctx.viewers());
                this.shownTo.clear();
                return;
            }
            Vec3 localWithOffset = new Vec3((double)ContraptionMachineRendererElement.this.localPos.getX() + 0.5 + er.tdOx, (double)ContraptionMachineRendererElement.this.localPos.getY() + er.tdOy, (double)ContraptionMachineRendererElement.this.localPos.getZ() + 0.5 + er.tdOz);
            Vec3 worldPos = ContraptionMath.renderPosition(localWithOffset, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            float yawDeg = (float)ctx.yawDegrees();
            boolean metaChanged = !Objects.equals(er.textContent, this.lastText) || Float.compare(er.textScale, this.lastScale) != 0 || blockLight != this.lastBlockLight || skyLight != this.lastSkyLight || ctx.scale() != this.lastScale3d;
            this.lastText = er.textContent;
            this.lastScale = er.textScale;
            this.lastBlockLight = blockLight;
            this.lastSkyLight = skyLight;
            this.lastScale3d = ctx.scale();
            HashSet<UUID> current = new HashSet<UUID>();
            for (Player p : ctx.viewers()) {
                UUID id = p.uuid();
                if (id == null) continue;
                current.add(id);
                if (this.shownTo.add(id)) {
                    this.spawnFor(p, worldPos, yawDeg, er, spec, blockLight, skyLight);
                    continue;
                }
                if (ctx.moved()) {
                    this.updatePositionFor(p, worldPos, yawDeg);
                    if (!metaChanged) continue;
                    this.updateMetaFor(p, er, spec, blockLight, skyLight);
                    continue;
                }
                if (!metaChanged) continue;
                this.updateMetaFor(p, er, spec, blockLight, skyLight);
            }
            this.shownTo.retainAll(current);
        }

        private void spawnFor(Player p, Vec3 pos, float yawDeg, RendererManager.EvalResult er, RendererSpec.TextDisplaySpec spec, int bl, int sl) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, pos.x, pos.y, pos.z, 0.0f, yawDeg, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMeta(er, spec, bl, sl));
            p.sendPackets(List.of(add, data), false);
        }

        private void updatePositionFor(Player p, Vec3 pos, float yawDeg) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, yawDeg, 0.0f, false), false);
        }

        private void updateMetaFor(Player p, RendererManager.EvalResult er, RendererSpec.TextDisplaySpec spec, int bl, int sl) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMeta(er, spec, bl, sl)), false);
        }

        private List<Object> buildMeta(RendererManager.EvalResult er, RendererSpec.TextDisplaySpec spec, int bl, int sl) {
            EntityDataAccessor acc;
            ArrayList<Object> values = new ArrayList<Object>();
            try {
                DisplayData.TextDisplayData.Text.addEntityData(Component.literal((String)(er.textContent != null ? er.textContent : "")), values);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            DisplayData.Scale.addEntityData(new Vector3f(er.textScale, er.textScale, er.textScale), values);
            try {
                byte bb = TextCell.textBillboardByte(spec.billboard());
                DisplayData.BillboardConstraints.addEntityData(bb, values);
            }
            catch (Throwable bb) {
                // empty catch block
            }
            try {
                int bgColor = TextCell.parseColor(spec.backgroundExpr());
                DisplayData.TextDisplayData.BackgroundColor.addEntityData(bgColor, values);
            }
            catch (Throwable bgColor) {
                // empty catch block
            }
            try {
                byte styleFlags = 0;
                if (spec.shadow()) {
                    styleFlags = (byte)(styleFlags | 1);
                }
                if (spec.seeThrough()) {
                    styleFlags = (byte)(styleFlags | 2);
                }
                Field sf = Display.TextDisplay.class.getDeclaredField("DATA_STYLE_FLAGS");
                sf.setAccessible(true);
                EntityDataAccessor acc2 = (EntityDataAccessor)sf.get(null);
                values.add(SynchedEntityData.DataValue.create((EntityDataAccessor)acc2, styleFlags));
            }
            catch (Throwable styleFlags) {
                // empty catch block
            }
            try {
                Field opField = Display.TextDisplay.class.getDeclaredField("DATA_TEXT_OPACITY");
                opField.setAccessible(true);
                acc = (EntityDataAccessor)opField.get(null);
                values.add(SynchedEntityData.DataValue.create((EntityDataAccessor)acc, ((byte)spec.opacity())));
            }
            catch (Throwable opField) {
                // empty catch block
            }
            try {
                Field lwField = Display.TextDisplay.class.getDeclaredField("DATA_LINE_WIDTH");
                lwField.setAccessible(true);
                acc = (EntityDataAccessor)lwField.get(null);
                values.add(SynchedEntityData.DataValue.create((EntityDataAccessor)acc, spec.lineWidth()));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            DisplayData.BrightnessOverride.addEntityData((bl << 4 | sl << 20), values);
            ContraptionMachineRendererElement.addInterpolation(values);
            return values;
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                if (!this.shownTo.remove(p.uuid())) continue;
                p.sendPacket(this.despawnPacket, false);
            }
        }

        private static byte textBillboardByte(String mode) {
            if (mode == null) {
                return 3;
            }
            return switch (mode.toLowerCase(Locale.ROOT)) {
                case "none" -> 0;
                case "vertical" -> 1;
                case "horizontal" -> 2;
                default -> 3;
            };
        }

        private static int parseColor(String expr) {
            if (expr == null || expr.equals("0") || expr.isBlank()) {
                return 0;
            }
            try {
                String s = expr.startsWith("#") ? expr.substring(1) : expr;
                return (int)Long.parseLong(s, 16);
            }
            catch (Throwable ignored) {
                return 0;
            }
        }
    }

    private final class FluidCell {
        final int specIndex;
        final int entityId;
        final UUID entityUuid;
        final Object despawnPacket;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        int lastCeLevel = -1;
        String lastFluidType;
        int lastBlockLight = -1;
        int lastSkyLight = -1;
        double lastScale = 1.0;

        FluidCell(int specIndex) {
            this.specIndex = specIndex;
            this.entityId = Entity.nextEntityId();
            this.entityUuid = UUID.randomUUID();
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        void render(RenderContext ctx, RendererManager.EvalResult er, RendererSpec.FluidTankSpec spec, int blockLight, int skyLight) {
            if (!er.active || er.ceFluidLevel <= 0) {
                this.despawnAll(ctx.viewers());
                this.shownTo.clear();
                return;
            }
            Vec3 localWithOffset = new Vec3((double)ContraptionMachineRendererElement.this.localPos.getX() + 0.5 + er.specRelX, (double)ContraptionMachineRendererElement.this.localPos.getY() + er.specRelY, (double)ContraptionMachineRendererElement.this.localPos.getZ() + 0.5 + er.specRelZ);
            Vec3 worldPos = ContraptionMath.renderPosition(localWithOffset, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            boolean metaChanged = er.ceFluidLevel != this.lastCeLevel || !Objects.equals(er.fluidTypeValue, this.lastFluidType) || blockLight != this.lastBlockLight || skyLight != this.lastSkyLight || ctx.scale() != this.lastScale;
            this.lastCeLevel = er.ceFluidLevel;
            this.lastFluidType = er.fluidTypeValue;
            this.lastBlockLight = blockLight;
            this.lastSkyLight = skyLight;
            this.lastScale = ctx.scale();
            HashSet<UUID> current = new HashSet<UUID>();
            for (Player p : ctx.viewers()) {
                UUID id = p.uuid();
                if (id == null) continue;
                current.add(id);
                if (this.shownTo.add(id)) {
                    this.spawnFor(p, worldPos, er, spec, blockLight, skyLight);
                    continue;
                }
                if (ctx.moved()) {
                    this.updatePositionFor(p, worldPos);
                    if (!metaChanged) continue;
                    this.updateMetaFor(p, er, spec, blockLight, skyLight);
                    continue;
                }
                if (!metaChanged) continue;
                this.updateMetaFor(p, er, spec, blockLight, skyLight);
            }
            this.shownTo.retainAll(current);
        }

        private void spawnFor(Player p, Vec3 pos, RendererManager.EvalResult er, RendererSpec.FluidTankSpec spec, int bl, int sl) {
            Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, pos.x, pos.y, pos.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMeta(er, spec, bl, sl));
            p.sendPackets(List.of(add, data), false);
        }

        private void updatePositionFor(Player p, Vec3 pos) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, 0.0f, 0.0f, false), false);
        }

        private void updateMetaFor(Player p, RendererManager.EvalResult er, RendererSpec.FluidTankSpec spec, int bl, int sl) {
            p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMeta(er, spec, bl, sl)), false);
        }

        private List<Object> buildMeta(RendererManager.EvalResult er, RendererSpec.FluidTankSpec spec, int bl, int sl) {
            ArrayList<Object> values = new ArrayList<Object>();
            try {
                ItemStack bukkit;
                Key fluidKey = Key.of((String)"cml", (String)("fluidlvl_" + er.fluidTypeValue + "_" + er.ceFluidLevel));
                BukkitItemDefinition def = CraftEngineItems.byId((Key)fluidKey);
                if (def != null && (bukkit = def.buildBukkitItem()) != null) {
                    net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy((ItemStack)bukkit);
                    DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItem, values);
                }
            }
            catch (Throwable fluidKey) {
                // empty catch block
            }
            float scaleY = spec.maxHeight() * ((float)er.ceFluidLevel / 16.0f);
            DisplayData.Scale.addEntityData(new Vector3f(1.0f, scaleY, 1.0f), values);
            DisplayData.BrightnessOverride.addEntityData((bl << 4 | sl << 20), values);
            ContraptionMachineRendererElement.addInterpolation(values);
            return values;
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                if (!this.shownTo.remove(p.uuid())) continue;
                p.sendPacket(this.despawnPacket, false);
            }
        }
    }
}

