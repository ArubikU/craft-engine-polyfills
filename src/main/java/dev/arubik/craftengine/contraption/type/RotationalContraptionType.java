package dev.arubik.craftengine.contraption.type;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Rotational (windmill-style) contraption type.
 *
 * <h2>Design pattern: block-anchored bearing with angular velocity</h2>
 * Bearing block stays fixed. Structure rotates continuously around bearing axis. Powered by
 * adjacent motor.
 *
 * <h2>Reference for external plugins</h2>
 * Study this for:
 * - Windmills, water wheels (continuous rotation)
 * - Rotating platforms, turntables
 * - Gears, mechanical assemblies
 *
 * <h2>Key elements</h2>
 * 1. {@link dev.arubik.craftengine.contraption.behavior.RotationalBearingBehavior}: continuous yaw += RPM
 * 2. Bearing block remains in world
 * 3. {@link #isRotational}: true (enables angular velocity)
 *
 * <h2>Behavior attachment</h2>
 * See {@link dev.arubik.craftengine.contraption.ContraptionAssembler#attachDefaultBehavior} ROTATIONAL case.
 * Motor RPM passed directly to behavior.
 */
public class RotationalContraptionType implements ContraptionType {

    public static final RotationalContraptionType INSTANCE = new RotationalContraptionType();

    private RotationalContraptionType() {
    }

    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new ContraptionEntity(state);
    }

    @Override
    public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
        // RotationalBearingBehavior attached by ContraptionAssembler#attachDefaultBehavior.
        // Motor RPM read from adjacent motor.
    }

    @Override
    public boolean isRotational() {
        return true; // enables angular velocity
    }

    @Override
    public boolean isVehicle() {
        return false;
    }
}
