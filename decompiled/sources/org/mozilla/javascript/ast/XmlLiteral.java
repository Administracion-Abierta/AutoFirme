package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/XmlLiteral.class */
public class XmlLiteral extends AstNode {
    private List<XmlFragment> fragments;

    public XmlLiteral() {
        this.fragments = new ArrayList();
        this.type = 146;
    }

    public XmlLiteral(int pos) {
        super(pos);
        this.fragments = new ArrayList();
        this.type = 146;
    }

    public XmlLiteral(int pos, int len) {
        super(pos, len);
        this.fragments = new ArrayList();
        this.type = 146;
    }

    public List<XmlFragment> getFragments() {
        return this.fragments;
    }

    public void setFragments(List<XmlFragment> fragments) {
        assertNotNull(fragments);
        this.fragments.clear();
        for (XmlFragment fragment : fragments) {
            addFragment(fragment);
        }
    }

    public void addFragment(XmlFragment fragment) {
        assertNotNull(fragment);
        this.fragments.add(fragment);
        fragment.setParent(this);
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder(250);
        for (XmlFragment frag : this.fragments) {
            sb.append(frag.toSource(0));
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            for (XmlFragment frag : this.fragments) {
                frag.visit(v);
            }
        }
    }
}
