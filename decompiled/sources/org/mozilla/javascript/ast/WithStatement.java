package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/WithStatement.class */
public class WithStatement extends AstNode {
    private AstNode expression;
    private AstNode statement;
    private int lp;
    private int rp;

    public WithStatement() {
        this.lp = -1;
        this.rp = -1;
        this.type = 124;
    }

    public WithStatement(int pos) {
        super(pos);
        this.lp = -1;
        this.rp = -1;
        this.type = 124;
    }

    public WithStatement(int pos, int len) {
        super(pos, len);
        this.lp = -1;
        this.rp = -1;
        this.type = 124;
    }

    public AstNode getExpression() {
        return this.expression;
    }

    public void setExpression(AstNode expression) {
        assertNotNull(expression);
        this.expression = expression;
        expression.setParent(this);
    }

    public AstNode getStatement() {
        return this.statement;
    }

    public void setStatement(AstNode statement) {
        assertNotNull(statement);
        this.statement = statement;
        statement.setParent(this);
    }

    public int getLp() {
        return this.lp;
    }

    public void setLp(int lp) {
        this.lp = lp;
    }

    public int getRp() {
        return this.rp;
    }

    public void setRp(int rp) {
        this.rp = rp;
    }

    public void setParens(int lp, int rp) {
        this.lp = lp;
        this.rp = rp;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        sb.append("with (");
        sb.append(this.expression.toSource(0));
        sb.append(") ");
        if (getInlineComment() != null) {
            sb.append(getInlineComment().toSource(depth + 1));
        }
        if (this.statement.getType() == 130) {
            if (getInlineComment() != null) {
                sb.append("\n");
            }
            sb.append(this.statement.toSource(depth).trim());
            sb.append("\n");
        } else {
            sb.append("\n").append(this.statement.toSource(depth + 1));
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.expression.visit(v);
            this.statement.visit(v);
        }
    }
}
