package dev.arubik.craftengine.util.plugins;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Bridge to MythicMobs' spawn/lookup/skill API via {@link BukkitAPIHelper} — the same high-level
 * entry point MythicMobs' own docs point third-party plugins at (rather than the lower-level
 * manager interfaces, which change more often). {@code Mythic-Dist} is a {@code compileOnly}
 * dependency (Lumine's own repo — see build.gradle.kts); every call is gated behind {@link
 * #isAvailable()} and wrapped in {@code catch (Throwable)}, so this addon compiles and runs fine
 * on a server with no MythicMobs installed.
 */
public final class MythicMobsSupport {

    private MythicMobsSupport() {}

    public static boolean isAvailable() {
        try { return Bukkit.getPluginManager().isPluginEnabled("MythicMobs"); }
        catch (Throwable t) { return false; }
    }

    private static BukkitAPIHelper helper() {
        return MythicBukkit.inst().getAPIHelper();
    }

    /** Spawns a MythicMobs mob by its internal name (config id, not display name) at {@code loc},
     *  returning the resulting Bukkit entity — null if MythicMobs is absent, the mob type doesn't
     *  exist, or spawning otherwise fails. */
    public static Entity spawnMob(String mobType, Location loc) {
        if (!isAvailable() || mobType == null || loc == null) return null;
        try { return helper().spawnMythicMob(mobType, loc); }
        catch (Throwable ignored) { return null; }
    }

    /** Same as {@link #spawnMob(String, Location)} but at an explicit mob level (affects stats
     *  scaling the same way {@code /mm mobs spawn <type> <level>} does in-game). */
    public static Entity spawnMob(String mobType, Location loc, int level) {
        if (!isAvailable() || mobType == null || loc == null) return null;
        try { return helper().spawnMythicMob(mobType, loc, level); }
        catch (Throwable ignored) { return null; }
    }

    /** True if {@code entity} is a currently-tracked MythicMobs mob. */
    public static boolean isMythicMob(Entity entity) {
        if (!isAvailable() || entity == null) return false;
        try { return helper().isMythicMob(entity); } catch (Throwable ignored) { return false; }
    }

    private static ActiveMob activeMob(Entity entity) {
        if (!isAvailable() || entity == null) return null;
        try { return helper().getMythicMobInstance(entity); } catch (Throwable ignored) { return null; }
    }

    /** {@code entity}'s MythicMobs internal mob-type name, or "" if it isn't one (see
     *  {@link #isMythicMob}) or MythicMobs is absent. */
    public static String mobType(Entity entity) {
        ActiveMob mob = activeMob(entity);
        if (mob == null) return "";
        try {
            var type = mob.getType();
            return type != null ? type.getInternalName() : "";
        } catch (Throwable ignored) { return ""; }
    }

    /** {@code entity}'s CURRENT resolved display name (after MythicMobs' own placeholder/color
     *  processing) — distinct from the mob TYPE's own raw display-name template. */
    public static String displayName(Entity entity) {
        ActiveMob mob = activeMob(entity);
        if (mob == null) return "";
        try {
            String name = mob.getDisplayName();
            return name != null ? name : "";
        } catch (Throwable ignored) { return ""; }
    }

    /** {@code entity}'s mob level (affects its stat scaling) — 0 if it isn't an active MythicMobs
     *  mob or MythicMobs is absent. */
    public static double mobLevel(Entity entity) {
        ActiveMob mob = activeMob(entity);
        if (mob == null) return 0.0;
        try { return mob.getLevel(); } catch (Throwable ignored) { return 0.0; }
    }

    public static String faction(Entity entity) {
        ActiveMob mob = activeMob(entity);
        if (mob == null) return "";
        try { return mob.hasFaction() ? mob.getFaction() : ""; } catch (Throwable ignored) { return ""; }
    }

    public static boolean setFaction(Entity entity, String faction) {
        ActiveMob mob = activeMob(entity);
        if (mob == null || faction == null) return false;
        try { mob.setFaction(faction); return true; } catch (Throwable ignored) { return false; }
    }

    /** Despawns {@code entity} the "clean" MythicMobs way (fires its own death/despawn hooks,
     *  unlike a plain {@code Entity.remove()}) — false if it isn't an active MythicMobs mob. */
    public static boolean despawn(Entity entity) {
        ActiveMob mob = activeMob(entity);
        if (mob == null) return false;
        try { mob.despawn(); return true; } catch (Throwable ignored) { return false; }
    }

    /** Every registered MythicMobs internal mob-type name (config ids) — empty if MythicMobs is
     *  absent. */
    public static List<String> mobTypeNames() {
        if (!isAvailable()) return List.of();
        try { return new ArrayList<>(MythicBukkit.inst().getMobManager().getMobNames()); }
        catch (Throwable ignored) { return List.of(); }
    }

    /** Casts {@code skillName} (a MythicMobs skill file/mechanic-list name, not a mob type) from
     *  {@code caster} — the caster does NOT need to be a MythicMobs mob itself (a plain
     *  player/vanilla entity can cast a standalone skill too, e.g. as an item's ability). */
    public static boolean castSkill(Entity caster, String skillName) {
        if (!isAvailable() || caster == null || skillName == null) return false;
        try { return helper().castSkill(caster, skillName); } catch (Throwable ignored) { return false; }
    }

    /** Same as {@link #castSkill(Entity, String)} but targeted at a specific location (skills
     *  using {@code targetlocation} triggers/mechanics need this instead). */
    public static boolean castSkillAt(Entity caster, String skillName, Location target) {
        if (!isAvailable() || caster == null || skillName == null || target == null) return false;
        try { return helper().castSkill(caster, skillName, target); } catch (Throwable ignored) { return false; }
    }
}
