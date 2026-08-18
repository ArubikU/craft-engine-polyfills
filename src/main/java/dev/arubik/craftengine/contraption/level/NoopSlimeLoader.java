/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.infernalsuite.asp.api.exceptions.UnknownWorldException
 *  com.infernalsuite.asp.api.loaders.SlimeLoader
 */
package dev.arubik.craftengine.contraption.level;

import com.infernalsuite.asp.api.exceptions.UnknownWorldException;
import com.infernalsuite.asp.api.loaders.SlimeLoader;
import java.util.List;

public final class NoopSlimeLoader
implements SlimeLoader {
    public static final NoopSlimeLoader INSTANCE = new NoopSlimeLoader();

    private NoopSlimeLoader() {
    }

    public byte[] readWorld(String worldName) throws UnknownWorldException {
        throw new UnknownWorldException(worldName);
    }

    public boolean worldExists(String worldName) {
        return false;
    }

    public List<String> listWorlds() {
        return List.of();
    }

    public void saveWorld(String worldName, byte[] serializedWorld) {
    }

    public void deleteWorld(String worldName) {
    }
}

