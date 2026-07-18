package dev.arubik.craftengine.contraption;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.plugin.Plugin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.ContraptionInteractionListener.Hit;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * <b>Mining blocks OUT of a contraption</b> — the destructive twin of {@link ContraptionInteractionListener}'s
 * placement path. Placing a block into a contraption already works (right-click routes the held item's real
 * {@code BlockItem#place} against the hidden {@link ContraptionLevel}); this is the reverse: hold left-click on
 * a captured cell and it mines away, at the correct tool speed, dropping the correct loot, damaging the tool,
 * with a crumbling animation and vanilla dig/break sounds.
 *
 * <h2>Why this can't reuse vanilla mining</h2>
 * Vanilla mining is driven by the client: aim at a REAL block, the client sends
 * {@code ServerboundPlayerActionPacket(START_DESTROY_BLOCK)} and the server's {@code ServerPlayerGameMode}
 * ticks the progress. A contraption cell is NOT a real block in the player's world — it is a packet-only
 * block_display over air — so the client never sends a dig packet and never runs the destroy animation for it.
 * The whole loop therefore has to live server-side:
 * <ul>
 *   <li><b>Heartbeat.</b> Holding left-click makes the client swing its arm continuously, which arrives as
 *       {@link PlayerAnimationEvent}. Each swing that lands on a contraption cell (re)arms a mining session
 *       for that player+cell. This works whether the crosshair is on a block_display (air, so LEFT_CLICK_AIR)
 *       OR on one of the fake INTERACTION/collider entities (an attack the server drops) — the swing fires
 *       either way, unlike the block-interact events.</li>
 *   <li><b>Progress.</b> A 1-tick task advances every live session by vanilla's own per-tick fraction
 *       ({@code digSpeed / hardness / (correctTool ? 30 : 100)}), cancels it the moment the player looks away
 *       or stops swinging, and breaks the cell when the fraction reaches 1.</li>
 *   <li><b>Animation.</b> The vanilla crack overlay ({@code ClientboundBlockDestructionPacket}) renders on the
 *       block model at a real {@code BlockPos}; over air it shows nothing, so it is useless here. Instead the
 *       cell crumbles with intensifying {@code BLOCK} break particles and ticks the block's own hit sound as
 *       it is worked, then its break sound when it goes — the same audible/visible read as real mining.</li>
 * </ul>
 */
public final class ContraptionMining implements Listener {

    private ContraptionMining() {
    }

    /**
     * Milliseconds without a swing after which a session is considered released and cancelled (progress lost,
     * as in vanilla). Generous because a held left-click's swing cadence depends on the tool's attack speed —
     * a slow pickaxe re-swings only every ~16 ticks against a non-real target — and looking away already
     * cancels instantly (the per-tick aim check), which is the common "stop mining" gesture.
     */
    private static final long RELEASE_MS = 1500L;

    /** Flags for removing a mined cell: update clients, keep the known shape, and suppress vanilla drops (we drop ourselves). */
    private static final int REMOVE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS;

    /** One player's in-progress dig against one contraption cell. */
    private static final class Session {
        final UUID contraptionId;
        final BlockPos local;
        double progress;
        long lastSwingMs;
        int tickCounter;

        Session(UUID contraptionId, BlockPos local, long nowMs) {
            this.contraptionId = contraptionId;
            this.local = local;
            this.lastSwingMs = nowMs;
        }
    }

    /** player uuid -> their single active dig. A player mines one cell at a time, like vanilla. */
    private static final Map<UUID, Session> SESSIONS = new ConcurrentHashMap<>();

    /** Guards against a creative instant-break firing twice from a double-delivered swing. */
    private static final Map<UUID, Long> LAST_CREATIVE_BREAK_MS = new ConcurrentHashMap<>();

    private static int taskId = -1;

    /** Registers the swing heartbeat and starts the per-tick progress task. Called once on enable. */
    public static void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new ContraptionMining(), plugin);
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, ContraptionMining::tickAll, 1L, 1L).getTaskId();
    }

    /**
     * Every arm swing is a potential mining heartbeat: if the player is aiming at a contraption cell, arm (or
     * re-arm) their dig against it. A creative player breaks it outright on the swing, exactly like vanilla
     * insta-mining.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onSwing(PlayerAnimationEvent event) {
        if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) {
            return;
        }
        ServerPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        Hit hit = ContraptionInteractionListener.raycast(player);
        if (hit != null) {
            armDig(player, hit);
        }
    }

    /**
     * Arms (or re-arms) a player's dig against the cell they just swung at — the shared entry point for both
     * the {@link PlayerAnimationEvent} heartbeat and {@link ContraptionInteractionListener}'s left-click path,
     * so the dig stays alive whichever event a given swing surfaces as. Breaks outright in creative.
     */
    static void armDig(ServerPlayer player, Hit hit) {
        if (hit.state().level() == null) {
            return;
        }
        BlockState state = hit.state().level().getBlockState(hit.local());
        if (state.isAir()) {
            return;
        }
        UUID playerId = player.getUUID();
        if (player.getAbilities().instabuild) {
            // Creative: one swing removes the cell (no drops, no durability, no progress) — vanilla insta-break.
            long now = System.currentTimeMillis();
            Long last = LAST_CREATIVE_BREAK_MS.get(playerId);
            if (last != null && now - last < 200L) {
                return; // a swing delivered twice in the same click must not break two cells
            }
            LAST_CREATIVE_BREAK_MS.put(playerId, now);
            breakCell(player, hit.state(), hit.local(), state, false);
            SESSIONS.remove(playerId);
            return;
        }
        // Survival: arm or re-arm the dig. A swing onto a DIFFERENT cell restarts progress on the new one.
        Session existing = SESSIONS.get(playerId);
        if (existing != null && existing.contraptionId.equals(hit.state().id()) && existing.local.equals(hit.local())) {
            existing.lastSwingMs = System.currentTimeMillis();
        } else {
            SESSIONS.put(playerId, new Session(hit.state().id(), hit.local(), System.currentTimeMillis()));
        }
    }

    /** Advances every live dig one tick: cancel on release/look-away, crumble the cell, break it when done. */
    private static void tickAll() {
        if (SESSIONS.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        for (Iterator<Map.Entry<UUID, Session>> it = SESSIONS.entrySet().iterator(); it.hasNext();) {
            Map.Entry<UUID, Session> entry = it.next();
            Session s = entry.getValue();
            if (now - s.lastSwingMs > RELEASE_MS) {
                it.remove(); // released — progress is lost, exactly like letting go mid-dig in vanilla
                continue;
            }
            org.bukkit.entity.Player bukkit = Bukkit.getPlayer(entry.getKey());
            ContraptionEntity entity = ContraptionManager.get(s.contraptionId);
            if (bukkit == null || entity == null || entity.state().level() == null) {
                it.remove();
                continue;
            }
            ServerPlayer player = ((CraftPlayer) bukkit).getHandle();
            // Must still be aiming at the SAME cell — looking away cancels the dig immediately.
            Hit hit = ContraptionInteractionListener.raycast(player);
            if (hit == null || !hit.state().id().equals(s.contraptionId) || !hit.local().equals(s.local)) {
                it.remove();
                continue;
            }
            ContraptionLevel level = entity.state().level();
            BlockState state = level.getBlockState(s.local);
            if (state.isAir()) {
                it.remove(); // already gone
                continue;
            }
            ServerLevel cLevel = level.serverLevel();
            float hardness = state.getDestroySpeed(cLevel, s.local);
            if (hardness < 0.0f) {
                it.remove(); // unbreakable (bedrock-like) — nothing to do
                continue;
            }
            float digSpeed = player.getDestroySpeed(state);
            boolean correctTool = player.hasCorrectToolForDrops(state);
            // Vanilla's own per-tick destroy fraction (ServerPlayerGameMode/Player#getDigSpeed): a wrong tool
            // is ~3.3x slower AND drops nothing (handled at break). hardness 0 (e.g. a plant) breaks instantly.
            double delta = hardness <= 0.0f ? 1.0 : digSpeed / hardness / (correctTool ? 30.0 : 100.0);
            s.progress += delta;

            // Crumble animation: a few of the block's own break particles each tick, growing with progress,
            // plus the block's hit sound roughly every quarter-second so the dig is audible while it works.
            emitCrumbs(player, level, s.local, state, s.progress);
            if (s.tickCounter++ % 5 == 0) {
                SoundType st = state.getSoundType();
                playCellSound(level, s.local, st.getHitSound(), (st.getVolume() + 1.0f) / 8.0f, st.getPitch() * 0.5f);
            }

            if (s.progress >= 1.0) {
                breakCell(player, entity.state(), s.local, state, !correctTool);
                it.remove();
            }
        }
    }

    /**
     * Removes {@code state} from the contraption, drops its loot into the real world (unless creative-driven
     * via {@code suppressDrops} = true for a wrong tool, matching vanilla's "wrong tool, no drops"), damages
     * the tool, and plays the block's break sound + a burst of particles.
     */
    private static void breakCell(ServerPlayer player, ContraptionState state, BlockPos local, BlockState blockState,
            boolean wrongTool) {
        ContraptionLevel level = state.level();
        if (level == null) {
            return;
        }
        ServerLevel cLevel = level.serverLevel();
        ServerLevel realLevel = (ServerLevel) player.level();
        Vec3 realPos = level.realWorldPositionOf(local);
        BlockPos realBlockPos = BlockPos.containing(realPos.x, realPos.y, realPos.z);
        boolean creative = player.getAbilities().instabuild;

        BlockEntity be = level.getBlockEntity(local);
        // Container contents drop too (mine a chest cell, get what was inside) — the block's own getDrops does
        // NOT include them, and our quiet removal suppresses vanilla's onRemove drop path. Skip in creative.
        if (!creative && be instanceof Container container) {
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack content = container.getItem(slot);
                if (!content.isEmpty()) {
                    Block.popResource(realLevel, realBlockPos, content.copy());
                }
            }
            container.clearContent();
        }

        // Block loot — tool/fortune/silk-touch aware, via the same overload vanilla's playerDestroy uses. A
        // wrong tool or a creative break yields nothing, exactly like vanilla.
        if (!creative && !wrongTool) {
            List<ItemStack> drops = Block.getDrops(blockState, cLevel, local, be, player, player.getMainHandItem());
            for (ItemStack drop : drops) {
                Block.popResource(realLevel, realBlockPos, drop);
            }
        }

        // Remove the cell (quiet: no neighbor cascade, no vanilla drops). setBlock on a ContraptionLevel marks
        // its cells dirty, so the engine's per-tick rebuild drops this cell's display/hitbox and the physics
        // world re-derives mass/shape next sync.
        level.setBlock(local, Blocks.AIR.defaultBlockState(), REMOVE_FLAGS);
        level.markCellsDirty();

        // Tool durability — one point per block broken, as vanilla, skipped in creative. hurtAndBreak is a
        // no-op on a non-damageable item, so no need to check the tool type first.
        if (!creative) {
            ItemStack tool = player.getMainHandItem();
            if (!tool.isEmpty()) {
                tool.hurtAndBreak(1, cLevel, player, item -> {
                });
            }
        }

        SoundType st = blockState.getSoundType();
        playCellSound(level, local, st.getBreakSound(), (st.getVolume() + 1.0f) / 2.0f, st.getPitch() * 0.8f);
        // A generous burst of the block's break particles in the real world where the cell stood.
        realLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                realPos.x, realPos.y, realPos.z, 24, 0.25, 0.25, 0.25, 0.05);
    }

    /** A handful of the block's break particles at the cell's real position, scaled up as the dig nears completion. */
    private static void emitCrumbs(ServerPlayer player, ContraptionLevel level, BlockPos local, BlockState state,
            double progress) {
        Vec3 realPos = level.realWorldPositionOf(local);
        int count = 1 + (int) (progress * 5.0);
        ((ServerLevel) player.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                realPos.x, realPos.y, realPos.z, count, 0.2, 0.2, 0.2, 0.02);
    }

    /** Plays {@code sound} centred on a cell — the ContraptionLevel maps the local position to the real world. */
    private static void playCellSound(ContraptionLevel level, BlockPos local, SoundEvent sound, float volume,
            float pitch) {
        if (sound == null) {
            return;
        }
        level.playSeededSound(null, local.getX() + 0.5, local.getY() + 0.5, local.getZ() + 0.5,
                Holder.direct(sound), SoundSource.BLOCKS, volume, pitch, 0L);
    }

    /** Drops a player's dig when they disconnect / their contraption goes away (called from the interaction cleanup, best-effort). */
    public static void forget(UUID playerId) {
        SESSIONS.remove(playerId);
        LAST_CREATIVE_BREAK_MS.remove(playerId);
    }
}
