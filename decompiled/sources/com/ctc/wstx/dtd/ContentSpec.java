package com.ctc.wstx.dtd;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/ContentSpec.class */
public abstract class ContentSpec {
    protected char mArity;

    public abstract StructValidator getSimpleValidator();

    public abstract ModelNode rewrite();

    public ContentSpec(char arity) {
        this.mArity = arity;
    }

    public final char getArity() {
        return this.mArity;
    }

    public final void setArity(char c) {
        this.mArity = c;
    }

    public boolean isLeaf() {
        return false;
    }
}
