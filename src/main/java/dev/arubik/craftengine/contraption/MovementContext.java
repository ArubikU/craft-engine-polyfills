/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.minecraft.server.level.ServerLevel;

public record MovementContext(ContraptionState state, ServerLevel level) {
}

