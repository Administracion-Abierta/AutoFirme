package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/DoLoop.class */
public class DoLoop extends Loop {
    private AstNode condition;
    private int whilePosition;

    public DoLoop() {
        this.whilePosition = -1;
        this.type = 119;
    }

    public DoLoop(int pos) {
        super(pos);
        this.whilePosition = -1;
        this.type = 119;
    }

    public DoLoop(int pos, int len) {
        super(pos, len);
        this.whilePosition = -1;
        this.type = 119;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public void setCondition(AstNode condition) {
        assertNotNull(condition);
        this.condition = condition;
        condition.setParent(this);
    }

    public int getWhilePosition() {
        return this.whilePosition;
    }

    public void setWhilePosition(int whilePosition) {
        this.whilePosition = whilePosition;
    }

    @Override // org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        sb.append("do ");
        if (getInlineComment() != null) {
            sb.append(getInlineComment().toSource(depth + 1)).append("\n");
        }
        sb.append(this.body.toSource(depth).trim());
        sb.append(" while (");
        sb.append(this.condition.toSource(0));
        sb.append(");\n");
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.body.visit(v);
            this.condition.visit(v);
        }
    }
}
