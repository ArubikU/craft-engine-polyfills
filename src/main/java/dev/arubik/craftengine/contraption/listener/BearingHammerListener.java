/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractAtEntityEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.contraption.listener;

import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.api.ContraptionTypeRegistry;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.bearing.MinecartBearing;
import dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.multiblock.HammerItems;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BearingHammerListener
implements Listener {
    private static final Map<AnchorKey, UUID> ASSEMBLED = new HashMap<AnchorKey, UUID>();
    private static final Map<UUID, AnchorKey> BY_CONTRAPTION = new HashMap<UUID, AnchorKey>();

    public static UUID assembledContraptionAt(ResourceKey<Level> worldId, BlockPos pos) {
        return ASSEMBLED.get(new AnchorKey(worldId, pos));
    }

    public static Map<UUID, AnchorKey> assembledAnchorsSnapshot() {
        return Map.copyOf(BY_CONTRAPTION);
    }

    public static void forgetAssembled(UUID contraptionId) {
        AnchorKey anchor = BY_CONTRAPTION.remove(contraptionId);
        if (anchor != null) {
            ASSEMBLED.remove(anchor);
        }
    }

    public static void markAssembled(ResourceKey<Level> worldId, BlockPos pos, UUID contraptionId) {
        AnchorKey anchor = new AnchorKey(worldId, pos);
        AnchorKey previous = BY_CONTRAPTION.get(contraptionId);
        if (anchor.equals(previous)) {
            return;
        }
        if (previous != null) {
            ASSEMBLED.remove(previous);
        }
        BY_CONTRAPTION.put(contraptionId, anchor);
        ASSEMBLED.put(anchor, contraptionId);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteractBlock(PlayerInteractEvent e) {
        BlockPos pos;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Key hammer = BearingHammerListener.hammerHeld(e.getItem());
        if (hammer == null) {
            return;
        }
        Block clicked = e.getClickedBlock();
        if (clicked == null) {
            return;
        }
        ServerLevel level = ((CraftWorld)clicked.getWorld()).getHandle();
        Key type = BearingBlockBehavior.typeAt((Level)level, pos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ()));
        if (type == null) {
            return;
        }
        if (type.equals(Key.of((String)"polyfills", (String)"linear"))) {
            return;
        }
        ResourceKey worldId = ((CraftWorld)clicked.getWorld()).getHandle().dimension();
        UUID assembledId = BearingHammerListener.assembledContraptionAt((ResourceKey<Level>)worldId, pos);
        if (assembledId != null) {
            e.setCancelled(true);
            ContraptionEntity entity = ContraptionManager.get(assembledId);
            if (entity != null) {
                ContraptionAssembler.disassemble(clicked.getWorld(), entity);
            }
            BearingHammerListener.forgetAssembled(assembledId);
            clicked.getWorld().playSound(clicked.getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1.4f);
            e.getPlayer().sendMessage("\u00a77Contraption disassembled, blocks restored.");
            return;
        }
        e.setCancelled(true);
        if (type.equals(Key.of((String)"polyfills", (String)"minecart"))) {
            ContraptionEntity entity = MinecartBearing.assemble(clicked.getWorld(), pos);
            if (entity == null) {
                e.getPlayer().sendMessage("\u00a7cA minecart bearing needs a rail directly beneath it.");
                return;
            }
            clicked.getWorld().playSound(clicked.getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1.0f);
            e.getPlayer().sendMessage("\u00a7bContraption assembled onto a minecart. \u00a77Right-click the minecart with a hammer to disassemble.");
            return;
        }
        double rotationalRpm = BearingBlockBehavior.rpmAt((Level)level, pos);
        double suPerBlock = BearingBlockBehavior.suPerBlockAt((Level)level, pos);
        ContraptionEntity entity = ContraptionAssembler.assemble(clicked.getWorld(), pos, type, rotationalRpm, suPerBlock);
        if (entity == null) {
            e.getPlayer().sendMessage("\u00a7cNothing to assemble here.");
            return;
        }
        BearingHammerListener.markAssembled((ResourceKey<Level>)worldId, pos, entity.state().id());
        clicked.getWorld().playSound(clicked.getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1.0f);
        e.getPlayer().sendMessage("\u00a7bContraption assembled \u00a77(" + String.valueOf(type) + " bearing).");
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        this.handleInteractEntity(e);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        this.handleInteractEntity((PlayerInteractEntityEvent)e);
    }

    private void handleInteractEntity(PlayerInteractEntityEvent e) {
        ContraptionEntity entity;
        if (e.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Key hammer = BearingHammerListener.hammerHeld(e.getPlayer().getInventory().getItemInMainHand());
        if (hammer == null) {
            return;
        }
        e.setCancelled(true);
        Entity nmsEntity = ((CraftEntity)e.getRightClicked()).getHandle();
        Level level = nmsEntity.level();
        ContraptionType owningType = null;
        for (Key typeKey : ContraptionTypeRegistry.getRegisteredTypes()) {
            ContraptionType typeImpl = ContraptionTypeRegistry.get(typeKey);
            if (typeImpl == null || !typeImpl.isBearingEntity(nmsEntity)) continue;
            owningType = typeImpl;
            break;
        }
        if (owningType == null) {
            return;
        }
        UUID contraptionId = owningType.getContraptionId(nmsEntity);
        ContraptionEntity contraptionEntity = entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
        if (entity == null) {
            e.getRightClicked().remove();
            return;
        }
        net.minecraft.world.item.ItemStack packedNms = owningType.toItem(entity, level);
        if (packedNms == null) {
            e.getPlayer().sendMessage("\u00a7cCouldn't pack this contraption.");
            return;
        }
        ItemStack packedBukkit = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)packedNms);
        HashMap<Integer, ItemStack> overflow = e.getPlayer().getInventory().addItem(new ItemStack[]{packedBukkit});
        for (ItemStack leftover : overflow.values()) {
            e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), leftover);
        }
        e.getRightClicked().getWorld().playSound(e.getRightClicked().getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.8f, 1.0f);
        e.getPlayer().sendMessage("\u00a7bContraption packed into an item.");
    }

    private static Key hammerHeld(ItemStack hand) {
        if (hand == null || hand.getType().isAir()) {
            return null;
        }
        Key hammer = CraftEngineItems.getCustomItemId((ItemStack)hand);
        return HammerItems.isHammer(hammer) ? hammer : null;
    }

    public record AnchorKey(ResourceKey<Level> worldId, BlockPos pos) {
    }
}

