package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/PropertyGet.class */
public class PropertyGet extends InfixExpression {
    public PropertyGet() {
        this.type = 33;
    }

    public PropertyGet(int pos) {
        super(pos);
        this.type = 33;
    }

    public PropertyGet(int pos, int len) {
        super(pos, len);
        this.type = 33;
    }

    public PropertyGet(int pos, int len, AstNode target, Name property) {
        super(pos, len, target, property);
        this.type = 33;
    }

    public PropertyGet(AstNode target, Name property) {
        super(target, property);
        this.type = 33;
    }

    public PropertyGet(AstNode target, Name property, int dotPosition) {
        super(33, target, property, dotPosition);
        this.type = 33;
    }

    public AstNode getTarget() {
        return getLeft();
    }

    public void setTarget(AstNode target) {
        setLeft(target);
    }

    public Name getProperty() {
        return (Name) getRight();
    }

    public void setProperty(Name property) {
        setRight(property);
    }

    @Override // org.mozilla.javascript.ast.InfixExpression, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + getLeft().toSource(0) + "." + getRight().toSource(0);
    }

    @Override // org.mozilla.javascript.ast.InfixExpression, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            getTarget().visit(v);
            getProperty().visit(v);
        }
    }
}
