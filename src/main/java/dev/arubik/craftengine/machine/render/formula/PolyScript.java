package dev.arubik.craftengine.machine.render.formula;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A loaded {@code .pf} script file containing named {@link PolyFormula} expressions
 * and optional control-flow blocks.
 *
 * <h3>File format</h3>
 * <pre>{@code
 * # crusher.pf — lines starting with # are comments (anywhere on their own line)
 *
 * # Simple assignment
 * speed   = rpm * (1 + overclock * 0.5)
 * running = processing && rpm > 0
 *
 * # If / else if / else.  Opening brace belongs on the same line as the condition.
 * # Whitespace around keywords and braces is flexible.
 * if running && overclock > 2 {
 *     smoke_count = 8
 *     glow = true
 * } else if running {
 *     smoke_count = 3
 *     glow = false
 * } else {
 *     smoke_count = 0
 *     glow = false
 * }
 * }</pre>
 *
 * <h3>Notes</h3>
 * <ul>
 *   <li>Earlier assignments are visible to later ones (scripts execute sequentially).</li>
 *   <li>Expressions in assignments span exactly one logical line.</li>
 *   <li>If-condition expressions span from after {@code if}/{@code else if} to the
 *       opening {@code \{}.</li>
 *   <li>Errors during individual expression evaluations are silently skipped.</li>
 * </ul>
 */
public final class PolyScript {

    private final String name;
    /** Flat map of top-level assignments — for simple {@link #get} lookups. */
    private final Map<String, PolyFormula> entries;
    /** Full compiled statement list including if/else chains. */
    private final List<Statement> statements;

    // ---- Statement model -----------------------------------------------------

    private sealed interface Statement {

        /** Simple {@code name = expr} assignment. */
        record Assign(String name, PolyFormula formula) implements Statement {}

        /**
         * An {@code if / else if / else} chain.
         * The last clause may have a {@code null} condition (the {@code else} branch).
         */
        record IfChain(List<Clause> clauses) implements Statement {}

        record Clause(PolyFormula condition, List<Statement> body) {
            boolean isElse() { return condition == null; }
        }

        /**
         * {@code for var in iterExpr { body }} — iterate an Array, binding {@code var}
         * to each element.  The iteration expression is evaluated fresh at the start of
         * the loop; the body runs once per element with {@code var} injected into the
         * context builder.  Variables written by the body are visible to later iterations
         * and to statements that follow the loop.
         */
        record ForStatement(String var, String iterExpr, List<Statement> body) implements Statement {}

        /**
         * {@code while condExpr { body }} — repeat the body while the condition is truthy.
         * {@code maxIter} is a safety limit (default 1000) to prevent infinite loops.
         */
        record WhileStatement(String condExpr, List<Statement> body, int maxIter) implements Statement {}
    }

    // ---- Construction --------------------------------------------------------

    private PolyScript(String name,
                       Map<String, PolyFormula> entries,
                       List<Statement> statements) {
        this.name       = name;
        this.entries    = entries;
        this.statements = statements;
    }

    /** The script name (file name without the {@code .pf} extension). */
    public String name() { return name; }

    /**
     * Flat map of <em>top-level</em> {@code name = expr} assignments.
     * Assignments that live inside {@code if/else} blocks are NOT included.
     * Use {@link #evaluate(PolyContext)} to run the full script.
     */
    public Map<String, PolyFormula> entries() { return entries; }

    // ---- Loading -------------------------------------------------------------

    /**
     * Load a {@code .pf} file.  Bad expressions are logged and skipped; IO errors
     * produce an empty (no-op) script.
     */
    public static PolyScript load(File file) {
        Logger log = org.bukkit.Bukkit.getLogger();
        String scriptName = file.getName().replaceAll("\\.pf$", "");
        String src;
        try {
            src = new String(Files.readAllBytes(file.toPath()));
        } catch (Throwable ex) {
            log.warning("[CEPolyfills] Failed to load script " + file + ": " + ex.getMessage());
            return new PolyScript(scriptName, Map.of(), List.of());
        }

        Tokenizer tok = new Tokenizer(src);
        Parser    par = new Parser(scriptName, tok, log);
        List<Statement> stmts = par.parseBlock(false);

        Map<String, PolyFormula> entries = new LinkedHashMap<>();
        for (Statement s : stmts)
            if (s instanceof Statement.Assign a) entries.put(a.name(), a.formula());

        return new PolyScript(scriptName, Collections.unmodifiableMap(entries), stmts);
    }

    // ---- Evaluation ----------------------------------------------------------

    /**
     * Evaluate all statements against {@code ctx} and return an augmented context.
     * Earlier assignments are visible to later ones; if/else chains run the first
     * matching branch.  Evaluation errors on individual expressions are silently
     * skipped (the variable retains its previous value or stays absent).
     */
    public PolyContext evaluate(PolyContext ctx) {
        PolyContext.Builder b = PolyContext.builder().copyFrom(ctx);
        runStatements(statements, b);
        return b.build();
    }

    /** Return a specific top-level named formula, or {@code null} if not defined. */
    public PolyFormula get(String name) { return entries.get(name); }

    // ---- Execution helpers ---------------------------------------------------

    private static void runStatements(List<Statement> stmts, PolyContext.Builder b) {
        for (Statement stmt : stmts) {
            switch (stmt) {
                case Statement.Assign a -> {
                    PolyContext snap = b.build();
                    try { b.val(a.name(), a.formula().evaluate(snap)); }
                    catch (Throwable ignored) {}
                }
                case Statement.IfChain chain -> {
                    PolyContext snap = b.build();
                    for (Statement.Clause clause : chain.clauses()) {
                        boolean taken;
                        if (clause.isElse()) {
                            taken = true;
                        } else {
                            try { taken = clause.condition().evaluateBool(snap); }
                            catch (Throwable ignored) { taken = false; }
                        }
                        if (taken) { runStatements(clause.body(), b); break; }
                    }
                }
                case Statement.ForStatement fs -> {
                    PolyContext snap = b.build();
                    try {
                        PolyValue arr = PolyFormula.compile(fs.iterExpr()).evaluate(snap);
                        java.util.List<PolyValue> elems = null;
                        if (arr instanceof PolyValue.Array a) {
                            elems = a.elements();
                        } else if (!(arr instanceof PolyValue.Null)) {
                            // Single value — iterate once
                            elems = java.util.List.of(arr);
                        }
                        if (elems != null) {
                            for (PolyValue elem : elems) {
                                b.val(fs.var(), elem);
                                runStatements(fs.body(), b);
                            }
                        }
                    } catch (Throwable ignored) {}
                }
                case Statement.WhileStatement ws -> {
                    int iters = 0;
                    while (iters++ < ws.maxIter()) {
                        PolyContext snap = b.build();
                        boolean cond;
                        try { cond = PolyFormula.compile(ws.condExpr()).evaluateBool(snap); }
                        catch (Throwable ignored) { break; }
                        if (!cond) break;
                        runStatements(ws.body(), b);
                    }
                }
            }
        }
    }

    // =========================================================================
    // Tokenizer
    // =========================================================================

    /**
     * Converts the raw {@code .pf} source text into a flat stream of {@link Token}s.
     *
     * <p>Token types:</p>
     * <ul>
     *   <li>{@code IDENT}  — an ASCII identifier (may be a keyword like {@code if}/{@code else})</li>
     *   <li>{@code ASSIGN} — {@code =} (only when NOT preceded by {@code !/</>} etc.)</li>
     *   <li>{@code LBRACE} — {@code \{}</li>
     *   <li>{@code RBRACE} — {@code }}</li>
     *   <li>{@code RAW}    — raw expression text (grabbed by the parser on demand)</li>
     *   <li>{@code EOL}    — end of logical line (newline or semicolon)</li>
     *   <li>{@code EOF}    — end of input</li>
     * </ul>
     *
     * <p>Comments ({@code #} to EOL) produce an {@code EOL} token so the parser
     * can detect statement boundaries.</p>
     */
    private static final class Tokenizer {

        enum TT { IDENT, ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, STAR_ASSIGN, PLUSPLUS, MINUSMINUS, COLON, LBRACE, RBRACE, EOL, EOF }

        record Token(TT type, String text, int pos) {}

        private final String src;
        private int pos;

        Tokenizer(String src) {
            this.src = src;
            this.pos = 0;
        }

        // ---- Low-level character ops -----------------------------------------

        private boolean atEnd() { return pos >= src.length(); }
        private char ch()       { return src.charAt(pos); }

        /** Skip spaces and tabs (not newlines). */
        private void skipHSpace() {
            while (!atEnd() && (ch() == ' ' || ch() == '\t')) pos++;
        }

        /** Skip all whitespace including newlines. */
        private void skipWS() {
            while (!atEnd() && Character.isWhitespace(ch())) pos++;
        }

        /**
         * Skip a comment if the current position starts with {@code #}
         * (after horizontal whitespace has been skipped).  A comment runs to the
         * end of the physical line.
         *
         * @return {@code true} if a comment was consumed.
         */
        private boolean skipComment() {
            if (atEnd() || ch() != '#') return false;
            while (!atEnd() && ch() != '\n') pos++;
            return true;
        }

        // ---- Structured token scan -------------------------------------------

        /**
         * Return the next meaningful token, advancing the position past it.
         *
         * <p>Skips horizontal whitespace before each scan.  A {@code #} comment
         * terminates the current logical line and yields an {@code EOL} token.
         * Physical newlines also yield {@code EOL}.</p>
         */
        Token next() {
            while (true) {
                skipHSpace();
                if (atEnd()) return new Token(TT.EOF, "", pos);

                char c = ch();

                // Physical end-of-line
                if (c == '\n' || c == '\r') {
                    while (!atEnd() && (ch() == '\n' || ch() == '\r')) pos++;
                    return new Token(TT.EOL, "\n", pos);
                }

                // Comment → acts as EOL
                if (c == '#') {
                    int start = pos;
                    while (!atEnd() && ch() != '\n') pos++;
                    return new Token(TT.EOL, "#", start);
                }

                // Semicolons as statement separator (optional convenience)
                if (c == ';') { pos++; return new Token(TT.EOL, ";", pos - 1); }

                if (c == '{') { pos++; return new Token(TT.LBRACE, "{", pos - 1); }
                if (c == '}') { pos++; return new Token(TT.RBRACE, "}", pos - 1); }
                if (c == ':') { pos++; return new Token(TT.COLON, ":", pos - 1); }

                // Compound assignment / increment operators
                if (c == '+') {
                    if (pos + 1 < src.length()) {
                        if (src.charAt(pos + 1) == '+') { pos += 2; return new Token(TT.PLUSPLUS,     "++", pos - 2); }
                        if (src.charAt(pos + 1) == '=') { pos += 2; return new Token(TT.PLUS_ASSIGN,  "+=", pos - 2); }
                    }
                    pos++; continue; // lone '+' at top level — skip
                }
                if (c == '-') {
                    if (pos + 1 < src.length()) {
                        if (src.charAt(pos + 1) == '-') { pos += 2; return new Token(TT.MINUSMINUS,   "--", pos - 2); }
                        if (src.charAt(pos + 1) == '=') { pos += 2; return new Token(TT.MINUS_ASSIGN, "-=", pos - 2); }
                    }
                    pos++; continue;
                }
                if (c == '*') {
                    if (pos + 1 < src.length() && src.charAt(pos + 1) == '=') {
                        pos += 2; return new Token(TT.STAR_ASSIGN, "*=", pos - 2);
                    }
                    pos++; continue;
                }

                // Assignment = (not ==, !=, <=, >=)
                if (c == '=') {
                    pos++;
                    return new Token(TT.ASSIGN, "=", pos - 1);
                }

                // Identifier or keyword
                if (Character.isLetter(c) || c == '_') {
                    int start = pos;
                    while (!atEnd() && (Character.isLetterOrDigit(ch()) || ch() == '_')) pos++;
                    return new Token(TT.IDENT, src.substring(start, pos), start);
                }

                // Anything else (unexpected top-level char) — skip it
                pos++;
            }
        }

        /**
         * Save and restore the position for speculative parsing.
         * (Simple integer save/restore — the tokenizer has no mutable "last token"
         * state beyond {@code pos}.)
         */
        int mark()            { return pos; }
        void reset(int mark)  { pos = mark; }

        /**
         * Read raw expression text up to (and NOT consuming) the next logical line
         * ending ({@code \n}, {@code ;}, {@code #}) or the first {@code \{} or {@code }}
         * at the outermost paren-depth.  The returned string is trimmed.
         *
         * <p>Parentheses and string literals inside the expression are tracked so that
         * a {@code \{} inside {@code f(x, \{y\})} is NOT treated as a block delimiter.</p>
         */
        String readExprToEOL() {
            return readExprUntil(false);
        }

        /**
         * Read raw expression text up to (and NOT consuming) the next un-nested
         * {@code \{}.  The returned string is trimmed.
         * Used for the condition part of {@code if <cond> \{}.
         */
        String readExprToBrace() {
            return readExprUntil(true);
        }

        /**
         * Core expression extractor.
         *
         * @param stopAtBrace stop at the first {@code \{} at depth 0 (for if-conditions);
         *                    when {@code false}, stop at end-of-line
         */
        private String readExprUntil(boolean stopAtBrace) {
            StringBuilder sb = new StringBuilder();
            int depth = 0; // parenthesis depth (tracks '(' / ')')
            while (!atEnd()) {
                char c = ch();

                // String literal — pass through verbatim
                if (c == '"' || c == '\'') {
                    char q = c;
                    sb.append(c); pos++;
                    while (!atEnd() && ch() != q) {
                        if (ch() == '\\' && pos + 1 < src.length()) {
                            sb.append(ch()); pos++;
                        }
                        sb.append(ch()); pos++;
                    }
                    if (!atEnd()) { sb.append(ch()); pos++; } // closing quote
                    continue;
                }

                // Comment — ends the expression
                if (c == '#') break;

                // Physical newline / semicolon — end of expression
                if (c == '\n' || c == '\r' || c == ';') break;

                if (c == '(') { depth++; sb.append(c); pos++; continue; }
                if (c == ')') { depth--; sb.append(c); pos++; continue; }

                // '{' — stop at outermost level if requested
                if (c == '{') {
                    if (stopAtBrace && depth == 0) break;
                    sb.append(c); pos++;
                    continue;
                }

                // ':' — Python-style block opener; consume and stop when in brace-stop mode
                if (c == ':' && stopAtBrace && depth == 0) {
                    pos++; // consume the ':' so the parser sees the body next
                    break;
                }

                // '}' — should not appear in a plain expression; stop
                if (c == '}') {
                    if (depth == 0) break;
                    sb.append(c); pos++;
                    continue;
                }

                sb.append(c); pos++;
            }
            return sb.toString().trim();
        }
    }

    // =========================================================================
    // Parser  (token-stream → Statement list)
    // =========================================================================

    private static final class Parser {

        private final String    scriptName;
        private final Tokenizer tok;
        private final Logger    log;

        // One-token look-ahead buffer
        private Tokenizer.Token lookahead;

        Parser(String scriptName, Tokenizer tok, Logger log) {
            this.scriptName = scriptName;
            this.tok        = tok;
            this.log        = log;
            this.lookahead  = tok.next(); // prime the lookahead
        }

        // ---- Lookahead helpers -----------------------------------------------

        private Tokenizer.Token peek() { return lookahead; }

        private Tokenizer.Token consume() {
            Tokenizer.Token t = lookahead;
            lookahead = tok.next();
            return t;
        }

        /** Consume EOL tokens until a non-EOL token is at the front. */
        private void skipEOLs() {
            while (peek().type() == Tokenizer.TT.EOL) consume();
        }

        // ---- Block parser ----------------------------------------------------

        /**
         * Parse a sequence of statements until EOF or (when {@code insideBlock=true})
         * until an unmatched {@code \}} is seen.  The {@code \}} is left unconsumed
         * so the if-chain parser can inspect it.
         */
        List<Statement> parseBlock(boolean insideBlock) {
            List<Statement> out = new ArrayList<>();
            while (true) {
                skipEOLs();
                Tokenizer.Token t = peek();

                if (t.type() == Tokenizer.TT.EOF) break;

                // Closing brace — end of block body
                if (t.type() == Tokenizer.TT.RBRACE) {
                    if (insideBlock) break; // leave "}" for if-chain/for/while handler
                    consume(); // stray "}" at top level — discard
                    continue;
                }

                // "if" starts an if-chain
                if (t.type() == Tokenizer.TT.IDENT && t.text().equals("if")) {
                    out.add(parseIfChain());
                    continue;
                }

                // "for" starts a for-loop
                if (t.type() == Tokenizer.TT.IDENT && t.text().equals("for")) {
                    Statement fs = parseForLoop();
                    if (fs != null) out.add(fs);
                    continue;
                }

                // "while" starts a while-loop
                if (t.type() == Tokenizer.TT.IDENT && t.text().equals("while")) {
                    Statement ws = parseWhileLoop();
                    if (ws != null) out.add(ws);
                    continue;
                }

                // IDENT — assignment, compound assignment, or increment
                if (t.type() == Tokenizer.TT.IDENT) {
                    String name = t.text();
                    consume(); // consume IDENT

                    Tokenizer.Token op = peek();

                    // i++ → i = i + 1
                    if (op.type() == Tokenizer.TT.PLUSPLUS) {
                        consume();
                        out.add(compileAssign(name, name + " + 1"));
                        while (peek().type() != Tokenizer.TT.EOL
                                && peek().type() != Tokenizer.TT.EOF) consume();
                        continue;
                    }
                    // i-- → i = i - 1
                    if (op.type() == Tokenizer.TT.MINUSMINUS) {
                        consume();
                        out.add(compileAssign(name, name + " - 1"));
                        while (peek().type() != Tokenizer.TT.EOL
                                && peek().type() != Tokenizer.TT.EOF) consume();
                        continue;
                    }
                    // i += expr → i = i + (expr)
                    if (op.type() == Tokenizer.TT.PLUS_ASSIGN) {
                        consume();
                        String rhs = tok.readExprToEOL();
                        out.add(compileAssign(name, name + " + (" + rhs + ")"));
                        lookahead = tok.next();
                        continue;
                    }
                    // i -= expr → i = i - (expr)
                    if (op.type() == Tokenizer.TT.MINUS_ASSIGN) {
                        consume();
                        String rhs = tok.readExprToEOL();
                        out.add(compileAssign(name, name + " - (" + rhs + ")"));
                        lookahead = tok.next();
                        continue;
                    }
                    // i *= expr → i = i * (expr)
                    if (op.type() == Tokenizer.TT.STAR_ASSIGN) {
                        consume();
                        String rhs = tok.readExprToEOL();
                        out.add(compileAssign(name, name + " * (" + rhs + ")"));
                        lookahead = tok.next();
                        continue;
                    }

                    // Expect plain "="
                    if (op.type() != Tokenizer.TT.ASSIGN) {
                        log.warning("[CEPolyfills] Expected '=' after '" + name
                                + "' in " + scriptName + ".pf — skipping.");
                        while (peek().type() != Tokenizer.TT.EOL
                                && peek().type() != Tokenizer.TT.EOF) consume();
                        continue;
                    }
                    consume(); // consume "="

                    String expr = tok.readExprToEOL();
                    if (expr.isEmpty()) {
                        log.warning("[CEPolyfills] Empty expression for '" + name
                                + "' in " + scriptName + ".pf — skipping.");
                        continue;
                    }
                    Statement assign = compileAssign(name, expr);
                    if (assign != null) out.add(assign);
                    lookahead = tok.next();
                    continue;
                }

                // Anything else — skip
                consume();
            }
            return out;
        }

        // ---- for / while parsers ---------------------------------------------

        /**
         * Parse {@code for var in iterExpr { body }} or {@code for var in iterExpr: body}.
         * Returns {@code null} on parse error.
         *
         * <p>Note: after {@code consume("for")}, the tokenizer's internal position is past the
         * first lookahead token.  We reset the position to {@code lookahead.pos()} so that
         * {@code readExprToBrace()} includes the whole {@code "var in iterExpr"} text.
         */
        Statement parseForLoop() {
            consume(); // consume "for" — lookahead is now the var IDENT
            skipEOLs();

            // Reset tokenizer to the start of the lookahead (var IDENT) so readExprToBrace
            // includes "var in iterExpr" rather than starting after the var name.
            int resetPos = lookahead.pos();
            tok.reset(resetPos);

            // Read the full "var in iterExpr" text, stopping at { or :
            String forHeader = tok.readExprToBrace();
            lookahead = tok.next(); // re-prime lookahead
            skipEOLs();
            if (peek().type() == Tokenizer.TT.LBRACE) consume(); // consume "{"

            List<Statement> body = parseBlock(true);
            skipEOLs();
            if (peek().type() == Tokenizer.TT.RBRACE) consume(); // consume "}"

            // Split header: "varName in iterableExpr"
            int inIdx = forHeader.indexOf(" in ");
            if (inIdx < 0) {
                log.warning("[CEPolyfills] 'for' loop missing 'in' keyword in " + scriptName + ".pf: " + forHeader);
                return null;
            }
            String var      = forHeader.substring(0, inIdx).trim();
            String iterExpr = forHeader.substring(inIdx + 4).trim();
            if (var.isEmpty() || iterExpr.isEmpty()) {
                log.warning("[CEPolyfills] Invalid 'for' loop header in " + scriptName + ".pf: " + forHeader);
                return null;
            }
            return new Statement.ForStatement(var, iterExpr, body);
        }

        /**
         * Parse {@code while condExpr { body }} or {@code while condExpr: body}.
         * Returns {@code null} on parse error.
         *
         * <p>Same tokenizer-reset trick as {@link #parseForLoop()}.
         */
        Statement parseWhileLoop() {
            consume(); // consume "while" — lookahead is now the first condition token
            skipEOLs();

            // Reset to include the lookahead token in the raw expression read
            tok.reset(lookahead.pos());
            String condExpr = tok.readExprToBrace();
            lookahead = tok.next();
            skipEOLs();
            if (peek().type() == Tokenizer.TT.LBRACE) consume();

            List<Statement> body = parseBlock(true);
            skipEOLs();
            if (peek().type() == Tokenizer.TT.RBRACE) consume();

            if (condExpr.isEmpty()) {
                log.warning("[CEPolyfills] Empty condition in 'while' in " + scriptName + ".pf");
                return null;
            }
            return new Statement.WhileStatement(condExpr, body, 1000);
        }

        // ---- Helper ----------------------------------------------------------

        private Statement.Assign compileAssign(String name, String expr) {
            try {
                return new Statement.Assign(name, PolyFormula.compile(expr));
            } catch (IllegalArgumentException ex) {
                log.warning("[CEPolyfills] Bad expression '" + name
                        + "' in " + scriptName + ".pf: " + ex.getMessage());
                return null;
            }
        }

        // ---- If-chain parser -------------------------------------------------

        /**
         * Parse a full {@code if / else if / else} chain.
         * The lookahead must be at the {@code "if"} token when called.
         * Returns with the lookahead positioned after the final {@code \}}.
         */
        Statement.IfChain parseIfChain() {
            List<Statement.Clause> clauses = new ArrayList<>();

            while (true) {
                skipEOLs();
                Tokenizer.Token t = peek();

                // "if <cond> {" — opening or the first branch
                if (t.type() == Tokenizer.TT.IDENT && t.text().equals("if")) {
                    consume(); // consume "if"
                    String condExpr = tok.readExprToBrace();
                    lookahead = tok.next(); // re-prime after raw read
                    skipEOLs();
                    if (peek().type() == Tokenizer.TT.LBRACE) consume(); // consume "{"
                    List<Statement> body = parseBlock(true);
                    clauses.add(new Statement.Clause(compileCondition(condExpr), body));
                    // cursor is now at the "}" or "} else ..." position — loop continues

                // "} else if <cond> {" — additional else-if clause
                } else if (t.type() == Tokenizer.TT.RBRACE) {
                    // Peek further: is the next non-EOL token "else"?
                    int mark = tok.mark();
                    consume(); // consume "}"

                    skipEOLs();
                    Tokenizer.Token maybeElse = peek();
                    if (maybeElse.type() == Tokenizer.TT.IDENT
                            && maybeElse.text().equals("else")) {
                        consume(); // consume "else"
                        skipEOLs();
                        Tokenizer.Token afterElse = peek();

                        if (afterElse.type() == Tokenizer.TT.IDENT
                                && afterElse.text().equals("if")) {
                            // "} else if <cond> {"
                            consume(); // consume "if"
                            String condExpr = tok.readExprToBrace();
                            lookahead = tok.next();
                            skipEOLs();
                            if (peek().type() == Tokenizer.TT.LBRACE) consume();
                            List<Statement> body = parseBlock(true);
                            clauses.add(new Statement.Clause(compileCondition(condExpr), body));

                        } else if (afterElse.type() == Tokenizer.TT.LBRACE) {
                            // "} else {"
                            consume(); // consume "{"
                            List<Statement> body = parseBlock(true);
                            clauses.add(new Statement.Clause(null, body));
                            // consume the final "}"
                            skipEOLs();
                            if (peek().type() == Tokenizer.TT.RBRACE) consume();
                            break; // else is always last

                        } else {
                            // "} else" with no "{" — treat as plain else {}
                            List<Statement> body = parseBlock(true);
                            clauses.add(new Statement.Clause(null, body));
                            skipEOLs();
                            if (peek().type() == Tokenizer.TT.RBRACE) consume();
                            break;
                        }
                    } else {
                        // "}" with nothing after — end of chain
                        // (the "}" was already consumed above; lookahead is at "maybeElse")
                        break;
                    }

                } else {
                    break; // unexpected token — stop
                }
            }

            return new Statement.IfChain(clauses);
        }

        // ---- Helper ----------------------------------------------------------

        private PolyFormula compileCondition(String expr) {
            if (expr.isEmpty()) {
                log.warning("[CEPolyfills] Empty condition in " + scriptName + ".pf");
                return PolyFormula.compile("__false__"); // always false
            }
            try {
                return PolyFormula.compile(expr);
            } catch (IllegalArgumentException ex) {
                log.warning("[CEPolyfills] Bad condition in " + scriptName
                        + ".pf: " + ex.getMessage());
                return PolyFormula.compile("__false__");
            }
        }
    }
}
