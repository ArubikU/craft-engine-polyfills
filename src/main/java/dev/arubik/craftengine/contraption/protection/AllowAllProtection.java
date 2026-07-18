package dev.arubik.craftengine.contraption.protection;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import net.minecraft.core.BlockPos;

/**
 * The built-in no-op provider registered by default in {@link ContraptionProtectionRegistry}
 * (roadmap item #7, Phase C2 "SPI + registry + AllowAll default"). Every query returns
 * {@link ProtectionResult#PASS}, so with only this provider present the registry always falls
 * through to its allow default and contraption behavior is <b>identical to before the
 * protection system existed</b> — nothing regresses until a real land-claim adapter is
 * registered ahead of it. This is the deliberate "ship a self-contained default so the plugin
 * still works with zero config" piece of the design.
 */
public final class AllowAllProtection implements ContraptionProtection {

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
