package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/VariableInitializer.class */
public class VariableInitializer extends AstNode {
    private AstNode target;
    private AstNode initializer;

    public void setNodeType(int nodeType) {
        if (nodeType != 123 && nodeType != 155 && nodeType != 154) {
            throw new IllegalArgumentException("invalid node type");
        }
        setType(nodeType);
    }

    public VariableInitializer() {
        this.type = 123;
    }

    public VariableInitializer(int pos) {
        super(pos);
        this.type = 123;
    }

    public VariableInitializer(int pos, int len) {
        super(pos, len);
        this.type = 123;
    }

    public boolean isDestructuring() {
        return !(this.target instanceof Name);
    }

    public AstNode getTarget() {
        return this.target;
    }

    public void setTarget(AstNode target) {
        if (target == null) {
            throw new IllegalArgumentException("invalid target arg");
        }
        this.target = target;
        target.setParent(this);
    }

    public AstNode getInitializer() {
        return this.initializer;
    }

    public void setInitializer(AstNode initializer) {
        this.initializer = initializer;
        if (initializer != null) {
            initializer.setParent(this);
        }
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        sb.append(this.target.toSource(0));
        if (this.initializer != null) {
            sb.append(" = ");
            sb.append(this.initializer.toSource(0));
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.target.visit(v);
            if (this.initializer != null) {
                this.initializer.visit(v);
            }
        }
    }
}
