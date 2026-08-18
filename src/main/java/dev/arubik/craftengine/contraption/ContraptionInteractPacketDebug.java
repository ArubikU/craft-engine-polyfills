/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.retrooper.packetevents.PacketEvents
 *  com.github.retrooper.packetevents.event.PacketListener
 *  com.github.retrooper.packetevents.event.PacketListenerPriority
 *  com.github.retrooper.packetevents.event.PacketReceiveEvent
 *  com.github.retrooper.packetevents.protocol.packettype.PacketType$Play$Client
 *  com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
 *  com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity$InteractAction
 *  com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.SignBlockEntity
 *  net.minecraft.world.level.block.entity.SignText
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Bukkit
 *  org.bukkit.Sound
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package dev.arubik.craftengine.contraption;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.ContraptionInteractionListener;
import dev.arubik.craftengine.contraption.bearing.MinecartBearing;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.special.ContraptionSignElement;
import dev.arubik.craftengine.contraption.listener.CreativePhysWandListener;
import dev.arubik.craftengine.multiblock.HammerItems;
import java.lang.reflect.Method;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public final class ContraptionInteractPacketDebug
implements PacketListener {
    public static void register() {
        PacketEvents.getAPI().getEventManager().registerListener((PacketListener)new ContraptionInteractPacketDebug(), PacketListenerPriority.LOWEST);
    }

    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.UPDATE_SIGN)) {
            ContraptionInteractPacketDebug.handleSignUpdate(event);
            return;
        }
        if (!event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
            return;
        }
        try {
            ContraptionState overlayState;
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            Player bukkitPlayer = Bukkit.getPlayer((UUID)event.getUser().getUUID());
            if (!(bukkitPlayer instanceof CraftPlayer)) {
                return;
            }
            CraftPlayer craftPlayer = (CraftPlayer)bukkitPlayer;
            ServerPlayer player = craftPlayer.getHandle();
            if (wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) {
                int targetId = wrapper.getEntityId();
                Bukkit.getScheduler().runTask((Plugin)CraftEnginePolyfills.instance(), () -> ContraptionInteractPacketDebug.tryDisassembleBearing(bukkitPlayer, targetId));
                return;
            }
            if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT) {
                return;
            }
            if (CreativePhysWandListener.wouldHandlePacketInteract(bukkitPlayer)) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTask((Plugin)CraftEnginePolyfills.instance(), () -> CreativePhysWandListener.handlePacketInteract(bukkitPlayer));
                return;
            }
            int clickedEntityId = wrapper.getEntityId();
            ContraptionElement overlayElement = ContraptionInteractPacketDebug.resolveOverlayElement(clickedEntityId);
            if (overlayElement != null && (overlayState = ContraptionInteractPacketDebug.resolveOverlayState(clickedEntityId)) != null) {
                event.setCancelled(true);
                ServerPlayer sp = player;
                ContraptionElement elem = overlayElement;
                ContraptionState st = overlayState;
                Vec3 eyePos = sp.getEyePosition(1.0f);
                Vec3 lookDir = sp.getLookAngle();
                Vec3 hitPos = eyePos.add(lookDir.scale(4.0));
                Bukkit.getScheduler().runTask((Plugin)CraftEnginePolyfills.instance(), () -> elem.onInteract(sp, st, hitPos, InteractionHand.MAIN_HAND, true));
                return;
            }
            ContraptionInteractionListener.Hit blockHit = ContraptionInteractionListener.raycast(player);
            if (blockHit != null) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTask((Plugin)CraftEnginePolyfills.instance(), () -> ContraptionInteractionListener.forward(player, blockHit));
            }
        }
        catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption][PACKET] failed to handle INTERACT_ENTITY: " + String.valueOf(t));
        }
    }

    private static ContraptionElement resolveOverlayElement(int entityId) {
        for (ContraptionEntity entity : ContraptionManager.all()) {
            ContraptionState state = entity.state();
            ContraptionElement elem = state.elementByInteractionEntityId(entityId);
            if (elem == null) continue;
            return elem;
        }
        return null;
    }

    private static ContraptionState resolveOverlayState(int entityId) {
        for (ContraptionEntity entity : ContraptionManager.all()) {
            ContraptionState state = entity.state();
            ContraptionElement elem = state.elementByInteractionEntityId(entityId);
            if (elem == null) continue;
            return state;
        }
        return null;
    }

    private static void handleSignUpdate(PacketReceiveEvent event) {
        try {
            ServerPlayer serverPlayer;
            UUID playerId = event.getUser().getUUID();
            ContraptionSignElement.PendingSignEdit pending = ContraptionSignElement.PENDING_EDITS.remove(playerId);
            if (pending == null) {
                return;
            }
            event.setCancelled(true);
            WrapperPlayClientUpdateSign wrapper = new WrapperPlayClientUpdateSign(event);
            String[] lines = wrapper.getTextLines();
            boolean isFront = pending.isFront();
            ContraptionSignElement.PendingSignEdit ctx = pending;
            String[] finalLines = lines;
            Player finalBukkitPlayer = Bukkit.getPlayer((UUID)playerId);
            if (finalBukkitPlayer instanceof CraftPlayer) {
                CraftPlayer cp2 = (CraftPlayer)finalBukkitPlayer;
                serverPlayer = cp2.getHandle();
            } else {
                serverPlayer = null;
            }
            ServerPlayer finalServerPlayer = serverPlayer;
            Bukkit.getScheduler().runTask((Plugin)CraftEnginePolyfills.instance(), () -> {
                try {
                    ContraptionLevel level = ctx.state().level();
                    if (level == null) {
                        return;
                    }
                    BlockEntity be = level.getBlockEntity(ctx.localPos());
                    if (be instanceof SignBlockEntity) {
                        SignBlockEntity sign = (SignBlockEntity)be;
                        ContraptionInteractPacketDebug.applySignText(sign, finalLines, isFront);
                    }
                    ctx.cleanup(finalServerPlayer);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static void applySignText(SignBlockEntity sign, String[] lines, boolean front) {
        try {
            SignText current;
            SignText updated = current = front ? sign.getFrontText() : sign.getBackText();
            for (int i = 0; i < 4 && i < lines.length; ++i) {
                updated = updated.setMessage(i, (Component)Component.literal((String)lines[i]));
            }
            String method = front ? "setFrontText" : "setBackText";
            Method m = SignBlockEntity.class.getDeclaredMethod(method, SignText.class);
            m.setAccessible(true);
            m.invoke(sign, updated);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void tryDisassembleBearing(Player bukkitPlayer, int targetId) {
        try {
            ContraptionEntity entity;
            ServerPlayer sp = ((CraftPlayer)bukkitPlayer).getHandle();
            net.minecraft.world.entity.Entity nms = sp.level().getEntity(targetId);
            if (nms == null) {
                return;
            }
            CraftEntity clicked = nms.getBukkitEntity();
            if (clicked == null || !MinecartBearing.isBearing((Entity)clicked) || !MinecartBearing.isAssembled((Entity)clicked)) {
                return;
            }
            ItemStack hand = bukkitPlayer.getInventory().getItemInMainHand();
            if (hand == null || hand.getType().isAir()) {
                return;
            }
            Key hammer = CraftEngineItems.getCustomItemId((ItemStack)hand);
            if (hammer == null || !HammerItems.isHammer(hammer)) {
                return;
            }
            UUID contraptionId = MinecartBearing.contraptionId((Entity)clicked);
            ContraptionEntity contraptionEntity = entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
            if (entity != null) {
                MinecartBearing.disassemble(clicked.getWorld(), (Entity)clicked, entity);
            } else {
                clicked.remove();
            }
            clicked.getWorld().playSound(clicked.getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1.4f);
            bukkitPlayer.sendMessage("\u00a77Contraption disassembled, blocks restored, minecart removed.");
        }
        catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption][PACKET] bearing disassemble failed: " + String.valueOf(t));
        }
    }
}

