package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/ArrayLiteral.class */
public class ArrayLiteral extends AstNode implements DestructuringForm {
    private static final List<AstNode> NO_ELEMS = Collections.unmodifiableList(new ArrayList());
    private List<AstNode> elements;
    private int destructuringLength;
    private int skipCount;
    private boolean isDestructuring;

    public ArrayLiteral() {
        this.type = 66;
    }

    public ArrayLiteral(int pos) {
        super(pos);
        this.type = 66;
    }

    public ArrayLiteral(int pos, int len) {
        super(pos, len);
        this.type = 66;
    }

    public List<AstNode> getElements() {
        return this.elements != null ? this.elements : NO_ELEMS;
    }

    public void setElements(List<AstNode> elements) {
        if (elements == null) {
            this.elements = null;
            return;
        }
        if (this.elements != null) {
            this.elements.clear();
        }
        for (AstNode e : elements) {
            addElement(e);
        }
    }

    public void addElement(AstNode element) {
        assertNotNull(element);
        if (this.elements == null) {
            this.elements = new ArrayList();
        }
        this.elements.add(element);
        element.setParent(this);
    }

    public int getSize() {
        if (this.elements == null) {
            return 0;
        }
        return this.elements.size();
    }

    public AstNode getElement(int index) {
        if (this.elements == null) {
            throw new IndexOutOfBoundsException("no elements");
        }
        return this.elements.get(index);
    }

    public int getDestructuringLength() {
        return this.destructuringLength;
    }

    public void setDestructuringLength(int destructuringLength) {
        this.destructuringLength = destructuringLength;
    }

    public int getSkipCount() {
        return this.skipCount;
    }

    public void setSkipCount(int count) {
        this.skipCount = count;
    }

    @Override // org.mozilla.javascript.ast.DestructuringForm
    public void setIsDestructuring(boolean destructuring) {
        this.isDestructuring = destructuring;
    }

    @Override // org.mozilla.javascript.ast.DestructuringForm
    public boolean isDestructuring() {
        return this.isDestructuring;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        sb.append("[");
        if (this.elements != null) {
            printList(this.elements, sb);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            for (AstNode e : getElements()) {
                e.visit(v);
            }
        }
    }
}
