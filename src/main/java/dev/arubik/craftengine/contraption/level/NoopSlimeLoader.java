package dev.arubik.craftengine.contraption.level;

import java.util.List;

import com.infernalsuite.asp.api.exceptions.UnknownWorldException;
import com.infernalsuite.asp.api.loaders.SlimeLoader;

/**
 * A {@link SlimeLoader} that persists nothing — a contraption's ASP world lives only in memory and is
 * thrown away on dispose (its real persistence is the structure NBT on the bearing, exactly as with
 * {@link BukkitContraptionLevel}). {@code createEmptyWorld} needs a loader; this is the "no disk" one.
 *
 * <p>Only referenced from {@code AspContraptionLevel}, which is itself loaded only when the ASP API is on
 * the classpath, so a plain Paper server never touches this class.
 */
public final class NoopSlimeLoader implements SlimeLoader {

    public static final NoopSlimeLoader INSTANCE = new NoopSlimeLoader();

    private NoopSlimeLoader() {
    }

    @Override
    public byte[] readWorld(String worldName) throws UnknownWorldException {
        throw new UnknownWorldException(worldName); // nothing is ever stored to read back
    }

    @Override
    public boolean worldExists(String worldName) {
        return false;
    }

    @Override
    public List<String> listWorlds() {
        return List.of();
    }

    @Override
    public void saveWorld(String worldName, byte[] serializedWorld) {
        // no-op — a contraption world is never written to disk
    }

    @Override
    public void deleteWorld(String worldName) {
        // no-op — nothing to delete
    }
}
