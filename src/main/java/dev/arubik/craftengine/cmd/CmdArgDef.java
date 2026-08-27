package dev.arubik.craftengine.cmd;

import java.util.List;

/**
 * One argument (or literal keyword) in a {@link CmdDefinition}'s Brigadier chain, in declaration
 * order. {@code type == "literal"} isn't a REAL Brigadier argument (no value is captured, no name
 * shows in {@code Cmd.arg(...)}) — it's a fixed keyword the player must type verbatim, the
 * mechanism {@link CmdDefinition#subcommands()} itself is built from at a higher level; exposed
 * here too so a single command can also branch mid-argument-chain (e.g. {@code /warp set <name>}
 * vs {@code /warp tp <name>} sharing everything up to the branch).
 *
 * <p>{@code choices} and {@code suggest} both only affect TAB-COMPLETE, never validation — the
 * underlying Brigadier type ({@code type}, typically {@code "string"}) still decides what's
 * actually accepted. {@code choices} is a fixed list authored right in the JSON (a data-driven
 * enum: {@code "type": "string", "choices": ["easy","normal","hard"]}); {@code suggest} instead
 * names a {@code "file.pf:function"} ref called fresh on every keystroke, with every EARLIER
 * argument in the same command already resolved and readable via {@code Cmd.arg(...)} — the
 * function returns an array of strings and that becomes the suggestion list, which is what makes
 * something like "pick an auction lot id, but only ones the command's earlier
 * category/player argument actually has for sale" possible without any Java.
 */
public record CmdArgDef(String name, String type, String literal, List<String> choices, String suggest) {

    public boolean isLiteral() {
        return "literal".equals(type);
    }
}
