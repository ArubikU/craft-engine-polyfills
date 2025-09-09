package dev.arubik.craftengine.block.behavior;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitExistingBlock;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.World;

public class MagnetBlockBehavior extends BukkitBlockBehavior {

    public static final Factory FACTORY = new Factory();

    public enum MagnetFlag {
        ITEMS,
        PLAYERS,
        MOBS,
        PROJECTILES,
        EXPERIENCE,
        ALL,
        REPEL
    }

    private final EnumSet<MagnetFlag> flags;
    private final double radius;
    private final double strength;

    public MagnetBlockBehavior(CustomBlock customBlock, EnumSet<MagnetFlag> flags, double radius, double strength) {
        super(customBlock);
        this.flags = flags;
        this.radius = radius;
        this.strength = strength;
    }

    @Override
    public void randomTick(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        World level = (World) args[1];
        BlockPos blockPos = (BlockPos) args[2];

        BukkitExistingBlock blockInWorld = (BukkitExistingBlock) level.getBlockAt(LocationUtils.fromBlockPos(blockPos));
        if (blockInWorld.block() == null) return;

        Vector center = blockInWorld.block().getLocation().add(0.5, 0.5, 0.5).toVector();

        for (Entity entity : blockInWorld.block().getWorld().getNearbyEntities(blockInWorld.block().getLocation(), radius, radius, radius)) {
            if (!shouldAffect(entity)) continue;

            Vector dir = center.clone().subtract(entity.getLocation().toVector());
            double distance = dir.length();
            if (distance < 0.1) continue;

            dir.normalize().multiply(strength / distance);

            // Si tiene REPEL -> invierte la dirección
            if (flags.contains(MagnetFlag.REPEL)) {
                dir.multiply(-1);
            }

            entity.setVelocity(entity.getVelocity().add(dir));
        }
    }

    private boolean shouldAffect(Entity entity) {
        if (flags.contains(MagnetFlag.ALL)) return true;
        if (entity instanceof Item && flags.contains(MagnetFlag.ITEMS)) return true;
        if (entity instanceof Player && flags.contains(MagnetFlag.PLAYERS)) return true;
        if (entity instanceof LivingEntity && !(entity instanceof Player) && flags.contains(MagnetFlag.MOBS)) return true;
        if (entity instanceof Projectile && flags.contains(MagnetFlag.PROJECTILES)) return true;
        if (entity instanceof ExperienceOrb && flags.contains(MagnetFlag.EXPERIENCE)) return true;
        return false;
    }

    public static class Factory implements BlockBehaviorFactory {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            double radius = Double.parseDouble(arguments.getOrDefault("radius", 5.0).toString());
            double strength = Double.parseDouble(arguments.getOrDefault("strength", 0.2).toString());

            String flagsStr = arguments.getOrDefault("flags", "ITEMS").toString();
            EnumSet<MagnetFlag> flags = EnumSet.noneOf(MagnetFlag.class);
            for (String flag : flagsStr.split(",")) {
                try {
                    flags.add(MagnetFlag.valueOf(flag.trim().toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }

            return new MagnetBlockBehavior(block, flags, radius, strength);
        }
    }
}
