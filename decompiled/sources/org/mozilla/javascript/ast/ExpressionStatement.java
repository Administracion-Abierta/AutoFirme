package org.mozilla.javascript.ast;

import es.gob.afirma.keystores.filters.PolicyIdFilter;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ExpressionStatement.class */
public class ExpressionStatement extends AstNode {
    private AstNode expr;

    public void setHasResult() {
        this.type = 135;
    }

    public ExpressionStatement() {
        this.type = 134;
    }

    public ExpressionStatement(AstNode expr, boolean hasResult) {
        this(expr);
        if (hasResult) {
            setHasResult();
        }
    }

    public ExpressionStatement(AstNode expr) {
        this(expr.getPosition(), expr.getLength(), expr);
    }

    public ExpressionStatement(int pos, int len) {
        super(pos, len);
        this.type = 134;
    }

    public ExpressionStatement(int pos, int len, AstNode expr) {
        super(pos, len);
        this.type = 134;
        setExpression(expr);
    }

    public AstNode getExpression() {
        return this.expr;
    }

    public void setExpression(AstNode expression) {
        assertNotNull(expression);
        this.expr = expression;
        expression.setParent(this);
        setLineno(expression.getLineno());
    }

    @Override // org.mozilla.javascript.ast.AstNode, org.mozilla.javascript.Node
    public boolean hasSideEffects() {
        return this.type == 135 || this.expr.hasSideEffects();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.expr.toSource(depth));
        sb.append(PolicyIdFilter.OID_SEPARATOR);
        if (getInlineComment() != null) {
            sb.append(getInlineComment().toSource(depth));
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.expr.visit(v);
        }
    }
}
