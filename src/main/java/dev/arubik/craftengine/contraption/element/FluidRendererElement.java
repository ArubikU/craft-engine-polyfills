/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$ItemDisplayData
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class FluidRendererElement
implements ContraptionElement {
    private final Vec3 localOffset;
    private final Key fluidTypeId;
    private final int fluidLevel;
    private final float scaleX;
    private final float scaleY;
    private final float scaleZ;
    private final int entityId;
    private final UUID entityUuid;
    private final Object despawnPacket;
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private boolean metaDirty = true;
    private double lastScale = 1.0;
    private int lastBlockLight = -1;
    private int lastSkyLight = -1;
    private int interpTicks = 2;

    public FluidRendererElement(Vec3 localOffset, Key fluidTypeId, int fluidLevel, float scaleX, float scaleY, float scaleZ) {
        this.localOffset = localOffset;
        this.fluidTypeId = fluidTypeId;
        this.fluidLevel = fluidLevel;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.entityId = Entity.nextEntityId();
        this.entityUuid = UUID.randomUUID();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
    }

    @Override
    public Key type() {
        return ElementTypes.FLUID;
    }

    @Override
    public Vec3 localOffset() {
        return this.localOffset;
    }

    @Override
    public boolean isValid() {
        return this.fluidTypeId != null && this.fluidLevel >= 0;
    }

    @Override
    public int[] entityIds() {
        return new int[]{this.entityId};
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 worldPos = ContraptionMath.renderPosition(this.localOffset, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        boolean scaleChanged = ctx.scale() != this.lastScale;
        this.lastScale = ctx.scale();
        int blockLight = 15;
        int skyLight = 15;
        if (ctx.realLevel() != null) {
            try {
                BlockPos worldAt = BlockPos.containing((double)worldPos.x, (double)worldPos.y, (double)worldPos.z);
                blockLight = ctx.realLevel().getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(worldAt);
                skyLight = ctx.realLevel().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(worldAt);
            }
            catch (Throwable worldAt) {
                // empty catch block
            }
        }
        BlockPos localPos = BlockPos.containing((double)this.localOffset.x, (double)this.localOffset.y, (double)this.localOffset.z);
        if (ctx.lightMap() != null) {
            blockLight = ctx.lightMap().combinedBlockLight(localPos, blockLight);
        }
        if (blockLight != this.lastBlockLight || skyLight != this.lastSkyLight) {
            this.lastBlockLight = blockLight;
            this.lastSkyLight = skyLight;
            this.metaDirty = true;
        }
        if (ctx.interpTicks() != this.interpTicks) {
            this.interpTicks = ctx.interpTicks();
            this.metaDirty = true;
        }
        Quaternionf rotation = this.buildRotation(ctx);
        for (Player viewer : ctx.viewers()) {
            UUID viewerId = viewer.uuid();
            if (!this.shownTo.contains(viewerId)) {
                this.spawn(viewer, worldPos, rotation);
                this.shownTo.add(viewerId);
                continue;
            }
            if (!ctx.moved() && !this.metaDirty && !scaleChanged) continue;
            this.sendPositionSync(viewer, worldPos);
            if (!this.metaDirty && !scaleChanged) continue;
            viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMetadata(rotation)), false);
        }
        this.metaDirty = false;
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Player viewer : viewers) {
            if (!this.shownTo.remove(viewer.uuid())) continue;
            viewer.sendPacket(this.despawnPacket, false);
        }
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
    }

    private void spawn(Player viewer, Vec3 pos, Quaternionf rotation) {
        Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.entityUuid, pos.x, pos.y, pos.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
        Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.buildMetadata(rotation));
        viewer.sendPackets(List.of(add, data), false);
    }

    private void sendPositionSync(Player viewer, Vec3 pos) {
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x, pos.y, pos.z, 0.0f, 0.0f, false), false);
    }

    private List<Object> buildMetadata(Quaternionf rotation) {
        ArrayList<Object> values = new ArrayList<Object>();
        Object nmsItem = this.resolveFluidItem();
        if (nmsItem != null) {
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItem, values);
        }
        float s = (float)this.lastScale;
        DisplayData.Scale.addEntityData(new Vector3f(this.scaleX * s, this.scaleY * s, this.scaleZ * s), values);
        if (rotation != null) {
            DisplayData.LeftRotation.addEntityData(rotation, values);
        }
        int bl = this.lastBlockLight >= 0 ? this.lastBlockLight : 15;
        int sl = this.lastSkyLight >= 0 ? this.lastSkyLight : 15;
        DisplayData.BrightnessOverride.addEntityData((bl << 4 | sl << 20), values);
        DisplayData.PosRotInterpolationDuration.addEntityData(this.interpTicks, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(this.interpTicks, values);
        return values;
    }

    private Quaternionf buildRotation(RenderContext ctx) {
        if (ctx.pitchRadians() == 0.0 && ctx.rollRadians() == 0.0 && ctx.yawRadians() == 0.0) {
            return null;
        }
        return new Quaternionf().rotateY((float)(-ctx.yawRadians())).rotateX((float)ctx.pitchRadians()).rotateZ((float)ctx.rollRadians());
    }

    private Object resolveFluidItem() {
        try {
            ItemStack bukkit;
            BukkitItemDefinition def = CraftEngineItems.byId((Key)Key.of((String)"cml", (String)("fluidlvl_" + this.fluidTypeId.value() + "_" + this.fluidLevel)));
            if (def != null && (bukkit = def.buildBukkitItem()) != null) {
                return CraftItemStack.asNMSCopy((ItemStack)bukkit);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }
}

