package dev.arubik.craftengine.script;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A named script-visible type. Defines properties and methods accessible via
 * PolyFormula expressions. Supports single-parent inheritance — a child type
 * inherits all parent properties/methods, but may override any of them.
 *
 * Types are identified by string name (e.g. "Entity", "Player", "Block").
 * Register them via {@link PolyTypeRegistry}.
 */
public final class PolyType {

    private final String name;
    private final PolyType parent;
    private final Map<String, PropertyHandler> properties = new ConcurrentHashMap<>();
    private final Map<String, MethodHandler> methods = new ConcurrentHashMap<>();
    // Typed-registration metadata, kept ALONGSIDE (not instead of) the untyped `methods` entry a
    // typed registration also installs (see methodTyped0..7 below) — so every existing dispatch
    // path (interpreter, PolyTypeRegistry.callMethod, ScriptValue.callMethod, the current JIT
    // direct-dispatch specialization) keeps working completely unchanged against `methods`, while
    // a FUTURE JIT specialization can additionally consult this map to skip ScriptValue coercion
    // entirely for a method that opted in. Never read by anything today except resolveTypedMethod.
    private final Map<String, TypedMethodDescriptor> typedMethods = new ConcurrentHashMap<>();
    private DefaultPropertyHandler defaultProperty = null;
    private DefaultMethodHandler defaultMethod = null;

    PolyType(String name, PolyType parent) {
        this.name = name;
        this.parent = parent;
    }

    public String name() { return name; }
    public PolyType parent() { return parent; }

    public PolyType property(String name, PropertyHandler handler) {
        properties.put(name, handler);
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public PolyType method(String name, MethodHandler handler) {
        methods.put(name, handler);
        // Drop any typed descriptor this name had: `methods` is now an UNTYPED handler, and a
        // leftover typedMethods entry would still advertise the old native signature to
        // resolveTypedMethod — so anything reading that map (PolyClassGenerator) would keep
        // calling the replaced handler while the interpreter, which only ever reads `methods`,
        // correctly used the new one. The two maps must never disagree about what's registered.
        typedMethods.remove(name);
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public PolyType replaceMethod(String name, MethodHandler handler) {
        methods.put(name, handler);
        typedMethods.remove(name); // see method(...) — keeps the two maps consistent
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public PolyType replaceProperty(String name, PropertyHandler handler) {
        properties.put(name, handler);
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    // --- Typed method registration -----------------------------------------------------------
    //
    // The untyped MethodHandler above (Object instance, List<ScriptValue> args) -> ScriptValue is
    // deliberately erased of all type information — nothing for a compiler to introspect. These
    // methodTypedN(...) overloads let a call site register a handler with REAL native parameter/
    // return types (e.g. (MachineRef, String, double) -> boolean) while still producing a fully
    // conforming untyped MethodHandler under the hood, via a TypeCodec<T> per argument/return that
    // knows how to decode a ScriptValue -> T and encode a T -> ScriptValue. That decode/encode
    // wrapper is installed into `methods` exactly the way `.method(name, handler)` would install
    // one by hand, so every existing untyped call path (interpreter, PolyTypeRegistry.callMethod,
    // ScriptValue.callMethod, the current JIT direct-dispatch specialization) is completely
    // unaffected — this is purely additive.
    //
    // A TypedMethodDescriptor recording the argument/return TypeCodecs (and the original typed
    // handler instance) is ALSO stashed in `typedMethods`, queryable via resolveTypedMethod(name).
    // Nothing reads that map today; it exists so a FUTURE JIT specialization can look up a typed
    // descriptor for a known top-level call and invoke the real native-typed handler directly,
    // skipping ScriptValue boxing/coercion entirely. Building that codegen is explicitly out of
    // scope for this pass — only the metadata needs to already be there.
    //
    // `onMissingArgs` mirrors the "not enough args -> return false/NULL" short-circuit nearly
    // every hand-written untyped method already does at its very first line — since R's shape
    // isn't known generically, the caller supplies the already-correct-for-R fallback value
    // (e.g. Boolean.FALSE for a boolean-returning method, ScriptValue.NULL for a method whose
    // return type is genuinely dynamic and left as TypeCodecs.RAW).

    /** Register a 0-arg typed method. No missing-args case exists at this arity. */
    public <I, R> PolyType methodTyped0(String name, TypeCodec<R> ret, TypedMethodHandler0<I, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance))));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(), ret, handler));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, R> PolyType methodTyped1(String name, TypeCodec<A1> a1, TypeCodec<R> ret, R onMissingArgs,
                                             TypedMethodHandler1<I, A1, R> handler) {
        methods.put(name, (instance, args) -> {
            if (args.size() < 1) return ret.encode(onMissingArgs);
            return ret.encode(handler.call(cast(instance), a1.decode(args.get(0))));
        });
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1), ret, handler));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, R> PolyType methodTyped2(String name, TypeCodec<A1> a1, TypeCodec<A2> a2, TypeCodec<R> ret,
                                                 R onMissingArgs, TypedMethodHandler2<I, A1, A2, R> handler) {
        methods.put(name, (instance, args) -> {
            if (args.size() < 2) return ret.encode(onMissingArgs);
            return ret.encode(handler.call(cast(instance), a1.decode(args.get(0)), a2.decode(args.get(1))));
        });
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2), ret, handler));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, R> PolyType methodTyped3(String name, TypeCodec<A1> a1, TypeCodec<A2> a2, TypeCodec<A3> a3,
                                                      TypeCodec<R> ret, R onMissingArgs,
                                                      TypedMethodHandler3<I, A1, A2, A3, R> handler) {
        methods.put(name, (instance, args) -> {
            if (args.size() < 3) return ret.encode(onMissingArgs);
            return ret.encode(handler.call(cast(instance), a1.decode(args.get(0)), a2.decode(args.get(1)), a3.decode(args.get(2))));
        });
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3), ret, handler));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, R> PolyType methodTyped4(String name, TypeCodec<A1> a1, TypeCodec<A2> a2, TypeCodec<A3> a3,
                                                          TypeCodec<A4> a4, TypeCodec<R> ret, R onMissingArgs,
                                                          TypedMethodHandler4<I, A1, A2, A3, A4, R> handler) {
        methods.put(name, (instance, args) -> {
            if (args.size() < 4) return ret.encode(onMissingArgs);
            return ret.encode(handler.call(cast(instance), a1.decode(args.get(0)), a2.decode(args.get(1)),
                    a3.decode(args.get(2)), a4.decode(args.get(3))));
        });
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4), ret, handler));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, A5, R> PolyType methodTyped5(String name, TypeCodec<A1> a1, TypeCodec<A2> a2, TypeCodec<A3> a3,
                                                              TypeCodec<A4> a4, TypeCodec<A5> a5, TypeCodec<R> ret, R onMissingArgs,
                                                              TypedMethodHandler5<I, A1, A2, A3, A4, A5, R> handler) {
        methods.put(name, (instance, args) -> {
            if (args.size() < 5) return ret.encode(onMissingArgs);
            return ret.encode(handler.call(cast(instance), a1.decode(args.get(0)), a2.decode(args.get(1)),
                    a3.decode(args.get(2)), a4.decode(args.get(3)), a5.decode(args.get(4))));
        });
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4, a5), ret, handler));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, A5, A6, R> PolyType methodTyped6(String name, TypeCodec<A1> a1, TypeCodec<A2> a2, TypeCodec<A3> a3,
                                                                  TypeCodec<A4> a4, TypeCodec<A5> a5, TypeCodec<A6> a6,
                                                                  TypeCodec<R> ret, R onMissingArgs,
                                                                  TypedMethodHandler6<I, A1, A2, A3, A4, A5, A6, R> handler) {
        methods.put(name, (instance, args) -> {
            if (args.size() < 6) return ret.encode(onMissingArgs);
            return ret.encode(handler.call(cast(instance), a1.decode(args.get(0)), a2.decode(args.get(1)),
                    a3.decode(args.get(2)), a4.decode(args.get(3)), a5.decode(args.get(4)), a6.decode(args.get(5))));
        });
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4, a5, a6), ret, handler));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, A5, A6, A7, R> PolyType methodTyped7(String name, TypeCodec<A1> a1, TypeCodec<A2> a2, TypeCodec<A3> a3,
                                                                      TypeCodec<A4> a4, TypeCodec<A5> a5, TypeCodec<A6> a6, TypeCodec<A7> a7,
                                                                      TypeCodec<R> ret, R onMissingArgs,
                                                                      TypedMethodHandler7<I, A1, A2, A3, A4, A5, A6, A7, R> handler) {
        methods.put(name, (instance, args) -> {
            if (args.size() < 7) return ret.encode(onMissingArgs);
            return ret.encode(handler.call(cast(instance), a1.decode(args.get(0)), a2.decode(args.get(1)),
                    a3.decode(args.get(2)), a4.decode(args.get(3)), a5.decode(args.get(4)), a6.decode(args.get(5)),
                    a7.decode(args.get(6))));
        });
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4, a5, a6, a7), ret, handler));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    // --- Optional-argument typed registration -------------------------------------------------
    //
    // methodTypedN's `onMissingArgs` SKIPS the handler entirely and returns a fixed value. That is
    // wrong for the single most common shape in this codebase:
    //
    //     .method("foo", (o, args) -> { int n = args.isEmpty() ? 0 : (int) args.get(0).asNum();
    //                                   ref(o).doTheThing(n); return ScriptValue.of(true); })
    //
    // Here a missing argument gets a DEFAULT and the body still runs its side effect — so
    // onMissingArgs would silently drop that side effect. These methodTypedOptN overloads express
    // it correctly: every argument is optional, each with its own default value, and the handler
    // ALWAYS runs. That unblocks the large set of methods previously stuck on the untyped API.
    //
    // Extra trailing arguments are ignored, and missing ones take their default — matching what the
    // hand-written bodies above already do.
    //
    // A `null` DEFAULT IS SUPPORTED AND LOAD-BEARING. Many registrations need "argument absent"
    // to mean "don't touch this field" or "take the branch that returns the instance unchanged",
    // rather than "substitute some value" — they pass a null default and branch on `param == null`
    // inside the body. That test is exact, because a PRESENT argument can never decode to Java
    // null: TypeCodecs.DOUBLE/BOOL return primitives, TypeCodecs.STRING's asStr() is total (it
    // yields the literal "null" for a NULL ScriptValue, never a Java null), TypeCodecs.RAW yields
    // the ScriptValue itself, and the args list never holds nulls.
    //
    // Therefore the defaults below MUST be stored in a null-permitting list — Collections
    // .singletonList / Arrays.asList. Switching them to List.of(...) would throw at REGISTRATION
    // time for every null-default method, i.e. at plugin startup.
    //
    // (Sharp edge worth knowing when choosing a non-null String default: ScriptValue.NULL.asStr()
    // is the string "null", not "". A defaulted string flowing into an id/name lookup looks up
    // "null" rather than blank.)

    public <I, A1, R> PolyType methodTypedOpt1(String name, TypeCodec<A1> a1, A1 def1, TypeCodec<R> ret,
                                                TypedMethodHandler1<I, A1, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance),
                args.size() > 0 ? a1.decode(args.get(0)) : def1)));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1), ret, handler,
                java.util.Collections.singletonList(def1)));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, R> PolyType methodTypedOpt2(String name, TypeCodec<A1> a1, A1 def1,
                                                    TypeCodec<A2> a2, A2 def2, TypeCodec<R> ret,
                                                    TypedMethodHandler2<I, A1, A2, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance),
                args.size() > 0 ? a1.decode(args.get(0)) : def1,
                args.size() > 1 ? a2.decode(args.get(1)) : def2)));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2), ret, handler,
                java.util.Arrays.asList(def1, def2)));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, R> PolyType methodTypedOpt3(String name, TypeCodec<A1> a1, A1 def1,
                                                        TypeCodec<A2> a2, A2 def2, TypeCodec<A3> a3, A3 def3,
                                                        TypeCodec<R> ret,
                                                        TypedMethodHandler3<I, A1, A2, A3, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance),
                args.size() > 0 ? a1.decode(args.get(0)) : def1,
                args.size() > 1 ? a2.decode(args.get(1)) : def2,
                args.size() > 2 ? a3.decode(args.get(2)) : def3)));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3), ret, handler,
                java.util.Arrays.asList(def1, def2, def3)));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, R> PolyType methodTypedOpt4(String name, TypeCodec<A1> a1, A1 def1,
                                                            TypeCodec<A2> a2, A2 def2, TypeCodec<A3> a3, A3 def3,
                                                            TypeCodec<A4> a4, A4 def4, TypeCodec<R> ret,
                                                            TypedMethodHandler4<I, A1, A2, A3, A4, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance),
                args.size() > 0 ? a1.decode(args.get(0)) : def1,
                args.size() > 1 ? a2.decode(args.get(1)) : def2,
                args.size() > 2 ? a3.decode(args.get(2)) : def3,
                args.size() > 3 ? a4.decode(args.get(3)) : def4)));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4), ret, handler,
                java.util.Arrays.asList(def1, def2, def3, def4)));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, A5, R> PolyType methodTypedOpt5(String name, TypeCodec<A1> a1, A1 def1,
                                                                TypeCodec<A2> a2, A2 def2, TypeCodec<A3> a3, A3 def3,
                                                                TypeCodec<A4> a4, A4 def4, TypeCodec<A5> a5, A5 def5,
                                                                TypeCodec<R> ret,
                                                                TypedMethodHandler5<I, A1, A2, A3, A4, A5, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance),
                args.size() > 0 ? a1.decode(args.get(0)) : def1,
                args.size() > 1 ? a2.decode(args.get(1)) : def2,
                args.size() > 2 ? a3.decode(args.get(2)) : def3,
                args.size() > 3 ? a4.decode(args.get(3)) : def4,
                args.size() > 4 ? a5.decode(args.get(4)) : def5)));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4, a5), ret, handler,
                java.util.Arrays.asList(def1, def2, def3, def4, def5)));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, A5, A6, R> PolyType methodTypedOpt6(String name, TypeCodec<A1> a1, A1 def1,
                                                                    TypeCodec<A2> a2, A2 def2, TypeCodec<A3> a3, A3 def3,
                                                                    TypeCodec<A4> a4, A4 def4, TypeCodec<A5> a5, A5 def5,
                                                                    TypeCodec<A6> a6, A6 def6, TypeCodec<R> ret,
                                                                    TypedMethodHandler6<I, A1, A2, A3, A4, A5, A6, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance),
                args.size() > 0 ? a1.decode(args.get(0)) : def1,
                args.size() > 1 ? a2.decode(args.get(1)) : def2,
                args.size() > 2 ? a3.decode(args.get(2)) : def3,
                args.size() > 3 ? a4.decode(args.get(3)) : def4,
                args.size() > 4 ? a5.decode(args.get(4)) : def5,
                args.size() > 5 ? a6.decode(args.get(5)) : def6)));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4, a5, a6), ret, handler,
                java.util.Arrays.asList(def1, def2, def3, def4, def5, def6)));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, A5, A6, A7, R> PolyType methodTypedOpt7(String name, TypeCodec<A1> a1, A1 def1,
                                                                        TypeCodec<A2> a2, A2 def2, TypeCodec<A3> a3, A3 def3,
                                                                        TypeCodec<A4> a4, A4 def4, TypeCodec<A5> a5, A5 def5,
                                                                        TypeCodec<A6> a6, A6 def6, TypeCodec<A7> a7, A7 def7,
                                                                        TypeCodec<R> ret,
                                                                        TypedMethodHandler7<I, A1, A2, A3, A4, A5, A6, A7, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance),
                args.size() > 0 ? a1.decode(args.get(0)) : def1,
                args.size() > 1 ? a2.decode(args.get(1)) : def2,
                args.size() > 2 ? a3.decode(args.get(2)) : def3,
                args.size() > 3 ? a4.decode(args.get(3)) : def4,
                args.size() > 4 ? a5.decode(args.get(4)) : def5,
                args.size() > 5 ? a6.decode(args.get(5)) : def6,
                args.size() > 6 ? a7.decode(args.get(6)) : def7)));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4, a5, a6, a7), ret, handler,
                java.util.Arrays.asList(def1, def2, def3, def4, def5, def6, def7)));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, A5, A6, A7, A8, R> PolyType methodTypedOpt8(String name,
            TypeCodec<A1> a1, A1 def1, TypeCodec<A2> a2, A2 def2, TypeCodec<A3> a3, A3 def3,
            TypeCodec<A4> a4, A4 def4, TypeCodec<A5> a5, A5 def5, TypeCodec<A6> a6, A6 def6,
            TypeCodec<A7> a7, A7 def7, TypeCodec<A8> a8, A8 def8, TypeCodec<R> ret,
            TypedMethodHandler8<I, A1, A2, A3, A4, A5, A6, A7, A8, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance),
                args.size() > 0 ? a1.decode(args.get(0)) : def1,
                args.size() > 1 ? a2.decode(args.get(1)) : def2,
                args.size() > 2 ? a3.decode(args.get(2)) : def3,
                args.size() > 3 ? a4.decode(args.get(3)) : def4,
                args.size() > 4 ? a5.decode(args.get(4)) : def5,
                args.size() > 5 ? a6.decode(args.get(5)) : def6,
                args.size() > 6 ? a7.decode(args.get(6)) : def7,
                args.size() > 7 ? a8.decode(args.get(7)) : def8)));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4, a5, a6, a7, a8), ret, handler,
                java.util.Arrays.asList(def1, def2, def3, def4, def5, def6, def7, def8)));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, A5, A6, A7, A8, A9, R> PolyType methodTypedOpt9(String name,
            TypeCodec<A1> a1, A1 def1, TypeCodec<A2> a2, A2 def2, TypeCodec<A3> a3, A3 def3,
            TypeCodec<A4> a4, A4 def4, TypeCodec<A5> a5, A5 def5, TypeCodec<A6> a6, A6 def6,
            TypeCodec<A7> a7, A7 def7, TypeCodec<A8> a8, A8 def8, TypeCodec<A9> a9, A9 def9,
            TypeCodec<R> ret, TypedMethodHandler9<I, A1, A2, A3, A4, A5, A6, A7, A8, A9, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance),
                args.size() > 0 ? a1.decode(args.get(0)) : def1,
                args.size() > 1 ? a2.decode(args.get(1)) : def2,
                args.size() > 2 ? a3.decode(args.get(2)) : def3,
                args.size() > 3 ? a4.decode(args.get(3)) : def4,
                args.size() > 4 ? a5.decode(args.get(4)) : def5,
                args.size() > 5 ? a6.decode(args.get(5)) : def6,
                args.size() > 6 ? a7.decode(args.get(6)) : def7,
                args.size() > 7 ? a8.decode(args.get(7)) : def8,
                args.size() > 8 ? a9.decode(args.get(8)) : def9)));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4, a5, a6, a7, a8, a9), ret,
                handler, java.util.Arrays.asList(def1, def2, def3, def4, def5, def6, def7, def8, def9)));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public <I, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R> PolyType methodTypedOpt10(String name,
            TypeCodec<A1> a1, A1 def1, TypeCodec<A2> a2, A2 def2, TypeCodec<A3> a3, A3 def3,
            TypeCodec<A4> a4, A4 def4, TypeCodec<A5> a5, A5 def5, TypeCodec<A6> a6, A6 def6,
            TypeCodec<A7> a7, A7 def7, TypeCodec<A8> a8, A8 def8, TypeCodec<A9> a9, A9 def9,
            TypeCodec<A10> a10, A10 def10, TypeCodec<R> ret,
            TypedMethodHandler10<I, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R> handler) {
        methods.put(name, (instance, args) -> ret.encode(handler.call(cast(instance),
                args.size() > 0 ? a1.decode(args.get(0)) : def1,
                args.size() > 1 ? a2.decode(args.get(1)) : def2,
                args.size() > 2 ? a3.decode(args.get(2)) : def3,
                args.size() > 3 ? a4.decode(args.get(3)) : def4,
                args.size() > 4 ? a5.decode(args.get(4)) : def5,
                args.size() > 5 ? a6.decode(args.get(5)) : def6,
                args.size() > 6 ? a7.decode(args.get(6)) : def7,
                args.size() > 7 ? a8.decode(args.get(7)) : def8,
                args.size() > 8 ? a9.decode(args.get(8)) : def9,
                args.size() > 9 ? a10.decode(args.get(9)) : def10)));
        typedMethods.put(name, new TypedMethodDescriptor(name, List.of(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10), ret,
                handler, java.util.Arrays.asList(def1, def2, def3, def4, def5, def6, def7, def8, def9, def10)));
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    @SuppressWarnings("unchecked")
    private static <I> I cast(Object instance) { return (I) instance; }

    /** Walks the parent chain like resolveMethod, but for typed-registration metadata. Returns
     *  null for any method registered only via the untyped {@code .method(...)} API — there is no
     *  obligation for a method to have a typed descriptor. */
    public TypedMethodDescriptor resolveTypedMethod(String method) {
        TypedMethodDescriptor d = typedMethods.get(method);
        if (d != null) return d;
        if (parent != null) return parent.resolveTypedMethod(method);
        return null;
    }

    /** Fallback handler called when no named property matches. */
    public PolyType defaultProperty(DefaultPropertyHandler handler) {
        this.defaultProperty = handler;
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    /** Fallback handler called when no named method matches. */
    public PolyType defaultMethod(DefaultMethodHandler handler) {
        this.defaultMethod = handler;
        PolyTypeRegistry.notifyMutation();
        return this;
    }

    public PropertyHandler resolveProperty(String prop) {
        PropertyHandler h = properties.get(prop);
        if (h != null) return h;
        if (defaultProperty != null) {
            DefaultPropertyHandler dp = defaultProperty;
            return instance -> dp.get(instance, prop);
        }
        if (parent != null) return parent.resolveProperty(prop);
        return null;
    }

    public MethodHandler resolveMethod(String method) {
        MethodHandler h = methods.get(method);
        if (h != null) return h;
        if (defaultMethod != null) {
            DefaultMethodHandler dm = defaultMethod;
            return (instance, args) -> dm.call(instance, method, args);
        }
        if (parent != null) return parent.resolveMethod(method);
        return null;
    }

    /** This type's OWN methods, excluding anything inherited — what {@link PolyClassGenerator} needs
     *  to decide which members a generated subclass must declare itself versus inherit from its
     *  parent's generated class. */
    public Set<String> ownMethodNames() {
        return new LinkedHashSet<>(methods.keySet());
    }

    /** This type's OWN properties, excluding anything inherited — see {@link #ownMethodNames()}. */
    public Set<String> ownPropertyNames() {
        return new LinkedHashSet<>(properties.keySet());
    }

    public Set<String> allPropertyNames() {
        Set<String> result = new LinkedHashSet<>();
        if (parent != null) result.addAll(parent.allPropertyNames());
        result.addAll(properties.keySet());
        return result;
    }

    public Set<String> allMethodNames() {
        Set<String> result = new LinkedHashSet<>();
        if (parent != null) result.addAll(parent.allMethodNames());
        result.addAll(methods.keySet());
        return result;
    }

    @FunctionalInterface
    public interface PropertyHandler {
        ScriptValue get(Object instance);
    }

    @FunctionalInterface
    public interface MethodHandler {
        ScriptValue call(Object instance, List<ScriptValue> args);
    }

    @FunctionalInterface
    public interface DefaultPropertyHandler {
        ScriptValue get(Object instance, String propertyName);
    }

    @FunctionalInterface
    public interface DefaultMethodHandler {
        ScriptValue call(Object instance, String methodName, List<ScriptValue> args);
    }

    // --- Typed registration support -----------------------------------------------------------

    @FunctionalInterface
    public interface TypedMethodHandler0<I, R> {
        R call(I instance);
    }

    @FunctionalInterface
    public interface TypedMethodHandler1<I, A1, R> {
        R call(I instance, A1 arg1);
    }

    @FunctionalInterface
    public interface TypedMethodHandler2<I, A1, A2, R> {
        R call(I instance, A1 arg1, A2 arg2);
    }

    @FunctionalInterface
    public interface TypedMethodHandler3<I, A1, A2, A3, R> {
        R call(I instance, A1 arg1, A2 arg2, A3 arg3);
    }

    @FunctionalInterface
    public interface TypedMethodHandler4<I, A1, A2, A3, A4, R> {
        R call(I instance, A1 arg1, A2 arg2, A3 arg3, A4 arg4);
    }

    @FunctionalInterface
    public interface TypedMethodHandler5<I, A1, A2, A3, A4, A5, R> {
        R call(I instance, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
    }

    @FunctionalInterface
    public interface TypedMethodHandler6<I, A1, A2, A3, A4, A5, A6, R> {
        R call(I instance, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6);
    }

    @FunctionalInterface
    public interface TypedMethodHandler7<I, A1, A2, A3, A4, A5, A6, A7, R> {
        R call(I instance, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7);
    }

    // Arities 8-10 exist only in the optional-argument (methodTypedOptN) family. Nothing in this
    // codebase registers a REQUIRED-argument method that wide; what does go this wide is builder-
    // style calls (UI widgets, map markers, particles) where every slot past the first few is
    // optional. Adding the required variants too would be dead code.

    @FunctionalInterface
    public interface TypedMethodHandler8<I, A1, A2, A3, A4, A5, A6, A7, A8, R> {
        R call(I instance, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8);
    }

    @FunctionalInterface
    public interface TypedMethodHandler9<I, A1, A2, A3, A4, A5, A6, A7, A8, A9, R> {
        R call(I instance, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9);
    }

    @FunctionalInterface
    public interface TypedMethodHandler10<I, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R> {
        R call(I instance, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7, A8 arg8, A9 arg9,
               A10 arg10);
    }

    /**
     * Knows how to convert between a native Java type {@code T} and the boxed {@link ScriptValue}
     * union every untyped handler already speaks. A typed registration uses one TypeCodec per
     * declared argument (to decode) and one for its return value (to encode), so the untyped
     * MethodHandler it installs behaves identically to a hand-written one.
     *
     * <p>{@code type()} is exposed so future JIT codegen can identify, at compile time, exactly
     * which native type a descriptor's slot uses without invoking anything.
     */
    public interface TypeCodec<T> {
        Class<T> type();
        T decode(ScriptValue value);
        ScriptValue encode(T value);
    }

    /**
     * Metadata captured by a methodTypedN(...) registration: the method's name, its argument
     * codecs in declared order, its return codec, and the original typed handler instance (kept
     * as {@code Object} — callers that want to actually invoke it natively need to know, out of
     * band, which of the TypedMethodHandlerN interfaces it implements; today nothing does, this is
     * purely for a future JIT specialization to consume). Not used by any dispatch path today —
     * see resolveTypedMethod.
     */
    public record TypedMethodDescriptor(String name, List<TypeCodec<?>> argTypes, TypeCodec<?> returnType,
                                         Object handler, List<Object> defaults) {
        /** A registration with no optional arguments — every argument required (methodTypedN). */
        public TypedMethodDescriptor(String name, List<TypeCodec<?>> argTypes, TypeCodec<?> returnType, Object handler) {
            this(name, argTypes, returnType, handler, List.of());
        }
        public int arity() { return argTypes.size(); }
        /** Whether every argument is optional with a default (methodTypedOptN), so a call site may
         *  legally pass fewer than {@link #arity()} arguments. */
        public boolean hasDefaults() { return !defaults.isEmpty(); }
    }
}
