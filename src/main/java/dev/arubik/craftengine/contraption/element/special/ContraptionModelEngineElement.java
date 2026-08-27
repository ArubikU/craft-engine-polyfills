package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.machine.render.renderer.MegRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.List;

/**
 * Contraption element that mirrors a {@link MegRenderer} into the bearing's
 * world-space position. ModelEngine manages a real Bukkit INTERACTION entity, so this element
 * does not need per-player show/hide logic — ME handles player visibility itself.
 */
public final class ContraptionModelEngineElement implements ContraptionElement {

    private final BlockPos localPos;
    private final MegRenderer source;

    public ContraptionModelEngineElement(BlockPos localPos, MegRenderer source) {
        this.localPos = localPos;
        this.source = source;
    }

    public BlockPos localPos() {
        return localPos;
    }

    @Override
    public Key type() {
        return ElementTypes.MODEL_ENGINE;
    }

    @Override
    public Vec3 localOffset() {
        return new Vec3(localPos.getX() + 0.5, localPos.getY() + 0.5, localPos.getZ() + 0.5);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int[] entityIds() {
        return new int[0];
    }

    @Override
    public void render(RenderContext ctx) {
        if (!MegRenderer.available()) return;
        if (!source.isShown()) return;

        if (ctx.level() == null) return;
        Vec3 realPos = ctx.level().realWorldPositionOf(localPos);

        org.bukkit.World world = ctx.realLevel() != null ? ctx.realLevel().getWorld() : null;
        if (world == null) return;

        float yawDeg = (float) ctx.yawDegrees();
        source.setLocation(world, realPos.x - 0.5, realPos.y - 0.5, realPos.z - 0.5, yawDeg);

        String anim = source.loopAnimation();
        if (anim != null) {
            source.playLoop(anim);
        } else {
            source.stopAnim();
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        // ModelEngine manages entity visibility — no-op.
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        source.close();
    }
}
