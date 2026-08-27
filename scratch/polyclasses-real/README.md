# The REAL MachineType PolyClass

`../polyclasses/PC_Machine_1.java` is generated from the small stand-in "Machine" that
WindmillScriptTest / WaterWheelScriptTest / etc. each register for their own purposes (~7 members).
It is NOT the real `MachineType`, and it makes the generator look like it dropped almost everything.

This directory holds the real one, generated from `MachineType.register()` (120 registrations):

```
./gradlew.bat test --tests "*RealPolyClassGenerationTest" -PpolyclassDump=<dir>
```

## Coverage: complete

114 members generated, none dropped — pinned by `RealPolyClassGenerationTest`, which asserts every
single `allMethodNames()` / `allPropertyNames()` entry has a generated Java method.

| shape | count |
|---|---|
| typed (native Java signature) | 4 |
| untyped shim `ScriptValue(List)` | 56 |
| property accessor `ScriptValue()` | 54 |

## The remaining gap is porting, not the engine

Only 4 methods get a native signature:

```java
public ScriptValue tm$18_get_typed(String string, String string2)
public boolean     tm$29_report_su(double d)
public boolean     tm$42_set_typed(String string, String string2, ScriptValue scriptValue)
public boolean     tm$54_set_rpm_output(double d)
```

The other 56 still register via untyped `.method(...)` in `MachineType.java`, so the generator can
only emit the erased shim for them. They dispatch correctly and still skip the registry lookup —
but they carry `ScriptValue` boxing that a `methodTypedN` registration would eliminate.

Of those 56, ~30 use the "default-if-missing" shape (`args.isEmpty() ? 0 : args.get(0).asNum()`),
which the current typed API cannot express: its `onMissingArgs` SKIPS the handler and returns a
fixed value, which would silently drop the side effect those bodies still perform.
