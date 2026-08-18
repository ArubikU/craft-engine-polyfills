/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Input
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption.player;

import java.util.UUID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class PlayerCarry {
    private static final double CARRY_Y_MIN = 0.02;

    private PlayerCarry() {
    }

    public static void carry(ServerPlayer player, double dx, double dy, double dz) {
        boolean applyY;
        boolean jumping;
        player.connection.resetFlyingTicks();
        if (dx == 0.0 && dy == 0.0 && dz == 0.0) {
            return;
        }
        Input input = player.getLastClientInput();
        boolean bl = jumping = input != null && input.jump();
        if (jumping) {
            return;
        }
        boolean hasDirectionalInput = input != null && (input.forward() || input.backward() || input.left() || input.right());
        boolean bl2 = applyY = Math.abs(dy) > 0.02;
        if (hasDirectionalInput && !applyY) {
            return;
        }
        Vec3 current = player.getDeltaMovement();
        double outX = hasDirectionalInput ? current.x : dx;
        double outZ = hasDirectionalInput ? current.z : dz;
        Vec3 combined = new Vec3(outX, applyY ? dy : current.y, outZ);
        player.setDeltaMovement(combined);
        player.connection.send((Packet)new ClientboundSetEntityMotionPacket((Entity)player));
    }

    public static void release(UUID playerId) {
        Player bukkit = Bukkit.getPlayer((UUID)playerId);
        if (bukkit == null) {
            return;
        }
        ServerPlayer player = ((CraftPlayer)bukkit).getHandle();
        player.setDeltaMovement(Vec3.ZERO);
        player.connection.send((Packet)new ClientboundSetEntityMotionPacket((Entity)player));
    }
}

