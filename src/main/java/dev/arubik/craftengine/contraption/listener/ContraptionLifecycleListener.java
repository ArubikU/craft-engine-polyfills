/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.event.entity.EntityEquipmentChangedEvent
 *  io.papermc.paper.event.entity.EntityEquipmentChangedEvent$EquipmentChange
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.craftbukkit.entity.CraftLivingEntity
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HappyGhast
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.player.PlayerInteractAtEntityEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.vehicle.VehicleDamageEvent
 *  org.bukkit.event.vehicle.VehicleDestroyEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.contraption.listener;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.api.ContraptionTypeRegistry;
import dev.arubik.craftengine.contraption.bearing.GhastHarness;
import dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.type.GhastContraptionType;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ContraptionLifecycleListener
implements Listener {
    private final Map<UUID, Integer> armed = new HashMap<UUID, Integer>();
    private static final int ARM_WINDOW_TICKS = 1;

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlaceItem(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy((ItemStack)item);
        ContraptionType matchedType = null;
        for (Key typeKey : ContraptionTypeRegistry.getRegisteredTypes()) {
            ContraptionType type = ContraptionTypeRegistry.get(typeKey);
            if (type == null || !type.isPackedItem(nmsItem)) continue;
            matchedType = type;
            break;
        }
        if (matchedType == null) {
            return;
        }
        event.setCancelled(true);
        ServerLevel level = ((CraftWorld)clicked.getWorld()).getHandle();
        BlockPos pos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());
        ContraptionEntity entity = matchedType.fromItem(nmsItem, (Level)level, pos);
        if (entity == null) {
            event.getPlayer().sendMessage("\u00a7cCouldn't place contraption here.");
            return;
        }
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }
        clicked.getWorld().playSound(clicked.getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1.0f);
        event.getPlayer().sendMessage("\u00a7bContraption restored.");
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEquipHarness(EntityEquipmentChangedEvent event) {
        org.bukkit.entity.LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof HappyGhast)) {
            return;
        }
        HappyGhast ghast = (HappyGhast)livingEntity;
        EntityEquipmentChangedEvent.EquipmentChange change = (EntityEquipmentChangedEvent.EquipmentChange)event.getEquipmentChanges().get(EquipmentSlot.BODY);
        if (change == null) {
            return;
        }
        if (!GhastHarness.isHarness(change.oldItem().getType()) && GhastHarness.isHarness(change.newItem().getType())) {
            BlockPos ghastPos;
            ItemStack harness = change.newItem();
            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy((ItemStack)harness);
            ContraptionType ghastType = ContraptionTypeRegistry.get(Key.of((String)"polyfills", (String)"ghast"));
            if (ghastType == null || !ghastType.isPackedItem(nmsItem)) {
                return;
            }
            if (GhastContraptionType.isBearing((Entity)ghast) && GhastContraptionType.isAssembled((Entity)ghast)) {
                return;
            }
            LivingEntity nmsGhast = ((CraftLivingEntity)ghast).getHandle();
            Level level = nmsGhast.level();
            ContraptionEntity restored = ghastType.fromItem(nmsItem, level, ghastPos = nmsGhast.blockPosition());
            if (restored == null) {
                return;
            }
            restored.state().setAnchorEntityId(ghast.getUniqueId());
            Vec3 anchorOffset = restored.state().anchorOffset();
            OptionalDouble yawOffset = restored.state().yawOffset();
            restored.state().addBehavior(new GhastFollowBehavior(ghast.getUniqueId(), anchorOffset, yawOffset));
            restored.state().setScale(nmsGhast.getScale());
            net.minecraft.world.item.ItemStack clearedHarness = nmsItem.copy();
            clearedHarness.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            ghast.getEquipment().setItem(EquipmentSlot.BODY, CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)clearedHarness), true);
            ghast.getWorld().playSound(ghast.getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1.2f);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        this.onInteractEntity((PlayerInteractEntityEvent)e);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (!(entity instanceof HappyGhast)) {
            return;
        }
        HappyGhast ghast = (HappyGhast)entity;
        Player player = e.getPlayer();
        ItemStack hand = e.getHand() == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
        this.preStampForShear(ghast, player, hand);
    }

    private void preStampForShear(HappyGhast ghast, Player player, ItemStack hand) {
        ContraptionEntity entity;
        if (!GhastContraptionType.isBearing((Entity)ghast) || !GhastContraptionType.isAssembled((Entity)ghast)) {
            return;
        }
        if (hand == null || hand.getType() != Material.SHEARS || player.isSneaking()) {
            return;
        }
        UUID contraptionId = GhastContraptionType.contraptionId((Entity)ghast);
        ContraptionEntity contraptionEntity = entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
        if (entity == null) {
            return;
        }
        ItemStack harness = ghast.getEquipment().getItem(EquipmentSlot.BODY);
        if (!GhastHarness.isHarness(harness.getType())) {
            return;
        }
        Key type = entity.state().bearingType();
        if (type == null) {
            return;
        }
        ContraptionType typeImpl = ContraptionTypeRegistry.get(type);
        if (typeImpl == null || !typeImpl.canPackToItem()) {
            return;
        }
        ServerLevel level = ((CraftWorld)ghast.getWorld()).getHandle();
        net.minecraft.world.item.ItemStack nmsItem = typeImpl.toItem(entity, (Level)level);
        if (nmsItem == null) {
            return;
        }
        ItemStack packed = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nmsItem);
        ghast.getEquipment().setItem(EquipmentSlot.BODY, packed, true);
        int now = Bukkit.getCurrentTick();
        this.armed.values().removeIf(armedAt -> now - armedAt > 1);
        this.armed.put(ghast.getUniqueId(), now);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEquipmentChanged(EntityEquipmentChangedEvent e) {
        org.bukkit.entity.LivingEntity livingEntity = e.getEntity();
        if (!(livingEntity instanceof HappyGhast)) {
            return;
        }
        HappyGhast ghast = (HappyGhast)livingEntity;
        EntityEquipmentChangedEvent.EquipmentChange change = (EntityEquipmentChangedEvent.EquipmentChange)e.getEquipmentChanges().get(EquipmentSlot.BODY);
        if (change == null) {
            return;
        }
        if (GhastHarness.isHarness(change.oldItem().getType()) && !GhastHarness.isHarness(change.newItem().getType())) {
            this.armed.remove(ghast.getUniqueId());
            this.onUnequip(ghast);
        }
    }

    private void onUnequip(HappyGhast ghast) {
        boolean carriedOutOnItem;
        UUID contraptionId = GhastContraptionType.contraptionId((Entity)ghast);
        ContraptionEntity entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
        Integer armedAt = this.armed.remove(ghast.getUniqueId());
        if (entity == null) {
            return;
        }
        boolean bl = carriedOutOnItem = armedAt != null && Bukkit.getCurrentTick() - armedAt <= 1;
        if (carriedOutOnItem) {
            GhastContraptionType.tearDownIntoItem(ghast.getWorld(), (Entity)ghast, entity);
        } else {
            GhastContraptionType.disassembleInPlace(ghast.getWorld(), (Entity)ghast, entity);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onVehicleDamage(VehicleDamageEvent event) {
        Vehicle vehicle = event.getVehicle();
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity)vehicle).getHandle();
        ContraptionEntity contraption = this.findByAnchor(nmsEntity.getUUID());
        if (contraption == null) {
            return;
        }
        Key type = contraption.state().bearingType();
        if (type == null) {
            return;
        }
        ContraptionType typeImpl = ContraptionTypeRegistry.get(type);
        if (typeImpl == null) {
            return;
        }
        if (typeImpl.onAnchorDamage(nmsEntity, nmsEntity.level())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Vehicle vehicle = event.getVehicle();
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity)vehicle).getHandle();
        ContraptionEntity contraption = this.findByAnchor(nmsEntity.getUUID());
        if (contraption == null) {
            return;
        }
        Key type = contraption.state().bearingType();
        if (type == null) {
            return;
        }
        ContraptionType typeImpl = ContraptionTypeRegistry.get(type);
        if (typeImpl == null) {
            return;
        }
        if (typeImpl.onAnchorDamage(nmsEntity, nmsEntity.level())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        org.bukkit.entity.LivingEntity dead = event.getEntity();
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity)dead).getHandle();
        ContraptionEntity contraption = this.findByAnchor(nmsEntity.getUUID());
        if (contraption == null) {
            return;
        }
        Key type = contraption.state().bearingType();
        if (type == null) {
            return;
        }
        ContraptionType typeImpl = ContraptionTypeRegistry.get(type);
        if (typeImpl == null) {
            return;
        }
        Level level = nmsEntity.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        ArrayList<net.minecraft.world.item.ItemStack> nmsDrops = new ArrayList<net.minecraft.world.item.ItemStack>();
        for (ItemStack bukkitDrop : event.getDrops()) {
            nmsDrops.add(CraftItemStack.asNMSCopy((ItemStack)bukkitDrop));
        }
        typeImpl.onAnchorDeath(nmsEntity, (Level)serverLevel, nmsDrops);
        event.getDrops().clear();
        for (net.minecraft.world.item.ItemStack nmsDrop : nmsDrops) {
            event.getDrops().add(CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nmsDrop));
        }
    }

    private ContraptionEntity findByAnchor(UUID anchorId) {
        for (ContraptionEntity entity : ContraptionManager.all()) {
            ContraptionState state = entity.state();
            if (anchorId.equals(state.anchorEntityId())) {
                return entity;
            }
            for (MovementBehavior behavior : state.behaviors()) {
                GhastFollowBehavior ghast;
                MinecartFollowBehavior minecart;
                if (!(behavior instanceof MinecartFollowBehavior ? anchorId.equals((minecart = (MinecartFollowBehavior)behavior).entityId()) : behavior instanceof GhastFollowBehavior && anchorId.equals((ghast = (GhastFollowBehavior)behavior).entityId()))) continue;
                return entity;
            }
        }
        return null;
    }
}

