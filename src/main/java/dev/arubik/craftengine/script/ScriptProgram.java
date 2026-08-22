package dev.arubik.craftengine.script;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Logger;

/**
 * A loaded {@code .pf} script file — the PolyFill interpreter.
 *
 * <p>Supports: assignments, if/else-if/else, for-in loops, while loops,
 * break, continue, i++/i--, i+=/-=/*=, comments (#).</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * speed = abs(rpm) / 10
 * if speed > 0 {
 *     for item in Machine.nearby_items(3) {
 *         Machine.push_item_to_inventory(item)
 *     }
 * }
 * }</pre>
 *
 * Port of PolyScript to the new ScriptFormula / ScriptContext system.
 */
public final class ScriptProgram {

    private final String name;
    private final Map<String, ScriptFormula> topLevel;
    private final List<Statement> statements;

    // ---- Statement model -----------------------------------------------------

    sealed interface Statement {
        record Assign(String name, ScriptFormula formula) implements Statement {}
        record ExprStatement(ScriptFormula formula) implements Statement {}
        record IfChain(List<Clause> clauses) implements Statement {}
        /** {@code vars} holds one name for a plain {@code for x in arr}, or two for Polyloft-style
         *  destructuring ({@code for key, value in map}). {@code guardExpr} is the optional
         *  Polyloft {@code where <cond>} clause — null when absent, re-checked every iteration and
         *  skipping (not stopping) elements that fail it. */
        record ForStatement(List<String> vars, String iterExpr, String guardExpr, List<Statement> body) implements Statement {}
        record WhileStatement(String condExpr, List<Statement> body, int maxIter) implements Statement {}
        record BreakStatement() implements Statement {}
        record ContinueStatement() implements Statement {}
        record ReturnStatement(String expr) implements Statement {}
        record FunctionDef(String name, List<String> params, List<Statement> body) implements Statement {}
    }

    record Clause(ScriptFormula condition, List<Statement> body) {
        boolean isElse() { return condition == null; }
    }

    /** Signal thrown to implement break/continue without exceptions-as-flow-control overhead. */
    private static final class BreakSignal extends RuntimeException {
        BreakSignal() { super(null, null, true, false); }
    }
    private static final class ContinueSignal extends RuntimeException {
        ContinueSignal() { super(null, null, true, false); }
    }
    private static final class ReturnSignal extends RuntimeException {
        final ScriptValue value;
        ReturnSignal(ScriptValue v) { super(null, null, true, false); this.value = v; }
    }

    // ---- Construction --------------------------------------------------------

    private ScriptProgram(String name, Map<String, ScriptFormula> topLevel, List<Statement> statements) {
        this.name = name;
        this.topLevel = topLevel;
        this.statements = statements;
    }

    public String name() { return name; }
    public Map<String, ScriptFormula> entries() { return topLevel; }
    public ScriptFormula get(String name) { return topLevel.get(name); }

    // ---- Loading -------------------------------------------------------------

    public static ScriptProgram load(File file) {
        Logger log = java.util.logging.Logger.getLogger("CraftEnginePolyfills");
        String scriptName = file.getName().replaceAll("\\.pf$", "");
        String src;
        try {
            src = new String(Files.readAllBytes(file.toPath()));
        } catch (Throwable ex) {
            log.warning("[CEPolyfills] Failed to load script " + file + ": " + ex.getMessage());
            return new ScriptProgram(scriptName, Map.of(), List.of());
        }
        return parse(scriptName, src, log);
    }

    public static ScriptProgram parse(String name, String src, Logger log) {
        Tokenizer tok = new Tokenizer(src);
        Parser par = new Parser(name, tok, log);
        List<Statement> stmts = par.parseBlock(false);
        Map<String, ScriptFormula> entries = new LinkedHashMap<>();
        for (Statement s : stmts)
            if (s instanceof Statement.Assign a) entries.put(a.name(), a.formula());
        return new ScriptProgram(name, Collections.unmodifiableMap(entries), stmts);
    }

    // ---- Evaluation ----------------------------------------------------------

    /**
     * Run the script against {@code ctx}, returning an updated context with new variable values.
     */
    public ScriptContext evaluate(ScriptContext ctx) {
        ScriptContext.Builder b = ScriptContext.builder().copyFrom(ctx);
        try { runStatements(statements, b); }
        catch (ReturnSignal rs) { b.val("__return__", rs.value); }
        return b.build();
    }

    /**
     * Run the script and return the final value of a named variable, or NULL.
     */
    public ScriptValue evaluateVar(ScriptContext ctx, String varName) {
        ScriptContext result = evaluate(ctx);
        return result.getVar(varName);
    }

    /**
     * Public accessor so ScriptFormula can execute user-defined function bodies.
     * Propagates ReturnSignal upward.
     */
    public static void executeStatements(List<Statement> stmts, ScriptContext.Builder b) {
        runStatements(stmts, b);
    }

    private static void runStatements(List<Statement> stmts, ScriptContext.Builder b) {
        for (Statement stmt : stmts) {
            switch (stmt) {
                case Statement.Assign a -> {
                    ScriptContext snap = b.build();
                    try { b.val(a.name(), a.formula().evaluate(snap)); }
                    catch (Throwable ignored) {}
                }
                case Statement.ExprStatement es -> {
                    ScriptContext snap = b.build();
                    try { es.formula().evaluate(snap); } catch (Throwable ignored) {}
                }
                case Statement.IfChain chain -> {
                    ScriptContext snap = b.build();
                    for (Clause clause : chain.clauses()) {
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
                    ScriptContext snap = b.build();
                    // Array evaluation genuinely wants to swallow errors (a bad iterExpr just
                    // skips the loop) — but that catch must NOT also wrap the loop body, or a
                    // `return` inside the loop throws ReturnSignal straight into this same
                    // catch(Throwable) and gets silently discarded instead of exiting the
                    // function (the loop looks like it "ran to the end" even after returning).
                    //
                    // Two loop vars = Polyloft-style destructuring: `for key, value in map` walks
                    // entries, binding both; `for x in map` (one var) walks its KEYS, matching the
                    // common for-in-dict convention. One var over anything else walks elements as
                    // before (a Map is otherwise opaque to plain iteration).
                    List<String> vars = fs.vars();
                    List<ScriptValue[]> rows;
                    try {
                        ScriptValue iterable = ScriptFormula.compile(fs.iterExpr()).evaluate(snap);
                        if (iterable instanceof ScriptValue.Obj o && "Map".equals(o.typeName())
                                && o.instance() instanceof Map<?, ?> rawMap) {
                            rows = new ArrayList<>();
                            for (Map.Entry<?, ?> e : rawMap.entrySet()) {
                                ScriptValue key = ScriptValue.of(String.valueOf(e.getKey()));
                                ScriptValue val = (ScriptValue) e.getValue();
                                rows.add(vars.size() >= 2 ? new ScriptValue[]{key, val} : new ScriptValue[]{key});
                            }
                        } else {
                            List<ScriptValue> elems = switch (iterable) {
                                case ScriptValue.Array a -> a.elements();
                                case ScriptValue.Null ignored -> null;
                                default -> List.of(iterable);
                            };
                            rows = elems == null ? null : elems.stream().map(v -> new ScriptValue[]{v}).toList();
                        }
                    } catch (Throwable ignored) { rows = null; }
                    if (rows != null) {
                        outer:
                        for (ScriptValue[] row : rows) {
                            for (int i = 0; i < vars.size(); i++)
                                b.val(vars.get(i), i < row.length ? row[i] : ScriptValue.NULL);
                            if (fs.guardExpr() != null) {
                                boolean pass;
                                try { pass = ScriptFormula.compile(fs.guardExpr()).evaluateBool(b.build()); }
                                catch (Throwable ignored) { pass = false; }
                                if (!pass) continue;
                            }
                            try { runStatements(fs.body(), b); }
                            catch (BreakSignal ignored) { break outer; }
                            catch (ContinueSignal ignored) { /* next iteration */ }
                        }
                    }
                }
                case Statement.WhileStatement ws -> {
                    int iters = 0;
                    outer:
                    while (iters++ < ws.maxIter()) {
                        ScriptContext snap = b.build();
                        boolean cond;
                        try { cond = ScriptFormula.compile(ws.condExpr()).evaluateBool(snap); }
                        catch (Throwable ignored) { break; }
                        if (!cond) break;
                        try { runStatements(ws.body(), b); }
                        catch (BreakSignal ignored) { break outer; }
                        catch (ContinueSignal ignored) { /* next iteration */ }
                    }
                }
                case Statement.BreakStatement ignored -> throw new BreakSignal();
                case Statement.ContinueStatement ignored -> throw new ContinueSignal();
                case Statement.ReturnStatement rs -> {
                    ScriptValue val = ScriptValue.NULL;
                    if (!rs.expr().isEmpty()) {
                        try { val = ScriptFormula.compile(rs.expr()).evaluate(b.build()); }
                        catch (Throwable ignored) {}
                    }
                    throw new ReturnSignal(val);
                }
                case Statement.FunctionDef fd -> {
                    // Capture body and params — build a UserFunction and store as var
                    List<Statement> capturedBody = fd.body();
                    List<String> capturedParams = fd.params();
                    UserFunction fn = new UserFunction(fd.name(), capturedParams,
                        (callerCtx, resultB) -> {
                            try { runStatements(capturedBody, resultB); }
                            catch (ReturnSignal rs) { resultB.val("__return__", rs.value); }
                        });
                    b.val(fd.name(), ScriptValue.ofObj(UserFunction.TYPE, fn));
                }
            }
        }
    }

    // =========================================================================
    // Tokenizer
    // =========================================================================

    private static final class Tokenizer {
        enum TT { IDENT, ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, STAR_ASSIGN, SLASH_ASSIGN,
                  PERCENT_ASSIGN, PLUSPLUS, MINUSMINUS, COLON, LBRACE, RBRACE, EOL, EOF }

        record Token(TT type, String text, int pos) {}

        private final String src;
        private int pos;

        Tokenizer(String src) { this.src = src; this.pos = 0; }

        private boolean atEnd() { return pos >= src.length(); }
        private char ch()       { return src.charAt(pos); }
        private void skipHSpace() { while (!atEnd() && (ch() == ' ' || ch() == '\t')) pos++; }

        Token next() {
            while (true) {
                skipHSpace();
                if (atEnd()) return new Token(TT.EOF, "", pos);
                char c = ch();
                if (c == '\n' || c == '\r') { while (!atEnd() && (ch() == '\n' || ch() == '\r')) pos++; return new Token(TT.EOL, "\n", pos); }
                if (c == '#') { while (!atEnd() && ch() != '\n') pos++; return new Token(TT.EOL, "#", pos); }
                if (c == ';') { pos++; return new Token(TT.EOL, ";", pos - 1); }
                if (c == '{') { pos++; return new Token(TT.LBRACE, "{", pos - 1); }
                if (c == '}') { pos++; return new Token(TT.RBRACE, "}", pos - 1); }
                if (c == ':') { pos++; return new Token(TT.COLON, ":", pos - 1); }
                if (c == '+') {
                    if (pos+1 < src.length()) {
                        if (src.charAt(pos+1) == '+') { pos+=2; return new Token(TT.PLUSPLUS, "++", pos-2); }
                        if (src.charAt(pos+1) == '=') { pos+=2; return new Token(TT.PLUS_ASSIGN, "+=", pos-2); }
                    }
                    pos++; continue;
                }
                if (c == '-') {
                    if (pos+1 < src.length()) {
                        if (src.charAt(pos+1) == '-') { pos+=2; return new Token(TT.MINUSMINUS, "--", pos-2); }
                        if (src.charAt(pos+1) == '=') { pos+=2; return new Token(TT.MINUS_ASSIGN, "-=", pos-2); }
                    }
                    pos++; continue;
                }
                if (c == '*') { if (pos+1 < src.length() && src.charAt(pos+1) == '=') { pos+=2; return new Token(TT.STAR_ASSIGN, "*=", pos-2); } pos++; continue; }
                if (c == '/') { if (pos+1 < src.length() && src.charAt(pos+1) == '=') { pos+=2; return new Token(TT.SLASH_ASSIGN, "/=", pos-2); } pos++; continue; }
                if (c == '%') { if (pos+1 < src.length() && src.charAt(pos+1) == '=') { pos+=2; return new Token(TT.PERCENT_ASSIGN, "%=", pos-2); } pos++; continue; }
                if (c == '=') { pos++; return new Token(TT.ASSIGN, "=", pos-1); }
                if (Character.isLetter(c) || c == '_') {
                    int start = pos;
                    while (!atEnd() && (Character.isLetterOrDigit(ch()) || ch() == '_')) pos++;
                    return new Token(TT.IDENT, src.substring(start, pos), start);
                }
                pos++;
            }
        }

        int mark()           { return pos; }
        void reset(int mark) { pos = mark; }

        /** Find the position of char c at or before startPos (scanning backwards). Returns -1 if not found. */
        int findCharBefore(char c, int startPos) {
            for (int i = startPos; i >= 0; i--) {
                if (i < src.length() && src.charAt(i) == c) return i;
            }
            return -1;
        }

        String readExprToEOL()   { return readExprUntil(false); }
        String readExprToBrace() { return readExprUntil(true); }

        private String readExprUntil(boolean stopAtBrace) {
            StringBuilder sb = new StringBuilder();
            int depth = 0;
            int bracketDepth = 0; // track [] so array literals don't get cut
            while (!atEnd()) {
                char c = ch();
                if (c == '"' || c == '\'') {
                    char q = c; sb.append(c); pos++;
                    while (!atEnd() && ch() != q) {
                        if (ch() == '\\' && pos+1 < src.length()) { sb.append(ch()); pos++; }
                        sb.append(ch()); pos++;
                    }
                    if (!atEnd()) { sb.append(ch()); pos++; }
                    continue;
                }
                if (c == '#') break;
                // Only stop at newline/semicolon when outside all brackets (allow multiline expressions inside parens/brackets)
                if ((c == '\n' || c == '\r' || c == ';') && depth == 0 && bracketDepth == 0) break;
                if (c == '\n' || c == '\r') { pos++; continue; } // inside parens: skip newline, don't append
                if (c == '(') { depth++; sb.append(c); pos++; continue; }
                if (c == ')') { depth--; sb.append(c); pos++; continue; }
                if (c == '[') { bracketDepth++; sb.append(c); pos++; continue; }
                if (c == ']') { bracketDepth--; sb.append(c); pos++; continue; }
                if (c == '{') { if (stopAtBrace && depth == 0 && bracketDepth == 0) break; sb.append(c); pos++; continue; }
                if (c == ':' && stopAtBrace && depth == 0 && bracketDepth == 0) { pos++; break; }
                if (c == '}') { if (depth == 0 && bracketDepth == 0) break; sb.append(c); pos++; continue; }
                sb.append(c); pos++;
            }
            return sb.toString().trim();
        }
    }

    // =========================================================================
    // Parser
    // =========================================================================

    private static final class Parser {
        private final String scriptName;
        private final Tokenizer tok;
        private final Logger log;
        private Tokenizer.Token lookahead;

        Parser(String scriptName, Tokenizer tok, Logger log) {
            this.scriptName = scriptName;
            this.tok = tok;
            this.log = log;
            this.lookahead = tok.next();
        }

        private Tokenizer.Token peek() { return lookahead; }
        private Tokenizer.Token consume() { Tokenizer.Token t = lookahead; lookahead = tok.next(); return t; }
        private void skipEOLs() { while (peek().type() == Tokenizer.TT.EOL) consume(); }

        /** Bare keywords that close a colon-style block body — checked only when
         *  {@code insideBlock}, so they stay ordinary identifiers at the top level, and ONLY
         *  passed for bodies actually opened with ':' — a brace-style body never treats "end"/
         *  "else" as special, so an existing script using either as a plain variable name (however
         *  unlikely) keeps working unchanged. */
        private static final Set<String> NONE = Set.of();
        private static final Set<String> END_ONLY = Set.of("end");
        private static final Set<String> END_OR_ELSE = Set.of("end", "else");

        List<Statement> parseBlock(boolean insideBlock) { return parseBlock(insideBlock, NONE); }

        List<Statement> parseBlock(boolean insideBlock, Set<String> stopWords) {
            List<Statement> out = new ArrayList<>();
            while (true) {
                skipEOLs();
                Tokenizer.Token t = peek();
                if (t.type() == Tokenizer.TT.EOF) break;
                if (t.type() == Tokenizer.TT.RBRACE) { if (insideBlock) break; consume(); continue; }
                // Colon-style closer (Polyloft "end", and "else" for if-chains) — left UN-consumed,
                // same as RBRACE above, so the caller (parseForLoop/parseIfChain/...) decides what
                // to do with it (plain consume, or branch into an else-clause).
                if (insideBlock && t.type() == Tokenizer.TT.IDENT && stopWords.contains(t.text())) break;
                Statement s = parseOneStatement();
                if (s != null) out.add(s);
            }
            return out;
        }

        /**
         * Parses exactly ONE statement at the current position and returns it (or null for a
         * malformed/empty one — matches each case's original behavior). Shared by
         * {@link #parseBlock}'s loop and inline colon bodies ({@code if x: return 1}, a single
         * statement with no {@code end} — see {@link #parseInlineOrBlockBody}), where reusing
         * this instead of duplicating the whole dispatch keeps the two forms from drifting apart.
         */
        private Statement parseOneStatement() {
            skipEOLs();
            Tokenizer.Token t = peek();
            if (t.type() != Tokenizer.TT.IDENT) {
                if (t.type() != Tokenizer.TT.EOF && t.type() != Tokenizer.TT.RBRACE) consume();
                return null;
            }
            switch (t.text()) {
                case "if"       -> { return parseIfChain(); }
                case "for"      -> { return parseForLoop(); }
                case "while"    -> { return parseWhileLoop(); }
                case "break"    -> { consume(); return new Statement.BreakStatement(); }
                case "continue" -> { consume(); return new Statement.ContinueStatement(); }
                case "return" -> {
                    int afterReturn = t.pos() + t.text().length();
                    consume();
                    tok.reset(afterReturn);
                    String retExpr = tok.readExprToEOL();
                    lookahead = tok.next();
                    return new Statement.ReturnStatement(retExpr);
                }
                case "def" -> { return parseFunctionDef(); }
            }

            String name = t.text();
            consume();
            Tokenizer.Token op = peek();

            if (op.type() == Tokenizer.TT.PLUSPLUS)     { consume(); Statement s = compile(name, name + " + 1"); skipLine(); return s; }
            if (op.type() == Tokenizer.TT.MINUSMINUS)   { consume(); Statement s = compile(name, name + " - 1"); skipLine(); return s; }
            if (op.type() == Tokenizer.TT.PLUS_ASSIGN)  { int ps = op.pos()+2; consume(); tok.reset(ps); Statement s = compile(name, name + " + (" + tok.readExprToEOL() + ")"); lookahead = tok.next(); return s; }
            if (op.type() == Tokenizer.TT.MINUS_ASSIGN) { int ps = op.pos()+2; consume(); tok.reset(ps); Statement s = compile(name, name + " - (" + tok.readExprToEOL() + ")"); lookahead = tok.next(); return s; }
            if (op.type() == Tokenizer.TT.STAR_ASSIGN)  { int ps = op.pos()+2; consume(); tok.reset(ps); Statement s = compile(name, name + " * (" + tok.readExprToEOL() + ")"); lookahead = tok.next(); return s; }
            if (op.type() == Tokenizer.TT.SLASH_ASSIGN) { int ps = op.pos()+2; consume(); tok.reset(ps); Statement s = compile(name, name + " / (" + tok.readExprToEOL() + ")"); lookahead = tok.next(); return s; }
            if (op.type() == Tokenizer.TT.PERCENT_ASSIGN) { int ps = op.pos()+2; consume(); tok.reset(ps); Statement s = compile(name, name + " % (" + tok.readExprToEOL() + ")"); lookahead = tok.next(); return s; }

            if (op.type() != Tokenizer.TT.ASSIGN) {
                // No '=' — treat as expression statement (e.g. Machine.set_flag(...), entity.remove())
                tok.reset(t.pos()); // reset to start of the IDENT name
                String callExpr = tok.readExprToEOL();
                lookahead = tok.next();
                if (callExpr.isEmpty()) return null;
                try { return new Statement.ExprStatement(ScriptFormula.compile(callExpr)); }
                catch (Throwable ignored) { return null; }
            }
            int exprStart = op.pos() + 1; // right after '='
            consume(); // consume "="
            tok.reset(exprStart); // reset to right after '=' — includes '(', '.', etc.
            String expr = tok.readExprToEOL();
            Statement s = !expr.isEmpty() ? compile(name, expr) : null;
            lookahead = tok.next();
            return s;
        }

        private void skipLine() { while (peek().type() != Tokenizer.TT.EOL && peek().type() != Tokenizer.TT.EOF) consume(); }

        /** Consumes whichever closer is actually present after a colon/brace body — {@code }}
         *  for brace-style, bare {@code end} for Polyloft colon-style. Never used where "else"
         *  must stay un-consumed (if-chains handle that themselves). */
        private void consumeGenericCloser() {
            skipEOLs();
            if (peek().type() == Tokenizer.TT.RBRACE) consume();
            else if (peek().type() == Tokenizer.TT.IDENT && peek().text().equals("end")) consume();
        }

        /** result of {@link #parseOpenedBody}: the parsed body, whether it opened with '{'
         *  (brace-style), and whether the caller still needs to consume a closer — an inline
         *  colon body (single statement, no "end") has nothing left to close. */
        private record OpenedBody(List<Statement> body, boolean brace, boolean needsCloser) {}

        /**
         * Parses whichever body form follows a header whose ':' or '{' has already been read
         * (or, for a bare "else"/"else if" with no header expression at all, is still sitting
         * un-consumed as a raw COLON token — handled here too):
         *
         * <ul>
         *   <li>{@code { ... }} — brace-style, needs its '}' consumed by the caller.</li>
         *   <li>{@code : <stmt>} on the SAME line — Polyloft-adjacent INLINE form, a single
         *       statement with no trailing "end" at all ({@code if x: return 1}). Nothing to
         *       close afterward.</li>
         *   <li>{@code :\n ... \nend} — Polyloft multi-line colon body, needs its "end" (or a
         *       chained "else"/"else if", handled by the if-chain caller) consumed.</li>
         * </ul>
         */
        private OpenedBody parseOpenedBody(Set<String> multilineStopWords) {
            if (peek().type() == Tokenizer.TT.LBRACE) {
                consume();
                return new OpenedBody(parseBlock(true, NONE), true, true);
            }
            // A bare "else:"/"end:" with no expression before it never runs its colon through
            // readExprToBrace (nothing to read as a header) — it's still sitting in the token
            // stream here as a raw COLON, so consume it explicitly instead of leaving it dangling.
            if (peek().type() == Tokenizer.TT.COLON) consume();
            if (peek().type() != Tokenizer.TT.EOL && peek().type() != Tokenizer.TT.EOF) {
                // Content right on the same physical line as the colon, no intervening EOL —
                // Polyloft never does this (always multi-line + "end"), but it's an unambiguous,
                // useful extra: a single-statement guard clause needs no block at all.
                Statement one = parseOneStatement();
                return new OpenedBody(one != null ? List.of(one) : List.of(), false, false);
            }
            skipEOLs();
            if (peek().type() == Tokenizer.TT.LBRACE) { // Allman-style brace on its own line
                consume();
                return new OpenedBody(parseBlock(true, NONE), true, true);
            }
            return new OpenedBody(parseBlock(true, multilineStopWords), false, true);
        }

        /** Finds a top-level (not inside quotes/parens/brackets) " where " in a for-header's
         *  post-"in" remainder, splitting the iterable from Polyloft's optional guard clause:
         *  {@code for i in range(0, 6) where i < 5}. -1 if absent. */
        private static int indexOfTopLevelWhere(String s) {
            int depth = 0;
            boolean inStr = false;
            char strCh = 0;
            for (int i = 0; i + 7 <= s.length(); i++) {
                char c = s.charAt(i);
                if (inStr) {
                    if (c == '\\') { i++; continue; }
                    if (c == strCh) inStr = false;
                    continue;
                }
                if (c == '"' || c == '\'') { inStr = true; strCh = c; continue; }
                if (c == '(' || c == '[') { depth++; continue; }
                if (c == ')' || c == ']') { depth--; continue; }
                if (depth == 0 && s.regionMatches(i, " where ", 0, 7)) return i;
            }
            return -1;
        }

        /**
         * Parses either style:
         *   for x in arr { ... }                          (brace)
         *   for x in arr:\n ... \nend                      (Polyloft colon/end)
         *   for k, v in map:\n ... \nend                    (Polyloft destructuring)
         *   for i in range(0,6) where i < 5:\n ... \nend    (Polyloft guard clause)
         */
        Statement parseForLoop() {
            consume(); // "for"
            skipEOLs();
            tok.reset(lookahead.pos());
            String forHeader = tok.readExprToBrace(); // stops at '{' or a top-level ':'
            lookahead = tok.next();
            OpenedBody opened = parseOpenedBody(END_ONLY);
            if (opened.needsCloser()) consumeGenericCloser();
            List<Statement> body = opened.body();
            int inIdx = forHeader.indexOf(" in ");
            if (inIdx < 0) { log.warning("[CEPolyfills] 'for' missing 'in' in " + scriptName + ".pf"); return null; }
            String varsPart = forHeader.substring(0, inIdx).trim();
            String rest = forHeader.substring(inIdx + 4).trim();
            String guardExpr = null;
            int whereIdx = indexOfTopLevelWhere(rest);
            if (whereIdx >= 0) {
                guardExpr = rest.substring(whereIdx + 7).trim();
                rest = rest.substring(0, whereIdx).trim();
            }
            List<String> vars = new ArrayList<>();
            for (String v : varsPart.split(",")) {
                String vn = v.trim();
                if (!vn.isEmpty()) vars.add(vn);
            }
            if (vars.isEmpty() || rest.isEmpty()) { log.warning("[CEPolyfills] Bad 'for' header in " + scriptName + ".pf"); return null; }
            return new Statement.ForStatement(vars, rest, guardExpr, body);
        }

        Statement parseWhileLoop() {
            consume(); // "while"
            skipEOLs();
            tok.reset(lookahead.pos());
            String condExpr = tok.readExprToBrace();
            lookahead = tok.next();
            OpenedBody opened = parseOpenedBody(END_ONLY);
            if (opened.needsCloser()) consumeGenericCloser();
            List<Statement> body = opened.body();
            if (condExpr.isEmpty()) { log.warning("[CEPolyfills] Empty 'while' condition in " + scriptName + ".pf"); return null; }
            return new Statement.WhileStatement(condExpr, body, 1000);
        }

        /**
         * Parses if/else-if/else in either style. Brace-style chains one clause per
         * {@code } else { }/{ } else if ... { }} pair, exactly as before. Colon-style has ONE
         * trailing {@code end} for the whole chain (Polyloft): {@code if c: ... else: ... end}.
         */
        Statement.IfChain parseIfChain() {
            List<Clause> clauses = new ArrayList<>();
            while (true) {
                skipEOLs();
                Tokenizer.Token t = peek();
                if (t.type() == Tokenizer.TT.IDENT && t.text().equals("if")) {
                    // Handles both the opening "if" AND an "else if" reached via loop-back below —
                    // deliberately does NOT consume its own closer (RBRACE or bare "end"/"else"):
                    // looping back to the top lets the RBRACE/"end"/"else" arms below decide what
                    // comes next, exactly how the original brace-only design chained clauses.
                    int afterIf = t.pos() + t.text().length();
                    consume();
                    tok.reset(afterIf);
                    String condExpr = tok.readExprToBrace();
                    lookahead = tok.next();
                    clauses.add(new Clause(compileFormula(condExpr), parseOpenedBody(END_OR_ELSE).body()));
                } else if (t.type() == Tokenizer.TT.RBRACE) {
                    // Brace-style clause just closed — consume it and look for a chained else.
                    consume();
                    skipEOLs();
                    Tokenizer.Token maybeElse = peek();
                    if (maybeElse.type() == Tokenizer.TT.IDENT && maybeElse.text().equals("else")) {
                        consume();
                        skipEOLs();
                        Tokenizer.Token afterElse = peek();
                        if (afterElse.type() == Tokenizer.TT.IDENT && afterElse.text().equals("if")) {
                            int afterElseIf = afterElse.pos() + afterElse.text().length();
                            consume();
                            tok.reset(afterElseIf);
                            String elseIfCond = tok.readExprToBrace();
                            lookahead = tok.next();
                            clauses.add(new Clause(compileFormula(elseIfCond), parseOpenedBody(END_OR_ELSE).body()));
                            // loop back — its closer is handled by the RBRACE/end arms next time around
                        } else {
                            clauses.add(new Clause(null, parseOpenedBody(END_OR_ELSE).body()));
                            // a plain else is always terminal — loop back just to consume its own
                            // closer (RBRACE or "end"), then the next iteration's "no else follows
                            // a plain else" check naturally ends the chain.
                        }
                    } else { break; }
                } else if (t.type() == Tokenizer.TT.IDENT && t.text().equals("end")) {
                    consume(); break; // colon-style chain fully closed, no trailing else
                } else if (t.type() == Tokenizer.TT.IDENT && t.text().equals("else")) {
                    // Colon-style continuation, reached via loop-back after a colon-style clause
                    // stopped at a bare "else" instead of "end" — same else/else-if shape as the
                    // RBRACE arm above, just with no brace to consume first.
                    consume();
                    skipEOLs();
                    Tokenizer.Token afterElse = peek();
                    if (afterElse.type() == Tokenizer.TT.IDENT && afterElse.text().equals("if")) {
                        int afterElseIf = afterElse.pos() + afterElse.text().length();
                        consume();
                        tok.reset(afterElseIf);
                        String elseIfCond = tok.readExprToBrace();
                        lookahead = tok.next();
                        clauses.add(new Clause(compileFormula(elseIfCond), parseOpenedBody(END_OR_ELSE).body()));
                    } else {
                        clauses.add(new Clause(null, parseOpenedBody(END_OR_ELSE).body()));
                    }
                } else { break; }
            }
            return new Statement.IfChain(clauses);
        }

        /**
         * Parses: def name(param1, param2, ...) { body }   or   def name(...): body end
         * Called after consuming the "def" token.
         */
        Statement parseFunctionDef() {
            consume(); // consume "def"
            skipEOLs();
            Tokenizer.Token nameTok = peek();
            if (nameTok.type() != Tokenizer.TT.IDENT) {
                log.warning("[CEPolyfills] 'def' missing function name in " + scriptName + ".pf");
                skipLine(); return null;
            }
            String funcName = nameTok.text();
            consume(); // consume name
            // Read param list: "(a, b, c)"
            // tok.next() skips '(' since it's not a tokenizer token, so lookahead.pos()
            // lands AFTER '('. We must reset to the '(' itself so that readExprToBrace
            // sees depth=0→1 (from '(') and correctly stops at the matching '{' or ':'.
            int openParen = tok.findCharBefore('(', lookahead.pos() - 1);
            tok.reset(openParen >= 0 ? openParen : lookahead.pos());
            String rawParams = tok.readExprToBrace(); // reads "(params)" up to '{' or ':'
            lookahead = tok.next();
            OpenedBody opened = parseOpenedBody(END_ONLY);
            if (opened.needsCloser()) consumeGenericCloser();
            List<Statement> body = opened.body();
            // Parse param names from rawParams: "(a, b, c)" → ["a","b","c"]
            List<String> params = new ArrayList<>();
            String paramStr = rawParams.trim();
            if (paramStr.startsWith("(")) paramStr = paramStr.substring(1);
            if (paramStr.endsWith(")")) paramStr = paramStr.substring(0, paramStr.length() - 1);
            for (String p : paramStr.split(",")) {
                String pn = p.trim();
                if (!pn.isEmpty()) params.add(pn);
            }
            return new Statement.FunctionDef(funcName, params, body);
        }

        private Statement.Assign compile(String name, String expr) {
            try { return new Statement.Assign(name, ScriptFormula.compile(expr)); }
            catch (IllegalArgumentException ex) { log.warning("[CEPolyfills] Bad expr '" + name + "' in " + scriptName + ".pf: " + ex.getMessage()); return null; }
        }

        private ScriptFormula compileFormula(String expr) {
            if (expr.isEmpty()) return ScriptFormula.compile("false");
            try { return ScriptFormula.compile(expr); }
            catch (IllegalArgumentException ex) { log.warning("[CEPolyfills] Bad condition in " + scriptName + ".pf: " + ex.getMessage()); return ScriptFormula.compile("false"); }
        }
    }
}
