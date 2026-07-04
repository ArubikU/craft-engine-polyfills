package dev.arubik.craftengine.block.behavior;

import java.util.EnumSet;

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
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
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

    public MagnetBlockBehavior(BlockDefinition customBlock, EnumSet<MagnetFlag> flags, double radius, double strength) {
        super(customBlock);
        this.flags = flags;
        this.radius = radius;
        this.strength = strength;
    }

    @Override
    public void randomTick(Object thisBlock, Object[] args) {
        World level = (World) args[1];
        BlockPos blockPos = (BlockPos) args[2];

        BukkitExistingBlock blockInWorld = (BukkitExistingBlock) level.getBlock(blockPos.x(), blockPos.y(),
                blockPos.z());
        if (blockInWorld.block() == null)
            return;

        org.bukkit.Location blockLoc = blockInWorld.block().getLocation();
        Vector center = blockLoc.clone().add(0.5, 0.5, 0.5).toVector();

        // Match Bukkit getNearbyEntities(loc, radius, radius, radius): box of
        // half-extents radius centered on the block location.
        double bx = blockLoc.getX();
        double by = blockLoc.getY();
        double bz = blockLoc.getZ();
        net.minecraft.server.level.ServerLevel serverLevel = ((org.bukkit.craftbukkit.CraftWorld) blockLoc.getWorld())
                .getHandle();
        net.minecraft.world.phys.AABB aabb = new net.minecraft.world.phys.AABB(
                bx - radius, by - radius, bz - radius,
                bx + radius, by + radius, bz + radius);

        // The pull vector below is `center(THIS block's position) - entity.getLocation()`. Inside a
        // contraption ContraptionLevel#getEntitiesOfClass returns the dual-world union (fake + real), but
        // `center` here is the magnet's FAKE-LEVEL local position while a real-world entity's location is
        // real-world — subtracting the two mixes coordinate spaces and yields a garbage pull direction. A
        // magnet correctly attracting real entities would need transform-aware math it doesn't have, so
        // restrict a captured magnet to the FAKE level (getLocalEntities); it still attracts co-captured
        // items/entities normally, just not real-world ones.
        java.util.List<net.minecraft.world.entity.Entity> targets =
                (serverLevel instanceof dev.arubik.craftengine.contraption.level.ContraptionLevel cl)
                        ? cl.getLocalEntities(net.minecraft.world.entity.Entity.class, aabb, e -> !e.isRemoved())
                        : serverLevel.getEntitiesOfClass(net.minecraft.world.entity.Entity.class, aabb,
                                e -> !e.isRemoved());
        for (net.minecraft.world.entity.Entity nms : targets) {
            Entity entity = nms.getBukkitEntity();
            if (!shouldAffect(entity))
                continue;

            Vector dir = center.clone().subtract(entity.getLocation().toVector());
            double distance = dir.length();
            if (distance < 0.1)
                continue;

            dir.normalize().multiply(strength / distance);

            // Si tiene REPEL -> invierte la dirección
            if (flags.contains(MagnetFlag.REPEL)) {
                dir.multiply(-1);
            }

            entity.setVelocity(entity.getVelocity().add(dir));
        }
    }

    private boolean shouldAffect(Entity entity) {
        if (flags.contains(MagnetFlag.ALL))
            return true;
        if (entity instanceof Item && flags.contains(MagnetFlag.ITEMS))
            return true;
        if (entity instanceof Player && flags.contains(MagnetFlag.PLAYERS))
            return true;
        if (entity instanceof LivingEntity && !(entity instanceof Player) && flags.contains(MagnetFlag.MOBS))
            return true;
        if (entity instanceof Projectile && flags.contains(MagnetFlag.PROJECTILES))
            return true;
        if (entity instanceof ExperienceOrb && flags.contains(MagnetFlag.EXPERIENCE))
            return true;
        return false;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            double radius = Double.parseDouble(arguments.getOrDefault("radius", 5.0).toString());
            double strength = Double.parseDouble(arguments.getOrDefault("strength", 0.2).toString());

            String flagsStr = arguments.getOrDefault("flags", "ITEMS").toString();
            EnumSet<MagnetFlag> flags = EnumSet.noneOf(MagnetFlag.class);
            for (String flag : flagsStr.split(",")) {
                try {
                    flags.add(MagnetFlag.valueOf(flag.trim().toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                }
            }

            return new MagnetBlockBehavior(block, flags, radius, strength);
        }
    }
}
