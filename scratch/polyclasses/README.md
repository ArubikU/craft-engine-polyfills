# Generated PolyClasses

Decompiled output of the real Java classes `PolyClassGenerator` builds at runtime — one per
registered `PolyType`. This is what a script value **unboxes into**: a `Machine` value becomes an
instance of `PC_Machine_*` wrapping the real underlying object.

## Regenerating

```
./gradlew.bat test -PpolyclassDump=D:/Github/craft-engine-polyfills/scratch/polyclasses
cd scratch/polyclasses
for f in *.class; do java -jar <cfr.jar> "$f" > "${f%.class}.java"; done
```

The `-PpolyclassDump` flag is a debug aid only — without it the generator writes nothing and costs
nothing. (These are the classes generated during the *test* run, so the types present are the ones
the tests register.)

## What to look for

**`PC_SpecTypedNumArgType_6.java`** — a `methodTypedN` registration with known codecs becomes a
genuinely native Java signature. No `ScriptValue` anywhere on the fast path:

```java
public double tm$0_scale(double d) {
    if (h$0 != null) return (Double) h$0.call(this.instance, d);
    return PolyClassRuntime.genericCall(...).asNum();   // fallback only
}
```

**`PC_Machine_1.java`** — plain `.method(...)` registrations get the erased
`ScriptValue name(List)` shape instead, and properties get `ScriptValue name()`. Still a win: the
`PolyTypeRegistry.get`/`resolveMethod` lookup is gone from every call, hoisted into the static
handler field.

## The `refresh()` method

The load-bearing part. A compiled `.pf` formula is cached forever by `ScriptFormula.CACHE` and keeps
calling the *same* generated class, so if handlers were baked in permanently the first registration
ever seen would be pinned for the life of the process — later `define`/`extend`/`replaceMethod`
calls silently ignored. `PolyTypeRegistry` notifies the generator on every mutation and each live
wrapper re-runs `refresh()`, re-resolving **by name**. Correct at zero per-call cost.

`resolveTypedHandler` is also passed the exact signature the method was generated for (e.g.
`"D:D"`). If a member is later re-registered with a different shape, it returns null and the
generated method routes through generic dispatch — degrading to correct-but-slower, never to wrong.

## Naming

- `tm$N_<name>` — typed method, native signature
- `um$N_<name>` — untyped method, `(List) -> ScriptValue`
- `pg$N_<name>` — property getter, `() -> ScriptValue`
- `h$N` / `m$N` / `p$N` — the corresponding cached handler field

Prefixed and index-suffixed so a script-level member name can never collide with the wrapper's own
`instance`/`refresh`/`of` members, or with a same-named property.
