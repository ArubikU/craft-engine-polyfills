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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.Plugin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.MinecraftServer;
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
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.player.CePlayers;

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

    /**
     * How recently the player must have SWUNG for the dig to keep advancing — the "held button" test (fix for
     * Rick's "when you stop holding down the button it still mines"). The old dig was purely aim-driven: one
     * swing armed it, then merely LOOKING at the cell carried it to completion, so a single tap mined the whole
     * cell. Progress now only advances while a swing landed within this window, i.e. the button is actually held.
     *
     * <p>Sized ABOVE a slow tool's attack-cooldown cadence: the crosshair sits on the cell's SHULKER/INTERACTION
     * colliders, so a held left-click attacks them and the client gates re-swings by attack speed (a pickaxe
     * ~0.83s). A window over that never starves a genuine hold, while releasing the button — no more swings —
     * freezes the dig within ~0.9s. Crucially we FREEZE (hold progress) rather than reset on a gap, so a slow
     * cadence between swings can never lose progress ("los que no son insta break nunca terminan" regression);
     * only looking away ({@link #AIM_GRACE_TICKS}) drops it, exactly like vanilla releasing to a different block.
     */
    private static final long SWING_HOLD_MS = 900L;

    /** After a drop, ignore swings for this long — see {@link #LAST_DROP_MS}. */
    private static final long DROP_SWING_GRACE_MS = 250L;

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
        /** Real-world block the vanilla crack is currently faked onto for this dig, or null if none (see #crackTarget). */
        BlockPos crackPos;
        /** Last destroy stage (0-9) sent, or -1 if none — so a fresh stage is only sent when it actually changes. */
        int crackStage = -1;

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

    /** When each player last RIGHT-clicked a contraption (a use). A swing within a breath of one is a use-swing, not a mine. */
    private static final Map<UUID, Long> LAST_USE_MS = new ConcurrentHashMap<>();

    /**
     * When each player last DROPPED an item. Pressing Q makes the client send the DROP action packet and THEN a
     * swing packet ({@code LocalPlayer.dropItem} calls {@code swing(MAIN_HAND)}), so that swing used to arm a dig
     * and — in creative — instantly break whatever cell the player was looking at (Rick: "when you drop an item
     * against a contraption it breaks the cell you're currently looking at"). The drop packet is processed BEFORE
     * the swing, so {@link PlayerDropItemEvent} records the time here and {@link #onSwing} ignores the swing that
     * follows within {@link #DROP_SWING_GRACE_MS}.
     */
    private static final Map<UUID, Long> LAST_DROP_MS = new ConcurrentHashMap<>();

    /** Records a right-click use so the following arm-swing is not mistaken for a mining attack. Called from the interact dispatch. */
    public static void noteUse(UUID playerId) {
        LAST_USE_MS.put(playerId, System.currentTimeMillis());
    }

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
        // A swing is NOT always a left-click: the client also swings the arm on a right-click USE (place/
        // interact), so mining used to arm on right-clicks too — "el right click y left click se consideran
        // igual" (2026-07-18). The client sends the use packet BEFORE the swing, so a right-click has just
        // called noteUse; skip arming if a use landed in the last breath. Genuine left-clicks never call
        // noteUse, so they still arm.
        java.util.UUID playerId = ((CraftPlayer) event.getPlayer()).getUniqueId();
        long nowMs = System.currentTimeMillis();
        Long lastUse = LAST_USE_MS.get(playerId);
        if (lastUse != null && nowMs - lastUse < 250L) {
            return;
        }
        // A Q-drop sends the drop packet then a swing (LocalPlayer.dropItem swings) — that swing is not a mine.
        Long lastDrop = LAST_DROP_MS.get(playerId);
        if (lastDrop != null && nowMs - lastDrop < DROP_SWING_GRACE_MS) {
            return;
        }
        ServerPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        Hit hit = ContraptionInteractionListener.raycast(player);
        if (hit != null) {
            armDig(player, hit);
        }
    }

    /**
     * Records a Q-drop so the swing the client sends right after it (see {@link #LAST_DROP_MS}) does not arm a
     * dig / creative-break the looked-at cell. Fires before that swing since the client sends the drop packet
     * first. MONITOR/ignoreCancelled so a cancelled drop still suppresses its swing (the swing was still sent).
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onDrop(PlayerDropItemEvent event) {
        LAST_DROP_MS.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
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
            if (bukkit == null) {
                it.remove(); // player offline — their client is gone, nothing to clean up
                continue;
            }
            ServerPlayer player = ((CraftPlayer) bukkit).getHandle();
            ContraptionEntity entity = ContraptionManager.get(s.contraptionId);
            if (entity == null || entity.state().level() == null) {
                clearCrack(s, player); // contraption gone — revert any fake crack block on the player's client
                it.remove();
                continue;
            }
            // Continuation needs BOTH a held button AND aim on the cell (fix — Rick: "when you stop holding the
            // button it still mines"). The dig's LIFE is aim-driven (looking away for a few ticks cancels it), but
            // its PROGRESS only advances while the player keeps swinging — the held-button test below. Freezing
            // (not resetting) on a swing gap is what lets a slow attack-cooldown cadence between swings advance
            // without ever losing progress, avoiding the earlier "los que no son insta break nunca terminan"
            // regression that a hard swing-timeout cancel caused. The first swing arms the dig (see armDig).
            Hit hit = ContraptionInteractionListener.raycast(player);
            boolean onCell = hit != null && hit.state().id().equals(s.contraptionId) && hit.local().equals(s.local);
            if (!onCell) {
                if (++s.missTicks > AIM_GRACE_TICKS) {
                    clearCrack(s, player);
                    it.remove(); // looked away — progress is lost, as in vanilla
                }
                continue; // a transient raycast miss (aim jitter) is tolerated; don't advance this tick
            }
            s.missTicks = 0;
            // Held-button gate: advance only while the player is actively swinging (button held). No swing within
            // SWING_HOLD_MS means the button was released — freeze the dig here (keep progress, no break) until they
            // resume swinging or look away. This is what stops a single tap from mining the whole cell.
            if (System.currentTimeMillis() - s.lastSwingMs > SWING_HOLD_MS) {
                continue;
            }
            ContraptionLevel level = entity.state().level();
            BlockState state = level.getBlockState(s.local);
            if (state.isAir()) {
                clearCrack(s, player);
                it.remove(); // already gone
                continue;
            }
            ServerLevel cLevel = level.serverLevel();
            float hardness = state.getDestroySpeed(cLevel, s.local);
            if (hardness < 0.0f) {
                clearCrack(s, player);
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

            // Real vanilla destroy-stage crack, when possible. The overlay renders on whatever block the CLIENT
            // has at a real BlockPos, and a contraption cell is a packet-only display over air — so we send the
            // player a FAKE real block there for the crack to land on, then the stage. Only when the cell is
            // grid-aligned, unrotated, scale-1 and over real air (see #crackTarget) does the axis-aligned fake
            // block line up with the display; a rotated/moving/scaled cell returns null and rides on the
            // particle crumble alone. Per-player packet — nobody else sees the fake block.
            BlockPos ct = crackTarget(entity.state(), level, s.local, (ServerLevel) player.level());
            if (ct != null) {
                if (!ct.equals(s.crackPos)) {
                    clearCrack(s, player); // dig walked onto a new aligned cell/pos — revert the old fake block
                    player.connection.send(new ClientboundBlockUpdatePacket(ct, state));
                    s.crackPos = ct;
                }
                int stage = Math.min(9, (int) (s.progress * 10.0));
                if (stage != s.crackStage) {
                    player.connection.send(new ClientboundBlockDestructionPacket(player.getId(), ct, stage));
                    s.crackStage = stage;
                }
            } else {
                clearCrack(s, player); // no longer eligible (started rotating/moving) — fall back to particles
            }

            if (s.progress >= 1.0) {
                breakCell(player, entity.state(), s.local, state, !correctTool, faceWorld);
                clearCrack(s, player);
                it.remove();
            }
        }
    }

    /**
     * Removes {@code state} from the contraption, drops its loot into the real world (unless creative-driven
     * via {@code suppressDrops} = true for a wrong tool, matching vanilla's "wrong tool, no drops"), damages
     * the tool, and plays the block's break sound + a burst of particles.
     */
    /**
     * Breaks every contraption cell that can no longer survive after {@code origin} was removed, climbing the
     * stack (bamboo, sugar cane, torches, rails …). Replaces the vanilla scheduled-tick cascade, which never runs
     * in the hidden level (it isn't in the server's level-tick loop). Each broken cell drops its loot in the real
     * world at its own cell centre; a guard + seen-set bound the walk.
     */
    private static void cascadeUnsupported(ContraptionLevel level, ServerLevel cLevel, ServerLevel realLevel,
            BlockPos origin) {
        java.util.ArrayDeque<BlockPos> queue = new java.util.ArrayDeque<>();
        java.util.Set<BlockPos> seen = new java.util.HashSet<>();
        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
            queue.add(origin.relative(d));
        }
        int guard = 0;
        while (!queue.isEmpty() && guard++ < 4096) {
            BlockPos p = queue.poll();
            if (!seen.add(p)) {
                continue;
            }
            BlockState s = level.getBlockState(p);
            if (s.isAir()) {
                continue;
            }
            boolean survives;
            try {
                survives = s.canSurvive(cLevel, p);
            } catch (Throwable t) {
                survives = true; // a block whose survival check needs context we can't give — leave it be
            }
            if (survives) {
                continue; // still supported (a normal block, or a plant whose base is intact)
            }
            Vec3 c = level.realWorldPositionOf(new Vec3(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5));
            BlockPos realPos = BlockPos.containing(c.x, c.y, c.z);
            try {
                for (ItemStack drop : Block.getDrops(s, cLevel, p, level.getBlockEntity(p))) {
                    Block.popResource(realLevel, realPos, drop);
                }
            } catch (Throwable ignored) {
                // no-loot block, or a drop that needs a tool context — still remove it below
            }
            level.setBlock(p, Blocks.AIR.defaultBlockState(), REMOVE_FLAGS);
            for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
                queue.add(p.relative(d));
            }
        }
    }

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

        // Block loot. A CraftEngine CUSTOM block (a captured machine, a decorative custom block) must drop its
        // OWN item, not the loot of the vanilla base state it is painted onto (2026-07-18) — Block.getDrops on
        // the raw BlockState would hand back a note block / mushroom stem / nothing. So resolve the custom block
        // and drop its item form; only a genuine vanilla block falls to the tool/fortune/silk-aware getDrops. A
        // wrong tool or a creative break yields nothing, exactly like vanilla.
        if (!creative && !wrongTool) {
            net.momirealms.craftengine.core.block.ImmutableBlockState ce =
                    net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(blockState).orElse(null);
            if (ce != null && !ce.isEmpty()) {
                ItemStack customDrop = ceBlockDrop(ce);
                if (customDrop != null && !customDrop.isEmpty()) {
                    Block.popResource(realLevel, realBlockPos, customDrop);
                }
            } else {
                List<ItemStack> drops = Block.getDrops(blockState, cLevel, local, be, player, player.getMainHandItem());
                for (ItemStack drop : drops) {
                    Block.popResource(realLevel, realBlockPos, drop);
                }
            }
        }

        // Remove the cell (quiet: no neighbor cascade, no vanilla drops). setBlock on a ContraptionLevel marks
        // its cells dirty, so the engine's per-tick rebuild drops this cell's display/hitbox and the physics
        // world re-derives mass/shape next sync.
        level.setBlock(local, Blocks.AIR.defaultBlockState(), REMOVE_FLAGS);
        // Cascade the support loss up the stack (user: "al romper un bambú sigue sin romperse todos los bambús").
        // Vanilla would do this through SCHEDULED block ticks (bamboo's neighborChanged schedules a tick that
        // then checks canSurvive), but the hidden contraption level is NOT in the server's level-tick loop, so
        // those scheduled ticks never fire — updateNeighborsAt alone does nothing. So we walk it ourselves: any
        // neighbour cell that can no longer survive (bamboo/sugar-cane/torch/rail with its support gone) is
        // broken here, its loot dropped in the real world, and its own neighbours enqueued — climbing the whole
        // bamboo tower in one pass. updateNeighborsAt is still fired for the (rare) support-dependent block that a
        // future ticking path might handle, but the manual cascade is what actually clears the stack.
        cascadeUnsupported(level, cLevel, realLevel, local);
        level.updateNeighborsAt(local, blockState.getBlock());
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
            MinecraftServer server = ((org.bukkit.craftbukkit.CraftServer) Bukkit.getServer()).getServer();
            ServerLevel serverLevel = server.getLevel(state.worldId());
            org.bukkit.World bukkitWorld = serverLevel != null ? serverLevel.getWorld() : null;
            if (entity != null) {
                entity.despawn(bukkitWorld == null ? List.of() : CePlayers.resolve(bukkitWorld.getPlayers()));
            }
            ContraptionManager.remove(state.id());
            dev.arubik.craftengine.contraption.physics.PhysicsWorld.remove(state.id());
            dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.delete(state.id());
            dev.arubik.craftengine.contraption.listener.BearingHammerListener.forgetAssembled(state.id());
            ContraptionLevel level = state.level();
            if (level != null) {
                level.dispose();
            }
        } catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption] mining teardown of emptied contraption failed: " + t);
        }
    }

    /**
     * The real-world block a cell's vanilla crack overlay can be faked onto, or {@code null} if the cell is
     * not eligible. Eligible only when the contraption is unrotated (pitch/roll ≈ 0), unscaled, and the cell's
     * display cube lines up with a block-grid cell over real AIR — i.e. its centre sits at a block centre — so
     * that an axis-aligned fake block matches the display. A rotated/moving/scaled cell, or one whose grid cell
     * holds a real block, returns {@code null} (particle-only). Yaw is not tested directly: any yaw that keeps
     * the cube on the grid (multiples of 90°, cube-preserving) still lands the centre on a block centre, and
     * any other yaw shifts it off-centre and fails the check.
     */
    private static BlockPos crackTarget(ContraptionState state, ContraptionLevel level, BlockPos local,
            ServerLevel realLevel) {
        if (Math.abs(state.pitchRadians()) > 1.0E-3 || Math.abs(state.rollRadians()) > 1.0E-3
                || Math.abs(state.scale() - 1.0) > 1.0E-3) {
            return null;
        }
        Vec3 centre = level.realWorldPositionOf(new Vec3(local.getX() + 0.5, local.getY() + 0.5, local.getZ() + 0.5));
        BlockPos bp = BlockPos.containing(centre.x, centre.y, centre.z);
        if (Math.abs(centre.x - (bp.getX() + 0.5)) > 0.05 || Math.abs(centre.y - (bp.getY() + 0.5)) > 0.05
                || Math.abs(centre.z - (bp.getZ() + 0.5)) > 0.05) {
            return null; // display cube not aligned with a grid cell — the fake block would be offset
        }
        return realLevel.getBlockState(bp).isAir() ? bp : null;
    }

    /** Clears a dig's faked crack: removes the destroy-stage overlay and reverts the fake block to the real world's state. */
    private static void clearCrack(Session s, ServerPlayer player) {
        if (s.crackPos != null && player != null) {
            player.connection.send(new ClientboundBlockDestructionPacket(player.getId(), s.crackPos, -1));
            BlockState real = ((ServerLevel) player.level()).getBlockState(s.crackPos);
            player.connection.send(new ClientboundBlockUpdatePacket(s.crackPos, real));
        }
        s.crackPos = null;
        s.crackStage = -1;
    }

    /** The item a captured CraftEngine custom block drops when mined — its own block-item, not the vanilla base's loot; null if unresolvable. */
    private static ItemStack ceBlockDrop(net.momirealms.craftengine.core.block.ImmutableBlockState ce) {
        try {
            net.momirealms.craftengine.core.util.Key id = ce.owner().value().id();
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(id);
            if (def == null) {
                return null;
            }
            org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
            return bukkit == null ? null : org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
        } catch (Throwable t) {
            return null;
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
        LAST_USE_MS.remove(playerId);
        LAST_DROP_MS.remove(playerId);
    }
}
