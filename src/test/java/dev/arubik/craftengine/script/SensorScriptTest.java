package dev.arubik.craftengine.script;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs the real sensor scripts against a mock Machine.
 *
 * <p>Covers three reported defects: the block sensor scanned the wrong way, its item filter read
 * the wrong slot and compared item ids against block ids (so no CraftEngine block ever matched),
 * and the speed sensor's scale made the top of its range unreachable.
 */
class SensorScriptTest {

    private static final Logger LOG = Logger.getLogger("test");

    static final Map<String, Integer> flags = new HashMap<>();
    /** Blocks keyed by offset "dx,dy,dz". */
    static final Map<String, ScriptValue> blocks = new HashMap<>();
    /** Item in each machine slot. */
    static final Map<Integer, ScriptValue> slots = new HashMap<>();
    static final List<Integer> emitted = new ArrayList<>();
    static final List<ScriptValue> entities = new ArrayList<>();
    static int facingDx = 0, facingDy = 0, facingDz = 1;

    record FakeBlock(String id) {}
    /** id = what Item.id returns, blockId = what Item.block_id returns. */
    record FakeItem(String id, String blockId) {}
    record FakeEntity(double vx, double vy, double vz, boolean living) {}

    @BeforeAll
    static void registerTypes() {
        PolyTypeRegistry.define("Block")
            .property("id", o -> ScriptValue.of(((FakeBlock) o).id()))
            .property("is_air", o -> ScriptValue.of(((FakeBlock) o).id().isEmpty()));

        PolyTypeRegistry.define("Item")
            .property("id", o -> ScriptValue.of(((FakeItem) o).id()))
            .property("block_id", o -> ScriptValue.of(((FakeItem) o).blockId()))
            .property("is_empty", o -> ScriptValue.of(((FakeItem) o).id().isEmpty()));

        PolyTypeRegistry.define("Entity")
            .property("velocity_x", o -> ScriptValue.of(((FakeEntity) o).vx()))
            .property("velocity_y", o -> ScriptValue.of(((FakeEntity) o).vy()))
            .property("velocity_z", o -> ScriptValue.of(((FakeEntity) o).vz()))
            .property("is_living", o -> ScriptValue.of(((FakeEntity) o).living()));

        PolyTypeRegistry.define("Redstone")
            .method("set", (o, a) -> { emitted.add((int) a.get(0).asNum()); return ScriptValue.of(true); })
            .method("off", (o, a) -> { emitted.add(0); return ScriptValue.of(true); });

        PolyTypeRegistry.define("Machine")
            .property("redstone", o -> ScriptValue.ofObj("Redstone", new Object()))
            .property("facing_dx", o -> ScriptValue.of(facingDx))
            .property("facing_dy", o -> ScriptValue.of(facingDy))
            .property("facing_dz", o -> ScriptValue.of(facingDz))
            .method("get_flag", (o, a) -> ScriptValue.of(flags.getOrDefault(a.get(0).asStr(), 0)))
            .method("set_flag", (o, a) -> { flags.put(a.get(0).asStr(), (int) a.get(1).asNum()); return ScriptValue.of(true); })
            // An absent slot is NULL, which is what is_empty() recognises as empty.
            .method("get_item_in_slot", (o, a) -> slots.getOrDefault((int) a.get(0).asNum(), ScriptValue.NULL))
            .method("block_at", (o, a) -> {
                String k = (int) a.get(0).asNum() + "," + (int) a.get(1).asNum() + "," + (int) a.get(2).asNum();
                return blocks.getOrDefault(k, ScriptValue.ofObj("Block", new FakeBlock("")));
            })
            .method("nearby_entities", (o, a) -> new ScriptValue.Array(new ArrayList<>(entities)))
            .method("emit_redstone", (o, a) -> { emitted.add((int) a.get(0).asNum()); return ScriptValue.of(true); });
    }

    @BeforeEach
    void reset() {
        flags.clear(); blocks.clear(); slots.clear(); emitted.clear(); entities.clear();
        facingDx = 0; facingDy = 0; facingDz = 1;   // facing south
    }

    private void runScript(String name) throws Exception {
        String src = Files.readString(Path.of("src/main/resources/scripts/" + name + ".pf"));
        ScriptProgram prog = ScriptProgram.parse(name, src, LOG);
        prog.evaluate(ScriptContext.builder().typed("Machine", new Object()).build());
    }

    private static ScriptValue block(String id) { return ScriptValue.ofObj("Block", new FakeBlock(id)); }

    private int lastPower() {
        assertFalse(emitted.isEmpty(), "the sensor must always emit a power level");
        return emitted.get(emitted.size() - 1);
    }

    // ---------------- block_sensor ----------------

    @Test
    @DisplayName("block sensor scans OPPOSITE to facing_dz (its model faces backwards)")
    void blockSensorScansInverted() throws Exception {
        // facing is +z; the sensor's model looks the other way, so detection must happen at -z.
        blocks.put("0,0,-1", block("minecraft:stone"));
        runScript("block_sensor");
        assertTrue(lastPower() > 0, "a block behind the facing direction must be detected");

        reset();
        blocks.put("0,0,1", block("minecraft:stone"));   // in front of facing
        runScript("block_sensor");
        assertEquals(0, lastPower(), "a block along facing_dz must NOT be detected");
    }

    @Test
    @DisplayName("block sensor reads its filter from the declared input slot 4, not slot 0")
    void blockSensorUsesInputSlotFour() throws Exception {
        blocks.put("0,0,-1", block("minecraft:stone"));
        // A filter in slot 0 is not the machine's input slot and must be ignored entirely.
        slots.put(0, ScriptValue.ofObj("Item", new FakeItem("minecraft:dirt", "minecraft:dirt")));
        runScript("block_sensor");
        assertTrue(lastPower() > 0, "a filter in the wrong slot must not suppress detection");

        reset();
        blocks.put("0,0,-1", block("minecraft:stone"));
        slots.put(4, ScriptValue.ofObj("Item", new FakeItem("minecraft:dirt", "minecraft:dirt")));
        runScript("block_sensor");
        assertEquals(0, lastPower(), "a non-matching filter in slot 4 must suppress detection");
    }

    @Test
    @DisplayName("block sensor matches a CraftEngine block via the item's block_id")
    void blockSensorMatchesCraftEngineBlock() throws Exception {
        // A CE item is backed by a plain vanilla material, so item.id can never equal a block id.
        // block_id resolves the block_item behavior, which is what actually gets placed.
        blocks.put("0,0,-1", block("cml:crate_acacia"));
        slots.put(4, ScriptValue.ofObj("Item", new FakeItem("minecraft:paper", "cml:crate_acacia")));
        runScript("block_sensor");
        assertTrue(lastPower() > 0, "a CE filter item must match the CE block it places");
    }

    @Test
    @DisplayName("block sensor rejects a different CraftEngine block")
    void blockSensorRejectsOtherCraftEngineBlock() throws Exception {
        blocks.put("0,0,-1", block("cml:crate_birch"));
        slots.put(4, ScriptValue.ofObj("Item", new FakeItem("minecraft:paper", "cml:crate_acacia")));
        runScript("block_sensor");
        assertEquals(0, lastPower(), "two CE blocks sharing a base material must not be confused");
    }

    @Test
    @DisplayName("block sensor reports at least 1 on any detection")
    void blockSensorNeverFloorsADetectionToZero() throws Exception {
        // One block at range 8 is 1/8 of the way -> floor(1/8*15) = 1; with a longer range the
        // old formula floored to 0 and a real detection read as "no signal".
        flags.put("scan_range", 30);
        blocks.put("0,0,-1", block("minecraft:stone"));
        runScript("block_sensor");
        assertTrue(lastPower() >= 1, "any detection must be visible as redstone, got " + lastPower());
    }

    // ---------------- speed_sensor ----------------

    private void addEntity(double vx, double vy, double vz, boolean living) {
        entities.add(ScriptValue.ofObj("Entity", new FakeEntity(vx, vy, vz, living)));
    }

    @Test
    @DisplayName("speed sensor reaches full power at the configured full_speed")
    void speedSensorReachesFullPower() throws Exception {
        addEntity(0.5, 0, 0, true);   // default full_speed = 50 => 0.50 blocks/tick
        runScript("speed_sensor");
        assertEquals(15, lastPower(), "0.50 b/t must read as full power under the default scale");
    }

    @Test
    @DisplayName("speed sensor scales between deadzone and full_speed")
    void speedSensorScalesLinearly() throws Exception {
        // deadzone 0.10, full 0.50 -> 0.30 sits halfway across the usable band.
        addEntity(0.30, 0, 0, true);
        runScript("speed_sensor");
        // Exactly 7.5 before rounding, so either neighbour is correct; what matters is that the
        // midpoint of the band lands in the middle of the output range.
        assertTrue(lastPower() == 7 || lastPower() == 8,
                "midway across the band should read about half power, got " + lastPower());
    }

    @Test
    @DisplayName("a standing entity reads 0 despite gravity's constant vertical velocity")
    void speedSensorIgnoresIdleGravityJitter() throws Exception {
        // A player standing on the ground is not at rest: vy stays pinned near -0.0784 every
        // tick. Reading raw magnitude reported 1-2 for someone holding perfectly still.
        addEntity(0, -0.0784, 0, true);
        runScript("speed_sensor");
        assertEquals(0, lastPower(), "standing still must read 0, not gravity jitter");
    }

    @Test
    @DisplayName("small horizontal drift below the deadzone reads 0")
    void speedSensorDeadzone() throws Exception {
        addEntity(0.05, 0, 0, true);
        runScript("speed_sensor");
        assertEquals(0, lastPower(), "sub-deadzone drift must not register");
    }

    @Test
    @DisplayName("a sprinting player is clearly readable, not stuck near zero")
    void speedSensorReadsASprintingPlayer() throws Exception {
        // ~0.28 blocks/tick. The old formula (speed * 15) gave floor(4.2) = 4 out of 15.
        addEntity(0.28, 0, 0, true);
        runScript("speed_sensor");
        assertTrue(lastPower() >= 6, "a sprinting player should read mid-range, got " + lastPower());
    }

    @Test
    @DisplayName("speed sensor clamps above full_speed instead of overflowing")
    void speedSensorClamps() throws Exception {
        addEntity(5.0, 0, 0, true);
        runScript("speed_sensor");
        assertEquals(15, lastPower(), "power must never exceed 15");
    }

    @Test
    @DisplayName("speed sensor reports 0 for a still world")
    void speedSensorZeroWhenStill() throws Exception {
        addEntity(0, 0, 0, true);
        runScript("speed_sensor");
        assertEquals(0, lastPower());
    }

    @Test
    @DisplayName("full_speed flag retunes the scale")
    void speedSensorFullSpeedFlagIsHonoured() throws Exception {
        flags.put("full_speed", 25);   // 0.25 b/t = full power
        flags.put("deadzone", 1);      // effectively no deadzone
        addEntity(0.25, 0, 0, true);
        runScript("speed_sensor");
        assertEquals(15, lastPower(), "full power must follow the configured full_speed");
    }

    @Test
    @DisplayName("living_only ignores item entities")
    void speedSensorLivingOnly() throws Exception {
        flags.put("living_only", 1);
        addEntity(0.5, 0, 0, false);   // a fast item entity
        runScript("speed_sensor");
        assertEquals(0, lastPower(), "non-living entities must be ignored when living_only is set");
    }

    @Test
    @DisplayName("vertical speed is excluded by default and opt-in via count_y")
    void speedSensorCountY() throws Exception {
        addEntity(0, 0.5, 0, true);   // falling fast, no horizontal motion
        runScript("speed_sensor");
        assertEquals(0, lastPower(), "vertical motion is excluded by default");

        reset();
        flags.put("count_y", 1);
        addEntity(0, 0.5, 0, true);
        runScript("speed_sensor");
        assertEquals(15, lastPower(), "count_y opts vertical motion back in");
    }
}
