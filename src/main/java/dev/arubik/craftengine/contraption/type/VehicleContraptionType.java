package dev.arubik.craftengine.contraption.type;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Vehicle (player-controlled physics) contraption type.
 *
 * <h2>Design pattern: piloted phys contraption</h2>
 * Free rigid body under gravity + collision + buoyancy. Player WASD/jump input becomes thrust.
 * Built on {@link PhysContraptionType} — inherits XPBD solver, adds steering.
 *
 * <h2>Reference for external plugins</h2>
 * Study this for:
 * - Custom vehicles (cars, tanks, helicopters)
 * - Steerable submarines (buoyancy + thrust)
 * - Mechs (leg IK + physics body)
 *
 * <h2>Key elements</h2>
 * 1. {@link dev.arubik.craftengine.contraption.behavior.VehicleControlBehavior}: WASD -> thrust impulses
 * 2. Inherits gravity/collision/buoyancy from PHYS
 * 3. {@link #isVehicle}: true (enables player input)
 * 4. {@link #hasPhysics}: true (enables runtime physics control)
 *
 * <h2>Control mapping</h2>
 * - W/S: forward/backward thrust
 * - A/D: left/right thrust (or yaw rotation for wheeled vehicles)
 * - Space: upward thrust (jump)
 * - Shift: downward thrust (sneak)
 * - Sprint: thrust multiplier
 *
 * <h2>Power-to-mass</h2>
 * Thrust scaled by inverse mass. Light vehicles accelerate faster. Mass computed from block types.
 * See {@link dev.arubik.craftengine.contraption.behavior.VehicleControlBehavior}.
 */
public class VehicleContraptionType implements ContraptionType {

    public static final VehicleContraptionType INSTANCE = new VehicleContraptionType();

    private VehicleContraptionType() {
    }

    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new ContraptionEntity(state);
    }

    @Override
    public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
        // VehicleControlBehavior attached by PhysicsWorld (extends PhysicsBehavior).
        // See ContraptionAssembler#attachDefaultBehavior VEHICLE case.
    }

    @Override
    public boolean isRotational() {
        return false;
    }

    @Override
    public boolean isVehicle() {
        return true; // enables player input routing
    }

    @Override
    public boolean hasPhysics() {
        return true; // XPBD solver, dynamic control
    }
}
