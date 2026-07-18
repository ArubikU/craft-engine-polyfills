package dev.arubik.craftengine.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.world.BukkitWorldManager;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * In craft-engine 26.6.2 there is no global block-entity-type registry and no
 * {@code BukkitBlockEntityTypes.register(Key)}. Block entities are created by the
 * engine and owned by the block behavior (via {@code EntityBlock#createBlockEntityController}).
 * This class keeps only the convenience lookup; callers must read {@code BlockEntity#controller}
 * to reach the concrete controller.
 */
public class BukkitBlockEntityTypes {

    /**
     * Resolves the CraftEngine block entity at {@code pos}, or {@code null}.
     *
     * <h2>Do not route this through a {@code BukkitWorld}</h2>
     * The obvious spelling — constructing a {@code BukkitWorld} per call and asking it for its
     * {@code storageWorld()} — is a
     * significant hot-path cost, because this lookup is called per block, per component, per tick by
     * the fluid graph. Decompiling {@code BukkitWorld#storageWorld} shows it is nothing but a lazy
     * cache over exactly the call below:
     *
     * <pre>
     *   if (ceWorld == null) ceWorld = BukkitWorldManager.instance().getWorld(uuid());
     *   return ceWorld;
     * </pre>
     *
     * <p>So constructing a wrapper per call throws that cache away every time and pays the
     * constructor for nothing. That constructor is not free: profiling put a measurable share of the
     * whole server thread inside {@code BukkitWorld.<init>} resolving a file path
     * ({@code File.toPath} into the platform path parser). Going straight to the manager is the same
     * {@code CEWorld} by the same code path, minus the allocation and the path parsing.
     */
    public static BlockEntity getIfLoaded(Level world, BlockPos pos) {
        if (world == null) {
            return null;
        }
        CEWorld ceWorld = BukkitWorldManager.instance().getWorld(world.getWorld().getUID());
        if (ceWorld == null) {
            return null;
        }
        return ceWorld.getBlockEntityAtIfLoaded(net.momirealms.craftengine.core.world.BlockPos.of(pos.asLong()));
    }
}
