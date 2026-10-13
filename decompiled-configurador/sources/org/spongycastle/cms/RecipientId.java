package org.spongycastle.cms;

import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/RecipientId.class */
public abstract class RecipientId implements Selector {
    public static final int keyTrans = 0;
    public static final int kek = 1;
    public static final int keyAgree = 2;
    public static final int password = 3;
    private final int type;

    @Override // org.spongycastle.util.Selector
    public abstract Object clone();

    protected RecipientId(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
