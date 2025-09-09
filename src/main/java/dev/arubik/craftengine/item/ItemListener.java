package dev.arubik.craftengine.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCollectItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerHeldItemChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;

import dev.arubik.craftengine.item.behavior.ExtendedItemBehavior;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.item.behavior.ItemBehavior;

public class ItemListener implements Listener {

    public enum ItemActionType {
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
        DEATH
    }

    // Event ActionType Item Holder Slot [Extra args...]
    public static ItemStack callBehavior(Object... args) {
        if (args.length < 5)
            return args[2] instanceof ItemStack ? (ItemStack) args[2] : null;
        Event event = (Event) args[0];
        ItemActionType actionType = (ItemActionType) args[1];
        Entity holder = (Entity) args[2];
        net.minecraft.world.entity.Entity nmsHolder = ((org.bukkit.craftbukkit.entity.CraftEntity) holder).getHandle();
        ItemStack item = (ItemStack) args[3];
        int slot = (int) args[4];
        if(item==null) return null;
        net.minecraft.world.item.ItemStack nmsItem = ((CraftItemStack) item).handle;
        ItemStack result = item;
        if(CraftEngineItems.byItemStack(item)==null) return item;
        for (ItemBehavior behavior : CraftEngineItems.byItemStack(item).behaviors()) {
            if (behavior instanceof ExtendedItemBehavior extBehavior) {
                switch (actionType) {
                    case ATTACK_ENTITY -> {
                        if (args.length < 5)
                            break;
                        Entity target = (Entity) args[4];
                        net.minecraft.world.entity.Entity nmsTarget = ((org.bukkit.craftbukkit.entity.CraftEntity) target)
                                .getHandle();
                        result = extBehavior.onAttackEntity(nmsItem, event, nmsHolder, nmsTarget, slot)
                                .asBukkitMirror();
                    }
                    case INTERACT_ENTITY -> {
                        if (args.length < 6)
                            break;
                        Entity target = (Entity) args[5];
                        net.minecraft.world.entity.Entity nmsTarget = ((org.bukkit.craftbukkit.entity.CraftEntity) target)
                                .getHandle();
                        result = extBehavior.onInteractEntity(nmsItem, event, nmsHolder, nmsTarget, slot)
                                .asBukkitMirror();
                    }
                    case DROP -> {
                        if (args.length < 6)
                            break;
                        Entity dropEntity = (Entity) args[5];
                        net.minecraft.world.entity.Entity nmsDropEntity = ((org.bukkit.craftbukkit.entity.CraftEntity) dropEntity)
                                .getHandle();
                        extBehavior.onDrop(nmsItem, event, nmsHolder, slot, nmsDropEntity);
                    }
                    case PICKUP -> {
                        if (args.length < 5)
                            break;
                        Entity pickupEntity = (Entity) args[4];
                        net.minecraft.world.entity.Entity nmsPickupEntity = ((org.bukkit.craftbukkit.entity.CraftEntity) pickupEntity)
                                .getHandle();
                        extBehavior.onPickup(nmsItem, event, nmsHolder, slot, nmsPickupEntity);
                    }
                    case RENDER -> {
                    }
                    case USE -> {
                        var returned = extBehavior.onUse(nmsItem, event, nmsHolder, slot);
                        result = returned != null ? returned.asBukkitMirror() : result;
                    }
                    case LEFT_CLICK -> {
                        var returned = extBehavior.onLeftClick(nmsItem, event, nmsHolder, slot);
                        result = returned != null ? returned.asBukkitMirror() : result;
                    }
                    case RIGHT_CLICK -> {
                        var returned = extBehavior.onRightClick(nmsItem, event, nmsHolder, slot);
                        result = returned != null ? returned.asBukkitMirror() : result;
                    }
                    case CONSUME -> {
                        var returned = extBehavior.onConsume(nmsItem, event, nmsHolder, slot);
                        result = returned != null ? returned.asBukkitMirror() : result;
                    }
                    case BREAK_BLOCK -> {
                        var returned = extBehavior.onBreakBlock(nmsItem, event, nmsHolder, slot);
                        result = returned != null ? returned.asBukkitMirror() : result;
                    }
                    case PLACE_BLOCK -> {
                        var returned = extBehavior.onPlaceBlock(nmsItem, event, nmsHolder, slot);
                        result = returned != null ? returned.asBukkitMirror() : result;
                    }
                    case EQUIP -> {
                        var returned = extBehavior.onEquip(nmsItem, event, nmsHolder, slot);
                        result = returned != null ? returned.asBukkitMirror() : result;
                    }
                    case UNEQUIP -> {
                        var returned = extBehavior.onUnequip(nmsItem, event, nmsHolder, slot);
                        result = returned != null ? returned.asBukkitMirror() : result;
                    }
                    case DAMAGE_TAKEN -> {
                        if (args.length < 5)
                            break;
                        double amount = (double) args[4];
                        extBehavior.onDamageTaken(nmsItem, event, nmsHolder, slot, amount);
                    }
                    case SLOT_CHANGE -> {
                        if (args.length < 6)
                            break;
                        ItemStack oldItem = (ItemStack) args[4];
                        ItemStack newItem = (ItemStack) args[5];
                        if (oldItem == null) oldItem = ItemStack.empty();
                        if (newItem == null) newItem = ItemStack.empty();
                        net.minecraft.world.item.ItemStack nmsOldItem = ((CraftItemStack) oldItem).handle;
                        net.minecraft.world.item.ItemStack nmsNewItem = ((CraftItemStack) newItem).handle;
                        extBehavior.onSlotChange(nmsOldItem, event, nmsHolder, nmsNewItem, slot, (int) args[5]);
                    }
                    case DEATH -> {
                        result = extBehavior.onDeath(nmsItem, event, nmsHolder, slot).asBukkitCopy();
                    }
                    default -> {
                    }
                }
            }
        }
        return result;
    }

    @EventHandler
    public void onEntityDeath(org.bukkit.event.entity.EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player))
            return;

        List<ItemStack> drops = event.getDrops();
        for (int i = 0; i < drops.size(); i++) {
            ItemStack item = drops.get(i);
            ItemStack newItem = callBehavior(event, ItemActionType.DEATH, player, item, -1);
            if (newItem == null || newItem.isEmpty()) {
                drops.remove(i);
                i--;
            } else if (newItem != item) {
                drops.set(i, newItem);
            }
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        org.bukkit.inventory.EquipmentSlot hand = event.getHand();
        if (item == null)
            return;

        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK ->
                event.getPlayer().getInventory().setItem(hand, callBehavior(event, ItemActionType.RIGHT_CLICK, player,
                        item, player.getInventory().getHeldItemSlot()));
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK ->
                event.getPlayer().getInventory().setItem(hand, callBehavior(event, ItemActionType.LEFT_CLICK, player,
                        item, player.getInventory().getHeldItemSlot()));
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        callBehavior(event, ItemActionType.SLOT_CHANGE, player, oldItem, event.getPreviousSlot(), newItem,
                event.getNewSlot());
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        callBehavior(event, ItemActionType.CONSUME, player, event.getItem(), player.getInventory().getHeldItemSlot());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        callBehavior(event, ItemActionType.BREAK_BLOCK, player, player.getInventory().getItemInMainHand(),
                player.getInventory().getHeldItemSlot());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        callBehavior(event, ItemActionType.PLACE_BLOCK, player, event.getItemInHand(),
                player.getInventory().getHeldItemSlot());
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        switch (event.getCause()) {
            case BLOCK_EXPLOSION:
                break;
            case CAMPFIRE:
                break;
            case CONTACT:
                break;
            case CRAMMING:
                break;
            case CUSTOM:
                break;
            case DRAGON_BREATH:
                break;
            case DROWNING:
                break;
            case DRYOUT:
                break;
            case ENTITY_SWEEP_ATTACK:
            case ENTITY_ATTACK: {
                if (event.getDamager() instanceof LivingEntity e) {
                    if (e instanceof Player player) {
                        callBehavior(event, ItemActionType.ATTACK_ENTITY, player,
                                player.getInventory().getItemInMainHand(),
                                player.getInventory().getHeldItemSlot(), event.getEntity());
                    } else {
                        ItemStack item = e.getEquipment().getItemInMainHand();
                        if (item != null)
                            callBehavior(event, ItemActionType.ATTACK_ENTITY, e, item, -1, event.getEntity());
                    }
                }
            }
                break;
            case ENTITY_EXPLOSION:
                break;
            case FALL:
                break;
            case FALLING_BLOCK:
                break;
            case FIRE:
                break;
            case FIRE_TICK:
                break;
            case FLY_INTO_WALL:
                break;
            case FREEZE:
                break;
            case HOT_FLOOR:
                break;
            case KILL:
                break;
            case LAVA:
                break;
            case LIGHTNING:
                break;
            case MAGIC:
                break;
            case MELTING:
                break;
            case POISON:
                break;
            case PROJECTILE: {
                if (event.getDamager() instanceof org.bukkit.entity.Projectile p) {
                    if (p.getShooter() instanceof LivingEntity e) {
                        if (e instanceof Player player) {
                            callBehavior(event, ItemActionType.ATTACK_ENTITY, player,
                                    player.getInventory().getItemInMainHand(),
                                    player.getInventory().getHeldItemSlot(), event.getEntity());
                        } else {
                            ItemStack item = e.getEquipment().getItemInMainHand();
                            if (item != null)
                                callBehavior(event, ItemActionType.ATTACK_ENTITY, e, item, -1, event.getEntity());
                        }
                    }
                }
            }
                break;
            case SONIC_BOOM:
                break;
            case STARVATION:
                break;
            case SUFFOCATION:
                break;
            case SUICIDE:
                break;
            case THORNS: {
                if (event.getDamager() instanceof LivingEntity e) {
                    // Get equipment
                    ItemStack[] equipment = new ItemStack[5];
                    equipment[0] = e.getEquipment().getHelmet();
                    equipment[1] = e.getEquipment().getChestplate();
                    equipment[2] = e.getEquipment().getLeggings();
                    equipment[3] = e.getEquipment().getBoots();
                    equipment[4] = e.getEquipment().getItemInMainHand();

                    // Map Bukkit EquipmentSlot to Minecraft slot index
                    int[] slotMap = new int[] {
                            49, // HEAD
                            50, // CHEST
                            51, // LEGS
                            52, // FEET
                            47 // MAINHAND
                    };

                    EquipmentSlot[] slots = new EquipmentSlot[] {
                            EquipmentSlot.HEAD,
                            EquipmentSlot.CHEST,
                            EquipmentSlot.LEGS,
                            EquipmentSlot.FEET,
                            EquipmentSlot.MAINHAND
                    };

                    for (int i = 0; i < equipment.length; i++) {
                        ItemStack item = equipment[i];
                        if (item != null) {
                            callBehavior(event, ItemActionType.ATTACK_ENTITY, e, item, slotMap[i], slots[i]);
                        }
                    }
                }
                break;
            }
            case VOID:
                break;
            case WITHER:
                break;
            case WORLD_BORDER:
                break;
            default:
                break;

        }

    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            callBehavior(event, ItemActionType.PICKUP, player, event.getItem().getItemStack(),
                    player.getInventory().getHeldItemSlot(), event.getItem());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        callBehavior(event, ItemActionType.DROP, player, event.getItemDrop().getItemStack(),
                player.getInventory().getHeldItemSlot(), event.getItemDrop());
    }

    public static class ItemPacketHandler implements PacketListener {
        @Override
        public void onPacketSend(PacketSendEvent event) {
            Player p = event.getPlayer();
            if(p==null) return;
            net.minecraft.world.entity.Entity player = ((CraftPlayer) p).getHandle();
            if (event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)) {
                WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
                net.minecraft.world.item.ItemStack nmsItem = fromBukkitItemStack(
                        SpigotConversionUtil.toBukkitItemStack(packet.getItem()));
                if (isValidItem(nmsItem)) {
                    for (ExtendedItemBehavior behavior : getBehaviors(nmsItem)) {

                        nmsItem = behavior.onRender(player, nmsItem, packet.getSlot()).copy();
                    }

                packet.setItem(SpigotConversionUtil.fromBukkitItemStack(nmsItem.asBukkitCopy()));
                }else{
                    packet.setItem(packet.getItem());
                }

            } else if (event.getPacketType().equals(PacketType.Play.Server.SET_CURSOR_ITEM)) {
                WrapperPlayServerSetCursorItem packet = new WrapperPlayServerSetCursorItem(event);

                net.minecraft.world.item.ItemStack nmsItem = fromBukkitItemStack(
                        SpigotConversionUtil.toBukkitItemStack(packet.getStack()));
                if (isValidItem(nmsItem)) {
                    for (ExtendedItemBehavior behavior : getBehaviors(nmsItem)) {

                        nmsItem = behavior.onRender(player, nmsItem, -1).copy();
                    }

                packet.setStack(SpigotConversionUtil.fromBukkitItemStack(nmsItem.asBukkitCopy()));
                }else{
                    packet.setStack(packet.getStack());
                }
            } else if(event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS)){
                WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
                List<com.github.retrooper.packetevents.protocol.item.ItemStack> items = new ArrayList<>();
                for (com.github.retrooper.packetevents.protocol.item.ItemStack i : packet.getItems()) {
                    net.minecraft.world.item.ItemStack item = fromBukkitItemStack(SpigotConversionUtil.toBukkitItemStack(i));
                    if (isValidItem(item)) {
                        for (ExtendedItemBehavior behavior : getBehaviors(item)) {
                            item = behavior.onRender(player, item, -1).copy();
                        }
                        items.add(SpigotConversionUtil.fromBukkitItemStack(item.asBukkitCopy()));
                    }else{
                        items.add(i);
                    }
                }
                packet.setItems(items);
                if(packet.getCarriedItem().isPresent()){
                    net.minecraft.world.item.ItemStack item = fromBukkitItemStack(SpigotConversionUtil.toBukkitItemStack(packet.getCarriedItem().get()));
                    if (isValidItem(item)) {
                        for (ExtendedItemBehavior behavior : getBehaviors(item)) {
                            item = behavior.onRender(player, item, -1).copy();
                        }
                    packet.setCarriedItem(SpigotConversionUtil.fromBukkitItemStack(item.asBukkitCopy()));
                    }else{
                        packet.setCarriedItem(packet.getCarriedItem().get());
                    }
                }

            }
            else if(event.getPacketType().equals(PacketType.Play.Server.ENTITY_EQUIPMENT)){
                WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
                List<Equipment> equipments = new ArrayList<>();
                for (Equipment e : packet.getEquipment()) {
                    if(e.getItem() == null) {
                        equipments.add(e);
                        continue;
                    }
                    net.minecraft.world.item.ItemStack item = fromBukkitItemStack(SpigotConversionUtil.toBukkitItemStack(e.getItem()));
                    if (isValidItem(item)) {
                        for (ExtendedItemBehavior behavior : getBehaviors(item)) {
                            item = behavior.onRender(player, item, -1).copy();
                        }
                    equipments.add(new Equipment(e.getSlot(),SpigotConversionUtil.fromBukkitItemStack(item.asBukkitCopy())));
                    }else{
                        equipments.add(e);
                    }
                }
                packet.setEquipment(equipments);
            }
            event.markForReEncode(true);
            
        }

        public boolean isValidItem(net.minecraft.world.item.ItemStack item) {
            return item != null && !item.isEmpty();
        }

        public List<ExtendedItemBehavior> getBehaviors(net.minecraft.world.item.ItemStack item) {
            List<ExtendedItemBehavior> behaviors = new ArrayList<>();
            if( CraftEngineItems.byItemStack(item.asBukkitMirror())==null) return behaviors;
            for (ItemBehavior behavior : CraftEngineItems.byItemStack(item.asBukkitMirror()).behaviors()) {
                if (behavior instanceof ExtendedItemBehavior extBehavior) {
                    behaviors.add(extBehavior);
                }
            }
            return behaviors;
        }

        public net.minecraft.world.item.ItemStack fromBukkitItemStack(ItemStack item) {
            return ((CraftItemStack) item).handle;
        }

    }

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ItemListener(), plugin);
        PacketEvents.getAPI().getEventManager().registerListener(new ItemPacketHandler(),PacketListenerPriority.LOWEST);
    }
}
