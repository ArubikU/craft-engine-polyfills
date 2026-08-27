package dev.arubik.craftengine.script.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.world.PortalCreateEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import dev.arubik.craftengine.script.types.world.WorldType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a nether/end portal is about to form (a player lighting an obsidian frame, a nether
 * portal generating naturally on the other side of a link, ...). A script could use {@link
 * #reason()} to only allow {@code "fire"}-lit portals (blocking {@code "ender_pearl"}-triggered
 * ones, say) via {@link ScriptEvent#cancel}.
 */
public final class PortalCreateWrapper extends ScriptEvent {
    private final PortalCreateEvent raw;

    public PortalCreateWrapper(PortalCreateEvent raw) {
        super("PortalCreateEvent");
        this.raw = raw;
    }

    public PortalCreateEvent raw() { return raw; }

    public ScriptValue world() {
        ServerLevel level = ((CraftWorld) raw.getWorld()).getHandle();
        return WorldType.wrap(level);
    }

    /** e.g. {@code "fire"}, {@code "nether_pair"}, {@code "end_platform"}, {@code "glowstone"},
     *  {@code "custom"}. */
    public String reason() { return raw.getReason().name().toLowerCase(Locale.ROOT); }

    /** The blocks about to be turned into portal blocks. {@code getBlocks()} returns immutable
     *  {@code BlockState} snapshots rather than live {@code Block}s, but each still carries a real
     *  world+position, so each is re-wrapped as the live block at that position. */
    public List<ScriptValue> blocks() {
        List<ScriptValue> out = new ArrayList<>();
        for (BlockState state : raw.getBlocks()) {
            ServerLevel level = ((CraftWorld) state.getWorld()).getHandle();
            out.add(BlockType.wrap(level, new BlockPos(state.getX(), state.getY(), state.getZ())));
        }
        return out;
    }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
