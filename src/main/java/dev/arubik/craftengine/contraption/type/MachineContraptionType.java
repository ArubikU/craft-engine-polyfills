package dev.arubik.craftengine.contraption.type;

import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.util.Key;

/**
 * Generic machine-controlled contraption.
 * Key: polyfills:machine_contraption
 *
 * Machine scripts drive everything via:
 *   Machine.assemble_contraption()      — assemble glued structure
 *   Machine.contraption_alive()         — check UUID alive
 *   Machine.contraption_blocks()        — get blocks from contraption level
 *   Machine.set_contraption_rpm(rpm)    — set rotation speed + shaft output
 *   Machine.contraption_disassemble()   — return blocks to world
 *
 * Rotation axis is derived from the bearing's facing direction at assembly time
 * and handled by MachineContraptionBehavior reading state.globalRpm().
 */
public class MachineContraptionType implements ContraptionType {

    public static final Key KEY = Key.of("polyfills", "machine_contraption");
    public static final MachineContraptionType INSTANCE = new MachineContraptionType();

    private MachineContraptionType() {}

    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new ContraptionEntity(state);
    }

    @Override
    public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
        // Machine script drives everything via globalRpm — no auto-behavior needed here.
        // MachineContraptionBehavior is attached in ContraptionAssembler.attachDefaultBehavior.
    }

    @Override
    public boolean isRotational() {
        return true;
    }

    @Override
    public boolean isVehicle() {
        return false;
    }
}
