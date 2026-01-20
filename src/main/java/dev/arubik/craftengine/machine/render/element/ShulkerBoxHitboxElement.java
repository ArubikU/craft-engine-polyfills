package dev.arubik.craftengine.machine.render.element;

import it.unimi.dsi.fastutil.ints.IntList;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.CoreReflections;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.MEntityTypes;
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElement;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.world.BlockPos;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class ShulkerBoxHitboxElement implements BlockEntityElement {
    public final ShulkerBoxHitboxElementConfig config;
    public final Object cachedSpawnPacket;
    public final Object cachedDespawnPacket;
    public final Object cachedUpdatePosPacket;
    public final int entityId;

    public ShulkerBoxHitboxElement(ShulkerBoxHitboxElementConfig config, BlockPos pos) {
        this(config, pos, CoreReflections.instance$Entity$ENTITY_COUNTER.incrementAndGet(), false);
    }

    public ShulkerBoxHitboxElement(ShulkerBoxHitboxElementConfig config, BlockPos pos, int entityId,
            boolean posChanged) {
        Vector3f position = config.position();
        this.cachedSpawnPacket = FastNMS.INSTANCE.constructor$ClientboundAddEntityPacket(
                entityId, UUID.randomUUID(), pos.x() + position.x, pos.y() + position.y, pos.z() + position.z,
                config.xRot(), config.yRot(), MEntityTypes.SHULKER, 0, CoreReflections.instance$Vec3$Zero, 0);
        this.config = config;
        this.cachedDespawnPacket = FastNMS.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        this.entityId = entityId;
        this.cachedUpdatePosPacket = posChanged
                ? FastNMS.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, pos.x() + position.x,
                        pos.y() + position.y, pos.z() + position.z, config.yRot(), config.xRot(), false)
                : null;
    }

    @Override
    public void hide(Player player) {
        player.sendPacket(this.cachedDespawnPacket, false);
    }

    @Override
    public void show(Player player) {
        player.sendPackets(
                List.of(this.cachedSpawnPacket, FastNMS.INSTANCE
                        .constructor$ClientboundSetEntityDataPacket(this.entityId, this.config.metadataValues(player))),
                false);
    }

    @Override
    public void transform(Player player) {
        if (this.cachedUpdatePosPacket != null) {
            player.sendPackets(List.of(this.cachedUpdatePosPacket, FastNMS.INSTANCE
                    .constructor$ClientboundSetEntityDataPacket(this.entityId, this.config.metadataValues(player))),
                    false);
        } else {
            player.sendPacket(FastNMS.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId,
                    this.config.metadataValues(player)), false);
        }
    }

    public int entityId() {
        return entityId;
    }
}
