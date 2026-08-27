package dev.arubik.craftengine.script.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityExplodeEvent;

import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.world.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when an entity (creeper, TNT minecart, charged creeper, ...) explodes — before the blocks
 * in {@link #blockList()} actually break. A script could use this to protect a claimed region:
 * filter {@link #blockList()} down to blocks outside the claim and {@link #setBlockList} the
 * trimmed list back, leaving the explosion's damage/knockback intact but sparing the protected
 * blocks.
 */
public final class EntityExplodeWrapper extends ScriptEvent {
    private final EntityExplodeEvent raw;

    public EntityExplodeWrapper(EntityExplodeEvent raw) {
        super("EntityExplodeEvent");
        this.raw = raw;
    }

    public EntityExplodeEvent raw() { return raw; }

    public ScriptValue location() { return ScriptEventUtil.wrapLocation(raw.getLocation()); }

    public List<ScriptValue> blockList() {
        List<ScriptValue> out = new ArrayList<>();
        for (Block block : raw.blockList()) {
            ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
            out.add(BlockType.wrap(level, new BlockPos(block.getX(), block.getY(), block.getZ())));
        }
        return out;
    }

    public float yield() { return raw.getYield(); }
    public void setYield(float yield) { raw.setYield(yield); }

    @Override
    public boolean isCancelled() { return ((Cancellable) raw).isCancelled(); }

    @Override
    public void setCancelled(boolean cancelled) { ((Cancellable) raw).setCancelled(cancelled); }
}
