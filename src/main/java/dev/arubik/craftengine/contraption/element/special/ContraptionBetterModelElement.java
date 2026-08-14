package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.machine.render.BetterModelMachineRenderer;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationIterator;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.tracker.DummyTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.*;

public final class ContraptionBetterModelElement implements ContraptionElement {

    private final BlockPos localPos;
    private final BetterModelMachineRenderer source;
    private DummyTracker mirrorTracker;
    private String mirrorAnim;
    private Set<UUID> shownToViewers = new HashSet<>();

    public ContraptionBetterModelElement(BlockPos localPos, BetterModelMachineRenderer source) {
        this.localPos = localPos;
        this.source = source;
    }

    public BlockPos localPos() {
        return localPos;
    }

    @Override
    public Key type() {
        return ElementTypes.BETTER_MODEL;
    }

    @Override
    public Vec3 localOffset() {
        return new Vec3(localPos.getX() + 0.5, localPos.getY(), localPos.getZ() + 0.5);
    }

    @Override
    public boolean isValid() {
        return source != null;
    }

    @Override
    public int[] entityIds() {
        return new int[0];
    }

    @Override
    public List<AABB> interactionBounds() {
        return List.of();
    }

    @Override
    public void tick(RenderContext ctx) {
    }

    @Override
    public void render(RenderContext ctx) {
        if (!BetterModelMachineRenderer.available()) {
            closeTracker();
            return;
        }
        if (!source.isShown()) {
            closeTracker();
            return;
        }

        Vec3 localCenter = new Vec3(localPos.getX() + 0.5, localPos.getY(), localPos.getZ() + 0.5);
        Vec3 realPos = ctx.level().realWorldPositionOf(localCenter);
        float yawDeg = (float) Math.toDegrees(ctx.yawRadians());
        org.bukkit.Location loc = new org.bukkit.Location(
                ctx.realLevel().getWorld(),
                realPos.x, realPos.y, realPos.z, yawDeg, 0f);

        try {
            if (mirrorTracker == null || mirrorTracker.isClosed()) {
                mirrorAnim = null;
                mirrorTracker = BetterModel.model(source.modelId())
                        .map(r -> r.create(BukkitAdapter.adapt(loc)))
                        .orElse(null);
            } else {
                mirrorTracker.location(BukkitAdapter.adapt(loc));
            }
        } catch (Throwable ignored) {
            mirrorTracker = null;
            return;
        }
        if (mirrorTracker == null) {
            return;
        }

        String wantAnim = source.currentAnimation();
        if (wantAnim != null && !wantAnim.equals(mirrorAnim)) {
            try {
                mirrorTracker.animate(wantAnim,
                        new AnimationModifier(0, 0, AnimationIterator.Type.LOOP, () -> 1f));
                mirrorAnim = wantAnim;
            } catch (Throwable ignored) {
            }
        } else if (wantAnim == null && mirrorAnim != null) {
            try {
                mirrorTracker.stopAnimation(mirrorAnim);
            } catch (Throwable ignored) {
            }
            mirrorAnim = null;
        }

        Set<UUID> currentViewerIds = new HashSet<>();
        for (Player p : ctx.viewers()) {
            Object pp = p.platformPlayer();
            if (!(pp instanceof org.bukkit.entity.Player b)) {
                continue;
            }
            UUID id = b.getUniqueId();
            currentViewerIds.add(id);
            try {
                var bp = BukkitAdapter.adapt(b);
                if (!mirrorTracker.isSpawned(bp)) {
                    mirrorTracker.spawn(bp);
                }
            } catch (Throwable ignored) {
            }
        }

        for (UUID gone : shownToViewers) {
            if (!currentViewerIds.contains(gone)) {
                org.bukkit.entity.Player bp = org.bukkit.Bukkit.getPlayer(gone);
                if (bp != null && mirrorTracker != null) {
                    try {
                        mirrorTracker.remove(BukkitAdapter.adapt(bp));
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        shownToViewers = currentViewerIds;
    }

    @Override
    public void despawn(List<Player> viewers) {
        if (mirrorTracker == null) {
            return;
        }
        for (Player p : viewers) {
            Object pp = p.platformPlayer();
            if (pp instanceof org.bukkit.entity.Player b) {
                try {
                    mirrorTracker.remove(BukkitAdapter.adapt(b));
                } catch (Throwable ignored) {
                }
            }
        }
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        closeTracker();
    }

    private void closeTracker() {
        if (mirrorTracker == null) {
            return;
        }
        try {
            mirrorTracker.close();
        } catch (Throwable ignored) {
        }
        mirrorTracker = null;
        mirrorAnim = null;
    }
}
