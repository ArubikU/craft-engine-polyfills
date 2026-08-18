/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.vehicle.VehicleDamageEvent
 *  org.bukkit.event.vehicle.VehicleDestroyEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataContainer
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 */
package dev.arubik.craftengine.contraption.bearing;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.assembly.ContraptionCapture;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurnitureCapture;
import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import dev.arubik.craftengine.contraption.persistence.ContraptionStorage;
import dev.arubik.craftengine.contraption.player.CePlayers;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class MinecartBearing {
    private static final NamespacedKey IS_BEARING = MinecartBearing.key("contraption_minecart_bearing");
    private static final NamespacedKey ASSEMBLED = MinecartBearing.key("contraption_assembled");
    private static final NamespacedKey CONTRAPTION_ID = MinecartBearing.key("contraption_id");
    private static final NamespacedKey STRUCTURE = MinecartBearing.key("contraption_structure");
    private static final NamespacedKey YAW = MinecartBearing.key("contraption_yaw");
    private static final NamespacedKey EXPLOSION_PROOF = MinecartBearing.key("contraption_explosion_proof");
    private static final NamespacedKey ITEM_MARKER = MinecartBearing.key("contraption_minecart_item");

    private MinecartBearing() {
    }

    private static NamespacedKey key(String name) {
        return new NamespacedKey((Plugin)CraftEnginePolyfills.instance(), name);
    }

    public static boolean isBearing(Entity entity) {
        return entity.getPersistentDataContainer().has(IS_BEARING, PersistentDataType.BYTE);
    }

    public static boolean isAssembled(Entity entity) {
        Byte v = (Byte)entity.getPersistentDataContainer().get(ASSEMBLED, PersistentDataType.BYTE);
        return v != null && v != 0;
    }

    public static UUID contraptionId(Entity entity) {
        String id = (String)entity.getPersistentDataContainer().get(CONTRAPTION_ID, PersistentDataType.STRING);
        return id == null ? null : UUID.fromString(id);
    }

    public static ContraptionEntity assemble(World bukkitWorld, BlockPos bearingPos) {
        ServerLevel realLevel = ((CraftWorld)bukkitWorld).getHandle();
        BlockState below = realLevel.getBlockState(bearingPos.below());
        if (!(below.getBlock() instanceof BaseRailBlock)) {
            return null;
        }
        Set<BlockPos> structure = GlueRegistry.structureAt((ResourceKey<Level>)realLevel.dimension(), bearingPos);
        double explosionProof = BearingBlockBehavior.explosionProofAt((Level)realLevel, bearingPos);
        if (ContraptionAssembler.fireAssembleCancelled(bukkitWorld, bearingPos, Key.of((String)"polyfills", (String)"minecart"), structure, null)) {
            return null;
        }
        ContraptionCapture.Result captured = ContraptionCapture.capture((Level)realLevel, structure, bearingPos);
        ContraptionCapture.captureGlueEdges((ResourceKey<Level>)realLevel.dimension(), captured.level(), bearingPos);
        ContraptionFurnitureCapture.Result furnitureResult = ContraptionFurnitureCapture.captureNear((Level)realLevel, structure, bearingPos, captured.level());
        ContraptionCapture.removeFromWorld((Level)realLevel, structure);
        Location spawnAt = new Location(bukkitWorld, (double)bearingPos.getX() + 0.5, (double)bearingPos.getY(), (double)bearingPos.getZ() + 0.5);
        Entity minecart = bukkitWorld.spawnEntity(spawnAt, EntityType.MINECART);
        minecart.setInvulnerable(true);
        UUID id = UUID.randomUUID();
        ContraptionState state = new ContraptionState(id, (ResourceKey<Level>)((CraftWorld)bukkitWorld).getHandle().dimension(), captured.level(), bearingPos.getX(), bearingPos.getY(), bearingPos.getZ());
        state.setBearingType(Key.of((String)"polyfills", (String)"minecart"));
        state.setExplosionProof(explosionProof);
        state.setFurniture(furnitureResult.furniture());
        for (Map.Entry<UUID, Vec3> e : furnitureResult.seatedRiders().entrySet()) {
            state.addSeatedRider(e.getKey(), e.getValue());
            UUID mountId = furnitureResult.seatedRiderMounts().get(e.getKey());
            if (mountId == null) continue;
            state.setSeatedRiderMount(e.getKey(), mountId);
        }
        for (MovementBehavior autoBehavior : captured.autoBehaviors()) {
            state.addBehavior(autoBehavior);
        }
        state.addBehavior(new MinecartFollowBehavior(minecart.getUniqueId()));
        MinecartBearing.tag(minecart, id, true);
        MinecartBearing.saveStructure(minecart, state);
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        ContraptionAssembler.fireAssembled(entity);
        return entity;
    }

    public static void disassemble(World bukkitWorld, Entity minecart, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        if (ContraptionAssembler.fireDisassembleCancelled(entity)) {
            return;
        }
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());
        ServerLevel realLevel = ((CraftWorld)bukkitWorld).getHandle();
        BlockPos snapped = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0.0, state.yawRadians());
        ContraptionCapture.restoreGlue((ResourceKey<Level>)realLevel.dimension(), state.level(), state.originBearingBlockPos(), snapped, quarterTurns);
        ContraptionCapture.restoreRotated((Level)realLevel, state.level(), snapped, quarterTurns);
        HashSet<BlockPos> restingPositions = new HashSet<BlockPos>();
        if (state.level() != null) {
            for (BlockPos local : state.level().localPositions()) {
                restingPositions.add(ContraptionMath.toWorld(ContraptionCapture.rotateLocal(local, quarterTurns), snapped));
            }
        }
        ContraptionFurnitureCapture.restoreFurniture(bukkitWorld, state.furniture(), snapped, quarterTurns);
        if (state.level() != null) {
            state.level().dispose();
        }
        minecart.remove();
        ContraptionAssembler.fireDisassembled(state.id(), bukkitWorld, snapped, restingPositions, quarterTurns);
    }

    public static void disassembleInPlace(World bukkitWorld, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());
        ServerLevel realLevel = ((CraftWorld)bukkitWorld).getHandle();
        BlockPos snapped = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0.0, state.yawRadians());
        ContraptionCapture.restoreGlue((ResourceKey<Level>)realLevel.dimension(), state.level(), state.originBearingBlockPos(), snapped, quarterTurns);
        ContraptionCapture.restoreRotated((Level)realLevel, state.level(), snapped, quarterTurns);
        HashSet<BlockPos> restingPositions = new HashSet<BlockPos>();
        if (state.level() != null) {
            for (BlockPos local : state.level().localPositions()) {
                restingPositions.add(ContraptionMath.toWorld(ContraptionCapture.rotateLocal(local, quarterTurns), snapped));
            }
        }
        ContraptionFurnitureCapture.restoreFurniture(bukkitWorld, state.furniture(), snapped, quarterTurns);
        if (state.level() != null) {
            state.level().dispose();
        }
        try {
            for (MovementBehavior b : state.behaviors()) {
                if (!(b instanceof MinecartFollowBehavior)) continue;
                MinecartFollowBehavior follow = (MinecartFollowBehavior)b;
                Entity cart = Bukkit.getEntity((UUID)follow.entityId());
                if (cart != null) {
                    cart.remove();
                }
                break;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        ContraptionAssembler.fireDisassembled(state.id(), bukkitWorld, snapped, restingPositions, quarterTurns);
    }

    private static void tag(Entity minecart, UUID contraptionId, boolean assembled) {
        PersistentDataContainer pdc = minecart.getPersistentDataContainer();
        pdc.set(IS_BEARING, PersistentDataType.BYTE, (byte)1);
        pdc.set(ASSEMBLED, PersistentDataType.BYTE, ((byte)(assembled ? 1 : 0)));
        pdc.set(CONTRAPTION_ID, PersistentDataType.STRING, contraptionId.toString());
    }

    public static void saveStructure(Entity minecart, ContraptionState state) {
        if (state.level() == null) {
            return;
        }
        try {
            byte[] bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
            minecart.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, bytes);
            minecart.getPersistentDataContainer().set(YAW, PersistentDataType.DOUBLE, state.yawRadians());
            minecart.getPersistentDataContainer().set(EXPLOSION_PROOF, PersistentDataType.DOUBLE, state.explosionProof());
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to save minecart bearing structure: " + String.valueOf(e));
        }
    }

    public static void rehydrate(Entity minecart) {
        Double savedYaw;
        if (!MinecartBearing.isBearing(minecart) || !MinecartBearing.isAssembled(minecart)) {
            return;
        }
        minecart.setInvulnerable(true);
        UUID id = MinecartBearing.contraptionId(minecart);
        byte[] bytes = (byte[])minecart.getPersistentDataContainer().get(STRUCTURE, PersistentDataType.BYTE_ARRAY);
        if (id == null || bytes == null || ContraptionManager.get(id) != null) {
            return;
        }
        World bukkitWorld = minecart.getWorld();
        ServerLevel realLevel = ((CraftWorld)bukkitWorld).getHandle();
        Location loc = minecart.getLocation();
        ContraptionLevel level = ContraptionLevel.create((Level)realLevel, loc.getX(), loc.getY(), loc.getZ(), 0.0);
        try {
            ContraptionStorage.loadLevel(level, ContraptionStorage.fromBytes(bytes));
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to load minecart bearing structure: " + String.valueOf(e));
            return;
        }
        ContraptionState state = new ContraptionState(id, (ResourceKey<Level>)((CraftWorld)bukkitWorld).getHandle().dimension(), level, loc.getX(), loc.getY(), loc.getZ());
        state.setBearingType(Key.of((String)"polyfills", (String)"minecart"));
        state.setScale(level.realScaleFactor());
        state.setFurniture(ContraptionFurnitureCapture.restoreIntoFakeLevel(level, level.furnitureRecords()));
        for (MovementBehavior autoBehavior : ContraptionCapture.resolveAutoBehaviors(level)) {
            state.addBehavior(autoBehavior);
        }
        Double savedProof = (Double)minecart.getPersistentDataContainer().get(EXPLOSION_PROOF, PersistentDataType.DOUBLE);
        if (savedProof != null) {
            state.setExplosionProof(savedProof);
        }
        if ((savedYaw = (Double)minecart.getPersistentDataContainer().get(YAW, PersistentDataType.DOUBLE)) != null) {
            state.setYawRadians(savedYaw);
        }
        state.addBehavior(new MinecartFollowBehavior(minecart.getUniqueId()));
        ContraptionManager.register(new ContraptionEntity(state));
    }

    public static boolean isContraptionItem(ItemStack item) {
        if (item == null || item.getType() != Material.CHEST_MINECART || !item.hasItemMeta()) {
            return false;
        }
        Byte v = (Byte)item.getItemMeta().getPersistentDataContainer().get(ITEM_MARKER, PersistentDataType.BYTE);
        return v != null && v != 0;
    }

    private static byte[] structureBytesOf(ItemStack item) {
        if (!MinecartBearing.isContraptionItem(item)) {
            return null;
        }
        return (byte[])item.getItemMeta().getPersistentDataContainer().get(STRUCTURE, PersistentDataType.BYTE_ARRAY);
    }

    private static ItemStack buildItem(byte[] structureBytes) {
        ItemStack item = new ItemStack(Material.CHEST_MINECART);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(ITEM_MARKER, PersistentDataType.BYTE, (byte)1);
        meta.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, structureBytes);
        meta.setDisplayName("\u00a7bPacked Contraption Minecart");
        item.setItemMeta(meta);
        return item;
    }

    @Deprecated
    public static ItemStack pickUpToItem(World bukkitWorld, Entity minecart, ContraptionEntity entity) {
        byte[] bytes;
        ContraptionState state = entity.state();
        if (state.level() == null) {
            return null;
        }
        try {
            bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to pack minecart contraption to item: " + String.valueOf(e));
            return null;
        }
        ItemStack item = MinecartBearing.buildItem(bytes);
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());
        state.level().dispose();
        minecart.remove();
        return item;
    }

    public static ContraptionEntity placeFromItem(ItemStack item, World bukkitWorld, Location at) {
        BlockPos railPos;
        byte[] bytes = MinecartBearing.structureBytesOf(item);
        if (bytes == null) {
            return null;
        }
        ServerLevel realLevel = ((CraftWorld)bukkitWorld).getHandle();
        if (!(realLevel.getBlockState(railPos = new BlockPos(at.getBlockX(), at.getBlockY(), at.getBlockZ())).getBlock() instanceof BaseRailBlock)) {
            return null;
        }
        Entity minecart = bukkitWorld.spawnEntity(at, EntityType.MINECART);
        minecart.setInvulnerable(true);
        UUID id = UUID.randomUUID();
        MinecartBearing.tag(minecart, id, true);
        minecart.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, bytes);
        MinecartBearing.rehydrate(minecart);
        ContraptionEntity entity = ContraptionManager.get(id);
        if (entity == null) {
            minecart.remove();
        }
        return entity;
    }

    public static final class DamageGuard
    implements Listener {
        @EventHandler(ignoreCancelled=true)
        public void onVehicleDamage(VehicleDamageEvent event) {
            if (MinecartBearing.isBearing((Entity)event.getVehicle())) {
                event.setCancelled(true);
            }
        }

        @EventHandler(ignoreCancelled=true)
        public void onVehicleDestroy(VehicleDestroyEvent event) {
            if (MinecartBearing.isBearing((Entity)event.getVehicle())) {
                event.setCancelled(true);
            }
        }
    }
}

