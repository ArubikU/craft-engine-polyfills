package dev.arubik.craftengine.cmd;

import java.util.List;

/**
 * One data-driven command loaded from {@code plugins/CraftEnginePolyfill/cmds/*.json} — the whole
 * point being a server owner authors {@code /tpa}, {@code /warp}, an auction house command, etc.
 * entirely in JSON + {@code .pf} scripts, without a single line of Java.
 *
 * <p>{@code args} is one linear Brigadier chain (typed arguments in order); {@code subcommands}
 * are named branches that each get their OWN args/execute/subcommands recursively, e.g.
 * {@code /tpa <player>} (root {@code args} + {@code execute}) alongside {@code /tpa accept} and
 * {@code /tpa deny} (each a {@code CmdDefinition} in {@code subcommands} with its own literal
 * {@code name} and no further args). A node needs at least one of {@code execute} (only reachable
 * once every arg above it is filled), {@code gui}, {@code pages}, or non-empty {@code
 * subcommands}. A node may also declare {@code gui} — a {@code "file.pf:function"} ref called
 * with the SAME context as {@code execute} (after it, if both are present) whose job is to build
 * and {@code .open(...)} a {@code Menu} right there.
 *
 * <p>{@code pages} is the DECLARATIVE alternative to {@code gui} — see {@link CmdPageDef}'s
 * javadoc: a list of full GUI screens (title/size/layout/buttons, same shape a machine's own
 * {@code "pages"} JSON already uses), built directly by {@code CmdRegistry} with no script code
 * needed for the layout itself. When present, invoking this node opens {@code pages.get(0)}.
 */
public record CmdDefinition(
        String name,
        List<String> aliases,
        String permission,
        String description,
        List<CmdArgDef> args,
        String execute,
        String gui,
        List<CmdPageDef> pages,
        List<CmdDefinition> subcommands
) {}
