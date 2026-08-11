package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionBlockElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.util.Key;

public final class ContraptionJukeboxElement extends ContraptionBlockElement {

    private boolean isPlaying = false;
    private int ticksPlaying = 0;
    private int discLengthTicks = 0;
    private net.minecraft.sounds.SoundEvent playingSound = null;
    // Last world position sound was emitted from — re-emit if moved more than threshold
    private Vec3 lastSoundPos = null;
    private static final double RESYNC_DIST_SQ = 4.0 * 4.0; // 4 blocks before resyncing position

    public ContraptionJukeboxElement(BlockPos localPos, BlockState blockState, CompoundTag beTag) {
        super(localPos, blockState, beTag, false, 0f);
    }

    @Override public Key type() { return ElementTypes.JUKEBOX; }

    @Override
    public void tick(RenderContext ctx) {
        super.tick(ctx);
        if (!isPlaying || ctx.level() == null || !(ctx.level().realLevel() instanceof ServerLevel sl)) return;

        ticksPlaying++;

        // Stop when disc finished
        if (discLengthTicks > 0 && ticksPlaying >= discLengthTicks) {
            stopSound(sl);
            isPlaying = false;
            ticksPlaying = 0;
            return;
        }

        // Re-emit at new position if contraption moved significantly
        if (ctx.moved() && playingSound != null) {
            Vec3 bearing = ctx.bearing();
            Vec3 worldPos = ContraptionMath.renderPosition(localOffset(), bearing,
                    ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            if (lastSoundPos == null || lastSoundPos.distanceToSqr(worldPos) >= RESYNC_DIST_SQ) {
                // Stop old position sound, restart at new position
                // Client can't seek, so we restart — brief glitch but correct position
                stopSound(sl);
                sl.playSeededSound(null, worldPos.x, worldPos.y, worldPos.z,
                        playingSound, net.minecraft.sounds.SoundSource.RECORDS, 4f, 1f, 0L);
                lastSoundPos = worldPos;
            }
        }
    }

    @Override
    public boolean onInteract(ServerPlayer player, ContraptionState state, Vec3 hitPos, InteractionHand hand, boolean rightClick) {
        boolean result = super.onInteract(player, state, hitPos, hand, rightClick);
        if (rightClick) syncDiscState(state);
        return result;
    }

    /** Called after forward() — reads current jukebox disc state and starts/stops real-world sound. */
    private void syncDiscState(ContraptionState state) {
        try {
            if (!(state.level().realLevel() instanceof ServerLevel sl)) return;
            var be = state.level().getBlockEntity(localPos());
            if (!(be instanceof net.minecraft.world.level.block.entity.JukeboxBlockEntity jukebox)) return;
            ItemStack disc = jukebox.getTheItem();
            Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
            Vec3 worldPos = ContraptionMath.renderPosition(localOffset(), bearing,
                    state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale());
            if (disc.isEmpty()) {
                stopSound(sl);
                isPlaying = false;
                ticksPlaying = 0;
                playingSound = null;
                lastSoundPos = null;
            } else {
                net.minecraft.sounds.SoundEvent sound = resolveDiscSound(disc, sl);
                if (sound != null) {
                    isPlaying = true;
                    ticksPlaying = 0;
                    playingSound = sound;
                    discLengthTicks = resolveDiscLength(disc, sl);
                    lastSoundPos = worldPos;
                    sl.playSeededSound(null, worldPos.x, worldPos.y, worldPos.z,
                            sound, net.minecraft.sounds.SoundSource.RECORDS, 4f, 1f, 0L);
                }
            }
        } catch (Throwable ignored) {}
    }

    private static void stopSound(ServerLevel sl) {
        Object stopPkt = new net.minecraft.network.protocol.game.ClientboundStopSoundPacket(
                null, net.minecraft.sounds.SoundSource.RECORDS);
        for (ServerPlayer sp : sl.players()) {
            sp.connection.send((net.minecraft.network.protocol.Packet<?>) stopPkt);
        }
    }

    private static net.minecraft.sounds.SoundEvent resolveDiscSound(ItemStack disc, ServerLevel sl) {
        try {
            var playable = disc.get(net.minecraft.core.component.DataComponents.JUKEBOX_PLAYABLE);
            if (playable == null) return null;
            var song = playable.song();
            try {
                Object val = song.getClass().getMethod("value").invoke(song);
                if (val instanceof JukeboxSong js) return js.soundEvent().value();
            } catch (Throwable ignored) {}
            return song.key()
                    .flatMap(k -> sl.registryAccess()
                            .lookupOrThrow(net.minecraft.core.registries.Registries.JUKEBOX_SONG).get(k))
                    .map(h -> h.value().soundEvent().value())
                    .orElse(null);
        } catch (Throwable ignored) { return null; }
    }

    private static int resolveDiscLength(ItemStack disc, ServerLevel sl) {
        try {
            var playable = disc.get(net.minecraft.core.component.DataComponents.JUKEBOX_PLAYABLE);
            if (playable == null) return 0;
            var song = playable.song();
            try {
                Object val = song.getClass().getMethod("value").invoke(song);
                if (val instanceof JukeboxSong js) return js.lengthInTicks();
            } catch (Throwable ignored) {}
            return song.key()
                    .flatMap(k -> sl.registryAccess()
                            .lookupOrThrow(net.minecraft.core.registries.Registries.JUKEBOX_SONG).get(k))
                    .map(h -> h.value().lengthInTicks())
                    .orElse(0);
        } catch (Throwable ignored) { return 0; }
    }
}
