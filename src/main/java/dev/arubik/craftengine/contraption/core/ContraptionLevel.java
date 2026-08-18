/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.entity.player.Player
 *  org.bukkit.World
 *  org.joml.Quaternionf
 */
package dev.arubik.craftengine.contraption.core;

import dev.arubik.craftengine.contraption.level.AspSupport;
import dev.arubik.craftengine.contraption.level.BukkitContraptionLevel;
import dev.arubik.craftengine.contraption.level.ContraptionBoundary;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import org.bukkit.World;
import org.joml.Quaternionf;

public interface ContraptionLevel
extends ContraptionBoundary {
    public static ContraptionLevel create(Level realLevel, double x, double y, double z, double yawRadians) {
        if (AspSupport.available()) {
            return AspSupport.create(realLevel, x, y, z, yawRadians);
        }
        return BukkitContraptionLevel.create(realLevel, x, y, z, yawRadians);
    }

    public ServerLevel serverLevel();

    public BlockState getBlockState(BlockPos var1);

    public BlockEntity getBlockEntity(BlockPos var1);

    public boolean setBlock(BlockPos var1, BlockState var2, int var3);

    public World getWorld();

    public RegistryAccess registryAccess();

    public Iterable<Entity> getAllEntities();

    public void updateNeighborsAt(BlockPos var1, Block var2);

    public void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12);

    public void playSeededSound(Entity var1, double var2, double var4, double var6, Holder<SoundEvent> var8, SoundSource var9, float var10, float var11, long var12);

    public void putBlock(BlockPos var1, BlockState var2);

    public void putBlock(BlockPos var1, BlockState var2, boolean var3);

    public void putBlocks(Map<BlockPos, BlockState> var1, boolean var2);

    public void markCellsDirty();

    public void refreshLocalPositions();

    public void ensureChunkReady(BlockPos var1);

    public void putBlockEntity(BlockPos var1, CompoundTag var2);

    public CompoundTag saveBlockEntity(BlockPos var1);

    public void putCeControllerData(BlockPos var1, byte[] var2);

    public void tickBlockEntities();

    public byte[] getCeControllerData(BlockPos var1);

    public Set<BlockPos> localPositions();

    public int blockCount();

    public List<long[]> glueEdgesLocal();

    public void setGlueEdgesLocal(List<long[]> var1);

    public List<FurnitureRecord> furnitureRecords();

    public void addFurnitureRecord(FurnitureRecord var1);

    public void setFurnitureRecords(List<FurnitureRecord> var1);

    public void setTransform(double var1, double var3, double var5, double var7);

    public void setTransform(double var1, double var3, double var5, double var7, double var9);

    public void setTransform(double var1, double var3, double var5, double var7, double var9, double var11);

    public void setTransform(double var1, double var3, double var5, double var7, double var9, double var11, double var13);

    public void setScaleFactor(double var1);

    @Override
    public double realScaleFactor();

    public void reanchor(Level var1, double var2, double var4, double var6, double var8);

    @Override
    public Vec3 realWorldPositionOf(BlockPos var1);

    @Override
    public Vec3 realWorldPositionOf(Vec3 var1);

    @Override
    public double realYawRadians();

    public double realPitchRadians();

    public double realRollRadians();

    @Override
    public Quaternionf realOrientationOf(Quaternionf var1);

    @Override
    public List<Player> realViewers(BlockPos var1);

    @Override
    public List<Player> realViewers(Vec3 var1);

    @Override
    public Level realLevel();

    @Override
    public Vec3 rotateToRealWorld(Vec3 var1);

    public void transferRemainingEntitiesToRealWorld();

    public <T extends Entity> List<T> getLocalEntities(Class<T> var1, AABB var2);

    @Override
    public <T extends Entity> List<T> getLocalEntities(Class<T> var1, AABB var2, Predicate<? super T> var3);

    @Override
    public boolean isRealWorldEntity(Entity var1);

    public void dispose();

    public record FurnitureRecord(String definitionId, String variantName, double lx, double ly, double lz, float yaw) {
    }
}

