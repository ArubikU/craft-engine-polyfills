package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.config.ContraptionConfig;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.FluidRendererElement;
import dev.arubik.craftengine.contraption.element.RenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.List;

/**
 * Renders vanilla water/lava inside a contraption using CE fluid item models (FluidRendererElement).
 * Driven by fluid_render config in contraptions.yml. Lava applies fire damage each tick.
 */
public final class ContraptionVanillaFluidElement implements ContraptionElement {

    private final BlockPos localPos;
    private final boolean isLava;
    private FluidRendererElement renderer;
    private int lastFluidLevel = -1;
    private int damageTimer = 0;
    private static final int DAMAGE_INTERVAL = 20;

    public ContraptionVanillaFluidElement(BlockPos localPos, boolean isLava, int initialLevel) {
        this.localPos = localPos;
        this.isLava = isLava;
        this.renderer = buildRenderer(initialLevel);
        this.lastFluidLevel = initialLevel;
    }

    @Override public Key type() { return ElementTypes.FLUID; }
    @Override public Vec3 localOffset() { return new Vec3(localPos.getX() + 0.5, localPos.getY(), localPos.getZ() + 0.5); }
    @Override public boolean isValid() { return renderer != null; }
    @Override public int[] entityIds() { return renderer != null ? renderer.entityIds() : new int[0]; }
    @Override public List<AABB> interactionBounds() { return List.of(); }

    @Override
    public void tick(RenderContext ctx) {
        if (ctx.level() == null) return;

        // Sync fluid level from level state
        FluidState fs = ctx.level().getBlockState(localPos).getFluidState();
        int level = fs.isEmpty() ? 0 : fs.getAmount();
        if (level != lastFluidLevel) {
            lastFluidLevel = level;
            renderer = buildRenderer(level);
        }
        if (renderer != null) renderer.tick(ctx);

        // Lava damage
        if (isLava && level > 0 && ctx.realLevel() instanceof ServerLevel sl) {
            damageTimer++;
            if (damageTimer >= DAMAGE_INTERVAL) {
                damageTimer = 0;
                applyLavaDamage(ctx, sl, level);
            }
        } else if (!isLava) {
            damageTimer = 0;
        }
    }

    @Override
    public void render(RenderContext ctx) {
        if (renderer != null) renderer.render(ctx);
    }

    @Override
    public void despawn(List<Player> viewers) {
        if (renderer != null) renderer.despawn(viewers);
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {}

    private void applyLavaDamage(RenderContext ctx, ServerLevel sl, int level) {
        double scaleY = level / 8.0;
        Vec3 worldCenter = ContraptionMath.renderPosition(
                new Vec3(localPos.getX() + 0.5, localPos.getY() + scaleY * 0.5, localPos.getZ() + 0.5),
                ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        double r = 0.5 * ctx.scale();
        var box = new AABB(worldCenter.x - r, worldCenter.y - r * scaleY,
                worldCenter.z - r, worldCenter.x + r, worldCenter.y + r * scaleY, worldCenter.z + r);
        float dmg = (float)(ContraptionConfig.get().lavaDamage() * DAMAGE_INTERVAL / 20.0);
        try {
            for (var e : sl.getEntities((net.minecraft.world.entity.Entity) null, box, ent -> true)) {
                try { e.hurtServer(sl, sl.damageSources().lava(), dmg); }
                catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }

    private FluidRendererElement buildRenderer(int level) {
        String fluidTypeName = isLava ? ContraptionConfig.get().lavaFluidType()
                : ContraptionConfig.get().waterFluidType();
        if (fluidTypeName.isEmpty() || level <= 0) return null;
        // FluidRendererElement resolves "cml:fluidlvl_<fluidTypeId.value()>_<level>"
        // so we pass Key.of("cml", fluidTypeName) — .value() = fluidTypeName
        Key fluidTypeKey = Key.of("cml", fluidTypeName);
        float scaleY = level / 8.0f;
        return new FluidRendererElement(
                new Vec3(localPos.getX() + 0.5, localPos.getY(), localPos.getZ() + 0.5),
                fluidTypeKey, level,
                1.0f, scaleY, 1.0f);
    }

    /** True if this block should get a vanilla fluid overlay element. */
    public static boolean shouldRender(BlockState bs, boolean checkLava) {
        FluidState fs = bs.getFluidState();
        if (fs.isEmpty()) return false;
        boolean isLava = fs.getType() == net.minecraft.world.level.material.Fluids.LAVA
                || fs.getType() == net.minecraft.world.level.material.Fluids.FLOWING_LAVA;
        boolean isWater = fs.getType() == net.minecraft.world.level.material.Fluids.WATER
                || fs.getType() == net.minecraft.world.level.material.Fluids.FLOWING_WATER;
        if (checkLava) return isLava && ContraptionConfig.get().renderLava();
        return isWater && ContraptionConfig.get().renderWater();
    }
}
