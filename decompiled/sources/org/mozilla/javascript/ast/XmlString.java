package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/XmlString.class */
public class XmlString extends XmlFragment {
    private String xml;

    public XmlString() {
    }

    public XmlString(int pos) {
        super(pos);
    }

    public XmlString(int pos, String s) {
        super(pos);
        setXml(s);
    }

    public void setXml(String s) {
        assertNotNull(s);
        this.xml = s;
        setLength(s.length());
    }

    public String getXml() {
        return this.xml;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        return makeIndent(depth) + this.xml;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}
