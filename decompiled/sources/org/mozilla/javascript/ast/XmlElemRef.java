package org.mozilla.javascript.ast;

import org.ini4j.Registry;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/XmlElemRef.class */
public class XmlElemRef extends XmlRef {
    private AstNode indexExpr;
    private int lb;
    private int rb;

    public XmlElemRef() {
        this.lb = -1;
        this.rb = -1;
        this.type = 78;
    }

    public XmlElemRef(int pos) {
        super(pos);
        this.lb = -1;
        this.rb = -1;
        this.type = 78;
    }

    public XmlElemRef(int pos, int len) {
        super(pos, len);
        this.lb = -1;
        this.rb = -1;
        this.type = 78;
    }

    public AstNode getExpression() {
        return this.indexExpr;
    }

    public void setExpression(AstNode expr) {
        assertNotNull(expr);
        this.indexExpr = expr;
        expr.setParent(this);
    }

    public int getLb() {
        return this.lb;
    }

    public void setLb(int lb) {
        this.lb = lb;
    }

    public int getRb() {
        return this.rb;
    }

    public void setRb(int rb) {
        this.rb = rb;
    }

    public void setBrackets(int lb, int rb) {
        this.lb = lb;
        this.rb = rb;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        if (isAttributeAccess()) {
            sb.append(Registry.Key.DEFAULT_NAME);
        }
        if (this.namespace != null) {
            sb.append(this.namespace.toSource(0));
            sb.append("::");
        }
        sb.append("[");
        sb.append(this.indexExpr.toSource(0));
        sb.append("]");
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            if (this.namespace != null) {
                this.namespace.visit(v);
            }
            this.indexExpr.visit(v);
        }
    }
}
