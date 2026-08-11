package dev.arubik.craftengine.contraption.listener;

import dev.arubik.craftengine.contraption.api.ContraptionTypeRegistry;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import net.momirealms.craftengine.core.util.Key;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.bearing.GhastHarness;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.type.GhastContraptionType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Universal contraption lifecycle listener.
 *
 * <p>
 * Single unified thread for all contraption lifecycle events:
 * - Placement (right-click rail with minecart item, equip harness on ghast)
 * - Damage/death protection (minecart invulnerable, ghast death cleanup)
 * - All events delegate to ContraptionType implementations
 *
 * <h2>Replaces</h2>
 * - BearingHammerListener.onPlaceContraptionItem (moved here)
 * - GhastContraptionType.onEquip (moved here)
 * - MinecartBearing.DamageGuard (moved here)
 * - GhastContraptionType.onDeath (moved here)
 *
 * <h2>Flow</h2>
 * 1. Placement: PlayerInteractEvent → ContraptionType.fromItem()
 * 2. Equip: EntityEquipmentChangedEvent → ContraptionType.fromItem()
 * 3. Damage: VehicleDamage/Destroy → ContraptionType.onAnchorDamage()
 * 4. Death: EntityDeathEvent → ContraptionType.onAnchorDeath()
 *
 * <h2>Why unified</h2>
 * External plugins register custom ContraptionTypes. This single listener
 * handles all lifecycle without requiring separate listener registration per type.
 * Read ContraptionType implementations (MinecartContraptionType, GhastContraptionType)
 * to understand full lifecycle.
 */
public class ContraptionLifecycleListener implements Listener {

    private final Map<UUID, Integer> armed = new HashMap<>();
    private static final int ARM_WINDOW_TICKS = 1;

    /**
     * Placement: right-click block with packed contraption item.
     * Delegates to ContraptionType.fromItem().
     * Replaces BearingHammerListener.onPlaceContraptionItem.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlaceItem(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND)
            return;
        ItemStack item = event.getItem();
        if (item == null)
            return;
        Block clicked = event.getClickedBlock();
        if (clicked == null)
            return;

        // Check if any ContraptionType recognizes as packed item
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        ContraptionType matchedType = null;

        for (Key typeKey : ContraptionTypeRegistry.getRegisteredTypes()) {
            ContraptionType type = ContraptionTypeRegistry.get(typeKey);
            if (type != null && type.isPackedItem(nmsItem)) {
                matchedType = type;
                break;
            }
        }

        if (matchedType == null)
            return;

        event.setCancelled(true);

        // Call ContraptionType.fromItem
        Level level = ((org.bukkit.craftbukkit.CraftWorld) clicked.getWorld()).getHandle();
        BlockPos pos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());
        ContraptionEntity entity = matchedType.fromItem(nmsItem, level, pos);

        if (entity == null) {
            event.getPlayer().sendMessage("§cCouldn't place contraption here.");
            return;
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }
        clicked.getWorld().playSound(clicked.getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1.0f);
        event.getPlayer().sendMessage("§bContraption restored.");
    }

    /**
     * Equip: ghast equips harness with packed contraption.
     * Delegates to ContraptionType.fromItem().
     * Replaces GhastContraptionType.onEquip.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEquipHarness(EntityEquipmentChangedEvent event) {
        if (!(event.getEntity() instanceof HappyGhast ghast)) {
            return;
        }
        EntityEquipmentChangedEvent.EquipmentChange change = event.getEquipmentChanges().get(EquipmentSlot.BODY);
        if (change == null) {
            return;
        }

        // Check if harness equipped
        if (!GhastHarness.isHarness(change.oldItem().getType()) && GhastHarness.isHarness(change.newItem().getType())) {
            ItemStack harness = change.newItem();

            // Check if packed contraption
            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(harness);
            ContraptionType ghastType = ContraptionTypeRegistry.get(Key.of("polyfills", "ghast"));

            if (ghastType == null || !ghastType.isPackedItem(nmsItem)) {
                return; // plain harness
            }

            if (GhastContraptionType.isBearing(ghast) && GhastContraptionType.isAssembled(ghast)) {
                return; // already carrying one
            }

            // Call ContraptionType.fromItem
            net.minecraft.world.entity.LivingEntity nmsGhast = ((org.bukkit.craftbukkit.entity.CraftLivingEntity) ghast).getHandle();
            net.minecraft.world.level.Level level = nmsGhast.level();
            net.minecraft.core.BlockPos ghastPos = nmsGhast.blockPosition();

            ContraptionEntity restored = ghastType.fromItem(nmsItem, level, ghastPos);
            if (restored == null) {
                return;
            }

            // Set anchor entity ID + attach behaviors with SAVED offsets from item
            restored.state().setAnchorEntityId(ghast.getUniqueId());

            // Attach follow behavior with saved offsets from state
            net.minecraft.world.phys.Vec3 anchorOffset = restored.state().anchorOffset();
            java.util.OptionalDouble yawOffset = restored.state().yawOffset();

            restored.state().addBehavior(
                new dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior(
                    ghast.getUniqueId(), anchorOffset, yawOffset));

            // Sync scale
            restored.state().setScale(nmsGhast.getScale());

            // Clear stale bytes from equipped harness
            net.minecraft.world.item.ItemStack clearedHarness = nmsItem.copy();
            clearedHarness.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY);
            ghast.getEquipment().setItem(EquipmentSlot.BODY,
                CraftItemStack.asBukkitCopy(clearedHarness), true);

            ghast.getWorld().playSound(ghast.getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1.2f);
        }
    }

    // ==================== Shear pre-stamp + unequip teardown ====================

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        onInteractEntity(e);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof HappyGhast ghast)) {
            return;
        }
        Player player = e.getPlayer();
        ItemStack hand = e.getHand() == EquipmentSlot.OFF_HAND
                ? player.getInventory().getItemInOffHand()
                : player.getInventory().getItemInMainHand();
        preStampForShear(ghast, player, hand);
    }

    private void preStampForShear(HappyGhast ghast, Player player, ItemStack hand) {
        if (!GhastContraptionType.isBearing(ghast) || !GhastContraptionType.isAssembled(ghast)) {
            return;
        }
        if (hand == null || hand.getType() != Material.SHEARS || player.isSneaking()) {
            return;
        }
        UUID contraptionId = GhastContraptionType.contraptionId(ghast);
        ContraptionEntity entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
        if (entity == null) {
            return;
        }
        ItemStack harness = ghast.getEquipment().getItem(EquipmentSlot.BODY);
        if (!GhastHarness.isHarness(harness.getType())) {
            return;
        }
        Key type = entity.state().bearingType();
        if (type == null) return;
        ContraptionType typeImpl = ContraptionTypeRegistry.get(type);
        if (typeImpl == null || !typeImpl.canPackToItem()) return;

        Level level = ((org.bukkit.craftbukkit.CraftWorld) ghast.getWorld()).getHandle();
        net.minecraft.world.item.ItemStack nmsItem = typeImpl.toItem(entity, level);
        if (nmsItem == null) return;

        ItemStack packed = CraftItemStack.asBukkitCopy(nmsItem);
        ghast.getEquipment().setItem(EquipmentSlot.BODY, packed, true);
        int now = Bukkit.getCurrentTick();
        armed.values().removeIf(armedAt -> now - armedAt > ARM_WINDOW_TICKS);
        armed.put(ghast.getUniqueId(), now);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEquipmentChanged(EntityEquipmentChangedEvent e) {
        if (!(e.getEntity() instanceof HappyGhast ghast)) {
            return;
        }
        EntityEquipmentChangedEvent.EquipmentChange change = e.getEquipmentChanges().get(EquipmentSlot.BODY);
        if (change == null) {
            return;
        }
        if (GhastHarness.isHarness(change.oldItem().getType()) && !GhastHarness.isHarness(change.newItem().getType())) {
            armed.remove(ghast.getUniqueId());
            onUnequip(ghast);
        }
    }

    private void onUnequip(HappyGhast ghast) {
        UUID contraptionId = GhastContraptionType.contraptionId(ghast);
        ContraptionEntity entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
        Integer armedAt = armed.remove(ghast.getUniqueId());
        if (entity == null) {
            return;
        }
        boolean carriedOutOnItem = armedAt != null
                && Bukkit.getCurrentTick() - armedAt <= ARM_WINDOW_TICKS;
        if (carriedOutOnItem) {
            GhastContraptionType.tearDownIntoItem(ghast.getWorld(), ghast, entity);
        } else {
            GhastContraptionType.disassembleInPlace(ghast.getWorld(), ghast, entity);
        }
    }

    /**
     * Vehicle damage (minecarts).
     * Delegates to ContraptionType.onAnchorDamage().
     * Replaces MinecartBearing.DamageGuard.
     */
    @EventHandler(ignoreCancelled = true)
    public void onVehicleDamage(VehicleDamageEvent event) {
        org.bukkit.entity.Entity vehicle = event.getVehicle();
        Entity nmsEntity = ((CraftEntity) vehicle).getHandle();

        ContraptionEntity contraption = findByAnchor(nmsEntity.getUUID());
        if (contraption == null) return;

        Key type = contraption.state().bearingType();
        if (type == null) return;

        ContraptionType typeImpl = ContraptionTypeRegistry.get(type);
        if (typeImpl == null) return;

        if (typeImpl.onAnchorDamage(nmsEntity, nmsEntity.level())) {
            event.setCancelled(true);
        }
    }

    /**
     * Vehicle destroy (minecarts).
     * Delegates to ContraptionType.onAnchorDamage().
     */
    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        org.bukkit.entity.Entity vehicle = event.getVehicle();
        Entity nmsEntity = ((CraftEntity) vehicle).getHandle();

        ContraptionEntity contraption = findByAnchor(nmsEntity.getUUID());
        if (contraption == null) return;

        Key type = contraption.state().bearingType();
        if (type == null) return;

        ContraptionType typeImpl = ContraptionTypeRegistry.get(type);
        if (typeImpl == null) return;

        if (typeImpl.onAnchorDamage(nmsEntity, nmsEntity.level())) {
            event.setCancelled(true);
        }
    }

    /**
     * Entity death (ghasts, custom entity-anchored types).
     * Delegates to ContraptionType.onAnchorDeath().
     * Replaces GhastContraptionType.onDeath.
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        org.bukkit.entity.LivingEntity dead = event.getEntity();
        Entity nmsEntity = ((CraftEntity) dead).getHandle();

        ContraptionEntity contraption = findByAnchor(nmsEntity.getUUID());
        if (contraption == null) return;

        Key type = contraption.state().bearingType();
        if (type == null) return;

        ContraptionType typeImpl = ContraptionTypeRegistry.get(type);
        if (typeImpl == null) return;

        if (!(nmsEntity.level() instanceof ServerLevel serverLevel)) return;

        // Convert Bukkit drops to NMS
        java.util.List<net.minecraft.world.item.ItemStack> nmsDrops = new java.util.ArrayList<>();
        for (org.bukkit.inventory.ItemStack bukkitDrop : event.getDrops()) {
            nmsDrops.add(CraftItemStack.asNMSCopy(bukkitDrop));
        }

        typeImpl.onAnchorDeath(nmsEntity, serverLevel, nmsDrops);

        // Convert back to Bukkit (in case implementation modified drops)
        event.getDrops().clear();
        for (net.minecraft.world.item.ItemStack nmsDrop : nmsDrops) {
            event.getDrops().add(CraftItemStack.asBukkitCopy(nmsDrop));
        }
    }

    /**
     * Finds contraption by anchor entity UUID.
     * Checks all contraptions, matches anchorEntityId or behavior entity IDs.
     */
    private ContraptionEntity findByAnchor(java.util.UUID anchorId) {
        for (ContraptionEntity entity : ContraptionManager.all()) {
            ContraptionState state = entity.state();

            // Check direct anchor ID
            if (anchorId.equals(state.anchorEntityId())) {
                return entity;
            }

            // Check behavior entity IDs (MinecartFollowBehavior, GhastFollowBehavior)
            for (MovementBehavior behavior : state.behaviors()) {
                if (behavior instanceof dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior minecart) {
                    if (anchorId.equals(minecart.entityId())) {
                        return entity;
                    }
                } else if (behavior instanceof dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior ghast) {
                    if (anchorId.equals(ghast.entityId())) {
                        return entity;
                    }
                }
            }
        }
        return null;
    }
}
