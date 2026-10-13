package org.bouncycastle.cms;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/PasswordRecipientId.class */
public class PasswordRecipientId extends RecipientId {
    public PasswordRecipientId() {
        super(3);
    }

    public int hashCode() {
        return 3;
    }

    public boolean equals(Object obj) {
        return obj instanceof PasswordRecipientId;
    }

    @Override // org.bouncycastle.cms.RecipientId, org.bouncycastle.util.Selector
    public Object clone() {
        return new PasswordRecipientId();
    }

    @Override // org.bouncycastle.util.Selector
    public boolean match(Object obj) {
        return obj instanceof PasswordRecipientInformation;
    }
}
