/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.bukkit.World
 *  org.bukkit.util.BoundingBox
 */
package dev.arubik.craftengine.contraption.protection;

import dev.arubik.craftengine.contraption.protection.ContraptionProtection;
import dev.arubik.craftengine.contraption.protection.ProtectionResult;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public final class AllowAllProtection
implements ContraptionProtection {
    @Override
    public ProtectionResult canBuild(UUID actor, World world, BlockPos pos) {
        return ProtectionResult.PASS;
    }

    @Override
    public ProtectionResult canBreak(UUID actor, World world, BlockPos pos) {
        return ProtectionResult.PASS;
    }

    @Override
    public ProtectionResult canInteract(UUID actor, World world, BlockPos pos) {
        return ProtectionResult.PASS;
    }

    @Override
    public ProtectionResult canUseRegion(UUID actor, World world, BoundingBox box) {
        return ProtectionResult.PASS;
    }

    @Override
    public String name() {
        return "AllowAll";
    }
}

