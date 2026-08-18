/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.RenderContext;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

public interface ContraptionElement {
    public Key type();

    public Vec3 localOffset();

    public boolean isValid();

    public int[] entityIds();

    default public List<AABB> interactionBounds() {
        return List.of();
    }

    default public boolean onInteract(ServerPlayer player, ContraptionState state, Vec3 hitPos, InteractionHand hand, boolean rightClick) {
        return false;
    }

    default public void tick(RenderContext ctx) {
    }

    public void render(RenderContext var1);

    public void despawn(List<Player> var1);

    public void disassemble(ServerLevel var1, BlockPos var2, int var3);

    default public boolean isPersistent() {
        return false;
    }

    default public CompoundTag toNbt() {
        return null;
    }
}

