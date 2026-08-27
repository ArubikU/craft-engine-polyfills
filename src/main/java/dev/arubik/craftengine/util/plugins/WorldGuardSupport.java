package dev.arubik.craftengine.util.plugins;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Bridge to WorldGuard's region query/flag API. {@code worldguard-bukkit}/{@code worldedit-bukkit}
 * are {@code compileOnly} dependencies (EngineHub's own repo — see build.gradle.kts); every call
 * is gated behind {@link #isAvailable()} and wrapped in {@code catch (Throwable)}, so this addon
 * compiles and runs fine on a server with no WorldGuard installed.
 */
public final class WorldGuardSupport {

    private static volatile Boolean available;
    // Custom flags a script registered via registerFlag — kept alongside WorldGuard's own
    // FlagRegistry so getRegionFlag/setRegionFlag can look one up by name without a script having
    // to hold onto the Flag<?> object itself between calls.
    private static final Map<String, Flag<?>> CUSTOM_FLAGS = new HashMap<>();

    private WorldGuardSupport() {}

    public static void reset() {
        available = null;
    }

    public static boolean isAvailable() {
        Boolean result = available;
        if (result == null) {
            result = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
            available = result;
        }
        return result;
    }

    /**
     * Registers a brand-new WorldGuard region flag by name — the script-facing counterpart of
     * writing a custom {@code Flag} subclass in Java. {@code type} is one of
     * "bool"/"string"/"int"/"double" (defaults to "string" for anything else). Intended to be
     * called once from a script's {@code __init__()} (this addon's own script-lifecycle
     * convention — see {@code ScriptRegistry}), mirroring how a real WorldGuard-dependent plugin
     * registers custom flags from its own {@code onLoad()}.
     *
     * <p><b>Known limitation:</b> WorldGuard only reliably restores a flag's PERSISTED value for
     * regions it loads AFTER that flag is registered — since this addon is a {@code softDepend}
     * (not a hard {@code depend}), it enables (and so runs any script {@code __init__()}) AFTER
     * WorldGuard has already loaded every world's region data for this server run. A flag
     * registered here therefore won't reflect a value some region may already carry for it from a
     * previous run where this SAME flag was also registered — set it again after registering and
     * it persists normally from then on across restarts. Returns false if WorldGuard is absent or
     * a DIFFERENT flag (including one of WorldGuard's own built-ins) already claimed this name.
     */
    public static boolean registerFlag(String name, String type) {
        if (!isAvailable() || name == null || name.isBlank()) return false;
        if (CUSTOM_FLAGS.containsKey(name)) return true; // already registered by an earlier __init__
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            Flag<?> existing = registry.get(name);
            if (existing != null) {
                CUSTOM_FLAGS.put(name, existing);
                return true;
            }
            Flag<?> flag = switch (type == null ? "" : type.toLowerCase(Locale.ROOT)) {
                case "bool", "boolean" -> new BooleanFlag(name);
                case "int", "integer" -> new IntegerFlag(name);
                case "double", "num", "number" -> new DoubleFlag(name);
                default -> new StringFlag(name);
            };
            registry.register(flag);
            CUSTOM_FLAGS.put(name, flag);
            return true;
        } catch (FlagConflictException conflict) {
            try {
                Flag<?> existing = WorldGuard.getInstance().getFlagRegistry().get(name);
                if (existing != null) {
                    CUSTOM_FLAGS.put(name, existing);
                    return true;
                }
            } catch (Throwable ignored) {}
            return false;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Flag<?> resolveFlag(String name) {
        if (name == null) return null;
        Flag<?> flag = CUSTOM_FLAGS.get(name);
        if (flag != null) return flag;
        try { return WorldGuard.getInstance().getFlagRegistry().get(name); }
        catch (Throwable t) { return null; }
    }

    private static RegionManager regionManager(World world) {
        try {
            return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        } catch (Throwable t) { return null; }
    }

    /** Every region id overlapping this point. Empty if WorldGuard is absent, the world has no
     *  regions, or none overlap. */
    public static List<String> regionsAt(World world, double x, double y, double z) {
        List<String> out = new ArrayList<>();
        if (!isAvailable() || world == null) return out;
        try {
            RegionManager manager = regionManager(world);
            if (manager == null) return out;
            ApplicableRegionSet set = manager.getApplicableRegions(BlockVector3.at(x, y, z));
            for (ProtectedRegion region : set) out.add(region.getId());
        } catch (Throwable ignored) {}
        return out;
    }

    /** True if this point is inside at least one region (a cheap "is this location claimed at
     *  all" check, without caring which flag/who owns it). */
    public static boolean isRegionProtected(World world, double x, double y, double z) {
        return !regionsAt(world, x, y, z).isEmpty();
    }

    /** Reads {@code flagName}'s value directly off region {@code regionId} (its OWN set value,
     *  NOT resolved through any parent-region inheritance) — works for both WorldGuard's built-in
     *  flags and one registered via {@link #registerFlag}. Null if WorldGuard/the region/the
     *  flag/a set value isn't found. */
    public static Object getRegionFlag(World world, String regionId, String flagName) {
        if (!isAvailable() || world == null || regionId == null) return null;
        try {
            RegionManager manager = regionManager(world);
            if (manager == null) return null;
            ProtectedRegion region = manager.getRegion(regionId);
            Flag<?> flag = resolveFlag(flagName);
            if (region == null || flag == null) return null;
            return region.getFlag(flag);
        } catch (Throwable ignored) { return null; }
    }

    /** Sets {@code flagName} on region {@code regionId} — {@code value}'s Java type must match the
     *  flag's own (Boolean for a bool flag, String for a string flag, Integer/Double for
     *  int/double). Returns false on any mismatch, missing region/flag, or WorldGuard absence. */
    @SuppressWarnings("unchecked")
    public static boolean setRegionFlag(World world, String regionId, String flagName, Object value) {
        if (!isAvailable() || world == null || regionId == null) return false;
        try {
            RegionManager manager = regionManager(world);
            if (manager == null) return false;
            ProtectedRegion region = manager.getRegion(regionId);
            Flag<?> flag = resolveFlag(flagName);
            if (region == null || flag == null) return false;
            region.setFlag((Flag<Object>) flag, value);
            return true;
        } catch (Throwable ignored) { return false; }
    }

    /** Script-friendly counterpart of {@link #setRegionFlag} — coerces {@code rawValue} (always a
     *  plain String from a script's perspective) to whatever Java type the resolved flag actually
     *  needs (Boolean/Integer/Double/String) instead of requiring the caller to already know it. */
    public static boolean setRegionFlagFromString(World world, String regionId, String flagName, String rawValue) {
        Flag<?> flag = resolveFlag(flagName);
        if (flag == null) return false;
        Object value = switch (flag) {
            case BooleanFlag ignored -> Boolean.valueOf(rawValue);
            case IntegerFlag ignored -> {
                try { yield Integer.valueOf(rawValue); } catch (Throwable t) { yield null; }
            }
            case DoubleFlag ignored -> {
                try { yield Double.valueOf(rawValue); } catch (Throwable t) { yield null; }
            }
            default -> rawValue;
        };
        return value != null && setRegionFlag(world, regionId, flagName, value);
    }

    /** The plain state of a named {@code StateFlag} at this point (e.g. "pvp", "mob-spawning",
     *  "invincible", ...), IGNORING region owner/member membership. Null if WorldGuard is absent,
     *  the flag name doesn't resolve to a StateFlag, or the world has no regions at all. */
    public static Boolean queryFlagState(World world, double x, double y, double z, String flagName) {
        if (!isAvailable() || world == null) return null;
        try {
            Flag<?> flag = resolveFlag(flagName);
            if (!(flag instanceof StateFlag stateFlag)) return null;
            RegionManager manager = regionManager(world);
            if (manager == null) return null;
            ApplicableRegionSet set = manager.getApplicableRegions(BlockVector3.at(x, y, z));
            return set.testState(null, stateFlag);
        } catch (Throwable ignored) { return null; }
    }

    /** Whether {@code player} may build at this point — the actual member/owner-aware check
     *  (unlike {@link #queryFlagState}, which ignores who's asking). Fails OPEN (returns true) if
     *  WorldGuard is absent or this call breaks on an unexpected WorldGuard version, so a
     *  missing/incompatible protection plugin never silently locks builders out. */
    public static boolean canBuild(Player player, double x, double y, double z) {
        if (!isAvailable() || player == null) return true;
        try {
            WorldGuardPlugin wgPlugin = WorldGuardPlugin.inst();
            LocalPlayer localPlayer = wgPlugin.wrapPlayer(player);
            RegionManager manager = regionManager(player.getWorld());
            if (manager == null) return true;
            ApplicableRegionSet set = manager.getApplicableRegions(BlockVector3.at(x, y, z));
            return set.testState(localPlayer, Flags.BUILD);
        } catch (Throwable ignored) { return true; }
    }
}
