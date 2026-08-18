/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntitySpawnReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.animal.Animal
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.component.ItemAttributeModifiers
 *  net.minecraft.world.item.component.ItemAttributeModifiers$Entry
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.property.BooleanProperty
 *  net.momirealms.craftengine.core.block.property.IntegerProperty
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.util.RayTraceResult
 *  org.bukkit.util.Vector
 */
package dev.arubik.craftengine.machine.render.formula;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.behavior.ScriptStallBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import dev.arubik.craftengine.contraption.type.MachineContraptionType;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.machine.render.formula.BeltClass;
import dev.arubik.craftengine.machine.render.formula.BlockClass;
import dev.arubik.craftengine.machine.render.formula.ContraptionWorldClass;
import dev.arubik.craftengine.machine.render.formula.EntityClass;
import dev.arubik.craftengine.machine.render.formula.LocationClass;
import dev.arubik.craftengine.machine.render.formula.NbtClass;
import dev.arubik.craftengine.machine.render.formula.PipeClass;
import dev.arubik.craftengine.machine.render.formula.PlayerClass;
import dev.arubik.craftengine.machine.render.formula.PolyClassFactory;
import dev.arubik.craftengine.machine.render.formula.PolyClassFactory.Builder;
import dev.arubik.craftengine.machine.render.formula.PolyFunctionRegistry;
import dev.arubik.craftengine.machine.render.formula.PolyValue;
import dev.arubik.craftengine.machine.render.formula.VectorClass;
import dev.arubik.craftengine.machine.render.formula.WorldClass;
import dev.arubik.craftengine.rotation.DataMotorBlockEntity;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.BooleanProperty;
import net.momirealms.craftengine.core.block.property.IntegerProperty;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public final class MachineClass
implements PolyClass {
    private static final ConcurrentHashMap<UUID, ScriptStallBehavior> SCRIPT_STALLS = new ConcurrentHashMap();
    private static final Builder<MachineClass> BUILDER = PolyClassFactory.builder();
    
    
    private static final PolyClassFactory<MachineClass> FACTORY = BUILDER.property("x", mc -> {
        try {
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos rp = MachineClass.resolveReal(mc.nmsLevel(), mc.nmsPos(), rl)[0];
            return PolyValue.of((double)rp.getX() + 0.5);
        }
        catch (Throwable ignored) {
            return PolyValue.of(mc.x);
        }
    }).property("y", mc -> {
        try {
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos rp = MachineClass.resolveReal(mc.nmsLevel(), mc.nmsPos(), rl)[0];
            return PolyValue.of((double)rp.getY() + 0.5);
        }
        catch (Throwable ignored) {
            return PolyValue.of(mc.y);
        }
    }).property("z", mc -> {
        try {
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos rp = MachineClass.resolveReal(mc.nmsLevel(), mc.nmsPos(), rl)[0];
            return PolyValue.of((double)rp.getZ() + 0.5);
        }
        catch (Throwable ignored) {
            return PolyValue.of(mc.z);
        }
    }).property("pos", mc -> {
        try {
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos rp = MachineClass.resolveReal(mc.nmsLevel(), mc.nmsPos(), rl)[0];
            return new VectorClass((double)rp.getX() + 0.5, (double)rp.getY() + 0.5, (double)rp.getZ() + 0.5);
        }
        catch (Throwable ignored) {
            return new VectorClass(mc.x, mc.y, mc.z);
        }
    }).property("facing", mc -> PolyValue.of(mc.facing != null ? mc.facing : "north")).property("facing_dx", mc -> {
        int[] o = mc.facingOffset();
        return PolyValue.of(o[0]);
    }).property("facing_dy", mc -> {
        int[] o = mc.facingOffset();
        return PolyValue.of(o[1]);
    }).property("facing_dz", mc -> {
        int[] o = mc.facingOffset();
        return PolyValue.of(o[2]);
    }).property("facing_block", mc -> {
        try {
            int[] off = mc.facingOffset();
            ServerLevel sl = mc.nmsLevel();
            BlockPos local = mc.nmsPos().offset(off[0], off[1], off[2]);
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos realP = MachineClass.resolveReal(sl, local, rl)[0];
            return BlockClass.of(rl[0], realP);
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).property("facing_angle", mc -> PolyValue.of(mc.facingYaw)).property("self_block", mc -> {
        try {
            return BlockClass.of(mc.nmsLevel(), mc.nmsPos());
        }
        catch (Throwable i) {
            return PolyValue.NULL;
        }
    }).property("location", mc -> {
        if (mc.world == null) {
            return PolyValue.NULL;
        }
        ServerLevel sl = ((CraftWorld)mc.world).getHandle();
        return LocationClass.forLevel(sl, mc.x, mc.y, mc.z);
    }).property("world", mc -> {
        if (mc.world == null) {
            return PolyValue.NULL;
        }
        ServerLevel sl = ((CraftWorld)mc.world).getHandle();
        return WorldClass.forLevel(sl);
    }).property("owner_uuid", mc -> {
        PersistentBlockEntity patt0$temp = mc.blockEntity;
        if (patt0$temp instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity mbe = (AbstractMachineBlockEntity)patt0$temp;
            UUID uuid = mbe.getOwnerUuid();
            return uuid != null ? PolyValue.of(uuid.toString()) : PolyValue.NULL;
        }
        return PolyValue.NULL;
    }).property("has_nearby_player", mc -> PolyValue.of(mc.anyPlayerInRange(8.0))).property("nearest_player_yaw", mc -> PolyValue.of(mc.computePlayerYaw(Double.MAX_VALUE))).property("nearest_player_pitch", mc -> PolyValue.of(mc.computePlayerPitch(Double.MAX_VALUE))).property("nearest_player_distance", mc -> {
        org.bukkit.entity.Player np = mc.nearestPlayer(Double.MAX_VALUE);
        return np != null ? PolyValue.of(np.getLocation().distance(new Location(mc.world, mc.x, mc.y, mc.z))) : PolyValue.of(999999.0);
    }).property("player_yaw", mc -> PolyValue.of(mc.computePlayerYaw(Double.MAX_VALUE))).property("player_pitch", mc -> PolyValue.of(mc.computePlayerPitch(Double.MAX_VALUE))).property("facing_dx", mc -> PolyValue.of(switch (mc.facing != null ? mc.facing : "north") {
        case "east" -> 1.0;
        case "west" -> -1.0;
        default -> 0.0;
    })).property("facing_dy", mc -> PolyValue.of(switch (mc.facing != null ? mc.facing : "north") {
        case "up" -> 1.0;
        case "down" -> -1.0;
        default -> 0.0;
    })).property("facing_dz", mc -> PolyValue.of(switch (mc.facing != null ? mc.facing : "north") {
        case "south" -> 1.0;
        case "north" -> -1.0;
        default -> 0.0;
    })).method("player_facing", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(false);
        }
        String target = ((PolyValue)args.get(0)).asStr().toLowerCase();
        String zone = args.size() > 1 ? ((PolyValue)args.get(1)).asStr() : null;
        int slash = target.indexOf(47);
        if (slash >= 0 && zone == null) {
            zone = target.substring(slash + 1);
            target = target.substring(0, slash);
        }
        return PolyValue.of(mc.anyPlayerFacing(target, zone));
    }).method("player_in_range", (mc, args) -> {
        double dist = args.isEmpty() ? 8.0 : ((PolyValue)args.get(0)).asNum();
        return PolyValue.of(mc.anyPlayerInRange(dist));
    }).method("player_above", (mc, args) -> PolyValue.of(mc.anyPlayerInCone(0.0, 1.0, 0.0, 45.0))).method("player_below", (mc, args) -> PolyValue.of(mc.anyPlayerInCone(0.0, -1.0, 0.0, 45.0))).method("nearest_player_yaw", (mc, args) -> {
        double r = args.isEmpty() ? Double.MAX_VALUE : ((PolyValue)args.get(0)).asNum();
        return PolyValue.of(mc.computePlayerYaw(r));
    }).method("nearest_player_pitch", (mc, args) -> {
        double r = args.isEmpty() ? Double.MAX_VALUE : ((PolyValue)args.get(0)).asNum();
        return PolyValue.of(mc.computePlayerPitch(r));
    }).method("player_yaw", (mc, args) -> {
        double r = args.isEmpty() ? Double.MAX_VALUE : ((PolyValue)args.get(0)).asNum();
        return PolyValue.of(mc.computePlayerYaw(r));
    }).method("player_pitch", (mc, args) -> {
        double r = args.isEmpty() ? Double.MAX_VALUE : ((PolyValue)args.get(0)).asNum();
        return PolyValue.of(mc.computePlayerPitch(r));
    }).method("has_nearby_player", (mc, args) -> {
        double dist = args.isEmpty() ? 8.0 : ((PolyValue)args.get(0)).asNum();
        return PolyValue.of(mc.anyPlayerInRange(dist));
    }).method("nearest_player", (mc, args) -> {
        double r = args.isEmpty() ? Double.MAX_VALUE : ((PolyValue)args.get(0)).asNum();
        org.bukkit.entity.Player np = mc.nearestPlayer(r);
        if (np == null) {
            return PolyValue.NULL;
        }
        ServerPlayer sp = ((CraftPlayer)np).getHandle();
        return new PlayerClass(sp);
    }).method("nearby_players", (mc, args) -> {
        double dist = args.isEmpty() ? 8.0 : ((PolyValue)args.get(0)).asNum();
        double distSq = dist * dist;
        Location center = new Location(mc.world, mc.x, mc.y, mc.z);
        ArrayList<PolyValue> players = new ArrayList<PolyValue>();
        for (org.bukkit.entity.Player player : mc.queryPlayers()) {
            if (!(player.getLocation().distanceSquared(center) <= distSq)) continue;
            ServerPlayer sp = ((CraftPlayer)player).getHandle();
            players.add(new PlayerClass(sp));
        }
        return new PolyValue.Array(players);
    }).method("consume_gas", (mc, args) -> {
        if (args.size() < 2 || mc.blockEntity == null) {
            return PolyValue.of(0.0);
        }
        String tankName = ((PolyValue)args.get(0)).asStr();
        int amount = (int)((PolyValue)args.get(1)).asNum();
        try {
            PersistentBlockEntity patt0$temp = mc.blockEntity;
            if (patt0$temp instanceof AbstractMachineBlockEntity) {
                AbstractMachineBlockEntity mbe = (AbstractMachineBlockEntity)patt0$temp;
                for (GasTank t : mbe.gasTankList()) {
                    BlockPos nmsPos;
                    if (!tankName.equals(t.getName())) continue;
                    Level nmsLevel = mbe.getNMSLevel();
                    GasStack stored = t.getGas(nmsLevel, nmsPos = mbe.getMachinePos());
                    if (stored != null && stored.getAmount() >= amount) {
                        t.extract(nmsLevel, nmsPos, amount, null);
                        return PolyValue.of(1.0);
                    }
                    return PolyValue.of(0.0);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(0.0);
    }).method("gas", (mc, args) -> {
        if (args.isEmpty() || mc.blockEntity == null) {
            return PolyValue.of(0.0);
        }
        String tankName = ((PolyValue)args.get(0)).asStr();
        try {
            PersistentBlockEntity patt0$temp = mc.blockEntity;
            if (patt0$temp instanceof AbstractMachineBlockEntity) {
                AbstractMachineBlockEntity mbe = (AbstractMachineBlockEntity)patt0$temp;
                for (GasTank t : mbe.gasTankList()) {
                    if (!tankName.equals(t.getName())) continue;
                    GasStack stored = t.getGas(mbe.getNMSLevel(), mbe.getMachinePos());
                    return PolyValue.of(stored != null ? (double)stored.getAmount() : 0.0);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(0.0);
    }).method("set_flag", (mc, args) -> {
        if (args.size() < 2 || mc.blockEntity == null) {
            return PolyValue.NULL;
        }
        String flagKey = ((PolyValue)args.get(0)).asStr();
        int flagVal = (int)((PolyValue)args.get(1)).asNum();
        try {
            TypedKey tk = TypedKey.of("polyfills", "flag_" + flagKey, NbtType.INTEGER);
            mc.blockEntity.set(tk, flagVal);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(flagVal);
    }).method("get_flag", (mc, args) -> {
        if (args.isEmpty() || mc.blockEntity == null) {
            return PolyValue.of(0.0);
        }
        String flagKey = ((PolyValue)args.get(0)).asStr();
        try {
            TypedKey tk = TypedKey.of("polyfills", "flag_" + flagKey, NbtType.INTEGER);
            Integer v = (Integer)mc.blockEntity.get(tk);
            return PolyValue.of(v != null ? (double)v.intValue() : 0.0);
        }
        catch (Throwable throwable) {
            return PolyValue.of(0.0);
        }
    }).method("set_str_flag", (mc, args) -> {
        if (args.size() < 2 || mc.blockEntity == null) {
            return PolyValue.NULL;
        }
        String flagKey = ((PolyValue)args.get(0)).asStr();
        String flagVal = ((PolyValue)args.get(1)).asStr();
        try {
            TypedKey tk = TypedKey.of("polyfills", "sflag_" + flagKey, NbtType.STRING);
            mc.blockEntity.set(tk, flagVal);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(flagVal);
    }).method("get_str_flag", (mc, args) -> {
        if (args.isEmpty() || mc.blockEntity == null) {
            return PolyValue.of("");
        }
        String flagKey = ((PolyValue)args.get(0)).asStr();
        try {
            TypedKey tk = TypedKey.of("polyfills", "sflag_" + flagKey, NbtType.STRING);
            String v = (String)mc.blockEntity.get(tk);
            return PolyValue.of(v != null ? v : "");
        }
        catch (Throwable throwable) {
            return PolyValue.of("");
        }
    }).method("break_block_at", (mc, args) -> {
        if (args.size() < 3) {
            return new PolyValue.Array(List.of());
        }
        try {
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos base = mc.nmsPos();
            BlockPos dropPos = base.offset(dx, dy, dz);
            BlockState state = sl.getBlockState(dropPos);
            if (state.isAir()) {
                return new PolyValue.Array(List.of());
            }
            List drops = Block.getDrops((BlockState)state, (ServerLevel)sl, (BlockPos)dropPos, null);
            sl.setBlock(dropPos, Blocks.AIR.defaultBlockState(), 3);
            ArrayList<PolyValue> result = new ArrayList<PolyValue>();
            for (ItemStack s : drops) {
                result.add(new PolyValue.Item(s));
            }
            return new PolyValue.Array(result);
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("break_block_facing", (mc, args) -> {
        try {
            int[] off = mc.facingOffset();
            return mc.delegate.call("break_block_at", List.of(PolyValue.of(off[0]), PolyValue.of(off[1]), PolyValue.of(off[2])));
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("scan_block_at", (mc, args) -> {
        if (args.size() < 3) {
            return PolyValue.of("minecraft:air");
        }
        try {
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos target = mc.nmsPos().offset(dx, dy, dz);
            BlockState state = sl.getBlockState(target);
            String id = BuiltInRegistries.BLOCK.getKey((Object)state.getBlock()).toString();
            return PolyValue.of(id);
        }
        catch (Throwable ignored) {
            return PolyValue.of("minecraft:air");
        }
    }).method("blocks_in_range", (mc, args) -> {
        int radius = args.isEmpty() ? 3 : (int)((PolyValue)args.get(0)).asNum();
        try {
            ServerLevel sl = mc.nmsLevel();
            BlockPos base = mc.nmsPos();
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos[] rb = MachineClass.resolveReal(sl, base, rl);
            ServerLevel realSl = rl[0];
            BlockPos realBase = rb[0];
            ArrayList<PolyValue> result = new ArrayList<PolyValue>();
            for (int dx = -radius; dx <= radius; ++dx) {
                for (int dy = -radius; dy <= radius; ++dy) {
                    for (int dz = -radius; dz <= radius; ++dz) {
                        BlockPos p = realBase.offset(dx, dy, dz);
                        BlockState bs = realSl.getBlockState(p);
                        if (bs.isAir()) continue;
                        result.add(BlockClass.of(realSl, p));
                    }
                }
            }
            return new PolyValue.Array(result);
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("block_at", (mc, args) -> {
        if (args.size() < 3) {
            return PolyValue.NULL;
        }
        try {
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos local = mc.nmsPos().offset(dx, dy, dz);
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos realP = MachineClass.resolveReal(sl, local, rl)[0];
            return BlockClass.of(rl[0], realP);
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).method("set_block_state", (mc, args) -> {
        Object patt0$temp;
        if (args.size() < 3 || !((patt0$temp = args.get(0)) instanceof BlockClass)) {
            return PolyValue.of(false);
        }
        BlockClass bc = (BlockClass)patt0$temp;
        String pName = ((PolyValue)args.get(1)).asStr();
        String pVal = ((PolyValue)args.get(2)).asStr();
        try {
            ImmutableBlockState newCeState;
            net.momirealms.craftengine.core.block.property.Property ceProp;
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos rp = MachineClass.resolveReal(bc.level, bc.pos, rl)[0];
            BlockState st = rl[0].getBlockState(rp);
            for (Property prop : st.getProperties()) {
                BlockState ns;
                if (!prop.getName().equals(pName) || (ns = MachineClass.applyProp(st, prop, pVal)) == null) continue;
                rl[0].setBlock(rp, ns, 3);
                return PolyValue.of(true);
            }
            ImmutableBlockState ceState = BlockStateUtils.getOptionalCustomBlockState((Object)st).orElse(null);
            if (ceState != null && (ceProp = ((BlockDefinition)ceState.owner().value()).getProperty(pName)) != null && (newCeState = MachineClass.applyCeProp(ceState, ceProp, pVal)) != null) {
                rl[0].setBlock(rp, (BlockState)newCeState.customBlockState().minecraftState(), 2);
                return PolyValue.of(true);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(false);
    }).method("cycle_block_prop", (mc, args) -> {
        Object patt0$temp;
        if (args.size() < 2 || !((patt0$temp = args.get(0)) instanceof BlockClass)) {
            return PolyValue.of(false);
        }
        BlockClass bc = (BlockClass)patt0$temp;
        String pName = ((PolyValue)args.get(1)).asStr();
        try {
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos rp = MachineClass.resolveReal(bc.level, bc.pos, rl)[0];
            BlockState st = rl[0].getBlockState(rp);
            for (Property prop : st.getProperties()) {
                BlockState ns;
                if (!prop.getName().equals(pName) || (ns = MachineClass.cycleProp(st, prop)) == null) continue;
                rl[0].setBlock(rp, ns, 3);
                return PolyValue.of(true);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(false);
    }).method("set_block", (mc, args) -> {
        Object patt0$temp;
        if (args.size() < 2 || !((patt0$temp = args.get(0)) instanceof BlockClass)) {
            return PolyValue.of(false);
        }
        BlockClass bc = (BlockClass)patt0$temp;
        String id = ((PolyValue)args.get(1)).asStr();
        try {
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos realPos = MachineClass.resolveReal(bc.level, bc.pos, rl)[0];
            Object full = id.contains(":") ? id : "minecraft:" + id;
            Block blk = (Block)BuiltInRegistries.BLOCK.getValue(Identifier.parse((String)full));
            if (blk == null) {
                return PolyValue.of(false);
            }
            rl[0].setBlock(realPos, blk.defaultBlockState(), 3);
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("push_entity", (mc, args) -> {
        Object patt0$temp;
        if (args.isEmpty() || !((patt0$temp = args.get(0)) instanceof EntityClass)) {
            return PolyValue.of(false);
        }
        EntityClass ec = (EntityClass)patt0$temp;
        try {
            double vz;
            double vy;
            double vx;
            Object patt1$temp;
            if (args.size() >= 2 && (patt1$temp = args.get(1)) instanceof VectorClass) {
                VectorClass v = (VectorClass)patt1$temp;
                vx = v.x;
                vy = v.y;
                vz = v.z;
            } else if (args.size() >= 4) {
                vx = ((PolyValue)args.get(1)).asNum();
                vy = ((PolyValue)args.get(2)).asNum();
                vz = ((PolyValue)args.get(3)).asNum();
            } else {
                return PolyValue.of(false);
            }
            ec.entity.setDeltaMovement(vx, vy, vz);
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).property("contraption_world", mc -> {
        try {
            ServerLevel sl = mc.nmsLevel();
            if (sl instanceof ContraptionLevel) {
                ContraptionLevel cl = (ContraptionLevel)sl;
                return new ContraptionWorldClass(cl);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.NULL;
    }).method("belt_at", (mc, args) -> {
        if (args.size() < 3) {
            return PolyValue.NULL;
        }
        try {
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos local = mc.nmsPos().offset(dx, dy, dz);
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos realP = MachineClass.resolveReal(sl, local, rl)[0];
            return new BeltClass(rl[0], realP);
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).method("adjacent_belts", (mc, args) -> {
        ArrayList<PolyValue> result = new ArrayList<PolyValue>();
        int[][] dirs = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
        try {
            ServerLevel sl = mc.nmsLevel();
            BlockPos base = mc.nmsPos();
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos realBase = MachineClass.resolveReal(sl, base, rl)[0];
            for (int[] d : dirs) {
                BlockPos p = realBase.offset(d[0], d[1], d[2]);
                BlockState bs = rl[0].getBlockState(p);
                boolean hasFacing = bs.getProperties().stream().anyMatch(prop -> prop.getName().equals("facing") || prop.getName().equals("horizontal_facing"));
                if (!hasFacing) continue;
                result.add(new BeltClass(rl[0], p));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return new PolyValue.Array(result);
    }).method("pipe_at", (mc, args) -> {
        if (args.size() < 3) {
            return PolyValue.NULL;
        }
        try {
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos local = mc.nmsPos().offset(dx, dy, dz);
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos realP = MachineClass.resolveReal(sl, local, rl)[0];
            return new PipeClass(rl[0], realP);
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).property("rpm_direction", mc -> {
        float r = mc.rpmFromBlockEntity();
        return PolyValue.of(r > 0.0f ? 1.0 : (r < 0.0f ? -1.0 : 0.0));
    }).property("rpm_abs", mc -> PolyValue.of(Math.abs(mc.rpmFromBlockEntity()))).property("rpm_inv", mc -> PolyValue.of(-mc.rpmFromBlockEntity())).method("get_rpm_at", (mc, args) -> {
        try {
            BlockEntityController patt0$temp;
            BlockPos target = mc.resolveTargetPos(args);
            if (target == null) {
                return PolyValue.of(0.0);
            }
            ServerLevel sl = mc.nmsLevel();
            net.momirealms.craftengine.core.block.entity.BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)sl, target);
            if (be != null && (patt0$temp = be.controller) instanceof RpmProvider) {
                RpmProvider p = (RpmProvider)patt0$temp;
                return PolyValue.of(p.getRpm());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(0.0);
    }).method("set_rpm_at", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(false);
        }
        try {
            if (args.get(0) instanceof PolyValue.Str) {
                float rpmVal = (float)((PolyValue)args.get(args.size() - 1)).asNum();
                for (int i = 0; i < args.size() - 1; ++i) {
                    Direction dir = mc.resolveNamedFace(((PolyValue)args.get(i)).asStr());
                    if (dir == null) continue;
                    mc.pushRpmToFace(dir, rpmVal);
                }
                return PolyValue.of(true);
            }
            if (args.size() < 4) {
                return PolyValue.of(false);
            }
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            float rpmVal = (float)((PolyValue)args.get(3)).asNum();
            mc.pushRpmToOffset(dx, dy, dz, rpmVal);
            return PolyValue.of(true);
        }
        catch (Throwable throwable) {
            return PolyValue.of(false);
        }
    }).method("get_glued_blocks", (mc, args) -> {
        try {
            ServerLevel sl = mc.nmsLevel();
            BlockPos pos = mc.nmsPos();
            Set<BlockPos> structure = GlueRegistry.structureAt((ResourceKey<Level>)sl.dimension(), pos);
            ArrayList<PolyValue> result = new ArrayList<PolyValue>();
            for (BlockPos bp : structure) {
                if (bp.equals((Object)pos)) continue;
                result.add(BlockClass.of(sl, bp));
            }
            return new PolyValue.Array(result);
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("get_rpm_ratio", (mc, args) -> {
        try {
            DataMachineBlockEntity dm;
            PersistentBlockEntity patt0$temp = mc.blockEntity;
            if (patt0$temp instanceof DataMachineBlockEntity && (dm = (DataMachineBlockEntity)patt0$temp).definition() != null) {
                return PolyValue.of(dm.definition().rpmRatio());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(1.0);
    }).method("set_rpm_output", (mc, args) -> {
        float rpm = args.isEmpty() ? 0.0f : (float)((PolyValue)args.get(0)).asNum();
        try {
            PersistentBlockEntity patt0$temp = mc.blockEntity;
            if (patt0$temp instanceof DataMachineBlockEntity) {
                DataMachineBlockEntity dm = (DataMachineBlockEntity)patt0$temp;
                dm.setRpmSourceOutput(rpm);
                ServerLevel sl = mc.nmsLevel();
                BlockPos pos = mc.nmsPos();
                if (sl != null && pos != null) {
                    BlockState bs = sl.getBlockState(pos);
                    sl.updateNeighborsAt(pos, bs.getBlock());
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(rpm);
    }).method("report_su", (mc, args) -> {
        float su = args.isEmpty() ? 0.0f : (float)Math.max(0.0, ((PolyValue)args.get(0)).asNum());
        try {
            PersistentBlockEntity patt0$temp = mc.blockEntity;
            if (patt0$temp instanceof DataMachineBlockEntity) {
                DataMachineBlockEntity dm = (DataMachineBlockEntity)patt0$temp;
                dm.reportStressLoad(su);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(su);
    }).method("windmill_assemble", (mc, args) -> {
        try {
            ContraptionEntity ce;
            ServerLevel sl = mc.nmsLevel();
            BlockPos pos = mc.nmsPos();
            UUID assembler = null;
            if (mc.singlePlayer != null) {
                try {
                    assembler = mc.singlePlayer.getUniqueId();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if ((ce = ContraptionAssembler.assemble(sl.getWorld(), pos, MachineContraptionType.KEY, 0.0, 0.0, assembler)) != null && mc.blockEntity != null) {
                TypedKey tk = TypedKey.of("polyfills", "sflag_contraption_uuid", NbtType.STRING);
                mc.blockEntity.set(tk, ce.state().id().toString());
            }
            return PolyValue.of(ce != null);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("get_windmill_rpm", (mc, args) -> {
        try {
            UUID cid;
            if (mc.blockEntity == null) {
                return PolyValue.of(0.0);
            }
            TypedKey tk = TypedKey.of("polyfills", "sflag_contraption_uuid", NbtType.STRING);
            String uuidStr = (String)mc.blockEntity.get(tk);
            if (uuidStr == null || uuidStr.isEmpty()) {
                return PolyValue.of(0.0);
            }
            try {
                cid = UUID.fromString(uuidStr);
            }
            catch (IllegalArgumentException ignored2) {
                return PolyValue.of(0.0);
            }
            if (ContraptionManager.get(cid) == null) {
                mc.blockEntity.set(tk, "");
                return PolyValue.of(0.0);
            }
            ServerLevel sl = mc.nmsLevel();
            BlockPos pos = mc.nmsPos();
            // RPM is now computed by script from contraption_blocks() — just return 0 here
            return PolyValue.of(0.0);
        }
        catch (Throwable ignored) {
            return PolyValue.of(0.0);
        }
    }).method("windmill_disassemble", (mc, args) -> {
        try {
            if (mc.blockEntity == null) {
                return PolyValue.of(false);
            }
            TypedKey tk = TypedKey.of("polyfills", "sflag_contraption_uuid", NbtType.STRING);
            String uuidStr = (String)mc.blockEntity.get(tk);
            if (uuidStr == null || uuidStr.isEmpty()) {
                return PolyValue.of(false);
            }
            UUID cid = UUID.fromString(uuidStr);
            ContraptionEntity ce = ContraptionManager.get(cid);
            if (ce == null) {
                mc.blockEntity.set(tk, "");
                return PolyValue.of(false);
            }
            CraftWorld bukkit = mc.nmsLevel().getWorld();
            ContraptionAssembler.disassemble((World)bukkit, ce);
            mc.blockEntity.set(tk, "");
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("windmill_alive", (mc, args) -> {
        try {
            if (mc.blockEntity == null) {
                return PolyValue.of(false);
            }
            TypedKey tk = TypedKey.of("polyfills", "sflag_contraption_uuid", NbtType.STRING);
            String uuidStr = (String)mc.blockEntity.get(tk);
            if (uuidStr == null || uuidStr.isEmpty()) {
                return PolyValue.of(false);
            }
            UUID cid = UUID.fromString(uuidStr);
            return PolyValue.of(ContraptionManager.get(cid) != null);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("relay_rpm", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(false);
        }
        try {
            float rpmVal = (float)((PolyValue)args.get(0)).asNum();
            if (args.size() > 1) {
                for (int i = 1; i < args.size(); ++i) {
                    Direction dir = mc.resolveNamedFace(((PolyValue)args.get(i)).asStr());
                    if (dir == null) continue;
                    mc.pushRpmToFace(dir, rpmVal);
                }
            } else {
                DataMachineBlockEntity dm;
                PersistentBlockEntity patt0$temp = mc.blockEntity;
                if (!(patt0$temp instanceof DataMachineBlockEntity) || (dm = (DataMachineBlockEntity)patt0$temp).definition() == null) {
                    return PolyValue.of(false);
                }
                MachineDefinition def = dm.definition();
                Direction facing = mc.currentFacing();
                for (String s : def.rpmOutputFacesRaw()) {
                    Direction dir = DataMachineBlockEntity.rpmFacesContainDir(s, facing);
                    if (dir == null) continue;
                    mc.pushRpmToFace(dir, rpmVal);
                }
                float ratio = def.rpmRatio();
                for (String s : def.rpmOutputInvertedRaw()) {
                    Direction dir;
                    if ("axis_perp".equals(s)) {
                        try {
                            net.momirealms.craftengine.core.block.property.Property axisProp;
                            ServerLevel sl = mc.nmsLevel();
                            BlockPos pos = mc.nmsPos();
                            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState((Object)sl.getBlockState(pos)).orElse(null);
                            if (cs != null && (axisProp = ((BlockDefinition)cs.owner().value()).getProperty("axis")) != null) {
                                Direction.Axis cogAxis = switch (String.valueOf(cs.get(axisProp)).toLowerCase()) {
                                    case "x" -> Direction.Axis.X;
                                    case "y" -> Direction.Axis.Y;
                                    default -> Direction.Axis.Z;
                                };
                                for (Direction dir2 : Direction.values()) {
                                    if (dir2.getAxis() == cogAxis) continue;
                                    mc.pushRpmToFace(dir2, -rpmVal * ratio);
                                }
                                continue;
                            }
                        }
                        catch (Throwable sl) {
                            // empty catch block
                        }
                    }
                    if ((dir = DataMachineBlockEntity.rpmFacesContainDir(s, facing)) == null) continue;
                    mc.pushRpmToFace(dir, -rpmVal * ratio);
                }
            }
            return PolyValue.of(true);
        }
        catch (Throwable throwable) {
            return PolyValue.of(false);
        }
    }).method("emit_redstone", (mc, args) -> {
        int power;
        if (mc.blockEntity == null) {
            if (DataMachineBlockEntity.SCRIPT_DEBUG) {
                System.out.println("[CEP emit_redstone] blockEntity is NULL");
            }
            return PolyValue.of(false);
        }
        int n = power = args.isEmpty() ? 0 : (int)Math.max(0.0, Math.min(15.0, ((PolyValue)args.get(0)).asNum()));
        if (DataMachineBlockEntity.SCRIPT_DEBUG) {
            System.out.println("[CEP emit_redstone] power=" + power + " be=" + ((Object)((Object)mc.blockEntity)).getClass().getSimpleName());
        }
        try {
            PersistentBlockEntity patt0$temp;
            net.momirealms.craftengine.core.block.property.Property prop;
            ServerLevel sl = mc.nmsLevel();
            BlockPos pos = mc.nmsPos();
            BlockState bs = sl.getBlockState(pos);
            boolean set = false;
            ImmutableBlockState ceState = BlockStateUtils.getOptionalCustomBlockState((Object)bs).orElse(null);
            if (ceState != null && (prop = ((BlockDefinition)ceState.owner().value()).getProperty("power_level")) instanceof IntegerProperty) {
                IntegerProperty ip = (IntegerProperty)prop;
                ImmutableBlockState newState = ceState.with((net.momirealms.craftengine.core.block.property.Property)ip, (Comparable)Integer.valueOf(power));
                sl.setBlock(pos, (BlockState)newState.customBlockState().minecraftState(), 2);
                set = true;
            }
            if (!set && (patt0$temp = mc.blockEntity) instanceof PersistentBlockEntity) {
                PersistentBlockEntity pbe = patt0$temp;
                pbe.set(Key.of((String)"polyfills", (String)"_redstone_power"), NbtType.INTEGER, Integer.valueOf(power));
                set = true;
            }
            if (set) {
                Block blk = bs.getBlock();
                sl.updateNeighborsAt(pos, blk);
                for (Direction dir : Direction.values()) {
                    sl.updateNeighborsAt(pos.relative(dir), blk);
                }
            }
            return PolyValue.of(set);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("apply_bone_meal", (mc, args) -> {
        Object patt0$temp;
        if (args.isEmpty() || !((patt0$temp = args.get(0)) instanceof BlockClass)) {
            return PolyValue.of(false);
        }
        BlockClass bc = (BlockClass)patt0$temp;
        try {
            BlockHitResult hit;
            UseOnContext ctx;
            InteractionResult r;
            boolean result;
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos realPos = MachineClass.resolveReal(bc.level, bc.pos, rl)[0];
            ServerLevel realSl = rl[0];
            ItemStack boneStack = new ItemStack((ItemLike)Items.BONE_MEAL);
            ServerPlayer nearestP = null;
            if (mc.world != null) {
                try {
                    org.bukkit.entity.Player closest = mc.nearestPlayer(16.0);
                    if (closest != null) {
                        nearestP = ((CraftPlayer)closest).getHandle();
                    }
                }
                catch (Throwable closest) {
                    // empty catch block
                }
            }
            boolean bl = result = (r = boneStack.useOn(ctx = new UseOnContext((Level)realSl, (Player)nearestP, InteractionHand.MAIN_HAND, boneStack, hit = new BlockHitResult(Vec3.atCenterOf((Vec3i)realPos), Direction.UP, realPos, false)))) != InteractionResult.FAIL && r != InteractionResult.PASS;
            if (result) {
                realSl.playSound(null, realPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return PolyValue.of(result);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("spawn_entity", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(false);
        }
        String typeId = ((PolyValue)args.get(0)).asStr();
        double dx = args.size() > 1 ? ((PolyValue)args.get(1)).asNum() : 0.0;
        double dy = args.size() > 2 ? ((PolyValue)args.get(2)).asNum() : 0.0;
        double dz = args.size() > 3 ? ((PolyValue)args.get(3)).asNum() : 0.0;
        try {
            ServerLevel sl = mc.nmsLevel();
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos rp = MachineClass.resolveReal(sl, mc.nmsPos(), rl)[0];
            Object full = typeId.contains(":") ? typeId : "minecraft:" + typeId;
            EntityType et = (EntityType)BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse((String)full));
            if (et == null) {
                return PolyValue.of(false);
            }
            Entity entity = et.create(rl[0], null, rp, EntitySpawnReason.SPAWNER, false, false);
            if (entity == null) {
                return PolyValue.of(false);
            }
            entity.setPosRaw((double)rp.getX() + 0.5 + dx, (double)rp.getY() + dy, (double)rp.getZ() + 0.5 + dz);
            return PolyValue.of(rl[0].addFreshEntity(entity));
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("play_sound", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(false);
        }
        String soundId = ((PolyValue)args.get(0)).asStr();
        float vol = args.size() > 1 ? (float)((PolyValue)args.get(1)).asNum() : 1.0f;
        float pitch = args.size() > 2 ? (float)((PolyValue)args.get(2)).asNum() : 1.0f;
        try {
            ServerLevel sl = mc.nmsLevel();
            BlockPos pos = mc.nmsPos();
            Object full = soundId.contains(":") ? soundId : "minecraft:" + soundId;
            SoundEvent sound = (SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse((String)full));
            if (sound == null) {
                return PolyValue.of(false);
            }
            sl.playSound(null, pos, sound, SoundSource.BLOCKS, vol, pitch);
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("get_su_capacity", (mc, args) -> {
        try {
            PersistentBlockEntity patt0$temp = mc.blockEntity;
            if (patt0$temp instanceof DataMachineBlockEntity) {
                DataMachineBlockEntity dm = (DataMachineBlockEntity)patt0$temp;
                float total = 0.0f;
                for (RpmProvider m : dm.getActiveMotors()) {
                    if (!(m instanceof DataMotorBlockEntity)) continue;
                    DataMotorBlockEntity motor = (DataMotorBlockEntity)m;
                    total += motor.getSuCapacity();
                }
                return PolyValue.of(total);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(0.0);
    }).method("get_active_motors_count", (mc, args) -> {
        try {
            PersistentBlockEntity patt0$temp = mc.blockEntity;
            if (patt0$temp instanceof DataMachineBlockEntity) {
                DataMachineBlockEntity dm = (DataMachineBlockEntity)patt0$temp;
                return PolyValue.of(dm.getActiveMotors().size());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(0.0);
    }).method("is_overstressed", (mc, args) -> {
        try {
            PersistentBlockEntity patt0$temp = mc.blockEntity;
            if (patt0$temp instanceof DataMachineBlockEntity) {
                DataMachineBlockEntity dm = (DataMachineBlockEntity)patt0$temp;
                boolean over = true;
                for (RpmProvider m : dm.getActiveMotors()) {
                    DataMotorBlockEntity motor;
                    if (!(m instanceof DataMotorBlockEntity) || (motor = (DataMotorBlockEntity)m).isOverstressed()) continue;
                    over = false;
                    break;
                }
                return PolyValue.of(over && !dm.getActiveMotors().isEmpty());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(false);
    }).method("facing_block_at", (mc, args) -> {
        if (args.size() < 3) {
            return PolyValue.NULL;
        }
        try {
            int fwd = (int)((PolyValue)args.get(0)).asNum();
            int up = (int)((PolyValue)args.get(1)).asNum();
            int side = (int)((PolyValue)args.get(2)).asNum();
            int[] fo = mc.facingOffset();
            Direction facing = mc.currentFacing();
            Direction right = facing.getClockWise();
            int wx = fo[0] * fwd + right.getStepX() * side;
            int wy = up;
            int wz = fo[2] * fwd + right.getStepZ() * side;
            ServerLevel sl = mc.nmsLevel();
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos realPos = MachineClass.resolveReal(sl, mc.nmsPos().offset(wx, wy, wz), rl)[0];
            return BlockClass.of(rl[0] != null ? rl[0] : sl, realPos);
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).method("drop_item", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(false);
        }
        try {
            PolyValue.Item it;
            PolyValue val = (PolyValue)args.get(0);
            if (!(val instanceof PolyValue.Item) || (it = (PolyValue.Item)val).stack() == null || it.stack().isEmpty()) {
                return PolyValue.of(false);
            }
            double dx = args.size() > 1 ? ((PolyValue)args.get(1)).asNum() : 0.0;
            double dy = args.size() > 2 ? ((PolyValue)args.get(2)).asNum() : 0.5;
            double dz = args.size() > 3 ? ((PolyValue)args.get(3)).asNum() : 0.0;
            ServerLevel sl = mc.nmsLevel();
            BlockPos pos = mc.nmsPos();
            ItemEntity ie = new ItemEntity((Level)sl, (double)pos.getX() + 0.5 + dx, (double)pos.getY() + dy, (double)pos.getZ() + 0.5 + dz, it.stack().copy());
            ie.setDefaultPickUpDelay();
            sl.addFreshEntity((Entity)ie);
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("get_glued_blocks", (mc, args) -> {
        try {
            ServerLevel sl = mc.nmsLevel();
            BlockPos pos = mc.nmsPos();
            Set<BlockPos> structure = GlueRegistry.structureAt((ResourceKey<Level>)sl.dimension(), pos);
            ArrayList<PolyValue> result = new ArrayList<PolyValue>();
            for (BlockPos bp : structure) {
                if (bp.equals((Object)pos)) continue;
                result.add(BlockClass.of(sl, bp));
            }
            return new PolyValue.Array(result);
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("is_player_looking_at", (mc, args) -> {
        double maxDist = args.isEmpty() ? 6.0 : ((PolyValue)args.get(0)).asNum();
        double margin = args.size() >= 2 ? ((PolyValue)args.get(1)).asNum() : 0.0;
        try {
            List<Object> candidates;
            ServerLevel rsl;
            ServerLevel sl = mc.nmsLevel();
            if (sl == null) {
                return PolyValue.of(false);
            }
            ArrayList<BlockPos> cells = new ArrayList<BlockPos>();
            ServerLevel[] rl = new ServerLevel[]{null};
            cells.add(MachineClass.resolveReal(sl, mc.nmsPos(), rl)[0]);
            ServerLevel serverLevel = rsl = rl[0] != null ? rl[0] : sl;
            if (mc.footprint != null) {
                for (int[] fp : mc.footprint) {
                    ServerLevel[] rlt = new ServerLevel[]{null};
                    cells.add(MachineClass.resolveReal(sl, new BlockPos(fp[0], fp[1], fp[2]), rlt)[0]);
                }
            }
            AABB box = null;
            for (BlockPos cp : cells) {
                AABB cb = new AABB((double)cp.getX() + margin, (double)cp.getY(), (double)cp.getZ() + margin, (double)(cp.getX() + 1) - margin, (double)(cp.getY() + 1), (double)(cp.getZ() + 1) - margin);
                box = box == null ? cb : box.minmax(cb);
            }
            if (box == null) {
                return PolyValue.of(false);
            }
            if (mc.singlePlayer != null) {
                candidates = List.of(((CraftPlayer)mc.singlePlayer).getHandle());
            } else {
                double maxSq = maxDist * maxDist;
                BlockPos origin = (BlockPos)cells.get(0);
                candidates = rsl.players().stream().filter(p -> p.distanceToSqr((double)origin.getX() + 0.5, (double)origin.getY() + 0.5, (double)origin.getZ() + 0.5) <= maxSq).collect(Collectors.toList());
            }
            for (ServerPlayer sp : candidates) {
                Vec3 look;
                Vec3 eye = sp.getEyePosition(1.0f);
                if (!box.clip(eye, eye.add((look = sp.getViewVector(1.0f)).scale(maxDist))).isPresent()) continue;
                return PolyValue.of(true);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(false);
    }).method("attract_entity", (mc, args) -> {
        Object patt0$temp;
        if (args.isEmpty() || !((patt0$temp = args.get(0)) instanceof EntityClass)) {
            return PolyValue.of(false);
        }
        EntityClass ec = (EntityClass)patt0$temp;
        double speed = args.size() >= 2 ? ((PolyValue)args.get(1)).asNum() : 0.15;
        try {
            double dx = mc.x - ec.entity.getX();
            double dy = mc.y - ec.entity.getY();
            double dz = mc.z - ec.entity.getZ();
            double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len > 0.01) {
                double s = speed / len;
                ec.entity.setDeltaMovement(dx * s, dy * s, dz * s);
            }
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("kill_entity", (mc, args) -> {
        Object patt0$temp;
        if (args.isEmpty() || !((patt0$temp = args.get(0)) instanceof EntityClass)) {
            return PolyValue.of(false);
        }
        EntityClass ec = (EntityClass)patt0$temp;
        try {
            ec.entity.discard();
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("fill_fluid", (mc, args) -> {
        if (args.size() < 2 || mc.blockEntity == null) {
            return PolyValue.of(0.0);
        }
        String typeId = ((PolyValue)args.get(0)).asStr();
        int amount = (int)((PolyValue)args.get(1)).asNum();
        try {
            PersistentBlockEntity patt0$temp = mc.blockEntity;
            if (patt0$temp instanceof AbstractMachineBlockEntity) {
                AbstractMachineBlockEntity mbe = (AbstractMachineBlockEntity)patt0$temp;
                FluidType ft = FluidType.byName(typeId);
                if (ft == null) {
                    return PolyValue.of(0.0);
                }
                FluidStack fs = FluidStack.of(ft, amount);
                Level lvl = mbe.getNMSLevel();
                boolean ok = mbe.fillTank(lvl, fs);
                return PolyValue.of(ok ? (double)amount : 0.0);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(0.0);
    }).method("break_block", (mc, args) -> {
        Object patt0$temp;
        if (args.isEmpty() || !((patt0$temp = args.get(0)) instanceof BlockClass)) {
            return new PolyValue.Array(List.of());
        }
        BlockClass bc = (BlockClass)patt0$temp;
        try {
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos realPos = MachineClass.resolveReal(bc.level, bc.pos, rl)[0];
            ServerLevel realSl = rl[0];
            BlockState state = realSl.getBlockState(realPos);
            if (state.isAir()) {
                return new PolyValue.Array(List.of());
            }
            ItemStack fakeTool = new ItemStack((ItemLike)Items.DIAMOND_PICKAXE);
            List drops = Block.getDrops((BlockState)state, (ServerLevel)realSl, (BlockPos)realPos, null, null, (ItemStack)fakeTool);
            realSl.setBlock(realPos, Blocks.AIR.defaultBlockState(), 3);
            MachineClass.clearBreakAnimation(realSl, realPos);
            ArrayList<PolyValue> result = new ArrayList<PolyValue>();
            for (ItemStack s : drops) {
                result.add(new PolyValue.Item(s));
            }
            return new PolyValue.Array(result);
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("tick_break", (mc, args) -> {
        Object patt0$temp;
        if (args.isEmpty() || !((patt0$temp = args.get(0)) instanceof BlockClass)) {
            return PolyValue.NULL;
        }
        BlockClass bc = (BlockClass)patt0$temp;
        double speed = args.size() >= 2 ? Math.max(1.0, ((PolyValue)args.get(1)).asNum()) : 10.0;
        try {
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos realPos = MachineClass.resolveReal(bc.level, bc.pos, rl)[0];
            ServerLevel realSl = rl[0];
            BlockState state = realSl.getBlockState(realPos);
            if (state.isAir()) {
                mc.clearBreakState();
                return PolyValue.NULL;
            }
            int tx = realPos.getX();
            int ty = realPos.getY();
            int tz = realPos.getZ();
            int px = mc.getIntFlag("_bk_x");
            int py = mc.getIntFlag("_bk_y");
            int pz = mc.getIntFlag("_bk_z");
            if (px != tx || py != ty || pz != tz) {
                MachineClass.clearBreakAnimation(realSl, new BlockPos(px, py, pz));
                mc.setBreakState(tx, ty, tz, 0.0);
            }
            double progress = (double)mc.getIntFlag("_bk_prog") + speed;
            if (DataMachineBlockEntity.SCRIPT_DEBUG) {
                System.out.println("[CEP tick_break] pos=" + realPos.toShortString() + " state=" + state.getBlock().getName().getString() + " progress=" + progress);
            }
            if (progress >= 100.0) {
                ItemStack fakeTool = new ItemStack((ItemLike)Items.DIAMOND_PICKAXE);
                List drops = Block.getDrops((BlockState)state, (ServerLevel)realSl, (BlockPos)realPos, null, null, (ItemStack)fakeTool);
                realSl.setBlock(realPos, Blocks.AIR.defaultBlockState(), 3);
                realSl.levelEvent(2001, realPos, Block.getId((BlockState)state));
                MachineClass.clearBreakAnimation(realSl, realPos);
                mc.clearBreakState();
                ArrayList<PolyValue> result = new ArrayList<PolyValue>();
                for (ItemStack s : drops) {
                    if (s.isEmpty()) continue;
                    result.add(new PolyValue.Item(s.copy()));
                }
                if (DataMachineBlockEntity.SCRIPT_DEBUG) {
                    System.out.println("[CEP tick_break] BROKE! drops=" + drops.size() + " result=" + result.size());
                }
                return new PolyValue.Array(result);
            }
            mc.setBreakState(tx, ty, tz, progress);
            int stage = (int)(progress / 100.0 * 9.0);
            MachineClass.sendBreakAnimation(realSl, realPos, stage);
            return PolyValue.NULL;
        }
        catch (Throwable t) {
            if (DataMachineBlockEntity.SCRIPT_DEBUG) {
                System.out.println("[CEP tick_break] error: " + t.getMessage());
            }
            return PolyValue.NULL;
        }
    }).method("hold_contraption", (mc, args) -> {
        try {
            ServerLevel sl = mc.nmsLevel();
            if (sl instanceof ContraptionLevel) {
                ContraptionLevel cl = (ContraptionLevel)sl;
                ContraptionWorlds.entityOf(cl).ifPresent(entity -> {
                    UUID id = entity.state().id();
                    ScriptStallBehavior b = SCRIPT_STALLS.computeIfAbsent(id, k -> {
                        ScriptStallBehavior nb = new ScriptStallBehavior();
                        entity.state().addBehavior(nb);
                        return nb;
                    });
                    b.setStalled(true);
                });
                return PolyValue.of(true);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(false);
    }).method("release_contraption", (mc, args) -> {
        try {
            ServerLevel sl = mc.nmsLevel();
            if (sl instanceof ContraptionLevel) {
                ContraptionLevel cl = (ContraptionLevel)sl;
                ContraptionWorlds.entityOf(cl).ifPresent(entity -> {
                    ScriptStallBehavior b = SCRIPT_STALLS.get(entity.state().id());
                    if (b != null) {
                        b.setStalled(false);
                    }
                });
                return PolyValue.of(true);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(false);
    }).method("place_block_at", (mc, args) -> {
        if (args.size() < 4) {
            return PolyValue.of(false);
        }
        try {
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            PolyValue itemArg = (PolyValue)args.get(3);
            if (!(itemArg instanceof PolyValue.Item)) {
                return PolyValue.of(false);
            }
            PolyValue.Item itemVal = (PolyValue.Item)itemArg;
            ItemStack nmsStack = itemVal.stack();
            if (nmsStack == null || nmsStack.isEmpty()) {
                return PolyValue.of(false);
            }
            Item patt0$temp = nmsStack.getItem();
            if (!(patt0$temp instanceof BlockItem)) {
                return PolyValue.of(false);
            }
            BlockItem bi = (BlockItem)patt0$temp;
            ServerLevel sl = mc.nmsLevel();
            BlockPos target = mc.nmsPos().offset(dx, dy, dz);
            sl.setBlock(target, bi.getBlock().defaultBlockState(), 3);
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("push_item_to_inventory", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(0.0);
        }
        Object patt0$temp = args.get(0);
        if (!(patt0$temp instanceof PolyValue.Item)) {
            return PolyValue.of(0.0);
        }
        PolyValue.Item itemArg = (PolyValue.Item)patt0$temp;
        PersistentBlockEntity patt1$temp = mc.blockEntity;
        if (!(patt1$temp instanceof PersistentWorldlyBlockEntity)) {
            return PolyValue.of(0.0);
        }
        PersistentWorldlyBlockEntity wbe = (PersistentWorldlyBlockEntity)patt1$temp;
        try {
            int slot;
            ItemStack incoming = itemArg.stack();
            if (incoming == null || incoming.isEmpty()) {
                return PolyValue.of(0.0);
            }
            incoming = incoming.copy();
            int total = incoming.getCount();
            int containerSize = wbe.getContainerSize();
            int maxSlot = wbe.getMaxStackSize();
            for (slot = 0; slot < containerSize && !incoming.isEmpty(); ++slot) {
                int space;
                int take;
                ItemStack existing = wbe.getItem(slot);
                if (existing.isEmpty() || !ItemStack.isSameItemSameComponents((ItemStack)existing, (ItemStack)incoming) || (take = Math.min(space = Math.min(existing.getMaxStackSize(), maxSlot) - existing.getCount(), incoming.getCount())) <= 0) continue;
                existing.grow(take);
                incoming.shrink(take);
                wbe.setItem(slot, existing);
            }
            for (slot = 0; slot < containerSize && !incoming.isEmpty(); ++slot) {
                if (!wbe.getItem(slot).isEmpty()) continue;
                int take = Math.min(incoming.getCount(), Math.min(incoming.getMaxStackSize(), maxSlot));
                wbe.setItem(slot, incoming.copyWithCount(take));
                incoming.shrink(take);
            }
            return PolyValue.of(total - incoming.getCount());
        }
        catch (Throwable ignored) {
            return PolyValue.of(0.0);
        }
    }).method("damage_entities_in", (mc, args) -> {
        if (args.size() < 2) {
            return PolyValue.of(0.0);
        }
        try {
            double radius = ((PolyValue)args.get(0)).asNum();
            float amount = (float)((PolyValue)args.get(1)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            AABB aabb = new AABB((double)bp.getX() - radius, (double)bp.getY() - radius, (double)bp.getZ() - radius, (double)(bp.getX() + 1) + radius, (double)(bp.getY() + 1) + radius, (double)(bp.getZ() + 1) + radius);
            List entities = sl.getEntitiesOfClass(LivingEntity.class, aabb);
            for (LivingEntity e : entities) {
                e.hurt(sl.damageSources().generic(), amount);
            }
            return PolyValue.of(entities.size());
        }
        catch (Throwable ignored) {
            return PolyValue.of(0.0);
        }
    }).method("fire_damage_entities_in", (mc, args) -> {
        if (args.size() < 2) {
            return PolyValue.of(0.0);
        }
        try {
            double radius = ((PolyValue)args.get(0)).asNum();
            int fireTicks = (int)((PolyValue)args.get(1)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            AABB aabb = new AABB((double)bp.getX() - radius, (double)bp.getY() - radius, (double)bp.getZ() - radius, (double)(bp.getX() + 1) + radius, (double)(bp.getY() + 1) + radius, (double)(bp.getZ() + 1) + radius);
            List entities = sl.getEntitiesOfClass(LivingEntity.class, aabb);
            for (LivingEntity e : entities) {
                e.hurt(sl.damageSources().onFire(), 1.0f);
                e.setRemainingFireTicks(fireTicks);
            }
            return PolyValue.of(entities.size());
        }
        catch (Throwable ignored) {
            return PolyValue.of(0.0);
        }
    }).method("attract_items_in", (mc, args) -> {
        if (args.isEmpty()) {
            return new PolyValue.Array(List.of());
        }
        try {
            double radius = ((PolyValue)args.get(0)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            AABB aabb = new AABB((double)bp.getX() - radius, (double)bp.getY() - radius, (double)bp.getZ() - radius, (double)(bp.getX() + 1) + radius, (double)(bp.getY() + 1) + radius, (double)(bp.getZ() + 1) + radius);
            List entities = sl.getEntitiesOfClass(ItemEntity.class, aabb);
            ArrayList<PolyValue> result = new ArrayList<PolyValue>();
            for (ItemEntity e : entities) {
                result.add(new PolyValue.Item(e.getItem().copy()));
                e.discard();
            }
            return new PolyValue.Array(result);
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("delete_items_in", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(0.0);
        }
        try {
            double radius = ((PolyValue)args.get(0)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            AABB aabb = new AABB((double)bp.getX() - radius, (double)bp.getY() - radius, (double)bp.getZ() - radius, (double)(bp.getX() + 1) + radius, (double)(bp.getY() + 1) + radius, (double)(bp.getZ() + 1) + radius);
            List entities = sl.getEntitiesOfClass(ItemEntity.class, aabb);
            for (ItemEntity e : entities) {
                e.discard();
            }
            return PolyValue.of(entities.size());
        }
        catch (Throwable ignored) {
            return PolyValue.of(0.0);
        }
    }).method("collect_xp_in", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(0.0);
        }
        try {
            double radius = ((PolyValue)args.get(0)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            AABB aabb = new AABB((double)bp.getX() - radius, (double)bp.getY() - radius, (double)bp.getZ() - radius, (double)(bp.getX() + 1) + radius, (double)(bp.getY() + 1) + radius, (double)(bp.getZ() + 1) + radius);
            List orbs = sl.getEntitiesOfClass(ExperienceOrb.class, aabb);
            int total = 0;
            for (ExperienceOrb orb : orbs) {
                total += orb.getValue();
                orb.discard();
            }
            return PolyValue.of(total);
        }
        catch (Throwable ignored) {
            return PolyValue.of(0.0);
        }
    }).method("push_entities_in", (mc, args) -> {
        if (args.size() < 5) {
            return PolyValue.of(0.0);
        }
        try {
            double dx = ((PolyValue)args.get(0)).asNum();
            double dy = ((PolyValue)args.get(1)).asNum();
            double dz = ((PolyValue)args.get(2)).asNum();
            double radius = ((PolyValue)args.get(3)).asNum();
            double strength = ((PolyValue)args.get(4)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            AABB aabb = new AABB((double)bp.getX() - radius, (double)bp.getY() - radius, (double)bp.getZ() - radius, (double)(bp.getX() + 1) + radius, (double)(bp.getY() + 1) + radius, (double)(bp.getZ() + 1) + radius);
            List entities = sl.getEntitiesOfClass(Entity.class, aabb);
            for (Entity e : entities) {
                e.push(dx * strength, dy * strength, dz * strength);
            }
            return PolyValue.of(entities.size());
        }
        catch (Throwable ignored) {
            return PolyValue.of(0.0);
        }
    }).method("freeze_blocks_in", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(0.0);
        }
        try {
            int radius = (int)((PolyValue)args.get(0)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            int count = 0;
            for (BlockPos scan : BlockPos.betweenClosed((BlockPos)bp.offset(-radius, -radius, -radius), (BlockPos)bp.offset(radius, radius, radius))) {
                if (!sl.getBlockState(scan).is(Blocks.WATER)) continue;
                sl.setBlock(scan.immutable(), Blocks.ICE.defaultBlockState(), 3);
                ++count;
            }
            return PolyValue.of(count);
        }
        catch (Throwable ignored) {
            return PolyValue.of(0.0);
        }
    }).method("harvest_crops_in", (mc, args) -> {
        if (args.isEmpty()) {
            return new PolyValue.Array(List.of());
        }
        try {
            int radius = (int)((PolyValue)args.get(0)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            ArrayList<PolyValue> allDrops = new ArrayList<PolyValue>();
            for (BlockPos scan : BlockPos.betweenClosed((BlockPos)bp.offset(-radius, -radius, -radius), (BlockPos)bp.offset(radius, radius, radius))) {
                CropBlock crop;
                BlockState state = sl.getBlockState(scan);
                Block patt0$temp = state.getBlock();
                if (!(patt0$temp instanceof CropBlock) || !(crop = (CropBlock)patt0$temp).isMaxAge(state)) continue;
                List drops = Block.getDrops((BlockState)state, (ServerLevel)sl, (BlockPos)scan, null);
                for (ItemStack s : drops) {
                    allDrops.add(new PolyValue.Item(s));
                }
                sl.setBlock(scan.immutable(), crop.getStateForAge(0), 3);
            }
            return new PolyValue.Array(allDrops);
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("breed_animals_in", (mc, args) -> {
        if (args.size() < 2) {
            return PolyValue.of(0.0);
        }
        try {
            double radius = ((PolyValue)args.get(0)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            AABB aabb = new AABB((double)bp.getX() - radius, (double)bp.getY() - radius, (double)bp.getZ() - radius, (double)(bp.getX() + 1) + radius, (double)(bp.getY() + 1) + radius, (double)(bp.getZ() + 1) + radius);
            List animals = sl.getEntitiesOfClass(Animal.class, aabb);
            int count = 0;
            for (Animal a : animals) {
                if (a.getAge() < 0 || a.getInLoveTime() != 0) continue;
                a.setInLove(null);
                ++count;
            }
            return PolyValue.of(count);
        }
        catch (Throwable ignored) {
            return PolyValue.of(0.0);
        }
    }).method("nearby_entities", (mc, args) -> {
        try {
            double radius = args.isEmpty() ? 4.0 : ((PolyValue)args.get(0)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            ServerLevel[] rl = new ServerLevel[]{null};
            BlockPos rb = MachineClass.resolveReal(sl, bp, rl)[0];
            ServerLevel realSl = rl[0];
            AABB aabb = new AABB((double)rb.getX() - radius, (double)rb.getY() - radius, (double)rb.getZ() - radius, (double)(rb.getX() + 1) + radius, (double)(rb.getY() + 1) + radius, (double)(rb.getZ() + 1) + radius);
            List entities = realSl.getEntities((Entity)null, aabb, e -> true);
            ArrayList<PolyValue> result = new ArrayList<PolyValue>();
            for (Entity e2 : entities) {
                result.add(new EntityClass(e2));
            }
            return new PolyValue.Array(result);
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("nearby_animals", (mc, args) -> {
        if (mc.blockEntity == null) {
            return new PolyValue.Array(List.of());
        }
        try {
            double radius = args.isEmpty() ? 5.0 : ((PolyValue)args.get(0)).asNum();
            String typeFilter = args.size() >= 2 ? ((PolyValue)args.get(1)).asStr() : null;
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            AABB aabb = new AABB((double)bp.getX() - radius, (double)bp.getY() - radius, (double)bp.getZ() - radius, (double)(bp.getX() + 1) + radius, (double)(bp.getY() + 1) + radius, (double)(bp.getZ() + 1) + radius);
            List animals = sl.getEntitiesOfClass(Animal.class, aabb);
            ArrayList<PolyValue> result = new ArrayList<PolyValue>();
            for (Animal a : animals) {
                Identifier eid;
                if (typeFilter != null && ((eid = BuiltInRegistries.ENTITY_TYPE.getKey((Object)a.getType())) == null || !eid.toString().equals(typeFilter))) continue;
                result.add(new EntityClass((Entity)a));
            }
            return new PolyValue.Array(result);
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("damage_entity", (mc, args) -> {
        if (args.size() < 2) {
            return PolyValue.of(false);
        }
        try {
            PolyValue.Obj o;
            Object patt0$temp = args.get(0);
            if (!(patt0$temp instanceof PolyValue.Obj) || !((o = (PolyValue.Obj)patt0$temp) instanceof EntityClass)) {
                return PolyValue.of(false);
            }
            EntityClass ec = (EntityClass)o;
            ServerLevel sl = mc.nmsLevel();
            float amount = (float)((PolyValue)args.get(1)).asNum();
            DamageSource src = sl.damageSources().generic();
            boolean hurt = ec.entity.hurtOrSimulate(src, amount);
            return PolyValue.of(hurt);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("fire_entity", (mc, args) -> {
        if (args.size() < 2) {
            return PolyValue.of(false);
        }
        try {
            PolyValue.Obj o;
            Object patt0$temp = args.get(0);
            if (!(patt0$temp instanceof PolyValue.Obj) || !((o = (PolyValue.Obj)patt0$temp) instanceof EntityClass)) {
                return PolyValue.of(false);
            }
            EntityClass ec = (EntityClass)o;
            int ticks = (int)((PolyValue)args.get(1)).asNum();
            ec.entity.setRemainingFireTicks(ticks);
            ServerLevel sl = mc.nmsLevel();
            ec.entity.hurtOrSimulate(sl.damageSources().inFire(), 1.0f);
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("freeze_entity", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(false);
        }
        try {
            PolyValue.Obj o;
            Object patt0$temp = args.get(0);
            if (!(patt0$temp instanceof PolyValue.Obj) || !((o = (PolyValue.Obj)patt0$temp) instanceof EntityClass)) {
                return PolyValue.of(false);
            }
            EntityClass ec = (EntityClass)o;
            int ticks = args.size() >= 2 ? (int)((PolyValue)args.get(1)).asNum() : 100;
            ec.entity.setTicksFrozen(Math.max(ec.entity.getTicksFrozen(), ticks));
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("find_breeding_food", (mc, args) -> {
        if (mc.blockEntity == null || args.isEmpty()) {
            return PolyValue.of(-1.0);
        }
        try {
            String animalType = ((PolyValue)args.get(0)).asStr();
            PersistentWorldlyBlockEntity inv = (PersistentWorldlyBlockEntity)mc.blockEntity;
            List<String> foods = MachineClass.breedingFoodsFor(animalType);
            for (int s = 0; s < inv.getContainerSize(); ++s) {
                Identifier itemId;
                ItemStack stack = inv.getItem(s);
                if (stack.isEmpty() || (itemId = BuiltInRegistries.ITEM.getKey((Object)stack.getItem())) == null || !foods.contains(itemId.toString())) continue;
                return PolyValue.of(s);
            }
            return PolyValue.of(-1.0);
        }
        catch (Throwable ignored) {
            return PolyValue.of(-1.0);
        }
    }).method("use_item_on_entity", (mc, args) -> {
        if (args.size() < 2 || mc.blockEntity == null) {
            return PolyValue.of(false);
        }
        try {
            Entity patt1$temp;
            org.bukkit.entity.Player closest;
            Object patt0$temp = args.get(0);
            if (!(patt0$temp instanceof EntityClass)) {
                return PolyValue.of(false);
            }
            EntityClass ec = (EntityClass)patt0$temp;
            PersistentWorldlyBlockEntity inv = (PersistentWorldlyBlockEntity)mc.blockEntity;
            int slot = (int)((PolyValue)args.get(1)).asNum();
            ItemStack food = inv.getItem(slot);
            if (food.isEmpty()) {
                return PolyValue.of(false);
            }
            ServerPlayer nearestNmsPlayer = null;
            if (mc.world != null && (closest = mc.nearestPlayer(8.0)) != null) {
                nearestNmsPlayer = ((CraftPlayer)closest).getHandle();
            }
            if ((patt1$temp = ec.entity) instanceof Animal) {
                Animal animal = (Animal)patt1$temp;
                animal.setInLove((Player)nearestNmsPlayer);
            } else {
                ec.entity.interactAt((Player)(nearestNmsPlayer != null ? nearestNmsPlayer : ec.entity.level().getNearestPlayer(ec.entity, 16.0)), ec.entity.position(), InteractionHand.MAIN_HAND);
            }
            inv.removeItem(slot, 1);
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("use_item_on_animal", (mc, args) -> {
        if (args.size() < 2 || mc.blockEntity == null) {
            return PolyValue.of(false);
        }
        try {
            org.bukkit.entity.Player closest;
            Object patt0$temp = args.get(0);
            if (!(patt0$temp instanceof EntityClass)) {
                return PolyValue.of(false);
            }
            EntityClass ec = (EntityClass)patt0$temp;
            Entity patt1$temp = ec.entity;
            if (!(patt1$temp instanceof Animal)) {
                return PolyValue.of(false);
            }
            Animal animal = (Animal)patt1$temp;
            PersistentWorldlyBlockEntity inv = (PersistentWorldlyBlockEntity)mc.blockEntity;
            int slot = (int)((PolyValue)args.get(1)).asNum();
            ItemStack food = inv.getItem(slot);
            if (food.isEmpty()) {
                return PolyValue.of(false);
            }
            ServerPlayer nearestNmsPlayer = null;
            if (mc.world != null && (closest = mc.nearestPlayer(8.0)) != null) {
                nearestNmsPlayer = ((CraftPlayer)closest).getHandle();
            }
            animal.setInLove(nearestNmsPlayer);
            inv.removeItem(slot, 1);
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("get_item_in_slot", (mc, args) -> {
        if (args.isEmpty() || mc.blockEntity == null) {
            return PolyValue.NULL;
        }
        try {
            PersistentWorldlyBlockEntity inv = (PersistentWorldlyBlockEntity)mc.blockEntity;
            int slot = (int)((PolyValue)args.get(0)).asNum();
            if (slot < 0 || slot >= inv.getContainerSize()) {
                return PolyValue.NULL;
            }
            ItemStack stack = inv.getItem(slot);
            return stack.isEmpty() ? PolyValue.NULL : new PolyValue.Item(stack);
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).method("add_item", (mc, args) -> {
        PolyValue.Item it;
        if (args.isEmpty() || mc.blockEntity == null) {
            if (DataMachineBlockEntity.SCRIPT_DEBUG) {
                System.out.println("[CEP add_item] FAIL: args empty or blockEntity null");
            }
            return PolyValue.of(0.0);
        }
        PolyValue val = (PolyValue)args.get(0);
        if (!(val instanceof PolyValue.Item) || (it = (PolyValue.Item)val).stack() == null || it.stack().isEmpty()) {
            if (DataMachineBlockEntity.SCRIPT_DEBUG) {
                System.out.println("[CEP add_item] FAIL: not Item or empty. val=" + val.getClass().getSimpleName());
            }
            return PolyValue.of(0.0);
        }
        try {
            int si;
            PersistentWorldlyBlockEntity inv = (PersistentWorldlyBlockEntity)mc.blockEntity;
            ItemStack toAdd = it.stack().copy();
            int original = toAdd.getCount();
            int size = inv.getContainerSize();
            for (si = 0; si < size && !toAdd.isEmpty(); ++si) {
                int space;
                int move;
                ItemStack ex = inv.getItem(si);
                if (ex.isEmpty() || !ItemStack.isSameItemSameComponents((ItemStack)ex, (ItemStack)toAdd) || (move = Math.min(space = ex.getMaxStackSize() - ex.getCount(), toAdd.getCount())) <= 0) continue;
                ex.grow(move);
                inv.setItem(si, ex);
                toAdd.shrink(move);
            }
            for (si = 0; si < size && !toAdd.isEmpty(); ++si) {
                if (!inv.getItem(si).isEmpty()) continue;
                int move = Math.min(toAdd.getMaxStackSize(), toAdd.getCount());
                inv.setItem(si, toAdd.copyWithCount(move));
                toAdd.shrink(move);
            }
            int placed = original - toAdd.getCount();
            if (DataMachineBlockEntity.SCRIPT_DEBUG) {
                System.out.println("[CEP add_item] OK placed=" + placed + " item=" + String.valueOf(it.stack().getItem()));
            }
            return PolyValue.of(placed);
        }
        catch (Throwable t) {
            if (DataMachineBlockEntity.SCRIPT_DEBUG) {
                System.out.println("[CEP add_item] EXCEPTION: " + t.getMessage());
            }
            return PolyValue.of(0.0);
        }
    }).method("add_items", (mc, args) -> {
        List<PolyValue> items;
        if (args.isEmpty() || mc.blockEntity == null) {
            return PolyValue.of(0.0);
        }
        PolyValue val = (PolyValue)args.get(0);
        if (val instanceof PolyValue.Null) {
            return PolyValue.of(0.0);
        }
        if (val instanceof PolyValue.Array) {
            PolyValue.Array a = (PolyValue.Array)val;
            items = a.elements();
        } else if (val instanceof PolyValue.Item) {
            items = List.of(val);
        } else {
            return PolyValue.of(0.0);
        }
        int totalInserted = 0;
        try {
            PersistentWorldlyBlockEntity inv = (PersistentWorldlyBlockEntity)mc.blockEntity;
            int size = inv.getContainerSize();
            for (PolyValue pv : items) {
                int si;
                PolyValue.Item it;
                if (!(pv instanceof PolyValue.Item) || (it = (PolyValue.Item)pv).stack() == null || it.stack().isEmpty()) continue;
                ItemStack toAdd = it.stack().copy();
                int original = toAdd.getCount();
                for (si = 0; si < size && !toAdd.isEmpty(); ++si) {
                    int space;
                    int move;
                    ItemStack ex = inv.getItem(si);
                    if (ex.isEmpty() || !ItemStack.isSameItemSameComponents((ItemStack)ex, (ItemStack)toAdd) || (move = Math.min(space = ex.getMaxStackSize() - ex.getCount(), toAdd.getCount())) <= 0) continue;
                    ex.grow(move);
                    inv.setItem(si, ex);
                    toAdd.shrink(move);
                }
                for (si = 0; si < size && !toAdd.isEmpty(); ++si) {
                    if (!inv.getItem(si).isEmpty()) continue;
                    int move = Math.min(toAdd.getMaxStackSize(), toAdd.getCount());
                    inv.setItem(si, toAdd.copyWithCount(move));
                    toAdd.shrink(move);
                }
                totalInserted += original - toAdd.getCount();
            }
            if (totalInserted > 0) {
                inv.setChanged();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return PolyValue.of(totalInserted);
    }).method("set_item_in_slot", (mc, args) -> {
        if (args.size() < 2 || mc.blockEntity == null) {
            return PolyValue.of(false);
        }
        try {
            PolyValue.Item i;
            PersistentWorldlyBlockEntity inv = (PersistentWorldlyBlockEntity)mc.blockEntity;
            int slot = (int)((PolyValue)args.get(0)).asNum();
            if (slot < 0 || slot >= inv.getContainerSize()) {
                return PolyValue.of(false);
            }
            PolyValue val = (PolyValue)args.get(1);
            ItemStack nms = ItemStack.EMPTY;
            if (val instanceof PolyValue.Item && (i = (PolyValue.Item)val).stack() != null) {
                nms = i.stack().copy();
            }
            inv.setItem(slot, nms);
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("remove_item_in_slot", (mc, args) -> {
        if (args.isEmpty() || mc.blockEntity == null) {
            return PolyValue.of(0.0);
        }
        try {
            int amount;
            PersistentWorldlyBlockEntity inv = (PersistentWorldlyBlockEntity)mc.blockEntity;
            int slot = (int)((PolyValue)args.get(0)).asNum();
            int n = amount = args.size() >= 2 ? (int)((PolyValue)args.get(1)).asNum() : 1;
            if (slot < 0 || slot >= inv.getContainerSize()) {
                return PolyValue.of(0.0);
            }
            ItemStack removed = inv.removeItem(slot, amount);
            return PolyValue.of(removed.getCount());
        }
        catch (Throwable ignored) {
            return PolyValue.of(0.0);
        }
    }).method("give_xp_to_nearest", (mc, args) -> {
        if (args.isEmpty() || mc.world == null) {
            return PolyValue.of(false);
        }
        try {
            int amount = (int)((PolyValue)args.get(0)).asNum();
            if (amount <= 0) {
                return PolyValue.of(false);
            }
            org.bukkit.entity.Player p = mc.nearestPlayer(16.0);
            if (p == null) {
                return PolyValue.of(false);
            }
            ServerLevel sl = mc.nmsLevel();
            BlockPos bp = mc.nmsPos();
            ExperienceOrb.award((ServerLevel)sl, (Vec3)new Vec3((double)bp.getX() + 0.5, (double)bp.getY() + 0.5, (double)bp.getZ() + 0.5), (int)amount);
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("item_attack_damage", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.of(1.0);
        }
        try {
            PolyValue.Item iv;
            Object patt0$temp = args.get(0);
            if (!(patt0$temp instanceof PolyValue.Item) || (iv = (PolyValue.Item)patt0$temp).stack() == null || iv.stack().isEmpty()) {
                return PolyValue.of(1.0);
            }
            double dmg = 1.0;
            ItemAttributeModifiers attrMods = (ItemAttributeModifiers)iv.stack().get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (attrMods != null) {
                for (ItemAttributeModifiers.Entry entry : attrMods.modifiers()) {
                    if (entry.attribute().value() != Attributes.ATTACK_DAMAGE.value()) continue;
                    dmg += entry.modifier().amount();
                }
            }
            return PolyValue.of(dmg);
        }
        catch (Throwable ignored) {
            return PolyValue.of(1.0);
        }
    }).method("owner_uuid", (mc, args) -> {
        PersistentBlockEntity patt0$temp = mc.blockEntity;
        if (patt0$temp instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity mbe = (AbstractMachineBlockEntity)patt0$temp;
            UUID uuid = mbe.getOwnerUuid();
            return uuid != null ? PolyValue.of(uuid.toString()) : PolyValue.NULL;
        }
        return PolyValue.NULL;
    }).method("owner_player", (mc, args) -> {
        PersistentBlockEntity patt0$temp = mc.blockEntity;
        if (patt0$temp instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity mbe = (AbstractMachineBlockEntity)patt0$temp;
            UUID uuid = mbe.getOwnerUuid();
            if (uuid == null || mc.world == null) {
                return PolyValue.NULL;
            }
            org.bukkit.entity.Player owner = mc.world.getPlayers().stream().filter(p -> p.getUniqueId().equals(uuid)).findFirst().orElse(null);
            if (owner == null) {
                return PolyValue.NULL;
            }
            ServerPlayer sp = ((CraftPlayer)owner).getHandle();
            return new PlayerClass(sp);
        }
        return PolyValue.NULL;
    }).method("block_nbt_at", (mc, args) -> {
        if (args.size() < 3 || mc.blockEntity == null) {
            return PolyValue.NULL;
        }
        try {
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos target = mc.nmsPos().offset(dx, dy, dz);
            BlockEntity be = sl.getBlockEntity(target);
            if (be == null) {
                return PolyValue.NULL;
            }
            CompoundTag tag = be.saveWithoutMetadata((HolderLookup.Provider)sl.registryAccess());
            return new NbtClass(tag);
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).method("entity_nbt", (mc, args) -> PolyValue.NULL).method("item_nbt", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.NULL;
        }
        try {
            PolyValue.Item iv;
            Object patt0$temp = args.get(0);
            if (!(patt0$temp instanceof PolyValue.Item) || (iv = (PolyValue.Item)patt0$temp).stack() == null || iv.stack().isEmpty()) {
                return PolyValue.NULL;
            }
            CustomData customData = (CustomData)iv.stack().get(DataComponents.CUSTOM_DATA);
            if (customData == null) {
                return PolyValue.NULL;
            }
            return new NbtClass(customData.copyTag());
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).method("own_nbt", (mc, args) -> {
        if (mc.blockEntity == null) {
            return PolyValue.NULL;
        }
        try {
            PersistentBlockEntity patt0$temp = mc.blockEntity;
            if (patt0$temp instanceof AbstractMachineBlockEntity) {
                AbstractMachineBlockEntity mbe = (AbstractMachineBlockEntity)patt0$temp;
                Level lvl = mbe.getNMSLevel();
                if (lvl == null) {
                    return PolyValue.NULL;
                }
                BlockEntity nmsbe = lvl.getBlockEntity(mbe.getMachinePos());
                if (nmsbe == null) {
                    return PolyValue.NULL;
                }
                CompoundTag tag = nmsbe.saveWithoutMetadata((HolderLookup.Provider)lvl.registryAccess());
                return new NbtClass(tag);
            }
            return PolyValue.NULL;
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).method("delete_item_in_slot", (mc, args) -> {
        if (args.isEmpty() || mc.blockEntity == null) {
            return PolyValue.of(false);
        }
        try {
            PersistentWorldlyBlockEntity inv = (PersistentWorldlyBlockEntity)mc.blockEntity;
            int slot = (int)((PolyValue)args.get(0)).asNum();
            if (slot < 0 || slot >= inv.getContainerSize()) {
                return PolyValue.of(false);
            }
            ItemStack existing = inv.getItem(slot);
            if (existing.isEmpty()) {
                return PolyValue.of(false);
            }
            inv.removeItem(slot, existing.getCount());
            return PolyValue.of(true);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("create_item", (mc, args) -> {
        if (args.isEmpty()) {
            return PolyValue.NULL;
        }
        try {
            String itemId = ((PolyValue)args.get(0)).asStr();
            int count = args.size() >= 2 ? Math.max(1, (int)((PolyValue)args.get(1)).asNum()) : 1;
            Identifier id = Identifier.tryParse((String)itemId);
            if (id == null) {
                return PolyValue.NULL;
            }
            Item item = (Item)BuiltInRegistries.ITEM.getValue(id);
            if (item == null || item == Items.AIR) {
                return PolyValue.NULL;
            }
            return new PolyValue.Item(new ItemStack((ItemLike)item, count));
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).method("cycle_block_facing", (mc, args) -> {
        if (args.size() < 3) {
            return PolyValue.of(false);
        }
        try {
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos target = mc.nmsPos().offset(dx, dy, dz);
            BlockState state = sl.getBlockState(target);
            if (state.hasProperty((Property)BlockStateProperties.FACING)) {
                Direction[] dirs = Direction.values();
                Direction cur = (Direction)state.getValue((Property)BlockStateProperties.FACING);
                Direction next = dirs[(cur.ordinal() + 1) % dirs.length];
                sl.setBlock(target, (BlockState)state.setValue((Property)BlockStateProperties.FACING, (Comparable)next), 3);
                return PolyValue.of(true);
            }
            if (state.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING)) {
                Direction[] horiz = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
                Direction cur = (Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
                int idx = 0;
                for (int i = 0; i < horiz.length; ++i) {
                    if (horiz[i] != cur) continue;
                    idx = i;
                    break;
                }
                Direction next = horiz[(idx + 1) % horiz.length];
                sl.setBlock(target, (BlockState)state.setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)next), 3);
                return PolyValue.of(true);
            }
            return PolyValue.of(false);
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("scan_blocks_in", (mc, args) -> {
        if (mc.blockEntity == null) {
            return new PolyValue.Array(List.of());
        }
        try {
            double radius = args.isEmpty() ? 3.0 : ((PolyValue)args.get(0)).asNum();
            String filter = args.size() >= 2 ? ((PolyValue)args.get(1)).asStr() : null;
            ServerLevel sl = mc.nmsLevel();
            BlockPos base = mc.nmsPos();
            ArrayList<PolyValue> positions = new ArrayList<PolyValue>();
            int r = (int)Math.ceil(radius);
            for (int dx = -r; dx <= r; ++dx) {
                for (int dy = -r; dy <= r; ++dy) {
                    for (int dz = -r; dz <= r; ++dz) {
                        Identifier bid;
                        BlockPos pos;
                        BlockState state;
                        if (dx == 0 && dy == 0 && dz == 0 || (double)(dx * dx + dy * dy + dz * dz) > radius * radius || (state = sl.getBlockState(pos = base.offset(dx, dy, dz))).isAir() || filter != null && ((bid = BuiltInRegistries.BLOCK.getKey((Object)state.getBlock())) == null || !bid.toString().equals(filter))) continue;
                        positions.add(new PolyValue.Array(List.of(PolyValue.of(dx), PolyValue.of(dy), PolyValue.of(dz))));
                    }
                }
            }
            return new PolyValue.Array(positions);
        }
        catch (Throwable ignored) {
            return new PolyValue.Array(List.of());
        }
    }).method("block_state", (mc, args) -> {
        if (args.size() < 4 || mc.blockEntity == null) {
            return PolyValue.NULL;
        }
        try {
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            String propName = ((PolyValue)args.get(3)).asStr();
            ServerLevel sl = mc.nmsLevel();
            BlockPos pos = mc.nmsPos().offset(dx, dy, dz);
            BlockState state = sl.getBlockState(pos);
            for (Property prop : state.getProperties()) {
                if (!prop.getName().equals(propName)) continue;
                Comparable val = state.getValue(prop);
                if (val instanceof Integer) {
                    Integer i = (Integer)val;
                    return PolyValue.of(i.doubleValue());
                }
                if (val instanceof Boolean) {
                    Boolean b = (Boolean)val;
                    return PolyValue.of(b);
                }
                return PolyValue.of(val.toString());
            }
            return PolyValue.NULL;
        }
        catch (Throwable ignored) {
            return PolyValue.NULL;
        }
    }).method("use_item_on_block", (mc, args) -> {
        if (args.size() < 4 || mc.blockEntity == null) {
            return PolyValue.of(false);
        }
        try {
            org.bukkit.entity.Player closest;
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            int slot = (int)((PolyValue)args.get(3)).asNum();
            PersistentWorldlyBlockEntity inv = (PersistentWorldlyBlockEntity)mc.blockEntity;
            ItemStack item = inv.getItem(slot);
            if (item.isEmpty()) {
                return PolyValue.of(false);
            }
            ServerLevel sl = mc.nmsLevel();
            BlockPos pos = mc.nmsPos().offset(dx, dy, dz);
            BlockState state = sl.getBlockState(pos);
            ServerPlayer actingPlayer = null;
            if (mc.world != null && (closest = mc.nearestPlayer(16.0)) != null) {
                actingPlayer = ((CraftPlayer)closest).getHandle();
            }
            if (actingPlayer == null) {
                return PolyValue.of(false);
            }
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf((Vec3i)pos), Direction.UP, pos, false);
            InteractionResult result = state.useItemOn(item, (Level)sl, (Player)actingPlayer, InteractionHand.MAIN_HAND, hit);
            return PolyValue.of(result.consumesAction());
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).method("interact_block", (mc, args) -> {
        if (args.size() < 3 || mc.blockEntity == null) {
            return PolyValue.of(false);
        }
        try {
            org.bukkit.entity.Player closest;
            int dx = (int)((PolyValue)args.get(0)).asNum();
            int dy = (int)((PolyValue)args.get(1)).asNum();
            int dz = (int)((PolyValue)args.get(2)).asNum();
            ServerLevel sl = mc.nmsLevel();
            BlockPos pos = mc.nmsPos().offset(dx, dy, dz);
            BlockState state = sl.getBlockState(pos);
            ServerPlayer actingPlayer = null;
            if (mc.world != null && (closest = mc.nearestPlayer(16.0)) != null) {
                actingPlayer = ((CraftPlayer)closest).getHandle();
            }
            if (actingPlayer == null) {
                return PolyValue.of(false);
            }
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf((Vec3i)pos), Direction.UP, pos, false);
            InteractionResult result = state.useWithoutItem((Level)sl, (Player)actingPlayer, hit);
            return PolyValue.of(result.consumesAction());
        }
        catch (Throwable ignored) {
            return PolyValue.of(false);
        }
    }).build();
    private final double x;
    private final double y;
    private final double z;
    private final String facing;
    private final float facingYaw;
    private final World world;
    private final org.bukkit.entity.Player singlePlayer;
    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private int[][] footprint;
    private PersistentBlockEntity blockEntity;
    private static final String[] OCTANT_NAMES = new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
    private final PolyValue.Obj delegate;

    BlockPos resolveTargetPos(List<PolyValue> args) {
        if (args.isEmpty()) {
            return null;
        }
        if (args.get(0) instanceof PolyValue.Str) {
            Direction dir = this.resolveNamedFace(args.get(0).asStr());
            return dir != null ? this.nmsPos().relative(dir) : null;
        }
        if (args.size() >= 3) {
            int dx = (int)args.get(0).asNum();
            int dy = (int)args.get(1).asNum();
            int dz = (int)args.get(2).asNum();
            return this.nmsPos().offset(dx, dy, dz);
        }
        return null;
    }

    Direction resolveNamedFace(String name) {
        Direction facing = this.currentFacing();
        return switch (name.toLowerCase()) {
            case "front" -> facing;
            case "back" -> facing.getOpposite();
            case "right" -> facing.getClockWise();
            case "left" -> facing.getCounterClockWise();
            case "up" -> Direction.UP;
            case "down" -> Direction.DOWN;
            case "axis_pos" -> this.axisPositiveDir();
            case "axis_neg" -> this.axisNegativeDir();
            default -> Direction.byName((String)name.toLowerCase());
        };
    }

    Direction currentFacing() {
        if (this.facing == null) {
            return Direction.NORTH;
        }
        return switch (this.facing.toLowerCase()) {
            case "south" -> Direction.SOUTH;
            case "east" -> Direction.EAST;
            case "west" -> Direction.WEST;
            case "up" -> Direction.UP;
            case "down" -> Direction.DOWN;
            default -> Direction.NORTH;
        };
    }

    private Direction axisPositiveDir() {
        try {
            net.momirealms.craftengine.core.block.property.Property p;
            BlockState bs = this.nmsLevel().getBlockState(this.nmsPos());
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState((Object)bs).orElse(null);
            if (cs != null && (p = ((BlockDefinition)cs.owner().value()).getProperty("axis")) != null) {
                return switch (String.valueOf(cs.get(p)).toLowerCase()) {
                    case "x" -> Direction.EAST;
                    case "y" -> Direction.UP;
                    default -> Direction.SOUTH;
                };
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return Direction.SOUTH;
    }

    private Direction axisNegativeDir() {
        return this.axisPositiveDir().getOpposite();
    }

    void pushRpmToOffset(int dx, int dy, int dz, float rpm) throws Throwable {
        BlockPos target = this.nmsPos().offset(dx, dy, dz);
        this.pushRpmToPos(target, rpm);
    }

    void pushRpmToFace(Direction dir, float rpm) {
        try {
            this.pushRpmToPos(this.nmsPos().relative(dir), rpm);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void pushRpmToPos(BlockPos target, float rpm) throws Throwable {
        net.momirealms.craftengine.core.block.entity.BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)this.nmsLevel(), target);
        if (be == null) {
            return;
        }
        BlockEntityController blockEntityController = be.controller;
        if (blockEntityController instanceof RpmConsumer) {
            DataMachineBlockEntity dm;
            Direction fromDir;
            RpmConsumer c = (RpmConsumer)blockEntityController;
            int dx = this.nmsPos().getX() - target.getX();
            int dy = this.nmsPos().getY() - target.getY();
            int dz = this.nmsPos().getZ() - target.getZ();
            Direction direction = Math.abs(dx) >= Math.abs(dy) && Math.abs(dx) >= Math.abs(dz) ? (dx > 0 ? Direction.EAST : Direction.WEST) : (Math.abs(dy) >= Math.abs(dz) ? (dy > 0 ? Direction.UP : Direction.DOWN) : (fromDir = dz > 0 ? Direction.SOUTH : Direction.NORTH));
            if (c instanceof DataMachineBlockEntity && (dm = (DataMachineBlockEntity)c).definition() != null) {
                boolean targetAcceptsAxisPerp;
                Set<String> perpFilter;
                DataMachineBlockEntity src;
                ServerLevel sl = this.nmsLevel();
                if (!dm.definition().rpmInputFacesRaw().isEmpty() && !DataMachineBlockEntity.rpmFacesContainWithAxisStatic(dm.definition().rpmInputFacesRaw(), fromDir, dm.getFacingPublic((Level)sl), sl, target)) {
                    return;
                }
                PersistentBlockEntity persistentBlockEntity = this.blockEntity;
                if (persistentBlockEntity instanceof DataMachineBlockEntity && (src = (DataMachineBlockEntity)persistentBlockEntity).definition() != null && !src.definition().rpmOutputBlockFilter().isEmpty() && (perpFilter = src.definition().rpmOutputBlockFilter().get("axis_perp")) != null && !(targetAcceptsAxisPerp = dm.definition().rpmInputFacesRaw().contains("axis_perp"))) {
                    return;
                }
            }
            c.setInputRpm(rpm);
        }
    }

    float rpmFromBlockEntity() {
        try {
            PersistentBlockEntity persistentBlockEntity = this.blockEntity;
            if (persistentBlockEntity instanceof RpmConsumer) {
                RpmConsumer c = (RpmConsumer)((Object)persistentBlockEntity);
                return c.getInputRpm();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return 0.0f;
    }

    public MachineClass(double x, double y, double z, String facing, float facingYaw, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        this.facingYaw = facingYaw;
        this.world = world;
        this.singlePlayer = null;
        this.blockX = (int)Math.floor(x);
        this.blockY = (int)Math.floor(y);
        this.blockZ = (int)Math.floor(z);
        this.delegate = FACTORY.wrap(this);
    }

    public MachineClass(double x, double y, double z, String facing, float facingYaw, org.bukkit.entity.Player singlePlayer) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        this.facingYaw = facingYaw;
        this.world = singlePlayer != null ? singlePlayer.getWorld() : null;
        this.singlePlayer = singlePlayer;
        this.blockX = (int)Math.floor(x);
        this.blockY = (int)Math.floor(y);
        this.blockZ = (int)Math.floor(z);
        this.delegate = FACTORY.wrap(this);
    }

    public MachineClass withFootprint(int[] ... cells) {
        this.footprint = cells;
        return this;
    }

    public MachineClass withBlockEntity(PersistentBlockEntity be) {
        this.blockEntity = be;
        return this;
    }

    @Override
    public PolyValue get(String property) {
        return this.delegate.get(property);
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        return this.delegate.call(method, args);
    }

    private static List<String> breedingFoodsFor(String animalType) {
        List<String> builtIn;
        List<String> extra = PolyFunctionRegistry.getExtraAnimalFoods(animalType);
        switch (animalType) {
            case "minecraft:cow": 
            case "minecraft:mooshroom": {
                List<Object> list = List.of("minecraft:wheat");
                break;
            }
            case "minecraft:sheep": {
                List<Object> list = List.of("minecraft:wheat");
                break;
            }
            case "minecraft:pig": {
                List<Object> list = List.of("minecraft:carrot", "minecraft:potato", "minecraft:beetroot");
                break;
            }
            case "minecraft:chicken": {
                List<Object> list = List.of("minecraft:wheat_seeds", "minecraft:melon_seeds", "minecraft:pumpkin_seeds", "minecraft:beetroot_seeds");
                break;
            }
            case "minecraft:wolf": {
                List<Object> list = List.of("minecraft:beef", "minecraft:cooked_beef", "minecraft:porkchop", "minecraft:cooked_porkchop", "minecraft:chicken", "minecraft:cooked_chicken");
                break;
            }
            case "minecraft:cat": {
                List<Object> list = List.of("minecraft:cod", "minecraft:salmon");
                break;
            }
            case "minecraft:rabbit": {
                List<Object> list = List.of("minecraft:dandelion", "minecraft:carrot", "minecraft:golden_carrot");
                break;
            }
            case "minecraft:horse": 
            case "minecraft:donkey": {
                List<Object> list = List.of("minecraft:golden_apple", "minecraft:golden_carrot");
                break;
            }
            case "minecraft:llama": {
                List<Object> list = List.of("minecraft:hay_block");
                break;
            }
            case "minecraft:turtle": {
                List<Object> list = List.of("minecraft:seagrass");
                break;
            }
            case "minecraft:panda": {
                List<Object> list = List.of("minecraft:bamboo");
                break;
            }
            case "minecraft:fox": {
                List<Object> list = List.of("minecraft:sweet_berries", "minecraft:glow_berries");
                break;
            }
            case "minecraft:bee": {
                List<Object> list = List.of("minecraft:dandelion", "minecraft:poppy", "minecraft:blue_orchid", "minecraft:allium", "minecraft:azure_bluet", "minecraft:red_tulip", "minecraft:orange_tulip", "minecraft:white_tulip", "minecraft:pink_tulip", "minecraft:oxeye_daisy", "minecraft:sunflower", "minecraft:lilac", "minecraft:rose_bush", "minecraft:peony", "minecraft:cornflower", "minecraft:lily_of_the_valley", "minecraft:wither_rose");
                break;
            }
            case "minecraft:goat": {
                List<Object> list = List.of("minecraft:wheat");
                break;
            }
            case "minecraft:frog": {
                List<Object> list = List.of("minecraft:slime_ball");
                break;
            }
            case "minecraft:camel": {
                List<Object> list = List.of("minecraft:cactus");
                break;
            }
            case "minecraft:sniffer": {
                List<Object> list = List.of("minecraft:torchflower_seeds");
                break;
            }
            default: {
                List<Object> list = builtIn = List.of();
            }
        }
        if (extra.isEmpty()) {
            return builtIn;
        }
        ArrayList<String> merged = new ArrayList<String>(builtIn);
        merged.addAll(extra);
        return merged;
    }

    private Collection<? extends org.bukkit.entity.Player> queryPlayers() {
        if (this.singlePlayer != null) {
            return List.of(this.singlePlayer);
        }
        if (this.world == null) {
            return List.of();
        }
        return this.world.getPlayers();
    }

    private boolean anyPlayerFacing(String targetFace, String zone) {
        for (org.bukkit.entity.Player player : this.queryPlayers()) {
            if (!this.playerFacingCheck(player, targetFace, zone)) continue;
            return true;
        }
        return false;
    }

    private boolean playerFacingCheck(org.bukkit.entity.Player p, String targetFace, String zone) {
        double v;
        double u;
        BlockFace face;
        double preZ;
        double preY;
        Location pLoc = p.getLocation();
        double preX = pLoc.getX() - ((double)this.blockX + 0.5);
        if (preX * preX + (preY = pLoc.getY() - ((double)this.blockY + 0.5)) * preY + (preZ = pLoc.getZ() - ((double)this.blockZ + 0.5)) * preZ > 64.0) {
            return false;
        }
        RayTraceResult trace = p.rayTraceBlocks(6.0);
        if (trace == null) {
            return false;
        }
        org.bukkit.block.Block hitBlock = trace.getHitBlock();
        if (hitBlock == null) {
            return false;
        }
        if (hitBlock.getX() != this.blockX || hitBlock.getY() != this.blockY || hitBlock.getZ() != this.blockZ) {
            if (this.footprint == null) {
                return false;
            }
            boolean inFootprint = false;
            for (int[] cell : this.footprint) {
                if (hitBlock.getX() != cell[0] || hitBlock.getY() != cell[1] || hitBlock.getZ() != cell[2]) continue;
                inFootprint = true;
                break;
            }
            if (!inFootprint) {
                return false;
            }
        }
        if ((face = trace.getHitBlockFace()) == null) {
            return false;
        }
        if (!targetFace.equals(face.name().toLowerCase())) {
            return false;
        }
        if (zone == null || zone.isEmpty()) {
            return true;
        }
        Vector hitPos = trace.getHitPosition();
        double fx = MachineClass.clamp01(hitPos.getX() - (double)this.blockX);
        double fy = MachineClass.clamp01(hitPos.getY() - (double)this.blockY);
        double fz = MachineClass.clamp01(hitPos.getZ() - (double)this.blockZ);
        switch (face) {
            case NORTH: {
                u = 1.0 - fx;
                v = fy;
                break;
            }
            case SOUTH: {
                u = fx;
                v = fy;
                break;
            }
            case EAST: {
                u = 1.0 - fz;
                v = fy;
                break;
            }
            case WEST: {
                u = fz;
                v = fy;
                break;
            }
            case UP: {
                u = fx;
                v = 1.0 - fz;
                break;
            }
            case DOWN: {
                u = fx;
                v = fz;
                break;
            }
            default: {
                return true;
            }
        }
        u = MachineClass.clamp01(u);
        v = MachineClass.clamp01(v);
        if (zone.contains(",")) {
            String[] parts = zone.split(",");
            String hitZone = MachineClass.computeFaceZone(u, v, parts[0].trim());
            for (String part : parts) {
                if (!part.trim().equals(hitZone)) continue;
                return true;
            }
            return false;
        }
        if (zone.contains("..")) {
            return MachineClass.matchesRangeZone(u, v, zone);
        }
        return zone.equals(MachineClass.computeFaceZone(u, v, zone));
    }

    private static String computeFaceZone(double u, double v, String target) {
        if (MachineClass.isSixteenthTarget(target)) {
            int col = Math.min(3, (int)(u * 4.0));
            int row = Math.min(3, 3 - (int)(v * 4.0));
            return row + "-" + col;
        }
        if (MachineClass.isOctantTarget(target)) {
            double bearing = Math.toDegrees(Math.atan2(u - 0.5, v - 0.5));
            if (bearing < 0.0) {
                bearing += 360.0;
            }
            int sector = (int)((bearing + 22.5) / 45.0) % 8;
            return OCTANT_NAMES[sector];
        }
        if (MachineClass.isQuarterTarget(target)) {
            return (v >= 0.5 ? "top" : "bottom") + "-" + (u < 0.5 ? "left" : "right");
        }
        return switch (target) {
            case "top", "bottom" -> {
                if (v >= 0.5) {
                    yield "top";
                }
                yield "bottom";
            }
            default -> u < 0.5 ? "left" : "right";
        };
    }

    private static boolean isSixteenthTarget(String t) {
        return t.length() == 3 && t.charAt(0) >= '0' && t.charAt(0) <= '3' && t.charAt(1) == '-' && t.charAt(2) >= '0' && t.charAt(2) <= '3';
    }

    private static boolean isOctantTarget(String t) {
        return switch (t) {
            case "N", "NE", "E", "SE", "S", "SW", "W", "NW" -> true;
            default -> false;
        };
    }

    private static boolean isQuarterTarget(String t) {
        return switch (t) {
            case "top-left", "top-right", "bottom-left", "bottom-right" -> true;
            default -> false;
        };
    }

    private boolean anyPlayerInRange(double dist) {
        double distSq = dist * dist;
        Location center = new Location(this.world, this.x, this.y, this.z);
        for (org.bukkit.entity.Player player : this.queryPlayers()) {
            if (!(player.getLocation().distanceSquared(center) <= distSq)) continue;
            return true;
        }
        return false;
    }

    private boolean anyPlayerInCone(double dx, double dy, double dz, double halfAngleDeg) {
        double cosAngle = Math.cos(Math.toRadians(halfAngleDeg));
        for (org.bukkit.entity.Player player : this.queryPlayers()) {
            double dot;
            double rz;
            double ry;
            Location pLoc = player.getLocation();
            double rx = pLoc.getX() - this.x;
            double dist = Math.sqrt(rx * rx + (ry = pLoc.getY() - this.y) * ry + (rz = pLoc.getZ() - this.z) * rz);
            if (dist < 0.5 || dist > 24.0 || !((dot = (rx * dx + ry * dy + rz * dz) / dist) >= cosAngle)) continue;
            return true;
        }
        return false;
    }

    private static boolean matchesRangeZone(double u, double v, String zone) {
        int sep = zone.lastIndexOf(45);
        if (sep < 0) {
            return false;
        }
        String rowPart = zone.substring(0, sep);
        String colPart = zone.substring(sep + 1);
        int[] rows = MachineClass.parseRangePart(rowPart);
        int[] cols = MachineClass.parseRangePart(colPart);
        if (rows == null || cols == null) {
            return false;
        }
        int col = Math.min(3, (int)(u * 4.0));
        int row = Math.min(3, 3 - (int)(v * 4.0));
        return row >= rows[0] && row <= rows[1] && col >= cols[0] && col <= cols[1];
    }

    private static int[] parseRangePart(String s) {
        int dotdot = s.indexOf("..");
        if (dotdot < 0) {
            try {
                int v = Integer.parseInt(s.trim());
                return new int[]{v, v};
            }
            catch (Exception v) {
                return null;
            }
        }
        try {
            int lo = Integer.parseInt(s.substring(0, dotdot).trim());
            int hi = Integer.parseInt(s.substring(dotdot + 2).trim());
            return new int[]{Math.min(lo, hi), Math.max(lo, hi)};
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private org.bukkit.entity.Player nearestPlayer(double maxDist) {
        double maxDistSq = maxDist * maxDist;
        org.bukkit.entity.Player nearest = null;
        double nearestSq = maxDistSq;
        Location center = new Location(this.world, this.x, this.y, this.z);
        for (org.bukkit.entity.Player player : this.queryPlayers()) {
            double dSq = player.getLocation().distanceSquared(center);
            if (!(dSq <= nearestSq)) continue;
            nearestSq = dSq;
            nearest = player;
        }
        return nearest;
    }

    private double computePlayerYaw(double maxDist) {
        org.bukkit.entity.Player p = this.nearestPlayer(maxDist);
        if (p == null) {
            return 0.0;
        }
        Location loc = p.getLocation();
        return MachineClass.yawTo(this.x, this.z, loc.getX(), loc.getZ());
    }

    private double computePlayerPitch(double maxDist) {
        org.bukkit.entity.Player p = this.nearestPlayer(maxDist);
        if (p == null) {
            return 0.0;
        }
        Location eye = p.getEyeLocation();
        return MachineClass.pitchTo(this.x, this.y, this.z, eye.getX(), eye.getY(), eye.getZ());
    }

    static double yawTo(double fromX, double fromZ, double toX, double toZ) {
        return Math.toDegrees(Math.atan2(-(toX - fromX), toZ - fromZ));
    }

    static double pitchTo(double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
        double dx = toX - fromX;
        double dy = toY - fromY;
        double dz = toZ - fromZ;
        double hDist = Math.sqrt(dx * dx + dz * dz);
        return Math.toDegrees(-Math.atan2(dy, hDist));
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(0.999999999, v));
    }

    private ServerLevel nmsLevel() {
        PersistentBlockEntity persistentBlockEntity = this.blockEntity;
        if (persistentBlockEntity instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity mbe = (AbstractMachineBlockEntity)persistentBlockEntity;
            return (ServerLevel)mbe.getNMSLevel();
        }
        persistentBlockEntity = this.blockEntity;
        if (persistentBlockEntity instanceof PersistentWorldlyBlockEntity) {
            PersistentWorldlyBlockEntity wbe = (PersistentWorldlyBlockEntity)persistentBlockEntity;
            return (ServerLevel)wbe.getNMSLevel();
        }
        if (this.world != null) {
            return ((CraftWorld)this.world).getHandle();
        }
        throw new IllegalStateException("MachineClass: no ServerLevel available");
    }

    private BlockPos nmsPos() {
        PersistentBlockEntity persistentBlockEntity = this.blockEntity;
        if (persistentBlockEntity instanceof AbstractMachineBlockEntity) {
            AbstractMachineBlockEntity mbe = (AbstractMachineBlockEntity)persistentBlockEntity;
            return mbe.getMachinePos();
        }
        return new BlockPos(this.blockX, this.blockY, this.blockZ);
    }

    private int[] facingOffset() {
        int[] nArray;
        if (this.facing == null) {
            return new int[]{0, 0, -1};
        }
        switch (this.facing.toLowerCase()) {
            case "north": {
                int[] nArray2 = new int[3];
                nArray2[0] = 0;
                nArray2[1] = 0;
                nArray = nArray2;
                nArray2[2] = -1;
                break;
            }
            case "south": {
                int[] nArray3 = new int[3];
                nArray3[0] = 0;
                nArray3[1] = 0;
                nArray = nArray3;
                nArray3[2] = 1;
                break;
            }
            case "east": {
                int[] nArray4 = new int[3];
                nArray4[0] = 1;
                nArray4[1] = 0;
                nArray = nArray4;
                nArray4[2] = 0;
                break;
            }
            case "west": {
                int[] nArray5 = new int[3];
                nArray5[0] = -1;
                nArray5[1] = 0;
                nArray = nArray5;
                nArray5[2] = 0;
                break;
            }
            case "up": {
                int[] nArray6 = new int[3];
                nArray6[0] = 0;
                nArray6[1] = 1;
                nArray = nArray6;
                nArray6[2] = 0;
                break;
            }
            case "down": {
                int[] nArray7 = new int[3];
                nArray7[0] = 0;
                nArray7[1] = -1;
                nArray = nArray7;
                nArray7[2] = 0;
                break;
            }
            default: {
                int[] nArray8 = new int[3];
                nArray8[0] = 0;
                nArray8[1] = 0;
                nArray = nArray8;
                nArray8[2] = -1;
            }
        }
        return nArray;
    }

    public static String getBlockIdStatic(ServerLevel level, BlockPos pos) {
        return MachineClass.getBlockId(level, pos);
    }

    public static boolean passesBlockFilterStatic(Map<String, Set<String>> filterMap, Direction face, Direction facing, ServerLevel level, BlockPos selfPos, String targetBlockId) {
        return MachineClass.passesBlockFilter(filterMap, face, facing, level, selfPos, targetBlockId);
    }

    private static String getBlockId(ServerLevel level, BlockPos pos) {
        try {
            BlockState bs = level.getBlockState(pos);
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState((Object)bs).orElse(null);
            if (cs != null) {
                return ((BlockDefinition)cs.owner().value()).id().toString();
            }
            return BuiltInRegistries.BLOCK.getKey((Object)bs.getBlock()).toString();
        }
        catch (Throwable ignored) {
            return "";
        }
    }

    private static boolean passesBlockFilter(Map<String, Set<String>> filterMap, Direction face, Direction facing, ServerLevel level, BlockPos selfPos, String targetBlockId) {
        ArrayList<String> groups = new ArrayList<String>();
        groups.add(face.getName());
        try {
            net.momirealms.craftengine.core.block.property.Property axisProp;
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState((Object)level.getBlockState(selfPos)).orElse(null);
            if (cs != null && (axisProp = ((BlockDefinition)cs.owner().value()).getProperty("axis")) != null) {
                Direction posDir;
                String axis;
                switch (axis = String.valueOf(cs.get(axisProp)).toLowerCase()) {
                    case "x": {
                        Direction direction = Direction.EAST;
                        break;
                    }
                    case "y": {
                        Direction direction = Direction.UP;
                        break;
                    }
                    default: {
                        Direction direction = posDir = Direction.SOUTH;
                    }
                }
                if (face == posDir) {
                    groups.add("axis_pos");
                } else if (face == posDir.getOpposite()) {
                    groups.add("axis_neg");
                } else {
                    groups.add("axis_perp");
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        for (String group : groups) {
            Set<String> allowed = filterMap.get(group);
            if (allowed == null) continue;
            return allowed.contains(targetBlockId);
        }
        return true;
    }

    private int getIntFlag(String name) {
        if (this.blockEntity == null) {
            return 0;
        }
        try {
            TypedKey tk = TypedKey.of("polyfills", "flag_" + name, NbtType.INTEGER);
            Integer v = (Integer)this.blockEntity.get(tk);
            return v != null ? v : 0;
        }
        catch (Throwable ignored) {
            return 0;
        }
    }

    private void setIntFlag(String name, int value) {
        if (this.blockEntity == null) {
            return;
        }
        try {
            TypedKey tk = TypedKey.of("polyfills", "flag_" + name, NbtType.INTEGER);
            this.blockEntity.set(tk, value);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void setBreakState(int x, int y, int z, double progress) {
        this.setIntFlag("_bk_x", x);
        this.setIntFlag("_bk_y", y);
        this.setIntFlag("_bk_z", z);
        this.setIntFlag("_bk_prog", (int)progress);
    }

    private void clearBreakState() {
        this.setBreakState(0, 0, 0, 0.0);
    }

    static BlockPos[] resolveReal(ServerLevel level, BlockPos pos, ServerLevel[] outLevel) {
        ContraptionLevel cl;
        Level rl;
        if (level instanceof ContraptionLevel && (rl = (cl = (ContraptionLevel)level).realLevel()) instanceof ServerLevel) {
            ServerLevel rsl;
            outLevel[0] = rsl = (ServerLevel)rl;
            Vec3 rv = cl.realWorldPositionOf(pos);
            return new BlockPos[]{new BlockPos((int)Math.floor(rv.x), (int)Math.floor(rv.y), (int)Math.floor(rv.z))};
        }
        outLevel[0] = level;
        return new BlockPos[]{pos};
    }

    private static void sendBreakAnimation(ServerLevel level, BlockPos pos, int stage) {
        int netId = Objects.hash(pos.getX(), pos.getY(), pos.getZ());
        try {
            ClientboundBlockDestructionPacket pkt = new ClientboundBlockDestructionPacket(netId, pos, stage);
            for (ServerPlayer sp : level.players()) {
                if (!(sp.distanceToSqr((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) < 1024.0)) continue;
                sp.connection.send((Packet)pkt);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void clearBreakAnimation(ServerLevel level, BlockPos pos) {
        MachineClass.sendBreakAnimation(level, pos, -1);
    }

    static <T extends Comparable<T>> BlockState cycleProp(BlockState state, Property<T> prop) {
        Comparable current = state.getValue(prop);
        Object[] vals = prop.getPossibleValues().toArray();
        for (int i = 0; i < vals.length; ++i) {
            if (!vals[i].equals(current)) continue;
            Comparable next = (Comparable)vals[(i + 1) % vals.length];
            return (BlockState)state.setValue(prop, next);
        }
        return null;
    }

    static <T extends Comparable<T>> BlockState applyProp(BlockState state, Property<T> prop, String valueStr) {
        return prop.getValue(valueStr).map(v -> (BlockState)state.setValue(prop, v)).orElse(null);
    }

    static ImmutableBlockState applyCeProp(ImmutableBlockState state, net.momirealms.craftengine.core.block.property.Property<?> prop, String valueStr) {
        try {
            Enum e;
            Object matched;
            if (prop instanceof BooleanProperty) {
                BooleanProperty bp = (BooleanProperty)prop;
                return state.with((net.momirealms.craftengine.core.block.property.Property)bp, (Comparable)Boolean.valueOf(Boolean.parseBoolean(valueStr)));
            }
            if (prop instanceof IntegerProperty) {
                IntegerProperty ip = (IntegerProperty)prop;
                return state.with((net.momirealms.craftengine.core.block.property.Property)ip, (Comparable)Integer.valueOf(Integer.parseInt(valueStr)));
            }
            Comparable cur = state.get(prop);
            if (cur instanceof Enum && (matched = Arrays.stream((Enum[])(e = (Enum)((Object)cur)).getClass().getEnumConstants()).filter(c -> c.name().equalsIgnoreCase(valueStr) || c.toString().equalsIgnoreCase(valueStr)).findFirst().orElse(null)) != null) {
                for (Method m : state.getClass().getMethods()) {
                    Object result;
                    if (!m.getName().equals("with") || m.getParameterCount() != 2 || !((result = m.invoke((Object)state, prop, matched)) instanceof ImmutableBlockState)) continue;
                    ImmutableBlockState r = (ImmutableBlockState)result;
                    return r;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }
}

