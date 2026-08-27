/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Container
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.ContainerUser
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ContainerLevelAccess
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.momirealms.craftengine.bukkit.api.BukkitAdaptor
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.item.BukkitItem
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.bukkit.world.BukkitWorld
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.entity.player.InteractionHand
 *  net.momirealms.craftengine.core.entity.player.InteractionResult
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.item.Item
 *  net.momirealms.craftengine.core.item.behavior.BlockItem
 *  net.momirealms.craftengine.core.item.behavior.ItemBehavior
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockHitResult
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.Vec3d
 *  net.momirealms.craftengine.core.world.World
 *  net.momirealms.craftengine.core.world.context.UseOnContext
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftHumanEntity
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.ContraptionMining;
import dev.arubik.craftengine.contraption.VehicleDriverRegistry;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.bearing.MinecartBearing;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.event.ContraptionBlockPlaceEvent;
import dev.arubik.craftengine.contraption.event.ContraptionInteractEvent;
import dev.arubik.craftengine.multiblock.HammerItems;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItem;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.item.behavior.ItemBehavior;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockHitResult;
import net.momirealms.craftengine.core.world.Vec3d;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class ContraptionInteractionListener
implements Listener {
    public static volatile boolean PLACEMENT_DEBUG = false;

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=false)
    public void debugRawInteract(PlayerInteractEvent event) {
        if (!PLACEMENT_DEBUG) return;
        Bukkit.getLogger().info("[Contraption][RAW] action=" + String.valueOf(event.getAction()) + " hand=" + String.valueOf(event.getHand()) + " cancelled=" + event.isCancelled() + " clickedBlock=" + String.valueOf(event.getClickedBlock()) + " item=" + String.valueOf(event.getItem()) + " player=" + event.getPlayer().getName());
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=false)
    public void debugRawInteractEntity(PlayerInteractEntityEvent event) {
        Bukkit.getLogger().info("[Contraption][RAW-ENTITY] entity=" + String.valueOf(event.getRightClicked()) + " hand=" + String.valueOf(event.getHand()) + " cancelled=" + event.isCancelled() + " player=" + event.getPlayer().getName());
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (MinecartBearing.isBearing(event.getRightClicked())) {
            return;
        }
        ServerPlayer player = ((CraftPlayer)event.getPlayer()).getHandle();
        Hit hit = ContraptionInteractionListener.raycast(player);
        if (hit == null) {
            return;
        }
        event.setCancelled(true);
        ContraptionInteractionListener.forward(player, hit);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent event) {
        boolean left;
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Action action = event.getAction();
        boolean right = action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR;
        boolean bl = left = action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR;
        if (!right && !left) {
            return;
        }
        ServerPlayer player = ((CraftPlayer)event.getPlayer()).getHandle();
        Hit hit = ContraptionInteractionListener.raycast(player);
        if (hit == null) {
            if (PLACEMENT_DEBUG && !ContraptionManager.all().isEmpty()) {
                Bukkit.getLogger().info("[Contraption] interact raycast: no hit (" + right + "/" + left + ") for " + event.getPlayer().getName());
            }
            return;
        }
        if (PLACEMENT_DEBUG) {
            Bukkit.getLogger().info("[Contraption] interact raycast HIT local=" + String.valueOf(hit.local()) + " right=" + right);
        }
        event.setCancelled(true);
        if (left) {
            ContraptionInteractionListener.forwardAttack(player, hit);
            ContraptionMining.armDig(player, hit);
            return;
        }
        ContraptionInteractionListener.forward(player, hit);
    }

    private static boolean tryHammerDisassemblePhys(ServerPlayer player, Hit hit) {
        ContraptionState state = hit.state();
        if (!Key.of((String)"polyfills", (String)"phys").equals(state.bearingType()) && !Key.of((String)"polyfills", (String)"vehicle").equals(state.bearingType())) {
            return false;
        }
        CraftPlayer bukkitPlayer = player.getBukkitEntity();
        ItemStack held = bukkitPlayer.getInventory().getItemInMainHand();
        if (!HammerItems.isHammer(CraftEngineItems.getCustomItemId((ItemStack)held))) {
            return false;
        }
        ContraptionEntity entity = ContraptionManager.get(state.id());
        CraftWorld world = null;
        try {
            DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
            ServerLevel level = server.getLevel(state.worldId());
            world = level != null ? level.getWorld() : null;
        }
        catch (Throwable server) {
            // empty catch block
        }
        if (entity == null || world == null) {
            return true;
        }
        try {
            UUID driver = VehicleDriverRegistry.driverOf(state.id());
            if (driver != null) {
                VehicleDriverRegistry.clearDriver(driver);
            }
            ContraptionAssembler.disassemble((World)world, entity);
            world.playSound(bukkitPlayer.getLocation(), Sound.BLOCK_ANVIL_USE, 0.7f, 1.4f);
            bukkitPlayer.sendMessage("\u00a77Contraption disassembled, blocks restored.");
        }
        catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption] phys hammer disassemble failed: " + String.valueOf(t));
        }
        return true;
    }

    public static Hit raycast(ServerPlayer player) {
        double maxDistance = player.blockInteractionRange();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(maxDistance));
        Hit best = null;
        double bestDistSq = Double.MAX_VALUE;
        int contraptionCount = 0;
        for (ContraptionEntity entity : ContraptionManager.all()) {
            ++contraptionCount;
            ContraptionState state = entity.state();
            ContraptionLevel level = state.level();
            if (level == null) continue;
            Vec3 bearingPos = new Vec3(state.x(), state.y(), state.z());
            double yaw = state.yawRadians();
            double pitch = state.pitchRadians();
            double roll = state.rollRadians();
            double scale = state.scale();
            Vec3 localEye = ContraptionMath.realToLocal(eye, bearingPos, yaw, pitch, roll, scale);
            Vec3 localEnd = ContraptionMath.realToLocal(end, bearingPos, yaw, pitch, roll, scale);
            if (PLACEMENT_DEBUG) Bukkit.getLogger().info("[Contraption] raycast vs contraption bearing=" + String.valueOf(bearingPos) + " yaw=" + yaw + " cells=" + level.localPositions().size() + " localEye=" + String.valueOf(localEye) + " localEnd=" + String.valueOf(localEnd) + " maxDist=" + maxDistance);
            for (BlockPos local : level.localPositions()) {
                double distSq;
                AABB box = ContraptionInteractionListener.localCellBox(level, local);
                Optional clip = box.clip(localEye, localEnd);
                if (clip.isEmpty() || !((distSq = localEye.distanceToSqr((Vec3)clip.get()) * scale * scale) < bestDistSq)) continue;
                bestDistSq = distSq;
                Vec3 center = box.getCenter();
                Vec3 rel = ((Vec3)clip.get()).subtract(center);
                Direction face = Direction.getApproximateNearest((double)rel.x, (double)rel.y, (double)rel.z);
                best = new Hit(state, local, (Vec3)clip.get(), face);
            }
        }
        if (best == null && PLACEMENT_DEBUG) {
            Bukkit.getLogger().info("[Contraption] raycast: " + contraptionCount + " contraption(s) checked, no cell clip hit");
        }
        return best;
    }

    private static AABB localCellBox(ContraptionLevel level, BlockPos local) {
        AABB unit = new AABB((double)local.getX(), (double)local.getY(), (double)local.getZ(), (double)(local.getX() + 1), (double)(local.getY() + 1), (double)(local.getZ() + 1));
        try {
            BlockState state = level.getBlockState(local);
            if (state.isAir()) {
                return unit;
            }
            VoxelShape shape = state.getCollisionShape((BlockGetter)level.serverLevel(), local);
            if (shape.isEmpty()) {
                return unit;
            }
            AABB b = shape.bounds();
            return b.move((double)local.getX(), (double)local.getY(), (double)local.getZ());
        }
        catch (Throwable t) {
            return unit;
        }
    }

    public static void forward(ServerPlayer player, Hit hit) {
        ContraptionEntity entity;
        ContraptionMining.noteUse(player.getUUID());
        if (ContraptionInteractionListener.tryHammerDisassemblePhys(player, hit)) {
            return;
        }
        ContraptionLevel level = hit.state().level();
        if (level == null) {
            return;
        }
        BlockState blockState = level.getBlockState(hit.local());
        if (blockState.isAir()) {
            return;
        }
        if (ContraptionInteractionListener.fireInteractCancelled(player, hit, true)) {
            return;
        }
        net.minecraft.world.phys.BlockHitResult hitResult = new net.minecraft.world.phys.BlockHitResult(hit.localClip(), hit.face(), hit.local(), false);
        AbstractContainerMenu menuBefore = player.containerMenu;
        Bukkit.getLogger().info("[Contraption] pre-dispatch blockEntity@" + String.valueOf(hit.local()) + "=" + String.valueOf(level.getBlockEntity(hit.local())));
        boolean consumed = false;
        InteractionHand swungHand = InteractionHand.MAIN_HAND;
        boolean anyHandHeld = !player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() || !player.getItemInHand(InteractionHand.OFF_HAND).isEmpty();
        boolean skipInteract = player.isSecondaryUseActive() && anyHandHeld;
        try {
            if (!skipInteract) {
                Optional customState = BlockStateUtils.getOptionalCustomBlockState(blockState);
                if (customState.isPresent()) {
                    InteractionResult ceResult = ContraptionInteractionListener.dispatchCraftEngineCustomBlock((ImmutableBlockState)customState.get(), level, player, hit);
                    Bukkit.getLogger().info("[Contraption] CE-direct dispatch result=" + String.valueOf(ceResult) + " consumes=" + ceResult.consumesAction());
                    consumed = ceResult.consumesAction();
                } else {
                    net.minecraft.world.item.ItemStack offHeld;
                    InteractionResult mainResult = ContraptionInteractionListener.tryHand(blockState, level, player, hitResult, InteractionHand.MAIN_HAND, true);
                    Bukkit.getLogger().info("[Contraption] mainHand dispatch result=" + String.valueOf(mainResult) + " consumes=" + mainResult.consumesAction());
                    consumed = mainResult.consumesAction();
                    if (!consumed && !(offHeld = player.getItemInHand(InteractionHand.OFF_HAND)).isEmpty()) {
                        InteractionResult offResult = ContraptionInteractionListener.tryHand(blockState, level, player, hitResult, InteractionHand.OFF_HAND, false);
                        Bukkit.getLogger().info("[Contraption] offHand dispatch result=" + String.valueOf(offResult) + " consumes=" + offResult.consumesAction());
                        if (offResult.consumesAction()) {
                            consumed = true;
                            swungHand = InteractionHand.OFF_HAND;
                        }
                    }
                }
            } else {
                Bukkit.getLogger().info("[Contraption] sneak-bypass: skipping interact-with-block, falling through to placement for " + player.getName().getString());
            }
            if (!consumed) {
                PlaceOutcome mainPlace = ContraptionInteractionListener.tryPlace(level, player, hitResult, InteractionHand.MAIN_HAND, hit);
                if (mainPlace.consumed()) {
                    consumed = true;
                } else {
                    PlaceOutcome offPlace;
                    net.minecraft.world.item.ItemStack offHeld = player.getItemInHand(InteractionHand.OFF_HAND);
                    if (!offHeld.isEmpty() && (offPlace = ContraptionInteractionListener.tryPlace(level, player, hitResult, InteractionHand.OFF_HAND, hit)).consumed()) {
                        consumed = true;
                        swungHand = InteractionHand.OFF_HAND;
                    }
                }
            }
        }
        catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption] interact dispatch threw: " + String.valueOf(t));
            t.printStackTrace();
        }
        Bukkit.getLogger().info("[Contraption] menuBefore=" + String.valueOf(menuBefore) + " menuAfter=" + String.valueOf(player.containerMenu));
        if (player.containerMenu != menuBefore) {
            ContraptionInteractionListener.fixVanillaMenuStillValid(player.containerMenu, level, hit.local());
            ContraptionInteractionListener.fixVanillaMenuLevelAccess(player.containerMenu, level, hit.local());
        }
        if ((entity = ContraptionManager.get(hit.state().id())) != null) {
            entity.markDisplayDirty(hit.local());
            entity.markDisplayDirty(hit.local().relative(hit.face()));
        }
        if (consumed) {
            player.swing(swungHand, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static PlaceOutcome tryPlace(ContraptionLevel level, ServerPlayer player, net.minecraft.world.phys.BlockHitResult hitResult, InteractionHand hand, Hit hit) {
        net.minecraft.world.item.ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return new PlaceOutcome(false);
        }
        if (ContraptionInteractionListener.fireBlockPlaceCancelled(player, hit, hitResult.getBlockPos().immutable(), held, hand)) {
            return new PlaceOutcome(false);
        }
        BlockPos targetPos = hitResult.getBlockPos().immutable();
        level.ensureChunkReady(targetPos);
        level.ensureChunkReady(targetPos.relative(hitResult.getDirection()));
        float originalYaw = player.getYRot();
        float originalHeadYaw = player.getYHeadRot();
        float delta = (float)Math.toDegrees(hit.state().yawRadians());
        float localYaw = originalYaw - delta;
        player.setYRot(localYaw);
        player.setYHeadRot(originalHeadYaw - delta);
        try {
            PlaceOutcome outcome = ContraptionInteractionListener.placeWithAim(level, player, hitResult, hand, held, targetPos);
            if (PLACEMENT_DEBUG) {
                Bukkit.getLogger().info("[ContraptionRot] contraptionYaw=" + String.format("%.1f", Math.toDegrees(hit.state().yawRadians())) + " playerYaw=" + String.format("%.1f", Float.valueOf(originalYaw)) + " placedWithYaw=" + String.format("%.1f", Float.valueOf(localYaw)) + " face=" + String.valueOf(hitResult.getDirection()) + " cell=" + String.valueOf(targetPos) + " -> " + String.valueOf(level.getBlockState(targetPos)));
            }
            PlaceOutcome placeOutcome = outcome;
            return placeOutcome;
        }
        finally {
            player.setYRot(originalYaw);
            player.setYHeadRot(originalHeadYaw);
        }
    }

    private static PlaceOutcome placeWithAim(ContraptionLevel level, ServerPlayer player, net.minecraft.world.phys.BlockHitResult hitResult, InteractionHand hand, net.minecraft.world.item.ItemStack held, BlockPos targetPos) {
        Key customItemId = CraftEngineItems.getCustomItemId((ItemStack)CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)held));
        if (customItemId != null) {
            boolean placed = ContraptionInteractionListener.tryPlaceCraftEngineCustomItem(level, player, hitResult, hand, held);
            if (PLACEMENT_DEBUG) {
                Bukkit.getLogger().info("[Contraption] CE-item placement id=" + String.valueOf(customItemId) + " hand=" + String.valueOf(hand) + " placed=" + placed);
            }
            if (placed) {
                ContraptionInteractionListener.playPlaceSound(level, targetPos, hitResult.getDirection());
            }
            return new PlaceOutcome(placed);
        }
        Item item = held.getItem();
        if (!(item instanceof BlockItem)) {
            return new PlaceOutcome(false);
        }
        BlockItem blockItem = (BlockItem)item;
        BlockPlaceContext context = new BlockPlaceContext((Level)level.serverLevel(), (net.minecraft.world.entity.player.Player)player, hand, held, hitResult);
        InteractionResult result = blockItem.place(context);
        if (PLACEMENT_DEBUG) {
            Bukkit.getLogger().info("[Contraption] vanilla placement item=" + String.valueOf(held) + " hand=" + String.valueOf(hand) + " result=" + String.valueOf(result) + " consumes=" + result.consumesAction());
        }
        if (result.consumesAction()) {
            ContraptionInteractionListener.playPlaceSound(level, targetPos, hitResult.getDirection());
        }
        return new PlaceOutcome(result.consumesAction());
    }

    private static void playPlaceSound(ContraptionLevel level, BlockPos targetPos, Direction face) {
        try {
            BlockState placed = level.getBlockState(targetPos);
            if (placed.isAir()) {
                placed = level.getBlockState(targetPos.relative(face));
            }
            if (placed.isAir()) {
                return;
            }
            SoundType soundType = placed.getSoundType();
            SoundEvent placeSound = soundType.getPlaceSound();
            if (placeSound == null) {
                return;
            }
            level.playSeededSound(null, (double)targetPos.getX() + 0.5, (double)targetPos.getY() + 0.5, (double)targetPos.getZ() + 0.5, (Holder<SoundEvent>)Holder.direct(placeSound), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f, 0L);
        }
        catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption] playPlaceSound threw: " + String.valueOf(t));
        }
    }

    private static boolean tryPlaceCraftEngineCustomItem(ContraptionLevel level, ServerPlayer player, net.minecraft.world.phys.BlockHitResult hitResult, InteractionHand hand, net.minecraft.world.item.ItemStack held) {
        ItemStack bukkitStack = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)held);
        BukkitItemDefinition def = CraftEngineItems.byItemStack((ItemStack)bukkitStack);
        if (def == null) {
            return false;
        }
        ItemBehavior behavior = def.behavior();
        if (!(behavior instanceof net.momirealms.craftengine.core.item.behavior.BlockItem)) {
            return false;
        }
        CraftPlayer bukkitPlayer = player.getBukkitEntity();
        BukkitServerPlayer cePlayer = BukkitAdaptor.adapt((org.bukkit.entity.Player)bukkitPlayer);
        BukkitWorld ceWorld = BukkitAdaptor.adapt((World)level.getWorld());
        BukkitItem ceItem = BukkitAdaptor.adapt((ItemStack)bukkitStack);
        BlockPos clickedPos = hitResult.getBlockPos();
        net.momirealms.craftengine.core.world.BlockPos ceBlockPos = new net.momirealms.craftengine.core.world.BlockPos(clickedPos.getX(), clickedPos.getY(), clickedPos.getZ());
        net.momirealms.craftengine.core.util.Direction ceFace = net.momirealms.craftengine.core.util.Direction.valueOf((String)hitResult.getDirection().name());
        Vec3 clip = hitResult.getLocation();
        Vec3d ceClip = new Vec3d(clip.x, clip.y, clip.z);
        BlockHitResult ceHit = new BlockHitResult(ceClip, ceFace, ceBlockPos, false);
        net.momirealms.craftengine.core.entity.player.InteractionHand ceHand = hand == InteractionHand.MAIN_HAND ? net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND : net.momirealms.craftengine.core.entity.player.InteractionHand.OFF_HAND;
        UseOnContext ctx = new UseOnContext((net.momirealms.craftengine.core.world.World)ceWorld, (Player)cePlayer, ceHand, (net.momirealms.craftengine.core.item.Item)ceItem, ceHit);
        net.momirealms.craftengine.core.entity.player.InteractionResult result = behavior.useOnBlock(ctx);
        return result != null && result.success();
    }

    private static InteractionResult dispatchCraftEngineCustomBlock(ImmutableBlockState ceState, ContraptionLevel level, ServerPlayer player, Hit hit) {
        BukkitItem ceItem;
        UseOnContext withItemCtx;
        net.momirealms.craftengine.core.entity.player.InteractionResult withItemResult;
        BlockBehavior behavior = ceState.behavior();
        if (behavior == null) {
            return InteractionResult.PASS;
        }
        CraftPlayer bukkitPlayer = player.getBukkitEntity();
        BukkitServerPlayer cePlayer = BukkitAdaptor.adapt((org.bukkit.entity.Player)bukkitPlayer);
        BukkitWorld ceWorld = BukkitAdaptor.adapt((World)level.getWorld());
        net.momirealms.craftengine.core.world.BlockPos ceBlockPos = new net.momirealms.craftengine.core.world.BlockPos(hit.local().getX(), hit.local().getY(), hit.local().getZ());
        net.momirealms.craftengine.core.util.Direction ceFace = net.momirealms.craftengine.core.util.Direction.valueOf((String)hit.face().name());
        Vec3 localClip = hit.localClip();
        Vec3d ceClip = new Vec3d(localClip.x, localClip.y, localClip.z);
        BlockHitResult ceHit = new BlockHitResult(ceClip, ceFace, ceBlockPos, false);
        net.minecraft.world.item.ItemStack mainHeld = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!mainHeld.isEmpty() && (withItemResult = behavior.useOnBlock(withItemCtx = new UseOnContext((net.momirealms.craftengine.core.world.World)ceWorld, (Player)cePlayer, net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND, (net.momirealms.craftengine.core.item.Item)(ceItem = BukkitAdaptor.adapt((ItemStack)CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)mainHeld))), ceHit), ceState)) != null && withItemResult.success()) {
            return InteractionResult.SUCCESS;
        }
        UseOnContext emptyCtx = new UseOnContext((net.momirealms.craftengine.core.world.World)ceWorld, (Player)cePlayer, net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND, null, ceHit);
        net.momirealms.craftengine.core.entity.player.InteractionResult emptyResult = behavior.useWithoutItem(emptyCtx, ceState);
        return emptyResult != null && emptyResult.success() ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private static InteractionResult tryHand(BlockState blockState, ContraptionLevel level, ServerPlayer player, net.minecraft.world.phys.BlockHitResult hitResult, InteractionHand hand, boolean allowEmptyHandFallback) {
        net.minecraft.world.item.ItemStack held = player.getItemInHand(hand);
        InteractionResult result = blockState.useItemOn(held, (Level)level.serverLevel(), (net.minecraft.world.entity.player.Player)player, hand, hitResult);
        if (result.consumesAction()) {
            return result;
        }
        if (allowEmptyHandFallback) {
            return blockState.useWithoutItem((Level)level.serverLevel(), (net.minecraft.world.entity.player.Player)player, hitResult);
        }
        return result;
    }

    public static void forwardAttack(ServerPlayer player, Hit hit) {
        ContraptionLevel level = hit.state().level();
        if (level == null) {
            return;
        }
        BlockState blockState = level.getBlockState(hit.local());
        if (blockState.isAir()) {
            return;
        }
        if (ContraptionInteractionListener.fireInteractCancelled(player, hit, false)) {
            return;
        }
        try {
            blockState.attack((Level)level.serverLevel(), hit.local(), (net.minecraft.world.entity.player.Player)player);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean fireInteractCancelled(ServerPlayer player, Hit hit, boolean right) {
        try {
            ContraptionEntity entity = ContraptionManager.get(hit.state().id());
            if (entity == null) {
                return false;
            }
            ContraptionElement element = hit.state().elementByLocalPos(hit.local());
            ContraptionInteractEvent event = new ContraptionInteractEvent((org.bukkit.entity.Player)player.getBukkitEntity(), entity, hit.local(), hit.face(), EquipmentSlot.HAND, right, element);
            Bukkit.getPluginManager().callEvent((Event)event);
            return event.isCancelled();
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean fireBlockPlaceCancelled(ServerPlayer player, Hit hit, BlockPos targetLocal, net.minecraft.world.item.ItemStack held, InteractionHand hand) {
        try {
            ContraptionEntity entity = ContraptionManager.get(hit.state().id());
            if (entity == null) {
                return false;
            }
            EquipmentSlot slot = hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
            ContraptionBlockPlaceEvent event = new ContraptionBlockPlaceEvent((org.bukkit.entity.Player)player.getBukkitEntity(), entity, targetLocal, CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)held), slot);
            Bukkit.getPluginManager().callEvent((Event)event);
            return event.isCancelled();
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    private static void fixVanillaMenuLevelAccess(AbstractContainerMenu menu, final ContraptionLevel level, final BlockPos local) {
        if (menu == null) {
            return;
        }
        ContainerLevelAccess patched = new ContainerLevelAccess(){

            public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> function) {
                return Optional.empty();
            }

            public void execute(BiConsumer<Level, BlockPos> consumer) {
                consumer.accept((Level)level.serverLevel(), local);
            }
        };
        try {
            for (Field field : ContraptionInteractionListener.allFields(menu.getClass())) {
                if (field.getType() != ContainerLevelAccess.class) continue;
                field.setAccessible(true);
                if (field.get(menu) == patched) continue;
                field.set(menu, patched);
            }
        }
        catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption] fixVanillaMenuLevelAccess threw: " + String.valueOf(t));
        }
    }

    private static void fixVanillaMenuStillValid(AbstractContainerMenu menu, ContraptionLevel level, BlockPos local) {
        if (menu == null) {
            return;
        }
        try {
            for (Field field : ContraptionInteractionListener.allFields(menu.getClass())) {
                if (field.getType() != Container.class) continue;
                field.setAccessible(true);
                Object value = field.get(menu);
                if (!(value instanceof Container)) continue;
                Container original = (Container)value;
                if (value instanceof RealWorldAwareContainer) continue;
                field.set(menu, new RealWorldAwareContainer(original, level, local));
                Bukkit.getLogger().info("[Contraption] stillValid-wrapped field=" + String.valueOf(field));
            }
        }
        catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption] fixVanillaMenuStillValid threw: " + String.valueOf(t));
            t.printStackTrace();
        }
    }

    private static List<Field> allFields(Class<?> type) {
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    public record Hit(ContraptionState state, BlockPos local, Vec3 localClip, Direction face) {
    }

    private record PlaceOutcome(boolean consumed) {
    }

    private static final class RealWorldAwareContainer
    implements Container {
        private final Container delegate;
        private final ContraptionLevel level;
        private final BlockPos local;

        RealWorldAwareContainer(Container delegate, ContraptionLevel level, BlockPos local) {
            this.delegate = delegate;
            this.level = level;
            this.local = local;
        }

        public boolean stillValid(net.minecraft.world.entity.player.Player player) {
            if (player.isRemoved()) {
                return false;
            }
            Vec3 localCenter = new Vec3((double)this.local.getX() + 0.5, (double)this.local.getY() + 0.5, (double)this.local.getZ() + 0.5);
            Vec3 realPos = this.level.realWorldPositionOf(localCenter);
            return player.distanceToSqr(realPos.x, realPos.y, realPos.z) <= 64.0;
        }

        public int getContainerSize() {
            return this.delegate.getContainerSize();
        }

        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }

        public net.minecraft.world.item.ItemStack getItem(int slot) {
            return this.delegate.getItem(slot);
        }

        public net.minecraft.world.item.ItemStack removeItem(int slot, int amount) {
            return this.delegate.removeItem(slot, amount);
        }

        public net.minecraft.world.item.ItemStack removeItemNoUpdate(int slot) {
            return this.delegate.removeItemNoUpdate(slot);
        }

        public void setItem(int slot, net.minecraft.world.item.ItemStack stack) {
            this.delegate.setItem(slot, stack);
        }

        public void setChanged() {
            this.delegate.setChanged();
        }

        public int getMaxStackSize() {
            return this.delegate.getMaxStackSize();
        }

        public int getMaxStackSize(net.minecraft.world.item.ItemStack stack) {
            return this.delegate.getMaxStackSize(stack);
        }

        public void startOpen(ContainerUser user) {
            this.delegate.startOpen(user);
        }

        public void stopOpen(ContainerUser user) {
            this.delegate.stopOpen(user);
        }

        public List<ContainerUser> getEntitiesWithContainerOpen() {
            return this.delegate.getEntitiesWithContainerOpen();
        }

        public boolean canPlaceItem(int slot, net.minecraft.world.item.ItemStack stack) {
            return this.delegate.canPlaceItem(slot, stack);
        }

        public boolean canTakeItem(Container target, int slot, net.minecraft.world.item.ItemStack stack) {
            return this.delegate.canTakeItem(target, slot, stack);
        }

        public void clearContent() {
            this.delegate.clearContent();
        }

        public List<net.minecraft.world.item.ItemStack> getContents() {
            return this.delegate.getContents();
        }

        public void onOpen(CraftHumanEntity who) {
            this.delegate.onOpen(who);
        }

        public void onClose(CraftHumanEntity who) {
            this.delegate.onClose(who);
        }

        public List<HumanEntity> getViewers() {
            return this.delegate.getViewers();
        }

        public InventoryHolder getOwner() {
            return this.delegate.getOwner();
        }

        public void setMaxStackSize(int size) {
            this.delegate.setMaxStackSize(size);
        }

        public Location getLocation() {
            return this.delegate.getLocation();
        }
    }
}

