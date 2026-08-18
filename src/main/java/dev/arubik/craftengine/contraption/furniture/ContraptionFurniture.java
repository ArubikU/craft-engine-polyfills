/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.contraption.furniture;

import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.util.Key;

public record ContraptionFurniture(Key definitionId, String variantName, Vec3 localOffset, float yawOffsetDegrees, BukkitFurniture liveFurniture) {
    public boolean hasLiveFurniture() {
        return this.liveFurniture != null && this.liveFurniture.isValid();
    }
}

