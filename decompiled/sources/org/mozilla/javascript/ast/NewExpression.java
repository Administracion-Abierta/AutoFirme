package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/NewExpression.class */
public class NewExpression extends FunctionCall {
    private ObjectLiteral initializer;

    public NewExpression() {
        this.type = 30;
    }

    public NewExpression(int pos) {
        super(pos);
        this.type = 30;
    }

    public NewExpression(int pos, int len) {
        super(pos, len);
        this.type = 30;
    }

    public ObjectLiteral getInitializer() {
        return this.initializer;
    }

    public void setInitializer(ObjectLiteral initializer) {
        this.initializer = initializer;
        if (initializer != null) {
            initializer.setParent(this);
        }
    }

    @Override // org.mozilla.javascript.ast.FunctionCall, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        sb.append("new ");
        sb.append(this.target.toSource(0));
        sb.append("(");
        if (this.arguments != null) {
            printList(this.arguments, sb);
        }
        sb.append(")");
        if (this.initializer != null) {
            sb.append(" ");
            sb.append(this.initializer.toSource(0));
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.FunctionCall, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.target.visit(v);
            for (AstNode arg : getArguments()) {
                arg.visit(v);
            }
            if (this.initializer != null) {
                this.initializer.visit(v);
            }
        }
    }
}
