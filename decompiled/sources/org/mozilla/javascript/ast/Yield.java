package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/Yield.class */
public class Yield extends AstNode {
    private AstNode value;

    public Yield() {
        this.type = 73;
    }

    public Yield(int pos) {
        super(pos);
        this.type = 73;
    }

    public Yield(int pos, int len) {
        super(pos, len);
        this.type = 73;
    }

    public Yield(int pos, int len, AstNode value, boolean isStar) {
        super(pos, len);
        this.type = isStar ? 166 : 73;
        setValue(value);
    }

    public AstNode getValue() {
        return this.value;
    }

    public void setValue(AstNode expr) {
        this.value = expr;
        if (expr != null) {
            expr.setParent(this);
        }
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return this.value == null ? "yield" : "yield " + this.value.toSource(0);
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this) && this.value != null) {
            this.value.visit(v);
        }
    }
}
