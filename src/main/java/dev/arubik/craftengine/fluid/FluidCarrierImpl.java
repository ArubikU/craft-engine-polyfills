package dev.arubik.craftengine.fluid;

import java.util.function.Consumer;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class FluidCarrierImpl {

    /**
     * Obtiene el fluido almacenado en un bloque, leido directamente del block entity
     * (persistido via NBT, no chunk PDC).
     */
    public static FluidStack getStored(Level level, BlockPos pos) {
        PersistentBlockEntity be = PersistentBlockEntity.getIfLoaded(level, pos);
        return be != null ? be.getOrDefault(FluidKeys.FLUID, FluidStack.EMPTY) : FluidStack.EMPTY;
    }

    /**
     * Inserta fluido en el almacenamiento del bloque.
     * 
     * @param level         El nivel
     * @param pos           La posición del bloque
     * @param stack         El fluido a insertar
     * @param capacity      La capacidad máxima del tanque
     * @param pressureBoost Aumento de presión a aplicar al fluido entrante (usado
     *                      por bombas)
     * @return La cantidad de fluido aceptada
     */
    public static int insertFluid(Level level, BlockPos pos, FluidStack stack, int capacity, int pressureBoost) {
        return insertFluid(level, pos, stack, capacity, pressureBoost, FluidKeys.FLUID);
    }

    public static int insertFluid(Level level, BlockPos pos, FluidStack stack, int capacity, int pressureBoost,
            net.minecraft.core.Direction direction) {
        return insertFluid(level, pos, stack, capacity, pressureBoost, FluidKeys.FLUID);
    }

    public static int insertFluid(Level level, BlockPos pos, FluidStack stack, int capacity, int pressureBoost,
            dev.arubik.craftengine.util.TypedKey<FluidStack> key) {
        if (stack == null || stack.isEmpty())
            return 0;

        // Pump pressure: SET to the pump's rating (max), don't ADD — adding let recirculating
        // fluid ramp pressure to absurd values (two pumps -> ~100).
        FluidStack incoming = stack;
        if (pressureBoost > 0) {
            incoming = new FluidStack(stack.getType(), stack.getAmount(),
                    Math.max(stack.getPressure(), pressureBoost));
        }

        final int[] accepted = { 0 };
        final FluidStack finalIncoming = incoming; // effectively final for lambda

        PersistentBlockEntity.executeAt(level, pos, p -> {
            FluidStack stored = p.getOrDefault(key, FluidStack.EMPTY);

            if (stored.isEmpty()) {
                int move = Math.min(capacity, finalIncoming.getAmount());
                p.set(key, new FluidStack(finalIncoming.getType(), move, finalIncoming.getPressure()));
                accepted[0] = move;
            } else if (stored.getType() == finalIncoming.getType()) {
                int space = capacity - stored.getAmount();
                if (space > 0) {
                    int oldAmt = stored.getAmount();
                    int move = Math.min(space, finalIncoming.getAmount());
                    // Amount-weighted AVERAGE pressure (not max): so fluid that climbed in at a lower
                    // pressure actually lowers this pipe's pressure over ticks, instead of max-merge
                    // pinning the whole line to the source pressure (the "9p everywhere" bug). Averaging
                    // can never exceed the highest input, so it still can't ramp from recirculation.
                    int blended = (oldAmt + move) > 0
                            ? Math.round((oldAmt * stored.getPressure() + move * finalIncoming.getPressure())
                                    / (float) (oldAmt + move))
                            : finalIncoming.getPressure();
                    stored.addAmount(move);
                    p.set(key, new FluidStack(stored.getType(), stored.getAmount(), blended));
                    accepted[0] = move;
                }
            } else {
            }
        });

        return accepted[0];
    }

    /**
     * Extrae fluido del almacenamiento del bloque.
     * 
     * @param level   El nivel
     * @param pos     La posición del bloque
     * @param max     Cantidad máxima a extraer
     * @param drained Consumer que recibe el fluido extraído
     * @return La cantidad extraída
     */
    public static int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained) {
        return extractFluid(level, pos, max, drained, FluidKeys.FLUID);
    }

    public static int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained,
            net.minecraft.core.Direction direction) {
        return extractFluid(level, pos, max, drained, FluidKeys.FLUID);
    }

    public static int extractFluid(Level level, BlockPos pos, int max, Consumer<FluidStack> drained,
            dev.arubik.craftengine.util.TypedKey<FluidStack> key) {
        final int[] moved = { 0 };
        PersistentBlockEntity.executeAt(level, pos, p -> {
            FluidStack stored = p.getOrDefault(key, FluidStack.EMPTY);
            if (stored.isEmpty())
                return;

            int toMove = Math.min(max, stored.getAmount());
            FluidStack out = new FluidStack(stored.getType(), toMove, stored.getPressure());
            stored.removeAmount(toMove);

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
}
