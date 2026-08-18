/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class ContraptionLightMap {
    private final Long2IntOpenHashMap map = new Long2IntOpenHashMap();

    public ContraptionLightMap() {
        this.map.defaultReturnValue(0);
    }

    public void bake(ContraptionLevel level) {
        this.map.clear();
        if (level == null) {
            return;
        }
        ArrayList<EmitterEntry> emitters = new ArrayList<EmitterEntry>();
        for (BlockPos pos : level.localPositions()) {
            BlockState state = level.getBlockState(pos);
            int emission = state.getLightEmission();
            if (emission <= 0) continue;
            emitters.add(new EmitterEntry(pos.immutable(), emission));
        }
        if (emitters.isEmpty()) {
            return;
        }
        for (BlockPos target : level.localPositions()) {
            int best = 0;
            for (EmitterEntry e : emitters) {
                int dist = ContraptionLightMap.chebyshev(target, e.pos);
                int contribution = e.emission - dist;
                if (contribution <= best) continue;
                best = contribution;
            }
            if (best <= 0) continue;
            this.map.put(target.asLong(), Math.min(15, best));
        }
    }

    public int getInternalLight(BlockPos local) {
        return this.map.get(local.asLong());
    }

    public int combinedBlockLight(BlockPos local, int ambientBlockLight) {
        return Math.min(15, Math.max(ambientBlockLight, this.map.get(local.asLong())));
    }

    private static int chebyshev(BlockPos a, BlockPos b) {
        return Math.max(Math.abs(a.getX() - b.getX()), Math.max(Math.abs(a.getY() - b.getY()), Math.abs(a.getZ() - b.getZ())));
    }

    private record EmitterEntry(BlockPos pos, int emission) {
    }
}

