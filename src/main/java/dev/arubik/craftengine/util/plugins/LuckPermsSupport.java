package dev.arubik.craftengine.util.plugins;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bridge to LuckPerms — permission/group READ and WRITE. {@code net.luckperms:api} is a {@code
 * compileOnly} dependency (Maven Central — see build.gradle.kts); every call is gated behind
 * {@link #isAvailable()} and wrapped in {@code catch (Throwable)}, so this addon compiles and runs
 * fine without LuckPerms installed.
 *
 * <p>Deliberately limited to the ONLINE-player, cached-data surface for USER lookups: {@code
 * UserManager#getUser} only ever returns a hit for a user LuckPerms already has loaded (in
 * practice, an online player), never triggering a load. Reaching an OFFLINE player would need
 * {@code loadUser(uuid).join()}, which BLOCKS the calling thread on a database round trip — since
 * every script this addon runs executes on the main server thread, that join() is a real freeze
 * risk, so it is deliberately never used here. GROUP lookups have no such limitation (LuckPerms
 * loads every group up front at startup and keeps them all resident), so group-side operations
 * ({@link #groupHasPermission}/{@link #setGroupPermission}) work regardless of who's online.
 *
 * <p>Every mutation (group add/remove, permission add/remove, primary group) updates the LIVE
 * in-memory user/group immediately (so a permission check right after reflects it) and fires an
 * async {@code saveUser}/{@code saveGroup} to persist it — deliberately NOT awaited (`.join()`),
 * for the same main-thread-blocking reason lookups avoid it; the save completes in the background.
 */
public final class LuckPermsSupport {

    private static volatile LuckPerms api;
    private static volatile boolean resolved = false;

    private LuckPermsSupport() {}

    public static void reset() {
        resolved = false;
        api = null;
    }

    private static LuckPerms api() {
        if (!resolved) {
            synchronized (LuckPermsSupport.class) {
                if (!resolved) {
                    LuckPerms found = null;
                    try {
                        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
                            found = LuckPermsProvider.get();
                        }
                    } catch (Throwable ignored) {}
                    api = found;
                    resolved = true;
                }
            }
        }
        return api;
    }

    public static boolean isAvailable() {
        return api() != null;
    }

    /** The cached LuckPerms {@code User} object for {@code uuid}, or null if LuckPerms is absent
     *  or that user isn't currently loaded (see class javadoc — never blocks to load one). */
    private static User cachedUser(UUID uuid) {
        LuckPerms lp = api();
        if (lp == null || uuid == null) return null;
        try { return lp.getUserManager().getUser(uuid); } catch (Throwable t) { return null; }
    }

    private static Group group(String name) {
        LuckPerms lp = api();
        if (lp == null || name == null) return null;
        try { return lp.getGroupManager().getGroup(name); } catch (Throwable t) { return null; }
    }

    // ---- User: reads --------------------------------------------------------------------------

    public static String primaryGroup(UUID uuid) {
        User user = cachedUser(uuid);
        if (user == null) return "";
        try {
            String group = user.getPrimaryGroup();
            return group != null ? group : "";
        } catch (Throwable t) { return ""; }
    }

    /** Every group {@code uuid} inherits from directly (not resolved through further inheritance
     *  chains) — "" is never one of these; see {@link #primaryGroup} for that. */
    public static List<String> groups(UUID uuid) {
        User user = cachedUser(uuid);
        if (user == null) return List.of();
        try {
            List<String> out = new ArrayList<>();
            for (InheritanceNode node : user.getNodes(NodeType.INHERITANCE)) out.add(node.getGroupName());
            return out;
        } catch (Throwable t) { return List.of(); }
    }

    public static boolean hasGroup(UUID uuid, String group) {
        if (group == null) return false;
        for (String g : groups(uuid)) if (g.equalsIgnoreCase(group)) return true;
        return false;
    }

    /** A LuckPerms meta node's value (e.g. a "prefix"/"suffix"/custom rank-scaling number stored
     *  as meta) — "" if unset, LuckPerms is absent, or the user isn't loaded. */
    public static String metaValue(UUID uuid, String key) {
        User user = cachedUser(uuid);
        if (user == null || key == null) return "";
        try {
            String value = user.getCachedData().getMetaData().getMetaValue(key);
            return value != null ? value : "";
        } catch (Throwable t) { return ""; }
    }

    /** Permission check via LuckPerms' own cached permission tree — same effective result as
     *  {@code Player.has_permission} for an ONLINE player (both ultimately resolve through the
     *  same Bukkit permissible when LuckPerms is the permission plugin), offered here mainly for
     *  parity/consistency for a script that's already reaching into LuckPerms for group/meta data
     *  and wants one shape for all three instead of mixing Player.has_permission in too. */
    public static boolean hasPermission(UUID uuid, String node) {
        User user = cachedUser(uuid);
        if (user == null || node == null) return false;
        try {
            return user.getCachedData().getPermissionData().checkPermission(node).asBoolean();
        } catch (Throwable t) { return false; }
    }

    // ---- User: writes ---------------------------------------------------------------------

    /** Adds {@code group} as one of {@code uuid}'s inherited groups (does NOT change their
     *  primary group — see {@link #setPrimaryGroup}). Takes effect immediately for permission
     *  checks; persisted in the background. */
    public static boolean addGroup(UUID uuid, String group) {
        User user = cachedUser(uuid);
        if (user == null || group == null) return false;
        try {
            user.data().add(InheritanceNode.builder(group).build());
            api().getUserManager().saveUser(user);
            return true;
        } catch (Throwable t) { return false; }
    }

    public static boolean removeGroup(UUID uuid, String group) {
        User user = cachedUser(uuid);
        if (user == null || group == null) return false;
        try {
            user.data().clear(node -> node.getType() == NodeType.INHERITANCE
                    && NodeType.INHERITANCE.cast(node).getGroupName().equalsIgnoreCase(group));
            api().getUserManager().saveUser(user);
            return true;
        } catch (Throwable t) { return false; }
    }

    public static boolean setPrimaryGroup(UUID uuid, String group) {
        User user = cachedUser(uuid);
        if (user == null || group == null) return false;
        try {
            user.setPrimaryGroup(group);
            api().getUserManager().saveUser(user);
            return true;
        } catch (Throwable t) { return false; }
    }

    public static boolean addPermission(UUID uuid, String node, boolean value) {
        User user = cachedUser(uuid);
        if (user == null || node == null) return false;
        try {
            user.data().add(PermissionNode.builder(node).value(value).build());
            api().getUserManager().saveUser(user);
            return true;
        } catch (Throwable t) { return false; }
    }

    public static boolean removePermission(UUID uuid, String node) {
        User user = cachedUser(uuid);
        if (user == null || node == null) return false;
        try {
            user.data().clear(n -> n.getType() == NodeType.PERMISSION
                    && NodeType.PERMISSION.cast(n).getPermission().equalsIgnoreCase(node));
            api().getUserManager().saveUser(user);
            return true;
        } catch (Throwable t) { return false; }
    }

    // ---- Groups: always-loaded, so these work regardless of who's online -----------------------

    public static boolean groupExists(String group) {
        return group(group) != null;
    }

    public static boolean groupHasPermission(String group, String node) {
        Group g = group(group);
        if (g == null || node == null) return false;
        try { return g.getCachedData().getPermissionData().checkPermission(node).asBoolean(); }
        catch (Throwable t) { return false; }
    }

    public static boolean setGroupPermission(String group, String node, boolean value) {
        Group g = group(group);
        if (g == null || node == null) return false;
        try {
            g.data().add(PermissionNode.builder(node).value(value).build());
            api().getGroupManager().saveGroup(g);
            return true;
        } catch (Throwable t) { return false; }
    }

    public static boolean removeGroupPermission(String group, String node) {
        Group g = group(group);
        if (g == null || node == null) return false;
        try {
            g.data().clear(n -> n.getType() == NodeType.PERMISSION
                    && NodeType.PERMISSION.cast(n).getPermission().equalsIgnoreCase(node));
            api().getGroupManager().saveGroup(g);
            return true;
        } catch (Throwable t) { return false; }
    }

    /** Every group {@code group} itself inherits from (its own parent groups). */
    public static List<String> groupParents(String group) {
        Group g = group(group);
        if (g == null) return List.of();
        try {
            List<String> out = new ArrayList<>();
            for (InheritanceNode node : g.getNodes(NodeType.INHERITANCE)) out.add(node.getGroupName());
            return out;
        } catch (Throwable t) { return List.of(); }
    }
}
