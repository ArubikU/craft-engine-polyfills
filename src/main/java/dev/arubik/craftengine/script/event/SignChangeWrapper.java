package dev.arubik.craftengine.script.event;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.SignChangeEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.PlayerType;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a player finishes editing a sign, before the text is committed to the world — the
 * hook a "shop sign" or "portal sign" system would use. A script could use {@link #getLine} to
 * parse {@code [shop]} out of line 0 and, if it matches, rewrite the remaining lines with
 * {@link #setLine} to show a formatted price pulled from a config.
 *
 * <p>Text get/set uses the same MiniMessage-or-legacy convention as every other text property in
 * this package (see {@link ScriptEventUtil}), so a script can freely mix {@code <red>} tags and
 * legacy {@code &c} codes across lines.
 */
public final class SignChangeWrapper extends ScriptEvent {
    private final SignChangeEvent raw;

    public SignChangeWrapper(SignChangeEvent raw) {
        super("SignChangeEvent");
        this.raw = raw;
    }

    public SignChangeEvent raw() { return raw; }

    public ScriptValue block() {
        Block block = raw.getBlock();
        ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
        return BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ()));
    }

    public ScriptValue player() {
        return PlayerType.wrap(((CraftPlayer) raw.getPlayer()).getHandle());
    }

    /** Line {@code index} (0-3) of the sign being edited, as a MiniMessage string. */
    public String getLine(int index) {
        return ScriptEventUtil.componentToString(raw.line(index));
    }

    /** Rewrites line {@code index} (0-3) of the sign being edited. */
    public void setLine(int index, String text) {
        raw.line(index, ScriptEventUtil.parseComponent(text));
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
