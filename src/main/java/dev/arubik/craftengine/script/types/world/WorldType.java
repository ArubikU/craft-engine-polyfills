package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.script.types.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;

public final class WorldType {

    // World.get_typed's own namespace within ServerFlags.TYPED, keyed by dimension id — see
    // worldTypedKey. Kept distinct from a bare Server.get_typed key of the same name.
    private static final String TYPED_PREFIX = "typed_";

    private WorldType() {}

    public static void register() {
        PolyTypeRegistry.define("World")
            .property("time",         obj -> ScriptValue.of(level(obj).getDayTime()))
            .property("day_time",     obj -> ScriptValue.of(level(obj).getDayTime() % 24000))
            .property("is_day",       obj -> ScriptValue.of(level(obj).getDayTime() % 24000 < 12000))
            .property("is_night",     obj -> ScriptValue.of(level(obj).getDayTime() % 24000 >= 12000))
            .property("is_raining",   obj -> ScriptValue.of(level(obj).isRaining()))
            .property("is_thundering",obj -> ScriptValue.of(level(obj).isThundering()))
            .property("name",         obj -> ScriptValue.of(level(obj).dimension().identifier().toString()))
            .property("seed",         obj -> ScriptValue.of((double) level(obj).getSeed()))
            // location(x, y, z) -> Location IN THIS world — the missing link for anything that
            // resolved a target World via world(name) (see ScriptFormula) and now needs an actual
            // Location to hand to Entity.teleport_to (cross-dimension teleport).
            .methodTyped3("location", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (ServerLevel obj, Double x, Double y, Double z) -> LocationType.wrap(obj, x, y, z))
            // get_block(x, y, z, force_load?) — force_load (default false) synchronously loads
            // the chunk first if it isn't already, for a caller that needs a reliable read of a
            // possibly-distant position (e.g. reading a sign on a destination teleporter that may
            // be far from any online player) instead of silently seeing air — plain NMS
            // getBlockState/getBlockEntity on an unloaded chunk returns air without loading
            // anything, the same "reads air on an unready chunk" gotcha ContraptionType's
            // ensureChunkReady already works around for contraption virtual chunks. Left opt-in
            // (default false) since forcing a load has a real one-time I/O/generation cost that a
            // frequent, non-critical get_block call shouldn't pay unconditionally.
            // NOT migrated to a typed method: force_load is a genuinely optional 4th argument whose
            // presence is checked via args.size() >= 4 — the handler still runs (and returns the
            // block) when it's omitted, it just skips the getChunkAt() load; onMissingArgs would
            // instead SKIP the handler entirely below arity, which isn't the same behavior. Left
            // untyped (same reasoning as ContraptionType.teleport's NOT-migrated note).
            .method("get_block", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                int x = (int) args.get(0).asNum(), y = (int) args.get(1).asNum(), z = (int) args.get(2).asNum();
                net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(x, y, z);
                if (args.size() >= 4 && args.get(3).asBool()) {
                    try { level(obj).getChunkAt(pos); } catch (Throwable ignored) {}
                }
                return BlockType.wrap(level(obj), pos);
            })
            .methodTyped3("get_light", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.of(0),
                (ServerLevel obj, Double xArg, Double yArg, Double zArg) -> {
                    int x = xArg.intValue(), y = yArg.intValue(), z = zArg.intValue();
                    return ScriptValue.of(obj.getMaxLocalRawBrightness(new net.minecraft.core.BlockPos(x, y, z)));
                })
            // spawn_entity(type, x, y, z) → Entity
            .methodTyped4("spawn_entity", TypeCodecs.STRING, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (ServerLevel obj, String typeId, Double xArg, Double yArg, Double zArg) -> {
                    double x = xArg, y = yArg, z = zArg;
                    try {
                        var optType = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(typeId.contains(":") ? typeId : "minecraft:" + typeId));
                        if (optType.isEmpty()) return ScriptValue.NULL;
                        net.minecraft.world.entity.Entity spawned = optType.get().create(obj, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                        if (spawned == null) return ScriptValue.NULL;
                        spawned.setPos(x, y, z);
                        obj.addFreshEntity(spawned);
                        return EntityType.wrap(spawned);
                    } catch (Throwable e) { return ScriptValue.NULL; }
                })
            // set_block(x, y, z, blockId)
            .methodTyped4("set_block", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (ServerLevel obj, Double xArg, Double yArg, Double zArg, String id) -> {
                    int x = xArg.intValue(), y = yArg.intValue(), z = zArg.intValue();
                    try {
                        var block = (net.minecraft.world.level.block.Block) BuiltInRegistries.BLOCK.getValue(
                            Identifier.parse(id.contains(":") ? id : "minecraft:" + id));
                        if (block == null) return false;
                        obj.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), 3);
                        return true;
                    } catch (Throwable e) { return false; }
                })
            // entities_in_range(x, y, z, radius) → Array<Entity>
            .methodTyped4("entities_in_range", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW,
                new ScriptValue.Array(java.util.List.of()),
                (ServerLevel obj, Double xArg, Double yArg, Double zArg, Double rArg) -> {
                    double x = xArg, y = yArg, z = zArg, r = rArg;
                    var entities = obj.getEntities((net.minecraft.world.entity.Entity) null, AABB.ofSize(new net.minecraft.world.phys.Vec3(x, y, z), r*2, r*2, r*2), e -> true);
                    java.util.List<ScriptValue> result = new java.util.ArrayList<>(entities.size());
                    for (var e : entities) result.add(EntityType.wrap(e));
                    return new ScriptValue.Array(result);
                })
            // NOT migrated to a typed method: volume/pitch are optional trailing args checked via
            // args.size() >= 5 / >= 6, and the handler still runs (with in-body defaults 1.0f/1.0f)
            // when they're omitted rather than being skipped entirely — same "default-if-missing"
            // shape as ContraptionType.play_sound's NOT-migrated note. Left untyped.
            // play_sound(x, y, z, soundId, volume?, pitch?)
            .method("play_sound", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.of(false);
                try {
                    double x = args.get(0).asNum(), y = args.get(1).asNum(), z = args.get(2).asNum();
                    String sound = args.get(3).asStr();
                    float vol   = args.size() >= 5 ? (float) args.get(4).asNum() : 1.0f;
                    float pitch = args.size() >= 6 ? (float) args.get(5).asNum() : 1.0f;
                    Identifier id = Identifier.tryParse(sound.contains(":") ? sound : "minecraft:" + sound);
                    if (id == null) return ScriptValue.of(false);
                    SoundEvent event = SoundEvent.createVariableRangeEvent(id);
                    level(obj).playSeededSound(null, x, y, z, Holder.direct(event),
                        SoundSource.BLOCKS, vol, pitch, 0L);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            // spawn_particle(name, x, y, z, count?, offset_x?, offset_y?, offset_z?, speed?) — vanilla
            // particle ids only (FLAME, CLOUD, SMOKE, ...); a CraftEngine custom particle isn't a
            // vanilla ParticleType and isn't resolvable here.
            // NOT migrated to a typed method: 5 optional trailing args (count, offset_x/y/z, speed)
            // checked via args.size() >= 5..9, each with an in-body default and the handler still
            // running when they're omitted — same "default-if-missing" shape as play_sound above.
            // Also more than 7 args total. Left untyped.
            .method("spawn_particle", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.of(false);
                try {
                    String name = args.get(0).asStr();
                    double x = args.get(1).asNum(), y = args.get(2).asNum(), z = args.get(3).asNum();
                    int count = args.size() >= 5 ? (int) args.get(4).asNum() : 1;
                    double ox = args.size() >= 6 ? args.get(5).asNum() : 0.0;
                    double oy = args.size() >= 7 ? args.get(6).asNum() : 0.0;
                    double oz = args.size() >= 8 ? args.get(7).asNum() : 0.0;
                    double speed = args.size() >= 9 ? args.get(8).asNum() : 0.0;
                    Identifier id = Identifier.tryParse(name.contains(":") ? name : "minecraft:" + name);
                    if (id == null) return ScriptValue.of(false);
                    var particleType = BuiltInRegistries.PARTICLE_TYPE.getValue(id);
                    if (!(particleType instanceof net.minecraft.core.particles.SimpleParticleType simple))
                        return ScriptValue.of(false);
                    level(obj).sendParticles(simple, x, y, z, count, ox, oy, oz, speed);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            // --- Generic TypedKey storage — backed by the SAME generic global store as
            // Server.get_typed (see ServerFlags) with the dimension id folded into the key, rather
            // than Bukkit's per-World PersistentDataContainer — this addon's persistence stays on
            // one NMS/CraftEngine-native path throughout instead of splitting across a second,
            // Bukkit-specific mechanism just for this one scope.
            .methodTyped2("get_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (ServerLevel obj, String key, String typeName) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec =
                            dev.arubik.craftengine.script.TypedKeyBridge.resolve(typeName);
                    if (codec == null) return ScriptValue.NULL;
                    return codec.fromStorage(dev.arubik.craftengine.util.ServerFlags.getTyped(worldTypedKey(obj, TYPED_PREFIX + key)));
                })
            // value (3rd arg) is dynamically coerced by codec.toStorage(ScriptValue) — decoded with
            // TypeCodecs.RAW (identity passthrough) and passed straight through, same reasoning as
            // ContraptionType.hold's TypeCodecs.STRING note but here the coercion genuinely depends
            // on the resolved codec, so it can't be pinned to one native Java type.
            .methodTyped3("set_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (ServerLevel obj, String key, String typeName, ScriptValue value) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec =
                            dev.arubik.craftengine.script.TypedKeyBridge.resolve(typeName);
                    if (codec == null) return false;
                    try {
                        dev.arubik.craftengine.util.ServerFlags.setTyped(worldTypedKey(obj, TYPED_PREFIX + key), codec.toStorage(value));
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            .methodTyped1("has_typed", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (ServerLevel obj, String key) ->
                        dev.arubik.craftengine.util.ServerFlags.hasTyped(worldTypedKey(obj, TYPED_PREFIX + key)))
            // broadcast_title(title, subtitle?, fade_in?, stay?, fade_out?) — sends the SAME title
            // to every player currently in THIS world (not server-wide — see Server for that scope
            // if it's ever needed). Same text/timing conventions as Player.send_title.
            // NOT migrated to a typed method: 4 optional trailing args (subtitle, fade_in, stay,
            // fade_out) each with an in-body default, and the handler still runs when they're
            // omitted — same "default-if-missing" shape as play_sound/spawn_particle above. Left
            // untyped.
            .method("broadcast_title", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    net.kyori.adventure.text.Component title = parseComponent(args.get(0).asStr());
                    net.kyori.adventure.text.Component subtitle = args.size() > 1
                        ? parseComponent(args.get(1).asStr()) : net.kyori.adventure.text.Component.empty();
                    int fadeIn  = args.size() > 2 ? (int) args.get(2).asNum() : 10;
                    int stay    = args.size() > 3 ? (int) args.get(3).asNum() : 70;
                    int fadeOut = args.size() > 4 ? (int) args.get(4).asNum() : 20;
                    net.kyori.adventure.title.Title t = net.kyori.adventure.title.Title.title(title, subtitle,
                        net.kyori.adventure.title.Title.Times.times(
                            java.time.Duration.ofMillis(fadeIn * 50L),
                            java.time.Duration.ofMillis(stay * 50L),
                            java.time.Duration.ofMillis(fadeOut * 50L)));
                    for (net.minecraft.server.level.ServerPlayer sp : level(obj).players()) {
                        sp.getBukkitEntity().showTitle(t);
                    }
                    return ScriptValue.of(true);
                } catch (Throwable t) { return ScriptValue.of(false); }
            })
            // broadcast_actionbar(text) — same scope as broadcast_title (this world only).
            .methodTyped1("broadcast_actionbar", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (ServerLevel obj, String text) -> {
                    try {
                        net.kyori.adventure.text.Component comp = parseComponent(text);
                        for (net.minecraft.server.level.ServerPlayer sp : obj.players()) {
                            sp.getBukkitEntity().sendActionBar(comp);
                        }
                        return true;
                    } catch (Throwable t) { return false; }
                });
    }

    /** Same MiniMessage-if-tagged / legacy-ampersand-otherwise heuristic {@code PlayerType} uses
     *  for its own text methods, duplicated here (small enough not to be worth sharing a helper
     *  across the two classes) so world-wide broadcasts accept either formatting convention too. */
    private static net.kyori.adventure.text.Component parseComponent(String text) {
        if (text != null && text.contains("<") && text.contains(">")) {
            try { return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text); }
            catch (Throwable ignored) {}
        }
        return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
            .deserialize(text == null ? "" : text);
    }

    private static String worldTypedKey(Object obj, String name) {
        return level(obj).dimension().identifier() + "|" + name;
    }

    public static ScriptValue wrap(ServerLevel level) {
        if (level == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("World", level);
    }

    private static ServerLevel level(Object obj) { return (ServerLevel) obj; }
}
