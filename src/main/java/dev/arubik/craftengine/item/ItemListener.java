/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.retrooper.packetevents.PacketEvents
 *  com.github.retrooper.packetevents.event.PacketListener
 *  com.github.retrooper.packetevents.event.PacketListenerPriority
 *  com.github.retrooper.packetevents.event.PacketSendEvent
 *  com.github.retrooper.packetevents.protocol.item.ItemStack
 *  com.github.retrooper.packetevents.protocol.packettype.PacketType$Play$Server
 *  com.github.retrooper.packetevents.protocol.player.Equipment
 *  com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment
 *  com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem
 *  com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
 *  com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
 *  io.github.retrooper.packetevents.util.SpigotConversionUtil
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ItemStack
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityPickupItemEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.projectiles.ProjectileSource
 */
package dev.arubik.craftengine.item;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import dev.arubik.craftengine.item.behavior.ExtendedItemBehavior;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

public class ItemListener
implements Listener {
    public static ItemStack callBehavior(Object ... args) {
        if (args.length < 5) {
            return args[2] instanceof ItemStack ? (ItemStack)args[2] : null;
        }
        Event event = (Event)args[0];
        ItemActionType actionType = (ItemActionType)(args[1]);
        Entity holder = (Entity)args[2];
        net.minecraft.world.entity.Entity nmsHolder = ((CraftEntity)holder).getHandle();
        ItemStack item = (ItemStack)args[3];
        int slot = (Integer)args[4];
        if (item == null) {
            return null;
        }
        net.minecraft.world.item.ItemStack nmsItem = ((CraftItemStack)item).handle;
        ItemStack result = item;
        if (CraftEngineItems.byItemStack((ItemStack)item) == null) {
            return item;
        }
        ArrayList<ExtendedItemBehavior> behaviors = new ArrayList<>();
        CraftEngineItems.byItemStack((ItemStack)item).behavior().let(ExtendedItemBehavior.class, behaviors::add);
        for (ExtendedItemBehavior extBehavior : behaviors) {
            switch (actionType.ordinal()) {
                case 0: {
                    if (args.length < 5) break;
                    Entity target = (Entity)args[4];
                    net.minecraft.world.entity.Entity nmsTarget = ((CraftEntity)target).getHandle();
                    result = extBehavior.onAttackEntity(nmsItem, event, nmsHolder, nmsTarget, slot).asBukkitMirror();
                    break;
                }
                case 1: {
                    if (args.length < 6) break;
                    Entity target = (Entity)args[5];
                    net.minecraft.world.entity.Entity nmsTarget = ((CraftEntity)target).getHandle();
                    result = extBehavior.onInteractEntity(nmsItem, event, nmsHolder, nmsTarget, slot).asBukkitMirror();
                    break;
                }
                case 2: {
                    if (args.length < 6) break;
                    Entity dropEntity = (Entity)args[5];
                    net.minecraft.world.entity.Entity nmsDropEntity = ((CraftEntity)dropEntity).getHandle();
                    extBehavior.onDrop(nmsItem, event, nmsHolder, slot, nmsDropEntity);
                    break;
                }
                case 3: {
                    if (args.length < 5) break;
                    Entity pickupEntity = (Entity)args[4];
                    net.minecraft.world.entity.Entity nmsPickupEntity = ((CraftEntity)pickupEntity).getHandle();
                    extBehavior.onPickup(nmsItem, event, nmsHolder, slot, nmsPickupEntity);
                    break;
                }
                case 4: {
                    break;
                }
                case 5: {
                    net.minecraft.world.item.ItemStack returned = extBehavior.onUse(nmsItem, event, nmsHolder, slot);
                    result = returned != null ? returned.asBukkitMirror() : result;
                    break;
                }
                case 6: {
                    net.minecraft.world.item.ItemStack returned = extBehavior.onLeftClick(nmsItem, event, nmsHolder, slot);
                    result = returned != null ? returned.asBukkitMirror() : result;
                    break;
                }
                case 7: {
                    net.minecraft.world.item.ItemStack returned = extBehavior.onRightClick(nmsItem, event, nmsHolder, slot);
                    result = returned != null ? returned.asBukkitMirror() : result;
                    break;
                }
                case 8: {
                    net.minecraft.world.item.ItemStack returned = extBehavior.onConsume(nmsItem, event, nmsHolder, slot);
                    result = returned != null ? returned.asBukkitMirror() : result;
                    break;
                }
                case 9: {
                    net.minecraft.world.item.ItemStack returned = extBehavior.onBreakBlock(nmsItem, event, nmsHolder, slot);
                    result = returned != null ? returned.asBukkitMirror() : result;
                    break;
                }
                case 10: {
                    net.minecraft.world.item.ItemStack returned = extBehavior.onPlaceBlock(nmsItem, event, nmsHolder, slot);
                    result = returned != null ? returned.asBukkitMirror() : result;
                    break;
                }
                case 11: {
                    net.minecraft.world.item.ItemStack returned = extBehavior.onEquip(nmsItem, event, nmsHolder, slot);
                    result = returned != null ? returned.asBukkitMirror() : result;
                    break;
                }
                case 12: {
                    net.minecraft.world.item.ItemStack returned = extBehavior.onUnequip(nmsItem, event, nmsHolder, slot);
                    result = returned != null ? returned.asBukkitMirror() : result;
                    break;
                }
                case 13: {
                    if (args.length < 5) break;
                    double amount = (Double)args[4];
                    extBehavior.onDamageTaken(nmsItem, event, nmsHolder, slot, amount);
                    break;
                }
                case 14: {
                    if (args.length < 7) break;
                    ItemStack oldSlotItem = item;
                    ItemStack newSlotItem = (ItemStack)args[5];
                    if (oldSlotItem == null) {
                        oldSlotItem = ItemStack.empty();
                    }
                    if (newSlotItem == null) {
                        newSlotItem = ItemStack.empty();
                    }
                    net.minecraft.world.item.ItemStack nmsOldItem = ((CraftItemStack)oldSlotItem).handle;
                    net.minecraft.world.item.ItemStack nmsNewItem = ((CraftItemStack)newSlotItem).handle;
                    extBehavior.onSlotChange(nmsOldItem, event, nmsHolder, nmsNewItem, slot, (Integer)args[6]);
                    break;
                }
                case 15: {
                    result = extBehavior.onDeath(nmsItem, event, nmsHolder, slot).asBukkitCopy();
                    break;
                }
            }
        }
        return result;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        List drops = event.getDrops();
        for (int i = 0; i < drops.size(); ++i) {
            ItemStack item = (ItemStack)drops.get(i);
            ItemStack newItem = ItemListener.callBehavior(new Object[]{event, ItemActionType.DEATH, player, item, -1});
            if (newItem == null || newItem.isEmpty()) {
                drops.remove(i);
                --i;
                continue;
            }
            if (newItem == item) continue;
            drops.set(i, newItem);
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        org.bukkit.inventory.EquipmentSlot hand = event.getHand();
        if (item == null) {
            return;
        }
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR: 
            case RIGHT_CLICK_BLOCK: {
                event.getPlayer().getInventory().setItem(hand, ItemListener.callBehavior(new Object[]{event, ItemActionType.RIGHT_CLICK, player, item, player.getInventory().getHeldItemSlot()}));
                break;
            }
            case LEFT_CLICK_AIR: 
            case LEFT_CLICK_BLOCK: {
                event.getPlayer().getInventory().setItem(hand, ItemListener.callBehavior(new Object[]{event, ItemActionType.LEFT_CLICK, player, item, player.getInventory().getHeldItemSlot()}));
            }
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemListener.callBehavior(new Object[]{event, ItemActionType.SLOT_CHANGE, player, oldItem, event.getPreviousSlot(), newItem, event.getNewSlot()});
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemListener.callBehavior(new Object[]{event, ItemActionType.CONSUME, player, event.getItem(), player.getInventory().getHeldItemSlot()});
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemListener.callBehavior(new Object[]{event, ItemActionType.BREAK_BLOCK, player, player.getInventory().getItemInMainHand(), player.getInventory().getHeldItemSlot()});
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemListener.callBehavior(new Object[]{event, ItemActionType.PLACE_BLOCK, player, event.getItemInHand(), player.getInventory().getHeldItemSlot()});
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        switch (event.getCause()) {
            case BLOCK_EXPLOSION: {
                break;
            }
            case CAMPFIRE: {
                break;
            }
            case CONTACT: {
                break;
            }
            case CRAMMING: {
                break;
            }
            case CUSTOM: {
                break;
            }
            case DRAGON_BREATH: {
                break;
            }
            case DROWNING: {
                break;
            }
            case DRYOUT: {
                break;
            }
            case ENTITY_SWEEP_ATTACK: 
            case ENTITY_ATTACK: {
                Entity entity = event.getDamager();
                if (!(entity instanceof LivingEntity)) break;
                LivingEntity e = (LivingEntity)entity;
                if (e instanceof Player) {
                    Player player = (Player)e;
                    ItemListener.callBehavior(new Object[]{event, ItemActionType.ATTACK_ENTITY, player, player.getInventory().getItemInMainHand(), player.getInventory().getHeldItemSlot(), event.getEntity()});
                    break;
                }
                ItemStack item = e.getEquipment().getItemInMainHand();
                if (item == null) break;
                ItemListener.callBehavior(new Object[]{event, ItemActionType.ATTACK_ENTITY, e, item, -1, event.getEntity()});
                break;
            }
            case ENTITY_EXPLOSION: {
                break;
            }
            case FALL: {
                break;
            }
            case FALLING_BLOCK: {
                break;
            }
            case FIRE: {
                break;
            }
            case FIRE_TICK: {
                break;
            }
            case FLY_INTO_WALL: {
                break;
            }
            case FREEZE: {
                break;
            }
            case HOT_FLOOR: {
                break;
            }
            case KILL: {
                break;
            }
            case LAVA: {
                break;
            }
            case LIGHTNING: {
                break;
            }
            case MAGIC: {
                break;
            }
            case MELTING: {
                break;
            }
            case POISON: {
                break;
            }
            case PROJECTILE: {
                Projectile p;
                ProjectileSource item;
                Entity player = event.getDamager();
                if (!(player instanceof Projectile) || !((item = (p = (Projectile)player).getShooter()) instanceof LivingEntity)) break;
                LivingEntity e = (LivingEntity)item;
                if (e instanceof Player) {
                    Player player2 = (Player)e;
                    ItemListener.callBehavior(new Object[]{event, ItemActionType.ATTACK_ENTITY, player2, player2.getInventory().getItemInMainHand(), player2.getInventory().getHeldItemSlot(), event.getEntity()});
                    break;
                }
                ItemStack item2 = e.getEquipment().getItemInMainHand();
                if (item2 == null) break;
                ItemListener.callBehavior(new Object[]{event, ItemActionType.ATTACK_ENTITY, e, item2, -1, event.getEntity()});
                break;
            }
            case SONIC_BOOM: {
                break;
            }
            case STARVATION: {
                break;
            }
            case SUFFOCATION: {
                break;
            }
            case SUICIDE: {
                break;
            }
            case THORNS: {
                Entity e = event.getDamager();
                if (!(e instanceof LivingEntity)) break;
                LivingEntity e2 = (LivingEntity)e;
                ItemStack[] equipment = new ItemStack[]{e2.getEquipment().getHelmet(), e2.getEquipment().getChestplate(), e2.getEquipment().getLeggings(), e2.getEquipment().getBoots(), e2.getEquipment().getItemInMainHand()};
                int[] slotMap = new int[]{49, 50, 51, 52, 47};
                EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND};
                for (int i = 0; i < equipment.length; ++i) {
                    ItemStack item = equipment[i];
                    if (item == null) continue;
                    ItemListener.callBehavior(new Object[]{event, ItemActionType.ATTACK_ENTITY, e2, item, slotMap[i], slots[i]});
                }
                break;
            }
            case VOID: {
                break;
            }
            case WITHER: {
                break;
            }
            case WORLD_BORDER: {
                break;
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player) {
            Player player = (Player)livingEntity;
            ItemListener.callBehavior(new Object[]{event, ItemActionType.PICKUP, player, event.getItem().getItemStack(), player.getInventory().getHeldItemSlot(), event.getItem()});
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemListener.callBehavior(new Object[]{event, ItemActionType.DROP, player, event.getItemDrop().getItemStack(), player.getInventory().getHeldItemSlot(), event.getItemDrop()});
    }

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents((Listener)new ItemListener(), plugin);
        PacketEvents.getAPI().getEventManager().registerListener((PacketListener)new ItemPacketHandler(), PacketListenerPriority.LOWEST);
    }

    public static enum ItemActionType {
        ATTACK_ENTITY,
        INTERACT_ENTITY,
        DROP,
        PICKUP,
        RENDER,
        USE,
        LEFT_CLICK,
        RIGHT_CLICK,
        CONSUME,
        BREAK_BLOCK,
        PLACE_BLOCK,
        EQUIP,
        UNEQUIP,
        DAMAGE_TAKEN,
        SLOT_CHANGE,
        DEATH;

    }

    public static class ItemPacketHandler
    implements PacketListener {
        public void onPacketSend(PacketSendEvent event) {
            Player p = (Player)event.getPlayer();
            if (p == null) {
                return;
            }
            ServerPlayer player = ((CraftPlayer)p).getHandle();
            if (event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)) {
                WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
                net.minecraft.world.item.ItemStack nmsItem = this.fromBukkitItemStack(SpigotConversionUtil.toBukkitItemStack((com.github.retrooper.packetevents.protocol.item.ItemStack)packet.getItem()));
                if (this.isValidItem(nmsItem)) {
                    for (ExtendedItemBehavior behavior : this.getBehaviors(nmsItem)) {
                        nmsItem = behavior.onRender((net.minecraft.world.entity.Entity)player, nmsItem, packet.getSlot()).copy();
                    }
                    packet.setItem(SpigotConversionUtil.fromBukkitItemStack((ItemStack)nmsItem.asBukkitCopy()));
                } else {
                    packet.setItem(packet.getItem());
                }
            } else if (event.getPacketType().equals(PacketType.Play.Server.SET_CURSOR_ITEM)) {
                WrapperPlayServerSetCursorItem packet = new WrapperPlayServerSetCursorItem(event);
                net.minecraft.world.item.ItemStack nmsItem = this.fromBukkitItemStack(SpigotConversionUtil.toBukkitItemStack((com.github.retrooper.packetevents.protocol.item.ItemStack)packet.getStack()));
                if (this.isValidItem(nmsItem)) {
                    for (ExtendedItemBehavior behavior : this.getBehaviors(nmsItem)) {
                        nmsItem = behavior.onRender((net.minecraft.world.entity.Entity)player, nmsItem, -1).copy();
                    }
                    packet.setStack(SpigotConversionUtil.fromBukkitItemStack((ItemStack)nmsItem.asBukkitCopy()));
                } else {
                    packet.setStack(packet.getStack());
                }
            } else if (event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS)) {
                WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
                ArrayList<com.github.retrooper.packetevents.protocol.item.ItemStack> items = new ArrayList<com.github.retrooper.packetevents.protocol.item.ItemStack>();
                for (Object i : packet.getItems()) {
                    net.minecraft.world.item.ItemStack item = this.fromBukkitItemStack(SpigotConversionUtil.toBukkitItemStack((com.github.retrooper.packetevents.protocol.item.ItemStack)i));
                    if (this.isValidItem(item)) {
                        for (ExtendedItemBehavior behavior : this.getBehaviors(item)) {
                            item = behavior.onRender((net.minecraft.world.entity.Entity)player, item, -1).copy();
                        }
                        items.add(SpigotConversionUtil.fromBukkitItemStack((ItemStack)item.asBukkitCopy()));
                        continue;
                    }
                    items.add((com.github.retrooper.packetevents.protocol.item.ItemStack)i);
                }
                packet.setItems(items);
                if (packet.getCarriedItem().isPresent()) {
                    net.minecraft.world.item.ItemStack item = this.fromBukkitItemStack(SpigotConversionUtil.toBukkitItemStack((com.github.retrooper.packetevents.protocol.item.ItemStack)((com.github.retrooper.packetevents.protocol.item.ItemStack)packet.getCarriedItem().get())));
                    if (this.isValidItem(item)) {
                        for (ExtendedItemBehavior behavior : this.getBehaviors(item)) {
                            item = behavior.onRender((net.minecraft.world.entity.Entity)player, item, -1).copy();
                        }
                        packet.setCarriedItem(SpigotConversionUtil.fromBukkitItemStack((ItemStack)item.asBukkitCopy()));
                    } else {
                        packet.setCarriedItem((com.github.retrooper.packetevents.protocol.item.ItemStack)packet.getCarriedItem().get());
                    }
                }
            } else if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_EQUIPMENT)) {
                WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
                ArrayList<Equipment> equipments = new ArrayList<Equipment>();
                for (Equipment e : packet.getEquipment()) {
                    if (e.getItem() == null) {
                        equipments.add(e);
                        continue;
                    }
                    net.minecraft.world.item.ItemStack item = this.fromBukkitItemStack(SpigotConversionUtil.toBukkitItemStack((com.github.retrooper.packetevents.protocol.item.ItemStack)e.getItem()));
                    if (this.isValidItem(item)) {
                        for (ExtendedItemBehavior behavior : this.getBehaviors(item)) {
                            item = behavior.onRender((net.minecraft.world.entity.Entity)player, item, -1).copy();
                        }
                        equipments.add(new Equipment(e.getSlot(), SpigotConversionUtil.fromBukkitItemStack((ItemStack)item.asBukkitCopy())));
                        continue;
                    }
                    equipments.add(e);
                }
                packet.setEquipment(equipments);
            }
            event.markForReEncode(true);
        }

        public boolean isValidItem(net.minecraft.world.item.ItemStack item) {
            return item != null && !item.isEmpty();
        }

        public List<ExtendedItemBehavior> getBehaviors(net.minecraft.world.item.ItemStack item) {
            ArrayList<ExtendedItemBehavior> behaviors = new ArrayList<ExtendedItemBehavior>();
            if (CraftEngineItems.byItemStack((ItemStack)item.asBukkitMirror()) == null) {
                return behaviors;
            }
            CraftEngineItems.byItemStack((ItemStack)item.asBukkitMirror()).behavior().let(ExtendedItemBehavior.class, behaviors::add);
            return behaviors;
        }

        public net.minecraft.world.item.ItemStack fromBukkitItemStack(ItemStack item) {
            return ((CraftItemStack)item).handle;
        }
    }
}

