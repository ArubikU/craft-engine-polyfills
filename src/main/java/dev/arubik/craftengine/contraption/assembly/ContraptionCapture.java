/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.Containers
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DiodeBlock
 *  net.minecraft.world.level.block.RedStoneWireBlock
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.bukkit.world.BukkitWorld
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.chunk.CEChunk
 *  org.bukkit.World
 */
package dev.arubik.craftengine.contraption.assembly;

import dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.behavior.MovementBehaviorRegistry;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.glue.GlueGraph;
import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.render.BetterModelDriven;
import dev.arubik.craftengine.machine.render.ModelEngineDriven;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.chunk.CEChunk;
import org.bukkit.World;

public final class ContraptionCapture {
    private static final int CONNECT_MODEL_FLAGS = 18;
    private static boolean removingForCapture = false;
    private static final int QUIET_PLACE_FLAGS = 50;

    private ContraptionCapture() {
    }

    public static Result capture(Level realLevel, Set<BlockPos> worldPositions, BlockPos bearingWorldPos) {
        ContraptionLevel level = ContraptionLevel.create(realLevel, bearingWorldPos.getX(), bearingWorldPos.getY(), bearingWorldPos.getZ(), 0.0);
        for (BlockPos pos : worldPositions) {
            PersistentBlockEntity ce;
            BlockState state = realLevel.getBlockState(pos);
            BlockPos local = ContraptionMath.toLocal(pos, bearingWorldPos);
            level.putBlock(local, state, true);
            BlockEntity be = realLevel.getBlockEntity(pos);
            if (be != null) {
                level.putBlockEntity(local, be.saveWithFullMetadata((HolderLookup.Provider)realLevel.registryAccess()));
            }
            if ((ce = PersistentBlockEntity.getIfLoaded(realLevel, pos)) == null) continue;
            try {
                byte[] bytes = ce.serializeToBytes();
                level.putCeControllerData(local, bytes);
                PersistentBlockEntity inLevel = PersistentBlockEntity.getIfLoaded((Level)level.serverLevel(), local);
                if (inLevel == null) continue;
                inLevel.loadFromBytes(bytes);
            }
            catch (Throwable throwable) {}
        }
        ArrayList<BlockPos> structuralLocals = new ArrayList<BlockPos>();
        ArrayList<BlockPos> dependentLocals = new ArrayList<BlockPos>();
        for (BlockPos pos : worldPositions) {
            BlockPos local = ContraptionMath.toLocal(pos, bearingWorldPos);
            if (ContraptionCapture.isSupportDependent(level.getBlockState(local))) {
                dependentLocals.add(local);
                continue;
            }
            structuralLocals.add(local);
        }
        for (int pass = 0; pass < 2; ++pass) {
            ContraptionCapture.settleLocalNeighbors(level, structuralLocals);
            ContraptionCapture.settleLocalNeighbors(level, dependentLocals);
        }
        ServerLevel nms = level.serverLevel();
        for (int pass = 0; pass < 2; ++pass) {
            ContraptionCapture.settleConnectedModels((Level)nms, structuralLocals);
            ContraptionCapture.settleConnectedModels((Level)nms, dependentLocals);
        }
        return new Result(level, ContraptionCapture.resolveAutoBehaviors(level));
    }

    private static void settleLocalNeighbors(ContraptionLevel level, List<BlockPos> locals) {
        for (BlockPos local : locals) {
            try {
                level.updateNeighborsAt(local, level.getBlockState(local).getBlock());
            }
            catch (Throwable throwable) {}
        }
    }

    private static void settleConnectedModels(Level level, Iterable<BlockPos> positions) {
        for (BlockPos pos : positions) {
            try {
                ImmutableBlockState recomputed;
                ConnectedBlockBehavior conn;
                BlockState state = level.getBlockState(pos);
                ImmutableBlockState current = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
                if (current == null || (conn = ContraptionCapture.asConnected(current.behavior())) == null || (recomputed = (ImmutableBlockState)conn.vanillaMakeState(pos, level)) == null || recomputed.equals(current)) continue;
                level.setBlock(pos, (BlockState)recomputed.customBlockState().minecraftState(), 18);
            }
            catch (Throwable throwable) {}
        }
    }

    private static ConnectedBlockBehavior asConnected(Object behavior) {
        if (behavior instanceof ConnectedBlockBehavior) {
            ConnectedBlockBehavior connected = (ConnectedBlockBehavior)(behavior);
            return connected;
        }
        if (behavior instanceof BlockBehavior) {
            BlockBehavior bb = (BlockBehavior)behavior;
            try {
                return (ConnectedBlockBehavior)(bb.getFirst(ConnectedBlockBehavior.class));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return null;
    }

    public static void captureGlueEdges(ResourceKey<Level> worldId, ContraptionLevel level, BlockPos origin) {
        GlueGraph graph = GlueRegistry.graphFor(worldId);
        HashSet<BlockPos> localSet = new HashSet<BlockPos>(level.localPositions());
        ArrayList<long[]> edges = new ArrayList<long[]>();
        HashSet<Long> seenEdges = new HashSet<Long>();
        HashMap<Long, Long> parent = new HashMap<Long, Long>();
        for (BlockPos local : localSet) {
            parent.put(local.asLong(), local.asLong());
        }
        for (BlockPos local : localSet) {
            BlockPos world = ContraptionMath.toWorld(local, origin);
            for (BlockPos neighborWorld : graph.neighbors(world)) {
                long hi;
                long lb;
                long la;
                long lo;
                long edgeKey;
                BlockPos neighborLocal = ContraptionMath.toLocal(neighborWorld, origin);
                if (!localSet.contains(neighborLocal) || !seenEdges.add(edgeKey = (lo = Math.min(la = local.asLong(), lb = neighborLocal.asLong())) * 31L + (hi = Math.max(la, lb)))) continue;
                edges.add(new long[]{lo, hi});
                ContraptionCapture.union(parent, lo, hi);
            }
        }
        Long anchor = null;
        for (BlockPos local : localSet) {
            long key = local.asLong();
            if (anchor == null) {
                anchor = key;
                continue;
            }
            if (ContraptionCapture.find(parent, key) == ContraptionCapture.find(parent, anchor)) continue;
            edges.add(new long[]{Math.min(anchor, key), Math.max(anchor, key)});
            ContraptionCapture.union(parent, anchor, key);
        }
        level.setGlueEdgesLocal(edges);
    }

    private static long find(Map<Long, Long> parent, long x) {
        long root = x;
        while (parent.get(root) != root) {
            root = parent.get(root);
        }
        long cur = x;
        while (parent.get(cur) != root) {
            long next = parent.get(cur);
            parent.put(cur, root);
            cur = next;
        }
        return root;
    }

    private static void union(Map<Long, Long> parent, long a, long b) {
        long rb;
        long ra = ContraptionCapture.find(parent, a);
        if (ra != (rb = ContraptionCapture.find(parent, b))) {
            parent.put(ra, rb);
        }
    }

    public static void restoreGlue(ResourceKey<Level> worldId, ContraptionLevel level, BlockPos origin, BlockPos snappedBearing, int quarterTurns) {
        if (level == null) {
            return;
        }
        GlueGraph graph = GlueRegistry.graphFor(worldId);
        for (BlockPos blockPos : level.localPositions()) {
            graph.removeNode(ContraptionMath.toWorld(blockPos, origin));
        }
        HashMap<Long, BlockPos> localToWorld = new HashMap<Long, BlockPos>();
        for (BlockPos local : level.localPositions()) {
            BlockPos rotatedLocal = ContraptionCapture.rotateLocal(local, quarterTurns);
            localToWorld.put(local.asLong(), ContraptionMath.toWorld(rotatedLocal, snappedBearing));
        }
        List<long[]> list = level.glueEdgesLocal();
        if (list.isEmpty()) {
            BlockPos anchor = null;
            for (BlockPos world : localToWorld.values()) {
                if (anchor == null) {
                    anchor = world;
                    continue;
                }
                if (anchor.equals(world)) continue;
                graph.glue(anchor, world);
            }
            return;
        }
        for (long[] edge : list) {
            BlockPos wa = (BlockPos)localToWorld.get(edge[0]);
            BlockPos wb = (BlockPos)localToWorld.get(edge[1]);
            if (wa == null || wb == null || wa.equals(wb)) continue;
            graph.glue(wa, wb);
        }
    }

    public static List<MovementBehavior> resolveAutoBehaviors(ContraptionLevel level) {
        ArrayList<MovementBehavior> autoBehaviors = new ArrayList<MovementBehavior>();
        for (BlockPos local : level.localPositions()) {
            BlockState state = level.getBlockState(local);
            Key blockKey = ContraptionCapture.customBlockKey(state);
            MovementBehavior behavior = MovementBehaviorRegistry.resolve(blockKey, local, state);
            if (behavior == null) continue;
            autoBehaviors.add(behavior);
        }
        return autoBehaviors;
    }

    private static Key customBlockKey(BlockState state) {
        try {
            return BlockStateUtils.getOptionalCustomBlockState(state).map(ImmutableBlockState::owner).filter(owner -> owner != null).map(owner -> ((BlockDefinition)owner.value()).id()).orElse(null);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static void removeFromWorld(Level level, Set<BlockPos> worldPositions) {
        boolean prevRemovingForCapture = removingForCapture;
        removingForCapture = true;
        try {
            ContraptionCapture.removeFromWorld0(level, worldPositions);
        }
        finally {
            removingForCapture = prevRemovingForCapture;
        }
    }

    public static boolean isRemovingForCapture() {
        return removingForCapture;
    }

    private static void removeFromWorld0(Level level, Set<BlockPos> worldPositions) {
        for (BlockPos pos : worldPositions) {
            PersistentBlockEntity ce = PersistentBlockEntity.getIfLoaded(level, pos);
            if (ce instanceof BetterModelDriven) {
                BetterModelDriven driven = (BetterModelDriven)(ce);
                try {
                    driven.betterModelRenderer().close();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (ce instanceof ModelEngineDriven) {
                ModelEngineDriven med = (ModelEngineDriven)(ce);
                try {
                    med.modelEngineRenderer().close();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (!(ce instanceof AbstractMachineBlockEntity)) continue;
            AbstractMachineBlockEntity machine = (AbstractMachineBlockEntity)ce;
            try {
                machine.unregister();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                ContraptionCapture.removeCeBlockEntity(level, pos);
            }
            catch (Throwable throwable) {}
        }
        int quietFlags = 50;
        for (BlockPos pos : worldPositions) {
            PersistentBlockEntity ce = PersistentBlockEntity.getIfLoaded(level, pos);
            if (ce instanceof PersistentWorldlyBlockEntity) {
                PersistentWorldlyBlockEntity worldly = (PersistentWorldlyBlockEntity)ce;
                worldly.clearContent();
            }
            level.removeBlockEntity(pos);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), quietFlags);
        }
    }

    private static void removeCeBlockEntity(Level level, BlockPos pos) {
        CEWorld ceWorld = new BukkitWorld((World)level.getWorld()).storageWorld();
        if (ceWorld == null) {
            return;
        }
        CEChunk chunk = ceWorld.getChunkAtIfLoaded(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk != null) {
            chunk.removeBlockEntity(net.momirealms.craftengine.core.world.BlockPos.of((long)pos.asLong()));
        }
    }

    public static void restore(Level realLevel, ContraptionLevel level, BlockPos bearingWorldPos) {
        ArrayList<Placement> placements = new ArrayList<Placement>();
        for (BlockPos local : new HashSet<BlockPos>(level.localPositions())) {
            BlockPos worldPos = ContraptionMath.toWorld(local, bearingWorldPos);
            placements.add(new Placement(local, worldPos, level.getBlockState(local)));
        }
        ContraptionCapture.placeOrdered(realLevel, level, placements);
    }

    public static void restoreWithCollisionCheck(Level realLevel, ContraptionLevel level, BlockPos bearingWorldPos) {
        ArrayList<Placement> placements = new ArrayList<Placement>();
        for (BlockPos local : new HashSet<BlockPos>(level.localPositions())) {
            BlockPos worldPos = ContraptionMath.toWorld(local, bearingWorldPos);
            BlockState toPlace = level.getBlockState(local);
            BlockState existing = realLevel.getBlockState(worldPos);
            if (!existing.isAir() && !existing.canBeReplaced()) {
                ItemStack drop = new ItemStack((ItemLike)toPlace.getBlock().asItem());
                if (drop.isEmpty()) continue;
                Containers.dropItemStack((Level)realLevel, (double)worldPos.getX(), (double)worldPos.getY(), (double)worldPos.getZ(), (ItemStack)drop);
                continue;
            }
            placements.add(new Placement(local, worldPos, toPlace));
        }
        ContraptionCapture.placeOrdered(realLevel, level, placements);
    }

    public static void restoreRotated(Level realLevel, ContraptionLevel level, BlockPos bearingWorldPos, int quarterTurns) {
        if (quarterTurns == 0) {
            ContraptionCapture.restoreWithCollisionCheck(realLevel, level, bearingWorldPos);
            return;
        }
        Rotation rotation = ContraptionCapture.rotationFor(quarterTurns);
        ArrayList<Placement> placements = new ArrayList<Placement>();
        for (BlockPos local : new HashSet<BlockPos>(level.localPositions())) {
            BlockPos rotatedLocal = ContraptionCapture.rotateLocal(local, quarterTurns);
            BlockPos worldPos = ContraptionMath.toWorld(rotatedLocal, bearingWorldPos);
            BlockState toPlace = level.getBlockState(local).rotate(rotation);
            BlockState existing = realLevel.getBlockState(worldPos);
            if (!existing.isAir() && !existing.canBeReplaced()) {
                ItemStack drop = new ItemStack((ItemLike)toPlace.getBlock().asItem());
                if (drop.isEmpty()) continue;
                Containers.dropItemStack((Level)realLevel, (double)worldPos.getX(), (double)worldPos.getY(), (double)worldPos.getZ(), (ItemStack)drop);
                continue;
            }
            placements.add(new Placement(local, worldPos, toPlace));
        }
        ContraptionCapture.placeOrdered(realLevel, level, placements);
    }

    private static Rotation rotationFor(int quarterTurns) {
        return switch ((quarterTurns % 4 + 4) % 4) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    public static BlockPos rotateLocal(BlockPos local, int quarterTurns) {
        int x = local.getX();
        int z = local.getZ();
        for (int i = 0; i < (quarterTurns % 4 + 4) % 4; ++i) {
            int newX = -z;
            int newZ = x;
            x = newX;
            z = newZ;
        }
        return new BlockPos(x, local.getY(), z);
    }

    private static void placeOrdered(Level realLevel, ContraptionLevel level, List<Placement> placements) {
        ArrayList<Placement> structural = new ArrayList<Placement>();
        ArrayList<Placement> dependent = new ArrayList<Placement>();
        for (Placement p : placements) {
            if (ContraptionCapture.isSupportDependent(p.state())) {
                dependent.add(p);
                continue;
            }
            structural.add(p);
        }
        for (Placement p : structural) {
            realLevel.setBlock(p.worldPos(), p.state(), 50);
            ContraptionCapture.restoreBlockEntity(realLevel, level, p.local(), p.worldPos());
        }
        for (Placement p : dependent) {
            realLevel.setBlock(p.worldPos(), p.state(), 50);
            ContraptionCapture.restoreBlockEntity(realLevel, level, p.local(), p.worldPos());
        }
        ContraptionCapture.settleNeighbors(realLevel, structural);
        ContraptionCapture.settleNeighbors(realLevel, dependent);
        ArrayList<BlockPos> structuralWorld = new ArrayList<BlockPos>();
        ArrayList<BlockPos> dependentWorld = new ArrayList<BlockPos>();
        for (Placement p : structural) {
            structuralWorld.add(p.worldPos());
        }
        for (Placement p : dependent) {
            dependentWorld.add(p.worldPos());
        }
        for (int pass = 0; pass < 2; ++pass) {
            ContraptionCapture.settleConnectedModels(realLevel, structuralWorld);
            ContraptionCapture.settleConnectedModels(realLevel, dependentWorld);
        }
    }

    private static void settleNeighbors(Level realLevel, List<Placement> placed) {
        for (Placement p : placed) {
            try {
                realLevel.updateNeighborsAt(p.worldPos(), realLevel.getBlockState(p.worldPos()).getBlock());
            }
            catch (Throwable throwable) {}
        }
    }

    private static boolean isSupportDependent(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof RedStoneWireBlock || block instanceof DiodeBlock || block instanceof BaseRailBlock) {
            return true;
        }
        for (Property prop : state.getProperties()) {
            String name = prop.getName();
            if (!name.equals("face") && !name.equals("attachment") && !name.equals("hanging")) continue;
            return true;
        }
        return false;
    }

    private static void restoreBlockEntity(Level realLevel, ContraptionLevel level, BlockPos local, BlockPos worldPos) {
        PersistentBlockEntity ce;
        byte[] ceBytes;
        PersistentBlockEntity live;
        BlockEntity be;
        CompoundTag tag = level.saveBlockEntity(local);
        if (tag != null && (be = BlockEntity.loadStatic((BlockPos)worldPos, (BlockState)realLevel.getBlockState(worldPos), (CompoundTag)tag, (HolderLookup.Provider)realLevel.registryAccess())) != null) {
            be.setLevel(realLevel);
            realLevel.setBlockEntity(be);
        }
        if ((live = PersistentBlockEntity.getIfLoaded((Level)level.serverLevel(), local)) != null) {
            try {
                ceBytes = live.serializeToBytes();
            }
            catch (Throwable t) {
                ceBytes = level.getCeControllerData(local);
            }
        } else {
            ceBytes = level.getCeControllerData(local);
        }
        if (ceBytes != null && (ce = PersistentBlockEntity.getIfLoaded(realLevel, worldPos)) != null) {
            try {
                ce.loadFromBytes(ceBytes);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    public record Result(ContraptionLevel level, List<MovementBehavior> autoBehaviors) {
    }

    private record Placement(BlockPos local, BlockPos worldPos, BlockState state) {
    }
}

