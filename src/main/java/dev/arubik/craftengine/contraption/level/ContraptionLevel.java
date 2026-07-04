package dev.arubik.craftengine.contraption.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.joml.Quaternionf;

import com.mojang.serialization.Lifecycle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.ContraptionMath;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;

/**
 * One contraption's real, actual mini {@link ServerLevel} — a genuine
 * {@code net.minecraft.world.level.Level} (via {@code ServerLevel}), not a data proxy,
 * specifically so any code holding a plain {@code Level}/{@code ServerLevel} reference
 * during normal ticking can do {@code level instanceof ContraptionLevel} and know it's
 * inside a contraption. A bare {@code Level} subclass isn't viable on this server
 * ({@code Level}'s only constructor unconditionally does
 * {@code new CraftWorld((ServerLevel) this, ...)} — confirmed via {@code javap} against
 * this project's own mapped server jar), so this extends {@link ServerLevel} instead: the
 * cast trivially succeeds because this class genuinely IS one, and — since
 * {@link ServerLevel} is fully concrete — normal chunk loading, block-entity ticking, and
 * entity AI/physics all run completely unmodified; nothing here needs to be reimplemented.
 *
 * <p>One dedicated instance per contraption (mirrors Create's own one-{@code Level}-per-
 * contraption model), local offsets ({@code local}) map 1:1 onto this level's own block
 * positions — no shared cell grid, no collision risk between contraptions. Bearing-relative
 * capture (see {@code ContraptionCapture}) still applies at the local/{@link #realLevel}
 * boundary via {@link #getBlockState}/{@link #putBlock}/etc.
 *
 * <p><b>Real vs. fake positions</b>: local offsets here ARE this level's real block
 * positions (the "fake" address space, since this level itself isn't the original world).
 * {@link #realWorldPositionOf} is the continuous "as if this were still really there"
 * position in {@link #realLevel}, computed from the bearing's live transform
 * ({@link #setTransform}) — this is what {@link #getUncachedNoiseBiome}/{@link #addParticle}/
 * {@link #playSeededSound}/{@link #destroyBlockProgress} resolve against, so a fan's
 * particles or a mixer's sound reproduce at the contraption's true in-flight location even
 * though the block generating them physically lives in this mini level.
 *
 * <p><b>Ticking.</b> A freshly-created {@link ServerLevel} has zero chunk tickets, so vanilla
 * never promotes any of its chunks past {@code FullChunkStatus.FULL} — vanilla block-entity
 * tickers ({@code Level#tickBlockEntities}) are gated on {@code FullChunkStatus.BLOCK_TICKING}
 * (confirmed against this project's own mache-decompiled 1.21.11 sources:
 * {@code ServerLevel#shouldTickBlocksAt} -&gt; {@code DistanceManager#inBlockTickingRange}, purely
 * ticket-driven, no natural promotion without one), so without a ticket a captured furnace
 * would never cook even though {@link #putBlockEntity} correctly registers its ticker via
 * {@code Level#setBlockEntity} (which itself DOES register a ticker — verified via
 * {@code LevelChunk#addAndRegisterBlockEntity} -&gt; {@code updateBlockEntityTicker}). Likewise
 * CraftEngine's OWN block-entity controllers (funnel, machines, ...) only get a ticker
 * registered when their owning {@code CEChunk#activated} is true ({@code CEChunk#addBlockEntity}
 * gates ticker creation on it), and activation is normally player-chunk-tracking-driven — which
 * never happens here. {@link #putBlock} fixes both: it force-loads the target chunk with a
 * permanent plugin chunk ticket (so vanilla ticks it) and activates that chunk's {@code CEChunk}
 * directly via its public {@code activateAllBlockEntities()} (confirmed callable from outside
 * CraftEngine's own package — see that method's javadoc). {@link #dispose} releases the tickets.
 *
 * <p><b>CraftEngine's own built-in block-entity-renderer visuals</b> ({@code entity-renderer}
 * config, e.g. {@code ItemDisplayBlockEntityElement}) DO render correctly for a block sitting
 * inside a {@link ContraptionLevel} via {@code ContraptionBlockEntityElementMirror}, which reads
 * each {@code BlockEntity}'s already-constructed {@code renderer().elements()} directly (built
 * eagerly at {@code BlockEntity} construction time, independent of {@code CEChunk} activation —
 * confirmed against CraftEngine's own {@code BlockEntityController#gatherElements}) and redirects
 * spawn/position packets through {@link #realWorldPositionOf}/{@link #realViewers} itself, rather
 * than relying on CraftEngine's own chunk-tracker-driven {@code updateConstantRenderers()} path.
 * The one prerequisite — a live {@code CEWorld} for this level's Bukkit world, and CraftEngine's
 * ByteBuddy section injection having run for its chunks so custom-block {@code BlockEntity}
 * objects actually get constructed on {@code setBlock} — is satisfied by {@link #putBlock}
 * proactively resolving/creating the {@code CEWorld} (via {@code WorldManager#getWorld}'s lazy
 * self-healing fallback, the same mechanism CraftEngine ships for worlds that never fired
 * {@code WorldInitEvent}) before ever calling {@code setBlock}, so section injection is already
 * wired up by the time real blocks land.
 */
public final class ContraptionLevel extends ServerLevel {

    /**
     * Global kill-switch / scoping flag for the {@link #getEntities} dual-world union (see that method).
     * When {@code true} (default) a captured block-entity's local entity scan ALSO returns the real-world
     * entities in its transformed real-world region — the behavior the copper fan needs to blow/process
     * real mobs/items. Set {@code false} to fall back to pure fake-level queries everywhere (the vanilla
     * behavior) if the union is ever found to disrupt a vanilla-internal AI/collision scan that happens to
     * run inside a contraption dimension. Callers that ALWAYS want fake-only, regardless of this flag, use
     * {@link #getLocalEntities} instead.
     */
    public static volatile boolean UNION_REAL_ENTITIES = true;

    private final Level realLevel;
    private final Set<BlockPos> localPositions = new HashSet<>();

    /**
     * Captured-footprint bounding box, in local coordinates, EXPANDED by {@link #FOOTPRINT_MARGIN}
     * in every direction -- recorded lazily from the first batch of {@link #putBlock} calls
     * (capture time) and used by {@link #refreshLocalPositions()} to bound how far a live block
     * change (e.g. a piston pushing a block into a previously-air cell) is allowed to grow
     * {@link #localPositions}. Without a bound, {@link VoidChunkGenerator} lets a piston with
     * nothing to stop it push a block arbitrarily far into permanently-air void; this cap doesn't
     * stop the underlying vanilla piston push itself (out of scope -- see class javadoc), it only
     * ensures such a block is never tracked/rendered once it leaves the sane working volume
     * around the original structure.
     */
    private int footprintMinX = Integer.MAX_VALUE, footprintMinY = Integer.MAX_VALUE, footprintMinZ = Integer.MAX_VALUE;
    private int footprintMaxX = Integer.MIN_VALUE, footprintMaxY = Integer.MIN_VALUE, footprintMaxZ = Integer.MIN_VALUE;
    private static final int FOOTPRINT_MARGIN = 2;

    /** Chunk keys ({@code net.minecraft.world.level.ChunkPos#asLong}) already given a permanent plugin ticket — see {@link #ensureChunkTicking}. */
    private final Set<Long> tickingChunks = new HashSet<>();

    /**
     * CraftEngine-controlled block entities (chest-like {@code StorageBlockEntity} and any
     * other {@code PersistentBlockEntity}-backed CE custom block) do NOT live behind a real
     * vanilla {@link BlockEntity} — CraftEngine's own controllers are bookkept in a parallel
     * {@code CEChunk}, reached only via {@code BukkitBlockEntityTypes.getIfLoaded}/
     * {@code PersistentBlockEntity.getIfLoaded} (see {@code ContraptionCapture} javadoc). This
     * level itself has no CE world registration, so a captured CE controller's data can't be
     * carried by replaying a {@code setBlock} here the way {@link #putBlockEntity} does for
     * genuine vanilla block entities. Instead {@link dev.arubik.craftengine.contraption.ContraptionCapture#capture}
     * stores each CE controller's serialized bytes (see
     * {@code PersistentBlockEntity#serializeToBytes}) here directly, keyed by local pos, and
     * {@code restore}/{@code restoreWithCollisionCheck} feed them back into the freshly
     * recreated controller at the real world position via {@code PersistentBlockEntity#loadFromBytes}.
     */
    private final Map<BlockPos, byte[]> ceControllerData = new HashMap<>();

    /**
     * The captured structure's internal glue topology, in LOCAL coordinates (2026-07-03 session —
     * "has que las glue persista al apagar o reiniciar... persistir los glue block en el nbt del
     * contraption"). Each entry is a {@code {BlockPos.asLong(localA), BlockPos.asLong(localB)}}
     * undirected edge among this contraption's own cells, captured at assembly time from the real
     * world's {@link dev.arubik.craftengine.contraption.GlueRegistry} and carried here so it can be
     * (a) serialized alongside the blocks by {@code ContraptionStructureNbt} — surviving restart in
     * the bearing's saved NBT, since the volatile in-memory registry does not — and (b) re-applied
     * to the world glue graph on disassemble so every block re-sticks exactly where it lands. Empty
     * for a legacy contraption captured before this existed; disassemble then falls back to gluing
     * all restored cells into one component ("siempre todos sus bloques se peguen").
     */
    private final List<long[]> glueEdgesLocal = new ArrayList<>();

    /**
     * Captured CraftEngine furniture metadata, in LOCAL coordinates (2026-07-03 session — "todos
     * los craft engine furnitures se pierden al reiniciar el sv"). The live {@code BukkitFurniture}
     * objects live inside this throwaway dimension and are NOT persisted by vanilla (noSave), so
     * their definition/variant/local-offset/yaw are recorded here to ride along in the bearing's
     * structure NBT (serialized by {@code ContraptionStructureNbt}) and be re-placed on rehydrate.
     * Populated at capture ({@code ContraptionFurnitureCapture#captureNear}) and on load.
     */
    private final List<FurnitureRecord> furnitureRecords = new ArrayList<>();

    /** Serializable snapshot of one captured furniture piece — see {@link #furnitureRecords}. */
    public record FurnitureRecord(String definitionId, String variantName, double lx, double ly, double lz, float yaw) {
    }

    private double realX, realY, realZ, realYaw;

    private ContraptionLevel(Level realLevel, MinecraftServer server, java.util.concurrent.Executor executor,
            LevelStorageSource.LevelStorageAccess storageAccess, PrimaryLevelData levelData,
            ResourceKey<Level> dimensionKey, LevelStem levelStem, boolean isDebug, long seed,
            List<net.minecraft.world.level.CustomSpawner> customSpawners, boolean tickTime,
            RandomSequences randomSequences, World.Environment environment,
            org.bukkit.generator.ChunkGenerator bukkitGenerator, org.bukkit.generator.BiomeProvider biomeProvider) {
        super(server, executor, storageAccess, levelData, dimensionKey, levelStem, isDebug, seed, customSpawners,
                tickTime, randomSequences, environment, bukkitGenerator, biomeProvider);
        this.realLevel = realLevel;
        // Zero disk footprint for this hidden dimension (2026-07-03 "el contraption se debe guardar solo
        // en su .nbt"). The contraption's ONLY persistence is its structure NBT (ContraptionStructureNbt,
        // stored on the bearing) — never region/level.dat files for this throwaway dimension. `noSave` is
        // vanilla's own per-level save-suppression flag: MinecraftServer's periodic autosave and the
        // shutdown save both skip any level whose noSave is true (verified via javap — the save CALLERS
        // gate on it), so setting it here keeps chunks/block-entities fully functional in RAM while the
        // contraption is live but stops anything ever being written to disk. The save(...) overrides below
        // are belt-and-suspenders for any direct/plugin save call that bypasses the noSave gate.
        this.noSave = true;
    }

    /**
     * Hard no-op: this hidden contraption dimension must never write region/level.dat files to disk (see
     * constructor's {@code noSave} note). Overriding the full 4-arg {@code save} — the sink every other
     * {@code save}/{@code saveIncrementally}/autosave/shutdown path funnels into — guarantees nothing is
     * persisted even if some caller bypasses the {@code noSave} flag. In-memory chunk/block-entity state
     * is untouched; only the DISK write is suppressed. All real persistence is the structure NBT.
     */
    @Override
    public void save(net.minecraft.util.ProgressListener progress, boolean flush, boolean skipSave,
            boolean close) {
        // intentionally empty — no disk write for a contraption dimension
    }

    @Override
    public void saveIncrementally(boolean doFull) {
        // intentionally empty — no disk write for a contraption dimension
    }

    /**
     * Bootstraps a fresh, fully-registered mini dimension for one contraption — a real
     * {@link ServerLevel} (this class), given its own storage folder/dimension key (per
     * instance, never reused) so multiple contraptions never collide. {@code x,y,z,yawRadians}
     * is the bearing's initial real-world transform (see {@link #setTransform}).
     */
    public static ContraptionLevel create(Level realLevel, double x, double y, double z, double yawRadians) {
        try {
            CraftServer craftServer = (CraftServer) org.bukkit.Bukkit.getServer();
            MinecraftServer server = craftServer.getServer();
            ServerLevel overworld = server.overworld();

            String id = "cep_contraption_" + UUID.randomUUID();
            Identifier dimensionId = Identifier.fromNamespaceAndPath("cep", id);
            ResourceKey<Level> dimensionKey = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimensionId);
            ResourceKey<LevelStem> levelStemKey = ResourceKey.create(net.minecraft.core.registries.Registries.LEVEL_STEM, dimensionId);
            LevelStem levelStem = new LevelStem(overworld.dimensionTypeRegistration(),
                    overworld.getChunkSource().getGenerator());

            GameRules gameRules = new GameRules(FeatureFlags.DEFAULT_FLAGS);
            LevelSettings levelSettings = new LevelSettings(id, GameType.SURVIVAL, false,
                    net.minecraft.world.Difficulty.PEACEFUL, false, gameRules, WorldDataConfiguration.DEFAULT);
            WorldOptions worldOptions = new WorldOptions(0L, false, false);
            PrimaryLevelData levelData = new PrimaryLevelData(levelSettings, worldOptions,
                    PrimaryLevelData.SpecialWorldProperty.NONE, Lifecycle.stable());

            LevelStorageSource.LevelStorageAccess storageAccess = server.storageSource.parent().createAccess(id, levelStemKey);

            // NOTE: do NOT call craftServer.addWorld(level.getWorld()) here — ServerLevel's own
            // constructor (confirmed via javap against this project's mapped/patched Paper server
            // jar, net.minecraft.server.level.ServerLevel's ctor, the very last thing it does) ALREADY
            // ends with `getCraftServer().addWorld(this.getWorld())`, so the Bukkit-side registration
            // happens automatically the instant `new ContraptionLevel(...)` (below) returns — an
            // explicit second call here re-registers the SAME UUID a moment later and trips
            // CraftServer#addWorld's own "is this UUID already known" duplicate guard against the
            // registration the constructor itself just performed, printing the misleading "World ...
            // is a duplicate of another world" log and silently no-opping (leaving the Bukkit World
            // correctly registered from the constructor's call, but confirmed once via this comment
            // rather than relied upon implicitly again).
            ContraptionLevel level = new ContraptionLevel(realLevel, server, server.executor, storageAccess, levelData,
                    dimensionKey, levelStem, false, 0L, List.of(), true, new RandomSequences(),
                    World.Environment.CUSTOM, new VoidChunkGenerator(), null);
            level.setTransform(x, y, z, yawRadians);

            server.addLevel(level);
            return level;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create ContraptionLevel", e);
        }
    }

    public void putBlock(BlockPos local, BlockState state) {
        putBlock(local, state, false);
    }

    /**
     * {@code quiet=true} suppresses vanilla's neighbor/shape-update machinery entirely while
     * placing an entire captured structure's worth of blocks in one batch -- used by
     * {@link dev.arubik.craftengine.contraption.ContraptionCapture#capture} and
     * {@link dev.arubik.craftengine.contraption.persistence.ContraptionStructureNbt#load}.
     * Without this, a support-dependent block (lever, redstone dust, ...) placed before its
     * supporting neighbor -- inevitable given {@code Set<BlockPos>} iteration order is
     * unspecified -- gets its own {@code canSurvive}/shape-update check run against neighbors
     * that are still air (not yet placed, just not placed YET), and vanilla immediately pops it
     * off into a dropped item even though the support block IS coming later in the same batch.
     *
     * <p><b>Verified against the real flag constants</b> (decompiled/javap'd from this project's
     * own mapped server jar, {@code net.minecraft.world.level.block.Block}):
     * {@code UPDATE_NEIGHBORS=1}, {@code UPDATE_CLIENTS=2}, {@code UPDATE_KNOWN_SHAPE=16},
     * {@code UPDATE_SUPPRESS_DROPS=32}. An EARLIER fix used flag {@code 2} alone
     * ({@code UPDATE_CLIENTS} only, skipping bit {@code 1}) on the theory that skipping
     * {@code UPDATE_NEIGHBORS} would be enough -- confirmed WRONG by reading
     * {@code Level#notifyAndUpdatePhysics}'s actual bytecode: bit {@code 1} only gates
     * {@code updateNeighborsAt}/{@code updateNeighbourForOutputSignal} (neighbor block-updates).
     * The support/shape-check pass that actually pops an unsupported lever/redstone-dust off
     * ({@code BlockState#updateShape} via {@code updateNeighbourShapes}/
     * {@code updateIndirectNeighbourShapes}) is a SEPARATE branch gated on
     * {@code (flags & UPDATE_KNOWN_SHAPE) == 0} -- i.e. it still runs at flag {@code 2}, since
     * {@code 2} doesn't set bit {@code 16}. That's why the lever still popped after the earlier
     * fix. The real quiet flag has to ALSO set {@code UPDATE_KNOWN_SHAPE (16)} to skip that
     * branch, plus {@code UPDATE_SUPPRESS_DROPS (32)} as defense-in-depth so even if some other
     * path calls {@code Block.dropResources} during this batch, it's a no-op. Final quiet flags:
     * {@code 2 | 16 | 32 = 50}. No real client ever renders this level directly (see class
     * javadoc), so skipping all of this is safe -- nothing here depends on redstone/support
     * blocks reacting instantly to each individual placement during a capture/restore batch,
     * only on the end state being correct once every block has landed.
     */
    public void putBlock(BlockPos local, BlockState state, boolean quiet) {
        ensureChunkTicking(local);
        int flags = quiet
                ? (net.minecraft.world.level.block.Block.UPDATE_CLIENTS
                        | net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE
                        | net.minecraft.world.level.block.Block.UPDATE_SUPPRESS_DROPS)
                : net.minecraft.world.level.block.Block.UPDATE_ALL;
        setBlock(local, state, flags);
        localPositions.add(local.immutable());
        growFootprint(local);
        activateCeChunk(local);
    }

    /** Expands the captured footprint bounds (see {@link #footprintMinX} javadoc) to include {@code local}. */
    private void growFootprint(BlockPos local) {
        footprintMinX = Math.min(footprintMinX, local.getX());
        footprintMinY = Math.min(footprintMinY, local.getY());
        footprintMinZ = Math.min(footprintMinZ, local.getZ());
        footprintMaxX = Math.max(footprintMaxX, local.getX());
        footprintMaxY = Math.max(footprintMaxY, local.getY());
        footprintMaxZ = Math.max(footprintMaxZ, local.getZ());
    }

    /** Whether {@code local} lies within the captured footprint's bounds plus {@link #FOOTPRINT_MARGIN}. */
    private boolean withinFootprint(BlockPos local) {
        if (footprintMinX > footprintMaxX) {
            return true; // no footprint recorded yet (e.g. freshly-created/empty level) -- don't block anything
        }
        return local.getX() >= footprintMinX - FOOTPRINT_MARGIN && local.getX() <= footprintMaxX + FOOTPRINT_MARGIN
                && local.getY() >= footprintMinY - FOOTPRINT_MARGIN && local.getY() <= footprintMaxY + FOOTPRINT_MARGIN
                && local.getZ() >= footprintMinZ - FOOTPRINT_MARGIN && local.getZ() <= footprintMaxZ + FOOTPRINT_MARGIN;
    }

    /**
     * Re-syncs {@link #localPositions} against this level's LIVE block contents (CONTRAPTIONS.md
     * "contraption render never updates when blocks are added/removed live" bug fix) -- called
     * periodically by {@link dev.arubik.craftengine.contraption.ContraptionEntity#rebuildSwarm()}
     * (itself driven every tick by {@code ContraptionEngine.tickAll}). {@link #putBlock} alone
     * only ever ADDS to {@link #localPositions} at initial-capture time; nothing previously
     * updated it when something INSIDE the level changed on its own afterwards -- e.g. a piston
     * moving a block from a tracked cell to a previously-untracked one, which would otherwise stay
     * permanently invisible/hitbox-less forever.
     *
     * <p>Scans exactly the tracked cells plus their immediate 6-neighbors (cheap: bounded by the
     * current occupied set's size, not the whole footprint volume) so a block freshly pushed one
     * cell over from a tracked position is discovered, while a block that became air is dropped.
     * Growth beyond the original captured footprint is capped via {@link #withinFootprint} (see
     * {@link #footprintMinX} javadoc) -- a cell outside that bound is simply never (re)tracked
     * even if vanilla physically placed a block there.
     */
    public void refreshLocalPositions() {
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
            if (!withinFootprint(pos)) {
                continue;
            }
            if (!getBlockState(pos).isAir()) {
                BlockPos immutable = pos.immutable();
                nowOccupied.add(immutable);
                growFootprint(immutable); // a block that moved further within-margin still counts as captured territory
            }
        }

        localPositions.clear();
        localPositions.addAll(nowOccupied);
    }

    /**
     * Force-loads and permanently tickets the chunk at {@code local} so vanilla actually
     * promotes it to {@code FullChunkStatus.BLOCK_TICKING} (see class javadoc "Ticking") —
     * without this, {@link #putBlockEntity}'s {@code setBlockEntity} call registers a ticker
     * that vanilla's own {@code Level#tickBlockEntities} would otherwise permanently skip.
     * Idempotent per chunk (Bukkit's plugin-ticket API itself dedupes per-plugin/per-chunk).
     */
    private void ensureChunkTicking(BlockPos local) {
        try {
            int chunkX = local.getX() >> 4;
            int chunkZ = local.getZ() >> 4;
            long key = net.minecraft.world.level.ChunkPos.asLong(chunkX, chunkZ);
            if (tickingChunks.add(key)) {
                getWorld().addPluginChunkTicket(chunkX, chunkZ,
                        org.bukkit.plugin.java.JavaPlugin.getPlugin(CraftEnginePolyfills.class));
            }
        } catch (Throwable ignored) {
            // best-effort — a ticket failure shouldn't abort block placement
        }
    }

    /**
     * Forces CraftEngine's own {@code CEChunk} for {@code local} to activate (see class
     * javadoc "Ticking") so any CraftEngine-custom block-entity controller placed there
     * (funnel, machines, ...) gets a real ticker registered too — CraftEngine only does this
     * automatically via real player chunk-tracking, which never happens inside this level.
     * Also proactively resolves/creates this level's {@code CEWorld} (CraftEngine's own lazy
     * self-healing {@code WorldManager#getWorld} fallback) BEFORE any block is placed, so
     * CraftEngine's ByteBuddy section injection is already wired up by the time {@code setBlock}
     * runs — otherwise a CE custom block placed here would never even get its
     * {@code BlockEntity}/renderer constructed (see class javadoc's entity-renderer paragraph).
     */
    private void activateCeChunk(BlockPos local) {
        try {
            CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(getWorld().getUID());
            if (ceWorld == null) {
                return;
            }
            net.momirealms.craftengine.core.world.chunk.CEChunk ceChunk =
                    ceWorld.getChunkAtIfLoaded(local.getX() >> 4, local.getZ() >> 4);
            if (ceChunk != null) {
                ceChunk.activateAllBlockEntities();
            }
        } catch (Throwable ignored) {
            // best-effort — CraftEngine-custom activation failure shouldn't abort block placement
        }
    }

    /**
     * Public entry point for non-block content that still needs this level's chunk actually
     * force-loaded/ticking before it's placed — e.g. {@code ContraptionFurnitureCapture} placing
     * a real CraftEngine furniture entity at a local position that may have no captured block at
     * all (a wall/ceiling-anchored piece's own anchor cell is frequently pure air once the
     * supporting block itself is a NEIGHBOR cell, and standalone floor furniture sits ON TOP of
     * its supporting block's cell, one Y higher — a position {@link #putBlock} was never called
     * for). Idempotent per chunk exactly like {@link #putBlock}'s own internal call to this.
     */
    public void ensureChunkReady(BlockPos local) {
        ensureChunkTicking(local);
        activateCeChunk(local);
    }

    /** Reconstructs a real, ticking {@link BlockEntity} from captured NBT at {@code local}. */
    public void putBlockEntity(BlockPos local, CompoundTag nbt) {
        BlockEntity be = BlockEntity.loadStatic(local, getBlockState(local), nbt, registryAccess());
        if (be != null) {
            be.setLevel(this);
            setBlockEntity(be);
        }
    }

    /** The real, currently-ticking block-entity's saved NBT at {@code local}, or null if it has none. */
    public CompoundTag saveBlockEntity(BlockPos local) {
        BlockEntity be = getBlockEntity(local);
        return be != null ? be.saveWithFullMetadata(registryAccess()) : null;
    }

    /** Stores a captured CE controller's serialized bytes at {@code local} — see {@link #ceControllerData}. */
    public void putCeControllerData(BlockPos local, byte[] bytes) {
        if (bytes != null) {
            ceControllerData.put(local.immutable(), bytes);
        }
    }

    /** The captured CE controller bytes at {@code local}, or null if that cell has none. */
    public byte[] getCeControllerData(BlockPos local) {
        return ceControllerData.get(local);
    }

    public Set<BlockPos> localPositions() {
        return Collections.unmodifiableSet(localPositions);
    }

    /** The captured structure's internal glue edges, in LOCAL coords — see {@link #glueEdgesLocal}. */
    public List<long[]> glueEdgesLocal() {
        return Collections.unmodifiableList(glueEdgesLocal);
    }

    /** Replaces the stored glue topology (local edges). See {@link #glueEdgesLocal} — called at capture and on load. */
    public void setGlueEdgesLocal(List<long[]> edges) {
        glueEdgesLocal.clear();
        if (edges != null) {
            for (long[] e : edges) {
                if (e != null && e.length == 2) {
                    glueEdgesLocal.add(new long[] {e[0], e[1]});
                }
            }
        }
    }

    /** Captured furniture metadata — see {@link #furnitureRecords}. */
    public List<FurnitureRecord> furnitureRecords() {
        return Collections.unmodifiableList(furnitureRecords);
    }

    /** Records one captured furniture piece (called during capture). */
    public void addFurnitureRecord(FurnitureRecord record) {
        if (record != null) {
            furnitureRecords.add(record);
        }
    }

    /** Replaces all furniture records (called on load from the structure NBT). */
    public void setFurnitureRecords(List<FurnitureRecord> records) {
        furnitureRecords.clear();
        if (records != null) {
            furnitureRecords.addAll(records);
        }
    }

    public int blockCount() {
        return localPositions.size();
    }

    /** Updates the bearing's live real-world transform — called every tick position/yaw changes (see {@code ContraptionState}). */
    public void setTransform(double x, double y, double z, double yawRadians) {
        this.realX = x;
        this.realY = y;
        this.realZ = z;
        this.realYaw = yawRadians;
    }

    /** Where {@code local} would be right now if the structure were still really standing in {@link #realLevel}. */
    public Vec3 realWorldPositionOf(BlockPos local) {
        return ContraptionMath.renderPosition(local, new Vec3(realX, realY, realZ), realYaw);
    }

    /**
     * Continuous-position overload of {@link #realWorldPositionOf(BlockPos)} — for callers
     * rendering something at a fractional local-space position (e.g. an item mid-travel on a
     * conveyor belt, or a fluid display centred inside a cell) rather than a whole block.
     *
     * <p><b>2026-07-02 pivot fix.</b> This used to rotate {@code local} directly around local
     * {@code (0,0,0)} then translate by the bearing's real position — the exact same
     * corner-instead-of-center pivot bug {@link ContraptionMath#renderPosition} itself had (see
     * that method's javadoc for the full root-cause writeup). Now delegates to the already-fixed
     * {@link ContraptionMath#renderPosition(Vec3, Vec3, double)} instead of duplicating the
     * rotate+translate here with the old wrong pivot — keeps every local-offset-to-real-world
     * conversion in the codebase going through the one corrected implementation.
     */
    public Vec3 realWorldPositionOf(Vec3 local) {
        return ContraptionMath.renderPosition(local, new Vec3(realX, realY, realZ), realYaw);
    }

    /** The bearing's current real-world yaw, radians (see {@link #setTransform}). */
    public double realYawRadians() {
        return realYaw;
    }

    /**
     * Composes a local-space orientation (e.g. a conveyor item's own facing/slope rotation)
     * with the bearing's current real-world yaw, so a rotating contraption visibly rotates
     * everything riding it, not just translates it. {@code local} may be {@code null} for "no
     * local orientation" (bearing yaw only). Convention: {@link ContraptionMath#rotateYaw}
     * rotates (x,z) by {@code +yawRadians} using {@code rx = x*cos - z*sin}; JOML's
     * {@code Quaternionf#rotateY(a)} rotates the same axes by {@code -a} in that basis, so the
     * matching bearing quaternion is {@code rotateY(-realYaw)}.
     */
    public Quaternionf realOrientationOf(Quaternionf local) {
        Quaternionf bearing = new Quaternionf().rotateY((float) -realYaw);
        return local != null ? bearing.mul(local, new Quaternionf()) : bearing;
    }

    /**
     * Real players who should see something rendered at {@code local} (this level's own
     * chunk-tracking is always empty — see class javadoc — so this resolves against
     * {@link #realLevel}'s tracking at {@link #realWorldPositionOf(BlockPos)} instead). The
     * shared redirect every packet-only renderer that lives inside a contraption should use
     * instead of calling {@code BukkitWorld#getTrackedBy} against this level directly.
     */
    public List<Player> realViewers(BlockPos local) {
        return realViewers(realWorldPositionOf(local));
    }

    /** {@link #realViewers(BlockPos)} for a continuous real-world position (already translated). */
    public List<Player> realViewers(Vec3 realPos) {
        try {
            org.bukkit.World bukkitWorld = ((ServerLevel) realLevel).getWorld();
            int chunkX = ((int) Math.floor(realPos.x)) >> 4;
            int chunkZ = ((int) Math.floor(realPos.z)) >> 4;
            return new BukkitWorld(bukkitWorld).getTrackedBy(new ChunkPos(chunkX, chunkZ));
        } catch (Throwable t) {
            return List.of();
        }
    }

    /**
     * The original world this contraption was captured from — for callers that need to reach it
     * directly (e.g. to push real-world entities near the contraption, not just ones physically
     * inside this mini level) rather than going through one of this class's own bridging overrides.
     */
    public Level realLevel() {
        return realLevel;
    }

    /**
     * Safety net for real entities still alive inside this level at teardown time (e.g. a mob
     * that wandered/was carried in) — {@code dispose()} discards this whole dimension instantly
     * with no vanilla save/transfer of its own, which would otherwise silently delete anything
     * still living here. Dropped items are handled separately (a real, already-in-the-real-world
     * mirror entity — see {@code ContraptionItemPickupSwarm}) and are skipped here so they aren't
     * double-created; everything else gets moved into the real world at its current mirrored
     * position via Bukkit's own cross-world teleport (handles the dimension change safely for any
     * entity type, unlike hand-rolling an NBT copy). Call this BEFORE {@link #dispose()}.
     */
    public void transferRemainingEntitiesToRealWorld() {
        if (!(realLevel instanceof ServerLevel)) {
            return;
        }
        org.bukkit.World realBukkitWorld = ((ServerLevel) realLevel).getWorld();
        java.util.List<net.minecraft.world.entity.Entity> snapshot = new java.util.ArrayList<>();
        for (net.minecraft.world.entity.Entity e : getAllEntities()) {
            snapshot.add(e);
        }
        for (net.minecraft.world.entity.Entity entity : snapshot) {
            if (entity instanceof net.minecraft.world.entity.item.ItemEntity || entity.isRemoved()) {
                continue;
            }
            // Never dump captured CraftEngine furniture (its meta Display + Interaction colliders)
            // into the real world (2026-07-03 — "todos los craft engine furnitures se pierden al
            // reiniciar"). Furniture is persisted via ContraptionLevel#furnitureRecords in the
            // structure NBT and re-placed on rehydrate; teleporting the live fake-level instances out
            // here would both leak duplicates into the real world AND orphan CraftEngine's tracking.
            // These entity types are furniture render/collider artifacts, never a "mob that wandered
            // in" (the actual thing this rescue transfer exists for), so skipping them is safe.
            if (entity instanceof net.minecraft.world.entity.Display
                    || entity instanceof net.minecraft.world.entity.Interaction) {
                continue;
            }
            try {
                Vec3 realPos = realWorldPositionOf(entity.position());
                org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
                bukkitEntity.teleport(new org.bukkit.Location(realBukkitWorld, realPos.x, realPos.y, realPos.z));
            } catch (Throwable ignored) {
                // best-effort — a single misbehaving entity shouldn't block the rest of teardown
            }
        }
    }

    /** Rotates (NOT translates) a local-space direction/velocity vector into the bearing's current real-world orientation. */
    public Vec3 rotateToRealWorld(Vec3 localDirection) {
        return ContraptionMath.rotateYaw(localDirection, realYaw);
    }

    /**
     * Maps a LOCAL-space axis-aligned box to the smallest real-world axis-aligned box that contains it
     * under the bearing's live transform (rotate about the bearing center + translate). Because a yaw
     * rotation turns an AABB into an oriented box, its 8 corners are transformed individually and the
     * result is their real-world bounding box — used to widen a captured block-entity's own local entity
     * query into the real-world region it currently occupies (see {@link #getEntities}).
     */
    private net.minecraft.world.phys.AABB localBoxToRealWorld(net.minecraft.world.phys.AABB local) {
        double[] xs = { local.minX, local.maxX };
        double[] ys = { local.minY, local.maxY };
        double[] zs = { local.minZ, local.maxZ };
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        for (double cx : xs) {
            for (double cy : ys) {
                for (double cz : zs) {
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
        return new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * <b>Dual-world entity query.</b> A captured block-entity (e.g. the copper fan) ticks inside this
     * hidden level and finds its targets with a local-space AABB scan. By default such a scan only ever
     * returns entities physically inside this fake dimension, so a captured fan blows/pushes/processes ONLY
     * co-captured belts/items/mobs and never touches the real world it's flying through.
     *
     * <p><b>IMPORTANT — enter via {@link #unionEntities}, NOT {@code getEntitiesOfClass}.</b> On this
     * Moonrise-patched Paper server {@code EntityGetter#getEntitiesOfClass} does NOT route through this
     * {@code getEntities(EntityTypeTest, AABB, Predicate)} seam — it calls the chunk-system entity lookup
     * directly and bypasses this override entirely (see {@link #unionEntities}'s javadoc for the javap
     * proof; an earlier revision wrongly asserted the delegation held and so the fan's push silently saw
     * fake-only entities). This overload IS a genuine virtual seam, so any effect block that wants the
     * dual-world union must call {@link #unionEntities} (which routes here) rather than
     * {@code getEntitiesOfClass}.
     *
     * <p>The user's intended model is DUAL-WORLD for the fan's physical effects (push + item processing):
     * it must affect BOTH the entities co-captured inside the contraption AND the real-world entities in
     * its real-world blow volume. Overriding this one seam delivers exactly that with the fan's OWN query
     * unchanged: return the fake-level results (super) UNION the same-typed real-world entities found in
     * the {@linkplain #localBoxToRealWorld transformed real-world region} of the query box. The fan then
     * pushes/processes every returned entity via its Bukkit handle ({@code setVelocity}, {@code setItem},
     * {@code discard}) — which act on each entity in whichever world it truly lives, so real entities move
     * in the real world and captured ones move inside the contraption, from a single unmodified scan.
     *
     * <p><b>Local-space effects that need the transform.</b> Two of the fan's effects are computed in this
     * level's LOCAL frame and so are wrong for the real-world entities this union hands back at the bearing's
     * yaw-rotated transform: the push VELOCITY (a local direction) and a recipe's OUTPUT SPAWN (at a local
     * cell position). Rather than make the fan transform-aware, the apply-time counterparts to this
     * query-time union — {@link #pushEntity} and {@link #spawnProcessingOutput} — take the fan's local-space
     * effect PER ENTITY and land it correctly: {@code pushEntity} rotates the push by the live
     * {@link #realYawRadians() bearing yaw} for a real-world target (raw for a fake co-captured one), and
     * {@code spawnProcessingOutput} drops a real input's product at its real-world position (a fake input's
     * inside this level). The fan stays a pure local-space actor; every transform/real-vs-fake decision
     * lives here in the bridge alongside this query. (Rotation-invariant effects — freeze/slow potion
     * applications — need no bridging and are applied directly on the Bukkit handle as before.)
     */
    @Override
    public <T extends net.minecraft.world.entity.Entity> java.util.List<T> getEntities(
            net.minecraft.world.level.entity.EntityTypeTest<net.minecraft.world.entity.Entity, T> typeTest,
            net.minecraft.world.phys.AABB box, java.util.function.Predicate<? super T> predicate) {
        java.util.List<T> fake = super.getEntities(typeTest, box, predicate);
        if (!UNION_REAL_ENTITIES || !(realLevel instanceof ServerLevel realServerLevel)) {
            return fake;
        }
        net.minecraft.world.phys.AABB realBox;
        try {
            realBox = localBoxToRealWorld(box);
        } catch (Throwable t) {
            return fake;
        }
        java.util.List<T> real = realServerLevel.getEntities(typeTest, realBox, predicate);
        if (real.isEmpty()) {
            return fake;
        }
        java.util.List<T> union = new java.util.ArrayList<>(fake.size() + real.size());
        union.addAll(fake);
        union.addAll(real);
        return union;
    }

    /**
     * FAKE-LEVEL-ONLY entity query — exactly the pre-override vanilla behavior, never unioned with the
     * real world. This is the escape hatch the {@link #getEntities} dual-world override needs: any caller
     * that must see ONLY this contraption's own captured/interior entities (e.g. items a captured funnel
     * dropped INSIDE the contraption, the level's own physics/pickup housekeeping, or this project's own
     * swarm/carry code when it deliberately wants fake-only contents) calls this instead of the overridden
     * {@code getEntities}/{@code getEntitiesOfClass}, so it isn't handed real-world entities it would then
     * double-process or mishandle. Mirrors {@code getEntitiesOfClass}'s own signature for a drop-in swap.
     */
    public <T extends net.minecraft.world.entity.Entity> java.util.List<T> getLocalEntities(
            Class<T> clazz, net.minecraft.world.phys.AABB box, java.util.function.Predicate<? super T> predicate) {
        return super.getEntities(net.minecraft.world.level.entity.EntityTypeTest.forClass(clazz), box, predicate);
    }

    /** {@link #getLocalEntities(Class, net.minecraft.world.phys.AABB, java.util.function.Predicate)} with a keep-all predicate. */
    public <T extends net.minecraft.world.entity.Entity> java.util.List<T> getLocalEntities(
            Class<T> clazz, net.minecraft.world.phys.AABB box) {
        return getLocalEntities(clazz, box, e -> true);
    }

    /**
     * <b>The dual-world query a captured effect block should call</b> instead of
     * {@code level.getEntitiesOfClass(...)}. Routes through the {@link #getEntities(net.minecraft.world.level.entity.EntityTypeTest,
     * net.minecraft.world.phys.AABB, java.util.function.Predicate) EntityTypeTest seam} that this class
     * overrides to add the real-world union — so the caller actually receives real-world entities when it
     * runs inside a contraption.
     *
     * <p><b>Why this indirection exists (Moonrise bypass).</b> On this Paper server the chunk-system
     * (Moonrise) patch makes {@code EntityGetter#getEntitiesOfClass(Class, AABB, Predicate)} — which is
     * {@code final} — call {@code moonrise$getEntityLookup().getEntities(...)} DIRECTLY (confirmed via javap
     * against this project's own mapped server jar: {@code Level#getEntitiesOfClass}'s bytecode invokes
     * {@code EntityLookup.getEntities(Class, Entity, AABB, List, Predicate)}, NOT any overridable
     * {@code Level#getEntities}). So a captured block calling {@code getEntitiesOfClass} silently BYPASSES
     * the {@link #getEntities} union override and only ever sees fake-level entities — which is exactly why
     * the fan rendered particles into the real world (a different, working redirect) yet pushed NOTHING out
     * there. Only the {@code EntityTypeTest}-shaped {@code getEntities} overload is a real virtual seam this
     * class can override, so effect blocks must enter through it via this helper. For a plain
     * {@link ServerLevel} (fan not in a contraption) this is byte-for-byte equivalent to
     * {@code getEntitiesOfClass}.
     */
    public static <T extends net.minecraft.world.entity.Entity> java.util.List<T> unionEntities(
            net.minecraft.world.level.Level level, Class<T> clazz, net.minecraft.world.phys.AABB box,
            java.util.function.Predicate<? super T> predicate) {
        return level.getEntities(net.minecraft.world.level.entity.EntityTypeTest.forClass(clazz), box, predicate);
    }

    // ---- local-space effect bridging (companion to the getEntities dual-world union) ----
    //
    // The getEntities override above hands a captured effect-block (the copper fan) a SINGLE list
    // mixing fake-level entities (which live in THIS level's local coordinate frame) and real-world
    // entities (which live in realLevel at the bearing's yaw-rotated transform). Anything that block
    // computes in LOCAL space — a push velocity, an item-processing output position — is correct for
    // the fake entities but wrong for the real ones, which sit rotated/translated by the live bearing
    // transform. These two helpers are the apply-time counterpart to the query-time union: the effect
    // block routes its local-space effect through here PER ENTITY, and this level (the one place that
    // knows the transform AND can tell which world each entity truly lives in) lands it correctly in
    // whichever world that entity belongs to. This keeps every scrap of contraption-awareness in the
    // bridge — the effect block stays a pure local-space actor and never sees a yaw or a realLevel.

    /**
     * Whether {@code entity} is a REAL-world entity handed to a captured block via the {@link #getEntities}
     * union (i.e. it lives in {@link #realLevel}, not inside this fake dimension). Fake/co-captured entities
     * return {@code false} — their local coordinate frame already IS this level's, so no transform applies.
     */
    private boolean isRealWorldEntity(net.minecraft.world.entity.Entity entity) {
        return entity != null && entity.level() != this;
    }

    /**
     * <b>Residual 1 fix — yaw-correct push.</b> Adds a LOCAL-space push velocity to {@code entity}'s
     * existing velocity, rotating it into the bearing's live real-world orientation FIRST iff the entity
     * is a real-world one from the {@link #getEntities} union (see {@link #rotateToRealWorld}); a fake
     * co-captured entity keeps the raw local push (its own frame is already this level's local frame).
     * Adds to the current velocity via the entity's Bukkit handle, matching the pre-existing fan idiom
     * ({@code setVelocity(getVelocity().add(push))}).
     *
     * <p>Static so a captured effect block can call it uniformly whether or not it happens to be running
     * inside a contraption: when {@code level} is a plain {@link ServerLevel} the push is applied raw
     * (there is no bearing to rotate against), so the caller needs no {@code instanceof} of its own.
     */
    public static void pushEntity(net.minecraft.world.level.Level level, net.minecraft.world.entity.Entity entity,
            org.bukkit.util.Vector localPush) {
        org.bukkit.util.Vector push = localPush;
        if (level instanceof ContraptionLevel cl && cl.isRealWorldEntity(entity)) {
            Vec3 rotated = cl.rotateToRealWorld(new Vec3(localPush.getX(), localPush.getY(), localPush.getZ()));
            push = new org.bukkit.util.Vector(rotated.x, rotated.y, rotated.z);
        }
        org.bukkit.entity.Entity bukkit = entity.getBukkitEntity();
        bukkit.setVelocity(bukkit.getVelocity().add(push));
    }

    /**
     * <b>Residual 2 fix — output lands in the input's own world.</b> Spawns a recipe {@code output}
     * ItemEntity that was produced by processing {@code inputEntity}. If the input was a REAL-world item
     * from the {@link #getEntities} union, the output is spawned into {@link #realLevel} at the input's
     * OWN real-world position (so the processed product appears right next to it in the real world, not
     * hidden inside this fake dimension). If the input is a genuinely fake co-captured item — or the
     * effect block isn't inside a contraption at all — the output is spawned into {@code fallbackLevel}
     * at the given local coordinates, exactly as before.
     *
     * <p>{@code inputEntity} may be {@code null} (e.g. an output not tied to a specific dropped item, such
     * as a belt conversion), in which case the fake/local path is used.
     */
    public static void spawnProcessingOutput(net.minecraft.server.level.ServerLevel fallbackLevel,
            net.minecraft.world.entity.Entity inputEntity, double localX, double localY, double localZ,
            net.minecraft.world.item.ItemStack output) {
        net.minecraft.server.level.ServerLevel target = fallbackLevel;
        double x = localX, y = localY, z = localZ;
        if (fallbackLevel instanceof ContraptionLevel cl && cl.isRealWorldEntity(inputEntity)
                && cl.realLevel instanceof net.minecraft.server.level.ServerLevel realServerLevel) {
            // Real input: drop the product at the input's actual real-world position (it already lives
            // there), NOT at the fan's local cell center — which would be an unrelated point in this
            // hidden dimension. The input entity's own position() is already in real-world coordinates.
            target = realServerLevel;
            Vec3 p = inputEntity.position();
            x = p.x;
            y = p.y;
            z = p.z;
        }
        net.minecraft.world.entity.item.ItemEntity spawned =
                new net.minecraft.world.entity.item.ItemEntity(target, x, y, z, output);
        spawned.setDeltaMovement(0, 0, 0);
        spawned.setDefaultPickUpDelay();
        target.addFreshEntity(spawned);
    }

    /** Tears down this contraption's dedicated dimension entirely (disassembly / discard). */
    public void dispose() {
        // Order matters. FIRST run the machine-specific teardown (menus/renderers/BetterModel trackers a
        // machine holds open that CraftEngine's generic BE teardown never closes) while the block entities
        // are still resolvable, THEN unload CraftEngine's own CEWorld for this dimension — that single call
        // is what actually STOPS the emitter: it invalidates every captured block-entity's ticker and stops
        // the CEWorld tick task, so nothing captured keeps ticking/emitting once we're through here. Only
        // after both do we drop the NMS level + Bukkit world.
        unregisterCapturedMachines();
        unloadCeWorld();
        releaseChunkTickets();
        localPositions.clear();
        MinecraftServer server = getServer();
        server.removeLevel(this);
        ((CraftServer) org.bukkit.Bukkit.getServer()).getWorlds().remove(getWorld());
        try {
            close();
        } catch (Exception ignored) {
        }
    }

    /**
     * Unloads THIS contraption dimension's CraftEngine {@code CEWorld} through CraftEngine's own
     * {@code WorldManager#unloadWorld} — the generalizing core of the leftover-emitter fix
     * (2026-07-02 "al deconstruir/borrar un contraption debe borrar consigo el mundo, particulas y
     * todo lo que quede").
     *
     * <p>Root cause: a captured machine block-entity (the copper fan) is NOT ticked by the NMS
     * {@code ServerLevel} — it is ticked by CraftEngine's OWN per-{@code CEWorld} tick task
     * ({@code CEWorld.syncTickingBlockEntities}, driven by {@code CEWorld#setTicking}). {@code dispose()}
     * previously only did {@code server.removeLevel(this)} + removed the Bukkit world from the world
     * list; NEITHER of those tells CraftEngine to unload the {@code CEWorld}, so its tick task kept
     * running and kept ticking (and emitting particles from) every captured machine forever -> the
     * leftover/orphan emitter. {@code AbstractMachineBlockEntity#unregister()} does not help here either:
     * it never invalidates the backing {@code BlockEntity}, and CraftEngine's tick loop only self-evicts
     * a ticker once its {@code BlockEntity#isValid()} returns false.
     *
     * <p>{@code WorldManager#unloadWorld} (verified via {@code javap} against this project's craft-engine
     * jar) is the exact choke point that fixes this for ALL captured block-entity types, not just the fan:
     * it removes the {@code CEWorld} from the manager map, calls {@code CEWorld#setTicking(false)} (stops
     * the tick task -> no more machine ticks/emission), and unloads every loaded chunk (which invalidates
     * and removes each block-entity's ticker + dynamic renderer). Called BEFORE the Bukkit world is dropped
     * from the world list so its {@code getLoadedChunks()} iteration inside {@code unloadWorld} still
     * resolves. Any future machine type is covered automatically — nothing captured survives this call.
     */
    private void unloadCeWorld() {
        try {
            CraftEngine.instance().worldManager().unloadWorld(new BukkitWorld(getWorld()));
        } catch (Throwable ignored) {
            // best-effort — a CraftEngine world-unload hiccup must not block NMS level disposal
        }
    }

    /**
     * Runs the machine-SPECIFIC teardown ({@code AbstractMachineBlockEntity#unregister()}) for every
     * captured machine block-entity (the copper fan) living in this level before the dimension is
     * discarded — the restore-side twin of the capture-side {@code ContraptionCapture#removeFromWorld}
     * unregister. This closes state that CraftEngine's generic block-entity teardown never touches: the
     * machine's open menu, its BetterModel renderer/tracker, etc.
     *
     * <p>Note: this is NOT what stops the fan ticking/emitting — that is {@link #unloadCeWorld}, which
     * invalidates every captured block-entity's CraftEngine ticker and stops the {@code CEWorld} tick
     * task (see its javadoc for the root cause). This method only frees the per-machine resources CE's
     * unload can't know about, and must therefore run BEFORE {@link #unloadCeWorld} while the block
     * entities are still resolvable. The freshly-restored real-world fan is a brand-new block-entity and
     * is unaffected by either.
     */
    private void unregisterCapturedMachines() {
        for (BlockPos local : new HashSet<>(localPositions)) {
            try {
                net.momirealms.craftengine.core.block.entity.BlockEntity be =
                        dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(this, local);
                if (be != null && be.controller instanceof
                        dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity machine) {
                    machine.unregister();
                }
            } catch (Throwable ignored) {
                // best-effort — a single machine's teardown hiccup must not block level disposal
            }
        }
    }

    /** Releases every permanent plugin chunk ticket {@link #ensureChunkTicking} requested — see {@link #dispose}. */
    private void releaseChunkTickets() {
        try {
            org.bukkit.plugin.Plugin plugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(CraftEnginePolyfills.class);
            for (Long key : tickingChunks) {
                int chunkX = net.minecraft.world.level.ChunkPos.getX(key);
                int chunkZ = net.minecraft.world.level.ChunkPos.getZ(key);
                getWorld().removePluginChunkTicket(chunkX, chunkZ, plugin);
            }
        } catch (Throwable ignored) {
            // best-effort — the whole level is about to be removed regardless
        }
        tickingChunks.clear();
    }

    // ---- real-world bridging: everything below resolves against the live bearing transform ----

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

    @Override
    public void addParticle(ParticleOptions particle, double x, double y, double z, double dx, double dy, double dz) {
        Vec3 realPos = realWorldPositionOf(BlockPos.containing(x, y, z));
        realLevel.addParticle(particle, realPos.x, realPos.y, realPos.z, dx, dy, dz);
    }

    /**
     * Server-broadcast particle redirect — the {@code Level#addParticle} override just above only catches
     * the pure-NMS {@code addParticle} path; a plugin that spawns particles via Bukkit
     * {@code World#spawnParticle} (e.g. the captured copper fan's airflow stream, see
     * {@code FanMachineBlockEntity#applyPushAndParticles}) goes through
     * {@code CraftWorld#spawnParticle -> ServerLevel#sendParticlesSource} instead (verified via javap
     * against this project's mapped server jar: {@code CraftWorld#spawnParticle} with a null explicit
     * player list resolves the recipient list to {@code getHandle().players()} — i.e. THIS level's own
     * player list, which is always empty for a contraption's hidden dimension, so the particle reaches
     * nobody). Overriding {@code sendParticlesSource} here fixes every such Bukkit-side emitter uniformly,
     * exactly like the {@code playSeededSound}/{@code addParticle} redirects already do for their APIs:
     * translate the spawn position to the bearing's live real-world transform and re-broadcast against the
     * REAL level using ITS players (each of whom passes vanilla's own {@code player.level()==this} +
     * 32-block distance cull inside {@code ServerLevel#sendParticles}). When {@code count==0} the client
     * treats {@code dx/dy/dz} as a single particle's world-space drift VELOCITY (this is how the fan
     * animates its directional airflow stream) — so that vector is rotated into the bearing's real-world
     * orientation via {@link #rotateToRealWorld}, otherwise a fan streaming local-north would visibly
     * blow the wrong way on a rotated contraption. For {@code count>0} the same fields are per-axis
     * spread magnitudes (rotation-invariant), so they're passed through unchanged.
     */
    @Override
    public <T extends ParticleOptions> int sendParticlesSource(java.util.List<net.minecraft.server.level.ServerPlayer> players,
            net.minecraft.world.entity.Entity source, T particle, boolean overrideLimiter, boolean force,
            double x, double y, double z, int count, double dx, double dy, double dz, double speed) {
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
        return realServerLevel.sendParticlesSource(realServerLevel.players(), null, particle, overrideLimiter,
                force, realPos.x, realPos.y, realPos.z, count, rdx, rdy, rdz, speed);
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
        // Entity-to-entity variant has no meaningful in-level position to translate; drop it —
        // callers that want a real-world-audible sound should use the BlockPos-anchored overload.
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

    /** All-air, no terrain/structures/mobs — a contraption's mini level only ever holds explicitly-placed blocks. */
    private static final class VoidChunkGenerator extends org.bukkit.generator.ChunkGenerator {
        @Override
        public boolean shouldGenerateNoise() {
            return false;
        }

        @Override
        public boolean shouldGenerateSurface() {
            return false;
        }

        @Override
        public boolean shouldGenerateCaves() {
            return false;
        }

        @Override
        public boolean shouldGenerateDecorations() {
            return false;
        }

        @Override
        public boolean shouldGenerateMobs() {
            return false;
        }

        @Override
        public boolean shouldGenerateStructures() {
            return false;
        }
    }
}
