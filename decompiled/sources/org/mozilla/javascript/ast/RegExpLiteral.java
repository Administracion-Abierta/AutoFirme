package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/RegExpLiteral.class */
public class RegExpLiteral extends AstNode {
    private String value;
    private String flags;

    public RegExpLiteral() {
        this.type = 48;
    }

    public RegExpLiteral(int pos) {
        super(pos);
        this.type = 48;
    }

    public RegExpLiteral(int pos, int len) {
        super(pos, len);
        this.type = 48;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        assertNotNull(value);
        this.value = value;
    }

    public String getFlags() {
        return this.flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + "/" + this.value + "/" + (this.flags == null ? "" : this.flags);
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}
