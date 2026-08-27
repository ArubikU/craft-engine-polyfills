package dev.arubik.craftengine.cmd;

import java.util.Map;

import org.bukkit.entity.Player;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * One live command execution — the object bound as the {@code Cmd} class instance inside a
 * command's {@code execute}/{@code gui} script, OR inside a click script fired from one of that
 * command's declarative {@code pages} (see {@code CmdPageDef}). Carries the parsed argument
 * values (already type-converted and wrapped as {@link ScriptValue}s by {@link CmdRegistry}) plus
 * a little sender bookkeeping; actual persistent state a command wants to remember belongs in
 * {@code Server.*_flag} (global) or {@code Player.*_flag} (per player, already PDC-backed — see
 * {@code EntityType}) rather than a bespoke store here, so an auction house or economy command
 * composes out of primitives that already exist instead of a new one-off mechanism.
 *
 * <p>{@code player} (nullable — absent for a non-player sender) backs {@link #open_page}, letting
 * a page's own button script (e.g. a pagination Prev/Next click) reopen one of the SAME command's
 * declared pages via {@code Cmd.open_page("name")} instead of needing Java to expose some other
 * bespoke "reopen the current menu" hook.
 */
public record CmdInvocation(String commandName, String senderName, boolean isPlayer, Map<String, ScriptValue> args, Player player) {

    public ScriptValue arg(String key) {
        return args.getOrDefault(key, ScriptValue.NULL);
    }

    public boolean openPage(String pageName) {
        return player != null && CmdRegistry.openPageByCommandName(commandName, pageName, player);
    }
}
