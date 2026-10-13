package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/XmlRef.class */
public abstract class XmlRef extends AstNode {
    protected Name namespace;
    protected int atPos;
    protected int colonPos;

    public XmlRef() {
        this.atPos = -1;
        this.colonPos = -1;
    }

    public XmlRef(int pos) {
        super(pos);
        this.atPos = -1;
        this.colonPos = -1;
    }

    public XmlRef(int pos, int len) {
        super(pos, len);
        this.atPos = -1;
        this.colonPos = -1;
    }

    public Name getNamespace() {
        return this.namespace;
    }

    public void setNamespace(Name namespace) {
        this.namespace = namespace;
        if (namespace != null) {
            namespace.setParent(this);
        }
    }

    public boolean isAttributeAccess() {
        return this.atPos >= 0;
    }

    public int getAtPos() {
        return this.atPos;
    }

    public void setAtPos(int atPos) {
        this.atPos = atPos;
    }

    public int getColonPos() {
        return this.colonPos;
    }

    public void setColonPos(int colonPos) {
        this.colonPos = colonPos;
    }
}
