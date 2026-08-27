package dev.arubik.craftengine.cmd;

import dev.arubik.craftengine.machine.MachineDefinition;

/**
 * One declarative GUI screen for a {@code /cmds} command — the SAME {@code "pages"} idea a
 * machine's JSON already uses (see {@code src/main/resources/machines/teleporter.json}'s
 * {@code "pages"} array: title/size/layout/buttons, static or generator-driven), just built into
 * a standalone {@code dev.arubik.craftengine.menu.ScriptMenu} instead of a block-backed {@code
 * MachineMenu} — so a command like {@code /warps} can declare its ENTIRE UI (main screen, manage
 * screen, settings screen, ...) in JSON exactly like a machine does, instead of every button
 * needing hand-written {@code Menu.create()...set_item(...)} script code.
 *
 * <p>Wraps a REAL {@link MachineDefinition.PageDef} — parsed by the exact same {@code
 * MachineDefinitionLoader#parsePage} every machine page already goes through, so JSON written for
 * one system transfers directly to the other (same {@code layout}/{@code buttons}/generator-string
 * convention). The machine-only fields on {@code PageDef} (input/output/fuel/storage slots, bars,
 * ghost slots, upgrades) are simply never read for a Cmd page.
 *
 * <p>{@code name} is how a button elsewhere in the SAME command's page set navigates here via the
 * built-in {@code "page:<name>"} action (see {@code CmdRegistry} — this is Java-side navigation,
 * no script call happens, so it works even for a page with no scripts at all). The FIRST page in
 * a command's {@code pages} list is the one opened when the command itself (or the subcommand
 * declaring {@code pages}) is invoked.
 */
public record CmdPageDef(String name, MachineDefinition.PageDef page) {}
