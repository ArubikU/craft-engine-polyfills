package dev.arubik.craftengine.machine.render.formula;

import java.util.List;

/**
 * A named object that can be addressed in expressions as {@code ClassName.property}
 * or {@code ClassName.method(args)}.
 *
 * <p>Implement this interface to expose domain objects (e.g. the machine's
 * {@code Inventory}) to {@link PolyFormula} at evaluation time.  Implementations
 * are registered on a {@link PolyContext} under a name and looked up by the
 * parser via {@link PolyContext#getClass(String)}.</p>
 *
 * <p>Unrecognised properties / methods should return {@link PolyValue#NULL}
 * rather than throwing.</p>
 */
public interface PolyClass {

    /**
     * Read a named property (no arguments), e.g. {@code Inventory.size}.
     *
     * @param property property name
     * @return resolved value; never {@code null} — return {@link PolyValue#NULL} instead
     */
    PolyValue get(String property);

    /**
     * Invoke a named method with the given evaluated arguments,
     * e.g. {@code Inventory.slot(9)}.
     *
     * @param method method name
     * @param args   already-evaluated argument list (may be empty)
     * @return result; never {@code null} — return {@link PolyValue#NULL} instead
     */
    PolyValue call(String method, List<PolyValue> args);
}
