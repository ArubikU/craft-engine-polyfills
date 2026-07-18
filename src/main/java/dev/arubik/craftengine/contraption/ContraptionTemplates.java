package dev.arubik.craftengine.contraption;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore;
import dev.arubik.craftengine.contraption.persistence.ContraptionStructureNbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.entity.player.Player;

/**
 * Public "save contraption to item / spawn from item" template facade (user request: "saveToItem
 * contraption api, it deletes(flag) the current contraption and creates a
 * {@code <id>.template.nbt} so when any plugin uses fromItem or Contraption.ofItem it can create a
 * copy of it").
 *
 * <p>Unlike every OTHER persistence path in this package — {@link MinecartBearing}'s entity-PDC and
 * {@link BlockAnchoredContraptionStore}'s per-contraption {@code .dat}, both of which are ANCHORED
 * bookkeeping tied to one live bearing that is re-hydrated in place — a template is a detached,
 * anchor-less, reusable SNAPSHOT. The same file can spawn an unlimited number of independent copies
 * (each a fresh {@link ContraptionState}/UUID), the source contraption is torn down when it's saved,
 * and the file is never consumed by a spawn. It is purely a copy medium, so it lives in its own
 * {@code contraptions/templates/} subfolder rather than alongside the live {@code .dat} files.
 *
 * <p><b>Serialization is 100% reused, not reinvented.</b> The heavy structure blob is exactly
 * {@link ContraptionStructureNbt#dump}/{@link ContraptionStructureNbt#load} — the same gzip'd
 * vanilla {@code CompoundTag} (blocks + block-entities + glue topology + CraftEngine furniture
 * metadata) that {@link BlockAnchoredContraptionStore}/{@link MinecartBearing} already ride on. The
 * live-contraption reconstruction in {@link #ofItem} mirrors
 * {@link BlockAnchoredContraptionStore#rehydrate} / {@link MinecartBearing#rehydrate}
 * step-for-step (create empty {@link ContraptionLevel}, {@code load} the blob into it, re-place
 * captured furniture via {@link ContraptionFurnitureCapture#restoreIntoFakeLevel}, re-derive
 * per-block movement behaviors via {@link ContraptionCapture#resolveAutoBehaviors}, register a new
 * {@link ContraptionEntity}) — the ONE deliberate difference being a brand-new random UUID per
 * spawn (a template is a copy source, so every spawn must be a distinct contraption, never the
 * saved one's identity).
 *
 * <p><b>What round-trips</b>: everything {@link ContraptionStructureNbt} carries — every captured
 * block's state, each block-entity's full NBT (chest contents, furnace burn, etc.), the internal
 * glue topology, and CraftEngine furniture (definition/variant/local-offset/yaw). Auto-attachable
 * movement behaviors (miners, etc.) are re-derived from the restored blocks exactly as a
 * chunk-load rehydrate does. <b>What does NOT round-trip</b> (same limits as every other
 * structure-NBT consumer here): the bearing's live kinematic transform/rpm/su and any seated
 * riders — a spawned copy starts fresh at the given position with no drivers beyond the
 * auto-derived ones, which is the correct behavior for a "stamp out a fresh copy" template anyway
 * (see the limitations note at the end of the class).
 */
public final class ContraptionTemplates {

    private ContraptionTemplates() {
    }

    private static NamespacedKey key(String name) {
        return new NamespacedKey(CraftEnginePolyfills.instance(), name);
    }

    /** PDC key carrying the template file id on a saved item — see {@link #saveToItem}/{@link #ofItem}. */
    private static final NamespacedKey TEMPLATE_ID = key("contraption_template");

    /** Default base material for a freshly-saved template item when a caller doesn't specify one. */
    private static final Material DEFAULT_MATERIAL = Material.PAPER;

    private static Path templatesDir() {
        return CraftEnginePolyfills.instance().getDataFolder().toPath().resolve("contraptions").resolve("templates");
    }

    private static Path templateFile(String templateId) {
        return templatesDir().resolve(templateId + ".template.nbt");
    }

    // ---- save ----

    /**
     * Dumps {@code entity}'s current structure to a fresh {@code <uuid>.template.nbt}, tears the
     * live contraption down WITHOUT restoring its real blocks (it vanishes — see {@link #vanish}),
     * and returns a {@link #DEFAULT_MATERIAL} item carrying the new template id in its PDC so a
     * later {@link #ofItem} can stamp out copies. Returns {@code null} if the contraption has no
     * live level (a pure-kinematics test stub) or the dump fails.
     */
    public static ItemStack saveToItem(ContraptionEntity entity) {
        return saveToItem(entity, DEFAULT_MATERIAL);
    }

    /** {@link #saveToItem(ContraptionEntity)} with an explicit base material for the returned item. */
    public static ItemStack saveToItem(ContraptionEntity entity, Material baseMaterial) {
        if (entity == null || entity.state() == null || entity.state().level() == null) {
            return null;
        }
        ContraptionState state = entity.state();
        String templateId = UUID.randomUUID().toString();
        try {
            Files.createDirectories(templatesDir());
            // Reuse the exact structure-dump primitive every other persistence path uses — blocks +
            // block-entities + glue + furniture, gzip'd vanilla CompoundTag (ContraptionStructureNbt).
            ContraptionStructureNbt.save(state.level(), templateFile(templateId));
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to save contraption template " + templateId + ": " + t);
            return null;
        }
        // The source contraption is consumed by the save (the user's "it deletes(flag) the current
        // contraption"): vanish it from the world — same teardown as disassemble, minus block restore.
        World world = Bukkit.getWorld(state.worldId());
        if (world != null) {
            vanish(world, entity);
        }
        ItemStack item = new ItemStack(baseMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(TEMPLATE_ID, PersistentDataType.STRING, templateId);
            item.setItemMeta(meta);
        }
        return item;
    }

    // ---- spawn ----

    /**
     * Reads the template id from {@code item}'s PDC, loads its {@code <id>.template.nbt}, builds a
     * FRESH {@link ContraptionLevel} populated from it (a COPY — the file is never consumed, so the
     * same item can spawn any number of copies), registers a NEW {@link ContraptionEntity} anchored
     * at {@code (x,y,z)} in {@code world}, and returns it. Returns {@code null} if the item carries
     * no template tag ({@link #isTemplateItem} is false) or the template file is missing/unreadable.
     */
    public static ContraptionEntity ofItem(ItemStack item, World world, double x, double y, double z) {
        String templateId = templateId(item);
        if (templateId == null || world == null) {
            return null;
        }
        Path file = templateFile(templateId);
        if (!Files.isRegularFile(file)) {
            return null;
        }
        CompoundTag structure;
        try {
            structure = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to read contraption template " + templateId + ": " + t);
            return null;
        }
        try {
            Level realLevel = ((CraftWorld) world).getHandle();
            // Mirror BlockAnchoredContraptionStore#rehydrate / MinecartBearing#rehydrate: capture
            // always creates the level at yaw 0; the copy starts at the requested position.
            ContraptionLevel level = ContraptionLevel.create(realLevel, x, y, z, 0);
            ContraptionStructureNbt.load(level, structure);
            // Fresh UUID — a template is a COPY source, so each spawn is a distinct contraption, never
            // the identity of whatever contraption was originally saved.
            ContraptionState state = new ContraptionState(UUID.randomUUID(), world.getUID(), level, x, y, z);
            state.setFurniture(ContraptionFurnitureCapture.restoreIntoFakeLevel(level, level.furnitureRecords()));
            for (MovementBehavior autoBehavior : ContraptionCapture.resolveAutoBehaviors(level)) {
                state.addBehavior(autoBehavior);
            }
            return ContraptionManager.register(new ContraptionEntity(state));
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to spawn contraption from template " + templateId + ": " + t);
            return null;
        }
    }

    /** Alias for {@link #ofItem} (the user's "fromItem" spelling). */
    public static ContraptionEntity fromItem(ItemStack item, World world, double x, double y, double z) {
        return ofItem(item, world, x, y, z);
    }

    // ---- helpers ----

    /** Whether {@code item} carries a contraption template id in its PDC (see {@link #saveToItem}). */
    public static boolean isTemplateItem(ItemStack item) {
        return templateId(item) != null;
    }

    /** The template id stored on {@code item}, or {@code null} if it isn't a template item. */
    private static String templateId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.get(TEMPLATE_ID, PersistentDataType.STRING);
    }

    /**
     * Tears the live contraption down WITHOUT restoring its real blocks — the "flag/delete, it
     * vanishes" half of {@link #saveToItem}. Mirrors {@link ContraptionAssembler#disassemble}'s
     * teardown (drop on-disk/assembled bookkeeping, despawn every render swarm, drop from the
     * manager, rescue stray entities, dispose the mini-dimension) but deliberately SKIPS the
     * grid-snap/collision-check/restore step — the structure now lives only in the template file.
     * For a MINECART bearing the real anchor minecart entity is removed too (its structure has been
     * captured into the template; leaving it would orphan an assembled-but-empty bearing).
     */
    private static void vanish(World bukkitWorld, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        // Drop the block-anchored on-disk persistence + assembled-anchor bookkeeping first, so a later
        // chunk load/unload can never resurrect or re-save the contraption we're about to delete.
        BlockAnchoredContraptionStore.delete(state.id());
        BearingHammerListener.forgetAssembled(state.id());
        // Remove the MINECART anchor entity, if any — its structure is now in the template file.
        for (MovementBehavior behavior : state.behaviors()) {
            if (behavior instanceof MinecartFollowBehavior follow) {
                org.bukkit.entity.Entity anchor = Bukkit.getEntity(follow.entityId());
                if (anchor != null) {
                    anchor.remove();
                }
            }
        }
        // Full despawn INCLUDING the hitbox swarm: unlike disassemble there is no real-block restore
        // to follow, so the "don't despawn hitboxes until the blocks are back" ordering (see
        // ContraptionEntity#despawnRest/#despawnHitboxesOnly) doesn't apply here.
        List<Player> viewers = CePlayers.resolve(bukkitWorld.getPlayers());
        entity.despawn(viewers);
        ContraptionManager.remove(state.id());
        if (state.level() != null) {
            // Rescue any real entity that wandered into the mini-dimension before discarding it (same
            // call disassemble/onDisable use) so it isn't silently deleted with the level.
            state.level().transferRemainingEntitiesToRealWorld();
            state.level().dispose();
        }
    }
}
