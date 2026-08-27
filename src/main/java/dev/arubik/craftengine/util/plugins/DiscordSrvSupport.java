package dev.arubik.craftengine.util.plugins;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Bridge to DiscordSRV. {@code discordsrv} is a {@code compileOnly} dependency (see
 * build.gradle.kts); every call is gated behind {@link #isAvailable()} and wrapped in {@code catch
 * (Throwable)}, so this addon compiles and runs fine on a server with no DiscordSRV installed.
 */
public final class DiscordSrvSupport {

    private DiscordSrvSupport() {}

    public static boolean isAvailable() {
        try { return Bukkit.getPluginManager().isPluginEnabled("DiscordSRV"); }
        catch (Throwable t) { return false; }
    }

    /** Sends {@code message} to the Discord channel DiscordSRV's config maps {@code gameChannel}
     *  (the in-game channel NAME from config.yml's {@code Channels} section, e.g. "global" — NOT a
     *  raw Discord channel id) to. False if DiscordSRV is absent or that channel isn't configured. */
    public static boolean sendMessage(String gameChannel, String message) {
        if (!isAvailable() || gameChannel == null || message == null) return false;
        try {
            TextChannel channel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(gameChannel);
            if (channel == null) return false;
            DiscordUtil.sendMessage(channel, message);
            return true;
        } catch (Throwable ignored) { return false; }
    }

    /** Sends {@code message} to DiscordSRV's configured main text channel directly. */
    public static boolean sendToMainChannel(String message) {
        if (!isAvailable() || message == null) return false;
        try {
            TextChannel channel = DiscordSRV.getPlugin().getMainTextChannel();
            if (channel == null) return false;
            DiscordUtil.sendMessage(channel, message);
            return true;
        } catch (Throwable ignored) { return false; }
    }

    /** The Discord user id linked to {@code uuid}, or "" if unlinked/DiscordSRV is absent. May
     *  briefly block on a cache miss the first time (DiscordSRV's own storage lookup) — cheap in
     *  practice (in-memory/local file backed), unlike the genuinely network-bound lookups this
     *  addon's other bridges deliberately avoid. */
    public static String discordId(UUID uuid) {
        if (!isAvailable() || uuid == null) return "";
        try {
            String id = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
            return id != null ? id : "";
        } catch (Throwable ignored) { return ""; }
    }

    /** The Minecraft UUID linked to Discord user id {@code discordId}, or null if unlinked/absent. */
    public static UUID minecraftUuid(String discordId) {
        if (!isAvailable() || discordId == null) return null;
        try { return DiscordSRV.getPlugin().getAccountLinkManager().getUuid(discordId); }
        catch (Throwable ignored) { return null; }
    }
}
