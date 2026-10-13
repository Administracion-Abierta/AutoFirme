package org.spongycastle.cms;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/PasswordRecipientId.class */
public class PasswordRecipientId extends RecipientId {
    public PasswordRecipientId() {
        super(3);
    }

    public int hashCode() {
        return 3;
    }

    public boolean equals(Object o) {
        if (!(o instanceof PasswordRecipientId)) {
            return false;
        }
        return true;
    }

    @Override // org.spongycastle.cms.RecipientId, org.spongycastle.util.Selector
    public Object clone() {
        return new PasswordRecipientId();
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(Object obj) {
        if (obj instanceof PasswordRecipientInformation) {
            return true;
        }
        return false;
    }
}
