/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.PositionMoveRotation
 *  net.minecraft.world.entity.Relative
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.attribute.Attribute
 *  org.bukkit.attribute.AttributeInstance
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption.furniture;

import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public final class ContraptionSeatMount {
    private static final double SEAT_HEIGHT = 0.6;
    private static final double ARMOR_STAND_SPAWN_CORRECTION = 0.9875;

    private ContraptionSeatMount() {
    }

    public static ArmorStand mount(Player player, World world, Vec3 realPos, float yawDegrees) {
        double spawnY = realPos.y + 0.6 - 0.9875;
        Location loc = new Location(world, realPos.x, spawnY, realPos.z, yawDegrees, 0.0f);
        ArmorStand stand = (ArmorStand)world.spawnEntity(loc, EntityType.ARMOR_STAND);
        AttributeInstance health = stand.getAttribute(Attribute.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(0.01);
        }
        stand.setSmall(true);
        stand.setInvisible(true);
        stand.setSilent(true);
        stand.setInvulnerable(true);
        stand.setArms(false);
        stand.setCanTick(false);
        stand.setAI(false);
        stand.setGravity(false);
        stand.setPersistent(false);
        if (!stand.addPassenger((Entity)player)) {
            stand.remove();
            return null;
        }
        return stand;
    }

    public static void reposition(Entity mount, Vec3 realPos, float yawDegrees) {
        Location loc = mount.getLocation();
        loc.setX(realPos.x);
        loc.setY(realPos.y + 0.6 - 0.9875);
        loc.setZ(realPos.z);
        loc.setYaw(yawDegrees);
        mount.teleport(loc);
    }

    public static void rotateRiderView(Player player, float deltaYawDegrees) {
        if (deltaYawDegrees == 0.0f) {
            return;
        }
        try {
            ServerPlayer sp = ((CraftPlayer)player).getHandle();
            sp.connection.teleport(new PositionMoveRotation(Vec3.ZERO, Vec3.ZERO, deltaYawDegrees, 0.0f), EnumSet.allOf(Relative.class));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static void applyContraptionScale(Player player, double scale) {
        try {
            ServerPlayer sp = ((CraftPlayer)player).getHandle();
            net.minecraft.world.entity.ai.attributes.AttributeInstance inst = sp.getAttribute(Attributes.SCALE);
            if (inst == null || inst.getBaseValue() == scale) {
                return;
            }
            inst.setBaseValue(scale);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static Entity resolve(UUID mountEntityId) {
        if (mountEntityId == null) {
            return null;
        }
        return Bukkit.getEntity((UUID)mountEntityId);
    }

    public static void unmount(UUID mountEntityId) {
        Entity mount = ContraptionSeatMount.resolve(mountEntityId);
        if (mount == null) {
            return;
        }
        mount.eject();
        mount.remove();
    }
}

