package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.ContraptionInteractionListener;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionBlockElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.util.Key;

public final class ContraptionJukeboxElement extends ContraptionBlockElement {

    public ContraptionJukeboxElement(BlockPos localPos, BlockState blockState, CompoundTag beTag) {
        super(localPos, blockState, beTag, false, 0f);
    }

    @Override public Key type() { return ElementTypes.JUKEBOX; }

    @Override
    public boolean onInteract(ServerPlayer player, ContraptionState state, Vec3 hitPos, InteractionHand hand, boolean rightClick) {
        // Let block element handle the interaction (note block, lever, chest etc. all work via forward)
        boolean result = super.onInteract(player, state, hitPos, hand, rightClick);
        if (rightClick) playDiscSoundInRealWorld(state);
        return result;
    }

    private void playDiscSoundInRealWorld(ContraptionState state) {
        try {
            if (!(state.level().realLevel() instanceof ServerLevel sl)) return;
            var be = state.level().getBlockEntity(localPos());
            if (!(be instanceof net.minecraft.world.level.block.entity.JukeboxBlockEntity jukebox)) return;
            net.minecraft.world.item.ItemStack disc = jukebox.getTheItem();
            Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
            Vec3 worldPos = ContraptionMath.renderPosition(localOffset(), bearing,
                    state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale());
            if (disc.isEmpty()) {
                Object stopPkt = new net.minecraft.network.protocol.game.ClientboundStopSoundPacket(
                        null, net.minecraft.sounds.SoundSource.RECORDS);
                for (net.minecraft.server.level.ServerPlayer sp : sl.players()) {
                    sp.connection.send((net.minecraft.network.protocol.Packet<?>) stopPkt);
                }
            } else {
                net.minecraft.sounds.SoundEvent sound = resolveDiscSound(disc, sl);
                if (sound != null) sl.playSeededSound(null, worldPos.x, worldPos.y, worldPos.z,
                        sound, net.minecraft.sounds.SoundSource.RECORDS, 4f, 1f, 0L);
            }
        } catch (Throwable ignored) {}
    }

    private static net.minecraft.sounds.SoundEvent resolveDiscSound(net.minecraft.world.item.ItemStack disc, ServerLevel sl) {
        try {
            var playable = disc.get(net.minecraft.core.component.DataComponents.JUKEBOX_PLAYABLE);
            if (playable == null) return null;
            // Try all known EitherHolder access patterns defensively
            var song = playable.song();
            try {
                // 1.21 path: EitherHolder.value() returns Optional or direct
                Object val = song.getClass().getMethod("value").invoke(song);
                if (val instanceof net.minecraft.world.item.JukeboxSong js) return js.soundEvent().value();
            } catch (Throwable ignored) {}
            return song.key()
                    .flatMap(k -> sl.registryAccess()
                            .lookupOrThrow(net.minecraft.core.registries.Registries.JUKEBOX_SONG).get(k))
                    .map(h -> h.value().soundEvent().value())
                    .orElse(null);
        } catch (Throwable ignored) { return null; }
    }
}
