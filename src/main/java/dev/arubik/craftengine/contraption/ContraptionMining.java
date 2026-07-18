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
import net.minecraft.world.InteractionHand;
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
     * Consecutive ticks the player may look OFF the cell before the dig is cancelled and its progress lost.
     * A small grace absorbs raycast jitter (aim wobbling across a cell edge, a moving contraption) without
     * dropping the dig, while a genuine look-away still cancels within a fifth of a second.
     */
    private static final int AIM_GRACE_TICKS = 4;

    /** Flags for removing a mined cell: update clients, keep the known shape, and suppress vanilla drops (we drop ourselves). */
    private static final int REMOVE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS;

    /** One player's in-progress dig against one contraption cell. */
    private static final class Session {
        final UUID contraptionId;
        final BlockPos local;
        double progress;
        long lastSwingMs;
        int tickCounter;
        int missTicks;

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
            Vec3 faceWorld = hit.state().level().realWorldPositionOf(hit.localClip());
            breakCell(player, hit.state(), hit.local(), state, false, faceWorld);
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

    /** Advances every live dig one tick: cancel on look-away, crumble the cell, break it when done. */
    private static void tickAll() {
        if (SESSIONS.isEmpty()) {
            return;
        }
        for (Iterator<Map.Entry<UUID, Session>> it = SESSIONS.entrySet().iterator(); it.hasNext();) {
            Map.Entry<UUID, Session> entry = it.next();
            Session s = entry.getValue();
            org.bukkit.entity.Player bukkit = Bukkit.getPlayer(entry.getKey());
            ContraptionEntity entity = ContraptionManager.get(s.contraptionId);
            if (bukkit == null || entity == null || entity.state().level() == null) {
                it.remove();
                continue;
            }
            ServerPlayer player = ((CraftPlayer) bukkit).getHandle();
            // Continuation is AIM-driven, not swing-cadence-driven (2026-07-17 — "los que no son insta break
            // nunca terminan de romperse"): a held left-click against a packet-only cell does NOT reliably send
            // continuous swings (the client attack-cooldown-gates re-swings on a fake target, and a wrong-tool
            // dig takes several seconds), so keying the dig's life on recent swings killed it long before it
            // finished. Instead the dig lives as long as the player keeps AIMING at the cell — the first swing
            // arms it (see armDig), then aim carries it to completion. Looking away for a few ticks cancels it.
            Hit hit = ContraptionInteractionListener.raycast(player);
            boolean onCell = hit != null && hit.state().id().equals(s.contraptionId) && hit.local().equals(s.local);
            if (!onCell) {
                if (++s.missTicks > AIM_GRACE_TICKS) {
                    it.remove(); // looked away — progress is lost, as in vanilla
                }
                continue; // a transient raycast miss (aim jitter) is tolerated; don't advance this tick
            }
            s.missTicks = 0;
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

            // Particles come out of the FACE the player is aiming at — the exact ray hit point, mapped to the
            // real world (2026-07-17 — "las partículas salen en una esquina").
            Vec3 faceWorld = level.realWorldPositionOf(hit.localClip());
            // Animate the arm while mining. The first-person swing is client-predicted, but broadcasting the
            // swing keeps the player visibly working to everyone tracking them; the internal swing-timer gates
            // the cadence so calling it every tick just sustains a continuous mining swing.
            player.swing(InteractionHand.MAIN_HAND, true);
            // Crumble animation: a few of the block's own break particles each tick, growing with progress,
            // plus the block's hit sound roughly every quarter-second so the dig is audible while it works.
            emitCrumbs((ServerLevel) player.level(), faceWorld, state, s.progress);
            if (s.tickCounter++ % 5 == 0) {
                SoundType st = state.getSoundType();
                playCellSound(level, s.local, st.getHitSound(), (st.getVolume() + 1.0f) / 8.0f, st.getPitch() * 0.5f);
            }

            if (s.progress >= 1.0) {
                breakCell(player, entity.state(), s.local, state, !correctTool, faceWorld);
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
            boolean wrongTool, Vec3 faceWorld) {
        ContraptionLevel level = state.level();
        if (level == null) {
            return;
        }
        ServerLevel cLevel = level.serverLevel();
        ServerLevel realLevel = (ServerLevel) player.level();
        // Drops pop from the cell CENTRE (its real-world position), particles burst from the FACE the player
        // was aiming at (the exact ray hit point) — not the cell's min corner, which is what realWorldPositionOf
        // of the raw BlockPos gives (2026-07-17 — "las partículas salen en una esquina").
        Vec3 centerWorld = level.realWorldPositionOf(new Vec3(local.getX() + 0.5, local.getY() + 0.5, local.getZ() + 0.5));
        BlockPos realBlockPos = BlockPos.containing(centerWorld.x, centerWorld.y, centerWorld.z);
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
        // A generous burst of the block's break particles at the aimed face.
        realLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                faceWorld.x, faceWorld.y, faceWorld.z, 24, 0.2, 0.2, 0.2, 0.05);

        // Mine the LAST block of a contraption and the (now empty) structure is torn down (2026-07-17 — user:
        // "al romper todos los bloques ... se borra no?"). refreshLocalPositions() rebuilds the cell set from
        // the level's non-air blocks; if nothing solid is left, remove the contraption outright — its blocks
        // are all gone as drops, so there is nothing to restore. A bearing's real anchor block is not a cell,
        // so it stays behind, un-assembled.
        level.refreshLocalPositions();
        boolean empty = true;
        for (BlockPos p : level.localPositions()) {
            if (!level.getBlockState(p).isAir()) {
                empty = false;
                break;
            }
        }
        if (empty) {
            teardownEmpty(state);
        }
    }

    /**
     * Removes a contraption that has had its last cell mined away — the same teardown a TNT detonation uses,
     * minus the blast: despawn its packet-only render/colliders, unregister it from the manager, physics, and
     * persistence, clear its assembled marker, and dispose the hidden level. Never restores blocks (they left
     * as drops). Best-effort — a teardown hiccup must not throw back into the mining tick.
     */
    private static void teardownEmpty(ContraptionState state) {
        try {
            ContraptionEntity entity = ContraptionManager.get(state.id());
            org.bukkit.World bukkitWorld = Bukkit.getWorld(state.worldId());
            if (entity != null) {
                entity.despawn(bukkitWorld == null ? List.of() : CePlayers.resolve(bukkitWorld.getPlayers()));
            }
            ContraptionManager.remove(state.id());
            dev.arubik.craftengine.contraption.physics.PhysicsWorld.remove(state.id());
            dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.delete(state.id());
            dev.arubik.craftengine.contraption.BearingHammerListener.forgetAssembled(state.id());
            ContraptionLevel level = state.level();
            if (level != null) {
                level.dispose();
            }
        } catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption] mining teardown of emptied contraption failed: " + t);
        }
    }

    /** A handful of the block's break particles at the aimed face, scaled up as the dig nears completion. */
    private static void emitCrumbs(ServerLevel realLevel, Vec3 faceWorld, BlockState state, double progress) {
        int count = 1 + (int) (progress * 5.0);
        realLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                faceWorld.x, faceWorld.y, faceWorld.z, count, 0.12, 0.12, 0.12, 0.02);
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
