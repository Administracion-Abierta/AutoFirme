package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/BreakStatement.class */
public class BreakStatement extends Jump {
    private Name breakLabel;
    private AstNode target;

    public BreakStatement() {
        this.type = 121;
    }

    public BreakStatement(int pos) {
        this.type = 121;
        this.position = pos;
    }

    public BreakStatement(int pos, int len) {
        this.type = 121;
        this.position = pos;
        this.length = len;
    }

    public Name getBreakLabel() {
        return this.breakLabel;
    }

    public void setBreakLabel(Name label) {
        this.breakLabel = label;
        if (label != null) {
            label.setParent(this);
        }
    }

    public AstNode getBreakTarget() {
        return this.target;
    }

    public void setBreakTarget(Jump target) {
        assertNotNull(target);
        this.target = target;
        setJumpStatement(target);
    }

    @Override // org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        sb.append("break");
        if (this.breakLabel != null) {
            sb.append(" ");
            sb.append(this.breakLabel.toSource(0));
        }
        sb.append(";\n");
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this) && this.breakLabel != null) {
            this.breakLabel.visit(v);
        }
    }
}
