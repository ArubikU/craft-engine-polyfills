/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  dev.arubik.craftengine.CraftEnginePolyfills
 *  dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes
 *  dev.arubik.craftengine.contraption.ContraptionMath
 *  dev.arubik.craftengine.contraption.ContraptionWorlds
 *  dev.arubik.craftengine.contraption.level.ContraptionBoundary
 *  dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity
 *  dev.arubik.craftengine.util.CeWorlds
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.ProgressListener
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.RandomSequences
 *  net.minecraft.world.entity.Display
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Interaction
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.flag.FeatureFlags
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.CustomSpawner
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelSettings
 *  net.minecraft.world.level.WorldDataConfiguration
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biomes
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkGenerator
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.dimension.LevelStem
 *  net.minecraft.world.level.entity.EntityTypeTest
 *  net.minecraft.world.level.gamerules.GameRules
 *  net.minecraft.world.level.levelgen.FlatLevelSource
 *  net.minecraft.world.level.levelgen.WorldOptions
 *  net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings
 *  net.minecraft.world.level.storage.LevelStorageSource
 *  net.minecraft.world.level.storage.LevelStorageSource$LevelStorageAccess
 *  net.minecraft.world.level.storage.PrimaryLevelData
 *  net.minecraft.world.level.storage.PrimaryLevelData$SpecialWorldProperty
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.plugin.CraftEngine
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.ChunkPos
 *  net.momirealms.craftengine.core.world.World
 *  net.momirealms.craftengine.core.world.chunk.CEChunk
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.generator.BiomeProvider
 *  org.bukkit.generator.ChunkGenerator
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package dev.arubik.craftengine.contraption.level;

import com.mojang.serialization.Lifecycle;
import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.level.ContraptionBoundary;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionLevel.FurnitureRecord;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.util.CeWorlds;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.chunk.CEChunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

/**
 * A contraption's hidden mini-dimension, backed by a real vanilla {@link ServerLevel} the plugin builds
 * itself. Implements {@link ContraptionLevel}; the ASP-backed alternative is {@code AspContraptionLevel}.
 *
 * <h2>ServerLevel / Level load analysis — what this dimension strips, and what it cannot</h2>
 * A {@link ServerLevel} is built for a real, saved, populated multiplayer world. A contraption is a handful
 * of blocks that lives for seconds and persists only as NBT. So this class carries a vanilla level but pays
 * for as little of it as possible. Analysed against {@code net.minecraft.server.level.ServerLevel} /
 * {@code net.minecraft.world.level.Level} (the mapped server jar):
 *
 * <h3>Construction cost — stripped where reachable</h3>
 * <ul>
 * <li><b>The natural-terrain generator.</b> The {@code ServerLevel} ctor builds a {@code ServerChunkCache}
 *     and immediately calls {@code getGeneratorState()} on the level's generator. Handed the overworld
 *     generator (the vanilla default) that computes structure placement for every structure set — tens of ms
 *     for a dimension that never generates a single natural block. {@link #voidGenerator} passes a flat,
 *     structureless {@link net.minecraft.world.level.levelgen.FlatLevelSource} instead, so the state is
 *     trivial and every chunk is pure void — exactly right, since every cell is placed by hand.</li>
 * <li><b>The real world folder.</b> {@code createAccess} makes a {@code uid.dat}/{@code session.lock}
 *     directory on disk. It is created in OS temp ({@link #tmpSource}), deleted off-thread the moment the
 *     contraption disposes, and wiped wholesale on boot — never in the server's world directory, and
 *     nothing of value is ever written (see {@code noSave} below). It cannot be eliminated entirely: a
 *     {@code CraftWorld} needs a {@code File}-based folder and {@code session.lock} needs a real
 *     {@code FileChannel}, so the achievable minimum is "ephemeral, out of sight, deleted fast".</li>
 * </ul>
 *
 * <h3>Per-tick cost — the server ticks every registered level; here is what each does for THIS one</h3>
 * <ul>
 * <li>{@code tickChunk} — <b>overridden empty.</b> Vanilla runs random block ticks (crop growth, fire
 *     spread, fluid) for every ticking chunk; a contraption wants none, so the override skips them.
 *     Belt-and-suspenders with {@code RANDOM_TICK_SPEED = 0} in {@link #quietGameRules}.</li>
 * <li>{@code tickCustomSpawners} — <b>overridden empty.</b> No cats/phantoms/wandering-traders/sieges scan
 *     a hidden dimension. Backed by {@code SPAWN_MOBS = false}.</li>
 * <li>{@code advanceWeatherCycle} — private, cannot be individually overridden, but gated to a near-no-op by
 *     {@code ADVANCE_WEATHER = false}.</li>
 * <li>{@code tickTime} — <b>deliberately KEPT.</b> It calls {@code PrimaryLevelData.setGameTime} (verified in
 *     the mapped jar), and captured machines (crushers/pumps/timers) read {@code getGameTime()} for their
 *     cooldowns — freezing it would stall every machine. So this one stays; it is cheap anyway.</li>
 * <li>{@code save}/{@code saveIncrementally} — <b>overridden empty</b>, and {@code noSave = true}, so
 *     autosave and shutdown-save both skip this level: no region/level.dat is ever written. The only
 *     persistence is the structure NBT on the bearing.</li>
 * </ul>
 *
 * <h3>The irreducible floor</h3>
 * The {@code ServerLevel} ctor's {@code new ServerChunkCache(...)} (a {@code final} field, direct
 * {@code new} — no method to override) and its per-tick {@code ServerChunkCache.tick} are needed: the chunk
 * cache is what ticks the captured block-entities and processes their scheduled block/fluid ticks, which is
 * the whole point of using a real level rather than a plain block map. That cost is why the ASP path exists
 * — an in-memory slime world removes the storage/region half of it — but it cannot be overridden away while
 * a contraption IS a {@code ServerLevel}.
 */
public final class BukkitContraptionLevel
extends ServerLevel
implements ContraptionBoundary, ContraptionLevel {
    public static volatile boolean UNION_REAL_ENTITIES = true;
    private Level realLevel;
    private final Set<BlockPos> localPositions = new HashSet<BlockPos>();
    private boolean cellsDirty = true;
    private int footprintMinX = Integer.MAX_VALUE;
    private int footprintMinY = Integer.MAX_VALUE;
    private int footprintMinZ = Integer.MAX_VALUE;
    private int footprintMaxX = Integer.MIN_VALUE;
    private int footprintMaxY = Integer.MIN_VALUE;
    private int footprintMaxZ = Integer.MIN_VALUE;
    private static final int FOOTPRINT_MARGIN = 2;
    private final Set<Long> tickingChunks = new HashSet<Long>();
    private final Map<BlockPos, byte[]> ceControllerData = new HashMap<BlockPos, byte[]>();
    private final List<long[]> glueEdgesLocal = new ArrayList<long[]>();
    private final List<FurnitureRecord> furnitureRecords = new ArrayList<FurnitureRecord>();
    private double realX;
    private double realY;
    private double realZ;
    private double realYaw;
    private double realPitch;
    private double realRoll;
    private double realScale = 1.0;
    private final LevelStorageSource.LevelStorageAccess storageAccess;
    private static LevelStorageSource TMP_SOURCE;
    private static FlatLevelSource VOID_GENERATOR;
    private static final ExecutorService CLEANUP;

    private BukkitContraptionLevel(Level realLevel, MinecraftServer server, Executor executor, LevelStorageSource.LevelStorageAccess storageAccess, PrimaryLevelData levelData, ResourceKey<Level> dimensionKey, LevelStem levelStem, boolean isDebug, long seed, List<CustomSpawner> customSpawners, boolean tickTime, RandomSequences randomSequences, World.Environment environment, org.bukkit.generator.ChunkGenerator bukkitGenerator, BiomeProvider biomeProvider) {
        super(server, executor, storageAccess, levelData, dimensionKey, levelStem, isDebug, seed, customSpawners, tickTime, randomSequences, environment, bukkitGenerator, biomeProvider);
        this.realLevel = realLevel;
        this.storageAccess = storageAccess;
        this.noSave = true;
    }

    public void save(ProgressListener progress, boolean flush, boolean skipSave, boolean close) {
    }

    public void saveIncrementally(boolean doFull) {
    }

    private static GameRules quietGameRules() {
        GameRules rules = new GameRules(FeatureFlags.DEFAULT_FLAGS);
        rules.set(GameRules.RANDOM_TICK_SPEED, 0, null);
        rules.set(GameRules.SPAWN_MOBS, false, null);
        rules.set(GameRules.ADVANCE_TIME, false, null);
        rules.set(GameRules.ADVANCE_WEATHER, false, null);
        rules.set(GameRules.RAIDS, false, null);
        rules.set(GameRules.SPAWNER_BLOCKS_WORK, false, null);
        return rules;
    }

    private static Path storageRoot() {
        return Path.of(System.getProperty("java.io.tmpdir"), "cep_contraptions");
    }

    private static LevelStorageSource tmpSource() throws IOException {
        if (TMP_SOURCE == null) {
            Path root = BukkitContraptionLevel.storageRoot();
            Files.createDirectories(root, new FileAttribute[0]);
            TMP_SOURCE = LevelStorageSource.createDefault((Path)root);
        }
        return TMP_SOURCE;
    }

    public static void wipeStorageRoot() {
        Path root = BukkitContraptionLevel.storageRoot();
        if (!Files.isDirectory(root, new LinkOption[0])) {
            return;
        }
        try (Stream<Path> walk = Files.walk(root, new FileVisitOption[0]);){
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            });
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static FlatLevelSource voidGenerator(MinecraftServer server) {
        if (VOID_GENERATOR == null) {
            Holder.Reference biome = server.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.THE_VOID);
            FlatLevelGeneratorSettings settings = new FlatLevelGeneratorSettings(Optional.empty(), (Holder)biome, List.of());
            VOID_GENERATOR = new FlatLevelSource(settings);
        }
        return VOID_GENERATOR;
    }

    public static BukkitContraptionLevel create(Level realLevel, double x, double y, double z, double yawRadians) {
        try {
            CraftServer craftServer = (CraftServer)Bukkit.getServer();
            DedicatedServer server = craftServer.getServer();
            ServerLevel overworld = server.overworld();
            String id = "cep_contraption_" + String.valueOf(UUID.randomUUID());
            Identifier dimensionId = Identifier.fromNamespaceAndPath((String)"cep", (String)id);
            ResourceKey dimensionKey = ResourceKey.create((ResourceKey)Registries.DIMENSION, (Identifier)dimensionId);
            ResourceKey levelStemKey = ResourceKey.create((ResourceKey)Registries.LEVEL_STEM, (Identifier)dimensionId);
            LevelStem levelStem = new LevelStem(overworld.dimensionTypeRegistration(), (ChunkGenerator)BukkitContraptionLevel.voidGenerator((MinecraftServer)server));
            GameRules gameRules = BukkitContraptionLevel.quietGameRules();
            LevelSettings levelSettings = new LevelSettings(id, GameType.SURVIVAL, false, Difficulty.PEACEFUL, false, gameRules, WorldDataConfiguration.DEFAULT);
            WorldOptions worldOptions = new WorldOptions(0L, false, false);
            PrimaryLevelData levelData = new PrimaryLevelData(levelSettings, worldOptions, PrimaryLevelData.SpecialWorldProperty.NONE, Lifecycle.stable());
            LevelStorageSource.LevelStorageAccess storageAccess = BukkitContraptionLevel.tmpSource().createAccess(id, levelStemKey);
            BukkitContraptionLevel level = new BukkitContraptionLevel(realLevel, (MinecraftServer)server, server.executor, storageAccess, levelData, (ResourceKey<Level>)dimensionKey, levelStem, false, 0L, List.of(), true, new RandomSequences(), World.Environment.CUSTOM, new VoidChunkGenerator(), null);
            level.setTransform(x, y, z, yawRadians);
            server.addLevel((ServerLevel)level);
            return level;
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to create ContraptionLevel", e);
        }
    }

    public void putBlock(BlockPos local, BlockState state) {
        this.putBlock(local, state, false);
    }

    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        this.cellsDirty = true;
        return super.setBlock(pos, state, flags, recursionLeft);
    }

    public void markCellsDirty() {
        this.cellsDirty = true;
    }

    public void putBlock(BlockPos local, BlockState state, boolean quiet) {
        this.ensureChunkTicking(local);
        int flags = quiet ? 50 : 3;
        this.setBlock(local, state, flags);
        this.localPositions.add(local.immutable());
        this.growFootprint(local);
        this.activateCeChunk(local);
    }

    public void putBlocks(Map<BlockPos, BlockState> blocks, boolean quiet) {
        int flags = quiet ? 50 : 3;
        HashSet<Long> touchedChunks = new HashSet<Long>();
        for (Map.Entry<BlockPos, BlockState> e : blocks.entrySet()) {
            BlockPos local = e.getKey();
            if (e.getValue().isAir()) continue;
            if (touchedChunks.add(ChunkPos.asLong((int)(local.getX() >> 4), (int)(local.getZ() >> 4)))) {
                this.ensureChunkTicking(local);
            }
            this.setBlock(local, e.getValue(), flags);
            this.localPositions.add(local.immutable());
            this.growFootprint(local);
        }
        for (long chunkKey : touchedChunks) {
            this.activateCeChunk(new BlockPos(ChunkPos.getX(chunkKey) << 4, 0, ChunkPos.getZ(chunkKey) << 4));
        }
    }

    private void growFootprint(BlockPos local) {
        this.footprintMinX = Math.min(this.footprintMinX, local.getX());
        this.footprintMinY = Math.min(this.footprintMinY, local.getY());
        this.footprintMinZ = Math.min(this.footprintMinZ, local.getZ());
        this.footprintMaxX = Math.max(this.footprintMaxX, local.getX());
        this.footprintMaxY = Math.max(this.footprintMaxY, local.getY());
        this.footprintMaxZ = Math.max(this.footprintMaxZ, local.getZ());
    }

    private boolean withinFootprint(BlockPos local) {
        if (this.footprintMinX > this.footprintMaxX) {
            return true;
        }
        return local.getX() >= this.footprintMinX - 2 && local.getX() <= this.footprintMaxX + 2 && local.getY() >= this.footprintMinY - 2 && local.getY() <= this.footprintMaxY + 2 && local.getZ() >= this.footprintMinZ - 2 && local.getZ() <= this.footprintMaxZ + 2;
    }

    public void refreshLocalPositions() {
        if (!this.cellsDirty) {
            return;
        }
        this.cellsDirty = false;
        HashSet<BlockPos> candidates = new HashSet<BlockPos>(this.localPositions);
        for (BlockPos pos : this.localPositions) {
            candidates.add(pos.above());
            candidates.add(pos.below());
            candidates.add(pos.north());
            candidates.add(pos.south());
            candidates.add(pos.east());
            candidates.add(pos.west());
        }
        HashSet<BlockPos> nowOccupied = new HashSet<BlockPos>();
        for (BlockPos pos : candidates) {
            if (!this.withinFootprint(pos) || !this.getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4) || this.getBlockState(pos).isAir()) continue;
            BlockPos immutable = pos.immutable();
            nowOccupied.add(immutable);
            this.growFootprint(immutable);
        }
        this.localPositions.clear();
        this.localPositions.addAll(nowOccupied);
    }

    private void ensureChunkTicking(BlockPos local) {
        try {
            int chunkX = local.getX() >> 4;
            int chunkZ = local.getZ() >> 4;
            long key = ChunkPos.asLong((int)chunkX, (int)chunkZ);
            if (this.tickingChunks.add(key)) {
                this.getWorld().addPluginChunkTicket(chunkX, chunkZ, (Plugin)JavaPlugin.getPlugin(CraftEnginePolyfills.class));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void activateCeChunk(BlockPos local) {
        try {
            CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(this.getWorld().getUID());
            if (ceWorld == null) {
                return;
            }
            CEChunk ceChunk = ceWorld.getChunkAtIfLoaded(local.getX() >> 4, local.getZ() >> 4);
            if (ceChunk != null) {
                ceChunk.activateAllBlockEntities();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void ensureChunkReady(BlockPos local) {
        this.ensureChunkTicking(local);
        this.activateCeChunk(local);
    }

    public void putBlockEntity(BlockPos local, CompoundTag nbt) {
        BlockEntity be = BlockEntity.loadStatic((BlockPos)local, (BlockState)this.getBlockState(local), (CompoundTag)nbt, (HolderLookup.Provider)this.registryAccess());
        if (be != null) {
            be.setLevel((Level)this);
            this.setBlockEntity(be);
        }
    }

    public CompoundTag saveBlockEntity(BlockPos local) {
        BlockEntity be = this.getBlockEntity(local);
        return be != null ? be.saveWithFullMetadata((HolderLookup.Provider)this.registryAccess()) : null;
    }

    public void putCeControllerData(BlockPos local, byte[] bytes) {
        if (bytes != null) {
            this.ceControllerData.put(local.immutable(), bytes);
        }
    }

    public byte[] getCeControllerData(BlockPos local) {
        return this.ceControllerData.get(local);
    }

    public Set<BlockPos> localPositions() {
        return Collections.unmodifiableSet(this.localPositions);
    }

    public List<long[]> glueEdgesLocal() {
        return Collections.unmodifiableList(this.glueEdgesLocal);
    }

    public void setGlueEdgesLocal(List<long[]> edges) {
        this.glueEdgesLocal.clear();
        if (edges != null) {
            for (long[] e : edges) {
                if (e == null || e.length != 2) continue;
                this.glueEdgesLocal.add(new long[]{e[0], e[1]});
            }
        }
    }

    public List<FurnitureRecord> furnitureRecords() {
        return Collections.unmodifiableList(this.furnitureRecords);
    }

    public void addFurnitureRecord(FurnitureRecord record) {
        if (record != null) {
            this.furnitureRecords.add(record);
        }
    }

    public void setFurnitureRecords(List<FurnitureRecord> records) {
        this.furnitureRecords.clear();
        if (records != null) {
            this.furnitureRecords.addAll(records);
        }
    }

    public int blockCount() {
        return this.localPositions.size();
    }

    public void setTransform(double x, double y, double z, double yawRadians) {
        this.realX = x;
        this.realY = y;
        this.realZ = z;
        this.realYaw = yawRadians;
    }

    public void setTransform(double x, double y, double z, double yawRadians, double pitchRadians) {
        this.realX = x;
        this.realY = y;
        this.realZ = z;
        this.realYaw = yawRadians;
        this.realPitch = pitchRadians;
    }

    public void setTransform(double x, double y, double z, double yawRadians, double pitchRadians, double scale) {
        this.realX = x;
        this.realY = y;
        this.realZ = z;
        this.realYaw = yawRadians;
        this.realPitch = pitchRadians;
        this.realScale = scale;
    }

    public void setTransform(double x, double y, double z, double yawRadians, double pitchRadians, double rollRadians, double scale) {
        this.realX = x;
        this.realY = y;
        this.realZ = z;
        this.realYaw = yawRadians;
        this.realPitch = pitchRadians;
        this.realRoll = rollRadians;
        this.realScale = scale;
    }

    public void setScaleFactor(double scale) {
        this.realScale = scale;
    }

    public double realScaleFactor() {
        return this.realScale;
    }

    public void reanchor(Level newRealLevel, double x, double y, double z, double yawRadians) {
        if (newRealLevel != null) {
            this.realLevel = newRealLevel;
        }
        this.setTransform(x, y, z, yawRadians);
    }

    public Vec3 realWorldPositionOf(BlockPos local) {
        return ContraptionMath.renderPosition((BlockPos)local, (Vec3)new Vec3(this.realX, this.realY, this.realZ), (double)this.realYaw, (double)this.realPitch, (double)this.realRoll, (double)this.realScale);
    }

    public Vec3 realWorldPositionOf(Vec3 local) {
        return ContraptionMath.renderPosition((Vec3)local, (Vec3)new Vec3(this.realX, this.realY, this.realZ), (double)this.realYaw, (double)this.realPitch, (double)this.realRoll, (double)this.realScale);
    }

    public double realYawRadians() {
        return this.realYaw;
    }

    public double realPitchRadians() {
        return this.realPitch;
    }

    public double realRollRadians() {
        return this.realRoll;
    }

    public Quaternionf realOrientationOf(Quaternionf local) {
        Quaternionf bearing = new Quaternionf().rotateY((float)(-this.realYaw)).rotateX((float)this.realPitch).rotateZ((float)this.realRoll);
        return local != null ? bearing.mul((Quaternionfc)local, new Quaternionf()) : bearing;
    }

    public List<Player> realViewers(BlockPos local) {
        return this.realViewers(this.realWorldPositionOf(local));
    }

    public List<Player> realViewers(Vec3 realPos) {
        try {
            CraftWorld bukkitWorld = ((ServerLevel)this.realLevel).getWorld();
            int chunkX = (int)Math.floor(realPos.x) >> 4;
            int chunkZ = (int)Math.floor(realPos.z) >> 4;
            return CeWorlds.of((World)bukkitWorld).getTrackedBy(new net.momirealms.craftengine.core.world.ChunkPos(chunkX, chunkZ));
        }
        catch (Throwable t) {
            return List.of();
        }
    }

    public Level realLevel() {
        return this.realLevel;
    }

    public void transferRemainingEntitiesToRealWorld() {
        if (!(this.realLevel instanceof ServerLevel)) {
            return;
        }
        CraftWorld realBukkitWorld = ((ServerLevel)this.realLevel).getWorld();
        ArrayList<Entity> snapshot = new ArrayList<Entity>();
        for (Entity e : this.getAllEntities()) {
            snapshot.add(e);
        }
        for (Entity entity : snapshot) {
            if (entity instanceof ItemEntity || entity.isRemoved() || entity instanceof Display || entity instanceof Interaction) continue;
            try {
                Vec3 realPos = this.realWorldPositionOf(entity.position());
                CraftEntity bukkitEntity = entity.getBukkitEntity();
                bukkitEntity.teleport(new Location((World)realBukkitWorld, realPos.x, realPos.y, realPos.z));
            }
            catch (Throwable throwable) {}
        }
    }

    public Vec3 rotateToRealWorld(Vec3 localDirection) {
        // Full orientation, not yaw-only (2026-07-18 — "el fan no sigue bien el pitch/yaw ... si el contraption
        // está de cabeza el fan me jala en vez de empujarme"). This rotates a LOCAL direction (a fan's thrust
        // axis, its particle-stream velocity) into the world, so it must apply the SAME yaw∘pitch∘roll the cells
        // render with — with only yaw an upside-down contraption's thrust/airflow pointed the wrong way.
        return ContraptionMath.rotateYawPitchRoll(localDirection, this.realYaw, this.realPitch, this.realRoll);
    }

    private AABB localBoxToRealWorld(AABB local) {
        double[] xs = new double[]{local.minX, local.maxX};
        double[] ys = new double[]{local.minY, local.maxY};
        double[] zs = new double[]{local.minZ, local.maxZ};
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        for (double cx : xs) {
            for (double cy : ys) {
                for (double cz : zs) {
                    Vec3 r = this.realWorldPositionOf(new Vec3(cx, cy, cz));
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

    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> typeTest, AABB box, Predicate<? super T> predicate) {
        AABB realBox;
        Level level;
        List fake = super.getEntities(typeTest, box, predicate);
        if (!UNION_REAL_ENTITIES || !((level = this.realLevel) instanceof ServerLevel)) {
            return fake;
        }
        ServerLevel realServerLevel = (ServerLevel)level;
        try {
            realBox = this.localBoxToRealWorld(box);
        }
        catch (Throwable t) {
            return fake;
        }
        List real = realServerLevel.getEntities(typeTest, realBox, predicate);
        if (real.isEmpty()) {
            return fake;
        }
        ArrayList union = new ArrayList(fake.size() + real.size());
        union.addAll(fake);
        union.addAll(real);
        return union;
    }

    public <T extends Entity> List<T> getLocalEntities(Class<T> clazz, AABB box, Predicate<? super T> predicate) {
        return super.getEntities(EntityTypeTest.forClass(clazz), box, predicate);
    }

    public <T extends Entity> List<T> getLocalEntities(Class<T> clazz, AABB box) {
        return this.getLocalEntities(clazz, box, e -> true);
    }

    public static <T extends Entity> List<T> unionEntities(Level level, Class<T> clazz, AABB box, Predicate<? super T> predicate) {
        return ContraptionWorlds.unionEntities((Level)level, clazz, (AABB)box, predicate);
    }

    public boolean isRealWorldEntity(Entity entity) {
        return entity != null && entity.level() != this;
    }

    public static void pushEntity(Level level, Entity entity, Vector localPush) {
        Vector push = localPush;
        ContraptionBoundary boundary = ContraptionBoundary.of((Level)level).orElse(null);
        if (boundary != null && boundary.isRealWorldEntity(entity)) {
            Vec3 rotated = boundary.rotateToRealWorld(new Vec3(localPush.getX(), localPush.getY(), localPush.getZ()));
            push = new Vector(rotated.x, rotated.y, rotated.z);
        }
        CraftEntity bukkit = entity.getBukkitEntity();
        bukkit.setVelocity(bukkit.getVelocity().add(push));
    }

    public static void spawnProcessingOutput(ServerLevel fallbackLevel, Entity inputEntity, double localX, double localY, double localZ, ItemStack output) {
        Level level;
        ServerLevel target = fallbackLevel;
        double x = localX;
        double y = localY;
        double z = localZ;
        ContraptionBoundary boundary = ContraptionBoundary.of((Level)fallbackLevel).orElse(null);
        if (boundary != null && boundary.isRealWorldEntity(inputEntity) && (level = boundary.realLevel()) instanceof ServerLevel) {
            ServerLevel realServerLevel;
            target = realServerLevel = (ServerLevel)level;
            Vec3 p = inputEntity.position();
            x = p.x;
            y = p.y;
            z = p.z;
        }
        ItemEntity spawned = new ItemEntity((Level)target, x, y, z, output);
        spawned.setDeltaMovement(0.0, 0.0, 0.0);
        spawned.setDefaultPickUpDelay();
        target.addFreshEntity((Entity)spawned);
    }

    public void tickChunk(LevelChunk chunk, int randomTickSpeed) {
    }

    public void tickCustomSpawners(boolean spawnEnemies) {
    }

    public void dispose() {
        this.unregisterCapturedMachines();
        this.unloadCeWorld();
        this.releaseChunkTickets();
        this.localPositions.clear();
        MinecraftServer server = this.getServer();
        server.removeLevel((ServerLevel)this);
        ((CraftServer)Bukkit.getServer()).getWorlds().remove(this.getWorld());
        try {
            this.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        Path dir = null;
        try {
            dir = this.storageAccess.getLevelDirectory().path();
            this.storageAccess.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (dir != null) {
            BukkitContraptionLevel.deleteDirAsync(dir);
        }
    }

    private static void deleteDirAsync(Path dir) {
        CLEANUP.execute(() -> {
            try (Stream<Path> walk = Files.walk(dir, new FileVisitOption[0]);){
                walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                });
            }
            catch (IOException iOException) {
                // empty catch block
            }
        });
    }

    private void unloadCeWorld() {
        try {
            CraftEngine.instance().worldManager().unloadWorld((net.momirealms.craftengine.core.world.World)CeWorlds.of((World)this.getWorld()));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void unregisterCapturedMachines() {
        for (BlockPos local : new HashSet<BlockPos>(this.localPositions)) {
            try {
                BlockEntityController blockEntityController;
                net.momirealms.craftengine.core.block.entity.BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)this, (BlockPos)local);
                if (be == null || !((blockEntityController = be.controller) instanceof dev.arubik.craftengine.contraption.api.ContraptionTickable)) continue;
                AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)blockEntityController;
                machine.unregister();
            }
            catch (Throwable throwable) {}
        }
    }

    @Override
    public void tickBlockEntities() {
        CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(this.getWorld().getUID());
        for (BlockPos local : new HashSet<BlockPos>(this.localPositions)) {
            try {
                net.momirealms.craftengine.core.block.entity.BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)this, (BlockPos)local);
                if (be == null || be.controller == null) continue;
                net.minecraft.world.level.block.state.BlockState nms = this.getBlockState(local);
                net.momirealms.craftengine.core.block.ImmutableBlockState ce =
                        net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(nms).orElse(null);
                if (ce == null) continue;
                if (ceWorld != null) {
                    // Every CE block entity via its own ticker — machines, tanks, pumps, pipes.
                    @SuppressWarnings({ "rawtypes", "unchecked" })
                    net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker ticker =
                            be.controller.createBlockEntityTicker(ceWorld, ce);
                    if (ticker != null) {
                        ticker.tick(ceWorld, new net.momirealms.craftengine.core.world.BlockPos(local.getX(),
                                local.getY(), local.getZ()), ce, be.controller);
                    }
                } else if (be.controller instanceof dev.arubik.craftengine.contraption.api.ContraptionTickable tickable) {
                    tickable.tick((Level) this, local, ce);
                }
            }
            catch (Throwable throwable) {}
        }
        // Also tick vanilla block entities (furnace, campfire, etc.) — CE loop only covers CE blocks
        for (BlockPos local : new HashSet<BlockPos>(this.localPositions)) {
            try {
                net.minecraft.world.level.block.entity.BlockEntity be = this.getBlockEntity(local);
                if (be == null) continue;
                net.minecraft.world.level.block.state.BlockState bs = this.getBlockState(local);
                // Skip CE-managed BEs (already ticked above)
                if (net.momirealms.craftengine.bukkit.util.BlockStateUtils
                        .getOptionalCustomBlockState(bs).isPresent()) continue;
                // Use EntityBlock interface if the block implements it
                if (bs.getBlock() instanceof net.minecraft.world.level.block.EntityBlock eb) {
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    net.minecraft.world.level.block.entity.BlockEntityTicker ticker =
                            eb.getTicker(this, bs, be.getType());
                    if (ticker != null) ticker.tick(this, local, bs, be);
                }
            } catch (Throwable ignored) {}
        }
        try {
            dev.arubik.craftengine.fluid.graph.GasEngine.tickAll((Level) this);
        } catch (Throwable ignored) {}
        try {
            dev.arubik.craftengine.fluid.graph.FluidEngine.tickAll((Level) this);
        } catch (Throwable ignored) {}
    }

    private void releaseChunkTickets() {
        try {
            JavaPlugin plugin = JavaPlugin.getPlugin(CraftEnginePolyfills.class);
            for (Long key : this.tickingChunks) {
                int chunkX = ChunkPos.getX((long)key);
                int chunkZ = ChunkPos.getZ((long)key);
                this.getWorld().removePluginChunkTicket(chunkX, chunkZ, (Plugin)plugin);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.tickingChunks.clear();
    }

    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        BlockPos local = new BlockPos(x << 2, y << 2, z << 2);
        BlockPos realPos = BlockPos.containing((Position)this.realWorldPositionOf(local));
        return this.realLevel.getBiome(realPos);
    }

    public Holder<Biome> getNoiseBiome(int x, int y, int z) {
        return this.getUncachedNoiseBiome(x, y, z);
    }

    public void addParticle(ParticleOptions particle, double x, double y, double z, double dx, double dy, double dz) {
        Vec3 realPos = this.realWorldPositionOf(BlockPos.containing((double)x, (double)y, (double)z));
        this.realLevel.addParticle(particle, realPos.x, realPos.y, realPos.z, dx, dy, dz);
    }

    public <T extends ParticleOptions> int sendParticlesSource(List<ServerPlayer> players, Entity source, T particle, boolean overrideLimiter, boolean force, double x, double y, double z, int count, double dx, double dy, double dz, double speed) {
        Level level = this.realLevel;
        if (!(level instanceof ServerLevel)) {
            return 0;
        }
        ServerLevel realServerLevel = (ServerLevel)level;
        Vec3 realPos = this.realWorldPositionOf(new Vec3(x, y, z));
        double rdx = dx;
        double rdy = dy;
        double rdz = dz;
        if (count == 0) {
            Vec3 rotated = this.rotateToRealWorld(new Vec3(dx, dy, dz));
            rdx = rotated.x;
            rdy = rotated.y;
            rdz = rotated.z;
        }
        return realServerLevel.sendParticlesSource(realServerLevel.players(), null, particle, overrideLimiter, force, realPos.x, realPos.y, realPos.z, count, rdx, rdy, rdz, speed);
    }

    public void playSeededSound(Entity source, double x, double y, double z, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed) {
        Vec3 realPos = this.realWorldPositionOf(BlockPos.containing((double)x, (double)y, (double)z));
        this.realLevel.playSeededSound(null, realPos.x, realPos.y, realPos.z, sound, category, volume, pitch, seed);
    }

    public void playSeededSound(Entity source, Entity target, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed) {
    }

    public void levelEvent(Entity source, int type, BlockPos pos, int data) {
        BlockPos realPos = BlockPos.containing((Position)this.realWorldPositionOf(pos));
        this.realLevel.levelEvent(null, type, realPos, data);
    }

    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
        BlockPos realPos = BlockPos.containing((Position)this.realWorldPositionOf(pos));
        this.realLevel.destroyBlockProgress(breakerId, realPos, progress);
    }

    static {
        CLEANUP = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "cep-level-cleanup");
            t.setDaemon(true);
            return t;
        });
    }

    private static final class VoidChunkGenerator
    extends org.bukkit.generator.ChunkGenerator {
        private VoidChunkGenerator() {
        }

        public boolean shouldGenerateNoise() {
            return false;
        }

        public boolean shouldGenerateSurface() {
            return false;
        }

        public boolean shouldGenerateCaves() {
            return false;
        }

        public boolean shouldGenerateDecorations() {
            return false;
        }

        public boolean shouldGenerateMobs() {
            return false;
        }

        public boolean shouldGenerateStructures() {
            return false;
        }
    }

    @Override
    public ServerLevel serverLevel() {
        return this;
    }
}
