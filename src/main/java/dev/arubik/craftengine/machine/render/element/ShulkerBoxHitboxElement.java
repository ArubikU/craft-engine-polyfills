package dev.arubik.craftengine.machine.render.element;

import it.unimi.dsi.fastutil.ints.IntList;
import net.momirealms.craftengine.core.block.entity.render.element.ConstantBlockEntityElement;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.world.BlockPos;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ShulkerBoxHitboxElement implements ConstantBlockEntityElement {
    public final ShulkerBoxHitboxElementConfig config;
    public final Object cachedSpawnPacket;
    public final Object cachedDespawnPacket;
    public final Object cachedUpdatePosPacket;
    public final int entityId;

    public ShulkerBoxHitboxElement(ShulkerBoxHitboxElementConfig config, BlockPos pos) {
        this(config, pos, nextEntityId(), false);
    }

    public ShulkerBoxHitboxElement(ShulkerBoxHitboxElementConfig config, BlockPos pos, int entityId,
            boolean posChanged) {
        Vector3f position = config.position();
        this.cachedSpawnPacket = dev.arubik.craftengine.util.MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                entityId, UUID.randomUUID(), pos.x() + position.x, pos.y() + position.y, pos.z() + position.z,
                config.xRot(), config.yRot(), net.minecraft.world.entity.EntityType.SHULKER, 0,
                net.minecraft.world.phys.Vec3.ZERO, 0);
        this.config = config;
        this.cachedDespawnPacket = dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        this.entityId = entityId;
        this.cachedUpdatePosPacket = posChanged
                ? dev.arubik.craftengine.util.MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                        this.entityId, pos.x() + position.x,
                        pos.y() + position.y, pos.z() + position.z, config.yRot(), config.xRot(), false)
                : null;
    }

    // net.minecraft Entity.ENTITY_COUNTER is private; access it reflectively to
    // obtain a fresh, server-unique fake entity id.
    private static final AtomicInteger ENTITY_COUNTER;
    static {
        AtomicInteger counter;
        try {
            Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }

    @Override
    public void hide(Player player) {
        player.sendPacket(this.cachedDespawnPacket, false);
    }

    @Override
    public void showInternal(Player player) {
        player.sendPackets(
                List.of(this.cachedSpawnPacket, dev.arubik.craftengine.util.MNms.INSTANCE
                        .constructor$ClientboundSetEntityDataPacket(this.entityId, this.config.metadataValues(player))),
                false);
    }

    @Override
    public boolean supportsTransform() {
        return true;
    }

    @Override
    public void update(Player player) {
        if (this.cachedUpdatePosPacket != null) {
            player.sendPackets(List.of(this.cachedUpdatePosPacket, dev.arubik.craftengine.util.MNms.INSTANCE
                    .constructor$ClientboundSetEntityDataPacket(this.entityId, this.config.metadataValues(player))),
                    false);
        } else {
            player.sendPacket(dev.arubik.craftengine.util.MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                    this.entityId,
                    this.config.metadataValues(player)), false);
        }
    }

    public int entityId() {
        return entityId;
    }
}
