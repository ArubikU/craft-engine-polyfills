/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.infernalsuite.asp.api.AdvancedSlimePaperAPI
 *  com.infernalsuite.asp.api.loaders.SlimeLoader
 *  com.infernalsuite.asp.api.world.SlimeWorld
 *  com.infernalsuite.asp.api.world.properties.SlimeProperties
 *  com.infernalsuite.asp.api.world.properties.SlimeProperty
 *  com.infernalsuite.asp.api.world.properties.SlimePropertyMap
 *  com.infernalsuite.asp.level.SlimeBootstrap
 *  com.infernalsuite.asp.level.SlimeLevelInstance
 *  com.mojang.serialization.Lifecycle
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
 *  net.minecraft.server.WorldLoader$DataLoadContext
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.ProgressListener
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Display
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Interaction
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelSettings
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.dimension.LevelStem
 *  net.minecraft.world.level.entity.EntityTypeTest
 *  net.minecraft.world.level.gamerules.GameRuleMap
 *  net.minecraft.world.level.gamerules.GameRules
 *  net.minecraft.world.level.levelgen.WorldOptions
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.storage.PrimaryLevelData
 *  net.minecraft.world.level.storage.PrimaryLevelData$SpecialWorldProperty
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.ticks.TickPriority
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.plugin.CraftEngine
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.ChunkPos
 *  net.momirealms.craftengine.core.world.World
 *  net.momirealms.craftengine.core.world.chunk.CEChunk
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package dev.arubik.craftengine.contraption.level;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.loaders.SlimeLoader;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.api.world.properties.SlimeProperties;
import com.infernalsuite.asp.api.world.properties.SlimeProperty;
import com.infernalsuite.asp.api.world.properties.SlimePropertyMap;
import com.infernalsuite.asp.level.SlimeBootstrap;
import com.infernalsuite.asp.level.SlimeLevelInstance;
import com.mojang.serialization.Lifecycle;
import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.contraption.api.ContraptionTickable;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.config.ContraptionConfig;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.level.ContraptionBoundary;
import dev.arubik.craftengine.contraption.level.NoopSlimeLoader;
import dev.arubik.craftengine.fluid.graph.FluidEngine;
import dev.arubik.craftengine.fluid.graph.GasEngine;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.util.CeWorlds;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
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
import net.minecraft.server.WorldLoader;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.TickPriority;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.chunk.CEChunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public final class AspContraptionLevel
extends SlimeLevelInstance
implements ContraptionBoundary,
ContraptionLevel {
    public static volatile boolean UNION_REAL_ENTITIES = true;
    private Level realLevel;
    private final Set<BlockPos> localPositions = new HashSet<BlockPos>();
    private final Set<Long> tickingChunks = new HashSet<Long>();
    private final Map<BlockPos, byte[]> ceControllerData = new HashMap<BlockPos, byte[]>();
    private final List<long[]> glueEdgesLocal = new ArrayList<long[]>();
    private final List<ContraptionLevel.FurnitureRecord> furnitureRecords = new ArrayList<ContraptionLevel.FurnitureRecord>();
    private boolean cellsDirty = true;
    private int footprintMinX = Integer.MAX_VALUE;
    private int footprintMinY = Integer.MAX_VALUE;
    private int footprintMinZ = Integer.MAX_VALUE;
    private int footprintMaxX = Integer.MIN_VALUE;
    private int footprintMaxY = Integer.MIN_VALUE;
    private int footprintMaxZ = Integer.MIN_VALUE;
    private double realX;
    private double realY;
    private double realZ;
    private double realYaw;
    private double realPitch;
    private double realRoll;
    private double realScale = 1.0;

    private AspContraptionLevel(Level realLevel, SlimeBootstrap bootstrap, PrimaryLevelData worldData, ResourceKey<Level> worldKey, ResourceKey<LevelStem> dimension, LevelStem stem, World.Environment environment) throws IOException {
        super(bootstrap, worldData, worldKey, dimension, stem, environment);
        this.realLevel = realLevel;
        this.noSave = true;
    }

    public static AspContraptionLevel create(Level realLevel, double x, double y, double z, double yawRadians) {
        try {
            AdvancedSlimePaperAPI asp = AdvancedSlimePaperAPI.instance();
            SlimePropertyMap props = new SlimePropertyMap();
            props.setValue((SlimeProperty)SlimeProperties.ENVIRONMENT, "NORMAL");
            props.setValue((SlimeProperty)SlimeProperties.DIFFICULTY, "peaceful");
            props.setValue((SlimeProperty)SlimeProperties.ALLOW_MONSTERS, false);
            props.setValue((SlimeProperty)SlimeProperties.ALLOW_ANIMALS, false);
            props.setValue((SlimeProperty)SlimeProperties.PVP, false);
            String name = "cep_contraption_" + String.valueOf(UUID.randomUUID());
            SlimeWorld world = asp.createEmptyWorld(name, false, props, (SlimeLoader)NoopSlimeLoader.INSTANCE);
            SlimeBootstrap bootstrap = new SlimeBootstrap(world);
            PrimaryLevelData worldData = AspContraptionLevel.createWorldData(name);
            ResourceKey dimension = LevelStem.OVERWORLD;
            ResourceKey worldKey = ResourceKey.create((ResourceKey)Registries.DIMENSION, (Identifier)Identifier.parse((String)name.toLowerCase(Locale.ENGLISH)));
            LevelStem stem = (LevelStem)((Holder.Reference)MinecraftServer.getServer().registries().compositeAccess().lookupOrThrow(Registries.LEVEL_STEM).get(dimension).orElseThrow()).value();
            AspContraptionLevel level = new AspContraptionLevel(realLevel, bootstrap, worldData, (ResourceKey<Level>)worldKey, (ResourceKey<LevelStem>)dimension, stem, World.Environment.NORMAL);
            MinecraftServer mc = MinecraftServer.getServer();
            mc.initWorld((ServerLevel)level, level.serverLevelData, level.serverLevelData.worldGenOptions());
            mc.addLevel((ServerLevel)level);
            level.setTransform(x, y, z, yawRadians);
            return level;
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to create AspContraptionLevel", e);
        }
    }

    private static PrimaryLevelData createWorldData(String worldName) {
        DedicatedServer mc = (DedicatedServer)MinecraftServer.getServer();
        WorldLoader.DataLoadContext context = mc.worldLoaderContext;
        GameRules rules = new GameRules(context.dataConfiguration().enabledFeatures(), GameRuleMap.of());
        rules.set(GameRules.RANDOM_TICK_SPEED, 0, null);
        rules.set(GameRules.SPAWN_MOBS, false, null);
        rules.set(GameRules.ADVANCE_TIME, false, null);
        rules.set(GameRules.ADVANCE_WEATHER, false, null);
        rules.set(GameRules.RAIDS, false, null);
        rules.set(GameRules.SPAWNER_BLOCKS_WORK, false, null);
        LevelSettings settings = new LevelSettings(worldName, GameType.SURVIVAL, false, Difficulty.PEACEFUL, true, rules, context.dataConfiguration());
        WorldOptions options = new WorldOptions(0L, false, false);
        PrimaryLevelData data = new PrimaryLevelData(settings, options, PrimaryLevelData.SpecialWorldProperty.FLAT, Lifecycle.stable());
        data.checkName(worldName);
        data.setModdedInfo(mc.getServerModName(), mc.getModdedStatus().shouldReportAsModified());
        data.setInitialized(true);
        return data;
    }

    @Override
    public ServerLevel serverLevel() {
        return this;
    }

    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        this.cellsDirty = true;
        return super.setBlock(pos, state, flags, recursionLeft);
    }

    @Override
    public void putBlock(BlockPos local, BlockState state) {
        this.putBlock(local, state, false);
    }

    @Override
    public void putBlock(BlockPos local, BlockState state, boolean quiet) {
        this.ensureChunkTicking(local);
        this.setBlock(local, state, quiet ? 50 : 3);
        this.localPositions.add(local.immutable());
        this.growFootprint(local);
        this.activateCeChunk(local);
    }

    @Override
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
        Iterator<Long> iterator = touchedChunks.iterator();
        while (iterator.hasNext()) {
            long chunkKey = (Long)(iterator.next());
            this.activateCeChunk(new BlockPos(ChunkPos.getX((long)chunkKey) << 4, 0, ChunkPos.getZ((long)chunkKey) << 4));
        }
    }

    @Override
    public void markCellsDirty() {
        this.cellsDirty = true;
    }

    @Override
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

    @Override
    public void ensureChunkReady(BlockPos local) {
        this.ensureChunkTicking(local);
        this.activateCeChunk(local);
    }

    @Override
    public void putBlockEntity(BlockPos local, CompoundTag nbt) {
        BlockEntity be = BlockEntity.loadStatic((BlockPos)local, (BlockState)this.getBlockState(local), (CompoundTag)nbt, (HolderLookup.Provider)this.registryAccess());
        if (be != null) {
            be.setLevel((Level)this);
            this.setBlockEntity(be);
        }
    }

    @Override
    public CompoundTag saveBlockEntity(BlockPos local) {
        BlockEntity be = this.getBlockEntity(local);
        return be != null ? be.saveWithFullMetadata((HolderLookup.Provider)this.registryAccess()) : null;
    }

    @Override
    public void putCeControllerData(BlockPos local, byte[] bytes) {
        if (bytes != null) {
            this.ceControllerData.put(local.immutable(), bytes);
        }
    }

    @Override
    public byte[] getCeControllerData(BlockPos local) {
        return this.ceControllerData.get(local);
    }

    @Override
    public Set<BlockPos> localPositions() {
        return Collections.unmodifiableSet(this.localPositions);
    }

    @Override
    public int blockCount() {
        return this.localPositions.size();
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

    private void ensureChunkTicking(BlockPos local) {
        try {
            int chunkX = local.getX() >> 4;
            int chunkZ = local.getZ() >> 4;
            if (this.tickingChunks.add(ChunkPos.asLong((int)chunkX, (int)chunkZ))) {
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

    @Override
    public List<long[]> glueEdgesLocal() {
        return Collections.unmodifiableList(this.glueEdgesLocal);
    }

    @Override
    public void setGlueEdgesLocal(List<long[]> edges) {
        this.glueEdgesLocal.clear();
        if (edges != null) {
            for (long[] e : edges) {
                if (e == null || e.length != 2) continue;
                this.glueEdgesLocal.add(new long[]{e[0], e[1]});
            }
        }
    }

    @Override
    public List<ContraptionLevel.FurnitureRecord> furnitureRecords() {
        return Collections.unmodifiableList(this.furnitureRecords);
    }

    @Override
    public void addFurnitureRecord(ContraptionLevel.FurnitureRecord record) {
        if (record != null) {
            this.furnitureRecords.add(record);
        }
    }

    @Override
    public void setFurnitureRecords(List<ContraptionLevel.FurnitureRecord> records) {
        this.furnitureRecords.clear();
        if (records != null) {
            this.furnitureRecords.addAll(records);
        }
    }

    @Override
    public void setTransform(double x, double y, double z, double yawRadians) {
        this.realX = x;
        this.realY = y;
        this.realZ = z;
        this.realYaw = yawRadians;
    }

    @Override
    public void setTransform(double x, double y, double z, double yawRadians, double pitchRadians) {
        this.setTransform(x, y, z, yawRadians);
        this.realPitch = pitchRadians;
    }

    @Override
    public void setTransform(double x, double y, double z, double yawRadians, double pitchRadians, double scale) {
        this.setTransform(x, y, z, yawRadians, pitchRadians);
        this.realScale = scale;
    }

    @Override
    public void setTransform(double x, double y, double z, double yawRadians, double pitchRadians, double rollRadians, double scale) {
        this.setTransform(x, y, z, yawRadians, pitchRadians);
        this.realRoll = rollRadians;
        this.realScale = scale;
    }

    @Override
    public void setScaleFactor(double scale) {
        this.realScale = scale;
    }

    @Override
    public double realScaleFactor() {
        return this.realScale;
    }

    @Override
    public void reanchor(Level newRealLevel, double x, double y, double z, double yawRadians) {
        if (newRealLevel != null) {
            this.realLevel = newRealLevel;
        }
        this.setTransform(x, y, z, yawRadians);
    }

    @Override
    public Vec3 realWorldPositionOf(BlockPos local) {
        return ContraptionMath.renderPosition(local, new Vec3(this.realX, this.realY, this.realZ), this.realYaw, this.realPitch, this.realRoll, this.realScale);
    }

    @Override
    public Vec3 realWorldPositionOf(Vec3 local) {
        return ContraptionMath.renderPosition(local, new Vec3(this.realX, this.realY, this.realZ), this.realYaw, this.realPitch, this.realRoll, this.realScale);
    }

    @Override
    public double realYawRadians() {
        return this.realYaw;
    }

    @Override
    public double realPitchRadians() {
        return this.realPitch;
    }

    @Override
    public double realRollRadians() {
        return this.realRoll;
    }

    @Override
    public Quaternionf realOrientationOf(Quaternionf local) {
        Quaternionf bearing = new Quaternionf().rotateY((float)(-this.realYaw)).rotateX((float)this.realPitch).rotateZ((float)this.realRoll);
        return local != null ? bearing.mul((Quaternionfc)local, new Quaternionf()) : bearing;
    }

    @Override
    public List<Player> realViewers(BlockPos local) {
        return this.realViewers(this.realWorldPositionOf(local));
    }

    @Override
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

    @Override
    public Level realLevel() {
        return this.realLevel;
    }

    @Override
    public Vec3 rotateToRealWorld(Vec3 localDirection) {
        return ContraptionMath.rotateYawPitchRoll(localDirection, this.realYaw, this.realPitch, this.realRoll);
    }

    private AABB localBoxToRealWorld(AABB local) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        for (double cx : new double[]{local.minX, local.maxX}) {
            for (double cy : new double[]{local.minY, local.maxY}) {
                for (double cz : new double[]{local.minZ, local.maxZ}) {
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

    @Override
    public <T extends Entity> List<T> getLocalEntities(Class<T> clazz, AABB box) {
        return this.getLocalEntities(clazz, box, e -> true);
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
        if (!(this.realLevel instanceof ServerLevel)) {
            return;
        }
        CraftWorld realBukkitWorld = ((ServerLevel)this.realLevel).getWorld();
        ArrayList<Entity> snapshot = new ArrayList();
        this.getAllEntities().forEach(snapshot::add);
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

    public void tickChunk(LevelChunk chunk, int randomTickSpeed) {
    }

    public void tickCustomSpawners(boolean spawnEnemies) {
    }

    public void scheduleTick(BlockPos pos, Fluid fluid, int delay, TickPriority priority) {
        if (ContraptionConfig.get().simulateFluidFlow()) {
            super.scheduleTick(pos, fluid, delay, priority);
        }
    }

    public void save(ProgressListener progress, boolean flush, boolean skipSave, boolean close) {
    }

    public void saveIncrementally(boolean doFull) {
    }

    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        BlockPos local = new BlockPos(x << 2, y << 2, z << 2);
        BlockPos realPos = BlockPos.containing((Position)this.realWorldPositionOf(local));
        return this.realLevel.getBiome(realPos);
    }

    public Holder<Biome> getNoiseBiome(int x, int y, int z) {
        return this.getUncachedNoiseBiome(x, y, z);
    }

    @Override
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

    @Override
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

    @Override
    public void dispose() {
        this.unregisterCapturedMachines();
        this.unloadCeWorld();
        this.releaseChunkTickets();
        this.localPositions.clear();
        try {
            Bukkit.unloadWorld((World)this.getWorld(), (boolean)false);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
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
                net.momirealms.craftengine.core.block.entity.BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)this, local);
                BlockEntityController controller = be == null ? null : be.controller;
                if (!(controller instanceof AbstractMachineBlockEntity)) continue;
                AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)controller;
                machine.unregister();
            }
            catch (Throwable throwable) {}
        }
    }

    @Override
    public void tickBlockEntities() {
        ContraptionTickable tickable;
        net.momirealms.craftengine.core.block.entity.BlockEntity be2;
        CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(this.getWorld().getUID());
        for (BlockPos local : new HashSet<BlockPos>(this.localPositions)) {
            try {
                BlockState nms;
                ImmutableBlockState ce;
                be2 = BukkitBlockEntityTypes.getIfLoaded((Level)this, local);
                if (be2 == null || be2.controller == null || (ce = (ImmutableBlockState)BlockStateUtils.getOptionalCustomBlockState((nms = this.getBlockState(local))).orElse(null)) == null) continue;
                if (ceWorld != null) {
                    net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker ticker = be2.controller.createBlockEntityTicker(ceWorld, ce);
                    if (ticker == null) continue;
                    ticker.tick(ceWorld, new net.momirealms.craftengine.core.world.BlockPos(local.getX(), local.getY(), local.getZ()), ce, be2.controller);
                    continue;
                }
                BlockEntityController blockEntityController = be2.controller;
                if (!(blockEntityController instanceof ContraptionTickable)) continue;
                tickable = (ContraptionTickable)blockEntityController;
                tickable.tick((Level)this, local, ce);
            }
            catch (Throwable throwable) {}
        }
        for (BlockPos local : new HashSet<BlockPos>(this.localPositions)) {
            try {
                EntityBlock eb;
                BlockEntityTicker ticker;
                BlockState bs;
                be2 = this.getBlockEntity(local);
                if (be2 == null || BlockStateUtils.getOptionalCustomBlockState((bs = this.getBlockState(local))).isPresent() || !((tickable = bs.getBlock()) instanceof EntityBlock) || (ticker = (eb = (EntityBlock)tickable).getTicker((Level)this, bs, be2.getType())) == null) continue;
                ticker.tick((Level)this, local, bs, (BlockEntity)be2);
            }
            catch (Throwable throwable) {}
        }
        try {
            GasEngine.tickAll((Level)this);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            FluidEngine.tickAll((Level)this);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void releaseChunkTickets() {
        try {
            JavaPlugin plugin = JavaPlugin.getPlugin(CraftEnginePolyfills.class);
            for (Long key : this.tickingChunks) {
                this.getWorld().removePluginChunkTicket(ChunkPos.getX((long)key), ChunkPos.getZ((long)key), (Plugin)plugin);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.tickingChunks.clear();
    }
}

