# Generated PolyClasses (from the test suite)

Decompiled output of the real Java classes `PolyClassGenerator` builds at runtime — one per
registered `PolyType`. This is what a script value **unboxes into**.

```
./gradlew.bat test -PpolyclassDump=<dir>
cd <dir>; for f in *.class; do java -jar <cfr.jar> "$f" > "${f%.class}.java"; done
```

These are the types the TEST SUITE registers, so most are small fixtures. In particular
`PolyClassMachine` here is a ~7-member stand-in that the behavioural script tests register — **not**
the real `MachineType`. For that one see `../polyclasses-real`.

`PolyClassMachine_v2` is the documented rebuild case working as designed: two test classes register
different fake "Machine" types, and `buildAll()` rebuilds a wrapper when a type has gained members
its existing class doesn't cover. In production every type is fully registered before `buildAll()`
runs, so each gets exactly one class and no `_v` suffix appears.

## What to look for

**`PolyClassSpecTypedNumArgType.java`** — a typed registration becomes a genuinely native Java
signature. No `ScriptValue` anywhere on the fast path:

```java
public double tm$0_scale(double d) {
    if (h$0 != null) return (Double) h$0.call(this.instance, d);
    return PolyClassRuntime.genericCall(...).asNum();   // fallback only
}
```

**`PolyClassSpecChildType.java`** — extends its parent's class and inherits its members rather than
re-emitting them.

## The `refresh()` method

The load-bearing part. A compiled `.pf` formula is cached forever by `ScriptFormula.CACHE` and keeps
calling the *same* generated class, so if handlers were baked in permanently the first registration
ever seen would be pinned for the life of the process — later `define`/`extend`/`replaceMethod`
calls silently ignored. `PolyTypeRegistry` notifies the generator on every mutation and each live
wrapper re-runs `refresh()`, re-resolving **by name**. Correct at zero per-call cost.

`resolveTypedHandler` is also passed the exact signature the method was generated for (e.g. `"D:D"`).
If a member is later re-registered with a different shape it returns null and the generated method
routes through generic dispatch — degrading to correct-but-slower, never to wrong.
