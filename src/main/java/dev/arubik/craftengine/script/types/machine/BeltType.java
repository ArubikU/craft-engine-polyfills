package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.script.types.primitive.VectorType;
import dev.arubik.craftengine.script.types.world.BlockType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class BeltType {

    public record BeltRef(ServerLevel level, BlockPos pos) {}

    private BeltType() {}

    public static void register() {
        PolyTypeRegistry.define("Belt")
            .property("x",   obj -> ScriptValue.of(ref(obj).pos().getX()))
            .property("y",   obj -> ScriptValue.of(ref(obj).pos().getY()))
            .property("z",   obj -> ScriptValue.of(ref(obj).pos().getZ()))
            .property("pos", obj -> VectorType.wrap(ref(obj).pos().getX(), ref(obj).pos().getY(), ref(obj).pos().getZ()))
            .property("block", obj -> BlockType.wrap(ref(obj).level(), ref(obj).pos()))
            .method("get_block", (obj, args) -> BlockType.wrap(ref(obj).level(), ref(obj).pos()));
    }

    public static ScriptValue wrap(ServerLevel level, BlockPos pos) {
        if (level == null || pos == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Belt", new BeltRef(level, pos));
    }

    private static BeltRef ref(Object obj) { return (BeltRef) obj; }
}
