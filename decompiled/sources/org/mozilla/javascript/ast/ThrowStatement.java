package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ThrowStatement.class */
public class ThrowStatement extends AstNode {
    private AstNode expression;

    public ThrowStatement() {
        this.type = 50;
    }

    public ThrowStatement(int pos) {
        super(pos);
        this.type = 50;
    }

    public ThrowStatement(int pos, int len) {
        super(pos, len);
        this.type = 50;
    }

    public ThrowStatement(AstNode expr) {
        this.type = 50;
        setExpression(expr);
    }

    public ThrowStatement(int pos, AstNode expr) {
        super(pos, expr.getLength());
        this.type = 50;
        setExpression(expr);
    }

    public ThrowStatement(int pos, int len, AstNode expr) {
        super(pos, len);
        this.type = 50;
        setExpression(expr);
    }

    public AstNode getExpression() {
        return this.expression;
    }

    public void setExpression(AstNode expression) {
        assertNotNull(expression);
        this.expression = expression;
        expression.setParent(this);
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + "throw " + this.expression.toSource(0) + ";\n";
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.expression.visit(v);
        }
    }
}
