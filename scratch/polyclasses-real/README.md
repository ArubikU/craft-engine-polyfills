# The real MachineType PolyClass

Generated from `MachineType.register()` — the actual production type, ~120 registrations.

```
./gradlew.bat test --tests "*RealMachinePolyClassDumpTest" \
    -PpolyclassDumpReal=true -PpolyclassDump=<dir>
cd <dir>; for f in *.class; do java -jar <cfr.jar> "$f" > "${f%.class}.java"; done
```

The `--tests` filter is load-bearing: that test registers the real MachineType into the
process-global `PolyTypeRegistry`, replacing the small stand-in "Machine" other test classes
register. Run alone, nothing else is there to clobber. (Routine coverage is guaranteed by
`RealPolyClassGenerationTest` against a synthetic type, which pollutes nothing.)

## What it shows

`PolyClassMachine extends PolyClassBlock` — the generated hierarchy mirrors the PolyType hierarchy.
Machine doesn't redeclare the wrapped-`instance` field; it inherits Block's.

| shape | count |
|---|---|
| typed (native Java signature) | 58 |
| untyped shim `ScriptValue(List)` | 60 |
| property accessor `ScriptValue()` | 54 |

Every registered member has a generated method — none dropped.

**58 typed, up from 4.** That is the porting work: methods that used to register via untyped
`.method(...)` now declare real types, so the generator can emit genuinely native signatures:

```java
public boolean tm$11_consume_energy(double d)
public boolean tm$7_turn_page(double d)
public ScriptValue tm$2_tick_break(ScriptValue scriptValue, double d)
```

No `ScriptValue` boxing on those paths at all. The 60 remaining shims are methods the typed API
genuinely cannot express — variadic tails, multi-shape dispatch, all-or-nothing positional groups —
each documented at its registration site.

## Naming

- `tm$N_<name>` — typed method, native signature
- `um$N_<name>` — untyped method, `(List) -> ScriptValue`
- `pg$N_<name>` — property getter, `() -> ScriptValue`
- `h$N` / `m$N` / `p$N` — the corresponding cached handler field

Prefixed and index-suffixed so a script-level member name can never collide with the wrapper's own
`instance`/`refresh`/`of` members, or with a same-named property.

A typed method also gets a `um$` companion: the native signature has a fixed arity, but a call site
may legally pass fewer arguments (`methodTypedOptN` defaults, or `methodTypedN`'s `onMissingArgs`),
and the shim is what applies that.

## What the motor emits for real scripts

`Script_windmill.class` / `Script_shaft.class` are the real shipped `.pf` files compiled with the
real types registered — the end-to-end artifact worth auditing, not a fixture.

Measured on `windmill.pf`:

| | count |
|---|---|
| native PolyClass calls (`tm$`) | 14 |
| PolyClass shim calls (`um$`/`pg$`) | 3 |
| invokedynamic inline-cache sites | 22 |
| `ScriptFormula.memberCall` / `memberGet` | **0** |
| `ScriptFormula.compile(...)` | **0** |
| `callBuiltin` | 4 |

The two zeroes are the point. Every member access is now either a direct call on a generated
PolyClass or a self-linking inline cache; the per-evaluation registry lookup is gone, and so is the
`ScriptFormula.compile(expr).evaluate(ctx)` round-trip that started this work.

### What is deliberately left

- **4 `callBuiltin` sites** (`contains`, `tick`, `clamp` x2). An inline cache could hoist the
  `ScriptBuiltins.get(name)` map lookup, but `callBuiltin` must FIRST probe `ctx.peekVar(name)` —
  a user-defined `def` is allowed to shadow a builtin, and that probe is context-dependent, so it
  cannot be cached away. Caching would halve the lookups, not remove them, in exchange for another
  invalidation path. Not worth it.
- **`ScriptValue.of` calls**, almost all `arrayList.add(ScriptValue.of(...))`. These are argument
  lists for the erased shim / builtin APIs, which take `List<ScriptValue>` by signature. The typed
  native path already skips the list entirely; these are the calls that genuinely cannot.
