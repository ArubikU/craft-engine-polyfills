package dev.arubik.craftengine.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

/**
 * A plain global key -&gt; value store, scoped to the whole server rather than any one block,
 * player, or world — the generic primitive behind {@code Server.get_flag}/{@code set_flag}/
 * {@code get_str_flag}/{@code set_str_flag} (see {@code ServerType}).
 *
 * <p>Exists for state that genuinely belongs to neither a block (Machine.*_flag), an entity
 * (Entity.*_flag, PersistentDataContainer-backed), nor a world (World.*_flag, also PDC-backed) —
 * a cross-dimension registry (frequencies, global counters, feature toggles) is the textbook case.
 * Deliberately just a flat map: any structure beyond "one string key, one string/int value" (a
 * list of linked positions, say) is the calling SCRIPT's job to encode/decode — see {@code
 * ScriptFormula}'s {@code split}/{@code join} — not something this class should grow bespoke
 * shapes for. That is what keeps this reusable instead of turning into another one-off registry.
 *
 * <p>Persistence mirrors {@code GlueRegistry}/{@code ChainRegistry} exactly (one compressed NBT
 * file), wired into {@code CraftEnginePolyfills#onEnable}/{@code #onDisable}.
 */
public final class ServerFlags {

    private static final Map<String, Integer> INTS = new HashMap<>();
    private static final Map<String, String> STRINGS = new HashMap<>();

    private ServerFlags() {
    }

    public static int getInt(String key) {
        return INTS.getOrDefault(key, 0);
    }

    public static void setInt(String key, int value) {
        INTS.put(key, value);
    }

    public static String getStr(String key) {
        return STRINGS.getOrDefault(key, "");
    }

    public static void setStr(String key, String value) {
        if (value == null || value.isEmpty())
            STRINGS.remove(key);
        else
            STRINGS.put(key, value);
    }

    public static void saveAll(Path file) throws IOException {
        CompoundTag root = new CompoundTag();
        CompoundTag ints = new CompoundTag();
        for (Map.Entry<String, Integer> e : INTS.entrySet())
            ints.putInt(e.getKey(), e.getValue());
        CompoundTag strings = new CompoundTag();
        for (Map.Entry<String, String> e : STRINGS.entrySet())
            strings.putString(e.getKey(), e.getValue());
        root.put("ints", ints);
        root.put("strings", strings);
        Files.createDirectories(file.getParent(), new FileAttribute[0]);
        NbtIo.writeCompressed(root, file);
    }

    public static void loadAll(Path file) throws IOException {
        if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS))
            return;
        CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
        CompoundTag ints = root.getCompoundOrEmpty("ints");
        for (String key : ints.keySet())
            ints.getInt(key).ifPresent(v -> INTS.put(key, v));
        CompoundTag strings = root.getCompoundOrEmpty("strings");
        for (String key : strings.keySet())
            strings.getString(key).ifPresent(v -> STRINGS.put(key, v));
    }
}
