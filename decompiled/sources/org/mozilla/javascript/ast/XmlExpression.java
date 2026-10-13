package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/XmlExpression.class */
public class XmlExpression extends XmlFragment {
    private AstNode expression;
    private boolean isXmlAttribute;

    public XmlExpression() {
    }

    public XmlExpression(int pos) {
        super(pos);
    }

    public XmlExpression(int pos, int len) {
        super(pos, len);
    }

    public XmlExpression(int pos, AstNode expr) {
        super(pos);
        setExpression(expr);
    }

    public AstNode getExpression() {
        return this.expression;
    }

    public void setExpression(AstNode expression) {
        assertNotNull(expression);
        this.expression = expression;
        expression.setParent(this);
    }

    public boolean isXmlAttribute() {
        return this.isXmlAttribute;
    }

    public void setIsXmlAttribute(boolean isXmlAttribute) {
        this.isXmlAttribute = isXmlAttribute;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + "{" + this.expression.toSource(depth) + "}";
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.expression.visit(v);
        }
    }
}
