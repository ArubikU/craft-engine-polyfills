package dev.arubik.craftengine.script;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.objectweb.asm.Opcodes.*;

/**
 * Generates a REAL Java class — a "PolyClass" — per registered {@link PolyType}: the thing a script
 * value of that type UNBOXES into. A "Machine" script value becomes an instance of a generated
 * class wrapping the real underlying object (the {@code MachineRef}), exposing a genuinely
 * native-signatured Java method per registered member.
 *
 * <p>Shape of a generated class:
 * <ul>
 *   <li>a {@code public} constructor taking the wrapped instance, plus a static {@code of(Object)}
 *       factory;
 *   <li>a {@code protected final instance} field holding the real wrapped object — declared only by
 *       the ROOT of a generated hierarchy, since subclasses inherit it;
 *   <li>one {@code private static volatile} field per member, holding its RESOLVED handler — this
 *       is the whole point: no {@code PolyTypeRegistry.get}/{@code resolveMethod} lookup on any
 *       call, ever;
 *   <li>one real instance method per member. A {@code methodTypedN}/{@code methodTypedOptN}
 *       registration whose codecs are all known {@link TypeCodecs} singletons gets a genuinely
 *       native signature ({@code double get_x()}, {@code boolean set_typed(String, double)}) AND an
 *       erased companion shim, because a call site may legally pass fewer arguments than the fixed
 *       native arity (per-argument defaults, or {@code onMissingArgs}) and the shim is what applies
 *       that. Every other method gets only the erased {@code ScriptValue name(List)} shape; every
 *       property gets {@code ScriptValue name()};
 *   <li>a static {@code refresh()} that re-resolves every one of those handler fields BY NAME.
 * </ul>
 *
 * <p>The generated hierarchy MIRRORS the {@link PolyType} hierarchy — {@code PolyClassMachine extends
 * PolyClassBlock} — so a child emits only the members it declares itself and inherits the rest by
 * ordinary virtual dispatch. A child that re-registers an inherited member declares its own method,
 * which wins.
 *
 * <h2>Staleness — the hard part, and why {@code refresh()} exists</h2>
 * A compiled {@code .pf} formula is cached forever by {@code ScriptFormula.CACHE}, keyed on the
 * expression TEXT. Its bytecode references one specific generated class by name. So if a wrapper
 * baked its handlers in permanently, the FIRST registration ever seen would be pinned for the life
 * of the process, and any later {@code define}/{@code extend}/{@code replaceMethod} would be
 * silently ignored — running the old handler forever, which is wrong behavior, not merely a missed
 * optimization. (Every other dispatch tier in {@link ScriptBytecodeCompiler} deliberately
 * re-resolves at runtime for exactly this reason.)
 *
 * <p>Instead: {@link PolyTypeRegistry} notifies this class after EVERY mutation, and every live
 * generated class re-runs {@code refresh()}, re-resolving each handler BY NAME. A wrapper therefore
 * always dispatches to whatever is registered right now, while still costing zero lookups per call.
 * The cache is NOT invalidated — exactly one class is ever generated per type name (see {@link
 * #getOrGenerate}), since classes are never unloaded and minting a fresh one per registration would
 * leak. {@link #buildAll()} builds every type's wrapper once registration is complete, so a wrapper
 * covers its type's full member set; a member added after that simply isn't specialized.
 *
 * <p>A member can also change SHAPE, not just handler — a typed method re-registered with different
 * codecs or arity, or replaced by an untyped one, while a generated native signature is fixed
 * forever. {@code refresh()} passes the exact signature it was generated for to {@link
 * PolyClassRuntime#resolveTypedHandler}, which returns null on ANY mismatch; the generated method
 * then routes through {@link PolyClassRuntime#genericCall} instead. A shape change degrades to
 * correct-but-slower, never to wrong.
 *
 * <p>Classes are defined via {@link MethodHandles.Lookup#defineClass}, NOT {@code
 * defineHiddenClass}: a hidden class is deliberately unresolvable by ordinary symbolic references
 * from other classes, so the {@code NEW}/{@code INVOKEVIRTUAL} that {@link ScriptBytecodeCompiler}
 * emits against it by name would fail to link. They are also {@code ACC_PUBLIC} because those
 * calling classes live in a different package AND a different class loader.
 */
final class PolyClassGenerator {
    private PolyClassGenerator() {}

    private static final Logger LOG = Logger.getLogger(PolyClassGenerator.class.getName());
    /** sanitized type name -> how many classes have been generated for it, so a rebuild can pick a
     *  non-colliding name. Normally every entry stays at 1. */
    private static final ConcurrentHashMap<String, Integer> GENERATIONS = new ConcurrentHashMap<>();

    /** name -> the wrapper generated for it. Never invalidated: exactly one class per type name for
     *  the life of the process (see {@link #getOrGenerate}). */
    private static final ConcurrentHashMap<String, GeneratedPolyClass> CACHE = new ConcurrentHashMap<>();
    /** Every wrapper ever generated, each with a handle to its own static refresh(), so a registry
     *  mutation can re-resolve all of their handler fields. */
    private static final CopyOnWriteArrayList<MethodHandle> LIVE_REFRESHERS = new CopyOnWriteArrayList<>();

    static {
        PolyTypeRegistry.addMutationListener(PolyClassGenerator::onRegistryMutation);
    }

    /** Ensures the static initializer above has run, so mutations are observed even if nothing has
     *  asked for a wrapper yet. Called by {@link ScriptBytecodeCompiler} before it consults us. */
    static void init() { /* triggers <clinit> */ }

    /** The set of members a wrapper was generated for — {@code "m:"}/{@code "p:"}-prefixed so a
     *  method and a property of the same name stay distinct. Recorded for diagnostics; a wrapper is
     *  never regenerated, so this is not used to invalidate anything (see {@link #getOrGenerate}). */
    private static java.util.Set<String> memberSetOf(PolyType type) {
        java.util.Set<String> members = new java.util.HashSet<>();
        for (String m : type.allMethodNames()) members.add("m:" + m);
        for (String p : type.allPropertyNames()) members.add("p:" + p);
        return members;
    }

    /**
     * Generates the wrapper for EVERY currently-registered type in one pass — the "scan the whole
     * registry once, emit exactly one PolyClass per PolyType" entry point. Call this after all
     * registration is complete (end of {@code ScriptBootstrap.init()}/{@code reload()}) so every
     * type's wrapper covers its FULL member set before any script compiles against it.
     *
     * <p>Unlike {@link #getOrGenerate}, which never regenerates, this is the one place that will
     * rebuild a wrapper — and ONLY when the type has gained members the existing wrapper does not
     * cover. That is what makes it a "sync" pass rather than churn: in the normal flow every type is
     * fully registered before this runs, so each gets exactly one class and later calls are no-ops.
     * A reload that genuinely ADDS members to a type is the only case that mints a second class,
     * which is the correct trade — the alternative is those new members never being specialized.
     */
    static void buildAll() {
        for (String typeName : PolyTypeRegistry.typeNames()) {
            PolyType type = PolyTypeRegistry.get(typeName);
            if (type == null) continue;
            GeneratedPolyClass cached = CACHE.get(typeName);
            if (cached != null && cached.memberSet().containsAll(memberSetOf(type))) continue;
            GeneratedPolyClass fresh = generate(typeName, type);
            if (fresh != null) CACHE.put(typeName, fresh);
        }
    }

    private static void onRegistryMutation() {
        // Deliberately does NOT invalidate CACHE: exactly one class is ever generated per type name
        // (see getOrGenerate). Mutations only re-point the existing wrappers' handler fields at
        // whatever is registered now, which is what keeps already-compiled formulas correct.
        for (MethodHandle refresher : LIVE_REFRESHERS) {
            try {
                refresher.invokeExact();
            } catch (Throwable t) {
                LOG.log(Level.WARNING, t, () -> "[CEPolyfills] [JIT] PolyClass refresh failed; that wrapper's"
                        + " members will fall back to generic dispatch");
            }
        }
    }

    private static final String OBJECT = "java/lang/Object";
    private static final String STRING = "java/lang/String";
    private static final String LIST = "java/util/List";
    private static final String VALUE = "dev/arubik/craftengine/script/ScriptValue";
    private static final String RUNTIME = "dev/arubik/craftengine/script/PolyClassRuntime";
    private static final String METHOD_HANDLER = "dev/arubik/craftengine/script/PolyType$MethodHandler";
    private static final String PROPERTY_HANDLER = "dev/arubik/craftengine/script/PolyType$PropertyHandler";
    private static final String[] TYPED_HANDLER_IFACE = {
            "dev/arubik/craftengine/script/PolyType$TypedMethodHandler0",
            "dev/arubik/craftengine/script/PolyType$TypedMethodHandler1",
            "dev/arubik/craftengine/script/PolyType$TypedMethodHandler2",
            "dev/arubik/craftengine/script/PolyType$TypedMethodHandler3",
            "dev/arubik/craftengine/script/PolyType$TypedMethodHandler4",
            "dev/arubik/craftengine/script/PolyType$TypedMethodHandler5",
            "dev/arubik/craftengine/script/PolyType$TypedMethodHandler6",
            "dev/arubik/craftengine/script/PolyType$TypedMethodHandler7",
    "dev/arubik/craftengine/script/PolyType$TypedMethodHandler8",
    "dev/arubik/craftengine/script/PolyType$TypedMethodHandler9",
    "dev/arubik/craftengine/script/PolyType$TypedMethodHandler10",
    };

    enum Kind { DOUBLE, BOOL, STRING, RAW, UNKNOWN }

    static Kind kindOf(PolyType.TypeCodec<?> codec) {
        if (codec == TypeCodecs.DOUBLE) return Kind.DOUBLE;
        if (codec == TypeCodecs.BOOL) return Kind.BOOL;
        if (codec == TypeCodecs.STRING) return Kind.STRING;
        if (codec == TypeCodecs.RAW) return Kind.RAW;
        return Kind.UNKNOWN;
    }

    private static String jvmType(Kind k) {
        return switch (k) {
            case DOUBLE -> "D";
            case BOOL -> "Z";
            case STRING -> "L" + STRING + ";";
            case RAW -> "L" + VALUE + ";";
            case UNKNOWN -> throw new IllegalStateException("jvmType(UNKNOWN)");
        };
    }

    private static int loadOpcode(Kind k) { return k == Kind.DOUBLE ? DLOAD : k == Kind.BOOL ? ILOAD : ALOAD; }
    private static int slotWidth(Kind k) { return k == Kind.DOUBLE ? 2 : 1; }
    private static int returnOpcode(Kind k) { return k == Kind.DOUBLE ? DRETURN : k == Kind.BOOL ? IRETURN : ARETURN; }

    /** A typed method's generated shape. {@code javaName} is what to {@code INVOKEVIRTUAL} — a
     *  mangled, collision-free name, NOT the script-level name (which could clash with the
     *  wrapper's own {@code instance}/{@code refresh}/{@code of} members, or with a same-named
     *  property). */
    record TypedMemberRef(String javaName, String descriptor, Kind[] argKinds, Kind retKind) {}

    /** The generated wrapper for one {@link PolyType}. The three maps are keyed by SCRIPT-level
     *  member name and give the generated Java method to call. */
    /**
     * The generated wrapper for one {@link PolyType}. The three member maps are keyed by
     * SCRIPT-level name and give the generated Java method to call; they include INHERITED members
     * (pointing at the parent class's generated method, reachable by ordinary virtual dispatch)
     * as well as this type's own.
     *
     * <p>{@code instanceOwner} is the internal name of the class that actually DECLARES the
     * {@code instance} field — the root of the generated hierarchy, since only it declares one.
     */
    record GeneratedPolyClass(String internalName, Map<String, TypedMemberRef> typedMethods,
                               Map<String, String> untypedMethods, Map<String, String> properties,
                               java.util.Set<String> memberSet, String instanceOwner) {}

    /**
     * The wrapper for {@code typeName}, generating it on first use, or null if the type isn't
     * registered or generation failed — either way the caller must fall back to fully generic
     * dispatch, never to a silent shortcut.
     *
     * <p>EXACTLY ONE class is ever generated per type name, for the whole life of the process. It
     * is never invalidated or regenerated: a later re-registration re-points the existing wrapper's
     * handler fields (see {@link #onRegistryMutation}) rather than minting a new class, since
     * classes are never unloaded and a fresh one per mutation would leak. A member added AFTER a
     * wrapper was built simply has no generated method, so call sites for it use the ordinary
     * {@code memberCall} path — correct, just unspecialized. {@link #buildAll()} exists so that
     * normally never happens: it builds every type's wrapper once registration is complete.
     */
    static GeneratedPolyClass getOrGenerate(String typeName) {
        GeneratedPolyClass cached = CACHE.get(typeName);
        if (cached != null) return cached;
        PolyType type = PolyTypeRegistry.get(typeName);
        if (type == null) return null;
        GeneratedPolyClass generated = generate(typeName, type);
        if (generated == null) return null;
        GeneratedPolyClass existing = CACHE.putIfAbsent(typeName, generated);
        return existing != null ? existing : generated;
    }

    /** ASM needs a common-superclass oracle for frame computation, and its default implementation
     *  LOADS both classes — which would fail (or recurse) for the class currently being generated.
     *  Everything we emit is either {@code Object}-rooted or our own class, so answering {@code
     *  Object} whenever our own name is involved is both safe and sufficient. */
    private static final class SafeClassWriter extends ClassWriter {
        private final String selfInternalName;
        SafeClassWriter(int flags, String selfInternalName) {
            super(flags);
            this.selfInternalName = selfInternalName;
        }
        @Override protected String getCommonSuperClass(String a, String b) {
            if (selfInternalName.equals(a) || selfInternalName.equals(b)) return OBJECT;
            try {
                return super.getCommonSuperClass(a, b);
            } catch (Throwable t) {
                return OBJECT;
            }
        }
    }

    private static GeneratedPolyClass generate(String typeName, PolyType type) {
        try {
            // Mirror the PolyType hierarchy in the generated one: PolyClassMachine extends PolyClassBlock,
            // Machine wrapper simply INHERITS every method Block already generated instead of
            // re-emitting it. The parent must exist first, so build it (recursively) up front; if
            // that fails for any reason we fall back to a flat Object-rooted class carrying the
            // FULL inherited member set, which is what this generator did before.
            PolyType parentType = type.parent();
            GeneratedPolyClass parent = parentType != null ? getOrGenerate(parentType.name()) : null;
            String superName = parent != null ? parent.internalName() : OBJECT;

            // Named for the type it wraps, with no counter — the steady state is exactly one class
            // per PolyType, so "PolyClassMachine" is the honest name. A suffix appears ONLY on the
            // rare rebuild (buildAll seeing a type that gained members): defining the same binary
            // name twice in one loader is a LinkageError, so the uniqueness has to come from
            // somewhere, and this way it shows up only when a second class genuinely exists.
            String safeType = sanitize(typeName);
            int generation = GENERATIONS.merge(safeType, 1, Integer::sum);
            String className = "dev/arubik/craftengine/script/PolyClass" + safeType
                    + (generation == 1 ? "" : "_v" + generation);

            SafeClassWriter cw = new SafeClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, className);
            // Deliberately NOT ACC_FINAL: any type may later be declared a parent of another, and
            // that child's generated class extends this one.
            cw.visit(V21, ACC_PUBLIC | ACC_SUPER, className, null, superName, null);

            // Only the ROOT of a generated hierarchy declares the field; subclasses inherit it.
            String instanceOwner = parent != null ? parent.instanceOwner() : className;
            if (parent == null) {
                cw.visitField(ACC_PROTECTED | ACC_FINAL, "instance", "Ljava/lang/Object;", null, null).visitEnd();
            }

            MethodVisitor refresh = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "refresh", "()V", null, null);
            refresh.visitCode();

            // Start from the parent's members so inherited ones stay callable, then let this type's
            // OWN registrations override them.
            Map<String, TypedMemberRef> typedRefs = new LinkedHashMap<>();
            Map<String, String> untypedRefs = new LinkedHashMap<>();
            Map<String, String> propRefs = new LinkedHashMap<>();
            if (parent != null) {
                typedRefs.putAll(parent.typedMethods());
                untypedRefs.putAll(parent.untypedMethods());
                propRefs.putAll(parent.properties());
            }
            int[] counter = {0};

            // With a parent, emit only what this type declares itself; everything else is inherited.
            java.util.Collection<String> methodsToEmit = parent != null ? type.ownMethodNames() : type.allMethodNames();
            java.util.Collection<String> propsToEmit = parent != null ? type.ownPropertyNames() : type.allPropertyNames();

            for (String methodName : new ArrayList<>(methodsToEmit)) {
                int idx = counter[0]++;
                Kind[] argKinds = typedArgKinds(type, methodName);
                Kind retKind = argKinds != null ? kindOf(type.resolveTypedMethod(methodName).returnType()) : Kind.UNKNOWN;
                if (argKinds != null && retKind != Kind.UNKNOWN) {
                    String field = "h$" + idx;
                    String iface = TYPED_HANDLER_IFACE[argKinds.length];
                    String sig = signatureOf(argKinds, retKind);
                    cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_VOLATILE, field, "L" + iface + ";", null, null).visitEnd();
                    refresh.visitLdcInsn(typeName);
                    refresh.visitLdcInsn(methodName);
                    refresh.visitLdcInsn(sig);
                    refresh.visitMethodInsn(INVOKESTATIC, RUNTIME, "resolveTypedHandler",
                            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;", false);
                    refresh.visitTypeInsn(CHECKCAST, iface);
                    refresh.visitFieldInsn(PUTSTATIC, className, field, "L" + iface + ";");

                    String javaName = "tm$" + idx + "_" + sanitize(methodName);
                    String desc = emitTypedMethod(cw, className, instanceOwner, javaName, typeName, methodName,
                            field, iface, argKinds, retKind);
                    typedRefs.put(methodName, new TypedMemberRef(javaName, desc, argKinds, retKind));

                    // ALSO emit the erased shim for a typed method. The native signature has a fixed
                    // arity, but a call site may legitimately pass fewer arguments — every
                    // methodTypedOptN registration is exactly that (all args optional with
                    // defaults), and methodTypedN's onMissingArgs case likewise. Without this the
                    // short-arg call site has nothing to dispatch to and falls all the way back to
                    // generic memberCall. The shim calls the same MethodHandler that the typed
                    // registration installed, so arity handling stays in one place.
                    int uidx = counter[0]++;
                    String ufield = "m$" + uidx;
                    cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_VOLATILE, ufield, "L" + METHOD_HANDLER + ";", null, null).visitEnd();
                    refresh.visitLdcInsn(typeName);
                    refresh.visitLdcInsn(methodName);
                    refresh.visitMethodInsn(INVOKESTATIC, RUNTIME, "resolveMethodHandler",
                            "(Ljava/lang/String;Ljava/lang/String;)L" + METHOD_HANDLER + ";", false);
                    refresh.visitFieldInsn(PUTSTATIC, className, ufield, "L" + METHOD_HANDLER + ";");
                    String ujavaName = "um$" + uidx + "_" + sanitize(methodName);
                    emitUntypedMethod(cw, className, instanceOwner, ujavaName, typeName, methodName, ufield);
                    untypedRefs.put(methodName, ujavaName);
                } else {
                    String field = "m$" + idx;
                    cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_VOLATILE, field, "L" + METHOD_HANDLER + ";", null, null).visitEnd();
                    refresh.visitLdcInsn(typeName);
                    refresh.visitLdcInsn(methodName);
                    refresh.visitMethodInsn(INVOKESTATIC, RUNTIME, "resolveMethodHandler",
                            "(Ljava/lang/String;Ljava/lang/String;)L" + METHOD_HANDLER + ";", false);
                    refresh.visitFieldInsn(PUTSTATIC, className, field, "L" + METHOD_HANDLER + ";");

                    String javaName = "um$" + idx + "_" + sanitize(methodName);
                    emitUntypedMethod(cw, className, instanceOwner, javaName, typeName, methodName, field);
                    untypedRefs.put(methodName, javaName);
                    typedRefs.remove(methodName); // see the typed branch — shape override
                }
            }

            for (String propName : new ArrayList<>(propsToEmit)) {
                int idx = counter[0]++;
                String field = "p$" + idx;
                cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_VOLATILE, field, "L" + PROPERTY_HANDLER + ";", null, null).visitEnd();
                refresh.visitLdcInsn(typeName);
                refresh.visitLdcInsn(propName);
                refresh.visitMethodInsn(INVOKESTATIC, RUNTIME, "resolvePropertyHandler",
                        "(Ljava/lang/String;Ljava/lang/String;)L" + PROPERTY_HANDLER + ";", false);
                refresh.visitFieldInsn(PUTSTATIC, className, field, "L" + PROPERTY_HANDLER + ";");

                String javaName = "pg$" + idx + "_" + sanitize(propName);
                emitPropertyMethod(cw, className, instanceOwner, javaName, typeName, propName, field);
                propRefs.put(propName, javaName);
            }

            refresh.visitInsn(RETURN);
            refresh.visitMaxs(0, 0);
            refresh.visitEnd();

            MethodVisitor ctor = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
            ctor.visitCode();
            ctor.visitVarInsn(ALOAD, 0);
            if (parent != null) {
                // The root already stores `instance`; just chain to it.
                ctor.visitVarInsn(ALOAD, 1);
                ctor.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "(Ljava/lang/Object;)V", false);
            } else {
                ctor.visitMethodInsn(INVOKESPECIAL, OBJECT, "<init>", "()V", false);
                ctor.visitVarInsn(ALOAD, 0);
                ctor.visitVarInsn(ALOAD, 1);
                ctor.visitFieldInsn(PUTFIELD, className, "instance", "Ljava/lang/Object;");
            }
            ctor.visitInsn(RETURN);
            ctor.visitMaxs(0, 0);
            ctor.visitEnd();

            MethodVisitor factory = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "of",
                    "(Ljava/lang/Object;)L" + className + ";", null, null);
            factory.visitCode();
            factory.visitTypeInsn(NEW, className);
            factory.visitInsn(DUP);
            factory.visitVarInsn(ALOAD, 0);
            factory.visitMethodInsn(INVOKESPECIAL, className, "<init>", "(Ljava/lang/Object;)V", false);
            factory.visitInsn(ARETURN);
            factory.visitMaxs(0, 0);
            factory.visitEnd();

            cw.visitEnd();
            byte[] bytes = cw.toByteArray();
            dumpIfRequested(className, bytes);
            Class<?> defined = MethodHandles.lookup().defineClass(bytes);

            // Register for future refreshes BEFORE the initial resolve, not after. A mutation landing
            // between the two would otherwise be missed by this class — it isn't in the list yet, so
            // onRegistryMutation skips it, and it keeps the handlers this resolve captured until some
            // unrelated later mutation happens to clear them. Refreshing early is harmless: refresh()
            // only assigns static fields and is idempotent, so a concurrent one just does the same
            // work twice.
            MethodHandle refresher = MethodHandles.lookup()
                    .findStatic(defined, "refresh", MethodType.methodType(void.class));
            LIVE_REFRESHERS.add(refresher);
            refresher.invokeExact();

            LOG.log(Level.FINE, () -> "[CEPolyfills] [JIT] generated PolyClass " + className + " for " + typeName
                    + " (" + typedRefs.size() + " typed, " + untypedRefs.size() + " untyped, "
                    + propRefs.size() + " properties)");
            return new GeneratedPolyClass(className, Map.copyOf(typedRefs), Map.copyOf(untypedRefs),
                    Map.copyOf(propRefs), memberSetOf(type), instanceOwner);
        } catch (Throwable t) {
            LOG.log(Level.WARNING, t, () -> "[CEPolyfills] [JIT] failed to generate PolyClass for " + typeName
                    + " — falling back to generic dispatch");
            return null;
        }
    }

    /** The arg kinds of {@code method}'s typed registration, or null when it has none, its arity
     *  exceeds the 7 the {@code TypedMethodHandlerN} family covers, or any codec isn't one of the
     *  four known {@link TypeCodecs} singletons. */
    private static Kind[] typedArgKinds(PolyType type, String method) {
        PolyType.TypedMethodDescriptor d = type.resolveTypedMethod(method);
        if (d == null || d.arity() > 10) return null;
        Kind[] kinds = new Kind[d.arity()];
        for (int i = 0; i < d.arity(); i++) {
            kinds[i] = kindOf(d.argTypes().get(i));
            if (kinds[i] == Kind.UNKNOWN) return null;
        }
        return kindOf(d.returnType()) == Kind.UNKNOWN ? null : kinds;
    }

    private static String signatureOf(Kind[] argKinds, Kind retKind) {
        StringBuilder sb = new StringBuilder();
        for (Kind k : argKinds) sb.append(PolyClassRuntime.kindChar(k));
        return sb.append(':').append(PolyClassRuntime.kindChar(retKind)).toString();
    }

    private static String sanitize(String raw) {
        String s = raw.replaceAll("[^A-Za-z0-9_]", "_");
        return s.isEmpty() ? "_" : s;
    }

    /** A genuinely native-signatured method: {@code <nativeRet> name(<nativeArgs>)}, calling the
     *  cached typed handler directly. If that handler is null (member gone, or its registered shape
     *  no longer matches what this method was generated for — see the class doc), boxes its args
     *  back up and routes through {@link PolyClassRuntime#genericCall}. */
    private static String emitTypedMethod(ClassWriter cw, String className, String instanceOwner, String javaName,
                                           String typeName, String scriptName, String field, String iface,
                                           Kind[] argKinds, Kind retKind) {
        StringBuilder desc = new StringBuilder("(");
        for (Kind k : argKinds) desc.append(jvmType(k));
        desc.append(")").append(jvmType(retKind));

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, javaName, desc.toString(), null, null);
        mv.visitCode();
        Label slow = new Label();
        mv.visitFieldInsn(GETSTATIC, className, field, "L" + iface + ";");
        mv.visitJumpInsn(IFNULL, slow);

        mv.visitFieldInsn(GETSTATIC, className, field, "L" + iface + ";");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, instanceOwner, "instance", "Ljava/lang/Object;");
        int slot = 1;
        for (Kind k : argKinds) {
            mv.visitVarInsn(loadOpcode(k), slot);
            switch (k) {
                case DOUBLE -> mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                case BOOL -> mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                default -> { /* String/ScriptValue are already references */ }
            }
            slot += slotWidth(k);
        }
        mv.visitMethodInsn(INVOKEINTERFACE, iface, "call",
                "(" + "Ljava/lang/Object;".repeat(argKinds.length + 1) + ")Ljava/lang/Object;", true);
        switch (retKind) {
            case DOUBLE -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            }
            case BOOL -> {
                mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            }
            case STRING -> mv.visitTypeInsn(CHECKCAST, STRING);
            case RAW -> mv.visitTypeInsn(CHECKCAST, VALUE);
            case UNKNOWN -> throw new IllegalStateException("emitTypedMethod(UNKNOWN ret)");
        }
        mv.visitInsn(returnOpcode(retKind));

        mv.visitLabel(slow);
        mv.visitLdcInsn(typeName);
        mv.visitLdcInsn(scriptName);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, instanceOwner, "instance", "Ljava/lang/Object;");
        pushInt(mv, argKinds.length);
        mv.visitTypeInsn(ANEWARRAY, VALUE);
        slot = 1;
        for (int i = 0; i < argKinds.length; i++) {
            Kind k = argKinds[i];
            mv.visitInsn(DUP);
            pushInt(mv, i);
            mv.visitVarInsn(loadOpcode(k), slot);
            switch (k) {
                case DOUBLE -> mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(D)L" + VALUE + ";", true);
                case BOOL -> mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Z)L" + VALUE + ";", true);
                case STRING -> mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Ljava/lang/String;)L" + VALUE + ";", true);
                case RAW -> { /* already a ScriptValue */ }
                case UNKNOWN -> throw new IllegalStateException("emitTypedMethod(UNKNOWN arg)");
            }
            mv.visitInsn(AASTORE);
            slot += slotWidth(k);
        }
        mv.visitMethodInsn(INVOKESTATIC, RUNTIME, "genericCall",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;[L" + VALUE + ";)L" + VALUE + ";", false);
        switch (retKind) {
            case DOUBLE -> mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asNum", "()D", true);
            case BOOL -> mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asBool", "()Z", true);
            case STRING -> mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asStr", "()Ljava/lang/String;", true);
            case RAW -> { /* genericCall already returns a ScriptValue */ }
            case UNKNOWN -> throw new IllegalStateException("emitTypedMethod(UNKNOWN ret)");
        }
        mv.visitInsn(returnOpcode(retKind));
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        return desc.toString();
    }

    /** {@code ScriptValue name(List args)} — the erased {@code MethodHandler} shape, for any method
     *  without an eligible typed registration. Still worth generating: the registry lookup this call
     *  would otherwise redo every time is gone. */
    private static void emitUntypedMethod(ClassWriter cw, String className, String instanceOwner, String javaName,
                                           String typeName, String scriptName, String field) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, javaName, "(L" + LIST + ";)L" + VALUE + ";", null, null);
        mv.visitCode();
        Label slow = new Label();
        mv.visitFieldInsn(GETSTATIC, className, field, "L" + METHOD_HANDLER + ";");
        mv.visitJumpInsn(IFNULL, slow);
        mv.visitFieldInsn(GETSTATIC, className, field, "L" + METHOD_HANDLER + ";");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, instanceOwner, "instance", "Ljava/lang/Object;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, METHOD_HANDLER, "call",
                "(Ljava/lang/Object;L" + LIST + ";)L" + VALUE + ";", true);
        mv.visitInsn(ARETURN);
        mv.visitLabel(slow);
        mv.visitLdcInsn(typeName);
        mv.visitLdcInsn(scriptName);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, instanceOwner, "instance", "Ljava/lang/Object;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, RUNTIME, "genericCallList",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;L" + LIST + ";)L" + VALUE + ";", false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    /** {@code ScriptValue name()} — a property read against the cached {@code PropertyHandler}. */
    private static void emitPropertyMethod(ClassWriter cw, String className, String instanceOwner, String javaName,
                                            String typeName, String scriptName, String field) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, javaName, "()L" + VALUE + ";", null, null);
        mv.visitCode();
        Label slow = new Label();
        mv.visitFieldInsn(GETSTATIC, className, field, "L" + PROPERTY_HANDLER + ";");
        mv.visitJumpInsn(IFNULL, slow);
        mv.visitFieldInsn(GETSTATIC, className, field, "L" + PROPERTY_HANDLER + ";");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, instanceOwner, "instance", "Ljava/lang/Object;");
        mv.visitMethodInsn(INVOKEINTERFACE, PROPERTY_HANDLER, "get", "(Ljava/lang/Object;)L" + VALUE + ";", true);
        mv.visitInsn(ARETURN);
        mv.visitLabel(slow);
        mv.visitLdcInsn(typeName);
        mv.visitLdcInsn(scriptName);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, instanceOwner, "instance", "Ljava/lang/Object;");
        mv.visitMethodInsn(INVOKESTATIC, RUNTIME, "genericProperty",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)L" + VALUE + ";", false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    /** Debug aid: with {@code -Dcraftengine.polyclass.dump=<dir>}, writes every generated class to
     *  that directory so it can be disassembled/decompiled and read as ordinary Java. Off (and
     *  free) unless the property is set — this is for inspecting what the JIT built, not a
     *  production path. */
    private static void dumpIfRequested(String internalName, byte[] bytes) {
        String dir = System.getProperty("craftengine.polyclass.dump");
        if (dir == null || dir.isBlank()) return;
        try {
            java.nio.file.Path out = java.nio.file.Path.of(dir);
            java.nio.file.Files.createDirectories(out);
            String simple = internalName.substring(internalName.lastIndexOf('/') + 1);
            java.nio.file.Files.write(out.resolve(simple + ".class"), bytes);
        } catch (Exception e) {
            LOG.log(Level.WARNING, e, () -> "[CEPolyfills] [JIT] could not dump " + internalName);
        }
    }

    private static void pushInt(MethodVisitor mv, int i) {
        switch (i) {
            case 0 -> mv.visitInsn(ICONST_0);
            case 1 -> mv.visitInsn(ICONST_1);
            case 2 -> mv.visitInsn(ICONST_2);
            case 3 -> mv.visitInsn(ICONST_3);
            case 4 -> mv.visitInsn(ICONST_4);
            case 5 -> mv.visitInsn(ICONST_5);
            default -> mv.visitIntInsn(BIPUSH, i);
        }
    }
}
