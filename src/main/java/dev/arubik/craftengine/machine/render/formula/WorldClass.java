package dev.arubik.craftengine.machine.render.formula;

import java.util.List;

/**
 * {@link PolyClass} wrapping an NMS {@link net.minecraft.server.level.ServerLevel}.
 *
 * <p>When the level is a contraption's hidden dimension
 * ({@link dev.arubik.craftengine.contraption.core.ContraptionLevel}), the static factory
 * {@link #forLevel(net.minecraft.server.level.ServerLevel)} automatically resolves to the
 * real parent world so expressions return sensible values (day/night, biome, etc.).</p>
 *
 * <h3>Properties</h3>
 * <ul>
 *   <li>{@code name}          — level dimension key path (e.g. "overworld")</li>
 *   <li>{@code time}          — day-time ticks (0–24000)</li>
 *   <li>{@code full_time}     — absolute game-time ticks</li>
 *   <li>{@code is_day}        — true when sun is above horizon</li>
 *   <li>{@code is_night}      — inverse of {@code is_day}</li>
 *   <li>{@code is_raining}    — storm flag</li>
 *   <li>{@code is_thundering} — thundering flag</li>
 *   <li>{@code moon_phase}    — phase index 0–7</li>
 *   <li>{@code difficulty}    — "peaceful" | "easy" | "normal" | "hard"</li>
 *   <li>{@code players}       — number of players in this level</li>
 *   <li>{@code environment}   — dimension key path: "overworld" | "the_nether" | "the_end" | …</li>
 * </ul>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@code biome(x, y, z)} — namespaced biome key (e.g. "minecraft:plains")</li>
 * </ul>
 */
public final class WorldClass implements PolyClass {

    private final net.minecraft.server.level.ServerLevel level;

    public WorldClass(net.minecraft.server.level.ServerLevel level) {
        this.level = level;
    }

    /**
     * Preferred factory: if {@code level} is a contraption's hidden dimension, transparently
     * resolves to the real parent {@link net.minecraft.server.level.ServerLevel} so that all
     * time/weather/biome queries reflect the true world.
     */
    public static WorldClass forLevel(net.minecraft.server.level.ServerLevel level) {
        if (level instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl) {
            net.minecraft.world.level.Level rl = cl.realLevel();
            if (rl instanceof net.minecraft.server.level.ServerLevel rsl) {
                return new WorldClass(rsl);
            }
        }
        return new WorldClass(level);
    }

    @Override
    public PolyValue get(String property) {
        if (level == null) return PolyValue.NULL;
        return switch (property) {
            case "name"        -> PolyValue.of(level.getWorld().getName());
            case "environment" -> PolyValue.of(level.getWorld().getEnvironment().name().toLowerCase());
            case "time"           -> PolyValue.of(level.getDayTime() % 24000L);
            case "full_time"      -> PolyValue.of((double) level.getGameTime());
            case "is_day"         -> {
                long t = level.getDayTime() % 24000L;
                yield PolyValue.of(t < 12300 || t > 23000);
            }
            case "is_night"       -> {
                long t = level.getDayTime() % 24000L;
                yield PolyValue.of(t >= 12300 && t <= 23000);
            }
            case "is_raining"     -> PolyValue.of(level.isRaining());
            case "is_thundering"  -> PolyValue.of(level.isThundering());
            case "moon_phase"     -> PolyValue.of(level.getWorld().getMoonPhase().ordinal());
            case "difficulty"     -> PolyValue.of(level.getDifficulty().getKey());
            case "players" -> {
                java.util.List<PolyValue> list = new java.util.ArrayList<>();
                for (net.minecraft.server.level.ServerPlayer sp : level.players())
                    list.add(new PolyValue.Obj(new PlayerClass(sp)));
                yield new PolyValue.Array(list);
            }
            case "player_count"   -> PolyValue.of(level.players().size());
            default               -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        if (level == null) return PolyValue.NULL;
        return switch (method) {

            // biome(x, y, z) → "minecraft:plains"
            case "biome" -> {
                if (args.size() < 3) yield PolyValue.NULL;
                int bx = (int) args.get(0).asNum();
                int by = (int) args.get(1).asNum();
                int bz = (int) args.get(2).asNum();
                try {
                    net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biome =
                            level.getBiome(new net.minecraft.core.BlockPos(bx, by, bz));
                    yield PolyValue.of(biome.unwrapKey()
                            .map(k -> k.toString()).orElse("minecraft:plains"));
                } catch (Throwable ignored) { yield PolyValue.NULL; }
            }

            // block(x, y, z) → BlockClass at that position (member-chainable via PolyValue.Obj)
            case "block" -> {
                if (args.size() < 3) yield PolyValue.NULL;
                int bx = (int) args.get(0).asNum(), by = (int) args.get(1).asNum(), bz = (int) args.get(2).asNum();
                yield new PolyValue.Obj(BlockClass.of(level, new net.minecraft.core.BlockPos(bx, by, bz)));
            }

            // entity_count(x, y, z, radius) → number of entities near position
            case "entity_count" -> {
                double cx, cy, cz, radius;
                if (args.size() >= 4) {
                    cx = args.get(0).asNum(); cy = args.get(1).asNum(); cz = args.get(2).asNum();
                    radius = args.get(3).asNum();
                } else if (args.size() >= 1) {
                    cx = 0; cy = 0; cz = 0; radius = args.get(0).asNum();
                } else { yield PolyValue.of(0); }
                try {
                    var box = new net.minecraft.world.phys.AABB(
                            cx - radius, cy - radius, cz - radius,
                            cx + radius, cy + radius, cz + radius);
                    yield PolyValue.of(level.getEntities(
                            (net.minecraft.world.entity.Entity) null, box, e -> true).size());
                } catch (Throwable ignored) { yield PolyValue.of(0); }
            }

            // entities(x, y, z, radius) → array of EntityClass Objs (member-chainable)
            case "entities", "near_entities" -> {
                double cx, cy, cz, radius;
                if (args.size() >= 4) {
                    cx = args.get(0).asNum(); cy = args.get(1).asNum(); cz = args.get(2).asNum();
                    radius = args.get(3).asNum();
                } else if (args.size() >= 1) {
                    cx = 0; cy = 0; cz = 0; radius = args.get(0).asNum();
                } else { yield PolyValue.NULL; }
                try {
                    var box = new net.minecraft.world.phys.AABB(
                            cx - radius, cy - radius, cz - radius,
                            cx + radius, cy + radius, cz + radius);
                    var entities = level.getEntities(
                            (net.minecraft.world.entity.Entity) null, box, e -> true);
                    java.util.List<PolyValue> wrapped = new java.util.ArrayList<>(entities.size());
                    for (var e : entities) wrapped.add(new PolyValue.Obj(new EntityClass(e)));
                    yield new PolyValue.Array(wrapped);
                } catch (Throwable ignored) { yield PolyValue.NULL; }
            }

            // player_count() → number of players in this world
            case "player_count" -> PolyValue.of(level.players().size());

            // players_in_range(x, y, z, radius) → Array of PlayerClass Objs
            case "players_in_range", "nearby_players" -> {
                if (args.size() < 4) yield PolyValue.NULL;
                double px = args.get(0).asNum(), py = args.get(1).asNum(), pz = args.get(2).asNum();
                double radius = args.get(3).asNum();
                double rSq = radius * radius;
                java.util.List<PolyValue> players = new java.util.ArrayList<>();
                for (net.minecraft.server.level.ServerPlayer sp : level.players()) {
                    if (sp.distanceToSqr(px, py, pz) <= rSq) {
                        players.add(new PolyValue.Obj(new PlayerClass(sp)));
                    }
                }
                yield new PolyValue.Array(players);
            }

            // light(x, y, z) → combined light level at position
            case "light" -> {
                if (args.size() < 3) yield PolyValue.of(15);
                int lx = (int) args.get(0).asNum(), ly = (int) args.get(1).asNum(), lz = (int) args.get(2).asNum();
                try {
                    yield PolyValue.of(level.getMaxLocalRawBrightness(new net.minecraft.core.BlockPos(lx, ly, lz)));
                } catch (Throwable ignored) { yield PolyValue.of(15); }
            }

            default -> get(method);
        };
    }

    /** The underlying NMS level (always the real parent level, never a contraption sub-level). */
    public net.minecraft.server.level.ServerLevel level() { return level; }
}
