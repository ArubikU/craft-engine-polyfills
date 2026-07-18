package dev.arubik.craftengine.contraption.protection;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import net.minecraft.core.BlockPos;

/**
 * Land-claim / block-protection SPI for contraptions (roadmap item #7 "Claims / protection
 * system", {@code .migration/ROADMAP-claims-and-phys.md} §1). One interface with a
 * per-position {@code canBuild}/{@code canBreak}/{@code canInteract} plus a coarse
 * {@code canUseRegion}, each returning a {@link ProtectionResult} tri-state so the
 * {@link ContraptionProtectionRegistry} can chain multiple providers with deny-wins
 * resolution.
 *
 * <p><b>Two distinct ownership axes, deliberately kept separate</b> (per the design): this SPI
 * is the <em>land-claim</em> axis — "may this player affect the block at this WORLD position?"
 * — the concern that external plugins (WorldGuard / GriefPrevention / Towny) own. The other
 * axis, <em>contraption ownership</em> ("whose contraption is this, and may this other player
 * operate/disassemble it?", i.e. anti-theft), is a purely internal concern keyed off
 * {@link dev.arubik.craftengine.contraption.ContraptionState#owner()} and lives in
 * {@link ContraptionOwnership}, NOT on this chain. Do not conflate the two.
 *
 * <p><b>Soft-dependency adapters (deferred — Phase C4, NOT implemented here).</b> The interface
 * is shaped so an external-claim adapter can be added as its own class and self-register only if
 * its plugin is present (a {@code Bukkit.getPluginManager().isPluginEnabled(...)} guard), keeping
 * the plugin loadable when the claim plugin is absent — WorldGuard maps to {@code RegionQuery},
 * GriefPrevention to {@code Claim.checkPermission}, Towny to {@code TownyAPI}. Region checks
 * ({@link #canUseRegion}) intentionally exist so an adapter can answer against its plugin's
 * <em>native</em> region index in one call instead of iterating cells — the mandatory perf
 * mitigation for the per-tick move gate. Only {@link AllowAllProtection} ships today; the
 * adapters are documented-but-unwritten Phase-1 scaffolding.
 *
 * <p><b>Actor may be offline.</b> Autonomous movement/mining is attributed to the contraption's
 * owning-player UUID, who may be offline when the check runs (per the design's "attributing
 * autonomous movement to an offline owner" risk note); providers must therefore accept a bare
 * {@link UUID} and answer via offline-safe APIs rather than requiring a live player context.
 * Every method runs on the server main thread, synchronously.
 */
public interface ContraptionProtection {

    /** May {@code actor} PLACE / write a block at {@code pos} in {@code world}? (disassemble writeback, restore) */
    ProtectionResult canBuild(UUID actor, World world, BlockPos pos);

    /** May {@code actor} BREAK / remove a block at {@code pos} in {@code world}? (capture, miner drilling) */
    ProtectionResult canBreak(UUID actor, World world, BlockPos pos);

    /** May {@code actor} INTERACT with (right-click) a block at {@code pos} in {@code world}? (land-claim axis only) */
    ProtectionResult canInteract(UUID actor, World world, BlockPos pos);

    /**
     * Coarse region query for the per-tick move gate — "may {@code actor}'s contraption occupy
     * the whole {@code box} in {@code world}?". Default {@link ProtectionResult#PASS}: a
     * cell-agnostic provider ignores region checks and lets the chain fall through; a real
     * land-claim adapter overrides this to hit its native region index (much cheaper and more
     * correct than OR-ing {@link #canBuild} over every cell in the box).
     */
    default ProtectionResult canUseRegion(UUID actor, World world, BoundingBox box) {
        return ProtectionResult.PASS;
    }

    /** Human-readable provider name for logging / diagnostics. */
    default String name() {
        return getClass().getSimpleName();
    }
}
