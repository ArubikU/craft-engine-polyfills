package dev.arubik.craftengine.multiblock;

import dev.arubik.craftengine.machine.block.entity.GasPumpBlockEntity;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

import java.util.List;

/** Script-accessible form condition checker for multiblock structures. */
public final class FormConditionClass implements dev.arubik.craftengine.script.PolyClass {
    private final Level level;
    private final BlockPos core;

    public FormConditionClass(Level level, BlockPos core) {
        this.level = level;
        this.core = core;
    }

    @Override
    public ScriptValue get(String property) {
        return ScriptValue.NULL;
    }

    @Override
    public ScriptValue call(String method, List<ScriptValue> args) {
        return switch (method) {
            case "is_gas_provider" -> ScriptValue.of(GasPumpBlockEntity.providerAt(this.level, this.offset(args)) != null);
            case "block_id" -> {
                ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(this.level.getBlockState(this.offset(args))).orElse(null);
                if (cs != null && cs.owner() != null) {
                    yield ScriptValue.of(((BlockDefinition)cs.owner().value()).id().toString());
                }
                yield ScriptValue.of("");
            }
            default -> ScriptValue.NULL;
        };
    }

    private BlockPos offset(List<ScriptValue> args) {
        int dx = args.size() > 0 ? (int)args.get(0).asNum() : 0;
        int dy = args.size() > 1 ? (int)args.get(1).asNum() : 0;
        int dz = args.size() > 2 ? (int)args.get(2).asNum() : 0;
        return this.core.offset(dx, dy, dz);
    }
}
