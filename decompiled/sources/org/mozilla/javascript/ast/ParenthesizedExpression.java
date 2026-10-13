package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ParenthesizedExpression.class */
public class ParenthesizedExpression extends AstNode {
    private AstNode expression;

    public ParenthesizedExpression() {
        this.type = 88;
    }

    public ParenthesizedExpression(int pos) {
        super(pos);
        this.type = 88;
    }

    public ParenthesizedExpression(int pos, int len) {
        super(pos, len);
        this.type = 88;
    }

    public ParenthesizedExpression(AstNode expr) {
        this(expr != null ? expr.getPosition() : 0, expr != null ? expr.getLength() : 1, expr);
    }

    public ParenthesizedExpression(int pos, int len, AstNode expr) {
        super(pos, len);
        this.type = 88;
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
        return makeIndent(depth) + "(" + this.expression.toSource(0) + ")";
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.expression.visit(v);
        }
    }
}
