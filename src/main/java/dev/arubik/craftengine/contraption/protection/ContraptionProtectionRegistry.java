package dev.arubik.craftengine.contraption.protection;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import net.minecraft.core.BlockPos;

/**
 * Ordered provider chain that resolves {@link ContraptionProtection} votes with
 * <b>deny-wins</b> semantics (roadmap item #7, {@code .migration/ROADMAP-claims-and-phys.md} §1
 * "ProtectionRegistry"). Every enforcement site in the contraption package calls one of the
 * {@code canBuild}/{@code canBreak}/{@code canInteract}/{@code canUseRegion} statics here and
 * gets back a plain {@code boolean} ("is this action allowed?").
 *
 * <p><b>Resolution:</b> walk providers in registration order; the FIRST
 * {@link ProtectionResult#DENY} short-circuits to {@code false}. If no provider denies (every
 * vote {@link ProtectionResult#ALLOW}/{@link ProtectionResult#PASS}), fall through to the
 * configurable default — currently always allow, for backward compatibility. A provider that
 * throws is treated as {@link ProtectionResult#PASS} (fail-open) so a misbehaving adapter can
 * never wedge the enforcement path.
 *
 * <p><b>Default state:</b> {@link AllowAllProtection} is registered in the static initializer,
 * so a stock install (no external adapter) resolves every query to {@code true} — behavior is
 * identical to before this system existed. Adapters (WorldGuard / GriefPrevention / Towny) would
 * {@link #register} themselves AHEAD of / alongside it in a later phase (C4, not implemented).
 */
public final class ContraptionProtectionRegistry {

    private ContraptionProtectionRegistry() {
    }

    /**
     * The provider chain. {@code CopyOnWriteArrayList} because it's read on the hot tick path
     * (move gate, miner) but written only at plugin enable / adapter registration — cheap reads,
     * rare writes, no external locking needed.
     */
    private static final CopyOnWriteArrayList<ContraptionProtection> PROVIDERS = new CopyOnWriteArrayList<>();

    static {
        // Default allow-all — see class javadoc. Keeps a stock install behavior-identical.
        PROVIDERS.add(new AllowAllProtection());
    }

    /** Appends a provider to the end of the chain. Deny-wins means order only matters for short-circuit cost, not correctness. */
    public static void register(ContraptionProtection provider) {
        if (provider != null) {
            PROVIDERS.add(provider);
        }
    }

    /** Removes a previously-registered provider (e.g. an adapter unregistering on its plugin's disable). */
    public static void unregister(ContraptionProtection provider) {
        PROVIDERS.remove(provider);
    }

    /** Immutable snapshot of the current chain, for diagnostics. */
    public static java.util.List<ContraptionProtection> providers() {
        return java.util.List.copyOf(PROVIDERS);
    }

    /** May {@code actor} place/write a block at {@code pos}? Deny-wins; all-PASS → allow (default). */
    public static boolean canBuild(UUID actor, World world, BlockPos pos) {
        return resolve(p -> p.canBuild(actor, world, pos));
    }

    /** May {@code actor} break/remove a block at {@code pos}? Deny-wins; all-PASS → allow (default). */
    public static boolean canBreak(UUID actor, World world, BlockPos pos) {
        return resolve(p -> p.canBreak(actor, world, pos));
    }

    /** May {@code actor} interact with a block at {@code pos} (land-claim axis)? Deny-wins; all-PASS → allow. */
    public static boolean canInteract(UUID actor, World world, BlockPos pos) {
        return resolve(p -> p.canInteract(actor, world, pos));
    }

    /** May {@code actor}'s contraption occupy {@code box}? Coarse region query for the move gate. Deny-wins; all-PASS → allow. */
    public static boolean canUseRegion(UUID actor, World world, BoundingBox box) {
        return resolve(p -> p.canUseRegion(actor, world, box));
    }

    /**
     * Deny-wins fold over the chain: {@code false} on the first {@link ProtectionResult#DENY},
     * otherwise {@code true} (the allow default). A provider throwing is swallowed as
     * {@link ProtectionResult#PASS} — fail-open, so a broken adapter never blocks legitimate
     * actions nor crashes the tick loop.
     */
    private static boolean resolve(Function<ContraptionProtection, ProtectionResult> query) {
        for (ContraptionProtection provider : PROVIDERS) {
            ProtectionResult result;
            try {
                result = query.apply(provider);
            } catch (Throwable t) {
                result = ProtectionResult.PASS; // fail-open — a misbehaving provider abstains
            }
            if (result == ProtectionResult.DENY) {
                return false;
            }
        }
        return true;
    }
}
