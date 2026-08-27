package dev.arubik.craftengine.script.types.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.script.TypedKeyBridge;
import dev.arubik.craftengine.script.types.event.EventManagerType;
import dev.arubik.craftengine.script.types.primitive.ItemType;
import dev.arubik.craftengine.script.types.world.ServerType;
import dev.arubik.craftengine.sql.SQLDriver;

/**
 * {@code SQL} — the scripting front for {@link SQLDriver}. {@code SQL.query(sql, ...params)} /
 * {@code SQL.execute(sql, ...params)} are the raw JDBC escape hatch (parameterized — never string-
 * concatenate a value into {@code sql} itself); {@code SQL.get_typed}/{@code set_typed} are a
 * ready-made key-value table that round-trips ANY {@link TypedKeyBridge}-known type (int, string,
 * item, vector, a whole compound map, ...) through the exact same codec {@code Server}/{@code
 * Machine}'s own {@code get_typed}/{@code set_typed} use — so an item or map saved via {@code
 * Server.set_typed} and one saved via {@code SQL.set_typed} are byte-for-byte the same encoding,
 * just landing in a different backing store.
 *
 * <p><b>Blocking vs async:</b> {@code query}/{@code execute}/{@code get_typed}/{@code set_typed}
 * run on the CALLING thread — fine from a command, cron, or dialog callback, but never from a
 * per-tick machine/item script (a slow query would eat a whole tick). {@code query_async}/{@code
 * execute_async} run on a dedicated pool and hand the result to a {@code "file.pf:func"} callback
 * on the MAIN thread afterward, safe to call from anywhere.
 */
public final class SQLDriverType {

    public static final Object INSTANCE = new Object();

    private SQLDriverType() {}

    public static void register() {
        PolyTypeRegistry.define("SQL")
            .methodTyped0("is_ready", TypeCodecs.BOOL, (Object obj) -> SQLDriver.isReady())
            .methodTyped0("last_error", TypeCodecs.STRING, (Object obj) -> SQLDriver.lastError() == null ? "" : SQLDriver.lastError())
            .methodTyped0("backend", TypeCodecs.STRING, (Object obj) -> SQLDriver.backend().name().toLowerCase(Locale.ROOT))

            // SQL.query("SELECT * FROM t WHERE id = ?", id) -> Array<Map<column, value>>
            // NOT migrated to methodTyped: params are a trailing VARIADIC tail (jdbcParams(args, 1))
            // of unbounded length — no fixed arity for methodTypedN to decode. Left untyped.
            .method("query", (obj, args) -> {
                if (args.isEmpty()) return new ScriptValue.Array(List.of());
                try {
                    List<Map<String, Object>> rows = SQLDriver.query(args.get(0).asStr(), jdbcParams(args, 1));
                    List<ScriptValue> out = new ArrayList<>(rows.size());
                    for (Map<String, Object> row : rows) out.add(rowToMap(row));
                    return new ScriptValue.Array(out);
                } catch (Throwable t) {
                    CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[SQL] query threw", t);
                    return new ScriptValue.Array(List.of());
                }
            })
            // SQL.query_typed(sql, [params...], make_map("icon", "item", "pos", "block_pos")) —
            // like query(), but decodes the NAMED columns through TypedKeyBridge instead of the
            // generic jdbcToScript guess (which can only ever produce a Num/Bool/base64-String — it
            // has no way to know a BLOB column is really an Item). Params is a real Array here
            // (same reason query_async's is) since the column-types map is a third fixed argument.
            // Any column not listed in the map still decodes the normal generic way.
            .methodTyped3("query_typed", TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.RAW, TypeCodecs.RAW,
                    new ScriptValue.Array(List.of()),
                    (Object obj, String sql, ScriptValue paramsArg, ScriptValue columnTypesArg) -> {
                        List<Object> params = arrayToJdbcParams(paramsArg);
                        Map<String, String> columnTypes = mapArg(columnTypesArg);
                        try {
                            List<Map<String, Object>> rows = SQLDriver.query(sql, params);
                            List<ScriptValue> out = new ArrayList<>(rows.size());
                            for (Map<String, Object> row : rows) out.add(rowToMap(row, columnTypes));
                            return new ScriptValue.Array(out);
                        } catch (Throwable t) {
                            CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[SQL] query_typed threw", t);
                            return new ScriptValue.Array(List.of());
                        }
                    })
            // SQL.execute("UPDATE t SET x = ? WHERE id = ?", x, id) -> affected row count
            // NOT migrated to methodTyped: params are a trailing VARIADIC tail (jdbcParams(args, 1))
            // of unbounded length — no fixed arity for methodTypedN to decode. Left untyped.
            .method("execute", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0);
                try {
                    return ScriptValue.of(SQLDriver.execute(args.get(0).asStr(), jdbcParams(args, 1)));
                } catch (Throwable t) {
                    CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[SQL] execute threw", t);
                    return ScriptValue.of(0);
                }
            })
            // SQL.execute_typed(sql, [params...], [types...]) — the write-side counterpart of
            // query_typed: EXPLICITLY names each param's TypedKeyBridge type by position instead of
            // relying on scriptToJdbc's shape auto-detection. Needed whenever the script value is
            // ambiguous about which typed encoding it wants — a plain string that should be packed
            // as "uuid_array"/"int_array" rather than left as-is, or forcing a specific encoding
            // where auto-detection would guess wrong. types[i] == "" (or the types array shorter
            // than params) falls back to scriptToJdbc's normal auto-detect/passthrough for that slot.
            .methodTyped3("execute_typed", TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.RAW, TypeCodecs.DOUBLE, 0.0,
                    (Object obj, String sql, ScriptValue paramsArg, ScriptValue typesArg) -> {
                        try {
                            return (double) SQLDriver.execute(sql, typedJdbcParams(paramsArg, typesArg));
                        } catch (Throwable t) {
                            CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[SQL] execute_typed threw", t);
                            return 0.0;
                        }
                    })
            // SQL.execute_id("INSERT INTO t (x) VALUES (?)", x) -> the new row's auto-generated id, -1 on failure
            // NOT migrated to methodTyped: params are a trailing VARIADIC tail (jdbcParams(args, 1))
            // of unbounded length — no fixed arity for methodTypedN to decode. Left untyped.
            .method("execute_id", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(-1);
                try {
                    return ScriptValue.of(SQLDriver.executeReturningId(args.get(0).asStr(), jdbcParams(args, 1)));
                } catch (Throwable t) {
                    CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[SQL] execute_id threw", t);
                    return ScriptValue.of(-1);
                }
            })

            // SQL.query_async(sql, [params...], "file.pf:on_result") — on_result(rows) fires on the
            // main thread once the query completes. Params MUST be a real Array (make_array/[...]),
            // even a single-element or empty one — unlike query()/execute() this can't be variadic
            // since the callback ref is a THIRD fixed argument after it.
            .methodTyped3("query_async", TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                    (Object obj, String sql, ScriptValue paramsArg, String callbackRef) -> {
                        List<Object> params = arrayToJdbcParams(paramsArg);
                        SQLDriver.queryAsync(sql, params).whenComplete((rows, err) ->
                            runOnMainThread(() -> {
                                if (err != null) {
                                    CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[SQL] query_async threw", err);
                                    return;
                                }
                                List<ScriptValue> out = new ArrayList<>(rows.size());
                                for (Map<String, Object> row : rows) out.add(rowToMap(row));
                                invokeCallback(callbackRef, new ScriptValue.Array(out));
                            }));
                        return true;
                    })
            // SQL.execute_async(sql, [params...], "file.pf:on_done") — on_done(affected_rows)
            .methodTyped3("execute_async", TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                    (Object obj, String sql, ScriptValue paramsArg, String callbackRef) -> {
                        List<Object> params = arrayToJdbcParams(paramsArg);
                        SQLDriver.executeAsync(sql, params).whenComplete((count, err) ->
                            runOnMainThread(() -> {
                                if (err != null) {
                                    CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[SQL] execute_async threw", err);
                                    return;
                                }
                                invokeCallback(callbackRef, ScriptValue.of(count));
                            }));
                        return true;
                    })

            // --- TypedKey-backed key/value table — see class javadoc. `table` is validated to
            // [A-Za-z0-9_] since it's interpolated as an identifier (JDBC can't parameterize those).
            .methodTyped4("set_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW,
                    TypeCodecs.BOOL, false,
                    (Object obj, String tableArg, String key, String typeName, ScriptValue value) -> {
                        String table = safeTableName(tableArg);
                        if (table == null) return false;
                        TypedKeyBridge.Codec codec = TypedKeyBridge.resolve(typeName);
                        if (codec == null) return false;
                        try {
                            ensureTypedTable(table);
                            Object raw = codec.toStorage(value);
                            String stored = rawToText(raw);
                            SQLDriver.execute("DELETE FROM " + table + " WHERE k = ?", List.of(key));
                            SQLDriver.execute("INSERT INTO " + table + " (k, type, v) VALUES (?, ?, ?)",
                                    List.of(key, typeName.toLowerCase(Locale.ROOT), stored));
                            return true;
                        } catch (Throwable t) {
                            CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[SQL] set_typed threw", t);
                            return false;
                        }
                    })
            .methodTyped3("get_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                    (Object obj, String tableArg, String key, String typeName) -> {
                        String table = safeTableName(tableArg);
                        if (table == null) return ScriptValue.NULL;
                        TypedKeyBridge.Codec codec = TypedKeyBridge.resolve(typeName);
                        if (codec == null) return ScriptValue.NULL;
                        try {
                            ensureTypedTable(table);
                            List<Map<String, Object>> rows = SQLDriver.query("SELECT v FROM " + table + " WHERE k = ?", List.of(key));
                            if (rows.isEmpty()) return codec.fromStorage(null);
                            return codec.fromStorage(textToRaw(codec, rows.get(0).get("v")));
                        } catch (Throwable t) {
                            CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[SQL] get_typed threw", t);
                            return codec.fromStorage(null);
                        }
                    })
            .methodTyped2("has_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                    (Object obj, String tableArg, String key) -> {
                        String table = safeTableName(tableArg);
                        if (table == null) return false;
                        try {
                            ensureTypedTable(table);
                            return !SQLDriver.query("SELECT 1 FROM " + table + " WHERE k = ?", List.of(key)).isEmpty();
                        } catch (Throwable t) {
                            return false;
                        }
                    })
            .methodTyped2("delete_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                    (Object obj, String tableArg, String key) -> {
                        String table = safeTableName(tableArg);
                        if (table == null) return false;
                        try {
                            ensureTypedTable(table);
                            return SQLDriver.execute("DELETE FROM " + table + " WHERE k = ?", List.of(key)) > 0;
                        } catch (Throwable t) {
                            return false;
                        }
                    });
    }

    private static final java.util.Set<String> ENSURED_TABLES = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private static void ensureTypedTable(String table) throws java.sql.SQLException {
        if (!ENSURED_TABLES.add(table)) return;
        SQLDriver.execute("CREATE TABLE IF NOT EXISTS " + table
                + " (k VARCHAR(191) PRIMARY KEY, type VARCHAR(32) NOT NULL, v TEXT)", List.of());
    }

    /** Table/column identifiers can't be JDBC-parameterized, so this is the injection guard for
     *  every place a script-supplied name gets interpolated straight into SQL text. */
    private static String safeTableName(String name) {
        if (name == null || !name.matches("[A-Za-z0-9_]{1,64}")) return null;
        return "cep_" + name;
    }

    /** {@link TypedKeyBridge.Codec#toStorage} returns the exact boxed primitive its {@code
     *  storage()} NbtType expects (Integer, Boolean, byte[], ...) — this project's typed columns
     *  are all TEXT, so encode anything non-String into a form {@link #textToRaw} can invert. */
    private static String rawToText(Object raw) {
        return switch (raw) {
            case null -> null;
            case String s -> s;
            case byte[] b -> java.util.Base64.getEncoder().encodeToString(b);
            case int[] a -> { StringBuilder sb = new StringBuilder(); for (int v : a) sb.append(v).append(','); yield sb.toString(); }
            case long[] a -> { StringBuilder sb = new StringBuilder(); for (long v : a) sb.append(v).append(','); yield sb.toString(); }
            default -> String.valueOf(raw);
        };
    }

    /** Package-visible so {@link RedisDriverType#get_typed} can decode the same text encoding
     *  (both share the "TypedKeyBridge value -> TEXT" convention, just against a different store). */
    static Object textToRaw(TypedKeyBridge.Codec codec, Object stored) {
        String text = stored == null ? null : String.valueOf(stored);
        if (text == null) return null;
        return switch (codec.storage()) {
            case BYTE -> Byte.parseByte(text);
            case SHORT -> Short.parseShort(text);
            case INTEGER -> Integer.parseInt(text);
            case LONG -> Long.parseLong(text);
            case FLOAT -> Float.parseFloat(text);
            case DOUBLE -> Double.parseDouble(text);
            case BOOLEAN -> Boolean.parseBoolean(text);
            case STRING -> text;
            case BYTE_ARRAY -> java.util.Base64.getDecoder().decode(text);
            case INTEGER_ARRAY -> parseIntArray(text);
            case LONG_ARRAY -> parseLongArray(text);
        };
    }

    private static int[] parseIntArray(String s) {
        if (s.isBlank()) return new int[0];
        String[] parts = s.split(",");
        int[] out = new int[parts.length];
        for (int i = 0; i < parts.length; i++) out[i] = Integer.parseInt(parts[i]);
        return out;
    }

    private static long[] parseLongArray(String s) {
        if (s.isBlank()) return new long[0];
        String[] parts = s.split(",");
        long[] out = new long[parts.length];
        for (int i = 0; i < parts.length; i++) out[i] = Long.parseLong(parts[i]);
        return out;
    }

    /** Trailing variadic args (from index {@code from}) -> raw JDBC bind values. */
    private static List<Object> jdbcParams(List<ScriptValue> args, int from) {
        List<Object> out = new ArrayList<>();
        for (int i = from; i < args.size(); i++) out.add(scriptToJdbc(args.get(i)));
        return out;
    }

    /** A real Array value (from {@code query_async}/{@code execute_async}'s fixed params slot) ->
     *  raw JDBC bind values. */
    private static List<Object> arrayToJdbcParams(ScriptValue v) {
        if (!(v instanceof ScriptValue.Array arr)) return List.of();
        List<Object> out = new ArrayList<>(arr.elements().size());
        for (ScriptValue e : arr.elements()) out.add(scriptToJdbc(e));
        return out;
    }

    /** {@code execute_typed}'s params — {@code paramsArr[i]} bound through the EXPLICIT type
     *  {@code typesArr[i]} names (a missing/blank/unresolvable entry, or the types array running
     *  short, falls back to {@link #scriptToJdbc}'s normal auto-detect/passthrough for that slot). */
    private static List<Object> typedJdbcParams(ScriptValue paramsArr, ScriptValue typesArr) {
        if (!(paramsArr instanceof ScriptValue.Array params)) return List.of();
        List<ScriptValue> types = typesArr instanceof ScriptValue.Array t ? t.elements() : List.of();
        List<Object> out = new ArrayList<>(params.elements().size());
        for (int i = 0; i < params.elements().size(); i++) {
            ScriptValue param = params.elements().get(i);
            String typeName = i < types.size() ? types.get(i).asStr() : "";
            TypedKeyBridge.Codec codec = typeName == null || typeName.isBlank() ? null : TypedKeyBridge.resolve(typeName);
            if (codec != null) {
                try { out.add(codec.toStorage(param)); continue; } catch (Throwable ignored) {}
            }
            out.add(scriptToJdbc(param));
        }
        return out;
    }

    /** Binds a raw SQL param — the SqlTypedBridge half of this class: a plain Num/Str/Bool passes
     *  straight through (JDBC already knows those), but a value that's really a {@link
     *  TypedKeyBridge}-known shape (an Item, a Map/"compound", a Vector, a BlockState, a BlockPos)
     *  auto-detects and routes through THAT type's own codec instead of falling through to {@code
     *  asStr()} (which would silently bind junk for anything but a plain string). This is what
     *  lets a script write {@code SQL.execute("INSERT INTO t (icon) VALUES (?)", some_item)}
     *  directly against a real hand-written table/column, not just the {@code get_typed}/{@code
     *  set_typed} convenience table. */
    private static Object scriptToJdbc(ScriptValue v) {
        String typeName = autoDetectTypeName(v);
        if (typeName != null) {
            TypedKeyBridge.Codec codec = TypedKeyBridge.resolve(typeName);
            if (codec != null) {
                try { return codec.toStorage(v); } catch (Throwable ignored) {}
            }
        }
        return switch (v) {
            case ScriptValue.Num n -> n.value();
            case ScriptValue.Str s -> s.value();
            case ScriptValue.Bool b -> b.value();
            case ScriptValue.Null n -> null;
            default -> v.asStr();
        };
    }

    /** The {@link TypedKeyBridge} type name a wrapped script value's Java shape naturally maps to,
     *  or null for a plain Num/Str/Bool/Array/Null that {@link #scriptToJdbc}'s own switch already
     *  handles directly. Mirrors exactly what each custom codec in TypedKeyBridge itself pattern-
     *  matches on — kept in sync with that file if a new wrapped type is ever added there. */
    private static String autoDetectTypeName(ScriptValue v) {
        if (v instanceof ScriptValue.Item) return "item";
        if (v instanceof ScriptValue.Obj o) {
            return switch (o.typeName()) {
                case "Map" -> "compound";
                case "Vector" -> "vector";
                case "Location" -> "location";
                case "BlockState" -> "block_state";
                case "BlockPos" -> "block_pos";
                default -> null;
            };
        }
        return null;
    }

    private static ScriptValue rowToMap(Map<String, Object> row) {
        return rowToMap(row, Map.of());
    }

    /** {@code columnTypes}: column name -> TypedKeyBridge type name (see {@code query_typed}) — a
     *  listed column decodes through that codec's {@code fromStorage} (the raw JDBC value, e.g. a
     *  {@code byte[]} for a BLOB, IS the primitive that codec expects), everything else falls back
     *  to the generic guess. */
    private static ScriptValue rowToMap(Map<String, Object> row, Map<String, String> columnTypes) {
        Map<String, ScriptValue> out = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : row.entrySet()) {
            String typeName = columnTypes.get(e.getKey());
            TypedKeyBridge.Codec codec = typeName == null ? null : TypedKeyBridge.resolve(typeName);
            out.put(e.getKey(), codec != null ? codec.fromStorage(e.getValue()) : jdbcToScript(e.getValue()));
        }
        return dev.arubik.craftengine.script.types.primitive.MapType.wrap(out);
    }

    /** Unwraps a script "Map" value (from {@code make_map(...)}) into a plain {@code
     *  Map<String,String>} of {@code column -> TypedKeyBridge type name}, for {@code query_typed}'s
     *  column-types argument. */
    @SuppressWarnings("unchecked")
    private static Map<String, String> mapArg(ScriptValue v) {
        if (!(v instanceof ScriptValue.Obj o) || !"Map".equals(o.typeName())) return Map.of();
        Map<String, ScriptValue> raw = (Map<String, ScriptValue>) o.instance();
        Map<String, String> out = new LinkedHashMap<>();
        for (Map.Entry<String, ScriptValue> e : raw.entrySet()) out.put(e.getKey(), e.getValue().asStr());
        return out;
    }

    private static ScriptValue jdbcToScript(Object v) {
        return switch (v) {
            case null -> ScriptValue.NULL;
            case Number n -> ScriptValue.of(n.doubleValue());
            case Boolean b -> ScriptValue.of(b);
            case byte[] b -> ScriptValue.of(java.util.Base64.getEncoder().encodeToString(b));
            default -> ScriptValue.of(String.valueOf(v));
        };
    }

    private static void runOnMainThread(Runnable r) {
        org.bukkit.Bukkit.getScheduler().runTask(CraftEnginePolyfills.instance(), r);
    }

    private static void invokeCallback(String ref, ScriptValue result) {
        try {
            ScriptCall call = ScriptCall.parse(ref);
            if (call == null) return;
            ScriptContext.Builder b = ScriptContext.builder();
            b.typedAll(dev.arubik.craftengine.script.ScriptBootstrap.globalSingletons());
            call.executeWithExtraArgs(b.build(), List.of(result));
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger().log(Level.WARNING, "[SQL] async callback " + ref + " threw", t);
        }
    }
}
