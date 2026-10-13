package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ArrayComprehensionLoop.class */
public class ArrayComprehensionLoop extends ForInLoop {
    public ArrayComprehensionLoop() {
    }

    public ArrayComprehensionLoop(int pos) {
        super(pos);
    }

    public ArrayComprehensionLoop(int pos, int len) {
        super(pos, len);
    }

    @Override // org.mozilla.javascript.ast.Loop
    public AstNode getBody() {
        return null;
    }

    @Override // org.mozilla.javascript.ast.Loop
    public void setBody(AstNode body) {
        throw new UnsupportedOperationException("this node type has no body");
    }

    @Override // org.mozilla.javascript.ast.ForInLoop, org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + " for " + (isForEach() ? "each " : "") + "(" + this.iterator.toSource(0) + (isForOf() ? " of " : " in ") + this.iteratedObject.toSource(0) + ")";
    }

    @Override // org.mozilla.javascript.ast.ForInLoop, org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.iterator.visit(v);
            this.iteratedObject.visit(v);
        }
    }
}
