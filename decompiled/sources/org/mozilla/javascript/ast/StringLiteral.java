package org.mozilla.javascript.ast;

import org.mozilla.javascript.ScriptRuntime;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/StringLiteral.class */
public class StringLiteral extends AstNode {
    private String value;
    private char quoteChar;

    public StringLiteral() {
        this.type = 41;
    }

    public StringLiteral(int pos) {
        super(pos);
        this.type = 41;
    }

    public StringLiteral(int pos, int len) {
        super(pos, len);
        this.type = 41;
    }

    public String getValue() {
        return this.value;
    }

    public String getValue(boolean includeQuotes) {
        if (!includeQuotes) {
            return this.value;
        }
        return this.quoteChar + this.value + this.quoteChar;
    }

    public void setValue(String value) {
        assertNotNull(value);
        this.value = value;
    }

    public char getQuoteCharacter() {
        return this.quoteChar;
    }

    public void setQuoteCharacter(char c) {
        this.quoteChar = c;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + this.quoteChar + ScriptRuntime.escapeString(this.value, this.quoteChar) + this.quoteChar;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}
