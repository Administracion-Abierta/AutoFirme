package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ForLoop.class */
public class ForLoop extends Loop {
    private AstNode initializer;
    private AstNode condition;
    private AstNode increment;

    public ForLoop() {
        this.type = 120;
    }

    public ForLoop(int pos) {
        super(pos);
        this.type = 120;
    }

    public ForLoop(int pos, int len) {
        super(pos, len);
        this.type = 120;
    }

    public AstNode getInitializer() {
        return this.initializer;
    }

    public void setInitializer(AstNode initializer) {
        assertNotNull(initializer);
        this.initializer = initializer;
        initializer.setParent(this);
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public void setCondition(AstNode condition) {
        assertNotNull(condition);
        this.condition = condition;
        condition.setParent(this);
    }

    public AstNode getIncrement() {
        return this.increment;
    }

    public void setIncrement(AstNode increment) {
        assertNotNull(increment);
        this.increment = increment;
        increment.setParent(this);
    }

    @Override // org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        sb.append("for (");
        sb.append(this.initializer.toSource(0));
        sb.append("; ");
        sb.append(this.condition.toSource(0));
        sb.append("; ");
        sb.append(this.increment.toSource(0));
        sb.append(") ");
        if (getInlineComment() != null) {
            sb.append(getInlineComment().toSource()).append("\n");
        }
        if (this.body.getType() == 130) {
            String bodySource = this.body.toSource(depth);
            if (getInlineComment() == null) {
                bodySource = bodySource.trim();
            }
            sb.append(bodySource).append("\n");
        } else {
            if (getInlineComment() == null) {
                sb.append("\n");
            }
            sb.append(this.body.toSource(depth + 1));
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.initializer.visit(v);
            this.condition.visit(v);
            this.increment.visit(v);
            this.body.visit(v);
        }
    }
}
