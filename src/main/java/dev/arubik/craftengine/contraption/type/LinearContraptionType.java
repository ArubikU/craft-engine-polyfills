/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtAccounter
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 */
package dev.arubik.craftengine.contraption.type;

import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

public class LinearContraptionType
implements ContraptionType {
    public static final LinearContraptionType INSTANCE = new LinearContraptionType();
    private static final Map<ResourceKey<Level>, Map<Long, ExtendedSolidState>> EXTENDED_SOLIDS = new ConcurrentHashMap<ResourceKey<Level>, Map<Long, ExtendedSolidState>>();

    private LinearContraptionType() {
    }

    public static void recordExtendedSolid(ResourceKey<Level> worldId, BlockPos bodyPos, Vec3 facing, int distance, double speed, double su, PistonBearingBehavior.Mode mode, long delay, Set<BlockPos> positions, Set<BlockPos> shaftPositions, boolean initialRedstone) {
        EXTENDED_SOLIDS.computeIfAbsent(worldId, k -> new ConcurrentHashMap()).put(bodyPos.asLong(), new ExtendedSolidState(worldId, bodyPos, facing, distance, speed, su, mode, delay, new HashSet<BlockPos>(positions), new HashSet<BlockPos>(shaftPositions), initialRedstone));
    }

    public static void forgetExtendedSolid(ResourceKey<Level> worldId, BlockPos bodyPos) {
        Map<Long, ExtendedSolidState> w = EXTENDED_SOLIDS.get(worldId);
        if (w != null) {
            w.remove(bodyPos.asLong());
        }
    }

    public static boolean isExtendedSolid(ResourceKey<Level> worldId, BlockPos bodyPos) {
        Map<Long, ExtendedSolidState> w = EXTENDED_SOLIDS.get(worldId);
        return w != null && w.containsKey(bodyPos.asLong());
    }

    public static void tickExtendedSolids() {
        for (Map.Entry<ResourceKey<Level>, Map<Long, ExtendedSolidState>> we : EXTENDED_SOLIDS.entrySet()) {
            DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
            ServerLevel level = server.getLevel(we.getKey());
            if (level == null) continue;
            CraftWorld world = level.getWorld();
            ArrayList<ExtendedSolidState> triggered = null;
            for (ExtendedSolidState e : we.getValue().values()) {
                boolean fire;
                boolean redstone;
                try {
                    redstone = level.hasNeighborSignal(e.bodyPos);
                }
                catch (Throwable t) {
                    continue;
                }
                boolean rising = redstone && !e.prevRedstone;
                e.prevRedstone = redstone;
                if (e.mode == PistonBearingBehavior.Mode.ROUND_ROBIN) {
                    if (redstone) {
                        fire = ++e.timer >= Math.max(1L, e.delay);
                    } else {
                        e.timer = 0L;
                        fire = false;
                    }
                } else {
                    fire = rising;
                }
                if (!fire) continue;
                if (triggered == null) {
                    triggered = new ArrayList<ExtendedSolidState>();
                }
                triggered.add(e);
            }
            if (triggered == null) continue;
            for (ExtendedSolidState e : triggered) {
                we.getValue().remove(e.bodyPos.asLong());
                try {
                    for (BlockPos sp : e.shaftPositions) {
                        try {
                            level.setBlock(sp, Blocks.AIR.defaultBlockState(), 3);
                        }
                        catch (Throwable throwable) {}
                    }
                    ContraptionEntity entity = ContraptionAssembler.assembleExplicitRetracting((World)world, e.bodyPos, e.positions, e.facing, e.distance, e.speed, e.su, e.mode, e.delay);
                    if (entity == null) continue;
                    BearingHammerListener.markAssembled(e.worldId, e.bodyPos, entity.state().id());
                }
                catch (Throwable t) {
                    Bukkit.getLogger().warning("[Contraption] euler re-assemble failed: " + String.valueOf(t));
                }
            }
        }
    }

    public static void saveExtendedSolids(Path file) throws IOException {
        CompoundTag root = new CompoundTag();
        ListTag entries = new ListTag();
        for (Map<Long, ExtendedSolidState> w : EXTENDED_SOLIDS.values()) {
            for (ExtendedSolidState e : w.values()) {
                CompoundTag t = new CompoundTag();
                t.putString("world", e.worldId.identifier().getNamespace() + ":" + e.worldId.identifier().getPath());
                t.putLong("body", e.bodyPos.asLong());
                t.putDouble("fx", e.facing.x);
                t.putDouble("fy", e.facing.y);
                t.putDouble("fz", e.facing.z);
                t.putInt("distance", e.distance);
                t.putDouble("speed", e.speed);
                t.putDouble("su", e.su);
                t.putString("mode", e.mode.name());
                t.putLong("delay", e.delay);
                t.putLong("timer", e.timer);
                long[] pos = new long[e.positions.size()];
                int i = 0;
                for (BlockPos p : e.positions) {
                    pos[i++] = p.asLong();
                }
                t.putLongArray("positions", pos);
                long[] shaft = new long[e.shaftPositions.size()];
                int j = 0;
                for (BlockPos p : e.shaftPositions) {
                    shaft[j++] = p.asLong();
                }
                t.putLongArray("shaft", shaft);
                entries.add(t);
            }
        }
        root.put("entries", (Tag)entries);
        Files.createDirectories(file.getParent(), new FileAttribute[0]);
        NbtIo.writeCompressed((CompoundTag)root, (Path)file);
    }

    public static void loadExtendedSolids(Path file) throws IOException {
        if (!Files.exists(file, new LinkOption[0])) {
            return;
        }
        CompoundTag root = NbtIo.readCompressed((Path)file, (NbtAccounter)NbtAccounter.unlimitedHeap());
        ListTag entries = root.getListOrEmpty("entries");
        for (int i = 0; i < entries.size(); ++i) {
            ResourceKey worldKey;
            CompoundTag t = entries.getCompoundOrEmpty(i);
            String worldStr = t.getString("world").orElse("");
            if (worldStr.contains(":")) {
                try {
                    Identifier loc = Identifier.parse((String)worldStr);
                    worldKey = ResourceKey.create((ResourceKey)Registries.DIMENSION, (Identifier)loc);
                }
                catch (Exception e) {
                    continue;
                }
            }
            try {
                UUID worldUuid = UUID.fromString(worldStr);
                World bukkitWorld = Bukkit.getWorld((UUID)worldUuid);
                if (bukkitWorld == null) continue;
                worldKey = ((CraftWorld)bukkitWorld).getHandle().dimension();
            }
            catch (IllegalArgumentException bad) {
                continue;
            }
            BlockPos body = BlockPos.of((long)t.getLong("body").orElse(0L));
            Vec3 facing = new Vec3(t.getDouble("fx").orElse(0.0).doubleValue(), t.getDouble("fy").orElse(1.0).doubleValue(), t.getDouble("fz").orElse(0.0).doubleValue());
            HashSet<BlockPos> positions = new HashSet<BlockPos>();
            for (long l : t.getLongArray("positions").orElse(new long[0])) {
                positions.add(BlockPos.of((long)l));
            }
            HashSet<BlockPos> shaft = new HashSet<BlockPos>();
            for (long l : t.getLongArray("shaft").orElse(new long[0])) {
                shaft.add(BlockPos.of((long)l));
            }
            ExtendedSolidState e = new ExtendedSolidState((ResourceKey<Level>)worldKey, body, facing, t.getInt("distance").orElse(1), t.getDouble("speed").orElse(1.0), t.getDouble("su").orElse(2.0), PistonBearingBehavior.Mode.fromString(t.getString("mode").orElse("euler")), t.getLong("delay").orElse(100L), positions, shaft, false);
            e.timer = t.getLong("timer").orElse(0L);
            EXTENDED_SOLIDS.computeIfAbsent((ResourceKey<Level>)worldKey, k -> new ConcurrentHashMap()).put(body.asLong(), e);
        }
    }

    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new ContraptionEntity(state);
    }

    @Override
    public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
    }

    @Override
    public boolean isRotational() {
        return false;
    }

    @Override
    public boolean isVehicle() {
        return false;
    }

    private static final class ExtendedSolidState {
        final ResourceKey<Level> worldId;
        final BlockPos bodyPos;
        final Vec3 facing;
        final int distance;
        final double speed;
        final double su;
        final PistonBearingBehavior.Mode mode;
        final long delay;
        final Set<BlockPos> positions;
        final Set<BlockPos> shaftPositions;
        long timer = 0L;
        boolean prevRedstone;

        ExtendedSolidState(ResourceKey<Level> worldId, BlockPos bodyPos, Vec3 facing, int distance, double speed, double su, PistonBearingBehavior.Mode mode, long delay, Set<BlockPos> positions, Set<BlockPos> shaftPositions, boolean initialRedstone) {
            this.worldId = worldId;
            this.bodyPos = bodyPos.immutable();
            this.facing = facing;
            this.distance = distance;
            this.speed = speed;
            this.su = su;
            this.mode = mode;
            this.delay = delay;
            this.positions = positions;
            this.shaftPositions = shaftPositions == null ? new HashSet() : shaftPositions;
            this.prevRedstone = initialRedstone;
        }
    }
}

