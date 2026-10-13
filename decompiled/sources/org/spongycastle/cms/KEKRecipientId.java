package org.spongycastle.cms;

import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/KEKRecipientId.class */
public class KEKRecipientId extends RecipientId {
    private byte[] keyIdentifier;

    public KEKRecipientId(byte[] keyIdentifier) {
        super(1);
        this.keyIdentifier = keyIdentifier;
    }

    public int hashCode() {
        return Arrays.hashCode(this.keyIdentifier);
    }

    public boolean equals(Object o) {
        if (!(o instanceof KEKRecipientId)) {
            return false;
        }
        KEKRecipientId id = (KEKRecipientId) o;
        return Arrays.areEqual(this.keyIdentifier, id.keyIdentifier);
    }

    public byte[] getKeyIdentifier() {
        return Arrays.clone(this.keyIdentifier);
    }

    @Override // org.spongycastle.cms.RecipientId, org.spongycastle.util.Selector
    public Object clone() {
        return new KEKRecipientId(this.keyIdentifier);
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(Object obj) {
        if (obj instanceof byte[]) {
            return Arrays.areEqual(this.keyIdentifier, (byte[]) obj);
        }
        if (obj instanceof KEKRecipientInformation) {
            return ((KEKRecipientInformation) obj).getRID().equals(this);
        }
        return false;
    }
}
