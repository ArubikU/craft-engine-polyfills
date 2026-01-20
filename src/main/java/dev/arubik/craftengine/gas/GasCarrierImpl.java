package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.util.CustomBlockData;
import dev.arubik.craftengine.util.TypedKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.function.Consumer;

public class GasCarrierImpl {

    public static int insertGas(Level level, BlockPos pos, GasStack stack, int capacity, TypedKey<GasStack> key) {
        if (stack == null || stack.isEmpty())
            return 0;

        final int[] accepted = { 0 };

        CustomBlockData.from(level, pos).edit(p -> {
            GasStack stored = p.getOrDefault(key, GasStack.EMPTY);

            if (stored.isEmpty()) {
                int move = Math.min(capacity, stack.getAmount());
                p.set(key, new GasStack(stack.getType(), move));
                accepted[0] = move;
            } else if (stored.isGasEqual(stack)) {
                int space = capacity - stored.getAmount(); // Amount is stored field
                if (space > 0) {
                    int move = Math.min(space, stack.getAmount());
                    stored.grow(move);
                    p.set(key, new GasStack(stored.getType(), stored.getAmount()));
                    accepted[0] = move;
                }
            }
        });

        return accepted[0];
    }

    public static int extractGas(Level level, BlockPos pos, int max, Consumer<GasStack> drained,
            TypedKey<GasStack> key) {
        final int[] moved = { 0 };
        CustomBlockData.from(level, pos).edit(p -> {
            GasStack stored = p.getOrDefault(key, GasStack.EMPTY);
            if (stored.isEmpty())
                return;

            int toMove = Math.min(max, stored.getAmount());
            GasStack out = new GasStack(stored.getType(), toMove);
            stored.shrink(toMove);

            if (stored.isEmpty())
                p.remove(key);
            else
                p.set(key, stored);

            moved[0] = toMove;
            if (drained != null)
                drained.accept(out);
        });
        return moved[0];
    }

    public static GasStack getStoredGas(Level level, BlockPos pos, TypedKey<GasStack> key) {
        return CustomBlockData.from(level, pos).getOrDefault(key, GasStack.EMPTY);
    }
}
