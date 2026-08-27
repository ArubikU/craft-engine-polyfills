package dev.arubik.craftengine.script;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.SwitchPoint;
import java.util.List;

/**
 * An {@code invokedynamic} INLINE CACHE for member access whose receiver type isn't known at compile
 * time.
 *
 * <p>{@link PolyClassGenerator} handles the case where it IS known — {@code Machine.foo()}, where
 * "Machine" is a registered {@link PolyType} name, compiles to a direct call on a generated class.
 * But most real member access in {@code .pf} scripts is NOT of that shape:
 *
 * <pre>
 *   contraption = Machine.contraption      # a plain variable...
 *   contraption.set_spin(axis, rpm)        # ...so this receiver's type is unknown statically
 *   warp_row(name).get("id")               # and so is any chained call
 * </pre>
 *
 * Those previously went through {@link ScriptFormula#memberCall} on EVERY evaluation: a
 * {@code PolyTypeRegistry} map lookup, a {@code resolveMethod} walk up the parent chain, and a
 * switch over the {@link ScriptValue} shape — all repeated per tick, per machine, for a receiver
 * whose type is in practice the SAME every single time.
 *
 * <p>So: each such call site becomes an {@code invokedynamic} whose target starts as a slow generic
 * call and then RELINKS itself, after observing the receiver, into
 * {@code if (typeName == "Contraption") <resolved handler>(...) else <previous target>}. Steady
 * state is a string comparison plus a direct interface call — no registry lookups at all. Call sites
 * are per-instruction, so each one caches independently.
 *
 * <h2>Correctness</h2>
 * <ul>
 *   <li>The guard is the same one {@link ScriptBytecodeCompiler}'s static specialization uses, and
 *       for the same load-bearing reason: it demands a {@link ScriptValue.Obj} whose instance is
 *       non-null and NOT a {@link PolyClass}, then an exact {@code typeName} match. A
 *       {@code PolyClass} owns its own dispatch and must never be routed to a
 *       {@code PolyTypeRegistry}-resolved handler that merely shares its name.
 *   <li>Every cached target is guarded by a {@link SwitchPoint} that {@link PolyTypeRegistry}
 *       invalidates on ANY mutation. A {@code replaceMethod} therefore drops every inline cache in
 *       the process, and the next call re-resolves — the same guarantee
 *       {@code PolyClassGenerator.refresh()} provides for generated classes. Without this, a cached
 *       handler would outlive the registration it came from.
 *   <li>Anything the fast path can't prove — a non-Obj receiver, a {@code PolyClass}, an
 *       unresolvable member — falls through to the exact same {@code memberCall}/{@code memberGet}
 *       the interpreter uses, so behaviour is identical, only the route differs.
 *   <li>After {@link #MAX_CHAIN_DEPTH} distinct receiver types the site goes MEGAMORPHIC and pins
 *       itself to the generic path, rather than growing an unbounded guard chain that would be
 *       slower than the thing it replaced.
 * </ul>
 */
public final class PolyDispatch {

    private PolyDispatch() {}

    /** Distinct receiver types to cache at one call site before giving up and going generic. Real
     *  {@code .pf} sites are overwhelmingly monomorphic; a couple of extra shapes are worth a guard,
     *  a long chain is not. */
    private static final int MAX_CHAIN_DEPTH = 3;

    /** Guards every cached target. Swapped (and the old one invalidated, which deoptimises every
     *  site holding it) whenever anything in the registry changes. */
    private static volatile SwitchPoint switchPoint = new SwitchPoint();

    static {
        PolyTypeRegistry.addMutationListener(PolyDispatch::invalidateAll);
    }

    /** Ensures the static initializer above has run, so mutations are observed even before the first
     *  call site links. */
    static void init() { /* triggers <clinit> */ }

    private static synchronized void invalidateAll() {
        SwitchPoint old = switchPoint;
        switchPoint = new SwitchPoint();
        SwitchPoint.invalidateAll(new SwitchPoint[]{old});
    }

    // ---- Bootstraps -------------------------------------------------------

    /** Bootstrap for {@code recv.method(args)}. Invoked type:
     *  {@code (ScriptValue, List, ScriptContext) -> ScriptValue}. */
    public static CallSite bootstrapCall(MethodHandles.Lookup lookup, String invokedName, MethodType type,
                                          String methodName) {
        CallIC site = new CallIC(type, methodName);
        site.setTarget(FALLBACK_CALL.bindTo(site).asType(type));
        return site;
    }

    /** Bootstrap for {@code recv.property}. Invoked type:
     *  {@code (ScriptValue, ScriptContext) -> ScriptValue}. */
    public static CallSite bootstrapGet(MethodHandles.Lookup lookup, String invokedName, MethodType type,
                                         String propName) {
        GetIC site = new GetIC(type, propName);
        site.setTarget(FALLBACK_GET.bindTo(site).asType(type));
        return site;
    }

    private static final class CallIC extends MutableCallSite {
        final String methodName;
        int depth;
        CallIC(MethodType type, String methodName) { super(type); this.methodName = methodName; }
    }

    private static final class GetIC extends MutableCallSite {
        final String propName;
        int depth;
        GetIC(MethodType type, String propName) { super(type); this.propName = propName; }
    }

    // ---- Runtime helpers the linked MethodHandles are built from ----------

    /** The one guard, shared by both shapes. See the class doc for why the {@link PolyClass} check
     *  is load-bearing rather than an optimisation. */
    public static boolean guard(ScriptValue sv, String expectedTypeName) {
        if (!(sv instanceof ScriptValue.Obj o)) return false;
        Object inst = o.instance();
        return inst != null && !(inst instanceof PolyClass) && expectedTypeName.equals(o.typeName());
    }

    public static ScriptValue invokeMethod(PolyType.MethodHandler h, ScriptValue sv, List<ScriptValue> args,
                                            ScriptContext ctx) {
        ScriptValue r = h.call(((ScriptValue.Obj) sv).instance(), args);
        return r != null ? r : ScriptValue.NULL;
    }

    public static ScriptValue invokeProperty(PolyType.PropertyHandler h, ScriptValue sv, ScriptContext ctx) {
        ScriptValue r = h.get(((ScriptValue.Obj) sv).instance());
        return r != null ? r : ScriptValue.NULL;
    }

    /** Ran on a cache miss: computes the result the always-correct way, then tries to install a
     *  guarded fast path for the receiver type just observed. */
    public static ScriptValue fallbackCall(CallIC site, ScriptValue sv, List<ScriptValue> args, ScriptContext ctx) {
        String typeName = cacheableTypeName(sv);
        if (typeName != null && site.depth < MAX_CHAIN_DEPTH) {
            PolyType type = PolyTypeRegistry.get(typeName);
            PolyType.MethodHandler handler = type != null ? type.resolveMethod(site.methodName) : null;
            if (handler != null) {
                MethodHandle target = MethodHandles.insertArguments(INVOKE_METHOD, 0, handler)
                        .asType(site.type());
                MethodHandle test = MethodHandles.dropArguments(
                        MethodHandles.insertArguments(GUARD, 1, typeName), 1,
                        site.type().parameterList().subList(1, site.type().parameterCount()));
                MethodHandle guarded = MethodHandles.guardWithTest(test, target, site.getTarget());
                site.depth++;
                site.setTarget(switchPoint.guardWithTest(guarded, FALLBACK_CALL.bindTo(site).asType(site.type())));
                return invokeMethod(handler, sv, args, ctx);
            }
        }
        if (site.depth >= MAX_CHAIN_DEPTH) {
            // Megamorphic: stop growing the chain and pin the generic path for good.
            site.setTarget(MethodHandles.insertArguments(MEMBER_CALL, 1, site.methodName).asType(site.type()));
        }
        return ScriptFormula.memberCall(sv, site.methodName, args, ctx);
    }

    public static ScriptValue fallbackGet(GetIC site, ScriptValue sv, ScriptContext ctx) {
        String typeName = cacheableTypeName(sv);
        if (typeName != null && site.depth < MAX_CHAIN_DEPTH) {
            PolyType type = PolyTypeRegistry.get(typeName);
            PolyType.PropertyHandler handler = type != null ? type.resolveProperty(site.propName) : null;
            if (handler != null) {
                MethodHandle target = MethodHandles.insertArguments(INVOKE_PROPERTY, 0, handler)
                        .asType(site.type());
                MethodHandle test = MethodHandles.dropArguments(
                        MethodHandles.insertArguments(GUARD, 1, typeName), 1,
                        site.type().parameterList().subList(1, site.type().parameterCount()));
                MethodHandle guarded = MethodHandles.guardWithTest(test, target, site.getTarget());
                site.depth++;
                site.setTarget(switchPoint.guardWithTest(guarded, FALLBACK_GET.bindTo(site).asType(site.type())));
                return invokeProperty(handler, sv, ctx);
            }
        }
        if (site.depth >= MAX_CHAIN_DEPTH) {
            site.setTarget(MethodHandles.insertArguments(MEMBER_GET, 1, site.propName).asType(site.type()));
        }
        return ScriptFormula.memberGet(sv, site.propName, ctx);
    }

    /** The receiver's type name if it is safe to cache on, else null — mirrors {@link #guard}. */
    private static String cacheableTypeName(ScriptValue sv) {
        if (!(sv instanceof ScriptValue.Obj o)) return null;
        Object inst = o.instance();
        if (inst == null || inst instanceof PolyClass) return null;
        return o.typeName();
    }

    private static final MethodHandle GUARD;
    private static final MethodHandle INVOKE_METHOD;
    private static final MethodHandle INVOKE_PROPERTY;
    private static final MethodHandle FALLBACK_CALL;
    private static final MethodHandle FALLBACK_GET;
    private static final MethodHandle MEMBER_CALL;
    private static final MethodHandle MEMBER_GET;

    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            GUARD = l.findStatic(PolyDispatch.class, "guard",
                    MethodType.methodType(boolean.class, ScriptValue.class, String.class));
            INVOKE_METHOD = l.findStatic(PolyDispatch.class, "invokeMethod",
                    MethodType.methodType(ScriptValue.class, PolyType.MethodHandler.class, ScriptValue.class,
                            List.class, ScriptContext.class));
            INVOKE_PROPERTY = l.findStatic(PolyDispatch.class, "invokeProperty",
                    MethodType.methodType(ScriptValue.class, PolyType.PropertyHandler.class, ScriptValue.class,
                            ScriptContext.class));
            FALLBACK_CALL = l.findStatic(PolyDispatch.class, "fallbackCall",
                    MethodType.methodType(ScriptValue.class, CallIC.class, ScriptValue.class, List.class,
                            ScriptContext.class));
            FALLBACK_GET = l.findStatic(PolyDispatch.class, "fallbackGet",
                    MethodType.methodType(ScriptValue.class, GetIC.class, ScriptValue.class, ScriptContext.class));
            MEMBER_CALL = l.findStatic(ScriptFormula.class, "memberCall",
                    MethodType.methodType(ScriptValue.class, ScriptValue.class, String.class, List.class,
                            ScriptContext.class));
            MEMBER_GET = l.findStatic(ScriptFormula.class, "memberGet",
                    MethodType.methodType(ScriptValue.class, ScriptValue.class, String.class, ScriptContext.class));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
