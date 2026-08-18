/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$Palette
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.block.data.CraftBlockData
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.ContraptionPerf;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class DebugPhysSpawn {
    private static final double SPAWN_DISTANCE = 3.0;
    private static final double SPAWN_LIFT = 1.0;

    private DebugPhysSpawn() {
    }

    public static Vec3 spawnAnchor(Vec3 eye, Vec3 look) {
        Vec3 flat = new Vec3(look.x, 0.0, look.z);
        flat = flat.lengthSqr() < 1.0E-6 ? new Vec3(0.0, 0.0, 1.0) : flat.normalize();
        return eye.add(flat.scale(3.0)).add(0.0, 1.0, 0.0);
    }

    public static BlockPos centerOffset(Vec3i size) {
        return new BlockPos(-(size.getX() - 1) / 2, 0, -(size.getZ() - 1) / 2);
    }

    public static BlockState parseBlockState(String input) {
        return ((CraftBlockData)Bukkit.createBlockData((String)input)).getState();
    }

    public static Optional<Map<BlockPos, StructureTemplate.StructureBlockInfo>> structureCells(ServerLevel level, String id) {
        Identifier key = Identifier.tryParse((String)id);
        if (key == null) {
            return Optional.empty();
        }
        Optional template = level.getStructureManager().get(key);
        if (template.isEmpty() || ((StructureTemplate)template.get()).palettes.isEmpty()) {
            return Optional.empty();
        }
        BlockPos offset = DebugPhysSpawn.centerOffset(((StructureTemplate)template.get()).getSize());
        HashMap<BlockPos, StructureTemplate.StructureBlockInfo> cells = new HashMap<BlockPos, StructureTemplate.StructureBlockInfo>();
        for (StructureTemplate.StructureBlockInfo info : ((StructureTemplate.Palette)((StructureTemplate)template.get()).palettes.get(0)).blocks()) {
            if (info.state().isAir() || info.state().is(Blocks.STRUCTURE_VOID)) continue;
            cells.put(info.pos().offset((Vec3i)offset), info);
        }
        return Optional.of(cells);
    }

    public static ContraptionEntity spawn(World bukkitWorld, Vec3 anchor, Map<BlockPos, BlockState> cells, Map<BlockPos, CompoundTag> blockEntities) {
        return DebugPhysSpawn.spawn(bukkitWorld, anchor, cells, blockEntities, Key.of((String)"polyfills", (String)"phys"));
    }

    public static ContraptionEntity spawn(World bukkitWorld, Vec3 anchor, Map<BlockPos, BlockState> cells, Map<BlockPos, CompoundTag> blockEntities, Key type) {
        long __t2;
        boolean __prof = ContraptionPerf.enabled();
        long __t0 = __prof ? System.nanoTime() : 0L;
        ServerLevel realLevel = ((CraftWorld)bukkitWorld).getHandle();
        ContraptionLevel level = ContraptionLevel.create((Level)realLevel, anchor.x, anchor.y, anchor.z, 0.0);
        long __t1 = __prof ? System.nanoTime() : 0L;
        level.putBlocks(cells, true);
        long l = __t2 = __prof ? System.nanoTime() : 0L;
        if (__prof) {
            Bukkit.getLogger().info(String.format("[SpawnProfile] create=%.2fms putBlocks=%.2fms", (double)(__t1 - __t0) / 1000000.0, (double)(__t2 - __t1) / 1000000.0));
        }
        if (blockEntities != null) {
            for (Map.Entry<BlockPos, CompoundTag> be : blockEntities.entrySet()) {
                if (cells.getOrDefault(be.getKey(), Blocks.AIR.defaultBlockState()).isAir()) continue;
                level.putBlockEntity(be.getKey(), be.getValue());
            }
        }
        if (level.blockCount() == 0) {
            level.dispose();
            return null;
        }
        ContraptionState state = new ContraptionState(UUID.randomUUID(), (ResourceKey<Level>)realLevel.dimension(), level, anchor.x, anchor.y, anchor.z);
        ContraptionAssembler.attachDefaultBehavior((Level)realLevel, BlockPos.containing((Position)anchor), state, type, 5.0, 2.0);
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        PhysicsWorld.ensureBody(state);
        BearingHammerListener.markAssembled((ResourceKey<Level>)((CraftWorld)bukkitWorld).getHandle().dimension(), BlockPos.containing((Position)anchor), state.id());
        return entity;
    }
}

