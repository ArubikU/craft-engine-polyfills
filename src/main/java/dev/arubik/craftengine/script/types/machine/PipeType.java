package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.script.types.world.BlockType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3d;

public final class PipeType {

    public record PipeRef(ServerLevel level, BlockPos pos, boolean isFluid, boolean isGas) {}

    private PipeType() {}

    public static void register() {
        PolyTypeRegistry.define("Pipe")
            .propertyTyped("x", TypeCodecs.DOUBLE, (PipeRef r) -> (double) r.pos().getX())
            .propertyTyped("y", TypeCodecs.DOUBLE, (PipeRef r) -> (double) r.pos().getY())
            .propertyTyped("z", TypeCodecs.DOUBLE, (PipeRef r) -> (double) r.pos().getZ())
            .propertyTyped("pos", TypeCodecs.polyType("Vector", Vector3d.class),
                (PipeRef r) -> new Vector3d(r.pos().getX(), r.pos().getY(), r.pos().getZ()))
            // A PipeRef is only ever built through wrap(...), which rejects a null level/pos, so the
            // BlockRef is always constructible — the same value BlockType.wrap produced here.
            .propertyTyped("block", TypeCodecs.polyType("Block", BlockType.BlockRef.class),
                (PipeRef r) -> r.level() == null || r.pos() == null ? null : new BlockType.BlockRef(r.level(), r.pos()))
            .propertyTyped("is_pipe", TypeCodecs.BOOL, (PipeRef r) -> true)
            .propertyTyped("is_fluid_pipe", TypeCodecs.BOOL, (PipeRef r) -> r.isFluid())
            .propertyTyped("is_gas_pipe", TypeCodecs.BOOL, (PipeRef r) -> r.isGas());
    }

    public static ScriptValue wrap(ServerLevel level, BlockPos pos) {
        return wrap(level, pos, false, false);
    }

    public static ScriptValue wrap(ServerLevel level, BlockPos pos, boolean isFluid, boolean isGas) {
        if (level == null || pos == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Pipe", new PipeRef(level, pos, isFluid, isGas));
    }
}
