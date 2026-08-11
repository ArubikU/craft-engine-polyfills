package dev.arubik.craftengine.contraption.listener;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;

/**
 * Ejects entities that fall into a contraption's VOID back into the real world (user: "haz que al caer
 * cualquier item o entidad como falling al vacío del contraption, dropeen los items fuera del contraption en
 * la última posición que se pueda").
 *
 * <p>A contraption lives in a hidden {@link ContraptionLevel}; an item dropped inside it, or a block that
 * comes loose and falls (a {@link FallingBlockEntity}), tumbles under gravity and — with nothing below the
 * captured structure — falls forever inside that hidden level, lost to the real world. Each tick this catches
 * anything that has fallen a little below the LOWEST captured cell and re-emits it in the real world at the
 * point it visually occupied when it crossed out (its hidden position run through the contraption transform),
 * which is the last position it could sensibly be dropped at.
 */
public final class ContraptionVoidDrop {

    private ContraptionVoidDrop() {
    }

    /** How far (blocks) below the lowest captured cell an entity must fall before it counts as "in the void". */
    private static final double VOID_MARGIN = 3.0;

    /** Scans {@code level} for entities in the void and drops their items into {@code realLevel}. Main thread only. */
    public static void handle(ContraptionLevel level, ServerLevel realLevel) {
        if (level == null || realLevel == null) {
            return;
        }
        double minCellY = Double.MAX_VALUE;
        for (BlockPos p : level.localPositions()) {
            if (p.getY() < minCellY) {
                minCellY = p.getY();
            }
        }
        if (minCellY == Double.MAX_VALUE) {
            return; // no cells — nothing to reference a floor from
        }
        double floor = minCellY - VOID_MARGIN;
        // Snapshot first — eject() discards entities, so we can't mutate while iterating the live view.
        List<Entity> all = new ArrayList<>();
        for (Entity e : level.getAllEntities()) {
            all.add(e);
        }
        for (Entity e : all) {
            if (e == null || e.isRemoved() || e instanceof ServerPlayer) {
                continue; // never eject a player
            }
            if (e.position().y >= floor) {
                continue;
            }
            Vec3 at = level.realWorldPositionOf(e.position());
            eject(e, realLevel, at);
            e.discard();
        }
    }

    private static void eject(Entity e, ServerLevel realLevel, Vec3 at) {
        try {
            if (e instanceof ItemEntity item) {
                spawnItem(realLevel, at, item.getItem().copy());
            } else if (e instanceof FallingBlockEntity falling) {
                spawnItem(realLevel, at, new ItemStack(falling.getBlockState().getBlock()));
            }
            // Other entity kinds (a stray mob that walked off): removed from the doomed hidden level without a
            // drop — nothing sensible to "drop" for them, and cross-level teleport is out of scope here.
        } catch (Throwable ignored) {
            // an odd stack / removed entity mid-iteration must not break the sweep for the rest
        }
    }

    private static void spawnItem(ServerLevel realLevel, Vec3 at, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        ItemEntity drop = new ItemEntity(realLevel, at.x, at.y, at.z, stack);
        drop.setDeltaMovement(Vec3.ZERO);
        realLevel.addFreshEntity(drop);
    }
}
