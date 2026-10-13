package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/GeneratorExpressionLoop.class */
public class GeneratorExpressionLoop extends ForInLoop {
    public GeneratorExpressionLoop() {
    }

    public GeneratorExpressionLoop(int pos) {
        super(pos);
    }

    public GeneratorExpressionLoop(int pos, int len) {
        super(pos, len);
    }

    @Override // org.mozilla.javascript.ast.ForInLoop
    public boolean isForEach() {
        return false;
    }

    @Override // org.mozilla.javascript.ast.ForInLoop
    public void setIsForEach(boolean isForEach) {
        throw new UnsupportedOperationException("this node type does not support for each");
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
