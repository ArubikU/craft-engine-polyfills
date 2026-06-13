package dev.arubik.craftengine.util;

import java.util.List;
import java.util.UUID;

import org.bukkit.craftbukkit.block.data.CraftBlockData;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.phys.Vec3;

/**
 * Direct-NMS replacement for the craft-engine reflective bridge
 * (net.momirealms...dev.arubik.craftengine.util.MNms.INSTANCE.method$/field$/constructor$),
 * which was removed in craft-engine 26.x. The project compiles against
 * Mojang-mapped NMS (paperweight userdev), so these are plain direct calls.
 * Method names intentionally mirror the old reflective members so call sites
 * only swap {@code FastNMS.INSTANCE} for {@code MNms}.
 */
public final class MNms {
    private MNms() {}

    public static final MNms INSTANCE = new MNms();

    // ---- Vec3i field accessors ----
    public int field$Vec3i$x(Object vec) { return ((Vec3i) vec).getX(); }
    public int field$Vec3i$y(Object vec) { return ((Vec3i) vec).getY(); }
    public int field$Vec3i$z(Object vec) { return ((Vec3i) vec).getZ(); }

    // ---- BlockPos constructor ----
    public Object constructor$BlockPos(int x, int y, int z) { return new BlockPos(x, y, z); }

    // ---- BlockGetter / Level state access ----
    public Object method$BlockGetter$getBlockState(Object getter, Object pos) {
        return ((BlockGetter) getter).getBlockState((BlockPos) pos);
    }

    public Object method$BlockGetter$getFluidState(Object getter, Object pos) {
        return ((BlockGetter) getter).getFluidState((BlockPos) pos);
    }

    public Object method$FluidState$getType(Object fluidState) {
        return ((FluidState) fluidState).getType();
    }

    public Object method$BlockState$getBlock(Object state) {
        return ((BlockState) state).getBlock();
    }

    /** {@code blockOrTag} may be a {@link Block} or a {@link TagKey TagKey&lt;Block&gt;}; dispatch is by runtime type. */
    @SuppressWarnings("unchecked")
    public boolean method$BlockStateBase$is(Object state, Object blockOrTag) {
        BlockState bs = (BlockState) state;
        if (blockOrTag instanceof TagKey) {
            return bs.is((TagKey<Block>) blockOrTag);
        }
        return bs.is((Block) blockOrTag);
    }

    public boolean method$BlockStateBase$isFaceSturdy(Object state, Object getter, Object pos,
            net.minecraft.core.Direction direction, SupportType supportType) {
        return ((BlockState) state).isFaceSturdy((BlockGetter) getter, (BlockPos) pos, direction, supportType);
    }

    // ---- Level writes ----
    public boolean method$LevelWriter$setBlock(Object level, Object pos, Object state, int flags) {
        return ((LevelWriter) level).setBlock((BlockPos) pos, (BlockState) state, flags);
    }

    public boolean method$LevelWriter$destroyBlock(Object level, Object pos, boolean dropBlock) {
        return ((Level) level).destroyBlock((BlockPos) pos, dropBlock);
    }

    public void method$ScheduledTickAccess$scheduleBlockTick(Object level, Object pos, Object block, int delay) {
        ((ServerLevel) level).scheduleTick((BlockPos) pos, (Block) block, delay);
    }

    // ---- Bukkit bridge ----
    public org.bukkit.World method$Level$getCraftWorld(Object level) {
        return ((ServerLevel) level).getWorld();
    }

    public org.bukkit.block.data.BlockData method$CraftBlockData$fromData(Object state) {
        return CraftBlockData.fromData((BlockState) state);
    }

    // ---- Packets ----
    public Object constructor$ClientboundAddEntityPacket(int entityId, UUID uuid, double x, double y, double z,
            float xRot, float yRot, EntityType<?> type, int data, Vec3 deltaMovement, double yHeadRot) {
        return new ClientboundAddEntityPacket(entityId, uuid, x, y, z, xRot, yRot, type, data, deltaMovement, yHeadRot);
    }

    public Object constructor$ClientboundRemoveEntitiesPacket(IntList entityIds) {
        return new ClientboundRemoveEntitiesPacket(entityIds.toIntArray());
    }

    public Object constructor$ClientboundEntityPositionSyncPacket(int entityId, double x, double y, double z,
            float yRot, float xRot, boolean onGround) {
        PositionMoveRotation pmr = new PositionMoveRotation(new Vec3(x, y, z), Vec3.ZERO, yRot, xRot);
        return new ClientboundEntityPositionSyncPacket(entityId, pmr, onGround);
    }

    @SuppressWarnings("unchecked")
    public Object constructor$ClientboundSetEntityDataPacket(int entityId, Object packedItems) {
        return new ClientboundSetEntityDataPacket(entityId,
                (List<SynchedEntityData.DataValue<?>>) packedItems);
    }
}
