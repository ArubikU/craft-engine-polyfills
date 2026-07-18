package dev.arubik.craftengine.contraption.level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Quaternionf;

import com.mojang.serialization.Lifecycle;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.api.world.properties.SlimeProperties;
import com.infernalsuite.asp.api.world.properties.SlimePropertyMap;
import com.infernalsuite.asp.level.SlimeBootstrap;
import com.infernalsuite.asp.level.SlimeLevelInstance;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.contraption.ContraptionMath;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.util.CeWorlds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.server.WorldLoader;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.chunk.CEChunk;

/**
 * A contraption's hidden mini-dimension backed by an Advanced Slime Paper IN-MEMORY world — zero folder,
 * zero region I/O — and, crucially, a real {@code ServerLevel} SUBCLASS just like {@link BukkitContraptionLevel}.
 *
 * <h2>Why it EXTENDS SlimeLevelInstance (and not a wrapper)</h2>
 * The whole point of the contraption level being a {@code ServerLevel} subclass is that vanilla code running
 * INSIDE it — a captured block-entity emitting a particle/sound, a biome/light query, {@code levelEvent} —
 * hits this class's {@code @Override}s and gets redirected into the real world, and that
 * {@code level instanceof ContraptionBoundary} resolves. A wrapper that merely HELD an ASP level loses both:
 * ASP's level isn't our subclass, so internal emissions escape into the invisible dimension and the boundary
 * lookup fails. So instead of wrapping, this EXTENDS ASP's own {@link SlimeLevelInstance} (a public
 * {@code ServerLevel} subclass) and reproduces {@code SlimeNMSBridgeImpl}'s world-load flow 1:1 in
 * {@link #create}, only instantiating THIS class in place of {@code SlimeLevelInstance}. Result: ASP's
 * in-memory chunk storage AND every override BukkitContraptionLevel has — identical behaviour, no disk.
 */
public final class AspContraptionLevel extends SlimeLevelInstance implements ContraptionBoundary, ContraptionLevel {

    public static volatile boolean UNION_REAL_ENTITIES = true;

    private Level realLevel;
    private final Set<BlockPos> localPositions = new HashSet<>();
    private final Set<Long> tickingChunks = new HashSet<>();
    private final Map<BlockPos, byte[]> ceControllerData = new HashMap<>();
    private final List<long[]> glueEdgesLocal = new ArrayList<>();
    private final List<FurnitureRecord> furnitureRecords = new ArrayList<>();
    private boolean cellsDirty = true;
    private int footprintMinX = Integer.MAX_VALUE, footprintMinY = Integer.MAX_VALUE, footprintMinZ = Integer.MAX_VALUE;
    private int footprintMaxX = Integer.MIN_VALUE, footprintMaxY = Integer.MIN_VALUE, footprintMaxZ = Integer.MIN_VALUE;
    private double realX, realY, realZ, realYaw, realPitch, realRoll;
    private double realScale = 1.0;

    private AspContraptionLevel(Level realLevel, SlimeBootstrap bootstrap, PrimaryLevelData worldData,
            ResourceKey<Level> worldKey, ResourceKey<LevelStem> dimension, LevelStem stem, World.Environment environment)
            throws IOException {
        super(bootstrap, worldData, worldKey, dimension, stem, environment);
        this.realLevel = realLevel;
        this.noSave = true;
    }

    /**
     * Builds an in-memory ASP world and wraps it in THIS subclass, replicating {@code SlimeNMSBridgeImpl}'s
     * {@code createCustomWorld} + {@code registerWorld} exactly (only the concrete class differs). Main-thread only.
     */
    public static AspContraptionLevel create(Level realLevel, double x, double y, double z, double yawRadians) {
        try {
            AdvancedSlimePaperAPI asp = AdvancedSlimePaperAPI.instance();
            SlimePropertyMap props = new SlimePropertyMap();
            props.setValue(SlimeProperties.ENVIRONMENT, "NORMAL");
            props.setValue(SlimeProperties.DIFFICULTY, "peaceful");
            props.setValue(SlimeProperties.ALLOW_MONSTERS, false);
            props.setValue(SlimeProperties.ALLOW_ANIMALS, false);
            props.setValue(SlimeProperties.PVP, false);
            String name = "cep_contraption_" + UUID.randomUUID();
            SlimeWorld world = asp.createEmptyWorld(name, false, props, NoopSlimeLoader.INSTANCE);

            SlimeBootstrap bootstrap = new SlimeBootstrap(world);
            PrimaryLevelData worldData = createWorldData(name);
            ResourceKey<LevelStem> dimension = LevelStem.OVERWORLD;
            ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION,
                    Identifier.parse(name.toLowerCase(Locale.ENGLISH)));
            LevelStem stem = MinecraftServer.getServer().registries().compositeAccess()
                    .lookupOrThrow(Registries.LEVEL_STEM).get(dimension).orElseThrow().value();

            AspContraptionLevel level = new AspContraptionLevel(realLevel, bootstrap, worldData, worldKey, dimension,
                    stem, World.Environment.NORMAL);

            MinecraftServer mc = MinecraftServer.getServer();
            mc.initWorld(level, level.serverLevelData, level.serverLevelData.worldGenOptions());
            mc.addLevel(level);

            level.setTransform(x, y, z, yawRadians);
            return level;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create AspContraptionLevel", e);
        }
    }

    /** Mirrors {@code SlimeNMSBridgeImpl.createWorldData}, but with the quiet gamerules a contraption wants. */
    private static PrimaryLevelData createWorldData(String worldName) {
        DedicatedServer mc = (DedicatedServer) MinecraftServer.getServer();
        WorldLoader.DataLoadContext context = mc.worldLoaderContext;
        GameRules rules = new GameRules(context.dataConfiguration().enabledFeatures(), GameRuleMap.of());
        rules.set(GameRules.RANDOM_TICK_SPEED, 0, null);
        rules.set(GameRules.SPAWN_MOBS, false, null);
        rules.set(GameRules.ADVANCE_TIME, false, null);
        rules.set(GameRules.ADVANCE_WEATHER, false, null);
        rules.set(GameRules.RAIDS, false, null);
        rules.set(GameRules.SPAWNER_BLOCKS_WORK, false, null);
        LevelSettings settings = new LevelSettings(worldName, GameType.SURVIVAL, false, Difficulty.PEACEFUL, true, rules,
                context.dataConfiguration());
        WorldOptions options = new WorldOptions(0L, false, false);
        PrimaryLevelData data = new PrimaryLevelData(settings, options, PrimaryLevelData.SpecialWorldProperty.FLAT,
                Lifecycle.stable());
        data.checkName(worldName);
        data.setModdedInfo(mc.getServerModName(), mc.getModdedStatus().shouldReportAsModified());
        data.setInitialized(true);
        return data;
    }

    @Override
    public ServerLevel serverLevel() {
        return this;
    }

    // ---- block contents (this == the level, exactly like BukkitContraptionLevel) ----
    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        this.cellsDirty = true;
        return super.setBlock(pos, state, flags, recursionLeft);
    }

    @Override
    public void putBlock(BlockPos local, BlockState state) {
        putBlock(local, state, false);
    }

    @Override
    public void putBlock(BlockPos local, BlockState state, boolean quiet) {
        ensureChunkTicking(local);
        setBlock(local, state, quiet ? 50 : 3);
        localPositions.add(local.immutable());
        growFootprint(local);
        activateCeChunk(local);
    }

    @Override
    public void putBlocks(Map<BlockPos, BlockState> blocks, boolean quiet) {
        int flags = quiet ? 50 : 3;
        Set<Long> touchedChunks = new HashSet<>();
        for (Map.Entry<BlockPos, BlockState> e : blocks.entrySet()) {
            BlockPos local = e.getKey();
            if (e.getValue().isAir()) {
                continue;
            }
            if (touchedChunks.add(ChunkPos.asLong(local.getX() >> 4, local.getZ() >> 4))) {
                ensureChunkTicking(local);
            }
            setBlock(local, e.getValue(), flags);
            localPositions.add(local.immutable());
            growFootprint(local);
        }
        for (long chunkKey : touchedChunks) {
            activateCeChunk(new BlockPos(ChunkPos.getX(chunkKey) << 4, 0, ChunkPos.getZ(chunkKey) << 4));
        }
    }

    @Override
    public void markCellsDirty() {
        cellsDirty = true;
    }

    @Override
    public void refreshLocalPositions() {
        if (!cellsDirty) {
            return;
        }
        cellsDirty = false;
        Set<BlockPos> candidates = new HashSet<>(localPositions);
        for (BlockPos pos : localPositions) {
            candidates.add(pos.above());
            candidates.add(pos.below());
            candidates.add(pos.north());
            candidates.add(pos.south());
            candidates.add(pos.east());
            candidates.add(pos.west());
        }
        Set<BlockPos> nowOccupied = new HashSet<>();
        for (BlockPos pos : candidates) {
            if (!withinFootprint(pos) || !getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4)
                    || getBlockState(pos).isAir()) {
                continue;
            }
            BlockPos immutable = pos.immutable();
            nowOccupied.add(immutable);
            growFootprint(immutable);
        }
        localPositions.clear();
        localPositions.addAll(nowOccupied);
    }

    @Override
    public void ensureChunkReady(BlockPos local) {
        ensureChunkTicking(local);
        activateCeChunk(local);
    }

    @Override
    public void putBlockEntity(BlockPos local, CompoundTag nbt) {
        BlockEntity be = BlockEntity.loadStatic(local, getBlockState(local), nbt, registryAccess());
        if (be != null) {
            be.setLevel(this);
            setBlockEntity(be);
        }
    }

    @Override
    public CompoundTag saveBlockEntity(BlockPos local) {
        BlockEntity be = getBlockEntity(local);
        return be != null ? be.saveWithFullMetadata(registryAccess()) : null;
    }

    @Override
    public void putCeControllerData(BlockPos local, byte[] bytes) {
        if (bytes != null) {
            ceControllerData.put(local.immutable(), bytes);
        }
    }

    @Override
    public byte[] getCeControllerData(BlockPos local) {
        return ceControllerData.get(local);
    }

    @Override
    public Set<BlockPos> localPositions() {
        return Collections.unmodifiableSet(localPositions);
    }

    @Override
    public int blockCount() {
        return localPositions.size();
    }

    private void growFootprint(BlockPos local) {
        footprintMinX = Math.min(footprintMinX, local.getX());
        footprintMinY = Math.min(footprintMinY, local.getY());
        footprintMinZ = Math.min(footprintMinZ, local.getZ());
        footprintMaxX = Math.max(footprintMaxX, local.getX());
        footprintMaxY = Math.max(footprintMaxY, local.getY());
        footprintMaxZ = Math.max(footprintMaxZ, local.getZ());
    }

    private boolean withinFootprint(BlockPos local) {
        if (footprintMinX > footprintMaxX) {
            return true;
        }
        return local.getX() >= footprintMinX - 2 && local.getX() <= footprintMaxX + 2
                && local.getY() >= footprintMinY - 2 && local.getY() <= footprintMaxY + 2
                && local.getZ() >= footprintMinZ - 2 && local.getZ() <= footprintMaxZ + 2;
    }

    private void ensureChunkTicking(BlockPos local) {
        try {
            int chunkX = local.getX() >> 4;
            int chunkZ = local.getZ() >> 4;
            if (tickingChunks.add(ChunkPos.asLong(chunkX, chunkZ))) {
                getWorld().addPluginChunkTicket(chunkX, chunkZ, JavaPlugin.getPlugin(CraftEnginePolyfills.class));
            }
        } catch (Throwable ignored) {
        }
    }

    private void activateCeChunk(BlockPos local) {
        try {
            CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(getWorld().getUID());
            if (ceWorld == null) {
                return;
            }
            CEChunk ceChunk = ceWorld.getChunkAtIfLoaded(local.getX() >> 4, local.getZ() >> 4);
            if (ceChunk != null) {
                ceChunk.activateAllBlockEntities();
            }
        } catch (Throwable ignored) {
        }
    }

    // ---- glue / furniture ----
    @Override
    public List<long[]> glueEdgesLocal() {
        return Collections.unmodifiableList(glueEdgesLocal);
    }

    @Override
    public void setGlueEdgesLocal(List<long[]> edges) {
        glueEdgesLocal.clear();
        if (edges != null) {
            for (long[] e : edges) {
                if (e != null && e.length == 2) {
                    glueEdgesLocal.add(new long[] { e[0], e[1] });
                }
            }
        }
    }

    @Override
    public List<FurnitureRecord> furnitureRecords() {
        return Collections.unmodifiableList(furnitureRecords);
    }

    @Override
    public void addFurnitureRecord(FurnitureRecord record) {
        if (record != null) {
            furnitureRecords.add(record);
        }
    }

    @Override
    public void setFurnitureRecords(List<FurnitureRecord> records) {
        furnitureRecords.clear();
        if (records != null) {
            furnitureRecords.addAll(records);
        }
    }

    // ---- transform ----
    @Override
    public void setTransform(double x, double y, double z, double yawRadians) {
        realX = x;
        realY = y;
        realZ = z;
        realYaw = yawRadians;
    }

    @Override
    public void setTransform(double x, double y, double z, double yawRadians, double pitchRadians) {
        setTransform(x, y, z, yawRadians);
        realPitch = pitchRadians;
    }

    @Override
    public void setTransform(double x, double y, double z, double yawRadians, double pitchRadians, double scale) {
        setTransform(x, y, z, yawRadians, pitchRadians);
        realScale = scale;
    }

    @Override
    public void setTransform(double x, double y, double z, double yawRadians, double pitchRadians, double rollRadians,
            double scale) {
        setTransform(x, y, z, yawRadians, pitchRadians);
        realRoll = rollRadians;
        realScale = scale;
    }

    @Override
    public void setScaleFactor(double scale) {
        realScale = scale;
    }

    @Override
    public double realScaleFactor() {
        return realScale;
    }

    @Override
    public void reanchor(Level newRealLevel, double x, double y, double z, double yawRadians) {
        if (newRealLevel != null) {
            realLevel = newRealLevel;
        }
        setTransform(x, y, z, yawRadians);
    }

    // ---- real-world projection (identical to BukkitContraptionLevel) ----
    @Override
    public Vec3 realWorldPositionOf(BlockPos local) {
        return ContraptionMath.renderPosition(local, new Vec3(realX, realY, realZ), realYaw, realPitch, realRoll, realScale);
    }

    @Override
    public Vec3 realWorldPositionOf(Vec3 local) {
        return ContraptionMath.renderPosition(local, new Vec3(realX, realY, realZ), realYaw, realPitch, realRoll, realScale);
    }

    @Override
    public double realYawRadians() {
        return realYaw;
    }

    @Override
    public double realPitchRadians() {
        return realPitch;
    }

    @Override
    public double realRollRadians() {
        return realRoll;
    }

    @Override
    public Quaternionf realOrientationOf(Quaternionf local) {
        Quaternionf bearing = new Quaternionf().rotateY((float) (-realYaw)).rotateX((float) realPitch).rotateZ((float) realRoll);
        return local != null ? bearing.mul(local, new Quaternionf()) : bearing;
    }

    @Override
    public List<Player> realViewers(BlockPos local) {
        return realViewers(realWorldPositionOf(local));
    }

    @Override
    public List<Player> realViewers(Vec3 realPos) {
        try {
            CraftWorld bukkitWorld = ((ServerLevel) realLevel).getWorld();
            int chunkX = (int) Math.floor(realPos.x) >> 4;
            int chunkZ = (int) Math.floor(realPos.z) >> 4;
            return CeWorlds.of(bukkitWorld).getTrackedBy(new net.momirealms.craftengine.core.world.ChunkPos(chunkX, chunkZ));
        } catch (Throwable t) {
            return List.of();
        }
    }

    @Override
    public Level realLevel() {
        return realLevel;
    }

    @Override
    public Vec3 rotateToRealWorld(Vec3 localDirection) {
        return ContraptionMath.rotateYaw(localDirection, realYaw);
    }

    private AABB localBoxToRealWorld(AABB local) {
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        for (double cx : new double[] { local.minX, local.maxX }) {
            for (double cy : new double[] { local.minY, local.maxY }) {
                for (double cz : new double[] { local.minZ, local.maxZ }) {
                    Vec3 r = realWorldPositionOf(new Vec3(cx, cy, cz));
                    minX = Math.min(minX, r.x);
                    minY = Math.min(minY, r.y);
                    minZ = Math.min(minZ, r.z);
                    maxX = Math.max(maxX, r.x);
                    maxY = Math.max(maxY, r.y);
                    maxZ = Math.max(maxZ, r.z);
                }
            }
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> typeTest, AABB box, Predicate<? super T> predicate) {
        List<T> fake = super.getEntities(typeTest, box, predicate);
        if (!UNION_REAL_ENTITIES || !(realLevel instanceof ServerLevel realServerLevel)) {
            return fake;
        }
        AABB realBox;
        try {
            realBox = localBoxToRealWorld(box);
        } catch (Throwable t) {
            return fake;
        }
        List<T> real = realServerLevel.getEntities(typeTest, realBox, predicate);
        if (real.isEmpty()) {
            return fake;
        }
        List<T> union = new ArrayList<>(fake.size() + real.size());
        union.addAll(fake);
        union.addAll(real);
        return union;
    }

    @Override
    public <T extends Entity> List<T> getLocalEntities(Class<T> clazz, AABB box) {
        return getLocalEntities(clazz, box, e -> true);
    }

    @Override
    public <T extends Entity> List<T> getLocalEntities(Class<T> clazz, AABB box, Predicate<? super T> filter) {
        return super.getEntities(EntityTypeTest.forClass(clazz), box, filter);
    }

    @Override
    public boolean isRealWorldEntity(Entity entity) {
        return entity != null && entity.level() != this;
    }

    @Override
    public void transferRemainingEntitiesToRealWorld() {
        if (!(realLevel instanceof ServerLevel)) {
            return;
        }
        CraftWorld realBukkitWorld = ((ServerLevel) realLevel).getWorld();
        List<Entity> snapshot = new ArrayList<>();
        getAllEntities().forEach(snapshot::add);
        for (Entity entity : snapshot) {
            if (entity instanceof ItemEntity || entity.isRemoved() || entity instanceof Display
                    || entity instanceof Interaction) {
                continue;
            }
            try {
                Vec3 realPos = realWorldPositionOf(entity.position());
                CraftEntity bukkitEntity = entity.getBukkitEntity();
                bukkitEntity.teleport(new Location(realBukkitWorld, realPos.x, realPos.y, realPos.z));
            } catch (Throwable ignored) {
            }
        }
    }

    // ---- suppressed vanilla ticking (same as BukkitContraptionLevel) ----
    @Override
    public void tickChunk(LevelChunk chunk, int randomTickSpeed) {
    }

    @Override
    public void tickCustomSpawners(boolean spawnEnemies) {
    }

    @Override
    public void save(net.minecraft.util.ProgressListener progress, boolean flush, boolean skipSave, boolean close) {
    }

    @Override
    public void saveIncrementally(boolean doFull) {
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        BlockPos local = new BlockPos(x << 2, y << 2, z << 2);
        BlockPos realPos = BlockPos.containing(realWorldPositionOf(local));
        return realLevel.getBiome(realPos);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z) {
        return getUncachedNoiseBiome(x, y, z);
    }

    // ---- real-world redirects (identical to BukkitContraptionLevel) ----
    @Override
    public void addParticle(ParticleOptions particle, double x, double y, double z, double dx, double dy, double dz) {
        Vec3 realPos = realWorldPositionOf(BlockPos.containing(x, y, z));
        realLevel.addParticle(particle, realPos.x, realPos.y, realPos.z, dx, dy, dz);
    }

    @Override
    public <T extends ParticleOptions> int sendParticlesSource(List<ServerPlayer> players, Entity source, T particle,
            boolean overrideLimiter, boolean force, double x, double y, double z, int count, double dx, double dy,
            double dz, double speed) {
        if (!(realLevel instanceof ServerLevel realServerLevel)) {
            return 0;
        }
        Vec3 realPos = realWorldPositionOf(new Vec3(x, y, z));
        double rdx = dx, rdy = dy, rdz = dz;
        if (count == 0) {
            Vec3 rotated = rotateToRealWorld(new Vec3(dx, dy, dz));
            rdx = rotated.x;
            rdy = rotated.y;
            rdz = rotated.z;
        }
        return realServerLevel.sendParticlesSource(realServerLevel.players(), null, particle, overrideLimiter, force,
                realPos.x, realPos.y, realPos.z, count, rdx, rdy, rdz, speed);
    }

    @Override
    public void playSeededSound(Entity source, double x, double y, double z, Holder<SoundEvent> sound,
            SoundSource category, float volume, float pitch, long seed) {
        Vec3 realPos = realWorldPositionOf(BlockPos.containing(x, y, z));
        realLevel.playSeededSound(null, realPos.x, realPos.y, realPos.z, sound, category, volume, pitch, seed);
    }

    @Override
    public void playSeededSound(Entity source, Entity target, Holder<SoundEvent> sound, SoundSource category,
            float volume, float pitch, long seed) {
    }

    @Override
    public void levelEvent(Entity source, int type, BlockPos pos, int data) {
        BlockPos realPos = BlockPos.containing(realWorldPositionOf(pos));
        realLevel.levelEvent(null, type, realPos, data);
    }

    @Override
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
        BlockPos realPos = BlockPos.containing(realWorldPositionOf(pos));
        realLevel.destroyBlockProgress(breakerId, realPos, progress);
    }

    // ---- lifecycle ----
    @Override
    public void dispose() {
        unregisterCapturedMachines();
        unloadCeWorld();
        releaseChunkTickets();
        localPositions.clear();
        try {
            // Unload the ASP world — its in-memory chunks are dropped (NoopSlimeLoader never persisted).
            Bukkit.unloadWorld(getWorld(), false);
        } catch (Throwable ignored) {
        }
    }

    private void unloadCeWorld() {
        try {
            CraftEngine.instance().worldManager().unloadWorld(CeWorlds.of(getWorld()));
        } catch (Throwable ignored) {
        }
    }

    private void unregisterCapturedMachines() {
        for (BlockPos local : new HashSet<>(localPositions)) {
            try {
                net.momirealms.craftengine.core.block.entity.BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(this, local);
                BlockEntityController controller = be == null ? null : be.controller;
                if (controller instanceof AbstractMachineBlockEntity machine) {
                    machine.unregister();
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private void releaseChunkTickets() {
        try {
            JavaPlugin plugin = JavaPlugin.getPlugin(CraftEnginePolyfills.class);
            for (Long key : tickingChunks) {
                getWorld().removePluginChunkTicket(ChunkPos.getX(key), ChunkPos.getZ(key), plugin);
            }
        } catch (Throwable ignored) {
        }
        tickingChunks.clear();
    }
}
