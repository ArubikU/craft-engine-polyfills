package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.script.types.primitive.VectorType;
import dev.arubik.craftengine.script.types.world.BlockType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class PipeType {

    public record PipeRef(ServerLevel level, BlockPos pos, boolean isFluid, boolean isGas) {}

    private PipeType() {}

    public static void register() {
        PolyTypeRegistry.define("Pipe")
            .property("x",            obj -> ScriptValue.of(ref(obj).pos().getX()))
            .property("y",            obj -> ScriptValue.of(ref(obj).pos().getY()))
            .property("z",            obj -> ScriptValue.of(ref(obj).pos().getZ()))
            .property("pos",          obj -> VectorType.wrap(ref(obj).pos().getX(), ref(obj).pos().getY(), ref(obj).pos().getZ()))
            .property("block",        obj -> BlockType.wrap(ref(obj).level(), ref(obj).pos()))
            .property("is_pipe",      obj -> ScriptValue.of(true))
            .property("is_fluid_pipe",obj -> ScriptValue.of(ref(obj).isFluid()))
            .property("is_gas_pipe",  obj -> ScriptValue.of(ref(obj).isGas()));
    }

    public static ScriptValue wrap(ServerLevel level, BlockPos pos) {
        return wrap(level, pos, false, false);
    }

    public static ScriptValue wrap(ServerLevel level, BlockPos pos, boolean isFluid, boolean isGas) {
        if (level == null || pos == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Pipe", new PipeRef(level, pos, isFluid, isGas));
    }

    private static PipeRef ref(Object obj) { return (PipeRef) obj; }
}
