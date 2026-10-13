package org.mozilla.javascript;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.ast.ArrayComprehension;
import org.mozilla.javascript.ast.ArrayComprehensionLoop;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DestructuringForm;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.EmptyExpression;
import org.mozilla.javascript.ast.EmptyStatement;
import org.mozilla.javascript.ast.ErrorNode;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.GeneratorExpression;
import org.mozilla.javascript.ast.GeneratorExpressionLoop;
import org.mozilla.javascript.ast.IdeErrorReporter;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Label;
import org.mozilla.javascript.ast.LabeledStatement;
import org.mozilla.javascript.ast.LetNode;
import org.mozilla.javascript.ast.Loop;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.RegExpLiteral;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;
import org.mozilla.javascript.ast.XmlDotQuery;
import org.mozilla.javascript.ast.XmlElemRef;
import org.mozilla.javascript.ast.XmlExpression;
import org.mozilla.javascript.ast.XmlLiteral;
import org.mozilla.javascript.ast.XmlMemberGet;
import org.mozilla.javascript.ast.XmlPropRef;
import org.mozilla.javascript.ast.XmlRef;
import org.mozilla.javascript.ast.XmlString;
import org.mozilla.javascript.ast.Yield;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Parser.class */
public class Parser {
    public static final int ARGC_LIMIT = 65536;
    static final int CLEAR_TI_MASK = 65535;
    static final int TI_AFTER_EOL = 65536;
    static final int TI_CHECK_LABEL = 131072;
    CompilerEnvirons compilerEnv;
    private ErrorReporter errorReporter;
    private IdeErrorReporter errorCollector;
    private String sourceURI;
    private char[] sourceChars;
    boolean calledByCompileFunction;
    private boolean parseFinished;
    private TokenStream ts;
    private int currentFlaggedToken;
    private int currentToken;
    private int syntaxErrorCount;
    private List<Comment> scannedComments;
    private Comment currentJsDocComment;
    protected int nestingOfFunction;
    private LabeledStatement currentLabel;
    private boolean inDestructuringAssignment;
    protected boolean inUseStrictDirective;
    ScriptNode currentScriptOrFn;
    Scope currentScope;
    private int endFlags;
    private boolean inForInit;
    private Map<String, LabeledStatement> labelSet;
    private List<Loop> loopSet;
    private List<Jump> loopAndSwitchSet;
    private int prevNameTokenStart;
    private String prevNameTokenString;
    private int prevNameTokenLineno;
    private boolean defaultUseStrictDirective;
    private static final int PROP_ENTRY = 1;
    private static final int GET_ENTRY = 2;
    private static final int SET_ENTRY = 4;
    private static final int METHOD_ENTRY = 8;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Parser$ParserException.class */
    private static class ParserException extends RuntimeException {
        private static final long serialVersionUID = 5882582646773765630L;

        private ParserException() {
        }
    }

    public Parser() {
        this(new CompilerEnvirons());
    }

    public Parser(CompilerEnvirons compilerEnv) {
        this(compilerEnv, compilerEnv.getErrorReporter());
    }

    public Parser(CompilerEnvirons compilerEnv, ErrorReporter errorReporter) {
        this.currentFlaggedToken = 0;
        this.prevNameTokenString = "";
        this.compilerEnv = compilerEnv;
        this.errorReporter = errorReporter;
        if (errorReporter instanceof IdeErrorReporter) {
            this.errorCollector = (IdeErrorReporter) errorReporter;
        }
    }

    void addStrictWarning(String messageId, String messageArg) {
        int beg = -1;
        int end = -1;
        if (this.ts != null) {
            beg = this.ts.tokenBeg;
            end = this.ts.tokenEnd - this.ts.tokenBeg;
        }
        addStrictWarning(messageId, messageArg, beg, end);
    }

    void addStrictWarning(String messageId, String messageArg, int position, int length) {
        if (this.compilerEnv.isStrictMode()) {
            addWarning(messageId, messageArg, position, length);
        }
    }

    void addWarning(String messageId, String messageArg) {
        int beg = -1;
        int end = -1;
        if (this.ts != null) {
            beg = this.ts.tokenBeg;
            end = this.ts.tokenEnd - this.ts.tokenBeg;
        }
        addWarning(messageId, messageArg, beg, end);
    }

    void addWarning(String messageId, int position, int length) {
        addWarning(messageId, null, position, length);
    }

    void addWarning(String messageId, String messageArg, int position, int length) {
        String message = lookupMessage(messageId, messageArg);
        if (this.compilerEnv.reportWarningAsError()) {
            addError(messageId, messageArg, position, length);
        } else if (this.errorCollector != null) {
            this.errorCollector.warning(message, this.sourceURI, position, length);
        } else {
            this.errorReporter.warning(message, this.sourceURI, this.ts.getLineno(), this.ts.getLine(), this.ts.getOffset());
        }
    }

    void addError(String messageId) {
        addError(messageId, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
    }

    void addError(String messageId, int position, int length) {
        addError(messageId, null, position, length);
    }

    void addError(String messageId, String messageArg) {
        addError(messageId, messageArg, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
    }

    void addError(String messageId, int c) {
        String messageArg = Character.toString((char) c);
        addError(messageId, messageArg, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
    }

    void addError(String messageId, String messageArg, int position, int length) {
        this.syntaxErrorCount++;
        String message = lookupMessage(messageId, messageArg);
        if (this.errorCollector != null) {
            this.errorCollector.error(message, this.sourceURI, position, length);
            return;
        }
        int lineno = 1;
        int offset = 1;
        String line = "";
        if (this.ts != null) {
            lineno = this.ts.getLineno();
            line = this.ts.getLine();
            offset = this.ts.getOffset();
        }
        this.errorReporter.error(message, this.sourceURI, lineno, line, offset);
    }

    private void addStrictWarning(String messageId, String messageArg, int position, int length, int line, String lineSource, int lineOffset) {
        if (this.compilerEnv.isStrictMode()) {
            addWarning(messageId, messageArg, position, length, line, lineSource, lineOffset);
        }
    }

    private void addWarning(String messageId, String messageArg, int position, int length, int line, String lineSource, int lineOffset) {
        String message = lookupMessage(messageId, messageArg);
        if (this.compilerEnv.reportWarningAsError()) {
            addError(messageId, messageArg, position, length, line, lineSource, lineOffset);
        } else if (this.errorCollector != null) {
            this.errorCollector.warning(message, this.sourceURI, position, length);
        } else {
            this.errorReporter.warning(message, this.sourceURI, line, lineSource, lineOffset);
        }
    }

    private void addError(String messageId, String messageArg, int position, int length, int line, String lineSource, int lineOffset) {
        this.syntaxErrorCount++;
        String message = lookupMessage(messageId, messageArg);
        if (this.errorCollector != null) {
            this.errorCollector.error(message, this.sourceURI, position, length);
        } else {
            this.errorReporter.error(message, this.sourceURI, line, lineSource, lineOffset);
        }
    }

    String lookupMessage(String messageId) {
        return lookupMessage(messageId, null);
    }

    String lookupMessage(String messageId, String messageArg) {
        if (messageArg == null) {
            return ScriptRuntime.getMessage0(messageId);
        }
        return ScriptRuntime.getMessage1(messageId, messageArg);
    }

    void reportError(String messageId) {
        reportError(messageId, null);
    }

    void reportError(String messageId, String messageArg) {
        if (this.ts == null) {
            reportError(messageId, messageArg, 1, 1);
        } else {
            reportError(messageId, messageArg, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
        }
    }

    void reportError(String messageId, int position, int length) {
        reportError(messageId, null, position, length);
    }

    void reportError(String messageId, String messageArg, int position, int length) {
        addError(messageId, messageArg, position, length);
        if (!this.compilerEnv.recoverFromErrors()) {
            throw new ParserException();
        }
    }

    private static int getNodeEnd(AstNode n) {
        return n.getPosition() + n.getLength();
    }

    private void recordComment(int lineno, String comment) {
        if (this.scannedComments == null) {
            this.scannedComments = new ArrayList();
        }
        Comment commentNode = new Comment(this.ts.tokenBeg, this.ts.getTokenLength(), this.ts.commentType, comment);
        if (this.ts.commentType == Token.CommentType.JSDOC && this.compilerEnv.isRecordingLocalJsDocComments()) {
            Comment jsDocCommentNode = new Comment(this.ts.tokenBeg, this.ts.getTokenLength(), this.ts.commentType, comment);
            this.currentJsDocComment = jsDocCommentNode;
            this.currentJsDocComment.setLineno(lineno);
        }
        commentNode.setLineno(lineno);
        this.scannedComments.add(commentNode);
    }

    private Comment getAndResetJsDoc() {
        Comment saved = this.currentJsDocComment;
        this.currentJsDocComment = null;
        return saved;
    }

    private int peekToken() throws IOException {
        if (this.currentFlaggedToken != 0) {
            return this.currentToken;
        }
        int lineno = this.ts.getLineno();
        int tt = this.ts.getToken();
        boolean sawEOL = false;
        while (true) {
            if (tt != 1 && tt != 162) {
                break;
            }
            if (tt == 1) {
                lineno++;
                sawEOL = true;
                tt = this.ts.getToken();
            } else {
                if (this.compilerEnv.isRecordingComments()) {
                    String comment = this.ts.getAndResetCurrentComment();
                    recordComment(lineno, comment);
                    break;
                }
                tt = this.ts.getToken();
            }
        }
        this.currentToken = tt;
        this.currentFlaggedToken = tt | (sawEOL ? 65536 : 0);
        return this.currentToken;
    }

    private int peekFlaggedToken() throws IOException {
        peekToken();
        return this.currentFlaggedToken;
    }

    private void consumeToken() {
        this.currentFlaggedToken = 0;
    }

    private int nextToken() throws IOException {
        int tt = peekToken();
        consumeToken();
        return tt;
    }

    private boolean matchToken(int toMatch, boolean ignoreComment) throws IOException {
        int tt;
        int iPeekToken = peekToken();
        while (true) {
            tt = iPeekToken;
            if (tt != 162 || !ignoreComment) {
                break;
            }
            consumeToken();
            iPeekToken = peekToken();
        }
        if (tt != toMatch) {
            return false;
        }
        consumeToken();
        return true;
    }

    private int peekTokenOrEOL() throws IOException {
        int tt = peekToken();
        if ((this.currentFlaggedToken & 65536) != 0) {
            tt = 1;
        }
        return tt;
    }

    private boolean mustMatchToken(int toMatch, String messageId, boolean ignoreComment) throws IOException {
        return mustMatchToken(toMatch, messageId, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg, ignoreComment);
    }

    private boolean mustMatchToken(int toMatch, String msgId, int pos, int len, boolean ignoreComment) throws IOException {
        if (matchToken(toMatch, ignoreComment)) {
            return true;
        }
        reportError(msgId, pos, len);
        return false;
    }

    private void mustHaveXML() {
        if (!this.compilerEnv.isXmlAvailable()) {
            reportError("msg.XML.not.available");
        }
    }

    public boolean eof() {
        return this.ts.eof();
    }

    boolean insideFunction() {
        return this.nestingOfFunction != 0;
    }

    void pushScope(Scope scope) {
        Scope parent = scope.getParentScope();
        if (parent != null) {
            if (parent != this.currentScope) {
                codeBug();
            }
        } else {
            this.currentScope.addChildScope(scope);
        }
        this.currentScope = scope;
    }

    void popScope() {
        this.currentScope = this.currentScope.getParentScope();
    }

    private void enterLoop(Loop loop) {
        if (this.loopSet == null) {
            this.loopSet = new ArrayList();
        }
        this.loopSet.add(loop);
        if (this.loopAndSwitchSet == null) {
            this.loopAndSwitchSet = new ArrayList();
        }
        this.loopAndSwitchSet.add(loop);
        pushScope(loop);
        if (this.currentLabel != null) {
            this.currentLabel.setStatement(loop);
            this.currentLabel.getFirstLabel().setLoop(loop);
            loop.setRelative(-this.currentLabel.getPosition());
        }
    }

    private void exitLoop() {
        Loop loop = this.loopSet.remove(this.loopSet.size() - 1);
        this.loopAndSwitchSet.remove(this.loopAndSwitchSet.size() - 1);
        if (loop.getParent() != null) {
            loop.setRelative(loop.getParent().getPosition());
        }
        popScope();
    }

    private void enterSwitch(SwitchStatement node) {
        if (this.loopAndSwitchSet == null) {
            this.loopAndSwitchSet = new ArrayList();
        }
        this.loopAndSwitchSet.add(node);
    }

    private void exitSwitch() {
        this.loopAndSwitchSet.remove(this.loopAndSwitchSet.size() - 1);
    }

    public AstRoot parse(String sourceString, String sourceURI, int lineno) {
        if (this.parseFinished) {
            throw new IllegalStateException("parser reused");
        }
        this.sourceURI = sourceURI;
        if (this.compilerEnv.isIdeMode()) {
            this.sourceChars = sourceString.toCharArray();
        }
        this.ts = new TokenStream(this, null, sourceString, lineno);
        try {
            try {
                AstRoot astRoot = parse();
                this.parseFinished = true;
                return astRoot;
            } catch (IOException e) {
                throw new IllegalStateException();
            }
        } catch (Throwable th) {
            this.parseFinished = true;
            throw th;
        }
    }

    @Deprecated
    public AstRoot parse(Reader sourceReader, String sourceURI, int lineno) throws IOException {
        if (this.parseFinished) {
            throw new IllegalStateException("parser reused");
        }
        if (this.compilerEnv.isIdeMode()) {
            return parse(Kit.readReader(sourceReader), sourceURI, lineno);
        }
        try {
            this.sourceURI = sourceURI;
            this.ts = new TokenStream(this, sourceReader, null, lineno);
            return parse();
        } finally {
            this.parseFinished = true;
        }
    }

    /* JADX WARN: Code duplicated, block: B:45:0x012a  */
    /* JADX WARN: Code duplicated, block: B:47:0x0147  */
    /* JADX WARN: Code duplicated, block: B:51:0x0161  */
    /* JADX WARN: Code duplicated, block: B:54:0x019b A[LOOP:1: B:52:0x0191->B:54:0x019b, LOOP_END] */
    private AstRoot parse() throws IOException {
        String msg;
        AstNode n;
        AstRoot root = new AstRoot(0);
        this.currentScriptOrFn = root;
        this.currentScope = root;
        int baseLineno = this.ts.lineno;
        int end = 0;
        boolean inDirectivePrologue = true;
        boolean savedStrictMode = this.inUseStrictDirective;
        this.inUseStrictDirective = this.defaultUseStrictDirective;
        if (this.inUseStrictDirective) {
            root.setInStrictMode(true);
        }
        while (true) {
            try {
                try {
                    int tt = peekToken();
                    if (tt <= 0) {
                        break;
                    }
                    if (tt == 110) {
                        consumeToken();
                        try {
                            n = function(this.calledByCompileFunction ? 2 : 1);
                        } catch (ParserException e) {
                        }
                    } else if (tt == 162) {
                        n = this.scannedComments.get(this.scannedComments.size() - 1);
                        consumeToken();
                    } else {
                        n = statement();
                        if (inDirectivePrologue) {
                            String directive = getDirective(n);
                            if (directive == null) {
                                inDirectivePrologue = false;
                            } else if (directive.equals("use strict")) {
                                this.inUseStrictDirective = true;
                                root.setInStrictMode(true);
                            }
                        }
                    }
                    end = getNodeEnd(n);
                    root.addChildToBack(n);
                    n.setParent(root);
                    this.inUseStrictDirective = savedStrictMode;
                } catch (StackOverflowError e2) {
                    String msg2 = lookupMessage("msg.too.deep.parser.recursion");
                    if (!this.compilerEnv.isIdeMode()) {
                        throw Context.reportRuntimeError(msg2, this.sourceURI, this.ts.lineno, null, 0);
                    }
                    this.inUseStrictDirective = savedStrictMode;
                }
                if (this.syntaxErrorCount != 0) {
                    String msg3 = String.valueOf(this.syntaxErrorCount);
                    msg = lookupMessage("msg.got.syntax.errors", msg3);
                    if (!this.compilerEnv.isIdeMode()) {
                        throw this.errorReporter.runtimeError(msg, this.sourceURI, baseLineno, null, 0);
                    }
                }
                if (this.scannedComments != null) {
                    int last = this.scannedComments.size() - 1;
                    end = Math.max(end, getNodeEnd(this.scannedComments.get(last)));
                    for (Comment c : this.scannedComments) {
                        root.addComment(c);
                    }
                }
                root.setLength(end - 0);
                root.setSourceName(this.sourceURI);
                root.setBaseLineno(baseLineno);
                root.setEndLineno(this.ts.lineno);
                return root;
            } catch (Throwable th) {
                this.inUseStrictDirective = savedStrictMode;
                throw th;
            }
        }
        this.inUseStrictDirective = savedStrictMode;
        if (this.syntaxErrorCount != 0) {
            String msg4 = String.valueOf(this.syntaxErrorCount);
            msg = lookupMessage("msg.got.syntax.errors", msg4);
            if (!this.compilerEnv.isIdeMode()) {
                throw this.errorReporter.runtimeError(msg, this.sourceURI, baseLineno, null, 0);
            }
        }
        if (this.scannedComments != null) {
            int last2 = this.scannedComments.size() - 1;
            end = Math.max(end, getNodeEnd(this.scannedComments.get(last2)));
            while (r0.hasNext()) {
                root.addComment(c);
            }
        }
        root.setLength(end - 0);
        root.setSourceName(this.sourceURI);
        root.setBaseLineno(baseLineno);
        root.setEndLineno(this.ts.lineno);
        return root;
    }

    private AstNode parseFunctionBody(int type, FunctionNode fnNode) throws IOException {
        AstNode n;
        boolean isExpressionClosure = false;
        if (!matchToken(86, true)) {
            if (this.compilerEnv.getLanguageVersion() < 180 && type != 4) {
                reportError("msg.no.brace.body");
            } else {
                isExpressionClosure = true;
            }
        }
        boolean isArrow = type == 4;
        this.nestingOfFunction++;
        int pos = this.ts.tokenBeg;
        Block pn = new Block(pos);
        boolean inDirectivePrologue = true;
        boolean savedStrictMode = this.inUseStrictDirective;
        this.inUseStrictDirective = false;
        pn.setLineno(this.ts.lineno);
        try {
            if (!isExpressionClosure) {
                while (true) {
                    int tt = peekToken();
                    switch (tt) {
                        case -1:
                        case 0:
                        case 87:
                            break;
                        case 110:
                            consumeToken();
                            n = function(1);
                            continue;
                            pn.addStatement(n);
                            break;
                        case 162:
                            consumeToken();
                            n = this.scannedComments.get(this.scannedComments.size() - 1);
                            continue;
                            pn.addStatement(n);
                            break;
                        default:
                            n = statement();
                            if (inDirectivePrologue) {
                                String directive = getDirective(n);
                                if (directive == null) {
                                    inDirectivePrologue = false;
                                } else if (directive.equals("use strict")) {
                                    this.inUseStrictDirective = true;
                                    fnNode.setInStrictMode(true);
                                    if (!savedStrictMode) {
                                        setRequiresActivation();
                                        continue;
                                    }
                                }
                            }
                            pn.addStatement(n);
                            break;
                    }
                }
            } else {
                AstNode returnValue = assignExpr();
                ReturnStatement n2 = new ReturnStatement(returnValue.getPosition(), returnValue.getLength(), returnValue);
                n2.putProp(25, Boolean.TRUE);
                pn.putProp(25, Boolean.TRUE);
                if (isArrow) {
                    n2.putProp(27, Boolean.TRUE);
                }
                pn.addStatement(n2);
            }
            this.nestingOfFunction--;
            this.inUseStrictDirective = savedStrictMode;
        } catch (ParserException e) {
            this.nestingOfFunction--;
            this.inUseStrictDirective = savedStrictMode;
        } catch (Throwable th) {
            this.nestingOfFunction--;
            this.inUseStrictDirective = savedStrictMode;
            throw th;
        }
        int end = this.ts.tokenEnd;
        getAndResetJsDoc();
        if (!isExpressionClosure && mustMatchToken(87, "msg.no.brace.after.body", true)) {
            end = this.ts.tokenEnd;
        }
        pn.setLength(end - pos);
        return pn;
    }

    private static String getDirective(AstNode n) {
        if (n instanceof ExpressionStatement) {
            AstNode e = ((ExpressionStatement) n).getExpression();
            if (e instanceof StringLiteral) {
                return ((StringLiteral) e).getValue();
            }
            return null;
        }
        return null;
    }

    private void parseFunctionParams(FunctionNode fnNode) throws IOException {
        if (matchToken(89, true)) {
            fnNode.setRp(this.ts.tokenBeg - fnNode.getPosition());
            return;
        }
        Map<String, Node> destructuring = null;
        Set<String> paramNames = new HashSet<>();
        do {
            int tt = peekToken();
            if (tt == 84 || tt == 86) {
                AstNode expr = destructuringPrimaryExpr();
                markDestructuring(expr);
                fnNode.addParam(expr);
                if (destructuring == null) {
                    destructuring = new HashMap<>();
                }
                String pname = this.currentScriptOrFn.getNextTempName();
                defineSymbol(88, pname, false);
                destructuring.put(pname, expr);
            } else if (mustMatchToken(39, "msg.no.parm", true)) {
                Name paramNameNode = createNameNode();
                Comment jsdocNodeForName = getAndResetJsDoc();
                if (jsdocNodeForName != null) {
                    paramNameNode.setJsDocNode(jsdocNodeForName);
                }
                fnNode.addParam(paramNameNode);
                String paramName = this.ts.getString();
                defineSymbol(88, paramName);
                if (this.inUseStrictDirective) {
                    if ("eval".equals(paramName) || "arguments".equals(paramName)) {
                        reportError("msg.bad.id.strict", paramName);
                    }
                    if (paramNames.contains(paramName)) {
                        addError("msg.dup.param.strict", paramName);
                    }
                    paramNames.add(paramName);
                }
            } else {
                fnNode.addParam(makeErrorNode());
            }
        } while (matchToken(90, true));
        if (destructuring != null) {
            Node destructuringNode = new Node(90);
            for (Map.Entry<String, Node> param : destructuring.entrySet()) {
                Node assign = createDestructuringAssignment(123, param.getValue(), createName(param.getKey()));
                destructuringNode.addChildToBack(assign);
            }
            fnNode.putProp(23, destructuringNode);
        }
        if (mustMatchToken(89, "msg.no.paren.after.parms", true)) {
            fnNode.setRp(this.ts.tokenBeg - fnNode.getPosition());
        }
    }

    private FunctionNode function(int type) throws IOException {
        return function(type, false);
    }

    private FunctionNode function(int type, boolean isGenerator) throws IOException {
        int syntheticType = type;
        int baseLineno = this.ts.lineno;
        int functionSourceStart = this.ts.tokenBeg;
        Name name = null;
        AstNode memberExprNode = null;
        if (matchToken(39, true)) {
            name = createNameNode(true, 39);
            if (this.inUseStrictDirective) {
                String id = name.getIdentifier();
                if ("eval".equals(id) || "arguments".equals(id)) {
                    reportError("msg.bad.id.strict", id);
                }
            }
            if (!matchToken(88, true)) {
                if (this.compilerEnv.isAllowMemberExprAsFunctionName()) {
                    name = null;
                    memberExprNode = memberExprTail(false, name);
                }
                mustMatchToken(88, "msg.no.paren.parms", true);
            }
        } else if (!matchToken(88, true)) {
            if (matchToken(23, true) && this.compilerEnv.getLanguageVersion() >= 200) {
                return function(type, true);
            }
            if (this.compilerEnv.isAllowMemberExprAsFunctionName()) {
                memberExprNode = memberExpr(false);
            }
            mustMatchToken(88, "msg.no.paren.parms", true);
        }
        int lpPos = this.currentToken == 88 ? this.ts.tokenBeg : -1;
        if (memberExprNode != null) {
            syntheticType = 2;
        }
        if (syntheticType != 2 && name != null && name.length() > 0) {
            defineSymbol(110, name.getIdentifier());
        }
        FunctionNode fnNode = new FunctionNode(functionSourceStart, name);
        fnNode.setFunctionType(type);
        if (isGenerator) {
            fnNode.setIsES6Generator();
        }
        if (lpPos != -1) {
            fnNode.setLp(lpPos - functionSourceStart);
        }
        fnNode.setJsDocNode(getAndResetJsDoc());
        PerFunctionVariables savedVars = new PerFunctionVariables(fnNode);
        try {
            parseFunctionParams(fnNode);
            fnNode.setBody(parseFunctionBody(type, fnNode));
            fnNode.setEncodedSourceBounds(functionSourceStart, this.ts.tokenEnd);
            fnNode.setLength(this.ts.tokenEnd - functionSourceStart);
            if (this.compilerEnv.isStrictMode() && !fnNode.getBody().hasConsistentReturnUsage()) {
                String msg = (name == null || name.length() <= 0) ? "msg.anon.no.return.value" : "msg.no.return.value";
                addStrictWarning(msg, name == null ? "" : name.getIdentifier());
            }
            savedVars.restore();
            if (memberExprNode != null) {
                Kit.codeBug();
                fnNode.setMemberExprNode(memberExprNode);
            }
            fnNode.setSourceName(this.sourceURI);
            fnNode.setBaseLineno(baseLineno);
            fnNode.setEndLineno(this.ts.lineno);
            if (this.compilerEnv.isIdeMode()) {
                fnNode.setParentScope(this.currentScope);
            }
            return fnNode;
        } catch (Throwable th) {
            savedVars.restore();
            throw th;
        }
    }

    private AstNode arrowFunction(AstNode params) throws IOException {
        int baseLineno = this.ts.lineno;
        int functionSourceStart = params != null ? params.getPosition() : -1;
        FunctionNode fnNode = new FunctionNode(functionSourceStart);
        fnNode.setFunctionType(4);
        fnNode.setJsDocNode(getAndResetJsDoc());
        Map<String, Node> destructuring = new HashMap<>();
        Set<String> paramNames = new HashSet<>();
        PerFunctionVariables savedVars = new PerFunctionVariables(fnNode);
        try {
            if (params instanceof ParenthesizedExpression) {
                fnNode.setParens(0, params.getLength());
                AstNode p = ((ParenthesizedExpression) params).getExpression();
                if (!(p instanceof EmptyExpression)) {
                    arrowFunctionParams(fnNode, p, destructuring, paramNames);
                }
            } else {
                arrowFunctionParams(fnNode, params, destructuring, paramNames);
            }
            if (!destructuring.isEmpty()) {
                Node destructuringNode = new Node(90);
                for (Map.Entry<String, Node> param : destructuring.entrySet()) {
                    Node assign = createDestructuringAssignment(123, param.getValue(), createName(param.getKey()));
                    destructuringNode.addChildToBack(assign);
                }
                fnNode.putProp(23, destructuringNode);
            }
            fnNode.setBody(parseFunctionBody(4, fnNode));
            fnNode.setEncodedSourceBounds(functionSourceStart, this.ts.tokenEnd);
            fnNode.setLength(this.ts.tokenEnd - functionSourceStart);
            savedVars.restore();
            if (fnNode.isGenerator()) {
                reportError("msg.arrowfunction.generator");
                return makeErrorNode();
            }
            fnNode.setSourceName(this.sourceURI);
            fnNode.setBaseLineno(baseLineno);
            fnNode.setEndLineno(this.ts.lineno);
            return fnNode;
        } catch (Throwable th) {
            savedVars.restore();
            throw th;
        }
    }

    private void arrowFunctionParams(FunctionNode fnNode, AstNode params, Map<String, Node> destructuring, Set<String> paramNames) {
        if ((params instanceof ArrayLiteral) || (params instanceof ObjectLiteral)) {
            markDestructuring(params);
            fnNode.addParam(params);
            String pname = this.currentScriptOrFn.getNextTempName();
            defineSymbol(88, pname, false);
            destructuring.put(pname, params);
            return;
        }
        if ((params instanceof InfixExpression) && params.getType() == 90) {
            arrowFunctionParams(fnNode, ((InfixExpression) params).getLeft(), destructuring, paramNames);
            arrowFunctionParams(fnNode, ((InfixExpression) params).getRight(), destructuring, paramNames);
            return;
        }
        if (params instanceof Name) {
            fnNode.addParam(params);
            String paramName = ((Name) params).getIdentifier();
            defineSymbol(88, paramName);
            if (this.inUseStrictDirective) {
                if ("eval".equals(paramName) || "arguments".equals(paramName)) {
                    reportError("msg.bad.id.strict", paramName);
                }
                if (paramNames.contains(paramName)) {
                    addError("msg.dup.param.strict", paramName);
                }
                paramNames.add(paramName);
                return;
            }
            return;
        }
        reportError("msg.no.parm", params.getPosition(), params.getLength());
        fnNode.addParam(makeErrorNode());
    }

    private AstNode statements(AstNode parent) throws IOException {
        if (this.currentToken != 86 && !this.compilerEnv.isIdeMode()) {
            codeBug();
        }
        int pos = this.ts.tokenBeg;
        AstNode block = parent != null ? parent : new Block(pos);
        block.setLineno(this.ts.lineno);
        while (true) {
            int tt = peekToken();
            if (tt <= 0 || tt == 87) {
                break;
            }
            block.addChild(statement());
        }
        block.setLength(this.ts.tokenBeg - pos);
        return block;
    }

    private AstNode statements() throws IOException {
        return statements(null);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Parser$ConditionData.class */
    private static class ConditionData {
        AstNode condition;
        int lp;
        int rp;

        private ConditionData() {
            this.lp = -1;
            this.rp = -1;
        }
    }

    private ConditionData condition() throws IOException {
        ConditionData data = new ConditionData();
        if (mustMatchToken(88, "msg.no.paren.cond", true)) {
            data.lp = this.ts.tokenBeg;
        }
        data.condition = expr();
        if (mustMatchToken(89, "msg.no.paren.after.cond", true)) {
            data.rp = this.ts.tokenBeg;
        }
        if (data.condition instanceof Assignment) {
            addStrictWarning("msg.equal.as.assign", "", data.condition.getPosition(), data.condition.getLength());
        }
        return data;
    }

    private AstNode statement() throws IOException {
        int pos = this.ts.tokenBeg;
        try {
            AstNode pn = statementHelper();
            if (pn != null) {
                if (this.compilerEnv.isStrictMode() && !pn.hasSideEffects()) {
                    int beg = pn.getPosition();
                    int beg2 = Math.max(beg, lineBeginningFor(beg));
                    addStrictWarning(pn instanceof EmptyStatement ? "msg.extra.trailing.semi" : "msg.no.side.effects", "", beg2, nodeEnd(pn) - beg2);
                }
                int ntt = peekToken();
                if (ntt == 162 && pn.getLineno() == this.scannedComments.get(this.scannedComments.size() - 1).getLineno()) {
                    pn.setInlineComment(this.scannedComments.get(this.scannedComments.size() - 1));
                    consumeToken();
                }
                return pn;
            }
            while (true) {
                int tt = peekTokenOrEOL();
                consumeToken();
                switch (tt) {
                    case -1:
                    case 0:
                    case 1:
                    case 83:
                        return new EmptyStatement(pos, this.ts.tokenBeg - pos);
                    default:
                        break;
                }
            }
        } catch (ParserException e) {
        }
    }

    private AstNode statementHelper() throws IOException {
        AstNode pn;
        if (this.currentLabel != null && this.currentLabel.getStatement() != null) {
            this.currentLabel = null;
        }
        int tt = peekToken();
        int i = this.ts.tokenBeg;
        switch (tt) {
            case -1:
                consumeToken();
                return makeErrorNode();
            case 4:
            case 73:
                pn = returnOrYield(tt, false);
                break;
            case 39:
                pn = nameOrLabel();
                if (!(pn instanceof ExpressionStatement)) {
                    return pn;
                }
                break;
            case 50:
                pn = throwStatement();
                break;
            case 82:
                return tryStatement();
            case 83:
                consumeToken();
                int pos = this.ts.tokenBeg;
                AstNode pn2 = new EmptyStatement(pos, this.ts.tokenEnd - pos);
                pn2.setLineno(this.ts.lineno);
                return pn2;
            case 86:
                return block();
            case 110:
                consumeToken();
                return function(3);
            case 113:
                return ifStatement();
            case 115:
                return switchStatement();
            case 117:
                pn = defaultXmlNamespace();
                break;
            case 118:
                return whileLoop();
            case 119:
                return doLoop();
            case 120:
                return forLoop();
            case 121:
                pn = breakStatement();
                break;
            case 122:
                pn = continueStatement();
                break;
            case 123:
            case 155:
                consumeToken();
                int lineno = this.ts.lineno;
                pn = variables(this.currentToken, this.ts.tokenBeg, true);
                pn.setLineno(lineno);
                break;
            case 124:
                if (this.inUseStrictDirective) {
                    reportError("msg.no.with.strict");
                }
                return withStatement();
            case 154:
                pn = letStatement();
                if (!(pn instanceof VariableDeclaration) || peekToken() != 83) {
                    return pn;
                }
                break;
            case 161:
                consumeToken();
                pn = new KeywordLiteral(this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg, tt);
                pn.setLineno(this.ts.lineno);
                break;
            case 162:
                return this.scannedComments.get(this.scannedComments.size() - 1);
            default:
                int lineno2 = this.ts.lineno;
                pn = new ExpressionStatement(expr(), !insideFunction());
                pn.setLineno(lineno2);
                break;
        }
        autoInsertSemicolon(pn);
        return pn;
    }

    private void autoInsertSemicolon(AstNode pn) throws IOException {
        int ttFlagged = peekFlaggedToken();
        int pos = pn.getPosition();
        switch (ttFlagged & 65535) {
            case -1:
            case 0:
            case 87:
                warnMissingSemi(pos, Math.max(pos + 1, nodeEnd(pn)));
                break;
            case 83:
                consumeToken();
                pn.setLength(this.ts.tokenEnd - pos);
                break;
            default:
                if ((ttFlagged & 65536) == 0) {
                    reportError("msg.no.semi.stmt");
                } else {
                    warnMissingSemi(pos, nodeEnd(pn));
                }
                break;
        }
    }

    private IfStatement ifStatement() throws IOException {
        if (this.currentToken != 113) {
            codeBug();
        }
        consumeToken();
        int pos = this.ts.tokenBeg;
        int lineno = this.ts.lineno;
        int elsePos = -1;
        IfStatement pn = new IfStatement(pos);
        ConditionData data = condition();
        AstNode ifTrue = getNextStatementAfterInlineComments(pn);
        AstNode ifFalse = null;
        if (matchToken(114, true)) {
            int tt = peekToken();
            if (tt == 162) {
                pn.setElseKeyWordInlineComment(this.scannedComments.get(this.scannedComments.size() - 1));
                consumeToken();
            }
            elsePos = this.ts.tokenBeg - pos;
            ifFalse = statement();
        }
        int end = getNodeEnd(ifFalse != null ? ifFalse : ifTrue);
        pn.setLength(end - pos);
        pn.setCondition(data.condition);
        pn.setParens(data.lp - pos, data.rp - pos);
        pn.setThenPart(ifTrue);
        pn.setElsePart(ifFalse);
        pn.setElsePosition(elsePos);
        pn.setLineno(lineno);
        return pn;
    }

    private SwitchStatement switchStatement() throws IOException {
        int tt;
        if (this.currentToken != 115) {
            codeBug();
        }
        consumeToken();
        int pos = this.ts.tokenBeg;
        SwitchStatement pn = new SwitchStatement(pos);
        if (mustMatchToken(88, "msg.no.paren.switch", true)) {
            pn.setLp(this.ts.tokenBeg - pos);
        }
        pn.setLineno(this.ts.lineno);
        AstNode discriminant = expr();
        pn.setExpression(discriminant);
        enterSwitch(pn);
        try {
            if (mustMatchToken(89, "msg.no.paren.after.switch", true)) {
                pn.setRp(this.ts.tokenBeg - pos);
            }
            mustMatchToken(86, "msg.no.brace.switch", true);
            boolean hasDefault = false;
            while (true) {
                int tt2 = nextToken();
                int casePos = this.ts.tokenBeg;
                int caseLineno = this.ts.lineno;
                AstNode caseExpression = null;
                switch (tt2) {
                    case 87:
                        pn.setLength(this.ts.tokenEnd - pos);
                        break;
                    case 116:
                        caseExpression = expr();
                        mustMatchToken(104, "msg.no.colon.case", true);
                        SwitchCase caseNode = new SwitchCase(casePos);
                        caseNode.setExpression(caseExpression);
                        caseNode.setLength(this.ts.tokenEnd - pos);
                        caseNode.setLineno(caseLineno);
                        while (true) {
                            tt = peekToken();
                            if (tt == 87 && tt != 116 && tt != 117 && tt != 0) {
                                if (tt == 162) {
                                    Comment inlineComment = this.scannedComments.get(this.scannedComments.size() - 1);
                                    if (caseNode.getInlineComment() == null && inlineComment.getLineno() == caseNode.getLineno()) {
                                        caseNode.setInlineComment(inlineComment);
                                    } else {
                                        caseNode.addStatement(inlineComment);
                                    }
                                    consumeToken();
                                } else {
                                    AstNode nextStmt = statement();
                                    caseNode.addStatement(nextStmt);
                                }
                            }
                        }
                        pn.addCase(caseNode);
                        break;
                    case 117:
                        if (hasDefault) {
                            reportError("msg.double.switch.default");
                        }
                        hasDefault = true;
                        mustMatchToken(104, "msg.no.colon.case", true);
                        SwitchCase caseNode2 = new SwitchCase(casePos);
                        caseNode2.setExpression(caseExpression);
                        caseNode2.setLength(this.ts.tokenEnd - pos);
                        caseNode2.setLineno(caseLineno);
                        while (true) {
                            tt = peekToken();
                            if (tt == 87) {
                            }
                            pn.addCase(caseNode2);
                        }
                        break;
                    case 162:
                        AstNode n = this.scannedComments.get(this.scannedComments.size() - 1);
                        pn.addChild(n);
                        break;
                    default:
                        reportError("msg.bad.switch");
                        break;
                }
            }
            exitSwitch();
            return pn;
        } catch (Throwable th) {
            exitSwitch();
            throw th;
        }
    }

    private WhileLoop whileLoop() throws IOException {
        if (this.currentToken != 118) {
            codeBug();
        }
        consumeToken();
        int pos = this.ts.tokenBeg;
        WhileLoop pn = new WhileLoop(pos);
        pn.setLineno(this.ts.lineno);
        enterLoop(pn);
        try {
            ConditionData data = condition();
            pn.setCondition(data.condition);
            pn.setParens(data.lp - pos, data.rp - pos);
            AstNode body = getNextStatementAfterInlineComments(pn);
            pn.setLength(getNodeEnd(body) - pos);
            pn.setBody(body);
            return pn;
        } finally {
            exitLoop();
        }
    }

    private DoLoop doLoop() throws IOException {
        if (this.currentToken != 119) {
            codeBug();
        }
        consumeToken();
        int pos = this.ts.tokenBeg;
        DoLoop pn = new DoLoop(pos);
        pn.setLineno(this.ts.lineno);
        enterLoop(pn);
        try {
            AstNode body = getNextStatementAfterInlineComments(pn);
            mustMatchToken(118, "msg.no.while.do", true);
            pn.setWhilePosition(this.ts.tokenBeg - pos);
            ConditionData data = condition();
            pn.setCondition(data.condition);
            pn.setParens(data.lp - pos, data.rp - pos);
            int end = getNodeEnd(body);
            pn.setBody(body);
            exitLoop();
            if (matchToken(83, true)) {
                end = this.ts.tokenEnd;
            }
            pn.setLength(end - pos);
            return pn;
        } catch (Throwable th) {
            exitLoop();
            throw th;
        }
    }

    private int peekUntilNonComment(int tt) throws IOException {
        while (tt == 162) {
            consumeToken();
            tt = peekToken();
        }
        return tt;
    }

    private AstNode getNextStatementAfterInlineComments(AstNode pn) throws IOException {
        AstNode body = statement();
        if (162 == body.getType()) {
            body = statement();
            if (pn != null) {
                pn.setInlineComment(body);
            } else {
                body.setInlineComment(body);
            }
        }
        return body;
    }

    private Loop forLoop() throws IOException {
        AstNode cond;
        Loop pn;
        if (this.currentToken != 120) {
            codeBug();
        }
        consumeToken();
        int forPos = this.ts.tokenBeg;
        int lineno = this.ts.lineno;
        boolean isForEach = false;
        boolean isForIn = false;
        boolean isForOf = false;
        int eachPos = -1;
        int inPos = -1;
        int lp = -1;
        int rp = -1;
        AstNode incr = null;
        Scope tempScope = new Scope();
        pushScope(tempScope);
        try {
            if (matchToken(39, true)) {
                if ("each".equals(this.ts.getString())) {
                    isForEach = true;
                    eachPos = this.ts.tokenBeg - forPos;
                } else {
                    reportError("msg.no.paren.for");
                }
            }
            if (mustMatchToken(88, "msg.no.paren.for", true)) {
                lp = this.ts.tokenBeg - forPos;
            }
            int tt = peekToken();
            AstNode init = forLoopInit(tt);
            if (matchToken(52, true)) {
                isForIn = true;
                inPos = this.ts.tokenBeg - forPos;
                cond = expr();
            } else if (this.compilerEnv.getLanguageVersion() >= 200 && matchToken(39, true) && "of".equals(this.ts.getString())) {
                isForOf = true;
                inPos = this.ts.tokenBeg - forPos;
                cond = expr();
            } else {
                mustMatchToken(83, "msg.no.semi.for", true);
                if (peekToken() == 83) {
                    cond = new EmptyExpression(this.ts.tokenBeg, 1);
                    cond.setLineno(this.ts.lineno);
                } else {
                    cond = expr();
                }
                mustMatchToken(83, "msg.no.semi.for.cond", true);
                int tmpPos = this.ts.tokenEnd;
                if (peekToken() == 89) {
                    incr = new EmptyExpression(tmpPos, 1);
                    incr.setLineno(this.ts.lineno);
                } else {
                    incr = expr();
                }
            }
            if (mustMatchToken(89, "msg.no.paren.for.ctrl", true)) {
                rp = this.ts.tokenBeg - forPos;
            }
            if (isForIn || isForOf) {
                ForInLoop fis = new ForInLoop(forPos);
                if ((init instanceof VariableDeclaration) && ((VariableDeclaration) init).getVariables().size() > 1) {
                    reportError("msg.mult.index");
                }
                if (isForOf && isForEach) {
                    reportError("msg.invalid.for.each");
                }
                fis.setIterator(init);
                fis.setIteratedObject(cond);
                fis.setInPosition(inPos);
                fis.setIsForEach(isForEach);
                fis.setEachPosition(eachPos);
                fis.setIsForOf(isForOf);
                pn = fis;
            } else {
                ForLoop fl = new ForLoop(forPos);
                fl.setInitializer(init);
                fl.setCondition(cond);
                fl.setIncrement(incr);
                pn = fl;
            }
            this.currentScope.replaceWith(pn);
            popScope();
            enterLoop(pn);
            try {
                AstNode body = getNextStatementAfterInlineComments(pn);
                pn.setLength(getNodeEnd(body) - forPos);
                pn.setBody(body);
                exitLoop();
                if (this.currentScope == tempScope) {
                    popScope();
                }
                pn.setParens(lp, rp);
                pn.setLineno(lineno);
                return pn;
            } catch (Throwable th) {
                exitLoop();
                throw th;
            }
        } catch (Throwable th2) {
            if (this.currentScope == tempScope) {
                popScope();
            }
            throw th2;
        }
    }

    private AstNode forLoopInit(int tt) throws IOException {
        AstNode init;
        try {
            this.inForInit = true;
            if (tt == 83) {
                init = new EmptyExpression(this.ts.tokenBeg, 1);
                init.setLineno(this.ts.lineno);
            } else if (tt == 123 || tt == 154) {
                consumeToken();
                init = variables(tt, this.ts.tokenBeg, false);
            } else {
                init = expr();
                markDestructuring(init);
            }
            return init;
        } finally {
            this.inForInit = false;
        }
    }

    private TryStatement tryStatement() throws IOException {
        if (this.currentToken != 82) {
            codeBug();
        }
        consumeToken();
        Comment jsdocNode = getAndResetJsDoc();
        int tryPos = this.ts.tokenBeg;
        int lineno = this.ts.lineno;
        int finallyPos = -1;
        TryStatement pn = new TryStatement(tryPos);
        int lctt = peekToken();
        if (lctt == 162) {
            Comment commentNode = this.scannedComments.get(this.scannedComments.size() - 1);
            pn.setInlineComment(commentNode);
            consumeToken();
            lctt = peekToken();
        }
        if (lctt != 86) {
            reportError("msg.no.brace.try");
        }
        AstNode tryBlock = getNextStatementAfterInlineComments(pn);
        int tryEnd = getNodeEnd(tryBlock);
        List<CatchClause> clauses = null;
        boolean sawDefaultCatch = false;
        int peek = peekToken();
        if (peek == 125) {
            while (matchToken(125, true)) {
                int catchLineNum = this.ts.lineno;
                if (sawDefaultCatch) {
                    reportError("msg.catch.unreachable");
                }
                int catchPos = this.ts.tokenBeg;
                int lp = -1;
                int rp = -1;
                int guardPos = -1;
                if (mustMatchToken(88, "msg.no.paren.catch", true)) {
                    lp = this.ts.tokenBeg;
                }
                mustMatchToken(39, "msg.bad.catchcond", true);
                Name varName = createNameNode();
                Comment jsdocNodeForName = getAndResetJsDoc();
                if (jsdocNodeForName != null) {
                    varName.setJsDocNode(jsdocNodeForName);
                }
                String varNameString = varName.getIdentifier();
                if (this.inUseStrictDirective && ("eval".equals(varNameString) || "arguments".equals(varNameString))) {
                    reportError("msg.bad.id.strict", varNameString);
                }
                AstNode catchCond = null;
                if (matchToken(113, true)) {
                    guardPos = this.ts.tokenBeg;
                    catchCond = expr();
                } else {
                    sawDefaultCatch = true;
                }
                if (mustMatchToken(89, "msg.bad.catchcond", true)) {
                    rp = this.ts.tokenBeg;
                }
                mustMatchToken(86, "msg.no.brace.catchblock", true);
                Block catchBlock = (Block) statements();
                tryEnd = getNodeEnd(catchBlock);
                CatchClause catchNode = new CatchClause(catchPos);
                catchNode.setVarName(varName);
                catchNode.setCatchCondition(catchCond);
                catchNode.setBody(catchBlock);
                if (guardPos != -1) {
                    catchNode.setIfPosition(guardPos - catchPos);
                }
                catchNode.setParens(lp, rp);
                catchNode.setLineno(catchLineNum);
                if (mustMatchToken(87, "msg.no.brace.after.body", true)) {
                    tryEnd = this.ts.tokenEnd;
                }
                catchNode.setLength(tryEnd - catchPos);
                if (clauses == null) {
                    clauses = new ArrayList<>();
                }
                clauses.add(catchNode);
            }
        } else if (peek != 126) {
            mustMatchToken(126, "msg.try.no.catchfinally", true);
        }
        AstNode finallyBlock = null;
        if (matchToken(126, true)) {
            finallyPos = this.ts.tokenBeg;
            finallyBlock = statement();
            tryEnd = getNodeEnd(finallyBlock);
        }
        pn.setLength(tryEnd - tryPos);
        pn.setTryBlock(tryBlock);
        pn.setCatchClauses(clauses);
        pn.setFinallyBlock(finallyBlock);
        if (finallyPos != -1) {
            pn.setFinallyPosition(finallyPos - tryPos);
        }
        pn.setLineno(lineno);
        if (jsdocNode != null) {
            pn.setJsDocNode(jsdocNode);
        }
        return pn;
    }

    private ThrowStatement throwStatement() throws IOException {
        if (this.currentToken != 50) {
            codeBug();
        }
        consumeToken();
        int pos = this.ts.tokenBeg;
        int lineno = this.ts.lineno;
        if (peekTokenOrEOL() == 1) {
            reportError("msg.bad.throw.eol");
        }
        AstNode expr = expr();
        ThrowStatement pn = new ThrowStatement(pos, expr);
        pn.setLineno(lineno);
        return pn;
    }

    private LabeledStatement matchJumpLabelName() throws IOException {
        LabeledStatement label = null;
        if (peekTokenOrEOL() == 39) {
            consumeToken();
            if (this.labelSet != null) {
                label = this.labelSet.get(this.ts.getString());
            }
            if (label == null) {
                reportError("msg.undef.label");
            }
        }
        return label;
    }

    private BreakStatement breakStatement() throws IOException {
        if (this.currentToken != 121) {
            codeBug();
        }
        consumeToken();
        int lineno = this.ts.lineno;
        int pos = this.ts.tokenBeg;
        int end = this.ts.tokenEnd;
        Name breakLabel = null;
        if (peekTokenOrEOL() == 39) {
            breakLabel = createNameNode();
            end = getNodeEnd(breakLabel);
        }
        LabeledStatement labels = matchJumpLabelName();
        Jump breakTarget = labels == null ? null : labels.getFirstLabel();
        if (breakTarget == null && breakLabel == null) {
            if (this.loopAndSwitchSet == null || this.loopAndSwitchSet.size() == 0) {
                reportError("msg.bad.break", pos, end - pos);
            } else {
                breakTarget = this.loopAndSwitchSet.get(this.loopAndSwitchSet.size() - 1);
            }
        }
        BreakStatement pn = new BreakStatement(pos, end - pos);
        pn.setBreakLabel(breakLabel);
        if (breakTarget != null) {
            pn.setBreakTarget(breakTarget);
        }
        pn.setLineno(lineno);
        return pn;
    }

    private ContinueStatement continueStatement() throws IOException {
        if (this.currentToken != 122) {
            codeBug();
        }
        consumeToken();
        int lineno = this.ts.lineno;
        int pos = this.ts.tokenBeg;
        int end = this.ts.tokenEnd;
        Name label = null;
        if (peekTokenOrEOL() == 39) {
            label = createNameNode();
            end = getNodeEnd(label);
        }
        LabeledStatement labels = matchJumpLabelName();
        Loop target = null;
        if (labels == null && label == null) {
            if (this.loopSet == null || this.loopSet.size() == 0) {
                reportError("msg.continue.outside");
            } else {
                target = this.loopSet.get(this.loopSet.size() - 1);
            }
        } else {
            if (labels == null || !(labels.getStatement() instanceof Loop)) {
                reportError("msg.continue.nonloop", pos, end - pos);
            }
            target = labels == null ? null : (Loop) labels.getStatement();
        }
        ContinueStatement pn = new ContinueStatement(pos, end - pos);
        if (target != null) {
            pn.setTarget(target);
        }
        pn.setLabel(label);
        pn.setLineno(lineno);
        return pn;
    }

    private WithStatement withStatement() throws IOException {
        if (this.currentToken != 124) {
            codeBug();
        }
        consumeToken();
        Comment withComment = getAndResetJsDoc();
        int lineno = this.ts.lineno;
        int pos = this.ts.tokenBeg;
        int lp = -1;
        int rp = -1;
        if (mustMatchToken(88, "msg.no.paren.with", true)) {
            lp = this.ts.tokenBeg;
        }
        AstNode obj = expr();
        if (mustMatchToken(89, "msg.no.paren.after.with", true)) {
            rp = this.ts.tokenBeg;
        }
        WithStatement pn = new WithStatement(pos);
        AstNode body = getNextStatementAfterInlineComments(pn);
        pn.setLength(getNodeEnd(body) - pos);
        pn.setJsDocNode(withComment);
        pn.setExpression(obj);
        pn.setStatement(body);
        pn.setParens(lp, rp);
        pn.setLineno(lineno);
        return pn;
    }

    private AstNode letStatement() throws IOException {
        AstNode pn;
        if (this.currentToken != 154) {
            codeBug();
        }
        consumeToken();
        int lineno = this.ts.lineno;
        int pos = this.ts.tokenBeg;
        if (peekToken() == 88) {
            pn = let(true, pos);
        } else {
            pn = variables(154, pos, true);
        }
        pn.setLineno(lineno);
        return pn;
    }

    private static final boolean nowAllSet(int before, int after, int mask) {
        return (before & mask) != mask && (after & mask) == mask;
    }

    private AstNode returnOrYield(int tt, boolean exprContext) throws IOException {
        AstNode ret;
        if (!insideFunction()) {
            reportError(tt == 4 ? "msg.bad.return" : "msg.bad.yield");
        }
        consumeToken();
        int lineno = this.ts.lineno;
        int pos = this.ts.tokenBeg;
        int end = this.ts.tokenEnd;
        boolean yieldStar = false;
        if (tt == 73 && this.compilerEnv.getLanguageVersion() >= 200 && peekToken() == 23) {
            yieldStar = true;
            consumeToken();
        }
        AstNode e = null;
        switch (peekTokenOrEOL()) {
            case -1:
            case 0:
            case 1:
            case 83:
            case 85:
            case 87:
            case 89:
                break;
            case 73:
                if (this.compilerEnv.getLanguageVersion() >= 200) {
                }
            default:
                e = expr();
                end = getNodeEnd(e);
                break;
        }
        int before = this.endFlags;
        if (tt == 4) {
            this.endFlags |= e == null ? 2 : 4;
            ret = new ReturnStatement(pos, end - pos, e);
            if (nowAllSet(before, this.endFlags, 6)) {
                addStrictWarning("msg.return.inconsistent", "", pos, end - pos);
            }
        } else {
            if (!insideFunction()) {
                reportError("msg.bad.yield");
            }
            this.endFlags |= 8;
            ret = new Yield(pos, end - pos, e, yieldStar);
            setRequiresActivation();
            setIsGenerator();
            if (!exprContext) {
                ret = new ExpressionStatement(ret);
            }
        }
        if (insideFunction() && nowAllSet(before, this.endFlags, 12)) {
            FunctionNode fn = (FunctionNode) this.currentScriptOrFn;
            if (!fn.isES6Generator()) {
                Name name = ((FunctionNode) this.currentScriptOrFn).getFunctionName();
                if (name == null || name.length() == 0) {
                    addError("msg.anon.generator.returns", "");
                } else {
                    addError("msg.generator.returns", name.getIdentifier());
                }
            }
        }
        ret.setLineno(lineno);
        return ret;
    }

    private AstNode block() throws IOException {
        if (this.currentToken != 86) {
            codeBug();
        }
        consumeToken();
        int pos = this.ts.tokenBeg;
        Scope block = new Scope(pos);
        block.setLineno(this.ts.lineno);
        pushScope(block);
        try {
            statements(block);
            mustMatchToken(87, "msg.no.brace.block", true);
            block.setLength(this.ts.tokenEnd - pos);
            return block;
        } finally {
            popScope();
        }
    }

    private AstNode defaultXmlNamespace() throws IOException {
        if (this.currentToken != 117) {
            codeBug();
        }
        consumeToken();
        mustHaveXML();
        setRequiresActivation();
        int lineno = this.ts.lineno;
        int pos = this.ts.tokenBeg;
        if (!matchToken(39, true) || !"xml".equals(this.ts.getString())) {
            reportError("msg.bad.namespace");
        }
        if (!matchToken(39, true) || !"namespace".equals(this.ts.getString())) {
            reportError("msg.bad.namespace");
        }
        if (!matchToken(91, true)) {
            reportError("msg.bad.namespace");
        }
        AstNode e = expr();
        UnaryExpression dxmln = new UnaryExpression(pos, getNodeEnd(e) - pos);
        dxmln.setOperator(75);
        dxmln.setOperand(e);
        dxmln.setLineno(lineno);
        ExpressionStatement es2 = new ExpressionStatement((AstNode) dxmln, true);
        return es2;
    }

    private void recordLabel(Label label, LabeledStatement bundle) throws IOException {
        if (peekToken() != 104) {
            codeBug();
        }
        consumeToken();
        String name = label.getName();
        if (this.labelSet == null) {
            this.labelSet = new HashMap();
        } else {
            LabeledStatement ls = this.labelSet.get(name);
            if (ls != null) {
                if (this.compilerEnv.isIdeMode()) {
                    Label dup = ls.getLabelByName(name);
                    reportError("msg.dup.label", dup.getAbsolutePosition(), dup.getLength());
                }
                reportError("msg.dup.label", label.getPosition(), label.getLength());
            }
        }
        bundle.addLabel(label);
        this.labelSet.put(name, bundle);
    }

    private AstNode nameOrLabel() throws IOException {
        int nodeEnd;
        if (this.currentToken != 39) {
            throw codeBug();
        }
        int pos = this.ts.tokenBeg;
        this.currentFlaggedToken |= 131072;
        AstNode expr = expr();
        if (expr.getType() != 131) {
            AstNode n = new ExpressionStatement(expr, !insideFunction());
            n.lineno = expr.lineno;
            return n;
        }
        LabeledStatement bundle = new LabeledStatement(pos);
        recordLabel((Label) expr, bundle);
        bundle.setLineno(this.ts.lineno);
        AstNode stmt = null;
        while (peekToken() == 39) {
            this.currentFlaggedToken |= 131072;
            AstNode expr2 = expr();
            if (expr2.getType() != 131) {
                stmt = new ExpressionStatement(expr2, !insideFunction());
                autoInsertSemicolon(stmt);
                break;
            }
            recordLabel((Label) expr2, bundle);
        }
        try {
            this.currentLabel = bundle;
            if (stmt == null) {
                stmt = statementHelper();
                int ntt = peekToken();
                if (ntt == 162 && stmt.getLineno() == this.scannedComments.get(this.scannedComments.size() - 1).getLineno()) {
                    stmt.setInlineComment(this.scannedComments.get(this.scannedComments.size() - 1));
                    consumeToken();
                }
            }
            this.currentLabel = null;
            for (Label lb : bundle.getLabels()) {
                this.labelSet.remove(lb.getName());
            }
            if (stmt.getParent() == null) {
                nodeEnd = getNodeEnd(stmt) - pos;
            } else {
                nodeEnd = getNodeEnd(stmt);
            }
            bundle.setLength(nodeEnd);
            bundle.setStatement(stmt);
            return bundle;
        } catch (Throwable th) {
            this.currentLabel = null;
            for (Label lb2 : bundle.getLabels()) {
                this.labelSet.remove(lb2.getName());
            }
            throw th;
        }
    }

    private VariableDeclaration variables(int declType, int pos, boolean isStatement) throws IOException {
        int end;
        VariableDeclaration pn = new VariableDeclaration(pos);
        pn.setType(declType);
        pn.setLineno(this.ts.lineno);
        Comment varjsdocNode = getAndResetJsDoc();
        if (varjsdocNode != null) {
            pn.setJsDocNode(varjsdocNode);
        }
        do {
            AstNode destructuring = null;
            Name name = null;
            int tt = peekToken();
            int kidPos = this.ts.tokenBeg;
            end = this.ts.tokenEnd;
            if (tt == 84 || tt == 86) {
                destructuring = destructuringPrimaryExpr();
                end = getNodeEnd(destructuring);
                if (!(destructuring instanceof DestructuringForm)) {
                    reportError("msg.bad.assign.left", kidPos, end - kidPos);
                }
                markDestructuring(destructuring);
            } else {
                mustMatchToken(39, "msg.bad.var", true);
                name = createNameNode();
                name.setLineno(this.ts.getLineno());
                if (this.inUseStrictDirective) {
                    String id = this.ts.getString();
                    if ("eval".equals(id) || "arguments".equals(this.ts.getString())) {
                        reportError("msg.bad.id.strict", id);
                    }
                }
                defineSymbol(declType, this.ts.getString(), this.inForInit);
            }
            int lineno = this.ts.lineno;
            Comment jsdocNode = getAndResetJsDoc();
            AstNode init = null;
            if (matchToken(91, true)) {
                init = assignExpr();
                end = getNodeEnd(init);
            }
            VariableInitializer vi = new VariableInitializer(kidPos, end - kidPos);
            if (destructuring != null) {
                if (init == null && !this.inForInit) {
                    reportError("msg.destruct.assign.no.init");
                }
                vi.setTarget(destructuring);
            } else {
                vi.setTarget(name);
            }
            vi.setInitializer(init);
            vi.setType(declType);
            vi.setJsDocNode(jsdocNode);
            vi.setLineno(lineno);
            pn.addVariable(vi);
        } while (matchToken(90, true));
        pn.setLength(end - pos);
        pn.setIsStatement(isStatement);
        return pn;
    }

    private AstNode let(boolean isStatement, int pos) throws IOException {
        LetNode pn = new LetNode(pos);
        pn.setLineno(this.ts.lineno);
        if (mustMatchToken(88, "msg.no.paren.after.let", true)) {
            pn.setLp(this.ts.tokenBeg - pos);
        }
        pushScope(pn);
        try {
            VariableDeclaration vars = variables(154, this.ts.tokenBeg, isStatement);
            pn.setVariables(vars);
            if (mustMatchToken(89, "msg.no.paren.let", true)) {
                pn.setRp(this.ts.tokenBeg - pos);
            }
            if (isStatement && peekToken() == 86) {
                consumeToken();
                int beg = this.ts.tokenBeg;
                AstNode stmt = statements();
                mustMatchToken(87, "msg.no.curly.let", true);
                stmt.setLength(this.ts.tokenEnd - beg);
                pn.setLength(this.ts.tokenEnd - pos);
                pn.setBody(stmt);
                pn.setType(154);
            } else {
                AstNode expr = expr();
                pn.setLength(getNodeEnd(expr) - pos);
                pn.setBody(expr);
                if (isStatement) {
                    ExpressionStatement es2 = new ExpressionStatement(pn, !insideFunction());
                    es2.setLineno(pn.getLineno());
                    return es2;
                }
            }
            return pn;
        } finally {
            popScope();
        }
    }

    void defineSymbol(int declType, String name) {
        defineSymbol(declType, name, false);
    }

    void defineSymbol(int declType, String name, boolean ignoreNotInBlock) {
        String str;
        if (name == null) {
            if (this.compilerEnv.isIdeMode()) {
                return;
            } else {
                codeBug();
            }
        }
        Scope definingScope = this.currentScope.getDefiningScope(name);
        org.mozilla.javascript.ast.Symbol symbol = definingScope != null ? definingScope.getSymbol(name) : null;
        int symDeclType = symbol != null ? symbol.getDeclType() : -1;
        if (symbol != null && (symDeclType == 155 || declType == 155 || (definingScope == this.currentScope && symDeclType == 154))) {
            if (symDeclType == 155) {
                str = "msg.const.redecl";
            } else if (symDeclType == 154) {
                str = "msg.let.redecl";
            } else if (symDeclType == 123) {
                str = "msg.var.redecl";
            } else {
                str = symDeclType == 110 ? "msg.fn.redecl" : "msg.parm.redecl";
            }
            addError(str, name);
            return;
        }
        switch (declType) {
            case 88:
                if (symbol != null) {
                    addWarning("msg.dup.parms", name);
                }
                this.currentScriptOrFn.putSymbol(new org.mozilla.javascript.ast.Symbol(declType, name));
                return;
            case 110:
            case 123:
            case 155:
                if (symbol != null) {
                    if (symDeclType == 123) {
                        addStrictWarning("msg.var.redecl", name);
                        return;
                    } else {
                        if (symDeclType == 88) {
                            addStrictWarning("msg.var.hides.arg", name);
                            return;
                        }
                        return;
                    }
                }
                this.currentScriptOrFn.putSymbol(new org.mozilla.javascript.ast.Symbol(declType, name));
                return;
            case 154:
                if (!ignoreNotInBlock && (this.currentScope.getType() == 113 || (this.currentScope instanceof Loop))) {
                    addError("msg.let.decl.not.in.block");
                    return;
                } else {
                    this.currentScope.putSymbol(new org.mozilla.javascript.ast.Symbol(declType, name));
                    return;
                }
            default:
                throw codeBug();
        }
    }

    private AstNode expr() throws IOException {
        AstNode pn = assignExpr();
        int pos = pn.getPosition();
        while (matchToken(90, true)) {
            int opPos = this.ts.tokenBeg;
            if (this.compilerEnv.isStrictMode() && !pn.hasSideEffects()) {
                addStrictWarning("msg.no.side.effects", "", pos, nodeEnd(pn) - pos);
            }
            if (peekToken() == 73) {
                reportError("msg.yield.parenthesized");
            }
            pn = new InfixExpression(90, pn, assignExpr(), opPos);
        }
        return pn;
    }

    private AstNode assignExpr() throws IOException {
        int tt = peekToken();
        if (tt == 73) {
            return returnOrYield(tt, true);
        }
        AstNode pn = condExpr();
        boolean hasEOL = false;
        int tt2 = peekTokenOrEOL();
        if (tt2 == 1) {
            hasEOL = true;
            tt2 = peekToken();
        }
        if (91 <= tt2 && tt2 <= 102) {
            if (this.inDestructuringAssignment) {
                reportError("msg.destruct.default.vals");
            }
            consumeToken();
            Comment jsdocNode = getAndResetJsDoc();
            markDestructuring(pn);
            int opPos = this.ts.tokenBeg;
            pn = new Assignment(tt2, pn, assignExpr(), opPos);
            if (jsdocNode != null) {
                pn.setJsDocNode(jsdocNode);
            }
        } else if (tt2 == 83) {
            if (this.currentJsDocComment != null) {
                pn.setJsDocNode(getAndResetJsDoc());
            }
        } else if (!hasEOL && tt2 == 165) {
            consumeToken();
            pn = arrowFunction(pn);
        }
        return pn;
    }

    private AstNode condExpr() throws IOException {
        AstNode astNodeOrExpr = orExpr();
        AstNode astNode = astNodeOrExpr;
        if (matchToken(103, true)) {
            int i = this.ts.lineno;
            int i2 = this.ts.tokenBeg;
            int i3 = -1;
            boolean z = this.inForInit;
            this.inForInit = false;
            try {
                AstNode astNodeAssignExpr = assignExpr();
                this.inForInit = z;
                if (mustMatchToken(104, "msg.no.colon.cond", true)) {
                    i3 = this.ts.tokenBeg;
                }
                AstNode astNodeAssignExpr2 = assignExpr();
                int position = astNodeOrExpr.getPosition();
                ConditionalExpression conditionalExpression = new ConditionalExpression(position, getNodeEnd(astNodeAssignExpr2) - position);
                conditionalExpression.setLineno(i);
                conditionalExpression.setTestExpression(astNodeOrExpr);
                conditionalExpression.setTrueExpression(astNodeAssignExpr);
                conditionalExpression.setFalseExpression(astNodeAssignExpr2);
                conditionalExpression.setQuestionMarkPosition(i2 - position);
                conditionalExpression.setColonPosition(i3 - position);
                astNode = conditionalExpression;
            } catch (Throwable th) {
                this.inForInit = z;
                throw th;
            }
        }
        return astNode;
    }

    private AstNode orExpr() throws IOException {
        AstNode pn = andExpr();
        if (matchToken(105, true)) {
            int opPos = this.ts.tokenBeg;
            pn = new InfixExpression(105, pn, orExpr(), opPos);
        }
        return pn;
    }

    private AstNode andExpr() throws IOException {
        AstNode pn = bitOrExpr();
        if (matchToken(106, true)) {
            int opPos = this.ts.tokenBeg;
            pn = new InfixExpression(106, pn, andExpr(), opPos);
        }
        return pn;
    }

    private AstNode bitOrExpr() throws IOException {
        AstNode astNodeBitXorExpr = bitXorExpr();
        while (true) {
            AstNode pn = astNodeBitXorExpr;
            if (matchToken(9, true)) {
                int opPos = this.ts.tokenBeg;
                astNodeBitXorExpr = new InfixExpression(9, pn, bitXorExpr(), opPos);
            } else {
                return pn;
            }
        }
    }

    private AstNode bitXorExpr() throws IOException {
        AstNode astNodeBitAndExpr = bitAndExpr();
        while (true) {
            AstNode pn = astNodeBitAndExpr;
            if (matchToken(10, true)) {
                int opPos = this.ts.tokenBeg;
                astNodeBitAndExpr = new InfixExpression(10, pn, bitAndExpr(), opPos);
            } else {
                return pn;
            }
        }
    }

    private AstNode bitAndExpr() throws IOException {
        AstNode astNodeEqExpr = eqExpr();
        while (true) {
            AstNode pn = astNodeEqExpr;
            if (matchToken(11, true)) {
                int opPos = this.ts.tokenBeg;
                astNodeEqExpr = new InfixExpression(11, pn, eqExpr(), opPos);
            } else {
                return pn;
            }
        }
    }

    /* JADX WARN: Switch 'out' block B:3:0x0005 for B:4:0x0013 already processed. Defaulting to fallback option. */
    private AstNode eqExpr() throws IOException {
        AstNode astNodeRelExpr = relExpr();
        while (true) {
            AstNode pn = astNodeRelExpr;
            int tt = peekToken();
            int opPos = this.ts.tokenBeg;
            switch (tt) {
                case 12:
                case 13:
                case 46:
                case 47:
                    consumeToken();
                    int parseToken = tt;
                    if (this.compilerEnv.getLanguageVersion() == 120) {
                        if (tt == 12) {
                            parseToken = 46;
                        } else if (tt == 13) {
                            parseToken = 47;
                        }
                    }
                    astNodeRelExpr = new InfixExpression(parseToken, pn, relExpr(), opPos);
                    break;
            }
            return pn;
        }
    }

    private AstNode relExpr() throws IOException {
        AstNode pn;
        AstNode astNodeShiftExpr = shiftExpr();
        while (true) {
            pn = astNodeShiftExpr;
            int tt = peekToken();
            int opPos = this.ts.tokenBeg;
            switch (tt) {
                case 14:
                case 15:
                case 16:
                case 17:
                case 53:
                    break;
                case 52:
                    if (!this.inForInit) {
                    }
                    break;
                default:
                    break;
            }
            consumeToken();
            astNodeShiftExpr = new InfixExpression(tt, pn, shiftExpr(), opPos);
        }
        return pn;
    }

    private AstNode shiftExpr() throws IOException {
        AstNode astNodeAddExpr = addExpr();
        while (true) {
            AstNode pn = astNodeAddExpr;
            int tt = peekToken();
            int opPos = this.ts.tokenBeg;
            switch (tt) {
                case 18:
                case 19:
                case 20:
                    consumeToken();
                    astNodeAddExpr = new InfixExpression(tt, pn, addExpr(), opPos);
                    break;
                default:
                    return pn;
            }
        }
    }

    private AstNode addExpr() throws IOException {
        AstNode astNodeMulExpr = mulExpr();
        while (true) {
            AstNode pn = astNodeMulExpr;
            int tt = peekToken();
            int opPos = this.ts.tokenBeg;
            if (tt == 21 || tt == 22) {
                consumeToken();
                astNodeMulExpr = new InfixExpression(tt, pn, mulExpr(), opPos);
            } else {
                return pn;
            }
        }
    }

    private AstNode mulExpr() throws IOException {
        AstNode astNodeUnaryExpr = unaryExpr();
        while (true) {
            AstNode pn = astNodeUnaryExpr;
            int tt = peekToken();
            int opPos = this.ts.tokenBeg;
            switch (tt) {
                case 23:
                case 24:
                case 25:
                    consumeToken();
                    astNodeUnaryExpr = new InfixExpression(tt, pn, unaryExpr(), opPos);
                    break;
                default:
                    return pn;
            }
        }
    }

    private AstNode unaryExpr() throws IOException {
        int tt = peekToken();
        if (tt == 162) {
            consumeToken();
            tt = peekUntilNonComment(tt);
        }
        int line = this.ts.lineno;
        switch (tt) {
            case -1:
                consumeToken();
                return makeErrorNode();
            case 14:
                if (this.compilerEnv.isXmlAvailable()) {
                    consumeToken();
                    return memberExprTail(true, xmlInitializer());
                }
                break;
            case 21:
                consumeToken();
                AstNode node = new UnaryExpression(28, this.ts.tokenBeg, unaryExpr());
                node.setLineno(line);
                return node;
            case 22:
                consumeToken();
                AstNode node2 = new UnaryExpression(29, this.ts.tokenBeg, unaryExpr());
                node2.setLineno(line);
                return node2;
            case 26:
            case 27:
            case 32:
            case 127:
                consumeToken();
                AstNode node3 = new UnaryExpression(tt, this.ts.tokenBeg, unaryExpr());
                node3.setLineno(line);
                return node3;
            case 31:
                consumeToken();
                AstNode node4 = new UnaryExpression(tt, this.ts.tokenBeg, unaryExpr());
                node4.setLineno(line);
                return node4;
            case 107:
            case 108:
                consumeToken();
                UnaryExpression expr = new UnaryExpression(tt, this.ts.tokenBeg, memberExpr(true));
                expr.setLineno(line);
                checkBadIncDec(expr);
                return expr;
        }
        AstNode pn = memberExpr(true);
        int tt2 = peekTokenOrEOL();
        if (tt2 != 107 && tt2 != 108) {
            return pn;
        }
        consumeToken();
        UnaryExpression uexpr = new UnaryExpression(tt2, this.ts.tokenBeg, pn, true);
        uexpr.setLineno(line);
        checkBadIncDec(uexpr);
        return uexpr;
    }

    /* JADX WARN: Switch 'out' block B:12:0x004c for B:13:0x004d already processed. Defaulting to fallback option. */
    private AstNode xmlInitializer() throws IOException {
        if (this.currentToken != 14) {
            codeBug();
        }
        int pos = this.ts.tokenBeg;
        int tt = this.ts.getFirstXMLToken();
        if (tt != 146 && tt != 149) {
            reportError("msg.syntax");
            return makeErrorNode();
        }
        XmlLiteral pn = new XmlLiteral(pos);
        pn.setLineno(this.ts.lineno);
        while (true) {
            switch (tt) {
                case 146:
                    pn.addFragment(new XmlString(this.ts.tokenBeg, this.ts.getString()));
                    mustMatchToken(86, "msg.syntax", true);
                    int beg = this.ts.tokenBeg;
                    AstNode expr = peekToken() == 87 ? new EmptyExpression(beg, this.ts.tokenEnd - beg) : expr();
                    mustMatchToken(87, "msg.syntax", true);
                    XmlExpression xexpr = new XmlExpression(beg, expr);
                    xexpr.setIsXmlAttribute(this.ts.isXMLAttribute());
                    xexpr.setLength(this.ts.tokenEnd - beg);
                    pn.addFragment(xexpr);
                    tt = this.ts.getNextXMLToken();
                    break;
                case 149:
                    pn.addFragment(new XmlString(this.ts.tokenBeg, this.ts.getString()));
                    return pn;
                default:
                    reportError("msg.syntax");
                    return makeErrorNode();
            }
        }
    }

    private List<AstNode> argumentList() throws IOException {
        if (matchToken(89, true)) {
            return null;
        }
        List<AstNode> result = new ArrayList<>();
        boolean wasInForInit = this.inForInit;
        this.inForInit = false;
        do {
            try {
                if (peekToken() == 89) {
                    break;
                }
                if (peekToken() == 73) {
                    reportError("msg.yield.parenthesized");
                }
                AstNode en = assignExpr();
                if (peekToken() == 120) {
                    try {
                        result.add(generatorExpression(en, 0, true));
                    } catch (IOException e) {
                    }
                } else {
                    result.add(en);
                }
            } catch (Throwable th) {
                this.inForInit = wasInForInit;
                throw th;
            }
        } while (matchToken(90, true));
        this.inForInit = wasInForInit;
        mustMatchToken(89, "msg.no.paren.arg", true);
        return result;
    }

    private AstNode memberExpr(boolean allowCallSyntax) throws IOException {
        AstNode pn;
        int tt = peekToken();
        int lineno = this.ts.lineno;
        if (tt != 30) {
            pn = primaryExpr();
        } else {
            consumeToken();
            int pos = this.ts.tokenBeg;
            NewExpression nx = new NewExpression(pos);
            AstNode target = memberExpr(false);
            int end = getNodeEnd(target);
            nx.setTarget(target);
            if (matchToken(88, true)) {
                int lp = this.ts.tokenBeg;
                List<AstNode> args = argumentList();
                if (args != null && args.size() > 65536) {
                    reportError("msg.too.many.constructor.args");
                }
                int rp = this.ts.tokenBeg;
                end = this.ts.tokenEnd;
                if (args != null) {
                    nx.setArguments(args);
                }
                nx.setParens(lp - pos, rp - pos);
            }
            if (matchToken(86, true)) {
                ObjectLiteral initializer = objectLiteral();
                end = getNodeEnd(initializer);
                nx.setInitializer(initializer);
            }
            nx.setLength(end - pos);
            pn = nx;
        }
        pn.setLineno(lineno);
        AstNode tail = memberExprTail(allowCallSyntax, pn);
        return tail;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.mozilla.javascript.ast.AstNode] */
    /* JADX WARN: Type inference failed for: r0v24, types: [org.mozilla.javascript.ast.FunctionCall] */
    /* JADX WARN: Type inference failed for: r0v52, types: [org.mozilla.javascript.ast.ElementGet] */
    /* JADX WARN: Type inference failed for: r0v80, types: [org.mozilla.javascript.ast.XmlDotQuery] */
    /* JADX WARN: Type inference failed for: r1v10, types: [org.mozilla.javascript.ast.AstNode] */
    /* JADX WARN: Type inference failed for: r1v12, types: [org.mozilla.javascript.ast.AstNode] */
    /* JADX WARN: Type inference failed for: r1v31, types: [org.mozilla.javascript.ast.AstNode] */
    /* JADX WARN: Type inference failed for: r1v37, types: [org.mozilla.javascript.ast.AstNode] */
    /* JADX WARN: Type inference failed for: r2v0, types: [org.mozilla.javascript.ast.AstNode] */
    /* JADX WARN: Type inference failed for: r6v0, types: [org.mozilla.javascript.Parser] */
    /* JADX WARN: Type inference failed for: r8v0, types: [org.mozilla.javascript.ast.AstNode] */
    /* JADX WARN: Type inference failed for: r8v1 */
    /* JADX WARN: Type inference failed for: r8v2, types: [org.mozilla.javascript.ast.AstNode] */
    /* JADX WARN: Type inference failed for: r8v3 */
    /* JADX WARN: Type inference failed for: r8v4 */
    /* JADX WARN: Type inference failed for: r8v5 */
    /* JADX WARN: Type inference failed for: r8v6 */
    private AstNode memberExprTail(boolean allowCallSyntax, AstNode astNode) throws IOException {
        if (astNode == 0) {
            codeBug();
        }
        int pos = astNode.getPosition();
        while (true) {
            int tt = peekToken();
            switch (tt) {
                case 84:
                    consumeToken();
                    int lb = this.ts.tokenBeg;
                    int rb = -1;
                    int lineno = this.ts.lineno;
                    AstNode expr = expr();
                    int end = getNodeEnd(expr);
                    if (mustMatchToken(85, "msg.no.bracket.index", true)) {
                        rb = this.ts.tokenBeg;
                        end = this.ts.tokenEnd;
                    }
                    ?? elementGet = new ElementGet(pos, end - pos);
                    elementGet.setTarget(astNode);
                    elementGet.setElement(expr);
                    elementGet.setParens(lb, rb);
                    elementGet.setLineno(lineno);
                    astNode = elementGet;
                    break;
                case 88:
                    if (allowCallSyntax) {
                        int lineno2 = this.ts.lineno;
                        consumeToken();
                        checkCallRequiresActivation(astNode);
                        ?? functionCall = new FunctionCall(pos);
                        functionCall.setTarget(astNode);
                        functionCall.setLineno(lineno2);
                        functionCall.setLp(this.ts.tokenBeg - pos);
                        List<AstNode> args = argumentList();
                        if (args != null && args.size() > 65536) {
                            reportError("msg.too.many.function.args");
                        }
                        functionCall.setArguments(args);
                        functionCall.setRp(this.ts.tokenBeg - pos);
                        functionCall.setLength(this.ts.tokenEnd - pos);
                        astNode = functionCall;
                    }
                    break;
                case 109:
                case 144:
                    int lineno3 = this.ts.lineno;
                    astNode = propertyAccess(tt, astNode);
                    astNode.setLineno(lineno3);
                    break;
                case 147:
                    consumeToken();
                    int opPos = this.ts.tokenBeg;
                    int rp = -1;
                    int lineno4 = this.ts.lineno;
                    mustHaveXML();
                    setRequiresActivation();
                    AstNode filter = expr();
                    int end2 = getNodeEnd(filter);
                    if (mustMatchToken(89, "msg.no.paren", true)) {
                        rp = this.ts.tokenBeg;
                        end2 = this.ts.tokenEnd;
                    }
                    ?? xmlDotQuery = new XmlDotQuery(pos, end2 - pos);
                    xmlDotQuery.setLeft(astNode);
                    xmlDotQuery.setRight(filter);
                    xmlDotQuery.setOperatorPosition(opPos);
                    xmlDotQuery.setRp(rp - pos);
                    xmlDotQuery.setLineno(lineno4);
                    astNode = xmlDotQuery;
                    break;
                case 162:
                    int currentFlagTOken = this.currentFlaggedToken;
                    peekUntilNonComment(tt);
                    this.currentFlaggedToken = (this.currentFlaggedToken & 65536) != 0 ? this.currentFlaggedToken : currentFlagTOken;
                    break;
                default:
                    break;
            }
        }
        return astNode;
    }

    private AstNode propertyAccess(int tt, AstNode pn) throws IOException {
        String name;
        AstNode ref;
        if (pn == null) {
            codeBug();
        }
        int memberTypeFlags = 0;
        int lineno = this.ts.lineno;
        int dotPos = this.ts.tokenBeg;
        consumeToken();
        if (tt == 144) {
            mustHaveXML();
            memberTypeFlags = 4;
        }
        if (!this.compilerEnv.isXmlAvailable()) {
            int maybeName = nextToken();
            if (maybeName != 39 && (!this.compilerEnv.isReservedKeywordAsIdentifier() || !TokenStream.isKeyword(this.ts.getString(), this.compilerEnv.getLanguageVersion(), this.inUseStrictDirective))) {
                reportError("msg.no.name.after.dot");
            }
            PropertyGet pg = new PropertyGet(pn, createNameNode(true, 33), dotPos);
            pg.setLineno(lineno);
            return pg;
        }
        int token = nextToken();
        switch (token) {
            case 23:
                saveNameTokenData(this.ts.tokenBeg, "*", this.ts.lineno);
                ref = propertyName(-1, memberTypeFlags);
                break;
            case 39:
                ref = propertyName(-1, memberTypeFlags);
                break;
            case 50:
                saveNameTokenData(this.ts.tokenBeg, "throw", this.ts.lineno);
                ref = propertyName(-1, memberTypeFlags);
                break;
            case 128:
                saveNameTokenData(this.ts.tokenBeg, this.ts.getString(), this.ts.lineno);
                ref = propertyName(-1, memberTypeFlags);
                break;
            case 148:
                ref = attributeAccess();
                break;
            default:
                if (this.compilerEnv.isReservedKeywordAsIdentifier() && (name = Token.keywordToName(token)) != null) {
                    saveNameTokenData(this.ts.tokenBeg, name, this.ts.lineno);
                    ref = propertyName(-1, memberTypeFlags);
                } else {
                    reportError("msg.no.name.after.dot");
                    return makeErrorNode();
                }
                break;
        }
        boolean xml = ref instanceof XmlRef;
        InfixExpression result = xml ? new XmlMemberGet() : new PropertyGet();
        if (xml && tt == 109) {
            result.setType(109);
        }
        int pos = pn.getPosition();
        result.setPosition(pos);
        result.setLength(getNodeEnd(ref) - pos);
        result.setOperatorPosition(dotPos - pos);
        result.setLineno(pn.getLineno());
        result.setLeft(pn);
        result.setRight(ref);
        return result;
    }

    private AstNode attributeAccess() throws IOException {
        int tt = nextToken();
        int atPos = this.ts.tokenBeg;
        switch (tt) {
            case 23:
                saveNameTokenData(this.ts.tokenBeg, "*", this.ts.lineno);
                return propertyName(atPos, 0);
            case 39:
                return propertyName(atPos, 0);
            case 84:
                return xmlElemRef(atPos, null, -1);
            default:
                reportError("msg.no.name.after.xmlAttr");
                return makeErrorNode();
        }
    }

    private AstNode propertyName(int atPos, int memberTypeFlags) throws IOException {
        int pos = atPos != -1 ? atPos : this.ts.tokenBeg;
        int lineno = this.ts.lineno;
        int colonPos = -1;
        Name name = createNameNode(true, this.currentToken);
        Name ns = null;
        if (matchToken(145, true)) {
            ns = name;
            colonPos = this.ts.tokenBeg;
            switch (nextToken()) {
                case 23:
                    saveNameTokenData(this.ts.tokenBeg, "*", this.ts.lineno);
                    name = createNameNode(false, -1);
                    break;
                case 39:
                    name = createNameNode();
                    break;
                case 84:
                    return xmlElemRef(atPos, ns, colonPos);
                default:
                    reportError("msg.no.name.after.coloncolon");
                    return makeErrorNode();
            }
        }
        if (ns == null && memberTypeFlags == 0 && atPos == -1) {
            return name;
        }
        XmlPropRef ref = new XmlPropRef(pos, getNodeEnd(name) - pos);
        ref.setAtPos(atPos);
        ref.setNamespace(ns);
        ref.setColonPos(colonPos);
        ref.setPropName(name);
        ref.setLineno(lineno);
        return ref;
    }

    private XmlElemRef xmlElemRef(int atPos, Name namespace, int colonPos) throws IOException {
        int lb = this.ts.tokenBeg;
        int rb = -1;
        int pos = atPos != -1 ? atPos : lb;
        AstNode expr = expr();
        int end = getNodeEnd(expr);
        if (mustMatchToken(85, "msg.no.bracket.index", true)) {
            rb = this.ts.tokenBeg;
            end = this.ts.tokenEnd;
        }
        XmlElemRef ref = new XmlElemRef(pos, end - pos);
        ref.setNamespace(namespace);
        ref.setColonPos(colonPos);
        ref.setAtPos(atPos);
        ref.setExpression(expr);
        ref.setBrackets(lb, rb);
        return ref;
    }

    private AstNode destructuringPrimaryExpr() throws ParserException, IOException {
        try {
            this.inDestructuringAssignment = true;
            return primaryExpr();
        } finally {
            this.inDestructuringAssignment = false;
        }
    }

    private AstNode primaryExpr() throws IOException {
        int ttFlagged = peekFlaggedToken();
        int tt = ttFlagged & 65535;
        switch (tt) {
            case -1:
                consumeToken();
                break;
            case 0:
                consumeToken();
                reportError("msg.unexpected.eof");
                break;
            case 24:
            case 101:
                consumeToken();
                this.ts.readRegExp(tt);
                int pos = this.ts.tokenBeg;
                int end = this.ts.tokenEnd;
                RegExpLiteral re = new RegExpLiteral(pos, end - pos);
                re.setValue(this.ts.getString());
                re.setFlags(this.ts.readAndClearRegExpFlags());
                return re;
            case 39:
                consumeToken();
                return name(ttFlagged, tt);
            case 40:
                consumeToken();
                String s = this.ts.getString();
                if (this.inUseStrictDirective && this.ts.isNumberOldOctal()) {
                    reportError("msg.no.old.octal.strict");
                }
                if (this.ts.isNumberBinary()) {
                    s = "0b" + s;
                }
                if (this.ts.isNumberOldOctal()) {
                    s = "0" + s;
                }
                if (this.ts.isNumberOctal()) {
                    s = "0o" + s;
                }
                if (this.ts.isNumberHex()) {
                    s = "0x" + s;
                }
                return new NumberLiteral(this.ts.tokenBeg, s, this.ts.getNumber());
            case 41:
                consumeToken();
                return createStringLiteral();
            case 42:
            case 43:
            case 44:
            case 45:
                consumeToken();
                int pos2 = this.ts.tokenBeg;
                int end2 = this.ts.tokenEnd;
                return new KeywordLiteral(pos2, end2 - pos2, tt);
            case 84:
                consumeToken();
                return arrayLiteral();
            case 86:
                consumeToken();
                return objectLiteral();
            case 88:
                consumeToken();
                return parenExpr();
            case 110:
                consumeToken();
                return function(2);
            case 128:
                consumeToken();
                reportError("msg.reserved.id", this.ts.getString());
                break;
            case 148:
                consumeToken();
                mustHaveXML();
                return attributeAccess();
            case 154:
                consumeToken();
                return let(false, this.ts.tokenBeg);
            default:
                consumeToken();
                reportError("msg.syntax");
                break;
        }
        consumeToken();
        return makeErrorNode();
    }

    private AstNode parenExpr() throws IOException {
        boolean wasInForInit = this.inForInit;
        this.inForInit = false;
        try {
            Comment jsdocNode = getAndResetJsDoc();
            int lineno = this.ts.lineno;
            int begin = this.ts.tokenBeg;
            AstNode e = peekToken() == 89 ? new EmptyExpression(begin) : expr();
            if (peekToken() == 120) {
                return generatorExpression(e, begin);
            }
            mustMatchToken(89, "msg.no.paren", true);
            if (e.getType() == 129 && peekToken() != 165) {
                reportError("msg.syntax");
                return makeErrorNode();
            }
            int length = this.ts.tokenEnd - begin;
            ParenthesizedExpression pn = new ParenthesizedExpression(begin, length, e);
            pn.setLineno(lineno);
            if (jsdocNode == null) {
                jsdocNode = getAndResetJsDoc();
            }
            if (jsdocNode != null) {
                pn.setJsDocNode(jsdocNode);
            }
            return pn;
        } finally {
            this.inForInit = wasInForInit;
        }
    }

    private AstNode name(int ttFlagged, int tt) throws IOException {
        String nameString = this.ts.getString();
        int namePos = this.ts.tokenBeg;
        int nameLineno = this.ts.lineno;
        if (0 != (ttFlagged & 131072) && peekToken() == 104) {
            Label label = new Label(namePos, this.ts.tokenEnd - namePos);
            label.setName(nameString);
            label.setLineno(this.ts.lineno);
            return label;
        }
        saveNameTokenData(namePos, nameString, nameLineno);
        if (this.compilerEnv.isXmlAvailable()) {
            return propertyName(-1, 0);
        }
        return createNameNode(true, 39);
    }

    private AstNode arrayLiteral() throws IOException {
        if (this.currentToken != 84) {
            codeBug();
        }
        int pos = this.ts.tokenBeg;
        int end = this.ts.tokenEnd;
        List<AstNode> elements = new ArrayList<>();
        ArrayLiteral pn = new ArrayLiteral(pos);
        boolean after_lb_or_comma = true;
        int afterComma = -1;
        int skipCount = 0;
        while (true) {
            int tt = peekToken();
            if (tt == 90) {
                consumeToken();
                afterComma = this.ts.tokenEnd;
                if (!after_lb_or_comma) {
                    after_lb_or_comma = true;
                } else {
                    elements.add(new EmptyExpression(this.ts.tokenBeg, 1));
                    skipCount++;
                }
            } else if (tt == 162) {
                consumeToken();
            } else {
                if (tt == 85) {
                    consumeToken();
                    end = this.ts.tokenEnd;
                    pn.setDestructuringLength(elements.size() + (after_lb_or_comma ? 1 : 0));
                    pn.setSkipCount(skipCount);
                    if (afterComma == -1) {
                        break;
                    }
                    warnTrailingComma(pos, elements, afterComma);
                    break;
                }
                if (tt == 120 && !after_lb_or_comma && elements.size() == 1) {
                    return arrayComprehension(elements.get(0), pos);
                }
                if (tt == 0) {
                    reportError("msg.no.bracket.arg");
                    break;
                }
                if (!after_lb_or_comma) {
                    reportError("msg.no.bracket.arg");
                }
                elements.add(assignExpr());
                after_lb_or_comma = false;
                afterComma = -1;
            }
        }
        for (AstNode e : elements) {
            pn.addElement(e);
        }
        pn.setLength(end - pos);
        return pn;
    }

    private AstNode arrayComprehension(AstNode result, int pos) throws IOException {
        List<ArrayComprehensionLoop> loops = new ArrayList<>();
        while (peekToken() == 120) {
            loops.add(arrayComprehensionLoop());
        }
        int ifPos = -1;
        ConditionData data = null;
        if (peekToken() == 113) {
            consumeToken();
            ifPos = this.ts.tokenBeg - pos;
            data = condition();
        }
        mustMatchToken(85, "msg.no.bracket.arg", true);
        ArrayComprehension pn = new ArrayComprehension(pos, this.ts.tokenEnd - pos);
        pn.setResult(result);
        pn.setLoops(loops);
        if (data != null) {
            pn.setIfPosition(ifPos);
            pn.setFilter(data.condition);
            pn.setFilterLp(data.lp - pos);
            pn.setFilterRp(data.rp - pos);
        }
        return pn;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:32:0x0137 A[Catch: all -> 0x01ae, TryCatch #0 {all -> 0x01ae, blocks: (B:6:0x0033, B:8:0x003d, B:10:0x004d, B:11:0x005a, B:12:0x0061, B:14:0x006e, B:15:0x0078, B:16:0x007f, B:17:0x00a0, B:20:0x00c3, B:22:0x00cd, B:23:0x00dc, B:24:0x00e0, B:25:0x00fc, B:33:0x013e, B:35:0x0151, B:36:0x015c, B:40:0x0191, B:26:0x010a, B:30:0x011f, B:31:0x0126, B:32:0x0137, B:18:0x00af, B:19:0x00bc), top: B:47:0x0033 }] */
    private ArrayComprehensionLoop arrayComprehensionLoop() throws IOException {
        if (nextToken() != 120) {
            codeBug();
        }
        int pos = this.ts.tokenBeg;
        int eachPos = -1;
        int inPos = -1;
        boolean isForOf = false;
        ArrayComprehensionLoop pn = new ArrayComprehensionLoop(pos);
        pushScope(pn);
        try {
            if (matchToken(39, true)) {
                if (this.ts.getString().equals("each")) {
                    eachPos = this.ts.tokenBeg - pos;
                } else {
                    reportError("msg.no.paren.for");
                }
            }
            int lp = mustMatchToken(88, "msg.no.paren.for", true) ? this.ts.tokenBeg - pos : -1;
            AstNode iter = null;
            switch (peekToken()) {
                case 39:
                    consumeToken();
                    iter = createNameNode();
                    break;
                case 84:
                case 86:
                    iter = destructuringPrimaryExpr();
                    markDestructuring(iter);
                    break;
                default:
                    reportError("msg.bad.var");
                    break;
            }
            if (iter.getType() == 39) {
                defineSymbol(154, this.ts.getString(), true);
            }
            switch (nextToken()) {
                case 39:
                    if ("of".equals(this.ts.getString())) {
                        if (eachPos != -1) {
                            reportError("msg.invalid.for.each");
                        }
                        inPos = this.ts.tokenBeg - pos;
                        isForOf = true;
                    } else {
                        reportError("msg.in.after.for.name");
                    }
                    break;
                case 52:
                    inPos = this.ts.tokenBeg - pos;
                    break;
                default:
                    reportError("msg.in.after.for.name");
                    break;
            }
            AstNode obj = expr();
            int rp = mustMatchToken(89, "msg.no.paren.for.ctrl", true) ? this.ts.tokenBeg - pos : -1;
            pn.setLength(this.ts.tokenEnd - pos);
            pn.setIterator(iter);
            pn.setIteratedObject(obj);
            pn.setInPosition(inPos);
            pn.setEachPosition(eachPos);
            pn.setIsForEach(eachPos != -1);
            pn.setParens(lp, rp);
            pn.setIsForOf(isForOf);
            return pn;
        } finally {
            popScope();
        }
    }

    private AstNode generatorExpression(AstNode result, int pos) throws IOException {
        return generatorExpression(result, pos, false);
    }

    private AstNode generatorExpression(AstNode result, int pos, boolean inFunctionParams) throws IOException {
        List<GeneratorExpressionLoop> loops = new ArrayList<>();
        while (peekToken() == 120) {
            loops.add(generatorExpressionLoop());
        }
        int ifPos = -1;
        ConditionData data = null;
        if (peekToken() == 113) {
            consumeToken();
            ifPos = this.ts.tokenBeg - pos;
            data = condition();
        }
        if (!inFunctionParams) {
            mustMatchToken(89, "msg.no.paren.let", true);
        }
        GeneratorExpression pn = new GeneratorExpression(pos, this.ts.tokenEnd - pos);
        pn.setResult(result);
        pn.setLoops(loops);
        if (data != null) {
            pn.setIfPosition(ifPos);
            pn.setFilter(data.condition);
            pn.setFilterLp(data.lp - pos);
            pn.setFilterRp(data.rp - pos);
        }
        return pn;
    }

    private GeneratorExpressionLoop generatorExpressionLoop() throws IOException {
        if (nextToken() != 120) {
            codeBug();
        }
        int pos = this.ts.tokenBeg;
        int lp = -1;
        int rp = -1;
        int inPos = -1;
        GeneratorExpressionLoop pn = new GeneratorExpressionLoop(pos);
        pushScope(pn);
        try {
            if (mustMatchToken(88, "msg.no.paren.for", true)) {
                lp = this.ts.tokenBeg - pos;
            }
            AstNode iter = null;
            switch (peekToken()) {
                case 39:
                    consumeToken();
                    iter = createNameNode();
                    break;
                case 84:
                case 86:
                    iter = destructuringPrimaryExpr();
                    markDestructuring(iter);
                    break;
                default:
                    reportError("msg.bad.var");
                    break;
            }
            if (iter.getType() == 39) {
                defineSymbol(154, this.ts.getString(), true);
            }
            if (mustMatchToken(52, "msg.in.after.for.name", true)) {
                inPos = this.ts.tokenBeg - pos;
            }
            AstNode obj = expr();
            if (mustMatchToken(89, "msg.no.paren.for.ctrl", true)) {
                rp = this.ts.tokenBeg - pos;
            }
            pn.setLength(this.ts.tokenEnd - pos);
            pn.setIterator(iter);
            pn.setIteratedObject(obj);
            pn.setInPosition(inPos);
            pn.setParens(lp, rp);
            return pn;
        } finally {
            popScope();
        }
    }

    private ObjectLiteral objectLiteral() throws IOException {
        int pos = this.ts.tokenBeg;
        int lineno = this.ts.lineno;
        int afterComma = -1;
        List<ObjectProperty> elems = new ArrayList<>();
        Set<String> getterNames = null;
        Set<String> setterNames = null;
        if (this.inUseStrictDirective) {
            getterNames = new HashSet<>();
            setterNames = new HashSet<>();
        }
        Comment objJsdocNode = getAndResetJsDoc();
        while (true) {
            String propertyName = null;
            int entryKind = 1;
            int tt = peekToken();
            Comment jsdocNode = getAndResetJsDoc();
            if (tt == 162) {
                consumeToken();
                tt = peekUntilNonComment(tt);
            }
            if (tt == 87) {
                if (afterComma != -1) {
                    warnTrailingComma(pos, elems, afterComma);
                }
            } else {
                AstNode pname = objliteralProperty();
                if (pname == null) {
                    reportError("msg.bad.prop");
                } else {
                    propertyName = this.ts.getString();
                    int ppos = this.ts.tokenBeg;
                    consumeToken();
                    int peeked = peekToken();
                    if (peeked != 90 && peeked != 104 && peeked != 87) {
                        if (peeked == 88) {
                            entryKind = 8;
                        } else if (pname.getType() == 39) {
                            if ("get".equals(propertyName)) {
                                entryKind = 2;
                            } else if ("set".equals(propertyName)) {
                                entryKind = 4;
                            }
                        }
                        if (entryKind == 2 || entryKind == 4) {
                            pname = objliteralProperty();
                            if (pname == null) {
                                reportError("msg.bad.prop");
                            }
                            consumeToken();
                        }
                        if (pname == null) {
                            propertyName = null;
                        } else {
                            propertyName = this.ts.getString();
                            ObjectProperty objectProp = methodDefinition(ppos, pname, entryKind);
                            pname.setJsDocNode(jsdocNode);
                            elems.add(objectProp);
                        }
                    } else {
                        pname.setJsDocNode(jsdocNode);
                        elems.add(plainProperty(pname, tt));
                    }
                }
                if (this.inUseStrictDirective && propertyName != null) {
                    switch (entryKind) {
                        case 1:
                        case 8:
                            if (getterNames.contains(propertyName) || setterNames.contains(propertyName)) {
                                addError("msg.dup.obj.lit.prop.strict", propertyName);
                            }
                            getterNames.add(propertyName);
                            setterNames.add(propertyName);
                            break;
                        case 2:
                            if (getterNames.contains(propertyName)) {
                                addError("msg.dup.obj.lit.prop.strict", propertyName);
                            }
                            getterNames.add(propertyName);
                            break;
                        case 4:
                            if (setterNames.contains(propertyName)) {
                                addError("msg.dup.obj.lit.prop.strict", propertyName);
                            }
                            setterNames.add(propertyName);
                            break;
                    }
                }
                getAndResetJsDoc();
                if (matchToken(90, true)) {
                    afterComma = this.ts.tokenEnd;
                }
            }
        }
        mustMatchToken(87, "msg.no.brace.prop", true);
        ObjectLiteral pn = new ObjectLiteral(pos, this.ts.tokenEnd - pos);
        if (objJsdocNode != null) {
            pn.setJsDocNode(objJsdocNode);
        }
        pn.setElements(elems);
        pn.setLineno(lineno);
        return pn;
    }

    private AstNode objliteralProperty() throws IOException {
        AstNode pname;
        int tt = peekToken();
        switch (tt) {
            case 39:
                pname = createNameNode();
                break;
            case 40:
                pname = new NumberLiteral(this.ts.tokenBeg, this.ts.getString(), this.ts.getNumber());
                break;
            case 41:
                pname = createStringLiteral();
                break;
            default:
                if (this.compilerEnv.isReservedKeywordAsIdentifier() && TokenStream.isKeyword(this.ts.getString(), this.compilerEnv.getLanguageVersion(), this.inUseStrictDirective)) {
                    pname = createNameNode();
                } else {
                    return null;
                }
                break;
        }
        return pname;
    }

    private ObjectProperty plainProperty(AstNode property, int ptt) throws IOException {
        int tt = peekToken();
        if ((tt == 90 || tt == 87) && ptt == 39 && this.compilerEnv.getLanguageVersion() >= 180) {
            if (!this.inDestructuringAssignment) {
                reportError("msg.bad.object.init");
            }
            AstNode nn = new Name(property.getPosition(), property.getString());
            ObjectProperty pn = new ObjectProperty();
            pn.putProp(26, Boolean.TRUE);
            pn.setLeftAndRight(property, nn);
            return pn;
        }
        mustMatchToken(104, "msg.no.colon.prop", true);
        ObjectProperty pn2 = new ObjectProperty();
        pn2.setOperatorPosition(this.ts.tokenBeg);
        pn2.setLeftAndRight(property, assignExpr());
        return pn2;
    }

    private ObjectProperty methodDefinition(int pos, AstNode propName, int entryKind) throws IOException {
        FunctionNode fn = function(2);
        Name name = fn.getFunctionName();
        if (name != null && name.length() != 0) {
            reportError("msg.bad.prop");
        }
        ObjectProperty pn = new ObjectProperty(pos);
        switch (entryKind) {
            case 2:
                pn.setIsGetterMethod();
                fn.setFunctionIsGetterMethod();
                break;
            case 4:
                pn.setIsSetterMethod();
                fn.setFunctionIsSetterMethod();
                break;
            case 8:
                pn.setIsNormalMethod();
                fn.setFunctionIsNormalMethod();
                break;
        }
        int end = getNodeEnd(fn);
        pn.setLeft(propName);
        pn.setRight(fn);
        pn.setLength(end - pos);
        return pn;
    }

    private Name createNameNode() {
        return createNameNode(false, 39);
    }

    private Name createNameNode(boolean checkActivation, int token) {
        int beg = this.ts.tokenBeg;
        String s = this.ts.getString();
        int lineno = this.ts.lineno;
        if (!"".equals(this.prevNameTokenString)) {
            beg = this.prevNameTokenStart;
            s = this.prevNameTokenString;
            lineno = this.prevNameTokenLineno;
            this.prevNameTokenStart = 0;
            this.prevNameTokenString = "";
            this.prevNameTokenLineno = 0;
        }
        if (s == null) {
            if (this.compilerEnv.isIdeMode()) {
                s = "";
            } else {
                codeBug();
            }
        }
        Name name = new Name(beg, s);
        name.setLineno(lineno);
        if (checkActivation) {
            checkActivationName(s, token);
        }
        return name;
    }

    private StringLiteral createStringLiteral() {
        int pos = this.ts.tokenBeg;
        int end = this.ts.tokenEnd;
        StringLiteral s = new StringLiteral(pos, end - pos);
        s.setLineno(this.ts.lineno);
        s.setValue(this.ts.getString());
        s.setQuoteCharacter(this.ts.getQuoteChar());
        return s;
    }

    protected void checkActivationName(String name, int token) {
        if (!insideFunction()) {
            return;
        }
        boolean activation = false;
        if ("arguments".equals(name) && ((FunctionNode) this.currentScriptOrFn).getFunctionType() != 4) {
            activation = true;
        } else if (this.compilerEnv.getActivationNames() != null && this.compilerEnv.getActivationNames().contains(name)) {
            activation = true;
        } else if (XSDatatype.FACET_LENGTH.equals(name) && token == 33 && this.compilerEnv.getLanguageVersion() == 120) {
            activation = true;
        }
        if (activation) {
            setRequiresActivation();
        }
    }

    protected void setRequiresActivation() {
        if (insideFunction()) {
            ((FunctionNode) this.currentScriptOrFn).setRequiresActivation();
        }
    }

    private void checkCallRequiresActivation(AstNode pn) {
        if ((pn.getType() == 39 && "eval".equals(((Name) pn).getIdentifier())) || (pn.getType() == 33 && "eval".equals(((PropertyGet) pn).getProperty().getIdentifier()))) {
            setRequiresActivation();
        }
    }

    protected void setIsGenerator() {
        if (insideFunction()) {
            ((FunctionNode) this.currentScriptOrFn).setIsGenerator();
        }
    }

    private void checkBadIncDec(UnaryExpression expr) {
        AstNode op = removeParens(expr.getOperand());
        int tt = op.getType();
        if (tt != 39 && tt != 33 && tt != 36 && tt != 68 && tt != 38) {
            reportError(expr.getType() == 107 ? "msg.bad.incr" : "msg.bad.decr");
        }
    }

    private ErrorNode makeErrorNode() {
        ErrorNode pn = new ErrorNode(this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
        pn.setLineno(this.ts.lineno);
        return pn;
    }

    private static int nodeEnd(AstNode node) {
        return node.getPosition() + node.getLength();
    }

    private void saveNameTokenData(int pos, String name, int lineno) {
        this.prevNameTokenStart = pos;
        this.prevNameTokenString = name;
        this.prevNameTokenLineno = lineno;
    }

    private int lineBeginningFor(int pos) {
        char c;
        if (this.sourceChars == null) {
            return -1;
        }
        if (pos <= 0) {
            return 0;
        }
        char[] buf = this.sourceChars;
        if (pos >= buf.length) {
            pos = buf.length - 1;
        }
        do {
            pos--;
            if (pos >= 0) {
                c = buf[pos];
            } else {
                return 0;
            }
        } while (!ScriptRuntime.isJSLineTerminator(c));
        return pos + 1;
    }

    private void warnMissingSemi(int pos, int end) {
        if (this.compilerEnv.isStrictMode()) {
            int[] linep = new int[2];
            String line = this.ts.getLine(end, linep);
            int beg = this.compilerEnv.isIdeMode() ? Math.max(pos, end - linep[1]) : pos;
            if (line != null) {
                addStrictWarning("msg.missing.semi", "", beg, end - beg, linep[0], line, linep[1]);
            } else {
                addStrictWarning("msg.missing.semi", "", beg, end - beg);
            }
        }
    }

    private void warnTrailingComma(int pos, List<?> elems, int commaPos) {
        if (this.compilerEnv.getWarnTrailingComma()) {
            if (!elems.isEmpty()) {
                pos = ((AstNode) elems.get(0)).getPosition();
            }
            int pos2 = Math.max(pos, lineBeginningFor(commaPos));
            addWarning("msg.extra.trailing.comma", pos2, commaPos - pos2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/Parser$PerFunctionVariables.class */
    protected class PerFunctionVariables {
        private ScriptNode savedCurrentScriptOrFn;
        private Scope savedCurrentScope;
        private int savedEndFlags;
        private boolean savedInForInit;
        private Map<String, LabeledStatement> savedLabelSet;
        private List<Loop> savedLoopSet;
        private List<Jump> savedLoopAndSwitchSet;

        PerFunctionVariables(FunctionNode fnNode) {
            this.savedCurrentScriptOrFn = Parser.this.currentScriptOrFn;
            Parser.this.currentScriptOrFn = fnNode;
            this.savedCurrentScope = Parser.this.currentScope;
            Parser.this.currentScope = fnNode;
            this.savedLabelSet = Parser.this.labelSet;
            Parser.this.labelSet = null;
            this.savedLoopSet = Parser.this.loopSet;
            Parser.this.loopSet = null;
            this.savedLoopAndSwitchSet = Parser.this.loopAndSwitchSet;
            Parser.this.loopAndSwitchSet = null;
            this.savedEndFlags = Parser.this.endFlags;
            Parser.this.endFlags = 0;
            this.savedInForInit = Parser.this.inForInit;
            Parser.this.inForInit = false;
        }

        void restore() {
            Parser.this.currentScriptOrFn = this.savedCurrentScriptOrFn;
            Parser.this.currentScope = this.savedCurrentScope;
            Parser.this.labelSet = this.savedLabelSet;
            Parser.this.loopSet = this.savedLoopSet;
            Parser.this.loopAndSwitchSet = this.savedLoopAndSwitchSet;
            Parser.this.endFlags = this.savedEndFlags;
            Parser.this.inForInit = this.savedInForInit;
        }
    }

    Node createDestructuringAssignment(int type, Node left, Node right) {
        String tempName = this.currentScriptOrFn.getNextTempName();
        Node result = destructuringAssignmentHelper(type, left, right, tempName);
        Node comma = result.getLastChild();
        comma.addChildToBack(createName(tempName));
        return result;
    }

    Node destructuringAssignmentHelper(int variableType, Node left, Node right, String tempName) {
        Scope result = createScopeNode(159, left.getLineno());
        result.addChildToFront(new Node(154, createName(39, tempName, right)));
        try {
            pushScope(result);
            defineSymbol(154, tempName, true);
            popScope();
            Node comma = new Node(90);
            result.addChildToBack(comma);
            List<String> destructuringNames = new ArrayList<>();
            boolean empty = true;
            switch (left.getType()) {
                case 33:
                case 36:
                    switch (variableType) {
                        case 123:
                        case 154:
                        case 155:
                            reportError("msg.bad.assign.left");
                            break;
                    }
                    comma.addChildToBack(simpleAssignment(left, createName(tempName)));
                    break;
                case 66:
                    empty = destructuringArray((ArrayLiteral) left, variableType, tempName, comma, destructuringNames);
                    break;
                case 67:
                    empty = destructuringObject((ObjectLiteral) left, variableType, tempName, comma, destructuringNames);
                    break;
                default:
                    reportError("msg.bad.assign.left");
                    break;
            }
            if (empty) {
                comma.addChildToBack(createNumber(0.0d));
            }
            result.putProp(22, destructuringNames);
            return result;
        } catch (Throwable th) {
            popScope();
            throw th;
        }
    }

    boolean destructuringArray(ArrayLiteral array, int variableType, String tempName, Node parent, List<String> destructuringNames) {
        boolean empty = true;
        int setOp = variableType == 155 ? 156 : 8;
        int index = 0;
        for (AstNode n : array.getElements()) {
            if (n.getType() == 129) {
                index++;
            } else {
                Node rightElem = new Node(36, createName(tempName), createNumber(index));
                if (n.getType() == 39) {
                    String name = n.getString();
                    parent.addChildToBack(new Node(setOp, createName(49, name, null), rightElem));
                    if (variableType != -1) {
                        defineSymbol(variableType, name, true);
                        destructuringNames.add(name);
                    }
                } else {
                    parent.addChildToBack(destructuringAssignmentHelper(variableType, n, rightElem, this.currentScriptOrFn.getNextTempName()));
                }
                index++;
                empty = false;
            }
        }
        return empty;
    }

    boolean destructuringObject(ObjectLiteral node, int variableType, String tempName, Node parent, List<String> destructuringNames) {
        Node rightElem;
        boolean empty = true;
        int setOp = variableType == 155 ? 156 : 8;
        for (ObjectProperty prop : node.getElements()) {
            int lineno = 0;
            if (this.ts != null) {
                lineno = this.ts.lineno;
            }
            AstNode id = prop.getLeft();
            if (id instanceof Name) {
                Node s = Node.newString(((Name) id).getIdentifier());
                rightElem = new Node(33, createName(tempName), s);
            } else if (id instanceof StringLiteral) {
                Node s2 = Node.newString(((StringLiteral) id).getValue());
                rightElem = new Node(33, createName(tempName), s2);
            } else if (id instanceof NumberLiteral) {
                Node s3 = createNumber((int) ((NumberLiteral) id).getNumber());
                rightElem = new Node(36, createName(tempName), s3);
            } else {
                throw codeBug();
            }
            rightElem.setLineno(lineno);
            AstNode value = prop.getRight();
            if (value.getType() == 39) {
                String name = ((Name) value).getIdentifier();
                parent.addChildToBack(new Node(setOp, createName(49, name, null), rightElem));
                if (variableType != -1) {
                    defineSymbol(variableType, name, true);
                    destructuringNames.add(name);
                }
            } else {
                parent.addChildToBack(destructuringAssignmentHelper(variableType, value, rightElem, this.currentScriptOrFn.getNextTempName()));
            }
            empty = false;
        }
        return empty;
    }

    protected Node createName(String name) {
        checkActivationName(name, 39);
        return Node.newString(39, name);
    }

    protected Node createName(int type, String name, Node child) {
        Node result = createName(name);
        result.setType(type);
        if (child != null) {
            result.addChildToBack(child);
        }
        return result;
    }

    protected Node createNumber(double number) {
        return Node.newNumber(number);
    }

    protected Scope createScopeNode(int token, int lineno) {
        Scope scope = new Scope();
        scope.setType(token);
        scope.setLineno(lineno);
        return scope;
    }

    protected Node simpleAssignment(Node left, Node right) {
        Node obj;
        Node id;
        int type;
        int nodeType = left.getType();
        switch (nodeType) {
            case 33:
            case 36:
                if (left instanceof PropertyGet) {
                    obj = ((PropertyGet) left).getTarget();
                    id = ((PropertyGet) left).getProperty();
                } else if (left instanceof ElementGet) {
                    obj = ((ElementGet) left).getTarget();
                    id = ((ElementGet) left).getElement();
                } else {
                    obj = left.getFirstChild();
                    id = left.getLastChild();
                }
                if (nodeType == 33) {
                    type = 35;
                    id.setType(41);
                } else {
                    type = 37;
                }
                return new Node(type, obj, id, right);
            case 39:
                String name = ((Name) left).getIdentifier();
                if (this.inUseStrictDirective && ("eval".equals(name) || "arguments".equals(name))) {
                    reportError("msg.bad.id.strict", name);
                }
                left.setType(49);
                return new Node(8, left, right);
            case 68:
                Node ref = left.getFirstChild();
                checkMutableReference(ref);
                return new Node(69, ref, right);
            default:
                throw codeBug();
        }
    }

    protected void checkMutableReference(Node n) {
        int memberTypeFlags = n.getIntProp(16, 0);
        if ((memberTypeFlags & 4) != 0) {
            reportError("msg.bad.assign.left");
        }
    }

    protected AstNode removeParens(AstNode node) {
        while (node instanceof ParenthesizedExpression) {
            node = ((ParenthesizedExpression) node).getExpression();
        }
        return node;
    }

    /* JADX WARN: Multi-variable type inference failed */
    void markDestructuring(AstNode astNode) {
        if (astNode instanceof DestructuringForm) {
            ((DestructuringForm) astNode).setIsDestructuring(true);
        } else if (astNode instanceof ParenthesizedExpression) {
            markDestructuring(((ParenthesizedExpression) astNode).getExpression());
        }
    }

    private RuntimeException codeBug() throws RuntimeException {
        throw Kit.codeBug("ts.cursor=" + this.ts.cursor + ", ts.tokenBeg=" + this.ts.tokenBeg + ", currentToken=" + this.currentToken);
    }

    public void setDefaultUseStrictDirective(boolean useStrict) {
        this.defaultUseStrictDirective = useStrict;
    }

    public boolean inUseStrictDirective() {
        return this.inUseStrictDirective;
    }
}
