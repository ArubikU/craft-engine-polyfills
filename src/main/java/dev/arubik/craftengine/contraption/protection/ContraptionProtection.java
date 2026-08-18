/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.bukkit.World
 *  org.bukkit.util.BoundingBox
 */
package dev.arubik.craftengine.contraption.protection;

import dev.arubik.craftengine.contraption.protection.ProtectionResult;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public interface ContraptionProtection {
    public ProtectionResult canBuild(UUID var1, World var2, BlockPos var3);

    public ProtectionResult canBreak(UUID var1, World var2, BlockPos var3);

    public ProtectionResult canInteract(UUID var1, World var2, BlockPos var3);

    default public ProtectionResult canUseRegion(UUID actor, World world, BoundingBox box) {
        return ProtectionResult.PASS;
    }

    default public String name() {
        return this.getClass().getSimpleName();
    }
}

