package org.apache.fontbox.cff;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/FDSelect.class */
public abstract class FDSelect {
    protected final CFFCIDFont owner;

    public abstract int getFDIndex(int i);

    public FDSelect(CFFCIDFont owner) {
        this.owner = owner;
    }
}
