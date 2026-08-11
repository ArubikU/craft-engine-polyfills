package dev.arubik.craftengine.contraption.type;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.util.Key;

/**
 * Physics (free-falling) contraption type (roadmap #9).
 *
 * <h2>Design pattern: dynamic physics control</h2>
 * No bearing anchor. Falls under gravity, collides with ground/water. XPBD solver.
 * Can enable/disable physics per instance (trains with derailment: start as rail-follow,
 * switch to PHYS when derailed).
 *
 * <h2>Reference for external plugins</h2>
 * Study this for:
 * - Falling structures (TNT cannon payload, collapsing buildings)
 * - Submarines (buoyancy via PhysicsBehavior buoyancy flag)
 * - Dynamic mode switching (train derailment: MINECART -> PHYS)
 *
 * <h2>Key elements</h2>
 * 1. {@link PhysicsBehavior}: gravity integrator, ground collision, buoyancy
 * 2. No anchor block — structure free-falls
 * 3. {@link #hasPhysics}: true (enables runtime physics control)
 *
 * <h2>Milestone 1 scope</h2>
 * Gravity + falling + ground collision only. No torque/rotation. No off-thread solver.
 * See PhysicsBehavior for full roadmap.
 *
 * <h2>Dynamic switching example</h2>
 * Train derailment:
 * <pre>{@code
 * // Start as MINECART
 * ContraptionEntity train = assemble(world, bearing, Key.of("polyfills", "minecart"));
 *
 * // Detect derail (no rail beneath)
 * if (noRailBeneath) {
 *     // Remove minecart behavior
 *     train.state().behaviors().removeIf(b -> b instanceof MinecartFollowBehavior);
 *     // Add physics
 *     train.state().addBehavior(new PhysicsBehavior(level, anchor));
 *     train.state().setBearingType(Key.of("polyfills", "phys"));
 * }
 * }</pre>
 */
public class PhysContraptionType implements ContraptionType {

    public static final PhysContraptionType INSTANCE = new PhysContraptionType();

    private PhysContraptionType() {
    }

    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new ContraptionEntity(state);
    }

    @Override
    public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
        // PhysicsBehavior attached by PhysicsWorld on first tick (no public constructor).
        // See ContraptionAssembler#attachDefaultBehavior PHYS case.
    }

    @Override
    public boolean isRotational() {
        return false;
    }

    @Override
    public boolean isVehicle() {
        return false;
    }

    @Override
    public boolean hasPhysics() {
        return true; // XPBD solver, dynamic enable/disable
    }
}
