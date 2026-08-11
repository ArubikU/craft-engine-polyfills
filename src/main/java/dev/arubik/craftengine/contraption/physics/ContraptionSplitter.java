package dev.arubik.craftengine.contraption.physics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.joml.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.behavior.MassModel;
import dev.arubik.craftengine.contraption.behavior.PhysicsBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.level.BukkitContraptionLevel;
import net.momirealms.craftengine.core.util.Key;

/**
 * Fractures a PhysContraption whose cells no longer form one connected solid into one independent
 * body per island.
 *
 * <h2>Why adjacency, and explicitly NOT the glue graph</h2>
 * The obvious connectivity source would be {@code ContraptionLevel#glueEdgesLocal}, but it cannot be
 * used here: {@code ContraptionCapture#captureGlueEdges} ends with an "always-stick guarantee" that
 * chains every otherwise-disconnected component to a single anchor cell with SYNTHETIC edges. The
 * stored glue graph is therefore connected by construction — asking it whether the body fell apart
 * always answers "no". Connectivity is instead 6-neighbour BLOCK ADJACENCY over the live cells,
 * which is the only signal that actually changes when a block is broken out of the middle.
 *
 * <h2>Why the fragments do not simply drop</h2>
 * Each island is re-registered as its own PhysContraption at the parent's EXACT transform, keeping
 * its original local coordinates. Render and physics both derive world positions as
 * {@code bearingOrigin + P + scale·R·(cell − P)}, so identical bearing origin + identical rotation +
 * identical local coords reproduce identical world positions for every cell — the split is
 * invisible on the tick it happens, and the bodies only diverge afterwards, under their own
 * (now different) mass, COM and inertia.
 *
 * <h2>Velocity inheritance</h2>
 * A fragment does not inherit the parent's COM velocity; it inherits the velocity of the parent's
 * material at the fragment's OWN center of mass, {@code v + ω × (childCom − parentCom)} (see
 * {@link RigidBody#velocityAt}), with the parent's angular velocity carried over unchanged. Without
 * the {@code ω × r} term every fragment of a spinning body would leave with the hub's velocity and
 * the spin's energy would be silently created or destroyed; with it, a spinning body's pieces fly
 * apart tangentially, which is what a break actually looks like. The retained parent is re-based the
 * same way, because its COM also moved when it lost mass.
 *
 * <h2>Deliberate scope limits</h2>
 * A fragment gets a {@link PhysicsBehavior} and nothing else. It carries no bearing block, so it has
 * no bearing behavior to inherit; captured auto-behaviors (fans, magnets) and furniture stay with
 * the retained parent, and a fragment is session-only — it has no anchor block for
 * {@code BlockAnchoredContraptionStore} to key persistence on.
 */
public final class ContraptionSplitter {

    private ContraptionSplitter() {
    }

    /** Block flags matching {@code ContraptionLevel#putBlock}'s quiet batch path. */
    private static final int QUIET_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS;

    /**
     * Partitions {@code cells} into connected components under 6-neighbour face adjacency.
     *
     * <p>Face adjacency, not 26-neighbour: two cells touching only at an edge or a corner share no
     * face and hold nothing together, so they must land in different islands. Pure — takes and
     * returns plain values so the fracture rule is testable without a server.
     *
     * @return one set per island; empty for an empty input; a single set for a connected body
     */
    /**
     * Whether this contraption is a kind that can come apart.
     *
     * <p>A LINEAR, ROTATIONAL or MINECART contraption is held together BY ITS BEARING — the bearing moves
     * the whole captured structure rigidly, and a cell that stops touching its neighbours is still carried,
     * because contact was never what held it on. Fracturing those would be wrong.
     *
     * <p>PHYS and GHAST are the two where contact IS the attachment. A phys body is nothing but its cells,
     * and a ghast's structure is glued blocks riding an entity — in both, a cell that touches nothing is
     * attached to nothing, and the user's rule is that it becomes its own falling body ("si en un
     * contraption del tipo ghast un bloque deja de estar conectado a cualquier otro, le ocurra lo de
     * convertirse en phys").
     *
     * <p>Identified by the BEHAVIOR rather than {@code ContraptionState#bearingType}, because a state built
     * by a path predating that field can carry none — the same reason {@code ContraptionEngine}'s
     * dead-anchor teardown decides by behavior.
     */
    private static boolean mayFracture(ContraptionState state) {
        if (physicsBehaviorOf(state) != null) {
            return true;
        }
        for (dev.arubik.craftengine.contraption.MovementBehavior behavior : state.behaviors()) {
            if (behavior instanceof dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior) {
                return true;
            }
        }
        return false;
    }

    public static List<Set<BlockPos>> islands(Set<BlockPos> cells) {
        List<Set<BlockPos>> result = new ArrayList<>();
        if (cells == null || cells.isEmpty()) {
            return result;
        }
        Set<BlockPos> remaining = new HashSet<>(cells);
        Deque<BlockPos> frontier = new ArrayDeque<>();
        for (BlockPos seed : cells) {
            if (!remaining.remove(seed)) {
                continue;
            }
            Set<BlockPos> island = new LinkedHashSet<>();
            frontier.add(seed);
            island.add(seed);
            while (!frontier.isEmpty()) {
                BlockPos current = frontier.poll();
                for (Direction direction : Direction.values()) {
                    BlockPos neighbor = current.relative(direction);
                    if (remaining.remove(neighbor)) {
                        island.add(neighbor);
                        frontier.add(neighbor);
                    }
                }
            }
            result.add(island);
        }
        return result;
    }

    /**
     * Splits {@code parent} if its cells have become disconnected, keeping the largest island in
     * place and spawning one new PhysContraption per remaining island.
     *
     * <p><b>Must be called with no iteration in flight over {@link ContraptionManager#all()}</b> —
     * it registers new contraptions. {@link PhysicsWorld#stepAll} therefore collects candidates
     * during its pass and calls this after it, the same defer-and-apply shape
     * {@code ContraptionEngine#tickAll} uses for its disassembles.
     *
     * <p>Cheap and idempotent for the overwhelmingly common case: this runs on every cell-count
     * change, and a still-connected body costs one flood fill and returns.
     *
     * @return the newly spawned fragments, empty when nothing split
     */
    public static List<ContraptionEntity> splitIfDisconnected(ContraptionEntity parent) {
        if (parent == null || !mayFracture(parent.state())) {
            return List.of();
        }
        ContraptionState parentState = parent.state();
        ContraptionLevel parentLevel = parentState.level();
        if (parentLevel == null) {
            return List.of();
        }
        List<Set<BlockPos>> islands = islands(parentLevel.localPositions());
        if (islands.size() <= 1) {
            return List.of();
        }
        Set<BlockPos> retained = largest(islands);

        PhysBody parentBody = PhysicsWorld.bodyOf(parentState.id());
        // Snapshot BEFORE any cell moves: once the parent's body resyncs, its position is the NEW
        // COM, and every fragment's inherited velocity is measured against the OLD one.
        Vector3d parentCom = parentBody == null ? null : new Vector3d(parentBody.body.position);
        Vector3d parentLinear = parentBody == null ? new Vector3d() : new Vector3d(parentBody.body.linearVelocity);
        Vector3d parentAngular = parentBody == null ? new Vector3d() : new Vector3d(parentBody.body.angularVelocity);

        List<ContraptionEntity> spawned = new ArrayList<>();
        for (Set<BlockPos> island : islands) {
            if (island == retained) {
                continue;
            }
            ContraptionEntity child = spawn(parentState, parentLevel, island);
            if (child == null) {
                continue; // failed to build a level for this island — leave its cells with the parent
            }
            PhysBody childBody = PhysicsWorld.ensureBody(child.state());
            inheritVelocity(childBody, parentCom, parentLinear, parentAngular);
            clear(parentLevel, island);
            spawned.add(child);
        }
        if (spawned.isEmpty()) {
            return List.of();
        }
        parentLevel.refreshLocalPositions();
        retainGlueEdges(parentLevel);
        // The parent's mass, COM, shape and inertia are all stale now. PhysicsWorld rebuilds them on
        // the next step from the changed cell count; resync only forces the transform re-derive and
        // wakes a body that may have been asleep when it was hit.
        PhysicsWorld.resync(parentState.id());
        rebaseParent(parentState, parentBody, parentCom, parentLinear, parentAngular);
        return spawned;
    }

    /**
     * Builds and registers one fragment: a fresh {@link ContraptionLevel} holding {@code island}'s
     * cells at their ORIGINAL local coordinates, anchored at the parent's exact pose.
     *
     * <p>Reusing the local coordinates rather than re-basing them to the island's own corner is what
     * makes the fragment land exactly where its blocks already are — see the class javadoc.
     */
    private static ContraptionEntity spawn(ContraptionState parentState, ContraptionLevel parentLevel,
            Set<BlockPos> island) {
        ContraptionLevel childLevel;
        try {
            childLevel = ContraptionLevel.create(parentLevel.realLevel(), parentState.x(), parentState.y(),
                    parentState.z(), parentState.yawRadians());
        } catch (Throwable t) {
            return null;
        }
        for (BlockPos local : island) {
            BlockState state = parentLevel.getBlockState(local);
            if (state.isAir()) {
                continue;
            }
            CompoundTag blockEntity = parentLevel.saveBlockEntity(local);
            byte[] ceController = parentLevel.getCeControllerData(local);
            childLevel.putBlock(local, state, true);
            if (blockEntity != null) {
                childLevel.putBlockEntity(local, blockEntity);
            }
            if (ceController != null) {
                childLevel.putCeControllerData(local, ceController);
            }
        }
        childLevel.setGlueEdgesLocal(edgesWithin(parentLevel, island));

        ContraptionState childState = new ContraptionState(UUID.randomUUID(), parentState.worldId(), childLevel,
                parentState.x(), parentState.y(), parentState.z());
        childState.setOwner(parentState.owner());
        childState.setYawRadians(parentState.yawRadians());
        childState.setPitchRadians(parentState.pitchRadians());
        childState.setRollRadians(parentState.rollRadians());
        childState.setScale(parentState.scale());
        // A fragment IS a phys contraption, and anything that asks what kind it is must get an answer.
        // Attaching the behavior alone left bearingType null, so every check keyed on the type — the
        // hammer's disassemble, the persistence type resolver — silently did not apply to fragments, which
        // are exactly the bodies a player most wants to clear up.
        childState.setBearingType(net.momirealms.craftengine.core.util.Key.of("polyfills", "phys"));
        childState.addBehavior(new PhysicsBehavior());
        ContraptionEntity child = ContraptionManager.register(new ContraptionEntity(childState));
        // Transfer matching block elements from parent so there's no despawn/respawn flash on split
        transferElements(parentState, childState, island);
        dev.arubik.craftengine.contraption.listener.BearingHammerListener.markAssembled(childState.worldId(),
                net.minecraft.core.BlockPos.containing(childState.x(), childState.y(), childState.z()),
                childState.id());
        return child;
    }

    /**
     * Moves block elements whose localPos is in {@code island} from parent to child element list.
     * Avoids despawn+respawn flash when a contraption splits — the elements keep their packet entity
     * IDs and shownTo state, so viewers see no visual change on the split tick.
     */
    private static void transferElements(ContraptionState parent, ContraptionState child,
            Set<BlockPos> island) {
        java.util.List<dev.arubik.craftengine.contraption.element.ContraptionElement> parentElems =
                new java.util.ArrayList<>(parent.elements());
        java.util.List<dev.arubik.craftengine.contraption.element.ContraptionElement> childElems =
                new java.util.ArrayList<>(child.elements());

        java.util.Iterator<dev.arubik.craftengine.contraption.element.ContraptionElement> it =
                parentElems.iterator();
        while (it.hasNext()) {
            dev.arubik.craftengine.contraption.element.ContraptionElement e = it.next();
            if (e instanceof dev.arubik.craftengine.contraption.element.ContraptionBlockElement block) {
                if (island.contains(block.localPos())) {
                    childElems.add(block);
                    it.remove();
                }
            }
        }
        parent.setElements(parentElems);
        child.setElements(childElems);
    }

    /**
     * Sets {@code body}'s velocity to the parent's velocity AT THIS BODY'S center of mass.
     */
    private static void inheritVelocity(PhysBody body, Vector3d parentCom, Vector3d parentLinear,
            Vector3d parentAngular) {
        if (body == null || parentCom == null) {
            return;
        }
        Vector3d r = new Vector3d(body.body.position).sub(parentCom);
        body.body.linearVelocity.set(parentAngular).cross(r).add(parentLinear);
        body.body.angularVelocity.set(parentAngular);
        body.wakeUp();
    }

    /**
     * Re-bases the retained parent's linear velocity onto its new center of mass.
     *
     * <p>Losing mass moves the COM, and {@link RigidBody#linearVelocity} is the velocity OF the COM —
     * so leaving it untouched would silently re-interpret the old point's velocity as the new
     * point's. The new COM is derived from the freshly recomputed {@link MassModel} rather than read
     * off the body, because the body has not been rebuilt yet at this point.
     */
    private static void rebaseParent(ContraptionState parentState, PhysBody parentBody, Vector3d parentCom,
            Vector3d parentLinear, Vector3d parentAngular) {
        if (parentBody == null || parentCom == null) {
            return;
        }
        MassModel model = MassModel.of(parentState.level());
        if (model.cellCount() == 0) {
            return;
        }
        Vector3d comLocal = new Vector3d(model.centerOfMass().x, model.centerOfMass().y, model.centerOfMass().z);
        Vector3d newCom = ContraptionTransform.comWorld(
                new net.minecraft.world.phys.Vec3(parentState.x(), parentState.y(), parentState.z()),
                ContraptionTransform.fromEuler(parentState.yawRadians(), parentState.pitchRadians(),
                        parentState.rollRadians()),
                comLocal, parentState.scale());
        Vector3d r = newCom.sub(parentCom);
        parentBody.body.linearVelocity.set(parentAngular).cross(r).add(parentLinear);
        parentBody.body.angularVelocity.set(parentAngular);
        parentBody.wakeUp();
    }

    /** Empties {@code island}'s cells out of {@code level} — the fragment now owns them. */
    private static void clear(ContraptionLevel level, Set<BlockPos> island) {
        BlockState air = Blocks.AIR.defaultBlockState();
        for (BlockPos local : island) {
            level.setBlock(local, air, QUIET_FLAGS);
        }
    }

    /** The parent's glue edges restricted to the cells it still holds — an edge to a departed cell is meaningless. */
    private static void retainGlueEdges(ContraptionLevel level) {
        level.setGlueEdgesLocal(edgesWithin(level, level.localPositions()));
    }

    /** The subset of {@code level}'s glue edges with BOTH endpoints inside {@code cells}. */
    private static List<long[]> edgesWithin(ContraptionLevel level, Set<BlockPos> cells) {
        Set<Long> packed = new HashSet<>();
        for (BlockPos cell : cells) {
            packed.add(cell.asLong());
        }
        List<long[]> kept = new ArrayList<>();
        for (long[] edge : level.glueEdgesLocal()) {
            if (packed.contains(edge[0]) && packed.contains(edge[1])) {
                kept.add(new long[] { edge[0], edge[1] });
            }
        }
        return kept;
    }

    /**
     * The island the parent keeps: the most cells, ties broken by the lowest packed position so the
     * choice never depends on hash iteration order.
     */
    private static Set<BlockPos> largest(List<Set<BlockPos>> islands) {
        Set<BlockPos> best = null;
        long bestKey = 0;
        for (Set<BlockPos> island : islands) {
            long key = lowestPacked(island);
            if (best == null || island.size() > best.size() || (island.size() == best.size() && key < bestKey)) {
                best = island;
                bestKey = key;
            }
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
            if (behavior instanceof PhysicsBehavior physics) {
                return physics;
            }
        }
        return null;
    }
}
