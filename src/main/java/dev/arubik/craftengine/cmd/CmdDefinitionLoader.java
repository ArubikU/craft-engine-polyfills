package dev.arubik.craftengine.cmd;

import java.util.ArrayList;
import java.util.List;

import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.machine.MachineDefinitionLoader;

/**
 * Loads every {@code cmds/*.json} into {@link CmdRegistry}. Mirrors {@code
 * MachineDefinitionLoader}'s split (bootstrap seeds bundled examples, load parses) except commands
 * have no cross-registry ordering dependency, so both happen in one pass here.
 */
public final class CmdDefinitionLoader {

    private CmdDefinitionLoader() {}

    public static void load() {
        List<CmdDefinition> defs = new ArrayList<>();
        DataFiles.loadDirectory("cmds", (view, name) -> defs.add(parseCommand(view)));
        CmdRegistry.setDefinitions(defs);
    }

    /** Re-parses exactly ONE {@code cmds/<name>.json} and replaces (or, for a brand-new file,
     *  adds — though a NEW top-level command still needs a restart before Brigadier registers it,
     *  same caveat as {@code load()}) that single entry in {@link CmdRegistry}'s definitions,
     *  leaving every other loaded command untouched — for a targeted {@code /cep reload cmds
     *  <name>} instead of re-parsing every command file just to pick up one edit. Returns false on
     *  a missing/malformed file (see {@code DataFiles#loadSingleFile}). */
    public static boolean loadOne(String name) {
        String fileName = name.endsWith(".json") ? name : name + ".json";
        CmdDefinition[] holder = new CmdDefinition[1];
        boolean ok = DataFiles.loadSingleFile("cmds", fileName, (view, n) -> holder[0] = parseCommand(view));
        if (!ok || holder[0] == null) return false;
        CmdRegistry.replaceOne(holder[0]);
        return true;
    }

    private static CmdDefinition parseCommand(JsonView view) {
        String name = view.string("name");
        List<String> aliases = view.has("aliases") ? view.stringList("aliases") : List.of();
        String permission = view.string("permission", null);
        String description = view.string("description", "");
        List<CmdArgDef> args = parseArgs(view);
        String execute = view.string("execute", null);
        String gui = view.string("gui", null);
        List<CmdPageDef> pages = parsePages(view);
        List<CmdDefinition> subcommands = new ArrayList<>();
        if (view.has("subcommands")) {
            for (JsonView sub : view.objectList("subcommands")) {
                subcommands.add(parseCommand(sub));
            }
        }
        if (execute == null && gui == null && pages.isEmpty() && subcommands.isEmpty()) {
            throw view.error("command '" + name + "' needs at least one of \"execute\", \"gui\", \"pages\", or \"subcommands\"");
        }
        return new CmdDefinition(name, aliases, permission, description, args, execute, gui, pages, subcommands);
    }

    /** {@code "pages": [...]} — same shape/parser as a machine's own {@code "pages"} array (see
     *  {@link CmdPageDef}'s javadoc for why reusing {@code MachineDefinitionLoader.parsePage}
     *  directly is safe here: the machine-only fields it also parses are simply unused). Each
     *  page object additionally needs a {@code "name"} for {@code "page:<name>"} navigation. */
    private static List<CmdPageDef> parsePages(JsonView view) {
        List<CmdPageDef> out = new ArrayList<>();
        if (!view.has("pages")) return out;
        for (JsonView p : view.objectList("pages")) {
            String pageName = p.string("name", "page" + out.size());
            out.add(new CmdPageDef(pageName, MachineDefinitionLoader.parsePage(p.raw(), view)));
        }
        return out;
    }

    private static List<CmdArgDef> parseArgs(JsonView view) {
        List<CmdArgDef> out = new ArrayList<>();
        if (!view.has("args")) return out;
        for (JsonView a : view.objectList("args")) {
            String type = a.string("type", "string");
            if ("literal".equals(type)) {
                String literal = a.string("value");
                out.add(new CmdArgDef(literal, "literal", literal, null, null));
            } else {
                List<String> choices = a.has("choices") ? a.stringList("choices") : null;
                String suggest = a.string("suggest", null);
                out.add(new CmdArgDef(a.string("name"), type, null, choices, suggest));
            }
        }
        return out;
    }
}
