package dev.arubik.craftengine.fluid;

import java.util.Random;

import com.mojang.datafixers.util.Pair;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public enum FluidType {
    WATER(1000),
    LAVA(1000),
    SLIME(111),
    EXPERIENCE(1),
    POWDER_SNOW(1000),
    MILK(1000),
    EMPTY(0),
    HONEY(250);

    public final int unit;

    FluidType(int unit) {
        this.unit = unit;
    }

    // Delay específico para recolectar desde bloques (permite diferenciar de I/O)
    // Delay específico para recolectar desde bloques (permite diferenciar de I/O)
    public static int blockCollectDelay(FluidType t) {
        return FluidCollector.blockCollectDelay(t);
    }

    // Delay específico para I/O con carriers (push/pull)
    public static int carrierIODelay(FluidType t) {
        if (t == null)
            return 1;
        switch (t) {
            case LAVA:
                return 8; // mover lava entre carriers es menos costoso que recolectarla de mundo
            case SLIME:
                return 8;
            case HONEY:
                return 8;
            case POWDER_SNOW:
                return 4;
            case WATER:
                return 2; // rápido
            case MILK:
                return 2;
            case EXPERIENCE:
                return 1;
            case EMPTY:
            default:
                return 1;
        }
    }

    public static final int MB_PER_BUCKET = 1000;

    // ---- Helpers unidad ----
    public int mbPerFullBlock() {
        switch (this) {
            case WATER:
            case LAVA:
            case POWDER_SNOW:
            case MILK:
                return MB_PER_BUCKET;
            case SLIME:
                return 9 * unit; // 9*250=2250
            case EXPERIENCE:
                return unit; // 1:1
            case HONEY:
                return 4 * unit; // 4*250=1000
            default:
                return 0;
        }
    }

    public int unitMb() {
        return unit;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    // --- Recolección simplificada (2 funciones públicas) ---
    // 1) Recolecta en un solo bloque hasta maxMb, sin bucles infinitos
    // --- Recolección simplificada (Delegada a FluidCollector) ---
    // 1) Recolecta en un solo bloque hasta maxMb, sin bucles infinitos
    public static FluidStack collectAt(BlockPos pos, Level level, int maxMb, FluidType preferred) {
        return FluidCollector.collectAt(pos, level, maxMb, preferred);
    }

    public static FluidType getFluidTypeAt(BlockPos pos, Level level) {
        return FluidCollector.getFluidTypeAt(pos, level);
    }

    // 2) Recolecta en un área hasta maxMb, respetando el tipo base del bloque
    // inicial
    // 2) Recolecta en un área hasta maxMb
    public static FluidStack collectArea(BlockPos pos, Level level, int radius, int maxMb, FluidType preferred) {
        return FluidCollector.collectArea(pos, level, radius, maxMb, preferred);
    }

    // (El resto de overloads antiguos se han eliminado a favor de estas 2
    // funciones)

    public static boolean place(FluidStack stack, BlockPos pos, Level level) {
        return FluidPlacer.place(stack, pos, level);
    }

    public static boolean place(FluidStack stack, BlockPos pos, Level level, int radius) {
        return FluidPlacer.place(stack, pos, level, radius);
    }

    public boolean isSourceBlock(FluidState state) {
        if (this == EMPTY)
            return false;
        if (this == WATER)
            return state.is(net.minecraft.world.level.material.Fluids.FLOWING_WATER)
                    || state.is(net.minecraft.world.level.material.Fluids.WATER);
        if (this == LAVA)
            return state.is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA)
                    || state.is(net.minecraft.world.level.material.Fluids.LAVA);
        return false;
    }

    public static boolean matches(FluidType t, FluidState state) {
        if (t == null || state == null || state.isEmpty())
            return false;
        return (t == WATER && (state.is(net.minecraft.world.level.material.Fluids.WATER)
                || state.is(net.minecraft.world.level.material.Fluids.FLOWING_WATER)))
                || (t == LAVA && (state.is(net.minecraft.world.level.material.Fluids.LAVA)
                        || state.is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA)));
    }

    // Carrier helpers
    public static int depositToCarrier(FluidCarrier carrier, Level level, BlockPos pos, FluidStack stack) {
        return depositToCarrier(carrier, level, pos, stack, null);
    }

    public static int depositToCarrier(FluidCarrier carrier, Level level, BlockPos pos, FluidStack stack,
            net.minecraft.core.Direction direction) {
        if (carrier == null || stack == null || stack.isEmpty())
            return 0;
        return carrier.insertFluid(level, pos, stack, direction);
    }

    public static int extractFromCarrier(FluidCarrier carrier, Level level, BlockPos pos, int mb,
            java.util.function.Consumer<FluidStack> drained) {
        return extractFromCarrier(carrier, level, pos, mb, drained, null);
    }

    public static int extractFromCarrier(FluidCarrier carrier, Level level, BlockPos pos, int mb,
            java.util.function.Consumer<FluidStack> drained, net.minecraft.core.Direction direction) {
        if (carrier == null || mb <= 0)
            return 0;
        return carrier.extractFluid(level, pos, mb, drained, direction);
    }

    public static Pair<FluidStack, ItemStack> collectFromStack(ItemStack stack) {
        return FluidItemConverter.collectFromStack(stack);
    }

    public static Pair<ItemStack, FluidStack> collectToStack(ItemStack container, FluidStack fluid,
            int requestedAmount) {
        return FluidItemConverter.collectToStack(container, fluid, requestedAmount);
    }

    /**
     * Reacción de un fluido con el ítem del jugador: consume parte del fluido y
     * transforma el ítem.
     * Devuelve el nuevo Fluido restante y una lista de ítems generados (por
     * ejemplo, cubos o botellas).
     */
    /**
     * Reacción de un fluido con el ítem del jugador: consume parte del fluido y
     * transforma el ítem.
     * Devuelve el nuevo Fluido restante y una lista de ítems generados (por
     * ejemplo, cubos o botellas).
     */
    public static Pair<FluidStack, java.util.List<ItemStack>> reaction(FluidStack fluid, ItemStack playerItem) {
        return FluidReactions.reaction(fluid, playerItem);
    }

    /**
     * Intenta dispersar un fluido en un bloque de aire de manera inteligente.
     * Para XP: genera partículas de experiencia
     * Para líquidos colocables: intenta colocar el bloque
     * 
     * @param stack El fluido a dispersar
     * @param pos   La posición del bloque de aire
     * @param level El mundo
     * @return La cantidad de fluido consumido
     */
    public static int disperseIntoAir(FluidStack stack, BlockPos pos, Level level) {
        return FluidPlacer.disperseIntoAir(stack, pos, level);
    }

    public static final Random RANDOM = new Random();

    @SuppressWarnings("deprecation")
    public static void spawnXpOrb(Level level, BlockPos pos, int xp) {
        ExperienceOrb orb = new ExperienceOrb(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xp);
        level.addFreshEntity(orb);
    }
}
