package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ETSIRecipientInfoBuilder.class */
public class ETSIRecipientInfoBuilder {
    private final ETSIKeyWrapper keyWrapper;
    private final byte[] recipientID;

    public ETSIRecipientInfoBuilder(ETSIKeyWrapper eTSIKeyWrapper, byte[] bArr) {
        this.keyWrapper = eTSIKeyWrapper;
        this.recipientID = bArr;
    }

    public RecipientInfo build(byte[] bArr) {
        try {
            return RecipientInfo.certRecipInfo(PKRecipientInfo.builder().setRecipientId(new HashedId8(this.recipientID)).setEncKey(this.keyWrapper.wrap(bArr)).createPKRecipientInfo());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
