package dev.arubik.craftengine.contraption.level;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.joml.Quaternionf;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.momirealms.craftengine.core.entity.player.Player;

/**
 * A contraption's hidden mini-dimension, as an interface so it can be backed either by a
 * {@link BukkitContraptionLevel} (a vanilla {@code ServerLevel} the plugin builds itself, temp-folder
 * backed, works on any Paper server) or an {@code AspContraptionLevel} (an Advanced Slime Paper in-memory
 * world — zero folder, zero region I/O — used only when the ASP fork is present).
 *
 * <p>The rest of the plugin talks to this interface for contraption behaviour (transform, local block set,
 * real-world projection) and never cares which backs it. {@link #serverLevel()} hands out the underlying
 * NMS level for the places that must pass it to vanilla/CraftEngine. The interface keeps the name the
 * concrete class used to have, so existing {@code ContraptionLevel}-typed references resolve unchanged.
 */
public interface ContraptionLevel extends ContraptionBoundary {

    /**
     * Builds a contraption's hidden level at the given bearing transform — an in-memory ASP world when the
     * ASP fork is present (zero folder, zero region I/O), otherwise a temp-folder-backed vanilla
     * {@link BukkitContraptionLevel}. The single factory every capture/spawn/rehydrate path calls, so the
     * choice is made once and nothing downstream cares which backs it.
     */
    static ContraptionLevel create(Level realLevel, double x, double y, double z, double yawRadians) {
        if (AspSupport.available()) {
            return AspSupport.create(realLevel, x, y, z, yawRadians);
        }
        return BukkitContraptionLevel.create(realLevel, x, y, z, yawRadians);
    }

    /** The underlying NMS level — for code that must hand a real {@link ServerLevel} to vanilla/CraftEngine. */
    ServerLevel serverLevel();

    // ---- NMS convenience: the level's block/world accessors, so callers that read or write its contents
    // do not have to spell out serverLevel() every time. BukkitContraptionLevel satisfies these by simply
    // BEING a ServerLevel (inherited/overridden); an ASP-backed level delegates them to its serverLevel(). ----
    BlockState getBlockState(BlockPos pos);

    net.minecraft.world.level.block.entity.BlockEntity getBlockEntity(BlockPos pos);

    boolean setBlock(BlockPos pos, BlockState state, int flags);

    org.bukkit.World getWorld();

    net.minecraft.core.RegistryAccess registryAccess();

    Iterable<Entity> getAllEntities();

    void updateNeighborsAt(BlockPos pos, net.minecraft.world.level.block.Block block);

    void addParticle(net.minecraft.core.particles.ParticleOptions particle, double x, double y, double z,
            double dx, double dy, double dz);

    void playSeededSound(Entity source, double x, double y, double z,
            net.minecraft.core.Holder<net.minecraft.sounds.SoundEvent> sound, net.minecraft.sounds.SoundSource category,
            float volume, float pitch, long seed);

    // ---- block contents ----
    void putBlock(BlockPos local, BlockState state);

    void putBlock(BlockPos local, BlockState state, boolean quiet);

    void putBlocks(Map<BlockPos, BlockState> blocks, boolean quiet);

    void markCellsDirty();

    void refreshLocalPositions();

    void ensureChunkReady(BlockPos local);

    void putBlockEntity(BlockPos local, CompoundTag nbt);

    CompoundTag saveBlockEntity(BlockPos local);

    void putCeControllerData(BlockPos local, byte[] bytes);

    byte[] getCeControllerData(BlockPos local);

    Set<BlockPos> localPositions();

    int blockCount();

    // ---- glue / furniture side-data ----
    List<long[]> glueEdgesLocal();

    void setGlueEdgesLocal(List<long[]> edges);

    List<FurnitureRecord> furnitureRecords();

    void addFurnitureRecord(FurnitureRecord record);

    void setFurnitureRecords(List<FurnitureRecord> records);

    // ---- transform ----
    void setTransform(double x, double y, double z, double yawRadians);

    void setTransform(double x, double y, double z, double yawRadians, double pitchRadians);

    void setTransform(double x, double y, double z, double yawRadians, double pitchRadians, double scale);

    void setTransform(double x, double y, double z, double yawRadians, double pitchRadians, double rollRadians,
            double scale);

    void setScaleFactor(double scale);

    double realScaleFactor();

    void reanchor(Level newRealLevel, double x, double y, double z, double yawRadians);

    // ---- real-world projection ----
    Vec3 realWorldPositionOf(BlockPos local);

    Vec3 realWorldPositionOf(Vec3 local);

    double realYawRadians();

    double realPitchRadians();

    double realRollRadians();

    Quaternionf realOrientationOf(Quaternionf local);

    List<Player> realViewers(BlockPos local);

    List<Player> realViewers(Vec3 realPos);

    Level realLevel();

    Vec3 rotateToRealWorld(Vec3 localDirection);

    void transferRemainingEntitiesToRealWorld();

    <T extends Entity> List<T> getLocalEntities(Class<T> type, AABB localBounds);

    <T extends Entity> List<T> getLocalEntities(Class<T> type, AABB localBounds, Predicate<? super T> filter);

    boolean isRealWorldEntity(Entity entity);

    // ---- lifecycle ----
    void dispose();

    /**
     * A captured CraftEngine furniture piece, stored so a contraption can restore its furniture on
     * disassemble. Lives on the interface so {@code ContraptionLevel.FurnitureRecord} resolves for every
     * caller regardless of which backing level produced it.
     */
    record FurnitureRecord(String definitionId, String variantName, double lx, double ly, double lz, float yaw) {
    }
}
