package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ContinueStatement.class */
public class ContinueStatement extends Jump {
    private Name label;
    private Loop target;

    public ContinueStatement() {
        this.type = 122;
    }

    public ContinueStatement(int pos) {
        this(pos, -1);
    }

    public ContinueStatement(int pos, int len) {
        this.type = 122;
        this.position = pos;
        this.length = len;
    }

    public ContinueStatement(Name label) {
        this.type = 122;
        setLabel(label);
    }

    public ContinueStatement(int pos, Name label) {
        this(pos);
        setLabel(label);
    }

    public ContinueStatement(int pos, int len, Name label) {
        this(pos, len);
        setLabel(label);
    }

    public Loop getTarget() {
        return this.target;
    }

    public void setTarget(Loop target) {
        assertNotNull(target);
        this.target = target;
        setJumpStatement(target);
    }

    public Name getLabel() {
        return this.label;
    }

    public void setLabel(Name label) {
        this.label = label;
        if (label != null) {
            label.setParent(this);
        }
    }

    @Override // org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        sb.append("continue");
        if (this.label != null) {
            sb.append(" ");
            sb.append(this.label.toSource(0));
        }
        sb.append(";\n");
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this) && this.label != null) {
            this.label.visit(v);
        }
    }
}
