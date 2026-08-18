/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
 *  net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Container
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerAnimationEvent
 *  org.bukkit.event.player.PlayerAnimationType
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.ContraptionInteractionListener;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import dev.arubik.craftengine.contraption.player.CePlayers;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public final class ContraptionMining
implements Listener {
    private static final int AIM_GRACE_TICKS = 4;
    private static final long SWING_HOLD_MS = 900L;
    private static final long DROP_SWING_GRACE_MS = 250L;
    private static final int REMOVE_FLAGS = 50;
    private static final Map<UUID, Session> SESSIONS = new ConcurrentHashMap<UUID, Session>();
    private static final Map<UUID, Long> LAST_CREATIVE_BREAK_MS = new ConcurrentHashMap<UUID, Long>();
    private static final Map<UUID, Long> LAST_USE_MS = new ConcurrentHashMap<UUID, Long>();
    private static final Map<UUID, Long> LAST_DROP_MS = new ConcurrentHashMap<UUID, Long>();
    private static int taskId = -1;

    private ContraptionMining() {
    }

    public static void noteUse(UUID playerId) {
        LAST_USE_MS.put(playerId, System.currentTimeMillis());
    }

    public static void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents((Listener)new ContraptionMining(), plugin);
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, ContraptionMining::tickAll, 1L, 1L).getTaskId();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
    public void onSwing(PlayerAnimationEvent event) {
        if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) {
            return;
        }
        UUID playerId = ((CraftPlayer)event.getPlayer()).getUniqueId();
        long nowMs = System.currentTimeMillis();
        Long lastUse = LAST_USE_MS.get(playerId);
        if (lastUse != null && nowMs - lastUse < 250L) {
            return;
        }
        Long lastDrop = LAST_DROP_MS.get(playerId);
        if (lastDrop != null && nowMs - lastDrop < 250L) {
            return;
        }
        ServerPlayer player = ((CraftPlayer)event.getPlayer()).getHandle();
        ContraptionInteractionListener.Hit hit = ContraptionInteractionListener.raycast(player);
        if (hit != null) {
            ContraptionMining.armDig(player, hit);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
    public void onDrop(PlayerDropItemEvent event) {
        LAST_DROP_MS.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    static void armDig(ServerPlayer player, ContraptionInteractionListener.Hit hit) {
        if (hit.state().level() == null) {
            return;
        }
        BlockState state = hit.state().level().getBlockState(hit.local());
        if (state.isAir()) {
            return;
        }
        UUID playerId = player.getUUID();
        if (player.getAbilities().instabuild) {
            long now = System.currentTimeMillis();
            Long last = LAST_CREATIVE_BREAK_MS.get(playerId);
            if (last != null && now - last < 200L) {
                return;
            }
            LAST_CREATIVE_BREAK_MS.put(playerId, now);
            Vec3 faceWorld = hit.state().level().realWorldPositionOf(hit.localClip());
            ContraptionMining.breakCell(player, hit.state(), hit.local(), state, false, faceWorld);
            SESSIONS.remove(playerId);
            return;
        }
        Session existing = SESSIONS.get(playerId);
        if (existing != null && existing.contraptionId.equals(hit.state().id()) && existing.local.equals(hit.local())) {
            existing.lastSwingMs = System.currentTimeMillis();
        } else {
            SESSIONS.put(playerId, new Session(hit.state().id(), hit.local(), System.currentTimeMillis()));
        }
    }

    private static void tickAll() {
        if (SESSIONS.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, Session>> it = SESSIONS.entrySet().iterator();
        while (it.hasNext()) {
            BlockPos ct;
            boolean onCell;
            Map.Entry<UUID, Session> entry = it.next();
            Session s = entry.getValue();
            Player bukkit = Bukkit.getPlayer((UUID)entry.getKey());
            if (bukkit == null) {
                it.remove();
                continue;
            }
            ServerPlayer player = ((CraftPlayer)bukkit).getHandle();
            ContraptionEntity entity = ContraptionManager.get(s.contraptionId);
            if (entity == null || entity.state().level() == null) {
                ContraptionMining.clearCrack(s, player);
                it.remove();
                continue;
            }
            ContraptionInteractionListener.Hit hit = ContraptionInteractionListener.raycast(player);
            boolean bl = onCell = hit != null && hit.state().id().equals(s.contraptionId) && hit.local().equals(s.local);
            if (!onCell) {
                if (++s.missTicks <= 4) continue;
                ContraptionMining.clearCrack(s, player);
                it.remove();
                continue;
            }
            s.missTicks = 0;
            if (System.currentTimeMillis() - s.lastSwingMs > 900L) continue;
            ContraptionLevel level = entity.state().level();
            BlockState state = level.getBlockState(s.local);
            if (state.isAir()) {
                ContraptionMining.clearCrack(s, player);
                it.remove();
                continue;
            }
            ServerLevel cLevel = level.serverLevel();
            float hardness = state.getDestroySpeed((BlockGetter)cLevel, s.local);
            if (hardness < 0.0f) {
                ContraptionMining.clearCrack(s, player);
                it.remove();
                continue;
            }
            float digSpeed = player.getDestroySpeed(state);
            boolean correctTool = player.hasCorrectToolForDrops(state);
            double delta = hardness <= 0.0f ? 1.0 : (double)(digSpeed / hardness) / (correctTool ? 30.0 : 100.0);
            s.progress += delta;
            Vec3 faceWorld = level.realWorldPositionOf(hit.localClip());
            player.swing(InteractionHand.MAIN_HAND, true);
            ContraptionMining.emitCrumbs(player.level(), faceWorld, state, s.progress);
            if (s.tickCounter++ % 5 == 0) {
                SoundType st = state.getSoundType();
                ContraptionMining.playCellSound(level, s.local, st.getHitSound(), (st.getVolume() + 1.0f) / 8.0f, st.getPitch() * 0.5f);
            }
            if ((ct = ContraptionMining.crackTarget(entity.state(), level, s.local, player.level())) != null) {
                int stage;
                if (!ct.equals(s.crackPos)) {
                    ContraptionMining.clearCrack(s, player);
                    player.connection.send((Packet)new ClientboundBlockUpdatePacket(ct, state));
                    s.crackPos = ct;
                }
                if ((stage = Math.min(9, (int)(s.progress * 10.0))) != s.crackStage) {
                    player.connection.send((Packet)new ClientboundBlockDestructionPacket(player.getId(), ct, stage));
                    s.crackStage = stage;
                }
            } else {
                ContraptionMining.clearCrack(s, player);
            }
            if (!(s.progress >= 1.0)) continue;
            ContraptionMining.breakCell(player, entity.state(), s.local, state, !correctTool, faceWorld);
            ContraptionMining.clearCrack(s, player);
            it.remove();
        }
    }

    private static void cascadeUnsupported(ContraptionLevel level, ServerLevel cLevel, ServerLevel realLevel, BlockPos origin) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>();
        HashSet<BlockPos> seen = new HashSet<BlockPos>();
        for (Direction d : Direction.values()) {
            queue.add(origin.relative(d));
        }
        int guard = 0;
        while (!queue.isEmpty() && guard++ < 4096) {
            boolean survives;
            BlockState s;
            BlockPos p = (BlockPos)queue.poll();
            if (!seen.add(p) || (s = level.getBlockState(p)).isAir()) continue;
            try {
                survives = s.canSurvive((LevelReader)cLevel, p);
            }
            catch (Throwable t) {
                survives = true;
            }
            if (survives) continue;
            Vec3 c = level.realWorldPositionOf(new Vec3((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5));
            BlockPos realPos = BlockPos.containing((double)c.x, (double)c.y, (double)c.z);
            try {
                for (net.minecraft.world.item.ItemStack drop : Block.getDrops((BlockState)s, (ServerLevel)cLevel, (BlockPos)p, (BlockEntity)level.getBlockEntity(p))) {
                    Block.popResource((Level)realLevel, (BlockPos)realPos, (net.minecraft.world.item.ItemStack)drop);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            level.setBlock(p, Blocks.AIR.defaultBlockState(), 50);
            for (Direction d : Direction.values()) {
                queue.add(p.relative(d));
            }
        }
    }

    private static void breakCell(ServerPlayer player, ContraptionState state, BlockPos local, BlockState blockState, boolean wrongTool, Vec3 faceWorld) {
        net.minecraft.world.item.ItemStack tool;
        ContraptionLevel level = state.level();
        if (level == null) {
            return;
        }
        ServerLevel cLevel = level.serverLevel();
        ServerLevel realLevel = player.level();
        Vec3 centerWorld = level.realWorldPositionOf(new Vec3((double)local.getX() + 0.5, (double)local.getY() + 0.5, (double)local.getZ() + 0.5));
        BlockPos realBlockPos = BlockPos.containing((double)centerWorld.x, (double)centerWorld.y, (double)centerWorld.z);
        boolean creative = player.getAbilities().instabuild;
        BlockEntity be = level.getBlockEntity(local);
        if (!creative && be instanceof Container) {
            Container container = (Container)be;
            for (int slot = 0; slot < container.getContainerSize(); ++slot) {
                net.minecraft.world.item.ItemStack content = container.getItem(slot);
                if (content.isEmpty()) continue;
                Block.popResource((Level)realLevel, (BlockPos)realBlockPos, (net.minecraft.world.item.ItemStack)content.copy());
            }
            container.clearContent();
        }
        if (!creative && !wrongTool) {
            ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(blockState).orElse(null);
            if (ce != null && !ce.isEmpty()) {
                net.minecraft.world.item.ItemStack customDrop = ContraptionMining.ceBlockDrop(ce);
                if (customDrop != null && !customDrop.isEmpty()) {
                    Block.popResource((Level)realLevel, (BlockPos)realBlockPos, (net.minecraft.world.item.ItemStack)customDrop);
                }
            } else {
                List<net.minecraft.world.item.ItemStack> drops = Block.getDrops((BlockState)blockState, (ServerLevel)cLevel, (BlockPos)local, (BlockEntity)be, (Entity)player, (net.minecraft.world.item.ItemStack)player.getMainHandItem());
                for (net.minecraft.world.item.ItemStack drop : drops) {
                    Block.popResource((Level)realLevel, (BlockPos)realBlockPos, (net.minecraft.world.item.ItemStack)drop);
                }
            }
        }
        level.setBlock(local, Blocks.AIR.defaultBlockState(), 50);
        ContraptionMining.cascadeUnsupported(level, cLevel, realLevel, local);
        level.updateNeighborsAt(local, blockState.getBlock());
        level.markCellsDirty();
        if (!creative && !(tool = player.getMainHandItem()).isEmpty()) {
            tool.hurtAndBreak(1, cLevel, (LivingEntity)player, item -> {});
        }
        SoundType st = blockState.getSoundType();
        ContraptionMining.playCellSound(level, local, st.getBreakSound(), (st.getVolume() + 1.0f) / 2.0f, st.getPitch() * 0.8f);
        realLevel.sendParticles((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, blockState), faceWorld.x, faceWorld.y, faceWorld.z, 24, 0.2, 0.2, 0.2, 0.05);
        level.refreshLocalPositions();
        boolean empty = true;
        for (BlockPos p : level.localPositions()) {
            if (level.getBlockState(p).isAir()) continue;
            empty = false;
            break;
        }
        if (empty) {
            ContraptionMining.teardownEmpty(state);
        }
    }

    private static void teardownEmpty(ContraptionState state) {
        try {
            CraftWorld bukkitWorld;
            ContraptionEntity entity = ContraptionManager.get(state.id());
            DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
            ServerLevel serverLevel = server.getLevel(state.worldId());
            CraftWorld craftWorld = bukkitWorld = serverLevel != null ? serverLevel.getWorld() : null;
            if (entity != null) {
                entity.despawn(bukkitWorld == null ? List.of() : CePlayers.resolve(bukkitWorld.getPlayers()));
            }
            ContraptionManager.remove(state.id());
            PhysicsWorld.remove(state.id());
            BlockAnchoredContraptionStore.delete(state.id());
            BearingHammerListener.forgetAssembled(state.id());
            ContraptionLevel level = state.level();
            if (level != null) {
                level.dispose();
            }
        }
        catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption] mining teardown of emptied contraption failed: " + String.valueOf(t));
        }
    }

    private static BlockPos crackTarget(ContraptionState state, ContraptionLevel level, BlockPos local, ServerLevel realLevel) {
        if (Math.abs(state.pitchRadians()) > 0.001 || Math.abs(state.rollRadians()) > 0.001 || Math.abs(state.scale() - 1.0) > 0.001) {
            return null;
        }
        Vec3 centre = level.realWorldPositionOf(new Vec3((double)local.getX() + 0.5, (double)local.getY() + 0.5, (double)local.getZ() + 0.5));
        BlockPos bp = BlockPos.containing((double)centre.x, (double)centre.y, (double)centre.z);
        if (Math.abs(centre.x - ((double)bp.getX() + 0.5)) > 0.05 || Math.abs(centre.y - ((double)bp.getY() + 0.5)) > 0.05 || Math.abs(centre.z - ((double)bp.getZ() + 0.5)) > 0.05) {
            return null;
        }
        return realLevel.getBlockState(bp).isAir() ? bp : null;
    }

    private static void clearCrack(Session s, ServerPlayer player) {
        if (s.crackPos != null && player != null) {
            player.connection.send((Packet)new ClientboundBlockDestructionPacket(player.getId(), s.crackPos, -1));
            BlockState real = player.level().getBlockState(s.crackPos);
            player.connection.send((Packet)new ClientboundBlockUpdatePacket(s.crackPos, real));
        }
        s.crackPos = null;
        s.crackStage = -1;
    }

    private static net.minecraft.world.item.ItemStack ceBlockDrop(ImmutableBlockState ce) {
        try {
            Key id = ((BlockDefinition)ce.owner().value()).id();
            BukkitItemDefinition def = CraftEngineItems.byId((Key)id);
            if (def == null) {
                return null;
            }
            ItemStack bukkit = def.buildBukkitItem();
            return bukkit == null ? null : CraftItemStack.asNMSCopy((ItemStack)bukkit);
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static void emitCrumbs(ServerLevel realLevel, Vec3 faceWorld, BlockState state, double progress) {
        int count = 1 + (int)(progress * 5.0);
        realLevel.sendParticles((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, state), faceWorld.x, faceWorld.y, faceWorld.z, count, 0.12, 0.12, 0.12, 0.02);
    }

    private static void playCellSound(ContraptionLevel level, BlockPos local, SoundEvent sound, float volume, float pitch) {
        if (sound == null) {
            return;
        }
        level.playSeededSound(null, (double)local.getX() + 0.5, (double)local.getY() + 0.5, (double)local.getZ() + 0.5, (Holder<SoundEvent>)Holder.direct(sound), SoundSource.BLOCKS, volume, pitch, 0L);
    }

    public static void forget(UUID playerId) {
        SESSIONS.remove(playerId);
        LAST_CREATIVE_BREAK_MS.remove(playerId);
        LAST_USE_MS.remove(playerId);
        LAST_DROP_MS.remove(playerId);
    }

    private static final class Session {
        final UUID contraptionId;
        final BlockPos local;
        double progress;
        long lastSwingMs;
        int tickCounter;
        int missTicks;
        BlockPos crackPos;
        int crackStage = -1;

        Session(UUID contraptionId, BlockPos local, long nowMs) {
            this.contraptionId = contraptionId;
            this.local = local;
            this.lastSwingMs = nowMs;
        }
    }
}

