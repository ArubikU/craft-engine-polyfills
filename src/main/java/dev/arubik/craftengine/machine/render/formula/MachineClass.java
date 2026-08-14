package dev.arubik.craftengine.machine.render.formula;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * PolyClass for machine block position and facing context.
 * Exposed as "Machine" in PolyContext.
 *
 * <h3>Properties</h3>
 * <pre>
 *   x, y, z           — integer block coords (centre - 0.5)
 *   facing             — "north"|"south"|"east"|"west"|"up"|"down"
 *   facing_angle       — yaw degrees (south=0, west=90, north=180, east=270)
 * </pre>
 *
 * <h3>player_facing(face [, zone])</h3>
 * <p>Ray-traces from every nearby player's eye. Returns {@code true} when any
 * player's ray hits this block on {@code face} and (if given) in {@code zone}.</p>
 *
 * <p>Zone precision tiers — the precision is inferred from the zone string format:</p>
 * <ul>
 *   <li><b>Face only</b> (no zone) — just which of the 6 faces was hit.</li>
 *   <li><b>Half</b> — {@code "top"}, {@code "bottom"}, {@code "left"},
 *       {@code "right"} — two halves on one axis.</li>
 *   <li><b>Quarter</b> — {@code "top-left"}, {@code "top-right"},
 *       {@code "bottom-left"}, {@code "bottom-right"} — 2×2 grid.</li>
 *   <li><b>Octant</b> — {@code "N"}, {@code "NE"}, {@code "E"}, {@code "SE"},
 *       {@code "S"}, {@code "SW"}, {@code "W"}, {@code "NW"} (uppercase compass) —
 *       8 angular wedges from the face centre. N = top, E = right.</li>
 *   <li><b>Sixteenth</b> — {@code "0-0"} … {@code "3-3"} (row-col, row 0 = top,
 *       col 0 = left) — 4×4 grid (16 cells per face).</li>
 * </ul>
 *
 * <p>The zone can also be embedded in the face argument with a {@code /} separator:
 * {@code player_facing("north/top-left")} ≡ {@code player_facing("north","top-left")}.</p>
 *
 * <h3>Other methods</h3>
 * <pre>
 *   player_in_range(dist)  → bool: any player within dist blocks
 *   player_above()         → bool: any player in 45° upward cone (range 24)
 *   player_below()         → bool: any player in 45° downward cone (range 24)
 * </pre>
 */
public final class MachineClass implements PolyClass {

    // ---- Fields ------------------------------------------------------------

    private final double x, y, z;
    /** Block-facing name: "north"|"south"|"east"|"west"|"up"|"down". */
    private final String facing;
    /** Facing yaw: south=0°, west=90°, north=180°, east=270°. */
    private final float  facingYaw;
    private final World  world;

    /**
     * When non-null, all player queries ({@code player_facing}, {@code player_in_range},
     * {@code player_above}, {@code player_below}) check only this specific player instead
     * of iterating the whole world. Used for per-viewer condition evaluation in
     * {@link dev.arubik.craftengine.machine.render.RendererManager}.
     */
    private final Player singlePlayer;

    /**
     * Integer block coordinates — used to match against {@link Block#getX()} etc.
     * from ray-trace results.
     */
    private final int blockX, blockY, blockZ;

    /** Clockwise octant zone names starting from North (= top of any face). */
    private static final String[] OCTANT_NAMES = {
        "N", "NE", "E", "SE", "S", "SW", "W", "NW"
    };

    // ---- Constructor -------------------------------------------------------

    /**
     * @param x          block centre X (typically {@code blockPos.getX() + 0.5})
     * @param y          block centre Y
     * @param z          block centre Z
     * @param facing     direction string ("north" etc.); null is treated as "north"
     * @param facingYaw  yaw in degrees
     * @param world      Bukkit world; null disables all player queries
     */
    public MachineClass(double x, double y, double z,
                        String facing, float facingYaw, World world) {
        this.x            = x;
        this.y            = y;
        this.z            = z;
        this.facing       = facing;
        this.facingYaw    = facingYaw;
        this.world        = world;
        this.singlePlayer = null;
        // blockX/Y/Z = integer block coordinate = floor(centre - 0.5) = floor(x - 0.5 + 0.5)
        this.blockX = (int) Math.floor(x);
        this.blockY = (int) Math.floor(y);
        this.blockZ = (int) Math.floor(z);
    }

    /**
     * Single-player variant — all player queries check only {@code singlePlayer}.
     * Used for per-viewer condition evaluation in the renderer manager.
     *
     * @param x          block centre X
     * @param y          block centre Y
     * @param z          block centre Z
     * @param facing     direction name
     * @param facingYaw  yaw in degrees
     * @param singlePlayer the one player to evaluate against; null is a no-op (nothing will match)
     */
    public MachineClass(double x, double y, double z,
                        String facing, float facingYaw, Player singlePlayer) {
        this.x            = x;
        this.y            = y;
        this.z            = z;
        this.facing       = facing;
        this.facingYaw    = facingYaw;
        this.world        = singlePlayer != null ? singlePlayer.getWorld() : null;
        this.singlePlayer = singlePlayer;
        this.blockX = (int) Math.floor(x);
        this.blockY = (int) Math.floor(y);
        this.blockZ = (int) Math.floor(z);
    }

    // ---- PolyClass ---------------------------------------------------------

    @Override
    public PolyValue get(String property) {
        return switch (property) {
            case "x"            -> PolyValue.of(x);
            case "y"            -> PolyValue.of(y);
            case "z"            -> PolyValue.of(z);
            case "facing"       -> PolyValue.of(facing != null ? facing : "north");
            case "facing_angle" -> PolyValue.of(facingYaw);

            case "location" -> {
                if (world == null) yield PolyValue.NULL;
                net.minecraft.server.level.ServerLevel sl =
                        ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
                yield new PolyValue.Obj(LocationClass.forLevel(sl, x, y, z));
            }
            case "world" -> {
                if (world == null) yield PolyValue.NULL;
                net.minecraft.server.level.ServerLevel sl =
                        ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
                yield new PolyValue.Obj(WorldClass.forLevel(sl));
            }

            // ---- player proximity shorthand (default range = 8) ----
            case "has_nearby_player" -> PolyValue.of(anyPlayerInRange(8.0));

            // ---- look-at: toward nearest/packet player ----
            // nearest_player_yaw / nearest_player_pitch always aim at the
            // closest player; in per-viewer (singlePlayer) mode queryPlayers()
            // already restricts to that one viewer — so these automatically
            // become "always face the viewer" when used in per-viewer rendering.
            case "nearest_player_yaw"   -> PolyValue.of(computePlayerYaw(Double.MAX_VALUE));
            case "nearest_player_pitch" -> PolyValue.of(computePlayerPitch(Double.MAX_VALUE));

            // nearest_player_distance — 999999 when no player found
            case "nearest_player_distance" -> {
                Player np = nearestPlayer(Double.MAX_VALUE);
                yield np != null
                        ? PolyValue.of(np.getLocation().distance(new Location(world, x, y, z)))
                        : PolyValue.of(999999.0);
            }

            // player_yaw / player_pitch — explicit "face the packet viewer"
            // alias; falls back to nearest player when not in per-viewer mode.
            case "player_yaw"   -> PolyValue.of(computePlayerYaw(Double.MAX_VALUE));
            case "player_pitch" -> PolyValue.of(computePlayerPitch(Double.MAX_VALUE));

            default -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        return switch (method) {

            case "player_facing" -> {
                if (args.isEmpty()) yield PolyValue.of(false);
                String target = args.get(0).asStr().toLowerCase();
                // Zone: second arg, or embedded in first arg as "face/zone"
                String zone = args.size() > 1 ? args.get(1).asStr() : null;
                int slash = target.indexOf('/');
                if (slash >= 0 && zone == null) {
                    zone   = target.substring(slash + 1);
                    target = target.substring(0, slash);
                }
                yield PolyValue.of(anyPlayerFacing(target, zone));
            }

            case "player_in_range" -> {
                double dist = args.isEmpty() ? 8.0 : args.get(0).asNum();
                yield PolyValue.of(anyPlayerInRange(dist));
            }

            case "player_above" -> PolyValue.of(anyPlayerInCone(0, 1, 0, 45));
            case "player_below" -> PolyValue.of(anyPlayerInCone(0, -1, 0, 45));

            // nearest_player_yaw(radius?) / nearest_player_pitch(radius?)
            // Optional radius argument — omit for unlimited range.
            case "nearest_player_yaw" -> {
                double r = args.isEmpty() ? Double.MAX_VALUE : args.get(0).asNum();
                yield PolyValue.of(computePlayerYaw(r));
            }
            case "nearest_player_pitch" -> {
                double r = args.isEmpty() ? Double.MAX_VALUE : args.get(0).asNum();
                yield PolyValue.of(computePlayerPitch(r));
            }
            case "player_yaw" -> {
                double r = args.isEmpty() ? Double.MAX_VALUE : args.get(0).asNum();
                yield PolyValue.of(computePlayerYaw(r));
            }
            case "player_pitch" -> {
                double r = args.isEmpty() ? Double.MAX_VALUE : args.get(0).asNum();
                yield PolyValue.of(computePlayerPitch(r));
            }
            // has_nearby_player(radius) — method form with explicit radius
            case "has_nearby_player" -> {
                double dist = args.isEmpty() ? 8.0 : args.get(0).asNum();
                yield PolyValue.of(anyPlayerInRange(dist));
            }

            // nearest_player() → PlayerClass Obj of the closest player (or NULL)
            case "nearest_player" -> {
                double r = args.isEmpty() ? Double.MAX_VALUE : args.get(0).asNum();
                Player np = nearestPlayer(r);
                if (np == null) yield PolyValue.NULL;
                net.minecraft.server.level.ServerPlayer sp =
                        ((org.bukkit.craftbukkit.entity.CraftPlayer) np).getHandle();
                yield new PolyValue.Obj(new PlayerClass(sp));
            }

            // nearby_players(distance) → Array of PlayerClass Objs within range
            case "nearby_players" -> {
                double dist = args.isEmpty() ? 8.0 : args.get(0).asNum();
                double distSq = dist * dist;
                Location center = new Location(world, x, y, z);
                java.util.List<PolyValue> players = new java.util.ArrayList<>();
                for (Player p : queryPlayers()) {
                    if (p.getLocation().distanceSquared(center) <= distSq) {
                        net.minecraft.server.level.ServerPlayer sp =
                                ((org.bukkit.craftbukkit.entity.CraftPlayer) p).getHandle();
                        players.add(new PolyValue.Obj(new PlayerClass(sp)));
                    }
                }
                yield new PolyValue.Array(players);
            }

            default -> PolyValue.NULL;
        };
    }

    // ---- Shared player-collection helper -----------------------------------

    /**
     * Returns the players to query. When a {@link #singlePlayer} is pinned,
     * returns only that player; otherwise returns all players in the world.
     */
    private java.util.Collection<? extends Player> queryPlayers() {
        if (singlePlayer != null) return java.util.List.of(singlePlayer);
        if (world == null)        return java.util.List.of();
        return world.getPlayers();
    }

    // ---- player_facing — ray trace + zone subdivision ----------------------

    private boolean anyPlayerFacing(String targetFace, String zone) {
        for (Player p : queryPlayers()) {
            if (playerFacingCheck(p, targetFace, zone)) return true;
        }
        return false;
    }

    /**
     * Ray-traces from the player's eye and checks whether the ray hits this
     * machine's block on {@code targetFace} (and in {@code zone} if specified).
     *
     * <p>The ray has a maximum distance of 6 blocks (generous reach; survival
     * players can interact at 4.5). Players beyond 8 blocks are skipped before
     * tracing to keep the per-tick cost low.</p>
     */
    private boolean playerFacingCheck(Player p, String targetFace, String zone) {
        // Pre-cull: skip players definitely beyond ray reach
        Location pLoc  = p.getLocation();
        double   preX  = pLoc.getX() - (blockX + 0.5);
        double   preY  = pLoc.getY() - (blockY + 0.5);
        double   preZ  = pLoc.getZ() - (blockZ + 0.5);
        if (preX * preX + preY * preY + preZ * preZ > 64.0) return false; // > 8 blocks

        RayTraceResult trace = p.rayTraceBlocks(6.0);
        if (trace == null) return false;

        Block hitBlock = trace.getHitBlock();
        if (hitBlock == null) return false;
        if (hitBlock.getX() != blockX
                || hitBlock.getY() != blockY
                || hitBlock.getZ() != blockZ) return false;

        BlockFace face = trace.getHitBlockFace();
        if (face == null) return false;
        if (!targetFace.equals(face.name().toLowerCase())) return false;

        // Face matched — done if no zone precision requested
        if (zone == null || zone.isEmpty()) return true;

        // Compute where on the face the ray landed
        Vector hitPos = trace.getHitPosition();
        double fx = clamp01(hitPos.getX() - blockX);
        double fy = clamp01(hitPos.getY() - blockY);
        double fz = clamp01(hitPos.getZ() - blockZ);

        // Map to face-local UV:
        //   u = horizontal axis (0 = left → 1 = right) from viewer's perspective
        //   v = vertical axis   (0 = bottom → 1 = top)
        double u, v;
        switch (face) {
            case NORTH -> { u = 1.0 - fx; v = fy; }   // viewer looks from -Z: left side = +X
            case SOUTH -> { u = fx;        v = fy; }   // viewer looks from +Z: left side = -X
            case EAST  -> { u = 1.0 - fz;  v = fy; }  // viewer looks from +X: left side = +Z
            case WEST  -> { u = fz;         v = fy; }  // viewer looks from -X: left side = -Z
            case UP    -> { u = fx;         v = 1.0 - fz; } // looking down: top = -Z
            case DOWN  -> { u = fx;         v = fz; }        // looking up:   top = +Z
            default    -> { return true; }                    // exotic face — accept
        }
        u = clamp01(u);
        v = clamp01(v);

        // Comma-separated zone list: cast ONE ray, check against multiple zones.
        // e.g. "1-1,1-2,2-1,2-2" — avoids 4 separate player_facing() calls.
        if (zone.contains(",")) {
            String[] parts = zone.split(",");
            String hitZone = computeFaceZone(u, v, parts[0].trim()); // precision from first entry
            for (String part : parts) {
                if (part.trim().equals(hitZone)) return true;
            }
            return false;
        }
        // Range notation: "R1..R2-C1..C2" for a rectangular region in the 4×4 grid.
        // e.g. "1..2-1..2" covers rows 1-2, cols 1-2 (center 2×2).
        if (zone.contains("..")) {
            return matchesRangeZone(u, v, zone);
        }
        return zone.equals(computeFaceZone(u, v, zone));
    }

    /**
     * Given a hit UV on a face and the target zone string, computes the zone name
     * for this UV at the precision tier implied by the target's format.
     *
     * @param u      horizontal fraction (0 = left, 1 = right), already clamped
     * @param v      vertical fraction   (0 = bottom, 1 = top), already clamped
     * @param target the zone string the caller is testing; its format determines
     *               which precision tier to use
     * @return the zone name for this UV point at the detected precision
     */
    private static String computeFaceZone(double u, double v, String target) {

        // ── Sixteenth (4×4 grid): "r-c" where r,c ∈ {0,1,2,3} ─────────────
        // row 0 = top, col 0 = left
        if (isSixteenthTarget(target)) {
            int col = Math.min(3, (int)(u * 4));
            int row = Math.min(3, 3 - (int)(v * 4));
            return row + "-" + col;
        }

        // ── Octant (8 angular wedges): uppercase compass "N","NE",… ─────────
        // The compass is face-relative: N = top (↑v), E = right (→u)
        if (isOctantTarget(target)) {
            // Compass bearing = atan2(east_component, north_component)
            //                 = atan2(u - 0.5, v - 0.5)
            double bearing = Math.toDegrees(Math.atan2(u - 0.5, v - 0.5));
            if (bearing < 0) bearing += 360.0;
            int sector = (int)((bearing + 22.5) / 45.0) % 8;
            return OCTANT_NAMES[sector];
        }

        // ── Quarter (2×2 grid): "top-left"|"top-right"|"bottom-left"|"bottom-right" ──
        if (isQuarterTarget(target)) {
            return (v >= 0.5 ? "top" : "bottom") + "-" + (u < 0.5 ? "left" : "right");
        }

        // ── Half: "top"/"bottom" split on v, "left"/"right" split on u ──────
        return switch (target) {
            case "top", "bottom" -> v >= 0.5 ? "top" : "bottom";
            default              -> u < 0.5 ? "left" : "right";
        };
    }

    // ---- Zone-format detectors ---------------------------------------------

    /**
     * {@code "0-0"} through {@code "3-3"}: exactly "digit, hyphen, digit" where
     * both digits are 0-3.
     */
    private static boolean isSixteenthTarget(String t) {
        return t.length() == 3
                && t.charAt(0) >= '0' && t.charAt(0) <= '3'
                && t.charAt(1) == '-'
                && t.charAt(2) >= '0' && t.charAt(2) <= '3';
    }

    /** Uppercase compass abbreviations: N, NE, E, SE, S, SW, W, NW. */
    private static boolean isOctantTarget(String t) {
        return switch (t) {
            case "N", "NE", "E", "SE", "S", "SW", "W", "NW" -> true;
            default -> false;
        };
    }

    /** All four lowercase two-word quarter names. */
    private static boolean isQuarterTarget(String t) {
        return switch (t) {
            case "top-left", "top-right", "bottom-left", "bottom-right" -> true;
            default -> false;
        };
    }

    // ---- Helpers for other spatial methods ---------------------------------

    private boolean anyPlayerInRange(double dist) {
        double distSq   = dist * dist;
        Location center = new Location(world, x, y, z);
        for (Player p : queryPlayers()) {
            if (p.getLocation().distanceSquared(center) <= distSq) return true;
        }
        return false;
    }

    /**
     * True if any player is within 24 blocks and lies inside a cone of
     * {@code halfAngleDeg} degrees around the axis {@code (dx, dy, dz)}.
     */
    private boolean anyPlayerInCone(double dx, double dy, double dz, double halfAngleDeg) {
        double cosAngle = Math.cos(Math.toRadians(halfAngleDeg));
        for (Player p : queryPlayers()) {
            Location pLoc = p.getLocation();
            double rx = pLoc.getX() - x;
            double ry = pLoc.getY() - y;
            double rz = pLoc.getZ() - z;
            double dist = Math.sqrt(rx * rx + ry * ry + rz * rz);
            if (dist < 0.5 || dist > 24) continue;
            double dot = (rx * dx + ry * dy + rz * dz) / dist;
            if (dot >= cosAngle) return true;
        }
        return false;
    }

    /**
     * Checks if a face UV hit falls within a rectangular region expressed as
     * {@code "R1..R2-C1..C2"} where rows/cols are 4×4 grid indices (0 = top/left).
     * Example: {@code "1..2-1..2"} = rows 1-2, cols 1-2 (center 2×2 of the face).
     */
    private static boolean matchesRangeZone(double u, double v, String zone) {
        int sep = zone.lastIndexOf('-');
        if (sep < 0) return false;
        String rowPart = zone.substring(0, sep);
        String colPart = zone.substring(sep + 1);
        int[] rows = parseRangePart(rowPart);
        int[] cols = parseRangePart(colPart);
        if (rows == null || cols == null) return false;
        int col = Math.min(3, (int)(u * 4));
        int row = Math.min(3, 3 - (int)(v * 4));
        return row >= rows[0] && row <= rows[1] && col >= cols[0] && col <= cols[1];
    }

    /** Parses {@code "1..2"} → {@code [1, 2]} or {@code "1"} → {@code [1, 1]}. */
    private static int[] parseRangePart(String s) {
        int dotdot = s.indexOf("..");
        if (dotdot < 0) {
            try { int v = Integer.parseInt(s.trim()); return new int[]{v, v}; } catch (Exception ignored) {}
            return null;
        }
        try {
            int lo = Integer.parseInt(s.substring(0, dotdot).trim());
            int hi = Integer.parseInt(s.substring(dotdot + 2).trim());
            return new int[]{Math.min(lo, hi), Math.max(lo, hi)};
        } catch (Exception ignored) { return null; }
    }

    // ---- Look-at helpers ---------------------------------------------------

    /**
     * Finds the nearest Bukkit {@link Player} from {@link #queryPlayers()} within
     * {@code maxDist} blocks. Returns {@code null} when none qualify.
     */
    private Player nearestPlayer(double maxDist) {
        double maxDistSq = maxDist * maxDist;
        Player nearest   = null;
        double nearestSq = maxDistSq;
        Location center  = new Location(world, x, y, z);
        for (Player p : queryPlayers()) {
            double dSq = p.getLocation().distanceSquared(center);
            if (dSq <= nearestSq) { nearestSq = dSq; nearest = p; }
        }
        return nearest;
    }

    /**
     * Yaw (degrees, Minecraft convention: south=0°, west=90°, north=±180°, east=−90°/270°)
     * to rotate this machine's position to face the nearest qualifying player.
     * In per-viewer mode ({@link #singlePlayer} set) {@link #queryPlayers()} already restricts
     * to that one viewer, so this naturally becomes "face the packet viewer".
     */
    private double computePlayerYaw(double maxDist) {
        Player p = nearestPlayer(maxDist);
        if (p == null) return 0.0;
        Location loc = p.getLocation();
        return yawTo(x, z, loc.getX(), loc.getZ());
    }

    /**
     * Pitch (degrees, Minecraft convention: up=−90°, down=+90°) to look at the
     * nearest qualifying player's eye position from this machine's origin.
     */
    private double computePlayerPitch(double maxDist) {
        Player p = nearestPlayer(maxDist);
        if (p == null) return 0.0;
        Location eye = p.getEyeLocation();
        return pitchTo(x, y, z, eye.getX(), eye.getY(), eye.getZ());
    }

    /**
     * Horizontal yaw toward a target point.
     * Convention: south=0°, west=90°, north=±180°, east=−90° (identical to Minecraft entity yaw).
     */
    static double yawTo(double fromX, double fromZ, double toX, double toZ) {
        return Math.toDegrees(Math.atan2(-(toX - fromX), toZ - fromZ));
    }

    /**
     * Vertical pitch toward a target point.
     * Convention: straight up=−90°, straight down=+90° (identical to Minecraft entity pitch).
     *
     * @param toY target Y (use eye position for players)
     */
    static double pitchTo(double fromX, double fromY, double fromZ,
                           double toX,   double toY,   double toZ) {
        double dx    = toX - fromX;
        double dy    = toY - fromY;
        double dz    = toZ - fromZ;
        double hDist = Math.sqrt(dx * dx + dz * dz);
        return Math.toDegrees(-Math.atan2(dy, hDist));
    }

    // ---- Utility -----------------------------------------------------------

    /** Clamp to [0, 1) so grid-index calculations never exceed the top bucket. */
    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0 - 1e-9, v));
    }
}
