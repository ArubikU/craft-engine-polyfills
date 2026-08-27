package dev.arubik.craftengine.conveyor.belt;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/** Turns a {@link BeltType}'s properties into concrete runtime values/decisions — split out of
 *  {@link ConveyorBlockEntity} so that class stays focused on the belt state machine, not
 *  belt_types interpretation. Pure functions of their arguments; {@code type} may be {@code null}. */
final class BeltRuntime {

    private BeltRuntime() {
    }

    static float carryHeight(BeltType type) {
        return type != null ? type.height() : ConveyorMath.BELT_TOP_Y;
    }

    static float itemScale(BeltType type) {
        return type != null ? type.itemScale() : BeltType.BeltProperties.DEFAULT_ITEM_SCALE;
    }

    static double pickupRadius(BeltType type) {
        return type != null ? type.pickupRadius() : BeltType.BeltProperties.DEFAULT_PICKUP_RADIUS;
    }

    static int maxLength(BeltType type) {
        return type != null ? type.maxLength() : BeltType.BeltProperties.DEFAULT_MAX_LENGTH;
    }

    /** {@link BeltType#speedFormula()} if set (evaluated with rpm/base_rpm/base_travel_ticks
     *  bound), else the plain {@link ConveyorMath#progressPerTick}. Falls back on any formula
     *  error rather than stalling the belt. */
    static float progressPerTick(BeltType type, float effectiveRpm, float baseRpm, int baseTravelTicks) {
        String formula = type != null ? type.speedFormula() : null;
        if (formula != null && !formula.isBlank()) {
            try {
                dev.arubik.craftengine.script.ScriptContext ctx = dev.arubik.craftengine.script.ScriptContext.builder()
                        .val("rpm", dev.arubik.craftengine.script.ScriptValue.of((double) effectiveRpm))
                        .val("base_rpm", dev.arubik.craftengine.script.ScriptValue.of((double) baseRpm))
                        .val("base_travel_ticks", dev.arubik.craftengine.script.ScriptValue.of((double) baseTravelTicks))
                        .build();
                float result = (float) dev.arubik.craftengine.script.ScriptFormula.compile(formula).evaluateNum(ctx);
                return Math.max(0.0f, result);
            } catch (Throwable ignored) {
            }
        }
        return ConveyorMath.progressPerTick(effectiveRpm, baseRpm, baseTravelTicks);
    }

    /** {@code true} unless ELECTRIC and the buffer at {@code pos} can't cover energyPerTick this
     *  tick (in which case it's drawn down and {@code false} returned to stall the belt). Fails
     *  open on lookup error. */
    static boolean hasRequiredPower(BeltType type, Level level, BlockPos pos) {
        if (type == null || type.energyType() != BeltType.EnergyKind.ELECTRIC)
            return true;
        int need = type.energyPerTick();
        if (need <= 0 || level == null)
            return true;
        try {
            if (dev.arubik.craftengine.energy.EnergyCarrierImpl.getStoredEnergy(level, pos) < need)
                return false;
            return dev.arubik.craftengine.energy.EnergyCarrierImpl.extractEnergy(level, pos, need) >= need;
        } catch (Throwable ignored) {
            return true;
        }
    }

    /** Fallback when no {@code ConveyorReceiver} neighbor was found — pushes into a plain
     *  {@link net.minecraft.world.Container} at one of {@code candidates}, only if its block id is
     *  in {@code type}'s {@link BeltType#connectsTo()} allowlist (mirrors {@code PipeType#connectsTo}). */
    static boolean tryPushIntoConnectedContainer(BeltType type, Level level,
            Iterable<net.momirealms.craftengine.core.world.BlockPos> candidates, ItemStack item) {
        if (type == null || level == null)
            return false;
        java.util.Set<String> connectsTo = type.connectsTo();
        if (connectsTo.isEmpty())
            return false;
        try {
            for (net.momirealms.craftengine.core.world.BlockPos cand : candidates) {
                BlockPos nmsPos = new BlockPos(cand.x(), cand.y(), cand.z());
                net.minecraft.world.level.block.state.BlockState state = level.getBlockState(nmsPos);
                ImmutableBlockState custom = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                        .getOptionalCustomBlockState(state).orElse(null);
                if (custom == null || custom.isEmpty())
                    continue;
                BlockDefinition def = (BlockDefinition) custom.owner().value();
                if (!connectsTo.contains(def.id().toString()))
                    continue;
                var containerOpt = dev.arubik.craftengine.pipe.item.ItemTransferHelper.getContainer(level, nmsPos);
                if (containerOpt.isEmpty())
                    continue;
                net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
                net.minecraft.world.item.ItemStack leftover = dev.arubik.craftengine.script.types.util.ContainerType
                        .push(containerOpt.get(), nmsStack);
                if (leftover.isEmpty())
                    return true;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }
}
