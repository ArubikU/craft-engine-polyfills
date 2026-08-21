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
        record ForStatement(String var, String iterExpr, List<Statement> body) implements Statement {}
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
                    try {
                        ScriptValue arr = ScriptFormula.compile(fs.iterExpr()).evaluate(snap);
                        List<ScriptValue> elems = switch (arr) {
                            case ScriptValue.Array a -> a.elements();
                            case ScriptValue.Null ignored -> null;
                            default -> List.of(arr);
                        };
                        if (elems != null) {
                            outer:
                            for (ScriptValue elem : elems) {
                                b.val(fs.var(), elem);
                                try { runStatements(fs.body(), b); }
                                catch (BreakSignal ignored) { break outer; }
                                catch (ContinueSignal ignored) { /* next iteration */ }
                            }
                        }
                    } catch (Throwable ignored) {}
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

        List<Statement> parseBlock(boolean insideBlock) {
            List<Statement> out = new ArrayList<>();
            while (true) {
                skipEOLs();
                Tokenizer.Token t = peek();
                if (t.type() == Tokenizer.TT.EOF) break;
                if (t.type() == Tokenizer.TT.RBRACE) { if (insideBlock) break; consume(); continue; }

                if (t.type() == Tokenizer.TT.IDENT) {
                    switch (t.text()) {
                        case "if"       -> { out.add(parseIfChain()); continue; }
                        case "for"      -> { Statement fs = parseForLoop();   if (fs != null) out.add(fs); continue; }
                        case "while"    -> { Statement ws = parseWhileLoop(); if (ws != null) out.add(ws); continue; }
                        case "break"    -> { consume(); out.add(new Statement.BreakStatement()); continue; }
                        case "continue" -> { consume(); out.add(new Statement.ContinueStatement()); continue; }
                        case "return"   -> {
                            int afterReturn = t.pos() + t.text().length();
                            consume();
                            tok.reset(afterReturn);
                            String retExpr = tok.readExprToEOL();
                            lookahead = tok.next();
                            out.add(new Statement.ReturnStatement(retExpr));
                            continue;
                        }
                        case "def" -> {
                            Statement fd = parseFunctionDef();
                            if (fd != null) out.add(fd);
                            continue;
                        }
                    }

                    String name = t.text();
                    consume();
                    Tokenizer.Token op = peek();

                    if (op.type() == Tokenizer.TT.PLUSPLUS)     { consume(); out.add(compile(name, name + " + 1")); skipLine(); continue; }
                    if (op.type() == Tokenizer.TT.MINUSMINUS)   { consume(); out.add(compile(name, name + " - 1")); skipLine(); continue; }
                    if (op.type() == Tokenizer.TT.PLUS_ASSIGN)  { int ps = op.pos()+2; consume(); tok.reset(ps); out.add(compile(name, name + " + (" + tok.readExprToEOL() + ")")); lookahead = tok.next(); continue; }
                    if (op.type() == Tokenizer.TT.MINUS_ASSIGN) { int ps = op.pos()+2; consume(); tok.reset(ps); out.add(compile(name, name + " - (" + tok.readExprToEOL() + ")")); lookahead = tok.next(); continue; }
                    if (op.type() == Tokenizer.TT.STAR_ASSIGN)  { int ps = op.pos()+2; consume(); tok.reset(ps); out.add(compile(name, name + " * (" + tok.readExprToEOL() + ")")); lookahead = tok.next(); continue; }
                    if (op.type() == Tokenizer.TT.SLASH_ASSIGN) { int ps = op.pos()+2; consume(); tok.reset(ps); out.add(compile(name, name + " / (" + tok.readExprToEOL() + ")")); lookahead = tok.next(); continue; }
                    if (op.type() == Tokenizer.TT.PERCENT_ASSIGN) { int ps = op.pos()+2; consume(); tok.reset(ps); out.add(compile(name, name + " % (" + tok.readExprToEOL() + ")")); lookahead = tok.next(); continue; }

                    if (op.type() != Tokenizer.TT.ASSIGN) {
                        // No '=' — treat as expression statement (e.g. Machine.set_flag(...), entity.remove())
                        tok.reset(t.pos()); // reset to start of the IDENT name
                        String callExpr = tok.readExprToEOL();
                        lookahead = tok.next();
                        if (!callExpr.isEmpty()) {
                            try {
                                out.add(new Statement.ExprStatement(ScriptFormula.compile(callExpr)));
                            } catch (Throwable ignored) {}
                        }
                        continue;
                    }
                    int exprStart = op.pos() + 1; // right after '='
                    consume(); // consume "="
                    tok.reset(exprStart); // reset to right after '=' — includes '(', '.', etc.
                    String expr = tok.readExprToEOL();
                    if (!expr.isEmpty()) { Statement s = compile(name, expr); if (s != null) out.add(s); }
                    lookahead = tok.next();
                    continue;
                }
                consume();
            }
            return out;
        }

        private void skipLine() { while (peek().type() != Tokenizer.TT.EOL && peek().type() != Tokenizer.TT.EOF) consume(); }

        Statement parseForLoop() {
            consume();
            skipEOLs();
            int resetPos = lookahead.pos();
            tok.reset(resetPos);
            String forHeader = tok.readExprToBrace();
            lookahead = tok.next();
            skipEOLs();
            if (peek().type() == Tokenizer.TT.LBRACE) consume();
            List<Statement> body = parseBlock(true);
            skipEOLs();
            if (peek().type() == Tokenizer.TT.RBRACE) consume();
            int inIdx = forHeader.indexOf(" in ");
            if (inIdx < 0) { log.warning("[CEPolyfills] 'for' missing 'in' in " + scriptName + ".pf"); return null; }
            String var = forHeader.substring(0, inIdx).trim();
            String iterExpr = forHeader.substring(inIdx + 4).trim();
            if (var.isEmpty() || iterExpr.isEmpty()) { log.warning("[CEPolyfills] Bad 'for' header in " + scriptName + ".pf"); return null; }
            return new Statement.ForStatement(var, iterExpr, body);
        }

        Statement parseWhileLoop() {
            consume();
            skipEOLs();
            tok.reset(lookahead.pos());
            String condExpr = tok.readExprToBrace();
            lookahead = tok.next();
            skipEOLs();
            if (peek().type() == Tokenizer.TT.LBRACE) consume();
            List<Statement> body = parseBlock(true);
            skipEOLs();
            if (peek().type() == Tokenizer.TT.RBRACE) consume();
            if (condExpr.isEmpty()) { log.warning("[CEPolyfills] Empty 'while' condition in " + scriptName + ".pf"); return null; }
            return new Statement.WhileStatement(condExpr, body, 1000);
        }

        Statement.IfChain parseIfChain() {
            List<Clause> clauses = new ArrayList<>();
            while (true) {
                skipEOLs();
                Tokenizer.Token t = peek();
                if (t.type() == Tokenizer.TT.IDENT && t.text().equals("if")) {
                    int afterIf = t.pos() + t.text().length(); // right after "if"
                    consume();
                    tok.reset(afterIf);
                    String condExpr = tok.readExprToBrace();
                    lookahead = tok.next();
                    skipEOLs();
                    if (peek().type() == Tokenizer.TT.LBRACE) consume();
                    clauses.add(new Clause(compileFormula(condExpr), parseBlock(true)));
                } else if (t.type() == Tokenizer.TT.RBRACE) {
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
                            String condExpr = tok.readExprToBrace();
                            lookahead = tok.next();
                            skipEOLs();
                            if (peek().type() == Tokenizer.TT.LBRACE) consume();
                            clauses.add(new Clause(compileFormula(condExpr), parseBlock(true)));
                        } else {
                            if (peek().type() == Tokenizer.TT.LBRACE) consume();
                            clauses.add(new Clause(null, parseBlock(true)));
                            skipEOLs();
                            if (peek().type() == Tokenizer.TT.RBRACE) consume();
                            break;
                        }
                    } else { break; }
                } else { break; }
            }
            return new Statement.IfChain(clauses);
        }

        /**
         * Parse: def name(param1, param2, ...) { body }
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
            // sees depth=0→1 (from '(') and correctly stops at the matching '{'.
            int openParen = tok.findCharBefore('(', lookahead.pos() - 1);
            tok.reset(openParen >= 0 ? openParen : lookahead.pos());
            String rawParams = tok.readExprToBrace(); // reads "(params)" up to '{'
            lookahead = tok.next();
            skipEOLs();
            if (peek().type() == Tokenizer.TT.LBRACE) consume();
            List<Statement> body = parseBlock(true);
            skipEOLs();
            if (peek().type() == Tokenizer.TT.RBRACE) consume();
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
