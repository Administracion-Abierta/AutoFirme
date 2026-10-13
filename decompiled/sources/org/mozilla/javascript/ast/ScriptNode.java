package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mozilla.javascript.Node;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ScriptNode.class */
public class ScriptNode extends Scope {
    private int encodedSourceStart;
    private int encodedSourceEnd;
    private String sourceName;
    private String encodedSource;
    private int endLineno;
    private List<FunctionNode> functions;
    private List<RegExpLiteral> regexps;
    private List<FunctionNode> EMPTY_LIST;
    private List<Symbol> symbols;
    private int paramCount;
    private String[] variableNames;
    private boolean[] isConsts;
    private Object compilerData;
    private int tempNumber;
    private boolean inStrictMode;

    public ScriptNode() {
        this.encodedSourceStart = -1;
        this.encodedSourceEnd = -1;
        this.endLineno = -1;
        this.EMPTY_LIST = Collections.emptyList();
        this.symbols = new ArrayList(4);
        this.paramCount = 0;
        this.tempNumber = 0;
        this.top = this;
        this.type = 137;
    }

    public ScriptNode(int pos) {
        super(pos);
        this.encodedSourceStart = -1;
        this.encodedSourceEnd = -1;
        this.endLineno = -1;
        this.EMPTY_LIST = Collections.emptyList();
        this.symbols = new ArrayList(4);
        this.paramCount = 0;
        this.tempNumber = 0;
        this.top = this;
        this.type = 137;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public int getEncodedSourceStart() {
        return this.encodedSourceStart;
    }

    public void setEncodedSourceStart(int start) {
        this.encodedSourceStart = start;
    }

    public int getEncodedSourceEnd() {
        return this.encodedSourceEnd;
    }

    public void setEncodedSourceEnd(int end) {
        this.encodedSourceEnd = end;
    }

    public void setEncodedSourceBounds(int start, int end) {
        this.encodedSourceStart = start;
        this.encodedSourceEnd = end;
    }

    public void setEncodedSource(String encodedSource) {
        this.encodedSource = encodedSource;
    }

    public String getEncodedSource() {
        return this.encodedSource;
    }

    public int getBaseLineno() {
        return this.lineno;
    }

    public void setBaseLineno(int lineno) {
        if (lineno < 0 || this.lineno >= 0) {
            codeBug();
        }
        this.lineno = lineno;
    }

    public int getEndLineno() {
        return this.endLineno;
    }

    public void setEndLineno(int lineno) {
        if (lineno < 0 || this.endLineno >= 0) {
            codeBug();
        }
        this.endLineno = lineno;
    }

    public int getFunctionCount() {
        if (this.functions == null) {
            return 0;
        }
        return this.functions.size();
    }

    public FunctionNode getFunctionNode(int i) {
        return this.functions.get(i);
    }

    public List<FunctionNode> getFunctions() {
        return this.functions == null ? this.EMPTY_LIST : this.functions;
    }

    public int addFunction(FunctionNode fnNode) {
        if (fnNode == null) {
            codeBug();
        }
        if (this.functions == null) {
            this.functions = new ArrayList();
        }
        this.functions.add(fnNode);
        return this.functions.size() - 1;
    }

    public int getRegexpCount() {
        if (this.regexps == null) {
            return 0;
        }
        return this.regexps.size();
    }

    public String getRegexpString(int index) {
        return this.regexps.get(index).getValue();
    }

    public String getRegexpFlags(int index) {
        return this.regexps.get(index).getFlags();
    }

    public void addRegExp(RegExpLiteral re) {
        if (re == null) {
            codeBug();
        }
        if (this.regexps == null) {
            this.regexps = new ArrayList();
        }
        this.regexps.add(re);
        re.putIntProp(4, this.regexps.size() - 1);
    }

    public int getIndexForNameNode(Node nameNode) {
        if (this.variableNames == null) {
            codeBug();
        }
        Scope node = nameNode.getScope();
        Symbol symbol = null;
        if (node != null && (nameNode instanceof Name)) {
            symbol = node.getSymbol(((Name) nameNode).getIdentifier());
        }
        if (symbol == null) {
            return -1;
        }
        return symbol.getIndex();
    }

    public String getParamOrVarName(int index) {
        if (this.variableNames == null) {
            codeBug();
        }
        return this.variableNames[index];
    }

    public int getParamCount() {
        return this.paramCount;
    }

    public int getParamAndVarCount() {
        if (this.variableNames == null) {
            codeBug();
        }
        return this.symbols.size();
    }

    public String[] getParamAndVarNames() {
        if (this.variableNames == null) {
            codeBug();
        }
        return this.variableNames;
    }

    public boolean[] getParamAndVarConst() {
        if (this.variableNames == null) {
            codeBug();
        }
        return this.isConsts;
    }

    void addSymbol(Symbol symbol) {
        if (this.variableNames != null) {
            codeBug();
        }
        if (symbol.getDeclType() == 88) {
            this.paramCount++;
        }
        this.symbols.add(symbol);
    }

    public List<Symbol> getSymbols() {
        return this.symbols;
    }

    public void setSymbols(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public void flattenSymbolTable(boolean flattenAllTables) {
        if (!flattenAllTables) {
            List<Symbol> newSymbols = new ArrayList<>();
            if (this.symbolTable != null) {
                for (int i = 0; i < this.symbols.size(); i++) {
                    Symbol symbol = this.symbols.get(i);
                    if (symbol.getContainingTable() == this) {
                        newSymbols.add(symbol);
                    }
                }
            }
            this.symbols = newSymbols;
        }
        this.variableNames = new String[this.symbols.size()];
        this.isConsts = new boolean[this.symbols.size()];
        for (int i2 = 0; i2 < this.symbols.size(); i2++) {
            Symbol symbol2 = this.symbols.get(i2);
            this.variableNames[i2] = symbol2.getName();
            this.isConsts[i2] = symbol2.getDeclType() == 155;
            symbol2.setIndex(i2);
        }
    }

    public Object getCompilerData() {
        return this.compilerData;
    }

    public void setCompilerData(Object data) {
        assertNotNull(data);
        if (this.compilerData != null) {
            throw new IllegalStateException();
        }
        this.compilerData = data;
    }

    public String getNextTempName() {
        StringBuilder sbAppend = new StringBuilder().append("$");
        int i = this.tempNumber;
        this.tempNumber = i + 1;
        return sbAppend.append(i).toString();
    }

    public void setInStrictMode(boolean inStrictMode) {
        this.inStrictMode = inStrictMode;
    }

    public boolean isInStrictMode() {
        return this.inStrictMode;
    }

    @Override // org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            for (Node kid : this) {
                ((AstNode) kid).visit(v);
            }
        }
    }
}
