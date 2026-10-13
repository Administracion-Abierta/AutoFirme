package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ETSIRecipientID.class */
public class ETSIRecipientID implements Selector<ETSIRecipientInfo> {
    private final HashedId8 id;

    public ETSIRecipientID(byte[] bArr) {
        this(new HashedId8(bArr));
    }

    public ETSIRecipientID(HashedId8 hashedId8) {
        this.id = hashedId8;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ETSIRecipientID eTSIRecipientID = (ETSIRecipientID) obj;
        if (this.id != null) {
            return this.id.equals(eTSIRecipientID.id);
        }
        return eTSIRecipientID.id == null;
    }

    public int hashCode() {
        if (this.id != null) {
            return this.id.hashCode();
        }
        return 0;
    }

    @Override // org.bouncycastle.util.Selector
    public boolean match(ETSIRecipientInfo eTSIRecipientInfo) {
        if (eTSIRecipientInfo.getRecipientInfo().getChoice() == 2) {
            return Arrays.areEqual(PKRecipientInfo.getInstance(eTSIRecipientInfo.getRecipientInfo().getRecipientInfo()).getRecipientId().getHashBytes(), this.id.getHashBytes());
        }
        return false;
    }

    @Override // org.bouncycastle.util.Selector
    public Object clone() {
        return this;
    }
}
