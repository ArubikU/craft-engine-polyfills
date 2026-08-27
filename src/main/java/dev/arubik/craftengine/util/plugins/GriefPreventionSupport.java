package dev.arubik.craftengine.util.plugins;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Bridge to GriefPrevention's claim API. {@code GriefPrevention} is a {@code compileOnly}
 * dependency (via JitPack, compiled against the full plugin jar since it has no separate slim API
 * module — see build.gradle.kts); every call is gated behind {@link #isAvailable()} and wrapped in
 * {@code catch (Throwable)}, so this addon compiles and runs fine on a server with no
 * GriefPrevention installed.
 */
public final class GriefPreventionSupport {

    private GriefPreventionSupport() {}

    public static boolean isAvailable() {
        try { return Bukkit.getPluginManager().isPluginEnabled("GriefPrevention") && GriefPrevention.instance != null; }
        catch (Throwable t) { return false; }
    }

    /** True if {@code loc} falls inside any claim at all. */
    public static boolean isClaimed(Location loc) {
        return claimAt(loc) != null;
    }

    private static Claim claimAt(Location loc) {
        if (!isAvailable() || loc == null) return null;
        try { return GriefPrevention.instance.dataStore.getClaimAt(loc, false, null); }
        catch (Throwable ignored) { return null; }
    }

    /** The owning player's UUID for the claim at {@code loc} — null if unclaimed, an admin claim
     *  (no single owner), or GriefPrevention is absent. */
    public static UUID claimOwner(Location loc) {
        Claim claim = claimAt(loc);
        if (claim == null) return null;
        try { return claim.isAdminClaim() ? null : claim.ownerID; } catch (Throwable ignored) { return null; }
    }

    /** Whether {@code player} may build at {@code loc} — GriefPrevention's own real permission
     *  check (owner/trust-aware), not just "is this claimed". Fails OPEN (returns true) if
     *  GriefPrevention is absent, same "an absent integration never silently restricts" contract
     *  every bridge here follows. */
    public static boolean canBuild(Player player, Location loc) {
        if (!isAvailable() || player == null || loc == null) return true;
        try { return GriefPrevention.instance.allowBuild(player, loc) == null; }
        catch (Throwable ignored) { return true; }
    }
}
