/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.util.Key
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior;
import dev.arubik.craftengine.contraption.behavior.MassModel;
import dev.arubik.craftengine.contraption.behavior.PhysicsBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionBlockElement;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.physics.ContraptionTransform;
import dev.arubik.craftengine.contraption.physics.PhysBody;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.util.Key;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class ContraptionSplitter {
    private static final int QUIET_FLAGS = 50;

    private ContraptionSplitter() {
    }

    private static boolean mayFracture(ContraptionState state) {
        if (ContraptionSplitter.physicsBehaviorOf(state) != null) {
            return true;
        }
        for (MovementBehavior behavior : state.behaviors()) {
            if (!(behavior instanceof GhastFollowBehavior)) continue;
            return true;
        }
        return false;
    }

    public static List<Set<BlockPos>> islands(Set<BlockPos> cells) {
        ArrayList<Set<BlockPos>> result = new ArrayList<Set<BlockPos>>();
        if (cells == null || cells.isEmpty()) {
            return result;
        }
        HashSet<BlockPos> remaining = new HashSet<BlockPos>(cells);
        ArrayDeque<BlockPos> frontier = new ArrayDeque<BlockPos>();
        for (BlockPos seed : cells) {
            if (!remaining.remove(seed)) continue;
            LinkedHashSet<BlockPos> island = new LinkedHashSet<BlockPos>();
            frontier.add(seed);
            island.add(seed);
            while (!frontier.isEmpty()) {
                BlockPos current = (BlockPos)frontier.poll();
                for (Direction direction : Direction.values()) {
                    BlockPos neighbor = current.relative(direction);
                    if (!remaining.remove(neighbor)) continue;
                    island.add(neighbor);
                    frontier.add(neighbor);
                }
            }
            result.add(island);
        }
        return result;
    }

    public static List<ContraptionEntity> splitIfDisconnected(ContraptionEntity parent) {
        if (parent == null || !ContraptionSplitter.mayFracture(parent.state())) {
            return List.of();
        }
        ContraptionState parentState = parent.state();
        ContraptionLevel parentLevel = parentState.level();
        if (parentLevel == null) {
            return List.of();
        }
        List<Set<BlockPos>> islands = ContraptionSplitter.islands(parentLevel.localPositions());
        if (islands.size() <= 1) {
            return List.of();
        }
        Set<BlockPos> retained = ContraptionSplitter.largest(islands);
        PhysBody parentBody = PhysicsWorld.bodyOf(parentState.id());
        Vector3d parentCom = parentBody == null ? null : new Vector3d((Vector3dc)parentBody.body.position);
        Vector3d parentLinear = parentBody == null ? new Vector3d() : new Vector3d((Vector3dc)parentBody.body.linearVelocity);
        Vector3d parentAngular = parentBody == null ? new Vector3d() : new Vector3d((Vector3dc)parentBody.body.angularVelocity);
        ArrayList<ContraptionEntity> spawned = new ArrayList<ContraptionEntity>();
        for (Set<BlockPos> island : islands) {
            ContraptionEntity child;
            if (island == retained || (child = ContraptionSplitter.spawn(parentState, parentLevel, island)) == null) continue;
            PhysBody childBody = PhysicsWorld.ensureBody(child.state());
            ContraptionSplitter.inheritVelocity(childBody, parentCom, parentLinear, parentAngular);
            ContraptionSplitter.clear(parentLevel, island);
            spawned.add(child);
        }
        if (spawned.isEmpty()) {
            return List.of();
        }
        parentLevel.refreshLocalPositions();
        ContraptionSplitter.retainGlueEdges(parentLevel);
        PhysicsWorld.resync(parentState.id());
        ContraptionSplitter.rebaseParent(parentState, parentBody, parentCom, parentLinear, parentAngular);
        return spawned;
    }

    private static ContraptionEntity spawn(ContraptionState parentState, ContraptionLevel parentLevel, Set<BlockPos> island) {
        ContraptionLevel childLevel;
        try {
            childLevel = ContraptionLevel.create(parentLevel.realLevel(), parentState.x(), parentState.y(), parentState.z(), parentState.yawRadians());
        }
        catch (Throwable t) {
            return null;
        }
        for (BlockPos local : island) {
            BlockState state = parentLevel.getBlockState(local);
            if (state.isAir()) continue;
            CompoundTag blockEntity = parentLevel.saveBlockEntity(local);
            byte[] ceController = parentLevel.getCeControllerData(local);
            childLevel.putBlock(local, state, true);
            if (blockEntity != null) {
                childLevel.putBlockEntity(local, blockEntity);
            }
            if (ceController == null) continue;
            childLevel.putCeControllerData(local, ceController);
        }
        childLevel.setGlueEdgesLocal(ContraptionSplitter.edgesWithin(parentLevel, island));
        ContraptionState childState = new ContraptionState(UUID.randomUUID(), parentState.worldId(), childLevel, parentState.x(), parentState.y(), parentState.z());
        childState.setOwner(parentState.owner());
        childState.setYawRadians(parentState.yawRadians());
        childState.setPitchRadians(parentState.pitchRadians());
        childState.setRollRadians(parentState.rollRadians());
        childState.setScale(parentState.scale());
        childState.setBearingType(Key.of((String)"polyfills", (String)"phys"));
        childState.addBehavior(new PhysicsBehavior());
        ContraptionEntity child = ContraptionManager.register(new ContraptionEntity(childState));
        ContraptionSplitter.transferElements(parentState, childState, island);
        BearingHammerListener.markAssembled(childState.worldId(), BlockPos.containing((double)childState.x(), (double)childState.y(), (double)childState.z()), childState.id());
        return child;
    }

    private static void transferElements(ContraptionState parent, ContraptionState child, Set<BlockPos> island) {
        ArrayList<ContraptionElement> parentElems = new ArrayList<ContraptionElement>(parent.elements());
        ArrayList<ContraptionElement> childElems = new ArrayList<ContraptionElement>(child.elements());
        Iterator it = parentElems.iterator();
        while (it.hasNext()) {
            ContraptionBlockElement block;
            ContraptionElement e = (ContraptionElement)it.next();
            if (!(e instanceof ContraptionBlockElement) || !island.contains((block = (ContraptionBlockElement)e).localPos())) continue;
            childElems.add(block);
            it.remove();
        }
        parent.setElements(parentElems);
        child.setElements(childElems);
    }

    private static void inheritVelocity(PhysBody body, Vector3d parentCom, Vector3d parentLinear, Vector3d parentAngular) {
        if (body == null || parentCom == null) {
            return;
        }
        Vector3d r = new Vector3d((Vector3dc)body.body.position).sub((Vector3dc)parentCom);
        body.body.linearVelocity.set((Vector3dc)parentAngular).cross((Vector3dc)r).add((Vector3dc)parentLinear);
        body.body.angularVelocity.set((Vector3dc)parentAngular);
        body.wakeUp();
    }

    private static void rebaseParent(ContraptionState parentState, PhysBody parentBody, Vector3d parentCom, Vector3d parentLinear, Vector3d parentAngular) {
        if (parentBody == null || parentCom == null) {
            return;
        }
        MassModel model = MassModel.of(parentState.level());
        if (model.cellCount() == 0) {
            return;
        }
        Vector3d comLocal = new Vector3d(model.centerOfMass().x, model.centerOfMass().y, model.centerOfMass().z);
        Vector3d newCom = ContraptionTransform.comWorld(new Vec3(parentState.x(), parentState.y(), parentState.z()), ContraptionTransform.fromEuler(parentState.yawRadians(), parentState.pitchRadians(), parentState.rollRadians()), comLocal, parentState.scale());
        Vector3d r = newCom.sub((Vector3dc)parentCom);
        parentBody.body.linearVelocity.set((Vector3dc)parentAngular).cross((Vector3dc)r).add((Vector3dc)parentLinear);
        parentBody.body.angularVelocity.set((Vector3dc)parentAngular);
        parentBody.wakeUp();
    }

    private static void clear(ContraptionLevel level, Set<BlockPos> island) {
        BlockState air = Blocks.AIR.defaultBlockState();
        for (BlockPos local : island) {
            level.setBlock(local, air, 50);
        }
    }

    private static void retainGlueEdges(ContraptionLevel level) {
        level.setGlueEdgesLocal(ContraptionSplitter.edgesWithin(level, level.localPositions()));
    }

    private static List<long[]> edgesWithin(ContraptionLevel level, Set<BlockPos> cells) {
        HashSet<Long> packed = new HashSet<Long>();
        for (BlockPos cell : cells) {
            packed.add(cell.asLong());
        }
        ArrayList<long[]> kept = new ArrayList<long[]>();
        for (long[] edge : level.glueEdgesLocal()) {
            if (!packed.contains(edge[0]) || !packed.contains(edge[1])) continue;
            kept.add(new long[]{edge[0], edge[1]});
        }
        return kept;
    }

    private static Set<BlockPos> largest(List<Set<BlockPos>> islands) {
        Set<BlockPos> best = null;
        long bestKey = 0L;
        for (Set<BlockPos> island : islands) {
            long key = ContraptionSplitter.lowestPacked(island);
            if (best != null && island.size() <= best.size() && (island.size() != best.size() || key >= bestKey)) continue;
            best = island;
            bestKey = key;
        }
        return best;
    }

    private static long lowestPacked(Set<BlockPos> island) {
        long lowest = Long.MAX_VALUE;
        for (BlockPos pos : island) {
            lowest = Math.min(lowest, pos.asLong());
        }
        return lowest;
    }

    private static PhysicsBehavior physicsBehaviorOf(ContraptionState state) {
        for (MovementBehavior behavior : state.behaviors()) {
            if (!(behavior instanceof PhysicsBehavior)) continue;
            PhysicsBehavior physics = (PhysicsBehavior)behavior;
            return physics;
        }
        return null;
    }
}

