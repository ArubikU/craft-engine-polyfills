/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.entity.player.Player
 *  org.joml.Quaternionf
 */
package dev.arubik.craftengine.contraption.level;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import org.joml.Quaternionf;

public interface ContraptionBoundary {
    public Level realLevel();

    public Vec3 realWorldPositionOf(Vec3 var1);

    public Vec3 realWorldPositionOf(BlockPos var1);

    public Vec3 rotateToRealWorld(Vec3 var1);

    public Quaternionf realOrientationOf(Quaternionf var1);

    public double realYawRadians();

    public double realScaleFactor();

    public List<Player> realViewers(BlockPos var1);

    public List<Player> realViewers(Vec3 var1);

    public boolean isRealWorldEntity(Entity var1);

    public <T extends Entity> List<T> getLocalEntities(Class<T> var1, AABB var2, Predicate<? super T> var3);

    public static Optional<ContraptionBoundary> of(Level level) {
        Optional<ContraptionBoundary> optional;
        if (level instanceof ContraptionBoundary) {
            ContraptionBoundary boundary = (ContraptionBoundary)level;
            optional = Optional.of(boundary);
        } else {
            optional = Optional.empty();
        }
        return optional;
    }
}

