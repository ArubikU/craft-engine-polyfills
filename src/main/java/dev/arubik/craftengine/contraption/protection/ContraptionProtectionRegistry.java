/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.bukkit.World
 *  org.bukkit.util.BoundingBox
 */
package dev.arubik.craftengine.contraption.protection;

import dev.arubik.craftengine.contraption.protection.AllowAllProtection;
import dev.arubik.craftengine.contraption.protection.ContraptionProtection;
import dev.arubik.craftengine.contraption.protection.ProtectionResult;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public final class ContraptionProtectionRegistry {
    private static final CopyOnWriteArrayList<ContraptionProtection> PROVIDERS = new CopyOnWriteArrayList();

    private ContraptionProtectionRegistry() {
    }

    public static void register(ContraptionProtection provider) {
        if (provider != null) {
            PROVIDERS.add(provider);
        }
    }

    public static void unregister(ContraptionProtection provider) {
        PROVIDERS.remove(provider);
    }

    public static List<ContraptionProtection> providers() {
        return List.copyOf(PROVIDERS);
    }

    public static boolean canBuild(UUID actor, World world, BlockPos pos) {
        return ContraptionProtectionRegistry.resolve(p -> p.canBuild(actor, world, pos));
    }

    public static boolean canBreak(UUID actor, World world, BlockPos pos) {
        return ContraptionProtectionRegistry.resolve(p -> p.canBreak(actor, world, pos));
    }

    public static boolean canInteract(UUID actor, World world, BlockPos pos) {
        return ContraptionProtectionRegistry.resolve(p -> p.canInteract(actor, world, pos));
    }

    public static boolean canUseRegion(UUID actor, World world, BoundingBox box) {
        return ContraptionProtectionRegistry.resolve(p -> p.canUseRegion(actor, world, box));
    }

    private static boolean resolve(Function<ContraptionProtection, ProtectionResult> query) {
        for (ContraptionProtection provider : PROVIDERS) {
            ProtectionResult result;
            try {
                result = query.apply(provider);
            }
            catch (Throwable t) {
                result = ProtectionResult.PASS;
            }
            if (result != ProtectionResult.DENY) continue;
            return false;
        }
        return true;
    }

    static {
        PROVIDERS.add(new AllowAllProtection());
    }
}

