package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * "Io" type — a machine's IO port map, reachable from scripts as {@code Machine.io}.
 *
 * <p>Until now a machine's IO was fixed by the {@code io} block of its JSON. This exposes the same
 * configuration at runtime, for every {@link IOConfiguration.IOType}, so a script can build it on
 * the fly: a pipe whose faces the player toggles, an output that only opens once a craft finishes,
 * a machine that accepts gas only while it is running.
 *
 * <pre>
 *   Machine.io.allow_input("gas", "up")            # accept gas from above
 *   Machine.io.allow_output("fluid", "down")       # drain fluid downward
 *   Machine.io.deny_output("item", "north")
 *   if Machine.io.accepts_input("gas", "up") { ... }
 *   Machine.io.clear()                             # close every face
 * </pre>
 *
 * <p>Faces accept the same vocabulary as {@code Machine.rpm_out}: absolute ("north".."down"),
 * facing-relative ("front", "back", "left", "right"), or "all". Editing goes through
 * {@code AbstractMachineBlockEntity.mutableIO()}, which first gives this block its own copy — a
 * script must never reconfigure every machine of its type by editing the shared definition.
 */
public final class IoType {

    /** Instance object: the machine whose ports these are. */
    public record IoRef(MachineType.MachineRef machine) {

        AbstractMachineBlockEntity be() {
            return machine.blockEntity() instanceof AbstractMachineBlockEntity m ? m : null;
        }

        IOConfiguration read() {
            AbstractMachineBlockEntity m = be();
            return m == null ? null : m.getIOConfiguration();
        }

        IOConfiguration.Simple write() {
            AbstractMachineBlockEntity m = be();
            return m == null ? null : m.mutableIO();
        }
    }

    private IoType() {}

    /** Resolves an IO type name, or null when it names nothing. */
    private static IOConfiguration.IOType type(ScriptValue v) {
        try {
            return IOConfiguration.IOType.valueOf(v.asStr().trim().toUpperCase(Locale.ROOT));
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * Resolves a face name to world directions. "all" expands to every face, so a script can open
     * or close a machine in one call.
     */
    private static List<Direction> faces(MachineType.MachineRef m, String name) {
        List<Direction> out = new ArrayList<>();
        if (name == null) return out;
        String n = name.trim().toLowerCase(Locale.ROOT);
        Direction facing = m.facingDirection();
        switch (n) {
            case "all" -> out.addAll(List.of(Direction.values()));
            case "horizontal" -> {
                for (Direction d : Direction.values()) if (d.getAxis() != Direction.Axis.Y) out.add(d);
            }
            case "vertical" -> out.addAll(List.of(Direction.UP, Direction.DOWN));
            case "front" -> out.add(facing);
            case "back" -> out.add(facing.getOpposite());
            case "right" -> out.add(facing.getClockWise());
            case "left" -> out.add(facing.getCounterClockWise());
            case "top" -> out.add(Direction.UP);
            case "bottom" -> out.add(Direction.DOWN);
            default -> {
                Direction d = Direction.byName(n);
                if (d != null) out.add(d);
            }
        }
        return out;
    }

    public static void register() {
        PolyTypeRegistry.define("Io")
            // Not TypeCodecs.listOf even though propertyTyped now exists: the elements are plain
            // strings, not Obj-wrapped PolyType instances, and no string-list codec exists.
            .property("types", obj -> {
                List<ScriptValue> names = new ArrayList<>();
                for (IOConfiguration.IOType t : IOConfiguration.IOType.values())
                    names.add(ScriptValue.of(t.name().toLowerCase(Locale.ROOT)));
                return new ScriptValue.Array(names);
            })
            .propertyTyped("configured", TypeCodecs.BOOL, (IoRef r) -> r.read() != null)

            // Arg0 (the IO type name) is decoded with TypeCodecs.RAW rather than STRING: the
            // type(ScriptValue) helper below does more than a plain asStr() — it also
            // trim/uppercase/IOType.valueOf's the string, returning null on a bad name — so the
            // raw ScriptValue is passed straight through unchanged and handed to type() exactly as
            // the untyped body did, rather than re-wrapping a decoded String back into one.
            .methodTyped2("accepts_input", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (IoRef r, ScriptValue typeArg, String face) -> {
                    IOConfiguration cfg = r.read();
                    IOConfiguration.IOType t = type(typeArg);
                    if (cfg == null || t == null) return false;
                    for (Direction d : faces(r.machine(), face))
                        if (cfg.acceptsInput(t, d)) return true;
                    return false;
                })
            .methodTyped2("provides_output", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (IoRef r, ScriptValue typeArg, String face) -> {
                    IOConfiguration cfg = r.read();
                    IOConfiguration.IOType t = type(typeArg);
                    if (cfg == null || t == null) return false;
                    for (Direction d : faces(r.machine(), face))
                        if (cfg.providesOutput(t, d)) return true;
                    return false;
                })

            .methodTyped2("allow_input", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (IoRef r, ScriptValue typeArg, String face) -> editTyped(r, typeArg, face, true, true))
            .methodTyped2("deny_input", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (IoRef r, ScriptValue typeArg, String face) -> editTyped(r, typeArg, face, true, false))
            .methodTyped2("allow_output", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (IoRef r, ScriptValue typeArg, String face) -> editTyped(r, typeArg, face, false, true))
            .methodTyped2("deny_output", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (IoRef r, ScriptValue typeArg, String face) -> editTyped(r, typeArg, face, false, false))

            /** Opens a face both ways in one call. */
            .methodTyped2("allow", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (IoRef r, ScriptValue typeArg, String face) -> {
                    boolean in = editTyped(r, typeArg, face, true, true);
                    boolean out = editTyped(r, typeArg, face, false, true);
                    return in || out;
                })
            .methodTyped2("deny", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (IoRef r, ScriptValue typeArg, String face) -> {
                    boolean in = editTyped(r, typeArg, face, true, false);
                    boolean out = editTyped(r, typeArg, face, false, false);
                    return in || out;
                })

            /** Closes every face for every type — a blank slate to build on. */
            .methodTyped0("clear", TypeCodecs.BOOL,
                (IoRef r) -> {
                    IOConfiguration.Simple cfg = r.write();
                    if (cfg == null) return false;
                    for (IOConfiguration.IOType t : IOConfiguration.IOType.values())
                        for (Direction d : Direction.values())
                            cfg.removeInput(t, d).removeOutput(t, d);
                    return true;
                })

            /** Every face currently open for a type, as "input"/"output"/"both" per direction. */
            // describe()'s single argument is optional and the body still runs when it's absent, so
            // this is methodTypedOpt1's shape, not onMissingArgs'. The type argument is decoded with
            // TypeCodecs.RAW and resolved by type() inside the body exactly as the other IO methods
            // do (see allow/deny above), with a ScriptValue.NULL default: type(NULL) reads as the
            // literal string "null", which is not an IOType name, so it returns null — identical to
            // the old `a.isEmpty() ? null` branch, and the empty-map result that follows from it.
            .methodTypedOpt1("describe", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.RAW,
                (IoRef r, ScriptValue typeArg) -> {
                IOConfiguration cfg = r.read();
                IOConfiguration.IOType t = type(typeArg);
                java.util.LinkedHashMap<String, ScriptValue> map = new java.util.LinkedHashMap<>();
                if (cfg == null || t == null)
                    return dev.arubik.craftengine.script.types.primitive.MapType.wrap(map);
                for (Direction d : Direction.values()) {
                    boolean in = cfg.acceptsInput(t, d);
                    boolean out = cfg.providesOutput(t, d);
                    if (!in && !out) continue;
                    map.put(d.getName(), ScriptValue.of(in && out ? "both" : (in ? "input" : "output")));
                }
                return dev.arubik.craftengine.script.types.primitive.MapType.wrap(map);
            });
    }

    /** Shared body of allow_/deny_ input/output — the typed-registration form of the old
     *  edit(Object, List&lt;ScriptValue&gt;, boolean, boolean) helper, taking already-decoded
     *  arguments instead of the raw args list (the "&lt; 2 args" short-circuit it used to open with
     *  is now each caller's methodTyped2 onMissingArgs). */
    private static boolean editTyped(IoRef r, ScriptValue typeArg, String face, boolean input, boolean grant) {
        IOConfiguration.IOType t = type(typeArg);
        IOConfiguration.Simple cfg = r.write();
        if (cfg == null || t == null) return false;
        List<Direction> dirs = faces(r.machine(), face);
        if (dirs.isEmpty()) return false;
        for (Direction d : dirs) {
            if (input) {
                if (grant) cfg.addInput(t, d); else cfg.removeInput(t, d);
            } else {
                if (grant) cfg.addOutput(t, d); else cfg.removeOutput(t, d);
            }
        }
        return true;
    }

    public static ScriptValue wrap(MachineType.MachineRef machine) {
        return machine == null ? ScriptValue.NULL : ScriptValue.ofObj("Io", new IoRef(machine));
    }
}
