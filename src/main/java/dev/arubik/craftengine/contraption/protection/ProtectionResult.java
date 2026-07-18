package dev.arubik.craftengine.contraption.protection;

/**
 * Tri-state vote returned by every {@link ContraptionProtection} provider for a single
 * permission query (see {@code .migration/ROADMAP-claims-and-phys.md} §1 "SPI vs
 * self-contained"). The {@link ContraptionProtectionRegistry} resolves an ordered chain of
 * providers with <b>deny-wins</b> semantics:
 * <ul>
 *   <li>{@link #DENY} — this provider explicitly forbids the action. A single DENY anywhere in
 *   the chain short-circuits to "not allowed", regardless of any other provider's vote.</li>
 *   <li>{@link #ALLOW} — this provider explicitly permits the action, but does NOT override a
 *   later provider's DENY (deny-wins). Distinguished from {@link #PASS} only for provider
 *   authoring clarity; the registry treats a chain that ends with no DENY as allowed either
 *   way.</li>
 *   <li>{@link #PASS} — this provider has no opinion (e.g. the position isn't inside any claim
 *   it manages). The chain falls through to the next provider, and if <em>every</em> provider
 *   passes the registry falls back to its configurable default (allow, for backward
 *   compatibility — see {@link ContraptionProtectionRegistry}).</li>
 * </ul>
 */
public enum ProtectionResult {
    /** Explicitly permitted by this provider (does not override a later DENY — deny-wins). */
    ALLOW,
    /** Explicitly forbidden — short-circuits the whole chain to "not allowed". */
    DENY,
    /** No opinion; defer to the next provider (all-PASS falls through to the registry default). */
    PASS
}
