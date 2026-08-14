package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.config.ContraptionConfig;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.FluidRendererElement;
import dev.arubik.craftengine.contraption.element.RenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.List;

/**
 * Renders vanilla water/lava inside a contraption using CE fluid item models (FluidRendererElement).
 *
 * Level mapping (vanilla amount 1-8 → CE item level 1-16):
 *   falling water  → CE 16 (full column)
 *   other          → round(amount × 1.8) capped at 14 (source = 14, flowing 7→13 … 1→2)
 *
 * Entity Y = block_bottom + scaleY/2 so rotation keeps the column bottom-aligned.
 */
public final class ContraptionVanillaFluidElement implements ContraptionElement {

    private final BlockPos localPos;
    private final boolean isLava;
    private final boolean isWaterlogged;
    private FluidRendererElement renderer;
    private int lastVanillaLevel = -1;
    private boolean lastFalling = false;
    private int damageTimer = 0;
    private static final int DAMAGE_INTERVAL = 20;

    public ContraptionVanillaFluidElement(BlockPos localPos, boolean isLava, boolean isWaterlogged, FluidState initialFs) {
        this.localPos = localPos;
        this.isLava = isLava;
        this.isWaterlogged = isWaterlogged;
        int level = initialFs.isEmpty() ? 0 : initialFs.getAmount();
        boolean falling = isFalling(initialFs);
        this.renderer = buildRenderer(level, falling);
        this.lastVanillaLevel = level;
        this.lastFalling = falling;
    }

    @Override public Key type() { return ElementTypes.FLUID; }
    @Override public Vec3 localOffset() { return new Vec3(localPos.getX() + 0.5, localPos.getY() + 0.5, localPos.getZ() + 0.5); }
    @Override public boolean isValid() { return renderer != null; }
    @Override public int[] entityIds() { return renderer != null ? renderer.entityIds() : new int[0]; }
    @Override public List<AABB> interactionBounds() { return List.of(); }

    @Override
    public void tick(RenderContext ctx) {
        if (ctx.level() == null) return;
        FluidState fs = ctx.level().getBlockState(localPos).getFluidState();
        int level = fs.isEmpty() ? 0 : fs.getAmount();
        boolean falling = isFalling(fs);
        if (level != lastVanillaLevel || falling != lastFalling) {
            lastVanillaLevel = level;
            lastFalling = falling;
            FluidRendererElement old = renderer;
            renderer = buildRenderer(level, falling);
            if (old != null) old.despawn(ctx.viewers());
        }
        if (renderer != null) renderer.tick(ctx);

        if (isLava && level > 0 && ctx.realLevel() instanceof ServerLevel sl) {
            damageTimer++;
            if (damageTimer >= DAMAGE_INTERVAL) {
                damageTimer = 0;
                applyLavaDamage(ctx, sl, level);
            }
        } else {
            if (!isLava) damageTimer = 0;
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

    // ---- helpers ----

    private static boolean isFalling(FluidState fs) {
        if (fs.isEmpty() || fs.isSource()) return false;
        try {
            return fs.hasProperty(FlowingFluid.FALLING) && fs.getValue(FlowingFluid.FALLING);
        } catch (Throwable ignored) { return false; }
    }

    /** Maps vanilla fluid amount + falling flag to CE item level 1-16. */
    private static int toCeLevel(int vanillaAmount, boolean falling) {
        if (falling) return 16;
        return Math.max(1, Math.min(14, (int) Math.round(vanillaAmount * 1.8)));
    }

    private void applyLavaDamage(RenderContext ctx, ServerLevel sl, int vanillaLevel) {
        double fillH = vanillaLevel / 8.0;
        Vec3 worldCenter = ContraptionMath.renderPosition(
                new Vec3(localPos.getX() + 0.5, localPos.getY() + fillH * 0.5, localPos.getZ() + 0.5),
                ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        double r = 0.5 * ctx.scale();
        var box = new AABB(worldCenter.x - r, worldCenter.y - r * fillH,
                worldCenter.z - r, worldCenter.x + r, worldCenter.y + r * fillH, worldCenter.z + r);
        float dmg = (float)(ContraptionConfig.get().lavaDamage() * DAMAGE_INTERVAL / 20.0);
        try {
            for (var e : sl.getEntities((net.minecraft.world.entity.Entity) null, box, ent -> true)) {
                try { e.hurtServer(sl, sl.damageSources().lava(), dmg); } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }

    private FluidRendererElement buildRenderer(int vanillaLevel, boolean falling) {
        if (vanillaLevel <= 0) return null;
        ContraptionConfig cfg = ContraptionConfig.get();
        if (!cfg.fluidRenderEnabled()) return null;
        if (!(isLava ? cfg.renderLava() : cfg.renderWater())) return null;
        if (isWaterlogged && !cfg.renderWaterlogged()) return null;
        if (!isWaterlogged) {
            boolean isSource = vanillaLevel == 8 && !falling;
            if (isSource && !cfg.renderSourceFluid()) return null;
            if (!isSource && !cfg.renderFlowingFluid()) return null;
        }

        int ceLevel = toCeLevel(vanillaLevel, falling);
        String itemId = isLava ? cfg.lavaItemId(ceLevel) : cfg.waterItemId(ceLevel);
        if (itemId.isEmpty()) return null;

        // FluidRendererElement looks up: Key.of(ns, "fluidlvl_" + typeId.value() + "_" + ceLevel)
        // Extract type value by stripping "fluidlvl_" prefix and "_<ceLevel>" suffix from itemId.
        Key resolvedKey = Key.of(itemId);
        String value = resolvedKey.value();
        String levelSuffix = "_" + ceLevel;
        String typeValue = value.endsWith(levelSuffix) ? value.substring(0, value.length() - levelSuffix.length()) : value;
        String fluidPrefix = "fluidlvl_";
        String finalTypeValue = typeValue.startsWith(fluidPrefix) ? typeValue.substring(fluidPrefix.length()) : typeValue;
        Key fluidTypeKey = Key.of(resolvedKey.namespace(), finalTypeValue);

        float scaleY = ceLevel / 16.0f;
        float centerY = scaleY / 2.0f;
        // 0.9998 on XZ prevents z-fighting against adjacent block faces
        float scaleXZ = 0.9998f;

        return new FluidRendererElement(
                new Vec3(localPos.getX() + 0.5, localPos.getY() + centerY, localPos.getZ() + 0.5),
                fluidTypeKey, ceLevel, scaleXZ, scaleY, scaleXZ);
    }
}
